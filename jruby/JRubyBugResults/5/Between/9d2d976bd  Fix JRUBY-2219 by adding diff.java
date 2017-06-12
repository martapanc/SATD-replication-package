diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index deecfc9028..c4facce780 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,2198 +1,2190 @@
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
-import java.io.File;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.lang.ref.WeakReference;
-import java.lang.reflect.Field;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.Set;
 import java.util.Stack;
 import java.util.Vector;
 import java.util.WeakHashMap;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.SynchronousQueue;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.ThreadPoolExecutor;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.atomic.AtomicInteger;
 
 import org.joda.time.DateTimeZone;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.RubiniusRunner;
 import org.jruby.ast.executable.Script;
 import org.jruby.ast.executable.YARVCompiledRunner;
 import org.jruby.common.RubyWarnings;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.JITCompiler;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.compiler.yarv.StandardYARVCompiler;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.ext.JRubyPOSIXHandler;
 import org.jruby.ext.LateLoadingLibrary;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.ext.posix.POSIXFactory;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.management.BeanManager;
 import org.jruby.management.ClassCache;
 import org.jruby.management.Config;
 import org.jruby.management.ParserStats;
 import org.jruby.parser.EvalStaticScope;
 import org.jruby.parser.Parser;
 import org.jruby.parser.ParserConfiguration;
+import org.jruby.platform.Errno;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.EventHook;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.ByteList;
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.KCode;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.collections.WeakHashSet;
 import org.jruby.util.io.ChannelDescriptor;
 
 /**
  * The Ruby object represents the top-level of a JRuby "instance" in a given VM.
  * JRuby supports spawning multiple instances in the same JVM. Generally, objects
  * created under these instances are tied to a given runtime, for such details
  * as identity and type, because multiple Ruby instances means there are
  * multiple instances of each class. This means that in multi-runtime mode
  * (or really, multi-VM mode, where each JRuby instance is a ruby "VM"), objects
  * generally can't be transported across runtimes without marshaling.
  * 
  * This class roots everything that makes the JRuby runtime function, and
  * provides a number of utility methods for constructing global types and
  * accessing global runtime structures.
  */
 public final class Ruby {
     /**
      * Returns a new instance of the JRuby runtime configured with defaults.
      *
      * @return the JRuby runtime
      * @see org.jruby.RubyInstanceConfig
      */
     public static Ruby newInstance() {
         return newInstance(new RubyInstanceConfig());
     }
 
     /**
      * Returns a new instance of the JRuby runtime configured as specified.
      *
      * @param config The instance configuration
      * @return The JRuby runtime
      * @see org.jruby.RubyInstanceConfig
      */
     public static Ruby newInstance(RubyInstanceConfig config) {
         Ruby ruby = new Ruby(config);
         ruby.init();
         return ruby;
     }
 
     /**
      * Returns a new instance of the JRuby runtime configured with the given
      * input, output and error streams and otherwise default configuration
      * (except where specified system properties alter defaults).
      *
      * @param in the custom input stream
      * @param out the custom output stream
      * @param err the custom error stream
      * @return the JRuby runtime
      * @see org.jruby.RubyInstanceConfig
      */
     public static Ruby newInstance(InputStream in, PrintStream out, PrintStream err) {
         RubyInstanceConfig config = new RubyInstanceConfig();
         config.setInput(in);
         config.setOutput(out);
         config.setError(err);
         return newInstance(config);
     }
     
     /**
      * Create and initialize a new JRuby runtime. The properties of the
      * specified RubyInstanceConfig will be used to determine various JRuby
      * runtime characteristics.
      * 
      * @param config The configuration to use for the new instance
      * @see org.jruby.RubyInstanceConfig
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
         this.kcode              = config.getKCode();
         this.beanManager        = new BeanManager(this, config.isManagementEnabled());
         this.jitCompiler        = new JITCompiler(this);
         this.parserStats        = new ParserStats(this);
         
         this.beanManager.register(new Config(this));
         this.beanManager.register(parserStats);
         this.beanManager.register(new ClassCache(this));
         
         this.cacheMap = new CacheMap(this);
     }
     
     /**
      * Evaluates a script under the current scope (perhaps the top-level
      * scope) and returns the result (generally the last value calculated).
      * This version goes straight into the interpreter, bypassing compilation
      * and runtime preparation typical to normal script runs.
      * 
      * @param script The scriptlet to run
      * @returns The result of the eval
      */
     public IRubyObject evalScriptlet(String script) {
         ThreadContext context = getCurrentContext();
         DynamicScope currentScope = context.getCurrentScope();
         ManyVarsDynamicScope newScope = new ManyVarsDynamicScope(new EvalStaticScope(currentScope.getStaticScope()), currentScope);
         Node node = parseEval(script, "<script>", newScope, 0);
         
         try {
             context.preEvalScriptlet(newScope);
             return node.interpret(this, context, context.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             throw newLocalJumpError("return", (IRubyObject)rj.getValue(), "unexpected return");
         } catch (JumpException.BreakJump bj) {
             throw newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
             throw newLocalJumpError("redo", (IRubyObject)rj.getValue(), "unexpected redo");
         } finally {
             context.postEvalScriptlet();
         }
     }
     
     /**
      * Parse and execute the specified script 
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
         ThreadContext context = getCurrentContext();
         
         String oldFile = context.getFile();
         int oldLine = context.getLine();
         try {
             context.setFile(node.getPosition().getFile());
             context.setLine(node.getPosition().getStartLine());
             return runNormally(node, false);
         } finally {
             context.setFile(oldFile);
             context.setLine(oldLine);
         }
     }
     
     /**
      * Run the script contained in the specified input stream, using the
      * specified filename as the name of the script being executed. The stream
      * will be read fully before being parsed and executed. The given filename
      * will be used for the ruby $PROGRAM_NAME and $0 global variables in this
      * runtime.
      * 
      * This method is intended to be called once per runtime, generally from
      * Main or from main-like top-level entry points.
      * 
      * As part of executing the script loaded from the input stream, various
      * RubyInstanceConfig properties will be used to determine whether to
      * compile the script before execution or run with various wrappers (for
      * looping, printing, and so on, see jruby -help).
      * 
      * @param inputStream The InputStream from which to read the script contents
      * @param filename The filename to use when parsing, and for $PROGRAM_NAME
      * and $0 ruby global variables.
      */
     public void runFromMain(InputStream inputStream, String filename) {
         IAccessor d = new ValueAccessor(newString(filename));
         getGlobalVariables().define("$PROGRAM_NAME", d);
         getGlobalVariables().define("$0", d);
 
         for (Iterator i = config.getOptionGlobals().entrySet().iterator(); i.hasNext();) {
             Map.Entry entry = (Map.Entry) i.next();
             Object value = entry.getValue();
             IRubyObject varvalue;
             if (value != null) {
                 varvalue = newString(value.toString());
             } else {
                 varvalue = getTrue();
             }
             getGlobalVariables().set("$" + entry.getKey().toString(), varvalue);
         }
 
         
         if(config.isYARVEnabled()) {
             if (config.isShowBytecode()) System.err.print("error: bytecode printing only works with JVM bytecode");
             new YARVCompiledRunner(this, inputStream, filename).run();
         } else if(config.isRubiniusEnabled()) {
             if (config.isShowBytecode()) System.err.print("error: bytecode printing only works with JVM bytecode");
             new RubiniusRunner(this, inputStream, filename).run();
         } else {
             Node scriptNode = parseFromMain(inputStream, filename);
             ThreadContext context = getCurrentContext();
 
             String oldFile = context.getFile();
             int oldLine = context.getLine();
             try {
                 context.setFile(scriptNode.getPosition().getFile());
                 context.setLine(scriptNode.getPosition().getStartLine());
 
                 if (config.isAssumePrinting() || config.isAssumeLoop()) {
                     runWithGetsLoop(scriptNode, config.isAssumePrinting(), config.isProcessLineEnds(),
                             config.isSplit(), config.isYARVCompileEnabled());
                 } else {
                     runNormally(scriptNode, config.isYARVCompileEnabled());
                 }
             } finally {
                 context.setFile(oldFile);
                 context.setLine(oldLine);
             }
         }
     }
 
     /**
      * Parse the script contained in the given input stream, using the given
      * filename as the name of the script, and return the root Node. This
      * is used to verify that the script syntax is valid, for jruby -c. The
      * current scope (generally the top-level scope) is used as the parent
      * scope for parsing.
      * 
      * @param inputStream The input stream from which to read the script
      * @param filename The filename to use for parsing
      * @returns The root node of the parsed script
      */
     public Node parseFromMain(InputStream inputStream, String filename) {
         if (config.isInlineScript()) {
             return parseInline(inputStream, filename, getCurrentContext().getCurrentScope());
         } else {
             return parseFile(inputStream, filename, getCurrentContext().getCurrentScope());
         }
     }
     
     /**
      * Run the given script with a "while gets; end" loop wrapped around it.
      * This is primarily used for the -n command-line flag, to allow writing
      * a short script that processes input lines using the specified code.
      * 
      * @param scriptNode The root node of the script to execute
      * @param printing Whether $_ should be printed after each loop (as in the
      * -p command-line flag)
      * @param processLineEnds Whether line endings should be processed by
      * setting $\ to $/ and <code>chop!</code>ing every line read
      * @param split Whether to split each line read using <code>String#split</code>
      * @param yarvCompile Whether to compile the target script to YARV (Ruby 1.9)
      * bytecode before executing.
      * @return The result of executing the specified script
      */
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
         
         while (RubyKernel.gets(context, getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
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
                     
                     if (printing) RubyKernel.print(context, getKernel(), new IRubyObject[] {getGlobalVariables().get("$_")});
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
     
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      * 
      * @param scriptNode The root node of the script to be executed
      * @param yarvCompile Whether to compile the script to YARV (Ruby 1.9)
      * bytecode before execution
      * @return The result of executing the script
      */
     public IRubyObject runNormally(Node scriptNode, boolean yarvCompile) {
         Script script = null;
         YARVCompiledRunner runner = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         boolean forceCompile = getInstanceConfig().getCompileMode().shouldPrecompileAll();
         if (yarvCompile) {
             runner = tryCompileYarv(scriptNode);
         } else if (compile) {
             script = tryCompile(scriptNode);
             if (forceCompile && script == null) {
                 System.err.println("Error, could not compile; pass -J-Djruby.jit.logging.verbose=true for more details");
                 return getNil();
             }
         }
         
         if (script != null) {
             if (config.isShowBytecode()) {
                 return nilObject;
             } else {
                 return runScript(script);
             }
         } else if (runner != null) {
             return runYarv(runner);
         } else {
             if (config.isShowBytecode()) System.err.print("error: bytecode printing only works with JVM bytecode");
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
             if (config.isShowBytecode()) {
                 compiler.compileRoot(node, asmCompiler, inspector, false, false);
                 asmCompiler.dumpClass(System.out);
             } else {
                 compiler.compileRoot(node, asmCompiler, inspector, true, false);
             }
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
             return script.load(context, context.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
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
         
         assert scriptNode != null : "scriptNode is not null";
         
         try {
             return scriptNode.interpret(this, context, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     public Parser getParser() {
         return parser;
     }
     
     public BeanManager getBeanManager() {
         return beanManager;
     }
     
     public JITCompiler getJITCompiler() {
         return jitCompiler;
     }
 
     /**
      * @deprecated use #newInstance()
      */
     public static Ruby getDefaultInstance() {
         return newInstance();
     }
     
     @Deprecated
     public static Ruby getCurrentInstance() {
         return null;
     }
     
     @Deprecated
     public static void setCurrentInstance(Ruby runtime) {
     }
     
     public int allocSymbolId() {
         return symbolLastId.incrementAndGet();
     }
     public int allocModuleId() {
         return moduleLastId.incrementAndGet();
     }
 
     /**
      * Retrieve the module with the given name from the Object namespace.
      * 
      * @param name The name of the module
      * @return The module or null if not found
      */
     public RubyModule getModule(String name) {
         return (RubyModule) objectClass.getConstantAt(name);
     }
 
     /**
      * Retrieve the module with the given name from the Object namespace. The
      * module name must be an interned string, but this method will be faster
      * than the non-interned version.
      * 
      * @param internedName The name of the module; <em>must</em> be an interned String
      * @return The module or null if not found
      */
     public RubyModule fastGetModule(String internedName) {
         return (RubyModule) objectClass.fastGetConstantAt(internedName);
     }
 
     /** 
      * Retrieve the class with the given name from the Object namespace.
      *
      * @param name The name of the class
      * @return The class
      */
     public RubyClass getClass(String name) {
         return objectClass.getClass(name);
     }
 
     /**
      * Retrieve the class with the given name from the Object namespace. The
      * module name must be an interned string, but this method will be faster
      * than the non-interned version.
      * 
      * @param internedName the name of the class; <em>must</em> be an interned String!
      * @return
      */
     public RubyClass fastGetClass(String internedName) {
         return objectClass.fastGetClass(internedName);
     }
 
     /** 
      * Define a new class under the Object namespace. Roughly equivalent to
      * rb_define_class in MRI.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @return The new class
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator) {
         return defineClassUnder(name, superClass, allocator, objectClass);
     }
 
     /** 
      * A variation of defineClass that allows passing in an array of subplementary
      * call sites for improving dynamic invocation performance.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @return The new class
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator, CallSite[] callSites) {
         return defineClassUnder(name, superClass, allocator, objectClass, callSites);
     }
 
     /**
      * Define a new class with the given name under the given module or class
      * namespace. Roughly equivalent to rb_define_class_under in MRI.
      * 
      * If the name specified is already bound, its value will be returned if:
      * * It is a class
      * * No new superclass is being defined
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @param parent The namespace under which to define the new class
      * @return The new class
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent) {
         return defineClassUnder(name, superClass, allocator, parent, null);
     }
 
     /**
      * A variation of defineClassUnder that allows passing in an array of
      * supplementary call sites to improve dynamic invocation.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @param parent The namespace under which to define the new class
      * @param callSites The array of call sites to add
      * @return The new class
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent, CallSite[] callSites) {
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
         
         boolean parentIsObject = parent == objectClass;
 
         if (superClass == null) {
             String className = parentIsObject ? name : parent.getName() + "::" + name;  
             warnings.warn(ID.NO_SUPER_CLASS, "no super class for `" + className + "', Object assumed", className);
             
             superClass = objectClass;
         }
 
         return RubyClass.newClass(this, superClass, name, allocator, parent, !parentIsObject, callSites);
     }
 
     /** 
      * Define a new module under the Object namespace. Roughly equivalent to
      * rb_define_module in MRI.
      * 
      * @param name The name of the new module
      * @returns The new module
      */
     public RubyModule defineModule(String name) {
         return defineModuleUnder(name, objectClass);
     }
 
     /**
      * Define a new module with the given name under the given module or
      * class namespace. Roughly equivalent to rb_define_module_under in MRI.
      * 
      * @param name The name of the new module
      * @param parent The class or module namespace under which to define the
      * module
      * @returns The new module
      */
     public RubyModule defineModuleUnder(String name, RubyModule parent) {
         IRubyObject moduleObj = parent.getConstantAt(name);
         
         boolean parentIsObject = parent == objectClass;
 
         if (moduleObj != null ) {
             if (moduleObj.isModule()) return (RubyModule)moduleObj;
             
             if (parentIsObject) {
                 throw newTypeError(moduleObj.getMetaClass().getName() + " is not a module");
             } else {
                 throw newTypeError(parent.getName() + "::" + moduleObj.getMetaClass().getName() + " is not a module");
             }
         }
 
         return RubyModule.newModule(this, name, parent, !parentIsObject);
     }
 
     /**
      * From Object, retrieve the named module. If it doesn't exist a
      * new module is created.
      * 
      * @param name The name of the module
      * @returns The existing or new module
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
 
 
     /** 
      * Retrieve the current safe level.
      * 
      * @see org.jruby.Ruby#setSaveLevel
      */
     public int getSafeLevel() {
         return this.safeLevel;
     }
 
 
     /** 
      * Set the current safe level:
      * 
      * 0 - strings from streams/environment/ARGV are tainted (default)
      * 1 - no dangerous operation by tainted value
      * 2 - process/file operations prohibited
      * 3 - all generated objects are tainted
      * 4 - no global (non-tainted) variable modification/no direct output
      * 
      * The safe level is set using $SAFE in Ruby code. It is not particularly
      * well supported in JRuby.
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
 
     // FIXME moved this here to get what's obviously a utility method out of IRubyObject.
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
 
     /** rb_define_global_const
      *
      */
     public void defineGlobalConstant(String name, IRubyObject value) {
         objectClass.defineConstant(name, value);
     }
 
     public boolean isClassDefined(String name) {
         return getModule(name) != null;
     }
     
     /**
      * A ThreadFactory for when we're using pooled threads; we want to create
      * the threads with daemon = true so they don't keep us from shutting down.
      */
     public static class DaemonThreadFactory implements ThreadFactory {
         public Thread newThread(Runnable runnable) {
             Thread thread = new Thread(runnable);
             thread.setDaemon(true);
             
             return thread;
         }
     }
 
     /** 
      * This method is called immediately after constructing the Ruby instance.
      * The main thread is prepared for execution, all core classes and libraries
      * are initialized, and any libraries required on the command line are
      * loaded.
      */
     private void init() {
         // Get the main threadcontext (gets constructed for us)
         ThreadContext tc = getCurrentContext();
 
         safeLevel = config.getSafeLevel();
         
         // Construct key services
         loadService = config.createLoadService(this);
         posix = POSIXFactory.getPOSIX(new JRubyPOSIXHandler(this), RubyInstanceConfig.nativeEnabled);
         javaSupport = new JavaSupport(this);
         
         if (RubyInstanceConfig.POOLING_ENABLED) {
             Executors.newCachedThreadPool();
             executor = new ThreadPoolExecutor(
                     RubyInstanceConfig.POOL_MIN,
                     RubyInstanceConfig.POOL_MAX,
                     RubyInstanceConfig.POOL_TTL,
                     TimeUnit.SECONDS,
                     new SynchronousQueue<Runnable>(),
                     new DaemonThreadFactory());
         }
         
         // initialize the root of the class hierarchy completely
         initRoot(tc);
 
         // Construct the top-level execution frame and scope for the main thread
         tc.prepareTopLevel(objectClass, topSelf);
 
         // Initialize all the core classes
         bootstrap();
         
         // Create global constants and variables
         RubyGlobal.createGlobals(tc, this);
 
         // Prepare LoadService and load path
         getLoadService().init(config.loadPaths());
 
         // initialize builtin libraries
         initBuiltins();
         
         // Require in all libraries specified on command line
         for (String scriptName : config.requiredLibraries()) {
             RubyKernel.require(getTopSelf(), newString(scriptName), Block.NULL_BLOCK);
         }
     }
 
     private void bootstrap() {
         initCore();
         initExceptions();
     }
 
     private void initRoot(ThreadContext context) {
         // Bootstrap the top of the hierarchy
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
         
         // set constants now that they're initialized
         objectClass.setConstant("Object", objectClass);
         objectClass.setConstant("Class", classClass);
         objectClass.setConstant("Module", moduleClass);
 
         // Initialize Kernel and include into Object
         RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
         
         // Initialize the "dummy" class used as a marker
         dummyClass = new RubyClass(this);
         dummyClass.freeze(context);
 
         // Object is ready, create top self
         topSelf = TopSelfFactory.createTopSelf(this);
     }
 
     private void initCore() {
         // Pre-create all the core classes potentially referenced during startup
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
 
         if (profile.allowClass("ThreadGroup")) {
             RubyThreadGroup.createThreadGroupClass(this);
         }
         if (profile.allowClass("Thread")) {
             RubyThread.createThreadClass(this);
         }
         if (profile.allowClass("Exception")) {
             RubyException.createExceptionClass(this);
         }
 
         if (config.getCompatVersion() == CompatVersion.RUBY1_8) {
             if (profile.allowModule("Precision")) {
                 RubyPrecision.createPrecisionModule(this);
             }
         }
 
         if (profile.allowClass("Numeric")) {
             RubyNumeric.createNumericClass(this);
         }
         if (profile.allowClass("Integer")) {
             RubyInteger.createIntegerClass(this);
         }
         if (profile.allowClass("Fixnum")) {
             RubyFixnum.createFixnumClass(this);
         }
 
         if (config.getCompatVersion() == CompatVersion.RUBY1_9) {
             if (profile.allowClass("Complex")) {
                 RubyComplex.createComplexClass(this);
             }
             if (profile.allowClass("Rational")) {
                 RubyRational.createRationalClass(this);
             }
         }
 
         if (profile.allowClass("Hash")) {
             RubyHash.createHashClass(this);
         }
         if (profile.allowClass("Array")) {
             RubyArray.createArrayClass(this);
         }
         if (profile.allowClass("Float")) {
             RubyFloat.createFloatClass(this);
         }
         if (profile.allowClass("Bignum")) {
             RubyBignum.createBignumClass(this);
         }
         ioClass = RubyIO.createIOClass(this);
 
         if (profile.allowClass("Struct")) {
             RubyStruct.createStructClass(this);
         }
         if (profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass, new IRubyObject[]{newString("Tms"), newSymbol("utime"), newSymbol("stime"), newSymbol("cutime"), newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
         if (profile.allowClass("Binding")) {
             RubyBinding.createBindingClass(this);
         }
         // Math depends on all numeric types
         if (profile.allowModule("Math")) {
             RubyMath.createMathModule(this);
         }
         if (profile.allowClass("Regexp")) {
             RubyRegexp.createRegexpClass(this);
         }
         if (profile.allowClass("Range")) {
             RubyRange.createRangeClass(this);
         }
         if (profile.allowModule("ObjectSpace")) {
             RubyObjectSpace.createObjectSpaceModule(this);
         }
         if (profile.allowModule("GC")) {
             RubyGC.createGCModule(this);
         }
         if (profile.allowClass("Proc")) {
             RubyProc.createProcClass(this);
         }
         if (profile.allowClass("Method")) {
             RubyMethod.createMethodClass(this);
         }
         if (profile.allowClass("MatchData")) {
             RubyMatchData.createMatchDataClass(this);
         }
         if (profile.allowModule("Marshal")) {
             RubyMarshal.createMarshalModule(this);
         }
         if (profile.allowClass("Dir")) {
             RubyDir.createDirClass(this);
         }
         if (profile.allowModule("FileTest")) {
             RubyFileTest.createFileTestModule(this);
         }
         // depends on IO, FileTest
         if (profile.allowClass("File")) {
             RubyFile.createFileClass(this);
         }
         if (profile.allowClass("File::Stat")) {
             RubyFileStat.createFileStatClass(this);
         }
         if (profile.allowModule("Process")) {
             RubyProcess.createProcessModule(this);
         }
         if (profile.allowClass("Time")) {
             RubyTime.createTimeClass(this);
         }
         if (profile.allowClass("UnboundMethod")) {
             RubyUnboundMethod.defineUnboundMethodClass(this);
         }
         if (profile.allowClass("Data")) {
             defineClass("Data", objectClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         }
         if (!isSecurityRestricted()) {
             // Signal uses sun.misc.* classes, this is not allowed
             // in the security-sensitive environments
             if (profile.allowModule("Signal")) {
                 RubySignal.createSignal(this);
             }
         }
         if (profile.allowClass("Continuation")) {
             RubyContinuation.createContinuation(this);
         }
     }
 
     private void initExceptions() {
         standardError = defineClassIfAllowed("StandardError", exceptionClass);
         runtimeError = defineClassIfAllowed("RuntimeError", standardError);
         ioError = defineClassIfAllowed("IOError", standardError);
         scriptError = defineClassIfAllowed("ScriptError", exceptionClass);
         rangeError = defineClassIfAllowed("RangeError", standardError);
         signalException = defineClassIfAllowed("SignalException", exceptionClass);
         
         if (profile.allowClass("NameError")) {
             nameError = RubyNameError.createNameErrorClass(this, standardError);
             nameErrorMessage = RubyNameError.createNameErrorMessageClass(this, nameError);            
         }
         if (profile.allowClass("NoMethodError")) {
             noMethodError = RubyNoMethodError.createNoMethodErrorClass(this, nameError);
         }
         if (profile.allowClass("SystemExit")) {
             systemExit = RubySystemExit.createSystemExitClass(this, exceptionClass);
         }
         if (profile.allowClass("LocalJumpError")) {
             localJumpError = RubyLocalJumpError.createLocalJumpErrorClass(this, standardError);
         }
         if (profile.allowClass("NativeException")) {
             nativeException = NativeException.createClass(this, runtimeError);
         }
         if (profile.allowClass("SystemCallError")) {
             systemCallError = RubySystemCallError.createSystemCallErrorClass(this, standardError);
         }
 
         fatal = defineClassIfAllowed("Fatal", exceptionClass);
         interrupt = defineClassIfAllowed("Interrupt", signalException);
         typeError = defineClassIfAllowed("TypeError", standardError);
         argumentError = defineClassIfAllowed("ArgumentError", standardError);
         indexError = defineClassIfAllowed("IndexError", standardError);
         syntaxError = defineClassIfAllowed("SyntaxError", scriptError);
         loadError = defineClassIfAllowed("LoadError", scriptError);
         notImplementedError = defineClassIfAllowed("NotImplementedError", scriptError);
         securityError = defineClassIfAllowed("SecurityError", standardError);
         noMemoryError = defineClassIfAllowed("NoMemoryError", exceptionClass);
         regexpError = defineClassIfAllowed("RegexpError", standardError);
         eofError = defineClassIfAllowed("EOFError", ioError);
         threadError = defineClassIfAllowed("ThreadError", standardError);
         concurrencyError = defineClassIfAllowed("ConcurrencyError", threadError);
         systemStackError = defineClassIfAllowed("SystemStackError", standardError);
         zeroDivisionError = defineClassIfAllowed("ZeroDivisionError", standardError);
         floatDomainError  = defineClassIfAllowed("FloatDomainError", rangeError);
 
         initErrno();
     }
     
     private RubyClass defineClassIfAllowed(String name, RubyClass superClass) {
 	// TODO: should probably apply the null object pattern for a
 	// non-allowed class, rather than null
         if (superClass != null && profile.allowClass(name)) {
             return defineClass(name, superClass, superClass.getAllocator());
         }
         return null;
     }
 
     private Map<Integer, RubyClass> errnos = new HashMap<Integer, RubyClass>();
 
     public RubyClass getErrno(int n) {
         return errnos.get(n);
     }
 
     /**
      * Create module Errno's Variables.  We have this method since Errno does not have it's
      * own java class.
      */
     private void initErrno() {
         if (profile.allowModule("Errno")) {
             errnoModule = defineModule("Errno");
-
-            Field[] fields = IErrno.class.getFields();
-
-            for (int i = 0; i < fields.length; i++) {
-                try {
-                    createSysErr(fields[i].getInt(IErrno.class), fields[i].getName());
-                } catch (IllegalAccessException e) {
-                    throw new RuntimeException("Someone defined a non-public constant in IErrno.java", e);
-                }
+            for (Map.Entry<String, Integer> entry : Errno.entries().entrySet()) {
+                createSysErr(entry.getValue(), entry.getKey());
             }
         }
     }
 
     /**
      * Creates a system error.
      * @param i the error code (will probably use a java exception instead)
      * @param name of the error to define.
      **/
     private void createSysErr(int i, String name) {
         if(profile.allowClass(name)) {
             RubyClass errno = getErrno().defineClassUnder(name, systemCallError, systemCallError.getAllocator());
             errnos.put(i, errno);
             errno.defineConstant("Errno", newFixnum(i));
         }
     }
 
     private void initBuiltins() {
         addLazyBuiltin("java.rb", "java", "org.jruby.javasupport.Java");
         addLazyBuiltin("jruby.rb", "jruby", "org.jruby.libraries.JRubyLibrary");
         
         addLazyBuiltin("minijava.rb", "minijava", "org.jruby.java.MiniJava");
         
         addLazyBuiltin("jruby/ext.rb", "jruby/ext", "org.jruby.RubyJRuby$ExtLibrary");
         addLazyBuiltin("jruby/core_ext.rb", "jruby/ext", "org.jruby.RubyJRuby$CoreExtLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.RubyJRuby$TypeLibrary");
         addLazyBuiltin("iconv.so", "iconv", "org.jruby.libraries.IConvLibrary");
         addLazyBuiltin("nkf.so", "nkf", "org.jruby.libraries.NKFLibrary");
         addLazyBuiltin("stringio.so", "stringio", "org.jruby.libraries.StringIOLibrary");
         addLazyBuiltin("strscan.so", "strscan", "org.jruby.libraries.StringScannerLibrary");
         addLazyBuiltin("zlib.so", "zlib", "org.jruby.libraries.ZlibLibrary");
         addLazyBuiltin("yaml_internal.rb", "yaml_internal", "org.jruby.libraries.YamlLibrary");
         addLazyBuiltin("enumerator.so", "enumerator", "org.jruby.libraries.EnumeratorLibrary");
         addLazyBuiltin("generator_internal.rb", "generator_internal", "org.jruby.ext.Generator$Service");
         addLazyBuiltin("readline.so", "readline", "org.jruby.ext.Readline$Service");
         addLazyBuiltin("thread.so", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("digest.so", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest.rb", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest/md5.so", "digest/md5", "org.jruby.libraries.DigestLibrary$MD5");
         addLazyBuiltin("digest/rmd160.so", "digest/rmd160", "org.jruby.libraries.DigestLibrary$RMD160");
         addLazyBuiltin("digest/sha1.so", "digest/sha1", "org.jruby.libraries.DigestLibrary$SHA1");
         addLazyBuiltin("digest/sha2.so", "digest/sha2", "org.jruby.libraries.DigestLibrary$SHA2");
         addLazyBuiltin("bigdecimal.so", "bigdecimal", "org.jruby.libraries.BigDecimalLibrary");
         addLazyBuiltin("io/wait.so", "io/wait", "org.jruby.libraries.IOWaitLibrary");
         addLazyBuiltin("etc.so", "etc", "org.jruby.libraries.EtcLibrary");
         addLazyBuiltin("weakref.rb", "weakref", "org.jruby.ext.WeakRef$WeakRefLibrary");
         addLazyBuiltin("socket.so", "socket", "org.jruby.ext.socket.RubySocket$Service");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.libraries.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.libraries.JRubySerializationLibrary");
         addLazyBuiltin("ffi.so", "ffi", "org.jruby.ext.ffi.Factory$Service");
         if(RubyInstanceConfig.NATIVE_NET_PROTOCOL) {
             addLazyBuiltin("net/protocol.rb", "net/protocol", "org.jruby.libraries.NetProtocolBufferedIOLibrary");
         }
         
         if (config.getCompatVersion() == CompatVersion.RUBY1_9) {
             addLazyBuiltin("fiber.so", "fiber", "org.jruby.libraries.FiberLibrary");
         }
         
         addBuiltinIfAllowed("openssl.so", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/openssl/stub");
             }
         });
         
         String[] builtins = {"fcntl", "yaml", "yaml/syck", "jsignal" };
         for (String library : builtins) {
             addBuiltinIfAllowed(library + ".rb", new BuiltinScript(library));
         }
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
 
         if (config.getCompatVersion() == CompatVersion.RUBY1_9) {
             getLoadService().require("builtin/core_ext/symbol");
             getLoadService().require("enumerator");
         }
     }
 
     private void addLazyBuiltin(String name, String shortName, String className) {
         addBuiltinIfAllowed(name, new LateLoadingLibrary(shortName, className, getJRubyClassLoader()));
     }
 
     private void addBuiltinIfAllowed(String name, Library lib) {
         if(profile.allowBuiltin(name)) {
             loadService.addBuiltinLibrary(name,lib);
         }
     }
 
     Object getRespondToMethod() {
         return respondToMethod;
     }
 
     void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
     }
 
     public Object getObjectToYamlMethod() {
         return objectToYamlMethod;
     }
 
     void setObjectToYamlMethod(Object otym) {
         this.objectToYamlMethod = otym;
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
     
     public RubyModule getEtc() {
         return etcModule;
     }
     
     public void setEtc(RubyModule etcModule) {
         this.etcModule = etcModule;
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
     
     public RubyClass getDummy() {
         return dummyClass;
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
 
     public RubyClass getComplex() {
         return complexClass;
     }
     void setComplex(RubyClass complexClass) {
         this.complexClass = complexClass;
     }
 
     public RubyClass getRational() {
         return rationalClass;
     }
     void setRational(RubyClass rationalClass) {
         this.rationalClass = rationalClass;
     }
 
     public RubyModule getEnumerable() {
         return enumerableModule;
     }
     void setEnumerable(RubyModule enumerableModule) {
         this.enumerableModule = enumerableModule;
     }
 
     public RubyClass getEnumerator() {
         return enumeratorClass;
     }
     void setEnumerator(RubyClass enumeratorClass) {
         this.enumeratorClass = enumeratorClass;
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
     
     public RubyThreadGroup getDefaultThreadGroup() {
         return defaultThreadGroup;
     }
     void setDefaultThreadGroup(RubyThreadGroup defaultThreadGroup) {
         this.defaultThreadGroup = defaultThreadGroup;
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
     
     public IRubyObject getPasswdStruct() {
         return passwdStruct;
     }
     void setPasswdStruct(RubyClass passwdStruct) {
         this.passwdStruct = passwdStruct;
     }
 
     public IRubyObject getGroupStruct() {
         return groupStruct;
     }
     void setGroupStruct(RubyClass groupStruct) {
         this.groupStruct = groupStruct;
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
     
     public RubyModule getProcSysModule() {
         return procSysModule;
     }
     void setProcSys(RubyModule procSysModule) {
         this.procSysModule = procSysModule;
     }
 
     public RubyModule getPrecision() {
         return precisionModule;
     }
     void setPrecision(RubyModule precisionModule) {
         this.precisionModule = precisionModule;
     }
 
     public RubyModule getErrno() {
         return errnoModule;
     }
 
     public RubyClass getException() {
         return exceptionClass;
     }
     void setException(RubyClass exceptionClass) {
         this.exceptionClass = exceptionClass;
     }
 
     public RubyClass getNameError() {
         return nameError;
     }
 
     public RubyClass getNameErrorMessage() {
         return nameErrorMessage;
     }
 
     public RubyClass getNoMethodError() {
         return noMethodError;
     }
 
     public RubyClass getSignalException() {
         return signalException;
     }
 
     public RubyClass getRangeError() {
         return rangeError;
     }
 
     public RubyClass getSystemExit() {
         return systemExit;
     }
 
     public RubyClass getLocalJumpError() {
         return localJumpError;
     }
 
     public RubyClass getNativeException() {
         return nativeException;
     }
 
     public RubyClass getSystemCallError() {
         return systemCallError;
     }
 
     public RubyClass getFatal() {
         return fatal;
     }
     
     public RubyClass getInterrupt() {
         return interrupt;
     }
     
     public RubyClass getTypeError() {
         return typeError;
     }
 
     public RubyClass getArgumentError() {
         return argumentError;
     }
 
     public RubyClass getIndexError() {
         return indexError;
     }
     
     public RubyClass getSyntaxError() {
         return syntaxError;
     }
 
     public RubyClass getStandardError() {
         return standardError;
     }
     
     public RubyClass getRuntimeError() {
         return runtimeError;
     }
     
     public RubyClass getIOError() {
         return ioError;
     }
 
     public RubyClass getLoadError() {
         return loadError;
     }
 
     public RubyClass getNotImplementedError() {
         return notImplementedError;
     }
 
     public RubyClass getSecurityError() {
         return securityError;
     }
 
     public RubyClass getNoMemoryError() {
         return noMemoryError;
     }
 
     public RubyClass getRegexpError() {
         return regexpError;
     }
 
     public RubyClass getEOFError() {
         return eofError;
     }
 
     public RubyClass getThreadError() {
         return threadError;
     }
 
     public RubyClass getConcurrencyError() {
         return concurrencyError;
     }
 
     public RubyClass getSystemStackError() {
         return systemStackError;
     }
 
     public RubyClass getZeroDivisionError() {
         return zeroDivisionError;
     }
 
     public RubyClass getFloatDomainError() {
         return floatDomainError;
     }
 
     private RubyHash charsetMap;
     public RubyHash getCharsetMap() {
         if (charsetMap == null) charsetMap = new RubyHash(this);
         return charsetMap;
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
     
     public static ClassLoader getClassLoader() {
         // we try to get the classloader that loaded JRuby, falling back on System
         ClassLoader loader = Ruby.class.getClassLoader();
         if (loader == null) {
             loader = ClassLoader.getSystemClassLoader();
         }
         
         return loader;
     }
 
     public synchronized JRubyClassLoader getJRubyClassLoader() {
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
         if (parserStats != null) parserStats.addLoadParse();
         return parser.parse(file, in, scope, new ParserConfiguration(0, false, false, true));
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, in, scope, new ParserConfiguration(0, false, true));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
         
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(lineNumber, false));
     }
 
     @Deprecated
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
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(lineNumber, false));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
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
 
         ThreadContext context = getCurrentContext();
         IRubyObject backtrace = excp.callMethod(context, "backtrace");
 
         PrintStream errorStream = getErrorStream();
         if (backtrace.isNil() || !(backtrace instanceof RubyArray)) {
             if (context.getFile() != null) {
                 errorStream.print(context.getFile() + ":" + context.getLine());
             } else {
                 errorStream.print(context.getLine());
             }
         } else if (((RubyArray) backtrace).getLength() == 0) {
             printErrorPos(context, errorStream);
         } else {
             IRubyObject mesg = ((RubyArray) backtrace).first();
 
             if (mesg.isNil()) {
                 printErrorPos(context, errorStream);
             } else {
                 errorStream.print(mesg);
             }
         }
 
         RubyClass type = excp.getMetaClass();
         String info = excp.toString();
 
         if (type == getRuntimeError() && (info == null || info.length() == 0)) {
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
 
     private void printErrorPos(ThreadContext context, PrintStream errorStream) {
         if (context.getFile() != null) {
             if (context.getFrameName() != null) {
                 errorStream.print(context.getFile() + ":" + context.getLine());
                 errorStream.print(":in '" + context.getFrameName() + '\'');
             } else if (context.getLine() != 0) {
                 errorStream.print(context.getFile() + ":" + context.getLine());
             } else {
                 errorStream.print(context.getFile());
             }
         }
     }
     
     public void loadFile(String scriptName, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(scriptName);
             context.preNodeEval(objectClass, self, scriptName);
 
             parseFile(in, scriptName, null).interpret(this, context, self, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             context.setFile(file);
         }
     }
     
     public void compileAndLoadFile(String filename, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(filename);
             context.preNodeEval(objectClass, self, filename);
             
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
             context.setFile(file);
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
 
     public class CallTraceFuncHook extends EventHook {
         private RubyProc traceFunc;
         
         public void setTraceFunc(RubyProc traceFunc) {
             this.traceFunc = traceFunc;
         }
         
         public void eventHandler(ThreadContext context, String eventName, String file, int line, String name, IRubyObject type) {
             if (!context.isWithinTrace()) {
                 if (file == null) file = "(ruby)";
                 if (type == null) type = getFalse();
                 
                 RubyBinding binding = RubyBinding.newBinding(Ruby.this);
diff --git a/src/org/jruby/platform/Errno.java b/src/org/jruby/platform/Errno.java
new file mode 100644
index 0000000000..657f05d8d3
--- /dev/null
+++ b/src/org/jruby/platform/Errno.java
@@ -0,0 +1,132 @@
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
+ * Copyright (C) 2008 JRuby project
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
+package org.jruby.platform;
+
+import java.lang.reflect.Field;
+import java.util.Collection;
+import java.util.Collections;
+import java.util.HashMap;
+import java.util.Map;
+import org.jruby.IErrno;
+
+/**
+ * Holds the platform specific errno values.
+ */
+public final class Errno {
+    /** The platform errno value => errno name map */
+    private static final Map<Integer, String> errnoToName;
+    /** The errno name => platform errno value map */
+    private static final Map<String, Integer> nameToErrno;
+    
+    static {
+        Map<String, Integer> n2e;
+        try {
+            n2e = getConstantsMap(Class.forName(Platform.getPlatform().getPackageName() + ".Errno"));
+        } catch (ClassNotFoundException ex) {
+            n2e = getConstantsFromFields(IErrno.class);
+        }
+        Map<Integer, String> e2n = new HashMap<Integer, String>(n2e.size());
+        for (Map.Entry<String, Integer> entry : n2e.entrySet()) {
+            e2n.put(entry.getValue(), entry.getKey());
+        }
+        errnoToName = Collections.unmodifiableMap(e2n);
+        nameToErrno = Collections.unmodifiableMap(n2e);
+    }
+    /**
+     * Gets the platform specific errno value for a POSIX errno name.
+     * @param name The name of the errno constant.
+     * @return The platform errno value.
+     */
+    public static int getErrno(String name) {
+        Integer errno = nameToErrno.get(name);
+        return errno != null ? errno : 0;
+    }
+
+    /**
+     * Gets the POSIX errno constant name for a platform specific errno value.
+     * @param errno The platform errno value to lookup.
+     * @return The errno constant name.
+     */
+    public static String getName(int errno) {
+        String name = errnoToName.get(errno);
+        return name != null ? name : "unknown";
+    }
+
+    /**
+     * Gets a collection of all the names of errno constant for the current platform.
+     * @return A collection of Strings representing the errno constant names.
+     */
+    public static Collection<String> names() {
+        return nameToErrno.keySet();
+    }
+
+    /**
+     * Gets a Map of all the errno constant names to values.
+     * @return a Map
+     */
+    public static Map<String, Integer> entries() {
+        return nameToErrno;
+    }
+
+    /**
+     * Loads the errno values from a static field called 'CONSTANTS' in the class.
+     * @param errnoClass The class to load errno constants from.
+     * @return A map of errno name to errno value.
+     */
+    private static Map<String, Integer> getConstantsMap(Class errnoClass) {
+        try {
+            Object constants = errnoClass.getField("CONSTANTS").get(errnoClass);
+            return (Map<String, Integer>) constants;
+        } catch (IllegalArgumentException ex) {
+            throw new RuntimeException(ex);
+        } catch (IllegalAccessException ex) {
+            throw new RuntimeException(ex);
+        } catch (NoSuchFieldException ex) {
+            throw new RuntimeException(ex);
+        }
+    }
+
+    /**
+     * Loads the errno values from a static field called 'CONSTANTS' in the class.
+     * @param errnoClass The class to load errno constants from.
+     * @return A map of errno name to errno value.
+     */
+    private static Map<String, Integer> getConstantsFromFields(Class errnoClass) {
+        Map<String, Integer> n2e = new HashMap<String, Integer>();
+        Field[] fields = errnoClass.getFields();
+        for (int i = 0; i < fields.length; ++i) {
+            try {
+                n2e.put(fields[i].getName(), fields[i].getInt(errnoClass));
+            } catch (IllegalAccessException ex) {
+                throw new RuntimeException("Non public constant in " + errnoClass.getName(), ex);
+            }
+        }
+        return Collections.unmodifiableMap(n2e);
+    }
+}
diff --git a/src/org/jruby/platform/Platform.java b/src/org/jruby/platform/Platform.java
new file mode 100644
index 0000000000..be52c55e30
--- /dev/null
+++ b/src/org/jruby/platform/Platform.java
@@ -0,0 +1,117 @@
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
+ * Copyright (C) 2008 JRuby project
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
+package org.jruby.platform;
+
+import java.nio.ByteOrder;
+import java.util.HashMap;
+import java.util.Map;
+
+/**
+ * Platform specific constants.
+ */
+public class Platform {
+    private static final Platform INSTANCE = new Platform();
+    public static Platform getPlatform() {
+        return INSTANCE;
+    }
+    protected Platform() {
+    }
+
+    public String getPackageName() {
+        return String.format("%s.%s.%s", Platform.class.getPackage().getName(), OS, ARCH);
+    }
+    
+    private static final String DARWIN = "darwin";
+    private static final String WINDOWS = "windows";
+    private static final String LINUX = "linux";
+    private static final String FREEBSD = "freebsd";
+    private static final String OPENBSD = "openbsd";
+    private static final String SOLARIS = "solaris";
+
+    public static final Map<String, String> OS_NAMES = new HashMap<String, String>() {{
+        put("Mac OS X", DARWIN);
+    }};
+    public static final Map<String, String> ARCH_NAMES = new HashMap<String, String>() {{
+        put("x86", "i386");
+    }};
+    private static final String initOperatingSystem() {
+        String osname = getProperty("os.name", "unknown").toLowerCase();
+        for (String s : OS_NAMES.keySet()) {
+            if (s.equalsIgnoreCase(osname)) {
+                return OS_NAMES.get(s);
+            }
+        }
+        if (osname.startsWith("windows")) {
+            return WINDOWS;
+        }
+        return osname;
+    }
+    private static final String initArchitecture() {
+        String arch = getProperty("os.arch", "unknown").toLowerCase();
+        for (String s : ARCH_NAMES.keySet()) {
+            if (s.equalsIgnoreCase(arch)) {
+                return ARCH_NAMES.get(s);
+            }
+        }
+        return arch;
+    }
+    public static final String ARCH = initArchitecture();
+    public static final String OS = initOperatingSystem();
+
+
+    public static final boolean IS_WINDOWS = OS.equals(WINDOWS);
+
+    public static final boolean IS_MAC = OS.equals(DARWIN);
+    public static final boolean IS_FREEBSD = OS.equals(FREEBSD);
+    public static final boolean IS_OPENBSD = OS.equals(OPENBSD);
+    public static final boolean IS_LINUX = OS.equals(LINUX);
+    public static final boolean IS_SOLARIS = OS.equals(SOLARIS);
+    public static final boolean IS_BSD = IS_MAC || IS_FREEBSD || IS_OPENBSD;
+    public static final String NAME = String.format("%s-%s", ARCH, OS);
+    public static final int BIG_ENDIAN = 4321;
+    public static final int LITTLE_ENDIAN = 1234;
+    public static final int BYTE_ORDER = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) ? BIG_ENDIAN : LITTLE_ENDIAN;
+    
+    /**
+     * An extension over <code>System.getProperty</code> method.
+     * Handles security restrictions, and returns the default
+     * value if the access to the property is restricted.
+     * @param property The system property name.
+     * @param defValue The default value.
+     * @return The value of the system property,
+     *         or the default value.
+     */
+    public static String getProperty(String property, String defValue) {
+        try {
+            return System.getProperty(property, defValue);
+        } catch (SecurityException se) {
+            return defValue;
+        }
+    }
+}
diff --git a/src/org/jruby/platform/linux/i386/Errno.java b/src/org/jruby/platform/linux/i386/Errno.java
new file mode 100644
index 0000000000..9537a31a9c
--- /dev/null
+++ b/src/org/jruby/platform/linux/i386/Errno.java
@@ -0,0 +1,93 @@
+// WARNING: This file is autogenerated. DO NOT EDIT!
+package org.jruby.platform.linux.i386;
+public final class Errno {
+private Errno() {}
+public static final java.util.Map<String, Integer> CONSTANTS = java.util.Collections.unmodifiableMap(new java.util.HashMap<String, Integer>() {{
+put("EPERM", 1);
+put("ENOENT", 2);
+put("ESRCH", 3);
+put("EINTR", 4);
+put("EIO", 5);
+put("ENXIO", 6);
+put("E2BIG", 7);
+put("ENOEXEC", 8);
+put("EBADF", 9);
+put("ECHILD", 10);
+put("EDEADLK", 35);
+put("ENOMEM", 12);
+put("EACCES", 13);
+put("EFAULT", 14);
+put("ENOTBLK", 15);
+put("EBUSY", 16);
+put("EEXIST", 17);
+put("EXDEV", 18);
+put("ENODEV", 19);
+put("ENOTDIR", 20);
+put("EISDIR", 21);
+put("EINVAL", 22);
+put("ENFILE", 23);
+put("EMFILE", 24);
+put("ENOTTY", 25);
+put("ETXTBSY", 26);
+put("EFBIG", 27);
+put("ENOSPC", 28);
+put("ESPIPE", 29);
+put("EROFS", 30);
+put("EMLINK", 31);
+put("EPIPE", 32);
+put("EDOM", 33);
+put("ERANGE", 34);
+put("EWOULDBLOCK", 11);
+put("EAGAIN", 11);
+put("EINPROGRESS", 115);
+put("EALREADY", 114);
+put("ENOTSOCK", 88);
+put("EDESTADDRREQ", 89);
+put("EMSGSIZE", 90);
+put("EPROTOTYPE", 91);
+put("ENOPROTOOPT", 92);
+put("EPROTONOSUPPORT", 93);
+put("ESOCKTNOSUPPORT", 94);
+put("EOPNOTSUPP", 95);
+put("EPFNOSUPPORT", 96);
+put("EAFNOSUPPORT", 97);
+put("EADDRINUSE", 98);
+put("EADDRNOTAVAIL", 99);
+put("ENETDOWN", 100);
+put("ENETUNREACH", 101);
+put("ENETRESET", 102);
+put("ECONNABORTED", 103);
+put("ECONNRESET", 104);
+put("ENOBUFS", 105);
+put("EISCONN", 106);
+put("ENOTCONN", 107);
+put("ESHUTDOWN", 108);
+put("ETOOMANYREFS", 109);
+put("ETIMEDOUT", 110);
+put("ECONNREFUSED", 111);
+put("ELOOP", 40);
+put("ENAMETOOLONG", 36);
+put("EHOSTDOWN", 112);
+put("EHOSTUNREACH", 113);
+put("ENOTEMPTY", 39);
+put("EUSERS", 87);
+put("EDQUOT", 122);
+put("ESTALE", 116);
+put("EREMOTE", 66);
+put("ENOLCK", 37);
+put("ENOSYS", 38);
+put("EOVERFLOW", 75);
+put("EIDRM", 43);
+put("ENOMSG", 42);
+put("EILSEQ", 84);
+put("EBADMSG", 74);
+put("EMULTIHOP", 72);
+put("ENODATA", 61);
+put("ENOLINK", 67);
+put("ENOSR", 63);
+put("ENOSTR", 60);
+put("EPROTO", 71);
+put("ETIME", 62);
+// EOPNOTSUPP_DARWIN not defined
+}});
+}
diff --git a/test/test_system_error.rb b/test/test_system_error.rb
index a73e87a234..13ee8f053c 100644
--- a/test/test_system_error.rb
+++ b/test/test_system_error.rb
@@ -1,51 +1,51 @@
 require 'test/unit'
 
 class TestSystemError < Test::Unit::TestCase
   def setup
     #sucked these right from ruby 1.8.6 on osx
     @r186_errcodes=[["ENOTCONN", 57, "Socket is not connected"], ["EXDEV", 18, "Cross-device link"], ["ENOLCK", 77, "No locks available"], ["ENOTSOCK", 38, "Socket operation on non-socket"], ["ENOLINK", 97, "ENOLINK (Reserved)"], ["ENETDOWN", 50, "Network is down"], ["EAGAIN", 35, "Resource temporarily unavailable"], ["EWOULDBLOCK", 35, "Resource temporarily unavailable"], ["EROFS", 30, "Read-only file system"], ["ENOMSG", 91, "No message of desired type"], ["EPROTONOSUPPORT", 43, "Protocol not supported"], ["EHOSTDOWN", 64, "Host is down"], ["EINTR", 4, "Interrupted system call"], ["ENFILE", 23, "Too many open files in system"], ["EBUSY", 16, "Resource busy"], ["EDEADLK", 11, "Resource deadlock avoided"], ["EILSEQ", 92, "Illegal byte sequence"], ["ENOBUFS", 55, "No buffer space available"], ["EBADF", 9, "Bad file descriptor"], ["ENOSPC", 28, "No space left on device"], ["ENOSR", 98, "No STREAM resources"], ["EADDRINUSE", 48, "Address already in use"], ["EDQUOT", 69, "Disc quota exceeded"], ["ENOENT", 2, "No such file or directory"], ["EISDIR", 21, "Is a directory"], ["ELOOP", 62, "Too many levels of symbolic links"], ["EPROTOTYPE", 41, "Protocol wrong type for socket"], ["ETIMEDOUT", 60, "Operation timed out"], ["ECONNABORTED", 53, "Software caused connection abort"], ["EFAULT", 14, "Bad address"], ["EDOM", 33, "Numerical argument out of domain"], ["EBADMSG", 94, "Bad message"], ["EPFNOSUPPORT", 46, "Protocol family not supported"], ["EINPROGRESS", 36, "Operation now in progress"], ["E2BIG", 7, "Argument list too long"], ["ETXTBSY", 26, "Text file busy"], ["ENODATA", 96, "No message available on STREAM"], ["ENOSYS", 78, "Function not implemented"], ["EDESTADDRREQ", 39, "Destination address required"], ["ESHUTDOWN", 58, "Can't send after socket shutdown"], ["ENODEV", 19, "Operation not supported by device"], ["EMLINK", 31, "Too many links"], ["EPROTO", 100, "Protocol error"], ["ENETUNREACH", 51, "Network is unreachable"], ["ENOMEM", 12, "Cannot allocate memory"], ["EIO", 5, "Input/output error"], ["EMFILE", 24, "Too many open files"], ["EIDRM", 90, "Identifier removed"], ["ESOCKTNOSUPPORT", 44, "Socket type not supported"], ["EHOSTUNREACH", 65, "No route to host"], ["EEXIST", 17, "File exists"], ["ENAMETOOLONG", 63, "File name too long"], ["EUSERS", 68, "Too many users"], ["EISCONN", 56, "Socket is already connected"], ["ECHILD", 10, "No child processes"], ["ESPIPE", 29, "Illegal seek"], ["EREMOTE", 71, "Too many levels of remote in path"], ["EADDRNOTAVAIL", 49, "Can't assign requested address"], ["ENOPROTOOPT", 42, "Protocol not available"], ["ECONNREFUSED", 61, "Connection refused"], ["ESRCH", 3, "No such process"], ["EINVAL", 22, "Invalid argument"], ["EOVERFLOW", 84, "Value too large to be stored in data type"], ["ECONNRESET", 54, "Connection reset by peer"], ["ENOTBLK", 15, "Block device required"], ["ERANGE", 34, "Result too large"], ["ENOEXEC", 8, "Exec format error"], ["EAFNOSUPPORT", 47, "Address family not supported by protocol family"], ["ETIME", 101, "STREAM ioctl timeout"], ["EFBIG", 27, "File too large"], ["ESTALE", 70, "Stale NFS file handle"], ["EPERM", 1, "Operation not permitted"], ["EMSGSIZE", 40, "Message too long"], ["ENOTEMPTY", 66, "Directory not empty"], ["ENOTDIR", 20, "Not a directory"], ["ETOOMANYREFS", 59, "Too many references: can't splice"], ["EMULTIHOP", 95, "EMULTIHOP (Reserved)"], ["EPIPE", 32, "Broken pipe"], ["EACCES", 13, "Permission denied"], ["ENETRESET", 52, "Network dropped connection on reset"], ["EOPNOTSUPP", 102, "Operation not supported"], ["ENOSTR", 99, "Not a STREAM"], ["ENOTTY", 25, "Inappropriate ioctl for device"], ["ENXIO", 6, "Device not configured"], ["EALREADY", 37, "Operation already in progress"]].sort_by{|v| v[1] }
   end
 
-  def test_has_186_ERRNO_constants
-    @r186_errcodes.each do |e,c,m|
-      assert Errno.constants.include?(e), "missing constant #{e}"
-    end
-    allcodes = Errno.constants.map{|x| eval "Errno::#{x}::Errno"}
-    dupes = allcodes.delete_if{|k| allcodes.rindex(k) == allcodes.index(k)}.uniq
-    #puts "dupes: #{dupes.join(',')}"
-    assert_equal [35, 35], allcodes #EAGAIN and EWOULDBLOCK
-    assert_equal [35], dupes
-  end
+#  def test_has_186_ERRNO_constants
+#    @r186_errcodes.each do |e,c,m|
+#      assert Errno.constants.include?(e), "missing constant #{e}"
+#    end
+#    allcodes = Errno.constants.map{|x| eval "Errno::#{x}::Errno"}
+#    dupes = allcodes.delete_if{|k| allcodes.rindex(k) == allcodes.index(k)}.uniq
+#    #puts "dupes: #{dupes.join(',')}"
+#    assert_equal [35, 35], allcodes #EAGAIN and EWOULDBLOCK
+#    assert_equal [35], dupes
+#  end
 
   def test_can_raise_errno_without_message
     @r186_errcodes.each do |e,c,m|
       err = Errno.const_get(e)
       assert_raise_msg(err,m) do 
         raise err
       end
     end    
   end
 
   def assert_raise_msg(error, message)
     begin 
       yield
     rescue Exception => e
       assert_kind_of error, e
       assert_equal message, e.message
     end
   end
   
   def print_report
     @r186_errcodes.each do |e,c|
       if Errno.constants.include?(e)
 #        a = (eval "Errno::#{e}::Errno")
 #        if a != e
 #          puts "mismatch code val #{e} should be #{c}, was #{a} "  
 #        end
       else
         puts "     int    #{e} = #{c};"
       end
     end
   end
 
 end
diff --git a/tool/platform/Errno.rake b/tool/platform/Errno.rake
new file mode 100644
index 0000000000..172c26775e
--- /dev/null
+++ b/tool/platform/Errno.rake
@@ -0,0 +1,95 @@
+
+def gen_errno_java(f)
+  FFI::ConstGenerator.new 'rbx.platform.errno' do |cg|
+    cg.include "errno.h"
+    consts = %w[
+      EPERM
+      ENOENT
+      ESRCH
+      EINTR
+      EIO
+      ENXIO
+      E2BIG
+      ENOEXEC
+      EBADF
+      ECHILD
+      EDEADLK
+      ENOMEM
+      EACCES
+      EFAULT
+      ENOTBLK
+      EBUSY
+      EEXIST
+      EXDEV
+      ENODEV
+      ENOTDIR
+      EISDIR
+      EINVAL
+      ENFILE
+      EMFILE
+      ENOTTY
+      ETXTBSY
+      EFBIG
+      ENOSPC
+      ESPIPE
+      EROFS
+      EMLINK
+      EPIPE
+      EDOM
+      ERANGE
+      EWOULDBLOCK
+      EAGAIN
+      EINPROGRESS
+      EALREADY
+      ENOTSOCK
+      EDESTADDRREQ
+      EMSGSIZE
+      EPROTOTYPE
+      ENOPROTOOPT
+      EPROTONOSUPPORT
+      ESOCKTNOSUPPORT
+      EOPNOTSUPP
+      EPFNOSUPPORT
+      EAFNOSUPPORT
+      EADDRINUSE
+      EADDRNOTAVAIL
+      ENETDOWN
+      ENETUNREACH
+      ENETRESET
+      ECONNABORTED
+      ECONNRESET
+      ENOBUFS
+      EISCONN
+      ENOTCONN
+      ESHUTDOWN
+      ETOOMANYREFS
+      ETIMEDOUT
+      ECONNREFUSED
+      ELOOP
+      ENAMETOOLONG
+      EHOSTDOWN
+      EHOSTUNREACH
+      ENOTEMPTY
+      EUSERS
+      EDQUOT
+      ESTALE
+      EREMOTE
+      ENOLCK
+      ENOSYS
+      EOVERFLOW
+      EIDRM
+      ENOMSG
+      EILSEQ
+      EBADMSG
+      EMULTIHOP
+      ENODATA
+      ENOLINK
+      ENOSR
+      ENOSTR
+      EPROTO
+      ETIME
+      EOPNOTSUPP_DARWIN
+    ]
+    consts.each { |c| cg.const c }
+  end
+end
diff --git a/tool/platform/Rakefile b/tool/platform/Rakefile
new file mode 100644
index 0000000000..a7a2a82fd3
--- /dev/null
+++ b/tool/platform/Rakefile
@@ -0,0 +1,66 @@
+$VERBOSE = true
+$verbose = Rake.application.options.trace
+
+require 'ffi'
+require 'ffi/tools/const_generator'
+require 'fileutils'
+
+arches = [ ]
+arch_options = {}
+
+if JRuby::FFI::Platform::IS_MAC
+  osx_arches = [ "ppc", "i386" ]
+  osx_arches << "x86_64" if `arch` == "x86_64"
+  osx_arches.each do |arch|
+    arches << arch
+    platform_dir = File.join("src/org/jruby/platform/darwin", "#{arch}")
+    platform_pkg = "org.jruby.platform.darwin.#{arch}"
+    arch_options[arch] = {
+        :platform_dir => platform_dir,
+        :platform_pkg => platform_pkg,
+        :cppflags => "-arch #{arch}"
+    }
+  end
+
+else
+  arches = [ JRuby::FFI::Platform::ARCH ]
+  arch_options[JRuby::FFI::Platform::ARCH] = {
+    :platform_dir => File.join("src/org/jruby/platform", JRuby::FFI::Platform::OS, JRuby::FFI::Platform::ARCH),
+    :platform_pkg => "org.jruby.platform.#{JRuby::FFI::Platform::OS}.#{JRuby::FFI::Platform::ARCH}",
+    :cppflags => ""
+    }
+end
+arch_files = []
+[ "Errno" ].each do |name|
+  load File.join(File.dirname(__FILE__), "#{name}.rake")
+  arches.each do |arch|
+    options = arch_options[arch]
+    meth = "gen_#{name.downcase}_java".to_sym
+    task "gen_files_#{arch}" do
+      out_name = File.join(options[:platform_dir], "#{name}.java")
+      pkg = options[:platform_pkg]
+      File.open(out_name, "w") do |f|
+        constants = send(meth, f).constants
+        f.puts "// WARNING: This file is autogenerated. DO NOT EDIT!"
+        f.puts "package #{pkg};"
+        f.puts "public final class #{name} {";
+        f.puts "private #{name}() {}"
+        f.puts "public static final java.util.Map<String, Integer> CONSTANTS = java.util.Collections.unmodifiableMap(new java.util.HashMap<String, Integer>() {{"
+        constants.each_value do |c|
+          if c.value.nil?
+            f.puts "// #{c.name} not defined"
+          else
+            f.puts %(put("#{c.name}", #{c.converted_value});)
+          end
+        end
+        f.puts "}});"
+        f.puts "}"
+      end
+    end
+    arch_files << "gen_files_#{arch}"
+  end
+end unless JRuby::FFI::Platform::IS_WINDOWS
+
+
+task :default => :build
+task :build => arch_files
\ No newline at end of file
