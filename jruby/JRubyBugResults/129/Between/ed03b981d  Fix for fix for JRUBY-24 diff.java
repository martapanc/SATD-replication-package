diff --git a/lib/ruby/site_ruby/1.8/builtin/javasupport/java.rb b/lib/ruby/site_ruby/1.8/builtin/javasupport/java.rb
index 912c42f66e..c11a5dbba6 100644
--- a/lib/ruby/site_ruby/1.8/builtin/javasupport/java.rb
+++ b/lib/ruby/site_ruby/1.8/builtin/javasupport/java.rb
@@ -1,59 +1,62 @@
 module Java
  class << self
    def const_missing(sym)
      result = JavaUtilities.get_top_level_proxy_or_package(sym)
      if const_defined? sym
        result
      else
        const_set(sym, result)
      end
    end
 
    def method_missing(sym, *args)
      raise ArgumentError, "wrong number of arguments (#{args.length} for 0)" unless args.empty?
      JavaUtilities.get_top_level_proxy_or_package sym
    end
  end
 
 end
  
-module JavaPackageModuleTemplate  
+module JavaPackageModuleTemplate
+  # don't undefine important methods
   @@keep = /^(__|<|>|=)|^(class|const_missing|inspect|method_missing|to_s)$|(\?|!|=)$/
+  # don't alias "special" methods
+  @@no_alias = /^(eval|module_eval|class_eval|instance_eval|instance_exec|binding|local_variables)$/
   class << self
   
     # blank-slate logic relocated from org.jruby.javasupport.Java
     instance_methods.each do |meth|
       unless meth.to_s =~ @@keep
         # keep aliased methods for those we'll undef, for use by IRB and other utilities
-        unless method_defined?(method_alias = :"__#{meth}__")
+        unless meth.to_s =~ @@no_alias || method_defined?(method_alias = :"__#{meth}__")
           alias_method method_alias, meth
         end
         undef_method meth  
       end
     end
     
     def __block__(name)
       if (name_str = name.to_s) !~ @@keep 
         (class<<self;self;end).__send__(:undef_method, name) rescue nil
       end
     end
 
     def const_missing(const)
       JavaUtilities.get_proxy_class(@package_name + const.to_s)
     end
     private :const_missing
     
     def method_missing(sym, *args)
       raise ArgumentError, "wrong number of arguments (#{args.length} for 0)" unless args.empty?
       JavaUtilities.get_proxy_or_package_under_package self, sym
     end
     private :method_missing
 
     def package_name
       # strip off trailing .
       @package_name[0..-2]
     end
   end
 end
 # pull in the default package
 JavaUtilities.get_package_module("Default")
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 56e1dcb8e5..e256283730 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1110 +1,1111 @@
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
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.FullFunctionCallbackMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
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
 import org.jruby.runtime.builtin.InstanceVariableTable;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.callsite.CacheEntry;
 import org.jruby.runtime.component.VariableEntry;
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
+            "class_eval",
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
         if (includingHierarchies == null) includingHierarchies = new WeakHashSet<RubyClass>();
         includingHierarchies.add(hierarchy);
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
