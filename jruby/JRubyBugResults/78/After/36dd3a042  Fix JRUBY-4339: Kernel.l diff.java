diff --git a/spec/tags/1.8/ruby/core/kernel/load_tags.txt b/spec/tags/1.8/ruby/core/kernel/load_tags.txt
deleted file mode 100644
index 034de6547f..0000000000
--- a/spec/tags/1.8/ruby/core/kernel/load_tags.txt
+++ /dev/null
@@ -1,2 +0,0 @@
-fails(JRUBY-4543):Kernel#load sets the enclosing scope to an anonymous module if passed true for 'wrap'
-fails(JRUBY-4543):Kernel.load sets the enclosing scope to an anonymous module if passed true for 'wrap'
diff --git a/spec/tags/1.9/ruby/core/kernel/load_tags.txt b/spec/tags/1.9/ruby/core/kernel/load_tags.txt
deleted file mode 100644
index 197e3eb1ca..0000000000
--- a/spec/tags/1.9/ruby/core/kernel/load_tags.txt
+++ /dev/null
@@ -1,2 +0,0 @@
-fails:Kernel.load sets the enclosing scope to an anonymous module if passed true for 'wrap'
-fails:Kernel#load sets the enclosing scope to an anonymous module if passed true for 'wrap'
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index a76b0b54e7..64c284e9d1 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,3527 +1,3536 @@
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
 
 import jnr.constants.Constant;
 import jnr.constants.ConstantSet;
 import jnr.constants.platform.Errno;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.nio.channels.ClosedChannelException;
 import java.util.EnumSet;
 import java.util.concurrent.atomic.AtomicLong;
 import org.jruby.RubyInstanceConfig.CompileMode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.executable.RuntimeCache;
 import org.jruby.runtime.opto.ObjectIdentityInvalidator;
 import org.jruby.runtime.opto.Invalidator;
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
 import org.jruby.runtime.opto.OptoFactory;
 import org.jruby.threading.DaemonThreadFactory;
 import org.jruby.util.io.SelectorPool;
 import org.objectweb.asm.Opcodes;
 
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
         this.beanManager.register(new org.jruby.management.Runtime(this));
 
         this.runtimeCache = new RuntimeCache();
         runtimeCache.initMethodCache(ClassIndex.MAX_CLASSES * MethodIndex.MAX_METHODS);
         
         constantInvalidator = OptoFactory.newConstantInvalidator();
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
+        return runScript(script, false);
+    }
+    
+    public IRubyObject runScript(Script script, boolean wrap) {
         ThreadContext context = getCurrentContext();
         
         try {
-            return script.load(context, getTopSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
+            return script.load(context, getTopSelf(), wrap);
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
         
         booting = false;
 
         // initialize builtin libraries
         initBuiltins();
         
         if(config.isProfiling()) {
             getLoadService().require("jruby/profiler/shutdown_hook");
         }
 
         // Require in all libraries specified on command line
         for (String scriptName : config.requiredLibraries()) {
             loadService.require(scriptName);
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
 
         encodingService = new EncodingService(this);
 
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
         systemStackError = defineClassIfAllowed("SystemStackError", standardError);
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
 
             mathDomainError = defineClassUnder("DomainError", argumentError, argumentError.getAllocator(), mathModule);
             recursiveKey = newSymbol("__recursive_key__");
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
                 e.printStackTrace();
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
         addLazyBuiltin("java.rb", "java", "org.jruby.javasupport.Java");
         addLazyBuiltin("jruby.rb", "jruby", "org.jruby.libraries.JRubyLibrary");
         addLazyBuiltin("jruby/ext.rb", "jruby/ext", "org.jruby.ext.jruby.JRubyExtLibrary");
         addLazyBuiltin("jruby/util.rb", "jruby/util", "org.jruby.ext.jruby.JRubyUtilLibrary");
         addLazyBuiltin("jruby/core_ext.rb", "jruby/core_ext", "org.jruby.ext.jruby.JRubyCoreExtLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.ext.jruby.JRubyTypeLibrary");
         addLazyBuiltin("jruby/synchronized.rb", "jruby/synchronized", "org.jruby.ext.jruby.JRubySynchronizedLibrary");
         addLazyBuiltin("iconv.jar", "iconv", "org.jruby.libraries.IConvLibrary");
         addLazyBuiltin("nkf.jar", "nkf", "org.jruby.libraries.NKFLibrary");
         addLazyBuiltin("stringio.jar", "stringio", "org.jruby.libraries.StringIOLibrary");
         addLazyBuiltin("strscan.jar", "strscan", "org.jruby.libraries.StringScannerLibrary");
         addLazyBuiltin("zlib.jar", "zlib", "org.jruby.libraries.ZlibLibrary");
         addLazyBuiltin("enumerator.jar", "enumerator", "org.jruby.libraries.EnumeratorLibrary");
         addLazyBuiltin("readline.jar", "readline", "org.jruby.ext.ReadlineService");
         addLazyBuiltin("thread.jar", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("thread.rb", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("digest.jar", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest.rb", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest/md5.jar", "digest/md5", "org.jruby.libraries.MD5");
         addLazyBuiltin("digest/rmd160.jar", "digest/rmd160", "org.jruby.libraries.RMD160");
         addLazyBuiltin("digest/sha1.jar", "digest/sha1", "org.jruby.libraries.SHA1");
         addLazyBuiltin("digest/sha2.jar", "digest/sha2", "org.jruby.libraries.SHA2");
         addLazyBuiltin("bigdecimal.jar", "bigdecimal", "org.jruby.libraries.BigDecimalLibrary");
         addLazyBuiltin("io/wait.jar", "io/wait", "org.jruby.libraries.IOWaitLibrary");
         addLazyBuiltin("etc.jar", "etc", "org.jruby.libraries.EtcLibrary");
         addLazyBuiltin("weakref.rb", "weakref", "org.jruby.ext.WeakRefLibrary");
         addLazyBuiltin("delegate_internal.jar", "delegate_internal", "org.jruby.ext.DelegateLibrary");
         addLazyBuiltin("timeout.rb", "timeout", "org.jruby.ext.Timeout");
         addLazyBuiltin("socket.jar", "socket", "org.jruby.ext.socket.SocketLibrary");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.libraries.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.libraries.JRubySerializationLibrary");
         addLazyBuiltin("ffi-internal.jar", "ffi-internal", "org.jruby.ext.ffi.FFIService");
         addLazyBuiltin("tempfile.rb", "tempfile", "org.jruby.libraries.TempfileLibrary");
         addLazyBuiltin("fcntl.rb", "fcntl", "org.jruby.libraries.FcntlLibrary");
         addLazyBuiltin("rubinius.jar", "rubinius", "org.jruby.ext.rubinius.RubiniusLibrary");
         addLazyBuiltin("yecht.jar", "yecht", "YechtService");
 
         if (is1_9()) {
             addLazyBuiltin("mathn/complex.jar", "mathn/complex", "org.jruby.ext.mathn.Complex");
             addLazyBuiltin("mathn/rational.jar", "mathn/rational", "org.jruby.ext.mathn.Rational");
             addLazyBuiltin("fiber.rb", "fiber", "org.jruby.libraries.FiberExtLibrary");
             addLazyBuiltin("psych.jar", "psych", "org.jruby.ext.psych.PsychLibrary");
         }
 
         if(RubyInstanceConfig.NATIVE_NET_PROTOCOL) {
             addLazyBuiltin("net/protocol.rb", "net/protocol", "org.jruby.libraries.NetProtocolBufferedIOLibrary");
         }
         
         if (is1_9()) {
             LoadService.reflectedLoad(this, "fiber", "org.jruby.libraries.FiberLibrary", getJRubyClassLoader(), false);
         }
         
         addBuiltinIfAllowed("openssl.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/openssl/stub");
             }
         });
 
         addBuiltinIfAllowed("win32ole.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/win32ole/stub");
             }
         });
         
         String[] builtins = {"jsignal_internal", "generator_internal"};
         for (String library : builtins) {
             addBuiltinIfAllowed(library + ".rb", new BuiltinScript(library));
         }
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
 
         if(is1_9()) {
             // see ruby.c's ruby_init_gems function
             loadFile("builtin/prelude.rb", getJRubyClassLoader().getResourceAsStream("builtin/prelude.rb"), false);
             if (!config.isDisableGems()) {
                 // NOTE: This has been disabled because gem_prelude is terribly broken.
                 //       We just require 'rubygems' in gem_prelude, at least for now.
                 //defineModule("Gem"); // dummy Gem module for prelude
                 loadFile("builtin/gem_prelude.rb", getJRubyClassLoader().getResourceAsStream("builtin/gem_prelude.rb"), false);
             }
         }
 
         getLoadService().require("enumerator");
     }
 
     private void addLazyBuiltin(String name, String shortName, String className) {
         addBuiltinIfAllowed(name, new LateLoadingLibrary(shortName, className, getClassLoader()));
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
 
     public RubyClass getYielder() {
         return yielderClass;
     }
     void setYielder(RubyClass yielderClass) {
         this.yielderClass = yielderClass;
     }
 
     public RubyClass getString() {
         return stringClass;
     }
     void setString(RubyClass stringClass) {
         this.stringClass = stringClass;
     }
 
     public RubyClass getEncoding() {
         return encodingClass;
     }
     void setEncoding(RubyClass encodingClass) {
         this.encodingClass = encodingClass;
     }
 
     public RubyClass getConverter() {
         return converterClass;
     }
     void setConverter(RubyClass converterClass) {
         this.converterClass = converterClass;
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
 
     public IRubyObject[] getSingleNilArray() {
         return singleNilArray;
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
     
     public RubyClass getRandomClass() {
         return randomClass;
     }
     void setRandomClass(RubyClass randomClass) {
         this.randomClass = randomClass;
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
     
     public RubyHash getENV() {
         return envObject;
     }
     
     public void setENV(RubyHash env) {
         envObject = env;
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
 
     public RubyClass getStopIteration() {
         return stopIteration;
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
 
     public RubyClass getMathDomainError() {
         return mathDomainError;
     }
 
     public RubyClass getEncodingError() {
         return encodingError;
     }
 
     public RubyClass getEncodingCompatibilityError() {
         return encodingCompatibilityError;
     }
 
     public RubyClass getConverterNotFoundError() {
         return converterNotFoundError;
     }
 
     public RubyClass getFiberError() {
         return fiberError;
     }
 
     public RubyClass getUndefinedConversionError() {
         return undefinedConversionError;
     }
 
     public RubyClass getInvalidByteSequenceError() {
         return invalidByteSequenceError;
     }
 
     private RubyRandom.RandomType defaultRand;
     public RubyRandom.RandomType getDefaultRand() {
         return defaultRand;
     }
     
     public void setDefaultRand(RubyRandom.RandomType defaultRand) {
         this.defaultRand = defaultRand;
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
         return verboseValue;
     }
 
     public boolean isVerbose() {
         return verbose;
     }
 
     public boolean warningsEnabled() {
         return warningsEnabled;
     }
 
     /** Setter for property isVerbose.
      * @param verbose New value of property isVerbose.
      */
     public void setVerbose(IRubyObject verbose) {
         this.verbose = verbose.isTrue();
         this.verboseValue = verbose;
         warningsEnabled = !verbose.isNil();
     }
 
     /** Getter for property isDebug.
      * @return Value of property isDebug.
      */
     public IRubyObject getDebug() {
         return debug ? trueObject : falseObject;
     }
 
     public boolean isDebug() {
         return debug;
     }
 
     /** Setter for property isDebug.
      * @param debug New value of property isDebug.
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
 
     public Node parseFileFromMain(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addLoadParse();
         return parser.parse(file, in, scope, new ParserConfiguration(this,
                 0, false, false, true, true, config));
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
                 lineNumber, false, false, false, false, config));
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
 
-            runInterpreter(context, parseFile(in, scriptName, null), self);
+            Node node = parseFile(in, scriptName, null);
+            if (wrap) {
+                // toss an anonymous module into the search path
+                ((RootNode)node).getStaticScope().setModule(RubyModule.newModule(this));
+            }
+            runInterpreter(context, node, self);
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
-                runScript(script);
+                runScript(script, wrap);
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
 
         getSelectorPool().cleanup();
 
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
         return RubyFixnum.newFixnum(this, value.intValue());
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
         return newErrnoException(getErrno().fastGetClass("EAGAIN"), message);
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
         if (ioe instanceof ClosedChannelException) {
             throw newIOError("closed stream");
         }
 
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
         return newTypeError(receivedObject, expectedType.getName());
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyModule expectedType) {
         return newTypeError(receivedObject, expectedType.getName());
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, String expectedType) {
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
     public RaiseException newRaiseException(RubyClass exceptionClass, String message) {
         return new RaiseException(this, exceptionClass, message, true);
     }
 
     /**
      * Generate one of the ERRNO exceptions. This differs from the normal logic
      * by avoiding the generation of a backtrace. Many ERRNO values are expected,
      * such as EAGAIN, and JRuby pays a very high cost to generate backtraces that
      * are never used. The flags -Xerrno.backtrace=true or the property
      * jruby.errno.backtrace=true forces all errno exceptions to generate a backtrace.
      * 
      * @param exceptionClass
      * @param message
      * @return
      */
     private RaiseException newErrnoException(RubyClass exceptionClass, String message) {
         if (RubyInstanceConfig.ERRNO_BACKTRACE) {
             return new RaiseException(this, exceptionClass, message, true);
         } else {
             return new RaiseException(this, exceptionClass, ERRNO_BACKTRACE_MESSAGE, RubyArray.newEmptyArray(this), true);
         }
     }
 
     // Equivalent of Data_Wrap_Struct
     public RubyObject.Data newData(RubyClass objectClass, Object sval) {
         return new RubyObject.Data(this, objectClass, sval);
     }
 
     public RubySymbol.SymbolTable getSymbolTable() {
         return symbolTable;
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
diff --git a/src/org/jruby/ast/executable/AbstractScript.java b/src/org/jruby/ast/executable/AbstractScript.java
index 647248a240..bf5703220f 100644
--- a/src/org/jruby/ast/executable/AbstractScript.java
+++ b/src/org/jruby/ast/executable/AbstractScript.java
@@ -1,378 +1,383 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.ast.executable;
 
 import java.math.BigInteger;
 import org.jcodings.Encoding;
 import org.jcodings.EncodingDB;
 import org.jruby.Ruby;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 /**
  *
  * @author headius
  */
 public abstract class AbstractScript implements Script {
     public AbstractScript() {
     }
 
     public IRubyObject __file__(ThreadContext context, IRubyObject self, Block block) {
         return __file__(context, self, IRubyObject.NULL_ARRAY, block);
     }
     
     public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg, Block block) {
         return __file__(context, self, new IRubyObject[] {arg}, block);
     }
     
     public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2, Block block) {
         return __file__(context, self, new IRubyObject[] {arg1, arg2}, block);
     }
     
     public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
         return __file__(context, self, new IRubyObject[] {arg1, arg2, arg3}, block);
     }
     
+    @Deprecated
     public IRubyObject load(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
+        return load(context, self, false);
+    }
+    
+    public IRubyObject load(ThreadContext context, IRubyObject self, boolean wrap) {
         return null;
     }
     
     public IRubyObject run(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         return __file__(context, self, args, block);
     }
 
     public RuntimeCache runtimeCache;
 
     public static final int NUMBERED_SCOPE_COUNT = 10;
 
     public final StaticScope getScope(ThreadContext context, String varNamesDescriptor, int i) {return runtimeCache.getScope(context, varNamesDescriptor, i);}
     public final StaticScope getScope0(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 0);}
     public final StaticScope getScope1(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 1);}
     public final StaticScope getScope2(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 2);}
     public final StaticScope getScope3(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 3);}
     public final StaticScope getScope4(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 4);}
     public final StaticScope getScope5(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 5);}
     public final StaticScope getScope6(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 6);}
     public final StaticScope getScope7(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 7);}
     public final StaticScope getScope8(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 8);}
     public final StaticScope getScope9(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 9);}
 
     public static final int NUMBERED_CALLSITE_COUNT = 10;
 
     public final CallSite getCallSite(int i) {return runtimeCache.callSites[i];}
     public final CallSite getCallSite0() {return runtimeCache.callSites[0];}
     public final CallSite getCallSite1() {return runtimeCache.callSites[1];}
     public final CallSite getCallSite2() {return runtimeCache.callSites[2];}
     public final CallSite getCallSite3() {return runtimeCache.callSites[3];}
     public final CallSite getCallSite4() {return runtimeCache.callSites[4];}
     public final CallSite getCallSite5() {return runtimeCache.callSites[5];}
     public final CallSite getCallSite6() {return runtimeCache.callSites[6];}
     public final CallSite getCallSite7() {return runtimeCache.callSites[7];}
     public final CallSite getCallSite8() {return runtimeCache.callSites[8];}
     public final CallSite getCallSite9() {return runtimeCache.callSites[9];}
 
     public static final int NUMBERED_BLOCKBODY_COUNT = 10;
 
     public final BlockBody getBlockBody(ThreadContext context, int i, String descriptor) {return runtimeCache.getBlockBody(this, context, i, descriptor);}
     public final BlockBody getBlockBody0(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 0, descriptor);}
     public final BlockBody getBlockBody1(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 1, descriptor);}
     public final BlockBody getBlockBody2(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 2, descriptor);}
     public final BlockBody getBlockBody3(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 3, descriptor);}
     public final BlockBody getBlockBody4(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 4, descriptor);}
     public final BlockBody getBlockBody5(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 5, descriptor);}
     public final BlockBody getBlockBody6(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 6, descriptor);}
     public final BlockBody getBlockBody7(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 7, descriptor);}
     public final BlockBody getBlockBody8(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 8, descriptor);}
     public final BlockBody getBlockBody9(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 9, descriptor);}
 
     public final BlockBody getBlockBody19(ThreadContext context, int i, String descriptor) {return runtimeCache.getBlockBody19(this, context, i, descriptor);}
     public final BlockBody getBlockBody190(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 0, descriptor);}
     public final BlockBody getBlockBody191(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 1, descriptor);}
     public final BlockBody getBlockBody192(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 2, descriptor);}
     public final BlockBody getBlockBody193(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 3, descriptor);}
     public final BlockBody getBlockBody194(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 4, descriptor);}
     public final BlockBody getBlockBody195(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 5, descriptor);}
     public final BlockBody getBlockBody196(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 6, descriptor);}
     public final BlockBody getBlockBody197(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 7, descriptor);}
     public final BlockBody getBlockBody198(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 8, descriptor);}
     public final BlockBody getBlockBody199(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 9, descriptor);}
 
     public static final int NUMBERED_BLOCKCALLBACK_COUNT = 10;
 
     public final CompiledBlockCallback getBlockCallback(int i, String method) {return runtimeCache.getBlockCallback(this, i, method);}
     public final CompiledBlockCallback getBlockCallback0(String method) {return runtimeCache.getBlockCallback(this, 0, method);}
     public final CompiledBlockCallback getBlockCallback1(String method) {return runtimeCache.getBlockCallback(this, 1, method);}
     public final CompiledBlockCallback getBlockCallback2(String method) {return runtimeCache.getBlockCallback(this, 2, method);}
     public final CompiledBlockCallback getBlockCallback3(String method) {return runtimeCache.getBlockCallback(this, 3, method);}
     public final CompiledBlockCallback getBlockCallback4(String method) {return runtimeCache.getBlockCallback(this, 4, method);}
     public final CompiledBlockCallback getBlockCallback5(String method) {return runtimeCache.getBlockCallback(this, 5, method);}
     public final CompiledBlockCallback getBlockCallback6(String method) {return runtimeCache.getBlockCallback(this, 6, method);}
     public final CompiledBlockCallback getBlockCallback7(String method) {return runtimeCache.getBlockCallback(this, 7, method);}
     public final CompiledBlockCallback getBlockCallback8(String method) {return runtimeCache.getBlockCallback(this, 8, method);}
     public final CompiledBlockCallback getBlockCallback9(String method) {return runtimeCache.getBlockCallback(this, 9, method);}
 
     public static final int NUMBERED_SYMBOL_COUNT = 10;
 
     public final RubySymbol getSymbol(ThreadContext context, int i, String name) {return runtimeCache.getSymbol(context, i, name);}
     public final RubySymbol getSymbol0(ThreadContext context, String name) {return runtimeCache.getSymbol(context, 0, name);}
     public final RubySymbol getSymbol1(ThreadContext context, String name) {return runtimeCache.getSymbol(context, 1, name);}
     public final RubySymbol getSymbol2(ThreadContext context, String name) {return runtimeCache.getSymbol(context, 2, name);}
     public final RubySymbol getSymbol3(ThreadContext context, String name) {return runtimeCache.getSymbol(context, 3, name);}
     public final RubySymbol getSymbol4(ThreadContext context, String name) {return runtimeCache.getSymbol(context, 4, name);}
     public final RubySymbol getSymbol5(ThreadContext context, String name) {return runtimeCache.getSymbol(context, 5, name);}
     public final RubySymbol getSymbol6(ThreadContext context, String name) {return runtimeCache.getSymbol(context, 6, name);}
     public final RubySymbol getSymbol7(ThreadContext context, String name) {return runtimeCache.getSymbol(context, 7, name);}
     public final RubySymbol getSymbol8(ThreadContext context, String name) {return runtimeCache.getSymbol(context, 8, name);}
     public final RubySymbol getSymbol9(ThreadContext context, String name) {return runtimeCache.getSymbol(context, 9, name);}
 
     public static final int NUMBERED_STRING_COUNT = 10;
 
     public final RubyString getString(ThreadContext context, int i, int codeRange) {return runtimeCache.getString(context, i, codeRange);}
     public final RubyString getString0(ThreadContext context, int codeRange) {return runtimeCache.getString(context, 0, codeRange);}
     public final RubyString getString1(ThreadContext context, int codeRange) {return runtimeCache.getString(context, 1, codeRange);}
     public final RubyString getString2(ThreadContext context, int codeRange) {return runtimeCache.getString(context, 2, codeRange);}
     public final RubyString getString3(ThreadContext context, int codeRange) {return runtimeCache.getString(context, 3, codeRange);}
     public final RubyString getString4(ThreadContext context, int codeRange) {return runtimeCache.getString(context, 4, codeRange);}
     public final RubyString getString5(ThreadContext context, int codeRange) {return runtimeCache.getString(context, 5, codeRange);}
     public final RubyString getString6(ThreadContext context, int codeRange) {return runtimeCache.getString(context, 6, codeRange);}
     public final RubyString getString7(ThreadContext context, int codeRange) {return runtimeCache.getString(context, 7, codeRange);}
     public final RubyString getString8(ThreadContext context, int codeRange) {return runtimeCache.getString(context, 8, codeRange);}
     public final RubyString getString9(ThreadContext context, int codeRange) {return runtimeCache.getString(context, 9, codeRange);}
 
     public final ByteList getByteList(int i) {return runtimeCache.getByteList(i);}
     public final ByteList getByteList0() {return runtimeCache.getByteList(0);}
     public final ByteList getByteList1() {return runtimeCache.getByteList(1);}
     public final ByteList getByteList2() {return runtimeCache.getByteList(2);}
     public final ByteList getByteList3() {return runtimeCache.getByteList(3);}
     public final ByteList getByteList4() {return runtimeCache.getByteList(4);}
     public final ByteList getByteList5() {return runtimeCache.getByteList(5);}
     public final ByteList getByteList6() {return runtimeCache.getByteList(6);}
     public final ByteList getByteList7() {return runtimeCache.getByteList(7);}
     public final ByteList getByteList8() {return runtimeCache.getByteList(8);}
     public final ByteList getByteList9() {return runtimeCache.getByteList(9);}
 
     public static final int NUMBERED_ENCODING_COUNT = 10;
 
     public final Encoding getEncoding(int i) {return runtimeCache.getEncoding(i);}
     public final Encoding getEncoding0() {return runtimeCache.getEncoding(0);}
     public final Encoding getEncoding1() {return runtimeCache.getEncoding(1);}
     public final Encoding getEncoding2() {return runtimeCache.getEncoding(2);}
     public final Encoding getEncoding3() {return runtimeCache.getEncoding(3);}
     public final Encoding getEncoding4() {return runtimeCache.getEncoding(4);}
     public final Encoding getEncoding5() {return runtimeCache.getEncoding(5);}
     public final Encoding getEncoding6() {return runtimeCache.getEncoding(6);}
     public final Encoding getEncoding7() {return runtimeCache.getEncoding(7);}
     public final Encoding getEncoding8() {return runtimeCache.getEncoding(8);}
     public final Encoding getEncoding9() {return runtimeCache.getEncoding(9);}
 
     public static final int NUMBERED_FIXNUM_COUNT = 10;
 
     public final RubyFixnum getFixnum(ThreadContext context, int i, int value) {return runtimeCache.getFixnum(context, i, value);}
     public final RubyFixnum getFixnum(ThreadContext context, int i, long value) {return runtimeCache.getFixnum(context, i, value);}
     public final RubyFixnum getFixnum0(ThreadContext context, int value) {return runtimeCache.getFixnum(context, 0, value);}
     public final RubyFixnum getFixnum1(ThreadContext context, int value) {return runtimeCache.getFixnum(context, 1, value);}
     public final RubyFixnum getFixnum2(ThreadContext context, int value) {return runtimeCache.getFixnum(context, 2, value);}
     public final RubyFixnum getFixnum3(ThreadContext context, int value) {return runtimeCache.getFixnum(context, 3, value);}
     public final RubyFixnum getFixnum4(ThreadContext context, int value) {return runtimeCache.getFixnum(context, 4, value);}
     public final RubyFixnum getFixnum5(ThreadContext context, int value) {return runtimeCache.getFixnum(context, 5, value);}
     public final RubyFixnum getFixnum6(ThreadContext context, int value) {return runtimeCache.getFixnum(context, 6, value);}
     public final RubyFixnum getFixnum7(ThreadContext context, int value) {return runtimeCache.getFixnum(context, 7, value);}
     public final RubyFixnum getFixnum8(ThreadContext context, int value) {return runtimeCache.getFixnum(context, 8, value);}
     public final RubyFixnum getFixnum9(ThreadContext context, int value) {return runtimeCache.getFixnum(context, 9, value);}
 
     public static final int NUMBERED_FLOAT_COUNT = 10;
 
     public final RubyFloat getFloat(ThreadContext context, int i, double value) {return runtimeCache.getFloat(context, i, value);}
     public final RubyFloat getFloat0(ThreadContext context, double value) {return runtimeCache.getFloat(context, 0, value);}
     public final RubyFloat getFloat1(ThreadContext context, double value) {return runtimeCache.getFloat(context, 1, value);}
     public final RubyFloat getFloat2(ThreadContext context, double value) {return runtimeCache.getFloat(context, 2, value);}
     public final RubyFloat getFloat3(ThreadContext context, double value) {return runtimeCache.getFloat(context, 3, value);}
     public final RubyFloat getFloat4(ThreadContext context, double value) {return runtimeCache.getFloat(context, 4, value);}
     public final RubyFloat getFloat5(ThreadContext context, double value) {return runtimeCache.getFloat(context, 5, value);}
     public final RubyFloat getFloat6(ThreadContext context, double value) {return runtimeCache.getFloat(context, 6, value);}
     public final RubyFloat getFloat7(ThreadContext context, double value) {return runtimeCache.getFloat(context, 7, value);}
     public final RubyFloat getFloat8(ThreadContext context, double value) {return runtimeCache.getFloat(context, 8, value);}
     public final RubyFloat getFloat9(ThreadContext context, double value) {return runtimeCache.getFloat(context, 9, value);}
 
     public static final int NUMBERED_REGEXP_COUNT = 10;
 
     public final RubyRegexp getRegexp(ThreadContext context, int i, ByteList pattern, int options) {return runtimeCache.getRegexp(context, i, pattern, options);}
     public final RubyRegexp getRegexp0(ThreadContext context, ByteList pattern, int options) {return runtimeCache.getRegexp(context, 0, pattern, options);}
     public final RubyRegexp getRegexp1(ThreadContext context, ByteList pattern, int options) {return runtimeCache.getRegexp(context, 1, pattern, options);}
     public final RubyRegexp getRegexp2(ThreadContext context, ByteList pattern, int options) {return runtimeCache.getRegexp(context, 2, pattern, options);}
     public final RubyRegexp getRegexp3(ThreadContext context, ByteList pattern, int options) {return runtimeCache.getRegexp(context, 3, pattern, options);}
     public final RubyRegexp getRegexp4(ThreadContext context, ByteList pattern, int options) {return runtimeCache.getRegexp(context, 4, pattern, options);}
     public final RubyRegexp getRegexp5(ThreadContext context, ByteList pattern, int options) {return runtimeCache.getRegexp(context, 5, pattern, options);}
     public final RubyRegexp getRegexp6(ThreadContext context, ByteList pattern, int options) {return runtimeCache.getRegexp(context, 6, pattern, options);}
     public final RubyRegexp getRegexp7(ThreadContext context, ByteList pattern, int options) {return runtimeCache.getRegexp(context, 7, pattern, options);}
     public final RubyRegexp getRegexp8(ThreadContext context, ByteList pattern, int options) {return runtimeCache.getRegexp(context, 8, pattern, options);}
     public final RubyRegexp getRegexp9(ThreadContext context, ByteList pattern, int options) {return runtimeCache.getRegexp(context, 9, pattern, options);}
 
     public static final int NUMBERED_BIGINTEGER_COUNT = 10;
 
     public final BigInteger getBigInteger(int i, String name) {return runtimeCache.getBigInteger(i, name);}
     public final BigInteger getBigInteger0(String name) {return runtimeCache.getBigInteger(0, name);}
     public final BigInteger getBigInteger1(String name) {return runtimeCache.getBigInteger(1, name);}
     public final BigInteger getBigInteger2(String name) {return runtimeCache.getBigInteger(2, name);}
     public final BigInteger getBigInteger3(String name) {return runtimeCache.getBigInteger(3, name);}
     public final BigInteger getBigInteger4(String name) {return runtimeCache.getBigInteger(4, name);}
     public final BigInteger getBigInteger5(String name) {return runtimeCache.getBigInteger(5, name);}
     public final BigInteger getBigInteger6(String name) {return runtimeCache.getBigInteger(6, name);}
     public final BigInteger getBigInteger7(String name) {return runtimeCache.getBigInteger(7, name);}
     public final BigInteger getBigInteger8(String name) {return runtimeCache.getBigInteger(8, name);}
     public final BigInteger getBigInteger9(String name) {return runtimeCache.getBigInteger(9, name);}
 
     public static final int NUMBERED_VARIABLEREADER_COUNT = 10;
 
     public final IRubyObject getVariable(ThreadContext context, int i, String name, IRubyObject object) {return runtimeCache.getVariable(context, i, name, object);}
     public final IRubyObject getVariable0(ThreadContext context, String name, IRubyObject object) {return runtimeCache.getVariable(context, 0, name, object);}
     public final IRubyObject getVariable1(ThreadContext context, String name, IRubyObject object) {return runtimeCache.getVariable(context, 1, name, object);}
     public final IRubyObject getVariable2(ThreadContext context, String name, IRubyObject object) {return runtimeCache.getVariable(context, 2, name, object);}
     public final IRubyObject getVariable3(ThreadContext context, String name, IRubyObject object) {return runtimeCache.getVariable(context, 3, name, object);}
     public final IRubyObject getVariable4(ThreadContext context, String name, IRubyObject object) {return runtimeCache.getVariable(context, 4, name, object);}
     public final IRubyObject getVariable5(ThreadContext context, String name, IRubyObject object) {return runtimeCache.getVariable(context, 5, name, object);}
     public final IRubyObject getVariable6(ThreadContext context, String name, IRubyObject object) {return runtimeCache.getVariable(context, 6, name, object);}
     public final IRubyObject getVariable7(ThreadContext context, String name, IRubyObject object) {return runtimeCache.getVariable(context, 7, name, object);}
     public final IRubyObject getVariable8(ThreadContext context, String name, IRubyObject object) {return runtimeCache.getVariable(context, 8, name, object);}
     public final IRubyObject getVariable9(ThreadContext context, String name, IRubyObject object) {return runtimeCache.getVariable(context, 9, name, object);}
 
     public static final int NUMBERED_VARIABLEWRITER_COUNT = 10;
 
     public final IRubyObject setVariable(int i, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(i, name, object, value);}
     public final IRubyObject setVariable0(String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(0, name, object, value);}
     public final IRubyObject setVariable1(String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(1, name, object, value);}
     public final IRubyObject setVariable2(String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(2, name, object, value);}
     public final IRubyObject setVariable3(String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(3, name, object, value);}
     public final IRubyObject setVariable4(String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(4, name, object, value);}
     public final IRubyObject setVariable5(String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(5, name, object, value);}
     public final IRubyObject setVariable6(String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(6, name, object, value);}
     public final IRubyObject setVariable7(String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(7, name, object, value);}
     public final IRubyObject setVariable8(String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(8, name, object, value);}
     public final IRubyObject setVariable9(String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(9, name, object, value);}
 
     public static final int NUMBERED_CONSTANT_COUNT = 10;
 
     public final IRubyObject getConstant(ThreadContext context, String name, int i) {return runtimeCache.getConstant(context, name, i);}
     public final IRubyObject getConstant0(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 0);}
     public final IRubyObject getConstant1(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 1);}
     public final IRubyObject getConstant2(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 2);}
     public final IRubyObject getConstant3(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 3);}
     public final IRubyObject getConstant4(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 4);}
     public final IRubyObject getConstant5(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 5);}
     public final IRubyObject getConstant6(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 6);}
     public final IRubyObject getConstant7(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 7);}
     public final IRubyObject getConstant8(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 8);}
     public final IRubyObject getConstant9(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 9);}
 
     public static final int NUMBERED_CONSTANTFROM_COUNT = 10;
 
     public final IRubyObject getConstantFrom(RubyModule target, ThreadContext context, String name, int i) {return runtimeCache.getConstantFrom(target, context, name, i);}
     public final IRubyObject getConstantFrom0(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 0);}
     public final IRubyObject getConstantFrom1(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 1);}
     public final IRubyObject getConstantFrom2(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 2);}
     public final IRubyObject getConstantFrom3(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 3);}
     public final IRubyObject getConstantFrom4(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 4);}
     public final IRubyObject getConstantFrom5(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 5);}
     public final IRubyObject getConstantFrom6(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 6);}
     public final IRubyObject getConstantFrom7(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 7);}
     public final IRubyObject getConstantFrom8(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 8);}
     public final IRubyObject getConstantFrom9(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 9);}
 
     public static final int NUMBERED_METHOD_COUNT = 10;
 
     protected DynamicMethod getMethod(ThreadContext context, IRubyObject self, int i, String methodName) {
         return runtimeCache. getMethod(context, self, i, methodName);
     }
     protected DynamicMethod getMethod0(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 0, methodName);
     }
     protected DynamicMethod getMethod1(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 1, methodName);
     }
     protected DynamicMethod getMethod2(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 2, methodName);
     }
     protected DynamicMethod getMethod3(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 3, methodName);
     }
     protected DynamicMethod getMethod4(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 4, methodName);
     }
     protected DynamicMethod getMethod5(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 5, methodName);
     }
     protected DynamicMethod getMethod6(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 6, methodName);
     }
     protected DynamicMethod getMethod7(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 7, methodName);
     }
     protected DynamicMethod getMethod8(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 8, methodName);
     }
     protected DynamicMethod getMethod9(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 9, methodName);
     }
 
     public void setByteList(int index, String str, Encoding encoding) {
         // decode chars back into bytes
         char[] chars = str.toCharArray();
         byte[] bytes = new byte[chars.length];
         for (int i = 0; i < chars.length; i++) {
             bytes[i] = (byte)chars[i];
         }
 
         runtimeCache.byteLists[index] = new ByteList(bytes, encoding, false);
     }
 
     public void setEncoding(int index, String encStr) {
         runtimeCache.encodings[index] = EncodingDB.getEncodings().get(encStr.getBytes()).getEncoding();
     }
 
     public static CallSite[] setCallSite(CallSite[] callSites, int index, String name) {
         callSites[index] = MethodIndex.getCallSite(name);
         return callSites;
     }
 
     public static CallSite[] setFunctionalCallSite(CallSite[] callSites, int index, String name) {
         callSites[index] = MethodIndex.getFunctionalCallSite(name);
         return callSites;
     }
 
     public static CallSite[] setVariableCallSite(CallSite[] callSites, int index, String name) {
         callSites[index] = MethodIndex.getVariableCallSite(name);
         return callSites;
     }
 
     public static CallSite[] setSuperCallSite(CallSite[] callSites, int index) {
         callSites[index] = MethodIndex.getSuperCallSite();
         return callSites;
     }
 
     public final void setFilename(String filename) {
         this.filename = filename;
     }
 
     public final void initFromDescriptor(String descriptor) {
         runtimeCache.initFromDescriptor(descriptor);
     }
 
     protected String filename;
 }
diff --git a/src/org/jruby/ast/executable/Script.java b/src/org/jruby/ast/executable/Script.java
index 0974e92aa5..8d6d1636a0 100644
--- a/src/org/jruby/ast/executable/Script.java
+++ b/src/org/jruby/ast/executable/Script.java
@@ -1,17 +1,19 @@
 package org.jruby.ast.executable;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public interface Script {
     public IRubyObject __file__(ThreadContext context, IRubyObject self, Block block);
     public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg, Block block);
     public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2, Block block);
     public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg, IRubyObject arg2, IRubyObject arg3, Block block);
     public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block);
     
     public IRubyObject run(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block);
+    @Deprecated
     public IRubyObject load(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block);
+    public IRubyObject load(ThreadContext context, IRubyObject self, boolean wrap);
     public void setFilename(String filename);
 }
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index c5e5f9d7cb..bc84242a41 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -1,752 +1,759 @@
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
  * Copyright (C) 2006 Charles O Nutter <headius@headius.com>
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
 
 package org.jruby.compiler.impl;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jruby.Ruby;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ast.executable.AbstractScript;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.CacheCompiler;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.compiler.BodyCompiler;
 import org.jruby.compiler.ScriptCompiler;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.opto.OptoFactory;
 import static org.jruby.util.CodegenUtils.*;
 import org.jruby.util.JRubyClassLoader;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.ClassVisitor;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.Type;
 import org.objectweb.asm.util.CheckClassAdapter;
 import org.objectweb.asm.util.TraceClassVisitor;
 
 /**
  *
  * @author headius
  */
 public class StandardASMCompiler implements ScriptCompiler, Opcodes {
     public static final String THREADCONTEXT = p(ThreadContext.class);
     public static final String RUBY = p(Ruby.class);
     public static final String IRUBYOBJECT = p(IRubyObject.class);
     public static final boolean VERIFY_CLASSFILES = true;
 
     public static String getStaticMethodSignature(String classname, int args) {
         switch (args) {
         case 0:
             return sig(IRubyObject.class, "L" + classname + ";", ThreadContext.class, IRubyObject.class, Block.class);
         case 1:
             return sig(IRubyObject.class, "L" + classname + ";", ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 2:
             return sig(IRubyObject.class, "L" + classname + ";", ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 3:
             return sig(IRubyObject.class, "L" + classname + ";", ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 4:
             return sig(IRubyObject.class, "L" + classname + ";", ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
         default:
             throw new RuntimeException("unsupported arity: " + args);
         }
     }
 
     public static Class[] getStaticMethodParams(Class target, int args) {
         switch (args) {
         case 0:
             return new Class[] {target, ThreadContext.class, IRubyObject.class, Block.class};
         case 1:
             return new Class[] {target, ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class};
         case 2:
             return new Class[] {target, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class};
         case 3:
             return new Class[] {target, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class};
         case 4:
             return new Class[] {target, ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class};
         default:
             throw new RuntimeException("unsupported arity: " + args);
         }
     }
 
     public static String getMethodSignature(int args) {
         switch (args) {
         case 0:
             return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, Block.class);
         case 1:
             return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 2:
             return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 3:
             return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 4:
             return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
         default:
             throw new RuntimeException("unsupported arity: " + args);
         }
     }
 
     public static String getStaticClosureSignature(String classdesc) {
         return sig(IRubyObject.class, "L" + classdesc + ";", ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class);
     }
 
     public static String getStaticClosure19Signature(String classdesc) {
         return sig(IRubyObject.class, "L" + classdesc + ";", ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
     }
 
     public static String getClosureSignature() {
         return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class);
     }
 
     public static String getClosure19Signature() {
         return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
     }
 
     public static final int THIS = 0;
     public static final int THREADCONTEXT_INDEX = 1;
     public static final int SELF_INDEX = 2;
     public static final int ARGS_INDEX = 3;
     
     public static final int CLOSURE_OFFSET = 0;
     public static final int DYNAMIC_SCOPE_OFFSET = 1;
     public static final int VARS_ARRAY_OFFSET = 2;
     public static final int EXCEPTION_OFFSET = 3;
     public static final int PREVIOUS_EXCEPTION_OFFSET = 4;
     public static final int FIRST_TEMP_OFFSET = 5;
 
     public static final int STARTING_DSTR_SIZE = 20;
     
     private String classname;
     private String sourcename;
 
     private Integer javaVersion;
 
     private ClassWriter classWriter;
     private SkinnyMethodAdapter initMethod;
     private SkinnyMethodAdapter clinitMethod;
     private int methodIndex = 0;
     private int innerIndex = 0;
     private int rescueNumber = 1;
     private int ensureNumber = 1;
     StaticScope topLevelScope;
     
     private CacheCompiler cacheCompiler;
 
     private List<InvokerDescriptor> invokerDescriptors = new ArrayList<InvokerDescriptor>();
     private List<BlockCallbackDescriptor> blockCallbackDescriptors = new ArrayList<BlockCallbackDescriptor>();
     private List<BlockCallbackDescriptor> blockCallback19Descriptors = new ArrayList<BlockCallbackDescriptor>();
     
     /** Creates a new instance of StandardCompilerContext */
     public StandardASMCompiler(String classname, String sourcename) {
         this.classname = classname;
         this.sourcename = sourcename;
     }
 
     public void setJavaVersion(Integer javaVersion) {
         this.javaVersion = javaVersion;
     }
 
     public byte[] getClassByteArray() {
         return classWriter.toByteArray();
     }
 
     public Class<?> loadClass(JRubyClassLoader classLoader) throws ClassNotFoundException {
         classLoader.defineClass(c(getClassname()), classWriter.toByteArray());
         return classLoader.loadClass(c(getClassname()));
     }
     
     public void dumpClass(PrintStream out) {
         PrintWriter pw = new PrintWriter(out);
 
         try {
             TraceClassVisitor tcv = new TraceClassVisitor(pw);
             new ClassReader(classWriter.toByteArray()).accept(tcv, 0);
         } finally {
             pw.close();
         }
     }
 
     public void writeClass(File destination) throws IOException {
         writeClass(getClassname(), destination, classWriter);
     }
 
     public void writeInvokers(File destination) throws IOException {
         for (InvokerDescriptor descriptor : invokerDescriptors) {
             byte[] invokerBytes = RuntimeHelpers.defOffline(
                     descriptor.getName(),
                     descriptor.getClassname(),
                     descriptor.getInvokerName(),
                     descriptor.getArity(),
                     descriptor.getScope(),
                     descriptor.getCallConfig(),
                     descriptor.getFile(),
                     descriptor.getLine());
 
             if (VERIFY_CLASSFILES) CheckClassAdapter.verify(new ClassReader(invokerBytes), false, new PrintWriter(System.err));
 
             writeClassFile(destination, invokerBytes, descriptor.getInvokerName());
         }
 
         for (BlockCallbackDescriptor descriptor : blockCallbackDescriptors) {
             byte[] callbackBytes = RuntimeHelpers.createBlockCallbackOffline(
                     descriptor.getClassname(),
                     descriptor.getMethod(),
                     descriptor.getFile(),
                     descriptor.getLine());
 
             if (VERIFY_CLASSFILES) CheckClassAdapter.verify(new ClassReader(callbackBytes), false, new PrintWriter(System.err));
 
             writeClassFile(destination, callbackBytes, descriptor.getCallbackName());
         }
 
         for (BlockCallbackDescriptor descriptor : blockCallback19Descriptors) {
             byte[] callbackBytes = RuntimeHelpers.createBlockCallback19Offline(
                     descriptor.getClassname(),
                     descriptor.getMethod(),
                     descriptor.getFile(),
                     descriptor.getLine());
 
             if (VERIFY_CLASSFILES) CheckClassAdapter.verify(new ClassReader(callbackBytes), false, new PrintWriter(System.err));
 
             writeClassFile(destination, callbackBytes, descriptor.getCallbackName());
         }
     }
 
     private void writeClass(String classname, File destination, ClassWriter writer) throws IOException {
         // verify the class
         byte[] bytecode = writer.toByteArray();
         if (VERIFY_CLASSFILES) CheckClassAdapter.verify(new ClassReader(bytecode), false, new PrintWriter(System.err));
 
         writeClassFile(destination, bytecode, classname);
     }
 
     private void writeClassFile(File destination, byte[] bytecode, String classname) throws IOException {
         String fullname = classname + ".class";
         String filename = null;
         String path = null;
 
         if (fullname.lastIndexOf("/") == -1) {
             filename = fullname;
             path = "";
         } else {
             filename = fullname.substring(fullname.lastIndexOf("/") + 1);
             path = fullname.substring(0, fullname.lastIndexOf("/"));
         }
         // create dir if necessary
         File pathfile = new File(destination, path);
         pathfile.mkdirs();
 
         FileOutputStream out = new FileOutputStream(new File(pathfile, filename));
 
         try {
             out.write(bytecode);
         } finally {
             out.close();
         }
     }
 
     public static class InvokerDescriptor {
         private final String name;
         private final String classname;
         private final String invokerName;
         private final Arity arity;
         private final StaticScope scope;
         private final CallConfiguration callConfig;
         private final String file;
         private final int line;
         
         public InvokerDescriptor(String name, String classname, String invokerName, Arity arity, StaticScope scope, CallConfiguration callConfig, String file, int line) {
             this.name = name;
             this.classname = classname;
             this.invokerName = invokerName;
             this.arity = arity;
             this.scope = scope;
             this.callConfig = callConfig;
             this.file = file;
             this.line = line;
         }
 
         public Arity getArity() {
             return arity;
         }
 
         public CallConfiguration getCallConfig() {
             return callConfig;
         }
 
         public String getClassname() {
             return classname;
         }
 
         public String getFile() {
             return file;
         }
 
         public String getInvokerName() {
             return invokerName;
         }
 
         public int getLine() {
             return line;
         }
 
         public String getName() {
             return name;
         }
 
         public StaticScope getScope() {
             return scope;
         }
     }
 
     private static class BlockCallbackDescriptor {
         private final String method;
         private final String classname;
         private final String callbackName;
         private final String file;
         private final int line;
 
         public BlockCallbackDescriptor(String method, String classname, String file, int line) {
             this.method = method;
             this.classname = classname;
             this.callbackName = classname + "BlockCallback$" + method + "xx1";
             this.file = file;
             this.line = line;
         }
 
         public String getClassname() {
             return classname;
         }
 
         public String getMethod() {
             return method;
         }
 
         public String getCallbackName() {
             return callbackName;
         }
 
         public String getFile() {
             return file;
         }
 
         public int getLine() {
             return line;
         }
     }
 
     public void addInvokerDescriptor(String newMethodName, int methodArity, StaticScope scope, CallConfiguration callConfig, String filename, int line) {
         String classPath = classname.replaceAll("/", "_");
         Arity arity = Arity.createArity(methodArity);
         String invokerName = classPath + "Invoker" + newMethodName + arity;
         InvokerDescriptor descriptor = new InvokerDescriptor(newMethodName, classname, invokerName, arity, scope, callConfig, filename, line);
 
         invokerDescriptors.add(descriptor);
     }
 
     public void addBlockCallbackDescriptor(String method, String file, int line) {
         blockCallbackDescriptors.add(new BlockCallbackDescriptor(method, classname, file, line));
     }
 
     public void addBlockCallback19Descriptor(String method, String file, int line) {
         blockCallback19Descriptors.add(new BlockCallbackDescriptor(method, classname, file, line));
     }
 
     public String getClassname() {
         return classname;
     }
 
     public String getSourcename() {
         return sourcename;
     }
 
     public ClassVisitor getClassVisitor() {
         return classWriter;
     }
     
     public void startScript(StaticScope scope) {
         classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
 
         // Create the class with the appropriate class name and source file
         classWriter.visit(javaVersion == null ? RubyInstanceConfig.JAVA_VERSION : javaVersion,
                 ACC_PUBLIC + ACC_SUPER,getClassname(), null, p(AbstractScript.class), null);
 
         // add setPosition impl, which stores filename as constant to speed updates
         SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor(), ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, "setPosition", sig(Void.TYPE, params(ThreadContext.class, int.class)), null, null);
         method.start();
 
         method.aload(0); // thread context
         method.ldc(sourcename);
         method.iload(1); // line number
         method.invokevirtual(p(ThreadContext.class), "setFileAndLine", sig(void.class, String.class, int.class));
         method.voidreturn();
         method.end();
         
         topLevelScope = scope;
 
         beginInit();
         
         cacheCompiler = OptoFactory.newCacheCompiler(this);
 
         // This code was originally used to provide debugging info using JSR-45
         // "SMAP" format. However, it breaks using normal Java traces to
         // generate Ruby traces, since the original path is lost. Reverting
         // to full path for now.
 //        String sourceNoPath;
 //        if (sourcename.indexOf("/") >= 0) {
 //            String[] pathElements = sourcename.split("/");
 //            sourceNoPath = pathElements[pathElements.length - 1];
 //        } else if (sourcename.indexOf("\\") >= 0) {
 //            String[] pathElements = sourcename.split("\\\\");
 //            sourceNoPath = pathElements[pathElements.length - 1];
 //        } else {
 //            sourceNoPath = sourcename;
 //        }
 
         final File sourceFile = new File(getSourcename());
         // Revert to using original sourcename here, so that jitted traces match
         // interpreted traces.
         classWriter.visitSource(sourcename, sourceFile.getAbsolutePath());
     }
 
     public void endScript(boolean generateLoad, boolean generateMain) {
         // add Script#run impl, used for running this script with a specified threadcontext and self
         // root method of a script is always in __file__ method
         String methodName = "__file__";
         
+        String loadSig = sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, boolean.class);
+        
         if (generateLoad || generateMain) {
             // the load method is used for loading as a top-level script, and prepares appropriate scoping around the code
-            SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor(), ACC_PUBLIC, "load", getMethodSignature(4), null, null);
+            SkinnyMethodAdapter method = new SkinnyMethodAdapter(
+                    getClassVisitor(),
+                    ACC_PUBLIC,
+                    "load",
+                    loadSig,
+                    null,
+                    null);
             method.start();
 
             // invoke __file__ with threadcontext, self, args (null), and block (null)
             Label tryBegin = new Label();
             Label tryFinally = new Label();
 
             method.label(tryBegin);
             method.aload(THREADCONTEXT_INDEX);
             String scopeNames = RuntimeHelpers.encodeScope(topLevelScope);
             method.ldc(scopeNames);
-            method.invokestatic(p(RuntimeHelpers.class), "preLoad", sig(void.class, ThreadContext.class, String.class));
+            method.iload(SELF_INDEX + 1);
+            method.invokestatic(p(RuntimeHelpers.class), "preLoad", sig(void.class, ThreadContext.class, String.class, boolean.class));
 
             method.aload(THIS);
             method.aload(THREADCONTEXT_INDEX);
             method.aload(SELF_INDEX);
-            method.aload(ARGS_INDEX);
-            // load always uses IRubyObject[], so simple closure offset calculation here
-            method.aload(ARGS_INDEX + 1 + CLOSURE_OFFSET);
+            method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
+            method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
             method.invokestatic(getClassname(),methodName, getStaticMethodSignature(getClassname(), 4));
             method.aload(THREADCONTEXT_INDEX);
             method.invokestatic(p(RuntimeHelpers.class), "postLoad", sig(void.class, ThreadContext.class));
             method.areturn();
 
             method.label(tryFinally);
             method.aload(THREADCONTEXT_INDEX);
             method.invokestatic(p(RuntimeHelpers.class), "postLoad", sig(void.class, ThreadContext.class));
             method.athrow();
 
             method.trycatch(tryBegin, tryFinally, tryFinally, null);
 
             method.end();
         }
         
         if (generateMain) {
             // add main impl, used for detached or command-line execution of this script with a new runtime
             // root method of a script is always in stub0, method0
             SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor(), ACC_PUBLIC | ACC_STATIC, "main", sig(Void.TYPE, params(String[].class)), null, null);
             method.start();
 
             // new instance to invoke run against
             method.newobj(getClassname());
             method.dup();
             method.invokespecial(getClassname(), "<init>", sig(Void.TYPE));
 
             // set filename for the loaded script class (JRUBY-4825)
             method.dup();
             method.ldc(Type.getType("L" + getClassname() + ";"));
             method.invokevirtual(p(Class.class), "getClassLoader", sig(ClassLoader.class));
             method.ldc(getClassname() + ".class");
             method.invokevirtual(p(ClassLoader.class), "getResource", sig(URL.class, String.class));
             method.invokevirtual(p(Object.class), "toString", sig(String.class));
             method.astore(1);
             method.aload(1);
             method.invokevirtual(p(AbstractScript.class), "setFilename", sig(void.class, String.class));
 
             // instance config for the script run
             method.newobj(p(RubyInstanceConfig.class));
             method.dup();
             method.invokespecial(p(RubyInstanceConfig.class), "<init>", "()V");
 
             // set argv from main's args
             method.dup();
             method.aload(0);
             method.invokevirtual(p(RubyInstanceConfig.class), "setArgv", sig(void.class, String[].class));
 
             // set script filename ($0)
             method.dup();
             method.aload(1);
             method.invokevirtual(p(RubyInstanceConfig.class), "setScriptFileName", sig(void.class, String.class));
 
             // invoke run with threadcontext and topself
             method.invokestatic(p(Ruby.class), "newInstance", sig(Ruby.class, RubyInstanceConfig.class));
             method.dup();
 
             method.invokevirtual(RUBY, "getCurrentContext", sig(ThreadContext.class));
             method.swap();
             method.invokevirtual(RUBY, "getTopSelf", sig(IRubyObject.class));
-            method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
-            method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
+            method.ldc(false);
 
-            method.invokevirtual(getClassname(), "load", getMethodSignature(4));
+            method.invokevirtual(getClassname(), "load", loadSig);
             method.voidreturn();
             method.end();
         }
 
         getCacheCompiler().finish();
         
         endInit();
         endClassInit();
     }
 
     public static String buildStaticScopeNames(StaticScope scope) {
         return RuntimeHelpers.encodeScope(scope);
     }
 
     private void beginInit() {
         ClassVisitor cv = getClassVisitor();
 
         initMethod = new SkinnyMethodAdapter(cv, ACC_PUBLIC, "<init>", sig(Void.TYPE), null, null);
         initMethod.start();
         initMethod.aload(THIS);
         initMethod.invokespecial(p(AbstractScript.class), "<init>", sig(Void.TYPE));
         
         // JRUBY-3014: make __FILE__ dynamically determined at load time, but
         // we provide a reasonable default here
         initMethod.aload(THIS);
         initMethod.ldc(getSourcename());
         initMethod.putfield(getClassname(), "filename", ci(String.class));
     }
 
     private void endInit() {
         initMethod.voidreturn();
         initMethod.end();
     }
 
     private void beginClassInit() {
         ClassVisitor cv = getClassVisitor();
 
         clinitMethod = new SkinnyMethodAdapter(cv, ACC_PUBLIC | ACC_STATIC, "<clinit>", sig(Void.TYPE), null, null);
         clinitMethod.start();
     }
 
     private void endClassInit() {
         if (clinitMethod != null) {
             clinitMethod.voidreturn();
             clinitMethod.end();
         }
     }
     
     public SkinnyMethodAdapter getInitMethod() {
         return initMethod;
     }
     
     public SkinnyMethodAdapter getClassInitMethod() {
         // lazily create class init only if necessary
         if (clinitMethod == null) {
             beginClassInit();
         }
         return clinitMethod;
     }
     
     public CacheCompiler getCacheCompiler() {
         return cacheCompiler;
     }
     
     public BodyCompiler startMethod(String rubyName, String javaName, CompilerCallback args, StaticScope scope, ASTInspector inspector) {
         RootScopedBodyCompiler methodCompiler = new MethodBodyCompiler(this, rubyName, javaName, inspector, scope);
         
         methodCompiler.beginMethod(args, scope);
         
         return methodCompiler;
     }
 
     public BodyCompiler startFileMethod(CompilerCallback args, StaticScope scope, ASTInspector inspector) {
         MethodBodyCompiler methodCompiler = new MethodBodyCompiler(this, "__file__", "__file__", inspector, scope);
         
         methodCompiler.beginMethod(args, scope);
         
         // boxed arg list __file__
         SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor(), ACC_PUBLIC, "__file__", getMethodSignature(4), null, null);
         method.start();
 
         // invoke static __file__
         method.aload(THIS);
         method.aload(THREADCONTEXT_INDEX);
         method.aload(SELF_INDEX);
         method.aload(ARGS_INDEX);
         method.aload(ARGS_INDEX + 1); // block
         method.invokestatic(getClassname(), "__file__", getStaticMethodSignature(getClassname(), 4));
 
         method.areturn();
         method.end();
         
         if (methodCompiler.isSpecificArity()) {
             // exact arg list __file__
             method = new SkinnyMethodAdapter(getClassVisitor(), ACC_PUBLIC, "__file__", getMethodSignature(scope.getRequiredArgs()), null, null);
             method.start();
 
             // invoke static __file__
             method.aload(THIS);
             method.aload(THREADCONTEXT_INDEX);
             method.aload(SELF_INDEX);
             for (int i = 0; i < scope.getRequiredArgs(); i++) {
                 method.aload(ARGS_INDEX + i);
             }
             method.aload(ARGS_INDEX + scope.getRequiredArgs()); // block
             method.invokestatic(getClassname(), "__file__", getStaticMethodSignature(getClassname(), scope.getRequiredArgs()));
 
             method.areturn();
             method.end();
         }
 
         return methodCompiler;
     }
 
     public BodyCompiler startRoot(String rubyName, String javaName, StaticScope scope, ASTInspector inspector) {
         RootScopedBodyCompiler methodCompiler = new MethodBodyCompiler(this, rubyName, javaName, inspector, scope);
 
         methodCompiler.beginMethod(null, scope);
 
         return methodCompiler;
     }
 
     public int getMethodIndex() {
         return methodIndex;
     }
     
     public int getAndIncrementMethodIndex() {
         return methodIndex++;
     }
 
     public int getInnerIndex() {
         return innerIndex;
     }
 
     public int getAndIncrementInnerIndex() {
         return innerIndex++;
     }
 
     public int getRescueNumber() {
         return rescueNumber;
     }
 
     public int getAndIncrementRescueNumber() {
         return rescueNumber++;
     }
 
     public int getEnsureNumber() {
         return ensureNumber;
     }
 
     public int getAndIncrementEnsureNumber() {
         return ensureNumber++;
     }
 
     private int constants = 0;
 
     public String getNewConstant(String type, String name_prefix) {
         return getNewConstant(type, name_prefix, null);
     }
 
     public synchronized String getNewConstantName() {
         return "_" + constants++;
     }
 
     public String getNewConstant(String type, String name_prefix, Object init) {
         ClassVisitor cv = getClassVisitor();
 
         String realName = getNewConstantName();
 
         // declare the field
         cv.visitField(ACC_PRIVATE, realName, type, null, null).visitEnd();
 
         if(init != null) {
             initMethod.aload(THIS);
             initMethod.ldc(init);
             initMethod.putfield(getClassname(),realName, type);
         }
 
         return realName;
     }
 
     public String getNewField(String type, String name, Object init) {
         ClassVisitor cv = getClassVisitor();
 
         // declare the field
         cv.visitField(ACC_PRIVATE, name, type, null, null).visitEnd();
 
         if(init != null) {
             initMethod.aload(THIS);
             initMethod.ldc(init);
             initMethod.putfield(getClassname(),name, type);
         }
 
         return name;
     }
 
     public String getNewStaticConstant(String type, String name_prefix) {
         ClassVisitor cv = getClassVisitor();
 
         String realName;
         synchronized (this) {
             realName = "__" + constants++;
         }
 
         // declare the field
         cv.visitField(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, realName, type, null, null).visitEnd();
         return realName;
     }
 }
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index 0e1ac97c47..8b7ab417c7 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -576,2012 +576,2016 @@ public class RuntimeHelpers {
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
 
     public static IRubyObject invokeChecked(ThreadContext context, IRubyObject self, String name) {
         return self.getMetaClass().finvokeChecked(context, self, name);
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
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
         
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, args, block);
         }
         return method.call(context, self, superClass, name, args, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, block);
         }
         return method.call(context, self, superClass, name, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, block);
         }
         return method.call(context, self, superClass, name, arg0, block);
     }
     
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
         if (rubyModule == null || rubyModule.isNil()) {
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
      * @param runtime
      */
     public static Throwable unwrapRedoNextBreakOrJustLocalJump(RaiseException re, ThreadContext context) {
         RubyException exception = re.getException();
         if (context.getRuntime().getLocalJumpError().isInstance(exception)) {
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
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         if(backref instanceof RubyMatchData) {
             ((RubyMatchData)backref).use();
         }
         return backref;
     }
     
     public static IRubyObject backrefLastMatch(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         return RubyRegexp.last_match(backref);
     }
     
     public static IRubyObject backrefMatchPre(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         return RubyRegexp.match_pre(backref);
     }
     
     public static IRubyObject backrefMatchPost(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         return RubyRegexp.match_post(backref);
     }
     
     public static IRubyObject backrefMatchLast(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
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
         return context.getRuntime().getFalse();
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception, ThreadContext context) {
         return isExceptionHandled((IRubyObject)currentException, exception, context);
     }
 
     public static IRubyObject isExceptionHandled(IRubyObject currentException, IRubyObject exception, ThreadContext context) {
         Ruby runtime = context.getRuntime();
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
         if (context.getRuntime().getException().op_ge(catchable).isTrue() ||
                 context.getRuntime().getObject() == catchable) {
             if (throwable instanceof RaiseException) {
                 return isExceptionHandled(((RaiseException)throwable).getException(), catchable, context).isTrue();
             }
             // let Ruby exceptions decide if they handle it
             return isExceptionHandled(JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), throwable), catchable, context).isTrue();
         }
         if (catchable instanceof RubyClass && catchable.getInstanceVariables().hasInstanceVariable("@java_class")) {
             RubyClass rubyClass = (RubyClass)catchable;
             JavaClass javaClass = (JavaClass)rubyClass.fastGetInstanceVariable("@java_class");
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
             for (int i = 0; i < throwables.length; i++) {
                 if (checkJavaException(currentThrowable, throwables[0], context)) {
                     return context.getRuntime().getTrue();
                 }
             }
 
             return context.getRuntime().getFalse();
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
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
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
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1, context)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
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
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1, context)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable2, context)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static void storeExceptionInErrorInfo(Throwable currentThrowable, ThreadContext context) {
         IRubyObject exception = null;
         if (currentThrowable instanceof RaiseException) {
             exception = ((RaiseException)currentThrowable).getException();
         } else {
             exception = JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), currentThrowable);
         }
         context.setErrorInfo(exception);
     }
 
     public static void clearErrorInfo(ThreadContext context) {
         context.setErrorInfo(context.getRuntime().getNil());
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
                 return context.getRuntime().getTrue();
             }
         }
         
         return context.getRuntime().getFalse();
     }
     
     public static IRubyObject setConstantInModule(IRubyObject module, IRubyObject value, String name, ThreadContext context) {
         return context.setConstantInModule(name, module, value);
     }
 
     public static IRubyObject setConstantInCurrent(IRubyObject value, ThreadContext context, String name) {
         return context.setConstantInCurrent(name, value);
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
 
     public static IRubyObject undefMethod(ThreadContext context, Object nameArg) {
         RubyModule module = context.getRubyClass();
 
         String name = (nameArg instanceof String) ?
             (String) nameArg : nameArg.toString();
 
         if (module == null) {
             throw context.getRuntime().newTypeError("No class to undef method '" + name + "'.");
         }
 
         module.undef(context, name);
 
         return context.getRuntime().getNil();
     }
     
     public static IRubyObject defineAlias(ThreadContext context, IRubyObject self, Object newNameArg, Object oldNameArg) {
         Ruby runtime = context.getRuntime();
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
     
     public static void preLoad(ThreadContext context, String[] varNames) {
         StaticScope staticScope = new LocalStaticScope(null, varNames);
-        preLoadCommon(context, staticScope);
+        preLoadCommon(context, staticScope, false);
     }
 
-    public static void preLoad(ThreadContext context, String scopeString) {
+    public static void preLoad(ThreadContext context, String scopeString, boolean wrap) {
         StaticScope staticScope = decodeRootScope(context, scopeString);
-        preLoadCommon(context, staticScope);
+        preLoadCommon(context, staticScope, wrap);
     }
 
-    private static void preLoadCommon(ThreadContext context, StaticScope staticScope) {
+    private static void preLoadCommon(ThreadContext context, StaticScope staticScope, boolean wrap) {
         RubyClass objectClass = context.getRuntime().getObject();
         IRubyObject topLevel = context.getRuntime().getTopSelf();
-        staticScope.setModule(objectClass);
+        if (wrap) {
+            staticScope.setModule(RubyModule.newModule(context.runtime));
+        } else {
+            staticScope.setModule(objectClass);
+        }
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
         StringBuilder namesBuilder = new StringBuilder();
 
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
 
     public static LocalStaticScope decodeRootScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         LocalStaticScope scope = new LocalStaticScope(null, decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     public static LocalStaticScope decodeLocalScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         LocalStaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     public static BlockStaticScope decodeBlockScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         BlockStaticScope scope = new BlockStaticScope(context.getCurrentScope().getStaticScope(), decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     private static String[][] decodeScopeDescriptor(String scopeString) {
         String[] scopeElements = scopeString.split(",");
         String[] scopeNames = scopeElements[0].length() == 0 ? new String[0] : getScopeNames(scopeElements[0]);
         return new String[][] {scopeElements, scopeNames};
     }
 
     private static void setAritiesFromDecodedScope(StaticScope scope, String[] scopeElements) {
         scope.setArities(Integer.parseInt(scopeElements[1]), Integer.parseInt(scopeElements[2]), Integer.parseInt(scopeElements[3]));
     }
 
     private static StaticScope createScopeForClass(ThreadContext context, String scopeString) {
         StaticScope scope = decodeLocalScope(context, scopeString);
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
         IRubyObject result = self.getInstanceVariables().fastGetInstanceVariable(internedName);
         if (result != null) return result;
         if (runtime.isVerbose()) warnAboutUninitializedIvar(runtime, internedName);
         return runtime.getNil();
     }
 
     private static void warnAboutUninitializedIvar(Ruby runtime, String internedName) {
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + internedName + " not initialized");
     }
 
     public static IRubyObject setInstanceVariable(IRubyObject value, IRubyObject self, String name) {
         return self.getInstanceVariables().fastSetInstanceVariable(name, value);
     }
 
     public static RubyProc newLiteralLambda(ThreadContext context, Block block, IRubyObject self) {
         return RubyProc.newProc(context.getRuntime(), block, Block.Type.LAMBDA);
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
         Ruby runtime = context.getRuntime();
         if (a == b) return runtime.getTrue();
         IRubyObject res = invokedynamic(context, a, OP_EQUAL, b);
         return runtime.newBoolean(res.isTrue());
     }
 
     public static void traceLine(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.getRuntime().callEventHooks(context, RubyEvent.LINE, context.getFile(), context.getLine(), name, type);
     }
 
     public static void traceClass(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.getRuntime().callEventHooks(context, RubyEvent.CLASS, context.getFile(), context.getLine(), name, type);
     }
 
     public static void traceEnd(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.getRuntime().callEventHooks(context, RubyEvent.END, context.getFile(), context.getLine(), name, type);
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
         throw context.getRuntime().newArgumentError(length, expected);
     }
 
     public static boolean isModuleAndHasConstant(IRubyObject left, String name) {
         return left instanceof RubyModule && ((RubyModule) left).fastGetConstantFromNoConstMissing(name) != null;
     }
 
     public static ByteList getDefinedConstantOrBoundMethod(IRubyObject left, String name) {
         if (isModuleAndHasConstant(left, name)) return Node.CONSTANT_BYTELIST;
         if (left.getMetaClass().isMethodBound(name, true)) return Node.METHOD_BYTELIST;
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
         return metaClass.getCacheToken() == generation;
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
         updateScopeWithCaptures(context, scope, decodeCaptureOffsets(scopeOffsets), value);
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
         }
 
         if (argsNode.getRestArg() >= 0) {
             if (added) builder.append(';');
             added = true;
             if (argsNode.getRestArgNode() instanceof UnnamedRestArgNode) {
                 if (((UnnamedRestArgNode) argsNode.getRestArgNode()).isStar()) builder.append("R");
             } else {
                 builder.append("r").append(argsNode.getRestArgNode().getName());
             }
         }
 
         if (argsNode.getPost() != null) {
             for (Node postNode : argsNode.getPost().childNodes()) {
                 if (added) builder.append(';');
                 added = true;
                 if (postNode instanceof MultipleAsgn19Node) {
                     builder.append("nil");
                 } else {
                     builder.append("q").append(((ArgumentNode)postNode).getName());
                 }
             }
         }
 
         if (argsNode.getBlock() != null) {
             if (added) builder.append(';');
             added = true;
             builder.append("b").append(argsNode.getBlock().getName());
         }
 
         if (!added) builder.append("NONE");
 
         return builder.toString();
     }
 
     public static RubyArray parameterListToParameters(Ruby runtime, String[] parameterList, boolean isLambda) {
         RubyArray parms = RubyArray.newEmptyArray(runtime);
 
         for (String param : parameterList) {
             if (param.equals("NONE")) break;
 
             RubyArray elem = RubyArray.newEmptyArray(runtime);
             if (param.equals("nil")) {
                 // marker for masgn args (the parens in "a, b, (c, d)"
                 elem.add(RubySymbol.newSymbol(runtime, isLambda ? "req" : "opt"));
                 parms.add(elem);
                 continue;
             }
 
             if (param.charAt(0) == 'q') {
                 // required/normal arg
                 elem.add(RubySymbol.newSymbol(runtime, isLambda ? "req" : "opt"));
             } else if (param.charAt(0) == 'r') {
                 // named rest arg
                 elem.add(RubySymbol.newSymbol(runtime, "rest"));
             } else if (param.charAt(0) == 'R') {
                 // unnamed rest arg (star)
                 elem.add(RubySymbol.newSymbol(runtime, "rest"));
                 parms.add(elem);
                 continue;
             } else if (param.charAt(0) == 'o') {
                 // optional arg
                 elem.add(RubySymbol.newSymbol(runtime, "opt"));
                 if (param.length() == 1) {
                     // no name; continue
                     parms.add(elem);
                     continue;
                 }
             } else if (param.charAt(0) == 'b') {
                 // block arg
                 elem.add(RubySymbol.newSymbol(runtime, "block"));
             }
