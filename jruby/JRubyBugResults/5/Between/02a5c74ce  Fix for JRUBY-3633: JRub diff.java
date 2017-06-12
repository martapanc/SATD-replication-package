diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 3622054f19..3e28c7febb 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1457 +1,1489 @@
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
 import java.lang.ref.Reference;
 import java.lang.ref.ReferenceQueue;
 import java.lang.ref.WeakReference;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
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
 import java.util.concurrent.ThreadPoolExecutor;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.atomic.AtomicInteger;
 
 import org.jcodings.Encoding;
 import org.joda.time.DateTimeZone;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.common.RubyWarnings;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.JITCompiler;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.ext.JRubyPOSIXHandler;
 import org.jruby.ext.LateLoadingLibrary;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.ext.posix.POSIXFactory;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.management.ClassCache;
 import org.jruby.management.Config;
 import org.jruby.management.ParserStats;
 import org.jruby.parser.EvalStaticScope;
 import org.jruby.parser.Parser;
 import org.jruby.parser.ParserConfiguration;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
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
 import org.jruby.runtime.encoding.EncodingService;
 import org.jruby.runtime.load.CompiledScriptLoader;
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
 
 import com.kenai.constantine.Constant;
 import com.kenai.constantine.ConstantSet;
 import com.kenai.constantine.platform.Errno;
 import java.util.EnumSet;
 import org.jruby.ast.RootNode;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.management.BeanManager;
 import org.jruby.management.BeanManagerFactory;
 import org.jruby.threading.DaemonThreadFactory;
 
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
 
     private static Ruby globalRuntime;
 
     public static synchronized Ruby getGlobalRuntime() {
         if (globalRuntime == null) {
             initGlobalRuntime();
         }
         return globalRuntime;
     }
 
     private static void initGlobalRuntime() {
         globalRuntime = newInstance();
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
         this.is1_9               = config.getCompatVersion() == CompatVersion.RUBY1_9;
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
         this.beanManager        = BeanManagerFactory.create(this, config.isManagementEnabled());
         this.jitCompiler        = new JITCompiler(this);
         this.parserStats        = new ParserStats(this);
         
         this.beanManager.register(new Config(this));
         this.beanManager.register(parserStats);
         this.beanManager.register(new ClassCache(this));
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
             throw newLocalJumpError(RubyLocalJumpError.Reason.RETURN, (IRubyObject)rj.getValue(), "unexpected return");
         } catch (JumpException.BreakJump bj) {
             throw newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
             throw newLocalJumpError(RubyLocalJumpError.Reason.REDO, (IRubyObject)rj.getValue(), "unexpected redo");
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
             context.setFileAndLine(node.getPosition());
             return runNormally(node);
         } finally {
             context.setFileAndLine(oldFile, oldLine);
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
 
         if (filename.endsWith(".class")) {
             // we are presumably running a precompiled class; load directly
             Script script = CompiledScriptLoader.loadScriptFromFile(this, inputStream, filename);
             if (script == null) {
                 throw new MainExitException(1, "error: .class file specified is not a compiled JRuby script");
             }
             script.setFilename(filename);
             runScript(script);
             return;
         }
         
         Node scriptNode = parseFromMain(inputStream, filename);
         ThreadContext context = getCurrentContext();
 
         String oldFile = context.getFile();
         int oldLine = context.getLine();
         try {
             context.setFileAndLine(scriptNode.getPosition());
 
             if (config.isAssumePrinting() || config.isAssumeLoop()) {
                 runWithGetsLoop(scriptNode, config.isAssumePrinting(), config.isProcessLineEnds(),
                         config.isSplit());
             } else {
                 runNormally(scriptNode);
             }
         } finally {
             context.setFileAndLine(oldFile, oldLine);
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
+
+    /**
+     * Run the given script with a "while gets; end" loop wrapped around it.
+     * This is primarily used for the -n command-line flag, to allow writing
+     * a short script that processes input lines using the specified code.
+     *
+     * @param scriptNode The root node of the script to execute
+     * @param printing Whether $_ should be printed after each loop (as in the
+     * -p command-line flag)
+     * @param processLineEnds Whether line endings should be processed by
+     * setting $\ to $/ and <code>chop!</code>ing every line read
+     * @param split Whether to split each line read using <code>String#split</code>
+     * bytecode before executing.
+     * @return The result of executing the specified script
+     */
+    @Deprecated
+    public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split, boolean unused) {
+        return runWithGetsLoop(scriptNode, printing, processLineEnds, split);
+    }
     
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
      * bytecode before executing.
      * @return The result of executing the specified script
      */
     public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split) {
         ThreadContext context = getCurrentContext();
         
         Script script = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         if (compile) {
             script = tryCompile(scriptNode);
             if (compile && script == null) {
                 // terminate; tryCompile will have printed out an error and we're done
                 return getNil();
             }
         }
         
         if (processLineEnds) {
             getGlobalVariables().set("$\\", getGlobalVariables().get("$/"));
         }
 
         // we do preand post load outside the "body" versions to pre-prepare
         // and pre-push the dynamic scope we need for lastline
         RuntimeHelpers.preLoad(context, ((RootNode)scriptNode).getStaticScope().getVariables());
 
         try {
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
                             runScriptBody(script);
                         } else {
                             runInterpreterBody(scriptNode);
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
         } finally {
             RuntimeHelpers.postLoad(context);
         }
         
         return getNil();
     }
+
+    /**
+     * Run the specified script without any of the loop-processing wrapper
+     * code.
+     *
+     * @param scriptNode The root node of the script to be executed
+     * bytecode before execution
+     * @return The result of executing the script
+     */
+    @Deprecated
+    public IRubyObject runNormally(Node scriptNode, boolean unused) {
+        return runNormally(scriptNode);
+    }
     
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      * 
      * @param scriptNode The root node of the script to be executed
      * bytecode before execution
      * @return The result of executing the script
      */
     public IRubyObject runNormally(Node scriptNode) {
         Script script = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         boolean forceCompile = getInstanceConfig().getCompileMode().shouldPrecompileAll();
         if (compile) {
             script = tryCompile(scriptNode);
             if (forceCompile && script == null) {
                 return getNil();
             }
         }
         
         if (script != null) {
             if (config.isShowBytecode()) {
                 return nilObject;
             } else {
                 return runScript(script);
             }
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
             ASTCompiler compiler = config.newCompiler();
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
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + nce.getMessage());
                 nce.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (ClassNotFoundException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (InstantiationException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (IllegalAccessException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (Throwable t) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("could not compile: " + node.getPosition().getFile() + " because of: \"" + t.getMessage() + "\"");
                 t.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         }
         
         return script;
     }
     
     private IRubyObject runScript(Script script) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(context, context.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     private IRubyObject runScriptBody(Script script) {
         ThreadContext context = getCurrentContext();
 
         try {
             return script.__file__(context, context.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     public IRubyObject runInterpreter(Node scriptNode) {
         ThreadContext context = getCurrentContext();
         
         assert scriptNode != null : "scriptNode is not null";
         
         try {
             return scriptNode.interpret(this, context, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runInterpreterBody(Node scriptNode) {
         ThreadContext context = getCurrentContext();
 
         assert scriptNode != null : "scriptNode is not null";
         assert scriptNode instanceof RootNode;
 
         try {
             return ((RootNode)scriptNode).interpret(this, context, getTopSelf(), Block.NULL_BLOCK);
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
             executor = new ThreadPoolExecutor(
                     RubyInstanceConfig.POOL_MIN,
                     RubyInstanceConfig.POOL_MAX,
                     RubyInstanceConfig.POOL_TTL,
                     TimeUnit.SECONDS,
                     new SynchronousQueue<Runnable>(),
                     new DaemonThreadFactory());
         }
         
         // initialize the root of the class hierarchy completely
         initRoot();
 
         // Construct the top-level execution frame and scope for the main thread
         tc.prepareTopLevel(objectClass, topSelf);
 
         // Initialize all the core classes
         bootstrap();
         
         // Initialize the "dummy" class used as a marker
         dummyClass = new RubyClass(this, classClass);
         dummyClass.freeze(tc);
         
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
 
     private void initRoot() {
         boolean oneNine = is1_9();
         // Bootstrap the top of the hierarchy
         if (oneNine) {
             basicObjectClass = RubyClass.createBootstrapClass(this, "BasicObject", null, RubyBasicObject.OBJECT_ALLOCATOR);
             objectClass = RubyClass.createBootstrapClass(this, "Object", basicObjectClass, RubyObject.OBJECT_ALLOCATOR);
         } else {
             objectClass = RubyClass.createBootstrapClass(this, "Object", null, RubyObject.OBJECT_ALLOCATOR);
         }
         moduleClass = RubyClass.createBootstrapClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR);
         classClass = RubyClass.createBootstrapClass(this, "Class", moduleClass, RubyClass.CLASS_ALLOCATOR);
 
         if (oneNine) basicObjectClass.setMetaClass(classClass);
         objectClass.setMetaClass(classClass);
         moduleClass.setMetaClass(classClass);
         classClass.setMetaClass(classClass);
 
         RubyClass metaClass;
         if (oneNine) metaClass = basicObjectClass.makeMetaClass(classClass);
         metaClass = objectClass.makeMetaClass(classClass);
         metaClass = moduleClass.makeMetaClass(metaClass);
         metaClass = classClass.makeMetaClass(metaClass);
 
         if (oneNine) RubyBasicObject.createBasicObjectClass(this, basicObjectClass);
         RubyObject.createObjectClass(this, objectClass);
         RubyModule.createModuleClass(this, moduleClass);
         RubyClass.createClassClass(this, classClass);
         
         // set constants now that they're initialized
         if (oneNine) objectClass.setConstant("BasicObject", basicObjectClass);
         objectClass.setConstant("Object", objectClass);
         objectClass.setConstant("Class", classClass);
         objectClass.setConstant("Module", moduleClass);
 
         // Initialize Kernel and include into Object
         RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         // Object is ready, create top self
         topSelf = TopSelfFactory.createTopSelf(this);
     }
 
     private void initCore() {
         // Pre-create all the core classes potentially referenced during startup
         RubyNil.createNilClass(this);
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
 
         nilObject = new RubyNil(this);
         for (int i=0; i<NIL_PREFILLED_ARRAY_SIZE; i++) nilPrefilledArray[i] = nilObject;
         singleNilArray = new IRubyObject[] {nilObject};
 
         falseObject = new RubyBoolean(this, false);
         trueObject = new RubyBoolean(this, true);
 
         if (profile.allowClass("Data")) {
             defineClass("Data", objectClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         }
 
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         RubyString.createStringClass(this);
 
         if (is1_9()) {
             RubyEncoding.createEncodingClass(this);
             RubyConverter.createConverterClass(this);
             encodingService = new EncodingService(this);
         }
 
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
 
         if (!is1_9()) {
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
 
         if (is1_9()) {
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
 
     public static final int NIL_PREFILLED_ARRAY_SIZE = RubyArray.ARRAY_DEFAULT_SIZE * 8;
     private final IRubyObject nilPrefilledArray[] = new IRubyObject[NIL_PREFILLED_ARRAY_SIZE];
     public IRubyObject[] getNilPrefilledArray() {
         return nilPrefilledArray;
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
         if (is1_9()) stopIteration = defineClassIfAllowed("StopIteration", indexError);
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
 
         if (is1_9()) {
             if (profile.allowClass("EncodingError")) {
                 encodingError = defineClass("EncodingError", standardError, standardError.getAllocator()); 
                 encodingCompatibilityError = defineClassUnder("CompatibilityError", encodingError, encodingError.getAllocator(), encodingClass);
             }
         }
 
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
             for (Errno e : Errno.values()) {
                 Constant c = (Constant) e;
                 if (Character.isUpperCase(c.name().charAt(0))) {
                     createSysErr(c.value(), c.name());
                 }
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
         addLazyBuiltin("timeout.rb", "timeout", "org.jruby.ext.Timeout");
         addLazyBuiltin("socket.so", "socket", "org.jruby.ext.socket.RubySocket$Service");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.libraries.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.libraries.JRubySerializationLibrary");
         addLazyBuiltin("ffi-internal.so", "ffi-internal", "org.jruby.ext.ffi.Factory$Service");
         addLazyBuiltin("tempfile.rb", "tempfile", "org.jruby.libraries.TempfileLibrary");
         if(RubyInstanceConfig.NATIVE_NET_PROTOCOL) {
             addLazyBuiltin("net/protocol.rb", "net/protocol", "org.jruby.libraries.NetProtocolBufferedIOLibrary");
         }
         
         if (is1_9()) {
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
 
         if (is1_9()) {
             getLoadService().require("builtin/prelude.rb");
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
 
     public Object getRespondToMethod() {
         return respondToMethod;
     }
 
     public void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
     }
 
     public Object getObjectToYamlMethod() {
         return objectToYamlMethod;
     }
 
     void setObjectToYamlMethod(Object otym) {
         this.objectToYamlMethod = otym;
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
 
     public RubyClass getBasicObject() {
         return basicObjectClass;
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
 
     public DynamicMethod getPrivateMethodMissing() {
         return privateMethodMissing;
     }
     public void setPrivateMethodMissing(DynamicMethod method) {
         privateMethodMissing = method;
     }
     public DynamicMethod getProtectedMethodMissing() {
         return protectedMethodMissing;
     }
     public void setProtectedMethodMissing(DynamicMethod method) {
         protectedMethodMissing = method;
     }
     public DynamicMethod getVariableMethodMissing() {
         return variableMethodMissing;
     }
     public void setVariableMethodMissing(DynamicMethod method) {
         variableMethodMissing = method;
     }
     public DynamicMethod getSuperMethodMissing() {
         return superMethodMissing;
     }
     public void setSuperMethodMissing(DynamicMethod method) {
         superMethodMissing = method;
     }
     public DynamicMethod getNormalMethodMissing() {
         return normalMethodMissing;
     }
     public void setNormalMethodMissing(DynamicMethod method) {
         normalMethodMissing = method;
     }
     public DynamicMethod getDefaultMethodMissing() {
         return defaultMethodMissing;
     }
     public void setDefaultMethodMissing(DynamicMethod method) {
         defaultMethodMissing = method;
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
