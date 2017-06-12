diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index 0aa3abf0e4..c25b398f4e 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -1,809 +1,862 @@
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
 
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.Proxy;
 import java.util.concurrent.ConcurrentHashMap;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.RubyTime;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.ClassProvider;
 
 public class Java {
     public static RubyModule createJavaModule(Ruby runtime) {
         RubyModule javaModule = runtime.defineModule("Java");
         CallbackFactory callbackFactory = runtime.callbackFactory(Java.class);
         javaModule.defineModuleFunction("define_exception_handler", callbackFactory.getOptSingletonMethod("define_exception_handler"));
         javaModule.defineModuleFunction("primitive_to_java", callbackFactory.getSingletonMethod("primitive_to_java", IRubyObject.class));
         javaModule.defineModuleFunction("java_to_primitive", callbackFactory.getSingletonMethod("java_to_primitive", IRubyObject.class));
         javaModule.defineModuleFunction("java_to_ruby", callbackFactory.getSingletonMethod("java_to_ruby", IRubyObject.class));
         javaModule.defineModuleFunction("ruby_to_java", callbackFactory.getSingletonMethod("ruby_to_java", IRubyObject.class));
         javaModule.defineModuleFunction("new_proxy_instance", callbackFactory.getOptSingletonMethod("new_proxy_instance"));
 
         JavaObject.createJavaObjectClass(runtime, javaModule);
         JavaArray.createJavaArrayClass(runtime, javaModule);
         JavaClass.createJavaClassClass(runtime, javaModule);
         JavaMethod.createJavaMethodClass(runtime, javaModule);
         JavaConstructor.createJavaConstructorClass(runtime, javaModule);
         JavaField.createJavaFieldClass(runtime, javaModule);
 
         // also create the JavaProxy* classes
         JavaProxyClass.createJavaProxyModule(runtime);
 
         RubyModule javaUtils = runtime.defineModule("JavaUtilities");
         javaUtils.defineFastModuleFunction("wrap", callbackFactory.getFastSingletonMethod("wrap",IRubyObject.class));
         javaUtils.defineFastModuleFunction("valid_constant_name?", callbackFactory.getFastSingletonMethod("valid_constant_name_p",IRubyObject.class));
         javaUtils.defineFastModuleFunction("primitive_match", callbackFactory.getFastSingletonMethod("primitive_match",IRubyObject.class,IRubyObject.class));
         javaUtils.defineFastModuleFunction("access", callbackFactory.getFastSingletonMethod("access",IRubyObject.class));
         javaUtils.defineFastModuleFunction("matching_method", callbackFactory.getFastSingletonMethod("matching_method", IRubyObject.class, IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_deprecated_interface_proxy", callbackFactory.getFastSingletonMethod("get_deprecated_interface_proxy", IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_interface_module", callbackFactory.getFastSingletonMethod("get_interface_module", IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_package_module", callbackFactory.getFastSingletonMethod("get_package_module", IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_package_module_dot_format", callbackFactory.getFastSingletonMethod("get_package_module_dot_format", IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_proxy_class", callbackFactory.getFastSingletonMethod("get_proxy_class", IRubyObject.class));
 
         // Note: deprecated
         javaUtils.defineFastModuleFunction("add_proxy_extender", callbackFactory.getFastSingletonMethod("add_proxy_extender", IRubyObject.class));
 
         runtime.getJavaSupport().setConcreteProxyCallback(
                 callbackFactory.getFastSingletonMethod("concrete_proxy_inherited", IRubyObject.class));
 
         JavaArrayUtilities.createJavaArrayUtilitiesModule(runtime);
         
         RubyClass javaProxy = runtime.defineClass("JavaProxy", runtime.getObject(), runtime.getObject().getAllocator());
         javaProxy.getMetaClass().defineFastMethod("new_instance_for", callbackFactory.getFastSingletonMethod("new_instance_for", IRubyObject.class));
         javaProxy.getMetaClass().defineFastMethod("to_java_object", callbackFactory.getFastSingletonMethod("to_java_object"));
 
         return javaModule;
     }
 
 
     private static final ClassProvider JAVA_PACKAGE_CLASS_PROVIDER = new ClassProvider() {
         public RubyClass defineClassUnder(final RubyModule pkg, final String name, final RubyClass superClazz) {
             // shouldn't happen, but if a superclass is specified, it's not ours
             if (superClazz != null) {
                 return null;
             }
             final IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) return null;
 
             final Ruby runtime = pkg.getRuntime();
             return (RubyClass)get_proxy_class(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forName(runtime, packageName.asJavaString() + name));
         }
         
         public RubyModule defineModuleUnder(final RubyModule pkg, final String name) {
             final IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) return null;
 
             final Ruby runtime = pkg.getRuntime();
             return (RubyModule)get_interface_module(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forName(runtime, packageName.asJavaString() + name));
         }
     };
         
-    // JavaProxy
+    /**
+     * Returns a new proxy instance of type (RubyClass)recv for the wrapped java_object,
+     * or the cached proxy if we've already seen this object.
+     * 
+     * @param recv the class for this object
+     * @param java_object the java object wrapped in a JavaObject wrapper
+     * @return the new or cached proxy for the specified Java object
+     */
     public static IRubyObject new_instance_for(IRubyObject recv, IRubyObject java_object) {
+        // FIXME: note temporary double-allocation of JavaObject as we move to cleaner interface
+        if (java_object instanceof JavaObject) {
+            return getInstance(((JavaObject)java_object).getValue(), (RubyClass)recv);
+        }
+        // in theory we should never get here, keeping around temporarily
         IRubyObject new_instance = ((RubyClass)recv).allocate();
         new_instance.getInstanceVariables().fastSetInstanceVariable("@java_object",java_object);
         return new_instance;
     }
+    
+    /**
+     * Returns a new proxy instance of type clazz for rawJavaObject, or the cached
+     * proxy if we've already seen this object.
+     * 
+     * @param rawJavaObject
+     * @param clazz
+     * @return the new or cached proxy for the specified Java object
+     */
+    public static IRubyObject getInstance(Object rawJavaObject, RubyClass clazz) {
+        return clazz.getRuntime().getJavaSupport().getObjectProxyCache()
+            .getOrCreate(rawJavaObject, clazz);
+    }
+    
+    /**
+     * Returns a new proxy instance of a type corresponding to rawJavaObject's class,
+     * or the cached proxy if we've already seen this object.  Note that primitives
+     * and strings are <em>not</em> coerced to corresponding Ruby types; use
+     * JavaUtil.convertJavaToUsableRubyObject to get coerced types or proxies as
+     * appropriate.
+     * 
+     * @param runtime
+     * @param rawJavaObject
+     * @return the new or cached proxy for the specified Java object
+     * @see JavaUtil.convertJavaToUsableRubyObject
+     */
+    public static IRubyObject getInstance(Ruby runtime, Object rawJavaObject) {
+        if (rawJavaObject != null) {
+            return runtime.getJavaSupport().getObjectProxyCache()
+                .getOrCreate(rawJavaObject,
+                        (RubyClass)getProxyClass(runtime,
+                            JavaClass.get(runtime, rawJavaObject.getClass())));
+        }
+        return runtime.getNil();
+    }
 
     // If the proxy class itself is passed as a parameter this will be called by Java#ruby_to_java    
     public static IRubyObject to_java_object(IRubyObject recv) {
         return recv.getInstanceVariables().fastGetInstanceVariable("@java_class");
     }
 
     // JavaUtilities
     
     /**
      * Add a new proxy extender. This is used by JavaUtilities to allow adding methods
      * to a given type's proxy and all types descending from that proxy's Java class.
      */
     @Deprecated
     public static IRubyObject add_proxy_extender(final IRubyObject recv, final IRubyObject extender) {
         // hacky workaround in case any users call this directly.
         // most will have called JavaUtilities.extend_proxy instead.
         recv.getRuntime().getWarnings().warn("JavaUtilities.add_proxy_extender is deprecated - use JavaUtilities.extend_proxy instead");
         final IRubyObject javaClassVar = extender.getInstanceVariables().fastGetInstanceVariable("@java_class");
         if (!(javaClassVar instanceof JavaClass)) {
             throw recv.getRuntime().newArgumentError("extender does not have a valid @java_class");
         }
         ((JavaClass)javaClassVar).addProxyExtender(extender);
         return recv.getRuntime().getNil();
     }
     
-    public static IRubyObject get_interface_module(final IRubyObject recv, final IRubyObject javaClassObject) {
-        final Ruby runtime = recv.getRuntime();
-        final JavaClass javaClass;
-        if (javaClassObject instanceof RubyString) {
-            javaClass = JavaClass.for_name(recv, javaClassObject);
-        } else if (javaClassObject instanceof JavaClass) {
-            javaClass = (JavaClass)javaClassObject;
-        } else  {
-            throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
-        }
+    public static RubyModule getInterfaceModule(Ruby runtime, JavaClass javaClass) {
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
                 interfaceModule = (RubyModule)runtime.getJavaSupport().getJavaInterfaceTemplate().dup();
                 interfaceModule.fastSetInstanceVariable("@java_class",javaClass);
                 addToJavaPackageModule(interfaceModule,javaClass);
                 javaClass.setupInterfaceModule(interfaceModule);
                 // include any interfaces we extend
-                final Class[] extended = javaClass.javaClass().getInterfaces();
+                Class[] extended = javaClass.javaClass().getInterfaces();
                 for (int i = extended.length; --i >= 0; ) {
                     JavaClass extendedClass = JavaClass.get(runtime, extended[i]);
-                    RubyModule extModule = (RubyModule)get_interface_module(recv, extendedClass);
+                    RubyModule extModule = getInterfaceModule(runtime, extendedClass);
                     interfaceModule.includeModule(extModule);
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return interfaceModule;
     }
+    
+    public static IRubyObject get_interface_module(IRubyObject recv, IRubyObject javaClassObject) {
+        Ruby runtime = recv.getRuntime();
+        JavaClass javaClass;
+        if (javaClassObject instanceof RubyString) {
+            javaClass = JavaClass.for_name(recv, javaClassObject);
+        } else if (javaClassObject instanceof JavaClass) {
+            javaClass = (JavaClass)javaClassObject;
+        } else  {
+            throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
+        }
+        return getInterfaceModule(runtime, javaClass);
+    }
 
     // Note: this isn't really all that deprecated, as it is used for
     // internal purposes, at least for now. But users should be discouraged
     // from calling this directly; eventually it will go away.
-    public static IRubyObject get_deprecated_interface_proxy(final IRubyObject recv, final IRubyObject javaClassObject) {
-        final Ruby runtime = recv.getRuntime();
-        final JavaClass javaClass;
+    public static IRubyObject get_deprecated_interface_proxy(IRubyObject recv, IRubyObject javaClassObject) {
+        Ruby runtime = recv.getRuntime();
+        JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, javaClassObject);
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass)javaClassObject;
         } else  {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         if (!javaClass.javaClass().isInterface()) {
             throw runtime.newArgumentError("expected Java interface class, got " + javaClassObject);
         }
         RubyClass proxyClass;
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         javaClass.lockProxy();
         try {
             if((proxyClass = javaClass.getProxyClass()) == null) {
-                final RubyModule interfaceModule = (RubyModule)get_interface_module(recv, javaClass);
+                RubyModule interfaceModule = getInterfaceModule(runtime, javaClass);
                 RubyClass interfaceJavaProxy = runtime.fastGetClass("InterfaceJavaProxy");
                 proxyClass = RubyClass.newClass(runtime, interfaceJavaProxy);
                 proxyClass.setAllocator(interfaceJavaProxy.getAllocator());
                 proxyClass.makeMetaClass(interfaceJavaProxy.getMetaClass());
                 // parent.setConstant(name, proxyClass); // where the name should come from ?
                 proxyClass.inherit(interfaceJavaProxy);                
                 proxyClass.callMethod(recv.getRuntime().getCurrentContext(), "java_class=", javaClass);
                 // including interface module so old-style interface "subclasses" will
                 // respond correctly to #kind_of?, etc.
                 proxyClass.includeModule(interfaceModule);
                 javaClass.setupProxy(proxyClass);
                 // add reference to interface module
                 if (proxyClass.fastGetConstantAt("Includable") == null) {
                     proxyClass.fastSetConstant("Includable", interfaceModule);
                 }
 
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
-
-    public static IRubyObject get_proxy_class(final IRubyObject recv, final IRubyObject java_class_object) {
-        final Ruby runtime = recv.getRuntime();
-        final JavaClass javaClass;
-        if (java_class_object instanceof RubyString) {
-            javaClass = JavaClass.for_name(recv, java_class_object);
-        } else if (java_class_object instanceof JavaClass) {
-            javaClass = (JavaClass)java_class_object;
-        } else  {
-            throw runtime.newArgumentError("expected JavaClass, got " + java_class_object);
-        }
+    
+    public static RubyModule getProxyClass(Ruby runtime, JavaClass javaClass) {
         RubyClass proxyClass;
-        final Class c;
+        Class c;
         if ((c = javaClass.javaClass()).isInterface()) {
-            return get_interface_module(recv,javaClass);
+            return getInterfaceModule(runtime, javaClass);
         }
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         javaClass.lockProxy();
         try {
             if((proxyClass = javaClass.getProxyClass()) == null) {
 
                 if(c.isArray()) {
-                    proxyClass = createProxyClass(recv,
+                    proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getArrayProxyClass(),
                             javaClass, true);
 
                 } else if (c.isPrimitive()) {
-                    proxyClass = createProxyClass(recv,
+                    proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
 
                 } else if (c == Object.class) {
                     // java.lang.Object is added at root of java proxy classes
-                    proxyClass = createProxyClass(recv,
+                    proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
                     proxyClass.getMetaClass().defineFastMethod("inherited",
                             runtime.getJavaSupport().getConcreteProxyCallback());
                     addToJavaPackageModule(proxyClass, javaClass);
 
                 } else {
                     // other java proxy classes added under their superclass' java proxy
-                    proxyClass = createProxyClass(recv,
-                            get_proxy_class(recv,runtime.newString(c.getSuperclass().getName())),
+                    proxyClass = createProxyClass(runtime,
+                            (RubyClass)getProxyClass(runtime, JavaClass.get(runtime, c.getSuperclass())),
                             javaClass, false);
 
                     // include interface modules into the proxy class
                     Class[] interfaces = c.getInterfaces();
                     for (int i = interfaces.length; --i >= 0; ) {
-                        JavaClass ifc = JavaClass.get(runtime,interfaces[i]);
-                        proxyClass.includeModule(get_interface_module(recv,ifc));
+                        JavaClass ifc = JavaClass.get(runtime, interfaces[i]);
+                        proxyClass.includeModule(getInterfaceModule(runtime, ifc));
                     }
                     if (Modifier.isPublic(c.getModifiers())) {
                         addToJavaPackageModule(proxyClass, javaClass);
                     }
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
 
-    private static RubyClass createProxyClass(final IRubyObject recv, final IRubyObject baseType,
-            final JavaClass javaClass, final boolean invokeInherited) {
+    public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject java_class_object) {
+        Ruby runtime = recv.getRuntime();
+        JavaClass javaClass;
+        if (java_class_object instanceof RubyString) {
+            javaClass = JavaClass.for_name(recv, java_class_object);
+        } else if (java_class_object instanceof JavaClass) {
+            javaClass = (JavaClass)java_class_object;
+        } else  {
+            throw runtime.newArgumentError("expected JavaClass, got " + java_class_object);
+        }
+        return getProxyClass(runtime, javaClass);
+    }
+
+    private static RubyClass createProxyClass(Ruby runtime, RubyClass baseType,
+            JavaClass javaClass, boolean invokeInherited) {
         // this needs to be split, since conditional calling #inherited doesn't fit standard ruby semantics
         RubyClass.checkInheritable(baseType);
         RubyClass superClass = (RubyClass)baseType;
-        RubyClass proxyClass = RubyClass.newClass(recv.getRuntime(), superClass);
+        RubyClass proxyClass = RubyClass.newClass(runtime, superClass);
         proxyClass.makeMetaClass(superClass.getMetaClass());
         proxyClass.setAllocator(superClass.getAllocator());
         if (invokeInherited) proxyClass.inherit(superClass);
 
-        proxyClass.callMethod(recv.getRuntime().getCurrentContext(), "java_class=", javaClass);
+        proxyClass.callMethod(runtime.getCurrentContext(), "java_class=", javaClass);
         javaClass.setupProxy(proxyClass);
         return proxyClass;
     }
 
     public static IRubyObject concrete_proxy_inherited(IRubyObject recv, IRubyObject subclass) {
         Ruby runtime = recv.getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         JavaSupport javaSupport = runtime.getJavaSupport();
         RubyClass javaProxyClass = javaSupport.getJavaProxyClass().getMetaClass();
         RuntimeHelpers.invokeAs(tc, javaProxyClass, recv, "inherited", new IRubyObject[]{subclass},
                 org.jruby.runtime.CallType.SUPER, Block.NULL_BLOCK);
         // TODO: move to Java
         return javaSupport.getJavaUtilitiesModule().callMethod(tc, "setup_java_subclass",
                 new IRubyObject[]{subclass, recv.callMethod(tc,"java_class")});
     }
     
     // package scheme 2: separate module for each full package name, constructed 
     // from the camel-cased package segments: Java::JavaLang::Object, 
     private static void addToJavaPackageModule(RubyModule proxyClass, JavaClass javaClass) {
         Class clazz = javaClass.javaClass();
         String fullName;
         if ((fullName = clazz.getName()) == null) return;
         int endPackage = fullName.lastIndexOf('.');
         // we'll only map conventional class names to modules 
         if (fullName.indexOf('$') != -1 || !Character.isUpperCase(fullName.charAt(endPackage + 1))) {
             return;
         }
         Ruby runtime = proxyClass.getRuntime();
         String packageString = endPackage < 0 ? "" : fullName.substring(0,endPackage);
         RubyModule packageModule = getJavaPackageModule(runtime, packageString);
         if (packageModule != null) {
             String className = fullName.substring(endPackage + 1);
             if (packageModule.getConstantAt(className) == null) {
                 packageModule.const_set(runtime.newSymbol(className),proxyClass);
             }
         }
     }
     
     private static RubyModule getJavaPackageModule(Ruby runtime, String packageString) {
         String packageName;
         int length = packageString.length();
         if (length == 0) {
             packageName = "Default";
         } else {
             StringBuffer buf = new StringBuffer();
             for (int start = 0, offset = 0; start < length; start = offset + 1) {
                 if ((offset = packageString.indexOf('.', start)) == -1) {
                     offset = length;
                 }
                 buf.append(Character.toUpperCase(packageString.charAt(start)))
                         .append(packageString.substring(start+1, offset));
             }
             packageName = buf.toString();
         }
 
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject packageModule = javaModule.getConstantAt(packageName);
         if (packageModule == null) {
             return createPackageModule(javaModule, packageName, packageString);
         } else if (packageModule instanceof RubyModule) {
             return (RubyModule)packageModule;
         } else {
             return null;
         }
     }
 
     private static RubyModule createPackageModule(final RubyModule parent, final String name, final String packageString) {
         Ruby runtime = parent.getRuntime();
         RubyModule packageModule = (RubyModule)runtime.getJavaSupport()
                 .getPackageModuleTemplate().dup();
         packageModule.fastSetInstanceVariable("@package_name",runtime.newString(
                 packageString.length() > 0 ? packageString + '.' : packageString));
 
         // this is where we'll get connected when classes are opened using
         // package module syntax.
         packageModule.addClassProvider(JAVA_PACKAGE_CLASS_PROVIDER);
 
         parent.const_set(runtime.newSymbol(name), packageModule);
         return packageModule;
     }
     
     private static final Pattern CAMEL_CASE_PACKAGE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");
 
     public static IRubyObject get_package_module(IRubyObject recv, IRubyObject symObject) {
         String sym = symObject.asJavaString().intern();
         RubyModule javaModule = recv.getRuntime().getJavaSupport().getJavaModule();
         IRubyObject value;
         if ((value = javaModule.fastGetConstantAt(sym)) != null) {
             return value;
         }
         String packageName;
         if ("Default".equals(sym)) {
             packageName = "";
         } else {
             Matcher m = CAMEL_CASE_PACKAGE_SPLITTER.matcher(sym);
             packageName = m.replaceAll("$1.$2").toLowerCase();
         }
         return createPackageModule(javaModule, sym, packageName);
     }
     
     public static IRubyObject get_package_module_dot_format(IRubyObject recv, IRubyObject dottedName) {
         Ruby runtime = recv.getRuntime();
         RubyModule module = getJavaPackageModule(runtime, dottedName.asJavaString());
         return module == null ? runtime.getNil() : module;
     }
     
     
     public static IRubyObject matching_method(IRubyObject recv, IRubyObject methods, IRubyObject args) {
         Map matchCache = recv.getRuntime().getJavaSupport().getMatchCache();
 
         List arg_types = new ArrayList();
         int alen = ((RubyArray)args).getLength();
         IRubyObject[] aargs = ((RubyArray)args).toJavaArrayMaybeUnsafe();
         for(int i=0;i<alen;i++) {
             if (aargs[i] instanceof JavaObject) {
                 arg_types.add(((JavaClass)((JavaObject)aargs[i]).java_class()).getValue());
             } else {
                 arg_types.add(aargs[i].getClass());
             }
         }
 
         Map ms = (Map)matchCache.get(methods);
         if(ms == null) {
             ms = new HashMap();
             matchCache.put(methods, ms);
         } else {
             IRubyObject method = (IRubyObject)ms.get(arg_types);
             if(method != null) {
                 return method;
             }
         }
 
         int mlen = ((RubyArray)methods).getLength();
         IRubyObject[] margs = ((RubyArray)methods).toJavaArrayMaybeUnsafe();
 
         for(int i=0;i<2;i++) {
             for(int k=0;k<mlen;k++) {
                 List types = null;
                 IRubyObject method = margs[k];
                 if(method instanceof JavaCallable) {
                     types = java.util.Arrays.asList(((JavaCallable)method).parameterTypes());
                 } else if(method instanceof JavaProxyMethod) {
                     types = java.util.Arrays.asList(((JavaProxyMethod)method).getParameterTypes());
                 } else if(method instanceof JavaProxyConstructor) {
                     types = java.util.Arrays.asList(((JavaProxyConstructor)method).getParameterTypes());
                 }
 
                 // Compatible (by inheritance)
                 if(arg_types.size() == types.size()) {
                     // Exact match
                     if(types.equals(arg_types)) {
                         ms.put(arg_types, method);
                         return method;
                     }
 
                     boolean match = true;
                     for(int j=0; j<types.size(); j++) {
                         if(!(JavaClass.assignable((Class)types.get(j),(Class)arg_types.get(j)) &&
                              (i > 0 || primitive_match(types.get(j),arg_types.get(j))))
                            && !JavaUtil.isDuckTypeConvertable((Class)arg_types.get(j), (Class)types.get(j))) {
                             match = false;
                             break;
                         }
                     }
                     if(match) {
                         ms.put(arg_types, method);
                         return method;
                     }
                 } // Could check for varargs here?
             }
         }
 
         Object o1 = margs[0];
 
         if(o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw recv.getRuntime().newNameError("no constructor with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(),"inspect"), null);
         } else {
             throw recv.getRuntime().newNameError("no " + ((JavaMethod)o1).name() + " with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(),"inspect"), null);
         }
     }
 
     public static IRubyObject matching_method_internal(IRubyObject recv, IRubyObject methods, IRubyObject[] args, int start, int len) {
         Map matchCache = recv.getRuntime().getJavaSupport().getMatchCache();
 
         List arg_types = new ArrayList();
         int aend = start+len;
 
         for(int i=start;i<aend;i++) {
             if (args[i] instanceof JavaObject) {
                 arg_types.add(((JavaClass)((JavaObject)args[i]).java_class()).getValue());
             } else {
                 arg_types.add(args[i].getClass());
             }
         }
 
         Map ms = (Map)matchCache.get(methods);
         if(ms == null) {
             ms = new HashMap();
             matchCache.put(methods, ms);
         } else {
             IRubyObject method = (IRubyObject)ms.get(arg_types);
             if(method != null) {
                 return method;
             }
         }
 
         int mlen = ((RubyArray)methods).getLength();
         IRubyObject[] margs = ((RubyArray)methods).toJavaArrayMaybeUnsafe();
 
         mfor: for(int k=0;k<mlen;k++) {
             Class[] types = null;
             IRubyObject method = margs[k];
             if(method instanceof JavaCallable) {
                 types = ((JavaCallable)method).parameterTypes();
             } else if(method instanceof JavaProxyMethod) {
                 types = ((JavaProxyMethod)method).getParameterTypes();
             } else if(method instanceof JavaProxyConstructor) {
                 types = ((JavaProxyConstructor)method).getParameterTypes();
             }
             // Compatible (by inheritance)
             if(len == types.length) {
                 // Exact match
                 boolean same = true;
                 for(int x=0,y=len;x<y;x++) {
                     if(!types[x].equals(arg_types.get(x))) {
                         same = false;
                         break;
                     }
                 }
                 if(same) {
                     ms.put(arg_types, method);
                     return method;
                 }
                 
                 for(int j=0,m=len; j<m; j++) {
                     if(!(
                          JavaClass.assignable(types[j],(Class)arg_types.get(j)) &&
                          primitive_match(types[j],arg_types.get(j))
                          )) {
                         continue mfor;
                     }
                 }
                 ms.put(arg_types, method);
                 return method;
             }
         }
 
         mfor: for(int k=0;k<mlen;k++) {
             Class[] types = null;
             IRubyObject method = margs[k];
             if(method instanceof JavaCallable) {
                 types = ((JavaCallable)method).parameterTypes();
             } else if(method instanceof JavaProxyMethod) {
                 types = ((JavaProxyMethod)method).getParameterTypes();
             } else if(method instanceof JavaProxyConstructor) {
                 types = ((JavaProxyConstructor)method).getParameterTypes();
             }
             // Compatible (by inheritance)
             if(len == types.length) {
                 for(int j=0,m=len; j<m; j++) {
                     if(!JavaClass.assignable(types[j],(Class)arg_types.get(j)) 
                         && !JavaUtil.isDuckTypeConvertable((Class)arg_types.get(j), types[j])) {
                         continue mfor;
                     }
                 }
                 ms.put(arg_types, method);
                 return method;
             }
         }
 
         Object o1 = margs[0];
 
         if(o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw recv.getRuntime().newNameError("no constructor with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(),"inspect"), null);
         } else {
             throw recv.getRuntime().newNameError("no " + ((JavaMethod)o1).name() + " with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(),"inspect"), null);
         }
     }
 
     public static IRubyObject access(IRubyObject recv, IRubyObject java_type) {
         int modifiers = ((JavaClass)java_type).javaClass().getModifiers();
         return recv.getRuntime().newString(Modifier.isPublic(modifiers) ? "public" : (Modifier.isProtected(modifiers) ? "protected" : "private"));
     }
 
     public static IRubyObject valid_constant_name_p(IRubyObject recv, IRubyObject name) {
         RubyString sname = name.convertToString();
         if(sname.getByteList().length() == 0) {
             return recv.getRuntime().getFalse();
         }
         return Character.isUpperCase(sname.getByteList().charAt(0)) ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
 
     public static boolean primitive_match(Object v1, Object v2) {
         if(((Class)v1).isPrimitive()) {
             if(v1 == Integer.TYPE || v1 == Long.TYPE || v1 == Short.TYPE || v1 == Character.TYPE) {
                 return v2 == Integer.class ||
                     v2 == Long.class ||
                     v2 == Short.class ||
                     v2 == Character.class;
             } else if(v1 == Float.TYPE || v1 == Double.TYPE) {
                 return v2 == Float.class ||
                     v2 == Double.class;
             } else if(v1 == Boolean.TYPE) {
                 return v2 == Boolean.class;
             }
             return false;
         }
         return true;
     }
 
     public static IRubyObject primitive_match(IRubyObject recv, IRubyObject t1, IRubyObject t2) {
         if(((JavaClass)t1).primitive_p().isTrue()) {
             Object v1 = ((JavaObject)t1).getValue();
             Object v2 = ((JavaObject)t2).getValue();
             return primitive_match(v1,v2) ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
         }
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject wrap(IRubyObject recv, IRubyObject java_object) {
-        return new_instance_for(get_proxy_class(recv, ((JavaObject)java_object).java_class()),java_object);
+        return getInstance(recv.getRuntime(), ((JavaObject)java_object).getValue());
     }
 
 	// Java methods
     public static IRubyObject define_exception_handler(IRubyObject recv, IRubyObject[] args, Block block) {
         String name = args[0].toString();
         RubyProc handler = null;
         if (args.length > 1) {
             handler = (RubyProc)args[1];
         } else {
             handler = recv.getRuntime().newProc(Block.Type.PROC, block);
         }
         recv.getRuntime().getJavaSupport().defineExceptionHandler(name, handler);
 
         return recv;
     }
 
     public static IRubyObject primitive_to_java(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
             return object;
         }
         Ruby runtime = recv.getRuntime();
         Object javaObject;
         switch (object.getMetaClass().index) {
         case ClassIndex.NIL:
             javaObject = null;
             break;
         case ClassIndex.FIXNUM:
             javaObject = new Long(((RubyFixnum) object).getLongValue());
             break;
         case ClassIndex.BIGNUM:
             javaObject = ((RubyBignum) object).getValue();
             break;
         case ClassIndex.FLOAT:
             javaObject = new Double(((RubyFloat) object).getValue());
             break;
         case ClassIndex.STRING:
             try {
                 ByteList bytes = ((RubyString) object).getByteList();
                 javaObject = new String(bytes.unsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
             } catch (UnsupportedEncodingException uee) {
                 javaObject = object.toString();
             }
             break;
         case ClassIndex.TRUE:
             javaObject = Boolean.TRUE;
             break;
         case ClassIndex.FALSE:
             javaObject = Boolean.FALSE;
             break;
         default:
             if (object instanceof RubyTime) {
                 javaObject = ((RubyTime)object).getJavaDate();
             } else {
                 javaObject = object;
             }
         }
         return JavaObject.wrap(runtime, javaObject);
     }
 
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version 
      */
     public static IRubyObject java_to_ruby(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if(object instanceof JavaObject) {
-        	object = JavaUtil.convertJavaToRuby(recv.getRuntime(), ((JavaObject) object).getValue());
-
-            //if (object.isKindOf(recv.getRuntime().fastGetModule("Java").fastGetClass("JavaObject"))) {
-            if(object instanceof JavaObject) {
-                return wrap(recv.getRuntime().getJavaSupport().getJavaUtilitiesModule(), object);
-            }
+            return JavaUtil.convertJavaToUsableRubyObject(recv.getRuntime(), ((JavaObject)object).getValue());
         }
-
 		return object;
     }
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     /**
      * High-level object conversion utility. 
      */
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
     	if(object.respondsTo("to_java_object")) {
             IRubyObject result = object.getInstanceVariables().fastGetInstanceVariable("@java_object");
             if(result == null) {
                 result = object.callMethod(recv.getRuntime().getCurrentContext(), "to_java_object");
             }
+            if (result instanceof JavaObject) {
+                recv.getRuntime().getJavaSupport().getObjectProxyCache()
+                    .put(((JavaObject)result).getValue(), object);
+            }
             return result;
     	}
     	
     	return primitive_to_java(recv, object, unusedBlock);
     }    
 
     public static IRubyObject java_to_primitive(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
         	return JavaUtil.convertJavaToRuby(recv.getRuntime(), ((JavaObject) object).getValue());
         }
 
 		return object;
     }
 
     public static IRubyObject new_proxy_instance(final IRubyObject recv, IRubyObject[] args, Block block) {
     	int size = Arity.checkArgumentCount(recv.getRuntime(), args, 1, -1) - 1;
     	final RubyProc proc;
 
     	// Is there a supplied proc argument or do we assume a block was supplied
     	if (args[size] instanceof RubyProc) {
     		proc = (RubyProc) args[size];
     	} else {
     		proc = recv.getRuntime().newProc(Block.Type.PROC, block);
     		size++;
     	}
     	
     	// Create list of interfaces to proxy (and make sure they really are interfaces)
         Class[] interfaces = new Class[size];
         for (int i = 0; i < size; i++) {
             if (!(args[i] instanceof JavaClass) || !((JavaClass)args[i]).interface_p().isTrue()) {
                 throw recv.getRuntime().newArgumentError("Java interface expected. got: " + args[i]);
             }
             interfaces[i] = ((JavaClass) args[i]).javaClass();
         }
         
         return JavaObject.wrap(recv.getRuntime(), Proxy.newProxyInstance(recv.getRuntime().getJRubyClassLoader(), interfaces, new InvocationHandler() {
             private Map parameterTypeCache = new ConcurrentHashMap();
             public Object invoke(Object proxy, Method method, Object[] nargs) throws Throwable {
                 Class[] parameterTypes = (Class[])parameterTypeCache.get(method);
                 if (parameterTypes == null) {
                     parameterTypes = method.getParameterTypes();
                     parameterTypeCache.put(method, parameterTypes);
                 }
             	int methodArgsLength = parameterTypes.length;
             	String methodName = method.getName();
             	
                 if (methodName.equals("toString") && methodArgsLength == 0) {
                     return proxy.getClass().getName();
                 } else if (methodName.equals("hashCode") && methodArgsLength == 0) {
                     return new Integer(proxy.getClass().hashCode());
                 } else if (methodName.equals("equals") && methodArgsLength == 1 && parameterTypes[0].equals(Object.class)) {
                     return Boolean.valueOf(proxy == nargs[0]);
                 }
                 int length = nargs == null ? 0 : nargs.length;
                 IRubyObject[] rubyArgs = new IRubyObject[length + 2];
                 rubyArgs[0] = JavaObject.wrap(recv.getRuntime(), proxy);
                 rubyArgs[1] = new JavaMethod(recv.getRuntime(), method);
                 for (int i = 0; i < length; i++) {
                     rubyArgs[i + 2] = JavaObject.wrap(recv.getRuntime(), nargs[i]);
                 }
                 return JavaUtil.convertArgument(proc.call(rubyArgs), method.getReturnType());
             }
         }));
     }
 }
diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index 5284ca045b..5f81928b14 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,1371 +1,1371 @@
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
 
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.locks.ReentrantLock;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.IdUtil;
 import org.jruby.util.collections.IntHashMap;
 
 public class JavaClass extends JavaObject {
 
     private static class AssignedName {
         // to override an assigned name, the type must be less than
         // or equal to the assigned type. so a field name in a subclass
         // will override an alias in a superclass, but not a method.
         static final int RESERVED = 0;
         static final int METHOD = 1;
         static final int FIELD = 2;
         static final int PROTECTED_METHOD = 3;
         static final int WEAKLY_RESERVED = 4; // we'll be peeved, but not devastated, if you override
         static final int ALIAS = 5;
         // yes, protected fields are weaker than aliases. many conflicts
         // in the old AWT code, for example, where you really want 'size'
         // to mean the public method getSize, not the protected field 'size'.
         static final int PROTECTED_FIELD = 6;
         String name;
         int type;
         AssignedName () {}
         AssignedName(String name, int type) {
             this.name = name;
             this.type = type;
         }
     }
 
     // TODO: other reserved names?
     private static final Map RESERVED_NAMES = new HashMap();
     static {
         RESERVED_NAMES.put("__id__", new AssignedName("__id__", AssignedName.RESERVED));
         RESERVED_NAMES.put("__send__", new AssignedName("__send__", AssignedName.RESERVED));
         RESERVED_NAMES.put("class", new AssignedName("class", AssignedName.RESERVED));
         RESERVED_NAMES.put("initialize", new AssignedName("initialize", AssignedName.RESERVED));
         RESERVED_NAMES.put("object_id", new AssignedName("object_id", AssignedName.RESERVED));
         RESERVED_NAMES.put("private", new AssignedName("private", AssignedName.RESERVED));
         RESERVED_NAMES.put("protected", new AssignedName("protected", AssignedName.RESERVED));
         RESERVED_NAMES.put("public", new AssignedName("public", AssignedName.RESERVED));
 
         // weakly reserved names
         RESERVED_NAMES.put("id", new AssignedName("id", AssignedName.WEAKLY_RESERVED));
     }
     private static final Map STATIC_RESERVED_NAMES = new HashMap(RESERVED_NAMES);
     static {
         STATIC_RESERVED_NAMES.put("new", new AssignedName("new", AssignedName.RESERVED));
     }
     private static final Map INSTANCE_RESERVED_NAMES = new HashMap(RESERVED_NAMES);
 
     private static abstract class NamedCallback implements Callback {
         static final int STATIC_FIELD = 1;
         static final int STATIC_METHOD = 2;
         static final int INSTANCE_FIELD = 3;
         static final int INSTANCE_METHOD = 4;
         String name;
         int type;
         Visibility visibility = Visibility.PUBLIC;
         boolean isProtected;
         NamedCallback () {}
         NamedCallback (String name, int type) {
             this.name = name;
             this.type = type;
         }
         abstract void install(RubyClass proxy);
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
 
     private static abstract class FieldCallback extends NamedCallback {
         Field field;
         JavaField javaField;
         FieldCallback(){}
         FieldCallback(String name, int type, Field field) {
             super(name,type);
             this.field = field;
         }
     }
 
     private class StaticFieldGetter extends FieldCallback {
         StaticFieldGetter(){}
         StaticFieldGetter(String name, Field field) {
             super(name,STATIC_FIELD,field);
         }
         void install(RubyClass proxy) {
             proxy.getSingletonClass().defineFastMethod(this.name,this,this.visibility);
         }
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             if (javaField == null) {
                 javaField = new JavaField(getRuntime(),field);
             }
             return Java.java_to_ruby(self,javaField.static_value(),Block.NULL_BLOCK);
         }
         public Arity getArity() {
             return Arity.NO_ARGUMENTS;
         }
     }
 
     private class StaticFieldSetter extends FieldCallback {
         StaticFieldSetter(){}
         StaticFieldSetter(String name, Field field) {
             super(name,STATIC_FIELD,field);
         }
         void install(RubyClass proxy) {
             proxy.getSingletonClass().defineFastMethod(this.name,this,this.visibility);
         }
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             if (javaField == null) {
                 javaField = new JavaField(getRuntime(),field);
             }
             return Java.java_to_ruby(self,
                     javaField.set_static_value(Java.ruby_to_java(self,args[0],Block.NULL_BLOCK)),
                     Block.NULL_BLOCK);
         }
         public Arity getArity() {
             return Arity.ONE_ARGUMENT;
         }
     }
 
     private class InstanceFieldGetter extends FieldCallback {
         InstanceFieldGetter(){}
         InstanceFieldGetter(String name, Field field) {
             super(name,INSTANCE_FIELD,field);
         }
         void install(RubyClass proxy) {
             proxy.defineFastMethod(this.name,this,this.visibility);
         }
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             if (javaField == null) {
                 javaField = new JavaField(getRuntime(),field);
             }
             return Java.java_to_ruby(self,
                     javaField.value(self.getInstanceVariables().fastGetInstanceVariable("@java_object")),
                     Block.NULL_BLOCK);
         }
         public Arity getArity() {
             return Arity.NO_ARGUMENTS;
         }
     }
 
     private class InstanceFieldSetter extends FieldCallback {
         InstanceFieldSetter(){}
         InstanceFieldSetter(String name, Field field) {
             super(name,INSTANCE_FIELD,field);
         }
         void install(RubyClass proxy) {
             proxy.defineFastMethod(this.name,this,this.visibility);
         }
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             if (javaField == null) {
                 javaField = new JavaField(getRuntime(),field);
             }
             return Java.java_to_ruby(self,
                     javaField.set_value(self.getInstanceVariables().fastGetInstanceVariable("@java_object"),
                             Java.ruby_to_java(self,args[0],Block.NULL_BLOCK)),
                     Block.NULL_BLOCK);
         }
         public Arity getArity() {
             return Arity.ONE_ARGUMENT;
         }
     }
 
     private static abstract class MethodCallback extends NamedCallback {
         private boolean haveLocalMethod;
         private List methods;
         protected List aliases;
         protected JavaMethod javaMethod;
         protected IntHashMap javaMethods;
         protected IntHashMap matchingMethods;
         MethodCallback(){}
         MethodCallback(String name, int type) {
             super(name,type);
         }
 
         // called only by initializing thread; no synchronization required
         void addMethod(Method method, Class javaClass) {
             if (methods == null) {
                 methods = new ArrayList();
             }
             methods.add(method);
             haveLocalMethod |= javaClass == method.getDeclaringClass();
         }
 
         // called only by initializing thread; no synchronization required
         void addAlias(String alias) {
             if (aliases == null) {
                 aliases = new ArrayList();
             }
             if (!aliases.contains(alias))
                 aliases.add(alias);
         }
 
         // modified only by addMethod; no synchronization required
         boolean hasLocalMethod () {
             return haveLocalMethod;
         }
 
         // TODO: varargs?
         // TODO: rework Java.matching_methods_internal and
         // ProxyData.method_cache, since we really don't need to be passing
         // around RubyArray objects anymore.
         synchronized void createJavaMethods(Ruby runtime) {
             if (methods != null) {
                 if (methods.size() == 1) {
                     javaMethod = JavaMethod.create(runtime,(Method)methods.get(0));
                 } else {
                     javaMethods = new IntHashMap();
                     matchingMethods = new IntHashMap(); 
                     for (Iterator iter = methods.iterator(); iter.hasNext() ;) {
                         Method method = (Method)iter.next();
                         // TODO: deal with varargs
                         int arity = method.getParameterTypes().length;
                         RubyArray methodsForArity = (RubyArray)javaMethods.get(arity);
                         if (methodsForArity == null) {
                             methodsForArity = RubyArray.newArrayLight(runtime);
                             javaMethods.put(arity,methodsForArity);
                         }
                         methodsForArity.append(JavaMethod.create(runtime,method));
                     }
                 }
                 methods = null;
             }
         }
 
         void raiseNoMatchingMethodError(IRubyObject proxy, IRubyObject[] args, int start) {
             int len = args.length;
             List argTypes = new ArrayList(len - start);
             for (int i = start ; i < len; i++) {
                 argTypes.add(((JavaClass)((JavaObject)args[i]).java_class()).getValue());
             }
             throw proxy.getRuntime().newNameError("no " + this.name + " with arguments matching " + argTypes + " on object " + proxy.callMethod(proxy.getRuntime().getCurrentContext(),"inspect"), null);
         }
     }
 
     private class StaticMethodInvoker extends MethodCallback {
         StaticMethodInvoker(){}
         StaticMethodInvoker(String name) {
             super(name,STATIC_METHOD);
         }
 
         void install(RubyClass proxy) {
             if (hasLocalMethod()) {
                 RubyClass singleton = proxy.getSingletonClass();
                 singleton.defineFastMethod(this.name,this,this.visibility);
                 if (aliases != null && isPublic() ) {
                     for (Iterator iter = aliases.iterator(); iter.hasNext(); ) {
                         singleton.defineAlias((String)iter.next(), this.name);
                     }
                     aliases = null;
                 }
             }
         }
 
         // synchronized due to modification of matchingMethods
         synchronized public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             createJavaMethods(self.getRuntime());
             // TODO: ok to convert args in place, rather than new array?
             int len = args.length;
             IRubyObject[] convertedArgs = new IRubyObject[len];
             for (int i = len; --i >= 0; ) {
                 convertedArgs[i] = Java.ruby_to_java(self,args[i],Block.NULL_BLOCK);
             }
             if (javaMethods == null) {
                 return Java.java_to_ruby(self,javaMethod.invoke_static(convertedArgs),Block.NULL_BLOCK); 
             } else {
                 int argsTypeHash = 0;
                 for (int i = len; --i >= 0; ) {
                     argsTypeHash += 3*args[i].getMetaClass().id;
                 }
                 IRubyObject match = (IRubyObject)matchingMethods.get(argsTypeHash);
                 if (match == null) {
                     // TODO: varargs?
                     RubyArray methods = (RubyArray)javaMethods.get(len);
                     if (methods == null) {
                         raiseNoMatchingMethodError(self,convertedArgs,0);
                     }
                     match = Java.matching_method_internal(JAVA_UTILITIES, methods, convertedArgs, 0, len);
                 }
                 return Java.java_to_ruby(self, ((JavaMethod)match).invoke_static(convertedArgs), Block.NULL_BLOCK);
             }
         }
         public Arity getArity() {
             return Arity.OPTIONAL;
         }
     }
 
     private class InstanceMethodInvoker extends MethodCallback {
         InstanceMethodInvoker(){}
         InstanceMethodInvoker(String name) {
             super(name,INSTANCE_METHOD);
         }
         void install(RubyClass proxy) {
             if (hasLocalMethod()) {
                 proxy.defineFastMethod(this.name,this,this.visibility);
                 if (aliases != null && isPublic()) {
                     for (Iterator iter = aliases.iterator(); iter.hasNext(); ) {
                         proxy.defineAlias((String)iter.next(), this.name);
                     }
                     aliases = null;
                 }
             }
         }
 
         // synchronized due to modification of matchingMethods
         synchronized public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             createJavaMethods(self.getRuntime());
             // TODO: ok to convert args in place, rather than new array?
             int len = args.length;
             if (block.isGiven()) { // convert block to argument
                 len += 1;
                 IRubyObject[] newArgs = new IRubyObject[args.length+1];
                 System.arraycopy(args, 0, newArgs, 0, args.length);
                 newArgs[args.length] = RubyProc.newProc(self.getRuntime(), block, Block.Type.LAMBDA);
                 args = newArgs;
             }
             IRubyObject[] convertedArgs = new IRubyObject[len+1];
             convertedArgs[0] = self.getInstanceVariables().fastGetInstanceVariable("@java_object");
             int i = len;
             if (block.isGiven()) {
                 convertedArgs[len] = args[len - 1];
                 i -= 1;
             }
             for (; --i >= 0; ) {
                 convertedArgs[i+1] = Java.ruby_to_java(self,args[i],Block.NULL_BLOCK);
             }
 
             if (javaMethods == null) {
                 return Java.java_to_ruby(self,javaMethod.invoke(convertedArgs),Block.NULL_BLOCK);
             } else {
                 int argsTypeHash = 0;
                 for (i = len; --i >= 0; ) {
                     argsTypeHash += 3*args[i].getMetaClass().id;
                 }
                 IRubyObject match = (IRubyObject)matchingMethods.get(argsTypeHash);
                 if (match == null) {
                     // TODO: varargs?
                     RubyArray methods = (RubyArray)javaMethods.get(len);
                     if (methods == null) {
                         raiseNoMatchingMethodError(self,convertedArgs,1);
                     }
                     match = Java.matching_method_internal(JAVA_UTILITIES, methods, convertedArgs, 1, len);
                     matchingMethods.put(argsTypeHash, match);
                 }
                 return Java.java_to_ruby(self,((JavaMethod)match).invoke(convertedArgs),Block.NULL_BLOCK);
             }
         }
         public Arity getArity() {
             return Arity.OPTIONAL;
         }
     }
 
     private static class ConstantField {
         static final int CONSTANT = Modifier.FINAL | Modifier.PUBLIC | Modifier.STATIC;
         final Field field;
         ConstantField(Field field) {
             this.field = field;
         }
         void install(final RubyModule proxy) {
             if (proxy.fastGetConstantAt(field.getName()) == null) {
                 JavaField javaField = new JavaField(proxy.getRuntime(),field);
                 // TODO: catch exception if constant is already set by other
                 // thread
                 proxy.const_set(javaField.name(),Java.java_to_ruby(proxy,javaField.static_value(),Block.NULL_BLOCK));
             }
         }
         static boolean isConstant(final Field field) {
             return (field.getModifiers() & CONSTANT) == CONSTANT &&
                 Character.isUpperCase(field.getName().charAt(0));
         }
     }
     
     private final RubyModule JAVA_UTILITIES = getRuntime().getJavaSupport().getJavaUtilitiesModule();
     
     private Map staticAssignedNames;
     private Map instanceAssignedNames;
     private Map staticCallbacks;
     private Map instanceCallbacks;
     private List constantFields;
     // caching constructors, as they're accessed for each new instance
     private RubyArray constructors;
     
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
 
     protected Map getStaticAssignedNames() {
         return staticAssignedNames;
     }
     protected Map getInstanceAssignedNames() {
         return instanceAssignedNames;
     }
     
     private JavaClass(Ruby runtime, Class javaClass) {
         super(runtime, (RubyClass) runtime.getJavaSupport().getJavaClassClass(), javaClass);
         if (javaClass.isInterface()) {
             initializeInterface(javaClass);
         } else if (!(javaClass.isArray() || javaClass.isPrimitive())) {
             // TODO: public only?
             initializeClass(javaClass);
         }
     }
     
     private void initializeInterface(Class javaClass) {
         Map staticNames  = new HashMap(STATIC_RESERVED_NAMES);
         List constantFields = new ArrayList(); 
         Field[] fields;
         try {
             fields = javaClass.getDeclaredFields();
         } catch (SecurityException e) {
             fields = javaClass.getFields();
         }
         for (int i = fields.length; --i >= 0; ) {
             Field field = fields[i];
             if (javaClass != field.getDeclaringClass()) continue;
             if (ConstantField.isConstant(field)) {
                 constantFields.add(new ConstantField(field));
             }
         }
         this.staticAssignedNames = staticNames;
         this.constantFields = constantFields;
     }
 
     private void initializeClass(Class javaClass) {
         Class superclass = javaClass.getSuperclass();
         Map staticNames;
         Map instanceNames;
         if (superclass == null) {
             staticNames = new HashMap();
             instanceNames = new HashMap();
         } else {
             JavaClass superJavaClass = get(getRuntime(),superclass);
             staticNames = new HashMap(superJavaClass.getStaticAssignedNames());
             instanceNames = new HashMap(superJavaClass.getInstanceAssignedNames());
         }
         staticNames.putAll(STATIC_RESERVED_NAMES);
         instanceNames.putAll(INSTANCE_RESERVED_NAMES);
         Map staticCallbacks = new HashMap();
         Map instanceCallbacks = new HashMap();
         List constantFields = new ArrayList(); 
         Field[] fields = javaClass.getFields();
         for (int i = fields.length; --i >= 0; ) {
             Field field = fields[i];
             if (javaClass != field.getDeclaringClass()) continue;
 
             if (ConstantField.isConstant(field)) {
                 constantFields.add(new ConstantField(field));
                 continue;
             }
             String name = field.getName();
             int modifiers = field.getModifiers();
             if (Modifier.isStatic(modifiers)) {
                 AssignedName assignedName = (AssignedName)staticNames.get(name);
                 if (assignedName != null && assignedName.type < AssignedName.FIELD)
                     continue;
                 staticNames.put(name,new AssignedName(name,AssignedName.FIELD));
                 staticCallbacks.put(name,new StaticFieldGetter(name,field));
                 if (!Modifier.isFinal(modifiers)) {
                     String setName = name + '=';
                     staticCallbacks.put(setName,new StaticFieldSetter(setName,field));
                 }
             } else {
                 AssignedName assignedName = (AssignedName)instanceNames.get(name);
                 if (assignedName != null && assignedName.type < AssignedName.FIELD)
                     continue;
                 instanceNames.put(name,new AssignedName(name,AssignedName.FIELD));
                 instanceCallbacks.put(name,new InstanceFieldGetter(name,field));
                 if (!Modifier.isFinal(modifiers)) {
                     String setName = name + '=';
                     instanceCallbacks.put(setName,new InstanceFieldSetter(setName,field));
                 }
             }
         }
         // TODO: protected methods.  this is going to require a rework 
         // of some of the mechanism.  
         Method[] methods = javaClass.getMethods();
         for (int i = methods.length; --i >= 0; ) {
             // we need to collect all methods, though we'll only
             // install the ones that are named in this class
             Method method = methods[i];
             String name = method.getName();
             if (Modifier.isStatic(method.getModifiers())) {
                 AssignedName assignedName = (AssignedName)staticNames.get(name);
                 if (assignedName == null) {
                     staticNames.put(name,new AssignedName(name,AssignedName.METHOD));
                 } else {
                     if (assignedName.type < AssignedName.METHOD)
                         continue;
                     if (assignedName.type != AssignedName.METHOD) {
                         staticCallbacks.remove(name);
                         staticCallbacks.remove(name+'=');
                         staticNames.put(name,new AssignedName(name,AssignedName.METHOD));
                     }
                 }
                 StaticMethodInvoker invoker = (StaticMethodInvoker)staticCallbacks.get(name);
                 if (invoker == null) {
                     invoker = new StaticMethodInvoker(name);
                     staticCallbacks.put(name,invoker);
                 }
                 invoker.addMethod(method,javaClass);
             } else {
                 AssignedName assignedName = (AssignedName)instanceNames.get(name);
                 if (assignedName == null) {
                     instanceNames.put(name,new AssignedName(name,AssignedName.METHOD));
                 } else {
                     if (assignedName.type < AssignedName.METHOD)
                         continue;
                     if (assignedName.type != AssignedName.METHOD) {
                         instanceCallbacks.remove(name);
                         instanceCallbacks.remove(name+'=');
                         instanceNames.put(name,new AssignedName(name,AssignedName.METHOD));
                     }
                 }
                 InstanceMethodInvoker invoker = (InstanceMethodInvoker)instanceCallbacks.get(name);
                 if (invoker == null) {
                     invoker = new InstanceMethodInvoker(name);
                     instanceCallbacks.put(name,invoker);
                 }
                 invoker.addMethod(method,javaClass);
             }
         }
         this.staticAssignedNames = staticNames;
         this.instanceAssignedNames = instanceNames;
         this.staticCallbacks = staticCallbacks;
         this.instanceCallbacks = instanceCallbacks;
         this.constantFields = constantFields;
     }
     
     public void setupProxy(final RubyClass proxy) {
         assert proxyLock.isHeldByCurrentThread();
         proxy.defineFastMethod("__jsend!", __jsend_method);
         final Class javaClass = javaClass();
         if (javaClass.isInterface()) {
             setupInterfaceProxy(proxy);
             return;
         }
         assert this.proxyClass == null;
         this.unfinishedProxyClass = proxy;
         if (javaClass.isArray() || javaClass.isPrimitive()) {
             // see note below re: 2-field kludge
             this.proxyClass = proxy;
             this.proxyModule = proxy;
             return;
         }
 
         for (Iterator iter = constantFields.iterator(); iter.hasNext(); ) {
             ((ConstantField)iter.next()).install(proxy);
         }
         for (Iterator iter = staticCallbacks.values().iterator(); iter.hasNext(); ) {
             NamedCallback callback = (NamedCallback)iter.next();
             if (callback.type == NamedCallback.STATIC_METHOD && callback.hasLocalMethod()) {
                 assignAliases((MethodCallback)callback,staticAssignedNames);
             }
             callback.install(proxy);
         }
         for (Iterator iter = instanceCallbacks.values().iterator(); iter.hasNext(); ) {
             NamedCallback callback = (NamedCallback)iter.next();
             if (callback.type == NamedCallback.INSTANCE_METHOD && callback.hasLocalMethod()) {
                 assignAliases((MethodCallback)callback,instanceAssignedNames);
             }
             callback.install(proxy);
         }
         // setup constants for public inner classes
         Class[] classes = javaClass.getClasses();
         for (int i = classes.length; --i >= 0; ) {
             if (javaClass == classes[i].getDeclaringClass()) {
                 Class clazz = classes[i];
                 String simpleName = getSimpleName(clazz);
                 
                 if (simpleName.length() == 0) continue;
                 
                 // Ignore bad constant named inner classes pending JRUBY-697
                 if (IdUtil.isConstant(simpleName) && proxy.getConstantAt(simpleName) == null) {
                     proxy.setConstant(simpleName,
                         Java.get_proxy_class(JAVA_UTILITIES,get(getRuntime(),clazz)));
                 }
             }
         }
         // FIXME: bit of a kludge here (non-interface classes assigned to both
         // class and module fields). simplifies proxy extender code, will go away
         // when JI is overhauled (and proxy extenders are deprecated).
         this.proxyClass = proxy;
         this.proxyModule = proxy;
 
         applyProxyExtenders();
 
         // TODO: we can probably release our references to the constantFields
         // array and static/instance callback hashes at this point. 
     }
 
     private static void assignAliases(MethodCallback callback, Map assignedNames) {
         String name = callback.name;
         addUnassignedAlias(getRubyCasedName(name),assignedNames,callback);
         // logic adapted from java.beans.Introspector
         if (!(name.length() > 3 || name.startsWith("is")))
             return;
 
         String javaPropertyName = getJavaPropertyName(name);
         if (javaPropertyName == null)
             return; // not a Java property name, done with this method
 
         for (Iterator iter = callback.methods.iterator(); iter.hasNext(); ) {
             Method method = (Method)iter.next();
             Class[] argTypes = method.getParameterTypes();
             Class resultType = method.getReturnType();
             int argCount = argTypes.length;
             if (argCount == 0) {
                 if (name.startsWith("get")) {
                     addUnassignedAlias(getRubyCasedName(name).substring(4),assignedNames,callback);
                     addUnassignedAlias(javaPropertyName,assignedNames,callback);
                 } else if (resultType == boolean.class && name.startsWith("is")) {
                     String rubyName = getRubyCasedName(name).substring(3);
                     if (rubyName != null) {
                         addUnassignedAlias(rubyName,assignedNames,callback);
                         addUnassignedAlias(rubyName+'?',assignedNames,callback);
                     }
                     if (!javaPropertyName.equals(rubyName)) {
                         addUnassignedAlias(javaPropertyName,assignedNames,callback);
                         addUnassignedAlias(javaPropertyName+'?',assignedNames,callback);
                     }
                 }
             } else if (argCount == 1) {
                 // indexed get
                 if (argTypes[0] == int.class && name.startsWith("get")) {
                     addUnassignedAlias(getRubyCasedName(name).substring(4),assignedNames,callback);
                     addUnassignedAlias(javaPropertyName,assignedNames,callback);
                 } else if (resultType == void.class && name.startsWith("set")) {
                     String rubyName = getRubyCasedName(name).substring(4);
                     if (rubyName != null) {
                         addUnassignedAlias(rubyName + '=',assignedNames,callback);
                     }
                     if (!javaPropertyName.equals(rubyName)) {
                         addUnassignedAlias(javaPropertyName + '=',assignedNames,callback);
                     }
                 }
             }
         }
     }
     
     private static void addUnassignedAlias(String name, Map assignedNames,
             MethodCallback callback) {
         if (name != null) {
             AssignedName assignedName = (AssignedName)assignedNames.get(name);
             if (assignedName == null) {
                 callback.addAlias(name);
                 assignedNames.put(name,new AssignedName(name,AssignedName.ALIAS));
             } else if (assignedName.type == AssignedName.ALIAS) {
                 callback.addAlias(name);
             } else if (assignedName.type > AssignedName.ALIAS) {
                 // TODO: there will be some additional logic in this branch
                 // dealing with conflicting protected fields. 
                 callback.addAlias(name);
                 assignedNames.put(name,new AssignedName(name,AssignedName.ALIAS));
             }
         }
     }
 
     private static final Pattern JAVA_PROPERTY_CHOPPER = Pattern.compile("(get|set|is)([A-Z0-9])(.*)");
     public static String getJavaPropertyName(String beanMethodName) {
         Matcher m = JAVA_PROPERTY_CHOPPER.matcher(beanMethodName);
 
         if (!m.find()) return null;
         String javaPropertyName = m.group(2).toLowerCase() + m.group(3);
         return javaPropertyName;
     }
 
     private static final Pattern CAMEL_CASE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");    
     public static String getRubyCasedName(String javaCasedName) {
         Matcher m = CAMEL_CASE_SPLITTER.matcher(javaCasedName);
         String rubyCasedName = m.replaceAll("$1_$2").toLowerCase();
         if (rubyCasedName.equals(javaCasedName)) {
             return null;
         }
         return rubyCasedName;
     }
     
     
     // old (quasi-deprecated) interface class
     private void setupInterfaceProxy(final RubyClass proxy) {
         assert javaClass().isInterface();
         assert proxyLock.isHeldByCurrentThread();
         assert this.proxyClass == null;
         this.proxyClass = proxy;
         // nothing else to here - the module version will be
         // included in the class.
     }
     
     public void setupInterfaceModule(final RubyModule module) {
         assert javaClass().isInterface();
         assert proxyLock.isHeldByCurrentThread();
         assert this.proxyModule == null;
         this.unfinishedProxyModule = module;
         final Class javaClass = javaClass();
         for (Iterator iter = constantFields.iterator(); iter.hasNext(); ){
             ((ConstantField)iter.next()).install(module);
         }
         // setup constants for public inner classes
         final Class[] classes = javaClass.getClasses();
         for (int i = classes.length; --i >= 0; ) {
             if (javaClass == classes[i].getDeclaringClass()) {
                 Class clazz = classes[i];
                 String simpleName = getSimpleName(clazz);
                 if (simpleName.length() == 0) continue;
                 
                 // Ignore bad constant named inner classes pending JRUBY-697
                 if (IdUtil.isConstant(simpleName) && module.getConstantAt(simpleName) == null) {
                     module.const_set(getRuntime().newString(simpleName),
                         Java.get_proxy_class(JAVA_UTILITIES,get(getRuntime(),clazz)));
                 }
             }
         }
         
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
                 getRuntime().getWarnings().warn(" proxy extender added after proxy class created for " + this);
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
 
-    private void extendProxy(final IRubyObject extender) {
+    private void extendProxy(IRubyObject extender) {
         extender.callMethod(getRuntime().getCurrentContext(), "extend_proxy", proxyModule);
     }
     
-    public IRubyObject extend_proxy(final IRubyObject extender) {
+    public IRubyObject extend_proxy(IRubyObject extender) {
         addProxyExtender(extender);
         return getRuntime().getNil();
     }
     
-    public static JavaClass get(final Ruby runtime, final Class klass) {
+    public static JavaClass get(Ruby runtime, Class klass) {
         JavaClass javaClass = runtime.getJavaSupport().getJavaClassFromCache(klass);
         if (javaClass == null) {
             javaClass = createJavaClass(runtime,klass);
         }
         return javaClass;
     }
 
     private static synchronized JavaClass createJavaClass(final Ruby runtime, final Class klass) {
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
 
     	CallbackFactory callbackFactory = runtime.callbackFactory(JavaClass.class);
         
         result.includeModule(runtime.fastGetModule("Comparable"));
         
         JavaObject.registerRubyMethods(runtime, result);
 
         result.getMetaClass().defineFastMethod("for_name", 
                 callbackFactory.getFastSingletonMethod("for_name", IRubyObject.class));
         result.defineFastMethod("public?", 
                 callbackFactory.getFastMethod("public_p"));
         result.defineFastMethod("protected?", 
                 callbackFactory.getFastMethod("protected_p"));
         result.defineFastMethod("private?", 
                 callbackFactory.getFastMethod("private_p"));
         result.defineFastMethod("final?", 
                 callbackFactory.getFastMethod("final_p"));
         result.defineFastMethod("interface?", 
                 callbackFactory.getFastMethod("interface_p"));
         result.defineFastMethod("array?", 
                 callbackFactory.getFastMethod("array_p"));
         result.defineFastMethod("name", 
                 callbackFactory.getFastMethod("name"));
         result.defineFastMethod("class_loader", 
                 callbackFactory.getFastMethod("class_loader"));
         result.defineFastMethod("simple_name",
                 callbackFactory.getFastMethod("simple_name"));
         result.defineFastMethod("to_s", 
                 callbackFactory.getFastMethod("name"));
         result.defineFastMethod("superclass", 
                 callbackFactory.getFastMethod("superclass"));
         result.defineFastMethod("<=>", 
                 callbackFactory.getFastMethod("op_cmp", IRubyObject.class));
         result.defineFastMethod("java_instance_methods", 
                 callbackFactory.getFastMethod("java_instance_methods"));
         result.defineFastMethod("java_class_methods", 
                 callbackFactory.getFastMethod("java_class_methods"));
         result.defineFastMethod("java_method", 
                 callbackFactory.getFastOptMethod("java_method"));
         result.defineFastMethod("constructors", 
                 callbackFactory.getFastMethod("constructors"));
         result.defineFastMethod("constructor", 
                 callbackFactory.getFastOptMethod("constructor"));
         result.defineFastMethod("array_class", 
                 callbackFactory.getFastMethod("array_class"));
         result.defineFastMethod("new_array", 
                 callbackFactory.getFastMethod("new_array", IRubyObject.class));
         result.defineFastMethod("fields", 
                 callbackFactory.getFastMethod("fields"));
         result.defineFastMethod("field", 
                 callbackFactory.getFastMethod("field", IRubyObject.class));
         result.defineFastMethod("interfaces", 
                 callbackFactory.getFastMethod("interfaces"));
         result.defineFastMethod("primitive?", 
                 callbackFactory.getFastMethod("primitive_p"));
         result.defineFastMethod("assignable_from?", 
                 callbackFactory.getFastMethod("assignable_from_p", IRubyObject.class));
         result.defineFastMethod("component_type", 
                 callbackFactory.getFastMethod("component_type"));
         result.defineFastMethod("declared_instance_methods", 
                 callbackFactory.getFastMethod("declared_instance_methods"));
         result.defineFastMethod("declared_class_methods", 
                 callbackFactory.getFastMethod("declared_class_methods"));
         result.defineFastMethod("declared_fields", 
                 callbackFactory.getFastMethod("declared_fields"));
         result.defineFastMethod("declared_field", 
                 callbackFactory.getFastMethod("declared_field", IRubyObject.class));
         result.defineFastMethod("declared_constructors", 
                 callbackFactory.getFastMethod("declared_constructors"));
         result.defineFastMethod("declared_constructor", 
                 callbackFactory.getFastOptMethod("declared_constructor"));
         result.defineFastMethod("declared_classes", 
                 callbackFactory.getFastMethod("declared_classes"));
         result.defineFastMethod("declared_method", 
                 callbackFactory.getFastOptMethod("declared_method"));
 
         result.defineFastMethod("extend_proxy", 
                 callbackFactory.getFastMethod("extend_proxy", IRubyObject.class));
 
         result.getMetaClass().undefineMethod("new");
         result.getMetaClass().undefineMethod("allocate");
 
         return result;
     }
     
     public static synchronized JavaClass forName(Ruby runtime, String className) {
         Class klass = runtime.getJavaSupport().loadJavaClass(className);
         return JavaClass.get(runtime, klass);
     }
 
     public static JavaClass for_name(IRubyObject recv, IRubyObject name) {
         return forName(recv.getRuntime(), name.asJavaString());
     }
     
     private static final Callback __jsend_method = new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 String name = args[0].asJavaString();
                 
                 DynamicMethod method = self.getMetaClass().searchMethod(name);
                 int v = method.getArity().getValue();
                 
                 IRubyObject[] newArgs = new IRubyObject[args.length - 1];
                 System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
                 if(v < 0 || v == (newArgs.length)) {
                     return RuntimeHelpers.invoke(self.getRuntime().getCurrentContext(), self, name, newArgs, CallType.FUNCTIONAL, block);
                 } else {
                     RubyClass superClass = self.getMetaClass().getSuperClass();
                     return RuntimeHelpers.invokeAs(self.getRuntime().getCurrentContext(), superClass, self, name, newArgs, CallType.SUPER, block);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         };
 
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(javaClass().getModifiers()));
     }
 
     public RubyBoolean protected_p() {
         return getRuntime().newBoolean(Modifier.isProtected(javaClass().getModifiers()));
     }
 
     public RubyBoolean private_p() {
         return getRuntime().newBoolean(Modifier.isPrivate(javaClass().getModifiers()));
     }
 
 	public Class javaClass() {
 		return (Class) getValue();
 	}
 
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(javaClass().getModifiers()));
     }
 
     public RubyBoolean interface_p() {
         return getRuntime().newBoolean(javaClass().isInterface());
     }
 
     public RubyBoolean array_p() {
         return getRuntime().newBoolean(javaClass().isArray());
     }
     
     public RubyString name() {
         return getRuntime().newString(javaClass().getName());
     }
 
     public IRubyObject class_loader() {
         return Java.java_to_ruby(this, JavaObject.wrap(getRuntime(),javaClass().getClassLoader()), Block.NULL_BLOCK);
     }
 
     private static String getSimpleName(Class class_) {
  		if (class_.isArray()) {
  			return getSimpleName(class_.getComponentType()) + "[]";
  		}
  
  		String className = class_.getName();
  
         int i = className.lastIndexOf('$');
  		if (i != -1) {
             do {
  				i++;
  			} while (i < className.length() && Character.isDigit(className.charAt(i)));
  			return className.substring(i);
  		}
  
  		return className.substring(className.lastIndexOf('.') + 1);
  	}
 
     public RubyString simple_name() {
         return getRuntime().newString(getSimpleName(javaClass()));
     }
 
     public IRubyObject superclass() {
         Class superclass = javaClass().getSuperclass();
         if (superclass == null) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), superclass);
     }
 
     public RubyFixnum op_cmp(IRubyObject other) {
         if (! (other instanceof JavaClass)) {
             throw getRuntime().newTypeError("<=> requires JavaClass (" + other.getType() + " given)");
         }
         JavaClass otherClass = (JavaClass) other;
         if (this.javaClass() == otherClass.javaClass()) {
             return getRuntime().newFixnum(0);
         }
         if (otherClass.javaClass().isAssignableFrom(this.javaClass())) {
             return getRuntime().newFixnum(-1);
         }
         return getRuntime().newFixnum(1);
     }
 
     public RubyArray java_instance_methods() {
         return java_methods(javaClass().getMethods(), false);
     }
 
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
 
 	public RubyArray java_class_methods() {
 	    return java_methods(javaClass().getMethods(), true);
     }
 
 	public RubyArray declared_class_methods() {
 	    return java_methods(javaClass().getDeclaredMethods(), true);
     }
 
 	public JavaMethod java_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asJavaString();
         Class[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.create(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     public JavaMethod declared_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asJavaString();
         Class[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.createDeclared(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     private Class[] buildArgumentTypes(IRubyObject[] args) throws ClassNotFoundException {
         if (args.length < 1) {
             throw getRuntime().newArgumentError(args.length, 1);
         }
         Class[] argumentTypes = new Class[args.length - 1];
         for (int i = 1; i < args.length; i++) {
             JavaClass type = for_name(this, args[i]);
             argumentTypes[i - 1] = type.javaClass();
         }
         return argumentTypes;
     }
 
     public RubyArray constructors() {
         if (constructors == null) {
             constructors = buildConstructors(javaClass().getConstructors());
         }
         return constructors;
     }
     
     public RubyArray declared_classes() {
         Ruby runtime = getRuntime();
         RubyArray result = runtime.newArray();
         Class javaClass = javaClass();
         try {
             Class[] classes = javaClass.getDeclaredClasses();
             for (int i = 0; i < classes.length; i++) {
                 if (Modifier.isPublic(classes[i].getModifiers())) {
                     result.append(get(runtime, classes[i]));
                 }
             }
         } catch (SecurityException e) {
             // restrictive security policy; no matter, we only want public
             // classes anyway
             try {
                 Class[] classes = javaClass.getClasses();
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
 
     public RubyArray declared_constructors() {
         return buildConstructors(javaClass().getDeclaredConstructors());
     }
 
     private RubyArray buildConstructors(Constructor[] constructors) {
         RubyArray result = getRuntime().newArray(constructors.length);
         for (int i = 0; i < constructors.length; i++) {
             result.append(new JavaConstructor(getRuntime(), constructors[i]));
         }
         return result;
     }
 
     public JavaConstructor constructor(IRubyObject[] args) {
         try {
             Class[] parameterTypes = buildClassArgs(args);
             Constructor constructor;
             constructor = javaClass().getConstructor(parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
             throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     public JavaConstructor declared_constructor(IRubyObject[] args) {
         try {
             Class[] parameterTypes = buildClassArgs(args);
             Constructor constructor;
             constructor = javaClass().getDeclaredConstructor (parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
             throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     private Class[] buildClassArgs(IRubyObject[] args) {
         Class[] parameterTypes = new Class[args.length];
         for (int i = 0; i < args.length; i++) {
             String name = args[i].asJavaString();
             parameterTypes[i] = getRuntime().getJavaSupport().loadJavaClass(name);
         }
         return parameterTypes;
     }
 
     public JavaClass array_class() {
         return JavaClass.get(getRuntime(), Array.newInstance(javaClass(), 0).getClass());
     }
    
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
 
     public RubyArray fields() {
         return buildFieldResults(javaClass().getFields());
     }
 
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
 
 	public JavaField field(IRubyObject name) {
 		String stringName = name.asJavaString();
         try {
             Field field = javaClass().getField(stringName);
 			return new JavaField(getRuntime(),field);
         } catch (NoSuchFieldException nsfe) {
             throw undefinedFieldError(stringName);
         }
     }
 
 	public JavaField declared_field(IRubyObject name) {
 		String stringName = name.asJavaString();
         try {
             Field field = javaClass().getDeclaredField(stringName);
 			return new JavaField(getRuntime(),field);
         } catch (NoSuchFieldException nsfe) {
             throw undefinedFieldError(stringName);
         }
     }
 
     private RaiseException undefinedFieldError(String name) {
         return getRuntime().newNameError("undefined field '" + name + "' for class '" + javaClass().getName() + "'", name);
     }
 
     public RubyArray interfaces() {
         Class[] interfaces = javaClass().getInterfaces();
         RubyArray result = getRuntime().newArray(interfaces.length);
         for (int i = 0; i < interfaces.length; i++) {
             result.append(JavaClass.get(getRuntime(), interfaces[i]));
         }
         return result;
     }
 
     public RubyBoolean primitive_p() {
         return getRuntime().newBoolean(isPrimitive());
     }
 
     public RubyBoolean assignable_from_p(IRubyObject other) {
         if (! (other instanceof JavaClass)) {
             throw getRuntime().newTypeError("assignable_from requires JavaClass (" + other.getType() + " given)");
         }
 
         Class otherClass = ((JavaClass) other).javaClass();
         return assignable(javaClass(), otherClass) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     static boolean assignable(Class thisClass, Class otherClass) {
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
 
     public JavaClass component_type() {
         if (! javaClass().isArray()) {
             throw getRuntime().newTypeError("not a java array-class");
         }
         return JavaClass.get(getRuntime(), javaClass().getComponentType());
     }
     
 }
diff --git a/src/org/jruby/javasupport/JavaObject.java b/src/org/jruby/javasupport/JavaObject.java
index 55c5137f73..d9af8524dc 100644
--- a/src/org/jruby/javasupport/JavaObject.java
+++ b/src/org/jruby/javasupport/JavaObject.java
@@ -1,203 +1,193 @@
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyString;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  * @author  jpetersen
  */
 public class JavaObject extends RubyObject {
     private static Object NULL_LOCK = new Object();
     private final Object value;
 
     protected JavaObject(Ruby runtime, RubyClass rubyClass, Object value) {
         super(runtime, rubyClass);
         this.value = value;
     }
 
     protected JavaObject(Ruby runtime, Object value) {
         this(runtime, runtime.getJavaSupport().getJavaObjectClass(), value);
     }
 
     public static JavaObject wrap(Ruby runtime, Object value) {
-        Object lock = value == null ? NULL_LOCK : value;
-        
-        synchronized (lock) {
-            JavaObject wrapper = runtime.getJavaSupport().getJavaObjectFromCache(value);
-            if (wrapper == null) {
-            	if (value == null) {
-            		wrapper = new JavaObject(runtime, value);
-            	} else if (value.getClass().isArray()) {
-                	wrapper = new JavaArray(runtime, value);
-                } else if (value.getClass().equals(Class.class)) {
-                	wrapper = JavaClass.get(runtime, (Class)value);
-                } else {
-                	wrapper = new JavaObject(runtime, value);
-                }
-                runtime.getJavaSupport().putJavaObjectIntoCache(wrapper);
+        if (value != null) {
+            if (value instanceof Class) {
+                return JavaClass.get(runtime, (Class)value);
+            } else if (value.getClass().isArray()) {
+                return new JavaArray(runtime, value);
             }
-            return wrapper;
         }
+        return new JavaObject(runtime, value);
     }
 
     public Class getJavaClass() {
         return value != null ? value.getClass() : Void.TYPE;
     }
 
     public Object getValue() {
         return value;
     }
 
     public static RubyClass createJavaObjectClass(Ruby runtime, RubyModule javaModule) {
         // FIXME: Ideally JavaObject instances should be marshallable, which means that
         // the JavaObject metaclass should have an appropriate allocator. JRUBY-414
     	RubyClass result = javaModule.defineClassUnder("JavaObject", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
     	registerRubyMethods(runtime, result);
 
         result.getMetaClass().undefineMethod("new");
         result.getMetaClass().undefineMethod("allocate");
         
         result.setMarshal(ObjectMarshal.NOT_MARSHALABLE_MARSHAL);
 
         return result;
     }
 
 	protected static void registerRubyMethods(Ruby runtime, RubyClass result) {
 		CallbackFactory callbackFactory = runtime.callbackFactory(JavaObject.class);
 
         result.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         result.defineFastMethod("==", callbackFactory.getFastMethod("op_equal", IRubyObject.class));
         result.defineFastMethod("eql?", callbackFactory.getFastMethod("op_equal", IRubyObject.class));
         result.defineFastMethod("equal?", callbackFactory.getFastMethod("same", IRubyObject.class));
         result.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
         result.defineFastMethod("java_type", callbackFactory.getFastMethod("java_type"));
         result.defineFastMethod("java_class", callbackFactory.getFastMethod("java_class"));
         result.defineFastMethod("java_proxy?", callbackFactory.getFastMethod("is_java_proxy"));
         result.defineMethod("synchronized", callbackFactory.getMethod("ruby_synchronized"));
         result.defineFastMethod("length", callbackFactory.getFastMethod("length"));
         result.defineFastMethod("[]", callbackFactory.getFastMethod("aref", IRubyObject.class));
         result.defineFastMethod("[]=", callbackFactory.getFastMethod("aset", IRubyObject.class, IRubyObject.class));
         result.defineFastMethod("fill", callbackFactory.getFastMethod("afill", IRubyObject.class, IRubyObject.class, IRubyObject.class));
 	}
 
 	public RubyFixnum hash() {
         return getRuntime().newFixnum(value == null ? 0 : value.hashCode());
     }
 
     public IRubyObject to_s() {
         String s = value == null ? "" : value.toString();
 
         return s == null ? getRuntime().getNil() : RubyString.newUnicodeString(getRuntime(), s);
     }
 
     public IRubyObject op_equal(IRubyObject other) {
         if (!(other instanceof JavaObject)) {
             other = other.getInstanceVariables().fastGetInstanceVariable("@java_object");
             if (!(other instanceof JavaObject)) {
                 return getRuntime().getFalse();
             }
         }
     	
         if (getValue() == null && ((JavaObject) other).getValue() == null) {
             return getRuntime().getTrue();
         }
     	
         boolean isEqual = getValue().equals(((JavaObject) other).getValue());
         return isEqual ? getRuntime().getTrue() : getRuntime().getFalse();
     }
     
     public IRubyObject same(IRubyObject other) {
         if (!(other instanceof JavaObject)) {
             other = other.getInstanceVariables().fastGetInstanceVariable("@java_object");
             if (!(other instanceof JavaObject)) {
               return getRuntime().getFalse();
             }
         }
       
         if (getValue() == null && ((JavaObject) other).getValue() == null) {
             return getRuntime().getTrue();
         }
       
         boolean isSame = getValue() == ((JavaObject) other).getValue();
         return isSame ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public RubyString java_type() {
         return getRuntime().newString(getJavaClass().getName());
     }
 
     public IRubyObject java_class() {
         return JavaClass.get(getRuntime(), getJavaClass());
     }
 
     public RubyFixnum length() {
         throw getRuntime().newTypeError("not a java array");
     }
 
     public IRubyObject aref(IRubyObject index) {
         throw getRuntime().newTypeError("not a java array");
     }
 
     public IRubyObject aset(IRubyObject index, IRubyObject someValue) {
         throw getRuntime().newTypeError("not a java array");
     }
     
     public IRubyObject afill (IRubyObject beginIndex, IRubyObject endIndex, IRubyObject someValue) {
         throw getRuntime().newTypeError("not a java array");
     }
     
     public IRubyObject is_java_proxy() {
         return getRuntime().getTrue();
     }
 
     public IRubyObject ruby_synchronized(Block block) {
         Object lock = getValue();
         synchronized (lock != null ? lock : NULL_LOCK) {
             return block.yield(getRuntime().getCurrentContext(), null);
         }
     }
 }
diff --git a/src/org/jruby/javasupport/JavaSupport.java b/src/org/jruby/javasupport/JavaSupport.java
index 08a9d5c9f4..7db31495dd 100644
--- a/src/org/jruby/javasupport/JavaSupport.java
+++ b/src/org/jruby/javasupport/JavaSupport.java
@@ -1,264 +1,263 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 
-import java.lang.ref.WeakReference;
-import java.net.URL;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
-import org.jruby.util.WeakIdentityHashMap;
 import org.jruby.exceptions.RaiseException;
+import org.jruby.javasupport.util.ObjectProxyCache;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 
 public class JavaSupport {
     private final Ruby runtime;
 
     private final Map exceptionHandlers = new HashMap();
+    
+    private final ObjectProxyCache<IRubyObject,RubyClass> objectProxyCache = 
+        // TODO: specifying soft refs, may want to compare memory consumption,
+        // behavior with weak refs (specify WEAK in place of SOFT below)
+        new ObjectProxyCache<IRubyObject,RubyClass>(ObjectProxyCache.ReferenceType.SOFT) {
+
+        public IRubyObject allocateProxy(Object javaObject, RubyClass clazz) {
+            IRubyObject proxy = clazz.allocate();
+            proxy.getInstanceVariables().fastSetInstanceVariable("@java_object",
+                    JavaObject.wrap(clazz.getRuntime(), javaObject));
+            return proxy;
+        }
+
+    };
 
-    private final Map instanceCache = Collections.synchronizedMap(new WeakIdentityHashMap(100));
     
     // There's not a compelling reason to keep JavaClass instances in a weak map
     // (any proxies created are [were] kept in a non-weak map, so in most cases they will
     // stick around anyway), and some good reasons not to (JavaClass creation is
     // expensive, for one; many lookups are performed when passing parameters to/from
     // methods; etc.).
     // TODO: faster custom concurrent map
     private final ConcurrentHashMap<Class,JavaClass> javaClassCache =
         new ConcurrentHashMap<Class,JavaClass>(128);
     
     // FIXME: needs to be rethought
     private final Map matchCache = Collections.synchronizedMap(new HashMap(128));
 
     private Callback concreteProxyCallback;
 
     private RubyModule javaModule;
     private RubyModule javaUtilitiesModule;
     private RubyClass javaObjectClass;
     private RubyClass javaClassClass;
     private RubyClass javaArrayClass;
     private RubyClass javaProxyClass;
     private RubyModule javaInterfaceTemplate;
     private RubyModule packageModuleTemplate;
     private RubyClass arrayProxyClass;
     private RubyClass concreteProxyClass;
     
     
     public JavaSupport(Ruby ruby) {
         this.runtime = ruby;
     }
 
     final synchronized void setConcreteProxyCallback(Callback concreteProxyCallback) {
         if (this.concreteProxyCallback == null) {
             this.concreteProxyCallback = concreteProxyCallback;
         }
     }
     
     final Callback getConcreteProxyCallback() {
         return concreteProxyCallback;
     }
     
     final Map getMatchCache() {
         return matchCache;
     }
 
     
     public Class loadJavaClass(String className) {
         if (Ruby.isSecurityRestricted() && className.startsWith("sun.misc.")) {
             throw runtime.newNameError("security: cannot load class " + className, className);
         }
         try {
             Class result = primitiveClass(className);
             if(result == null) {
                 return (Ruby.isSecurityRestricted()) ? Class.forName(className) :
                    Class.forName(className, true, runtime.getJRubyClassLoader());
             }
             return result;
         } catch (ClassNotFoundException cnfExcptn) {
             throw runtime.newNameError("cannot load Java class " + className, className);
         }
     }
 
     public JavaClass getJavaClassFromCache(Class clazz) {
         return javaClassCache.get(clazz);
     }
     
     public void putJavaClassIntoCache(JavaClass clazz) {
         javaClassCache.put(clazz.javaClass(), clazz);
     }
     
     public void defineExceptionHandler(String exceptionClass, RubyProc handler) {
         exceptionHandlers.put(exceptionClass, handler);
     }
 
     public void handleNativeException(Throwable exception) {
         if (exception instanceof RaiseException) {
             throw (RaiseException) exception;
         }
         Class excptnClass = exception.getClass();
         RubyProc handler = (RubyProc)exceptionHandlers.get(excptnClass.getName());
         while (handler == null &&
                excptnClass != Throwable.class) {
             excptnClass = excptnClass.getSuperclass();
         }
         if (handler != null) {
             handler.call(new IRubyObject[]{JavaUtil.convertJavaToRuby(runtime, exception)});
         } else {
             throw createRaiseException(exception);
         }
     }
 
     private RaiseException createRaiseException(Throwable exception) {
         RaiseException re = RaiseException.createNativeRaiseException(runtime, exception);
         
         return re;
     }
 
     private static Class primitiveClass(String name) {
         if (name.equals("long")) {
             return Long.TYPE;
         } else if (name.equals("int")) {
             return Integer.TYPE;
         } else if (name.equals("boolean")) {
             return Boolean.TYPE;
         } else if (name.equals("char")) {
             return Character.TYPE;
         } else if (name.equals("short")) {
             return Short.TYPE;
         } else if (name.equals("byte")) {
             return Byte.TYPE;
         } else if (name.equals("float")) {
             return Float.TYPE;
         } else if (name.equals("double")) {
             return Double.TYPE;
         }
         return null;
     }
-
-    public JavaObject getJavaObjectFromCache(Object object) {
-    	WeakReference ref = (WeakReference)instanceCache.get(object);
-    	if (ref == null) {
-    		return null;
-    	}
-    	JavaObject javaObject = (JavaObject) ref.get();
-    	if (javaObject != null && javaObject.getValue() == object) {
-    		return javaObject;
-    	}
-        return null;
-    }
     
-    public void putJavaObjectIntoCache(JavaObject object) {
-    	instanceCache.put(object.getValue(), new WeakReference(object));
+    public ObjectProxyCache<IRubyObject,RubyClass> getObjectProxyCache() {
+        return objectProxyCache;
     }
 
     // not synchronizing these methods, no harm if these values get set twice...
     
     public RubyModule getJavaModule() {
         if (javaModule == null) {
             javaModule = runtime.fastGetModule("Java");
         }
         return javaModule;
     }
     
     public RubyModule getJavaUtilitiesModule() {
         if (javaUtilitiesModule == null) {
             javaUtilitiesModule = runtime.fastGetModule("JavaUtilities");
         }
         return javaUtilitiesModule;
     }
     
     public RubyClass getJavaObjectClass() {
         if (javaObjectClass == null) {
             javaObjectClass = getJavaModule().fastGetClass("JavaObject");
         }
         return javaObjectClass;
     }
 
     public RubyClass getJavaArrayClass() {
         if (javaArrayClass == null) {
             javaArrayClass = getJavaModule().fastGetClass("JavaArray");
         }
         return javaArrayClass;
     }
     
     public RubyClass getJavaClassClass() {
         if(javaClassClass == null) {
             javaClassClass = getJavaModule().fastGetClass("JavaClass");
         }
         return javaClassClass;
     }
     
     public RubyModule getJavaInterfaceTemplate() {
         if (javaInterfaceTemplate == null) {
             javaInterfaceTemplate = runtime.fastGetModule("JavaInterfaceTemplate");
         }
         return javaInterfaceTemplate;
     }
     
     public RubyModule getPackageModuleTemplate() {
         if (packageModuleTemplate == null) {
             packageModuleTemplate = runtime.fastGetModule("JavaPackageModuleTemplate");
         }
         return packageModuleTemplate;
     }
     
     public RubyClass getJavaProxyClass() {
         if (javaProxyClass == null) {
             javaProxyClass = runtime.fastGetClass("JavaProxy");
         }
         return javaProxyClass;
     }
     
     public RubyClass getConcreteProxyClass() {
         if (concreteProxyClass == null) {
             concreteProxyClass = runtime.fastGetClass("ConcreteJavaProxy");
         }
         return concreteProxyClass;
     }
     
     public RubyClass getArrayProxyClass() {
         if (arrayProxyClass == null) {
             arrayProxyClass = runtime.fastGetClass("ArrayJavaProxy");
         }
         return arrayProxyClass;
     }
 
 }
diff --git a/src/org/jruby/javasupport/JavaUtil.java b/src/org/jruby/javasupport/JavaUtil.java
index 69fb1be634..58607e4dc4 100644
--- a/src/org/jruby/javasupport/JavaUtil.java
+++ b/src/org/jruby/javasupport/JavaUtil.java
@@ -1,473 +1,504 @@
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
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Don Schwartz <schwardo@users.sourceforge.net>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 
 import java.io.UnsupportedEncodingException;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.util.ByteList;
 
 /**
  *
  * @author Jan Arne Petersen, Alan Moore
  */
 public class JavaUtil {
 
     public static Object convertRubyToJava(IRubyObject rubyObject) {
         return convertRubyToJava(rubyObject, null);
     }
     
     public interface RubyConverter {
         public Object convert(IRubyObject rubyObject);
     }
     
     public static final RubyConverter RUBY_BOOLEAN_CONVERTER = new RubyConverter() {
         public Object convert(IRubyObject rubyObject) {
             return Boolean.valueOf(rubyObject.isTrue());
         }
     };
     
     public static final RubyConverter RUBY_BYTE_CONVERTER = new RubyConverter() {
         public Object convert(IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Byte((byte) ((RubyNumeric) rubyObject.callMethod(
                         rubyObject.getRuntime().getCurrentContext(), MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Byte((byte) 0);
         }
     };
     
     public static final RubyConverter RUBY_SHORT_CONVERTER = new RubyConverter() {
         public Object convert(IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Short((short) ((RubyNumeric) rubyObject.callMethod(
                         rubyObject.getRuntime().getCurrentContext(),
                         MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Short((short) 0);
         }
     };
     
     public static final RubyConverter RUBY_INTEGER_CONVERTER = new RubyConverter() {
         public Object convert(IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Integer((int) ((RubyNumeric) rubyObject.callMethod(
                         rubyObject.getRuntime().getCurrentContext(),
                         MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Integer(0);
         }
     };
     
     public static final RubyConverter RUBY_LONG_CONVERTER = new RubyConverter() {
         public Object convert(IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Long(((RubyNumeric) rubyObject.callMethod(
                         rubyObject.getRuntime().getCurrentContext(),
                         MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Long(0);
         }
     };
     
     public static final RubyConverter RUBY_FLOAT_CONVERTER = new RubyConverter() {
         public Object convert(IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Float((float) ((RubyNumeric) rubyObject.callMethod(
                         rubyObject.getRuntime().getCurrentContext(),
                         MethodIndex.TO_F, "to_f")).getDoubleValue());
             }
             return new Float(0.0);
         }
     };
     
     public static final RubyConverter RUBY_DOUBLE_CONVERTER = new RubyConverter() {
         public Object convert(IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Double(((RubyNumeric) rubyObject.callMethod(
                         rubyObject.getRuntime().getCurrentContext(),
                         MethodIndex.TO_F, "to_f")).getDoubleValue());
             }
             return new Double(0.0);
         }
     };
     
     public static final Map<Class, RubyConverter> RUBY_CONVERTERS = new HashMap<Class, RubyConverter>();
     
     static {
         RUBY_CONVERTERS.put(Boolean.class, RUBY_BOOLEAN_CONVERTER);
         RUBY_CONVERTERS.put(Boolean.TYPE, RUBY_BOOLEAN_CONVERTER);
         RUBY_CONVERTERS.put(Byte.class, RUBY_BYTE_CONVERTER);
         RUBY_CONVERTERS.put(Byte.TYPE, RUBY_BYTE_CONVERTER);
         RUBY_CONVERTERS.put(Short.class, RUBY_SHORT_CONVERTER);
         RUBY_CONVERTERS.put(Short.TYPE, RUBY_SHORT_CONVERTER);
         RUBY_CONVERTERS.put(Integer.class, RUBY_INTEGER_CONVERTER);
         RUBY_CONVERTERS.put(Integer.TYPE, RUBY_INTEGER_CONVERTER);
         RUBY_CONVERTERS.put(Long.class, RUBY_LONG_CONVERTER);
         RUBY_CONVERTERS.put(Long.TYPE, RUBY_LONG_CONVERTER);
         RUBY_CONVERTERS.put(Float.class, RUBY_FLOAT_CONVERTER);
         RUBY_CONVERTERS.put(Float.TYPE, RUBY_FLOAT_CONVERTER);
         RUBY_CONVERTERS.put(Double.class, RUBY_DOUBLE_CONVERTER);
         RUBY_CONVERTERS.put(Double.TYPE, RUBY_DOUBLE_CONVERTER);
     }
 
     public static Object convertRubyToJava(IRubyObject rubyObject, Class javaClass) {
         if (rubyObject == null || rubyObject.isNil()) {
             return null;
         }
         
         ThreadContext context = rubyObject.getRuntime().getCurrentContext();
         
         if (rubyObject.respondsTo("java_object")) {
         	rubyObject = rubyObject.callMethod(context, "java_object");
         }
 
         if (rubyObject.respondsTo("to_java_object")) {
         	rubyObject = rubyObject.callMethod(context, "to_java_object");
         }
 
         if (rubyObject instanceof JavaObject) {
             Object value =  ((JavaObject) rubyObject).getValue();
             
             return convertArgument(value, javaClass);
             
         } else if (javaClass == Object.class || javaClass == null) {
             /* The Java method doesn't care what class it is, but we need to
                know what to convert it to, so we use the object's own class.
                If that doesn't help, we use String to force a call to the
                object's "to_s" method. */
             javaClass = rubyObject.getJavaClass();
         }
 
         if (javaClass.isInstance(rubyObject)) {
             // rubyObject is already of the required jruby class (or subclass)
             return rubyObject;
         }
 
         if (javaClass.isPrimitive()) {
             RubyConverter converter = RUBY_CONVERTERS.get(javaClass);
             if (converter != null) {
                 return converter.convert(rubyObject);
             }
 
             // XXX this probably isn't good enough -AM
             String s = ((RubyString) rubyObject.callMethod(context, MethodIndex.TO_S, "to_s")).toString();
             if (s.length() > 0) {
                 return new Character(s.charAt(0));
             }
 			return new Character('\0');
         } else if (javaClass == String.class) {
             RubyString rubyString = (RubyString) rubyObject.callMethod(context, MethodIndex.TO_S, "to_s");
             ByteList bytes = rubyString.getByteList();
             try {
                 return new String(bytes.unsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
             } catch (UnsupportedEncodingException uee) {
                 return new String(bytes.unsafeBytes(), bytes.begin(), bytes.length());
             }
         } else if (javaClass == ByteList.class) {
             return rubyObject.convertToString().getByteList();
         } else if (javaClass == BigInteger.class) {
          	if (rubyObject instanceof RubyBignum) {
                     return ((RubyBignum)rubyObject).getValue();
          	} else if (rubyObject instanceof RubyNumeric) {
                     return BigInteger.valueOf (((RubyNumeric)rubyObject).getLongValue());
          	} else if (rubyObject.respondsTo("to_i")) {
                     RubyNumeric rubyNumeric = ((RubyNumeric)rubyObject.callMethod(context,MethodIndex.TO_F, "to_f"));
                     return BigInteger.valueOf (rubyNumeric.getLongValue());
          	}
         } else if (javaClass == BigDecimal.class && !(rubyObject instanceof JavaObject)) {
             if (rubyObject.respondsTo("to_f")) {
                 double double_value = ((RubyNumeric)rubyObject.callMethod(context,MethodIndex.TO_F, "to_f")).getDoubleValue();
                 return new BigDecimal(double_value);
             }
         }
         try {
             return ((JavaObject) rubyObject).getValue();
         } catch (ClassCastException ex) {
             ex.printStackTrace();
             return null;
         }
     }
 
     public static IRubyObject[] convertJavaArrayToRuby(Ruby runtime, Object[] objects) {
         IRubyObject[] rubyObjects = new IRubyObject[objects.length];
         for (int i = 0; i < objects.length; i++) {
             rubyObjects[i] = convertJavaToRuby(runtime, objects[i]);
         }
         return rubyObjects;
     }
     
     public interface JavaConverter {
         public IRubyObject convert(Ruby runtime, Object object);
     }
     
     public static final JavaConverter JAVA_DEFAULT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) {
                 return runtime.getNil();
             }
 
             if (object instanceof IRubyObject) {
                 return (IRubyObject) object;
             }
-            
+ 
+            // Note: returns JavaObject instance, which is not
+            // directly usable. probably too late to change this now,
+            // supplying alternate method convertJavaToUsableRubyObject
             return JavaObject.wrap(runtime, object);
         }
     };
     
     public static final JavaConverter JAVA_BOOLEAN_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBoolean.newBoolean(runtime, ((Boolean)object).booleanValue());
         }
     };
     
     public static final JavaConverter JAVA_FLOAT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Float)object).doubleValue());
         }
     };
     
     public static final JavaConverter JAVA_DOUBLE_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Double)object).doubleValue());
         }
     };
     
     public static final JavaConverter JAVA_CHAR_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Character)object).charValue());
         }
     };
     
     public static final JavaConverter JAVA_BYTE_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Byte)object).byteValue());
         }
     };
     
     public static final JavaConverter JAVA_SHORT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Short)object).shortValue());
         }
     };
     
     public static final JavaConverter JAVA_INT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Integer)object).intValue());
         }
     };
     
     public static final JavaConverter JAVA_LONG_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Long)object).longValue());
         }
     };
     
     public static final JavaConverter JAVA_STRING_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyString.newUnicodeString(runtime, (String)object);
         }
     };
     
     public static final JavaConverter BYTELIST_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyString.newString(runtime, (ByteList)object);
         }
     };
     
     public static final JavaConverter JAVA_BIGINTEGER_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBignum.newBignum(runtime, (BigInteger)object);
         }
     };
     
-    private static final Map JAVA_CONVERTERS = new HashMap();
+    private static final Map<Class,JavaConverter> JAVA_CONVERTERS =
+        new HashMap<Class,JavaConverter>();
     
     static {
         JAVA_CONVERTERS.put(Byte.class, JAVA_BYTE_CONVERTER);
         JAVA_CONVERTERS.put(Byte.TYPE, JAVA_BYTE_CONVERTER);
         JAVA_CONVERTERS.put(Short.class, JAVA_SHORT_CONVERTER);
         JAVA_CONVERTERS.put(Short.TYPE, JAVA_SHORT_CONVERTER);
         JAVA_CONVERTERS.put(Character.class, JAVA_CHAR_CONVERTER);
         JAVA_CONVERTERS.put(Character.TYPE, JAVA_CHAR_CONVERTER);
         JAVA_CONVERTERS.put(Integer.class, JAVA_INT_CONVERTER);
         JAVA_CONVERTERS.put(Integer.TYPE, JAVA_INT_CONVERTER);
         JAVA_CONVERTERS.put(Long.class, JAVA_LONG_CONVERTER);
         JAVA_CONVERTERS.put(Long.TYPE, JAVA_LONG_CONVERTER);
         JAVA_CONVERTERS.put(Float.class, JAVA_FLOAT_CONVERTER);
         JAVA_CONVERTERS.put(Float.TYPE, JAVA_FLOAT_CONVERTER);
         JAVA_CONVERTERS.put(Double.class, JAVA_DOUBLE_CONVERTER);
         JAVA_CONVERTERS.put(Double.TYPE, JAVA_DOUBLE_CONVERTER);
         JAVA_CONVERTERS.put(Boolean.class, JAVA_BOOLEAN_CONVERTER);
         JAVA_CONVERTERS.put(Boolean.TYPE, JAVA_BOOLEAN_CONVERTER);
         
         JAVA_CONVERTERS.put(String.class, JAVA_STRING_CONVERTER);
         
         JAVA_CONVERTERS.put(ByteList.class, BYTELIST_CONVERTER);
         
         JAVA_CONVERTERS.put(BigInteger.class, JAVA_BIGINTEGER_CONVERTER);
 
     }
     
     public static JavaConverter getJavaConverter(Class clazz) {
-        JavaConverter converter = (JavaConverter)JAVA_CONVERTERS.get(clazz);
+        JavaConverter converter = JAVA_CONVERTERS.get(clazz);
         
         if (converter == null) {
             converter = JAVA_DEFAULT_CONVERTER;
         }
         
         return converter;
     }
 
+    /**
+     * Converts object to the corresponding Ruby type; however, for non-primitives,
+     * a JavaObject instance is returned. This must be subsequently wrapped by
+     * calling one of Java.wrap, Java.java_to_ruby, Java.new_instance_for, or
+     * Java.getInstance, depending on context.
+     * 
+     * @param runtime
+     * @param object 
+     * @return corresponding Ruby type, or a JavaObject instance
+     */
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object) {
         if (object == null) {
             return runtime.getNil();
         }
         return convertJavaToRuby(runtime, object, object.getClass());
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object, Class javaClass) {
         return getJavaConverter(javaClass).convert(runtime, object);
     }
+    
+    /**
+     * Returns a usable RubyObject; for types that are not converted to Ruby native
+     * types, a Java proxy will be returned. 
+     * 
+     * @param runtime
+     * @param object
+     * @return corresponding Ruby type, or a functional Java proxy
+     */
+    public static IRubyObject convertJavaToUsableRubyObject(Ruby runtime, Object object) {
+        if (object == null) return runtime.getNil();
+        JavaConverter converter = JAVA_CONVERTERS.get(object.getClass());
+        if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
+            return Java.getInstance(runtime, object);
+        }
+        return converter.convert(runtime, object);
+    }
 
     public static Class primitiveToWrapper(Class type) {
         if (type == Double.TYPE) {
             return Double.class;
         } else if (type == Float.TYPE) {
             return Float.class;
         } else if (type == Integer.TYPE) {
             return Integer.class;
         } else if (type == Long.TYPE) {
             return Long.class;
         } else if (type == Short.TYPE) {
             return Short.class;
         } else if (type == Byte.TYPE) {
             return Byte.class;
         } else if (type == Character.TYPE) {
             return Character.class;
         } else if (type == Void.TYPE) {
             return Void.class;
         } else if (type == Boolean.TYPE) {
             return Boolean.class;
         } else {
             return type;
         }
     }
 
     public static Object convertArgument(Object argument, Class parameterType) {
         if (argument instanceof JavaObject) {
             argument = ((JavaObject) argument).getValue();
             if (argument == null) {
                 return null;
             }
         }
         Class type = primitiveToWrapper(parameterType);
         if (type == Void.class) {
             return null;
         }
         if (argument instanceof Number) {
             final Number number = (Number) argument;
             if (type == Long.class) {
                 return new Long(number.longValue());
             } else if (type == Integer.class) {
                 return new Integer(number.intValue());
             } else if (type == Short.class) {
                 return new Short(number.shortValue());
             } else if (type == Byte.class) {
                 return new Byte(number.byteValue());
             } else if (type == Character.class) {
                 return new Character((char) number.intValue());
             } else if (type == Double.class) {
                 return new Double(number.doubleValue());
             } else if (type == Float.class) {
                 return new Float(number.floatValue());
             }
         }
         if (isDuckTypeConvertable(argument.getClass(), parameterType)) {
             RubyObject rubyObject = (RubyObject) argument;
             if (!rubyObject.respondsTo("java_object")) {
                 Ruby runtime = rubyObject.getRuntime();
                 IRubyObject javaUtilities = runtime.getJavaSupport().getJavaUtilitiesModule();
                 IRubyObject javaInterfaceModule = Java.get_interface_module(javaUtilities, JavaClass.get(runtime, parameterType));
                 if (!((RubyModule)javaInterfaceModule).isInstance(rubyObject)) {
                     rubyObject.extend(new IRubyObject[] {javaInterfaceModule});
                 }
                 if (rubyObject instanceof RubyProc) {
                     // Proc implementing an interface, pull in the catch-all code that lets the proc get invoked
                     // no matter what method is called on the interface
                     rubyObject.instance_eval(new IRubyObject[] {
                         runtime.newString("extend Proc::CatchAll")}, Block.NULL_BLOCK);
                 }
                 JavaObject jo = (JavaObject) rubyObject.instance_eval(new IRubyObject[] {
                     runtime.newString("send :__jcreate_meta!")}, Block.NULL_BLOCK);
                 return jo.getValue();
             }
         }
         return argument;
     }
     
     public static boolean isDuckTypeConvertable(Class providedArgumentType, Class parameterType) {
         return parameterType.isInterface() && !parameterType.isAssignableFrom(providedArgumentType) 
             && RubyObject.class.isAssignableFrom(providedArgumentType);
     }
 }
diff --git a/src/org/jruby/javasupport/util/ObjectProxyCache.java b/src/org/jruby/javasupport/util/ObjectProxyCache.java
new file mode 100644
index 0000000000..e6a3bbfde2
--- /dev/null
+++ b/src/org/jruby/javasupport/util/ObjectProxyCache.java
@@ -0,0 +1,398 @@
+package org.jruby.javasupport.util;
+
+import java.lang.ref.ReferenceQueue;
+import java.lang.ref.SoftReference;
+import java.lang.ref.WeakReference;
+
+import java.util.concurrent.locks.ReentrantLock;
+
+
+/**
+ * Maps Java objects to their proxies.  Combines elements of WeakHashMap and
+ * ConcurrentHashMap to permit unsynchronized reads.  May be configured to
+ * use either Weak (the default) or Soft references.<p>
+ * 
+ * Note that both Java objects and their proxies are held by weak/soft
+ * references; because proxies (currently) keep strong references to their
+ * Java objects, if we kept strong references to them the Java objects would
+ * never be gc'ed.  This presents a problem in the case where a user passes
+ * a Rubified Java object out to Java but keeps no reference in Ruby to the 
+ * proxy; if the object is returned to Ruby after its proxy has been gc'ed,
+ * a new (and possibly very wrong, in the case of JRuby-defined subclasses)
+ * proxy will be created.  Use of soft references may help reduce the
+ * likelihood of this occurring; users may be advised to keep Ruby-side
+ * references to prevent it occurring altogether.
+ * 
+ * @author <a href="mailto:bill.dortch@gmail.com">Bill Dortch</a>
+ * 
+ */
+public abstract class ObjectProxyCache<T,A> {
+    
+    public static enum ReferenceType { WEAK, SOFT }
+    
+    private static final int DEFAULT_SEGMENTS = 16; // must be power of 2
+    private static final int DEFAULT_SEGMENT_SIZE = 16; // must be power of 2
+    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
+    private static final int MAX_CAPACITY = 1 << 30;
+    private static final int MAX_SEGMENTS = 1 << 16;
+    private static final int VULTURE_RUN_FREQ_SECONDS = 10;
+
+    
+    private final ReferenceType referenceType;
+    private final Segment<T,A>[] segments;
+    private final int segmentShift;
+    private final int segmentMask;
+    private final Thread vulture;
+    
+    public ObjectProxyCache() {
+        this(DEFAULT_SEGMENTS, DEFAULT_SEGMENT_SIZE, ReferenceType.WEAK);
+    }
+    
+    public ObjectProxyCache(ReferenceType refType) {
+        this(DEFAULT_SEGMENTS, DEFAULT_SEGMENT_SIZE, refType);
+    }
+    
+    
+    public ObjectProxyCache(int numSegments, int initialSegCapacity, ReferenceType refType) {
+        if (numSegments <= 0 || initialSegCapacity <= 0 || refType == null) {
+            throw new IllegalArgumentException();
+        }
+        this.referenceType = refType;
+        if (numSegments > MAX_SEGMENTS) numSegments = MAX_SEGMENTS;
+    
+        // Find power-of-two sizes best matching arguments
+        int sshift = 0;
+        int ssize = 1;
+        while (ssize < numSegments) {
+            ++sshift;
+            ssize <<= 1;
+        }
+        segmentShift = 32 - sshift;
+        segmentMask = ssize - 1;
+        segments = Segment.newArray(ssize);
+    
+        if (initialSegCapacity > MAX_CAPACITY)
+            initialSegCapacity = MAX_CAPACITY;
+        int cap = 1;
+        while (cap < initialSegCapacity) cap <<= 1;
+    
+        for (int i = ssize; --i >= 0; ) {
+            segments[i] = new Segment<T,A>(cap, this);
+        }
+        // vulture thread will periodically expunge dead
+        // entries.  entries are also expunged during 'put'
+        // operations; this is designed to cover the case where
+        // many objects are created initially, followed by limited
+        // put activiity.
+        vulture = new Thread("ObjectProxyCache vulture") {
+            public void run() {
+                for ( ;; ) {
+                    try {
+                        sleep(VULTURE_RUN_FREQ_SECONDS * 1000);
+                    } catch (InterruptedException e) {}
+                    for (int i = segments.length; --i >= 0; ) {
+                        Segment seg = segments[i];
+                        seg.lock();
+                        try {
+                            seg.expunge();
+                        } finally {
+                            seg.unlock();
+                        }
+                        yield();
+                    }
+                }
+            }
+        };
+        try {
+            vulture.setDaemon(true);
+        } catch (SecurityException e) {}
+        vulture.start();
+    }
+    
+    public abstract T allocateProxy(Object javaObject, A allocator);
+    
+    public T get(Object javaObject) {
+        if (javaObject == null) return null;
+        int hash = hash(javaObject);
+        return segmentFor(hash).get(javaObject, hash);
+    }
+    
+    public T getOrCreate(Object javaObject, A allocator) {
+        if (javaObject == null || allocator == null) return null;
+        int hash = hash(javaObject);
+        return segmentFor(hash).getOrCreate(javaObject, hash, allocator);
+    }
+    
+    public void put(Object javaObject, T proxy) {
+        if (javaObject == null || proxy == null) return;
+        int hash = hash(javaObject);
+        segmentFor(hash).put(javaObject, hash, proxy);
+    }
+    
+    private static int hash(Object javaObject) {
+        int h = System.identityHashCode(javaObject);
+        h ^= (h >>> 20) ^ (h >>> 12);
+        return h ^ (h >>> 7) ^ (h >>> 4);
+    }
+    
+    private Segment<T,A> segmentFor(int hash) {
+        return segments[(hash >>> segmentShift) & segmentMask];
+    }
+    
+    // EntryRefs include hash with key to facilitate lookup by Segment#expunge
+    // after ref is removed from ReferenceQueue
+    private static interface EntryRef<T> {
+        T get();
+        int hash();
+    }
+
+    private static final class WeakEntryRef<T> extends WeakReference<T> implements EntryRef<T> {
+        final int hash;
+        WeakEntryRef(int hash, T rawObject, ReferenceQueue<Object> queue) {
+            super(rawObject, queue);
+            this.hash = hash;
+        }
+        public int hash() {
+            return hash;
+        }
+    }
+
+    private static final class SoftEntryRef<T> extends SoftReference<T> implements EntryRef<T> {
+        final int hash;
+        SoftEntryRef(int hash, T rawObject, ReferenceQueue<Object> queue) {
+            super(rawObject, queue);
+            this.hash = hash;
+        }
+        public int hash() {
+            return hash;
+        }
+    }
+
+    // Unlike WeakHashMap, our Entry does not subclass WeakReference, but rather
+    // makes it a final field.  The theory is that doing so should force a happens-before
+    // relationship WRT the WeakReference constructor, guaranteeing that the key will be
+    // visibile to other threads (unless it's been GC'ed).  See JLS 17.5 (final fields) and
+    // 17.4.5 (Happens-before order) to confirm or refute my reasoning here.
+    static class Entry<T> {
+        final EntryRef<Object> objectRef;
+        final int hash;
+        final EntryRef<T> proxyRef;
+        final Entry<T> next;
+        volatile boolean invalid = false;
+        
+        Entry(Object object, int hash, T proxy, ReferenceType type, Entry<T> next, ReferenceQueue<Object> queue) {
+            this.hash = hash;
+            this.next = next;
+            // references to the Java object and its proxy will either both be
+            // weak or both be soft, since the proxy contains a strong reference
+            // to the object, so it wouldn't make sense for the reference types
+            // to differ.
+            if (type == ReferenceType.WEAK) {
+                this.objectRef = new WeakEntryRef<Object>(hash, object, queue);
+                this.proxyRef = new WeakEntryRef<T>(hash, proxy, queue);
+            } else {
+                this.objectRef = new SoftEntryRef<Object>(hash, object, queue);
+                this.proxyRef = new SoftEntryRef<T>(hash, proxy, queue);
+            }
+        }
+        
+        // ctor used by remove/rehash
+        Entry(EntryRef<Object> objectRef, int hash, EntryRef<T> proxyRef, Entry<T> next) {
+            this.objectRef = objectRef;
+            this.hash = hash;
+            this.proxyRef = proxyRef;
+            this.next = next;
+        }
+        
+        @SuppressWarnings("unchecked")
+        static final <T> Entry<T>[] newArray(int size) {
+            return new Entry[size];
+        }
+     }
+    
+    // lame generics issues: making Segment class static and manually
+    // inserting cache reference to work around various problems generically
+    // referencing methods/vars across classes.
+    static class Segment<T,A> extends ReentrantLock {
+        
+        final ObjectProxyCache<T,A> cache;
+        final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
+        volatile Entry<T>[] entryTable;
+        int tableSize;
+        int threshold;
+        
+        Segment(int capacity, ObjectProxyCache<T,A> cache) {
+            threshold = (int)(capacity * DEFAULT_LOAD_FACTOR);
+            entryTable = Entry.newArray(capacity);
+            this.cache = cache;
+        }
+        
+        // must be called under lock
+        private void expunge() {
+            Entry<T>[] table = entryTable;
+            ReferenceQueue<Object> queue = referenceQueue;
+            EntryRef ref;
+            // note that we'll potentially see the refs for both the java object and
+            // proxy -- whichever we see first will cause the entry to be removed;
+            // the other will not match an entry and will be ignored.
+            while ((ref = (EntryRef)queue.poll()) != null) {
+                int hash;
+                for (Entry<T> e = table[(hash = ref.hash()) & (table.length - 1)]; e != null; e = e.next) {
+                    if (hash == e.hash && (ref == e.objectRef || ref == e.proxyRef)) {
+                        remove(table, hash, e);
+                        break;
+                    }
+                }
+            }
+        }
+        
+        // must be called under lock
+        private void remove(Entry<T>[] table, int hash, Entry<T> e) {
+            int index = hash & (table.length - 1);
+            Entry<T> first = table[index];
+            for (Entry<T> n = first; n != null; n = n.next) {
+                if (n == e) {
+                    Entry<T> newFirst = n.next;
+                    for (Entry<T> p = first; p != n; p = p.next) {
+                        newFirst = new Entry<T>(p.objectRef, p.hash, p.proxyRef, newFirst);
+                    }
+                    table[index] = newFirst;
+                    tableSize--;
+                    entryTable = table; // write-volatile
+                    return;
+                }
+            }
+        }
+        
+        // must be called under lock
+        private Entry<T>[] rehash() {
+            Entry<T>[] oldTable = entryTable; // read-volatile
+            int oldCapacity;
+            if ((oldCapacity = oldTable.length) >= MAX_CAPACITY) {
+                return oldTable;
+            }
+            int newCapacity = oldCapacity << 1;
+            int sizeMask = newCapacity - 1;
+            threshold = (int)(newCapacity * DEFAULT_LOAD_FACTOR);
+            Entry<T>[] newTable = Entry.newArray(newCapacity);
+            Entry<T> e;
+            for (int i = oldCapacity; --i >= 0; ) {
+                if ((e = oldTable[i]) != null) {
+                    int idx = e.hash & sizeMask;
+                    Entry<T> next;
+                    if ((next = e.next) == null) {
+                        // Single node in list
+                        newTable[idx] = e;
+                    } else {
+                        // Reuse trailing consecutive sequence at same slot
+                        int lastIdx = idx;
+                        Entry<T> lastRun = e;
+                        for (Entry<T> last = next; last != null; last = last.next) {
+                            int k;
+                            if ((k = last.hash & sizeMask) != lastIdx) {
+                                lastIdx = k;
+                                lastRun = last;
+                            }
+                        }
+                        newTable[lastIdx] = lastRun;
+                        // Clone all remaining nodes
+                        for (Entry<T> p = e; p != lastRun; p = p.next) {
+                            int k = p.hash & sizeMask;
+                            Entry<T> m = new Entry<T>(p.objectRef, p.hash, p.proxyRef, newTable[k]);
+                            newTable[k] = m;
+                        }
+                    }
+                }
+            }
+            entryTable = newTable; // write-volatile
+            return newTable;
+        }
+
+        void put(Object object, int hash, T proxy) {
+            lock();
+            try {
+                expunge();
+                Entry<T>[] table;
+                int potentialNewSize;
+                if ((potentialNewSize = tableSize + 1) > threshold) {
+                    table = rehash(); // indirect read-/write- volatile
+                } else {
+                    table = entryTable; // read-volatile
+                }
+                int index;
+                Entry<T> e;
+                for (e = table[index = hash & (table.length - 1)]; e != null; e = e.next) {
+                    if (hash == e.hash && object == e.objectRef.get()) {
+                        if (proxy == e.proxyRef.get()) return;
+                        // entry exists, proxy doesn't match. replace.
+                        // this could happen if old proxy was gc'ed
+                        // TODO: raise exception if stored proxy is non-null? (not gc'ed)
+                        remove(table, hash, e);
+                        potentialNewSize--;
+                        break;
+                    }
+                }
+                e = new Entry<T>(object, hash, proxy, cache.referenceType, table[index], referenceQueue);
+                table[index] = e;
+                tableSize = potentialNewSize;
+                entryTable = table; // write-volatile
+            } finally {
+                unlock();
+            }
+        }
+
+        T getOrCreate(Object object, int hash, A allocator) {
+            Entry<T>[] table;
+            for (Entry<T> e = (table = entryTable)[hash & table.length - 1]; e != null; e = e.next) {
+                if (hash == e.hash && object == e.objectRef.get()) {
+                    return e.proxyRef.get();
+                }
+            }
+            lock();
+            try {
+                expunge();
+                int potentialNewSize;
+                if ((potentialNewSize = tableSize + 1) > threshold) {
+                    table = rehash(); // indirect read-/write- volatile
+                } else {
+                    table = entryTable; // read-volatile
+                }
+                int index;
+                Entry<T> e;
+                T proxy;
+                for (e = table[index = hash & (table.length - 1)]; e != null; e = e.next) {
+                    if (hash == e.hash && object == e.objectRef.get()) {
+                        if ((proxy = e.proxyRef.get()) != null) return proxy;
+                        // entry exists, proxy has been gc'ed. replace entry.
+                        remove(table, hash, e);
+                        potentialNewSize--;
+                        break;
+                    }
+                }
+                proxy = cache.allocateProxy(object, allocator);
+                e = new Entry<T>(object, hash, proxy, cache.referenceType, table[index], referenceQueue);
+                table[index] = e;
+                tableSize = potentialNewSize;
+                entryTable = table; // write-volatile
+                return proxy;
+            } finally {
+                unlock();
+            }
+        }
+        
+        T get(Object object, int hash) {
+            Entry<T>[] table;
+            for (Entry<T> e = (table = entryTable)[hash & table.length - 1]; e != null; e = e.next) {
+                if (hash == e.hash && object == e.objectRef.get()) {
+                    return e.proxyRef.get();
+                }
+            }
+            return null;
+        }
+
+        @SuppressWarnings("unchecked")
+        static final <T,A> Segment<T,A>[] newArray(int size) {
+            return new Segment[size];
+        }
+    }
+}
