diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index e093597bee..ab7ff82075 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1938 +1,1993 @@
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
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.FullFunctionCallbackMethod;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodOne;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodZero;
 import org.jruby.internal.runtime.methods.MethodMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.ProfilingDynamicMethod;
 import org.jruby.internal.runtime.methods.SimpleCallbackMethod;
 import org.jruby.internal.runtime.methods.SynchronizedDynamicMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
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
     protected static final String ERR_INSECURE_SET_CONSTANT  = "Insecure: can't modify constant";
     protected static final String ERR_FROZEN_CONST_TYPE = "class/module ";
     public static final Set<String> SCOPE_CAPTURING_METHODS = new HashSet<String>(Arrays.asList(
             "eval",
             "module_eval",
             "class_eval",
             "instance_eval",
             "module_exec",
             "class_exec",
             "instance_exec",
             "binding",
             "local_variables"
             ));
 
     public static final ObjectAllocator MODULE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyModule(runtime, klass);
         }
     };
     
     public static RubyClass createModuleClass(Ruby runtime, RubyClass moduleClass) {
         moduleClass.index = ClassIndex.MODULE;
         moduleClass.setReifiedClass(RubyModule.class);
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
 
     public static class KindOf {
         public static final KindOf DEFAULT_KIND_OF = new KindOf();
         public boolean isKindOf(IRubyObject obj, RubyModule type) {
             return obj.getMetaClass().hasModuleInHierarchy(type);
         }
     }
     
     public boolean isInstance(IRubyObject object) {
         return kindOf.isKindOf(object, this);
     }
 
     public Map<String, IRubyObject> getConstantMap() {
         return constants;
     }
 
     public synchronized Map<String, IRubyObject> getConstantMapForWrite() {
         return constants == Collections.EMPTY_MAP ? constants = new ConcurrentHashMap<String, IRubyObject>(4, 0.9f, 1) : constants;
     }
     
     public void addIncludingHierarchy(IncludedModuleWrapper hierarchy) {
         synchronized (getRuntime().getHierarchyLock()) {
             Set<RubyClass> oldIncludingHierarchies = includingHierarchies;
             if (oldIncludingHierarchies == Collections.EMPTY_SET) includingHierarchies = oldIncludingHierarchies = new WeakHashSet(4);
             oldIncludingHierarchies.add(hierarchy);
         }
     }
 
     /** separate path for MetaClass construction
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass, boolean objectSpace) {
         super(runtime, metaClass, objectSpace);
         id = runtime.allocModuleId();
         runtime.addModule(this);
         // if (parent == null) parent = runtime.getObject();
         setFlag(USER7_F, !isClass());
         generation = runtime.getNextModuleGeneration();
         if (runtime.getInstanceConfig().isProfiling()) {
-            cacheEntryFactory = ProfilingCacheEntryFactory;
+            cacheEntryFactory = new ProfilingCacheEntryFactory(NormalCacheEntryFactory);
         } else {
             cacheEntryFactory = NormalCacheEntryFactory;
         }
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
         if (!classProviders.contains(provider)) {
             Set<ClassProvider> cp = new HashSet<ClassProvider>(classProviders);
             cp.add(provider);
             classProviders = cp;
         }
     }
 
     public synchronized void removeClassProvider(ClassProvider provider) {
         Set<ClassProvider> cp = new HashSet<ClassProvider>(classProviders);
         cp.remove(provider);
         classProviders = cp;
     }
 
     private void checkForCyclicInclude(RubyModule m) throws RaiseException {
         if (getNonIncludedClass() == m.getNonIncludedClass()) {
             throw getRuntime().newArgumentError("cyclic include detected");
         }
     }
 
     private RubyClass searchProvidersForClass(String name, RubyClass superClazz) {
         RubyClass clazz;
         for (ClassProvider classProvider: classProviders) {
             if ((clazz = classProvider.defineClassUnder(this, name, superClazz)) != null) {
                 return clazz;
             }
         }
         return null;
     }
 
     private RubyModule searchProvidersForModule(String name) {
         RubyModule module;
         for (ClassProvider classProvider: classProviders) {
             if ((module = classProvider.defineModuleUnder(this, name)) != null) {
                 return module;
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
         if (superClass != null && superClass.isSynchronized()) becomeSynchronized();
     }
 
     public RubyModule getParent() {
         return parent;
     }
 
     public void setParent(RubyModule parent) {
         this.parent = parent;
     }
     
     public Map<String, DynamicMethod> getMethods() {
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
         if (fullName == null) {
             calculateName();
         }
         return fullName;
     }
 
     private void calculateName() {
         fullName = calculateFullName();
     }
 
     private String calculateFullName() {
         if (getBaseName() == null) {
             if (bareName == null) {
                 if (isClass()) {
                     bareName = "#<" + "Class"  + ":0x1" + String.format("%08x", System.identityHashCode(this)) + ">";
                 } else {
                     bareName = "#<" + "Module" + ":0x1" + String.format("%08x", System.identityHashCode(this)) + ">";
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
 
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
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
 
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
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
+
+        /**
+         * Test all WrapperCacheEntryFactory instances in the chain for assignability
+         * from the given class.
+         *
+         * @param cacheEntryFactoryClass the class from which to test assignability
+         * @return whether the given class is assignable from any factory in the chain
+         */
+        public boolean hasCacheEntryFactory(Class cacheEntryFactoryClass) {
+            CacheEntryFactory current = this;
+            while (current instanceof WrapperCacheEntryFactory) {
+                if (cacheEntryFactoryClass.isAssignableFrom(current.getClass())) {
+                    return true;
+                }
+                current = ((WrapperCacheEntryFactory)current).getPrevious();
+            }
+            if (cacheEntryFactoryClass.isAssignableFrom(current.getClass())) {
+                return true;
+            }
+            return false;
+        }
+    }
+
+    /**
+     * A wrapper CacheEntryFactory, for delegating cache entry creation along a chain.
+     */
+    protected static abstract class WrapperCacheEntryFactory extends CacheEntryFactory {
+        /** The CacheEntryFactory being wrapped. */
+        protected final CacheEntryFactory previous;
+
+        /**
+         * Construct a new WrapperCacheEntryFactory using the given CacheEntryFactory as
+         * the "previous" wrapped factory.
+         *
+         * @param previous the wrapped factory
+         */
+        public WrapperCacheEntryFactory(CacheEntryFactory previous) {
+            this.previous = previous;
+        }
+
+        public CacheEntryFactory getPrevious() {
+            return previous;
+        }
     }
 
     protected static final CacheEntryFactory NormalCacheEntryFactory = new CacheEntryFactory() {
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             return new CacheEntry(method, token);
         }
     };
 
-    protected static final CacheEntryFactory SynchronizedCacheEntryFactory = new CacheEntryFactory() {
+    protected static class SynchronizedCacheEntryFactory extends WrapperCacheEntryFactory {
+        public SynchronizedCacheEntryFactory(CacheEntryFactory previous) {
+            super(previous);
+        }
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
-            return new CacheEntry(new SynchronizedDynamicMethod(method), token);
+            if (method.isUndefined()) {
+                return new CacheEntry(method, token);
+            }
+            // delegate up the chain
+            CacheEntry delegated = previous.newCacheEntry(method, token);
+            return new CacheEntry(new SynchronizedDynamicMethod(delegated.method), delegated.token);
         }
-    };
+    }
 
-    protected static final CacheEntryFactory ProfilingCacheEntryFactory = new CacheEntryFactory() {
+    protected static class ProfilingCacheEntryFactory extends WrapperCacheEntryFactory {
+        public ProfilingCacheEntryFactory(CacheEntryFactory previous) {
+            super(previous);
+        }
         @Override
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             if (method.isUndefined()) {
                 return new CacheEntry(method, token);
             }
-            return new CacheEntry(new ProfilingDynamicMethod(method), token);
+            CacheEntry delegated = previous.newCacheEntry(method, token);
+            return new CacheEntry(new ProfilingDynamicMethod(delegated.method), delegated.token);
         }
-    };
+    }
 
     private volatile CacheEntryFactory cacheEntryFactory;
 
     // modifies this class only; used to make the Synchronized module synchronized
     public void becomeSynchronized() {
-        cacheEntryFactory = SynchronizedCacheEntryFactory;
+        cacheEntryFactory = new SynchronizedCacheEntryFactory(cacheEntryFactory);
     }
 
     public boolean isSynchronized() {
-        return cacheEntryFactory == SynchronizedCacheEntryFactory;
+        return cacheEntryFactory.hasCacheEntryFactory(SynchronizedCacheEntryFactory.class);
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
 
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined() && isModule()) {
             method = runtime.getObject().searchMethod(name);
         }
 
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
 
     public IRubyObject newMethod(IRubyObject receiver, String name, boolean bound, Visibility visibility) {
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined() ||
             (visibility != null && method.getVisibility() != visibility)) {
             throw getRuntime().newNameError("undefined method `" + name +
                 "' for class `" + this.getName() + "'", name);
         }
 
         RubyModule implementationModule = method.getImplementationClass();
         RubyModule originModule = this;
         while (originModule != implementationModule && originModule.isSingleton()) {
             originModule = ((MetaClass)originModule).getRealClass();
         }
 
         RubyMethod newMethod;
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
         if (other.getConstantMap() != Collections.EMPTY_MAP) {
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
             block.yieldNonArray(getRuntime().getCurrentContext(), this, this, this);
         }
 
         return getRuntime().getNil();
     }
     
     public void addReadWriteAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), Visibility.PUBLIC, true, true);
     }
     
     public void addReadAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), Visibility.PUBLIC, true, false);
     }
     
     public void addWriteAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), Visibility.PUBLIC, false, true);
     }
 
     /** rb_mod_attr
      *
      */
     @JRubyMethod(name = "attr", required = 1, optional = 1, visibility = PRIVATE, reads = VISIBILITY, compat = CompatVersion.RUBY1_8)
     public IRubyObject attr(ThreadContext context, IRubyObject[] args) {
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
         
         addAccessor(context, args[0].asJavaString().intern(), visibility, true, writeable);
 
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "attr", rest = true, visibility = PRIVATE, reads = VISIBILITY, compat = CompatVersion.RUBY1_9)
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
         return instance_methods(args, PUBLIC, false, true);
     }
 
     @JRubyMethod(name = "instance_method", required = 1)
     public IRubyObject instance_method(IRubyObject symbol) {
         return newMethod(null, symbol.asJavaString(), false, null);
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
diff --git a/src/org/jruby/internal/runtime/methods/ProfilingDynamicMethod.java b/src/org/jruby/internal/runtime/methods/ProfilingDynamicMethod.java
index 004a968e31..cc4a44c35d 100644
--- a/src/org/jruby/internal/runtime/methods/ProfilingDynamicMethod.java
+++ b/src/org/jruby/internal/runtime/methods/ProfilingDynamicMethod.java
@@ -1,239 +1,148 @@
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
 package org.jruby.internal.runtime.methods;
 
-import org.jruby.Ruby;
 import org.jruby.RubyModule;
-import org.jruby.exceptions.JumpException.BreakJump;
-import org.jruby.exceptions.JumpException.ReturnJump;
-import org.jruby.exceptions.RaiseException;
-import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
-import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
-import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
- *
- * @author headius
+ * A DynamicMethod wrapper that performs timed profiling for each call.
  */
-public class ProfilingDynamicMethod extends DynamicMethod {
-    private final DynamicMethod delegate;
-
+public class ProfilingDynamicMethod extends WrapperMethod {
     public ProfilingDynamicMethod(DynamicMethod delegate) {
-        super(delegate.getImplementationClass(), delegate.getVisibility(), delegate.getCallConfig());
-        this.delegate = delegate;
+        super(delegate);
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
         int previousSerial = context.profileEnter((int)getSerialNumber());
         try {
             return delegate.call(context, self, clazz, name);
         } finally {
             context.profileExit(previousSerial);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
         int previousSerial = context.profileEnter((int)getSerialNumber());
         try {
             return delegate.call(context, self, clazz, name, arg0);
         } finally {
             context.profileExit(previousSerial);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
         int previousSerial = context.profileEnter((int)getSerialNumber());
         try {
             return delegate.call(context, self, clazz, name, arg0, arg1);
         } finally {
             context.profileExit(previousSerial);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         int previousSerial = context.profileEnter((int)getSerialNumber());
         try {
             return delegate.call(context, self, clazz, name, arg0, arg1, arg2);
         } finally {
             context.profileExit(previousSerial);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
         int previousSerial = context.profileEnter((int)getSerialNumber());
         try {
             return delegate.call(context, self, clazz, name, args);
         } finally {
             context.profileExit(previousSerial);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
         int previousSerial = context.profileEnter((int)getSerialNumber());
         try {
             return delegate.call(context, self, clazz, name, block);
         } finally {
             context.profileExit(previousSerial);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
         int previousSerial = context.profileEnter((int)getSerialNumber());
         try {
             return delegate.call(context, self, clazz, name, arg0, block);
         } finally {
             context.profileExit(previousSerial);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         int previousSerial = context.profileEnter((int)getSerialNumber());
         try {
             return delegate.call(context, self, clazz, name, arg0, arg1, block);
         } finally {
             context.profileExit(previousSerial);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         int previousSerial = context.profileEnter((int)getSerialNumber());
         try {
             return delegate.call(context, self, clazz, name, arg0, arg1, arg2, block);
         } finally {
             context.profileExit(previousSerial);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
         int previousSerial = context.profileEnter((int)getSerialNumber());
         try {
             return delegate.call(context, self, clazz, name, args, block);
         } finally {
             context.profileExit(previousSerial);
         }
     }
 
     @Override
     public DynamicMethod dup() {
         return new ProfilingDynamicMethod(delegate.dup());
     }
-
-    @Override
-    public void setVisibility(Visibility visibility) {
-        delegate.setVisibility(visibility);
-    }
-
-    @Override
-    public void setNativeCall(Class nativeTarget, String nativeName, Class nativeReturn, Class[] nativeSignature, boolean statik) {
-        delegate.setNativeCall(nativeTarget, nativeName, nativeReturn, nativeSignature, statik);
-    }
-
-    @Override
-    public void setIsBuiltin(boolean isBuiltin) {
-        delegate.setIsBuiltin(isBuiltin);
-    }
-
-    @Override
-    public void setImplementationClass(RubyModule implClass) {
-        delegate.setImplementationClass(implClass);
-    }
-
-    @Override
-    public void setCallConfig(CallConfiguration callConfig) {
-        delegate.setCallConfig(callConfig);
-    }
-
-    @Override
-    public boolean isNative() {
-        return delegate.isNative();
-    }
-
-    @Override
-    public boolean isCallableFrom(IRubyObject caller, CallType callType) {
-        return delegate.isCallableFrom(caller, callType);
-    }
-
-    @Override
-    public boolean isBuiltin() {
-        return delegate.isBuiltin();
-    }
-
-    @Override
-    public Visibility getVisibility() {
-        return delegate.getVisibility();
-    }
-
-    @Override
-    public long getSerialNumber() {
-        return delegate.getSerialNumber();
-    }
-
-    @Override
-    public DynamicMethod getRealMethod() {
-        return delegate.getRealMethod();
-    }
-
-    @Override
-    protected RubyModule getProtectedClass() {
-        return delegate.getProtectedClass();
-    }
-
-    @Override
-    public NativeCall getNativeCall() {
-        return delegate.getNativeCall();
-    }
-
-    @Override
-    public RubyModule getImplementationClass() {
-        return delegate.getImplementationClass();
-    }
-
-    @Override
-    public CallConfiguration getCallConfig() {
-        return delegate.getCallConfig();
-    }
-
-    @Override
-    public Arity getArity() {
-        return delegate.getArity();
-    }
 }
diff --git a/src/org/jruby/internal/runtime/methods/SynchronizedDynamicMethod.java b/src/org/jruby/internal/runtime/methods/SynchronizedDynamicMethod.java
index 79c906aaad..dcd1c45beb 100644
--- a/src/org/jruby/internal/runtime/methods/SynchronizedDynamicMethod.java
+++ b/src/org/jruby/internal/runtime/methods/SynchronizedDynamicMethod.java
@@ -1,60 +1,120 @@
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
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.RubyModule;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  * @author headius
  */
-public class SynchronizedDynamicMethod extends DynamicMethod {
-    private final DynamicMethod delegate;
-
+public class SynchronizedDynamicMethod extends WrapperMethod {
     public SynchronizedDynamicMethod(DynamicMethod delegate) {
-        super(delegate.getImplementationClass(), delegate.getVisibility(), delegate.getCallConfig());
-        this.delegate = delegate;
+        super(delegate);
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
+        synchronized (self) {
+            return delegate.call(context, self, clazz, name);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
+        synchronized (self) {
+            return delegate.call(context, self, clazz, name, arg0);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
+        synchronized (self) {
+            return delegate.call(context, self, clazz, name, arg0, arg1);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
+        synchronized (self) {
+            return delegate.call(context, self, clazz, name, arg0, arg1, arg2);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
+        synchronized (self) {
+            return delegate.call(context, self, clazz, name, args);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
+        synchronized (self) {
+            return delegate.call(context, self, clazz, name, block);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
+        synchronized (self) {
+            return delegate.call(context, self, clazz, name, arg0, block);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
+        synchronized (self) {
+            return delegate.call(context, self, clazz, name, arg0, arg1, block);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
+        synchronized (self) {
+            return delegate.call(context, self, clazz, name, arg0, arg1, arg2, block);
+        }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
         synchronized (self) {
             return delegate.call(context, self, clazz, name, args, block);
         }
     }
 
     @Override
     public DynamicMethod dup() {
-        return new SynchronizedDynamicMethod(delegate);
+        return new SynchronizedDynamicMethod(delegate.dup());
     }
 
 }
diff --git a/src/org/jruby/internal/runtime/methods/WrapperMethod.java b/src/org/jruby/internal/runtime/methods/WrapperMethod.java
index 7a7531fb14..33e99b81d7 100644
--- a/src/org/jruby/internal/runtime/methods/WrapperMethod.java
+++ b/src/org/jruby/internal/runtime/methods/WrapperMethod.java
@@ -1,100 +1,196 @@
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
+import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
+import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * 
  * @author jpetersen
  */
 public class WrapperMethod extends DynamicMethod {
-    private DynamicMethod method;
+    protected final DynamicMethod delegate;
+
+    public WrapperMethod(DynamicMethod delegate) {
+        super(delegate.getImplementationClass(), delegate.getVisibility(), delegate.getCallConfig());
+        this.delegate = delegate;
+    }
 
     /**
      * Constructor for WrapperCallable.
      * @param visibility
      */
     public WrapperMethod(RubyModule implementationClass, DynamicMethod method, Visibility visibility) {
-        super(implementationClass, visibility, null);
-        this.method = method;
+        super(implementationClass, visibility, CallConfiguration.FrameNoneScopeNone);
+        this.delegate = method;
+    }
+
+    public DynamicMethod getDelegate() {
+        return delegate;
     }
     
+    @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name) {
-        return method.call(context, self, klazz, name);
+        return delegate.call(context, self, klazz, name);
     }
     
+    @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg) {
-        return method.call(context, self, klazz, name, arg);
+        return delegate.call(context, self, klazz, name, arg);
     }
     
+    @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2) {
-        return method.call(context, self, klazz, name, arg1, arg2);
+        return delegate.call(context, self, klazz, name, arg1, arg2);
     }
     
+    @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
-        return method.call(context, self, klazz, name, arg1, arg2, arg3);
+        return delegate.call(context, self, klazz, name, arg1, arg2, arg3);
     }
     
+    @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject[] args) {
-        return method.call(context, self, klazz, name, args);
+        return delegate.call(context, self, klazz, name, args);
     }
     
+    @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, Block block) {
-        return method.call(context, self, klazz, name, block);
+        return delegate.call(context, self, klazz, name, block);
     }
     
+    @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, Block block) {
-        return method.call(context, self, klazz, name, arg1, block);
+        return delegate.call(context, self, klazz, name, arg1, block);
     }
     
+    @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2, Block block) {
-        return method.call(context, self, klazz, name, arg1, arg2, block);
+        return delegate.call(context, self, klazz, name, arg1, arg2, block);
     }
     
+    @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
-        return method.call(context, self, klazz, name, arg1, arg2, arg3, block);
+        return delegate.call(context, self, klazz, name, arg1, arg2, arg3, block);
     }
     
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject[] args, Block block) {
-        return method.call(context, self, klazz, name, args, block);
+        return delegate.call(context, self, klazz, name, args, block);
     }
     
     public DynamicMethod dup() {
-        return new WrapperMethod(getImplementationClass(), method, getVisibility());
+        return new WrapperMethod(getImplementationClass(), delegate, getVisibility());
+    }
+
+    @Override
+    public void setVisibility(Visibility visibility) {
+        delegate.setVisibility(visibility);
+    }
+
+    @Override
+    public void setNativeCall(Class nativeTarget, String nativeName, Class nativeReturn, Class[] nativeSignature, boolean statik) {
+        delegate.setNativeCall(nativeTarget, nativeName, nativeReturn, nativeSignature, statik);
+    }
+
+    @Override
+    public void setIsBuiltin(boolean isBuiltin) {
+        delegate.setIsBuiltin(isBuiltin);
+    }
+
+    @Override
+    public void setImplementationClass(RubyModule implClass) {
+        delegate.setImplementationClass(implClass);
+    }
+
+    @Override
+    public void setCallConfig(CallConfiguration callConfig) {
+        delegate.setCallConfig(callConfig);
+    }
+
+    @Override
+    public boolean isNative() {
+        return delegate.isNative();
+    }
+
+    @Override
+    public boolean isCallableFrom(IRubyObject caller, CallType callType) {
+        return delegate.isCallableFrom(caller, callType);
+    }
+
+    @Override
+    public boolean isBuiltin() {
+        return delegate.isBuiltin();
+    }
+
+    @Override
+    public Visibility getVisibility() {
+        return delegate.getVisibility();
     }
 
+    @Override
     public long getSerialNumber() {
-        return method.getSerialNumber();
+        return delegate.getSerialNumber();
+    }
+
+    @Override
+    public DynamicMethod getRealMethod() {
+        return delegate.getRealMethod();
+    }
+
+    @Override
+    protected RubyModule getProtectedClass() {
+        return delegate.getProtectedClass();
+    }
+
+    @Override
+    public NativeCall getNativeCall() {
+        return delegate.getNativeCall();
+    }
+
+    @Override
+    public RubyModule getImplementationClass() {
+        return delegate.getImplementationClass();
+    }
+
+    @Override
+    public CallConfiguration getCallConfig() {
+        return delegate.getCallConfig();
+    }
+
+    @Override
+    public Arity getArity() {
+        return delegate.getArity();
     }
 }
diff --git a/test/jruby_index b/test/jruby_index
index 303c94263c..d014246d10 100644
--- a/test/jruby_index
+++ b/test/jruby_index
@@ -1,126 +1,127 @@
 # Our own test/unit-based tests
 # NOTE: test_globals comes first here because it has tests that $? be nil
 test_globals
 # NOTE: test_object_class_default_methods comes early because it depends
 #       on no additional methods existing on the Object class
 test_object_class_default_methods
 test_argf
 test_array
 test_array_subclass_behavior
 test_autoload
 test_backquote
 test_backtraces
 test_big_decimal
 test_bignum
 test_binding_eval_yield
 test_block
 test_block_arg_processing
 test_cache_map_leak
 test_caller
 test_case
 test_class
 test_command_line_switches
 test_comparable
 test_core_arities
 test_crazy_blocks
 test_custom_enumerable
 test_cvars_in_odd_scopes
 test_date_time
 test_defined
 test_default_constants
 test_delegated_array_equals
 test_dir
 test_digest_lazy_load
 test_digest_extend
 test_digest2
 test_dup_clone_taint_freeze
 test_env
 test_etc
 test_eval
 test_eval_with_binding
 test_file
 test_flip
 test_frame_self
 test_hash
 test_higher_javasupport
 test_iconv
 test_included_in_object_space
 test_ivar_table_integrity
 test_io
 test_line_endings
 test_load
 test_math
 test_method
 test_method_cache
 test_method_override_and_caching
 test_nkf
 test_java_accessible_object
 test_java_extension
 test_java_wrapper_deadlock
 test_jruby_internals
 test_jruby_4084
 compiler/test_jrubyc
 test_launching_by_shell_script
 #test_local_jump_error
 test_marshal_with_instance_variables
 test_marshal_gemspec
 test_method_missing
 test_methods
 test_no_stack_trace_stomp
 test_pack
 test_primitive_to_java
 test_process
 test_proc_visibility
 test_parsing
 test_random
 test_rbconfig
 test_require_once
 test_respond_to
 test_ri
 test_socket
 test_string_java_bytes
 test_string_printf
 test_string_sub
 test_string_to_number
 test_super_call_site_caching
 test_symbol
 test_tb_yaml
 test_timeout
 test_thread
 test_thread_backtrace
 test_threaded_nonlocal_return
 test_time_nil_ops
 test_unicode_paths
 test_unmarshal
 test_variables
 test_vietnamese_charset
 #test_trace_func
 test_win32
 test_zlib
 test_yaml
 test_system_error
 
 # these tests are last because they pull in libraries that can affect others
 test_base64_strangeness
 test_loading_builtin_libraries
 test_load_compiled_ruby_class_from_classpath
 test_null_channel
 test_irubyobject_java_passing
 test_jruby_object_input_stream
 test_jar_on_load_path
 test_jruby_ext
 test_jruby_core_ext
 test_thread_context_frame_dereferences_unreachable_variables
 test_context_classloader
 test_rexml_document
 test_load_compiled_ruby
 test_openssl_stub
 test_missing_jruby_home
 test_ast_inspector
 test_jarred_gems_with_spaces_in_directory
 test_weak_drb_id_conv
 test_kernel
 test_dir_with_plusses
 test_jar_file
 # Not sure how to test this, since soft references take too long to collect
 #test_thread_service
+test_jruby_synchronized
diff --git a/test/test_jruby_synchronized.rb b/test/test_jruby_synchronized.rb
new file mode 100644
index 0000000000..4c03a0921b
--- /dev/null
+++ b/test/test_jruby_synchronized.rb
@@ -0,0 +1,13 @@
+require 'test/unit'
+require 'jruby/synchronized'
+
+class TestJrubySynchronized < Test::Unit::TestCase
+  # JRUBY-5168
+  def test_to_s_on_synchronized_array
+    a=[Object.new]
+    a[0].extend(JRuby::Synchronized)
+    assert_nothing_raised do
+      a.to_s
+    end
+  end
+end
