diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 00b4a9c2d6..0fe3ce000c 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -679,2001 +679,2002 @@ public class RubyModule extends RubyObject {
     }
 
     public void removeMethod(ThreadContext context, String name) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't remove method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             DynamicMethod method = (DynamicMethod) getMethods().remove(name);
             if (method == null) {
                 throw getRuntime().newNameError("method '" + name + "' not defined in " + getName(), name);
             }
             
             getRuntime().getCacheMap().remove(method);
         }
         
         if(isSingleton()){
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_removed", getRuntime().newSymbol(name));
         } else {
             callMethod(context, "method_removed", getRuntime().newSymbol(name));
     }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod searchMethod(String name) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // See if current class has method or if it has been cached here already
             DynamicMethod method = (DynamicMethod) searchModule.getMethods().get(name);
 
             if (method != null) {
                 return method;
             }
         }
 
         return UndefinedMethod.getInstance();
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod retrieveMethod(String name) {
         return (DynamicMethod)getMethods().get(name);
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public RubyModule findImplementer(RubyModule clazz) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             if (searchModule.isSame(clazz)) {
                 return searchModule;
             }
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
         DynamicMethod oldMethod = searchMethod(name);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = runtime.getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw runtime.newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         CacheMap cacheMap = runtime.getCacheMap();
         cacheMap.remove(method);
         cacheMap.remove(oldMethod);
         if (oldMethod != oldMethod.getRealMethod()) {
             cacheMap.remove(oldMethod.getRealMethod());
         }
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
         CacheMap cacheMap = runtime.getCacheMap();
         cacheMap.remove(method);
         for (String name: aliases) {
             if (oldName.equals(name)) continue;
             DynamicMethod oldMethod = searchMethod(name);
             cacheMap.remove(oldMethod);
             if (oldMethod != oldMethod.getRealMethod()) {
                 cacheMap.remove(oldMethod.getRealMethod());
             }
             putMethod(name, new AliasMethod(this, method, oldName));
         }
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
 
         final Ruby runtime = getRuntime();
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = context.getCurrentVisibility();
         if (attributeScope == Visibility.PRIVATE) {
             //FIXME warning
         } else if (attributeScope == Visibility.MODULE_FUNCTION) {
             attributeScope = Visibility.PRIVATE;
             // FIXME warning
         }
         final String variableName = ("@" + internedName).intern();
         if (readable) {
             // FIXME: should visibility be set to current visibility?
             addMethod(internedName, new JavaMethod(this, Visibility.PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) Arity.raiseArgumentError(runtime, args.length, 0, 0);
 
                     IRubyObject variable = self.getInstanceVariables().fastGetInstanceVariable(variableName);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
         if (writeable) {
             internedName = (internedName + "=").intern();
             // FIXME: should visibility be set to current visibility?
             addMethod(internedName, new JavaMethod(this, Visibility.PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     // ENEBO: Can anyone get args to be anything but length 1?
                     if (args.length != 1) Arity.raiseArgumentError(runtime, args.length, 1, 1);
 
                     return self.getInstanceVariables().fastSetInstanceVariable(variableName, args[0]);
                 }
 
                 public Arity getArity() {
                     return Arity.singleArgument();
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
         }
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility() == Visibility.PRIVATE);
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
 
     // What is argument 1 for in this method? A Method or Proc object /OB
     @JRubyMethod(name = "define_method", required = 1, optional = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject define_method(ThreadContext context, IRubyObject[] args, Block block) {
         if (args.length < 1 || args.length > 2) {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         IRubyObject body;
         String name = args[0].asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = context.getCurrentVisibility();
 
         if (visibility == Visibility.MODULE_FUNCTION) visibility = Visibility.PRIVATE;
         if (args.length == 1) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = getRuntime().newProc(Block.Type.LAMBDA, block);
             body = proc;
             
             // a normal block passed to define_method changes to do arity checking; make it a lambda
             proc.getBlock().type = Block.Type.LAMBDA;
 
             newMethod = createProcMethod(name, visibility, proc);
         } else if (args.length == 2) {
             if (getRuntime().getProc().isInstance(args[1])) {
                 // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
                 RubyProc proc = (RubyProc)args[1];
                 body = proc;
 
                 newMethod = createProcMethod(name, visibility, proc);
             } else if (getRuntime().getMethod().isInstance(args[1])) {
                 RubyMethod method = (RubyMethod)args[1];
                 body = method;
 
                 newMethod = new MethodMethod(this, method.unbind(null), visibility);
             } else {
                 throw getRuntime().newTypeError("wrong argument type " + args[1].getType().getName() + " (expected Proc/Method)");
             }
         } else {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         addMethod(name, newMethod);
 
         RubySymbol symbol = getRuntime().fastNewSymbol(name);
 
         if (context.getPreviousVisibility() == Visibility.MODULE_FUNCTION) {
             getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), newMethod, Visibility.PUBLIC));
         }
 
         if(isSingleton()){
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_added", symbol);
         }else{
             callMethod(context, "method_added", symbol);
         }
 
         return body;
     }
     
     private DynamicMethod createProcMethod(String name, Visibility visibility, RubyProc proc) {
         proc.getBlock().getBinding().getFrame().setKlazz(this);
         proc.getBlock().getBinding().getFrame().setName(name);
 
         // for zsupers in define_method (blech!) we tell the proc scope to act as the "argument" scope
         proc.getBlock().getBody().getStaticScope().setArgumentScope(true);
         // just using required is broken...but no more broken than before zsuper refactoring
         proc.getBlock().getBody().getStaticScope().setRequiredArgs(proc.getBlock().arity().required());
 
         return new ProcMethod(this, proc, visibility);
     }
 
     public IRubyObject executeUnder(ThreadContext context, Callback method, IRubyObject[] args, Block block) {
         context.preExecuteUnder(this, block);
         try {
             return method.execute(this, args, block);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     @JRubyMethod(name = "name")
     public RubyString name() {
         return getRuntime().newString(getBaseName() == null ? "" : getName());
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Map.Entry<String, DynamicMethod> entry : getMethods().entrySet()) {
             DynamicMethod method = entry.getValue();
             // Do not clone cached methods
             // FIXME: MRI copies all methods here
             if (method.getImplementationClass() == realType || method instanceof UndefinedMethod) {
                 
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
     public IRubyObject initialize_copy(IRubyObject original) {
         super.initialize_copy(original);
 
         RubyModule originalModule = (RubyModule)original;
 
         if (!getMetaClass().isSingleton()) setMetaClass(originalModule.getSingletonClassClone());
         setSuperClass(originalModule.getSuperClass());
 
         if (originalModule.hasVariables()){
             syncVariables(originalModule.getVariableList());
         }
 
         originalModule.cloneMethods(this);
 
         return this;
     }
 
     /** rb_mod_included_modules
      *
      */
     @JRubyMethod(name = "included_modules")
     public RubyArray included_modules() {
         RubyArray ary = getRuntime().newArray();
 
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
     public RubyArray ancestors() {
         RubyArray ary = getRuntime().newArray(getAncestorList());
 
         return ary;
     }
 
     public List<IRubyObject> getAncestorList() {
         ArrayList<IRubyObject> list = new ArrayList<IRubyObject>();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if(!p.isSingleton()) {
                 list.add(p.getNonIncludedClass());
             }
         }
 
         return list;
     }
 
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getNonIncludedClass() == type) return true;
         }
 
         return false;
     }
 
     public int hashCode() {
         return id;
     }
 
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         return getRuntime().newFixnum(id);
     }
 
     /** rb_mod_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
         if(isSingleton()){            
             IRubyObject attached = ((MetaClass)this).getAttached();
             StringBuffer buffer = new StringBuffer("#<Class:");
             if(attached instanceof RubyClass || attached instanceof RubyModule){
                 buffer.append(attached.inspect());
             }else{
                 buffer.append(attached.anyToString());
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
     public RubyBoolean op_eqq(IRubyObject obj) {
         return getRuntime().newBoolean(isInstance(obj));
     }
 
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         return super.op_equal(context, other);
     }
 
     /** rb_mod_freeze
      *
      */
     @JRubyMethod(name = "freeze")
     public IRubyObject freeze() {
         to_s();
         return super.freeze();
     }
 
     /** rb_mod_le
     *
     */
     @JRubyMethod(name = "<=", required = 1)
    public IRubyObject op_le(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         if (isKindOfModule((RubyModule) obj)) {
             return getRuntime().getTrue();
         } else if (((RubyModule) obj).isKindOfModule(this)) {
             return getRuntime().getFalse();
         }
 
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
 
         if (module.isKindOfModule(this)) {
             return getRuntime().newFixnum(1);
         } else if (this.isKindOfModule(module)) {
             return getRuntime().newFixnum(-1);
         }
 
         return getRuntime().getNil();
     }
 
     public boolean isKindOfModule(RubyModule type) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.isSame(type)) {
                 return true;
             }
         }
 
         return false;
     }
 
     protected boolean isSame(RubyModule module) {
         return this == module;
     }
 
     /** rb_mod_initialize
      *
      */
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(Block block) {
         if (block.isGiven()) {
             // class and module bodies default to public, so make the block's visibility public. JRUBY-1185.
             block.getBinding().setVisibility(Visibility.PUBLIC);
             block.yield(getRuntime().getCurrentContext(), null, this, this, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr
      *
      */
     @JRubyMethod(name = "attr", required = 1, optional = 1, visibility = Visibility.PRIVATE)
     public IRubyObject attr(ThreadContext context, IRubyObject[] args) {
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         addAccessor(context, args[0].asJavaString().intern(), true, writeable);
 
         return getRuntime().getNil();
     }
 
     /**
      * @deprecated
      */
     public IRubyObject attr_reader(IRubyObject[] args) {
         return attr_reader(getRuntime().getCurrentContext(), args);
     }
     
     /** rb_mod_attr_reader
      *
      */
     @JRubyMethod(name = "attr_reader", rest = true, visibility = Visibility.PRIVATE)
     public IRubyObject attr_reader(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), true, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     @JRubyMethod(name = "attr_writer", rest = true, visibility = Visibility.PRIVATE)
     public IRubyObject attr_writer(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), false, true);
         }
 
         return getRuntime().getNil();
     }
 
     /**
      * @deprecated
      */
     public IRubyObject attr_accessor(IRubyObject[] args) {
         return attr_accessor(getRuntime().getCurrentContext(), args);
     }
 
     /** rb_mod_attr_accessor
      *
      */
     @JRubyMethod(name = "attr_accessor", rest = true, visibility = Visibility.PRIVATE)
     public IRubyObject attr_accessor(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             // This is almost always already interned, since it will be called with a symbol in most cases
             // but when created from Java code, we might get an argument that needs to be interned.
             // addAccessor has as a precondition that the string MUST be interned
             addAccessor(context, args[i].asJavaString().intern(), true, true);
         }
 
         return getRuntime().getNil();
     }
 
     /**
      * Get a list of all instance methods names of the provided visibility unless not is true, then 
      * get all methods which are not the provided visibility.
      * 
      * @param args passed into one of the Ruby instance_method methods
      * @param visibility to find matching instance methods against
      * @param not if true only find methods not matching supplied visibility
      * @return a RubyArray of instance method names
      */
     private RubyArray instance_methods(IRubyObject[] args, final Visibility visibility, boolean not) {
         boolean includeSuper = args.length > 0 ? args[0].isTrue() : true;
         RubyArray ary = getRuntime().newArray();
         Set<String> seen = new HashSet<String>();
 
         for (RubyModule type = this; type != null; type = type.getSuperClass()) {
             RubyModule realType = type.getNonIncludedClass();
             for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext();) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
                 String methodName = (String) entry.getKey();
 
                 if (! seen.contains(methodName)) {
                     seen.add(methodName);
                     
                     if (method.getImplementationClass() == realType &&
                         (!not && method.getVisibility() == visibility || (not && method.getVisibility() != visibility)) &&
                         ! method.isUndefined()) {
 
                         ary.append(getRuntime().newString(methodName));
                     }
                 }
             }
 
             if (!includeSuper) {
                 break;
             }
         }
 
         return ary;
     }
 
     @JRubyMethod(name = "instance_methods", optional = 1)
     public RubyArray instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PRIVATE, true);
     }
 
     @JRubyMethod(name = "public_instance_methods", optional = 1)
     public RubyArray public_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PUBLIC, false);
     }
 
     @JRubyMethod(name = "instance_method", required = 1)
     public IRubyObject instance_method(IRubyObject symbol) {
         return newMethod(null, symbol.asJavaString(), false);
     }
 
     /** rb_class_protected_instance_methods
      *
      */
     @JRubyMethod(name = "protected_instance_methods", optional = 1)
     public RubyArray protected_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PROTECTED, false);
     }
 
     /** rb_class_private_instance_methods
      *
      */
     @JRubyMethod(name = "private_instance_methods", optional = 1)
     public RubyArray private_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PRIVATE, false);
     }
 
     /** rb_mod_append_features
      *
      */
     @JRubyMethod(name = "append_features", required = 1, visibility = Visibility.PRIVATE)
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
     @JRubyMethod(name = "extend_object", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject extend_object(IRubyObject obj) {
         obj.getSingletonClass().includeModule(this);
         return obj;
     }
 
     /** rb_mod_include
      *
      */
     @JRubyMethod(name = "include", required = 1, rest = true, visibility = Visibility.PRIVATE)
     public RubyModule include(IRubyObject[] modules) {
         ThreadContext context = getRuntime().getCurrentContext();
         // MRI checks all types first:
         for (int i = modules.length; --i >= 0; ) {
             IRubyObject obj = modules[i];
             if (!obj.isModule()) throw getRuntime().newTypeError(obj,getRuntime().getModule());
         }
         for (int i = modules.length - 1; i >= 0; i--) {
             modules[i].callMethod(context, "append_features", this);
             modules[i].callMethod(context, "included", this);
         }
 
         return this;
     }
 
     @JRubyMethod(name = "included", required = 1)
     public IRubyObject included(IRubyObject other) {
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "extended", required = 1, frame = true)
     public IRubyObject extended(IRubyObject other, Block block) {
         return getRuntime().getNil();
     }
 
     private void setVisibility(ThreadContext context, IRubyObject[] args, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
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
     @JRubyMethod(name = "public", rest = true, visibility = Visibility.PRIVATE)
     public RubyModule rbPublic(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, Visibility.PUBLIC);
         return this;
     }
 
     /** rb_mod_protected
      *
      */
     @JRubyMethod(name = "protected", rest = true, visibility = Visibility.PRIVATE)
     public RubyModule rbProtected(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, Visibility.PROTECTED);
         return this;
     }
 
     /** rb_mod_private
      *
      */
     @JRubyMethod(name = "private", rest = true, visibility = Visibility.PRIVATE)
     public RubyModule rbPrivate(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, Visibility.PRIVATE);
         return this;
     }
 
     /** rb_mod_modfunc
      *
      */
     @JRubyMethod(name = "module_function", rest = true, visibility = Visibility.PRIVATE)
     public RubyModule module_function(IRubyObject[] args) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
             context.setCurrentVisibility(Visibility.MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, Visibility.PRIVATE);
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asJavaString().intern();
                 DynamicMethod method = searchMethod(name);
                 assert !method.isUndefined() : "undefined method '" + name + "'";
                 getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), method, Visibility.PUBLIC));
                 callMethod(context, "singleton_method_added", getRuntime().fastNewSymbol(name));
             }
         }
         return this;
     }
 
     @JRubyMethod(name = "method_added", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject method_added(IRubyObject nothing) {
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "method_removed", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject method_removed(IRubyObject nothing) {
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "method_undefined", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject method_undefined(IRubyObject nothing) {
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "method_defined?", required = 1)
     public RubyBoolean method_defined_p(IRubyObject symbol) {
         return isMethodBound(symbol.asJavaString(), true) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "public_method_defined?", required = 1)
     public IRubyObject public_method_defined(IRubyObject symbol) {
 	    DynamicMethod method = searchMethod(symbol.asJavaString());
 	    
 		return getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == Visibility.PUBLIC);
     }
 
     @JRubyMethod(name = "protected_method_defined?", required = 1)
     public IRubyObject protected_method_defined(IRubyObject symbol) {
 	    DynamicMethod method = searchMethod(symbol.asJavaString());
 	    
 		return getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == Visibility.PROTECTED);
     }
 	
     @JRubyMethod(name = "private_method_defined?", required = 1)
     public IRubyObject private_method_defined(IRubyObject symbol) {
 	    DynamicMethod method = searchMethod(symbol.asJavaString());
 	    
 		return getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == Visibility.PRIVATE);
     }
 
     @JRubyMethod(name = "public_class_method", rest = true)
     public RubyModule public_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, Visibility.PUBLIC);
         return this;
     }
 
     @JRubyMethod(name = "private_class_method", rest = true)
     public RubyModule private_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, Visibility.PRIVATE);
         return this;
     }
 
     @JRubyMethod(name = "alias_method", required = 2, visibility = Visibility.PRIVATE)
     public RubyModule alias_method(ThreadContext context, IRubyObject newId, IRubyObject oldId) {
         String newName = newId.asJavaString();
         defineAlias(newName, oldId.asJavaString());
-        RubySymbol newSym = context.getRuntime().newSymbol(newName);
+        RubySymbol newSym = newId instanceof RubySymbol ? (RubySymbol)newId :
+            context.getRuntime().newSymbol(newName);
         if (isSingleton()) {
             ((MetaClass)this).getAttached().callMethod(context, "singleton_method_added", newSym);
         } else {
             callMethod(context, "method_added", newSym);
         }
         return this;
     }
 
     @JRubyMethod(name = "undef_method", required = 1, rest = true, visibility = Visibility.PRIVATE)
     public RubyModule undef_method(ThreadContext context, IRubyObject[] args) {
         for (int i=0; i<args.length; i++) {
             undef(context, args[i].asJavaString());
         }
         return this;
     }
 
     @JRubyMethod(name = {"module_eval", "class_eval"}, optional = 3, frame = true)
     public IRubyObject module_eval(ThreadContext context, IRubyObject[] args, Block block) {
         return specificEval(context, this, args, block);
     }
 
     @JRubyMethod(name = "remove_method", required = 1, rest = true, visibility = Visibility.PRIVATE)
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
         Ruby runtime = recv.getRuntime();
         RubyModule object = runtime.getObject();
         StaticScope scope = context.getCurrentScope().getStaticScope();
         RubyArray result = runtime.newArray();
         
         for (StaticScope current = scope; current.getModule() != object; current = current.getPreviousCRefScope()) {
             result.append(current.getModule());
         }
         
         return result;
     }
 
     private void doIncludeModule(RubyModule includedModule) {
         boolean skip = false;
 
         RubyModule currentModule = this;
         while (includedModule != null) {
 
             if (getNonIncludedClass() == includedModule.getNonIncludedClass()) {
                 throw getRuntime().newArgumentError("cyclic include detected");
             }
 
             boolean superclassSeen = false;
 
             // scan class hierarchy for module
             for (RubyModule superClass = this.getSuperClass(); superClass != null; superClass = superClass.getSuperClass()) {
                 if (superClass instanceof IncludedModuleWrapper) {
                     if (superClass.getNonIncludedClass() == includedModule.getNonIncludedClass()) {
                         if (!superclassSeen) {
                             currentModule = superClass;
                         }
                         skip = true;
                         break;
                     }
                 } else {
                     superclassSeen = true;
                 }
             }
 
             if (!skip) {
 
                 // blow away caches for any methods that are redefined by module
                 getRuntime().getCacheMap().moduleIncluded(currentModule, includedModule);
                 
                 // In the current logic, if we get here we know that module is not an
                 // IncludedModuleWrapper, so there's no need to fish out the delegate. But just
                 // in case the logic should change later, let's do it anyway:
                 currentModule.setSuperClass(new IncludedModuleWrapper(getRuntime(), currentModule.getSuperClass(),
                         includedModule.getNonIncludedClass()));
                 currentModule = currentModule.getSuperClass();
             }
 
             includedModule = includedModule.getSuperClass();
             skip = false;
         }
     }
 
 
     //
     ////////////////// CLASS VARIABLE RUBY METHODS ////////////////
     //
 
     @JRubyMethod(name = "class_variable_defined?", required = 1)
     public IRubyObject class_variable_defined_p(IRubyObject var) {
         String internedName = validateClassVariable(var.asJavaString().intern());
         RubyModule module = this;
         do {
             if (module.fastHasClassVariable(internedName)) {
                 return getRuntime().getTrue();
             }
         } while ((module = module.getSuperClass()) != null);
 
         return getRuntime().getFalse();
     }
 
     /** rb_mod_cvar_get
      *
      */
     @JRubyMethod(name = "class_variable_get", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject class_variable_get(IRubyObject var) {
         return fastGetClassVar(validateClassVariable(var.asJavaString()).intern());
     }
 
     /** rb_mod_cvar_set
      *
      */
     @JRubyMethod(name = "class_variable_set", required = 2, visibility = Visibility.PRIVATE)
     public IRubyObject class_variable_set(IRubyObject var, IRubyObject value) {
         return fastSetClassVar(validateClassVariable(var.asJavaString()).intern(), value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     @JRubyMethod(name = "remove_class_variable", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject remove_class_variable(IRubyObject name) {
         String javaName = validateClassVariable(name.asJavaString());
         IRubyObject value;
 
         if ((value = deleteClassVariable(javaName)) != null) {
             return value;
         }
 
         if (fastIsClassVarDefined(javaName)) {
             throw cannotRemoveError(javaName);
         }
 
         throw getRuntime().newNameError("class variable " + javaName + " not defined for " + getName(), javaName);
     }
 
     /** rb_mod_class_variables
      *
      */
     @JRubyMethod(name = "class_variables")
     public RubyArray class_variables() {
         Set<String> names = new HashSet<String>();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             for (String name : p.getClassVariableNameList()) {
                 names.add(name);
             }
         }
 
         Ruby runtime = getRuntime();
         RubyArray ary = runtime.newArray();
 
         for (String name : names) {
             ary.add(runtime.newString(name));
         }
 
         return ary;
     }
 
 
     //
     ////////////////// CONSTANT RUBY METHODS ////////////////
     //
 
     /** rb_mod_const_defined
      *
      */
     @JRubyMethod(name = "const_defined?", required = 1)
     public RubyBoolean const_defined_p(IRubyObject symbol) {
         // Note: includes part of fix for JRUBY-1339
         return getRuntime().newBoolean(fastIsConstantDefined(validateConstant(symbol.asJavaString()).intern()));
     }
 
     /** rb_mod_const_get
      *
      */
     @JRubyMethod(name = "const_get", required = 1)
     public IRubyObject const_get(IRubyObject symbol) {
         return fastGetConstant(validateConstant(symbol.asJavaString()).intern());
     }
 
     /** rb_mod_const_set
      *
      */
     @JRubyMethod(name = "const_set", required = 2)
     public IRubyObject const_set(IRubyObject symbol, IRubyObject value) {
         return fastSetConstant(validateConstant(symbol.asJavaString()).intern(), value);
     }
 
     @JRubyMethod(name = "remove_const", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject remove_const(IRubyObject name) {
         String id = validateConstant(name.asJavaString());
         IRubyObject value;
         if ((value = deleteConstant(id)) != null) {
             if (value != getRuntime().getUndef()) {
                 return value;
             }
             getRuntime().getLoadService().removeAutoLoadFor(getName() + "::" + id);
             // FIXME: I'm not sure this is right, but the old code returned
             // the undef, which definitely isn't right...
             return getRuntime().getNil();
         }
 
         if (hasConstantInHierarchy(id)) {
             throw cannotRemoveError(id);
         }
 
         throw getRuntime().newNameError("constant " + id + " not defined for " + getName(), id);
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
     public IRubyObject const_missing(IRubyObject name, Block block) {
         /* Uninitialized constant */
         if (this != getRuntime().getObject()) {
             throw getRuntime().newNameError("uninitialized constant " + getName() + "::" + name.asJavaString(), "" + getName() + "::" + name.asJavaString());
         }
 
         throw getRuntime().newNameError("uninitialized constant " + name.asJavaString(), name.asJavaString());
     }
 
     /** rb_mod_constants
      *
      */
     @JRubyMethod(name = "constants")
     public RubyArray constants() {
         Ruby runtime = getRuntime();
         RubyArray array = runtime.newArray();
         RubyModule objectClass = runtime.getObject();
 
         if (getRuntime().getModule() == this) {
 
             for (String name : objectClass.getStoredConstantNameList()) {
                 array.add(runtime.newString(name));
             }
 
         } else if (objectClass == this) {
 
             for (String name : getStoredConstantNameList()) {
                 array.add(runtime.newString(name));
             }
 
         } else {
             Set<String> names = new HashSet<String>();
             for (RubyModule p = this; p != null; p = p.getSuperClass()) {
                 if (objectClass != p) {
                     for (String name : p.getStoredConstantNameList()) {
                         names.add(name);
                     }
                 }
             }
             for (String name : names) {
                 array.add(runtime.newString(name));
             }
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
             if ((value = module.variableTableFastFetch(internedName)) != null) return value; 
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
         IRubyObject value;
         if ((value = fetchConstant(name)) != getRuntime().getUndef()) {
             return value;
         }
         deleteConstant(name);
         return getRuntime().getLoadService().autoload(getName() + "::" + name);
     }
 
     public IRubyObject fastGetConstantAt(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         IRubyObject value;
         if ((value = fastFetchConstant(internedName)) != getRuntime().getUndef()) {
             return value;
         }
         deleteConstant(internedName);
         return getRuntime().getLoadService().autoload(getName() + "::" + internedName);
     }
 
     /**
      * Retrieve the named constant, invoking 'const_missing' should that be appropriate.
      * 
      * @param name The constant to retrieve
      * @return The value for the constant, or null if not found
      */
     public IRubyObject getConstant(String name) {
         assert IdUtil.isConstant(name);
         IRubyObject undef = getRuntime().getUndef();
         boolean retryForModule = false;
         IRubyObject value;
         RubyModule p = this;
 
         retry: while (true) {
             while (p != null) {
                 if ((value = p.constantTableFetch(name)) != null) {
                     if (value != undef) {
                         return value;
                     }
                     p.deleteConstant(name);
                     if (getRuntime().getLoadService().autoload(
                             p.getName() + "::" + name) == null) {
                         break;
                     }
                     continue;
                 }
                 p = p.getSuperClass();
             };
 
             if (!retryForModule && !isClass()) {
                 retryForModule = true;
                 p = getRuntime().getObject();
                 continue retry;
             }
 
             break;
         }
 
         return callMethod(getRuntime().getCurrentContext(),
                 "const_missing", getRuntime().newSymbol(name));
     }
     
     public IRubyObject fastGetConstant(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         assert IdUtil.isConstant(internedName);
         IRubyObject undef = getRuntime().getUndef();
         boolean retryForModule = false;
         IRubyObject value;
         RubyModule p = this;
 
         retry: while (true) {
             while (p != null) {
                 if ((value = p.constantTableFastFetch(internedName)) != null) {
                     if (value != undef) {
                         return value;
                     }
                     p.deleteConstant(internedName);
                     if (getRuntime().getLoadService().autoload(
                             p.getName() + "::" + internedName) == null) {
                         break;
                     }
                     continue;
                 }
                 p = p.getSuperClass();
             };
 
             if (!retryForModule && !isClass()) {
                 retryForModule = true;
                 p = getRuntime().getObject();
                 continue retry;
             }
 
             break;
         }
 
         return callMethod(getRuntime().getCurrentContext(),
                 "const_missing", getRuntime().fastNewSymbol(internedName));
     }
 
     // not actually called anywhere (all known uses call the fast version)
     public IRubyObject getConstantFrom(String name) {
         return fastGetConstantFrom(name.intern());
     }
     
     public IRubyObject fastGetConstantFrom(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         assert IdUtil.isConstant(internedName);
         RubyClass objectClass = getRuntime().getObject();
         IRubyObject undef = getRuntime().getUndef();
         IRubyObject value;
 
         RubyModule p = this;
         
         while (p != null) {
             if ((value = p.constantTableFastFetch(internedName)) != null) {
                 if (value != undef) {
                     if (p == objectClass && this != objectClass) {
                         String badCName = getName() + "::" + internedName;
                         getRuntime().getWarnings().warn(ID.CONSTANT_BAD_REFERENCE, "toplevel constant " + 
                                 internedName + " referenced by " + badCName, badCName);
                     }
                     return value;
                 }
                 p.deleteConstant(internedName);
                 if (getRuntime().getLoadService().autoload(
                         p.getName() + "::" + internedName) == null) {
                     break;
                 }
                 continue;
             }
             p = p.getSuperClass();
         };
 
         return callMethod(getRuntime().getCurrentContext(),
                 "const_missing", getRuntime().fastNewSymbol(internedName));
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
         IRubyObject oldValue;
         if ((oldValue = fetchConstant(name)) != null) {
             if (oldValue == getRuntime().getUndef()) {
                 getRuntime().getLoadService().removeAutoLoadFor(getName() + "::" + name);
             } else {
                 getRuntime().getWarnings().warn(ID.CONSTANT_ALREADY_INITIALIZED, "already initialized constant " + name, name);
             }
         }
 
         storeConstant(name, value);
 
         // if adding a module under a constant name, set that module's basename to the constant name
         if (value instanceof RubyModule) {
             RubyModule module = (RubyModule)value;
             if (module.getBaseName() == null) {
                 module.setBaseName(name);
                 module.setParent(this);
             }
             /*
             module.setParent(this);
             */
         }
         return value;
     }
 
     public IRubyObject fastSetConstant(String internedName, IRubyObject value) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         IRubyObject oldValue;
         if ((oldValue = fastFetchConstant(internedName)) != null) {
             if (oldValue == getRuntime().getUndef()) {
                 getRuntime().getLoadService().removeAutoLoadFor(getName() + "::" + internedName);
             } else {
                 getRuntime().getWarnings().warn(ID.CONSTANT_ALREADY_INITIALIZED, "already initialized constant " + internedName, internedName);
             }
         }
 
         fastStoreConstant(internedName, value);
 
         // if adding a module under a constant name, set that module's basename to the constant name
         if (value instanceof RubyModule) {
             RubyModule module = (RubyModule)value;
             if (module.getBaseName() == null) {
                 module.setBaseName(internedName);
                 module.setParent(this);
             }
             /*
             module.setParent(this);
             */
         }
         return value;
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
         Object undef = getRuntime().getUndef();
 
         RubyModule module = this;
 
         do {
             Object value;
             if ((value = module.constantTableFetch(name)) != null) {
                 if (value != undef) return true;
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
         Object undef = getRuntime().getUndef();
 
         RubyModule module = this;
 
         do {
             Object value;
             if ((value = module.constantTableFastFetch(internedName)) != null) {
                 if (value != undef) return true;
                 return getRuntime().getLoadService().autoloadFor(
                         module.getName() + "::" + internedName) != null;
             }
 
         } while (isObject && (module = module.getSuperClass()) != null );
 
         return false;
     }
 
     //
     ////////////////// COMMON CONSTANT / CVAR METHODS ////////////////
     //
 
     private RaiseException cannotRemoveError(String id) {
         return getRuntime().newNameError("cannot remove " + id + " for " + getName(), id);
     }
 
 
     //
     ////////////////// INTERNAL MODULE VARIABLE API METHODS ////////////////
     //
     
     /**
      * Behaves similarly to {@link #getClassVar(String)}. Searches this
      * class/module <em>and its ancestors</em> for the specified internal
      * variable.
      * 
      * @param name the internal variable name
      * @return the value of the specified internal variable if found, else null
      * @see #setInternalModuleVariable(String, IRubyObject)
      */
     public boolean hasInternalModuleVariable(final String name) {
         RubyModule module = this;
         do {
             if (module.hasInternalVariable(name)) {
                 return true;
             }
         } while ((module = module.getSuperClass()) != null);
 
         return false;
     }
     /**
      * Behaves similarly to {@link #getClassVar(String)}. Searches this
      * class/module <em>and its ancestors</em> for the specified internal
      * variable.
      * 
      * @param name the internal variable name
      * @return the value of the specified internal variable if found, else null
      * @see #setInternalModuleVariable(String, IRubyObject)
      */
     public IRubyObject searchInternalModuleVariable(final String name) {
         RubyModule module = this;
         IRubyObject value;
         do {
             if ((value = module.getInternalVariable(name)) != null) {
                 return value;
             }
         } while ((module = module.getSuperClass()) != null);
 
         return null;
     }
 
     /**
      * Behaves similarly to {@link #setClassVar(String, IRubyObject)}. If the
      * specified internal variable is found in this class/module <em>or an ancestor</em>,
      * it is set where found.  Otherwise it is set in this module. 
      * 
      * @param name the internal variable name
      * @param value the internal variable value
      * @see #searchInternalModuleVariable(String)
      */
     public void setInternalModuleVariable(final String name, final IRubyObject value) {
         RubyModule module = this;
         do {
             if (module.hasInternalVariable(name)) {
                 module.setInternalVariable(name, value);
                 return;
             }
         } while ((module = module.getSuperClass()) != null);
 
         setInternalVariable(name, value);
     }
 
     //
     ////////////////// LOW-LEVEL CLASS VARIABLE INTERFACE ////////////////
     //
     // fetch/store/list class variables for this module
     //
     
     public boolean hasClassVariable(String name) {
         assert IdUtil.isClassVariable(name);
         return variableTableContains(name);
     }
 
     public boolean fastHasClassVariable(String internedName) {
         assert IdUtil.isClassVariable(internedName);
         return variableTableFastContains(internedName);
     }
 
     public IRubyObject fetchClassVariable(String name) {
         assert IdUtil.isClassVariable(name);
         return variableTableFetch(name);
     }
 
     public IRubyObject fastFetchClassVariable(String internedName) {
         assert IdUtil.isClassVariable(internedName);
         return variableTableFastFetch(internedName);
     }
 
     public IRubyObject storeClassVariable(String name, IRubyObject value) {
         assert IdUtil.isClassVariable(name) && value != null;
         ensureClassVariablesSettable();
         return variableTableStore(name, value);
     }
 
     public IRubyObject fastStoreClassVariable(String internedName, IRubyObject value) {
         assert IdUtil.isClassVariable(internedName) && value != null;
         ensureClassVariablesSettable();
         return variableTableFastStore(internedName, value);
     }
 
     public IRubyObject deleteClassVariable(String name) {
         assert IdUtil.isClassVariable(name);
         ensureClassVariablesSettable();
         return variableTableRemove(name);
     }
 
     public List<Variable<IRubyObject>> getClassVariableList() {
         ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
         VariableTableEntry[] table = variableTableGetTable();
         IRubyObject readValue;
         for (int i = table.length; --i >= 0; ) {
             for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                 if (IdUtil.isClassVariable(e.name)) {
                     if ((readValue = e.value) == null) readValue = variableTableReadLocked(e);
                     list.add(new VariableEntry<IRubyObject>(e.name, readValue));
                 }
             }
         }
         return list;
     }
 
     public List<String> getClassVariableNameList() {
         ArrayList<String> list = new ArrayList<String>();
         VariableTableEntry[] table = variableTableGetTable();
         for (int i = table.length; --i >= 0; ) {
             for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                 if (IdUtil.isClassVariable(e.name)) {
                     list.add(e.name);
                 }
             }
         }
         return list;
     }
 
     protected static final String ERR_INSECURE_SET_CLASS_VAR = "Insecure: can't modify class variable";
     protected static final String ERR_FROZEN_CVAR_TYPE = "class/module ";
    
     protected final String validateClassVariable(String name) {
         if (IdUtil.isValidClassVariableName(name)) {
             return name;
         }
         throw getRuntime().newNameError("`" + name + "' is not allowed as a class variable name", name);
     }
 
     protected final void ensureClassVariablesSettable() {
         if (!isFrozen() && (getRuntime().getSafeLevel() < 4 || isTaint())) {
             return;
         }
         
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError(ERR_INSECURE_SET_CONSTANT);
         }
         if (isFrozen()) {
             if (this instanceof RubyModule) {
                 throw getRuntime().newFrozenError(ERR_FROZEN_CONST_TYPE);
             } else {
                 throw getRuntime().newFrozenError("");
             }
         }
     }
 
     //
     ////////////////// LOW-LEVEL CONSTANT INTERFACE ////////////////
     //
     // fetch/store/list constants for this module
     //
 
     public boolean hasConstant(String name) {
         assert IdUtil.isConstant(name);
         return constantTableContains(name);
     }
 
     public boolean fastHasConstant(String internedName) {
         assert IdUtil.isConstant(internedName);
         return constantTableFastContains(internedName);
     }
 
     // returns the stored value without processing undefs (autoloads)
     public IRubyObject fetchConstant(String name) {
         assert IdUtil.isConstant(name);
         return constantTableFetch(name);
     }
 
     // returns the stored value without processing undefs (autoloads)
     public IRubyObject fastFetchConstant(String internedName) {
         assert IdUtil.isConstant(internedName);
         return constantTableFastFetch(internedName);
     }
 
     public IRubyObject storeConstant(String name, IRubyObject value) {
         assert IdUtil.isConstant(name) && value != null;
         ensureConstantsSettable();
         return constantTableStore(name, value);
     }
 
     public IRubyObject fastStoreConstant(String internedName, IRubyObject value) {
         assert IdUtil.isConstant(internedName) && value != null;
         ensureConstantsSettable();
         return constantTableFastStore(internedName, value);
     }
 
     // removes and returns the stored value without processing undefs (autoloads)
     public IRubyObject deleteConstant(String name) {
         assert IdUtil.isConstant(name);
         ensureConstantsSettable();
         return constantTableRemove(name);
     }
 
     public List<Variable<IRubyObject>> getStoredConstantList() {
         ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
         ConstantTableEntry[] table = constantTableGetTable();
         for (int i = table.length; --i >= 0; ) {
             for (ConstantTableEntry e = table[i]; e != null; e = e.next) {
                 list.add(e);
             }
         }
         return list;
     }
 
     public List<String> getStoredConstantNameList() {
         ArrayList<String> list = new ArrayList<String>();
         ConstantTableEntry[] table = constantTableGetTable();
         for (int i = table.length; --i >= 0; ) {
             for (ConstantTableEntry e = table[i]; e != null; e = e.next) {
                 list.add(e.name);
             }
         }
         return list;
     }
 
     protected static final String ERR_INSECURE_SET_CONSTANT  = "Insecure: can't modify constant";
     protected static final String ERR_FROZEN_CONST_TYPE = "class/module ";
    
     protected final String validateConstant(String name) {
         if (IdUtil.isValidConstantName(name)) {
             return name;
         }
         throw getRuntime().newNameError("wrong constant name " + name, name);
     }
 
     protected final void ensureConstantsSettable() {
         if (!isFrozen() && (getRuntime().getSafeLevel() < 4 || isTaint())) {
             return;
         }
         
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError(ERR_INSECURE_SET_CONSTANT);
         }
         if (isFrozen()) {
             if (this instanceof RubyModule) {
                 throw getRuntime().newFrozenError(ERR_FROZEN_CONST_TYPE);
             } else {
                 throw getRuntime().newFrozenError("");
             }
         }
     }
 
      
     //
     ////////////////// VARIABLE TABLE METHODS ////////////////
     //
     // Overridden to use variableWriteLock in place of synchronization  
     //
 
     @Override
     protected IRubyObject variableTableStore(String name, IRubyObject value) {
         int hash = name.hashCode();
         ReentrantLock lock;
         (lock = variableWriteLock).lock();
         try {
             VariableTableEntry[] table;
             VariableTableEntry e;
             if ((table = variableTable) == null) {
                 table =  new VariableTableEntry[VARIABLE_TABLE_DEFAULT_CAPACITY];
                 e = new VariableTableEntry(hash, name.intern(), value, null);
                 table[hash & (VARIABLE_TABLE_DEFAULT_CAPACITY - 1)] = e;
                 variableTableThreshold = (int)(VARIABLE_TABLE_DEFAULT_CAPACITY * VARIABLE_TABLE_LOAD_FACTOR);
                 variableTableSize = 1;
                 variableTable = table;
                 return value;
             }
             int potentialNewSize;
             if ((potentialNewSize = variableTableSize + 1) > variableTableThreshold) {
                 table = variableTableRehash();
             }
             int index;
             for (e = table[index = hash & (table.length - 1)]; e != null; e = e.next) {
                 if (hash == e.hash && name.equals(e.name)) {
                     e.value = value;
                     return value;
                 }
             }
             // external volatile value initialization intended to obviate the need for
             // readValueUnderLock technique used in ConcurrentHashMap. may be a little
             // slower, but better to pay a price on first write rather than all reads.
             e = new VariableTableEntry(hash, name.intern(), value, table[index]);
             table[index] = e;
             variableTableSize = potentialNewSize;
             variableTable = table; // write-volatile
         } finally {
             lock.unlock();
         }
         return value;
     }
     
     @Override
     protected IRubyObject variableTableFastStore(String internedName, IRubyObject value) {
         assert internedName == internedName.intern() : internedName + " not interned";
         int hash = internedName.hashCode();
         ReentrantLock lock;
         (lock = variableWriteLock).lock();
         try {
             VariableTableEntry[] table;
             VariableTableEntry e;
