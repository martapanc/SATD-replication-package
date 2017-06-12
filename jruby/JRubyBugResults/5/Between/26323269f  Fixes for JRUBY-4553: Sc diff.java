diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 68a24e6e57..b861225437 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,3251 +1,3233 @@
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
 import java.io.DataInputStream;
 import java.io.File;
 import java.util.EnumSet;
 import java.util.concurrent.atomic.AtomicLong;
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
-        byte[] bytes;
-        
-        try {
-            bytes = script.getBytes(KCode.NONE.getKCode());
-        } catch (UnsupportedEncodingException e) {
-            bytes = script.getBytes();
-        }
+        byte[] bytes = RubyEncoding.encode(script, KCode.NONE.getCharset());
 
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
     
     public Script tryCompile(Node node) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()));
     }
     
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader) {
         return tryCompile(node, cachedClassName, classLoader, false);
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, boolean dump) {
         Script script = null;
         try {
             String filename = node.getPosition().getFile();
             String classname = JavaNameMangler.mangledFilenameForStartupClasspath(filename);
 
             ASTInspector inspector = new ASTInspector();
             inspector.inspect(node);
 
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
 
         // Modified for JRUBY-4484
         // prepare a temporary toplevel frame during init, for consumers like TOPLEVEL_BINDING
         tc.preTopLevel(objectClass, topSelf);
 
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
 
         // Modified for JRUBY-4484
         // remove the temporary toplevel frame
         tc.postTopLevel();
         
         // Require in all libraries specified on command line
         for (String scriptName : config.requiredLibraries()) {
             loadService.smartLoad(scriptName);
         }
 
         // Modified for JRUBY-4484
         // Construct the final toplevel frame and scope for the main thread
         tc.preTopLevel(objectClass, topSelf);
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
             }
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
                 for (Errno e : Errno.values()) {
                     Constant c = (Constant) e;
                     if (Character.isUpperCase(c.name().charAt(0))) {
                         createSysErr(c.value(), c.name());
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
             RubyClass errno = getErrno().defineClassUnder(name, systemCallError, systemCallError.getAllocator());
             errnos.put(i, errno);
             errno.defineConstant("Errno", newFixnum(i));
         }
     }
 
     private void initBuiltins() {
         addLazyBuiltin("java.rb", "java", "org.jruby.javasupport.Java");
         addLazyBuiltin("jruby.rb", "jruby", "org.jruby.libraries.JRubyLibrary");
         addLazyBuiltin("jruby/ext.rb", "jruby/ext", "org.jruby.RubyJRuby$ExtLibrary");
         addLazyBuiltin("jruby/util.rb", "jruby/util", "org.jruby.RubyJRuby$UtilLibrary");
         addLazyBuiltin("jruby/core_ext.rb", "jruby/core_ext", "org.jruby.RubyJRuby$CoreExtLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.RubyJRuby$TypeLibrary");
         addLazyBuiltin("iconv.jar", "iconv", "org.jruby.libraries.IConvLibrary");
         addLazyBuiltin("nkf.jar", "nkf", "org.jruby.libraries.NKFLibrary");
         addLazyBuiltin("stringio.jar", "stringio", "org.jruby.libraries.StringIOLibrary");
         addLazyBuiltin("strscan.jar", "strscan", "org.jruby.libraries.StringScannerLibrary");
         addLazyBuiltin("zlib.jar", "zlib", "org.jruby.libraries.ZlibLibrary");
         addLazyBuiltin("enumerator.jar", "enumerator", "org.jruby.libraries.EnumeratorLibrary");
         addLazyBuiltin("readline.jar", "readline", "org.jruby.ext.Readline$Service");
         addLazyBuiltin("thread.jar", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("thread.rb", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("digest.jar", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest.rb", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest/md5.jar", "digest/md5", "org.jruby.libraries.DigestLibrary$MD5");
         addLazyBuiltin("digest/rmd160.jar", "digest/rmd160", "org.jruby.libraries.DigestLibrary$RMD160");
         addLazyBuiltin("digest/sha1.jar", "digest/sha1", "org.jruby.libraries.DigestLibrary$SHA1");
         addLazyBuiltin("digest/sha2.jar", "digest/sha2", "org.jruby.libraries.DigestLibrary$SHA2");
         addLazyBuiltin("bigdecimal.jar", "bigdecimal", "org.jruby.libraries.BigDecimalLibrary");
         addLazyBuiltin("io/wait.jar", "io/wait", "org.jruby.libraries.IOWaitLibrary");
         addLazyBuiltin("etc.jar", "etc", "org.jruby.libraries.EtcLibrary");
         addLazyBuiltin("weakref.rb", "weakref", "org.jruby.ext.WeakRef$WeakRefLibrary");
         addLazyBuiltin("timeout.rb", "timeout", "org.jruby.ext.Timeout");
         addLazyBuiltin("socket.jar", "socket", "org.jruby.ext.socket.RubySocket$Service");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.libraries.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.libraries.JRubySerializationLibrary");
         addLazyBuiltin("ffi-internal.jar", "ffi-internal", "org.jruby.ext.ffi.Factory$Service");
         addLazyBuiltin("tempfile.rb", "tempfile", "org.jruby.libraries.TempfileLibrary");
         addLazyBuiltin("fcntl.rb", "fcntl", "org.jruby.libraries.FcntlLibrary");
         if (is1_9()) {
             addLazyBuiltin("mathn/complex.jar", "mathn/complex", "org.jruby.ext.mathn.Complex");
             addLazyBuiltin("mathn/rational.jar", "mathn/rational", "org.jruby.ext.mathn.Rational");
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
         
         String[] builtins = {"yaml", 
                              "yaml/yecht", "yaml/baseemitter", "yaml/basenode", 
                              "yaml/compat", "yaml/constants", "yaml/dbm", 
                              "yaml/emitter", "yaml/encoding", "yaml/error", 
                              "yaml/rubytypes", "yaml/store", "yaml/stream", 
                              "yaml/stringio", "yaml/tag", "yaml/types", 
                              "yaml/yamlnode", "yaml/ypath", 
                              "jsignal", "generator", "prelude"};
         for (String library : builtins) {
             addBuiltinIfAllowed(library + ".rb", new BuiltinScript(library));
         }
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
 
         if(is1_9()) {
             // see ruby.c's ruby_init_gems function
             defineModule("Gem"); // dummy Gem module for prelude
             getLoadService().require("builtin/prelude.rb");
             getLoadService().require("builtin/gem_prelude.rb");
         }
 
         getLoadService().require("builtin/core_ext/symbol");
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
 
     public RubyClass getEncodingError() {
         return encodingError;
     }
 
     public RubyClass getEncodingCompatibilityError() {
         return encodingCompatibilityError;
     }
 
     public RubyClass getConverterNotFoundError() {
         return converterNotFoundError;
     }
 
     public RubyClass getUndefinedConversionError() {
         return undefinedConversionError;
     }
 
     public RubyClass getInvalidByteSequenceError() {
         return invalidByteSequenceError;
     }
 
     public RubyClass getRandomClass() {
         return randomClass;
     }
 
     public void setRandomClass(RubyClass randomClass) {
         this.randomClass = randomClass;
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
 
     public boolean isVerbose() {
         return isVerbose;
     }
 
     public boolean warningsEnabled() {
         return warningsEnabled;
     }
 
     /** Setter for property isVerbose.
      * @param verbose New value of property isVerbose.
      */
     public void setVerbose(IRubyObject verbose) {
         this.verbose = verbose;
         isVerbose = verbose.isTrue();
         warningsEnabled = !verbose.isNil();
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
 
     public Node parseFile(InputStream in, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addLoadParse();
         return parser.parse(file, in, scope, new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, in, scope, new ParserConfiguration(getKCode(), 0, false, true, false, config));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
-        byte[] bytes;
-        
-        try {
-            bytes = content.getBytes(KCode.NONE.getKCode());
-        } catch (UnsupportedEncodingException e) {
-            bytes = content.getBytes();
-        }
+        byte[] bytes = RubyEncoding.encode(content, KCode.NONE.getCharset());
         
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, bytes, scope,
                 new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
-        byte[] bytes;
-        
-        try {
-            bytes = content.getBytes(KCode.NONE.getKCode());
-        } catch (UnsupportedEncodingException e) {
-            bytes = content.getBytes();
-        }
+        byte[] bytes = RubyEncoding.encode(content, KCode.NONE.getCharset());
 
         return parser.parse(file, bytes, scope,
                 new ParserConfiguration(getKCode(), lineNumber, extraPositionInformation, false, true, config));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
         return parser.parse(file, content, scope, 
                 new ParserConfiguration(getKCode(), lineNumber, extraPositionInformation, false, true, config));
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
 
         if (RubyException.TRACE_TYPE == RubyException.RUBINIUS) {
             printRubiniusTrace(excp);
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
 
     private void printRubiniusTrace(RubyException exception) {
 
         ThreadContext.RubyStackTraceElement[] frames = exception.getBacktraceFrames();
 
         ArrayList firstParts = new ArrayList();
         int longestFirstPart = 0;
         for (ThreadContext.RubyStackTraceElement frame : frames) {
             String firstPart = frame.getClassName() + "#" + frame.getMethodName();
             if (firstPart.length() > longestFirstPart) longestFirstPart = firstPart.length();
             firstParts.add(firstPart);
         }
 
         // determine spacing
         int center = longestFirstPart
                 + 2 // initial spaces
                 + 1; // spaces before "at"
 
         StringBuffer buffer = new StringBuffer();
 
         buffer
                 .append("An exception has occurred:\n")
                 .append("    ");
 
         if (exception.getMetaClass() == getRuntimeError() && exception.message(getCurrentContext()).toString().length() == 0) {
             buffer.append("No current exception (RuntimeError)");
         } else {
             buffer.append(exception.message(getCurrentContext()).toString());
         }
 
         buffer
                 .append('\n')
                 .append('\n')
                 .append("Backtrace:\n");
 
         int i = 0;
         for (ThreadContext.RubyStackTraceElement frame : frames) {
             String firstPart = (String)firstParts.get(i);
             String secondPart = frame.getFileName() + ":" + frame.getLineNumber();
             
             buffer.append("  ");
             for (int j = 0; j < center - firstPart.length(); j++) {
                 buffer.append(' ');
             }
             buffer.append(firstPart);
             buffer.append(" at ");
             buffer.append(secondPart);
             buffer.append('\n');
             i++;
         }
 
         PrintStream errorStream = getErrorStream();
         errorStream.print(buffer.toString());
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
         InputStream readStream = in;
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(filename);
             context.preNodeEval(objectClass, self, filename);
 
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
                 className = "ruby.jit.FILE_" + hash;
 
                 // FIXME: duplicated from ClassCache
                 Class contents;
                 try {
                     contents = jrubyClassLoader.loadClass(className);
                     if (JITCompiler.DEBUG) {
                         System.err.println("found jitted code in classloader: " + className);
                     }
                     script = (Script)contents.newInstance();
                     readStream = new ByteArrayInputStream(buffer);
                 } catch (ClassNotFoundException cnfe) {
                     if (JITCompiler.DEBUG) {
                         System.err.println("no jitted code in classloader for file " + filename + " at class: " + className);
                     }
                 } catch (InstantiationException ie) {
                     if (JITCompiler.DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 } catch (IllegalAccessException iae) {
                     if (JITCompiler.DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 }
             } catch (IOException ioe) {
                 // TODO: log something?
             }
 
             // script was not found in cache above, so proceed to compile
             if (script == null) {
                 Node scriptNode = parseFile(readStream, filename, null);
 
                 script = tryCompile(scriptNode, className, new JRubyClassLoader(jrubyClassLoader));
             }
             
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
                         f.finalize();
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
                         f.finalize();
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
         return newRaiseException(getArgumentError(), "wrong # of arguments(" + got + " for " + expected + ")");
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
                 this, getNameError(), message, name), true);
     }
 
     public RaiseException newLocalJumpError(RubyLocalJumpError.Reason reason, IRubyObject exitValue, String message) {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), message, reason, exitValue), true);
     }
 
     public RaiseException newLocalJumpErrorNoBlock() {
         return newLocalJumpError(RubyLocalJumpError.Reason.NOREASON, getNil(), "no block given");
     }
 
     public RaiseException newRedoLocalJumpError() {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), "unexpected redo", RubyLocalJumpError.Reason.REDO, getNil()), true);
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(getLoadError(), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(is1_9() ? getRuntimeError() : getTypeError(), "can't modify frozen " + objectType);
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
 
     public RaiseException newEncodingError(String message){
         return newRaiseException(getEncodingError(), message);
     }
 
     public RaiseException newEncodingCompatibilityError(String message){
         return newRaiseException(getEncodingCompatibilityError(), message);
diff --git a/src/org/jruby/RubyEncoding.java b/src/org/jruby/RubyEncoding.java
index 706342f20e..87df25f1f6 100644
--- a/src/org/jruby/RubyEncoding.java
+++ b/src/org/jruby/RubyEncoding.java
@@ -1,350 +1,392 @@
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
 
+import java.nio.ByteBuffer;
+import java.nio.CharBuffer;
 import java.nio.charset.Charset;
+import java.nio.charset.CharsetEncoder;
 import org.jcodings.Encoding;
 import org.jcodings.EncodingDB.Entry;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jcodings.util.CaseInsensitiveBytesHash;
 import org.jcodings.util.Hash.HashEntryIterator;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.runtime.encoding.EncodingService;
 import org.jruby.util.ByteList;
 import org.jruby.util.StringSupport;
 
 @JRubyClass(name="Encoding")
 public class RubyEncoding extends RubyObject {
+    public static final Charset UTF8 = Charset.forName("UTF8");
 
     public static RubyClass createEncodingClass(Ruby runtime) {
         RubyClass encodingc = runtime.defineClass("Encoding", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setEncoding(encodingc);
         encodingc.index = ClassIndex.ENCODING;
         encodingc.setReifiedClass(RubyEncoding.class);
         encodingc.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyEncoding;
             }
         };
 
         encodingc.getSingletonClass().undefineMethod("allocate");
         encodingc.defineAnnotatedMethods(RubyEncoding.class);
 
         return encodingc;
     }
 
     private Encoding encoding;
     private final ByteList name;
     private final boolean isDummy;
 
     private RubyEncoding(Ruby runtime, byte[]name, int p, int end, boolean isDummy) {
         super(runtime, runtime.getEncoding());
         this.name = new ByteList(name, p, end);
         this.isDummy = isDummy;
     }
     
     private RubyEncoding(Ruby runtime, byte[]name, boolean isDummy) {
         this(runtime, name, 0, name.length, isDummy);
     }
 
     private RubyEncoding(Ruby runtime, Encoding encoding) {
         super(runtime, runtime.getEncoding());
         this.name = new ByteList(encoding.getName());
         this.isDummy = false;
         this.encoding = encoding;
     }
 
     public static RubyEncoding newEncoding(Ruby runtime, byte[]name, int p, int end, boolean isDummy) {
         return new RubyEncoding(runtime, name, p, end, isDummy);
     }
 
     public static RubyEncoding newEncoding(Ruby runtime, byte[]name, boolean isDummy) {
         return new RubyEncoding(runtime, name, isDummy);
     }
 
     public final Encoding getEncoding() {
         // TODO: make threadsafe
         if (encoding == null) encoding = getRuntime().getEncodingService().loadEncoding(name);
         return encoding;
     }
 
     public static final Encoding areCompatible(IRubyObject obj1, IRubyObject obj2) {
         if (obj1 instanceof EncodingCapable && obj2 instanceof EncodingCapable) {
             Encoding enc1 = ((EncodingCapable)obj1).getEncoding();
             Encoding enc2 = ((EncodingCapable)obj2).getEncoding();
             if (enc1 == enc2) return enc1;
 
             if (obj2 instanceof RubyString && ((RubyString) obj2).getByteList().getRealSize() == 0) return enc1;
             if (obj1 instanceof RubyString && ((RubyString) obj1).getByteList().getRealSize() == 0) return enc2;
 
             if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
 
             if (!(obj2 instanceof RubyString) && enc2 instanceof USASCIIEncoding) return enc1;
             if (!(obj1 instanceof RubyString) && enc1 instanceof USASCIIEncoding) return enc2;
 
             if(!(obj1 instanceof RubyString)) {
                 IRubyObject objTmp = obj1;
                 obj1 = obj2;
                 obj1 = objTmp;
 
                 Encoding encTmp = enc1;
                 enc1 = enc2;
                 enc2 = encTmp;
             }
 
             if (obj1 instanceof RubyString) {
                 int cr1 = ((RubyString)obj1).scanForCodeRange();
                 if (obj2 instanceof RubyString) {
                     int cr2 = ((RubyString)obj2).scanForCodeRange();
                     return areCompatible(enc1, cr1, enc2, cr2);
                 }
                 if (cr1 == StringSupport.CR_7BIT) return enc2;
             }
         }
         return null;
     }
 
     static Encoding areCompatible(Encoding enc1, int cr1, Encoding enc2, int cr2) {
         if (cr1 != cr2) {
             /* may need to handle ENC_CODERANGE_BROKEN */
             if (cr1 == StringSupport.CR_7BIT) return enc2;
             if (cr2 == StringSupport.CR_7BIT) return enc1;
         }
         if (cr2 == StringSupport.CR_7BIT) {
             if (enc1 instanceof ASCIIEncoding) return enc2;
             return enc1;
         }
         if (cr1 == StringSupport.CR_7BIT) return enc2;
         return null;
     }
 
+    public static byte[] encodeUTF8(CharSequence cs) {
+        return encode(cs, UTF8);
+    }
+
+    public static byte[] encodeUTF8(String str) {
+        return encode(str, UTF8);
+    }
+
+    public static byte[] encode(CharSequence cs, Charset charset) {
+        ByteBuffer buffer = charset.encode(cs.toString());
+        byte[] bytes = new byte[buffer.limit()];
+        buffer.get(bytes);
+        return bytes;
+    }
+
+    public static byte[] encode(String str, Charset charset) {
+        ByteBuffer buffer = charset.encode(str);
+        byte[] bytes = new byte[buffer.limit()];
+        buffer.get(bytes);
+        return bytes;
+    }
+
+    public static String decodeUTF8(byte[] bytes, int start, int length) {
+        return decode(bytes, start, length, UTF8);
+    }
+
+    public static String decodeUTF8(byte[] bytes) {
+        return decode(bytes, UTF8);
+    }
+
+    public static String decode(byte[] bytes, int start, int length, Charset charset) {
+        return charset.decode(ByteBuffer.wrap(bytes, start, length)).toString();
+    }
+
+    public static String decode(byte[] bytes, Charset charset) {
+        return charset.decode(ByteBuffer.wrap(bytes)).toString();
+    }
+
     @JRubyMethod(name = "list", meta = true)
     public static IRubyObject list(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         return RubyArray.newArrayNoCopy(runtime, runtime.getEncodingService().getEncodingList(), 0);
     }
 
     @JRubyMethod(name = "locale_charmap", meta = true)
     public static IRubyObject locale_charmap(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         EncodingService service = runtime.getEncodingService();
         Entry entry = service.findEncodingOrAliasEntry(new ByteList(Charset.defaultCharset().name().getBytes()));
         return RubyString.newUsAsciiStringNoCopy(runtime, new ByteList(entry.getEncoding().getName()));
     }
 
     @SuppressWarnings("unchecked")
     @JRubyMethod(name = "name_list", meta = true)
     public static IRubyObject name_list(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         EncodingService service = runtime.getEncodingService();
         
         RubyArray result = runtime.newArray(service.getEncodings().size() + service.getAliases().size());
         HashEntryIterator i;
         i = service.getEncodings().entryIterator();
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
         }
         i = service.getAliases().entryIterator();        
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
         }
         return result;
     }
 
     @SuppressWarnings("unchecked")
     @JRubyMethod(name = "aliases", meta = true)
     public static IRubyObject aliases(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         EncodingService service = runtime.getEncodingService();
 
         IRubyObject list[] = service.getEncodingList();
         HashEntryIterator i = service.getAliases().entryIterator();
         RubyHash result = RubyHash.newHash(runtime);
 
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             IRubyObject alias = RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context);
             IRubyObject name = RubyString.newUsAsciiStringShared(runtime, 
                                 ((RubyEncoding)list[e.value.getIndex()]).name).freeze(context);
             result.fastASet(alias, name);
         }
         return result;
     }
 
     @JRubyMethod(name = "find", meta = true)
     public static IRubyObject find(ThreadContext context, IRubyObject recv, IRubyObject str) {
         Ruby runtime = context.getRuntime();
         EncodingService service = runtime.getEncodingService();
         // TODO: check for ascii string
         ByteList name = str.convertToString().getByteList();
         Entry e = service.findEncodingOrAliasEntry(name);
 
         if (e == null) throw context.getRuntime().newArgumentError("unknown encoding name - " + name);
 
         return service.getEncodingList()[e.getIndex()];
     }
 
     @JRubyMethod(name = "_dump")
     public IRubyObject _dump(ThreadContext context) {
         return to_s(context);
     }
 
     @JRubyMethod(name = "_load", meta = true)
     public static IRubyObject _load(ThreadContext context, IRubyObject recv, IRubyObject str) {
         return find(context, recv, str);
     }
 
     @JRubyMethod(name = {"to_s", "name"})
     public IRubyObject to_s(ThreadContext context) {
         // TODO: rb_usascii_str_new2
         return RubyString.newUsAsciiStringShared(context.getRuntime(), name);
     }
 
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect(ThreadContext context) {
         ByteList bytes = new ByteList();
         bytes.append("#<Encoding:".getBytes());
         bytes.append(name);
         if (isDummy) bytes.append(" (dummy)".getBytes());
         bytes.append('>');
         return RubyString.newUsAsciiStringNoCopy(context.getRuntime(), bytes);
     }
 
     @SuppressWarnings("unchecked")
     @JRubyMethod(name = "names")
     public IRubyObject names(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         EncodingService service = runtime.getEncodingService();
         Entry entry = service.findEncodingOrAliasEntry(name);
 
         RubyArray result = runtime.newArray();
         HashEntryIterator i;
         i = service.getEncodings().entryIterator();
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             if (e.value == entry) {
                 result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
             }
         }
         i = service.getAliases().entryIterator();        
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             if (e.value == entry) {
                 result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
             }
         }
         return result;
     }
 
     @JRubyMethod(name = "dummy?")
     public IRubyObject dummy_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isDummy);
     }
 
     @JRubyMethod(name = "compatible?", meta = true)
     public static IRubyObject compatible_p(ThreadContext context, IRubyObject self, IRubyObject first, IRubyObject second) {
         Ruby runtime = context.getRuntime();
         Encoding enc = areCompatible(first, second);
 
         return enc == null ? runtime.getNil() : new RubyEncoding(runtime, enc);
     }
 
     @JRubyMethod(name = "default_external", meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject getDefaultExternal(IRubyObject recv) {
         return getDefaultExternal(recv.getRuntime());
     }
 
     public static IRubyObject getDefaultExternal(Ruby runtime) {
         IRubyObject defaultExternal = convertEncodingToRubyEncoding(runtime, runtime.getDefaultExternalEncoding());
 
         if (defaultExternal.isNil()) {
             ByteList encodingName = ByteList.create(Charset.defaultCharset().name());
             Encoding encoding = runtime.getEncodingService().loadEncoding(encodingName);
 
             runtime.setDefaultExternalEncoding(encoding);
             defaultExternal = convertEncodingToRubyEncoding(runtime, encoding);
         }
 
         return defaultExternal;
     }
 
     @JRubyMethod(name = "default_external=", required = 1, frame = true, meta = true, compat = CompatVersion.RUBY1_9)
     public static void setDefaultExternal(IRubyObject recv, IRubyObject encoding) {
         if (encoding.isNil()) {
             recv.getRuntime().newArgumentError("default_external can not be nil");
         }
         recv.getRuntime().setDefaultExternalEncoding(getEncodingFromObject(recv.getRuntime(), encoding));
     }
 
     @JRubyMethod(name = "default_internal", meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject getDefaultInternal(IRubyObject recv) {
         return getDefaultInternal(recv.getRuntime());
     }
 
     public static IRubyObject getDefaultInternal(Ruby runtime) {
         return convertEncodingToRubyEncoding(runtime, runtime.getDefaultInternalEncoding());
     }
 
     @JRubyMethod(name = "default_internal=", required = 1, frame = true, meta = true, compat = CompatVersion.RUBY1_9)
     public static void setDefaultInternal(IRubyObject recv, IRubyObject encoding) {
         if (encoding.isNil()) {
             recv.getRuntime().newArgumentError("default_internal can not be nil");
         }
         recv.getRuntime().setDefaultInternalEncoding(getEncodingFromObject(recv.getRuntime(), encoding));
     }
 
     public static IRubyObject convertEncodingToRubyEncoding(Ruby runtime, Encoding defaultEncoding) {
         if (defaultEncoding != null) {
             return new RubyEncoding(runtime, defaultEncoding);
         }
         return runtime.getNil();
     }
 
     public static Encoding getEncodingFromObject(Ruby runtime, IRubyObject arg) {
         Encoding encoding = null;
         if (arg instanceof RubyEncoding) {
             encoding = ((RubyEncoding) arg).getEncoding();
         } else if (!arg.isNil()) {
             encoding = arg.convertToString().toEncoding(runtime);
         }
         return encoding;
     }
 }
diff --git a/src/org/jruby/RubyNKF.java b/src/org/jruby/RubyNKF.java
index 8f63ea4870..f63496cc4f 100644
--- a/src/org/jruby/RubyNKF.java
+++ b/src/org/jruby/RubyNKF.java
@@ -1,438 +1,439 @@
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
  * Copyright (C) 2007-2010 Koichiro Ohba <koichiro@meadowy.org>
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
 
 import java.nio.ByteBuffer;
 import java.nio.CharBuffer;
 import java.nio.charset.CharacterCodingException;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetDecoder;
 import java.nio.charset.CharsetEncoder;
 import java.nio.charset.UnsupportedCharsetException;
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.Pack;
 
 @JRubyModule(name="NKF")
 public class RubyNKF {
     public static final NKFCharset AUTO = new NKFCharset(0, "x-JISAutoDetect");
     public static final NKFCharset JIS = new NKFCharset(1, "iso-2022-jp");
     public static final NKFCharset EUC = new NKFCharset(2, "EUC-JP");
     public static final NKFCharset SJIS = new NKFCharset(3, "Windows-31J");
     public static final NKFCharset BINARY = new NKFCharset(4, null);
     public static final NKFCharset NOCONV = new NKFCharset(4, null);
     public static final NKFCharset UNKNOWN = new NKFCharset(0, null);
     public static final NKFCharset ASCII = new NKFCharset(5, "iso-8859-1");
     public static final NKFCharset UTF8 = new NKFCharset(6, "UTF-8");
     public static final NKFCharset UTF16 = new NKFCharset(8, "UTF-16");
     public static final NKFCharset UTF32 = new NKFCharset(12, "UTF-32");
     public static final NKFCharset OTHER = new NKFCharset(16, null);
     public static final NKFCharset BASE64 = new NKFCharset(20, "base64");
     public static final NKFCharset QENCODE = new NKFCharset(21, "qencode");
     public static final NKFCharset MIME_DETECT = new NKFCharset(22, "MimeAutoDetect");
 
     private static final ByteList BEGIN_MIME_STRING = new ByteList(ByteList.plain("=?"));
     private static final ByteList END_MIME_STRING = new ByteList(ByteList.plain("?="));
     private static final ByteList MIME_ASCII = new ByteList(ByteList.plain(ASCII.getCharset()));
     private static final ByteList MIME_UTF8 = new ByteList(ByteList.plain(UTF8.getCharset()));
     private static final ByteList MIME_JIS = new ByteList(ByteList.plain(JIS.getCharset()));
     private static final ByteList MIME_EUC_JP = new ByteList(ByteList.plain(EUC.getCharset()));
 
     public static class NKFCharset {
         private final int value;
         private final String charset;
 
         public NKFCharset(int v, String c) {
             value = v;
             charset = c;
         }
         
         public int getValue() {
             return value;
         }
         
         public String getCharset() {
             return charset;
         }
     }
 
     public static void createNKF(Ruby runtime) {
         RubyModule nkfModule = runtime.defineModule("NKF");
 
         nkfModule.defineConstant("AUTO", RubyFixnum.newFixnum(runtime, AUTO.getValue()));
         nkfModule.defineConstant("JIS", RubyFixnum.newFixnum(runtime, JIS.getValue()));
         nkfModule.defineConstant("EUC", RubyFixnum.newFixnum(runtime, EUC.getValue()));
         nkfModule.defineConstant("SJIS", RubyFixnum.newFixnum(runtime, SJIS.getValue()));
         nkfModule.defineConstant("BINARY", RubyFixnum.newFixnum(runtime, BINARY.getValue()));
         nkfModule.defineConstant("NOCONV", RubyFixnum.newFixnum(runtime, NOCONV.getValue()));
         nkfModule.defineConstant("UNKNOWN", RubyFixnum.newFixnum(runtime, UNKNOWN.getValue()));
         nkfModule.defineConstant("ASCII", RubyFixnum.newFixnum(runtime, ASCII.getValue()));
         nkfModule.defineConstant("UTF8", RubyFixnum.newFixnum(runtime, UTF8.getValue()));
         nkfModule.defineConstant("UTF16", RubyFixnum.newFixnum(runtime, UTF16.getValue()));
         nkfModule.defineConstant("UTF32", RubyFixnum.newFixnum(runtime, UTF32.getValue()));
         nkfModule.defineConstant("OTHER", RubyFixnum.newFixnum(runtime, OTHER.getValue()));
         
         RubyString version = runtime.newString("2.0.7 (JRuby 2007-05-11)");
         RubyString nkfVersion = runtime.newString("2.0.7");
         RubyString nkfDate = runtime.newString("2007-05-11");
 
         ThreadContext context = runtime.getCurrentContext();
         
         version.freeze(context);
         nkfVersion.freeze(context);
         nkfDate.freeze(context);
 
         nkfModule.defineAnnotatedMethods(RubyNKF.class);
     }
 
     @JRubyMethod(name = "guess", required = 1, module = true)
     public static IRubyObject guess(ThreadContext context, IRubyObject recv, IRubyObject s) {
+        // TODO: Fix charset usage for JRUBY-4553
         Ruby runtime = context.getRuntime();
         if (!s.respondsTo("to_str")) {
             throw runtime.newTypeError("can't convert " + s.getMetaClass() + " into String");
         }
         ByteList bytes = s.convertToString().getByteList();
         ByteBuffer buf = ByteBuffer.wrap(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
         CharsetDecoder decoder;
         try {
             decoder = Charset.forName("x-JISAutoDetect").newDecoder();
         } catch (UnsupportedCharsetException e) {
             throw runtime.newStandardError("charsets.jar is required to use NKF#guess. Please install JRE which supports m17n.");
         }
         try {
             decoder.decode(buf);
         } catch (CharacterCodingException e) {
             return runtime.newFixnum(UNKNOWN.getValue());
         }
         if (!decoder.isCharsetDetected()) {
             return runtime.newFixnum(UNKNOWN.getValue());
         }
         Charset charset = decoder.detectedCharset();
         String name = charset.name();
 //        System.out.println("detect: " + name + "\n");
         if ("Shift_JIS".equals(name))
             return runtime.newFixnum(SJIS.getValue());
         if ("windows-31j".equals(name))
             return runtime.newFixnum(SJIS.getValue());
         else if ("EUC-JP".equals(name))
             return runtime.newFixnum(EUC.getValue());
         else if ("ISO-2022-JP".equals(name))
             return runtime.newFixnum(JIS.getValue());
         else
             return runtime.newFixnum(UNKNOWN.getValue());
     }
     
     @JRubyMethod(name = "guess1", required = 1, module = true)
     public static IRubyObject guess1(ThreadContext context, IRubyObject recv, IRubyObject str) {
         return guess(context, recv, str);
     }
     
     @JRubyMethod(name = "guess2", required = 1, module = true)
     public static IRubyObject guess2(ThreadContext context, IRubyObject recv, IRubyObject str) {
         return guess(context, recv, str);
     }
     
     @JRubyMethod(name = "nkf", required = 2, module = true)
     public static IRubyObject nkf(ThreadContext context, IRubyObject recv, IRubyObject opt, IRubyObject str) {
         Ruby runtime = context.getRuntime();
         
         if (!opt.respondsTo("to_str")) {
             throw runtime.newTypeError("can't convert " + opt.getMetaClass() + " into String");
         }
         
         if (!str.respondsTo("to_str")) {
             throw runtime.newTypeError("can't convert " + str.getMetaClass() + " into String");
         }
         
         Map<String, NKFCharset> options = parseOpt(opt.convertToString().toString());
         
         if (options.get("input").getValue() == AUTO.getValue()) {
             KCode kcode = runtime.getKCode();
             if (kcode == KCode.SJIS) {
                 options.put("input", SJIS);
             } else if (kcode == KCode.EUC) {
                 options.put("input", EUC);
             } else if (kcode == KCode.UTF8) {
                 options.put("input", UTF8);
             }
         }
 
         ByteList mimeString = str.convertToString().getByteList();
         ByteList mimeText = null;
         NKFCharset mimeState = detectMimeString(mimeString, options);
         if (mimeState == NOCONV) {
             mimeText = mimeString;
         } else {
             mimeText = getMimeText(mimeString);
             RubyArray array = null;
             if (mimeState == BASE64) {
                 array = Pack.unpack(runtime, mimeText, new ByteList(ByteList.plain("m")));
             } else if (mimeState == QENCODE) {
                 array = Pack.unpack(runtime, mimeText, new ByteList(ByteList.plain("M")));
             }
             RubyString s = (RubyString) array.entry(0);
             mimeText = s.asString().getByteList();
         }
 
         String decodeCharset = options.get("input").getCharset();
         String encodeCharset = options.get("output").getCharset();
 
         RubyString result = convert(context, decodeCharset, encodeCharset, mimeText);
 
         if (options.get("mime-encode") == BASE64) {
             result = encodeMimeString(runtime, result, "m"); // BASE64
         } else if (options.get("mime-encode") == QENCODE) {
             result = encodeMimeString(runtime, result, "M"); // quoted-printable
         }
 
         return result;
     }
     
     private static RubyString convert(ThreadContext context, String decodeCharset,
             String encodeCharset, ByteList str) {
         Ruby runtime = context.getRuntime();
         CharsetDecoder decoder;
         CharsetEncoder encoder;
         try {
             decoder = Charset.forName(decodeCharset).newDecoder();
             encoder = Charset.forName(encodeCharset).newEncoder();
         } catch (UnsupportedCharsetException e) {
             throw runtime.newArgumentError("invalid encoding");
         }
         
         ByteBuffer buf = ByteBuffer.wrap(str.getUnsafeBytes(), str.begin(), str.length());
         try {
             CharBuffer cbuf = decoder.decode(buf);
             buf = encoder.encode(cbuf);
         } catch (CharacterCodingException e) {
             throw runtime.newArgumentError("invalid encoding");
         }
         byte[] arr = buf.array();
         
         return runtime.newString(new ByteList(arr, 0, buf.limit()));
         
     }
 
     private static RubyString encodeMimeString(Ruby runtime, RubyString str, String format) {
         RubyArray array = RubyArray.newArray(runtime, str);
         return Pack.pack(runtime, array, new ByteList(ByteList.plain(format))).chomp(runtime.getCurrentContext());
     }
 
     private static NKFCharset detectMimeString(ByteList str, Map<String, NKFCharset> options) {
         if (str.length() <= 6) return NOCONV;
         if (options.get("mime-decode") == NOCONV) return NOCONV;
         if (!str.startsWith(BEGIN_MIME_STRING)) return NOCONV;
         if (!str.endsWith(END_MIME_STRING)) return NOCONV;
 
         int pos = str.indexOf('?', 3);
         if (pos < 0) return NOCONV;
         ByteList charset = new ByteList(str, 2, pos - 2);
         if (charset.caseInsensitiveCmp(MIME_UTF8) == 0) {
             options.put("input", UTF8);
         } else if (charset.caseInsensitiveCmp(MIME_JIS) == 0) {
             options.put("input", JIS);
         } else if (charset.caseInsensitiveCmp(MIME_EUC_JP) == 0) {
             options.put("input", EUC);
         } else {
             options.put("input", ASCII);
         }
 
         int prev = pos;
         pos = str.indexOf('?', pos + 1);
         if (pos < 0) return NOCONV;
         char encode = str.charAt(pos - 1);
 
         switch (encode) {
         case 'q':
         case 'Q':
             return QENCODE;
         case 'b':
         case 'B':
             return BASE64;
         default:
             return NOCONV;
         }
     }
 
     private static ByteList getMimeText(ByteList str) {
         int pos = 0;
         for (int i = 3; i >= 1; i--) {
             pos = str.indexOf('?', pos + 1);
         }
         return new ByteList(str, pos + 1, str.length() - pos - 3);
     }
 
     private static int optionUTF(String s, int pos) {
         int n = 8;
         int first = pos + 1;
         int second = pos + 2;
         if (first < s.length() && Character.isDigit(s.charAt(first))) {
             n = Character.digit(s.charAt(first), 10);
             if (second < s.length() && Character.isDigit(s.charAt(second))) {
                 n *= 10;
                 n += Character.digit(s.charAt(second), 10);
             }
         }
         return n;
     }
 
     private static Map<String, NKFCharset> parseOpt(String s) {
         Map<String, NKFCharset> options = new HashMap<String, NKFCharset>();
 
         // default options
         options.put("input", AUTO);
         options.put("output", JIS);
         options.put("mime-decode", MIME_DETECT);
         options.put("mime-encode", NOCONV);
         
         for (int i = 0; i < s.length(); i++) {
             switch (s.charAt(i)) {
             case 'b':
                 break;
             case 'u':
                 break;
             case 'j': // iso-2022-jp
                 options.put("output", JIS);
                 break;
             case 's': // Shift_JIS
                 options.put("output", SJIS);
                 break;
             case 'e': // EUC-JP
                 options.put("output", EUC);
                 break;
             case 'w': // UTF-8
             {
                 int n = optionUTF(s, i);
                 if (n == 32)
                     options.put("output", UTF32);
                 else if (n == 16)
                     options.put("output", UTF16);
                 else
                     options.put("output", UTF8);
             }
                 break;
             case 'J': // iso-2022-jp
                 options.put("input", JIS);
                 break;
             case 'S': // Shift_JIS
                 options.put("input", SJIS);
                 break;
             case 'E': // EUC-JP
                 options.put("input", EUC);
                 break;
             case 'W': // UTF-8
             {
                 int n = optionUTF(s, i);
                 if (n == 32)
                     options.put("input", UTF32);
                 else if (n == 16)
                     options.put("input", UTF16);
                 else
                     options.put("input", UTF8);
             }
                 break;
             case 't':
                 break;
             case 'r':
                 break;
             case 'h':
                 break;
             case 'm':
                 if (i+1 >= s.length()) {
                     options.put("mime-decode", MIME_DETECT);
                     break;
                 }
                 switch (s.charAt(i+1)) {
                 case 'B':
                     options.put("mime-decode", BASE64);
                     break;
                 case 'Q':
                     options.put("mime-decode", QENCODE);
                     break;
                 case 'N':
                     // TODO: non-strict option
                     break;
                 case '0':
                     options.put("mime-decode", NOCONV);
                     break;
                 }
                 break;
             case 'M':
                 if (i+1 >= s.length()) {
                     options.put("mime-encode", NOCONV);
                 }
                 switch (s.charAt(i+1)) {
                 case 'B':
                     options.put("mime-encode", BASE64);
                     break;
                 case 'Q':
                     options.put("mime-encode", QENCODE);
                     break;
                 }
                 break;
             case 'l':
                 break;
             case 'f':
                 break;
             case 'F':
                 break;
             case 'Z':
                 break;
             case 'X':
                 break;
             case 'x':
                 break;
             case 'B':
                 break;
             case 'T':
                 break;
             case 'd':
                 break;
             case 'c':
                 break;
             case 'I':
                 break;
             case 'L':
                 break;
             case '-':
                 if (s.charAt(i+1) == '-') {
                     // long name option
                 }
             default:
             }
         }
         return options;
     }
 }
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index 0cdda8a0cd..d450362ff1 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -1,1482 +1,1473 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.anno.FrameField.BACKREF;
 import static org.jruby.util.StringSupport.CR_7BIT;
 import static org.jruby.util.StringSupport.CR_BROKEN;
 import static org.jruby.util.StringSupport.CR_MASK;
 import static org.jruby.util.StringSupport.CR_UNKNOWN;
 import static org.jruby.util.StringSupport.CR_VALID;
 import static org.jruby.util.StringSupport.codeLength;
 import static org.jruby.util.StringSupport.codePoint;
 import static org.jruby.util.StringSupport.codeRangeScan;
 import static org.jruby.util.StringSupport.searchNonAscii;
 import static org.jruby.util.StringSupport.strLengthWithCodeRange;
 import static org.jruby.util.StringSupport.toLower;
 import static org.jruby.util.StringSupport.toUpper;
 import static org.jruby.util.StringSupport.unpackArg;
 import static org.jruby.util.StringSupport.unpackResult;
 
 import java.io.UnsupportedEncodingException;
 import java.nio.ByteBuffer;
 import java.nio.charset.CharacterCodingException;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetEncoder;
 import java.nio.charset.CodingErrorAction;
 import java.util.Arrays;
 import java.util.Locale;
 
 import org.jcodings.Encoding;
 import org.jcodings.EncodingDB.Entry;
 import org.jcodings.ascii.AsciiTables;
 import org.jcodings.constants.CharacterType;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jcodings.specific.UTF8Encoding;
 import org.jcodings.util.IntHash;
 import org.joni.Matcher;
 import org.joni.Option;
 import org.joni.Regex;
 import org.joni.Region;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.ConvertBytes;
 import org.jruby.util.Numeric;
 import org.jruby.util.Pack;
 import org.jruby.util.Sprintf;
 import org.jruby.util.StringSupport;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.string.JavaCrypt;
 
 /**
  * Implementation of Ruby String class
  * 
  * Concurrency: no synchronization is required among readers, but
  * all users must synchronize externally with writers.
  *
  */
 @JRubyClass(name="String", include={"Enumerable", "Comparable"})
 public class RubyString extends RubyObject implements EncodingCapable {
     private static final ASCIIEncoding ASCII = ASCIIEncoding.INSTANCE;
     private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
 
     // string doesn't share any resources
     private static final int SHARE_LEVEL_NONE = 0;
     // string has it's own ByteList, but it's pointing to a shared buffer (byte[])
     private static final int SHARE_LEVEL_BUFFER = 1;
     // string doesn't have it's own ByteList (values)
     private static final int SHARE_LEVEL_BYTELIST = 2;
 
     private volatile int shareLevel = SHARE_LEVEL_NONE;
 
     private ByteList value;
 
     public static RubyClass createStringClass(Ruby runtime) {
         RubyClass stringClass = runtime.defineClass("String", runtime.getObject(), STRING_ALLOCATOR);
         runtime.setString(stringClass);
         stringClass.index = ClassIndex.STRING;
         stringClass.setReifiedClass(RubyString.class);
         stringClass.kindOf = new RubyModule.KindOf() {
             @Override
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyString;
                 }
             };
 
         stringClass.includeModule(runtime.getComparable());
         if (!runtime.is1_9()) stringClass.includeModule(runtime.getEnumerable());
         stringClass.defineAnnotatedMethods(RubyString.class);
 
         return stringClass;
     }
 
     private static ObjectAllocator STRING_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return RubyString.newEmptyString(runtime, klass);
         }
     };
 
     public Encoding getEncoding() {
         return value.getEncoding();
     }
 
     public void associateEncoding(Encoding enc) {
         if (value.getEncoding() != enc) {
             if (!isCodeRangeAsciiOnly() || !enc.isAsciiCompatible()) clearCodeRange();
             value.setEncoding(enc);
         }
     }
 
     public final void setEncodingAndCodeRange(Encoding enc, int cr) {
         value.setEncoding(enc);
         setCodeRange(cr);
     }
 
     public final Encoding toEncoding(Ruby runtime) {
         if (!value.getEncoding().isAsciiCompatible()) {
             throw runtime.newArgumentError("invalid name encoding (non ASCII)");
         }
         Entry entry = runtime.getEncodingService().findEncodingOrAliasEntry(value);
         if (entry == null) {
             throw runtime.newArgumentError("unknown encoding name - " + value);
         }
         return entry.getEncoding();
     }
 
     public final int getCodeRange() {
         return flags & CR_MASK;
     }
 
     public final void setCodeRange(int codeRange) {
         flags |= codeRange & CR_MASK;
     }
 
     public final void clearCodeRange() {
         flags &= ~CR_MASK;
     }
 
     private void keepCodeRange() {
         if (getCodeRange() == CR_BROKEN) clearCodeRange();
     }
 
     // ENC_CODERANGE_ASCIIONLY
     public final boolean isCodeRangeAsciiOnly() {
         return getCodeRange() == CR_7BIT;
     }
 
     // rb_enc_str_asciionly_p
     public final boolean isAsciiOnly() {
         return value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT;
     }
 
     public final boolean isCodeRangeValid() {
         return (flags & CR_VALID) != 0;
     }
 
     public final boolean isCodeRangeBroken() {
         return (flags & CR_BROKEN) != 0;
     }
 
     static int codeRangeAnd(int cr1, int cr2) {
         if (cr1 == CR_7BIT) return cr2;
         if (cr1 == CR_VALID) return cr2 == CR_7BIT ? CR_VALID : cr2;
         return CR_UNKNOWN;
     }
 
     private void copyCodeRangeForSubstr(RubyString from, Encoding enc) {
         int fromCr = from.getCodeRange();
         if (fromCr == CR_7BIT) {
             setCodeRange(fromCr);
         } else if (fromCr == CR_VALID) {
             if (!enc.isAsciiCompatible() || searchNonAscii(value) != -1) {
                 setCodeRange(CR_VALID);
             } else {
                 setCodeRange(CR_7BIT);
             }
         } else{ 
             if (value.getRealSize() == 0) {
                 setCodeRange(!enc.isAsciiCompatible() ? CR_VALID : CR_7BIT);
             }
         }
     }
 
     private void copyCodeRange(RubyString from) {
         value.setEncoding(from.value.getEncoding());
         setCodeRange(from.getCodeRange());
     }
 
     // rb_enc_str_coderange
     final int scanForCodeRange() {
         int cr = getCodeRange();
         if (cr == CR_UNKNOWN) {
             cr = codeRangeScan(value.getEncoding(), value);
             setCodeRange(cr);
         }
         return cr;
     }
 
     final boolean singleByteOptimizable() {
         return getCodeRange() == CR_7BIT || value.getEncoding().isSingleByte();
     }
 
     final boolean singleByteOptimizable(Encoding enc) {
         return getCodeRange() == CR_7BIT || enc.isSingleByte();
     }
 
     private Encoding isCompatibleWith(RubyString other) { 
         Encoding enc1 = value.getEncoding();
         Encoding enc2 = other.value.getEncoding();
 
         if (enc1 == enc2) return enc1;
 
         if (other.value.getRealSize() == 0) return enc1;
         if (value.getRealSize() == 0) return enc2;
 
         if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
 
         return RubyEncoding.areCompatible(enc1, scanForCodeRange(), enc2, other.scanForCodeRange());
     }
 
     final Encoding isCompatibleWith(EncodingCapable other) {
         if (other instanceof RubyString) return checkEncoding((RubyString)other);
         Encoding enc1 = value.getEncoding();
         Encoding enc2 = other.getEncoding();
 
         if (enc1 == enc2) return enc1;
         if (value.getRealSize() == 0) return enc2;
         if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
         if (enc2 instanceof USASCIIEncoding) return enc1;
         if (scanForCodeRange() == CR_7BIT) return enc2;
         return null;
     }
 
     final Encoding checkEncoding(RubyString other) {
         Encoding enc = isCompatibleWith(other);
         if (enc == null) throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + 
                                 value.getEncoding() + " and " + other.value.getEncoding());
         return enc;
     }
 
     final Encoding checkEncoding(EncodingCapable other) {
         Encoding enc = isCompatibleWith(other);
         if (enc == null) throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + 
                                 value.getEncoding() + " and " + other.getEncoding());
         return enc;
     }
 
     private Encoding checkDummyEncoding() {
         Encoding enc = value.getEncoding();
         if (enc.isDummy()) throw getRuntime().newEncodingCompatibilityError(
                 "incompatible encoding with this operation: " + enc);
         return enc;
     }
 
     private boolean isComparableWith(RubyString other) {
         ByteList otherValue = other.value;
         if (value.getEncoding() == otherValue.getEncoding() ||
             value.getRealSize() == 0 || otherValue.getRealSize() == 0) return true;
         return isComparableViaCodeRangeWith(other);
     }
 
     private boolean isComparableViaCodeRangeWith(RubyString other) {
         int cr1 = scanForCodeRange();
         int cr2 = other.scanForCodeRange();
 
         if (cr1 == CR_7BIT && (cr2 == CR_7BIT || other.value.getEncoding().isAsciiCompatible())) return true;
         if (cr2 == CR_7BIT && value.getEncoding().isAsciiCompatible()) return true;
         return false;
     }
 
     private int strLength(Encoding enc) {
         if (singleByteOptimizable(enc)) return value.getRealSize();
         return strLength(value, enc);
     }
 
     final int strLength() {
         if (singleByteOptimizable()) return value.getRealSize();
         return strLength(value);
     }
 
     private int strLength(ByteList bytes) {
         return strLength(bytes, bytes.getEncoding());
     }
 
     private int strLength(ByteList bytes, Encoding enc) {
         if (isCodeRangeValid() && enc instanceof UTF8Encoding) return StringSupport.utf8Length(value);
 
         long lencr = strLengthWithCodeRange(bytes, enc);
         int cr = unpackArg(lencr);
         if (cr != 0) setCodeRange(cr);
         return unpackResult(lencr);
     }
 
     final int subLength(int pos) {
         if (singleByteOptimizable() || pos < 0) return pos;
         return StringSupport.strLength(value.getEncoding(), value.getUnsafeBytes(), value.getBegin(), value.getBegin() + pos);
     }
 
     /** short circuit for String key comparison
      * 
      */
     @Override
     public final boolean eql(IRubyObject other) {
         Ruby runtime = getRuntime();
         if (getMetaClass() != runtime.getString() || getMetaClass() != other.getMetaClass()) return super.eql(other);
         return runtime.is1_9() ? eql19(runtime, other) : eql18(runtime, other);
     }
 
     private boolean eql18(Ruby runtime, IRubyObject other) {
         return value.equal(((RubyString)other).value);
     }
 
     // rb_str_hash_cmp
     private boolean eql19(Ruby runtime, IRubyObject other) {
         RubyString otherString = (RubyString)other;
         return isComparableWith(otherString) && value.equal(((RubyString)other).value);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass) {
         this(runtime, rubyClass, EMPTY_BYTE_ARRAY);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, CharSequence value) {
         super(runtime, rubyClass);
         assert value != null;
-        byte[] bytes = null;
-        try {
-            bytes = value.toString().getBytes("UTF-8");
-        } catch (UnsupportedEncodingException ex) {
-            bytes = ByteList.plain(value);
-        }
+        byte[] bytes = RubyEncoding.encodeUTF8(value);
         this.value = new ByteList(bytes, false);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, byte[] value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = new ByteList(value);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = value;
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, boolean objectSpace) {
         super(runtime, rubyClass, objectSpace);
         assert value != null;
         this.value = value;
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding enc, int cr) {
         this(runtime, rubyClass, value);
         value.setEncoding(enc);
         flags |= cr;
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding enc) {
         this(runtime, rubyClass, value);
         value.setEncoding(enc);
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, int cr) {
         this(runtime, rubyClass, value);
         flags |= cr;
     }
 
     // Deprecated String construction routines
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *  @deprecated  
      */
     @Deprecated
     public RubyString newString(CharSequence s) {
         return new RubyString(getRuntime(), getType(), s);
     }
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *  @deprecated
      */
     @Deprecated
     public RubyString newString(ByteList s) {
         return new RubyString(getRuntime(), getMetaClass(), s);
     }
 
     @Deprecated
     public static RubyString newString(Ruby runtime, RubyClass clazz, CharSequence str) {
         return new RubyString(runtime, clazz, str);
     }
 
     public static RubyString newStringLight(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes, false);
     }
 
     public static RubyString newStringLight(Ruby runtime, int size) {
         return new RubyString(runtime, runtime.getString(), new ByteList(size), false);
     }
   
     public static RubyString newString(Ruby runtime, CharSequence str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
 
     public static RubyString newString(Ruby runtime, String str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
     
     public static RubyString newString(Ruby runtime, byte[] bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newString(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] copy = new byte[length];
         System.arraycopy(bytes, start, copy, 0, length);
         return new RubyString(runtime, runtime.getString(), new ByteList(copy, false));
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
     
     public static RubyString newUnicodeString(Ruby runtime, String str) {
-        try {
-            return new RubyString(runtime, runtime.getString(), new ByteList(str.getBytes("UTF8"), false));
-        } catch (UnsupportedEncodingException uee) {
-            return new RubyString(runtime, runtime.getString(), str);
-        }
+        return new RubyString(runtime, runtime.getString(), new ByteList(RubyEncoding.encodeUTF8(str), false));
     }
 
     // String construction routines by NOT byte[] buffer and making the target String shared 
     public static RubyString newStringShared(Ruby runtime, RubyString orig) {
         orig.shareLevel = SHARE_LEVEL_BYTELIST;
         RubyString str = new RubyString(runtime, runtime.getString(), orig.value);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }       
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes) {
         return newStringShared(runtime, runtime.getString(), bytes);
     }    
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes) {
         RubyString str = new RubyString(runtime, clazz, bytes);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, byte[] bytes) {
         return newStringShared(runtime, new ByteList(bytes, false));
     }
 
     public static RubyString newStringShared(Ruby runtime, byte[] bytes, int start, int length) {
         return newStringShared(runtime, new ByteList(bytes, start, length, false));
     }
 
     public static RubyString newEmptyString(Ruby runtime) {
         return newEmptyString(runtime, runtime.getString());
     }
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass) {
         RubyString empty = new RubyString(runtime, metaClass, ByteList.EMPTY_BYTELIST);
         empty.shareLevel = SHARE_LEVEL_BYTELIST;
         return empty;
     }
 
     // String construction routines by NOT byte[] buffer and NOT making the target String shared 
     public static RubyString newStringNoCopy(Ruby runtime, ByteList bytes) {
         return newStringNoCopy(runtime, runtime.getString(), bytes);
     }    
 
     public static RubyString newStringNoCopy(Ruby runtime, RubyClass clazz, ByteList bytes) {
         return new RubyString(runtime, clazz, bytes);
     }    
 
     public static RubyString newStringNoCopy(Ruby runtime, byte[] bytes, int start, int length) {
         return newStringNoCopy(runtime, new ByteList(bytes, start, length, false));
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, byte[] bytes) {
         return newStringNoCopy(runtime, new ByteList(bytes, false));
     }
 
     /** Encoding aware String construction routines for 1.9
      * 
      */
     private static final class EmptyByteListHolder {
         final ByteList bytes;
         final int cr;
         EmptyByteListHolder(Encoding enc) {
             this.bytes = new ByteList(ByteList.NULL_ARRAY, enc);
             this.cr = bytes.getEncoding().isAsciiCompatible() ? CR_7BIT : CR_VALID;
         }
     }
 
     private static final EmptyByteListHolder EMPTY_BYTELISTS[] = new EmptyByteListHolder[4];
 
     static EmptyByteListHolder getEmptyByteList(Encoding enc) {
         int index = enc.getIndex();
         EmptyByteListHolder bytes;
         if (index < EMPTY_BYTELISTS.length && (bytes = EMPTY_BYTELISTS[index]) != null) {
             return bytes;
         }
         return prepareEmptyByteList(enc);
     }
 
     private static EmptyByteListHolder prepareEmptyByteList(Encoding enc) {
         int index = enc.getIndex();
         if (index >= EMPTY_BYTELISTS.length) {
             EmptyByteListHolder tmp[] = new EmptyByteListHolder[index + 4];
             System.arraycopy(EMPTY_BYTELISTS,0, tmp, 0, EMPTY_BYTELISTS.length);
         }
         return EMPTY_BYTELISTS[index] = new EmptyByteListHolder(enc);
     }
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass, Encoding enc) {
         EmptyByteListHolder holder = getEmptyByteList(enc);
         RubyString empty = new RubyString(runtime, metaClass, holder.bytes, holder.cr);
         empty.shareLevel = SHARE_LEVEL_BYTELIST;
         return empty;
     }
 
     public static RubyString newEmptyString(Ruby runtime, Encoding enc) {
         return newEmptyString(runtime, runtime.getString(), enc);
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, RubyClass clazz, ByteList bytes, Encoding enc, int cr) {
         return new RubyString(runtime, clazz, bytes, enc, cr);
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, ByteList bytes, Encoding enc, int cr) {
         return newStringNoCopy(runtime, runtime.getString(), bytes, enc, cr);
     }
 
     public static RubyString newUsAsciiStringNoCopy(Ruby runtime, ByteList bytes) {
         return newStringNoCopy(runtime, bytes, USASCIIEncoding.INSTANCE, CR_7BIT);
     }
 
     public static RubyString newUsAsciiStringShared(Ruby runtime, ByteList bytes) {
         RubyString str = newStringNoCopy(runtime, bytes, USASCIIEncoding.INSTANCE, CR_7BIT);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
     
     public static RubyString newUsAsciiStringShared(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] copy = new byte[length];
         System.arraycopy(bytes, start, copy, 0, length);
         return newUsAsciiStringShared(runtime, new ByteList(copy, false));
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.STRING;
     }
 
     @Override
     public Class getJavaClass() {
         return String.class;
     }
 
     @Override
     public RubyString convertToString() {
         return this;
     }
 
     @Override
     public String toString() {
         return value.toString();
     }
 
     /** rb_str_dup
      * 
      */
     @Deprecated
     public final RubyString strDup() {
         return strDup(getRuntime(), getMetaClass());
     }
     
     public final RubyString strDup(Ruby runtime) {
         return strDup(runtime, getMetaClass());
     }
     
     @Deprecated
     final RubyString strDup(RubyClass clazz) {
         return strDup(getRuntime(), getMetaClass());
     }
 
     final RubyString strDup(Ruby runtime, RubyClass clazz) {
         shareLevel = SHARE_LEVEL_BYTELIST;
         RubyString dup = new RubyString(runtime, clazz, value);
         dup.shareLevel = SHARE_LEVEL_BYTELIST;
         dup.flags |= flags & (CR_MASK | TAINTED_F | UNTRUSTED_F);
 
         return dup;
     }
 
     /* rb_str_subseq */
     public final RubyString makeShared(Ruby runtime, int index, int len) {
         final RubyString shared;
         RubyClass meta = getMetaClass();
         if (len == 0) {
             shared = newEmptyString(runtime, meta);
         } else if (len == 1) {
             shared = newStringShared(runtime, meta, 
                     RubyInteger.SINGLE_CHAR_BYTELISTS[value.getUnsafeBytes()[value.getBegin() + index] & 0xff]);
         } else {
             if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
             shared = new RubyString(runtime, meta, value.makeShared(index, len));
             shared.shareLevel = SHARE_LEVEL_BUFFER;
         }
 
         shared.infectBy(this);
         return shared;
     }
 
     public final RubyString makeShared19(Ruby runtime, int index, int len) {
         return makeShared19(runtime, value, index, len);
     }
 
     private RubyString makeShared19(Ruby runtime, ByteList value, int index, int len) {
         final RubyString shared;
         Encoding enc = value.getEncoding();
         RubyClass meta = getMetaClass();
         if (len == 0) {
             shared = newEmptyString(runtime, meta, enc);
         } else {
             if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
             shared = new RubyString(runtime, meta, value.makeShared(index, len));
             shared.shareLevel = SHARE_LEVEL_BUFFER;
             shared.copyCodeRangeForSubstr(this, enc); // no need to assign encoding, same bytelist shared
         }
         shared.infectBy(this);
         return shared;
     }
 
     final void modifyCheck() {
         if ((flags & FROZEN_F) != 0) throw getRuntime().newFrozenError("string");
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify string");
         }
     }
 
     private final void modifyCheck(byte[] b, int len) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len) throw getRuntime().newRuntimeError("string modified");
     }
 
     private final void modifyCheck(byte[] b, int len, Encoding enc) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len || value.getEncoding() != enc) throw getRuntime().newRuntimeError("string modified");
     }
 
     private final void frozenCheck() {
         if (isFrozen()) throw getRuntime().newRuntimeError("string frozen");
     }
 
     /** rb_str_modify
      * 
      */
     public final void modify() {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 value = value.dup();
             } else {
                 value.unshare();
             }
             shareLevel = SHARE_LEVEL_NONE;
         }
 
         value.invalidate();
     }
 
     public final void modify19() {
         modify();
         clearCodeRange();
     }
 
     private void modifyAndKeepCodeRange() {
         modify();
         keepCodeRange();
     }
     
     /** rb_str_modify (with length bytes ensured)
      * 
      */    
     public final void modify(int length) {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 value = value.dup(length);
             } else {
                 value.unshare(length);
             }
             shareLevel = SHARE_LEVEL_NONE;
         } else {
             value.ensure(length);
         }
 
         value.invalidate();
     }
     
     public final void modify19(int length) {
         modify(length);
         clearCodeRange();
     }
     
     final void view(ByteList bytes) {
         modifyCheck();
 
         value = bytes;
         shareLevel = SHARE_LEVEL_NONE;
     }
 
     private final void view(byte[]bytes) {
         modifyCheck();        
 
         value.replace(bytes);
         shareLevel = SHARE_LEVEL_NONE;
 
         value.invalidate();        
     }
 
     private final void view(int index, int len) {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 // if len == 0 then shared empty
                 value = value.makeShared(index, len);
                 shareLevel = SHARE_LEVEL_BUFFER;
             } else {
                 value.view(index, len);
             }
         } else {        
             value.view(index, len);
             // FIXME this below is temporary, but its much safer for COW (it prevents not shared Strings with begin != 0)
             // this allows now e.g.: ByteList#set not to be begin aware
             shareLevel = SHARE_LEVEL_BUFFER;
         }
 
         value.invalidate();
     }
 
     public static String bytesToString(byte[] bytes, int beg, int len) {
         return new String(ByteList.plain(bytes, beg, len));
     }
 
     public static String byteListToString(ByteList bytes) {
         return bytesToString(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
     }
 
     public static String bytesToString(byte[] bytes) {
         return bytesToString(bytes, 0, bytes.length);
     }
 
     public static byte[] stringToBytes(String string) {
         return ByteList.plain(string);
     }
 
     @Override
     public RubyString asString() {
         return this;
     }
 
     @Override
     public IRubyObject checkStringType() {
         return this;
     }
 
     @Override
     public IRubyObject checkStringType19() {
         return this;
     }
 
     @JRubyMethod(name = "try_convert", meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject try_convert(ThreadContext context, IRubyObject recv, IRubyObject str) {
         return str.checkStringType();
     }
 
     @JRubyMethod(name = {"to_s", "to_str"})
     @Override
     public IRubyObject to_s() {
         Ruby runtime = getRuntime();
         if (getMetaClass().getRealClass() != runtime.getString()) {
             return strDup(runtime, runtime.getString());
         }
         return this;
     }
 
     @Override
     public final int compareTo(IRubyObject other) {
         Ruby runtime = getRuntime();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             return runtime.is1_9() ? op_cmp19(otherString) : op_cmp(otherString);
         }
         return (int)op_cmpCommon(runtime.getCurrentContext(), other).convertToInteger().getLongValue();
     }
 
     /* rb_str_cmp_m */
     @JRubyMethod(name = "<=>", compat = CompatVersion.RUBY1_8)
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.getRuntime().newFixnum(op_cmp((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     @JRubyMethod(name = "<=>", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_cmp19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.getRuntime().newFixnum(op_cmp19((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     private IRubyObject op_cmpCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         // deal with case when "other" is not a string
         if (other.respondsTo("to_str") && other.respondsTo("<=>")) {
             IRubyObject result = other.callMethod(context, "<=>", this);
             if (result.isNil()) return result;
             if (result instanceof RubyFixnum) {
                 return RubyFixnum.newFixnum(runtime, -((RubyFixnum)result).getLongValue());
             } else {
                 return RubyFixnum.zero(runtime).callMethod(context, "-", result);
             }
         }
         return runtime.getNil();        
     }
         
     /** rb_str_equal
      * 
      */
     @JRubyMethod(name = "==", compat = CompatVersion.RUBY1_8)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             return value.equal(((RubyString)other).value) ? runtime.getTrue() : runtime.getFalse();    
         }
         return op_equalCommon(context, other);
     }
 
     @JRubyMethod(name = "==", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_equal19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             return isComparableWith(otherString) && value.equal(otherString.value) ? runtime.getTrue() : runtime.getFalse();    
         }
         return op_equalCommon(context, other);
     }
 
     private IRubyObject op_equalCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (!other.respondsTo("to_str")) return runtime.getFalse();
         return other.callMethod(context, "==", this).isTrue() ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "+", required = 1, compat = CompatVersion.RUBY1_8, argTypes = RubyString.class)
     public IRubyObject op_plus(ThreadContext context, RubyString str) {
         RubyString resultStr = newString(context.getRuntime(), addByteLists(value, str.value));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
     public IRubyObject op_plus(ThreadContext context, IRubyObject other) {
         return op_plus(context, other.convertToString());
     }
 
     @JRubyMethod(name = "+", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_plus19(ThreadContext context, RubyString str) {
         Encoding enc = checkEncoding(str);
         RubyString resultStr = newStringNoCopy(context.getRuntime(), addByteLists(value, str.value),
                                     enc, codeRangeAnd(getCodeRange(), str.getCodeRange()));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
     public IRubyObject op_plus19(ThreadContext context, IRubyObject other) {
         return op_plus19(context, other.convertToString());
     }
 
     private ByteList addByteLists(ByteList value1, ByteList value2) {
         ByteList result = new ByteList(value1.getRealSize() + value2.getRealSize());
         result.setRealSize(value1.getRealSize() + value2.getRealSize());
         System.arraycopy(value1.getUnsafeBytes(), value1.getBegin(), result.getUnsafeBytes(), 0, value1.getRealSize());
         System.arraycopy(value2.getUnsafeBytes(), value2.getBegin(), result.getUnsafeBytes(), value1.getRealSize(), value2.getRealSize());
         return result;
     }
 
     @JRubyMethod(name = "*", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         return multiplyByteList(context, other);
     }
 
     @JRubyMethod(name = "*", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_mul19(ThreadContext context, IRubyObject other) {
         RubyString result = multiplyByteList(context, other);
         Encoding encoding = value.getEncoding();
         result.value.setEncoding(encoding);
         result.copyCodeRangeForSubstr(this, encoding);
         return result;
     }
 
     private RubyString multiplyByteList(ThreadContext context, IRubyObject arg) {
         int len = RubyNumeric.num2int(arg);
         if (len < 0) throw context.getRuntime().newArgumentError("negative argument");
 
         // we limit to int because ByteBuffer can only allocate int sizes
         if (len > 0 && Integer.MAX_VALUE / len < value.getRealSize()) {
             throw context.getRuntime().newArgumentError("argument too big");
         }
 
         ByteList bytes = new ByteList(len *= value.getRealSize());
         if (len > 0) {
             bytes.setRealSize(len);
             int n = value.getRealSize();
             System.arraycopy(value.getUnsafeBytes(), value.getBegin(), bytes.getUnsafeBytes(), 0, n);
             while (n <= len >> 1) {
                 System.arraycopy(bytes.getUnsafeBytes(), 0, bytes.getUnsafeBytes(), n, n);
                 n <<= 1;
             }
             System.arraycopy(bytes.getUnsafeBytes(), 0, bytes.getUnsafeBytes(), n, len - n);
         }
         RubyString result = new RubyString(context.getRuntime(), getMetaClass(), bytes);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "%", required = 1)
     public IRubyObject op_format(ThreadContext context, IRubyObject arg) {
         return opFormatCommon(context, arg, context.getRuntime().getInstanceConfig().getCompatVersion());
     }
 
     private IRubyObject opFormatCommon(ThreadContext context, IRubyObject arg, CompatVersion compat) {
         IRubyObject tmp = arg.checkArrayType();
         if (tmp.isNil()) tmp = arg;
 
         // FIXME: Should we make this work with platform's locale,
         // or continue hardcoding US?
         ByteList out = new ByteList(value.getRealSize());
         boolean tainted;
         switch (compat) {
         case RUBY1_8:
             tainted = Sprintf.sprintf(out, Locale.US, value, tmp);
             break;
         case RUBY1_9:
             tainted = Sprintf.sprintf1_9(out, Locale.US, value, tmp);
             break;
         default:
             throw new RuntimeException("invalid compat version for sprintf: " + compat);
         }
         RubyString str = newString(context.getRuntime(), out);
 
         str.setTaint(tainted || isTaint());
         return str;
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
         Ruby runtime = getRuntime();
         return RubyFixnum.newFixnum(runtime, strHashCode(runtime));
     }
 
     @Override
     public int hashCode() {
         return strHashCode(getRuntime());
     }
 
     private int strHashCode(Ruby runtime) {
         if (runtime.is1_9()) {
             return value.hashCode() ^ (value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT ? 0 : value.getEncoding().getIndex());
         } else {
             return value.hashCode();
         }
     }
 
     @Override
     public boolean equals(Object other) {
         if (this == other) return true;
 
         if (other instanceof RubyString) {
             if (((RubyString) other).value.equal(value)) return true;
         }
 
         return false;
     }
 
     /** rb_obj_as_string
      *
      */
     public static RubyString objAsString(ThreadContext context, IRubyObject obj) {
         if (obj instanceof RubyString) return (RubyString) obj;
         IRubyObject str = obj.callMethod(context, "to_s");
         if (!(str instanceof RubyString)) return (RubyString) obj.anyToString();
         if (obj.isTaint()) str.setTaint(true);
         return (RubyString) str;
     }
 
     /** rb_str_cmp
      *
      */
     public final int op_cmp(RubyString other) {
         return value.cmp(other.value);
     }
 
     public final int op_cmp19(RubyString other) {
         int ret = value.cmp(other.value);
         if (ret == 0 && !isComparableWith(other)) {
             return value.getEncoding().getIndex() > other.value.getEncoding().getIndex() ? 1 : -1;
         }
         return ret;
     }
 
     /** rb_to_id
      *
      */
     @Override
     public String asJavaString() {
         return toString();
     }
 
     public IRubyObject doClone(){
         return newString(getRuntime(), value.dup());
     }
 
     public final RubyString cat(byte[] str) {
         modify(value.getRealSize() + str.length);
         System.arraycopy(str, 0, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), str.length);
         value.setRealSize(value.getRealSize() + str.length);
         return this;
     }
 
     public final RubyString cat(byte[] str, int beg, int len) {
         modify(value.getRealSize() + len);
         System.arraycopy(str, beg, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
         value.setRealSize(value.getRealSize() + len);
         return this;
     }
 
     // // rb_str_buf_append
     public final RubyString cat19(RubyString str) {
         ByteList strValue = str.value;
         int strCr = str.getCodeRange();
         strCr = cat(strValue.getUnsafeBytes(), strValue.getBegin(), strValue.getRealSize(), strValue.getEncoding(), strCr, strCr);
         infectBy(str);
         str.setCodeRange(strCr);
         return this;
     }
 
     public final RubyString cat(ByteList str) {
         modify(value.getRealSize() + str.getRealSize());
         System.arraycopy(str.getUnsafeBytes(), str.getBegin(), value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), str.getRealSize());
         value.setRealSize(value.getRealSize() + str.getRealSize());
         return this;
     }
 
     public final RubyString cat(byte ch) {
         modify(value.getRealSize() + 1);
         value.getUnsafeBytes()[value.getBegin() + value.getRealSize()] = ch;
         value.setRealSize(value.getRealSize() + 1);
         return this;
     }
 
     public final RubyString cat(int ch) {
         return cat((byte)ch);
     }
 
     public final RubyString cat(int code, Encoding enc) {
         int n = codeLength(getRuntime(), enc, code);
         modify(value.getRealSize() + n);
         enc.codeToMbc(code, value.getUnsafeBytes(), value.getBegin() + value.getRealSize());
         value.setRealSize(value.getRealSize() + n);
         return this;
     }
 
     public final int cat(byte[]bytes, int p, int len, Encoding enc, int cr, int cr2) {
         modify(value.getRealSize() + len);
         int toCr = getCodeRange();
         Encoding toEnc = value.getEncoding();
         
         if (toEnc == enc) {
             if (toCr == CR_UNKNOWN || (toEnc == ASCIIEncoding.INSTANCE && toCr != CR_7BIT)) { 
                 cr = CR_UNKNOWN;
             } else if (cr == CR_UNKNOWN) {
                 cr = codeRangeScan(enc, bytes, p, len);
             }
         } else {
             if (!toEnc.isAsciiCompatible() || !enc.isAsciiCompatible()) {
                 if (len == 0) return cr2;
                 if (value.getRealSize() == 0) {
                     System.arraycopy(bytes, p, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
                     value.setRealSize(value.getRealSize() + len);
                     setEncodingAndCodeRange(enc, cr);
                     return cr2;
                 }
                 throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + toEnc + " and " + enc);
             }
             if (cr == CR_UNKNOWN) cr = codeRangeScan(enc, bytes, p, len);
             if (toCr == CR_UNKNOWN) {
                 if (toEnc == ASCIIEncoding.INSTANCE || cr != CR_7BIT) toCr = scanForCodeRange(); 
             }
         }
         if (cr2 != 0) cr2 = cr;
 
         if (toEnc != enc && toCr != CR_7BIT && cr != CR_7BIT) {        
             throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + toEnc + " and " + enc);
         }
         
         final int resCr;
         final Encoding resEnc;
         if (toCr == CR_UNKNOWN) {
             resEnc = toEnc;
             resCr = CR_UNKNOWN;
         } else if (toCr == CR_7BIT) {
             if (cr == CR_7BIT) {
                 resEnc = toEnc == ASCIIEncoding.INSTANCE ? toEnc : enc;
                 resCr = CR_7BIT;
             } else {
                 resEnc = enc;
                 resCr = cr;
             }
         } else if (toCr == CR_VALID) {
             resEnc = toEnc;
             resCr = toCr;
         } else {
             resEnc = toEnc;
             resCr = len > 0 ? CR_BROKEN : toCr;
         }
         
         if (len < 0) throw getRuntime().newArgumentError("negative string size (or size too big)");            
 
         System.arraycopy(bytes, p, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
         value.setRealSize(value.getRealSize() + len);
         setEncodingAndCodeRange(resEnc, resCr);
 
         return cr2;
     }
 
     public final int cat(byte[]bytes, int p, int len, Encoding enc) {
         return cat(bytes, p, len, enc, CR_UNKNOWN, 0);
     }
 
     public final RubyString catAscii(byte[]bytes, int p, int len) {
         Encoding enc = value.getEncoding();
         if (enc.isAsciiCompatible()) {
             cat(bytes, p, len, enc, CR_7BIT, 0);
         } else {
             byte buf[] = new byte[enc.maxLength()];
             int end = p + len;
             while (p < end) {
                 int c = bytes[p];
                 int cl = codeLength(getRuntime(), enc, c);
                 enc.codeToMbc(c, buf, 0);
                 cat(buf, 0, cl, enc, CR_VALID, 0);
                 p++;
             }
         }
         return this;
     }
 
     /** rb_str_replace_m
      *
      */
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject replace(IRubyObject other) {
         if (this == other) return this;
         replaceCommon(other);
         return this;
     }
 
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1, compat = CompatVersion.RUBY1_9)
     public RubyString replace19(IRubyObject other) {
         if (this == other) return this;
         setCodeRange(replaceCommon(other).getCodeRange()); // encoding doesn't have to be copied.
         return this;
     }
 
     private RubyString replaceCommon(IRubyObject other) {
         modifyCheck();
         RubyString otherStr = other.convertToString();
         otherStr.shareLevel = shareLevel = SHARE_LEVEL_BYTELIST;
         value = otherStr.value;
         infectBy(otherStr);
         return otherStr;
     }
 
     @JRubyMethod(name = "clear", compat = CompatVersion.RUBY1_9)
     public RubyString clear() {
         Encoding enc = value.getEncoding();
 
         EmptyByteListHolder holder = getEmptyByteList(enc); 
         value = holder.bytes;
         shareLevel = SHARE_LEVEL_BYTELIST;
         setCodeRange(holder.cr);
         return this;
     }
 
     @JRubyMethod(name = "reverse", compat = CompatVersion.RUBY1_8)
     public IRubyObject reverse(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() <= 1) return strDup(context.getRuntime());
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         byte[]obytes = new byte[len];
 
         for (int i = 0; i <= len >> 1; i++) {
             obytes[i] = bytes[p + len - i - 1];
             obytes[len - i - 1] = bytes[p + i];
         }
 
         return new RubyString(runtime, getMetaClass(), new ByteList(obytes, false)).infectBy(this);
     }
 
     @JRubyMethod(name = "reverse", compat = CompatVersion.RUBY1_9)
     public IRubyObject reverse19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() <= 1) return strDup(context.getRuntime());
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         byte[]obytes = new byte[len];
 
         boolean single = true;
         Encoding enc = value.getEncoding();
         // this really needs to be inlined here
         if (singleByteOptimizable(enc)) {
             for (int i = 0; i <= len >> 1; i++) {
                 obytes[i] = bytes[p + len - i - 1];
                 obytes[len - i - 1] = bytes[p + i];
             }
         } else {
             int end = p + len;
             int op = len;
             while (p < end) {
                 int cl = StringSupport.length(enc, bytes, p, end);
                 if (cl > 1 || (bytes[p] & 0x80) != 0) {
                     single = false;
                     op -= cl;
                     System.arraycopy(bytes, p, obytes, op, cl);
                     p += cl;
                 } else {
                     obytes[--op] = bytes[p++];
                 }
             }
         }
 
         RubyString result = new RubyString(runtime, getMetaClass(), new ByteList(obytes, false));
 
         if (getCodeRange() == CR_UNKNOWN) setCodeRange(single ? CR_7BIT : CR_VALID);
         Encoding encoding = value.getEncoding();
         result.value.setEncoding(encoding);
         result.copyCodeRangeForSubstr(this, encoding);
         return result.infectBy(this);
     }
 
     @JRubyMethod(name = "reverse!", compat = CompatVersion.RUBY1_8)
     public RubyString reverse_bang(ThreadContext context) {
         if (value.getRealSize() > 1) {
             modify();
             byte[]bytes = value.getUnsafeBytes();
             int p = value.getBegin();
             int len = value.getRealSize();
             for (int i = 0; i < len >> 1; i++) {
                 byte b = bytes[p + i];
                 bytes[p + i] = bytes[p + len - i - 1];
                 bytes[p + len - i - 1] = b;
             }
         }
 
         return this;
     }
 
     @JRubyMethod(name = "reverse!", compat = CompatVersion.RUBY1_9)
     public RubyString reverse_bang19(ThreadContext context) {
         if (value.getRealSize() > 1) {
             modifyAndKeepCodeRange();
             byte[]bytes = value.getUnsafeBytes();
             int p = value.getBegin();
             int len = value.getRealSize();
             
             Encoding enc = value.getEncoding();
             // this really needs to be inlined here
             if (singleByteOptimizable(enc)) {
                 for (int i = 0; i < len >> 1; i++) {
                     byte b = bytes[p + i];
                     bytes[p + i] = bytes[p + len - i - 1];
                     bytes[p + len - i - 1] = b;
                 }
             } else {
                 int end = p + len;
                 int op = len;
                 byte[]obytes = new byte[len];
                 boolean single = true;
                 while (p < end) {
                     int cl = StringSupport.length(enc, bytes, p, end);
                     if (cl > 1 || (bytes[p] & 0x80) != 0) {
                         single = false;
                         op -= cl;
                         System.arraycopy(bytes, p, obytes, op, cl);
                         p += cl;
                     } else {
                         obytes[--op] = bytes[p++];
                     }
                 }
                 value.setUnsafeBytes(obytes);
                 if (getCodeRange() == CR_UNKNOWN) setCodeRange(single ? CR_7BIT : CR_VALID);
             }
         }
         return this;
     }
 
     /** rb_str_s_new
      *
      */
     public static RubyString newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString newString = newStringShared(recv.getRuntime(), ByteList.EMPTY_BYTELIST);
         newString.setMetaClass((RubyClass) recv);
         newString.callInit(args, block);
         return newString;
     }
 
     @JRubyMethod(frame = true, visibility = Visibility.PRIVATE)
     @Override
     public IRubyObject initialize() {
         return this;
     }
 
     @JRubyMethod(frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject arg0) {
         replace(arg0);
         return this;
     }
 
     @JRubyMethod(name = "casecmp", compat = CompatVersion.RUBY1_8)
     public IRubyObject casecmp(ThreadContext context, IRubyObject other) {
         return RubyFixnum.newFixnum(context.getRuntime(), value.caseInsensitiveCmp(other.convertToString().value));
     }
 
     @JRubyMethod(name = "casecmp", compat = CompatVersion.RUBY1_9)
     public IRubyObject casecmp19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         RubyString otherStr = other.convertToString();
         Encoding enc = isCompatibleWith(otherStr);
         if (enc == null) return runtime.getNil();
         
         if (singleByteOptimizable() && otherStr.singleByteOptimizable()) {
             return RubyFixnum.newFixnum(runtime, value.caseInsensitiveCmp(otherStr.value));
         } else {
             return multiByteCasecmp(runtime, enc, value, otherStr.value);
         }
     }
 
     private IRubyObject multiByteCasecmp(Ruby runtime, Encoding enc, ByteList value, ByteList otherValue) {
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
 
         byte[]obytes = otherValue.getUnsafeBytes();
         int op = otherValue.getBegin();
         int oend = op + otherValue.getRealSize();
 
         while (p < end && op < oend) {
             final int c, oc;
             if (enc.isAsciiCompatible()) {
                 c = bytes[p] & 0xff;
                 oc = obytes[op] & 0xff;
             } else {
                 c = StringSupport.preciseCodePoint(enc, bytes, p, end);
                 oc = StringSupport.preciseCodePoint(enc, obytes, op, oend);                
             }
 
             int cl, ocl;
             if (Encoding.isAscii(c) && Encoding.isAscii(oc)) {
                 if (AsciiTables.ToUpperCaseTable[c] != AsciiTables.ToUpperCaseTable[oc]) {
                     return c < oc ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime); 
                 }
                 cl = ocl = 1;
             } else {
                 cl = StringSupport.length(enc, bytes, p, end);
                 ocl = StringSupport.length(enc, obytes, op, oend);
                 // TODO: opt for 2 and 3 ?
                 int ret = StringSupport.caseCmp(bytes, p, obytes, op, cl < ocl ? cl : ocl);
                 if (ret != 0) return ret < 0 ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
                 if (cl != ocl) return cl < ocl ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
             }
 
             p += cl;
             op += ocl;
         }
         if (end - p == oend - op) return RubyFixnum.zero(runtime);
         return end - p > oend - op ? RubyFixnum.one(runtime) : RubyFixnum.minus_one(runtime);
     }
 
     /** rb_str_match
      *
      */
     @JRubyMethod(name = "=~", compat = CompatVersion.RUBY1_8, writes = BACKREF)
     @Override
     public IRubyObject op_match(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).op_match(context, this);
         if (other instanceof RubyString) throw context.getRuntime().newTypeError("type mismatch: String given");
         return other.callMethod(context, "=~", this);
     }
 
     @JRubyMethod(name = "=~", compat = CompatVersion.RUBY1_9, writes = BACKREF)
     public IRubyObject op_match19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).op_match19(context, this);
         if (other instanceof RubyString) throw context.getRuntime().newTypeError("type mismatch: String given");
         return other.callMethod(context, "=~", this);
     }
     /**
      * String#match(pattern)
      *
      * rb_str_match_m
      *
      * @param pattern Regexp or String
      */
     @JRubyMethod(name = "match", compat = CompatVersion.RUBY1_8, reads = BACKREF)
     public IRubyObject match(ThreadContext context, IRubyObject pattern) {
@@ -6167,1184 +6158,1181 @@ public class RubyString extends RubyObject implements EncodingCapable {
             int t = s;
             int last = -1;
             while (s < send) {
                 int c0 = sbytes[s++];
                 if ((c = trans[c0 & 0xff]) >= 0) {
                     if (last == c) continue;
                     last = c;
                     sbytes[t++] = (byte)(c & 0xff);
                     modify = true;
                 } else {
                     last = -1;
                     sbytes[t++] = (byte)c0;
                 }
             }
 
             if (value.getRealSize() > (t - value.getBegin())) {
                 value.setRealSize(t - value.getBegin());
                 modify = true;
             }
         } else {
             while (s < send) {
                 if ((c = trans[sbytes[s] & 0xff]) >= 0) {
                     sbytes[s] = (byte)(c & 0xff);
                     modify = true;
                 }
                 s++;
             }
         }
 
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject trTrans19(ThreadContext context, IRubyObject src, IRubyObject repl, boolean sflag) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) return runtime.getNil();
 
         RubyString replStr = repl.convertToString();
         ByteList replList = replStr.value;
         if (replList.getRealSize() == 0) return delete_bang19(context, src);
 
         RubyString srcStr = src.convertToString();
         ByteList srcList = srcStr.value;
         Encoding e1 = checkEncoding(srcStr);
         Encoding e2 = checkEncoding(replStr);
         Encoding enc = e1 == e2 ? e1 : srcStr.checkEncoding(replStr);
 
         int cr = getCodeRange();
 
         final TR trSrc = new TR(srcList);
         boolean cflag = false;
         if (value.getRealSize() > 1) {
             if (enc.isAsciiCompatible()) {
                 if (trSrc.buf.length > 0 && (trSrc.buf[trSrc.p] & 0xff) == '^' && trSrc.p + 1 < trSrc.pend) {
                     cflag = true;
                     trSrc.p++;
                 }
             } else {
                 int cl = StringSupport.preciseLength(enc, trSrc.buf, trSrc.p, trSrc.pend);
                 if (enc.mbcToCode(trSrc.buf, trSrc.p, trSrc.pend) == '^' && trSrc.p + cl < trSrc.pend) {
                     cflag = true;
                     trSrc.p += cl;
                 }
             }            
         }
 
         boolean singlebyte = true;
         int c;
         final int[]trans = new int[TRANS_SIZE];
         IntHash<Integer> hash = null;
         final TR trRepl = new TR(replList);
 
         if (cflag) {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = 1;
             
             while ((c = trNext(trSrc, runtime, enc)) >= 0) {
                 if (c < TRANS_SIZE) {
                     trans[c & 0xff] = -1;
                 } else {
                     if (hash == null) hash = new IntHash<Integer>();
                     hash.put(c, 1); // QTRUE
                 }
             }
             while ((c = trNext(trRepl, runtime, enc)) >= 0) {}  /* retrieve last replacer */
             int last = trRepl.now;
             for (int i=0; i<TRANS_SIZE; i++) {
                 if (trans[i] >= 0) trans[i] = last;
             }
         } else {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = -1;
             
             while ((c = trNext(trSrc, runtime, enc)) >= 0) {
                 int r = trNext(trRepl, runtime, enc);
                 if (r == -1) r = trRepl.now;
                 if (c < TRANS_SIZE) {
                     trans[c & 0xff] = r;
                     if (r > TRANS_SIZE - 1) singlebyte = false;
                 } else {
                     if (hash == null) hash = new IntHash<Integer>();
                     hash.put(c, r);
                 }
             }
         }
 
         if (cr == CR_VALID) cr = CR_7BIT;
         modifyAndKeepCodeRange();
         int s = value.getBegin();
         int send = s + value.getRealSize();
         byte sbytes[] = value.getUnsafeBytes();
         int max = value.getRealSize();
         boolean modify = false;
 
         int last = -1;
         int clen, tlen, c0;
 
         if (sflag) {
             int save = -1;
             byte[]buf = new byte[max];
             int t = 0;
             while (s < send) {
                 boolean mayModify = false;
                 c0 = c = codePoint(runtime, e1, sbytes, s, send);
                 clen = codeLength(runtime, e1, c);
                 tlen = enc == e1 ? clen : codeLength(runtime, enc, c);
                 s += clen;
                 c = trCode(c, trans, hash, cflag, last);
 
                 if (c != -1) {
                     if (save == c) {
                         if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                         continue;
                     }
                     save = c;
                     tlen = codeLength(runtime, enc, c);
                     modify = true;
                 } else {
                     save = -1;
                     c = c0;
                     if (enc != e1) mayModify = true;
                 }
 
                 while (t + tlen >= max) {
                     max <<= 1;
                     byte[]tbuf = new byte[max];
                     System.arraycopy(buf, 0, tbuf, 0, buf.length);
                     buf = tbuf;
                 }
                 enc.codeToMbc(c, buf, t);
                 if (mayModify && (tlen == 1 ? sbytes[s] != buf[t] : ByteList.memcmp(sbytes, s, buf, t, tlen) != 0)) modify = true;
                 if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                 t += tlen;
             }
             value.setUnsafeBytes(buf);
             value.setRealSize(t);
         } else if (enc.isSingleByte() || (singlebyte && hash == null)) {
             while (s < send) {
                 c = sbytes[s] & 0xff;
                 if (trans[c] != -1) {
                     if (!cflag) {
                         c = trans[c];
                         sbytes[s] = (byte)c;
                     } else {
                         sbytes[s] = (byte)last;
                     }
                     modify = true;
                 }
                 if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                 s++;
             }
         } else {
             max += max >> 1;
             byte[]buf = new byte[max];
             int t = 0;
 
             while (s < send) {
                 boolean mayModify = false;
                 c0 = c = codePoint(runtime, e1, sbytes, s, send);
                 clen = codeLength(runtime, e1, c);
                 tlen = enc == e1 ? clen : codeLength(runtime, enc, c);
 
                 c = trCode(c, trans, hash, cflag, last);
 
                 if (c != -1) {
                     tlen = codeLength(runtime, enc, c);
                     modify = true;
                 } else {
                     c = c0;
                     if (enc != e1) mayModify = true;
                 }
                 while (t + tlen >= max) {
                     max <<= 1;
                     byte[]tbuf = new byte[max];
                     System.arraycopy(buf, 0, tbuf, 0, buf.length);
                     buf = tbuf;
                 }
 
                 enc.codeToMbc(c, buf, t);
 
                 if (mayModify && (tlen == 1 ? sbytes[s] != buf[t] : ByteList.memcmp(sbytes, s, buf, t, tlen) != 0)) modify = true;
                 if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                 s += clen;
                 t += tlen;
             }
             value.setUnsafeBytes(buf);
             value.setRealSize(t);
         }
 
         if (modify) {
             if (cr != CR_BROKEN) setCodeRange(cr);
             associateEncoding(enc);
             return this;
         }
         return runtime.getNil();
     }
 
     private int trCode(int c, int[]trans, IntHash<Integer> hash, boolean cflag, int last) {
         if (c < TRANS_SIZE) {
             return trans[c];
         } else if (hash != null) {
             Integer tmp = hash.get(c);
             if (tmp == null) {
                 return cflag ? last : -1;
             } else {
                 return cflag ? -1 : tmp;
             }
         } else {
             return -1;
         }
     }
 
     /** trnext
     *
     */    
     private int trNext(TR t) {
         byte[]buf = t.buf;
         
         for (;;) {
             if (!t.gen) {
                 if (t.p == t.pend) return -1;
                 if (t.p < t.pend -1 && buf[t.p] == '\\') t.p++;
                 t.now = buf[t.p++] & 0xff;
                 if (t.p < t.pend - 1 && buf[t.p] == '-') {
                     t.p++;
                     if (t.p < t.pend) {
                         if (t.now > (buf[t.p] & 0xff)) {
                             t.p++;
                             continue;
                         }
                         t.gen = true;
                         t.max = buf[t.p++] & 0xff;
                     }
                 }
                 return t.now;
             } else if (++t.now < t.max) {
                 return t.now;
             } else {
                 t.gen = false;
                 return t.max;
             }
         }
     }
 
     private int trNext(TR t, Ruby runtime, Encoding enc) {
         byte[]buf = t.buf;
         
         for (;;) {
             if (!t.gen) {
                 if (t.p == t.pend) return -1;
                 if (t.p < t.pend -1 && buf[t.p] == '\\') t.p++;
                 t.now = codePoint(runtime, enc, buf, t.p, t.pend);
                 t.p += codeLength(runtime, enc, t.now);
                 if (t.p < t.pend - 1 && buf[t.p] == '-') {
                     t.p++;
                     if (t.p < t.pend) {
                         int c = codePoint(runtime, enc, buf, t.p, t.pend);
                         t.p += codeLength(runtime, enc, c);
                         if (t.now > c) continue;
                         t.gen = true;
                         t.max = c;
                     }
                 }
                 return t.now;
             } else if (++t.now < t.max) {
                 return t.now;
             } else {
                 t.gen = false;
                 return t.max;
             }
         }
     }
 
     /** rb_str_tr_s / rb_str_tr_s_bang
      *
      */
     @JRubyMethod(name ="tr_s", compat = CompatVersion.RUBY1_8)
     public IRubyObject tr_s(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.trTrans(context, src, repl, true);
         return str;
     }
 
     @JRubyMethod(name = "tr_s!", compat = CompatVersion.RUBY1_8)
     public IRubyObject tr_s_bang(ThreadContext context, IRubyObject src, IRubyObject repl) {
         return trTrans(context, src, repl, true);
     }
 
     @JRubyMethod(name ="tr_s", compat = CompatVersion.RUBY1_9)
     public IRubyObject tr_s19(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.trTrans19(context, src, repl, true);
         return str;
     }
 
     @JRubyMethod(name = "tr_s!", compat = CompatVersion.RUBY1_9)
     public IRubyObject tr_s_bang19(ThreadContext context, IRubyObject src, IRubyObject repl) {
         return trTrans19(context, src, repl, true);
     }
 
     /** rb_str_each_line
      *
      */
     public IRubyObject each_line(ThreadContext context, Block block) {
         return each_lineCommon(context, context.getRuntime().getGlobalVariables().get("$/"), block);
     }
 
     public IRubyObject each_line(ThreadContext context, IRubyObject arg, Block block) {
         return each_lineCommon(context, arg, block);
     }
 
     public IRubyObject each_lineCommon(ThreadContext context, IRubyObject sep, Block block) {        
         Ruby runtime = context.getRuntime();
         if (sep.isNil()) {
             block.yield(context, this);
             return this;
         }
 
         RubyString sepStr = sep.convertToString();
         ByteList sepValue = sepStr.value;
         int rslen = sepValue.getRealSize();
 
         final byte newline;
         if (rslen == 0) {
             newline = '\n';
         } else {
             newline = sepValue.getUnsafeBytes()[sepValue.getBegin() + rslen - 1];
         }
 
         int p = value.getBegin();
         int end = p + value.getRealSize();
         int ptr = p, s = p;
         int len = value.getRealSize();
         byte[] bytes = value.getUnsafeBytes();
 
         p += rslen;
 
         for (; p < end; p++) {
             if (rslen == 0 && bytes[p] == '\n') {
                 if (bytes[++p] != '\n') continue;
                 while(p < end && bytes[p] == '\n') p++;
             }
             if (ptr < p && bytes[p - 1] == newline &&
                (rslen <= 1 || 
                 ByteList.memcmp(sepValue.getUnsafeBytes(), sepValue.getBegin(), rslen, bytes, p - rslen, rslen) == 0)) {
                 block.yield(context, makeShared(runtime, s - ptr, p - s).infectBy(this));
                 modifyCheck(bytes, len);
                 s = p;
             }
         }
 
         if (s != end) {
             if (p > end) p = end;
             block.yield(context, makeShared(runtime, s - ptr, p - s).infectBy(this));
         }
 
         return this;
     }
 
     @JRubyMethod(name = "each", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject each18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "each");
     }
 
     @JRubyMethod(name = "each", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject each18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each", arg);
     }
 
     @JRubyMethod(name = "each_line", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject each_line18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     @JRubyMethod(name = "each_line", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject each_line18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each_line", arg);
     }
 
     @JRubyMethod(name = "lines", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject lines18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "lines");
     }
 
     @JRubyMethod(name = "lines", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject lines18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "lines", arg);
     }
 
     @JRubyMethod(name = "each_line", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_line19(ThreadContext context, Block block) {
         return block.isGiven() ? each_lineCommon19(context, block) : 
             enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     @JRubyMethod(name = "each_line", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_line19(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon19(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each_line", arg);
     }
 
     @JRubyMethod(name = "lines", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject lines(ThreadContext context, Block block) {
         return block.isGiven() ? each_lineCommon19(context, block) : 
             enumeratorize(context.getRuntime(), this, "lines");
     }
 
     @JRubyMethod(name = "lines", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject lines(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon19(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "lines", arg);
     }
 
     private IRubyObject each_lineCommon19(ThreadContext context, Block block) {
         return each_lineCommon19(context, context.getRuntime().getGlobalVariables().get("$/"), block);
     }
 
     private IRubyObject each_lineCommon19(ThreadContext context, IRubyObject sep, Block block) {        
         Ruby runtime = context.getRuntime();
         if (sep.isNil()) {
             block.yield(context, this);
             return this;
         }
 
         ByteList val = value.shallowDup();
         int p = val.getBegin();
         int s = p;
         int len = val.getRealSize();
         int end = p + len;
         byte[]bytes = val.getUnsafeBytes();
 
         final Encoding enc;
         RubyString sepStr = sep.convertToString();
         if (sepStr == runtime.getGlobalVariables().getDefaultSeparator()) {
             enc = val.getEncoding();
             while (p < end) {
                 if (bytes[p] == (byte)'\n') {
                     int p0 = enc.leftAdjustCharHead(bytes, s, p, end);
                     if (enc.isNewLine(bytes, p0, end)) {
                         p = p0 + StringSupport.length(enc, bytes, p0, end);
                         block.yield(context, makeShared19(runtime, val, s, p - s).infectBy(this));
                         s = p++;
                     }
                 }
                 p++;
             }
         } else {
             enc = checkEncoding(sepStr);
             ByteList sepValue = sepStr.value;
             final int newLine;
             int rslen = sepValue.getRealSize();
             if (rslen == 0) {
                 newLine = '\n';
             } else {
                 newLine = codePoint(runtime, enc, sepValue.getUnsafeBytes(), sepValue.getBegin(), sepValue.getBegin() + sepValue.getRealSize());
             }
 
             while (p < end) {
                 int c = codePoint(runtime, enc, bytes, p, end);
                 again: do {
                     int n = codeLength(runtime, enc, c);
                     if (rslen == 0 && c == newLine) {
                         p += n;
                         if (p < end && (c = codePoint(runtime, enc, bytes, p, end)) != newLine) continue again;
                         while (p < end && codePoint(runtime, enc, bytes, p, end) == newLine) p += n;
                         p -= n;
                     }
                     if (c == newLine && (rslen <= 1 ||
                             ByteList.memcmp(sepValue.getUnsafeBytes(), sepValue.getBegin(), rslen, bytes, p, rslen) == 0)) {
                         block.yield(context, makeShared19(runtime, val, s, p - s + (rslen != 0 ? rslen : n)).infectBy(this));
                         s = p + (rslen != 0 ? rslen : n);
                     }
                     p += n;
                 } while (false);
             }
         }
 
         if (s != end) {
             block.yield(context, makeShared19(runtime, val, s, end - s).infectBy(this));
         }
         return this;
     }
 
     /**
      * rb_str_each_byte
      */
     public RubyString each_byte(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         // Check the length every iteration, since
         // the block can modify this string.
         for (int i = 0; i < value.length(); i++) {
             block.yield(context, runtime.newFixnum(value.get(i) & 0xFF));
         }
         return this;
     }
 
     @JRubyMethod(name = "each_byte", frame = true)
     public IRubyObject each_byte19(ThreadContext context, Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod(name = "bytes", frame = true)
     public IRubyObject bytes(ThreadContext context, Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "bytes");
     }
 
     /** rb_str_each_char
      * 
      */
     @JRubyMethod(name = "each_char", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject each_char18(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon18(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod(name = "chars", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject chars18(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon18(context, block) : enumeratorize(context.getRuntime(), this, "chars");
     }
 
     private IRubyObject each_charCommon18(ThreadContext context, Block block) {
         byte bytes[] = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
 
         Ruby runtime = context.getRuntime();
         Encoding enc = runtime.getKCode().getEncoding();
         ByteList val = value.shallowDup();
         while (p < end) {
             int n = StringSupport.length(enc, bytes, p, end);
             block.yield(context, makeShared19(runtime, val, p-val.getBegin(), n));
             p += n;
         }
         return this;
     }
 
     @JRubyMethod(name = "each_char", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_char19(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon19(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod(name = "chars", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject chars19(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon19(context, block) : enumeratorize(context.getRuntime(), this, "chars");
     }
 
     private IRubyObject each_charCommon19(ThreadContext context, Block block) {
         byte bytes[] = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
         Encoding enc = value.getEncoding();
 
         Ruby runtime = context.getRuntime();
         ByteList val = value.shallowDup();
         while (p < end) {
             int n = StringSupport.length(enc, bytes, p, end);
             block.yield(context, makeShared19(runtime, val, p-value.getBegin(), n));
             p += n;
         }
         return this;
     }
 
     /** rb_str_each_codepoint
      * 
      */
     @JRubyMethod(name = "each_codepoint", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_codepoint(ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "each_codepoint");
         return singleByteOptimizable() ? each_byte(context, block) : each_codepointCommon(context, block);
     }
 
     @JRubyMethod(name = "codepoints", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject codepoints(ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "codepoints");
         return singleByteOptimizable() ? each_byte(context, block) : each_codepointCommon(context, block);
     }
 
     private IRubyObject each_codepointCommon(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         byte bytes[] = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
         Encoding enc = value.getEncoding();
 
         while (p < end) {
             int c = codePoint(runtime, enc, bytes, p, end);
             int n = codeLength(runtime, enc, c);
             block.yield(context, runtime.newFixnum(c));
             p += n;
         }
         return this;
     }
 
     /** rb_str_intern
      *
      */
     private RubySymbol to_sym() {
         RubySymbol symbol = getRuntime().getSymbolTable().getSymbol(value);
         if (symbol.getBytes() == value) shareLevel = SHARE_LEVEL_BYTELIST;
         return symbol;
     }
 
     @JRubyMethod(name = {"to_sym", "intern"}, compat = CompatVersion.RUBY1_8)
     public RubySymbol intern() {
         if (value.getRealSize() == 0) throw getRuntime().newArgumentError("interning empty string");
         for (int i = 0; i < value.getRealSize(); i++) {
             if (value.getUnsafeBytes()[value.getBegin() + i] == 0) throw getRuntime().newArgumentError("symbol string may not contain '\\0'");
         }
         return to_sym();
     }
 
     @JRubyMethod(name = {"to_sym", "intern"}, compat = CompatVersion.RUBY1_9)
     public RubySymbol intern19() {
         return to_sym();
     }
 
     @JRubyMethod(name = "ord", compat = CompatVersion.RUBY1_9)
     public IRubyObject ord(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return RubyFixnum.newFixnum(runtime, codePoint(runtime, value.getEncoding(), value.getUnsafeBytes(), value.getBegin(),
                                                                 value.getBegin() + value.getRealSize()));
     }
 
     @JRubyMethod(name = "sum")
     public IRubyObject sum(ThreadContext context) {
         return sumCommon(context, 16);
     }
 
     @JRubyMethod(name = "sum")
     public IRubyObject sum(ThreadContext context, IRubyObject arg) {
         return sumCommon(context, RubyNumeric.num2long(arg));
     }
 
     public IRubyObject sumCommon(ThreadContext context, long bits) {
         Ruby runtime = context.getRuntime();
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         int end = p + len; 
 
         if (bits >= 8 * 8) { // long size * bits in byte
             IRubyObject one = RubyFixnum.one(runtime);
             IRubyObject sum = RubyFixnum.zero(runtime);
             while (p < end) {
                 modifyCheck(bytes, len);
                 sum = sum.callMethod(context, "+", RubyFixnum.newFixnum(runtime, bytes[p++] & 0xff));
             }
             if (bits != 0) {
                 IRubyObject mod = one.callMethod(context, "<<", RubyFixnum.newFixnum(runtime, bits));
                 sum = sum.callMethod(context, "&", mod.callMethod(context, "-", one));
             }
             return sum;
         } else {
             long sum = 0;
             while (p < end) {
                 modifyCheck(bytes, len);
                 sum += bytes[p++] & 0xff;
             }
             return RubyFixnum.newFixnum(runtime, bits == 0 ? sum : sum & (1L << bits) - 1L);
         }
     }
 
     /** string_to_c
      * 
      */
     @JRubyMethod(name = "to_c", reads = BACKREF, writes = BACKREF, compat = CompatVersion.RUBY1_9)
     public IRubyObject to_c(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         DynamicScope scope = context.getCurrentScope();
         IRubyObject backref = scope.getBackRef(runtime);
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
 
         IRubyObject s = RuntimeHelpers.invoke(
                 context, this, "gsub",
                 RubyRegexp.newDummyRegexp(runtime, Numeric.ComplexPatterns.underscores_pat),
                 runtime.newString(new ByteList(new byte[]{'_'})));
 
         RubyArray a = RubyComplex.str_to_c_internal(context, s);
 
         scope.setBackRef(backref);
 
         if (!a.eltInternal(0).isNil()) {
             return a.eltInternal(0);
         } else {
             return RubyComplex.newComplexCanonicalize(context, RubyFixnum.zero(runtime));
         }
     }
 
     /** string_to_r
      * 
      */
     @JRubyMethod(name = "to_r", reads = BACKREF, writes = BACKREF, compat = CompatVersion.RUBY1_9)
     public IRubyObject to_r(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         DynamicScope scope = context.getCurrentScope();
         IRubyObject backref = scope.getBackRef(runtime);
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
 
         IRubyObject s = RuntimeHelpers.invoke(
                 context, this, "gsub",
                 RubyRegexp.newDummyRegexp(runtime, Numeric.ComplexPatterns.underscores_pat),
                 runtime.newString(new ByteList(new byte[]{'_'})));
 
         RubyArray a = RubyRational.str_to_r_internal(context, s);
 
         scope.setBackRef(backref);
 
         if (!a.eltInternal(0).isNil()) {
             return a.eltInternal(0);
         } else {
             return RubyRational.newRationalCanonicalize(context, RubyFixnum.zero(runtime));
         }
     }    
 
     public static RubyString unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyString result = newString(input.getRuntime(), input.unmarshalString());
         input.registerLinkTarget(result);
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#unpack
      */
     @JRubyMethod(name = "unpack")
     public RubyArray unpack(IRubyObject obj) {
         return Pack.unpack(getRuntime(), this.value, stringValue(obj).value);
     }
 
     public void empty() {
         value = ByteList.EMPTY_BYTELIST;
         shareLevel = SHARE_LEVEL_BYTELIST;
     }
 
     @JRubyMethod(name = "encoding", compat = CompatVersion.RUBY1_9)
     public IRubyObject encoding(ThreadContext context) {
         return context.getRuntime().getEncodingService().getEncoding(value.getEncoding());
     }
 
     @JRubyMethod(name = "encode!", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context) {
         modify19();
         IRubyObject defaultInternal = RubyEncoding.getDefaultInternal(context.getRuntime());
         if (!defaultInternal.isNil()) {
             encode_bang(context, defaultInternal);
         }
         return this;
     }
 
     @JRubyMethod(name = "encode!", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context, IRubyObject enc) {
         modify19();
 
         Ruby runtime = context.getRuntime();
         this.value = encodeCommon(context, runtime, this.value, enc, runtime.getNil(),
             runtime.getNil());
 
         return this;
     }
 
     @JRubyMethod(name = "encode!", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context, IRubyObject enc, IRubyObject arg) {
         modify19();
 
         Ruby runtime = context.getRuntime();
         IRubyObject fromEnc = arg;
         IRubyObject opts = runtime.getNil();
         if (arg instanceof RubyHash) {
             fromEnc = runtime.getNil();
             opts = arg;
         }
         this.value = encodeCommon(context, runtime, this.value, enc, fromEnc, opts);
 
         return this;
     }
 
     @JRubyMethod(name = "encode!", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context, IRubyObject enc, IRubyObject fromEnc, IRubyObject opts) {
         modify19();
         this.value = encodeCommon(context, context.getRuntime(), this.value, enc, fromEnc, opts);
         return this;
     }
 
     @JRubyMethod(name = "encode", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode(ThreadContext context) {
         modify19();
         Ruby runtime = context.getRuntime();
         IRubyObject defaultInternal = RubyEncoding.getDefaultInternal(runtime);
 
         if (!defaultInternal.isNil()) {
             ByteList encoded = encodeCommon(context, runtime, value, defaultInternal,
                                             runtime.getNil(), runtime.getNil());
             return runtime.newString(encoded);
         } else {
             return dup();
         }
     }
 
     @JRubyMethod(name = "encode", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc) {
         modify19();
         Ruby runtime = context.getRuntime();
 
         ByteList encoded = encodeCommon(context, runtime, value, enc, runtime.getNil(),
             runtime.getNil());
         return runtime.newString(encoded);
     }
 
     @JRubyMethod(name = "encode", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc, IRubyObject arg) {
         modify19();
         Ruby runtime = context.getRuntime();
 
         IRubyObject fromEnc = arg;
         IRubyObject opts = runtime.getNil();
         if (arg instanceof RubyHash) {
             fromEnc = runtime.getNil();
             opts = arg;
         }
         ByteList encoded = encodeCommon(context, runtime, value, enc, fromEnc, opts);
         return runtime.newString(encoded);
     }
 
     @JRubyMethod(name = "encode", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc, IRubyObject fromEnc, IRubyObject opts) {
         modify19();
         Ruby runtime = context.getRuntime();
 
         ByteList encoded = encodeCommon(context, runtime, value, enc, fromEnc, opts);
         return runtime.newString(encoded);
     }
 
     private ByteList encodeCommon(ThreadContext context, Ruby runtime, ByteList value,
             IRubyObject toEnc, IRubyObject fromEnc, IRubyObject opts) {
         Charset from = fromEnc.isNil() ? getCharset(runtime, value.getEncoding()) : getCharset(runtime, fromEnc);
 
         Encoding encoding = getEncoding(runtime, toEnc);
         Charset to = getCharset(runtime, encoding);
 
         CharsetEncoder encoder = getEncoder(context, runtime, to, opts);
 
         // decode from "from" and encode to "to"
         ByteBuffer fromBytes = ByteBuffer.wrap(value.getUnsafeBytes(), value.begin(), value.length());
         ByteBuffer toBytes;
         try {
             toBytes = encoder.encode(from.decode(fromBytes));
         } catch (CharacterCodingException e) {
             throw runtime.newInvalidByteSequenceError("");
         }
 
         // CharsetEncoder#encode guarantees a newly-allocated buffer, so
         // it's safe for us to take ownership of it without copying
         ByteList result = new ByteList(toBytes.array(), toBytes.arrayOffset(),
                 toBytes.limit() - toBytes.arrayOffset(), false);
         result.setEncoding(encoding);
         return result;
     }
 
     private CharsetEncoder getEncoder(ThreadContext context, Ruby runtime, Charset charset, IRubyObject opts) {
         CharsetEncoder encoder = charset.newEncoder();
 
         if (!opts.isNil()) {
             RubyHash hash = (RubyHash) opts;
             CodingErrorAction action = CodingErrorAction.REPLACE;
             
             IRubyObject replace = hash.fastARef(runtime.newSymbol("replace"));
             if (replace != null && !replace.isNil()) {
                 String replaceWith = replace.toString();
                 if (replaceWith.length() > 0) {
                     encoder.replaceWith(replaceWith.getBytes());
                 } else {
                     action = CodingErrorAction.IGNORE;
                 }
             }
             
             IRubyObject invalid = hash.fastARef(runtime.newSymbol("invalid"));
             if (invalid != null && invalid.op_equal(context, runtime.newSymbol("replace")).isTrue()) {
                 encoder.onMalformedInput(action);
             }
 
             IRubyObject undef = hash.fastARef(runtime.newSymbol("undef"));
             if (undef != null && undef.op_equal(context, runtime.newSymbol("replace")).isTrue()) {
                 encoder.onUnmappableCharacter(action);
             }
 
 //            FIXME: Parse the option :xml
 //            The value must be +:text+ or +:attr+. If the
 //            value is +:text+ +#encode+ replaces undefined
 //            characters with their (upper-case hexadecimal)
 //            numeric character references. '&', '<', and
 //            '>' are converted to "&amp;", "&lt;", and
 //            "&gt;", respectively. If the value is +:attr+,
 //            +#encode+ also quotes the replacement result
 //            (using '"'), and replaces '"' with "&quot;".
         }
 
         return encoder;
     }
 
     private Encoding getEncoding(Ruby runtime, IRubyObject toEnc) {
         try {
             return RubyEncoding.getEncodingFromObject(runtime, toEnc);
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + toEnc.toString() + ")");
         }
     }
 
     private Charset getCharset(Ruby runtime, IRubyObject toEnc) {
         try {
             Encoding encoding = RubyEncoding.getEncodingFromObject(runtime, toEnc);
 
             return getCharset(runtime, encoding);
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + toEnc.toString() + ")");
         }
     }
 
     private Charset getCharset(Ruby runtime, Encoding encoding) {
         try {
             // special-casing ASCII* to ASCII
             return encoding.toString().startsWith("ASCII") ?
                 Charset.forName("ASCII") :
                 Charset.forName(encoding.toString());
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + encoding.toString() + ")");
         }
     }
 
     @JRubyMethod(name = "force_encoding", compat = CompatVersion.RUBY1_9)
     public IRubyObject force_encoding(ThreadContext context, IRubyObject enc) {
         modify19();
         Encoding encoding = RubyEncoding.getEncodingFromObject(context.getRuntime(), enc);
         associateEncoding(encoding);
         return this;
     }
 
     @JRubyMethod(name = "valid_encoding?", compat = CompatVersion.RUBY1_9)
     public IRubyObject valid_encoding_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return scanForCodeRange() == CR_BROKEN ? runtime.getFalse() : runtime.getTrue();
     }
 
     @JRubyMethod(name = "ascii_only?", compat = CompatVersion.RUBY1_9)
     public IRubyObject ascii_only_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return scanForCodeRange() == CR_7BIT ? runtime.getTrue() : runtime.getFalse();
     }
 
     /**
      * Mutator for internal string representation.
      *
      * @param value The new java.lang.String this RubyString should encapsulate
      * @deprecated
      */
     public void setValue(CharSequence value) {
         view(ByteList.plain(value));
     }
 
     public void setValue(ByteList value) {
         view(value);
     }
 
     public CharSequence getValue() {
         return toString();
     }
 
     public byte[] getBytes() {
         return value.bytes();
     }
 
     public ByteList getByteList() {
         return value;
     }
 
     /** used by ar-jdbc
      * 
      */
     public String getUnicodeValue() {
-        try {
-            return new String(value.getUnsafeBytes(), value.getBegin(), value.getRealSize(), "UTF-8");
-        } catch (Exception e) {
-            throw new RuntimeException("Something's seriously broken with encodings", e);
-        }
+        return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.getBegin(), value.getRealSize());
     }
 
     @Override
     public Object toJava(Class target) {
         if (target.isAssignableFrom(String.class)) {
             try {
                 // 1.9 support for encodings
+                // TODO: Fix charset use for JRUBY-4553
                 if (getRuntime().is1_9()) {
                     return new String(value.getUnsafeBytes(), value.begin(), value.length(), getEncoding().toString());
                 }
 
-                return new String(value.getUnsafeBytes(), value.begin(), value.length(), "UTF8");
+                return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.begin(), value.length());
             } catch (UnsupportedEncodingException uee) {
                 return toString();
             }
         } else if (target.isAssignableFrom(ByteList.class)) {
             return value;
         } else {
             throw getRuntime().newTypeError("cannot convert instance of String to " + target);
         }
     }
 
     /**
      * Variable-arity versions for compatibility. Not bound to Ruby.
      * @deprecated Use the versions with zero or one arguments
      */
 
     @Deprecated
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         switch (args.length) {
         case 0: return this;
         case 1: return initialize(args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject sub(ThreadContext context, IRubyObject[] args, Block block) {
         RubyString str = strDup(context.getRuntime());
         str.sub_bang(context, args, block);
         return str;
     }
 
     @Deprecated
     public IRubyObject sub_bang(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1: return sub_bang(context, args[0], block);
         case 2: return sub_bang(context, args[0], args[1], block);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject gsub(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1: return gsub(context, args[0], block);
         case 2: return gsub(context, args[0], args[1], block);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject gsub_bang(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1: return gsub_bang(context, args[0], block);
         case 2: return gsub_bang(context, args[0], args[1], block);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject index(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return index(context, args[0]);
         case 2: return index(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject rindex(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return rindex(context, args[0]);
         case 2: return rindex(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject op_aref(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return op_aref(context, args[0]);
         case 2: return op_aref(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject op_aset(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 2: return op_aset(context, args[0], args[1]);
         case 3: return op_aset(context, args[0], args[1], args[2]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 2, 3); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject slice_bang(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return slice_bang(context, args[0]);
         case 2: return slice_bang(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject to_i(IRubyObject[] args) {
         switch (args.length) {
         case 0: return to_i();
         case 1: return to_i(args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 
     @Deprecated
     public RubyArray split(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 0: return split(context);
         case 1: return split(context, args[0]);
         case 2: return split(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 0, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject ljust(IRubyObject [] args) {
         switch (args.length) {
         case 1: return ljust(args[0]);
         case 2: return ljust(args[0], args[1]);
         default: Arity.raiseArgumentError(getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject rjust(IRubyObject [] args) {
         switch (args.length) {
         case 1: return rjust(args[0]);
         case 2: return rjust(args[0], args[1]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject center(IRubyObject [] args) {
         switch (args.length) {
         case 1: return center(args[0]);
         case 2: return center(args[0], args[1]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public RubyString chomp(IRubyObject[] args) {
         switch (args.length) {
         case 0:return chomp(getRuntime().getCurrentContext());
         case 1:return chomp(getRuntime().getCurrentContext(), args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject chomp_bang(IRubyObject[] args) {
         switch (args.length) {
         case 0: return chomp_bang(getRuntime().getCurrentContext());
         case 1: return chomp_bang(getRuntime().getCurrentContext(), args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 }
diff --git a/src/org/jruby/compiler/JITCompiler.java b/src/org/jruby/compiler/JITCompiler.java
index fbfa1d6e37..2c19f05599 100644
--- a/src/org/jruby/compiler/JITCompiler.java
+++ b/src/org/jruby/compiler/JITCompiler.java
@@ -1,443 +1,440 @@
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
  * Copyright (C) 2006-2008 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2008 Thomas E Enebo <enebo@acm.org>
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
 
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.UnsupportedEncodingException;
 import java.nio.charset.Charset;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.Locale;
 import java.util.Set;
 import java.util.concurrent.atomic.AtomicLong;
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.MetaClass;
+import org.jruby.RubyEncoding;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.ast.util.SexpMaker;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.util.ClassCache;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.SafePropertyAccessor;
 import org.objectweb.asm.ClassReader;
 
 public class JITCompiler implements JITCompilerMBean {
     public static final boolean USE_CACHE = true;
     public static final boolean DEBUG = SafePropertyAccessor.getBoolean("jruby.jit.debug", false);
     
     private AtomicLong compiledCount = new AtomicLong(0);
     private AtomicLong successCount = new AtomicLong(0);
     private AtomicLong failCount = new AtomicLong(0);
     private AtomicLong abandonCount = new AtomicLong(0);
     private AtomicLong compileTime = new AtomicLong(0);
     private AtomicLong averageCompileTime = new AtomicLong(0);
     private AtomicLong codeSize = new AtomicLong(0);
     private AtomicLong averageCodeSize = new AtomicLong(0);
     private AtomicLong largestCodeSize = new AtomicLong(0);
     
     public JITCompiler(Ruby ruby) {
         ruby.getBeanManager().register(this);
     }
 
     public DynamicMethod tryJIT(final DefaultMethod method, final ThreadContext context, final String name) {
         if (context.getRuntime().getInstanceConfig().getCompileMode().shouldJIT()) {
             return jitIsEnabled(method, context, name);
         }
 
         return null;
     }
     
     @Deprecated
     public void runJIT(final DefaultMethod method, final ThreadContext context, final String name) {
         // This method has JITed already or has been abandoned. Bail out.
         if (method.getCallCount() < 0) {
             return;
         } else {
             jitIsEnabled(method, context, name);
         }
     }
 
     private DynamicMethod jitIsEnabled(final DefaultMethod method, final ThreadContext context, final String name) {
         RubyInstanceConfig instanceConfig = context.getRuntime().getInstanceConfig();
         
         if (method.incrementCallCount() >= instanceConfig.getJitThreshold()) {
             return jitThresholdReached(method, instanceConfig, context, name);
         }
 
         return null;
     }
     
     private DynamicMethod jitThresholdReached(final DefaultMethod method, RubyInstanceConfig instanceConfig, final ThreadContext context, final String name) {
         try {
             // The cache is full. Abandon JIT for this method and bail out.
             ClassCache classCache = instanceConfig.getClassCache();
             if (classCache.isFull()) {
                 abandonCount.incrementAndGet();
                 method.setCallCount(-1);
                 return null;
             }
 
             // Check if the method has been explicitly excluded
             String moduleName = method.getImplementationClass().getName();
             if(instanceConfig.getExcludedMethods().size() > 0) {
                 String excludeModuleName = moduleName;
                 if(method.getImplementationClass().isSingleton()) {
                     IRubyObject possibleRealClass = ((MetaClass)method.getImplementationClass()).getAttached();
                     if(possibleRealClass instanceof RubyModule) {
                         excludeModuleName = "Meta:" + ((RubyModule)possibleRealClass).getName();
                     }
                 }
 
                 if ((instanceConfig.getExcludedMethods().contains(excludeModuleName) ||
                      instanceConfig.getExcludedMethods().contains(excludeModuleName +"#"+name) ||
                      instanceConfig.getExcludedMethods().contains(name))) {
                     method.setCallCount(-1);
                     return null;
                 }
             }
 
             String key = SexpMaker.create(name, method.getArgsNode(), method.getBodyNode());
             JITClassGenerator generator = new JITClassGenerator(name, key, context.getRuntime(), method, context);
 
             Class<Script> sourceClass = (Class<Script>)instanceConfig.getClassCache().cacheClassByKey(key, generator);
 
             if (sourceClass == null) {
                 // class could not be found nor generated; give up on JIT and bail out
                 failCount.incrementAndGet();
                 method.setCallCount(-1);
                 return null;
             }
 
             // successfully got back a jitted method
             successCount.incrementAndGet();
 
             // finally, grab the script
             Script jitCompiledScript = sourceClass.newInstance();
 
             // add to the jitted methods set
             Set<Script> jittedMethods = context.getRuntime().getJittedMethods();
             jittedMethods.add(jitCompiledScript);
 
             // logEvery n methods based on configuration
             if (instanceConfig.getJitLogEvery() > 0) {
                 int methodCount = jittedMethods.size();
                 if (methodCount % instanceConfig.getJitLogEvery() == 0) {
                     log(method, name, "live compiled methods: " + methodCount);
                 }
             }
 
             if (instanceConfig.isJitLogging()) log(method, name, "done jitting");
 
             method.switchToJitted(jitCompiledScript, generator.callConfig());
             return null;
         } catch (Throwable t) {
             if (context.getRuntime().getDebug().isTrue()) t.printStackTrace();
             if (instanceConfig.isJitLoggingVerbose()) log(method, name, "could not compile", t.getMessage());
 
             failCount.incrementAndGet();
             method.setCallCount(-1);
             return null;
         }
     }
 
     public static String getHashForString(String str) {
-        try {
-            return getHashForBytes(str.getBytes("UTF-8"));
-        } catch (UnsupportedEncodingException uee) {
-            throw new RuntimeException(uee);
-        }
+        return getHashForBytes(RubyEncoding.encodeUTF8(str));
     }
 
     public static String getHashForBytes(byte[] bytes) {
         try {
             MessageDigest sha1 = MessageDigest.getInstance("SHA1");
             sha1.update(bytes);
             byte[] digest = sha1.digest();
             char[] digestChars = new char[digest.length * 2];
             for (int i = 0; i < digest.length; i++) {
                 digestChars[i * 2] = Character.forDigit(digest[i] & 0xF, 16);
                 digestChars[i * 2 + 1] = Character.forDigit((digest[i] & 0xF0) >> 4, 16);
             }
             return new String(digestChars).toUpperCase(Locale.ENGLISH);
         } catch (NoSuchAlgorithmException nsae) {
             throw new RuntimeException(nsae);
         }
     }
 
     public static void saveToCodeCache(Ruby ruby, byte[] bytecode, String packageName, File cachedClassFile) {
         String codeCache = RubyInstanceConfig.JIT_CODE_CACHE;
         File codeCacheDir = new File(codeCache);
         if (!codeCacheDir.exists()) {
             ruby.getWarnings().warn("jruby.jit.codeCache directory " + codeCacheDir + " does not exist");
         } else if (!codeCacheDir.isDirectory()) {
             ruby.getWarnings().warn("jruby.jit.codeCache directory " + codeCacheDir + " is not a directory");
         } else if (!codeCacheDir.canWrite()) {
             ruby.getWarnings().warn("jruby.jit.codeCache directory " + codeCacheDir + " is not writable");
         } else {
             if (!new File(codeCache, packageName).isDirectory()) {
                 boolean createdDirs = new File(codeCache, packageName).mkdirs();
                 if (!createdDirs) {
                     ruby.getWarnings().warn("could not create JIT cache dir: " + new File(codeCache, packageName));
                 }
             }
             // write to code cache
             FileOutputStream fos = null;
             try {
                 if (DEBUG) System.err.println("writing jitted code to to " + cachedClassFile);
                 fos = new FileOutputStream(cachedClassFile);
                 fos.write(bytecode);
             } catch (Exception e) {
                 e.printStackTrace();
                 // ignore
             } finally {
                 try {fos.close();} catch (Exception e) {}
             }
         }
     }
     
     public class JITClassGenerator implements ClassCache.ClassGenerator {
         private StandardASMCompiler asmCompiler;
         private StaticScope staticScope;
         private Node bodyNode;
         private ArgsNode argsNode;
         private CallConfiguration jitCallConfig;
         private String digestString;
         
         private byte[] bytecode;
         private String name;
         private Ruby ruby;
         private String packageName;
         private String className;
         private String filename;
         private String methodName;
         
         public JITClassGenerator(String name, String key, Ruby ruby, DefaultMethod method, ThreadContext context) {
             this.packageName = "ruby/jit";
             this.digestString = getHashForString(key);
             this.className = packageName + "/" + JavaNameMangler.mangleStringForCleanJavaIdentifier(name) + "_" + digestString;
             this.name = className.replaceAll("/", ".");
             this.bodyNode = method.getBodyNode();
             this.argsNode = method.getArgsNode();
             this.methodName = name;
             filename = calculateFilename(argsNode, bodyNode);
             staticScope = method.getStaticScope();
             asmCompiler = new StandardASMCompiler(className, filename);
             this.ruby = ruby;
         }
         
         @SuppressWarnings("unchecked")
         protected void compile() {
             if (bytecode != null) return;
             
             // check if we have a cached compiled version on disk
             String codeCache = RubyInstanceConfig.JIT_CODE_CACHE;
             File cachedClassFile = new File(codeCache + "/" + className + ".class");
 
             if (codeCache != null &&
                     cachedClassFile.exists()) {
                 FileInputStream fis = null;
                 try {
                     if (DEBUG) System.err.println("loading cached code from: " + cachedClassFile);
                     fis = new FileInputStream(cachedClassFile);
                     bytecode = new byte[(int)fis.getChannel().size()];
                     fis.read(bytecode);
                     name = new ClassReader(bytecode).getClassName();
                     return;
                 } catch (Exception e) {
                     // ignore and proceed to compile
                 } finally {
                     try {fis.close();} catch (Exception e) {}
                 }
             }
             
             // Time the compilation
             long start = System.nanoTime();
 
             asmCompiler = new StandardASMCompiler(className, filename);
             asmCompiler.startScript(staticScope);
             final ASTCompiler compiler = ruby.getInstanceConfig().newCompiler();
 
             CompilerCallback args = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     compiler.compileArgs(argsNode, context, true);
                 }
             };
 
             ASTInspector inspector = new ASTInspector();
             // check args first, since body inspection can depend on args
             inspector.inspect(argsNode);
             inspector.inspect(bodyNode);
 
             BodyCompiler methodCompiler;
             if (bodyNode != null) {
                 // we have a body, do a full-on method
                 methodCompiler = asmCompiler.startFileMethod(args, staticScope, inspector);
                 compiler.compile(bodyNode, methodCompiler,true);
             } else {
                 // If we don't have a body, check for required or opt args
                 // if opt args, they could have side effects
                 // if required args, need to raise errors if too few args passed
                 // otherwise, method does nothing, make it a nop
                 if (argsNode != null && (argsNode.getRequiredArgsCount() > 0 || argsNode.getOptionalArgsCount() > 0)) {
                     methodCompiler = asmCompiler.startFileMethod(args, staticScope, inspector);
                     methodCompiler.loadNil();
                 } else {
                     methodCompiler = asmCompiler.startFileMethod(null, staticScope, inspector);
                     methodCompiler.loadNil();
                     jitCallConfig = CallConfiguration.FrameNoneScopeNone;
                 }
             }
             methodCompiler.endBody();
             asmCompiler.endScript(false, false);
             
             // if we haven't already decided on a do-nothing call
             if (jitCallConfig == null) {
                 jitCallConfig = inspector.getCallConfig();
             }
             
             bytecode = asmCompiler.getClassByteArray();
             
             if (bytecode.length > ruby.getInstanceConfig().getJitMaxSize()) {
                 bytecode = null;
                 throw new NotCompilableException(
                         "JITed method size exceeds configured max of " +
                         ruby.getInstanceConfig().getJitMaxSize());
             }
 
             if (codeCache != null) {
                 JITCompiler.saveToCodeCache(ruby, bytecode, packageName, cachedClassFile);
             }
             
             compiledCount.incrementAndGet();
             compileTime.addAndGet(System.nanoTime() - start);
             codeSize.addAndGet(bytecode.length);
             averageCompileTime.set(compileTime.get() / compiledCount.get());
             averageCodeSize.set(codeSize.get() / compiledCount.get());
             synchronized (largestCodeSize) {
                 if (largestCodeSize.get() < bytecode.length) {
                     largestCodeSize.set(bytecode.length);
                 }
             }
         }
 
         public void generate() {
             compile();
         }
         
         public byte[] bytecode() {
             return bytecode;
         }
 
         public String name() {
             return name;
         }
         
         public CallConfiguration callConfig() {
             compile();
             return jitCallConfig;
         }
 
         public String toString() {
             return methodName + "() at " + bodyNode.getPosition().getFile() + ":" + bodyNode.getPosition().getLine();
         }
     }
     
     private static String calculateFilename(ArgsNode argsNode, Node bodyNode) {
         if (bodyNode != null) return bodyNode.getPosition().getFile();
         if (argsNode != null) return argsNode.getPosition().getFile();
         
         return "__eval__";
     }
 
     static void log(DefaultMethod method, String name, String message, String... reason) {
         String className = method.getImplementationClass().getBaseName();
         
         if (className == null) className = "<anon class>";
 
         System.err.print(message + ":" + className + "." + name);
         
         if (reason.length > 0) {
             System.err.print(" because of: \"");
             for (int i = 0; i < reason.length; i++) {
                 System.err.print(reason[i]);
             }
             System.err.print('"');
         }
         
         System.err.println("");
     }
 
     public long getSuccessCount() {
         return successCount.get();
     }
 
     public long getCompileCount() {
         return compiledCount.get();
     }
 
     public long getFailCount() {
         return failCount.get();
     }
 
     public long getCompileTime() {
         return compileTime.get() / 1000;
     }
 
     public long getAbandonCount() {
         return abandonCount.get();
     }
     
     public long getCodeSize() {
         return codeSize.get();
     }
     
     public long getAverageCodeSize() {
         return averageCodeSize.get();
     }
     
     public long getAverageCompileTime() {
         return averageCompileTime.get() / 1000;
     }
     
     public long getLargestCodeSize() {
         return largestCodeSize.get();
     }
 }
diff --git a/src/org/jruby/demo/TextAreaReadline.java b/src/org/jruby/demo/TextAreaReadline.java
index f950f391aa..78504e776f 100644
--- a/src/org/jruby/demo/TextAreaReadline.java
+++ b/src/org/jruby/demo/TextAreaReadline.java
@@ -1,620 +1,608 @@
 package org.jruby.demo;
 
 import java.awt.Color;
 import java.awt.Point;
 import java.awt.EventQueue;
 import java.awt.event.KeyEvent;
 import java.awt.event.KeyListener;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import javax.swing.DefaultListCellRenderer;
 import javax.swing.JComboBox;
 import javax.swing.plaf.basic.BasicComboPopup;
 import javax.swing.text.AbstractDocument;
 import javax.swing.text.AttributeSet;
 import javax.swing.text.BadLocationException;
 import javax.swing.text.Document;
 import javax.swing.text.DocumentFilter;
 import javax.swing.text.JTextComponent;
 import javax.swing.text.MutableAttributeSet;
 import javax.swing.text.SimpleAttributeSet;
 import javax.swing.text.StyleConstants;
 
 import org.jruby.Ruby;
+import org.jruby.RubyEncoding;
 import org.jruby.RubyIO;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.ext.Readline;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.Join;
 
 public class TextAreaReadline implements KeyListener {
     private static final String EMPTY_LINE = "";
     
     private JTextComponent area;
     private volatile int startPos;
     private String currentLine;
     
     public volatile MutableAttributeSet promptStyle;
     public volatile MutableAttributeSet inputStyle;
     public volatile MutableAttributeSet outputStyle;
     public volatile MutableAttributeSet resultStyle;
     
     private JComboBox completeCombo;
     private BasicComboPopup completePopup;
     private int start;
     private int end;
 
     private final InputStream inputStream = new Input();
     private final OutputStream outputStream = new Output();
 
     private static class InputBuffer {
         public final byte[] bytes;
         public int offset = 0;
         public InputBuffer(byte[] bytes) {
             this.bytes = bytes;
         }
     }
 
     public enum Channel {
         AVAILABLE,
         READ,
         BUFFER,
         EMPTY,
         LINE,
         GET_LINE,
         SHUTDOWN,
         FINISHED
     }
 
     private static class ReadRequest {
         public final byte[] b;
         public final int off;
         public final int len;
 
         public ReadRequest(byte[] b, int off, int len) {
             this.b = b;
             this.off = off;
             this.len = len;
         }
 
         public int perform(Join join, InputBuffer buffer) {
             final int available = buffer.bytes.length - buffer.offset;
             int len = this.len;
             if ( len > available ) {
                 len = available;
             }
             if ( len == available ) {
                 join.send(Channel.EMPTY, null);
             } else {
                 buffer.offset += len;
                 join.send(Channel.BUFFER, buffer);
             }
             System.arraycopy(buffer.bytes, buffer.offset, this.b, this.off, len);
             return len;
         }
     }
 
     private static final Join.Spec INPUT_SPEC = new Join.Spec() {{
         addReaction(new Join.FastReaction(Channel.SHUTDOWN, Channel.BUFFER) {
             public void react(Join join, Object[] args) {
                 join.send(Channel.FINISHED, null);
             }
         });
         addReaction(new Join.FastReaction(Channel.SHUTDOWN, Channel.EMPTY) {
             public void react(Join join, Object[] args) {
                 join.send(Channel.FINISHED, null);
             }
         });
         addReaction(new Join.FastReaction(Channel.SHUTDOWN, Channel.FINISHED) {
             public void react(Join join, Object[] args) {
                 join.send(Channel.FINISHED, null);
             }
         });
 
         addReaction(new Join.FastReaction(Channel.FINISHED, Channel.LINE) {
             public void react(Join join, Object[] args) {
                 join.send(Channel.FINISHED, null);
             }
         });
 
         addReaction(new Join.SyncReaction(Channel.AVAILABLE, Channel.BUFFER) {
             public Object react(Join join, Object[] args) {
                 InputBuffer buffer = (InputBuffer)args[1];
                 join.send(Channel.BUFFER, buffer);
                 return buffer.bytes.length - buffer.offset;
             }
         });
         addReaction(new Join.SyncReaction(Channel.AVAILABLE, Channel.EMPTY) {
             public Object react(Join join, Object[] args) {
                 join.send(Channel.EMPTY, null);
                 return 0;
             }
         });
         addReaction(new Join.SyncReaction(Channel.AVAILABLE, Channel.FINISHED) {
             public Object react(Join join, Object[] args) {
                 join.send(Channel.FINISHED, null);
                 return 0;
             }
         });
 
         addReaction(new Join.SyncReaction(Channel.READ, Channel.BUFFER) {
             public Object react(Join join, Object[] args) {
                 return ((ReadRequest)args[0]).perform(join, (InputBuffer)args[1]);
             }
         });
         addReaction(new Join.SyncReaction(Channel.READ, Channel.EMPTY, Channel.LINE) {
             public Object react(Join join, Object[] args) {
                 final ReadRequest request = (ReadRequest)args[0];
                 final String line = (String)args[2];
                 if (line.length() != 0) {
-                    byte[] bytes;
-                    try {
-                        bytes = line.getBytes("UTF-8");
-                    } catch (UnsupportedEncodingException e) {
-                        bytes = line.getBytes();
-                    }
+                    byte[] bytes = RubyEncoding.encodeUTF8(line);
                     return request.perform(join, new InputBuffer(bytes));
                 } else {
                     return -1;
                 }
             }
         });
         addReaction(new Join.SyncReaction(Channel.READ, Channel.FINISHED) {
             public Object react(Join join, Object[] args) {
                 join.send(Channel.FINISHED, null);
                 return -1;
             }
         });
 
         addReaction(new Join.SyncReaction(Channel.GET_LINE, Channel.LINE) {
             public Object react(Join join, Object[] args) {
                 return args[1];
             }
         });
         addReaction(new Join.SyncReaction(Channel.GET_LINE, Channel.FINISHED) {
             public Object react(Join join, Object[] args) {
                 join.send(Channel.FINISHED, null);
                 return EMPTY_LINE;
             }
         });
     }};
 
     private static final int MAX_DOC_SIZE = 100000;
     private final Join inputJoin = INPUT_SPEC.createJoin();
 
     public TextAreaReadline(JTextComponent area) {
         this(area, null);
     }
     
     public TextAreaReadline(JTextComponent area, final String message) {
         this.area = area;
 
         inputJoin.send(Channel.EMPTY, null);
         
         area.addKeyListener(this);
         
         // No editing before startPos
         if (area.getDocument() instanceof AbstractDocument)
             ((AbstractDocument) area.getDocument()).setDocumentFilter(
                 new DocumentFilter() {
                     public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                         if (offset >= startPos) super.insertString(fb, offset, string, attr);
                     }
                     
                     public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
                         if (offset >= startPos || offset == 0) super.remove(fb, offset, length);
                     }
                     
                     public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                         if (offset >= startPos) super.replace(fb, offset, length, text, attrs);
                     }
                 }
             );
         
         promptStyle = new SimpleAttributeSet();
         StyleConstants.setForeground(promptStyle, new Color(0xa4, 0x00, 0x00));
         
         inputStyle = new SimpleAttributeSet();
         StyleConstants.setForeground(inputStyle, new Color(0x20, 0x4a, 0x87));
         
         outputStyle = new SimpleAttributeSet();
         StyleConstants.setForeground(outputStyle, Color.darkGray);
         
         resultStyle = new SimpleAttributeSet();
         StyleConstants.setItalic(resultStyle, true);
         StyleConstants.setForeground(resultStyle, new Color(0x20, 0x4a, 0x87));
         
         completeCombo = new JComboBox();
         completeCombo.setRenderer(new DefaultListCellRenderer()); // no silly ticks!
         completePopup = new BasicComboPopup(completeCombo);
         
         if (message != null) {
             final MutableAttributeSet messageStyle = new SimpleAttributeSet();
             StyleConstants.setBackground(messageStyle, area.getForeground());
             StyleConstants.setForeground(messageStyle, area.getBackground());
             append(message, messageStyle);
         }
 
         startPos = area.getDocument().getLength();
     }
 
     public InputStream getInputStream() {
         return inputStream;
     }
 
     public OutputStream getOutputStream() {
         return outputStream;
     }
     
     private Ruby runtime;
 
     /**
      * Hooks this <code>TextAreaReadline</code> instance into the
      * runtime, redefining the <code>Readline</code> module so that
      * it uses this object. This method does not redefine the standard
      * input-output streams. If you need that, use
      * {@link #hookIntoRuntimeWithStreams(Ruby)}.
      *
      * @param runtime
      *                The runtime.
      * @see #hookIntoRuntimeWithStreams(Ruby)
      */
     public void hookIntoRuntime(final Ruby runtime) {
         this.runtime = runtime;
         /* Hack in to replace usual readline with this */
         runtime.getLoadService().require("readline");
         RubyModule readlineM = runtime.fastGetModule("Readline");
 
         readlineM.defineModuleFunction("readline", new Callback() {
             public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
                 String line = readLine(args[0].toString());
                 if (line != null) {
                     return RubyString.newUnicodeString(runtime, line);
                 } else {
                     return runtime.getNil();
                 }
             }
             public Arity getArity() { return Arity.twoArguments(); }
         });
     }
 
     /**
      * Hooks this <code>TextAreaReadline</code> instance into the
      * runtime, redefining the <code>Readline</code> module so that
      * it uses this object. This method also redefines the standard
      * input-output streams accordingly.
      *
      * @param runtime
      *                The runtime.
      * @see #hookIntoRuntime(Ruby)
      */
     public void hookIntoRuntimeWithStreams(final Ruby runtime) {
         hookIntoRuntime(runtime);
 
         RubyIO in = new RubyIO(runtime, getInputStream());
         runtime.getGlobalVariables().set("$stdin", in);
 
         RubyIO out = new RubyIO(runtime, getOutputStream());
         runtime.getGlobalVariables().set("$stdout", out);
         runtime.getGlobalVariables().set("$stderr", out);
     }
     
     protected void completeAction(KeyEvent event) {
         if (Readline.getCompletor(Readline.getHolder(runtime)) == null) return;
         
         event.consume();
         
         if (completePopup.isVisible()) return;
         
         List candidates = new LinkedList();
         String bufstr = null;
         try {
             bufstr = area.getText(startPos, area.getCaretPosition() - startPos);
         } catch (BadLocationException e) {
             return;
         }
         
         int cursor = area.getCaretPosition() - startPos;
         
         int position = Readline.getCompletor(Readline.getHolder(runtime)).complete(bufstr, cursor, candidates);
         
         // no candidates? Fail.
         if (candidates.isEmpty())
             return;
         
         if (candidates.size() == 1) {
             replaceText(startPos + position, area.getCaretPosition(), (String) candidates.get(0));
             return;
         }
         
         start = startPos + position;
         end = area.getCaretPosition();
         
         Point pos = area.getCaret().getMagicCaretPosition();
 
         // bit risky if someone changes completor, but useful for method calls
         int cutoff = bufstr.substring(position).lastIndexOf('.') + 1;
         start += cutoff;
 
         if (candidates.size() < 10)
             completePopup.getList().setVisibleRowCount(candidates.size());
         else
             completePopup.getList().setVisibleRowCount(10);
 
         completeCombo.removeAllItems();
         for (Iterator i = candidates.iterator(); i.hasNext();) {
             String item = (String) i.next();
             if (cutoff != 0) item = item.substring(cutoff);
             completeCombo.addItem(item);
         }
 
         completePopup.show(area, pos.x, pos.y + area.getFontMetrics(area.getFont()).getHeight());
     }
 
     protected void backAction(KeyEvent event) {
         if (area.getCaretPosition() <= startPos)
             event.consume();
     }
     
     protected void upAction(KeyEvent event) {
         event.consume();
         
         if (completePopup.isVisible()) {
             int selected = completeCombo.getSelectedIndex() - 1;
             if (selected < 0) return;
             completeCombo.setSelectedIndex(selected);
             return;
         }
         
         if (!Readline.getHistory(Readline.getHolder(runtime)).next()) // at end
             currentLine = getLine();
         else
             Readline.getHistory(Readline.getHolder(runtime)).previous(); // undo check
         
         if (!Readline.getHistory(Readline.getHolder(runtime)).previous()) return;
         
         String oldLine = Readline.getHistory(Readline.getHolder(runtime)).current().trim();
         replaceText(startPos, area.getDocument().getLength(), oldLine);
     }
     
     protected void downAction(KeyEvent event) {
         event.consume();
         
         if (completePopup.isVisible()) {
             int selected = completeCombo.getSelectedIndex() + 1;
             if (selected == completeCombo.getItemCount()) return;
             completeCombo.setSelectedIndex(selected);
             return;
         }
         
         if (!Readline.getHistory(Readline.getHolder(runtime)).next()) return;
         
         String oldLine;
         if (!Readline.getHistory(Readline.getHolder(runtime)).next()) // at end
             oldLine = currentLine;
         else {
             Readline.getHistory(Readline.getHolder(runtime)).previous(); // undo check
             oldLine = Readline.getHistory(Readline.getHolder(runtime)).current().trim();
         }
         
         replaceText(startPos, area.getDocument().getLength(), oldLine);
     }
     
     protected void replaceText(int start, int end, String replacement) {
         try {
             area.getDocument().remove(start, end - start);
             area.getDocument().insertString(start, replacement, inputStyle);
         } catch (BadLocationException e) {
             e.printStackTrace();
         }
     }
     
     protected String getLine() {
         try {
             return area.getText(startPos, area.getDocument().getLength() - startPos);
         } catch (BadLocationException e) {
             e.printStackTrace();
         }
         return null;
     }
     
     protected void enterAction(KeyEvent event) {
         event.consume();
         
         if (completePopup.isVisible()) {
             if (completeCombo.getSelectedItem() != null)
                 replaceText(start, end, (String) completeCombo.getSelectedItem());
             completePopup.setVisible(false);
             return;
         }
         
         append("\n", null);
         
         String line = getLine();
         startPos = area.getDocument().getLength();
         inputJoin.send(Channel.LINE, line);
     }
     
     public String readLine(final String prompt) {
         if (EventQueue.isDispatchThread()) {
             throw runtime.newThreadError("Cannot call readline from event dispatch thread");
         }
 
         EventQueue.invokeLater(new Runnable() {
            public void run() {
                append(prompt.trim(), promptStyle);
                append(" ", inputStyle); // hack to get right style for input
                area.setCaretPosition(area.getDocument().getLength());
                startPos = area.getDocument().getLength();
                Readline.getHistory(Readline.getHolder(runtime)).moveToEnd();
             }
         });
         
         final String line = (String)inputJoin.call(Channel.GET_LINE, null);
         if (line.length() > 0) {
             return line.trim();
         } else {
             return null;
         }
     }
     
     public void keyPressed(KeyEvent event) {
         int code = event.getKeyCode();
         switch (code) {
         case KeyEvent.VK_TAB: completeAction(event); break;
         case KeyEvent.VK_LEFT: 
         case KeyEvent.VK_BACK_SPACE:
             backAction(event); break;
         case KeyEvent.VK_UP: upAction(event); break;
         case KeyEvent.VK_DOWN: downAction(event); break;
         case KeyEvent.VK_ENTER: enterAction(event); break;
         case KeyEvent.VK_HOME: event.consume(); area.setCaretPosition(startPos); break;
         case KeyEvent.VK_D:
             if ( ( event.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK ) != 0 ) {
                 event.consume();
                 inputJoin.send(Channel.LINE, EMPTY_LINE);
             }
             break;
         }
         
         if (completePopup.isVisible() &&
                 code !=  KeyEvent.VK_TAB &&
                 code != KeyEvent.VK_UP &&
                 code != KeyEvent.VK_DOWN )
             completePopup.setVisible(false);
     }
 
     public void keyReleased(KeyEvent arg0) { }
 
     public void keyTyped(KeyEvent arg0) { }
 
     public void shutdown() {
         inputJoin.send(Channel.SHUTDOWN, null);
     }
 
     /** Output methods **/
 
     protected void append(String toAppend, AttributeSet style) {
        try {
            Document doc = area.getDocument();
            doc.insertString(doc.getLength(), toAppend, style);
 
            // Cut the document to fit into the MAX_DOC_SIZE.
            // See JRUBY-4237.
            int extra = doc.getLength() - MAX_DOC_SIZE;
            if (extra > 0) {
                int removeBytes = extra + MAX_DOC_SIZE/10;
                doc.remove(0, removeBytes);
                startPos -= removeBytes;
            }
        } catch (BadLocationException e) {}
     }
 
     private void writeLineUnsafe(final String line) {
         if (line.startsWith("=>")) {
             append(line, resultStyle);
         } else {
             append(line, outputStyle);
         }
         startPos = area.getDocument().getLength();
     }
     
     private void writeLine(final String line) {
         if (EventQueue.isDispatchThread()) {
             writeLineUnsafe(line);
         } else {
             EventQueue.invokeLater(new Runnable() {
                 public void run() {
                     writeLineUnsafe(line);
                 }
             });
         }
     }
 
     private class Input extends InputStream {
         private volatile boolean closed = false;
 
         @Override
         public int available() throws IOException {
             if (closed) {
                 throw new IOException("Stream is closed");
             }
 
             return (Integer)inputJoin.call(Channel.AVAILABLE, null);
         }
 
         @Override
         public int read() throws IOException {
             byte[] b = new byte[1];
             if ( read(b, 0, 1) == 1 ) {
                 return b[0];
             } else {
                 return -1;
             }
         }
 
         @Override
         public int read(byte[] b, int off, int len) throws IOException {
             if (closed) {
                 throw new IOException("Stream is closed");
             }
 
             if (EventQueue.isDispatchThread()) {
                 throw new IOException("Cannot call read from event dispatch thread");
             }
 
             if (b == null) {
                 throw new NullPointerException();
             }
             if (off < 0 || len < 0 || off+len > b.length) {
                 throw new IndexOutOfBoundsException();
             }
             if (len == 0) {
                 return 0;
             }
 
             final ReadRequest request = new ReadRequest(b, off, len);
             return (Integer)inputJoin.call(Channel.READ, request);
         }
 
         @Override
         public void close() {
             closed = true;
             inputJoin.send(Channel.SHUTDOWN, null);
         }
     }
 
     private class Output extends OutputStream {
         @Override
         public void write(int b) throws IOException {
             writeLine("" + b);
         }
 
         @Override
         public void write(byte[] b, int off, int len) {
-            try {
-                writeLine(new String(b, off, len, "UTF-8"));
-            } catch (UnsupportedEncodingException ex) {
-                writeLine(new String(b, off, len));
-            }
+            writeLine(RubyEncoding.decodeUTF8(b, off, len));
         }
 
         @Override
         public void write(byte[] b) {
-            try {
-                writeLine(new String(b, "UTF-8"));
-            } catch (UnsupportedEncodingException ex) {
-                writeLine(new String(b));
-            }
+            writeLine(RubyEncoding.decodeUTF8(b));
         }
     }
 }
diff --git a/src/org/jruby/javasupport/JavaUtil.java b/src/org/jruby/javasupport/JavaUtil.java
index a80695f3f2..55b69f25cf 100644
--- a/src/org/jruby/javasupport/JavaUtil.java
+++ b/src/org/jruby/javasupport/JavaUtil.java
@@ -1,1344 +1,1338 @@
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
  * Copyright (C) 2002 Don Schwartz <schwardo@users.sourceforge.net>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
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
 
 import java.io.UnsupportedEncodingException;
 import java.lang.reflect.Method;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.HashMap;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 
 import java.util.Set;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.jruby.Ruby;
 import org.jruby.RubyBasicObject;
 import org.jruby.RubyBigDecimal;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
+import org.jruby.RubyEncoding;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyNil;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.RubyTime;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.java.proxies.RubyObjectHolderProxy;
 import org.jruby.javasupport.proxy.InternalJavaProxy;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.util.ByteList;
 import org.jruby.util.TypeConverter;
 
 public class JavaUtil {
     public static IRubyObject[] convertJavaArrayToRuby(Ruby runtime, Object[] objects) {
         if (objects == null) return IRubyObject.NULL_ARRAY;
         
         IRubyObject[] rubyObjects = new IRubyObject[objects.length];
         for (int i = 0; i < objects.length; i++) {
             rubyObjects[i] = convertJavaToUsableRubyObject(runtime, objects[i]);
         }
         return rubyObjects;
     }
 
     public static JavaConverter getJavaConverter(Class clazz) {
         JavaConverter converter = JAVA_CONVERTERS.get(clazz);
 
         if (converter == null) {
             converter = JAVA_DEFAULT_CONVERTER;
         }
 
         return converter;
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object) {
         return convertJavaToUsableRubyObject(runtime, object);
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object, Class javaClass) {
         return convertJavaToUsableRubyObjectWithConverter(runtime, object, getJavaConverter(javaClass));
     }
 
     /**
      * Returns a usable RubyObject; for types that are not converted to Ruby native
      * types, a Java proxy will be returned.
      *
      * @param runtime
      * @param object
      * @return corresponding Ruby type, or a functional Java proxy
      */
     public static IRubyObject convertJavaToUsableRubyObject(Ruby runtime, Object object) {
         IRubyObject result = trySimpleConversions(runtime, object);
 
         if (result != null) return result;
 
         JavaConverter converter = getJavaConverter(object.getClass());
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             return Java.getInstance(runtime, object);
         }
         return converter.convert(runtime, object);
     }
 
     public static IRubyObject convertJavaToUsableRubyObjectWithConverter(Ruby runtime, Object object, JavaConverter converter) {
         IRubyObject result = trySimpleConversions(runtime, object);
 
         if (result != null) return result;
 
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             return Java.getInstance(runtime, object);
         }
         return converter.convert(runtime, object);
     }
 
     public static IRubyObject convertJavaArrayElementToRuby(Ruby runtime, JavaConverter converter, Object array, int i) {
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             IRubyObject x = convertJavaToUsableRubyObject(runtime, ((Object[])array)[i]);
             return x;
         }
         return converter.get(runtime, array, i);
     }
 
     public static Class<?> primitiveToWrapper(Class<?> type) {
         if (type.isPrimitive()) {
             if (type == boolean.class) {
                 return Boolean.class;
             } else if (type == byte.class) {
                 return Byte.class;
             } else if (type == short.class) {
                 return Short.class;
             } else if (type == char.class) {
                 return Character.class;
             } else if (type == int.class) {
                 return Integer.class;
             } else if (type == long.class) {
                 return Long.class;
             } else if (type == float.class) {
                 return Float.class;
             } else if (type == double.class) {
                 return Double.class;
             } else if (type == void.class) {
                 return Void.class;
             }
         }
         return type;
     }
 
     public static boolean isDuckTypeConvertable(Class providedArgumentType, Class parameterType) {
         return
                 parameterType.isInterface() &&
                 !parameterType.isAssignableFrom(providedArgumentType) &&
                 RubyObject.class.isAssignableFrom(providedArgumentType);
     }
 
     public static Object convertProcToInterface(ThreadContext context, RubyObject rubyObject, Class target) {
         return convertProcToInterface(context, (RubyBasicObject)rubyObject, target);
     }
 
     public static Object convertProcToInterface(ThreadContext context, RubyBasicObject rubyObject, Class target) {
         Ruby runtime = context.getRuntime();
         RubyModule javaInterfaceModule = (RubyModule)Java.get_interface_module(runtime, JavaClass.get(runtime, target));
         if (!((RubyModule) javaInterfaceModule).isInstance(rubyObject)) {
             javaInterfaceModule.callMethod(context, "extend_object", rubyObject);
             javaInterfaceModule.callMethod(context, "extended", rubyObject);
         }
 
         if (rubyObject instanceof RubyProc) {
             // Proc implementing an interface, pull in the catch-all code that lets the proc get invoked
             // no matter what method is called on the interface
             RubyClass singletonClass = rubyObject.getSingletonClass();
 
             singletonClass.addMethod("method_missing", new DynamicMethod(singletonClass, Visibility.PUBLIC, CallConfiguration.FrameNoneScopeNone) {
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (!(self instanceof RubyProc)) {
                         throw context.getRuntime().newTypeError("interface impl method_missing for block used with non-Proc object");
                     }
                     RubyProc proc = (RubyProc)self;
                     IRubyObject[] newArgs;
                     if (args.length == 1) {
                         newArgs = IRubyObject.NULL_ARRAY;
                     } else {
                         newArgs = new IRubyObject[args.length - 1];
                         System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                     }
                     return proc.call(context, newArgs);
                 }
 
                 @Override
                 public DynamicMethod dup() {
                     return this;
                 }
             });
         }
         JavaObject jo = (JavaObject) RuntimeHelpers.invoke(context, rubyObject, "__jcreate_meta!");
         return jo.getValue();
     }
 
     public static NumericConverter getNumericConverter(Class target) {
         NumericConverter converter = NUMERIC_CONVERTERS.get(target);
         if (converter == null) {
             return NUMERIC_TO_OTHER;
         }
         return converter;
     }
 
     public static boolean isJavaObject(IRubyObject candidate) {
         return candidate.dataGetStruct() instanceof JavaObject;
     }
 
     public static Object unwrapJavaObject(IRubyObject object) {
         return ((JavaObject)object.dataGetStruct()).getValue();
     }
 
     public static JavaObject unwrapJavaObject(Ruby runtime, IRubyObject convertee, String errorMessage) {
         IRubyObject obj = convertee;
         if(!(obj instanceof JavaObject)) {
             if (obj.dataGetStruct() != null && (obj.dataGetStruct() instanceof JavaObject)) {
                 obj = (JavaObject)obj.dataGetStruct();
             } else {
                 throw runtime.newTypeError(errorMessage);
             }
         }
         return (JavaObject)obj;
     }
 
     public static Object unwrapJavaValue(Ruby runtime, IRubyObject obj, String errorMessage) {
         if(obj instanceof JavaMethod) {
             return ((JavaMethod)obj).getValue();
         } else if(obj instanceof JavaConstructor) {
             return ((JavaConstructor)obj).getValue();
         } else if(obj instanceof JavaField) {
             return ((JavaField)obj).getValue();
         } else if(obj instanceof JavaObject) {
             return ((JavaObject)obj).getValue();
         } else if(obj.dataGetStruct() != null && (obj.dataGetStruct() instanceof IRubyObject)) {
             return unwrapJavaValue(runtime, ((IRubyObject)obj.dataGetStruct()), errorMessage);
         } else {
             throw runtime.newTypeError(errorMessage);
         }
     }
 
     private static final Pattern JAVA_PROPERTY_CHOPPER = Pattern.compile("(get|set|is)([A-Z0-9])(.*)");
     public static String getJavaPropertyName(String beanMethodName) {
         Matcher m = JAVA_PROPERTY_CHOPPER.matcher(beanMethodName);
 
         if (!m.find()) return null;
         String javaPropertyName = m.group(2).toLowerCase() + m.group(3);
         return javaPropertyName;
     }
 
     private static final Pattern CAMEL_CASE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");
     public static String getRubyCasedName(String javaCasedName) {
         Matcher m = CAMEL_CASE_SPLITTER.matcher(javaCasedName);
         return m.replaceAll("$1_$2").toLowerCase();
     }
 
     private static final Pattern RUBY_CASE_SPLITTER = Pattern.compile("([a-z][0-9]*)_([a-z])");
     public static String getJavaCasedName(String javaCasedName) {
         Matcher m = RUBY_CASE_SPLITTER.matcher(javaCasedName);
         StringBuffer newName = new StringBuffer();
         if (!m.find()) {
             return null;
         }
         m.reset();
 
         while (m.find()) {
             m.appendReplacement(newName, m.group(1) + Character.toUpperCase(m.group(2).charAt(0)));
         }
 
         m.appendTail(newName);
 
         return newName.toString();
     }
 
     /**
      * Given a simple Java method name and the Java Method objects that represent
      * all its overloads, add to the given nameSet all possible Ruby names that would
      * be valid.
      *
      * @param simpleName
      * @param nameSet
      * @param methods
      */
     public static Set<String> getRubyNamesForJavaName(String javaName, List<Method> methods) {
         String javaPropertyName = JavaUtil.getJavaPropertyName(javaName);
         String rubyName = JavaUtil.getRubyCasedName(javaName);
         Set<String> nameSet = new LinkedHashSet<String>();
         nameSet.add(javaName);
         nameSet.add(rubyName);
         String rubyPropertyName = null;
         for (Method method: methods) {
             Class<?>[] argTypes = method.getParameterTypes();
             Class<?> resultType = method.getReturnType();
             int argCount = argTypes.length;
 
             // Add property name aliases
             if (javaPropertyName != null) {
                 if (rubyName.startsWith("get_")) {
                     rubyPropertyName = rubyName.substring(4);
                     if (argCount == 0 ||                                // getFoo      => foo
                         argCount == 1 && argTypes[0] == int.class) {    // getFoo(int) => foo(int)
 
                         nameSet.add(javaPropertyName);
                         nameSet.add(rubyPropertyName);
                         if (resultType == boolean.class) {              // getFooBar() => fooBar?, foo_bar?(*)
                             nameSet.add(javaPropertyName + '?');
                             nameSet.add(rubyPropertyName + '?');
                         }
                     }
                 } else if (rubyName.startsWith("set_")) {
                     rubyPropertyName = rubyName.substring(4);
                     if (argCount == 1 && resultType == void.class) {    // setFoo(Foo) => foo=(Foo)
                         nameSet.add(javaPropertyName + '=');
                         nameSet.add(rubyPropertyName + '=');
                     }
                 } else if (rubyName.startsWith("is_")) {
                     rubyPropertyName = rubyName.substring(3);
                     if (resultType == boolean.class) {                  // isFoo() => foo, isFoo(*) => foo(*)
                         nameSet.add(javaPropertyName);
                         nameSet.add(rubyPropertyName);
                         nameSet.add(javaPropertyName + '?');
                         nameSet.add(rubyPropertyName + '?');
                     }
                 }
             } else {
                 // If not a property, but is boolean add ?-postfixed aliases.
                 if (resultType == boolean.class) {
                     // is_something?, contains_thing?
                     nameSet.add(javaName + '?');
                     nameSet.add(rubyName + '?');
                 }
             }
         }
 
         return nameSet;
     }
     
     public static abstract class JavaConverter {
         private final Class type;
         public JavaConverter(Class type) {this.type = type;}
         public abstract IRubyObject convert(Ruby runtime, Object object);
         public abstract IRubyObject get(Ruby runtime, Object array, int i);
         public String toString() {return type.getName() + " converter";}
     }
 
     public interface NumericConverter {
         public Object coerce(RubyNumeric numeric, Class target);
     }
 
     private static IRubyObject trySimpleConversions(Ruby runtime, Object object) {
         if (object == null) {
             return runtime.getNil();
         }
 
         if (object instanceof IRubyObject) {
             return (IRubyObject) object;
         }
 
         if (object instanceof RubyObjectHolderProxy) {
             return ((RubyObjectHolderProxy) object).__ruby_object();
         }
 
         if (object instanceof InternalJavaProxy) {
             InternalJavaProxy internalJavaProxy = (InternalJavaProxy) object;
             IRubyObject orig = internalJavaProxy.___getInvocationHandler().getOrig();
 
             if (orig != null) {
                 return orig;
             }
         }
 
         return null;
     }
     
     private static final JavaConverter JAVA_DEFAULT_CONVERTER = new JavaConverter(Object.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             IRubyObject result = trySimpleConversions(runtime, object);
 
             if (result != null) return result;
             
             return JavaObject.wrap(runtime, object);
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Object[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_BOOLEAN_CONVERTER = new JavaConverter(Boolean.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBoolean.newBoolean(runtime, ((Boolean)object).booleanValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Boolean[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_FLOAT_CONVERTER = new JavaConverter(Float.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Float)object).doubleValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Float[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_DOUBLE_CONVERTER = new JavaConverter(Double.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Double)object).doubleValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Double[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_CHAR_CONVERTER = new JavaConverter(Character.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Character)object).charValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Character[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_BYTE_CONVERTER = new JavaConverter(Byte.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Byte)object).byteValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Byte[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_SHORT_CONVERTER = new JavaConverter(Short.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Short)object).shortValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Short[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_INT_CONVERTER = new JavaConverter(Integer.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Integer)object).intValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Integer[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_LONG_CONVERTER = new JavaConverter(Long.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Long)object).longValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Long[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_BOOLEANPRIM_CONVERTER = new JavaConverter(boolean.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBoolean.newBoolean(runtime, ((Boolean)object).booleanValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyBoolean.newBoolean(runtime, ((boolean[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_FLOATPRIM_CONVERTER = new JavaConverter(float.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Float)object).doubleValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFloat.newFloat(runtime, ((float[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_DOUBLEPRIM_CONVERTER = new JavaConverter(double.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Double)object).doubleValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFloat.newFloat(runtime, ((double[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_CHARPRIM_CONVERTER = new JavaConverter(char.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Character)object).charValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFixnum.newFixnum(runtime, ((char[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_BYTEPRIM_CONVERTER = new JavaConverter(byte.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Byte)object).byteValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFixnum.newFixnum(runtime, ((byte[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_SHORTPRIM_CONVERTER = new JavaConverter(short.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Short)object).shortValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFixnum.newFixnum(runtime, ((short[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_INTPRIM_CONVERTER = new JavaConverter(int.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Integer)object).intValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFixnum.newFixnum(runtime, ((int[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_LONGPRIM_CONVERTER = new JavaConverter(long.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Long)object).longValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFixnum.newFixnum(runtime, ((long[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_STRING_CONVERTER = new JavaConverter(String.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyString.newUnicodeString(runtime, (String)object);
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((String[])array)[i]);
         }
     };
     
     private static final JavaConverter BYTELIST_CONVERTER = new JavaConverter(ByteList.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyString.newString(runtime, (ByteList)object);
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((ByteList[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_BIGINTEGER_CONVERTER = new JavaConverter(BigInteger.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBignum.newBignum(runtime, (BigInteger)object);
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((BigInteger[])array)[i]);
         }
     };
     
     private static final Map<Class,JavaConverter> JAVA_CONVERTERS =
         new HashMap<Class,JavaConverter>();
     
     static {
         JAVA_CONVERTERS.put(Byte.class, JAVA_BYTE_CONVERTER);
         JAVA_CONVERTERS.put(Byte.TYPE, JAVA_BYTEPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Short.class, JAVA_SHORT_CONVERTER);
         JAVA_CONVERTERS.put(Short.TYPE, JAVA_SHORTPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Character.class, JAVA_CHAR_CONVERTER);
         JAVA_CONVERTERS.put(Character.TYPE, JAVA_CHARPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Integer.class, JAVA_INT_CONVERTER);
         JAVA_CONVERTERS.put(Integer.TYPE, JAVA_INTPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Long.class, JAVA_LONG_CONVERTER);
         JAVA_CONVERTERS.put(Long.TYPE, JAVA_LONGPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Float.class, JAVA_FLOAT_CONVERTER);
         JAVA_CONVERTERS.put(Float.TYPE, JAVA_FLOATPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Double.class, JAVA_DOUBLE_CONVERTER);
         JAVA_CONVERTERS.put(Double.TYPE, JAVA_DOUBLEPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Boolean.class, JAVA_BOOLEAN_CONVERTER);
         JAVA_CONVERTERS.put(Boolean.TYPE, JAVA_BOOLEANPRIM_CONVERTER);
         
         JAVA_CONVERTERS.put(String.class, JAVA_STRING_CONVERTER);
         
         JAVA_CONVERTERS.put(ByteList.class, BYTELIST_CONVERTER);
         
         JAVA_CONVERTERS.put(BigInteger.class, JAVA_BIGINTEGER_CONVERTER);
     }
 
     private static NumericConverter NUMERIC_TO_BYTE = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             long value = numeric.getLongValue();
             if (isLongByteable(value)) {
                 return Byte.valueOf((byte)value);
             }
             throw numeric.getRuntime().newRangeError("too big for byte: " + numeric);
         }
     };
     private static NumericConverter NUMERIC_TO_SHORT = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             long value = numeric.getLongValue();
             if (isLongShortable(value)) {
                 return Short.valueOf((short)value);
             }
             throw numeric.getRuntime().newRangeError("too big for short: " + numeric);
         }
     };
     private static NumericConverter NUMERIC_TO_CHARACTER = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             long value = numeric.getLongValue();
             if (isLongCharable(value)) {
                 return Character.valueOf((char)value);
             }
             throw numeric.getRuntime().newRangeError("too big for char: " + numeric);
         }
     };
     private static NumericConverter NUMERIC_TO_INTEGER = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             long value = numeric.getLongValue();
             if (isLongIntable(value)) {
                 return Integer.valueOf((int)value);
             }
             throw numeric.getRuntime().newRangeError("too big for int: " + numeric);
         }
     };
     private static NumericConverter NUMERIC_TO_LONG = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             return Long.valueOf(numeric.getLongValue());
         }
     };
     private static NumericConverter NUMERIC_TO_FLOAT = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             double value = numeric.getDoubleValue();
             // many cases are ok to convert to float; if not one of these, error
             if (isDoubleFloatable(value)) {
                 return Float.valueOf((float)value);
             } else {
                 throw numeric.getRuntime().newTypeError("too big for float: " + numeric);
             }
         }
     };
     private static NumericConverter NUMERIC_TO_DOUBLE = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             return Double.valueOf(numeric.getDoubleValue());
         }
     };
     private static NumericConverter NUMERIC_TO_BIGINTEGER = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             return numeric.getBigIntegerValue();
         }
     };
     private static NumericConverter NUMERIC_TO_OBJECT = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             // for Object, default to natural wrapper type
             if (numeric instanceof RubyFixnum) {
                 long value = numeric.getLongValue();
                 return Long.valueOf(value);
             } else if (numeric instanceof RubyFloat) {
                 double value = numeric.getDoubleValue();
                 return Double.valueOf(value);
             } else if (numeric instanceof RubyBignum) {
                 return ((RubyBignum)numeric).getValue();
             } else if (numeric instanceof RubyBigDecimal) {
                 return ((RubyBigDecimal)numeric).getValue();
             } else {
                 return NUMERIC_TO_OTHER.coerce(numeric, target);
             }
         }
     };
     private static NumericConverter NUMERIC_TO_OTHER = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             throw numeric.getRuntime().newTypeError("could not coerce " + numeric.getMetaClass() + " to " + target);
         }
     };
     private static NumericConverter NUMERIC_TO_VOID = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             return null;
         }
     };
     private static boolean isDoubleFloatable(double value) {
         return true;
     }
     private static boolean isLongByteable(long value) {
         return value >= Byte.MIN_VALUE && value <= 0xFF;
     }
     private static boolean isLongShortable(long value) {
         return value >= Short.MIN_VALUE && value <= 0xFFFF;
     }
     private static boolean isLongCharable(long value) {
         return value >= Character.MIN_VALUE && value <= Character.MAX_VALUE;
     }
     private static boolean isLongIntable(long value) {
         return value >= Integer.MIN_VALUE && value <= 0xFFFFFFFFL;
     }
     
     private static Map<Class, NumericConverter> NUMERIC_CONVERTERS = new HashMap<Class, NumericConverter>();
 
     static {
         NUMERIC_CONVERTERS.put(Byte.TYPE, NUMERIC_TO_BYTE);
         NUMERIC_CONVERTERS.put(Byte.class, NUMERIC_TO_BYTE);
         NUMERIC_CONVERTERS.put(Short.TYPE, NUMERIC_TO_SHORT);
         NUMERIC_CONVERTERS.put(Short.class, NUMERIC_TO_SHORT);
         NUMERIC_CONVERTERS.put(Character.TYPE, NUMERIC_TO_CHARACTER);
         NUMERIC_CONVERTERS.put(Character.class, NUMERIC_TO_CHARACTER);
         NUMERIC_CONVERTERS.put(Integer.TYPE, NUMERIC_TO_INTEGER);
         NUMERIC_CONVERTERS.put(Integer.class, NUMERIC_TO_INTEGER);
         NUMERIC_CONVERTERS.put(Long.TYPE, NUMERIC_TO_LONG);
         NUMERIC_CONVERTERS.put(Long.class, NUMERIC_TO_LONG);
         NUMERIC_CONVERTERS.put(Float.TYPE, NUMERIC_TO_FLOAT);
         NUMERIC_CONVERTERS.put(Float.class, NUMERIC_TO_FLOAT);
         NUMERIC_CONVERTERS.put(Double.TYPE, NUMERIC_TO_DOUBLE);
         NUMERIC_CONVERTERS.put(Double.class, NUMERIC_TO_DOUBLE);
         NUMERIC_CONVERTERS.put(BigInteger.class, NUMERIC_TO_BIGINTEGER);
         NUMERIC_CONVERTERS.put(Object.class, NUMERIC_TO_OBJECT);
         NUMERIC_CONVERTERS.put(void.class, NUMERIC_TO_VOID);
     }
 
     @Deprecated
     public static Object convertRubyToJava(IRubyObject rubyObject) {
         return convertRubyToJava(rubyObject, Object.class);
     }
 
     @Deprecated
     public static Object convertRubyToJava(IRubyObject rubyObject, Class javaClass) {
         if (javaClass == void.class || rubyObject == null || rubyObject.isNil()) {
             return null;
         }
 
         ThreadContext context = rubyObject.getRuntime().getCurrentContext();
         IRubyObject origObject = rubyObject;
         if (rubyObject.dataGetStruct() instanceof JavaObject) {
             rubyObject = (JavaObject) rubyObject.dataGetStruct();
             if(rubyObject == null) {
                 throw new RuntimeException("dataGetStruct returned null for " + origObject.getType().getName());
             }
         } else if (rubyObject.respondsTo("java_object")) {
             rubyObject = rubyObject.callMethod(context, "java_object");
             if(rubyObject == null) {
                 throw new RuntimeException("java_object returned null for " + origObject.getType().getName());
             }
         }
 
         if (rubyObject instanceof JavaObject) {
             Object value =  ((JavaObject) rubyObject).getValue();
 
             return convertArgument(rubyObject.getRuntime(), value, value.getClass());
 
         } else if (javaClass == Object.class || javaClass == null) {
             /* The Java method doesn't care what class it is, but we need to
                know what to convert it to, so we use the object's own class.
                If that doesn't help, we use String to force a call to the
                object's "to_s" method. */
             javaClass = rubyObject.getJavaClass();
         }
 
         if (javaClass.isInstance(rubyObject)) {
             // rubyObject is already of the required jruby class (or subclass)
             return rubyObject;
         }
 
         // the converters handle not only primitive types but also their boxed versions, so we should check
         // if we have a converter before checking for isPrimitive()
         RubyConverter converter = RUBY_CONVERTERS.get(javaClass);
         if (converter != null) {
             return converter.convert(context, rubyObject);
         }
 
         if (javaClass.isPrimitive()) {
             String s = ((RubyString)TypeConverter.convertToType(rubyObject, rubyObject.getRuntime().getString(), "to_s", true)).getUnicodeValue();
             if (s.length() > 0) {
                 return Character.valueOf(s.charAt(0));
             }
             return Character.valueOf('\0');
         } else if (javaClass == String.class) {
             RubyString rubyString = (RubyString) rubyObject.callMethod(context, "to_s");
             ByteList bytes = rubyString.getByteList();
-            try {
-                return new String(bytes.getUnsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
-            } catch (UnsupportedEncodingException uee) {
-                return new String(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
-            }
+            return RubyEncoding.decodeUTF8(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
         } else if (javaClass == ByteList.class) {
             return rubyObject.convertToString().getByteList();
         } else if (javaClass == BigInteger.class) {
          	if (rubyObject instanceof RubyBignum) {
          		return ((RubyBignum)rubyObject).getValue();
          	} else if (rubyObject instanceof RubyNumeric) {
  				return  BigInteger.valueOf (((RubyNumeric)rubyObject).getLongValue());
          	} else if (rubyObject.respondsTo("to_i")) {
          		RubyNumeric rubyNumeric = ((RubyNumeric)rubyObject.callMethod(context, "to_f"));
  				return  BigInteger.valueOf (rubyNumeric.getLongValue());
          	}
         } else if (javaClass == BigDecimal.class && !(rubyObject instanceof JavaObject)) {
          	if (rubyObject.respondsTo("to_f")) {
              	double double_value = ((RubyNumeric)rubyObject.callMethod(context, "to_f")).getDoubleValue();
              	return new BigDecimal(double_value);
          	}
         }
 
         try {
             if (isDuckTypeConvertable(rubyObject.getClass(), javaClass)) {
                 return convertProcToInterface(context, (RubyObject) rubyObject, javaClass);
             }
             return ((JavaObject) rubyObject).getValue();
         } catch (ClassCastException ex) {
             if (rubyObject.getRuntime().getDebug().isTrue()) ex.printStackTrace();
             return null;
         }
     }
 
     @Deprecated
     public static byte convertRubyToJavaByte(IRubyObject rubyObject) {
         return ((Byte)convertRubyToJava(rubyObject, byte.class)).byteValue();
     }
 
     @Deprecated
     public static short convertRubyToJavaShort(IRubyObject rubyObject) {
         return ((Short)convertRubyToJava(rubyObject, short.class)).shortValue();
     }
 
     @Deprecated
     public static char convertRubyToJavaChar(IRubyObject rubyObject) {
         return ((Character)convertRubyToJava(rubyObject, char.class)).charValue();
     }
 
     @Deprecated
     public static int convertRubyToJavaInt(IRubyObject rubyObject) {
         return ((Integer)convertRubyToJava(rubyObject, int.class)).intValue();
     }
 
     @Deprecated
     public static long convertRubyToJavaLong(IRubyObject rubyObject) {
         return ((Long)convertRubyToJava(rubyObject, long.class)).longValue();
     }
 
     @Deprecated
     public static float convertRubyToJavaFloat(IRubyObject rubyObject) {
         return ((Float)convertRubyToJava(rubyObject, float.class)).floatValue();
     }
 
     @Deprecated
     public static double convertRubyToJavaDouble(IRubyObject rubyObject) {
         return ((Double)convertRubyToJava(rubyObject, double.class)).doubleValue();
     }
 
     @Deprecated
     public static boolean convertRubyToJavaBoolean(IRubyObject rubyObject) {
         return ((Boolean)convertRubyToJava(rubyObject, boolean.class)).booleanValue();
     }
 
     @Deprecated
     public static Object convertArgumentToType(ThreadContext context, IRubyObject arg, Class target) {
         return arg.toJava(target);
     }
 
     @Deprecated
     public static Object coerceNilToType(RubyNil nil, Class target) {
         return nil.toJava(target);
     }
 
     @Deprecated
     public static final RubyConverter RUBY_BOOLEAN_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return Boolean.valueOf(rubyObject.isTrue());
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_BYTE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return Byte.valueOf((byte) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return Byte.valueOf((byte) 0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_SHORT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return Short.valueOf((short) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return Short.valueOf((short) 0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_CHAR_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return Character.valueOf((char) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return Character.valueOf((char) 0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_INTEGER_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return Integer.valueOf((int) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return Integer.valueOf(0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_LONG_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return Long.valueOf(((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return Long.valueOf(0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_FLOAT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Float((float) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_f")).getDoubleValue());
             }
             return new Float(0.0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_DOUBLE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Double(((RubyNumeric) rubyObject.callMethod(
                         context, "to_f")).getDoubleValue());
             }
             return new Double(0.0);
         }
     };
 
     @Deprecated
     public static final Map<Class, RubyConverter> RUBY_CONVERTERS = new HashMap<Class, RubyConverter>();
     static {
         RUBY_CONVERTERS.put(Boolean.class, RUBY_BOOLEAN_CONVERTER);
         RUBY_CONVERTERS.put(Boolean.TYPE, RUBY_BOOLEAN_CONVERTER);
         RUBY_CONVERTERS.put(Byte.class, RUBY_BYTE_CONVERTER);
         RUBY_CONVERTERS.put(Byte.TYPE, RUBY_BYTE_CONVERTER);
         RUBY_CONVERTERS.put(Short.class, RUBY_SHORT_CONVERTER);
         RUBY_CONVERTERS.put(Short.TYPE, RUBY_SHORT_CONVERTER);
         RUBY_CONVERTERS.put(Integer.class, RUBY_INTEGER_CONVERTER);
         RUBY_CONVERTERS.put(Integer.TYPE, RUBY_INTEGER_CONVERTER);
         RUBY_CONVERTERS.put(Long.class, RUBY_LONG_CONVERTER);
         RUBY_CONVERTERS.put(Long.TYPE, RUBY_LONG_CONVERTER);
         RUBY_CONVERTERS.put(Float.class, RUBY_FLOAT_CONVERTER);
         RUBY_CONVERTERS.put(Float.TYPE, RUBY_FLOAT_CONVERTER);
         RUBY_CONVERTERS.put(Double.class, RUBY_DOUBLE_CONVERTER);
         RUBY_CONVERTERS.put(Double.TYPE, RUBY_DOUBLE_CONVERTER);
     }
 
     @Deprecated
     public static IRubyObject convertJavaToRuby(Ruby runtime, int i) {
         return runtime.newFixnum(i);
     }
 
     @Deprecated
     public static IRubyObject convertJavaToRuby(Ruby runtime, long l) {
         return runtime.newFixnum(l);
     }
 
     @Deprecated
     public static IRubyObject convertJavaToRuby(Ruby runtime, float f) {
         return runtime.newFloat(f);
     }
 
     @Deprecated
     public static IRubyObject convertJavaToRuby(Ruby runtime, double d) {
         return runtime.newFloat(d);
     }
 
     @Deprecated
     public static IRubyObject convertJavaToRuby(Ruby runtime, boolean b) {
         return runtime.newBoolean(b);
     }
 
     @Deprecated
     public static IRubyObject convertJavaToRuby(Ruby runtime, JavaConverter converter, Object object) {
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             return Java.getInstance(runtime, object);
         }
         return converter.convert(runtime, object);
     }
 
     @Deprecated
     public interface RubyConverter {
         public Object convert(ThreadContext context, IRubyObject rubyObject);
     }
 
     @Deprecated
     public static final RubyConverter ARRAY_BOOLEAN_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Boolean.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_BYTE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Byte.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_SHORT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Short.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_CHAR_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Character.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_INT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Integer.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_LONG_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Long.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_FLOAT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Float.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_DOUBLE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Double.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_OBJECT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Object.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_CLASS_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Class.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_STRING_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(String.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_BIGINTEGER_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(BigInteger.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_BIGDECIMAL_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(BigDecimal.class);
         }
     };
 
     @Deprecated
     public static final Map<Class, RubyConverter> ARRAY_CONVERTERS = new HashMap<Class, RubyConverter>();
     static {
         ARRAY_CONVERTERS.put(Boolean.class, ARRAY_BOOLEAN_CONVERTER);
         ARRAY_CONVERTERS.put(Boolean.TYPE, ARRAY_BOOLEAN_CONVERTER);
         ARRAY_CONVERTERS.put(Byte.class, ARRAY_BYTE_CONVERTER);
         ARRAY_CONVERTERS.put(Byte.TYPE, ARRAY_BYTE_CONVERTER);
         ARRAY_CONVERTERS.put(Short.class, ARRAY_SHORT_CONVERTER);
         ARRAY_CONVERTERS.put(Short.TYPE, ARRAY_SHORT_CONVERTER);
         ARRAY_CONVERTERS.put(Character.class, ARRAY_CHAR_CONVERTER);
         ARRAY_CONVERTERS.put(Character.TYPE, ARRAY_CHAR_CONVERTER);
         ARRAY_CONVERTERS.put(Integer.class, ARRAY_INT_CONVERTER);
         ARRAY_CONVERTERS.put(Integer.TYPE, ARRAY_INT_CONVERTER);
         ARRAY_CONVERTERS.put(Long.class, ARRAY_LONG_CONVERTER);
         ARRAY_CONVERTERS.put(Long.TYPE, ARRAY_LONG_CONVERTER);
         ARRAY_CONVERTERS.put(Float.class, ARRAY_FLOAT_CONVERTER);
         ARRAY_CONVERTERS.put(Float.TYPE, ARRAY_FLOAT_CONVERTER);
         ARRAY_CONVERTERS.put(Double.class, ARRAY_DOUBLE_CONVERTER);
         ARRAY_CONVERTERS.put(Double.TYPE, ARRAY_DOUBLE_CONVERTER);
         ARRAY_CONVERTERS.put(String.class, ARRAY_STRING_CONVERTER);
         ARRAY_CONVERTERS.put(Class.class, ARRAY_CLASS_CONVERTER);
         ARRAY_CONVERTERS.put(BigInteger.class, ARRAY_BIGINTEGER_CONVERTER);
         ARRAY_CONVERTERS.put(BigDecimal.class, ARRAY_BIGDECIMAL_CONVERTER);
     }
 
     @Deprecated
     public static RubyConverter getArrayConverter(Class type) {
         RubyConverter converter = ARRAY_CONVERTERS.get(type);
         if (converter == null) {
             return ARRAY_OBJECT_CONVERTER;
         }
         return converter;
     }
 
     /**
      * High-level object conversion utility.
      */
     @Deprecated
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object.respondsTo("to_java_object")) {
             IRubyObject result = (JavaObject)object.dataGetStruct();
             if (result == null) {
                 result = object.callMethod(recv.getRuntime().getCurrentContext(), "to_java_object");
             }
             if (result instanceof JavaObject) {
                 recv.getRuntime().getJavaSupport().getObjectProxyCache().put(((JavaObject) result).getValue(), object);
             }
             return result;
         }
 
         return primitive_to_java(recv, object, unusedBlock);
     }
 
     @Deprecated
     public static IRubyObject java_to_primitive(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
             return JavaUtil.convertJavaToRuby(recv.getRuntime(), ((JavaObject) object).getValue());
         }
 
         return object;
     }
 
     @Deprecated
     public static IRubyObject primitive_to_java(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
             return object;
         }
         Ruby runtime = recv.getRuntime();
         Object javaObject;
         switch (object.getMetaClass().index) {
         case ClassIndex.NIL:
             javaObject = null;
             break;
         case ClassIndex.FIXNUM:
             javaObject = Long.valueOf(((RubyFixnum) object).getLongValue());
             break;
         case ClassIndex.BIGNUM:
             javaObject = ((RubyBignum) object).getValue();
             break;
         case ClassIndex.FLOAT:
             javaObject = new Double(((RubyFloat) object).getValue());
             break;
         case ClassIndex.STRING:
-            try {
-                ByteList bytes = ((RubyString) object).getByteList();
-                javaObject = new String(bytes.getUnsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
-            } catch (UnsupportedEncodingException uee) {
-                javaObject = object.toString();
-            }
+            ByteList bytes = ((RubyString) object).getByteList();
+            javaObject = RubyEncoding.decodeUTF8(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
             break;
         case ClassIndex.TRUE:
             javaObject = Boolean.TRUE;
             break;
         case ClassIndex.FALSE:
             javaObject = Boolean.FALSE;
             break;
         case ClassIndex.TIME:
             javaObject = ((RubyTime) object).getJavaDate();
             break;
         default:
             // it's not one of the types we convert, so just pass it out as-is without wrapping
             return object;
         }
 
         // we've found a Java type to which we've coerced the Ruby value, wrap it
         return JavaObject.wrap(runtime, javaObject);
     }
 
     @Deprecated
     public static Object convertArgument(Ruby runtime, Object argument, Class<?> parameterType) {
         if (argument == null) {
           if(parameterType.isPrimitive()) {
             throw runtime.newTypeError("primitives do not accept null");
           } else {
             return null;
           }
         }
 
         if (argument instanceof JavaObject) {
             argument = ((JavaObject) argument).getValue();
             if (argument == null) {
                 return null;
             }
         }
         Class<?> type = primitiveToWrapper(parameterType);
         if (type == Void.class) {
             return null;
         }
         if (argument instanceof Number) {
             final Number number = (Number) argument;
             if (type == Long.class) {
                 return Long.valueOf(number.longValue());
             } else if (type == Integer.class) {
                 return Integer.valueOf(number.intValue());
             } else if (type == Byte.class) {
                 return Byte.valueOf(number.byteValue());
             } else if (type == Character.class) {
                 return Character.valueOf((char) number.intValue());
             } else if (type == Double.class) {
                 return new Double(number.doubleValue());
             } else if (type == Float.class) {
                 return new Float(number.floatValue());
             } else if (type == Short.class) {
                 return Short.valueOf(number.shortValue());
             }
         }
         if (isDuckTypeConvertable(argument.getClass(), parameterType)) {
             RubyObject rubyObject = (RubyObject) argument;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(runtime.getCurrentContext(), rubyObject, parameterType);
             }
         }
         return argument;
     }
 
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version
      */
     @Deprecated
     public static IRubyObject java_to_ruby(Ruby runtime, IRubyObject object) {
         if (object instanceof JavaObject) {
             return JavaUtil.convertJavaToUsableRubyObject(runtime, ((JavaObject) object).getValue());
         }
         return object;
     }
 
     // FIXME: This doesn't actually support anything but String
     @Deprecated
     public static Object coerceStringToType(RubyString string, Class target) {
         try {
             ByteList bytes = string.getByteList();
 
             // 1.9 support for encodings
+            // TODO: Fix charset use for JRUBY-4553
             if (string.getRuntime().is1_9()) {
                 return new String(bytes.getUnsafeBytes(), bytes.begin(), bytes.length(), string.getEncoding().toString());
             }
 
-            return new String(bytes.getUnsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
+            return RubyEncoding.decodeUTF8(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
         } catch (UnsupportedEncodingException uee) {
             return string.toString();
         }
     }
 
     @Deprecated
     public static Object coerceOtherToType(ThreadContext context, IRubyObject arg, Class target) {
         if (isDuckTypeConvertable(arg.getClass(), target)) {
             RubyObject rubyObject = (RubyObject) arg;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(context, rubyObject, target);
             }
         }
 
         // it's either as converted as we can make it via above logic or it's
         // not one of the types we convert, so just pass it out as-is without wrapping
         return arg;
     }
 
     @Deprecated
     public static Object coerceJavaObjectToType(ThreadContext context, Object javaObject, Class target) {
         if (javaObject != null && isDuckTypeConvertable(javaObject.getClass(), target)) {
             RubyObject rubyObject = (RubyObject) javaObject;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(context, rubyObject, target);
             }
 
             // can't be converted any more, return it
             return javaObject;
         } else {
             return javaObject;
         }
     }
 }
diff --git a/src/org/jruby/runtime/load/ExternalScript.java b/src/org/jruby/runtime/load/ExternalScript.java
index 58493a9c04..46a509c291 100644
--- a/src/org/jruby/runtime/load/ExternalScript.java
+++ b/src/org/jruby/runtime/load/ExternalScript.java
@@ -1,79 +1,80 @@
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
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2005 Thomas E. Enebo <enebo@acm.org>
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
 
 import java.io.IOException;
 import java.io.InputStream;
 import org.jruby.Ruby;
 
 import static org.jruby.util.JRubyFile.normalizeSeps;
 
 public class ExternalScript implements Library {
     private final LoadServiceResource resource;
     
     public ExternalScript(LoadServiceResource resource, String name) {
         this.resource = resource;
     }
 
     public void load(Ruby runtime, boolean wrap) {
         InputStream in = null;
         try {
             in = resource.getInputStream();
             String name = normalizeSeps(resource.getName());
             try {
+                // TODO: Fix charset use for JRUBY-4553
                 name = java.net.URLDecoder.decode(name, "ISO-8859-1");
             } catch(Exception ignored) {}
 
             if (runtime.getInstanceConfig().getCompileMode().shouldPrecompileAll()) {
                 runtime.compileAndLoadFile(name, in, wrap);
             } else {
                 java.io.File path = resource.getPath();
 
                 if(path != null && !resource.isAbsolute()) {
                     name = normalizeSeps(path.getCanonicalPath());
                 }
 
                 runtime.loadFile(name, in, wrap);
             }
 
 
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } finally {
             try { in.close(); } catch (Exception ex) {}
         }
     }
 
     @Override
     public String toString() {
         return "ExternalScript: " + resource.getName();
     }
 }
diff --git a/src/org/jruby/runtime/load/LoadService.java b/src/org/jruby/runtime/load/LoadService.java
index 188f68766f..5b8b06e4cb 100644
--- a/src/org/jruby/runtime/load/LoadService.java
+++ b/src/org/jruby/runtime/load/LoadService.java
@@ -86,1170 +86,1170 @@ import org.jruby.util.JRubyFile;
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
     private final LoadTimer loadTimer;
 
     public enum SuffixType {
         Source, Extension, Both, Neither;
         
         public static final String[] sourceSuffixes = { ".class", ".rb" };
         public static final String[] extensionSuffixes = { ".jar" };
         private static final String[] allSuffixes = { ".class", ".rb", ".jar" };
         private static final String[] emptySuffixes = { "" };
         
         public String[] getSuffixes() {
             switch (this) {
             case Source:
                 return sourceSuffixes;
             case Extension:
                 return extensionSuffixes;
             case Both:
                 return allSuffixes;
             case Neither:
                 return emptySuffixes;
             }
             throw new RuntimeException("Unknown SuffixType: " + this);
         }
     }
     protected static final Pattern sourcePattern = Pattern.compile("\\.(?:rb)$");
     protected static final Pattern extensionPattern = Pattern.compile("\\.(?:so|o|dll|jar)$");
 
     protected RubyArray loadPath;
     protected RubyArray loadedFeatures;
     protected List loadedFeaturesInternal;
     protected final Map<String, Library> builtinLibraries = new HashMap<String, Library>();
 
     protected final Map<String, JarFile> jarFiles = new HashMap<String, JarFile>();
 
     protected final Map<String, IAutoloadMethod> autoloadMap = new HashMap<String, IAutoloadMethod>();
 
     protected final Ruby runtime;
     
     public LoadService(Ruby runtime) {
         this.runtime = runtime;
         if (RubyInstanceConfig.DEBUG_LOAD_TIMINGS) {
             loadTimer = new TracingLoadTimer();
         } else {
             loadTimer = new LoadTimer();
         }
     }
 
     public void init(List additionalDirectories) {
         loadPath = RubyArray.newArray(runtime);
         loadedFeatures = RubyArray.newArray(runtime);
         loadedFeaturesInternal = Collections.synchronizedList(loadedFeatures);
         
         // add all startup load paths to the list first
         for (Iterator iter = additionalDirectories.iterator(); iter.hasNext();) {
             addPath((String) iter.next());
         }
 
         // add $RUBYLIB paths
         RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
         RubyString env_rubylib = runtime.newString("RUBYLIB");
         if (env.has_key_p(env_rubylib).isTrue()) {
             String rubylib = env.op_aref(runtime.getCurrentContext(), env_rubylib).toString();
             String[] paths = rubylib.split(File.pathSeparator);
             for (int i = 0; i < paths.length; i++) {
                 addPath(paths[i]);
             }
         }
 
         // wrap in try/catch for security exceptions in an applet
         try {
             String jrubyHome = runtime.getJRubyHome();
             if (jrubyHome != null) {
                 char sep = '/';
                 String rubyDir = jrubyHome + sep + "lib" + sep + "ruby" + sep;
 
                 // If we're running in 1.9 compat mode, add Ruby 1.9 libs to path before 1.8 libs
                 if (runtime.is1_9()) {
                     addPath(rubyDir + "site_ruby" + sep + Constants.RUBY1_9_MAJOR_VERSION);
                     addPath(rubyDir + "site_ruby" + sep + "shared");
                     addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
                     addPath(rubyDir + Constants.RUBY1_9_MAJOR_VERSION);
                 } else {
                     // Add 1.8 libs
                     addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
                     addPath(rubyDir + "site_ruby" + sep + "shared");
                     addPath(rubyDir + Constants.RUBY_MAJOR_VERSION);
                 }
             }
         } catch(SecurityException ignore) {}
         
         // "." dir is used for relative path loads from a given file, as in require '../foo/bar'
         if (runtime.getSafeLevel() == 0) {
             addPath(".");
         }
     }
 
     protected void addLoadedFeature(RubyString loadNameRubyString) {
         loadedFeaturesInternal.add(loadNameRubyString);
     }
 
     protected void addPath(String path) {
         // Empty paths do not need to be added
         if (path == null || path.length() == 0) return;
         
         synchronized(loadPath) {
             loadPath.append(runtime.newString(path.replace('\\', '/')));
         }
     }
 
     public void load(String file, boolean wrap) {
         if(!runtime.getProfile().allowLoad(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
 
         SearchState state = new SearchState(file);
         state.prepareLoadSearch(file);
         
         Library library = findBuiltinLibrary(state, state.searchFile, state.suffixType);
         if (library == null) library = findLibraryWithoutCWD(state, state.searchFile, state.suffixType);
 
         if (library == null) {
             library = findLibraryWithClassloaders(state, state.searchFile, state.suffixType);
             if (library == null) {
                 throw runtime.newLoadError("No such file to load -- " + file);
             }
         }
         try {
             library.load(runtime, wrap);
         } catch (IOException e) {
             if (runtime.getDebug().isTrue()) e.printStackTrace(runtime.getErr());
             throw newLoadErrorFromThrowable(runtime, file, e);
         }
     }
 
     public SearchState findFileForLoad(String file) throws AlreadyLoaded {
         SearchState state = new SearchState(file);
         state.prepareRequireSearch(file);
 
         for (LoadSearcher searcher : searchers) {
             if (searcher.shouldTrySearch(state)) {
                 searcher.trySearch(state);
             } else {
                 continue;
             }
         }
 
         return state;
     }
 
     public boolean lockAndRequire(String requireName) {
         Object requireLock;
         try {
             synchronized (requireLocks) {
                 requireLock = requireLocks.get(requireName);
                 if (requireLock == null) {
                     requireLock = new Object();
                     requireLocks.put(requireName, requireLock);
                 }
             }
 
             synchronized (requireLock) {
                 return require(requireName);
             }
         } finally {
             synchronized (requireLocks) {
                 requireLocks.remove(requireName);
             }
         }
     }
 
     protected Map requireLocks = new Hashtable();
 
     public boolean smartLoad(String file) {
         checkEmptyLoad(file);
 
         // We don't support .so, but some stdlib require .so directly
         // replace it with .jar to look for an extension type we do support
         if (file.endsWith(".so")) {
             file = file.replaceAll(".so$", ".jar");
         }
         if (Platform.IS_WINDOWS) {
             file = file.replace('\\', '/');
         }
         
         try {
             SearchState state = findFileForLoad(file);
             return tryLoadingLibraryOrScript(runtime, state);
         } catch (AlreadyLoaded al) {
             // Library has already been loaded in some form, bail out
             return false;
         }
     }
 
     private static class LoadTimer {
         public long startLoad(String file) { return 0L; }
         public void endLoad(String file, long startTime) {}
     }
 
     private static class TracingLoadTimer extends LoadTimer {
         private final AtomicInteger indent = new AtomicInteger(0);
         private String getIndentString() {
             StringBuilder buf = new StringBuilder();
             int i = indent.get();
             for (int j = 0; j < i; j++) {
                 buf.append("  ");
             }
             return buf.toString();
         }
         @Override
         public long startLoad(String file) {
             int i = indent.incrementAndGet();
             System.err.println(getIndentString() + "-> " + file);
             return System.currentTimeMillis();
         }
         @Override
         public void endLoad(String file, long startTime) {
             System.err.println(getIndentString() + "<- " + file + " - "
                     + (System.currentTimeMillis() - startTime) + "ms");
             indent.decrementAndGet();
         }
     }
 
     public boolean require(String file) {
         if(!runtime.getProfile().allowRequire(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
 
         long startTime = loadTimer.startLoad(file);
         try {
             return smartLoad(file);
         } finally {
             loadTimer.endLoad(file, startTime);
         }
 
     }
 
     /**
      * Load the org.jruby.runtime.load.Library implementation specified by
      * className. The purpose of using this method is to avoid having static
      * references to the given library class, thereby avoiding the additional
      * classloading when the library is not in use.
      * 
      * @param runtime The runtime in which to load
      * @param libraryName The name of the library, to use for error messages
      * @param className The class of the library
      * @param classLoader The classloader to use to load it
      * @param wrap Whether to wrap top-level in an anonymous module
      */
     public static void reflectedLoad(Ruby runtime, String libraryName, String className, ClassLoader classLoader, boolean wrap) {
         try {
             if (classLoader == null && Ruby.isSecurityRestricted()) {
                 classLoader = runtime.getInstanceConfig().getLoader();
             }
 
             Library library = (Library) classLoader.loadClass(className).newInstance();
 
             library.load(runtime, false);
         } catch (RaiseException re) {
             throw re;
         } catch (Throwable e) {
             if (runtime.getDebug().isTrue()) e.printStackTrace();
             throw runtime.newLoadError("library `" + libraryName + "' could not be loaded: " + e);
         }
     }
 
     public IRubyObject getLoadPath() {
         return loadPath;
     }
 
     public IRubyObject getLoadedFeatures() {
         return loadedFeatures;
     }
 
     public IAutoloadMethod autoloadFor(String name) {
         return autoloadMap.get(name);
     }
 
     public void removeAutoLoadFor(String name) {
         autoloadMap.remove(name);
     }
 
     public IRubyObject autoload(String name) {
         IAutoloadMethod loadMethod = autoloadMap.remove(name);
         if (loadMethod != null) {
             return loadMethod.load(runtime, name);
         }
         return null;
     }
 
     public void addAutoload(String name, IAutoloadMethod loadMethod) {
         autoloadMap.put(name, loadMethod);
     }
 
     public void addBuiltinLibrary(String name, Library library) {
         builtinLibraries.put(name, library);
     }
 
     public void removeBuiltinLibrary(String name) {
         builtinLibraries.remove(name);
     }
 
     public void removeInternalLoadedFeature(String name) {
         loadedFeaturesInternal.remove(name);
     }
 
     protected boolean featureAlreadyLoaded(RubyString loadNameRubyString) {
         return loadedFeaturesInternal.contains(loadNameRubyString);
     }
 
     protected boolean isJarfileLibrary(SearchState state, final String file) {
         return state.library instanceof JarredScript && file.endsWith(".jar");
     }
 
     protected void removeLoadedFeature(RubyString loadNameRubyString) {
 
         loadedFeaturesInternal.remove(loadNameRubyString);
     }
 
     protected void reraiseRaiseExceptions(Throwable e) throws RaiseException {
         if (e instanceof RaiseException) {
             throw (RaiseException) e;
         }
     }
     
     public interface LoadSearcher {
         public boolean shouldTrySearch(SearchState state);
         public void trySearch(SearchState state) throws AlreadyLoaded;
     }
     
     public class AlreadyLoaded extends Exception {
         private RubyString searchNameString;
         
         public AlreadyLoaded(RubyString searchNameString) {
             this.searchNameString = searchNameString;
         }
         
         public RubyString getSearchNameString() {
             return searchNameString;
         }
     }
     
     public class BailoutSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return true;
         }
     
         public void trySearch(SearchState state) throws AlreadyLoaded {
             for (String suffix : state.suffixType.getSuffixes()) {
                 String searchName = state.searchFile + suffix;
                 RubyString searchNameString = RubyString.newString(runtime, searchName);
                 if (featureAlreadyLoaded(searchNameString)) {
                     throw new AlreadyLoaded(searchNameString);
                 }
             }
         }
     }
 
     public class NormalSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
         
         public void trySearch(SearchState state) {
             state.library = findLibraryWithoutCWD(state, state.searchFile, state.suffixType);
         }
     }
 
     public class ClassLoaderSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
         
         public void trySearch(SearchState state) {
             state.library = findLibraryWithClassloaders(state, state.searchFile, state.suffixType);
         }
     }
 
     public class ExtensionSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return (state.library == null || state.library instanceof JarredScript) && !state.searchFile.equalsIgnoreCase("");
         }
         
         public void trySearch(SearchState state) {
             // This code exploits the fact that all .jar files will be found for the JarredScript feature.
             // This is where the basic extension mechanism gets fixed
             Library oldLibrary = state.library;
 
             // Create package name, by splitting on / and joining all but the last elements with a ".", and downcasing them.
             String[] all = state.searchFile.split("/");
 
             StringBuilder finName = new StringBuilder();
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
                 if(state.library instanceof JarredScript) {
                     // It's _really_ expensive to check that the class actually exists in the Jar, so
                     // we don't do that now.
                     runtime.getJRubyClassLoader().addURL(((JarredScript)state.library).getResource().getURL());
                 }
 
                 // quietly try to load the class
                 Class theClass = runtime.getJavaSupport().loadJavaClassQuiet(className);
                 state.library = new ClassExtensionLibrary(theClass);
             } catch (Exception ee) {
                 state.library = null;
                 runtime.getGlobalVariables().set("$!", runtime.getNil());
             }
 
             // If there was a good library before, we go back to that
             if(state.library == null && oldLibrary != null) {
                 state.library = oldLibrary;
             }
         }
     }
 
     public class ScriptClassSearcher implements LoadSearcher {
         public class ScriptClassLibrary implements Library {
             private Script script;
 
             public ScriptClassLibrary(Script script) {
                 this.script = script;
             }
 
             public void load(Ruby runtime, boolean wrap) {
                 runtime.loadScript(script);
             }
         }
         
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
         
         public void trySearch(SearchState state) throws RaiseException {
             // no library or extension found, try to load directly as a class
             Script script;
             String className = buildClassName(state.searchFile);
             int lastSlashIndex = className.lastIndexOf('/');
             if (lastSlashIndex > -1 && lastSlashIndex < className.length() - 1 && !Character.isJavaIdentifierStart(className.charAt(lastSlashIndex + 1))) {
                 if (lastSlashIndex == -1) {
                     className = "_" + className;
                 } else {
                     className = className.substring(0, lastSlashIndex + 1) + "_" + className.substring(lastSlashIndex + 1);
                 }
             }
             className = className.replace('/', '.');
             try {
                 Class scriptClass = Class.forName(className);
                 script = (Script) scriptClass.newInstance();
             } catch (Exception cnfe) {
                 throw runtime.newLoadError("no such file to load -- " + state.searchFile);
             }
             state.library = new ScriptClassLibrary(script);
         }
     }
 
     public class SearchState {
         public Library library;
         public String loadName;
         public SuffixType suffixType;
         public String searchFile;
         
         public SearchState(String file) {
             loadName = file;
         }
 
         public void prepareRequireSearch(final String file) {
             // if an extension is specified, try more targetted searches
             if (file.lastIndexOf('.') > file.lastIndexOf('/')) {
                 Matcher matcher = null;
                 if ((matcher = sourcePattern.matcher(file)).find()) {
                     // source extensions
                     suffixType = SuffixType.Source;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else if ((matcher = extensionPattern.matcher(file)).find()) {
                     // extension extensions
                     suffixType = SuffixType.Extension;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else {
                     // unknown extension, fall back to search with extensions
                     suffixType = SuffixType.Both;
                     searchFile = file;
                 }
             } else {
                 // try all extensions
                 suffixType = SuffixType.Both;
                 searchFile = file;
             }
         }
 
         public void prepareLoadSearch(final String file) {
             // if a source extension is specified, try all source extensions
             if (file.lastIndexOf('.') > file.lastIndexOf('/')) {
                 Matcher matcher = null;
                 if ((matcher = sourcePattern.matcher(file)).find()) {
                     // source extensions
                     suffixType = SuffixType.Source;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else {
                     // unknown extension, fall back to exact search
                     suffixType = SuffixType.Neither;
                     searchFile = file;
                 }
             } else {
                 // try only literal search
                 suffixType = SuffixType.Neither;
                 searchFile = file;
             }
         }
     }
     
     protected boolean tryLoadingLibraryOrScript(Ruby runtime, SearchState state) {
         // attempt to load the found library
         RubyString loadNameRubyString = RubyString.newString(runtime, state.loadName);
         try {
             synchronized (loadedFeaturesInternal) {
                 if (loadedFeaturesInternal.contains(loadNameRubyString)) {
                     return false;
                 } else {
                     addLoadedFeature(loadNameRubyString);
                 }
             }
             
             // otherwise load the library we've found
             state.library.load(runtime, false);
             return true;
         } catch (MainExitException mee) {
             // allow MainExitException to propagate out for exec and friends
             throw mee;
         } catch (Throwable e) {
             if(isJarfileLibrary(state, state.searchFile)) {
                 return true;
             }
 
             removeLoadedFeature(loadNameRubyString);
             reraiseRaiseExceptions(e);
 
             if(runtime.getDebug().isTrue()) e.printStackTrace(runtime.getErr());
             
             RaiseException re = newLoadErrorFromThrowable(runtime, state.searchFile, e);
             re.initCause(e);
             throw re;
         }
     }
 
     private static RaiseException newLoadErrorFromThrowable(Ruby runtime, String file, Throwable t) {
         return runtime.newLoadError(String.format("load error: %s -- %s: %s", file, t.getClass().getName(), t.getMessage()));
     }
     
     protected final List<LoadSearcher> searchers = new ArrayList<LoadSearcher>();
     {
         searchers.add(new BailoutSearcher());
         searchers.add(new NormalSearcher());
         searchers.add(new ClassLoaderSearcher());
         searchers.add(new ExtensionSearcher());
         searchers.add(new ScriptClassSearcher());
     }
 
     protected String buildClassName(String className) {
         // Remove any relative prefix, e.g. "./foo/bar" becomes "foo/bar".
         className = className.replaceFirst("^\\.\\/", "");
         if (className.lastIndexOf(".") != -1) {
             className = className.substring(0, className.lastIndexOf("."));
         }
         className = className.replace("-", "_minus_").replace('.', '_');
         return className;
     }
 
     protected void checkEmptyLoad(String file) throws RaiseException {
         if (file.equals("")) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
     }
 
     protected void debugLogTry(String what, String msg) {
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             runtime.getErr().println( "LoadService: trying " + what + ": " + msg );
         }
     }
 
     protected void debugLogFound(String what, String msg) {
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             runtime.getErr().println( "LoadService: found " + what + ": " + msg );
         }
     }
 
     protected void debugLogFound( LoadServiceResource resource ) {
         String resourceUrl;
         try {
             resourceUrl = resource.getURL().toString();
         } catch (IOException e) {
             resourceUrl = e.getMessage();
         }
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             runtime.getErr().println( "LoadService: found: " + resourceUrl );
         }
     }
     
     protected Library findBuiltinLibrary(SearchState state, String baseName, SuffixType suffixType) {
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = baseName + suffix;
             debugLogTry( "builtinLib",  namePlusSuffix );
             if (builtinLibraries.containsKey(namePlusSuffix)) {
                 state.loadName = namePlusSuffix;
                 Library lib = builtinLibraries.get(namePlusSuffix);
                 debugLogFound( "builtinLib", namePlusSuffix );
                 return lib;
             }
         }
         return null;
     }
 
     protected Library findLibraryWithoutCWD(SearchState state, String baseName, SuffixType suffixType) {
         Library library = null;
         
         switch (suffixType) {
         case Both:
             library = findBuiltinLibrary(state, baseName, SuffixType.Source);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Source));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Source));
             // If we fail to find as a normal Ruby script, we try to find as an extension,
             // checking for a builtin first.
             if (library == null) library = findBuiltinLibrary(state, baseName, SuffixType.Extension);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Extension));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Extension));
             break;
         case Source:
         case Extension:
             // Check for a builtin first.
             library = findBuiltinLibrary(state, baseName, suffixType);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, suffixType));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, suffixType));
             break;
         case Neither:
             library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Neither));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Neither));
             break;
         }
 
         return library;
     }
 
     protected Library findLibraryWithClassloaders(SearchState state, String baseName, SuffixType suffixType) {
         for (String suffix : suffixType.getSuffixes()) {
             String file = baseName + suffix;
             LoadServiceResource resource = findFileInClasspath(file);
             if (resource != null) {
                 state.loadName = resolveLoadName(resource, file);
                 return createLibrary(state, resource);
             }
         }
         return null;
     }
 
     protected Library createLibrary(SearchState state, LoadServiceResource resource) {
         if (resource == null) {
             return null;
         }
         String file = state.loadName;
         if (file.endsWith(".so")) {
             throw runtime.newLoadError("JRuby does not support .so libraries from filesystem");
         } else if (file.endsWith(".jar")) {
             return new JarredScript(resource);
         } else if (file.endsWith(".class")) {
             return new JavaCompiledScript(resource);
         } else {
             return new ExternalScript(resource, file);
         }      
     }
 
     protected LoadServiceResource tryResourceFromCWD(SearchState state, String baseName,SuffixType suffixType) throws RaiseException {
         LoadServiceResource foundResource = null;
         
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = baseName + suffix;
             // check current directory; if file exists, retrieve URL and return resource
             try {
                 JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 debugLogTry("resourceFromCWD", file.toString());
                 if (file.isFile() && file.isAbsolute() && file.canRead()) {
                     boolean absolute = true;
                     String s = namePlusSuffix;
                     if(!namePlusSuffix.startsWith("./")) {
                         s = "./" + s;
                     }
                     foundResource = new LoadServiceResource(file, s, absolute);
                     debugLogFound(foundResource);
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     break;
                 }
             } catch (IllegalArgumentException illArgEx) {
             } catch (SecurityException secEx) {
             }
         }
         
         return foundResource;
     }
 
     protected LoadServiceResource tryResourceFromHome(SearchState state, String baseName, SuffixType suffixType) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
         RubyString env_home = runtime.newString("HOME");
         if (env.has_key_p(env_home).isFalse()) {
             return null;
         }
         String home = env.op_aref(runtime.getCurrentContext(), env_home).toString();
         String path = baseName.substring(2);
 
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = path + suffix;
             // check home directory; if file exists, retrieve URL and return resource
             try {
                 JRubyFile file = JRubyFile.create(home, RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 debugLogTry("resourceFromHome", file.toString());
                 if (file.isFile() && file.isAbsolute() && file.canRead()) {
                     boolean absolute = true;
                     String s = "~/" + namePlusSuffix;
                     
                     foundResource = new LoadServiceResource(file, s, absolute);
                     debugLogFound(foundResource);
                     state.loadName = resolveLoadName(foundResource, s);
                     break;
                 }
             } catch (IllegalArgumentException illArgEx) {
             } catch (SecurityException secEx) {
             }
         }
 
         return foundResource;
     }
     
     protected LoadServiceResource tryResourceFromJarURL(SearchState state, String baseName, SuffixType suffixType) {
         // if a jar or file URL, return load service resource directly without further searching
         LoadServiceResource foundResource = null;
         if (baseName.startsWith("jar:")) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 try {
                     URL url = new URL(namePlusSuffix);
                     debugLogTry("resourceFromJarURL", url.toString());
                     if (url.openStream() != null) {
                         foundResource = new LoadServiceResource(url, namePlusSuffix);
                         debugLogFound(foundResource);
                     }
                 } catch (FileNotFoundException e) {
                 } catch (MalformedURLException e) {
                     throw runtime.newIOErrorFromException(e);
                 } catch (IOException e) {
                     throw runtime.newIOErrorFromException(e);
                 }
                 if (foundResource != null) {
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     break; // end suffix iteration
                 }
             }
         } else if(baseName.startsWith("file:") && baseName.indexOf("!/") != -1) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 try {
                     String jarFile = namePlusSuffix.substring(5, namePlusSuffix.indexOf("!/"));
                     JarFile file = new JarFile(jarFile);
                     String filename = namePlusSuffix.substring(namePlusSuffix.indexOf("!/") + 2);
                     String canonicalFilename = canonicalizePath(filename);
                     
                     debugLogTry("resourceFromJarURL", canonicalFilename.toString());
                     if(file.getJarEntry(canonicalFilename) != null) {
                         foundResource = new LoadServiceResource(new URL("jar:file:" + jarFile + "!/" + canonicalFilename), namePlusSuffix);
                         debugLogFound(foundResource);
                     }
                 } catch(Exception e) {}
                 if (foundResource != null) {
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     break; // end suffix iteration
                 }
             }    
         }
         
         return foundResource;
     }
     
     protected LoadServiceResource tryResourceFromLoadPathOrURL(SearchState state, String baseName, SuffixType suffixType) {
         LoadServiceResource foundResource = null;
 
         // if it's a ./ baseName, use CWD logic
         if (baseName.startsWith("./")) {
             foundResource = tryResourceFromCWD(state, baseName, suffixType);
 
             if (foundResource != null) {
                 state.loadName = resolveLoadName(foundResource, foundResource.getName());
                 return foundResource;
             }
         }
 
         // if it's a ~/ baseName use HOME logic
         if (baseName.startsWith("~/")) {
             foundResource = tryResourceFromHome(state, baseName, suffixType);
 
             if (foundResource != null) {
                 state.loadName = resolveLoadName(foundResource, foundResource.getName());
                 return foundResource;
             }
         }
 
         // if given path is absolute, just try it as-is (with extensions) and no load path
         if (new File(baseName).isAbsolute() || baseName.startsWith("../")) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 foundResource = tryResourceAsIs(namePlusSuffix);
 
                 if (foundResource != null) {
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     return foundResource;
                 }
             }
 
             return null;
         }
         
         Outer: for (int i = 0; i < loadPath.size(); i++) {
             // TODO this is really inefficient, and potentially a problem everytime anyone require's something.
             // we should try to make LoadPath a special array object.
             RubyString entryString = loadPath.eltInternal(i).convertToString();
             String loadPathEntry = entryString.asJavaString();
 
             if (loadPathEntry.equals(".") || loadPathEntry.equals("")) {
                 foundResource = tryResourceFromCWD(state, baseName, suffixType);
 
                 if (foundResource != null) {
                     String ss = foundResource.getName();
                     if(ss.startsWith("./")) {
                         ss = ss.substring(2);
                     }
                     state.loadName = resolveLoadName(foundResource, ss);
                     break Outer;
                 }
             } else {
                 boolean looksLikeJarURL = loadPathLooksLikeJarURL(loadPathEntry);
                 for (String suffix : suffixType.getSuffixes()) {
                     String namePlusSuffix = baseName + suffix;
 
                     if (looksLikeJarURL) {
                         foundResource = tryResourceFromJarURLWithLoadPath(namePlusSuffix, loadPathEntry);
                     } else if(namePlusSuffix.startsWith("./")) {
                         throw runtime.newLoadError("");
                     } else {
                         foundResource = tryResourceFromLoadPath(namePlusSuffix, loadPathEntry);
                     }
 
                     if (foundResource != null) {
                         String ss = namePlusSuffix;
                         if(ss.startsWith("./")) {
                             ss = ss.substring(2);
                         }
                         state.loadName = resolveLoadName(foundResource, ss);
                         break Outer; // end suffix iteration
                     }
                 }
             }
         }
         
         return foundResource;
     }
     
     protected LoadServiceResource tryResourceFromJarURLWithLoadPath(String namePlusSuffix, String loadPathEntry) {
         LoadServiceResource foundResource = null;
         
         JarFile current = jarFiles.get(loadPathEntry);
         boolean isFileJarUrl = loadPathEntry.startsWith("file:") && loadPathEntry.indexOf("!/") != -1;
         String after = isFileJarUrl ? loadPathEntry.substring(loadPathEntry.indexOf("!/") + 2) + "/" : "";
         String before = isFileJarUrl ? loadPathEntry.substring(0, loadPathEntry.indexOf("!/")) : loadPathEntry;
 
         if(null == current) {
             try {
                 if(loadPathEntry.startsWith("jar:")) {
                     current = new JarFile(loadPathEntry.substring(4));
                 } else if (loadPathEntry.endsWith(".jar")) {
                     current = new JarFile(loadPathEntry);
                 } else {
                     current = new JarFile(loadPathEntry.substring(5,loadPathEntry.indexOf("!/")));
                 }
                 jarFiles.put(loadPathEntry,current);
             } catch (ZipException ignored) {
                 if (runtime.getInstanceConfig().isDebug()) {
                     runtime.getErr().println("ZipException trying to access " + loadPathEntry + ", stack trace follows:");
                     ignored.printStackTrace(runtime.getErr());
                 }
             } catch (FileNotFoundException ignored) {
             } catch (IOException e) {
                 throw runtime.newIOErrorFromException(e);
             }
         }
         String canonicalEntry = after+namePlusSuffix;
         if (current != null ) {
             debugLogTry("resourceFromJarURLWithLoadPath", current.getName() + "!/" + canonicalEntry);
             if (current.getJarEntry(canonicalEntry) != null) {
                 try {
                     if (loadPathEntry.endsWith(".jar")) {
                         foundResource = new LoadServiceResource(new URL("jar:file:" + loadPathEntry + "!/" + canonicalEntry), "/" + namePlusSuffix);
                     } else if (loadPathEntry.startsWith("file:")) {
                         foundResource = new LoadServiceResource(new URL("jar:" + before + "!/" + canonicalEntry), loadPathEntry + "/" + namePlusSuffix);
                     } else {
                         foundResource =  new LoadServiceResource(new URL("jar:file:" + loadPathEntry.substring(4) + "!/" + namePlusSuffix), loadPathEntry + namePlusSuffix);
                     }
                     debugLogFound(foundResource);
                 } catch (MalformedURLException e) {
                     throw runtime.newIOErrorFromException(e);
                 }
             }
         }
         
         return foundResource;
     }
 
     protected boolean loadPathLooksLikeJarURL(String loadPathEntry) {
         return loadPathEntry.startsWith("jar:") || loadPathEntry.endsWith(".jar") || (loadPathEntry.startsWith("file:") && loadPathEntry.indexOf("!/") != -1);
     }
 
     protected LoadServiceResource tryResourceFromLoadPath( String namePlusSuffix,String loadPathEntry) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         try {
             if (!Ruby.isSecurityRestricted()) {
                 String reportedPath = loadPathEntry + "/" + namePlusSuffix;
                 JRubyFile actualPath;
                 boolean absolute = false;
                 // we check length == 0 for 'load', which does not use load path
                 if (new File(reportedPath).isAbsolute()) {
                     absolute = true;
                     // it's an absolute path, use it as-is
                     actualPath = JRubyFile.create(loadPathEntry, RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 } else {
                     absolute = false;
                     // prepend ./ if . is not already there, since we're loading based on CWD
                     if (reportedPath.charAt(0) != '.') {
                         reportedPath = "./" + reportedPath;
                     }
                     actualPath = JRubyFile.create(JRubyFile.create(runtime.getCurrentDirectory(), loadPathEntry).getAbsolutePath(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 }
-                debugLogTry("resourceFromLoadPath", actualPath.toString());
+                debugLogTry("resourceFromLoadPath", "'" + actualPath.toString() + "' " + actualPath.isFile() + " " + actualPath.canRead());
                 if (actualPath.isFile() && actualPath.canRead()) {
                     foundResource = new LoadServiceResource(actualPath, reportedPath, absolute);
                     debugLogFound(foundResource);
                 }
             }
         } catch (SecurityException secEx) {
         }
 
         return foundResource;
     }
 
     protected LoadServiceResource tryResourceAsIs(String namePlusSuffix) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         try {
             if (!Ruby.isSecurityRestricted()) {
                 String reportedPath = namePlusSuffix;
                 File actualPath;
                 // we check length == 0 for 'load', which does not use load path
                 if (new File(reportedPath).isAbsolute()) {
                     // it's an absolute path, use it as-is
                     actualPath = new File(RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 } else {
                     // prepend ./ if . is not already there, since we're loading based on CWD
                     if (reportedPath.charAt(0) == '.' && reportedPath.charAt(1) == '/') {
                         reportedPath = reportedPath.replaceFirst("\\./", runtime.getCurrentDirectory());
                     }
 
                     actualPath = JRubyFile.create(runtime.getCurrentDirectory(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 }
                 debugLogTry("resourceAsIs", actualPath.toString());
                 if (actualPath.isFile() && actualPath.canRead()) {
                     foundResource = new LoadServiceResource(actualPath, reportedPath);
                     debugLogFound(foundResource);
                 }
             }
         } catch (SecurityException secEx) {
         }
 
         return foundResource;
     }
 
     /**
      * this method uses the appropriate lookup strategy to find a file.
      * It is used by Kernel#require.
      *
      * @mri rb_find_file
      * @param name the file to find, this is a path name
      * @return the correct file
      */
     protected LoadServiceResource findFileInClasspath(String name) {
         // Look in classpath next (we do not use File as a test since UNC names will match)
         // Note: Jar resources must NEVER begin with an '/'. (previous code said "always begin with a /")
         ClassLoader classLoader = runtime.getJRubyClassLoader();
 
         // handle security-sensitive case
         if (Ruby.isSecurityRestricted() && classLoader == null) {
             classLoader = runtime.getInstanceConfig().getLoader();
         }
 
         // absolute classpath URI, no need to iterate over loadpaths
         if (name.startsWith("classpath:/")) {
             LoadServiceResource foundResource = getClassPathResource(classLoader, name);
             if (foundResource != null) {
                 return foundResource;
             }
         } else if (name.startsWith("classpath:")) {
             // "relative" classpath URI
             name = name.substring("classpath:".length());
         }
 
         for (int i = 0; i < loadPath.size(); i++) {
             // TODO this is really inefficient, and potentially a problem everytime anyone require's something.
             // we should try to make LoadPath a special array object.
             RubyString entryString = loadPath.eltInternal(i).convertToString();
             String entry = entryString.asJavaString();
 
             // if entry is an empty string, skip it
             if (entry.length() == 0) continue;
 
             // if entry starts with a slash, skip it since classloader resources never start with a /
             if (entry.charAt(0) == '/' || (entry.length() > 1 && entry.charAt(1) == ':')) continue;
 
             if (entry.startsWith("classpath:/")) {
                 entry = entry.substring("classpath:/".length());
             } else if (entry.startsWith("classpath:")) {
                 entry = entry.substring("classpath:".length());
             }
 
             // otherwise, try to load from classpath (Note: Jar resources always uses '/')
             LoadServiceResource foundResource = getClassPathResource(classLoader, entry + "/" + name);
             if (foundResource != null) {
                 return foundResource;
             }
         }
 
         // if name starts with a / we're done (classloader resources won't load with an initial /)
         if (name.charAt(0) == '/' || (name.length() > 1 && name.charAt(1) == ':')) return null;
         
         // Try to load from classpath without prefix. "A/b.rb" will not load as 
         // "./A/b.rb" in a jar file.
         LoadServiceResource foundResource = getClassPathResource(classLoader, name);
         if (foundResource != null) {
             return foundResource;
         }
 
         return null;
     }
     
     /* Directories and unavailable resources are not able to open a stream. */
     protected boolean isRequireable(URL loc) {
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
 
     protected LoadServiceResource getClassPathResource(ClassLoader classLoader, String name) {
         boolean isClasspathScheme = false;
 
         // strip the classpath scheme first
         if (name.startsWith("classpath:/")) {
             isClasspathScheme = true;
             name = name.substring("classpath:/".length());
         } else if (name.startsWith("classpath:")) {
             isClasspathScheme = true;
             name = name.substring("classpath:".length());
         }
 
         debugLogTry("fileInClasspath", name);
         URL loc = classLoader.getResource(name);
 
         if (loc != null) { // got it
             String path = "classpath:/" + name;
             // special case for typical jar:file URLs, but only if the name didn't have
             // the classpath scheme explicitly
             if (!isClasspathScheme && loc.toString().startsWith("jar:file:") && isRequireable(loc)) {
                 // Make sure this is not a directory or unavailable in some way
                 path = loc.getPath();
             }
             LoadServiceResource foundResource = new LoadServiceResource(loc, path);
             debugLogFound(foundResource);
             return foundResource;
         }
         return null;
     }
     
     protected String canonicalizePath(String path) {
         try {
             String cwd = new File(runtime.getCurrentDirectory()).getCanonicalPath();
             return new File(path).getCanonicalPath()
                                  .substring(cwd.length() + 1)
                                  .replaceAll("\\\\","/");
       } catch(Exception e) {
         return path;
       }
     }
 
     protected String resolveLoadName(LoadServiceResource foundResource, String previousPath) {
         return previousPath;
     }
 }
diff --git a/src/org/jruby/util/Dir.java b/src/org/jruby/util/Dir.java
index 9f13db5d80..400ee0073f 100644
--- a/src/org/jruby/util/Dir.java
+++ b/src/org/jruby/util/Dir.java
@@ -1,814 +1,803 @@
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
  * Copyright (C) 2007, 2008 Ola Bini <ola@ologix.com>
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
 package org.jruby.util;
 
 import java.io.File;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Enumeration;
 import java.util.List;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
+import org.jruby.RubyEncoding;
 
 import org.jruby.ext.posix.JavaSecuredFile;
 import org.jruby.platform.Platform;
 
 /**
  * This class exists as a counterpart to the dir.c file in 
  * MRI source. It contains many methods useful for 
  * File matching and Globbing.
  *
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class Dir {
     public final static boolean DOSISH = Platform.IS_WINDOWS;
     public final static boolean CASEFOLD_FILESYSTEM = DOSISH;
 
     public final static int FNM_NOESCAPE = 0x01;
     public final static int FNM_PATHNAME = 0x02;
     public final static int FNM_DOTMATCH = 0x04;
     public final static int FNM_CASEFOLD = 0x08;
 
     public final static int FNM_SYSCASE = CASEFOLD_FILESYSTEM ? FNM_CASEFOLD : 0;
 
     public final static int FNM_NOMATCH = 1;
     public final static int FNM_ERROR   = 2;
 
     public final static byte[] EMPTY = new byte[0];
     public final static byte[] SLASH = new byte[]{'/'};
     public final static byte[] STAR = new byte[]{'*'};
     public final static byte[] DOUBLE_STAR = new byte[]{'*','*'};
 
     private static boolean isdirsep(byte c) {
         return DOSISH ? (c == '\\' || c == '/') : c == '/';
     }
 
     private static int rb_path_next(byte[] _s, int s, int send) {
         while(s < send && !isdirsep(_s[s])) {
             s++;
         }
         return s;
     }
 
     private static int fnmatch_helper(byte[] bytes, int pstart, int pend, byte[] string, int sstart, int send, int flags) {
         char test;
         int s = sstart;
         int pat = pstart;
         boolean escape = (flags & FNM_NOESCAPE) == 0;
         boolean pathname = (flags & FNM_PATHNAME) != 0;
         boolean period = (flags & FNM_DOTMATCH) == 0;
         boolean nocase = (flags & FNM_CASEFOLD) != 0;
 
         while(pat<pend) {
             byte c = bytes[pat++];
             switch(c) {
             case '?':
                 if(s >= send || (pathname && isdirsep(string[s])) || 
                    (period && string[s] == '.' && (s == 0 || (pathname && isdirsep(string[s-1]))))) {
                     return FNM_NOMATCH;
                 }
                 s++;
                 break;
             case '*':
                 while(pat < pend && (c = bytes[pat++]) == '*');
                 if(s < send && (period && string[s] == '.' && (s == 0 || (pathname && isdirsep(string[s-1]))))) {
                     return FNM_NOMATCH;
                 }
                 if(pat > pend || (pat == pend && c == '*')) {
                     if(pathname && rb_path_next(string, s, send) < send) {
                         return FNM_NOMATCH;
                     } else {
                         return 0;
                     }
                 } else if((pathname && isdirsep(c))) {
                     s = rb_path_next(string, s, send);
                     if(s < send) {
                         s++;
                         break;
                     }
                     return FNM_NOMATCH;
                 }
                 test = (char)((escape && c == '\\' && pat < pend ? bytes[pat] : c)&0xFF);
                 test = Character.toLowerCase(test);
                 pat--;
                 while(s < send) {
                     if((c == '?' || c == '[' || Character.toLowerCase((char) string[s]) == test) &&
                        fnmatch(bytes, pat, pend, string, s, send, flags | FNM_DOTMATCH) == 0) {
                         return 0;
                     } else if((pathname && isdirsep(string[s]))) {
                         break;
                     }
                     s++;
                 }
                 return FNM_NOMATCH;
             case '[':
                 if(s >= send || (pathname && isdirsep(string[s]) || 
                                  (period && string[s] == '.' && (s == 0 || (pathname && isdirsep(string[s-1])))))) {
                     return FNM_NOMATCH;
                 }
                 pat = range(bytes, pat, pend, (char)(string[s]&0xFF), flags);
                 if(pat == -1) {
                     return FNM_NOMATCH;
                 }
                 s++;
                 break;
             case '\\':
                 if (escape) {
                     if (pat >= pend) {
                         c = '\\';
                     } else {
                         c = bytes[pat++];
                     }
                 }
             default:
                 if(s >= send) {
                     return FNM_NOMATCH;
                 }
                 if(DOSISH && (pathname && isdirsep(c) && isdirsep(string[s]))) {
                 } else {
                     if (nocase) {
                         if(Character.toLowerCase((char)c) != Character.toLowerCase((char)string[s])) {
                             return FNM_NOMATCH;
                         }
                         
                     } else {
                         if(c != (char)string[s]) {
                             return FNM_NOMATCH;
                         }
                     }
                     
                 }
                 s++;
                 break;
             }
         }
         return s >= send ? 0 : FNM_NOMATCH;
     }
 
     public static int fnmatch(
             byte[] bytes, int pstart, int pend,
             byte[] string, int sstart, int send, int flags) {
         
         // This method handles '**/' patterns and delegates to
         // fnmatch_helper for the main work.
 
         boolean period = (flags & FNM_DOTMATCH) == 0;
         boolean pathname = (flags & FNM_PATHNAME) != 0;
 
         int pat_pos = pstart;
         int str_pos = sstart;
         int ptmp = -1;
         int stmp = -1;
 
         if (pathname) {
             while (true) {
                 if (isDoubleStarAndSlash(bytes, pat_pos)) {
                     do { pat_pos += 3; } while (isDoubleStarAndSlash(bytes, pat_pos));
                     ptmp = pat_pos;
                     stmp = str_pos;
                 }
 
                 int patSlashIdx = nextSlashIndex(bytes, pat_pos, pend);
                 int strSlashIdx = nextSlashIndex(string, str_pos, send);
 
                 if (fnmatch_helper(bytes, pat_pos, patSlashIdx,
                         string, str_pos, strSlashIdx, flags) == 0) {
                     if (patSlashIdx < pend && strSlashIdx < send) {
                         pat_pos = ++patSlashIdx;
                         str_pos = ++strSlashIdx;
                         continue;
                     }
                     if (patSlashIdx == pend && strSlashIdx == send) {
                         return 0;
                     }
                 }
                 /* failed : try next recursion */
                 if (ptmp != -1 && stmp != -1 && !(period && string[stmp] == '.')) {
                     stmp = nextSlashIndex(string, stmp, send);
                     if (stmp < send) {
                         pat_pos = ptmp;
                         stmp++;
                         str_pos = stmp;
                         continue;
                     }
                 }
                 return FNM_NOMATCH;
             }
         } else {
             return fnmatch_helper(bytes, pstart, pend, string, sstart, send, flags);
         }
 
     }
 
     // are we at '**/'
     private static boolean isDoubleStarAndSlash(byte[] bytes, int pos) {
         if ((bytes.length - pos) <= 2) {
             return false; // not enough bytes
         }
 
         return bytes[pos] == '*'
             && bytes[pos + 1] == '*'
             && bytes[pos + 2] == '/';
     }
 
     // Look for slash, starting from 'start' position, until 'end'.
     private static int nextSlashIndex(byte[] bytes, int start, int end) {
         int idx = start;
         while (idx < end && idx < bytes.length && bytes[idx] != '/') {
             idx++;
         }
         return idx;
     }
 
     public static int range(byte[] _pat, int pat, int pend, char test, int flags) {
         boolean not;
         boolean ok = false;
         boolean nocase = (flags & FNM_CASEFOLD) != 0;
         boolean escape = (flags & FNM_NOESCAPE) == 0;
 
         not = _pat[pat] == '!' || _pat[pat] == '^';
         if(not) {
             pat++;
         }
 
         if (nocase) {
             test = Character.toLowerCase(test);
         }
 
         while(_pat[pat] != ']') {
             char cstart, cend;
             if(escape && _pat[pat] == '\\') {
                 pat++;
             }
             if(pat >= pend) {
                 return -1;
             }
             cstart = cend = (char)(_pat[pat++]&0xFF);
             if(_pat[pat] == '-' && _pat[pat+1] != ']') {
                 pat++;
                 if(escape && _pat[pat] == '\\') {
                     pat++;
                 }
                 if(pat >= pend) {
                     return -1;
                 }
 
                 cend = (char)(_pat[pat++] & 0xFF);
             }
 
             if (nocase) {
                 if (Character.toLowerCase(cstart) <= test
                         && test <= Character.toLowerCase(cend)) {
                     ok = true;
                 }
             } else {
                 if (cstart <= test && test <= cend) {
                     ok = true;
                 }
             }
         }
 
         return ok == not ? -1 : pat + 1;
     }
 
     public static List<ByteList> push_glob(String cwd, ByteList globByteList, int flags) {
         List<ByteList> result = new ArrayList<ByteList>();
         if (globByteList.length() > 0) {
             push_braces(cwd, result, new GlobPattern(globByteList, flags));
         }
 
         return result;
     }
     
     private static class GlobPattern {
         final byte[] bytes;        
         final int begin;
         final int end;
         
         int flags;
         int index;
 
         public GlobPattern(ByteList bytelist, int flags) {
             this(bytelist.getUnsafeBytes(), bytelist.getBegin(),  bytelist.getBegin() + bytelist.getRealSize(), flags);
         }
         
         public GlobPattern(byte[] bytes, int index, int end, int flags) {
             this.bytes = bytes;
             this.index = index;
             this.begin = index;
             this.end = end;
             this.flags = flags;
         }
         
         public int findClosingIndexOf(int leftTokenIndex) {
             if (leftTokenIndex == -1 || leftTokenIndex > end) return -1;
             
             byte leftToken = bytes[leftTokenIndex];
             byte rightToken;
             
             switch (leftToken) {
             case '{': rightToken = '}'; break;
             case '[': rightToken = ']'; break;
             default: return -1;
             }
             
             int nest = 1; // leftToken made us start as nest 1
             index = leftTokenIndex + 1;
             while (hasNext()) {
                 byte c = next();
                 
                 if (c == leftToken) {
                     nest++;
                 } else if (c == rightToken && --nest == 0) {
                     return index();
                 }
             }
             
             return -1;
         }
         
         public boolean hasNext() {
             return index < end;
         }
         
         public void reset() {
             index = begin;
         }
         
         public void setIndex(int value) {
             index = value;
         }
         
         // Get index of last read byte
         public int index() {
             return index - 1;
         }
         
         public int indexOf(byte c) {
             while (hasNext()) if (next() == c) return index();
             
             return -1;
         }
         
         public byte next() {
             return bytes[index++];
         }
 
     }
 
     private static interface GlobFunc {
         int call(byte[] ptr, int p, int len, Object ary);
     }
 
     private static class GlobArgs {
         GlobFunc func;
         int c = -1;
         List<ByteList> v;
         
         public GlobArgs(GlobFunc func, List<ByteList> arg) {
             this.func = func;
             this.v = arg;
         }
     }
 
     public final static GlobFunc push_pattern = new GlobFunc() {
             @SuppressWarnings("unchecked")
             public int call(byte[] ptr, int p, int len, Object ary) {
                 ((List) ary).add(new ByteList(ptr, p, len));
                 return 0;
             }
         };
     public final static GlobFunc glob_caller = new GlobFunc() {
         public int call(byte[] ptr, int p, int len, Object ary) {
             GlobArgs args = (GlobArgs)ary;
             args.c = p;
             return args.func.call(ptr, args.c, len, args.v);
         }
     };
 
     /*
      * Process {}'s (example: Dir.glob("{jruby,jython}/README*") 
      */
     private static int push_braces(String cwd, List<ByteList> result, GlobPattern pattern) {
         pattern.reset();
         int lbrace = pattern.indexOf((byte) '{'); // index of left-most brace
         int rbrace = pattern.findClosingIndexOf(lbrace);// index of right-most brace
 
         // No or mismatched braces..Move along..nothing to see here
         if (lbrace == -1 || rbrace == -1) return push_globs(cwd, result, pattern); 
 
         // Peel onion...make subpatterns out of outer layer of glob and recall with each subpattern 
         // Example: foo{a{c},b}bar -> fooa{c}bar, foobbar
         ByteList buf = new ByteList(20);
         int middleRegionIndex;
         int i = lbrace;
         while (pattern.bytes[i] != '}') {
             middleRegionIndex = i + 1;
             for(i = middleRegionIndex; i < pattern.end && pattern.bytes[i] != '}' && pattern.bytes[i] != ','; i++) {
                 if (pattern.bytes[i] == '{') i = pattern.findClosingIndexOf(i); // skip inner braces
             }
 
             buf.length(0);
             buf.append(pattern.bytes, pattern.begin, lbrace - pattern.begin);
             buf.append(pattern.bytes, middleRegionIndex, i - middleRegionIndex);
             buf.append(pattern.bytes, rbrace + 1, pattern.end - (rbrace + 1));
             int status = push_braces(cwd, result, new GlobPattern(buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize(),pattern.flags));
             if(status != 0) return status;
         }
         
         return 0; // All braces pushed..
     }
 
     private static int push_globs(String cwd, List<ByteList> ary, GlobPattern pattern) {
         pattern.flags |= FNM_SYSCASE;
         return glob_helper(cwd, pattern.bytes, pattern.begin, pattern.end, -1, pattern.flags, glob_caller, new GlobArgs(push_pattern, ary));
     }
 
     private static boolean has_magic(byte[] bytes, int begin, int end, int flags) {
         boolean escape = (flags & FNM_NOESCAPE) == 0;
         boolean nocase = (flags & FNM_CASEFOLD) != 0;
         int open = 0;
 
         for (int i = begin; i < end; i++) {
             switch(bytes[i]) {
             case '?':
             case '*':
                 return true;
             case '[':	/* Only accept an open brace if there is a close */
                 open++;	/* brace to match it.  Bracket expressions must be */
                 continue;	/* complete, according to Posix.2 */
             case ']':
                 if (open > 0) return true;
 
                 continue;
             case '\\':
                 if (escape && i == end) return false;
 
                 break;
             default:
                 if (FNM_SYSCASE == 0 && nocase && Character.isLetter((char)(bytes[i]&0xFF))) return true;
             }
         }
 
         return false;
     }
 
     private static int remove_backslashes(byte[] bytes, int index, int len) {
         int t = index;
         
         for (; index < len; index++, t++) {
             if (bytes[index] == '\\' && ++index == len) break;
             
             bytes[t] = bytes[index];
         }
         
         return t;
     }
 
     private static int strchr(byte[] bytes, int begin, int end, byte ch) {
         for (int i = begin; i < end; i++) {
             if (bytes[i] == ch) return i;
         }
         
         return -1;
     }
 
     private static byte[] extract_path(byte[] bytes, int begin, int end) {
         int len = end - begin;
         
         if (len > 1 && bytes[end-1] == '/' && (!DOSISH || (len < 2 || bytes[end-2] != ':'))) len--;
 
         byte[] alloc = new byte[len];
         System.arraycopy(bytes,begin,alloc,0,len);
         return alloc;
     }
 
     private static byte[] extract_elem(byte[] bytes, int begin, int end) {
         int elementEnd = strchr(bytes, begin, end, (byte)'/');
         if (elementEnd == -1) elementEnd = end;
         
         return extract_path(bytes, begin, elementEnd);
     }
 
     private static boolean BASE(byte[] base) {
         return DOSISH ? 
             (base.length > 0 && !((isdirsep(base[0]) && base.length < 2) || (base.length > 2 && base[1] == ':' && isdirsep(base[2]) && base.length < 4)))
             :
             (base.length > 0 && !(isdirsep(base[0]) && base.length < 2));
     }
     
     private static boolean isJarFilePath(byte[] bytes, int begin, int end) {
         return end > 6 && bytes[begin] == 'f' && bytes[begin+1] == 'i' &&
             bytes[begin+2] == 'l' && bytes[begin+3] == 'e' && bytes[begin+4] == ':';
     }
 
     private static String[] files(File directory) {
         String[] files = directory.list();
         
         if (files != null) {
             String[] filesPlusDotFiles = new String[files.length + 2];
             System.arraycopy(files, 0, filesPlusDotFiles, 2, files.length);
             filesPlusDotFiles[0] = ".";
             filesPlusDotFiles[1] = "..";
 
             return filesPlusDotFiles;
         } else {
             return new String[0];
         }
     }
 
     private static int glob_helper(String cwd, byte[] bytes, int begin, int end, int sub, int flags, GlobFunc func, GlobArgs arg) {
         int p,m;
         int status = 0;
         byte[] newpath = null;
         File st;
         p = sub != -1 ? sub : begin;
         if (!has_magic(bytes, p, end, flags)) {
             if (DOSISH || (flags & FNM_NOESCAPE) == 0) {
                 newpath = new byte[end];
                 System.arraycopy(bytes,0,newpath,0,end);
                 if (sub != -1) {
                     p = (sub - begin);
                     end = remove_backslashes(newpath, p, end);
                     sub = p;
                 } else {
                     end = remove_backslashes(newpath, 0, end);
                     bytes = newpath;
                 }
             }
 
             if (bytes[begin] == '/' || (DOSISH && begin+2<end && bytes[begin+1] == ':' && isdirsep(bytes[begin+2]))) {
                 if (new JavaSecuredFile(newStringFromUTF8(bytes, begin, end - begin)).exists()) {
                     status = func.call(bytes, begin, end, arg);
                 }
             } else if (isJarFilePath(bytes, begin, end)) {
                 int ix = end;
                 for(int i = 0;i<end;i++) {
                     if(bytes[begin+i] == '!') {
                         ix = i;
                         break;
                     }
                 }
 
                 st = new JavaSecuredFile(newStringFromUTF8(bytes, begin+5, ix-5));
                 try {
                     String jar = newStringFromUTF8(bytes, begin+ix+1, end-(ix+1));
                     JarFile jf = new JarFile(st);
                     
                     if (jar.startsWith("/")) jar = jar.substring(1);
                     if (jf.getEntry(jar + "/") != null) jar = jar + "/";
                     if (jf.getEntry(jar) != null) {
                         status = func.call(bytes, begin, end, arg);
                     }
                 } catch(Exception e) {}
             } else if ((end - begin) > 0) { // Length check is a hack.  We should not be reeiving "" as a filename ever. 
                 if (new JavaSecuredFile(cwd, newStringFromUTF8(bytes, begin, end - begin)).exists()) {
                     status = func.call(bytes, begin, end - begin, arg);
                 }
             }
 
             return status;
         }
         
         ByteList buf = new ByteList(20);
         List<ByteList> link = new ArrayList<ByteList>();
         mainLoop: while(p != -1 && status == 0) {
             if (bytes[p] == '/') p++;
 
             m = strchr(bytes, p, end, (byte)'/');
             if(has_magic(bytes, p, m == -1 ? end : m, flags)) {
                 finalize: do {
                     byte[] base = extract_path(bytes, begin, p);
                     byte[] dir = begin == p ? new byte[]{'.'} : base; 
                     byte[] magic = extract_elem(bytes,p,end);
                     boolean recursive = false;
                     String jar = null;
                     JarFile jf = null;
 
                     if(dir[0] == '/'  || (DOSISH && 2<dir.length && dir[1] == ':' && isdirsep(dir[2]))) {
                         st = new JavaSecuredFile(newStringFromUTF8(dir));
                     } else if(isJarFilePath(dir, 0, dir.length)) {
                         int ix = dir.length;
                         for(int i = 0;i<dir.length;i++) {
                             if(dir[i] == '!') {
                                 ix = i;
                                 break;
                             }
                         }
 
                         st = new JavaSecuredFile(newStringFromUTF8(dir, 5, ix-5));
                         jar = newStringFromUTF8(dir, ix+1, dir.length-(ix+1));
                         try {
                             jf = new JarFile(st);
 
                             if (jar.startsWith("/")) jar = jar.substring(1);
                             if (jf.getEntry(jar + "/") != null) jar = jar + "/";
                         } catch(Exception e) {
                             jar = null;
                             jf = null;
                         }
                     } else {
                         st = new JavaSecuredFile(cwd, newStringFromUTF8(dir));
                     }
 
                     if((jf != null && ("".equals(jar) || (jf.getJarEntry(jar) != null && jf.getJarEntry(jar).isDirectory()))) || st.isDirectory()) {
                         if(m != -1 && Arrays.equals(magic, DOUBLE_STAR)) {
                             int n = base.length;
                             recursive = true;
                             buf.length(0);
                             buf.append(base);
                             buf.append(bytes, (base.length > 0 ? m : m + 1), end - (base.length > 0 ? m : m + 1));
                             status = glob_helper(cwd, buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize(), n, flags, func, arg);
                             if(status != 0) {
                                 break finalize;
                             }
                         }
                     } else {
                         break mainLoop;
                     }
 
                     if(jar == null) {
                         String[] dirp = files(st);
 
                         for(int i=0;i<dirp.length;i++) {
                             if(recursive) {
                                 byte[] bs = getBytesInUTF8(dirp[i]);
                                 if (fnmatch(STAR,0,1,bs,0,bs.length,flags) != 0) {
                                     continue;
                                 }
                                 buf.length(0);
                                 buf.append(base);
                                 buf.append( BASE(base) ? SLASH : EMPTY );
                                 buf.append(getBytesInUTF8(dirp[i]));
                                 if (buf.getUnsafeBytes()[0] == '/' || (DOSISH && 2<buf.getRealSize() && buf.getUnsafeBytes()[1] == ':' && isdirsep(buf.getUnsafeBytes()[2]))) {
                                     st = new JavaSecuredFile(newStringFromUTF8(buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize()));
                                 } else {
                                     st = new JavaSecuredFile(cwd, newStringFromUTF8(buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize()));
                                 }
                                 if(st.isDirectory() && !".".equals(dirp[i]) && !"..".equals(dirp[i])) {
                                     int t = buf.getRealSize();
                                     buf.append(SLASH);
                                     buf.append(DOUBLE_STAR);
                                     buf.append(bytes, m, end - m);
                                     status = glob_helper(cwd, buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize(), t, flags, func, arg);
                                     if(status != 0) {
                                         break;
                                     }
                                 }
                                 continue;
                             }
                             byte[] bs = getBytesInUTF8(dirp[i]);
                             if(fnmatch(magic,0,magic.length,bs,0, bs.length,flags) == 0) {
                                 buf.length(0);
                                 buf.append(base);
                                 buf.append( BASE(base) ? SLASH : EMPTY );
                                 buf.append(getBytesInUTF8(dirp[i]));
                                 if(m == -1) {
                                     status = func.call(buf.getUnsafeBytes(),0, buf.getRealSize(),arg);
                                     if(status != 0) {
                                         break;
                                     }
                                     continue;
                                 }
                                 link.add(buf);
                                 buf = new ByteList(20);
                             }
                         }
                     } else {
                         try {
                             List<JarEntry> dirp = new ArrayList<JarEntry>();
                             for(Enumeration<JarEntry> eje = jf.entries(); eje.hasMoreElements(); ) {
                                 JarEntry je = eje.nextElement();
                                 String name = je.getName();
                                 int ix = name.indexOf('/', jar.length());
                                 if (ix == -1 || ix == name.length()-1) {
                                     if("/".equals(jar) || (name.startsWith(jar) && name.length()>jar.length())) {
                                         dirp.add(je);
                                     }
                                 }
                             }
                             for(JarEntry je : dirp) {
                                 byte[] bs = getBytesInUTF8(je.getName());
                                 int len = bs.length;
 
                                 if(je.isDirectory()) {
                                     len--;
                                 }
 
                                 if(recursive) {
                                     if(fnmatch(STAR,0,1,bs,0,len,flags) != 0) {
                                         continue;
                                     }
                                     buf.length(0);
                                     buf.append(base, 0, base.length - jar.length());
                                     buf.append( BASE(base) ? SLASH : EMPTY );
                                     buf.append(bs, 0, len);
 
                                     if(je.isDirectory()) {
                                         int t = buf.getRealSize();
                                         buf.append(SLASH);
                                         buf.append(DOUBLE_STAR);
                                         buf.append(bytes, m, end - m);
                                         status = glob_helper(cwd, buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize(), t, flags, func, arg);
                                         if(status != 0) {
                                             break;
                                         }
                                     }
                                     continue;
                                 }
 
                                 if(fnmatch(magic,0,magic.length,bs,0,len,flags) == 0) {
                                     buf.length(0);
                                     buf.append(base, 0, base.length - jar.length());
                                     buf.append( BASE(base) ? SLASH : EMPTY );
                                     buf.append(bs, 0, len);
                                     if(m == -1) {
                                         status = func.call(buf.getUnsafeBytes(),0, buf.getRealSize(),arg);
                                         if(status != 0) {
                                             break;
                                         }
                                         continue;
                                     }
                                     link.add(buf);
                                     buf = new ByteList(20);
                                 }
                             }
                         } catch(Exception e) {}
                     }
                 } while(false);
 
                 if (link.size() > 0) {
                     for (ByteList b : link) {
                         if (status == 0) {
                             if(b.getUnsafeBytes()[0] == '/'  || (DOSISH && 2<b.getRealSize() && b.getUnsafeBytes()[1] == ':' && isdirsep(b.getUnsafeBytes()[2]))) {
                                 st = new JavaSecuredFile(newStringFromUTF8(b.getUnsafeBytes(), 0, b.getRealSize()));
                             } else {
                                 st = new JavaSecuredFile(cwd, newStringFromUTF8(b.getUnsafeBytes(), 0, b.getRealSize()));
                             }
 
                             if(st.isDirectory()) {
                                 int len = b.getRealSize();
                                 buf.length(0);
                                 buf.append(b);
                                 buf.append(bytes, m, end - m);
                                 status = glob_helper(cwd, buf.getUnsafeBytes(),0, buf.getRealSize(),len,flags,func,arg);
                             }
                         }
                     }
                     break mainLoop;
                 }
             }
             p = m;
         }
         return status;
     }
 
     private static byte[] getBytesInUTF8(String s) {
-        try {
-            return s.getBytes("UTF-8");
-        } catch (java.io.UnsupportedEncodingException ex) {
-            return s.getBytes(); // NOT REACHED HERE
-        }
+        return RubyEncoding.encodeUTF8(s);
     }
 
     private static String newStringFromUTF8(byte[] buf, int offset, int len) {
-        try {
-            return new String(buf, offset, len, "UTF-8");
-        } catch (java.io.UnsupportedEncodingException ex) {
-            return new String(buf, offset, len); // NOT REACHED HERE
-        }
+        return RubyEncoding.decodeUTF8(buf, offset, len);
     }
 
     private static String newStringFromUTF8(byte[] buf) {
-        try {
-            return new String(buf, "UTF-8");
-        } catch (java.io.UnsupportedEncodingException ex) {
-            return new String(buf); // NOT REACHED HERE
-        }
+        return RubyEncoding.decodeUTF8(buf);
     }
 }
diff --git a/src/org/jruby/util/KCode.java b/src/org/jruby/util/KCode.java
index a26eef1094..c069dadb24 100644
--- a/src/org/jruby/util/KCode.java
+++ b/src/org/jruby/util/KCode.java
@@ -1,104 +1,111 @@
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
 
 package org.jruby.util;
 
+import java.nio.charset.Charset;
 import org.jcodings.Encoding;
 import org.jruby.Ruby;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public final class KCode {
-    public static final KCode NIL = new KCode(null, "ASCII", 0);
-    public static final KCode NONE = new KCode("NONE", "ASCII", 0);
-    public static final KCode UTF8 = new KCode("UTF8", "NonStrictUTF8", 64);
-    public static final KCode SJIS = new KCode("SJIS", "NonStrictSJIS", 48);
-    public static final KCode EUC = new KCode("EUC", "NonStrictEUCJP", 32);
+    public static final KCode NIL = new KCode(null, "ASCII", 0, Charset.forName("ISO-8859-1"));
+    public static final KCode NONE = new KCode("NONE", "ASCII", 0, Charset.forName("ISO-8859-1"));
+    public static final KCode UTF8 = new KCode("UTF8", "NonStrictUTF8", 64, Charset.forName("UTF8"));
+    public static final KCode SJIS = new KCode("SJIS", "NonStrictSJIS", 48, Charset.forName("SJIS"));
+    public static final KCode EUC = new KCode("EUC", "NonStrictEUCJP", 32, Charset.forName("EUC-JP"));
 
     private final String kcode;
     private final String encodingName;
     private final int code;
+    private final Charset charset;
 
     private volatile Encoding encoding;
 
-    private KCode(String kcode, String encodingName, int code) {
+    private KCode(String kcode, String encodingName, int code, Charset charset) {
         this.kcode = kcode;
         this.encodingName = encodingName;
         this.code = code;
+        this.charset = charset;
     }
 
     public static KCode create(Ruby runtime, String lang) {
         if (lang == null) return NIL;
         if (lang.length() == 0) return NONE;
 
         switch (lang.charAt(0)) {
         case 'E':
         case 'e':
             return EUC;
         case 'S':
         case 's':
             return SJIS;
         case 'U':
         case 'u':
             return UTF8;
         case 'N':
         case 'n':
         case 'A':
         case 'a':
             return NONE;
         }
         return NIL;
     }
 
     public IRubyObject kcode(Ruby runtime) {
         return kcode == null ? runtime.getNil() : runtime.newString(kcode); 
     }
 
     public String getKCode() {
         return kcode;
     }
 
+    public Charset getCharset() {
+        return charset;
+    }
+
     public int bits() {
         return code;
     }
 
     public String name() {
         return kcode != null ? kcode.toLowerCase() : null;
     }
 
     public Encoding getEncoding() {
         if (encoding == null) {
             encoding = Encoding.load(encodingName);
         }
         return encoding;
     }
 
     @Override
     public String toString() {
         return name();
     }
 }
