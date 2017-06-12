diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index cca852dc7d..1b4e7b0fd2 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -171,2002 +171,2008 @@ public class RubyModule extends RubyObject {
     
     // Lock used for variableTable/constantTable writes. The RubyObject variableTable
     // write methods are overridden here to use this lock rather than Java
     // synchronization for faster concurrent writes for modules/classes.
     protected final ReentrantLock variableWriteLock = new ReentrantLock();
     
     protected transient volatile ConstantTableEntry[] constantTable =
         new ConstantTableEntry[CONSTANT_TABLE_DEFAULT_CAPACITY];
 
     protected transient int constantTableSize;
 
     protected transient int constantTableThreshold = 
         (int)(CONSTANT_TABLE_DEFAULT_CAPACITY * CONSTANT_TABLE_LOAD_FACTOR);
 
     private final Map<String, DynamicMethod> methods = new ConcurrentHashMap<String, DynamicMethod>(12, 0.75f, 1);
     
     // ClassProviders return Java class/module (in #defineOrGetClassUnder and
     // #defineOrGetModuleUnder) when class/module is opened using colon syntax. 
     private transient List<ClassProvider> classProviders;
 
     /** separate path for MetaClass construction
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass, boolean objectSpace) {
         super(runtime, metaClass, objectSpace);
         id = runtime.allocModuleId();
         // if (parent == null) parent = runtime.getObject();
         setFlag(USER7_F, !isClass());
     }
     
     /** used by MODULE_ALLOCATOR and RubyClass constructors
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
     
     /** standard path for Module construction
      * 
      */
     protected RubyModule(Ruby runtime) {
         this(runtime, runtime.getModule());
     }
 
     public boolean needsImplementer() {
         return getFlag(USER7_F);
     }
     
     /** rb_module_new
      * 
      */
     public static RubyModule newModule(Ruby runtime) {
         return new RubyModule(runtime);
     }
     
     /** rb_module_new/rb_define_module_id/rb_name_class/rb_set_class_path
      * 
      */
     public static RubyModule newModule(Ruby runtime, String name, RubyModule parent, boolean setParent) {
         RubyModule module = newModule(runtime);
         module.setBaseName(name);
         if (setParent) module.setParent(parent);
         parent.setConstant(name, module);
         return module;
     }
     
     // synchronized method per JRUBY-1173 (unsafe Double-Checked Locking)
     // FIXME: synchronization is still wrong in CP code
     public synchronized void addClassProvider(ClassProvider provider) {
         if (classProviders == null) {
             List<ClassProvider> cp = Collections.synchronizedList(new ArrayList<ClassProvider>());
             cp.add(provider);
             classProviders = cp;
         } else {
             synchronized(classProviders) {
                 if (!classProviders.contains(provider)) {
                     classProviders.add(provider);
                 }
             }
         }
     }
 
     public void removeClassProvider(ClassProvider provider) {
         if (classProviders != null) {
             classProviders.remove(provider);
         }
     }
 
     private RubyClass searchProvidersForClass(String name, RubyClass superClazz) {
         if (classProviders != null) {
             synchronized(classProviders) {
                 RubyClass clazz;
                 for (ClassProvider classProvider: classProviders) {
                     if ((clazz = classProvider.defineClassUnder(this, name, superClazz)) != null) {
                         return clazz;
                     }
                 }
             }
         }
         return null;
     }
 
     private RubyModule searchProvidersForModule(String name) {
         if (classProviders != null) {
             synchronized(classProviders) {
                 RubyModule module;
                 for (ClassProvider classProvider: classProviders) {
                     if ((module = classProvider.defineModuleUnder(this, name)) != null) {
                         return module;
                     }
                 }
             }
         }
         return null;
     }
 
     public Dispatcher getDispatcher() {
         return dispatcher;
     }
 
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     protected void setSuperClass(RubyClass superClass) {
         this.superClass = superClass;
     }
 
     public RubyModule getParent() {
         return parent;
     }
 
     public void setParent(RubyModule parent) {
         this.parent = parent;
     }
 
     public Map<String, DynamicMethod> getMethods() {
         return methods;
     }
     
 
     // note that addMethod now does its own put, so any change made to
     // functionality here should be made there as well 
     private void putMethod(String name, DynamicMethod method) {
         // FIXME: kinda hacky...flush STI here
         dispatcher.clearIndex(MethodIndex.getIndex(name));
         getMethods().put(name, method);
     }
 
     /**
      * Is this module one that in an included one (e.g. an IncludedModuleWrapper). 
      */
     public boolean isIncluded() {
         return false;
     }
 
     public RubyModule getNonIncludedClass() {
         return this;
     }
 
     public String getBaseName() {
         return classId;
     }
 
     public void setBaseName(String name) {
         classId = name;
     }
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (getBaseName() == null) {
             if (isClass()) {
                 return "#<" + "Class" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             } else {
                 return "#<" + "Module" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             }
         }
 
         StringBuffer result = new StringBuffer(getBaseName());
         RubyClass objectClass = getRuntime().getObject();
 
         for (RubyModule p = this.getParent(); p != null && p != objectClass; p = p.getParent()) {
             String pName = p.getBaseName();
             // This is needed when the enclosing class or module is a singleton.
             // In that case, we generated a name such as null::Foo, which broke 
             // Marshalling, among others. The correct thing to do in this situation 
             // is to insert the generate the name of form #<Class:01xasdfasd> if 
             // it's a singleton module/class, which this code accomplishes.
             if(pName == null) {
                 pName = p.getName();
             }
             result.insert(0, "::").insert(0, pName);
         }
 
         return result.toString();
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
 
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
 
         return includedModule;
     }
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module;
         if ((module = getConstantAt(name)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     public RubyClass fastGetClass(String internedName) {
         IRubyObject module;
         if ((module = fastGetConstantAt(internedName)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
 
         testFrozen("module");
         if (!isTaint()) {
             getRuntime().secure(4);
         }
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
 
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         if (isSame(module)) {
             return;
         }
 
         infectBy(module);
 
         doIncludeModule(module);
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
     
     public void defineAnnotatedMethod(Class clazz, String name) {
         // FIXME: This is probably not very efficient, since it loads all methods for each call
         boolean foundMethod = false;
         for (Method method : clazz.getDeclaredMethods()) {
             if (method.getName().equals(name) && defineAnnotatedMethod(method, MethodFactory.createFactory(getRuntime().getJRubyClassLoader()))) {
                 foundMethod = true;
             }
         }
 
         if (!foundMethod) {
             throw new RuntimeException("No JRubyMethod present for method " + name + "on class " + clazz.getName());
         }
     }
     
     public void defineAnnotatedConstants(Class clazz) {
         Field[] declaredFields = clazz.getDeclaredFields();
         for (Field field : declaredFields) {
             if(Modifier.isStatic(field.getModifiers())) {
                 defineAnnotatedConstant(field);
             }
         }
     }
 
     public boolean defineAnnotatedConstant(Field field) {
         JRubyConstant jrubyConstant = field.getAnnotation(JRubyConstant.class);
 
         if (jrubyConstant == null) return false;
 
         String[] names = jrubyConstant.value();
         if(names.length == 0) {
             names = new String[]{field.getName()};
         }
 
         Class tp = field.getType();
         IRubyObject realVal = getRuntime().getNil();
 
         try {
             if(tp == Integer.class || tp == Integer.TYPE || tp == Short.class || tp == Short.TYPE || tp == Byte.class || tp == Byte.TYPE) {
                 realVal = RubyNumeric.int2fix(getRuntime(), field.getInt(null));
             } else if(tp == Boolean.class || tp == Boolean.TYPE) {
                 realVal = field.getBoolean(null) ? getRuntime().getTrue() : getRuntime().getFalse();
             }
         } catch(Exception e) {}
 
         
         for(String name : names) {
             this.fastSetConstant(name, realVal);
         }
 
         return true;
     }
 
     public void defineAnnotatedMethods(Class clazz) {
         if (RubyInstanceConfig.INDEXED_METHODS) {
             defineAnnotatedMethodsIndexed(clazz);
         } else {
             defineAnnotatedMethodsIndividually(clazz);
         }
     }
     
     public void defineAnnotatedMethodsIndividually(Class clazz) {
         try {
             Class populatorClass = Class.forName(clazz.getSimpleName() + "Populator");
             TypePopulator populator = (TypePopulator)populatorClass.newInstance();
             populator.populate(this);
         } catch (Throwable t) {
             // fallback on non-pregenerated logic
             Method[] declaredMethods = clazz.getDeclaredMethods();
             MethodFactory methodFactory = MethodFactory.createFactory(getRuntime().getJRubyClassLoader());
             for (Method method: declaredMethods) {
                 defineAnnotatedMethod(method, methodFactory);
             }
             
             // FIXME: dispatcher is only supported for non-pregenerated binding logic
             CallbackFactory callbackFactory = getRuntime().callbackFactory(RubyString.class);
 //            if (this instanceof RubyClass) {
 //                dispatcher = callbackFactory.createDispatcher((RubyClass)this);
 //            }
         }
         
     }
     
     private void defineAnnotatedMethodsIndexed(Class clazz) {
         MethodFactory methodFactory = MethodFactory.createFactory(getRuntime().getJRubyClassLoader());
         methodFactory.defineIndexedAnnotatedMethods(this, clazz, methodDefiningCallback);
     }
     
     private static MethodFactory.MethodDefiningCallback methodDefiningCallback = new MethodFactory.MethodDefiningCallback() {
         public void define(RubyModule module, Method method, DynamicMethod dynamicMethod) {
             JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
             if (jrubyMethod.frame()) {
                 for (String name : jrubyMethod.name()) {
                     ASTInspector.FRAME_AWARE_METHODS.add(name);
                 }
             }
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     module.getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
                 RubyModule metaClass = module.metaClass;
 
                 if (jrubyMethod.meta()) {
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = method.getName();
                         metaClass.addMethod(baseName, dynamicMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             metaClass.addMethod(name, dynamicMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             metaClass.defineAlias(alias, baseName);
                         }
                     }
                 } else {
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = method.getName();
                         module.addMethod(method.getName(), dynamicMethod);
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
                         // module/singleton methods are all defined public
                         DynamicMethod moduleMethod = dynamicMethod.dup();
                         moduleMethod.setVisibility(Visibility.PUBLIC);
 
                         RubyModule singletonClass = module.getSingletonClass();
 
                         if (jrubyMethod.name().length == 0) {
                             baseName = method.getName();
                             singletonClass.addMethod(method.getName(), moduleMethod);
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
     
     public boolean defineAnnotatedMethod(Method method, MethodFactory methodFactory) {
         JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, new JavaMethodDescriptor(method));
             methodDefiningCallback.define(this, method, dynamicMethod);
             
             return true;
         }
         return false;
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method, Visibility visibility) {
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastProtectedMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PROTECTED));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(ThreadContext context, String name) {
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw new SecurityException("Insecure: can't undef");
         }
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             getRuntime().getWarnings().warn(ID.UNDEFINING_BAD, "undefining `"+ name +"' may cause serious problem");
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
 
             throw getRuntime().newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_undefined", getRuntime().newSymbol(name));
         } else {
             callMethod(context, "method_undefined", getRuntime().newSymbol(name));
         }
     }
     
     @JRubyMethod(name = "include?", required = 1)
     public IRubyObject include_p(IRubyObject arg) {
         if (!arg.isModule()) {
             throw getRuntime().newTypeError(arg, getRuntime().getModule());
         }
         
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if ((p instanceof IncludedModuleWrapper) && ((IncludedModuleWrapper) p).getNonIncludedClass() == arg) {
                 return getRuntime().newBoolean(true);
             }
         }
         
         return getRuntime().newBoolean(false);
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't define method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             // If we add a method which already is cached in this class, then we should update the 
             // cachemap so it stays up to date.
             DynamicMethod existingMethod = getMethods().put(name, method);
             if (existingMethod != null) {
                 getRuntime().getCacheMap().remove(existingMethod);
             }
             // note: duplicating functionality from putMethod, since we
             // remove/put atomically here
             dispatcher.clearIndex(MethodIndex.getIndex(name));
         }
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
+
+        Arity arity = proc.getBlock().arity();
         // just using required is broken...but no more broken than before zsuper refactoring
-        proc.getBlock().getBody().getStaticScope().setRequiredArgs(proc.getBlock().arity().required());
+        proc.getBlock().getBody().getStaticScope().setRequiredArgs(arity.required());
+
+        if(!arity.isFixed()) {
+            proc.getBlock().getBody().getStaticScope().setRestArg(arity.required());
+        }
 
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
         RubySymbol newSym = newId instanceof RubySymbol ? (RubySymbol)newId :
             context.getRuntime().newSymbol(newName);
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
diff --git a/test/testModule.rb b/test/testModule.rb
index 661e6d0d7c..e689399eca 100644
--- a/test/testModule.rb
+++ b/test/testModule.rb
@@ -1,409 +1,437 @@
 require 'test/minirunit'
 test_check "module"
 
 # MRI 1.7-style self-replacement for define_method's blocks
 
 class TestModule_Foo
   define_method(:foo) { self }
 end
 # MRI 1.6 returns Class, 1.7 returns Foo.
 #test_equal(Class, TestModule_Foo.new.foo.class)
 #test_equal(TestModule_Foo, TestModule_Foo.new.foo.class)
 
 test_equal("TestModule_Foo", TestModule_Foo.new.foo.class.name)
 
 testmodule_local_variable = 123
 
 TestModule_Foo.module_eval {||
   def abc(x)
     2 * x
   end
   XYZ = 10
   ABC = self
   LOCAL1 = testmodule_local_variable
 }
 test_ok(! defined? abc)
 test_equal(4, TestModule_Foo.new.abc(2))
 test_equal(10, XYZ)
 test_equal(TestModule_Foo, ABC)
 test_equal(testmodule_local_variable, LOCAL1)
 
 class TestModule2
 end
 TestModule2.module_eval("def abc(x); 3 * x; end; XYZ = 12; ABC = self; LOCAL2 = testmodule_local_variable")
 test_equal(6, TestModule2.new.abc(2))
 test_equal(12, TestModule2::XYZ)
 test_equal(TestModule2, TestModule2::ABC)
 test_equal(testmodule_local_variable, TestModule2::LOCAL2)
 
 module A
   module B
     module C
       test_equal([A::B::C, A::B, A], Module.nesting)
       $nest = Module.nesting
     end
     module D
       test_equal([A::B::D, A::B, A], Module.nesting)
     end
     test_equal([A::B, A], Module.nesting)
   end
 end
 test_equal([], Module.nesting)
 test_equal([A::B::C, A::B, A], $nest)
 
 OUTER_CONSTANT = 4711
 
 module TestModule_A
   A_CONSTANT = 123
   class TestModule_B
     attr_reader :a
     attr_reader :b
     def initialize
       @a = A_CONSTANT
       @b = OUTER_CONSTANT
     end
   end
 end
 test_equal(123, TestModule_A::TestModule_B.new.a)
 test_equal(4711, TestModule_A::TestModule_B.new.b)
 
 class TestModule_C_1 < Array
   def a_defined_method
     :ok
   end
 end
 class TestModule_C_1
 end
 test_equal(:ok, TestModule_C_1.new.a_defined_method)
 
 
 #test_exception(TypeError) {
 #  module Object; end
 #}
 test_exception(TypeError) {
   class Kernel; end
 }
 
 
 ################# test external reference to constant from included module
 module M1
   CONST = 7
 end
 class C1
   include M1
   x = CONST
 end
 
 test_equal(7, C1::CONST)
 
 ################ test define_method
 
 class C2
   define_method( 'methodName', proc { 1 })
   e = test_exception(TypeError) {
     define_method( 'methodNameX', 'badParameter')
   }
   test_equal('wrong argument type String (expected Proc/Method)', e.message)
 end
 class C3 < C2
   define_method( 'methodName2', instance_method(:methodName))
 end
 
 ############### test caching system when including a module
 
 class D1
   def foo; "foo"; end
 end
 
 class D2 < D1
   def bar; foo; end
 end
 
 class D3 < D2
   def bar; foo; end
 end
 
 # Call methods once to force D1.foo to cache
 b = D2.new
 b.bar
 c = D3.new
 c.bar
 
 module Foo
   def foo; "fooFoo"; end
 end
 
 class D2
   include Foo
 end
 
 test_equal("fooFoo", b.bar)
 test_equal("fooFoo", c.bar)
 
 ###### included
 $included = false
 module I1
   def I1.included(m)
     test_equal(I2, m)
     $included = true
   end
 end
 
 class I2
   include I1
 end
 
 test_ok($included)
 
 ############### test 'super' within a module method
 module A3
   module B3
     def self.extend_object(obj)
       super
     end
   end
 end
 
 x = []
 x.extend(A::B)
 
 test_ok(x.kind_of?(A::B))
 
 ############## test multiple layers of includes
 module ModA
  def methodA; true; end
 end
 
 module ModB
  include ModA
  def methodB; methodA; end
 end
 
 module ModC
  include ModB
  def methodC; methodB; end
 end
 
 class ModTest
  include ModC
  def test; methodC; end
 end
 
 test_ok(ModTest.new.test)
 
 ############# test same included modules from multiple parents
 module ModHello
   def hello; "hello"; end
 end
 module IncludedFromMultipleParents
 end
 
 module ParentMod
   include ModHello
   include IncludedFromMultipleParents
 end
 
 class ParentClass
   include IncludedFromMultipleParents
 end
 
 class Victim < ParentClass
   include ParentMod
 end
 
 v = Victim.new
 test_no_exception { v.hello }
 
 ###### instance_methods + undef_method
 
 class InstanceMethodsUndefBase
   def foo; end
 end
 
 class InstanceMethodsUndefDerived < InstanceMethodsUndefBase
   test_equal(true, instance_methods.include?("foo"))
   undef_method "foo"
   test_equal(false, instance_methods.include?("foo"))
 end
 
 class InstanceMethodsUndefBase
   test_equal(true, instance_methods.include?("foo"))
 end
   
 ###### attr_reader ######
 
 class AttrReaderTest
   attr_reader :foo
   def initialize(a); @foo = a; end
 end
 
 a = AttrReaderTest.new(9)
 test_equal(9, a.foo)
 test_exception(ArgumentError) { a.foo 1 }
 test_exception(ArgumentError) { a.foo 1, 2 }
 
 ##### test include order when specifying multiple modules ###
 class Base
 attr_reader :last_called
 def initialize
 super
 end
 end
 
 module Mod1
 def initialize
 super
   @last_called = :Mod1
 end
 end
 
 module Mod2
 def initialize
 super
   @last_called = :Mod2
 end
 end
 
 class Child < Base
 include Mod1, Mod2
 
 def initialize
 super
 end
 end
 
 test_equal(:Mod1, Child.new.last_called)
 
 ##### JRUBY-104: test super called from within a module-defined initialize #####
 module FooNew
 def initialize(); @inits ||= []; @inits << FooNew; super(); end
 end
 
 class ClassB
 def initialize(); @inits ||= []; @inits << ClassB; end
 end
 
 class ClassA < ClassB
 include FooNew
 def inits; @inits; end
 end
 
 test_equal([FooNew, ClassB], ClassA.new().inits)
 
 module Foo
   Bar = Class.new
 end
 
 test_equal("Foo::Bar",Foo::Bar.name)
 
 Fred = Module.new do
   def meth1
      "hello" 
   end
 end
 
 a = "my string"
 a.extend(Fred)
 test_equal("hello", a.meth1)
 
 # Chain of includes deals with method cache flush
 module MT_A
   def foo
   end
 end
 module MT_B
   include MT_A
   alias :foo_x :foo
 end
 class MT_C
   include MT_B
 end
 
 # Make sure that the self-object inside a block to new instance of Module evals correctly.
 
 x = Module.new do
   def self.foo
     "1"
   end
 end
 
 test_ok x.methods.include?("foo")
 
 # Make sure that the self object will fire correctly with super, when using define_method
 Aaaa = Class.new(Dir) { 
   define_method(:initialize) do |*args| 
     super(*args) 
   end 
 }
 
 test_no_exception { Aaaa.new("/") }
 
 class Froom < Module; end
 test_equal Froom, Froom.new.class
 
 # Dup/Clon'ing of modules
 module M
     def self.initialize_copy original
        raise Exception.new
     end
     
     def self.meth;end
 end
 
 test_no_exception do
     M.dup
 end
 
 test_exception do
     M.clone
 end
 
 
 module M2
     def self.meth;end
 end
 
 test_no_exception do
     M2.clone.instance_eval{meth}
     M2.dup.instance_eval{meth}
 end
 
 test_ok(9.class.include?(Precision))
 test_ok(9.class.include?(Kernel))
 test_equal(false, Precision.include?(Precision))
 
 class ModuleForTestingIfMethodsAreDefined
   def a_public_method; end
   protected
   def a_protected_method; end
   private
   def a_private_method; end
 end
 test_ok(ModuleForTestingIfMethodsAreDefined.method_defined?(:a_public_method))
 test_ok(ModuleForTestingIfMethodsAreDefined.method_defined?(:a_protected_method))
 test_ok(! ModuleForTestingIfMethodsAreDefined.method_defined?(:a_private_method))
 
 test_ok(ModuleForTestingIfMethodsAreDefined.public_method_defined?(:a_public_method))
 test_ok(! ModuleForTestingIfMethodsAreDefined.public_method_defined?(:a_protected_method))
 test_ok(! ModuleForTestingIfMethodsAreDefined.public_method_defined?(:a_private_method))
 
 test_ok(! ModuleForTestingIfMethodsAreDefined.protected_method_defined?(:a_public_method))
 test_ok(ModuleForTestingIfMethodsAreDefined.protected_method_defined?(:a_protected_method))
 test_ok(! ModuleForTestingIfMethodsAreDefined.protected_method_defined?(:a_private_method))
 
 test_ok(! ModuleForTestingIfMethodsAreDefined.private_method_defined?(:a_public_method))
 test_ok(! ModuleForTestingIfMethodsAreDefined.private_method_defined?(:a_protected_method))
 test_ok(ModuleForTestingIfMethodsAreDefined.private_method_defined?(:a_private_method))
 
 module Mod
   @@one = 123
   @@two = nil
 end
 
 test_exception(NameError) do 
   Mod.class_variable_defined? :abc
 end
 
 test_ok !Mod.class_variable_defined?(:@@three)
 test_ok Mod.class_variable_defined?(:@@one)
 test_ok Mod.class_variable_defined?(:@@two)
+
+# JRUBY-2330, combination of define_method, rest args and zsuper fails
+class Foo2330
+ def bar(x, y); [x,y]; end
+end
+
+class Bar2330 < Foo2330
+ define_method :bar do |*args|
+    super
+  end
+end
+
+test_equal [1,2], Bar2330.new.bar(1,2)
+
+class Quux2330
+ def bar(x, y);  [x,y]; end
+end
+
+f = Quux2330.new
+module Module2330 
+ define_method :bar do |*args|
+   super
+ end
+end
+
+f.extend Module2330
+
+test_equal [1,2], f.bar(1, 2)
