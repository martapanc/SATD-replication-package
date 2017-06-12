diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index 8052383711..64c4334990 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,1917 +1,1947 @@
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
 
 
 @JRubyClass(name="Java::JavaClass", parent="Java::JavaObject")
 public class JavaClass extends JavaObject {
     public static final String METHOD_MANGLE = "__method";
     
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
-            haveLocalMethod |= javaClass == method.getDeclaringClass();
+            haveLocalMethod |= javaClass == method.getDeclaringClass() ||
+                    method.getDeclaringClass().isInterface();
         }
 
         // called only by initializing thread; no synchronization required
         void addAlias(String alias) {
             if (aliases == null) {
                 aliases = new ArrayList<String>(4);
             }
             if (!aliases.contains(alias))
                 aliases.add(alias);
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
     public IRubyObject get_package() {
         return Java.getInstance(getRuntime(), javaClass().getPackage());
     }
 
     @JRubyMethod
     public IRubyObject class_loader() {
         return Java.getInstance(getRuntime(), javaClass().getClassLoader());
     }
 
     @JRubyMethod
     public IRubyObject protection_domain() {
         return Java.getInstance(getRuntime(), javaClass().getProtectionDomain());
     }
     
     @JRubyMethod(required = 1)
     public IRubyObject resource(IRubyObject name) {
         return Java.getInstance(getRuntime(), javaClass().getResource(name.asJavaString()));
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject resource_as_stream(IRubyObject name) {
         return Java.getInstance(getRuntime(), javaClass().getResourceAsStream(name.asJavaString()));
     }
     
     @JRubyMethod(required = 1)
     public IRubyObject resource_as_string(IRubyObject name) {
         InputStream in = javaClass().getResourceAsStream(name.asJavaString());
         if (in == null) return getRuntime().getNil();
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         try {
             int len;
             byte[] buf = new byte[4096];
             while ((len = in.read(buf)) >= 0) {
                 out.write(buf, 0, len);
             }
         } catch (IOException e) {
             throw getRuntime().newIOErrorFromException(e);
         } finally {
             try {in.close();} catch (IOException ioe) {}
         }
         return getRuntime().newString(new ByteList(out.toByteArray(), false));
     }
     
     @SuppressWarnings("unchecked")
     @JRubyMethod(required = 1)
     public IRubyObject annotation(IRubyObject annoClass) {
         if (!(annoClass instanceof JavaClass)) {
             throw getRuntime().newTypeError(annoClass, getRuntime().getJavaSupport().getJavaClassClass());
         }
         return Java.getInstance(getRuntime(), javaClass().getAnnotation(((JavaClass)annoClass).javaClass()));
     }
     
     @JRubyMethod
     public IRubyObject annotations() {
         // note: intentionally returning the actual array returned from Java, rather
         // than wrapping it in a RubyArray. wave of the future, when java_class will
         // return the actual class, rather than a JavaClass wrapper.
         return Java.getInstance(getRuntime(), javaClass().getAnnotations());
     }
     
     @JRubyMethod(name = "annotations?")
     public RubyBoolean annotations_p() {
         return getRuntime().newBoolean(javaClass().getAnnotations().length > 0);
     }
     
     @JRubyMethod
     public IRubyObject declared_annotations() {
         // see note above re: return type
         return Java.getInstance(getRuntime(), javaClass().getDeclaredAnnotations());
     }
     
     @JRubyMethod(name = "declared_annotations?")
     public RubyBoolean declared_annotations_p() {
         return getRuntime().newBoolean(javaClass().getDeclaredAnnotations().length > 0);
     }
     
     @SuppressWarnings("unchecked")
     @JRubyMethod(name = "annotation_present?", required = 1)
     public IRubyObject annotation_present_p(IRubyObject annoClass) {
         if (!(annoClass instanceof JavaClass)) {
             throw getRuntime().newTypeError(annoClass, getRuntime().getJavaSupport().getJavaClassClass());
         }
         return getRuntime().newBoolean(javaClass().isAnnotationPresent(((JavaClass)annoClass).javaClass()));
     }
     
     @JRubyMethod
     public IRubyObject modifiers() {
         return getRuntime().newFixnum(javaClass().getModifiers());
     }
 
     @JRubyMethod
     public IRubyObject declaring_class() {
         Class<?> clazz = javaClass().getDeclaringClass();
         if (clazz != null) {
             return JavaClass.get(getRuntime(), clazz);
         }
         return getRuntime().getNil();
     }
 
     @JRubyMethod
     public IRubyObject enclosing_class() {
         return Java.getInstance(getRuntime(), javaClass().getEnclosingClass());
     }
     
     @JRubyMethod
     public IRubyObject enclosing_constructor() {
         Constructor<?> ctor = javaClass().getEnclosingConstructor();
         if (ctor != null) {
             return new JavaConstructor(getRuntime(), ctor);
         }
         return getRuntime().getNil();
     }
 
     @JRubyMethod
     public IRubyObject enclosing_method() {
         Method meth = javaClass().getEnclosingMethod();
         if (meth != null) {
             return new JavaMethod(getRuntime(), meth);
         }
         return getRuntime().getNil();
     }
 
     @JRubyMethod
     public IRubyObject enum_constants() {
         return Java.getInstance(getRuntime(), javaClass().getEnumConstants());
     }
 
     @JRubyMethod
     public IRubyObject generic_interfaces() {
         return Java.getInstance(getRuntime(), javaClass().getGenericInterfaces());
     }
     
     @JRubyMethod
     public IRubyObject generic_superclass() {
         return Java.getInstance(getRuntime(), javaClass().getGenericSuperclass());
     }
     
     @JRubyMethod
     public IRubyObject type_parameters() {
         return Java.getInstance(getRuntime(), javaClass().getTypeParameters());
     }
     
     @JRubyMethod
     public IRubyObject signers() {
         return Java.getInstance(getRuntime(), javaClass().getSigners());
     }
     
     private static String getSimpleName(Class<?> clazz) {
  		if (clazz.isArray()) {
  			return getSimpleName(clazz.getComponentType()) + "[]";
  		}
  
  		String className = clazz.getName();
  		int len = className.length();
         int i = className.lastIndexOf('$');
  		if (i != -1) {
             do {
  				i++;
  			} while (i < len && Character.isDigit(className.charAt(i)));
  			return className.substring(i);
  		}
  
  		return className.substring(className.lastIndexOf('.') + 1);
  	}
 
     @JRubyMethod
     public RubyString simple_name() {
         return getRuntime().newString(getSimpleName(javaClass()));
     }
 
     @JRubyMethod
     public IRubyObject superclass() {
         Class<?> superclass = javaClass().getSuperclass();
         if (superclass == null) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), superclass);
     }
 
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(IRubyObject other) {
         Class me = javaClass();
         Class them = null;
 
         // dig out the other class
         if (other instanceof JavaClass) {
             JavaClass otherClass = (JavaClass) other;
             them = otherClass.javaClass();
         } else if (other instanceof ConcreteJavaProxy) {
             ConcreteJavaProxy proxy = (ConcreteJavaProxy)other;
             if (proxy.getObject() instanceof Class) {
                 them = (Class)proxy.getObject();
             }
         }
 
         if (them != null) {
             if (this.javaClass() == them) {
                 return getRuntime().newFixnum(0);
             }
             if (them.isAssignableFrom(me)) {
                 return getRuntime().newFixnum(-1);
             }
             if (me.isAssignableFrom(them)) {
                 return getRuntime().newFixnum(1);
             }
         }
 
         // can't do a comparison
         return getRuntime().getNil();
     }
 
     @JRubyMethod
     public RubyArray java_instance_methods() {
         return java_methods(javaClass().getMethods(), false);
     }
 
     @JRubyMethod
     public RubyArray declared_instance_methods() {
         return java_methods(javaClass().getDeclaredMethods(), false);
     }
 
     private RubyArray java_methods(Method[] methods, boolean isStatic) {
         RubyArray result = getRuntime().newArray(methods.length);
         for (int i = 0; i < methods.length; i++) {
             Method method = methods[i];
             if (isStatic == Modifier.isStatic(method.getModifiers())) {
                 result.append(JavaMethod.create(getRuntime(), method));
             }
         }
         return result;
     }
 
     @JRubyMethod
     public RubyArray java_class_methods() {
         return java_methods(javaClass().getMethods(), true);
     }
 
     @JRubyMethod
     public RubyArray declared_class_methods() {
         return java_methods(javaClass().getDeclaredMethods(), true);
     }
 
     @JRubyMethod(required = 1, rest = true)
     public JavaMethod java_method(IRubyObject[] args) {
         String methodName = args[0].asJavaString();
         try {
             Class<?>[] argumentTypes = buildArgumentTypes(args);
             return JavaMethod.create(getRuntime(), javaClass(), methodName, argumentTypes);
         } catch (ClassNotFoundException cnfe) {
             throw getRuntime().newNameError("undefined method '" + methodName + "' for class '" + javaClass().getName() + "'",
                 methodName);
         }
 
     }
 
     @JRubyMethod(required = 1, rest = true)
     public JavaMethod declared_method(IRubyObject[] args) {
         String methodName = args[0].asJavaString();
         try {
             Class<?>[] argumentTypes = buildArgumentTypes(args);
             return JavaMethod.createDeclared(getRuntime(), javaClass(), methodName, argumentTypes);
         } catch (ClassNotFoundException cnfe) {
             throw getRuntime().newNameError("undefined method '" + methodName + "' for class '" + javaClass().getName() + "'",
                 methodName);
         }
     }
 
     @JRubyMethod(required = 1, rest = true)
     public JavaCallable declared_method_smart(IRubyObject[] args) {
         String methodName = args[0].asJavaString();
 
         try {
             Class<?>[] argumentTypes = buildArgumentTypes(args);
 
             JavaCallable callable = getMatchingCallable(getRuntime(), javaClass(), methodName, argumentTypes);
 
             if (callable != null) return callable;
         } catch (ClassNotFoundException cnfe) {
             // fall through to error below
         }
 
         throw getRuntime().newNameError("undefined method '" + methodName + "' for class '" + javaClass().getName() + "'",
                 methodName);
     }
     
     public static JavaCallable getMatchingCallable(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         if ("<init>".equals(methodName)) {
             return JavaConstructor.getMatchingConstructor(runtime, javaClass, argumentTypes);
         } else {
             // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
             // include superclass methods
             return JavaMethod.getMatchingDeclaredMethod(runtime, javaClass, methodName, argumentTypes);
         }
     }
 
     private Class<?>[] buildArgumentTypes(IRubyObject[] args) throws ClassNotFoundException {
         if (args.length < 1) {
             throw getRuntime().newArgumentError(args.length, 1);
         }
         Class<?>[] argumentTypes = new Class[args.length - 1];
         for (int i = 1; i < args.length; i++) {
             JavaClass type;
             if (args[i] instanceof JavaClass) {
                 type = (JavaClass)args[i];
             } else if (args[i].respondsTo("java_class")) {
                 type = (JavaClass)args[i].callMethod(getRuntime().getCurrentContext(), "java_class");
             } else {
                 type = for_name(this, args[i]);
             }
             argumentTypes[i - 1] = type.javaClass();
         }
         return argumentTypes;
     }
 
     @JRubyMethod
     public RubyArray constructors() {
         RubyArray ctors;
         if ((ctors = constructors) != null) return ctors;
         return constructors = buildConstructors(javaClass().getConstructors());
     }
     
     @JRubyMethod
     public RubyArray classes() {
         return JavaClass.getRubyArray(getRuntime(), javaClass().getClasses());
     }
 
     @JRubyMethod
     public RubyArray declared_classes() {
         Ruby runtime = getRuntime();
         RubyArray result = runtime.newArray();
         Class<?> javaClass = javaClass();
         try {
             Class<?>[] classes = javaClass.getDeclaredClasses();
             for (int i = 0; i < classes.length; i++) {
                 if (Modifier.isPublic(classes[i].getModifiers())) {
                     result.append(get(runtime, classes[i]));
                 }
             }
         } catch (SecurityException e) {
             // restrictive security policy; no matter, we only want public
             // classes anyway
             try {
                 Class<?>[] classes = javaClass.getClasses();
                 for (int i = 0; i < classes.length; i++) {
                     if (javaClass == classes[i].getDeclaringClass()) {
                         result.append(get(runtime, classes[i]));
                     }
                 }
             } catch (SecurityException e2) {
                 // very restrictive policy (disallows Member.PUBLIC)
                 // we'd never actually get this far in that case
             }
         }
         return result;
     }
 
     @JRubyMethod
     public RubyArray declared_constructors() {
         return buildConstructors(javaClass().getDeclaredConstructors());
     }
 
     private RubyArray buildConstructors(Constructor<?>[] constructors) {
         RubyArray result = getRuntime().newArray(constructors.length);
         for (int i = 0; i < constructors.length; i++) {
             result.append(new JavaConstructor(getRuntime(), constructors[i]));
         }
         return result;
     }
 
     @JRubyMethod(rest = true)
     public JavaConstructor constructor(IRubyObject[] args) {
         try {
             Class<?>[] parameterTypes = buildClassArgs(args);
             Constructor<?> constructor = javaClass().getConstructor(parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
             throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     @JRubyMethod(rest = true)
     public JavaConstructor declared_constructor(IRubyObject[] args) {
         try {
             Class<?>[] parameterTypes = buildClassArgs(args);
             Constructor<?> constructor = javaClass().getDeclaredConstructor (parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
             throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     private Class<?>[] buildClassArgs(IRubyObject[] args) {
         Class<?>[] parameterTypes = new Class<?>[args.length];
         for (int i = 0; i < args.length; i++) {
             JavaClass type;
             if (args[i] instanceof JavaClass) {
                 type = (JavaClass)args[i];
             } else if (args[i].respondsTo("java_class")) {
                 type = (JavaClass)args[i].callMethod(getRuntime().getCurrentContext(), "java_class");
             } else {
                 type = for_name(this, args[i]);
             }
             parameterTypes[i] = type.javaClass();
         }
         return parameterTypes;
     }
 
     @JRubyMethod
     public JavaClass array_class() {
         return JavaClass.get(getRuntime(), Array.newInstance(javaClass(), 0).getClass());
     }
    
     @JRubyMethod(required = 1)
     public JavaObject new_array(IRubyObject lengthArgument) {
         if (lengthArgument instanceof RubyInteger) {
             // one-dimensional array
             int length = (int) ((RubyInteger) lengthArgument).getLongValue();
             return new JavaArray(getRuntime(), Array.newInstance(javaClass(), length));
         } else if (lengthArgument instanceof RubyArray) {
             // n-dimensional array
             List list = ((RubyArray)lengthArgument).getList();
             int length = list.size();
             if (length == 0) {
                 throw getRuntime().newArgumentError("empty dimensions specifier for java array");
             }
             int[] dimensions = new int[length];
             for (int i = length; --i >= 0; ) {
                 IRubyObject dimensionLength = (IRubyObject)list.get(i);
                 if ( !(dimensionLength instanceof RubyInteger) ) {
                     throw getRuntime()
                     .newTypeError(dimensionLength, getRuntime().getInteger());
                 }
                 dimensions[i] = (int) ((RubyInteger) dimensionLength).getLongValue();
             }
             return new JavaArray(getRuntime(), Array.newInstance(javaClass(), dimensions));
         } else {
             throw getRuntime().newArgumentError(
                     "invalid length or dimensions specifier for java array" +
             " - must be Integer or Array of Integer");
         }
     }
    
     public IRubyObject emptyJavaArray(ThreadContext context) {
         JavaArray javaArray = new JavaArray(getRuntime(), Array.newInstance(javaClass(), 0));
         RubyClass newProxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
         
         ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), newProxyClass);
         proxy.dataWrapStruct(javaArray);
         
         return proxy;
     }
    
     public IRubyObject javaArraySubarray(ThreadContext context, JavaArray fromArray, int index, int size) {
         int actualLength = Array.getLength(fromArray.getValue());
         if (index >= actualLength) {
             return context.getRuntime().getNil();
         } else {
             if (index + size > actualLength) {
                 size = actualLength - index;
             }
             
             Object newArray = Array.newInstance(javaClass(), size);
             JavaArray javaArray = new JavaArray(getRuntime(), newArray);
             System.arraycopy(fromArray.getValue(), index, newArray, 0, size);
             RubyClass newProxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
 
             ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), newProxyClass);
             proxy.dataWrapStruct(javaArray);
 
             return proxy;
         }
     }
    
     /**
      * Contatenate two Java arrays into a new one. The component type of the
      * additional array must be assignable to the component type of the
      * original array.
      * 
      * @param context
      * @param original
      * @param additional
      * @return
      */
     public IRubyObject concatArrays(ThreadContext context, JavaArray original, JavaArray additional) {
         int oldLength = (int)original.length().getLongValue();
         int addLength = (int)additional.length().getLongValue();
         Object newArray = Array.newInstance(javaClass(), oldLength + addLength);
         JavaArray javaArray = new JavaArray(getRuntime(), newArray);
         System.arraycopy(original.getValue(), 0, newArray, 0, oldLength);
         System.arraycopy(additional.getValue(), 0, newArray, oldLength, addLength);
         RubyClass newProxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
 
         ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), newProxyClass);
         proxy.dataWrapStruct(javaArray);
 
         return proxy;
     }
    
     /**
      * The slow version for when concatenating a Java array of a different type.
      * 
      * @param context
      * @param original
      * @param additional
      * @return
      */
     public IRubyObject concatArrays(ThreadContext context, JavaArray original, IRubyObject additional) {
         int oldLength = (int)original.length().getLongValue();
         int addLength = (int)((RubyFixnum)RuntimeHelpers.invoke(context, additional, "length")).getLongValue();
         Object newArray = Array.newInstance(javaClass(), oldLength + addLength);
         JavaArray javaArray = new JavaArray(getRuntime(), newArray);
         System.arraycopy(original.getValue(), 0, newArray, 0, oldLength);
         RubyClass newProxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
         ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), newProxyClass);
         proxy.dataWrapStruct(javaArray);
         
         Ruby runtime = context.getRuntime();
         for (int i = 0; i < addLength; i++) {
             RuntimeHelpers.invoke(context, proxy, "[]=", runtime.newFixnum(oldLength + i), 
                     RuntimeHelpers.invoke(context, additional, "[]", runtime.newFixnum(i)));
         }
 
         return proxy;
     }
 
     public IRubyObject javaArrayFromRubyArray(ThreadContext context, IRubyObject fromArray) {
         Ruby runtime = context.getRuntime();
         if (!(fromArray instanceof RubyArray)) {
             throw runtime.newTypeError(fromArray, runtime.getArray());
         }
         RubyArray rubyArray = (RubyArray)fromArray;
         JavaArray javaArray = new JavaArray(getRuntime(), Array.newInstance(javaClass(), rubyArray.size()));
         
         if (javaClass().isArray()) {
             // if it's an array of arrays, recurse with the component type
             for (int i = 0; i < rubyArray.size(); i++) {
                 JavaClass componentType = component_type();
                 IRubyObject wrappedComponentArray = componentType.javaArrayFromRubyArray(context, rubyArray.eltInternal(i));
                 javaArray.setWithExceptionHandling(i, JavaUtil.unwrapJavaObject(wrappedComponentArray));
             }
         } else {
             ArrayJavaAddons.copyDataToJavaArray(context, rubyArray, javaArray);
         }
         
         RubyClass newProxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
 
         ArrayJavaProxy proxy = new ArrayJavaProxy(runtime, newProxyClass);
         proxy.dataWrapStruct(javaArray);
         
         return proxy;
     }
 
     @JRubyMethod
     public RubyArray fields() {
         return buildFieldResults(javaClass().getFields());
     }
 
     @JRubyMethod
     public RubyArray declared_fields() {
         return buildFieldResults(javaClass().getDeclaredFields());
     }
 
     private RubyArray buildFieldResults(Field[] fields) {
         RubyArray result = getRuntime().newArray(fields.length);
         for (int i = 0; i < fields.length; i++) {
             result.append(new JavaField(getRuntime(), fields[i]));
         }
         return result;
     }
 
     @JRubyMethod(required = 1)
     public JavaField field(ThreadContext context, IRubyObject name) {
         Class<?> javaClass = javaClass();
         Ruby runtime = context.getRuntime();
         String stringName = name.asJavaString();
 
         try {
             return new JavaField(runtime, javaClass.getField(stringName));
         } catch (NoSuchFieldException nsfe) {
             String newName = JavaUtil.getJavaCasedName(stringName);
             if(newName != null) {
                 try {
                     return new JavaField(runtime, javaClass.getField(newName));
                 } catch (NoSuchFieldException nsfe2) {}
             }
             throw undefinedFieldError(runtime, javaClass.getName(), stringName);
          }
     }
 
     @JRubyMethod(required = 1)
     public JavaField declared_field(ThreadContext context, IRubyObject name) {
         Class<?> javaClass = javaClass();
         Ruby runtime = context.getRuntime();
         String stringName = name.asJavaString();
         
         try {
             return new JavaField(runtime, javaClass.getDeclaredField(stringName));
         } catch (NoSuchFieldException nsfe) {
             String newName = JavaUtil.getJavaCasedName(stringName);
             if(newName != null) {
                 try {
                     return new JavaField(runtime, javaClass.getDeclaredField(newName));
                 } catch (NoSuchFieldException nsfe2) {}
             }
             throw undefinedFieldError(runtime, javaClass.getName(), stringName);
         }
     }
     
     public static RaiseException undefinedFieldError(Ruby runtime, String javaClassName, String name) {
         return runtime.newNameError("undefined field '" + name + "' for class '" + javaClassName + "'", name);
     }
 
     @JRubyMethod
     public RubyArray interfaces() {
         return JavaClass.getRubyArray(getRuntime(), javaClass().getInterfaces());
     }
 
     @JRubyMethod(name = "primitive?")
     public RubyBoolean primitive_p() {
         return getRuntime().newBoolean(isPrimitive());
     }
 
     @JRubyMethod(name = "assignable_from?", required = 1)
     public RubyBoolean assignable_from_p(IRubyObject other) {
         if (! (other instanceof JavaClass)) {
             throw getRuntime().newTypeError("assignable_from requires JavaClass (" + other.getType() + " given)");
         }
 
         Class<?> otherClass = ((JavaClass) other).javaClass();
         return assignable(javaClass(), otherClass) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public static boolean assignable(Class<?> thisClass, Class<?> otherClass) {
         if(!thisClass.isPrimitive() && otherClass == Void.TYPE ||
             thisClass.isAssignableFrom(otherClass)) {
             return true;
         }
 
         otherClass = JavaUtil.primitiveToWrapper(otherClass);
         thisClass = JavaUtil.primitiveToWrapper(thisClass);
 
         if(thisClass.isAssignableFrom(otherClass)) {
             return true;
         }
         if(Number.class.isAssignableFrom(thisClass)) {
             if(Number.class.isAssignableFrom(otherClass)) {
                 return true;
             }
             if(otherClass.equals(Character.class)) {
                 return true;
             }
         }
         if(thisClass.equals(Character.class)) {
             if(Number.class.isAssignableFrom(otherClass)) {
                 return true;
             }
         }
         return false;
     }
 
     private boolean isPrimitive() {
         return javaClass().isPrimitive();
     }
 
     @JRubyMethod
     public JavaClass component_type() {
         if (! javaClass().isArray()) {
             throw getRuntime().newTypeError("not a java array-class");
         }
         return JavaClass.get(getRuntime(), javaClass().getComponentType());
     }
     
     private static Constructor[] getConstructors(Class<?> javaClass) {
         try {
             return javaClass.getConstructors();
         } catch (SecurityException e) {
             return new Constructor[] {};
         }        
     }
 
     private static Class<?>[] getDeclaredClasses(Class<?> javaClass) {
         try {
             return javaClass.getDeclaredClasses();
         } catch (SecurityException e) {
             return new Class<?>[] {};
         }
     }
     
     private static Class<?>[] getClasses(Class<?> javaClass) {
         try {
             return javaClass.getClasses();
         } catch (SecurityException e) {
             return new Class<?>[] {};
         }
     }
 
     public static Field[] getDeclaredFields(Class<?> javaClass) {
         try {
             return javaClass.getDeclaredFields();
         } catch (SecurityException e) {
             return getFields(javaClass);
         }
     }
 
     public static Field[] getFields(Class<?> javaClass) {
         try {
             return javaClass.getFields();
         } catch (SecurityException e) {
             return new Field[] {};
         }
     }
     
     private static boolean methodsAreEquivalent(Method child, Method parent) {
         return parent.getDeclaringClass().isAssignableFrom(child.getDeclaringClass())
                 && Arrays.equals(child.getParameterTypes(), parent.getParameterTypes())
                 && child.getReturnType() == parent.getReturnType()
                 && child.isVarArgs() == parent.isVarArgs()
                 && Modifier.isPublic(child.getModifiers()) == Modifier.isPublic(parent.getModifiers())
                 && Modifier.isProtected(child.getModifiers()) == Modifier.isProtected(parent.getModifiers())
                 && Modifier.isStatic(child.getModifiers()) == Modifier.isStatic(parent.getModifiers());
     }
     
     private static Method[] getMethods(Class<?> javaClass) {
         HashMap<String, List<Method>> nameMethods = new HashMap<String, List<Method>>();
         ArrayList<Method> list2 = new ArrayList<Method>();
 
         // aggregate all candidate method names from child, with their method objects
 
-        // instance methods only; static methods are local to the class and always bound
-        for (Method m: javaClass.getDeclaredMethods()) {
-            int modifiers = m.getModifiers();
-            if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
-                if (Modifier.isStatic(modifiers)) {
-                    // static methods are always bound
-                    list2.add(m);
-                } else {
-                    List<Method> methods = nameMethods.get(m.getName());
-                    if (methods == null) {
-                        nameMethods.put(m.getName(), methods = new ArrayList<Method>());
+        // Instance methods only; static methods are local to the class and always bound.
+        // Don't do this class's methods if it isn't public, since only superclass
+        // methods will be available.
+        if (Modifier.isPublic(javaClass.getModifiers())) {
+            for (Method m: javaClass.getDeclaredMethods()) {
+                int modifiers = m.getModifiers();
+                if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
+                    if (Modifier.isStatic(modifiers)) {
+                        // static methods are always bound
+                        list2.add(m);
+                    } else {
+                        List<Method> methods = nameMethods.get(m.getName());
+                        if (methods == null) {
+                            nameMethods.put(m.getName(), methods = new ArrayList<Method>());
+                        }
+                        methods.add(m);
                     }
-                    methods.add(m);
                 }
             }
         }
 
+        // add all directly-implemented interface methods
+        for (Class c : javaClass.getInterfaces()) {
+            if (!Modifier.isPublic(c.getModifiers())) continue;
+            
+            for (Method m: c.getDeclaredMethods()) {
+                List<Method> methods = nameMethods.get(m.getName());
+                if (methods == null) {
+                    nameMethods.put(m.getName(), methods = new ArrayList<Method>());
+                }
+                methods.add(m);
+            }
+        }
+
         // we all all superclasses, but avoid adding superclass methods with same name+signature as subclass methods
         // see JRUBY-3130
         for (Class c = javaClass.getSuperclass(); c != null; c = c.getSuperclass()) {
             try {
                 Methods: for (Method m : c.getDeclaredMethods()) {
                     List<Method> childMethods = nameMethods.get(m.getName());
                     if (childMethods == null) continue;
                     
                     for (Method m2 : childMethods) {
                         if (methodsAreEquivalent(m2, m)) {
                             childMethods.remove(m2);
                             if (childMethods.isEmpty()) nameMethods.remove(m.getName());
                             continue Methods;
                         }
                     }
                 }
             } catch (SecurityException e) {
             }
         }
         
         // now only bind the ones that remain
         for (Class c = javaClass; c != null; c = c.getSuperclass()) {
+            // skip non-public classes
+            if (!Modifier.isPublic(c.getModifiers())) continue;
+            
             try {
                 for (Method m : c.getDeclaredMethods()) {
                     int modifiers = m.getModifiers();
                     if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
                         if (!nameMethods.containsKey(m.getName())) continue;
                         list2.add(m);
                     }
                 }
             } catch (SecurityException e) {
             }
         }
+        for (Class c : javaClass.getInterfaces()) {
+            try {
+                for (Method m : c.getDeclaredMethods()) {
+                    if (!nameMethods.containsKey(m.getName())) continue;
+                    list2.add(m);
+                }
+            } catch (SecurityException e) {
+            }
+        }
         
         return list2.toArray(new Method[list2.size()]);
     }
 }
diff --git a/src/org/jruby/javasupport/JavaMethod.java b/src/org/jruby/javasupport/JavaMethod.java
index 83c5dbfc7e..6a0d263904 100644
--- a/src/org/jruby/javasupport/JavaMethod.java
+++ b/src/org/jruby/javasupport/JavaMethod.java
@@ -1,740 +1,738 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
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
 
 import java.lang.annotation.Annotation;
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.Type;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.compiler.util.HandleFactory;
 import org.jruby.compiler.util.HandleFactory.Handle;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.proxy.InternalJavaProxy;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 
 @JRubyClass(name="Java::JavaMethod")
 public class JavaMethod extends JavaCallable {
     private final static boolean USE_HANDLES = RubyInstanceConfig.USE_GENERATED_HANDLES;
     private final static boolean HANDLE_DEBUG = false;
     private final Method method;
     private final Class boxedReturnType;
     private final boolean isFinal;
     private final Handle handle;
     private final JavaUtil.JavaConverter returnConverter;
 
     public Object getValue() {
         return method;
     }
 
     public static RubyClass createJavaMethodClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = 
             javaModule.defineClassUnder("JavaMethod", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
         JavaCallable.registerRubyMethods(runtime, result);
         
         result.defineAnnotatedMethods(JavaMethod.class);
 
         return result;
     }
 
     public JavaMethod(Ruby runtime, Method method) {
         super(runtime, runtime.getJavaSupport().getJavaMethodClass(), method.getParameterTypes());
         this.method = method;
         this.isFinal = Modifier.isFinal(method.getModifiers());
         if (method.getReturnType().isPrimitive() && method.getReturnType() != void.class) {
             this.boxedReturnType = CodegenUtils.getBoxType(method.getReturnType());
         } else {
             this.boxedReturnType = method.getReturnType();
         }
 
         boolean methodIsPublic = Modifier.isPublic(method.getModifiers());
         boolean classIsPublic = Modifier.isPublic(method.getDeclaringClass().getModifiers());
 
         // try to find a "totally" public version of the method using superclasses and interfaces
         if (methodIsPublic && !classIsPublic) {
             if (HANDLE_DEBUG) System.out.println("Method " + method + " is not on a public class, searching for a better one");
             Method newMethod = method;
             Class newClass = method.getDeclaringClass();
 
             OUTER: while (newClass != null) {
                 // try class
                 try {
                     if (HANDLE_DEBUG) System.out.println("Trying to find " + method + " on " + newClass);
                     newMethod = newClass.getMethod(method.getName(), method.getParameterTypes());
 
                     // got it; break if this class is public
                     if (Modifier.isPublic(newMethod.getDeclaringClass().getModifiers())) {
                         break;
                     }
                 } catch (NoSuchMethodException nsme) {
                 }
 
                 // try interfaces
                 for (Class ifc : newClass.getInterfaces()) {
                     try {
                         if (HANDLE_DEBUG) System.out.println("Trying to find " + method + " on " + ifc);
                         newMethod = ifc.getMethod(method.getName(), method.getParameterTypes());
                         break OUTER;
                     } catch (NoSuchMethodException nsme) {
                     }
                 }
 
                 // go to superclass
                 newClass = newClass.getSuperclass();
                 newMethod = null;
             }
             
             if (newMethod != null) {
                 if (HANDLE_DEBUG) System.out.println("Found a better method target: " + newMethod);
                 method = newMethod;
                 methodIsPublic = Modifier.isPublic(method.getModifiers());
                 classIsPublic = Modifier.isPublic(method.getDeclaringClass().getModifiers());
             }
         }
 
         // prepare a faster handle if handles are enabled and the method and class are public
         Handle tmpHandle = null;
         try {
             if (USE_HANDLES &&
                     // must be a public method
                     methodIsPublic &&
                     // must be a public class
                     classIsPublic &&
                     // must have been loaded from our known classloader hierarchy
                     runtime.getJRubyClassLoader().loadClass(method.getDeclaringClass().getCanonicalName()) == method.getDeclaringClass()) {
                 tmpHandle = HandleFactory.createHandle(runtime.getJRubyClassLoader(), method);
             } else {
                 tmpHandle = null;
             }
         } catch (ClassNotFoundException cnfe) {
             tmpHandle = null;
         }
 
         if (tmpHandle == null) {
             if (HANDLE_DEBUG) System.out.println("did not use handle for " + method);
         }
         
         handle = tmpHandle;
 
         // Special classes like Collections.EMPTY_LIST are inner classes that are private but 
         // implement public interfaces.  Their methods are all public methods for the public 
         // interface.  Let these public methods execute via setAccessible(true). 
-        if (methodIsPublic &&
-            !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
-            accessibleObject().setAccessible(true);
-        }
+        assert Modifier.isPublic(method.getDeclaringClass().getModifiers()) :
+            "we should not be trying to bind methods declared on non-public classes";
         
         returnConverter = JavaUtil.getJavaConverter(method.getReturnType());
     }
 
     public static JavaMethod create(Ruby runtime, Method method) {
         return new JavaMethod(runtime, method);
     }
 
     public static JavaMethod create(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         try {
             Method method = javaClass.getMethod(methodName, argumentTypes);
             return create(runtime, method);
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
     public static JavaMethod createDeclared(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         try {
             return create(runtime, javaClass.getDeclaredMethod(methodName, argumentTypes));
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
     public static JavaMethod getMatchingDeclaredMethod(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
         // include superclass methods.  also, the getDeclared calls may throw SecurityException if
         // we're running under a restrictive security policy.
         try {
             return create(runtime, javaClass.getDeclaredMethod(methodName, argumentTypes));
         } catch (NoSuchMethodException e) {
             // search through all declared methods to find a closest match
             MethodSearch: for (Method method : javaClass.getDeclaredMethods()) {
                 if (method.getName().equals(methodName)) {
                     Class<?>[] targetTypes = method.getParameterTypes();
                 
                     // for zero args case we can stop searching
                     if (targetTypes.length == 0 && argumentTypes.length == 0) {
                         return create(runtime, method);
                     }
                     
                     TypeScan: for (int i = 0; i < argumentTypes.length; i++) {
                         if (i >= targetTypes.length) continue MethodSearch;
 
                         if (targetTypes[i].isAssignableFrom(argumentTypes[i])) {
                             continue TypeScan;
                         } else {
                             continue MethodSearch;
                         }
                     }
 
                     // if we get here, we found a matching method, use it
                     // TODO: choose narrowest method by continuing to search
                     return create(runtime, method);
                 }
             }
         }
         // no matching method found
         return null;
     }
     
     @Override
     public boolean equals(Object other) {
         return other instanceof JavaMethod &&
             this.method == ((JavaMethod)other).method;
     }
     
     @Override
     public int hashCode() {
         return method.hashCode();
     }
 
     @JRubyMethod
     @Override
     public RubyString name() {
         return getRuntime().newString(method.getName());
     }
 
     public int getArity() {
         return parameterTypes.length;
     }
 
     @JRubyMethod(name = "public?")
     @Override
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(method.getModifiers()));
     }
 
     @JRubyMethod(name = "final?")
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(method.getModifiers()));
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject invoke(IRubyObject[] args) {
         checkArity(args.length - 1);
         Object[] arguments = new Object[args.length - 1];
         convertArguments(args, arguments, 1);
 
         IRubyObject invokee = args[0];
         if(invokee.isNil()) {
             return invokeWithExceptionHandling(method, null, arguments);
         }
 
         Object javaInvokee = null;
 
         if (!isStatic()) {
             javaInvokee = JavaUtil.unwrapJavaValue(getRuntime(), invokee, "invokee not a java object");
 
             if (! method.getDeclaringClass().isInstance(javaInvokee)) {
                 throw getRuntime().newTypeError("invokee not instance of method's class (" +
                                                   "got" + javaInvokee.getClass().getName() + " wanted " +
                                                   method.getDeclaringClass().getName() + ")");
             }
 
             //
             // this test really means, that this is a ruby-defined subclass of a java class
             //
             if (javaInvokee instanceof InternalJavaProxy &&
                     // don't bother to check if final method, it won't
                     // be there (not generated, can't be!)
                     !Modifier.isFinal(method.getModifiers())) {
                 JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee)
                         .___getProxyClass();
                 JavaProxyMethod jpm;
                 if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null &&
                         jpm.hasSuperImplementation()) {
                     return invokeWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arguments);
                 }
             }
         }
         return invokeWithExceptionHandling(method, javaInvokee, arguments);
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject invoke_static(IRubyObject[] args) {
         checkArity(args.length);
         Object[] arguments = new Object[args.length];
         System.arraycopy(args, 0, arguments, 0, arguments.length);
         convertArguments(args, arguments, 0);
         return invokeWithExceptionHandling(method, null, arguments);
     }
 
     @JRubyMethod
     public IRubyObject return_type() {
         Class<?> klass = method.getReturnType();
         
         if (klass.equals(void.class)) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), klass);
     }
 
     @JRubyMethod
     public IRubyObject type_parameters() {
         return Java.getInstance(getRuntime(), method.getTypeParameters());
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object[] args) {
         checkArity(args.length);
         checkInstanceof(javaInvokee);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, args);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, args);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(0);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(1);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0, Object arg1) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(2);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0, arg1);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0, Object arg1, Object arg2) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(3);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0, arg1, arg2);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0, Object arg1, Object arg2, Object arg3) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(4);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0, arg1, arg2, arg3);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2, arg3);
     }
 
     public IRubyObject invokeStaticDirect(Object[] args) {
         checkArity(args.length);
         return invokeDirectWithExceptionHandling(method, null, args);
     }
 
     public IRubyObject invokeStaticDirect() {
         checkArity(0);
         return invokeDirectWithExceptionHandling(method, null);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0) {
         checkArity(1);
         return invokeDirectWithExceptionHandling(method, null, arg0);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0, Object arg1) {
         checkArity(2);
         return invokeDirectWithExceptionHandling(method, null, arg0, arg1);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0, Object arg1, Object arg2) {
         checkArity(3);
         return invokeDirectWithExceptionHandling(method, null, arg0, arg1, arg2);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0, Object arg1, Object arg2, Object arg3) {
         checkArity(4);
         return invokeDirectWithExceptionHandling(method, null, arg0, arg1, arg2, arg3);
     }
 
     private void checkInstanceof(Object javaInvokee) throws RaiseException {
         if (!method.getDeclaringClass().isInstance(javaInvokee)) {
             throw getRuntime().newTypeError("invokee not instance of method's class (" + "got" + javaInvokee.getClass().getName() + " wanted " + method.getDeclaringClass().getName() + ")");
         }
     }
 
     private IRubyObject invokeWithExceptionHandling(Method method, Object javaInvokee, Object[] arguments) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arguments)
                     : method.invoke(javaInvokee, arguments);
             return returnConverter.convert(getRuntime(), result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arguments);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectSuperWithExceptionHandling(Method method, Object javaInvokee, Object... arguments) {
         // super calls from proxies must use reflected method
         // FIXME: possible to make handles do the superclass call?
         try {
             Object result = method.invoke(javaInvokee, arguments);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arguments);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object[] arguments) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arguments)
                     : method.invoke(javaInvokee, arguments);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arguments);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee)
                     : method.invoke(javaInvokee);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arg0)
                     : method.invoke(javaInvokee, arg0);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0, Object arg1) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arg0, arg1)
                     : method.invoke(javaInvokee, arg0, arg1);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0, arg1);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0, Object arg1, Object arg2) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arg0, arg1, arg2)
                     : method.invoke(javaInvokee, arg0, arg1, arg2);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0, arg1, arg2);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0, Object arg1, Object arg2, Object arg3) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arg0, arg1, arg2, arg3)
                     : method.invoke(javaInvokee, arg0, arg1, arg2, arg3);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0, arg1, arg2, arg3);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject convertReturn(Object result) {
         if (result != null && result.getClass() != boxedReturnType) {
             // actual type does not exactly match method return type, re-get converter
             // FIXME: when the only autoconversions are primitives, this won't be needed
             return JavaUtil.convertJavaToUsableRubyObject(getRuntime(), result);
         }
         return JavaUtil.convertJavaToUsableRubyObjectWithConverter(getRuntime(), result, returnConverter);
     }
 
     private IRubyObject handleIllegalAccessEx(Method method, IllegalAccessException iae) throws RaiseException {
         throw getRuntime().newTypeError("illegal access on '" + method.getName() + "': " + iae.getMessage());
     }
 
     private IRubyObject handlelIllegalArgumentEx(Method method, IllegalArgumentException iae, Object... arguments) throws RaiseException {
         throw getRuntime().newTypeError(
                 "for method " +
                 method.getDeclaringClass().getSimpleName() +
                 "." +
                 method.getName() +
                 " expected " +
                 argument_types().inspect() +
                 "; got: " +
                 dumpArgTypes(arguments) +
                 "; error: " +
                 iae.getMessage());
     }
 
     private void convertArguments(IRubyObject[] argsIn, Object[] argsOut, int from) {
         Class<?>[] types = parameterTypes;
         for (int i = argsOut.length; --i >= 0; ) {
             argsOut[i] = argsIn[i+from].toJava(types[i]);
         }
     }
 
     public Class<?>[] getParameterTypes() {
         return parameterTypes;
     }
 
     public Class<?>[] getExceptionTypes() {
         return method.getExceptionTypes();
     }
 
     public Type[] getGenericParameterTypes() {
         return method.getGenericParameterTypes();
     }
 
     public Type[] getGenericExceptionTypes() {
         return method.getGenericExceptionTypes();
     }
     
     public Annotation[][] getParameterAnnotations() {
         return method.getParameterAnnotations();
     }
 
     public boolean isVarArgs() {
         return method.isVarArgs();
     }
 
     protected String nameOnInspection() {
         return "#<" + getType().toString() + "/" + method.getName() + "(";
     }
 
     public RubyBoolean static_p() {
         return getRuntime().newBoolean(isStatic());
     }
     
     public RubyBoolean bridge_p() {
         return getRuntime().newBoolean(method.isBridge());
     }
 
     private boolean isStatic() {
         return Modifier.isStatic(method.getModifiers());
     }
 
     public int getModifiers() {
         return method.getModifiers();
     }
 
     public String toGenericString() {
         return method.toGenericString();
     }
 
     protected AccessibleObject accessibleObject() {
         return method;
     }
 
     private boolean mightBeProxy(Object javaInvokee) {
         // this test really means, that this is a ruby-defined subclass of a java class
         return javaInvokee instanceof InternalJavaProxy && !isFinal;
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object... args) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, args);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, args);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0, Object arg1) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0, arg1);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0, Object arg1, Object arg2) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0, arg1, arg2);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0, Object arg1, Object arg2, Object arg3) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0, arg1, arg2, arg3);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2, arg3);
         }
     }
 
     public static RaiseException newMethodNotFoundError(Ruby runtime, Class target, String prettyName, String simpleName) {
         return runtime.newNameError("java method not found: " + target.getName() + "." + prettyName, simpleName);
     }
 
     public static RaiseException newArgSizeMismatchError(Ruby runtime, Class ... argTypes) {
         return runtime.newArgumentError("argument count mismatch for method signature " + CodegenUtils.prettyParams(argTypes));
     }
 }
diff --git a/test/org/jruby/test/TestJavaReentrantExceptions.java b/test/org/jruby/test/TestJavaReentrantExceptions.java
index 3d74f8f431..c2ef231dc6 100644
--- a/test/org/jruby/test/TestJavaReentrantExceptions.java
+++ b/test/org/jruby/test/TestJavaReentrantExceptions.java
@@ -1,56 +1,56 @@
 package org.jruby.test;
 import junit.framework.TestCase;
 import org.jruby.NativeException;
 import org.jruby.Ruby;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.JavaEmbedUtils;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Tests that a Java Exception can be thrown from Java through JRuby and
  * caught again by Java (when unwrapped).
  * 
  * Designed to test JRUBY-2652
  * 
  * @author Rick Moynihan rick@calicojack.co.uk
  */
 public class TestJavaReentrantExceptions extends TestCase {
     private Ruby runtime;
 
     public TestJavaReentrantExceptions(String name) {
         super(name);
     }
 
     @Override
     public void setUp() {
         runtime = Ruby.newInstance();
     }
 
     public void testExceptionsAcrossTheBridge() {
         final IRubyObject wrappedThrower = JavaEmbedUtils.javaToRuby(runtime, new ExceptionThrower());
         boolean exceptionThrown = false;
         try {
             //call throwException via JRuby
             JavaEmbedUtils.invokeMethod(runtime, wrappedThrower, "throwException", new Object[] { }, Object.class);
         } catch(final RaiseException e) {
             exceptionThrown = true;
             final NativeException ne = (NativeException) e.getException();
             final ExpectedException ee = (ExpectedException) ne.getCause();
             assertEquals("The unpacked exception we receive should be the one we threw.",
                          ExceptionThrower.expectedException,ee);
         } finally {
             assertTrue("Java Exception should have been thrown and wrapped as a RaiseException",exceptionThrown);
         }
     }
+
+    public static class ExceptionThrower {
+        public static final ExpectedException expectedException = new ExpectedException();
+
+        public void throwException() throws ExpectedException {
+            throw expectedException;
+        }
+    }
 }
 class ExpectedException extends Exception {
     
-}
-
-class ExceptionThrower {
-    public static final ExpectedException expectedException = new ExpectedException();
-    
-    public void throwException() throws ExpectedException {
-        throw expectedException;
-    }
 }
\ No newline at end of file
