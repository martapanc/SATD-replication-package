diff --git a/src/jruby/kernel19.rb b/src/jruby/kernel19.rb
index f3a779b62f..f914a0493c 100644
--- a/src/jruby/kernel19.rb
+++ b/src/jruby/kernel19.rb
@@ -1,19 +1,19 @@
 # This is the Ruby 1.9-specific kernel file.
 
 # Thread features are always available in 1.9.
 require 'thread.jar'
 
 # These are loads so they don't pollute LOADED_FEATURES
 load 'jruby/kernel19/thread.rb'
 load 'jruby/kernel19/kernel.rb'
 load 'jruby/kernel19/proc.rb'
 load 'jruby/kernel19/process.rb'
 load 'jruby/kernel19/jruby/process_util.rb'
+load 'jruby/kernel19/jruby/type.rb'
 load 'jruby/kernel19/enumerator.rb'
 load 'jruby/kernel19/enumerable.rb'
 load 'jruby/kernel19/io.rb'
 load 'jruby/kernel19/time.rb'
 load 'jruby/kernel19/gc.rb'
-load 'jruby/kernel19/string.rb'
 
 load 'jruby/kernel19/rubygems.rb' unless JRuby::CONFIG.rubygems_disabled?
diff --git a/src/jruby/kernel19/jruby/type.rb b/src/jruby/kernel19/jruby/type.rb
new file mode 100644
index 0000000000..534f94a559
--- /dev/null
+++ b/src/jruby/kernel19/jruby/type.rb
@@ -0,0 +1,10 @@
+module JRuby
+  module Type
+    def self.convert_to_str(obj)
+      unless obj.respond_to? :to_str
+        raise TypeError, "cannot convert #{obj.class} into String"
+      end
+      obj = obj.to_str
+    end
+  end
+end
\ No newline at end of file
diff --git a/src/jruby/kernel19/kernel.rb b/src/jruby/kernel19/kernel.rb
index bb39bbf3fe..4fd89e029f 100644
--- a/src/jruby/kernel19/kernel.rb
+++ b/src/jruby/kernel19/kernel.rb
@@ -1,31 +1,33 @@
 module Kernel
   module_function
   def require_relative(relative_feature)
     if relative_feature.respond_to? :to_path
       relative_feature = relative_feature.to_path
     else
       relative_feature = relative_feature
     end
-    unless relative_feature.respond_to? :to_str
-      raise TypeError, "cannot convert #{relative_feature.class} into String"
-    end
-    relative_feature = relative_feature.to_str
+
+    relative_feature = JRuby::Type.convert_to_str(relative_feature)
     
     c = caller.first
     e = c.rindex(/:\d+:in /)
     file = $`
     if /\A\((.*)\)/ =~ file # eval, etc.
       raise LoadError, "cannot infer basepath"
     end
     absolute_feature = File.join(File.dirname(File.realpath(file)), relative_feature)
     require absolute_feature
   end
 
   def exec(*args)
     _exec_internal(*JRuby::ProcessUtil.exec_args(args))
   end
   
   def spawn(*args)
     Process.spawn(*args)
   end
+
+  def sprintf(pattern, *args)
+    JRuby::Type.convert_to_str(pattern) % args
+  end
 end
diff --git a/src/jruby/kernel19/string.rb b/src/jruby/kernel19/string.rb
deleted file mode 100644
index 56a4066abf..0000000000
--- a/src/jruby/kernel19/string.rb
+++ /dev/null
@@ -1,29 +0,0 @@
-class String
-  alias_method :old_format, :%
-  def %(replacements)
-    split_re = /(?<!%)(%{[^}]+})/
-    replace_re = /(?<!%)%{([^}]+)}/
-    if ! replacements.is_a? Hash
-      if split_re.match self
-        raise ArgumentError, "one hash required"
-      else
-        return self.old_format replacements
-      end
-    end
-    segments = self.split split_re
-    segments.each_index do |i; md, key|
-    md = replace_re.match(segments[i])
-    if ! md.nil?
-      key = md.captures[0].to_sym
-      raise KeyError, "key[#{key}] not found" unless replacements.has_key?(key)
-      segments[i] = replacements[key]
-    else
-      segments[i] = segments[i].gsub "%%", "%"
-    end
-    end
-    segments.join
-  end
-end
-
-class KeyError < IndexError
-end
\ No newline at end of file
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index ffee7c290b..0171305ae0 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -450,3853 +450,3858 @@ public final class Ruby {
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
             final Class compiled = JVM.compile(this, node, classLoader);
             return new AbstractScript() {
                 public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
                     try {
                         return (IRubyObject)compiled.getMethod("__script__", ThreadContext.class, IRubyObject.class).invoke(null, getCurrentContext(), getTopSelf());
                     } catch (Exception e) {
                         throw new RuntimeException(e);
                     }
                 }
 
                 public IRubyObject load(ThreadContext context, IRubyObject self, boolean wrap) {
                     return __file__(context, self, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
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
         
         executor = new ThreadPoolExecutor(
                 RubyInstanceConfig.POOL_MIN,
                 RubyInstanceConfig.POOL_MAX,
                 RubyInstanceConfig.POOL_TTL,
                 TimeUnit.SECONDS,
                 new SynchronousQueue<Runnable>(),
                 new DaemonThreadFactory());
         
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
             getLoadService().require("jruby/profiler/shutdown_hook");
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
                 LoadService.reflectedLoad(this, "fiber", "org.jruby.ext.fiber.CoroutineFiberLibrary", getJRubyClassLoader(), false);
             } else {
                 LoadService.reflectedLoad(this, "fiber", "org.jruby.ext.fiber.ThreadFiberLibrary", getJRubyClassLoader(), false);
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
+            concurrencyError = defineClassIfAllowed("ConcurrencyError", threadError);
 
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
         addLazyBuiltin("jruby_ext.jar", "jruby", "org.jruby.ext.jruby.JRubyLibrary");
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
         }
 
         if(RubyInstanceConfig.NATIVE_NET_PROTOCOL) {
             addLazyBuiltin("net/protocol.rb", "net/protocol", "org.jruby.ext.net.protocol.NetProtocolBufferedIOLibrary");
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
 
     public Object getRespondToMethod() {
         return respondToMethod;
     }
 
     public void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
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
 
             context.setFile(scriptName);
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
         } finally {
             context.setFile(file);
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
         getBeanManager().unregisterRuntime();
 
         getSelectorPool().cleanup();
 
         if (getJRubyClassLoader() != null) {
             getJRubyClassLoader().tearDown(isDebug());
         }
 
         if (config.isProfilingEntireRun()) {
             // not using logging because it's formatted
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
 
+    public RubySymbol newSymbol(ByteList name) {
+        return symbolTable.getSymbol(name);
+    }
+
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
         return newRaiseException(getErrno().getClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoEISCONNError() {
         return newRaiseException(getErrno().getClass("EISCONN"), "Socket is already connected");
     }
 
     public RaiseException newErrnoEINPROGRESSError() {
         return newRaiseException(getErrno().getClass("EINPROGRESS"), "Operation now in progress");
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
 
     public RaiseException newErrnoENOENTError() {
         return newRaiseException(getErrno().getClass("ENOENT"), "File not found");
     }
 
     public RaiseException newErrnoEACCESError(String message) {
         return newRaiseException(getErrno().getClass("EACCES"), message);
     }
 
     public RaiseException newErrnoEAGAINError(String message) {
         return newErrnoException(getErrno().getClass("EAGAIN"), message);
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
-            invalidByteSequenceError, fiberError, randomClass;
+            invalidByteSequenceError, fiberError, randomClass, keyError;
 
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
     private volatile String currentDirectory;
 
     // The "current line" global variable
     private volatile int currentLine = 0;
 
     private volatile IRubyObject argsFile;
 
     private final long startTime = System.currentTimeMillis();
 
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
     
     // Message for Errno exceptions that will not generate a backtrace
     public static final String ERRNO_BACKTRACE_MESSAGE = "errno backtraces disabled; run with -Xerrno.backtrace=true to enable";
     
     // Count of RaiseExceptions generated by code running in this runtime
     private final AtomicInteger exceptionCount = new AtomicInteger();
     
     // Count of exception backtraces generated by code running in this runtime
     private final AtomicInteger backtraceCount = new AtomicInteger();
     
     // Count of Kernel#caller backtraces generated by code running in this runtime
     private final AtomicInteger callerCount = new AtomicInteger();
     
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
 }
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index c8f395dc04..e16ae2f169 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -143,2002 +143,2007 @@ public class RubyString extends RubyObject implements EncodingCapable {
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
 
     public void setEncoding(Encoding encoding) {
         value.setEncoding(encoding);
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
         return runtime.getEncodingService().findEncoding(this);
     }
 
     public final int getCodeRange() {
         return flags & CR_MASK;
     }
 
     public final void setCodeRange(int codeRange) {
         clearCodeRange();
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
         byte[] bytes = RubyEncoding.encodeUTF8(value);
         this.value = new ByteList(bytes, false);
         this.value.setEncoding(UTF8);
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
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding encoding, boolean objectSpace) {
         this(runtime, rubyClass, value, objectSpace);
         value.setEncoding(encoding);
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
 
     public static RubyString newStringLight(Ruby runtime, int size, Encoding encoding) {
         return new RubyString(runtime, runtime.getString(), new ByteList(size), encoding, false);
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
 
     public static RubyString newString(Ruby runtime, ByteList bytes, Encoding encoding) {
         return new RubyString(runtime, runtime.getString(), bytes, encoding);
     }
     
     public static RubyString newUnicodeString(Ruby runtime, String str) {
         ByteList byteList = new ByteList(RubyEncoding.encodeUTF8(str), UTF8Encoding.INSTANCE, false);
         return new RubyString(runtime, runtime.getString(), byteList);
     }
     
     public static RubyString newUnicodeString(Ruby runtime, CharSequence str) {
         ByteList byteList = new ByteList(RubyEncoding.encodeUTF8(str), UTF8Encoding.INSTANCE, false);
         return new RubyString(runtime, runtime.getString(), byteList);
     }
 
     /**
      * Return a new Ruby String encoded as the default internal encoding given a Java String that
      * has come from an external source. If there is no default internal encoding set, the Ruby
      * String will be encoded using Java's default external encoding. If an internal encoding is
      * set, that encoding will be used for the Ruby String.
      *
      * @param runtime
      * @param str
      * @return
      */
     public static RubyString newInternalFromJavaExternal(Ruby runtime, String str) {
         // Ruby internal
         Encoding internal = runtime.getDefaultInternalEncoding();
         Charset rubyInt = null;
         if (internal != null && internal.getCharset() != null) rubyInt = internal.getCharset();
 
         // Java external, used if no internal
         Charset javaExt = Charset.defaultCharset();
         Encoding javaExtEncoding = runtime.getEncodingService().getJavaDefault();
 
         if (rubyInt == null) {
             return RubyString.newString(
                     runtime,
                     new ByteList(str.getBytes(), javaExtEncoding));
         } else {
             return RubyString.newString(
                     runtime,
                     new ByteList(RubyEncoding.encode(str, rubyInt), internal));
         }
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
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes, Encoding encoding) {
         return newStringShared(runtime, runtime.getString(), bytes, encoding);
     }
 
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes, int codeRange) {
         RubyString str = new RubyString(runtime, runtime.getString(), bytes, codeRange);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes) {
         RubyString str = new RubyString(runtime, clazz, bytes);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes, Encoding encoding) {
         RubyString str = new RubyString(runtime, clazz, bytes, encoding);
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
 
     private static EmptyByteListHolder EMPTY_BYTELISTS[] = new EmptyByteListHolder[4];
 
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
             EMPTY_BYTELISTS = tmp;
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
         return decodeString();
     }
 
     /**
      * Convert this Ruby string to a Java String. This version is encoding-aware.
      *
      * @return A decoded Java String, based on this Ruby string's encoding.
      */
     public String decodeString() {
         Ruby runtime = getRuntime();
         // Note: we always choose UTF-8 for outbound strings in 1.8 mode.  This is clearly undesirable
         // but we do not mark any incoming Strings from JI with their real encoding so we just pick utf-8.
         
         if (runtime.is1_9()) {
             Encoding encoding = getEncoding();
             
             if (encoding == UTF8) {
                 // faster UTF8 decoding
                 return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.begin(), value.length());
             }
             
             Charset charset = runtime.getEncodingService().charsetForEncoding(encoding);
 
             encoding.getCharset();
 
             // charset is not defined for this encoding in jcodings db.  Try letting Java resolve this.
             if (charset == null) {
                 try {
                     return new String(value.getUnsafeBytes(), value.begin(), value.length(), encoding.toString());
                 } catch (UnsupportedEncodingException uee) {
                     return value.toString();
                 }
             }
             
             return RubyEncoding.decode(value.getUnsafeBytes(), value.begin(), value.length(), charset);
         } else {
             // fast UTF8 decoding
             return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.begin(), value.length());
         }
     }
 
     /**
      * Overridden dup for fast-path logic.
      *
      * @return A new RubyString sharing the original backing store.
      */
     @Override
     public IRubyObject dup() {
         RubyClass mc = metaClass.getRealClass();
         if (mc.index != ClassIndex.STRING) return super.dup();
 
         return strDup(mc.getClassRuntime(), mc.getRealClass());
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
     public final RubyString makeSharedString(Ruby runtime, int index, int len) {
         return makeShared(runtime, runtime.getString(), index, len);
     }
     
     public RubyString makeSharedString19(Ruby runtime, int index, int len) {
         return makeShared19(runtime, runtime.getString(), value, index, len);
     }
 
     public final RubyString makeShared(Ruby runtime, int index, int len) {
         return makeShared(runtime, getType(), index, len);
     }
 
     public final RubyString makeShared(Ruby runtime, RubyClass meta, int index, int len) {
         final RubyString shared;
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
         return makeShared19(runtime, getType(), value, index, len);
     }
     
     private RubyString makeShared19(Ruby runtime, RubyClass meta, ByteList value, int index, int len) {
         final RubyString shared;
         Encoding enc = value.getEncoding();
 
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
 
     public final void setByteListShared() {
         if (shareLevel != SHARE_LEVEL_BYTELIST) shareLevel = SHARE_LEVEL_BYTELIST;
     }
 
     public final void modifyCheck() {
         frozenCheck();
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify string");
         }
     }
 
     private void modifyCheck(byte[] b, int len) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len) throw getRuntime().newRuntimeError("string modified");
     }
 
     private void modifyCheck(byte[] b, int len, Encoding enc) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len || value.getEncoding() != enc) throw getRuntime().newRuntimeError("string modified");
     }
 
     private void frozenCheck() {
         frozenCheck(false);
     }
 
     private void frozenCheck(boolean runtimeError) {
         if (isFrozen()) throw getRuntime().newFrozenError("string", runtimeError);
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
 
     /** rb_str_resize
      */
     public final void resize(int length) {
         modify();
         if (value.getRealSize() > length) {
             value.setRealSize(length);
         } else if (value.length() < length) {
             value.length(length);
         }
     }
 
     public final void view(ByteList bytes) {
         modifyCheck();
 
         value = bytes;
         shareLevel = SHARE_LEVEL_NONE;
     }
 
     private void view(byte[]bytes) {
         modifyCheck();
 
         value.replace(bytes);
         shareLevel = SHARE_LEVEL_NONE;
 
         value.invalidate();
     }
 
     private void view(int index, int len) {
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
 
     @JRubyMethod(name = "try_convert", meta = true, compat = RUBY1_9)
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
     @JRubyMethod(name = "<=>", compat = RUBY1_8)
     @Override
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.getRuntime().newFixnum(op_cmp((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     @JRubyMethod(name = "<=>", compat = RUBY1_9)
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
             IRubyObject result = invokedynamic(context, other, OP_CMP, this);
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
     @JRubyMethod(name = "==", compat = RUBY1_8)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             return value.equal(((RubyString)other).value) ? runtime.getTrue() : runtime.getFalse();
         }
         return op_equalCommon(context, other);
     }
 
     @JRubyMethod(name = {"==", "==="}, compat = RUBY1_9)
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
         return invokedynamic(context, other, OP_EQUAL, this).isTrue() ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_8)
     public IRubyObject op_plus(ThreadContext context, IRubyObject _str) {
         RubyString str = _str.convertToString();
         RubyString resultStr = newString(context.getRuntime(), addByteLists(value, str.value));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_9)
     public IRubyObject op_plus19(ThreadContext context, IRubyObject _str) {
         RubyString str = _str.convertToString();
         Encoding enc = checkEncoding(str);
         RubyString resultStr = newStringNoCopy(context.getRuntime(), addByteLists(value, str.value),
                                     enc, codeRangeAnd(getCodeRange(), str.getCodeRange()));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
 
     private ByteList addByteLists(ByteList value1, ByteList value2) {
         ByteList result = new ByteList(value1.getRealSize() + value2.getRealSize());
         result.setRealSize(value1.getRealSize() + value2.getRealSize());
         System.arraycopy(value1.getUnsafeBytes(), value1.getBegin(), result.getUnsafeBytes(), 0, value1.getRealSize());
         System.arraycopy(value2.getUnsafeBytes(), value2.getBegin(), result.getUnsafeBytes(), value1.getRealSize(), value2.getRealSize());
         return result;
     }
 
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_8)
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         return multiplyByteList(context, other);
     }
 
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_9)
     public IRubyObject op_mul19(ThreadContext context, IRubyObject other) {
         RubyString result = multiplyByteList(context, other);
         result.value.setEncoding(value.getEncoding());
         result.copyCodeRange(this);
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
-        IRubyObject tmp = arg.checkArrayType();
-        if (tmp.isNil()) tmp = arg;
+        IRubyObject tmp;
+        if (context.runtime.is1_9() && arg instanceof RubyHash) {
+            tmp = arg;
+        } else {
+            tmp = arg.checkArrayType();
+            if (tmp.isNil()) tmp = arg;
+        }
 
         ByteList out = new ByteList(value.getRealSize());
         out.setEncoding(value.getEncoding());
 
         boolean tainted;
 
         // FIXME: Should we make this work with platform's locale,
         // or continue hardcoding US?
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
 
     /**
      * Generate a murmurhash for the String, using its associated Ruby instance's hash seed.
      *
      * @param runtime
      * @return
      */
     public int strHashCode(Ruby runtime) {
         int hash = MurmurHash.hash32(value.getUnsafeBytes(), value.getBegin(), value.getRealSize(), runtime.getHashSeed());
         if (runtime.is1_9()) {
             hash ^= (value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT ? 0 : value.getEncoding().getIndex());
         }
         return hash;
     }
 
     /**
      * Generate a murmurhash for the String, without a seed.
      *
      * @param runtime
      * @return
      */
     public int unseededStrHashCode(Ruby runtime) {
         int hash = MurmurHash.hash32(value.getUnsafeBytes(), value.getBegin(), value.getRealSize(), 0);
         if (runtime.is1_9()) {
             hash ^= (value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT ? 0 : value.getEncoding().getIndex());
         }
         return hash;
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
         ByteList other = str.value;
         int otherCr = cat(other.getUnsafeBytes(), other.getBegin(), other.getRealSize(),
                 other.getEncoding(), str.getCodeRange());
         infectBy(str);
         str.setCodeRange(otherCr);
         return this;
     }
 
     public final RubyString cat(RubyString str) {
         return cat(str.getByteList());
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
 
     public final int cat(byte[]bytes, int p, int len, Encoding enc, int cr) {
         modify(value.getRealSize() + len);
         int toCr = getCodeRange();
         Encoding toEnc = value.getEncoding();
         int cr2 = cr;
 
         if (toEnc == enc) {
             if (toCr == CR_UNKNOWN || (toEnc == ASCIIEncoding.INSTANCE && toCr != CR_7BIT)) {
                 cr = CR_UNKNOWN;
             } else if (cr == CR_UNKNOWN) {
                 cr = codeRangeScan(enc, bytes, p, len);
             }
         } else {
             if (!toEnc.isAsciiCompatible() || !enc.isAsciiCompatible()) {
                 if (len == 0) return toCr;
                 if (value.getRealSize() == 0) {
                     System.arraycopy(bytes, p, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
                     value.setRealSize(value.getRealSize() + len);
                     setEncodingAndCodeRange(enc, cr);
                     return cr;
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
                 resEnc = toEnc != ASCIIEncoding.INSTANCE ? toEnc : enc;
                 resCr = CR_7BIT;
             } else {
                 resEnc = enc;
                 resCr = cr;
             }
         } else if (toCr == CR_VALID) {
             resEnc = toEnc;
             if (cr == CR_7BIT || cr == CR_VALID) {
                 resCr = toCr;
             } else {
                 resCr = cr;
             }
         } else {
             resEnc = toEnc;
             resCr = len > 0 ? CR_UNKNOWN : toCr;
         }
 
         if (len < 0) throw getRuntime().newArgumentError("negative string size (or size too big)");
 
         System.arraycopy(bytes, p, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
         value.setRealSize(value.getRealSize() + len);
         setEncodingAndCodeRange(resEnc, resCr);
 
         return cr2;
     }
 
     public final int cat(byte[]bytes, int p, int len, Encoding enc) {
         return cat(bytes, p, len, enc, CR_UNKNOWN);
     }
 
     public final RubyString catAscii(byte[]bytes, int p, int len) {
         Encoding enc = value.getEncoding();
         if (enc.isAsciiCompatible()) {
             cat(bytes, p, len, enc, CR_7BIT);
         } else {
             byte buf[] = new byte[enc.maxLength()];
             int end = p + len;
             while (p < end) {
                 int c = bytes[p];
                 int cl = codeLength(getRuntime(), enc, c);
                 enc.codeToMbc(c, buf, 0);
                 cat(buf, 0, cl, enc, CR_VALID);
                 p++;
             }
         }
         return this;
     }
 
     /** rb_str_replace_m
      *
      */
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1, compat = RUBY1_8)
     public IRubyObject replace(IRubyObject other) {
         if (this == other) return this;
         replaceCommon(other);
         return this;
     }
 
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1, compat = RUBY1_9)
     public RubyString replace19(IRubyObject other) {
         modifyCheck();
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
 
     @JRubyMethod(name = "clear", compat = RUBY1_9)
     public RubyString clear() {
         modifyCheck();
         Encoding enc = value.getEncoding();
 
         EmptyByteListHolder holder = getEmptyByteList(enc);
         value = holder.bytes;
         shareLevel = SHARE_LEVEL_BYTELIST;
         setCodeRange(holder.cr);
         return this;
     }
 
     @JRubyMethod(name = "reverse", compat = RUBY1_8)
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
 
     @JRubyMethod(name = "reverse", compat = RUBY1_9)
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
 
     @JRubyMethod(name = "reverse!", compat = RUBY1_8)
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
 
     @JRubyMethod(name = "reverse!", compat = RUBY1_9)
     public RubyString reverse_bang19(ThreadContext context) {
         modifyCheck();
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
 
     @JRubyMethod(visibility = PRIVATE, compat = RUBY1_8)
     @Override
     public IRubyObject initialize(ThreadContext context) {
         return this;
     }
 
     @JRubyMethod(visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize(ThreadContext context, IRubyObject arg0) {
         replace(arg0);
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     @Override
     public IRubyObject initialize19(ThreadContext context) {
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject arg0) {
         replace19(arg0);
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject casecmp(ThreadContext context, IRubyObject other) {
         return RubyFixnum.newFixnum(context.getRuntime(), value.caseInsensitiveCmp(other.convertToString().value));
     }
 
     @JRubyMethod(name = "casecmp", compat = RUBY1_9)
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
                 byte uc = AsciiTables.ToUpperCaseTable[c];
                 byte uoc = AsciiTables.ToUpperCaseTable[oc];
                 if (uc != uoc) {
                     return uc < uoc ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
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
     @JRubyMethod(name = "=~", compat = RUBY1_8, writes = BACKREF)
     @Override
     public IRubyObject op_match(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).op_match(context, this);
         if (other instanceof RubyString) throw context.getRuntime().newTypeError("type mismatch: String given");
         return other.callMethod(context, "=~", this);
     }
 
     @JRubyMethod(name = "=~", compat = RUBY1_9, writes = BACKREF)
     @Override
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
     @JRubyMethod(compat = RUBY1_8, reads = BACKREF)
     public IRubyObject match(ThreadContext context, IRubyObject pattern) {
         return getPattern(pattern).callMethod(context, "match", this);
     }
 
     @JRubyMethod(name = "match", compat = RUBY1_9, reads = BACKREF)
     public IRubyObject match19(ThreadContext context, IRubyObject pattern, Block block) {
         IRubyObject result = getPattern(pattern).callMethod(context, "match", this);
         return block.isGiven() && !result.isNil() ? block.yield(context, result) : result;
     }
 
     @JRubyMethod(name = "match", required = 2, rest = true, compat = RUBY1_9, reads = BACKREF)
     public IRubyObject match19(ThreadContext context, IRubyObject[]args, Block block) {
         RubyRegexp pattern = getPattern(args[0]);
         args[0] = this;
         IRubyObject result = pattern.callMethod(context, "match", args);
         return block.isGiven() && !result.isNil() ? block.yield(context, result) : result;
     }
 
     /** rb_str_capitalize / rb_str_capitalize_bang
      *
      */
     @JRubyMethod(name = "capitalize", compat = RUBY1_8)
     public IRubyObject capitalize(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.capitalize_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "capitalize!", compat = RUBY1_8)
     public IRubyObject capitalize_bang(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modify();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
         boolean modify = false;
 
         int c = bytes[s] & 0xff;
         if (ASCII.isLower(c)) {
             bytes[s] = AsciiTables.ToUpperCaseTable[c];
             modify = true;
         }
 
         while (++s < end) {
             c = bytes[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 bytes[s] = AsciiTables.ToLowerCaseTable[c];
                 modify = true;
             }
         }
 
         return modify ? this : runtime.getNil();
     }
 
     @JRubyMethod(name = "capitalize", compat = RUBY1_9)
     public IRubyObject capitalize19(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.capitalize_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "capitalize!", compat = RUBY1_9)
     public IRubyObject capitalize_bang19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         Encoding enc = checkDummyEncoding();
 
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
         boolean modify = false;
 
         int c = codePoint(runtime, enc, bytes, s, end);
         if (enc.isLower(c)) {
             enc.codeToMbc(toUpper(enc, c), bytes, s);
             modify = true;
         }
 
         s += codeLength(runtime, enc, c);
         while (s < end) {
             c = codePoint(runtime, enc, bytes, s, end);
             if (enc.isUpper(c)) {
                 enc.codeToMbc(toLower(enc, c), bytes, s);
                 modify = true;
             }
             s += codeLength(runtime, enc, c);
         }
 
         return modify ? this : runtime.getNil();
     }
 
     @JRubyMethod(name = ">=", compat = RUBY1_8)
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp((RubyString) other) >= 0);
         return RubyComparable.op_ge(context, this, other);
     }
 
     @JRubyMethod(name = ">=", compat = RUBY1_9)
     public IRubyObject op_ge19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp19((RubyString) other) >= 0);
         return RubyComparable.op_ge(context, this, other);
     }
 
     @JRubyMethod(name = ">", compat = RUBY1_8)
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp((RubyString) other) > 0);
         return RubyComparable.op_gt(context, this, other);
     }
 
     @JRubyMethod(name = ">", compat = RUBY1_9)
     public IRubyObject op_gt19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp19((RubyString) other) > 0);
         return RubyComparable.op_gt(context, this, other);
     }
 
     @JRubyMethod(name = "<=", compat = RUBY1_8)
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp((RubyString) other) <= 0);
         return RubyComparable.op_le(context, this, other);
     }
 
     @JRubyMethod(name = "<=", compat = RUBY1_9)
     public IRubyObject op_le19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp19((RubyString) other) <= 0);
         return RubyComparable.op_le(context, this, other);
     }
 
     @JRubyMethod(name = "<", compat = RUBY1_8)
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp((RubyString) other) < 0);
         return RubyComparable.op_lt(context, this, other);
     }
 
     @JRubyMethod(name = "<", compat = RUBY1_9)
     public IRubyObject op_lt19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp19((RubyString) other) < 0);
         return RubyComparable.op_lt(context, this, other);
     }
 
     @JRubyMethod(name = "eql?", compat = RUBY1_8)
     public IRubyObject str_eql_p(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (other instanceof RubyString && value.equal(((RubyString)other).value)) return runtime.getTrue();
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = "eql?", compat = RUBY1_9)
     public IRubyObject str_eql_p19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             if (isComparableWith(otherString) && value.equal(otherString.value)) return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     /** rb_str_upcase / rb_str_upcase_bang
      *
      */
     @JRubyMethod(name = "upcase", compat = RUBY1_8)
     public RubyString upcase(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.upcase_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "upcase!", compat = RUBY1_8)
     public IRubyObject upcase_bang(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
         modify();
         return singleByteUpcase(runtime, value.getUnsafeBytes(), value.getBegin(), value.getBegin() + value.getRealSize());
     }
 
     @JRubyMethod(name = "upcase", compat = RUBY1_9)
     public RubyString upcase19(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.upcase_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "upcase!", compat = RUBY1_9)
     public IRubyObject upcase_bang19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         Encoding enc = checkDummyEncoding();
 
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
 
         if (singleByteOptimizable(enc)) {
             return singleByteUpcase(runtime, bytes, s, end);
         } else {
             return multiByteUpcase(runtime, enc, bytes, s, end);
         }
     }
 
     private IRubyObject singleByteUpcase(Ruby runtime, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = bytes[s] & 0xff;
             if (ASCII.isLower(c)) {
                 bytes[s] = AsciiTables.ToUpperCaseTable[c];
                 modify = true;
             }
             s++;
         }
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject multiByteUpcase(Ruby runtime, Encoding enc, byte[]bytes, int s, int end) {
         boolean modify = false;
         int c;
         while (s < end) {
             if (enc.isAsciiCompatible() && Encoding.isAscii(c = bytes[s] & 0xff)) {
                 if (ASCII.isLower(c)) {
                     bytes[s] = AsciiTables.ToUpperCaseTable[c];
                     modify = true;
                 }
                 s++;
             } else {
                 c = codePoint(runtime, enc, bytes, s, end);
                 if (enc.isLower(c)) {
                     enc.codeToMbc(toUpper(enc, c), bytes, s);
                     modify = true;
                 }
                 s += codeLength(runtime, enc, c);
             }
         }
         return modify ? this : runtime.getNil();
     }
 
     /** rb_str_downcase / rb_str_downcase_bang
      *
      */
     @JRubyMethod(name = "downcase", compat = RUBY1_8)
     public RubyString downcase(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.downcase_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "downcase!", compat = RUBY1_8)
     public IRubyObject downcase_bang(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modify();
         return singleByteDowncase(runtime, value.getUnsafeBytes(), value.getBegin(), value.getBegin() + value.getRealSize());
     }
 
     @JRubyMethod(name = "downcase", compat = RUBY1_9)
     public RubyString downcase19(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.downcase_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "downcase!", compat = RUBY1_9)
     public IRubyObject downcase_bang19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         Encoding enc = checkDummyEncoding();
 
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
 
         if (singleByteOptimizable(enc)) {
             return singleByteDowncase(runtime, bytes, s, end);
         } else {
             return multiByteDowncase(runtime, enc, bytes, s, end);
         }
     }
 
     private IRubyObject singleByteDowncase(Ruby runtime, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = bytes[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 bytes[s] = AsciiTables.ToLowerCaseTable[c];
                 modify = true;
             }
             s++;
         }
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject multiByteDowncase(Ruby runtime, Encoding enc, byte[]bytes, int s, int end) {
         boolean modify = false;
         int c;
         while (s < end) {
             if (enc.isAsciiCompatible() && Encoding.isAscii(c = bytes[s] & 0xff)) {
                 if (ASCII.isUpper(c)) {
                     bytes[s] = AsciiTables.ToLowerCaseTable[c];
                     modify = true;
                 }
                 s++;
             } else {
                 c = codePoint(runtime, enc, bytes, s, end);
                 if (enc.isUpper(c)) {
                     enc.codeToMbc(toLower(enc, c), bytes, s);
                     modify = true;
                 }
                 s += codeLength(runtime, enc, c);
             }
         }
         return modify ? this : runtime.getNil();
     }
 
 
     /** rb_str_swapcase / rb_str_swapcase_bang
      *
      */
     @JRubyMethod(name = "swapcase", compat = RUBY1_8)
     public RubyString swapcase(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.swapcase_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "swapcase!", compat = RUBY1_8)
     public IRubyObject swapcase_bang(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
         modify();
         return singleByteSwapcase(runtime, value.getUnsafeBytes(), value.getBegin(), value.getBegin() + value.getRealSize());
     }
 
     @JRubyMethod(name = "swapcase", compat = RUBY1_9)
     public RubyString swapcase19(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.swapcase_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "swapcase!", compat = RUBY1_9)
     public IRubyObject swapcase_bang19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         Encoding enc = checkDummyEncoding();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
 
         if (singleByteOptimizable(enc)) {
             return singleByteSwapcase(runtime, bytes, s, end);
         } else {
             return multiByteSwapcase(runtime, enc, bytes, s, end);
         }
     }
 
     private IRubyObject singleByteSwapcase(Ruby runtime, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = bytes[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 bytes[s] = AsciiTables.ToLowerCaseTable[c];
                 modify = true;
             } else if (ASCII.isLower(c)) {
                 bytes[s] = AsciiTables.ToUpperCaseTable[c];
                 modify = true;
             }
             s++;
         }
 
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject multiByteSwapcase(Ruby runtime, Encoding enc, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = codePoint(runtime, enc, bytes, s, end);
             if (enc.isUpper(c)) {
                 enc.codeToMbc(toLower(enc, c), bytes, s);
                 modify = true;
             } else if (enc.isLower(c)) {
                 enc.codeToMbc(toUpper(enc, c), bytes, s);
                 modify = true;
             }
             s += codeLength(runtime, enc, c);
         }
 
         return modify ? this : runtime.getNil();
     }
 
     /** rb_str_dump
      *
      */
     @JRubyMethod(name = "dump", compat = RUBY1_8)
     public IRubyObject dump() {
         return dumpCommon(false);
     }
 
     @JRubyMethod(name = "dump", compat = RUBY1_9)
     public IRubyObject dump19() {
         return dumpCommon(true);
     }
 
     private IRubyObject dumpCommon(boolean is1_9) {
         Ruby runtime = getRuntime();
         ByteList buf = null;
         Encoding enc = value.getEncoding();
 
         int p = value.getBegin();
         int end = p + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
 
         int len = 2;
         while (p < end) {
             int c = bytes[p++] & 0xff;
 
             switch (c) {
             case '"':case '\\':case '\n':case '\r':case '\t':case '\f':
             case '\013': case '\010': case '\007': case '\033':
                 len += 2;
                 break;
             case '#':
                 len += isEVStr(bytes, p, end) ? 2 : 1;
                 break;
             default:
                 if (ASCII.isPrint(c)) {
                     len++;
                 } else {
                     if (is1_9 && enc instanceof UTF8Encoding) {
diff --git a/src/org/jruby/util/Sprintf.java b/src/org/jruby/util/Sprintf.java
index fc2c538dc2..64954204e9 100644
--- a/src/org/jruby/util/Sprintf.java
+++ b/src/org/jruby/util/Sprintf.java
@@ -1,1441 +1,1523 @@
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
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
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
 
 import java.math.BigInteger;
 import java.text.DecimalFormatSymbols;
 import java.text.NumberFormat;
 import java.util.Locale;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
+import org.jruby.RubyHash;
 import org.jruby.RubyInteger;
 import org.jruby.RubyKernel;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
+import org.jruby.RubySymbol;
 import org.jruby.common.IRubyWarnings.ID;
+import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.builtin.IRubyObject;
 
 
 /**
  * @author Bill Dortch
  *
  */
 public class Sprintf {
     private static final int FLAG_NONE        = 0;
     private static final int FLAG_SPACE       = 1;
     private static final int FLAG_ZERO        = 1 << 1;
     private static final int FLAG_PLUS        = 1 << 2;
     private static final int FLAG_MINUS       = 1 << 3;
     private static final int FLAG_SHARP       = 1 << 4;
     private static final int FLAG_WIDTH       = 1 << 5;
     private static final int FLAG_PRECISION   = 1 << 6;
     
     private static final byte[] PREFIX_OCTAL     = {'0'};
     private static final byte[] PREFIX_HEX_LC    = {'0','x'};
     private static final byte[] PREFIX_HEX_UC    = {'0','X'};
     private static final byte[] PREFIX_BINARY_LC = {'0','b'};
     private static final byte[] PREFIX_BINARY_UC = {'0','B'};
     
     private static final byte[] PREFIX_NEGATIVE = {'.','.'};
     
     private static final byte[] NAN_VALUE       = {'N','a','N'};
     private static final byte[] INFINITY_VALUE  = {'I','n','f'};
        
     private static final BigInteger BIG_32 = BigInteger.valueOf(((long)Integer.MAX_VALUE + 1L) << 1);
     private static final BigInteger BIG_64 = BIG_32.shiftLeft(32);
     private static final BigInteger BIG_MINUS_32 = BigInteger.valueOf((long)Integer.MIN_VALUE << 1);
     private static final BigInteger BIG_MINUS_64 = BIG_MINUS_32.shiftLeft(32);
 
     private static final String ERR_MALFORMED_FORMAT = "malformed format string";
     private static final String ERR_MALFORMED_NUM = "malformed format string - %[0-9]";
     private static final String ERR_MALFORMED_DOT_NUM = "malformed format string - %.[0-9]";
     private static final String ERR_MALFORMED_STAR_NUM = "malformed format string - %*[0-9]";
     private static final String ERR_ILLEGAL_FORMAT_CHAR = "illegal format character - %";
+    private static final String ERR_MALFORMED_NAME = "malformed name - unmatched parenthesis";
     
     
     private static final class Args {
         private final Ruby runtime;
         private final Locale locale;
         private final IRubyObject rubyObject;
         private final RubyArray rubyArray;
+        private final RubyHash rubyHash;
         private final int length;
         private int unnumbered; // last index (+1) accessed by next()
         private int numbered;   // last index (+1) accessed by get()
         
         Args(Locale locale, IRubyObject rubyObject) {
             if (rubyObject == null) throw new IllegalArgumentException("null IRubyObject passed to sprintf");
             this.locale = locale == null ? Locale.getDefault() : locale;
             this.rubyObject = rubyObject;
             if (rubyObject instanceof RubyArray) {
-                this.rubyArray = ((RubyArray)rubyObject);
+                this.rubyArray = (RubyArray)rubyObject;
+                this.rubyHash = null;
                 this.length = rubyArray.size();
+            } else if (rubyObject instanceof RubyHash && rubyObject.getRuntime().is1_9()) {
+                // allow a hash for args if in 1.9 mode
+                this.rubyHash = (RubyHash)rubyObject;
+                this.rubyArray = null;
+                this.length = -1;
             } else {
                 this.length = 1;
                 this.rubyArray = null;
+                this.rubyHash = null;
             }
             this.runtime = rubyObject.getRuntime();
         }
         
         Args(IRubyObject rubyObject) {
             this(Locale.getDefault(),rubyObject);
         }
 
         // temporary hack to handle non-Ruby values
         // will come up with better solution shortly
         Args(Ruby runtime, long value) {
             this(RubyFixnum.newFixnum(runtime,value));
         }
         
         void raiseArgumentError(String message) {
             throw runtime.newArgumentError(message);
         }
+
+        void raiseKeyError(String message) {
+            RubyKernel.raise(runtime.getCurrentContext(),
+                    runtime.getKernel(),
+                    new IRubyObject[] {runtime.getClass("KeyError"), runtime.newString(message)},
+                    Block.NULL_BLOCK);
+        }
         
         void warn(ID id, String message) {
             runtime.getWarnings().warn(id, message);
         }
         
         void warning(ID id, String message) {
             if (runtime.isVerbose()) runtime.getWarnings().warning(id, message);
         }
         
-        IRubyObject next() {
+        IRubyObject next(ByteList name) {
+            // for 1.9 hash args
+            if (rubyHash != null && name == null ||
+                    rubyHash == null && name != null) raiseArgumentError("positional args mixed with named args");
+            if (name != null) {
+                IRubyObject object = rubyHash.fastARef(runtime.newSymbol(name));
+                if (object == null) raiseKeyError("key<" + name + "> not found");
+                return object;
+            }
+
             // this is the order in which MRI does these two tests
             if (numbered > 0) raiseArgumentError("unnumbered" + (unnumbered + 1) + "mixed with numbered");
             if (unnumbered >= length) raiseArgumentError("too few arguments");
             IRubyObject object = rubyArray == null ? rubyObject : rubyArray.eltInternal(unnumbered);
             unnumbered++;
             return object;
         }
         
         IRubyObject get(int index) {
+            // for 1.9 hash args
+            if (rubyHash != null) raiseArgumentError("positional args mixed with named args");
             // this is the order in which MRI does these tests
             if (unnumbered > 0) raiseArgumentError("numbered("+numbered+") after unnumbered("+unnumbered+")");
             if (index < 0) raiseArgumentError("invalid index - " + (index + 1) + '$');
             if (index >= length) raiseArgumentError("too few arguments");
             numbered = index + 1;
             return rubyArray == null ? rubyObject : rubyArray.eltInternal(index);
         }
         
         IRubyObject getNth(int formatIndex) {
             return get(formatIndex - 1);
         }
         
         int nextInt() {
-            return intValue(next());
-        }
-        
-        int getInt(int index) {
-            return intValue(get(index));
+            return intValue(next(null));
         }
 
         int getNthInt(int formatIndex) {
             return intValue(get(formatIndex - 1));
         }
         
         int intValue(IRubyObject obj) {
             if (obj instanceof RubyNumeric) return (int)((RubyNumeric)obj).getLongValue();
 
             // basically just forcing a TypeError here to match MRI
             obj = TypeConverter.convertToType(obj, obj.getRuntime().getFixnum(), "to_int", true);
             return (int)((RubyFixnum)obj).getLongValue();
         }
         
         byte getDecimalSeparator() {
             // not saving DFS instance, as it will only be used once (at most) per call
             return (byte)new DecimalFormatSymbols(locale).getDecimalSeparator();
         }
     } // Args
 
     // static methods only
     private Sprintf () {}
     
     // Special form of sprintf that returns a RubyString and handles
     // tainted strings correctly.
     public static boolean sprintf(ByteList to, Locale locale, CharSequence format, IRubyObject args) {
         return rubySprintfToBuffer(to, format, new Args(locale, args));
     }
 
     // Special form of sprintf that returns a RubyString and handles
     // tainted strings correctly. Version for 1.9.
     public static boolean sprintf1_9(ByteList to, Locale locale, CharSequence format, IRubyObject args) {
         return rubySprintfToBuffer(to, format, new Args(locale, args), false);
     }
 
     public static boolean sprintf(ByteList to, CharSequence format, IRubyObject args) {
         return rubySprintf(to, format, new Args(args));
     }
 
     public static boolean sprintf(Ruby runtime, ByteList to, CharSequence format, int arg) {
         return rubySprintf(to, format, new Args(runtime, (long)arg));
     }
 
     public static boolean sprintf(ByteList to, RubyString format, IRubyObject args) {
         return rubySprintf(to, format.getByteList(), new Args(args));
     }
 
     private static boolean rubySprintf(ByteList to, CharSequence charFormat, Args args) {
         return rubySprintfToBuffer(to, charFormat, args);
     }
 
     private static boolean rubySprintfToBuffer(ByteList buf, CharSequence charFormat, Args args) {
         return rubySprintfToBuffer(buf, charFormat, args, true);
     }
 
     private static boolean rubySprintfToBuffer(ByteList buf, CharSequence charFormat, Args args, boolean usePrefixForZero) {
         boolean tainted = false;
         final byte[] format;
 
         int offset;
         int length;
         int start;
-        int mark;        
+        int mark;
+        ByteList name = null;
 
         if (charFormat instanceof ByteList) {
             ByteList list = (ByteList)charFormat;
             format = list.getUnsafeBytes();
             int begin = list.begin(); 
             offset = begin;
             length = begin + list.length();
             start = begin;
             mark = begin;
         } else {
             format = stringToBytes(charFormat, false);
             offset = 0;
             length = charFormat.length();
             start = 0;
             mark = 0;             
         }
 
         while (offset < length) {
             start = offset;
             for ( ; offset < length && format[offset] != '%'; offset++) {}
 
             if (offset > start) {
                 buf.append(format,start,offset-start);
                 start = offset;
             }
             if (offset++ >= length) break;
 
             IRubyObject arg = null;
             int flags = 0;
             int width = 0;
             int precision = 0;
             int number = 0;
             byte fchar = 0;
             boolean incomplete = true;
             for ( ; incomplete && offset < length ; ) {
                 switch (fchar = format[offset]) {
                 default:
                     if (fchar == '\0' && flags == FLAG_NONE) {
                         // MRI 1.8.6 behavior: null byte after '%'
                         // leads to "%" string. Null byte in
                         // other places, like "%5\0", leads to error.
                         buf.append('%');
                         buf.append(fchar);
                         incomplete = false;
                         offset++;
                         break;
                     } else if (isPrintable(fchar)) {
                         raiseArgumentError(args,"malformed format string - %" + (char)fchar);
                     } else {
                         raiseArgumentError(args,ERR_MALFORMED_FORMAT);
                     }
                     break;
 
+                case '<': {
+                    // Ruby 1.9 named args
+                    int nameStart = ++offset;
+                    int nameEnd = nameStart;
+
+                    for ( ; offset < length ; offset++) {
+                        if (format[offset] == '>') {
+                            nameEnd = offset;
+                            offset++;
+                            break;
+                        }
+                    }
+
+                    if (nameEnd == nameStart) raiseArgumentError(args, ERR_MALFORMED_NAME);
+
+                    // TODO: encoding for name?
+                    name = new ByteList(format, nameStart, nameEnd - nameStart);
+
+                    break;
+                }
+
+                case '{': {
+                    // Ruby 1.9 named replacement
+                    int nameStart = ++offset;
+                    int nameEnd = nameStart;
+
+                    for ( ; offset < length ; offset++) {
+                        if (format[offset] == '}') {
+                            nameEnd = offset;
+                            offset++;
+                            break;
+                        }
+                    }
+
+                    if (nameEnd == nameStart) raiseArgumentError(args, ERR_MALFORMED_NAME);
+
+                    ByteList localName = new ByteList(format, nameStart, nameEnd - nameStart);
+                    buf.append(args.next(localName).asString().getByteList());
+                    incomplete = false;
+
+                    break;
+                }
+
                 case ' ':
                     flags |= FLAG_SPACE;
                     offset++;
                     break;
                 case '0':
                     flags |= FLAG_ZERO;
                     offset++;
                     break;
                 case '+':
                     flags |= FLAG_PLUS;
                     offset++;
                     break;
                 case '-':
                     flags |= FLAG_MINUS;
                     offset++;
                     break;
                 case '#':
                     flags |= FLAG_SHARP;
                     offset++;
                     break;
                 case '1':case '2':case '3':case '4':case '5':
                 case '6':case '7':case '8':case '9':
                     // MRI doesn't flag it as an error if width is given multiple
                     // times as a number (but it does for *)
                     number = 0;
                     for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                         number = extendWidth(args, number, fchar);
                     }
                     checkOffset(args,offset,length,ERR_MALFORMED_NUM);
                     if (fchar == '$') {
                         if (arg != null) {
                             raiseArgumentError(args,"value given twice - " + number + "$");
                         }
                         arg = args.getNth(number);
                         offset++;
                     } else {
                         width = number;
                         flags |= FLAG_WIDTH;
                     }
                     break;
                 
                 case '*':
                     if ((flags & FLAG_WIDTH) != 0) {
                         raiseArgumentError(args,"width given twice");
                     }
                     flags |= FLAG_WIDTH;
                     // TODO: factor this chunk as in MRI/YARV GETASTER
                     checkOffset(args,++offset,length,ERR_MALFORMED_STAR_NUM);
                     mark = offset;
                     number = 0;
                     for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                         number = extendWidth(args,number,fchar);
                     }
                     checkOffset(args,offset,length,ERR_MALFORMED_STAR_NUM);
                     if (fchar == '$') {
                         width = args.getNthInt(number);
                         if (width < 0) {
                             flags |= FLAG_MINUS;
                             width = -width;
                         }
                         offset++;
                     } else {
                         width = args.nextInt();
                         if (width < 0) {
                             flags |= FLAG_MINUS;
                             width = -width;
                         }
                         // let the width (if any), get processed in the next loop,
                         // so any leading 0 gets treated correctly 
                         offset = mark;
                     }
                     break;
                 
                 case '.':
                     if ((flags & FLAG_PRECISION) != 0) {
                         raiseArgumentError(args,"precision given twice");
                     }
                     flags |= FLAG_PRECISION;
                     checkOffset(args,++offset,length,ERR_MALFORMED_DOT_NUM);
                     fchar = format[offset];
                     if (fchar == '*') {
                         // TODO: factor this chunk as in MRI/YARV GETASTER
                         checkOffset(args,++offset,length,ERR_MALFORMED_STAR_NUM);
                         mark = offset;
                         number = 0;
                         for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                             number = extendWidth(args,number,fchar);
                         }
                         checkOffset(args,offset,length,ERR_MALFORMED_STAR_NUM);
                         if (fchar == '$') {
                             precision = args.getNthInt(number);
                             if (precision < 0) {
                                 flags &= ~FLAG_PRECISION;
                             }
                             offset++;
                         } else {
                             precision = args.nextInt();
                             if (precision < 0) {
                                 flags &= ~FLAG_PRECISION;
                             }
                             // let the width (if any), get processed in the next loop,
                             // so any leading 0 gets treated correctly 
                             offset = mark;
                         }
                     } else {
                         number = 0;
                         for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                             number = extendWidth(args,number,fchar);
                         }
                         checkOffset(args,offset,length,ERR_MALFORMED_DOT_NUM);
                         precision = number;
                     }
                     break;
 
                 case '\n':
                     offset--;
                 case '%':
                     if (flags != FLAG_NONE) {
                         raiseArgumentError(args,ERR_ILLEGAL_FORMAT_CHAR);
                     }
                     buf.append('%');
                     offset++;
                     incomplete = false;
                     break;
 
                 case 'c': {
-                    if (arg == null) arg = args.next();
+                    if (arg == null || name != null) {
+                        arg = args.next(name);
+                        name = null;
+                    }
                     
                     int c = 0;
                     // MRI 1.8.5-p12 doesn't support 1-char strings, but
                     // YARV 0.4.1 does. I don't think it hurts to include
                     // this; sprintf('%c','a') is nicer than sprintf('%c','a'[0])
                     if (arg instanceof RubyString) {
                         ByteList bytes = ((RubyString)arg).getByteList();
                         if (bytes.length() == 1) {
                             c = bytes.getUnsafeBytes()[bytes.begin()];
                         } else {
                             raiseArgumentError(args,"%c requires a character");
                         }
                     } else {
                         c = args.intValue(arg);
                     }
                     if ((flags & FLAG_WIDTH) != 0 && width > 1) {
                         if ((flags & FLAG_MINUS) != 0) {
                             buf.append(c);
                             buf.fill(' ', width-1);
                         } else {
                             buf.fill(' ',width-1);
                             buf.append(c);
                         }
                     } else {
                         buf.append(c);
                     }
                     offset++;
                     incomplete = false;
                     break;
                 }
                 case 'p':
                 case 's': {
-                    if (arg == null) arg = args.next();
+                    if (arg == null || name != null) {
+                        arg = args.next(name);
+                        name = null;
+                    }
 
                     if (fchar == 'p') {
                         arg = arg.callMethod(arg.getRuntime().getCurrentContext(),"inspect");
                     }
                     ByteList bytes = arg.asString().getByteList();
                     int len = bytes.length();
                     if (arg.isTaint()) tainted = true;
                     if ((flags & FLAG_PRECISION) != 0 && precision < len) {
                         len = precision;
                     }
                     // TODO: adjust length so it won't fall in the middle 
                     // of a multi-byte character. MRI's sprintf.c uses tables
                     // in a modified version of regex.c, which assume some
                     // particular  encoding for a given installation/application.
                     // (See regex.c#re_mbcinit in ruby-1.8.5-p12) 
                     //
                     // This is only an issue if the user specifies a precision
                     // that causes the string to be truncated. The same issue
                     // would arise taking a substring of a ByteList-backed RubyString.
 
                     if ((flags & FLAG_WIDTH) != 0 && width > len) {
                         width -= len;
                         if ((flags & FLAG_MINUS) != 0) {
                             buf.append(bytes.getUnsafeBytes(),bytes.begin(),len);
                             buf.fill(' ',width);
                         } else {
                             buf.fill(' ',width);
                             buf.append(bytes.getUnsafeBytes(),bytes.begin(),len);
                         }
                     } else {
                         buf.append(bytes.getUnsafeBytes(),bytes.begin(),len);
                     }
                     offset++;
                     incomplete = false;
                     break;
                 }
                 case 'd':
                 case 'i':
                 case 'o':
                 case 'x':
                 case 'X':
                 case 'b':
                 case 'B':
                 case 'u': {
-                    if (arg == null) arg = args.next();
+                    if (arg == null || name != null) {
+                        arg = args.next(name);
+                        name = null;
+                    }
 
                     int type = arg.getMetaClass().index;
                     if (type != ClassIndex.FIXNUM && type != ClassIndex.BIGNUM) {
                         switch(type) {
                         case ClassIndex.FLOAT:
                             arg = RubyNumeric.dbl2num(arg.getRuntime(),((RubyFloat)arg).getValue());
                             break;
                         case ClassIndex.STRING:
                             arg = ((RubyString)arg).stringToInum(0, true);
                             break;
                         default:
                             if (arg.respondsTo("to_int")) {
                                 arg = TypeConverter.convertToType(arg, arg.getRuntime().getInteger(), "to_int", true);
                             } else {
                                 arg = TypeConverter.convertToType(arg, arg.getRuntime().getInteger(), "to_i", true);
                             }
                             break;
                         }
                         type = arg.getMetaClass().index;
                     }
                     byte[] bytes = null;
                     int first = 0;
                     byte[] prefix = null;
                     boolean sign;
                     boolean negative;
                     byte signChar = 0;
                     byte leadChar = 0;
                     int base;
 
                     // 'd' and 'i' are the same
                     if (fchar == 'i') fchar = 'd';
 
                     // 'u' with space or plus flags is same as 'd'
                     if (fchar == 'u' && (flags & (FLAG_SPACE | FLAG_PLUS)) != 0) {
                         fchar = 'd';
                     }
                     sign = (fchar == 'd' || (flags & (FLAG_SPACE | FLAG_PLUS)) != 0);
 
                     switch (fchar) {
                     case 'o':
                         base = 8; break;
                     case 'x':
                     case 'X':
                         base = 16; break;
                     case 'b':
                     case 'B':
                         base = 2; break;
                     case 'u':
                     case 'd':
                     default:
                         base = 10; break;
                     }
                     // We depart here from strict adherence to MRI code, as MRI
                     // uses C-sprintf, in part, to format numeric output, while
                     // we'll use Java's numeric formatting code (and our own).
                     boolean zero;
                     if (type == ClassIndex.FIXNUM) {
                         negative = ((RubyFixnum)arg).getLongValue() < 0;
                         zero = ((RubyFixnum)arg).getLongValue() == 0;
                         if (negative && fchar == 'u') {
                             bytes = getUnsignedNegativeBytes((RubyFixnum)arg);
                         } else {
                             bytes = getFixnumBytes((RubyFixnum)arg,base,sign,fchar=='X');
                         }
                     } else {
                         negative = ((RubyBignum)arg).getValue().signum() < 0;
                         zero = ((RubyBignum)arg).getValue().equals(BigInteger.ZERO);
                         if (negative && fchar == 'u' && usePrefixForZero) {
                             bytes = getUnsignedNegativeBytes((RubyBignum)arg);
                         } else {
                             bytes = getBignumBytes((RubyBignum)arg,base,sign,fchar=='X');
                         }
                     }
                     if ((flags & FLAG_SHARP) != 0) {
                         if (!zero || usePrefixForZero) {
                             switch (fchar) {
                             case 'o': prefix = PREFIX_OCTAL; break;
                             case 'x': prefix = PREFIX_HEX_LC; break;
                             case 'X': prefix = PREFIX_HEX_UC; break;
                             case 'b': prefix = PREFIX_BINARY_LC; break;
                             case 'B': prefix = PREFIX_BINARY_UC; break;
                             }
                         }
                         if (prefix != null) width -= prefix.length;
                     }
                     int len = 0;
                     if (sign) {
                         if (negative) {
                             signChar = '-';
                             width--;
                             first = 1; // skip '-' in bytes, will add where appropriate
                         } else if ((flags & FLAG_PLUS) != 0) {
                             signChar = '+';
                             width--;
                         } else if ((flags & FLAG_SPACE) != 0) {
                             signChar = ' ';
                             width--;
                         }
                     } else if (negative) {
                         if (base == 10) {
                             warning(ID.NEGATIVE_NUMBER_FOR_U, args, "negative number for %u specifier");
                             leadChar = '.';
                             len += 2;
                         } else {
                             if ((flags & (FLAG_PRECISION | FLAG_ZERO)) == 0) len += 2; // ..
 
                             first = skipSignBits(bytes,base);
                             switch(fchar) {
                             case 'b':
                             case 'B':
                                 leadChar = '1';
                                 break;
                             case 'o':
                                 leadChar = '7';
                                 break;
                             case 'x':
                                 leadChar = 'f';
                                 break;
                             case 'X':
                                 leadChar = 'F';
                                 break;
                             }
                             if (leadChar != 0) len++;
                         }
                     }
                     int numlen = bytes.length - first;
                     len += numlen;
                     
                     if ((flags & (FLAG_ZERO|FLAG_PRECISION)) == FLAG_ZERO) {
                         precision = width;
                         width = 0;
                     } else {
                         if (precision < len) precision = len;
 
                         width -= precision;
                     }
                     if ((flags & FLAG_MINUS) == 0) {
                         buf.fill(' ',width);
                         width = 0;
                     }
                     if (signChar != 0) buf.append(signChar);
                     if (prefix != null) buf.append(prefix);
 
                     if (len < precision) {
                         if (leadChar == 0) {
                             if (fchar != 'd' || usePrefixForZero || !negative ||
                                     ((flags & FLAG_ZERO) != 0 && (flags & FLAG_MINUS) == 0)) {
                                 buf.fill('0', precision - len);
                             }
                         } else if (leadChar == '.') {
                             buf.fill(leadChar,precision-len);
                             buf.append(PREFIX_NEGATIVE);
                         } else if (!usePrefixForZero) {
                             buf.append(PREFIX_NEGATIVE);
                             buf.fill(leadChar,precision - len - 1);
                         } else {
                             buf.fill(leadChar,precision-len+1); // the 1 is for the stripped sign char
                         }
                     } else if (leadChar != 0) {
                         if (((flags & (FLAG_PRECISION | FLAG_ZERO)) == 0 && usePrefixForZero) ||
                                 (!usePrefixForZero && "xXbBo".indexOf(fchar) != -1)) {
                             buf.append(PREFIX_NEGATIVE);
                         }
                         if (leadChar != '.') buf.append(leadChar);
                     }
                     buf.append(bytes,first,numlen);
 
                     if (width > 0) buf.fill(' ',width);
                     if (len < precision && fchar == 'd' && negative && 
                             !usePrefixForZero && (flags & FLAG_MINUS) != 0) {
                         buf.fill(' ', precision - len);
                     }
                                         
                     offset++;
                     incomplete = false;
                     break;
                 }
                 case 'E':
                 case 'e':
                 case 'f':
                 case 'G':
                 case 'g': {
-                    if (arg == null) arg = args.next();
+                    if (arg == null || name != null) {
+                        arg = args.next(name);
+                        name = null;
+                    }
                     
                     if (!(arg instanceof RubyFloat)) {
                         // FIXME: what is correct 'recv' argument?
                         // (this does produce the desired behavior)
                         if (usePrefixForZero) {
                             arg = RubyKernel.new_float(arg,arg);
                         } else {
                             arg = RubyKernel.new_float19(arg,arg);
                         }
                     }
                     double dval = ((RubyFloat)arg).getDoubleValue();
                     boolean nan = dval != dval;
                     boolean inf = dval == Double.POSITIVE_INFINITY || dval == Double.NEGATIVE_INFINITY;
                     boolean negative = dval < 0.0d || (dval == 0.0d && (new Float(dval)).equals(new Float(-0.0)));
                     
                     byte[] digits;
                     int nDigits = 0;
                     int exponent = 0;
 
                     int len = 0;
                     byte signChar;
                     
                     if (nan || inf) {
                         if (nan) {
                             digits = NAN_VALUE;
                             len = NAN_VALUE.length;
                         } else {
                             digits = INFINITY_VALUE;
                             len = INFINITY_VALUE.length;
                         }
                         if (negative) {
                             signChar = '-';
                             width--;
                         } else if ((flags & FLAG_PLUS) != 0) {
                             signChar = '+';
                             width--;
                         } else if ((flags & FLAG_SPACE) != 0) {
                             signChar = ' ';
                             width--;
                         } else {
                             signChar = 0;
                         }
                         width -= len;
                         
                         if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                             buf.fill(' ',width);
                             width = 0;
                         }
                         if (signChar != 0) buf.append(signChar);
 
                         if (width > 0 && (flags & FLAG_MINUS) == 0) {
                             buf.fill('0',width);
                             width = 0;
                         }
                         buf.append(digits);
                         if (width > 0) buf.fill(' ', width);
 
                         offset++;
                         incomplete = false;
                         break;
                     }
 
                     NumberFormat nf = NumberFormat.getNumberInstance(args.locale);
                     nf.setMaximumFractionDigits(Integer.MAX_VALUE);
                     String str = nf.format(dval);
                     
                     // grrr, arghh, want to subclass sun.misc.FloatingDecimal, but can't,
                     // so we must do all this (the next 70 lines of code), which has already
                     // been done by FloatingDecimal.
                     int strlen = str.length();
                     digits = new byte[strlen];
                     int nTrailingZeroes = 0;
                     int i = negative ? 1 : 0;
                     int decPos = 0;
                     byte ival;
                 int_loop:
                     for ( ; i < strlen ; ) {
                         switch(ival = (byte)str.charAt(i++)) {
                         case '0':
                             if (nDigits > 0) nTrailingZeroes++;
 
                             break; // switch
                         case '1': case '2': case '3': case '4':
                         case '5': case '6': case '7': case '8': case '9':
                             if (nTrailingZeroes > 0) {
                                 for ( ; nTrailingZeroes > 0 ; nTrailingZeroes-- ) {
                                     digits[nDigits++] = '0';
                                 }
                             }
                             digits[nDigits++] = ival;
                             break; // switch
                         case '.':
                             break int_loop;
                         }
                     }
                     decPos = nDigits + nTrailingZeroes;
                 dec_loop:
                     for ( ; i < strlen ; ) {
                         switch(ival = (byte)str.charAt(i++)) {
                         case '0':
                             if (nDigits > 0) {
                                 nTrailingZeroes++;
                             } else {
                                 exponent--;
                             }
                             break; // switch
                         case '1': case '2': case '3': case '4':
                         case '5': case '6': case '7': case '8': case '9':
                             if (nTrailingZeroes > 0) {
                                 for ( ; nTrailingZeroes > 0 ; nTrailingZeroes--  ) {
                                     digits[nDigits++] = '0';
                                 }
                             }
                             digits[nDigits++] = ival;
                             break; // switch
                         case 'E':
                             break dec_loop;
                         }
                     }
                     if (i < strlen) {
                         int expSign;
                         int expVal = 0;
                         if (str.charAt(i) == '-') {
                             expSign = -1;
                             i++;
                         } else {
                             expSign = 1;
                         }
                         for ( ; i < strlen ; ) {
                             expVal = expVal * 10 + ((int)str.charAt(i++)-(int)'0');
                         }
                         exponent += expVal * expSign;
                     }
                     exponent += decPos - nDigits;
 
                     // gotta have at least a zero...
                     if (nDigits == 0) {
                         digits[0] = '0';
                         nDigits = 1;
                         exponent = 0;
                     }
 
                     // OK, we now have the significand in digits[0...nDigits]
                     // and the exponent in exponent.  We're ready to format.
 
                     int intDigits, intZeroes, intLength;
                     int decDigits, decZeroes, decLength;
                     byte expChar;
 
                     if (negative) {
                         signChar = '-';
                         width--;
                     } else if ((flags & FLAG_PLUS) != 0) {
                         signChar = '+';
                         width--;
                     } else if ((flags & FLAG_SPACE) != 0) {
                         signChar = ' ';
                         width--;
                     } else {
                         signChar = 0;
                     }
                     if ((flags & FLAG_PRECISION) == 0) {
                         precision = 6;
                     }
                     
                     switch(fchar) {
                     case 'E':
                     case 'G':
                         expChar = 'E';
                         break;
                     case 'e':
                     case 'g':
                         expChar = 'e';
                         break;
                     default:
                         expChar = 0;
                     }
 
                     switch (fchar) {
                     case 'g':
                     case 'G':
                         // an empirically derived rule: precision applies to
                         // significand length, irrespective of exponent
 
                         // an official rule, clarified: if the exponent
                         // <clarif>after adjusting for exponent form</clarif>
                         // is < -4,  or the exponent <clarif>after adjusting 
                         // for exponent form</clarif> is greater than the
                         // precision, use exponent form
                         boolean expForm = (exponent + nDigits - 1 < -4 ||
                             exponent + nDigits > (precision == 0 ? 1 : precision));
                         // it would be nice (and logical!) if exponent form 
                         // behaved like E/e, and decimal form behaved like f,
                         // but no such luck. hence: 
                         if (expForm) {
                             // intDigits isn't used here, but if it were, it would be 1
                             /* intDigits = 1; */
                             decDigits = nDigits - 1;
                             // precision for G/g includes integer digits
                             precision = Math.max(0,precision - 1);
 
                             if (precision < decDigits) {
                                 int n = round(digits,nDigits,precision,precision!=0);
                                 if (n > nDigits) nDigits = n;
                                 decDigits = Math.min(nDigits - 1,precision);
                             }
                             exponent += nDigits - 1;
                             
                             boolean isSharp = (flags & FLAG_SHARP) != 0;
 
                             // deal with length/width
 			    
                             len++; // first digit is always printed
 
                             // MRI behavior: Be default, 2 digits
                             // in the exponent. Use 3 digits
                             // only when necessary.
                             // See comment for writeExp method for more details.
                             if (exponent > 99) {
                             	len += 5; // 5 -> e+nnn / e-nnn
                             } else {
                             	len += 4; // 4 -> e+nn / e-nn
                             }
 
                             if (isSharp) {
                             	// in this mode, '.' is always printed
                             	len++;
                             }
 
                             if (precision > 0) {
                             	if (!isSharp) {
                             	    // MRI behavior: In this mode
                             	    // trailing zeroes are removed:
                             	    // 1.500E+05 -> 1.5E+05 
                             	    int j = decDigits;
                             	    for (; j >= 1; j--) {
                             	        if (digits[j]== '0') {
                             	            decDigits--;
                             	        } else {
                             	            break;
                             	        }
                             	    }
 
                             	    if (decDigits > 0) {
                             	        len += 1; // '.' is printed
                             	        len += decDigits;
                             	    }
                             	} else  {
                             	    // all precision numebers printed
                             	    len += precision;
                             	}
                             }
 
                             width -= len;
 
                             if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                                 buf.fill(' ',width);
                                 width = 0;
                             }
                             if (signChar != 0) {
                                 buf.append(signChar);
                             }
                             if (width > 0 && (flags & FLAG_MINUS) == 0) {
                                 buf.fill('0',width);
                                 width = 0;
                             }
 
                             // now some data...
                             buf.append(digits[0]);
 
                             boolean dotToPrint = isSharp
                                     || (precision > 0 && decDigits > 0);
 
                             if (dotToPrint) {
                             	buf.append(args.getDecimalSeparator()); // '.'
                             }
 
                             if (precision > 0 && decDigits > 0) {
                             	buf.append(digits, 1, decDigits);
                             	precision -= decDigits;
                             }
 
                             if (precision > 0 && isSharp) {
                             	buf.fill('0', precision);
                             }
 
                             writeExp(buf, exponent, expChar);
 
                             if (width > 0) {
                                 buf.fill(' ', width);
                             }
                         } else { // decimal form, like (but not *just* like!) 'f'
                             intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                             intZeroes = Math.max(0,exponent);
                             intLength = intDigits + intZeroes;
                             decDigits = nDigits - intDigits;
                             decZeroes = Math.max(0,-(decDigits + exponent));
                             decLength = decZeroes + decDigits;
                             precision = Math.max(0,precision - intLength);
                             
                             if (precision < decDigits) {
                                 int n = round(digits,nDigits,intDigits+precision-1,precision!=0);
                                 if (n > nDigits) {
                                     // digits array shifted, update all
                                     nDigits = n;
                                     intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                                     intLength = intDigits + intZeroes;
                                     decDigits = nDigits - intDigits;
                                     decZeroes = Math.max(0,-(decDigits + exponent));
                                     precision = Math.max(0,precision-1);
                                 }
                                 decDigits = precision;
                                 decLength = decZeroes + decDigits;
                             }
                             len += intLength;
                             if (decLength > 0) {
                                 len += decLength + 1;
                             } else {
                                 if ((flags & FLAG_SHARP) != 0) {
                                     len++; // will have a trailing '.'
                                     if (precision > 0) { // g fills trailing zeroes if #
                                         len += precision;
                                     }
                                 }
                             }
                             
                             width -= len;
                             
                             if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                                 buf.fill(' ',width);
                                 width = 0;
                             }
                             if (signChar != 0) {
                                 buf.append(signChar);
                             }
                             if (width > 0 && (flags & FLAG_MINUS) == 0) {
                                 buf.fill('0',width);
                                 width = 0;
                             }
                             // now some data...
                             if (intLength > 0){
                                 if (intDigits > 0) { // s/b true, since intLength > 0
                                     buf.append(digits,0,intDigits);
                                 }
                                 if (intZeroes > 0) {
                                     buf.fill('0',intZeroes);
                                 }
                             } else {
                                 // always need at least a 0
                                 buf.append('0');
                             }
                             if (decLength > 0 || (flags & FLAG_SHARP) != 0) {
                                 buf.append(args.getDecimalSeparator());
                             }
                             if (decLength > 0) {
                                 if (decZeroes > 0) {
                                     buf.fill('0',decZeroes);
                                     precision -= decZeroes;
                                 }
                                 if (decDigits > 0) {
                                     buf.append(digits,intDigits,decDigits);
                                     precision -= decDigits;
                                 }
                                 if ((flags & FLAG_SHARP) != 0 && precision > 0) {
                                     buf.fill('0',precision);
                                 }
                             }
                             if ((flags & FLAG_SHARP) != 0 && precision > 0) buf.fill('0',precision);
                             if (width > 0) buf.fill(' ', width);
                         }
                         break;
                     
                     case 'f':
                         intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                         intZeroes = Math.max(0,exponent);
                         intLength = intDigits + intZeroes;
                         decDigits = nDigits - intDigits;
                         decZeroes = Math.max(0,-(decDigits + exponent));
                         decLength = decZeroes + decDigits;                                     
 
                         if (precision < decLength) {
                             if (precision < decZeroes) {
                                 decDigits = 0;
                                 decZeroes = precision;
                             } else {
                                 int n = round(digits, nDigits, intDigits+precision-decZeroes-1, false);
                                 if (n > nDigits) {
                                     // digits arr shifted, update all
                                     nDigits = n;
                                     intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                                     intLength = intDigits + intZeroes;
                                     decDigits = nDigits - intDigits;
                                     decZeroes = Math.max(0,-(decDigits + exponent));
                                     decLength = decZeroes + decDigits;
                                 }
                                 decDigits = precision - decZeroes;
                             }
                             decLength = decZeroes + decDigits;
                         }
                         if (precision > 0) {
                             len += Math.max(1,intLength) + 1 + precision;
                             // (1|intlen).prec
                         } else {
                             len += Math.max(1,intLength);
                             // (1|intlen)
                             if ((flags & FLAG_SHARP) != 0) {
                                 len++; // will have a trailing '.'
                             }
                         }
                         
                         width -= len;
                         
                         if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                             buf.fill(' ',width);
                             width = 0;
                         }
                         if (signChar != 0) {
                             buf.append(signChar);
                         }
                         if (width > 0 && (flags & FLAG_MINUS) == 0) {
                             buf.fill('0',width);
                             width = 0;
                         }
                         // now some data...
                         if (intLength > 0){
                             if (intDigits > 0) { // s/b true, since intLength > 0
                                 buf.append(digits,0,intDigits);
                             }
                             if (intZeroes > 0) {
                                 buf.fill('0',intZeroes);
                             }
                         } else {
                             // always need at least a 0
                             buf.append('0');
                         }
                         if (precision > 0 || (flags & FLAG_SHARP) != 0) {
                             buf.append(args.getDecimalSeparator());
                         }
                         if (precision > 0) {
                             if (decZeroes > 0) {
                                 buf.fill('0',decZeroes);
                                 precision -= decZeroes;
                             }
                             if (decDigits > 0) {
                                 buf.append(digits,intDigits,decDigits);
                                 precision -= decDigits;
                             }
                             // fill up the rest with zeroes
                             if (precision > 0) {
                                 buf.fill('0',precision);
                             }
                         }
                         if (width > 0) {
                             buf.fill(' ', width);
                         }
                         break;
                     case 'E':
                     case 'e':
                         // intDigits isn't used here, but if it were, it would be 1
                         /* intDigits = 1; */
                         decDigits = nDigits - 1;
                         
                         if (precision < decDigits) {
                             int n = round(digits,nDigits,precision,precision!=0);
                             if (n > nDigits) {
                                 nDigits = n;
                             }
                             decDigits = Math.min(nDigits - 1,precision);
                         }
                         exponent += nDigits - 1;
 
                         boolean isSharp = (flags & FLAG_SHARP) != 0;
 
                         // deal with length/width
 
                         len++; // first digit is always printed
 
                         // MRI behavior: Be default, 2 digits
                         // in the exponent. Use 3 digits
                         // only when necessary.
                         // See comment for writeExp method for more details.
                         if (exponent > 99) {
                             len += 5; // 5 -> e+nnn / e-nnn
                         } else {
                             len += 4; // 4 -> e+nn / e-nn
                         }
 
                         if (precision > 0) {
                             // '.' and all precision digits printed
                             len += 1 + precision;
                         } else  if (isSharp) {
                             len++;  // in this mode, '.' is always printed
                         }
 
                         width -= len;
 
                         if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                             buf.fill(' ',width);
                             width = 0;
                         }
                         if (signChar != 0) {
                             buf.append(signChar);
                         }
                         if (width > 0 && (flags & FLAG_MINUS) == 0) {
                             buf.fill('0',width);
                             width = 0;
                         }
                         // now some data...
                         buf.append(digits[0]);
                         if (precision > 0) {
                             buf.append(args.getDecimalSeparator()); // '.'
                             if (decDigits > 0) {
                                 buf.append(digits,1,decDigits);
                                 precision -= decDigits;
                             }
                             if (precision > 0) buf.fill('0',precision);
 
                         } else if ((flags & FLAG_SHARP) != 0) {
                             buf.append(args.getDecimalSeparator());
                         }
 
                         writeExp(buf, exponent, expChar);
 
                         if (width > 0) buf.fill(' ', width);
                         break;
                     } // switch (format char E,e,f,G,g)
                     
                     offset++;
                     incomplete = false;
                     break;
                 } // block (case E,e,f,G,g)
                 } // switch (each format char in spec)
             } // for (each format spec)
             
             // equivalent to MRI case '\0':
             if (incomplete) {
                 if (flags == FLAG_NONE) {
                     // dangling '%' char
                     buf.append('%');
                 } else {
                     raiseArgumentError(args,ERR_ILLEGAL_FORMAT_CHAR);
                 }
             }
         } // main while loop (offset < length)
 
         // MRI behavior: validate only the unnumbered arguments
         if ((args.numbered == 0) && args.unnumbered < args.length) {
             if (args.runtime.getDebug().isTrue()) {
                 args.raiseArgumentError("too many arguments for format string");
             } else if (args.runtime.isVerbose()) {
                 args.warn(ID.TOO_MANY_ARGUMENTS, "too many arguments for format string");
             }
         }
 
         return tainted;
     }
 
     private static void writeExp(ByteList buf, int exponent, byte expChar) {
         // Unfortunately, the number of digits in the exponent is
         // not clearly defined in Ruby documentation. This is a
         // platform/version-dependent behavior. On Linux/Mac/Cygwin/*nix,
         // two digits are used. On Windows, 3 digits are used.
         // It is desirable for JRuby to have consistent behavior, and
         // the two digits behavior was selected. This is also in sync
         // with "Java-native" sprintf behavior (java.util.Formatter).
         buf.append(expChar); // E or e
         buf.append(exponent >= 0 ? '+' : '-');
         if (exponent < 0) {
             exponent = -exponent;
         }
         if (exponent > 99) {                                
             buf.append(exponent / 100 + '0');
             buf.append(exponent % 100 / 10 + '0');
         } else {
             buf.append(exponent / 10 + '0');
         }
         buf.append(exponent % 10 + '0');
     }
 
     // debugging code, keeping for now
     /*
     private static final void showLiteral(byte[] format, int start, int offset) {
         System.out.println("literal: ["+ new String(format,start,offset-start)+ "], " +
                 " s="+ start + " o="+ offset);
     }
     
     // debugging code, keeping for now
     private static final void showVals(byte[] format,int start,int offset, byte fchar,
             int flags, int width, int precision, Object arg) {
         System.out.println(new StringBuffer()
         .append("value: ").append(new String(format,start,offset-start+1)).append('\n')
         .append("type: ").append((char)fchar).append('\n')
         .append("start: ").append(start).append('\n')
         .append("length: ").append(offset-start).append('\n')
         .append("flags: ").append(Integer.toBinaryString(flags)).append('\n')
         .append("width: ").append(width).append('\n')
         .append("precision: ").append(precision).append('\n')
         .append("arg: ").append(arg).append('\n')
         .toString());
         
     }
     */
     
     private static void raiseArgumentError(Args args, String message) {
         args.raiseArgumentError(message);
     }
     
     private static void warning(ID id, Args args, String message) {
         args.warning(id, message);
     }
     
     private static void checkOffset(Args args, int offset, int length, String message) {
         if (offset >= length) {
             raiseArgumentError(args,message);
         }
     }
 
     private static int extendWidth(Args args, int oldWidth, byte newChar) {
         int newWidth = oldWidth * 10 + (newChar - '0');
         if (newWidth / 10 != oldWidth) raiseArgumentError(args,"width too big");
         return newWidth;
     }
     
     private static boolean isDigit(byte aChar) {
         return (aChar >= '0' && aChar <= '9');
     }
     
     private static boolean isPrintable(byte aChar) {
         return (aChar > 32 && aChar < 127);
     }
 
     private static int skipSignBits(byte[] bytes, int base) {
         int skip = 0;
         int length = bytes.length;
         byte b;
         switch(base) {
         case 2:
             for ( ; skip < length && bytes[skip] == '1'; skip++ ) {}
             break;
         case 8:
             if (length > 0 && bytes[0] == '3') skip++;
             for ( ; skip < length && bytes[skip] == '7'; skip++ ) {}
             break;
         case 10:
             if (length > 0 && bytes[0] == '-') skip++;
             break;
         case 16:
             for ( ; skip < length && ((b = bytes[skip]) == 'f' || b == 'F'); skip++ ) {}
         }
         return skip;
     }
     
     private static int round(byte[] bytes, int nDigits, int roundPos, boolean roundDown) {
         int next = roundPos + 1;
         if (next >= nDigits || bytes[next] < '5' ||
                 // MRI rounds up on nnn5nnn, but not nnn5 --
                 // except for when they do
                 (roundDown && bytes[next] == '5' && next == nDigits - 1)) {
             return nDigits;
         }
         if (roundPos < 0) { // "%.0f" % 0.99
             System.arraycopy(bytes,0,bytes,1,nDigits);
             bytes[0] = '1';
             return nDigits + 1;
         }
         bytes[roundPos] += 1;
         while (bytes[roundPos] > '9') {
             bytes[roundPos] = '0';
             roundPos--;
             if (roundPos >= 0) {
                 bytes[roundPos] += 1;
             } else {
                 System.arraycopy(bytes,0,bytes,1,nDigits);
                 bytes[0] = '1';
                 return nDigits + 1;
             }
         }
         return nDigits;
     }
 
     private static byte[] getFixnumBytes(RubyFixnum arg, int base, boolean sign, boolean upper) {
         long val = arg.getLongValue();
 
         // limit the length of negatives if possible (also faster)
         if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) {
             if (sign) {
                 return ConvertBytes.intToByteArray((int)val,base,upper);
             } else {
                 switch(base) {
                 case 2:  return ConvertBytes.intToBinaryBytes((int)val);
                 case 8:  return ConvertBytes.intToOctalBytes((int)val);
                 case 10:
                 default: return ConvertBytes.intToCharBytes((int)val);
                 case 16: return ConvertBytes.intToHexBytes((int)val,upper);
                 }
             }
         } else {
             if (sign) {
                 return ConvertBytes.longToByteArray(val,base,upper);
             } else {
                 switch(base) {
                 case 2:  return ConvertBytes.longToBinaryBytes(val);
                 case 8:  return ConvertBytes.longToOctalBytes(val);
                 case 10:
                 default: return ConvertBytes.longToCharBytes(val);
                 case 16: return ConvertBytes.longToHexBytes(val,upper);
                 }
             }
         }
     }
     
     private static byte[] getBignumBytes(RubyBignum arg, int base, boolean sign, boolean upper) {
         BigInteger val = arg.getValue();
         if (sign || base == 10 || val.signum() >= 0) {
             return stringToBytes(val.toString(base),upper);
         }
 
         // negative values
         byte[] bytes = val.toByteArray();
         switch(base) {
         case 2:  return ConvertBytes.twosComplementToBinaryBytes(bytes);
         case 8:  return ConvertBytes.twosComplementToOctalBytes(bytes);
         case 16: return ConvertBytes.twosComplementToHexBytes(bytes,upper);
         default: return stringToBytes(val.toString(base),upper);
         }
     }
     
     private static byte[] getUnsignedNegativeBytes(RubyInteger arg) {
         // calculation for negatives when %u specified
         // for values >= Integer.MIN_VALUE * 2, MRI uses (the equivalent of)
         //   long neg_u = (((long)Integer.MAX_VALUE + 1) << 1) + val
         // for smaller values, BigInteger math is required to conform to MRI's
         // result.
         long longval;
         BigInteger bigval;
         
         if (arg instanceof RubyFixnum) {
             // relatively cheap test for 32-bit values
             longval = ((RubyFixnum)arg).getLongValue();
             if (longval >= Long.MIN_VALUE << 1) {
                 return ConvertBytes.longToCharBytes(((Long.MAX_VALUE + 1L) << 1) + longval);
             }
             // no such luck...
             bigval = BigInteger.valueOf(longval);
         } else {
             bigval = ((RubyBignum)arg).getValue();
         }
         // ok, now it gets expensive...
         int shift = 0;
         // go through negated powers of 32 until we find one small enough 
         for (BigInteger minus = BIG_MINUS_64 ;
                 bigval.compareTo(minus) < 0 ;
                 minus = minus.shiftLeft(32), shift++) {}
         // add to the corresponding positive power of 32 for the result.
         // meaningful? no. conformant? yes. I just write the code...
         BigInteger nPower32 = shift > 0 ? BIG_64.shiftLeft(32 * shift) : BIG_64;
         return stringToBytes(nPower32.add(bigval).toString(),false);
     }
     
     private static byte[] stringToBytes(CharSequence s, boolean upper) {
         int len = s.length();
         byte[] bytes = new byte[len];
         if (upper) {
             for (int i = len; --i >= 0; ) {
                 int b = (byte)((int)s.charAt(i) & (int)0xff);
                 if (b >= 'a' && b <= 'z') {
                     bytes[i] = (byte)(b & ~0x20);
                 } else {
                     bytes[i] = (byte)b;
                 }
             }
         } else {
             for (int i = len; --i >= 0; ) {
                 bytes[i] = (byte)((int)s.charAt(i) & (int)0xff); 
             }
         }
         return bytes;
     }
 }
