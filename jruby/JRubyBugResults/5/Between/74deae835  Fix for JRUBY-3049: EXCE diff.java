diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 291421bdc5..211756af16 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1245 +1,1245 @@
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
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.lang.ref.WeakReference;
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
 import org.jruby.platform.Errno;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ConstantCacheMap;
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
     private ConstantCacheMap constantCacheMap;
     
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
         this.constantCacheMap = new ConstantCacheMap(this);
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
-            throw newLocalJumpError("return", (IRubyObject)rj.getValue(), "unexpected return");
+            throw newLocalJumpError(RubyLocalJumpError.Reason.RETURN, (IRubyObject)rj.getValue(), "unexpected return");
         } catch (JumpException.BreakJump bj) {
-            throw newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
+            throw newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
-            throw newLocalJumpError("redo", (IRubyObject)rj.getValue(), "unexpected redo");
+            throw newLocalJumpError(RubyLocalJumpError.Reason.REDO, (IRubyObject)rj.getValue(), "unexpected redo");
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
             for (Map.Entry<String, Integer> entry : Errno.entries().entrySet()) {
                 createSysErr(entry.getValue(), entry.getKey());
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
@@ -1675,1400 +1675,1400 @@ public final class Ruby {
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
 
                 context.preTrace();
                 try {
                     traceFunc.call(context, new IRubyObject[] {
                         newString(eventName), // event name
                         newString(file), // filename
                         newFixnum(line), // line numbers should be 1-based
                         name != null ? newSymbol(name) : getNil(),
                         binding,
                         type
                     });
                 } finally {
                     context.postTrace();
                 }
             }
         }
 
         public boolean isInterestedInEvent(RubyEvent event) {
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
     
     public void callEventHooks(ThreadContext context, RubyEvent event, String file, int line, String name, IRubyObject type) {
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
         int status = 0;
 
         while (!atExitBlocks.empty()) {
             RubyProc proc = atExitBlocks.pop();
             try {
                 proc.call(getCurrentContext(), IRubyObject.NULL_ARRAY);
             } catch (RaiseException rj) {
                 RubyException raisedException = rj.getException();
                 if (!getSystemExit().isInstance(raisedException)) {
                     status = 1;
                     printError(raisedException);
                 } else {
                     IRubyObject statusObj = raisedException.callMethod(
                             getCurrentContext(), "status");
                     if (statusObj != null && !statusObj.isNil()) {
                         status = RubyNumeric.fix2int(statusObj);
                     }
                 }
             }
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
 
         getThreadService().disposeCurrentThread();
 
         getBeanManager().unregisterCompiler();
         getBeanManager().unregisterConfig();
         getBeanManager().unregisterParserStats();
         getBeanManager().unregisterClassCache();
         getBeanManager().unregisterMethodCache();
 
         if (status != 0) {
             throw newSystemExit(status);
         }
     }
 
     // new factory methods ------------------------------------------------------------------------
 
     public RubyArray newEmptyArray() {
         return RubyArray.newEmptyArray(this);
     }
 
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
     
     public RubyFileStat newFileStat(FileDescriptor descriptor) {
         return RubyFileStat.newFileStat(this, descriptor);
     }
 
     public RubyFixnum newFixnum(long value) {
         return RubyFixnum.newFixnum(this, value);
     }
 
     public RubyFixnum newFixnum(int value) {
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
 
     public RubyProc newBlockPassProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, type);
         proc.initialize(getCurrentContext(), block);
 
         return proc;
     }
 
     public RubyBinding newBinding() {
         return RubyBinding.newBinding(this);
     }
 
     public RubyBinding newBinding(Binding binding) {
         return RubyBinding.newBinding(this, binding);
     }
 
     public RubyString newString() {
         return RubyString.newString(this, new ByteList());
     }
 
     public RubyString newString(String string) {
         return RubyString.newString(this, string);
     }
     
     public RubyString newString(ByteList byteList) {
         return RubyString.newString(this, byteList);
     }
 
     @Deprecated
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
         return newRaiseException(getRuntimeError(), message);
     }    
     
     public RaiseException newArgumentError(String message) {
         return newRaiseException(getArgumentError(), message);
     }
 
     public RaiseException newArgumentError(int got, int expected) {
         return newRaiseException(getArgumentError(), "wrong # of arguments(" + got + " for " + expected + ")");
     }
 
     public RaiseException newErrnoEBADFError() {
         return newRaiseException(getErrno().fastGetClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoENOPROTOOPTError() {
         return newRaiseException(getErrno().fastGetClass("ENOPROTOOPT"), "Protocol not available");
     }
 
     public RaiseException newErrnoEPIPEError() {
         return newRaiseException(getErrno().fastGetClass("EPIPE"), "Broken pipe");
     }
 
     public RaiseException newErrnoECONNREFUSEDError() {
         return newRaiseException(getErrno().fastGetClass("ECONNREFUSED"), "Connection refused");
     }
 
     public RaiseException newErrnoECONNRESETError() {
         return newRaiseException(getErrno().fastGetClass("ECONNRESET"), "Connection reset by peer");
     }
 
     public RaiseException newErrnoEADDRINUSEError() {
         return newRaiseException(getErrno().fastGetClass("EADDRINUSE"), "Address in use");
     }
 
     public RaiseException newErrnoEINVALError() {
         return newRaiseException(getErrno().fastGetClass("EINVAL"), "Invalid file");
     }
 
     public RaiseException newErrnoENOENTError() {
         return newRaiseException(getErrno().fastGetClass("ENOENT"), "File not found");
     }
 
     public RaiseException newErrnoEACCESError(String message) {
         return newRaiseException(getErrno().fastGetClass("EACCES"), message);
     }
 
     public RaiseException newErrnoEAGAINError(String message) {
         return newRaiseException(getErrno().fastGetClass("EAGAIN"), message);
     }
 
     public RaiseException newErrnoEISDirError() {
         return newRaiseException(getErrno().fastGetClass("EISDIR"), "Is a directory");
     }
 
     public RaiseException newErrnoESPIPEError() {
         return newRaiseException(getErrno().fastGetClass("ESPIPE"), "Illegal seek");
     }
 
     public RaiseException newErrnoEBADFError(String message) {
         return newRaiseException(getErrno().fastGetClass("EBADF"), message);
     }
 
     public RaiseException newErrnoEINVALError(String message) {
         return newRaiseException(getErrno().fastGetClass("EINVAL"), message);
     }
 
     public RaiseException newErrnoENOTDIRError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOTDIR"), message);
     }
 
     public RaiseException newErrnoENOTSOCKError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOTSOCK"), message);
     }
 
     public RaiseException newErrnoENOENTError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOENT"), message);
     }
 
     public RaiseException newErrnoESPIPEError(String message) {
         return newRaiseException(getErrno().fastGetClass("ESPIPE"), message);
     }
 
     public RaiseException newErrnoEEXISTError(String message) {
         return newRaiseException(getErrno().fastGetClass("EEXIST"), message);
     }
     
     public RaiseException newErrnoEDOMError(String message) {
         return newRaiseException(getErrno().fastGetClass("EDOM"), "Domain error - " + message);
     }   
     
     public RaiseException newErrnoECHILDError() {
         return newRaiseException(getErrno().fastGetClass("ECHILD"), "No child processes");
     }    
 
     public RaiseException newIndexError(String message) {
         return newRaiseException(getIndexError(), message);
     }
 
     public RaiseException newSecurityError(String message) {
         return newRaiseException(getSecurityError(), message);
     }
 
     public RaiseException newSystemCallError(String message) {
         return newRaiseException(getSystemCallError(), message);
     }
 
     public RaiseException newTypeError(String message) {
         return newRaiseException(getTypeError(), message);
     }
 
     public RaiseException newThreadError(String message) {
         return newRaiseException(getThreadError(), message);
     }
 
     public RaiseException newConcurrencyError(String message) {
         return newRaiseException(getConcurrencyError(), message);
     }
 
     public RaiseException newSyntaxError(String message) {
         return newRaiseException(getSyntaxError(), message);
     }
 
     public RaiseException newRegexpError(String message) {
         return newRaiseException(getRegexpError(), message);
     }
 
     public RaiseException newRangeError(String message) {
         return newRaiseException(getRangeError(), message);
     }
 
     public RaiseException newNotImplementedError(String message) {
         return newRaiseException(getNotImplementedError(), message);
     }
     
     public RaiseException newInvalidEncoding(String message) {
         return newRaiseException(fastGetClass("Iconv").fastGetClass("InvalidEncoding"), message);
     }
 
     public RaiseException newNoMethodError(String message, String name, IRubyObject args) {
         return new RaiseException(new RubyNoMethodError(this, getNoMethodError(), message, name, args), true);
     }
 
     public RaiseException newNameError(String message, String name) {
         return newNameError(message, name, null);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException) {
         return newNameError(message, name, origException, true);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException, boolean printWhenVerbose) {
         if (printWhenVerbose && origException != null && this.getVerbose().isTrue()) {
             origException.printStackTrace(getErrorStream());
         }
         return new RaiseException(new RubyNameError(
                 this, getNameError(), message, name), true);
     }
 
-    public RaiseException newLocalJumpError(String reason, IRubyObject exitValue, String message) {
+    public RaiseException newLocalJumpError(RubyLocalJumpError.Reason reason, IRubyObject exitValue, String message) {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), message, reason, exitValue), true);
     }
 
     public RaiseException newRedoLocalJumpError() {
-        return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), "unexpected redo", "redo", getNil()), true);
+        return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), "unexpected redo", RubyLocalJumpError.Reason.REDO, getNil()), true);
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(getLoadError(), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(getTypeError(), "can't modify frozen " + objectType);
     }
 
     public RaiseException newSystemStackError(String message) {
         return newRaiseException(getSystemStackError(), message);
     }
 
     public RaiseException newSystemExit(int status) {
         return new RaiseException(RubySystemExit.newInstance(this, status));
     }
 
     public RaiseException newIOError(String message) {
         return newRaiseException(getIOError(), message);
     }
 
     public RaiseException newStandardError(String message) {
         return newRaiseException(getStandardError(), message);
     }
 
     public RaiseException newIOErrorFromException(IOException ioe) {
         // TODO: this is kinda gross
         if(ioe.getMessage() != null) {
             if (ioe.getMessage().equals("Broken pipe")) {
                 throw newErrnoEPIPEError();
             } else if (ioe.getMessage().equals("Connection reset by peer")) {
                 throw newErrnoECONNRESETError();
             }
             return newRaiseException(getIOError(), ioe.getMessage());
         } else {
             return newRaiseException(getIOError(), "IO Error");
         }
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyClass expectedType) {
         return newRaiseException(getTypeError(), "wrong argument type " +
                 receivedObject.getMetaClass().getRealClass() + " (expected " + expectedType + ")");
     }
 
     public RaiseException newEOFError() {
         return newRaiseException(getEOFError(), "End of file reached");
     }
 
     public RaiseException newEOFError(String message) {
         return newRaiseException(getEOFError(), message);
     }
 
     public RaiseException newZeroDivisionError() {
         return newRaiseException(getZeroDivisionError(), "divided by 0");
     }
 
     public RaiseException newFloatDomainError(String message){
         return newRaiseException(getFloatDomainError(), message);
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
 
     public Map<Integer, WeakReference<ChannelDescriptor>> getDescriptors() {
         return descriptors;
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
 
     // The method is intentionally not public, since it typically should
     // not be used outside of the core.
     /* package-private */ void setObjectSpaceEnabled(boolean objectSpaceEnabled) {
         this.objectSpaceEnabled = objectSpaceEnabled;
     }
 
     public long getStartTime() {
         return startTime;
     }
 
     public Profile getProfile() {
         return profile;
     }
 
     public String getJRubyHome() {
         return config.getJRubyHome();
     }
 
     public void setJRubyHome(String home) {
         config.setJRubyHome(home);
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
         return securityRestricted;
     }
     
     public static void setSecurityRestricted(boolean restricted) {
         securityRestricted = restricted;
     }
     
     public POSIX getPosix() {
         return posix;
     }
     
     public void setRecordSeparatorVar(GlobalVariable recordSeparatorVar) {
         this.recordSeparatorVar = recordSeparatorVar;
     }
     
     public GlobalVariable getRecordSeparatorVar() {
         return recordSeparatorVar;
     }
     
     public Set<Script> getJittedMethods() {
         return jittedMethods;
     }
     
     public ExecutorService getExecutor() {
         return executor;
     }
 
     public Map<String, DateTimeZone> getLocalTimezoneCache() {
         return localTimeZoneCache;
     }
 
     private final CacheMap cacheMap;
     private final ThreadService threadService;
     private Hashtable<Object, Object> runtimeInformation;
     
     private POSIX posix;
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
     private Map<Integer, WeakReference<ChannelDescriptor>> descriptors = new ConcurrentHashMap<Integer, WeakReference<ChannelDescriptor>>();
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private List<EventHook> eventHooks = new Vector<EventHook>();
     private boolean hasEventHooks;  
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private volatile boolean objectSpaceEnabled;
     
     private final Set<Script> jittedMethods = Collections.synchronizedSet(new WeakHashSet<Script>());
     
     private static ThreadLocal<Ruby> currentRuntime = new ThreadLocal<Ruby>();
     
     private long globalState = 1;
     
     private int safeLevel = -1;
 
     // Default objects
     private IRubyObject topSelf;
     private RubyNil nilObject;
     private RubyBoolean trueObject;
     private RubyBoolean falseObject;
     public final RubyFixnum[] fixnumCache = new RubyFixnum[256];
 
     private IRubyObject verbose;
     private IRubyObject debug;
     
     private RubyThreadGroup defaultThreadGroup;
 
     /**
      * All the core classes we keep hard references to. These are here largely
      * so that if someone redefines String or Array we won't start blowing up
      * creating strings and arrays internally. They also provide much faster
      * access than going through normal hash lookup on the Object class.
      */
     private RubyClass
             objectClass, moduleClass, classClass, nilClass, trueClass,
             falseClass, numericClass, floatClass, integerClass, fixnumClass,
             complexClass, rationalClass, enumeratorClass,
             arrayClass, hashClass, rangeClass, stringClass, symbolClass,
             procClass, bindingClass, methodClass, unboundMethodClass,
             matchDataClass, regexpClass, timeClass, bignumClass, dirClass,
             fileClass, fileStatClass, ioClass, threadClass, threadGroupClass,
             continuationClass, structClass, tmsStruct, passwdStruct,
             groupStruct, procStatusClass, exceptionClass, runtimeError, ioError,
             scriptError, nameError, nameErrorMessage, noMethodError, signalException,
             rangeError, dummyClass, systemExit, localJumpError, nativeException,
             systemCallError, fatal, interrupt, typeError, argumentError, indexError,
             syntaxError, standardError, loadError, notImplementedError, securityError, noMemoryError,
             regexpError, eofError, threadError, concurrencyError, systemStackError, zeroDivisionError, floatDomainError;
 
     /**
      * All the core modules we keep direct references to, for quick access and
      * to ensure they remain available.
      */
     private RubyModule
             kernelModule, comparableModule, enumerableModule, mathModule,
             marshalModule, etcModule, fileTestModule, gcModule,
             objectSpaceModule, processModule, procUIDModule, procGIDModule,
             procSysModule, precisionModule, errnoModule;
     
     // record separator var, to speed up io ops that use it
     private GlobalVariable recordSeparatorVar;
 
     // former java.lang.System concepts now internalized for MVM
     private String currentDirectory;
 
     private long startTime = System.currentTimeMillis();
 
     private RubyInstanceConfig config;
 
     private InputStream in;
     private PrintStream out;
     private PrintStream err;
 
     // Java support
     private JavaSupport javaSupport;
     private JRubyClassLoader jrubyClassLoader;
     
     // Management/monitoring
     private BeanManager beanManager;
 
     // Parser stats
     private ParserStats parserStats;
     
     // Compilation
     private final JITCompiler jitCompiler;
 
     // Note: this field and the following static initializer
     // must be located be in this order!
     private volatile static boolean securityRestricted = false;
     static {
         if (SafePropertyAccessor.isSecurityProtected("jruby.reflection")) {
             // can't read non-standard properties
             securityRestricted = true;
         } else {
             SecurityManager sm = System.getSecurityManager();
             if (sm != null) {
                 try {
                     sm.checkCreateClassLoader();
                 } catch (SecurityException se) {
                     // can't create custom classloaders
                     securityRestricted = true;
                 }
             }
         }
     }
 
     private Parser parser = new Parser(this);
 
     private LoadService loadService;
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private Stack<RubyProc> atExitBlocks = new Stack<RubyProc>();
 
     private Profile profile;
 
     private KCode kcode = KCode.NONE;
 
     // Atomic integers for symbol and method IDs
     private AtomicInteger symbolLastId = new AtomicInteger(128);
     private AtomicInteger moduleLastId = new AtomicInteger(0);
 
     private Object respondToMethod;
     private Object objectToYamlMethod;
 
     private Map<String, DateTimeZone> localTimeZoneCache = new HashMap<String,DateTimeZone>();
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
     
     // A thread pool to use for executing this runtime's Ruby threads
     private ExecutorService executor;
 }
diff --git a/src/org/jruby/RubyLocalJumpError.java b/src/org/jruby/RubyLocalJumpError.java
index 62a3ebeb14..f0b7481ada 100644
--- a/src/org/jruby/RubyLocalJumpError.java
+++ b/src/org/jruby/RubyLocalJumpError.java
@@ -1,71 +1,87 @@
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
 
 package org.jruby;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 @JRubyClass(name="LocalJumpError",parent="StandardError")
 public class RubyLocalJumpError extends RubyException {
+    public enum Reason {
+        REDO, BREAK, NEXT, RETURN, RETRY, NOREASON;
+        
+        @Override
+        public String toString() {
+            return super.toString().toLowerCase();
+        }
+    }
+    
     private static ObjectAllocator LOCALJUMPERROR_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyLocalJumpError(runtime, klass);
         }
     };
 
     public static RubyClass createLocalJumpErrorClass(Ruby runtime, RubyClass standardErrorClass) {
         RubyClass nameErrorClass = runtime.defineClass("LocalJumpError", standardErrorClass, LOCALJUMPERROR_ALLOCATOR);
         
         nameErrorClass.defineAnnotatedMethods(RubyLocalJumpError.class);
 
         return nameErrorClass;
     }
     
+    private Reason reason;
+    
     private RubyLocalJumpError(Ruby runtime, RubyClass exceptionClass) {
         super(runtime, exceptionClass);
     }
 
-    public RubyLocalJumpError(Ruby runtime, RubyClass exceptionClass, String message, String reason, IRubyObject exitValue) {
+    public RubyLocalJumpError(Ruby runtime, RubyClass exceptionClass, String message, Reason reason, IRubyObject exitValue) {
         super(runtime, exceptionClass, message);
-        fastSetInternalVariable("reason", runtime.newSymbol(reason));
+        this.reason = reason;
+        fastSetInternalVariable("reason", runtime.newSymbol(reason.toString()));
         fastSetInternalVariable("exit_value", exitValue);
     }
 
     @JRubyMethod(name = "reason")
     public IRubyObject reason() {
         return fastGetInternalVariable("reason");
     }
     
+    public Reason getReason() {
+        return reason;
+    }
+    
     @JRubyMethod(name = "exit_value")
     public IRubyObject exit_value() {
         return fastGetInternalVariable("exit_value");
     }
 }
diff --git a/src/org/jruby/RubyProc.java b/src/org/jruby/RubyProc.java
index d773dc3eef..5688c9e027 100644
--- a/src/org/jruby/RubyProc.java
+++ b/src/org/jruby/RubyProc.java
@@ -1,257 +1,257 @@
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
 
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.java.MiniJava;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author  jpetersen
  */
 @JRubyClass(name="Proc")
 public class RubyProc extends RubyObject implements JumpTarget {
     private Block block = Block.NULL_BLOCK;
     private Block.Type type;
     private String file;
     private int line;
 
     public RubyProc(Ruby runtime, RubyClass rubyClass, Block.Type type) {
         super(runtime, rubyClass);
         
         this.type = type;
     }
     
     private static ObjectAllocator PROC_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyProc instance = RubyProc.newProc(runtime, Block.Type.PROC);
 
             instance.setMetaClass(klass);
 
             return instance;
         }
     };
 
     public static RubyClass createProcClass(Ruby runtime) {
         RubyClass procClass = runtime.defineClass("Proc", runtime.getObject(), PROC_ALLOCATOR);
         runtime.setProc(procClass);
         
         procClass.defineAnnotatedMethods(RubyProc.class);
         
         return procClass;
     }
 
     public Block getBlock() {
         return block;
     }
 
     // Proc class
 
     public static RubyProc newProc(Ruby runtime, Block.Type type) {
         return new RubyProc(runtime, runtime.getProc(), type);
     }
     public static RubyProc newProc(Ruby runtime, Block block, Block.Type type) {
         RubyProc proc = new RubyProc(runtime, runtime.getProc(), type);
         proc.callInit(NULL_ARRAY, block);
         
         return proc;
     }
     
     /**
      * Create a new instance of a Proc object.  We override this method (from RubyClass)
      * since we need to deal with special case of Proc.new with no arguments or block arg.  In 
      * this case, we need to check previous frame for a block to consume.
      */
     @JRubyMethod(name = "new", rest = true, frame = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         // No passed in block, lets check next outer frame for one ('Proc.new')
         if (!block.isGiven()) {
             block = context.getPreviousFrame().getBlock();
         }
         
         if (block.isGiven() && block.getProcObject() != null) {
             return block.getProcObject();
         }
         
         IRubyObject obj = ((RubyClass) recv).allocate();
         
         obj.callMethod(context, "initialize", args, block);
         return obj;
     }
     
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, Block procBlock) {
         if (!procBlock.isGiven()) {
             throw getRuntime().newArgumentError("tried to create Proc object without a block");
         }
         
         if (type == Block.Type.LAMBDA && procBlock == null) {
             // TODO: warn "tried to create Proc object without a block"
         }
         
         block = procBlock.cloneBlock();
         block.type = type;
         block.setProcObject(this);
 
         file = context.getFile();
         line = context.getLine();
         return this;
     }
     
     @JRubyMethod(name = "clone")
     public IRubyObject rbClone() {
     	RubyProc newProc = new RubyProc(getRuntime(), getRuntime().getProc(), type);
     	newProc.block = getBlock();
     	newProc.file = file;
     	newProc.line = line;
     	// TODO: CLONE_SETUP here
     	return newProc;
     }
 
     @JRubyMethod(name = "dup")
     public IRubyObject dup() {
         RubyProc newProc = new RubyProc(getRuntime(), getRuntime().getProc(), type);
         newProc.block = getBlock();
         newProc.file = file;
         newProc.line = line;
         return newProc;
     }
     
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(IRubyObject other) {
         if (!(other instanceof RubyProc)) return getRuntime().getFalse();
         
         if (this == other || this.block == ((RubyProc)other).block) {
             return getRuntime().getTrue();
         }
         
         return getRuntime().getFalse();
     }
     
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
         return RubyString.newString(getRuntime(), 
                 "#<Proc:0x" + Integer.toString(block.hashCode(), 16) + "@" + 
                 file + ":" + (line + 1) + ">");
     }
 
     @JRubyMethod(name = "binding")
     public IRubyObject binding() {
         return getRuntime().newBinding(block.getBinding());
     }
 
     @JRubyMethod(name = {"call", "[]"}, rest = true, frame = true)
     public IRubyObject call(ThreadContext context, IRubyObject[] args) {
         return call(context, args, null);
     }
     
     public IRubyObject call(ThreadContext context, IRubyObject[] args, IRubyObject self) {
         assert args != null;
         
         Ruby runtime = getRuntime();
         Block newBlock = block.cloneBlock();
         JumpTarget jumpTarget = newBlock.getBinding().getFrame().getJumpTarget();
         
         try {
             if (self != null) newBlock.getBinding().setSelf(self);
             
             return newBlock.call(context, args);
         } catch (JumpException.BreakJump bj) {
             switch(block.type) {
             case LAMBDA: if (bj.getTarget() == jumpTarget) {
                 return (IRubyObject) bj.getValue();
             } else {
-                throw runtime.newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
+                throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "unexpected break");
             }
             case PROC:
                 if (newBlock.isEscaped()) {
-                    throw runtime.newLocalJumpError("break", (IRubyObject)bj.getValue(), "break from proc-closure");
+                    throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "break from proc-closure");
                 } else {
                     throw bj;
                 }
             default: throw bj;
             }
         } catch (JumpException.ReturnJump rj) {
             Object target = rj.getTarget();
             
             if (target == jumpTarget && block.type == Block.Type.LAMBDA) return (IRubyObject) rj.getValue();
 
             if (type == Block.Type.THREAD) {
                 throw runtime.newThreadError("return can't jump across threads");
             }
             throw rj;
         } catch (JumpException.RetryJump rj) {
-            throw runtime.newLocalJumpError("retry", (IRubyObject)rj.getValue(), "retry not supported outside rescue");
+            throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.RETRY, (IRubyObject)rj.getValue(), "retry not supported outside rescue");
         }
     }
 
     @JRubyMethod(name = "arity")
     public RubyFixnum arity() {
         return getRuntime().newFixnum(block.arity().getValue());
     }
     
     @JRubyMethod(name = "to_proc")
     public RubyProc to_proc() {
     	return this;
     }
     
     public IRubyObject as(Class asClass) {
         final Ruby ruby = getRuntime();
         if (!asClass.isInterface()) {
             throw ruby.newTypeError(asClass.getCanonicalName() + " is not an interface");
         }
 
         return MiniJava.javaToRuby(ruby, Proxy.newProxyInstance(Ruby.getClassLoader(), new Class[] {asClass}, new InvocationHandler() {
             public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                 IRubyObject[] rubyArgs = new IRubyObject[args.length + 1];
                 rubyArgs[0] = RubySymbol.newSymbol(ruby, method.getName());
                 for (int i = 1; i < rubyArgs.length; i++) {
                     rubyArgs[i] = MiniJava.javaToRuby(ruby, args[i - 1]);
                 }
                 return MiniJava.rubyToJava(call(ruby.getCurrentContext(), rubyArgs));
             }
         }));
     }
 }
diff --git a/src/org/jruby/compiler/impl/BaseBodyCompiler.java b/src/org/jruby/compiler/impl/BaseBodyCompiler.java
index 3453272893..c502fb3245 100644
--- a/src/org/jruby/compiler/impl/BaseBodyCompiler.java
+++ b/src/org/jruby/compiler/impl/BaseBodyCompiler.java
@@ -1,1793 +1,1746 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 package org.jruby.compiler.impl;
 
 import java.io.PrintStream;
 import java.lang.reflect.InvocationTargetException;
 import java.math.BigInteger;
 import java.util.Arrays;
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyRange;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ArrayCallback;
 import org.jruby.compiler.BranchCallback;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.compiler.InvocationCompiler;
 import org.jruby.compiler.BodyCompiler;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.VariableCompiler;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.ReOptions;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.InstanceVariables;
 import org.jruby.util.ByteList;
 import org.jruby.util.JavaNameMangler;
 import org.objectweb.asm.Label;
 import static org.objectweb.asm.Opcodes.*;
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  * BaseBodyCompiler encapsulates all common behavior between BodyCompiler
  * implementations.
  */
 public abstract class BaseBodyCompiler implements BodyCompiler {
     protected SkinnyMethodAdapter method;
     protected VariableCompiler variableCompiler;
     protected InvocationCompiler invocationCompiler;
     protected int argParamCount;
     protected Label[] currentLoopLabels;
     protected Label scopeStart = new Label();
     protected Label scopeEnd = new Label();
     protected Label redoJump;
     protected boolean withinProtection = false;
     private int lastLine = -1;
     private int lastPositionLine = -1;
     protected StaticScope scope;
     protected ASTInspector inspector;
     protected String methodName;
     protected StandardASMCompiler script;
 
     public BaseBodyCompiler(StandardASMCompiler scriptCompiler, String methodName, ASTInspector inspector, StaticScope scope) {
         this.script = scriptCompiler;
         this.scope = scope;
         this.inspector = inspector;
         this.methodName = methodName;
         if (scope.getRestArg() >= 0 || scope.getOptionalArgs() > 0 || scope.getRequiredArgs() > 3) {
             argParamCount = 1; // use IRubyObject[]
         } else {
             argParamCount = scope.getRequiredArgs(); // specific arity
         }
 
         method = new SkinnyMethodAdapter(script.getClassVisitor().visitMethod(ACC_PUBLIC, methodName, getSignature(), null, null));
 
         createVariableCompiler();
         if (StandardASMCompiler.invDynInvCompilerConstructor != null) {
             try {
                 invocationCompiler = (InvocationCompiler) StandardASMCompiler.invDynInvCompilerConstructor.newInstance(this, method);
             } catch (InstantiationException ie) {
                 // do nothing, fall back on default compiler below
                 } catch (IllegalAccessException ie) {
                 // do nothing, fall back on default compiler below
                 } catch (InvocationTargetException ie) {
                 // do nothing, fall back on default compiler below
                 }
         }
         if (invocationCompiler == null) {
             invocationCompiler = new StandardInvocationCompiler(this, method);
         }
     }
 
     protected abstract String getSignature();
 
     protected abstract void createVariableCompiler();
 
     public abstract void beginMethod(CompilerCallback args, StaticScope scope);
 
     public abstract void endBody();
 
     public BodyCompiler chainToMethod(String methodName, ASTInspector inspector) {
         BodyCompiler compiler = outline(methodName, inspector);
         endBody();
         return compiler;
     }
 
     public BodyCompiler outline(String methodName, ASTInspector inspector) {
         // chain to the next segment of this giant method
         method.aload(StandardASMCompiler.THIS);
 
         // load all arguments straight through
         for (int i = 1; i <= getClosureIndex(); i++) {
             method.aload(i);
         }
         method.invokevirtual(script.getClassname(), methodName, getSignature());
 
         ChainedBodyCompiler methodCompiler = new ChainedBodyCompiler(script, methodName, inspector, scope, this);
 
         methodCompiler.beginChainedMethod();
 
         return methodCompiler;
     }
 
     public StandardASMCompiler getScriptCompiler() {
         return script;
     }
 
     public void lineNumber(ISourcePosition position) {
         int thisLine = position.getStartLine();
 
         // No point in updating number if last number was same value.
         if (thisLine != lastLine) {
             lastLine = thisLine;
         } else {
             return;
         }
 
         Label line = new Label();
         method.label(line);
         method.visitLineNumber(thisLine + 1, line);
     }
 
     public void loadThreadContext() {
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
     }
 
     public void loadSelf() {
         method.aload(StandardASMCompiler.SELF_INDEX);
     }
 
     protected int getClosureIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.CLOSURE_OFFSET;
     }
 
     protected int getRuntimeIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.RUNTIME_OFFSET;
     }
 
     protected int getNilIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.NIL_OFFSET;
     }
 
     protected int getPreviousExceptionIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.PREVIOUS_EXCEPTION_OFFSET;
     }
 
     protected int getDynamicScopeIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.DYNAMIC_SCOPE_OFFSET;
     }
 
     protected int getVarsArrayIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.VARS_ARRAY_OFFSET;
     }
 
     protected int getFirstTempIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.FIRST_TEMP_OFFSET;
     }
 
     protected int getExceptionIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.EXCEPTION_OFFSET;
     }
 
     public void loadThis() {
         method.aload(StandardASMCompiler.THIS);
     }
 
     public void loadRuntime() {
         method.aload(getRuntimeIndex());
     }
 
     public void loadBlock() {
         method.aload(getClosureIndex());
     }
 
     public void loadNil() {
         method.aload(getNilIndex());
     }
 
     public void loadNull() {
         method.aconst_null();
     }
 
     public void loadSymbol(String symbol) {
         loadRuntime();
 
         method.ldc(symbol);
 
         invokeIRuby("newSymbol", sig(RubySymbol.class, params(String.class)));
     }
 
     public void loadObject() {
         loadRuntime();
 
         invokeIRuby("getObject", sig(RubyClass.class, params()));
     }
 
     /**
      * This is for utility methods used by the compiler, to reduce the amount of code generation
      * necessary.  All of these live in CompilerHelpers.
      */
     public void invokeUtilityMethod(String methodName, String signature) {
         method.invokestatic(p(RuntimeHelpers.class), methodName, signature);
     }
 
     public void invokeThreadContext(String methodName, String signature) {
         method.invokevirtual(StandardASMCompiler.THREADCONTEXT, methodName, signature);
     }
 
     public void invokeIRuby(String methodName, String signature) {
         method.invokevirtual(StandardASMCompiler.RUBY, methodName, signature);
     }
 
     public void invokeIRubyObject(String methodName, String signature) {
         method.invokeinterface(StandardASMCompiler.IRUBYOBJECT, methodName, signature);
     }
 
     public void consumeCurrentValue() {
         method.pop();
     }
 
     public void duplicateCurrentValue() {
         method.dup();
     }
 
     public void swapValues() {
         method.swap();
     }
 
     public void retrieveSelf() {
         loadSelf();
     }
 
     public void retrieveSelfClass() {
         loadSelf();
         metaclass();
     }
 
     public VariableCompiler getVariableCompiler() {
         return variableCompiler;
     }
 
     public InvocationCompiler getInvocationCompiler() {
         return invocationCompiler;
     }
 
     public void assignConstantInCurrent(String name) {
         loadThreadContext();
         method.ldc(name);
         method.dup2_x1();
         method.pop2();
         invokeThreadContext("setConstantInCurrent", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
     }
 
     public void assignConstantInModule(String name) {
         method.ldc(name);
         loadThreadContext();
         invokeUtilityMethod("setConstantInModule", sig(IRubyObject.class, IRubyObject.class, IRubyObject.class, String.class, ThreadContext.class));
     }
 
     public void assignConstantInObject(String name) {
         // load Object under value
         loadRuntime();
         invokeIRuby("getObject", sig(RubyClass.class, params()));
         method.swap();
 
         assignConstantInModule(name);
     }
 
     public void retrieveConstant(String name) {
         loadThreadContext();
         method.ldc(name);
         invokeUtilityMethod("getConstant", sig(IRubyObject.class, params(ThreadContext.class, String.class)));
     }
 
     public void retrieveConstantFromModule(String name) {
         method.visitTypeInsn(CHECKCAST, p(RubyModule.class));
         method.ldc(name);
         method.invokevirtual(p(RubyModule.class), "fastGetConstantFrom", sig(IRubyObject.class, params(String.class)));
     }
 
     public void retrieveClassVariable(String name) {
         loadThreadContext();
         loadRuntime();
         loadSelf();
         method.ldc(name);
 
         invokeUtilityMethod("fastFetchClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class)));
     }
 
     public void assignClassVariable(String name) {
         loadThreadContext();
         method.swap();
         loadRuntime();
         method.swap();
         loadSelf();
         method.swap();
         method.ldc(name);
         method.swap();
 
         invokeUtilityMethod("fastSetClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void assignClassVariable(String name, CompilerCallback value) {
         loadThreadContext();
         loadRuntime();
         loadSelf();
         method.ldc(name);
         value.call(this);
 
         invokeUtilityMethod("fastSetClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void declareClassVariable(String name) {
         loadThreadContext();
         method.swap();
         loadRuntime();
         method.swap();
         loadSelf();
         method.swap();
         method.ldc(name);
         method.swap();
 
         invokeUtilityMethod("fastDeclareClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void declareClassVariable(String name, CompilerCallback value) {
         loadThreadContext();
         loadRuntime();
         loadSelf();
         method.ldc(name);
         value.call(this);
 
         invokeUtilityMethod("fastDeclareClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void createNewFloat(double value) {
         loadRuntime();
         method.ldc(new Double(value));
 
         invokeIRuby("newFloat", sig(RubyFloat.class, params(Double.TYPE)));
     }
 
     public void createNewFixnum(long value) {
         script.getCacheCompiler().cacheFixnum(this, value);
     }
 
     public void createNewBignum(BigInteger value) {
         loadRuntime();
         script.getCacheCompiler().cacheBigInteger(this, value);
         method.invokestatic(p(RubyBignum.class), "newBignum", sig(RubyBignum.class, params(Ruby.class, BigInteger.class)));
     }
 
     public void createNewString(ArrayCallback callback, int count) {
         loadRuntime();
         invokeIRuby("newString", sig(RubyString.class, params()));
         for (int i = 0; i < count; i++) {
             callback.nextValue(this, null, i);
             method.invokevirtual(p(RubyString.class), "append", sig(RubyString.class, params(IRubyObject.class)));
         }
     }
 
     public void createNewSymbol(ArrayCallback callback, int count) {
         loadRuntime();
         createNewString(callback, count);
         toJavaString();
         invokeIRuby("newSymbol", sig(RubySymbol.class, params(String.class)));
     }
 
     public void createNewString(ByteList value) {
         // FIXME: this is sub-optimal, storing string value in a java.lang.String again
         loadRuntime();
         script.getCacheCompiler().cacheByteList(this, value.toString());
 
         invokeIRuby("newStringShared", sig(RubyString.class, params(ByteList.class)));
     }
 
     public void createNewSymbol(String name) {
         script.getCacheCompiler().cacheSymbol(this, name);
     }
 
     public void createNewArray(boolean lightweight) {
         loadRuntime();
         // put under object array already present
         method.swap();
 
         if (lightweight) {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopyLight", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         } else {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopy", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         }
     }
 
     public void createNewArray(Object[] sourceArray, ArrayCallback callback, boolean lightweight) {
         loadRuntime();
 
         createObjectArray(sourceArray, callback);
 
         if (lightweight) {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopyLight", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         } else {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopy", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         }
     }
 
     public void createEmptyArray() {
         loadRuntime();
 
         invokeIRuby("newArray", sig(RubyArray.class, params()));
     }
 
     public void createObjectArray(Object[] sourceArray, ArrayCallback callback) {
         buildObjectArray(StandardASMCompiler.IRUBYOBJECT, sourceArray, callback);
     }
 
     public void createObjectArray(int elementCount) {
         // if element count is less than 6, use helper methods
         if (elementCount < 6) {
             Class[] params = new Class[elementCount];
             Arrays.fill(params, IRubyObject.class);
             invokeUtilityMethod("constructObjectArray", sig(IRubyObject[].class, params));
         } else {
             // This is pretty inefficient for building an array, so just raise an error if someone's using it for a lot of elements
             throw new NotCompilableException("Don't use createObjectArray(int) for more than 5 elements");
         }
     }
 
     private void buildObjectArray(String type, Object[] sourceArray, ArrayCallback callback) {
         if (sourceArray.length == 0) {
             method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
         } else if (sourceArray.length <= RuntimeHelpers.MAX_SPECIFIC_ARITY_OBJECT_ARRAY) {
             // if we have a specific-arity helper to construct an array for us, use that
             for (int i = 0; i < sourceArray.length; i++) {
                 callback.nextValue(this, sourceArray, i);
             }
             invokeUtilityMethod("constructObjectArray", sig(IRubyObject[].class, params(IRubyObject.class, sourceArray.length)));
         } else {
             // brute force construction inline
             method.pushInt(sourceArray.length);
             method.anewarray(type);
 
             for (int i = 0; i < sourceArray.length; i++) {
                 method.dup();
                 method.pushInt(i);
 
                 callback.nextValue(this, sourceArray, i);
 
                 method.arraystore();
             }
         }
     }
 
     public void createEmptyHash() {
         loadRuntime();
 
         method.invokestatic(p(RubyHash.class), "newHash", sig(RubyHash.class, params(Ruby.class)));
     }
 
     public void createNewHash(Object elements, ArrayCallback callback, int keyCount) {
         loadRuntime();
 
         if (keyCount <= RuntimeHelpers.MAX_SPECIFIC_ARITY_HASH) {
             // we have a specific-arity method we can use to construct, so use that
             for (int i = 0; i < keyCount; i++) {
                 callback.nextValue(this, elements, i);
             }
 
             invokeUtilityMethod("constructHash", sig(RubyHash.class, params(Ruby.class, IRubyObject.class, keyCount * 2)));
         } else {
             method.invokestatic(p(RubyHash.class), "newHash", sig(RubyHash.class, params(Ruby.class)));
 
             for (int i = 0; i < keyCount; i++) {
                 method.dup();
                 callback.nextValue(this, elements, i);
                 method.invokevirtual(p(RubyHash.class), "fastASet", sig(void.class, params(IRubyObject.class, IRubyObject.class)));
             }
         }
     }
 
     public void createNewRange(boolean isExclusive) {
         loadRuntime();
         loadThreadContext();
 
         // could be more efficient with a callback
         method.dup2_x2();
         method.pop2();
 
         if (isExclusive) {
             method.invokestatic(p(RubyRange.class), "newExclusiveRange", sig(RubyRange.class, params(Ruby.class, ThreadContext.class, IRubyObject.class, IRubyObject.class)));
         } else {
             method.invokestatic(p(RubyRange.class), "newInclusiveRange", sig(RubyRange.class, params(Ruby.class, ThreadContext.class, IRubyObject.class, IRubyObject.class)));
         }
     }
 
     /**
      * Invoke IRubyObject.isTrue
      */
     private void isTrue() {
         invokeIRubyObject("isTrue", sig(Boolean.TYPE));
     }
 
     public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch) {
         Label afterJmp = new Label();
         Label falseJmp = new Label();
 
         // call isTrue on the result
         isTrue();
 
         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(afterJmp);
 
         // FIXME: optimize for cases where we have no false branch
         method.label(falseJmp);
         falseBranch.branch(this);
 
         method.label(afterJmp);
     }
 
     public void performLogicalAnd(BranchCallback longBranch) {
         Label falseJmp = new Label();
 
         // dup it since we need to return appropriately if it's false
         method.dup();
 
         // call isTrue on the result
         isTrue();
 
         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
         // pop the extra result and replace with the send part of the AND
         method.pop();
         longBranch.branch(this);
         method.label(falseJmp);
     }
 
     public void performLogicalOr(BranchCallback longBranch) {
         // FIXME: after jump is not in here.  Will if ever be?
         //Label afterJmp = new Label();
         Label falseJmp = new Label();
 
         // dup it since we need to return appropriately if it's false
         method.dup();
 
         // call isTrue on the result
         isTrue();
 
         method.ifne(falseJmp); // EQ == 0 (i.e. false)
         // pop the extra result and replace with the send part of the AND
         method.pop();
         longBranch.branch(this);
         method.label(falseJmp);
     }
 
     public void performBooleanLoopSafe(BranchCallback condition, BranchCallback body, boolean checkFirst) {
         String mname = getNewRescueName();
         String signature = sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, Block.class});
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(script.getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, signature, null, null));
         SkinnyMethodAdapter old_method = null;
         SkinnyMethodAdapter var_old_method = null;
         SkinnyMethodAdapter inv_old_method = null;
         boolean oldWithinProtection = withinProtection;
         withinProtection = true;
         Label[] oldLoopLabels = currentLoopLabels;
         currentLoopLabels = null;
         int oldArgCount = argParamCount;
         argParamCount = 0; // synthetic methods always have zero arg parameters
         try {
             old_method = this.method;
             var_old_method = getVariableCompiler().getMethodAdapter();
             inv_old_method = getInvocationCompiler().getMethodAdapter();
             this.method = mv;
             getVariableCompiler().setMethodAdapter(mv);
             getInvocationCompiler().setMethodAdapter(mv);
 
             mv.visitCode();
 
             // set up a local IRuby variable
             mv.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             mv.dup();
             mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             mv.dup();
             mv.astore(getRuntimeIndex());
 
             // store previous exception for restoration if we rescue something
             loadRuntime();
             invokeUtilityMethod("getErrorInfo", sig(IRubyObject.class, Ruby.class));
             mv.astore(getPreviousExceptionIndex());
 
             // grab nil for local variables
             mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             mv.astore(getNilIndex());
 
             mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             mv.dup();
             mv.astore(getDynamicScopeIndex());
             mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             mv.astore(getVarsArrayIndex());
 
             performBooleanLoop(condition, body, checkFirst);
 
             mv.areturn();
             mv.visitMaxs(1, 1);
             mv.visitEnd();
         } finally {
             withinProtection = oldWithinProtection;
             this.method = old_method;
             getVariableCompiler().setMethodAdapter(var_old_method);
             getInvocationCompiler().setMethodAdapter(inv_old_method);
             currentLoopLabels = oldLoopLabels;
             argParamCount = oldArgCount;
         }
 
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         loadSelf();
         if (this instanceof ChildScopedBodyCompiler) {
             pushNull();
         } else {
             loadBlock();
         }
         method.invokevirtual(script.getClassname(), mname, signature);
     }
 
-    public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst) {
+    public void performBooleanLoop(BranchCallback condition, final BranchCallback body, boolean checkFirst) {
         // FIXME: handle next/continue, break, etc
         Label tryBegin = new Label();
         Label tryEnd = new Label();
-        Label catchRedo = new Label();
         Label catchNext = new Label();
         Label catchBreak = new Label();
-        Label catchRaised = new Label();
         Label endOfBody = new Label();
         Label conditionCheck = new Label();
-        Label topOfBody = new Label();
+        final Label topOfBody = new Label();
         Label done = new Label();
         Label normalLoopEnd = new Label();
-        method.trycatch(tryBegin, tryEnd, catchRedo, p(JumpException.RedoJump.class));
         method.trycatch(tryBegin, tryEnd, catchNext, p(JumpException.NextJump.class));
         method.trycatch(tryBegin, tryEnd, catchBreak, p(JumpException.BreakJump.class));
-        if (checkFirst) {
-            // only while loops seem to have this RaiseException magic
-            method.trycatch(tryBegin, tryEnd, catchRaised, p(RaiseException.class));
-        }
 
         method.label(tryBegin);
         {
 
             Label[] oldLoopLabels = currentLoopLabels;
 
             currentLoopLabels = new Label[]{endOfBody, topOfBody, done};
 
             // FIXME: if we terminate immediately, this appears to break while in method arguments
             // we need to push a nil for the cases where we will never enter the body
             if (checkFirst) {
                 method.go_to(conditionCheck);
             }
 
             method.label(topOfBody);
 
-            body.branch(this);
+            Runnable redoBody = new Runnable() { public void run() {
+                Runnable raiseBody = new Runnable() { public void run() {
+                    body.branch(BaseBodyCompiler.this);
+                }};
+                Runnable raiseCatch = new Runnable() { public void run() {
+                    loadThreadContext();
+                    invokeUtilityMethod("unwrapRedoNextBreakOrJustLocalJump", sig(Throwable.class, RaiseException.class, ThreadContext.class));
+                    method.athrow();
+                }};
+                method.trycatch(p(RaiseException.class), raiseBody, raiseCatch);
+            }};
+            Runnable redoCatch = new Runnable() { public void run() {
+                method.pop();
+                method.go_to(topOfBody);
+            }};
+            method.trycatch(p(JumpException.RedoJump.class), redoBody, redoCatch);
 
             method.label(endOfBody);
 
             // clear body or next result after each successful loop
             method.pop();
 
             method.label(conditionCheck);
 
             // check the condition
             condition.branch(this);
             isTrue();
             method.ifne(topOfBody); // NE == nonzero (i.e. true)
 
             currentLoopLabels = oldLoopLabels;
         }
 
         method.label(tryEnd);
         // skip catch block
         method.go_to(normalLoopEnd);
 
-        // catch logic for flow-control exceptions
+        // catch logic for flow-control: next, break
         {
-            // redo jump
-            {
-                method.label(catchRedo);
-                method.pop();
-                method.go_to(topOfBody);
-            }
-
             // next jump
             {
                 method.label(catchNext);
                 method.pop();
                 // exceptionNext target is for a next that doesn't push a new value, like this one
                 method.go_to(conditionCheck);
             }
 
             // break jump
             {
                 method.label(catchBreak);
                 loadBlock();
                 loadThreadContext();
                 invokeUtilityMethod("breakJumpInWhile", sig(IRubyObject.class, JumpException.BreakJump.class, Block.class, ThreadContext.class));
                 method.go_to(done);
             }
-
-            // FIXME: This generates a crapload of extra code that is frequently *never* needed
-            // raised exception
-            if (checkFirst) {
-                // only while loops seem to have this RaiseException magic
-                method.label(catchRaised);
-                Label raiseNext = new Label();
-                Label raiseRedo = new Label();
-                Label raiseRethrow = new Label();
-                method.dup();
-                invokeUtilityMethod("getLocalJumpTypeOrRethrow", sig(String.class, params(RaiseException.class)));
-                // if we get here we have a RaiseException we know is a local jump error and an error type
-
-                // is it break?
-                method.dup(); // dup string
-                method.ldc("break");
-                method.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
-                method.ifeq(raiseNext);
-                // pop the extra string, get the break value, and end the loop
-                method.pop();
-                invokeUtilityMethod("unwrapLocalJumpErrorValue", sig(IRubyObject.class, params(RaiseException.class)));
-                method.go_to(done);
-
-                // is it next?
-                method.label(raiseNext);
-                method.dup();
-                method.ldc("next");
-                method.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
-                method.ifeq(raiseRedo);
-                // pop the extra string and the exception, jump to the condition
-                method.pop2();
-                method.go_to(conditionCheck);
-
-                // is it redo?
-                method.label(raiseRedo);
-                method.dup();
-                method.ldc("redo");
-                method.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
-                method.ifeq(raiseRethrow);
-                // pop the extra string and the exception, jump to the condition
-                method.pop2();
-                method.go_to(topOfBody);
-
-                // just rethrow it
-                method.label(raiseRethrow);
-                method.pop(); // pop extra string
-                method.athrow();
-            }
         }
 
         method.label(normalLoopEnd);
         loadNil();
         method.label(done);
     }
 
     public void performBooleanLoopLight(BranchCallback condition, BranchCallback body, boolean checkFirst) {
         Label endOfBody = new Label();
         Label conditionCheck = new Label();
         Label topOfBody = new Label();
         Label done = new Label();
 
         Label[] oldLoopLabels = currentLoopLabels;
 
         currentLoopLabels = new Label[]{endOfBody, topOfBody, done};
 
         // FIXME: if we terminate immediately, this appears to break while in method arguments
         // we need to push a nil for the cases where we will never enter the body
         if (checkFirst) {
             method.go_to(conditionCheck);
         }
 
         method.label(topOfBody);
 
         body.branch(this);
 
         method.label(endOfBody);
 
         // clear body or next result after each successful loop
         method.pop();
 
         method.label(conditionCheck);
 
         // check the condition
         condition.branch(this);
         isTrue();
         method.ifne(topOfBody); // NE == nonzero (i.e. true)
 
         currentLoopLabels = oldLoopLabels;
 
         loadNil();
         method.label(done);
     }
 
     public void createNewClosure(
             int line,
             StaticScope scope,
             int arity,
             CompilerCallback body,
             CompilerCallback args,
             boolean hasMultipleArgsHead,
             NodeType argsNodeId,
             ASTInspector inspector) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$" + "__block__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, inspector, scope);
 
         closureCompiler.beginMethod(args, scope);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
 
         loadThreadContext();
         loadSelf();
         script.getCacheCompiler().cacheClosure(this, closureMethodName, arity, scope, hasMultipleArgsHead, argsNodeId, inspector);
 
         invokeUtilityMethod("createBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, BlockBody.class)));
     }
 
     public void runBeginBlock(StaticScope scope, CompilerCallback body) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__begin__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, null, scope);
 
         closureCompiler.beginMethod(null, scope);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
 
         StandardASMCompiler.buildStaticScopeNames(method, scope);
 
         script.getCacheCompiler().cacheClosureOld(this, closureMethodName);
 
         invokeUtilityMethod("runBeginBlock", sig(IRubyObject.class,
                 params(ThreadContext.class, IRubyObject.class, String[].class, CompiledBlockCallback.class)));
     }
 
     public void createNewForLoop(int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__for__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, null, scope);
 
         closureCompiler.beginMethod(args, null);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
         method.pushInt(arity);
 
         script.getCacheCompiler().cacheClosureOld(this, closureMethodName);
 
         method.ldc(Boolean.valueOf(hasMultipleArgsHead));
         method.ldc(BlockBody.asArgumentType(argsNodeId));
 
         invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
     }
 
     public void createNewEndBlock(CompilerCallback body) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__end__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, null, scope);
 
         closureCompiler.beginMethod(null, null);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
         method.iconst_0();
 
         script.getCacheCompiler().cacheClosureOld(this, closureMethodName);
 
         method.iconst_0(); // false
         method.iconst_0(); // zero
 
         invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
 
         loadRuntime();
         invokeUtilityMethod("registerEndBlock", sig(void.class, Block.class, Ruby.class));
         loadNil();
     }
 
     public void getCompiledClass() {
         method.aload(StandardASMCompiler.THIS);
         method.getfield(script.getClassname(), "$class", ci(Class.class));
     }
 
     public void println() {
         method.dup();
         method.getstatic(p(System.class), "out", ci(PrintStream.class));
         method.swap();
 
         method.invokevirtual(p(PrintStream.class), "println", sig(Void.TYPE, params(Object.class)));
     }
 
     public void defineAlias(String newName, String oldName) {
         loadThreadContext();
         method.ldc(newName);
         method.ldc(oldName);
         invokeUtilityMethod("defineAlias", sig(IRubyObject.class, ThreadContext.class, String.class, String.class));
     }
 
     public void loadFalse() {
         // TODO: cache?
         loadRuntime();
         invokeIRuby("getFalse", sig(RubyBoolean.class));
     }
 
     public void loadTrue() {
         // TODO: cache?
         loadRuntime();
         invokeIRuby("getTrue", sig(RubyBoolean.class));
     }
 
     public void loadCurrentModule() {
         loadThreadContext();
         invokeThreadContext("getCurrentScope", sig(DynamicScope.class));
         method.invokevirtual(p(DynamicScope.class), "getStaticScope", sig(StaticScope.class));
         method.invokevirtual(p(StaticScope.class), "getModule", sig(RubyModule.class));
     }
 
     public void retrieveInstanceVariable(String name) {
         loadRuntime();
         loadSelf();
         method.ldc(name);
         invokeUtilityMethod("fastGetInstanceVariable", sig(IRubyObject.class, Ruby.class, IRubyObject.class, String.class));
     }
 
     public void assignInstanceVariable(String name) {
         // FIXME: more efficient with a callback
         loadSelf();
         invokeIRubyObject("getInstanceVariables", sig(InstanceVariables.class));
         method.swap();
 
         method.ldc(name);
         method.swap();
 
         method.invokeinterface(p(InstanceVariables.class), "fastSetInstanceVariable", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
     }
 
     public void assignInstanceVariable(String name, CompilerCallback value) {
         // FIXME: more efficient with a callback
         loadSelf();
         invokeIRubyObject("getInstanceVariables", sig(InstanceVariables.class));
 
         method.ldc(name);
         value.call(this);
 
         method.invokeinterface(p(InstanceVariables.class), "fastSetInstanceVariable", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
     }
 
     public void retrieveGlobalVariable(String name) {
         loadRuntime();
 
         invokeIRuby("getGlobalVariables", sig(GlobalVariables.class));
         method.ldc(name);
         method.invokevirtual(p(GlobalVariables.class), "get", sig(IRubyObject.class, params(String.class)));
     }
 
     public void assignGlobalVariable(String name) {
         // FIXME: more efficient with a callback
         loadRuntime();
 
         invokeIRuby("getGlobalVariables", sig(GlobalVariables.class));
         method.swap();
         method.ldc(name);
         method.swap();
         method.invokevirtual(p(GlobalVariables.class), "set", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
     }
 
     public void assignGlobalVariable(String name, CompilerCallback value) {
         // FIXME: more efficient with a callback
         loadRuntime();
 
         invokeIRuby("getGlobalVariables", sig(GlobalVariables.class));
         method.ldc(name);
         value.call(this);
         method.invokevirtual(p(GlobalVariables.class), "set", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
     }
 
     public void negateCurrentValue() {
         loadRuntime();
         invokeUtilityMethod("negate", sig(IRubyObject.class, IRubyObject.class, Ruby.class));
     }
 
     public void splatCurrentValue() {
         method.invokestatic(p(RuntimeHelpers.class), "splatValue", sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void singlifySplattedValue() {
         method.invokestatic(p(RuntimeHelpers.class), "aValueSplat", sig(IRubyObject.class, params(IRubyObject.class)));
     }
 
     public void aryToAry() {
         method.invokestatic(p(RuntimeHelpers.class), "aryToAry", sig(IRubyObject.class, params(IRubyObject.class)));
     }
 
     public void ensureRubyArray() {
         invokeUtilityMethod("ensureRubyArray", sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void ensureMultipleAssignableRubyArray(boolean masgnHasHead) {
         loadRuntime();
         method.pushBoolean(masgnHasHead);
         invokeUtilityMethod("ensureMultipleAssignableRubyArray", sig(RubyArray.class, params(IRubyObject.class, Ruby.class, boolean.class)));
     }
 
     public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback, ArrayCallback nilCallback, CompilerCallback argsCallback) {
         // FIXME: This could probably be made more efficient
         for (; start < count; start++) {
             method.dup(); // dup the original array object
             loadNil();
             method.pushInt(start);
             invokeUtilityMethod("arrayEntryOrNil", sig(IRubyObject.class, RubyArray.class, IRubyObject.class, int.class));
             callback.nextValue(this, source, start);
             method.pop();
         }
 
         if (argsCallback != null) {
             method.dup(); // dup the original array object
             loadRuntime();
             method.pushInt(start);
             invokeUtilityMethod("subarrayOrEmpty", sig(RubyArray.class, RubyArray.class, Ruby.class, int.class));
             argsCallback.call(this);
             method.pop();
         }
     }
 
     public void asString() {
         method.invokeinterface(p(IRubyObject.class), "asString", sig(RubyString.class));
     }
 
     public void toJavaString() {
         method.invokevirtual(p(Object.class), "toString", sig(String.class));
     }
 
     public void nthRef(int match) {
         method.pushInt(match);
         backref();
         method.invokestatic(p(RubyRegexp.class), "nth_match", sig(IRubyObject.class, params(Integer.TYPE, IRubyObject.class)));
     }
 
     public void match() {
         loadThreadContext();
         method.invokevirtual(p(RubyRegexp.class), "op_match2", sig(IRubyObject.class, params(ThreadContext.class)));
     }
 
     public void match2() {
         loadThreadContext();
         method.swap();
         method.invokevirtual(p(RubyRegexp.class), "op_match", sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class)));
     }
 
     public void match3() {
         loadThreadContext();
         invokeUtilityMethod("match3", sig(IRubyObject.class, RubyRegexp.class, IRubyObject.class, ThreadContext.class));
     }
 
     public void createNewRegexp(final ByteList value, final int options) {
         String regexpField = script.getNewConstant(ci(RubyRegexp.class), "lit_reg_");
 
         // in current method, load the field to see if we've created a Pattern yet
         method.aload(StandardASMCompiler.THIS);
         method.getfield(script.getClassname(), regexpField, ci(RubyRegexp.class));
 
         Label alreadyCreated = new Label();
         method.ifnonnull(alreadyCreated); //[]
 
         // load string, for Regexp#source and Regexp#inspect
         String regexpString = value.toString();
 
         loadRuntime(); //[R]
         method.ldc(regexpString); //[R, rS]
         method.pushInt(options); //[R, rS, opts]
 
         method.invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, params(Ruby.class, String.class, Integer.TYPE))); //[reg]
 
         method.aload(StandardASMCompiler.THIS); //[reg, T]
         method.swap(); //[T, reg]
         method.putfield(script.getClassname(), regexpField, ci(RubyRegexp.class)); //[]
         method.label(alreadyCreated);
         method.aload(StandardASMCompiler.THIS); //[T]
         method.getfield(script.getClassname(), regexpField, ci(RubyRegexp.class));
     }
 
     public void createNewRegexp(CompilerCallback createStringCallback, final int options) {
         boolean onceOnly = (options & ReOptions.RE_OPTION_ONCE) != 0;   // for regular expressions with the /o flag
         Label alreadyCreated = null;
         String regexpField = null;
 
         // only alter the code if the /o flag was present
         if (onceOnly) {
             regexpField = script.getNewConstant(ci(RubyRegexp.class), "lit_reg_");
 
             // in current method, load the field to see if we've created a Pattern yet
             method.aload(StandardASMCompiler.THIS);
             method.getfield(script.getClassname(), regexpField, ci(RubyRegexp.class));
 
             alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
         }
 
         loadRuntime();
 
         createStringCallback.call(this);
         method.invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
         method.pushInt(options);
 
         method.invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, params(Ruby.class, ByteList.class, Integer.TYPE))); //[reg]
 
         // only alter the code if the /o flag was present
         if (onceOnly) {
             method.aload(StandardASMCompiler.THIS);
             method.swap();
             method.putfield(script.getClassname(), regexpField, ci(RubyRegexp.class));
             method.label(alreadyCreated);
             method.aload(StandardASMCompiler.THIS);
             method.getfield(script.getClassname(), regexpField, ci(RubyRegexp.class));
         }
     }
 
     public void pollThreadEvents() {
         if (!RubyInstanceConfig.THREADLESS_COMPILE_ENABLED) {
             loadThreadContext();
             invokeThreadContext("pollThreadEvents", sig(Void.TYPE));
         }
     }
 
     public void nullToNil() {
         Label notNull = new Label();
         method.dup();
         method.ifnonnull(notNull);
         method.pop();
         loadNil();
         method.label(notNull);
     }
 
     public void isInstanceOf(Class clazz, BranchCallback trueBranch, BranchCallback falseBranch) {
         method.instance_of(p(clazz));
 
         Label falseJmp = new Label();
         Label afterJmp = new Label();
 
         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
 
         method.go_to(afterJmp);
         method.label(falseJmp);
 
         falseBranch.branch(this);
 
         method.label(afterJmp);
     }
 
     public void isCaptured(final int number, final BranchCallback trueBranch, final BranchCallback falseBranch) {
         backref();
         method.dup();
         isInstanceOf(RubyMatchData.class, new BranchCallback() {
 
             public void branch(BodyCompiler context) {
                 method.visitTypeInsn(CHECKCAST, p(RubyMatchData.class));
                 method.dup();
                 method.invokevirtual(p(RubyMatchData.class), "use", sig(void.class));
                 method.pushInt(number);
                 method.invokevirtual(p(RubyMatchData.class), "group", sig(IRubyObject.class, params(int.class)));
                 method.invokeinterface(p(IRubyObject.class), "isNil", sig(boolean.class));
                 Label isNil = new Label();
                 Label after = new Label();
 
                 method.ifne(isNil);
                 trueBranch.branch(context);
                 method.go_to(after);
 
                 method.label(isNil);
                 falseBranch.branch(context);
                 method.label(after);
             }
         }, new BranchCallback() {
 
             public void branch(BodyCompiler context) {
                 method.pop();
                 falseBranch.branch(context);
             }
         });
     }
 
     public void branchIfModule(CompilerCallback receiverCallback, BranchCallback moduleCallback, BranchCallback notModuleCallback, boolean mustBeModule) {
         receiverCallback.call(this);
         invokeUtilityMethod("checkIsModule", sig(RubyModule.class, IRubyObject.class));
         isInstanceOf(RubyModule.class, moduleCallback, notModuleCallback);
     }
 
     public void backref() {
         loadThreadContext();
         invokeThreadContext("getCurrentFrame", sig(Frame.class));
         method.invokevirtual(p(Frame.class), "getBackRef", sig(IRubyObject.class));
     }
 
     public void backrefMethod(String methodName) {
         backref();
         method.invokestatic(p(RubyRegexp.class), methodName, sig(IRubyObject.class, params(IRubyObject.class)));
     }
 
     public void issueLoopBreak() {
         // inside a loop, break out of it
         // go to end of loop, leaving break value on stack
         method.go_to(currentLoopLabels[2]);
     }
 
     public void issueLoopNext() {
         // inside a loop, jump to conditional
         method.go_to(currentLoopLabels[0]);
     }
 
     public void issueLoopRedo() {
         // inside a loop, jump to body
         method.go_to(currentLoopLabels[1]);
     }
 
     protected String getNewEnsureName() {
         return "ensure_" + (script.getAndIncrementEnsureNumber()) + "$RUBY$__ensure__";
     }
 
     public void protect(BranchCallback regularCode, BranchCallback protectedCode, Class ret) {
 
         String mname = getNewEnsureName();
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(script.getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}), null, null));
         SkinnyMethodAdapter old_method = null;
         SkinnyMethodAdapter var_old_method = null;
         SkinnyMethodAdapter inv_old_method = null;
         boolean oldWithinProtection = withinProtection;
         withinProtection = true;
         Label[] oldLoopLabels = currentLoopLabels;
         currentLoopLabels = null;
         int oldArgCount = argParamCount;
         argParamCount = 0; // synthetic methods always have zero arg parameters
         try {
             old_method = this.method;
             var_old_method = getVariableCompiler().getMethodAdapter();
             inv_old_method = getInvocationCompiler().getMethodAdapter();
             this.method = mv;
             getVariableCompiler().setMethodAdapter(mv);
             getInvocationCompiler().setMethodAdapter(mv);
 
             mv.visitCode();
             // set up a local IRuby variable
 
             mv.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             mv.dup();
             mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             mv.dup();
             mv.astore(getRuntimeIndex());
 
             // grab nil for local variables
             mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             mv.astore(getNilIndex());
 
             mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             mv.dup();
             mv.astore(getDynamicScopeIndex());
             mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             mv.astore(getVarsArrayIndex());
 
             Label codeBegin = new Label();
             Label codeEnd = new Label();
             Label ensureBegin = new Label();
             Label ensureEnd = new Label();
             method.label(codeBegin);
 
             regularCode.branch(this);
 
             method.label(codeEnd);
 
             protectedCode.branch(this);
             mv.areturn();
 
             method.label(ensureBegin);
             method.astore(getExceptionIndex());
             method.label(ensureEnd);
 
             protectedCode.branch(this);
 
             method.aload(getExceptionIndex());
             method.athrow();
 
             method.trycatch(codeBegin, codeEnd, ensureBegin, null);
             method.trycatch(ensureBegin, ensureEnd, ensureBegin, null);
 
             mv.visitMaxs(1, 1);
             mv.visitEnd();
         } finally {
             this.method = old_method;
             getVariableCompiler().setMethodAdapter(var_old_method);
             getInvocationCompiler().setMethodAdapter(inv_old_method);
             withinProtection = oldWithinProtection;
             currentLoopLabels = oldLoopLabels;
             argParamCount = oldArgCount;
         }
 
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         loadSelf();
         if (this instanceof ChildScopedBodyCompiler) {
             pushNull();
         } else {
             loadBlock();
         }
         method.invokevirtual(script.getClassname(), mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}));
     }
 
     protected String getNewRescueName() {
         return "rescue_" + (script.getAndIncrementRescueNumber()) + "$RUBY$__rescue__";
     }
 
     public void rescue(BranchCallback regularCode, Class exception, BranchCallback catchCode, Class ret) {
         String mname = getNewRescueName();
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(script.getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}), null, null));
         SkinnyMethodAdapter old_method = null;
         SkinnyMethodAdapter var_old_method = null;
         SkinnyMethodAdapter inv_old_method = null;
         Label afterMethodBody = new Label();
         Label catchRetry = new Label();
         Label catchRaised = new Label();
         Label catchJumps = new Label();
         Label exitRescue = new Label();
         boolean oldWithinProtection = withinProtection;
         withinProtection = true;
         Label[] oldLoopLabels = currentLoopLabels;
         currentLoopLabels = null;
         int oldArgCount = argParamCount;
         argParamCount = 0; // synthetic methods always have zero arg parameters
         try {
             old_method = this.method;
             var_old_method = getVariableCompiler().getMethodAdapter();
             inv_old_method = getInvocationCompiler().getMethodAdapter();
             this.method = mv;
             getVariableCompiler().setMethodAdapter(mv);
             getInvocationCompiler().setMethodAdapter(mv);
 
             mv.visitCode();
 
             // set up a local IRuby variable
             mv.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             mv.dup();
             mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             mv.dup();
             mv.astore(getRuntimeIndex());
 
             // store previous exception for restoration if we rescue something
             loadRuntime();
             invokeUtilityMethod("getErrorInfo", sig(IRubyObject.class, Ruby.class));
             mv.astore(getPreviousExceptionIndex());
 
             // grab nil for local variables
             mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             mv.astore(getNilIndex());
 
             mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             mv.dup();
             mv.astore(getDynamicScopeIndex());
             mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             mv.astore(getVarsArrayIndex());
 
             Label beforeBody = new Label();
             Label afterBody = new Label();
             Label catchBlock = new Label();
             mv.visitTryCatchBlock(beforeBody, afterBody, catchBlock, p(exception));
             mv.visitLabel(beforeBody);
 
             regularCode.branch(this);
 
             mv.label(afterBody);
             mv.go_to(exitRescue);
             mv.label(catchBlock);
             mv.astore(getExceptionIndex());
 
             catchCode.branch(this);
 
             mv.label(afterMethodBody);
             mv.go_to(exitRescue);
 
             // retry handling in the rescue block
             mv.trycatch(catchBlock, afterMethodBody, catchRetry, p(JumpException.RetryJump.class));
             mv.label(catchRetry);
             mv.pop();
             mv.go_to(beforeBody);
 
             // any exceptions raised must continue to be raised, skipping $! restoration
             mv.trycatch(beforeBody, afterMethodBody, catchRaised, p(RaiseException.class));
             mv.label(catchRaised);
             mv.athrow();
 
             // and remaining jump exceptions should restore $!
             mv.trycatch(beforeBody, afterMethodBody, catchJumps, p(JumpException.class));
             mv.label(catchJumps);
             loadRuntime();
             mv.aload(getPreviousExceptionIndex());
             invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
             mv.athrow();
 
             mv.label(exitRescue);
 
             // restore the original exception
             loadRuntime();
             mv.aload(getPreviousExceptionIndex());
             invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
 
             mv.areturn();
             mv.visitMaxs(1, 1);
             mv.visitEnd();
         } finally {
             withinProtection = oldWithinProtection;
             this.method = old_method;
             getVariableCompiler().setMethodAdapter(var_old_method);
             getInvocationCompiler().setMethodAdapter(inv_old_method);
             currentLoopLabels = oldLoopLabels;
             argParamCount = oldArgCount;
         }
 
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         loadSelf();
         if (this instanceof ChildScopedBodyCompiler) {
             pushNull();
         } else {
             loadBlock();
         }
         method.invokevirtual(script.getClassname(), mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}));
     }
 
     public void performRescue(BranchCallback regularCode, BranchCallback rubyCatchCode, BranchCallback javaCatchCode) {
         String mname = getNewRescueName();
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(script.getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}), null, null));
         SkinnyMethodAdapter old_method = null;
         SkinnyMethodAdapter var_old_method = null;
         SkinnyMethodAdapter inv_old_method = null;
         Label afterRubyCatchBody = new Label();
         Label afterJavaCatchBody = new Label();
         Label rubyCatchRetry = new Label();
         Label rubyCatchRaised = new Label();
         Label rubyCatchJumps = new Label();
         Label javaCatchRetry = new Label();
         Label javaCatchRaised = new Label();
         Label javaCatchJumps = new Label();
         Label exitRescue = new Label();
         boolean oldWithinProtection = withinProtection;
         withinProtection = true;
         Label[] oldLoopLabels = currentLoopLabels;
         currentLoopLabels = null;
         int oldArgCount = argParamCount;
         argParamCount = 0; // synthetic methods always have zero arg parameters
         try {
             old_method = this.method;
             var_old_method = getVariableCompiler().getMethodAdapter();
             inv_old_method = getInvocationCompiler().getMethodAdapter();
             this.method = mv;
             getVariableCompiler().setMethodAdapter(mv);
             getInvocationCompiler().setMethodAdapter(mv);
 
             mv.visitCode();
 
             // set up a local IRuby variable
             mv.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             mv.dup();
             mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             mv.dup();
             mv.astore(getRuntimeIndex());
 
             // store previous exception for restoration if we rescue something
             loadRuntime();
             invokeUtilityMethod("getErrorInfo", sig(IRubyObject.class, Ruby.class));
             mv.astore(getPreviousExceptionIndex());
 
             // grab nil for local variables
             mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             mv.astore(getNilIndex());
 
             mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             mv.dup();
             mv.astore(getDynamicScopeIndex());
             mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             mv.astore(getVarsArrayIndex());
 
             Label beforeBody = new Label();
             Label afterBody = new Label();
             Label rubyCatchBlock = new Label();
             Label flowCatchBlock = new Label();
             Label javaCatchBlock = new Label();
             mv.visitTryCatchBlock(beforeBody, afterBody, rubyCatchBlock, p(RaiseException.class));
             mv.visitTryCatchBlock(beforeBody, afterBody, flowCatchBlock, p(JumpException.FlowControlException.class));
             mv.visitTryCatchBlock(beforeBody, afterBody, javaCatchBlock, p(Exception.class));
 
             mv.visitLabel(beforeBody);
             {
                 regularCode.branch(this);
             }
             mv.label(afterBody);
             mv.go_to(exitRescue);
 
             // first handle Ruby exceptions (RaiseException)
             mv.label(rubyCatchBlock);
             {
                 mv.astore(getExceptionIndex());
 
                 rubyCatchCode.branch(this);
                 mv.label(afterRubyCatchBody);
                 mv.go_to(exitRescue);
 
                 // retry handling in the rescue block
                 mv.trycatch(rubyCatchBlock, afterRubyCatchBody, rubyCatchRetry, p(JumpException.RetryJump.class));
                 mv.label(rubyCatchRetry);
                 {
                     mv.pop();
                 }
                 mv.go_to(beforeBody);
 
                 // any exceptions raised must continue to be raised, skipping $! restoration
                 mv.trycatch(beforeBody, afterRubyCatchBody, rubyCatchRaised, p(RaiseException.class));
                 mv.label(rubyCatchRaised);
                 {
                     mv.athrow();
                 }
 
                 // and remaining jump exceptions should restore $!
                 mv.trycatch(beforeBody, afterRubyCatchBody, rubyCatchJumps, p(JumpException.class));
                 mv.label(rubyCatchJumps);
                 {
                     loadRuntime();
                     mv.aload(getPreviousExceptionIndex());
                     invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
                     mv.athrow();
                 }
             }
 
             // Next handle Flow exceptions, just propagating them
             mv.label(flowCatchBlock);
             {
                 // restore the original exception
                 loadRuntime();
                 mv.aload(getPreviousExceptionIndex());
                 invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
 
                 // rethrow
                 mv.athrow();
             }
 
             // now handle Java exceptions
             mv.label(javaCatchBlock);
             {
                 mv.astore(getExceptionIndex());
 
                 javaCatchCode.branch(this);
                 mv.label(afterJavaCatchBody);
                 mv.go_to(exitRescue);
 
                 // retry handling in the rescue block
                 mv.trycatch(javaCatchBlock, afterJavaCatchBody, javaCatchRetry, p(JumpException.RetryJump.class));
                 mv.label(javaCatchRetry);
                 {
                     mv.pop();
                 }
                 mv.go_to(beforeBody);
 
                 // any exceptions raised must continue to be raised, skipping $! restoration
                 mv.trycatch(javaCatchBlock, afterJavaCatchBody, javaCatchRaised, p(RaiseException.class));
                 mv.label(javaCatchRaised);
                 {
                     mv.athrow();
                 }
 
                 // and remaining jump exceptions should restore $!
                 mv.trycatch(javaCatchBlock, afterJavaCatchBody, javaCatchJumps, p(JumpException.class));
                 mv.label(javaCatchJumps);
                 {
                     loadRuntime();
                     mv.aload(getPreviousExceptionIndex());
                     invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
                     mv.athrow();
                 }
             }
 
             mv.label(exitRescue);
 
             // restore the original exception
             loadRuntime();
             mv.aload(getPreviousExceptionIndex());
             invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
 
             mv.areturn();
             mv.visitMaxs(1, 1);
             mv.visitEnd();
         } finally {
             withinProtection = oldWithinProtection;
             this.method = old_method;
             getVariableCompiler().setMethodAdapter(var_old_method);
             getInvocationCompiler().setMethodAdapter(inv_old_method);
             currentLoopLabels = oldLoopLabels;
             argParamCount = oldArgCount;
         }
 
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         loadSelf();
         if (this instanceof ChildScopedBodyCompiler) {
             pushNull();
         } else {
             loadBlock();
         }
         method.invokevirtual(script.getClassname(), mname, sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}));
     }
 
     public void wrapJavaException() {
         loadRuntime();
         loadException();
         wrapJavaObject();
     }
 
     public void wrapJavaObject() {
         method.invokestatic(p(JavaUtil.class), "convertJavaToUsableRubyObject", sig(IRubyObject.class, Ruby.class, Object.class));
     }
 
     public void inDefined() {
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
         method.iconst_1();
         invokeThreadContext("setWithinDefined", sig(void.class, params(boolean.class)));
     }
 
     public void outDefined() {
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
         method.iconst_0();
         invokeThreadContext("setWithinDefined", sig(void.class, params(boolean.class)));
     }
 
     public void stringOrNil() {
         loadRuntime();
         loadNil();
         invokeUtilityMethod("stringOrNil", sig(IRubyObject.class, String.class, Ruby.class, IRubyObject.class));
     }
 
     public void pushNull() {
         method.aconst_null();
     }
 
     public void pushString(String str) {
         method.ldc(str);
     }
 
     public void isMethodBound(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
         metaclass();
         method.ldc(name);
         method.iconst_0(); // push false
         method.invokevirtual(p(RubyClass.class), "isMethodBound", sig(boolean.class, params(String.class, boolean.class)));
         Label falseLabel = new Label();
         Label exitLabel = new Label();
         method.ifeq(falseLabel); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(exitLabel);
         method.label(falseLabel);
         falseBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void hasBlock(BranchCallback trueBranch, BranchCallback falseBranch) {
         loadBlock();
         method.invokevirtual(p(Block.class), "isGiven", sig(boolean.class));
         Label falseLabel = new Label();
         Label exitLabel = new Label();
         method.ifeq(falseLabel); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(exitLabel);
         method.label(falseLabel);
         falseBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void isGlobalDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
         loadRuntime();
         invokeIRuby("getGlobalVariables", sig(GlobalVariables.class));
         method.ldc(name);
         method.invokevirtual(p(GlobalVariables.class), "isDefined", sig(boolean.class, params(String.class)));
         Label falseLabel = new Label();
         Label exitLabel = new Label();
         method.ifeq(falseLabel); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(exitLabel);
         method.label(falseLabel);
         falseBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void isConstantDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
         loadThreadContext();
         method.ldc(name);
         invokeThreadContext("getConstantDefined", sig(boolean.class, params(String.class)));
         Label falseLabel = new Label();
         Label exitLabel = new Label();
         method.ifeq(falseLabel); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(exitLabel);
         method.label(falseLabel);
         falseBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void isInstanceVariableDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
         loadSelf();
         invokeIRubyObject("getInstanceVariables", sig(InstanceVariables.class));
         method.ldc(name);
         //method.invokeinterface(p(IRubyObject.class), "getInstanceVariable", sig(IRubyObject.class, params(String.class)));
         method.invokeinterface(p(InstanceVariables.class), "fastHasInstanceVariable", sig(boolean.class, params(String.class)));
         Label trueLabel = new Label();
         Label exitLabel = new Label();
         //method.ifnonnull(trueLabel);
         method.ifne(trueLabel);
         falseBranch.branch(this);
         method.go_to(exitLabel);
         method.label(trueLabel);
         trueBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void isClassVarDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
         method.ldc(name);
         method.invokevirtual(p(RubyModule.class), "fastIsClassVarDefined", sig(boolean.class, params(String.class)));
         Label trueLabel = new Label();
         Label exitLabel = new Label();
         method.ifne(trueLabel);
         falseBranch.branch(this);
         method.go_to(exitLabel);
         method.label(trueLabel);
         trueBranch.branch(this);
diff --git a/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java b/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java
index 2eb6f0d66d..ecf079a8f0 100644
--- a/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java
+++ b/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java
@@ -1,828 +1,846 @@
 /*
  * SkinnyMethodAdapter.java
  *
  * Created on March 10, 2007, 2:52 AM
  *
  * To change this template, choose Tools | Template Manager
  * and open the template in the editor.
  */
 
 package org.jruby.compiler.impl;
 
 import java.io.PrintStream;
 import static org.jruby.util.CodegenUtils.*;
 import org.objectweb.asm.AnnotationVisitor;
 import org.objectweb.asm.Attribute;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.MethodVisitor;
 import org.objectweb.asm.Opcodes;
 
 /**
  *
  * @author headius
  */
 public class SkinnyMethodAdapter implements MethodVisitor, Opcodes {
     private MethodVisitor method;
     
     /** Creates a new instance of SkinnyMethodAdapter */
     public SkinnyMethodAdapter(MethodVisitor method) {
         this.method = method;
     }
     
     public SkinnyMethodAdapter() {
     }
     
     public MethodVisitor getMethodVisitor() {
         return method;
     }
     
     public void setMethodVisitor(MethodVisitor mv) {
         this.method = mv;
     }
     
     public void aload(int arg0) {
         getMethodVisitor().visitVarInsn(ALOAD, arg0);
     }
     
     public void iload(int arg0) {
         getMethodVisitor().visitVarInsn(ILOAD, arg0);
     }
     
     public void lload(int arg0) {
         getMethodVisitor().visitVarInsn(LLOAD, arg0);
     }
     
     public void fload(int arg0) {
         getMethodVisitor().visitVarInsn(FLOAD, arg0);
     }
     
     public void dload(int arg0) {
         getMethodVisitor().visitVarInsn(DLOAD, arg0);
     }
     
     public void astore(int arg0) {
         getMethodVisitor().visitVarInsn(ASTORE, arg0);
     }
     
     public void istore(int arg0) {
         getMethodVisitor().visitVarInsn(ISTORE, arg0);
     }
     
     public void lstore(int arg0) {
         getMethodVisitor().visitVarInsn(LSTORE, arg0);
     }
     
     public void fstore(int arg0) {
         getMethodVisitor().visitVarInsn(FSTORE, arg0);
     }
     
     public void dstore(int arg0) {
         getMethodVisitor().visitVarInsn(DSTORE, arg0);
     }
     
     public void ldc(Object arg0) {
         getMethodVisitor().visitLdcInsn(arg0);
     }
     
     public void bipush(int arg) {
         getMethodVisitor().visitIntInsn(BIPUSH, arg);
     }
     
     public void sipush(int arg) {
         getMethodVisitor().visitIntInsn(SIPUSH, arg);
     }
         
     public void pushInt(int value) {
         if (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE) {
             switch (value) {
             case -1:
                 iconst_m1();
                 break;
             case 0:
                 iconst_0();
                 break;
             case 1:
                 iconst_1();
                 break;
             case 2:
                 iconst_2();
                 break;
             case 3:
                 iconst_3();
                 break;
             case 4:
                 iconst_4();
                 break;
             case 5:
                 iconst_5();
                 break;
             default:
                 bipush(value);
                 break;
             }
         } else if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE) {
             sipush(value);
         } else {
             ldc(value);
         }
     }
         
     public void pushBoolean(boolean bool) {
         if (bool) iconst_1(); else iconst_0();
     }
     
     public void invokestatic(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitMethodInsn(INVOKESTATIC, arg1, arg2, arg3);
     }
     
     public void invokespecial(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitMethodInsn(INVOKESPECIAL, arg1, arg2, arg3);
     }
     
     public void invokevirtual(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitMethodInsn(INVOKEVIRTUAL, arg1, arg2, arg3);
     }
     
     public void invokeinterface(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitMethodInsn(INVOKEINTERFACE, arg1, arg2, arg3);
     }
     
     public void aprintln() {
         dup();
         getstatic(p(System.class), "out", ci(PrintStream.class));
         swap();
         invokevirtual(p(PrintStream.class), "println", sig(void.class, params(Object.class)));
     }
     
     public void areturn() {
         getMethodVisitor().visitInsn(ARETURN);
     }
     
     public void ireturn() {
         getMethodVisitor().visitInsn(IRETURN);
     }
     
     public void freturn() {
         getMethodVisitor().visitInsn(FRETURN);
     }
     
     public void lreturn() {
         getMethodVisitor().visitInsn(LRETURN);
     }
     
     public void dreturn() {
         getMethodVisitor().visitInsn(DRETURN);
     }
     
     public void newobj(String arg0) {
         getMethodVisitor().visitTypeInsn(NEW, arg0);
     }
     
     public void dup() {
         getMethodVisitor().visitInsn(DUP);
     }
     
     public void swap() {
         getMethodVisitor().visitInsn(SWAP);
     }
     
     public void swap2() {
         dup2_x2();
         pop2();
     }
     
     public void getstatic(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitFieldInsn(GETSTATIC, arg1, arg2, arg3);
     }
     
     public void putstatic(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitFieldInsn(PUTSTATIC, arg1, arg2, arg3);
     }
     
     public void getfield(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitFieldInsn(GETFIELD, arg1, arg2, arg3);
     }
     
     public void putfield(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitFieldInsn(PUTFIELD, arg1, arg2, arg3);
     }
     
     public void voidreturn() {
         getMethodVisitor().visitInsn(RETURN);
     }
     
     public void anewarray(String arg0) {
         getMethodVisitor().visitTypeInsn(ANEWARRAY, arg0);
     }
     
     public void multianewarray(String arg0, int dims) {
         getMethodVisitor().visitMultiANewArrayInsn(arg0, dims);
     }
     
     public void newarray(int arg0) {
         getMethodVisitor().visitIntInsn(NEWARRAY, arg0);
     }
     
     public void iconst_m1() {
         getMethodVisitor().visitInsn(ICONST_M1);
     }
     
     public void iconst_0() {
         getMethodVisitor().visitInsn(ICONST_0);
     }
     
     public void iconst_1() {
         getMethodVisitor().visitInsn(ICONST_1);
     }
     
     public void iconst_2() {
         getMethodVisitor().visitInsn(ICONST_2);
     }
     
     public void iconst_3() {
         getMethodVisitor().visitInsn(ICONST_3);
     }
     
     public void iconst_4() {
         getMethodVisitor().visitInsn(ICONST_4);
     }
     
     public void iconst_5() {
         getMethodVisitor().visitInsn(ICONST_5);
     }
     
     public void lconst_0() {
         getMethodVisitor().visitInsn(LCONST_0);
     }
     
     public void aconst_null() {
         getMethodVisitor().visitInsn(ACONST_NULL);
     }
     
     public void label(Label label) {
         getMethodVisitor().visitLabel(label);
     }
     
     public void nop() {
         getMethodVisitor().visitInsn(NOP);
     }
     
     public void pop() {
         getMethodVisitor().visitInsn(POP);
     }
     
     public void pop2() {
         getMethodVisitor().visitInsn(POP2);
     }
     
     public void arrayload() {
         getMethodVisitor().visitInsn(AALOAD);
     }
     
     public void arraystore() {
         getMethodVisitor().visitInsn(AASTORE);
     }
     
     public void iarrayload() {
         getMethodVisitor().visitInsn(IALOAD);
     }
     
     public void barrayload() {
         getMethodVisitor().visitInsn(BALOAD);
     }
     
     public void barraystore() {
         getMethodVisitor().visitInsn(BASTORE);
     }
     
     public void aaload() {
         getMethodVisitor().visitInsn(AALOAD);
     }
     
     public void aastore() {
         getMethodVisitor().visitInsn(AASTORE);
     }
     
     public void iaload() {
         getMethodVisitor().visitInsn(IALOAD);
     }
     
     public void iastore() {
         getMethodVisitor().visitInsn(IASTORE);
     }
     
     public void laload() {
         getMethodVisitor().visitInsn(LALOAD);
     }
     
     public void lastore() {
         getMethodVisitor().visitInsn(LASTORE);
     }
     
     public void baload() {
         getMethodVisitor().visitInsn(BALOAD);
     }
     
     public void bastore() {
         getMethodVisitor().visitInsn(BASTORE);
     }
     
     public void saload() {
         getMethodVisitor().visitInsn(SALOAD);
     }
     
     public void sastore() {
         getMethodVisitor().visitInsn(SASTORE);
     }
     
     public void caload() {
         getMethodVisitor().visitInsn(CALOAD);
     }
     
     public void castore() {
         getMethodVisitor().visitInsn(CASTORE);
     }
     
     public void faload() {
         getMethodVisitor().visitInsn(FALOAD);
     }
     
     public void fastore() {
         getMethodVisitor().visitInsn(FASTORE);
     }
     
     public void daload() {
         getMethodVisitor().visitInsn(DALOAD);
     }
     
     public void dastore() {
         getMethodVisitor().visitInsn(DASTORE);
     }
     
     public void fcmpl() {
         getMethodVisitor().visitInsn(FCMPL);
     }
     
     public void fcmpg() {
         getMethodVisitor().visitInsn(FCMPG);
     }
     
     public void dcmpl() {
         getMethodVisitor().visitInsn(DCMPL);
     }
     
     public void dcmpg() {
         getMethodVisitor().visitInsn(DCMPG);
     }
     
     public void dup_x2() {
         getMethodVisitor().visitInsn(DUP_X2);
     }
     
     public void dup_x1() {
         getMethodVisitor().visitInsn(DUP_X1);
     }
     
     public void dup2_x2() {
         getMethodVisitor().visitInsn(DUP2_X2);
     }
     
     public void dup2_x1() {
         getMethodVisitor().visitInsn(DUP2_X1);
     }
     
     public void dup2() {
         getMethodVisitor().visitInsn(DUP2);
     }
     
     public void trycatch(Label arg0, Label arg1, Label arg2,
                                    String arg3) {
         getMethodVisitor().visitTryCatchBlock(arg0, arg1, arg2, arg3);
     }
     
+    public void trycatch(String type, Runnable body, Runnable catchBody) {
+        Label before = new Label();
+        Label after = new Label();
+        Label catchStart = new Label();
+        Label done = new Label();
+
+        trycatch(before, after, catchStart, type);
+        label(before);
+        body.run();
+        label(after);
+        go_to(done);
+        if (catchBody != null) {
+            label(catchStart);
+            catchBody.run();
+        }
+        label(done);
+    }
+    
     public void go_to(Label arg0) {
         getMethodVisitor().visitJumpInsn(GOTO, arg0);
     }
     
     public void lookupswitch(Label arg0, int[] arg1, Label[] arg2) {
         getMethodVisitor().visitLookupSwitchInsn(arg0, arg1, arg2);
     }
     
     public void athrow() {
         getMethodVisitor().visitInsn(ATHROW);
     }
     
     public void instance_of(String arg0) {
         getMethodVisitor().visitTypeInsn(INSTANCEOF, arg0);
     }
     
     public void ifeq(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFEQ, arg0);
     }
     
     public void ifne(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFNE, arg0);
     }
     
     public void if_acmpne(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ACMPNE, arg0);
     }
     
     public void if_acmpeq(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ACMPEQ, arg0);
     }
     
     public void if_icmple(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ICMPLE, arg0);
     }
     
     public void if_icmpgt(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ICMPGT, arg0);
     }
     
     public void if_icmplt(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ICMPLT, arg0);
     }
     
     public void if_icmpne(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ICMPNE, arg0);
     }
     
     public void if_icmpeq(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ICMPEQ, arg0);
     }
     
     public void checkcast(String arg0) {
         getMethodVisitor().visitTypeInsn(CHECKCAST, arg0);
     }
     
     public void start() {
         getMethodVisitor().visitCode();
     }
     
     public void end() {
         getMethodVisitor().visitMaxs(1, 1);
         getMethodVisitor().visitEnd();
     }
     
     public void ifnonnull(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFNONNULL, arg0);
     }
     
     public void ifnull(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFNULL, arg0);
     }
     
     public void iflt(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFLT, arg0);
     }
     
     public void ifle(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFLE, arg0);
     }
     
     public void ifgt(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFGT, arg0);
     }
     
     public void ifge(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFGE, arg0);
     }
     
     public void arraylength() {
         getMethodVisitor().visitInsn(ARRAYLENGTH);
     }
     
     public void ishr() {
         getMethodVisitor().visitInsn(ISHR);
     }
     
     public void ishl() {
         getMethodVisitor().visitInsn(ISHL);
     }
     
     public void iushr() {
         getMethodVisitor().visitInsn(IUSHR);
     }
     
     public void lshr() {
         getMethodVisitor().visitInsn(LSHR);
     }
     
     public void lshl() {
         getMethodVisitor().visitInsn(LSHL);
     }
     
     public void lushr() {
         getMethodVisitor().visitInsn(LUSHR);
     }
     
     public void lcmp() {
         getMethodVisitor().visitInsn(LCMP);
     }
     
     public void iand() {
         getMethodVisitor().visitInsn(IAND);
     }
     
     public void ior() {
         getMethodVisitor().visitInsn(IOR);
     }
     
     public void ixor() {
         getMethodVisitor().visitInsn(IXOR);
     }
     
     public void land() {
         getMethodVisitor().visitInsn(LAND);
     }
     
     public void lor() {
         getMethodVisitor().visitInsn(LOR);
     }
     
     public void lxor() {
         getMethodVisitor().visitInsn(LXOR);
     }
     
     public void iadd() {
         getMethodVisitor().visitInsn(IADD);
     }
     
     public void ladd() {
         getMethodVisitor().visitInsn(LADD);
     }
     
     public void fadd() {
         getMethodVisitor().visitInsn(FADD);
     }
     
     public void dadd() {
         getMethodVisitor().visitInsn(DADD);
     }
     
     public void isub() {
         getMethodVisitor().visitInsn(ISUB);
     }
     
     public void lsub() {
         getMethodVisitor().visitInsn(LSUB);
     }
     
     public void fsub() {
         getMethodVisitor().visitInsn(FSUB);
     }
     
     public void dsub() {
         getMethodVisitor().visitInsn(DSUB);
     }
     
     public void idiv() {
         getMethodVisitor().visitInsn(IDIV);
     }
     
     public void irem() {
         getMethodVisitor().visitInsn(IREM);
     }
     
     public void ineg() {
         getMethodVisitor().visitInsn(INEG);
     }
     
     public void i2d() {
         getMethodVisitor().visitInsn(I2D);
     }
     
     public void i2l() {
         getMethodVisitor().visitInsn(I2L);
     }
     
     public void i2f() {
         getMethodVisitor().visitInsn(I2F);
     }
     
     public void i2s() {
         getMethodVisitor().visitInsn(I2S);
     }
     
     public void i2c() {
         getMethodVisitor().visitInsn(I2C);
     }
     
     public void i2b() {
         getMethodVisitor().visitInsn(I2B);
     }
     
     public void ldiv() {
         getMethodVisitor().visitInsn(LDIV);
     }
     
     public void lrem() {
         getMethodVisitor().visitInsn(LREM);
     }
     
     public void lneg() {
         getMethodVisitor().visitInsn(LNEG);
     }
     
     public void l2d() {
         getMethodVisitor().visitInsn(L2D);
     }
     
     public void l2i() {
         getMethodVisitor().visitInsn(L2I);
     }
     
     public void l2f() {
         getMethodVisitor().visitInsn(L2F);
     }
     
     public void fdiv() {
         getMethodVisitor().visitInsn(FDIV);
     }
     
     public void frem() {
         getMethodVisitor().visitInsn(FREM);
     }
     
     public void fneg() {
         getMethodVisitor().visitInsn(FNEG);
     }
     
     public void f2d() {
         getMethodVisitor().visitInsn(F2D);
     }
     
     public void f2i() {
         getMethodVisitor().visitInsn(F2D);
     }
     
     public void f2l() {
         getMethodVisitor().visitInsn(F2L);
     }
     
     public void ddiv() {
         getMethodVisitor().visitInsn(DDIV);
     }
     
     public void drem() {
         getMethodVisitor().visitInsn(DREM);
     }
     
     public void dneg() {
         getMethodVisitor().visitInsn(DNEG);
     }
     
     public void d2f() {
         getMethodVisitor().visitInsn(D2F);
     }
     
     public void d2i() {
         getMethodVisitor().visitInsn(D2I);
     }
     
     public void d2l() {
         getMethodVisitor().visitInsn(D2L);
     }
     
     public void imul() {
         getMethodVisitor().visitInsn(IMUL);
     }
     
     public void lmul() {
         getMethodVisitor().visitInsn(LMUL);
     }
     
     public void fmul() {
         getMethodVisitor().visitInsn(FMUL);
     }
     
     public void dmul() {
         getMethodVisitor().visitInsn(DMUL);
     }
     
     public void iinc(int arg0, int arg1) {
         getMethodVisitor().visitIincInsn(arg0, arg1);
     }
     
     public void monitorenter() {
         getMethodVisitor().visitInsn(MONITORENTER);
     }
     
     public void monitorexit() {
         getMethodVisitor().visitInsn(MONITOREXIT);
     }
     
     public void jsr(Label branch) {
         getMethodVisitor().visitJumpInsn(JSR, branch);
     }
     
     public void ret(int arg0) {
         getMethodVisitor().visitVarInsn(RET, arg0);
     }
     
     public AnnotationVisitor visitAnnotationDefault() {
         return getMethodVisitor().visitAnnotationDefault();
     }
 
     public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
         return getMethodVisitor().visitAnnotation(arg0, arg1);
     }
 
     public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1,
                                                       boolean arg2) {
         return getMethodVisitor().visitParameterAnnotation(arg0, arg1, arg2);
     }
 
     public void visitAttribute(Attribute arg0) {
         getMethodVisitor().visitAttribute(arg0);
     }
 
     public void visitCode() {
         getMethodVisitor().visitCode();
     }
 
     public void visitInsn(int arg0) {
         getMethodVisitor().visitInsn(arg0);
     }
 
     public void visitIntInsn(int arg0, int arg1) {
         getMethodVisitor().visitIntInsn(arg0, arg1);
     }
 
     public void visitVarInsn(int arg0, int arg1) {
         getMethodVisitor().visitVarInsn(arg0, arg1);
     }
 
     public void visitTypeInsn(int arg0, String arg1) {
         getMethodVisitor().visitTypeInsn(arg0, arg1);
     }
 
     public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {
         getMethodVisitor().visitFieldInsn(arg0, arg1, arg2, arg3);
     }
 
     public void visitMethodInsn(int arg0, String arg1, String arg2, String arg3) {
         getMethodVisitor().visitMethodInsn(arg0, arg1, arg2, arg3);
     }
 
     public void visitJumpInsn(int arg0, Label arg1) {
         getMethodVisitor().visitJumpInsn(arg0, arg1);
     }
 
     public void visitLabel(Label arg0) {
         getMethodVisitor().visitLabel(arg0);
     }
 
     public void visitLdcInsn(Object arg0) {
         getMethodVisitor().visitLdcInsn(arg0);
     }
 
     public void visitIincInsn(int arg0, int arg1) {
         getMethodVisitor().visitIincInsn(arg0, arg1);
     }
 
     public void visitTableSwitchInsn(int arg0, int arg1, Label arg2,
                                      Label[] arg3) {
         getMethodVisitor().visitTableSwitchInsn(arg0, arg1, arg2, arg3);
     }
 
     public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
         getMethodVisitor().visitLookupSwitchInsn(arg0, arg1, arg2);
     }
 
     public void visitMultiANewArrayInsn(String arg0, int arg1) {
         getMethodVisitor().visitMultiANewArrayInsn(arg0, arg1);
     }
 
     public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2,
                                    String arg3) {
         getMethodVisitor().visitTryCatchBlock(arg0, arg1, arg2, arg3);
     }
 
     public void visitLocalVariable(String arg0, String arg1, String arg2,
                                    Label arg3, Label arg4, int arg5) {
         getMethodVisitor().visitLocalVariable(arg0, arg1, arg2, arg3, arg4, arg5);
     }
 
     public void visitLineNumber(int arg0, Label arg1) {
         getMethodVisitor().visitLineNumber(arg0, arg1);
     }
 
     public void visitMaxs(int arg0, int arg1) {
         getMethodVisitor().visitMaxs(arg0, arg1);
     }
 
     public void visitEnd() {
         getMethodVisitor().visitEnd();
     }
     
     public void tableswitch(int min, int max, Label defaultLabel, Label[] cases) {
         getMethodVisitor().visitTableSwitchInsn(min, max, defaultLabel, cases);
     }
 
     public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
         getMethodVisitor().visitFrame(arg0, arg1, arg2, arg3, arg4);
     }
 
 }
diff --git a/src/org/jruby/evaluator/ASTInterpreter.java b/src/org/jruby/evaluator/ASTInterpreter.java
index b62ba4e6ee..84b512f01f 100644
--- a/src/org/jruby/evaluator/ASTInterpreter.java
+++ b/src/org/jruby/evaluator/ASTInterpreter.java
@@ -1,398 +1,399 @@
 /*
  ******************************************************************************
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
 import org.jruby.RubyArray;
 import org.jruby.RubyBinding;
+import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.JumpException;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.EventHook;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.InterpretedBlock;
 import org.jruby.util.TypeConverter;
 
 public class ASTInterpreter {
     @Deprecated
     public static IRubyObject eval(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block block) {
         assert self != null : "self during eval must never be null";
         
         // TODO: Make into an assert once I get things like blockbodynodes to be implicit nil
         if (node == null) return runtime.getNil();
         
         try {
             return node.interpret(runtime, context, self, block);
         } catch (StackOverflowError sfe) {
             throw runtime.newSystemStackError("stack level too deep");
         }
     }
     
     /**
      * Evaluate the given string under the specified binding object. If the binding is not a Proc or Binding object
      * (RubyProc or RubyBinding) throw an appropriate type error.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @param file The filename to use when reporting errors during the evaluation
      * @param lineNumber is the line number to pretend we are starting from
      * @return An IRubyObject result from the evaluation
      */
     public static IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, 
             String file, int lineNumber) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         //assert file != null;
 
         Ruby runtime = src.getRuntime();
         String savedFile = context.getFile();
         int savedLine = context.getLine();
 
         if (!(scope instanceof RubyBinding)) {
             if (scope instanceof RubyProc) {
                 scope = ((RubyProc) scope).binding();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw runtime.newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
 
         Binding binding = ((RubyBinding)scope).getBinding();
         DynamicScope evalScope = binding.getDynamicScope().getEvalScope();
 
         // If no explicit file passed in we will use the bindings location
         if (file == null) file = binding.getFrame().getFile();
         if (lineNumber == -1) lineNumber = binding.getFrame().getLine();
         
         // FIXME:  This determine module is in a strange location and should somehow be in block
         evalScope.getStaticScope().determineModule();
 
         Frame lastFrame = context.preEvalWithBinding(binding);
         try {
             // Binding provided for scope, use it
             IRubyObject newSelf = binding.getSelf();
             RubyString source = src.convertToString();
             Node node = runtime.parseEval(source.getByteList(), file, evalScope, lineNumber);
 
             return node.interpret(runtime, context, newSelf, binding.getFrame().getBlock());
         } catch (JumpException.BreakJump bj) {
-            throw runtime.newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
+            throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
-            throw runtime.newLocalJumpError("redo", (IRubyObject)rj.getValue(), "unexpected redo");
+            throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.REDO, (IRubyObject)rj.getValue(), "unexpected redo");
         } catch (StackOverflowError sfe) {
             throw runtime.newSystemStackError("stack level too deep");            
         } finally {
             context.postEvalWithBinding(binding, lastFrame);
 
             // restore position
             context.setFile(savedFile);
             context.setLine(savedLine);
         }
     }
 
     /**
      * Evaluate the given string.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param file The filename to use when reporting errors during the evaluation
      * @param lineNumber that the eval supposedly starts from
      * @return An IRubyObject result from the evaluation
      * @deprecated Call with a RubyString now.
      */
     public static IRubyObject evalSimple(ThreadContext context, IRubyObject self, IRubyObject src, String file, int lineNumber) {
         RubyString source = src.convertToString();
         return evalSimple(context, self, source, file, lineNumber);
     }
 
     /**
      * Evaluate the given string.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param file The filename to use when reporting errors during the evaluation
      * @param lineNumber that the eval supposedly starts from
      * @return An IRubyObject result from the evaluation
      */
     public static IRubyObject evalSimple(ThreadContext context, IRubyObject self, RubyString src, String file, int lineNumber) {
         // this is ensured by the callers
         assert file != null;
 
         Ruby runtime = src.getRuntime();
         String savedFile = context.getFile();
         int savedLine = context.getLine();
 
         // no binding, just eval in "current" frame (caller's frame)
         RubyString source = src.convertToString();
         
         DynamicScope evalScope = context.getCurrentScope().getEvalScope();
         evalScope.getStaticScope().determineModule();
         
         try {
             Node node = runtime.parseEval(source.getByteList(), file, evalScope, lineNumber);
             
             return node.interpret(runtime, context, self, Block.NULL_BLOCK);
         } catch (JumpException.BreakJump bj) {
-            throw runtime.newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
+            throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "unexpected break");
         } catch (StackOverflowError sfe) {
             throw runtime.newSystemStackError("stack level too deep");
         } finally {
             // restore position
             context.setFile(savedFile);
             context.setLine(savedLine);
         }
     }
 
 
     public static void callTraceFunction(Ruby runtime, ThreadContext context, RubyEvent event) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         runtime.callEventHooks(context, event, context.getFile(), context.getLine(), name, type);
     }
     
     public static IRubyObject pollAndReturn(ThreadContext context, IRubyObject result) {
         context.pollThreadEvents();
 
         return result;
     }
     
     public static IRubyObject multipleAsgnArrayNode(Ruby runtime, ThreadContext context, MultipleAsgnNode iVisited, ArrayNode node, IRubyObject self, Block aBlock) {
         IRubyObject[] array = new IRubyObject[node.size()];
 
         for (int i = 0; i < node.size(); i++) {
             array[i] = node.get(i).interpret(runtime,context, self, aBlock);
         }
         return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, RubyArray.newArrayNoCopyLight(runtime, array), false);
     }
 
     /** Evaluates the body in a class or module definition statement.
      *
      */
     public static IRubyObject evalClassDefinitionBody(Ruby runtime, ThreadContext context, StaticScope scope, 
             Node bodyNode, RubyModule type, IRubyObject self, Block block) {
         context.preClassEval(scope, type);
 
         try {
             if (runtime.hasEventHooks()) {
                 callTraceFunction(runtime, context, RubyEvent.CLASS);
             }
 
             if (bodyNode == null) return runtime.getNil();
             return bodyNode.interpret(runtime, context, type, block);
         } finally {
             if (runtime.hasEventHooks()) {
                 callTraceFunction(runtime, context, RubyEvent.END);
             }
             
             context.postClassEval();
         }
     }
 
     public static String getArgumentDefinition(Ruby runtime, ThreadContext context, Node node, String type, IRubyObject self, Block block) {
         if (node == null) return type;
             
         if (node instanceof ArrayNode) {
             ArrayNode list = (ArrayNode) node;
             int size = list.size();
 
             for (int i = 0; i < size; i++) {
                 if (list.get(i).definition(runtime, context, self, block) == null) return null;
             }
         } else if (node.definition(runtime, context, self, block) == null) {
             return null;
         }
 
         return type;
     }
     
     public static Block getBlock(Ruby runtime, ThreadContext context, IRubyObject self, Block currentBlock, Node blockNode) {
         if (blockNode == null) return Block.NULL_BLOCK;
         
         if (blockNode instanceof IterNode) {
             return getIterNodeBlock(blockNode, context,self);
         } else if (blockNode instanceof BlockPassNode) {
             return getBlockPassBlock(blockNode, runtime,context, self, currentBlock);
         }
          
         assert false: "Trying to get block from something which cannot deliver";
         return null;
     }
 
     private static Block getBlockPassBlock(Node blockNode, Ruby runtime, ThreadContext context, IRubyObject self, Block currentBlock) {
         Node bodyNode = ((BlockPassNode) blockNode).getBodyNode();
         IRubyObject proc;
         if (bodyNode == null) {
             proc = runtime.getNil();
         } else {
             proc = bodyNode.interpret(runtime, context, self, currentBlock);
         }
 
         return RuntimeHelpers.getBlockFromBlockPassBody(proc, currentBlock);
     }
 
     private static Block getIterNodeBlock(Node blockNode, ThreadContext context, IRubyObject self) {
         IterNode iterNode = (IterNode) blockNode;
 
         StaticScope scope = iterNode.getScope();
         scope.determineModule();
 
         // Create block for this iter node
         // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
         return InterpretedBlock.newInterpretedClosure(context, iterNode.getBlockBody(), self);
     }
 
     /* Something like cvar_cbase() from eval.c, factored out for the benefit
      * of all the classvar-related node evaluations */
     public static RubyModule getClassVariableBase(ThreadContext context, Ruby runtime) {
         StaticScope scope = context.getCurrentScope().getStaticScope();
         RubyModule rubyClass = scope.getModule();
         if (rubyClass.isSingleton() || rubyClass == runtime.getDummy()) {
             scope = scope.getPreviousCRefScope();
             rubyClass = scope.getModule();
             if (scope.getPreviousCRefScope() == null) {
                 runtime.getWarnings().warn(ID.CVAR_FROM_TOPLEVEL_SINGLETON_METHOD, "class variable access from toplevel singleton method");
             }            
         }
         return rubyClass;
     }
 
     @Deprecated
     public static String getDefinition(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         try {
             context.setWithinDefined(true);
             return node.definition(runtime, context, self, aBlock);
         } finally {
             context.setWithinDefined(false);
         }
     }
 
     public static IRubyObject[] setupArgs(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         if (node == null) return IRubyObject.NULL_ARRAY;
 
         if (node instanceof ArrayNode) {
             ArrayNode argsArrayNode = (ArrayNode) node;
             String savedFile = context.getFile();
             int savedLine = context.getLine();
             int size = argsArrayNode.size();
             IRubyObject[] argsArray = new IRubyObject[size];
 
             for (int i = 0; i < size; i++) {
                 argsArray[i] = argsArrayNode.get(i).interpret(runtime, context, self, aBlock);
             }
 
             context.setFile(savedFile);
             context.setLine(savedLine);
 
             return argsArray;
         }
 
         return ArgsUtil.convertToJavaArray(node.interpret(runtime,context, self, aBlock));
     }
 
     @Deprecated
     public static IRubyObject aValueSplat(Ruby runtime, IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return runtime.getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first(IRubyObject.NULL_ARRAY) : array;
     }
 
     @Deprecated
     public static RubyArray arrayValue(Ruby runtime, IRubyObject value) {
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
             // remove this hack too.
             if (value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime.getKernel()) {
                 value = value.callMethod(runtime.getCurrentContext(), "to_a");
                 if (!(value instanceof RubyArray)) throw runtime.newTypeError("`to_a' did not return Array");
                 return (RubyArray)value;
             } else {
                 return runtime.newArray(value);
             }
         }
         return (RubyArray)tmp;
     }
 
     @Deprecated
     public static IRubyObject aryToAry(Ruby runtime, IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return TypeConverter.convertToType(value, runtime.getArray(), MethodIndex.TO_A, "to_ary", false);
         }
 
         return runtime.newArray(value);
     }
 
     @Deprecated
     public static RubyArray splatValue(Ruby runtime, IRubyObject value) {
         if (value.isNil()) {
             return runtime.newArray(value);
         }
 
         return arrayValue(runtime, value);
     }
 
     // Used by the compiler to simplify arg processing
     @Deprecated
     public static RubyArray splatValue(IRubyObject value, Ruby runtime) {
         return splatValue(runtime, value);
     }
     @Deprecated
     public static IRubyObject aValueSplat(IRubyObject value, Ruby runtime) {
         return aValueSplat(runtime, value);
     }
     @Deprecated
     public static IRubyObject aryToAry(IRubyObject value, Ruby runtime) {
         return aryToAry(runtime, value);
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/DynamicMethod.java b/src/org/jruby/internal/runtime/methods/DynamicMethod.java
index dc3f032134..ec8631be45 100644
--- a/src/org/jruby/internal/runtime/methods/DynamicMethod.java
+++ b/src/org/jruby/internal/runtime/methods/DynamicMethod.java
@@ -1,463 +1,464 @@
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
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
+import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyModule;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * DynamicMethod represents a method handle in JRuby, to provide both entry
  * points into AST and bytecode interpreters, but also to provide handles to
  * JIT-compiled and hand-implemented Java methods. All methods invokable from
  * Ruby code are referenced by method handles, either directly or through
  * delegation or callback mechanisms.
  */
 public abstract class DynamicMethod {
     /** The Ruby module or class in which this method is immediately defined. */
     protected RubyModule implementationClass;
     /** The "protected class" used for calculating protected access. */
     protected RubyModule protectedClass;
     /** The visibility of this method. */
     protected Visibility visibility;
     /** The "call configuration" to use for pre/post call logic. */
     protected CallConfiguration callConfig;
     
     /**
      * Base constructor for dynamic method handles.
      * 
      * @param implementationClass The class to which this method will be
      * immediately bound
      * @param visibility The visibility assigned to this method
      * @param callConfig The CallConfiguration to use for this method's
      * pre/post invocation logic.
      */
     protected DynamicMethod(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
         assert implementationClass != null;
         init(implementationClass, visibility, callConfig);
     }
     
     /**
      * A no-arg constructor used only by the UndefinedMethod subclass and
      * CompiledMethod handles. instanceof assertions make sure this is so.
      */
     protected DynamicMethod() {
 //        assert (this instanceof UndefinedMethod ||
 //                this instanceof CompiledMethod ||
 //                this instanceof );
     }
     
     protected void init(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
         this.visibility = visibility;
         this.implementationClass = implementationClass;
         // TODO: Determine whether we should perhaps store non-singleton class
         // in the implementationClass
         this.protectedClass = calculateProtectedClass(implementationClass);
         this.callConfig = callConfig;
     }
 
     /**
      * The minimum 'call' method required for a dynamic method handle.
      * Subclasses must impleemnt this method, but may implement the other
      * signatures to provide faster, non-boxing call paths. Typically
      * subclasses will implement this method to check variable arity calls,
      * then performing a specific-arity invocation to the appropriate method
      * or performing variable-arity logic in-line.
      * 
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param args The argument list to this invocation
      * @param block The block passed to this invocation
      * @return The result of the call
      */
     public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, 
             String name, IRubyObject[] args, Block block);
     
     ////////////////////////////////////////////////////////////////////////////
     // Now we provide default impls of a number of signatures. First, non-block
     // versions of all methods, which just add NULL_BLOCK and re-call, allowing
     // e.g. compiled code, which always can potentially take a block, to only
     // generate the block-receiving signature and still avoid arg boxing.
     ////////////////////////////////////////////////////////////////////////////
     
     /**
      * A default implementation of n-arity, non-block 'call' method,
      * which simply calls the n-arity, block-receiving version with
      * the arg list and Block.NULL_BLOCK.
      * 
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param arg1 The first argument to this invocation
      * @param arg2 The second argument to this invocation
      * @return The result of the call
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, 
             String name, IRubyObject[] args) {
         return call(context, self, clazz, name, args, Block.NULL_BLOCK);
     }
     
     /**
      * A default implementation of one-arity, non-block 'call' method,
      * which simply calls the one-arity, block-receiving version with
      * the argument and Block.NULL_BLOCK.
      * 
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param arg1 The first argument to this invocation
      * @return The result of the call
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg) {
         return call(context, self, klazz, name, arg, Block.NULL_BLOCK);
     }
     
     /**
      * A default implementation of two-arity, non-block 'call' method,
      * which simply calls the two-arity, block-receiving version with
      * the arguments and Block.NULL_BLOCK.
      * 
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param arg1 The first argument to this invocation
      * @param arg2 The second argument to this invocation
      * @return The result of the call
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2) {
         return call(context, self, klazz, name, arg1,arg2, Block.NULL_BLOCK);
     }
     
     /**
      * A default implementation of three-arity, non-block 'call' method,
      * which simply calls the three-arity, block-receiving version with
      * the arguments and Block.NULL_BLOCK.
      * 
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param arg1 The first argument to this invocation
      * @param arg2 The second argument to this invocation
      * @param arg2 The third argument to this invocation
      * @return The result of the call
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return call(context, self, klazz, name, arg1,arg2,arg3, Block.NULL_BLOCK);
     }
     
     /**
      * A default implementation of zero arity, non-block 'call' method,
      * which simply calls the zero-arity, block-receiving version with
      * Block.NULL_BLOCK.
      * 
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param block The block passed to this invocation
      * @return The result of the call
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name) {
         return call(context, self, klazz, name, Block.NULL_BLOCK);
     }
     
     ////////////////////////////////////////////////////////////////////////////
     // Next, we provide default implementations of each block-accepting method
     // that in turn call the IRubyObject[]+Block version of call. This then
     // finally falls back on the minimum implementation requirement for
     // dynamic method handles.
     ////////////////////////////////////////////////////////////////////////////
     
     /**
      * A default implementation of zero arity, block-receiving 'call' method,
      * which simply calls the n-arity, block-receiving version with
      * IRubyObject.NULL_ARRAY.
      * 
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param block The block passed to this invocation
      * @return The result of the call
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, Block block) {
         return call(context, self, klazz, name, IRubyObject.NULL_ARRAY, block);
     }
     
     /**
      * A default implementation of one-arity, block-receiving 'call' method,
      * which simply calls the n-arity, block-receiving version with
      * a boxed arg list.
      * 
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param arg The one argument to this method
      * @param block The block passed to this invocation
      * @return The result of the call
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg}, block);
     }
     
     /**
      * A default implementation of two-arity, block-receiving 'call' method,
      * which simply calls the n-arity, block-receiving version with
      * a boxed arg list.
      * 
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param arg1 The first argument to this invocation
      * @param arg2 The second argument to this invocation
      * @param block The block passed to this invocation
      * @return The result of the call
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg1,arg2}, block);
     }
     
     /**
      * A default implementation of three-arity, block-receiving 'call' method,
      * which simply calls the n-arity, block-receiving version with
      * a boxed arg list.
      * 
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param arg1 The first argument to this invocation
      * @param arg2 The second argument to this invocation
      * @param arg2 The third argument to this invocation
      * @param block The block passed to this invocation
      * @return The result of the call
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg1,arg2,arg3}, block);
     }
     
     /**
      * Duplicate this method, returning DynamicMethod referencing the same code
      * and with the same attributes.
      * 
      * It is not required that this method produce a new object if the
      * semantics of the DynamicMethod subtype do not require such.
      * 
      * @return An identical DynamicMethod object to the target.
      */
     public abstract DynamicMethod dup();
 
     /**
      * Determine whether this method is callable from the given object using
      * the given call type.
      * 
      * @param caller The calling object
      * @param callType The type of call
      * @return true if the call would not violate visibility; false otherwise
      */
     public boolean isCallableFrom(IRubyObject caller, CallType callType) {
         switch (visibility) {
         case PUBLIC:
             return true;
         case PRIVATE:
             return callType != CallType.NORMAL;
         case PROTECTED:
             return protectedAccessOk(caller);
         }
         
         return true;
     }
     
     /**
      * Determine whether the given object can safely invoke protected methods on
      * the class this method is bound to.
      * 
      * @param caller The calling object
      * @return true if the calling object can call protected methods; false
      * otherwise
      */
     private boolean protectedAccessOk(IRubyObject caller) {
         return getProtectedClass().isInstance(caller);
     }
     
     /**
      * Calculate, based on given RubyModule, which class in its hierarchy
      * should be used to determine protected access.
      * 
      * @param cls The class from which to calculate
      * @return The class to be used for protected access checking.
      */
     protected static RubyModule calculateProtectedClass(RubyModule cls) {
         // singleton classes don't get their own visibility domain
         if (cls.isSingleton()) cls = cls.getSuperClass();
 
         while (cls.isIncluded()) cls = cls.getMetaClass();
 
         // For visibility we need real meta class and not anonymous one from class << self
         if (cls instanceof MetaClass) cls = ((MetaClass) cls).getRealClass();
         
         return cls;
     }
     
     /**
      * Retrieve the pre-calculated "protected class" used for access checks.
      * 
      * @return The "protected class" for access checks.
      */
     protected RubyModule getProtectedClass() {
         return protectedClass;
     }
     
     /**
      * Retrieve the class or module on which this method is implemented, used
      * for 'super' logic among others.
      * 
      * @return The class on which this method is implemented
      */
     public RubyModule getImplementationClass() {
         return implementationClass;
     }
 
     /**
      * Set the class on which this method is implemented, used for 'super'
      * logic, among others.
      * 
      * @param implClass The class on which this method is implemented
      */
     public void setImplementationClass(RubyModule implClass) {
         implementationClass = implClass;
         protectedClass = calculateProtectedClass(implClass);
     }
 
     /**
      * Get the visibility of this method.
      * 
      * @return The visibility of this method
      */
     public Visibility getVisibility() {
         return visibility;
     }
 
     /**
      * Set the visibility of this method.
      * 
      * @param visibility The visibility of this method
      */
     public void setVisibility(Visibility visibility) {
         this.visibility = visibility;
     }
 
     /**
      * Whether this method is the "undefined" method, used to represent a
      * missing or undef'ed method. Only returns true for UndefinedMethod
      * instances, of which there should be only one (a singleton).
      * 
      * @return true if this method is the undefined method; false otherwise
      */
     public final boolean isUndefined() {
         return this instanceof UndefinedMethod;
     }
 
     /**
      * Retrieve the arity of this method, used for reporting arity to Ruby
      * code. This arity may or may not reflect the actual specific or variable
      * arities of the referenced method.
      * 
      * @return The arity of the method, as reported to Ruby consumers.
      */
     public Arity getArity() {
         return Arity.optional();
     }
     
     /**
      * Get the "real" method contained within this method. This simply returns
      * self except in cases where a method is wrapped to give it a new
      * name or new implementation class (AliasMethod, WrapperMethod, ...).
      * 
      * @return The "real" method associated with this one
      */
     public DynamicMethod getRealMethod() {
         return this;
     }
 
     /**
      * Get the CallConfiguration used for pre/post logic for this method handle.
      * 
      * @return The CallConfiguration for this method handle
      */
     public CallConfiguration getCallConfig() {
         return callConfig;
     }
 
     /**
      * Set the CallConfiguration used for pre/post logic for this method handle.
      * 
      * @param callConfig The CallConfiguration for this method handle
      */
     public void setCallConfig(CallConfiguration callConfig) {
         this.callConfig = callConfig;
     }
     
     /**
      * Returns true if this method is backed by native (i.e. Java) code.
      * 
      * @return true If backed by Java code or JVM bytecode; false otherwise
      */
     public boolean isNative() {
         return false;
     }
 
     protected IRubyObject handleRedo(Ruby runtime) throws RaiseException {
-        throw runtime.newLocalJumpError("redo", runtime.getNil(), "unexpected redo");
+        throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.REDO, runtime.getNil(), "unexpected redo");
     }
 
     protected IRubyObject handleReturn(ThreadContext context, JumpException.ReturnJump rj) {
         if (rj.getTarget() == context.getFrameJumpTarget()) {
             return (IRubyObject) rj.getValue();
         }
         throw rj;
     }
 }
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index c8d30608dd..25e8fec8cd 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -1,1232 +1,1254 @@
 package org.jruby.javasupport.util;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyHash;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyKernel;
 import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.javasupport.JavaClass;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledBlockLight;
 import org.jruby.runtime.CompiledSharedScopeBlock;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.TypeConverter;
 
 /**
  * Helper methods which are called by the compiler.  Note: These will show no consumers, but
  * generated code does call these so don't remove them thinking they are dead code. 
  *
  */
 public class RuntimeHelpers {
     public static IRubyObject doAttrAsgn(IRubyObject value, IRubyObject receiver, ThreadContext context, IRubyObject caller, CallSite callSite) {
         callSite.call(context, caller, receiver, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject value, IRubyObject receiver, IRubyObject arg0, ThreadContext context, IRubyObject caller, CallSite callSite) {
         callSite.call(context, caller, receiver, arg0, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject value, IRubyObject receiver, IRubyObject arg0, IRubyObject arg1, ThreadContext context, IRubyObject caller, CallSite callSite) {
         callSite.call(context, caller, receiver, arg0, arg1, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject value, IRubyObject receiver, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, ThreadContext context, IRubyObject caller, CallSite callSite) {
         callSite.call(context, caller, receiver, arg0, arg1, arg2, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject value, IRubyObject receiver, IRubyObject[] args, ThreadContext context, IRubyObject caller, CallSite callSite) {
         callSite.call(context, caller, receiver, appendToObjectArray(args, value));
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject arg1, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, arg1, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject[] args, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, args);
         return args[args.length - 1];
     }
 
     public static IRubyObject invokeEqqForCaseWhen(IRubyObject receiver, IRubyObject arg, CallSite callSite, ThreadContext context, IRubyObject caller) {
         return callSite.call(context, caller, receiver, arg);
     }
     
     public static CompiledBlockCallback createBlockCallback(Ruby runtime, Object scriptObject, String closureMethod) {
         Class scriptClass = scriptObject.getClass();
         ClassLoader scriptClassLoader = scriptClass.getClassLoader();
         CallbackFactory factory = CallbackFactory.createFactory(runtime, scriptClass, scriptClassLoader);
         
         return factory.getBlockCallback(closureMethod, scriptObject);
     }
     
     public static BlockBody createCompiledBlockBody(ThreadContext context, Object scriptObject, String closureMethod, int arity, 
             String[] staticScopeNames, boolean hasMultipleArgsHead, int argsNodeType, boolean light) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         if (light) {
             return CompiledBlockLight.newCompiledBlockLight(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(context.getRuntime(), scriptObject, closureMethod),
                     hasMultipleArgsHead, argsNodeType);
         } else {
             return CompiledBlock.newCompiledBlock(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(context.getRuntime(), scriptObject, closureMethod),
                     hasMultipleArgsHead, argsNodeType);
         }
     }
     
     public static Block createBlock(ThreadContext context, IRubyObject self, BlockBody body) {
         return CompiledBlock.newCompiledClosure(
                 context,
                 self,
                 body);
     }
     
     public static IRubyObject runBeginBlock(ThreadContext context, IRubyObject self, String[] staticScopeNames, CompiledBlockCallback callback) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         context.preScopedBody(DynamicScope.newDynamicScope(staticScope, context.getCurrentScope()));
         
         Block block = CompiledBlock.newCompiledClosure(context, self, Arity.createArity(0), staticScope, callback, false, BlockBody.ZERO_ARGS);
         
         try {
             block.yield(context, null);
         } finally {
             context.postScopedBody();
         }
         
         return context.getRuntime().getNil();
     }
     
     public static Block createSharedScopeBlock(ThreadContext context, IRubyObject self, int arity, 
             CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType) {
         
         return CompiledSharedScopeBlock.newCompiledSharedScopeClosure(context, self, Arity.createArity(arity), 
                 context.getCurrentScope(), callback, hasMultipleArgsHead, argsNodeType);
     }
     
     public static IRubyObject def(ThreadContext context, IRubyObject self, Object scriptObject, String name, String javaName, String[] scopeNames,
             int arity, int required, int optional, int rest, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
         RubyModule containingClass = context.getRubyClass();
         Visibility visibility = context.getCurrentVisibility();
         
         performNormalMethodChecks(containingClass, runtime, name);
         
         StaticScope scope = creatScopeForClass(context, scopeNames, required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method = constructNormalMethod(name, visibility, factory, containingClass, javaName, arity, scope, scriptObject, callConfig);
         
         addInstanceMethod(containingClass, name, method, visibility,context, runtime);
         
         return runtime.getNil();
     }
     
     public static IRubyObject defs(ThreadContext context, IRubyObject self, IRubyObject receiver, Object scriptObject, String name, String javaName, String[] scopeNames,
             int arity, int required, int optional, int rest, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
 
         RubyClass rubyClass = performSingletonMethodChecks(runtime, receiver, name);
         
         StaticScope scope = creatScopeForClass(context, scopeNames, required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method = constructSingletonMethod(factory, rubyClass, javaName, arity, scope,scriptObject, callConfig);
         
         rubyClass.addMethod(name, method);
         
         callSingletonMethodHook(receiver,context, runtime.fastNewSymbol(name));
         
         return runtime.getNil();
     }
     
     public static RubyClass getSingletonClass(Ruby runtime, IRubyObject receiver) {
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("no virtual class for " + receiver.getMetaClass().getBaseName());
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't extend object.");
             }
 
             return receiver.getSingletonClass();
         }
     }
 
     // TODO: Only used by interface implementation; eliminate it
     public static IRubyObject invokeMethodMissing(IRubyObject receiver, String name, IRubyObject[] args) {
         ThreadContext context = receiver.getRuntime().getCurrentContext();
 
         // store call information so method_missing impl can use it
         context.setLastCallStatusAndVisibility(CallType.FUNCTIONAL, Visibility.PUBLIC);
 
         if (name == "method_missing") {
             return RubyKernel.method_missing(context, receiver, args, Block.NULL_BLOCK);
         }
 
         IRubyObject[] newArgs = prepareMethodMissingArgs(args, context, name);
 
         return invoke(context, receiver, "method_missing", newArgs, Block.NULL_BLOCK);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject[] args, CallType callType, Block block) {
         context.setLastCallStatusAndVisibility(callType, method.getVisibility());
         return callMethodMissingInternal(context, receiver, name, args, block);
     }
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject arg, CallType callType, Block block) {
         context.setLastCallStatusAndVisibility(callType, method.getVisibility());
         return callMethodMissingInternal(context, receiver, name, arg, block);
     }
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject arg0, IRubyObject arg1, CallType callType, Block block) {
         context.setLastCallStatusAndVisibility(callType, method.getVisibility());
         return callMethodMissingInternal(context, receiver, name, arg0, arg1, block);
     }
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType, Block block) {
         context.setLastCallStatusAndVisibility(callType, method.getVisibility());
         return callMethodMissingInternal(context, receiver, name, arg0, arg1, arg2, block);
     }
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 CallType callType, Block block) {
         context.setLastCallStatusAndVisibility(callType, method.getVisibility());
         return callMethodMissingInternal(context, receiver, name, block);
     }
     
     private static IRubyObject callMethodMissingInternal(ThreadContext context, IRubyObject receiver, String name, 
                                                 IRubyObject[] args, Block block) {
         if (name.equals("method_missing")) return RubyKernel.method_missing(context, receiver, args, block);
         IRubyObject[] newArgs = prepareMethodMissingArgs(args, context, name);
         return invoke(context, receiver, "method_missing", newArgs, block);
     }
     private static IRubyObject callMethodMissingInternal(ThreadContext context, IRubyObject receiver, String name, 
                                                 Block block) {
         if (name.equals("method_missing")) return RubyKernel.method_missing(context, receiver, IRubyObject.NULL_ARRAY, block);
         return invoke(context, receiver, "method_missing", context.getRuntime().newSymbol(name), block);
     }
     private static IRubyObject callMethodMissingInternal(ThreadContext context, IRubyObject receiver, String name, 
                                                 IRubyObject arg0, Block block) {
         if (name.equals("method_missing")) return RubyKernel.method_missing(context, receiver, constructObjectArray(arg0), block);
         return invoke(context, receiver, "method_missing", context.getRuntime().newSymbol(name), arg0, block);
     }
     private static IRubyObject callMethodMissingInternal(ThreadContext context, IRubyObject receiver, String name, 
                                                 IRubyObject arg0, IRubyObject arg1, Block block) {
         if (name.equals("method_missing")) return RubyKernel.method_missing(context, receiver, constructObjectArray(arg0,arg1), block);
         return invoke(context, receiver, "method_missing", context.getRuntime().newSymbol(name), arg0, arg1, block);
     }
     private static IRubyObject callMethodMissingInternal(ThreadContext context, IRubyObject receiver, String name, 
                                                 IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         if (name.equals("method_missing")) return RubyKernel.method_missing(context, receiver, constructObjectArray(arg0,arg1,arg2), block);
         return invoke(context, receiver, "method_missing", constructObjectArray(context.getRuntime().newSymbol(name), arg0, arg1, arg2), block);
     }
 
     private static IRubyObject[] prepareMethodMissingArgs(IRubyObject[] args, ThreadContext context, String name) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = context.getRuntime().newSymbol(name);
 
         return newArgs;
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, Block block) {
         return self.getMetaClass().finvoke(context, self, name, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, arg2, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, Block block) {
         return self.getMetaClass().finvoke(context, self, name, args, block);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name) {
         return self.getMetaClass().finvoke(context, self, name);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0) {
         return self.getMetaClass().finvoke(context, self, name, arg0);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, arg2);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args) {
         return self.getMetaClass().finvoke(context, self, name, args);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, CallType callType) {
         return RuntimeHelpers.invoke(context, self, name, IRubyObject.NULL_ARRAY, callType, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, name, args, callType, block);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, name, arg, callType, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject[] args, Block block) {
         return asClass.finvoke(context, self, name, args, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, Block block) {
         return asClass.finvoke(context, self, name, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, Block block) {
         return asClass.finvoke(context, self, name, arg0, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         return asClass.finvoke(context, self, name, arg0, arg1, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return asClass.finvoke(context, self, name, arg0, arg1, arg2, block);
     }
 
     /**
      * The protocol for super method invocation is a bit complicated
      * in Ruby. In real terms it involves first finding the real
      * implementation class (the super class), getting the name of the
      * method to call from the frame, and then invoke that on the
      * super class with the current self as the actual object
      * invoking.
      */
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         
         if (superClass == null) {
             String name = context.getFrameName(); 
             return callMethodMissing(context, self, klazz.searchMethod(name), name, args, CallType.SUPER, block);
         }
         return invokeAs(context, superClass, self, context.getFrameName(), args, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         
         if (superClass == null) {
             String name = context.getFrameName(); 
             return callMethodMissing(context, self, klazz.searchMethod(name), name, CallType.SUPER, block);
         }
         return invokeAs(context, superClass, self, context.getFrameName(), block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         
         if (superClass == null) {
             String name = context.getFrameName(); 
             return callMethodMissing(context, self, klazz.searchMethod(name), name, arg0, CallType.SUPER, block);
         }
         return invokeAs(context, superClass, self, context.getFrameName(), arg0, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         
         if (superClass == null) {
             String name = context.getFrameName(); 
             return callMethodMissing(context, self, klazz.searchMethod(name), name, arg0, arg1, CallType.SUPER, block);
         }
         return invokeAs(context, superClass, self, context.getFrameName(), arg0, arg1, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         
         if (superClass == null) {
             String name = context.getFrameName(); 
             return callMethodMissing(context, self, klazz.searchMethod(name), name, arg0, arg1, arg2, CallType.SUPER, block);
         }
         return invokeAs(context, superClass, self, context.getFrameName(), arg0, arg1, arg2, block);
     }
 
     public static RubyArray ensureRubyArray(IRubyObject value) {
         return ensureRubyArray(value.getRuntime(), value);
     }
 
     public static RubyArray ensureRubyArray(Ruby runtime, IRubyObject value) {
         return value instanceof RubyArray ? (RubyArray)value : RubyArray.newArray(runtime, value);
     }
 
     public static RubyArray ensureMultipleAssignableRubyArray(IRubyObject value, Ruby runtime, boolean masgnHasHead) {
         if (!(value instanceof RubyArray)) {
             value = ArgsUtil.convertToRubyArray(runtime, value, masgnHasHead);
         }
         return (RubyArray) value;
     }
     
     public static IRubyObject fetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.getClassVar(name);
     }
     
     public static IRubyObject fastFetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.fastGetClassVar(internedName);
     }
     
     public static IRubyObject getConstant(ThreadContext context, String internedName) {
         Ruby runtime = context.getRuntime();
 
         return context.getCurrentScope().getStaticScope().getConstantWithConstMissing(runtime, internedName, runtime.getObject());
     }
     
     public static IRubyObject nullToNil(IRubyObject value, Ruby runtime) {
         return value != null ? value : runtime.getNil();
     }
     
     public static RubyClass prepareSuperClass(Ruby runtime, IRubyObject rubyClass) {
         RubyClass.checkInheritable(rubyClass); // use the same logic as in EvaluationState
         return (RubyClass)rubyClass;
     }
     
     public static RubyModule prepareClassNamespace(ThreadContext context, IRubyObject rubyModule) {
         if (rubyModule == null || rubyModule.isNil()) { // the isNil check should go away since class nil::Foo;end is not supposed be correct
             rubyModule = context.getCurrentScope().getStaticScope().getModule();
 
             if (rubyModule == null) {
                 throw context.getRuntime().newTypeError("no outer class/module");
             }
         }
 
         if (rubyModule instanceof RubyModule) {
             return (RubyModule)rubyModule;
         } else {
             throw context.getRuntime().newTypeError(rubyModule + " is not a class/module");
         }
     }
     
     public static IRubyObject setClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name, IRubyObject value) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static IRubyObject fastSetClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName, IRubyObject value) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.fastSetClassVar(internedName, value);
    
         return value;
     }
     
     public static IRubyObject declareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String name, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static IRubyObject fastDeclareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String internedName, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.fastSetClassVar(internedName, value);
    
         return value;
     }
     
     public static void handleArgumentSizes(ThreadContext context, Ruby runtime, int given, int required, int opt, int rest) {
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
     
+    /**
+     * If it's Redo, Next, or Break, rethrow it as a normal exception for while to handle
+     * @param re
+     * @param runtime
+     */
+    public static Throwable unwrapRedoNextBreakOrJustLocalJump(RaiseException re, ThreadContext context) {
+        RubyException exception = re.getException();
+        if (context.getRuntime().getLocalJumpError().isInstance(exception)) {
+            RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
+
+            switch (jumpError.getReason()) {
+            case REDO:
+                return JumpException.REDO_JUMP;
+            case NEXT:
+                return new JumpException.NextJump(jumpError.exit_value());
+            case BREAK:
+                return new JumpException.BreakJump(context.getFrameJumpTarget(), jumpError.exit_value());
+            }
+        }
+        return re;
+    }
+    
     public static String getLocalJumpTypeOrRethrow(RaiseException re) {
         RubyException exception = re.getException();
         Ruby runtime = exception.getRuntime();
         if (runtime.getLocalJumpError().isInstance(exception)) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             IRubyObject reason = jumpError.reason();
 
             return reason.asJavaString();
         }
 
         throw re;
     }
     
     public static IRubyObject unwrapLocalJumpErrorValue(RaiseException re) {
         return ((RubyLocalJumpError)re.getException()).exit_value();
     }
     
     public static IRubyObject processBlockArgument(Ruby runtime, Block block) {
         if (!block.isGiven()) {
             return runtime.getNil();
         }
         
         return processGivenBlock(block, runtime);
     }
 
     private static IRubyObject processGivenBlock(Block block, Ruby runtime) {
         RubyProc blockArg = block.getProcObject();
 
         if (blockArg == null) {
             blockArg = runtime.newBlockPassProc(Block.Type.PROC, block);
             blockArg.getBlock().type = Block.Type.PROC;
         }
 
         return blockArg;
     }
     
     public static Block getBlockFromBlockPassBody(Ruby runtime, IRubyObject proc, Block currentBlock) {
         // No block from a nil proc
         if (proc.isNil()) return Block.NULL_BLOCK;
 
         // If not already a proc then we should try and make it one.
         if (!(proc instanceof RubyProc)) {
             proc = coerceProc(proc, runtime);
         }
 
         return getBlockFromProc(currentBlock, proc);
     }
 
     private static IRubyObject coerceProc(IRubyObject proc, Ruby runtime) throws RaiseException {
         proc = TypeConverter.convertToType(proc, runtime.getProc(), 0, "to_proc", false);
 
         if (!(proc instanceof RubyProc)) {
             throw runtime.newTypeError("wrong argument type " + proc.getMetaClass().getName() + " (expected Proc)");
         }
         return proc;
     }
 
     private static Block getBlockFromProc(Block currentBlock, IRubyObject proc) {
         // TODO: Add safety check for taintedness
         if (currentBlock != null && currentBlock.isGiven()) {
             RubyProc procObject = currentBlock.getProcObject();
             // The current block is already associated with proc.  No need to create a new one
             if (procObject != null && procObject == proc) {
                 return currentBlock;
             }
         }
 
         return ((RubyProc) proc).getBlock();       
     }
     
     public static Block getBlockFromBlockPassBody(IRubyObject proc, Block currentBlock) {
         return getBlockFromBlockPassBody(proc.getRuntime(), proc, currentBlock);
 
     }
     
     public static IRubyObject backref(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         if(backref instanceof RubyMatchData) {
             ((RubyMatchData)backref).use();
         }
         return backref;
     }
     
     public static IRubyObject backrefLastMatch(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.last_match(backref);
     }
     
     public static IRubyObject backrefMatchPre(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_pre(backref);
     }
     
     public static IRubyObject backrefMatchPost(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_post(backref);
     }
     
     public static IRubyObject backrefMatchLast(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_last(backref);
     }
     
     public static IRubyObject callZSuper(Ruby runtime, ThreadContext context, Block block, IRubyObject self) {
         checkSuperDisabledOrOutOfMethod(context);
 
         // Has the method that is calling super received a block argument
         if (!block.isGiven()) block = context.getCurrentFrame().getBlock(); 
         
         return RuntimeHelpers.invokeSuper(context, self, context.getCurrentScope().getArgValues(), block);
     }
     
     public static IRubyObject[] appendToObjectArray(IRubyObject[] array, IRubyObject add) {
         IRubyObject[] newArray = new IRubyObject[array.length + 1];
         System.arraycopy(array, 0, newArray, 0, array.length);
         newArray[array.length] = add;
         return newArray;
     }
     
     public static JumpException.ReturnJump returnJump(IRubyObject result, ThreadContext context) {
         return context.returnJump(result);
     }
     
     public static IRubyObject breakJumpInWhile(JumpException.BreakJump bj, Block aBlock, ThreadContext context) {
         // JRUBY-530, while case
         if (bj.getTarget() == context.getFrameJumpTarget()) {
             return (IRubyObject) bj.getValue();
         }
 
         throw bj;
     }
     
     public static IRubyObject breakJump(ThreadContext context, IRubyObject value) {
         throw new JumpException.BreakJump(context.getFrameJumpTarget(), value);
     }
     
     public static IRubyObject breakLocalJumpError(Ruby runtime, IRubyObject value) {
-        throw runtime.newLocalJumpError("break", value, "unexpected break");
+        throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, value, "unexpected break");
     }
     
     public static IRubyObject[] concatObjectArrays(IRubyObject[] array, IRubyObject[] add) {
         IRubyObject[] newArray = new IRubyObject[array.length + add.length];
         System.arraycopy(array, 0, newArray, 0, array.length);
         System.arraycopy(add, 0, newArray, array.length, add.length);
         return newArray;
     }
     
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject[] exceptions, Ruby runtime, ThreadContext context, IRubyObject self) {
         for (int i = 0; i < exceptions.length; i++) {
             if (!runtime.getModule().isInstance(exceptions[i])) {
                 throw runtime.newTypeError("class or module required for rescue clause");
             }
             IRubyObject result = exceptions[i].callMethod(context, "===", currentException);
             if (result.isTrue()) return result;
         }
         return runtime.getFalse();
     }
     
     public static IRubyObject isJavaExceptionHandled(Exception currentException, IRubyObject[] exceptions, Ruby runtime, ThreadContext context, IRubyObject self) {
         for (int i = 0; i < exceptions.length; i++) {
             if (exceptions[i] instanceof RubyClass) {
                 RubyClass rubyClass = (RubyClass)exceptions[i];
                 JavaClass javaClass = (JavaClass)rubyClass.fastGetInstanceVariable("@java_class");
                 if (javaClass != null) {
                     Class cls = javaClass.javaClass();
                     if (cls.isInstance(currentException)) {
                         return runtime.getTrue();
                     }
                 }
             }
         }
         
         return runtime.getFalse();
     }
     
     public static void checkSuperDisabledOrOutOfMethod(ThreadContext context) {
         if (context.getFrameKlazz() == null) {
             String name = context.getFrameName();
             if (name != null) {
                 throw context.getRuntime().newNameError("superclass method '" + name + "' disabled", name);
             } else {
                 throw context.getRuntime().newNoMethodError("super called outside of method", null, context.getRuntime().getNil());
             }
         }
     }
     
     public static Block ensureSuperBlock(Block given, Block parent) {
         if (!given.isGiven()) {
             return parent;
         }
         return given;
     }
     
     public static RubyModule findImplementerIfNecessary(RubyModule clazz, RubyModule implementationClass) {
         if (implementationClass != null && implementationClass.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             return clazz.findImplementer(implementationClass);
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             return implementationClass;
         }
     }
     
     public static RubyArray createSubarray(RubyArray input, int start) {
         return (RubyArray)input.subseqLight(start, input.size() - start);
     }
     
     public static RubyArray createSubarray(IRubyObject[] input, Ruby runtime, int start) {
         return RubyArray.newArrayNoCopy(runtime, input, start);
     }
     
     public static RubyBoolean isWhenTriggered(IRubyObject expression, IRubyObject expressionsObject, ThreadContext context) {
         RubyArray expressions = RuntimeHelpers.splatValue(expressionsObject);
         for (int j = 0,k = expressions.getLength(); j < k; j++) {
             IRubyObject condition = expressions.eltInternal(j);
 
             if ((expression != null && condition.callMethod(context, MethodIndex.OP_EQQ, "===", expression)
                     .isTrue())
                     || (expression == null && condition.isTrue())) {
                 return context.getRuntime().getTrue();
             }
         }
         
         return context.getRuntime().getFalse();
     }
     
     public static IRubyObject setConstantInModule(IRubyObject module, IRubyObject value, String name, ThreadContext context) {
         return context.setConstantInModule(name, module, value);
     }
     
     public static IRubyObject retryJump() {
         throw JumpException.RETRY_JUMP;
     }
     
     public static IRubyObject redoJump() {
         throw JumpException.REDO_JUMP;
     }
     
     public static IRubyObject redoLocalJumpError(Ruby runtime) {
-        throw runtime.newLocalJumpError("redo", runtime.getNil(), "unexpected redo");
+        throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.REDO, runtime.getNil(), "unexpected redo");
     }
     
     public static IRubyObject nextJump(IRubyObject value) {
         throw new JumpException.NextJump(value);
     }
     
     public static IRubyObject nextLocalJumpError(Ruby runtime, IRubyObject value) {
-        throw runtime.newLocalJumpError("next", value, "unexpected next");
+        throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.NEXT, value, "unexpected next");
     }
     
     public static final int MAX_SPECIFIC_ARITY_OBJECT_ARRAY = 5;
     
     public static IRubyObject[] constructObjectArray(IRubyObject one) {
         return new IRubyObject[] {one};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two) {
         return new IRubyObject[] {one, two};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three) {
         return new IRubyObject[] {one, two, three};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four) {
         return new IRubyObject[] {one, two, three, four};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five) {
         return new IRubyObject[] {one, two, three, four, five};
     }
     
     public static String[] constructStringArray(String one) {
         return new String[] {one};
     }
     
     public static String[] constructStringArray(String one, String two) {
         return new String[] {one, two};
     }
     
     public static String[] constructStringArray(String one, String two, String three) {
         return new String[] {one, two, three};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four) {
         return new String[] {one, two, three, four};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five) {
         return new String[] {one, two, three, four, five};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six) {
         return new String[] {one, two, three, four, five, six};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven) {
         return new String[] {one, two, three, four, five, six, seven};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight) {
         return new String[] {one, two, three, four, five, six, seven, eight};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight, String nine) {
         return new String[] {one, two, three, four, five, six, seven, eight, nine};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight, String nine, String ten) {
         return new String[] {one, two, three, four, five, six, seven, eight, nine, ten};
     }
     
     public static final int MAX_SPECIFIC_ARITY_HASH = 3;
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASet(key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASet(key1, value1);
         hash.fastASet(key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASet(key1, value1);
         hash.fastASet(key2, value2);
         hash.fastASet(key3, value3);
         return hash;
     }
     
     public static IRubyObject defineAlias(ThreadContext context, String newName, String oldName) {
         Ruby runtime = context.getRuntime();
         RubyModule module = context.getRubyClass();
    
         if (module == null) throw runtime.newTypeError("no class to make alias");
    
         module.defineAlias(newName, oldName);
         module.callMethod(context, "method_added", runtime.newSymbol(newName));
    
         return runtime.getNil();
     }
     
     public static IRubyObject getInstanceVariable(Ruby runtime, IRubyObject self, String name) {
         IRubyObject result = self.getInstanceVariables().getInstanceVariable(name);
         
         if (result != null) return result;
         
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + name + " not initialized");
         
         return runtime.getNil();
     }
     
     public static IRubyObject fastGetInstanceVariable(Ruby runtime, IRubyObject self, String internedName) {
         IRubyObject result;
         if ((result = self.getInstanceVariables().fastGetInstanceVariable(internedName)) != null) return result;
         
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + internedName + " not initialized");
         
         return runtime.getNil();
     }
     
     public static IRubyObject negate(IRubyObject value, Ruby runtime) {
         if (value.isTrue()) return runtime.getFalse();
         return runtime.getTrue();
     }
     
     public static IRubyObject stringOrNil(String value, Ruby runtime, IRubyObject nil) {
         if (value == null) return nil;
         return RubyString.newString(runtime, value);
     }
     
     public static void preLoad(ThreadContext context, String[] varNames) {
         StaticScope staticScope = new LocalStaticScope(null, varNames);
         staticScope.setModule(context.getRuntime().getObject());
         DynamicScope scope = DynamicScope.newDynamicScope(staticScope);
         
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
     }
     
     public static void postLoad(ThreadContext context) {
         context.postScopedBody();
     }
     
     public static void registerEndBlock(Block block, Ruby runtime) {
         runtime.pushExitBlock(runtime.newProc(Block.Type.LAMBDA, block));
     }
     
     public static IRubyObject match3(RubyRegexp regexp, IRubyObject value, ThreadContext context) {
         if (value instanceof RubyString) {
             return regexp.op_match(context, value);
         } else {
             return value.callMethod(context, "=~", regexp);
         }
     }
     
     public static IRubyObject getErrorInfo(Ruby runtime) {
         return runtime.getGlobalVariables().get("$!");
     }
     
     public static void setErrorInfo(Ruby runtime, IRubyObject error) {
         runtime.getGlobalVariables().set("$!", error);
     }
 
     public static IRubyObject setLastLine(Ruby runtime, ThreadContext context, IRubyObject value) {
         return context.getCurrentFrame().setLastLine(value);
     }
 
     public static IRubyObject getLastLine(Ruby runtime, ThreadContext context) {
         return context.getCurrentFrame().getLastLine();
     }
 
     public static IRubyObject setBackref(Ruby runtime, ThreadContext context, IRubyObject value) {
         if (!value.isNil() && !(value instanceof RubyMatchData)) throw runtime.newTypeError(value, runtime.getMatchData());
         return context.getCurrentFrame().setBackRef(value);
     }
 
     public static IRubyObject getBackref(Ruby runtime, ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
         return backref;
     }
     
     public static IRubyObject preOpAsgnWithOrAnd(IRubyObject receiver, ThreadContext context, IRubyObject self, CallSite varSite) {
         return varSite.call(context, self, receiver);
     }
     
     public static IRubyObject postOpAsgnWithOrAnd(IRubyObject receiver, IRubyObject value, ThreadContext context, IRubyObject self, CallSite varAsgnSite) {
         varAsgnSite.call(context, self, receiver, value);
         return value;
     }
     
     public static IRubyObject opAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, CallSite varSite, CallSite opSite, CallSite opAsgnSite) {
         IRubyObject var = varSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, arg);
         opAsgnSite.call(context, self, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, arg, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg1, arg2);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, arg1, arg2, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg1, arg2, arg3);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, new IRubyObject[] {arg1, arg2, arg3, result});
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, appendToObjectArray(args, result));
 
         return result;
     }
 
     
     public static IRubyObject opElementAsgnWithOrPartTwoOneArg(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, arg, value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoTwoArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, args[0], args[1], value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoThreeArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, new IRubyObject[] {args[0], args[1], args[2], value});
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoNArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 0, args.length);
         newArgs[args.length] = value;
         asetSite.call(context, self, receiver, newArgs);
         return value;
     }
 
     public static RubyArray arrayValue(IRubyObject value) {
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
             // remove this hack too.
             Ruby runtime = value.getRuntime();
             
             if (value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime.getKernel()) {
                 value = value.callMethod(runtime.getCurrentContext(), "to_a");
                 if (!(value instanceof RubyArray)) throw runtime.newTypeError("`to_a' did not return Array");
                 return (RubyArray)value;
             } else {
                 return runtime.newArray(value);
             }
         }
         return (RubyArray)tmp;
     }
 
     public static IRubyObject aryToAry(IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return TypeConverter.convertToType(value, value.getRuntime().getArray(), MethodIndex.TO_A, "to_ary", false);
         }
 
         return value.getRuntime().newArray(value);
     }
 
     public static IRubyObject aValueSplat(IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return value.getRuntime().getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first() : array;
     }
 
     public static RubyArray splatValue(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newArray(value);
         }
 
         return arrayValue(value);
     }
 
     public static void addInstanceMethod(RubyModule containingClass, String name, DynamicMethod method, Visibility visibility, ThreadContext context, Ruby runtime) {
         containingClass.addMethod(name, method);
 
         RubySymbol sym = runtime.fastNewSymbol(name);
         if (visibility == Visibility.MODULE_FUNCTION) {
             addModuleMethod(containingClass, name, method, context, sym);
         }
 
         callNormalMethodHook(containingClass, context, sym);
     }
 
     private static void addModuleMethod(RubyModule containingClass, String name, DynamicMethod method, ThreadContext context, RubySymbol sym) {
         containingClass.getSingletonClass().addMethod(name, new WrapperMethod(containingClass.getSingletonClass(), method, Visibility.PUBLIC));
         containingClass.callMethod(context, "singleton_method_added", sym);
     }
 
     private static void callNormalMethodHook(RubyModule containingClass, ThreadContext context, RubySymbol name) {
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             callSingletonMethodHook(((MetaClass) containingClass).getAttached(), context, name);
         } else {
             containingClass.callMethod(context, "method_added", name);
         }
     }
 
     private static void callSingletonMethodHook(IRubyObject receiver, ThreadContext context, RubySymbol name) {
         receiver.callMethod(context, "singleton_method_added", name);
     }
 
     private static DynamicMethod constructNormalMethod(String name, Visibility visibility, MethodFactory factory, RubyModule containingClass, String javaName, int arity, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
         DynamicMethod method;
 
         if (name.equals("initialize") || name.equals("initialize_copy") || visibility == Visibility.MODULE_FUNCTION) {
             visibility = Visibility.PRIVATE;
         }
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             method = factory.getCompiledMethodLazily(containingClass, javaName, Arity.createArity(arity), visibility, scope, scriptObject, callConfig);
         } else {
             method = factory.getCompiledMethod(containingClass, javaName, Arity.createArity(arity), visibility, scope, scriptObject, callConfig);
         }
 
         return method;
     }
 
     private static DynamicMethod constructSingletonMethod(MethodFactory factory, RubyClass rubyClass, String javaName, int arity, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
         return factory.getCompiledMethodLazily(rubyClass, javaName, Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject, callConfig);
     }
 
     private static StaticScope creatScopeForClass(ThreadContext context, String[] scopeNames, int required, int optional, int rest) {
 
         StaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), scopeNames);
         scope.determineModule();
         scope.setArities(required, optional, rest);
 
         return scope;
     }
 
     private static void performNormalMethodChecks(RubyModule containingClass, Ruby runtime, String name) throws RaiseException {
 
         if (containingClass == runtime.getDummy()) {
             throw runtime.newTypeError("no class/module to add method");
         }
 
         if (containingClass == runtime.getObject() && name.equals("initialize")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining Object#initialize may cause infinite loop", "Object#initialize");
         }
 
         if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining `" + name + "' may cause serious problem", name);
         }
     }
 
     private static RubyClass performSingletonMethodChecks(Ruby runtime, IRubyObject receiver, String name) throws RaiseException {
 
         if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
             throw runtime.newSecurityError("Insecure; can't define singleton method.");
         }
 
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("can't define singleton method \"" + name + "\" for " + receiver.getMetaClass().getBaseName());
         }
 
         if (receiver.isFrozen()) {
             throw runtime.newFrozenError("object");
         }
         
         RubyClass rubyClass = receiver.getSingletonClass();
 
         if (runtime.getSafeLevel() >= 4 && rubyClass.getMethods().get(name) != null) {
             throw runtime.newSecurityError("redefining method prohibited.");
         }
         
         return rubyClass;
     }
     
     public static IRubyObject arrayEntryOrNil(RubyArray array, IRubyObject nil, int index) {
         if (index < array.getLength()) {
             return array.entry(index);
         } else {
             return nil;
         }
     }
     
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index) {
         if (index < array.getLength()) {
             return createSubarray(array, index);
         } else {
             return RubyArray.newEmptyArray(runtime);
         }
     }
     
     public static RubyModule checkIsModule(IRubyObject maybeModule) {
         if (maybeModule instanceof RubyModule) return (RubyModule)maybeModule;
         
         throw maybeModule.getRuntime().newTypeError(maybeModule + " is not a class/module");
     }
 }
diff --git a/src/org/jruby/runtime/Block.java b/src/org/jruby/runtime/Block.java
index c5baa48326..fe0758fdff 100644
--- a/src/org/jruby/runtime/Block.java
+++ b/src/org/jruby/runtime/Block.java
@@ -1,200 +1,201 @@
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
 
+import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *  Internal live representation of a block ({...} or do ... end).
  */
 public class Block {
     public enum Type { NORMAL, PROC, LAMBDA, THREAD }
     
     /**
      * The Proc that this block is associated with.  When we reference blocks via variable
      * reference they are converted to Proc objects.  We store a reference of the associated
      * Proc object for easy conversion.  
      */
     private RubyProc proc = null;
     
     public Type type = Type.NORMAL;
     
     private final Binding binding;
     
     private final BlockBody body;
     
     private boolean[] escaped = new boolean[] {false};
     
     /**
      * All Block variables should either refer to a real block or this NULL_BLOCK.
      */
     public static final Block NULL_BLOCK = new Block() {
         @Override
         public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
                 RubyModule klass, boolean aValue) {
-            throw context.getRuntime().newLocalJumpError("noreason", (IRubyObject)value, "yield called out of block");
+            throw context.getRuntime().newLocalJumpError(RubyLocalJumpError.Reason.NOREASON, (IRubyObject)value, "yield called out of block");
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject[] args) {
-            throw context.getRuntime().newLocalJumpError("noreason", context.getRuntime().newArrayNoCopy(args), "yield called out of block");
+            throw context.getRuntime().newLocalJumpError(RubyLocalJumpError.Reason.NOREASON, context.getRuntime().newArrayNoCopy(args), "yield called out of block");
         }
 
         @Override
         public IRubyObject yield(ThreadContext context, boolean aValue) {
-            throw context.getRuntime().newLocalJumpError("noreason", (IRubyObject)null, "yield called out of block");
+            throw context.getRuntime().newLocalJumpError(RubyLocalJumpError.Reason.NOREASON, (IRubyObject)null, "yield called out of block");
         }
 
         @Override
         public IRubyObject yield(ThreadContext context, IRubyObject value, boolean aValue) {
-            throw context.getRuntime().newLocalJumpError("noreason", (IRubyObject)value, "yield called out of block");
+            throw context.getRuntime().newLocalJumpError(RubyLocalJumpError.Reason.NOREASON, (IRubyObject)value, "yield called out of block");
         }
 
         @Override
         public IRubyObject yield(ThreadContext context, IRubyObject value) {
-            throw context.getRuntime().newLocalJumpError("noreason", (IRubyObject)value, "yield called out of block");
+            throw context.getRuntime().newLocalJumpError(RubyLocalJumpError.Reason.NOREASON, (IRubyObject)value, "yield called out of block");
         }
         
         @Override
         public Block cloneBlock() {
             return this;
         }
         
         @Override
         public BlockBody getBody() {
             return BlockBody.NULL_BODY;
         }
     };
     
     protected Block() {
         this(null, null);
     }
     
     public Block(BlockBody body, Binding binding) {
         this.body = body;
         this.binding = binding;
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args) {
         return body.call(context, args, binding, type);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value) {
         return body.yield(context, value, binding, type);
     }
     
     public IRubyObject yield(ThreadContext context, boolean aValue) {
         return body.yield(context, null, null, null, aValue, binding, type);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value, boolean aValue) {
         return body.yield(context, value, null, null, aValue, binding, type);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue) {
         return body.yield(context, value, self, klass, aValue, binding, type);
     }
     
     public Block cloneBlock() {
         Block newBlock = body.cloneBlock(binding);
         
         newBlock.type = type;
         newBlock.escaped = escaped;
 
         return newBlock;
     }
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public Arity arity() {
         return body.arity();
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
      * Is the current block a real yield'able block instead a null one
      * 
      * @return true if this is a valid block or false otherwise
      */
     final public boolean isGiven() {
         return this != NULL_BLOCK;
     }
     
     public Binding getBinding() {
         return binding;
     }
     
     public BlockBody getBody() {
         return body;
     }
 
     /**
      * Gets the frame.
      * 
      * @return Returns a RubyFrame
      */
     public Frame getFrame() {
         return binding.getFrame();
     }
     
     public boolean isEscaped() {
         return escaped[0];
     }
     
     public void escape() {
         this.escaped[0] = true;
     }
 }
diff --git a/src/org/jruby/runtime/callsite/CachingCallSite.java b/src/org/jruby/runtime/callsite/CachingCallSite.java
index 9c944417e5..dbcd023ba1 100644
--- a/src/org/jruby/runtime/callsite/CachingCallSite.java
+++ b/src/org/jruby/runtime/callsite/CachingCallSite.java
@@ -1,396 +1,397 @@
 package org.jruby.runtime.callsite;
 
 import org.jruby.RubyClass;
+import org.jruby.RubyLocalJumpError;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.JumpException.BreakJump;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public abstract class CachingCallSite extends CallSite implements CacheMap.CacheSite {
     protected volatile CacheEntry cache = CacheEntry.NULL_CACHE;
     private int misses = 0;
     private static final int MAX_MISSES = 50;
     public static volatile int totalCallSites;
     public static volatile int failedCallSites;
 
     public CachingCallSite(String methodName, CallType callType) {
         super(methodName, callType);
         totalCallSites++;
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject... args) {
         RubyClass selfType = pollAndGetClass(context, self);
         CacheEntry myCache = cache;
         if (myCache.typeOk(selfType)) {
             return myCache.cachedMethod.call(context, self, selfType, methodName, args);
         }
         return cacheAndCall(caller, selfType, args, context, self);
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject[] args, Block block) {
         try {
             RubyClass selfType = pollAndGetClass(context, self);
             CacheEntry myCache = cache;
             if (myCache.typeOk(selfType)) {
                 return myCache.cachedMethod.call(context, self, selfType, methodName, args, block);
             }
             return cacheAndCall(caller, selfType, block, args, context, self);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         }
     }
 
     public IRubyObject callIter(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject[] args, Block block) {
         try {
             RubyClass selfType = pollAndGetClass(context, self);
             CacheEntry myCache = cache;
             if (myCache.typeOk(selfType)) {
                 return myCache.cachedMethod.call(context, self, selfType, methodName, args, block);
             }
             return cacheAndCall(caller, selfType, block, args, context, self);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         } finally {
             block.escape();
         }
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self) {
         RubyClass selfType = pollAndGetClass(context, self);
         CacheEntry myCache = cache;
         if (myCache.typeOk(selfType)) {
             return myCache.cachedMethod.call(context, self, selfType, methodName);
         }
         return cacheAndCall(caller, selfType, context, self);
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, Block block) {
         try {
             RubyClass selfType = pollAndGetClass(context, self);
             CacheEntry myCache = cache;
             if (myCache.typeOk(selfType)) {
                 return myCache.cachedMethod.call(context, self, selfType, methodName, block);
             }
             return cacheAndCall(caller, selfType, block, context, self);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         }
     }
 
     public IRubyObject callIter(ThreadContext context, IRubyObject caller, IRubyObject self, Block block) {
         try {
             RubyClass selfType = pollAndGetClass(context, self);
             CacheEntry myCache = cache;
             if (myCache.typeOk(selfType)) {
                 return myCache.cachedMethod.call(context, self, selfType, methodName, block);
             }
             return cacheAndCall(caller, selfType, block, context, self);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         } finally {
             block.escape();
         }
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject arg1) {
         RubyClass selfType = pollAndGetClass(context, self);
         CacheEntry myCache = cache;
         if (myCache.typeOk(selfType)) {
             return myCache.cachedMethod.call(context, self, selfType, methodName, arg1);
         }
         return cacheAndCall(caller, selfType, context, self, arg1);
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject arg1, Block block) {
         RubyClass selfType = pollAndGetClass(context, self);
         try {
             CacheEntry myCache = cache;
             if (myCache.typeOk(selfType)) {
                 return myCache.cachedMethod.call(context, self, selfType, methodName, arg1, block);
             }
             return cacheAndCall(caller, selfType, block, context, self, arg1);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         }
     }
 
     public IRubyObject callIter(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject arg1, Block block) {
         try {
             RubyClass selfType = pollAndGetClass(context, self);
             CacheEntry myCache = cache;
             if (myCache.typeOk(selfType)) {
                 return myCache.cachedMethod.call(context, self, selfType, methodName, arg1, block);
             }
             return cacheAndCall(caller, selfType, block, context, self, arg1);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         } finally {
             block.escape();
         }
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject arg1, IRubyObject arg2) {
         RubyClass selfType = pollAndGetClass(context, self);
         CacheEntry myCache = cache;
         if (myCache.typeOk(selfType)) {
             return myCache.cachedMethod.call(context, self, selfType, methodName, arg1, arg2);
         }
         return cacheAndCall(caller, selfType, context, self, arg1, arg2);
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject arg1, IRubyObject arg2, Block block) {
         try {
             RubyClass selfType = pollAndGetClass(context, self);
             CacheEntry myCache = cache;
             if (myCache.typeOk(selfType)) {
                 return myCache.cachedMethod.call(context, self, selfType, methodName, arg1, arg2, block);
             }
             return cacheAndCall(caller, selfType, block, context, self, arg1, arg2);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         }
     }
 
     public IRubyObject callIter(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject arg1, IRubyObject arg2, Block block) {
         try {
             RubyClass selfType = pollAndGetClass(context, self);
             CacheEntry myCache = cache;
             if (myCache.typeOk(selfType)) {
                 return myCache.cachedMethod.call(context, self, selfType, methodName, arg1, arg2, block);
             }
             return cacheAndCall(caller, selfType, block, context, self, arg1, arg2);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         } finally {
             block.escape();
         }
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         RubyClass selfType = pollAndGetClass(context, self);
         CacheEntry myCache = cache;
         if (myCache.typeOk(selfType)) {
             return myCache.cachedMethod.call(context, self, selfType, methodName, arg1, arg2, arg3);
         }
         return cacheAndCall(caller, selfType, context, self, arg1, arg2, arg3);
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
         try {
             RubyClass selfType = pollAndGetClass(context, self);
             CacheEntry myCache = cache;
             if (myCache.typeOk(selfType)) {
                 return myCache.cachedMethod.call(context, self, selfType, methodName, arg1, arg2, arg3, block);
             }
             return cacheAndCall(caller, selfType, block, context, self, arg1, arg2, arg3);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         }
     }
 
     public IRubyObject callIter(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
         try {
             RubyClass selfType = pollAndGetClass(context, self);
             CacheEntry myCache = cache;
             if (myCache.typeOk(selfType)) {
                 return myCache.cachedMethod.call(context, self, selfType, methodName, arg1, arg2, arg3, block);
             }
             return cacheAndCall(caller, selfType, block, context, self, arg1, arg2, arg3);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         } finally {
             block.escape();
         }
     }
 
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, Block block, IRubyObject[] args, ThreadContext context, IRubyObject self) {
         DynamicMethod method = selfType.searchMethod(methodName);
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, args, block);
         }
         updateCacheEntry(method, selfType);
         return method.call(context, self, selfType, methodName, args, block);
     }
 
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, IRubyObject[] args, ThreadContext context, IRubyObject self) {
         DynamicMethod method = selfType.searchMethod(methodName);
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, args);
         }
         updateCacheEntry(method, selfType);
         return method.call(context, self, selfType, methodName, args);
     }
 
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, ThreadContext context, IRubyObject self) {
         DynamicMethod method = selfType.searchMethod(methodName);
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method);
         }
         updateCacheEntry(method, selfType);
         return method.call(context, self, selfType, methodName);
     }
 
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, Block block, ThreadContext context, IRubyObject self) {
         DynamicMethod method = selfType.searchMethod(methodName);
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, block);
         }
         updateCacheEntry(method, selfType);
         return method.call(context, self, selfType, methodName, block);
     }
 
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, ThreadContext context, IRubyObject self, IRubyObject arg) {
         DynamicMethod method = selfType.searchMethod(methodName);
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, arg);
         }
         updateCacheEntry(method, selfType);
         return method.call(context, self, selfType, methodName, arg);
     }
 
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, Block block, ThreadContext context, IRubyObject self, IRubyObject arg) {
         DynamicMethod method = selfType.searchMethod(methodName);
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, arg, block);
         }
         updateCacheEntry(method, selfType);
         return method.call(context, self, selfType, methodName, arg, block);
     }
 
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2) {
         DynamicMethod method = selfType.searchMethod(methodName);
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, arg1, arg2);
         }
         updateCacheEntry(method, selfType);
         return method.call(context, self, selfType, methodName, arg1, arg2);
     }
 
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, Block block, ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2) {
         DynamicMethod method = selfType.searchMethod(methodName);
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, arg1, arg2, block);
         }
         updateCacheEntry(method, selfType);
         return method.call(context, self, selfType, methodName, arg1, arg2, block);
     }
 
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         DynamicMethod method = selfType.searchMethod(methodName);
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, arg1, arg2, arg3);
         }
         updateCacheEntry(method, selfType);
         return method.call(context, self, selfType, methodName, arg1, arg2, arg3);
     }
 
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, Block block, ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         DynamicMethod method = selfType.searchMethod(methodName);
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, arg1, arg2, arg3, block);
         }
         updateCacheEntry(method, selfType);
         return method.call(context, self, selfType, methodName, arg1, arg2, arg3, block);
     }
 
     private IRubyObject callMethodMissing(ThreadContext context, IRubyObject self, DynamicMethod method, IRubyObject[] args) {
         return RuntimeHelpers.callMethodMissing(context, self, method, methodName, args, callType, Block.NULL_BLOCK);
     }
 
     private IRubyObject callMethodMissing(ThreadContext context, IRubyObject self, DynamicMethod method) {
         return RuntimeHelpers.callMethodMissing(context, self, method, methodName, callType, Block.NULL_BLOCK);
     }
 
     private IRubyObject callMethodMissing(ThreadContext context, IRubyObject self, DynamicMethod method, Block block) {
         return RuntimeHelpers.callMethodMissing(context, self, method, methodName, callType, block);
     }
 
     private IRubyObject callMethodMissing(ThreadContext context, IRubyObject self, DynamicMethod method, IRubyObject arg) {
         return RuntimeHelpers.callMethodMissing(context, self, method, methodName, arg, callType, Block.NULL_BLOCK);
     }
 
     private IRubyObject callMethodMissing(ThreadContext context, IRubyObject self, DynamicMethod method, IRubyObject[] args, Block block) {
         return RuntimeHelpers.callMethodMissing(context, self, method, methodName, args, callType, block);
     }
 
     private IRubyObject callMethodMissing(ThreadContext context, IRubyObject self, DynamicMethod method, IRubyObject arg, Block block) {
         return RuntimeHelpers.callMethodMissing(context, self, method, methodName, arg, callType, block);
     }
 
     private IRubyObject callMethodMissing(ThreadContext context, IRubyObject self, DynamicMethod method, IRubyObject arg1, IRubyObject arg2) {
         return RuntimeHelpers.callMethodMissing(context, self, method, methodName, arg1, arg2, callType, Block.NULL_BLOCK);
     }
 
     private IRubyObject callMethodMissing(ThreadContext context, IRubyObject self, DynamicMethod method, IRubyObject arg1, IRubyObject arg2, Block block) {
         return RuntimeHelpers.callMethodMissing(context, self, method, methodName, arg1, arg2, callType, block);
     }
 
     private IRubyObject callMethodMissing(ThreadContext context, IRubyObject self, DynamicMethod method, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return RuntimeHelpers.callMethodMissing(context, self, method, methodName, arg1, arg2, arg3, callType, Block.NULL_BLOCK);
     }
 
     private IRubyObject callMethodMissing(ThreadContext context, IRubyObject self, DynamicMethod method, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
         return RuntimeHelpers.callMethodMissing(context, self, method, methodName, arg1, arg2, arg3, callType, block);
     }
 
     protected abstract boolean methodMissing(DynamicMethod method, IRubyObject caller);
 
     private RubyClass pollAndGetClass(ThreadContext context, IRubyObject self) {
         context.callThreadPoll();
         RubyClass selfType = self.getMetaClass();
         return selfType;
     }
 
     private void updateCacheEntry(DynamicMethod method, RubyClass selfType) {
         if (misses < MAX_MISSES) {
             misses++;
             if (misses >= MAX_MISSES) {
                 failedCallSites++;
             }
             cache = new CacheEntry(method, selfType, methodName);
             selfType.getRuntime().getCacheMap().add(method, this);
         }
     }
 
     public void removeCachedMethod() {
         cache = CacheEntry.NULL_CACHE;
     }
 
     private IRubyObject handleBreakJump(ThreadContext context, BreakJump bj) throws BreakJump {
         if (context.getFrameJumpTarget() == bj.getTarget()) {
             return (IRubyObject) bj.getValue();
         }
         throw bj;
     }
 
     private RaiseException retryJumpError(ThreadContext context) {
-        return context.getRuntime().newLocalJumpError("retry", context.getRuntime().getNil(), "retry outside of rescue not supported");
+        return context.getRuntime().newLocalJumpError(RubyLocalJumpError.Reason.RETRY, context.getRuntime().getNil(), "retry outside of rescue not supported");
     }
 }
diff --git a/src/org/jruby/runtime/invokedynamic/InvokeDynamicSupport.java b/src/org/jruby/runtime/invokedynamic/InvokeDynamicSupport.java
index 8cc4f1698a..9dfb48eb1b 100644
--- a/src/org/jruby/runtime/invokedynamic/InvokeDynamicSupport.java
+++ b/src/org/jruby/runtime/invokedynamic/InvokeDynamicSupport.java
@@ -1,312 +1,313 @@
 package org.jruby.runtime.invokedynamic;
 
 import java.dyn.CallSite;
 import java.dyn.Linkage;
 import java.dyn.MethodHandle;
 import java.dyn.MethodHandles;
 import java.dyn.MethodType;
 import org.jruby.RubyClass;
+import org.jruby.RubyLocalJumpError;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap.CacheSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import static org.jruby.util.CodegenUtils.*;
 import org.objectweb.asm.MethodVisitor;
 import org.objectweb.asm.Type;
 
 public class InvokeDynamicSupport {
     public static Object bootstrap(final CallSite site, Object... args) {
         // dynamic call
         IRubyObject self = (IRubyObject) args[0];
         ThreadContext context = (ThreadContext) args[1];
         String methodName = (String) args[2];
         CallType callType = CallType.NORMAL;
         String siteName = site.name();
         boolean iterator = siteName.length() == 2 && siteName.charAt(1) == 'b';
         
         switch (siteName.charAt(0)) {
         case 'c':
             callType = CallType.NORMAL;
             break;
         case 'f':
             callType = CallType.FUNCTIONAL;
             break;
         case 'v':
             callType = CallType.VARIABLE;
             break;
         }
 
         DynamicMethod method = self.getMetaClass().searchMethod(methodName);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, methodName, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method, methodName, callType, Block.NULL_BLOCK);
         }
 
         String dispatcherName = iterator ? "invokeDynamicIter" : "invokeDynamic";
         MethodHandle outHandle = MethodHandles.findStatic(
                 InvokeDynamicSupport.class,
                 dispatcherName,
                 MethodType.make(IRubyObject.class, DynamicMethod.class, site.type().parameterArray()));
         MethodHandle inHandle = MethodHandles.insertArgument(outHandle, method);
         site.setTarget(inHandle);
         
         context.getRuntime().getCacheMap().add(method, new CacheSite() {
             public void removeCachedMethod() {
                 site.setTarget(null);
             }
         });
 
         // do normal invocation this time
         int length = args.length;
         if (args[length - 1] instanceof Block) {
             if (iterator) {
                 switch (length) {
                 case 4:
                     return invokeDynamicIter(method, self, context, methodName, (Block) args[length - 1]);
                 case 5:
                     if (args[length - 2] instanceof IRubyObject[]) {
                         return invokeDynamicIter(method, self, context, methodName, (IRubyObject[]) args[length - 2], (Block) args[length - 1]);
                     } else {
                         return invokeDynamicIter(method, self, context, methodName, (IRubyObject) args[length - 2], (Block) args[length - 1]);
                     }
                 case 6:
                     return invokeDynamicIter(method, self, context, methodName, (IRubyObject) args[length - 3], (IRubyObject) args[length - 2], (Block) args[length - 1]);
                 case 7:
                     return invokeDynamicIter(method, self, context, methodName, (IRubyObject) args[length - 4], (IRubyObject) args[length - 3], (IRubyObject) args[length - 2], (Block) args[length - 1]);
                 }
             } else {
                 switch (length) {
                 case 4:
                     return invokeDynamic(method, self, context, methodName, (Block) args[length - 1]);
                 case 5:
                     if (args[length - 2] instanceof IRubyObject[]) {
                         return invokeDynamic(method, self, context, methodName, (IRubyObject[]) args[length - 2], (Block) args[length - 1]);
                     } else {
                         return invokeDynamic(method, self, context, methodName, (IRubyObject) args[length - 2], (Block) args[length - 1]);
                     }
                 case 6:
                     return invokeDynamic(method, self, context, methodName, (IRubyObject) args[length - 3], (IRubyObject) args[length - 2], (Block) args[length - 1]);
                 case 7:
                     return invokeDynamic(method, self, context, methodName, (IRubyObject) args[length - 4], (IRubyObject) args[length - 3], (IRubyObject) args[length - 2], (Block) args[length - 1]);
                 }
             }
         } else {
             switch (length) {
             case 3:
                 return invokeDynamic(method, self, context, methodName);
             case 4:
                 if (args[length - 1] instanceof IRubyObject[]) {
                     return invokeDynamic(method, self, context, methodName, (IRubyObject[]) args[length - 1]);
                 } else {
                     return invokeDynamic(method, self, context, methodName, (IRubyObject) args[length - 1]);
                 }
             case 5:
                 return invokeDynamic(method, self, context, methodName, (IRubyObject) args[length - 2], (IRubyObject) args[length - 1]);
             case 6:
                 return invokeDynamic(method, self, context, methodName, (IRubyObject) args[length - 3], (IRubyObject) args[length - 2], (IRubyObject) args[length - 1]);
             }
         }
 
         throw new RuntimeException("Unsupported method signature for dynamic call: " + site.type());
     }
     
     public static void installBytecode(MethodVisitor method, String classname) {
         SkinnyMethodAdapter clinitMethod = new SkinnyMethodAdapter(method);
         clinitMethod.ldc(c(classname));
         clinitMethod.invokestatic(p(Class.class), "forName", sig(Class.class, params(String.class)));
         clinitMethod.ldc(Type.getType(InvokeDynamicSupport.class));
         clinitMethod.ldc("bootstrap");
         clinitMethod.getstatic(p(Linkage.class), "BOOTSTRAP_METHOD_TYPE", ci(MethodType.class));
         clinitMethod.invokestatic(p(MethodHandles.class), "findStatic", sig(MethodHandle.class, Class.class, String.class, MethodType.class));
         clinitMethod.invokestatic(p(Linkage.class), "registerBootstrapMethod", sig(void.class, Class.class, MethodHandle.class));
     }
 
     private static boolean shouldCallMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return method.isUndefined() || notVisibleAndNotMethodMissing(method, name, caller, callType);
     }
 
     private static boolean notVisibleAndNotMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return !method.isCallableFrom(caller, callType) && !name.equals("method_missing");
     }
 
     public static IRubyObject invokeDynamic(DynamicMethod method, Object selfObj, ThreadContext context, String name) {
         IRubyObject self = (IRubyObject) selfObj;
         return method.call(context, self, self.getMetaClass(), name);
     }
 
     public static IRubyObject invokeDynamic(DynamicMethod method, Object selfObj, ThreadContext context, String name, Block block) {
         IRubyObject self = (IRubyObject) selfObj;
         RubyClass selfType = pollAndGetClass(context, self);
         try {
             return method.call(context, self, selfType, name, block);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         }
     }
 
     public static IRubyObject invokeDynamicIter(DynamicMethod method, Object selfObj, ThreadContext context, String name, Block block) {
         IRubyObject self = (IRubyObject) selfObj;
         RubyClass selfType = pollAndGetClass(context, self);
         try {
             return method.call(context, self, selfType, name, block);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         } finally {
             block.escape();
         }
     }
 
     public static IRubyObject invokeDynamic(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject arg) {
         IRubyObject self = (IRubyObject) selfObj;
         return method.call(context, self, self.getMetaClass(), name, arg);
     }
 
     public static IRubyObject invokeDynamic(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject arg, Block block) {
         IRubyObject self = (IRubyObject) selfObj;
         RubyClass selfType = pollAndGetClass(context, self);
         try {
             return method.call(context, self, selfType, name, arg, block);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         }
     }
 
     public static IRubyObject invokeDynamicIter(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject arg, Block block) {
         IRubyObject self = (IRubyObject) selfObj;
         RubyClass selfType = pollAndGetClass(context, self);
         try {
             return method.call(context, self, selfType, name, arg, block);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         } finally {
             block.escape();
         }
     }
 
     public static IRubyObject invokeDynamic(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject arg0, IRubyObject arg1) {
         IRubyObject self = (IRubyObject) selfObj;
         return method.call(context, self, self.getMetaClass(), name, arg0, arg1);
     }
 
     public static IRubyObject invokeDynamic(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         IRubyObject self = (IRubyObject) selfObj;
         RubyClass selfType = pollAndGetClass(context, self);
         try {
             return method.call(context, self, selfType, name, arg0, arg1, block);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         }
     }
 
     public static IRubyObject invokeDynamicIter(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         IRubyObject self = (IRubyObject) selfObj;
         RubyClass selfType = pollAndGetClass(context, self);
         try {
             return method.call(context, self, selfType, name, arg0, arg1, block);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         } finally {
             block.escape();
         }
     }
 
     public static IRubyObject invokeDynamic(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         IRubyObject self = (IRubyObject) selfObj;
         return method.call(context, self, self.getMetaClass(), name, arg0, arg1, arg2);
     }
 
     public static IRubyObject invokeDynamic(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         IRubyObject self = (IRubyObject) selfObj;
         RubyClass selfType = pollAndGetClass(context, self);
         try {
             return method.call(context, self, selfType, name, arg0, arg1, arg2, block);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         }
     }
 
     public static IRubyObject invokeDynamicIter(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         IRubyObject self = (IRubyObject) selfObj;
         RubyClass selfType = pollAndGetClass(context, self);
         try {
             return method.call(context, self, selfType, name, arg0, arg1, arg2, block);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         } finally {
             block.escape();
         }
     }
 
     public static IRubyObject invokeDynamic(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject[] args) {
         IRubyObject self = (IRubyObject) selfObj;
         return method.call(context, self, self.getMetaClass(), name, args);
     }
 
     public static IRubyObject invokeDynamic(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject[] args, Block block) {
         IRubyObject self = (IRubyObject) selfObj;
         RubyClass selfType = pollAndGetClass(context, self);
         try {
             return method.call(context, self, selfType, name, args, block);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         }
     }
 
     public static IRubyObject invokeDynamicIter(DynamicMethod method, Object selfObj, ThreadContext context, String name, IRubyObject[] args, Block block) {
         IRubyObject self = (IRubyObject) selfObj;
         RubyClass selfType = pollAndGetClass(context, self);
         try {
             return method.call(context, self, selfType, name, args, block);
         } catch (JumpException.BreakJump bj) {
             return handleBreakJump(context, bj);
         } catch (JumpException.RetryJump rj) {
             throw retryJumpError(context);
         } finally {
             block.escape();
         }
     }
 
     private static RubyClass pollAndGetClass(ThreadContext context, IRubyObject self) {
         context.callThreadPoll();
 
         RubyClass selfType = self.getMetaClass();
 
         return selfType;
     }
 
     private static IRubyObject handleBreakJump(ThreadContext context, JumpException.BreakJump bj) throws JumpException.BreakJump {
         // consume and rethrow or just keep rethrowing?
         if (context.getFrameJumpTarget() == bj.getTarget()) {
             return (IRubyObject) bj.getValue();
         }
         throw bj;
     }
 
     private static RaiseException retryJumpError(ThreadContext context) {
-        return context.getRuntime().newLocalJumpError("retry", context.getRuntime().getNil(), "retry outside of rescue not supported");
+        return context.getRuntime().newLocalJumpError(RubyLocalJumpError.Reason.RETRY, context.getRuntime().getNil(), "retry outside of rescue not supported");
     }
 }
