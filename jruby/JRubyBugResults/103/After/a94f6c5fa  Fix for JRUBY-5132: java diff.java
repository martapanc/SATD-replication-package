diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index 7e71976704..c9c1820bae 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,1166 +1,1168 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 package org.jruby.javasupport;
 
 import org.jruby.java.invokers.StaticFieldGetter;
 import org.jruby.java.invokers.StaticMethodInvoker;
 import org.jruby.java.invokers.InstanceFieldGetter;
 import org.jruby.java.invokers.InstanceFieldSetter;
 import org.jruby.java.invokers.InstanceMethodInvoker;
 import org.jruby.java.invokers.StaticFieldSetter;
 import java.io.ByteArrayOutputStream;
 import java.io.InputStream;
 import java.io.IOException;
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.ReflectPermission;
 import java.security.AccessControlException;
 import java.security.AccessController;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.locks.ReentrantLock;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodZero;
 import org.jruby.java.addons.ArrayJavaAddons;
 import org.jruby.java.proxies.ArrayJavaProxy;
 import org.jruby.java.invokers.ConstructorInvoker;
 import org.jruby.java.proxies.ConcreteJavaProxy;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.ByteList;
 import org.jruby.util.IdUtil;
 import org.jruby.util.SafePropertyAccessor;
 
 
 @JRubyClass(name="Java::JavaClass", parent="Java::JavaObject")
 public class JavaClass extends JavaObject {
     public static final String METHOD_MANGLE = "__method";
 
     public static final boolean CAN_SET_ACCESSIBLE;
 
     static {
         boolean canSetAccessible = false;
 
         if (RubyInstanceConfig.CAN_SET_ACCESSIBLE) {
             try {
                 AccessController.checkPermission(new ReflectPermission("suppressAccessChecks"));
                 canSetAccessible = true;
             } catch (Throwable t) {
                 // added this so if things are weird in the future we can debug without
                 // spinning a new binary
                 if (SafePropertyAccessor.getBoolean("jruby.ji.logCanSetAccessible")) {
                     t.printStackTrace();
                 }
 
                 // assume any exception means we can't suppress access checks
                 canSetAccessible = false;
             }
         }
 
         CAN_SET_ACCESSIBLE = canSetAccessible;
     }
     
     /**
      * Assigned names only override based priority of an assigned type, the type must be less than
      * or equal to the assigned type. For example, field name (FIELD) in a subclass will override
      * an alias (ALIAS) in a superclass, but not a method (METHOD).
      */
     private enum Priority {
         RESERVED(0), METHOD(1), FIELD(2), PROTECTED_METHOD(3),
         WEAKLY_RESERVED(4), ALIAS(5), PROTECTED_FIELD(6);
 
         private int value;
 
         Priority(int value) {
             this.value = value;
         }
 
         public boolean asImportantAs(AssignedName other) {
             return other != null && other.type.value == value;
         }
         
         public boolean lessImportantThan(AssignedName other) {
             return other != null && other.type.value < value;
         }
         
         public boolean moreImportantThan(AssignedName other) {
             return other == null || other.type.value > value;
         }
     }
 
     private static class AssignedName {
         String name;
         Priority type;
         
         AssignedName () {}
         AssignedName(String name, Priority type) {
             this.name = name;
             this.type = type;
         }
     }
 
     // TODO: other reserved names?
     private static final Map<String, AssignedName> RESERVED_NAMES = new HashMap<String, AssignedName>();
     static {
         RESERVED_NAMES.put("__id__", new AssignedName("__id__", Priority.RESERVED));
         RESERVED_NAMES.put("__send__", new AssignedName("__send__", Priority.RESERVED));
+        // JRUBY-5132: java.awt.Component.instance_of?() expects 2 args
+        RESERVED_NAMES.put("instance_of?", new AssignedName("instance_of?", Priority.RESERVED));
     }
     private static final Map<String, AssignedName> STATIC_RESERVED_NAMES = new HashMap<String, AssignedName>(RESERVED_NAMES);
     static {
         STATIC_RESERVED_NAMES.put("new", new AssignedName("new", Priority.RESERVED));
     }
     private static final Map<String, AssignedName> INSTANCE_RESERVED_NAMES = new HashMap<String, AssignedName>(RESERVED_NAMES);
     static {
         // only possible for "getClass" to be an instance method in Java
         INSTANCE_RESERVED_NAMES.put("class", new AssignedName("class", Priority.RESERVED));
         // "initialize" has meaning only for an instance (as opposed to a class)
         INSTANCE_RESERVED_NAMES.put("initialize", new AssignedName("initialize", Priority.RESERVED));
     }
 
     private static abstract class NamedInstaller {
         static final int STATIC_FIELD = 1;
         static final int STATIC_METHOD = 2;
         static final int INSTANCE_FIELD = 3;
         static final int INSTANCE_METHOD = 4;
         static final int CONSTRUCTOR = 5;
         String name;
         int type;
         Visibility visibility = Visibility.PUBLIC;
         NamedInstaller () {}
         NamedInstaller (String name, int type) {
             this.name = name;
             this.type = type;
         }
         abstract void install(RubyModule proxy);
         // small hack to save a cast later on
         boolean hasLocalMethod() {
             return true;
         }
         boolean isPublic() {
             return visibility == Visibility.PUBLIC;
         }
         boolean isProtected() {
             return visibility == Visibility.PROTECTED;
         }
     }
 
     private static abstract class FieldInstaller extends NamedInstaller {
         Field field;
         FieldInstaller(){}
         FieldInstaller(String name, int type, Field field) {
             super(name,type);
             this.field = field;
         }
     }
 
     private static class StaticFieldGetterInstaller extends FieldInstaller {
         StaticFieldGetterInstaller(){}
         StaticFieldGetterInstaller(String name, Field field) {
             super(name,STATIC_FIELD,field);
         }
         void install(RubyModule proxy) {
             if (Modifier.isPublic(field.getModifiers())) {
                 proxy.getSingletonClass().addMethod(name, new StaticFieldGetter(name, proxy, field));
             }
         }
     }
 
     private static class StaticFieldSetterInstaller extends FieldInstaller {
         StaticFieldSetterInstaller(){}
         StaticFieldSetterInstaller(String name, Field field) {
             super(name,STATIC_FIELD,field);
         }
         void install(RubyModule proxy) {
             if (Modifier.isPublic(field.getModifiers())) {
                 proxy.getSingletonClass().addMethod(name, new StaticFieldSetter(name, proxy, field));
             }
         }
     }
 
     private static class InstanceFieldGetterInstaller extends FieldInstaller {
         InstanceFieldGetterInstaller(){}
         InstanceFieldGetterInstaller(String name, Field field) {
             super(name,INSTANCE_FIELD,field);
         }
         void install(RubyModule proxy) {
             if (Modifier.isPublic(field.getModifiers())) {
                 proxy.addMethod(name, new InstanceFieldGetter(name, proxy, field));
             }
         }
     }
 
     private static class InstanceFieldSetterInstaller extends FieldInstaller {
         InstanceFieldSetterInstaller(){}
         InstanceFieldSetterInstaller(String name, Field field) {
             super(name,INSTANCE_FIELD,field);
         }
         void install(RubyModule proxy) {
             if (Modifier.isPublic(field.getModifiers())) {
                 proxy.addMethod(name, new InstanceFieldSetter(name, proxy, field));
             }
         }
     }
 
     private static abstract class MethodInstaller extends NamedInstaller {
         private boolean haveLocalMethod;
         protected List<Method> methods;
         protected List<String> aliases;
         MethodInstaller(){}
         MethodInstaller(String name, int type) {
             super(name,type);
         }
 
         // called only by initializing thread; no synchronization required
         void addMethod(Method method, Class<?> javaClass) {
             if (methods == null) {
                 methods = new ArrayList<Method>(4);
             }
             methods.add(method);
             haveLocalMethod |= javaClass == method.getDeclaringClass() ||
                     method.getDeclaringClass().isInterface();
         }
 
         // called only by initializing thread; no synchronization required
         void addAlias(String alias) {
             if (aliases == null) {
                 aliases = new ArrayList<String>(4);
             }
             if (!aliases.contains(alias)) {
                 aliases.add(alias);
             }
         }
 
         // modified only by addMethod; no synchronization required
         @Override
         boolean hasLocalMethod () {
             return haveLocalMethod;
         }
     }
 
     private static class ConstructorInvokerInstaller extends MethodInstaller {
         private boolean haveLocalConstructor;
         protected List<Constructor> constructors;
         
         ConstructorInvokerInstaller(String name) {
             super(name,STATIC_METHOD);
         }
 
         // called only by initializing thread; no synchronization required
         void addConstructor(Constructor ctor, Class<?> javaClass) {
             if (constructors == null) {
                 constructors = new ArrayList<Constructor>(4);
             }
             if (!Ruby.isSecurityRestricted()) {
                 try {
                     ctor.setAccessible(true);
                 } catch(SecurityException e) {}
             }
             constructors.add(ctor);
             haveLocalConstructor |= javaClass == ctor.getDeclaringClass();
         }
         
         void install(RubyModule proxy) {
             if (haveLocalConstructor) {
                 DynamicMethod method = new ConstructorInvoker(proxy, constructors);
                 proxy.addMethod(name, method);
             } else {
                 // if there's no constructor, we must prevent construction
                 proxy.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod() {
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                         throw context.getRuntime().newTypeError("no public constructors for " + clazz);
                     }
                 });
             }
         }
     }
 
     private static class StaticMethodInvokerInstaller extends MethodInstaller {
         StaticMethodInvokerInstaller(String name) {
             super(name,STATIC_METHOD);
         }
 
         void install(RubyModule proxy) {
             if (hasLocalMethod()) {
                 RubyClass singleton = proxy.getSingletonClass();
                 DynamicMethod method = new StaticMethodInvoker(singleton, methods);
                 singleton.addMethod(name, method);
                 if (aliases != null && isPublic() ) {
                     singleton.defineAliases(aliases, this.name);
                     aliases = null;
                 }
             }
         }
     }
 
     private static class InstanceMethodInvokerInstaller extends MethodInstaller {
         InstanceMethodInvokerInstaller(String name) {
             super(name,INSTANCE_METHOD);
         }
         void install(RubyModule proxy) {
             if (hasLocalMethod()) {
                 DynamicMethod method = new InstanceMethodInvoker(proxy, methods);
                 proxy.addMethod(name, method);
                 if (aliases != null && isPublic()) {
                     proxy.defineAliases(aliases, this.name);
                     aliases = null;
                 }
             }
         }
     }
 
     private static class ConstantField {
         static final int CONSTANT = Modifier.FINAL | Modifier.PUBLIC | Modifier.STATIC;
         final Field field;
         ConstantField(Field field) {
             this.field = field;
         }
         void install(final RubyModule proxy) {
             if (proxy.getConstantAt(field.getName()) == null) {
                 // TODO: catch exception if constant is already set by other
                 // thread
                 try {
                     proxy.setConstant(field.getName(), JavaUtil.convertJavaToUsableRubyObject(proxy.getRuntime(), field.get(null)));
                 } catch (IllegalAccessException iae) {
                     // if we can't read it, we don't set it
                 }
             }
         }
         static boolean isConstant(final Field field) {
             return (field.getModifiers() & CONSTANT) == CONSTANT &&
                 Character.isUpperCase(field.getName().charAt(0));
         }
     }
     
     private final RubyModule JAVA_UTILITIES = getRuntime().getJavaSupport().getJavaUtilitiesModule();
     
     private Map<String, AssignedName> staticAssignedNames;
     private Map<String, AssignedName> instanceAssignedNames;
     private Map<String, NamedInstaller> staticInstallers;
     private Map<String, NamedInstaller> instanceInstallers;
     private ConstructorInvokerInstaller constructorInstaller;
     private List<ConstantField> constantFields;
     // caching constructors, as they're accessed for each new instance
     private volatile RubyArray constructors;
     
     private volatile ArrayList<IRubyObject> proxyExtenders;
 
     // proxy module for interfaces
     private volatile RubyModule proxyModule;
 
     // proxy class for concrete classes.  also used for
     // "concrete" interfaces, which is why we have two fields
     private volatile RubyClass proxyClass;
 
     // readable only by thread building proxy, so don't need to be
     // volatile. used to handle recursive calls to getProxyClass/Module
     // while proxy is being constructed (usually when a constant
     // defined by a class is of the same type as that class).
     private RubyModule unfinishedProxyModule;
     private RubyClass unfinishedProxyClass;
     
     private final ReentrantLock proxyLock = new ReentrantLock();
 
     private final Initializer initializer;
     
     public RubyModule getProxyModule() {
         // allow proxy to be read without synchronization. if proxy
         // is under construction, only the building thread can see it.
         RubyModule proxy;
         if ((proxy = proxyModule) != null) {
             // proxy is complete, return it
             return proxy;
         } else if (proxyLock.isHeldByCurrentThread()) {
             // proxy is under construction, building thread can
             // safely read non-volatile value
             return unfinishedProxyModule; 
         }
         return null;
     }
     
     public RubyClass getProxyClass() {
         // allow proxy to be read without synchronization. if proxy
         // is under construction, only the building thread can see it.
         RubyClass proxy;
         if ((proxy = proxyClass) != null) {
             // proxy is complete, return it
             return proxy;
         } else if (proxyLock.isHeldByCurrentThread()) {
             // proxy is under construction, building thread can
             // safely read non-volatile value
             return unfinishedProxyClass; 
         }
         return null;
     }
     
     public void lockProxy() {
         proxyLock.lock();
     }
     
     public void unlockProxy() {
         proxyLock.unlock();
     }
 
     private Map<String, AssignedName> getStaticAssignedNames() {
         return staticAssignedNames;
     }
     private Map<String, AssignedName> getInstanceAssignedNames() {
         return instanceAssignedNames;
     }
     
     private JavaClass(Ruby runtime, Class<?> javaClass) {
         super(runtime, (RubyClass) runtime.getJavaSupport().getJavaClassClass(), javaClass);
         if (javaClass.isInterface()) {
             initializer = new InterfaceInitializer(javaClass);
         } else if (!(javaClass.isArray() || javaClass.isPrimitive())) {
             initializer = new ClassInitializer(javaClass);
         } else {
             initializer = DUMMY_INITIALIZER;
         }
     }
     
     @Override
     public boolean equals(Object other) {
         return other instanceof JavaClass &&
             this.getValue() == ((JavaClass)other).getValue();
     }
 
     @Override
     public int hashCode() {
         return javaClass().hashCode();
     }
 
     private interface Initializer {
         public void initialize();
     }
 
     private class InterfaceInitializer implements Initializer {
         private volatile boolean hasRun = false;
         private final Class javaClass;
 
         public InterfaceInitializer(Class<?> javaClass) {
             this.javaClass = javaClass;
         }
         
         public synchronized void initialize() {
             if (hasRun) return;
             hasRun = true;
 
             Map<String, AssignedName> staticNames  = new HashMap<String, AssignedName>(STATIC_RESERVED_NAMES);
             List<ConstantField> constants = new ArrayList<ConstantField>();
             Map<String, NamedInstaller> staticCallbacks = new HashMap<String, NamedInstaller>();
             Field[] fields = getDeclaredFields(javaClass);
 
             for (int i = fields.length; --i >= 0; ) {
                 Field field = fields[i];
                 if (javaClass != field.getDeclaringClass()) continue;
                 if (ConstantField.isConstant(field)) constants.add(new ConstantField(field));
 
                 int modifiers = field.getModifiers();
                 if (Modifier.isStatic(modifiers)) addField(staticCallbacks, staticNames, field, Modifier.isFinal(modifiers), true);
             }
 
             // Now add all aliases for the static methods (fields) as appropriate
             for (Map.Entry<String, NamedInstaller> entry : staticCallbacks.entrySet()) {
                 if (entry.getValue().type == NamedInstaller.STATIC_METHOD && entry.getValue().hasLocalMethod()) {
                     assignAliases((MethodInstaller)entry.getValue(), staticNames);
                 }
             }
 
             JavaClass.this.staticAssignedNames = staticNames;
             JavaClass.this.staticInstallers = staticCallbacks;
             JavaClass.this.constantFields = constants;
         }
     };
 
     private class ClassInitializer implements Initializer {
         private volatile boolean hasRun = false;
         private final Class javaClass;
 
         public ClassInitializer(Class<?> javaClass) {
             this.javaClass = javaClass;
         }
 
         public synchronized void initialize() {
             if (hasRun) return;
             hasRun = true;
             
             Class<?> superclass = javaClass.getSuperclass();
 
             InitializerState state = new InitializerState(getRuntime(), superclass);
 
             setupClassFields(javaClass, state);
             setupClassMethods(javaClass, state);
             setupClassConstructors(javaClass);
 
             JavaClass.this.staticAssignedNames = Collections.unmodifiableMap(state.staticNames);
             JavaClass.this.instanceAssignedNames = Collections.unmodifiableMap(state.instanceNames);
             JavaClass.this.staticInstallers = Collections.unmodifiableMap(state.staticCallbacks);
             JavaClass.this.instanceInstallers = Collections.unmodifiableMap(state.instanceCallbacks);
             JavaClass.this.constantFields = Collections.unmodifiableList(state.constantFields);
         }
     }
 
     private static class InitializerState {
         public final Map<String, AssignedName> staticNames;
         public final Map<String, AssignedName> instanceNames;
         public final Map<String, NamedInstaller> staticCallbacks = new HashMap<String, NamedInstaller>();
         public final Map<String, NamedInstaller> instanceCallbacks = new HashMap<String, NamedInstaller>();
         public final List<ConstantField> constantFields = new ArrayList<ConstantField>();
 
         public InitializerState(Ruby runtime, Class superclass) {
             if (superclass == null) {
                 staticNames = new HashMap<String, AssignedName>();
                 instanceNames = new HashMap<String, AssignedName>();
             } else {
                 JavaClass superJavaClass = get(runtime,superclass);
                 staticNames = new HashMap<String, AssignedName>(superJavaClass.getStaticAssignedNames());
                 instanceNames = new HashMap<String, AssignedName>(superJavaClass.getInstanceAssignedNames());
             }
             staticNames.putAll(STATIC_RESERVED_NAMES);
             instanceNames.putAll(INSTANCE_RESERVED_NAMES);
         }
     }
 
     private static final Initializer DUMMY_INITIALIZER = new Initializer() {
         public synchronized void initialize() {
             // anything useful we could do here?
         }
     };
     
     public void setupProxy(final RubyClass proxy) {
         initializer.initialize();
 
         assert proxyLock.isHeldByCurrentThread();
         proxy.defineFastMethod("__jsend!", __jsend_method);
         final Class<?> javaClass = javaClass();
         if (javaClass.isInterface()) {
             setupInterfaceProxy(proxy);
             return;
         }
 
         proxy.setReifiedClass(javaClass);
         
         assert this.proxyClass == null;
         this.unfinishedProxyClass = proxy;
         if (javaClass.isArray() || javaClass.isPrimitive()) {
             // see note below re: 2-field kludge
             this.proxyClass = proxy;
             this.proxyModule = proxy;
             return;
         }
 
         installClassFields(proxy);
         installClassMethods(proxy);
         installClassConstructors(proxy);
         installClassClasses(javaClass, proxy);
         
         // FIXME: bit of a kludge here (non-interface classes assigned to both
         // class and module fields). simplifies proxy extender code, will go away
         // when JI is overhauled (and proxy extenders are deprecated).
         this.proxyClass = proxy;
         this.proxyModule = proxy;
 
         applyProxyExtenders();
 
         // TODO: we can probably release our references to the constantFields
         // array and static/instance callback hashes at this point. 
     }
 
     private static void assignAliases(MethodInstaller installer, Map<String, AssignedName> assignedNames) {
         String name = installer.name;
         String rubyCasedName = JavaUtil.getRubyCasedName(name);
         addUnassignedAlias(rubyCasedName,assignedNames,installer);
 
         String javaPropertyName = JavaUtil.getJavaPropertyName(name);
         String rubyPropertyName = null;
 
         for (Method method: installer.methods) {
             Class<?>[] argTypes = method.getParameterTypes();
             Class<?> resultType = method.getReturnType();
             int argCount = argTypes.length;
 
             // Add property name aliases
             if (javaPropertyName != null) {
                 if (rubyCasedName.startsWith("get_")) {
                     rubyPropertyName = rubyCasedName.substring(4);
                     if (argCount == 0 ||                                // getFoo      => foo
                         argCount == 1 && argTypes[0] == int.class) {    // getFoo(int) => foo(int)
 
                         addUnassignedAlias(javaPropertyName,assignedNames,installer);
                         addUnassignedAlias(rubyPropertyName,assignedNames,installer);
                     }
                 } else if (rubyCasedName.startsWith("set_")) {
                     rubyPropertyName = rubyCasedName.substring(4);
                     if (argCount == 1 && resultType == void.class) {    // setFoo(Foo) => foo=(Foo)
                         addUnassignedAlias(javaPropertyName+'=',assignedNames,installer);
                         addUnassignedAlias(rubyPropertyName+'=',assignedNames,installer);
                     }
                 } else if (rubyCasedName.startsWith("is_")) {
                     rubyPropertyName = rubyCasedName.substring(3);
                     if (resultType == boolean.class) {                  // isFoo() => foo, isFoo(*) => foo(*)
                         addUnassignedAlias(javaPropertyName,assignedNames,installer);
                         addUnassignedAlias(rubyPropertyName,assignedNames,installer);
                     }
                 }
             }
 
             // Additionally add ?-postfixed aliases to any boolean methods and properties.
             if (resultType == boolean.class) {
                 // is_something?, contains_thing?
                 addUnassignedAlias(rubyCasedName+'?',assignedNames,installer);
                 if (rubyPropertyName != null) {
                     // something?
                     addUnassignedAlias(rubyPropertyName+'?',assignedNames,installer);
                 }
             }
         }
     }
     
     private static void addUnassignedAlias(String name, Map<String, AssignedName> assignedNames,
             MethodInstaller installer) {
         if (name == null) return;
 
         AssignedName assignedName = assignedNames.get(name);
         // TODO: missing additional logic for dealing with conflicting protected fields.
         if (Priority.ALIAS.moreImportantThan(assignedName)) {
             installer.addAlias(name);
             assignedNames.put(name, new AssignedName(name, Priority.ALIAS));
         } else if (Priority.ALIAS.asImportantAs(assignedName)) {
             installer.addAlias(name);
         }
     }
 
     private void installClassClasses(final Class<?> javaClass, final RubyModule proxy) {
         // setup constants for public inner classes
         Class<?>[] classes = getDeclaredClasses(javaClass);
 
         for (int i = classes.length; --i >= 0; ) {
             if (javaClass == classes[i].getDeclaringClass()) {
                 Class<?> clazz = classes[i];
 
                 // no non-public inner classes
                 if (!Modifier.isPublic(clazz.getModifiers())) continue;
                 
                 String simpleName = getSimpleName(clazz);
                 if (simpleName.length() == 0) continue;
 
                 final IRubyObject innerProxy = Java.get_proxy_class(JAVA_UTILITIES,get(getRuntime(),clazz));
 
                 if (IdUtil.isConstant(simpleName)) {
                     if (proxy.getConstantAt(simpleName) == null) {
                         proxy.const_set(getRuntime().newString(simpleName), innerProxy);
                     }
                 } else {
                     // lower-case name
                     if (!proxy.respondsTo(simpleName)) {
                         // define a class method
                         proxy.getSingletonClass().addMethod(simpleName, new JavaMethodZero(proxy.getSingletonClass(), Visibility.PUBLIC) {
                             @Override
                             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                                 return innerProxy;
                             }
                         });
                     }
                 }
             }
         }
     }
 
     private synchronized void installClassConstructors(final RubyClass proxy) {
         if (constructorInstaller != null) {
             constructorInstaller.install(proxy);
             constructorInstaller = null;
         }
     }
 
     private synchronized void installClassFields(final RubyClass proxy) {
         for (ConstantField field : constantFields) {
             field.install(proxy);
         }
         constantFields = null;
     }
 
     private synchronized void installClassMethods(final RubyClass proxy) {
         for (NamedInstaller installer : staticInstallers.values()) {
             installer.install(proxy);
         }
         staticInstallers = null;
         
         for (NamedInstaller installer : instanceInstallers.values()) {
             installer.install(proxy);
         }
         instanceInstallers = null;
     }
 
     private void setupClassConstructors(Class<?> javaClass) {
         // TODO: protected methods.  this is going to require a rework
         // of some of the mechanism.
         Constructor[] clsConstructors = getConstructors(javaClass);
         
         // create constructorInstaller; if there are no constructors, it will disable construction
         constructorInstaller = new ConstructorInvokerInstaller("__jcreate!");
 
         for (int i = clsConstructors.length; --i >= 0;) {
             // we need to collect all methods, though we'll only
             // install the ones that are named in this class
             Constructor ctor = clsConstructors[i];
             constructorInstaller.addConstructor(ctor, javaClass);
         }
     }
     
     private void addField(Map <String, NamedInstaller> callbacks, Map<String, AssignedName> names,
             Field field, boolean isFinal, boolean isStatic) {
         String name = field.getName();
 
         if (Priority.FIELD.lessImportantThan(names.get(name))) return;
 
         names.put(name, new AssignedName(name, Priority.FIELD));
         callbacks.put(name, isStatic ? new StaticFieldGetterInstaller(name, field) :
             new InstanceFieldGetterInstaller(name, field));
 
         if (!isFinal) {
             String setName = name + '=';
             callbacks.put(setName, isStatic ? new StaticFieldSetterInstaller(setName, field) :
                 new InstanceFieldSetterInstaller(setName, field));
         }
     }
     
     private void setupClassFields(Class<?> javaClass, InitializerState state) {
         Field[] fields = getFields(javaClass);
         
         for (int i = fields.length; --i >= 0;) {
             Field field = fields[i];
             if (javaClass != field.getDeclaringClass()) continue;
 
             if (ConstantField.isConstant(field)) {
                 state.constantFields.add(new ConstantField(field));
                 continue;
             }
 
             int modifiers = field.getModifiers();
             if (Modifier.isStatic(modifiers)) {
                 addField(state.staticCallbacks, state.staticNames, field, Modifier.isFinal(modifiers), true);
             } else {
                 addField(state.instanceCallbacks, state.instanceNames, field, Modifier.isFinal(modifiers), false);
             }
         }
     }
 
     private void setupClassMethods(Class<?> javaClass, InitializerState state) {
         // TODO: protected methods.  this is going to require a rework of some of the mechanism.
         Method[] methods = getMethods(javaClass);
 
         for (int i = methods.length; --i >= 0;) {
             // we need to collect all methods, though we'll only
             // install the ones that are named in this class
             Method method = methods[i];
             String name = method.getName();
 
             if (Modifier.isStatic(method.getModifiers())) {
                 AssignedName assignedName = state.staticNames.get(name);
 
                 // For JRUBY-4505, restore __method methods for reserved names
                 if (STATIC_RESERVED_NAMES.containsKey(method.getName())) {
                     installStaticMethods(state.staticCallbacks, javaClass, method, name + METHOD_MANGLE);
                     continue;
                 }
 
                 if (assignedName == null) {
                     state.staticNames.put(name, new AssignedName(name, Priority.METHOD));
                 } else {
                     if (Priority.METHOD.lessImportantThan(assignedName)) continue;
                     if (!Priority.METHOD.asImportantAs(assignedName)) {
                         state.staticCallbacks.remove(name);
                         state.staticCallbacks.remove(name + '=');
                         state.staticNames.put(name, new AssignedName(name, Priority.METHOD));
                     }
                 }
                 installStaticMethods(state.staticCallbacks, javaClass, method, name);
             } else {
                 AssignedName assignedName = state.instanceNames.get(name);
 
                 // For JRUBY-4505, restore __method methods for reserved names
                 if (INSTANCE_RESERVED_NAMES.containsKey(method.getName())) {
                     installInstanceMethods(state.instanceCallbacks, javaClass, method, name + METHOD_MANGLE);
                     continue;
                 }
 
                 if (assignedName == null) {
                     state.instanceNames.put(name, new AssignedName(name, Priority.METHOD));
                 } else {
                     if (Priority.METHOD.lessImportantThan(assignedName)) continue;
                     if (!Priority.METHOD.asImportantAs(assignedName)) {
                         state.instanceCallbacks.remove(name);
                         state.instanceCallbacks.remove(name + '=');
                         state.instanceNames.put(name, new AssignedName(name, Priority.METHOD));
                     }
                 }
                 installInstanceMethods(state.instanceCallbacks, javaClass, method, name);
             }
         }
 
         // now iterate over all installers and make sure they also have appropriate aliases
         for (Map.Entry<String, NamedInstaller> entry : state.staticCallbacks.entrySet()) {
             // no aliases for __method methods
             if (entry.getKey().endsWith("__method")) continue;
 
             if (entry.getValue().type == NamedInstaller.STATIC_METHOD && entry.getValue().hasLocalMethod()) {
                 assignAliases((MethodInstaller) entry.getValue(), state.staticNames);
             }
         }
         for (Map.Entry<String, NamedInstaller> entry : state.instanceCallbacks.entrySet()) {
             // no aliases for __method methods
             if (entry.getKey().endsWith("__method")) continue;
             
             if (entry.getValue().type == NamedInstaller.INSTANCE_METHOD && entry.getValue().hasLocalMethod()) {
                 assignAliases((MethodInstaller) entry.getValue(), state.instanceNames);
             }
         }
     }
 
     private void installInstanceMethods(Map<String, NamedInstaller> methodCallbacks, Class<?> javaClass, Method method, String name) {
         MethodInstaller invoker = (MethodInstaller) methodCallbacks.get(name);
         if (invoker == null) {
             invoker = new InstanceMethodInvokerInstaller(name);
             methodCallbacks.put(name, invoker);
         }
         invoker.addMethod(method, javaClass);
     }
 
     private void installStaticMethods(Map<String, NamedInstaller> methodCallbacks, Class<?> javaClass, Method method, String name) {
         MethodInstaller invoker = (MethodInstaller) methodCallbacks.get(name);
         if (invoker == null) {
             invoker = new StaticMethodInvokerInstaller(name);
             methodCallbacks.put(name, invoker);
         }
         invoker.addMethod(method, javaClass);
     }
     
     // old (quasi-deprecated) interface class
     private void setupInterfaceProxy(final RubyClass proxy) {
         initializer.initialize();
         
         assert javaClass().isInterface();
         assert proxyLock.isHeldByCurrentThread();
         assert this.proxyClass == null;
         this.proxyClass = proxy;
         // nothing else to here - the module version will be
         // included in the class.
     }
     
     public void setupInterfaceModule(final RubyModule module) {
         initializer.initialize();
         
         assert javaClass().isInterface();
         assert proxyLock.isHeldByCurrentThread();
         assert this.proxyModule == null;
         this.unfinishedProxyModule = module;
         Class<?> javaClass = javaClass();
         for (ConstantField field: constantFields) {
             field.install(module);
         }
         for (NamedInstaller installer : staticInstallers.values()) {
             installer.install(module);
         }
 
         installClassClasses(javaClass, module);
         
         this.proxyModule = module;
         applyProxyExtenders();
     }
 
     public void addProxyExtender(final IRubyObject extender) {
         lockProxy();
         try {
             if (!extender.respondsTo("extend_proxy")) {
                 throw getRuntime().newTypeError("proxy extender must have an extend_proxy method");
             }
             if (proxyModule == null) {
                 if (proxyExtenders == null) {
                     proxyExtenders = new ArrayList<IRubyObject>();
                 }
                 proxyExtenders.add(extender);
             } else {
                 getRuntime().getWarnings().warn(ID.PROXY_EXTENDED_LATE, " proxy extender added after proxy class created for " + this);
                 extendProxy(extender);
             }
         } finally {
             unlockProxy();
         }
     }
     
     private void applyProxyExtenders() {
         ArrayList<IRubyObject> extenders;
         if ((extenders = proxyExtenders) != null) {
             for (IRubyObject extender : extenders) {
                 extendProxy(extender);
             }
             proxyExtenders = null;
         }
     }
 
     private void extendProxy(IRubyObject extender) {
         extender.callMethod(getRuntime().getCurrentContext(), "extend_proxy", proxyModule);
     }
     
     @JRubyMethod(required = 1)
     public IRubyObject extend_proxy(IRubyObject extender) {
         addProxyExtender(extender);
         return getRuntime().getNil();
     }
     
     public static JavaClass get(Ruby runtime, Class<?> klass) {
         JavaClass javaClass = runtime.getJavaSupport().getJavaClassFromCache(klass);
         if (javaClass == null) {
             javaClass = createJavaClass(runtime,klass);
         }
         return javaClass;
     }
     
     public static RubyArray getRubyArray(Ruby runtime, Class<?>[] classes) {
         IRubyObject[] javaClasses = new IRubyObject[classes.length];
         for (int i = classes.length; --i >= 0; ) {
             javaClasses[i] = get(runtime, classes[i]);
         }
         return runtime.newArrayNoCopy(javaClasses);
     }
 
     private static synchronized JavaClass createJavaClass(Ruby runtime, Class<?> klass) {
         // double-check the cache now that we're synchronized
         JavaClass javaClass = runtime.getJavaSupport().getJavaClassFromCache(klass);
         if (javaClass == null) {
             javaClass = new JavaClass(runtime, klass);
             runtime.getJavaSupport().putJavaClassIntoCache(javaClass);
         }
         return javaClass;
     }
 
     public static RubyClass createJavaClassClass(Ruby runtime, RubyModule javaModule) {
         // FIXME: Determine if a real allocator is needed here. Do people want to extend
         // JavaClass? Do we want them to do that? Can you Class.new(JavaClass)? Should
         // you be able to?
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = javaModule.defineClassUnder("JavaClass", javaModule.fastGetClass("JavaObject"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR); 
         
         result.includeModule(runtime.fastGetModule("Comparable"));
         
         result.defineAnnotatedMethods(JavaClass.class);
 
         result.getMetaClass().undefineMethod("new");
         result.getMetaClass().undefineMethod("allocate");
 
         return result;
     }
 
     private static Map<String, Class> PRIMITIVE_TO_CLASS = new HashMap<String,Class>();
 
     static {
         PRIMITIVE_TO_CLASS.put("byte", byte.class);
         PRIMITIVE_TO_CLASS.put("boolean", boolean.class);
         PRIMITIVE_TO_CLASS.put("short", short.class);
         PRIMITIVE_TO_CLASS.put("char", char.class);
         PRIMITIVE_TO_CLASS.put("int", int.class);
         PRIMITIVE_TO_CLASS.put("long", long.class);
         PRIMITIVE_TO_CLASS.put("float", float.class);
         PRIMITIVE_TO_CLASS.put("double", double.class);
     }
     
     public static synchronized JavaClass forNameVerbose(Ruby runtime, String className) {
         Class <?> klass = null;
         if (className.indexOf(".") == -1 && Character.isLowerCase(className.charAt(0))) {
             // one word type name that starts lower-case...it may be a primitive type
             klass = PRIMITIVE_TO_CLASS.get(className);
         }
 
         if (klass == null) {
             klass = runtime.getJavaSupport().loadJavaClassVerbose(className);
         }
         return JavaClass.get(runtime, klass);
     }
     
     public static synchronized JavaClass forNameQuiet(Ruby runtime, String className) {
         Class klass = runtime.getJavaSupport().loadJavaClassQuiet(className);
         return JavaClass.get(runtime, klass);
     }
 
     @JRubyMethod(name = "for_name", required = 1, meta = true)
     public static JavaClass for_name(IRubyObject recv, IRubyObject name) {
         return forNameVerbose(recv.getRuntime(), name.asJavaString());
     }
     
     private static final Callback __jsend_method = new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 String name = args[0].asJavaString();
                 
                 DynamicMethod method = self.getMetaClass().searchMethod(name);
                 int v = method.getArity().getValue();
                 
                 IRubyObject[] newArgs = new IRubyObject[args.length - 1];
                 System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
                 if(v < 0 || v == (newArgs.length)) {
                     return RuntimeHelpers.invoke(self.getRuntime().getCurrentContext(), self, name, newArgs, block);
                 } else {
                     RubyClass superClass = self.getMetaClass().getSuperClass();
                     return RuntimeHelpers.invokeAs(self.getRuntime().getCurrentContext(), superClass, self, name, newArgs, block);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         };
 
     @JRubyMethod
     public RubyModule ruby_class() {
         // Java.getProxyClass deals with sync issues, so we won't duplicate the logic here
         return Java.getProxyClass(getRuntime(), this);
     }
 
     @JRubyMethod(name = "public?")
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(javaClass().getModifiers()));
     }
 
     @JRubyMethod(name = "protected?")
     public RubyBoolean protected_p() {
         return getRuntime().newBoolean(Modifier.isProtected(javaClass().getModifiers()));
     }
 
     @JRubyMethod(name = "private?")
     public RubyBoolean private_p() {
         return getRuntime().newBoolean(Modifier.isPrivate(javaClass().getModifiers()));
     }
 
     public Class javaClass() {
         return (Class) getValue();
     }
 
     @JRubyMethod(name = "final?")
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(javaClass().getModifiers()));
     }
 
     @JRubyMethod(name = "interface?")
     public RubyBoolean interface_p() {
         return getRuntime().newBoolean(javaClass().isInterface());
     }
 
     @JRubyMethod(name = "array?")
     public RubyBoolean array_p() {
         return getRuntime().newBoolean(javaClass().isArray());
     }
     
     @JRubyMethod(name = "enum?")
     public RubyBoolean enum_p() {
         return getRuntime().newBoolean(javaClass().isEnum());
     }
     
     @JRubyMethod(name = "annotation?")
     public RubyBoolean annotation_p() {
         return getRuntime().newBoolean(javaClass().isAnnotation());
     }
     
     @JRubyMethod(name = "anonymous_class?")
     public RubyBoolean anonymous_class_p() {
         return getRuntime().newBoolean(javaClass().isAnonymousClass());
     }
     
     @JRubyMethod(name = "local_class?")
     public RubyBoolean local_class_p() {
         return getRuntime().newBoolean(javaClass().isLocalClass());
     }
     
     @JRubyMethod(name = "member_class?")
     public RubyBoolean member_class_p() {
         return getRuntime().newBoolean(javaClass().isMemberClass());
     }
     
     @JRubyMethod(name = "synthetic?")
     public IRubyObject synthetic_p() {
         return getRuntime().newBoolean(javaClass().isSynthetic());
     }
 
     @JRubyMethod(name = {"name", "to_s"})
     public RubyString name() {
         return getRuntime().newString(javaClass().getName());
     }
 
     @JRubyMethod
     @Override
     public RubyString inspect() {
         return getRuntime().newString("class " + javaClass().getName());
     }
 
     @JRubyMethod
     public IRubyObject canonical_name() {
         String canonicalName = javaClass().getCanonicalName();
         if (canonicalName != null) {
             return getRuntime().newString(canonicalName);
         }
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "package")
