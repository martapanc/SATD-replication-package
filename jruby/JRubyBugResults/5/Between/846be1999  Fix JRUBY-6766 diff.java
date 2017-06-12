diff --git a/src/org/jruby/BasicObjectStub.java b/src/org/jruby/BasicObjectStub.java
index 9bf8d7bf47..5cb2feb896 100644
--- a/src/org/jruby/BasicObjectStub.java
+++ b/src/org/jruby/BasicObjectStub.java
@@ -1,424 +1,424 @@
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
  * Copyright (C) 2009 Charles O Nutter <headius@headius.com>
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
 
 import java.util.Collections;
 import java.util.List;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.InstanceVariables;
 import org.jruby.runtime.builtin.InternalVariables;
 import org.jruby.runtime.builtin.RubyJavaObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.util.TypeConverter;
 
 public final class BasicObjectStub {
     public static IRubyObject callSuper(IRubyObject self, ThreadContext context, IRubyObject[] args, Block block) {
         return RuntimeHelpers.invokeSuper(context, self, args, block);
     }
 
     public static IRubyObject callMethod(IRubyObject self, ThreadContext context, String name) {
         return RuntimeHelpers.invoke(context, self, name);
     }
 
     public static IRubyObject callMethod(IRubyObject self, ThreadContext context, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, self, name, arg);
     }
 
     public static IRubyObject callMethod(IRubyObject self, ThreadContext context, String name, IRubyObject[] args) {
         return RuntimeHelpers.invoke(context, self, name, args);
     }
 
     public static IRubyObject callMethod(IRubyObject self, ThreadContext context, String name, IRubyObject[] args, Block block) {
         return RuntimeHelpers.invoke(context, self, name, args, block);
     }
 
     public static IRubyObject callMethod(IRubyObject self, ThreadContext context, int methodIndex, String name) {
         return RuntimeHelpers.invoke(context, self, name);
     }
 
     public static IRubyObject callMethod(IRubyObject self, ThreadContext context, int methodIndex, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, self, name, arg);
     }
 
     public static boolean isNil(IRubyObject self) {
         return false;
     }
 
     public static boolean isTrue(IRubyObject self) {
         return true;
     }
 
     public static boolean isTaint(IRubyObject self) {
         return false;
     }
 
     public static void setTaint(IRubyObject self, boolean b) {
     }
 
     public static IRubyObject infectBy(IRubyObject self, IRubyObject obj) {
         return self;
     }
 
     public static boolean isFrozen(IRubyObject self) {
         return false;
     }
 
     public static void setFrozen(IRubyObject self, boolean b) {
     }
 
     public static boolean isUntrusted(IRubyObject self) {
         return false;
     }
 
     public static void setUntrusted(IRubyObject self, boolean b) {
     }
 
     public static boolean isImmediate(IRubyObject self) {
         return false;
     }
 
     public static RubyClass getMetaClass(IRubyObject self) {
         if (self instanceof RubyBasicObject) {
             return ((RubyBasicObject)self).getMetaClass();
         } else if (self instanceof RubyJavaObject) {
             return ((RubyJavaObject)self).getMetaClass();
         } else {
             throw new RuntimeException("unknown object type in BasicObjectStuff.getMetaClass: " + self.getClass());
         }
     }
 
     public static RubyClass getSingletonClass(IRubyObject self) {
         throw new UnsupportedOperationException("Not supported yet.");
     }
 
     public static RubyClass getType(IRubyObject self) {
         return getMetaClass(self).getRealClass();
     }
 
     public static boolean respondsTo(IRubyObject self, String name) {
-        if(getMetaClass(self).searchMethod("respond_to?") == getRuntime(self).getRespondToMethod()) {
+        if(getMetaClass(self).searchMethod("respond_to?").equals(getRuntime(self).getRespondToMethod())) {
             return getMetaClass(self).isMethodBound(name, false);
         } else {
             return callMethod(self, getRuntime(self).getCurrentContext(), "respond_to?", getRuntime(self).newSymbol(name)).isTrue();
         }
     }
 
     public static Ruby getRuntime(IRubyObject self) {
         return getMetaClass(self).getClassRuntime();
     }
 
     public static Class getJavaClass(IRubyObject self) {
         return self.getClass();
     }
 
     public static String asJavaString(IRubyObject self) {
         IRubyObject asString = checkStringType(self);
         if(!asString.isNil()) return ((RubyString)asString).asJavaString();
         throw getRuntime(self).newTypeError(inspect(self).toString() + " is not a string");
     }
 
     public static RubyString asString(IRubyObject self) {
         IRubyObject str = RuntimeHelpers.invoke(getRuntime(self).getCurrentContext(), self, "to_s");
 
         if (!(str instanceof RubyString)) return (RubyString)anyToString(self);
         if (isTaint(self)) str.setTaint(true);
         return (RubyString) str;
     }
 
     public static RubyArray convertToArray(IRubyObject self) {
         return (RubyArray) TypeConverter.convertToType(self, getRuntime(self).getArray(), "to_ary");
     }
 
     public static RubyHash convertToHash(IRubyObject self) {
         return (RubyHash)TypeConverter.convertToType(self, getRuntime(self).getHash(), "to_hash");
     }
 
     public static RubyFloat convertToFloat(IRubyObject self) {
         return (RubyFloat) TypeConverter.convertToType(self, getRuntime(self).getFloat(), "to_f");
     }
 
     public static RubyInteger convertToInteger(IRubyObject self) {
         return convertToInteger(self, "to_int");
     }
 
     public static RubyInteger convertToInteger(IRubyObject self, int convertMethodIndex, String convertMethod) {
         return convertToInteger(self, convertMethod);
     }
 
     public static RubyInteger convertToInteger(IRubyObject self, String convertMethod) {
         IRubyObject val = TypeConverter.convertToType(self, getRuntime(self).getInteger(), convertMethod, true);
         if (!(val instanceof RubyInteger)) throw getRuntime(self).newTypeError(getMetaClass(self).getName() + "#" + convertMethod + " should return Integer");
         return (RubyInteger)val;
     }
 
     public static RubyString convertToString(IRubyObject self) {
         return (RubyString) TypeConverter.convertToType(self, getRuntime(self).getString(), "to_str");
     }
 
     public static IRubyObject anyToString(IRubyObject self) {
         String cname = getMetaClass(self).getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime(self).newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(self)) + ">");
         str.setTaint(isTaint(self));
         return str;
     }
 
     public static IRubyObject checkStringType(IRubyObject self) {
         IRubyObject str = TypeConverter.convertToTypeWithCheck(self, getRuntime(self).getString(), "to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = RubyString.newEmptyString(getRuntime(self));
         }
         return str;
     }
 
     public static IRubyObject checkArrayType(IRubyObject self) {
         return TypeConverter.convertToTypeWithCheck(self, getRuntime(self).getArray(), "to_ary");
     }
 
     public static Object toJava(IRubyObject self, Class cls) {
         if (cls.isAssignableFrom(self.getClass())) {
             return self;
         } else {
             throw getRuntime(self).newTypeError("could not convert " + self.getClass() + " to " + cls);
         }
     }
 
     public static IRubyObject dup(IRubyObject self) {
         // TODO: java.lang.Object.clone?
         throw new UnsupportedOperationException("Not supported yet.");
     }
 
     public static IRubyObject inspect(IRubyObject self) {
         Ruby runtime = getRuntime(self);
         if (hasVariables(self)) {
             StringBuilder part = new StringBuilder();
             String cname = getMetaClass(self).getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(self)));
 
             if (runtime.isInspecting(self)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append(" ...>");
                 return runtime.newString(part.toString());
             }
             try {
                 runtime.registerInspecting(self);
                 return runtime.newString(inspectObj(self, part).toString());
             } finally {
                 runtime.unregisterInspecting(self);
             }
         }
 
         return RuntimeHelpers.invoke(runtime.getCurrentContext(), self, "to_s");
     }
 
 
     /** inspect_obj
      *
      * The internal helper method that takes care of the part of the
      * inspection that inspects instance variables.
      */
     private static StringBuilder inspectObj(IRubyObject self, StringBuilder part) {
         ThreadContext context = getRuntime(self).getCurrentContext();
         String sep = "";
 
         for (Variable<IRubyObject> ivar : getInstanceVariables(self).getInstanceVariableList()) {
             part.append(sep).append(" ").append(ivar.getName()).append("=");
             part.append(ivar.getValue().callMethod(context, "inspect"));
             sep = ",";
         }
         part.append(">");
         return part;
     }
 
     public static IRubyObject rbClone(IRubyObject self) {
         // TODO: java.lang.Object.clone?
         throw new UnsupportedOperationException("Not supported yet.");
     }
 
     public static boolean isModule(IRubyObject self) {
         return false;
     }
 
     public static boolean isClass(IRubyObject self) {
         return false;
     }
 
     public static void dataWrapStruct(IRubyObject self, Object obj) {
     }
 
     public static Object dataGetStruct(IRubyObject self) {
         return null;
     }
 
     public static Object dataGetStructChecked(IRubyObject self) {
         return null;
     }
 
     public static IRubyObject id(IRubyObject self) {
         return getRuntime(self).newFixnum(System.identityHashCode(self));
     }
 
     public static IRubyObject op_equal(IRubyObject self, ThreadContext context, IRubyObject other) {
         return getRuntime(self).newBoolean(self == other);
     }
 
     public static IRubyObject op_eqq(IRubyObject self, ThreadContext context, IRubyObject other) {
         return getRuntime(self).newBoolean(self == other);
     }
 
     public static boolean eql(IRubyObject self, IRubyObject other) {
         return self == other;
     }
 
     public static void addFinalizer(IRubyObject self, IRubyObject finalizer) {
     }
 
     public static void removeFinalizers(IRubyObject self) {
     }
 
     public static boolean hasVariables(IRubyObject self) {
         return false;
     }
 
     public static int getVariableCount(IRubyObject self) {
         return 0;
     }
 
     public static void syncVariables(IRubyObject self, List<Variable<Object>> variables) {
     }
 
     public static List<Variable<Object>> getVariableList(IRubyObject self) {
         return Collections.EMPTY_LIST;
     }
 
     @SuppressWarnings("deprecation")
     public static class DummyInstanceVariables implements InstanceVariables {
         private final IRubyObject nil;
 
         public DummyInstanceVariables(IRubyObject nil) {
             this.nil = nil;
         }
 
         public boolean hasInstanceVariable(String name) {
             return false;
         }
 
         public boolean fastHasInstanceVariable(String internedName) {
             return false;
         }
 
         public IRubyObject getInstanceVariable(String name) {
             return nil;
         }
 
         public IRubyObject fastGetInstanceVariable(String internedName) {
             return nil;
         }
 
         public IRubyObject setInstanceVariable(String name, IRubyObject value) {
             return value;
         }
 
         public IRubyObject fastSetInstanceVariable(String internedName, IRubyObject value) {
             return value;
         }
 
         public IRubyObject removeInstanceVariable(String name) {
             return nil;
         }
 
         public List<Variable<IRubyObject>> getInstanceVariableList() {
         return Collections.EMPTY_LIST;
         }
 
         public List<String> getInstanceVariableNameList() {
         return Collections.EMPTY_LIST;
         }
 
         public void copyInstanceVariablesInto(InstanceVariables other) {
         }
 
     }
 
     public static InstanceVariables getInstanceVariables(IRubyObject self) {
         // TODO: cache in runtime?
         return new DummyInstanceVariables(getRuntime(self).getNil());
     }
 
     @SuppressWarnings("deprecation")
     public static class DummyInternalVariables implements InternalVariables {
         public boolean hasInternalVariable(String name) {
             return false;
         }
 
         public boolean fastHasInternalVariable(String internedName) {
             return false;
         }
 
         public Object getInternalVariable(String name) {
             return null;
         }
 
         public Object fastGetInternalVariable(String internedName) {
             return null;
         }
 
         public void setInternalVariable(String name, Object value) {
         }
 
         public void fastSetInternalVariable(String internedName, Object value) {
         }
 
         public Object removeInternalVariable(String name) {
             return null;
         }
     }
     public static final InternalVariables DUMMY_INTERNAL_VARIABLES = new DummyInternalVariables();
 
     public static InternalVariables getInternalVariables(IRubyObject self) {
         return DUMMY_INTERNAL_VARIABLES;
     }
 
     public static List<String> getVariableNameList(IRubyObject self) {
         return Collections.EMPTY_LIST;
     }
 
     public static void copySpecialInstanceVariables(IRubyObject self, IRubyObject clone) {
     }
 
     public static Object getVariable(IRubyObject self, int index) {
         return null;
     }
 
     public static void setVariable(IRubyObject self, int index, Object value) {
     }
 }
\ No newline at end of file
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 9ad7c1c429..d7919d8dee 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -188,2580 +188,2602 @@ public final class Ruby {
      * specified RubyInstanceConfig will be used to determine various JRuby
      * runtime characteristics.
      * 
      * @param config The configuration to use for the new instance
      * @see org.jruby.RubyInstanceConfig
      */
     private Ruby(RubyInstanceConfig config) {
         this.config             = config;
         this.is1_9              = config.getCompatVersion().is1_9();
         this.is2_0              = config.getCompatVersion().is2_0();
         this.doNotReverseLookupEnabled = is1_9;
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
         
         Random myRandom;
         try {
             myRandom = new SecureRandom();
         } catch (Throwable t) {
             LOG.debug("unable to instantiate SecureRandom, falling back on Random", t);
             myRandom = new Random();
         }
         this.random = myRandom;
         this.hashSeed = this.random.nextInt();
         
         this.beanManager.register(new Config(this));
         this.beanManager.register(parserStats);
         this.beanManager.register(new ClassCache(this));
         this.beanManager.register(new org.jruby.management.Runtime(this));
 
         this.runtimeCache = new RuntimeCache();
         runtimeCache.initMethodCache(ClassIndex.MAX_CLASSES * MethodIndex.MAX_METHODS);
         
         constantInvalidator = OptoFactory.newConstantInvalidator();
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
             LOG.debug("warning: could not compile: {}; full trace follows", node.getPosition().getFile());
             LOG.debug(t.getMessage(), t);
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
      * @see org.jruby.Ruby#setSafeLevel
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
      * The safe level is set using $SAFE in Ruby code. It is not supported
      * in JRuby.
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
         
         irManager = new IRManager();
         
         // Initialize the "dummy" class used as a marker
         dummyClass = new RubyClass(this, classClass);
         dummyClass.freeze(tc);
         
         // Create global constants and variables
         RubyGlobal.createGlobals(tc, this);
 
         // Prepare LoadService and load path
         getLoadService().init(config.getLoadPaths());
         
         booting = false;
 
         // initialize builtin libraries
         initBuiltins();
         
         // init Ruby-based kernel
         initRubyKernel();
         
         if(config.isProfiling()) {
+            // additional twiddling for profiled mode
             getLoadService().require("jruby/profiler/shutdown_hook");
+
+            // recache core methods, since they'll have profiling wrappers now
+            kernelModule.invalidateCacheDescendants(); // to avoid already-cached methods
+            RubyKernel.recacheBuiltinMethods(this);
+            RubyBasicObject.recacheBuiltinMethods(this);
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
         addLazyBuiltin("readline.jar", "readline", "org.jruby.ext.ReadlineService");
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
         addLazyBuiltin("jopenssl.jar", "jopenssl", "org.jruby.ext.openssl.OSSLLibrary");
 
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
-
-    public Object getRespondToMethod() {
-        return respondToMethod;
-    }
-
-    public void setRespondToMethod(Object rtm) {
-        this.respondToMethod = rtm;
-    }
     
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
 
+    ///////////////////////////////////////////////////////////////////////////
+    // Cached DynamicMethod objects, used for direct dispatch or for short
+    // circuiting dynamic invocation logic.
+    ///////////////////////////////////////////////////////////////////////////
+
     public DynamicMethod getPrivateMethodMissing() {
         return privateMethodMissing;
     }
+
     public void setPrivateMethodMissing(DynamicMethod method) {
         privateMethodMissing = method;
     }
+
     public DynamicMethod getProtectedMethodMissing() {
         return protectedMethodMissing;
     }
+
     public void setProtectedMethodMissing(DynamicMethod method) {
         protectedMethodMissing = method;
     }
+
     public DynamicMethod getVariableMethodMissing() {
         return variableMethodMissing;
     }
+
     public void setVariableMethodMissing(DynamicMethod method) {
         variableMethodMissing = method;
     }
+
     public DynamicMethod getSuperMethodMissing() {
         return superMethodMissing;
     }
+
     public void setSuperMethodMissing(DynamicMethod method) {
         superMethodMissing = method;
     }
+
     public DynamicMethod getNormalMethodMissing() {
         return normalMethodMissing;
     }
+
     public void setNormalMethodMissing(DynamicMethod method) {
         normalMethodMissing = method;
     }
+
     public DynamicMethod getDefaultMethodMissing() {
         return defaultMethodMissing;
     }
+
     public void setDefaultMethodMissing(DynamicMethod method) {
         defaultMethodMissing = method;
     }
+
+    public DynamicMethod getRespondToMethod() {
+        return respondTo;
+    }
+
+    public void setRespondToMethod(DynamicMethod rtm) {
+        this.respondTo = rtm;
+    }
     
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
         errorStream.print(config.getTraceType().printBacktrace(excp, errorStream == System.err && getPosix().isatty(FileDescriptor.err)));
     }
     
     public void loadFile(String scriptName, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             ThreadContext.pushBacktrace(context, "(root)", file, 0);
             context.preNodeEval(objectClass, self, scriptName);
 
             Node node = parseFile(in, scriptName, null);
             if (wrap) {
                 // toss an anonymous module into the search path
                 ((RootNode)node).getStaticScope().setModule(RubyModule.newModule(this));
             }
             runInterpreter(context, node, self);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             ThreadContext.popBacktrace(context);
         }
     }
     
     public void compileAndLoadFile(String filename, InputStream in, boolean wrap) {
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         InputStream readStream = in;
         
         try {
             secure(4); /* should alter global state */
 
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
                         LOG.info("found jitted code for " + filename + " at class: " + className);
                     }
                     script = (Script)contents.newInstance();
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
             Node scriptNode = parseFile(readStream, filename, null);
             if (script == null) {
                 script = tryCompile(scriptNode, className, new JRubyClassLoader(jrubyClassLoader), false);
             }
 
             if (script == null) {
                 failForcedCompile(scriptNode);
 
                 runInterpreter(scriptNode);
             } else {
                 runScript(script, wrap);
             }
         } catch (JumpException.ReturnJump rj) {
             return;
         }
     }
 
     public void loadScript(Script script) {
         loadScript(script, false);
     }
 
     public void loadScript(Script script, boolean wrap) {
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
             
             script.load(context, self, wrap);
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
 
         try {
             secure(4); /* should alter global state */
 
             context.preExtensionLoad(self);
 
             extension.basicLoad(this);
         } catch (IOException ioe) {
             throw newIOErrorFromException(ioe);
         } catch (JumpException.ReturnJump rj) {
             return;
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
@@ -3310,1178 +3332,1176 @@ public final class Ruby {
 
     public RaiseException newSecurityError(String message) {
         return newRaiseException(getSecurityError(), message);
     }
 
     public RaiseException newSystemCallError(String message) {
         return newRaiseException(getSystemCallError(), message);
     }
 
     public RaiseException newKeyError(String message) {
         return newRaiseException(getKeyError(), message);
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
 
     private final static Pattern ADDR_NOT_AVAIL_PATTERN = Pattern.compile("assign.*address");
 
     public RaiseException newErrnoEADDRFromBindException(BindException be) {
         String msg = be.getMessage();
         if (msg == null) {
             msg = "bind";
         } else {
             msg = "bind - " + msg;
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
 
     public RaiseException newNameError(String message, String name, Throwable origException) {
         return newNameError(message, name, origException, true);
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
             LOG.debug(soe.getMessage(), soe);
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
     private RaiseException newLightweightErrnoException(RubyClass exceptionClass, String message) {
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
 
     public int getFilenoIntMapSize() {
         return filenoIntExtMap.size();
     }
 
     public void removeFilenoIntMap(int internal) {
         filenoIntExtMap.remove(internal);
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
 
     private void recursiveListClear() {
         Map<String, RubyHash> hash = recursive.get();
         if(hash != null) {
             hash.clear();
         }
     }
 
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
 
     /**
      * Perform a recursive walk on the given object using the given function.
      *
      * Do not call this method directly unless you know you're within a call
      * to {@link Ruby#recursiveListOperation(java.util.concurrent.Callable) recursiveListOperation},
      * which will ensure the thread-local recursion tracking data structs are
      * cleared.
      *
      * MRI: rb_exec_recursive
      *
      * Calls func(obj, arg, recursive), where recursive is non-zero if the
      * current method is called recursively on obj
      *
      * @param func
      * @param obj
      * @return
      */
     public IRubyObject execRecursive(RecursiveFunction func, IRubyObject obj) {
         if (!inRecursiveListOperation.get()) {
             throw newThreadError("BUG: execRecursive called outside recursiveListOperation");
         }
         return execRecursiveInternal(func, obj, null, false);
     }
 
     /**
      * Perform a recursive walk on the given object using the given function.
      * Treat this as the outermost call, cleaning up recursive structures.
      *
      * MRI: rb_exec_recursive_outer
      *
      * If recursion is detected on the current method and obj, the outermost
      * func will be called with (obj, arg, Qtrue). All inner func will be
      * short-circuited using throw.
      *
      * @param func
      * @param obj
      * @return
      */
     public IRubyObject execRecursiveOuter(RecursiveFunction func, IRubyObject obj) {
         try {
             return execRecursiveInternal(func, obj, null, true);
         } finally {
             recursiveListClear();
         }
     }
 
     /**
      * Begin a recursive walk that may make one or more calls to
      * {@link Ruby#execRecursive(org.jruby.Ruby.RecursiveFunction, org.jruby.runtime.builtin.IRubyObject) execRecursive}.
      * Clean up recursive structures once complete.
      *
      * @param body
      * @param <T>
      * @return
      */
     public <T extends IRubyObject> T recursiveListOperation(Callable<T> body) {
         try {
             inRecursiveListOperation.set(true);
             return body.call();
         } catch (Exception e) {
             UnsafeFactory.getUnsafe().throwException(e);
             return null; // not reached
         } finally {
             recursiveListClear();
             inRecursiveListOperation.set(false);
         }
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
 
     public boolean is2_0() {
         return is2_0;
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
 
     @Deprecated
     public int getConstantGeneration() {
         return -1;
     }
 
     @Deprecated
     public synchronized void incrementConstantGeneration() {
         constantInvalidator.invalidate();
     }
     
     public Invalidator getConstantInvalidator() {
         return constantInvalidator;
     }
     
     public void invalidateConstants() {
         
     }
 
     public <E extends Enum<E>> void loadConstantSet(RubyModule module, Class<E> enumClass) {
         for (E e : EnumSet.allOf(enumClass)) {
             Constant c = (Constant) e;
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.setConstant(c.name(), newFixnum(c.intValue()));
             }
         }
     }
     public void loadConstantSet(RubyModule module, String constantSetName) {
         for (Constant c : ConstantSet.getConstantSet(constantSetName)) {
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.setConstant(c.name(), newFixnum(c.intValue()));
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
     public synchronized void addProfiledMethod(String name, DynamicMethod method) {
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
     
     /**
      * Increment the count of exceptions generated by code in this runtime.
      */
     public void incrementExceptionCount() {
         exceptionCount.incrementAndGet();
     }
     
     /**
      * Get the current exception count.
      * 
      * @return he current exception count
      */
     public int getExceptionCount() {
         return exceptionCount.get();
     }
     
     /**
      * Increment the count of backtraces generated by code in this runtime.
      */
     public void incrementBacktraceCount() {
         backtraceCount.incrementAndGet();
     }
     
     /**
      * Get the current backtrace count.
      * 
      * @return the current backtrace count
      */
     public int getBacktraceCount() {
         return backtraceCount.get();
     }
     
     /**
      * Increment the count of backtraces generated for warnings in this runtime.
      */
     public void incrementWarningCount() {
         warningCount.incrementAndGet();
     }
     
     /**
      * Get the current backtrace count.
      * 
      * @return the current backtrace count
      */
     public int getWarningCount() {
         return warningCount.get();
     }
 
     /**
      * Increment the count of backtraces generated by code in this runtime.
      */
     public void incrementCallerCount() {
         callerCount.incrementAndGet();
     }
 
     /**
      * Get the current backtrace count.
      *
      * @return the current backtrace count
      */
     public int getCallerCount() {
         return callerCount.get();
     }
     
     /**
      * Whether the Fixnum class has been reopened and modified
      */
     public boolean isFixnumReopened() {
         return fixnumReopened;
     }
     
     /**
      * Set whether the Fixnum class has been reopened and modified
      */
     public void setFixnumReopened(boolean fixnumReopened) {
         this.fixnumReopened = fixnumReopened;
     }
     
     /**
      * Whether the Float class has been reopened and modified
      */
     public boolean isFloatReopened() {
         return floatReopened;
     }
     
     /**
      * Set whether the Float class has been reopened and modified
      */
     public void setFloatReopened(boolean floatReopened) {
         this.floatReopened = floatReopened;
     }
     
     public boolean isBooting() {
         return booting;
     }
     
     public CoverageData getCoverageData() {
         return coverageData;
     }
     
     public Random getRandom() {
         return random;
     }
     
     public int getHashSeed() {
         return hashSeed;
     }
     
     public StaticScopeFactory getStaticScopeFactory() {
         return staticScopeFactory;
     }
 
     public FFI getFFI() {
         return ffi;
     }
 
     public void setFFI(FFI ffi) {
         this.ffi = ffi;
     }
 
     private final Invalidator constantInvalidator;
     private final ThreadService threadService;
     
     private POSIX posix;
 
     private final ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
 
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
     private IRubyObject rootFiber;
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
             invalidByteSequenceError, fiberError, randomClass, keyError;
 
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
-            superMethodMissing, normalMethodMissing, defaultMethodMissing;
+            superMethodMissing, normalMethodMissing, defaultMethodMissing, respondTo;
     
     // record separator var, to speed up io ops that use it
     private GlobalVariable recordSeparatorVar;
 
     // former java.lang.System concepts now internalized for MVM
     private volatile String currentDirectory;
 
     // The "current line" global variable
     private volatile int currentLine = 0;
 
     private volatile IRubyObject argsFile;
 
     private final long startTime = System.currentTimeMillis();
 
     private final RubyInstanceConfig config;
     private final boolean is1_9;
     private final boolean is2_0;
 
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
         if (SafePropertyAccessor.isSecurityProtected("jruby.reflected.handles")) {
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
 
-    private Object respondToMethod;
-
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
     private final Map<String, Map<String, String>> boundMethods = new HashMap();
 
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
     
     // Message for Errno exceptions that will not generate a backtrace
     public static final String ERRNO_BACKTRACE_MESSAGE = "errno backtraces disabled; run with -Xerrno.backtrace=true to enable";
     
     // Count of RaiseExceptions generated by code running in this runtime
     private final AtomicInteger exceptionCount = new AtomicInteger();
     
     // Count of exception backtraces generated by code running in this runtime
     private final AtomicInteger backtraceCount = new AtomicInteger();
     
     // Count of Kernel#caller backtraces generated by code running in this runtime
     private final AtomicInteger callerCount = new AtomicInteger();
 
     // Count of built-in warning backtraces generated by code running in this runtime
     private final AtomicInteger warningCount = new AtomicInteger();
     
     private boolean fixnumReopened, floatReopened;
     
     private volatile boolean booting = true;
     
     private RubyHash envObject;
     
     private final CoverageData coverageData = new CoverageData();
 
     /** The "global" runtime. Set to the first runtime created, normally. */
     private static Ruby globalRuntime;
     
     /** The "thread local" runtime. Set to the global runtime if unset. */
     private static ThreadLocal<Ruby> threadLocalRuntime = new ThreadLocal<Ruby>();
     
     /** The runtime-local random number generator. Uses SecureRandom if permissions allow. */
     private final Random random;
 
     /** The runtime-local seed for hash randomization */
     private int hashSeed;
     
     private final StaticScopeFactory staticScopeFactory;
     
     private IRManager irManager;
 
     // structures and such for recursive operations
     private ThreadLocal<Map<String, RubyHash>> recursive = new ThreadLocal<Map<String, RubyHash>>();
     private RubySymbol recursiveKey;
     private ThreadLocal<Boolean> inRecursiveListOperation = new ThreadLocal<Boolean>();
 
     private FFI ffi;
 }
diff --git a/src/org/jruby/RubyBasicObject.java b/src/org/jruby/RubyBasicObject.java
index 3d225526c6..5792fa804b 100644
--- a/src/org/jruby/RubyBasicObject.java
+++ b/src/org/jruby/RubyBasicObject.java
@@ -1,1581 +1,1589 @@
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
 package org.jruby;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.security.AccessControlException;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.atomic.AtomicBoolean;
 import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.InstanceVariables;
 import org.jruby.runtime.builtin.InternalVariables;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.CoreObjectType;
 import org.jruby.util.IdUtil;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 
 import static org.jruby.javasupport.util.RuntimeHelpers.invokedynamic;
 import static org.jruby.runtime.MethodIndex.OP_EQUAL;
 import static org.jruby.runtime.MethodIndex.OP_CMP;
 import static org.jruby.runtime.MethodIndex.EQL;
 
 /**
  * RubyBasicObject is the only implementation of the
  * {@link org.jruby.runtime.builtin.IRubyObject}. Every Ruby object in JRuby
  * is represented by something that is an instance of RubyBasicObject. In
  * the core class implementations, this means doing a subclass
  * that extends RubyBasicObject. In other cases it means using a simple
  * RubyBasicObject instance and its data fields to store specific
  * information about the Ruby object.
  *
  * Some care has been taken to make the implementation be as
  * monomorphic as possible, so that the Java Hotspot engine can
  * improve performance of it. That is the reason for several patterns
  * that might seem odd in this class.
  *
  * The IRubyObject interface used to have lots of methods for
  * different things, but these have now mostly been refactored into
  * several interfaces that gives access to that specific part of the
  * object. This gives us the possibility to switch out that subsystem
  * without changing interfaces again. For example, instance variable
  * and internal variables are handled this way, but the implementation
  * in RubyObject only returns "this" in {@link #getInstanceVariables()} and
  * {@link #getInternalVariables()}.
  * 
  * Methods that are implemented here, such as "initialize" should be implemented
  * with care; reification of Ruby classes into Java classes can produce
  * conflicting method names in rare cases. See JRUBY-5906 for an example.
  */
 public class RubyBasicObject implements Cloneable, IRubyObject, Serializable, Comparable<IRubyObject>, CoreObjectType, InstanceVariables, InternalVariables {
 
     private static final Logger LOG = LoggerFactory.getLogger("RubyBasicObject");
 
     private static final boolean DEBUG = false;
     
     // The class of this object
     protected transient RubyClass metaClass;
 
     // zeroed by jvm
     protected int flags;
 
     // variable table, lazily allocated as needed (if needed)
     private transient volatile Object[] varTable;
 
     /**
      * The error message used when some one tries to modify an
      * instance variable in a high security setting.
      */
     protected static final String ERR_INSECURE_SET_INST_VAR  = "Insecure: can't modify instance variable";
 
     public static final int ALL_F = -1;
     public static final int FALSE_F = 1 << 0;
     /**
      * This flag is a bit funny. It's used to denote that this value
      * is nil. It's a bit counterintuitive for a Java programmer to
      * not use subclassing to handle this case, since we have a
      * RubyNil subclass anyway. Well, the reason for it being a flag
      * is that the {@link #isNil()} method is called extremely often. So often
      * that it gives a good speed boost to make it monomorphic and
      * final. It turns out using a flag for this actually gives us
      * better performance than having a polymorphic {@link #isNil()} method.
      */
     public static final int NIL_F = 1 << 1;
     public static final int FROZEN_F = 1 << 2;
     public static final int TAINTED_F = 1 << 3;
     public static final int UNTRUSTED_F = 1 << 4;
 
     public static final int FL_USHIFT = 5;
 
     public static final int USER0_F = (1<<(FL_USHIFT+0));
     public static final int USER1_F = (1<<(FL_USHIFT+1));
     public static final int USER2_F = (1<<(FL_USHIFT+2));
     public static final int USER3_F = (1<<(FL_USHIFT+3));
     public static final int USER4_F = (1<<(FL_USHIFT+4));
     public static final int USER5_F = (1<<(FL_USHIFT+5));
     public static final int USER6_F = (1<<(FL_USHIFT+6));
     public static final int USER7_F = (1<<(FL_USHIFT+7));
     public static final int USER8_F = (1<<(FL_USHIFT+8));
 
     public static final int COMPARE_BY_IDENTITY_F = USER8_F;
 
     /**
      *  A value that is used as a null sentinel in among other places
      *  the RubyArray implementation. It will cause large problems to
      *  call any methods on this object.
      */
     public static final IRubyObject NEVER = new RubyBasicObject();
 
     /**
      * A value that specifies an undefined value. This value is used
      * as a sentinel for undefined constant values, and other places
      * where neither null nor NEVER makes sense.
      */
     public static final IRubyObject UNDEF = new RubyBasicObject();
 
     /**
      * It's not valid to create a totally empty RubyObject. Since the
      * RubyObject is always defined in relation to a runtime, that
      * means that creating RubyObjects from outside the class might
      * cause problems.
      */
     private RubyBasicObject(){};
 
     /**
      * Default allocator instance for all Ruby objects. The only
      * reason to not use this allocator is if you actually need to
      * have all instances of something be a subclass of RubyObject.
      *
      * @see org.jruby.runtime.ObjectAllocator
      */
     public static final ObjectAllocator BASICOBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyBasicObject(runtime, klass);
         }
     };
 
     /**
      * Will create the Ruby class Object in the runtime
      * specified. This method needs to take the actual class as an
      * argument because of the Object class' central part in runtime
      * initialization.
      */
     public static RubyClass createBasicObjectClass(Ruby runtime, RubyClass objectClass) {
         objectClass.index = ClassIndex.OBJECT;
 
         objectClass.defineAnnotatedMethods(RubyBasicObject.class);
 
-        runtime.setDefaultMethodMissing(objectClass.searchMethod("method_missing"));
+        recacheBuiltinMethods(runtime);
 
         return objectClass;
     }
 
+    static void recacheBuiltinMethods(Ruby runtime) {
+        RubyModule objectClass = runtime.getBasicObject();
+
+        if (runtime.is1_9()) { // method_missing is in Kernel in 1.9
+            runtime.setDefaultMethodMissing(objectClass.searchMethod("method_missing"));
+        }
+    }
+
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context) {
         return context.nil;
     }
 
     /**
      * Standard path for object creation. Objects are entered into ObjectSpace
      * only if ObjectSpace is enabled.
      */
     public RubyBasicObject(Ruby runtime, RubyClass metaClass) {
         assert metaClass != null: "NULL Metaclass!!?!?!";
 
         this.metaClass = metaClass;
 
         if (runtime.isObjectSpaceEnabled()) addToObjectSpace(runtime);
     }
 
     /**
      * Path for objects that don't taint and don't enter objectspace.
      */
     public RubyBasicObject(RubyClass metaClass) {
         assert metaClass != null: "NULL Metaclass!!?!?!";
 
         this.metaClass = metaClass;
     }
 
     /**
      * Path for objects who want to decide whether they don't want to be in
      * ObjectSpace even when it is on. (notably used by objects being
      * considered immediate, they'll always pass false here)
      */
     protected RubyBasicObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         this.metaClass = metaClass;
 
         if (useObjectSpace) addToObjectSpace(runtime);
     }
 
     private void addToObjectSpace(Ruby runtime) {
         assert runtime.isObjectSpaceEnabled();
         runtime.getObjectSpace().add(this);
     }
 
     protected void taint(Ruby runtime) {
         runtime.secure(4);
         if (!isTaint()) {
         	testFrozen();
             setTaint(true);
         }
     }
 
     /** rb_frozen_class_p
      *
      * Helper to test whether this object is frozen, and if it is will
      * throw an exception based on the message.
      */
    protected final void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message);
        }
    }
 
     /** rb_frozen_class_p
      *
      * Helper to test whether this object is frozen, and if it is will
      * throw an exception based on the message.
      */
    protected final void testFrozen() {
        if (isFrozen()) {
            throw getRuntime().newFrozenError("object");
        }
    }
 
     /**
      * Sets or unsets a flag on this object. The only flags that are
      * guaranteed to be valid to use as the first argument is:
      *
      * <ul>
      *  <li>{@link #FALSE_F}</li>
      *  <li>{@link #NIL_F}</li>
      *  <li>{@link #FROZEN_F}</li>
      *  <li>{@link #TAINTED_F}</li>
      *  <li>{@link #USER0_F}</li>
      *  <li>{@link #USER1_F}</li>
      *  <li>{@link #USER2_F}</li>
      *  <li>{@link #USER3_F}</li>
      *  <li>{@link #USER4_F}</li>
      *  <li>{@link #USER5_F}</li>
      *  <li>{@link #USER6_F}</li>
      *  <li>{@link #USER7_F}</li>
      * </ul>
      *
      * @param flag the actual flag to set or unset.
      * @param set if true, the flag will be set, if false, the flag will be unset.
      */
     public final void setFlag(int flag, boolean set) {
         if (set) {
             flags |= flag;
         } else {
             flags &= ~flag;
         }
     }
 
     /**
      * Get the value of a custom flag on this object. The only
      * guaranteed flags that can be sent in to this method is:
      *
      * <ul>
      *  <li>{@link #FALSE_F}</li>
      *  <li>{@link #NIL_F}</li>
      *  <li>{@link #FROZEN_F}</li>
      *  <li>{@link #TAINTED_F}</li>
      *  <li>{@link #USER0_F}</li>
      *  <li>{@link #USER1_F}</li>
      *  <li>{@link #USER2_F}</li>
      *  <li>{@link #USER3_F}</li>
      *  <li>{@link #USER4_F}</li>
      *  <li>{@link #USER5_F}</li>
      *  <li>{@link #USER6_F}</li>
      *  <li>{@link #USER7_F}</li>
      * </ul>
      *
      * @param flag the flag to get
      * @return true if the flag is set, false otherwise
      */
     public final boolean getFlag(int flag) {
         return (flags & flag) != 0;
     }
 
     /**
      * Will invoke a named method with no arguments and no block if that method or a custom
      * method missing exists. Otherwise returns null. 1.9: rb_check_funcall
      */
     public final IRubyObject checkCallMethod(ThreadContext context, String name) {
         return RuntimeHelpers.invokeChecked(context, this, name);
     }
 
     /**
      * Will invoke a named method with no arguments and no block.
      */
     public final IRubyObject callMethod(ThreadContext context, String name) {
         return RuntimeHelpers.invoke(context, this, name);
     }
 
     /**
      * Will invoke a named method with one argument and no block with
      * functional invocation.
      */
      public final IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, this, name, arg);
     }
 
     /**
      * Will invoke a named method with the supplied arguments and no
      * block with functional invocation.
      */
     public final IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return RuntimeHelpers.invoke(context, this, name, args);
     }
 
     public final IRubyObject callMethod(String name, IRubyObject... args) {
         return RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, name, args);
     }
 
     public final IRubyObject callMethod(String name) {
         return RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, name);
     }
 
     /**
      * Will invoke a named method with the supplied arguments and
      * supplied block with functional invocation.
      */
     public final IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return RuntimeHelpers.invoke(context, this, name, args, block);
     }
 
     /**
      * Does this object represent nil? See the docs for the {@link
      * #NIL_F} flag for more information.
      */
     public final boolean isNil() {
         return (flags & NIL_F) != 0;
     }
 
     /**
      * Is this value a true value or not? Based on the {@link #FALSE_F} flag.
      */
     public final boolean isTrue() {
         return (flags & FALSE_F) == 0;
     }
 
     /**
      * Is this value a false value or not? Based on the {@link #FALSE_F} flag.
      */
     public final boolean isFalse() {
         return (flags & FALSE_F) != 0;
     }
 
     /**
      * Gets the taint. Shortcut for getFlag(TAINTED_F).
      *
      * @return true if this object is tainted
      */
     public boolean isTaint() {
         return (flags & TAINTED_F) != 0;
     }
 
     /**
      * Sets the taint flag. Shortcut for setFlag(TAINTED_F, taint)
      *
      * @param taint should this object be tainted or not?
      */
     public void setTaint(boolean taint) {
         // JRUBY-4113: callers should not call setTaint on immediate objects
         if (isImmediate()) return;
         
         if (taint) {
             flags |= TAINTED_F;
         } else {
             flags &= ~TAINTED_F;
         }
     }
 
 
     /** OBJ_INFECT
      *
      * Infects this object with traits from the argument obj. In real
      * terms this currently means that if obj is tainted, this object
      * will get tainted too. It's possible to hijack this method to do
      * other infections if that would be interesting.
      */
     public IRubyObject infectBy(IRubyObject obj) {
         if (obj.isTaint()) setTaint(true);
         if (obj.isUntrusted()) setUntrusted(true);
         return this;
     }
 
     final RubyBasicObject infectBy(RubyBasicObject obj) {
         flags |= (obj.flags & (TAINTED_F | UNTRUSTED_F));
         return this;
     }
 
     final RubyBasicObject infectBy(int tuFlags) {
         flags |= (tuFlags & (TAINTED_F | UNTRUSTED_F));
         return this;
     }
 
     /**
      * Is this value frozen or not? Shortcut for doing
      * getFlag(FROZEN_F).
      *
      * @return true if this object is frozen, false otherwise
      */
     public boolean isFrozen() {
         return (flags & FROZEN_F) != 0;
     }
 
     /**
      * Sets whether this object is frozen or not. Shortcut for doing
      * setFlag(FROZEN_F, frozen).
      *
      * @param frozen should this object be frozen?
      */
     public void setFrozen(boolean frozen) {
         if (frozen) {
             flags |= FROZEN_F;
         } else {
             flags &= ~FROZEN_F;
         }
     }
 
 
     /**
      * Is this value untrusted or not? Shortcut for doing
      * getFlag(UNTRUSTED_F).
      *
      * @return true if this object is frozen, false otherwise
      */
     public boolean isUntrusted() {
         return (flags & UNTRUSTED_F) != 0;
     }
 
     /**
      * Sets whether this object is untrusted or not. Shortcut for doing
      * setFlag(UNTRUSTED_F, untrusted).
      *
      * @param untrusted should this object be frozen?
      */
     public void setUntrusted(boolean untrusted) {
         if (untrusted) {
             flags |= UNTRUSTED_F;
         } else {
             flags &= ~UNTRUSTED_F;
         }
     }
 
     /**
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public final RubyClass getMetaClass() {
         return metaClass;
     }
 
     /** rb_singleton_class
      *
      * Note: this method is specialized for RubyFixnum, RubySymbol,
      * RubyNil and RubyBoolean
      *
      * Will either return the existing singleton class for this
      * object, or create a new one and return that.
      */
     public RubyClass getSingletonClass() {
         RubyClass klass;
 
         if (getMetaClass().isSingleton() && ((MetaClass)getMetaClass()).getAttached() == this) {
             klass = getMetaClass();
         } else {
             klass = makeMetaClass(getMetaClass());
         }
 
         klass.setTaint(isTaint());
         if (isFrozen()) klass.setFrozen(true);
 
         return klass;
     }
 
     /** rb_make_metaclass
      *
      * Will create a new meta class, insert this in the chain of
      * classes for this specific object, and return the generated meta
      * class.
      */
     public RubyClass makeMetaClass(RubyClass superClass) {
         MetaClass klass = new MetaClass(getRuntime(), superClass, this); // rb_class_boot
         setMetaClass(klass);
 
         klass.setMetaClass(superClass.getRealClass().getMetaClass());
 
         superClass.addSubclass(klass);
 
         return klass;
     }
 
     /**
      * Makes it possible to change the metaclass of an object. In
      * practice, this is a simple version of Smalltalks Become, except
      * that it doesn't work when we're dealing with subclasses. In
      * practice it's used to change the singleton/meta class used,
      * without changing the "real" inheritance chain.
      */
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return getMetaClass().getRealClass();
     }
 
     /**
      * Does this object respond to the specified message? Uses a
      * shortcut if it can be proved that respond_to? haven't been
      * overridden.
      */
     public final boolean respondsTo(String name) {
         DynamicMethod method = getMetaClass().searchMethod("respond_to?");
-        if(method == getRuntime().getRespondToMethod()) {
+        if(method.equals(getRuntime().getRespondToMethod())) {
             // fastest path; builtin respond_to? which just does isMethodBound
             return getMetaClass().isMethodBound(name, false);
         } else if (!method.isUndefined()) {
             // medium path, invoke user's respond_to? if defined
             return method.call(getRuntime().getCurrentContext(), this, metaClass, "respond_to?", getRuntime().newSymbol(name)).isTrue();
         } else {
             // slowest path, full callMethod to hit method_missing if present, or produce error
             return callMethod(getRuntime().getCurrentContext(), "respond_to?", getRuntime().newSymbol(name)).isTrue();
         }
     }
 
     /**
      * Does this object respond to the specified message via "method_missing?"
      */
     public final boolean respondsToMissing(String name) {
         return respondsToMissing(name, true);
     }
 
     /**
      * Does this object respond to the specified message via "method_missing?"
      */
     public final boolean respondsToMissing(String name, boolean priv) {
         DynamicMethod method = getMetaClass().searchMethod("respond_to_missing?");
         // perhaps should try a smart version as for respondsTo above?
         if(method.isUndefined()) {
             return false;
         } else {
             return method.call(
                     getRuntime().getCurrentContext(),
                     this,
                     metaClass,
                     "respond_to_missing?",
                     getRuntime().newSymbol(name),
                     getRuntime().newBoolean(priv)).isTrue();
         }
     }
 
     /**
      * Will return the runtime that this object is associated with.
      *
      * @return current runtime
      */
     public final Ruby getRuntime() {
         return getMetaClass().getClassRuntime();
     }
 
     /**
      * Will return the Java interface that most closely can represent
      * this object, when working through JAva integration
      * translations.
      */
     public Class getJavaClass() {
         Object obj = dataGetStruct();
         if (obj instanceof JavaObject) {
             return ((JavaObject)obj).getValue().getClass();
         }
         return getClass();
     }
 
     /** rb_to_id
      *
      * Will try to convert this object to a String using the Ruby
      * "to_str" if the object isn't already a String. If this still
      * doesn't work, will throw a Ruby TypeError.
      *
      */
     public String asJavaString() {
         IRubyObject asString = checkStringType();
         if(!asString.isNil()) return ((RubyString)asString).asJavaString();
         throw getRuntime().newTypeError(inspect().toString() + " is not a string");
     }
 
     /** rb_obj_as_string
      *
      * First converts this object into a String using the "to_s"
      * method, infects it with the current taint and returns it. If
      * to_s doesn't return a Ruby String, {@link #anyToString} is used
      * instead.
      */
     public RubyString asString() {
         IRubyObject str = RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "to_s");
 
         if (!(str instanceof RubyString)) return (RubyString)anyToString();
         if (isTaint()) str.setTaint(true);
         return (RubyString) str;
     }
 
  /**
      * Tries to convert this object to a Ruby Array using the "to_ary"
      * method.
      */
     public RubyArray convertToArray() {
         return (RubyArray) TypeConverter.convertToType(this, getRuntime().getArray(), "to_ary");
     }
 
     /**
      * Tries to convert this object to a Ruby Hash using the "to_hash"
      * method.
      */
     public RubyHash convertToHash() {
         return (RubyHash)TypeConverter.convertToType(this, getRuntime().getHash(), "to_hash");
     }
 
     /**
      * Tries to convert this object to a Ruby Float using the "to_f"
      * method.
      */
     public RubyFloat convertToFloat() {
         return (RubyFloat) TypeConverter.convertToType(this, getRuntime().getFloat(), "to_f");
     }
 
     /**
      * Tries to convert this object to a Ruby Integer using the "to_int"
      * method.
      */
     public RubyInteger convertToInteger() {
         return convertToInteger("to_int");
     }
 
     /**
      * Tries to convert this object to a Ruby Integer using the
      * supplied conversion method.
      */
     public RubyInteger convertToInteger(String convertMethod) {
         IRubyObject val = TypeConverter.convertToType(this, getRuntime().getInteger(), convertMethod, true);
         if (!(val instanceof RubyInteger)) throw getRuntime().newTypeError(getMetaClass().getName() + "#" + convertMethod + " should return Integer");
         return (RubyInteger)val;
     }
 
     /**
      * Tries to convert this object to a Ruby String using the
      * "to_str" method.
      */
     public RubyString convertToString() {
         return (RubyString) TypeConverter.convertToType(this, getRuntime().getString(), "to_str");
     }
 
     /**
      * Internal method that helps to convert any object into the
      * format of a class name and a hex string inside of #<>.
      */
     public IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     /** rb_check_string_type
      *
      * Tries to return a coerced string representation of this object,
      * using "to_str". If that returns something other than a String
      * or nil, an empty String will be returned.
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = TypeConverter.convertToTypeWithCheck(this, getRuntime().getString(), "to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = RubyString.newEmptyString(getRuntime());
         }
         return str;
     }
 
     /** rb_check_string_type
      *
      * Tries to return a coerced string representation of this object,
      * using "to_str". If that returns something other than a String
      * or nil, an empty String will be returned.
      *
      */
     public IRubyObject checkStringType19() {
         IRubyObject str = TypeConverter.convertToTypeWithCheck19(this, getRuntime().getString(), "to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = RubyString.newEmptyString(getRuntime());
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     * Returns the result of trying to convert this object to an Array
     * with "to_ary".
     */
     public IRubyObject checkArrayType() {
         return TypeConverter.convertToTypeWithCheck(this, getRuntime().getArray(), "to_ary");
     }
 
     /**
      * @see IRubyObject#toJava
      */
     public Object toJava(Class target) {
         // for callers that unconditionally pass null retval type (JRUBY-4737)
         if (target == void.class) return null;
 
         if (dataGetStruct() instanceof JavaObject) {
             // for interface impls
 
             JavaObject innerWrapper = (JavaObject)dataGetStruct();
 
             // ensure the object is associated with the wrapper we found it in,
             // so that if it comes back we don't re-wrap it
             if (target.isAssignableFrom(innerWrapper.getValue().getClass())) {
                 getRuntime().getJavaSupport().getObjectProxyCache().put(innerWrapper.getValue(), this);
 
                 return innerWrapper.getValue();
             }
         } else if (JavaUtil.isDuckTypeConvertable(getClass(), target)) {
             if (!respondsTo("java_object")) {
                 return JavaUtil.convertProcToInterface(getRuntime().getCurrentContext(), this, target);
             }
         } else if (target.isAssignableFrom(getClass())) {
             return this;
         }
         
         throw getRuntime().newTypeError("cannot convert instance of " + getClass() + " to " + target);
     }
 
     public IRubyObject dup() {
         if (isImmediate()) throw getRuntime().newTypeError("can't dup " + getMetaClass().getName());
 
         IRubyObject dup = getMetaClass().getRealClass().allocate();
         if (isTaint()) dup.setTaint(true);
         if (isUntrusted()) dup.setUntrusted(true);
 
         initCopy(dup, this);
 
         return dup;
     }
 
     /** init_copy
      *
      * Initializes a copy with variable and special instance variable
      * information, and then call the initialize_copy Ruby method.
      */
     private static void initCopy(IRubyObject clone, IRubyObject original) {
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         original.copySpecialInstanceVariables(clone);
 
         if (original.hasVariables()) clone.syncVariables(original);
         if (original instanceof RubyModule) {
             RubyModule cloneMod = (RubyModule)clone;
             cloneMod.syncConstants((RubyModule)original);
             cloneMod.syncClassVariables((RubyModule)original);
         }
 
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /**
      * Lots of MRI objects keep their state in non-lookupable ivars
      * (e:g. Range, Struct, etc). This method is responsible for
      * dupping our java field equivalents
      */
     public void copySpecialInstanceVariables(IRubyObject clone) {
     }
 
     /** rb_inspect
      *
      * The internal helper that ensures a RubyString instance is returned
      * so dangerous casting can be omitted
      * Prefered over callMethod(context, "inspect")
      */
     static RubyString inspect(ThreadContext context, IRubyObject object) {
         return RubyString.objAsString(context, object.callMethod(context, "inspect"));
     }
 
     public IRubyObject rbClone() {
         if (isImmediate()) throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
 
         // We're cloning ourselves, so we know the result should be a RubyObject
         RubyBasicObject clone = (RubyBasicObject)getMetaClass().getRealClass().allocate();
         clone.setMetaClass(getSingletonClassClone());
         if (isTaint()) clone.setTaint(true);
 
         initCopy(clone, this);
 
         if (isFrozen()) clone.setFrozen(true);
         if (isUntrusted()) clone.setUntrusted(true);
         return clone;
     }
 
     /** rb_singleton_class_clone
      *
      * Will make sure that if the current objects class is a
      * singleton, it will get cloned.
      *
      * @return either a real class, or a clone of the current singleton class
      */
     protected RubyClass getSingletonClassClone() {
         RubyClass klass = getMetaClass();
 
         if (!klass.isSingleton()) {
             return klass;
         }
 
         MetaClass clone = new MetaClass(getRuntime(), klass.getSuperClass(), ((MetaClass) klass).getAttached());
         clone.flags = flags;
 
         if (this instanceof RubyClass) {
             clone.setMetaClass(clone);
         } else {
             clone.setMetaClass(klass.getSingletonClassClone());
         }
 
         if (klass.hasVariables()) {
             clone.syncVariables(klass);
         }
         clone.syncConstants(klass);
 
         klass.cloneMethods(clone);
 
         ((MetaClass) clone.getMetaClass()).setAttached(clone);
 
         return clone;
     }
 
     /**
      * Specifically polymorphic method that are meant to be overridden
      * by modules to specify that they are modules in an easy way.
      */
     public boolean isModule() {
         return false;
     }
 
     /**
      * Specifically polymorphic method that are meant to be overridden
      * by classes to specify that they are classes in an easy way.
      */
     public boolean isClass() {
         return false;
     }
 
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct(Object)
      */
     public synchronized void dataWrapStruct(Object obj) {
         if (obj == null) {
             removeInternalVariable("__wrap_struct__");
         } else {
             fastSetInternalVariable("__wrap_struct__", obj);
         }
     }
 
     // The dataStruct is a place where custom information can be
     // contained for core implementations that doesn't necessarily
     // want to go to the trouble of creating a subclass of
     // RubyObject. The OpenSSL implementation uses this heavily to
     // save holder objects containing Java cryptography objects.
     // Java integration uses this to store the Java object ref.
     //protected transient Object dataStruct;
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataGetStruct()
      */
     public synchronized Object dataGetStruct() {
         return getInternalVariable("__wrap_struct__");
     }
 
     // Equivalent of Data_Get_Struct
     // This will first check that the object in question is actually a T_DATA equivalent.
     public synchronized Object dataGetStructChecked() {
         TypeConverter.checkData(this);
         return getInternalVariable("__wrap_struct__");
     }
 
     /** rb_obj_id
      *
      * Return the internal id of an object.
      */
     @JRubyMethod(name = {"object_id", "__id__"}, compat = RUBY1_9)
     public IRubyObject id() {
         return getRuntime().newFixnum(getObjectId());
     }
 
     /**
      * The logic here is to use the special objectId accessor slot from the
      * parent as a lazy store for an object ID. IDs are generated atomically,
      * in serial, and guaranteed unique for up to 2^63 objects. The special
      * objectId slot is managed separately from the "normal" vars so it
      * does not marshal, clone/dup, or refuse to be initially set when the
      * object is frozen.
      */
     protected long getObjectId() {
         RubyClass realClass = metaClass.getRealClass();
         RubyClass.VariableAccessor objectIdAccessor = realClass.getObjectIdAccessorForRead();
         Long id = (Long)objectIdAccessor.get(this);
         if (id != null) return id;
         
         synchronized (this) {
             objectIdAccessor = realClass.getObjectIdAccessorForRead();
             id = (Long)objectIdAccessor.get(this);
             if (id != null) return id;
 
             return initObjectId(realClass.getObjectIdAccessorForWrite());
         }
     }
 
     /**
      * We lazily stand up the object ID since it forces us to stand up
      * per-object state for a given object. We also check for ObjectSpace here,
      * and normally we do not register a given object ID into ObjectSpace due
      * to the high cost associated with constructing the related weakref. Most
      * uses of id/object_id will only ever need it to be a unique identifier,
      * and the id2ref behavior provided by ObjectSpace is considered internal
      * and not generally supported.
      * 
      * @param objectIdAccessor The variable accessor to use for storing the
      * generated object ID
      * @return The generated object ID
      */
     protected synchronized long initObjectId(RubyClass.VariableAccessor objectIdAccessor) {
         Ruby runtime = getRuntime();
         long id;
         
         if (runtime.isObjectSpaceEnabled()) {
             id = runtime.getObjectSpace().createAndRegisterObjectId(this);
         } else {
             id = ObjectSpace.calculateObjectId(this);
         }
         
         // we use a direct path here to avoid frozen checks
         setObjectId(objectIdAccessor.getIndex(), id);
 
         return id;
     }
 
     /** rb_obj_inspect
      *
      *  call-seq:
      *     obj.inspect   => string
      *
      *  Returns a string containing a human-readable representation of
      *  <i>obj</i>. If not overridden, uses the <code>to_s</code> method to
      *  generate the string.
      *
      *     [ 1, 2, 3..4, 'five' ].inspect   #=> "[1, 2, 3..4, \"five\"]"
      *     Time.new.inspect                 #=> "Wed Apr 09 08:54:39 CDT 2003"
      */
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         if ((!isImmediate()) && !(this instanceof RubyModule) && hasVariables()) {
             return hashyInspect();
         }
 
         if (isNil()) return RubyNil.inspect(this);
         return RuntimeHelpers.invoke(runtime.getCurrentContext(), this, "to_s");
     }
 
     public IRubyObject hashyInspect() {
         Ruby runtime = getRuntime();
         StringBuilder part = new StringBuilder();
         String cname = getMetaClass().getRealClass().getName();
         part.append("#<").append(cname).append(":0x");
         part.append(Integer.toHexString(inspectHashCode()));
 
         if (runtime.isInspecting(this)) {
             /* 6:tags 16:addr 1:eos */
             part.append(" ...>");
             return runtime.newString(part.toString());
         }
         try {
             runtime.registerInspecting(this);
             return runtime.newString(inspectObj(part).toString());
         } finally {
             runtime.unregisterInspecting(this);
         }
     }
 
     /**
      * For most objects, the hash used in the default #inspect is just the
      * identity hashcode of the actual object.
      *
      * See org.jruby.java.proxies.JavaProxy for a divergent case.
      *
      * @return The identity hashcode of this object
      */
     protected int inspectHashCode() {
         return System.identityHashCode(this);
     }
 
     /** inspect_obj
      *
      * The internal helper method that takes care of the part of the
      * inspection that inspects instance variables.
      */
     private StringBuilder inspectObj(StringBuilder part) {
         ThreadContext context = getRuntime().getCurrentContext();
         String sep = "";
 
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : getMetaClass().getVariableAccessorsForRead().entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null || !(value instanceof IRubyObject) || !IdUtil.isInstanceVariable(entry.getKey())) continue;
             
             part.append(sep).append(" ").append(entry.getKey()).append("=");
             part.append(((IRubyObject)value).callMethod(context, "inspect"));
             sep = ",";
         }
         part.append(">");
         return part;
     }
 
     // Methods of the Object class (rb_obj_*):
 
 
     @JRubyMethod(name = "!", compat = RUBY1_9)
     public IRubyObject op_not(ThreadContext context) {
         return context.getRuntime().newBoolean(!this.isTrue());
     }
 
     @JRubyMethod(name = "!=", required = 1, compat = RUBY1_9)
     public IRubyObject op_not_equal(ThreadContext context, IRubyObject other) {
         return context.getRuntime().newBoolean(!invokedynamic(context, this, OP_EQUAL, other).isTrue());
     }
 
     public int compareTo(IRubyObject other) {
         return (int)invokedynamic(getRuntime().getCurrentContext(), this, OP_CMP, other).convertToInteger().getLongValue();
     }
 
     public IRubyObject op_equal(ThreadContext context, IRubyObject obj) {
         return op_equal_19(context, obj);
     }
 
     /** rb_obj_equal
      *
      * Will by default use identity equality to compare objects. This
      * follows the Ruby semantics.
      *
      * The name of this method doesn't follow the convention because hierarchy problems
      */
     @JRubyMethod(name = "==", compat = RUBY1_9)
     public IRubyObject op_equal_19(ThreadContext context, IRubyObject obj) {
         return this == obj ? context.runtime.getTrue() : context.runtime.getFalse();
     }
 
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         // Remain unimplemented due to problems with the double java hierarchy
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "equal?", required = 1, compat = RUBY1_9)
     public IRubyObject equal_p19(ThreadContext context, IRubyObject other) {
         return op_equal_19(context, other);
     }
 
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "==" method.
      */
     protected static boolean equalInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         return that == other || invokedynamic(context, that, OP_EQUAL, other).isTrue();
     }
 
     /** method used for Hash key comparison (specialized for String, Symbol and Fixnum)
      *
      * Will by default just call the Ruby method "eql?"
      */
     public boolean eql(IRubyObject other) {
         return invokedynamic(getRuntime().getCurrentContext(), this, EQL, other).isTrue();
     }
 
     /**
      * Adds the specified object as a finalizer for this object.
      */
     public void addFinalizer(IRubyObject f) {
         Finalizer finalizer = (Finalizer)getInternalVariable("__finalizer__");
         if (finalizer == null) {
             // since this is the first time we're registering a finalizer, we
             // must also register this object in ObjectSpace, so that future
             // calls to undefine_finalizer, which takes an object ID, can
             // locate the object properly. See JRUBY-4839.
             long id = getObjectId();
             RubyFixnum fixnumId = (RubyFixnum)id();
 
             getRuntime().getObjectSpace().registerObjectId(id, this);
 
             finalizer = new Finalizer(fixnumId);
             fastSetInternalVariable("__finalizer__", finalizer);
             getRuntime().addFinalizer(finalizer);
         }
         finalizer.addFinalizer(f);
     }
 
     /**
      * Remove all the finalizers for this object.
      */
     public void removeFinalizers() {
         Finalizer finalizer = (Finalizer)getInternalVariable("__finalizer__");
         if (finalizer != null) {
             finalizer.removeFinalizers();
             removeInternalVariable("__finalizer__");
             getRuntime().removeFinalizer(finalizer);
         }
     }
 
     /**
      * Get variable table for read purposes. May return null if uninitialized.
      */
     protected final Object[] getVariableTableForRead() {
         return varTable;
     }
 
     private static final AtomicReferenceFieldUpdater VARTABLE_UPDATER;
 
     static {
         AtomicReferenceFieldUpdater updater = null;
         try {
             updater = AtomicReferenceFieldUpdater.newUpdater(RubyBasicObject.class, Object[].class, "varTable");
         } catch (RuntimeException re) {
             if (re.getCause() instanceof AccessControlException) {
                 // security prevented creation; fall back on synchronized assignment
             } else {
                 throw re;
             }
         }
         VARTABLE_UPDATER = updater;
     }
 
 
     /**
      * Get variable table for write purposes. Initializes if uninitialized, and
      * resizes if necessary.
      */
     protected final Object[] getVariableTableForWrite(int index) {
         if (VARTABLE_UPDATER == null) {
             return getVariableTableForWriteSynchronized(index);
         } else {
             return getVariableTableForWriteAtomic(index);
         }
     }
 
     /**
      * Get the variable table for write. If it is not set or not of the right size,
      * synchronize against the object and prepare it accordingly.
      *
      * @param index the index of the value soon to be set
      * @return the var table, ready for setting
      */
     private Object[] getVariableTableForWriteSynchronized(int index) {
         Object[] myVarTable = varTable;
         if (myVarTable == null || myVarTable.length <= index) {
             synchronized (this) {
                 myVarTable = varTable;
 
                 if (myVarTable == null) {
                     return varTable = new Object[getMetaClass().getRealClass().getVariableTableSizeWithExtras()];
                 } else if (myVarTable.length <= index) {
                     Object[] newTable = new Object[getMetaClass().getRealClass().getVariableTableSizeWithExtras()];
                     System.arraycopy(myVarTable, 0, newTable, 0, myVarTable.length);
                     return varTable = newTable;
                 } else {
                     return myVarTable;
                 }
             }
         }
 
         return varTable;
     }
 
 
     /**
      * Get the variable table for write. If it is not set or not of the right size,
      * atomically update it with an appropriate value.
      *
      * @param index the index of the value soon to be set
      * @return the var table, ready for setting
      */
     private Object[] getVariableTableForWriteAtomic(int index) {
         while (true) {
             Object[] myVarTable = varTable;
             Object[] newTable;
 
             if (myVarTable == null) {
                 newTable = new Object[metaClass.getRealClass().getVariableTableSizeWithExtras()];
             } else if (myVarTable.length <= index) {
                 newTable = new Object[metaClass.getRealClass().getVariableTableSizeWithExtras()];
                 System.arraycopy(myVarTable, 0, newTable, 0, myVarTable.length);
             } else {
                 return myVarTable;
             }
 
             // proceed with atomic update of table, or retry
             if (VARTABLE_UPDATER.compareAndSet(this, myVarTable, newTable)) {
                 return newTable;
             }
         }
     }
 
     public Object getVariable(int index) {
 		Object[] ivarTable;
         if (index < 0 || (ivarTable = getVariableTableForRead()) == null) return null;
         if (ivarTable.length > index) return ivarTable[index];
         return null;
     }
 
     public void setVariable(int index, Object value) {
         ensureInstanceVariablesSettable();
         if (index < 0) return;
         Object[] ivarTable = getVariableTableForWrite(index);
         ivarTable[index] = value;
     }
 
     private void setObjectId(int index, long value) {
         if (index < 0) return;
         Object[] ivarTable = getVariableTableForWrite(index);
         ivarTable[index] = value;
     }
     
     public final Object getNativeHandle() {
         return getMetaClass().getRealClass().getNativeHandleAccessorForRead().get(this);
     }
 
     public final void setNativeHandle(Object value) {
         int index = getMetaClass().getRealClass().getNativeHandleAccessorForWrite().getIndex();
         Object[] ivarTable = getVariableTableForWrite(index);
         ivarTable[index] = value;
     }
 
     public final Object getFFIHandle() {
         return getMetaClass().getRealClass().getFFIHandleAccessorForRead().get(this);
     }
 
     public final void setFFIHandle(Object value) {
         int index = getMetaClass().getRealClass().getFFIHandleAccessorForWrite().getIndex();
         Object[] ivarTable = getVariableTableForWrite(index);
         ivarTable[index] = value;
     }
 
     //
     // COMMON VARIABLE METHODS
     //
 
     /**
      * Returns true if object has any variables, defined as:
      * <ul>
      * <li> instance variables
      * <li> class variables
      * <li> constants
      * <li> internal variables, such as those used when marshaling Ranges and Exceptions
      * </ul>
      * @return true if object has any variables, else false
      */
     public boolean hasVariables() {
         // we check both to exclude object_id
         return getMetaClass().getRealClass().getVariableTableSize() > 0 && varTable != null && varTable.length > 0;
     }
 
     /**
      * Gets a list of all variables in this object.
      */
     // TODO: must override in RubyModule to pick up constants
     public List<Variable<Object>> getVariableList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getRealClass().getVariableAccessorsForRead();
         ArrayList<Variable<Object>> list = new ArrayList<Variable<Object>>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null) continue;
             list.add(new VariableEntry<Object>(entry.getKey(), value));
         }
         return list;
     }
 
     /**
      * Gets a name list of all variables in this object.
      */
    // TODO: must override in RubyModule to pick up constants
    public List<String> getVariableNameList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getRealClass().getVariableAccessorsForRead();
         ArrayList<String> list = new ArrayList<String>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null) continue;
             list.add(entry.getKey());
         }
         return list;
     }
 
     /**
      * Checks if the variable table contains a variable of the
      * specified name.
      */
     protected boolean variableTableContains(String name) {
         return getMetaClass().getRealClass().getVariableAccessorForRead(name).get(this) != null;
     }
 
     /**
      * Fetch an object from the variable table based on the name.
      *
      * @return the object or null if not found
      */
     protected Object variableTableFetch(String name) {
         return getMetaClass().getRealClass().getVariableAccessorForRead(name).get(this);
     }
 
     /**
      * Store a value in the variable store under the specific name.
      */
     protected Object variableTableStore(String name, Object value) {
         getMetaClass().getRealClass().getVariableAccessorForWrite(name).set(this, value);
         return value;
     }
 
     /**
      * Removes the entry with the specified name from the variable
      * table, and returning the removed value.
      */
     protected Object variableTableRemove(String name) {
         synchronized(this) {
             Object value = getMetaClass().getRealClass().getVariableAccessorForRead(name).get(this);
             getMetaClass().getRealClass().getVariableAccessorForWrite(name).set(this, null);
             
             // if there's no values set anymore, null out the table
             for (Object var : varTable) {
                 if (var != null) return value;
             }
             varTable = null;
             return value;
         }
     }
 
     /**
      * Synchronize the variable table with the argument. In real terms
      * this means copy all entries into a newly allocated table.
      */
     protected void variableTableSync(List<Variable<Object>> vars) {
         synchronized(this) {
             for (Variable<Object> var : vars) {
                 variableTableStore(var.getName(), var.getValue());
             }
         }
     }
 
     //
     // INTERNAL VARIABLE METHODS
     //
 
     /**
      * Dummy method to avoid a cast, and to avoid polluting the
      * IRubyObject interface with all the instance variable management
      * methods.
      */
     public InternalVariables getInternalVariables() {
         return this;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#hasInternalVariable
      */
     public boolean hasInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableContains(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#getInternalVariable
      */
     public Object getInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableFetch(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#setInternalVariable
      */
     public void setInternalVariable(String name, Object value) {
         assert !IdUtil.isRubyVariable(name);
         variableTableStore(name, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#removeInternalVariable
      */
     public Object removeInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableRemove(name);
     }
 
     /**
      * Sync one this object's variables with other's - this is used to make
      * rbClone work correctly.
      */
     public void syncVariables(IRubyObject other) {
         RubyClass realClass = metaClass.getRealClass();
         RubyClass otherRealClass = other.getMetaClass().getRealClass();
         boolean sameTable = otherRealClass == realClass;
 
         if (sameTable) {
             RubyClass.VariableAccessor objIdAccessor = otherRealClass.getObjectIdAccessorForRead();
             Object[] otherVars = ((RubyBasicObject) other).varTable;
             int otherLength = otherVars.length;
             Object[] myVars = getVariableTableForWrite(otherLength - 1);
             System.arraycopy(otherVars, 0, myVars, 0, otherLength);
 
             // null out object ID so we don't share it
             int objIdIndex = objIdAccessor.getIndex();
             if (objIdIndex > 0 && objIdIndex < myVars.length) {
                 myVars[objIdIndex] = null;
             }
         } else {
             for (Map.Entry<String, RubyClass.VariableAccessor> entry : otherRealClass.getVariableAccessorsForRead().entrySet()) {
                 RubyClass.VariableAccessor accessor = entry.getValue();
                 Object value = accessor.get(other);
 
                 if (value != null) {
                     if (sameTable) {
                         accessor.set(this, value);
                     } else {
                         realClass.getVariableAccessorForWrite(accessor.getName()).set(this, value);
                     }
                 }
             }
         }
     }
 
 
     //
     // INSTANCE VARIABLE API METHODS
     //
 
     /**
      * Dummy method to avoid a cast, and to avoid polluting the
      * IRubyObject interface with all the instance variable management
      * methods.
      */
     public InstanceVariables getInstanceVariables() {
         return this;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#hasInstanceVariable
      */
     public boolean hasInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         return variableTableContains(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariable
      */
     public IRubyObject getInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         return (IRubyObject)variableTableFetch(name);
     }
 
     /** rb_iv_set / rb_ivar_set
     *
     * @see org.jruby.runtime.builtin.InstanceVariables#setInstanceVariable
     */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         assert IdUtil.isInstanceVariable(name) && value != null;
         ensureInstanceVariablesSettable();
         return (IRubyObject)variableTableStore(name, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#removeInstanceVariable
      */
     public IRubyObject removeInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         ensureInstanceVariablesSettable();
         return (IRubyObject)variableTableRemove(name);
     }
 
     /**
      * Gets a list of all variables in this object.
      */
     // TODO: must override in RubyModule to pick up constants
     public List<Variable<IRubyObject>> getInstanceVariableList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getVariableAccessorsForRead();
         ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null || !(value instanceof IRubyObject) || !IdUtil.isInstanceVariable(entry.getKey())) continue;
             list.add(new VariableEntry<IRubyObject>(entry.getKey(), (IRubyObject)value));
         }
         return list;
     }
 
     /**
      * Gets a name list of all variables in this object.
      */
    // TODO: must override in RubyModule to pick up constants
    public List<String> getInstanceVariableNameList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getRealClass().getVariableAccessorsForRead();
         ArrayList<String> list = new ArrayList<String>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null || !(value instanceof IRubyObject) || !IdUtil.isInstanceVariable(entry.getKey())) continue;
             list.add(entry.getKey());
         }
         return list;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariableNameList
      */
     public void copyInstanceVariablesInto(final InstanceVariables other) {
         for (Variable<IRubyObject> var : getInstanceVariableList()) {
diff --git a/src/org/jruby/RubyClass.java b/src/org/jruby/RubyClass.java
index 21c5f7bce9..1e4e5dc1a9 100644
--- a/src/org/jruby/RubyClass.java
+++ b/src/org/jruby/RubyClass.java
@@ -1,1906 +1,1907 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 
 import static org.jruby.util.CodegenUtils.ci;
 import static org.jruby.util.CodegenUtils.p;
 import static org.jruby.util.CodegenUtils.sig;
 import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
 import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
 import static org.objectweb.asm.Opcodes.ACC_STATIC;
 import static org.objectweb.asm.Opcodes.ACC_SUPER;
 import static org.objectweb.asm.Opcodes.ACC_VARARGS;
 
 import java.io.IOException;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
+import org.jruby.internal.runtime.methods.ProfilingDynamicMethod;
 import org.jruby.java.codegen.RealClassGenerator;
 import org.jruby.java.codegen.Reified;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.CacheEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.runtime.opto.Invalidator;
 import org.jruby.util.ClassCache.OneShotClassLoader;
 import org.jruby.util.ClassDefiningClassLoader;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.collections.WeakHashSet;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.objectweb.asm.AnnotationVisitor;
 import org.objectweb.asm.ClassWriter;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Class", parent="Module")
 public class RubyClass extends RubyModule {
 
     private static final Logger LOG = LoggerFactory.getLogger("RubyClass");
 
     public static void createClassClass(Ruby runtime, RubyClass classClass) {
         classClass.index = ClassIndex.CLASS;
         classClass.setReifiedClass(RubyClass.class);
         classClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyClass;
             }
         };
         
         classClass.undefineMethod("module_function");
         classClass.undefineMethod("append_features");
         classClass.undefineMethod("extend_object");
         
         classClass.defineAnnotatedMethods(RubyClass.class);
     }
     
     public static final ObjectAllocator CLASS_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyClass clazz = new RubyClass(runtime);
             clazz.allocator = ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR; // Class.allocate object is not allocatable before it is initialized
             return clazz;
         }
     };
 
     public ObjectAllocator getAllocator() {
         return allocator;
     }
 
     public void setAllocator(ObjectAllocator allocator) {
         this.allocator = allocator;
     }
 
     /**
      * Set a reflective allocator that calls a no-arg constructor on the given
      * class.
      *
      * @param cls The class on which to call the default constructor to allocate
      */
     public void setClassAllocator(final Class cls) {
         this.allocator = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                 try {
                     RubyBasicObject object = (RubyBasicObject)cls.newInstance();
                     object.setMetaClass(klazz);
                     return object;
                 } catch (InstantiationException ie) {
                     throw runtime.newTypeError("could not allocate " + cls + " with default constructor:\n" + ie);
                 } catch (IllegalAccessException iae) {
                     throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible default constructor:\n" + iae);
                 }
             }
         };
         
         this.reifiedClass = cls;
     }
 
     /**
      * Set a reflective allocator that calls the "standard" Ruby object
      * constructor (Ruby, RubyClass) on the given class.
      *
      * @param cls The class from which to grab a standard Ruby constructor
      */
     public void setRubyClassAllocator(final Class cls) {
         try {
             final Constructor constructor = cls.getConstructor(Ruby.class, RubyClass.class);
             
             this.allocator = new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                     try {
                         return (IRubyObject)constructor.newInstance(runtime, klazz);
                     } catch (InvocationTargetException ite) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ite);
                     } catch (InstantiationException ie) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ie);
                     } catch (IllegalAccessException iae) {
                         throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible (Ruby, RubyClass) constructor:\n" + iae);
                     }
                 }
             };
 
             this.reifiedClass = cls;
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 
     /**
      * Set a reflective allocator that calls the "standard" Ruby object
      * constructor (Ruby, RubyClass) on the given class via a static
      * __allocate__ method intermediate.
      *
      * @param cls The class from which to grab a standard Ruby __allocate__
      *            method.
      */
     public void setRubyStaticAllocator(final Class cls) {
         try {
             final Method method = cls.getDeclaredMethod("__allocate__", Ruby.class, RubyClass.class);
 
             this.allocator = new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                     try {
                         return (IRubyObject)method.invoke(null, runtime, klazz);
                     } catch (InvocationTargetException ite) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ite);
                     } catch (IllegalAccessException iae) {
                         throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible (Ruby, RubyClass) constructor:\n" + iae);
                     }
                 }
             };
 
             this.reifiedClass = cls;
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 
     @JRubyMethod(name = "allocate")
     public IRubyObject allocate() {
         if (superClass == null) {
             if(!(runtime.is1_9() && this == runtime.getBasicObject())) {
                 throw runtime.newTypeError("can't instantiate uninitialized class");
             }
         }
         IRubyObject obj = allocator.allocate(runtime, this);
         if (obj.getMetaClass().getRealClass() != getRealClass()) {
             throw runtime.newTypeError("wrong instance allocation");
         }
         return obj;
     }
 
     public CallSite[] getBaseCallSites() {
         return baseCallSites;
     }
     
     public CallSite[] getExtraCallSites() {
         return extraCallSites;
     }
 
     public static class VariableAccessor {
         private final String name;
         private final int index;
         private final int classId;
         public VariableAccessor(String name, int index, int classId) {
             this.index = index;
             this.classId = classId;
             this.name = name;
         }
         public int getClassId() {
             return classId;
         }
         public int getIndex() {
             return index;
         }
         public String getName() {
             return name;
         }
         public Object get(Object object) {
             return ((IRubyObject)object).getVariable(index);
         }
         public void set(Object object, Object value) {
             ((IRubyObject)object).setVariable(index, value);
         }
         public static final VariableAccessor DUMMY_ACCESSOR = new VariableAccessor(null, -1, -1);
     }
 
     public Map<String, VariableAccessor> getVariableAccessorsForRead() {
         return variableAccessors;
     }
     
     private volatile VariableAccessor objectIdAccessor = VariableAccessor.DUMMY_ACCESSOR;
     
     private volatile VariableAccessor cextHandleAccessor = VariableAccessor.DUMMY_ACCESSOR;
 
     private volatile VariableAccessor ffiHandleAccessor = VariableAccessor.DUMMY_ACCESSOR;
 
     private synchronized final VariableAccessor allocateVariableAccessor(String name) {
         String[] myVariableNames = variableNames;
 
         int newIndex = myVariableNames.length;
         String[] newVariableNames = new String[newIndex + 1];
 
         VariableAccessor newVariableAccessor = new VariableAccessor(name, newIndex, this.id);
 
         System.arraycopy(myVariableNames, 0, newVariableNames, 0, newIndex);
 
         newVariableNames[newIndex] = name;
         variableNames = newVariableNames;
 
         return newVariableAccessor;
     }
 
     public VariableAccessor getVariableAccessorForWrite(String name) {
         VariableAccessor ivarAccessor = variableAccessors.get(name);
         if (ivarAccessor == null) {
 
             synchronized (this) {
                 Map<String, VariableAccessor> myVariableAccessors = variableAccessors;
                 ivarAccessor = myVariableAccessors.get(name);
 
                 if (ivarAccessor == null) {
                     // allocate a new accessor and populate a new table
                     ivarAccessor = allocateVariableAccessor(name);
                     Map<String, VariableAccessor> newVariableAccessors = new HashMap<String, VariableAccessor>(myVariableAccessors.size() + 1);
 
                     newVariableAccessors.putAll(myVariableAccessors);
                     newVariableAccessors.put(name, ivarAccessor);
 
                     variableAccessors = newVariableAccessors;
                 }
             }
         }
         return ivarAccessor;
     }
 
     private synchronized VariableAccessor allocateIdAccessor() {
         if (objectIdAccessor == VariableAccessor.DUMMY_ACCESSOR) {
             objectIdAccessor = allocateVariableAccessor("object_id");
         }
 
         return objectIdAccessor;
     }
 
     private synchronized VariableAccessor allocateCExtHandleAccessor() {
         if (cextHandleAccessor == VariableAccessor.DUMMY_ACCESSOR) {
             cextHandleAccessor = allocateVariableAccessor("cext");
         }
 
         return cextHandleAccessor;
     }
 
     private synchronized VariableAccessor allocateFFIHandleAccessor() {
         if (ffiHandleAccessor == VariableAccessor.DUMMY_ACCESSOR) {
             ffiHandleAccessor = allocateVariableAccessor("ffi");
         }
 
         return ffiHandleAccessor;
     }
 
 
     public VariableAccessor getVariableAccessorForRead(String name) {
         VariableAccessor accessor = getVariableAccessorsForRead().get(name);
         if (accessor == null) accessor = VariableAccessor.DUMMY_ACCESSOR;
         return accessor;
     }
 
     public VariableAccessor getObjectIdAccessorForWrite() {
         VariableAccessor accessor = objectIdAccessor;
         return accessor != VariableAccessor.DUMMY_ACCESSOR ? accessor : allocateIdAccessor();
     }
 
     public VariableAccessor getObjectIdAccessorForRead() {
         return objectIdAccessor;
     }
 
     public VariableAccessor getNativeHandleAccessorForWrite() {
         VariableAccessor accessor = cextHandleAccessor;
         return accessor != VariableAccessor.DUMMY_ACCESSOR ? accessor : allocateCExtHandleAccessor();
     }
 
     public VariableAccessor getNativeHandleAccessorForRead() {
         return cextHandleAccessor;
     }
 
     public VariableAccessor getFFIHandleAccessorForWrite() {
         VariableAccessor accessor = ffiHandleAccessor;
         return accessor != VariableAccessor.DUMMY_ACCESSOR ? accessor : allocateFFIHandleAccessor();
     }
 
     public VariableAccessor getFFIHandleAccessorForRead() {
         return ffiHandleAccessor;
     }
 
     public int getVariableTableSize() {
         return variableAccessors.size();
     }
 
     public int getVariableTableSizeWithExtras() {
         return variableNames.length;
     }
 
     public Map<String, VariableAccessor> getVariableTableCopy() {
         return new HashMap<String, VariableAccessor>(getVariableAccessorsForRead());
     }
 
     /**
      * Get an array of all the known instance variable names. The offset into
      * the array indicates the offset of the variable's value in the per-object
      * variable array.
      *
      * @return a copy of the array of known instance variable names
      */
     public String[] getVariableNames() {
         String[] original = variableNames;
         String[] copy = new String[original.length];
         System.arraycopy(original, 0, copy, 0, original.length);
         return copy;
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.CLASS;
     }
     
     @Override
     public boolean isModule() {
         return false;
     }
 
     @Override
     public boolean isClass() {
         return true;
     }
 
     @Override
     public boolean isSingleton() {
         return false;
     }
 
     /** boot_defclass
      * Create an initial Object meta class before Module and Kernel dependencies have
      * squirreled themselves together.
      * 
      * @param runtime we need it
      * @return a half-baked meta class for object
      */
     public static RubyClass createBootstrapClass(Ruby runtime, String name, RubyClass superClass, ObjectAllocator allocator) {
         RubyClass obj;
 
         if (superClass == null ) {  // boot the Object class 
             obj = new RubyClass(runtime);
             obj.marshal = DEFAULT_OBJECT_MARSHAL;
         } else {                    // boot the Module and Class classes
             obj = new RubyClass(runtime, superClass);
         }
         obj.setAllocator(allocator);
         obj.setBaseName(name);
         return obj;
     }
 
     /** separate path for MetaClass and IncludedModuleWrapper construction
      *  (rb_class_boot version for MetaClasses)
      *  no marshal, allocator initialization and addSubclass(this) here!
      */
     protected RubyClass(Ruby runtime, RubyClass superClass, boolean objectSpace) {
         super(runtime, runtime.getClassClass(), objectSpace);
         this.runtime = runtime;
         this.realClass = superClass == null ? null : superClass.getRealClass();
         setSuperClass(superClass); // this is the only case it might be null here (in MetaClass construction)
     }
     
     /** used by CLASS_ALLOCATOR (any Class' class will be a Class!)
      *  also used to bootstrap Object class
      */
     protected RubyClass(Ruby runtime) {
         super(runtime, runtime.getClassClass());
         this.runtime = runtime;
         this.realClass = this;
         index = ClassIndex.CLASS;
     }
     
     /** rb_class_boot (for plain Classes)
      *  also used to bootstrap Module and Class classes 
      */
     protected RubyClass(Ruby runtime, RubyClass superClazz) {
         this(runtime);
         setSuperClass(superClazz);
         marshal = superClazz.marshal; // use parent's marshal
         superClazz.addSubclass(this);
         allocator = superClazz.allocator;
         
         infectBy(superClass);        
     }
     
     /** 
      * A constructor which allows passing in an array of supplementary call sites.
      */
     protected RubyClass(Ruby runtime, RubyClass superClazz, CallSite[] extraCallSites) {
         this(runtime);
         setSuperClass(superClazz);
         this.marshal = superClazz.marshal; // use parent's marshal
         superClazz.addSubclass(this);
         
         this.extraCallSites = extraCallSites;
         
         infectBy(superClass);        
     }
 
     /** 
      * Construct a new class with the given name scoped under Object (global)
      * and with Object as its immediate superclass.
      * Corresponds to rb_class_new in MRI.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass) {
         if (superClass == runtime.getClassClass()) throw runtime.newTypeError("can't make subclass of Class");
         if (superClass.isSingleton()) throw runtime.newTypeError("can't make subclass of virtual class");
         return new RubyClass(runtime, superClass);        
     }
 
     /** 
      * A variation on newClass that allow passing in an array of supplementary
      * call sites to improve dynamic invocation.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, CallSite[] extraCallSites) {
         if (superClass == runtime.getClassClass()) throw runtime.newTypeError("can't make subclass of Class");
         if (superClass.isSingleton()) throw runtime.newTypeError("can't make subclass of virtual class");
         return new RubyClass(runtime, superClass, extraCallSites);        
     }
 
     /** 
      * Construct a new class with the given name, allocator, parent class,
      * and containing class. If setParent is true, the class's parent will be
      * explicitly set to the provided parent (rather than the new class just
      * being assigned to a constant in that parent).
      * Corresponds to rb_class_new/rb_define_class_id/rb_name_class/rb_set_class_path
      * in MRI.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, String name, ObjectAllocator allocator, RubyModule parent, boolean setParent) {
         RubyClass clazz = newClass(runtime, superClass);
         clazz.setBaseName(name);
         clazz.setAllocator(allocator);
         clazz.makeMetaClass(superClass.getMetaClass());
         if (setParent) clazz.setParent(parent);
         parent.setConstant(name, clazz);
         clazz.inherit(superClass);
         return clazz;
     }
 
     /** 
      * A variation on newClass that allows passing in an array of supplementary
      * call sites to improve dynamic invocation performance.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, String name, ObjectAllocator allocator, RubyModule parent, boolean setParent, CallSite[] extraCallSites) {
         RubyClass clazz = newClass(runtime, superClass, extraCallSites);
         clazz.setBaseName(name);
         clazz.setAllocator(allocator);
         clazz.makeMetaClass(superClass.getMetaClass());
         if (setParent) clazz.setParent(parent);
         parent.setConstant(name, clazz);
         clazz.inherit(superClass);
         return clazz;
     }
 
     /** rb_make_metaclass
      *
      */
     @Override
     public RubyClass makeMetaClass(RubyClass superClass) {
         if (isSingleton()) { // could be pulled down to RubyClass in future
             MetaClass klass = new MetaClass(runtime, superClass, this); // rb_class_boot
             setMetaClass(klass);
 
             klass.setMetaClass(klass);
             klass.setSuperClass(getSuperClass().getRealClass().getMetaClass());
             
             return klass;
         } else {
             return super.makeMetaClass(superClass);
         }
     }
     
     @Deprecated
     public IRubyObject invoke(ThreadContext context, IRubyObject self, int methodIndex, String name, IRubyObject[] args, CallType callType, Block block) {
         return invoke(context, self, name, args, callType, block);
     }
     
     public boolean notVisibleAndNotMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return !method.isCallableFrom(caller, callType) && !name.equals("method_missing");
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, block);
         }
         return method.call(context, self, this, name, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, block);
         }
         return method.call(context, self, this, name, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, args, block);
         }
         return method.call(context, self, this, name, args, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, Block block) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, args, block);
         }
         return method.call(context, self, this, name, args, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg, block);
         }
         return method.call(context, self, this, name, arg, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg, block);
         }
         return method.call(context, self, this, name, arg, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, block);
         }
         return method.call(context, self, this, name, arg0, arg1, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, block);
         }
         return method.call(context, self, this, name, arg0, arg1, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, arg2, block);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, arg2, block);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name);
     }
 
     public IRubyObject finvokeChecked(ThreadContext context, IRubyObject self, String name) {
         DynamicMethod method = searchMethod(name);
         if(method.isUndefined()) {
             DynamicMethod methodMissing = searchMethod("method_missing");
-            if(methodMissing.isUndefined() || methodMissing == context.getRuntime().getDefaultMethodMissing()) {
+            if(methodMissing.isUndefined() || methodMissing.equals(context.getRuntime().getDefaultMethodMissing())) {
                 return null;
             }
 
             try {
                 return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, Block.NULL_BLOCK);
             } catch(RaiseException e) {
                 if(context.getRuntime().getNoMethodError().isInstance(e.getException())) {
                     if(self.respondsTo(name)) {
                         throw e;
                     } else {
                         // we swallow, so we also must clear $!
                         context.setErrorInfo(context.nil);
                         return null;
                     }
                 } else {
                     throw e;
                 }
             }
         }
         return method.call(context, self, this, name);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, CallType callType) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, args, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, args);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, args, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, args);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2);
     }
 
     private void dumpReifiedClass(String dumpDir, String javaPath, byte[] classBytes) {
         if (dumpDir != null) {
             if (dumpDir.equals("")) {
                 dumpDir = ".";
             }
             java.io.FileOutputStream classStream = null;
             try {
                 java.io.File classFile = new java.io.File(dumpDir, javaPath + ".class");
                 classFile.getParentFile().mkdirs();
                 classStream = new java.io.FileOutputStream(classFile);
                 classStream.write(classBytes);
             } catch (IOException io) {
                 getRuntime().getWarnings().warn("unable to dump class file: " + io.getMessage());
             } finally {
                 if (classStream != null) {
                     try {
                         classStream.close();
                     } catch (IOException ignored) {
                     }
                 }
             }
         }
     }
 
     private void generateMethodAnnotations(Map<Class, Map<String, Object>> methodAnnos, SkinnyMethodAdapter m, List<Map<Class, Map<String, Object>>> parameterAnnos) {
         if (methodAnnos != null && methodAnnos.size() != 0) {
             for (Map.Entry<Class, Map<String, Object>> entry : methodAnnos.entrySet()) {
                 m.visitAnnotationWithFields(ci(entry.getKey()), true, entry.getValue());
             }
         }
         if (parameterAnnos != null && parameterAnnos.size() != 0) {
             for (int i = 0; i < parameterAnnos.size(); i++) {
                 Map<Class, Map<String, Object>> annos = parameterAnnos.get(i);
                 if (annos != null && annos.size() != 0) {
                     for (Iterator<Map.Entry<Class, Map<String, Object>>> it = annos.entrySet().iterator(); it.hasNext();) {
                         Map.Entry<Class, Map<String, Object>> entry = it.next();
                         m.visitParameterAnnotationWithFields(i, ci(entry.getKey()), true, entry.getValue());
                     }
                 }
             }
         }
     }
     
     private boolean shouldCallMethodMissing(DynamicMethod method) {
         return method.isUndefined();
     }
     private boolean shouldCallMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return method.isUndefined() || notVisibleAndNotMethodMissing(method, name, caller, callType);
     }
     
     public IRubyObject invokeInherited(ThreadContext context, IRubyObject self, IRubyObject subclass) {
         DynamicMethod method = getMetaClass().searchMethod("inherited");
 
         if (method.isUndefined()) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), "inherited", CallType.FUNCTIONAL, Block.NULL_BLOCK);
         }
 
         return method.call(context, self, getMetaClass(), "inherited", subclass, Block.NULL_BLOCK);
     }
 
     /** rb_class_new_instance
     *
     */
     @JRubyMethod(name = "new", omit = true)
     public IRubyObject newInstance(ThreadContext context, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, obj, obj, block);
         return obj;
     }
 
     @JRubyMethod(name = "new", omit = true)
     public IRubyObject newInstance(ThreadContext context, IRubyObject arg0, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, obj, obj, arg0, block);
         return obj;
     }
 
     @JRubyMethod(name = "new", omit = true)
     public IRubyObject newInstance(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, obj, obj, arg0, arg1, block);
         return obj;
     }
 
     @JRubyMethod(name = "new", omit = true)
     public IRubyObject newInstance(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, obj, obj, arg0, arg1, arg2, block);
         return obj;
     }
 
     @JRubyMethod(name = "new", rest = true, omit = true)
     public IRubyObject newInstance(ThreadContext context, IRubyObject[] args, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, obj, obj, args, block);
         return obj;
     }
 
     /** rb_class_initialize
      * 
      */
     @JRubyMethod(compat = RUBY1_8, visibility = PRIVATE)
     @Override
     public IRubyObject initialize(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(context, runtime.getObject(), block, false);
     }
         
     @JRubyMethod(compat = RUBY1_8, visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon(context, (RubyClass)superObject, block, false);
     }
         
     @JRubyMethod(name = "initialize", compat = RUBY1_9, visibility = PRIVATE)
     public IRubyObject initialize19(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(context, runtime.getObject(), block, true);
     }
         
     @JRubyMethod(name = "initialize", compat = RUBY1_9, visibility = PRIVATE)
     public IRubyObject initialize19(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon(context, (RubyClass)superObject, block, true);
     }
 
     private IRubyObject initializeCommon(ThreadContext context, RubyClass superClazz, Block block, boolean ruby1_9 /*callInheritBeforeSuper*/) {
         setSuperClass(superClazz);
         allocator = superClazz.allocator;
         makeMetaClass(superClazz.getMetaClass());
 
         marshal = superClazz.marshal;
 
         superClazz.addSubclass(this);
 
         if (ruby1_9) {
             inherit(superClazz);
             super.initialize(context, block);
         } else {
             super.initialize(context, block);
             inherit(superClazz);
         }
 
         return this;
     }
 
     /** rb_class_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = PRIVATE)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         checkNotInitialized();
         if (original instanceof MetaClass) throw runtime.newTypeError("can't copy singleton class");        
         
         super.initialize_copy(original);
         allocator = ((RubyClass)original).allocator; 
         return this;        
     }
 
     protected void setModuleSuperClass(RubyClass superClass) {
         // remove us from old superclass's child classes
         if (this.superClass != null) this.superClass.removeSubclass(this);
         // add us to new superclass's child classes
         superClass.addSubclass(this);
         // update superclass reference
         setSuperClass(superClass);
     }
     
     public Collection<RubyClass> subclasses(boolean includeDescendants) {
         Set<RubyClass> mySubclasses = subclasses;
         if (mySubclasses != null) {
             Collection<RubyClass> mine = new ArrayList<RubyClass>(mySubclasses);
             if (includeDescendants) {
                 for (RubyClass i: mySubclasses) {
                     mine.addAll(i.subclasses(includeDescendants));
                 }
             }
 
             return mine;
         } else {
             return Collections.EMPTY_LIST;
         }
     }
 
     /**
      * Add a new subclass to the weak set of subclasses.
      *
      * This version always constructs a new set to avoid having to synchronize
      * against the set when iterating it for invalidation in
      * invalidateCacheDescendants.
      *
      * @param subclass The subclass to add
      */
     public synchronized void addSubclass(RubyClass subclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) subclasses = oldSubclasses = new WeakHashSet<RubyClass>(4);
             oldSubclasses.add(subclass);
         }
     }
     
     /**
      * Remove a subclass from the weak set of subclasses.
      *
      * @param subclass The subclass to remove
      */
     public synchronized void removeSubclass(RubyClass subclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) return;
 
             oldSubclasses.remove(subclass);
         }
     }
 
     /**
      * Replace an existing subclass with a new one.
      *
      * @param subclass The subclass to remove
      * @param newSubclass The subclass to replace it with
      */
     public synchronized void replaceSubclass(RubyClass subclass, RubyClass newSubclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) return;
 
             oldSubclasses.remove(subclass);
             oldSubclasses.add(newSubclass);
         }
     }
 
     public void becomeSynchronized() {
         // make this class and all subclasses sync
         synchronized (getRuntime().getHierarchyLock()) {
             super.becomeSynchronized();
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.becomeSynchronized();
             }
         }
     }
 
     /**
      * Invalidate all subclasses of this class by walking the set of all
      * subclasses and asking them to invalidate themselves.
      *
      * Note that this version works against a reference to the current set of
      * subclasses, which could be replaced by the time this iteration is
      * complete. In theory, there may be a path by which invalidation would
      * miss a class added during the invalidation process, but the exposure is
      * minimal if it exists at all. The only way to prevent it would be to
      * synchronize both invalidation and subclass set modification against a
      * global lock, which we would like to avoid.
      */
     @Override
     public void invalidateCacheDescendants() {
         super.invalidateCacheDescendants();
 
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.invalidateCacheDescendants();
             }
         }
     }
     
     public void addInvalidatorsAndFlush(List<Invalidator> invalidators) {
         // add this class's invalidators to the aggregate
         invalidators.add(methodInvalidator);
         
         // if we're not at boot time, don't bother fully clearing caches
         if (!runtime.isBooting()) cachedMethods.clear();
 
         // no subclasses, don't bother with lock and iteration
         if (subclasses == null || subclasses.isEmpty()) return;
         
         // cascade into subclasses
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.addInvalidatorsAndFlush(invalidators);
             }
         }
     }
     
     public Ruby getClassRuntime() {
         return runtime;
     }
 
     public final RubyClass getRealClass() {
         return realClass;
     }    
 
     @JRubyMethod(name = "inherited", required = 1, visibility = PRIVATE)
     public IRubyObject inherited(ThreadContext context, IRubyObject arg) {
         return runtime.getNil();
     }
 
     /** rb_class_inherited (reversed semantics!)
      * 
      */
     public void inherit(RubyClass superClazz) {
         if (superClazz == null) superClazz = runtime.getObject();
 
         if (getRuntime().getNil() != null) {
             superClazz.invokeInherited(runtime.getCurrentContext(), superClazz, this);
         }
     }
 
     /** Return the real super class of this class.
      * 
      * rb_class_superclass
      *
      */    
     @JRubyMethod(name = "superclass")
     public IRubyObject superclass(ThreadContext context) {
         RubyClass superClazz = superClass;
         
         if (superClazz == null) {
             if (runtime.is1_9() && metaClass == runtime.getBasicObject().getMetaClass()) return runtime.getNil();
             throw runtime.newTypeError("uninitialized class");
         }
 
         while (superClazz != null && superClazz.isIncluded()) superClazz = superClazz.superClass;
 
         return superClazz != null ? superClazz : runtime.getNil();
     }
 
     private void checkNotInitialized() {
         if (superClass != null || (runtime.is1_9() && this == runtime.getBasicObject())) {
             throw runtime.newTypeError("already initialized class");
         }
     }
     /** rb_check_inheritable
      * 
      */
     public static void checkInheritable(IRubyObject superClass) {
         if (!(superClass instanceof RubyClass)) {
             throw superClass.getRuntime().newTypeError("superclass must be a Class (" + superClass.getMetaClass() + " given)"); 
         }
         if (((RubyClass)superClass).isSingleton()) {
             throw superClass.getRuntime().newTypeError("can't make subclass of virtual class");
         }        
     }
 
     public final ObjectMarshal getMarshal() {
         return marshal;
     }
     
     public final void setMarshal(ObjectMarshal marshal) {
         this.marshal = marshal;
     }
     
     public final void marshal(Object obj, MarshalStream marshalStream) throws IOException {
         getMarshal().marshalTo(runtime, obj, this, marshalStream);
     }
     
     public final Object unmarshal(UnmarshalStream unmarshalStream) throws IOException {
         return getMarshal().unmarshalFrom(runtime, this, unmarshalStream);
     }
     
     public static void marshalTo(RubyClass clazz, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(clazz);
         output.writeString(MarshalStream.getPathFromClass(clazz));
     }
 
     public static RubyClass unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = RubyString.byteListToString(input.unmarshalString());
         RubyClass result = UnmarshalStream.getClassFromPath(input.getRuntime(), name);
         input.registerLinkTarget(result);
         return result;
     }
 
     protected static final ObjectMarshal DEFAULT_OBJECT_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             IRubyObject object = (IRubyObject)obj;
             
             marshalStream.registerLinkTarget(object);
             marshalStream.dumpVariables(object.getVariableList());
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             IRubyObject result = type.allocate();
             
             unmarshalStream.registerLinkTarget(result);
 
             unmarshalStream.defaultVariablesUnmarshal(result);
 
             return result;
         }
     };
 
     /**
      * Whether this class can be reified into a Java class. Currently only objects
      * that descend from Object (or descend from Ruby-based classes that descend
      * from Object) can be reified.
      *
      * @return true if the class can be reified, false otherwise
      */
     public boolean isReifiable() {
         RubyClass realSuper = null;
 
         // already reified is not reifiable
         if (reifiedClass != null) return false;
 
         // root classes are not reifiable
         if (superClass == null || (realSuper = superClass.getRealClass()) == null) return false;
 
         Class reifiedSuper = realSuper.reifiedClass;
         
         // if super has been reified or is a native class
         if (reifiedSuper != null) {
             
             // super must be Object, BasicObject, or a reified user class
             return reifiedSuper == RubyObject.class ||
                     reifiedSuper == RubyBasicObject.class ||
                     Reified.class.isAssignableFrom(reifiedSuper);
         } else {
             // non-native, non-reified super; recurse
             return realSuper.isReifiable();
         }
     }
     
     public void reifyWithAncestors() {
         reifyWithAncestors(null, true);
     }
     public void reifyWithAncestors(String classDumpDir) {
         reifyWithAncestors(classDumpDir, true);
     }
     public void reifyWithAncestors(boolean useChildLoader) {
         reifyWithAncestors(null, useChildLoader);
     }
     
     /**
      * Reify this class, first reifying all its ancestors. This causes the
      * reified class and all ancestors' reified classes to come into existence,
      * so any future changes will not be reflected.
      *
      * This form also accepts a string argument indicating a path in which to dump
      * the intermediate reified class bytes.
      *
      * @param classDumpDir the path in which to dump reified class bytes
      * @param useChildLoader whether to load the class into its own child classloader
      */
     public void reifyWithAncestors(String classDumpDir, boolean useChildLoader) {
         if (isReifiable()) {
             RubyClass realSuper = getSuperClass().getRealClass();
 
             if (realSuper.reifiedClass == null) realSuper.reifyWithAncestors(classDumpDir, useChildLoader);
             reify(classDumpDir, useChildLoader);
         }
     }
 
     private static final boolean DEBUG_REIFY = false;
 
     public synchronized void reify() {
         reify(null, true);
     }
     public synchronized void reify(String classDumpDir) {
         reify(classDumpDir, true);
     }
     public synchronized void reify(boolean useChildLoader) {
         reify(null, useChildLoader);
     }
 
     /**
      * Stand up a real Java class for the backing store of this object
      * @param classDumpDir Directory to save reified java class
      */
     public synchronized void reify(String classDumpDir, boolean useChildLoader) {
         Class reifiedParent = RubyObject.class;
 
         // calculate an appropriate name, using "Anonymous####" if none is present
         String name;
         if (getBaseName() == null) {
             name = "AnonymousRubyClass__" + id;
         } else {
             name = getName();
         }
         
         String javaName = "rubyobj." + name.replaceAll("::", ".");
         String javaPath = "rubyobj/" + name.replaceAll("::", "/");
         ClassDefiningClassLoader parentCL;
         Class parentReified = superClass.getRealClass().getReifiedClass();
         if (parentReified == null) {
             throw getClassRuntime().newTypeError("class " + getName() + " parent class is not yet reified");
         }
         
         if (parentReified.getClassLoader() instanceof OneShotClassLoader) {
             parentCL = (OneShotClassLoader)superClass.getRealClass().getReifiedClass().getClassLoader();
         } else {
             if (useChildLoader) {
                 parentCL = new OneShotClassLoader(runtime.getJRubyClassLoader());
             } else {
                 parentCL = runtime.getJRubyClassLoader();
             }
         }
 
         if (superClass.reifiedClass != null) {
             reifiedParent = superClass.reifiedClass;
         }
 
         Class[] interfaces = Java.getInterfacesFromRubyClass(this);
         String[] interfaceNames = new String[interfaces.length + 1];
         
         // mark this as a Reified class
         interfaceNames[0] = p(Reified.class);
 
         // add the other user-specified interfaces
         for (int i = 0; i < interfaces.length; i++) {
             interfaceNames[i + 1] = p(interfaces[i]);
         }
 
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, javaPath, null, p(reifiedParent),
                 interfaceNames);
 
         if (classAnnotations != null && classAnnotations.size() != 0) {
             for (Map.Entry<Class,Map<String,Object>> entry : classAnnotations.entrySet()) {
                 Class annoType = entry.getKey();
                 Map<String,Object> fields = entry.getValue();
 
                 AnnotationVisitor av = cw.visitAnnotation(ci(annoType), true);
                 CodegenUtils.visitAnnotationFields(av, fields);
                 av.visitEnd();
             }
         }
 
         // fields to hold Ruby and RubyClass references
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "ruby", ci(Ruby.class), null, null);
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "rubyClass", ci(RubyClass.class), null, null);
 
         // static initializing method
         SkinnyMethodAdapter m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_STATIC, "clinit", sig(void.class, Ruby.class, RubyClass.class), null, null);
         m.start();
         m.aload(0);
         m.putstatic(javaPath, "ruby", ci(Ruby.class));
         m.aload(1);
         m.putstatic(javaPath, "rubyClass", ci(RubyClass.class));
         m.voidreturn();
         m.end();
 
         // standard constructor that accepts Ruby, RubyClass
         m = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "<init>", sig(void.class, Ruby.class, RubyClass.class), null, null);
         m.aload(0);
         m.aload(1);
         m.aload(2);
         m.invokespecial(p(reifiedParent), "<init>", sig(void.class, Ruby.class, RubyClass.class));
         m.voidreturn();
         m.end();
 
         // no-arg constructor using static references to Ruby and RubyClass
         m = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "<init>", CodegenUtils.sig(void.class), null, null);
         m.aload(0);
         m.getstatic(javaPath, "ruby", ci(Ruby.class));
         m.getstatic(javaPath, "rubyClass", ci(RubyClass.class));
         m.invokespecial(p(reifiedParent), "<init>", sig(void.class, Ruby.class, RubyClass.class));
         m.voidreturn();
         m.end();
 
         // gather a list of instance methods, so we don't accidentally make static ones that conflict
         Set<String> instanceMethods = new HashSet<String>();
 
         // define instance methods
         for (Map.Entry<String,DynamicMethod> methodEntry : getMethods().entrySet()) {
             String methodName = methodEntry.getKey();
 
             if (!JavaNameMangler.willMethodMangleOk(methodName)) continue;
 
             String javaMethodName = JavaNameMangler.mangleMethodName(methodName);
 
             Map<Class,Map<String,Object>> methodAnnos = getMethodAnnotations().get(methodName);
             List<Map<Class,Map<String,Object>>> parameterAnnos = getParameterAnnotations().get(methodName);
             Class[] methodSignature = getMethodSignatures().get(methodName);
 
             String signature;
             if (methodSignature == null) {
                 // non-signature signature with just IRubyObject
                 switch (methodEntry.getValue().getArity().getValue()) {
                 case 0:
                     signature = sig(IRubyObject.class);
                     m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS, javaMethodName, signature, null, null);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.aload(0);
                     m.ldc(methodName);
                     m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class));
                     break;
                 default:
                     signature = sig(IRubyObject.class, IRubyObject[].class);
                     m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS, javaMethodName, signature, null, null);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.aload(0);
                     m.ldc(methodName);
                     m.aload(1);
                     m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class));
                 }
                 m.areturn();
             } else {
                 // generate a real method signature for the method, with to/from coercions
 
                 // indices for temp values
                 Class[] params = new Class[methodSignature.length - 1];
                 System.arraycopy(methodSignature, 1, params, 0, params.length);
                 int baseIndex = 1;
                 for (Class paramType : params) {
                     if (paramType == double.class || paramType == long.class) {
                         baseIndex += 2;
                     } else {
                         baseIndex += 1;
                     }
                 }
                 int rubyIndex = baseIndex;
 
                 signature = sig(methodSignature[0], params);
                 m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS, javaMethodName, signature, null, null);
                 generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                 m.getstatic(javaPath, "ruby", ci(Ruby.class));
                 m.astore(rubyIndex);
 
                 m.aload(0); // self
                 m.ldc(methodName); // method name
                 RealClassGenerator.coerceArgumentsToRuby(m, params, rubyIndex);
                 m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class));
 
                 RealClassGenerator.coerceResultAndReturn(m, methodSignature[0]);
             }
 
             if (DEBUG_REIFY) LOG.debug("defining {}#{} as {}#{}", getName(), methodName, javaName, javaMethodName + signature);
 
             instanceMethods.add(javaMethodName + signature);
 
             m.end();
         }
 
         // define class/static methods
         for (Map.Entry<String,DynamicMethod> methodEntry : getMetaClass().getMethods().entrySet()) {
             String methodName = methodEntry.getKey();
 
             if (!JavaNameMangler.willMethodMangleOk(methodName)) continue;
 
             String javaMethodName = JavaNameMangler.mangleMethodName(methodName);
 
             Map<Class,Map<String,Object>> methodAnnos = getMetaClass().getMethodAnnotations().get(methodName);
             List<Map<Class,Map<String,Object>>> parameterAnnos = getMetaClass().getParameterAnnotations().get(methodName);
             Class[] methodSignature = getMetaClass().getMethodSignatures().get(methodName);
 
             String signature;
             if (methodSignature == null) {
                 // non-signature signature with just IRubyObject
                 switch (methodEntry.getValue().getArity().getValue()) {
                 case 0:
                     signature = sig(IRubyObject.class);
                     if (instanceMethods.contains(javaMethodName + signature)) continue;
                     m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS | ACC_STATIC, javaMethodName, signature, null, null);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.getstatic(javaPath, "rubyClass", ci(RubyClass.class));
                     //m.invokevirtual("org/jruby/RubyClass", "getMetaClass", sig(RubyClass.class) );
                     m.ldc(methodName); // Method name
                     m.invokevirtual("org/jruby/RubyClass", "callMethod", sig(IRubyObject.class, String.class) );
                     break;
                 default:
                     signature = sig(IRubyObject.class, IRubyObject[].class);
                     if (instanceMethods.contains(javaMethodName + signature)) continue;
                     m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS | ACC_STATIC, javaMethodName, signature, null, null);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.getstatic(javaPath, "rubyClass", ci(RubyClass.class));
                     m.ldc(methodName); // Method name
                     m.aload(0);
                     m.invokevirtual("org/jruby/RubyClass", "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class) );
                 }
                 m.areturn();
             } else {
                 // generate a real method signature for the method, with to/from coercions
 
                 // indices for temp values
                 Class[] params = new Class[methodSignature.length - 1];
                 System.arraycopy(methodSignature, 1, params, 0, params.length);
                 int baseIndex = 0;
                 for (Class paramType : params) {
                     if (paramType == double.class || paramType == long.class) {
                         baseIndex += 2;
                     } else {
                         baseIndex += 1;
                     }
                 }
                 int rubyIndex = baseIndex;
 
                 signature = sig(methodSignature[0], params);
                 if (instanceMethods.contains(javaMethodName + signature)) continue;
                 m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS | ACC_STATIC, javaMethodName, signature, null, null);
                 generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                 m.getstatic(javaPath, "ruby", ci(Ruby.class));
                 m.astore(rubyIndex);
 
                 m.getstatic(javaPath, "rubyClass", ci(RubyClass.class));
                 
                 m.ldc(methodName); // method name
                 RealClassGenerator.coerceArgumentsToRuby(m, params, rubyIndex);
                 m.invokevirtual("org/jruby/RubyClass", "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class));
 
                 RealClassGenerator.coerceResultAndReturn(m, methodSignature[0]);
             }
 
             if (DEBUG_REIFY) LOG.debug("defining {}.{} as {}.{}", getName(), methodName, javaName, javaMethodName + signature);
 
             m.end();
         }
 
 
         cw.visitEnd();
         byte[] classBytes = cw.toByteArray();
         dumpReifiedClass(classDumpDir, javaPath, classBytes);
         Class result = parentCL.defineClass(javaName, classBytes);
 
         try {
             java.lang.reflect.Method clinit = result.getDeclaredMethod("clinit", Ruby.class, RubyClass.class);
             clinit.invoke(null, runtime, this);
         } catch (Exception e) {
             if (RubyInstanceConfig.REIFY_LOG_ERRORS) {
                 LOG.error("failed to reify class " + getName() + " due to:\n");
                 LOG.error(e);
             }
         }
 
         setClassAllocator(result);
         reifiedClass = result;
     }
 
     public void setReifiedClass(Class newReifiedClass) {
         this.reifiedClass = newReifiedClass;
     }
 
     public Class getReifiedClass() {
         return reifiedClass;
     }
 
     public Map<String, List<Map<Class, Map<String,Object>>>> getParameterAnnotations() {
         if (parameterAnnotations == null) return Collections.EMPTY_MAP;
         return parameterAnnotations;
     }
 
     public synchronized void addParameterAnnotation(String method, int i, Class annoClass, Map<String,Object> value) {
         if (parameterAnnotations == null) parameterAnnotations = new Hashtable<String,List<Map<Class,Map<String,Object>>>>();
         List<Map<Class,Map<String,Object>>> paramList = parameterAnnotations.get(method);
         if (paramList == null) {
             paramList = new ArrayList<Map<Class,Map<String,Object>>>(i + 1);
             parameterAnnotations.put(method, paramList);
         }
         if (paramList.size() < i + 1) {
             for (int j = paramList.size(); j < i + 1; j++) {
                 paramList.add(null);
             }
         }
         if (annoClass != null && value != null) {
             Map<Class, Map<String, Object>> annos = paramList.get(i);
             if (annos == null) {
                 annos = new HashMap<Class, Map<String, Object>>();
                 paramList.set(i, annos);
             }
             annos.put(annoClass, value);
         } else {
             paramList.set(i, null);
         }
     }
 
     public Map<String,Map<Class,Map<String,Object>>> getMethodAnnotations() {
         if (methodAnnotations == null) return Collections.EMPTY_MAP;
 
         return methodAnnotations;
     }
 
     public synchronized void addMethodAnnotation(String methodName, Class annotation, Map fields) {
         if (methodAnnotations == null) methodAnnotations = new Hashtable<String,Map<Class,Map<String,Object>>>();
 
         Map<Class,Map<String,Object>> annos = methodAnnotations.get(methodName);
         if (annos == null) {
             annos = new Hashtable<Class,Map<String,Object>>();
             methodAnnotations.put(methodName, annos);
         }
 
         annos.put(annotation, fields);
     }
 
     public Map<String,Class[]> getMethodSignatures() {
         if (methodSignatures == null) return Collections.EMPTY_MAP;
 
         return methodSignatures;
     }
 
     public synchronized void addMethodSignature(String methodName, Class[] types) {
         if (methodSignatures == null) methodSignatures = new Hashtable<String,Class[]>();
 
         methodSignatures.put(methodName, types);
     }
 
     public Map<Class,Map<String,Object>> getClassAnnotations() {
         if (classAnnotations == null) return Collections.EMPTY_MAP;
 
         return classAnnotations;
     }
 
     public synchronized void addClassAnnotation(Class annotation, Map fields) {
         if (classAnnotations == null) classAnnotations = new Hashtable<Class,Map<String,Object>>();
 
         classAnnotations.put(annotation, fields);
     }
 
     @Override
     public Object toJava(Class klass) {
         Class returnClass = null;
 
         if (klass == Class.class) {
             // Class requested; try java_class or else return nearest reified class
             if (respondsTo("java_class")) {
                 return callMethod("java_class").toJava(klass);
             } else {
                 for (RubyClass current = this; current != null; current = current.getSuperClass()) {
                     returnClass = current.getReifiedClass();
                     if (returnClass != null) return returnClass;
                 }
             }
             // should never fall through, since RubyObject has a reified class
         }
 
         if (klass.isAssignableFrom(RubyClass.class)) {
             // they're asking for something RubyClass extends, give them that
             return this;
         }
 
         return super.toJava(klass);
     }
 
     /**
      * An enum defining the type of marshaling a given class's objects employ.
      */
     private static enum MarshalType {
         DEFAULT, NEW_USER, OLD_USER, DEFAULT_SLOW, NEW_USER_SLOW, USER_SLOW
     }
 
     /**
      * A tuple representing the mechanism by which objects should be marshaled.
      *
      * This tuple caches the type of marshaling to perform (from @MarshalType),
      * the method to be used for marshaling data (either marshal_load/dump or
      * _load/_dump), and the generation of the class at the time this tuple was
      * created. When "dump" or "load" are invoked, they either call the default
      * marshaling logic (@MarshalType.DEFAULT) or they further invoke the cached
      * marshal_dump/load or _dump/_load methods to marshal the data.
      *
      * It is expected that code outside MarshalTuple will validate that the
      * generation number still matches before invoking load or dump.
      */
     private static class MarshalTuple {
         /**
          * Construct a new MarshalTuple with the given values.
          *
          * @param method The method to invoke, or null in the case of default
          * marshaling.
          * @param type The type of marshaling to perform, from @MarshalType
          * @param generation The generation of the associated class at the time
          * of creation.
          */
         public MarshalTuple(DynamicMethod method, MarshalType type, int generation) {
             this.method = method;
             this.type = type;
             this.generation = generation;
         }
 
         /**
          * Dump the given object to the given stream, using the appropriate
          * marshaling method.
          *
          * @param stream The stream to which to dump
          * @param object The object to dump
          * @throws IOException If there is an IO error during dumping
          */
         public void dump(MarshalStream stream, IRubyObject object) throws IOException {
             switch (type) {
                 case DEFAULT:
                     stream.writeDirectly(object);
                     return;
                 case NEW_USER:
                     stream.userNewMarshal(object, method);
                     return;
                 case OLD_USER:
                     stream.userMarshal(object, method);
                     return;
                 case DEFAULT_SLOW:
                     if (object.respondsTo("marshal_dump")) {
                         stream.userNewMarshal(object);
                     } else if (object.respondsTo("_dump")) {
                         stream.userMarshal(object);
                     } else {
                         stream.writeDirectly(object);
                     }
                     return;
             }
         }
 
         /** A "null" tuple, used as the default value for caches. */
         public static final MarshalTuple NULL_TUPLE = new MarshalTuple(null, null, 0);
         /** The method associated with this tuple. */
         public final DynamicMethod method;
         /** The type of marshaling that will be performed */
         public final MarshalType type;
         /** The generation of the associated class at the time of creation */
         public final int generation;
     }
 
     /**
      * Marshal the given object to the marshaling stream, being "smart" and
      * caching how to do that marshaling.
      *
      * If the class defines a custom "respond_to?" method, then the behavior of
      * dumping could vary without our class structure knowing it. As a result,
      * we do only the slow-path classic behavior.
      *
      * If the class defines a real "marshal_dump" method, we cache and use that.
      *
      * If the class defines a real "_dump" method, we cache and use that.
      *
      * If the class neither defines none of the above methods, we use a fast
      * path directly to the default dumping logic.
      *
      * @param stream The stream to which to marshal the data
      * @param target The object whose data should be marshaled
      * @throws IOException If there is an IO exception while writing to the
      * stream.
      */
     public void smartDump(MarshalStream stream, IRubyObject target) throws IOException {
         MarshalTuple tuple;
         if ((tuple = cachedDumpMarshal).generation == generation) {
         } else {
             // recache
             DynamicMethod method = searchMethod("respond_to?");
-            if (method != runtime.getRespondToMethod() && !method.isUndefined()) {
+            if (!method.equals(runtime.getRespondToMethod()) && !method.isUndefined()) {
 
                 // custom respond_to?, always do slow default marshaling
                 tuple = (cachedDumpMarshal = new MarshalTuple(null, MarshalType.DEFAULT_SLOW, generation));
 
             } else if (!(method = searchMethod("marshal_dump")).isUndefined()) {
 
                 // object really has 'marshal_dump', cache "new" user marshaling
                 tuple = (cachedDumpMarshal = new MarshalTuple(method, MarshalType.NEW_USER, generation));
 
             } else if (!(method = searchMethod("_dump")).isUndefined()) {
 
                 // object really has '_dump', cache "old" user marshaling
                 tuple = (cachedDumpMarshal = new MarshalTuple(method, MarshalType.OLD_USER, generation));
 
             } else {
 
                 // no respond_to?, marshal_dump, or _dump, so cache default marshaling
                 tuple = (cachedDumpMarshal = new MarshalTuple(null, MarshalType.DEFAULT, generation));
             }
         }
 
         tuple.dump(stream, target);
     }
 
     /**
      * Load marshaled data into a blank target object using marshal_load, being
      * "smart" and caching the mechanism for invoking marshal_load.
      *
      * If the class implements a custom respond_to?, cache nothing and go slow
      * path invocation of respond_to? and marshal_load every time. Raise error
      * if respond_to? :marshal_load returns true and no :marshal_load is
      * defined.
      *
      * If the class implements marshal_load, cache and use that.
      *
      * Otherwise, error, since marshal_load is not present.
      *
      * @param target The blank target object into which marshal_load will
      * deserialize the given data
      * @param data The marshaled data
      * @return The fully-populated target object
      */
     public IRubyObject smartLoadNewUser(IRubyObject target, IRubyObject data) {
         ThreadContext context = runtime.getCurrentContext();
         CacheEntry cache;
         if ((cache = cachedLoad).token == generation) {
             cache.method.call(context, target, this, "marshal_load", data);
             return target;
         } else {
             DynamicMethod method = searchMethod("respond_to?");
-            if (method != runtime.getRespondToMethod() && !method.isUndefined()) {
+            if (!method.equals(runtime.getRespondToMethod()) && !method.isUndefined()) {
 
                 // custom respond_to?, cache nothing and use slow path
                 if (method.call(context, target, this, "respond_to?", runtime.newSymbol("marshal_load")).isTrue()) {
                     target.callMethod(context, "marshal_load", data);
                     return target;
                 } else {
                     throw runtime.newTypeError("class " + getName() + " needs to have method `marshal_load'");
                 }
 
             } else if (!(cache = searchWithCache("marshal_load")).method.isUndefined()) {
 
                 // real marshal_load defined, cache and call it
                 cachedLoad = cache;
                 cache.method.call(context, target, this, "marshal_load", data);
                 return target;
 
             } else {
 
                 // go ahead and call, method_missing might handle it
                 target.callMethod(context, "marshal_load", data);
                 return target;
                 
             }
         }
     }
 
 
     /**
      * Load marshaled data into a blank target object using _load, being
      * "smart" and caching the mechanism for invoking _load.
      *
      * If the metaclass implements custom respond_to?, cache nothing and go slow
      * path invocation of respond_to? and _load every time. Raise error if
      * respond_to? :_load returns true and no :_load is defined.
      *
      * If the metaclass implements _load, cache and use that.
      *
      * Otherwise, error, since _load is not present.
      *
      * @param data The marshaled data, to be reconstituted into an object by
      * _load
      * @return The fully-populated target object
      */
     public IRubyObject smartLoadOldUser(IRubyObject data) {
         ThreadContext context = runtime.getCurrentContext();
         CacheEntry cache;
         if ((cache = getSingletonClass().cachedLoad).token == getSingletonClass().generation) {
             return cache.method.call(context, this, getSingletonClass(), "_load", data);
         } else {
             DynamicMethod method = getSingletonClass().searchMethod("respond_to?");
-            if (method != runtime.getRespondToMethod() && !method.isUndefined()) {
+            if (!method.equals(runtime.getRespondToMethod()) && !method.isUndefined()) {
 
                 // custom respond_to?, cache nothing and use slow path
                 if (method.call(context, this, getSingletonClass(), "respond_to?", runtime.newSymbol("_load")).isTrue()) {
                     return callMethod(context, "_load", data);
                 } else {
                     throw runtime.newTypeError("class " + getName() + " needs to have method `_load'");
                 }
 
             } else if (!(cache = getSingletonClass().searchWithCache("_load")).method.isUndefined()) {
 
                 // real _load defined, cache and call it
                 getSingletonClass().cachedLoad = cache;
                 return cache.method.call(context, this, getSingletonClass(), "_load", data);
 
             } else {
 
                 // provide an error, since it doesn't exist
                 throw runtime.newTypeError("class " + getName() + " needs to have method `_load'");
 
             }
         }
     }
 
     protected final Ruby runtime;
     private ObjectAllocator allocator; // the default allocator
     protected ObjectMarshal marshal;
     private Set<RubyClass> subclasses;
     public static final int CS_IDX_INITIALIZE = 0;
     public static final String[] CS_NAMES = {
         "initialize"
     };
     private final CallSite[] baseCallSites = new CallSite[CS_NAMES.length];
     {
         for(int i = 0; i < CS_NAMES.length; i++) {
             baseCallSites[i] = MethodIndex.getFunctionalCallSite(CS_NAMES[i]);
         }
     }
 
     private CallSite[] extraCallSites;
 
     private Class reifiedClass;
 
     @SuppressWarnings("unchecked")
     private static String[] EMPTY_STRING_ARRAY = new String[0];
     private Map<String, VariableAccessor> variableAccessors = (Map<String, VariableAccessor>)Collections.EMPTY_MAP;
     private volatile String[] variableNames = EMPTY_STRING_ARRAY;
 
     private volatile boolean hasObjectID = false;
     public boolean hasObjectID() {
         return hasObjectID;
     }
 
     private Map<String, List<Map<Class, Map<String,Object>>>> parameterAnnotations;
 
     private Map<String, Map<Class, Map<String,Object>>> methodAnnotations;
 
     private Map<String, Class[]> methodSignatures;
 
     private Map<Class, Map<String,Object>> classAnnotations;
 
     /** A cached tuple of method, type, and generation for dumping */
     private MarshalTuple cachedDumpMarshal = MarshalTuple.NULL_TUPLE;
 
     /** A cached tuple of method and generation for marshal loading */
     private CacheEntry cachedLoad = CacheEntry.NULL_CACHE;
 
     /** The "real" class, used by includes and singletons to locate the actual type of the object */
     private final RubyClass realClass;
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 8768ed491d..28b1768651 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1153 +1,1165 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Kiel Hodges <jruby-devel@selfsosoft.com>
  * Copyright (C) 2006 Evan Buswell <evan@heron.sytes.net>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
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
 
 import java.io.ByteArrayOutputStream;
 import java.util.ArrayList;
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.anno.FrameField.*;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodNBlock;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.backtrace.RubyStackTraceElement;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.util.ByteList;
 import org.jruby.util.ConvertBytes;
 import org.jruby.util.IdUtil;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.cli.Options;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  */
 @JRubyModule(name="Kernel")
 public class RubyKernel {
     public final static Class<?> IRUBY_OBJECT = IRubyObject.class;
 
     public static abstract class MethodMissingMethod extends JavaMethodNBlock {
         public MethodMissingMethod(RubyModule implementationClass) {
             super(implementationClass, Visibility.PRIVATE, CallConfiguration.FrameFullScopeNone);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             try {
                 preFrameOnly(context, self, name, block);
                 return methodMissing(context, self, clazz, name, args, block);
             } finally {
                 postFrameOnly(context);
             }
         }
 
         public abstract IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block);
 
     }
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         runtime.setKernel(module);
 
         module.defineAnnotatedMethods(RubyKernel.class);
         
-        runtime.setRespondToMethod(module.searchMethod("respond_to?"));
-        
         module.setFlag(RubyObject.USER7_F, false); //Kernel is the only Module that doesn't need an implementor
 
         runtime.setPrivateMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PRIVATE, CallType.NORMAL, args, block);
             }
         });
 
         runtime.setProtectedMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PROTECTED, CallType.NORMAL, args, block);
             }
         });
 
         runtime.setVariableMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PUBLIC, CallType.VARIABLE, args, block);
             }
         });
 
         runtime.setSuperMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PUBLIC, CallType.SUPER, args, block);
             }
         });
 
         runtime.setNormalMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PUBLIC, CallType.NORMAL, args, block);
             }
         });
 
+        recacheBuiltinMethods(runtime);
+
+        return module;
+    }
+
+    /**
+     * Cache built-in versions of several core methods, to improve performance by using identity comparison (==) rather
+     * than going ahead with dynamic dispatch.
+     *
+     * @param runtime
+     */
+    static void recacheBuiltinMethods(Ruby runtime) {
+        RubyModule module = runtime.getKernel();
+
+        runtime.setRespondToMethod(module.searchMethod("respond_to?"));
+
         if (!runtime.is1_9()) { // method_missing is in BasicObject in 1.9
             runtime.setDefaultMethodMissing(module.searchMethod("method_missing"));
         }
-
-        return module;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject at_exit(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().pushExitBlock(context.getRuntime().newProc(Block.Type.PROC, block));
     }
 
     @JRubyMethod(name = "autoload?", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject autoload_p(ThreadContext context, final IRubyObject recv, IRubyObject symbol) {
         Ruby runtime = context.getRuntime();
         final RubyModule module = getModuleForAutoload(runtime, recv);
         String name = symbol.asJavaString();
         
         String file = module.getAutoloadFile(name);
         return (file == null) ? runtime.getNil() : runtime.newString(file);
     }
 
     @JRubyMethod(required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         Ruby runtime = recv.getRuntime(); 
         String nonInternedName = symbol.asJavaString();
         
         if (!IdUtil.isValidConstantName(nonInternedName)) {
             throw runtime.newNameError("autoload must be constant name", nonInternedName);
         }
 
         if (!runtime.is1_9() && !(file instanceof RubyString)) throw runtime.newTypeError(file, runtime.getString());
 
         RubyString fileString = RubyFile.get_path(runtime.getCurrentContext(), file);
         
         if (fileString.isEmpty()) throw runtime.newArgumentError("empty file name");
         
         final String baseName = symbol.asJavaString().intern(); // interned, OK for "fast" methods
         final RubyModule module = getModuleForAutoload(runtime, recv);
         
         IRubyObject existingValue = module.fetchConstant(baseName); 
         if (existingValue != null && existingValue != RubyObject.UNDEF) return runtime.getNil();
 
         module.defineAutoload(baseName, new IAutoloadMethod() {
             public String file() {
                 return file.toString();
             }
 
             public void load(Ruby runtime) {
                 if (runtime.getLoadService().autoloadRequire(file())) {
                     // Do not finish autoloading by cyclic autoload 
                     module.finishAutoload(baseName);
                 }
             }
         });
         return runtime.getNil();
     }
 
     private static RubyModule getModuleForAutoload(Ruby runtime, IRubyObject recv) {
         RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : recv.getMetaClass().getRealClass();
         if (module == runtime.getKernel()) {
             // special behavior if calling Kernel.autoload directly
             if (runtime.is1_9()) {
                 module = runtime.getObject().getSingletonClass();
             } else {
                 module = runtime.getObject();
             }
         }
         return module;
     }
 
     @JRubyMethod(rest = true, frame = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject method_missing(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Visibility lastVis = context.getLastVisibility();
         CallType lastCallType = context.getLastCallType();
 
         if (args.length == 0 || !(args[0] instanceof RubySymbol)) throw context.getRuntime().newArgumentError("no id given");
 
         return methodMissingDirect(context, recv, (RubySymbol)args[0], lastVis, lastCallType, args, block);
     }
 
     protected static IRubyObject methodMissingDirect(ThreadContext context, IRubyObject recv, RubySymbol symbol, Visibility lastVis, CallType lastCallType, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         
         // create a lightweight thunk
         IRubyObject msg = new RubyNameError.RubyNameErrorMessage(runtime,
                                                                  recv,
                                                                  symbol,
                                                                  lastVis,
                                                                  lastCallType);
         final IRubyObject[]exArgs;
         final RubyClass exc;
         if (lastCallType != CallType.VARIABLE) {
             exc = runtime.getNoMethodError();
             exArgs = new IRubyObject[]{msg, symbol, RubyArray.newArrayNoCopy(runtime, args, 1)};
         } else {
             exc = runtime.getNameError();
             exArgs = new IRubyObject[]{msg, symbol};
         }
 
         throw new RaiseException((RubyException)exc.newInstance(context, exArgs, Block.NULL_BLOCK));
     }
 
     private static IRubyObject methodMissing(ThreadContext context, IRubyObject recv, String name, Visibility lastVis, CallType lastCallType, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         // TODO: pass this in?
         RubySymbol symbol = runtime.newSymbol(name);
 
         // create a lightweight thunk
         IRubyObject msg = new RubyNameError.RubyNameErrorMessage(runtime,
                                                                  recv,
                                                                  symbol,
                                                                  lastVis,
                                                                  lastCallType);
         final IRubyObject[]exArgs;
         final RubyClass exc;
         if (lastCallType != CallType.VARIABLE) {
             exc = runtime.getNoMethodError();
             exArgs = new IRubyObject[]{msg, symbol, RubyArray.newArrayNoCopy(runtime, args)};
         } else {
             exc = runtime.getNameError();
             exArgs = new IRubyObject[]{msg, symbol};
         }
 
         throw new RaiseException((RubyException)exc.newInstance(context, exArgs, Block.NULL_BLOCK));
     }
 
     @JRubyMethod(required = 1, optional = 2, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         String arg = args[0].convertToString().toString();
         Ruby runtime = context.getRuntime();
 
         if (arg.startsWith("|")) {
             IRubyObject command = runtime.newString(arg.substring(1));
 
             final IRubyObject[] popenArgs;
             if (args.length >= 2) {
                 popenArgs = new IRubyObject[] { command, args[1] };
             } else {
                 popenArgs = new IRubyObject[] { command };
             }
 
             // exec process, create IO with process
             return RubyIO.popen(context, runtime.getIO(), popenArgs, block);
         } 
 
         return RubyFile.open(context, runtime.getFile(), args, block);
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject open19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         if (args[0].respondsTo("to_open")) {
             args[0] = args[0].callMethod(context, "to_open");
             return RubyFile.open(context, runtime.getFile(), args, block);
         } else {
             return open(context, recv, args, block);
         }
     }
 
     @JRubyMethod(name = "getc", module = true, visibility = PRIVATE)
     public static IRubyObject getc(ThreadContext context, IRubyObject recv) {
         context.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "getc is obsolete; use STDIN.getc instead");
         IRubyObject defin = context.getRuntime().getGlobalVariables().get("$stdin");
         return defin.callMethod(context, "getc");
     }
 
     @JRubyMethod(name = "gets", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject gets(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.gets(context, context.getRuntime().getArgsFile(), args);
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject abort(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         if(args.length == 1) {
             runtime.getGlobalVariables().get("$stderr").callMethod(context,"puts",args[0].convertToString());
         }
         
         exit(runtime, new IRubyObject[] { runtime.getFalse() }, false);
         return runtime.getNil(); // not reached
     }
 
     @JRubyMethod(name = "Array", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_array(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return RuntimeHelpers.arrayValue(context, context.getRuntime(), object);
     }
 
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert");
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg);
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg0, arg1);
     }
     
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert");
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg);
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg0, arg1);
     }
 
     @JRubyMethod(name = "Float", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyFloat new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return (RubyFloat)object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString) object).getByteList().getRealSize() == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = (RubyFloat)TypeConverter.convertToType(object, recv.getRuntime().getFloat(), "to_f");
             if (Double.isNaN(rFloat.getDoubleValue())) throw recv.getRuntime().newArgumentError("invalid value for Float()");
             return rFloat;
         }
     }
 
     @JRubyMethod(name = "Float", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyFloat new_float19(IRubyObject recv, IRubyObject object) {
         Ruby runtime = recv.getRuntime();
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(runtime, ((RubyFixnum)object).getDoubleValue());
         } else if (object instanceof RubyFloat) {
             return (RubyFloat)object;
         } else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(runtime, RubyBignum.big2dbl((RubyBignum)object));
         } else if(object instanceof RubyString){
             if(((RubyString) object).getByteList().getRealSize() == 0){ // rb_cstr_to_dbl case
                 throw runtime.newArgumentError("invalid value for Float(): " + object.inspect());
             }
             RubyString arg = (RubyString)object;
             if (arg.toString().startsWith("0x")) {
                 return ConvertBytes.byteListToInum19(runtime, arg.getByteList(), 16, true).toFloat();
             }
             return RubyNumeric.str2fnum19(runtime, arg,true);
         } else if(object.isNil()){
             throw runtime.newTypeError("can't convert nil into Float");
         } else {
             return (RubyFloat)TypeConverter.convertToType19(object, runtime.getFloat(), "to_f");
         }
     }
     
     @JRubyMethod(name = "Hash", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_hash(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         IRubyObject tmp;
         Ruby runtime = recv.getRuntime();
         if (arg.isNil()) return RubyHash.newHash(runtime);
         tmp = TypeConverter.checkHashType(runtime, arg);
         if (tmp.isNil()) {
             if (arg instanceof RubyArray && ((RubyArray) arg).isEmpty()) {
                 return RubyHash.newHash(runtime);
             }
             throw runtime.newTypeError("can't convert " + arg.getMetaClass() + " into Hash");
         }
         return tmp;
     } 
 
     @JRubyMethod(name = "Integer", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject new_integer(ThreadContext context, IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue();
             if (val >= (double) RubyFixnum.MAX || val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(context.getRuntime(),((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(context.getRuntime(),(RubyString)object,0,true);
         }
 
         IRubyObject tmp = TypeConverter.convertToType(object, context.getRuntime().getInteger(), "to_int", false);
         if (tmp.isNil()) return object.convertToInteger("to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "Integer", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_integer19(ThreadContext context, IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue(); 
             if (val > (double) RubyFixnum.MAX && val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(context.getRuntime(),((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(context.getRuntime(),(RubyString)object,0,true);
         } else if(object instanceof RubyNil) {
             throw context.getRuntime().newTypeError("can't convert nil into Integer");
         }
         
         IRubyObject tmp = TypeConverter.convertToType(object, context.getRuntime().getInteger(), "to_int", false);
         if (tmp.isNil()) return object.convertToInteger("to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "Integer", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_integer19(ThreadContext context, IRubyObject recv, IRubyObject object, IRubyObject base) {
         int bs = RubyNumeric.num2int(base);
         if(object instanceof RubyString) {
             return RubyNumeric.str2inum(context.getRuntime(),(RubyString)object,bs,true);
         } else {
             IRubyObject tmp = object.checkStringType();
             if(!tmp.isNil()) {
                 return RubyNumeric.str2inum(context.getRuntime(),(RubyString)tmp,bs,true);
             }
         }
         throw context.getRuntime().newArgumentError("base specified for non string value");
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject new_string(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType(object, context.getRuntime().getString(), "to_s");
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_string19(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType19(object, context.getRuntime().getString(), "to_s");
     }
 
     @JRubyMethod(name = "p", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject p(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         IRubyObject defout = runtime.getGlobalVariables().get("$>");
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod(context, "write", RubyObject.inspect(context, args[i]));
                 defout.callMethod(context, "write", runtime.newString("\n"));
             }
         }
 
         IRubyObject result = runtime.getNil();
         if (runtime.is1_9()) {
             if (args.length == 1) {
                 result = args[0];
             } else if (args.length > 1) {
                 result = runtime.newArray(args);
             }
         }
 
         if (defout instanceof RubyFile) {
             ((RubyFile)defout).flush();
         }
 
         return result;
     }
 
     @JRubyMethod(name = "public_method",required = 1, module = true, compat = RUBY1_9)
     public static IRubyObject public_method(ThreadContext context, IRubyObject recv, IRubyObject symbol) {
         return recv.getMetaClass().newMethod(recv, symbol.asJavaString(), true, PUBLIC, true, false);
     }
 
     /** rb_f_putc
      */
     @JRubyMethod(name = "putc", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject putc(ThreadContext context, IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
         
         return RubyIO.putc(context, defout, ch);
     }
 
     @JRubyMethod(name = "puts", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject puts(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
         return RubyIO.puts(context, defout, args);
     }
 
     @JRubyMethod(name = "print", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject print(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
         return RubyIO.print(context, defout, args);
     }
 
     @JRubyMethod(name = "printf", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject printf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             defout.callMethod(context, "write", RubyKernel.sprintf(context, recv, args));
         }
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "readline", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject readline(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(context, recv, args);
 
         if (line.isNil()) throw context.getRuntime().newEOFError();
 
         return line;
     }
 
     @JRubyMethod(name = "readlines", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.readlines(context, context.getRuntime().getArgsFile(), args);
     }
 
     @JRubyMethod(name = "respond_to_missing?", module = true, compat = RUBY1_9)
     public static IRubyObject respond_to_missing_p(ThreadContext context, IRubyObject recv, IRubyObject symbol) {
         return context.getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "respond_to_missing?", module = true, compat = RUBY1_9)
     public static IRubyObject respond_to_missing_p(ThreadContext context, IRubyObject recv, IRubyObject symbol, IRubyObject isPrivate) {
         return context.getRuntime().getFalse();
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(ThreadContext context, Ruby runtime) {
         IRubyObject line = context.getCurrentScope().getLastLine(runtime);
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     @JRubyMethod(name = "sub!", module = true, visibility = PRIVATE, reads = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "sub!", module = true, visibility = PRIVATE, reads = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, arg1, block);
     }
 
     @JRubyMethod(name = "sub", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, arg1, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "gsub!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, arg1, block);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, arg1, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chop!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chop_bang(ThreadContext context, IRubyObject recv, Block block) {
         return getLastlineString(context, context.getRuntime()).chop_bang(context);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chop(ThreadContext context, IRubyObject recv, Block block) {
         RubyString str = getLastlineString(context, context.getRuntime());
 
         if (str.getByteList().getRealSize() > 0) {
             str = (RubyString) str.dup();
             str.chop_bang(context);
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chomp!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(context);
     }
 
     @JRubyMethod(name = "chomp!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(context, arg0);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context).isNil()) {
             return str;
         } 
 
         context.getCurrentScope().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context, arg0).isNil()) {
             return str;
         } 
 
         context.getCurrentScope().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).split(context);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0, arg1);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = {LASTLINE, BACKREF}, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject scan(ThreadContext context, IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(context, context.getRuntime()).scan(context, pattern, block);
     }
 
     @JRubyMethod(required = 1, optional = 3, module = true, visibility = PRIVATE)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyIO.select_static(context, context.getRuntime(), args);
     }
 
     @JRubyMethod(optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject sleep(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         long milliseconds;
 
         if (args.length == 0) {
             // Zero sleeps forever
             milliseconds = 0;
         } else {
             if (!(args[0] instanceof RubyNumeric)) {
                 throw context.getRuntime().newTypeError("can't convert " + args[0].getMetaClass().getName() + "into time interval");
             }
             milliseconds = (long) (args[0].convertToFloat().getDoubleValue() * 1000);
             if (milliseconds < 0) {
                 throw context.getRuntime().newArgumentError("time interval must be positive");
             } else if (milliseconds == 0) {
                 // Explicit zero in MRI returns immediately
                 return context.getRuntime().newFixnum(0);
             }
         }
         long startTime = System.currentTimeMillis();
         
         RubyThread rubyThread = context.getThread();
 
         // Spurious wakeup-loop
         do {
             long loopStartTime = System.currentTimeMillis();
             try {
                 // We break if we know this sleep was explicitly woken up/interrupted
                 if (!rubyThread.sleep(milliseconds)) break;
             } catch (InterruptedException iExcptn) {
             }
             milliseconds -= (System.currentTimeMillis() - loopStartTime);
         } while (milliseconds > 0);
 
         return context.getRuntime().newFixnum(Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
     @JRubyMethod(name = "exit", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         exit(recv.getRuntime(), args, false);
         return recv.getRuntime().getNil(); // not reached
     }
 
     @JRubyMethod(name = "exit!", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         exit(recv.getRuntime(), args, true);
         return recv.getRuntime().getNil(); // not reached
     }
 
     private static void exit(Ruby runtime, IRubyObject[] args, boolean hard) {
         runtime.secure(4);
 
         int status = hard ? 1 : 0;
 
         if (args.length > 0) {
             RubyObject argument = (RubyObject) args[0];
             if (argument instanceof RubyBoolean) {
                 status = argument.isFalse() ? 1 : 0;
             } else {
                 status = RubyNumeric.fix2int(argument);
             }
         }
 
         if (hard) {
             if (runtime.getInstanceConfig().isHardExit()) {
                 System.exit(status);
             } else {
                 throw new MainExitException(status, true);
             }
         } else {
             throw runtime.newSystemExit(status);
         }
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
     @JRubyMethod(name = "global_variables", module = true, visibility = PRIVATE)
     public static RubyArray global_variables(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         RubyArray globalVariables = runtime.newArray();
 
         for (String globalVariableName : runtime.getGlobalVariables().getNames()) {
             globalVariables.append(runtime.newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     // In 1.9, return symbols
     @JRubyMethod(name = "global_variables", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyArray global_variables19(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         RubyArray globalVariables = runtime.newArray();
 
         for (String globalVariableName : runtime.getGlobalVariables().getNames()) {
             globalVariables.append(runtime.newSymbol(globalVariableName));
         }
 
         return globalVariables;
     }
 
     /** Returns an Array with the names of all local variables.
      *
      */
     @JRubyMethod(name = "local_variables", module = true, visibility = PRIVATE)
     public static RubyArray local_variables(ThreadContext context, IRubyObject recv) {
         final Ruby runtime = context.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         for (String name: context.getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newString(name));
         }
 
         return localVariables;
     }
 
     // In 1.9, return symbols
     @JRubyMethod(name = "local_variables", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyArray local_variables19(ThreadContext context, IRubyObject recv) {
         final Ruby runtime = context.getRuntime();
         RubyArray localVariables = runtime.newArray();
 
         for (String name: context.getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newSymbol(name));
         }
 
         return localVariables;
     }
     
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static RubyBinding binding(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime(), context.currentBinding(recv));
     }
     
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyBinding binding19(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime(), context.currentBinding());
     }
 
     @JRubyMethod(name = {"block_given?", "iterator?"}, module = true, visibility = PRIVATE)
     public static RubyBoolean block_given_p(ThreadContext context, IRubyObject recv) {
         return context.getRuntime().newBoolean(context.getCurrentFrame().getBlock().isGiven());
     }
 
 
     @Deprecated
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         return sprintf(recv.getRuntime().getCurrentContext(), recv, args);
     }
 
     @JRubyMethod(name = {"sprintf", "format"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject sprintf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw context.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         IRubyObject arg;
         if (context.runtime.is1_9() && args.length == 2 && args[1] instanceof RubyHash) {
             arg = args[1];
         } else {
             RubyArray newArgs = context.getRuntime().newArrayNoCopy(args);
             newArgs.shift(context);
             arg = newArgs;
         }
 
         return str.op_format(context, arg);
     }
 
     @JRubyMethod(name = {"raise", "fail"}, optional = 3, module = true, visibility = PRIVATE, omit = true)
     public static IRubyObject raise(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         Ruby runtime = context.getRuntime();
 
         RaiseException raise;
         switch (args.length) {
             case 0:
                 IRubyObject lastException = runtime.getGlobalVariables().get("$!");
                 if (lastException.isNil()) {
                     raise = new RaiseException(runtime, runtime.getRuntimeError(), "", false);
                 } else {
                     // non RubyException value is allowed to be assigned as $!.
                     raise = new RaiseException((RubyException) lastException);
                 }
                 break;
             case 1:
                 if (args[0] instanceof RubyString) {
                     raise = new RaiseException((RubyException) runtime.getRuntimeError().newInstance(context, args, block));
                 } else {
                     raise = new RaiseException(convertToException(runtime, args[0], null));
                 }
                 break;
             case 2:
                 raise = new RaiseException(convertToException(runtime, args[0], args[1]));
                 break;
             default:
                 raise = new RaiseException(convertToException(runtime, args[0], args[1]), args[2]);
                 break;
         }
 
         if (runtime.getDebug().isTrue()) {
             printExceptionSummary(context, runtime, raise.getException());
         }
 
         throw raise;
     }
 
     private static RubyException convertToException(Ruby runtime, IRubyObject obj, IRubyObject optionalMessage) {
         if (!obj.respondsTo("exception")) {
             throw runtime.newTypeError("exception class/object expected");
         }
         IRubyObject exception;
         if (optionalMessage == null) {
             exception = obj.callMethod(runtime.getCurrentContext(), "exception");
         } else {
             exception = obj.callMethod(runtime.getCurrentContext(), "exception", optionalMessage);
         }
         try {
             return (RubyException) exception;
         } catch (ClassCastException cce) {
             throw runtime.newTypeError("exception object expected");
         }
     }
 
     private static void printExceptionSummary(ThreadContext context, Ruby runtime, RubyException rEx) {
         RubyStackTraceElement[] elements = rEx.getBacktraceElements();
         RubyStackTraceElement firstElement = elements.length > 0 ? elements[0] : new RubyStackTraceElement("", "", "(empty)", 0, false);
         String msg = String.format("Exception `%s' at %s:%s - %s\n",
                 rEx.getMetaClass(),
                 firstElement.getFileName(), firstElement.getLineNumber(),
                 runtime.is1_9() ? TypeConverter.convertToType(rEx, runtime.getString(), "to_s") : rEx.convertToString().toString());
 
         runtime.getErrorStream().print(msg);
     }
 
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         return requireCommon(recv.getRuntime(), recv, name, block);
     }
 
     @JRubyMethod(name = "require", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject require19(ThreadContext context, IRubyObject recv, IRubyObject name, Block block) {
         Ruby runtime = context.getRuntime();
 
         IRubyObject tmp = name.checkStringType();
         if (!tmp.isNil()) {
             return requireCommon(runtime, recv, tmp, block);
         }
 
         return requireCommon(runtime, recv,
                 name.respondsTo("to_path") ? name.callMethod(context, "to_path") : name, block);
     }
 
     private static IRubyObject requireCommon(Ruby runtime, IRubyObject recv, IRubyObject name, Block block) {
         if (runtime.getLoadService().require(name.convertToString().toString())) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     @JRubyMethod(required = 1, optional = 1, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         return loadCommon(args[0], recv.getRuntime(), args, block);
     }
 
     @JRubyMethod(name = "load", required = 1, optional = 1, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject load19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject file = args[0];
         if (!(file instanceof RubyString) && file.respondsTo("to_path")) {
             file = file.callMethod(context, "to_path");
         }
 
         return loadCommon(file, context.getRuntime(), args, block);
     }
 
     private static IRubyObject loadCommon(IRubyObject fileName, Ruby runtime, IRubyObject[] args, Block block) {
         RubyString file = fileName.convertToString();
 
         boolean wrap = args.length == 2 ? args[1].isTrue() : false;
 
         runtime.getLoadService().load(file.toString(), wrap);
 
         return runtime.getTrue();
     }
 
     @JRubyMethod(required = 1, optional = 3, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject eval(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return evalCommon(context, recv, args, block, evalBinding18);
     }
 
     @JRubyMethod(name = "eval", required = 1, optional = 3, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject eval19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return evalCommon(context, recv, args, block, evalBinding19);
     }
 
     private static IRubyObject evalCommon(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block, EvalBinding evalBinding) {
         Ruby runtime = context.getRuntime();
         // string to eval
         RubyString src = args[0].convertToString();
         runtime.checkSafeString(src);
 
         boolean bindingGiven = args.length > 1 && !args[1].isNil();
         Binding binding = bindingGiven ? evalBinding.convertToBinding(args[1]) : context.currentBinding();
 
         if (args.length > 2) {
             // file given, use it and force it into binding
             binding.setFile(args[2].convertToString().toString());
 
             if (args.length > 3) {
                 // line given, use it and force it into binding
                 // -1 because parser uses zero offsets and other code compensates
                 binding.setLine(((int) args[3].convertToInteger().getLongValue()) - 1);
             } else {
                 // filename given, but no line, start from the beginning.
                 binding.setLine(0);
             }
         } else if (bindingGiven) {
             // binding given, use binding's file and line-number
         } else {
             // no binding given, use (eval) and start from first line.
             binding.setFile("(eval)");
             binding.setLine(0);
         }
 
         // set method to current frame's, which should be caller's
         String frameName = context.getFrameName();
         if (frameName != null) binding.setMethod(frameName);
 
         if (bindingGiven) recv = binding.getSelf();
 
         return ASTInterpreter.evalWithBinding(context, recv, src, binding);
     }
 
     private static abstract class EvalBinding {
         public abstract Binding convertToBinding(IRubyObject scope);
     }
 
     private static EvalBinding evalBinding18 = new EvalBinding() {
         public Binding convertToBinding(IRubyObject scope) {
             if (scope instanceof RubyBinding) {
                 return ((RubyBinding)scope).getBinding().clone();
             } else {
                 if (scope instanceof RubyProc) {
                     return ((RubyProc) scope).getBlock().getBinding().clone();
                 } else {
                     // bomb out, it's not a binding or a proc
                     throw scope.getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
                 }
             }
         }
     };
 
     private static EvalBinding evalBinding19 = new EvalBinding() {
         public Binding convertToBinding(IRubyObject scope) {
             if (scope instanceof RubyBinding) {
                 return ((RubyBinding)scope).getBinding().clone();
             } else {
                 throw scope.getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Binding)");
             }
         }
     };
 
 
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject callcc(ThreadContext context, IRubyObject recv, Block block) {
         RubyContinuation continuation = new RubyContinuation(context.getRuntime());
         return continuation.enter(context, continuation, block);
     }
 
     @JRubyMethod(optional = 1, module = true, visibility = PRIVATE, omit = true)
     public static IRubyObject caller(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw context.getRuntime().newArgumentError("negative level (" + level + ')');
         }
 
         return context.createCallerBacktrace(context.getRuntime(), level);
     }
 
     @JRubyMethod(name = "catch", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject rbCatch(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         Ruby runtime = context.runtime;
         RubySymbol sym = stringOrSymbol(tag);
         RubyContinuation rbContinuation = new RubyContinuation(runtime, sym);
         try {
             context.pushCatch(rbContinuation.getContinuation());
             return rbContinuation.enter(context, sym, block);
         } finally {
             context.popCatch();
         }
     }
 
     @JRubyMethod(name = "catch", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject rbCatch19(ThreadContext context, IRubyObject recv, Block block) {
         IRubyObject tag = new RubyObject(context.runtime.getObject());
         return rbCatch19Common(context, tag, block);
     }
 
diff --git a/src/org/jruby/internal/runtime/methods/DelegatingDynamicMethod.java b/src/org/jruby/internal/runtime/methods/DelegatingDynamicMethod.java
index eb68310242..c6ff2f28d8 100644
--- a/src/org/jruby/internal/runtime/methods/DelegatingDynamicMethod.java
+++ b/src/org/jruby/internal/runtime/methods/DelegatingDynamicMethod.java
@@ -1,179 +1,192 @@
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
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.RubyModule;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public abstract class DelegatingDynamicMethod extends DynamicMethod {
     protected final DynamicMethod delegate;
 
     public DelegatingDynamicMethod(DynamicMethod delegate) {
         super(delegate.getImplementationClass(), delegate.getVisibility(), delegate.getCallConfig());
         this.delegate = delegate;
     }
 
     public DynamicMethod getDelegate() {
         return delegate;
     }
     
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name) {
         return delegate.call(context, self, klazz, name);
     }
     
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg) {
         return delegate.call(context, self, klazz, name, arg);
     }
     
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2) {
         return delegate.call(context, self, klazz, name, arg1, arg2);
     }
     
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return delegate.call(context, self, klazz, name, arg1, arg2, arg3);
     }
     
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject[] args) {
         return delegate.call(context, self, klazz, name, args);
     }
     
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, Block block) {
         return delegate.call(context, self, klazz, name, block);
     }
     
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, Block block) {
         return delegate.call(context, self, klazz, name, arg1, block);
     }
     
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2, Block block) {
         return delegate.call(context, self, klazz, name, arg1, arg2, block);
     }
     
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
         return delegate.call(context, self, klazz, name, arg1, arg2, arg3, block);
     }
     
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject[] args, Block block) {
         return delegate.call(context, self, klazz, name, args, block);
     }
 
     @Override
     public void setVisibility(Visibility visibility) {
         delegate.setVisibility(visibility);
     }
 
     @Override
     public void setNativeCall(Class nativeTarget, String nativeName, Class nativeReturn, Class[] nativeSignature, boolean statik) {
         delegate.setNativeCall(nativeTarget, nativeName, nativeReturn, nativeSignature, statik);
     }
 
     @Override
     public void setIsBuiltin(boolean isBuiltin) {
         delegate.setIsBuiltin(isBuiltin);
     }
 
     @Override
     public void setImplementationClass(RubyModule implClass) {
         delegate.setImplementationClass(implClass);
     }
 
     @Override
     public void setCallConfig(CallConfiguration callConfig) {
         delegate.setCallConfig(callConfig);
     }
 
     @Override
     public boolean isNative() {
         return delegate.isNative();
     }
 
     @Override
     public boolean isCallableFrom(IRubyObject caller, CallType callType) {
         return delegate.isCallableFrom(caller, callType);
     }
 
     @Override
     public boolean isBuiltin() {
         return delegate.isBuiltin();
     }
 
     @Override
     public Visibility getVisibility() {
         return delegate.getVisibility();
     }
 
     @Override
     public long getSerialNumber() {
         return delegate.getSerialNumber();
     }
 
     @Override
     public DynamicMethod getRealMethod() {
         return delegate.getRealMethod();
     }
 
     @Override
     protected RubyModule getProtectedClass() {
         return delegate.getProtectedClass();
     }
 
     @Override
     public NativeCall getNativeCall() {
         return delegate.getNativeCall();
     }
 
     @Override
     public RubyModule getImplementationClass() {
         return delegate.getImplementationClass();
     }
 
     @Override
     public CallConfiguration getCallConfig() {
         return delegate.getCallConfig();
     }
 
     @Override
     public Arity getArity() {
         return delegate.getArity();
     }
+
+    @Override
+    /**
+     * We override equals so that for method identity checks we treat delegating wrappers the same as the original
+     * methods. See e.g. RubyClass.finvokeChecked and its method_missing comparison.
+     */
+    public boolean equals(Object other) {
+        if (other instanceof DelegatingDynamicMethod) {
+            return delegate.equals(((DelegatingDynamicMethod)other).getDelegate());
+        }
+
+        return delegate.equals(other);
+    }
 }
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index 4594e1c551..e63ffda95a 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -1,1475 +1,1475 @@
 package org.jruby.javasupport.util;
 
 import org.jruby.MetaClass;
 import org.jruby.NativeException;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBasicObject;
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
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgumentNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.LiteralNode;
 import org.jruby.ast.LocalAsgnNode;
 import org.jruby.ast.MultipleAsgn19Node;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.OptArgNode;
 import org.jruby.ast.UnnamedRestArgNode;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.Unrescuable;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.CompiledIRMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.ir.operands.UndefinedValue;
 import org.jruby.javasupport.JavaClass;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.SimpleSourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlock19;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledBlockCallback19;
 import org.jruby.runtime.CompiledBlockLight;
 import org.jruby.runtime.CompiledBlockLight19;
 import org.jruby.runtime.CompiledSharedScopeBlock;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.Interpreted19Block;
 import org.jruby.runtime.InterpretedBlock;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.unsafe.UnsafeFactory;
 
 import java.util.ArrayList;
 import java.util.StringTokenizer;
 
 import static org.jruby.runtime.MethodIndex.OP_EQUAL;
 
 /**
  * Helper methods which are called by the compiler.  Note: These will show no consumers, but
  * generated code does call these so don't remove them thinking they are dead code. 
  *
  */
 public class RuntimeHelpers {
     public static CallSite selectAttrAsgnCallSite(IRubyObject receiver, IRubyObject self, CallSite normalSite, CallSite variableSite) {
         if (receiver == self) return variableSite;
         return normalSite;
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
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, arg1, arg2, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject[] args, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, args);
         return args[args.length - 1];
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject[] args, IRubyObject value, ThreadContext context, IRubyObject caller) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 0, args.length);
         newArgs[args.length] = value;
         callSite.call(context, caller, receiver, newArgs);
         return value;
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject[] receivers) {
         for (int i = 0; i < receivers.length; i++) {
             IRubyObject receiver = receivers[i];
             if (invokeEqqForCaseWhen(callSite, context, caller, arg, receiver)) return true;
         }
         return false;
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver) {
         IRubyObject result = callSite.call(context, caller, receiver, arg);
         if (result.isTrue()) return true;
         return false;
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver0, IRubyObject receiver1) {
         IRubyObject result = callSite.call(context, caller, receiver0, arg);
         if (result.isTrue()) return true;
         return invokeEqqForCaseWhen(callSite, context, caller, arg, receiver1);
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver0, IRubyObject receiver1, IRubyObject receiver2) {
         IRubyObject result = callSite.call(context, caller, receiver0, arg);
         if (result.isTrue()) return true;
         return invokeEqqForCaseWhen(callSite, context, caller, arg, receiver1, receiver2);
     }
 
     public static boolean areAnyTrueForCaselessWhen(IRubyObject[] receivers) {
         for (int i = 0; i < receivers.length; i++) {
             if (receivers[i].isTrue()) return true;
         }
         return false;
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver) {
         return receiver.isTrue();
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver0, IRubyObject receiver1) {
         return receiver0.isTrue() || receiver1.isTrue();
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver0, IRubyObject receiver1, IRubyObject receiver2) {
         return receiver0.isTrue() || receiver1.isTrue() || receiver2.isTrue();
     }
     
     public static CompiledBlockCallback createBlockCallback(Object scriptObject, String closureMethod, String file, int line) {
         Class scriptClass = scriptObject.getClass();
         ClassLoader scriptClassLoader = scriptClass.getClassLoader();
         MethodFactory factory = MethodFactory.createFactory(scriptClassLoader);
         
         return factory.getBlockCallback(closureMethod, file, line, scriptObject);
     }
 
     public static CompiledBlockCallback19 createBlockCallback19(Object scriptObject, String closureMethod, String file, int line) {
         Class scriptClass = scriptObject.getClass();
         ClassLoader scriptClassLoader = scriptClass.getClassLoader();
         MethodFactory factory = MethodFactory.createFactory(scriptClassLoader);
 
         return factory.getBlockCallback19(closureMethod, file, line, scriptObject);
     }
 
     public static byte[] createBlockCallbackOffline(String classPath, String closureMethod, String file, int line) {
         MethodFactory factory = MethodFactory.createFactory(RuntimeHelpers.class.getClassLoader());
 
         return factory.getBlockCallbackOffline(closureMethod, file, line, classPath);
     }
 
     public static byte[] createBlockCallback19Offline(String classPath, String closureMethod, String file, int line) {
         MethodFactory factory = MethodFactory.createFactory(RuntimeHelpers.class.getClassLoader());
 
         return factory.getBlockCallback19Offline(closureMethod, file, line, classPath);
     }
 
     public static String buildBlockDescriptor19(
             String closureMethod,
             int arity,
             StaticScope scope,
             String file,
             int line,
             boolean hasMultipleArgsHead,
             NodeType argsNodeId,
             String parameterList,
             ASTInspector inspector) {
         return buildBlockDescriptor(closureMethod, arity, scope, file, line, hasMultipleArgsHead, argsNodeId, inspector) +
                 "," + parameterList;
     }
 
     public static String buildBlockDescriptor(
             String closureMethod,
             int arity,
             StaticScope scope,
             String file,
             int line,
             boolean hasMultipleArgsHead,
             NodeType argsNodeId,
             ASTInspector inspector) {
         
         // build scope names string
         StringBuffer scopeNames = new StringBuffer();
         for (int i = 0; i < scope.getVariables().length; i++) {
             if (i != 0) scopeNames.append(';');
             scopeNames.append(scope.getVariables()[i]);
         }
 
         // build descriptor string
         String descriptor =
                 closureMethod + ',' +
                 arity + ',' +
                 scopeNames + ',' +
                 hasMultipleArgsHead + ',' +
                 BlockBody.asArgumentType(argsNodeId) + ',' +
                 file + ',' +
                 line + ',' +
                 !(inspector.hasClosure() || inspector.hasScopeAwareMethods());
 
         return descriptor;
     }
     
     public static String[][] parseBlockDescriptor(String descriptor) {
         String[] firstSplit = descriptor.split(",");
         String[] secondSplit;
         if (firstSplit[2].length() == 0) {
             secondSplit = new String[0];
         } else {
             secondSplit = firstSplit[2].split(";");
             // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
             for (int i = 0; i < secondSplit.length; i++) {
                 secondSplit[i] = secondSplit[i].intern();
             }
         }
         return new String[][] {firstSplit, secondSplit};
     }
 
     public static BlockBody createCompiledBlockBody(ThreadContext context, Object scriptObject, String descriptor) {
         String[][] splitDesc = parseBlockDescriptor(descriptor);
         String[] firstSplit = splitDesc[0];
         String[] secondSplit = splitDesc[1];
         return createCompiledBlockBody(context, scriptObject, firstSplit[0], Integer.parseInt(firstSplit[1]), secondSplit, Boolean.valueOf(firstSplit[3]), Integer.parseInt(firstSplit[4]), firstSplit[5], Integer.parseInt(firstSplit[6]), Boolean.valueOf(firstSplit[7]));
     }
     
     public static BlockBody createCompiledBlockBody(ThreadContext context, Object scriptObject, String closureMethod, int arity, 
             String[] staticScopeNames, boolean hasMultipleArgsHead, int argsNodeType, String file, int line, boolean light) {
         StaticScope staticScope = context.getRuntime().getStaticScopeFactory().newBlockScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         if (light) {
             return CompiledBlockLight.newCompiledBlockLight(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType);
         } else {
             return CompiledBlock.newCompiledBlock(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType);
         }
     }
 
     public static BlockBody createCompiledBlockBody19(ThreadContext context, Object scriptObject, String descriptor) {
         String[][] splitDesc = parseBlockDescriptor(descriptor);
         String[] firstSplit = splitDesc[0];
         String[] secondSplit = splitDesc[1];
         return createCompiledBlockBody19(context, scriptObject, firstSplit[0], Integer.parseInt(firstSplit[1]), secondSplit, Boolean.valueOf(firstSplit[3]), Integer.parseInt(firstSplit[4]), firstSplit[5], Integer.parseInt(firstSplit[6]), Boolean.valueOf(firstSplit[7]), firstSplit[8]);
     }
 
     public static BlockBody createCompiledBlockBody19(ThreadContext context, Object scriptObject, String closureMethod, int arity,
             String[] staticScopeNames, boolean hasMultipleArgsHead, int argsNodeType, String file, int line, boolean light, String parameterList) {
         StaticScope staticScope = context.getRuntime().getStaticScopeFactory().newBlockScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
 
         if (light) {
             return CompiledBlockLight19.newCompiledBlockLight(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback19(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType, parameterList.split(";"));
         } else {
             return CompiledBlock19.newCompiledBlock(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback19(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType, parameterList.split(";"));
         }
     }
     
     public static Block createBlock(ThreadContext context, IRubyObject self, BlockBody body) {
         return CompiledBlock.newCompiledClosure(
                 context,
                 self,
                 body);
     }
 
     public static Block createBlock19(ThreadContext context, IRubyObject self, BlockBody body) {
         return CompiledBlock19.newCompiledClosure(
                 context,
                 self,
                 body);
     }
     
     public static IRubyObject runBeginBlock(ThreadContext context, IRubyObject self, String scopeString, CompiledBlockCallback callback) {
         StaticScope staticScope = decodeBlockScope(context, scopeString);
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
     
     public static IRubyObject def(ThreadContext context, IRubyObject self, Object scriptObject, String name, String javaName, StaticScope scope,
             int arity, String filename, int line, CallConfiguration callConfig, String parameterDesc) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
         RubyModule containingClass = context.getRubyClass();
         Visibility visibility = context.getCurrentVisibility();
         
         performNormalMethodChecks(containingClass, runtime, name);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method = constructNormalMethod(
                 factory, javaName,
                 name, containingClass, new SimpleSourcePosition(filename, line), arity, scope, visibility, scriptObject,
                 callConfig,
                 parameterDesc);
         
         addInstanceMethod(containingClass, name, method, visibility,context, runtime);
         
         return runtime.getNil();
     }
     
     public static IRubyObject defs(ThreadContext context, IRubyObject self, IRubyObject receiver, Object scriptObject, String name, String javaName, StaticScope scope,
             int arity, String filename, int line, CallConfiguration callConfig, String parameterDesc) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
 
         RubyClass rubyClass = performSingletonMethodChecks(runtime, receiver, name);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method = constructSingletonMethod(
                 factory, javaName, rubyClass,
                 new SimpleSourcePosition(filename, line), arity, scope,
                 scriptObject, callConfig, parameterDesc);
         
         rubyClass.addMethod(name, method);
         
         callSingletonMethodHook(receiver,context, runtime.fastNewSymbol(name));
         
         return runtime.getNil();
     }
 
     public static byte[] defOffline(String name, String classPath, String invokerName, Arity arity, StaticScope scope, CallConfiguration callConfig, String filename, int line) {
         MethodFactory factory = MethodFactory.createFactory(RuntimeHelpers.class.getClassLoader());
         byte[] methodBytes = factory.getCompiledMethodOffline(name, classPath, invokerName, arity, scope, callConfig, filename, line);
 
         return methodBytes;
     }
     
     public static RubyClass getSingletonClass(Ruby runtime, IRubyObject receiver) {
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError(runtime.is1_9() ? "can't define singleton" : ("no virtual class for " + receiver.getMetaClass().getBaseName()));
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
 
         if (name.equals("method_missing")) {
             return RubyKernel.method_missing(context, receiver, args, Block.NULL_BLOCK);
         }
 
         IRubyObject[] newArgs = prepareMethodMissingArgs(args, context, name);
 
         return invoke(context, receiver, "method_missing", newArgs, Block.NULL_BLOCK);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject[] args, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, args, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, IRubyObject arg1, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, arg1, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, arg1, arg2, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, block);
     }
 
     public static DynamicMethod selectMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType) {
         Ruby runtime = context.getRuntime();
 
         if (name.equals("method_missing")) {
             return selectInternalMM(runtime, visibility, callType);
         }
 
         DynamicMethod methodMissing = receiver.getMetaClass().searchMethod("method_missing");
-        if (methodMissing.isUndefined() || methodMissing == runtime.getDefaultMethodMissing()) {
+        if (methodMissing.isUndefined() || methodMissing.equals(runtime.getDefaultMethodMissing())) {
             return selectInternalMM(runtime, visibility, callType);
         }
         return new MethodMissingMethod(methodMissing, callType);
     }
 
     public static DynamicMethod selectMethodMissing(ThreadContext context, RubyClass selfClass, Visibility visibility, String name, CallType callType) {
         Ruby runtime = context.getRuntime();
 
         if (name.equals("method_missing")) {
             return selectInternalMM(runtime, visibility, callType);
         }
 
         DynamicMethod methodMissing = selfClass.searchMethod("method_missing");
-        if (methodMissing.isUndefined() || methodMissing == runtime.getDefaultMethodMissing()) {
+        if (methodMissing.isUndefined() || methodMissing.equals(runtime.getDefaultMethodMissing())) {
             return selectInternalMM(runtime, visibility, callType);
         }
         return new MethodMissingMethod(methodMissing, callType);
     }
 
     public static DynamicMethod selectMethodMissing(RubyClass selfClass, Visibility visibility, String name, CallType callType) {
         Ruby runtime = selfClass.getClassRuntime();
 
         if (name.equals("method_missing")) {
             return selectInternalMM(runtime, visibility, callType);
         }
 
         DynamicMethod methodMissing = selfClass.searchMethod("method_missing");
-        if (methodMissing.isUndefined() || methodMissing == runtime.getDefaultMethodMissing()) {
+        if (methodMissing.isUndefined() || methodMissing.equals(runtime.getDefaultMethodMissing())) {
             return selectInternalMM(runtime, visibility, callType);
         }
         return new MethodMissingMethod(methodMissing, callType);
     }
 
     private static class MethodMissingMethod extends DynamicMethod {
         private final DynamicMethod delegate;
         private final CallType lastCallStatus;
 
         public MethodMissingMethod(DynamicMethod delegate, CallType lastCallStatus) {
             this.delegate = delegate;
             this.lastCallStatus = lastCallStatus;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             context.setLastCallStatus(lastCallStatus);
             return this.delegate.call(context, self, clazz, "method_missing", prepareMethodMissingArgs(args, context, name), block);
         }
 
         @Override
         public DynamicMethod dup() {
             return this;
         }
     }
 
     private static DynamicMethod selectInternalMM(Ruby runtime, Visibility visibility, CallType callType) {
         if (visibility == Visibility.PRIVATE) {
             return runtime.getPrivateMethodMissing();
         } else if (visibility == Visibility.PROTECTED) {
             return runtime.getProtectedMethodMissing();
         } else if (callType == CallType.VARIABLE) {
             return runtime.getVariableMethodMissing();
         } else if (callType == CallType.SUPER) {
             return runtime.getSuperMethodMissing();
         } else {
             return runtime.getNormalMethodMissing();
         }
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
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject... args) {
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
             if (runtime.is1_9()) {
                 value = ArgsUtil.convertToRubyArray19(runtime, value, masgnHasHead);
             } else {
                 value = ArgsUtil.convertToRubyArray(runtime, value, masgnHasHead);                
             }
         }
         return (RubyArray) value;
     }
     
     public static IRubyObject fetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.getClassVar(name);
     }
     
     @Deprecated
     public static IRubyObject fastFetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName) {
         return fetchClassVariable(context, runtime, self, internedName);
     }
     
     public static IRubyObject getConstant(ThreadContext context, String internedName) {
         Ruby runtime = context.getRuntime();
 
         return context.getCurrentScope().getStaticScope().getConstantWithConstMissing(runtime, internedName, runtime.getObject());
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
     
     @Deprecated
     public static IRubyObject fastSetClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName, IRubyObject value) {
         return setClassVariable(context, runtime, self, internedName, value);
     }
     
     public static IRubyObject declareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String name, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     @Deprecated
     public static IRubyObject fastDeclareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String internedName, IRubyObject value) {
         return declareClassVariable(context, runtime, self, internedName, value);
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
         Ruby runtime = context.runtime;
         if (
                 // rescue exception needs to catch Java exceptions
                 runtime.getException() == catchable ||
 
                 // rescue Object needs to catch Java exceptions
                 runtime.getObject() == catchable) {
 
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
             for (int i = 0; i < throwables.length; i++) {
                 if (checkJavaException(currentThrowable, throwables[i], context)) {
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
     
     public static IRubyObject setConstantInModule(IRubyObject value, IRubyObject module, String name, ThreadContext context) {
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
diff --git a/src/org/jruby/runtime/callsite/RespondToCallSite.java b/src/org/jruby/runtime/callsite/RespondToCallSite.java
index 5fee03524c..cb2b168bae 100644
--- a/src/org/jruby/runtime/callsite/RespondToCallSite.java
+++ b/src/org/jruby/runtime/callsite/RespondToCallSite.java
@@ -1,129 +1,129 @@
 package org.jruby.runtime.callsite;
 
 import org.jruby.CompatVersion;
 import org.jruby.Ruby;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.RubyClass;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.runtime.Visibility;
 
 public class RespondToCallSite extends NormalCachingCallSite {
     private volatile RespondToTuple respondToTuple = RespondToTuple.NULL_CACHE;
 
     private static class RespondToTuple {
         static final RespondToTuple NULL_CACHE = new RespondToTuple("", true, CacheEntry.NULL_CACHE, CacheEntry.NULL_CACHE, null);
         public final String name;
         public final boolean checkVisibility;
         public final CacheEntry respondToMethod;
         public final CacheEntry entry;
         public final IRubyObject respondsTo;
         
         public RespondToTuple(String name, boolean checkVisibility, CacheEntry respondToMethod, CacheEntry entry, IRubyObject respondsTo) {
             this.name = name;
             this.checkVisibility = checkVisibility;
             this.respondToMethod = respondToMethod;
             this.entry = entry;
             this.respondsTo = respondsTo;
         }
 
         public boolean cacheOk(RubyClass klass) {
             return respondToMethod.typeOk(klass) && entry.typeOk(klass);
         }
     }
 
     public RespondToCallSite() {
         super("respond_to?");
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject name) { 
         RubyClass klass = self.getMetaClass();
         RespondToTuple tuple = respondToTuple;
         if (tuple.cacheOk(klass)) {
             String strName = name.asJavaString();
             if (strName.equals(tuple.name) && tuple.checkVisibility) return tuple.respondsTo;
         }
         // go through normal call logic, which will hit overridden cacheAndCall
         return super.call(context, caller, self, name);
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject name, IRubyObject bool) {
         RubyClass klass = self.getMetaClass();
         RespondToTuple tuple = respondToTuple;
         if (tuple.cacheOk(klass)) {
             String strName = name.asJavaString();
             if (strName.equals(tuple.name) && !bool.isTrue() == tuple.checkVisibility) return tuple.respondsTo;
         }
         // go through normal call logic, which will hit overridden cacheAndCall
         return super.call(context, caller, self, name, bool);
     }
 
     @Override
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, ThreadContext context, IRubyObject self, IRubyObject arg) {
         CacheEntry entry = selfType.searchWithCache(methodName);
         DynamicMethod method = entry.method;
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, arg);
         }
 
         // alternate logic to cache the result of respond_to if it's the standard one
         // FIXME: 1.9's respond_to_missing breaks this, so we have to bail out
         if (!context.runtime.is1_9() &&
-                entry.method == context.getRuntime().getRespondToMethod()) {
+                entry.method.equals(context.getRuntime().getRespondToMethod())) {
             String name = arg.asJavaString();
             RespondToTuple tuple = recacheRespondsTo(entry, name, selfType, true, context);
             respondToTuple = tuple;
             return tuple.respondsTo;
         }
 
         // normal logic if it's not the builtin respond_to? method
         cache = entry;
         return method.call(context, self, selfType, methodName, arg);
     }
 
     @Override
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1) {
         CacheEntry entry = selfType.searchWithCache(methodName);
         DynamicMethod method = entry.method;
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, arg0, arg1);
         }
 
         // alternate logic to cache the result of respond_to if it's the standard one
         // FIXME: 1.9's respond_to_missing breaks this, so we have to bail out
         // FIXME: 1.9's respond_to_missing breaks this, so we have to bail out
         if (!context.runtime.is1_9() &&
-                entry.method == context.runtime.getRespondToMethod()) {
+                entry.method.equals(context.runtime.getRespondToMethod())) {
             String name = arg0.asJavaString();
             RespondToTuple tuple = recacheRespondsTo(entry, name, selfType, !arg1.isTrue(), context);
             respondToTuple = tuple;
             return tuple.respondsTo;
         }
 
         // normal logic if it's not the builtin respond_to? method
         cache = entry;
         return method.call(context, self, selfType, methodName, arg0, arg1);
     }
 
     private static RespondToTuple recacheRespondsTo(CacheEntry respondToMethod, String newString, RubyClass klass, boolean checkVisibility, ThreadContext context) {
         Ruby runtime = context.getRuntime();
         CacheEntry respondToLookupResult = klass.searchWithCache(newString);
         IRubyObject respondsTo;
         if (!respondToLookupResult.method.isUndefined() && !respondToLookupResult.method.isNotImplemented()) {
             respondsTo = checkVisibilityAndCache(respondToLookupResult, checkVisibility, runtime);
         } else {
             respondsTo = runtime.getFalse();
         }
         return new RespondToTuple(newString, checkVisibility, respondToMethod, respondToLookupResult, respondsTo);
     }
 
     private static IRubyObject checkVisibilityAndCache(CacheEntry respondEntry, boolean checkVisibility, Ruby runtime) {
         if (!checkVisibility || respondEntry.method.getVisibility() != Visibility.PRIVATE) {
             return runtime.getTrue();
         } else {
             return runtime.getFalse();
         }
     }
 }
\ No newline at end of file
