diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 23366e2289..a1cd88f6fd 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1127 +1,1128 @@
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
 import org.jruby.runtime.profile.IProfileData;
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
+import java.nio.channels.ClosedChannelException;
 import java.util.EnumSet;
 import java.util.concurrent.atomic.AtomicLong;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.RubyInstanceConfig.CompileMode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.executable.RuntimeCache;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.Unrescuable;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.interpreter.Interpreter;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.management.BeanManager;
 import org.jruby.management.BeanManagerFactory;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.load.BasicLibraryService;
 import org.jruby.threading.DaemonThreadFactory;
 import org.jruby.util.io.SelectorPool;
 
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
         this.is1_9              = config.getCompatVersion() == CompatVersion.RUBY1_9;
         this.doNotReverseLookupEnabled = is1_9;
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
 
         this.runtimeCache = new RuntimeCache();
         runtimeCache.initMethodCache(ClassIndex.MAX_CLASSES * MethodIndex.MAX_METHODS);
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
             return ASTInterpreter.INTERPRET_ROOT(this, context, node, context.getFrameSelf(), Block.NULL_BLOCK);
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
         if (compile || config.isShowBytecode()) {
             script = tryCompile(scriptNode, null, new JRubyClassLoader(getJRubyClassLoader()), config.isShowBytecode());
         }
 
         if (script != null) {
             if (config.isShowBytecode()) {
                 return getNil();
             }
             
             return runScript(script);
         } else {
             failForcedCompile(scriptNode);
             
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
 
     private void failForcedCompile(Node scriptNode) throws RaiseException {
         if (config.getCompileMode().shouldPrecompileAll()) {
             throw newRuntimeError("could not compile and compile mode is 'force': " + scriptNode.getPosition().getFile());
         }
     }
 
     private void handeCompileError(Node node, Throwable t) {
         if (config.isJitLoggingVerbose() || config.isDebug()) {
             System.err.println("warning: could not compile: " + node.getPosition().getFile() + "; full trace follows");
             t.printStackTrace();
         }
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
         } catch (Throwable t) {
             handeCompileError(node, t);
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
 
     public IRubyObject runInterpreter(ThreadContext context, Node rootNode, IRubyObject self) {
         assert rootNode != null : "scriptNode is not null";
 
         try {
             if (getInstanceConfig().getCompileMode() == CompileMode.OFFIR) {
                 return Interpreter.interpret(this, rootNode, self);
             } else {
                 return ASTInterpreter.INTERPRET_ROOT(this, context, rootNode, getTopSelf(), Block.NULL_BLOCK);
             }
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     public IRubyObject runInterpreter(Node scriptNode) {
         return runInterpreter(getCurrentContext(), scriptNode, getTopSelf());
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runInterpreterBody(Node scriptNode) {
         assert scriptNode != null : "scriptNode is not null";
         assert scriptNode instanceof RootNode : "scriptNode is not a RootNode";
 
         return runInterpreter(((RootNode) scriptNode).getBodyNode());
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
         
         if(config.isProfiling()) {
             getLoadService().require("jruby/profiler/shutdown_hook");
         }
 
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
@@ -2231,1751 +2232,1755 @@ public final class Ruby {
      */
     public void setDebug(IRubyObject debug) {
         this.debug = debug.isTrue();
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
 
     public Node parseFile(InputStream in, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addLoadParse();
         return parser.parse(file, in, scope, new ParserConfiguration(this,
                 lineNumber, false, false, true, config));
     }
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         ParserConfiguration parserConfig =
                 new ParserConfiguration(this, 0, false, true, false, config);
         if (is1_9) parserConfig.setDefaultEncoding(getEncodingService().getLocaleEncoding());
         return parser.parse(file, in, scope, parserConfig);
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, extraPositionInformation, false, true, config));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, config));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
         return parser.parse(file, content, scope, new ParserConfiguration(this,
                 lineNumber, extraPositionInformation, false, true, config));
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
 
     public Encoding getDefaultInternalEncoding() {
         return defaultInternalEncoding;
     }
 
     public void setDefaultInternalEncoding(Encoding defaultInternalEncoding) {
         this.defaultInternalEncoding = defaultInternalEncoding;
     }
 
     public Encoding getDefaultExternalEncoding() {
         return defaultExternalEncoding;
     }
 
     public void setDefaultExternalEncoding(Encoding defaultExternalEncoding) {
         this.defaultExternalEncoding = defaultExternalEncoding;
     }
 
     public EncodingService getEncodingService() {
         return encodingService;
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
 
         PrintStream errorStream = getErrorStream();
         errorStream.print(config.getTraceType().printBacktrace(excp));
     }
     
     public void loadFile(String scriptName, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(scriptName);
             context.preNodeEval(objectClass, self, scriptName);
 
             runInterpreter(context, parseFile(in, scriptName, null), self);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             context.setFile(file);
         }
     }
     
     public void compileAndLoadFile(String filename, InputStream in, boolean wrap) {
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         InputStream readStream = in;
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(filename);
 
             Script script = null;
             String className = null;
 
             try {
                 // read full contents of file, hash it, and try to load that class first
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 byte[] buffer = new byte[1024];
                 int num;
                 while ((num = in.read(buffer)) > -1) {
                     baos.write(buffer, 0, num);
                 }
                 buffer = baos.toByteArray();
                 String hash = JITCompiler.getHashForBytes(buffer);
                 className = JITCompiler.RUBY_JIT_PREFIX + ".FILE_" + hash;
 
                 // FIXME: duplicated from ClassCache
                 Class contents;
                 try {
                     contents = jrubyClassLoader.loadClass(className);
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("found jitted code for " + filename + " at class: " + className);
                     }
                     script = (Script)contents.newInstance();
                     readStream = new ByteArrayInputStream(buffer);
                 } catch (ClassNotFoundException cnfe) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("no jitted code in classloader for file " + filename + " at class: " + className);
                     }
                 } catch (InstantiationException ie) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 } catch (IllegalAccessException iae) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 }
             } catch (IOException ioe) {
                 // TODO: log something?
             }
 
             // script was not found in cache above, so proceed to compile
             Node scriptNode = parseFile(readStream, filename, null);
             if (script == null) {
                 script = tryCompile(scriptNode, className, new JRubyClassLoader(jrubyClassLoader), false);
             }
 
             if (script == null) {
                 failForcedCompile(scriptNode);
 
                 runInterpreter(scriptNode);
             } else {
                 runScript(script);
             }
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.setFile(file);
         }
     }
 
     public void loadScript(Script script) {
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
             
             script.load(context, self, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         }
     }
 
     /**
      * Load the given BasicLibraryService instance, wrapping it in Ruby framing
      * to ensure it is isolated from any parent scope.
      * 
      * @param extName The name of the extension, to go on the frame wrapping it
      * @param extension The extension object to load
      * @param wrap Whether to use a new "self" for toplevel
      */
     public void loadExtension(String extName, BasicLibraryService extension, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
 
         try {
             secure(4); /* should alter global state */
 
             context.setFile(extName);
             context.preExtensionLoad(self);
 
             extension.basicLoad(this);
         } catch (IOException ioe) {
             throw newIOErrorFromException(ioe);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             context.setFile(file);
         }
     }
 
     public void addBoundMethod(String javaName, String rubyName) {
         boundMethods.put(javaName, rubyName);
     }
 
     public Map<String, String> getBoundMethods() {
         return boundMethods;
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
                 
                 RubyBinding binding = RubyBinding.newBinding(Ruby.this, context.currentBinding());
 
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
         if (!RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // without full tracing, many events will not fire
             getWarnings().warn("tracing (e.g. set_trace_func) will not capture all events without --debug flag");
         }
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
         if (context.isEventHooksEnabled()) {
             for (EventHook eventHook : eventHooks) {
                 if (eventHook.isInterestedInEvent(event)) {
                     eventHook.event(context, event, file, line, name, type);
                 }
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
         tearDown(true);
     }
 
     // tearDown(boolean) has been added for embedding API. When an error
     // occurs in Ruby code, JRuby does system exit abruptly, no chance to
     // catch exception. This makes debugging really hard. This is why
     // tearDown(boolean) exists.
     public void tearDown(boolean systemExit) {
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
             synchronized (finalizersMutex) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(finalizers.keySet()).iterator(); finalIter.hasNext();) {
                     Finalizable f = finalIter.next();
                     if (f != null) {
                         try {
                             f.finalize();
                         } catch (Throwable t) {
                             // ignore
                         }
                     }
                     finalIter.remove();
                 }
             }
         }
 
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers != null) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(
                         internalFinalizers.keySet()).iterator(); finalIter.hasNext();) {
                     Finalizable f = finalIter.next();
                     if (f != null) {
                         try {
                             f.finalize();
                         } catch (Throwable t) {
                             // ignore
                         }
                     }
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
 
         if (getJRubyClassLoader() != null) {
             getJRubyClassLoader().tearDown(isDebug());
         }
 
         if (config.isProfilingEntireRun()) {
             System.err.println("\nmain thread profile results:");
             IProfileData profileData = (IProfileData) threadService.getMainThread().getContext().getProfileData();
             config.makeDefaultProfilePrinter(profileData).printProfile(System.err);
         }
 
         if (systemExit && status != 0) {
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
 
     public RubyArray newArray(IRubyObject... objects) {
         return RubyArray.newArray(this, objects);
     }
     
     public RubyArray newArrayNoCopy(IRubyObject... objects) {
         return RubyArray.newArrayNoCopy(this, objects);
     }
     
     public RubyArray newArrayNoCopyLight(IRubyObject... objects) {
         return RubyArray.newArrayNoCopyLight(this, objects);
     }
     
     public RubyArray newArray(List<IRubyObject> list) {
         return RubyArray.newArray(this, list);
     }
 
     public RubyArray newArray(int size) {
         return RubyArray.newArray(this, size);
     }
 
     public RubyBoolean newBoolean(boolean value) {
         return value ? trueObject : falseObject;
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
 
     public RubyFixnum newFixnum(Constant value) {
         return RubyFixnum.newFixnum(this, value.value());
     }
 
     public RubyFloat newFloat(double value) {
         return RubyFloat.newFloat(this, value);
     }
 
     public RubyNumeric newNumeric() {
         return RubyNumeric.newNumeric(this);
     }
 
     public RubyRational newRational(long num, long den) {
         return RubyRational.newRationalRaw(this, newFixnum(num), newFixnum(den));
     }
 
     public RubyProc newProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, block, type);
 
         return proc;
     }
 
     public RubyProc newBlockPassProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, block, type);
 
         return proc;
     }
 
     public RubyBinding newBinding() {
         return RubyBinding.newBinding(this, getCurrentContext().currentBinding());
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
         //        assert internedName == internedName.intern() : internedName + " is not interned";
 
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
         return newRaiseException(getArgumentError(), "wrong number of arguments (" + got + " for " + expected + ")");
     }
 
     public RaiseException newErrnoEBADFError() {
         return newRaiseException(getErrno().fastGetClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoEISCONNError() {
         return newRaiseException(getErrno().fastGetClass("EISCONN"), "Socket is already connected");
     }
 
     public RaiseException newErrnoEINPROGRESSError() {
         return newRaiseException(getErrno().fastGetClass("EINPROGRESS"), "Operation now in progress");
     }
 
     public RaiseException newErrnoENOPROTOOPTError() {
         return newRaiseException(getErrno().fastGetClass("ENOPROTOOPT"), "Protocol not available");
     }
 
     public RaiseException newErrnoEPIPEError() {
         return newRaiseException(getErrno().fastGetClass("EPIPE"), "Broken pipe");
     }
 
     public RaiseException newErrnoECONNABORTEDError() {
         return newRaiseException(getErrno().fastGetClass("ECONNABORTED"),
                 "An established connection was aborted by the software in your host machine");
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
 
     public RaiseException newErrnoEADDRINUSEError(String message) {
         return newRaiseException(getErrno().fastGetClass("EADDRINUSE"), message);
     }
 
     public RaiseException newErrnoEHOSTUNREACHError(String message) {
         return newRaiseException(getErrno().fastGetClass("EHOSTUNREACH"), message);
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
 
     public RaiseException newErrnoEISDirError(String message) {
         return newRaiseException(getErrno().fastGetClass("EISDIR"), message);
     }
 
     public RaiseException newErrnoEPERMError(String name) {
         return newRaiseException(getErrno().fastGetClass("EPERM"), "Operation not permitted - " + name);
     }
 
     public RaiseException newErrnoEISDirError() {
         return newErrnoEISDirError("Is a directory");
     }
 
     public RaiseException newErrnoESPIPEError() {
         return newRaiseException(getErrno().fastGetClass("ESPIPE"), "Illegal seek");
     }
 
     public RaiseException newErrnoEBADFError(String message) {
         return newRaiseException(getErrno().fastGetClass("EBADF"), message);
     }
 
     public RaiseException newErrnoEINPROGRESSError(String message) {
         return newRaiseException(getErrno().fastGetClass("EINPROGRESS"), message);
     }
 
     public RaiseException newErrnoEISCONNError(String message) {
         return newRaiseException(getErrno().fastGetClass("EISCONN"), message);
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
 
     public RaiseException newErrnoENOTCONNError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOTCONN"), message);
     }
 
     public RaiseException newErrnoENOTCONNError() {
         return newRaiseException(getErrno().fastGetClass("ENOTCONN"), "Socket is not connected");
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
 
     public RaiseException newErrnoEADDRNOTAVAILError(String message) {
         return newRaiseException(getErrno().fastGetClass("EADDRNOTAVAIL"), message);
     }
 
     public RaiseException newErrnoESRCHError() {
         return newRaiseException(getErrno().fastGetClass("ESRCH"), null);
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
 
     public RaiseException newErrnoFromLastPOSIXErrno() {
         return newRaiseException(getErrno(getPosix().errno()), null);
     }
 
     public RaiseException newErrnoFromInt(int errno, String message) {
         RubyClass errnoClass = getErrno(errno);
         if (errnoClass != null) {
             return newRaiseException(errnoClass, message);
         } else {
             return newSystemCallError("Unknown Error (" + errno + ") - " + message);
         }
     }
 
     public RaiseException newErrnoFromInt(int errno) {
         Errno errnoObj = Errno.valueOf(errno);
         if (errnoObj == null) {
             return newSystemCallError("Unknown Error (" + errno + ")");
         }
         String message = errnoObj.description();
         return newErrnoFromInt(errno, message);
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
     
     public RaiseException newIllegalSequence(String message) {
         return newRaiseException(fastGetClass("Iconv").fastGetClass("IllegalSequence"), message);
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
         if (printWhenVerbose && origException != null && this.isVerbose()) {
             origException.printStackTrace(getErrorStream());
         }
         
         return new RaiseException(new RubyNameError(
                 this, getNameError(), message, name), false);
     }
 
     public RaiseException newLocalJumpError(RubyLocalJumpError.Reason reason, IRubyObject exitValue, String message) {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), message, reason, exitValue), true);
     }
 
     public RaiseException newLocalJumpErrorNoBlock() {
         return newLocalJumpError(RubyLocalJumpError.Reason.NOREASON, getNil(), "no block given");
     }
 
     public RaiseException newRedoLocalJumpError() {
         return newLocalJumpError(RubyLocalJumpError.Reason.REDO, getNil(), "unexpected redo");
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(getLoadError(), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         return newFrozenError(objectType, false);
     }
 
     public RaiseException newFrozenError(String objectType, boolean runtimeError) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(is1_9() || runtimeError ? getRuntimeError() : getTypeError(), "can't modify frozen " + objectType);
     }
 
     public RaiseException newSystemStackError(String message) {
         return newRaiseException(getSystemStackError(), message);
     }
 
     public RaiseException newSystemStackError(String message, StackOverflowError soe) {
         if (getDebug().isTrue()) {
             soe.printStackTrace(getInstanceConfig().getError());
         }
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
+        if (ioe instanceof ClosedChannelException) {
+            throw newIOError("closed stream");
+        }
+
         // TODO: this is kinda gross
         if(ioe.getMessage() != null) {
             if (ioe.getMessage().equals("Broken pipe")) {
                 throw newErrnoEPIPEError();
             } else if (ioe.getMessage().equals("Connection reset by peer") ||
                     (Platform.IS_WINDOWS && ioe.getMessage().contains("connection was aborted"))) {
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
 
     public RaiseException newMathDomainError(String message) {
         return newRaiseException(getMathDomainError(), "Numerical argument is out of domain - \"" + message + "\"");
     }
 
     public RaiseException newEncodingError(String message){
         return newRaiseException(getEncodingError(), message);
     }
 
     public RaiseException newEncodingCompatibilityError(String message){
         return newRaiseException(getEncodingCompatibilityError(), message);
     }
 
     public RaiseException newConverterNotFoundError(String message) {
         return newRaiseException(getConverterNotFoundError(), message);
     }
 
     public RaiseException newFiberError(String message) {
         return newRaiseException(getFiberError(), message);
     }
 
     public RaiseException newUndefinedConversionError(String message) {
         return newRaiseException(getUndefinedConversionError(), message);
     }
 
     public RaiseException newInvalidByteSequenceError(String message) {
         return newRaiseException(getInvalidByteSequenceError(), message);
     }
 
     /**
      * @param exceptionClass
      * @param message
      * @return
      */
     private RaiseException newRaiseException(RubyClass exceptionClass, String message) {
         return new RaiseException(this, exceptionClass, message, true);
     }
 
     // Equivalent of Data_Wrap_Struct
     public RubyObject.Data newData(RubyClass objectClass, Object sval) {
         return new RubyObject.Data(this, objectClass, sval);
     }
 
     public RubySymbol.SymbolTable getSymbolTable() {
         return symbolTable;
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
 
     private final Map<Integer, Integer> filenoExtIntMap = new HashMap<Integer, Integer>();
     private final Map<Integer, Integer> filenoIntExtMap = new HashMap<Integer, Integer>();
 
     public void putFilenoMap(int external, int internal) {
         filenoExtIntMap.put(external, internal);
         filenoIntExtMap.put(internal, external);
     }
 
     public int getFilenoExtMap(int external) {
         Integer internal = filenoExtIntMap.get(external);
         if (internal != null) return internal;
         return external;
     }
 
     public int getFilenoIntMap(int internal) {
         Integer external = filenoIntExtMap.get(internal);
         if (external != null) return external;
         return internal;
     }
 
     /**
      * Get the "external" fileno for a given ChannelDescriptor. Primarily for
      * the shared 0, 1, and 2 filenos, which we can't actually share across
      * JRuby runtimes.
      *
      * @param descriptor The descriptor for which to get the fileno
      * @return The external fileno for the descriptor
      */
     public int getFileno(ChannelDescriptor descriptor) {
         return getFilenoIntMap(descriptor.getFileno());
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor, boolean isRetained) {
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor) {
     }
 
     @Deprecated
     public void unregisterDescriptor(int aFileno) {
     }
 
     @Deprecated
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         return ChannelDescriptor.getDescriptorByFileno(aFileno);
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
 
     public static interface RecursiveFunction {
         IRubyObject call(IRubyObject obj, boolean recur);
     }
 
     private static class RecursiveError extends Error implements Unrescuable {
         public RecursiveError(Object tag) {
             this.tag = tag;
         }
         public final Object tag;
         
         @Override
         public synchronized Throwable fillInStackTrace() {
             return this;
         }
     }
 
     private ThreadLocal<Map<String, RubyHash>> recursive = new ThreadLocal<Map<String, RubyHash>>();
     private IRubyObject recursiveListAccess() {
         Map<String, RubyHash> hash = recursive.get();
         String sym = getCurrentContext().getFrameName();
         IRubyObject list = getNil();
         if(hash == null) {
             hash = new HashMap<String, RubyHash>();
             recursive.set(hash);
         } else {
             list = hash.get(sym);
         }
         if(list == null || list.isNil()) {
             list = RubyHash.newHash(this);
             list.setUntrusted(true);
             hash.put(sym, (RubyHash)list);
         }
         return list;
     }
 
     private RubySymbol recursiveKey;
 
     private static class ExecRecursiveParams {
         public ExecRecursiveParams() {}
         public RecursiveFunction func;
         public IRubyObject list;
         public IRubyObject obj;
         public IRubyObject objid;
         public IRubyObject pairid;
     }
 
     private void recursivePush(IRubyObject list, IRubyObject obj, IRubyObject paired_obj) {
         IRubyObject pair_list;
         if(paired_obj == null) {
             ((RubyHash)list).op_aset(getCurrentContext(), obj, getTrue());
         } else if((pair_list = ((RubyHash)list).fastARef(obj)) == null) {
             ((RubyHash)list).op_aset(getCurrentContext(), obj, paired_obj);
         } else {
             if(!(pair_list instanceof RubyHash)) {
                 IRubyObject other_paired_obj = pair_list;
                 pair_list = RubyHash.newHash(this);
                 pair_list.setUntrusted(true);
                 ((RubyHash)pair_list).op_aset(getCurrentContext(), other_paired_obj, getTrue());
                 ((RubyHash)list).op_aset(getCurrentContext(), obj, pair_list);
             }
             ((RubyHash)pair_list).op_aset(getCurrentContext(), paired_obj, getTrue());
         }
     }
 
     private void recursivePop(IRubyObject list, IRubyObject obj, IRubyObject paired_obj) {
         if(paired_obj != null) {
             IRubyObject pair_list = ((RubyHash)list).fastARef(obj);
             if(pair_list == null) {
                 throw newTypeError("invalid inspect_tbl pair_list for " + getCurrentContext().getFrameName());
             }
             if(pair_list instanceof RubyHash) {
                 ((RubyHash)pair_list).delete(getCurrentContext(), paired_obj, Block.NULL_BLOCK);
                 if(!((RubyHash)pair_list).isEmpty()) {
                     return;
                 }
             }
         }
         ((RubyHash)list).delete(getCurrentContext(), obj, Block.NULL_BLOCK);
     }
 
     private boolean recursiveCheck(IRubyObject list, IRubyObject obj_id, IRubyObject paired_obj_id) {
         IRubyObject pair_list = ((RubyHash)list).fastARef(obj_id);
         if(pair_list == null) {
             return false;
         }
         if(paired_obj_id != null) {
             if(!(pair_list instanceof RubyHash)) {
                 if(pair_list != paired_obj_id) {
                     return false;
                 }
             } else {
                 IRubyObject paired_result = ((RubyHash)pair_list).fastARef(paired_obj_id);
                 if(paired_result == null || paired_result.isNil()) {
                     return false;
                 }
             }
         }
         return true;
     }
 
     // exec_recursive_i
     private IRubyObject execRecursiveI(ExecRecursiveParams p) {
         IRubyObject result = null;
         recursivePush(p.list, p.objid, p.pairid);
         try {
             result = p.func.call(p.obj, false);
         } finally {
             recursivePop(p.list, p.objid, p.pairid);
         }
         return result;
     }
 
     // exec_recursive
     private IRubyObject execRecursiveInternal(RecursiveFunction func, IRubyObject obj, IRubyObject pairid, boolean outer) {
         ExecRecursiveParams p = new ExecRecursiveParams();
         p.list = recursiveListAccess();
         p.objid = obj.id();
         boolean outermost = outer && !recursiveCheck(p.list, recursiveKey, null);
         if(recursiveCheck(p.list, p.objid, pairid)) {
             if(outer && !outermost) {
                 throw new RecursiveError(p.list);
             }
             return func.call(obj, true); 
         } else {
             IRubyObject result = null;
             p.func = func;
             p.obj = obj;
             p.pairid = pairid;
 
             if(outermost) {
                 recursivePush(p.list, recursiveKey, null);
                 try {
                     result = execRecursiveI(p);
                 } catch(RecursiveError e) {
                     if(e.tag != p.list) {
                         throw e;
                     } else {
                         result = p.list;
                     }
                 }
                 recursivePop(p.list, recursiveKey, null);
                 if(result == p.list) {
                     result = func.call(obj, true);
                 }
             } else {
                 result = execRecursiveI(p);
             }
 
             return result;
         }
     }
 
     // rb_exec_recursive
     public IRubyObject execRecursive(RecursiveFunction func, IRubyObject obj) {
         return execRecursiveInternal(func, obj, null, false);
     }
 
     // rb_exec_recursive_outer
     public IRubyObject execRecursiveOuter(RecursiveFunction func, IRubyObject obj) {
         return execRecursiveInternal(func, obj, null, true);
     }
 
     public boolean isObjectSpaceEnabled() {
         return objectSpaceEnabled;
     }
 
     public void setObjectSpaceEnabled(boolean objectSpaceEnabled) {
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
 
     public boolean is1_9() {
         return is1_9;
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
 
     public Map<String, DateTimeZone> getTimezoneCache() {
         return timeZoneCache;
     }
 
     public int getConstantGeneration() {
         return constantGeneration;
     }
 
     public synchronized void incrementConstantGeneration() {
         constantGeneration++;
     }
 
     public <E extends Enum<E>> void loadConstantSet(RubyModule module, Class<E> enumClass) {
         for (E e : EnumSet.allOf(enumClass)) {
             Constant c = (Constant) e;
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.fastSetConstant(c.name(), newFixnum(c.value()));
             }
         }
     }
     public void loadConstantSet(RubyModule module, String constantSetName) {
         for (Constant c : ConstantSet.getConstantSet(constantSetName)) {
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.fastSetConstant(c.name(), newFixnum(c.value()));
             }
         }
     }
 
     /**
      * Get a new serial number for a new DynamicMethod instance
      * @return a new serial number
      */
     public long getNextDynamicMethodSerial() {
         return dynamicMethodSerial.getAndIncrement();
     }
 
     /**
      * Get a new generation number for a module or class.
      *
      * @return a new generation number
      */
     public int getNextModuleGeneration() {
         return moduleGeneration.incrementAndGet();
     }
 
     /**
      * Get the global object used to synchronize class-hierarchy modifications like
      * cache invalidation, subclass sets, and included hierarchy sets.
      *
      * @return The object to use for locking when modifying the hierarchy
      */
     public Object getHierarchyLock() {
         return hierarchyLock;
     }
 
     /**
      * Get the runtime-global selector pool
      *
      * @return a SelectorPool from which to get Selector instances
      */
     public SelectorPool getSelectorPool() {
         return selectorPool;
     }
 
     /**
      * Get the core class RuntimeCache instance, for doing dynamic calls from
      * core class methods.
      */
     public RuntimeCache getRuntimeCache() {
         return runtimeCache;
     }
 
     /**
      * Get the list of method names being profiled
      */
     public String[] getProfiledNames() {
         return profiledNames;
     }
 
     /**
      * Get the list of method objects for methods being profiled
      */
     public DynamicMethod[] getProfiledMethods() {
         return profiledMethods;
     }
 
     /**
      * Add a method and its name to the profiling arrays, so it can be printed out
      * later.
      *
      * @param name the name of the method
      * @param method
      */
     public void addProfiledMethod(String name, DynamicMethod method) {
         if (!config.isProfiling()) return;
         if (method.isUndefined()) return;
         if (method.getSerialNumber() > MAX_PROFILE_METHODS) return;
 
         int index = (int)method.getSerialNumber();
         if (profiledMethods.length <= index) {
             int newSize = Math.min((int)index * 2 + 1, MAX_PROFILE_METHODS);
             String[] newProfiledNames = new String[newSize];
             System.arraycopy(profiledNames, 0, newProfiledNames, 0, profiledNames.length);
             profiledNames = newProfiledNames;
             DynamicMethod[] newProfiledMethods = new DynamicMethod[newSize];
             System.arraycopy(profiledMethods, 0, newProfiledMethods, 0, profiledMethods.length);
             profiledMethods = newProfiledMethods;
         }
 
         // only add the first one we encounter, since others will probably share the original
         if (profiledNames[index] == null) {
             profiledNames[index] = name;
             profiledMethods[index] = method;
         }
     }
 
     private volatile int constantGeneration = 1;
     private final ThreadService threadService;
     
     private POSIX posix;
 
     private final ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
 
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private final List<EventHook> eventHooks = new Vector<EventHook>();
     private boolean hasEventHooks;  
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private volatile boolean objectSpaceEnabled;
     
     private final Set<Script> jittedMethods = Collections.synchronizedSet(new WeakHashSet<Script>());
     
     private long globalState = 1;
     
     private int safeLevel = -1;
 
     // Default objects
     private IRubyObject topSelf;
     private RubyNil nilObject;
     private IRubyObject[] singleNilArray;
     private RubyBoolean trueObject;
     private RubyBoolean falseObject;
     public final RubyFixnum[] fixnumCache = new RubyFixnum[2 * RubyFixnum.CACHE_OFFSET];
 
     private boolean verbose, warningsEnabled, debug;
     private IRubyObject verboseValue;
     
     private RubyThreadGroup defaultThreadGroup;
 
     /**
      * All the core classes we keep hard references to. These are here largely
      * so that if someone redefines String or Array we won't start blowing up
      * creating strings and arrays internally. They also provide much faster
      * access than going through normal hash lookup on the Object class.
      */
     private RubyClass
            basicObjectClass, objectClass, moduleClass, classClass, nilClass, trueClass,
             falseClass, numericClass, floatClass, integerClass, fixnumClass,
             complexClass, rationalClass, enumeratorClass, yielderClass,
             arrayClass, hashClass, rangeClass, stringClass, encodingClass, converterClass, symbolClass,
             procClass, bindingClass, methodClass, unboundMethodClass,
             matchDataClass, regexpClass, timeClass, bignumClass, dirClass,
             fileClass, fileStatClass, ioClass, threadClass, threadGroupClass,
             continuationClass, structClass, tmsStruct, passwdStruct,
             groupStruct, procStatusClass, exceptionClass, runtimeError, ioError,
             scriptError, nameError, nameErrorMessage, noMethodError, signalException,
             rangeError, dummyClass, systemExit, localJumpError, nativeException,
             systemCallError, fatal, interrupt, typeError, argumentError, indexError, stopIteration,
             syntaxError, standardError, loadError, notImplementedError, securityError, noMemoryError,
             regexpError, eofError, threadError, concurrencyError, systemStackError, zeroDivisionError, floatDomainError, mathDomainError,
             encodingError, encodingCompatibilityError, converterNotFoundError, undefinedConversionError,
             invalidByteSequenceError, fiberError, randomClass;
 
     /**
      * All the core modules we keep direct references to, for quick access and
      * to ensure they remain available.
      */
     private RubyModule
             kernelModule, comparableModule, enumerableModule, mathModule,
             marshalModule, etcModule, fileTestModule, gcModule,
             objectSpaceModule, processModule, procUIDModule, procGIDModule,
             procSysModule, precisionModule, errnoModule;
 
     private DynamicMethod privateMethodMissing, protectedMethodMissing, variableMethodMissing,
             superMethodMissing, normalMethodMissing, defaultMethodMissing;
     
     // record separator var, to speed up io ops that use it
     private GlobalVariable recordSeparatorVar;
 
     // former java.lang.System concepts now internalized for MVM
     private String currentDirectory;
 
     // The "current line" global variable
     private int currentLine = 0;
 
     private IRubyObject argsFile;
 
     private long startTime = System.currentTimeMillis();
 
     private final RubyInstanceConfig config;
     private final boolean is1_9;
 
     private final InputStream in;
     private final PrintStream out;
     private final PrintStream err;
 
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
 
     private final Parser parser = new Parser(this);
 
     private LoadService loadService;
 
     private Encoding defaultInternalEncoding, defaultExternalEncoding;
     private EncodingService encodingService;
 
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private final RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private final Stack<RubyProc> atExitBlocks = new Stack<RubyProc>();
 
     private Profile profile;
 
     private KCode kcode = KCode.NONE;
 
     // Atomic integers for symbol and method IDs
     private final AtomicInteger symbolLastId = new AtomicInteger(128);
     private final AtomicInteger moduleLastId = new AtomicInteger(0);
 
     // Weak map of all Modules in the system (and by extension, all Classes
     private final Set<RubyModule> allModules = new WeakHashSet<RubyModule>();
 
     private Object respondToMethod;
 
     private final Map<String, DateTimeZone> timeZoneCache = new HashMap<String,DateTimeZone>();
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
 
     // A global object lock for class hierarchy mutations
     private final Object hierarchyLock = new Object();
 
     // An atomic long for generating DynamicMethod serial numbers
     private final AtomicLong dynamicMethodSerial = new AtomicLong(1);
 
     // An atomic int for generating class generation numbers
     private final AtomicInteger moduleGeneration = new AtomicInteger(1);
 
     // A list of Java class+method names to include in backtraces
     private final Map<String, String> boundMethods = new HashMap();
 
     // A soft pool of selectors for blocking IO operations
     private final SelectorPool selectorPool = new SelectorPool();
 
     // A global cache for Java-to-Ruby calls
     private final RuntimeCache runtimeCache;
 
     // The maximum number of methods we will track for profiling purposes
     private static final int MAX_PROFILE_METHODS = 100000;
 
     // The list of method names associated with method serial numbers
     public String[] profiledNames = new String[0];
 
     // The method objects for serial numbers
     public DynamicMethod[] profiledMethods = new DynamicMethod[0];
 }
diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 5740f94734..294d79309b 100755
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1883 +1,1897 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2003 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2007 Charles O Nutter <headius@headius.com>
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
 
 import com.kenai.constantine.platform.OpenFlags;
 import org.jcodings.Encoding;
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.ChannelDescriptor;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.net.URI;
 import java.net.URL;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.FileLock;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipFile;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.ext.posix.FileStat;
 import org.jruby.ext.posix.util.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.DirectoryAsFileException;
 import org.jruby.util.io.PermissionDeniedException;
 import org.jruby.util.io.Stream;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 import static org.jruby.CompatVersion.*;
 
 /**
  * Ruby File class equivalent in java.
  **/
 @JRubyClass(name="File", parent="IO", include="FileTest")
 public class RubyFile extends RubyIO implements EncodingCapable {
     private static final long serialVersionUID = 1L;
     
     public static final int LOCK_SH = 1;
     public static final int LOCK_EX = 2;
     public static final int LOCK_NB = 4;
     public static final int LOCK_UN = 8;
 
     private static final int FNM_NOESCAPE = 1;
     private static final int FNM_PATHNAME = 2;
     private static final int FNM_DOTMATCH = 4;
     private static final int FNM_CASEFOLD = 8;
     private static final int FNM_SYSCASE;
 
     private static int _cachedUmask = 0;
     private static final Object _umaskLock = new Object();
 
     static {
         if (Platform.IS_WINDOWS) {
             FNM_SYSCASE = FNM_CASEFOLD;
         } else {
             FNM_SYSCASE = 0;
         }
     }
 
     public Encoding getEncoding() {
         return null;
     }
 
     public void setEncoding(Encoding encoding) {
         // :)
     }
 
     private static boolean startsWithDriveLetterOnWindows(String path) {
         return (path != null)
             && Platform.IS_WINDOWS && 
             ((path.length()>1 && path.charAt(0) == '/') ? 
              (path.length() > 2
               && isWindowsDriveLetter(path.charAt(1))
               && path.charAt(2) == ':') : 
              (path.length() > 1
               && isWindowsDriveLetter(path.charAt(0))
               && path.charAt(1) == ':'));
     }
     // adjusts paths started with '/' or '\\', on windows.
     static String adjustRootPathOnWindows(Ruby runtime, String path, String dir) {
         if (path == null || !Platform.IS_WINDOWS) return path;
 
         // MRI behavior on Windows: it treats '/' as a root of
         // a current drive (but only if SINGLE slash is present!):
         // E.g., if current work directory is
         // 'D:/home/directory', then '/' means 'D:/'.
         //
         // Basically, '/path' is treated as a *RELATIVE* path,
         // relative to the current drive. '//path' is treated
         // as absolute one.
         if ((path.startsWith("/") && !(path.length() > 2 && path.charAt(2) == ':')) || path.startsWith("\\")) {
             if (path.length() > 1 && (path.charAt(1) == '/' || path.charAt(1) == '\\')) {
                 return path;
             }
 
             // First try to use drive letter from supplied dir value,
             // then try current work dir.
             if (!startsWithDriveLetterOnWindows(dir)) {
                 dir = runtime.getCurrentDirectory();
             }
             if (dir.length() >= 2) {
                 path = dir.substring(0, 2) + path;
             }
         } else if (startsWithDriveLetterOnWindows(path) && path.length() == 2) {
             // compensate for missing slash after drive letter on windows
             path += "/";
         }
 
         return path;
     }
 
     protected String path;
     private FileLock currentLock;
     
     public RubyFile(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
     // XXX This constructor is a hack to implement the __END__ syntax.
     //     Converting a reader back into an InputStream doesn't generally work.
     public RubyFile(Ruby runtime, String path, final Reader reader) {
         this(runtime, path, new InputStream() {
             public int read() throws IOException {
                 return reader.read();
             }
         });
     }
     
     public RubyFile(Ruby runtime, String path, InputStream in) {
         super(runtime, runtime.getFile());
         this.path = path;
         try {
             this.openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(in))));
+            this.openFile.setMode(openFile.getMainStreamSafe().getModes().getOpenFileFlags());
+        } catch (BadDescriptorException e) {
+            throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         }
-        this.openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
     }
 
     private static ObjectAllocator FILE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyFile instance = new RubyFile(runtime, klass);
             
             instance.setMetaClass(klass);
             
             return instance;
         }
     };
 
     public String getPath() {
         return path;
     }
 
     @JRubyModule(name="File::Constants")
     public static class Constants {}
     
     public static RubyClass createFileClass(Ruby runtime) {
         RubyClass fileClass = runtime.defineClass("File", runtime.getIO(), FILE_ALLOCATOR);
         // Create Constants class
         RubyModule constants = fileClass.defineModuleUnder("Constants");
         runtime.setFile(fileClass);
 
         fileClass.index = ClassIndex.FILE;
         fileClass.setReifiedClass(RubyFile.class);
 
         RubyString separator = runtime.newString("/");
         ThreadContext context = runtime.getCurrentContext();
         
         fileClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyFile;
             }
         };
 
         separator.freeze(context);
         fileClass.defineConstant("SEPARATOR", separator);
         fileClass.defineConstant("Separator", separator);
         
         if (File.separatorChar == '\\') {
             RubyString altSeparator = runtime.newString("\\");
             altSeparator.freeze(context);
             fileClass.defineConstant("ALT_SEPARATOR", altSeparator);
         } else {
             fileClass.defineConstant("ALT_SEPARATOR", runtime.getNil());
         }
         
         RubyString pathSeparator = runtime.newString(File.pathSeparator);
         pathSeparator.freeze(context);
         fileClass.defineConstant("PATH_SEPARATOR", pathSeparator);
 
         // TODO: why are we duplicating the constants here, and then in
         // File::Constants below? File::Constants is included in IO.
 
         // TODO: These were missing, so we're not handling them elsewhere?
         fileClass.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         fileClass.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         fileClass.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_SYSCASE));
         fileClass.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
         fileClass.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
         
         // Create constants for open flags
         for (OpenFlags f : OpenFlags.values()) {
             // Strip off the O_ prefix, so they become File::RDONLY, and so on
             final String name = f.name();
             if (name.startsWith("O_")) {
                 final String cname = name.substring(2);
                 // Special case for handling ACCMODE, since constantine will generate
                 // an invalid value if it is not defined by the platform.
                 final RubyFixnum cvalue = f == OpenFlags.O_ACCMODE
                         ? runtime.newFixnum(ModeFlags.ACCMODE)
                         : runtime.newFixnum(f.value());
                 fileClass.fastSetConstant(cname, cvalue);
                 constants.fastSetConstant(cname, cvalue);
             }
         }
         
         // Create constants for flock
         fileClass.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         fileClass.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         fileClass.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         fileClass.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         constants.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         constants.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         constants.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_SYSCASE));
         constants.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
         constants.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
         
         // Create constants for flock
         constants.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         constants.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         constants.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         constants.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         // File::Constants module is included in IO.
         runtime.getIO().includeModule(constants);
 
         runtime.getFileTest().extend_object(fileClass);
         
         fileClass.defineAnnotatedMethods(RubyFile.class);
 
         // For JRUBY-5276, physically define FileTest methods on File's singleton
         fileClass.getSingletonClass().defineAnnotatedMethods(RubyFileTest.FileTestFileMethods.class);
         
         return fileClass;
     }
     
     @JRubyMethod
     @Override
     public IRubyObject close() {
         // Make sure any existing lock is released before we try and close the file
         if (currentLock != null) {
             try {
                 currentLock.release();
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
         }
         return super.close();
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject flock(ThreadContext context, IRubyObject lockingConstant) {
         // TODO: port exact behavior from MRI, and move most locking logic into ChannelDescriptor
         // TODO: for all LOCK_NB cases, return false if they would block
-        ChannelDescriptor descriptor = openFile.getMainStream().getDescriptor();
-        
-        // null channel always succeeds for all locking operations
-        if (descriptor.isNull()) return RubyFixnum.zero(context.getRuntime());
-
-        if (descriptor.getChannel() instanceof FileChannel) {
-            FileChannel fileChannel = (FileChannel)descriptor.getChannel();
-            int lockMode = RubyNumeric.num2int(lockingConstant);
-
-            // Exclusive locks in Java require the channel to be writable, otherwise
-            // an exception is thrown (terminating JRuby execution).
-            // But flock behavior of MRI is that it allows
-            // exclusive locks even on non-writable file. So we convert exclusive
-            // lock to shared lock if the channel is not writable, to better match
-            // the MRI behavior.
-            if (!openFile.isWritable() && (lockMode & LOCK_EX) > 0) {
-                lockMode = (lockMode ^ LOCK_EX) | LOCK_SH;
-            }
-
-            try {
-                switch (lockMode) {
-                    case LOCK_UN:
-                    case LOCK_UN | LOCK_NB:
-                        if (currentLock != null) {
-                            currentLock.release();
-                            currentLock = null;
-
-                            return RubyFixnum.zero(context.getRuntime());
-                        }
-                        break;
-                    case LOCK_EX:
-                        if (currentLock != null) {
-                            currentLock.release();
-                            currentLock = null;
-                        }
-                        currentLock = fileChannel.lock();
-                        if (currentLock != null) {
-                            return RubyFixnum.zero(context.getRuntime());
-                        }
-
-                        break;
-                    case LOCK_EX | LOCK_NB:
-                        if (currentLock != null) {
-                            currentLock.release();
-                            currentLock = null;
-                        }
-                        currentLock = fileChannel.tryLock();
-                        if (currentLock != null) {
-                            return RubyFixnum.zero(context.getRuntime());
-                        }
-
-                        break;
-                    case LOCK_SH:
-                        if (currentLock != null) {
-                            currentLock.release();
-                            currentLock = null;
-                        }
-
-                        currentLock = fileChannel.lock(0L, Long.MAX_VALUE, true);
-                        if (currentLock != null) {
-                            return RubyFixnum.zero(context.getRuntime());
-                        }
-
-                        break;
-                    case LOCK_SH | LOCK_NB:
-                        if (currentLock != null) {
-                            currentLock.release();
-                            currentLock = null;
-                        }
-
-                        currentLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true);
-                        if (currentLock != null) {
-                            return RubyFixnum.zero(context.getRuntime());
-                        }
-
-                        break;
-                    default:
-                }
-            } catch (IOException ioe) {
-                if (context.getRuntime().getDebug().isTrue()) {
-                    ioe.printStackTrace(System.err);
+        try {
+            ChannelDescriptor descriptor = openFile.getMainStreamSafe().getDescriptor();
+
+            // null channel always succeeds for all locking operations
+            if (descriptor.isNull()) return RubyFixnum.zero(context.getRuntime());
+
+            if (descriptor.getChannel() instanceof FileChannel) {
+                FileChannel fileChannel = (FileChannel)descriptor.getChannel();
+                int lockMode = RubyNumeric.num2int(lockingConstant);
+
+                // Exclusive locks in Java require the channel to be writable, otherwise
+                // an exception is thrown (terminating JRuby execution).
+                // But flock behavior of MRI is that it allows
+                // exclusive locks even on non-writable file. So we convert exclusive
+                // lock to shared lock if the channel is not writable, to better match
+                // the MRI behavior.
+                if (!openFile.isWritable() && (lockMode & LOCK_EX) > 0) {
+                    lockMode = (lockMode ^ LOCK_EX) | LOCK_SH;
                 }
-            } catch (java.nio.channels.OverlappingFileLockException ioe) {
-                if (context.getRuntime().getDebug().isTrue()) {
-                    ioe.printStackTrace(System.err);
+
+                try {
+                    switch (lockMode) {
+                        case LOCK_UN:
+                        case LOCK_UN | LOCK_NB:
+                            if (currentLock != null) {
+                                currentLock.release();
+                                currentLock = null;
+
+                                return RubyFixnum.zero(context.getRuntime());
+                            }
+                            break;
+                        case LOCK_EX:
+                            if (currentLock != null) {
+                                currentLock.release();
+                                currentLock = null;
+                            }
+                            currentLock = fileChannel.lock();
+                            if (currentLock != null) {
+                                return RubyFixnum.zero(context.getRuntime());
+                            }
+
+                            break;
+                        case LOCK_EX | LOCK_NB:
+                            if (currentLock != null) {
+                                currentLock.release();
+                                currentLock = null;
+                            }
+                            currentLock = fileChannel.tryLock();
+                            if (currentLock != null) {
+                                return RubyFixnum.zero(context.getRuntime());
+                            }
+
+                            break;
+                        case LOCK_SH:
+                            if (currentLock != null) {
+                                currentLock.release();
+                                currentLock = null;
+                            }
+
+                            currentLock = fileChannel.lock(0L, Long.MAX_VALUE, true);
+                            if (currentLock != null) {
+                                return RubyFixnum.zero(context.getRuntime());
+                            }
+
+                            break;
+                        case LOCK_SH | LOCK_NB:
+                            if (currentLock != null) {
+                                currentLock.release();
+                                currentLock = null;
+                            }
+
+                            currentLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true);
+                            if (currentLock != null) {
+                                return RubyFixnum.zero(context.getRuntime());
+                            }
+
+                            break;
+                        default:
+                    }
+                } catch (IOException ioe) {
+                    if (context.getRuntime().getDebug().isTrue()) {
+                        ioe.printStackTrace(System.err);
+                    }
+                } catch (java.nio.channels.OverlappingFileLockException ioe) {
+                    if (context.getRuntime().getDebug().isTrue()) {
+                        ioe.printStackTrace(System.err);
+                    }
                 }
+                return (lockMode & LOCK_EX) == 0 ? RubyFixnum.zero(context.getRuntime()) : context.getRuntime().getFalse();
+            } else {
+                // We're not actually a real file, so we can't flock
+                return context.getRuntime().getFalse();
             }
-            return (lockMode & LOCK_EX) == 0 ? RubyFixnum.zero(context.getRuntime()) : context.getRuntime().getFalse();
-        } else {
-            // We're not actually a real file, so we can't flock
-            return context.getRuntime().getFalse();
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
         }
     }
 
     @JRubyMethod(required = 1, optional = 2, visibility = PRIVATE, compat = RUBY1_8)
     @Override
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (openFile == null) {
             throw getRuntime().newRuntimeError("reinitializing File");
         }
         
         if (args.length > 0 && args.length < 3) {
             IRubyObject fd = TypeConverter.convertToTypeWithCheck(args[0], getRuntime().getFixnum(), "to_int");
             if (!fd.isNil()) {
                 args[0] = fd;
                 return super.initialize(args, block);
             }
         }
 
         return openFile(args);
     }
 
     @JRubyMethod(name = "initialize", required = 1, optional = 2, visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject[] args, Block block) {
         if (openFile == null) {
             throw context.getRuntime().newRuntimeError("reinitializing File");
         }
 
         if (args.length > 0 && args.length <= 3) {
             IRubyObject fd = TypeConverter.convertToTypeWithCheck(args[0], context.getRuntime().getFixnum(), "to_int");
             if (!fd.isNil()) {
                 args[0] = fd;
                 if (args.length == 1) {
                     return super.initialize19(context, args[0], block);
                 } else if (args.length == 2) {
                     return super.initialize19(context, args[0], args[1], block);
                 }
                 return super.initialize19(context, args[0], args[1], args[2], block);
             }
         }
 
         return openFile19(context, args);
     }
 
     private IRubyObject openFile19(ThreadContext context, IRubyObject args[]) {
         Ruby runtime = context.getRuntime();
         RubyString filename = get_path(context, args[0]);
         runtime.checkSafeString(filename);
 
         path = adjustRootPathOnWindows(runtime, filename.getUnicodeValue(), runtime.getCurrentDirectory());
 
         String modeString = "r";
         ModeFlags modes = new ModeFlags();
         int perm = 0;
 
         try {
             if (args.length > 1) {
                 modes = parseModes19(context, args[1]);
                 if (args[1] instanceof RubyFixnum) {
                     perm = getFilePermissions(args);
                 } else {
                     modeString = args[1].convertToString().toString();
                 }
             } else {
                 modes = parseModes19(context, RubyString.newString(runtime, modeString));
             }
             if (args.length > 2 && !args[2].isNil()) {
                 if (args[2] instanceof RubyHash) {
                     modes = parseOptions(context, args[2], modes);
                 } else {
                     perm = getFilePermissions(args);
                 }
             }
             if (perm > 0) {
                 sysopenInternal(path, modes, perm);
             } else {
                 openInternal(path, modeString, modes);
             }
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         }
 
         return this;
     }
     
     private IRubyObject openFile(IRubyObject args[]) {
         Ruby runtime = getRuntime();
         RubyString filename = get_path(runtime.getCurrentContext(), args[0]);
         runtime.checkSafeString(filename);
         
         path = adjustRootPathOnWindows(runtime, filename.getUnicodeValue(), runtime.getCurrentDirectory());
         
         String modeString;
         ModeFlags modes;
         int perm;
         
         try {
             if ((args.length > 1 && args[1] instanceof RubyFixnum) || (args.length > 2 && !args[2].isNil())) {
                 modes = parseModes(args[1]);
                 perm = getFilePermissions(args);
 
                 sysopenInternal(path, modes, perm);
             } else {
                 modeString = "r";
                 if (args.length > 1 && !args[1].isNil()) {
                     modeString = args[1].convertToString().toString();
                 }
                 
                 openInternal(path, modeString);
             }
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } finally {}
         
         return this;
     }
 
     private int getFilePermissions(IRubyObject[] args) {
         return (args.length > 2 && !args[2].isNil()) ? RubyNumeric.num2int(args[2]) : 438;
     }
 
     protected void sysopenInternal(String path, ModeFlags modes, int perm) throws InvalidValueException {
         openFile = new OpenFile();
         
         openFile.setPath(path);
         openFile.setMode(modes.getOpenFileFlags());
 
         int umask = getUmaskSafe( getRuntime() );
         perm = perm - (perm & umask);
         
         ChannelDescriptor descriptor = sysopen(path, modes, perm);
         openFile.setMainStream(fdopen(descriptor, modes));
     }
 
     protected void openInternal(String path, String modeString, ModeFlags modes) throws InvalidValueException {
         openFile = new OpenFile();
 
         openFile.setMode(modes.getOpenFileFlags());
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modeString));
     }
     
     protected void openInternal(String path, String modeString) throws InvalidValueException {
         openFile = new OpenFile();
 
         openFile.setMode(getIOModes(getRuntime(), modeString).getOpenFileFlags());
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modeString));
     }
     
     private ChannelDescriptor sysopen(String path, ModeFlags modes, int perm) throws InvalidValueException {
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.open(
                     getRuntime().getCurrentDirectory(),
                     path,
                     modes,
                     perm,
                     getRuntime().getPosix(),
                     getRuntime().getJRubyClassLoader());
 
             // TODO: check if too many open files, GC and try again
 
             return descriptor;
         } catch (PermissionDeniedException pde) {
             // PDException can be thrown only when creating the file and
             // permission is denied.  See JavaDoc of PermissionDeniedException.
             throw getRuntime().newErrnoEACCESError(path);
         } catch (FileNotFoundException fnfe) {
             // FNFException can be thrown in both cases, when the file
             // is not found, or when permission is denied.
             if (Ruby.isSecurityRestricted() || new File(path).exists()) {
                 throw getRuntime().newErrnoEACCESError(path);
             }
             throw getRuntime().newErrnoENOENTError(path);
         } catch (DirectoryAsFileException dafe) {
             throw getRuntime().newErrnoEISDirError();
         } catch (FileExistsException fee) {
             throw getRuntime().newErrnoEEXISTError(path);
         } catch (IOException ioe) {
             throw getRuntime().newIOErrorFromException(ioe);
         }
     }
     
     private Stream fopen(String path, String modeString) {
         try {
             Stream stream = ChannelStream.fopen(
                     getRuntime(),
                     path,
                     getIOModes(getRuntime(), modeString));
             
             if (stream == null) {
                 // TODO
     //            if (errno == EMFILE || errno == ENFILE) {
     //                rb_gc();
     //                file = fopen(fname, mode);
     //            }
     //            if (!file) {
     //                rb_sys_fail(fname);
     //            }
             }
 
             // Do we need to be in SETVBUF mode for buffering to make sense? This comes up elsewhere.
     //    #ifdef USE_SETVBUF
     //        if (setvbuf(file, NULL, _IOFBF, 0) != 0)
     //            rb_warn("setvbuf() can't be honoured for %s", fname);
     //    #endif
     //    #ifdef __human68k__
     //        fmode(file, _IOTEXT);
     //    #endif
             return stream;
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (PermissionDeniedException pde) {
             // PDException can be thrown only when creating the file and
             // permission is denied.  See JavaDoc of PermissionDeniedException.
             throw getRuntime().newErrnoEACCESError(path);
         } catch (FileNotFoundException ex) {
             // FNFException can be thrown in both cases, when the file
             // is not found, or when permission is denied.
             if (Ruby.isSecurityRestricted() || new File(path).exists()) {
                 throw getRuntime().newErrnoEACCESError(path);
             }
             throw getRuntime().newErrnoENOENTError(path);
         } catch (DirectoryAsFileException ex) {
             throw getRuntime().newErrnoEISDirError();
         } catch (FileExistsException ex) {
             throw getRuntime().newErrnoEEXISTError(path);
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (SecurityException ex) {
             throw getRuntime().newErrnoEACCESError(path);
         }
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject chmod(ThreadContext context, IRubyObject arg) {
         checkClosed(context);
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw context.getRuntime().newErrnoENOENTError(path);
         }
 
         return context.getRuntime().newFixnum(context.getRuntime().getPosix().chmod(path, mode));
     }
 
     @JRubyMethod(required = 2)
     public IRubyObject chown(ThreadContext context, IRubyObject arg1, IRubyObject arg2) {
         checkClosed(context);
         int owner = -1;
         if (!arg1.isNil()) {
             owner = RubyNumeric.num2int(arg1);
         }
 
         int group = -1;
         if (!arg2.isNil()) {
             group = RubyNumeric.num2int(arg2);
         }
 
         if (!new File(path).exists()) {
             throw context.getRuntime().newErrnoENOENTError(path);
         }
 
         return context.getRuntime().newFixnum(context.getRuntime().getPosix().chown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject atime(ThreadContext context) {
         checkClosed(context);
         return context.getRuntime().newFileStat(path, false).atime();
     }
 
     @JRubyMethod
     public IRubyObject ctime(ThreadContext context) {
         checkClosed(context);
         return context.getRuntime().newFileStat(path, false).ctime();
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject lchmod(ThreadContext context, IRubyObject arg) {
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw context.getRuntime().newErrnoENOENTError(path);
         }
 
         return context.getRuntime().newFixnum(context.getRuntime().getPosix().lchmod(path, mode));
     }
 
     // TODO: this method is not present in MRI!
     @JRubyMethod(required = 2)
     public IRubyObject lchown(ThreadContext context, IRubyObject arg1, IRubyObject arg2) {
         int owner = -1;
         if (!arg1.isNil()) {
             owner = RubyNumeric.num2int(arg1);
         }
 
         int group = -1;
         if (!arg2.isNil()) {
             group = RubyNumeric.num2int(arg2);
         }
 
         if (!new File(path).exists()) {
             throw context.getRuntime().newErrnoENOENTError(path);
         }
 
         return context.getRuntime().newFixnum(context.getRuntime().getPosix().lchown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject lstat(ThreadContext context) {
         checkClosed(context);
         return context.getRuntime().newFileStat(path, true);
     }
     
     @JRubyMethod
     public IRubyObject mtime(ThreadContext context) {
         checkClosed(context);
         return getLastModified(context.getRuntime(), path);
     }
 
     @JRubyMethod(meta = true, compat = RUBY1_9)
     public static IRubyObject path(ThreadContext context, IRubyObject self, IRubyObject str) {
         return get_path(context, str);
     }
 
     /**
      * similar in spirit to rb_get_path from 1.9 source
      * @param context
      * @param obj
      * @return
      */
     public static RubyString get_path(ThreadContext context, IRubyObject obj) {
         if (context.getRuntime().is1_9()) {
             if (obj instanceof RubyString) {
                 return (RubyString)obj;
             }
             
             if (obj.respondsTo("to_path")) {
                 obj = obj.callMethod(context, "to_path");
             }
         }
 
         return obj.convertToString();
     }
 
     /**
      * Get the fully-qualified JRubyFile object for the path, taking into
      * account the runtime's current directory.
      */
     public static JRubyFile file(IRubyObject pathOrFile) {
         Ruby runtime = pathOrFile.getRuntime();
 
         if (pathOrFile instanceof RubyFile) {
             return JRubyFile.create(runtime.getCurrentDirectory(), ((RubyFile) pathOrFile).getPath());
         } else {
             RubyString pathStr = get_path(runtime.getCurrentContext(), pathOrFile);
             String path = pathStr.getUnicodeValue();
             String[] pathParts = splitURI(path);
             if (pathParts != null && pathParts[0].equals("file:")) {
                 path = pathParts[1];
             }
             return JRubyFile.create(runtime.getCurrentDirectory(), path);
         }
     }
 
     @JRubyMethod(name = {"path", "to_path"})
     public IRubyObject path(ThreadContext context) {
         IRubyObject newPath = context.getRuntime().getNil();
         if (path != null) {
             newPath = context.getRuntime().newString(path);
             newPath.setTaint(true);
         }
         return newPath;
     }
 
     @JRubyMethod
     @Override
     public IRubyObject stat(ThreadContext context) {
         checkClosed(context);
         return context.getRuntime().newFileStat(path, false);
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject truncate(ThreadContext context, IRubyObject arg) {
         RubyInteger newLength = arg.convertToInteger();
         if (newLength.getLongValue() < 0) {
             throw context.getRuntime().newErrnoEINVALError(path);
         }
         try {
             openFile.checkWritable(context.getRuntime());
-            openFile.getMainStream().ftruncate(newLength.getLongValue());
+            openFile.getMainStreamSafe().ftruncate(newLength.getLongValue());
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (IOException e) {
             // Should we do anything?
         }
 
         return RubyFixnum.zero(context.getRuntime());
     }
 
     @Override
     public String toString() {
-        return "RubyFile(" + path + ", " + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStream().getDescriptor()) + ")";
+        try {
+            return "RubyFile(" + path + ", " + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStreamSafe().getDescriptor()) + ")";
+        } catch (BadDescriptorException e) {
+            throw getRuntime().newErrnoEBADFError();
+        }
     }
 
     // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
     private static ModeFlags getModes(Ruby runtime, IRubyObject object) throws InvalidValueException {
         if (object instanceof RubyString) {
             return getIOModes(runtime, ((RubyString) object).toString());
         } else if (object instanceof RubyFixnum) {
             return new ModeFlags(((RubyFixnum) object).getLongValue());
         }
 
         throw runtime.newTypeError("Invalid type for modes");
     }
 
     @JRubyMethod
     @Override
     public IRubyObject inspect() {
         StringBuilder val = new StringBuilder();
         val.append("#<File:").append(path);
         if(!openFile.isOpen()) {
             val.append(" (closed)");
         }
         val.append(">");
         return getRuntime().newString(val.toString());
     }
     
     /* File class methods */
     
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject basename(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         String name = get_path(context,args[0]).getUnicodeValue();
 
         // MRI-compatible basename handling for windows drive letter paths
         if (Platform.IS_WINDOWS) {
             if (name.length() > 1 && name.charAt(1) == ':' && Character.isLetter(name.charAt(0))) {
                 switch (name.length()) {
                 case 2:
                     return RubyString.newEmptyString(context.getRuntime()).infectBy(args[0]);
                 case 3:
                     return context.getRuntime().newString(name.substring(2)).infectBy(args[0]);
                 default:
                     switch (name.charAt(2)) {
                     case '/':
                     case '\\':
                         break;
                     default:
                         // strip c: away from relative-pathed name
                         name = name.substring(2);
                         break;
                     }
                     break;
                 }
             }
         }
 
         while (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
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
         return context.getRuntime().newString(name).infectBy(args[0]);
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject chmod(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             JRubyFile filename = file(args[i]);
             
             if (!filename.exists()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
             
             boolean result = 0 == runtime.getPosix().chmod(filename.getAbsolutePath(), (int)mode.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 3, rest = true, meta = true)
     public static IRubyObject chown(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         int count = 0;
         int owner = -1;
         if (!args[0].isNil()) {
             owner = RubyNumeric.num2int(args[0]);
         }
 
         int group = -1;
         if (!args[1].isNil()) {
             group = RubyNumeric.num2int(args[1]);
         }
         for (int i = 2; i < args.length; i++) {
             JRubyFile filename = file(args[i]);
 
             if (!filename.exists()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
             
             boolean result = 0 == runtime.getPosix().chown(filename.getAbsolutePath(), owner, group);
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject dirname(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyString filename = get_path(context, arg);
         
         String jfilename = filename.getUnicodeValue();
         
         String name = jfilename.replace('\\', '/');
         int minPathLength = 1;
         boolean trimmedSlashes = false;
 
         boolean startsWithDriveLetterOnWindows = startsWithDriveLetterOnWindows(name);
 
         if (startsWithDriveLetterOnWindows) {
             minPathLength = 3;
         }
 
         while (name.length() > minPathLength && name.charAt(name.length() - 1) == '/') {
             trimmedSlashes = true;
             name = name.substring(0, name.length() - 1);
         }
 
         String result;
         if (startsWithDriveLetterOnWindows && name.length() == 2) {
             if (trimmedSlashes) {
                 // C:\ is returned unchanged
                 result = jfilename.substring(0, 3);
             } else {
                 result = jfilename.substring(0, 2) + '.';
             }
         } else {
             //TODO deal with UNC names
             int index = name.lastIndexOf('/');
             if (index == -1) {
                 if (startsWithDriveLetterOnWindows) {
                     return context.getRuntime().newString(jfilename.substring(0, 2) + ".");
                 } else {
                     return context.getRuntime().newString(".");
                 }
             }
             if (index == 0) return context.getRuntime().newString("/");
 
             if (startsWithDriveLetterOnWindows && index == 2) {
                 // Include additional path separator
                 // (so that dirname of "C:\file.txt" is  "C:\", not "C:")
                 index++;
             }
 
             result = jfilename.substring(0, index);
         }
 
         char endChar;
         // trim trailing slashes
         while (result.length() > minPathLength) {
             endChar = result.charAt(result.length() - 1);
             if (endChar == '/' || endChar == '\\') {
                 result = result.substring(0, result.length() - 1);
             } else {
                 break;
             }
         }
 
         return context.getRuntime().newString(result).infectBy(filename);
     }
 
     private static boolean isWindowsDriveLetter(char c) {
            return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
     }
 
 
     /**
      * Returns the extension name of the file. An empty string is returned if 
      * the filename (not the entire path) starts or ends with a dot.
      * @param recv
      * @param arg Path to get extension name of
      * @return Extension, including the dot, or an empty string
      */
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject extname(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         IRubyObject baseFilename = basename(context, recv, new IRubyObject[]{arg});
         
         String filename = RubyString.stringValue(baseFilename).getUnicodeValue();
         String result = "";
 
         int dotIndex = filename.lastIndexOf(".");
         if (dotIndex > 0 && dotIndex != (filename.length() - 1)) {
             // Dot is not at beginning and not at end of filename. 
             result = filename.substring(dotIndex);
         }
 
         return context.getRuntime().newString(result);
     }
 
     /**
      * Converts a pathname to an absolute pathname. Relative paths are 
      * referenced from the current working directory of the process unless 
      * a second argument is given, in which case it will be used as the 
      * starting point. If the second argument is also relative, it will 
      * first be converted to an absolute pathname.
      * @param recv
      * @param args 
      * @return Resulting absolute path as a String
      */
     @JRubyMethod(required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject expand_path(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, true);
     }
 
     @JRubyMethod(name = "expand_path", required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject expand_path19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         RubyString path = (RubyString) expandPathInternal(context, recv, args, true);
         path.force_encoding(context, context.getRuntime().getEncodingService().getDefaultExternal());
 
         return path;
     }
 
 
     /**
      * ---------------------------------------------------- File::absolute_path
      *      File.absolute_path(file_name [, dir_string] ) -> abs_file_name
      *
      *      From Ruby 1.9.1
      * ------------------------------------------------------------------------
      *      Converts a pathname to an absolute pathname. Relative paths are
      *      referenced from the current working directory of the process unless
      *      _dir_string_ is given, in which case it will be used as the
      *      starting point. If the given pathname starts with a ``+~+'' it is
      *      NOT expanded, it is treated as a normal directory name.
      *
      *         File.absolute_path("~oracle/bin")       #=> "<relative_path>/~oracle/bin"
      *
      * @param context
      * @param recv
      * @param args
      * @return
      */
     @JRubyMethod(required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject absolute_path(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, false);
     }
 
     @JRubyMethod(name = {"realdirpath"}, required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject realdirpath(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, false);
     }
 
     @JRubyMethod(name = {"realpath"}, required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject realpath(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject file = expandPathInternal(context, recv, args, false);
         if (!RubyFileTest.exist_p(recv, file).isTrue()) {
             throw context.getRuntime().newErrnoENOENTError(file.toString());
         }
         return file;
     }
 
     private static IRubyObject expandPathInternal(ThreadContext context, IRubyObject recv, IRubyObject[] args, boolean expandUser) {
         Ruby runtime = context.getRuntime();
 
         String relativePath = get_path(context, args[0]).getUnicodeValue();
         String[] uriParts = splitURI(relativePath);
         String cwd = null;
 
         // Handle ~user paths 
         if (expandUser) {
             relativePath = expandUserPath(context, relativePath);
         }
 
         if (uriParts != null) {
             relativePath = uriParts[1];
         }
 
         // If there's a second argument, it's the path to which the first
         // argument is relative.
         if (args.length == 2 && !args[1].isNil()) {
             cwd = get_path(context, args[1]).getUnicodeValue();
 
             // Handle ~user paths.
             if (expandUser) {
                 cwd = expandUserPath(context, cwd);
             }
 
             String[] cwdURIParts = splitURI(cwd);
             if (uriParts == null && cwdURIParts != null) {
                 uriParts = cwdURIParts;
                 cwd = cwdURIParts[1];
             }
 
             cwd = adjustRootPathOnWindows(runtime, cwd, null);
 
             boolean startsWithSlashNotOnWindows = (cwd != null)
                     && !Platform.IS_WINDOWS && cwd.length() > 0
                     && cwd.charAt(0) == '/';
 
             // TODO: better detection when path is absolute or not.
             // If the path isn't absolute, then prepend the current working
             // directory to the path.
             if (!startsWithSlashNotOnWindows && !startsWithDriveLetterOnWindows(cwd)) {
                 cwd = new File(runtime.getCurrentDirectory(), cwd).getAbsolutePath();
             }
 
         } else {
             // If there's no second argument, simply use the working directory
             // of the runtime.
             cwd = runtime.getCurrentDirectory();
         }
         
         // Something wrong we don't know the cwd...
         // TODO: Is this behavior really desirable? /mov
         if (cwd == null) return runtime.getNil();
         
         /* The counting of slashes that follows is simply a way to adhere to 
          * Ruby's UNC (or something) compatibility. When Ruby's expand_path is 
          * called with "//foo//bar" it will return "//foo/bar". JRuby uses 
          * java.io.File, and hence returns "/foo/bar". In order to retain 
          * java.io.File in the lower layers and provide full Ruby 
          * compatibility, the number of extra slashes must be counted and 
          * prepended to the result.
          */ 
 
         // TODO: special handling on windows for some corner cases
 //        if (IS_WINDOWS) {
 //            if (relativePath.startsWith("//")) {
 //                if (relativePath.length() > 2 && relativePath.charAt(2) != '/') {
 //                    int nextSlash = relativePath.indexOf('/', 3);
 //                    if (nextSlash != -1) {
 //                        return runtime.newString(
 //                                relativePath.substring(0, nextSlash)
 //                                + canonicalize(relativePath.substring(nextSlash)));
 //                    } else {
 //                        return runtime.newString(relativePath);
 //                    }
 //                }
 //            }
 //        }
 
         // Find out which string to check.
         String padSlashes = "";
         if (uriParts != null) {
             padSlashes = uriParts[0];
         } else if (!Platform.IS_WINDOWS) {
             if (relativePath.length() > 0 && relativePath.charAt(0) == '/') {
                 padSlashes = countSlashes(relativePath);
             } else if (cwd.length() > 0 && cwd.charAt(0) == '/') {
                 padSlashes = countSlashes(cwd);
             }
         }
         
         JRubyFile path;
         
         if (relativePath.length() == 0) {
             path = JRubyFile.create(relativePath, cwd);
         } else {
             relativePath = adjustRootPathOnWindows(runtime, relativePath, cwd);
             path = JRubyFile.create(cwd, relativePath);
         }
 
         return runtime.newString(padSlashes + canonicalize(path.getAbsolutePath()));
     }
 
     private static Pattern URI_PREFIX = Pattern.compile("^[a-z]{2,}:(.*)");
     public static String[] splitURI(String path) {
         Matcher m = URI_PREFIX.matcher(path);
         if (m.find()) {
             if (m.group(1).length() == 0) {
                 return new String[] {path, ""};
             }
             try {
                 URI u = new URI(path);
                 String pathPart = u.getPath();
                 return new String[] {path.substring(0, path.indexOf(pathPart)), pathPart};
             } catch (Exception e) {
                 try {
                     URL u = new URL(path);
                     String pathPart = u.getPath();
                     return new String[] {path.substring(0, path.indexOf(pathPart)), pathPart};
                 } catch (Exception e2) {
                 }
             }
         }
         return null;
     }
 
     /**
      * This method checks a path, and if it starts with ~, then it expands 
      * the path to the absolute path of the user's home directory. If the 
      * string does not begin with ~, then the string is simply returned.
      * unaltered.
      * @param recv
      * @param path Path to check
      * @return Expanded path
      */
     public static String expandUserPath(ThreadContext context, String path) {
         
         int pathLength = path.length();
 
         if (pathLength >= 1 && path.charAt(0) == '~') {
             // Enebo : Should ~frogger\\foo work (it doesnt in linux ruby)?
             int userEnd = path.indexOf('/');
             
             if (userEnd == -1) {
                 if (pathLength == 1) {
                     // Single '~' as whole path to expand
                     path = RubyDir.getHomeDirectoryPath(context).toString();
                 } else {
                     // No directory delimeter.  Rest of string is username
                     userEnd = pathLength;
                 }
             }
             
             if (userEnd == 1) {
                 // '~/...' as path to expand
                 path = RubyDir.getHomeDirectoryPath(context).toString() +
                         path.substring(1);
             } else if (userEnd > 1){
                 // '~user/...' as path to expand
                 String user = path.substring(1, userEnd);
                 IRubyObject dir = RubyDir.getHomeDirectoryPath(context, user);
                 
                 if (dir.isNil()) {
                     throw context.getRuntime().newArgumentError("user " + user + " does not exist");
                 }
                 
                 path = "" + dir + (pathLength == userEnd ? "" : path.substring(userEnd));
             }
         }
         return path;
     }
 
     private static final String[] SLASHES = {"", "/", "//"};
     /**
      * Returns a string consisting of <code>n-1</code> slashes, where 
      * <code>n</code> is the number of slashes at the beginning of the input 
      * string.
      * @param stringToCheck
      * @return
      */
     private static String countSlashes( String stringToCheck ) {
         // Count number of extra slashes in the beginning of the string.
         int slashCount = 0;
         for (int i = 0; i < stringToCheck.length(); i++) {
             if (stringToCheck.charAt(i) == '/') {
                 slashCount++;
             } else {
                 break;
             }
         }
 
         // If there are N slashes, then we want N-1.
         if (slashCount > 0) {
             slashCount--;
         }
 
         if (slashCount < SLASHES.length) {
             return SLASHES[slashCount];
         }
         
         // Prepare a string with the same number of redundant slashes so that 
         // we easily can prepend it to the result.
         char[] slashes = new char[slashCount];
         for (int i = 0; i < slashCount; i++) {
             slashes[i] = '/';
         }
         return new String(slashes);
     }
 
     public static String canonicalize(String path) {
         return canonicalize(null, path);
     }
 
     private static String canonicalize(String canonicalPath, String remaining) {
         if (remaining == null) {
             if ("".equals(canonicalPath)) {
                 return "/";
             } else {
                 // compensate for missing slash after drive letter on windows
                 if (startsWithDriveLetterOnWindows(canonicalPath)
                         && canonicalPath.length() == 2) {
                     canonicalPath += "/";
                 }
             }
             return canonicalPath;
         }
 
         String child;
         int slash = remaining.indexOf('/');
         if (slash == -1) {
             child = remaining;
             remaining = null;
         } else {
             child = remaining.substring(0, slash);
             remaining = remaining.substring(slash + 1);
         }
 
         if (child.equals(".")) {
             // no canonical path yet or length is zero, and we have a / followed by a dot...
             if (slash == -1) {
                 // we don't have another slash after this, so replace /. with /
                 if (canonicalPath != null && canonicalPath.length() == 0 && slash == -1) canonicalPath += "/";
             } else {
                 // we do have another slash; omit both / and . (JRUBY-1606)
             }
         } else if (child.equals("..")) {
             if (canonicalPath == null) throw new IllegalArgumentException("Cannot have .. at the start of an absolute path");
             int lastDir = canonicalPath.lastIndexOf('/');
             if (lastDir == -1) {
                 if (startsWithDriveLetterOnWindows(canonicalPath)) {
                    // do nothing, we should not delete the drive letter
                 } else {
                     canonicalPath = "";
                 }
             } else {
                 canonicalPath = canonicalPath.substring(0, lastDir);
             }
         } else if (canonicalPath == null) {
             canonicalPath = child;
         } else {
             canonicalPath += "/" + child;
         }
 
         return canonicalize(canonicalPath, remaining);
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
     @JRubyMethod(name = {"fnmatch", "fnmatch?"}, required = 2, optional = 1, meta = true)
     public static IRubyObject fnmatch(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         int flags = args.length == 3 ? RubyNumeric.num2int(args[2]) : 0;
 
         ByteList pattern = args[0].convertToString().getByteList();
         ByteList path = get_path(context, args[1]).getByteList();
 
         if (org.jruby.util.Dir.fnmatch(pattern.getUnsafeBytes(), pattern.getBegin(), pattern.getBegin()+pattern.getRealSize(), path.getUnsafeBytes(), path.getBegin(), path.getBegin()+path.getRealSize(), flags) == 0) {
             return context.getRuntime().getTrue();
         }
         return context.getRuntime().getFalse();
     }
     
     @JRubyMethod(name = "ftype", required = 1, meta = true)
     public static IRubyObject ftype(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         return context.getRuntime().newFileStat(get_path(context, filename).getUnicodeValue(), true).ftype();
     }
 
     private static String inspectJoin(ThreadContext context, IRubyObject recv, RubyArray parent, RubyArray array) {
         Ruby runtime = context.getRuntime();
         
         // If already inspecting, there is no need to register/unregister again.
         if (runtime.isInspecting(parent)) return join(context, recv, array).toString();
 
         try {
             runtime.registerInspecting(parent);
             return join(context, recv, array).toString();
         } finally {
             runtime.unregisterInspecting(parent);
         }
     }
 
     private static RubyString join(ThreadContext context, IRubyObject recv, RubyArray ary) {
         IRubyObject[] args = ary.toJavaArray();
         boolean isTainted = false;
         StringBuilder buffer = new StringBuilder();
         Ruby runtime = context.getRuntime();
         
         for (int i = 0; i < args.length; i++) {
             if (args[i].isTaint()) {
                 isTainted = true;
             }
             String element;
             if (args[i] instanceof RubyString) {
                 element = args[i].convertToString().getUnicodeValue();
             } else if (args[i] instanceof RubyArray) {
                 if (runtime.isInspecting(args[i])) {
                     throw runtime.newArgumentError("recursive array");
                 } else {
                     element = inspectJoin(context, recv, ary, ((RubyArray)args[i]));
                 }
             } else {
                 RubyString path = get_path(context, args[i]);
                 element = path.getUnicodeValue();
             }
             
             chomp(buffer);
             if (i > 0 && !element.startsWith("/") && !element.startsWith("\\")) {
                 buffer.append("/");
             }
             buffer.append(element);
         }
         
         RubyString fixedStr = RubyString.newString(runtime, buffer.toString());
         fixedStr.setTaint(isTainted);
         return fixedStr;
     }
     
     /*
      * Fixme:  This does not have exact same semantics as RubyArray.join, but they
      * probably could be consolidated (perhaps as join(args[], sep, doChomp)).
      */
     @JRubyMethod(rest = true, meta = true)
     public static RubyString join(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return join(context, recv, RubyArray.newArrayNoCopyLight(context.getRuntime(), args));
     }
 
     private static void chomp(StringBuilder buffer) {
         int lastIndex = buffer.length() - 1;
         
         while (lastIndex >= 0 && (buffer.lastIndexOf("/") == lastIndex || buffer.lastIndexOf("\\") == lastIndex)) {
             buffer.setLength(lastIndex);
             lastIndex--;
         }
     }
     
     @JRubyMethod(name = "lstat", required = 1, meta = true)
     public static IRubyObject lstat(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.getRuntime().newFileStat(f, true);
     }
 
     @JRubyMethod(name = "stat", required = 1, meta = true)
     public static IRubyObject stat(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.getRuntime().newFileStat(f, false);
     }
 
     @JRubyMethod(name = "atime", required = 1, meta = true)
     public static IRubyObject atime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.getRuntime().newFileStat(f, false).atime();
     }
 
     @JRubyMethod(name = "ctime", required = 1, meta = true)
     public static IRubyObject ctime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.getRuntime().newFileStat(f, false).ctime();
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject lchmod(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             RubyString filename = get_path(context, args[i]);
             
             if (!RubyFileTest.exist_p(filename, filename).isTrue()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
             
             boolean result = 0 == runtime.getPosix().lchmod(filename.getUnicodeValue(), (int)mode.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject lchown(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int owner = !args[0].isNil() ? RubyNumeric.num2int(args[0]) : -1;
         int group = !args[1].isNil() ? RubyNumeric.num2int(args[1]) : -1;
         int count = 0;
 
         for (int i = 2; i < args.length; i++) {
             IRubyObject filename = args[i];
 
             if (0 != runtime.getPosix().lchown(filename.toString(), owner, group)) {
                 throw runtime.newErrnoFromLastPOSIXErrno();
             } else {
                 count++;
             }
         }
 
         return runtime.newFixnum(count);
     }
 
     @JRubyMethod(required = 2, meta = true, backtrace = true)
     public static IRubyObject link(ThreadContext context, IRubyObject recv, IRubyObject from, IRubyObject to) {
         Ruby runtime = context.getRuntime();
         RubyString fromStr = RubyString.stringValue(from);
         RubyString toStr = RubyString.stringValue(to);
 
         int ret = runtime.getPosix().link(fromStr.getUnicodeValue(), toStr.getUnicodeValue());
         if (ret != 0) {
             // In most cases, when there is an error during the call,
             // the POSIX handler throws an exception, but not in case
             // with pure Java POSIX layer (when native support is disabled),
             // so we deal with it like this:
             throw runtime.newErrnoEEXISTError(fromStr + " or " + toStr);
         }
         return runtime.newFixnum(ret);
     }
 
     @JRubyMethod(name = "mtime", required = 1, meta = true)
     public static IRubyObject mtime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         return getLastModified(context.getRuntime(), get_path(context, filename).getUnicodeValue());
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject rename(ThreadContext context, IRubyObject recv, IRubyObject oldName, IRubyObject newName) {
         Ruby runtime = context.getRuntime();
         RubyString oldNameString = RubyString.stringValue(oldName);
         RubyString newNameString = RubyString.stringValue(newName);
         runtime.checkSafeString(oldNameString);
         runtime.checkSafeString(newNameString);
 
         String newNameJavaString = newNameString.getUnicodeValue();
         String oldNameJavaString = oldNameString.getUnicodeValue();
         JRubyFile oldFile = JRubyFile.create(runtime.getCurrentDirectory(), oldNameJavaString);
         JRubyFile newFile = JRubyFile.create(runtime.getCurrentDirectory(), newNameJavaString);
         
         if (!oldFile.exists() || !newFile.getParentFile().exists()) {
             throw runtime.newErrnoENOENTError(oldNameJavaString + " or " + newNameJavaString);
         }
 
         JRubyFile dest = JRubyFile.create(runtime.getCurrentDirectory(), newNameJavaString);
 
         if (oldFile.renameTo(dest)) {  // rename is successful
             return RubyFixnum.zero(runtime);
         }
 
         // rename via Java API call wasn't successful, let's try some tricks, similar to MRI 
 
         if (newFile.exists()) {
             runtime.getPosix().chmod(newNameJavaString, 0666);
             newFile.delete();
         }
 
         if (oldFile.renameTo(dest)) { // try to rename one more time
             return RubyFixnum.zero(runtime);
         }
 
         throw runtime.newErrnoEACCESError(oldNameJavaString + " or " + newNameJavaString);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static RubyArray split(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyString filename = get_path(context, arg);
         
         return context.getRuntime().newArray(dirname(context, recv, filename),
                 basename(context, recv, new IRubyObject[] { filename }));
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject symlink(ThreadContext context, IRubyObject recv, IRubyObject from, IRubyObject to) {
         Ruby runtime = context.getRuntime();
         RubyString fromStr = get_path(context, from);
         RubyString toStr = get_path(context, to);
         String tovalue = toStr.getUnicodeValue();
         tovalue = JRubyFile.create(runtime.getCurrentDirectory(), tovalue).getAbsolutePath();
         try {
             if (runtime.getPosix().symlink(
                     fromStr.getUnicodeValue(), tovalue) == -1) {
                 // FIXME: When we get JNA3 we need to properly write this to errno.
                 throw runtime.newErrnoEEXISTError(fromStr + " or " + toStr);
             }
         } catch (java.lang.UnsatisfiedLinkError ule) {
             throw runtime.newNotImplementedError("symlink() function is unimplemented on this machine");
         }
         
         return runtime.newFixnum(0);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject readlink(ThreadContext context, IRubyObject recv, IRubyObject path) {
         Ruby runtime = context.getRuntime();
         
         try {
             String realPath = runtime.getPosix().readlink(path.convertToString().getUnicodeValue());
         
             if (!RubyFileTest.exist_p(recv, path).isTrue()) {
                 throw runtime.newErrnoENOENTError(path.toString());
             }
         
             if (!RubyFileTest.symlink_p(recv, path).isTrue()) {
                 throw runtime.newErrnoEINVALError(path.toString());
             }
         
             if (realPath == null) {
                 //FIXME: When we get JNA3 we need to properly write this to errno.
             }
 
             return runtime.newString(realPath);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     // Can we produce IOError which bypasses a close?
     @JRubyMethod(required = 2, meta = true, compat = RUBY1_8)
     public static IRubyObject truncate(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {        
         return truncateCommon(context, recv, arg1, arg2);
     }
 
     @JRubyMethod(name = "truncate", required = 2, meta = true, compat = RUBY1_9)
     public static IRubyObject truncate19(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         if (!(arg1 instanceof RubyString) && arg1.respondsTo("to_path")) {
             arg1 = arg1.callMethod(context, "to_path");
         }
         return truncateCommon(context, recv, arg1, arg2);
     }
 
     private static IRubyObject truncateCommon(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         RubyString filename = arg1.convertToString(); // TODO: SafeStringValue here
         Ruby runtime = context.getRuntime();
         RubyInteger newLength = arg2.convertToInteger();
 
         File testFile ;
         File childFile = new File(filename.getUnicodeValue() );
 
         if ( childFile.isAbsolute() ) {
           testFile = childFile ;
         } else {
           testFile = new File(runtime.getCurrentDirectory(), filename.getByteList().toString());
         }
 
         if (!testFile.exists()) {
             throw runtime.newErrnoENOENTError(filename.getByteList().toString());
         }
 
         if (newLength.getLongValue() < 0) {
             throw runtime.newErrnoEINVALError(filename.toString());
         }
 
         IRubyObject[] args = new IRubyObject[] { filename, runtime.newString("r+") };
         RubyFile file = (RubyFile) open(context, recv, args, Block.NULL_BLOCK);
         file.truncate(context, newLength);
         file.close();
 
         return RubyFixnum.zero(runtime);
     }
 
     @JRubyMethod(meta = true, optional = 1)
     public static IRubyObject umask(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int oldMask = 0;
         if (args.length == 0) {
             oldMask = getUmaskSafe( runtime );
         } else if (args.length == 1) {
             int newMask = (int) args[0].convertToInteger().getLongValue();
             synchronized (_umaskLock) {
                 oldMask = runtime.getPosix().umask(newMask);
                 _cachedUmask = newMask;
             }
         } else {
             runtime.newArgumentError("wrong number of arguments");
         }
         
         return runtime.newFixnum(oldMask);
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject utime(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         long[] atimeval = null;
         long[] mtimeval = null;
 
         if (args[0] != runtime.getNil() || args[1] != runtime.getNil()) {
             atimeval = extractTimeval(runtime, args[0]);
             mtimeval = extractTimeval(runtime, args[1]);
         }
 
         for (int i = 2, j = args.length; i < j; i++) {
             RubyString filename = get_path(context, args[i]);
             runtime.checkSafeString(filename);
             
             JRubyFile fileToTouch = JRubyFile.create(runtime.getCurrentDirectory(),filename.getUnicodeValue());
             
             if (!fileToTouch.exists()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
 
             runtime.getPosix().utimes(fileToTouch.getAbsolutePath(), atimeval, mtimeval);
         }
         
         return runtime.newFixnum(args.length - 2);
     }
     
     @JRubyMethod(name = {"unlink", "delete"}, rest = true, meta = true)
     public static IRubyObject unlink(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
          
         for (int i = 0; i < args.length; i++) {
             RubyString filename = get_path(context, args[i]);
             runtime.checkSafeString(filename);
             JRubyFile lToDelete = JRubyFile.create(runtime.getCurrentDirectory(), filename.getUnicodeValue());
             
             boolean isSymlink = RubyFileTest.symlink_p(recv, filename).isTrue();
             // Broken symlinks considered by exists() as non-existing,
             // so we need to check for symlinks explicitly.
             if (!lToDelete.exists() && !isSymlink) {
                 throw runtime.newErrnoENOENTError(filename.getUnicodeValue());
             }
 
             if (lToDelete.isDirectory() && !isSymlink) {
                 throw runtime.newErrnoEPERMError(filename.getUnicodeValue());
             }
 
             if (!lToDelete.delete()) {
                 throw runtime.newErrnoEACCESError(filename.getUnicodeValue());
             }
         }
         
         return runtime.newFixnum(args.length);
     }
 
     @JRubyMethod(name = "size", backtrace = true, compat = RUBY1_9)
     public IRubyObject size(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if ((openFile.getMode() & OpenFile.WRITABLE) != 0) {
             flush();
         }
 
-        FileStat stat = runtime.getPosix().fstat(
-                getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor());
-        if (stat == null) {
-            throw runtime.newErrnoEACCESError(path);
-        }
+        try {
+            FileStat stat = runtime.getPosix().fstat(
+                    getOpenFileChecked().getMainStreamSafe().getDescriptor().getFileDescriptor());
+            if (stat == null) {
+                throw runtime.newErrnoEACCESError(path);
+            }
 
-        return runtime.newFixnum(stat.st_size());
+            return runtime.newFixnum(stat.st_size());
+        } catch (BadDescriptorException e) {
+            throw runtime.newErrnoEBADFError();
+        }
     }
 
     public static ZipEntry getFileEntry(ZipFile zf, String path) throws IOException {
         ZipEntry entry = zf.getEntry(path);
         if (entry == null) {
             // try canonicalizing the path to eliminate . and .. (JRUBY-4760, JRUBY-4879)
             String prefix = new File(".").getCanonicalPath();
             entry = zf.getEntry(new File(path).getCanonicalPath().substring(prefix.length() + 1).replaceAll("\\\\", "/"));
         }
         return entry;
     }
 
     public static ZipEntry getDirOrFileEntry(ZipFile zf, String path) throws IOException {
         ZipEntry entry = zf.getEntry(path + "/"); // first try as directory
         if (entry == null) {
             // try canonicalizing the path to eliminate . and .. (JRUBY-4760, JRUBY-4879)
             String prefix = new File(".").getCanonicalPath();
             entry = zf.getEntry(new File(path + "/").getCanonicalPath().substring(prefix.length() + 1).replaceAll("\\\\", "/"));
 
             if (entry == null) {
                 // try as file
                 entry = getFileEntry(zf, path);
             }
         }
         return entry;
     }
 
     /**
      * Joy of POSIX, only way to get the umask is to set the umask,
      * then set it back. That's unsafe in a threaded program. We
      * minimize but may not totally remove this race by caching the
      * obtained or previously set (see umask() above) umask and using
      * that as the initial set value which, cross fingers, is a
      * no-op. The cache access is then synchronized. TODO: Better?
      */
     private static int getUmaskSafe( Ruby runtime ) {
         synchronized (_umaskLock) {
             final int umask = runtime.getPosix().umask(_cachedUmask);
             if (_cachedUmask != umask ) {
                 runtime.getPosix().umask(umask);
                 _cachedUmask = umask;
             }
             return umask;
         }
     }
 
     /**
      * Extract a timeval (an array of 2 longs: seconds and microseconds from epoch) from
      * an IRubyObject.
      */
     private static long[] extractTimeval(Ruby runtime, IRubyObject value) {
         long[] timeval = new long[2];
 
         if (value instanceof RubyFloat) {
             timeval[0] = Platform.IS_32_BIT ? RubyNumeric.num2int(value) : RubyNumeric.num2long(value);
             double fraction = ((RubyFloat) value).getDoubleValue() % 1.0;
             timeval[1] = (long)(fraction * 1e6 + 0.5);
         } else if (value instanceof RubyNumeric) {
             timeval[0] = Platform.IS_32_BIT ? RubyNumeric.num2int(value) : RubyNumeric.num2long(value);
             timeval[1] = 0;
         } else {
             RubyTime time;
             if (value instanceof RubyTime) {
                 time = ((RubyTime) value);
             } else {
                 time = (RubyTime) TypeConverter.convertToType(value, runtime.getTime(), "to_time", true);
             }
             timeval[0] = Platform.IS_32_BIT ? RubyNumeric.num2int(time.to_i()) : RubyNumeric.num2long(time.to_i());
             timeval[1] = Platform.IS_32_BIT ? RubyNumeric.num2int(time.usec()) : RubyNumeric.num2long(time.usec());
         }
 
         return timeval;
     }
 
     // Fast path since JNA stat is about 10x slower than this
     private static IRubyObject getLastModified(Ruby runtime, String path) {
         JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), path);
         
         if (!file.exists()) {
             throw runtime.newErrnoENOENTError(path);
         }
         
         return runtime.newTime(file.lastModified());
     }
 
     private void checkClosed(ThreadContext context) {
         openFile.checkClosed(context.getRuntime());
     }
 }
diff --git a/src/org/jruby/RubyGlobal.java b/src/org/jruby/RubyGlobal.java
index 2dc125ec91..0af252c2a0 100644
--- a/src/org/jruby/RubyGlobal.java
+++ b/src/org/jruby/RubyGlobal.java
@@ -1,732 +1,737 @@
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
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2008 Joseph LaFata <joe@quibb.org>
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
 
 import org.jruby.util.io.STDIO;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.environment.OSEnvironment;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ReadonlyGlobalVariable;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.KCode;
 import org.jruby.util.RegexpOptions;
+import org.jruby.util.io.BadDescriptorException;
 
 /** This class initializes global variables and constants.
  * 
  * @author jpetersen
  */
 public class RubyGlobal {
     
     /**
      * Obligate string-keyed and string-valued hash, used for ENV.
      * On Windows, the keys are case-insensitive for ENV
      * 
      */
     public static class CaseInsensitiveStringOnlyRubyHash extends StringOnlyRubyHash {
         
         public CaseInsensitiveStringOnlyRubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
             super(runtime, valueMap, defaultValue);
         }
 
         @Override
         public IRubyObject op_aref(ThreadContext context, IRubyObject key) {
             return case_aware_op_aref(context, key, false);
         }
 
         @Override
         public IRubyObject op_aset(ThreadContext context, IRubyObject key, IRubyObject value) {
             return case_aware_op_aset(context, key, value, false);
         }
 
         @JRubyMethod
         @Override
         public IRubyObject to_s(){
             return getRuntime().newString("ENV");
         }
 
     }
 
     /**
      * A Pseudo-hash whose keys and values are required to be Strings.
      * On all platforms, the keys are case-sensitive.
      * Used for ENV_JAVA.
      */
     public static class StringOnlyRubyHash extends RubyHash {
         public StringOnlyRubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
             super(runtime, valueMap, defaultValue);
         }
 
         @Override
         public IRubyObject op_aref(ThreadContext context, IRubyObject key) {
             return case_aware_op_aref(context, key, true);
         }
 
         @Override
         public RubyHash to_hash() {
             Ruby runtime = getRuntime();
             RubyHash hash = RubyHash.newHash(runtime);
             hash.replace(runtime.getCurrentContext(), this);
             return hash;
         }
 
         @Override
         public IRubyObject op_aset(ThreadContext context, IRubyObject key, IRubyObject value) {
             return case_aware_op_aset(context, key, value, true);
         }
 
         @Override
         public IRubyObject op_aset19(ThreadContext context, IRubyObject key, IRubyObject value) {
             return op_aset(context, key, value);
         }
 
         protected IRubyObject case_aware_op_aref(ThreadContext context, IRubyObject key, boolean caseSensitive) {
             if (! caseSensitive) {
                 key = getCorrectKey(key, context);
             }
             return super.op_aref(context, key);
         }
 
         protected IRubyObject case_aware_op_aset(ThreadContext context, IRubyObject key, IRubyObject value, boolean caseSensitive) {
             if (!key.respondsTo("to_str")) {
                 throw getRuntime().newTypeError("can't convert " + key.getMetaClass() + " into String");
             }
             if (!value.respondsTo("to_str") && !value.isNil()) {
                 throw getRuntime().newTypeError("can't convert " + value.getMetaClass() + " into String");
             }
 
             if (! caseSensitive) {
                 key = getCorrectKey(key, context);
             }
 
             if (value.isNil()) {
                 return super.delete(context, key, org.jruby.runtime.Block.NULL_BLOCK);
             }
 
             //return super.aset(getRuntime().newString("sadfasdF"), getRuntime().newString("sadfasdF"));
             return super.op_aset(context, RuntimeHelpers.invoke(context, key, "to_str"),
                     value.isNil() ? getRuntime().getNil() : RuntimeHelpers.invoke(context, value, "to_str"));
         }
 
         private RubyString getCorrectKey(IRubyObject key, ThreadContext context) {
             RubyString originalKey = key.convertToString();
             RubyString actualKey = originalKey;
             Ruby runtime = context.getRuntime();
             if (Platform.IS_WINDOWS) {
                 // this is a rather ugly hack, but similar to MRI. See hash.c:ruby_setenv and similar in MRI
                 // we search all keys for a case-insensitive match, and use that
                 RubyArray keys = super.keys();
                 for (int i = 0; i < keys.size(); i++) {
                     RubyString candidateKey = keys.eltInternal(i).convertToString();
                     if (candidateKey.casecmp(context, originalKey).op_equal(context, RubyFixnum.zero(runtime)).isTrue()) {
                         actualKey = candidateKey;
                         break;
                     }
                 }
             }
             return actualKey;
         }
     }
     
     public static void createGlobals(ThreadContext context, Ruby runtime) {
         runtime.defineGlobalConstant("TOPLEVEL_BINDING", runtime.newBinding());
         
         runtime.defineGlobalConstant("TRUE", runtime.getTrue());
         runtime.defineGlobalConstant("FALSE", runtime.getFalse());
         runtime.defineGlobalConstant("NIL", runtime.getNil());
         
         // define ARGV and $* for this runtime
         RubyArray argvArray = runtime.newArray();
         String[] argv = runtime.getInstanceConfig().getArgv();
         for (int i = 0; i < argv.length; i++) {
             argvArray.append(RubyString.newStringShared(runtime, argv[i].getBytes()));
         }
         runtime.defineGlobalConstant("ARGV", argvArray);
         runtime.getGlobalVariables().defineReadonly("$*", new ValueAccessor(argvArray));
 
         IAccessor d = new ValueAccessor(runtime.newString(
                 runtime.getInstanceConfig().displayedFileName()));
         runtime.getGlobalVariables().define("$PROGRAM_NAME", d);
         runtime.getGlobalVariables().define("$0", d);
 
         // Version information:
         IRubyObject version = null;
         IRubyObject patchlevel = null;
         IRubyObject release = runtime.newString(Constants.COMPILE_DATE).freeze(context);
         IRubyObject platform = runtime.newString(Constants.PLATFORM).freeze(context);
         IRubyObject engine = runtime.newString(Constants.ENGINE).freeze(context);
 
         switch (runtime.getInstanceConfig().getCompatVersion()) {
         case RUBY1_8:
             version = runtime.newString(Constants.RUBY_VERSION).freeze(context);
             patchlevel = runtime.newFixnum(Constants.RUBY_PATCHLEVEL);
             break;
         case RUBY1_9:
             version = runtime.newString(Constants.RUBY1_9_VERSION).freeze(context);
             patchlevel = runtime.newFixnum(Constants.RUBY1_9_PATCHLEVEL);
             break;
         }
         runtime.defineGlobalConstant("RUBY_VERSION", version);
         runtime.defineGlobalConstant("RUBY_PATCHLEVEL", patchlevel);
         runtime.defineGlobalConstant("RUBY_RELEASE_DATE", release);
         runtime.defineGlobalConstant("RUBY_PLATFORM", platform);
         runtime.defineGlobalConstant("RUBY_ENGINE", engine);
 
         IRubyObject description = runtime.newString(runtime.getInstanceConfig().getVersionString()).freeze(context);
         runtime.defineGlobalConstant("RUBY_DESCRIPTION", description);
 
         IRubyObject copyright = runtime.newString(runtime.getInstanceConfig().getCopyrightString()).freeze(context);
         runtime.defineGlobalConstant("RUBY_COPYRIGHT", copyright);
 
         runtime.defineGlobalConstant("VERSION", version);
         runtime.defineGlobalConstant("RELEASE_DATE", release);
         runtime.defineGlobalConstant("PLATFORM", platform);
         
         IRubyObject jrubyVersion = runtime.newString(Constants.VERSION).freeze(context);
         IRubyObject jrubyRevision = runtime.newString(Constants.REVISION).freeze(context);
         runtime.defineGlobalConstant("JRUBY_VERSION", jrubyVersion);
         runtime.defineGlobalConstant("JRUBY_REVISION", jrubyRevision);
 
         if (runtime.is1_9()) {
             // needs to be a fixnum, but our revision is a sha1 hash from git
             runtime.defineGlobalConstant("RUBY_REVISION", runtime.newFixnum(Constants.RUBY1_9_REVISION));
         }
 		
         GlobalVariable kcodeGV = new KCodeGlobalVariable(runtime, "$KCODE", runtime.newString("NONE"));
         runtime.defineVariable(kcodeGV);
         runtime.defineVariable(new GlobalVariable.Copy(runtime, "$-K", kcodeGV));
         IRubyObject defaultRS = runtime.newString(runtime.getInstanceConfig().getRecordSeparator()).freeze(context);
         GlobalVariable rs = new StringGlobalVariable(runtime, "$/", defaultRS);
         runtime.defineVariable(rs);
         runtime.setRecordSeparatorVar(rs);
         runtime.getGlobalVariables().setDefaultSeparator(defaultRS);
         runtime.defineVariable(new StringGlobalVariable(runtime, "$\\", runtime.getNil()));
         runtime.defineVariable(new StringGlobalVariable(runtime, "$,", runtime.getNil()));
 
         runtime.defineVariable(new LineNumberGlobalVariable(runtime, "$."));
         runtime.defineVariable(new LastlineGlobalVariable(runtime, "$_"));
         runtime.defineVariable(new LastExitStatusVariable(runtime, "$?"));
 
         runtime.defineVariable(new ErrorInfoGlobalVariable(runtime, "$!", runtime.getNil()));
         runtime.defineVariable(new NonEffectiveGlobalVariable(runtime, "$=", runtime.getFalse()));
 
         if(runtime.getInstanceConfig().getInputFieldSeparator() == null) {
             runtime.defineVariable(new GlobalVariable(runtime, "$;", runtime.getNil()));
         } else {
             runtime.defineVariable(new GlobalVariable(runtime, "$;", RubyRegexp.newRegexp(runtime, runtime.getInstanceConfig().getInputFieldSeparator(), new RegexpOptions())));
         }
         
         Boolean verbose = runtime.getInstanceConfig().getVerbose();
         IRubyObject verboseValue = null;
         if (verbose == null) {
             verboseValue = runtime.getNil();
         } else if(verbose == Boolean.TRUE) {
             verboseValue = runtime.getTrue();
         } else {
             verboseValue = runtime.getFalse();
         }
         runtime.defineVariable(new VerboseGlobalVariable(runtime, "$VERBOSE", verboseValue));
         
         IRubyObject debug = runtime.newBoolean(runtime.getInstanceConfig().isDebug());
         runtime.defineVariable(new DebugGlobalVariable(runtime, "$DEBUG", debug));
         runtime.defineVariable(new DebugGlobalVariable(runtime, "$-d", debug));
 
         runtime.defineVariable(new SafeGlobalVariable(runtime, "$SAFE"));
 
         runtime.defineVariable(new BacktraceGlobalVariable(runtime, "$@"));
 
         IRubyObject stdin = new RubyIO(runtime, STDIO.IN);
         IRubyObject stdout = new RubyIO(runtime, STDIO.OUT);
         IRubyObject stderr = new RubyIO(runtime, STDIO.ERR);
 
         runtime.defineVariable(new InputGlobalVariable(runtime, "$stdin", stdin));
 
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stdout", stdout));
         runtime.getGlobalVariables().alias("$>", "$stdout");
         runtime.getGlobalVariables().alias("$defout", "$stdout");
 
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stderr", stderr));
         runtime.getGlobalVariables().alias("$deferr", "$stderr");
 
         runtime.defineGlobalConstant("STDIN", stdin);
         runtime.defineGlobalConstant("STDOUT", stdout);
         runtime.defineGlobalConstant("STDERR", stderr);
 
         runtime.defineVariable(new LoadedFeatures(runtime, "$\""));
         runtime.defineVariable(new LoadedFeatures(runtime, "$LOADED_FEATURES"));
 
         runtime.defineVariable(new LoadPath(runtime, "$:"));
         runtime.defineVariable(new LoadPath(runtime, "$-I"));
         runtime.defineVariable(new LoadPath(runtime, "$LOAD_PATH"));
         
         runtime.defineVariable(new MatchMatchGlobalVariable(runtime, "$&"));
         runtime.defineVariable(new PreMatchGlobalVariable(runtime, "$`"));
         runtime.defineVariable(new PostMatchGlobalVariable(runtime, "$'"));
         runtime.defineVariable(new LastMatchGlobalVariable(runtime, "$+"));
         runtime.defineVariable(new BackRefGlobalVariable(runtime, "$~"));
 
         // On platforms without a c-library accessable through JNA, getpid will return hashCode 
         // as $$ used to. Using $$ to kill processes could take down many runtimes, but by basing
         // $$ on getpid() where available, we have the same semantics as MRI.
         runtime.getGlobalVariables().defineReadonly("$$", new PidAccessor(runtime));
 
         // after defn of $stderr as the call may produce warnings
         defineGlobalEnvConstants(runtime);
         
         // Fixme: Do we need the check or does Main.java not call this...they should consolidate 
         if (runtime.getGlobalVariables().get("$*").isNil()) {
             runtime.getGlobalVariables().defineReadonly("$*", new ValueAccessor(runtime.newArray()));
         }
         
         runtime.getGlobalVariables().defineReadonly("$-p", 
                 new ValueAccessor(runtime.newBoolean(runtime.getInstanceConfig().isAssumePrinting())));
         runtime.getGlobalVariables().defineReadonly("$-a", 
                 new ValueAccessor(runtime.newBoolean(runtime.getInstanceConfig().isSplit())));
         runtime.getGlobalVariables().defineReadonly("$-l", 
                 new ValueAccessor(runtime.newBoolean(runtime.getInstanceConfig().isProcessLineEnds())));
 
         // ARGF, $< object
         RubyArgsFile.initArgsFile(runtime);
     }
 
     private static void defineGlobalEnvConstants(Ruby runtime) {
     	Map environmentVariableMap = null;
     	OSEnvironment environment = new OSEnvironment();
         environmentVariableMap = environment.getEnvironmentVariableMap(runtime);
 		
     	if (environmentVariableMap == null) {
             // if the environment variables can't be obtained, define an empty ENV
     		environmentVariableMap = new HashMap();
     	}
 
         CaseInsensitiveStringOnlyRubyHash h1 = new CaseInsensitiveStringOnlyRubyHash(runtime,
                                                        environmentVariableMap, runtime.getNil());
         h1.getSingletonClass().defineAnnotatedMethods(CaseInsensitiveStringOnlyRubyHash.class);
         runtime.defineGlobalConstant("ENV", h1);
 
         // Define System.getProperties() in ENV_JAVA
         Map systemProps = environment.getSystemPropertiesMap(runtime);
         runtime.defineGlobalConstant("ENV_JAVA", new StringOnlyRubyHash(
                 runtime, systemProps, runtime.getNil()));
     }
 
     private static class NonEffectiveGlobalVariable extends GlobalVariable {
         public NonEffectiveGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             runtime.getWarnings().warn(ID.INEFFECTIVE_GLOBAL, "warning: variable " + name + " is no longer effective; ignored", name);
             return value;
         }
 
         @Override
         public IRubyObject get() {
             runtime.getWarnings().warn(ID.INEFFECTIVE_GLOBAL, "warning: variable " + name + " is no longer effective", name);
             return runtime.getFalse();
         }
     }
 
     private static class LastExitStatusVariable extends GlobalVariable {
         public LastExitStatusVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             IRubyObject lastExitStatus = runtime.getCurrentContext().getLastExitStatus();
             return lastExitStatus == null ? runtime.getNil() : lastExitStatus;
         }
         
         @Override
         public IRubyObject set(IRubyObject lastExitStatus) {
             throw runtime.newNameError("$? is a read-only variable", "$?");
         }
     }
 
     private static class MatchMatchGlobalVariable extends GlobalVariable {
         public MatchMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.last_match(runtime.getCurrentContext().getCurrentScope().getBackRef(runtime));
         }
     }
 
     private static class PreMatchGlobalVariable extends GlobalVariable {
         public PreMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.match_pre(runtime.getCurrentContext().getCurrentScope().getBackRef(runtime));
         }
     }
 
     private static class PostMatchGlobalVariable extends GlobalVariable {
         public PostMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.match_post(runtime.getCurrentContext().getCurrentScope().getBackRef(runtime));
         }
     }
 
     private static class LastMatchGlobalVariable extends GlobalVariable {
         public LastMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.match_last(runtime.getCurrentContext().getCurrentScope().getBackRef(runtime));
         }
     }
 
     private static class BackRefGlobalVariable extends GlobalVariable {
         public BackRefGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RuntimeHelpers.getBackref(runtime, runtime.getCurrentContext());
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             RuntimeHelpers.setBackref(runtime, runtime.getCurrentContext(), value);
             return value;
         }
     }
 
     // Accessor methods.
 
     private static class LineNumberGlobalVariable extends GlobalVariable {
         public LineNumberGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             int line = (int)value.convertToInteger().getLongValue();
             runtime.setCurrentLine(line);
             RubyArgsFile.setCurrentLineNumber(runtime.getArgsFile(), line);
             return value;
         }
 
         @Override
         public IRubyObject get() {
             return runtime.newFixnum(runtime.getCurrentLine());
         }
     }
 
     private static class ErrorInfoGlobalVariable extends GlobalVariable {
         public ErrorInfoGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, null);
             set(value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (!value.isNil() &&
                     !runtime.getException().isInstance(value) &&
                     !(JavaUtil.isJavaObject(value) && JavaUtil.unwrapJavaObject(value) instanceof Throwable)) {
                 throw runtime.newTypeError("assigning non-exception to $!");
             }
             
             return runtime.getCurrentContext().setErrorInfo(value);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.getCurrentContext().getErrorInfo();
         }
     }
 
     // FIXME: move out of this class!
     public static class StringGlobalVariable extends GlobalVariable {
         public StringGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (!value.isNil() && ! (value instanceof RubyString)) {
                 throw runtime.newTypeError("value of " + name() + " must be a String");
             }
             return super.set(value);
         }
     }
 
     public static class KCodeGlobalVariable extends GlobalVariable {
         public KCodeGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.getKCode().kcode(runtime);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             runtime.setKCode(KCode.create(runtime, value.convertToString().toString()));
             return value;
         }
     }
 
     private static class SafeGlobalVariable extends GlobalVariable {
         public SafeGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.newFixnum(runtime.getSafeLevel());
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
 //            int level = RubyNumeric.fix2int(value);
 //            if (level < runtime.getSafeLevel()) {
 //            	throw runtime.newSecurityError("tried to downgrade safe level from " + 
 //            			runtime.getSafeLevel() + " to " + level);
 //            }
 //            runtime.setSafeLevel(level);
             // thread.setSafeLevel(level);
             runtime.getWarnings().warn(ID.SAFE_NOT_SUPPORTED, "SAFE levels are not supported in JRuby");
             return RubyFixnum.newFixnum(runtime, runtime.getSafeLevel());
         }
     }
 
     private static class VerboseGlobalVariable extends GlobalVariable {
         public VerboseGlobalVariable(Ruby runtime, String name, IRubyObject initialValue) {
             super(runtime, name, initialValue);
             set(initialValue);
         }
         
         @Override
         public IRubyObject get() {
             return runtime.getVerbose();
         }
 
         @Override
         public IRubyObject set(IRubyObject newValue) {
             if (newValue.isNil()) {
                 runtime.setVerbose(newValue);
             } else {
                 runtime.setVerbose(runtime.newBoolean(newValue.isTrue()));
             }
 
             return newValue;
         }
     }
 
     private static class DebugGlobalVariable extends GlobalVariable {
         public DebugGlobalVariable(Ruby runtime, String name, IRubyObject initialValue) {
             super(runtime, name, initialValue);
             set(initialValue);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.getDebug();
         }
 
         @Override
         public IRubyObject set(IRubyObject newValue) {
             if (newValue.isNil()) {
                 runtime.setDebug(newValue);
             } else {
                 runtime.setDebug(runtime.newBoolean(newValue.isTrue()));
             }
 
             return newValue;
         }
     }
 
     private static class BacktraceGlobalVariable extends GlobalVariable {
         public BacktraceGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         @Override
         public IRubyObject get() {
             IRubyObject errorInfo = runtime.getGlobalVariables().get("$!");
             IRubyObject backtrace = errorInfo.isNil() ? runtime.getNil() : errorInfo.callMethod(errorInfo.getRuntime().getCurrentContext(), "backtrace");
             //$@ returns nil if $!.backtrace is not an array
             if (!(backtrace instanceof RubyArray)) {
                 backtrace = runtime.getNil();
             }
             return backtrace;
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (runtime.getGlobalVariables().get("$!").isNil()) {
                 throw runtime.newArgumentError("$! not set.");
             }
             runtime.getGlobalVariables().get("$!").callMethod(value.getRuntime().getCurrentContext(), "set_backtrace", value);
             return value;
         }
     }
 
     private static class LastlineGlobalVariable extends GlobalVariable {
         public LastlineGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         @Override
         public IRubyObject get() {
             return RuntimeHelpers.getLastLine(runtime, runtime.getCurrentContext());
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             RuntimeHelpers.setLastLine(runtime, runtime.getCurrentContext(), value);
             return value;
         }
     }
 
     public static class InputGlobalVariable extends GlobalVariable {
         public InputGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (value == get()) {
                 return value;
             }
             
             return super.set(value);
         }
     }
 
     public static class OutputGlobalVariable extends GlobalVariable {
         public OutputGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (value == get()) {
                 return value;
             }
             if (value instanceof RubyIO) {
                 RubyIO io = (RubyIO)value;
                 
                 // HACK: in order to have stdout/err act like ttys and flush always,
                 // we set anything assigned to stdout/stderr to sync
-                io.getHandler().setSync(true);
+                try {
+                    io.getOpenFile().getWriteStreamSafe().setSync(true);
+                } catch (BadDescriptorException e) {
+                    throw runtime.newErrnoEBADFError();
+                }
             }
 
             if (!value.respondsTo("write")) {
                 throw runtime.newTypeError(name() + " must have write method, " +
                                     value.getType().getName() + " given");
             }
 
             return super.set(value);
         }
     }
 
     private static class LoadPath extends ReadonlyGlobalVariable {
         public LoadPath(Ruby runtime, String name) {
             super(runtime, name, null);
         }
         
         /**
          * @see org.jruby.runtime.GlobalVariable#get()
          */
         @Override
         public IRubyObject get() {
             return runtime.getLoadService().getLoadPath();
         }
     }
 
     private static class LoadedFeatures extends ReadonlyGlobalVariable {
         public LoadedFeatures(Ruby runtime, String name) {
             super(runtime, name, null);
         }
         
         /**
          * @see org.jruby.runtime.GlobalVariable#get()
          */
         @Override
         public IRubyObject get() {
             return runtime.getLoadService().getLoadedFeatures();
         }
     }
 
     /**
      * A special accessor for getpid, to avoid loading the posix subsystem until
      * it is needed.
      */
     private static final class PidAccessor implements IAccessor {
         private final Ruby runtime;
         private IRubyObject pid = null;
 
         public PidAccessor(Ruby runtime) {
             this.runtime = runtime;
         }
 
         public IRubyObject getValue() {
             return pid != null ? pid : (pid = runtime.newFixnum(runtime.getPosix().getpid()));
         }
 
         public IRubyObject setValue(IRubyObject newValue) {
             throw runtime.newRuntimeError("cannot assign to $$");
         }
     }
 }
diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index 3346beed6f..3782b38342 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,4111 +1,4125 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Evan Buswell <ebuswell@gmail.com>
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
 
 import org.jcodings.exception.EncodingException;
 import com.kenai.constantine.platform.Fcntl;
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.FileNotFoundException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.nio.channels.CancelledKeyException;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.NonReadableChannelException;
 import java.nio.channels.Pipe;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jcodings.Encoding;
 import org.jruby.anno.FrameField;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.libraries.FcntlLibrary;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.Stream;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.DirectoryAsFileException;
 import org.jruby.util.io.STDIO;
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.ChannelDescriptor;
 
 import org.jruby.util.io.SelectorFactory;
 import java.nio.channels.spi.SelectorProvider;
 import java.util.Arrays;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.javasupport.util.RuntimeHelpers;
 
 import static org.jruby.CompatVersion.*;
 import static org.jruby.RubyEnumerator.enumeratorize;
 
 /**
  * 
  * @author jpetersen
  */
 @JRubyClass(name="IO", include="Enumerable")
 public class RubyIO extends RubyObject {
     // This should only be called by this and RubyFile.
     // It allows this object to be created without a IOHandler.
     public RubyIO(Ruby runtime, RubyClass type) {
         super(runtime, type);
         
         openFile = new OpenFile();
     }
 
     public RubyIO(Ruby runtime, OutputStream outputStream) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (outputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(outputStream))));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.WRITABLE | OpenFile.APPEND);
     }
     
     public RubyIO(Ruby runtime, InputStream inputStream) {
         super(runtime, runtime.getIO());
         
         if (inputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(inputStream))));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.READABLE);
     }
     
     public RubyIO(Ruby runtime, Channel channel) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (channel == null) {
             throw runtime.newRuntimeError("Opening null channelpo");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(channel)));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
     }
 
     public RubyIO(Ruby runtime, ShellLauncher.POpenProcess process, ModeFlags modes) {
     	super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         
         openFile.setMode(modes.getOpenFileFlags() | OpenFile.SYNC);
         openFile.setProcess(process);
 
         try {
             if (openFile.isReadable()) {
                 Channel inChannel;
                 if (process.getInput() != null) {
                     // NIO-based
                     inChannel = process.getInput();
                 } else {
                     // Stream-based
                     inChannel = Channels.newChannel(process.getInputStream());
                 }
                 
                 ChannelDescriptor main = new ChannelDescriptor(
                         inChannel);
                 main.setCanBeSeekable(false);
                 
                 openFile.setMainStream(ChannelStream.open(getRuntime(), main));
             }
             
             if (openFile.isWritable() && process.hasOutput()) {
                 Channel outChannel;
                 if (process.getOutput() != null) {
                     // NIO-based
                     outChannel = process.getOutput();
                 } else {
                     outChannel = Channels.newChannel(process.getOutputStream());
                 }
 
                 ChannelDescriptor pipe = new ChannelDescriptor(
                         outChannel);
                 pipe.setCanBeSeekable(false);
                 
                 if (openFile.getMainStream() != null) {
                     openFile.setPipeStream(ChannelStream.open(getRuntime(), pipe));
                 } else {
                     openFile.setMainStream(ChannelStream.open(getRuntime(), pipe));
                 }
             }
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
     
     public RubyIO(Ruby runtime, STDIO stdio) {
         super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         ChannelDescriptor descriptor;
+        Stream mainStream;
 
         try {
             switch (stdio) {
             case IN:
                 // special constructor that accepts stream, not channel
                 descriptor = new ChannelDescriptor(runtime.getIn(), new ModeFlags(ModeFlags.RDONLY), FileDescriptor.in);
                 runtime.putFilenoMap(0, descriptor.getFileno());
-                openFile.setMainStream(
-                        ChannelStream.open(
-                            runtime, 
-                            descriptor));
+                mainStream = ChannelStream.open(runtime, descriptor);
+                openFile.setMainStream(mainStream);
                 break;
             case OUT:
                 descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getOut()), new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out);
                 runtime.putFilenoMap(1, descriptor.getFileno());
-                openFile.setMainStream(
-                        ChannelStream.open(
-                            runtime, 
-                            descriptor));
+                mainStream = ChannelStream.open(runtime, descriptor);
+                openFile.setMainStream(mainStream);
                 openFile.getMainStream().setSync(true);
                 break;
             case ERR:
                 descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getErr()), new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.err);
                 runtime.putFilenoMap(2, descriptor.getFileno());
-                openFile.setMainStream(
-                        ChannelStream.open(
-                            runtime, 
-                            descriptor));
+                mainStream = ChannelStream.open(runtime, descriptor);
+                openFile.setMainStream(mainStream);
                 openFile.getMainStream().setSync(true);
                 break;
             }
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         // never autoclose stdio streams
         openFile.setAutoclose(false);
     }
     
     public static RubyIO newIO(Ruby runtime, Channel channel) {
         return new RubyIO(runtime, channel);
     }
     
     public OpenFile getOpenFile() {
         return openFile;
     }
     
     protected OpenFile getOpenFileChecked() {
         openFile.checkClosed(getRuntime());
         return openFile;
     }
     
     private static ObjectAllocator IO_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIO(runtime, klass);
         }
     };
 
     /*
      * We use FILE versus IO to match T_FILE in MRI.
      */
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.FILE;
     }
 
     public static RubyClass createIOClass(Ruby runtime) {
         RubyClass ioClass = runtime.defineClass("IO", runtime.getObject(), IO_ALLOCATOR);
 
         ioClass.index = ClassIndex.IO;
         ioClass.setReifiedClass(RubyIO.class);
 
         ioClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyIO;
             }
         };
 
         ioClass.includeModule(runtime.getEnumerable());
         
         // TODO: Implement tty? and isatty.  We have no real capability to
         // determine this from java, but if we could set tty status, then
         // we could invoke jruby differently to allow stdin to return true
         // on this.  This would allow things like cgi.rb to work properly.
         
         ioClass.defineAnnotatedMethods(RubyIO.class);
 
         // Constants for seek
         ioClass.fastSetConstant("SEEK_SET", runtime.newFixnum(Stream.SEEK_SET));
         ioClass.fastSetConstant("SEEK_CUR", runtime.newFixnum(Stream.SEEK_CUR));
         ioClass.fastSetConstant("SEEK_END", runtime.newFixnum(Stream.SEEK_END));
 
         if (runtime.is1_9()) {
             ioClass.defineModuleUnder("WaitReadable");
             ioClass.defineModuleUnder("WaitWritable");
         }
 
         return ioClass;
     }
 
     public OutputStream getOutStream() {
-        return getHandler().newOutputStream();
+        try {
+            return getOpenFileChecked().getMainStreamSafe().newOutputStream();
+        } catch (BadDescriptorException e) {
+            throw getRuntime().newErrnoEBADFError();
+        }
     }
 
     public InputStream getInStream() {
-        return getHandler().newInputStream();
+        try {
+            return getOpenFileChecked().getMainStreamSafe().newInputStream();
+        } catch (BadDescriptorException e) {
+            throw getRuntime().newErrnoEBADFError();
+        }
     }
 
     public Channel getChannel() {
-        return getHandler().getChannel();
+        try {
+            return getOpenFileChecked().getMainStreamSafe().getChannel();
+        } catch (BadDescriptorException e) {
+            throw getRuntime().newErrnoEBADFError();
+        }
     }
-    
-    public Stream getHandler() {
-        return getOpenFileChecked().getMainStream();
+
+    @Deprecated
+    public Stream getHandler() throws BadDescriptorException {
+        return getOpenFileChecked().getMainStreamSafe();
     }
 
     protected void reopenPath(Ruby runtime, IRubyObject[] args) {
         if (runtime.is1_9() && !(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             args[0] = args[0].callMethod(runtime.getCurrentContext(), "to_path");
         }
         IRubyObject pathString = args[0].convertToString();
 
         // TODO: check safe, taint on incoming string
 
         try {
             ModeFlags modes;
             if (args.length > 1) {
                 IRubyObject modeString = args[1].convertToString();
                 modes = getIOModes(runtime, modeString.toString());
 
                 openFile.setMode(modes.getOpenFileFlags());
             } else {
                 modes = getIOModes(runtime, "r");
             }
 
             String path = pathString.toString();
 
             // Ruby code frequently uses a platform check to choose "NUL:" on windows
             // but since that check doesn't work well on JRuby, we help it out
 
             openFile.setPath(path);
 
             if (openFile.getMainStream() == null) {
                 try {
                     openFile.setMainStream(ChannelStream.fopen(runtime, path, modes));
                 } catch (FileExistsException fee) {
                     throw runtime.newErrnoEEXISTError(path);
                 }
 
                 if (openFile.getPipeStream() != null) {
                     openFile.getPipeStream().fclose();
                     openFile.setPipeStream(null);
                 }
             } else {
                 // TODO: This is an freopen in MRI, this is close, but not quite the same
-                openFile.getMainStream().freopen(runtime, path, getIOModes(runtime, openFile.getModeAsString(runtime)));
+                openFile.getMainStreamSafe().freopen(runtime, path, getIOModes(runtime, openFile.getModeAsString(runtime)));
                 
                 if (openFile.getPipeStream() != null) {
                     // TODO: pipe handler to be reopened with path and "w" mode
                 }
             }
         } catch (PipeException pe) {
             throw runtime.newErrnoEPIPEError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     protected void reopenIO(Ruby runtime, RubyIO ios) {
         try {
             if (ios.openFile == this.openFile) return;
 
             OpenFile originalFile = ios.getOpenFileChecked();
             OpenFile selfFile = getOpenFileChecked();
 
             long pos = 0;
             if (originalFile.isReadable()) {
-                pos = originalFile.getMainStream().fgetpos();
+                pos = originalFile.getMainStreamSafe().fgetpos();
             }
 
             if (originalFile.getPipeStream() != null) {
                 originalFile.getPipeStream().fflush();
             } else if (originalFile.isWritable()) {
-                originalFile.getMainStream().fflush();
+                originalFile.getMainStreamSafe().fflush();
             }
 
             if (selfFile.isWritable()) {
-                selfFile.getWriteStream().fflush();
+                selfFile.getWriteStreamSafe().fflush();
             }
 
             selfFile.setMode(originalFile.getMode());
             selfFile.setProcess(originalFile.getProcess());
             selfFile.setLineNumber(originalFile.getLineNumber());
             selfFile.setPath(originalFile.getPath());
             selfFile.setFinalizer(originalFile.getFinalizer());
 
-            ChannelDescriptor selfDescriptor = selfFile.getMainStream().getDescriptor();
-            ChannelDescriptor originalDescriptor = originalFile.getMainStream().getDescriptor();
+            ChannelDescriptor selfDescriptor = selfFile.getMainStreamSafe().getDescriptor();
+            ChannelDescriptor originalDescriptor = originalFile.getMainStreamSafe().getDescriptor();
 
             // confirm we're not reopening self's channel
             if (selfDescriptor.getChannel() != originalDescriptor.getChannel()) {
                 // check if we're a stdio IO, and ensure we're not badly mutilated
                 if (runtime.getFileno(selfDescriptor) >= 0 && runtime.getFileno(selfDescriptor) <= 2) {
-                    selfFile.getMainStream().clearerr();
+                    selfFile.getMainStreamSafe().clearerr();
 
                     // dup2 new fd into self to preserve fileno and references to it
                     originalDescriptor.dup2Into(selfDescriptor);
                 } else {
                     Stream pipeFile = selfFile.getPipeStream();
                     int mode = selfFile.getMode();
-                    selfFile.getMainStream().fclose();
+                    selfFile.getMainStreamSafe().fclose();
                     selfFile.setPipeStream(null);
 
                     // TODO: turn off readable? am I reading this right?
                     // This only seems to be used while duping below, since modes gets
                     // reset to actual modes afterward
                     //fptr->mode &= (m & FMODE_READABLE) ? ~FMODE_READABLE : ~FMODE_WRITABLE;
 
                     if (pipeFile != null) {
                         selfFile.setMainStream(ChannelStream.fdopen(runtime, originalDescriptor, new ModeFlags()));
                         selfFile.setPipeStream(pipeFile);
                     } else {
                         // only use internal fileno here, stdio is handled above
                         selfFile.setMainStream(
                                 ChannelStream.open(
                                 runtime,
                                 originalDescriptor.dup2(selfDescriptor.getFileno())));
 
                         // since we're not actually duping the incoming channel into our handler, we need to
                         // copy the original sync behavior from the other handler
-                        selfFile.getMainStream().setSync(selfFile.getMainStream().isSync());
+                        selfFile.getMainStreamSafe().setSync(selfFile.getMainStreamSafe().isSync());
                     }
                     selfFile.setMode(mode);
                 }
 
                 // TODO: anything threads attached to original fd are notified of the close...
                 // see rb_thread_fd_close
 
                 if (originalFile.isReadable() && pos >= 0) {
                     selfFile.seek(pos, Stream.SEEK_SET);
                     originalFile.seek(pos, Stream.SEEK_SET);
                 }
             }
 
             // only use internal fileno here, stdio is handled above
             if (selfFile.getPipeStream() != null && selfDescriptor.getFileno() != selfFile.getPipeStream().getDescriptor().getFileno()) {
                 int fd = selfFile.getPipeStream().getDescriptor().getFileno();
 
                 if (originalFile.getPipeStream() == null) {
                     selfFile.getPipeStream().fclose();
                     selfFile.setPipeStream(null);
                 } else if (fd != originalFile.getPipeStream().getDescriptor().getFileno()) {
                     selfFile.getPipeStream().fclose();
                     ChannelDescriptor newFD2 = originalFile.getPipeStream().getDescriptor().dup2(fd);
                     selfFile.setPipeStream(ChannelStream.fdopen(runtime, newFD2, getIOModes(runtime, "w")));
                 }
             }
 
             // TODO: restore binary mode
             //            if (fptr->mode & FMODE_BINMODE) {
             //                rb_io_binmode(io);
             //            }
 
             // TODO: set our metaclass to target's class (i.e. scary!)
 
         } catch (IOException ex) { // TODO: better error handling
-            throw runtime.newIOError("could not reopen: " + ex.getMessage());
+            throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (PipeException ex) {
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (InvalidValueException ive) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     @JRubyMethod(name = "reopen", required = 1, optional = 1)
     public IRubyObject reopen(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
     	IRubyObject tmp = TypeConverter.convertToTypeWithCheck(args[0], runtime.getIO(), "to_io");
         
     	if (!tmp.isNil()) {
             reopenIO(runtime, (RubyIO) tmp);
         } else {
             reopenPath(runtime, args);
         }
         
         return this;
     }
     
     public static ModeFlags getIOModes(Ruby runtime, String modesString) throws InvalidValueException {
         return new ModeFlags(getIOModesIntFromString(runtime, modesString));
     }
         
     public static int getIOModesIntFromString(Ruby runtime, String modesString) {
         int modes = 0;
         int length = modesString.length();
 
         if (length == 0) {
             throw runtime.newArgumentError("illegal access mode");
         }
 
         switch (modesString.charAt(0)) {
         case 'r' :
             modes |= ModeFlags.RDONLY;
             break;
         case 'a' :
             modes |= ModeFlags.APPEND | ModeFlags.WRONLY | ModeFlags.CREAT;
             break;
         case 'w' :
             modes |= ModeFlags.WRONLY | ModeFlags.TRUNC | ModeFlags.CREAT;
             break;
         default :
             throw runtime.newArgumentError("illegal access mode " + modesString);
         }
 
         ModifierLoop: for (int n = 1; n < length; n++) {
             switch (modesString.charAt(n)) {
             case 'b':
                 modes |= ModeFlags.BINARY;
                 break;
             case '+':
                 modes = (modes & ~ModeFlags.ACCMODE) | ModeFlags.RDWR;
                 break;
             case 't' :
                 // FIXME: add text mode to mode flags
                 break;
             case ':':
                 break ModifierLoop;
             default:
                 throw runtime.newArgumentError("illegal access mode " + modesString);
             }
         }
 
         return modes;
     }
 
     /*
      * Ensure that separator is valid otherwise give it the default paragraph separator.
      */
     private static ByteList separator(Ruby runtime) {
         return separator(runtime.getRecordSeparatorVar().get());
     }
 
     private static ByteList separator(IRubyObject separatorValue) {
         ByteList separator = separatorValue.isNil() ? null :
             separatorValue.convertToString().getByteList();
 
         if (separator != null && separator.getRealSize() == 0) separator = Stream.PARAGRAPH_DELIMETER;
 
         return separator;
     }
 
     private static ByteList getSeparatorFromArgs(Ruby runtime, IRubyObject[] args, int idx) {
         return separator(args.length > idx ? args[idx] : runtime.getRecordSeparatorVar().get());
     }
 
     private ByteList getSeparatorForGets(Ruby runtime, IRubyObject[] args) {
         return getSeparatorFromArgs(runtime, args, 0);
     }
 
     private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {
         return getline(runtime, separator, -1, cache);
     }
 
     public IRubyObject getline(Ruby runtime, ByteList separator) {
         return getline(runtime, separator, -1, null);
     }
 
 
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     public IRubyObject getline(Ruby runtime, ByteList separator, long limit) {
         return getline(runtime, separator, limit, null);
     }
     
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     private IRubyObject getline(Ruby runtime, ByteList separator, long limit, ByteListCache cache) {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             boolean isParagraph = separator == Stream.PARAGRAPH_DELIMETER;
             separator = isParagraph ? Stream.PARAGRAPH_SEPARATOR : separator;
             
             if (isParagraph) swallow('\n');
             
             if (separator == null && limit < 0) {
                 RubyString str = readAll();
                 if (str.getByteList().length() == 0) {
                     return runtime.getNil();
                 }
                 incrementLineno(runtime, myOpenFile);
                 return str;
             } else if (limit == 0) {
                 return RubyString.newEmptyString(runtime, externalEncoding);
             } else if (separator.length() == 1 && limit < 0) {
                 return getlineFast(runtime, separator.get(0) & 0xFF, cache);
             } else {
-                Stream readStream = myOpenFile.getMainStream();
+                Stream readStream = myOpenFile.getMainStreamSafe();
                 int c = -1;
                 int n = -1;
                 int newline = separator.get(separator.length() - 1) & 0xFF;
 
                 ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
                 try {
                     boolean update = false;
                     boolean limitReached = false;
 
                     while (true) {
                         do {
                             readCheck(readStream);
                             readStream.clearerr();
 
                             try {
                                 runtime.getCurrentContext().getThread().beforeBlockingCall();
                                 if (limit == -1) {
                                     n = readStream.getline(buf, (byte) newline);
                                 } else {
                                     n = readStream.getline(buf, (byte) newline, limit);
                                     limit -= n;
                                     if (limit <= 0) {
                                         update = limitReached = true;
                                         break;
                                     }
                                 }
 
                                 c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                             } catch (EOFException e) {
                                 n = -1;
                             } finally {
                                 runtime.getCurrentContext().getThread().afterBlockingCall();
                             }
                             
                             // CRuby checks ferror(f) and retry getc for
                             // non-blocking IO.
                             if (n == 0) {
                                 waitReadable(readStream);
                                 continue;
                             } else if (n == -1) {
                                 break;
                             }
 
                             update = true;
                         } while (c != newline); // loop until we see the nth separator char
 
                         // if we hit EOF or reached limit then we're done
                         if (n == -1 || limitReached) break;
 
                         // if we've found the last char of the separator,
                         // and we've found at least as many characters as separator length,
                         // and the last n characters of our buffer match the separator, we're done
                         if (c == newline && buf.length() >= separator.length() &&
                                 0 == ByteList.memcmp(buf.getUnsafeBytes(), buf.getBegin() + buf.getRealSize() - separator.length(), separator.getUnsafeBytes(), separator.getBegin(), separator.getRealSize())) {
                             break;
                         }
                     }
                     
                     if (isParagraph && c != -1) swallow('\n');
                     if (!update) return runtime.getNil();
 
                     incrementLineno(runtime, myOpenFile);
 
                     return makeString(runtime, buf, cache != null);
                 }
                 finally {
                     if (cache != null) cache.release(buf);
                 }
             }
-        } catch (PipeException ex) {
-            throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (EOFException e) {
             return runtime.getNil();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
-            throw runtime.newIOError(e.getMessage());
+            throw runtime.newIOErrorFromException(e);
         }
     }
 
     private Encoding getExternalEncoding(Ruby runtime) {
         return externalEncoding != null ? externalEncoding : runtime.getDefaultExternalEncoding();
     }
 
     private RubyString makeString(Ruby runtime, ByteList buffer, boolean isCached) {
         ByteList newBuf = isCached ? new ByteList(buffer) : buffer;
         Encoding encoding = getExternalEncoding(runtime);
 
         if (encoding != null) newBuf.setEncoding(encoding);
 
         RubyString str = RubyString.newString(runtime, newBuf);
         str.setTaint(true);
 
         return str;
     }
 
     private void incrementLineno(Ruby runtime, OpenFile myOpenFile) {
         int lineno = myOpenFile.getLineNumber() + 1;
         myOpenFile.setLineNumber(lineno);
         runtime.setCurrentLine(lineno);
         RubyArgsFile.setCurrentLineNumber(runtime.getArgsFile(), lineno);
     }
 
     protected boolean swallow(int term) throws IOException, BadDescriptorException {
-        Stream readStream = openFile.getMainStream();
+        Stream readStream = openFile.getMainStreamSafe();
         int c;
         
         do {
             readCheck(readStream);
             
             try {
                 c = readStream.fgetc();
             } catch (EOFException e) {
                 c = -1;
             }
             
             if (c != term) {
                 readStream.ungetc(c);
                 return true;
             }
         } while (c != -1);
         
         return false;
     }
     
     private static String vendor;
     static { String v = SafePropertyAccessor.getProperty("java.vendor") ; vendor = (v == null) ? "" : v; };
     private static String msgEINTR = "Interrupted system call";
 
     public static boolean restartSystemCall(Exception e) {
         return vendor.startsWith("Apple") && e.getMessage().equals(msgEINTR);
     }
     
     private IRubyObject getlineFast(Ruby runtime, int delim, ByteListCache cache) throws IOException, BadDescriptorException {
-        Stream readStream = openFile.getMainStream();
+        Stream readStream = openFile.getMainStreamSafe();
         int c = -1;
 
         ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
         try {
             boolean update = false;
             do {
                 readCheck(readStream);
                 readStream.clearerr();
                 int n;
                 try {
                     runtime.getCurrentContext().getThread().beforeBlockingCall();
                     n = readStream.getline(buf, (byte) delim);
                     c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                 } catch (EOFException e) {
                     n = -1;
                 } finally {
                     runtime.getCurrentContext().getThread().afterBlockingCall();
                 }
 
                 // CRuby checks ferror(f) and retry getc for non-blocking IO.
                 if (n == 0) {
                     waitReadable(readStream);
                     continue;
                 } else if (n == -1) {
                     break;
                 }
                 
                 update = true;
             } while (c != delim);
 
             if (!update) return runtime.getNil();
                 
             incrementLineno(runtime, openFile);
 
             return makeString(runtime, buf, cache != null);
         } finally {
             if (cache != null) cache.release(buf);
         }
     }
     // IO class methods.
 
     @JRubyMethod(name = {"new", "for_fd"}, rest = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass)recv;
         
         if (block.isGiven()) {
             String className = klass.getName();
             context.getRuntime().getWarnings().warn(
                     ID.BLOCK_NOT_ACCEPTED,
                     className + "::new() does not take block; use " + className + "::open() instead",
                     className + "::open()");
         }
         
         return klass.newInstance(context, args, block);
     }
 
     private IRubyObject initializeCommon19(int fileno, ModeFlags modes) {
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(getRuntime().getFilenoExtMap(fileno));
 
             if (descriptor == null) throw getRuntime().newErrnoEBADFError();
 
             descriptor.checkOpen();
 
             if (modes == null) modes = descriptor.getOriginalModes();
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(getRuntime(), false);
             }
 
             openFile.setMode(modes.getOpenFileFlags());
             openFile.setMainStream(fdopen(descriptor, modes));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ive) {
             throw getRuntime().newErrnoEINVALError();
         }
 
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, Block unusedBlock) {
         return initializeCommon19(RubyNumeric.fix2int(fileNumber), null);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject second, Block unusedBlock) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         ModeFlags modes;
         if (second instanceof RubyHash) {
             modes = parseOptions(context, second, null);
         } else {
             modes = parseModes19(context, second);
         }
 
         return initializeCommon19(fileno, modes);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject modeValue, IRubyObject options, Block unusedBlock) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         ModeFlags modes = parseModes19(context, modeValue);
 
         modes = parseOptions(context, options, modes);
         return initializeCommon19(fileno, modes);
     }
 
     protected ModeFlags parseModes(IRubyObject arg) {
         try {
             if (arg instanceof RubyFixnum) return new ModeFlags(RubyFixnum.fix2long(arg));
 
             return getIOModes(getRuntime(), arg.convertToString().toString());
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
 
     protected ModeFlags parseModes19(ThreadContext context, IRubyObject arg) {
         ModeFlags modes = parseModes(arg);
 
         if (arg instanceof RubyString) {
             parseEncodingFromString(context, arg, 1);
         }
 
         return modes;
     }
 
     private void parseEncodingFromString(ThreadContext context, IRubyObject arg, int initialPosition) {
         RubyString modes19 = arg.convertToString();
         if (modes19.toString().contains(":")) {
             Ruby runtime = context.getRuntime();
 
             IRubyObject[] fullEncoding = modes19.split(context, RubyString.newString(runtime, ":")).toJavaArray();
 
             IRubyObject externalEncodingOption = fullEncoding[initialPosition];
             RubyString dash = runtime.newString("-");
             if (dash.eql(externalEncodingOption)) {
                 externalEncodingOption = runtime.getEncodingService().getDefaultExternal();
             }
 
             if (fullEncoding.length > (initialPosition + 1)) {
                 IRubyObject internalEncodingOption = fullEncoding[initialPosition + 1];
                 if (dash.eql(internalEncodingOption)) {
                     internalEncodingOption = runtime.getEncodingService().getDefaultInternal();
                 }
                 set_encoding(context, externalEncodingOption, internalEncodingOption);
             } else {
                 set_encoding(context, externalEncodingOption);
             }
         }
     }
 
     @JRubyMethod(required = 1, optional = 1, visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         int argCount = args.length;
         ModeFlags modes;
         
         int fileno = RubyNumeric.fix2int(args[0]);
         
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(getRuntime().getFilenoExtMap(fileno));
             
             if (descriptor == null) {
                 throw getRuntime().newErrnoEBADFError();
             }
             
             descriptor.checkOpen();
             
             if (argCount == 2) {
                 if (args[1] instanceof RubyFixnum) {
                     modes = new ModeFlags(RubyFixnum.fix2long(args[1]));
                 } else {
                     modes = getIOModes(getRuntime(), args[1].convertToString().toString());
                 }
             } else {
                 // use original modes
                 modes = descriptor.getOriginalModes();
             }
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(getRuntime(), false);
             }
 
             openFile.setMode(modes.getOpenFileFlags());
         
             openFile.setMainStream(fdopen(descriptor, modes));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ive) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         return this;
     }
     
     protected Stream fdopen(ChannelDescriptor existingDescriptor, ModeFlags modes) throws InvalidValueException {
         // See if we already have this descriptor open.
         // If so then we can mostly share the handler (keep open
         // file, but possibly change the mode).
         
         if (existingDescriptor == null) {
             // redundant, done above as well
             
             // this seems unlikely to happen unless it's a totally bogus fileno
             // ...so do we even need to bother trying to create one?
             
             // IN FACT, we should probably raise an error, yes?
             throw getRuntime().newErrnoEBADFError();
             
 //            if (mode == null) {
 //                mode = "r";
 //            }
 //            
 //            try {
 //                openFile.setMainStream(streamForFileno(getRuntime(), fileno));
 //            } catch (BadDescriptorException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            } catch (IOException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            }
 //            //modes = new IOModes(getRuntime(), mode);
 //            
 //            registerStream(openFile.getMainStream());
         } else {
             // We are creating a new IO object that shares the same
             // IOHandler (and fileno).
             return ChannelStream.fdopen(getRuntime(), existingDescriptor, modes);
         }
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject external_encoding(ThreadContext context) {
         return externalEncoding != null ?
             context.getRuntime().getEncodingService().getEncoding(externalEncoding) :
             context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject internal_encoding(ThreadContext context) {
         return internalEncoding != null ?
             context.getRuntime().getEncodingService().getEncoding(internalEncoding) :
             context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString) {
         setExternalEncoding(context, encodingString);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding, IRubyObject options) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     private void setExternalEncoding(ThreadContext context, IRubyObject encoding) {
         externalEncoding = getEncodingCommon(context, encoding);
     }
 
 
     private void setInternalEncoding(ThreadContext context, IRubyObject encoding) {
         Encoding internalEncodingOption = getEncodingCommon(context, encoding);
 
         if (internalEncodingOption == externalEncoding) {
             context.getRuntime().getWarnings().warn("Ignoring internal encoding " + encoding
                     + ": it is identical to external encoding " + external_encoding(context));
         } else {
             internalEncoding = internalEncodingOption;
         }
     }
 
     private Encoding getEncodingCommon(ThreadContext context, IRubyObject encoding) {
         if (encoding instanceof RubyEncoding) return ((RubyEncoding) encoding).getEncoding();
         
         return context.getRuntime().getEncodingService().getEncodingFromObject(encoding);
     }
 
     @JRubyMethod(required = 1, optional = 2, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         RubyClass klass = (RubyClass)recv;
         
         RubyIO io = (RubyIO)klass.newInstance(context, args, block);
 
         if (block.isGiven()) {
             try {
                 return block.yield(context, io);
             } finally {
                 try {
                     io.getMetaClass().finvoke(context, io, "close", IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
                 } catch (RaiseException re) {
                     RubyException rubyEx = re.getException();
                     if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                         // MRI behavior: swallow StandardErorrs
                         runtime.getGlobalVariables().clear("$!");
                     } else {
                         throw re;
                     }
                 }
             }
         }
 
         return io;
     }
 
     @JRubyMethod(required = 1, optional = 2, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject sysopen(IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject pathString = args[0].convertToString();
         return sysopenCommon(recv, args, block, pathString);
     }
     
     @JRubyMethod(name = "sysopen", required = 1, optional = 2, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject sysopen19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject pathString;
         if (!(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             pathString = args[0].callMethod(context, "to_path");
         } else {
             pathString = args[0].convertToString();
         }
         return sysopenCommon(recv, args, block, pathString);
     }
 
     private static IRubyObject sysopenCommon(IRubyObject recv, IRubyObject[] args, Block block, IRubyObject pathString) {
         Ruby runtime = recv.getRuntime();
         runtime.checkSafeString(pathString);
         String path = pathString.toString();
 
         ModeFlags modes = null;
         int perms = -1; // -1 == don't set permissions
         try {
             if (args.length > 1) {
                 IRubyObject modeString = args[1].convertToString();
                 modes = getIOModes(runtime, modeString.toString());
             } else {
                 modes = getIOModes(runtime, "r");
             }
             if (args.length > 2) {
                 RubyInteger permsInt =
                     args.length >= 3 ? args[2].convertToInteger() : null;
                 perms = RubyNumeric.fix2int(permsInt);
             }
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
 
         int fileno = -1;
         try {
             ChannelDescriptor descriptor =
                 ChannelDescriptor.open(runtime.getCurrentDirectory(),
                                        path, modes, perms, runtime.getPosix(),
                                        runtime.getJRubyClassLoader());
             // always a new fileno, so ok to use internal only
             fileno = descriptor.getFileno();
         }
         catch (FileNotFoundException fnfe) {
             throw runtime.newErrnoENOENTError(path);
         } catch (DirectoryAsFileException dafe) {
             throw runtime.newErrnoEISDirError(path);
         } catch (FileExistsException fee) {
             throw runtime.newErrnoEEXISTError(path);
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
         }
         return runtime.newFixnum(fileno);
     }
 
     public boolean isAutoclose() {
         return openFile.isAutoclose();
     }
 
     public void setAutoclose(boolean autoclose) {
         openFile.setAutoclose(autoclose);
     }
 
     @JRubyMethod
     public IRubyObject autoclose(ThreadContext context) {
         return context.runtime.newBoolean(isAutoclose());
     }
 
     @JRubyMethod(name = "autoclose=")
     public IRubyObject autoclose_set(ThreadContext context, IRubyObject autoclose) {
         setAutoclose(autoclose.isTrue());
         return context.nil;
     }
 
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
         if (isClosed()) throw getRuntime().newIOError("closed stream");
 
         Ruby runtime = getRuntime();
         if (getExternalEncoding(runtime) == USASCIIEncoding.INSTANCE) {
             externalEncoding = ASCIIEncoding.INSTANCE;
         }
         openFile.setBinmode();
         return this;
     }
 
     @JRubyMethod(name = "binmode?", compat = RUBY1_9)
     public IRubyObject op_binmode(ThreadContext context) {
         return RubyBoolean.newBoolean(context.getRuntime(), openFile.isBinmode());
     }
 
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         
         try {
             RubyString string = obj.asString();
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
             
             Stream writeStream = myOpenFile.getWriteStream();
             
             if (myOpenFile.isWriteBuffered()) {
                 runtime.getWarnings().warn(ID.SYSWRITE_BUFFERED_IO, "syswrite for buffered IO");
             }
             
             if (!writeStream.getDescriptor().isWritable()) {
                 myOpenFile.checkClosed(runtime);
             }
             
             context.getThread().beforeBlockingCall();
             int read = writeStream.getDescriptor().write(string.getByteList());
             
             if (read == -1) {
                 // TODO? I think this ends up propagating from normal Java exceptions
                 // sys_fail(openFile.getPath())
             }
             
             return runtime.newFixnum(read);
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
-        } catch (PipeException ex) {
-            throw runtime.newErrnoEPIPEError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newSystemCallError(e.getMessage());
         } finally {
             context.getThread().afterBlockingCall();
         }
     }
     
     @JRubyMethod(name = "write_nonblock", required = 1)
     public IRubyObject write_nonblock(ThreadContext context, IRubyObject obj) {
         // MRI behavior: always check whether the file is writable
         // or not, even if we are to write 0 bytes.
         OpenFile myOpenFile = getOpenFileChecked();
 
         try {
             myOpenFile.checkWritable(context.getRuntime());
             RubyString str = obj.asString();
             if (str.getByteList().length() == 0) {
                 return context.getRuntime().newFixnum(0);
             }
 
             if (myOpenFile.isWriteBuffered()) {
                 context.getRuntime().getWarnings().warn(ID.SYSWRITE_BUFFERED_IO, "write_nonblock for buffered IO");
             }
             int written = myOpenFile.getWriteStream().getDescriptor().write(str.getByteList());
             return context.getRuntime().newFixnum(written);
         } catch (IOException ex) {
             throw context.getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
-        }  catch (PipeException ex) {
-            throw context.getRuntime().newErrnoEPIPEError();
         }
     }
     
     /** io_write
      * 
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         
         runtime.secure(4);
         
         RubyString str = obj.asString();
 
         // TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
         
         if (str.getByteList().length() == 0) {
             return runtime.newFixnum(0);
         }
 
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
 
             context.getThread().beforeBlockingCall();
             int written = fwrite(str.getByteList());
 
             if (written == -1) {
                 // TODO: sys fail
             }
 
             // if not sync, we switch to write buffered mode
             if (!myOpenFile.isSync()) {
                 myOpenFile.setWriteBuffered();
             }
 
             return runtime.newFixnum(written);
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
-        } catch (PipeException ex) {
-            throw runtime.newErrnoEPIPEError();
         } finally {
             context.getThread().afterBlockingCall();
         }
     }
     
     private boolean waitWritable(Stream stream) {
         Channel ch = stream.getChannel();
         if (ch instanceof SelectableChannel) {
             getRuntime().getCurrentContext().getThread().select(ch, this, SelectionKey.OP_WRITE);
             return true;
         }
         return false;
     }
 
     private boolean waitReadable(Stream stream) {
         if (stream.readDataBuffered()) {
             return true;
         }
         Channel ch = stream.getChannel();
         if (ch instanceof SelectableChannel) {
             getRuntime().getCurrentContext().getThread().select(ch, this, SelectionKey.OP_READ);
             return true;
         }
         return false;
     }
 
     protected int fwrite(ByteList buffer) {
         int n, r, l, offset = 0;
         boolean eagain = false;
         Stream writeStream = openFile.getWriteStream();
 
         int len = buffer.length();
         
         if ((n = len) <= 0) return n;
 
         try {
             if (openFile.isSync()) {
                 openFile.fflush(writeStream);
 
                 // TODO: why is this guarded?
     //            if (!rb_thread_fd_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //            }
                
                 while(offset<len) {
                     l = n;
 
                     // TODO: Something about pipe buffer length here
 
                     r = writeStream.getDescriptor().write(buffer,offset,l);
 
                     if(r == len) {
                         return len; //Everything written
                     }
 
                     if (0 <= r) {
                         offset += r;
                         n -= r;
                         eagain = true;
                     }
 
                     if(eagain && waitWritable(writeStream)) {
                         openFile.checkClosed(getRuntime());
                         if(offset >= buffer.length()) {
                             return -1;
                         }
                         eagain = false;
                     } else {
                         return -1;
                     }
                 }
 
 
                 // TODO: all this stuff...some pipe logic, some async thread stuff
     //          retry:
     //            l = n;
     //            if (PIPE_BUF < l &&
     //                !rb_thread_critical &&
     //                !rb_thread_alone() &&
     //                wsplit_p(fptr)) {
     //                l = PIPE_BUF;
     //            }
     //            TRAP_BEG;
     //            r = write(fileno(f), RSTRING(str)->ptr+offset, l);
     //            TRAP_END;
     //            if (r == n) return len;
     //            if (0 <= r) {
     //                offset += r;
     //                n -= r;
     //                errno = EAGAIN;
     //            }
     //            if (rb_io_wait_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //                if (offset < RSTRING(str)->len)
     //                    goto retry;
     //            }
     //            return -1L;
             }
 
             // TODO: handle errors in buffered write by retrying until finished or file is closed
             return writeStream.fwrite(buffer);
     //        while (errno = 0, offset += (r = fwrite(RSTRING(str)->ptr+offset, 1, n, f)), (n -= r) > 0) {
     //            if (ferror(f)
     //            ) {
     //                if (rb_io_wait_writable(fileno(f))) {
     //                    rb_io_check_closed(fptr);
     //                    clearerr(f);
     //                    if (offset < RSTRING(str)->len)
     //                        continue;
     //                }
     //                return -1L;
     //            }
     //        }
 
 //            return len - n;
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     /** rb_io_addstr
      * 
      */
     @JRubyMethod(name = "<<", required = 1)
     public IRubyObject op_append(ThreadContext context, IRubyObject anObject) {
         // Claims conversion is done via 'to_s' in docs.
         callMethod(context, "write", anObject);
         
         return this; 
     }
 
     @JRubyMethod(name = "fileno", alias = "to_i")
     public RubyFixnum fileno(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         // map to external fileno
-        return runtime.newFixnum(runtime.getFileno(getOpenFileChecked().getMainStream().getDescriptor()));
+        try {
+            return runtime.newFixnum(runtime.getFileno(getOpenFileChecked().getMainStreamSafe().getDescriptor()));
+        } catch (BadDescriptorException e) {
+            throw runtime.newErrnoEBADFError();
+        }
     }
     
     /** Returns the current line number.
      * 
      * @return the current line number.
      */
     @JRubyMethod(name = "lineno")
     public RubyFixnum lineno(ThreadContext context) {
         return context.getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Sets the current line number.
      * 
      * @param newLineNumber The new line number.
      */
     @JRubyMethod(name = "lineno=", required = 1)
     public RubyFixnum lineno_set(ThreadContext context, IRubyObject newLineNumber) {
         getOpenFileChecked().setLineNumber(RubyNumeric.fix2int(newLineNumber));
 
         return context.getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Returns the current sync mode.
      * 
      * @return the current sync mode.
      */
     @JRubyMethod(name = "sync")
     public RubyBoolean sync(ThreadContext context) {
-        return context.getRuntime().newBoolean(getOpenFileChecked().getMainStream().isSync());
+        try {
+            return context.getRuntime().newBoolean(getOpenFileChecked().getMainStreamSafe().isSync());
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
+        }
     }
     
     /**
      * <p>Return the process id (pid) of the process this IO object
      * spawned.  If no process exists (popen was not called), then
      * nil is returned.  This is not how it appears to be defined
      * but ruby 1.8 works this way.</p>
      * 
      * @return the pid or nil
      */
     @JRubyMethod(name = "pid")
     public IRubyObject pid(ThreadContext context) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (myOpenFile.getProcess() == null) {
             return context.getRuntime().getNil();
         }
         
         // Of course this isn't particularly useful.
         long pid = myOpenFile.getPid();
         
         return context.getRuntime().newFixnum(pid); 
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos(ThreadContext context) {
         try {
-            return context.getRuntime().newFixnum(getOpenFileChecked().getMainStream().fgetpos());
+            return context.getRuntime().newFixnum(getOpenFileChecked().getMainStreamSafe().fgetpos());
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException bde) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
-            throw context.getRuntime().newIOError(e.getMessage());
+            throw context.getRuntime().newIOErrorFromException(e);
         }
     }
     
     @JRubyMethod(name = "pos=", required = 1)
     public RubyFixnum pos_set(ThreadContext context, IRubyObject newPosition) {
         long offset = RubyNumeric.num2long(newPosition);
 
         if (offset < 0) {
             throw context.getRuntime().newSystemCallError("Negative seek offset");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
-            myOpenFile.getMainStream().lseek(offset, Stream.SEEK_SET);
+            myOpenFile.getMainStreamSafe().lseek(offset, Stream.SEEK_SET);
+        
+            myOpenFile.getMainStreamSafe().clearerr();
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
-            throw context.getRuntime().newIOError(e.getMessage());
+            throw context.getRuntime().newIOErrorFromException(e);
         }
         
-        myOpenFile.getMainStream().clearerr();
-        
         return context.getRuntime().newFixnum(offset);
     }
     
     /** Print some objects to the stream.
      * 
      */
     @JRubyMethod(name = "print", rest = true, reads = FrameField.LASTLINE)
     public IRubyObject print(ThreadContext context, IRubyObject[] args) {
         return print(context, this, args);
     }
 
     /** Print some objects to the stream.
      *
      */
     public static IRubyObject print(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { context.getCurrentScope().getLastLine(context.getRuntime()) };
         }
 
         Ruby runtime = context.getRuntime();
         IRubyObject fs = runtime.getGlobalVariables().get("$,");
         IRubyObject rs = runtime.getGlobalVariables().get("$\\");
 
         for (int i = 0; i < args.length; i++) {
             if (i > 0 && !fs.isNil()) {
                 maybeIO.callMethod(context, "write", fs);
             }
             if (args[i].isNil()) {
                 maybeIO.callMethod(context, "write", runtime.newString("nil"));
             } else {
                 maybeIO.callMethod(context, "write", args[i]);
             }
         }
         if (!rs.isNil()) {
             maybeIO.callMethod(context, "write", rs);
         }
 
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "printf", required = 1, rest = true)
     public IRubyObject printf(ThreadContext context, IRubyObject[] args) {
         callMethod(context, "write", RubyKernel.sprintf(context, this, args));
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "putc", required = 1, backtrace = true)
     public IRubyObject putc(ThreadContext context, IRubyObject object) {
         return putc(context, this, object);
     }
 
     public static IRubyObject putc(ThreadContext context, IRubyObject maybeIO, IRubyObject object) {
         int c = RubyNumeric.num2chr(object);
         if (maybeIO instanceof RubyIO) {
             // FIXME we should probably still be dyncalling 'write' here
             RubyIO io = (RubyIO)maybeIO;
             try {
                 OpenFile myOpenFile = io.getOpenFileChecked();
                 myOpenFile.checkWritable(context.getRuntime());
                 Stream writeStream = myOpenFile.getWriteStream();
                 writeStream.fputc(c);
                 if (myOpenFile.isSync()) myOpenFile.fflush(writeStream);
             } catch (IOException ex) {
                 throw context.getRuntime().newIOErrorFromException(ex);
             } catch (BadDescriptorException e) {
                 throw context.getRuntime().newErrnoEBADFError();
             } catch (InvalidValueException ex) {
                 throw context.getRuntime().newErrnoEINVALError();
-            } catch (PipeException ex) {
-                throw context.getRuntime().newErrnoEPIPEError();
             }
         } else {
             maybeIO.callMethod(context, "write",
                     RubyString.newStringNoCopy(context.getRuntime(), new byte[] {(byte)c}));
         }
 
         return object;
     }
 
     public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         return doSeek(context, offset, whence);
     }
 
     @JRubyMethod(name = "seek")
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0) {
         long offset = RubyNumeric.num2long(arg0);
         int whence = Stream.SEEK_SET;
         
         return doSeek(context, offset, whence);
     }
 
     @JRubyMethod(name = "seek")
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         long offset = RubyNumeric.num2long(arg0);
         int whence = RubyNumeric.fix2int(arg1.convertToInteger());
         
         return doSeek(context, offset, whence);
     }
     
     private RubyFixnum doSeek(ThreadContext context, long offset, int whence) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.seek(offset, whence);
+        
+            myOpenFile.getMainStreamSafe().clearerr();
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
-            throw context.getRuntime().newIOError(e.getMessage());
+            throw context.getRuntime().newIOErrorFromException(e);
         }
         
-        myOpenFile.getMainStream().clearerr();
-        
         return RubyFixnum.zero(context.getRuntime());
     }
     
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "sysseek", required = 1, optional = 1)
     public RubyFixnum sysseek(ThreadContext context, IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         long pos;
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             
             if (myOpenFile.isReadable() && myOpenFile.isReadBuffered()) {
                 throw context.getRuntime().newIOError("sysseek for buffered IO");
             }
             if (myOpenFile.isWritable() && myOpenFile.isWriteBuffered()) {
                 context.getRuntime().getWarnings().warn(ID.SYSSEEK_BUFFERED_IO, "sysseek for buffered IO");
             }
             
-            pos = myOpenFile.getMainStream().getDescriptor().lseek(offset, whence);
+            pos = myOpenFile.getMainStreamSafe().getDescriptor().lseek(offset, whence);
+        
+            myOpenFile.getMainStreamSafe().clearerr();
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
-            throw context.getRuntime().newIOError(e.getMessage());
+            throw context.getRuntime().newIOErrorFromException(e);
         }
         
-        myOpenFile.getMainStream().clearerr();
-        
         return context.getRuntime().newFixnum(pos);
     }
 
     @JRubyMethod(name = "rewind")
     public RubyFixnum rewind(ThreadContext context) {
         OpenFile myOpenfile = getOpenFileChecked();
         
         try {
-            myOpenfile.getMainStream().lseek(0L, Stream.SEEK_SET);
-            myOpenfile.getMainStream().clearerr();
+            myOpenfile.getMainStreamSafe().lseek(0L, Stream.SEEK_SET);
+            myOpenfile.getMainStreamSafe().clearerr();
             
             // TODO: This is some goofy global file value from MRI..what to do?
 //            if (io == current_file) {
 //                gets_lineno -= fptr->lineno;
 //            }
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
-            throw context.getRuntime().newIOError(e.getMessage());
+            throw context.getRuntime().newIOErrorFromException(e);
         }
 
         // Must be back on first line on rewind.
         myOpenfile.setLineNumber(0);
         
         return RubyFixnum.zero(context.getRuntime());
     }
     
     @JRubyMethod(name = "fsync")
     public RubyFixnum fsync(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
         
             Stream writeStream = myOpenFile.getWriteStream();
 
             writeStream.fflush();
             writeStream.sync();
 
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
-        } catch (PipeException ex) {
-            throw runtime.newErrnoEPIPEError();
         } catch (IOException e) {
-            throw runtime.newIOError(e.getMessage());
+            throw runtime.newIOErrorFromException(e);
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         }
 
         return RubyFixnum.zero(runtime);
     }
 
     /** Sets the current sync mode.
      * 
      * @param newSync The new sync mode.
      */
     @JRubyMethod(name = "sync=", required = 1)
     public IRubyObject sync_set(IRubyObject newSync) {
-        getOpenFileChecked().setSync(newSync.isTrue());
-        getOpenFileChecked().getMainStream().setSync(newSync.isTrue());
+        try {
+            getOpenFileChecked().setSync(newSync.isTrue());
+            getOpenFileChecked().getMainStreamSafe().setSync(newSync.isTrue());
+        } catch (BadDescriptorException e) {
+            throw getRuntime().newErrnoEBADFError();
+        }
 
         return this;
     }
 
     @JRubyMethod(name = {"eof?", "eof"})
     public RubyBoolean eof_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
-            if (myOpenFile.getMainStream().feof()) {
+            if (myOpenFile.getMainStreamSafe().feof()) {
                 return runtime.getTrue();
             }
             
-            if (myOpenFile.getMainStream().readDataBuffered()) {
+            if (myOpenFile.getMainStreamSafe().readDataBuffered()) {
                 return runtime.getFalse();
             }
             
-            readCheck(myOpenFile.getMainStream());
-            waitReadable(myOpenFile.getMainStream());
+            readCheck(myOpenFile.getMainStreamSafe());
+            waitReadable(myOpenFile.getMainStreamSafe());
             
-            myOpenFile.getMainStream().clearerr();
+            myOpenFile.getMainStreamSafe().clearerr();
             
-            int c = myOpenFile.getMainStream().fgetc();
+            int c = myOpenFile.getMainStreamSafe().fgetc();
             
             if (c != -1) {
-                myOpenFile.getMainStream().ungetc(c);
+                myOpenFile.getMainStreamSafe().ungetc(c);
                 return runtime.getFalse();
             }
             
             myOpenFile.checkClosed(runtime);
             
-            myOpenFile.getMainStream().clearerr();
+            myOpenFile.getMainStreamSafe().clearerr();
             
             return runtime.getTrue();
-        } catch (PipeException ex) {
-            throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
-            throw runtime.newIOError(e.getMessage());
+            throw runtime.newIOErrorFromException(e);
         }
     }
 
     @JRubyMethod(name = {"tty?", "isatty"})
     public RubyBoolean tty_p(ThreadContext context) {
-        return context.getRuntime().newBoolean(
-                context.getRuntime().getPosix().isatty(
-                getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor()));
+        try {
+            return context.getRuntime().newBoolean(
+                    context.getRuntime().getPosix().isatty(
+                    getOpenFileChecked().getMainStreamSafe().getDescriptor().getFileDescriptor()));
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
+        }
     }
     
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         Ruby runtime = getRuntime();
         
         if (this == original) return this;
 
         RubyIO originalIO = (RubyIO) TypeConverter.convertToTypeWithCheck(original, runtime.getIO(), "to_io");
         
         OpenFile originalFile = originalIO.getOpenFileChecked();
         OpenFile newFile = openFile;
         
         try {
             if (originalFile.getPipeStream() != null) {
                 originalFile.getPipeStream().fflush();
-                originalFile.getMainStream().lseek(0, Stream.SEEK_CUR);
+                originalFile.getMainStreamSafe().lseek(0, Stream.SEEK_CUR);
             } else if (originalFile.isWritable()) {
-                originalFile.getMainStream().fflush();
+                originalFile.getMainStreamSafe().fflush();
             } else {
-                originalFile.getMainStream().lseek(0, Stream.SEEK_CUR);
+                originalFile.getMainStreamSafe().lseek(0, Stream.SEEK_CUR);
             }
 
             newFile.setMode(originalFile.getMode());
             newFile.setProcess(originalFile.getProcess());
             newFile.setLineNumber(originalFile.getLineNumber());
             newFile.setPath(originalFile.getPath());
             newFile.setFinalizer(originalFile.getFinalizer());
             
             ModeFlags modes;
             if (newFile.isReadable()) {
                 if (newFile.isWritable()) {
                     if (newFile.getPipeStream() != null) {
                         modes = new ModeFlags(ModeFlags.RDONLY);
                     } else {
                         modes = new ModeFlags(ModeFlags.RDWR);
                     }
                 } else {
                     modes = new ModeFlags(ModeFlags.RDONLY);
                 }
             } else {
                 if (newFile.isWritable()) {
                     modes = new ModeFlags(ModeFlags.WRONLY);
                 } else {
-                    modes = originalFile.getMainStream().getModes();
+                    modes = originalFile.getMainStreamSafe().getModes();
                 }
             }
             
-            ChannelDescriptor descriptor = originalFile.getMainStream().getDescriptor().dup();
+            ChannelDescriptor descriptor = originalFile.getMainStreamSafe().getDescriptor().dup();
 
             newFile.setMainStream(ChannelStream.fdopen(runtime, descriptor, modes));
 
-            newFile.getMainStream().setSync(originalFile.getMainStream().isSync());
+            newFile.getMainStream().setSync(originalFile.getMainStreamSafe().isSync());
             
             // TODO: the rest of this...seeking to same position is unnecessary since we share a channel
             // but some of this may be needed?
             
 //    fseeko(fptr->f, ftello(orig->f), SEEK_SET);
 //    if (orig->f2) {
 //	if (fileno(orig->f) != fileno(orig->f2)) {
 //	    fd = ruby_dup(fileno(orig->f2));
 //	}
 //	fptr->f2 = rb_fdopen(fd, "w");
 //	fseeko(fptr->f2, ftello(orig->f2), SEEK_SET);
 //    }
 //    if (fptr->mode & FMODE_BINMODE) {
 //	rb_io_binmode(dest);
 //    }
         } catch (IOException ex) {
-            throw runtime.newIOError("could not init copy: " + ex);
+            throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         } catch (PipeException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         } catch (InvalidValueException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         }
         
         return this;
     }
     
     @JRubyMethod(name = "closed?")
     public RubyBoolean closed_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isClosed());
     }
 
     /**
      * Is this IO closed
      * 
      * @return true if closed
      */
     public boolean isClosed() {
         return openFile.getMainStream() == null && openFile.getPipeStream() == null;
     }
 
     /** 
      * <p>Closes all open resources for the IO.  It also removes
      * it from our magical all open file descriptor pool.</p>
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         Ruby runtime = getRuntime();
         
         if (runtime.getSafeLevel() >= 4 && isTaint()) {
             throw runtime.newSecurityError("Insecure: can't close");
         }
         
         openFile.checkClosed(runtime);
         return close2(runtime);
     }
         
     protected IRubyObject close2(Ruby runtime) {
         if (openFile == null) return runtime.getNil();
         
         interruptBlockingThreads();
 
         /* FIXME: Why did we go to this trouble and not use these descriptors?
         ChannelDescriptor main, pipe;
         if (openFile.getPipeStream() != null) {
             pipe = openFile.getPipeStream().getDescriptor();
         } else {
             if (openFile.getMainStream() == null) {
                 return runtime.getNil();
             }
             pipe = null;
         }
         
         main = openFile.getMainStream().getDescriptor(); */
         
         // cleanup, raising errors if any
         openFile.cleanup(runtime, true);
         
         // TODO: notify threads waiting on descriptors/IO? probably not...
         
         if (openFile.getProcess() != null) {
             obliterateProcess(openFile.getProcess());
             IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(runtime, openFile.getProcess().exitValue());
             runtime.getCurrentContext().setLastExitStatus(processResult);
         }
         
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "close_write")
     public IRubyObject close_write(ThreadContext context) {
         try {
             if (context.getRuntime().getSafeLevel() >= 4 && isTaint()) {
                 throw context.getRuntime().newSecurityError("Insecure: can't close");
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isReadable()) {
                 throw context.getRuntime().newIOError("closing non-duplex IO for writing");
             }
             
             if (myOpenFile.getPipeStream() == null) {
                 close();
             } else{
                 myOpenFile.getPipeStream().fclose();
                 myOpenFile.setPipeStream(null);
                 myOpenFile.setMode(myOpenFile.getMode() & ~OpenFile.WRITABLE);
                 // TODO
                 // n is result of fclose; but perhaps having a SysError below is enough?
                 // if (n != 0) rb_sys_fail(fptr->path);
             }
         } catch (BadDescriptorException bde) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (IOException ioe) {
             // hmmmm
         }
         return this;
     }
 
     @JRubyMethod(name = "close_read")
     public IRubyObject close_read(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         try {
             if (runtime.getSafeLevel() >= 4 && isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't close");
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isWritable()) {
                 throw runtime.newIOError("closing non-duplex IO for reading");
             }
             
             if (myOpenFile.getPipeStream() == null) {
                 close();
             } else{
-                myOpenFile.getMainStream().fclose();
+                myOpenFile.getMainStreamSafe().fclose();
                 myOpenFile.setMode(myOpenFile.getMode() & ~OpenFile.READABLE);
                 myOpenFile.setMainStream(myOpenFile.getPipeStream());
                 myOpenFile.setPipeStream(null);
                 // TODO
                 // n is result of fclose; but perhaps having a SysError below is enough?
                 // if (n != 0) rb_sys_fail(fptr->path);
             }
         } catch (BadDescriptorException bde) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException ioe) {
             // I believe Ruby bails out with a "bug" if closing fails
             throw runtime.newIOErrorFromException(ioe);
         }
         return this;
     }
 
     /** Flushes the IO output stream.
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "flush")
     public RubyIO flush() {
         try { 
             getOpenFileChecked().getWriteStream().fflush();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
-            throw getRuntime().newIOError(e.getMessage());
+            throw getRuntime().newIOErrorFromException(e);
         }
 
         return this;
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_8)
     public IRubyObject gets(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         IRubyObject result = getline(runtime, separator(runtime.getRecordSeparatorVar().get()));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_8)
     public IRubyObject gets(ThreadContext context, IRubyObject separatorArg) {
         IRubyObject result = getline(context.getRuntime(), separator(separatorArg));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         IRubyObject result = getline(runtime, separator(runtime));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context, IRubyObject arg) {
         ByteList separator;
         long limit = -1;
         if (arg instanceof RubyInteger) {
             limit = RubyInteger.fix2long(arg);
             separator = separator(context.getRuntime());
         } else {
             separator = separator(arg);
         }
 
         IRubyObject result = getline(context.getRuntime(), separator, limit);
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context, IRubyObject separator, IRubyObject limit_arg) {
         long limit = limit_arg.isNil() ? -1 : RubyNumeric.fix2long(limit_arg);
         IRubyObject result = getline(context.getRuntime(), separator(separator), limit);
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     public boolean getBlocking() {
-        return ((ChannelStream) openFile.getMainStream()).isBlocking();
+        try {
+            return ((ChannelStream) openFile.getMainStreamSafe()).isBlocking();
+        } catch (BadDescriptorException e) {
+            throw getRuntime().newErrnoEBADFError();
+        }
     }
 
     @JRubyMethod(name = "fcntl")
     public IRubyObject fcntl(ThreadContext context, IRubyObject cmd) {
         // TODO: This version differs from ioctl by checking whether fcntl exists
         // and raising notimplemented if it doesn't; perhaps no difference for us?
         return ctl(context.getRuntime(), cmd, null);
     }
 
     @JRubyMethod(name = "fcntl")
     public IRubyObject fcntl(ThreadContext context, IRubyObject cmd, IRubyObject arg) {
         // TODO: This version differs from ioctl by checking whether fcntl exists
         // and raising notimplemented if it doesn't; perhaps no difference for us?
         return ctl(context.getRuntime(), cmd, arg);
     }
 
     @JRubyMethod(name = "ioctl", required = 1, optional = 1)
     public IRubyObject ioctl(ThreadContext context, IRubyObject[] args) {
         IRubyObject cmd = args[0];
         IRubyObject arg;
         
         if (args.length == 2) {
             arg = args[1];
         } else {
             arg = context.getRuntime().getNil();
         }
         
         return ctl(context.getRuntime(), cmd, arg);
     }
 
     public IRubyObject ctl(Ruby runtime, IRubyObject cmd, IRubyObject arg) {
         long realCmd = cmd.convertToInteger().getLongValue();
         long nArg = 0;
         
         // FIXME: Arg may also be true, false, and nil and still be valid.  Strangely enough, 
         // protocol conversion is not happening in Ruby on this arg?
         if (arg == null || arg.isNil() || arg == runtime.getFalse()) {
             nArg = 0;
         } else if (arg instanceof RubyFixnum) {
             nArg = RubyFixnum.fix2long(arg);
         } else if (arg == runtime.getTrue()) {
             nArg = 1;
         } else {
             throw runtime.newNotImplementedError("JRuby does not support string for second fcntl/ioctl argument yet");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
 
         // Fixme: Only F_SETFL and F_GETFL is current supported
         // FIXME: Only NONBLOCK flag is supported
         // FIXME: F_SETFL and F_SETFD are treated as the same thing here.  For the case of dup(fd) we
         //   should actually have F_SETFL only affect one (it is unclear how well we do, but this TODO
         //   is here to at least document that we might need to do more work here.  Mostly SETFL is
         //   for mode changes which should persist across fork() boundaries.  Since JVM has no fork
         //   this is not a problem for us.
-        if (realCmd == FcntlLibrary.FD_CLOEXEC) {
-            // Do nothing.  FD_CLOEXEC has no meaning in JVM since we cannot really exec.
-            // And why the hell does webrick pass this in as a first argument!!!!!
-        } else if (realCmd == Fcntl.F_SETFL.value() || realCmd == Fcntl.F_SETFD.value()) {
-            if ((nArg & FcntlLibrary.FD_CLOEXEC) == FcntlLibrary.FD_CLOEXEC) {
+        try {
+            if (realCmd == FcntlLibrary.FD_CLOEXEC) {
                 // Do nothing.  FD_CLOEXEC has no meaning in JVM since we cannot really exec.
-            } else {
-                try {
+                // And why the hell does webrick pass this in as a first argument!!!!!
+            } else if (realCmd == Fcntl.F_SETFL.value() || realCmd == Fcntl.F_SETFD.value()) {
+                if ((nArg & FcntlLibrary.FD_CLOEXEC) == FcntlLibrary.FD_CLOEXEC) {
+                    // Do nothing.  FD_CLOEXEC has no meaning in JVM since we cannot really exec.
+                } else {
                     boolean block = (nArg & ModeFlags.NONBLOCK) != ModeFlags.NONBLOCK;
 
-                    myOpenFile.getMainStream().setBlocking(block);
-                } catch (IOException e) {
-                    throw runtime.newIOError(e.getMessage());
+                    myOpenFile.getMainStreamSafe().setBlocking(block);
                 }
+            } else if (realCmd == Fcntl.F_GETFL.value()) {
+                return myOpenFile.getMainStreamSafe().isBlocking() ? RubyFixnum.zero(runtime) : RubyFixnum.newFixnum(runtime, ModeFlags.NONBLOCK);
+            } else {
+                throw runtime.newNotImplementedError("JRuby only supports F_SETFL and F_GETFL with NONBLOCK for fcntl/ioctl");
             }
-        } else if (realCmd == Fcntl.F_GETFL.value()) {
-            return myOpenFile.getMainStream().isBlocking() ? RubyFixnum.zero(runtime) : RubyFixnum.newFixnum(runtime, ModeFlags.NONBLOCK);
-        } else {
-            throw runtime.newNotImplementedError("JRuby only supports F_SETFL and F_GETFL with NONBLOCK for fcntl/ioctl");
+        } catch (BadDescriptorException e) {
+            throw runtime.newErrnoEBADFError();
+        } catch (IOException e) {
+            throw runtime.newIOErrorFromException(e);
         }
         
         return runtime.newFixnum(0);
     }
     
     private static final ByteList NIL_BYTELIST = ByteList.create("nil");
     private static final ByteList RECURSIVE_BYTELIST = ByteList.create("[...]");
 
     @JRubyMethod(name = "puts", rest = true)
     public IRubyObject puts(ThreadContext context, IRubyObject[] args) {
         return puts(context, this, args);
     }
 
     public static IRubyObject puts(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         if (args.length == 0) {
             return writeSeparator(context, maybeIO);
         }
 
         return putsArray(context, maybeIO, args);
     }
 
     private static IRubyObject writeSeparator(ThreadContext context, IRubyObject maybeIO) {
         Ruby runtime = context.getRuntime();
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         write(context, maybeIO, separator.getByteList());
         return runtime.getNil();
     }
 
     private static IRubyObject putsArray(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         for (int i = 0; i < args.length; i++) {
             ByteList line;
 
             if (args[i].isNil()) {
                 line = getNilByteList(runtime);
             } else if (runtime.isInspecting(args[i])) {
                 line = RECURSIVE_BYTELIST;
             } else if (args[i] instanceof RubyArray) {
                 inspectPuts(context, maybeIO, (RubyArray) args[i]);
                 continue;
             } else {
                 line = args[i].asString().getByteList();
             }
 
             write(context, maybeIO, line);
 
             if (line.length() == 0 || !line.endsWith(separator.getByteList())) {
                 write(context, maybeIO, separator.getByteList());
             }
         }
         return runtime.getNil();
     }
 
     protected void write(ThreadContext context, ByteList byteList) {
         callMethod(context, "write", RubyString.newStringShared(context.getRuntime(), byteList));
     }
 
     protected static void write(ThreadContext context, IRubyObject maybeIO, ByteList byteList) {
         maybeIO.callMethod(context, "write", RubyString.newStringShared(context.getRuntime(), byteList));
     }
 
     private static IRubyObject inspectPuts(ThreadContext context, IRubyObject maybeIO, RubyArray array) {
         try {
             context.getRuntime().registerInspecting(array);
             return putsArray(context, maybeIO, array.toJavaArray());
         } finally {
             context.getRuntime().unregisterInspecting(array);
         }
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "readline", writes = FrameField.LASTLINE)
     public IRubyObject readline(ThreadContext context) {
         IRubyObject line = gets(context);
 
         if (line.isNil()) throw context.getRuntime().newEOFError();
         
         return line;
     }
 
     @JRubyMethod(name = "readline", writes = FrameField.LASTLINE)
     public IRubyObject readline(ThreadContext context, IRubyObject separator) {
         IRubyObject line = gets(context, separator);
 
         if (line.isNil()) throw context.getRuntime().newEOFError();
 
         return line;
     }
 
     /** Read a byte. On EOF returns nil.
      * 
      */
     @JRubyMethod(name = {"getc", "getbyte"}, compat = RUBY1_8)
     public IRubyObject getc() {
         int c = getcCommon();
 
         if (c == -1) {
             // CRuby checks ferror(f) and retry getc for non-blocking IO
             // read. We checks readability first if possible so retry should
             // not be needed I believe.
             return getRuntime().getNil();
         }
 
         return getRuntime().newFixnum(c);
     }
 
     private ByteList fromEncodedBytes(Ruby runtime, Encoding enc, int value) {
         int n;
         try {
             n = value < 0 ? 0 : enc.codeToMbcLength(value);
         } catch (EncodingException ee) {
             n = 0;
         }
 
         if (n <= 0) throw runtime.newRangeError(this.toString() + " out of char range");
 
         ByteList bytes = new ByteList(n);
         enc.codeToMbc(value, bytes.getUnsafeBytes(), 0);
         bytes.setRealSize(n);
         return bytes;
     }
     
     @JRubyMethod(name = "readchar", compat = RUBY1_9)
     public IRubyObject readchar19(ThreadContext context) {
         IRubyObject value = getc19(context);
         
         if (value.isNil()) throw context.getRuntime().newEOFError();
         
         return value;
     }
 
     @JRubyMethod(name = "getbyte", compat = RUBY1_9)
     public IRubyObject getbyte19(ThreadContext context) {
         return getc(); // Yes 1.8 getc is 1.9 getbyte
     }
 
     @JRubyMethod(name = "getc", compat = RUBY1_9)
     public IRubyObject getc19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         int c = getcCommon();
 
         if (c == -1) {
             // CRuby checks ferror(f) and retry getc for non-blocking IO
             // read. We checks readability first if possible so retry should
             // not be needed I believe.
             return runtime.getNil();
         }
 
         Encoding enc = getExternalEncoding(runtime);
         // TODO: This should be optimized like RubyInteger.chr is for ascii values
         return RubyString.newStringNoCopy(runtime, fromEncodedBytes(runtime, enc, (int) c), enc, 0);
     }
 
     public int getcCommon() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(getRuntime());
             myOpenFile.setReadBuffered();
 
-            Stream stream = myOpenFile.getMainStream();
+            Stream stream = myOpenFile.getMainStreamSafe();
             
             readCheck(stream);
             waitReadable(stream);
             stream.clearerr();
             
-            return myOpenFile.getMainStream().fgetc();
-        } catch (PipeException ex) {
-            throw getRuntime().newErrnoEPIPEError();
+            return myOpenFile.getMainStreamSafe().fgetc();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
         } catch (IOException e) {
-            throw getRuntime().newIOError(e.getMessage());
+            throw getRuntime().newIOErrorFromException(e);
         }
     }
     
     private void readCheck(Stream stream) {
         if (!stream.readDataBuffered()) {
             openFile.checkClosed(getRuntime());
         }
     }
     
     /** 
      * <p>Pushes char represented by int back onto IOS.</p>
      * 
      * @param number to push back
      */
     @JRubyMethod(name = "ungetc", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject ungetc(IRubyObject number) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (!myOpenFile.isReadBuffered()) {
             throw getRuntime().newIOError("unread stream");
         }
 
         return ungetcCommon(number, myOpenFile);
     }
 
     @JRubyMethod(name = "ungetc", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject ungetc19(IRubyObject number) {
         Ruby runtime = getRuntime();
         OpenFile myOpenFile = getOpenFileChecked();
 
         if (!myOpenFile.isReadBuffered()) {
             return runtime.getNil();
         }
 
         if (number instanceof RubyString) {
             RubyString str = (RubyString) number;
             if (str.isEmpty()) return runtime.getNil();
 
             int c =  str.getEncoding().mbcToCode(str.getBytes(), 0, 1);
             number = runtime.newFixnum(c);
         }
 
         return ungetcCommon(number, myOpenFile);
     }
 
     private IRubyObject ungetcCommon(IRubyObject number, OpenFile myOpenFile) {
         int ch = RubyNumeric.fix2int(number);
 
         try {
             myOpenFile.checkReadable(getRuntime());
             myOpenFile.setReadBuffered();
 
-            if (myOpenFile.getMainStream().ungetc(ch) == -1 && ch != -1) {
+            if (myOpenFile.getMainStreamSafe().ungetc(ch) == -1 && ch != -1) {
                 throw getRuntime().newIOError("ungetc failed");
             }
-        } catch (PipeException ex) {
-            throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
         } catch (IOException e) {
-            throw getRuntime().newIOError(e.getMessage());
+            throw getRuntime().newIOErrorFromException(e);
         }
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "read_nonblock", required = 1, optional = 1, backtrace = true)
     public IRubyObject read_nonblock(ThreadContext context, IRubyObject[] args) {
         IRubyObject value = getPartial(context, args, true);
 
         if (value.isNil()) throw context.getRuntime().newEOFError();
 
         if (value instanceof RubyString) {
             RubyString str = (RubyString) value;
             if (str.isEmpty()) {
                 Ruby ruby = context.getRuntime();
                 RaiseException eagain = ruby.newErrnoEAGAINError("");
 
                 // FIXME: *oif* 1.9 actually does this
                 if (ruby.is1_9()) {
                     eagain.getException().extend(new IRubyObject[] {ruby.getIO().getConstant("WaitReadable")});
                 }
 
                 throw eagain;
             }
         }
 
         return value;
     }
 
     @JRubyMethod(name = "readpartial", required = 1, optional = 1)
     public IRubyObject readpartial(ThreadContext context, IRubyObject[] args) {
         IRubyObject value = getPartial(context, args, false);
 
         if (value.isNil()) throw context.getRuntime().newEOFError();
 
         return value;
     }
 
     // implements io_getpartial in io.c
     private IRubyObject getPartial(ThreadContext context, IRubyObject[] args, boolean isNonblocking) {
         Ruby runtime = context.getRuntime();
 
         // Length to read
         int length = RubyNumeric.fix2int(args[0]);
         if (length < 0) throw runtime.newArgumentError("negative length " + length + " given");
 
         // String/Buffer to read it into
         IRubyObject stringArg = args.length > 1 ? args[1] : runtime.getNil();
         RubyString string = stringArg.isNil() ? RubyString.newEmptyString(runtime) : stringArg.convertToString();
         string.empty();
         string.setTaint(true);
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             myOpenFile.checkReadable(runtime);
             
             if (length == 0) {
                 return string;
             }
 
-            if (!(myOpenFile.getMainStream() instanceof ChannelStream)) { // cryptic for the uninitiated...
+            if (!(myOpenFile.getMainStreamSafe() instanceof ChannelStream)) { // cryptic for the uninitiated...
                 throw runtime.newNotImplementedError("readpartial only works with Nio based handlers");
             }
-            ChannelStream stream = (ChannelStream) myOpenFile.getMainStream();
+            ChannelStream stream = (ChannelStream) myOpenFile.getMainStreamSafe();
 
             // We don't check RubyString modification since JRuby doesn't have
             // GIL. Other threads are free to change anytime.
 
             ByteList buf = null;
             if (isNonblocking) {
                 buf = stream.readnonblock(length);
             } else {
                 while ((buf == null || buf.length() == 0) && !stream.feof()) {
                     waitReadable(stream);
                     buf = stream.readpartial(length);
                 }
             }
             boolean empty = buf == null || buf.length() == 0;
 
             ByteList newBuf = empty ? ByteList.EMPTY_BYTELIST.dup() : buf;
             if (externalEncoding != null) { // TODO: Encapsulate into something more central (when adding trancoding)
                 newBuf.setEncoding(externalEncoding);
             }
             string.view(newBuf);
 
             if (stream.feof() && empty) return runtime.getNil();
 
             return string;
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
-        } catch (PipeException e) {
-            throw getRuntime().newErrnoEPIPEError();
         } catch (EOFException e) {
             throw runtime.newEOFError(e.getMessage());
         } catch (IOException e) {
-            throw runtime.newIOError(e.getMessage());
+            throw runtime.newIOErrorFromException(e);
         }
     }
 
     @JRubyMethod(name = "sysread", required = 1, optional = 1)
     public IRubyObject sysread(ThreadContext context, IRubyObject[] args) {
         int len = (int)RubyNumeric.num2long(args[0]);
         if (len < 0) throw getRuntime().newArgumentError("Negative size");
 
         try {
             RubyString str;
             ByteList buffer;
             if (args.length == 1 || args[1].isNil()) {
                 if (len == 0) {
                     return RubyString.newEmptyString(getRuntime());
                 }
                 
                 buffer = new ByteList(len);
                 str = RubyString.newString(getRuntime(), buffer);
             } else {
                 str = args[1].convertToString();
                 str.modify(len);
                 
                 if (len == 0) {
                     return str;
                 }
                 
                 buffer = str.getByteList();
                 buffer.length(0);
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
             
-            if (myOpenFile.getMainStream().readDataBuffered()) {
+            if (myOpenFile.getMainStreamSafe().readDataBuffered()) {
                 throw getRuntime().newIOError("sysread for buffered IO");
             }
             
             // TODO: Ruby locks the string here
             
-            waitReadable(myOpenFile.getMainStream());
+            waitReadable(myOpenFile.getMainStreamSafe());
             myOpenFile.checkClosed(getRuntime());
 
             // We don't check RubyString modification since JRuby doesn't have
             // GIL. Other threads are free to change anytime.
 
-            int bytesRead = myOpenFile.getMainStream().getDescriptor().read(len, str.getByteList());
+            int bytesRead = myOpenFile.getMainStreamSafe().getDescriptor().read(len, str.getByteList());
             
             // TODO: Ruby unlocks the string here
 
             if (bytesRead == -1 || (bytesRead == 0 && len > 0)) {
                 throw getRuntime().newEOFError();
             }
             
             str.setTaint(true);
             
             return str;
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
-        } catch (PipeException e) {
-            throw getRuntime().newErrnoEPIPEError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
     	} catch (IOException e) {
             synthesizeSystemCallError(e);
             return null;
         }
     }
 
     /**
      * Java does not give us enough information for specific error conditions
      * so we are reduced to divining them through string matches...
      */
     // TODO: Should ECONNABORTED get thrown earlier in the descriptor itself or is it ok to handle this late?
     // TODO: Should we include this into Errno code somewhere do we can use this from other places as well?
     private void synthesizeSystemCallError(IOException e) {
         String errorMessage = e.getMessage();
         // All errors to sysread should be SystemCallErrors, but on a closed stream
         // Ruby returns an IOError.  Java throws same exception for all errors so
         // we resort to this hack...
         if ("File not open".equals(errorMessage)) {
             throw getRuntime().newIOError(e.getMessage());
         } else if ("An established connection was aborted by the software in your host machine".equals(errorMessage)) {
             throw getRuntime().newErrnoECONNABORTEDError();
         }
 
         throw getRuntime().newSystemCallError(e.getMessage());
     }
     
     public IRubyObject read(IRubyObject[] args) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         switch (args.length) {
         case 0: return read(context);
         case 1: return read(context, args[0]);
         case 2: return read(context, args[0], args[1]);
         default: throw getRuntime().newArgumentError(args.length, 2);
         }
     }
     
     @JRubyMethod(name = "read")
     public IRubyObject read(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
             return readAll();
-        } catch (PipeException ex) {
-            throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (EOFException ex) {
             throw getRuntime().newEOFError();
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
     
     @JRubyMethod(name = "read")
     public IRubyObject read(ThreadContext context, IRubyObject arg0) {
         if (arg0.isNil()) {
             return read(context);
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         int length = RubyNumeric.num2int(arg0);
         
         if (length < 0) {
             throw getRuntime().newArgumentError("negative length " + length + " given");
         }
         
         RubyString str = RubyString.newEmptyString(getRuntime());
 
         return readNotAll(context, myOpenFile, length, str);
     }
     
     @JRubyMethod(name = "read")
     public IRubyObject read(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (arg0.isNil()) {
             try {
                 myOpenFile.checkReadable(getRuntime());
                 myOpenFile.setReadBuffered();
                 if (arg1.isNil()) {
                     return readAll();
                 } else {
                     return readAll(arg1.convertToString());
                 }
-            } catch (PipeException ex) {
-                throw getRuntime().newErrnoEPIPEError();
             } catch (InvalidValueException ex) {
                 throw getRuntime().newErrnoEINVALError();
             } catch (EOFException ex) {
                 throw getRuntime().newEOFError();
             } catch (IOException ex) {
                 throw getRuntime().newIOErrorFromException(ex);
             } catch (BadDescriptorException ex) {
                 throw getRuntime().newErrnoEBADFError();
             }
         }
         
         int length = RubyNumeric.num2int(arg0);
         
         if (length < 0) {
             throw getRuntime().newArgumentError("negative length " + length + " given");
         }
 
         if (arg1.isNil()) {
             return readNotAll(context, myOpenFile, length);
         } else {
             // this readNotAll empties the string for us
             return readNotAll(context, myOpenFile, length, arg1.convertToString());
         }
     }
     
     // implements latter part of io_read in io.c
     private IRubyObject readNotAll(ThreadContext context, OpenFile myOpenFile, int length, RubyString str) {
         Ruby runtime = context.getRuntime();
         str.empty();
 
         try {
             ByteList newBuffer = readNotAllCommon(context, myOpenFile, length);
 
             if (emptyBufferOrEOF(newBuffer, myOpenFile)) {
                 return runtime.getNil();
             }
 
             str.setValue(newBuffer);
             str.setTaint(true);
 
             return str;
         } catch (EOFException ex) {
             throw runtime.newEOFError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         }
     }
 
     // implements latter part of io_read in io.c
     private IRubyObject readNotAll(ThreadContext context, OpenFile myOpenFile, int length) {
         Ruby runtime = context.getRuntime();
 
         try {
             ByteList newBuffer = readNotAllCommon(context, myOpenFile, length);
 
             if (emptyBufferOrEOF(newBuffer, myOpenFile)) {
                 return runtime.getNil();
             }
 
             RubyString str = RubyString.newString(runtime, newBuffer);
             str.setTaint(true);
 
             return str;
         } catch (EOFException ex) {
             throw runtime.newEOFError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         }
     }
 
     private ByteList readNotAllCommon(ThreadContext context, OpenFile myOpenFile, int length) {
         Ruby runtime = context.getRuntime();
 
         try {
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
-            if (myOpenFile.getMainStream().feof()) {
+            if (myOpenFile.getMainStreamSafe().feof()) {
                 return null;
             }
 
             // READ_CHECK from MRI io.c
-            readCheck(myOpenFile.getMainStream());
+            readCheck(myOpenFile.getMainStreamSafe());
 
             ByteList newBuffer = fread(context.getThread(), length);
 
             return newBuffer;
         } catch (EOFException ex) {
             throw runtime.newEOFError();
-        } catch (PipeException ex) {
-            throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         }
     }
 
     protected static boolean emptyBufferOrEOF(ByteList buffer, OpenFile myOpenFile) throws BadDescriptorException, IOException {
         if (buffer == null) {
             return true;
         } else if (buffer.length() == 0) {
-            if (myOpenFile.getMainStream() == null) {
+            if (myOpenFile.getMainStreamSafe() == null) {
                 return true;
             }
 
-            if (myOpenFile.getMainStream().feof()) {
+            if (myOpenFile.getMainStreamSafe().feof()) {
                 return true;
             }
         }
         return false;
     }
     
     // implements read_all() in io.c
     protected RubyString readAll(RubyString str) throws BadDescriptorException, EOFException, IOException {
         Ruby runtime = getRuntime();
 
         // TODO: handle writing into original buffer better
         ByteList buf = readAllCommon(runtime);
         
         if (buf == null) {
             str.empty();
         } else {
             str.setValue(buf);
         }
         str.setTaint(true);
         return str;
     }
 
     // implements read_all() in io.c
     protected RubyString readAll() throws BadDescriptorException, EOFException, IOException {
         Ruby runtime = getRuntime();
 
         // TODO: handle writing into original buffer better
         ByteList buf = readAllCommon(runtime);
 
         RubyString str;
         if (buf == null) {
             str = RubyString.newEmptyString(runtime);
         } else {
             str = RubyString.newString(runtime, buf);
         }
         str.setTaint(true);
         return str;
     }
 
     protected ByteList readAllCommon(Ruby runtime) throws BadDescriptorException, EOFException, IOException {
         ByteList buf = null;
-        ChannelDescriptor descriptor = openFile.getMainStream().getDescriptor();
+        ChannelDescriptor descriptor = openFile.getMainStreamSafe().getDescriptor();
         try {
             // ChannelStream#readall knows what size should be allocated at first. Just use it.
             if (descriptor.isSeekable() && descriptor.getChannel() instanceof FileChannel) {
-                buf = openFile.getMainStream().readall();
+                buf = openFile.getMainStreamSafe().readall();
             } else if (descriptor == null) {
                 buf = null;
             } else {
                 RubyThread thread = runtime.getCurrentContext().getThread();
                 try {
                     while (true) {
                         // TODO: ruby locks the string here
-                        Stream stream = openFile.getMainStream();
+                        Stream stream = openFile.getMainStreamSafe();
                         readCheck(stream);
                         openFile.checkReadable(runtime);
                         ByteList read = fread(thread, ChannelStream.BUFSIZE);
                             
                         // TODO: Ruby unlocks the string here
                         if (read.length() == 0) {
                             break;
                         }
                         if (buf == null) {
                             buf = read;
                         } else {
                             buf.append(read);
                         }
                     }
-                } catch (PipeException ex) {
-                    throw runtime.newErrnoEPIPEError();
                 } catch (InvalidValueException ex) {
                     throw runtime.newErrnoEINVALError();
                 }
             }
         } catch (NonReadableChannelException ex) {
             throw runtime.newIOError("not opened for reading");
         }
 
         return buf;
     }
 
     // implements io_fread in io.c
     private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {
-        Stream stream = openFile.getMainStream();
+        Stream stream = openFile.getMainStreamSafe();
         int rest = length;
         waitReadable(stream);
         ByteList buf = blockingFRead(stream, thread, length);
         if (buf != null) {
             rest -= buf.length();
         }
         while (rest > 0) {
             waitReadable(stream);
             openFile.checkClosed(getRuntime());
             stream.clearerr();
             ByteList newBuffer = blockingFRead(stream, thread, rest);
             if (newBuffer == null) {
                 // means EOF
                 break;
             }
             int len = newBuffer.length();
             if (len == 0) {
                 // TODO: warn?
                 // rb_warning("nonblocking IO#read is obsolete; use IO#readpartial or IO#sysread")
                 continue;
             }
             if (buf == null) {
                 buf = newBuffer;
             } else {
                 buf.append(newBuffer);
             }
             rest -= len;
         }
         if (buf == null) {
             return ByteList.EMPTY_BYTELIST.dup();
         } else {
             return buf;
         }
     }
 
     private ByteList blockingFRead(Stream stream, RubyThread thread, int length) throws IOException, BadDescriptorException {
         try {
             thread.beforeBlockingCall();
             return stream.fread(length);
         } finally {
             thread.afterBlockingCall();
         }
     }
     
     /** Read a byte. On EOF throw EOFError.
      * 
      */
     @JRubyMethod(name = "readchar", compat = RUBY1_8)
     public IRubyObject readchar() {
         IRubyObject c = getc();
         
         if (c.isNil()) throw getRuntime().newEOFError();
         
         return c;
     }
     
     @JRubyMethod
     public IRubyObject stat(ThreadContext context) {
         openFile.checkClosed(context.getRuntime());
-        return context.getRuntime().newFileStat(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor());
+        try {
+            return context.getRuntime().newFileStat(getOpenFileChecked().getMainStreamSafe().getDescriptor().getFileDescriptor());
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
+        }
     }
 
     /** 
      * <p>Invoke a block for each byte.</p>
      */
     public IRubyObject each_byteInternal(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         
     	try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             while (true) {
                 myOpenFile.checkReadable(runtime);
                 myOpenFile.setReadBuffered();
                 waitReadable(myOpenFile.getMainStream());
                 
-                int c = myOpenFile.getMainStream().fgetc();
+                int c = myOpenFile.getMainStreamSafe().fgetc();
                 
                 // CRuby checks ferror(f) and retry getc for
                 // non-blocking IO.
                 if (c == -1) {
                     break;
                 }
                 
                 assert c < 256;
                 block.yield(context, getRuntime().newFixnum(c));
             }
 
             return this;
-        } catch (PipeException ex) {
-            throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (EOFException e) {
             return runtime.getNil();
     	} catch (IOException e) {
-    	    throw runtime.newIOError(e.getMessage());
+    	    throw runtime.newIOErrorFromException(e);
         }
     }
 
     @JRubyMethod
     public IRubyObject each_byte(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_byteInternal(context, block) : enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod(name = "bytes")
     public IRubyObject bytes(final ThreadContext context) {
         return enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod(name = "lines", compat = CompatVersion.RUBY1_8)
     public IRubyObject lines(final ThreadContext context, Block block) {
         return enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     @JRubyMethod(name = "lines", compat = CompatVersion.RUBY1_9)
     public IRubyObject lines19(final ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "each_line");
         return each_lineInternal(context, NULL_ARRAY, block);
     }
 
     public IRubyObject each_charInternal(final ThreadContext context, final Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject ch;
 
         while(!(ch = getc()).isNil()) {
             byte c = (byte)RubyNumeric.fix2int(ch);
             int n = runtime.getKCode().getEncoding().length(c);
             RubyString str = runtime.newString();
             if (externalEncoding != null) str.setEncoding(externalEncoding);
             str.setTaint(true);
             str.cat(c);
 
             while(--n > 0) {
                 if((ch = getc()).isNil()) {
                     block.yield(context, str);
                     return this;
                 }
                 c = (byte)RubyNumeric.fix2int(ch);
                 str.cat(c);
             }
             block.yield(context, str);
         }
         return this;
     }
 
     @JRubyMethod
     public IRubyObject each_char(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod
     public IRubyObject chars(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal(context, block) : enumeratorize(context.getRuntime(), this, "chars");
     }
 
     @JRubyMethod
     public IRubyObject codepoints(final ThreadContext context, final Block block) {
         return eachCodePointCommon(context, block, "codepoints");
     }
 
     @JRubyMethod
     public IRubyObject each_codepoint(final ThreadContext context, final Block block) {
         return eachCodePointCommon(context, block, "each_codepoint");
     }
 
     private IRubyObject eachCharCommon(final ThreadContext context, final Block block, final String methodName) {
         return block.isGiven() ? each_char(context, block) : enumeratorize(context.getRuntime(), this, methodName);
     }
 
     private IRubyObject eachCodePointCommon(final ThreadContext context, final Block block, final String methodName) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, methodName);
         IRubyObject ch;
 
         while(!(ch = getc()).isNil()) {
             block.yield(context, ch);
         }
         return this;
     }
 
     /** 
      * <p>Invoke a block for each line.</p>
      */
     public RubyIO each_lineInternal(ThreadContext context, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         ByteList separator = getSeparatorForGets(runtime, args);
 
         ByteListCache cache = new ByteListCache();
         for (IRubyObject line = getline(runtime, separator); !line.isNil(); 
 		line = getline(runtime, separator, cache)) {
             block.yield(context, line);
         }
         
         return this;
     }
 
     @JRubyMethod(optional = 1)
     public IRubyObject each(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_lineInternal(context, args, block) : enumeratorize(context.getRuntime(), this, "each", args);
     }
 
     @JRubyMethod(optional = 1)
     public IRubyObject each_line(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_lineInternal(context, args, block) : enumeratorize(context.getRuntime(), this, "each_line", args);
     }
 
     @JRubyMethod(optional = 1)
     public RubyArray readlines(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         IRubyObject[] separatorArgs = args.length > 0 ? new IRubyObject[] { args[0] } : IRubyObject.NULL_ARRAY;
         ByteList separator = getSeparatorForGets(runtime, separatorArgs);
         RubyArray result = runtime.newArray();
         IRubyObject line;
         
         while (! (line = getline(runtime, separator)).isNil()) {
             result.append(line);
         }
         return result;
     }
     
     @JRubyMethod(name = "to_io")
     public RubyIO to_io() {
     	return this;
     }
 
     @Override
     public String toString() {
-        return "RubyIO(" + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStream().getDescriptor()) + ")";
+        try {
+            return "RubyIO(" + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStreamSafe().getDescriptor()) + ")";
+        } catch (BadDescriptorException e) {
+            throw getRuntime().newErrnoEBADFError();
+        }
     }
     
     /* class methods for IO */
     
     /** rb_io_s_foreach
     *
     */
     public static IRubyObject foreachInternal(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject filename = args[0].convertToString();
         runtime.checkSafeString(filename);
 
         RubyIO io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
         
         ByteListCache cache = new ByteListCache();
         if (!io.isNil()) {
             try {
                 ByteList separator = getSeparatorFromArgs(runtime, args, 1);
                 IRubyObject str = io.getline(runtime, separator, cache);
                 while (!str.isNil()) {
                     block.yield(context, str);
                     str = io.getline(runtime, separator, cache);
                     if (runtime.is1_9()) {
                         separator = getSeparatorFromArgs(runtime, args, 1);
                     }
                 }
             } finally {
                 io.close();
             }
         }
        
         return runtime.getNil();
     }
     
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject foreach(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), recv, "foreach", args);
 
         if (!(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             args[0] = args[0].callMethod(context, "to_path");
         }
         return foreachInternal(context, recv, args, block);
     }
 
     private static RubyIO convertToIO(ThreadContext context, IRubyObject obj) {
         return (RubyIO)TypeConverter.convertToType(obj, context.getRuntime().getIO(), "to_io");
     }
    
     private static boolean registerSelect(ThreadContext context, Selector selector, IRubyObject obj, RubyIO ioObj, int ops) throws IOException {
        Channel channel = ioObj.getChannel();
        if (channel == null || !(channel instanceof SelectableChannel)) {
            return false;
        }
        
        ((SelectableChannel) channel).configureBlocking(false);
        int real_ops = ((SelectableChannel) channel).validOps() & ops;
        SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
        
        if (key == null) {
            ((SelectableChannel) channel).register(selector, real_ops, obj);
        } else {
            key.interestOps(key.interestOps()|real_ops);
        }
        
        return true;
     }
    
     @JRubyMethod(name = "select", required = 1, optional = 3, meta = true)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return select_static(context, context.getRuntime(), args);
     }
 
     private static void checkArrayType(Ruby runtime, IRubyObject obj) {
         if (!(obj instanceof RubyArray)) {
             throw runtime.newTypeError("wrong argument type "
                     + obj.getMetaClass().getName() + " (expected Array)");
         }
     }
 
     public static IRubyObject select_static(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         Selector selector = null;
        try {
             Set pending = new HashSet();
             Set unselectable_reads = new HashSet();
             Set unselectable_writes = new HashSet();
             Map<RubyIO, Boolean> blocking = new HashMap();
             
             selector = SelectorFactory.openWithRetryFrom(context.getRuntime(), SelectorProvider.provider());
             if (!args[0].isNil()) {
                 // read
                 checkArrayType(runtime, args[0]);
                 for (Iterator i = ((RubyArray)args[0]).getList().iterator(); i.hasNext();) {
                     IRubyObject obj = (IRubyObject)i.next();
                     RubyIO ioObj = convertToIO(context, obj);
 
                     // save blocking state
                     if (ioObj.getChannel() instanceof SelectableChannel) blocking.put(ioObj, ((SelectableChannel)ioObj.getChannel()).isBlocking());
 
                     // already buffered data? don't bother selecting
-                    if (ioObj.getHandler().readDataBuffered()) {
+                    if (ioObj.getOpenFile().getMainStreamSafe().readDataBuffered()) {
                         unselectable_reads.add(obj);
                     }
                     if (registerSelect(context, selector, obj, ioObj, SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) {
                         if (ioObj.writeDataBuffered()) {
                             pending.add(obj);
                         }
                     } else {
                         if ((ioObj.openFile.getMode() & OpenFile.READABLE) != 0) {
                             unselectable_reads.add(obj);
                         }
                     }
                 }
             }
 
             if (args.length > 1 && !args[1].isNil()) {
                 // write
                 checkArrayType(runtime, args[1]);
                 for (Iterator i = ((RubyArray)args[1]).getList().iterator(); i.hasNext();) {
                     IRubyObject obj = (IRubyObject)i.next();
                     RubyIO ioObj = convertToIO(context, obj);
 
                     // save blocking state
                     if (!blocking.containsKey(ioObj) && ioObj.getChannel() instanceof SelectableChannel) blocking.put(ioObj, ((SelectableChannel)ioObj.getChannel()).isBlocking());
 
                     if (!registerSelect(context, selector, obj, ioObj, SelectionKey.OP_WRITE)) {
                         if ((ioObj.openFile.getMode() & OpenFile.WRITABLE) != 0) {
                             unselectable_writes.add(obj);
                         }
                     }
                 }
             }
 
             if (args.length > 2 && !args[2].isNil()) {
                 checkArrayType(runtime, args[2]);
             // Java's select doesn't do anything about this, so we leave it be.
             }
 
             final boolean has_timeout = (args.length > 3 && !args[3].isNil());
             long timeout = 0;
             if (has_timeout) {
                 IRubyObject timeArg = args[3];
                 if (timeArg instanceof RubyFloat) {
                     timeout = Math.round(((RubyFloat)timeArg).getDoubleValue() * 1000);
                 } else if (timeArg instanceof RubyFixnum) {
                     timeout = Math.round(((RubyFixnum)timeArg).getDoubleValue() * 1000);
                 } else { // TODO: MRI also can hadle Bignum here
                     throw runtime.newTypeError("can't convert " + timeArg.getMetaClass().getName() + " into time interval");
                 }
 
                 if (timeout < 0) {
                     throw runtime.newArgumentError("negative timeout given");
                 }
             }
 
             if (pending.isEmpty() && unselectable_reads.isEmpty() && unselectable_writes.isEmpty()) {
                 if (has_timeout) {
                     if (timeout == 0) {
                         selector.selectNow();
                     } else {
                         selector.select(timeout);
                     }
                 } else {
                     selector.select();
                 }
             } else {
                 selector.selectNow();
             }
 
             List r = new ArrayList();
             List w = new ArrayList();
             List e = new ArrayList();
             for (Iterator i = selector.selectedKeys().iterator(); i.hasNext();) {
                 SelectionKey key = (SelectionKey)i.next();
                 try {
                     int interestAndReady = key.interestOps() & key.readyOps();
                     if ((interestAndReady & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT | SelectionKey.OP_CONNECT)) != 0) {
                         r.add(key.attachment());
                         pending.remove(key.attachment());
                     }
                     if ((interestAndReady & (SelectionKey.OP_WRITE)) != 0) {
                         w.add(key.attachment());
                     }
                 } catch (CancelledKeyException cke) {
                     // TODO: is this the right thing to do?
                     pending.remove(key.attachment());
                     e.add(key.attachment());
                 }
             }
             r.addAll(pending);
             r.addAll(unselectable_reads);
             w.addAll(unselectable_writes);
 
             // make all sockets blocking as configured again
             selector.close(); // close unregisters all channels, so we can safely reset blocking modes
             for (Map.Entry blockingEntry : blocking.entrySet()) {
                 SelectableChannel channel = (SelectableChannel)((RubyIO)blockingEntry.getKey()).getChannel();
                 synchronized (channel.blockingLock()) {
                     channel.configureBlocking((Boolean)blockingEntry.getValue());
                 }
             }
 
             if (r.isEmpty() && w.isEmpty() && e.isEmpty()) {
                 return runtime.getNil();
             }
 
             List ret = new ArrayList();
 
             ret.add(RubyArray.newArray(runtime, r));
             ret.add(RubyArray.newArray(runtime, w));
             ret.add(RubyArray.newArray(runtime, e));
 
             return RubyArray.newArray(runtime, ret);
+        } catch (BadDescriptorException e) {
+            throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
-            throw runtime.newIOError(e.getMessage());
+            throw runtime.newIOErrorFromException(e);
         } finally {
             if (selector != null) {
                 try {
                     selector.close();
                 } catch (Exception e) {
                 }
             }
         }
    }
    
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         switch (args.length) {
         case 0: throw context.getRuntime().newArgumentError(0, 1);
         case 1: return readStatic(context, recv, args[0]);
         case 2: return readStatic(context, recv, args[0], args[1]);
         case 3: return readStatic(context, recv, args[0], args[1], args[2]);
         default: throw context.getRuntime().newArgumentError(args.length, 3);
         }
    }
 
     private static RubyIO newFile(ThreadContext context, IRubyObject recv, IRubyObject... args) {
        return (RubyIO) RubyKernel.open(context, recv, args, Block.NULL_BLOCK);
     }
 
     public static void failIfDirectory(Ruby runtime, RubyString pathStr) {
         if (RubyFileTest.directory_p(runtime, pathStr).isTrue()) {
             if (Platform.IS_WINDOWS) {
                 throw runtime.newErrnoEACCESError(pathStr.asJavaString());
             } else {
                 throw runtime.newErrnoEISDirError(pathStr.asJavaString());
             }
         }
     }
 
     @Deprecated
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, Block unusedBlock) {
         return readStatic(context, recv, path);
     }
     @Deprecated
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length) {
         return readStatic(context, recv, path, length);
     }
     @Deprecated
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset) {
         return readStatic(context, recv, path, length, offset);
     }
    
     @JRubyMethod(name = "read", meta = true, compat = RUBY1_8)
     public static IRubyObject readStatic(ThreadContext context, IRubyObject recv, IRubyObject path) {
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
 
        try {
            return file.read(context);
        } finally {
            file.close();
        }
     }
    
     @JRubyMethod(name = "read", meta = true, compat = RUBY1_8)
     public static IRubyObject readStatic(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length) {
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
        
         try {
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     @JRubyMethod(name = "read", meta = true, compat = RUBY1_8)
     public static IRubyObject readStatic(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset) {
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     /**
      *  options is a hash which can contain:
      *    encoding: string or encoding
      *    mode: string
      *    open_args: array of string
      */
     private static IRubyObject read19(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset, RubyHash options) {
         // FIXME: process options
 
         RubyString pathStr = RubyFile.get_path(context, path);
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     /**
      * binread is just like read, except it doesn't take options and it forces
      * mode to be "rb:ASCII-8BIT"
      *
      * @param context the current ThreadContext
      * @param recv the target of the call (IO or a subclass)
      * @param args arguments; path [, length [, offset]]
      * @return the binary contents of the given file, at specified length and offset
      */
     @JRubyMethod(meta = true, required = 1, optional = 2, compat = RUBY1_9)
     public static IRubyObject binread(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject nil = context.getRuntime().getNil();
         IRubyObject path = args[0];
         IRubyObject length = nil;
         IRubyObject offset = nil;
         Ruby runtime = context.runtime;
 
         if (args.length > 2) {
             offset = args[2];
             length = args[1];
         } else if (args.length > 1) {
             length = args[1];
         }
         RubyIO file = (RubyIO)RuntimeHelpers.invoke(context, runtime.getFile(), "new", path, runtime.newString("rb:ASCII-8BIT"));
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     // Enebo: annotation processing forced me to do pangea method here...
     @JRubyMethod(name = "read", meta = true, required = 1, optional = 3, compat = RUBY1_9)
     public static IRubyObject read19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         IRubyObject nil = context.getRuntime().getNil();
         IRubyObject path = args[0];
         IRubyObject length = nil;
         IRubyObject offset = nil;
         RubyHash options = null;
         if (args.length > 3) {
             if (!(args[3] instanceof RubyHash)) throw context.getRuntime().newTypeError("Must be a hash");
             options = (RubyHash) args[3];
             offset = args[2];
             length = args[1];
         } else if (args.length > 2) {
             if (args[2] instanceof RubyHash) {
                 options = (RubyHash) args[2];
             } else {
                 offset = args[2];
             }
             length = args[1];
         } else if (args.length > 1) {
             if (args[1] instanceof RubyHash) {
                 options = (RubyHash) args[1];
             } else {
                 length = args[1];
             }
         }
 
         return read19(context, recv, path, length, offset, (RubyHash) options);
     }
 
     @JRubyMethod(name = "readlines", required = 1, optional = 1, meta = true)
     public static RubyArray readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         int count = args.length;
 
         IRubyObject[] fileArguments = new IRubyObject[]{ args[0].convertToString() };
         IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
         RubyIO file = (RubyIO) RubyKernel.open(context, recv, fileArguments, Block.NULL_BLOCK);
         try {
             return file.readlines(context, separatorArguments);
         } finally {
             file.close();
         }
     }
    
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true, compat = RUBY1_8)
     public static IRubyObject popen(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         int mode;
 
         IRubyObject cmdObj = null;
         if (Platform.IS_WINDOWS) {
             String[] tokens = args[0].convertToString().toString().split(" ", 2);
             String commandString = tokens[0].replace('/', '\\') +
                     (tokens.length > 1 ? ' ' + tokens[1] : "");
             cmdObj = runtime.newString(commandString);
         } else {
             cmdObj = args[0].convertToString();
         }
         runtime.checkSafeString(cmdObj);
 
         if ("-".equals(cmdObj.toString())) {
             throw runtime.newNotImplementedError("popen(\"-\") is unimplemented");
         }
 
         try {
             if (args.length == 1) {
                 mode = ModeFlags.RDONLY;
             } else if (args[1] instanceof RubyFixnum) {
                 mode = RubyFixnum.num2int(args[1]);
             } else {
                 mode = getIOModesIntFromString(runtime, args[1].convertToString().toString());
             }
 
             ModeFlags modes = new ModeFlags(mode);
 
             ShellLauncher.POpenProcess process = ShellLauncher.popen(runtime, cmdObj, modes);
 
             // Yes, this is gross. java.lang.Process does not appear to be guaranteed
             // "ready" when we get it back from Runtime#exec, so we try to give it a
             // chance by waiting for 10ms before we proceed. Only doing this on 1.5
             // since Hotspot 1.6+ does not seem to exhibit the problem.
             if (System.getProperty("java.specification.version", "").equals("1.5")) {
                 synchronized (process) {
                     try {
                         process.wait(100);
                     } catch (InterruptedException ie) {}
                 }
             }
             
             RubyIO io = new RubyIO(runtime, process, modes);
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, io);
                 } finally {
                     if (io.openFile.isOpen()) {
                         io.close();
                     }
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor())));
                 }
             }
             return io;
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject popen19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         int mode;
 
         IRubyObject[] cmdPlusArgs = null;
         RubyHash env = null;
         RubyHash opts = null;
         IRubyObject cmdObj = null;
         IRubyObject arg0 = args[0].checkArrayType();
         
         if (!arg0.isNil()) {
             List argList = new ArrayList(Arrays.asList(((RubyArray)arg0).toJavaArray()));
             if (argList.isEmpty()) throw runtime.newArgumentError("wrong number of arguments");
             if (argList.get(0) instanceof RubyHash) {
                 // leading hash, use for env
                 env = (RubyHash)argList.remove(0);
             }
             if (argList.isEmpty()) throw runtime.newArgumentError("wrong number of arguments");
             if (argList.size() > 1 && argList.get(argList.size() - 1) instanceof RubyHash) {
                 // trailing hash, use for opts
                 env = (RubyHash)argList.get(argList.size() - 1);
             }
             cmdPlusArgs = (IRubyObject[])argList.toArray(new IRubyObject[argList.size()]);
 
             if (Platform.IS_WINDOWS) {
                 String commandString = cmdPlusArgs[0].convertToString().toString().replace('/', '\\');
                 cmdPlusArgs[0] = runtime.newString(commandString);
             } else {
                 cmdPlusArgs[0] = cmdPlusArgs[0].convertToString();
             }
             cmdObj = cmdPlusArgs[0];
         } else {
             if (Platform.IS_WINDOWS) {
                 String[] tokens = args[0].convertToString().toString().split(" ", 2);
                 String commandString = tokens[0].replace('/', '\\') +
                         (tokens.length > 1 ? ' ' + tokens[1] : "");
                 cmdObj = runtime.newString(commandString);
             } else {
                 cmdObj = args[0].convertToString();
             }
         }
         
         runtime.checkSafeString(cmdObj);
 
         if ("-".equals(cmdObj.toString())) {
             throw runtime.newNotImplementedError("popen(\"-\") is unimplemented");
         }
 
         try {
             if (args.length == 1) {
                 mode = ModeFlags.RDONLY;
             } else if (args[1] instanceof RubyFixnum) {
                 mode = RubyFixnum.num2int(args[1]);
             } else {
                 mode = getIOModesIntFromString(runtime, args[1].convertToString().toString());
             }
 
             ModeFlags modes = new ModeFlags(mode);
 
             ShellLauncher.POpenProcess process;
             if (cmdPlusArgs == null) {
                 process = ShellLauncher.popen(runtime, cmdObj, modes);
             } else {
                 process = ShellLauncher.popen(runtime, cmdPlusArgs, env, modes);
             }
 
             // Yes, this is gross. java.lang.Process does not appear to be guaranteed
             // "ready" when we get it back from Runtime#exec, so we try to give it a
             // chance by waiting for 10ms before we proceed. Only doing this on 1.5
             // since Hotspot 1.6+ does not seem to exhibit the problem.
             if (System.getProperty("java.specification.version", "").equals("1.5")) {
                 synchronized (process) {
                     try {
                         process.wait(100);
                     } catch (InterruptedException ie) {}
                 }
             }
 
             RubyIO io = new RubyIO(runtime, process, modes);
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, io);
                 } finally {
                     if (io.openFile.isOpen()) {
                         io.close();
                     }
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor())));
                 }
             }
             return io;
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
    
     @JRubyMethod(rest = true, meta = true)
     public static IRubyObject popen3(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         try {
             POpenTuple tuple = popenSpecial(context, args);
 
             RubyArray yieldArgs = RubyArray.newArrayLight(runtime,
                     tuple.output,
                     tuple.input,
                     tuple.error);
             
             if (block.isGiven()) {
                 try {
                     return block.yield(context, yieldArgs);
                 } finally {
                     cleanupPOpen(tuple);
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, tuple.process.waitFor()));
                 }
             }
             return yieldArgs;
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     @JRubyMethod(rest = true, meta = true)
     public static IRubyObject popen4(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         try {
             POpenTuple tuple = popenSpecial(context, args);
 
             RubyArray yieldArgs = RubyArray.newArrayLight(runtime,
                     runtime.newFixnum(ShellLauncher.getPidFromProcess(tuple.process)),
                     tuple.output,
                     tuple.input,
                     tuple.error);
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, yieldArgs);
                 } finally {
                     cleanupPOpen(tuple);
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, tuple.process.waitFor()));
                 }
             }
             return yieldArgs;
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     private static void cleanupPOpen(POpenTuple tuple) {
         if (tuple.input.openFile.isOpen()) {
             tuple.input.close();
         }
         if (tuple.output.openFile.isOpen()) {
             tuple.output.close();
         }
         if (tuple.error.openFile.isOpen()) {
             tuple.error.close();
         }
     }
 
     private static class POpenTuple {
         public POpenTuple(RubyIO i, RubyIO o, RubyIO e, Process p) {
             input = i; output = o; error = e; process = p;
         }
         public final RubyIO input;
         public final RubyIO output;
         public final RubyIO error;
         public final Process process;
     }
 
     public static POpenTuple popenSpecial(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         try {
             ShellLauncher.POpenProcess process = ShellLauncher.popen3(runtime, args);
             RubyIO input = process.getInput() != null ?
                 new RubyIO(runtime, process.getInput()) :
                 new RubyIO(runtime, process.getInputStream());
             RubyIO output = process.getOutput() != null ?
                 new RubyIO(runtime, process.getOutput()) :
                 new RubyIO(runtime, process.getOutputStream());
             RubyIO error = process.getError() != null ?
                 new RubyIO(runtime, process.getError()) :
                 new RubyIO(runtime, process.getErrorStream());
 
-            input.getOpenFile().getMainStream().getDescriptor().
+            input.getOpenFile().getMainStreamSafe().getDescriptor().
               setCanBeSeekable(false);
-            output.getOpenFile().getMainStream().getDescriptor().
+            output.getOpenFile().getMainStreamSafe().getDescriptor().
               setCanBeSeekable(false);
-            error.getOpenFile().getMainStream().getDescriptor().
+            error.getOpenFile().getMainStreamSafe().getDescriptor().
               setCanBeSeekable(false);
 
             return new POpenTuple(input, output, error, process);
+        } catch (BadDescriptorException e) {
+            throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     // NIO based pipe
     @JRubyMethod(name = "pipe", meta = true)
     public static IRubyObject pipe(ThreadContext context, IRubyObject recv) {
         // TODO: This isn't an exact port of MRI's pipe behavior, so revisit
-       Ruby runtime = context.getRuntime();
-       try {
-           Pipe pipe = Pipe.open();
+        Ruby runtime = context.getRuntime();
+        try {
+            Pipe pipe = Pipe.open();
 
-           RubyIO source = new RubyIO(runtime, pipe.source());
-           RubyIO sink = new RubyIO(runtime, pipe.sink());
+            RubyIO source = new RubyIO(runtime, pipe.source());
+            RubyIO sink = new RubyIO(runtime, pipe.sink());
 
-           sink.openFile.getMainStream().setSync(true);
-           return runtime.newArrayNoCopy(new IRubyObject[] { source, sink });
-       } catch (IOException ioe) {
-           throw runtime.newIOErrorFromException(ioe);
-       }
-   }
+            sink.openFile.getMainStreamSafe().setSync(true);
+            return runtime.newArrayNoCopy(new IRubyObject[]{source, sink});
+        } catch (BadDescriptorException e) {
+            throw runtime.newErrnoEBADFError();
+        } catch (IOException ioe) {
+            throw runtime.newIOErrorFromException(ioe);
+        }
+    }
     
     @JRubyMethod(name = "copy_stream", meta = true, compat = RUBY1_9)
     public static IRubyObject copy_stream(ThreadContext context, IRubyObject recv, 
             IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
 
         RubyIO io1 = null;
         RubyIO io2 = null;
 
         try {
             if (arg1 instanceof RubyString) {
                 io1 = (RubyIO) RubyFile.open(context, runtime.getFile(), new IRubyObject[] {arg1}, Block.NULL_BLOCK);
             } else if (arg1 instanceof RubyIO) {
                 io1 = (RubyIO) arg1;
             } else {
                 throw runtime.newTypeError("Should be String or IO");
             }
 
             if (arg2 instanceof RubyString) {
                 io2 = (RubyIO) RubyFile.open(context, runtime.getFile(), new IRubyObject[] {arg2, runtime.newString("w")}, Block.NULL_BLOCK);
             } else if (arg2 instanceof RubyIO) {
                 io2 = (RubyIO) arg2;
             } else {
                 throw runtime.newTypeError("Should be String or IO");
             }
 
-            ChannelDescriptor d1 = io1.openFile.getMainStream().getDescriptor();
+            ChannelDescriptor d1 = io1.openFile.getMainStreamSafe().getDescriptor();
             if (!d1.isSeekable()) {
                 throw context.getRuntime().newTypeError("only supports file-to-file copy");
             }
-            ChannelDescriptor d2 = io2.openFile.getMainStream().getDescriptor();
+            ChannelDescriptor d2 = io2.openFile.getMainStreamSafe().getDescriptor();
             if (!d2.isSeekable()) {
                 throw context.getRuntime().newTypeError("only supports file-to-file copy");
             }
 
             FileChannel f1 = (FileChannel)d1.getChannel();
             FileChannel f2 = (FileChannel)d2.getChannel();
 
             try {
                 long size = f1.size();
 
                 f1.transferTo(f2.position(), size, f2);
 
                 return context.getRuntime().newFixnum(size);
             } catch (IOException ioe) {
                 throw runtime.newIOErrorFromException(ioe);
             }
+        } catch (BadDescriptorException e) {
+            throw runtime.newErrnoEBADFError();
         } finally {
             try {
                 if (io1 != null) {
                     io1.close();
                 }
             } finally {
                 if (io2 != null) {
                     io2.close();
                 }
             }
         }
     }
 
     @JRubyMethod(name = "try_convert", meta = true, backtrace = true, compat = RUBY1_9)
     public static IRubyObject tryConvert(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return arg.respondsTo("to_io") ? convertToIO(context, arg) : context.getRuntime().getNil();
     }
 
     private static ByteList getNilByteList(Ruby runtime) {
         return runtime.is1_9() ? ByteList.EMPTY_BYTELIST : NIL_BYTELIST;
     }
     
     /**
      * Add a thread to the list of blocking threads for this IO.
      * 
      * @param thread A thread blocking on this IO
      */
     public synchronized void addBlockingThread(RubyThread thread) {
         if (blockingThreads == null) {
             blockingThreads = new ArrayList<RubyThread>(1);
         }
         blockingThreads.add(thread);
     }
     
     /**
      * Remove a thread from the list of blocking threads for this IO.
      * 
      * @param thread A thread blocking on this IO
      */
     public synchronized void removeBlockingThread(RubyThread thread) {
         if (blockingThreads == null) {
             return;
         }
         for (int i = 0; i < blockingThreads.size(); i++) {
             if (blockingThreads.get(i) == thread) {
                 // not using remove(Object) here to avoid the equals() call
                 blockingThreads.remove(i);
             }
         }
     }
     
     /**
      * Fire an IOError in all threads blocking on this IO object
      */
     protected synchronized void interruptBlockingThreads() {
         if (blockingThreads == null) {
             return;
         }
         for (int i = 0; i < blockingThreads.size(); i++) {
             RubyThread thread = blockingThreads.get(i);
             
             // raise will also wake the thread from selection
             thread.raise(new IRubyObject[] {getRuntime().newIOError("stream closed").getException()}, Block.NULL_BLOCK);
         }
     }
 
     /**
      * Caching reference to allocated byte-lists, allowing for internal byte[] to be
      * reused, rather than reallocated.
      *
      * Predominately used on {@link RubyIO#getline(Ruby, ByteList)} and variants.
      *
      * @author realjenius
      */
     private static class ByteListCache {
         private byte[] buffer = new byte[0];
         public void release(ByteList l) {
             buffer = l.getUnsafeBytes();
         }
 
         public ByteList allocate(int size) {
             ByteList l = new ByteList(buffer, 0, size, false);
             return l;
         }
     }
 
     /**
      *
      *  ==== Options
      *  <code>opt</code> can have the following keys
      *  :mode ::
      *    same as <code>mode</code> parameter
      *  :external_encoding ::
      *    external encoding for the IO. "-" is a
      *    synonym for the default external encoding.
      *  :internal_encoding ::
      *    internal encoding for the IO.
      *    "-" is a synonym for the default internal encoding.
      *    If the value is nil no conversion occurs.
      *  :encoding ::
      *    specifies external and internal encodings as "extern:intern".
      *  :textmode ::
      *    If the value is truth value, same as "b" in argument <code>mode</code>.
      *  :binmode ::
      *    If the value is truth value, same as "t" in argument <code>mode</code>.
      *
      *  Also <code>opt</code> can have same keys in <code>String#encode</code> for
      *  controlling conversion between the external encoding and the internal encoding.
      *
      */
     protected ModeFlags parseOptions(ThreadContext context, IRubyObject options, ModeFlags modes) {
         Ruby runtime = context.getRuntime();
 
         RubyHash rubyOptions = (RubyHash) options;
 
         IRubyObject internalEncodingOption = rubyOptions.fastARef(runtime.newSymbol("internal_encoding"));
         IRubyObject externalEncodingOption = rubyOptions.fastARef(runtime.newSymbol("external_encoding"));
         RubyString dash = runtime.newString("-");
         if (externalEncodingOption != null && !externalEncodingOption.isNil()) {
             if (dash.eql(externalEncodingOption)) {
                 externalEncodingOption = runtime.getEncodingService().getDefaultExternal();
             }
             setExternalEncoding(context, externalEncodingOption);
         }
 
         if (internalEncodingOption != null && !internalEncodingOption.isNil()) {
             if (dash.eql(internalEncodingOption)) {
                 internalEncodingOption = runtime.getEncodingService().getDefaultInternal();
             }
             setInternalEncoding(context, internalEncodingOption);
         }
 
         IRubyObject encoding = rubyOptions.fastARef(runtime.newSymbol("encoding"));
         if (encoding != null && !encoding.isNil()) {
             if (externalEncodingOption != null && !externalEncodingOption.isNil()) {
                 runtime.getWarnings().warn("Ignoring encoding parameter '"+ encoding +"': external_encoding is used");
             } else if (internalEncodingOption != null && !internalEncodingOption.isNil()) {
                 runtime.getWarnings().warn("Ignoring encoding parameter '"+ encoding +"': internal_encoding is used");
             } else {
                 parseEncodingFromString(context, encoding, 0);
             }
         }
 
         if (rubyOptions.containsKey(runtime.newSymbol("mode"))) {
             modes = parseModes19(context, rubyOptions.fastARef(runtime.newSymbol("mode")).asString());
         }
 
 //      FIXME: check how ruby 1.9 handles this
 
 //        if (rubyOptions.containsKey(runtime.newSymbol("textmode")) &&
 //                rubyOptions.fastARef(runtime.newSymbol("textmode")).isTrue()) {
 //            try {
 //                modes = getIOModes(runtime, "t");
 //            } catch (InvalidValueException e) {
 //                throw getRuntime().newErrnoEINVALError();
 //            }
 //        }
 //
 //        if (rubyOptions.containsKey(runtime.newSymbol("binmode")) &&
 //                rubyOptions.fastARef(runtime.newSymbol("binmode")).isTrue()) {
 //            try {
 //                modes = getIOModes(runtime, "b");
 //            } catch (InvalidValueException e) {
 //                throw getRuntime().newErrnoEINVALError();
 //            }
 //        }
 
         return modes;
     }
 
     /**
      * Try for around 1s to destroy the child process. This is to work around
      * issues on some JVMs where if you try to destroy the process too quickly
      * it may not be ready and may ignore the destroy. A subsequent waitFor
      * will then hang. This version tries to destroy and call exitValue
      * repeatedly for up to 1000 calls with 1ms delay between iterations, with
      * the intent that the target process ought to be "ready to die" fairly
      * quickly and we don't get stuck in a blocking waitFor call.
      *
      * @param runtime The Ruby runtime, for raising an error
      * @param process The process to obliterate
      */
     public static void obliterateProcess(Process process) {
         int i = 0;
         Object waitLock = new Object();
         while (true) {
             // only try 1000 times with a 1ms sleep between, so we don't hang
             // forever on processes that ignore SIGTERM
             if (i >= 1000) {
                 throw new RuntimeException("could not shut down process: " + process);
             }
 
             // attempt to destroy (SIGTERM on UNIX, TerminateProcess on Windows)
             process.destroy();
             
             try {
                 // get the exit value; succeeds if it has terminated, throws
                 // IllegalThreadStateException if not.
                 process.exitValue();
             } catch (IllegalThreadStateException itse) {
                 // increment count and try again after a 1ms sleep
                 i += 1;
                 synchronized (waitLock) {
                     try {waitLock.wait(1);} catch (InterruptedException ie) {}
                 }
                 continue;
             }
             // success!
             break;
         }
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor, boolean isRetained) {
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor) {
     }
 
     @Deprecated
     public void unregisterDescriptor(int aFileno) {
     }
 
     @Deprecated
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         return ChannelDescriptor.getDescriptorByFileno(aFileno);
     }
 
     @Deprecated
     public static int getNewFileno() {
         return ChannelDescriptor.getNewFileno();
     }
 
     @Deprecated
     public boolean writeDataBuffered() {
         return openFile.getMainStream().writeDataBuffered();
     }
 
     @Deprecated
     public IRubyObject gets(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? gets(context) : gets(context, args[0]);
     }
 
     @Deprecated
     public IRubyObject readline(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? readline(context) : readline(context, args[0]);
     }
     
     protected OpenFile openFile;
     protected List<RubyThread> blockingThreads;
     protected Encoding externalEncoding;
     protected Encoding internalEncoding;
 }
diff --git a/src/org/jruby/ext/NetProtocolBufferedIO.java b/src/org/jruby/ext/NetProtocolBufferedIO.java
index 223f0ea14b..9fb3b8d73d 100644
--- a/src/org/jruby/ext/NetProtocolBufferedIO.java
+++ b/src/org/jruby/ext/NetProtocolBufferedIO.java
@@ -1,136 +1,151 @@
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
  * Copyright (C) 2008 Ola Bini <ola.bini@gmail.com>
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
 package org.jruby.ext;
 
 import java.io.IOException;
 
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.nio.channels.SelectableChannel;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 
 import org.jruby.RubyObject;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyIO;
 import org.jruby.Ruby;
 import org.jruby.RubyException;
 import org.jruby.RubyModule;
 import org.jruby.RubyClass;
 import org.jruby.RubyString;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.exceptions.RaiseException;
 
 import org.jruby.util.io.SelectorFactory;
 import java.nio.channels.spi.SelectorProvider;
+import org.jruby.util.io.BadDescriptorException;
+import org.jruby.util.io.OpenFile;
+import org.jruby.util.io.Stream;
 
 /**
  * @author <a href="mailto:ola.bini@gmail.com">Ola Bini</a>
  */
 @JRubyClass(name="Net::BufferedIO")
 public class NetProtocolBufferedIO { 
     public static void create(Ruby runtime) {
         RubyModule mNet = runtime.getModule("Net");
 
         RubyClass cBufferedIO = (RubyClass)mNet.getConstant("BufferedIO");
         cBufferedIO.defineAnnotatedMethods(NetProtocolBufferedIO.class);
 
         RubyModule mNativeImpl = cBufferedIO.defineModuleUnder("NativeImplementation");
 
         mNativeImpl.defineAnnotatedMethods(NativeImpl.class);
     }    
 
     @JRubyMethod(required = 1)
     public static IRubyObject initialize(IRubyObject recv, IRubyObject io) {
-        if(io instanceof RubyIO && 
-           (((RubyIO)io).getOpenFile().getMainStream() instanceof ChannelStream) &&
-           (((ChannelStream)((RubyIO)io).getOpenFile().getMainStream()).getDescriptor().getChannel() instanceof SelectableChannel))  {
-
-            ((RubyObject)recv).extend(new IRubyObject[]{((RubyModule)recv.getRuntime().getModule("Net").getConstant("BufferedIO")).getConstant("NativeImplementation")});
-            SelectableChannel sc = (SelectableChannel)(((ChannelStream)((RubyIO)io).getOpenFile().getMainStream()).getDescriptor().getChannel());
-            recv.dataWrapStruct(new NativeImpl(sc));
-        }
+        try {
+            if (io instanceof RubyIO) {
+                RubyIO rubyIO = (RubyIO)io;
+                OpenFile of = rubyIO.getOpenFile();
+                Stream stream = of.getMainStreamSafe();
+                if (stream instanceof ChannelStream) {
+                    ChannelStream cStream = (ChannelStream)stream;
+                    if (cStream.getDescriptor().getChannel() instanceof SelectableChannel)  {
+                        SelectableChannel selChannel = (SelectableChannel)cStream.getDescriptor().getChannel();
+
+                        ((RubyObject)recv).extend(
+                                new IRubyObject[]{((RubyModule)recv.getRuntime().getModule("Net").getConstant("BufferedIO")).getConstant("NativeImplementation")});
+                        SelectableChannel sc = (SelectableChannel)(selChannel);
+                        recv.dataWrapStruct(new NativeImpl(sc));
+                    }
+                }
+            }
 
-        recv.getInstanceVariables().setInstanceVariable("@io", io);
-        recv.getInstanceVariables().setInstanceVariable("@read_timeout", recv.getRuntime().newFixnum(60));
-        recv.getInstanceVariables().setInstanceVariable("@debug_output", recv.getRuntime().getNil());
-        recv.getInstanceVariables().setInstanceVariable("@rbuf", RubyString.newEmptyString(recv.getRuntime()));
+            recv.getInstanceVariables().setInstanceVariable("@io", io);
+            recv.getInstanceVariables().setInstanceVariable("@read_timeout", recv.getRuntime().newFixnum(60));
+            recv.getInstanceVariables().setInstanceVariable("@debug_output", recv.getRuntime().getNil());
+            recv.getInstanceVariables().setInstanceVariable("@rbuf", RubyString.newEmptyString(recv.getRuntime()));
 
-        return recv;
+            return recv;
+        } catch (BadDescriptorException e) {
+            throw recv.getRuntime().newErrnoEBADFError();
+        }
     }
 
     @JRubyModule(name="Net::BufferedIO::NativeImplementation")
     public static class NativeImpl {
         private SelectableChannel channel;
         public NativeImpl(SelectableChannel channel) {
             this.channel = channel;
         }
         
         @JRubyMethod
         public static IRubyObject rbuf_fill(IRubyObject recv) {
             RubyString buf = (RubyString)recv.getInstanceVariables().getInstanceVariable("@rbuf");
             RubyIO io = (RubyIO)recv.getInstanceVariables().getInstanceVariable("@io");
 
             int timeout = RubyNumeric.fix2int(recv.getInstanceVariables().getInstanceVariable("@read_timeout")) * 1000;
             NativeImpl nim = (NativeImpl)recv.dataGetStruct();
 
             Selector selector = null;
             synchronized (nim.channel.blockingLock()) {
                 boolean oldBlocking = nim.channel.isBlocking();
 
                 try {
                     selector = SelectorFactory.openWithRetryFrom(recv.getRuntime(), SelectorProvider.provider());
                     nim.channel.configureBlocking(false);
                     SelectionKey key = nim.channel.register(selector, SelectionKey.OP_READ);
                     int n = selector.select(timeout);
 
                     if(n > 0) {
                         IRubyObject readItems = io.read(new IRubyObject[]{recv.getRuntime().newFixnum(1024*16)});
                         return buf.concat(readItems);
                     } else {
                         RubyClass exc = (RubyClass)(recv.getRuntime().getModule("Timeout").getConstant("Error"));
                         throw new RaiseException(RubyException.newException(recv.getRuntime(), exc, "execution expired"),false);
                     }
                 } catch(IOException exception) {
                     throw recv.getRuntime().newIOErrorFromException(exception);
                 } finally {
                     if (selector != null) {
                         try {
                             selector.close();
                         } catch (Exception e) {
                         }
                     }
                     try {nim.channel.configureBlocking(oldBlocking);} catch (IOException ioe) {}
                 }
             }
         }
     }
 }// NetProtocolBufferedIO
diff --git a/src/org/jruby/ext/ffi/IOModule.java b/src/org/jruby/ext/ffi/IOModule.java
index c7497c3878..c7e22cd516 100644
--- a/src/org/jruby/ext/ffi/IOModule.java
+++ b/src/org/jruby/ext/ffi/IOModule.java
@@ -1,102 +1,100 @@
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
  * Copyright (C) 2009 JRuby project
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
 
 package org.jruby.ext.ffi;
 
 import java.io.EOFException;
 import java.io.IOException;
 import java.nio.ByteBuffer;
 import org.jruby.Ruby;
 import org.jruby.RubyIO;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.PipeException;
 
 /**
 * FFI specific I/O routines
  */
 public class IOModule {
 
     public static void createIOModule(Ruby runtime, RubyModule ffi) {
         RubyModule module = ffi.defineModuleUnder("IO");
         module.defineAnnotatedMethods(IOModule.class);
     }
 
     @JRubyMethod(name = "native_read", module = true)
     public static final IRubyObject native_read(ThreadContext context, IRubyObject self,
             IRubyObject src, IRubyObject dst, IRubyObject rbLength) {
         if (!(src instanceof RubyIO)) {
             throw context.getRuntime().newTypeError("wrong argument (expected IO)");
         }
 
         if (!(dst instanceof AbstractMemory)) {
             throw context.getRuntime().newTypeError("wrong argument (expected FFI memory)");
         }
 
         Ruby runtime = context.getRuntime();
         try {
             OpenFile openFile = ((RubyIO) src).getOpenFile();
             openFile.checkClosed(runtime);
             openFile.checkReadable(runtime);
 
-            ChannelStream stream = (ChannelStream) openFile.getMainStream();
+            ChannelStream stream = (ChannelStream) openFile.getMainStreamSafe();
 
             ByteBuffer buffer = ((AbstractMemory) dst).getMemoryIO().asByteBuffer();
             int count = RubyNumeric.num2int(rbLength);
 
             if (count > buffer.remaining()) {
                 throw runtime.newIndexError("read count too big for output buffer");
             }
 
             if (count < buffer.remaining()) {
                 buffer = buffer.duplicate();
                 buffer.limit(count);
             }
             
             return runtime.newFixnum(stream.read(buffer));
-            
-        } catch (PipeException ex) {
-            throw runtime.newErrnoEPIPEError();
+
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (EOFException e) {
             return runtime.newFixnum(-1);
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 }
diff --git a/src/org/jruby/ext/ffi/io/FileDescriptorIO.java b/src/org/jruby/ext/ffi/io/FileDescriptorIO.java
index ddd6060b0a..aad55662ae 100644
--- a/src/org/jruby/ext/ffi/io/FileDescriptorIO.java
+++ b/src/org/jruby/ext/ffi/io/FileDescriptorIO.java
@@ -1,92 +1,95 @@
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
  * Copyright (C) 2008 JRuby project
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
 
 package org.jruby.ext.ffi.io;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyIO;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
+import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.ModeFlags;
 
 /**
  * An IO implementation that reads/writes to a native file descriptor.
  */
 @JRubyClass(name="FFI::" + FileDescriptorIO.CLASS_NAME, parent="IO")
 public class FileDescriptorIO extends RubyIO {
     public static final String CLASS_NAME = "FileDescriptorIO";
     private static final class Allocator implements ObjectAllocator {
         public final IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new FileDescriptorIO(runtime, klass);
         }
         private static final ObjectAllocator INSTANCE = new Allocator();
     }
     public FileDescriptorIO(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
     }
     public FileDescriptorIO(Ruby runtime, IRubyObject fd) {
         super(runtime, runtime.fastGetModule("FFI").fastGetClass(CLASS_NAME));
         ModeFlags modes;
         try {
             modes = new ModeFlags(ModeFlags.RDWR);
+            openFile.setMainStream(ChannelStream.open(getRuntime(),
+                    new ChannelDescriptor(new FileDescriptorByteChannel(getRuntime(), RubyNumeric.fix2int(fd)),
+                    modes)));
+            openFile.setPipeStream(openFile.getMainStreamSafe());
+            openFile.setMode(modes.getOpenFileFlags());
+            openFile.getMainStreamSafe().setSync(true);
+        } catch (BadDescriptorException e) {
+            throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw new RuntimeException(ex);
         }
-        openFile.setMainStream(ChannelStream.open(getRuntime(),
-                new ChannelDescriptor(new FileDescriptorByteChannel(getRuntime(), RubyNumeric.fix2int(fd)),
-                modes)));
-        openFile.setPipeStream(openFile.getMainStream());
-        openFile.setMode(modes.getOpenFileFlags());
-        openFile.getMainStream().setSync(true);
     }
     public static RubyClass createFileDescriptorIOClass(Ruby runtime, RubyModule module) {
         RubyClass result = runtime.defineClassUnder(CLASS_NAME, runtime.fastGetClass("IO"),
                 Allocator.INSTANCE, module);
         result.defineAnnotatedMethods(FileDescriptorIO.class);
         result.defineAnnotatedConstants(FileDescriptorIO.class);
 
         return result;
     }
     @JRubyMethod(name = "new", meta = true)
     public static FileDescriptorIO newInstance(ThreadContext context, IRubyObject recv, IRubyObject fd) {
         return new FileDescriptorIO(context.getRuntime(), fd);
     }
     @JRubyMethod(name = "wrap", required = 1, meta = true)
     public static RubyIO wrap(ThreadContext context, IRubyObject recv, IRubyObject fd) {
         return new FileDescriptorIO(context.getRuntime(), fd);
     }
 }
diff --git a/src/org/jruby/ext/socket/RubyBasicSocket.java b/src/org/jruby/ext/socket/RubyBasicSocket.java
index 547584702a..fc2e387400 100644
--- a/src/org/jruby/ext/socket/RubyBasicSocket.java
+++ b/src/org/jruby/ext/socket/RubyBasicSocket.java
@@ -1,740 +1,764 @@
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
 import static com.kenai.constantine.platform.IPProto.IPPROTO_IP;
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
     protected MulticastStateManager multicastStateManager = null;
 
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
         doNotReverseLookup = runtime.is1_9();
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
-            shutdownInternal(context, 1);
+            try {
+                shutdownInternal(context, 1);
+            } catch (BadDescriptorException e) {
+                throw context.runtime.newErrnoEBADFError();
+            }
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
-            shutdownInternal(context, 0);
+            try {
+                shutdownInternal(context, 0);
+            } catch (BadDescriptorException e) {
+                throw context.runtime.newErrnoEBADFError();
+            }
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
-            return RubyString.newString(context.getRuntime(), openFile.getMainStream().read(RubyNumeric.fix2int(args[0])));
+            return RubyString.newString(context.getRuntime(), openFile.getMainStreamSafe().read(RubyNumeric.fix2int(args[0])));
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
 
-    protected InetSocketAddress getLocalSocket(String caller) {
+    protected InetSocketAddress getLocalSocket(String caller) throws BadDescriptorException {
         Channel socketChannel = getOpenChannel();
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
     
-    protected InetSocketAddress getRemoteSocket() {
+    protected InetSocketAddress getRemoteSocket() throws BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             return (InetSocketAddress)((SocketChannel)socketChannel).socket().getRemoteSocketAddress();
         } else {
             return null;
         }
     }
 
-    private Socket asSocket() {
+    private Socket asSocket() throws BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(!(socketChannel instanceof SocketChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((SocketChannel)socketChannel).socket();
     }
 
-    private ServerSocket asServerSocket() {
+    private ServerSocket asServerSocket() throws BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(!(socketChannel instanceof ServerSocketChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((ServerSocketChannel)socketChannel).socket();
     }
 
-    private DatagramSocket asDatagramSocket() {
+    private DatagramSocket asDatagramSocket() throws BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(!(socketChannel instanceof DatagramChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((DatagramChannel)socketChannel).socket();
     }
 
-    private IRubyObject getBroadcast(Ruby runtime) throws IOException {
+    private IRubyObject getBroadcast(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         return trueFalse(runtime, (socketChannel instanceof DatagramChannel) ? asDatagramSocket().getBroadcast() : false);
     }
 
-    private void setBroadcast(IRubyObject val) throws IOException {
+    private void setBroadcast(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setBroadcast(asBoolean(val));
         }
     }
 
-    private void setKeepAlive(IRubyObject val) throws IOException {
+    private void setKeepAlive(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setKeepAlive(asBoolean(val));
         }
     }
 
-    private void setTcpNoDelay(IRubyObject val) throws IOException {
+    private void setTcpNoDelay(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setTcpNoDelay(asBoolean(val));
         }
     }
 
-    private void joinMulticastGroup(IRubyObject val) throws IOException {
+    private void joinMulticastGroup(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
 
         if(socketChannel instanceof DatagramChannel) {
             if (multicastStateManager == null) {
                 multicastStateManager = new MulticastStateManager();
             }
 
             if (val instanceof RubyString) {
                 byte [] ipaddr_buf = val.convertToString().getBytes();
                 multicastStateManager.addMembership(ipaddr_buf);
             }
         }
     }
 
-    private void setReuseAddr(IRubyObject val) throws IOException {
+    private void setReuseAddr(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if (socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setReuseAddress(asBoolean(val));
         } else if (socketChannel instanceof SocketChannel) {
             asSocket().setReuseAddress(asBoolean(val));
         } else if (socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setReuseAddress(asBoolean(val));
         }
     }
 
-    private void setRcvBuf(IRubyObject val) throws IOException {
+    private void setRcvBuf(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setReceiveBufferSize(asNumber(val));
         } else if(socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setReceiveBufferSize(asNumber(val));
         } else if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setReceiveBufferSize(asNumber(val));
         }
     }
 
-    private void setTimeout(IRubyObject val) throws IOException {
+    private void setTimeout(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setSoTimeout(asNumber(val));
         } else if(socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setSoTimeout(asNumber(val));
         } else if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setSoTimeout(asNumber(val));
         }
     }
 
-    private void setSndBuf(IRubyObject val) throws IOException {
+    private void setSndBuf(IRubyObject val) throws IOException, BadDescriptorException {
         try {
             Channel socketChannel = getOpenChannel();
             if(socketChannel instanceof SocketChannel) {
                 asSocket().setSendBufferSize(asNumber(val));
             } else if(socketChannel instanceof DatagramChannel) {
                 asDatagramSocket().setSendBufferSize(asNumber(val));
             }
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newErrnoEINVALError(iae.getMessage());
         }
     }
 
-    private void setLinger(IRubyObject val) throws IOException {
+    private void setLinger(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
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
 
-    private void setOOBInline(IRubyObject val) throws IOException {
+    private void setOOBInline(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
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
 
-    private IRubyObject getKeepAlive(Ruby runtime) throws IOException {
+    private IRubyObject getKeepAlive(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         return trueFalse(runtime,
                          (socketChannel instanceof SocketChannel) ? asSocket().getKeepAlive() : false
                          );
     }
 
-    private IRubyObject getLinger(Ruby runtime) throws IOException {
+    private IRubyObject getLinger(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
 
         int linger = 0;
         if (socketChannel instanceof SocketChannel) {
             linger = asSocket().getSoLinger();
             if (linger < 0) {
                 linger = 0;
             }
         }
 
         return number(runtime, linger);
     }
 
-    private IRubyObject getOOBInline(Ruby runtime) throws IOException {
+    private IRubyObject getOOBInline(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         return trueFalse(runtime,
                          (socketChannel instanceof SocketChannel) ? asSocket().getOOBInline() : false
                          );
     }
 
-    private IRubyObject getRcvBuf(Ruby runtime) throws IOException {
+    private IRubyObject getRcvBuf(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         return number(runtime,
                       (socketChannel instanceof SocketChannel) ? asSocket().getReceiveBufferSize() : 
                       ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getReceiveBufferSize() : 
                        asDatagramSocket().getReceiveBufferSize())
                       );
     }
 
-    private IRubyObject getSndBuf(Ruby runtime) throws IOException {
+    private IRubyObject getSndBuf(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         return number(runtime,
                       (socketChannel instanceof SocketChannel) ? asSocket().getSendBufferSize() : 
                       ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSendBufferSize() : 0)
                       );
     }
 
-    private IRubyObject getReuseAddr(Ruby runtime) throws IOException {
+    private IRubyObject getReuseAddr(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
 
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
 
-    private IRubyObject getTimeout(Ruby runtime) throws IOException {
+    private IRubyObject getTimeout(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
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
-    private IRubyObject getSoType(Ruby runtime) throws IOException {
+    private IRubyObject getSoType(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
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
+        } catch (BadDescriptorException e) {
+            throw context.getRuntime().newErrnoEBADFError();
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
                     }
                     else if (IPPROTO_IP.value() == level) {
                         if (MulticastStateManager.IP_ADD_MEMBERSHIP == opt) {
                             joinMulticastGroup(val);
                         }
                     } else {
                         throw context.getRuntime().newErrnoENOPROTOOPTError();
                     }
                 }
                 break;
             default:
                 if (IPPROTO_TCP.value() == level && TCP_NODELAY.value() == opt) {
                     setTcpNoDelay(val);
                 }
                 else if (IPPROTO_IP.value() == level) {
                     if (MulticastStateManager.IP_ADD_MEMBERSHIP == opt) {
                         joinMulticastGroup(val);
                     }
                 } else {
                     throw context.getRuntime().newErrnoENOPROTOOPTError();
                 }
             }
+        } catch (BadDescriptorException e) {
+            throw context.getRuntime().newErrnoEBADFError();
         } catch(IOException e) {
             throw context.getRuntime().newErrnoENOPROTOOPTError();
         }
         return context.getRuntime().newFixnum(0);
     }
 
     @Deprecated
     public IRubyObject getsockname() {
         return getsockname(getRuntime().getCurrentContext());
     }
 
     @JRubyMethod(name = "getsockname")
     public IRubyObject getsockname(ThreadContext context) {
         return getSocknameCommon(context, "getsockname");
     }
 
     @JRubyMethod(name = "__getsockname")
     public IRubyObject getsockname_u(ThreadContext context) {
         return getSocknameCommon(context, "__getsockname");
     }
 
     protected IRubyObject getSocknameCommon(ThreadContext context, String caller) {
-        InetSocketAddress sock = getLocalSocket(caller);
-        if(null == sock) {
-            return RubySocket.pack_sockaddr_in(context, null, 0, "0.0.0.0");
-        } else {
-            return RubySocket.pack_sockaddr_in(context, sock);
+        try {
+            InetSocketAddress sock = getLocalSocket(caller);
+            if(null == sock) {
+                return RubySocket.pack_sockaddr_in(context, null, 0, "0.0.0.0");
+            } else {
+               return RubySocket.pack_sockaddr_in(context, sock);
+            }
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
         }
     }
 
     @Deprecated
     public IRubyObject getpeername() {
         return getpeername(getRuntime().getCurrentContext());
     }
     @JRubyMethod(name = {"getpeername", "__getpeername"})
     public IRubyObject getpeername(ThreadContext context) {
-        SocketAddress sock = getRemoteSocket();
-        if(null == sock) {
-            throw context.getRuntime().newIOError("Not Supported");
+        try {
+            SocketAddress sock = getRemoteSocket();
+            if(null == sock) {
+                throw context.getRuntime().newIOError("Not Supported");
+            }
+            return context.getRuntime().newString(sock.toString());
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
         }
-        return context.getRuntime().newString(sock.toString());
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
-        return shutdownInternal(context, how);
+        try {
+            return shutdownInternal(context, how);
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
+        }
     }
 
-    private IRubyObject shutdownInternal(ThreadContext context, int how) {
+    private IRubyObject shutdownInternal(ThreadContext context, int how) throws BadDescriptorException {
         Channel socketChannel;
         switch (how) {
         case 0:
             socketChannel = getOpenChannel();
             try {
                 if (socketChannel instanceof SocketChannel ||
                         socketChannel instanceof DatagramChannel) {
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
             socketChannel = getOpenChannel();
             try {
                 if (socketChannel instanceof SocketChannel ||
                         socketChannel instanceof DatagramChannel) {
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
     
-    private Channel getOpenChannel() {
-        return getOpenFileChecked().getMainStream().getDescriptor().getChannel();
+    private Channel getOpenChannel() throws BadDescriptorException {
+        return getOpenFileChecked().getMainStreamSafe().getDescriptor().getChannel();
     }
 }// RubyBasicSocket
diff --git a/src/org/jruby/ext/socket/RubyIPSocket.java b/src/org/jruby/ext/socket/RubyIPSocket.java
index 0a280d55dc..836991ce63 100644
--- a/src/org/jruby/ext/socket/RubyIPSocket.java
+++ b/src/org/jruby/ext/socket/RubyIPSocket.java
@@ -1,169 +1,190 @@
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
  * Copyright (C) 2007-2010 JRuby Team <team@jruby.org>
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
 
 import java.net.InetAddress;
 import java.net.InetSocketAddress;
 import java.net.UnknownHostException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
+import org.jruby.util.io.BadDescriptorException;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyClass(name="IPSocket", parent="BasicSocket")
 public class RubyIPSocket extends RubyBasicSocket {
     static void createIPSocket(Ruby runtime) {
         RubyClass rb_cIPSocket = runtime.defineClass("IPSocket", runtime.fastGetClass("BasicSocket"), IPSOCKET_ALLOCATOR);
         
         rb_cIPSocket.defineAnnotatedMethods(RubyIPSocket.class);
 
         runtime.getObject().fastSetConstant("IPsocket",rb_cIPSocket);
     }
     
     private static ObjectAllocator IPSOCKET_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIPSocket(runtime, klass);
         }
     };
 
     public RubyIPSocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
     protected static RuntimeException sockerr(Ruby runtime, String msg) {
         return new RaiseException(runtime, runtime.fastGetClass("SocketError"), msg, true);
     }
 
     public IRubyObject packSockaddrFromAddress(InetSocketAddress sock, ThreadContext context) {
         if (sock == null) {
             return RubySocket.pack_sockaddr_in(context, this, 0, "");
         } else {
             return RubySocket.pack_sockaddr_in(context, sock);
         }
     }
 
     private IRubyObject addrFor(ThreadContext context, InetSocketAddress addr) {
         Ruby r = context.getRuntime();
         IRubyObject[] ret = new IRubyObject[4];
         ret[0] = r.newString("AF_INET");
         ret[1] = r.newFixnum(addr.getPort());
         String hostAddress = addr.getAddress().getHostAddress();
         if (doNotReverseLookup(context)) {
             ret[2] = r.newString(hostAddress);
         } else {
             ret[2] = r.newString(addr.getHostName());
         }
         ret[3] = r.newString(hostAddress);
         return r.newArrayNoCopy(ret);
     }
     @Deprecated
     public IRubyObject addr() {
         return addr(getRuntime().getCurrentContext());
     }
     @JRubyMethod
     public IRubyObject addr(ThreadContext context) {
-        InetSocketAddress address = getLocalSocket("addr");
-        if (address == null) {
-            throw context.getRuntime().newErrnoENOTSOCKError("Not socket or not connected");
+        try {
+            InetSocketAddress address = getLocalSocket("addr");
+            if (address == null) {
+                throw context.getRuntime().newErrnoENOTSOCKError("Not socket or not connected");
+            }
+            return addrFor(context, address);
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
         }
-        return addrFor(context, address);
     }
     @Deprecated
     public IRubyObject peeraddr() {
         return peeraddr(getRuntime().getCurrentContext());
     }
     @JRubyMethod
     public IRubyObject peeraddr(ThreadContext context) {
-        InetSocketAddress address = getRemoteSocket();
-        if (address == null) {
-            throw context.getRuntime().newErrnoENOTSOCKError("Not socket or not connected");
+        try {
+            InetSocketAddress address = getRemoteSocket();
+            if (address == null) {
+                throw context.getRuntime().newErrnoENOTSOCKError("Not socket or not connected");
+            }
+            return addrFor(context, address);
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
         }
-        return addrFor(context, address);
     }
     @Override
     protected IRubyObject getSocknameCommon(ThreadContext context, String caller) {
-        InetSocketAddress sock = getLocalSocket(caller);
-        return packSockaddrFromAddress(sock, context);
+        try {
+            InetSocketAddress sock = getLocalSocket(caller);
+            return packSockaddrFromAddress(sock, context);
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
+        }
     }
     @Override
     public IRubyObject getpeername(ThreadContext context) {
-        InetSocketAddress sock = getRemoteSocket();
-        return packSockaddrFromAddress(sock, context);
+        try {
+            InetSocketAddress sock = getRemoteSocket();
+            return packSockaddrFromAddress(sock, context);
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
+        }
     }
     @Deprecated
     public static IRubyObject getaddress(IRubyObject recv, IRubyObject hostname) {
         return getaddress(recv.getRuntime().getCurrentContext(), recv, hostname);
     }
     @JRubyMethod(meta = true)
     public static IRubyObject getaddress(ThreadContext context, IRubyObject recv, IRubyObject hostname) {
         try {
             return context.getRuntime().newString(InetAddress.getByName(hostname.convertToString().toString()).getHostAddress());
         } catch(UnknownHostException e) {
             throw sockerr(context.getRuntime(), "getaddress: name or service not known");
         }
     }
 
     @JRubyMethod(required = 1, optional = 1)
     public IRubyObject recvfrom(ThreadContext context, IRubyObject[] args) {
-        IRubyObject result = recv(context, args);
-        InetSocketAddress sender = getRemoteSocket();
+        try {
+            IRubyObject result = recv(context, args);
+            InetSocketAddress sender = getRemoteSocket();
 
-        int port;
-        String hostName;
-        String hostAddress;
+            int port;
+            String hostName;
+            String hostAddress;
 
-        if (sender == null) {
-            port = 0;
-            hostName = hostAddress = "0.0.0.0";
-        } else {
-            port = sender.getPort();
-            hostName = sender.getHostName();
-            hostAddress = sender.getAddress().getHostAddress();
-        }
+            if (sender == null) {
+                port = 0;
+                hostName = hostAddress = "0.0.0.0";
+            } else {
+                port = sender.getPort();
+                hostName = sender.getHostName();
+                hostAddress = sender.getAddress().getHostAddress();
+            }
 
-        IRubyObject addressArray = context.getRuntime().newArray(
-                new IRubyObject[] {
-                        context.getRuntime().newString("AF_INET"),
-                        context.getRuntime().newFixnum(port),
-                        context.getRuntime().newString(hostName),
-                        context.getRuntime().newString(hostAddress)
-                });
+            IRubyObject addressArray = context.getRuntime().newArray(
+                    new IRubyObject[] {
+                            context.getRuntime().newString("AF_INET"),
+                            context.getRuntime().newFixnum(port),
+                            context.getRuntime().newString(hostName),
+                            context.getRuntime().newString(hostAddress)
+                    });
 
-        return context.getRuntime().newArray(new IRubyObject[] { result, addressArray });
+            return context.getRuntime().newArray(new IRubyObject[] { result, addressArray });
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
+        }
     }
 
 }// RubyIPSocket
diff --git a/src/org/jruby/ext/socket/RubyUNIXSocket.java b/src/org/jruby/ext/socket/RubyUNIXSocket.java
index 6b8b842277..b1e8dd6980 100644
--- a/src/org/jruby/ext/socket/RubyUNIXSocket.java
+++ b/src/org/jruby/ext/socket/RubyUNIXSocket.java
@@ -1,564 +1,567 @@
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
  * Copyright (C) 2008 Ola Bini <ola.bini@gmail.com>
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
 
 import com.kenai.constantine.platform.Fcntl;
 import com.kenai.constantine.platform.Shutdown;
 import com.kenai.constantine.platform.OpenFlags;
 import com.kenai.constantine.platform.SocketLevel;
 import com.kenai.constantine.platform.SocketOption;
 import com.kenai.jaffl.LastError;
 import com.kenai.jaffl.annotations.In;
 import com.kenai.jaffl.annotations.Out;
 import com.kenai.jaffl.annotations.Transient;
 import com.kenai.jaffl.byref.IntByReference;
 import java.io.IOException;
 
 import java.nio.ByteBuffer;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.WritableByteChannel;
 
 import static com.kenai.constantine.platform.AddressFamily.*;
 import static com.kenai.constantine.platform.ProtocolFamily.*;
 import static com.kenai.constantine.platform.Sock.*;
 
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.ext.posix.util.Platform;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.ByteList;
+import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.InvalidValueException;
 
 /**
  * @author <a href="mailto:ola.bini@gmail.com">Ola Bini</a>
  */
 @JRubyClass(name="UNIXSocket", parent="BasicSocket")
 public class RubyUNIXSocket extends RubyBasicSocket {
     protected static volatile LibCSocket INSTANCE = null;
 
     /**
      * Implements reading and writing to a socket fd using recv and send
      */
     private static class UnixDomainSocketChannel implements ReadableByteChannel, WritableByteChannel,
                                                             Shutdownable {
         private final static int SHUT_RD = Shutdown.SHUT_RD.value();
         private final static int SHUT_WR = Shutdown.SHUT_WR.value();
         
         private final Ruby runtime;
         private final int fd;
         private boolean open = true;
 
         public UnixDomainSocketChannel(Ruby runtime, int fd) {
             this.fd = fd;
             this.runtime = runtime;
         }
 
         public void close() throws IOException {
             open = false;
         }
 
         public boolean isOpen() {
             return open;
         }
 
         public int read(ByteBuffer dst) throws IOException {
             int max = dst.remaining();
 
             int v = INSTANCE.recv(fd, dst, max, 0);
 
             if(v != -1) {
                 dst.position(dst.position()+v);
             }
 
             if (v == 0) {
                 // Maintain ReadableByteChannel.read's EOF contract.
                 return -1;
             } else {
                 return v;
             }
         }
 
         public int write(ByteBuffer src) throws IOException {
             int max = src.remaining();
             int v = INSTANCE.send(fd, src, max, 0);
 
             if(v != -1) {
                 src.position(src.position()+v);
             }
 
             return v;
         }
         
         public void shutdownInput() throws IOException {
             int v = INSTANCE.shutdown(fd, SHUT_RD);
             if(v == -1) {
                 rb_sys_fail(runtime, "shutdown(2)");
             }
         }
         
         public void shutdownOutput() throws IOException {
             int v = INSTANCE.shutdown(fd, SHUT_WR);
             if(v == -1) {
                 rb_sys_fail(runtime, "shutdown(2)");
             }
         }
     }
 
     public static boolean tryUnixDomainSocket() {
         if(INSTANCE != null) {
             return true;
         }
 
         if (Platform.IS_WINDOWS) {
             return false; // no UNIXSockets on Windows
         }
 
         try {
             synchronized(RubyUNIXSocket.class) {
                 if(INSTANCE != null) {
                     return true;
                 }
 
                 String[] libnames = Platform.IS_SOLARIS
                         ? new String[] { "socket", "nsl", "c" }
                         : new String[] { "c" };
 
                 INSTANCE = (LibCSocket)com.kenai.jaffl.Library.loadLibrary(LibCSocket.class, libnames);
                 return true;
             }
         } catch(Throwable e) {
             return false;
         }
     }
 
     public static interface LibCSocket {
         int socketpair(int d, int type, int protocol, int[] sv);
         int socket(int domain, int type, int protocol);
         int connect(int s, @In @Transient sockaddr_un name, int namelen);
         int bind(int s, @Transient sockaddr_un name, int namelen);
         int listen(int s, int backlog);
         int accept(int s, @Transient sockaddr_un addr, IntByReference addrlen);
 
         int getsockname(int s, @Out @Transient sockaddr_un addr, IntByReference addrlen);
         int getpeername(int s, @Out @Transient sockaddr_un addr, IntByReference addrlen);
 
         int getsockopt(int s, int level, int optname, @Out byte[] optval, IntByReference optlen);
         int setsockopt(int s, int level, int optname, @In byte[] optval, int optlen);
 
         int recv(int s, @Out ByteBuffer buf, int len, int flags);
         int recvfrom(int s, @Out ByteBuffer buf, int len, int flags, @Out @Transient sockaddr_un from, IntByReference fromlen);
 
         int send(int s, @In ByteBuffer msg, int len, int flags);
 
         int fcntl(int fd, int cmd, int arg);
 
         int unlink(String path);
         int close(int s);
         int shutdown(int s, int how);
 
         void perror(String arg);
 
         // Sockaddr_un has different structure on different platforms.
         // See JRUBY-2213 for more details.
         public static abstract class sockaddr_un extends com.kenai.jaffl.struct.Struct {
             public final static int LENGTH = 106;
             public abstract void setFamily(int family);
             public abstract int getFamily();
             public abstract UTF8String path();
             
             public static final sockaddr_un newInstance() {
                 return Platform.IS_BSD ? new LibCSocket.BSDSockAddrUnix() : new DefaultSockAddrUnix();
             }
         }
 
         public static final class BSDSockAddrUnix extends sockaddr_un {
             public final Signed8 sun_len = new Signed8();
             public final Signed8 sun_family = new Signed8();
             public final UTF8String sun_path = new UTF8String(LENGTH - 2);
             public final void setFamily(int family) {
                 sun_family.set((byte) family);
             }
             public final int getFamily() {
                 return sun_family.get();
             }
             public final UTF8String path() { return sun_path; }
         }
         
         public static final class DefaultSockAddrUnix extends sockaddr_un {
             public final Signed16 sun_family = new Signed16();
             public final UTF8String sun_path = new UTF8String(LENGTH - 2);
 
             public final void setFamily(int family) {
                 sun_family.set((short) family);
             }
             public final int getFamily() {
                 return sun_family.get();
             }
             public final UTF8String path() { return sun_path; }
         }
 
     }
     
     private static ObjectAllocator UNIXSOCKET_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyUNIXSocket(runtime, klass);
         }
     };
 
     static void createUNIXSocket(Ruby runtime) {
         RubyClass rb_cUNIXSocket = runtime.defineClass("UNIXSocket", runtime.fastGetClass("BasicSocket"), UNIXSOCKET_ALLOCATOR);
         runtime.getObject().fastSetConstant("UNIXsocket", rb_cUNIXSocket);
         
         rb_cUNIXSocket.defineAnnotatedMethods(RubyUNIXSocket.class);
     }
 
     public RubyUNIXSocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     protected int fd;
     protected String fpath;
 
     protected static void rb_sys_fail(Ruby runtime, String message) {
         final int n = LastError.getLastError();
 
         IRubyObject arg = (message != null) ? runtime.newString(message) : runtime.getNil();
 
         RubyClass instance = runtime.getErrno(n);
         if(instance == null) {
             instance = runtime.getSystemCallError();
             throw new RaiseException((RubyException)(instance.newInstance(runtime.getCurrentContext(), new IRubyObject[]{arg, runtime.newFixnum(n)}, Block.NULL_BLOCK)));
         } else {
             throw new RaiseException((RubyException)(instance.newInstance(runtime.getCurrentContext(), new IRubyObject[]{arg}, Block.NULL_BLOCK)));
         }
     }
 
     protected final static int F_GETFL = Fcntl.F_GETFL.value();
     protected final static int F_SETFL = Fcntl.F_SETFL.value();
 
     protected final static int O_NONBLOCK = OpenFlags.O_NONBLOCK.value();
 
     protected void init_unixsock(Ruby runtime, IRubyObject _path, boolean server) {
         int status;
         fd = -1;
 
         ByteList path = _path.convertToString().getByteList();
         fpath = path.toString();
         
         try {
             fd = INSTANCE.socket(AF_UNIX.value(), SOCK_STREAM.value(), 0);
         } catch (UnsatisfiedLinkError ule) { }
         if (fd < 0) {
             rb_sys_fail(runtime, "socket(2)");
         }
 
         LibCSocket.sockaddr_un sockaddr = LibCSocket.sockaddr_un.newInstance();
         sockaddr.setFamily(AF_UNIX.value());
 
         if(sockaddr.path().length() <= path.getRealSize()) {
             throw runtime.newArgumentError("too long unix socket path (max: " + (sockaddr.path().length()-1) + "bytes)");
         }
 
         sockaddr.path().set(fpath);
 
         if(server) {
             status = INSTANCE.bind(fd, sockaddr, LibCSocket.sockaddr_un.LENGTH);
         } else {
             try {
                 status = INSTANCE.connect(fd, sockaddr, LibCSocket.sockaddr_un.LENGTH);
             } catch(RuntimeException e) {
                 INSTANCE.close(fd);
                 throw e;
             }
         }
 
         if(status < 0) {
             INSTANCE.close(fd);
             rb_sys_fail(runtime, fpath);
         }
 
         if(server) {
             INSTANCE.listen(fd, 5);
         }
 
         init_sock(runtime);
 
         if(server) {
             openFile.setPath(fpath);
         }
     }
 
     @Override
     public IRubyObject setsockopt(ThreadContext context, IRubyObject lev, IRubyObject optname, IRubyObject val) {
         int level = RubyNumeric.fix2int(lev);
         int opt = RubyNumeric.fix2int(optname);
 
         switch(SocketLevel.valueOf(level)) {
         case SOL_SOCKET:
             switch(SocketOption.valueOf(opt)) {
             case SO_KEEPALIVE: {
                 int res = INSTANCE.setsockopt(fd, level, opt, asBoolean(val) ? new byte[]{32,0,0,0} : new byte[]{0,0,0,0}, 4);
                 if(res == -1) {
                     rb_sys_fail(context.getRuntime(), openFile.getPath());
                 }
             }
                 break;
             default:
                 throw context.getRuntime().newErrnoENOPROTOOPTError();
             }
             break;
         default:
             throw context.getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return context.getRuntime().newFixnum(0);
     }
 
     protected void init_sock(Ruby runtime) {
         try {
             ModeFlags modes = new ModeFlags(ModeFlags.RDWR);
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(new UnixDomainSocketChannel(runtime, fd), modes)));
-            openFile.setPipeStream(openFile.getMainStream());
+            openFile.setPipeStream(openFile.getMainStreamSafe());
             openFile.setMode(modes.getOpenFileFlags());
-            openFile.getMainStream().setSync(true);
+            openFile.getMainStreamSafe().setSync(true);
+        } catch (BadDescriptorException e) {
+            throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException ive) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     @JRubyMethod(visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject path) {
         init_unixsock(context.getRuntime(), path, false);
         return this;
     }
 
     private String unixpath(LibCSocket.sockaddr_un addr, IntByReference len) {
         if (len.getValue() > 2) {
             // There is something valid in the sun_path component
             return addr.path().toString();
         } else {
             return "";
         }
     }
 
     private IRubyObject unixaddr(Ruby runtime, LibCSocket.sockaddr_un addr, IntByReference len) {
         return runtime.newArrayNoCopy(new IRubyObject[]{runtime.newString("AF_UNIX"), runtime.newString(unixpath(addr, len))});
     }
     @Deprecated
     public IRubyObject path() {
         return path(getRuntime().getCurrentContext());
     }
     @JRubyMethod
     public IRubyObject path(ThreadContext context) {
         if(openFile.getPath() == null) {
             LibCSocket.sockaddr_un addr = LibCSocket.sockaddr_un.newInstance();
             IntByReference len = new IntByReference(LibCSocket.sockaddr_un.LENGTH);
             if(INSTANCE.getsockname(fd, addr, len) < 0) {
                 rb_sys_fail(context.getRuntime(), null);
             }
             openFile.setPath(unixpath(addr, len));
         }
         return context.getRuntime().newString(openFile.getPath());
     }
 
     @Deprecated
     public IRubyObject addr() {
         return addr(getRuntime().getCurrentContext());
     }
     @JRubyMethod
     public IRubyObject addr(ThreadContext context) {
         LibCSocket.sockaddr_un addr = LibCSocket.sockaddr_un.newInstance();
         IntByReference len = new IntByReference(LibCSocket.sockaddr_un.LENGTH);
         if(INSTANCE.getsockname(fd, addr, len) < 0) {
             rb_sys_fail(context.getRuntime(), "getsockname(2)");
         }
         return unixaddr(context.getRuntime(), addr, len);
     }
     @Deprecated
     public IRubyObject peeraddr() {
         return peeraddr(getRuntime().getCurrentContext());
     }
     @JRubyMethod
     public IRubyObject peeraddr(ThreadContext context) {
         LibCSocket.sockaddr_un addr = LibCSocket.sockaddr_un.newInstance();
         IntByReference len = new IntByReference(LibCSocket.sockaddr_un.LENGTH);
         if(INSTANCE.getpeername(fd, addr, len) < 0) {
             rb_sys_fail(context.getRuntime(), "getpeername(2)");
         }
         return unixaddr(context.getRuntime(), addr, len);
     }
     @Deprecated
     public IRubyObject recvfrom(IRubyObject[] args) {
         return recvfrom(getRuntime().getCurrentContext(), args);
     }
 
     @JRubyMethod(name = "recvfrom", required = 1, optional = 1)
     public IRubyObject recvfrom(ThreadContext context, IRubyObject[] args) {
         
         LibCSocket.sockaddr_un buf = LibCSocket.sockaddr_un.newInstance();
         IntByReference alen = new IntByReference(LibCSocket.sockaddr_un.LENGTH);
 
         IRubyObject len, flg;
 
         int flags;
 
         if(Arity.checkArgumentCount(context.getRuntime(), args, 1, 2) == 2) {
             flg = args[1];
         } else {
             flg = context.getRuntime().getNil();
         }
 
         len = args[0];
 
         if(flg.isNil()) {
             flags = 0;
         } else {
             flags = RubyNumeric.fix2int(flg);
         }
 
         int buflen = RubyNumeric.fix2int(len);
         byte[] tmpbuf = new byte[buflen];
         ByteBuffer str = ByteBuffer.wrap(tmpbuf);
 
         int slen = INSTANCE.recvfrom(fd, str, buflen, flags, buf, alen);
         if(slen < 0) {
             rb_sys_fail(context.getRuntime(), "recvfrom(2)");
         }
 
         if(slen < buflen) {
             buflen = slen;
         }
 
         RubyString _str = context.getRuntime().newString(new ByteList(tmpbuf, 0, buflen, true));
 
         return context.getRuntime().newArrayNoCopy(new IRubyObject[]{_str, unixaddr(context.getRuntime(), buf, alen)});
     }
 
     @JRubyMethod
     public IRubyObject send_io(IRubyObject path) {
         //TODO: implement, won't do this now
         return  getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject recv_io(IRubyObject[] args) {
         //TODO: implement, won't do this now
         return  getRuntime().getNil();
     }
 
     @Override
     public IRubyObject close() {
         super.close();
         INSTANCE.close(fd);
         return getRuntime().getNil();
     }
     @Deprecated
     public static IRubyObject open(IRubyObject recv, IRubyObject path) {
         return open(recv.getRuntime().getCurrentContext(), recv, path);
     }
     @JRubyMethod(meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject path) {
         return RuntimeHelpers.invoke(context, recv, "new", path);
     }
 
     private static int getSocketType(IRubyObject tp) {
         if(tp instanceof RubyString) {
             String str = tp.toString();
             if("SOCK_STREAM".equals(str)) {
                 return SOCK_STREAM.value();
             } else if("SOCK_DGRAM".equals(str)) {
                 return SOCK_DGRAM.value();
             } else if("SOCK_RAW".equals(str)) {
                 return SOCK_RAW.value();
             } else {
                 return -1;
             }
         }
         return RubyNumeric.fix2int(tp);
     }
     @Deprecated
     public static IRubyObject socketpair(IRubyObject recv, IRubyObject[] args) {
         return socketpair(recv.getRuntime().getCurrentContext(), recv, args);
     }
     @JRubyMethod(name = {"socketpair", "pair"}, optional = 2, meta = true)
     public static IRubyObject socketpair(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         int domain = PF_UNIX.value();
         Ruby runtime = context.getRuntime();
         Arity.checkArgumentCount(runtime, args, 0, 2);
         
         int type;
 
         if(args.length == 0) { 
             type = SOCK_STREAM.value();
         } else {
             type = getSocketType(args[0]);
         }
 
         int protocol;
 
         if(args.length <= 1) {
             protocol = 0;
         } else {
             protocol = RubyNumeric.fix2int(args[1]);
         }
 
         int[] sp = new int[2];
         int ret = -1;
         try {
             ret = INSTANCE.socketpair(domain, type, protocol, sp);
         } catch (UnsatisfiedLinkError ule) { }
         if (ret < 0) {
             rb_sys_fail(runtime, "socketpair(2)");
         }
 
         RubyUNIXSocket sock = (RubyUNIXSocket)(RuntimeHelpers.invoke(context, runtime.fastGetClass("UNIXSocket"), "allocate"));
         sock.fd = sp[0];
         sock.init_sock(runtime);
         RubyUNIXSocket sock2 = (RubyUNIXSocket)(RuntimeHelpers.invoke(context, runtime.fastGetClass("UNIXSocket"), "allocate"));
         sock2.fd = sp[1];
         sock2.init_sock(runtime);
 
         return runtime.newArrayNoCopy(new IRubyObject[]{sock, sock2});
     }
 }
diff --git a/src/org/jruby/java/addons/IOJavaAddons.java b/src/org/jruby/java/addons/IOJavaAddons.java
index bf0bba95b4..ec4a0ec6a9 100644
--- a/src/org/jruby/java/addons/IOJavaAddons.java
+++ b/src/org/jruby/java/addons/IOJavaAddons.java
@@ -1,103 +1,99 @@
 package org.jruby.java.addons;
 
 import java.io.IOException;
 import org.jruby.Ruby;
 import org.jruby.RubyIO;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.IOChannel;
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 
 public class IOJavaAddons {
     // FIXME This whole thing could probably be implemented as a module and
     // mixed into appropriate classes, especially if it uses either
     // IOInput/OutputStream or is smart about the kind of IO-like object
     // it's being used against.
     
     @JRubyMethod
     public static IRubyObject to_inputstream(ThreadContext context, IRubyObject self) {
         RubyIO io = (RubyIO)self;
         Ruby runtime = context.getRuntime();
 
         try {
             io.getOpenFile().checkReadable(context.getRuntime());
-        } catch (PipeException pe) {
-            throw runtime.newErrnoEPIPEError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
         
         return JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), io.getInStream());
     }
     
     @JRubyMethod
     public static IRubyObject to_outputstream(ThreadContext context, IRubyObject self) {
         RubyIO io = (RubyIO)self;
         Ruby runtime = context.getRuntime();
 
         try {
             io.getOpenFile().checkWritable(context.getRuntime());
-        } catch (PipeException pe) {
-            throw runtime.newErrnoEPIPEError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
         
         return JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), io.getOutStream());
     }
 
     @JRubyMethod
     public static IRubyObject to_channel(ThreadContext context, IRubyObject self) {
         RubyIO io = (RubyIO)self;
 
         return JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), io.getChannel());
     }
 
     public static class AnyIO {
         @JRubyMethod(name = "to_inputstream")
         public static IRubyObject any_to_inputstream(ThreadContext context, IRubyObject self) {
             // using IOInputStream may not be the most performance way, but it's easy.
             return JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), new IOInputStream(self));
         }
 
         @JRubyMethod(name = "to_outputstream")
         public static IRubyObject any_to_outputstream(ThreadContext context, IRubyObject self) {
             // using IOOutputStream may not be the most performance way, but it's easy.
             return JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), new IOOutputStream(self));
         }
 
         @JRubyMethod(name = "to_channel")
         public static IRubyObject any_to_channel(ThreadContext context, IRubyObject self) {
             // using IOChannel may not be the most performant way, but it's easy.
             IOChannel channel;
             if (self.respondsTo("read")) {
                 if (self.respondsTo("write") || self.respondsTo("<<")) {
                     channel = new IOChannel.IOReadableWritableByteChannel(self);
                 } else {
                     channel = new IOChannel.IOReadableByteChannel(self);
                 }
             } else {
                 if (self.respondsTo("write") || self.respondsTo("<<")) {
                     channel = new IOChannel.IOWritableByteChannel(self);
                 } else {
                     throw context.getRuntime().newTypeError(self.inspect().toString() + " does not respond to any of read, write, or <<");
                 }
             }
             return JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), channel);
         }
     }
 }
diff --git a/src/org/jruby/libraries/IOWaitLibrary.java b/src/org/jruby/libraries/IOWaitLibrary.java
index a52765eb5b..a887036eb7 100644
--- a/src/org/jruby/libraries/IOWaitLibrary.java
+++ b/src/org/jruby/libraries/IOWaitLibrary.java
@@ -1,90 +1,95 @@
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
  * Copyright (C) 2006 Nick Sieger <nicksieger@gmail.com>
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
 package org.jruby.libraries;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyIO;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
+import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.OpenFile;
 
 /**
  * @author Nick Sieger
  */
 public class IOWaitLibrary implements Library {
 
     public void load(Ruby runtime, boolean wrap) {
         RubyClass ioClass = runtime.getIO();
         ioClass.defineAnnotatedMethods(IOWaitLibrary.class);
     }
 
     /**
      * returns non-nil if input available without blocking, false if EOF or not open/readable, otherwise nil.
      */
     @JRubyMethod(name = "ready?")
     public static IRubyObject ready(ThreadContext context, IRubyObject obj) {
         RubyIO io = (RubyIO)obj;
         try {
             OpenFile openFile = io.getOpenFile();
-            ChannelDescriptor descriptor = openFile.getMainStream().getDescriptor();
-            if (!descriptor.isOpen() || !openFile.getMainStream().getModes().isReadable() || openFile.getMainStream().feof()) {
+            ChannelDescriptor descriptor = openFile.getMainStreamSafe().getDescriptor();
+            if (!descriptor.isOpen() || !openFile.getMainStreamSafe().getModes().isReadable() || openFile.getMainStreamSafe().feof()) {
                 return context.getRuntime().getFalse();
             }
 
-            int avail = openFile.getMainStream().ready();
+            int avail = openFile.getMainStreamSafe().ready();
             if (avail > 0) {
                 return context.getRuntime().newFixnum(avail);
             }
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
         } catch (Exception anyEx) {
             return context.getRuntime().getFalse();
         }
         return context.getRuntime().getNil();
     }
 
     /**
      * waits until input available or timed out and returns self, or nil when EOF reached.
      */
     @JRubyMethod
     public static IRubyObject io_wait(ThreadContext context, IRubyObject obj) {
         RubyIO io = (RubyIO)obj;
         try {
             OpenFile openFile = io.getOpenFile();
-            if (openFile.getMainStream().feof()) {
+            if (openFile.getMainStreamSafe().feof()) {
                 return context.getRuntime().getNil();
             }
-            openFile.getMainStream().waitUntilReady();
+            openFile.getMainStreamSafe().waitUntilReady();
+        } catch (BadDescriptorException e) {
+            throw context.runtime.newErrnoEBADFError();
         } catch (Exception anyEx) {
             return context.getRuntime().getNil();
         }
         return obj;
     }
 }
diff --git a/src/org/jruby/util/io/OpenFile.java b/src/org/jruby/util/io/OpenFile.java
index cff5b1a6fa..b9c2700e25 100644
--- a/src/org/jruby/util/io/OpenFile.java
+++ b/src/org/jruby/util/io/OpenFile.java
@@ -1,335 +1,351 @@
 package org.jruby.util.io;
 
 import java.io.IOException;
 import org.jruby.Ruby;
 import org.jruby.util.ShellLauncher;
 
 public class OpenFile {
 
     public static final int READABLE = 1;
     public static final int WRITABLE = 2;
     public static final int READWRITE = 3;
     public static final int APPEND = 64;
     public static final int CREATE = 128;
     public static final int BINMODE = 4;
     public static final int SYNC = 8;
     public static final int WBUF = 16;
     public static final int RBUF = 32;
     public static final int WSPLIT = 512;
     public static final int WSPLIT_INITIALIZED = 1024;
     public static final int SYNCWRITE = SYNC | WRITABLE;
 
     public static interface Finalizer {
 
         public void finalize(Ruby runtime, boolean raise);
     }
     private Stream mainStream;
     private Stream pipeStream;
     private int mode;
     private Process process;
     private int lineNumber = 0;
     private String path;
     private Finalizer finalizer;
 
     public Stream getMainStream() {
         return mainStream;
     }
 
+    public Stream getMainStreamSafe() throws BadDescriptorException {
+        Stream stream = mainStream;
+        if (stream == null) throw new BadDescriptorException();
+        return stream;
+    }
+
     public void setMainStream(Stream mainStream) {
         this.mainStream = mainStream;
     }
 
     public Stream getPipeStream() {
         return pipeStream;
     }
 
+    public Stream getPipeStreamSafe() throws BadDescriptorException {
+        Stream stream = pipeStream;
+        if (stream == null) throw new BadDescriptorException();
+        return stream;
+    }
+
     public void setPipeStream(Stream pipeStream) {
         this.pipeStream = pipeStream;
     }
 
     public Stream getWriteStream() {
         return pipeStream == null ? mainStream : pipeStream;
     }
 
-    public Stream getWriteStreamOrError() throws BadDescriptorException {
+    public Stream getWriteStreamSafe() throws BadDescriptorException {
         Stream stream = pipeStream == null ? mainStream : pipeStream;
         if (stream == null) throw new BadDescriptorException();
         return stream;
     }
 
     public int getMode() {
         return mode;
     }
 
     public String getModeAsString(Ruby runtime) {
         String modeString = getStringFromMode(mode);
 
         if (modeString == null) {
             throw runtime.newArgumentError("Illegal access modenum " + Integer.toOctalString(mode));
         }
 
         return modeString;
     }
 
     public static String getStringFromMode(int mode) {
         if ((mode & APPEND) != 0) {
             if ((mode & READWRITE) != 0) {
                 return "ab+";
             }
             return "ab";
         }
         switch (mode & READWRITE) {
         case READABLE:
             return "rb";
         case WRITABLE:
             return "wb";
         case READWRITE:
             if ((mode & CREATE) != 0) {
                 return "wb+";
             }
             return "rb+";
         }
         return null;
     }
 
-    public void checkReadable(Ruby runtime) throws IOException, BadDescriptorException, PipeException, InvalidValueException {
+    public void checkReadable(Ruby runtime) throws IOException, BadDescriptorException, InvalidValueException {
         checkClosed(runtime);
 
         if ((mode & READABLE) == 0) {
             throw runtime.newIOError("not opened for reading");
         }
 
         if (((mode & WBUF) != 0 || (mode & (SYNCWRITE | RBUF)) == SYNCWRITE) &&
                 !mainStream.feof() && pipeStream == null) {
             try {
                 // seek to force underlying buffer to flush
                 seek(0, Stream.SEEK_CUR);
+            } catch (PipeException p) {
+                // ignore unseekable streams for purposes of checking readability
             } catch (IOException ioe) {
                 // MRI ignores seek errors, presumably for unseekable files like
                 // serial ports (JRUBY-2979), so we shall too.
             }
         }
     }
 
     public void seek(long offset, int whence) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
-        Stream stream = getWriteStreamOrError();
+        Stream stream = getWriteStreamSafe();
 
         seekInternal(stream, offset, whence);
     }
 
     private void seekInternal(Stream stream, long offset, int whence) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         flushBeforeSeek(stream);
 
         stream.lseek(offset, whence);
     }
 
     private void flushBeforeSeek(Stream stream) throws BadDescriptorException, IOException {
         if ((mode & WBUF) != 0) {
             fflush(stream);
         }
     }
 
     public void fflush(Stream stream) throws IOException, BadDescriptorException {
         while (true) {
             int n = stream.fflush();
             if (n != -1) {
                 break;
             }
         }
         mode &= ~WBUF;
     }
 
-    public void checkWritable(Ruby runtime) throws IOException, BadDescriptorException, InvalidValueException, PipeException {
+    public void checkWritable(Ruby runtime) throws IOException, BadDescriptorException, InvalidValueException {
         checkClosed(runtime);
         if ((mode & WRITABLE) == 0) {
             throw runtime.newIOError("not opened for writing");
         }
         if ((mode & RBUF) != 0 && !mainStream.feof() && pipeStream == null) {
             try {
                 // seek to force read buffer to invalidate
                 seek(0, Stream.SEEK_CUR);
+            } catch (PipeException p) {
+                // ignore unseekable streams for purposes of checking readability
             } catch (IOException ioe) {
                 // MRI ignores seek errors, presumably for unseekable files like
                 // serial ports (JRUBY-2979), so we shall too.
             }
         }
         if (pipeStream == null) {
             mode &= ~RBUF;
         }
     }
 
     public void checkClosed(Ruby runtime) {
         if (mainStream == null && pipeStream == null) {
             throw runtime.newIOError("closed stream");
         }
     }
 
     public boolean isBinmode() {
         return (mode & BINMODE) != 0;
     }
 
     public void setBinmode() {
         mode |= BINMODE;
         if (mainStream != null) {
             mainStream.setBinmode();
         }
     }
 
     public boolean isOpen() {
         return mainStream != null || pipeStream != null;
     }
 
     public boolean isReadable() {
         return (mode & READABLE) != 0;
     }
 
     public boolean isWritable() {
         return (mode & WRITABLE) != 0;
     }
 
     public boolean isReadBuffered() {
         return (mode & RBUF) != 0;
     }
 
     public void setReadBuffered() {
         mode |= RBUF;
     }
 
     public boolean isWriteBuffered() {
         return (mode & WBUF) != 0;
     }
 
     public void setWriteBuffered() {
         mode |= WBUF;
     }
 
     public void setSync(boolean sync) {
         if(sync) {
             mode = mode | SYNC;
         } else {
             mode = mode & ~SYNC;
         }
     }
 
     public boolean isSync() {
         return (mode & SYNC) != 0;
     }
 
     public boolean areBothEOF() throws IOException, BadDescriptorException {
         return mainStream.feof() && (pipeStream != null ? pipeStream.feof() : true);
     }
 
     public void setMode(int modes) {
         this.mode = modes;
     }
 
     public Process getProcess() {
         return process;
     }
 
     public void setProcess(Process process) {
         this.process = process;
     }
 
     public long getPid() {
         return ShellLauncher.getPidFromProcess(process);
     }
 
     public int getLineNumber() {
         return lineNumber;
     }
 
     public void setLineNumber(int lineNumber) {
         this.lineNumber = lineNumber;
     }
 
     public String getPath() {
         return path;
     }
 
     public void setPath(String path) {
         this.path = path;
     }
 
     public boolean isAutoclose() {
         boolean autoclose = true;
         Stream myMain, myPipe;
         if ((myMain = mainStream) != null) autoclose &= myMain.isAutoclose();
         if ((myPipe = pipeStream) != null) autoclose &= myPipe.isAutoclose();
         return autoclose;
     }
 
     public void setAutoclose(boolean autoclose) {
         Stream myMain, myPipe;
         if ((myMain = mainStream) != null) myMain.setAutoclose(autoclose);
         if ((myPipe = pipeStream) != null) myPipe.setAutoclose(autoclose);
     }
 
     public Finalizer getFinalizer() {
         return finalizer;
     }
 
     public void setFinalizer(Finalizer finalizer) {
         this.finalizer = finalizer;
     }
 
     public void cleanup(Ruby runtime, boolean raise) {
         if (finalizer != null) {
             finalizer.finalize(runtime, raise);
         } else {
             finalize(runtime, raise);
         }
     }
 
     public void finalize(Ruby runtime, boolean raise) {
         try {
             ChannelDescriptor main = null;
             ChannelDescriptor pipe = null;
 
             synchronized (this) {
                 Stream ps = pipeStream;
                 if (ps != null) {
                     pipe = ps.getDescriptor();
 
                     // TODO: Ruby logic is somewhat more complicated here, see comments after
                     try {
                         ps.fflush();
                         ps.fclose();
                     } finally {
                         // make sure the pipe stream is set to null
                         pipeStream = null;
                     }
                 }
                 Stream ms = mainStream;
                 if (ms != null) {
                     // TODO: Ruby logic is somewhat more complicated here, see comments after
                     main = ms.getDescriptor();
                     try {
                         if (pipe == null && isWriteBuffered()) {
                             ms.fflush();
                         }
                         ms.fclose();
                     } catch (BadDescriptorException bde) {
                         if (main == pipe) {
                         } else {
                             throw bde;
                         }
                     } finally {
                         // make sure the main stream is set to null
                         mainStream = null;
                     }
                 }
             }
         } catch (IOException ex) {
             if (raise) {
                 throw runtime.newIOErrorFromException(ex);
             }
         } catch (BadDescriptorException ex) {
             if (raise) {
                 throw runtime.newErrnoEBADFError();
             }
         } catch (Throwable t) {
             t.printStackTrace();
         }
     }
 }
