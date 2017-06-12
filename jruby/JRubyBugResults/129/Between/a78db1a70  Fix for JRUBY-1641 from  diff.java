diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index cce6f5fb11..0aea5d951c 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,2485 +1,2508 @@
 /*
  **** BEGIN LICENSE BLOCK *****
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
 
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.lang.ref.WeakReference;
 import java.util.ArrayList;
 import java.util.Hashtable;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.Stack;
 import java.util.WeakHashMap;
 
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.RubiniusRunner;
 import org.jruby.ast.executable.Script;
 import org.jruby.ast.executable.YARVCompiledRunner;
 import org.jruby.common.RubyWarnings;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.compiler.yarv.StandardYARVCompiler;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.ext.LateLoadingLibrary;
 import org.jruby.ext.posix.JRubyPOSIXHandler;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.ext.posix.POSIXFactory;
 import org.jruby.ext.socket.RubySocket;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.libraries.RbConfigLibrary;
 import org.jruby.parser.Parser;
 import org.jruby.parser.ParserConfiguration;
 import org.jruby.runtime.Binding;
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
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.ByteList;
 import org.jruby.util.IOHandler;
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.KCode;
 import org.jruby.util.MethodCache;
 import org.jruby.util.NormalizedFile;
+import org.jruby.util.SafePropertyAccessor;
 
 import org.jruby.util.JavaNameMangler;
 
 /**
  * The jruby runtime.
  */
 public final class Ruby {
     private static String[] BUILTIN_LIBRARIES = {"fcntl", "yaml", "yaml/syck", "jsignal" };
 
     private CacheMap cacheMap = new CacheMap();
     private MethodCache methodCache = new MethodCache();
     private ThreadService threadService;
     private Hashtable<Object, Object> runtimeInformation;
     
     private POSIX posix;
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     public final RubyFixnum[] fixnumCache = new RubyFixnum[256];
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
     private Hashtable<Integer, WeakReference<IOHandler>> ioHandlers = new Hashtable<Integer, WeakReference<IOHandler>>();
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private ArrayList<EventHook> eventHooks = new ArrayList<EventHook>();
     private boolean hasEventHooks;  
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private final boolean objectSpaceEnabled;
     
     private static ThreadLocal<Ruby> currentRuntime = new ThreadLocal<Ruby>();
-    public static final boolean RUNTIME_THREADLOCAL = Boolean.getBoolean("jruby.runtime.threadlocal");
+    public static final boolean RUNTIME_THREADLOCAL
+            = SafePropertyAccessor.getBoolean("jruby.runtime.threadlocal");
 
     private long globalState = 1;
 
     /** safe-level:
             0 - strings from streams/environment/ARGV are tainted (default)
             1 - no dangerous operation by tainted value
             2 - process/file operations prohibited
             3 - all generated objects are tainted
             4 - no global (non-tainted) variable modification/no direct output
     */
     private int safeLevel = 0;
 
     // Default classes/objects
     private IRubyObject undef;
 
     private RubyClass objectClass;
     private RubyClass moduleClass;
     private RubyClass classClass;
 
     private RubyModule kernelModule;
     
     private RubyClass nilClass;
     private RubyClass trueClass;
     private RubyClass falseClass;
     
     private RubyModule comparableModule;
     
     private RubyClass numericClass;
     private RubyClass floatClass;
     private RubyClass integerClass;
     private RubyClass fixnumClass;
 
     private RubyModule enumerableModule;    
     private RubyClass arrayClass;
     private RubyClass hashClass;
     private RubyClass rangeClass;
     
     private RubyClass stringClass;
     private RubyClass symbolClass;
     
     private RubyNil nilObject;
     private RubyBoolean trueObject;
     private RubyBoolean falseObject;
     
     private RubyClass procClass;
     private RubyClass bindingClass;
     private RubyClass methodClass;
     private RubyClass unboundMethodClass;
 
     private RubyClass matchDataClass;
     private RubyClass regexpClass;
 
     private RubyClass timeClass;
 
     private RubyModule mathModule;
 
     private RubyModule marshalModule;
 
     private RubyClass bignumClass;
 
     private RubyClass dirClass;
 
     private RubyClass fileClass;
     private RubyClass fileStatClass;
     private RubyModule fileTestModule;
 
     private RubyClass ioClass;
 
     private RubyClass threadClass;
     private RubyClass threadGroupClass;
 
     private RubyClass continuationClass;
 
     private RubyClass structClass;
     private IRubyObject tmsStruct;
 
     private RubyModule gcModule;
     private RubyModule objectSpaceModule;
 
     private RubyModule processModule;
     private RubyClass procStatusClass;
     private RubyModule procUIDModule;
     private RubyModule procGIDModule;
 
     private RubyModule precisionModule;
 
     private RubyClass exceptionClass;
     private RubyClass standardError;
 
     private RubyClass systemCallError;
     private RubyModule errnoModule;
     private IRubyObject topSelf;
     
     // record separator var, to speed up io ops that use it
     private GlobalVariable recordSeparatorVar;
 
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
     private JRubyClassLoader jrubyClassLoader;
 
     private static boolean securityRestricted = false;
 
+    static {
+        if (SafePropertyAccessor.isSecurityProtected("jruby.reflection")) {
+            // can't read non-standard properties
+            securityRestricted = true;
+        } else {
+            SecurityManager sm = System.getSecurityManager();
+            if (sm != null) {
+                try {
+                    sm.checkCreateClassLoader();
+                } catch (SecurityException se) {
+                    // can't create custom classloaders
+                    securityRestricted = true;
+                }
+            }
+        }
+    }
+
     private Parser parser = new Parser(this);
 
     private LoadService loadService;
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private Stack<RubyProc> atExitBlocks = new Stack<RubyProc>();
 
     private Profile profile;
 
     private String jrubyHome;
 
     private KCode kcode = KCode.NONE;
 
     public int symbolLastId = 128;
     public int moduleLastId = 0;
 
     private Object respondToMethod;
 
     /**
      * A list of "external" finalizers (the ones, registered via ObjectSpace),
      * weakly referenced, to be executed on tearDown.
      */
     private Map<Finalizable, Object> finalizers;
     
     /**
      * A list of JRuby-internal finalizers,  weakly referenced,
      * to be executed on tearDown.
      */
     private Map<Finalizable, Object> internalFinalizers;
 
     // mutex that controls modifications of user-defined finalizers
     private final Object finalizersMutex = new Object();
 
     // mutex that controls modifications of internal finalizers
     private final Object internalFinalizersMutex = new Object();
     
     private String[] argv;
 
     /**
      * Create and initialize a new JRuby Runtime.
      */
     private Ruby(RubyInstanceConfig config) {
         this.config             = config;
         this.threadService      = new ThreadService(this);
         if(config.isSamplingEnabled()) {
             org.jruby.util.SimpleSampler.registerThreadContext(threadService.getCurrentContext());
         }
 
         this.in                 = config.getInput();
         this.out                = config.getOutput();
         this.err                = config.getError();
         this.objectSpaceEnabled = config.isObjectSpaceEnabled();
         this.profile            = config.getProfile();
         this.currentDirectory   = config.getCurrentDirectory();
         this.jrubyClassLoader   = config.getJRubyClassLoader();
         this.argv               = config.getArgv();
     }
 
     /**
      * @deprecated use #newInstance()
      */
     public static Ruby getDefaultInstance() {
         return newInstance();
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static Ruby newInstance() {
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
         if (RUNTIME_THREADLOCAL) {
             setCurrentInstance(ruby);
         }
         return ruby;
     }
     
     public static Ruby getCurrentInstance() {
         return currentRuntime.get();
     }
     
     public static void setCurrentInstance(Ruby runtime) {
         currentRuntime.set(runtime);
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
 
     public IRubyObject evalFile(InputStream in, String name) {
         return eval(parseFile(in, name, getCurrentContext().getCurrentScope()));
     }
     
     /**
      * Evaluates a script and returns a RubyObject.
      */
     public IRubyObject evalScriptlet(String script) {
         return eval(parseEval(script, "<script>", getCurrentContext().getCurrentScope(), 0));
     }
 
     public IRubyObject eval(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
             return ASTInterpreter.eval(this, tc, node, tc.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             throw newLocalJumpError("return", (IRubyObject)rj.getValue(), "unexpected return");
         } catch (JumpException.BreakJump bj) {
             throw newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
             throw newLocalJumpError("redo", (IRubyObject)rj.getValue(), "unexpected redo");
         }
     }
     
     /**
      * This differs from the other methods in that it accepts a string-based script and
      * parses and runs it as though it were loaded at a command-line. This is the preferred
      * way to start up a new script when calling directly into the Ruby object (which is
      * generally *dis*couraged.
      * 
      * @param script The contents of the script to run as a normal, root script
      * @return The last value of the script
      */
     public IRubyObject executeScript(String script, String filename) {
         byte[] bytes;
         
         try {
             bytes = script.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = script.getBytes();
         }
 
         Node node = parseInline(new ByteArrayInputStream(bytes), filename, null);
         
         getCurrentContext().getCurrentFrame().setPosition(node.getPosition());
         return runNormally(node, false);
     }
     
     public void runFromMain(InputStream in, String filename) {
         if(config.isYARVEnabled()) {
             new YARVCompiledRunner(this, in, filename).run();
         } else if(config.isRubiniusEnabled()) {
             new RubiniusRunner(this, in, filename).run();
         } else {
             Node scriptNode;
             if (config.isInlineScript()) {
                 scriptNode = parseInline(in, filename, getCurrentContext().getCurrentScope());
             } else {
                 scriptNode = parseFile(in, filename, getCurrentContext().getCurrentScope());
             }
             
             getCurrentContext().getCurrentFrame().setPosition(scriptNode.getPosition());
 
             if (config.isAssumePrinting() || config.isAssumeLoop()) {
                 runWithGetsLoop(scriptNode, config.isAssumePrinting(), config.isProcessLineEnds(),
                         config.isSplit(), config.isYARVCompileEnabled());
             } else {
                 runNormally(scriptNode, config.isYARVCompileEnabled());
             }
         }
     }
     
     public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split, boolean yarvCompile) {
         ThreadContext context = getCurrentContext();
         
         Script script = null;
         YARVCompiledRunner runner = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         if (compile || !yarvCompile) {
             script = tryCompile(scriptNode);
             if (compile && script == null) {
                 // terminate; tryCompile will have printed out an error and we're done
                 return getNil();
             }
         } else if (yarvCompile) {
             runner = tryCompileYarv(scriptNode);
         }
         
         if (processLineEnds) {
             getGlobalVariables().set("$\\", getGlobalVariables().get("$/"));
         }
         
         while (RubyKernel.gets(getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
             loop: while (true) { // Used for the 'redo' command
                 try {
                     if (processLineEnds) {
                         getGlobalVariables().get("$_").callMethod(context, "chop!");
                     }
                     
                     if (split) {
                         getGlobalVariables().set("$F", getGlobalVariables().get("$_").callMethod(context, "split"));
                     }
                     
                     if (script != null) {
                         runScript(script);
                     } else if (runner != null) {
                         runYarv(runner);
                     } else {
                         runInterpreter(scriptNode);
                     }
                     
                     if (printing) RubyKernel.print(getKernel(), new IRubyObject[] {getGlobalVariables().get("$_")});
                     break loop;
                 } catch (JumpException.RedoJump rj) {
                     // do nothing, this iteration restarts
                 } catch (JumpException.NextJump nj) {
                     // recheck condition
                     break loop;
                 } catch (JumpException.BreakJump bj) {
                     // end loop
                     return (IRubyObject) bj.getValue();
                 }
             }
         }
         
         return getNil();
     }
     
     public IRubyObject runNormally(Node scriptNode, boolean yarvCompile) {
         Script script = null;
         YARVCompiledRunner runner = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         boolean forceCompile = getInstanceConfig().getCompileMode().shouldPrecompileAll();
         if (compile) {
             script = tryCompile(scriptNode);
             if (forceCompile && script == null) {
                 System.err.println("Error, could not compile; pass -J-Djruby.jit.logging.verbose=true for more details");
                 return getNil();
             }
         } else if (yarvCompile) {
             runner = tryCompileYarv(scriptNode);
         }
         
         if (script != null) {
             return runScript(script);
         } else if (runner != null) {
             return runYarv(runner);
         } else {
             return runInterpreter(scriptNode);
         }
     }
     
     private Script tryCompile(Node node) {
         return tryCompile(node, new JRubyClassLoader(getJRubyClassLoader()));
     }
     
     private Script tryCompile(Node node, JRubyClassLoader classLoader) {
         Script script = null;
         try {
             String filename = node.getPosition().getFile();
             String classname = JavaNameMangler.mangledFilenameForStartupClasspath(filename);
 
             ASTInspector inspector = new ASTInspector();
             inspector.inspect(node);
 
             StandardASMCompiler asmCompiler = new StandardASMCompiler(classname, filename);
             ASTCompiler compiler = new ASTCompiler();
             compiler.compileRoot(node, asmCompiler, inspector);
             script = (Script)asmCompiler.loadClass(classLoader).newInstance();
 
             if (config.isJitLogging()) {
                 System.err.println("compiled: " + node.getPosition().getFile());
             }
         } catch (NotCompilableException nce) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + nce.getMessage());
                 nce.printStackTrace();
             }
         } catch (ClassNotFoundException e) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             }
         } catch (InstantiationException e) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             }
         } catch (IllegalAccessException e) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             }
         } catch (Throwable t) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("could not compile: " + node.getPosition().getFile() + " because of: \"" + t.getMessage() + "\"");
                 t.printStackTrace();
             }
         }
         
         return script;
     }
     
     private YARVCompiledRunner tryCompileYarv(Node node) {
         try {
             StandardYARVCompiler compiler = new StandardYARVCompiler(this);
             ASTCompiler.getYARVCompiler().compile(node, compiler);
             org.jruby.lexer.yacc.ISourcePosition p = node.getPosition();
             if(p == null && node instanceof org.jruby.ast.RootNode) {
                 p = ((org.jruby.ast.RootNode)node).getBodyNode().getPosition();
             }
             return new YARVCompiledRunner(this,compiler.getInstructionSequence("<main>",p.getFile(),"toplevel"));
         } catch (NotCompilableException nce) {
             System.err.println("Error -- Not compileable: " + nce.getMessage());
             return null;
         } catch (JumpException.ReturnJump rj) {
             return null;
         }
     }
     
     private IRubyObject runScript(Script script) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(getCurrentContext(), context.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     private IRubyObject runYarv(YARVCompiledRunner runner) {
         try {
             return runner.run();
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     private IRubyObject runInterpreter(Node scriptNode) {
         ThreadContext context = getCurrentContext();
         
         try {
             return ASTInterpreter.eval(this, context, scriptNode, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     Object getRespondToMethod() {
         return respondToMethod;
     }
 
     void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
     }
     
     public IRubyObject getUndef() {
         return undef;
     }
 
     public RubyClass getObject() {
         return objectClass;
     }
 
     public RubyClass getModule() {
         return moduleClass;
     }
 
     public RubyClass getClassClass() {
         return classClass;
     }
     
     public RubyModule getKernel() {
         return kernelModule;
     }
     void setKernel(RubyModule kernelModule) {
         this.kernelModule = kernelModule;
     }    
 
     public RubyModule getComparable() {
         return comparableModule;
     }
     void setComparable(RubyModule comparableModule) {
         this.comparableModule = comparableModule;
     }    
 
     public RubyClass getNumeric() {
         return numericClass;
     }
     void setNumeric(RubyClass numericClass) {
         this.numericClass = numericClass;
     }    
 
     public RubyClass getFloat() {
         return floatClass;
     }
     void setFloat(RubyClass floatClass) {
         this.floatClass = floatClass;
     }
     
     public RubyClass getInteger() {
         return integerClass;
     }
     void setInteger(RubyClass integerClass) {
         this.integerClass = integerClass;
     }    
     
     public RubyClass getFixnum() {
         return fixnumClass;
     }
     void setFixnum(RubyClass fixnumClass) {
         this.fixnumClass = fixnumClass;
     }    
 
     public RubyModule getEnumerable() {
         return enumerableModule;
     }
     void setEnumerable(RubyModule enumerableModule) {
         this.enumerableModule = enumerableModule;
     }      
 
     public RubyClass getString() {
         return stringClass;
     }    
     void setString(RubyClass stringClass) {
         this.stringClass = stringClass;
     }    
 
     public RubyClass getSymbol() {
         return symbolClass;
     }
     void setSymbol(RubyClass symbolClass) {
         this.symbolClass = symbolClass;
     }   
 
     public RubyClass getArray() {
         return arrayClass;
     }    
     void setArray(RubyClass arrayClass) {
         this.arrayClass = arrayClass;
     }
 
     public RubyClass getHash() {
         return hashClass;
     }
     void setHash(RubyClass hashClass) {
         this.hashClass = hashClass;
     }
 
     public RubyClass getRange() {
         return rangeClass;
     }
     void setRange(RubyClass rangeClass) {
         this.rangeClass = rangeClass;
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
     void setNilClass(RubyClass nilClass) {
         this.nilClass = nilClass;
     }
 
     public RubyClass getTrueClass() {
         return trueClass;
     }
     void setTrueClass(RubyClass trueClass) {
         this.trueClass = trueClass;
     }
 
     public RubyClass getFalseClass() {
         return falseClass;
     }
     void setFalseClass(RubyClass falseClass) {
         this.falseClass = falseClass;
     }
 
     public RubyClass getProc() {
         return procClass;
     }
     void setProc(RubyClass procClass) {
         this.procClass = procClass;
     }
 
     public RubyClass getBinding() {
         return bindingClass;
     }
     void setBinding(RubyClass bindingClass) {
         this.bindingClass = bindingClass;
     }
 
     public RubyClass getMethod() {
         return methodClass;
     }
     void setMethod(RubyClass methodClass) {
         this.methodClass = methodClass;
     }    
 
     public RubyClass getUnboundMethod() {
         return unboundMethodClass;
     }
     void setUnboundMethod(RubyClass unboundMethodClass) {
         this.unboundMethodClass = unboundMethodClass;
     }    
 
     public RubyClass getMatchData() {
         return matchDataClass;
     }
     void setMatchData(RubyClass matchDataClass) {
         this.matchDataClass = matchDataClass;
     }    
 
     public RubyClass getRegexp() {
         return regexpClass;
     }
     void setRegexp(RubyClass regexpClass) {
         this.regexpClass = regexpClass;
     }    
 
     public RubyClass getTime() {
         return timeClass;
     }
     void setTime(RubyClass timeClass) {
         this.timeClass = timeClass;
     }    
 
     public RubyModule getMath() {
         return mathModule;
     }
     void setMath(RubyModule mathModule) {
         this.mathModule = mathModule;
     }    
 
     public RubyModule getMarshal() {
         return marshalModule;
     }
     void setMarshal(RubyModule marshalModule) {
         this.marshalModule = marshalModule;
     }    
 
     public RubyClass getBignum() {
         return bignumClass;
     }
     void setBignum(RubyClass bignumClass) {
         this.bignumClass = bignumClass;
     }    
 
     public RubyClass getDir() {
         return dirClass;
     }
     void setDir(RubyClass dirClass) {
         this.dirClass = dirClass;
     }    
 
     public RubyClass getFile() {
         return fileClass;
     }
     void setFile(RubyClass fileClass) {
         this.fileClass = fileClass;
     }    
 
     public RubyClass getFileStat() {
         return fileStatClass;
     }
     void setFileStat(RubyClass fileStatClass) {
         this.fileStatClass = fileStatClass;
     }    
 
     public RubyModule getFileTest() {
         return fileTestModule;
     }
     void setFileTest(RubyModule fileTestModule) {
         this.fileTestModule = fileTestModule;
     }    
 
     public RubyClass getIO() {
         return ioClass;
     }
     void setIO(RubyClass ioClass) {
         this.ioClass = ioClass;
     }    
 
     public RubyClass getThread() {
         return threadClass;
     }
     void setThread(RubyClass threadClass) {
         this.threadClass = threadClass;
     }    
 
     public RubyClass getThreadGroup() {
         return threadGroupClass;
     }
     void setThreadGroup(RubyClass threadGroupClass) {
         this.threadGroupClass = threadGroupClass;
     }    
 
     public RubyClass getContinuation() {
         return continuationClass;
     }
     void setContinuation(RubyClass continuationClass) {
         this.continuationClass = continuationClass;
     }    
 
     public RubyClass getStructClass() {
         return structClass;
     }
     void setStructClass(RubyClass structClass) {
         this.structClass = structClass;
     }    
 
     public IRubyObject getTmsStruct() {
         return tmsStruct;
     }
     void setTmsStruct(RubyClass tmsStruct) {
         this.tmsStruct = tmsStruct;
     }    
 
     public RubyModule getGC() {
         return gcModule;
     }
     void setGC(RubyModule gcModule) {
         this.gcModule = gcModule;
     }    
 
     public RubyModule getObjectSpaceModule() {
         return objectSpaceModule;
     }
     void setObjectSpaceModule(RubyModule objectSpaceModule) {
         this.objectSpaceModule = objectSpaceModule;
     }    
 
     public RubyModule getProcess() {
         return processModule;
     }
     void setProcess(RubyModule processModule) {
         this.processModule = processModule;
     }    
 
     public RubyClass getProcStatus() {
         return procStatusClass; 
     }
     void setProcStatus(RubyClass procStatusClass) {
         this.procStatusClass = procStatusClass;
     }
     
     public RubyModule getProcUID() {
         return procUIDModule;
     }
     void setProcUID(RubyModule procUIDModule) {
         this.procUIDModule = procUIDModule;
     }
     
     public RubyModule getProcGID() {
         return procGIDModule;
     }
     void setProcGID(RubyModule procGIDModule) {
         this.procGIDModule = procGIDModule;
     }
 
     public RubyModule getPrecision() {
         return precisionModule;
     }
     void setPrecision(RubyModule precisionModule) {
         this.precisionModule = precisionModule;
     }    
 
     public RubyClass getException() {
         return exceptionClass;
     }
     void setException(RubyClass exceptionClass) {
         this.exceptionClass = exceptionClass;
     }
     
     public RubyClass getStandardError() {
         return standardError;
     }
 
     public RubyModule getModule(String name) {
         return (RubyModule) objectClass.getConstantAt(name);
     }
 
     /**
      * 
      * @param internedName the name of the module; <em>must</em> be an interned String!
      * @return
      */
     public RubyModule fastGetModule(String internedName) {
         return (RubyModule) objectClass.fastGetConstantAt(internedName);
     }
 
     /** Returns a class from the instance pool.
      *
      * @param name The name of the class.
      * @return The class.
      */
     public RubyClass getClass(String name) {
         return objectClass.getClass(name);
     }
 
     /**
      * 
      * @param internedName the name of the class; <em>must</em> be an interned String!
      * @return
      */
     public RubyClass fastGetClass(String internedName) {
         return objectClass.fastGetClass(internedName);
     }
 
     /** rb_define_class
      *
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator) {
         IRubyObject classObj = objectClass.getConstantAt(name);
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw newTypeError(name + " is not a class");
             RubyClass klazz = (RubyClass)classObj;
             if (klazz.getSuperClass().getRealClass() != superClass) throw newNameError(name + " is already defined", name);
             return klazz;
         }
 
         if (superClass == null) {
             warnings.warn("no super class for `" + name + "', Object assumed");
             superClass = objectClass;
         }
 
         return RubyClass.newClass(this, superClass, name, allocator, objectClass, false);
     }
 
     /** rb_define_class_under
     *
     */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent) {
         IRubyObject classObj = parent.getConstantAt(name);
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw newTypeError(name + " is not a class");
             RubyClass klazz = (RubyClass)classObj;
             if (klazz.getSuperClass().getRealClass() != superClass) {
                 throw newNameError(name + " is already defined", name);
             }
             // If we define a class in Ruby, but later want to allow it to be defined in Java,
             // the allocator needs to be updated
             if (klazz.getAllocator() != allocator) {
                 klazz.setAllocator(allocator);
             }
             return klazz;
         }
 
         if (superClass == null) {
             warnings.warn("no super class for `" + parent.getName() + "::" + name + "', Object assumed");
             superClass = objectClass;
         }
 
         return RubyClass.newClass(this, superClass, name, allocator, parent, true);
     }
 
     /** rb_define_module
      *
      */
     public RubyModule defineModule(String name) {
         IRubyObject moduleObj = objectClass.getConstantAt(name);
 
         if (moduleObj != null ) {
             if (moduleObj.isModule()) return (RubyModule)moduleObj;
             throw newTypeError(moduleObj.getMetaClass().getName() + " is not a module");
         }
 
         return RubyModule.newModule(this, name, objectClass, false);
     }
 
     /** rb_define_module_under
     *
     */
     public RubyModule defineModuleUnder(String name, RubyModule parent) {
         IRubyObject moduleObj = parent.getConstantAt(name);
 
         if (moduleObj != null ) {
             if (moduleObj.isModule()) return (RubyModule)moduleObj;
             throw newTypeError(parent.getName() + "::" + moduleObj.getMetaClass().getName() + " is not a module");
         }
 
         return RubyModule.newModule(this, name, parent, true);
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
         } else if (!module.isModule()) {
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
     public Map<Object, Object> getRuntimeInformation() {
         return runtimeInformation == null ? runtimeInformation = new Hashtable<Object, Object>() : runtimeInformation;
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
 
         posix = POSIXFactory.getPOSIX(new JRubyPOSIXHandler(this), RubyInstanceConfig.nativeEnabled);
         
         defineGlobalVERBOSE();
         defineGlobalDEBUG();
 
         javaSupport = new JavaSupport(this);
 
         tc.preInitCoreClasses();
 
         initCoreClasses();
         
         // core classes are initialized, ensure top scope has Object as cref
         tc.getCurrentScope().getStaticScope().setModule(objectClass);
 
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
                     runtime.getLoadService().smartLoad("builtin/javasupport");
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
 
         registerBuiltin("jruby.rb", new LateLoadingLibrary("jruby", "org.jruby.libraries.JRubyLibrary", getJRubyClassLoader()));
         registerBuiltin("jruby/ext.rb", new LateLoadingLibrary("jruby/ext", "org.jruby.RubyJRuby$ExtLibrary", getJRubyClassLoader()));
         registerBuiltin("iconv.rb", new LateLoadingLibrary("iconv", "org.jruby.libraries.IConvLibrary", getJRubyClassLoader()));
         registerBuiltin("nkf.rb", new LateLoadingLibrary("nkf", "org.jruby.libraries.NKFLibrary", getJRubyClassLoader()));
         registerBuiltin("stringio.rb", new LateLoadingLibrary("stringio", "org.jruby.libraries.StringIOLibrary", getJRubyClassLoader()));
         registerBuiltin("strscan.rb", new LateLoadingLibrary("strscan", "org.jruby.libraries.StringScannerLibrary", getJRubyClassLoader()));
         registerBuiltin("zlib.rb", new LateLoadingLibrary("zlib", "org.jruby.libraries.ZlibLibrary", getJRubyClassLoader()));
         registerBuiltin("yaml_internal.rb", new LateLoadingLibrary("yaml_internal", "org.jruby.libraries.YamlLibrary", getJRubyClassLoader()));
         registerBuiltin("enumerator.rb", new LateLoadingLibrary("enumerator", "org.jruby.libraries.EnumeratorLibrary", getJRubyClassLoader()));
         registerBuiltin("generator_internal.rb", new LateLoadingLibrary("generator_internal", "org.jruby.ext.Generator$Service", getJRubyClassLoader()));
         registerBuiltin("readline.rb", new LateLoadingLibrary("readline", "org.jruby.ext.Readline$Service", getJRubyClassLoader()));
         registerBuiltin("thread.so", new LateLoadingLibrary("thread", "org.jruby.libraries.ThreadLibrary", getJRubyClassLoader()));
         registerBuiltin("openssl.so", new Library() {
                 public void load(Ruby runtime) throws IOException {
                     runtime.getLoadService().require("jruby/openssl/stub");
                 }
             });
         registerBuiltin("digest.so", new LateLoadingLibrary("digest", "org.jruby.libraries.DigestLibrary", getJRubyClassLoader()));
         registerBuiltin("digest.rb", new LateLoadingLibrary("digest", "org.jruby.libraries.DigestLibrary", getJRubyClassLoader()));
         registerBuiltin("digest/md5.rb", new LateLoadingLibrary("digest/md5", "org.jruby.libraries.DigestLibrary$MD5", getJRubyClassLoader()));
         registerBuiltin("digest/rmd160.rb", new LateLoadingLibrary("digest/rmd160", "org.jruby.libraries.DigestLibrary$RMD160", getJRubyClassLoader()));
         registerBuiltin("digest/sha1.rb", new LateLoadingLibrary("digest/sha1", "org.jruby.libraries.DigestLibrary$SHA1", getJRubyClassLoader()));
         registerBuiltin("digest/sha2.rb", new LateLoadingLibrary("digest/sha2", "org.jruby.libraries.DigestLibrary$SHA2", getJRubyClassLoader()));
         registerBuiltin("bigdecimal.rb", new LateLoadingLibrary("bigdecimal", "org.jruby.libraries.BigDecimalLibrary", getJRubyClassLoader()));
         registerBuiltin("io/wait.so", new LateLoadingLibrary("io/wait", "org.jruby.libraries.IOWaitLibrary", getJRubyClassLoader()));
         registerBuiltin("etc.so", NO_OP_LIBRARY);
         registerBuiltin("weakref.rb", new LateLoadingLibrary("weakref", "org.jruby.ext.WeakRef$WeakRefLibrary", getJRubyClassLoader()));
         
         if (config.getCompatVersion() == CompatVersion.RUBY1_9) {
             registerBuiltin("fiber.so", new LateLoadingLibrary("fiber", "org.jruby.libraries.FiberLibrary", getJRubyClassLoader()));
         }
     }
 
     private void registerBuiltin(String nm, Library lib) {
         if(profile.allowBuiltin(nm)) {
             loadService.registerBuiltin(nm,lib);
         }
     }
 
     private void initCoreClasses() {
         undef = new RubyUndef();
 
         objectClass = RubyClass.createBootstrapClass(this, "Object", null, RubyObject.OBJECT_ALLOCATOR);
         moduleClass = RubyClass.createBootstrapClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR);
         classClass = RubyClass.createBootstrapClass(this, "Class", moduleClass, RubyClass.CLASS_ALLOCATOR);
 
         objectClass.setMetaClass(classClass);
         moduleClass.setMetaClass(classClass);
         classClass.setMetaClass(classClass);
 
         RubyClass metaClass; 
         metaClass = objectClass.makeMetaClass(classClass);
         metaClass = moduleClass.makeMetaClass(metaClass);
         metaClass = classClass.makeMetaClass(metaClass);
         
         RubyObject.createObjectClass(this, objectClass);
         RubyModule.createModuleClass(this, moduleClass);
         RubyClass.createClassClass(this, classClass);
 
         RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         // Pre-create the core classes we know we will get referenced by starting up the runtime.
         RubyNil.createNilClass(this);
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
         
         nilObject = new RubyNil(this);
         falseObject = new RubyBoolean(this, false);
         trueObject = new RubyBoolean(this, true);
 
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         RubyString.createStringClass(this);
         RubySymbol.createSymbolClass(this);
         
         if (profile.allowClass("ThreadGroup")) RubyThreadGroup.createThreadGroupClass(this);
         if (profile.allowClass("Thread")) RubyThread.createThreadClass(this);
         if (profile.allowClass("Exception")) RubyException.createExceptionClass(this);
         if (profile.allowModule("Precision")) RubyPrecision.createPrecisionModule(this);
         if (profile.allowClass("Numeric")) RubyNumeric.createNumericClass(this);
         if (profile.allowClass("Integer")) RubyInteger.createIntegerClass(this);
         if (profile.allowClass("Fixnum")) RubyFixnum.createFixnumClass(this);
         if (profile.allowClass("Hash")) RubyHash.createHashClass(this);
         if (profile.allowClass("Array")) RubyArray.createArrayClass(this);
         
         // define ARGV and $* for this runtime
         RubyArray argvArray = newArray();
         for (int i = 0; i < argv.length; i++) {
             argvArray.add(newString(argv[i]));
         }
         defineGlobalConstant("ARGV", argvArray);
         getGlobalVariables().defineReadonly("$*", new ValueAccessor(argvArray));
         
         if (profile.allowClass("Float")) RubyFloat.createFloatClass(this);
         if (profile.allowClass("Bignum")) RubyBignum.createBignumClass(this);
 
         ioClass = RubyIO.createIOClass(this);        
         
         if (profile.allowClass("Struct")) RubyStruct.createStructClass(this);
 
         if (profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass,
                     new IRubyObject[] { newString("Tms"), newSymbol("utime"), newSymbol("stime"),
                         newSymbol("cutime"), newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
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
         if (profile.allowClass("File::Stat")) RubyFileStat.createFileStatClass(this);
         if (profile.allowModule("Process")) RubyProcess.createProcessModule(this);
         if (profile.allowClass("Time")) RubyTime.createTimeClass(this);
         if (profile.allowClass("UnboundMethod")) RubyUnboundMethod.defineUnboundMethodClass(this);
 
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
             systemCallError = RubySystemCallError.createSystemCallErrorClass(this, standardError);
         }
         if (profile.allowModule("Errno")) errnoModule = defineModule("Errno");
 
         initErrnoErrors();
 
         if (profile.allowClass("Data")) defineClass("Data", objectClass, objectClass.getAllocator());
-        if (profile.allowModule("Signal")) RubySignal.createSignal(this);
+        if (!isSecurityRestricted()) {
+            // Signal uses sun.misc.* classes, this is not allowed
+            // in the security-sensitive environments
+            if (profile.allowModule("Signal")) RubySignal.createSignal(this);
+        }
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
         createSysErr(IErrno.ECONNABORTED, "ECONNABORTED");
         createSysErr(IErrno.EPROTO, "EPROTO");
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
         // FIXME: Get rid of laziness and handle restricted access elsewhere
         if (!Ruby.isSecurityRestricted() && jrubyClassLoader == null) {
             jrubyClassLoader = new JRubyClassLoader(config.getLoader());
         }
         
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
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parser.parse(file, in, scope, new ParserConfiguration(0, false, false, true));
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         return parser.parse(file, in, scope, new ParserConfiguration(0, false, true));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
         
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(lineNumber, false));
     }
 
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
 
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(lineNumber, extraPositionInformation, false));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         return parser.parse(file, content, scope, new ParserConfiguration(lineNumber, false));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, content, scope, 
                 new ParserConfiguration(lineNumber, extraPositionInformation, false));
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
                 p += 2;
                 pbeg = p;
             }
 
             IRubyObject cc = c.getConstant(str);
             if(!(cc instanceof RubyModule)) {
                 throw newTypeError("" + path + " does not refer to class/module");
             }
             c = (RubyModule)cc;
         }
         return c;
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
 
         if (type == fastGetClass("RuntimeError") && (info == null || info.length() == 0)) {
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
     
     public void loadFile(String scriptName, InputStream in) {
         if (!Ruby.isSecurityRestricted()) {
             File f = new File(scriptName);
             if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
                 scriptName = "./" + scriptName;
             }
         }
 
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
 
             Node node = parseFile(in, scriptName, null);
             ASTInterpreter.eval(this, context, node, self, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
         }
     }
     
     public void compileAndLoadFile(String filename, InputStream in) {
         // FIXME: what is this for?
 //        if (!Ruby.isSecurityRestricted()) {
 //            File f = new File(scriptName);
 //            if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
 //                scriptName = "./" + scriptName;
 //            };
 //        }
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
             
             Node scriptNode = parseFile(in, filename, null);
             
             Script script = tryCompile(scriptNode, new JRubyClassLoader(jrubyClassLoader));
             if (script == null) {
                 System.err.println("Error, could not compile; pass -J-Djruby.jit.logging.verbose=true for more details");
             }
 
             runScript(script);
         } catch (JumpException.ReturnJump rj) {
             return;
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
             
             script.load(context, self, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
         }
     }
 
     public class CallTraceFuncHook implements EventHook {
         private RubyProc traceFunc;
         
         public void setTraceFunc(RubyProc traceFunc) {
             this.traceFunc = traceFunc;
         }
         
         public void event(ThreadContext context, int event, String file, int line, String name, IRubyObject type) {
             if (!context.isWithinTrace()) {
                 if (file == null) file = "(ruby)";
                 if (type == null) type = getFalse();
                 
                 RubyBinding binding = RubyBinding.newBinding(Ruby.this);
 
                 context.preTrace();
                 try {
                     traceFunc.call(new IRubyObject[] {
                         newString(EVENT_NAMES[event]), // event name
                         newString(file), // filename
                         newFixnum(line + 1), // line numbers should be 1-based
                         name != null ? newSymbol(name) : getNil(),
                         binding,
                         type
                     });
                 } finally {
                     context.postTrace();
                 }
             }
         }
 
         public boolean isInterestedInEvent(int event) {
             return true;
         }
     };
     
     private final CallTraceFuncHook callTraceFuncHook = new CallTraceFuncHook();
     
     public void addEventHook(EventHook hook) {
         eventHooks.add(hook);
         hasEventHooks = true;
     }
     
     public void removeEventHook(EventHook hook) {
         eventHooks.remove(hook);
         hasEventHooks = !eventHooks.isEmpty();
     }
 
     public void setTraceFunction(RubyProc traceFunction) {
         removeEventHook(callTraceFuncHook);
         
         if (traceFunction == null) {
             return;
         }
         
         callTraceFuncHook.setTraceFunc(traceFunction);
         addEventHook(callTraceFuncHook);
     }
     
     public void callEventHooks(ThreadContext context, int event, String file, int line, String name, IRubyObject type) {
         for (EventHook eventHook : eventHooks) {
             if (eventHook.isInterestedInEvent(event)) {
                 eventHook.event(context, event, file, line, name, type);
             }
         }
     }
     
     public boolean hasEventHooks() {
         return hasEventHooks;
     }
     
     public GlobalVariables getGlobalVariables() {
         return globalVariables;
     }
 
     // For JSR 223 support: see http://scripting.java.net/
     public void setGlobalVariables(GlobalVariables globalVariables) {
         this.globalVariables = globalVariables;
     }
 
     public CallbackFactory callbackFactory(Class<?> type) {
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
 
     // use this for JRuby-internal finalizers
     public void addInternalFinalizer(Finalizable finalizer) {
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers == null) {
                 internalFinalizers = new WeakHashMap<Finalizable, Object>();
             }
             internalFinalizers.put(finalizer, null);
         }
     }
 
     // this method is for finalizers registered via ObjectSpace
     public void addFinalizer(Finalizable finalizer) {
         synchronized (finalizersMutex) {
             if (finalizers == null) {
                 finalizers = new WeakHashMap<Finalizable, Object>();
             }
             finalizers.put(finalizer, null);
         }
     }
     
     public void removeInternalFinalizer(Finalizable finalizer) {
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers != null) {
                 internalFinalizers.remove(finalizer);
             }
         }
     }
 
     public void removeFinalizer(Finalizable finalizer) {
         synchronized (finalizersMutex) {
             if (finalizers != null) {
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
             RubyProc proc = atExitBlocks.pop();
 
             proc.call(IRubyObject.NULL_ARRAY);
         }
         if (finalizers != null) {
             synchronized (finalizers) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(finalizers.keySet()).iterator(); finalIter.hasNext();) {
                     finalIter.next().finalize();
                     finalIter.remove();
                 }
             }
         }
 
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers != null) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(
                         internalFinalizers.keySet()).iterator(); finalIter.hasNext();) {
                     finalIter.next().finalize();
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
     
     public RubyArray newArray(List<IRubyObject> list) {
         return RubyArray.newArray(this, list);
     }
 
     public RubyArray newArray(int size) {
         return RubyArray.newArray(this, size);
     }
 
     public RubyBoolean newBoolean(boolean value) {
         return RubyBoolean.newBoolean(this, value);
     }
 
     public RubyFileStat newFileStat(String filename, boolean lstat) {
         return RubyFileStat.newFileStat(this, filename, lstat);
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
 
     public RubyProc newProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, type);
 
         proc.callInit(IRubyObject.NULL_ARRAY, block);
 
         return proc;
     }
 
     public RubyBinding newBinding() {
         return RubyBinding.newBinding(this);
     }
 
     public RubyBinding newBinding(Binding binding) {
         return RubyBinding.newBinding(this, binding);
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
 
     public RubySymbol newSymbol(String name) {
         return symbolTable.getSymbol(name);
     }
 
     /**
      * Faster than {@link #newSymbol(String)} if you already have an interned
      * name String. Don't intern your string just to call this version - the
      * overhead of interning will more than wipe out any benefit from the faster
      * lookup.
      *   
      * @param internedName the symbol name, <em>must</em> be interned! if in
      *                     doubt, call {@link #newSymbol(String)} instead.
      * @return the symbol for name
      */
     public RubySymbol fastNewSymbol(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
 
         return symbolTable.fastGetSymbol(internedName);
     }
 
     public RubyTime newTime(long milliseconds) {
         return RubyTime.newTime(this, milliseconds);
     }
 
     public RaiseException newRuntimeError(String message) {
         return newRaiseException(fastGetClass("RuntimeError"), message);
     }    
     
     public RaiseException newArgumentError(String message) {
         return newRaiseException(fastGetClass("ArgumentError"), message);
     }
 
     public RaiseException newArgumentError(int got, int expected) {
         return newRaiseException(fastGetClass("ArgumentError"), "wrong # of arguments(" + got + " for " + expected + ")");
     }
 
     public RaiseException newErrnoEBADFError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoEPIPEError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EPIPE"), "Broken pipe");
     }
 
     public RaiseException newErrnoECONNREFUSEDError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ECONNREFUSED"), "Connection refused");
     }
 
     public RaiseException newErrnoEADDRINUSEError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EADDRINUSE"), "Address in use");
     }
 
     public RaiseException newErrnoEINVALError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EINVAL"), "Invalid file");
     }
 
     public RaiseException newErrnoENOENTError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ENOENT"), "File not found");
     }
 
     public RaiseException newErrnoEACCESError(String message) {
         return newRaiseException(
                 fastGetModule("Errno").fastGetClass("EACCES"), message);
     }
 
     public RaiseException newErrnoEAGAINError(String message) {
         return newRaiseException(
                 fastGetModule("Errno").fastGetClass("EAGAIN"), message);
     }
 
     public RaiseException newErrnoEISDirError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EISDIR"), "Is a directory");
     }
 
     public RaiseException newErrnoESPIPEError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ESPIPE"), "Illegal seek");
     }
 
     public RaiseException newErrnoEBADFError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EBADF"), message);
     }
 
     public RaiseException newErrnoEINVALError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EINVAL"), message);
     }
 
     public RaiseException newErrnoENOTDIRError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ENOTDIR"), message);
     }
 
     public RaiseException newErrnoENOENTError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ENOENT"), message);
     }
 
     public RaiseException newErrnoESPIPEError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ESPIPE"), message);
     }
 
     public RaiseException newErrnoEEXISTError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EEXIST"), message);
     }
     
     public RaiseException newErrnoEDOMError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EDOM"), "Domain error - " + message);
     }    
 
     public RaiseException newIndexError(String message) {
         return newRaiseException(fastGetClass("IndexError"), message);
     }
 
     public RaiseException newSecurityError(String message) {
         return newRaiseException(fastGetClass("SecurityError"), message);
     }
 
     public RaiseException newSystemCallError(String message) {
         return newRaiseException(fastGetClass("SystemCallError"), message);
     }
 
     public RaiseException newTypeError(String message) {
         return newRaiseException(fastGetClass("TypeError"), message);
     }
 
     public RaiseException newThreadError(String message) {
         return newRaiseException(fastGetClass("ThreadError"), message);
     }
 
     public RaiseException newSyntaxError(String message) {
         return newRaiseException(fastGetClass("SyntaxError"), message);
     }
 
     public RaiseException newRegexpError(String message) {
         return newRaiseException(fastGetClass("RegexpError"), message);
     }
 
     public RaiseException newRangeError(String message) {
         return newRaiseException(fastGetClass("RangeError"), message);
     }
 
     public RaiseException newNotImplementedError(String message) {
         return newRaiseException(fastGetClass("NotImplementedError"), message);
     }
     
     public RaiseException newInvalidEncoding(String message) {
         return newRaiseException(fastGetClass("Iconv").fastGetClass("InvalidEncoding"), message);
     }
 
     public RaiseException newNoMethodError(String message, String name, IRubyObject args) {
         return new RaiseException(new RubyNoMethodError(this, this.fastGetClass("NoMethodError"), message, name, args), true);
     }
 
     public RaiseException newNameError(String message, String name) {
         return new RaiseException(new RubyNameError(this, this.fastGetClass("NameError"), message, name), true);
     }
 
     public RaiseException newLocalJumpError(String reason, IRubyObject exitValue, String message) {
         return new RaiseException(new RubyLocalJumpError(this, fastGetClass("LocalJumpError"), message, reason, exitValue), true);
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(fastGetClass("LoadError"), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(fastGetClass("TypeError"), "can't modify frozen " + objectType);
     }
 
     public RaiseException newSystemStackError(String message) {
         return newRaiseException(fastGetClass("SystemStackError"), message);
     }
 
     public RaiseException newSystemExit(int status) {
         return new RaiseException(RubySystemExit.newInstance(this, status));
     }
 
     public RaiseException newIOError(String message) {
         return newRaiseException(fastGetClass("IOError"), message);
     }
 
     public RaiseException newStandardError(String message) {
         return newRaiseException(fastGetClass("StandardError"), message);
     }
 
     public RaiseException newIOErrorFromException(IOException ioe) {
         return newRaiseException(fastGetClass("IOError"), ioe.getMessage());
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyClass expectedType) {
         return newRaiseException(fastGetClass("TypeError"), "wrong argument type " +
                 receivedObject.getMetaClass().getRealClass() + " (expected " + expectedType + ")");
     }
 
     public RaiseException newEOFError() {
         return newRaiseException(fastGetClass("EOFError"), "End of file reached");
     }
 
     public RaiseException newEOFError(String message) {
         return newRaiseException(fastGetClass("EOFError"), message);
     }
 
     public RaiseException newZeroDivisionError() {
         return newRaiseException(fastGetClass("ZeroDivisionError"), "divided by 0");
     }
 
     public RaiseException newFloatDomainError(String message){
         return newRaiseException(fastGetClass("FloatDomainError"), message);
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
 
     public Hashtable<Integer, WeakReference<IOHandler>> getIoHandlers() {
         return ioHandlers;
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
 
     private ThreadLocal<Map<Object, Object>> inspect = new ThreadLocal<Map<Object, Object>>();
     public void registerInspecting(Object obj) {
         Map<Object, Object> val = inspect.get();
         if (val == null) inspect.set(val = new IdentityHashMap<Object, Object>());
         val.put(obj, null);
     }
 
     public boolean isInspecting(Object obj) {
         Map<Object, Object> val = inspect.get();
         return val == null ? false : val.containsKey(obj);
     }
 
     public void unregisterInspecting(Object obj) {
         Map<Object, Object> val = inspect.get();
         if (val != null ) val.remove(obj);
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
         
         try {
             // This comment also in rbConfigLibrary
             // Our shell scripts pass in non-canonicalized paths, but even if we didn't
             // anyone who did would become unhappy because Ruby apps expect no relative
             // operators in the pathname (rubygems, for example).
             return new NormalizedFile(jrubyHome).getCanonicalPath();
         } catch (IOException e) {}
         
         return new NormalizedFile(jrubyHome).getAbsolutePath();
     }
     
     public void setJRubyHome(String home) {
         jrubyHome = verifyHome(home);
     }
 
     // We require the home directory to be absolute
     private String verifyHome(String home) {
         if (home.equals(".")) {
             home = System.getProperty("user.dir");
         }
         NormalizedFile f = new NormalizedFile(home);
         if (!f.isAbsolute()) {
             home = f.getAbsolutePath();
         }
         f.mkdirs();
         return home;
     }
 
     public RubyInstanceConfig getInstanceConfig() {
         return config;
diff --git a/src/org/jruby/RubyInstanceConfig.java b/src/org/jruby/RubyInstanceConfig.java
index f7f6f7b787..8efcefac41 100644
--- a/src/org/jruby/RubyInstanceConfig.java
+++ b/src/org/jruby/RubyInstanceConfig.java
@@ -1,726 +1,728 @@
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
 
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.KCode;
+import org.jruby.util.SafePropertyAccessor;
 
 public class RubyInstanceConfig {
     
     public enum CompileMode {
         JIT, FORCE, OFF;
         
         public boolean shouldPrecompileCLI() {
             switch (this) {
             case JIT: case FORCE:
                 return true;
             }
             return false;
         }
         
         public boolean shouldJIT() {
             switch (this) {
             case JIT: case FORCE:
                 return true;
             }
             return false;
         }
         
         public boolean shouldPrecompileAll() {
             return this == FORCE;
         }
     }
     private InputStream input          = System.in;
     private PrintStream output         = System.out;
     private PrintStream error          = System.err;
     private Profile profile            = Profile.DEFAULT;
-    private boolean objectSpaceEnabled = false;
+    private boolean objectSpaceEnabled
+            = SafePropertyAccessor.getBoolean("jruby.objectspace.enabled", false);
     private CompileMode compileMode = CompileMode.JIT;
     private boolean runRubyInProcess   = true;
     private String currentDirectory;
     private Map environment;
     private String[] argv = {};
 
     private final boolean jitLogging;
     private final boolean jitLoggingVerbose;
     private final int jitThreshold;
     private final boolean samplingEnabled;
     private CompatVersion compatVersion;
 
     private final String defaultRegexpEngine;
     private final JRubyClassLoader defaultJRubyClassLoader;
 
     private ClassLoader loader = Thread.currentThread().getContextClassLoader();
     
     // from CommandlineParser
     private List<String> loadPaths = new ArrayList<String>();
     private StringBuffer inlineScript = new StringBuffer();
     private boolean hasInlineScript = false;
     private String scriptFileName = null;
     private List<String> requiredLibraries = new ArrayList<String>();
     private boolean benchmarking = false;
     private boolean assumeLoop = false;
     private boolean assumePrinting = false;
     private boolean processLineEnds = false;
     private boolean split = false;
     private boolean verbose = false;
     private boolean debug = false;
     private boolean showVersion = false;
     private boolean endOfArguments = false;
     private boolean shouldRunInterpreter = true;
     private boolean shouldPrintUsage = false;
     private boolean shouldPrintProperties=false;
     private boolean yarv = false;
     private boolean rubinius = false;
     private boolean yarvCompile = false;
     private KCode kcode = KCode.NONE;
     
-    public static final boolean FRAMELESS_COMPILE_ENABLED = Boolean.getBoolean("jruby.compile.frameless");
+    public static final boolean FRAMELESS_COMPILE_ENABLED
+            = SafePropertyAccessor.getBoolean("jruby.compile.frameless");
     public static boolean nativeEnabled = true;
     
+    static {
+        try {
+            if (System.getProperty("jruby.native.enabled") != null) {
+                nativeEnabled = Boolean.getBoolean("jruby.native.enabled");
+            }
+        } catch (SecurityException se) {
+            nativeEnabled = false;
+        }
+    }
+
     public int characterIndex = 0;
     
     public static interface LoadServiceCreator {
         LoadService create(Ruby runtime);
 
         LoadServiceCreator DEFAULT = new LoadServiceCreator() {
             public LoadService create(Ruby runtime) {
                 return new LoadService(runtime);
             }
         };
     }
 
     private LoadServiceCreator creator = LoadServiceCreator.DEFAULT;
-    
-    static {
-        if (System.getProperty("jruby.native.enabled") != null) {
-            nativeEnabled = Boolean.getBoolean("jruby.native.enabled");
-        }
-    }
 
     public RubyInstanceConfig() {
         if (Ruby.isSecurityRestricted())
             currentDirectory = "/";
         else {
             currentDirectory = JRubyFile.getFileProperty("user.dir");
-            if (System.getProperty("jruby.objectspace.enabled") != null) {
-                objectSpaceEnabled = Boolean.getBoolean("jruby.objectspace.enabled");
-            }
         }
 
-        samplingEnabled = System.getProperty("jruby.sampling.enabled") != null && Boolean.getBoolean("jruby.sampling.enabled");
-        String compatString = System.getProperty("jruby.compat.version", "RUBY1_8");
+        samplingEnabled = SafePropertyAccessor.getBoolean("jruby.sampling.enabled", false);
+        String compatString = SafePropertyAccessor.getProperty("jruby.compat.version", "RUBY1_8");
         if (compatString.equalsIgnoreCase("RUBY1_8")) {
             compatVersion = CompatVersion.RUBY1_8;
         } else if (compatString.equalsIgnoreCase("RUBY1_9")) {
             compatVersion = CompatVersion.RUBY1_9;
         } else {
             System.err.println("Compatibility version `" + compatString + "' invalid; use RUBY1_8 or RUBY1_9. Using RUBY1_8.");
             compatVersion = CompatVersion.RUBY1_8;
         }
         
         if (Ruby.isSecurityRestricted()) {
             compileMode = CompileMode.OFF;
             jitLogging = false;
             jitLoggingVerbose = false;
             jitThreshold = -1;
         } else {
-            String threshold = System.getProperty("jruby.jit.threshold");
+            String threshold = SafePropertyAccessor.getProperty("jruby.jit.threshold");
 
-            if (System.getProperty("jruby.launch.inproc") != null) {
-                runRubyInProcess = Boolean.getBoolean("jruby.launch.inproc");
-            }
-            boolean jitProperty = System.getProperty("jruby.jit.enabled") != null;
+            runRubyInProcess = SafePropertyAccessor.getBoolean("jruby.launch.inproc", true);
+            boolean jitProperty = SafePropertyAccessor.getProperty("jruby.jit.enabled") != null;
             if (jitProperty) {
                 error.print("jruby.jit.enabled property is deprecated; use jruby.compile.mode=(OFF|JIT|FORCE) for -C, default, and +C flags");
-                compileMode = Boolean.getBoolean("jruby.jit.enabled") ? CompileMode.JIT : CompileMode.OFF;
+                compileMode = SafePropertyAccessor.getBoolean("jruby.jit.enabled") ? CompileMode.JIT : CompileMode.OFF;
             } else {
-                String jitModeProperty = System.getProperty("jruby.compile.mode", "JIT");
+                String jitModeProperty = SafePropertyAccessor.getProperty("jruby.compile.mode", "JIT");
                 
                 if (jitModeProperty.equals("OFF")) {
                     compileMode = CompileMode.OFF;
                 } else if (jitModeProperty.equals("JIT")) {
                     compileMode = CompileMode.JIT;
                 } else if (jitModeProperty.equals("FORCE")) {
                     compileMode = CompileMode.FORCE;
                 } else {
                     error.print("jruby.jit.mode property must be OFF, JIT, FORCE, or unset; defaulting to JIT");
                     compileMode = CompileMode.JIT;
                 }
             }
-            jitLogging = Boolean.getBoolean("jruby.jit.logging");
-            jitLoggingVerbose = Boolean.getBoolean("jruby.jit.logging.verbose");
+            jitLogging = SafePropertyAccessor.getBoolean("jruby.jit.logging");
+            jitLoggingVerbose = SafePropertyAccessor.getBoolean("jruby.jit.logging.verbose");
             jitThreshold = threshold == null ? 20 : Integer.parseInt(threshold); 
         }
 
-        defaultRegexpEngine = System.getProperty("jruby.regexp","jregex");
+        defaultRegexpEngine = SafePropertyAccessor.getProperty("jruby.regexp","jregex");
         defaultJRubyClassLoader = null;
     }
     
     public String getBasicUsageHelp() {
         StringBuffer sb = new StringBuffer();
         sb
                 .append("Usage: jruby [switches] [--] [rubyfile.rb] [arguments]").append("\n")
                 .append("    -e 'command'    one line of script. Several -e's allowed. Omit [programfile]").append("\n")
                 .append("    -b              benchmark mode, times the script execution").append("\n")
                 .append("    -Jjava option   pass an option on to the JVM (e.g. -J-Xmx512m)").append("\n")
                 .append("    -Idirectory     specify $LOAD_PATH directory (may be used more than once)").append("\n")
                 .append("    --              optional -- before rubyfile.rb for compatibility with ruby").append("\n")
                 .append("    -d              set debugging flags (set $DEBUG to true)").append("\n")
                 .append("    -v              print version number, then turn on verbose mode").append("\n")
                 .append("    -O              run with ObjectSpace disabled (default; improves performance)").append("\n")
                 .append("    +O              run with ObjectSpace enabled (reduces performance)").append("\n")
                 .append("    -S cmd          run the specified command in JRuby's bin dir").append("\n")
                 .append("    -C              disable all compilation").append("\n")
                 .append("    +C              force compilation of all scripts before they are run (except eval)").append("\n")
                 .append("    -y              read a YARV-compiled Ruby script and run that (EXPERIMENTAL)").append("\n")
                 .append("    -Y              compile a Ruby script into YARV bytecodes and run this (EXPERIMENTAL)").append("\n")
                 .append("    -R              read a Rubinius-compiled Ruby script and run that (EXPERIMENTAL)").append("\n")
                 .append("    --properties    List all configuration properties (specify with -J-Dproperty=value)").append("\n");
         
         return sb.toString();
     }
     
     public String getPropertyHelp() {
         StringBuffer sb = new StringBuffer();
         sb
                 .append("These properties can be used to alter runtime behavior for perf or compatibility.").append("\n")
                 .append("Specify them by passing -J-D<property>=<value>").append("\n")
                 .append("    jruby.objectspace.enabled=true|false").append("\n")
                 .append("       Enable or disable ObjectSpace.each_object (default is disabled)").append("\n")
                 .append("    jruby.compile.mode=JIT|FORCE|OFF").append("\n")
                 .append("       Set compilation mode. JIT is default; FORCE compiles all, OFF disables").append("\n")
                 .append("    jruby.compile.boxed=true|false").append("\n")
                 .append("       Use boxed variables; this can speed up some methods. Default is false").append("\n")
                 .append("    jruby.compile.frameless=true|false").append("\n")
                 .append("       (EXPERIMENTAL) Turn on frameless compilation where possible").append("\n")
                 .append("    jruby.jit.threshold=<invocation count>").append("\n")
                 .append("       Set the JIT threshold to the specified method invocation count. Default is 20").append("\n")
                 .append("    jruby.jit.logging=true|false").append("\n")
                 .append("       Enable JIT logging (reports successful compilation). Default is false").append("\n")
                 .append("    jruby.jit.logging.verbose=true|false").append("\n")
                 .append("       Enable verbose JIT logging (reports failed compilation). Default is false").append("\n")
                 .append("    jruby.launch.inproc=true|false").append("\n")
                 .append("       Set in-process launching of e.g. system('ruby ...'). Default is true").append("\n")
                 .append("    jruby.native.enabled=true|false").append("\n")
                 .append("       Enable/disable native extensions (like JNA for non-Java APIs; Default is true").append("\n")
                 .append("       (This affects all JRuby instances in a given JVM)").append("\n");
         
         return sb.toString();
     }
     
     public String getVersionString() {
         StringBuffer buf = new StringBuffer("ruby ");
         switch (compatVersion) {
         case RUBY1_8:
             buf.append(Constants.RUBY_VERSION);
             break;
         case RUBY1_9:
             buf.append(Constants.RUBY1_9_VERSION);
             break;
         }
         buf
                 .append(" (")
                 .append(Constants.COMPILE_DATE + " rev " + Constants.REVISION)
                 .append(") [")
-                .append(System.getProperty("os.arch") + "-jruby" + Constants.VERSION)
+                .append(SafePropertyAccessor.getProperty("os.arch", "unknown") + "-jruby" + Constants.VERSION)
                 .append("]")
                 .append("\n");
         
         return buf.toString();
     }
     
     public void processArguments(String[] arguments) {
         new ArgumentProcessor(arguments).processArguments();
     }
 
     public LoadServiceCreator getLoadServiceCreator() {
         return creator;
     }
 
     public void setLoadServiceCreator(LoadServiceCreator creator) {
         this.creator = creator;
     }
 
     public LoadService createLoadService(Ruby runtime) {
         return this.creator.create(runtime);
     }
 
     public CompileMode getCompileMode() {
         return compileMode;
     }
     
     public void setCompileMode(CompileMode compileMode) {
         this.compileMode = compileMode;
     }
 
     public boolean isJitLogging() {
         return jitLogging;
     }
 
     public boolean isJitLoggingVerbose() {
         return jitLoggingVerbose;
     }
 
     public boolean isSamplingEnabled() {
         return samplingEnabled;
     }
     
     public int getJitThreshold() {
         return jitThreshold;
     }
     
     public boolean isRunRubyInProcess() {
         return runRubyInProcess;
     }
     
     public void setRunRubyInProcess(boolean flag) {
         this.runRubyInProcess = flag;
     }
 
     public void setInput(InputStream newInput) {
         input = newInput;
     }
 
     public InputStream getInput() {
         return input;
     }
 
     public CompatVersion getCompatVersion() {
         return compatVersion;
     }
 
     public void setOutput(PrintStream newOutput) {
         output = newOutput;
     }
 
     public PrintStream getOutput() {
         return output;
     }
 
     public void setError(PrintStream newError) {
         error = newError;
     }
 
     public PrintStream getError() {
         return error;
     }
 
     public void setCurrentDirectory(String newCurrentDirectory) {
         currentDirectory = newCurrentDirectory;
     }
 
     public String getCurrentDirectory() {
         return currentDirectory;
     }
 
     public void setProfile(Profile newProfile) {
         profile = newProfile;
     }
 
     public Profile getProfile() {
         return profile;
     }
 
     public void setObjectSpaceEnabled(boolean newObjectSpaceEnabled) {
         objectSpaceEnabled = newObjectSpaceEnabled;
     }
 
     public boolean isObjectSpaceEnabled() {
         return objectSpaceEnabled;
     }
 
     public void setEnvironment(Map newEnvironment) {
         environment = newEnvironment;
     }
 
     public Map getEnvironment() {
         return environment;
     }
 
     public String getDefaultRegexpEngine() {
         return defaultRegexpEngine;
     }
     
     public JRubyClassLoader getJRubyClassLoader() {
         return defaultJRubyClassLoader;
     }
 
     public ClassLoader getLoader() {
         return loader;
     }
 
     public void setLoader(ClassLoader loader) {
         this.loader = loader;
     }
     
     public String[] getArgv() {
         return argv;
     }
     
     public void setArgv(String[] argv) {
         this.argv = argv;
     }
 
     private class ArgumentProcessor {
         private String[] arguments;
         private int argumentIndex = 0;
         
         public ArgumentProcessor(String[] arguments) {
             this.arguments = arguments;
         }
         
         public void processArguments() {
             while (argumentIndex < arguments.length && isInterpreterArgument(arguments[argumentIndex])) {
                 processArgument();
                 argumentIndex++;
             }
 
             if (!hasInlineScript) {
                 if (argumentIndex < arguments.length) {
                     setScriptFileName(arguments[argumentIndex]); //consume the file name
                     argumentIndex++;
                 }
             }
 
 
             // Remaining arguments are for the script itself
             argv = new String[arguments.length - argumentIndex];
             System.arraycopy(arguments, argumentIndex, argv, 0, argv.length);
         }
 
         private boolean isInterpreterArgument(String argument) {
             return (argument.charAt(0) == '-' || argument.charAt(0) == '+') && !endOfArguments;
         }
 
         private void processArgument() {
             String argument = arguments[argumentIndex];
             FOR : for (characterIndex = 1; characterIndex < argument.length(); characterIndex++) {
                 switch (argument.charAt(characterIndex)) {
                     case 'h' :
                         shouldPrintUsage = true;
                         shouldRunInterpreter = false;
                         break;
                     case 'I' :
                         String s = grabValue(" -I must be followed by a directory name to add to lib path");
                         String[] ls = s.split(java.io.File.pathSeparator);
                         for(int i=0;i<ls.length;i++) {
                             loadPaths.add(ls[i]);
                         }
                         break FOR;
                     case 'r' :
                         requiredLibraries.add(grabValue("-r must be followed by a package to require"));
                         break FOR;
                     case 'e' :
                         inlineScript.append(grabValue(" -e must be followed by an expression to evaluate"));
                         inlineScript.append('\n');
                         hasInlineScript = true;
                         break FOR;
                     case 'b' :
                         benchmarking = true;
                         break;
                     case 'p' :
                         assumePrinting = true;
                         assumeLoop = true;
                         break;
                     case 'O' :
                         if (argument.charAt(0) == '-') {
                             objectSpaceEnabled = false;
                         } else if (argument.charAt(0) == '+') {
                             objectSpaceEnabled = true;
                         }
                         break;
                     case 'C' :
                         if (argument.charAt(0) == '-') {
                             compileMode = CompileMode.OFF;
                         } else if (argument.charAt(0) == '+') {
                             compileMode = CompileMode.FORCE;
                         }
                         break;
                     case 'y' :
                         yarv = true;
                         break;
                     case 'Y' :
                         yarvCompile = true;
                         break;
                     case 'R' :
                         rubinius = true;
                         break;
                     case 'n' :
                         assumeLoop = true;
                         break;
                     case 'a' :
                         split = true;
                         break;
                     case 'd' :
                         debug = true;
                         verbose = true;
                         break;
                     case 'l' :
                         processLineEnds = true;
                         break;
                     case 'v' :
                         verbose = true;
                         setShowVersion(true);
                         break;
                     case 'w' :
                         verbose = true;
                         break;
                     case 'K':
                         // FIXME: No argument seems to work for -K in MRI plus this should not
                         // siphon off additional args 'jruby -K ~/scripts/foo'.  Also better error
                         // processing.
                         String eArg = grabValue("provide a value for -K");
                         kcode = KCode.create(null, eArg);
                         break;
                     case 'S':
                         runBinScript();
                         break FOR;
                     case '-' :
                         if (argument.equals("--version")) {
                             setShowVersion(true);
                             break FOR;
                         } else if(argument.equals("--debug")) {
                             debug = true;
                             verbose = true;
                             break;
                         } else if (argument.equals("--help")) {
                             shouldPrintUsage = true;
                             shouldRunInterpreter = false;
                             break;
                         } else if (argument.equals("--command") || argument.equals("--bin")) {
                             characterIndex = argument.length();
                             runBinScript();
                             break;
                         } else if (argument.equals("--compat")) {
                             characterIndex = argument.length();
                             compatVersion = CompatVersion.getVersionFromString(grabValue("--compat must be RUBY1_8 or RUBY1_9"));
                             if (compatVersion == null) {
                                 compatVersion = CompatVersion.RUBY1_8;
                             }
                             break FOR;
                         } else if (argument.equals("--properties")) {
                             shouldPrintProperties = true;
                             shouldRunInterpreter = false;
                             break;
                         } else {
                             if (argument.equals("--")) {
                                 // ruby interpreter compatibilty 
                                 // Usage: ruby [switches] [--] [programfile] [arguments])
                                 endOfArguments = true;
                                 break;
                             }
                         }
                     default :
                         throw new MainExitException(1, "unknown option " + argument.charAt(characterIndex));
                 }
             }
         }
 
         private void runBinScript() {
             requiredLibraries.add("jruby/commands");
             inlineScript.append("JRuby::Commands." + grabValue("provide a bin script to execute"));
             inlineScript.append("\n");
             hasInlineScript = true;
             endOfArguments = true;
         }
 
         private String grabValue(String errorMessage) {
             characterIndex++;
             if (characterIndex < arguments[argumentIndex].length()) {
                 return arguments[argumentIndex].substring(characterIndex);
             }
             argumentIndex++;
             if (argumentIndex < arguments.length) {
                 return arguments[argumentIndex];
             }
 
             MainExitException mee = new MainExitException(1, "invalid argument " + argumentIndex + "\n" + errorMessage);
             mee.setUsageError(true);
 
             throw mee;
         }
     }
 
     public byte[] inlineScript() {
         try {
             return inlineScript.toString().getBytes("UTF-8");
         } catch (UnsupportedEncodingException e) {
             return inlineScript.toString().getBytes();
         }
     }
 
     public List<String> requiredLibraries() {
         return requiredLibraries;
     }
 
     public List<String> loadPaths() {
         return loadPaths;
     }
 
     public boolean shouldRunInterpreter() {
         if(isShowVersion() && (hasInlineScript || scriptFileName != null)) {
             return true;
         }
         return isShouldRunInterpreter();
     }
     
     public boolean shouldPrintUsage() {
         return shouldPrintUsage;
     }
     
     public boolean shouldPrintProperties() {
         return shouldPrintProperties;
     }
     
     private boolean isSourceFromStdin() {
         return getScriptFileName() == null;
     }
     
     public boolean isInlineScript() {
         return hasInlineScript;
     }
 
     public InputStream getScriptSource() {
         try {
             // KCode.NONE is used because KCODE does not affect parse in Ruby 1.8
             // if Ruby 2.0 encoding pragmas are implemented, this will need to change
             if (hasInlineScript) {
                 if (scriptFileName != null) {
                     File file = JRubyFile.create(getCurrentDirectory(), getScriptFileName());
                     return new FileInputStream(file);
                 }
                 
                 return new ByteArrayInputStream(inlineScript());
             } else if (isSourceFromStdin()) {
                 // can't use -v and stdin
                 if (isShowVersion()) {
                     return null;
                 }
                 return getInput();
             } else {
                 File file = JRubyFile.create(getCurrentDirectory(), getScriptFileName());
                 return new FileInputStream(file);
             }
         } catch (IOException e) {
             throw new MainExitException(1, "Error opening script file: " + e.getMessage());
         }
     }
 
     public String displayedFileName() {
         if (hasInlineScript) {
             if (scriptFileName != null) {
                 return scriptFileName;
             } else {
                 return "-e";
             }
         } else if (isSourceFromStdin()) {
             return "-";
         } else {
             return getScriptFileName();
         }
     }
 
     private void setScriptFileName(String scriptFileName) {
         this.scriptFileName = scriptFileName;
     }
 
     public String getScriptFileName() {
         return scriptFileName;
     }
 
     public boolean isBenchmarking() {
         return benchmarking;
     }
 
     public boolean isAssumeLoop() {
         return assumeLoop;
     }
 
     public boolean isAssumePrinting() {
         return assumePrinting;
     }
 
     public boolean isProcessLineEnds() {
         return processLineEnds;
     }
 
     public boolean isSplit() {
         return split;
     }
 
     public boolean isVerbose() {
         return verbose;
     }
 
     public boolean isDebug() {
         return debug;
     }
 
     public boolean isShowVersion() {
         return showVersion;
     }
 
     protected void setShowVersion(boolean showVersion) {
         this.showVersion = showVersion;
     }
 
     public boolean isShouldRunInterpreter() {
         return shouldRunInterpreter;
     }
 
     public boolean isYARVEnabled() {
         return yarv;
     }
 
     public boolean isRubiniusEnabled() {
         return rubinius;
     }
 
     public boolean isYARVCompileEnabled() {
         return yarvCompile;
     }
     
     public KCode getKCode() {
         return kcode;
     }
 }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 76f1dfecfa..551abb85d4 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1457 +1,1459 @@
 /*
  **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2006-2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 package org.jruby;
 
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.locks.ReentrantLock;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.internal.runtime.methods.AliasMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.FullFunctionCallbackMethod;
 import org.jruby.internal.runtime.methods.SimpleCallbackMethod;
 import org.jruby.internal.runtime.methods.MethodMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.Dispatcher;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ClassProvider;
 import org.jruby.util.IdUtil;
 import org.jruby.util.MethodCache;
+import org.jruby.util.SafePropertyAccessor;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.MethodIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyModule extends RubyObject {
     
     public static RubyClass createModuleClass(Ruby runtime, RubyClass moduleClass) {
         moduleClass.index = ClassIndex.MODULE;
         moduleClass.kindOf = new RubyModule.KindOf() {
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyModule;
             }
         };
         
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyModule.class);
         
         moduleClass.defineAnnotatedMethods(RubyModule.class);
         moduleClass.dispatcher = callbackFactory.createDispatcher(moduleClass);
 
         callbackFactory = runtime.callbackFactory(RubyKernel.class);
         moduleClass.defineFastMethod("autoload", callbackFactory.getFastSingletonMethod("autoload", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         moduleClass.defineFastMethod("autoload?", callbackFactory.getFastSingletonMethod("autoload_p", RubyKernel.IRUBY_OBJECT));
 
         return moduleClass;
     }    
     
     static ObjectAllocator MODULE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyModule(runtime, klass);
         }
     };
     
     public int getNativeTypeIndex() {
         return ClassIndex.MODULE;
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
     
     // superClass may be null.
     protected RubyClass superClass;
 
     public int index;
     
     public Dispatcher dispatcher = Dispatcher.DEFAULT_DISPATCHER;
 
     public static class KindOf {
         public static final KindOf DEFAULT_KIND_OF = new KindOf();
         public boolean isKindOf(IRubyObject obj, RubyModule type) {
             return obj.getMetaClass().hasModuleInHierarchy(type);
         }
     }
     
     public boolean isInstance(IRubyObject object) {
         return kindOf.isKindOf(object, this);
     }
 
     public KindOf kindOf = KindOf.DEFAULT_KIND_OF;
 
     public final int id;
 
     // Containing class...The parent of Object is null. Object should always be last in chain.
     public RubyModule parent;
 
     // ClassId is the name of the class/module sans where it is located.
     // If it is null, then it an anonymous class.
     protected String classId;
 
 
     // CONSTANT TABLE
     
     // Lock used for variableTable/constantTable writes. The RubyObject variableTable
     // write methods are overridden here to use this lock rather than Java
     // synchronization for faster concurrent writes for modules/classes.
     protected final ReentrantLock variableWriteLock = new ReentrantLock();
     
     protected transient volatile ConstantTableEntry[] constantTable =
         new ConstantTableEntry[CONSTANT_TABLE_DEFAULT_CAPACITY];
 
     protected transient int constantTableSize;
 
     protected transient int constantTableThreshold = 
         (int)(CONSTANT_TABLE_DEFAULT_CAPACITY * CONSTANT_TABLE_LOAD_FACTOR);
 
     private final Map methods = new ConcurrentHashMap(12, 2.0f, 1);
     
     // ClassProviders return Java class/module (in #defineOrGetClassUnder and
     // #defineOrGetModuleUnder) when class/module is opened using colon syntax. 
     private transient List classProviders;
 
     /** separate path for MetaClass construction
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass, boolean objectSpace) {
         super(runtime, metaClass, objectSpace);
         id = ++runtime.moduleLastId;
         // if (parent == null) parent = runtime.getObject();
         setFlag(USER7_F, !isClass());
     }
     
     /** used by MODULE_ALLOCATOR and RubyClass constructors
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
     
     /** standard path for Module construction
      * 
      */
     protected RubyModule(Ruby runtime) {
         this(runtime, runtime.getModule());
     }
 
     public boolean needsImplementer() {
         return getFlag(USER7_F);
     }
     
     /** rb_module_new
      * 
      */
     public static RubyModule newModule(Ruby runtime) {
         return new RubyModule(runtime);
     }
     
     /** rb_module_new/rb_define_module_id/rb_name_class/rb_set_class_path
      * 
      */
     public static RubyModule newModule(Ruby runtime, String name, RubyModule parent, boolean setParent) {
         RubyModule module = newModule(runtime);
         module.setBaseName(name);
         if (setParent) module.setParent(parent);
         parent.setConstant(name, module);
         return module;
     }
     
     // synchronized method per JRUBY-1173 (unsafe Double-Checked Locking)
     // FIXME: synchronization is still wrong in CP code
     public synchronized void addClassProvider(ClassProvider provider) {
         if (classProviders == null) {
             List cp = Collections.synchronizedList(new ArrayList());
             cp.add(provider);
             classProviders = cp;
         } else {
             synchronized(classProviders) {
                 if (!classProviders.contains(provider)) {
                     classProviders.add(provider);
                 }
             }
         }
     }
 
     public void removeClassProvider(ClassProvider provider) {
         if (classProviders != null) {
             classProviders.remove(provider);
         }
     }
 
     private RubyClass searchProvidersForClass(String name, RubyClass superClazz) {
         if (classProviders != null) {
             synchronized(classProviders) {
                 RubyClass clazz;
                 for (Iterator iter = classProviders.iterator(); iter.hasNext(); ) {
                     if ((clazz = ((ClassProvider)iter.next())
                             .defineClassUnder(this, name, superClazz)) != null) {
                         return clazz;
                     }
                 }
             }
         }
         return null;
     }
 
     private RubyModule searchProvidersForModule(String name) {
         if (classProviders != null) {
             synchronized(classProviders) {
                 RubyModule module;
                 for (Iterator iter = classProviders.iterator(); iter.hasNext(); ) {
                     if ((module = ((ClassProvider)iter.next()).defineModuleUnder(this, name)) != null) {
                         return module;
                     }
                 }
             }
         }
         return null;
     }
 
     public Dispatcher getDispatcher() {
         return dispatcher;
     }
 
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     protected void setSuperClass(RubyClass superClass) {
         this.superClass = superClass;
     }
 
     public RubyModule getParent() {
         return parent;
     }
 
     public void setParent(RubyModule parent) {
         this.parent = parent;
     }
 
     public Map getMethods() {
         return methods;
     }
     
     public void putMethod(Object name, DynamicMethod method) {
         // FIXME: kinda hacky...flush STI here
         dispatcher.clearIndex(MethodIndex.getIndex((String)name));
         getMethods().put(name, method);
     }
 
     /**
      * Is this module one that in an included one (e.g. an IncludedModuleWrapper). 
      */
     public boolean isIncluded() {
         return false;
     }
 
     public RubyModule getNonIncludedClass() {
         return this;
     }
 
     public String getBaseName() {
         return classId;
     }
 
     public void setBaseName(String name) {
         classId = name;
     }
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (getBaseName() == null) {
             if (isClass()) {
                 return "#<" + "Class" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             } else {
                 return "#<" + "Module" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             }
         }
 
         StringBuffer result = new StringBuffer(getBaseName());
         RubyClass objectClass = getRuntime().getObject();
 
         for (RubyModule p = this.getParent(); p != null && p != objectClass; p = p.getParent()) {
             String pName = p.getBaseName();
             // This is needed when the enclosing class or module is a singleton.
             // In that case, we generated a name such as null::Foo, which broke 
             // Marshalling, among others. The correct thing to do in this situation 
             // is to insert the generate the name of form #<Class:01xasdfasd> if 
             // it's a singleton module/class, which this code accomplishes.
             if(pName == null) {
                 pName = p.getName();
             }
             result.insert(0, "::").insert(0, pName);
         }
 
         return result.toString();
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
 
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
 
         return includedModule;
     }
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module;
         if ((module = getConstantAt(name)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     public RubyClass fastGetClass(String internedName) {
         IRubyObject module;
         if ((module = fastGetConstantAt(internedName)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
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
 
         doIncludeModule(module);
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
     
     public void defineAnnotatedMethod(Class clazz, String name) {
         // FIXME: This is probably not very efficient, since it loads all methods for each call
         boolean foundMethod = false;
         for (Method method : clazz.getDeclaredMethods()) {
             if (method.getName().equals(name) && defineAnnotatedMethod(method, MethodFactory.createFactory(getRuntime().getJRubyClassLoader()))) {
                 foundMethod = true;
             }
         }
 
         if (!foundMethod) {
             throw new RuntimeException("No JRubyMethod present for method " + name + "on class " + clazz.getName());
         }
     }
     
-    private static final boolean indexedMethods = Boolean.getBoolean("jruby.indexed.methods");
+    private static final boolean indexedMethods
+            = SafePropertyAccessor.getBoolean("jruby.indexed.methods");
     
     public void defineAnnotatedMethods(Class clazz) {
         if (indexedMethods) {
             defineAnnotatedMethodsIndexed(clazz);
         } else {
             defineAnnotatedMethodsIndividually(clazz);
         }
     }
     
     public void defineAnnotatedMethodsIndividually(Class clazz) {
         Method[] declaredMethods = clazz.getDeclaredMethods();
         MethodFactory methodFactory = MethodFactory.createFactory(getRuntime().getJRubyClassLoader());
         for (Method method: declaredMethods) {
             defineAnnotatedMethod(method, methodFactory);
         }
     }
     
     private void defineAnnotatedMethodsIndexed(Class clazz) {
         MethodFactory methodFactory = MethodFactory.createFactory(getRuntime().getJRubyClassLoader());
         methodFactory.defineIndexedAnnotatedMethods(this, clazz, methodDefiningCallback);
     }
     
     private static MethodFactory.MethodDefiningCallback methodDefiningCallback = new MethodFactory.MethodDefiningCallback() {
         public void define(RubyModule module, Method method, DynamicMethod dynamicMethod) {
             JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
             if (jrubyMethod.frame()) {
                 for (String name : jrubyMethod.name()) {
                     ASTInspector.FRAME_AWARE_METHODS.add(name);
                 }
             }
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     module.getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
                 RubyModule metaClass = module.metaClass;
 
                 if (jrubyMethod.meta()) {
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = method.getName();
                         metaClass.addMethod(baseName, dynamicMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             metaClass.addMethod(name, dynamicMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             metaClass.defineAlias(alias, baseName);
                         }
                     }
                 } else {
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = method.getName();
                         module.addMethod(method.getName(), dynamicMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             module.addMethod(name, dynamicMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             module.defineAlias(alias, baseName);
                         }
                     }
 
                     if (jrubyMethod.module()) {
                         // module/singleton methods are all defined public
                         DynamicMethod moduleMethod = dynamicMethod.dup();
                         moduleMethod.setVisibility(Visibility.PUBLIC);
 
                         RubyModule singletonClass = module.getSingletonClass();
 
                         if (jrubyMethod.name().length == 0) {
                             baseName = method.getName();
                             singletonClass.addMethod(method.getName(), moduleMethod);
                         } else {
                             baseName = jrubyMethod.name()[0];
                             for (String name : jrubyMethod.name()) {
                                 singletonClass.addMethod(name, moduleMethod);
                             }
                         }
 
                         if (jrubyMethod.alias().length > 0) {
                             for (String alias : jrubyMethod.alias()) {
                                 singletonClass.defineAlias(alias, baseName);
                             }
                         }
                     }
                 }
             }
         }
     };
     
     public boolean defineAnnotatedMethod(Method method, MethodFactory methodFactory) {
         JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, method);
             methodDefiningCallback.define(this, method, dynamicMethod);
             
             return true;
         }
         return false;
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
                 IRubyObject obj = ((MetaClass)c).getAttached();
 
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
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(runtime.getCurrentContext(), "singleton_method_undefined", getRuntime().newSymbol(name));
         } else {
             callMethod(runtime.getCurrentContext(), "method_undefined", getRuntime().newSymbol(name));
         }
     }
     
     @JRubyMethod(name = "include?", required = 1)
     public IRubyObject include_p(IRubyObject arg) {
         if (!arg.isModule()) {
             throw getRuntime().newTypeError(arg, getRuntime().getModule());
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
             DynamicMethod existingMethod = (DynamicMethod) getMethods().remove(name);
             if (existingMethod != null) {
                 getRuntime().getCacheMap().remove(name, existingMethod);
             }
             getRuntime().getMethodCache().removeMethod(name);
             putMethod(name, method);
         }
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
 
             getRuntime().getCacheMap().remove(name, method);
         }
         
         if(isSingleton()){
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(getRuntime().getCurrentContext(), "singleton_method_removed", getRuntime().newSymbol(name));
         }else{
             callMethod(getRuntime().getCurrentContext(), "method_removed", getRuntime().newSymbol(name));
     }
     }
 
     private DynamicMethod searchMethodWithoutCache(String name) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // included modules use delegates methods for we need to synchronize on result of getMethods
             synchronized(searchModule.getMethods()) {
                 // See if current class has method or if it has been cached here already
                 DynamicMethod method = (DynamicMethod) searchModule.getMethods().get(name);
                 
                 if (method != null) {
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
     public DynamicMethod searchMethod(String name) {
         MethodCache cache = getRuntime().getMethodCache();
         MethodCache.CacheEntry entry = cache.getMethod(this, name);
         if (entry.klass.get() == this && name.equals(entry.methodName)) {
             return entry.method.get();
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
         DynamicMethod oldMethod = searchMethodWithoutCache(name);
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
         getRuntime().getCacheMap().remove(name, method);
         getRuntime().getCacheMap().remove(name, oldMethod);
         getRuntime().getCacheMap().remove(name, oldMethod.getRealMethod());
         
         putMethod(name, new AliasMethod(this, method, oldName));
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
 
         Ruby runtime = getRuntime();
         IRubyObject classObj = getConstantAt(name);
         RubyClass clazz;
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw runtime.newTypeError(name + " is not a class");
             clazz = (RubyClass)classObj;
 
             if (superClazz != null) {
                 RubyClass tmp = clazz.getSuperClass();
                 while (tmp != null && tmp.isIncluded()) tmp = tmp.getSuperClass(); // need to skip IncludedModuleWrappers
                 if (tmp != null) tmp = tmp.getRealClass();
                 if (tmp != superClazz) throw runtime.newTypeError("superclass mismatch for class " + name);
                 // superClazz = null;
             }
 
             if (runtime.getSafeLevel() >= 4) throw runtime.newTypeError("extending class prohibited");
         } else if (classProviders != null && (clazz = searchProvidersForClass(name, superClazz)) != null) {
             // reopen a java class
         } else {
             if (superClazz == null) superClazz = runtime.getObject();
             clazz = RubyClass.newClass(runtime, superClazz, name, superClazz.getAllocator(), this, true);
         }
 
         return clazz;
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyModule defineOrGetModuleUnder(String name) {
         // This method is intended only for defining new modules in Ruby code
         Ruby runtime = getRuntime();
         IRubyObject moduleObj = getConstantAt(name);
         RubyModule module;
         if (moduleObj != null) {
             if (!moduleObj.isModule()) throw runtime.newTypeError(name + " is not a module");
             if (runtime.getSafeLevel() >= 4) throw runtime.newSecurityError("extending module prohibited");
             module = (RubyModule)moduleObj;
         } else if (classProviders != null && (module = searchProvidersForModule(name)) != null) {
             // reopen a java module
         } else {
             module = RubyModule.newModule(runtime, name, this, true); 
         }
         return module;
     }
 
     /** rb_define_class_under
      *  this method should be used only as an API to define/open nested classes 
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator) {
         return getRuntime().defineClassUnder(name, superClass, allocator, this);
     }
 
     /** rb_define_module_under
      *  this method should be used only as an API to define/open nested module
      */
     public RubyModule defineModuleUnder(String name) {
         return getRuntime().defineModuleUnder(name, this);
     }
 
     // FIXME: create AttrReaderMethod, AttrWriterMethod, for faster attr access
     private void addAccessor(String internedName, boolean readable, boolean writeable) {
         assert internedName == internedName.intern() : internedName + " is not interned";
 
         final Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = context.getCurrentVisibility();
         if (attributeScope == Visibility.PRIVATE) {
             //FIXME warning
         } else if (attributeScope == Visibility.MODULE_FUNCTION) {
             attributeScope = Visibility.PRIVATE;
             // FIXME warning
         }
         final String variableName = ("@" + internedName).intern();
         if (readable) {
             // FIXME: should visibility be set to current visibility?
             addMethod(internedName, new JavaMethod(this, Visibility.PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) Arity.raiseArgumentError(runtime, args.length, 0, 0);
 
                     IRubyObject variable = self.getInstanceVariables().fastGetInstanceVariable(variableName);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
         if (writeable) {
             internedName = (internedName + "=").intern();
             // FIXME: should visibility be set to current visibility?
             addMethod(internedName, new JavaMethod(this, Visibility.PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     // ENEBO: Can anyone get args to be anything but length 1?
                     if (args.length != 1) Arity.raiseArgumentError(runtime, args.length, 1, 1);
 
                     return self.getInstanceVariables().fastSetInstanceVariable(variableName, args[0]);
                 }
 
                 public Arity getArity() {
                     return Arity.singleArgument();
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
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
             exportMethod(methods[i].asJavaString(), visibility);
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
                 // FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
                 addMethod(name, new WrapperMethod(this, method, visibility));
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
             return !(checkVisibility && method.getVisibility() == Visibility.PRIVATE);
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
     @JRubyMethod(name = "define_method", required = 1, optional = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject define_method(IRubyObject[] args, Block block) {
         if (args.length < 1 || args.length > 2) {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         IRubyObject body;
         String name = args[0].asJavaString().intern();
         DynamicMethod newMethod = null;
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility visibility = tc.getCurrentVisibility();
 
         if (visibility == Visibility.MODULE_FUNCTION) visibility = Visibility.PRIVATE;
         if (args.length == 1) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = getRuntime().newProc(Block.Type.LAMBDA, block);
             body = proc;
 
             // a normal block passed to define_method changes to do arity checking; make it a lambda
             proc.getBlock().type = Block.Type.LAMBDA;
             
             proc.getBlock().getBinding().getFrame().setKlazz(this);
             proc.getBlock().getBinding().getFrame().setName(name);
             
             // for zsupers in define_method (blech!) we tell the proc scope to act as the "argument" scope
             proc.getBlock().getBody().getStaticScope().setArgumentScope(true);
             // just using required is broken...but no more broken than before zsuper refactoring
             proc.getBlock().getBody().getStaticScope().setRequiredArgs(proc.getBlock().arity().required());
 
             newMethod = new ProcMethod(this, proc, visibility);
         } else if (args.length == 2) {
             if (getRuntime().getProc().isInstance(args[1])) {
                 // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
                 RubyProc proc = (RubyProc)args[1];
                 body = proc;
                 
                 proc.getBlock().getBinding().getFrame().setKlazz(this);
                 proc.getBlock().getBinding().getFrame().setName(name);
 
                 // for zsupers in define_method (blech!) we tell the proc scope to act as the "argument" scope
                 proc.getBlock().getBody().getStaticScope().setArgumentScope(true);
                 // just using required is broken...but no more broken than before zsuper refactoring
                 proc.getBlock().getBody().getStaticScope().setRequiredArgs(proc.getBlock().arity().required());
 
                 newMethod = new ProcMethod(this, proc, visibility);
             } else if (getRuntime().getMethod().isInstance(args[1])) {
                 RubyMethod method = (RubyMethod)args[1];
                 body = method;
 
                 newMethod = new MethodMethod(this, method.unbind(null), visibility);
             } else {
                 throw getRuntime().newTypeError("wrong argument type " + args[1].getType().getName() + " (expected Proc/Method)");
             }
         } else {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         addMethod(name, newMethod);
 
         RubySymbol symbol = getRuntime().fastNewSymbol(name);
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (tc.getPreviousVisibility() == Visibility.MODULE_FUNCTION) {
             getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), newMethod, Visibility.PUBLIC));
         }
 
         if(isSingleton()){
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
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
 
     @JRubyMethod(name = "name")
     public RubyString name() {
         return getRuntime().newString(getBaseName() == null ? "" : getName());
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
 
     /** rb_mod_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1)
     public IRubyObject initialize_copy(IRubyObject original) {
         super.initialize_copy(original);
 
         RubyModule originalModule = (RubyModule)original;
 
         if (!getMetaClass().isSingleton()) setMetaClass(originalModule.getSingletonClassClone());
         setSuperClass(originalModule.getSuperClass());
 
         if (originalModule.hasVariables()){
             syncVariables(originalModule.getVariableList());
         }
 
         originalModule.cloneMethods(this);
 
         return this;
     }
 
     /** rb_mod_included_modules
      *
      */
     @JRubyMethod(name = "included_modules")
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
     @JRubyMethod(name = "ancestors")
     public RubyArray ancestors() {
         RubyArray ary = getRuntime().newArray(getAncestorList());
 
         return ary;
     }
 
     public List<IRubyObject> getAncestorList() {
         ArrayList<IRubyObject> list = new ArrayList<IRubyObject>();
 
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
 
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         return getRuntime().newFixnum(id);
     }
 
     /** rb_mod_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
         if(isSingleton()){            
             IRubyObject attached = ((MetaClass)this).getAttached();
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
     @JRubyMethod(name = "===", required = 1)
     public RubyBoolean op_eqq(IRubyObject obj) {
         return getRuntime().newBoolean(isInstance(obj));
     }
 
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(IRubyObject other) {
         return super.op_equal(other);
     }
 
     /** rb_mod_freeze
      *
      */
     @JRubyMethod(name = "freeze")
     public IRubyObject freeze() {
         to_s();
         return super.freeze();
     }
 
     /** rb_mod_le
     *
     */
     @JRubyMethod(name = "<=", required = 1)
    public IRubyObject op_le(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         if (isKindOfModule((RubyModule) obj)) {
             return getRuntime().getTrue();
         } else if (((RubyModule) obj).isKindOfModule(this)) {
             return getRuntime().getFalse();
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_lt
     *
     */
     @JRubyMethod(name = "<", required = 1)
    public IRubyObject op_lt(IRubyObject obj) {
         return obj == this ? getRuntime().getFalse() : op_le(obj);
     }
 
     /** rb_mod_ge
     *
     */
     @JRubyMethod(name = ">=", required = 1)
    public IRubyObject op_ge(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         return ((RubyModule) obj).op_le(this);
     }
 
     /** rb_mod_gt
     *
     */
     @JRubyMethod(name = ">", required = 1)
    public IRubyObject op_gt(IRubyObject obj) {
         return this == obj ? getRuntime().getFalse() : op_ge(obj);
     }
 
     /** rb_mod_cmp
     *
     */
     @JRubyMethod(name = "<=>", required = 1)
    public IRubyObject op_cmp(IRubyObject obj) {
         if (this == obj) {
             return getRuntime().newFixnum(0);
         }
 
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("<=> requires Class or Module (" + getMetaClass().getName() + " given)");
         }
 
         RubyModule module = (RubyModule) obj;
 
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
 
     protected boolean isSame(RubyModule module) {
         return this == module;
     }
 
     /** rb_mod_initialize
      *
      */
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(Block block) {
         if (block.isGiven()) {
             // class and module bodies default to public, so make the block's visibility public. JRUBY-1185.
             block.getBinding().setVisibility(Visibility.PUBLIC);
             block.yield(getRuntime().getCurrentContext(), null, this, this, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr
      *
      */
     @JRubyMethod(name = "attr", required = 1, optional = 1, visibility = Visibility.PRIVATE)
     public IRubyObject attr(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         addAccessor(args[0].asJavaString().intern(), true, writeable);
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_reader
      *
      */
     @JRubyMethod(name = "attr_reader", rest = true, visibility = Visibility.PRIVATE)
     public IRubyObject attr_reader(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asJavaString().intern(), true, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     @JRubyMethod(name = "attr_writer", rest = true, visibility = Visibility.PRIVATE)
     public IRubyObject attr_writer(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asJavaString().intern(), false, true);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_accessor
      *
      */
     @JRubyMethod(name = "attr_accessor", rest = true, visibility = Visibility.PRIVATE)
     public IRubyObject attr_accessor(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             // This is almost always already interned, since it will be called with a symbol in most cases
             // but when created from Java code, we might get an argument that needs to be interned.
             // addAccessor has as a precondition that the string MUST be interned
             addAccessor(args[i].asJavaString().intern(), true, true);
         }
 
         return getRuntime().getNil();
     }
 
     /**
      * Get a list of all instance methods names of the provided visibility unless not is true, then 
      * get all methods which are not the provided visibility.
      * 
      * @param args passed into one of the Ruby instance_method methods
      * @param visibility to find matching instance methods against
      * @param not if true only find methods not matching supplied visibility
      * @return a RubyArray of instance method names
      */
     private RubyArray instance_methods(IRubyObject[] args, final Visibility visibility, boolean not) {
         boolean includeSuper = args.length > 0 ? args[0].isTrue() : true;
         RubyArray ary = getRuntime().newArray();
         Set<String> seen = new HashSet<String>();
 
         for (RubyModule type = this; type != null; type = type.getSuperClass()) {
             RubyModule realType = type.getNonIncludedClass();
             for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext();) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
                 String methodName = (String) entry.getKey();
 
                 if (! seen.contains(methodName)) {
                     seen.add(methodName);
                     
                     if (method.getImplementationClass() == realType &&
                         (!not && method.getVisibility() == visibility || (not && method.getVisibility() != visibility)) &&
                         ! method.isUndefined()) {
 
                         ary.append(getRuntime().newString(methodName));
                     }
diff --git a/src/org/jruby/RubyThread.java b/src/org/jruby/RubyThread.java
index 5628db2522..fb49b17eef 100644
--- a/src/org/jruby/RubyThread.java
+++ b/src/org/jruby/RubyThread.java
@@ -1,730 +1,731 @@
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
  * Copyright (C) 2002 Jason Voegele <jason@jvoegele.com>
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
 
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.internal.runtime.FutureThread;
 import org.jruby.internal.runtime.NativeThread;
 import org.jruby.internal.runtime.RubyNativeThread;
 import org.jruby.internal.runtime.RubyRunnable;
 import org.jruby.internal.runtime.ThreadLike;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.TimeoutException;
 import java.util.concurrent.locks.ReentrantLock;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.Visibility;
+import org.jruby.util.SafePropertyAccessor;
 
 /**
  * Implementation of Ruby's <code>Thread</code> class.  Each Ruby thread is
  * mapped to an underlying Java Virtual Machine thread.
  * <p>
  * Thread encapsulates the behavior of a thread of execution, including the main
  * thread of the Ruby script.  In the descriptions that follow, the parameter
  * <code>aSymbol</code> refers to a symbol, which is either a quoted string or a
  * <code>Symbol</code> (such as <code>:name</code>).
  * 
  * Note: For CVS history, see ThreadClass.java.
  */
 public class RubyThread extends RubyObject {
     private ThreadLike threadImpl;
     private RubyFixnum priority;
     private Map threadLocalVariables = new HashMap();
     private boolean abortOnException;
     private IRubyObject finalResult;
     private RaiseException exitingException;
     private IRubyObject receivedException;
     private RubyThreadGroup threadGroup;
 
     private ThreadService threadService;
     private volatile boolean isStopped = false;
     public Object stopLock = new Object();
     
     private volatile boolean killed = false;
     public Object killLock = new Object();
     
     public final ReentrantLock lock = new ReentrantLock();
     
     private static boolean USE_POOLING;
     
     private static final boolean DEBUG = false;
     
    static {
        if (Ruby.isSecurityRestricted()) USE_POOLING = false;
-       else USE_POOLING = Boolean.getBoolean("jruby.thread.pooling");
+       else USE_POOLING = SafePropertyAccessor.getBoolean("jruby.thread.pooling");
    }
    
     public static RubyClass createThreadClass(Ruby runtime) {
         // FIXME: In order for Thread to play well with the standard 'new' behavior,
         // it must provide an allocator that can create empty object instances which
         // initialize then fills with appropriate data.
         RubyClass threadClass = runtime.defineClass("Thread", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setThread(threadClass);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyThread.class);
 
         threadClass.defineAnnotatedMethods(RubyThread.class);
 
         RubyThread rubyThread = new RubyThread(runtime, threadClass);
         // TODO: need to isolate the "current" thread from class creation
         rubyThread.threadImpl = new NativeThread(rubyThread, Thread.currentThread());
         runtime.getThreadService().setMainThread(rubyThread);
         
         threadClass.setMarshal(ObjectMarshal.NOT_MARSHALABLE_MARSHAL);
         
         return threadClass;
     }
 
     /**
      * <code>Thread.new</code>
      * <p>
      * Thread.new( <i>[ arg ]*</i> ) {| args | block } -> aThread
      * <p>
      * Creates a new thread to execute the instructions given in block, and
      * begins running it. Any arguments passed to Thread.new are passed into the
      * block.
      * <pre>
      * x = Thread.new { sleep .1; print "x"; print "y"; print "z" }
      * a = Thread.new { print "a"; print "b"; sleep .2; print "c" }
      * x.join # Let the threads finish before
      * a.join # main thread exits...
      * </pre>
      * <i>produces:</i> abxyzc
      */
     @JRubyMethod(name = {"new", "fork"}, rest = true, frame = true, meta = true)
     public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         return startThread(recv, args, true, block);
     }
 
     /**
      * Basically the same as Thread.new . However, if class Thread is
      * subclassed, then calling start in that subclass will not invoke the
      * subclass's initialize method.
      */
     @JRubyMethod(name = "start", rest = true, frame = true, meta = true)
     public static RubyThread start(IRubyObject recv, IRubyObject[] args, Block block) {
         return startThread(recv, args, false, block);
     }
     
     public static RubyThread adopt(IRubyObject recv, Thread t) {
         return adoptThread(recv, t, Block.NULL_BLOCK);
     }
 
     private static RubyThread adoptThread(final IRubyObject recv, Thread t, Block block) {
         final Ruby runtime = recv.getRuntime();
         final RubyThread rubyThread = new RubyThread(runtime, (RubyClass) recv);
         
         rubyThread.threadImpl = new NativeThread(rubyThread, t);
         ThreadContext context = runtime.getThreadService().registerNewThread(rubyThread);
         
         context.preAdoptThread();
 
         // FIXME: this block doesn't make much sense and should be removed?
         // adoptThread does not call init, since init expects a block to be passed in
         if (USE_POOLING) {
             rubyThread.threadImpl = new FutureThread(rubyThread, new RubyRunnable(rubyThread, IRubyObject.NULL_ARRAY, block));
         } else {
             rubyThread.threadImpl = new NativeThread(rubyThread, new RubyNativeThread(rubyThread, IRubyObject.NULL_ARRAY, block));
         }
         rubyThread.threadImpl.start();
         
         return rubyThread;
     }
     
     @JRubyMethod(name = "initialize", rest = true, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (!block.isGiven()) throw getRuntime().newThreadError("must be called with a block");
 
         if (USE_POOLING) {
             threadImpl = new FutureThread(this, new RubyRunnable(this, args, block));
         } else {
             threadImpl = new NativeThread(this, new RubyNativeThread(this, args, block));
         }
         threadImpl.start();
         
         return this;
     }
     
     private static RubyThread startThread(final IRubyObject recv, final IRubyObject[] args, boolean callInit, Block block) {
         RubyThread rubyThread = new RubyThread(recv.getRuntime(), (RubyClass) recv);
         
         if (callInit) {
             rubyThread.callInit(args, block);
         } else {
             // for Thread::start, which does not call the subclass's initialize
             rubyThread.initialize(args, block);
         }
         
         return rubyThread;
     }
     
     private void ensureCurrent() {
         if (this != getRuntime().getCurrentContext().getThread()) {
             throw new RuntimeException("internal thread method called from another thread");
         }
     }
     
     private void ensureNotCurrent() {
         if (this == getRuntime().getCurrentContext().getThread()) {
             throw new RuntimeException("internal thread method called from another thread");
         }
     }
     
     public void cleanTerminate(IRubyObject result) {
         finalResult = result;
         isStopped = true;
     }
 
     public void pollThreadEvents() {
         // check for criticalization *before* locking ourselves
         threadService.waitForCritical();
 
         ensureCurrent();
 
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before");
         if (killed) throw new ThreadKill();
 
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " after");
         if (receivedException != null) {
             // clear this so we don't keep re-throwing
             IRubyObject raiseException = receivedException;
             receivedException = null;
             RubyModule kernelModule = getRuntime().getKernel();
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before propagating exception: " + killed);
             kernelModule.callMethod(getRuntime().getCurrentContext(), "raise", raiseException);
         }
     }
 
     protected RubyThread(Ruby runtime, RubyClass type) {
         super(runtime, type);
         this.threadService = runtime.getThreadService();
         // set to default thread group
         RubyThreadGroup defaultThreadGroup = (RubyThreadGroup)runtime.getThreadGroup().fastGetConstant("Default");
         defaultThreadGroup.add(this, Block.NULL_BLOCK);
         finalResult = runtime.getNil();
     }
 
     /**
      * Returns the status of the global ``abort on exception'' condition. The
      * default is false. When set to true, will cause all threads to abort (the
      * process will exit(0)) if an exception is raised in any thread. See also
      * Thread.abort_on_exception= .
      */
     @JRubyMethod(name = "abort_on_exception", meta = true)
     public static RubyBoolean abort_on_exception_x(IRubyObject recv) {
     	Ruby runtime = recv.getRuntime();
         return runtime.isGlobalAbortOnExceptionEnabled() ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "abort_on_exception=", required = 1, meta = true)
     public static IRubyObject abort_on_exception_set_x(IRubyObject recv, IRubyObject value) {
         recv.getRuntime().setGlobalAbortOnExceptionEnabled(value.isTrue());
         return value;
     }
 
     @JRubyMethod(name = "current", meta = true)
     public static RubyThread current(IRubyObject recv) {
         return recv.getRuntime().getCurrentContext().getThread();
     }
 
     @JRubyMethod(name = "main", meta = true)
     public static RubyThread main(IRubyObject recv) {
         return recv.getRuntime().getThreadService().getMainThread();
     }
 
     @JRubyMethod(name = "pass", meta = true)
     public static IRubyObject pass(IRubyObject recv) {
         Ruby runtime = recv.getRuntime();
         ThreadService ts = runtime.getThreadService();
         boolean critical = ts.getCritical();
         
         ts.setCritical(false);
         
         Thread.yield();
         
         ts.setCritical(critical);
         
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "list", meta = true)
     public static RubyArray list(IRubyObject recv) {
     	RubyThread[] activeThreads = recv.getRuntime().getThreadService().getActiveRubyThreads();
         
         return recv.getRuntime().newArrayNoCopy(activeThreads);
     }
     
     private IRubyObject getSymbolKey(IRubyObject originalKey) {
         if (originalKey instanceof RubySymbol) {
             return originalKey;
         } else if (originalKey instanceof RubyString) {
             return getRuntime().fastNewSymbol(originalKey.asJavaString().intern());
         } else if (originalKey instanceof RubyFixnum) {
             getRuntime().getWarnings().warn("Do not use Fixnums as Symbols");
             throw getRuntime().newArgumentError(originalKey + " is not a symbol");
         } else {
             throw getRuntime().newTypeError(originalKey + " is not a symbol");
         }
     }
 
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject op_aref(IRubyObject key) {
         key = getSymbolKey(key);
         
         if (!threadLocalVariables.containsKey(key)) {
             return getRuntime().getNil();
         }
         return (IRubyObject) threadLocalVariables.get(key);
     }
 
     @JRubyMethod(name = "[]=", required = 2)
     public IRubyObject op_aset(IRubyObject key, IRubyObject value) {
         key = getSymbolKey(key);
         
         threadLocalVariables.put(key, value);
         return value;
     }
 
     @JRubyMethod(name = "abort_on_exception")
     public RubyBoolean abort_on_exception() {
         return abortOnException ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "abort_on_exception=", required = 1)
     public IRubyObject abort_on_exception_set(IRubyObject val) {
         abortOnException = val.isTrue();
         return val;
     }
 
     @JRubyMethod(name = "alive?")
     public RubyBoolean alive_p() {
         return threadImpl.isAlive() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "join", optional = 1)
     public IRubyObject join(IRubyObject[] args) {
         long timeoutMillis = 0;
         if (args.length > 0) {
             if (args.length > 1) {
                 throw getRuntime().newArgumentError(args.length,1);
             }
             // MRI behavior: value given in seconds; converted to Float; less
             // than or equal to zero returns immediately; returns nil
             timeoutMillis = (long)(1000.0D * args[0].convertToFloat().getValue());
             if (timeoutMillis <= 0) {
 	        // TODO: not sure that we should skip caling join() altogether.
 		// Thread.join() has some implications for Java Memory Model, etc.
 	        if (threadImpl.isAlive()) {
 		   return getRuntime().getNil();
 		} else {   
                    return this;
 		}
             }
         }
         if (isCurrent()) {
             throw getRuntime().newThreadError("thread tried to join itself");
         }
         try {
             if (threadService.getCritical()) {
                 threadImpl.interrupt(); // break target thread out of critical
             }
             threadImpl.join(timeoutMillis);
         } catch (InterruptedException ie) {
             ie.printStackTrace();
             assert false : ie;
         } catch (TimeoutException ie) {
             ie.printStackTrace();
             assert false : ie;
         } catch (ExecutionException ie) {
             ie.printStackTrace();
             assert false : ie;
         }
 
         if (exitingException != null) {
             throw exitingException;
         }
 
         if (threadImpl.isAlive()) {
             return getRuntime().getNil();
         } else {
             return this;
 	}
     }
 
     @JRubyMethod(name = "value")
     public IRubyObject value() {
         join(new IRubyObject[0]);
         synchronized (this) {
             return finalResult;
         }
     }
 
     @JRubyMethod(name = "group")
     public IRubyObject group() {
         if (threadGroup == null) {
         	return getRuntime().getNil();
         }
         
         return threadGroup;
     }
     
     void setThreadGroup(RubyThreadGroup rubyThreadGroup) {
     	threadGroup = rubyThreadGroup;
     }
     
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect() {
         // FIXME: There's some code duplication here with RubyObject#inspect
         StringBuffer part = new StringBuffer();
         String cname = getMetaClass().getRealClass().getName();
         part.append("#<").append(cname).append(":0x");
         part.append(Integer.toHexString(System.identityHashCode(this)));
         
         if (threadImpl.isAlive()) {
             if (isStopped) {
                 part.append(getRuntime().newString(" sleep"));
             } else if (killed) {
                 part.append(getRuntime().newString(" aborting"));
             } else {
                 part.append(getRuntime().newString(" run"));
             }
         } else {
             part.append(" dead");
         }
         
         part.append(">");
         return getRuntime().newString(part.toString());
     }
 
     @JRubyMethod(name = "key?", required = 1)
     public RubyBoolean key_p(IRubyObject key) {
         key = getSymbolKey(key);
         
         return getRuntime().newBoolean(threadLocalVariables.containsKey(key));
     }
 
     @JRubyMethod(name = "keys")
     public RubyArray keys() {
         IRubyObject[] keys = new IRubyObject[threadLocalVariables.size()];
         
         return RubyArray.newArrayNoCopy(getRuntime(), (IRubyObject[])threadLocalVariables.keySet().toArray(keys));
     }
     
     @JRubyMethod(name = "critical=", required = 1, meta = true)
     public static IRubyObject critical_set(IRubyObject receiver, IRubyObject value) {
     	receiver.getRuntime().getThreadService().setCritical(value.isTrue());
     	
     	return value;
     }
 
     @JRubyMethod(name = "critical", meta = true)
     public static IRubyObject critical(IRubyObject receiver) {
     	return receiver.getRuntime().newBoolean(receiver.getRuntime().getThreadService().getCritical());
     }
     
     @JRubyMethod(name = "stop", meta = true)
     public static IRubyObject stop(IRubyObject receiver) {
         RubyThread rubyThread = receiver.getRuntime().getThreadService().getCurrentContext().getThread();
         Object stopLock = rubyThread.stopLock;
         
         synchronized (stopLock) {
             try {
                 rubyThread.isStopped = true;
                 // attempt to decriticalize all if we're the critical thread
                 receiver.getRuntime().getThreadService().setCritical(false);
                 
                 stopLock.wait();
             } catch (InterruptedException ie) {
                 // ignore, continue;
             }
             rubyThread.isStopped = false;
         }
         
         return receiver.getRuntime().getNil();
     }
     
     @JRubyMethod(name = "kill", required = 1, frame = true, meta = true)
     public static IRubyObject kill(IRubyObject receiver, IRubyObject rubyThread, Block block) {
         if (!(rubyThread instanceof RubyThread)) throw receiver.getRuntime().newTypeError(rubyThread, receiver.getRuntime().getThread());
         return ((RubyThread)rubyThread).kill();
     }
     
     @JRubyMethod(name = "exit", frame = true, meta = true)
     public static IRubyObject s_exit(IRubyObject receiver, Block block) {
         RubyThread rubyThread = receiver.getRuntime().getThreadService().getCurrentContext().getThread();
         
         rubyThread.killed = true;
         // attempt to decriticalize all if we're the critical thread
         receiver.getRuntime().getThreadService().setCritical(false);
         
         throw new ThreadKill();
     }
 
     @JRubyMethod(name = "stop?")
     public RubyBoolean stop_p() {
     	// not valid for "dead" state
     	return getRuntime().newBoolean(isStopped);
     }
     
     @JRubyMethod(name = "wakeup")
     public RubyThread wakeup() {
     	synchronized (stopLock) {
     		stopLock.notifyAll();
     	}
     	
     	return this;
     }
     
     @JRubyMethod(name = "priority")
     public RubyFixnum priority() {
         return priority;
     }
 
     @JRubyMethod(name = "priority=", required = 1)
     public IRubyObject priority_set(IRubyObject priority) {
         // FIXME: This should probably do some translation from Ruby priority levels to Java priority levels (until we have green threads)
         int iPriority = RubyNumeric.fix2int(priority);
         
         if (iPriority < Thread.MIN_PRIORITY) {
             iPriority = Thread.MIN_PRIORITY;
         } else if (iPriority > Thread.MAX_PRIORITY) {
             iPriority = Thread.MAX_PRIORITY;
         }
         
         this.priority = RubyFixnum.newFixnum(getRuntime(), iPriority);
         
         if (threadImpl.isAlive()) {
             threadImpl.setPriority(iPriority);
         }
         return this.priority;
     }
 
     @JRubyMethod(name = "raise", optional = 1, frame = true)
     public IRubyObject raise(IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         ensureNotCurrent();
         Ruby runtime = getRuntime();
         
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before raising");
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
         try {
             while (!(currentThread.lock.tryLock() && this.lock.tryLock())) {
                 if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             }
 
             currentThread.pollThreadEvents();
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " raising");
             receivedException = prepareRaiseException(runtime, args, block);
 
             // interrupt the target thread in case it's blocking or waiting
             threadImpl.interrupt();
         } finally {
             if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
         }
 
         return this;
     }
 
     private IRubyObject prepareRaiseException(Ruby runtime, IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 3); 
 
         if(args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if(lastException.isNil()) {
                 return new RaiseException(runtime, runtime.fastGetClass("RuntimeError"), "", false).getException();
             } 
             return lastException;
         }
 
         IRubyObject exception;
         ThreadContext context = getRuntime().getCurrentContext();
         
         if(args.length == 1) {
             if(args[0] instanceof RubyString) {
                 return runtime.fastGetClass("RuntimeError").newInstance(args, block);
             }
             
             if(!args[0].respondsTo("exception")) {
                 return runtime.newTypeError("exception class/object expected").getException();
             }
             exception = args[0].callMethod(context, "exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 return runtime.newTypeError("exception class/object expected").getException();
             }
             
             exception = args[0].callMethod(context, "exception", args[1]);
         }
         
         if (!runtime.getException().isInstance(exception)) {
             return runtime.newTypeError("exception object expected").getException();
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
         
         return exception;
     }
     
     @JRubyMethod(name = "run")
     public IRubyObject run() {
         // if stopped, unstop
         synchronized (stopLock) {
             if (isStopped) {
                 isStopped = false;
                 stopLock.notifyAll();
             }
         }
     	
     	return this;
     }
     
     public void sleep(long millis) throws InterruptedException {
         ensureCurrent();
         synchronized (stopLock) {
             try {
                 isStopped = true;
                 stopLock.wait(millis);
             } finally {
                 isStopped = false;
                 pollThreadEvents();
             }
         }
     }
 
     @JRubyMethod(name = "status")
     public IRubyObject status() {
         if (threadImpl.isAlive()) {
         	if (isStopped) {
             	return getRuntime().newString("sleep");
             } else if (killed) {
                 return getRuntime().newString("aborting");
             }
         	
             return getRuntime().newString("run");
         } else if (exitingException != null) {
             return getRuntime().getNil();
         } else {
             return getRuntime().newBoolean(false);
         }
     }
 
     @JRubyMethod(name = {"kill", "exit", "terminate"})
     public IRubyObject kill() {
     	// need to reexamine this
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
         
         try {
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " trying to kill");
             while (!(currentThread.lock.tryLock() && this.lock.tryLock())) {
                 if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             }
 
             currentThread.pollThreadEvents();
 
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " succeeded with kill");
             killed = true;
 
             threadImpl.interrupt(); // break out of wait states and blocking IO
         } finally {
             if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
         }
         
         try {
             threadImpl.join();
         } catch (InterruptedException ie) {
             // we were interrupted, check thread events again
             currentThread.pollThreadEvents();
         } catch (ExecutionException ie) {
             // we were interrupted, check thread events again
             currentThread.pollThreadEvents();
         }
         
         return this;
     }
     
     @JRubyMethod(name = {"kill!", "exit!", "terminate!"})
     public IRubyObject kill_bang() {
         throw getRuntime().newNotImplementedError("Thread#kill!, exit!, and terminate! are not safe and not supported");
     }
     
     @JRubyMethod(name = "safe_level")
     public IRubyObject safe_level() {
         throw getRuntime().newNotImplementedError("Thread-specific SAFE levels are not supported");
     }
 
     private boolean isCurrent() {
         return threadImpl.isCurrent();
     }
 
     public void exceptionRaised(RaiseException exception) {
         assert isCurrent();
 
         RubyException rubyException = exception.getException();
         Ruby runtime = rubyException.getRuntime();
         if (runtime.fastGetClass("SystemExit").isInstance(rubyException)) {
             threadService.getMainThread().raise(new IRubyObject[] {rubyException}, Block.NULL_BLOCK);
         } else if (abortOnException(runtime)) {
             // FIXME: printError explodes on some nullpointer
             //getRuntime().getRuntime().printError(exception.getException());
             RubyException systemExit = RubySystemExit.newInstance(runtime, 1);
             systemExit.message = rubyException.message;
             threadService.getMainThread().raise(new IRubyObject[] {systemExit}, Block.NULL_BLOCK);
             return;
         } else if (runtime.getDebug().isTrue()) {
             runtime.printError(exception.getException());
         }
         exitingException = exception;
     }
 
     private boolean abortOnException(Ruby runtime) {
         return (runtime.isGlobalAbortOnExceptionEnabled() || abortOnException);
     }
 
     public static RubyThread mainThread(IRubyObject receiver) {
         return receiver.getRuntime().getThreadService().getMainThread();
     }
 }
diff --git a/src/org/jruby/compiler/ASTInspector.java b/src/org/jruby/compiler/ASTInspector.java
index d4cbd3e80a..881c4f0921 100644
--- a/src/org/jruby/compiler/ASTInspector.java
+++ b/src/org/jruby/compiler/ASTInspector.java
@@ -1,523 +1,524 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 
 import java.util.HashSet;
 import java.util.Set;
 import org.jruby.ast.AndNode;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.AssignableNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockAcceptingNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FlipNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.HashNode;
 import org.jruby.ast.IArgumentNode;
 import org.jruby.ast.IScopingNode;
 import org.jruby.ast.IfNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.Match2Node;
 import org.jruby.ast.Match3Node;
 import org.jruby.ast.MatchNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.OpAsgnAndNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.OrNode;
 import org.jruby.ast.PostExeNode;
 import org.jruby.ast.PreExeNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.ast.types.INameNode;
+import org.jruby.util.SafePropertyAccessor;
 
 /**
  *
  * @author headius
  */
 public class ASTInspector {
     private boolean hasClosure;
     private boolean hasClass;
     private boolean hasDef;
     private boolean hasScopeAwareMethods;
     private boolean hasFrameAwareMethods;
     private boolean hasBlockArg;
     private boolean hasOptArgs;
     private boolean hasRestArg;
     
     public static Set<String> FRAME_AWARE_METHODS = new HashSet<String>();
     private static Set<String> SCOPE_AWARE_METHODS = new HashSet<String>();
     
     static {
         FRAME_AWARE_METHODS.add("eval");
         FRAME_AWARE_METHODS.add("module_eval");
         FRAME_AWARE_METHODS.add("class_eval");
         FRAME_AWARE_METHODS.add("binding");
         FRAME_AWARE_METHODS.add("public");
         FRAME_AWARE_METHODS.add("private");
         FRAME_AWARE_METHODS.add("protected");
         FRAME_AWARE_METHODS.add("module_function");
         FRAME_AWARE_METHODS.add("block_given?");
         FRAME_AWARE_METHODS.add("iterator?");
         
         SCOPE_AWARE_METHODS.add("eval");
         SCOPE_AWARE_METHODS.add("module_eval");
         SCOPE_AWARE_METHODS.add("class_eval");
         SCOPE_AWARE_METHODS.add("binding");
         SCOPE_AWARE_METHODS.add("local_variables");
     }
     
     public void disable() {
         hasClosure = true;
         hasClass = true;
         hasDef = true;
         hasScopeAwareMethods = true;
         hasFrameAwareMethods = true;
         hasBlockArg = true;
         hasOptArgs = true;
         hasRestArg = true;
     }
     
-    public static final boolean ENABLED = System.getProperty("jruby.astInspector.enabled", "true").equals("true");
+    public static final boolean ENABLED = SafePropertyAccessor.getProperty("jruby.astInspector.enabled", "true").equals("true");
     
     public void inspect(Node node) {
         // TODO: This code effectively disables all inspection-based optimizations; none of them are 100% safe yet
         if (!ENABLED) disable();
 
         if (node == null) return;
         
         switch (node.nodeId) {
         case ALIASNODE:
             break;
         case ANDNODE:
             AndNode andNode = (AndNode)node;
             inspect(andNode.getFirstNode());
             inspect(andNode.getSecondNode());
             break;
         case ARGSCATNODE:
             ArgsCatNode argsCatNode = (ArgsCatNode)node;
             inspect(argsCatNode.getFirstNode());
             inspect(argsCatNode.getSecondNode());
             break;
         case ARGSPUSHNODE:
             ArgsPushNode argsPushNode = (ArgsPushNode)node;
             inspect(argsPushNode.getFirstNode());
             inspect(argsPushNode.getSecondNode());
             break;
         case ARGUMENTNODE:
             break;
         case ARRAYNODE:
         case BLOCKNODE:
         case DREGEXPNODE:
         case DSTRNODE:
         case DSYMBOLNODE:
         case DXSTRNODE:
         case LISTNODE:
             ListNode listNode = (ListNode)node;
             for (int i = 0; i < listNode.size(); i++) {
                 inspect(listNode.get(i));
             }
             break;
         case ARGSNODE:
             ArgsNode argsNode = (ArgsNode)node;
             if (argsNode.getBlockArgNode() != null) hasBlockArg = true;
             if (argsNode.getOptArgs() != null) {
                 hasOptArgs = true;
                 inspect(argsNode.getOptArgs());
             }
             if (argsNode.getRestArg() == -2 || argsNode.getRestArg() >= 0) hasRestArg = true;
             break;
         case ASSIGNABLENODE:
             AssignableNode assignableNode = (AssignableNode)node;
             inspect(assignableNode.getValueNode());
             break;
         case ATTRASSIGNNODE:
             AttrAssignNode attrAssignNode = (AttrAssignNode)node;
             inspect(attrAssignNode.getArgsNode());
             inspect(attrAssignNode.getReceiverNode());
             break;
         case BACKREFNODE:
             hasFrameAwareMethods = true;
             break;
         case BEGINNODE:
             inspect(((BeginNode)node).getBodyNode());
             break;
         case BIGNUMNODE:
             break;
         case BINARYOPERATORNODE:
             BinaryOperatorNode binaryOperatorNode = (BinaryOperatorNode)node;
             inspect(binaryOperatorNode.getFirstNode());
             inspect(binaryOperatorNode.getSecondNode());
             break;
         case BLOCKARGNODE:
             break;
         case BLOCKPASSNODE:
             BlockPassNode blockPassNode = (BlockPassNode)node;
             inspect(blockPassNode.getArgsNode());
             inspect(blockPassNode.getBodyNode());
             break;
         case BREAKNODE:
             inspect(((BreakNode)node).getValueNode());
             break;
         case CALLNODE:
             CallNode callNode = (CallNode)node;
             inspect(callNode.getReceiverNode());
         case FCALLNODE:
             inspect(((IArgumentNode)node).getArgsNode());
             inspect(((BlockAcceptingNode)node).getIterNode());
         case VCALLNODE:
             INameNode nameNode = (INameNode)node;
             if (FRAME_AWARE_METHODS.contains(nameNode.getName())) {
                 hasFrameAwareMethods = true;
             }
             if (SCOPE_AWARE_METHODS.contains(nameNode.getName())) {
                 hasScopeAwareMethods = true;
             }
             break;
         case CASENODE:
             CaseNode caseNode = (CaseNode)node;
             inspect(caseNode.getCaseNode());
             inspect(caseNode.getFirstWhenNode());
             break;
         case CLASSNODE:
             hasScopeAwareMethods = true;
             hasClass = true;
             break;
         case CLASSVARNODE:
             hasScopeAwareMethods = true;
             break;
         case GLOBALASGNNODE:
             GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode)node;
             if (globalAsgnNode.getName().equals("$_") || globalAsgnNode.getName().equals("$~")) {
                 hasScopeAwareMethods = true;
             }
             break;
         case CONSTDECLNODE:
         case CLASSVARASGNNODE:
         case CLASSVARDECLNODE:
             hasScopeAwareMethods = true;
         case DASGNNODE:
         case INSTASGNNODE:
         case LOCALASGNNODE:
             inspect(((AssignableNode)node).getValueNode());
             break;
         case COLON2NODE:
             inspect(((Colon2Node)node).getLeftNode());
             break;
         case COLON3NODE:
             break;
         case CONSTNODE:
             hasScopeAwareMethods = true;
             break;
         case DEFNNODE:
         case DEFSNODE:
             hasDef = true;
             hasScopeAwareMethods = true;
             break;
         case DEFINEDNODE:
             disable();
             break;
         case DOTNODE:
             DotNode dotNode = (DotNode)node;
             inspect(dotNode.getBeginNode());
             inspect(dotNode.getEndNode());
             break;
         case DVARNODE:
             break;
         case ENSURENODE:
             disable();
             break;
         case EVSTRNODE:
             inspect(((EvStrNode)node).getBody());
             break;
         case FALSENODE:
             break;
         case FIXNUMNODE:
             break;
         case FLIPNODE:
             inspect(((FlipNode)node).getBeginNode());
             inspect(((FlipNode)node).getEndNode());
             break;
         case FLOATNODE:
             break;
         case FORNODE:
             hasClosure = true;
             hasScopeAwareMethods = true;
             hasFrameAwareMethods = true;
             inspect(((ForNode)node).getIterNode());
             inspect(((ForNode)node).getBodyNode());
             inspect(((ForNode)node).getVarNode());
             break;
         case GLOBALVARNODE:
             break;
         case HASHNODE:
             HashNode hashNode = (HashNode)node;
             inspect(hashNode.getListNode());
             break;
         case IFNODE:
             IfNode ifNode = (IfNode)node;
             inspect(ifNode.getCondition());
             inspect(ifNode.getThenBody());
             inspect(ifNode.getElseBody());
             break;
         case INSTVARNODE:
             break;
         case ISCOPINGNODE:
             IScopingNode iscopingNode = (IScopingNode)node;
             inspect(iscopingNode.getCPath());
             break;
         case ITERNODE:
             hasClosure = true;
             hasFrameAwareMethods = true;
             break;
         case LOCALVARNODE:
             break;
         case MATCHNODE:
             inspect(((MatchNode)node).getRegexpNode());
             break;
         case MATCH2NODE:
             Match2Node match2Node = (Match2Node)node;
             inspect(match2Node.getReceiverNode());
             inspect(match2Node.getValueNode());
             break;
         case MATCH3NODE:
             Match3Node match3Node = (Match3Node)node;
             inspect(match3Node.getReceiverNode());
             inspect(match3Node.getValueNode());
             break;
         case MODULENODE:
             hasClass = true;
             hasScopeAwareMethods = true;
             break;
         case MULTIPLEASGNNODE:
             MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)node;
             inspect(multipleAsgnNode.getArgsNode());
             inspect(multipleAsgnNode.getHeadNode());
             inspect(multipleAsgnNode.getValueNode());
             break;
         case NEWLINENODE:
             inspect(((NewlineNode)node).getNextNode());
             break;
         case NEXTNODE:
             inspect(((NextNode)node).getValueNode());
             break;
         case NILNODE:
             break;
         case NOTNODE:
             inspect(((NotNode)node).getConditionNode());
             break;
         case NTHREFNODE:
             break;
         case OPASGNANDNODE:
             OpAsgnAndNode opAsgnAndNode = (OpAsgnAndNode)node;
             inspect(opAsgnAndNode.getFirstNode());
             inspect(opAsgnAndNode.getSecondNode());
             break;
         case OPASGNNODE:
             OpAsgnNode opAsgnNode = (OpAsgnNode)node;
             inspect(opAsgnNode.getReceiverNode());
             inspect(opAsgnNode.getValueNode());
             break;
         case OPASGNORNODE:
             disable(); // Depends on defined
             break;
         case OPELEMENTASGNNODE:
             OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode)node;
             inspect(opElementAsgnNode.getArgsNode());
             inspect(opElementAsgnNode.getReceiverNode());
             inspect(opElementAsgnNode.getValueNode());
             break;
         case ORNODE:
             OrNode orNode = (OrNode)node;
             inspect(orNode.getFirstNode());
             inspect(orNode.getSecondNode());
             break;
         case POSTEXENODE:
             PostExeNode postExeNode = (PostExeNode)node;
             hasClosure = true;
             hasFrameAwareMethods = true;
             hasScopeAwareMethods = true;
             inspect(postExeNode.getBodyNode());
             inspect(postExeNode.getVarNode());
             break;
         case PREEXENODE:
             PreExeNode preExeNode = (PreExeNode)node;
             hasClosure = true;
             hasFrameAwareMethods = true;
             hasScopeAwareMethods = true;
             inspect(preExeNode.getBodyNode());
             inspect(preExeNode.getVarNode());
             break;
         case REDONODE:
             break;
         case REGEXPNODE:
             break;
         case ROOTNODE:
             inspect(((RootNode)node).getBodyNode());
             break;
         case RESCUEBODYNODE:
             disable();
             break;
         case RESCUENODE:
             disable();
             break;
         case RETRYNODE:
             disable();
             break;
         case RETURNNODE:
             inspect(((ReturnNode)node).getValueNode());
             break;
         case SCLASSNODE:
             hasClass = true;
             hasScopeAwareMethods = true;
             break;
         case SCOPENODE:
             break;
         case SELFNODE:
             break;
         case SPLATNODE:
             inspect(((SplatNode)node).getValue());
             break;
         case STARNODE:
             break;
         case STRNODE:
             break;
         case SUPERNODE:
             SuperNode superNode = (SuperNode)node;
             inspect(superNode.getArgsNode());
             inspect(superNode.getIterNode());
             break;
         case SVALUENODE:
             inspect(((SValueNode)node).getValue());
             break;
         case SYMBOLNODE:
             break;
         case TOARYNODE:
             inspect(((ToAryNode)node).getValue());
             break;
         case TRUENODE:
             break;
         case UNDEFNODE:
             hasScopeAwareMethods = true;
             break;
         case UNTILNODE:
             UntilNode untilNode = (UntilNode)node;
             inspect(untilNode.getConditionNode());
             inspect(untilNode.getBodyNode());
             break;
         case VALIASNODE:
             break;
         case WHENNODE:
             inspect(((WhenNode)node).getBodyNode());
             inspect(((WhenNode)node).getExpressionNodes());
             inspect(((WhenNode)node).getNextCase());
             break;
         case WHILENODE:
             WhileNode whileNode = (WhileNode)node;
             inspect(whileNode.getConditionNode());
             inspect(whileNode.getBodyNode());
             break;
         case XSTRNODE:
             break;
         case YIELDNODE:
             inspect(((YieldNode)node).getArgsNode());
             break;
         case ZARRAYNODE:
             break;
         case ZEROARGNODE:
             break;
         case ZSUPERNODE:
             hasScopeAwareMethods = true;
             hasFrameAwareMethods = true;
             inspect(((ZSuperNode)node).getIterNode());
             break;
         default:
             // encountered a node we don't recognize, set everything to true to disable optz
             assert false : "All nodes should be accounted for in AST inspector: " + node;
             disable();
         }
     }
 
     public boolean hasClass() {
         return hasClass;
     }
 
     public boolean hasClosure() {
         return hasClosure;
     }
 
     public boolean hasDef() {
         return hasDef;
     }
 
     public boolean hasFrameAwareMethods() {
         return hasFrameAwareMethods;
     }
 
     public boolean hasScopeAwareMethods() {
         return hasScopeAwareMethods;
     }
 
     public boolean hasBlockArg() {
         return hasBlockArg;
     }
 
     public boolean hasOptArgs() {
         return hasOptArgs;
     }
 
     public boolean hasRestArg() {
         return hasRestArg;
     }
 }
diff --git a/src/org/jruby/ext/posix/POSIXFactory.java b/src/org/jruby/ext/posix/POSIXFactory.java
index 8fd90c4428..f1e649c02e 100644
--- a/src/org/jruby/ext/posix/POSIXFactory.java
+++ b/src/org/jruby/ext/posix/POSIXFactory.java
@@ -1,61 +1,63 @@
 package org.jruby.ext.posix;
 
 import java.util.HashMap;
 
+import org.jruby.util.SafePropertyAccessor;
+
 import com.sun.jna.Native;
 
 public class POSIXFactory {
     static LibC libc = null;
     
     public static POSIX getPOSIX(POSIXHandler handler, boolean useNativePOSIX) {
-        boolean thirtyTwoBit = "32".equals(System.getProperty("sun.arch.data.model")) == true;
+        boolean thirtyTwoBit = "32".equals(SafePropertyAccessor.getProperty("sun.arch.data.model", "32")) == true;
         
         // No 64 bit structures defined yet.
         if (useNativePOSIX && thirtyTwoBit) {
             try {
                 String os = System.getProperty("os.name");
                 if (os.startsWith("Mac OS")) {
                     return new MacOSPOSIX(loadMacOSLibC(), handler);
                 } else if (os.startsWith("Linux")) {
                     return new LinuxPOSIX(loadLinuxLibC(), handler);
                 } else if (os.startsWith("Windows")) {
                     return new WindowsPOSIX(loadWindowsLibC(), handler);
                 }
             } catch (Exception e) {
             } // Loading error...not much to be done with it?
         }
         
         return getJavaPOSIX(handler);
     }
     
     public static POSIX getJavaPOSIX(POSIXHandler handler) {
         return new JavaPOSIX(new JavaLibC(handler), handler);
     }
     
     public static LibC loadLinuxLibC() {
         if (libc != null) return libc;
         
         libc = (LibC) Native.loadLibrary("c", LinuxLibC.class, new HashMap<Object, Object>());
         
         return libc;
     }
     
     public static LibC loadMacOSLibC() {
         if (libc != null) return libc;
         
         libc = (LibC) Native.loadLibrary("c", LibC.class, new HashMap<Object, Object>());
         
         return libc;
     }
 
     public static LibC loadWindowsLibC() {
         if (libc != null) return libc;
         
         HashMap<Object, Object> options = new HashMap<Object, Object>();
         options.put(com.sun.jna.Library.OPTION_FUNCTION_MAPPER, new WindowsLibCFunctionMapper());
 
         libc = (LibC) Native.loadLibrary("msvcrt", LibC.class, options);
 
         return libc;
     }
 }
diff --git a/src/org/jruby/runtime/CallbackFactory.java b/src/org/jruby/runtime/CallbackFactory.java
index 151250d8ca..eec1531f17 100644
--- a/src/org/jruby/runtime/CallbackFactory.java
+++ b/src/org/jruby/runtime/CallbackFactory.java
@@ -1,173 +1,174 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.callback.ReflectionCallbackFactory;
 import org.jruby.runtime.callback.InvocationCallbackFactory;
 import org.jruby.runtime.callback.DumpingInvocationCallbackFactory;
+import org.jruby.util.SafePropertyAccessor;
 
 /**
  * Helper class to build Callback method.
  * This impements method corresponding to the signature of method most often found in
  * the Ruby library, for methods with other signature the appropriate Callback object
  * will need to be explicitly created.
  **/
 public abstract class CallbackFactory {
     public static final Class[] NULL_CLASS_ARRAY = new Class[0];
     
     /**
      * gets an instance method with no arguments.
      * @param method name of the method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getMethod(String method);
     public abstract Callback getFastMethod(String method);
 
     /**
      * gets an instance method with 1 argument.
      * @param method name of the method
      * @param arg1 the class of the only argument for this method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getMethod(String method, Class arg1);
     public abstract Callback getFastMethod(String method, Class arg1);
 
     /**
      * gets an instance method with two arguments.
      * @param method name of the method
      * @param arg1 the java class of the first argument for this method
      * @param arg2 the java class of the second argument for this method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getMethod(String method, Class arg1, Class arg2);
     public abstract Callback getFastMethod(String method, Class arg1, Class arg2);
     
     /**
      * gets an instance method with two arguments.
      * @param method name of the method
      * @param arg1 the java class of the first argument for this method
      * @param arg2 the java class of the second argument for this method
      * @param arg3 the java class of the second argument for this method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getMethod(String method, Class arg1, Class arg2, Class arg3);
     public abstract Callback getFastMethod(String method, Class arg1, Class arg2, Class arg3);
 
     /**
      * gets a singleton (class) method without arguments.
      * @param method name of the method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getSingletonMethod(String method);
     public abstract Callback getFastSingletonMethod(String method);
 
     /**
      * gets a singleton (class) method with 1 argument.
      * @param method name of the method
      * @param arg1 the class of the only argument for this method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getSingletonMethod(String method, Class arg1);
     public abstract Callback getFastSingletonMethod(String method, Class arg1);
 
     /**
      * gets a singleton (class) method with 2 arguments.
      * @param method name of the method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getSingletonMethod(String method, Class arg1, Class arg2);
     public abstract Callback getFastSingletonMethod(String method, Class arg1, Class arg2);
 
     /**
      * gets a singleton (class) method with 3 arguments.
      * @param method name of the method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getSingletonMethod(String method, Class arg1, Class arg2, Class arg3);
     public abstract Callback getFastSingletonMethod(String method, Class arg1, Class arg2, Class arg3);
 
     public abstract Callback getBlockMethod(String method);
     public abstract CompiledBlockCallback getBlockCallback(String method, Object scriptObject);
 
     /**
     * gets a singleton (class) method with no mandatory argument and some optional arguments.
      * @param method name of the method
     * @return a CallBack object corresponding to the appropriate method
     **/
     public abstract Callback getOptSingletonMethod(String method);
     public abstract Callback getFastOptSingletonMethod(String method);
 
     /**
     * gets an instance method with no mandatory argument and some optional arguments.
      * @param method name of the method
     * @return a CallBack object corresponding to the appropriate method
     **/
     public abstract Callback getOptMethod(String method);
     public abstract Callback getFastOptMethod(String method);
     
     public abstract Dispatcher createDispatcher(RubyClass metaClass);
 
     private static boolean reflection = false;
     private static boolean dumping = false;
 
     static {
        if (Ruby.isSecurityRestricted())
            reflection = true;
        else {
-           if(System.getProperty("jruby.reflection") != null && Boolean.getBoolean("jruby.reflection")) {
+           if(SafePropertyAccessor.getProperty("jruby.reflection") != null && SafePropertyAccessor.getBoolean("jruby.reflection")) {
                reflection = true;
            }
-           if(System.getProperty("jruby.dump_invocations") != null) {
+           if(SafePropertyAccessor.getProperty("jruby.dump_invocations") != null) {
                dumping = true;
            }
        }
     }
 
     public static CallbackFactory createFactory(Ruby runtime, Class type) {
         return createFactory(runtime, type, runtime.getJRubyClassLoader());
     }
 
     // used by compiler
     public static CallbackFactory createFactory(Ruby runtime, Class type, ClassLoader classLoader) {
         if(reflection) {
             return new ReflectionCallbackFactory(type);
         } else if(dumping) {
             return new DumpingInvocationCallbackFactory(runtime, type, classLoader);
         } else {
             // FIXME: No, I don't like it.
             return new InvocationCallbackFactory(runtime, type, classLoader);
         }
     }
 }
diff --git a/src/org/jruby/runtime/MethodFactory.java b/src/org/jruby/runtime/MethodFactory.java
index 9adb5dafc1..5c8a73554b 100644
--- a/src/org/jruby/runtime/MethodFactory.java
+++ b/src/org/jruby/runtime/MethodFactory.java
@@ -1,84 +1,85 @@
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
 package org.jruby.runtime;
 
 import java.lang.reflect.Method;
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.ReflectionMethodFactory;
 import org.jruby.internal.runtime.methods.InvocationMethodFactory;
 import org.jruby.internal.runtime.methods.DumpingInvocationMethodFactory;
 import org.jruby.parser.StaticScope;
+import org.jruby.util.SafePropertyAccessor;
 
 public abstract class MethodFactory {
     public interface MethodDefiningCallback {
         public void define(RubyModule targetMetaClass, Method method, DynamicMethod dynamicMethod);
     }
     public abstract DynamicMethod getCompiledMethod(
             RubyModule implementationClass, String method, 
             Arity arity, Visibility visibility, StaticScope scope, 
             Object scriptObject, CallConfiguration callConfig);
     public abstract DynamicMethod getAnnotatedMethod(RubyModule implementationClass, Method method);
     public abstract void defineIndexedAnnotatedMethods(RubyModule implementationClass, Class containingClass, MethodDefiningCallback callback);
 
     private static boolean reflection = false;
     private static boolean dumping = false;
     private static String dumpingPath = null;
 
     static {
        if (Ruby.isSecurityRestricted())
            reflection = true;
        else {
-           if (System.getProperty("jruby.reflection") != null && Boolean.getBoolean("jruby.reflection")) {
+           if (SafePropertyAccessor.getProperty("jruby.reflection") != null && SafePropertyAccessor.getBoolean("jruby.reflection")) {
                reflection = true;
            }
-           if (System.getProperty("jruby.dump_invocations") != null) {
+           if (SafePropertyAccessor.getProperty("jruby.dump_invocations") != null) {
                dumping = true;
-               dumpingPath = System.getProperty("jruby.dump_invocations").toString();
+               dumpingPath = SafePropertyAccessor.getProperty("jruby.dump_invocations").toString();
            }
        }
     }
 
     // Called from compiled code
     public static MethodFactory createFactory() {
         if (reflection) return new ReflectionMethodFactory();
         if (dumping) return new DumpingInvocationMethodFactory(dumpingPath);
 
         return new InvocationMethodFactory();
     }
 
     // Called from compiled code
     public static MethodFactory createFactory(ClassLoader classLoader) {
         if (reflection) return new ReflectionMethodFactory();
         if (dumping) return new DumpingInvocationMethodFactory(dumpingPath);
 
         return new InvocationMethodFactory(classLoader);
     }
 }
diff --git a/src/org/jruby/runtime/load/LoadService.java b/src/org/jruby/runtime/load/LoadService.java
index e7b5d647bc..3ed8f6fb1b 100644
--- a/src/org/jruby/runtime/load/LoadService.java
+++ b/src/org/jruby/runtime/load/LoadService.java
@@ -1,616 +1,627 @@
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
 package org.jruby.runtime.load;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.jar.JarFile;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyHash;
 import org.jruby.RubyString;
 import org.jruby.ast.executable.Script;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.JRubyFile;
 
 /**
  * <b>How require works in JRuby</b>
  * When requiring a name from Ruby, JRuby will first remove any file extension it knows about,
  * thereby making it possible to use this string to see if JRuby has already loaded
  * the name in question. If a .rb extension is specified, JRuby will only try
  * those extensions when searching. If a .so, .o, .dll, or .jar extension is specified, JRuby
  * will only try .so or .jar when searching. Otherwise, JRuby goes through the known suffixes
  * (.rb, .rb.ast.ser, .so, and .jar) and tries to find a library with this name. The process for finding a library follows this order
  * for all searchable extensions:
  * <ol>
  * <li>First, check if the name starts with 'jar:', then the path points to a jar-file resource which is returned.</li>
  * <li>Second, try searching for the file in the current dir</li>
  * <li>Then JRuby looks through the load path trying these variants:
  *   <ol>
  *     <li>See if the current load path entry starts with 'jar:', if so check if this jar-file contains the name</li>
  *     <li>Otherwise JRuby tries to construct a path by combining the entry and the current working directy, and then see if 
  *         a file with the correct name can be reached from this point.</li>
  *   </ol>
  * </li>
  * <li>If all these fail, try to load the name as a resource from classloader resources, using the bare name as
  *     well as the load path entries</li>
  * <li>When we get to this state, the normal JRuby loading has failed. At this stage JRuby tries to load 
  *     Java native extensions, by following this process:
  *   <ol>
  *     <li>First it checks that we haven't already found a library. If we found a library of type JarredScript, the method continues.</li>
  *     <li>The first step is translating the name given into a valid Java Extension class name. First it splits the string into 
  *     each path segment, and then makes all but the last downcased. After this it takes the last entry, removes all underscores
  *     and capitalizes each part separated by underscores. It then joins everything together and tacks on a 'Service' at the end.
  *     Lastly, it removes all leading dots, to make it a valid Java FWCN.</li>
  *     <li>If the previous library was of type JarredScript, we try to add the jar-file to the classpath</li>
  *     <li>Now JRuby tries to instantiate the class with the name constructed. If this works, we return a ClassExtensionLibrary. Otherwise,
  *     the old library is put back in place, if there was one.
  *   </ol>
  * </li>
  * <li>When all separate methods have been tried and there was no result, a LoadError will be raised.</li>
  * <li>Otherwise, the name will be added to the loaded features, and the library loaded</li>
  * </ol>
  *
  * <b>How to make a class that can get required by JRuby</b>
  * <p>First, decide on what name should be used to require the extension.
  * In this purely hypothetical example, this name will be 'active_record/connection_adapters/jdbc_adapter'.
  * Then create the class name for this require-name, by looking at the guidelines above. Our class should
  * be named active_record.connection_adapters.JdbcAdapterService, and implement one of the library-interfaces.
  * The easiest one is BasicLibraryService, where you define the basicLoad-method, which will get called
  * when your library should be loaded.</p>
  * <p>The next step is to either put your compiled class on JRuby's classpath, or package the class/es inside a
  * jar-file. To package into a jar-file, we first create the file, then rename it to jdbc_adapter.jar. Then 
  * we put this jar-file in the directory active_record/connection_adapters somewhere in JRuby's load path. For
  * example, copying jdbc_adapter.jar into JRUBY_HOME/lib/ruby/site_ruby/1.8/active_record/connection_adapters
  * will make everything work. If you've packaged your extension inside a RubyGem, write a setub.rb-script that 
  * copies the jar-file to this place.</p>
  * <p>If you don't want to have the name of your extension-class to be prescribed, you can also put a file called
  * jruby-ext.properties in your jar-files META-INF directory, where you can use the key <full-extension-name>.impl
  * to make the extension library load the correct class. An example for the above would have a jruby-ext.properties
  * that contained a ruby like: "active_record/connection_adapters/jdbc_adapter=org.jruby.ar.JdbcAdapter". (NOTE: THIS
  * FEATURE IS NOT IMPLEMENTED YET.)</p>
  *
  * @author jpetersen
  */
 public class LoadService {
     protected static final String JRUBY_BUILTIN_SUFFIX = ".rb";
 
     protected static final String[] sourceSuffixes = { ".class", ".rb" };
     protected static final String[] extensionSuffixes = { ".so", ".jar" };
     protected static final String[] allSuffixes = { ".class", ".rb", ".so", ".jar" };
     protected static final Pattern sourcePattern = Pattern.compile("\\.(?:rb)$");
     protected static final Pattern extensionPattern = Pattern.compile("\\.(?:so|o|dll|jar)$");
 
     protected final RubyArray loadPath;
     protected final RubyArray loadedFeatures;
     protected final Set loadedFeaturesInternal = Collections.synchronizedSet(new HashSet());
     protected final Set firstLineLoadedFeatures = Collections.synchronizedSet(new HashSet());
     protected final Map builtinLibraries = new HashMap();
 
     protected final Map jarFiles = new HashMap();
 
     protected final Map autoloadMap = new HashMap();
 
     protected final Ruby runtime;
     
     public LoadService(Ruby runtime) {
         this.runtime = runtime;
         loadPath = RubyArray.newArray(runtime);
         loadedFeatures = RubyArray.newArray(runtime);    
     }
 
     public void init(List additionalDirectories) {
         // add all startup load paths to the list first
         for (Iterator iter = additionalDirectories.iterator(); iter.hasNext();) {
             addPath((String) iter.next());
         }
 
         // add $RUBYLIB paths
        RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
        RubyString env_rubylib = runtime.newString("RUBYLIB");
        if (env.has_key_p(env_rubylib).isTrue()) {
            String rubylib = env.op_aref(env_rubylib).toString();
            String[] paths = rubylib.split(File.pathSeparator);
            for (int i = 0; i < paths.length; i++) {
                addPath(paths[i]);
            }
        }
 
         // wrap in try/catch for security exceptions in an applet
         if (!Ruby.isSecurityRestricted()) {
           String jrubyHome = runtime.getJRubyHome();
           if (jrubyHome != null) {
               char sep = '/';
               String rubyDir = jrubyHome + sep + "lib" + sep + "ruby" + sep;
 
               addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
               addPath(rubyDir + "site_ruby");
               addPath(rubyDir + Constants.RUBY_MAJOR_VERSION);
               addPath(rubyDir + Constants.RUBY_MAJOR_VERSION + sep + "java");
 
               // Added to make sure we find default distribution files within jar file.
               // TODO: Either make jrubyHome become the jar file or allow "classpath-only" paths
               addPath("lib" + sep + "ruby" + sep + Constants.RUBY_MAJOR_VERSION);
           }
         }
         
         // "." dir is used for relative path loads from a given file, as in require '../foo/bar'
         if (runtime.getSafeLevel() == 0) {
             addPath(".");
         }
     }
 
     private void addPath(String path) {
         // Empty paths do not need to be added
         if (path == null || path.length() == 0) return;
         
         synchronized(loadPath) {
             loadPath.add(runtime.newString(path.replace('\\', '/')));
         }
     }
 
     public void load(String file) {
         if(!runtime.getProfile().allowLoad(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
 
         Library library = null;
         
         library = findLibrary(file, false);
 
         if (library == null) {
             library = findLibraryWithClassloaders(file);
             if (library == null) {
                 throw runtime.newLoadError("No such file to load -- " + file);
             }
         }
         try {
             library.load(runtime);
         } catch (IOException e) {
             e.printStackTrace();
             throw runtime.newLoadError("IO error -- " + file);
         }
     }
 
     public boolean smartLoad(String file) {
         if (file.equals("")) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
         if(firstLineLoadedFeatures.contains(file)) {
             return false;
         }
         Library library = null;
         String loadName = file;
         String[] extensionsToSearch = null;
         
         // if an extension is specified, try more targetted searches
         if (file.lastIndexOf('.') > file.lastIndexOf('/')) {
             Matcher matcher = null;
             if ((matcher = sourcePattern.matcher(file)).find()) {
                 // source extensions
                 extensionsToSearch = sourceSuffixes;
                 
                 // trim extension to try other options
                 file = file.substring(0,matcher.start());
             } else if ((matcher = extensionPattern.matcher(file)).find()) {
                 // extension extensions
                 extensionsToSearch = extensionSuffixes;
                 
                 // trim extension to try other options
                 file = file.substring(0,matcher.start());
             } else {
                 // unknown extension, fall back to search with extensions
                 extensionsToSearch = allSuffixes;
             }
         } else {
             // try all extensions
             extensionsToSearch = allSuffixes;
         }
         
         // First try suffixes with normal loading
         for (int i = 0; i < extensionsToSearch.length; i++) {
-            library = findLibrary(file + extensionsToSearch[i], true);
+            if (Ruby.isSecurityRestricted()) {
+                // search in CWD only in if no security restrictions
+                library = findLibrary(file + extensionsToSearch[i], false);
+            } else {
+                library = findLibrary(file + extensionsToSearch[i], true);
+            }
+
             if (library != null) {
                 loadName = file + extensionsToSearch[i];
                 break;
             }
         }
 
         // Then try suffixes with classloader loading
         if (library == null) {
             for (int i = 0; i < extensionsToSearch.length; i++) {
                 library = findLibraryWithClassloaders(file + extensionsToSearch[i]);
                 if (library != null) {
                     loadName = file + extensionsToSearch[i];
                     break;
                 }
             }
         }
 
         library = tryLoadExtension(library,file);
 
         // no library or extension found, try to load directly as a class
         Script script = null;
         if (library == null) {
             String className = file;
             if (file.lastIndexOf(".") != -1) className = file.substring(0, file.lastIndexOf("."));
             className = className.replace('-', '_').replace('.', '_');
             int lastSlashIndex = className.lastIndexOf('/');
             if (lastSlashIndex > -1 &&
                     lastSlashIndex < className.length() - 1 &&
                     !Character.isJavaIdentifierStart(className.charAt(lastSlashIndex + 1))) {
                 if (lastSlashIndex == -1) {
                     className = "_" + className;
                 } else {
                     className = className.substring(0, lastSlashIndex + 1) + "_" + className.substring(lastSlashIndex + 1);
                 }
             }
             className = className.replace('/', '.');
             try {
                 Class scriptClass = Class.forName(className);
                 script = (Script)scriptClass.newInstance();
             } catch (Exception cnfe) {
                 throw runtime.newLoadError("no such file to load -- " + file);
             }
         }
         
         if (loadedFeaturesInternal.contains(loadName) || loadedFeatures.include_p(runtime.newString(loadName)).isTrue()) {
             return false;
         }
         
         // attempt to load the found library
         try {
             loadedFeaturesInternal.add(loadName);
             firstLineLoadedFeatures.add(file);
             synchronized(loadedFeatures) {
                 loadedFeatures.append(runtime.newString(loadName));
             }
 
             if (script != null) {
                 runtime.loadScript(script);
                 return true;
             }
             
             library.load(runtime);
             return true;
         } catch (Throwable e) {
             if(library instanceof JarredScript && file.endsWith(".jar")) {
                 return true;
             }
 
             loadedFeaturesInternal.remove(loadName);
             firstLineLoadedFeatures.remove(file);
             synchronized(loadedFeatures) {
                 loadedFeatures.remove(runtime.newString(loadName));
             }
             if (e instanceof RaiseException) throw (RaiseException) e;
 
             if(runtime.getDebug().isTrue()) e.printStackTrace();
 e.printStackTrace();
             RaiseException re = runtime.newLoadError("IO error -- " + file);
             re.initCause(e);
             throw re;
         }
     }
 
     public boolean require(String file) {
         if(!runtime.getProfile().allowRequire(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
         return smartLoad(file);
     }
 
     public IRubyObject getLoadPath() {
         return loadPath;
     }
 
     public IRubyObject getLoadedFeatures() {
         return loadedFeatures;
     }
 
     public IAutoloadMethod autoloadFor(String name) {
         return (IAutoloadMethod)autoloadMap.get(name);
     }
     
     public void removeAutoLoadFor(String name) {
         autoloadMap.remove(name);
     }
 
     public IRubyObject autoload(String name) {
         IAutoloadMethod loadMethod = (IAutoloadMethod)autoloadMap.remove(name);
         if (loadMethod != null) {
             return loadMethod.load(runtime, name);
         }
         return null;
     }
 
     public void addAutoload(String name, IAutoloadMethod loadMethod) {
         autoloadMap.put(name, loadMethod);
     }
 
     public void registerBuiltin(String name, Library library) {
         builtinLibraries.put(name, library);
     }
 
     public void registerRubyBuiltin(String libraryName) {
         registerBuiltin(libraryName + JRUBY_BUILTIN_SUFFIX, new BuiltinScript(libraryName));
     }
 
     private Library findLibrary(String file, boolean checkCWD) {
         if (builtinLibraries.containsKey(file)) {
             return (Library) builtinLibraries.get(file);
         }
         
         LoadServiceResource resource = findFile(file, checkCWD);
         if (resource == null) {
             return null;
         }
 
         if (file.endsWith(".jar")) {
             return new JarredScript(resource);
         } else if (file.endsWith(".class")) {
             return new JavaCompiledScript(resource);
         } else {
             return new ExternalScript(resource, file);
         }
     }
 
     private Library findLibraryWithClassloaders(String file) {
         LoadServiceResource resource = findFileInClasspath(file);
         if (resource == null) {
             return null;
         }
 
         if (file.endsWith(".jar")) {
             return new JarredScript(resource);
         } else {
             return new ExternalScript(resource, file);
         }
     }
 
     /**
      * this method uses the appropriate lookup strategy to find a file.
      * It is used by Kernel#require.
      *
      * @mri rb_find_file
      * @param name the file to find, this is a path name
      * @return the correct file
      */
     private LoadServiceResource findFile(String name, boolean checkCWD) {
         // if a jar URL, return load service resource directly without further searching
         if (name.startsWith("jar:")) {
             try {
                 return new LoadServiceResource(new URL(name), name);
             } catch (MalformedURLException e) {
                 throw runtime.newIOErrorFromException(e);
             }
         } else if(name.startsWith("file:") && name.indexOf("!/") != -1) {
             try {
                 JarFile file = new JarFile(name.substring(5, name.indexOf("!/")));
                 String filename = name.substring(name.indexOf("!/") + 2);
                 if(file.getJarEntry(filename) != null) {
                     return new LoadServiceResource(new URL("jar:" + name), name);
                 }
             } catch(Exception e) {}
         }
 
         if (checkCWD) {
             // check current directory; if file exists, retrieve URL and return resource
             try {
                 JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), name);
                 if (file.isFile() && file.isAbsolute()) {
                     try {
                         return new LoadServiceResource(file.toURI().toURL(), name);
                     } catch (MalformedURLException e) {
                         throw runtime.newIOErrorFromException(e);
                     }
                 }
             } catch (IllegalArgumentException illArgEx) {
             } catch (SecurityException secEx) {
             }
         }
         
 
         for (Iterator pathIter = loadPath.getList().iterator(); pathIter.hasNext();) {
             // TODO this is really ineffient, ant potentially a problem everytime anyone require's something.
             // we should try to make LoadPath a special array object.
             String entry = ((IRubyObject)pathIter.next()).toString();
             if (entry.startsWith("jar:") || (entry.startsWith("file:") && entry.indexOf("!/") != -1)) {
                 JarFile current = (JarFile)jarFiles.get(entry);
                 String after = entry.startsWith("file:") ? entry.substring(entry.indexOf("!/") + 2) + "/" : "";
                 String before = entry.startsWith("file:") ? entry.substring(0, entry.indexOf("!/")) : entry;
 
                 if(null == current) {
                     try {
                         if(entry.startsWith("jar:")) {
                             current = new JarFile(entry.substring(4));
                         } else {
                             current = new JarFile(entry.substring(5,entry.indexOf("!/")));
                         }
                         jarFiles.put(entry,current);
                     } catch (FileNotFoundException ignored) {
                     } catch (IOException e) {
                         throw runtime.newIOErrorFromException(e);
                     }
                 }
                 String canonicalEntry = after+name;
                 if(after.length()>0) {
                     try {
                         canonicalEntry = new File(after+name).getCanonicalPath().substring(new File(".").getCanonicalPath().length()+1);
                     } catch(Exception e) {}
                 }
                 if (current.getJarEntry(canonicalEntry) != null) {
                     try {
                         if(entry.startsWith("file:")) {
                             return new LoadServiceResource(new URL("jar:" + before + "!/" + canonicalEntry), entry + "/" + name);
                         } else {
                             return new LoadServiceResource(new URL("jar:file:" + entry.substring(4) + "!/" + name), entry + name);
                         }
                     } catch (MalformedURLException e) {
                         throw runtime.newIOErrorFromException(e);
                     }
                 }
             } 
             try {
                 JRubyFile current = JRubyFile.create(JRubyFile.create(runtime.getCurrentDirectory(),entry).getAbsolutePath(), name);
                 if (current.isFile()) {
                     try {
                         return new LoadServiceResource(current.toURI().toURL(), current.getPath());
                     } catch (MalformedURLException e) {
                         throw runtime.newIOErrorFromException(e);
                     }
                 }
             } catch (SecurityException secEx) { }
         }
 
         return null;
     }
 
     /**
      * this method uses the appropriate lookup strategy to find a file.
      * It is used by Kernel#require.
      *
      * @mri rb_find_file
      * @param name the file to find, this is a path name
      * @return the correct file
      */
     private LoadServiceResource findFileInClasspath(String name) {
         // Look in classpath next (we do not use File as a test since UNC names will match)
         // Note: Jar resources must NEVER begin with an '/'. (previous code said "always begin with a /")
         ClassLoader classLoader = runtime.getJRubyClassLoader();
 
+        // handle security-sensitive case
+        if (Ruby.isSecurityRestricted() && classLoader == null) {
+            classLoader = runtime.getInstanceConfig().getLoader();
+        }
+
         for (Iterator pathIter = loadPath.getList().iterator(); pathIter.hasNext();) {
             String entry = pathIter.next().toString();
 
             // if entry starts with a slash, skip it since classloader resources never start with a /
             if (entry.charAt(0) == '/' || (entry.length() > 1 && entry.charAt(1) == ':')) continue;
             
             // otherwise, try to load from classpath (Note: Jar resources always uses '/')
             URL loc = classLoader.getResource(entry + "/" + name);
 
             // Make sure this is not a directory or unavailable in some way
             if (isRequireable(loc)) {
                 return new LoadServiceResource(loc, loc.getPath());
             }
         }
 
         // if name starts with a / we're done (classloader resources won't load with an initial /)
         if (name.charAt(0) == '/' || (name.length() > 1 && name.charAt(1) == ':')) return null;
         
         // Try to load from classpath without prefix. "A/b.rb" will not load as 
         // "./A/b.rb" in a jar file.
         URL loc = classLoader.getResource(name);
 
         return isRequireable(loc) ? new LoadServiceResource(loc, loc.getPath()) : null;
     }
 
     private Library tryLoadExtension(Library library, String file) {
         // This code exploits the fact that all .jar files will be found for the JarredScript feature.
         // This is where the basic extension mechanism gets fixed
         Library oldLibrary = library;
         
         if((library == null || library instanceof JarredScript) && !file.equalsIgnoreCase("")) {
             // Create package name, by splitting on / and joining all but the last elements with a ".", and downcasing them.
             String[] all = file.split("/");
 
             StringBuffer finName = new StringBuffer();
             for(int i=0, j=(all.length-1); i<j; i++) {
                 finName.append(all[i].toLowerCase()).append(".");
                 
             }
             
             try {
                 // Make the class name look nice, by splitting on _ and capitalize each segment, then joining
                 // the, together without anything separating them, and last put on "Service" at the end.
                 String[] last = all[all.length-1].split("_");
                 for(int i=0, j=last.length; i<j; i++) {
                     finName.append(Character.toUpperCase(last[i].charAt(0))).append(last[i].substring(1));
                 }
                 finName.append("Service");
 
                 // We don't want a package name beginning with dots, so we remove them
                 String className = finName.toString().replaceAll("^\\.*","");
 
                 // If there is a jar-file with the required name, we add this to the class path.
                 if(library instanceof JarredScript) {
                     // It's _really_ expensive to check that the class actually exists in the Jar, so
                     // we don't do that now.
                     runtime.getJRubyClassLoader().addURL(((JarredScript)library).getResource().getURL());
                 }
 
                 Class theClass = runtime.getJavaSupport().loadJavaClass(className);
                 library = new ClassExtensionLibrary(theClass);
             } catch(Exception ee) {
                 library = null;
                 runtime.getGlobalVariables().set("$!", runtime.getNil());
             }
         }
         
         // If there was a good library before, we go back to that
         if(library == null && oldLibrary != null) {
             library = oldLibrary;
         }
         return library;
     }
     
     /* Directories and unavailable resources are not able to open a stream. */
     private boolean isRequireable(URL loc) {
         if (loc != null) {
         	if (loc.getProtocol().equals("file") && new java.io.File(loc.getFile()).isDirectory()) {
         		return false;
         	}
         	
         	try {
                 loc.openConnection();
                 return true;
             } catch (Exception e) {}
         }
         return false;
     }
 }
diff --git a/src/org/jruby/util/JRubyFile.java b/src/org/jruby/util/JRubyFile.java
index fda129f118..82311e8776 100644
--- a/src/org/jruby/util/JRubyFile.java
+++ b/src/org/jruby/util/JRubyFile.java
@@ -1,190 +1,190 @@
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
 package org.jruby.util;
 
 import java.io.File;
 import java.io.FileFilter;
 import java.io.FilenameFilter;
 import java.io.IOException;
 
 /**
  * <p>This file acts as an alternative to NormalizedFile, due to the problems with current working 
  * directory.</p>
  *
  */
 public class JRubyFile extends File {
     private static final long serialVersionUID = 435364547567567L;
 
     public static JRubyFile create(String cwd, String pathname) {
         if (pathname == null || pathname.equals("")) {
             return JRubyNonExistentFile.NOT_EXIST;
         }
         File internal = new File(pathname);
         if(!internal.isAbsolute()) {
             internal = new File(cwd,pathname);
             if(!internal.isAbsolute()) {
                 throw new IllegalArgumentException("Neither current working directory ("+cwd+") nor pathname ("+pathname+") led to an absolute path");
             }
         }
         return new JRubyFile(internal);
     }
 
     public static String getFileProperty(String property) {
-        String value = System.getProperty(property);
+        String value = SafePropertyAccessor.getProperty(property, "/");
         
         return value.replace(File.separatorChar, '/');
     }
 
     private JRubyFile(File file) {
         this(file.getAbsolutePath());
     }
 
     protected JRubyFile(String filename) {
         super(filename);
     }
 
     public String getAbsolutePath() {
         return new File(super.getPath()).getAbsolutePath().replace(File.separatorChar, '/'); 
     }
 
     public String getCanonicalPath() throws IOException {
         String canonicalPath = super.getCanonicalPath().replace(File.separatorChar, '/');
         
         // Java 1.4 canonicalPath does not strip off '.'
         if (canonicalPath.endsWith("/.")) {
             canonicalPath = canonicalPath.substring(0, canonicalPath.length() - 1);
         }
         
         return canonicalPath;
     }
 
     public String getPath() {
         return super.getPath().replace(File.separatorChar, '/');
     }
 
     public String toString() {
         return super.toString().replace(File.separatorChar, '/');
     }
 
     public File getAbsoluteFile() {
         return new JRubyFile(getAbsolutePath());
     }
 
     public File getCanonicalFile() throws IOException {
         return new JRubyFile(getCanonicalPath());
     }
 
     public String getParent() {
         String par = super.getParent();
         if (par != null) {
             par = par.replace(File.separatorChar, '/');
         }
         return par;
     }
 
     public File getParentFile() {
         String par = getParent();
         if (par == null) {
             return this;
         } else {
             return new JRubyFile(par);
         }
     }
     
     public static File[] listRoots() {
         File[] roots = File.listRoots();
         JRubyFile[] smartRoots = new JRubyFile[roots.length];
         for(int i = 0, j = roots.length; i < j; i++) {
             smartRoots[i] = new JRubyFile(roots[i].getPath());
         }
         return smartRoots;
     }
     
     public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
         return new JRubyFile(File.createTempFile(prefix, suffix,directory));
     }
     
     public static File createTempFile(String prefix, String suffix) throws IOException {
         return new JRubyFile(File.createTempFile(prefix, suffix));
     }
 
     public String[] list(FilenameFilter filter) {
         String[] files = super.list(filter);
         if (files == null) {
             return null;
         }
         
         String[] smartFiles = new String[files.length];
         for (int i = 0; i < files.length; i++) {
             smartFiles[i] = files[i].replace(File.separatorChar, '/');
         }
         return smartFiles;
     }
 
     public File[] listFiles() {
         File[] files = super.listFiles();
         if (files == null) {
             return null;
         }
         
         JRubyFile[] smartFiles = new JRubyFile[files.length];
         for (int i = 0, j = files.length; i < j; i++) {
             smartFiles[i] = create(super.getAbsolutePath(),files[i].getPath());
         }
         return smartFiles;
     }
 
     public File[] listFiles(final FileFilter filter) {
         final File[] files = super.listFiles(filter);
         if (files == null) {
             return null;
         }
         
         JRubyFile[] smartFiles = new JRubyFile[files.length];
         for (int i = 0,j = files.length; i < j; i++) {
             smartFiles[i] = create(super.getAbsolutePath(),files[i].getPath());
         }
         return smartFiles;
     }
 
     public File[] listFiles(final FilenameFilter filter) {
         final File[] files = super.listFiles(filter);
         if (files == null) {
             return null;
         }
         
         JRubyFile[] smartFiles = new JRubyFile[files.length];
         for (int i = 0,j = files.length; i < j; i++) {
             smartFiles[i] = create(super.getAbsolutePath(),files[i].getPath());
         }
         return smartFiles;
     }
 }
