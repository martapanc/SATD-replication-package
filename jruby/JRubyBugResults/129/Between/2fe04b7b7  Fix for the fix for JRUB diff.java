diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 0b942c3fc3..afdfcd4133 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -683,2001 +683,2001 @@ public class RubyModule extends RubyObject {
                     singletonClass = module.getSingletonClass();
                     dynamicMethod.setImplementationClass(singletonClass);
 
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = desc.name;
                         singletonClass.addMethod(baseName, dynamicMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             singletonClass.addMethod(name, dynamicMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             singletonClass.defineAlias(alias, baseName);
                         }
                     }
                 } else {
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = desc.name;
                         module.addMethod(baseName, dynamicMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             module.addMethod(name, dynamicMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             module.defineAlias(alias, baseName);
                         }
                     }
 
                     if (jrubyMethod.module()) {
                         singletonClass = module.getSingletonClass();
                         // module/singleton methods are all defined public
                         DynamicMethod moduleMethod = dynamicMethod.dup();
                         moduleMethod.setVisibility(PUBLIC);
 
                         if (jrubyMethod.name().length == 0) {
                             baseName = desc.name;
                             singletonClass.addMethod(desc.name, moduleMethod);
                         } else {
                             baseName = jrubyMethod.name()[0];
                             for (String name : jrubyMethod.name()) {
                                 singletonClass.addMethod(name, moduleMethod);
                             }
                         }
 
                         if (jrubyMethod.alias().length > 0) {
                             for (String alias : jrubyMethod.alias()) {
                                 singletonClass.defineAlias(alias, baseName);
                             }
                         }
                     }
                 }
             }
         }
     };
     
     public boolean defineAnnotatedMethod(String name, List<JavaMethodDescriptor> methods, MethodFactory methodFactory) {
         JavaMethodDescriptor desc = methods.get(0);
         if (methods.size() == 1) {
             return defineAnnotatedMethod(desc, methodFactory);
         } else {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, methods);
             methodDefiningCallback.define(this, desc, dynamicMethod);
             
             return true;
         }
     }
     
     public boolean defineAnnotatedMethod(Method method, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             methodDefiningCallback.define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
     
     public boolean defineAnnotatedMethod(JavaMethodDescriptor desc, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = desc.anno;
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             methodDefiningCallback.define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 PRIVATE : PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method, Visibility visibility) {
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, PRIVATE));
     }
 
     public void defineFastProtectedMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, PROTECTED));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(ThreadContext context, String name) {
         Ruby runtime = context.getRuntime();
         
         if (this == runtime.getObject()) runtime.secure(4);
 
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw new SecurityException("Insecure: can't undef");
         }
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.UNDEFINING_BAD, "undefining `"+ name +"' may cause serious problem");
         }
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             String s0 = " class";
             RubyModule c = this;
 
             if (c.isSingleton()) {
                 IRubyObject obj = ((MetaClass)c).getAttached();
 
                 if (obj != null && obj instanceof RubyModule) {
                     c = (RubyModule) obj;
                     s0 = "";
                 }
             } else if (c.isModule()) {
                 s0 = " module";
             }
 
             throw runtime.newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_undefined", runtime.newSymbol(name));
         } else {
             callMethod(context, "method_undefined", runtime.newSymbol(name));
         }
     }
 
     @JRubyMethod(name = "include?", required = 1)
     public IRubyObject include_p(ThreadContext context, IRubyObject arg) {
         if (!arg.isModule()) throw context.getRuntime().newTypeError(arg, context.getRuntime().getModule());
         RubyModule moduleToCompare = (RubyModule) arg;
 
         // See if module is in chain...Cannot match against itself so start at superClass.
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isSame(moduleToCompare)) return context.getRuntime().getTrue();
         }
         
         return context.getRuntime().getFalse();
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         Ruby runtime = getRuntime();
         
         if (this == runtime.getObject()) runtime.secure(4);
 
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw runtime.newSecurityError("Insecure: can't define method");
         }
         testFrozen("class/module");
 
         addMethodInternal(name, method);
     }
 
     public void addMethodInternal(String name, DynamicMethod method) {
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             getMethods().put(name, method);
             invalidateCacheDescendants();
         }
     }
 
     public void removeMethod(ThreadContext context, String name) {
         Ruby runtime = context.getRuntime();
         
         if (this == runtime.getObject()) runtime.secure(4);
 
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw runtime.newSecurityError("Insecure: can't remove method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             DynamicMethod method = (DynamicMethod) getMethods().remove(name);
             if (method == null) {
                 throw runtime.newNameError("method '" + name + "' not defined in " + getName(), name);
             }
 
             invalidateCacheDescendants();
         }
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_removed", runtime.newSymbol(name));
         } else {
             callMethod(context, "method_removed", runtime.newSymbol(name));
         }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */    
     public DynamicMethod searchMethod(String name) {
         return searchWithCache(name).method;
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */    
     public CacheEntry searchWithCache(String name) {
         CacheEntry entry = cacheHit(name);
 
         if (entry != null) return entry;
 
         // we grab serial number first; the worst that will happen is we cache a later
         // update with an earlier serial number, which would just flush anyway
         int serial = getSerialNumber();
         DynamicMethod method = searchMethodInner(name);
 
         return method != null ? addToCache(name, method, serial) : addToCache(name, UndefinedMethod.getInstance(), serial);
     }
     
     public final int getSerialNumber() {
         return generation.hash;
     }
     
     private CacheEntry cacheHit(String name) {
         CacheEntry cacheEntry = cachedMethods.get(name);
 
         if (cacheEntry != null) {
             if (cacheEntry.generation == getSerialNumber()) {
                 return cacheEntry;
             }
         }
         
         return null;
     }
     
     private CacheEntry addToCache(String name, DynamicMethod method, int serial) {
         CacheEntry entry = new CacheEntry(method, serial);
         cachedMethods.put(name, entry);
 
         return entry;
     }
     
     protected DynamicMethod searchMethodInner(String name) {
         DynamicMethod method = getMethods().get(name);
         
         if (method != null) return method;
         
         return superClass == null ? null : superClass.searchMethodInner(name);
     }
 
     protected void invalidateCacheDescendants() {
         generation.update();
         // update all hierarchies into which this module has been included
         if (includingHierarchies != null) for (RubyClass includingHierarchy : includingHierarchies) {
             includingHierarchy.invalidateCacheDescendants();
         }
     }
     
     protected void invalidateConstantCache() {
         getRuntime().incrementConstantGeneration();
     }    
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod retrieveMethod(String name) {
         return getMethods().get(name);
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public RubyModule findImplementer(RubyModule clazz) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.isSame(clazz)) return module;
         }
 
         return null;
     }
 
     public void addModuleFunction(String name, DynamicMethod method) {
         addMethod(name, method);
         getSingletonClass().addMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineModuleFunction(String name, Callback method) {
         definePrivateMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void definePublicModuleFunction(String name, Callback method) {
         defineMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastModuleFunction(String name, Callback method) {
         defineFastPrivateMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastPublicModuleFunction(String name, Callback method) {
         defineFastMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_alias
      *
      */
     public synchronized void defineAlias(String name, String oldName) {
         testFrozen("module");
         if (oldName.equals(name)) {
             return;
         }
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = runtime.getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw runtime.newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
 
         invalidateCacheDescendants();
         putMethod(name, new AliasMethod(this, method, oldName));
     }
 
     public synchronized void defineAliases(List<String> aliases, String oldName) {
         testFrozen("module");
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = runtime.getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw runtime.newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
 
         for (String name: aliases) {
             if (oldName.equals(name)) continue;
 
             putMethod(name, new AliasMethod(this, method, oldName));
         }
         invalidateCacheDescendants();
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
 
         Ruby runtime = getRuntime();
         IRubyObject classObj = getConstantAt(name);
         RubyClass clazz;
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw runtime.newTypeError(name + " is not a class");
             clazz = (RubyClass)classObj;
 
             if (superClazz != null) {
                 RubyClass tmp = clazz.getSuperClass();
                 while (tmp != null && tmp.isIncluded()) tmp = tmp.getSuperClass(); // need to skip IncludedModuleWrappers
                 if (tmp != null) tmp = tmp.getRealClass();
                 if (tmp != superClazz) throw runtime.newTypeError("superclass mismatch for class " + name);
                 // superClazz = null;
             }
 
             if (runtime.getSafeLevel() >= 4) throw runtime.newTypeError("extending class prohibited");
         } else if (classProviders != null && (clazz = searchProvidersForClass(name, superClazz)) != null) {
             // reopen a java class
         } else {
             if (superClazz == null) superClazz = runtime.getObject();
             clazz = RubyClass.newClass(runtime, superClazz, name, superClazz.getAllocator(), this, true);
         }
 
         return clazz;
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyModule defineOrGetModuleUnder(String name) {
         // This method is intended only for defining new modules in Ruby code
         Ruby runtime = getRuntime();
         IRubyObject moduleObj = getConstantAt(name);
         RubyModule module;
         if (moduleObj != null) {
             if (!moduleObj.isModule()) throw runtime.newTypeError(name + " is not a module");
             if (runtime.getSafeLevel() >= 4) throw runtime.newSecurityError("extending module prohibited");
             module = (RubyModule)moduleObj;
         } else if (classProviders != null && (module = searchProvidersForModule(name)) != null) {
             // reopen a java module
         } else {
             module = RubyModule.newModule(runtime, name, this, true); 
         }
         return module;
     }
 
     /** rb_define_class_under
      *  this method should be used only as an API to define/open nested classes 
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator) {
         return getRuntime().defineClassUnder(name, superClass, allocator, this);
     }
 
     /** rb_define_module_under
      *  this method should be used only as an API to define/open nested module
      */
     public RubyModule defineModuleUnder(String name) {
         return getRuntime().defineModuleUnder(name, this);
     }
 
     // FIXME: create AttrReaderMethod, AttrWriterMethod, for faster attr access
     private void addAccessor(ThreadContext context, String internedName, boolean readable, boolean writeable) {
         assert internedName == internedName.intern() : internedName + " is not interned";
 
         final Ruby runtime = context.getRuntime();
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = context.getCurrentVisibility();
         if (attributeScope == PRIVATE) {
             //FIXME warning
         } else if (attributeScope == MODULE_FUNCTION) {
             attributeScope = PRIVATE;
             // FIXME warning
         }
         final String variableName = ("@" + internedName).intern();
         if (readable) {
             addMethod(internedName, new JavaMethodZero(this, attributeScope, CallConfiguration.FrameNoneScopeNone) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                     IRubyObject variable = self.getInstanceVariables().fastGetInstanceVariable(variableName);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
         if (writeable) {
             internedName = (internedName + "=").intern();
             addMethod(internedName, new JavaMethodOne(this, attributeScope, CallConfiguration.FrameNoneScopeNone) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1) {
                     return self.getInstanceVariables().fastSetInstanceVariable(variableName, arg1);
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
     }
 
     /** set_method_visibility
      *
      */
     public void setMethodVisibility(IRubyObject[] methods, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         for (int i = 0; i < methods.length; i++) {
             exportMethod(methods[i].asJavaString(), visibility);
         }
     }
 
     /** rb_export_method
      *
      */
     public void exportMethod(String name, Visibility visibility) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 // FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
                 addMethod(name, new WrapperMethod(this, method, visibility));
             }
 
             invalidateCacheDescendants();
         }
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility() == PRIVATE);
         }
         return false;
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String name, boolean bound) {
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method `" + name +
                 "' for class `" + this.getName() + "'", name);
         }
 
         RubyModule implementationModule = method.getImplementationClass();
         RubyModule originModule = this;
         while (originModule != implementationModule && originModule.isSingleton()) {
             originModule = ((MetaClass)originModule).getRealClass();
         }
 
         RubyMethod newMethod = null;
         if (bound) {
             newMethod = RubyMethod.newMethod(implementationModule, name, originModule, name, method, receiver);
         } else {
             newMethod = RubyUnboundMethod.newUnboundMethod(implementationModule, name, originModule, name, method);
         }
         newMethod.infectBy(this);
 
         return newMethod;
     }
 
     @JRubyMethod(name = "define_method", frame = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, Block block) {
         Ruby runtime = context.getRuntime();
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = context.getCurrentVisibility();
 
         if (visibility == MODULE_FUNCTION) visibility = PRIVATE;
         RubyProc proc = runtime.newProc(Block.Type.LAMBDA, block);
 
         // a normal block passed to define_method changes to do arity checking; make it a lambda
         proc.getBlock().type = Block.Type.LAMBDA;
         
         newMethod = createProcMethod(name, visibility, proc);
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, context.getPreviousVisibility(), context, runtime);
 
         return proc;
     }
     
     @JRubyMethod(name = "define_method", frame = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject body;
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = context.getCurrentVisibility();
 
         if (visibility == MODULE_FUNCTION) visibility = PRIVATE;
         if (runtime.getProc().isInstance(arg1)) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (RubyProc)arg1;
             body = proc;
 
             newMethod = createProcMethod(name, visibility, proc);
         } else if (runtime.getMethod().isInstance(arg1)) {
             RubyMethod method = (RubyMethod)arg1;
             body = method;
 
             newMethod = new MethodMethod(this, method.unbind(null), visibility);
         } else {
             throw runtime.newTypeError("wrong argument type " + arg1.getType().getName() + " (expected Proc/Method)");
         }
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, context.getPreviousVisibility(), context, runtime);
 
         return body;
     }
     @Deprecated
     public IRubyObject define_method(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1:
             return define_method(context, args[0], block);
         case 2:
             return define_method(context, args[0], args[1], block);
         default:
             throw context.getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 2)");
         }
     }
     
     private DynamicMethod createProcMethod(String name, Visibility visibility, RubyProc proc) {
         Block block = proc.getBlock();
         block.getBinding().getFrame().setKlazz(this);
         block.getBinding().getFrame().setName(name);
         
         StaticScope scope = block.getBody().getStaticScope();
 
         // for zsupers in define_method (blech!) we tell the proc scope to act as the "argument" scope
         scope.setArgumentScope(true);
 
         Arity arity = block.arity();
         // just using required is broken...but no more broken than before zsuper refactoring
         scope.setRequiredArgs(arity.required());
 
         if(!arity.isFixed()) {
             scope.setRestArg(arity.required());
         }
 
         return new ProcMethod(this, proc, visibility);
     }
 
     @Deprecated
     public IRubyObject executeUnder(ThreadContext context, Callback method, IRubyObject[] args, Block block) {
         context.preExecuteUnder(this, block);
         try {
             return method.execute(this, args, block);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     @JRubyMethod(name = "name")
     public IRubyObject name() {
         Ruby runtime = getRuntime();
         if (getBaseName() == null) {
             return RubyString.newEmptyString(runtime);
         } else {
             return runtime.newString(getName());
         }
     }
 
     @JRubyMethod(name = "name", compat = CompatVersion.RUBY1_9)
     public IRubyObject name19() {
         Ruby runtime = getRuntime();
         if (getBaseName() == null) {
             return runtime.getNil();
         } else {
             return runtime.newString(getName());
         }
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Map.Entry<String, DynamicMethod> entry : getMethods().entrySet()) {
             DynamicMethod method = entry.getValue();
             // Do not clone cached methods
             // FIXME: MRI copies all methods here
             if (method.getImplementationClass() == realType || method.isUndefined()) {
                 
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make DynamicMethod immutable
                 DynamicMethod clonedMethod = method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.putMethod(entry.getKey(), clonedMethod);
             }
         }
 
         return clone;
     }
 
     /** rb_mod_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original) {
         super.initialize_copy(original);
 
         RubyModule originalModule = (RubyModule)original;
 
         if (!getMetaClass().isSingleton()) setMetaClass(originalModule.getSingletonClassClone());
         setSuperClass(originalModule.getSuperClass());
         if (originalModule.hasVariables()) syncVariables(originalModule.getVariableList());
         syncConstants(originalModule);
 
         originalModule.cloneMethods(this);
 
         return this;
     }
 
     public void syncConstants(RubyModule other) {
         if (other.getConstantMap() != DUMMY_CONSTANTS) {
             getConstantMapForWrite().putAll(other.getConstantMap());
         }
     }
 
     /** rb_mod_included_modules
      *
      */
     @JRubyMethod(name = "included_modules")
     public RubyArray included_modules(ThreadContext context) {
         RubyArray ary = context.getRuntime().newArray();
 
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isIncluded()) {
                 ary.append(p.getNonIncludedClass());
             }
         }
 
         return ary;
     }
 
     /** rb_mod_ancestors
      *
      */
     @JRubyMethod(name = "ancestors")
     public RubyArray ancestors(ThreadContext context) {
         return context.getRuntime().newArray(getAncestorList());
     }
     
     @Deprecated
     public RubyArray ancestors() {
         return getRuntime().newArray(getAncestorList());
     }
 
     public List<IRubyObject> getAncestorList() {
         ArrayList<IRubyObject> list = new ArrayList<IRubyObject>();
 
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if(!module.isSingleton()) list.add(module.getNonIncludedClass());
         }
 
         return list;
     }
 
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.getNonIncludedClass() == type) return true;
         }
 
         return false;
     }
 
     @Override
     public int hashCode() {
         return id;
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
         return getRuntime().newFixnum(id);
     }
 
     /** rb_mod_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     @Override
     public IRubyObject to_s() {
         if(isSingleton()){            
             IRubyObject attached = ((MetaClass)this).getAttached();
             StringBuilder buffer = new StringBuilder("#<Class:");
             if (attached != null) { // FIXME: figure out why we get null sometimes
                 if(attached instanceof RubyClass || attached instanceof RubyModule){
                     buffer.append(attached.inspect());
                 }else{
                     buffer.append(attached.anyToString());
                 }
             }
             buffer.append(">");
             return getRuntime().newString(buffer.toString());
         }
         return getRuntime().newString(getName());
     }
 
     /** rb_mod_eqq
      *
      */
     @JRubyMethod(name = "===", required = 1)
     @Override
     public RubyBoolean op_eqq(ThreadContext context, IRubyObject obj) {
         return context.getRuntime().newBoolean(isInstance(obj));
     }
 
     /**
      * We override equals here to provide a faster path, since equality for modules
      * is pretty cut and dried.
      * @param other The object to check for equality
      * @return true if reference equality, false otherwise
      */
     @Override
     public boolean equals(Object other) {
         return this == other;
     }
 
     @JRubyMethod(name = "==", required = 1)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         return super.op_equal(context, other);
     }
 
     /** rb_mod_freeze
      *
      */
     @JRubyMethod(name = "freeze")
     @Override
     public final IRubyObject freeze(ThreadContext context) {
         to_s();
         return super.freeze(context);
     }
 
     /** rb_mod_le
     *
     */
     @JRubyMethod(name = "<=", required = 1)
    public IRubyObject op_le(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         if (isKindOfModule((RubyModule) obj)) return getRuntime().getTrue();
         if (((RubyModule) obj).isKindOfModule(this)) return getRuntime().getFalse();
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_lt
     *
     */
     @JRubyMethod(name = "<", required = 1)
    public IRubyObject op_lt(IRubyObject obj) {
         return obj == this ? getRuntime().getFalse() : op_le(obj);
     }
 
     /** rb_mod_ge
     *
     */
     @JRubyMethod(name = ">=", required = 1)
    public IRubyObject op_ge(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         return ((RubyModule) obj).op_le(this);
     }
 
     /** rb_mod_gt
     *
     */
     @JRubyMethod(name = ">", required = 1)
    public IRubyObject op_gt(IRubyObject obj) {
         return this == obj ? getRuntime().getFalse() : op_ge(obj);
     }
 
     /** rb_mod_cmp
     *
     */
     @JRubyMethod(name = "<=>", required = 1)
    public IRubyObject op_cmp(IRubyObject obj) {
         if (this == obj) return getRuntime().newFixnum(0);
         if (!(obj instanceof RubyModule)) return getRuntime().getNil();
 
         RubyModule module = (RubyModule) obj;
 
         if (module.isKindOfModule(this)) return getRuntime().newFixnum(1);
         if (this.isKindOfModule(module)) return getRuntime().newFixnum(-1);
 
         return getRuntime().getNil();
     }
 
     public boolean isKindOfModule(RubyModule type) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.isSame(type)) return true;
         }
 
         return false;
     }
 
     protected boolean isSame(RubyModule module) {
         return this == module;
     }
 
     /** rb_mod_initialize
      *
      */
     @JRubyMethod(name = "initialize", frame = true, visibility = PRIVATE)
     public IRubyObject initialize(Block block) {
         if (block.isGiven()) {
             // class and module bodies default to public, so make the block's visibility public. JRUBY-1185.
             block.getBinding().setVisibility(PUBLIC);
             block.yield(getRuntime().getCurrentContext(), this, this, this, false);
         }
 
         return getRuntime().getNil();
     }
     
     public void addReadWriteAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), true, true);
     }
     
     public void addReadAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), true, false);
     }
     
     public void addWriteAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), false, true);
     }
 
     /** rb_mod_attr
      *
      */
     @JRubyMethod(name = "attr", required = 1, optional = 1, visibility = PRIVATE, reads = VISIBILITY, compat = CompatVersion.RUBY1_8)
     public IRubyObject attr(ThreadContext context, IRubyObject[] args) {
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         addAccessor(context, args[0].asJavaString().intern(), true, writeable);
 
         return getRuntime().getNil();
     }
     
     /** rb_mod_attr/1.9
      *
      */
     @JRubyMethod(name = "attr", rest = true, visibility = PRIVATE, reads = VISIBILITY, compat = CompatVersion.RUBY1_9)
     public IRubyObject attr_1_9(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
-            addAccessor(context, args[i].asJavaString().intern(), true, true);
+            addAccessor(context, args[i].asJavaString().intern(), true, false);
         }
         return getRuntime().getNil();
     }
 
     @Deprecated
     public IRubyObject attr_reader(IRubyObject[] args) {
         return attr_reader(getRuntime().getCurrentContext(), args);
     }
     
     /** rb_mod_attr_reader
      *
      */
     @JRubyMethod(name = "attr_reader", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_reader(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), true, false);
         }
 
         return context.getRuntime().getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     @JRubyMethod(name = "attr_writer", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_writer(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), false, true);
         }
 
         return context.getRuntime().getNil();
     }
 
 
     @Deprecated
     public IRubyObject attr_accessor(IRubyObject[] args) {
         return attr_accessor(getRuntime().getCurrentContext(), args);
     }
 
     /** rb_mod_attr_accessor
      *
      */
     @JRubyMethod(name = "attr_accessor", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_accessor(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             // This is almost always already interned, since it will be called with a symbol in most cases
             // but when created from Java code, we might get an argument that needs to be interned.
             // addAccessor has as a precondition that the string MUST be interned
             addAccessor(context, args[i].asJavaString().intern(), true, true);
         }
 
         return context.getRuntime().getNil();
     }
 
     /**
      * Get a list of all instance methods names of the provided visibility unless not is true, then 
      * get all methods which are not the provided 
      * 
      * @param args passed into one of the Ruby instance_method methods
      * @param visibility to find matching instance methods against
      * @param not if true only find methods not matching supplied visibility
      * @return a RubyArray of instance method names
      */
     private RubyArray instance_methods(IRubyObject[] args, final Visibility visibility, boolean not, boolean useSymbols) {
         boolean includeSuper = args.length > 0 ? args[0].isTrue() : true;
         Ruby runtime = getRuntime();
         RubyArray ary = runtime.newArray();
         Set<String> seen = new HashSet<String>();
 
         for (RubyModule type = this; type != null; type = type.getSuperClass()) {
             RubyModule realType = type.getNonIncludedClass();
             for (Map.Entry entry : type.getMethods().entrySet()) {
                 String methodName = (String) entry.getKey();
 
                 if (! seen.contains(methodName)) {
                     seen.add(methodName);
 
                     DynamicMethod method = (DynamicMethod) entry.getValue();
                     if (method.getImplementationClass() == realType &&
                         (!not && method.getVisibility() == visibility || (not && method.getVisibility() != visibility)) &&
                         ! method.isUndefined()) {
 
                         ary.append(useSymbols ? runtime.newSymbol(methodName) : runtime.newString(methodName));
                     }
                 }
             }
 
             if (!includeSuper) {
                 break;
             }
         }
 
         return ary;
     }
 
     @JRubyMethod(name = "instance_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public RubyArray instance_methods(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, true, false);
     }
 
     @JRubyMethod(name = "instance_methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public RubyArray instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, true, true);
     }
 
     @JRubyMethod(name = "public_instance_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public RubyArray public_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PUBLIC, false, false);
     }
 
     @JRubyMethod(name = "public_instance_methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public RubyArray public_instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PUBLIC, false, false);
     }
 
     @JRubyMethod(name = "instance_method", required = 1)
     public IRubyObject instance_method(IRubyObject symbol) {
         return newMethod(null, symbol.asJavaString(), false);
     }
 
     /** rb_class_protected_instance_methods
      *
      */
     @JRubyMethod(name = "protected_instance_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public RubyArray protected_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PROTECTED, false, false);
     }
 
     @JRubyMethod(name = "protected_instance_methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public RubyArray protected_instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PROTECTED, false, true);
     }
 
     /** rb_class_private_instance_methods
      *
      */
     @JRubyMethod(name = "private_instance_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public RubyArray private_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, false, false);
     }
 
     @JRubyMethod(name = "private_instance_methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public RubyArray private_instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, false, true);
     }
 
     /** rb_mod_append_features
      *
      */
     @JRubyMethod(name = "append_features", required = 1, visibility = PRIVATE)
     public RubyModule append_features(IRubyObject module) {
         if (!(module instanceof RubyModule)) {
             // MRI error message says Class, even though Module is ok 
             throw getRuntime().newTypeError(module,getRuntime().getClassClass());
         }
         ((RubyModule) module).includeModule(this);
         return this;
     }
 
     /** rb_mod_extend_object
      *
      */
     @JRubyMethod(name = "extend_object", required = 1, visibility = PRIVATE)
     public IRubyObject extend_object(IRubyObject obj) {
         obj.getSingletonClass().includeModule(this);
         return obj;
     }
 
     /** rb_mod_include
      *
      */
     @JRubyMethod(name = "include", required = 1, rest = true, visibility = PRIVATE)
     public RubyModule include(IRubyObject[] modules) {
         ThreadContext context = getRuntime().getCurrentContext();
         // MRI checks all types first:
         for (int i = modules.length; --i >= 0; ) {
             IRubyObject obj = modules[i];
             if (!obj.isModule()) throw context.getRuntime().newTypeError(obj, context.getRuntime().getModule());
         }
         for (int i = modules.length - 1; i >= 0; i--) {
             modules[i].callMethod(context, "append_features", this);
             modules[i].callMethod(context, "included", this);
         }
 
         return this;
     }
 
     @JRubyMethod(name = "included", required = 1)
     public IRubyObject included(ThreadContext context, IRubyObject other) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "extended", required = 1, frame = true)
     public IRubyObject extended(ThreadContext context, IRubyObject other, Block block) {
         return context.getRuntime().getNil();
     }
 
     private void setVisibility(ThreadContext context, IRubyObject[] args, Visibility visibility) {
         if (context.getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw context.getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         if (args.length == 0) {
             // Note: we change current frames visibility here because the methods which call
             // this method are all "fast" (e.g. they do not created their own frame).
             context.setCurrentVisibility(visibility);
         } else {
             setMethodVisibility(args, visibility);
         }
     }
 
     /** rb_mod_public
      *
      */
     @JRubyMethod(name = "public", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule rbPublic(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, PUBLIC);
         return this;
     }
 
     /** rb_mod_protected
      *
      */
     @JRubyMethod(name = "protected", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule rbProtected(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, PROTECTED);
         return this;
     }
 
     /** rb_mod_private
      *
      */
     @JRubyMethod(name = "private", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule rbPrivate(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, PRIVATE);
         return this;
     }
 
     /** rb_mod_modfunc
      *
      */
     @JRubyMethod(name = "module_function", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule module_function(ThreadContext context, IRubyObject[] args) {
         if (context.getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw context.getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         if (args.length == 0) {
             context.setCurrentVisibility(MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, PRIVATE);
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asJavaString().intern();
                 DynamicMethod method = searchMethod(name);
                 assert !method.isUndefined() : "undefined method '" + name + "'";
                 getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), method, PUBLIC));
                 callMethod(context, "singleton_method_added", context.getRuntime().fastNewSymbol(name));
             }
         }
         return this;
     }
 
     @JRubyMethod(name = "method_added", required = 1, visibility = PRIVATE)
     public IRubyObject method_added(ThreadContext context, IRubyObject nothing) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "method_removed", required = 1, visibility = PRIVATE)
     public IRubyObject method_removed(ThreadContext context, IRubyObject nothing) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "method_undefined", required = 1, visibility = PRIVATE)
     public IRubyObject method_undefined(ThreadContext context, IRubyObject nothing) {
         return context.getRuntime().getNil();
     }
     
     @JRubyMethod(name = "method_defined?", required = 1)
     public RubyBoolean method_defined_p(ThreadContext context, IRubyObject symbol) {
         return isMethodBound(symbol.asJavaString(), true) ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "public_method_defined?", required = 1)
     public IRubyObject public_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
         
         return context.getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == PUBLIC);
     }
 
     @JRubyMethod(name = "protected_method_defined?", required = 1)
     public IRubyObject protected_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
 	    
         return context.getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == PROTECTED);
     }
 	
     @JRubyMethod(name = "private_method_defined?", required = 1)
     public IRubyObject private_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
 	    
         return context.getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == PRIVATE);
     }
 
     @JRubyMethod(name = "public_class_method", rest = true)
     public RubyModule public_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, PUBLIC);
         return this;
     }
 
     @JRubyMethod(name = "private_class_method", rest = true)
     public RubyModule private_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, PRIVATE);
         return this;
     }
 
     @JRubyMethod(name = "alias_method", required = 2, visibility = PRIVATE)
     public RubyModule alias_method(ThreadContext context, IRubyObject newId, IRubyObject oldId) {
         String newName = newId.asJavaString();
         defineAlias(newName, oldId.asJavaString());
         RubySymbol newSym = newId instanceof RubySymbol ? (RubySymbol)newId :
             context.getRuntime().newSymbol(newName);
         if (isSingleton()) {
             ((MetaClass)this).getAttached().callMethod(context, "singleton_method_added", newSym);
         } else {
             callMethod(context, "method_added", newSym);
         }
         return this;
     }
 
     @JRubyMethod(name = "undef_method", required = 1, rest = true, visibility = PRIVATE)
     public RubyModule undef_method(ThreadContext context, IRubyObject[] args) {
         for (int i=0; i<args.length; i++) {
             undef(context, args[i].asJavaString());
         }
         return this;
     }
 
     @JRubyMethod(name = {"module_eval", "class_eval"}, frame = true)
     public IRubyObject module_eval(ThreadContext context, Block block) {
         return specificEval(context, this, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"}, frame = true)
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, Block block) {
         return specificEval(context, this, arg0, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"}, frame = true)
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return specificEval(context, this, arg0, arg1, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"}, frame = true)
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return specificEval(context, this, arg0, arg1, arg2, block);
     }
     @Deprecated
     public IRubyObject module_eval(ThreadContext context, IRubyObject[] args, Block block) {
         return specificEval(context, this, args, block);
     }
 
     @JRubyMethod(name = "remove_method", required = 1, rest = true, visibility = PRIVATE)
     public RubyModule remove_method(ThreadContext context, IRubyObject[] args) {
         for(int i=0;i<args.length;i++) {
             removeMethod(context, args[i].asJavaString());
         }
         return this;
     }
 
     public static void marshalTo(RubyModule module, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(module);
         output.writeString(MarshalStream.getPathFromClass(module));
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = RubyString.byteListToString(input.unmarshalString());
         RubyModule result = UnmarshalStream.getModuleFromPath(input.getRuntime(), name);
         input.registerLinkTarget(result);
         return result;
     }
 
     /* Module class methods */
     
     /** 
      * Return an array of nested modules or classes.
      */
     @JRubyMethod(name = "nesting", frame = true, meta = true)
     public static RubyArray nesting(ThreadContext context, IRubyObject recv, Block block) {
         Ruby runtime = context.getRuntime();
         RubyModule object = runtime.getObject();
         StaticScope scope = context.getCurrentScope().getStaticScope();
         RubyArray result = runtime.newArray();
         
         for (StaticScope current = scope; current.getModule() != object; current = current.getPreviousCRefScope()) {
             result.append(current.getModule());
         }
         
         return result;
     }
 
     /**
      * Include the given module and all related modules into the hierarchy above
      * this module/class. Inspects the hierarchy to ensure the same module isn't
      * included twice, and selects an appropriate insertion point for each incoming
      * module.
      * 
      * @param baseModule The module to include, along with any modules it itself includes
      */
     private void doIncludeModule(RubyModule baseModule) {
         List<RubyModule> modulesToInclude = gatherModules(baseModule);
         
         RubyModule currentInclusionPoint = this;
         ModuleLoop: for (RubyModule nextModule : modulesToInclude) {
             checkForCyclicInclude(nextModule);
 
             boolean superclassSeen = false;
 
             // scan class hierarchy for module
             for (RubyClass nextClass = this.getSuperClass(); nextClass != null; nextClass = nextClass.getSuperClass()) {
                 if (doesTheClassWrapTheModule(nextClass, nextModule)) {
                     // next in hierarchy is an included version of the module we're attempting,
                     // so we skip including it
                     
                     // if we haven't encountered a real superclass, use the found module as the new inclusion point
                     if (!superclassSeen) currentInclusionPoint = nextClass;
                     
                     continue ModuleLoop;
                 } else {
                     superclassSeen = true;
                 }
             }
 
             currentInclusionPoint = proceedWithInclude(currentInclusionPoint, nextModule);
         }
     }
     
     /**
      * Is the given class a wrapper for the specified module?
      * 
      * @param theClass The class to inspect
      * @param theModule The module we're looking for
      * @return true if the class is a wrapper for the module, false otherwise
      */
     private boolean doesTheClassWrapTheModule(RubyClass theClass, RubyModule theModule) {
         return theClass.isIncluded() &&
                 theClass.getNonIncludedClass() == theModule.getNonIncludedClass();
     }
 
     /**
      * Gather all modules that would be included by including the given module.
      * The resulting list contains the given module and its (zero or more)
      * module-wrapping superclasses.
      * 
      * @param baseModule The base module from which to aggregate modules
      * @return A list of all modules that would be included by including the given module
      */
     private List<RubyModule> gatherModules(RubyModule baseModule) {
         // build a list of all modules to consider for inclusion
         List<RubyModule> modulesToInclude = new ArrayList<RubyModule>();
         while (baseModule != null) {
             modulesToInclude.add(baseModule);
             baseModule = baseModule.getSuperClass();
         }
 
         return modulesToInclude;
     }
 
     /**
      * Actually proceed with including the specified module above the given target
      * in a hierarchy. Return the new module wrapper.
      * 
      * @param insertAbove The hierarchy target above which to include the wrapped module
      * @param moduleToInclude The module to wrap and include
      * @return The new module wrapper resulting from this include
      */
     private RubyModule proceedWithInclude(RubyModule insertAbove, RubyModule moduleToInclude) {
         // In the current logic, if we get here we know that module is not an
         // IncludedModuleWrapper, so there's no need to fish out the delegate. But just
         // in case the logic should change later, let's do it anyway
         RubyClass wrapper = new IncludedModuleWrapper(getRuntime(), insertAbove.getSuperClass(), moduleToInclude.getNonIncludedClass());
         
         // if the insertion point is a class, update subclass lists
         if (insertAbove instanceof RubyClass) {
             RubyClass insertAboveClass = (RubyClass)insertAbove;
             
             // if there's a non-null superclass, we're including into a normal class hierarchy;
             // update subclass relationships to avoid stale parent/child relationships
             if (insertAboveClass.getSuperClass() != null) {
                 insertAboveClass.getSuperClass().removeSubclass(insertAboveClass);
                 insertAboveClass.getSuperClass().addSubclass(wrapper);
             }
             
             wrapper.addSubclass(insertAboveClass);
         }
         
         insertAbove.setSuperClass(wrapper);
         insertAbove = insertAbove.getSuperClass();
         return insertAbove;
     }
 
 
     //
     ////////////////// CLASS VARIABLE RUBY METHODS ////////////////
     //
 
     @JRubyMethod(name = "class_variable_defined?", required = 1)
     public IRubyObject class_variable_defined_p(ThreadContext context, IRubyObject var) {
         String internedName = validateClassVariable(var.asJavaString().intern());
         RubyModule module = this;
         do {
             if (module.fastHasClassVariable(internedName)) {
                 return context.getRuntime().getTrue();
             }
         } while ((module = module.getSuperClass()) != null);
 
         return context.getRuntime().getFalse();
     }
 
     /** rb_mod_cvar_get
      *
      */
     @JRubyMethod(name = "class_variable_get", required = 1, visibility = PRIVATE)
     public IRubyObject class_variable_get(IRubyObject var) {
         return fastGetClassVar(validateClassVariable(var.asJavaString()).intern());
     }
 
     /** rb_mod_cvar_set
      *
      */
     @JRubyMethod(name = "class_variable_set", required = 2, visibility = PRIVATE)
     public IRubyObject class_variable_set(IRubyObject var, IRubyObject value) {
         return fastSetClassVar(validateClassVariable(var.asJavaString()).intern(), value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     @JRubyMethod(name = "remove_class_variable", required = 1, visibility = PRIVATE)
     public IRubyObject remove_class_variable(ThreadContext context, IRubyObject name) {
         String javaName = validateClassVariable(name.asJavaString());
         IRubyObject value;
 
         if ((value = deleteClassVariable(javaName)) != null) {
             return value;
         }
 
         if (fastIsClassVarDefined(javaName)) {
             throw cannotRemoveError(javaName);
         }
 
         throw context.getRuntime().newNameError("class variable " + javaName + " not defined for " + getName(), javaName);
     }
 
     /** rb_mod_class_variables
      *
      */
     @JRubyMethod(name = "class_variables")
     public RubyArray class_variables(ThreadContext context) {
         Set<String> names = new HashSet<String>();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             names.addAll(p.getClassVariableNameList());
         }
 
         RubyArray ary = context.getRuntime().newArray();
 
         ary.addAll(names);
 
         return ary;
     }
 
 
     //
     ////////////////// CONSTANT RUBY METHODS ////////////////
     //
 
     /** rb_mod_const_defined
      *
      */
     @JRubyMethod(name = "const_defined?", required = 1)
     public RubyBoolean const_defined_p(ThreadContext context, IRubyObject symbol) {
         // Note: includes part of fix for JRUBY-1339
         return context.getRuntime().newBoolean(fastIsConstantDefined(validateConstant(symbol.asJavaString()).intern()));
     }
 
     /** rb_mod_const_get
      *
      */
     @JRubyMethod(name = "const_get", required = 1)
     public IRubyObject const_get(IRubyObject symbol) {
         return getConstant(validateConstant(symbol.asJavaString()));
     }
 
     /** rb_mod_const_set
      *
      */
     @JRubyMethod(name = "const_set", required = 2)
     public IRubyObject const_set(IRubyObject symbol, IRubyObject value) {
         return fastSetConstant(validateConstant(symbol.asJavaString()).intern(), value);
     }
 
     @JRubyMethod(name = "remove_const", required = 1, visibility = PRIVATE)
     public IRubyObject remove_const(ThreadContext context, IRubyObject rubyName) {
         String name = validateConstant(rubyName.asJavaString());
         IRubyObject value;
         if ((value = deleteConstant(name)) != null) {
             invalidateConstantCache();
             if (value != UNDEF) {
                 return value;
             }
             context.getRuntime().getLoadService().removeAutoLoadFor(getName() + "::" + name);
             // FIXME: I'm not sure this is right, but the old code returned
             // the undef, which definitely isn't right...
             return context.getRuntime().getNil();
         }
 
         if (hasConstantInHierarchy(name)) {
             throw cannotRemoveError(name);
         }
 
         throw context.getRuntime().newNameError("constant " + name + " not defined for " + getName(), name);
     }
 
     private boolean hasConstantInHierarchy(final String name) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.hasConstant(name)) {
                 return true;
         }
         }
         return false;
     }
     
     /**
      * Base implementation of Module#const_missing, throws NameError for specific missing constant.
      * 
      * @param name The constant name which was found to be missing
      * @return Nothing! Absolutely nothing! (though subclasses might choose to return something)
      */
     @JRubyMethod(name = "const_missing", required = 1, frame = true)
     public IRubyObject const_missing(ThreadContext context, IRubyObject rubyName, Block block) {
         Ruby runtime = context.getRuntime();
         String name;
         
         if (this != runtime.getObject()) {
             name = getName() + "::" + rubyName.asJavaString();
         } else {
             name = rubyName.asJavaString();
         }
 
         throw runtime.newNameError("uninitialized constant " + name, name);
     }
 
     /** rb_mod_constants
      *
      */
     @JRubyMethod(name = "constants")
     public RubyArray constants(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         RubyArray array = runtime.newArray();
         RubyModule objectClass = runtime.getObject();
 
         if (getRuntime().getModule() == this || objectClass == this) {
             array.addAll(objectClass.getConstantNames());
         } else {
             Set<String> names = new HashSet<String>();
             for (RubyModule module = this; module != null && module != objectClass; module = module.getSuperClass()) {
                 names.addAll(module.getConstantNames());
             }
             array.addAll(names);
         }
 
         return array;
     }
 
 
     //
     ////////////////// CLASS VARIABLE API METHODS ////////////////
     //
 
     /**
      * Set the named class variable to the given value, provided taint and freeze allow setting it.
      * 
      * Ruby C equivalent = "rb_cvar_set"
      * 
      * @param name The variable name to set
      * @param value The value to set it to
      */
     public IRubyObject setClassVar(String name, IRubyObject value) {
         RubyModule module = this;
         do {
             if (module.hasClassVariable(name)) {
                 return module.storeClassVariable(name, value);
             }
         } while ((module = module.getSuperClass()) != null);
         
         return storeClassVariable(name, value);
     }
 
     public IRubyObject fastSetClassVar(final String internedName, final IRubyObject value) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         RubyModule module = this;
         do {
             if (module.fastHasClassVariable(internedName)) {
                 return module.fastStoreClassVariable(internedName, value);
             }
         } while ((module = module.getSuperClass()) != null);
         
         return fastStoreClassVariable(internedName, value);
     }
 
     /**
      * Retrieve the specified class variable, searching through this module, included modules, and supermodules.
      * 
      * Ruby C equivalent = "rb_cvar_get"
      * 
      * @param name The name of the variable to retrieve
      * @return The variable's value, or throws NameError if not found
      */
     public IRubyObject getClassVar(String name) {
         assert IdUtil.isClassVariable(name);
         IRubyObject value;
         RubyModule module = this;
         
         do {
             if ((value = module.variableTableFetch(name)) != null) return value;
         } while ((module = module.getSuperClass()) != null);
 
         throw getRuntime().newNameError("uninitialized class variable " + name + " in " + getName(), name);
     }
 
     public IRubyObject fastGetClassVar(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         assert IdUtil.isClassVariable(internedName);
         IRubyObject value;
         RubyModule module = this;
         
         do {
             if ((value = (IRubyObject)module.variableTableFastFetch(internedName)) != null) return value; 
         } while ((module = module.getSuperClass()) != null);
 
         throw getRuntime().newNameError("uninitialized class variable " + internedName + " in " + getName(), internedName);
     }
 
     /**
      * Is class var defined?
      * 
      * Ruby C equivalent = "rb_cvar_defined"
      * 
      * @param name The class var to determine "is defined?"
      * @return true if true, false if false
      */
     public boolean isClassVarDefined(String name) {
         RubyModule module = this;
         do {
             if (module.hasClassVariable(name)) return true;
         } while ((module = module.getSuperClass()) != null);
 
         return false;
     }
 
     public boolean fastIsClassVarDefined(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         RubyModule module = this;
         do {
             if (module.fastHasClassVariable(internedName)) return true;
         } while ((module = module.getSuperClass()) != null);
 
         return false;
     }
 
     
     /** rb_mod_remove_cvar
      *
      * FIXME: any good reason to have two identical methods? (same as remove_class_variable)
      */
     public IRubyObject removeCvar(IRubyObject name) { // Wrong Parameter ?
         String internedName = validateClassVariable(name.asJavaString());
         IRubyObject value;
 
         if ((value = deleteClassVariable(internedName)) != null) {
             return value;
         }
 
         if (fastIsClassVarDefined(internedName)) {
             throw cannotRemoveError(internedName);
         }
 
         throw getRuntime().newNameError("class variable " + internedName + " not defined for " + getName(), internedName);
     }
 
 
     //
     ////////////////// CONSTANT API METHODS ////////////////
     //
 
     public IRubyObject getConstantAt(String name) {
         IRubyObject value = fetchConstant(name);
         
         return value == UNDEF ? resolveUndefConstant(getRuntime(), name) : value;
     }
 
     public IRubyObject fastGetConstantAt(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         IRubyObject value = fastFetchConstant(internedName);
 
         return value == UNDEF ? resolveUndefConstant(getRuntime(), internedName) : value;
     }
 
     /**
      * Retrieve the named constant, invoking 'const_missing' should that be appropriate.
      * 
      * @param name The constant to retrieve
      * @return The value for the constant, or null if not found
      */
     public IRubyObject getConstant(String name) {
         return fastGetConstant(name.intern());
     }
     
     public IRubyObject fastGetConstant(String internedName) {
         IRubyObject value = getConstantNoConstMissing(internedName);
         Ruby runtime = getRuntime();
         
         return value == null ? callMethod(runtime.getCurrentContext(), "const_missing",
                 runtime.fastNewSymbol(internedName)) : value;
     }
 
     public IRubyObject getConstantNoConstMissing(String name) {
         assert IdUtil.isConstant(name);
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             IRubyObject value = p.getConstantInner(name);
 
             if (value != null) return value == UNDEF ? null : value;
         }
 
         if (!isClass()) {
             for (RubyModule p = getRuntime().getObject(); p != null; p = p.getSuperClass()) {
                 IRubyObject value = p.getConstantInner(name);
 
                 if (value != null) return value == UNDEF ? null : value;
             }
         }
 
         return null;
     }
     
     protected IRubyObject getConstantInner(String name) {
         IRubyObject value = constantTableFetch(name);
 
         for (; value == UNDEF; value = constantTableFetch(name)) {
             if (resolveUndefConstant(getRuntime(), name) == null) return UNDEF;
         }
         
         return value;
     }
 
     // not actually called anywhere (all known uses call the fast version)
     public IRubyObject getConstantFrom(String name) {
         return fastGetConstantFrom(name.intern());
     }
     
     public IRubyObject fastGetConstantFrom(String internedName) {
         IRubyObject value = fastGetConstantFromNoConstMissing(internedName);
 
         return value != null ? value : fastGetConstantFromConstMissing(internedName);
     }
 
     public IRubyObject fastGetConstantFromNoConstMissing(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         assert IdUtil.isConstant(internedName);
         Ruby runtime = getRuntime();
         RubyClass objectClass = runtime.getObject();
         IRubyObject value;
 
         RubyModule p = this;
 
         while (p != null) {
             if ((value = p.constantTableFastFetch(internedName)) != null) {
                 if (value == UNDEF) {
                     if (p.resolveUndefConstant(runtime, internedName) == null) break;
                     continue; // Not that is loaded loop around to resolve it next pass
                 }
 
                 if (p == objectClass && this != objectClass) {
                     String badCName = getName() + "::" + internedName;
                     runtime.getWarnings().warn(ID.CONSTANT_BAD_REFERENCE, "toplevel constant " +
                             internedName + " referenced by " + badCName, badCName);
                 }
 
                 return value;
             }
             p = p.getSuperClass();
         }
         return null;
     }
 
     public IRubyObject fastGetConstantFromConstMissing(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         assert IdUtil.isConstant(internedName);
 
         return callMethod(getRuntime().getCurrentContext(),
                 "const_missing", getRuntime().fastNewSymbol(internedName));
     }
     
     public IRubyObject resolveUndefConstant(Ruby runtime, String name) {
         deleteConstant(name);
         
         return runtime.getLoadService().autoload(getName() + "::" + name);
     }
     
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name.
      * 
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      */
     public IRubyObject setConstant(String name, IRubyObject value) {
         IRubyObject oldValue = fetchConstant(name);
         if (oldValue != null) {
             Ruby runtime = getRuntime();
             if (oldValue == UNDEF) {
                 runtime.getLoadService().removeAutoLoadFor(getName() + "::" + name);
             } else {
                 runtime.getWarnings().warn(ID.CONSTANT_ALREADY_INITIALIZED, "already initialized constant " + name, name);
             }
         }
 
         storeConstant(name, value);
         invalidateConstantCache();
 
         // if adding a module under a constant name, set that module's basename to the constant name
         if (value instanceof RubyModule) {
             RubyModule module = (RubyModule)value;
             if (module.getBaseName() == null) {
                 module.setBaseName(name);
                 module.setParent(this);
             }
         }
         return value;
     }
 
     public IRubyObject fastSetConstant(String internedName, IRubyObject value) {
         return setConstant(internedName, value);
     }
     
     /** rb_define_const
      *
      */
     public void defineConstant(String name, IRubyObject value) {
         assert value != null;
 
         if (this == getRuntime().getClassClass()) {
             getRuntime().secure(4);
         }
 
         if (!IdUtil.isValidConstantName(name)) {
             throw getRuntime().newNameError("bad constant name " + name, name);
         }
 
         setConstant(name, value);
     }
 
     // Fix for JRUBY-1339 - search hierarchy for constant
     /** rb_const_defined_at
      * 
      */
     public boolean isConstantDefined(String name) {
         assert IdUtil.isConstant(name);
         boolean isObject = this == getRuntime().getObject();
 
         RubyModule module = this;
 
         do {
             Object value;
             if ((value = module.constantTableFetch(name)) != null) {
                 if (value != UNDEF) return true;
                 return getRuntime().getLoadService().autoloadFor(
                         module.getName() + "::" + name) != null;
             }
 
         } while (isObject && (module = module.getSuperClass()) != null );
 
         return false;
     }
 
     public boolean fastIsConstantDefined(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         assert IdUtil.isConstant(internedName);
         boolean isObject = this == getRuntime().getObject();
 
         RubyModule module = this;
 
         do {
             Object value;
             if ((value = module.constantTableFastFetch(internedName)) != null) {
                 if (value != UNDEF) return true;
                 return getRuntime().getLoadService().autoloadFor(
                         module.getName() + "::" + internedName) != null;
             }
 