diff --git a/spec/java_integration/methods/java_send_spec.rb b/spec/java_integration/methods/java_send_spec.rb
index 9e4d9776ba..a4e63abcd6 100644
--- a/spec/java_integration/methods/java_send_spec.rb
+++ b/spec/java_integration/methods/java_send_spec.rb
@@ -1,37 +1,53 @@
 require 'java'
 
 describe "A Java object's java_send method" do
   before :each do
     @list = java.util.ArrayList.new
     @integer = java.lang.Integer.new(1)
   end
   
   it "works with name only for no-arg methods" do
-    m = @list.java_send(:toString).should == "[]"
+    @list.java_send(:toString).should == "[]"
+    java.lang.System.java_send(:currentTimeMillis).class.should == Fixnum
   end
 
   it "works with name plus empty arg list for no-arg methods" do
-    m = @list.java_send(:toString, []).should == "[]"
+    @list.java_send(:toString, []).should == "[]"
+    java.lang.System.java_send(:currentTimeMillis, []).class.should == Fixnum
   end
   
   it "works with a signature" do
     @list.java_send :add, [Java::int, java.lang.Object], 0, 'foo'
     @list.to_s.should == "[foo]"
+    java_home = java.lang.System.java_send(:getProperty, [java.lang.String], 'java.home')
+    java_home.should == java.lang.System.getProperty('java.home')
   end
   
   it "raises NameError if the method can't be found" do
     lambda do
       @list.java_send :foobar
     end.should raise_error(NameError)
     
     lambda do
       @list.java_send :add, [Java::long, java.lang.Object], 0, 'foo'
     end.should raise_error(NameError)
+
+    lambda do
+      java.lang.System.java_send :foobar
+    end.should raise_error(NameError)
+
+    lambda do
+      java.lang.System.java_send :getProperty, [Java::long, Java::long], 0, 0
+    end.should raise_error(NameError)
   end
   
   it "raises ArgumentError if type count doesn't match arg count" do
     lambda do
       @list.java_send :add, [Java::int, java.lang.Object], 0, 'foo', 'bar'
     end.should raise_error(ArgumentError)
+
+    lambda do
+      java.lang.System.java_send :getProperty, [java.lang.String], "foo", "bar"
+    end.should raise_error(ArgumentError)
   end
 end
\ No newline at end of file
diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index b64b9a9af7..59ae70cd47 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -1,1151 +1,1209 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
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
 
 import org.jruby.java.proxies.JavaInterfaceTemplate;
 import org.jruby.java.addons.KernelJavaAddons;
 import java.io.IOException;
 import java.lang.reflect.Constructor;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.Proxy;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.HashSet;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBasicObject;
 import org.jruby.RubyClass;
 import org.jruby.RubyClassPathVariable;
 import org.jruby.RubyException;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyMethod;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyString;
 import org.jruby.RubyUnboundMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.util.ClassProvider;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodN;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodZero;
 import org.jruby.java.addons.ArrayJavaAddons;
 import org.jruby.java.addons.IOJavaAddons;
 import org.jruby.java.addons.StringJavaAddons;
 import org.jruby.java.codegen.RealClassGenerator;
 import org.jruby.java.dispatch.CallableSelector;
 import org.jruby.java.invokers.InstanceMethodInvoker;
 import org.jruby.java.invokers.MethodInvoker;
 import org.jruby.java.invokers.StaticMethodInvoker;
 import org.jruby.java.proxies.ArrayJavaProxy;
 import org.jruby.java.proxies.ConcreteJavaProxy;
 import org.jruby.java.proxies.InterfaceJavaProxy;
 import org.jruby.java.proxies.JavaProxy;
 import org.jruby.java.proxies.RubyObjectHolderProxy;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.SafePropertyAccessor;
 
 @JRubyModule(name = "Java")
 public class Java implements Library {
     public static final boolean NEW_STYLE_EXTENSION = SafePropertyAccessor.getBoolean("jruby.ji.newStyleExtension", false);
 
     public void load(Ruby runtime, boolean wrap) throws IOException {
         createJavaModule(runtime);
         runtime.getLoadService().smartLoad("builtin/javasupport");
         RubyClassPathVariable.createClassPathVariable(runtime);
     }
 
     public static RubyModule createJavaModule(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         RubyModule javaModule = runtime.defineModule("Java");
         
         javaModule.defineAnnotatedMethods(Java.class);
 
         JavaObject.createJavaObjectClass(runtime, javaModule);
         JavaArray.createJavaArrayClass(runtime, javaModule);
         JavaClass.createJavaClassClass(runtime, javaModule);
         JavaMethod.createJavaMethodClass(runtime, javaModule);
         JavaConstructor.createJavaConstructorClass(runtime, javaModule);
         JavaField.createJavaFieldClass(runtime, javaModule);
         
         // set of utility methods for Java-based proxy objects
         JavaProxyMethods.createJavaProxyMethods(context);
         
         // the proxy (wrapper) type hierarchy
         JavaProxy.createJavaProxy(context);
         ConcreteJavaProxy.createConcreteJavaProxy(context);
         InterfaceJavaProxy.createInterfaceJavaProxy(context);
         ArrayJavaProxy.createArrayJavaProxy(context);
 
         // also create the JavaProxy* classes
         JavaProxyClass.createJavaProxyModule(runtime);
 
         // The template for interface modules
         JavaInterfaceTemplate.createJavaInterfaceTemplateModule(context);
 
         RubyModule javaUtils = runtime.defineModule("JavaUtilities");
         
         javaUtils.defineAnnotatedMethods(JavaUtilities.class);
 
         JavaArrayUtilities.createJavaArrayUtilitiesModule(runtime);
         
         // Now attach Java-related extras to core classes
         runtime.getArray().defineAnnotatedMethods(ArrayJavaAddons.class);
         runtime.getKernel().defineAnnotatedMethods(KernelJavaAddons.class);
         runtime.getString().defineAnnotatedMethods(StringJavaAddons.class);
         runtime.getIO().defineAnnotatedMethods(IOJavaAddons.class);
 
         if (runtime.getObject().isConstantDefined("StringIO")) {
             ((RubyClass)runtime.getObject()).defineAnnotatedMethods(IOJavaAddons.AnyIO.class);
         }
         
         // add all name-to-class mappings
         addNameClassMappings(runtime, runtime.getJavaSupport().getNameClassMap());
         
         // add some base Java classes everyone will need
         runtime.getJavaSupport().setObjectJavaClass(JavaClass.get(runtime, Object.class));
         
         // finally, set JavaSupport.isEnabled to true
         runtime.getJavaSupport().setActive(true);
 
         return javaModule;
     }
 
     public static class OldStyleExtensionInherited {
         @JRubyMethod
         public static IRubyObject inherited(IRubyObject recv, IRubyObject arg0) {
             return Java.concrete_proxy_inherited(recv, arg0);
         }
     };
 
     public static class NewStyleExtensionInherited {
         @JRubyMethod
         public static IRubyObject inherited(IRubyObject recv, IRubyObject arg0) {
             if (!(arg0 instanceof RubyClass)) {
                 throw recv.getRuntime().newTypeError(arg0, recv.getRuntime().getClassClass());
             }
             
             JavaInterfaceTemplate.addRealImplClassNew((RubyClass)arg0);
             return recv.getRuntime().getNil();
         }
     };
     
     /**
      * This populates the master map from short-cut names to JavaClass instances for
      * a number of core Java types.
      * 
      * @param runtime
      * @param nameClassMap
      */
     private static void addNameClassMappings(Ruby runtime, Map<String, JavaClass> nameClassMap) {
         JavaClass booleanPrimClass = JavaClass.get(runtime, Boolean.TYPE);
         JavaClass booleanClass = JavaClass.get(runtime, Boolean.class);
         nameClassMap.put("boolean", booleanPrimClass);
         nameClassMap.put("Boolean", booleanClass);
         nameClassMap.put("java.lang.Boolean", booleanClass);
         
         JavaClass bytePrimClass = JavaClass.get(runtime, Byte.TYPE);
         JavaClass byteClass = JavaClass.get(runtime, Byte.class);
         nameClassMap.put("byte", bytePrimClass);
         nameClassMap.put("Byte", byteClass);
         nameClassMap.put("java.lang.Byte", byteClass);
         
         JavaClass shortPrimClass = JavaClass.get(runtime, Short.TYPE);
         JavaClass shortClass = JavaClass.get(runtime, Short.class);
         nameClassMap.put("short", shortPrimClass);
         nameClassMap.put("Short", shortClass);
         nameClassMap.put("java.lang.Short", shortClass);
         
         JavaClass charPrimClass = JavaClass.get(runtime, Character.TYPE);
         JavaClass charClass = JavaClass.get(runtime, Character.class);
         nameClassMap.put("char", charPrimClass);
         nameClassMap.put("Character", charClass);
         nameClassMap.put("Char", charClass);
         nameClassMap.put("java.lang.Character", charClass);
         
         JavaClass intPrimClass = JavaClass.get(runtime, Integer.TYPE);
         JavaClass intClass = JavaClass.get(runtime, Integer.class);
         nameClassMap.put("int", intPrimClass);
         nameClassMap.put("Integer", intClass);
         nameClassMap.put("Int", intClass);
         nameClassMap.put("java.lang.Integer", intClass);
         
         JavaClass longPrimClass = JavaClass.get(runtime, Long.TYPE);
         JavaClass longClass = JavaClass.get(runtime, Long.class);
         nameClassMap.put("long", longPrimClass);
         nameClassMap.put("Long", longClass);
         nameClassMap.put("java.lang.Long", longClass);
         
         JavaClass floatPrimClass = JavaClass.get(runtime, Float.TYPE);
         JavaClass floatClass = JavaClass.get(runtime, Float.class);
         nameClassMap.put("float", floatPrimClass);
         nameClassMap.put("Float", floatClass);
         nameClassMap.put("java.lang.Float", floatClass);
         
         JavaClass doublePrimClass = JavaClass.get(runtime, Double.TYPE);
         JavaClass doubleClass = JavaClass.get(runtime, Double.class);
         nameClassMap.put("double", doublePrimClass);
         nameClassMap.put("Double", doubleClass);
         nameClassMap.put("java.lang.Double", doubleClass);
         
         JavaClass bigintClass = JavaClass.get(runtime, BigInteger.class);
         nameClassMap.put("big_int", bigintClass);
         nameClassMap.put("big_integer", bigintClass);
         nameClassMap.put("BigInteger", bigintClass);
         nameClassMap.put("java.math.BigInteger", bigintClass);
         
         JavaClass bigdecimalClass = JavaClass.get(runtime, BigDecimal.class);
         nameClassMap.put("big_decimal", bigdecimalClass);
         nameClassMap.put("BigDecimal", bigdecimalClass);
         nameClassMap.put("java.math.BigDecimal", bigdecimalClass);
         
         JavaClass objectClass = JavaClass.get(runtime, Object.class);
         nameClassMap.put("object", objectClass);
         nameClassMap.put("Object", objectClass);
         nameClassMap.put("java.lang.Object", objectClass);
         
         JavaClass stringClass = JavaClass.get(runtime, String.class);
         nameClassMap.put("string", stringClass);
         nameClassMap.put("String", stringClass);
         nameClassMap.put("java.lang.String", stringClass);
     }
 
     private static final ClassProvider JAVA_PACKAGE_CLASS_PROVIDER = new ClassProvider() {
 
         public RubyClass defineClassUnder(RubyModule pkg, String name, RubyClass superClazz) {
             // shouldn't happen, but if a superclass is specified, it's not ours
             if (superClazz != null) {
                 return null;
             }
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) {
                 return null;
             }
             Ruby runtime = pkg.getRuntime();
             return (RubyClass) get_proxy_class(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
 
         public RubyModule defineModuleUnder(RubyModule pkg, String name) {
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) {
                 return null;
             }
             Ruby runtime = pkg.getRuntime();
             return (RubyModule) get_interface_module(
                     runtime,
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
     };
 
     private static final Map<String, Boolean> JAVA_PRIMITIVES = new HashMap<String, Boolean>();
     static {
         String[] primitives = {"boolean", "byte", "char", "short", "int", "long", "float", "double"};
         for (String primitive : primitives) {
             JAVA_PRIMITIVES.put(primitive, Boolean.TRUE);
         }
     }
 
     public static IRubyObject create_proxy_class(
             IRubyObject recv,
             IRubyObject constant,
             IRubyObject javaClass,
             IRubyObject module) {
         if (!(module instanceof RubyModule)) {
             throw recv.getRuntime().newTypeError(module, recv.getRuntime().getModule());
         }
         return ((RubyModule) module).const_set(constant, get_proxy_class(recv, javaClass));
     }
 
     public static IRubyObject get_java_class(IRubyObject recv, IRubyObject name) {
         try {
             return JavaClass.for_name(recv, name);
         } catch (Exception e) {
             recv.getRuntime().getJavaSupport().handleNativeException(e, null);
             return recv.getRuntime().getNil();
         }
     }
 
     /**
      * Returns a new proxy instance of a type corresponding to rawJavaObject's class,
      * or the cached proxy if we've already seen this object.  Note that primitives
      * and strings are <em>not</em> coerced to corresponding Ruby types; use
      * JavaUtil.convertJavaToUsableRubyObject to get coerced types or proxies as
      * appropriate.
      * 
      * @param runtime
      * @param rawJavaObject
      * @return the new or cached proxy for the specified Java object
      * @see JavaUtil.convertJavaToUsableRubyObject
      */
     public static IRubyObject getInstance(Ruby runtime, Object rawJavaObject) {
         if (rawJavaObject != null) {
             return runtime.getJavaSupport().getObjectProxyCache().getOrCreate(rawJavaObject,
                     (RubyClass) getProxyClass(runtime, JavaClass.get(runtime, rawJavaObject.getClass())));
         }
         return runtime.getNil();
     }
 
     public static RubyModule getInterfaceModule(Ruby runtime, JavaClass javaClass) {
         if (!javaClass.javaClass().isInterface()) {
             throw runtime.newArgumentError(javaClass.toString() + " is not an interface");
         }
         RubyModule interfaceModule;
         if ((interfaceModule = javaClass.getProxyModule()) != null) {
             return interfaceModule;
         }
         javaClass.lockProxy();
         try {
             if ((interfaceModule = javaClass.getProxyModule()) == null) {
                 interfaceModule = (RubyModule) runtime.getJavaSupport().getJavaInterfaceTemplate().dup();
                 interfaceModule.fastSetInstanceVariable("@java_class", javaClass);
                 addToJavaPackageModule(interfaceModule, javaClass);
                 javaClass.setupInterfaceModule(interfaceModule);
                 // include any interfaces we extend
                 Class<?>[] extended = javaClass.javaClass().getInterfaces();
                 for (int i = extended.length; --i >= 0;) {
                     JavaClass extendedClass = JavaClass.get(runtime, extended[i]);
                     RubyModule extModule = getInterfaceModule(runtime, extendedClass);
                     interfaceModule.includeModule(extModule);
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return interfaceModule;
     }
 
     public static IRubyObject get_interface_module(Ruby runtime, IRubyObject javaClassObject) {
         JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.forNameVerbose(runtime, javaClassObject.asJavaString());
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass) javaClassObject;
         } else {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         return getInterfaceModule(runtime, javaClass);
     }
 
     public static RubyClass getProxyClassForObject(Ruby runtime, Object object) {
         return (RubyClass)getProxyClass(runtime, JavaClass.get(runtime, object.getClass()));
     }
 
     public static RubyModule getProxyClass(Ruby runtime, JavaClass javaClass) {
         RubyClass proxyClass;
         final Class<?> c = javaClass.javaClass();
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         if (c.isInterface()) {
             return getInterfaceModule(runtime, javaClass);
         }
         javaClass.lockProxy();
         try {
             if ((proxyClass = javaClass.getProxyClass()) == null) {
 
                 if (c.isArray()) {
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getArrayProxyClass(),
                             javaClass, true);
 
                 } else if (c.isPrimitive()) {
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
 
                 } else if (c == Object.class) {
                     // java.lang.Object is added at root of java proxy classes
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
                     if (NEW_STYLE_EXTENSION) {
                         proxyClass.getMetaClass().defineAnnotatedMethods(NewStyleExtensionInherited.class);
                     } else {
                         proxyClass.getMetaClass().defineAnnotatedMethods(OldStyleExtensionInherited.class);
                     }
                     addToJavaPackageModule(proxyClass, javaClass);
 
                 } else {
                     // other java proxy classes added under their superclass' java proxy
                     proxyClass = createProxyClass(runtime,
                             (RubyClass) getProxyClass(runtime, JavaClass.get(runtime, c.getSuperclass())),
                             javaClass, false);
 
                     // include interface modules into the proxy class
                     Class<?>[] interfaces = c.getInterfaces();
                     for (int i = interfaces.length; --i >= 0;) {
                         JavaClass ifc = JavaClass.get(runtime, interfaces[i]);
                         proxyClass.includeModule(getInterfaceModule(runtime, ifc));
                     }
                     if (Modifier.isPublic(c.getModifiers())) {
                         addToJavaPackageModule(proxyClass, javaClass);
                     }
                 }
 
                 // JRUBY-1000, fail early when attempting to subclass a final Java class;
                 // solved here by adding an exception-throwing "inherited"
                 if (Modifier.isFinal(c.getModifiers())) {
                     proxyClass.getMetaClass().addMethod("inherited", new org.jruby.internal.runtime.methods.JavaMethod() {
                         @Override
                         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                             throw context.getRuntime().newTypeError("can not extend final Java class: " + c.getCanonicalName());
                         }
                     });
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
 
     public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject java_class_object) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
         if (java_class_object instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, java_class_object);
         } else if (java_class_object instanceof JavaClass) {
             javaClass = (JavaClass) java_class_object;
         } else {
             throw runtime.newTypeError(java_class_object, runtime.getJavaSupport().getJavaClassClass());
         }
         return getProxyClass(runtime, javaClass);
     }
 
     private static RubyClass createProxyClass(Ruby runtime, RubyClass baseType,
             JavaClass javaClass, boolean invokeInherited) {
 	// JRUBY-2938 the proxy class might already exist
 	RubyClass proxyClass = javaClass.getProxyClass();
 	if (proxyClass != null)
 	    return proxyClass;
 
         // this needs to be split, since conditional calling #inherited doesn't fit standard ruby semantics
         RubyClass.checkInheritable(baseType);
         RubyClass superClass = (RubyClass) baseType;
         proxyClass = RubyClass.newClass(runtime, superClass);
         proxyClass.makeMetaClass(superClass.getMetaClass());
         proxyClass.setAllocator(superClass.getAllocator());
         if (invokeInherited) {
             proxyClass.inherit(superClass);
         }
         proxyClass.callMethod(runtime.getCurrentContext(), "java_class=", javaClass);
         javaClass.setupProxy(proxyClass);
 
         // add java_method for unbound use
         proxyClass.defineAnnotatedMethods(JavaProxyClassMethods.class);
         return proxyClass;
     }
 
     public static class JavaProxyClassMethods {
         @JRubyMethod(backtrace = true, meta = true)
         public static IRubyObject java_method(ThreadContext context, IRubyObject proxyClass, IRubyObject rubyName) {
             String name = rubyName.asJavaString();
 
             return getRubyMethod(context, proxyClass, name);
         }
 
         @JRubyMethod(backtrace = true, meta = true)
         public static IRubyObject java_method(ThreadContext context, IRubyObject proxyClass, IRubyObject rubyName, IRubyObject argTypes) {
             String name = rubyName.asJavaString();
             RubyArray argTypesAry = argTypes.convertToArray();
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
 
             return getRubyMethod(context, proxyClass, name, argTypesClasses);
         }
 
+        @JRubyMethod(backtrace = true, meta = true)
+        public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject rubyName) {
+            String name = rubyName.asJavaString();
+            Ruby runtime = context.getRuntime();
+
+            JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name));
+            return method.invokeStaticDirect();
+        }
+
+        @JRubyMethod(backtrace = true, meta = true)
+        public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject rubyName, IRubyObject argTypes) {
+            String name = rubyName.asJavaString();
+            RubyArray argTypesAry = argTypes.convertToArray();
+            Ruby runtime = context.getRuntime();
+
+            if (argTypesAry.size() != 0) {
+                Class[] argTypesClasses = (Class[]) argTypesAry.toArray(new Class[argTypesAry.size()]);
+                throw JavaMethod.newArgSizeMismatchError(runtime, argTypesClasses);
+            }
+
+            JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name));
+            return method.invokeStaticDirect();
+        }
+
+        @JRubyMethod(backtrace = true, meta = true)
+        public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject rubyName, IRubyObject argTypes, IRubyObject arg0) {
+            String name = rubyName.asJavaString();
+            RubyArray argTypesAry = argTypes.convertToArray();
+            Ruby runtime = context.getRuntime();
+
+            if (argTypesAry.size() != 1) {
+                throw JavaMethod.newArgSizeMismatchError(runtime, (Class) argTypesAry.eltInternal(0).toJava(Class.class));
+            }
+
+            Class argTypeClass = (Class) argTypesAry.eltInternal(0).toJava(Class.class);
+
+            JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name, argTypeClass));
+            return method.invokeStaticDirect(arg0.toJava(argTypeClass));
+        }
+
+        @JRubyMethod(required = 4, rest = true, backtrace = true, meta = true)
+        public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+            Ruby runtime = context.getRuntime();
+
+            String name = args[0].asJavaString();
+            RubyArray argTypesAry = args[1].convertToArray();
+            int argsLen = args.length - 2;
+
+            if (argTypesAry.size() != argsLen) {
+                throw JavaMethod.newArgSizeMismatchError(runtime, (Class[]) argTypesAry.toArray(new Class[argTypesAry.size()]));
+            }
+
+            Class[] argTypesClasses = (Class[]) argTypesAry.toArray(new Class[argsLen]);
+
+            Object[] argsAry = new Object[argsLen];
+            for (int i = 0; i < argsLen; i++) {
+                argsAry[i] = args[i + 2].toJava(argTypesClasses[i]);
+            }
+
+            JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name, argTypesClasses));
+            return method.invokeStaticDirect(argsAry);
+        }
+
         @JRubyMethod(backtrace = true, meta = true, visibility = Visibility.PRIVATE)
         public static IRubyObject java_alias(ThreadContext context, IRubyObject proxyClass, IRubyObject newName, IRubyObject rubyName) {
             return java_alias(context, proxyClass, newName, rubyName, context.getRuntime().newEmptyArray());
         }
 
         @JRubyMethod(backtrace = true, meta = true, visibility = Visibility.PRIVATE)
         public static IRubyObject java_alias(ThreadContext context, IRubyObject proxyClass, IRubyObject newName, IRubyObject rubyName, IRubyObject argTypes) {
             String name = rubyName.asJavaString();
             String newNameStr = newName.asJavaString();
             RubyArray argTypesAry = argTypes.convertToArray();
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
             Ruby runtime = context.getRuntime();
             RubyClass rubyClass;
 
             if (proxyClass instanceof RubyClass) {
                 rubyClass = (RubyClass)proxyClass;
             } else {
                 throw runtime.newTypeError(proxyClass, runtime.getModule());
             }
 
-            Class jclass = (Class)((JavaClass)proxyClass.callMethod(context, "java_class")).getValue();
-
-            Method method = getMethodFromClass(runtime, jclass, name, argTypesClasses);
+            Method method = getMethodFromClass(runtime, proxyClass, name, argTypesClasses);
             MethodInvoker invoker = getMethodInvokerForMethod(rubyClass, method);
 
             if (Modifier.isStatic(method.getModifiers())) {
                 // add alias to meta
                 rubyClass.getSingletonClass().addMethod(newNameStr, invoker);
             } else {
                 rubyClass.addMethod(newNameStr, invoker);
             }
 
             return runtime.getNil();
         }
     }
 
     private static IRubyObject getRubyMethod(ThreadContext context, IRubyObject proxyClass, String name, Class... argTypesClasses) {
         Ruby runtime = context.getRuntime();
         RubyClass rubyClass;
         
         if (proxyClass instanceof RubyClass) {
             rubyClass = (RubyClass)proxyClass;
         } else {
             throw runtime.newTypeError(proxyClass, runtime.getModule());
         }
 
-        Class jclass = (Class)((JavaClass)proxyClass.callMethod(context, "java_class")).getValue();
+        Method jmethod = getMethodFromClass(runtime, proxyClass, name, argTypesClasses);
         String prettyName = name + CodegenUtils.prettyParams(argTypesClasses);
         
-        try {
-            Method jmethod = jclass.getMethod(name, argTypesClasses);
-            MethodInvoker invoker;
-            if (Modifier.isStatic(jmethod.getModifiers())) {
-                invoker = new StaticMethodInvoker(rubyClass, jmethod);
-                return RubyMethod.newMethod(rubyClass, prettyName, rubyClass, name, invoker, proxyClass);
-            } else {
-                invoker = new InstanceMethodInvoker(rubyClass, jmethod);
-                return RubyUnboundMethod.newUnboundMethod(rubyClass, prettyName, rubyClass, name, invoker);
-            }
-        } catch (NoSuchMethodException nsme) {
-            String errorName = jclass.getName() + "." + prettyName;
-            throw runtime.newNameError("Java method not found: " + errorName, name);
+        if (Modifier.isStatic(jmethod.getModifiers())) {
+            MethodInvoker invoker = new StaticMethodInvoker(rubyClass, jmethod);
+            return RubyMethod.newMethod(rubyClass, prettyName, rubyClass, name, invoker, proxyClass);
+        } else {
+            MethodInvoker invoker = new InstanceMethodInvoker(rubyClass, jmethod);
+            return RubyUnboundMethod.newUnboundMethod(rubyClass, prettyName, rubyClass, name, invoker);
         }
     }
 
-    public static Method getMethodFromClass(Ruby runtime, Class jclass, String name, Class... argTypes) {
+    public static Method getMethodFromClass(Ruby runtime, IRubyObject proxyClass, String name, Class... argTypes) {
+        Class jclass = (Class)((JavaClass)proxyClass.callMethod(runtime.getCurrentContext(), "java_class")).getValue();
+
         try {
             return jclass.getMethod(name, argTypes);
         } catch (NoSuchMethodException nsme) {
-            throw JavaMethod.newMethodNotFoundError(runtime, jclass, name + CodegenUtils.prettyParams(argTypes), name);
+            String prettyName = name + CodegenUtils.prettyParams(argTypes);
+            String errorName = jclass.getName() + "." + prettyName;
+            throw runtime.newNameError("Java method not found: " + errorName, name);
         }
     }
 
     private static MethodInvoker getMethodInvokerForMethod(RubyClass metaClass, Method method) {
         if (Modifier.isStatic(method.getModifiers())) {
             return new StaticMethodInvoker(metaClass.getMetaClass(), method);
         } else {
             return new InstanceMethodInvoker(metaClass, method);
         }
     }
 
     public static IRubyObject concrete_proxy_inherited(IRubyObject recv, IRubyObject subclass) {
         Ruby runtime = recv.getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         JavaSupport javaSupport = runtime.getJavaSupport();
         RubyClass javaProxyClass = javaSupport.getJavaProxyClass().getMetaClass();
         RuntimeHelpers.invokeAs(tc, javaProxyClass, recv, "inherited", subclass,
                 Block.NULL_BLOCK);
         return setupJavaSubclass(tc, subclass, recv.callMethod(tc, "java_class"));
     }
 
     private static IRubyObject setupJavaSubclass(ThreadContext context, IRubyObject subclass, IRubyObject java_class) {
         Ruby runtime = context.getRuntime();
 
         if (!(subclass instanceof RubyClass)) {
             throw runtime.newTypeError(subclass, runtime.getClassClass());
         }
         RubyClass rubySubclass = (RubyClass)subclass;
         rubySubclass.getInstanceVariables().fastSetInstanceVariable("@java_proxy_class", runtime.getNil());
 
         RubyClass subclassSingleton = rubySubclass.getSingletonClass();
         subclassSingleton.addReadWriteAttribute(context, "java_proxy_class");
         subclassSingleton.addMethod("java_interfaces", new JavaMethodZero(subclassSingleton, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 IRubyObject javaInterfaces = self.getInstanceVariables().fastGetInstanceVariable("@java_interfaces");
                 if (javaInterfaces != null) return javaInterfaces.dup();
                 return context.getRuntime().getNil();
             }
         });
 
         rubySubclass.addMethod("__jcreate!", new JavaMethodN(subclassSingleton, Visibility.PUBLIC) {
             private final Map<Integer, ParameterTypes> methodCache = new HashMap<Integer, ParameterTypes>();
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
                 IRubyObject proxyClass = self.getMetaClass().getInstanceVariables().fastGetInstanceVariable("@java_proxy_class");
                 if (proxyClass == null || proxyClass.isNil()) {
                     proxyClass = JavaProxyClass.get_with_class(self, self.getMetaClass());
                     self.getMetaClass().getInstanceVariables().fastSetInstanceVariable("@java_proxy_class", proxyClass);
                 }
                 JavaProxyClass realProxyClass = (JavaProxyClass)proxyClass;
                 RubyArray constructors = realProxyClass.constructors();
                 ArrayList<JavaProxyConstructor> forArity = new ArrayList<JavaProxyConstructor>();
                 for (int i = 0; i < constructors.size(); i++) {
                     JavaProxyConstructor constructor = (JavaProxyConstructor)constructors.eltInternal(i);
                     if (constructor.getParameterTypes().length == args.length) {
                         forArity.add(constructor);
                     }
                 }
                 if (forArity.size() == 0) {
                     throw context.getRuntime().newArgumentError("wrong # of arguments for constructor");
                 }
                 JavaProxyConstructor matching = (JavaProxyConstructor)CallableSelector.matchingCallableArityN(
                         self,
                         methodCache,
                         forArity.toArray(new JavaProxyConstructor[forArity.size()]), args, args.length);
                 Object[] newArgs = new Object[args.length];
                 Class[] parameterTypes = matching.getParameterTypes();
                 for (int i = 0; i < args.length; i++) {
                     newArgs[i] = args[i].toJava(parameterTypes[i]);
                 }
                 JavaObject newObject = matching.newInstance(self, newArgs);
                 return JavaUtilities.set_java_object(self, self, newObject);
             }
         });
 
         return runtime.getNil();
     }
 
     // package scheme 2: separate module for each full package name, constructed 
     // from the camel-cased package segments: Java::JavaLang::Object, 
     private static void addToJavaPackageModule(RubyModule proxyClass, JavaClass javaClass) {
         Class<?> clazz = javaClass.javaClass();
         String fullName;
         if ((fullName = clazz.getName()) == null) {
             return;
         }
         int endPackage = fullName.lastIndexOf('.');
         // we'll only map conventional class names to modules 
         if (fullName.indexOf('$') != -1 || !Character.isUpperCase(fullName.charAt(endPackage + 1))) {
             return;
         }
         Ruby runtime = proxyClass.getRuntime();
         String packageString = endPackage < 0 ? "" : fullName.substring(0, endPackage);
         RubyModule packageModule = getJavaPackageModule(runtime, packageString);
         if (packageModule != null) {
             String className = fullName.substring(endPackage + 1);
             if (packageModule.getConstantAt(className) == null) {
                 packageModule.const_set(runtime.newSymbol(className), proxyClass);
             }
         }
     }
 
     private static RubyModule getJavaPackageModule(Ruby runtime, String packageString) {
         String packageName;
         int length = packageString.length();
         if (length == 0) {
             packageName = "Default";
         } else {
             StringBuilder buf = new StringBuilder();
             for (int start = 0, offset = 0; start < length; start = offset + 1) {
                 if ((offset = packageString.indexOf('.', start)) == -1) {
                     offset = length;
                 }
                 buf.append(Character.toUpperCase(packageString.charAt(start))).append(packageString.substring(start + 1, offset));
             }
             packageName = buf.toString();
         }
 
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject packageModule = javaModule.getConstantAt(packageName);
         if (packageModule == null) {
             return createPackageModule(javaModule, packageName, packageString);
         } else if (packageModule instanceof RubyModule) {
             return (RubyModule) packageModule;
         } else {
             return null;
         }
     }
 
     private static RubyModule createPackageModule(RubyModule parent, String name, String packageString) {
         Ruby runtime = parent.getRuntime();
         RubyModule packageModule = (RubyModule) runtime.getJavaSupport().getPackageModuleTemplate().dup();
         packageModule.fastSetInstanceVariable("@package_name", runtime.newString(
                 packageString.length() > 0 ? packageString + '.' : packageString));
 
         // this is where we'll get connected when classes are opened using
         // package module syntax.
         packageModule.addClassProvider(JAVA_PACKAGE_CLASS_PROVIDER);
 
         parent.const_set(runtime.newSymbol(name), packageModule);
         MetaClass metaClass = (MetaClass) packageModule.getMetaClass();
         metaClass.setAttached(packageModule);
         return packageModule;
     }
     private static final Pattern CAMEL_CASE_PACKAGE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");
 
     private static RubyModule getPackageModule(Ruby runtime, String name) {
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject value;
         if ((value = javaModule.getConstantAt(name)) instanceof RubyModule) {
             return (RubyModule) value;
         }
         String packageName;
         if ("Default".equals(name)) {
             packageName = "";
         } else {
             Matcher m = CAMEL_CASE_PACKAGE_SPLITTER.matcher(name);
             packageName = m.replaceAll("$1.$2").toLowerCase();
         }
         return createPackageModule(javaModule, name, packageName);
     }
 
     public static IRubyObject get_package_module(IRubyObject recv, IRubyObject symObject) {
         return getPackageModule(recv.getRuntime(), symObject.asJavaString());
     }
 
     public static IRubyObject get_package_module_dot_format(IRubyObject recv, IRubyObject dottedName) {
         Ruby runtime = recv.getRuntime();
         RubyModule module = getJavaPackageModule(runtime, dottedName.asJavaString());
         return module == null ? runtime.getNil() : module;
     }
 
     private static RubyModule getProxyOrPackageUnderPackage(ThreadContext context, final Ruby runtime,
             RubyModule parentPackage, String sym) {
         IRubyObject packageNameObj = parentPackage.fastGetInstanceVariable("@package_name");
         if (packageNameObj == null) {
             throw runtime.newArgumentError("invalid package module");
         }
         String packageName = packageNameObj.asJavaString();
         final String name = sym.trim().intern();
         if (name.length() == 0) {
             throw runtime.newArgumentError("empty class or package name");
         }
         String fullName = packageName + name;
         if (!Character.isUpperCase(name.charAt(0))) {
             // filter out any Java primitive names
             // TODO: should check against all Java reserved names here, not just primitives
             if (JAVA_PRIMITIVES.containsKey(name)) {
                 throw runtime.newArgumentError("illegal package name component: " + name);
             }
             
             // this covers the rare case of lower-case class names (and thus will
             // fail 99.999% of the time). fortunately, we'll only do this once per
             // package name. (and seriously, folks, look into best practices...)
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, fullName));
             } catch (RaiseException re) { /* expected */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* expected */ }
 
             // Haven't found a class, continue on as though it were a package
             final RubyModule packageModule = getJavaPackageModule(runtime, fullName);
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if (packageModule == null) {
                 return null;
             }
 
             // save package in singletonized parent, so we don't come back here
             memoizePackageOrClass(parentPackage, name, packageModule);
             
             return packageModule;
         } else {
             // upper case name, so most likely a class
             final RubyModule javaModule = getProxyClass(runtime, JavaClass.forNameVerbose(runtime, fullName));
 
             // save class in singletonized parent, so we don't come back here
             memoizePackageOrClass(parentPackage, name, javaModule);
 
             return javaModule;
 
         // FIXME: we should also support orgs that use capitalized package
         // names (including, embarrassingly, the one I work for), but this
         // should be enabled by a system property, as the expected default
         // behavior for an upper-case value should be (and is) to treat it
         // as a class name, and raise an exception if it's not found 
 
 //            try {
 //                return getProxyClass(runtime, JavaClass.forName(runtime, fullName));
 //            } catch (Exception e) {
 //                // but for those not hip to conventions and best practices,
 //                // we'll try as a package
 //                return getJavaPackageModule(runtime, fullName);
 //            }
         }
     }
 
     public static IRubyObject get_proxy_or_package_under_package(
             ThreadContext context,
             IRubyObject recv,
             IRubyObject parentPackage,
             IRubyObject sym) {
         Ruby runtime = recv.getRuntime();
         if (!(parentPackage instanceof RubyModule)) {
             throw runtime.newTypeError(parentPackage, runtime.getModule());
         }
         RubyModule result;
         if ((result = getProxyOrPackageUnderPackage(context, runtime,
                 (RubyModule) parentPackage, sym.asJavaString())) != null) {
             return result;
         }
         return runtime.getNil();
     }
 
     private static RubyModule getTopLevelProxyOrPackage(ThreadContext context, final Ruby runtime, String sym) {
         final String name = sym.trim().intern();
         if (name.length() == 0) {
             throw runtime.newArgumentError("empty class or package name");
         }
         if (Character.isLowerCase(name.charAt(0))) {
             // this covers primitives and (unlikely) lower-case class names
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not primitive or lc class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not primitive or lc class */ }
 
             // TODO: check for Java reserved names and raise exception if encountered
 
             final RubyModule packageModule = getJavaPackageModule(runtime, name);
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if (packageModule == null) {
                 return null;
             }
             RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
             if (javaModule.getMetaClass().isMethodBound(name, false)) {
                 return packageModule;
             }
 
             memoizePackageOrClass(javaModule, name, packageModule);
             
             return packageModule;
         } else {
             RubyModule javaModule = null;
             try {
                 javaModule = getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not a class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not a class */ }
 
             // upper-case package name
             // TODO: top-level upper-case package was supported in the previous (Ruby-based)
             // implementation, so leaving as is.  see note at #getProxyOrPackageUnderPackage
             // re: future approach below the top-level.
             if (javaModule == null) {
                 javaModule = getPackageModule(runtime, name);
             }
 
             memoizePackageOrClass(runtime.getJavaSupport().getJavaModule(), name, javaModule);
 
             return javaModule;
         }
     }
 
     private static void memoizePackageOrClass(RubyModule parentPackage, String name, final IRubyObject value) {
         RubyClass singleton = parentPackage.getSingletonClass();
         singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, Visibility.PUBLIC) {
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 Arity.checkArgumentCount(context.getRuntime(), args, 0, 0);
                 return call(context, self, clazz, name);
             }
 
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 return value;
             }
 
             @Override
             public Arity getArity() {
                 return Arity.noArguments();
             }
         });
     }
 
     public static IRubyObject get_top_level_proxy_or_package(ThreadContext context, IRubyObject recv, IRubyObject sym) {
         Ruby runtime = context.getRuntime();
         RubyModule result = getTopLevelProxyOrPackage(context, runtime, sym.asJavaString());
 
         return result != null ? result : runtime.getNil();
     }
 
     public static IRubyObject wrap(Ruby runtime, IRubyObject java_object) {
         return getInstance(runtime, ((JavaObject) java_object).getValue());
     }
 
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version 
      */
     @Deprecated
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject java_to_ruby(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         try {
             return JavaUtil.java_to_ruby(recv.getRuntime(), object);
         } catch (RuntimeException e) {
             recv.getRuntime().getJavaSupport().handleNativeException(e, null);
             // This point is only reached if there was an exception handler installed.
             return recv.getRuntime().getNil();
         }
     }
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     /**
      * High-level object conversion utility. 
      */
     @Deprecated
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.ruby_to_java(recv, object, unusedBlock);
     }
 
     @Deprecated
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject java_to_primitive(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.java_to_primitive(recv, object, unusedBlock);
     }
 
     @JRubyMethod(required = 2, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_proxy_instance2(IRubyObject recv, final IRubyObject wrapper, IRubyObject ifcs, Block block) {
         IRubyObject[] javaClasses = ((RubyArray)ifcs).toJavaArray();
         final Ruby runtime = recv.getRuntime();
 
         // Create list of interface names to proxy (and make sure they really are interfaces)
         // Also build a hashcode from all classes to use for retrieving previously-created impl
         Class[] interfaces = new Class[javaClasses.length];
         for (int i = 0; i < javaClasses.length; i++) {
             if (!(javaClasses[i] instanceof JavaClass) || !((JavaClass) javaClasses[i]).interface_p().isTrue()) {
                 throw recv.getRuntime().newArgumentError("Java interface expected. got: " + javaClasses[i]);
             }
             interfaces[i] = ((JavaClass) javaClasses[i]).javaClass();
         }
 
         return newInterfaceImpl(wrapper, interfaces);
     }
 
     public static IRubyObject newInterfaceImpl(final IRubyObject wrapper, Class[] interfaces) {
         final Ruby runtime = wrapper.getRuntime();
 
         Class[] tmp_interfaces = interfaces;
         interfaces = new Class[tmp_interfaces.length + 1];
         System.arraycopy(tmp_interfaces, 0, interfaces, 0, tmp_interfaces.length);
         interfaces[tmp_interfaces.length] = RubyObjectHolderProxy.class;
 
         // hashcode is a combination of the interfaces and the Ruby class we're using
         // to implement them
         if (!RubyInstanceConfig.INTERFACES_USE_PROXY) {
             int interfacesHashCode = interfacesHashCode(interfaces);
             // if it's a singleton class and the real class is proc, we're doing closure conversion
             // so just use Proc's hashcode
             if (wrapper.getMetaClass().isSingleton() && wrapper.getMetaClass().getRealClass() == runtime.getProc()) {
                 interfacesHashCode = 31 * interfacesHashCode + runtime.getProc().hashCode();
             } else {
                 // normal new class implementing interfaces
                 interfacesHashCode = 31 * interfacesHashCode + wrapper.getMetaClass().hashCode();
             }
             String implClassName = "org.jruby.gen.InterfaceImpl" + Math.abs(interfacesHashCode);
             Class proxyImplClass;
             try {
                 proxyImplClass = Class.forName(implClassName, true, runtime.getJRubyClassLoader());
             } catch (ClassNotFoundException cnfe) {
                 proxyImplClass = RealClassGenerator.createOldStyleImplClass(interfaces, wrapper.getMetaClass(), runtime, implClassName);
             }
 
             try {
                 Constructor proxyConstructor = proxyImplClass.getConstructor(IRubyObject.class);
                 return JavaObject.wrap(runtime, proxyConstructor.newInstance(wrapper));
             } catch (NoSuchMethodException nsme) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + nsme);
             } catch (InvocationTargetException ite) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ite);
             } catch (InstantiationException ie) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ie);
             } catch (IllegalAccessException iae) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + iae);
             }
         } else {
             Object proxyObject = Proxy.newProxyInstance(runtime.getJRubyClassLoader(), interfaces, new InvocationHandler() {
                 private Map parameterTypeCache = new ConcurrentHashMap();
 
                 public Object invoke(Object proxy, Method method, Object[] nargs) throws Throwable {
                     String methodName = method.getName();
                     int length = nargs == null ? 0 : nargs.length;
 
                     // FIXME: wtf is this? Why would these use the class?
                     if (methodName == "toString" && length == 0) {
                         return proxy.getClass().getName();
                     } else if (methodName == "hashCode" && length == 0) {
                         return Integer.valueOf(proxy.getClass().hashCode());
                     } else if (methodName == "equals" && length == 1) {
                         Class[] parameterTypes = (Class[]) parameterTypeCache.get(method);
                         if (parameterTypes == null) {
                             parameterTypes = method.getParameterTypes();
                             parameterTypeCache.put(method, parameterTypes);
                         }
                         if (parameterTypes[0].equals(Object.class)) {
                             return Boolean.valueOf(proxy == nargs[0]);
                         }
                     } else if (methodName == "__ruby_object" && length == 0) {
                         return wrapper;
                     }
 
                     IRubyObject[] rubyArgs = JavaUtil.convertJavaArrayToRuby(runtime, nargs);
                     try {
                         return RuntimeHelpers.invoke(runtime.getCurrentContext(), wrapper, methodName, rubyArgs).toJava(method.getReturnType());
                     } catch (RuntimeException e) { e.printStackTrace(); throw e; }
                 }
             });
             return JavaObject.wrap(runtime, proxyObject);
         }
     }
 
     public static IRubyObject generateRealClass(final RubyClass clazz) {
         final Ruby runtime = clazz.getRuntime();
         final Class[] interfaces = getInterfacesFromRubyClass(clazz);
 
         // hashcode is a combination of the interfaces and the Ruby class we're using
         // to implement them
         int interfacesHashCode = interfacesHashCode(interfaces);
         // normal new class implementing interfaces
         interfacesHashCode = 31 * interfacesHashCode + clazz.hashCode();
         
         String implClassName;
         if (clazz.getBaseName() == null) {
             // no-name class, generate a bogus name for it
             implClassName = "anon_class" + System.identityHashCode(clazz) + "_" + Math.abs(interfacesHashCode);
         } else {
             implClassName = clazz.getName().replaceAll("::", "\\$\\$") + "_" + Math.abs(interfacesHashCode);
         }
         Class proxyImplClass;
         try {
             proxyImplClass = Class.forName(implClassName, true, runtime.getJRubyClassLoader());
         } catch (ClassNotFoundException cnfe) {
             // try to use super's reified class; otherwise, RubyObject (for now)
             Class superClass = clazz.getSuperClass().getRealClass().getReifiedClass();
             if (superClass == null) {
                 superClass = RubyObject.class;
             }
             proxyImplClass = RealClassGenerator.createRealImplClass(superClass, interfaces, clazz, runtime, implClassName);
             clazz.setReifiedClass(proxyImplClass);
             
             // add a default initialize if one does not already exist and this is a Java-hierarchy class
             if (NEW_STYLE_EXTENSION &&
                     !(RubyBasicObject.class.isAssignableFrom(proxyImplClass) || clazz.getMethods().containsKey("initialize"))
                     ) {
                 clazz.addMethod("initialize", new JavaMethodZero(clazz, Visibility.PUBLIC) {
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                         return context.getRuntime().getNil();
                     }
                 });
             }
         }
 
         try {
             Constructor proxyConstructor = proxyImplClass.getConstructor(Ruby.class, RubyClass.class);
             return (IRubyObject)proxyConstructor.newInstance(runtime, clazz);
         } catch (NoSuchMethodException nsme) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + nsme);
         } catch (InvocationTargetException ite) {
             ite.printStackTrace();
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ite);
         } catch (InstantiationException ie) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ie);
         } catch (IllegalAccessException iae) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + iae);
         }
     }
     
     public static IRubyObject allocateProxy(Object javaObject, RubyClass clazz) {
         IRubyObject proxy = clazz.allocate();
         if (proxy instanceof JavaProxy) {
             ((JavaProxy)proxy).setObject(javaObject);
         } else {
             JavaObject wrappedObject = JavaObject.wrap(clazz.getRuntime(), javaObject);
             proxy.dataWrapStruct(wrappedObject);
         }
 
         return proxy;
     }
 
     public static IRubyObject wrapJavaObject(Ruby runtime, Object object) {
         return allocateProxy(object, getProxyClassForObject(runtime, object));
     }
 
     public static Class[] getInterfacesFromRubyClass(RubyClass klass) {
         Set<Class> interfaces = new HashSet<Class>();
         // walk all superclasses aggregating interfaces
         while (klass != null) {
             IRubyObject maybeInterfaces = klass.getInstanceVariables().getInstanceVariable("@java_interfaces");
             if (maybeInterfaces instanceof RubyArray) {
                 RubyArray moreInterfaces = (RubyArray)maybeInterfaces;
                 if (!moreInterfaces.isFrozen()) moreInterfaces.setFrozen(true);
 
                 interfaces.addAll(moreInterfaces);
             }
             klass = klass.getSuperClass();
         }
 
         return interfaces.toArray(new Class[interfaces.size()]);
     }
     
     private static int interfacesHashCode(Class[] a) {
         if (a == null)
             return 0;
 
         int result = 1;
 
         for (Class element : a)
             result = 31 * result + (element == null ? 0 : element.hashCode());
 
         return result;
     }
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index ea39f2b656..418d9e32c4 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -275,1229 +275,1230 @@ public final class ThreadContext {
         if (index == catchStack.length) {
             expandCatchIfNecessary();
         }
         catchStack[index] = catchTarget;
     }
     
     public void popCatch() {
         catchIndex--;
     }
 
     /**
      * Find the active Continuation for the given tag. Must be called with an
      * interned string.
      *
      * @param tag The interned string to search for
      * @return The continuation associated with this tag
      */
     public Continuation getActiveCatch(String tag) {
         for (int i = catchIndex; i >= 0; i--) {
             Continuation c = catchStack[i];
             if (c.tag == tag) return c;
         }
         return null;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         Frame currentFrame = stack[index - 1];
         stack[index].updateFrame(currentFrame);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private Frame pushFrame(Frame frame) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index] = frame;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
         return frame;
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(clazz, self, name, block, file, line, callNumber);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushEvalFrame(IRubyObject self) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrameForEval(self, file, line, callNumber);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushBacktraceFrame(String name) {
         pushFrame(name);        
     }
     
     private void pushFrame(String name) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(name, file, line);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushFrame() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(file, line);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void popFrame() {
         Frame frame = frameStack[frameIndex--];
         setFileAndLine(frame);
         
         frame.clear();
     }
         
     private void popFrameReal(Frame oldFrame) {
         int index = frameIndex;
         Frame frame = frameStack[index];
         frameStack[index] = oldFrame;
         frameIndex = index - 1;
         setFileAndLine(frame);
     }
     
     public Frame getCurrentFrame() {
         return frameStack[frameIndex];
     }
 
     public int getRubyFrameDelta() {
         return this.rubyFrameDelta;
     }
     
     public void setRubyFrameDelta(int newDelta) {
         this.rubyFrameDelta = newDelta;
     }
 
     public Frame getCurrentRubyFrame() {
         return frameStack[frameIndex-rubyFrameDelta];
     }
     
     public Frame getNextFrame() {
         int index = frameIndex;
         Frame[] stack = frameStack;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
         return stack[index + 1];
     }
     
     public Frame getPreviousFrame() {
         int index = frameIndex;
         return index < 1 ? null : frameStack[index - 1];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
 
     public Frame[] getFrames(int delta) {
         int top = frameIndex + delta;
         Frame[] frames = new Frame[top + 1];
         for (int i = 0; i <= top; i++) {
             frames[i] = frameStack[i].duplicateForBacktrace();
         }
         return frames;
     }
 
     /**
      * Search the frame stack for the given JumpTarget. Return true if it is
      * found and false otherwise. Skip the given number of frames before
      * beginning the search.
      * 
      * @param target The JumpTarget to search for
      * @param skipFrames The number of frames to skip before searching
      * @return
      */
     public boolean isJumpTargetAlive(int target, int skipFrames) {
         for (int i = frameIndex - skipFrames; i >= 0; i--) {
             if (frameStack[i].getJumpTarget() == target) return true;
         }
         return false;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public int getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlock();
     }
     
     public String getFile() {
         return file;
     }
     
     public int getLine() {
         return line;
     }
     
     public void setFile(String file) {
         this.file = file;
     }
     
     public void setLine(int line) {
         this.line = line;
     }
     
     public void setFileAndLine(String file, int line) {
         this.file = file;
         this.line = line;
     }
     
     public void setFileAndLine(Frame frame) {
         this.file = frame.getFile();
         this.line = frame.getLine();
     }
 
     public void setFileAndLine(ISourcePosition position) {
         this.file = position.getFile();
         this.line = position.getStartLine();
     }
     
     public Visibility getCurrentVisibility() {
         return getCurrentFrame().getVisibility();
     }
     
     public Visibility getPreviousVisibility() {
         return getPreviousFrame().getVisibility();
     }
     
     public void setCurrentVisibility(Visibility visibility) {
         getCurrentFrame().setVisibility(visibility);
     }
     
     public void pollThreadEvents() {
         thread.pollThreadEvents(this);
     }
     
     public int callNumber = 0;
 
     public int getCurrentTarget() {
         return callNumber;
     }
     
     public void callThreadPoll() {
         if ((callNumber++ & CALL_POLL_COUNT) == 0) pollThreadEvents();
     }
 
     public static void callThreadPoll(ThreadContext context) {
         if ((context.callNumber++ & CALL_POLL_COUNT) == 0) context.pollThreadEvents();
     }
     
     public void trace(RubyEvent event, String name, RubyModule implClass) {
         trace(event, name, implClass, file, line);
     }
 
     public void trace(RubyEvent event, String name, RubyModule implClass, String file, int line) {
         runtime.callEventHooks(this, event, file, line, name, implClass);
     }
     
     public void pushRubyClass(RubyModule currentModule) {
         // FIXME: this seems like a good assertion, but it breaks compiled code and the code seems
         // to run without it...
         //assert currentModule != null : "Can't push null RubyClass";
         
         int index = ++parentIndex;
         RubyModule[] stack = parentStack;
         stack[index] = currentModule;
         if (index + 1 == stack.length) {
             expandParentsIfNecessary();
         }
     }
     
     public RubyModule popRubyClass() {
         int index = parentIndex;
         RubyModule[] stack = parentStack;
         RubyModule ret = stack[index];
         stack[index] = null;
         parentIndex = index - 1;
         return ret;
     }
     
     public RubyModule getRubyClass() {
         assert parentIndex != -1 : "Trying to get RubyClass from empty stack";
         RubyModule parentModule = parentStack[parentIndex];
         return parentModule.getNonIncludedClass();
     }
 
     public RubyModule getPreviousRubyClass() {
         assert parentIndex != 0 : "Trying to get RubyClass from too-shallow stack";
         RubyModule parentModule = parentStack[parentIndex - 1];
         return parentModule.getNonIncludedClass();
     }
     
     public boolean getConstantDefined(String internedName) {
         IRubyObject value = getConstant(internedName);
 
         return value != null;
     }
     
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     public IRubyObject getConstant(String internedName) {
         return getCurrentScope().getStaticScope().getConstant(runtime, internedName, runtime.getObject());
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a null const decl
      */
     public IRubyObject setConstantInCurrent(String internedName, IRubyObject result) {
         RubyModule module;
 
         if ((module = getCurrentScope().getStaticScope().getModule()) != null) {
             module.fastSetConstant(internedName, result);
             return result;
         }
 
         // TODO: wire into new exception handling mechanism
         throw runtime.newTypeError("no class/module to define constant");
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name.
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInModule(String internedName, IRubyObject target, IRubyObject result) {
         if (!(target instanceof RubyModule)) {
             throw runtime.newTypeError(target.toString() + " is not a class/module");
         }
         RubyModule module = (RubyModule)target;
         module.fastSetConstant(internedName, result);
         
         return result;
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInObject(String internedName, IRubyObject result) {
         runtime.getObject().fastSetConstant(internedName, result);
         
         return result;
     }
     
     @Deprecated
     private static void addBackTraceElement(RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame) {
         addBackTraceElement(backtrace.getRuntime(), backtrace, frame, previousFrame);
     }
     
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, Frame frame, Frame previousFrame) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getLine() == previousFrame.getLine() &&
                 frame.getName() != null && 
                 frame.getName().equals(previousFrame.getName()) &&
                 frame.getFile().equals(previousFrame.getFile())) {
             return;
         }
         
         RubyString traceLine;
         if (previousFrame.getName() != null) {
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `" + previousFrame.getName() + '\'');
         } else if (runtime.is1_9()) {
             // TODO: This probably isn't the best hack, but it works until we can have different
             // root frame setup for 1.9 easily.
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `<main>'");
         } else {
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1));
         }
         
         backtrace.append(traceLine);
     }
     
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getLineNumber() == previousFrame.getLineNumber() &&
                 frame.getMethodName() != null &&
                 frame.getMethodName().equals(previousFrame.getMethodName()) &&
                 frame.getFileName() != null &&
                 frame.getFileName().equals(previousFrame.getFileName())) {
             return;
         }
         
         RubyString traceLine;
         String fileName = frame.getFileName();
         if (fileName == null) fileName = "";
         if (previousFrame.getMethodName() == UNKNOWN_NAME) {
             traceLine = RubyString.newString(runtime, fileName + ':' + (frame.getLineNumber()));
         } else {
             traceLine = RubyString.newString(runtime, fileName + ':' + (frame.getLineNumber()) + ":in `" + previousFrame.getMethodName() + '\'');
         }
         
         backtrace.append(traceLine);
     }
     
     private static void addBackTraceElement(RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame, FrameType frameType) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getMethodName() != null && 
                 frame.getMethodName().equals(previousFrame.getMethodName()) &&
                 frame.getFileName().equals(previousFrame.getFileName()) &&
                 frame.getLineNumber() == previousFrame.getLineNumber()) {
             return;
         }
         
         StringBuilder buf = new StringBuilder(60);
         buf.append(frame.getFileName()).append(':').append(frame.getLineNumber());
         
         if (previousFrame.getMethodName() != null) {
             switch (frameType) {
             case METHOD:
                 buf.append(":in `");
                 buf.append(previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case BLOCK:
                 buf.append(":in `");
                 buf.append("block in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case EVAL:
                 buf.append(":in `");
                 buf.append("eval in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case CLASS:
                 buf.append(":in `");
                 buf.append("class in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case ROOT:
                 buf.append(":in `<toplevel>'");
                 break;
             }
         }
         
         backtrace.append(backtrace.getRuntime().newString(buf.toString()));
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, RubyStackTraceElement[] backtraceFrames) {
         return createBacktraceFromFrames(runtime, backtraceFrames, true);
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public IRubyObject createCallerBacktrace(Ruby runtime, int level) {
         int traceSize = frameIndex - level + 1;
         RubyArray backtrace = runtime.newArray(traceSize);
 
         for (int i = traceSize - 1; i > 0; i--) {
             addBackTraceElement(runtime, backtrace, frameStack[i], frameStack[i - 1]);
         }
         
         return backtrace;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, RubyStackTraceElement[] backtraceFrames, boolean cropAtEval) {
         RubyArray backtrace = runtime.newArray();
         
         if (backtraceFrames == null || backtraceFrames.length <= 0) return backtrace;
         
         int traceSize = backtraceFrames.length;
 
         for (int i = 0; i < traceSize - 1; i++) {
             RubyStackTraceElement frame = backtraceFrames[i];
             // We are in eval with binding break out early
             // FIXME: This is broken with the new backtrace stuff
             if (cropAtEval && frame.isBinding()) break;
 
             addBackTraceElement(runtime, backtrace, frame, backtraceFrames[i + 1]);
         }
         
         return backtrace;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public Frame[] createBacktrace(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         Frame[] traceFrames;
         
         if (traceSize <= 0) return null;
         
         if (nativeException) {
             // assert level == 0;
             traceFrames = new Frame[traceSize + 1];
             traceFrames[traceSize] = frameStack[frameIndex];
         } else {
             traceFrames = new Frame[traceSize];
         }
         
         System.arraycopy(frameStack, 0, traceFrames, 0, traceSize);
         
         return traceFrames;
     }
 
     public boolean isEventHooksEnabled() {
         return eventHooksEnabled;
     }
 
     public void setEventHooksEnabled(boolean flag) {
         eventHooksEnabled = flag;
     }
 
     public static class RubyStackTraceElement {
         private StackTraceElement element;
         private boolean binding;
 
         public RubyStackTraceElement(String cls, String method, String file, int line, boolean binding) {
             element = new StackTraceElement(cls, method, file, line);
             this.binding = binding;
         }
 
         public StackTraceElement getElement() {
             return element;
         }
 
         public boolean isBinding() {
             return binding;
         }
 
         public String getClassName() {
             return element.getClassName();
         }
 
         public String getFileName() {
             return element.getFileName();
         }
 
         public int getLineNumber() {
             return element.getLineNumber();
         }
 
         public String getMethodName() {
             return element.getMethodName();
         }
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public RubyStackTraceElement[] createBacktrace2(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         RubyStackTraceElement[] newTrace;
         
         if (traceSize <= 0) return null;
 
         int totalSize = traceSize;
         if (nativeException) {
             // assert level == 0;
             totalSize = traceSize + 1;
         }
         newTrace = new RubyStackTraceElement[totalSize];
 
         return buildTrace(newTrace);
     }
 
     private RubyStackTraceElement[] buildTrace(RubyStackTraceElement[] newTrace) {
         for (int i = 0; i < newTrace.length; i++) {
             Frame current = frameStack[i];
             String klazzName = getClassNameFromFrame(current);
             String methodName = getMethodNameFromFrame(current);
             newTrace[newTrace.length - 1 - i] = 
                     new RubyStackTraceElement(klazzName, methodName, current.getFile(), current.getLine() + 1, current.isBindingFrame());
         }
         
         return newTrace;
     }
 
     private String getClassNameFromFrame(Frame current) {
         String klazzName;
         if (current.getKlazz() == null) {
             klazzName = UNKNOWN_NAME;
         } else {
             klazzName = current.getKlazz().getName();
         }
         return klazzName;
     }
     
     private String getMethodNameFromFrame(Frame current) {
         String methodName = current.getName();
         if (current.getName() == null) {
             methodName = UNKNOWN_NAME;
         }
         return methodName;
     }
     
     private static String createRubyBacktraceString(StackTraceElement element) {
         return element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'";
     }
     
     public static String createRawBacktraceStringFromThrowable(Throwable t) {
         StackTraceElement[] javaStackTrace = t.getStackTrace();
         
         StringBuffer buffer = new StringBuffer();
         if (javaStackTrace != null && javaStackTrace.length > 0) {
             StackTraceElement element = javaStackTrace[0];
 
             buffer
                     .append(createRubyBacktraceString(element))
                     .append(": ")
                     .append(t.toString())
                     .append("\n");
             for (int i = 1; i < javaStackTrace.length; i++) {
                 element = javaStackTrace[i];
                 
                 buffer
                         .append("\tfrom ")
                         .append(createRubyBacktraceString(element));
                 if (i + 1 < javaStackTrace.length) buffer.append("\n");
             }
         }
         
         return buffer.toString();
     }
     
     public static IRubyObject createRawBacktrace(Ruby runtime, StackTraceElement[] stackTrace, boolean filter) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             
             if (filter) {
                 if (element.getClassName().startsWith("org.jruby") ||
                         element.getLineNumber() < 0) {
                     continue;
                 }
             }
             RubyString str = RubyString.newString(runtime, createRubyBacktraceString(element));
             traceArray.append(str);
         }
         
         return traceArray;
     }
     
     public static IRubyObject createRubyCompiledBacktrace(Ruby runtime, StackTraceElement[] stackTrace) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             int index = element.getMethodName().indexOf("$RUBY$");
             if (index < 0) continue;
             String unmangledMethod = element.getMethodName().substring(index + 6);
             RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + unmangledMethod + "'");
             traceArray.append(str);
         }
         
         return traceArray;
     }
 
     private Frame pushFrameForBlock(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
 
         // set the binding's frame's "previous" file and line to current, so
         // trace will show who called the block
         f.setFileAndLine(file, line);
         
         setFileAndLine(binding.getFile(), binding.getLine());
         f.setVisibility(binding.getVisibility());
         
         return lastFrame;
     }
 
     private Frame pushFrameForEval(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         setFileAndLine(binding.getFile(), binding.getLine());
         f.setVisibility(binding.getVisibility());
         return lastFrame;
     }
     
     public enum FrameType { METHOD, BLOCK, EVAL, CLASS, ROOT }
     public static final Map<String, FrameType> INTERPRETED_FRAMES = new HashMap<String, FrameType>();
     
     static {
         INTERPRETED_FRAMES.put(DefaultMethod.class.getName() + ".interpretedCall", FrameType.METHOD);
         
         INTERPRETED_FRAMES.put(InterpretedBlock.class.getName() + ".evalBlockBody", FrameType.BLOCK);
         
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalWithBinding", FrameType.EVAL);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalSimple", FrameType.EVAL);
         
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalClassDefinitionBody", FrameType.CLASS);
         
         INTERPRETED_FRAMES.put(Ruby.class.getName() + ".runInterpreter", FrameType.ROOT);
     }
     
     public static IRubyObject createRubyHybridBacktrace(Ruby runtime, RubyStackTraceElement[] backtraceFrames, RubyStackTraceElement[] stackTrace, boolean debug) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         ThreadContext context = runtime.getCurrentContext();
         
         int rubyFrameIndex = backtraceFrames.length - 1;
         for (int i = 0; i < stackTrace.length; i++) {
             RubyStackTraceElement element = stackTrace[i];
             
             // look for mangling markers for compiled Ruby in method name
             int index = element.getMethodName().indexOf("$RUBY$");
             if (index >= 0) {
                 String unmangledMethod = element.getMethodName().substring(index + 6);
                 RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + unmangledMethod + "'");
                 traceArray.append(str);
                 
                 // if it's not a rescue or ensure, there's a frame associated, so decrement
                 if (!(element.getMethodName().contains("__rescue__") || element.getMethodName().contains("__ensure__"))) {
                     rubyFrameIndex--;
                 }
                 continue;
             }
             
             // look for __file__ method name for compiled roots
             if (element.getMethodName().equals("__file__")) {
                 RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ": `<toplevel>'");
                 traceArray.append(str);
                 rubyFrameIndex--;
                 continue;
             }
             
             // look for mangling markers for bound, unframed methods in class name
             index = element.getClassName().indexOf("$RUBYINVOKER$");
             if (index >= 0) {
                 // unframed invokers have no Ruby frames, so pull from class name
                 // but use current frame as file and line
                 String unmangledMethod = element.getClassName().substring(index + 13);
                 Frame current = context.frameStack[rubyFrameIndex];
                 RubyString str = RubyString.newString(runtime, current.getFile() + ":" + (current.getLine() + 1) + ":in `" + unmangledMethod + "'");
                 traceArray.append(str);
                 continue;
             }
             
             // look for mangling markers for bound, framed methods in class name
             index = element.getClassName().indexOf("$RUBYFRAMEDINVOKER$");
             if (index >= 0) {
                 // framed invokers will have Ruby frames associated with them
                 addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex - 1], FrameType.METHOD);
                 rubyFrameIndex--;
                 continue;
             }
             
             // try to mine out a Ruby frame using our list of interpreter entry-point markers
             String classMethod = element.getClassName() + "." + element.getMethodName();
             FrameType frameType = INTERPRETED_FRAMES.get(classMethod);
             if (frameType != null) {
                 // Frame matches one of our markers for "interpreted" calls
                 if (rubyFrameIndex == 0) {
                     addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex], frameType);
                 } else {
                     addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex - 1], frameType);
                     rubyFrameIndex--;
                 }
                 continue;
             } else {
                 // Frame is extraneous runtime information, skip it unless debug
                 if (debug) {
                     RubyString str = RubyString.newString(runtime, createRubyBacktraceString(element.getElement()));
                     traceArray.append(str);
                 }
                 continue;
             }
         }
         
         return traceArray;
     }
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
     
     public void preCompiledClass(RubyModule type, StaticScope staticScope) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         staticScope.setModule(type);
         pushScope(DynamicScope.newDynamicScope(staticScope));
     }
 
     public void preCompiledClassDummyScope(RubyModule type, StaticScope staticScope) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         staticScope.setModule(type);
         pushScope(staticScope.getDummyScope());
     }
 
     public void postCompiledClass() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preScopeNode(StaticScope staticScope) {
         pushScope(DynamicScope.newDynamicScope(staticScope, getCurrentScope()));
     }
 
     public void postScopeNode() {
         popScope();
     }
 
     public void preClassEval(StaticScope staticScope, RubyModule type) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
 
         pushScope(DynamicScope.newDynamicScope(staticScope, null));
     }
     
     public void postClassEval() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preBsfApply(String[] names) {
         // FIXME: I think we need these pushed somewhere?
         LocalStaticScope staticScope = new LocalStaticScope(null);
         staticScope.setVariables(names);
         pushFrame();
     }
     
     public void postBsfApply() {
         popFrame();
     }
     
     public void preMethodFrameAndScope(RubyModule clazz, String name, IRubyObject self, Block block, 
             StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block);
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void preMethodFrameAndDummyScope(RubyModule clazz, String name, IRubyObject self, Block block, 
             StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block);
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
 
     public void preMethodNoFrameAndDummyScope(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodFrameAndScope() {
         popRubyClass();
         popScope();
         popFrame();
     }
     
     public void preMethodFrameOnly(RubyModule clazz, String name, IRubyObject self, Block block) {
         pushRubyClass(clazz);
         pushCallFrame(clazz, name, self, block);
     }
     
     public void postMethodFrameOnly() {
         popFrame();
         popRubyClass();
     }
     
     public void preMethodScopeOnly(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postMethodScopeOnly() {
         popRubyClass();
         popScope();
     }
     
     public void preMethodBacktraceAndScope(String name, RubyModule clazz, StaticScope staticScope) {
         preMethodScopeOnly(clazz, staticScope);
         pushBacktraceFrame(name);
     }
     
     public void postMethodBacktraceAndScope() {
         postMethodScopeOnly();
         popFrame();
     }
     
     public void preMethodBacktraceOnly(String name) {
         pushBacktraceFrame(name);
     }
 
     public void preMethodBacktraceDummyScope(RubyModule clazz, String name, StaticScope staticScope) {
         pushBacktraceFrame(name);
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodBacktraceOnly() {
         popFrame();
     }
 
     public void postMethodBacktraceDummyScope() {
         popFrame();
         popRubyClass();
         popScope();
     }
 
     @Deprecated
     public void prepareTopLevel(RubyClass objectClass, IRubyObject topSelf) {
         preTopLevel(objectClass, topSelf);
     }
 
     /**
      * Prepare a toplevel execution context, including a frame, scope, and current RubyClass
      *
      * @param objectClass The current RubyClass
      * @param topSelf The "self" for the toplevel frame
      */
     public void preTopLevel(RubyClass objectClass, IRubyObject topSelf) {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
         
         pushRubyClass(objectClass);
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         StaticScope topStaticScope = new LocalStaticScope(null);
         pushScope(new ManyVarsDynamicScope(topStaticScope, null));
         
         getCurrentScope().getStaticScope().setModule(objectClass);
     }
 
     /**
      * Remove the elements added for the toplevel execution context.
      */
     public void postTopLevel() {
         popFrame();
         popRubyClass();
         popScope();
     }
     
     public void preNodeEval(RubyModule rubyClass, IRubyObject self, String name) {
         pushRubyClass(rubyClass);
         pushEvalFrame(self);
     }
 
     public void preNodeEval(RubyModule rubyClass, IRubyObject self) {
         pushRubyClass(rubyClass);
         pushEvalFrame(self);
     }
     
     public void postNodeEval() {
         popFrame();
         popRubyClass();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
     public void preExecuteUnder(RubyModule executeUnderClass, Block block) {
         Frame frame = getCurrentFrame();
+        Frame previous = getPreviousFrame();
         
         pushRubyClass(executeUnderClass);
         DynamicScope scope = getCurrentScope();
         StaticScope sScope = new BlockStaticScope(scope.getStaticScope());
         sScope.setModule(executeUnderClass);
         pushScope(DynamicScope.newDynamicScope(sScope, scope));
-        pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), block);
-        getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
+        pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), previous.getBlock());
+        getCurrentFrame().setVisibility(previous.getVisibility());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popScope();
         popRubyClass();
     }
     
     public void preMproc() {
         pushFrame();
     }
     
     public void postMproc() {
         popFrame();
     }
     
     public void preRunThread(Frame[] currentFrames) {
         for (Frame frame : currentFrames) {
             pushFrame(frame);
         }
         setFileAndLine(getCurrentFrame());
     }
     
     public void preTrace() {
         setWithinTrace(true);
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
         setWithinTrace(false);
     }
     
     public Frame preForBlock(Binding binding, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         pushScope(binding.getDynamicScope());
         return lastFrame;
     }
     
     public Frame preYieldSpecificBlock(Binding binding, StaticScope scope, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         // new scope for this invocation of the block, based on parent scope
         pushScope(DynamicScope.newDynamicScope(scope, binding.getDynamicScope()));
         return lastFrame;
     }
     
     public Frame preYieldLightBlock(Binding binding, DynamicScope emptyScope, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         // just push the same empty scope, since we won't use one
         pushScope(emptyScope);
         return lastFrame;
     }
     
     public Frame preYieldNoScope(Binding binding, RubyModule klass) {
         pushRubyClass((klass != null) ? klass : binding.getKlass());
         return pushFrameForBlock(binding);
     }
     
     public void preEvalScriptlet(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postEvalScriptlet() {
         popScope();
     }
     
     public Frame preEvalWithBinding(Binding binding) {
         binding.getFrame().setIsBindingFrame(true);
         Frame lastFrame = pushFrameForEval(binding);
         pushRubyClass(binding.getKlass());
         return lastFrame;
     }
     
     public void postEvalWithBinding(Binding binding, Frame lastFrame) {
         binding.getFrame().setIsBindingFrame(false);
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYield(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYieldLight(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYieldNoScope(Frame lastFrame) {
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void preScopedBody(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postScopedBody() {
         popScope();
     }
     
     /**
      * Is this thread actively tracing at this moment.
      *
      * @return true if so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public boolean isWithinTrace() {
         return isWithinTrace;
     }
     
     /**
      * Set whether we are actively tracing or not on this thread.
      *
      * @param isWithinTrace true is so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public void setWithinTrace(boolean isWithinTrace) {
         this.isWithinTrace = isWithinTrace;
     }
     
     /**
      * Is this thread actively in defined? at the moment.
      *
      * @return true if within defined?
      */
     public boolean isWithinDefined() {
         return isWithinDefined;
     }
     
     /**
      * Set whether we are actively within defined? or not.
      *
      * @param isWithinDefined true if so
      */
     public void setWithinDefined(boolean isWithinDefined) {
         this.isWithinDefined = isWithinDefined;
     }
 
     /**
      * Return a binding representing the current call's state
      * @return the current binding
      */
     public Binding currentBinding() {
         Frame frame = getCurrentFrame();
         return new Binding(frame, getRubyClass(), getCurrentScope(), file, line);
     }
 
     /**
      * Return a binding representing the current call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding currentBinding(IRubyObject self) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), getCurrentScope(), file, line);
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility and self.
      * @param self the self object to use
      * @param visibility the visibility to use
      * @return the current binding using the specified self and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, visibility, getRubyClass(), getCurrentScope(), file, line);
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified scope and self.
      * @param self the self object to use
      * @param visibility the scope to use
      * @return the current binding using the specified self and scope
      */
     public Binding currentBinding(IRubyObject self, DynamicScope scope) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), scope, file, line);
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility, scope, and self. For shared-scope binding
      * consumers like for loops.
      * 
      * @param self the self object to use
      * @param visibility the visibility to use
      * @param scope the scope to use
      * @return the current binding using the specified self, scope, and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility, DynamicScope scope) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, visibility, getRubyClass(), scope, file, line);
     }
 
     /**
      * Return a binding representing the previous call's state
      * @return the current binding
      */
     public Binding previousBinding() {
         Frame frame = getPreviousFrame();
         Frame current = getCurrentFrame();
         return new Binding(frame, getPreviousRubyClass(), getCurrentScope(), current.getFile(), current.getLine());
     }
 
     /**
      * Return a binding representing the previous call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding previousBinding(IRubyObject self) {
         Frame frame = getPreviousFrame();
         Frame current = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getPreviousRubyClass(), getCurrentScope(), current.getFile(), current.getLine());
     }
 }
