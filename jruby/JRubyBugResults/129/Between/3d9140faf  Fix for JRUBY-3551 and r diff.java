diff --git a/src/org/jruby/RubyClass.java b/src/org/jruby/RubyClass.java
index db9634649a..ad6174adcb 100644
--- a/src/org/jruby/RubyClass.java
+++ b/src/org/jruby/RubyClass.java
@@ -1,853 +1,896 @@
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
 
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.Map;
 import java.util.Set;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.collections.WeakHashSet;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Class", parent="Module")
 public class RubyClass extends RubyModule {
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
     
     public static void createClassClass(Ruby runtime, RubyClass classClass) {
         classClass.index = ClassIndex.CLASS;
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
         
         classClass.addMethod("new", new SpecificArityNew(classClass, Visibility.PUBLIC));
         
         // This is a non-standard method; have we decided to start extending Ruby?
         //classClass.defineFastMethod("subclasses", callbackFactory.getFastOptMethod("subclasses"));
         
         // FIXME: for some reason this dispatcher causes a VerifyError...
         //classClass.dispatcher = callbackFactory.createDispatcher(classClass);
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
 
     @JRubyMethod(name = "allocate")
     public IRubyObject allocate() {
         if (superClass == null) throw runtime.newTypeError("can't instantiate uninitialized class");
         IRubyObject obj = allocator.allocate(runtime, this);
         if (obj.getMetaClass().getRealClass() != getRealClass()) throw runtime.newTypeError("wrong instance allocation");
         return obj;
     }
 
     public CallSite[] getBaseCallSites() {
         return baseCallSites;
     }
     
     public CallSite[] getExtraCallSites() {
         return extraCallSites;
     }
 
     public static class VariableAccessor {
         private int index;
         private final int classId;
         public VariableAccessor(int index, int classId) {
             this.index = index;
             this.classId = classId;
         }
         public int getClassId() {
             return classId;
         }
         public int getIndex() {
             return index;
         }
         public Object get(Object object) {
             return ((IRubyObject)object).getVariable(index);
         }
         public void set(Object object, Object value) {
             ((IRubyObject)object).setVariable(index, value);
         }
         public static final VariableAccessor DUMMY_ACCESSOR = new VariableAccessor(-1, -1);
     }
 
     @SuppressWarnings("unchecked")
     private Map<String, VariableAccessor> variableAccessors = (Map<String, VariableAccessor>)Collections.EMPTY_MAP;
 
     public Map<String, VariableAccessor> getVariableAccessorsForRead() {
         return variableAccessors;
     }
 
     public synchronized Map<String, VariableAccessor> getVariableAccessorsForWrite() {
         if (variableAccessors == Collections.EMPTY_MAP) variableAccessors = new Hashtable<String, VariableAccessor>(1);
         return variableAccessors;
     }
 
     public synchronized VariableAccessor getVariableAccessorForWrite(String name) {
         Map<String, VariableAccessor> myVariableAccessors = getVariableAccessorsForWrite();
         VariableAccessor ivarAccessor = myVariableAccessors.get(name);
         if (ivarAccessor == null) {
             ivarAccessor = new VariableAccessor(myVariableAccessors.size(), this.id);
             myVariableAccessors.put(name, ivarAccessor);
         }
         return ivarAccessor;
     }
 
     public VariableAccessor getVariableAccessorForRead(String name) {
         VariableAccessor accessor = getVariableAccessorsForRead().get(name);
         if (accessor == null) accessor = VariableAccessor.DUMMY_ACCESSOR;
         return accessor;
     }
 
     public int getVariableTableSize() {
         return variableAccessors.size();
     }
 
     public Map<String, VariableAccessor> getVariableTableCopy() {
         return new HashMap<String, VariableAccessor>(getVariableAccessorsForRead());
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
 
     protected final Ruby runtime;
     private ObjectAllocator allocator; // the default allocator
     protected ObjectMarshal marshal;
     private Set<RubyClass> subclasses;
 
     /** separate path for MetaClass and IncludedModuleWrapper construction
      *  (rb_class_boot version for MetaClasses)
      *  no marshal, allocator initialization and addSubclass(this) here!
      */
     protected RubyClass(Ruby runtime, RubyClass superClass, boolean objectSpace) {
         super(runtime, runtime.getClassClass(), objectSpace);
         this.runtime = runtime;
         this.superClass = superClass; // this is the only case it might be null here (in MetaClass construction)
     }
 
     /** separate path for MetaClass and IncludedModuleWrapper construction
      *  (rb_class_boot version for MetaClasses)
      *  no marshal, allocator initialization and addSubclass(this) here!
      */
     protected RubyClass(Ruby runtime, RubyClass superClass, Generation generation, boolean objectSpace) {
         super(runtime, runtime.getClassClass(), generation, objectSpace);
         this.runtime = runtime;
         this.superClass = superClass; // this is the only case it might be null here (in MetaClass construction)
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
         superClass = superClazz;
         marshal = superClazz.marshal; // use parent's marshal
         superClazz.addSubclass(this);
         
         infectBy(superClass);        
     }
     
     /** 
      * A constructor which allows passing in an array of supplementary call sites.
      */
     protected RubyClass(Ruby runtime, RubyClass superClazz, CallSite[] extraCallSites) {
         this(runtime);
         this.superClass = superClazz;
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
             MetaClass klass = new MetaClass(runtime, superClass); // rb_class_boot
             setMetaClass(klass);
 
             klass.setAttached(this);
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
             RubyClass cls = (RubyClass)self;
             IRubyObject obj = cls.allocate();
             cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, args, block);
             return obj;
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             RubyClass cls = (RubyClass)self;
             IRubyObject obj = cls.allocate();
             cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, block);
             return obj;
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             RubyClass cls = (RubyClass)self;
             IRubyObject obj = cls.allocate();
             cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, block);
             return obj;
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             RubyClass cls = (RubyClass)self;
             IRubyObject obj = cls.allocate();
             cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, arg1, block);
             return obj;
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             RubyClass cls = (RubyClass)self;
             IRubyObject obj = cls.allocate();
             cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, arg1, arg2, block);
             return obj;
         }
     }
 
     /** rb_class_initialize
      * 
      */
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_8, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(runtime.getObject(), block, false);
     }
         
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_8, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon((RubyClass)superObject, block, false);
     }
         
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_9, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize19(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(runtime.getObject(), block, true);
     }
         
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_9, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize19(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon((RubyClass)superObject, block, true);
     }
 
     private IRubyObject initializeCommon(RubyClass superClazz, Block block, boolean callInheritBeforeSuper) {
         superClass = superClazz;
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
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = Visibility.PRIVATE)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         if (superClass != null) throw runtime.newTypeError("already initialized class");
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
         this.superClass = superClass;
     }
     
     public Collection subclasses(boolean includeDescendants) {
-        if (subclasses != null) {
-            Collection<RubyClass> mine = new ArrayList<RubyClass>(subclasses);
+        Set<RubyClass> mySubclasses = subclasses;
+        if (mySubclasses != null) {
+            Collection<RubyClass> mine = new ArrayList<RubyClass>(mySubclasses);
             if (includeDescendants) {
-                for (RubyClass i: subclasses) {
+                for (RubyClass i: mySubclasses) {
                     mine.addAll(i.subclasses(includeDescendants));
                 }
             }
 
             return mine;
         } else {
             return Collections.EMPTY_LIST;
         }
     }
-    
+
+    /**
+     * Add a new subclass to the weak set of subclasses.
+     *
+     * This version always constructs a new set to avoid having to synchronize
+     * against the set when iterating it for invalidation in
+     * invalidateCacheDescendants.
+     *
+     * @param subclass The subclass to add
+     */
     public synchronized void addSubclass(RubyClass subclass) {
-        if (subclasses == null) subclasses = new WeakHashSet<RubyClass>();
-        subclasses.add(subclass);
+        Set<RubyClass> oldSubclasses = subclasses;
+        Set<RubyClass> mySubclasses =
+                new WeakHashSet<RubyClass>(oldSubclasses == null ? 1 : oldSubclasses.size() + 1);
+        if (oldSubclasses != null) mySubclasses.addAll(oldSubclasses);
+        mySubclasses.add(subclass);
+        subclasses = Collections.unmodifiableSet(mySubclasses);
     }
     
+    /**
+     * Remove a subclass from the weak set of subclasses.
+     *
+     * This version always constructs a new set to avoid having to synchronize
+     * against the set when iterating it for invalidation in
+     * invalidateCacheDescendants.
+     *
+     * @param subclass The subclass to remove
+     */
     public synchronized void removeSubclass(RubyClass subclass) {
-        if (subclasses == null) return;
-        subclasses.remove(subclass);
+        Set<RubyClass> oldSubclasses = subclasses;
+        if (oldSubclasses == null) return;
+        
+        Set<RubyClass> mySubclasses = new WeakHashSet<RubyClass>(oldSubclasses.size() + 1);
+        mySubclasses.addAll(oldSubclasses);
+        mySubclasses.remove(subclass);
+
+        subclasses = Collections.unmodifiableSet(mySubclasses);
     }
 
+    /**
+     * Invalidate all subclasses of this class by walking the set of all
+     * subclasses and asking them to invalidate themselves.
+     *
+     * Note that this version works against a reference to the current set of
+     * subclasses, which could be replaced by the time this iteration is
+     * complete. In theory, there may be a path by which invalidation would
+     * miss a class added during the invalidation process, but the exposure is
+     * minimal if it exists at all. The only way to prevent it would be to
+     * synchronize both invalidation and subclass set modification against a
+     * global lock, which we would like to avoid.
+     */
+    @Override
     protected void invalidateCacheDescendants() {
         super.invalidateCacheDescendants();
         // update all subclasses
-        if (subclasses != null) for (RubyClass subclass : subclasses) {
+        Set<RubyClass> mySubclasses = subclasses;
+        if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
             subclass.invalidateCacheDescendants();
         }
     }
     
     public Ruby getClassRuntime() {
         return runtime;
     }
 
     public RubyClass getRealClass() {
         return this;
     }    
 
     @JRubyMethod(name = "inherited", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject inherited(ThreadContext context, IRubyObject arg) {
         return runtime.getNil();
     }
 
     /** rb_class_inherited (reversed semantics!)
      * 
      */
     public void inherit(RubyClass superClazz) {
         if (superClazz == null) superClazz = runtime.getObject();
 
         superClazz.invokeInherited(runtime.getCurrentContext(), superClazz, this);
     }
 
     /** Return the real super class of this class.
      * 
      * rb_class_superclass
      *
      */
     @JRubyMethod(name = "superclass", compat = CompatVersion.RUBY1_8)
     public IRubyObject superclass(ThreadContext context) {
         RubyClass superClazz = superClass;
 
         if (superClazz == null) throw runtime.newTypeError("uninitialized class");
 
         if (isSingleton()) superClazz = metaClass;
         while (superClazz != null && superClazz.isIncluded()) superClazz = superClazz.superClass;
 
         return superClazz != null ? superClazz : runtime.getNil();
     }
     
     @JRubyMethod(name = "superclass", compat = CompatVersion.RUBY1_9)
     public IRubyObject superclass19(ThreadContext context) {
         RubyClass superClazz = superClass;
         if (superClazz == null) {
             if (metaClass == runtime.getBasicObject()) return runtime.getNil();
             throw runtime.newTypeError("uninitialized class");
         }
 
         while (superClazz != null && superClazz.isIncluded()) superClazz = superClazz.superClass;
 
         return superClazz != null ? superClazz : runtime.getNil();
     }
 
     private void checkNotInitialized() {
         if (superClass != null) throw runtime.newTypeError("already initialized class");
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
 }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index ea41809d19..3a3a010bd1 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1224 +1,1230 @@
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
  * Copyright (C) 2006-2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import static org.jruby.anno.FrameField.VISIBILITY;
 import static org.jruby.runtime.Visibility.MODULE_FUNCTION;
 import static org.jruby.runtime.Visibility.PRIVATE;
 import static org.jruby.runtime.Visibility.PROTECTED;
 import static org.jruby.runtime.Visibility.PUBLIC;
 
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Hashtable;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyConstant;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JavaMethodDescriptor;
 import org.jruby.anno.TypePopulator;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.AliasMethod;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.FullFunctionCallbackMethod;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodOne;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodZero;
 import org.jruby.internal.runtime.methods.MethodMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.SimpleCallbackMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.callsite.CacheEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ClassProvider;
 import org.jruby.util.IdUtil;
 import org.jruby.util.collections.WeakHashSet;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Module")
 public class RubyModule extends RubyObject {
     private static final boolean DEBUG = false;
     public static final Set<String> SCOPE_CAPTURING_METHODS = new HashSet<String>(Arrays.asList(
             "eval",
             "module_eval",
             "class_eval",
             "instance_eval",
             "instance_exec",
             "binding",
             "local_variables"
             ));
     
     public static RubyClass createModuleClass(Ruby runtime, RubyClass moduleClass) {
         moduleClass.index = ClassIndex.MODULE;
         moduleClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyModule;
             }
         };
         
         moduleClass.defineAnnotatedMethods(RubyModule.class);
         moduleClass.defineAnnotatedMethods(ModuleKernelMethods.class);
 
         return moduleClass;
     }
     
     public static class ModuleKernelMethods {
         @JRubyMethod
         public static IRubyObject autoload(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return RubyKernel.autoload(recv, arg0, arg1);
         }
         
         @JRubyMethod(name = "autoload?")
         public static IRubyObject autoload_p(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
             return RubyKernel.autoload_p(context, recv, arg0);
         }
     }
     
     static ObjectAllocator MODULE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyModule(runtime, klass);
         }
     };
     
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.MODULE;
     }
 
     @Override
     public boolean isModule() {
         return true;
     }
 
     @Override
     public boolean isClass() {
         return false;
     }
 
     public boolean isSingleton() {
         return false;
     }    
     
     // superClass may be null.
     protected RubyClass superClass;
 
     public int index;
 
     public static class KindOf {
         public static final KindOf DEFAULT_KIND_OF = new KindOf();
         public boolean isKindOf(IRubyObject obj, RubyModule type) {
             return obj.getMetaClass().hasModuleInHierarchy(type);
         }
     }
     
     public boolean isInstance(IRubyObject object) {
         return kindOf.isKindOf(object, this);
     }
 
     public KindOf kindOf = KindOf.DEFAULT_KIND_OF;
 
     public final int id;
 
     // Containing class...The parent of Object is null. Object should always be last in chain.
     public RubyModule parent;
 
     // ClassId is the name of the class/module sans where it is located.
     // If it is null, then it an anonymous class.
     protected String classId;
 
     private static final Map<String, IRubyObject> DUMMY_CONSTANTS = Collections.unmodifiableMap(new HashMap(0));
 
     private volatile Map<String, IRubyObject> constants;
     private final Map<String, DynamicMethod> methods = new ConcurrentHashMap<String, DynamicMethod>(4, 0.9f, 1);
     private final Map<String, CacheEntry> cachedMethods = new ConcurrentHashMap<String, CacheEntry>(4, 0.75f, 1);
 
     public Map<String, IRubyObject> getConstantMap() {
         return constants == null ? DUMMY_CONSTANTS : constants;
     }
 
     public synchronized Map<String, IRubyObject> getConstantMapForWrite() {
         return constants == null ? constants = new ConcurrentHashMap<String, IRubyObject>(4, 0.9f, 1) : constants;
     }
     
     protected static class Generation {
         public volatile int hash;
         public Generation() {
             hash = hashCode();
         }
         public synchronized void update() {
             hash = hash + (hashCode() * 31);
         }
     }
     protected final Generation generation;
     
     protected Set<RubyClass> includingHierarchies;
     
     public synchronized void addIncludingHierarchy(IncludedModuleWrapper hierarchy) {
-        if (includingHierarchies == null) includingHierarchies = new WeakHashSet<RubyClass>();
-        includingHierarchies.add(hierarchy);
+        Set<RubyClass> oldIncludingHierarchies = includingHierarchies;
+        Set<RubyClass> myIncludingHierarchies =
+                new WeakHashSet<RubyClass>(oldIncludingHierarchies == null ? 1 : oldIncludingHierarchies.size() + 1);
+        if (includingHierarchies != null) {
+            myIncludingHierarchies.addAll(oldIncludingHierarchies);
+        }
+        myIncludingHierarchies.add(hierarchy);
+        includingHierarchies = Collections.unmodifiableSet(myIncludingHierarchies);
     }
     
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
         generation = new Generation();
     }
 
     /** separate path for MetaClass construction
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass, Generation generation, boolean objectSpace) {
         super(runtime, metaClass, objectSpace);
         id = runtime.allocModuleId();
         // if (parent == null) parent = runtime.getObject();
         setFlag(USER7_F, !isClass());
         this.generation = generation;
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
 
     private void checkForCyclicInclude(RubyModule m) throws RaiseException {
         if (getNonIncludedClass() == m.getNonIncludedClass()) {
             throw getRuntime().newArgumentError("cyclic include detected");
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
 
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     protected void setSuperClass(RubyClass superClass) {
         // update superclass reference
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
     
     private volatile String bareName;
     private volatile String fullName;
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (fullName == null) {
             fullName = calculateFullName();
         }
         return fullName;
     }
 
     private String calculateFullName() {
         if (getBaseName() == null) {
             if (bareName == null) {
                 if (isClass()) {
                     bareName = "#<" + "Class" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
                 } else {
                     bareName = "#<" + "Module" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
                 }
             }
 
             return bareName;
         }
 
         String result = getBaseName();
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
             result = pName + "::" + result;
         }
 
         return result;
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
                     if (anno.compat() == CompatVersion.RUBY1_8) {
                         methodsHash = staticAnnotatedMethods1_8;
                     } else if (anno.compat() == CompatVersion.RUBY1_9) {
                         methodsHash = staticAnnotatedMethods1_9;
                     } else {
                         methodsHash = staticAnnotatedMethods;
                     }
                 } else {
                     if (anno.compat() == CompatVersion.RUBY1_8) {
                         methodsHash = annotatedMethods1_8;
                     } else if (anno.compat() == CompatVersion.RUBY1_9) {
                         methodsHash = annotatedMethods1_9;
                     } else {
                         methodsHash = annotatedMethods;
                     }
                 }
                 
                 methodDescs = methodsHash.get(name);
                 if (methodDescs == null) {
                     methodDescs = new ArrayList<JavaMethodDescriptor>();
                     methodsHash.put(name, methodDescs);
                 }
                 
                 methodDescs.add(desc);
             }
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
         String x = clazz.getSimpleName();
         TypePopulator populator = null;
         
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // we need full traces, use default (slow) populator
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
     
     private static MethodFactory.MethodDefiningCallback methodDefiningCallback = new MethodFactory.MethodDefiningCallback() {
         public void define(RubyModule module, JavaMethodDescriptor desc, DynamicMethod dynamicMethod) {
             JRubyMethod jrubyMethod = desc.anno;
             if (jrubyMethod.frame()) {
                 for (String name : jrubyMethod.name()) {
                     ASTInspector.FRAME_AWARE_METHODS.add(name);
                 }
             }
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     module.getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
                 RubyModule singletonClass;
 
                 if (jrubyMethod.meta()) {
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
         synchronized(getMethods()) {
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
         getMethods().put(name, method);
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
                 private RubyClass.VariableAccessor accessor = RubyClass.VariableAccessor.DUMMY_ACCESSOR;
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                     IRubyObject variable = (IRubyObject)verifyAccessor(self.getMetaClass().getRealClass()).get(self);
diff --git a/src/org/jruby/util/collections/WeakHashSet.java b/src/org/jruby/util/collections/WeakHashSet.java
index 732f0d042c..cec07ceced 100644
--- a/src/org/jruby/util/collections/WeakHashSet.java
+++ b/src/org/jruby/util/collections/WeakHashSet.java
@@ -1,103 +1,107 @@
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
 package org.jruby.util.collections;
 
 import java.util.*;
 
 /**
  * A simple set that uses weak references to ensure that its elements can be garbage collected.
  * See WeakHashMap.
  *
  * @author <a href="http://www.cs.auckland.ac.nz/~robert/">Robert Egglestone</a>
  */
 public class WeakHashSet<T> implements Set<T> {
     private WeakHashMap<T,T> map;
 
     public WeakHashSet() {
         map = new WeakHashMap<T,T>();
     }
 
+    public WeakHashSet(int size) {
+        map = new WeakHashMap<T,T>(size);
+    }
+
     public boolean add(T o) {
         T previousValue = map.put(o, null);
         return previousValue == null;
     }
 
     public Iterator<T> iterator() {
         return map.keySet().iterator();
     }
 
     public int size() {
         return map.size();
     }
 
     public boolean isEmpty() {
         return map.isEmpty();
     }
 
     public boolean contains(Object o) {
         return map.containsKey(o);
     }
 
     public boolean remove(Object o) {
         boolean contains = contains(o);
         map.remove(o);
         return contains;
     }
 
     public boolean removeAll(Collection collection) {
         return map.keySet().removeAll(collection);
     }
 
     public boolean retainAll(Collection collection) {
         return map.keySet().retainAll(collection);
     }
 
     public void clear() {
         map.clear();
     }
 
     public Object[] toArray() {
         return map.keySet().toArray();
     }
 
     public boolean containsAll(Collection arg0) {
         return map.keySet().containsAll(arg0);
     }
 
     public boolean addAll(Collection<? extends T> arg0) {
         boolean added = false;
         for (T i: arg0) {
             add(i);
             added = true;
         }
         return added;
     }
 
     public <T> T[] toArray(T[] arg0) {
         return map.keySet().toArray(arg0);
     }
 
 }
