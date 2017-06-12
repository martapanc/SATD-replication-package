diff --git a/src/org/jruby/RubyClass.java b/src/org/jruby/RubyClass.java
index 1c1c8ed08a..6edd4974a0 100644
--- a/src/org/jruby/RubyClass.java
+++ b/src/org/jruby/RubyClass.java
@@ -134,1627 +134,1674 @@ public class RubyClass extends RubyModule {
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
         private String name;
         private int index;
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
 
     public VariableAccessor getVariableAccessorForRead(String name) {
         VariableAccessor accessor = getVariableAccessorsForRead().get(name);
         if (accessor == null) accessor = VariableAccessor.DUMMY_ACCESSOR;
         return accessor;
     }
 
     public synchronized VariableAccessor getObjectIdAccessorForWrite() {
         if (objectIdAccessor == VariableAccessor.DUMMY_ACCESSOR) objectIdAccessor = allocateVariableAccessor("object_id");
         return objectIdAccessor;
     }
 
     public VariableAccessor getObjectIdAccessorForRead() {
         return objectIdAccessor;
     }
 
     public int getVariableTableSize() {
         return variableAccessors.size();
     }
 
     public int getVariableTableSizeWithObjectId() {
         return variableAccessors.size() + (objectIdAccessor == VariableAccessor.DUMMY_ACCESSOR ? 0 : 1);
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
         setSuperClass(superClass); // this is the only case it might be null here (in MetaClass construction)
     }
     
     /** used by CLASS_ALLOCATOR (any Class' class will be a Class!)
      *  also used to bootstrap Object class
      */
     protected RubyClass(Ruby runtime) {
         super(runtime, runtime.getClassClass());
         this.runtime = runtime;
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
             if(methodMissing.isUndefined() || methodMissing == context.getRuntime().getDefaultMethodMissing()) {
                 return null;
             }
 
             try {
                 return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, Block.NULL_BLOCK);
             } catch(RaiseException e) {
                 if(context.getRuntime().getNoMethodError().isInstance(e.getException())) {
                     if(self.respondsTo(name)) {
                         throw e;
                     } else {
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
     public IRubyObject newInstance(ThreadContext context, IRubyObject[] args, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, this, obj, args, block);
         return obj;
     }
     
     public static class SpecificArityNew extends JavaMethod {
         public SpecificArityNew(RubyModule implClass, Visibility visibility) {
             super(implClass, visibility);
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, args, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, arg1, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, arg1, arg2, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
     }
 
     /** rb_class_initialize
      * 
      */
     @JRubyMethod(compat = RUBY1_8, visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(runtime.getObject(), block, false);
     }
         
     @JRubyMethod(compat = RUBY1_8, visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon((RubyClass)superObject, block, false);
     }
         
     @JRubyMethod(name = "initialize", compat = RUBY1_9, visibility = PRIVATE)
     public IRubyObject initialize19(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(runtime.getObject(), block, true);
     }
         
     @JRubyMethod(name = "initialize", compat = RUBY1_9, visibility = PRIVATE)
     public IRubyObject initialize19(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon((RubyClass)superObject, block, true);
     }
 
     private IRubyObject initializeCommon(RubyClass superClazz, Block block, boolean callInheritBeforeSuper) {
         setSuperClass(superClazz);
         allocator = superClazz.allocator;
         makeMetaClass(superClazz.getMetaClass());
 
         marshal = superClazz.marshal;
 
         superClazz.addSubclass(this);
 
         if (callInheritBeforeSuper) {
             inherit(superClazz);
             super.initialize(block);
         } else {
             super.initialize(block);
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
         // update all subclasses
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.invalidateCacheDescendants();
             }
         }
     }
     
     public Ruby getClassRuntime() {
         return runtime;
     }
 
     public RubyClass getRealClass() {
         return this;
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
             if (metaClass == runtime.getBasicObject().getMetaClass()) return runtime.getNil();
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
 
+    /**
+     * Whether this class can be reified into a Java class. Currently only objects
+     * that descend from Object (or Ruby-based classes that descend from Object)
+     * can be reified.
+     *
+     * @return true if the class can be reified, false otherwise
+     */
+    public boolean isReifiable() {
+        // this needs to be a better check, to avoid attempting to reify subclasses of Hash, etc.
+        return superClass.getRealClass() != null &&
+                (superClass.getRealClass().getReifiedClass() == null || superClass.getRealClass() == runtime.getObject());
+    }
+
+    /**
+     * Reify this class, first reifying all its ancestors. This causes the
+     * reified class and all ancestors' reified classes to come into existence,
+     * so any future changes will not be reflected.
+     */
+    public void reifyWithAncestors() {
+        if (isReifiable()) {
+            getSuperClass().getRealClass().reifyWithAncestors();
+            reify();
+        }
+    }
+
+    /**
+     * Reify this class, first reifying all its ancestors. This causes the
+     * reified class and all ancestors' reified classes to come into existence,
+     * so any future changes will not be reflected.
+     *
+     * This form also accepts a string argument indicating a path in which to dump
+     * the intermediate reified class bytes.
+     *
+     * @param classDumpDir the path in which to dump reified class bytes
+     */
+    public void reifyWithAncestors(String classDumpDir) {
+        if (isReifiable()) {
+            getSuperClass().getRealClass().reifyWithAncestors(classDumpDir);
+            reify(classDumpDir);
+        }
+    }
+
     public synchronized void reify() {
         reify(null);
     }
 
     private static final boolean DEBUG_REIFY = false;
 
     /**
      * Stand up a real Java class for the backing store of this object
      * @param classDumpDir Directory to save reified java class
      */
     public synchronized void reify(String classDumpDir) {
         Class reifiedParent = RubyObject.class;
 
         // calculate an appropriate name, using "Anonymous####" if none is present
         String name;
         if (getBaseName() == null) {
             name = "AnonymousRubyClass#" + id;
         } else {
             name = getName();
         }
         
         String javaName = "rubyobj." + name.replaceAll("::", ".");
         String javaPath = "rubyobj/" + name.replaceAll("::", "/");
         OneShotClassLoader parentCL;
-        if (superClass.getRealClass().getReifiedClass().getClassLoader() instanceof OneShotClassLoader) {
+        Class parentReified = superClass.getRealClass().getReifiedClass();
+        if (parentReified == null) {
+            throw getClassRuntime().newTypeError("class " + getName() + " parent class is not yet reified");
+        }
+        
+        if (parentReified.getClassLoader() instanceof OneShotClassLoader) {
             parentCL = (OneShotClassLoader)superClass.getRealClass().getReifiedClass().getClassLoader();
         } else {
             parentCL = new OneShotClassLoader(runtime.getJRubyClassLoader());
         }
 
         if (superClass.reifiedClass != null) {
             reifiedParent = superClass.reifiedClass;
         }
 
         Class[] interfaces = Java.getInterfacesFromRubyClass(this);
         String[] interfaceNames = new String[interfaces.length];
         for (int i = 0; i < interfaces.length; i++) {
             interfaceNames[i] = p(interfaces[i]);
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
         
         for (Map.Entry<String,DynamicMethod> methodEntry : getMethods().entrySet()) {
             String methodName = methodEntry.getKey();
             String javaMethodName = JavaNameMangler.mangleStringForCleanJavaIdentifier(methodName);
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
 
             if (DEBUG_REIFY) System.out.println("defining " + getName() + "#" + methodName + " as " + javaName + "#" + javaMethodName + signature);
 
             instanceMethods.add(javaMethodName + signature);
 
             m.end();
         }
         
         for (Map.Entry<String,DynamicMethod> methodEntry : getMetaClass().getMethods().entrySet()) {
             String methodName = methodEntry.getKey();
             String javaMethodName = JavaNameMangler.mangleStringForCleanJavaIdentifier(methodName);
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
 
             if (DEBUG_REIFY) System.out.println("defining " + getName() + "." + methodName + " as " + javaName + "." + javaMethodName + signature);
 
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
                 System.err.println("failed to reify class " + getName() + " due to:\n");
                 e.printStackTrace(System.err);
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
             if (method != runtime.getRespondToMethod() && !method.isUndefined()) {
 
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
             if (method != runtime.getRespondToMethod() && !method.isUndefined()) {
 
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
             if (method != runtime.getRespondToMethod() && !method.isUndefined()) {
 
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
 }
diff --git a/src/org/jruby/RubyJRuby.java b/src/org/jruby/RubyJRuby.java
index d5a238a940..51d35ba522 100644
--- a/src/org/jruby/RubyJRuby.java
+++ b/src/org/jruby/RubyJRuby.java
@@ -1,708 +1,708 @@
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
  * Copyright (C) 2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2009 MenTaLguY <mental@rydia.net>
  * Copyright (C) 2010 Charles Oliver Nutter <headius@headius.com>
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
 
 import org.jruby.ast.RestArgNode;
 import org.jruby.ext.jruby.JRubyUtilLibrary;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.lang.management.ManagementFactory;
 import java.lang.management.ThreadMXBean;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.anno.JRubyClass;
 
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ListNode;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 
 import org.jruby.ast.Node;
 import org.jruby.ast.types.INameNode;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.internal.runtime.methods.MethodArgs;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.InterpretedBlock;
 import org.jruby.runtime.ThreadContext;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.util.TraceClassVisitor;
 
 import java.util.Map;
 import org.jruby.ast.MultipleAsgn19Node;
 import org.jruby.ast.UnnamedRestArgNode;
 import org.jruby.internal.runtime.methods.MethodArgs2;
 import org.jruby.java.proxies.JavaProxy;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.ExecutionContext;
 import org.jruby.runtime.ObjectAllocator;
 import static org.jruby.runtime.Visibility.*;
 
 /**
  * Module which defines JRuby-specific methods for use. 
  */
 @JRubyModule(name="JRuby")
 public class RubyJRuby {
     public static RubyModule createJRuby(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         runtime.getKernel().callMethod(context, "require", runtime.newString("java"));
         RubyModule jrubyModule = runtime.defineModule("JRuby");
 
         jrubyModule.defineAnnotatedMethods(RubyJRuby.class);
         jrubyModule.defineAnnotatedMethods(JRubyUtilLibrary.class);
 
         RubyClass compiledScriptClass = jrubyModule.defineClassUnder("CompiledScript",runtime.getObject(), runtime.getObject().getAllocator());
 
         for (String name : new String[] {"name", "class_name", "original_script", "code"}) {
             compiledScriptClass.addReadWriteAttribute(context, name);
         }
         compiledScriptClass.defineAnnotatedMethods(JRubyCompiledScript.class);
 
         RubyClass threadLocalClass = jrubyModule.defineClassUnder("ThreadLocal", runtime.getObject(), JRubyThreadLocal.ALLOCATOR);
         threadLocalClass.defineAnnotatedMethods(JRubyExecutionContextLocal.class);
 
         RubyClass fiberLocalClass = jrubyModule.defineClassUnder("FiberLocal", runtime.getObject(), JRubyFiberLocal.ALLOCATOR);
         fiberLocalClass.defineAnnotatedMethods(JRubyExecutionContextLocal.class);
 
         return jrubyModule;
     }
 
     public static RubyModule createJRubyExt(Ruby runtime) {
         runtime.getKernel().callMethod(runtime.getCurrentContext(),"require", runtime.newString("java"));
         RubyModule mJRubyExt = runtime.getOrCreateModule("JRuby").defineModuleUnder("Extensions");
         
         mJRubyExt.defineAnnotatedMethods(JRubyExtensions.class);
 
         runtime.getObject().includeModule(mJRubyExt);
 
         return mJRubyExt;
     }
 
     public static void createJRubyCoreExt(Ruby runtime) {
         runtime.getClassClass().defineAnnotatedMethods(JRubyClassExtensions.class);
         runtime.getThread().defineAnnotatedMethods(JRubyThreadExtensions.class);
         runtime.getString().defineAnnotatedMethods(JRubyStringExtensions.class);
     }
 
     public static class JRubySynchronizedMeta {
         @JRubyMethod(visibility = PRIVATE)
         public static IRubyObject append_features(IRubyObject self, IRubyObject target) {
             if (target instanceof RubyClass && self instanceof RubyModule) { // should always be true
                 RubyClass targetModule = ((RubyClass)target);
                 targetModule.becomeSynchronized();
                 return ((RubyModule)self).append_features(target);
             }
             throw target.getRuntime().newTypeError(self + " can only be included into classes");
         }
 
         @JRubyMethod(visibility = PRIVATE)
         public static IRubyObject extend_object(IRubyObject self, IRubyObject obj) {
             if (self instanceof RubyModule) {
                 RubyClass singletonClass = obj.getSingletonClass();
                 singletonClass.becomeSynchronized();
                 return ((RubyModule)self).extend_object(obj);
             }
             // should never happen
             throw self.getRuntime().newTypeError("JRuby::Singleton.extend_object called against " + self);
         }
     }
 
     @JRubyMethod(module = true)
     public static IRubyObject runtime(IRubyObject recv, Block unusedBlock) {
         return JavaUtil.convertJavaToUsableRubyObject(recv.getRuntime(), recv.getRuntime());
     }
 
     @JRubyMethod(module = true)
     public static IRubyObject with_current_runtime_as_global(ThreadContext context, IRubyObject recv, Block block) {
         Ruby currentRuntime = context.getRuntime();
         Ruby globalRuntime = Ruby.getGlobalRuntime();
         try {
             if (globalRuntime != currentRuntime) {
                 currentRuntime.useAsGlobalRuntime();
             }
             block.yieldSpecific(context);
         } finally {
             if (Ruby.getGlobalRuntime() != globalRuntime) {
                 globalRuntime.useAsGlobalRuntime();
             }
         }
         return currentRuntime.getNil();
     }
 
     @JRubyMethod(name = {"parse", "ast_for"}, optional = 3, module = true)
     public static IRubyObject parse(IRubyObject recv, IRubyObject[] args, Block block) {
         if(block.isGiven()) {
             if(block.getBody() instanceof org.jruby.runtime.CompiledBlock) {
                 throw new RuntimeException("Cannot compile an already compiled block. Use -J-Djruby.jit.enabled=false to avoid this problem.");
             }
             Arity.checkArgumentCount(recv.getRuntime(),args,0,0);
             return JavaUtil.convertJavaToUsableRubyObject(recv.getRuntime(), ((InterpretedBlock)block.getBody()).getBodyNode());
         } else {
             Arity.checkArgumentCount(recv.getRuntime(),args,1,3);
             String filename = "-";
             boolean extraPositionInformation = false;
             RubyString content = args[0].convertToString();
             if(args.length>1) {
                 filename = args[1].convertToString().toString();
                 if(args.length>2) {
                     extraPositionInformation = args[2].isTrue();
                 }
             }
             return JavaUtil.convertJavaToUsableRubyObject(recv.getRuntime(),
                recv.getRuntime().parse(content.getByteList(), filename, null, 0, extraPositionInformation));
         }
     }
 
     @JRubyMethod(name = "compile", optional = 3, module = true)
     public static IRubyObject compile(IRubyObject recv, IRubyObject[] args, Block block) {
         Node node;
         String filename;
         RubyString content;
         if(block.isGiven()) {
             Arity.checkArgumentCount(recv.getRuntime(),args,0,0);
             if(block.getBody() instanceof org.jruby.runtime.CompiledBlock) {
                 throw new RuntimeException("Cannot compile an already compiled block. Use -J-Djruby.jit.enabled=false to avoid this problem.");
             }
             content = RubyString.newEmptyString(recv.getRuntime());
             Node bnode = ((InterpretedBlock)block.getBody()).getBodyNode();
             node = new org.jruby.ast.RootNode(bnode.getPosition(), block.getBinding().getDynamicScope(), bnode);
             filename = "__block_" + node.getPosition().getFile();
         } else {
             Arity.checkArgumentCount(recv.getRuntime(),args,1,3);
             filename = "-";
             boolean extraPositionInformation = false;
             content = args[0].convertToString();
             if(args.length>1) {
                 filename = args[1].convertToString().toString();
                 if(args.length>2) {
                     extraPositionInformation = args[2].isTrue();
                 }
             }
 
             node = recv.getRuntime().parse(content.getByteList(), filename, null, 0, extraPositionInformation);
         }
 
         String classname;
         if (filename.equals("-e")) {
             classname = "__dash_e__";
         } else {
             classname = filename.replace('\\', '/').replaceAll(".rb", "").replaceAll("-","_dash_");
         }
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(node);
             
         StandardASMCompiler asmCompiler = new StandardASMCompiler(classname, filename);
         ASTCompiler compiler = recv.getRuntime().getInstanceConfig().newCompiler();
         compiler.compileRoot(node, asmCompiler, inspector);
         byte[] bts = asmCompiler.getClassByteArray();
 
         IRubyObject compiledScript = ((RubyModule)recv).fastGetConstant("CompiledScript").callMethod(recv.getRuntime().getCurrentContext(),"new");
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "name=", recv.getRuntime().newString(filename));
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "class_name=", recv.getRuntime().newString(classname));
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "original_script=", content);
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "code=", JavaUtil.convertJavaToUsableRubyObject(recv.getRuntime(), bts));
 
         return compiledScript;
     }
 
     @JRubyMethod(name = "reference", required = 1, module = true)
     public static IRubyObject reference(ThreadContext context, IRubyObject recv, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
 
         return Java.getInstance(runtime, obj);
     }
 
     @JRubyMethod(name = "dereference", required = 1, module = true)
     public static IRubyObject dereference(ThreadContext context, IRubyObject recv, IRubyObject obj) {
         Object unwrapped;
 
         if (obj instanceof JavaProxy) {
             unwrapped = ((JavaProxy)obj).getObject();
         } else if (obj.dataGetStruct() instanceof JavaObject) {
             unwrapped = JavaUtil.unwrapJavaObject(obj);
         } else {
             throw context.getRuntime().newTypeError("got " + obj + ", expected wrapped Java object");
         }
 
         if (!(unwrapped instanceof IRubyObject)) {
             throw context.getRuntime().newTypeError("got " + obj + ", expected Java-wrapped Ruby object");
         }
 
         return (IRubyObject)unwrapped;
     }
 
     @JRubyClass(name="JRuby::CompiledScript")
     public static class JRubyCompiledScript {
         @JRubyMethod(name = "to_s")
         public static IRubyObject compiled_script_to_s(IRubyObject recv) {
             return recv.getInstanceVariables().fastGetInstanceVariable("@original_script");
         }
 
         @JRubyMethod(name = "inspect")
         public static IRubyObject compiled_script_inspect(IRubyObject recv) {
             return recv.getRuntime().newString("#<JRuby::CompiledScript " + recv.getInstanceVariables().fastGetInstanceVariable("@name") + ">");
         }
 
         @JRubyMethod(name = "inspect_bytecode")
         public static IRubyObject compiled_script_inspect_bytecode(IRubyObject recv) {
             StringWriter sw = new StringWriter();
             ClassReader cr = new ClassReader((byte[])recv.getInstanceVariables().fastGetInstanceVariable("@code").toJava(byte[].class));
             TraceClassVisitor cv = new TraceClassVisitor(new PrintWriter(sw));
             cr.accept(cv, ClassReader.SKIP_DEBUG);
             return recv.getRuntime().newString(sw.toString());
         }
     }
 
     public abstract static class JRubyExecutionContextLocal extends RubyObject {
         private IRubyObject default_value;
         private RubyProc default_proc;
 
         public JRubyExecutionContextLocal(Ruby runtime, RubyClass type) {
             super(runtime, type);
             default_value = runtime.getNil();
             default_proc = null;
         }
 
         @JRubyMethod(name="initialize", required=0, optional=1)
         public IRubyObject rubyInitialize(ThreadContext context, IRubyObject args[], Block block) {
             if (block.isGiven()) {
                 if (args.length != 0) {
                     throw context.getRuntime().newArgumentError("wrong number of arguments");
                 }
                 default_proc = block.getProcObject();
                 if (default_proc == null) {
                     default_proc = RubyProc.newProc(context.getRuntime(), block, block.type);
                 }
             } else {
                 if (args.length == 1) {
                     default_value = args[0];
                 } else if (args.length != 0) {
                     throw context.getRuntime().newArgumentError("wrong number of arguments");
                 }
             }
             return context.getRuntime().getNil();
         }
 
         @JRubyMethod(name="default", required=0)
         public IRubyObject getDefault(ThreadContext context) {
             return default_value;
         }
 
         @JRubyMethod(name="default_proc", required=0)
         public IRubyObject getDefaultProc(ThreadContext context) {
             if (default_proc != null) {
                 return default_proc;
             } else {
                 return context.getRuntime().getNil();
             }
         }
 
         private static final IRubyObject[] EMPTY_ARGS = new IRubyObject[]{};
 
         @JRubyMethod(name="value", required=0)
         public IRubyObject getValue(ThreadContext context) {
             final IRubyObject value;
             final Map<Object, IRubyObject> contextVariables;
             contextVariables = getContextVariables(context);
             value = contextVariables.get(this);
             if (value != null) {
                 return value;
             } else if (default_proc != null) {
                 // pre-set for the sake of terminating recursive calls
                 contextVariables.put(this, context.getRuntime().getNil());
 
                 final IRubyObject new_value;
                 new_value = default_proc.call(context, EMPTY_ARGS, null, Block.NULL_BLOCK);
                 contextVariables.put(this, new_value);
                 return new_value;
             } else {
                 return default_value;
             }
         }
 
         @JRubyMethod(name="value=", required=1)
         public IRubyObject setValue(ThreadContext context, IRubyObject value) {
             getContextVariables(context).put(this, value);
             return value;
         }
 
         protected final Map<Object, IRubyObject> getContextVariables(ThreadContext context) {
             return getExecutionContext(context).getContextVariables();
         }
 
         protected abstract ExecutionContext getExecutionContext(ThreadContext context);
     }
 
     @JRubyClass(name="JRuby::ThreadLocal")
     public final static class JRubyThreadLocal extends JRubyExecutionContextLocal {
         public static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass type) {
                 return new JRubyThreadLocal(runtime, type);
             }
         };
 
         public JRubyThreadLocal(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         protected final ExecutionContext getExecutionContext(ThreadContext context) {
             return context.getThread();
         }
     }
 
     @JRubyClass(name="JRuby::FiberLocal")
     public final static class JRubyFiberLocal extends JRubyExecutionContextLocal {
         public static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass type) {
                 return new JRubyFiberLocal(runtime, type);
             }
         };
 
         public JRubyFiberLocal(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         @JRubyMethod(name="with_value", required=1)
         public IRubyObject withValue(ThreadContext context, IRubyObject value, Block block) {
             final Map<Object, IRubyObject> contextVariables;
             contextVariables = getContextVariables(context);
             final IRubyObject old_value;
             old_value = contextVariables.get(this);
             contextVariables.put(this, value);
             try {
                 return block.yieldSpecific(context);
             } finally {
                 contextVariables.put(this, old_value);
             }
         }
 
         protected final ExecutionContext getExecutionContext(ThreadContext context) {
             final ExecutionContext fiber;
             fiber = context.getFiber();
             if (fiber != null) {
                 return fiber;
             } else {
                 /* root fiber */
                 return context.getThread();
             }
         }
     }
 
     @JRubyModule(name="JRubyExtensions")
     public static class JRubyExtensions {
         @JRubyMethod(name = "steal_method", required = 2, module = true)
         public static IRubyObject steal_method(IRubyObject recv, IRubyObject type, IRubyObject methodName) {
             RubyModule to_add = null;
             if(recv instanceof RubyModule) {
                 to_add = (RubyModule)recv;
             } else {
                 to_add = recv.getSingletonClass();
             }
             String name = methodName.toString();
             if(!(type instanceof RubyModule)) {
                 throw recv.getRuntime().newArgumentError("First argument must be a module/class");
             }
 
             DynamicMethod method = ((RubyModule)type).searchMethod(name);
             if(method == null || method.isUndefined()) {
                 throw recv.getRuntime().newArgumentError("No such method " + name + " on " + type);
             }
 
             to_add.addMethod(name, method);
             return recv.getRuntime().getNil();
         }
 
         @JRubyMethod(name = "steal_methods", required = 1, rest = true, module = true)
         public static IRubyObject steal_methods(IRubyObject recv, IRubyObject[] args) {
             IRubyObject type = args[0];
             for(int i=1;i<args.length;i++) {
                 steal_method(recv, type, args[i]);
             }
             return recv.getRuntime().getNil();
         }
     }
     
     public static class JRubyClassExtensions {
         // TODO: Someday, enable.
         @JRubyMethod(name = "subclasses", optional = 1)
         public static IRubyObject subclasses(ThreadContext context, IRubyObject maybeClass, IRubyObject[] args) {
             RubyClass clazz;
             if (maybeClass instanceof RubyClass) {
                 clazz = (RubyClass)maybeClass;
             } else {
                 throw context.getRuntime().newTypeError(maybeClass, context.getRuntime().getClassClass());
             }
 
             boolean recursive = false;
             if (args.length == 1) {
                 if (args[0] instanceof RubyBoolean) {
                     recursive = args[0].isTrue();
                 } else {
                     context.getRuntime().newTypeError(args[0], context.getRuntime().fastGetClass("Boolean"));
                 }
             }
 
             return RubyArray.newArray(context.getRuntime(), clazz.subclasses(recursive)).freeze(context);
         }
 
         @JRubyMethod(name = "become_java!", optional = 1)
         public static IRubyObject become_java_bang(ThreadContext context, IRubyObject maybeClass, IRubyObject[] args) {
             RubyClass clazz;
             if (maybeClass instanceof RubyClass) {
                 clazz = (RubyClass)maybeClass;
             } else {
                 throw context.getRuntime().newTypeError(maybeClass, context.getRuntime().getClassClass());
             }
 
             if (args.length > 0) {
-                clazz.reify(args[0].convertToString().asJavaString());
+                clazz.reifyWithAncestors(args[0].convertToString().asJavaString());
             } else {
-                clazz.reify();
+                clazz.reifyWithAncestors();
             }
 
             return JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), clazz.getReifiedClass());
         }
 
         @JRubyMethod
         public static IRubyObject java_class(ThreadContext context, IRubyObject maybeClass) {
             RubyClass clazz;
             if (maybeClass instanceof RubyClass) {
                 clazz = (RubyClass)maybeClass;
             } else {
                 throw context.getRuntime().newTypeError(maybeClass, context.getRuntime().getClassClass());
             }
 
             for (RubyClass current = clazz; current != null; current = current.getSuperClass()) {
                 if (current.getReifiedClass() != null) {
                     clazz = current;
                     break;
                 }
             }
 
             return JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), clazz.getReifiedClass());
         }
 
         @JRubyMethod
         public static IRubyObject add_method_annotation(ThreadContext context, IRubyObject maybeClass, IRubyObject methodName, IRubyObject annoMap) {
             RubyClass clazz = getRubyClass(maybeClass, context);
             String method = methodName.convertToString().asJavaString();
 
             Map<Class,Map<String,Object>> annos = (Map<Class,Map<String,Object>>)annoMap;
 
             for (Map.Entry<Class,Map<String,Object>> entry : annos.entrySet()) {
                 Map<String,Object> value = entry.getValue();
                 if (value == null) value = Collections.EMPTY_MAP;
                 clazz.addMethodAnnotation(method, getAnnoClass(context, entry.getKey()), value);
             }
 
             return context.getRuntime().getNil();
         }
 
         @JRubyMethod
         public static IRubyObject add_parameter_annotations(ThreadContext context, IRubyObject maybeClass, IRubyObject methodName, IRubyObject paramAnnoMaps) {
             RubyClass clazz = getRubyClass(maybeClass, context);
             String method = methodName.convertToString().asJavaString();
             List<Map<Class,Map<String,Object>>> annos = (List<Map<Class,Map<String,Object>>>) paramAnnoMaps;
 
             for (int i = annos.size() - 1; i >= 0; i--) {
                 Map<Class, Map<String, Object>> paramAnnos = annos.get(i);
                 for (Map.Entry<Class,Map<String,Object>> entry : paramAnnos.entrySet()) {
                     Map<String,Object> value = entry.getValue();
                     if (value == null) value = Collections.EMPTY_MAP;
                     clazz.addParameterAnnotation(method, i, getAnnoClass(context, entry.getKey()), value);
                 }
             }
             return context.getRuntime().getNil();
         }
 
         @JRubyMethod
         public static IRubyObject add_class_annotation(ThreadContext context, IRubyObject maybeClass, IRubyObject annoMap) {
             RubyClass clazz = getRubyClass(maybeClass, context);
             Map<Class,Map<String,Object>> annos = (Map<Class,Map<String,Object>>)annoMap;
 
             for (Map.Entry<Class,Map<String,Object>> entry : annos.entrySet()) {
                 Map<String,Object> value = entry.getValue();
                 if (value == null) value = Collections.EMPTY_MAP;
                 clazz.addClassAnnotation(getAnnoClass(context, entry.getKey()), value);
             }
 
             return context.getRuntime().getNil();
         }
 
         @JRubyMethod
         public static IRubyObject add_method_signature(ThreadContext context, IRubyObject maybeClass, IRubyObject methodName, IRubyObject clsList) {
             RubyClass clazz = getRubyClass(maybeClass, context);
             List<Class> types = new ArrayList<Class>();
             for (Iterator i = ((List)clsList).iterator(); i.hasNext();) {
                 types.add(getAnnoClass(context, i.next()));
             }
 
             clazz.addMethodSignature(methodName.convertToString().asJavaString(), types.toArray(new Class[types.size()]));
 
             return context.getRuntime().getNil();
         }
 
         private static Class getAnnoClass(ThreadContext context, Object annoClass) {
             if (annoClass instanceof Class) {
                 return (Class) annoClass;
             } else if (annoClass instanceof IRubyObject) {
                 IRubyObject annoMod = (IRubyObject) annoClass;
                 if (annoMod.respondsTo("java_class")) {
                     return (Class) annoMod.callMethod(context, "java_class").toJava(Object.class);
                 }
             }
             throw context.getRuntime().newArgumentError("must supply java class argument instead of " + annoClass.toString());
         }
 
         private static RubyClass getRubyClass(IRubyObject maybeClass, ThreadContext context) throws RaiseException {
             RubyClass clazz;
             if (maybeClass instanceof RubyClass) {
                 clazz = (RubyClass) maybeClass;
             } else {
                 throw context.getRuntime().newTypeError(maybeClass, context.getRuntime().getClassClass());
             }
             return clazz;
         }
     }
 
     public static class JRubyThreadExtensions {
         private static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
         
         @JRubyMethod(name = "times", module = true)
         public static IRubyObject times(IRubyObject recv, Block unusedBlock) {
             Ruby runtime = recv.getRuntime();
             long cpu = threadBean.getCurrentThreadCpuTime();
             long user = threadBean.getCurrentThreadUserTime();
             if (cpu == -1) {
                 cpu = 0L;
             }
             if (user == -1) {
                 user = 0L;
             }
             double system_d = (cpu - user) / 1000000000.0;
             double user_d = user / 1000000000.0;
             RubyFloat zero = runtime.newFloat(0.0);
             return RubyStruct.newStruct(runtime.getTmsStruct(),
                     new IRubyObject[] { RubyFloat.newFloat(runtime, user_d), RubyFloat.newFloat(runtime, system_d), zero, zero },
                     Block.NULL_BLOCK);
         }
     }
     
     public static class JRubyStringExtensions {
         @JRubyMethod(name = "alloc", meta = true)
         public static IRubyObject alloc(ThreadContext context, IRubyObject recv, IRubyObject size) {
             return RubyString.newStringLight(context.getRuntime(), (int)size.convertToInteger().getLongValue());
         }
     }
     
     public static class MethodExtensions {
         @JRubyMethod(name = "args")
         public static IRubyObject methodArgs(IRubyObject recv) {
             Ruby runtime = recv.getRuntime();
             RubyMethod rubyMethod = (RubyMethod)recv;
             RubyArray argsArray = RubyArray.newArray(runtime);
             DynamicMethod method = rubyMethod.method;
             RubySymbol req = runtime.newSymbol("req");
             RubySymbol opt = runtime.newSymbol("opt");
             RubySymbol rest = runtime.newSymbol("rest");
             RubySymbol block = runtime.newSymbol("block");
 
             if (method instanceof MethodArgs2) {
                 return RuntimeHelpers.parameterListToParameters(runtime, ((MethodArgs2)method).getParameterList(), true);
             } else if (method instanceof MethodArgs) {
                 MethodArgs interpMethod = (MethodArgs)method;
                 ArgsNode args = interpMethod.getArgsNode();
                 
                 ListNode requiredArgs = args.getPre();
                 for (int i = 0; requiredArgs != null && i < requiredArgs.size(); i++) {
                     Node argNode = requiredArgs.get(i);
                     if (argNode instanceof MultipleAsgn19Node) {
                         argsArray.append(RubyArray.newArray(runtime, req));
                     } else {
                         argsArray.append(RubyArray.newArray(runtime, req, getNameFrom(runtime, (INameNode)argNode)));
                     }
                 }
                 
                 ListNode optArgs = args.getOptArgs();
                 for (int i = 0; optArgs != null && i < optArgs.size(); i++) {
                     argsArray.append(RubyArray.newArray(runtime, opt, getNameFrom(runtime, (INameNode) optArgs.get(i))));
                 }
 
                 if (args.getRestArg() >= 0) {
                     RestArgNode restArg = (RestArgNode) args.getRestArgNode();
 
                     if (restArg instanceof UnnamedRestArgNode) {
                         if (((UnnamedRestArgNode) restArg).isStar()) {
                             argsArray.append(RubyArray.newArray(runtime, rest));
                         }
                     } else {
                         argsArray.append(RubyArray.newArray(runtime, rest, getNameFrom(runtime, args.getRestArgNode())));
                     }
                 }
                 
                 ListNode requiredArgsPost = args.getPost();
                 for (int i = 0; requiredArgsPost != null && i < requiredArgsPost.size(); i++) {
                     Node argNode = requiredArgsPost.get(i);
                     if (argNode instanceof MultipleAsgn19Node) {
                         argsArray.append(RubyArray.newArray(runtime, req));
                     } else {
                         argsArray.append(RubyArray.newArray(runtime, req, getNameFrom(runtime, (INameNode) requiredArgsPost.get(i))));
                     }
                 }
 
                 if (args.getBlock() != null) {
                     argsArray.append(RubyArray.newArray(runtime, block, getNameFrom(runtime, args.getBlock())));
                 }
             } else {
                 if (method.getArity() == Arity.OPTIONAL) {
                     argsArray.append(RubyArray.newArray(runtime, rest));
                 }
             }
 
             return argsArray;
         }
     }
 
     private static IRubyObject getNameFrom(Ruby runtime, INameNode node) {
         return node == null ? runtime.getNil() : RubySymbol.newSymbol(runtime, node.getName());
     }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index ed323e994d..ced16b9f62 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,454 +1,447 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 MenTaLguY <mental@rydia.net>
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
 package org.jruby;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.util.List;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import static org.jruby.javasupport.util.RuntimeHelpers.metaclass;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.DataType;
 
 /**
  * RubyObject is the only implementation of the
  * {@link org.jruby.runtime.builtin.IRubyObject}. Every Ruby object in JRuby
  * is represented by something that is an instance of RubyObject. In
  * some of the core class implementations, this means doing a subclass
  * that extends RubyObject, in other cases it means using a simple
  * RubyObject instance and the data field to store specific
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
  * @author  jpetersen
  */
 @JRubyClass(name="Object", include="Kernel")
 public class RubyObject extends RubyBasicObject {
     // Equivalent of T_DATA
     public static class Data extends RubyObject implements DataType {
         public Data(Ruby runtime, RubyClass metaClass, Object data) {
             super(runtime, metaClass);
             dataWrapStruct(data);
         }
 
         public Data(RubyClass metaClass, Object data) {
             super(metaClass);
             dataWrapStruct(data);
         }
     }
 
     /**
      * Standard path for object creation. Objects are entered into ObjectSpace
      * only if ObjectSpace is enabled.
      */
     public RubyObject(Ruby runtime, RubyClass metaClass) {
         super(runtime, metaClass);
     }
 
     /**
      * Path for objects that don't taint and don't enter objectspace.
      */
     public RubyObject(RubyClass metaClass) {
         super(metaClass);
     }
 
     /**
      * Path for objects who want to decide whether they don't want to be in
      * ObjectSpace even when it is on. (notably used by objects being
      * considered immediate, they'll always pass false here)
      */
     protected RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace, boolean canBeTainted) {
         super(runtime, metaClass, useObjectSpace, canBeTainted);
     }
 
     protected RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         super(runtime, metaClass, useObjectSpace);
     }
 
     /**
      * Will create the Ruby class Object in the runtime
      * specified. This method needs to take the actual class as an
      * argument because of the Object class' central part in runtime
      * initialization.
      */
     public static RubyClass createObjectClass(Ruby runtime, RubyClass objectClass) {
         objectClass.index = ClassIndex.OBJECT;
         objectClass.setReifiedClass(RubyObject.class);
 
         objectClass.defineAnnotatedMethods(RubyObject.class);
 
         return objectClass;
     }
 
     /**
      * Default allocator instance for all Ruby objects. The only
      * reason to not use this allocator is if you actually need to
      * have all instances of something be a subclass of RubyObject.
      *
      * @see org.jruby.runtime.ObjectAllocator
      */
     public static final ObjectAllocator OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyObject(runtime, klass);
         }
     };
 
     public static final ObjectAllocator REIFYING_OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
-            reifyAncestors(klass);
+            klass.reifyWithAncestors();
             return klass.allocate();
         }
-
-        public void reifyAncestors(RubyClass klass) {
-            if (klass.getAllocator() == this) {
-                reifyAncestors(klass.getSuperClass().getRealClass());
-                klass.reify();
-            }
-        }
     };
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize() {
         return getRuntime().getNil();
     }
 
     /**
      * Will make sure that this object is added to the current object
      * space.
      *
      * @see org.jruby.runtime.ObjectSpace
      */
     public void attachToObjectSpace() {
         getRuntime().getObjectSpace().add(this);
     }
 
     /**
      * This is overridden in the other concrete Java builtins to provide a fast way
      * to determine what type they are.
      *
      * Will generally return a value from org.jruby.runtime.ClassIndex
      *
      * @see org.jruby.runtime.ClassInde
      */
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.OBJECT;
     }
 
     /**
      * Simple helper to print any objects.
      */
     public static void puts(Object obj) {
         System.out.println(obj.toString());
     }
 
     /**
      * This method is just a wrapper around the Ruby "==" method,
      * provided so that RubyObjects can be used as keys in the Java
      * HashMap object underlying RubyHash.
      */
     @Override
     public boolean equals(Object other) {
         return other == this ||
                 other instanceof IRubyObject &&
                 callMethod(getRuntime().getCurrentContext(), "==", (IRubyObject) other).isTrue();
     }
 
     /**
      * The default toString method is just a wrapper that calls the
      * Ruby "to_s" method.
      */
     @Override
     public String toString() {
         RubyString rubyString = RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "to_s").convertToString();
         return rubyString.getUnicodeValue();
     }
 
     /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(IRubyObject[] args, Block block) {
         RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "initialize", args, block);
     }
 
     /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(Block block) {
         RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "initialize", block);
     }
 
     /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(IRubyObject arg0, Block block) {
         RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "initialize", arg0, block);
     }
 
     /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(IRubyObject arg0, IRubyObject arg1, Block block) {
         RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "initialize", arg0, arg1, block);
     }
 
     /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "initialize", arg0, arg1, arg2, block);
     }
 
     /**
      * Tries to convert this object to the specified Ruby type, using
      * a specific conversion method.
      */
     @Deprecated
     public final IRubyObject convertToType(RubyClass target, int convertMethodIndex) {
         throw new RuntimeException("Not supported; use the String versions");
     }
 
     /** specific_eval
      *
      * Evaluates the block or string inside of the context of this
      * object, using the supplied arguments. If a block is given, this
      * will be yielded in the specific context of this object. If no
      * block is given then a String-like object needs to be the first
      * argument, and this string will be evaluated. Second and third
      * arguments in the args-array is optional, but can contain the
      * filename and line of the string under evaluation.
      */
     @Deprecated
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(context, mod, block);
         }
 
         if (args.length == 0) {
             throw getRuntime().newArgumentError("block not supplied");
         } else if (args.length > 3) {
             String lastFuncName = context.getFrameName();
             throw getRuntime().newArgumentError(
                 "wrong number of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
         }
 
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         RubyString evalStr;
         if (args[0] instanceof RubyString) {
             evalStr = (RubyString)args[0];
         } else {
             evalStr = args[0].convertToString();
         }
 
         String file;
         int line;
         if (args.length > 1) {
             file = args[1].convertToString().asJavaString();
             if (args.length > 2) {
                 line = (int)(args[2].convertToInteger().getLongValue() - 1);
             } else {
                 line = 0;
             }
         } else {
             file = "(eval)";
             line = 0;
         }
 
         return evalUnder(context, mod, evalStr, file, line);
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_equal
      *
      * The Ruby "===" method is used by default in case/when
      * statements. The Object implementation first checks Java identity
      * equality and then calls the "==" method too.
      */
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return context.getRuntime().newBoolean(equalInternal(context, this, other));
     }
 
     protected static DynamicMethod _op_equal(ThreadContext context, RubyClass metaclass) {
         if (metaclass.index >= ClassIndex.MAX_CLASSES) return metaclass.searchMethod("==");
         return context.runtimeCache.getMethod(context, metaclass, metaclass.index * (MethodIndex.OP_EQUAL + 1), "==");
     }
 
     protected static DynamicMethod _eql(ThreadContext context, RubyClass metaclass) {
         if (metaclass.index >= ClassIndex.MAX_CLASSES) return metaclass.searchMethod("eql?");
         return context.runtimeCache.getMethod(context, metaclass, metaclass.index * (MethodIndex.EQL + 1), "eql?");
     }
 
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "==" method.
      */
     protected static boolean equalInternal(final ThreadContext context, final IRubyObject a, final IRubyObject b){
         if (a == b) {
             return true;
         } else if (a instanceof RubySymbol) {
             return false;
         } else if (a instanceof RubyFixnum && b instanceof RubyFixnum) {
             return ((RubyFixnum)a).fastEqual((RubyFixnum)b);
         } else if (a instanceof RubyFloat && b instanceof RubyFloat) {
             return ((RubyFloat)a).fastEqual((RubyFloat)b);
         } else {
             RubyClass metaclass = metaclass(a);
             return _op_equal(context, metaclass).call(context, a, metaclass, "==", b).isTrue();
         }
     }
 
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "eql?" method.
      */
     protected static boolean eqlInternal(final ThreadContext context, final IRubyObject a, final IRubyObject b){
         if (a == b) {
             return true;
         } else if (a instanceof RubySymbol) {
             return false;
         } else if (a instanceof RubyNumeric) {
             if (a.getClass() != b.getClass()) return false;
             return equalInternal(context, a, b);
         } else {
             RubyClass metaclass = metaclass(a);
             return _eql(context, metaclass).call(context, a, metaclass, "eql?", b).isTrue();
         }
     }
 
     /**
      * Override the Object#hashCode method to make sure that the Ruby
      * hash is actually used as the hashcode for Ruby objects. If the
      * Ruby "hash" method doesn't return a number, the Object#hashCode
      * implementation will be used instead.
      */
     @Override
     public int hashCode() {
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), "hash");
         if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue);
         return nonFixnumHashCode(hashValue);
     }
 
     private int nonFixnumHashCode(IRubyObject hashValue) {
         Ruby runtime = getRuntime();
         if (runtime.is1_9()) {
             RubyInteger integer = hashValue.convertToInteger();
             if (integer instanceof RubyBignum) {
                 return (int)integer.getBigIntegerValue().intValue();
             } else {
                 return (int)integer.getLongValue();
             }
         } else {
             hashValue = hashValue.callMethod(runtime.getCurrentContext(), "%", RubyFixnum.newFixnum(runtime, 536870923L));
             if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue);
             return System.identityHashCode(hashValue);
         }
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
 
     /**
      * Tries to support Java serialization of Ruby objects. This is
      * still experimental and might not work.
      */
     // NOTE: Serialization is primarily supported for testing purposes, and there is no general
     // guarantee that serialization will work correctly. Specifically, instance variables pointing
     // at symbols, threads, modules, classes, and other unserializable types are not detected.
     private void writeObject(ObjectOutputStream out) throws IOException {
         out.defaultWriteObject();
         // write out ivar count followed by name/value pairs
         List<String> names = getInstanceVariableNameList();
         out.writeInt(names.size());
         for (String name : names) {
             out.writeObject(name);
             out.writeObject(getInstanceVariables().getInstanceVariable(name));
         }
     }
 
     /**
      * Tries to support Java unserialization of Ruby objects. This is
      * still experimental and might not work.
      */
     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         // rest in ivar count followed by name/value pairs
         int ivarCount = in.readInt();
         for (int i = 0; i < ivarCount; i++) {
             setInstanceVariable((String)in.readObject(), (IRubyObject)in.readObject());
         }
     }
 
 }
