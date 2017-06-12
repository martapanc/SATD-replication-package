diff --git a/spec/java_integration/methods/basics_spec.rb b/spec/java_integration/methods/basics_spec.rb
index f693df228f..8b3f942ea8 100644
--- a/spec/java_integration/methods/basics_spec.rb
+++ b/spec/java_integration/methods/basics_spec.rb
@@ -1,17 +1,35 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 describe "Java instance methods" do
   it "should have Ruby arity -1" do
     lambda do
       java.lang.String.instance_method(:toString).arity.should == -1
     end.should_not raise_error
   end
 end
 
 describe "Java static methods" do
   it "should have Ruby arity -1" do
     lambda do
       java.lang.System.method(:getProperty).arity.should == -1
     end.should_not raise_error
   end
+end
+
+describe "JavaClass\#==" do
+  it "returns true for the same java.lang.Class" do
+    str_jclass = java.lang.String.java_class
+    str_class = java.lang.Class.forName('java.lang.String')
+
+    str_jclass.should == str_class
+  end
+end
+
+describe "java.lang.Class\#==" do
+  it "returns true for the same JavaClass" do
+    str_jclass = java.lang.String.java_class
+    str_class = java.lang.Class.forName('java.lang.String')
+
+    str_class.should == str_jclass
+  end
 end
\ No newline at end of file
diff --git a/spec/java_integration/types/wrapping_spec.rb b/spec/java_integration/types/wrapping_spec.rb
index 5a74a6b64e..da36999564 100644
--- a/spec/java_integration/types/wrapping_spec.rb
+++ b/spec/java_integration/types/wrapping_spec.rb
@@ -1,66 +1,78 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 import "java_integration.fixtures.JavaTypeMethods"
 
 describe "A Java method returning/receiving uncoercible Java types" do
   it "wraps the objects in Ruby object wrappers" do
     # static
     obj = JavaTypeMethods.staticNewObject
     obj.class.to_s.should == "Java::JavaLang::Object"
     obj.java_object.should_not == nil
 
     # instance
     obj = JavaTypeMethods.new.newObject
     obj.class.to_s.should == "Java::JavaLang::Object"
     obj.java_object.should_not == nil
   end
 
   it "registers the wrapper and reuses it when object returns" do
     # static
     obj = JavaTypeMethods.staticNewObject
     JavaTypeMethods.staticSetObject(obj)
     obj2 = JavaTypeMethods.staticGetObject
 
     obj.should == obj2
     obj.object_id.should == obj2.object_id
 
     # instance
     jtm = JavaTypeMethods.new
     obj = jtm.newObject
     jtm.setObject(obj)
     obj2 = jtm.getObject
 
     obj.should == obj2
     obj.object_id.should == obj2.object_id
   end
 
   describe "when receiving Ruby subtypes of Java types" do
     it "registers the Ruby part of the object so it is not lost when object returns" do
       class RubySubtypeOfJavaObject < Java::java.lang.Object
         def foo; true; end
       end
 
       rsojo = RubySubtypeOfJavaObject.new
 
       # static
       JavaTypeMethods.staticSetObject(rsojo)
       rsojo2 = JavaTypeMethods.staticGetObject
 
       rsojo.should == rsojo2
       rsojo.object_id.should == rsojo2.object_id
       rsojo.foo.should == true
       rsojo2.foo.should == true
 
       # instance
       rsojo = RubySubtypeOfJavaObject.new
       jtm = JavaTypeMethods.new
       jtm.setObject(rsojo)
       rsojo2 = jtm.getObject
 
       rsojo.should == rsojo2
       rsojo.object_id.should == rsojo2.object_id
       rsojo.foo.should == true
       rsojo2.foo.should == true
     end
   end
 end
+
+describe "Java::JavaObject.wrap" do
+  it "wraps a Java object with an appropriate JavaObject subclass" do
+    obj = Java::JavaObject.wrap(java.lang.Object.new)
+    str = Java::JavaObject.wrap(java.lang.String.new)
+    cls = Java::JavaObject.wrap(java.lang.Class.forName('java.lang.String'))
+
+    obj.class.should == Java::JavaObject
+    str.class.should == Java::JavaObject
+    cls.class.should == Java::JavaClass
+  end
+end
diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index d4c057734c..db9551d93f 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,1895 +1,1917 @@
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
+import org.jruby.java.proxies.ConcreteJavaProxy;
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
     
+    // An object that's never wrapped, so we can safely compare it with a
+    // wrapped null and not treat
+    private static final Object NEVER = new Object();
+    
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
         RESERVED_NAMES.put("private", new AssignedName("private", Priority.RESERVED));
         RESERVED_NAMES.put("protected", new AssignedName("protected", Priority.RESERVED));
         RESERVED_NAMES.put("public", new AssignedName("public", Priority.RESERVED));
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
         boolean isProtected;
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
             haveLocalMethod |= javaClass == method.getDeclaringClass();
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
 
     protected Map<String, AssignedName> getStaticAssignedNames() {
         return staticAssignedNames;
     }
     protected Map<String, AssignedName> getInstanceAssignedNames() {
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
         Constructor[] constructors = getConstructors(javaClass);
         
         // create constructorInstaller; if there are no constructors, it will disable construction
         constructorInstaller = new ConstructorInvokerInstaller("__jcreate!");
 
         for (int i = constructors.length; --i >= 0;) {
             // we need to collect all methods, though we'll only
             // install the ones that are named in this class
             Constructor ctor = constructors[i];
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
-    public RubyFixnum op_cmp(IRubyObject other) {
-        if (! (other instanceof JavaClass)) {
-            throw getRuntime().newTypeError("<=> requires JavaClass (" + other.getType() + " given)");
-        }
-        JavaClass otherClass = (JavaClass) other;
-        if (this.javaClass() == otherClass.javaClass()) {
-            return getRuntime().newFixnum(0);
+    public IRubyObject op_cmp(IRubyObject other) {
+        Class me = javaClass();
+        Class them = null;
+
+        // dig out the other class
+        if (other instanceof JavaClass) {
+            JavaClass otherClass = (JavaClass) other;
+            them = otherClass.javaClass();
+        } else if (other instanceof ConcreteJavaProxy) {
+            ConcreteJavaProxy proxy = (ConcreteJavaProxy)other;
+            if (proxy.getObject() instanceof Class) {
+                them = (Class)proxy.getObject();
+            }
         }
-        if (otherClass.javaClass().isAssignableFrom(this.javaClass())) {
-            return getRuntime().newFixnum(-1);
+
+        if (them != null) {
+            if (this.javaClass() == them) {
+                return getRuntime().newFixnum(0);
+            }
+            if (them.isAssignableFrom(me)) {
+                return getRuntime().newFixnum(-1);
+            }
+            if (me.isAssignableFrom(them)) {
+                return getRuntime().newFixnum(1);
+            }
         }
-        return getRuntime().newFixnum(1);
+
+        // can't do a comparison
+        return getRuntime().getNil();
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
         RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
         
         ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), proxyClass);
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
             RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
 
             ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), proxyClass);
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
         RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
 
         ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), proxyClass);
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
         RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
         ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), proxyClass);
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
         
         RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
 
         ArrayJavaProxy proxy = new ArrayJavaProxy(runtime, proxyClass);
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
 
         // instance methods only; static methods are local to the class and always bound
         for (Method m: javaClass.getDeclaredMethods()) {
             int modifiers = m.getModifiers();
             if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
                 if (Modifier.isStatic(modifiers)) {
                     // static methods are always bound
                     list2.add(m);
                 } else {
                     List<Method> methods = nameMethods.get(m.getName());
                     if (methods == null) {
                         nameMethods.put(m.getName(), methods = new ArrayList<Method>());
                     }
                     methods.add(m);
                 }
             }
         }
 
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
         
         return list2.toArray(new Method[list2.size()]);
     }
 }
diff --git a/src/org/jruby/javasupport/JavaObject.java b/src/org/jruby/javasupport/JavaObject.java
index dc3052c478..66cb82d669 100644
--- a/src/org/jruby/javasupport/JavaObject.java
+++ b/src/org/jruby/javasupport/JavaObject.java
@@ -1,291 +1,325 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
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
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
+import org.jruby.java.proxies.JavaProxy;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Java::JavaObject")
 public class JavaObject extends RubyObject {
 
     private static Object NULL_LOCK = new Object();
     private final RubyClass.VariableAccessor objectAccessor;
 
     protected JavaObject(Ruby runtime, RubyClass rubyClass, Object value) {
         super(runtime, rubyClass);
         objectAccessor = rubyClass.getVariableAccessorForWrite("__wrap_struct__");
         dataWrapStruct(value);
     }
 
     @Override
     public Object dataGetStruct() {
         return objectAccessor.get(this);
     }
 
     @Override
     public void dataWrapStruct(Object object) {
         objectAccessor.set(this, object);
     }
 
     protected JavaObject(Ruby runtime, Object value) {
         this(runtime, runtime.getJavaSupport().getJavaObjectClass(), value);
     }
 
     public static JavaObject wrap(Ruby runtime, Object value) {
         if (value != null) {
             if (value instanceof Class) {
                 return JavaClass.get(runtime, (Class<?>) value);
             } else if (value.getClass().isArray()) {
                 return new JavaArray(runtime, value);
             }
         }
         return new JavaObject(runtime, value);
     }
 
+    @JRubyMethod(meta = true)
+    public static IRubyObject wrap(ThreadContext context, IRubyObject self, IRubyObject object) {
+        Ruby runtime = context.getRuntime();
+        Object obj = getWrappedObject(object, NEVER);
+
+        if (obj == NEVER) return runtime.getNil();
+
+        return wrap(runtime, obj);
+    }
+
     @Override
     public Class<?> getJavaClass() {
         Object dataStruct = dataGetStruct();
         return dataStruct != null ? dataStruct.getClass() : Void.TYPE;
     }
 
     public Object getValue() {
         return dataGetStruct();
     }
 
     public static RubyClass createJavaObjectClass(Ruby runtime, RubyModule javaModule) {
         // FIXME: Ideally JavaObject instances should be marshallable, which means that
         // the JavaObject metaclass should have an appropriate allocator. JRUBY-414
         RubyClass result = javaModule.defineClassUnder("JavaObject", runtime.getObject(), JAVA_OBJECT_ALLOCATOR);
 
         registerRubyMethods(runtime, result);
 
         result.getMetaClass().undefineMethod("new");
         result.getMetaClass().undefineMethod("allocate");
 
         return result;
     }
 
     protected static void registerRubyMethods(Ruby runtime, RubyClass result) {
         result.defineAnnotatedMethods(JavaObject.class);
     }
 
     @Override
     public boolean equals(Object other) {
-        return other instanceof JavaObject &&
-                this.dataGetStruct() == ((JavaObject) other).dataGetStruct();
+        Ruby runtime = getRuntime();
+        Object myValue = getValue();
+        Object otherValue = other;
+        if (other instanceof IRubyObject) {
+            otherValue = getWrappedObject((IRubyObject)other, NEVER);
+        }
+
+        if (otherValue == NEVER) {
+            // not a wrapped object
+            return false;
+        }
+        return myValue == otherValue;
     }
 
     @Override
     public int hashCode() {
         Object dataStruct = dataGetStruct();
         if (dataStruct != null) {
             return dataStruct.hashCode();
         }
         return 0;
     }
 
     @JRubyMethod
     @Override
     public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
 
     @JRubyMethod
     @Override
     public IRubyObject to_s() {
         Object dataStruct = dataGetStruct();
         if (dataStruct != null) {
             String stringValue = dataStruct.toString();
             if (stringValue != null) {
                 return RubyString.newUnicodeString(getRuntime(), dataStruct.toString());
             }
 
             return getRuntime().getNil();
         }
         return RubyString.newEmptyString(getRuntime());
     }
 
     @JRubyMethod(name = {"==", "eql?"}, required = 1)
     public IRubyObject op_equal(IRubyObject other) {
-        if (!(other instanceof JavaObject)) {
-            other = (JavaObject)other.dataGetStruct();
-            if (!(other instanceof JavaObject)) {
-                return getRuntime().getFalse();
-            }
+        Ruby runtime = getRuntime();
+        Object myValue = getValue();
+        Object otherValue = getWrappedObject(other, NEVER);
+        
+        if (other == NEVER) {
+            // not a wrapped object
+            return runtime.getFalse();
         }
 
-        if (getValue() == null && ((JavaObject) other).getValue() == null) {
-            return getRuntime().getTrue();
+        if (myValue == null && otherValue == null) {
+            return runtime.getTrue();
         }
 
-        boolean isEqual = getValue().equals(((JavaObject) other).getValue());
-        return isEqual ? getRuntime().getTrue() : getRuntime().getFalse();
+        return runtime.newBoolean(myValue.equals(otherValue));
     }
 
     @JRubyMethod(name = "equal?", required = 1)
     public IRubyObject same(IRubyObject other) {
-        if (!(other instanceof JavaObject)) {
-            other = (JavaObject)other.dataGetStruct();
-            if (!(other instanceof JavaObject)) {
-                return getRuntime().getFalse();
-            }
+        Ruby runtime = getRuntime();
+        Object myValue = getValue();
+        Object otherValue = getWrappedObject(other, NEVER);
+
+        if (other == NEVER) {
+            // not a wrapped object
+            return runtime.getFalse();
         }
 
-        if (getValue() == null && ((JavaObject) other).getValue() == null) {
+        if (myValue == null && otherValue == null) {
             return getRuntime().getTrue();
         }
 
         boolean isSame = getValue() == ((JavaObject) other).getValue();
         return isSame ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
+    private static Object getWrappedObject(IRubyObject other, Object def) {
+        if (other instanceof JavaObject) {
+            return ((JavaObject)other).getValue();
+        } else if (other instanceof JavaProxy) {
+            return ((JavaProxy)other).getObject();
+        } else {
+            return def;
+        }
+    }
+
     @JRubyMethod
     public RubyString java_type() {
         return getRuntime().newString(getJavaClass().getName());
     }
 
     @JRubyMethod
     public IRubyObject java_class() {
         return JavaClass.get(getRuntime(), getJavaClass());
     }
 
     @JRubyMethod
     public RubyFixnum length() {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject aref(IRubyObject index) {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "[]=", required = 2)
     public IRubyObject aset(IRubyObject index, IRubyObject someValue) {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "fill", required = 3)
     public IRubyObject afill(IRubyObject beginIndex, IRubyObject endIndex, IRubyObject someValue) {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "java_proxy?")
     public IRubyObject is_java_proxy() {
         return getRuntime().getTrue();
     }
 
     @JRubyMethod(name = "synchronized")
     public IRubyObject ruby_synchronized(ThreadContext context, Block block) {
         Object lock = getValue();
         synchronized (lock != null ? lock : NULL_LOCK) {
             return block.yield(context, null);
         }
     }
     
     @JRubyMethod(frame = true)
     public IRubyObject marshal_dump() {
         if (Serializable.class.isAssignableFrom(getJavaClass())) {
             try {
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos);
 
                 oos.writeObject(getValue());
 
                 return getRuntime().newString(new ByteList(baos.toByteArray()));
             } catch (IOException ioe) {
                 throw getRuntime().newIOErrorFromException(ioe);
             }
         } else {
             throw getRuntime().newTypeError("no marshal_dump is defined for class " + getJavaClass());
         }
     }
 
     @JRubyMethod(frame = true)
     public IRubyObject marshal_load(ThreadContext context, IRubyObject str) {
         try {
             ByteList byteList = str.convertToString().getByteList();
             ByteArrayInputStream bais = new ByteArrayInputStream(byteList.getUnsafeBytes(), byteList.getBegin(), byteList.getRealSize());
             ObjectInputStream ois = new ObjectInputStream(bais);
 
             dataWrapStruct(ois.readObject());
 
             return this;
         } catch (IOException ioe) {
             throw context.getRuntime().newIOErrorFromException(ioe);
         } catch (ClassNotFoundException cnfe) {
             throw context.getRuntime().newTypeError("Class not found unmarshaling Java type: " + cnfe.getLocalizedMessage());
         }
     }
 
     @Override
     public Object toJava(Class cls) {
         if (getValue() == null) {
             // THIS SHOULD NEVER HAPPEN, but it DOES
             return getValue();
         }
         
         if (cls.isAssignableFrom(getValue().getClass())) {
             return getValue();
         }
         throw getRuntime().newTypeError("cannot convert instance of " + getValue().getClass() + " to " + cls);
     }
 
     private static final ObjectAllocator JAVA_OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
             return new JavaObject(runtime, klazz, null);
         }
     };
 
 }
diff --git a/src/org/jruby/javasupport/JavaProxyMethods.java b/src/org/jruby/javasupport/JavaProxyMethods.java
index 99936a44bf..6844d29c41 100644
--- a/src/org/jruby/javasupport/JavaProxyMethods.java
+++ b/src/org/jruby/javasupport/JavaProxyMethods.java
@@ -1,76 +1,81 @@
 package org.jruby.javasupport;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBasicObject;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaProxyMethods {
     public static RubyModule createJavaProxyMethods(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         RubyModule javaProxyMethods = runtime.defineModule("JavaProxyMethods");
         
         javaProxyMethods.defineAnnotatedMethods(JavaProxyMethods.class);
         
         return javaProxyMethods;
     }
     
     @JRubyMethod
     public static IRubyObject java_class(ThreadContext context, IRubyObject recv) {
         return recv.getMetaClass().getRealClass().fastGetInstanceVariable("@java_class");
     }
 
     @JRubyMethod
     public static IRubyObject java_object(ThreadContext context, IRubyObject recv) {
         return (IRubyObject)recv.dataGetStruct();
     }
 
     @JRubyMethod(name = "java_object=")
     public static IRubyObject java_object_set(ThreadContext context, IRubyObject recv, IRubyObject obj) {
         // XXX: Check if it's appropriate type?
         recv.dataWrapStruct(obj);
         return obj;
     }
 
     @JRubyMethod(name = {"=="})
     public static IRubyObject op_equal(IRubyObject recv, IRubyObject rhs) {
         return ((JavaObject)recv.dataGetStruct()).op_equal(rhs);
     }
     
     @JRubyMethod
     public static IRubyObject to_s(IRubyObject recv) {
         if(recv.dataGetStruct() != null) {
             return ((JavaObject)recv.dataGetStruct()).to_s();
         } else {
             return ((RubyObject)recv).to_s();
         }
     }
 
     @JRubyMethod
     public static IRubyObject inspect(IRubyObject recv) {
         if (recv instanceof RubyBasicObject) {
             return ((RubyBasicObject)recv).hashyInspect();
         } else {
             return recv.inspect();
         }
     }
     
     @JRubyMethod(name = "eql?")
     public static IRubyObject op_eql(IRubyObject recv, IRubyObject rhs) {
         return ((JavaObject)recv.dataGetStruct()).op_equal(rhs);
     }
     
     @JRubyMethod
     public static IRubyObject hash(IRubyObject recv) {
         return ((JavaObject)recv.dataGetStruct()).hash();
     }
+
+    @JRubyMethod
+    public static IRubyObject to_java_object(IRubyObject recv) {
+        return (JavaObject)recv.dataGetStruct();
+    }
     
     @JRubyMethod(name = "synchronized")
     public static IRubyObject rbSynchronized(ThreadContext context, IRubyObject recv, Block block) {
         return ((JavaObject)recv.dataGetStruct()).ruby_synchronized(context, block);
     }
 }
