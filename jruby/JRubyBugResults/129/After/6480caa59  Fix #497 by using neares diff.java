diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index f2e3b33c4f..57de8188b9 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1485 +1,1485 @@
 /*
  **** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
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
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jruby.internal.runtime.methods.AttrWriterMethod;
 import org.jruby.internal.runtime.methods.AttrReaderMethod;
 import static org.jruby.anno.FrameField.VISIBILITY;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.security.AccessControlException;
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
 import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
 
 import org.jruby.anno.AnnotationBinder;
 
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
 import org.jruby.internal.runtime.methods.CacheableMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.ProfilingDynamicMethod;
 import org.jruby.internal.runtime.methods.SynchronizedDynamicMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.runtime.Helpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.callsite.CacheEntry;
 import org.jruby.runtime.callsite.FunctionalCachingCallSite;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.runtime.opto.Invalidator;
 import org.jruby.runtime.opto.OptoFactory;
 import org.jruby.util.ClassProvider;
 import org.jruby.util.IdUtil;
 import org.jruby.util.collections.WeakHashSet;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Module")
 public class RubyModule extends RubyObject {
 
     private static final Logger LOG = LoggerFactory.getLogger("RubyModule");
 
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
         @Override
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
 
     public void checkValidBindTargetFrom(ThreadContext context, RubyModule originModule) throws RaiseException {
         if (!this.hasModuleInHierarchy(originModule)) {
             if (originModule instanceof MetaClass) {
                 throw context.runtime.newTypeError("can't bind singleton method to a different class");
             } else {
                 throw context.runtime.newTypeError("bind argument must be an instance of " + originModule.getName());
             }
         }
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
 
     public Map<String, ConstantEntry> getConstantMap() {
         return constants;
     }
 
     public synchronized Map<String, ConstantEntry> getConstantMapForWrite() {
         return constants == Collections.EMPTY_MAP ? constants = new ConcurrentHashMap<String, ConstantEntry>(4, 0.9f, 1) : constants;
     }
     
     /**
      * AutoloadMap must be accessed after checking ConstantMap. Checking UNDEF value in constantMap works as a guard.
      * For looking up constant, check constantMap first then try to get an Autoload object from autoloadMap.
      * For setting constant, update constantMap first and remove an Autoload object from autoloadMap.
      */
     private Map<String, Autoload> getAutoloadMap() {
         return autoloads;
     }
     
     private synchronized Map<String, Autoload> getAutoloadMapForWrite() {
         return autoloads == Collections.EMPTY_MAP ? autoloads = new ConcurrentHashMap<String, Autoload>(4, 0.9f, 1) : autoloads;
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
         generationObject = generation = runtime.getNextModuleGeneration();
         if (runtime.getInstanceConfig().isProfiling()) {
             cacheEntryFactory = new ProfilingCacheEntryFactory(NormalCacheEntryFactory);
         } else {
             cacheEntryFactory = NormalCacheEntryFactory;
         }
         
         // set up an invalidator for use in new optimization strategies
         methodInvalidator = OptoFactory.newMethodInvalidator(this);
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
 
     public void setSuperClass(RubyClass superClass) {
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
-            StringBuilder anonBase = new StringBuilder(isClass() ? "#<Class:0x" : "#<Module:0x");
+            StringBuilder anonBase = new StringBuilder("#<" + metaClass.getRealClass().getName() + ":0x");
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
             if (DEBUG) LOG.info("trace mode, using default populator");
             populator = TypePopulator.DEFAULT;
         } else {
             try {
                 String qualifiedName = "org.jruby.gen." + clazz.getCanonicalName().replace('.', '$');
 
                 if (DEBUG) LOG.info("looking for " + qualifiedName + AnnotationBinder.POPULATOR_SUFFIX);
 
                 Class populatorClass = Class.forName(qualifiedName + AnnotationBinder.POPULATOR_SUFFIX);
                 populator = (TypePopulator)populatorClass.newInstance();
             } catch (Throwable t) {
                 if (DEBUG) LOG.info("Could not find it, using default populator");
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
             CompatVersion compatVersion = getRuntime().getInstanceConfig().getCompatVersion();
             if (shouldBindMethod(compatVersion, desc.anno.compat())) {
                 DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, methods);
                 define(this, desc, dynamicMethod);
             
                 return true;
             }
             
             return false;
         }
     }
     
     public boolean defineAnnotatedMethod(Method method, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
         if (jrubyMethod == null) return false;
 
         CompatVersion compatVersion = getRuntime().getInstanceConfig().getCompatVersion();
         if (shouldBindMethod(compatVersion, jrubyMethod.compat())) {
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
 
         CompatVersion compatVersion = getRuntime().getInstanceConfig().getCompatVersion();
         if (shouldBindMethod(compatVersion, jrubyMethod.compat())) {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
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
 
             return cacheEntryFactoryClass.isAssignableFrom(current.getClass());
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
         @Override
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             return new CacheEntry(method, token);
         }
     };
 
     protected static class SynchronizedCacheEntryFactory extends WrapperCacheEntryFactory {
         public SynchronizedCacheEntryFactory(CacheEntryFactory previous) {
             super(previous);
         }
         @Override
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
                 getRuntime().reopenFixnum();
             } else if (this == getRuntime().getFloat()) {
                 getRuntime().reopenFloat();
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
      * Find the given class in this hierarchy, considering modules along the way.
      * 
      * @param clazz the class to find
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
 
     /** rb_alias
      *
      */
     public synchronized void defineAlias(String name, String oldName) {
         testFrozen("module");
         if (oldName.equals(name)) return;
 
         DynamicMethod method = searchForAliasMethod(getRuntime(), oldName);
 
         putMethod(name, new AliasMethod(this, method, oldName));
 
         invalidateCoreClasses();
         invalidateCacheDescendants();
     }
 
     public synchronized void defineAliases(List<String> aliases, String oldName) {
         testFrozen("module");
         DynamicMethod method = searchForAliasMethod(getRuntime(), oldName);
 
         for (String name: aliases) {
             if (oldName.equals(name)) continue;
 
             putMethod(name, new AliasMethod(this, method, oldName));
         }
         
         invalidateCoreClasses();
         invalidateCacheDescendants();
     }
     
     private DynamicMethod searchForAliasMethod(Ruby runtime, String name) {
         // JRUBY-2435: Aliasing eval and other "special" methods should display a warning
         // We warn because we treat certain method names as "special" for purposes of
         // optimization. Hopefully this will be enough to convince people not to alias
         // them.
         if (SCOPE_CAPTURING_METHODS.contains(name)) {
             runtime.getWarnings().warn("`" + name + "' should not be aliased");
         }  
       
         return deepMethodSearch(name, runtime);
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
 
         if (!(IdUtil.isLocal(internedName) || IdUtil.isConstant(internedName))) {
             throw runtime.newNameError("invalid attribute name", internedName);
         }
 
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
         
         Helpers.addInstanceMethod(this, name, newMethod, visibility, context, runtime);
 
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
             
             checkValidBindTargetFrom(context, (RubyModule)method.owner(context));
             
             newMethod = method.unbind().getMethod().dup();
             newMethod.setImplementationClass(this);
         } else {
             throw runtime.newTypeError("wrong argument type " + arg1.getType().getName() + " (expected Proc/Method)");
         }
         
         Helpers.addInstanceMethod(this, name, newMethod, visibility, context, runtime);
 
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
