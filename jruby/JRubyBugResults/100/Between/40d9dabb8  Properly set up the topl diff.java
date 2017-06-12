diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 0dfcaee9b8..c20408e25d 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1786 +1,1789 @@
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
 
 import jnr.constants.Constant;
 import jnr.constants.ConstantSet;
 import jnr.constants.platform.Errno;
 import jnr.posix.POSIX;
 import jnr.posix.POSIXFactory;
 import org.jcodings.Encoding;
 import org.joda.time.DateTimeZone;
 import org.jruby.RubyInstanceConfig.CompileMode;
 import org.jruby.ast.Node;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.executable.AbstractScript;
 import org.jruby.ast.executable.RuntimeCache;
 import org.jruby.ast.executable.Script;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.common.RubyWarnings;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.JITCompiler;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.Unrescuable;
 import org.jruby.ext.JRubyPOSIXHandler;
 import org.jruby.ext.LateLoadingLibrary;
 import org.jruby.ext.coverage.CoverageData;
 import org.jruby.ext.ffi.FFI;
 import org.jruby.ext.jruby.JRubyConfigLibrary;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.ir.IRBuilder;
 import org.jruby.ir.IRManager;
 import org.jruby.ir.IRScope;
 import org.jruby.ir.interpreter.Interpreter;
 import org.jruby.ir.targets.JVMVisitor;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.management.BeanManager;
 import org.jruby.management.BeanManagerFactory;
 import org.jruby.management.ClassCache;
 import org.jruby.management.Config;
 import org.jruby.management.ParserStats;
 import org.jruby.parser.IRStaticScopeFactory;
 import org.jruby.parser.Parser;
 import org.jruby.parser.ParserConfiguration;
 import org.jruby.parser.StaticScope;
 import org.jruby.parser.StaticScopeFactory;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
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
 import org.jruby.runtime.invokedynamic.MethodNames;
 import org.jruby.runtime.load.BasicLibraryService;
 import org.jruby.runtime.load.CompiledScriptLoader;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.runtime.opto.Invalidator;
 import org.jruby.runtime.opto.OptoFactory;
 import org.jruby.runtime.profile.ProfileData;
 import org.jruby.runtime.profile.ProfilePrinter;
 import org.jruby.runtime.profile.ProfiledMethod;
 import org.jruby.runtime.profile.ProfileOutput;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 import org.jruby.threading.DaemonThreadFactory;
 import org.jruby.util.ByteList;
 import org.jruby.util.DefinedMessage;
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.KCode;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.collections.WeakHashSet;
 import org.jruby.util.func.Function1;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.SelectorPool;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.jruby.util.unsafe.UnsafeFactory;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.lang.reflect.InvocationTargetException;
 import java.net.BindException;
 import java.nio.channels.ClosedChannelException;
 import java.security.SecureRandom;
 import java.util.*;
 import java.util.concurrent.Callable;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.SynchronousQueue;
 import java.util.concurrent.ThreadPoolExecutor;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.concurrent.atomic.AtomicLong;
 import java.util.regex.Pattern;
 import org.jruby.javasupport.proxy.JavaProxyClassFactory;
 
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
      * The logger used to log relevant bits.
      */
     private static final Logger LOG = LoggerFactory.getLogger("Ruby");
 
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
         
         if (config.getCompileMode() == CompileMode.OFFIR ||
                 config.getCompileMode() == CompileMode.FORCEIR) {
             this.staticScopeFactory = new IRStaticScopeFactory(this);
         } else {
             this.staticScopeFactory = new StaticScopeFactory(this);
         }
 
         this.beanManager        = BeanManagerFactory.create(this, config.isManagementEnabled());
         this.jitCompiler        = new JITCompiler(this);
         this.parserStats        = new ParserStats(this);
         
         Random myRandom;
         try {
             myRandom = new SecureRandom();
         } catch (Throwable t) {
             LOG.debug("unable to instantiate SecureRandom, falling back on Random", t);
             myRandom = new Random();
         }
         this.random = myRandom;
         this.hashSeedK0 = this.random.nextLong();
         this.hashSeedK1 = this.random.nextLong();
         
         this.beanManager.register(new Config(this));
         this.beanManager.register(parserStats);
         this.beanManager.register(new ClassCache(this));
         this.beanManager.register(new org.jruby.management.Runtime(this));
 
         this.runtimeCache = new RuntimeCache();
         runtimeCache.initMethodCache(ClassIndex.MAX_CLASSES * MethodNames.values().length - 1);
         
         constantInvalidator = OptoFactory.newConstantInvalidator();
 
         if (config.isObjectSpaceEnabled()) {
             objectSpacer = ENABLED_OBJECTSPACE;
         } else {
             objectSpacer = DISABLED_OBJECTSPACE;
         }
 
         reinitialize(false);
     }
 
     void reinitialize(boolean reinitCore) {
         this.is1_9              = config.getCompatVersion().is1_9();
         this.is2_0              = config.getCompatVersion().is2_0();
         this.doNotReverseLookupEnabled = is1_9;
 
         if (config.getCompileMode() == CompileMode.OFFIR ||
                 config.getCompileMode() == CompileMode.FORCEIR) {
             this.staticScopeFactory = new IRStaticScopeFactory(this);
         } else {
             this.staticScopeFactory = new StaticScopeFactory(this);
         }
 
         this.in                 = config.getInput();
         this.out                = config.getOutput();
         this.err                = config.getError();
         this.objectSpaceEnabled = config.isObjectSpaceEnabled();
         this.siphashEnabled     = config.isSiphashEnabled();
         this.profile            = config.getProfile();
         this.currentDirectory   = config.getCurrentDirectory();
         this.kcode              = config.getKCode();
 
         if (reinitCore) {
             RubyGlobal.initARGV(this);
         }
     }
     
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
 
     /**
      * Set the global runtime to the given runtime only if it has no been set.
      * 
      * @param runtime the runtime to use for global runtime
      */
     private static synchronized void setGlobalRuntimeFirstTimeOnly(Ruby runtime) {
         if (globalRuntime == null) {
             globalRuntime = runtime;
         }
     }
 
     /**
      * Get the global runtime.
      * 
      * @return the global runtime
      */
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
      * Clear the global runtime.
      */
     public static void clearGlobalRuntime() {
         globalRuntime = null;
     }
 
     /**
      * Get the thread-local runtime for the current thread, or null if unset.
      * 
      * @return the thread-local runtime, or null if unset
      */
     public static Ruby getThreadLocalRuntime() {
         return threadLocalRuntime.get();
     }
     
     /**
      * Set the thread-local runtime to the given runtime.
      *
      * Note that static threadlocals like this one can leak resources across
      * (for example) application redeploys. If you use this, it is your
      * responsibility to clean it up appropriately.
      * 
      * @param ruby the new runtime for thread-local
      */
     public static void setThreadLocalRuntime(Ruby ruby) {
         threadLocalRuntime.set(ruby);
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
         ManyVarsDynamicScope newScope = new ManyVarsDynamicScope(getStaticScopeFactory().newEvalScope(currentScope.getStaticScope()), currentScope);
 
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
             return parseFileFromMain(inputStream, filename, getCurrentContext().getCurrentScope());
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
 
         // we do pre and post load outside the "body" versions to pre-prepare
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
             LOG.error("warning: could not compile: {}; full trace follows", node.getPosition().getFile());
             LOG.error(t.getMessage(), t);
         }
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, boolean dump) {
         if (config.getCompileMode() == CompileMode.FORCEIR) {
             final IRScope scope = IRBuilder.createIRBuilder(getIRManager(), is1_9()).buildRoot((RootNode) node);
             final Class compiled = JVMVisitor.compile(this, scope, classLoader);
             final StaticScope staticScope = scope.getStaticScope();
             staticScope.setModule(getTopSelf().getMetaClass());
             return new AbstractScript() {
                 public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
                     try {
                         return (IRubyObject)compiled.getMethod("__script__0", ThreadContext.class, StaticScope.class, IRubyObject.class, Block.class).invoke(null, getCurrentContext(), scope.getStaticScope(), getTopSelf(), block);
                     } catch (InvocationTargetException ite) {
                         if (ite.getCause() instanceof JumpException) {
                             throw (JumpException)ite.getCause();
                         } else {
                             throw new RuntimeException(ite);
                         }
                     } catch (Exception e) {
                         throw new RuntimeException(e);
                     }
                 }
 
                 public IRubyObject load(ThreadContext context, IRubyObject self, boolean wrap) {
                     try {
                         RuntimeHelpers.preLoadCommon(context, staticScope, false);
                         return __file__(context, self, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
                     } finally {
                         RuntimeHelpers.postLoad(context);
                     }
                 }
             };
         }
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
 
             // __file__ method expects its scope at 0, so prepare that here
-            script.setRootScope(getCurrentContext().getCurrentStaticScope());
+            // this is only needed for the "gets loop" which skips calling load
+            StaticScope rootScope = ((RootNode)node).getStaticScope();
+            if (rootScope.getModule() == null) rootScope.setModule(objectClass);
+            script.setRootScope(rootScope);
 
             if (config.isJitLogging()) {
                 LOG.info("compiled: " + node.getPosition().getFile());
             }
         } catch (Throwable t) {
             handeCompileError(node, t);
         }
         
         return script;
     }
     
     public IRubyObject runScript(Script script) {
         return runScript(script, false);
     }
     
     public IRubyObject runScript(Script script, boolean wrap) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(context, getTopSelf(), wrap);
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
 
     @Deprecated
     public RubyModule fastGetModule(String internedName) {
         return getModule(internedName);
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
     @Deprecated
     public RubyClass fastGetClass(String internedName) {
         return getClass(internedName);
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
             warnings.warn(ID.NO_SUPER_CLASS, "no super class for `" + className + "', Object assumed");
             
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
         } else if (!module.isModule()) {
             throw newTypeError(name + " is not a Module");
         }
 
         return (RubyModule) module;
     }
 
     public KCode getKCode() {
         return kcode;
     }
 
     public void setKCode(KCode kcode) {
         this.kcode = kcode;
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
         // Construct key services
         loadService = config.createLoadService(this);
         posix = POSIXFactory.getPOSIX(new JRubyPOSIXHandler(this), config.isNativeEnabled());
         javaSupport = new JavaSupport(this);
 
         // HACK HACK HACK (jansi (part of jline) + classloader + windows == stacktrace barf)
         if (Platform.IS_WINDOWS) System.setProperty("jline.terminal", "none");
 
         executor = new ThreadPoolExecutor(
                 RubyInstanceConfig.POOL_MIN,
                 RubyInstanceConfig.POOL_MAX,
                 RubyInstanceConfig.POOL_TTL,
                 TimeUnit.SECONDS,
                 new SynchronousQueue<Runnable>(),
                 new DaemonThreadFactory("JRubyWorker"));
         
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
 
         // set up defined messages
         initDefinedMessages();
         
         irManager = new IRManager();
         
         // Initialize the "dummy" class used as a marker
         dummyClass = new RubyClass(this, classClass);
         dummyClass.freeze(tc);
         
         // Create global constants and variables
         RubyGlobal.createGlobals(tc, this);
 
         // Prepare LoadService and load path
         getLoadService().init(config.getLoadPaths());
 
         // initialize builtin libraries
         initBuiltins();
 
         // load JRuby internals, which loads Java support
         if (!RubyInstanceConfig.DEBUG_PARSER) {
             loadService.require("jruby");
         }
 
         // out of base boot mode
         booting = false;
         
         // init Ruby-based kernel
         initRubyKernel();
         
         if(config.isProfiling()) {
             // additional twiddling for profiled mode
             getLoadService().require("jruby/profiler/shutdown_hook");
 
             // recache core methods, since they'll have profiling wrappers now
             kernelModule.invalidateCacheDescendants(); // to avoid already-cached methods
             RubyKernel.recacheBuiltinMethods(this);
             RubyBasicObject.recacheBuiltinMethods(this);
         }
 
         if (config.getLoadGemfile()) {
             loadService.loadFromClassLoader(getClassLoader(), "jruby/bundler/startup.rb", false);
         }
 
         // Require in all libraries specified on command line
         for (String scriptName : config.getRequiredLibraries()) {
             if (is1_9) {
                 topSelf.callMethod(getCurrentContext(), "require", RubyString.newString(this, scriptName));
             } else {
                 loadService.require(scriptName);
             }
         }
     }
 
     private void bootstrap() {
         initCore();
         initExceptions();
     }
 
     private void initDefinedMessages() {
         for (DefinedMessage definedMessage : DefinedMessage.values()) {
             RubyString str = RubyString.newString(this, ByteList.create(definedMessage.getText()));
             str.setFrozen(true);
             definedMessages.put(definedMessage, str);
         }
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
 
         falseObject = new RubyBoolean.False(this);
         trueObject = new RubyBoolean.True(this);
     }
 
     private void initCore() {
         if (profile.allowClass("Data")) {
             defineClass("Data", objectClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         }
 
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         RubyString.createStringClass(this);
 
         encodingService = new EncodingService(this);
 
         RubySymbol.createSymbolClass(this);
         
         recursiveKey = newSymbol("__recursive_key__");
 
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
             // RubyRandom depends on Bignum existence.
             if (is1_9()) {
                 RubyRandom.createRandomClass(this);
             } else {
                 setDefaultRand(new RubyRandom.RandomType(this));
             }
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
         if (profile.allowModule("Signal")) {
             RubySignal.createSignal(this);
         }
         if (profile.allowClass("Continuation")) {
             RubyContinuation.createContinuation(this);
         }
         
         if (profile.allowClass("Enumerator")) {
             RubyEnumerator.defineEnumerator(this);
         }
         
         if (is1_9()) {
             if (RubyInstanceConfig.COROUTINE_FIBERS) {
                 LoadService.reflectedLoad(this, "fiber", "org.jruby.ext.fiber.CoroutineFiberLibrary", getClassLoader(), false);
             } else {
                 LoadService.reflectedLoad(this, "fiber", "org.jruby.ext.fiber.ThreadFiberLibrary", getClassLoader(), false);
             }
         } else {
             if (RubyInstanceConfig.COROUTINE_FIBERS) {
                 addLazyBuiltin("jruby/fiber.jar", "jruby/fiber", "org.jruby.ext.fiber.CoroutineFiberLibrary");
             } else {
                 addLazyBuiltin("jruby/fiber.jar", "jruby/fiber", "org.jruby.ext.fiber.ThreadFiberLibrary");
             }
         }
         
         // Load the JRuby::Config module for accessing configuration settings from Ruby
         new JRubyConfigLibrary().load(this, false);
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
         stopIteration = defineClassIfAllowed("StopIteration", indexError);
         syntaxError = defineClassIfAllowed("SyntaxError", scriptError);
         loadError = defineClassIfAllowed("LoadError", scriptError);
         notImplementedError = defineClassIfAllowed("NotImplementedError", scriptError);
         securityError = defineClassIfAllowed("SecurityError", standardError);
         noMemoryError = defineClassIfAllowed("NoMemoryError", exceptionClass);
         regexpError = defineClassIfAllowed("RegexpError", standardError);
         eofError = defineClassIfAllowed("EOFError", ioError);
         threadError = defineClassIfAllowed("ThreadError", standardError);
         concurrencyError = defineClassIfAllowed("ConcurrencyError", threadError);
         systemStackError = defineClassIfAllowed("SystemStackError", is1_9 ? exceptionClass : standardError);
         zeroDivisionError = defineClassIfAllowed("ZeroDivisionError", standardError);
         floatDomainError  = defineClassIfAllowed("FloatDomainError", rangeError);
 
         if (is1_9()) {
             if (profile.allowClass("EncodingError")) {
                 encodingError = defineClass("EncodingError", standardError, standardError.getAllocator());
                 encodingCompatibilityError = defineClassUnder("CompatibilityError", encodingError, encodingError.getAllocator(), encodingClass);
                 invalidByteSequenceError = defineClassUnder("InvalidByteSequenceError", encodingError, encodingError.getAllocator(), encodingClass);
                 undefinedConversionError = defineClassUnder("UndefinedConversionError", encodingError, encodingError.getAllocator(), encodingClass);
                 converterNotFoundError = defineClassUnder("ConverterNotFoundError", encodingError, encodingError.getAllocator(), encodingClass);
                 fiberError = defineClass("FiberError", standardError, standardError.getAllocator());
             }
             concurrencyError = defineClassIfAllowed("ConcurrencyError", threadError);
             keyError = defineClassIfAllowed("KeyError", indexError);
 
             mathDomainError = defineClassUnder("DomainError", argumentError, argumentError.getAllocator(), mathModule);
             inRecursiveListOperation.set(false);
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
             try {
                 // define EAGAIN now, so that future EWOULDBLOCK will alias to it
                 // see MRI's error.c and its explicit ordering of Errno definitions.
                 createSysErr(Errno.EAGAIN.intValue(), Errno.EAGAIN.name());
                 
                 for (Errno e : Errno.values()) {
                     Constant c = (Constant) e;
                     if (Character.isUpperCase(c.name().charAt(0))) {
                         createSysErr(c.intValue(), c.name());
                     }
                 }
             } catch (Exception e) {
                 // dump the trace and continue
                 // this is currently only here for Android, which seems to have
                 // bugs in its enumeration logic
                 // http://code.google.com/p/android/issues/detail?id=2812
                 LOG.error(e.getMessage(), e);
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
             if (errnos.get(i) == null) {
                 RubyClass errno = getErrno().defineClassUnder(name, systemCallError, systemCallError.getAllocator());
                 errnos.put(i, errno);
                 errno.defineConstant("Errno", newFixnum(i));
             } else {
                 // already defined a class for this errno, reuse it (JRUBY-4747)
                 getErrno().setConstant(name, errnos.get(i));
             }
         }
     }
 
     private void initBuiltins() {
         // We cannot load any .rb and debug new parser features
         if (RubyInstanceConfig.DEBUG_PARSER) return;
         
         addLazyBuiltin("java.rb", "java", "org.jruby.javasupport.Java");
         addLazyBuiltin("jruby.rb", "jruby", "org.jruby.ext.jruby.JRubyLibrary");
         addLazyBuiltin("jruby/util.rb", "jruby/util", "org.jruby.ext.jruby.JRubyUtilLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.ext.jruby.JRubyTypeLibrary");
         addLazyBuiltin("iconv.jar", "iconv", "org.jruby.ext.iconv.IConvLibrary");
         addLazyBuiltin("nkf.jar", "nkf", "org.jruby.ext.nkf.NKFLibrary");
         addLazyBuiltin("stringio.jar", "stringio", "org.jruby.ext.stringio.StringIOLibrary");
         addLazyBuiltin("strscan.jar", "strscan", "org.jruby.ext.strscan.StringScannerLibrary");
         addLazyBuiltin("zlib.jar", "zlib", "org.jruby.ext.zlib.ZlibLibrary");
         addLazyBuiltin("enumerator.jar", "enumerator", "org.jruby.ext.enumerator.EnumeratorLibrary");
         addLazyBuiltin("readline.jar", "readline", "org.jruby.ext.readline.ReadlineService");
         addLazyBuiltin("thread.jar", "thread", "org.jruby.ext.thread.ThreadLibrary");
         addLazyBuiltin("thread.rb", "thread", "org.jruby.ext.thread.ThreadLibrary");
         addLazyBuiltin("digest.jar", "digest.so", "org.jruby.ext.digest.DigestLibrary");
         addLazyBuiltin("digest/md5.jar", "digest/md5", "org.jruby.ext.digest.MD5");
         addLazyBuiltin("digest/rmd160.jar", "digest/rmd160", "org.jruby.ext.digest.RMD160");
         addLazyBuiltin("digest/sha1.jar", "digest/sha1", "org.jruby.ext.digest.SHA1");
         addLazyBuiltin("digest/sha2.jar", "digest/sha2", "org.jruby.ext.digest.SHA2");
         addLazyBuiltin("bigdecimal.jar", "bigdecimal", "org.jruby.ext.bigdecimal.BigDecimalLibrary");
         addLazyBuiltin("io/wait.jar", "io/wait", "org.jruby.ext.io.wait.IOWaitLibrary");
         addLazyBuiltin("etc.jar", "etc", "org.jruby.ext.etc.EtcLibrary");
         addLazyBuiltin("weakref.rb", "weakref", "org.jruby.ext.weakref.WeakRefLibrary");
         addLazyBuiltin("delegate_internal.jar", "delegate_internal", "org.jruby.ext.delegate.DelegateLibrary");
         addLazyBuiltin("timeout.rb", "timeout", "org.jruby.ext.timeout.Timeout");
         addLazyBuiltin("socket.jar", "socket", "org.jruby.ext.socket.SocketLibrary");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.ext.rbconfig.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.ext.jruby.JRubySerializationLibrary");
         addLazyBuiltin("ffi-internal.jar", "ffi-internal", "org.jruby.ext.ffi.FFIService");
         addLazyBuiltin("tempfile.rb", "tempfile", "org.jruby.ext.tempfile.TempfileLibrary");
         addLazyBuiltin("fcntl.rb", "fcntl", "org.jruby.ext.fcntl.FcntlLibrary");
         addLazyBuiltin("rubinius.jar", "rubinius", "org.jruby.ext.rubinius.RubiniusLibrary");
         addLazyBuiltin("yecht.jar", "yecht", "YechtService");
 
         if (is1_9()) {
             addLazyBuiltin("mathn/complex.jar", "mathn/complex", "org.jruby.ext.mathn.Complex");
             addLazyBuiltin("mathn/rational.jar", "mathn/rational", "org.jruby.ext.mathn.Rational");
             addLazyBuiltin("fiber.rb", "fiber", "org.jruby.ext.fiber.FiberExtLibrary");
             addLazyBuiltin("psych.jar", "psych", "org.jruby.ext.psych.PsychLibrary");
             addLazyBuiltin("coverage.jar", "coverage", "org.jruby.ext.coverage.CoverageLibrary");
 
             // TODO: implement something for these?
             Library dummy = new Library() {
                 public void load(Ruby runtime, boolean wrap) throws IOException {
                     // dummy library that does nothing right now
                 }
             };
             addBuiltinIfAllowed("continuation.rb", dummy);
             addBuiltinIfAllowed("io/nonblock.rb", dummy);
         } else {
             addLazyBuiltin("jruby/fiber_ext.rb", "jruby/fiber_ext", "org.jruby.ext.fiber.FiberExtLibrary");
         }
 
         if(RubyInstanceConfig.NATIVE_NET_PROTOCOL) {
             addLazyBuiltin("net/protocol.rb", "net/protocol", "org.jruby.ext.net.protocol.NetProtocolBufferedIOLibrary");
         }
 
         addBuiltinIfAllowed("win32ole.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/win32ole/stub");
             }
         });
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
     }
     
     private void initRubyKernel() {
         // We cannot load any .rb and debug new parser features
         if (RubyInstanceConfig.DEBUG_PARSER) return;
         
         // load Ruby parts of core
         loadService.loadFromClassLoader(getClassLoader(), "jruby/kernel.rb", false);
         
         switch (config.getCompatVersion()) {
             case RUBY1_8:
                 loadService.loadFromClassLoader(getClassLoader(), "jruby/kernel18.rb", false);
                 break;
             case RUBY1_9:
                 loadService.loadFromClassLoader(getClassLoader(), "jruby/kernel19.rb", false);
                 break;
             case RUBY2_0:
                 loadService.loadFromClassLoader(getClassLoader(), "jruby/kernel20.rb", false);
                 break;
         }
     }
 
     private void addLazyBuiltin(String name, String shortName, String className) {
         addBuiltinIfAllowed(name, new LateLoadingLibrary(shortName, className, getClassLoader()));
     }
 
     private void addBuiltinIfAllowed(String name, Library lib) {
         if(profile.allowBuiltin(name)) {
             loadService.addBuiltinLibrary(name,lib);
         }
     }
     
     public IRManager getIRManager() {
         return irManager;
     }
 
     /** Getter for property rubyTopSelf.
      * @return Value of property rubyTopSelf.
      */
     public IRubyObject getTopSelf() {
         return topSelf;
     }
 
     public IRubyObject getRootFiber() {
         return rootFiber;
     }
 
     public void setRootFiber(IRubyObject fiber) {
         rootFiber = fiber;
     }
 
     public void setCurrentDirectory(String dir) {
         currentDirectory = dir;
     }
 
     public String getCurrentDirectory() {
         return currentDirectory;
     }
 
     public void setCurrentLine(int line) {
         currentLine = line;
     }
 
     public int getCurrentLine() {
         return currentLine;
     }
 
     public void setArgsFile(IRubyObject argsFile) {
         this.argsFile = argsFile;
     }
 
     public IRubyObject getArgsFile() {
         return argsFile;
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
 
     ///////////////////////////////////////////////////////////////////////////
     // Cached DynamicMethod objects, used for direct dispatch or for short
     // circuiting dynamic invocation logic.
     ///////////////////////////////////////////////////////////////////////////
 
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
 
     public DynamicMethod getRespondToMethod() {
         return respondTo;
     }
 
     public void setRespondToMethod(DynamicMethod rtm) {
         this.respondTo = rtm;
     }
     
diff --git a/src/org/jruby/ast/ConstNode.java b/src/org/jruby/ast/ConstNode.java
index 956292f399..a037e0996a 100644
--- a/src/org/jruby/ast/ConstNode.java
+++ b/src/org/jruby/ast/ConstNode.java
@@ -1,122 +1,122 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.Ruby;
 import org.jruby.RubyString;
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.DefinedMessage;
 
 /**
  * The access to a Constant.
  */
 public class ConstNode extends Node implements INameNode {
     private String name;
     private volatile transient IRubyObject cachedValue = null;
     private Object generation = -1;
     
     public ConstNode(ISourcePosition position, String name) {
         super(position);
         this.name = name;
     }
 
     public NodeType getNodeType() {
         return NodeType.CONSTNODE;
     }
     
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Object accept(NodeVisitor iVisitor) {
         return iVisitor.visitConstNode(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List<Node> childNodes() {
         return EMPTY_LIST;
     }
     
     public void setName(String name) {
         this.name = name;
     }
     
     @Override
     public IRubyObject interpret(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         IRubyObject value = getValue(context);
 
         // We can callsite cache const_missing if we want
         return value != null ? value :
             context.getCurrentScope().getStaticScope().getModule().callMethod(context, "const_missing", runtime.fastNewSymbol(name));
     }
 
     @Override
     public RubyString definition(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         return context.getCurrentStaticScope().isConstantDefined(name) ? runtime.getDefinedMessage(DefinedMessage.CONSTANT) : null;
     }
     
     public IRubyObject getValue(ThreadContext context) {
         IRubyObject value = cachedValue; // Store to temp so it does null out on us mid-stream
 
         return isCached(context, value) ? value : reCache(context, name);
     }
     
     private boolean isCached(ThreadContext context, IRubyObject value) {
         return value != null && generation == context.runtime.getConstantInvalidator().getData();
     }
     
     public IRubyObject reCache(ThreadContext context, String name) {
         Object newGeneration = context.runtime.getConstantInvalidator().getData();
         IRubyObject value = context.getCurrentStaticScope().getConstant(name);
-            
+        
         cachedValue = value;
             
         if (value != null) generation = newGeneration;
         
         return value;
     }
 }
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index a02713f73f..e0cfb40e92 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -592,2001 +592,2001 @@ public class RuntimeHelpers {
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, arg1, block);
         }
         return method.call(context, self, superClass, name, arg0, arg1, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, arg1, arg2, block);
         }
         return method.call(context, self, superClass, name, arg0, arg1, arg2, block);
     }
 
     public static RubyArray ensureRubyArray(IRubyObject value) {
         return ensureRubyArray(value.getRuntime(), value);
     }
 
     public static RubyArray ensureRubyArray(Ruby runtime, IRubyObject value) {
         return value instanceof RubyArray ? (RubyArray)value : RubyArray.newArray(runtime, value);
     }
 
     public static RubyArray ensureMultipleAssignableRubyArray(IRubyObject value, Ruby runtime, boolean masgnHasHead) {
         if (!(value instanceof RubyArray)) {
             if (runtime.is1_9()) {
                 value = ArgsUtil.convertToRubyArray19(runtime, value, masgnHasHead);
             } else {
                 value = ArgsUtil.convertToRubyArray(runtime, value, masgnHasHead);                
             }
         }
         return (RubyArray) value;
     }
     
     public static IRubyObject fetchClassVariable(Ruby runtime, StaticScope scope,
             IRubyObject self, String name) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(runtime, scope);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.getClassVar(name);
     }
     
     public static IRubyObject getConstant(ThreadContext context, String internedName) {
         Ruby runtime = context.runtime;
 
         return context.getCurrentScope().getStaticScope().getConstantWithConstMissing(internedName);
     }
 
     public static IRubyObject nullToNil(IRubyObject value, ThreadContext context) {
         return value != null ? value : context.nil;
     }
     
     public static IRubyObject nullToNil(IRubyObject value, Ruby runtime) {
         return value != null ? value : runtime.getNil();
     }
     
     public static IRubyObject nullToNil(IRubyObject value, IRubyObject nil) {
         return value != null ? value : nil;
     }
     
     public static RubyClass prepareSuperClass(Ruby runtime, IRubyObject rubyClass) {
         RubyClass.checkInheritable(rubyClass); // use the same logic as in EvaluationState
         return (RubyClass)rubyClass;
     }
     
     public static RubyModule prepareClassNamespace(ThreadContext context, StaticScope scope, IRubyObject rubyModule) {
         if (rubyModule == null || rubyModule.isNil()) {
             rubyModule = scope.getModule();
 
             if (rubyModule == null) {
                 throw context.runtime.newTypeError("no outer class/module");
             }
         }
 
         if (rubyModule instanceof RubyModule) {
             return (RubyModule)rubyModule;
         } else {
             throw context.runtime.newTypeError(rubyModule + " is not a class/module");
         }
     }
     
     public static IRubyObject setClassVariable(Ruby runtime, StaticScope scope,
             IRubyObject self, String name, IRubyObject value) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(runtime, scope);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static IRubyObject declareClassVariable(Ruby runtime, StaticScope scope, IRubyObject self, String name, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(runtime, scope);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static void handleArgumentSizes(ThreadContext context, Ruby runtime, int given, int required, int opt, int rest) {
         if (opt == 0) {
             if (rest < 0) {
                 // no opt, no rest, exact match
                 if (given != required) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
                 }
             } else {
                 // only rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
                 }
             }
         } else {
             if (rest < 0) {
                 // opt but no rest, must be at least required and no more than required + opt
                 if (given < required) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
                 } else if (given > (required + opt)) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + (required + opt) + ")");
                 }
             } else {
                 // opt and rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
                 }
             }
         }
     }
     
     /**
      * If it's Redo, Next, or Break, rethrow it as a normal exception for while to handle
      * @param re
      * @param context
      */
     public static Throwable unwrapRedoNextBreakOrJustLocalJump(RaiseException re, ThreadContext context) {
         RubyException exception = re.getException();
         if (context.runtime.getLocalJumpError().isInstance(exception)) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             switch (jumpError.getReason()) {
             case REDO:
                 return JumpException.REDO_JUMP;
             case NEXT:
                 return new JumpException.NextJump(jumpError.exit_value());
             case BREAK:
                 return new JumpException.BreakJump(context.getFrameJumpTarget(), jumpError.exit_value());
             }
         }
         return re;
     }
     
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
         proc = TypeConverter.convertToType(proc, runtime.getProc(), "to_proc", false);
 
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
         IRubyObject backref = context.getCurrentScope().getBackRef(context.runtime);
         
         if(backref instanceof RubyMatchData) {
             ((RubyMatchData)backref).use();
         }
         return backref;
     }
     
     public static IRubyObject backrefLastMatch(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.runtime);
         
         return RubyRegexp.last_match(backref);
     }
     
     public static IRubyObject backrefMatchPre(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.runtime);
         
         return RubyRegexp.match_pre(backref);
     }
     
     public static IRubyObject backrefMatchPost(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.runtime);
         
         return RubyRegexp.match_post(backref);
     }
     
     public static IRubyObject backrefMatchLast(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.runtime);
         
         return RubyRegexp.match_last(backref);
     }
 
     public static IRubyObject[] getArgValues(ThreadContext context) {
         return context.getCurrentScope().getArgValues();
     }
     
     public static IRubyObject callZSuper(Ruby runtime, ThreadContext context, Block block, IRubyObject self) {
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
     
     public static IRubyObject throwReturnJump(IRubyObject result, ThreadContext context) {
         throw context.returnJump(result);
     }
     
     public static IRubyObject breakJumpInWhile(JumpException.BreakJump bj, ThreadContext context) {
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
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, value, "unexpected break");
     }
     
     public static IRubyObject[] concatObjectArrays(IRubyObject[] array, IRubyObject[] add) {
         IRubyObject[] newArray = new IRubyObject[array.length + add.length];
         System.arraycopy(array, 0, newArray, 0, array.length);
         System.arraycopy(add, 0, newArray, array.length, add.length);
         return newArray;
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject[] exceptions, ThreadContext context) {
         for (int i = 0; i < exceptions.length; i++) {
             IRubyObject result = isExceptionHandled(currentException, exceptions[i], context);
             if (result.isTrue()) return result;
         }
         return context.runtime.getFalse();
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception, ThreadContext context) {
         return isExceptionHandled((IRubyObject)currentException, exception, context);
     }
 
     public static IRubyObject isExceptionHandled(IRubyObject currentException, IRubyObject exception, ThreadContext context) {
         Ruby runtime = context.runtime;
         if (!runtime.getModule().isInstance(exception)) {
             throw runtime.newTypeError("class or module required for rescue clause");
         }
         IRubyObject result = invoke(context, exception, "===", currentException);
         if (result.isTrue()) return result;
         return runtime.getFalse();
     }
     
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception0, IRubyObject exception1, ThreadContext context) {
         IRubyObject result = isExceptionHandled(currentException, exception0, context);
         if (result.isTrue()) return result;
         return isExceptionHandled(currentException, exception1, context);
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception0, IRubyObject exception1, IRubyObject exception2, ThreadContext context) {
         IRubyObject result = isExceptionHandled(currentException, exception0, context);
         if (result.isTrue()) return result;
         return isExceptionHandled(currentException, exception1, exception2, context);
     }
 
     private static boolean checkJavaException(Throwable throwable, IRubyObject catchable, ThreadContext context) {
         Ruby runtime = context.runtime;
         if (
                 // rescue exception needs to catch Java exceptions
                 runtime.getException() == catchable ||
 
                 // rescue Object needs to catch Java exceptions
                 runtime.getObject() == catchable ||
 
                 // rescue StandardError needs t= catch Java exceptions
                 runtime.getStandardError() == catchable) {
 
             if (throwable instanceof RaiseException) {
                 return isExceptionHandled(((RaiseException)throwable).getException(), catchable, context).isTrue();
             }
 
             // let Ruby exceptions decide if they handle it
             return isExceptionHandled(JavaUtil.convertJavaToUsableRubyObject(runtime, throwable), catchable, context).isTrue();
 
         } else if (runtime.getNativeException() == catchable) {
             // NativeException catches Java exceptions, lazily creating the wrapper
             return true;
 
         } else if (catchable instanceof RubyClass && catchable.getInstanceVariables().hasInstanceVariable("@java_class")) {
             RubyClass rubyClass = (RubyClass)catchable;
             JavaClass javaClass = (JavaClass)rubyClass.getInstanceVariable("@java_class");
             if (javaClass != null) {
                 Class cls = javaClass.javaClass();
                 if (cls.isInstance(throwable)) {
                     return true;
                 }
             }
         }
 
         return false;
     }
     
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject[] throwables, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
 
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwables, context);
         } else {
             if (throwables.length == 0) {
                 // no rescue means StandardError, which rescues Java exceptions
                 return context.runtime.getTrue();
             } else {
                 for (int i = 0; i < throwables.length; i++) {
                     if (checkJavaException(currentThrowable, throwables[i], context)) {
                         return context.runtime.getTrue();
                     }
                 }
             }
 
             return context.runtime.getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
 
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable, context);
         } else {
             if (checkJavaException(currentThrowable, throwable, context)) {
                 return context.runtime.getTrue();
             }
 
             return context.runtime.getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable0, IRubyObject throwable1, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
 
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable0, throwable1, context);
         } else {
             if (checkJavaException(currentThrowable, throwable0, context)) {
                 return context.runtime.getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1, context)) {
                 return context.runtime.getTrue();
             }
 
             return context.runtime.getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable0, IRubyObject throwable1, IRubyObject throwable2, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
         
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable0, throwable1, throwable2, context);
         } else {
             if (checkJavaException(currentThrowable, throwable0, context)) {
                 return context.runtime.getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1, context)) {
                 return context.runtime.getTrue();
             }
             if (checkJavaException(currentThrowable, throwable2, context)) {
                 return context.runtime.getTrue();
             }
 
             return context.runtime.getFalse();
         }
     }
 
     public static void storeExceptionInErrorInfo(Throwable currentThrowable, ThreadContext context) {
         IRubyObject exception = null;
         if (currentThrowable instanceof RaiseException) {
             exception = ((RaiseException)currentThrowable).getException();
         } else {
             exception = JavaUtil.convertJavaToUsableRubyObject(context.runtime, currentThrowable);
         }
         context.setErrorInfo(exception);
     }
 
     public static void storeNativeExceptionInErrorInfo(Throwable currentThrowable, ThreadContext context) {
         IRubyObject exception = null;
         if (currentThrowable instanceof RaiseException) {
             exception = ((RaiseException)currentThrowable).getException();
         } else {
             Ruby runtime = context.runtime;
 
             // wrap Throwable in a NativeException object
             exception = new NativeException(runtime, runtime.getNativeException(), currentThrowable);
             ((NativeException)exception).prepareIntegratedBacktrace(context, currentThrowable.getStackTrace());
         }
         context.setErrorInfo(exception);
     }
 
     public static void clearErrorInfo(ThreadContext context) {
         context.setErrorInfo(context.runtime.getNil());
     }
     
     public static void checkSuperDisabledOrOutOfMethod(ThreadContext context) {
         if (context.getFrameKlazz() == null) {
             String name = context.getFrameName();
             if (name != null) {
                 throw context.runtime.newNameError("superclass method '" + name + "' disabled", name);
             } else {
                 throw context.runtime.newNoMethodError("super called outside of method", null, context.runtime.getNil());
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
 
     public static RubyArray createSubarray(RubyArray input, int start, int post) {
         return (RubyArray)input.subseqLight(start, input.size() - post - start);
     }
     
     public static RubyArray createSubarray(IRubyObject[] input, Ruby runtime, int start) {
         if (start >= input.length) {
             return RubyArray.newEmptyArray(runtime);
         } else {
             return RubyArray.newArrayNoCopy(runtime, input, start);
         }
     }
 
     public static RubyArray createSubarray(IRubyObject[] input, Ruby runtime, int start, int exclude) {
         int length = input.length - exclude - start;
         if (length <= 0) {
             return RubyArray.newEmptyArray(runtime);
         } else {
             return RubyArray.newArrayNoCopy(runtime, input, start, length);
         }
     }
 
     public static IRubyObject elementOrNull(IRubyObject[] input, int element) {
         if (element >= input.length) {
             return null;
         } else {
             return input[element];
         }
     }
 
     public static IRubyObject optElementOrNull(IRubyObject[] input, int element, int postCount) {
         if (element + postCount >= input.length) {
             return null;
         } else {
             return input[element];
         }
     }
 
     public static IRubyObject elementOrNil(IRubyObject[] input, int element, IRubyObject nil) {
         if (element >= input.length) {
             return nil;
         } else {
             return input[element];
         }
     }
 
     public static IRubyObject postElementOrNil(IRubyObject[] input, int postCount, int postIndex, IRubyObject nil) {
         int aryIndex = input.length - postCount + postIndex;
         if (aryIndex >= input.length || aryIndex < 0) {
             return nil;
         } else {
             return input[aryIndex];
         }
     }
     
     public static RubyBoolean isWhenTriggered(IRubyObject expression, IRubyObject expressionsObject, ThreadContext context) {
         RubyArray expressions = RuntimeHelpers.splatValue(expressionsObject);
         for (int j = 0,k = expressions.getLength(); j < k; j++) {
             IRubyObject condition = expressions.eltInternal(j);
 
             if ((expression != null && condition.callMethod(context, "===", expression).isTrue()) ||
                     (expression == null && condition.isTrue())) {
                 return context.runtime.getTrue();
             }
         }
 
         return context.runtime.getFalse();
     }
     
     public static IRubyObject setConstantInModule(ThreadContext context, String name, IRubyObject value, IRubyObject module) {
         if (!(module instanceof RubyModule)) {
             throw context.runtime.newTypeError(module.toString() + " is not a class/module");
         }
         ((RubyModule)module).setConstant(name, value);
 
         return value;
     }
 
     public static IRubyObject setConstantInCurrent(IRubyObject value, ThreadContext context, String name) {
         return context.getCurrentStaticScope().setConstant(name, value);
     }
 
     public static IRubyObject retryJump() {
         throw JumpException.RETRY_JUMP;
     }
 
     public static IRubyObject redoJump() {
         throw JumpException.REDO_JUMP;
     }
 
     public static IRubyObject redoLocalJumpError(Ruby runtime) {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.REDO, runtime.getNil(), "unexpected redo");
     }
 
     public static IRubyObject nextJump(IRubyObject value) {
         throw new JumpException.NextJump(value);
     }
     
     public static IRubyObject nextLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.NEXT, value, "unexpected next");
     }
     
     public static final int MAX_SPECIFIC_ARITY_OBJECT_ARRAY = 10;
     
     public static IRubyObject[] anewarrayIRubyObjects(int size) {
         return new IRubyObject[size];
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, int start) {
         ary[start] = one;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, int start) {
         ary[start] = one;
         ary[start+1] = two;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         ary[start+7] = eight;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         ary[start+7] = eight;
         ary[start+8] = nine;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, IRubyObject ten, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         ary[start+7] = eight;
         ary[start+8] = nine;
         ary[start+9] = ten;
         return ary;
     }
     
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
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six) {
         return new IRubyObject[] {one, two, three, four, five, six};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven) {
         return new IRubyObject[] {one, two, three, four, five, six, seven};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight) {
         return new IRubyObject[] {one, two, three, four, five, six, seven, eight};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine) {
         return new IRubyObject[] {one, two, three, four, five, six, seven, eight, nine};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, IRubyObject ten) {
         return new IRubyObject[] {one, two, three, four, five, six, seven, eight, nine, ten};
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one) {
         return RubyArray.newArrayLight(runtime, one);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two) {
         return RubyArray.newArrayLight(runtime, one, two);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three) {
         return RubyArray.newArrayLight(runtime, one, two, three);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four) {
         return RubyArray.newArrayLight(runtime, one, two, three, four);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven, eight);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven, eight, nine);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, IRubyObject ten) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven, eight, nine, ten);
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
         hash.fastASetCheckString(runtime, key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         hash.fastASetCheckString(runtime, key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         hash.fastASetCheckString(runtime, key2, value2);
         hash.fastASetCheckString(runtime, key3, value3);
         return hash;
     }
 
     public static RubyHash constructSmallHash(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newSmallHash(runtime);
         hash.fastASetSmallCheckString(runtime, key1, value1);
         return hash;
     }
 
     public static RubyHash constructSmallHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newSmallHash(runtime);
         hash.fastASetSmallCheckString(runtime, key1, value1);
         hash.fastASetSmallCheckString(runtime, key2, value2);
         return hash;
     }
 
     public static RubyHash constructSmallHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetSmallCheckString(runtime, key1, value1);
         hash.fastASetSmallCheckString(runtime, key2, value2);
         hash.fastASetSmallCheckString(runtime, key3, value3);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         hash.fastASetCheckString19(runtime, key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         hash.fastASetCheckString19(runtime, key2, value2);
         hash.fastASetCheckString19(runtime, key3, value3);
         return hash;
     }
 
     public static RubyHash constructSmallHash19(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newSmallHash(runtime);
         hash.fastASetSmallCheckString19(runtime, key1, value1);
         return hash;
     }
 
     public static RubyHash constructSmallHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newSmallHash(runtime);
         hash.fastASetSmallCheckString19(runtime, key1, value1);
         hash.fastASetSmallCheckString19(runtime, key2, value2);
         return hash;
     }
 
     public static RubyHash constructSmallHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newSmallHash(runtime);
         hash.fastASetSmallCheckString19(runtime, key1, value1);
         hash.fastASetSmallCheckString19(runtime, key2, value2);
         hash.fastASetSmallCheckString19(runtime, key3, value3);
         return hash;
     }
 
     public static IRubyObject undefMethod(ThreadContext context, Object nameArg) {
         RubyModule module = context.getRubyClass();
 
         String name = (nameArg instanceof String) ?
             (String) nameArg : nameArg.toString();
 
         if (module == null) {
             throw context.runtime.newTypeError("No class to undef method '" + name + "'.");
         }
 
         module.undef(context, name);
 
         return context.runtime.getNil();
     }
     
     public static IRubyObject defineAlias(ThreadContext context, IRubyObject self, Object newNameArg, Object oldNameArg) {
         Ruby runtime = context.runtime;
         RubyModule module = context.getRubyClass();
    
         if (module == null || self instanceof RubyFixnum || self instanceof RubySymbol){
             throw runtime.newTypeError("no class to make alias");
         }
 
         String newName = (newNameArg instanceof String) ?
             (String) newNameArg : newNameArg.toString();
         String oldName = (oldNameArg instanceof String) ? 
             (String) oldNameArg : oldNameArg.toString();
 
         module.defineAlias(newName, oldName);
         module.callMethod(context, "method_added", runtime.newSymbol(newName));
    
         return runtime.getNil();
     }
     
     public static IRubyObject negate(IRubyObject value, Ruby runtime) {
         if (value.isTrue()) return runtime.getFalse();
         return runtime.getTrue();
     }
     
     public static IRubyObject stringOrNil(ByteList value, ThreadContext context) {
         if (value == null) return context.nil;
         return RubyString.newStringShared(context.runtime, value);
     }
     
     public static StaticScope preLoad(ThreadContext context, String[] varNames) {
         StaticScope staticScope = context.runtime.getStaticScopeFactory().newLocalScope(null, varNames);
         preLoadCommon(context, staticScope, false);
 
         return staticScope;
     }
 
     public static StaticScope preLoad(ThreadContext context, String scopeString, boolean wrap) {
-        StaticScope staticScope = decodeScope(context, context.getCurrentStaticScope(), scopeString);
+        StaticScope staticScope = decodeScope(context, null, scopeString);
         preLoadCommon(context, staticScope, wrap);
 
         return staticScope;
     }
 
     public static void preLoadCommon(ThreadContext context, StaticScope staticScope, boolean wrap) {
         RubyClass objectClass = context.runtime.getObject();
         IRubyObject topLevel = context.runtime.getTopSelf();
         if (wrap) {
             staticScope.setModule(RubyModule.newModule(context.runtime));
         } else {
             staticScope.setModule(objectClass);
         }
         DynamicScope scope = DynamicScope.newDynamicScope(staticScope);
 
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
         context.preNodeEval(objectClass, topLevel);
     }
     
     public static void postLoad(ThreadContext context) {
         context.postNodeEval();
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
 
     public static IRubyObject match3_19(RubyRegexp regexp, IRubyObject value, ThreadContext context) {
         if (value instanceof RubyString) {
             return regexp.op_match19(context, value);
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
         return context.getCurrentScope().setLastLine(value);
     }
 
     public static IRubyObject getLastLine(Ruby runtime, ThreadContext context) {
         return context.getCurrentScope().getLastLine(runtime);
     }
 
     public static IRubyObject setBackref(Ruby runtime, ThreadContext context, IRubyObject value) {
         if (!value.isNil() && !(value instanceof RubyMatchData)) throw runtime.newTypeError(value, runtime.getMatchData());
         return context.getCurrentScope().setBackRef(value);
     }
 
     public static IRubyObject getBackref(Ruby runtime, ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(runtime);
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
         Ruby runtime = value.getRuntime();
         return arrayValue(runtime.getCurrentContext(), runtime, value);
     }
     
     public static RubyArray arrayValue(ThreadContext context, Ruby runtime, IRubyObject value) {
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
             // remove this hack too.
 
             if (value.respondsTo("to_a") && value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime.getKernel()) {
                 IRubyObject avalue = value.callMethod(context, "to_a");
                 if (!(avalue instanceof RubyArray)) {
                     if (runtime.is1_9() && avalue.isNil()) {
                         return runtime.newArray(value);
                     } else {
                         throw runtime.newTypeError("`to_a' did not return Array");
                     }
                 }
                 return (RubyArray)avalue;
             } else {
                 return runtime.newArray(value);
             }
         }
         return (RubyArray)tmp;
     }
 
     public static IRubyObject aryToAry(IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return TypeConverter.convertToType(value, value.getRuntime().getArray(), "to_ary", false);
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
 
     public static IRubyObject aValueSplat19(IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             return value.getRuntime().getNil();
         }
 
         return (RubyArray) value;
     }
 
     public static RubyArray splatValue(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newArray(value);
         }
 
         return arrayValue(value);
     }
 
     public static RubyArray splatValue19(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newEmptyArray();
         }
 
         return arrayValue(value);
     }
     
     public static IRubyObject unsplatValue19(IRubyObject argsResult) {
         if (argsResult instanceof RubyArray) {
             RubyArray array = (RubyArray) argsResult;
                     
             if (array.size() == 1) {
                 IRubyObject newResult = array.eltInternal(0);
                 if (!((newResult instanceof RubyArray) && ((RubyArray) newResult).size() == 0)) {
                     argsResult = newResult;
                 }
             }
         }        
         return argsResult;
     }
 
     public static IRubyObject unsplatValue19IfArityOne(IRubyObject argsResult, Block block) {
         if (block.isGiven() && block.arity().getValue() > 1) argsResult = RuntimeHelpers.unsplatValue19(argsResult);
         return argsResult;
     }
         
     public static IRubyObject[] splatToArguments(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         
         if (value.isNil()) {
             return runtime.getSingleNilArray();
         }
         
         return splatToArgumentsCommon(runtime, value);
     }
     
     public static IRubyObject[] splatToArguments19(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         
         if (value.isNil()) {
             return IRubyObject.NULL_ARRAY;
         }
         
         return splatToArgumentsCommon(runtime, value);
     }
     
     private static IRubyObject[] splatToArgumentsCommon(Ruby runtime, IRubyObject value) {
         
         if (value.isNil()) {
             return runtime.getSingleNilArray();
         }
         
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             return convertSplatToJavaArray(runtime, value);
         }
         return ((RubyArray)tmp).toJavaArrayMaybeUnsafe();
     }
     
     private static IRubyObject[] convertSplatToJavaArray(Ruby runtime, IRubyObject value) {
         // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
         // remove this hack too.
 
         RubyClass metaClass = value.getMetaClass();
         DynamicMethod method = metaClass.searchMethod("to_a");
         if (method.isUndefined() || method.getImplementationClass() == runtime.getKernel()) {
             return new IRubyObject[] {value};
         }
 
         IRubyObject avalue = method.call(runtime.getCurrentContext(), value, metaClass, "to_a");
         if (!(avalue instanceof RubyArray)) {
             if (runtime.is1_9() && avalue.isNil()) {
                 return new IRubyObject[] {value};
             } else {
                 throw runtime.newTypeError("`to_a' did not return Array");
             }
         }
         return ((RubyArray)avalue).toJavaArray();
     }
     
     public static IRubyObject[] argsCatToArguments(IRubyObject[] args, IRubyObject cat) {
         IRubyObject[] ary = splatToArguments(cat);
         return argsCatToArgumentsCommon(args, ary, cat);
     }
     
     public static IRubyObject[] argsCatToArguments19(IRubyObject[] args, IRubyObject cat) {
         IRubyObject[] ary = splatToArguments19(cat);
         return argsCatToArgumentsCommon(args, ary, cat);
     }
     
     private static IRubyObject[] argsCatToArgumentsCommon(IRubyObject[] args, IRubyObject[] ary, IRubyObject cat) {
         if (ary.length > 0) {
             IRubyObject[] newArgs = new IRubyObject[args.length + ary.length];
             System.arraycopy(args, 0, newArgs, 0, args.length);
             System.arraycopy(ary, 0, newArgs, args.length, ary.length);
             args = newArgs;
         }
         
         return args;
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
 
     private static DynamicMethod constructNormalMethod(
             MethodFactory factory,
             String javaName,
             String name,
             RubyModule containingClass,
             ISourcePosition position,
             int arity,
             StaticScope scope,
             Visibility visibility,
             Object scriptObject,
             CallConfiguration callConfig,
             String parameterDesc) {
         
         DynamicMethod method;
 
         if (name.equals("initialize") || name.equals("initialize_copy") || visibility == Visibility.MODULE_FUNCTION) {
             visibility = Visibility.PRIVATE;
         }
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             method = factory.getCompiledMethodLazily(
                     containingClass,
                     javaName,
                     Arity.createArity(arity),
                     visibility,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         } else {
             method = factory.getCompiledMethod(
                     containingClass,
                     javaName,
                     Arity.createArity(arity),
                     visibility,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         }
 
         return method;
     }
 
     private static DynamicMethod constructSingletonMethod(
             MethodFactory factory,
             String javaName,
             RubyClass rubyClass,
             ISourcePosition position,
             int arity,
             StaticScope scope,
             Object scriptObject,
             CallConfiguration callConfig,
             String parameterDesc) {
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             return factory.getCompiledMethodLazily(
                     rubyClass,
                     javaName,
                     Arity.createArity(arity),
                     Visibility.PUBLIC,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         } else {
             return factory.getCompiledMethod(
                     rubyClass,
                     javaName,
                     Arity.createArity(arity),
                     Visibility.PUBLIC,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         }
     }
 
     public static String encodeScope(StaticScope scope) {
         StringBuilder namesBuilder = new StringBuilder(scope.getType().name());
 
         namesBuilder.append(',');
 
         boolean first = true;
         for (String name : scope.getVariables()) {
             if (!first) namesBuilder.append(';');
             first = false;
             namesBuilder.append(name);
         }
         namesBuilder
                 .append(',')
                 .append(scope.getRequiredArgs())
                 .append(',')
                 .append(scope.getOptionalArgs())
                 .append(',')
                 .append(scope.getRestArg());
 
         return namesBuilder.toString();
     }
 
     @Deprecated
     public static StaticScope decodeRootScope(ThreadContext context, String scopeString) {
         return decodeScope(context, null, scopeString);
     }
 
     @Deprecated
     public static StaticScope decodeLocalScope(ThreadContext context, String scopeString) {
         return decodeScope(context, context.getCurrentStaticScope(), scopeString);
     }
 
     @Deprecated
     public static StaticScope decodeLocalScope(ThreadContext context, StaticScope parent, String scopeString) {
         return decodeScope(context, parent, scopeString);
     }
 
     @Deprecated
     public static StaticScope decodeBlockScope(ThreadContext context, String scopeString) {
         return decodeScope(context, context.getCurrentStaticScope(), scopeString);
     }
 
     public static StaticScope decodeScope(ThreadContext context, StaticScope parent, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         StaticScope scope = null;
         switch (StaticScope.Type.valueOf(decodedScope[0][0])) {
             case BLOCK:
                 scope = context.runtime.getStaticScopeFactory().newBlockScope(parent, decodedScope[1]);
                 break;
             case EVAL:
                 scope = context.runtime.getStaticScopeFactory().newEvalScope(parent, decodedScope[1]);
                 break;
             case LOCAL:
                 scope = context.runtime.getStaticScopeFactory().newLocalScope(parent, decodedScope[1]);
                 break;
         }
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     private static String[][] decodeScopeDescriptor(String scopeString) {
         String[] scopeElements = scopeString.split(",");
         String[] scopeNames = scopeElements[1].length() == 0 ? new String[0] : getScopeNames(scopeElements[1]);
         return new String[][] {scopeElements, scopeNames};
     }
 
     private static void setAritiesFromDecodedScope(StaticScope scope, String[] scopeElements) {
         scope.setArities(Integer.parseInt(scopeElements[2]), Integer.parseInt(scopeElements[3]), Integer.parseInt(scopeElements[4]));
     }
 
     public static StaticScope decodeScopeAndDetermineModule(ThreadContext context, StaticScope parent, String scopeString) {
         StaticScope scope = decodeScope(context, parent, scopeString);
         scope.determineModule();
 
         return scope;
     }
 
     private static void performNormalMethodChecks(RubyModule containingClass, Ruby runtime, String name) throws RaiseException {
 
         if (containingClass == runtime.getDummy()) {
             throw runtime.newTypeError("no class/module to add method");
         }
 
         if (containingClass == runtime.getObject() && name.equals("initialize")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining Object#initialize may cause infinite loop");
         }
 
         if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining `" + name + "' may cause serious problem");
         }
     }
 
     private static RubyClass performSingletonMethodChecks(Ruby runtime, IRubyObject receiver, String name) throws RaiseException {
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("can't define singleton method \"" + name + "\" for " + receiver.getMetaClass().getBaseName());
         }
 
         if (receiver.isFrozen()) {
             throw runtime.newFrozenError("object");
         }
         
         RubyClass rubyClass = receiver.getSingletonClass();
         
         return rubyClass;
     }
     
     public static IRubyObject arrayEntryOrNil(RubyArray array, int index) {
         if (index < array.getLength()) {
             return array.eltInternal(index);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilZero(RubyArray array) {
         if (0 < array.getLength()) {
             return array.eltInternal(0);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilOne(RubyArray array) {
         if (1 < array.getLength()) {
             return array.eltInternal(1);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilTwo(RubyArray array) {
         if (2 < array.getLength()) {
             return array.eltInternal(2);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNil(RubyArray array, int pre, int post, int index) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + index);
         } else if (pre + index < array.getLength()) {
             return array.eltInternal(pre + index);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilZero(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 0);
         } else if (pre + 0 < array.getLength()) {
             return array.eltInternal(pre + 0);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilOne(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 1);
         } else if (pre + 1 < array.getLength()) {
             return array.eltInternal(pre + 1);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilTwo(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 2);
         } else if (pre + 2 < array.getLength()) {
             return array.eltInternal(pre + 2);
         } else {
             return array.getRuntime().getNil();
         }
     }
     
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index) {
         if (index < array.getLength()) {
             return createSubarray(array, index);
         } else {
             return RubyArray.newEmptyArray(runtime);
         }
     }
 
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index, int post) {
         if (index + post < array.getLength()) {
             return createSubarray(array, index, post);
         } else {
             return RubyArray.newEmptyArray(runtime);
         }
     }
     
     public static RubyModule checkIsModule(IRubyObject maybeModule) {
         if (maybeModule instanceof RubyModule) return (RubyModule)maybeModule;
         
         throw maybeModule.getRuntime().newTypeError(maybeModule + " is not a class/module");
     }
     
     public static IRubyObject getGlobalVariable(Ruby runtime, String name) {
         return runtime.getGlobalVariables().get(name);
     }
     
     public static IRubyObject setGlobalVariable(IRubyObject value, Ruby runtime, String name) {
         return runtime.getGlobalVariables().set(name, value);
     }
 
     public static IRubyObject getInstanceVariable(IRubyObject self, Ruby runtime, String internedName) {
         IRubyObject result = self.getInstanceVariables().getInstanceVariable(internedName);
         if (result != null) return result;
         if (runtime.isVerbose()) warnAboutUninitializedIvar(runtime, internedName);
         return runtime.getNil();
     }
 
     private static void warnAboutUninitializedIvar(Ruby runtime, String internedName) {
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + internedName + " not initialized");
     }
 
     public static IRubyObject setInstanceVariable(IRubyObject value, IRubyObject self, String name) {
         return self.getInstanceVariables().setInstanceVariable(name, value);
     }
 
     public static RubyProc newLiteralLambda(ThreadContext context, Block block, IRubyObject self) {
         return RubyProc.newProc(context.runtime, block, Block.Type.LAMBDA);
     }
 
     public static void fillNil(IRubyObject[]arr, int from, int to, Ruby runtime) {
         IRubyObject nils[] = runtime.getNilPrefilledArray();
         int i;
 
         for (i = from; i + Ruby.NIL_PREFILLED_ARRAY_SIZE < to; i += Ruby.NIL_PREFILLED_ARRAY_SIZE) {
             System.arraycopy(nils, 0, arr, i, Ruby.NIL_PREFILLED_ARRAY_SIZE);
         }
         System.arraycopy(nils, 0, arr, i, to - i);
     }
 
     public static void fillNil(IRubyObject[]arr, Ruby runtime) {
         fillNil(arr, 0, arr.length, runtime);
     }
 
     public static boolean isFastSwitchableString(IRubyObject str) {
         return str instanceof RubyString;
     }
 
     public static boolean isFastSwitchableSingleCharString(IRubyObject str) {
         return str instanceof RubyString && ((RubyString)str).getByteList().length() == 1;
     }
 
     public static int getFastSwitchString(IRubyObject str) {
         ByteList byteList = ((RubyString)str).getByteList();
         return byteList.hashCode();
     }
 
     public static int getFastSwitchSingleCharString(IRubyObject str) {
         ByteList byteList = ((RubyString)str).getByteList();
         return byteList.get(0);
     }
 
     public static boolean isFastSwitchableSymbol(IRubyObject sym) {
         return sym instanceof RubySymbol;
     }
 
     public static boolean isFastSwitchableSingleCharSymbol(IRubyObject sym) {
         return sym instanceof RubySymbol && ((RubySymbol)sym).asJavaString().length() == 1;
     }
 
     public static int getFastSwitchSymbol(IRubyObject sym) {
         String str = ((RubySymbol)sym).asJavaString();
         return str.hashCode();
     }
 
     public static int getFastSwitchSingleCharSymbol(IRubyObject sym) {
         String str = ((RubySymbol)sym).asJavaString();
         return (int)str.charAt(0);
     }
 
     public static Block getBlock(ThreadContext context, IRubyObject self, Node node) {
         IterNode iter = (IterNode)node;
         iter.getScope().determineModule();
 
         // Create block for this iter node
         // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
         if (iter.getBlockBody() instanceof InterpretedBlock) {
             return InterpretedBlock.newInterpretedClosure(context, iter.getBlockBody(), self);
         } else {
             return Interpreted19Block.newInterpretedClosure(context, iter.getBlockBody(), self);
         }
     }
 
     public static Block getBlock(Ruby runtime, ThreadContext context, IRubyObject self, Node node, Block aBlock) {
         return RuntimeHelpers.getBlockFromBlockPassBody(runtime, node.interpret(runtime, context, self, aBlock), aBlock);
     }
 
     /**
      * Equivalent to rb_equal in MRI
      *
      * @param context
      * @param a
      * @param b
      * @return
      */
     public static RubyBoolean rbEqual(ThreadContext context, IRubyObject a, IRubyObject b) {
         Ruby runtime = context.runtime;
         if (a == b) return runtime.getTrue();
         IRubyObject res = invokedynamic(context, a, OP_EQUAL, b);
         return runtime.newBoolean(res.isTrue());
     }
 
     /**
      * Equivalent to rb_eql in MRI
      *
      * @param context
      * @param a
      * @param b
      * @return
      */
     public static RubyBoolean rbEql(ThreadContext context, IRubyObject a, IRubyObject b) {
         Ruby runtime = context.runtime;
         if (a == b) return runtime.getTrue();
         IRubyObject res = invokedynamic(context, a, EQL, b);
         return runtime.newBoolean(res.isTrue());
     }
 
     public static void traceLine(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.runtime.callEventHooks(context, RubyEvent.LINE, context.getFile(), context.getLine(), name, type);
     }
 
     public static void traceClass(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.runtime.callEventHooks(context, RubyEvent.CLASS, context.getFile(), context.getLine(), name, type);
     }
 
     public static void traceEnd(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.runtime.callEventHooks(context, RubyEvent.END, context.getFile(), context.getLine(), name, type);
     }
 
     /**
      * Some of this code looks scary.  All names for an alias or undef is a
      * fitem in 1.8/1.9 grammars.  This means it is guaranteed to be either
      * a LiteralNode of a DSymbolNode.  Nothing else is possible.  Also
      * Interpreting a DSymbolNode will always yield a RubySymbol.
      */
     public static String interpretAliasUndefName(Node nameNode, Ruby runtime,
             ThreadContext context, IRubyObject self, Block aBlock) {
         String name;
 
         if (nameNode instanceof LiteralNode) {
             name = ((LiteralNode) nameNode).getName();
         } else {
             assert nameNode instanceof DSymbolNode: "Alias or Undef not literal or dsym";
             name = ((RubySymbol) nameNode.interpret(runtime, context, self, aBlock)).asJavaString();
         }
 
         return name;
     }
 
     /**
      * Used by the compiler to simplify arg checking in variable-arity paths
      *
      * @param context thread context
      * @param args arguments array
      * @param min minimum required
      * @param max maximum allowed
      */
     public static void checkArgumentCount(ThreadContext context, IRubyObject[] args, int min, int max) {
         checkArgumentCount(context, args.length, min, max);
     }
 
     /**
      * Used by the compiler to simplify arg checking in variable-arity paths
      *
      * @param context thread context
      * @param args arguments array
      * @param req required number
      */
     public static void checkArgumentCount(ThreadContext context, IRubyObject[] args, int req) {
         checkArgumentCount(context, args.length, req, req);
     }
     
     public static void checkArgumentCount(ThreadContext context, int length, int min, int max) {
         int expected = 0;
         if (length < min) {
             expected = min;
         } else if (max > -1 && length > max) {
             expected = max;
         } else {
             return;
         }
         throw context.runtime.newArgumentError(length, expected);
     }
 
     public static boolean isModuleAndHasConstant(IRubyObject left, String name) {
         return left instanceof RubyModule && ((RubyModule) left).getConstantFromNoConstMissing(name, false) != null;
     }
 
     public static RubyString getDefinedConstantOrBoundMethod(IRubyObject left, String name) {
         if (isModuleAndHasConstant(left, name)) return left.getRuntime().getDefinedMessage(DefinedMessage.CONSTANT);
         if (left.getMetaClass().isMethodBound(name, true)) left.getRuntime().getDefinedMessage(DefinedMessage.METHOD);
         return null;
     }
 
     public static RubyModule getSuperClassForDefined(Ruby runtime, RubyModule klazz) {
         RubyModule superklazz = klazz.getSuperClass();
 
         if (superklazz == null && klazz.isModule()) superklazz = runtime.getObject();
 
         return superklazz;
     }
 
     public static boolean isGenerationEqual(IRubyObject object, int generation) {
         RubyClass metaClass;
         if (object instanceof RubyBasicObject) {
             metaClass = ((RubyBasicObject)object).getMetaClass();
         } else {
             metaClass = object.getMetaClass();
         }
         return metaClass.getGeneration() == generation;
     }
 
     public static String[] getScopeNames(String scopeNames) {
         StringTokenizer toker = new StringTokenizer(scopeNames, ";");
         ArrayList list = new ArrayList(10);
         while (toker.hasMoreTokens()) {
             list.add(toker.nextToken().intern());
         }
         return (String[])list.toArray(new String[list.size()]);
     }
 
     public static IRubyObject[] arraySlice1N(IRubyObject arrayish) {
         arrayish = aryToAry(arrayish);
         RubyArray arrayish2 = ensureMultipleAssignableRubyArray(arrayish, arrayish.getRuntime(), true);
         return new IRubyObject[] {arrayEntryOrNilZero(arrayish2), subarrayOrEmpty(arrayish2, arrayish2.getRuntime(), 1)};
     }
 
     public static IRubyObject arraySlice1(IRubyObject arrayish) {
         arrayish = aryToAry(arrayish);
         RubyArray arrayish2 = ensureMultipleAssignableRubyArray(arrayish, arrayish.getRuntime(), true);
         return arrayEntryOrNilZero(arrayish2);
     }
 
     public static RubyClass metaclass(IRubyObject object) {
         return object instanceof RubyBasicObject ?
             ((RubyBasicObject)object).getMetaClass() :
             object.getMetaClass();
     }
 
     public static String rawBytesToString(byte[] bytes) {
         // stuff bytes into chars
         char[] chars = new char[bytes.length];
         for (int i = 0; i < bytes.length; i++) chars[i] = (char)bytes[i];
         return new String(chars);
     }
 
     public static byte[] stringToRawBytes(String string) {
         char[] chars = string.toCharArray();
         byte[] bytes = new byte[chars.length];
         for (int i = 0; i < chars.length; i++) bytes[i] = (byte)chars[i];
         return bytes;
     }
 
     public static String encodeCaptureOffsets(int[] scopeOffsets) {
         char[] encoded = new char[scopeOffsets.length * 2];
         for (int i = 0; i < scopeOffsets.length; i++) {
             int offDepth = scopeOffsets[i];
             char off = (char)(offDepth & 0xFFFF);
             char depth = (char)(offDepth >> 16);
             encoded[2 * i] = off;
             encoded[2 * i + 1] = depth;
         }
         return new String(encoded);
     }
 
     public static int[] decodeCaptureOffsets(String encoded) {
         char[] chars = encoded.toCharArray();
         int[] scopeOffsets = new int[chars.length / 2];
         for (int i = 0; i < scopeOffsets.length; i++) {
             char off = chars[2 * i];
             char depth = chars[2 * i + 1];
             scopeOffsets[i] = (((int)depth) << 16) | (int)off;
         }
         return scopeOffsets;
     }
 
     public static IRubyObject match2AndUpdateScope(IRubyObject receiver, ThreadContext context, IRubyObject value, String scopeOffsets) {
         DynamicScope scope = context.getCurrentScope();
         IRubyObject match = ((RubyRegexp)receiver).op_match(context, value);
         updateScopeWithCaptures(context, scope, decodeCaptureOffsets(scopeOffsets), match);
         return match;
     }
 
     public static IRubyObject match2AndUpdateScope19(IRubyObject receiver, ThreadContext context, IRubyObject value, String scopeOffsets) {
         DynamicScope scope = context.getCurrentScope();
         IRubyObject match = ((RubyRegexp)receiver).op_match19(context, value);
         updateScopeWithCaptures(context, scope, decodeCaptureOffsets(scopeOffsets), match);
         return match;
     }
 
     public static void updateScopeWithCaptures(ThreadContext context, DynamicScope scope, int[] scopeOffsets, IRubyObject result) {
         Ruby runtime = context.runtime;
         if (result.isNil()) { // match2 directly calls match so we know we can count on result
             IRubyObject nil = runtime.getNil();
 
             for (int i = 0; i < scopeOffsets.length; i++) {
                 scope.setValue(nil, scopeOffsets[i], 0);
             }
         } else {
             RubyMatchData matchData = (RubyMatchData)scope.getBackRef(runtime);
             // FIXME: Mass assignment is possible since we know they are all locals in the same
             //   scope that are also contiguous
             IRubyObject[] namedValues = matchData.getNamedBackrefValues(runtime);
 
             for (int i = 0; i < scopeOffsets.length; i++) {
                 scope.setValue(namedValues[i], scopeOffsets[i] & 0xffff, scopeOffsets[i] >> 16);
             }
         }
     }
 
     public static RubyArray argsPush(RubyArray first, IRubyObject second) {
         return ((RubyArray)first.dup()).append(second);
     }
 
     public static RubyArray argsCat(IRubyObject first, IRubyObject second) {
         Ruby runtime = first.getRuntime();
         IRubyObject secondArgs;
         if (runtime.is1_9()) {
             secondArgs = RuntimeHelpers.splatValue19(second);
         } else {
             secondArgs = RuntimeHelpers.splatValue(second);
         }
 
         return ((RubyArray)RuntimeHelpers.ensureRubyArray(runtime, first).dup()).concat(secondArgs);
     }
 
     public static String encodeParameterList(ArgsNode argsNode) {
         StringBuilder builder = new StringBuilder();
         
         boolean added = false;
         if (argsNode.getPre() != null) {
             for (Node preNode : argsNode.getPre().childNodes()) {
                 if (added) builder.append(';');
                 added = true;
                 if (preNode instanceof MultipleAsgn19Node) {
                     builder.append("nil");
                 } else {
                     builder.append("q").append(((ArgumentNode)preNode).getName());
                 }
             }
         }
 
         if (argsNode.getOptArgs() != null) {
             for (Node optNode : argsNode.getOptArgs().childNodes()) {
                 if (added) builder.append(';');
                 added = true;
                 builder.append("o");
                 if (optNode instanceof OptArgNode) {
                     builder.append(((OptArgNode)optNode).getName());
                 } else if (optNode instanceof LocalAsgnNode) {
                     builder.append(((LocalAsgnNode)optNode).getName());
                 } else if (optNode instanceof DAsgnNode) {
                     builder.append(((DAsgnNode)optNode).getName());
                 }
             }
