diff --git a/core/src/main/java/org/jruby/Ruby.java b/core/src/main/java/org/jruby/Ruby.java
index bd503521f5..b8a0fe4f0e 100644
--- a/core/src/main/java/org/jruby/Ruby.java
+++ b/core/src/main/java/org/jruby/Ruby.java
@@ -1,1548 +1,1550 @@
 /*
  **** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
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
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jcodings.specific.UTF8Encoding;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.FCallNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.GlobalVarNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.VCallNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.compiler.Constantizable;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.ext.thread.ThreadLibrary;
+import org.jruby.ir.IRScope;
 import org.jruby.ir.IRScriptBody;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.javasupport.JavaSupportImpl;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.util.ClassDefiningClassLoader;
 import org.objectweb.asm.util.TraceClassVisitor;
 
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
 import org.jruby.ast.executable.RuntimeCache;
 import org.jruby.ast.executable.Script;
 import org.jruby.ast.executable.ScriptAndCode;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.common.RubyWarnings;
 import org.jruby.compiler.JITCompiler;
 import org.jruby.embed.Extension;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.Unrescuable;
 import org.jruby.ext.JRubyPOSIXHandler;
 import org.jruby.ext.LateLoadingLibrary;
 import org.jruby.ext.coverage.CoverageData;
 import org.jruby.ext.ffi.FFI;
 import org.jruby.ext.fiber.ThreadFiber;
 import org.jruby.ext.fiber.ThreadFiberLibrary;
 import org.jruby.ext.tracepoint.TracePoint;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.ir.Compiler;
 import org.jruby.ir.IRManager;
 import org.jruby.ir.interpreter.Interpreter;
 import org.jruby.ir.persistence.IRReader;
 import org.jruby.ir.persistence.IRReaderStream;
 import org.jruby.ir.persistence.util.IRFileExpert;
 import org.jruby.javasupport.proxy.JavaProxyClassFactory;
 import org.jruby.management.BeanManager;
 import org.jruby.management.BeanManagerFactory;
 import org.jruby.management.Config;
 import org.jruby.management.ParserStats;
 import org.jruby.parser.Parser;
 import org.jruby.parser.ParserConfiguration;
 import org.jruby.parser.StaticScopeFactory;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.EventHook;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingService;
 import org.jruby.runtime.invokedynamic.MethodNames;
 import org.jruby.runtime.load.BasicLibraryService;
 import org.jruby.runtime.load.CompiledScriptLoader;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.runtime.opto.Invalidator;
 import org.jruby.runtime.opto.OptoFactory;
 import org.jruby.runtime.profile.ProfileCollection;
 import org.jruby.runtime.profile.ProfilingService;
 import org.jruby.runtime.profile.ProfilingServiceLookup;
 import org.jruby.runtime.profile.builtin.ProfiledMethods;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 import org.jruby.threading.DaemonThreadFactory;
 import org.jruby.util.ByteList;
 import org.jruby.util.DefinedMessage;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.SelfFirstJRubyClassLoader;
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.ClassDefiningJRubyClassLoader;
 import org.jruby.util.KCode;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.cli.Options;
 import org.jruby.util.collections.WeakHashSet;
 import org.jruby.util.func.Function1;
 import org.jruby.util.io.FilenoUtil;
 import org.jruby.util.io.SelectorPool;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.objectweb.asm.ClassReader;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.lang.ref.WeakReference;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.net.BindException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.nio.channels.ClosedChannelException;
 import java.security.AccessControlException;
 import java.security.SecureRandom;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.EnumMap;
 import java.util.EnumSet;
 import java.util.HashMap;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.Set;
 import java.util.Stack;
 import java.util.WeakHashMap;
 import java.util.concurrent.Callable;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.SynchronousQueue;
 import java.util.concurrent.ThreadPoolExecutor;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.concurrent.atomic.AtomicLong;
 import java.util.regex.Pattern;
 
 import static org.jruby.internal.runtime.GlobalVariable.Scope.GLOBAL;
 
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
 public final class Ruby implements Constantizable {
 
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
 
         if( config.isProfiling() ) {
             this.profiledMethods        = new ProfiledMethods(this);
             this.profilingServiceLookup = new ProfilingServiceLookup(this);
         } else {
             this.profiledMethods        = null;
             this.profilingServiceLookup = null;
         }
 
         constant = OptoFactory.newConstantWrapper(Ruby.class, this);
 
         getJRubyClassLoader(); // force JRubyClassLoader to init if possible
 
         this.staticScopeFactory = new StaticScopeFactory(this);
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
 
         if (RubyInstanceConfig.CONSISTENT_HASHING_ENABLED) {
             this.hashSeedK0 = -561135208506705104l;
             this.hashSeedK1 = 7114160726623585955l;
         } else {
             this.hashSeedK0 = this.random.nextLong();
             this.hashSeedK1 = this.random.nextLong();
         }
 
         this.configBean = new Config(this);
         this.runtimeBean = new org.jruby.management.Runtime(this);
 
         registerMBeans();
 
         this.runtimeCache = new RuntimeCache();
         runtimeCache.initMethodCache(ClassIndex.MAX_CLASSES.ordinal() * MethodNames.values().length - 1);
 
         checkpointInvalidator = OptoFactory.newConstantInvalidator();
 
         if (config.isObjectSpaceEnabled()) {
             objectSpacer = ENABLED_OBJECTSPACE;
         } else {
             objectSpacer = DISABLED_OBJECTSPACE;
         }
 
         reinitialize(false);
     }
 
     public void registerMBeans() {
         this.beanManager.register(jitCompiler);
         this.beanManager.register(configBean);
         this.beanManager.register(parserStats);
         this.beanManager.register(runtimeBean);
     }
 
     void reinitialize(boolean reinitCore) {
         this.doNotReverseLookupEnabled = true;
         this.staticScopeFactory = new StaticScopeFactory(this);
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
 
     public static boolean isSubstrateVM() {
         return false;
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
             globalRuntime = this;
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
         Node rootNode = parseEval(script, "<script>", scope, 0);
 
         context.preEvalScriptlet(scope);
 
         try {
             return Interpreter.getInstance().execute(this, rootNode, context.getFrameSelf());
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
 
         RootNode root = (RootNode) parseInline(new ByteArrayInputStream(bytes), filename, null);
         ThreadContext context = getCurrentContext();
 
         String oldFile = context.getFile();
         int oldLine = context.getLine();
         try {
             context.setFileAndLine(root.getFile(), root.getLine());
             return runInterpreter(root);
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
         getGlobalVariables().define("$PROGRAM_NAME", d, GLOBAL);
         getGlobalVariables().define("$0", d, GLOBAL);
 
         for (Map.Entry<String, String> entry : config.getOptionGlobals().entrySet()) {
             final IRubyObject varvalue;
             if (entry.getValue() != null) {
                 varvalue = newString(entry.getValue());
             } else {
                 varvalue = getTrue();
             }
             getGlobalVariables().set("$" + entry.getKey(), varvalue);
         }
 
         if (filename.endsWith(".class")) {
             // we are presumably running a precompiled class; load directly
-            Script script = CompiledScriptLoader.loadScriptFromFile(this, inputStream, filename);
+            IRScope script = CompiledScriptLoader.loadScriptFromFile(this, inputStream, filename);
             if (script == null) {
                 throw new MainExitException(1, "error: .class file specified is not a compiled JRuby script");
             }
-            script.setFilename(filename);
-            runScript(script);
+            // FIXME: We need to be able to set the actual name for __FILE__ and friends to reflect it properly
+//            script.setFilename(filename);
+            runInterpreter(script);
             return;
         }
 
         ParseResult parseResult = parseFromMain(filename, inputStream);
 
         // if no DATA, we're done with the stream, shut it down
         if (fetchGlobalConstant("DATA") == null) {
             try {inputStream.close();} catch (IOException ioe) {}
         }
 
         if (parseResult instanceof RootNode) {
             RootNode scriptNode = (RootNode) parseResult;
 
             ThreadContext context = getCurrentContext();
 
             String oldFile = context.getFile();
             int oldLine = context.getLine();
             try {
                 context.setFileAndLine(scriptNode.getFile(), scriptNode.getLine());
 
                 if (config.isAssumePrinting() || config.isAssumeLoop()) {
                     runWithGetsLoop(scriptNode, config.isAssumePrinting(), config.isProcessLineEnds(),
                             config.isSplit());
                 } else {
                     runNormally(scriptNode);
                 }
             } finally {
                 context.setFileAndLine(oldFile, oldLine);
             }
         } else {
             // TODO: Only interpreter supported so far
             runInterpreter(parseResult);
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
 
     public ParseResult parseFromMain(String fileName, InputStream in) {
         if (config.isInlineScript()) return parseInline(in, fileName, getCurrentContext().getCurrentScope());
 
         return parseFileFromMain(fileName, in, getCurrentContext().getCurrentScope());
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
         return runWithGetsLoop((RootNode) scriptNode, printing, processLineEnds, split);
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
     public IRubyObject runWithGetsLoop(RootNode scriptNode, boolean printing, boolean processLineEnds, boolean split) {
         ThreadContext context = getCurrentContext();
 
         // We do not want special scope types in IR so we ammend the AST tree to contain the elements representing
         // a while gets; ...your code...; end
         scriptNode = addGetsLoop(scriptNode, printing, processLineEnds, split);
 
         Script script = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         if (compile) {
             try {
                 script = tryCompile(scriptNode);
                 if (Options.JIT_LOGGING.load()) {
                     LOG.info("Successfully compiled: " + scriptNode.getFile());
                 }
             } catch (Throwable e) {
                 if (Options.JIT_LOGGING.load()) {
                     LOG.error("Failed to compile: " + scriptNode.getFile());
                     if (Options.JIT_LOGGING_VERBOSE.load()) {
                         LOG.error(e);
                     }
                 }
             }
             if (compile && script == null) {
                 // IR JIT does not handle all scripts yet, so let those that fail run in interpreter instead
                 // FIXME: restore error once JIT should handle everything
             }
         }
 
         // we do pre and post load outside the "body" versions to pre-prepare
         // and pre-push the dynamic scope we need for lastline
         Helpers.preLoad(context, ((RootNode) scriptNode).getStaticScope().getVariables());
 
         try {
             if (script != null) {
                 runScriptBody(script);
             } else {
                 runInterpreterBody(scriptNode);
             }
 
         } finally {
             Helpers.postLoad(context);
         }
 
         return getNil();
     }
 
     // Modifies incoming source for -n, -p, and -F
     private RootNode addGetsLoop(RootNode oldRoot, boolean printing, boolean processLineEndings, boolean split) {
         ISourcePosition pos = oldRoot.getPosition();
         BlockNode newBody = new BlockNode(pos);
         newBody.add(new GlobalAsgnNode(pos, "$/", new StrNode(pos, new ByteList(getInstanceConfig().getRecordSeparator().getBytes()))));
 
         if (processLineEndings) newBody.add(new GlobalAsgnNode(pos, "$\\", new GlobalVarNode(pos, "$/")));
 
         GlobalVarNode dollarUnderscore = new GlobalVarNode(pos, "$_");
 
         BlockNode whileBody = new BlockNode(pos);
         newBody.add(new WhileNode(pos, new VCallNode(pos, "gets"), whileBody));
 
         if (processLineEndings) whileBody.add(new CallNode(pos, dollarUnderscore, "chop!", null, null));
         if (split) whileBody.add(new GlobalAsgnNode(pos, "$F", new CallNode(pos, dollarUnderscore, "split", null, null)));
         if (printing) whileBody.add(new FCallNode(pos, "puts", new ArrayNode(pos, dollarUnderscore), null));
 
         if (oldRoot.getBodyNode() instanceof BlockNode) {   // common case n stmts
             whileBody.addAll(oldRoot.getBodyNode());
         } else {                                            // single expr script
             whileBody.add(oldRoot.getBodyNode());
         }
 
         return new RootNode(pos, oldRoot.getScope(), newBody, oldRoot.getFile());
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
         ScriptAndCode scriptAndCode = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         if (compile || config.isShowBytecode()) {
             scriptAndCode = precompileCLI((RootNode) scriptNode);
         }
 
         if (scriptAndCode != null) {
             if (config.isShowBytecode()) {
                 TraceClassVisitor tracer = new TraceClassVisitor(new PrintWriter(System.err));
                 ClassReader reader = new ClassReader(scriptAndCode.bytecode());
                 reader.accept(tracer, 0);
                 return getNil();
             }
 
             return runScript(scriptAndCode.script());
         } else {
             // FIXME: temporarily allowing JIT to fail for $0 and fall back on interpreter
 //            failForcedCompile(scriptNode);
 
             return runInterpreter(scriptNode);
         }
     }
 
     private ScriptAndCode precompileCLI(RootNode scriptNode) {
         ScriptAndCode scriptAndCode = null;
 
         // IR JIT does not handle all scripts yet, so let those that fail run in interpreter instead
         // FIXME: restore error once JIT should handle everything
         try {
             scriptAndCode = tryCompile(scriptNode, new ClassDefiningJRubyClassLoader(getJRubyClassLoader()));
             if (scriptAndCode != null && Options.JIT_LOGGING.load()) {
                 LOG.info("done compiling target script: " + scriptNode.getFile());
             }
         } catch (Exception e) {
             if (Options.JIT_LOGGING.load()) {
                 LOG.error("failed to compile target script '" + scriptNode.getFile() + "'");
                 if (Options.JIT_LOGGING_VERBOSE.load()) {
                     e.printStackTrace();
                 }
             }
         }
         return scriptAndCode;
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
         return tryCompile((RootNode) node, new ClassDefiningJRubyClassLoader(getJRubyClassLoader())).script();
     }
 
     private void failForcedCompile(RootNode scriptNode) throws RaiseException {
         if (config.getCompileMode().shouldPrecompileAll()) {
             throw newRuntimeError("could not compile and compile mode is 'force': " + scriptNode.getFile());
         }
     }
 
     private ScriptAndCode tryCompile(RootNode root, ClassDefiningClassLoader classLoader) {
         try {
             return Compiler.getInstance().execute(this, root, classLoader);
         } catch (NotCompilableException e) {
             if (Options.JIT_LOGGING.load()) {
                 LOG.error("failed to compile target script " + root.getFile() + ": " + e.getLocalizedMessage());
 
                 if (Options.JIT_LOGGING_VERBOSE.load()) LOG.error(e);
             }
             return null;
         }
     }
 
     public IRubyObject runScript(Script script) {
         return runScript(script, false);
     }
 
     public IRubyObject runScript(Script script, boolean wrap) {
         if (getInstanceConfig().getCompileMode() == CompileMode.TRUFFLE) {
             throw new UnsupportedOperationException();
         }
 
         return script.load(getCurrentContext(), getTopSelf(), wrap);
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runScriptBody(Script script) {
         return script.__file__(getCurrentContext(), getTopSelf(), Block.NULL_BLOCK);
     }
 
     public IRubyObject runInterpreter(ThreadContext context, ParseResult parseResult, IRubyObject self) {
         if (getInstanceConfig().getCompileMode() == CompileMode.TRUFFLE) {
             throw new UnsupportedOperationException();
         }
 
         return Interpreter.getInstance().execute(this, parseResult, self);
    }
 
     public IRubyObject runInterpreter(ThreadContext context, Node rootNode, IRubyObject self) {
         assert rootNode != null : "scriptNode is not null";
 
         if (getInstanceConfig().getCompileMode() == CompileMode.TRUFFLE) {
             assert rootNode instanceof RootNode;
             assert self == getTopSelf();
             getTruffleContext().execute((RootNode) rootNode);
             return getNil();
         } else {
             return Interpreter.getInstance().execute(this, rootNode, self);
         }
     }
 
     public IRubyObject runInterpreter(Node scriptNode) {
         return runInterpreter(getCurrentContext(), scriptNode, getTopSelf());
     }
 
     public IRubyObject runInterpreter(ParseResult parseResult) {
         return runInterpreter(getCurrentContext(), parseResult, getTopSelf());
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runInterpreterBody(Node scriptNode) {
         assert scriptNode != null : "scriptNode is not null";
         assert scriptNode instanceof RootNode : "scriptNode is not a RootNode";
 
         return runInterpreter(scriptNode);
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
 
     public TruffleContextInterface getTruffleContext() {
         synchronized (truffleContextMonitor) {
             if (truffleContext == null) {
                 truffleContext = loadTruffleContext();
             }
             return truffleContext;
         }
     }
 
     private TruffleContextInterface loadTruffleContext() {
         final Class<?> clazz;
 
         try {
             clazz = getJRubyClassLoader().loadClass("org.jruby.truffle.runtime.RubyContext");
         } catch (Exception e) {
             throw new RuntimeException("Truffle backend not available", e);
         }
 
         final TruffleContextInterface truffleContext;
 
         try {
             Constructor<?> con = clazz.getConstructor(Ruby.class);
             truffleContext = (TruffleContextInterface) con.newInstance(this);
         } catch (Exception e) {
             throw new RuntimeException("Error while calling the constructor of Truffle's RubyContext", e);
         }
 
         truffleContext.initialize();
 
         return truffleContext;
     }
 
     public void shutdownTruffleContextIfRunning() {
         synchronized (truffleContextMonitor) {
             if (truffleContext != null) {
                 truffleContext.shutdown();
             }
         }
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
     @Extension
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
     @Extension
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
     @Extension
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
     @Extension
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
      * Define a constant on the global namespace (i.e. Object) with the given
      * name and value.
      *
      * @param name the name
      * @param value the value
      */
     public void defineGlobalConstant(String name, IRubyObject value) {
         objectClass.defineConstant(name, value);
     }
 
     /**
      * Fetch a constant from the global namespace (i.e. Object) with the given
      * name.
      *
      * @param name the name
      * @return the value
      */
     public IRubyObject fetchGlobalConstant(String name) {
         return objectClass.fetchConstant(name, false);
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
         javaSupport = loadJavaSupport();
 
         executor = new ThreadPoolExecutor(
                 RubyInstanceConfig.POOL_MIN,
                 RubyInstanceConfig.POOL_MAX,
                 RubyInstanceConfig.POOL_TTL,
                 TimeUnit.SECONDS,
                 new SynchronousQueue<Runnable>(),
                 new DaemonThreadFactory("Ruby-" + getRuntimeNumber() + "-Worker"));
 
         fiberExecutor = new ThreadPoolExecutor(
                 0,
                 Integer.MAX_VALUE,
                 RubyInstanceConfig.FIBER_POOL_TTL,
                 TimeUnit.SECONDS,
                 new SynchronousQueue<Runnable>(),
                 new DaemonThreadFactory("Ruby-" + getRuntimeNumber() + "-Fiber"));
 
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
 
         // set up thread statuses
         initThreadStatuses();
 
         // Create an IR manager and a top-level IR scope and bind it to the top-level static-scope object
         irManager = new IRManager(getInstanceConfig());
         // FIXME: This registers itself into static scope as a side-effect.  Let's make this
         // relationship handled either more directly or through a descriptice method
         // FIXME: We need a failing test case for this since removing it did not regress tests
         new IRScriptBody(irManager, "", tc.getCurrentScope().getStaticScope());
 
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
         // if we can't use reflection, 'jruby' and 'java' won't work; no load.
         boolean reflectionWorks = doesReflectionWork();
 
         if (!RubyInstanceConfig.DEBUG_PARSER && reflectionWorks
                 && getInstanceConfig().getCompileMode() != CompileMode.TRUFFLE) {
             loadService.require("jruby");
         }
 
         // attempt to enable unlimited-strength crypto on OpenJDK
         try {
             Class jceSecurity = Class.forName("javax.crypto.JceSecurity");
             Field isRestricted = jceSecurity.getDeclaredField("isRestricted");
             isRestricted.setAccessible(true);
             isRestricted.set(null, false);
             isRestricted.setAccessible(false);
         } catch (Exception e) {
             if (isDebug()) {
                 System.err.println("unable to enable unlimited-strength crypto");
                 e.printStackTrace();
             }
         }
 
         // out of base boot mode
         bootingCore = false;
 
         // init Ruby-based kernel
         if (getInstanceConfig().getCompileMode() != CompileMode.TRUFFLE) {
             initRubyKernel();
         }
 
         // everything booted, so SizedQueue should be available; set up root fiber
         if (getInstanceConfig().getCompileMode() != CompileMode.TRUFFLE) {
             ThreadFiber.initRootFiber(tc);
         }
 
         if(config.isProfiling()) {
             // additional twiddling for profiled mode
             getLoadService().require("jruby/profiler/shutdown_hook");
 
             // recache core methods, since they'll have profiling wrappers now
             kernelModule.invalidateCacheDescendants(); // to avoid already-cached methods
             RubyKernel.recacheBuiltinMethods(this);
             RubyBasicObject.recacheBuiltinMethods(this);
         }
 
         if (config.getLoadGemfile()) {
             loadBundler();
         }
 
         setNetworkStack();
 
         // Done booting JRuby runtime
         bootingRuntime = false;
 
         // Require in all libraries specified on command line
         if (getInstanceConfig().getCompileMode() != CompileMode.TRUFFLE) {
             for (String scriptName : config.getRequiredLibraries()) {
                 topSelf.callMethod(getCurrentContext(), "require", RubyString.newString(this, scriptName));
             }
         }
     }
 
     public JavaSupport loadJavaSupport() {
         return new JavaSupportImpl(this);
     }
 
     private void loadBundler() {
         loadService.loadFromClassLoader(getClassLoader(), "jruby/bundler/startup.rb", false);
     }
 
     private boolean doesReflectionWork() {
         try {
             ClassLoader.class.getDeclaredMethod("getResourceAsStream", String.class);
             return true;
         } catch (Exception e) {
             return false;
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
 
     private void initThreadStatuses() {
         for (RubyThread.Status status : RubyThread.Status.values()) {
             RubyString str = RubyString.newString(this, status.bytes);
             str.setFrozen(true);
             threadStatuses.put(status, str);
         }
     }
 
     private void initRoot() {
         // Bootstrap the top of the hierarchy
         basicObjectClass = RubyClass.createBootstrapClass(this, "BasicObject", null, RubyBasicObject.BASICOBJECT_ALLOCATOR);
         objectClass = RubyClass.createBootstrapClass(this, "Object", basicObjectClass, RubyObject.OBJECT_ALLOCATOR);
         moduleClass = RubyClass.createBootstrapClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR);
         classClass = RubyClass.createBootstrapClass(this, "Class", moduleClass, RubyClass.CLASS_ALLOCATOR);
 
         basicObjectClass.setMetaClass(classClass);
         objectClass.setMetaClass(basicObjectClass);
         moduleClass.setMetaClass(classClass);
         classClass.setMetaClass(classClass);
 
         RubyClass metaClass;
         metaClass = basicObjectClass.makeMetaClass(classClass);
         metaClass = objectClass.makeMetaClass(metaClass);
         metaClass = moduleClass.makeMetaClass(metaClass);
         metaClass = classClass.makeMetaClass(metaClass);
 
         RubyBasicObject.createBasicObjectClass(this, basicObjectClass);
         RubyObject.createObjectClass(this, objectClass);
         RubyModule.createModuleClass(this, moduleClass);
         RubyClass.createClassClass(this, classClass);
 
         // set constants now that they're initialized
         basicObjectClass.setConstant("BasicObject", basicObjectClass);
         objectClass.setConstant("Object", objectClass);
         objectClass.setConstant("Class", classClass);
         objectClass.setConstant("Module", moduleClass);
 
         // Initialize Kernel and include into Object
         RubyModule kernel = RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         // In 1.9 and later, Kernel.gsub is defined only when '-p' or '-n' is given on the command line
         if (config.getKernelGsubDefined()) {
             kernel.addMethod("gsub", new JavaMethod(kernel, Visibility.PRIVATE, CallConfiguration.FrameFullScopeNone) {
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     switch (args.length) {
                         case 1:
                             return RubyKernel.gsub(context, self, args[0], block);
                         case 2:
                             return RubyKernel.gsub(context, self, args[0], args[1], block);
                         default:
                             throw newArgumentError(String.format("wrong number of arguments %d for 1..2", args.length));
                     }
                 }
             });
         }
 
         // Object is ready, create top self
         topSelf = TopSelfFactory.createTopSelf(this, false);
 
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
 
         if (profile.allowClass("Numeric")) {
             RubyNumeric.createNumericClass(this);
         }
         if (profile.allowClass("Integer")) {
             RubyInteger.createIntegerClass(this);
         }
         if (profile.allowClass("Fixnum")) {
             RubyFixnum.createFixnumClass(this);
         }
 
         RubyEncoding.createEncodingClass(this);
         RubyConverter.createConverterClass(this);
 
         encodingService.defineEncodings();
         encodingService.defineAliases();
 
         // External should always have a value, but Encoding.external_encoding{,=} will lazily setup
         String encoding = config.getExternalEncoding();
         if (encoding != null && !encoding.equals("")) {
             Encoding loadedEncoding = encodingService.loadEncoding(ByteList.create(encoding));
             if (loadedEncoding == null) throw new MainExitException(1, "unknown encoding name - " + encoding);
             setDefaultExternalEncoding(loadedEncoding);
         } else {
             Encoding consoleEncoding = encodingService.getConsoleEncoding();
             Encoding availableEncoding = consoleEncoding == null ? encodingService.getLocaleEncoding() : consoleEncoding;
             setDefaultExternalEncoding(availableEncoding);
         }
 
         encoding = config.getInternalEncoding();
         if (encoding != null && !encoding.equals("")) {
             Encoding loadedEncoding = encodingService.loadEncoding(ByteList.create(encoding));
             if (loadedEncoding == null) throw new MainExitException(1, "unknown encoding name - " + encoding);
             setDefaultInternalEncoding(loadedEncoding);
         }
 
         if (profile.allowClass("Complex")) {
             RubyComplex.createComplexClass(this);
         }
         if (profile.allowClass("Rational")) {
             RubyRational.createRationalClass(this);
         }
 
         if (profile.allowClass("Hash")) {
             RubyHash.createHashClass(this);
         }
         if (profile.allowClass("Array")) {
             RubyArray.createArrayClass(this);
             emptyFrozenArray = newEmptyArray();
             emptyFrozenArray.setFrozen(true);
         }
         if (profile.allowClass("Float")) {
             RubyFloat.createFloatClass(this);
         }
         if (profile.allowClass("Bignum")) {
             RubyBignum.createBignumClass(this);
             // RubyRandom depends on Bignum existence.
             RubyRandom.createRandomClass(this);
         }
         ioClass = RubyIO.createIOClass(this);
 
         if (profile.allowClass("Struct")) {
             RubyStruct.createStructClass(this);
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
@@ -1908,2000 +1910,2021 @@ public final class Ruby implements Constantizable {
     public void setNormalMethodMissing(DynamicMethod method) {
         normalMethodMissing = method;
     }
 
     public DynamicMethod getDefaultMethodMissing() {
         return defaultMethodMissing;
     }
 
     public boolean isDefaultMethodMissing(DynamicMethod method) {
         return defaultMethodMissing == method || defaultModuleMethodMissing == method;
 
     }
 
     public void setDefaultMethodMissing(DynamicMethod method, DynamicMethod moduleMethod) {
         defaultMethodMissing = method;
         defaultModuleMethodMissing = moduleMethod;
     }
 
     public DynamicMethod getRespondToMethod() {
         return respondTo;
     }
 
     public void setRespondToMethod(DynamicMethod rtm) {
         this.respondTo = rtm;
     }
 
     public DynamicMethod getRespondToMissingMethod() {
         return respondToMissing;
     }
 
     public void setRespondToMissingMethod(DynamicMethod rtmm) {
         this.respondToMissing = rtmm;
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
 
     public RubyClass getGenerator() {
         return generatorClass;
     }
     public void setGenerator(RubyClass generatorClass) {
         this.generatorClass = generatorClass;
     }
 
     public RubyClass getFiber() {
         return fiberClass;
     }
     public void setFiber(RubyClass fiberClass) {
         this.fiberClass = fiberClass;
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
     public void setPasswdStruct(RubyClass passwdStruct) {
         this.passwdStruct = passwdStruct;
     }
 
     public IRubyObject getGroupStruct() {
         return groupStruct;
     }
     public void setGroupStruct(RubyClass groupStruct) {
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
 
     public RubyClass getLocation() {
         return locationClass;
     }
 
     public void setLocation(RubyClass location) {
         this.locationClass = location;
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
 
     public RubyClass getKeyError() {
         return keyError;
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
 
     public RubyClass getInterruptedRegexpError() {
         return interruptedRegexpError;
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
         // we try to getService the classloader that loaded JRuby, falling back on System
         ClassLoader loader = Ruby.class.getClassLoader();
         if (loader == null) {
             loader = ClassLoader.getSystemClassLoader();
         }
 
         return loader;
     }
 
     /**
      * TODO the property {@link #jrubyClassLoader} will only be set in constructor. in the first call of
      * {@link #getJRubyClassLoader() getJRubyClassLoader}. So the field {@link #jrubyClassLoader} can be final
      * set in the constructor directly and we avoid the synchronized here.
      *
      * @return
      */
     public synchronized JRubyClassLoader getJRubyClassLoader() {
         // FIXME: Get rid of laziness and handle restricted access elsewhere
         if (!Ruby.isSecurityRestricted() && jrubyClassLoader == null) {
             if (config.isClassloaderDelegate()){
                 jrubyClassLoader = new JRubyClassLoader(config.getLoader());
             }
             else {
                 jrubyClassLoader = new SelfFirstJRubyClassLoader(config.getLoader());
             }
 
             // if jit code cache is used, we need to add the cache directory to the classpath
             // so the previously generated class files can be reused.
             if( config.JIT_CODE_CACHE != null && !config.JIT_CODE_CACHE.trim().isEmpty() ) {
                 File file = new File( config.JIT_CODE_CACHE );
 
                 if( file.exists() == false || file.isDirectory() == false ) {
                     getWarnings().warning("The jit.codeCache '" + config.JIT_CODE_CACHE + "' directory doesn't exit.");
                 } else {
                     try {
                         URL url = file.toURI().toURL();
                         jrubyClassLoader.addURL( url );
                     } catch (MalformedURLException e) {
                         getWarnings().warning("Unable to add the jit.codeCache '" + config.JIT_CODE_CACHE + "' directory to the classpath." + e.getMessage());
                     }
                 }
             }
         }
 
         return jrubyClassLoader;
     }
 
     /** Defines a global variable
      */
     public void defineVariable(final GlobalVariable variable, org.jruby.internal.runtime.GlobalVariable.Scope scope) {
         globalVariables.define(variable.name(), new IAccessor() {
             @Override
             public IRubyObject getValue() {
                 return variable.get();
             }
 
             @Override
             public IRubyObject setValue(IRubyObject newValue) {
                 return variable.set(newValue);
             }
         }, scope);
     }
 
     /** defines a readonly global variable
      *
      */
     public void defineReadonlyVariable(String name, IRubyObject value, org.jruby.internal.runtime.GlobalVariable.Scope scope) {
         globalVariables.defineReadonly(name, new ValueAccessor(value), scope);
     }
 
     // Obsolete parseFile function
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     // Modern variant of parsFile function above
     public ParseResult parseFile(String file, InputStream in, DynamicScope scope) {
        return parseFile(file, in, scope, 0);
     }
 
     // Obsolete parseFile function
     public Node parseFile(InputStream in, String file, DynamicScope scope, int lineNumber) {
         addLoadParseToStats();
         return parseFileAndGetAST(in, file, scope, lineNumber, false);
     }
 
     // Modern variant of parseFile function above
     public ParseResult parseFile(String file, InputStream in, DynamicScope scope, int lineNumber) {
         addLoadParseToStats();
 
         if (!RubyInstanceConfig.IR_READING) return parseFileAndGetAST(in, file, scope, lineNumber, false);
 
         try {
             // Get IR from .ir file
             return IRReader.load(getIRManager(), new IRReaderStream(getIRManager(), IRFileExpert.getIRPersistedFile(file)));
         } catch (IOException e) {
             // FIXME: What is something actually throws IOException
             return parseFileAndGetAST(in, file, scope, lineNumber, false);
         }
     }
 
     // Obsolete parseFileFromMain function
     public Node parseFileFromMain(InputStream in, String file, DynamicScope scope) {
         addLoadParseToStats();
 
         return parseFileFromMainAndGetAST(in, file, scope);
     }
 
     // Modern variant of parseFileFromMain function above
     public ParseResult parseFileFromMain(String file, InputStream in, DynamicScope scope) {
         addLoadParseToStats();
 
         if (!RubyInstanceConfig.IR_READING) return parseFileFromMainAndGetAST(in, file, scope);
 
         try {
             return IRReader.load(getIRManager(), new IRReaderStream(getIRManager(), IRFileExpert.getIRPersistedFile(file)));
         } catch (IOException e) {
             System.out.println(e);
             e.printStackTrace();
             return parseFileFromMainAndGetAST(in, file, scope);
         }
     }
 
      private Node parseFileFromMainAndGetAST(InputStream in, String file, DynamicScope scope) {
          return parseFileAndGetAST(in, file, scope, 0, true);
      }
 
      private Node parseFileAndGetAST(InputStream in, String file, DynamicScope scope, int lineNumber, boolean isFromMain) {
          ParserConfiguration parserConfig =
                  new ParserConfiguration(this, lineNumber, false, true, config);
          setupSourceEncoding(parserConfig, UTF8Encoding.INSTANCE);
          return parser.parse(file, in, scope, parserConfig);
      }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         addEvalParseToStats();
         ParserConfiguration parserConfig =
                 new ParserConfiguration(this, 0, false, true, false, config);
         setupSourceEncoding(parserConfig, getEncodingService().getLocaleEncoding());
         return parser.parse(file, in, scope, parserConfig);
     }
 
     private void setupSourceEncoding(ParserConfiguration parserConfig, Encoding defaultEncoding) {
         if (config.getSourceEncoding() != null) {
             if (config.isVerbose()) {
                 config.getError().println("-K is specified; it is for 1.8 compatibility and may cause odd behavior");
             }
             parserConfig.setDefaultEncoding(getEncodingService().getEncodingFromString(config.getSourceEncoding()));
         } else {
             parserConfig.setDefaultEncoding(defaultEncoding);
         }
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         addEvalParseToStats();
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this, lineNumber, false, false, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber,
             boolean extraPositionInformation) {
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, extraPositionInformation, false, true, config));
     }
 
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         addEvalParseToStats();
         return parser.parse(file, content, scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, config));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber,
             boolean extraPositionInformation) {
         addEvalParseToStats();
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
 
     /**
      * This is an internal encoding if actually specified via default_internal=
      * or passed in via -E.
      *
      * @return null or encoding
      */
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
         /*java.io.OutputStream os = ((RubyIO) getGlobalVariables().getService("$stderr")).getOutStream();
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
         String backtrace = config.getTraceType().printBacktrace(excp, errorStream == System.err && getPosix().isatty(FileDescriptor.err));
         try {
             errorStream.print(backtrace);
         } catch (Exception e) {
             System.err.print(backtrace);
         }
     }
 
     public void loadFile(String scriptName, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this, true) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
 
         try {
             ThreadContext.pushBacktrace(context, "(root)", file, 0);
             context.preNodeEval(self);
             ParseResult parseResult = parseFile(scriptName, in, null);
 
             if (wrap) {
                 // toss an anonymous module into the search path
                 ((RootNode) parseResult).getStaticScope().setModule(RubyModule.newModule(this));
             }
 
             runInterpreter(context, parseResult, self);
         } finally {
             context.postNodeEval();
             ThreadContext.popBacktrace(context);
         }
     }
 
+    public void loadScope(IRScope scope, boolean wrap) {
+        IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this, true) : getTopSelf();
+        ThreadContext context = getCurrentContext();
+        String file = context.getFile();
+
+        try {
+            ThreadContext.pushBacktrace(context, "(root)", file, 0);
+            context.preNodeEval(self);
+
+            if (wrap) {
+                // toss an anonymous module into the search path
+                scope.getStaticScope().setModule(RubyModule.newModule(this));
+            }
+
+            runInterpreter(context, scope, self);
+        } finally {
+            context.postNodeEval();
+            ThreadContext.popBacktrace(context);
+        }
+    }
+
     public void compileAndLoadFile(String filename, InputStream in, boolean wrap) {
         InputStream readStream = in;
 
         Script script = null;
         ScriptAndCode scriptAndCode = null;
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
                     LOG.info("found jitted code for " + filename + " at class: " + className);
                 }
                 script = (Script) contents.newInstance();
                 readStream = new ByteArrayInputStream(buffer);
             } catch (ClassNotFoundException cnfe) {
                 if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                     LOG.info("no jitted code in classloader for file " + filename + " at class: " + className);
                 }
             } catch (InstantiationException ie) {
                 if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                     LOG.info("jitted code could not be instantiated for file " + filename + " at class: " + className);
                 }
             } catch (IllegalAccessException iae) {
                 if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                     LOG.info("jitted code could not be instantiated for file " + filename + " at class: " + className);
                 }
             }
         } catch (IOException ioe) {
             // TODO: log something?
         }
 
         // script was not found in cache above, so proceed to compile
         RootNode scriptNode = (RootNode) parseFile(readStream, filename, null);
         if (script == null) {
             scriptAndCode = tryCompile(scriptNode, new ClassDefiningJRubyClassLoader(jrubyClassLoader));
             if (scriptAndCode != null) script = scriptAndCode.script();
         }
 
         if (script == null) {
             failForcedCompile(scriptNode);
 
             runInterpreter(scriptNode);
         } else {
             runScript(script, wrap);
         }
     }
 
     public void loadScript(Script script) {
         loadScript(script, false);
     }
 
     public void loadScript(Script script, boolean wrap) {
         script.load(getCurrentContext(), getTopSelf(), wrap);
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
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this, true) : getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             context.preExtensionLoad(self);
 
             extension.basicLoad(this);
         } catch (IOException ioe) {
             throw newIOErrorFromException(ioe);
         } finally {
             context.postNodeEval();
         }
     }
 
     public void addBoundMethod(String className, String methodName, String rubyName) {
         Map<String, String> javaToRuby = boundMethods.get(className);
         if (javaToRuby == null) {
             javaToRuby = new HashMap<String, String>();
             boundMethods.put(className, javaToRuby);
         }
         javaToRuby.put(methodName, rubyName);
     }
 
     public Map<String, Map<String, String>> getBoundMethods() {
         return boundMethods;
     }
 
     public void setJavaProxyClassFactory(JavaProxyClassFactory factory) {
         this.javaProxyClassFactory = factory;
     }
 
     public JavaProxyClassFactory getJavaProxyClassFactory() {
         return javaProxyClassFactory;
     }
 
     public class CallTraceFuncHook extends EventHook {
         private RubyProc traceFunc;
         private EnumSet<RubyEvent> interest =
                 EnumSet.allOf(RubyEvent.class);
 
         public void setTraceFunc(RubyProc traceFunc) {
             this.traceFunc = traceFunc;
         }
 
         public void eventHandler(ThreadContext context, String eventName, String file, int line, String name, IRubyObject type) {
             if (!context.isWithinTrace()) {
                 if (file == null) file = "(ruby)";
                 if (type == null) type = getNil();
 
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
 
         @Override
         public boolean isInterestedInEvent(RubyEvent event) {
             return interest.contains(event);
         }
     };
 
     private final CallTraceFuncHook callTraceFuncHook = new CallTraceFuncHook();
 
     public synchronized void addEventHook(EventHook hook) {
         if (!RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // without full tracing, many events will not fire
             getWarnings().warn("tracing (e.g. set_trace_func) will not capture all events without --debug flag");
         }
 
         EventHook[] hooks = eventHooks;
         EventHook[] newHooks = Arrays.copyOf(hooks, hooks.length + 1);
         newHooks[hooks.length] = hook;
         eventHooks = newHooks;
         hasEventHooks = true;
     }
 
     public synchronized void removeEventHook(EventHook hook) {
         EventHook[] hooks = eventHooks;
 
         if (hooks.length == 0) return;
 
         int pivot = -1;
         for (int i = 0; i < hooks.length; i++) {
             if (hooks[i] == hook) {
                 pivot = i;
                 break;
             }
         }
 
         if (pivot == -1) return; // No such hook found.
 
         EventHook[] newHooks = new EventHook[hooks.length - 1];
         // copy before and after pivot into the new array but don't bother
         // to arraycopy if pivot is first/last element of the old list.
         if (pivot != 0) System.arraycopy(hooks, 0, newHooks, 0, pivot);
         if (pivot != hooks.length-1) System.arraycopy(hooks, pivot + 1, newHooks, pivot, hooks.length - (pivot + 1));
 
         eventHooks = newHooks;
         hasEventHooks = newHooks.length > 0;
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
             for (int i = 0; i < eventHooks.length; i++) {
                 EventHook eventHook = eventHooks[i];
 
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
      * Make sure Kernel#at_exit procs getService invoked on runtime shutdown.
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
 
         // clear out threadlocals so they don't leak
         recursive = new ThreadLocal<Map<String, RubyHash>>();
 
         ThreadContext context = getCurrentContext();
 
         // FIXME: 73df3d230b9d92c7237d581c6366df1b92ad9b2b exposed no toplevel scope existing anymore (I think the
         // bogus scope I removed was playing surrogate toplevel scope and wallpapering this bug).  For now, add a
         // bogus scope back for at_exit block run.  This is buggy if at_exit is capturing vars.
         if (!context.hasAnyScopes()) {
             StaticScope topStaticScope = getStaticScopeFactory().newLocalScope(null);
             context.pushScope(new ManyVarsDynamicScope(topStaticScope, null));
         }
 
         while (!atExitBlocks.empty()) {
             RubyProc proc = atExitBlocks.pop();
             // IRubyObject oldExc = context.runtime.getGlobalVariables().get("$!"); // Save $!
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
                 // Reset $! now that rj has been handled
                 // context.runtime.getGlobalVariables().set("$!", oldExc);
             }
         }
 
         // Fetches (and unsets) the SIGEXIT handler, if one exists.
         IRubyObject trapResult = RubySignal.__jtrap_osdefault_kernel(this.getNil(), this.newString("EXIT"));
         if (trapResult instanceof RubyArray) {
             IRubyObject[] trapResultEntries = ((RubyArray) trapResult).toJavaArray();
             IRubyObject exitHandlerProc = trapResultEntries[0];
             if (exitHandlerProc instanceof RubyProc) {
                 ((RubyProc) exitHandlerProc).call(this.getCurrentContext(), this.getSingleNilArray());
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
         getBeanManager().unregisterMethodCache();
         getBeanManager().unregisterRuntime();
 
         getSelectorPool().cleanup();
 
         tearDownClassLoader();
 
         if (config.isProfilingEntireRun()) {
             // not using logging because it's formatted
             ProfileCollection profileCollection = threadService.getMainThread().getContext().getProfileCollection();
             printProfileData(profileCollection);
         }
 
         if (systemExit && status != 0) {
             throw newSystemExit(status);
         }
 
         // This is a rather gross way to ensure nobody else performs the same clearing of globalRuntime followed by
         // initializing a new runtime, which would cause our clear below to clear the wrong runtime. Synchronizing
         // against the class is a problem, but the overhead of teardown and creating new containers should outstrip
         // a global synchronize around a few field accesses. -CON
         if (this == globalRuntime) {
             synchronized (Ruby.class) {
                 if (this == globalRuntime) {
                     globalRuntime = null;
                 }
             }
         }
     }
 
     private void tearDownClassLoader() {
         if (getJRubyClassLoader() != null) {
             getJRubyClassLoader().tearDown(isDebug());
         }
     }
 
     /**
      * TDOD remove the synchronized. Synchronization should be a implementation detail of the ProfilingService.
      * @param profileData
      */
     public synchronized void printProfileData( ProfileCollection profileData ) {
         getProfilingService().newProfileReporter(getCurrentContext()).report(profileData);
     }
 
     /**
      * Simple getter for #profilingServiceLookup to avoid direct property access
      * @return #profilingServiceLookup
      */
     private ProfilingServiceLookup getProfilingServiceLookup() {
         return profilingServiceLookup;
     }
 
     /**
      *
      * @return the, for this ruby instance, configured implementation of ProfilingService, or null
      */
     public ProfilingService getProfilingService() {
         ProfilingServiceLookup lockup = getProfilingServiceLookup();
         return lockup == null ? null : lockup.getService();
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
 
     public RubyArray getEmptyFrozenArray() {
         return emptyFrozenArray;
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
 
     public RubyRational newRationalReduced(long num, long den) {
         return (RubyRational)RubyRational.newRationalConvert(getCurrentContext(), newFixnum(num), newFixnum(den));
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
 
     public RubySymbol newSymbol(String name, Encoding encoding) {
         ByteList byteList = RubyString.encodeBytelist(name, encoding);
         return symbolTable.getSymbol(byteList);
     }
 
     public RubySymbol newSymbol(ByteList name) {
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
 
     public RaiseException newArgumentError(String name, int got, int expected) {
         return newRaiseException(getArgumentError(), "wrong number of arguments calling `" + name + "` (" + got + " for " + expected + ")");
     }
 
     public RaiseException newErrnoEBADFError() {
         return newRaiseException(getErrno().getClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoEISCONNError() {
         return newRaiseException(getErrno().getClass("EISCONN"), "Socket is already connected");
     }
 
     public RaiseException newErrnoEINPROGRESSError() {
         return newRaiseException(getErrno().getClass("EINPROGRESS"), "Operation now in progress");
     }
 
     public RaiseException newErrnoEINPROGRESSWritableError() {
         return newLightweightErrnoException(getIO().getClass("EINPROGRESSWaitWritable"), "");
     }
 
     public RaiseException newErrnoENOPROTOOPTError() {
         return newRaiseException(getErrno().getClass("ENOPROTOOPT"), "Protocol not available");
     }
 
     public RaiseException newErrnoEPIPEError() {
         return newRaiseException(getErrno().getClass("EPIPE"), "Broken pipe");
     }
 
     public RaiseException newErrnoECONNABORTEDError() {
         return newRaiseException(getErrno().getClass("ECONNABORTED"),
                 "An established connection was aborted by the software in your host machine");
     }
 
     public RaiseException newErrnoECONNREFUSEDError() {
         return newRaiseException(getErrno().getClass("ECONNREFUSED"), "Connection refused");
     }
 
     public RaiseException newErrnoECONNRESETError() {
         return newRaiseException(getErrno().getClass("ECONNRESET"), "Connection reset by peer");
     }
 
     public RaiseException newErrnoEADDRINUSEError() {
         return newRaiseException(getErrno().getClass("EADDRINUSE"), "Address in use");
     }
 
     public RaiseException newErrnoEADDRINUSEError(String message) {
         return newRaiseException(getErrno().getClass("EADDRINUSE"), message);
     }
 
     public RaiseException newErrnoEHOSTUNREACHError(String message) {
         return newRaiseException(getErrno().getClass("EHOSTUNREACH"), message);
     }
 
     public RaiseException newErrnoEINVALError() {
         return newRaiseException(getErrno().getClass("EINVAL"), "Invalid file");
     }
 
     public RaiseException newErrnoELOOPError() {
         return newRaiseException(getErrno().getClass("ELOOP"), "Too many levels of symbolic links");
     }
 
     public RaiseException newErrnoEMFILEError() {
         return newRaiseException(getErrno().getClass("EMFILE"), "Too many open files");
     }
 
     public RaiseException newErrnoENFILEError() {
         return newRaiseException(getErrno().getClass("ENFILE"), "Too many open files in system");
     }
 
     public RaiseException newErrnoENOENTError() {
         return newRaiseException(getErrno().getClass("ENOENT"), "File not found");
     }
 
     public RaiseException newErrnoEACCESError(String message) {
         return newRaiseException(getErrno().getClass("EACCES"), message);
     }
 
     public RaiseException newErrnoEAGAINError(String message) {
         return newLightweightErrnoException(getErrno().getClass("EAGAIN"), message);
     }
 
     public RaiseException newErrnoEAGAINReadableError(String message) {
         return newLightweightErrnoException(getModule("IO").getClass("EAGAINWaitReadable"), message);
     }
 
     public RaiseException newErrnoEAGAINWritableError(String message) {
         return newLightweightErrnoException(getModule("IO").getClass("EAGAINWaitWritable"), message);
     }
 
     public RaiseException newErrnoEISDirError(String message) {
         return newRaiseException(getErrno().getClass("EISDIR"), message);
     }
 
     public RaiseException newErrnoEPERMError(String name) {
         return newRaiseException(getErrno().getClass("EPERM"), "Operation not permitted - " + name);
     }
 
     public RaiseException newErrnoEISDirError() {
         return newErrnoEISDirError("Is a directory");
     }
 
     public RaiseException newErrnoESPIPEError() {
         return newRaiseException(getErrno().getClass("ESPIPE"), "Illegal seek");
     }
 
     public RaiseException newErrnoEBADFError(String message) {
         return newRaiseException(getErrno().getClass("EBADF"), message);
     }
 
     public RaiseException newErrnoEINPROGRESSError(String message) {
         return newRaiseException(getErrno().getClass("EINPROGRESS"), message);
     }
 
     public RaiseException newErrnoEINPROGRESSWritableError(String message) {
         return newLightweightErrnoException(getIO().getClass("EINPROGRESSWaitWritable"), message);
     }
 
     public RaiseException newErrnoEISCONNError(String message) {
         return newRaiseException(getErrno().getClass("EISCONN"), message);
     }
 
     public RaiseException newErrnoEINVALError(String message) {
         return newRaiseException(getErrno().getClass("EINVAL"), message);
     }
 
     public RaiseException newErrnoENOTDIRError(String message) {
         return newRaiseException(getErrno().getClass("ENOTDIR"), message);
     }
 
     public RaiseException newErrnoENOTEMPTYError(String message) {
         return newRaiseException(getErrno().getClass("ENOTEMPTY"), message);
     }
 
     public RaiseException newErrnoENOTSOCKError(String message) {
         return newRaiseException(getErrno().getClass("ENOTSOCK"), message);
     }
 
     public RaiseException newErrnoENOTCONNError(String message) {
         return newRaiseException(getErrno().getClass("ENOTCONN"), message);
     }
 
     public RaiseException newErrnoENOTCONNError() {
         return newRaiseException(getErrno().getClass("ENOTCONN"), "Socket is not connected");
     }
 
     public RaiseException newErrnoENOENTError(String message) {
         return newRaiseException(getErrno().getClass("ENOENT"), message);
     }
 
     public RaiseException newErrnoEOPNOTSUPPError(String message) {
         return newRaiseException(getErrno().getClass("EOPNOTSUPP"), message);
     }
 
     public RaiseException newErrnoESPIPEError(String message) {
         return newRaiseException(getErrno().getClass("ESPIPE"), message);
     }
 
     public RaiseException newErrnoEEXISTError(String message) {
         return newRaiseException(getErrno().getClass("EEXIST"), message);
     }
 
     public RaiseException newErrnoEDOMError(String message) {
         return newRaiseException(getErrno().getClass("EDOM"), "Domain error - " + message);
     }
 
     public RaiseException newErrnoECHILDError() {
         return newRaiseException(getErrno().getClass("ECHILD"), "No child processes");
     }
 
     public RaiseException newErrnoEADDRNOTAVAILError(String message) {
         return newRaiseException(getErrno().getClass("EADDRNOTAVAIL"), message);
     }
 
     public RaiseException newErrnoESRCHError() {
         return newRaiseException(getErrno().getClass("ESRCH"), null);
     }
 
     public RaiseException newErrnoEWOULDBLOCKError() {
         return newRaiseException(getErrno().getClass("EWOULDBLOCK"), null);
     }
 
     public RaiseException newErrnoEDESTADDRREQError(String func) {
         return newRaiseException(getErrno().getClass("EDESTADDRREQ"), func);
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
 
     public RaiseException newKeyError(String message) {
         return newRaiseException(getKeyError(), message);
     }
 
     public RaiseException newErrnoEINTRError() {
         return newRaiseException(getErrno().getClass("EINTR"), "Interrupted");
     }
 
     public RaiseException newErrnoFromLastPOSIXErrno() {
         RubyClass errnoClass = getErrno(getPosix().errno());
         if (errnoClass == null) errnoClass = systemCallError;
 
         return newRaiseException(errnoClass, null);
     }
 
     public RaiseException newErrnoFromInt(int errno, String methodName, String message) {
         if (Platform.IS_WINDOWS && ("stat".equals(methodName) || "lstat".equals(methodName))) {
             if (errno == 20047) return newErrnoENOENTError(message); // boo:bar UNC stat failure
             if (errno == Errno.ESRCH.intValue()) return newErrnoENOENTError(message); // ESRCH on stating ""
         }
 
         return newErrnoFromInt(errno, message);
     }
 
     public RaiseException newErrnoFromInt(int errno, String message) {
         RubyClass errnoClass = getErrno(errno);
         if (errnoClass != null) {
             return newRaiseException(errnoClass, message);
         } else {
             return newSystemCallError("Unknown Error (" + errno + ") - " + message);
         }
     }
 
     public RaiseException newErrnoFromErrno(Errno errno, String message) {
         if (errno == null || errno == Errno.__UNKNOWN_CONSTANT__) {
             return newSystemCallError(message);
         }
         return newErrnoFromInt(errno.intValue(), message);
     }
 
     public RaiseException newErrnoFromInt(int errno) {
         Errno errnoObj = Errno.valueOf(errno);
         if (errnoObj == null) {
             return newSystemCallError("Unknown Error (" + errno + ")");
         }
         String message = errnoObj.description();
         return newErrnoFromInt(errno, message);
     }
 
     private final static Pattern ADDR_NOT_AVAIL_PATTERN = Pattern.compile("assign.*address");
 
     public RaiseException newErrnoEADDRFromBindException(BindException be) {
 		return newErrnoEADDRFromBindException(be, null);
 	}
 
     public RaiseException newErrnoEADDRFromBindException(BindException be, String contextMessage) {
         String msg = be.getMessage();
         if (msg == null) {
             msg = "bind";
         } else {
             msg = "bind - " + msg;
         }
         if (contextMessage != null) {
             msg = msg + contextMessage;
         }
         // This is ugly, but what can we do, Java provides the same BindingException
         // for both EADDRNOTAVAIL and EADDRINUSE, so we differentiate the errors
         // based on BindException's message.
         if(ADDR_NOT_AVAIL_PATTERN.matcher(msg).find()) {
             return newErrnoEADDRNOTAVAILError(msg);
         } else {
             return newErrnoEADDRINUSEError(msg);
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
 
     public RaiseException newInterruptedRegexpError(String message) {
         return newRaiseException(getInterruptedRegexpError(), message);
     }
 
     public RaiseException newRangeError(String message) {
         return newRaiseException(getRangeError(), message);
     }
 
     public RaiseException newNotImplementedError(String message) {
         return newRaiseException(getNotImplementedError(), message);
     }
 
     public RaiseException newInvalidEncoding(String message) {
         return newRaiseException(fastGetClass("Iconv").getClass("InvalidEncoding"), message);
     }
 
     public RaiseException newIllegalSequence(String message) {
         return newRaiseException(fastGetClass("Iconv").getClass("IllegalSequence"), message);
     }
 
     public RaiseException newNoMethodError(String message, String name, IRubyObject args) {
         return new RaiseException(new RubyNoMethodError(this, getNoMethodError(), message, name, args), true);
     }
 
     public RaiseException newNameError(String message, String name) {
         return newNameError(message, name, null);
     }
 
     // This name sucks and should be replaced by newNameErrorfor 9k.
     public RaiseException newNameErrorObject(String message, IRubyObject name) {
         RubyException error = new RubyNameError(this, getNameError(), message, name);
 
         return new RaiseException(error, false);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException) {
         return newNameError(message, name, origException, false);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException, boolean printWhenVerbose) {
         if (origException != null) {
             if (printWhenVerbose && isVerbose()) {
                 LOG.error(origException.getMessage(), origException);
             } else if (isDebug()) {
                 LOG.debug(origException.getMessage(), origException);
             }
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
 
     public RaiseException newLoadError(String message, String path) {
         RaiseException loadError = newRaiseException(getLoadError(), message);
         loadError.getException().setInstanceVariable("@path", newString(path));
         return loadError;
     }
 
     public RaiseException newFrozenError(String objectType) {
         return newFrozenError(objectType, false);
     }
 
     public RaiseException newFrozenError(String objectType, boolean runtimeError) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(getRuntimeError(), "can't modify frozen " + objectType);
     }
 
     public RaiseException newSystemStackError(String message) {
         return newRaiseException(getSystemStackError(), message);
     }
 
     public RaiseException newSystemStackError(String message, StackOverflowError soe) {
         if (getDebug().isTrue()) {
             LOG.debug(soe.getMessage(), soe);
         }
         return newRaiseException(getSystemStackError(), message);
     }
 
     public RaiseException newSystemExit(int status) {
         return new RaiseException(RubySystemExit.newInstance(this, status, "exit"));
     }
 
     public RaiseException newSystemExit(int status, String message) {
diff --git a/core/src/main/java/org/jruby/runtime/load/CompiledScriptLoader.java b/core/src/main/java/org/jruby/runtime/load/CompiledScriptLoader.java
index 4456f94baa..8088f22c5d 100644
--- a/core/src/main/java/org/jruby/runtime/load/CompiledScriptLoader.java
+++ b/core/src/main/java/org/jruby/runtime/load/CompiledScriptLoader.java
@@ -1,74 +1,66 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.runtime.load;
 
-import java.io.BufferedInputStream;
-import java.io.ByteArrayOutputStream;
-import java.io.IOException;
-import java.io.InputStream;
-
 import org.jruby.Ruby;
-import org.jruby.ast.executable.Script;
+import org.jruby.ir.IRScope;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.OneShotClassLoader;
 import org.objectweb.asm.ClassReader;
 
+import java.io.BufferedInputStream;
+import java.io.ByteArrayOutputStream;
+import java.io.IOException;
+import java.io.InputStream;
+import java.lang.reflect.Method;
+
 /**
- *
- * @author headius
+ * Load serialized IR from the .class file requested.
  */
 public class CompiledScriptLoader {
-    public static Script loadScriptFromFile(Ruby runtime, InputStream inStream, String resourceName) {
+    public static IRScope loadScriptFromFile(Ruby runtime, InputStream inStream, String resourceName) {
         InputStream in = null;
         try {
             in = new BufferedInputStream(inStream, 8192);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             byte[] buf = new byte[8196];
             int read = 0;
             while ((read = in.read(buf)) != -1) {
                 baos.write(buf, 0, read);
             }
             buf = baos.toByteArray();
             JRubyClassLoader jcl = runtime.getJRubyClassLoader();
             OneShotClassLoader oscl = new OneShotClassLoader(jcl);
 
             ClassReader cr = new ClassReader(buf);
             String className = cr.getClassName().replace('/', '.');
 
             Class clazz = oscl.defineClass(className, buf);
 
-            // if it's a compiled JRuby script, instantiate and run it
-            if (Script.class.isAssignableFrom(clazz)) {
-                return (Script)clazz.newInstance();
-            } else {
-                throw runtime.newLoadError("use `java_import' to load normal Java classes: "+className);
+            try {
+                Method method = clazz.getMethod("loadIR", Ruby.class);
+                return (IRScope)method.invoke(null, runtime);
+            } catch (Exception e) {
+                // fall through
             }
+
+            throw runtime.newLoadError("use `java_import' to load normal Java classes: "+className);
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
-        } catch (InstantiationException ie) {
-            if (runtime.getDebug().isTrue()) {
-                ie.printStackTrace();
-            }
-            throw runtime.newLoadError("Error loading compiled script '" + resourceName + "': " + ie);
-        } catch (IllegalAccessException iae) {
-            if (runtime.getDebug().isTrue()) {
-                iae.printStackTrace();
-            }
-            throw runtime.newLoadError("Error loading compiled script '" + resourceName + "': " + iae);
         } catch (LinkageError le) {
             if (runtime.getDebug().isTrue()) {
                 le.printStackTrace();
             }
             throw runtime.newLoadError("Linkage error loading compiled script; you may need to recompile '" + resourceName + "': " + le);
         } finally {
             try {
                 in.close();
             } catch (IOException ioe) {
                 throw runtime.newIOErrorFromException(ioe);
             }
         }
     }
 }
diff --git a/core/src/main/java/org/jruby/runtime/load/JavaCompiledScript.java b/core/src/main/java/org/jruby/runtime/load/JavaCompiledScript.java
index 257beeaf04..9f3223d133 100644
--- a/core/src/main/java/org/jruby/runtime/load/JavaCompiledScript.java
+++ b/core/src/main/java/org/jruby/runtime/load/JavaCompiledScript.java
@@ -1,55 +1,57 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2007 The JRuby Community
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.runtime.load;
 
 import java.io.IOException;
 import org.jruby.Ruby;
 import org.jruby.ast.executable.Script;
+import org.jruby.ir.IRScope;
 
 public class JavaCompiledScript implements Library {
     private final LoadServiceResource resource;
     
     public JavaCompiledScript(LoadServiceResource resource) {
         this.resource = resource;
     }
 
     public void load(Ruby runtime, boolean wrap) {
         try {
-            Script script = CompiledScriptLoader.loadScriptFromFile(runtime, resource.getInputStream(), resource.getName());
+            IRScope script = CompiledScriptLoader.loadScriptFromFile(runtime, resource.getInputStream(), resource.getName());
             if (script == null) {
                 // we're depending on the side effect of the load, which loads the class but does not turn it into a script
                 // I don't like it, but until we restructure the code a bit more, we'll need to quietly let it by here.
                 return;
             }
-            script.setFilename(resource.getName());
-            runtime.loadScript(script, wrap);
+            // FIXME: We need to be able to set the actual name for __FILE__ and friends to reflect it properly (#3109)
+//            script.setFilename(resource.getName());
+            runtime.loadScope(script, wrap);
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 }
diff --git a/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java b/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
index 971fb357e4..c11fea5d38 100644
--- a/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
+++ b/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
@@ -1,320 +1,322 @@
 package org.jruby.runtime.load;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.RubyFile;
 import org.jruby.RubyHash;
 import org.jruby.RubyString;
 import org.jruby.ast.executable.Script;
+import org.jruby.ir.IRScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.LoadService.SuffixType;
 import org.jruby.util.FileResource;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.URLResource;
 
 class LibrarySearcher {
     static class FoundLibrary implements Library {
         private final Library delegate;
         private final String loadName;
 
         public FoundLibrary(Library delegate, String loadName) {
             this.delegate = delegate;
             this.loadName = loadName;
         }
 
         @Override
         public void load(Ruby runtime, boolean wrap) throws IOException {
             delegate.load(runtime, wrap);
         }
 
         public String getLoadName() {
             return loadName;
         }
     }
 
     private final LoadService loadService;
     private final Ruby runtime;
     private final Map<String, Library> builtinLibraries;
 
     public LibrarySearcher(LoadService loadService) {
         this.loadService = loadService;
         this.runtime = loadService.runtime;
         this.builtinLibraries = loadService.builtinLibraries;
     }
 
     // TODO(ratnikov): Kill this helper once we kill LoadService.SearchState
     public FoundLibrary findBySearchState(LoadService.SearchState state) {
         FoundLibrary lib = findLibrary(state.searchFile, state.suffixType);
         if (lib != null) {
             state.library = lib;
             state.setLoadName(lib.getLoadName());
         }
         return lib;
     }
 
     public FoundLibrary findLibrary(String baseName, SuffixType suffixType) {
         boolean searchedForServiceLibrary = false;
 
         for (String suffix : suffixType.getSuffixes()) {
             FoundLibrary library = findBuiltinLibrary(baseName, suffix);
             if (library == null) library = findResourceLibrary(baseName, suffix);
 
             // Since searching for a service library doesn't take the suffix into account, there's no need
             // to perform it more than once.
             if (library == null && !searchedForServiceLibrary) {
                 library = findServiceLibrary(baseName, suffix);
                 searchedForServiceLibrary = true;
             }
 
             if (library != null) {
                 return library;
             }
         }
 
         return null;
     }
 
     private FoundLibrary findBuiltinLibrary(String name, String suffix) {
         String namePlusSuffix = name + suffix;
 
         DebugLog.Builtin.logTry(namePlusSuffix);
         if (builtinLibraries.containsKey(namePlusSuffix)) {
             DebugLog.Builtin.logFound(namePlusSuffix);
             return new FoundLibrary(
                     builtinLibraries.get(namePlusSuffix),
                     namePlusSuffix);
         }
         return null;
     }
 
     private FoundLibrary findServiceLibrary(String name, String ignored) {
         DebugLog.JarExtension.logTry(name);
         Library extensionLibrary = ClassExtensionLibrary.tryFind(runtime, name);
         if (extensionLibrary != null) {
             DebugLog.JarExtension.logFound(name);
             return new FoundLibrary(extensionLibrary, name);
         } else {
             return null;
         }
     }
 
     private FoundLibrary findResourceLibrary(String baseName, String suffix) {
         if (baseName.startsWith("./")) {
             return findFileResource(baseName, suffix);
         }
 
         if (baseName.startsWith("../")) {
             // Path should be canonicalized in the findFileResource
             return findFileResource(baseName, suffix);
         }
 
         if (baseName.startsWith("~/")) {
             RubyHash env = (RubyHash) runtime.getObject().getConstant("ENV");
             RubyString env_home = runtime.newString("HOME");
             if (env.has_key_p(env_home).isFalse()) {
                 return null;
             }
             String home = env.op_aref(runtime.getCurrentContext(), env_home).toString();
             String path = home + "/" + baseName.substring(2);
 
             return findFileResource(path, suffix);
         }
 
         // If path is considered absolute, bypass loadPath iteration and load as-is
         if (isAbsolute(baseName)) {
           return findFileResource(baseName, suffix);
         }
 
         // search the $LOAD_PATH
         try {
             for (IRubyObject loadPathEntry : loadService.loadPath.toJavaArray()) {
                 FoundLibrary library = findFileResourceWithLoadPath(baseName, suffix, getPath(loadPathEntry));
                 if (library != null) return library;
             }
         } catch (Throwable t) {
             t.printStackTrace();
         }
 
         // inside a classloader the path "." is the place where to find the jruby kernel
         if (!runtime.getCurrentDirectory().startsWith(URLResource.URI_CLASSLOADER)) {
 
             // ruby does not load a relative path unless the current working directory is in $LOAD_PATH
             FoundLibrary library = findFileResourceWithLoadPath(baseName, suffix, ".");
 
             // we did not find the file on the $LOAD_PATH but in current directory so we need to treat it
             // as not found (the classloader search below will find it otherwise)
             if (library != null) return null;
         }
 
         // load the jruby kernel and all resource added to $CLASSPATH
         return findFileResourceWithLoadPath(baseName, suffix, URLResource.URI_CLASSLOADER);
     }
 
     // FIXME: to_path should not be called n times it should only be once and that means a cache which would
     // also reduce all this casting and/or string creates.
     // (mkristian) would it make sense to turn $LOAD_PATH into something like RubyClassPathVariable where we could cache
     // the Strings ?
     private String getPath(IRubyObject loadPathEntry) {
         return RubyFile.get_path(runtime.getCurrentContext(), loadPathEntry).asJavaString();
     }
 
     private FoundLibrary findFileResource(String searchName, String suffix) {
         return findFileResourceWithLoadPath(searchName, suffix, null);
     }
 
     private FoundLibrary findFileResourceWithLoadPath(String searchName, String suffix, String loadPath) {
         String fullPath = loadPath != null ? loadPath + "/" + searchName : searchName;
         String pathWithSuffix = fullPath + suffix;
 
         DebugLog.Resource.logTry(pathWithSuffix);
         FileResource resource = JRubyFile.createResourceAsFile(runtime, pathWithSuffix);
         if (resource.exists()) {
             DebugLog.Resource.logFound(pathWithSuffix);
             String scriptName = resolveScriptName(resource, pathWithSuffix);
             String loadName = resolveLoadName(resource, searchName + suffix);
 
             return new FoundLibrary(ResourceLibrary.create(searchName, scriptName, resource), loadName);
         }
 
         return null;
     }
 
     private static boolean isAbsolute(String path) {
         // jar: prefix doesn't mean anything anymore, but we might still encounter it
         if (path.startsWith("jar:")) {
             path = path.substring(4);
         }
 
         if (path.startsWith("file:")) {
             // We treat any paths with a file schema as absolute, because apparently some tests
             // explicitely depend on such behavior (test/test_load.rb). On other hand, maybe it's
             // not too bad, since otherwise joining LOAD_PATH logic would be more complicated if
             // it'd have to worry about schema.
             return true;
         }
         if (path.startsWith("uri:")) {
             // uri: are absolute
             return true;
         }
         return new File(path).isAbsolute();
     }
 
     protected String resolveLoadName(FileResource resource, String ruby18path) {
         return resource.absolutePath();
     }
 
     protected String resolveScriptName(FileResource resource, String ruby18Path) {
         return resource.absolutePath();
     }
 
     static class ResourceLibrary implements Library {
         public static ResourceLibrary create(String searchName, String scriptName, FileResource resource) {
             String location = resource.absolutePath();
 
             if (location.endsWith(".class")) return new ClassResourceLibrary(searchName, scriptName, resource);
             if (location.endsWith(".jar")) return new JarResourceLibrary(searchName, scriptName, resource);
 
             return new ResourceLibrary(searchName, scriptName, resource); // just .rb?
         }
 
         protected final String searchName;
         protected final String scriptName;
         protected final FileResource resource;
         protected final String location;
 
         public ResourceLibrary(String searchName, String scriptName, FileResource resource) {
             this.searchName = searchName;
             this.scriptName = scriptName;
             this.location = resource.absolutePath();
             this.resource = resource;
         }
 
         @Override
         public void load(Ruby runtime, boolean wrap) {
             InputStream ris = null;
             try {
                 ris = resource.inputStream();
                 runtime.loadFile(scriptName, new LoadServiceResourceInputStream(ris), wrap);
             } catch(IOException e) {
                 throw runtime.newLoadError("no such file to load -- " + searchName, searchName);
             } finally {
                 try {
                     if (ris != null) ris.close();
                 } catch (IOException ioE) { /* At least we tried.... */}
             }
         }
     }
 
     static class ClassResourceLibrary extends ResourceLibrary {
         public ClassResourceLibrary(String searchName, String scriptName, FileResource resource) {
             super(searchName, scriptName, resource);
         }
 
         @Override
         public void load(Ruby runtime, boolean wrap) {
             InputStream is = null;
             try {
                 is = new BufferedInputStream(resource.inputStream(), 32768);
-                Script script = CompiledScriptLoader.loadScriptFromFile(runtime, is, searchName);
+                IRScope script = CompiledScriptLoader.loadScriptFromFile(runtime, is, searchName);
 
                 // Depending on the side-effect of the load, which loads the class but does not turn it into a script.
                 // I don't like it, but until we restructure the code a bit more, we'll need to quietly let it by here.
                 if (script == null) return;
 
-                script.setFilename(scriptName);
-                runtime.loadScript(script, wrap);
+                // FIXME: We need to be able to set the actual name for __FILE__ and friends to reflect it properly (#3109)
+//                script.setFilename(scriptName);
+                runtime.loadScope(script, wrap);
             } catch(IOException e) {
                 throw runtime.newLoadError("no such file to load -- " + searchName, searchName);
             } finally {
                 try {
                     if (is != null) is.close();
                 } catch (IOException ioE) { /* At least we tried.... */ }
             }
         }
     }
 
     static class JarResourceLibrary extends ResourceLibrary {
         public JarResourceLibrary(String searchName, String scriptName, FileResource resource) {
             super(searchName, scriptName, resource);
         }
 
         @Override
         public void load(Ruby runtime, boolean wrap) {
             try {
                 URL url;
                 if (location.startsWith(URLResource.URI)) {
                     url = null;
                     runtime.getJRubyClassLoader().addURLNoIndex(URLResource.getResourceURL(runtime, location));
                 } else {
                     File f = new File(location);
                     if (f.exists() || location.contains( "!")){
                         url = f.toURI().toURL();
                         if (location.contains( "!")) {
                             url = new URL( "jar:" + url );
                         }
                     } else {
                         url = new URL(location);
                     }
                 }
                 if (url != null) {
                     runtime.getJRubyClassLoader().addURL(url);
                 }
             } catch (MalformedURLException badUrl) {
                 runtime.newIOErrorFromException(badUrl);
             }
 
             // If an associated Service library exists, load it as well
             ClassExtensionLibrary serviceExtension = ClassExtensionLibrary.tryFind(runtime, searchName);
             if (serviceExtension != null) {
                 serviceExtension.load(runtime, wrap);
             }
         }
     }
 }
diff --git a/lib/ruby/stdlib/jruby/compiler.rb b/lib/ruby/stdlib/jruby/compiler.rb
index a9ad052603..baa8b5bc9c 100644
--- a/lib/ruby/stdlib/jruby/compiler.rb
+++ b/lib/ruby/stdlib/jruby/compiler.rb
@@ -1,285 +1,303 @@
 require 'optparse'
 require 'fileutils'
 require 'digest/sha1'
 require 'jruby'
 require 'jruby/compiler/java_class'
 
 module JRuby::Compiler
   BAIS = java.io.ByteArrayInputStream
   Mangler = org.jruby.util.JavaNameMangler
   Opcodes = org.objectweb.asm.Opcodes rescue org.jruby.org.objectweb.asm.Opcodes
   ClassWriter = org.objectweb.asm.ClassWriter rescue org.jruby.org.objectweb.asm.ClassWriter
   SkinnyMethodAdapter = org.jruby.compiler.impl.SkinnyMethodAdapter
   ByteArrayOutputStream = java.io.ByteArrayOutputStream
   IRWriterStream = org.jruby.ir.persistence.IRWriterStream
   IRWriter = org.jruby.ir.persistence.IRWriter
   JavaFile = java.io.File
   MethodSignatureNode = org.jruby.ast.java_signature.MethodSignatureNode
   DEFAULT_PREFIX = ""
 
   def default_options
     {
       :basedir => Dir.pwd,
       :prefix => DEFAULT_PREFIX,
       :target => Dir.pwd,
       :java => false,
       :javac => false,
       :classpath => [],
       :javac_options => [],
       :sha1 => false,
       :handles => false,
       :verbose => false
     }
   end
   module_function :default_options
   
   def compile_argv(argv)
     options = default_options
 
     OptionParser.new("", 24, '  ') do |opts|
       opts.banner = "jrubyc [options] (FILE|DIRECTORY)"
       opts.separator ""
 
       opts.on("-d", "--dir DIR", "Use DIR as the base path") do |dir|
         options[:basedir] = dir
       end
 
       opts.on("-p", "--prefix PREFIX", "Prepend PREFIX to the file path and package. Default is no prefix.") do |pre|
         options[:prefix] = pre
       end
 
       opts.on("-t", "--target TARGET", "Output files to TARGET directory") do |tgt|
         options[:target] = tgt
       end
 
       opts.on("-J OPTION", "Pass OPTION to javac for javac compiles") do |tgt|
         options[:javac_options] << tgt
       end
 
       opts.on("-5"," --jdk5", "Generate JDK 5 classes (version 49)") do |x|
         options[:jdk5] = true
       end
 
       opts.on("--java", "Generate .java classes to accompany the script") do
         options[:java] = true
       end
 
       opts.on("--javac", "Generate and compile .java classes to accompany the script") do
         options[:javac] = true
       end
 
       opts.on("-c", "--classpath CLASSPATH", "Add a jar to the classpath for building") do |cp|
         options[:classpath].concat cp.split(':')
       end
 
       opts.on("--sha1", "Compile to a class named using the SHA1 hash of the source file") do
         options[:sha1] = true
       end
 
       opts.on("--handles", "Also generate all direct handle classes for the source file") do
         options[:handles] = true
       end
       
       opts.on("--verbose", "Log verbose output while compile") do
         options[:verbose] = true
       end
 
       opts.parse!(argv)
     end
 
     if (argv.length == 0)
       raise "No files or directories specified"
     end
 
     compile_files_with_options(argv, options)
   end
   module_function :compile_argv
 
   # deprecated, but retained for backward compatibility
   def compile_files(filenames, basedir = Dir.pwd, prefix = DEFAULT_PREFIX, target = Dir.pwd, java = false, javac = false, javac_options = [], classpath = [])
     compile_files_with_options(
       filenames,
       :basedir => basedir,
       :prefix => prefix,
       :target => target,
       :java => java,
       :javac => javac,
       :javac_options => javac_options,
       :classpath => classpath,
       :sha1 => false,
       :handles => false,
       :verbose => false
     )
   end
   module_function :compile_files
   
   def compile_files_with_options(filenames, options = default_options)
     runtime = JRuby.runtime
 
     unless File.exist? options[:target]
       raise "Target dir not found: #{options[:target]}"
     end
 
     files = []
 
     # The compilation code
     compile_proc = proc do |filename|
       begin
         file = File.open(filename)
 
         if options[:sha1]
           pathname = "ruby.jit.FILE_" + Digest::SHA1.hexdigest(File.read(filename)).upcase
         else
           pathname = Mangler.mangle_filename_for_classpath(filename, options[:basedir], options[:prefix], true, false)
         end
 
         source = file.read
 
         if options[:java] || options[:javac]
           node = runtime.parse_file(BAIS.new(source.to_java_bytes), filename, nil)
 
           ruby_script = JavaGenerator.generate_java(node, filename)
           ruby_script.classes.each do |cls|
             java_dir = File.join(options[:target], cls.package.gsub('.', '/'))
 
             FileUtils.mkdir_p java_dir
 
             java_src = File.join(java_dir, cls.name + ".java")
             puts "Generating Java class #{cls.name} to #{java_src}" if options[:verbose]
             
             files << java_src
 
             File.open(java_src, 'w') do |f|
               f.write(cls.to_s)
             end
           end
         else
           puts "Compiling #{filename}" if options[:verbose]
 
           scope = JRuby.compile_ir(source, filename)
           bytes = ByteArrayOutputStream.new
           stream = IRWriterStream.new(bytes)
           IRWriter.persist(stream, scope)
           string = String.from_java_bytes(bytes.to_byte_array, 'BINARY')
 
           # bust it up into 32k-1 chunks
           pieces = string.scan(/.{1,32767}/m)
 
           cls = ClassWriter.new(ClassWriter::COMPUTE_MAXS | ClassWriter::COMPUTE_FRAMES)
           cls.visit(
               Opcodes::V1_7,
               Opcodes::ACC_PUBLIC,
               pathname.gsub(".", "/"),
               nil,
               "java/lang/Object",
               nil
           )
           cls.visit_source filename, nil
 
           cls.visit_field(
               Opcodes::ACC_PRIVATE | Opcodes::ACC_STATIC | Opcodes::ACC_FINAL,
               "script_ir",
               "Ljava/lang/String;",
               nil,
               nil
           )
 
           static = SkinnyMethodAdapter.new(
               cls,
               Opcodes::ACC_PUBLIC | Opcodes::ACC_STATIC,
               "<clinit>",
               "()V",
               nil,
               nil)
           static.start
 
           # put String back together
           static.newobj("java/lang/StringBuilder")
           static.dup
           static.invokespecial("java/lang/StringBuilder", "<init>", "()V")
           pieces.each do |piece|
             static.ldc(piece)
             static.invokevirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")
           end
           static.invokevirtual("java/lang/Object", "toString", "()Ljava/lang/String;")
           static.putstatic(pathname, "script_ir", "Ljava/lang/String;")
           static.voidreturn
           static.end
           
           main = SkinnyMethodAdapter.new(
               cls,
               Opcodes::ACC_PUBLIC | Opcodes::ACC_STATIC,
               "main",
               "([Ljava/lang/String;)V",
               nil,
               nil)
           main.start
           main.invokestatic("org/jruby/Ruby", "newInstance", "()Lorg/jruby/Ruby;")
           main.astore(1)
           main.aload(1)
           main.aload(1)
 
           main.getstatic(pathname, "script_ir", "Ljava/lang/String;")
 
           main.ldc("ISO-8859-1")
           main.invokevirtual("java/lang/String", "getBytes", "(Ljava/lang/String;)[B")
           main.invokestatic("org/jruby/ir/runtime/IRRuntimeHelpers", "decodeScopeFromBytes", "(Lorg/jruby/Ruby;[B)Lorg/jruby/ir/IRScope;")
           main.invokevirtual("org/jruby/Ruby", "runInterpreter", "(Lorg/jruby/ParseResult;)Lorg/jruby/runtime/builtin/IRubyObject;")
           main.voidreturn
           main.end
 
+          loadIR = SkinnyMethodAdapter.new(
+              cls,
+              Opcodes::ACC_PUBLIC | Opcodes::ACC_STATIC,
+              "loadIR",
+              "(Lorg/jruby/Ruby;)Lorg/jruby/ir/IRScope;",
+              nil,
+              nil)
+          loadIR.start
+          loadIR.aload(0)
+
+          loadIR.getstatic(pathname, "script_ir", "Ljava/lang/String;")
+
+          loadIR.ldc("ISO-8859-1")
+          loadIR.invokevirtual("java/lang/String", "getBytes", "(Ljava/lang/String;)[B")
+          loadIR.invokestatic("org/jruby/ir/runtime/IRRuntimeHelpers", "decodeScopeFromBytes", "(Lorg/jruby/Ruby;[B)Lorg/jruby/ir/IRScope;")
+          loadIR.areturn
+          loadIR.end
+
           # prepare target
           class_filename = filename.sub(/(\.rb)?$/, '.class')
           target_file = File.join(options[:target], class_filename)
           target_dir = File.dirname(target_file)
           FileUtils.mkdir_p(target_dir)
 
           # write class
           File.open(target_file, 'wb') do |f|
             f.write(cls.to_byte_array)
           end
         end
 
         0
       # rescue Exception
       #   puts "Failure during compilation of file #{filename}:\n#{$!}"
       #   puts $!.backtrace
       #   1
       ensure
         file.close unless file.nil?
       end
     end
 
     errors = 0
     # Process all the file arguments
     Dir[*filenames].each do |filename|
       unless File.exists? filename
         puts "Error -- file not found: #{filename}"
         errors += 1
         next
       end
 
       if (File.directory?(filename))
         puts "Compiling all in '#{File.expand_path(filename)}'..." if options[:verbose]
         Dir.glob(filename + "/**/*.rb").each { |filename|
           errors += compile_proc[filename]
 	}
       else
         if filename =~ /\.java$/
           files << filename
         else
           errors += compile_proc[filename]
         end
       end
     end
 
     if options[:javac]
       javac_string = JavaGenerator.generate_javac(files, options)
       puts javac_string if options[:verbose]
       system javac_string
     end
 
     errors
   end
   module_function :compile_files_with_options
 end
diff --git a/rakelib/test.rake b/rakelib/test.rake
index c3d7995919..3cc11a194d 100644
--- a/rakelib/test.rake
+++ b/rakelib/test.rake
@@ -1,175 +1,176 @@
 require 'rake/testtask'
 
 desc "Alias for spec:ci"
 task :spec => "spec:ci"
 
 desc "Alias for test:short"
 task :test => "test:short"
 
 if ENV['CI']
   # MRI tests have a different flag for color
   ADDITIONAL_TEST_OPTIONS = "-v --color=never --tty=no"
 
   # for normal test/unit tests
   ENV['TESTOPT'] = "-v --no-use-color"
 else
   ADDITIONAL_TEST_OPTIONS = ""
 end
 
 namespace :test do
   desc "Compile test code"
   task :compile do
     mkdir_p "test/target/test-classes"
     classpath = %w[lib/jruby.jar test/target/junit.jar].join(File::PATH_SEPARATOR)
     sh "javac -cp #{classpath} -d test/target/test-classes #{Dir['spec/java_integration/fixtures/**/*.java'].to_a.join(' ')}"
   end
 
   short_tests = ['jruby', 'mri']
   slow_tests = ['test:slow', 'test:objectspace']
   specs = ['spec:ji', 'spec:compiler', 'spec:ffi', 'spec:regression'];
   long_tests = ["test:tracing"] + short_tests + slow_tests + specs
   all_tests = long_tests.map {|test| test + ':all'}
 
   desc "Run the short suite: #{short_tests.inspect}"
   task :short => [:compile, *short_tests]
 
   desc "Run the long suite: #{long_tests.inspect}"
   task :long => [:compile, *long_tests]
 
   desc "Run the comprehensive suite: #{all_tests}"
   task :all => [:compile, *all_tests]
 
   desc "Run tests that are too slow for the main suite"
   task :slow_suites => [:compile, *slow_tests]
 
   task :rake_targets => long_tests
   task :extended => long_tests
 
   desc "Run tracing tests"
   task :tracing do
     Rake::TestTask.new('test:tracing') do |t|
       t.pattern = 'test/tracing/test_*.rb'
       t.verbose = true
       t.ruby_opts << '-J-ea'
       t.ruby_opts << '--debug'
       t.ruby_opts << '--disable-gems'
     end
   end
   
   compile_flags = {
     :default => :int,
     :int => ["-X-C"],
     :jit => ["-Xjit.threshold=0", "-Xjit.background=false", "-J-XX:MaxPermSize=512M"],
     :aot => ["-X+C", "-J-XX:MaxPermSize=512M"],
     :all => [:int, :jit, :aot]
   }
 
   def files_in_file(filename)
     files = []
     File.readlines(filename).each do |line|
       filename = "test/#{line.chomp}.rb"
       files << filename if File.exist? filename
     end
     files
   end
 
   namespace :mri do
     mri_test_files = File.readlines('test/mri.index').grep(/^[^#]\w+/).map(&:chomp).join(' ')
     task :int do
       ruby "-X-C -r ./test/mri_test_env.rb test/mri/runner.rb #{ADDITIONAL_TEST_OPTIONS} -q -- #{mri_test_files}"
     end
 
     task :int_full do
       ruby "-Xjit.threshold=0 -Xjit.background=false -X-C -r ./test/mri_test_env.rb test/mri/runner.rb #{ADDITIONAL_TEST_OPTIONS} -q -- #{mri_test_files}"
     end
 
     task :jit do
       ruby "-J-XX:MaxPermSize=512M -Xjit.threshold=0 -Xjit.background=false -r ./test/mri_test_env.rb test/mri/runner.rb #{ADDITIONAL_TEST_OPTIONS} -q -- #{mri_test_files}"
     end
 
     task :aot do
       ruby "-J-XX:MaxPermSize=512M -X+C -Xjit.background=false -r ./test/mri_test_env.rb test/mri/runner.rb #{ADDITIONAL_TEST_OPTIONS} -q -- #{mri_test_files}"
     end
 
     task all: %s[int jit aot]
   end
   task mri: 'test:mri:int'
 
   permute_tests(:jruby, compile_flags, 'test:compile') do |t|
     files = []
     File.open('test/jruby.index') do |f|
       f.each_line.each do |line|
         filename = "test/#{line.chomp}.rb"
         next unless File.exist? filename
         files << filename
       end
     end
     t.test_files = files
     t.verbose = true
+    t.ruby_opts << '-Xaot.loadClasses=true' # disabled by default now
     t.ruby_opts << '-I.'
     t.ruby_opts << '-J-ea'
     classpath = %w[test test/target/test-classes core/target/test-classes].join(File::PATH_SEPARATOR)
     t.ruby_opts << "-J-cp #{classpath}"
   end
 
   permute_tests(:slow, compile_flags) do |t|
     files = []
     File.open('test/slow.index') do |f|
       f.each_line.each do |line|
         filename = "test/#{line.chomp}.rb"
         next unless File.exist? filename
         files << filename
       end
     end
     t.test_files = files
     t.verbose = true
     t.test_files = files_in_file 'test/slow.index'
     t.ruby_opts << '-J-ea' << '--1.8'
     t.ruby_opts << '-J-cp target/test-classes'
   end
 
   permute_tests(:objectspace, compile_flags) do |t|
     files = []
     File.open('test/objectspace.index') do |f|
       f.each_line.each do |line|
         filename = "test/#{line.chomp}.rb"
         next unless File.exist? filename
         files << filename
       end
     end
     t.test_files = files
     t.verbose = true
     t.ruby_opts << '-J-ea'
     t.ruby_opts << '-X+O'
   end
   
   def junit(options)
     cp = options[:classpath] or raise "junit tasks must have classpath"
     test = options[:test] or raise "junit tasks must have test"
     
     cmd = "#{ENV_JAVA['java.home']}/bin/java -cp #{cp.join(File::PATH_SEPARATOR)} -Djruby.compat.mode=1.8 junit.textui.TestRunner #{test}"
     
     puts cmd
     system cmd
   end
   
   namespace :junit do
     test_class_path = [
       "target/junit.jar",
       "target/livetribe-jsr223.jar",
       "target/bsf.jar",
       "target/commons-logging.jar",
       "lib/jruby.jar",
       "target/test-classes",
       "test/jruby/requireTest.jar",
       "test"
     ]
     
     desc "Run the main JUnit test suite"
     task :main => 'test:compile' do
       junit :classpath => test_class_path, :test => "org.jruby.test.MainTestSuite", :maxmemory => '512M' do
         jvmarg :line => '-ea'
       end
     end
   end
 end
diff --git a/test/jruby.index b/test/jruby.index
index b4df211ad5..3ac2e8eb47 100644
--- a/test/jruby.index
+++ b/test/jruby.index
@@ -1,114 +1,114 @@
 # Our own test/unit-based tests
 # NOTE: test_globals comes first here because it has tests that $? be nil
 jruby/test_globals
 
 jruby/test_addrinfo
 jruby/test_argf
 jruby/test_array
 jruby/test_autoload
 jruby/test_backquote
 jruby/test_backtraces
 jruby/test_big_decimal
 jruby/test_binding_eval_yield
 jruby/test_caller
 jruby/test_case
 jruby/test_class
 jruby/test_comparable
 jruby/test_core_arities
 jruby/test_custom_enumerable
 jruby/test_cvars_in_odd_scopes
 jruby/test_date_joda_time
 jruby/test_defined
 jruby/test_default_constants
 jruby/test_delegated_array_equals
 jruby/test_dir
 jruby/test_dir_with_jar_without_dir_entry
 jruby/test_digest_extend
 jruby/test_digest2
 jruby/test_env
 jruby/test_etc
 jruby/test_file
 # IRBuilder is unable to build these currently
 #jruby/test_flip
 jruby/test_frame_self
 jruby/test_hash
 jruby/test_higher_javasupport
 jruby/test_ifaddr
 jruby/test_included_in_object_space
 jruby/test_integer_overflows
 jruby/test_ivar_table_integrity
 jruby/test_io
 jruby/test_load
 jruby/test_load_gem_extensions
 jruby/test_method
 jruby/test_method_cache
 jruby/test_method_override_and_caching
 jruby/test_java_accessible_object
 jruby/test_java_extension
 jruby/test_java_wrapper_deadlock
 jruby/test_jruby_internals
 jruby/test_marshal_with_instance_variables
 jruby/test_marshal_gemspec
 jruby/test_method_missing
 jruby/test_no_stack_trace_stomp
 jruby/test_pack
 jruby/test_primitive_to_java
 jruby/test_process
 jruby/test_proc_visibility
 jruby/test_parsing
 jruby/test_pathname
 jruby/test_random
 jruby/test_rbconfig
 jruby/test_require_once
 jruby/test_respond_to
 jruby/test_socket
 jruby/test_string_java_bytes
 jruby/test_string_printf
 jruby/test_string_to_number
 jruby/test_super_call_site_caching
 jruby/test_system
 jruby/test_system_error
 jruby/test_timeout
 jruby/test_thread
 # FIXME probably should figure this one out
 #test_thread_backtrace
 jruby/test_threaded_nonlocal_return
 jruby/test_time_add
 jruby/test_time_nil_ops
 jruby/test_time_tz
 jruby/test_unmarshal
 jruby/test_vietnamese_charset
 jruby/test_win32
 jruby/test_zlib
 
 # these tests are last because they pull in libraries that can affect others
 jruby/test_loading_builtin_libraries
 jruby/test_null_channel
 jruby/test_irubyobject_java_passing
 jruby/test_jruby_object_input_stream
 jruby/test_jar_on_load_path
 jruby/test_jruby_core_ext
 jruby/test_thread_context_frame_dereferences_unreachable_variables
 jruby/test_context_classloader
 # fails with -j-ea on java :(
 # jruby/test_uri_classloader
 jruby/test_rexml_document
 jruby/test_openssl_stub
 jruby/test_missing_jruby_home
 jruby/test_ast_inspector
 jruby/test_jarred_gems_with_spaces_in_directory
 jruby/test_kernel
 jruby/test_dir_with_plusses
 jruby/test_jar_file
 # Not sure how to test this, since soft references take too long to collect
 #test_thread_service
 jruby/test_jruby_synchronized
 jruby/test_instantiating_interfaces
 jruby/test_openssl
 jruby/test_tempfile_cleanup
 
 # Disabled until the new JIT/AOT is more complete
-#jruby/compiler/test_jrubyc
-#jruby/test_load_compiled_ruby
-#jruby/test_load_compiled_ruby_class_from_classpath
+jruby/compiler/test_jrubyc
+jruby/test_load_compiled_ruby
+jruby/test_load_compiled_ruby_class_from_classpath
 
diff --git a/test/compiler/test_jrubyc.rb b/test/jruby/compiler/test_jrubyc.rb
similarity index 87%
rename from test/compiler/test_jrubyc.rb
rename to test/jruby/compiler/test_jrubyc.rb
index a02d5caadf..e7fee52631 100644
--- a/test/compiler/test_jrubyc.rb
+++ b/test/jruby/compiler/test_jrubyc.rb
@@ -1,121 +1,118 @@
 require 'test/unit'
 require 'stringio'
 require 'tempfile'
 require 'fileutils'
 require 'java'
 require 'jruby/jrubyc'
 
 class TestJrubyc < Test::Unit::TestCase
   def setup
     @tempfile_stdout = Tempfile.open("test_jrubyc_stdout")
     @old_stdout = $stdout.dup
     $stdout.reopen @tempfile_stdout
     $stdout.sync = true
 
     @tempfile_stderr = Tempfile.open("test_jrubyc_stderr")
     @old_stderr = $stderr.dup
     $stderr.reopen @tempfile_stderr
     $stderr.sync = true
   end
 
   def teardown
     FileUtils.rm_rf(["foo", "ruby"])
     $stdout.reopen(@old_stdout)
     $stderr.reopen(@old_stderr)
   end
 
 =begin Neither of these tests seem to work running under rake. FIXME
   def test_basic
     begin
       JRuby::Compiler::compile_argv(["--verbose", __FILE__])
       output = File.read(@tempfile_stdout.path)
 
       assert_equal(
         "Compiling #{__FILE__}\n",
         output)
 
       class_file = __FILE__.gsub('.rb', '.class')
 
       assert(File.exist?(class_file))
     ensure
       File.delete(class_file) rescue nil
     end
   end
 
   def test_target
     tempdir = File.dirname(@tempfile_stdout.path)
     JRuby::Compiler::compile_argv(["--verbose", "-t", tempdir, __FILE__])
     output = File.read(@tempfile_stdout.path)
 
     assert_equal(
       "Compiling #{__FILE__}\n",
       output)
 
     assert(File.exist?(tempdir + "/test/compiler/test_jrubyc.class"))
     FileUtils.rm_rf(tempdir + "/test/compiler/test_jrubyc.class")
   end
 =end
 
   def test_bad_target
     begin
       JRuby::Compiler::compile_argv(["--verbose", "-t", "does_not_exist", __FILE__])
     rescue Exception => e
     end
 
     assert(e)
     assert_equal(
       "Target dir not found: does_not_exist",
       e.message)
   end
 
   def test_require
     $compile_test = false
     File.open("test_file1.rb", "w") {|file| file.write("$compile_test = true")}
 
     JRuby::Compiler::compile_argv(["--verbose", "test_file1.rb"])
     output = File.read(@tempfile_stdout.path)
 
     assert_equal(
       "Compiling test_file1.rb\n",
       output)
 
     assert_nothing_raised { require 'test_file1' }
     assert($compile_test)
   ensure
     File.delete("test_file1.rb") rescue nil
     File.delete("test_file1.class") rescue nil
   end
 
   def test_signature_with_arg_named_result
     if RbConfig::CONFIG['bindir'].match( /!\//) || RbConfig::CONFIG['bindir'].match( /:\//)
       skip( 'only filesystem installations of jruby can compile ruby to java' ) 
     end
     $compile_test = false
     File.open("test_file2.rb", "w") {|file| file.write(<<-RUBY
       class C
         java_signature 'public int f(int result)'
         def f(arg)
           $compile_test = true
         end
       end
 
       C.new.f(0)
     RUBY
     )}
 
     JRuby::Compiler::compile_argv(["--verbose", "--java", "--javac", "test_file2.rb"])
     output = File.read(@tempfile_stderr.path)
 
-    # Truffle is showing a warn message when run on jdk 8
-    # This is a dirty hack to make the CI green again.
-    expected_result = java.lang.System.getProperty("java.version").split(".")[1] == "7" ? "" : "warning: Supported source version 'RELEASE_7' from annotation processor 'com.oracle.truffle.dsl.processor.TruffleProcessor' less than -source '1.8'\n1 warning\n"
-    assert_equal(expected_result, output)
+    assert_equal("", output)
 
     assert_nothing_raised { require 'test_file2' }
     assert($compile_test)
   ensure
     File.delete("test_file2.rb") rescue nil
     File.delete("C.java") rescue nil
     File.delete("C.class") rescue nil
   end
 end
diff --git a/test/jruby/test_load_compiled_ruby_class_from_classpath.rb b/test/jruby/test_load_compiled_ruby_class_from_classpath.rb
index 2ad854947b..7d88fd7687 100644
--- a/test/jruby/test_load_compiled_ruby_class_from_classpath.rb
+++ b/test/jruby/test_load_compiled_ruby_class_from_classpath.rb
@@ -1,117 +1,117 @@
 require "test/unit"
 require "fileutils"
 require 'test/jruby/test_helper'
 require 'rbconfig'
 
 # Necessary because of http://jira.codehaus.org/browse/JRUBY-1579
 require "jruby"
 
 require 'jruby/compiler'
 
 class LoadCompiledRubyClassFromClasspathTest < Test::Unit::TestCase
   include TestHelper
 
   RubyName = "runner"
   RubySource = "#{RubyName}.rb"
   RubyClass = "#{RubyName}.class"
   JarFile = "#{RubyName}_in_a_jar.jar"
   StarterName = "Starter"
   StarterSource = "#{StarterName}.java"
   StarterClass = "#{StarterName}.class"
   Manifest = "manifest.txt"
 
   if java.lang.System.getProperty("basedir") # FIXME: disabling this test under maven
     def test_truth; end
   else
   def setup
     remove_test_artifacts
     @original_classpath = ENV["CLASSPATH"]
 
     # This line means we assume the test is running from the jruby root directory
     @jruby_home = Dir.pwd
     @in_process = JRuby.runtime.instance_config.run_ruby_in_process
     JRuby.runtime.instance_config.run_ruby_in_process = false
   end
 
   def teardown
     remove_test_artifacts
     ENV["CLASSPATH"] = @original_classpath
     JRuby.runtime.instance_config.run_ruby_in_process = @in_process
   end
 
   def test_loading_compiled_ruby_class_from_classpath
     create_compiled_class
 
     append_to_classpath @jruby_home
     result = nil
 
     FileUtils.cd("..") do
-      result = jruby("-r#{RubyName} -e '1'")
+      result = jruby("-Xaot.loadClasses=true -r#{RubyName} -e '1'")
     end
     assert_equal 0, $?.exitstatus, "did not get 0 for exit status from running jruby against the class"
     assert_equal "hello from runner", result, "wrong text from runner"
   end
 
   def test_loading_compiled_ruby_class_from_jar
     return if RbConfig::CONFIG['host_os'] == "SunOS"
     create_compiled_class
 
     append_to_classpath jruby_jar
     File.open(StarterSource, "w") do |f|
       f.puts <<-EOS
 public class #{StarterName} {
   public static void main(String args[]) throws Exception {
      org.jruby.Main.main(new String[] { "-r#{RubyName}", "-e", "" });
   }
 }
       EOS
     end
     File.open(Manifest, "w") do |f|
       f.puts "Main-Class: #{StarterName}"
       f.puts "Class-Path: #{jruby_jar}"
     end
 
     javac = ENV['JAVA_HOME'] ? "#{ENV['JAVA_HOME']}/bin/javac" : "javac"
 
     `#{javac} -cp #{jruby_jar} #{StarterSource}`
     assert_equal 0, $?.exitstatus, "javac failed to compile #{StarterSource}"
     `jar cvfm #{JarFile} #{Manifest} #{StarterClass} #{RubyClass}`
     assert_equal 0, $?.exitstatus, "jar failed to build #{JarFile} from #{RubyClass}"
 
     remove_ruby_source_files
 
-    result = `java -jar #{JarFile}`
+    result = `java -jar -Djruby.aot.loadClasses=true #{JarFile}`
     assert_equal 0, $?.exitstatus, "did not get 0 for exit status from running java against the jar"
     assert_equal "hello from runner", result, "wrong text from runner"
   end
 
   private
   def remove_ruby_source_files
     FileUtils.rm_rf [RubySource, RubyClass]
   end
 
   def remove_test_artifacts
     remove_ruby_source_files
     FileUtils.rm_rf [JarFile, StarterSource, StarterClass, Manifest]
   end
 
   def create_compiled_class
     File.open(RubySource, "w") { |f| f << "print 'hello from runner'" }
     JRuby::Compiler::compile_argv([RubySource])
   rescue Exception => e
     raise "jrubyc failed to compile #{RubySource} into #{RubyClass}:\n#{e.message}\n#{e.backtrace}"
   ensure
     # just in case, remove the rb file
     FileUtils.rm_rf RubySource
   end
 
   def jruby_jar
     "lib/jruby.jar"
   end
 
   def append_to_classpath(*paths)
     current_classpath = ENV["CLASSPATH"].nil? ? "" : ENV["CLASSPATH"]
     ENV["CLASSPATH"] = "#{current_classpath}#{File::PATH_SEPARATOR}#{paths.join(File::PATH_SEPARATOR)}"
   end
   end # end FIXME
 end
