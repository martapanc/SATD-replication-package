diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index b5c3fdc7c3..a87552e349 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -428,2040 +428,2038 @@ public class RubyModule extends RubyObject {
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
         checkForCyclicInclude(module);
 
         infectBy(module);
 
         doIncludeModule(module);
         invalidateConstantCache();
         invalidateCacheDescendants();
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 PRIVATE : PUBLIC;
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
             if (Modifier.isStatic(field.getModifiers())) {
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
         IRubyObject realVal;
 
         try {
             if(tp == Integer.class || tp == Integer.TYPE || tp == Short.class || tp == Short.TYPE || tp == Byte.class || tp == Byte.TYPE) {
                 realVal = RubyNumeric.int2fix(getRuntime(), field.getInt(null));
             } else if(tp == Boolean.class || tp == Boolean.TYPE) {
                 realVal = field.getBoolean(null) ? getRuntime().getTrue() : getRuntime().getFalse();
             } else {
                 realVal = getRuntime().getNil();
             }
         } catch(Exception e) {
             realVal = getRuntime().getNil();
         }
 
         
         for(String name : names) {
             this.fastSetConstant(name, realVal);
         }
 
         return true;
     }
 
     public void defineAnnotatedMethods(Class clazz) {
         defineAnnotatedMethodsIndividually(clazz);
     }
     
     public static class MethodClumper {
         Map<String, List<JavaMethodDescriptor>> annotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_8 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_8 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_9 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_9 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> allAnnotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         
         public void clump(Class cls) {
             Method[] declaredMethods = cls.getDeclaredMethods();
             for (Method method: declaredMethods) {
                 JRubyMethod anno = method.getAnnotation(JRubyMethod.class);
                 if (anno == null) continue;
                 
                 JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
                 
                 String name = anno.name().length == 0 ? method.getName() : anno.name()[0];
                 
                 List<JavaMethodDescriptor> methodDescs;
                 Map<String, List<JavaMethodDescriptor>> methodsHash = null;
                 if (desc.isStatic) {
                     if (anno.compat() == RUBY1_8) {
                         methodsHash = staticAnnotatedMethods1_8;
                     } else if (anno.compat() == RUBY1_9) {
                         methodsHash = staticAnnotatedMethods1_9;
                     } else {
                         methodsHash = staticAnnotatedMethods;
                     }
                 } else {
                     if (anno.compat() == RUBY1_8) {
                         methodsHash = annotatedMethods1_8;
                     } else if (anno.compat() == RUBY1_9) {
                         methodsHash = annotatedMethods1_9;
                     } else {
                         methodsHash = annotatedMethods;
                     }
                 }
 
                 // add to specific
                 methodDescs = methodsHash.get(name);
                 if (methodDescs == null) {
                     methodDescs = new ArrayList<JavaMethodDescriptor>();
                     methodsHash.put(name, methodDescs);
                 }
                 
                 methodDescs.add(desc);
 
                 // add to general
                 methodDescs = allAnnotatedMethods.get(name);
                 if (methodDescs == null) {
                     methodDescs = new ArrayList<JavaMethodDescriptor>();
                     allAnnotatedMethods.put(name, methodDescs);
                 }
 
                 methodDescs.add(desc);
             }
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAllAnnotatedMethods() {
             return allAnnotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods() {
             return annotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_8() {
             return annotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_9() {
             return annotatedMethods1_9;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods() {
             return staticAnnotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_8() {
             return staticAnnotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_9() {
             return staticAnnotatedMethods1_9;
         }
     }
     
     public void defineAnnotatedMethodsIndividually(Class clazz) {
         TypePopulator populator;
         
         if (RubyInstanceConfig.FULL_TRACE_ENABLED || RubyInstanceConfig.REFLECTED_HANDLES) {
             // we want reflected invokers or need full traces, use default (slow) populator
             if (DEBUG) System.out.println("trace mode, using default populator");
             populator = TypePopulator.DEFAULT;
         } else {
             try {
                 String qualifiedName = "org.jruby.gen." + clazz.getCanonicalName().replace('.', '$');
 
                 if (DEBUG) System.out.println("looking for " + qualifiedName + "$Populator");
 
                 Class populatorClass = Class.forName(qualifiedName + "$Populator");
                 populator = (TypePopulator)populatorClass.newInstance();
             } catch (Throwable t) {
                 if (DEBUG) System.out.println("Could not find it, using default populator");
                 populator = TypePopulator.DEFAULT;
             }
         }
         
         populator.populate(this, clazz);
     }
     
     public boolean defineAnnotatedMethod(String name, List<JavaMethodDescriptor> methods, MethodFactory methodFactory) {
         JavaMethodDescriptor desc = methods.get(0);
         if (methods.size() == 1) {
             return defineAnnotatedMethod(desc, methodFactory);
         } else {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, methods);
             define(this, desc, dynamicMethod);
             
             return true;
         }
     }
     
     public boolean defineAnnotatedMethod(Method method, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
     
     public boolean defineAnnotatedMethod(JavaMethodDescriptor desc, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = desc.anno;
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             define(this, desc, dynamicMethod);
 
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
         synchronized(getMethodsForWrite()) {
             addMethodAtBootTimeOnly(name, method);
             invalidateCacheDescendants();
         }
     }
 
     /**
      * This method is not intended for use by normal users; it is a fast-path
      * method that skips synchronization and hierarchy invalidation to speed
      * boot-time method definition.
      *
      * @param name The name to which to bind the method
      * @param method The method to bind
      */
     public void addMethodAtBootTimeOnly(String name, DynamicMethod method) {
         getMethodsForWrite().put(name, method);
 
         getRuntime().addProfiledMethod(name, method);
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
         synchronized(getMethodsForWrite()) {
             DynamicMethod method = (DynamicMethod) getMethodsForWrite().remove(name);
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
         int token = getCacheToken();
         DynamicMethod method = searchMethodInner(name);
 
         if (method instanceof DefaultMethod) {
             method = ((DefaultMethod)method).getMethodForCaching();
         }
 
         return method != null ? addToCache(name, method, token) : addToCache(name, UndefinedMethod.getInstance(), token);
     }
     
     public final int getCacheToken() {
         return generation;
     }
 
     private final Map<String, CacheEntry> getCachedMethods() {
         return this.cachedMethods;
     }
 
     private final Map<String, CacheEntry> getCachedMethodsForWrite() {
         Map<String, CacheEntry> myCachedMethods = this.cachedMethods;
         return myCachedMethods == Collections.EMPTY_MAP ?
             this.cachedMethods = new ConcurrentHashMap<String, CacheEntry>(0, 0.75f, 1) :
             myCachedMethods;
     }
     
     private CacheEntry cacheHit(String name) {
         CacheEntry cacheEntry = getCachedMethods().get(name);
 
         if (cacheEntry != null) {
             if (cacheEntry.token == getCacheToken()) {
                 return cacheEntry;
             }
         }
         
         return null;
     }
     
     protected static abstract class CacheEntryFactory {
         public abstract CacheEntry newCacheEntry(DynamicMethod method, int token);
 
         /**
          * Test all WrapperCacheEntryFactory instances in the chain for assignability
          * from the given class.
          *
          * @param cacheEntryFactoryClass the class from which to test assignability
          * @return whether the given class is assignable from any factory in the chain
          */
         public boolean hasCacheEntryFactory(Class cacheEntryFactoryClass) {
             CacheEntryFactory current = this;
             while (current instanceof WrapperCacheEntryFactory) {
                 if (cacheEntryFactoryClass.isAssignableFrom(current.getClass())) {
                     return true;
                 }
                 current = ((WrapperCacheEntryFactory)current).getPrevious();
             }
             if (cacheEntryFactoryClass.isAssignableFrom(current.getClass())) {
                 return true;
             }
             return false;
         }
     }
 
     /**
      * A wrapper CacheEntryFactory, for delegating cache entry creation along a chain.
      */
     protected static abstract class WrapperCacheEntryFactory extends CacheEntryFactory {
         /** The CacheEntryFactory being wrapped. */
         protected final CacheEntryFactory previous;
 
         /**
          * Construct a new WrapperCacheEntryFactory using the given CacheEntryFactory as
          * the "previous" wrapped factory.
          *
          * @param previous the wrapped factory
          */
         public WrapperCacheEntryFactory(CacheEntryFactory previous) {
             this.previous = previous;
         }
 
         public CacheEntryFactory getPrevious() {
             return previous;
         }
     }
 
     protected static final CacheEntryFactory NormalCacheEntryFactory = new CacheEntryFactory() {
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             return new CacheEntry(method, token);
         }
     };
 
     protected static class SynchronizedCacheEntryFactory extends WrapperCacheEntryFactory {
         public SynchronizedCacheEntryFactory(CacheEntryFactory previous) {
             super(previous);
         }
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             if (method.isUndefined()) {
                 return new CacheEntry(method, token);
             }
             // delegate up the chain
             CacheEntry delegated = previous.newCacheEntry(method, token);
             return new CacheEntry(new SynchronizedDynamicMethod(delegated.method), delegated.token);
         }
     }
 
     protected static class ProfilingCacheEntryFactory extends WrapperCacheEntryFactory {
         public ProfilingCacheEntryFactory(CacheEntryFactory previous) {
             super(previous);
         }
         @Override
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             if (method.isUndefined()) {
                 return new CacheEntry(method, token);
             }
             CacheEntry delegated = previous.newCacheEntry(method, token);
             return new CacheEntry(new ProfilingDynamicMethod(delegated.method), delegated.token);
         }
     }
 
     private volatile CacheEntryFactory cacheEntryFactory;
 
     // modifies this class only; used to make the Synchronized module synchronized
     public void becomeSynchronized() {
         cacheEntryFactory = new SynchronizedCacheEntryFactory(cacheEntryFactory);
     }
 
     public boolean isSynchronized() {
         return cacheEntryFactory.hasCacheEntryFactory(SynchronizedCacheEntryFactory.class);
     }
 
     private CacheEntry addToCache(String name, DynamicMethod method, int token) {
         CacheEntry entry = cacheEntryFactory.newCacheEntry(method, token);
         getCachedMethodsForWrite().put(name, entry);
 
         return entry;
     }
     
     protected DynamicMethod searchMethodInner(String name) {
         DynamicMethod method = getMethods().get(name);
         
         if (method != null) return method;
         
         return superClass == null ? null : superClass.searchMethodInner(name);
     }
 
     public void invalidateCacheDescendants() {
         if (DEBUG) System.out.println("invalidating descendants: " + classId);
         generation = getRuntime().getNextModuleGeneration();
         // update all hierarchies into which this module has been included
         synchronized (getRuntime().getHierarchyLock()) {
             for (RubyClass includingHierarchy : includingHierarchies) {
                 includingHierarchy.invalidateCacheDescendants();
             }
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
 
         // JRUBY-2435: Aliasing eval and other "special" methods should display a warning
         // We warn because we treat certain method names as "special" for purposes of
         // optimization. Hopefully this will be enough to convince people not to alias
         // them.
         if (SCOPE_CAPTURING_METHODS.contains(oldName)) {
             runtime.getWarnings().warn("`" + oldName + "' should not be aliased");
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
         IRubyObject classObj = getConstantAtSpecial(name);
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
             if (superClazz == runtime.getObject() && RubyInstanceConfig.REIFY_RUBY_CLASSES) {
                 clazz = RubyClass.newClass(runtime, superClazz, name, REIFYING_OBJECT_ALLOCATOR, this, true);
             } else {
                 clazz = RubyClass.newClass(runtime, superClazz, name, superClazz.getAllocator(), this, true);
             }
         }
 
         return clazz;
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyModule defineOrGetModuleUnder(String name) {
         // This method is intended only for defining new modules in Ruby code
         Ruby runtime = getRuntime();
         IRubyObject moduleObj = getConstantAtSpecial(name);
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
 
     private void addAccessor(ThreadContext context, String internedName, Visibility visibility, boolean readable, boolean writeable) {
         assert internedName == internedName.intern() : internedName + " is not interned";
 
         final Ruby runtime = context.getRuntime();
 
         if (visibility == PRIVATE) {
             //FIXME warning
         } else if (visibility == MODULE_FUNCTION) {
             visibility = PRIVATE;
             // FIXME warning
         }
         final String variableName = ("@" + internedName).intern();
         if (readable) {
             addMethod(internedName, new JavaMethodZero(this, visibility, CallConfiguration.FrameNoneScopeNone) {
                 // volatile to ensure visibility across threads
                 private volatile RubyClass.VariableAccessor accessor = RubyClass.VariableAccessor.DUMMY_ACCESSOR;
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                     IRubyObject variable = (IRubyObject)verifyAccessor(self.getMetaClass().getRealClass()).get(self);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 private RubyClass.VariableAccessor verifyAccessor(RubyClass cls) {
                     RubyClass.VariableAccessor localAccessor = accessor;
                     if (localAccessor.getClassId() != cls.hashCode()) {
                         localAccessor = cls.getVariableAccessorForRead(variableName);
                         accessor = localAccessor;
                     }
                     return localAccessor;
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
         if (writeable) {
             internedName = (internedName + "=").intern();
             addMethod(internedName, new JavaMethodOne(this, visibility, CallConfiguration.FrameNoneScopeNone) {
                 // volatile to ensure visibility across threads
                 private volatile RubyClass.VariableAccessor accessor = RubyClass.VariableAccessor.DUMMY_ACCESSOR;
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1) {
                     verifyAccessor(self.getMetaClass().getRealClass()).set(self, arg1);
                     return arg1;
                 }
 
                 private RubyClass.VariableAccessor verifyAccessor(RubyClass cls) {
                     RubyClass.VariableAccessor localAccessor = accessor;
                     if (localAccessor.getClassId() != cls.hashCode()) {
                         localAccessor = cls.getVariableAccessorForWrite(variableName);
                         accessor = localAccessor;
                     }
                     return localAccessor;
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
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             getRuntime().secure(4);
         }
 
         DynamicMethod method = deepMethodSearch(name, runtime);
 
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
 
     private DynamicMethod deepMethodSearch(String name, Ruby runtime) {
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined() && isModule()) {
             method = runtime.getObject().searchMethod(name);
         }
 
         if (method.isUndefined()) {
             throw runtime.newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
         return method;
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
 
     public void checkMethodBound(ThreadContext context, IRubyObject[] args, Visibility visibility) {
         if (args.length == 0) {
             throw context.getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asJavaString();
 
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined() && method.getVisibility() != visibility) {
             Ruby runtime = context.getRuntime();
             RubyNameError.RubyNameErrorMessage message = new RubyNameError.RubyNameErrorMessage(runtime, this,
                     runtime.newString(name), method.getVisibility(), CallType.NORMAL);
 
             throw runtime.newNoMethodError(message.to_str(context).asJavaString(), name, NEVER);
         }
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String methodName, boolean bound, Visibility visibility) {
         return newMethod(receiver, methodName, bound, visibility, false, true);
     }
 
     public IRubyObject newMethod(IRubyObject receiver, final String methodName, boolean bound, Visibility visibility, boolean respondToMissing) {
         return newMethod(receiver, methodName, bound, visibility, respondToMissing, true);
     }
 
     public static class RespondToMissingMethod extends JavaMethod.JavaMethodNBlock {
         final CallSite site;
         public RespondToMissingMethod(RubyModule implClass, Visibility vis, String methodName) {
             super(implClass, vis);
 
             site = new FunctionalCachingCallSite(methodName);
         }
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             return site.call(context, self, self, args, block);
         }
 
         public boolean equals(Object other) {
             if (!(other instanceof RespondToMissingMethod)) return false;
 
             RespondToMissingMethod rtmm = (RespondToMissingMethod)other;
 
             return this.site.methodName.equals(rtmm.site.methodName) &&
                     getImplementationClass() == rtmm.getImplementationClass();
         }
     }
 
     public IRubyObject newMethod(IRubyObject receiver, final String methodName, boolean bound, Visibility visibility, boolean respondToMissing, boolean priv) {
         DynamicMethod method = searchMethod(methodName);
 
         if (method.isUndefined() ||
             (visibility != null && method.getVisibility() != visibility)) {
             if (respondToMissing) { // 1.9 behavior
                 if (receiver.respondsToMissing(methodName, priv)) {
                     method = new RespondToMissingMethod(this, PUBLIC, methodName);
                 } else {
                     throw getRuntime().newNameError("undefined method `" + methodName +
                         "' for class `" + this.getName() + "'", methodName);
                 }
             } else {
                 throw getRuntime().newNameError("undefined method `" + methodName +
                     "' for class `" + this.getName() + "'", methodName);
             }
         }
 
         RubyModule implementationModule = method.getImplementationClass();
         RubyModule originModule = this;
         while (originModule != implementationModule && originModule.isSingleton()) {
             originModule = ((MetaClass)originModule).getRealClass();
         }
 
         RubyMethod newMethod;
         if (bound) {
             newMethod = RubyMethod.newMethod(implementationModule, methodName, originModule, methodName, method, receiver);
         } else {
             newMethod = RubyUnboundMethod.newUnboundMethod(implementationModule, methodName, originModule, methodName, method);
         }
         newMethod.infectBy(this);
 
         return newMethod;
     }
 
     @JRubyMethod(name = "define_method", visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, Block block) {
         Ruby runtime = context.getRuntime();
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
-        Visibility visibility = context.getCurrentVisibility();
+        Visibility visibility = PUBLIC;
 
-        if (visibility == MODULE_FUNCTION) visibility = PRIVATE;
         RubyProc proc = runtime.newProc(Block.Type.LAMBDA, block);
 
         // a normal block passed to define_method changes to do arity checking; make it a lambda
         proc.getBlock().type = Block.Type.LAMBDA;
         
         newMethod = createProcMethod(name, visibility, proc);
         
-        RuntimeHelpers.addInstanceMethod(this, name, newMethod, context.getCurrentVisibility(), context, runtime);
+        RuntimeHelpers.addInstanceMethod(this, name, newMethod, visibility, context, runtime);
 
         return proc;
     }
     
     @JRubyMethod(name = "define_method", visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject body;
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
-        Visibility visibility = context.getCurrentVisibility();
+        Visibility visibility = PUBLIC;
 
-        if (visibility == MODULE_FUNCTION) visibility = PRIVATE;
         if (runtime.getProc().isInstance(arg1)) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (RubyProc)arg1;
             body = proc;
 
             newMethod = createProcMethod(name, visibility, proc);
         } else if (runtime.getMethod().isInstance(arg1)) {
             RubyMethod method = (RubyMethod)arg1;
             body = method;
 
             newMethod = new MethodMethod(this, method.unbind(), visibility);
         } else {
             throw runtime.newTypeError("wrong argument type " + arg1.getType().getName() + " (expected Proc/Method)");
         }
         
-        RuntimeHelpers.addInstanceMethod(this, name, newMethod, context.getCurrentVisibility(), context, runtime);
+        RuntimeHelpers.addInstanceMethod(this, name, newMethod, visibility, context, runtime);
 
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
             throw context.getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 2)");
         }
     }
     
     private DynamicMethod createProcMethod(String name, Visibility visibility, RubyProc proc) {
         Block block = proc.getBlock();
         block.getBinding().getFrame().setKlazz(this);
         block.getBinding().getFrame().setName(name);
         block.getBinding().setMethod(name);
         
         StaticScope scope = block.getBody().getStaticScope();
 
         // for zsupers in define_method (blech!) we tell the proc scope to act as the "argument" scope
         scope.makeArgumentScope();
 
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
 
     @JRubyMethod(name = "name", compat = RUBY1_9)
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
         if (originalModule.hasVariables()) syncVariables(originalModule);
         syncConstants(originalModule);
 
         originalModule.cloneMethods(this);
 
         return this;
     }
 
     public void syncConstants(RubyModule other) {
         if (other.getConstantMap() != Collections.EMPTY_MAP) {
             getConstantMapForWrite().putAll(other.getConstantMap());
         }
     }
 
     public void syncClassVariables(RubyModule other) {
         if (other.getClassVariablesForRead() != Collections.EMPTY_MAP) {
             getClassVariables().putAll(other.getClassVariablesForRead());
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
             block.yieldNonArray(getRuntime().getCurrentContext(), this, this, this);
         }
 
         return getRuntime().getNil();
     }
     
     public void addReadWriteAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), PUBLIC, true, true);
     }
     
     public void addReadAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), PUBLIC, true, false);
     }
     
     public void addWriteAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), PUBLIC, false, true);
     }
 
     /** rb_mod_attr
      *
      */
     @JRubyMethod(name = "attr", required = 1, optional = 1, visibility = PRIVATE, reads = VISIBILITY, compat = RUBY1_8)
     public IRubyObject attr(ThreadContext context, IRubyObject[] args) {
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
         
         addAccessor(context, args[0].asJavaString().intern(), visibility, true, writeable);
 
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "attr", rest = true, visibility = PRIVATE, reads = VISIBILITY, compat = RUBY1_9)
     public IRubyObject attr19(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         if (args.length == 2 && (args[1] == runtime.getTrue() || args[1] == runtime.getFalse())) {
             runtime.getWarnings().warn(ID.OBSOLETE_ARGUMENT, "optional boolean argument is obsoleted");
             addAccessor(context, args[0].asJavaString().intern(), context.getCurrentVisibility(), args[0].isTrue(), true);
             return runtime.getNil();
         }
 
         return attr_reader(context, args);
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
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
 
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), visibility, true, false);
         }
 
         return context.getRuntime().getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     @JRubyMethod(name = "attr_writer", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_writer(ThreadContext context, IRubyObject[] args) {
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
 
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), visibility, false, true);
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
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
 
         for (int i = 0; i < args.length; i++) {
             // This is almost always already interned, since it will be called with a symbol in most cases
             // but when created from Java code, we might get an argument that needs to be interned.
             // addAccessor has as a precondition that the string MUST be interned
             addAccessor(context, args[i].asJavaString().intern(), visibility, true, true);
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
 
         populateInstanceMethodNames(seen, ary, visibility, not, useSymbols, includeSuper);
 
         return ary;
     }
 
     public void populateInstanceMethodNames(Set<String> seen, RubyArray ary, final Visibility visibility, boolean not, boolean useSymbols, boolean includeSuper) {
         Ruby runtime = getRuntime();
 
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
     }
 
     @JRubyMethod(name = "instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray instance_methods(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, true, false);
     }
 
     @JRubyMethod(name = "instance_methods", optional = 1, compat = RUBY1_9)
     public RubyArray instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, true, true);
     }
 
     @JRubyMethod(name = "public_instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray public_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PUBLIC, false, false);
     }
 
     @JRubyMethod(name = "public_instance_methods", optional = 1, compat = RUBY1_9)
     public RubyArray public_instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PUBLIC, false, true);
     }
 
     @JRubyMethod(name = "instance_method", required = 1)
     public IRubyObject instance_method(IRubyObject symbol) {
         return newMethod(null, symbol.asJavaString(), false, null);
     }
 
     /** rb_class_protected_instance_methods
      *
      */
     @JRubyMethod(name = "protected_instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray protected_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PROTECTED, false, false);
     }
 
     @JRubyMethod(name = "protected_instance_methods", optional = 1, compat = RUBY1_9)
     public RubyArray protected_instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PROTECTED, false, true);
     }
 
     /** rb_class_private_instance_methods
      *
      */
     @JRubyMethod(name = "private_instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray private_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, false, false);
     }
 
     @JRubyMethod(name = "private_instance_methods", optional = 1, compat = RUBY1_9)
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
     @JRubyMethod(name = "include", rest = true, visibility = PRIVATE)
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
 
     @JRubyMethod(name = "included", required = 1, visibility = PRIVATE)
     public IRubyObject included(ThreadContext context, IRubyObject other) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "extended", required = 1, frame = true, visibility = PRIVATE)
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
         Ruby runtime = context.getRuntime();
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw runtime.newSecurityError("Insecure: can't change method visibility");
         }
 
         if (args.length == 0) {
             context.setCurrentVisibility(MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, PRIVATE);
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asJavaString().intern();
                 DynamicMethod method = deepMethodSearch(name, runtime);
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
 
     @JRubyMethod(name = {"module_exec", "class_exec"}, frame = true)
     public IRubyObject module_exec(ThreadContext context, Block block) {
         if (block.isGiven()) {
             return yieldUnder(context, this, block);
         } else {
             throw context.getRuntime().newLocalJumpErrorNoBlock();
         }
     }
     @JRubyMethod(name = {"module_exec", "class_exec"}, rest = true, frame = true)
     public IRubyObject module_exec(ThreadContext context, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             return yieldUnder(context, this, args, block);
         } else {
             throw context.getRuntime().newLocalJumpErrorNoBlock();
         }
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
                 insertAboveClass.getSuperClass().replaceSubclass(insertAboveClass, wrapper);
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
         return removeClassVariable(name.asJavaString());
     }
 
     /** rb_mod_class_variables
      *
      */
     @JRubyMethod(name = "class_variables", compat = RUBY1_8)
     public RubyArray class_variables(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         RubyArray ary = runtime.newArray();
         
         Collection<String> names = classVariablesCommon();
         ary.addAll(names);
         return ary;
     }
 
     @JRubyMethod(name = "class_variables", compat = RUBY1_9)
     public RubyArray class_variables19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         RubyArray ary = runtime.newArray();
         
         Collection<String> names = classVariablesCommon();
         for (String name : names) {
             ary.add(runtime.newSymbol(name));
         }
         return ary;
     }
 
     private Collection<String> classVariablesCommon() {
         Set<String> names = new HashSet<String>();
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             names.addAll(p.getClassVariableNameList());
         }
         return names;
     }
 
 
     //
     ////////////////// CONSTANT RUBY METHODS ////////////////
     //
 
     /** rb_mod_const_defined
      *
      */
     @JRubyMethod(name = "const_defined?", required = 1, compat = RUBY1_8)
     public RubyBoolean const_defined_p(ThreadContext context, IRubyObject symbol) {
         // Note: includes part of fix for JRUBY-1339
         return context.getRuntime().newBoolean(fastIsConstantDefined(validateConstant(symbol.asJavaString()).intern()));
     }
 
     @JRubyMethod(name = "const_defined?", required = 1, optional = 1, compat = RUBY1_9)
     public RubyBoolean const_defined_p19(ThreadContext context, IRubyObject[] args) {
         IRubyObject symbol = args[0];
         boolean inherit = args.length == 1 || (!args[1].isNil() && args[1].isTrue());
 
         return context.getRuntime().newBoolean(fastIsConstantDefined19(validateConstant(symbol.asJavaString()).intern(), inherit));
     }
 
     /** rb_mod_const_get
      *
      */
     @JRubyMethod(name = "const_get", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject const_get(IRubyObject symbol) {
         return getConstant(validateConstant(symbol.asJavaString()));
     }
 
     @JRubyMethod(name = "const_get", required = 1, optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject const_get(ThreadContext context, IRubyObject[] args) {
         IRubyObject symbol = args[0];
         boolean inherit = args.length == 1 || (!args[1].isNil() && args[1].isTrue());
 
         return getConstant(validateConstant(symbol.asJavaString()), inherit);
     }
