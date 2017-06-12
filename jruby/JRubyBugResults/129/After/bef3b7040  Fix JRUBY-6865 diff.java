diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index a236fac9fe..ad2154a206 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -349,2000 +349,2005 @@ public class RubyModule extends RubyObject {
         return this.methods;
     }
 
     public synchronized Map<String, DynamicMethod> getMethodsForWrite() {
         Map<String, DynamicMethod> myMethods = this.methods;
         return myMethods == Collections.EMPTY_MAP ?
             this.methods = new ConcurrentHashMap<String, DynamicMethod>(0, 0.9f, 1) :
             myMethods;
     }
     
     // note that addMethod now does its own put, so any change made to
     // functionality here should be made there as well 
     private void putMethod(String name, DynamicMethod method) {
         getMethodsForWrite().put(name, method);
 
         getRuntime().addProfiledMethod(name, method);
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
 
     /**
      * Get the base name of this class, or null if it is an anonymous class.
      * 
      * @return base name of the class
      */
     public String getBaseName() {
         return baseName;
     }
 
     /**
      * Set the base name of the class. If null, the class effectively becomes
      * anonymous (though constants elsewhere may reference it).
      * @param name the new base name of the class
      */
     public void setBaseName(String name) {
         baseName = name;
     }
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (cachedName != null) return cachedName;
         return calculateName();
     }
     
     /**
      * Get the "simple" name for the class, which is either the "base" name or
      * the "anonymous" class name.
      * 
      * @return the "simple" name of the class
      */
     public String getSimpleName() {
         if (baseName != null) return baseName;
         return calculateAnonymousName();
     }
 
     /**
      * Recalculate the fully-qualified name of this class/module.
      */
     private String calculateName() {
         boolean cache = true;
 
         if (getBaseName() == null) {
             // we are anonymous, use anonymous name
             return calculateAnonymousName();
         }
         
         Ruby runtime = getRuntime();
         
         String name = getBaseName();
         RubyClass objectClass = runtime.getObject();
         
         // First, we count the parents
         int parentCount = 0;
         for (RubyModule p = getParent() ; p != null && p != objectClass ; p = p.getParent()) {
             parentCount++;
         }
         
         // Allocate a String array for all of their names and populate it
         String[] parentNames = new String[parentCount];
         int i = parentCount - 1;
         int totalLength = name.length() + parentCount * 2; // name length + enough :: for all parents
         for (RubyModule p = getParent() ; p != null && p != objectClass ; p = p.getParent(), i--) {
             String pName = p.getBaseName();
             
             // This is needed when the enclosing class or module is a singleton.
             // In that case, we generated a name such as null::Foo, which broke 
             // Marshalling, among others. The correct thing to do in this situation 
             // is to insert the generate the name of form #<Class:01xasdfasd> if 
             // it's a singleton module/class, which this code accomplishes.
             if(pName == null) {
                 cache = false;
                 pName = p.getName();
              }
             
             parentNames[i] = pName;
             totalLength += pName.length();
         }
         
         // Then build from the front using a StringBuilder
         StringBuilder builder = new StringBuilder(totalLength);
         for (String parentName : parentNames) {
             builder.append(parentName).append("::");
         }
         builder.append(name);
         
         String fullName = builder.toString();
 
         if (cache) cachedName = fullName;
 
         return fullName;
     }
 
     private String calculateAnonymousName() {
         if (anonymousName == null) {
             // anonymous classes get the #<Class:0xdeadbeef> format
             StringBuilder anonBase = new StringBuilder(isClass() ? "#<Class:0x" : "#<Module:0x");
             anonBase.append(Integer.toHexString(System.identityHashCode(this))).append('>');
             anonymousName = anonBase.toString();
         }
         return anonymousName;
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     @Deprecated
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
 
     @Deprecated
     public RubyClass fastGetClass(String internedName) {
         return getClass(internedName);
     }
 
     /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
 
         testFrozen("module");
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
 
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         checkForCyclicInclude(module);
 
         infectBy(module);
 
         doIncludeModule(module);
         invalidateCoreClasses();
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
             this.setConstant(name, realVal);
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
         Map<String, List<JavaMethodDescriptor>> annotatedMethods2_0 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods2_0 = new HashMap<String, List<JavaMethodDescriptor>>();
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
                     } else if (anno.compat() == RUBY2_0) {
                         methodsHash = staticAnnotatedMethods2_0;
                     } else {
                         methodsHash = staticAnnotatedMethods;
                     }
                 } else {
                     if (anno.compat() == RUBY1_8) {
                         methodsHash = annotatedMethods1_8;
                     } else if (anno.compat() == RUBY1_9) {
                         methodsHash = annotatedMethods1_9;
                     } else if (anno.compat() == RUBY2_0) {
                         methodsHash = annotatedMethods2_0;
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
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods2_0() {
             return annotatedMethods2_0;
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
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods2_0() {
             return staticAnnotatedMethods2_0;
         }
     }
     
     public void defineAnnotatedMethodsIndividually(Class clazz) {
         TypePopulator populator;
         
         if (RubyInstanceConfig.FULL_TRACE_ENABLED || RubyInstanceConfig.REFLECTED_HANDLES) {
             // we want reflected invokers or need full traces, use default (slow) populator
             if (DEBUG) LOG.debug("trace mode, using default populator");
             populator = TypePopulator.DEFAULT;
         } else {
             try {
                 String qualifiedName = "org.jruby.gen." + clazz.getCanonicalName().replace('.', '$');
 
                 if (DEBUG) LOG.debug("looking for " + qualifiedName + AnnotationBinder.POPULATOR_SUFFIX);
 
                 Class populatorClass = Class.forName(qualifiedName + AnnotationBinder.POPULATOR_SUFFIX);
                 populator = (TypePopulator)populatorClass.newInstance();
             } catch (Throwable t) {
                 if (DEBUG) LOG.debug("Could not find it, using default populator");
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
         Ruby runtime = context.runtime;
 
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
         if (!arg.isModule()) {
             throw context.runtime.newTypeError(arg, context.runtime.getModule());
         }
         RubyModule moduleToCompare = (RubyModule) arg;
 
         // See if module is in chain...Cannot match against itself so start at superClass.
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isSame(moduleToCompare)) {
                 return context.runtime.getTrue();
             }
         }
 
         return context.runtime.getFalse();
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         Ruby runtime = getRuntime();
 
         testFrozen("class/module");
 
         addMethodInternal(name, method);
     }
 
     public void addMethodInternal(String name, DynamicMethod method) {
         synchronized(getMethodsForWrite()) {
             addMethodAtBootTimeOnly(name, method);
             invalidateCoreClasses();
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
         Ruby runtime = context.runtime;
 
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethodsForWrite()) {
             DynamicMethod method = (DynamicMethod) getMethodsForWrite().remove(name);
             if (method == null) {
                 throw runtime.newNameError("method '" + name + "' not defined in " + getName(), name);
             }
 
             invalidateCoreClasses();
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
         int token = getGeneration();
         DynamicMethod method = searchMethodInner(name);
 
         if (method instanceof CacheableMethod) {
             method = ((CacheableMethod) method).getMethodForCaching();
         }
 
         return method != null ? addToCache(name, method, token) : addToCache(name, UndefinedMethod.getInstance(), token);
     }
     
     @Deprecated
     public final int getCacheToken() {
         return generation;
     }
     
     public final int getGeneration() {
         return generation;
     }
 
     public final Integer getGenerationObject() {
         return generationObject;
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
             if (cacheEntry.token == getGeneration()) {
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
     
     public DynamicMethod searchMethodInner(String name) {
         DynamicMethod method = getMethods().get(name);
         
         if (method != null) return method;
         
         return superClass == null ? null : superClass.searchMethodInner(name);
     }
 
     public void invalidateCacheDescendants() {
         if (DEBUG) LOG.debug("invalidating descendants: {}", baseName);
 
         if (includingHierarchies.isEmpty()) {
             // it's only us; just invalidate directly
             methodInvalidator.invalidate();
             return;
         }
 
         List<Invalidator> invalidators = new ArrayList();
         invalidators.add(methodInvalidator);
         
         synchronized (getRuntime().getHierarchyLock()) {
             for (RubyClass includingHierarchy : includingHierarchies) {
                 includingHierarchy.addInvalidatorsAndFlush(invalidators);
             }
         }
         
         methodInvalidator.invalidateAll(invalidators);
     }
     
     protected void invalidateCoreClasses() {
         if (!getRuntime().isBooting()) {
             if (this == getRuntime().getFixnum()) {
                 getRuntime().setFixnumReopened(true);
             } else if (this == getRuntime().getFloat()) {
                 getRuntime().setFloatReopened(true);
             }
         }
     }
     
     public Invalidator getInvalidator() {
         return methodInvalidator;
     }
     
     public void updateGeneration() {
         generationObject = generation = getRuntime().getNextModuleGeneration();
     }
 
     @Deprecated
     protected void invalidateCacheDescendantsInner() {
         methodInvalidator.invalidate();
     }
     
     protected void invalidateConstantCache() {
         getRuntime().getConstantInvalidator().invalidate();
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
 
         putMethod(name, new AliasMethod(this, method, oldName));
 
         invalidateCoreClasses();
         invalidateCacheDescendants();
     }
 
     public synchronized void defineAliases(List<String> aliases, String oldName) {
         testFrozen("module");
         Ruby runtime = getRuntime();
 
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
         invalidateCoreClasses();
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
 
         final Ruby runtime = context.runtime;
 
         if (visibility == PRIVATE) {
             //FIXME warning
         } else if (visibility == MODULE_FUNCTION) {
             visibility = PRIVATE;
             // FIXME warning
         }
+
+        if (!(IdUtil.isLocal(internedName) || IdUtil.isConstant(internedName))) {
+            throw runtime.newNameError("invalid attribute name", internedName);
+        }
+
         final String variableName = ("@" + internedName).intern();
         if (readable) {
             addMethod(internedName, new AttrReaderMethod(this, visibility, CallConfiguration.FrameNoneScopeNone, variableName));
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
         if (writeable) {
             internedName = (internedName + "=").intern();
             addMethod(internedName, new AttrWriterMethod(this, visibility, CallConfiguration.FrameNoneScopeNone, variableName));
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
     }
 
     /** set_method_visibility
      *
      */
     public void setMethodVisibility(IRubyObject[] methods, Visibility visibility) {
         for (int i = 0; i < methods.length; i++) {
             exportMethod(methods[i].asJavaString(), visibility);
         }
     }
 
     /** rb_export_method
      *
      */
     public void exportMethod(String name, Visibility visibility) {
         Ruby runtime = getRuntime();
 
         DynamicMethod method = deepMethodSearch(name, runtime);
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 // FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
                 addMethod(name, new WrapperMethod(this, method, visibility));
             }
 
             invalidateCoreClasses();
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
     
     public boolean isMethodBound(String name, boolean checkVisibility, boolean checkRespondTo) {
         if (!checkRespondTo) return isMethodBound(name, checkVisibility);
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined() && !method.isNotImplemented()) {
             return !(checkVisibility && method.getVisibility() == PRIVATE);
         }
         return false;
     }
 
     public void checkMethodBound(ThreadContext context, IRubyObject[] args, Visibility visibility) {
         if (args.length == 0) {
             throw context.runtime.newArgumentError("no method name given");
         }
         String name = args[0].asJavaString();
 
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined() && method.getVisibility() != visibility) {
             Ruby runtime = context.runtime;
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
         Ruby runtime = context.runtime;
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = PUBLIC;
 
         RubyProc proc = runtime.newProc(Block.Type.LAMBDA, block);
 
         // a normal block passed to define_method changes to do arity checking; make it a lambda
         proc.getBlock().type = Block.Type.LAMBDA;
         
         newMethod = createProcMethod(name, visibility, proc);
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, visibility, context, runtime);
 
         return proc;
     }
     
     @JRubyMethod(name = "define_method", visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         Ruby runtime = context.runtime;
         IRubyObject body;
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = PUBLIC;
 
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
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, visibility, context, runtime);
 
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
             throw context.runtime.newArgumentError("wrong number of arguments (" + args.length + " for 2)");
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
         RubyArray ary = context.runtime.newArray();
 
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
         return context.runtime.newArray(getAncestorList());
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
         return context.runtime.newBoolean(isInstance(obj));
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
     public IRubyObject initialize(ThreadContext context, Block block) {
         if (block.isGiven()) {
             module_exec(context, new IRubyObject[] {this}, block);
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
         Ruby runtime = context.runtime;
 
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
 
         return context.runtime.getNil();
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
 
         return context.runtime.getNil();
     }
 
 
     @Deprecated
     public IRubyObject attr_accessor(IRubyObject[] args) {
         return attr_accessor(getRuntime().getCurrentContext(), args);
     }
 
     /** rb_mod_attr_accessor
      *  Note: this method should not be called from Java in most cases, since
      *  it depends on Ruby frame state for visibility. Use add[Read/Write]Attribute instead.
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
 
         return context.runtime.getNil();
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
             if (!obj.isModule()) {
                 throw context.runtime.newTypeError(obj, context.runtime.getModule());
             }
         }
         for (int i = modules.length - 1; i >= 0; i--) {
             modules[i].callMethod(context, "append_features", this);
             modules[i].callMethod(context, "included", this);
         }
 
         return this;
     }
 
     @JRubyMethod(name = "included", required = 1, visibility = PRIVATE)
     public IRubyObject included(ThreadContext context, IRubyObject other) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "extended", required = 1, frame = true, visibility = PRIVATE)
     public IRubyObject extended(ThreadContext context, IRubyObject other, Block block) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "mix", visibility = PRIVATE, compat = RUBY2_0)
     public IRubyObject mix(ThreadContext context, IRubyObject mod) {
         Ruby runtime = context.runtime;
 
         if (!mod.isModule()) {
             throw runtime.newTypeError(mod, runtime.getModule());
         }
 
         for (Map.Entry<String, DynamicMethod> entry : ((RubyModule)mod).methods.entrySet()) {
             if (methods.containsKey(entry.getKey())) {
                 throw runtime.newArgumentError("method would conflict - " + entry.getKey());
             }
         }
 
         for (Map.Entry<String, DynamicMethod> entry : ((RubyModule)mod).methods.entrySet()) {
             getMethodsForWrite().put(entry.getKey(), entry.getValue().dup());
         }
 
         return mod;
     }
 
     @JRubyMethod(name = "mix", visibility = PRIVATE, compat = RUBY2_0)
     public IRubyObject mix(ThreadContext context, IRubyObject mod, IRubyObject hash0) {
         Ruby runtime = context.runtime;
         RubyHash methodNames = null;
 
         if (!mod.isModule()) {
             throw runtime.newTypeError(mod, runtime.getModule());
         }
 
         if (hash0 instanceof RubyHash) {
             methodNames = (RubyHash)hash0;
         } else {
             throw runtime.newTypeError(hash0, runtime.getHash());
         }
         
         for (Map.Entry entry : (Set<Map.Entry<Object, Object>>)methodNames.directEntrySet()) {
             String name = entry.getValue().toString();
             if (methods.containsKey(entry.getValue().toString())) {
                 throw runtime.newArgumentError("constant would conflict - " + name);
             }
         }
 
         for (Map.Entry<String, DynamicMethod> entry : ((RubyModule)mod).methods.entrySet()) {
             if (methods.containsKey(entry.getKey())) {
                 throw runtime.newArgumentError("method would conflict - " + entry.getKey());
             }
         }
 
         for (Map.Entry<String, DynamicMethod> entry : ((RubyModule)mod).methods.entrySet()) {
             String name = entry.getKey();
             IRubyObject mapped = methodNames.fastARef(runtime.newSymbol(name));
             if (mapped == NEVER) {
                 // unmapped
             } else if (mapped == context.nil) {
                 // do not mix
                 continue;
             } else {
                 name = mapped.toString();
             }
             getMethodsForWrite().put(name, entry.getValue().dup());
         }
 
         return mod;
     }
 
     private void setVisibility(ThreadContext context, IRubyObject[] args, Visibility visibility) {
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
         Ruby runtime = context.runtime;
 
         if (args.length == 0) {
             context.setCurrentVisibility(MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, PRIVATE);
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asJavaString().intern();
                 DynamicMethod method = deepMethodSearch(name, runtime);
                 getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), method, PUBLIC));
                 callMethod(context, "singleton_method_added", context.runtime.fastNewSymbol(name));
             }
         }
         return this;
     }
 
     @JRubyMethod(name = "method_added", required = 1, visibility = PRIVATE)
     public IRubyObject method_added(ThreadContext context, IRubyObject nothing) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "method_removed", required = 1, visibility = PRIVATE)
     public IRubyObject method_removed(ThreadContext context, IRubyObject nothing) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "method_undefined", required = 1, visibility = PRIVATE)
     public IRubyObject method_undefined(ThreadContext context, IRubyObject nothing) {
         return context.runtime.getNil();
     }
     
     @JRubyMethod(name = "method_defined?", required = 1)
     public RubyBoolean method_defined_p(ThreadContext context, IRubyObject symbol) {
         return isMethodBound(symbol.asJavaString(), true) ? context.runtime.getTrue() : context.runtime.getFalse();
     }
 
     @JRubyMethod(name = "public_method_defined?", required = 1)
     public IRubyObject public_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
 
         return context.runtime.newBoolean(!method.isUndefined() && method.getVisibility() == PUBLIC);
     }
 
     @JRubyMethod(name = "protected_method_defined?", required = 1)
     public IRubyObject protected_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
 
         return context.runtime.newBoolean(!method.isUndefined() && method.getVisibility() == PROTECTED);
     }
 	
     @JRubyMethod(name = "private_method_defined?", required = 1)
     public IRubyObject private_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
 
         return context.runtime.newBoolean(!method.isUndefined() && method.getVisibility() == PRIVATE);
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
             context.runtime.newSymbol(newName);
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
 
     @JRubyMethod(name = {"module_eval", "class_eval"})
     public IRubyObject module_eval(ThreadContext context, Block block) {
         return specificEval(context, this, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"})
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, Block block) {
         return specificEval(context, this, arg0, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"})
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return specificEval(context, this, arg0, arg1, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"})
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return specificEval(context, this, arg0, arg1, arg2, block);
     }
     @Deprecated
     public IRubyObject module_eval(ThreadContext context, IRubyObject[] args, Block block) {
         return specificEval(context, this, args, block);
     }
 
     @JRubyMethod(name = {"module_exec", "class_exec"})
     public IRubyObject module_exec(ThreadContext context, Block block) {
         if (block.isGiven()) {
             return yieldUnder(context, this, IRubyObject.NULL_ARRAY, block);
         } else {
             throw context.runtime.newLocalJumpErrorNoBlock();
diff --git a/src/org/jruby/util/IdUtil.java b/src/org/jruby/util/IdUtil.java
index 88b83c7184..884493d744 100644
--- a/src/org/jruby/util/IdUtil.java
+++ b/src/org/jruby/util/IdUtil.java
@@ -1,166 +1,170 @@
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
 
 public final class IdUtil {
     /**
      * rb_is_const_id and is_const_id
      */    
 	public static boolean isConstant(String id) {
 	    return Character.isUpperCase(id.charAt(0));
     }
 
     /**
      * rb_is_class_id and is_class_id
      */    
 	public static boolean isClassVariable(String id) {
 	    return id.length()>1 && id.charAt(0) == '@' && id.charAt(1) == '@';
     }
 
     /**
      * rb_is_instance_id and is_instance_id
      */    
 	public static boolean isInstanceVariable(String id) {
 	    return id.length()>0 && id.charAt(0) == '@' && (id.length() < 2 || id.charAt(1) != '@');
     }
     
     /**
      * rb_is_global_id and is_global_id
      */    
     public static boolean isGlobal(String id) {
         return id.length()>0 && id.charAt(0) == '$';
     }
+
+    public static boolean isPredicate(String id) {
+        return id.endsWith("?");
+    }
     
     /**
      * rb_is_local_id and is_local_id
      */    
 	public static boolean isLocal(String id) {
-	    return !isGlobal(id) && !isClassVariable(id) && !isInstanceVariable(id) && !isConstant(id);
+	    return !isGlobal(id) && !isClassVariable(id) && !isInstanceVariable(id) && !isConstant(id) && !isPredicate(id);
     }
 
 	public static boolean isAttrSet(String id) {
 	    return id.endsWith("=");
 	}
 
     public static boolean isValidConstantName(String id) {
         char c;
         int len;
         if ((len = id.length()) > 0 && (c = id.charAt(0)) <= 'Z' && c >= 'A') {
             return isNameString(id, 1, len);
         }
         return false;
     }
 
     public static boolean isValidConstantName19(String id) {
         char c;
         int len;
         if ((len = id.length()) > 0 && (c = id.charAt(0)) <= 'Z' && c >= 'A') {
             return isNameString19(id, 1, len);
         }
         return false;
     }
     
     // Pickaxe says @ must be followed by a name character, but MRI
     // does not require this.
     public static boolean isValidInstanceVariableName(String id) {
         int len;
         if ((len = id.length()) > 0 && '@' == id.charAt(0)) {
             if (len > 1) {
                 if (isInitialCharacter(id.charAt(1))) {
                     return isNameString(id, 2, len);
                 }
                 return false;
             }
             return true;
         }
         return false;
     }
     
     // Pickaxe says @@ must be followed by a name character, but MRI
     // does not require this.
     public static boolean isValidClassVariableName(String id) {
         int len;
         if ((len = id.length()) > 1 && '@' == id.charAt(0) && '@' == id.charAt(1)) {
             if (len > 2) {
                 if (isInitialCharacter(id.charAt(2))) {
                     return isNameString(id, 3, len);
                 }
                 return false;
             }
             return true;
         }
         return false;
     }
     
     public static boolean isInitialCharacter(int c) {
         return ((c &= ~0x20) <= 'Z' && c >= 'A') || c == '_';
     }
     
     public static boolean isNameCharacter(char c) {
         int letter;
         return ((letter = c & ~0x20) <= 'Z' && letter >= 'A') ||
             c == '_' ||
             (c <= '9' && c >= '0');
     }
 
     public static boolean isNameCharacter19(char c) {
         int letter;
         return Character.isLetterOrDigit(c) || c == '_';
     }
     
     public static boolean isNameString(String id, int start, int limit) {
         for (int i = start; i < limit; i++) {
             if (!isNameCharacter(id.charAt(i))) {
                 return false;
             }
         }
         return true;
     }
 
     public static boolean isNameString19(String id, int start, int limit) {
         for (int i = start; i < limit; i++) {
             if (!isNameCharacter19(id.charAt(i))) {
                 return false;
             }
         }
         return true;
     }
 
     /**
      * Check the syntax of a Ruby variable, including that it's longer
      * than zero characters, and starts with either an @ or a capital
      * letter.
      */
     // FIXME: this should go somewhere more generic -- maybe IdUtil
     public static final boolean isRubyVariable(String name) {
         char c;
         return name.length() > 0 && ((c = name.charAt(0)) == '@' || (c <= 'Z' && c >= 'A'));
     }
 }
