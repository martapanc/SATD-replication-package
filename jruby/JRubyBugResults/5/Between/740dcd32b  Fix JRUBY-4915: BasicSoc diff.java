diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 20d807cffa..6b7d0da9a5 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1250 +1,1251 @@
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
 
 import org.jruby.util.func.Function1;
 import java.io.ByteArrayInputStream;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
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
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.util.Arrays;
 import java.util.EnumSet;
 import java.util.concurrent.atomic.AtomicLong;
 import org.jruby.ast.RootNode;
 import org.jruby.exceptions.Unrescuable;
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
         setGlobalRuntimeFirstTimeOnly(ruby);
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
 
     /**
      * Tests whether globalRuntime has been instantiated or not.
      *
      * This method is used by singleton model of org.jruby.embed.ScriptingContainer
      * to decide what RubyInstanceConfig should be used. When a global runtime is
      * not there, RubyInstanceConfig of AbstractContextProvider will be used to enact
      * configurations set by a user. When a global runtime is already instantiated,
      * RubyInstanceConfig of the global runtime should be used in ScriptingContaiener.
      *
      * @return true if a global runtime is instantiated, false for other.
      * 
      */
     public static boolean isGlobalRuntimeReady() {
         return globalRuntime != null;
     }
 
     private static synchronized void setGlobalRuntimeFirstTimeOnly(Ruby runtime) {
         if (globalRuntime == null) {
             globalRuntime = runtime;
         }
     }
 
     public static synchronized Ruby getGlobalRuntime() {
         if (globalRuntime == null) {
             newInstance();
         }
         return globalRuntime;
     }
     
     /**
      * Convenience method for java integrators who may need to switch the notion 
      * of "global" runtime. Use <tt>JRuby.runtime.use_as_global_runtime</tt>
      * from Ruby code to activate the current runtime as the global one.
      */
     public void useAsGlobalRuntime() {
         synchronized(Ruby.class) {
             globalRuntime = null;
             setGlobalRuntimeFirstTimeOnly(this);
         }
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
-        this.is1_9               = config.getCompatVersion() == CompatVersion.RUBY1_9;
+        this.is1_9              = config.getCompatVersion() == CompatVersion.RUBY1_9;
+        this.doNotReverseLookupEnabled = is1_9;
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
 
         return evalScriptlet(script, newScope);
     }
 
     /**
      * Evaluates a script under the current scope (perhaps the top-level
      * scope) and returns the result (generally the last value calculated).
      * This version goes straight into the interpreter, bypassing compilation
      * and runtime preparation typical to normal script runs.
      *
      * This version accepts a scope to use, so you can eval many times against
      * the same scope.
      *
      * @param script The scriptlet to run
      * @param scope The scope to execute against (ManyVarsDynamicScope is
      * recommended, so it can grow as needed)
      * @returns The result of the eval
      */
     public IRubyObject evalScriptlet(String script, DynamicScope scope) {
         ThreadContext context = getCurrentContext();
         Node node = parseEval(script, "<script>", scope, 0);
 
         try {
             context.preEvalScriptlet(scope);
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
         byte[] bytes = script.getBytes();
 
         Node node = parseInline(new ByteArrayInputStream(bytes), filename, null);
         ThreadContext context = getCurrentContext();
         
         String oldFile = context.getFile();
         int oldLine = context.getLine();
         try {
             context.setFileAndLine(node.getPosition());
             return runInterpreter(node);
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
 
         // done with the stream, shut it down
         try {inputStream.close();} catch (IOException ioe) {}
 
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
     @Deprecated
     public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split, boolean unused) {
         return runWithGetsLoop(scriptNode, printing, processLineEnds, split);
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
 
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      *
      * @param scriptNode The root node of the script to be executed
      * bytecode before execution
      * @return The result of executing the script
      */
     @Deprecated
     public IRubyObject runNormally(Node scriptNode, boolean unused) {
         return runNormally(scriptNode);
     }
     
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
             script = tryCompile(scriptNode, null, new JRubyClassLoader(getJRubyClassLoader()), config.isShowBytecode());
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
 
     /**
      * Try to compile the code associated with the given Node, returning an
      * instance of the successfully-compiled Script or null if the script could
      * not be compiled.
      *
      * @param node The node to attempt to compiled
      * @return an instance of the successfully-compiled Script, or null.
      */
     public Script tryCompile(Node node) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()), false);
     }
 
     /**
      * Try to compile the code associated with the given Node, returning an
      * instance of the successfully-compiled Script or null if the script could
      * not be compiled. This version accepts an ASTInspector instance assumed to
      * have appropriate flags set for compile optimizations, such as to turn
      * on heap-based local variables to share an existing scope.
      *
      * @param node The node to attempt to compiled
      * @param inspector The ASTInspector to use for making optimization decisions
      * @return an instance of the successfully-compiled Script, or null.
      */
     public Script tryCompile(Node node, ASTInspector inspector) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()), inspector, false);
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, boolean dump) {
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(node);
 
         return tryCompile(node, cachedClassName, classLoader, inspector, dump);
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, ASTInspector inspector, boolean dump) {
         Script script = null;
         try {
             String filename = node.getPosition().getFile();
             String classname = JavaNameMangler.mangledFilenameForStartupClasspath(filename);
 
             StandardASMCompiler asmCompiler = null;
             if (RubyInstanceConfig.JIT_CODE_CACHE != null && cachedClassName != null) {
                 asmCompiler = new StandardASMCompiler(cachedClassName.replace('.', '/'), filename);
             } else {
                 asmCompiler = new StandardASMCompiler(classname, filename);
             }
             ASTCompiler compiler = config.newCompiler();
             if (dump) {
                 compiler.compileRoot(node, asmCompiler, inspector, false, false);
                 asmCompiler.dumpClass(System.out);
             } else {
                 compiler.compileRoot(node, asmCompiler, inspector, true, false);
             }
 
             if (RubyInstanceConfig.JIT_CODE_CACHE != null && cachedClassName != null) {
                 // save script off to disk
                 String pathName = cachedClassName.replace('.', '/');
                 JITCompiler.saveToCodeCache(this, asmCompiler.getClassByteArray(), "ruby/jit", new File(RubyInstanceConfig.JIT_CODE_CACHE, pathName + ".class"));
             }
 
             script = (Script)asmCompiler.loadClass(classLoader).newInstance();
 
             if (config.isJitLogging()) {
                 System.err.println("compiled: " + node.getPosition().getFile());
             }
         } catch (NotCompilableException nce) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("warning: not compileable: " + nce.getMessage());
                 nce.printStackTrace();
             } else {
                 System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (ClassNotFoundException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("warning: not compileable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (InstantiationException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("warning: not compilable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (IllegalAccessException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("warning: not compilable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (Throwable t) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("warning: could not compile: " + node.getPosition().getFile() + " because of: \"" + t.getMessage() + "\"");
                 t.printStackTrace();
             } else {
                 System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         }
         
         return script;
     }
     
     public IRubyObject runScript(Script script) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(context, getTopSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runScriptBody(Script script) {
         ThreadContext context = getCurrentContext();
 
         try {
             return script.__file__(context, getTopSelf(), Block.NULL_BLOCK);
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
     public void addModule(RubyModule module) {
         synchronized (allModules) {
             allModules.add(module);
         }
     }
     public void eachModule(Function1<Object, IRubyObject> func) {
         synchronized (allModules) {
             for (RubyModule module : allModules) {
                 func.apply(module);
             }
         }
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
 
         // Set up the main thread in thread service
         threadService.initMainThread();
 
         // Get the main threadcontext (gets constructed for us)
         ThreadContext tc = getCurrentContext();
 
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
             loadService.smartLoad(scriptName);
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
             basicObjectClass = RubyClass.createBootstrapClass(this, "BasicObject", null, RubyBasicObject.BASICOBJECT_ALLOCATOR);
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
         
         // Pre-create all the core classes potentially referenced during startup
         RubyNil.createNilClass(this);
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
 
         nilObject = new RubyNil(this);
         for (int i=0; i<NIL_PREFILLED_ARRAY_SIZE; i++) nilPrefilledArray[i] = nilObject;
         singleNilArray = new IRubyObject[] {nilObject};
 
         falseObject = new RubyBoolean(this, false);
         trueObject = new RubyBoolean(this, true);
     }
 
     private void initCore() {
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
 
             RubyRandom.createRandomClass(this);
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
diff --git a/src/org/jruby/ext/socket/RubyBasicSocket.java b/src/org/jruby/ext/socket/RubyBasicSocket.java
index c9ea0847df..b09cdf0f70 100644
--- a/src/org/jruby/ext/socket/RubyBasicSocket.java
+++ b/src/org/jruby/ext/socket/RubyBasicSocket.java
@@ -1,696 +1,697 @@
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
 package org.jruby.ext.socket;
 
 import static com.kenai.constantine.platform.IPProto.IPPROTO_TCP;
 import static com.kenai.constantine.platform.Sock.SOCK_DGRAM;
 import static com.kenai.constantine.platform.Sock.SOCK_STREAM;
 import static com.kenai.constantine.platform.TCP.TCP_NODELAY;
 
 import java.io.EOFException;
 import java.io.IOException;
 import java.net.DatagramSocket;
 import java.net.InetSocketAddress;
 import java.net.ServerSocket;
 import java.net.Socket;
 import java.net.SocketAddress;
 import java.nio.channels.Channel;
 import java.nio.channels.DatagramChannel;
 import java.nio.channels.ServerSocketChannel;
 import java.nio.channels.SocketChannel;
 
 import org.jruby.CompatVersion;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyIO;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.Pack;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.io.OpenFile;
 
 import com.kenai.constantine.platform.SocketLevel;
 import com.kenai.constantine.platform.SocketOption;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyClass(name="BasicSocket", parent="IO")
 public class RubyBasicSocket extends RubyIO {
     private static final ByteList FORMAT_SMALL_I = new ByteList(ByteList.plain("i"));
 
     private static ObjectAllocator BASICSOCKET_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyBasicSocket(runtime, klass);
         }
     };
 
     static void createBasicSocket(Ruby runtime) {
         RubyClass rb_cBasicSocket = runtime.defineClass("BasicSocket", runtime.getIO(), BASICSOCKET_ALLOCATOR);
 
         rb_cBasicSocket.defineAnnotatedMethods(RubyBasicSocket.class);
     }
 
     // By default we always reverse lookup unless do_not_reverse_lookup set.
     private boolean doNotReverseLookup = false;
 
     public RubyBasicSocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
+        doNotReverseLookup = runtime.is1_9();
     }
     
     protected void initSocket(Ruby runtime, ChannelDescriptor descriptor) {
         // continue with normal initialization
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.fdopen(runtime, descriptor, new ModeFlags(ModeFlags.RDONLY)));
             openFile.setPipeStream(ChannelStream.fdopen(runtime, descriptor, new ModeFlags(ModeFlags.WRONLY)));
             openFile.getPipeStream().setSync(true);
         } catch (org.jruby.util.io.InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         }
         openFile.setMode(OpenFile.READWRITE | OpenFile.SYNC);
     }
 
     @Override
     public IRubyObject close_write(ThreadContext context) {
         if (context.getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw context.getRuntime().newSecurityError("Insecure: can't close");
         }
 
         if (!openFile.isWritable()) {
             return context.getRuntime().getNil();
         }
 
         if (openFile.getPipeStream() == null && openFile.isReadable()) {
             throw context.getRuntime().newIOError("closing non-duplex IO for writing");
         }
 
         if (!openFile.isReadable()) {
             close();
         } else {
             // shutdown write
             shutdownInternal(context, 1);
         }
         return context.getRuntime().getNil();
     }
 
     @Override
     public IRubyObject close_read(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (runtime.getSafeLevel() >= 4 && isTaint()) {
             throw runtime.newSecurityError("Insecure: can't close");
         }
 
         if (!openFile.isOpen()) {
             throw context.getRuntime().newIOError("not opened for reading");
         }
 
         if (!openFile.isWritable()) {
             close();
         } else {
             // shutdown read
             shutdownInternal(context, 0);
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "send", rest = true)
     public IRubyObject write_send(ThreadContext context, IRubyObject[] args) {
         return syswrite(context, args[0]);
     }
 
     @Deprecated
     public IRubyObject recv(IRubyObject[] args) {
         return recv(getRuntime().getCurrentContext(), args);
     }
     @JRubyMethod(rest = true)
     public IRubyObject recv(ThreadContext context, IRubyObject[] args) {
         OpenFile openFile = getOpenFileChecked();
         try {
             context.getThread().beforeBlockingCall();
             return RubyString.newString(context.getRuntime(), openFile.getMainStream().read(RubyNumeric.fix2int(args[0])));
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             // recv returns nil on EOF
             return context.getRuntime().getNil();
         } catch (IOException e) {
             // All errors to sysread should be SystemCallErrors, but on a closed stream
             // Ruby returns an IOError.  Java throws same exception for all errors so
             // we resort to this hack...
             if ("Socket not open".equals(e.getMessage())) {
 	            throw context.getRuntime().newIOError(e.getMessage());
             }
             throw context.getRuntime().newSystemCallError(e.getMessage());
         } finally {
             context.getThread().afterBlockingCall();
         }
     }
 
     protected InetSocketAddress getLocalSocket() {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if (socketChannel instanceof SocketChannel) {
             return (InetSocketAddress)((SocketChannel)socketChannel).socket().getLocalSocketAddress();
         } else if (socketChannel instanceof ServerSocketChannel) {
             return (InetSocketAddress)((ServerSocketChannel) socketChannel).socket().getLocalSocketAddress();
         } else if (socketChannel instanceof DatagramChannel) {
             return (InetSocketAddress)((DatagramChannel) socketChannel).socket().getLocalSocketAddress();
         } else {
             return null;
         }
     }
 
     protected InetSocketAddress getRemoteSocket() {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             return (InetSocketAddress)((SocketChannel)socketChannel).socket().getRemoteSocketAddress();
         } else {
             return null;
         }
     }
 
     private Socket asSocket() {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(!(socketChannel instanceof SocketChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((SocketChannel)socketChannel).socket();
     }
 
     private ServerSocket asServerSocket() {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(!(socketChannel instanceof ServerSocketChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((ServerSocketChannel)socketChannel).socket();
     }
 
     private DatagramSocket asDatagramSocket() {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(!(socketChannel instanceof DatagramChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((DatagramChannel)socketChannel).socket();
     }
 
     private IRubyObject getBroadcast(Ruby runtime) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return trueFalse(runtime, (socketChannel instanceof DatagramChannel) ? asDatagramSocket().getBroadcast() : false);
     }
 
     private void setBroadcast(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setBroadcast(asBoolean(val));
         }
     }
 
     private void setKeepAlive(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setKeepAlive(asBoolean(val));
         }
     }
 
     private void setTcpNoDelay(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setTcpNoDelay(asBoolean(val));
         }
     }
 
     private void setReuseAddr(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if (socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setReuseAddress(asBoolean(val));
         } else if (socketChannel instanceof SocketChannel) {
             asSocket().setReuseAddress(asBoolean(val));
         } else if (socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setReuseAddress(asBoolean(val));
         }
     }
 
     private void setRcvBuf(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setReceiveBufferSize(asNumber(val));
         } else if(socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setReceiveBufferSize(asNumber(val));
         } else if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setReceiveBufferSize(asNumber(val));
         }
     }
 
     private void setTimeout(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setSoTimeout(asNumber(val));
         } else if(socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setSoTimeout(asNumber(val));
         } else if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setSoTimeout(asNumber(val));
         }
     }
 
     private void setSndBuf(IRubyObject val) throws IOException {
         try {
             Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
             if(socketChannel instanceof SocketChannel) {
                 asSocket().setSendBufferSize(asNumber(val));
             } else if(socketChannel instanceof DatagramChannel) {
                 asDatagramSocket().setSendBufferSize(asNumber(val));
             }
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newErrnoEINVALError(iae.getMessage());
         }
     }
 
     private void setLinger(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             if(val instanceof RubyBoolean && !val.isTrue()) {
                 asSocket().setSoLinger(false, 0);
             } else {
                 int num = asNumber(val);
                 if(num == -1) {
                     asSocket().setSoLinger(false, 0);
                 } else {
                     asSocket().setSoLinger(true, num);
                 }
             }
         }
     }
 
     private void setOOBInline(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setOOBInline(asBoolean(val));
         }
     }
 
     private int asNumber(IRubyObject val) {
         if (val instanceof RubyNumeric) {
             return RubyNumeric.fix2int(val);
         } else if (val instanceof RubyBoolean) {
             return val.isTrue() ? 1 : 0;
         }
         else {
             return stringAsNumber(val);
         }
     }
 
     private int stringAsNumber(IRubyObject val) {
         ByteList str = val.convertToString().getByteList();
         IRubyObject res = Pack.unpack(getRuntime(), str, FORMAT_SMALL_I).entry(0);
         
         if (res.isNil()) {
             throw getRuntime().newErrnoEINVALError();
         }
 
         return RubyNumeric.fix2int(res);
     }
 
     protected boolean asBoolean(IRubyObject val) {
         if (val instanceof RubyString) {
             return stringAsNumber(val) != 0;
         } else if(val instanceof RubyNumeric) {
             return RubyNumeric.fix2int(val) != 0;
         } else {
             return val.isTrue();
         }
     }
 
     private IRubyObject getKeepAlive(Ruby runtime) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return trueFalse(runtime,
                          (socketChannel instanceof SocketChannel) ? asSocket().getKeepAlive() : false
                          );
     }
 
     private IRubyObject getLinger(Ruby runtime) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
 
         int linger = 0;
         if (socketChannel instanceof SocketChannel) {
             linger = asSocket().getSoLinger();
             if (linger < 0) {
                 linger = 0;
             }
         }
 
         return number(runtime, linger);
     }
 
     private IRubyObject getOOBInline(Ruby runtime) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return trueFalse(runtime,
                          (socketChannel instanceof SocketChannel) ? asSocket().getOOBInline() : false
                          );
     }
 
     private IRubyObject getRcvBuf(Ruby runtime) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return number(runtime,
                       (socketChannel instanceof SocketChannel) ? asSocket().getReceiveBufferSize() : 
                       ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getReceiveBufferSize() : 
                        asDatagramSocket().getReceiveBufferSize())
                       );
     }
 
     private IRubyObject getSndBuf(Ruby runtime) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return number(runtime,
                       (socketChannel instanceof SocketChannel) ? asSocket().getSendBufferSize() : 
                       ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSendBufferSize() : 0)
                       );
     }
 
     private IRubyObject getReuseAddr(Ruby runtime) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
 
         boolean reuse = false;
         if (socketChannel instanceof ServerSocketChannel) {
             reuse = asServerSocket().getReuseAddress();
         } else if (socketChannel instanceof SocketChannel) {
             reuse = asSocket().getReuseAddress();
         } else if (socketChannel instanceof DatagramChannel) {
             reuse = asDatagramSocket().getReuseAddress();
         }
 
         return trueFalse(runtime, reuse);
     }
 
     private IRubyObject getTimeout(Ruby runtime) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return number(runtime,
                       (socketChannel instanceof SocketChannel) ? asSocket().getSoTimeout() : 
                       ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getSoTimeout() : 
                        ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSoTimeout() : 0))
                       );
     }
 
     protected int getSoTypeDefault() {
         return 0;
     }
     private int getChannelSoType(Channel channel) {
         if (channel instanceof SocketChannel || channel instanceof ServerSocketChannel) {
             return SOCK_STREAM.value();
         } else if (channel instanceof DatagramChannel) {
             return SOCK_DGRAM.value();
         } else {
             return getSoTypeDefault();
         }
     }
     private IRubyObject getSoType(Ruby runtime) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return number(runtime, getChannelSoType(socketChannel));
     }
 
     private IRubyObject trueFalse(Ruby runtime, boolean val) {
         return number(runtime, val ? 1 : 0);
     }
 
     private static IRubyObject number(Ruby runtime, int s) {
         RubyArray array = runtime.newArray(runtime.newFixnum(s));
         return Pack.pack(runtime, array, FORMAT_SMALL_I);
     }
 
     @Deprecated
     public IRubyObject getsockopt(IRubyObject lev, IRubyObject optname) {
         return getsockopt(getRuntime().getCurrentContext(), lev, optname);
     }
     @JRubyMethod
     public IRubyObject getsockopt(ThreadContext context, IRubyObject lev, IRubyObject optname) {
         int level = RubyNumeric.fix2int(lev);
         int opt = RubyNumeric.fix2int(optname);
         Ruby runtime = context.getRuntime();
 
         try {
             switch(SocketLevel.valueOf(level)) {
             case SOL_IP:
             case SOL_SOCKET:
             case SOL_TCP:
             case SOL_UDP:
                 switch(SocketOption.valueOf(opt)) {
                 case SO_BROADCAST:
                     return getBroadcast(runtime);
                 case SO_KEEPALIVE:
                     return getKeepAlive(runtime);
                 case SO_LINGER:
                     return getLinger(runtime);
                 case SO_OOBINLINE:
                     return getOOBInline(runtime);
                 case SO_RCVBUF:
                     return getRcvBuf(runtime);
                 case SO_REUSEADDR:
                     return getReuseAddr(runtime);
                 case SO_SNDBUF:
                     return getSndBuf(runtime);
                 case SO_RCVTIMEO:
                 case SO_SNDTIMEO:
                     return getTimeout(runtime);
                 case SO_TYPE:
                     return getSoType(runtime);
 
                     // Can't support the rest with Java
                 case SO_RCVLOWAT:
                     return number(runtime, 1);
                 case SO_SNDLOWAT:
                     return number(runtime, 2048);
                 case SO_DEBUG:
                 case SO_ERROR:
                 case SO_DONTROUTE:
                 case SO_TIMESTAMP:
                     return trueFalse(runtime, false);
                 default:
                     throw context.getRuntime().newErrnoENOPROTOOPTError();
                 }
             default:
                 throw context.getRuntime().newErrnoENOPROTOOPTError();
             }
         } catch(IOException e) {
             throw context.getRuntime().newErrnoENOPROTOOPTError();
         }
     }
     @Deprecated
     public IRubyObject setsockopt(IRubyObject lev, IRubyObject optname, IRubyObject val) {
         return setsockopt(getRuntime().getCurrentContext(), lev, optname, val);
     }
     @JRubyMethod
     public IRubyObject setsockopt(ThreadContext context, IRubyObject lev, IRubyObject optname, IRubyObject val) {
         int level = RubyNumeric.fix2int(lev);
         int opt = RubyNumeric.fix2int(optname);
 
         try {
             switch(SocketLevel.valueOf(level)) {
             case SOL_IP:
             case SOL_SOCKET:
             case SOL_TCP:
             case SOL_UDP:
                 switch(SocketOption.valueOf(opt)) {
                 case SO_BROADCAST:
                     setBroadcast(val);
                     break;
                 case SO_KEEPALIVE:
                     setKeepAlive(val);
                     break;
                 case SO_LINGER:
                     setLinger(val);
                     break;
                 case SO_OOBINLINE:
                     setOOBInline(val);
                     break;
                 case SO_RCVBUF:
                     setRcvBuf(val);
                     break;
                 case SO_REUSEADDR:
                     setReuseAddr(val);
                     break;
                 case SO_SNDBUF:
                     setSndBuf(val);
                     break;
                 case SO_RCVTIMEO:
                 case SO_SNDTIMEO:
                     setTimeout(val);
                     break;
                     // Can't support the rest with Java
                 case SO_TYPE:
                 case SO_RCVLOWAT:
                 case SO_SNDLOWAT:
                 case SO_DEBUG:
                 case SO_ERROR:
                 case SO_DONTROUTE:
                 case SO_TIMESTAMP:
                     break;
                 default:
                     if (IPPROTO_TCP.value() == level && TCP_NODELAY.value() == opt) {
                         setTcpNoDelay(val);
                     } else {
                         throw context.getRuntime().newErrnoENOPROTOOPTError();
                     }
                 }
                 break;
             default:
                 if (IPPROTO_TCP.value() == level && TCP_NODELAY.value() == opt) {
                     setTcpNoDelay(val);
                 } else {
                     throw context.getRuntime().newErrnoENOPROTOOPTError();
                 }
             }
         } catch(IOException e) {
             throw context.getRuntime().newErrnoENOPROTOOPTError();
         }
         return context.getRuntime().newFixnum(0);
     }
 
     @Deprecated
     public IRubyObject getsockname() {
         return getsockname(getRuntime().getCurrentContext());
     }
 
     @JRubyMethod(name = {"getsockname", "__getsockname"})
     public IRubyObject getsockname(ThreadContext context) {
         SocketAddress sock = getLocalSocket();
         if(null == sock) {
             return RubySocket.pack_sockaddr_in(context, null, 0, "0.0.0.0");
         }
         return context.getRuntime().newString(sock.toString());
     }
     @Deprecated
     public IRubyObject getpeername() {
         return getpeername(getRuntime().getCurrentContext());
     }
     @JRubyMethod(name = {"getpeername", "__getpeername"})
     public IRubyObject getpeername(ThreadContext context) {
         SocketAddress sock = getRemoteSocket();
         if(null == sock) {
             throw context.getRuntime().newIOError("Not Supported");
         }
         return context.getRuntime().newString(sock.toString());
     }
 
     @JRubyMethod(optional = 1)
     public IRubyObject shutdown(ThreadContext context, IRubyObject[] args) {
         if (context.getRuntime().getSafeLevel() >= 4 && tainted_p(context).isFalse()) {
             throw context.getRuntime().newSecurityError("Insecure: can't shutdown socket");
         }
 
         int how = 2;
         if (args.length > 0) {
             how = RubyNumeric.fix2int(args[0]);
         }
         return shutdownInternal(context, how);
     }
 
     private IRubyObject shutdownInternal(ThreadContext context, int how) {
         Channel socketChannel;
         switch (how) {
         case 0:
             socketChannel = openFile.getMainStream().getDescriptor().getChannel();
             try {
                 if (socketChannel instanceof SocketChannel
                         || socketChannel instanceof DatagramChannel) {
                     asSocket().shutdownInput();
                 } else if (socketChannel instanceof Shutdownable) {
                     ((Shutdownable)socketChannel).shutdownInput();
                 }
             } catch (IOException e) {
                 throw context.getRuntime().newIOError(e.getMessage());
             }
             if(openFile.getPipeStream() != null) {
                 openFile.setMainStream(openFile.getPipeStream());
                 openFile.setPipeStream(null);
             }
             openFile.setMode(openFile.getMode() & ~OpenFile.READABLE);
             return RubyFixnum.zero(context.getRuntime());
         case 1:
             socketChannel = openFile.getMainStream().getDescriptor().getChannel();
             try {
                 if (socketChannel instanceof SocketChannel
                         || socketChannel instanceof DatagramChannel) {
                     asSocket().shutdownOutput();
                 } else if (socketChannel instanceof Shutdownable) {
                     ((Shutdownable)socketChannel).shutdownOutput();
                 }
             } catch (IOException e) {
                 throw context.getRuntime().newIOError(e.getMessage());
             }
             openFile.setPipeStream(null);
             openFile.setMode(openFile.getMode() & ~OpenFile.WRITABLE);
             return RubyFixnum.zero(context.getRuntime());
         case 2:
             shutdownInternal(context, 0);
             shutdownInternal(context, 1);
             return RubyFixnum.zero(context.getRuntime());
         default:
             throw context.getRuntime().newArgumentError("`how' should be either 0, 1, 2");
         }
     }
 
     protected boolean doNotReverseLookup(ThreadContext context) {
         return context.getRuntime().isDoNotReverseLookupEnabled() || doNotReverseLookup;
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject do_not_reverse_lookup19(ThreadContext context) {
         return context.getRuntime().newBoolean(doNotReverseLookup);
     }
 
     @JRubyMethod(name = "do_not_reverse_lookup=", compat = CompatVersion.RUBY1_9)
     public IRubyObject set_do_not_reverse_lookup19(ThreadContext context, IRubyObject flag) {
         doNotReverseLookup = flag.isTrue();
         return do_not_reverse_lookup19(context);
     }
 
     @JRubyMethod(meta = true)
     public static IRubyObject do_not_reverse_lookup(IRubyObject recv) {
         return recv.getRuntime().isDoNotReverseLookupEnabled() ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
     
     @JRubyMethod(name = "do_not_reverse_lookup=", meta = true)
     public static IRubyObject set_do_not_reverse_lookup(IRubyObject recv, IRubyObject flag) {
         recv.getRuntime().setDoNotReverseLookupEnabled(flag.isTrue());
         return recv.getRuntime().isDoNotReverseLookupEnabled() ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
 }// RubyBasicSocket
