diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index 06dc7bd46b..d59c8b1b16 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -1,1062 +1,1042 @@
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
 
 import java.io.IOException;
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
+import java.util.Arrays;
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
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.RubyClassPathVariable;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.RubyTime;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.util.ByteList;
 import org.jruby.util.ClassProvider;
 
 public class Java implements Library {
     public void load(Ruby runtime, boolean wrap) throws IOException {
         createJavaModule(runtime);
         runtime.getLoadService().smartLoad("builtin/javasupport");
         RubyClassPathVariable.createClassPathVariable(runtime);
     }
     
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
         javaUtils.defineFastModuleFunction("is_primitive_type", callbackFactory.getFastSingletonMethod("is_primitive_type",IRubyObject.class));
         javaUtils.defineFastModuleFunction("create_proxy_class", callbackFactory.getFastSingletonMethod("create_proxy_class",IRubyObject.class,IRubyObject.class,IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_java_class", callbackFactory.getFastSingletonMethod("get_java_class",IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_top_level_proxy_or_package", callbackFactory.getFastSingletonMethod("get_top_level_proxy_or_package",IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_proxy_or_package_under_package", callbackFactory.getFastSingletonMethod("get_proxy_or_package_under_package", IRubyObject.class, IRubyObject.class));
 
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
         public RubyClass defineClassUnder(RubyModule pkg, String name, RubyClass superClazz) {
             // shouldn't happen, but if a superclass is specified, it's not ours
             if (superClazz != null) {
                 return null;
             }
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) return null;
 
             Ruby runtime = pkg.getRuntime();
             return (RubyClass)get_proxy_class(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
         
         public RubyModule defineModuleUnder(RubyModule pkg, String name) {
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) return null;
 
             Ruby runtime = pkg.getRuntime();
             return (RubyModule)get_interface_module(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
     };
         
     private static final Map<String,Boolean> JAVA_PRIMITIVES = new HashMap<String,Boolean>();
     static {
         String[] primitives = {"boolean", "byte", "char", "short", "int", "long", "float", "double"};
         for (String primitive : primitives) JAVA_PRIMITIVES.put(primitive, Boolean.TRUE);
     }
 
     public static IRubyObject is_primitive_type(IRubyObject recv, IRubyObject sym) {
         return recv.getRuntime().newBoolean(JAVA_PRIMITIVES.containsKey(sym.asJavaString()));
     }
 
     public static IRubyObject create_proxy_class(
             IRubyObject recv,
             IRubyObject constant,
             IRubyObject javaClass,
             IRubyObject module) {
         if (!(module instanceof RubyModule)) throw recv.getRuntime().newTypeError(module, recv.getRuntime().getModule());
         return ((RubyModule)module).const_set(constant, get_proxy_class(recv, javaClass));
     }
 
     public static IRubyObject get_java_class(IRubyObject recv, IRubyObject name) {
         try {
             return JavaClass.for_name(recv, name);
         } catch (Exception e) {
             return recv.getRuntime().getNil();
         }
     }
 
     /**
      * Returns a new proxy instance of type (RubyClass)recv for the wrapped java_object,
      * or the cached proxy if we've already seen this object.
      * 
      * @param recv the class for this object
      * @param java_object the java object wrapped in a JavaObject wrapper
      * @return the new or cached proxy for the specified Java object
      */
     public static IRubyObject new_instance_for(IRubyObject recv, IRubyObject java_object) {
         // FIXME: note temporary double-allocation of JavaObject as we move to cleaner interface
         if (java_object instanceof JavaObject) {
             return getInstance(((JavaObject)java_object).getValue(), (RubyClass)recv);
         }
         // in theory we should never get here, keeping around temporarily
         IRubyObject new_instance = ((RubyClass)recv).allocate();
         new_instance.getInstanceVariables().fastSetInstanceVariable("@java_object",java_object);
         return new_instance;
     }
 
     /**
      * Returns a new proxy instance of type clazz for rawJavaObject, or the cached
      * proxy if we've already seen this object.
      * 
      * @param rawJavaObject
      * @param clazz
      * @return the new or cached proxy for the specified Java object
      */
     public static IRubyObject getInstance(Object rawJavaObject, RubyClass clazz) {
         return clazz.getRuntime().getJavaSupport().getObjectProxyCache()
             .getOrCreate(rawJavaObject, clazz);
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
     public static IRubyObject add_proxy_extender(IRubyObject recv, IRubyObject extender) {
         // hacky workaround in case any users call this directly.
         // most will have called JavaUtilities.extend_proxy instead.
         recv.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "JavaUtilities.add_proxy_extender is deprecated - use JavaUtilities.extend_proxy instead", "add_proxy_extender", "JavaUtilities.extend_proxy");
         IRubyObject javaClassVar = extender.getInstanceVariables().fastGetInstanceVariable("@java_class");
         if (!(javaClassVar instanceof JavaClass)) {
             throw recv.getRuntime().newArgumentError("extender does not have a valid @java_class");
         }
         ((JavaClass)javaClassVar).addProxyExtender(extender);
         return recv.getRuntime().getNil();
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
                 interfaceModule = (RubyModule)runtime.getJavaSupport().getJavaInterfaceTemplate().dup();
                 interfaceModule.fastSetInstanceVariable("@java_class",javaClass);
                 addToJavaPackageModule(interfaceModule,javaClass);
                 javaClass.setupInterfaceModule(interfaceModule);
                 // include any interfaces we extend
                 Class[] extended = javaClass.javaClass().getInterfaces();
                 for (int i = extended.length; --i >= 0; ) {
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
     
     public static IRubyObject get_interface_module(IRubyObject recv, IRubyObject javaClassObject) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, javaClassObject);
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass)javaClassObject;
         } else  {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         return getInterfaceModule(runtime, javaClass);
     }
 
     // Note: this isn't really all that deprecated, as it is used for
     // internal purposes, at least for now. But users should be discouraged
     // from calling this directly; eventually it will go away.
     public static IRubyObject get_deprecated_interface_proxy(IRubyObject recv, IRubyObject javaClassObject) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
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
                 RubyModule interfaceModule = getInterfaceModule(runtime, javaClass);
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
     
     public static RubyModule getProxyClass(Ruby runtime, JavaClass javaClass) {
         RubyClass proxyClass;
         Class c;
         if ((c = javaClass.javaClass()).isInterface()) {
             return getInterfaceModule(runtime, javaClass);
         }
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         javaClass.lockProxy();
         try {
             if((proxyClass = javaClass.getProxyClass()) == null) {
 
                 if(c.isArray()) {
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
                     proxyClass.getMetaClass().defineFastMethod("inherited",
                             runtime.getJavaSupport().getConcreteProxyCallback());
                     addToJavaPackageModule(proxyClass, javaClass);
 
                 } else {
                     // other java proxy classes added under their superclass' java proxy
                     proxyClass = createProxyClass(runtime,
                             (RubyClass)getProxyClass(runtime, JavaClass.get(runtime, c.getSuperclass())),
                             javaClass, false);
 
                     // include interface modules into the proxy class
                     Class[] interfaces = c.getInterfaces();
                     for (int i = interfaces.length; --i >= 0; ) {
                         JavaClass ifc = JavaClass.get(runtime, interfaces[i]);
                         proxyClass.includeModule(getInterfaceModule(runtime, ifc));
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
 
     public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject java_class_object) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
         if (java_class_object instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, java_class_object);
         } else if (java_class_object instanceof JavaClass) {
             javaClass = (JavaClass)java_class_object;
         } else  {
             throw runtime.newTypeError(java_class_object, runtime.getJavaSupport().getJavaClassClass());
         }
         return getProxyClass(runtime, javaClass);
     }
 
     private static RubyClass createProxyClass(Ruby runtime, RubyClass baseType,
             JavaClass javaClass, boolean invokeInherited) {
         // this needs to be split, since conditional calling #inherited doesn't fit standard ruby semantics
         RubyClass.checkInheritable(baseType);
         RubyClass superClass = (RubyClass)baseType;
         RubyClass proxyClass = RubyClass.newClass(runtime, superClass);
         proxyClass.makeMetaClass(superClass.getMetaClass());
         proxyClass.setAllocator(superClass.getAllocator());
         if (invokeInherited) proxyClass.inherit(superClass);
 
         proxyClass.callMethod(runtime.getCurrentContext(), "java_class=", javaClass);
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
 
     private static RubyModule createPackageModule(RubyModule parent, String name, String packageString) {
         Ruby runtime = parent.getRuntime();
         RubyModule packageModule = (RubyModule)runtime.getJavaSupport()
                 .getPackageModuleTemplate().dup();
         packageModule.fastSetInstanceVariable("@package_name",runtime.newString(
                 packageString.length() > 0 ? packageString + '.' : packageString));
 
         // this is where we'll get connected when classes are opened using
         // package module syntax.
         packageModule.addClassProvider(JAVA_PACKAGE_CLASS_PROVIDER);
 
         parent.const_set(runtime.newSymbol(name), packageModule);
         MetaClass metaClass = (MetaClass)packageModule.getMetaClass();
         metaClass.setAttached(packageModule);
         return packageModule;
     }
     
     private static final Pattern CAMEL_CASE_PACKAGE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");
 
     public static RubyModule getPackageModule(Ruby runtime, String name) {
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject value;
         if ((value = javaModule.getConstantAt(name)) instanceof RubyModule) {
             return (RubyModule)value;
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
     
     public static RubyModule getProxyOrPackageUnderPackage(final Ruby runtime, RubyModule parentPackage, String sym) {
         IRubyObject packageNameObj = parentPackage.fastGetInstanceVariable("@package_name");
         if (packageNameObj == null) throw runtime.newArgumentError("invalid package module");
         String packageName = packageNameObj.asJavaString();
         final String name = sym.trim().intern();
         if (name.length() == 0) throw runtime.newArgumentError("empty class or package name");
         String fullName = packageName + name;
         if (Character.isLowerCase(name.charAt(0))) {
             // TODO: should check against all Java reserved names here, not just primitives
             if (JAVA_PRIMITIVES.containsKey(name)) throw runtime.newArgumentError("illegal package name component: " + name);
             // this covers the rare case of lower-case class names (and thus will
             // fail 99.999% of the time). fortunately, we'll only do this once per
             // package name. (and seriously, folks, look into best practices...)
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, fullName));
             } catch (RaiseException re) { /* expected */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* expected */ }
             
             RubyModule packageModule;
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if ((packageModule = getJavaPackageModule(runtime, fullName)) == null) return null;
             // save package module as ivar in parent, and add method to parent so
             // we don't have to come back here.
             final String ivarName = ("@__pkg__" + name).intern();
             parentPackage.fastSetInstanceVariable(ivarName, packageModule);
             RubyClass singleton = parentPackage.getSingletonClass();
             singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, Visibility.PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) Arity.raiseArgumentError(runtime, args.length, 0, 0);
                     IRubyObject variable;
                     if ((variable = ((RubyModule)self).fastGetInstanceVariable(ivarName)) != null) {
                         return variable;
                     }
                     return runtime.getNil();
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             return packageModule;
         }  else {
             // upper case name, so most likely a class
             return getProxyClass(runtime, JavaClass.forNameVerbose(runtime, fullName));
 
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
             IRubyObject recv,
             IRubyObject parentPackage,
             IRubyObject sym) {
         Ruby runtime = recv.getRuntime();
         if (!(parentPackage instanceof RubyModule)) throw runtime.newTypeError(parentPackage, runtime.getModule());
         RubyModule result;
         if ((result = getProxyOrPackageUnderPackage(runtime, 
                 (RubyModule)parentPackage, sym.asJavaString())) != null) {
             return result;
         }
         return runtime.getNil();
     }
     
     
     public static RubyModule getTopLevelProxyOrPackage(final Ruby runtime, String sym) {
         final String name = sym.trim().intern();
         if (name.length() == 0) throw runtime.newArgumentError("empty class or package name");
         if (Character.isLowerCase(name.charAt(0))) {
             // this covers primitives and (unlikely) lower-case class names
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not primitive or lc class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not primitive or lc class */ }
             
             // TODO: check for Java reserved names and raise exception if encountered
             
             RubyModule packageModule;
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if ((packageModule = getJavaPackageModule(runtime, name)) == null) return null;
             RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
             if (javaModule.getMetaClass().isMethodBound(name, false)) return packageModule;
             // save package module as ivar in parent, and add method to parent so
             // we don't have to come back here.
             final String ivarName = ("@__pkg__" + name).intern();
             javaModule.fastSetInstanceVariable(ivarName, packageModule);
             RubyClass singleton = javaModule.getSingletonClass();
             singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, Visibility.PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) Arity.raiseArgumentError(runtime, args.length, 0, 0);
                     IRubyObject variable;
                     if ((variable = ((RubyModule)self).fastGetInstanceVariable(ivarName)) != null) {
                         return variable;
                     }
                     return runtime.getNil();
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             return packageModule;
         }  else {
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not a class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not a class */ }
 
             // upper-case package name
             // TODO: top-level upper-case package was supported in the previous (Ruby-based)
             // implementation, so leaving as is.  see note at #getProxyOrPackageUnderPackage
             // re: future approach below the top-level.
             return getPackageModule(runtime, name);
         }
     }
     
     public static IRubyObject get_top_level_proxy_or_package(IRubyObject recv, IRubyObject sym) {
         Ruby runtime = recv.getRuntime();
         RubyModule result;
         if ((result = getTopLevelProxyOrPackage(runtime, sym.asJavaString())) != null) {
             return result;
         }
         return runtime.getNil();
     }
     
     public static IRubyObject matching_method(IRubyObject recv, IRubyObject methods, IRubyObject args) {
         Map matchCache = recv.getRuntime().getJavaSupport().getMatchCache();
 
-        List arg_types = new ArrayList();
+        List<Class<?>> arg_types = new ArrayList<Class<?>>();
         int alen = ((RubyArray)args).getLength();
         IRubyObject[] aargs = ((RubyArray)args).toJavaArrayMaybeUnsafe();
         for(int i=0;i<alen;i++) {
             if (aargs[i] instanceof JavaObject) {
-                arg_types.add(((JavaClass)((JavaObject)aargs[i]).java_class()).getValue());
+                arg_types.add(((JavaClass)((JavaObject)aargs[i]).java_class()).javaClass());
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
-                List types = null;
                 IRubyObject method = margs[k];
-                if(method instanceof JavaCallable) {
-                    types = java.util.Arrays.asList(((JavaCallable)method).parameterTypes());
-                } else if(method instanceof JavaProxyMethod) {
-                    types = java.util.Arrays.asList(((JavaProxyMethod)method).getParameterTypes());
-                } else if(method instanceof JavaProxyConstructor) {
-                    types = java.util.Arrays.asList(((JavaProxyConstructor)method).getParameterTypes());
-                }
+                List<Class<?>> types = Arrays.asList(((ParameterTypes)method).getParameterTypes());
 
                 // Compatible (by inheritance)
                 if(arg_types.size() == types.size()) {
                     // Exact match
                     if(types.equals(arg_types)) {
                         ms.put(arg_types, method);
                         return method;
                     }
 
                     boolean match = true;
                     for(int j=0; j<types.size(); j++) {
-                        if(!(JavaClass.assignable((Class)types.get(j),(Class)arg_types.get(j)) &&
+                        if(!(JavaClass.assignable(types.get(j), arg_types.get(j)) &&
                              (i > 0 || primitive_match(types.get(j),arg_types.get(j))))
-                           && !JavaUtil.isDuckTypeConvertable((Class)arg_types.get(j), (Class)types.get(j))) {
+                           && !JavaUtil.isDuckTypeConvertable(arg_types.get(j), types.get(j))) {
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
 
-        List arg_types = new ArrayList();
+        List<Class<?>> arg_types = new ArrayList<Class<?>>();
         int aend = start+len;
 
         for(int i=start;i<aend;i++) {
             if (args[i] instanceof JavaObject) {
-                arg_types.add(((JavaClass)((JavaObject)args[i]).java_class()).getValue());
+                arg_types.add(((JavaClass)((JavaObject)args[i]).java_class()).javaClass());
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
-            Class[] types = null;
             IRubyObject method = margs[k];
-            if(method instanceof JavaCallable) {
-                types = ((JavaCallable)method).parameterTypes();
-            } else if(method instanceof JavaProxyMethod) {
-                types = ((JavaProxyMethod)method).getParameterTypes();
-            } else if(method instanceof JavaProxyConstructor) {
-                types = ((JavaProxyConstructor)method).getParameterTypes();
-            }
+            Class<?>[] types = ((ParameterTypes)method).getParameterTypes();
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
-                         JavaClass.assignable(types[j],(Class)arg_types.get(j)) &&
+                         JavaClass.assignable(types[j], arg_types.get(j)) &&
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
-            Class[] types = null;
             IRubyObject method = margs[k];
-            if(method instanceof JavaCallable) {
-                types = ((JavaCallable)method).parameterTypes();
-            } else if(method instanceof JavaProxyMethod) {
-                types = ((JavaProxyMethod)method).getParameterTypes();
-            } else if(method instanceof JavaProxyConstructor) {
-                types = ((JavaProxyConstructor)method).getParameterTypes();
-            }
+            Class<?>[] types = ((ParameterTypes)method).getParameterTypes();
             // Compatible (by inheritance)
             if(len == types.length) {
                 for(int j=0,m=len; j<m; j++) {
-                    if(!JavaClass.assignable(types[j],(Class)arg_types.get(j)) 
-                        && !JavaUtil.isDuckTypeConvertable((Class)arg_types.get(j), types[j])) {
+                    if(!JavaClass.assignable(types[j], arg_types.get(j)) 
+                        && !JavaUtil.isDuckTypeConvertable(arg_types.get(j), types[j])) {
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
         return getInstance(recv.getRuntime(), ((JavaObject)java_object).getValue());
     }
     
     public static IRubyObject wrap(Ruby runtime, IRubyObject java_object) {
         return getInstance(runtime, ((JavaObject)java_object).getValue());
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
             return JavaUtil.convertJavaToUsableRubyObject(recv.getRuntime(), ((JavaObject)object).getValue());
         }
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
             if (result instanceof JavaObject) {
                 recv.getRuntime().getJavaSupport().getObjectProxyCache()
                     .put(((JavaObject)result).getValue(), object);
             }
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
diff --git a/src/org/jruby/javasupport/JavaAccessibleObject.java b/src/org/jruby/javasupport/JavaAccessibleObject.java
index c8f8c167aa..08ecb07844 100644
--- a/src/org/jruby/javasupport/JavaAccessibleObject.java
+++ b/src/org/jruby/javasupport/JavaAccessibleObject.java
@@ -1,90 +1,161 @@
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
 
 import java.lang.reflect.AccessibleObject;
+import java.lang.reflect.Member;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyObject;
+import org.jruby.RubyString;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public abstract class JavaAccessibleObject extends RubyObject {
 
 	protected JavaAccessibleObject(Ruby runtime, RubyClass rubyClass) {
 		super(runtime, rubyClass);
 	}
 
 	public static void registerRubyMethods(Ruby runtime, RubyClass result) {
         CallbackFactory callbackFactory = runtime.callbackFactory(JavaAccessibleObject.class);
 
         result.defineFastMethod("==", callbackFactory.getFastMethod("op_equal", IRubyObject.class));
         result.defineFastMethod("eql?", callbackFactory.getFastMethod("op_equal", IRubyObject.class));
         result.defineFastMethod("equal?", callbackFactory.getFastMethod("same", IRubyObject.class));
         result.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
 
         result.defineFastMethod("accessible?", callbackFactory.getFastMethod("isAccessible"));
         result.defineFastMethod("accessible=", callbackFactory.getFastMethod("setAccessible", IRubyObject.class));
+        result.defineFastMethod("annotations", callbackFactory.getFastMethod("annotations"));
+        result.defineFastMethod("annotations?", callbackFactory.getFastMethod("annotations_p"));
+        result.defineFastMethod("declared_annotations?", callbackFactory.getFastMethod("declared_annotations_p"));
+        result.defineFastMethod("annotation", callbackFactory.getFastMethod("annotation", IRubyObject.class));
+        result.defineFastMethod("annotation_present?", callbackFactory.getFastMethod("annotation_present_p", IRubyObject.class));
+        result.defineFastMethod("declaring_class", callbackFactory.getFastMethod("declaring_class"));
+        result.defineFastMethod("modifiers", callbackFactory.getFastMethod("modifiers"));
+        result.defineFastMethod("name", callbackFactory.getFastMethod("name"));
+        result.defineFastMethod("synthetic?", callbackFactory.getFastMethod("synthetic_p"));
+        result.defineFastMethod("to_string", callbackFactory.getFastMethod("to_string"));
+        result.defineFastMethod("to_s", callbackFactory.getFastMethod("to_string"));
 	}
 	protected abstract AccessibleObject accessibleObject();
 
     public boolean equals(Object other) {
         return other instanceof JavaAccessibleObject &&
             this.accessibleObject() == ((JavaAccessibleObject)other).accessibleObject();
     }
     
     public int hashCode() {
         return this.accessibleObject().hashCode();
     }
 
 	public RubyFixnum hash() {
 		return getRuntime().newFixnum(hashCode());
     }
 
     public IRubyObject op_equal(IRubyObject other) {
 		return other instanceof JavaAccessibleObject && accessibleObject().equals(((JavaAccessibleObject)other).accessibleObject()) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
    
 	public IRubyObject same(IRubyObject other) {
         return getRuntime().newBoolean(equals(other));
 	}
        
 	public RubyBoolean isAccessible() {
 		return new RubyBoolean(getRuntime(),accessibleObject().isAccessible());
 	}
 
 	public IRubyObject setAccessible(IRubyObject object) {
 	    accessibleObject().setAccessible(object.isTrue());
 		return object;
 	}
+	
+    @SuppressWarnings("unchecked")
+    public IRubyObject annotation(IRubyObject annoClass) {
+        if (!(annoClass instanceof JavaClass)) {
+            throw getRuntime().newTypeError(annoClass, getRuntime().getJavaSupport().getJavaClassClass());
+        }
+        return Java.getInstance(getRuntime(), accessibleObject().getAnnotation(((JavaClass)annoClass).javaClass()));
+    }
+
+    public IRubyObject annotations() {
+        return Java.getInstance(getRuntime(), accessibleObject().getAnnotations());
+    }
+    
+    public RubyBoolean annotations_p() {
+        return getRuntime().newBoolean(accessibleObject().getAnnotations().length > 0);
+    }
+    
+    public IRubyObject declared_annotations() {
+        return Java.getInstance(getRuntime(), accessibleObject().getDeclaredAnnotations());
+    }
+    
+    public RubyBoolean declared_annotations_p() {
+        return getRuntime().newBoolean(accessibleObject().getDeclaredAnnotations().length > 0);
+    }
+    
+    public IRubyObject annotation_present_p(IRubyObject annoClass) {
+        if (!(annoClass instanceof JavaClass)) {
+            throw getRuntime().newTypeError(annoClass, getRuntime().getJavaSupport().getJavaClassClass());
+        }
+        return getRuntime().newBoolean(this.accessibleObject().isAnnotationPresent(((JavaClass)annoClass).javaClass()));
+    }
+
+    // for our purposes, Accessibles are also Members, and vice-versa,
+    // so we'll include Member methods here.
+
+    public IRubyObject declaring_class() {
+        Class<?> clazz = ((Member)accessibleObject()).getDeclaringClass();
+        if (clazz != null) {
+            return JavaClass.get(getRuntime(), clazz);
+        }
+        return getRuntime().getNil();
+    }
+
+    public IRubyObject modifiers() {
+        return getRuntime().newFixnum(((Member)accessibleObject()).getModifiers());
+    }
+
+    public IRubyObject name() {
+        return getRuntime().newString(((Member)accessibleObject()).getName());
+    }
+
+    public IRubyObject synthetic_p() {
+        return getRuntime().newBoolean(((Member)accessibleObject()).isSynthetic());
+    }
+    
+    public RubyString to_string() {
+        return getRuntime().newString(accessibleObject().toString());
+    }
 
 }
diff --git a/src/org/jruby/javasupport/JavaCallable.java b/src/org/jruby/javasupport/JavaCallable.java
index 4c07d638d1..63e91c8aa5 100644
--- a/src/org/jruby/javasupport/JavaCallable.java
+++ b/src/org/jruby/javasupport/JavaCallable.java
@@ -1,97 +1,140 @@
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
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 
+import java.lang.annotation.Annotation;
 import java.lang.reflect.Modifier;
+import java.lang.reflect.Type;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
+import org.jruby.RubyString;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.builtin.IRubyObject;
 
-public abstract class JavaCallable extends JavaAccessibleObject {
+public abstract class JavaCallable extends JavaAccessibleObject implements ParameterTypes {
 
     public JavaCallable(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
 
+    public static void registerRubyMethods(Ruby runtime, RubyClass result) {
+        
+        CallbackFactory callbackFactory = runtime.callbackFactory(JavaCallable.class);
+
+        result.defineFastMethod("public?",  callbackFactory.getFastMethod("public_p"));
+        result.defineFastMethod("arity", callbackFactory.getFastMethod("arity"));
+        result.defineFastMethod("argument_types", callbackFactory.getFastMethod("argument_types"));
+        result.defineFastMethod("parameter_types", callbackFactory.getFastMethod("parameter_types"));
+        result.defineFastMethod("exception_types", callbackFactory.getFastMethod("exception_types"));
+        result.defineFastMethod("generic_parameter_types", callbackFactory.getFastMethod("generic_parameter_types"));
+        result.defineFastMethod("generic_exception_types", callbackFactory.getFastMethod("generic_exception_types"));
+        result.defineFastMethod("parameter_annotations", callbackFactory.getFastMethod("parameter_annotations"));
+        result.defineFastMethod("varargs?", callbackFactory.getFastMethod("varargs_p"));
+        result.defineFastMethod("to_generic_string", callbackFactory.getFastMethod("to_generic_string"));
+        result.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
+    }
+
+    public abstract int getArity();
+    public abstract int getModifiers();
+    public abstract Class<?>[] getParameterTypes();
+    public abstract Class<?>[] getExceptionTypes();
+    public abstract Type[] getGenericExceptionTypes();
+    public abstract Type[] getGenericParameterTypes();
+    public abstract Annotation[][] getParameterAnnotations();
+    public abstract boolean isVarArgs();
+    public abstract String toGenericString();
+    /**
+     * @return the name used in the head of the string returned from inspect()
+     */
+    protected abstract String nameOnInspection();
+
     public final RubyFixnum arity() {
         return getRuntime().newFixnum(getArity());
     }
 
     public final RubyArray argument_types() {
-        Class[] parameterTypes = parameterTypes();
-        RubyArray result = getRuntime().newArray(parameterTypes.length);
-        for (int i = 0; i < parameterTypes.length; i++) {
-            result.append(JavaClass.get(getRuntime(),  parameterTypes[i]));
-        }
-        return result;
+        return JavaClass.getRubyArray(getRuntime(), getParameterTypes());
+    }
+
+    // same as argument_types, but matches name in java.lang.reflect.Constructor/Method
+    public IRubyObject parameter_types() {
+        return JavaClass.getRubyArray(getRuntime(), getParameterTypes());
+    }
+
+    public IRubyObject exception_types() {
+        return JavaClass.getRubyArray(getRuntime(), getExceptionTypes());
+    }
+
+    public IRubyObject generic_parameter_types() {
+        return Java.getInstance(getRuntime(), getGenericParameterTypes());
+    }
+
+    public IRubyObject generic_exception_types() {
+        return Java.getInstance(getRuntime(), getGenericExceptionTypes());
+    }
+
+    public IRubyObject parameter_annotations() {
+        return Java.getInstance(getRuntime(), getParameterAnnotations());
+    }
+    
+    public RubyBoolean varargs_p() {
+        return getRuntime().newBoolean(isVarArgs());
+    }
+    
+    public RubyString to_generic_string() {
+        return getRuntime().newString(toGenericString());
     }
 
     public IRubyObject inspect() {
         StringBuffer result = new StringBuffer();
         result.append(nameOnInspection());
-        Class[] parameterTypes = parameterTypes();
+        Class<?>[] parameterTypes = getParameterTypes();
         for (int i = 0; i < parameterTypes.length; i++) {
             result.append(parameterTypes[i].getName());
             if (i < parameterTypes.length - 1) {
                 result.append(',');
             }
         }
         result.append(")>");
         return getRuntime().newString(result.toString());
     }
 
-    protected abstract int getArity();
-    protected abstract Class[] parameterTypes();
-    protected abstract int getModifiers();
-
-    /**
-     * @return the name used in the head of the string returned from inspect()
-     */
-    protected abstract String nameOnInspection();
 
     public RubyBoolean public_p() {
         return RubyBoolean.newBoolean(getRuntime(), Modifier.isPublic(getModifiers()));
     }
 
 
-    public static void registerRubyMethods(Ruby runtime, RubyClass result, Class klass) {
-        registerRubyMethods(runtime, result);
-        
-        CallbackFactory callbackFactory = runtime.callbackFactory(klass);
-
-        result.defineFastMethod("public?",  callbackFactory.getFastMethod("public_p"));
-    }
 }
diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index 99a08ad8ac..d1ce1bba7b 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,1386 +1,1605 @@
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
 
+import java.io.ByteArrayOutputStream;
+import java.io.InputStream;
+import java.io.IOException;
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
 import org.jruby.common.IRubyWarnings.ID;
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
+import org.jruby.util.ByteList;
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
     private static final Map<String, AssignedName> RESERVED_NAMES = new HashMap<String, AssignedName>();
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
     private static final Map<String, AssignedName> STATIC_RESERVED_NAMES = new HashMap<String, AssignedName>(RESERVED_NAMES);
     static {
         STATIC_RESERVED_NAMES.put("new", new AssignedName("new", AssignedName.RESERVED));
     }
     private static final Map<String, AssignedName> INSTANCE_RESERVED_NAMES = new HashMap<String, AssignedName>(RESERVED_NAMES);
 
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
         private List<Method> methods;
         protected List<String> aliases;
         protected JavaMethod javaMethod;
         protected IntHashMap javaMethods;
         protected IntHashMap matchingMethods;
         MethodCallback(){}
         MethodCallback(String name, int type) {
             super(name,type);
         }
 
         // called only by initializing thread; no synchronization required
-        void addMethod(Method method, Class javaClass) {
+        void addMethod(Method method, Class<?> javaClass) {
             if (methods == null) {
                 methods = new ArrayList<Method>();
             }
             methods.add(method);
             haveLocalMethod |= javaClass == method.getDeclaringClass();
         }
 
         // called only by initializing thread; no synchronization required
         void addAlias(String alias) {
             if (aliases == null) {
                 aliases = new ArrayList<String>();
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
                     for (Method method: methods) {
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
             List<Object> argTypes = new ArrayList<Object>(len - start);
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
                     for (String alias : aliases) {
                         singleton.defineAlias(alias, this.name);
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
                     for (String alias: aliases) {
                         proxy.defineAlias(alias, this.name);
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
     
     private Map<String, AssignedName> staticAssignedNames;
     private Map<String, AssignedName> instanceAssignedNames;
-    private Map staticCallbacks;
-    private Map instanceCallbacks;
+    private Map<String, NamedCallback> staticCallbacks;
+    private Map<String, NamedCallback> instanceCallbacks;
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
     
-    private JavaClass(Ruby runtime, Class javaClass) {
+    private JavaClass(Ruby runtime, Class<?> javaClass) {
         super(runtime, (RubyClass) runtime.getJavaSupport().getJavaClassClass(), javaClass);
         if (javaClass.isInterface()) {
             initializeInterface(javaClass);
         } else if (!(javaClass.isArray() || javaClass.isPrimitive())) {
             // TODO: public only?
             initializeClass(javaClass);
         }
     }
     
     public boolean equals(Object other) {
         return other instanceof JavaClass &&
             this.getValue() == ((JavaClass)other).getValue();
     }
     
-    private void initializeInterface(Class javaClass) {
+    private void initializeInterface(Class<?> javaClass) {
         Map<String, AssignedName> staticNames  = new HashMap<String, AssignedName>(STATIC_RESERVED_NAMES);
         List<ConstantField> constantFields = new ArrayList<ConstantField>(); 
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
 
-    private void initializeClass(Class javaClass) {
-        Class superclass = javaClass.getSuperclass();
+    private void initializeClass(Class<?> javaClass) {
+        Class<?> superclass = javaClass.getSuperclass();
         Map<String, AssignedName> staticNames;
         Map<String, AssignedName> instanceNames;
         if (superclass == null) {
             staticNames = new HashMap<String, AssignedName>();
             instanceNames = new HashMap<String, AssignedName>();
         } else {
             JavaClass superJavaClass = get(getRuntime(),superclass);
             staticNames = new HashMap<String, AssignedName>(superJavaClass.getStaticAssignedNames());
             instanceNames = new HashMap<String, AssignedName>(superJavaClass.getInstanceAssignedNames());
         }
         staticNames.putAll(STATIC_RESERVED_NAMES);
         instanceNames.putAll(INSTANCE_RESERVED_NAMES);
-        Map staticCallbacks = new HashMap();
-        Map instanceCallbacks = new HashMap();
+        Map<String, NamedCallback> staticCallbacks = new HashMap<String, NamedCallback>();
+        Map<String, NamedCallback> instanceCallbacks = new HashMap<String, NamedCallback>();
         List<ConstantField> constantFields = new ArrayList<ConstantField>(); 
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
-                AssignedName assignedName = (AssignedName)staticNames.get(name);
+                AssignedName assignedName = staticNames.get(name);
                 if (assignedName != null && assignedName.type < AssignedName.FIELD)
                     continue;
                 staticNames.put(name,new AssignedName(name,AssignedName.FIELD));
                 staticCallbacks.put(name,new StaticFieldGetter(name,field));
                 if (!Modifier.isFinal(modifiers)) {
                     String setName = name + '=';
                     staticCallbacks.put(setName,new StaticFieldSetter(setName,field));
                 }
             } else {
-                AssignedName assignedName = (AssignedName)instanceNames.get(name);
+                AssignedName assignedName = instanceNames.get(name);
                 if (assignedName != null && assignedName.type < AssignedName.FIELD)
                     continue;
-                instanceNames.put(name,new AssignedName(name,AssignedName.FIELD));
-                instanceCallbacks.put(name,new InstanceFieldGetter(name,field));
+                instanceNames.put(name, new AssignedName(name,AssignedName.FIELD));
+                instanceCallbacks.put(name, new InstanceFieldGetter(name,field));
                 if (!Modifier.isFinal(modifiers)) {
                     String setName = name + '=';
-                    instanceCallbacks.put(setName,new InstanceFieldSetter(setName,field));
+                    instanceCallbacks.put(setName, new InstanceFieldSetter(setName,field));
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
-                AssignedName assignedName = (AssignedName)staticNames.get(name);
+                AssignedName assignedName = staticNames.get(name);
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
-                AssignedName assignedName = (AssignedName)instanceNames.get(name);
+                AssignedName assignedName = instanceNames.get(name);
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
-        final Class javaClass = javaClass();
+        final Class<?> javaClass = javaClass();
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
 
         for (ConstantField field: constantFields) {
             field.install(proxy);
         }
-        for (Iterator iter = staticCallbacks.values().iterator(); iter.hasNext(); ) {
-            NamedCallback callback = (NamedCallback)iter.next();
+        for (Iterator<NamedCallback> iter = staticCallbacks.values().iterator(); iter.hasNext(); ) {
+            NamedCallback callback = iter.next();
             if (callback.type == NamedCallback.STATIC_METHOD && callback.hasLocalMethod()) {
                 assignAliases((MethodCallback)callback,staticAssignedNames);
             }
             callback.install(proxy);
         }
-        for (Iterator iter = instanceCallbacks.values().iterator(); iter.hasNext(); ) {
-            NamedCallback callback = (NamedCallback)iter.next();
+        for (Iterator<NamedCallback> iter = instanceCallbacks.values().iterator(); iter.hasNext(); ) {
+            NamedCallback callback = iter.next();
             if (callback.type == NamedCallback.INSTANCE_METHOD && callback.hasLocalMethod()) {
                 assignAliases((MethodCallback)callback,instanceAssignedNames);
             }
             callback.install(proxy);
         }
         // setup constants for public inner classes
-        Class[] classes = javaClass.getClasses();
+        Class<?>[] classes = javaClass.getClasses();
         for (int i = classes.length; --i >= 0; ) {
             if (javaClass == classes[i].getDeclaringClass()) {
-                Class clazz = classes[i];
+                Class<?> clazz = classes[i];
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
 
     private static void assignAliases(MethodCallback callback, Map<String, AssignedName> assignedNames) {
         String name = callback.name;
         addUnassignedAlias(getRubyCasedName(name),assignedNames,callback);
         // logic adapted from java.beans.Introspector
         if (!(name.length() > 3 || name.startsWith("is")))
             return;
 
         String javaPropertyName = getJavaPropertyName(name);
         if (javaPropertyName == null)
             return; // not a Java property name, done with this method
 
         for (Method method: callback.methods) {
-            Class[] argTypes = method.getParameterTypes();
-            Class resultType = method.getReturnType();
+            Class<?>[] argTypes = method.getParameterTypes();
+            Class<?> resultType = method.getReturnType();
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
     
     private static void addUnassignedAlias(String name, Map<String, AssignedName> assignedNames,
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
-        final Class javaClass = javaClass();
+        Class<?> javaClass = javaClass();
         for (ConstantField field: constantFields) {
             field.install(module);
         }
         // setup constants for public inner classes
-        final Class[] classes = javaClass.getClasses();
+        Class<?>[] classes = javaClass.getClasses();
         for (int i = classes.length; --i >= 0; ) {
             if (javaClass == classes[i].getDeclaringClass()) {
-                Class clazz = classes[i];
+                Class<?> clazz = classes[i];
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
     
     public IRubyObject extend_proxy(IRubyObject extender) {
         addProxyExtender(extender);
         return getRuntime().getNil();
     }
     
-    public static JavaClass get(Ruby runtime, Class klass) {
+    public static JavaClass get(Ruby runtime, Class<?> klass) {
         JavaClass javaClass = runtime.getJavaSupport().getJavaClassFromCache(klass);
         if (javaClass == null) {
             javaClass = createJavaClass(runtime,klass);
         }
         return javaClass;
     }
+    
+    public static RubyArray getRubyArray(Ruby runtime, Class<?>[] classes) {
+        IRubyObject[] javaClasses = new IRubyObject[classes.length];
+        for (int i = classes.length; --i >= 0; ) {
+            javaClasses[i] = get(runtime, classes[i]);
+        }
+        return runtime.newArrayNoCopy(javaClasses);
+    }
 
-    private static synchronized JavaClass createJavaClass(final Ruby runtime, final Class klass) {
+    private static synchronized JavaClass createJavaClass(Ruby runtime, Class<?> klass) {
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
+        result.defineFastMethod("primitive?", 
+                callbackFactory.getFastMethod("primitive_p"));
+        result.defineFastMethod("enum?", 
+                callbackFactory.getFastMethod("enum_p"));
+        result.defineFastMethod("annotation?", 
+                callbackFactory.getFastMethod("annotation_p"));
+        result.defineFastMethod("anonymous_class?", 
+                callbackFactory.getFastMethod("anonymous_class_p"));
+        result.defineFastMethod("local_class?", 
+                callbackFactory.getFastMethod("local_class_p"));
+        result.defineFastMethod("member_class?", 
+                callbackFactory.getFastMethod("member_class_p"));
+        result.defineFastMethod("synthetic?", 
+                callbackFactory.getFastMethod("synthetic_p"));
+        result.defineFastMethod("package", 
+                callbackFactory.getFastMethod("get_package"));
         result.defineFastMethod("name", 
                 callbackFactory.getFastMethod("name"));
+        result.defineFastMethod("canonical_name", 
+                callbackFactory.getFastMethod("canonical_name"));
         result.defineFastMethod("class_loader", 
                 callbackFactory.getFastMethod("class_loader"));
         result.defineFastMethod("protection_domain", 
                 callbackFactory.getFastMethod("protection_domain"));
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
-        result.defineFastMethod("primitive?", 
-                callbackFactory.getFastMethod("primitive_p"));
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
+        result.defineFastMethod("classes", 
+                callbackFactory.getFastMethod("classes"));
         result.defineFastMethod("declared_classes", 
                 callbackFactory.getFastMethod("declared_classes"));
         result.defineFastMethod("declared_method", 
                 callbackFactory.getFastOptMethod("declared_method"));
-
+        result.defineFastMethod("resource", 
+                callbackFactory.getFastMethod("resource", IRubyObject.class));
+        result.defineFastMethod("resource_as_stream", 
+                callbackFactory.getFastMethod("resource_as_stream", IRubyObject.class));
+        result.defineFastMethod("resource_as_string", 
+                callbackFactory.getFastMethod("resource_as_string", IRubyObject.class));
+        result.defineFastMethod("declaring_class", 
+                callbackFactory.getFastMethod("declaring_class"));
+        result.defineFastMethod("enclosing_class", 
+                callbackFactory.getFastMethod("enclosing_class"));
+        result.defineFastMethod("enclosing_constructor", 
+                callbackFactory.getFastMethod("enclosing_constructor"));
+        result.defineFastMethod("enclosing_method", 
+                callbackFactory.getFastMethod("enclosing_method"));
+        result.defineFastMethod("enum_constants", 
+                callbackFactory.getFastMethod("enum_constants"));
+        result.defineFastMethod("generic_interfaces", 
+                callbackFactory.getFastMethod("generic_interfaces"));
+        result.defineFastMethod("generic_superclass", 
+                callbackFactory.getFastMethod("generic_superclass"));
+        result.defineFastMethod("type_parameters", 
+                callbackFactory.getFastMethod("type_parameters"));
+        result.defineFastMethod("signers", 
+                callbackFactory.getFastMethod("signers"));
+        result.defineFastMethod("annotations", 
+                callbackFactory.getFastMethod("annotations"));
+        result.defineFastMethod("annotations?", 
+                callbackFactory.getFastMethod("annotations_p"));
+        result.defineFastMethod("declared_annotations", 
+                callbackFactory.getFastMethod("declared_annotations"));
+        result.defineFastMethod("declared_annotations?", 
+                callbackFactory.getFastMethod("declared_annotations_p"));
+        result.defineFastMethod("annotation", 
+                callbackFactory.getFastMethod("annotation", IRubyObject.class));
         result.defineFastMethod("extend_proxy", 
                 callbackFactory.getFastMethod("extend_proxy", IRubyObject.class));
+        result.defineFastMethod("annotation_present?", 
+                callbackFactory.getFastMethod("annotation_present_p", IRubyObject.class));
+        result.defineFastMethod("modifiers", 
+                callbackFactory.getFastMethod("modifiers"));
+        result.defineFastMethod("ruby_class", 
+                callbackFactory.getFastMethod("ruby_class"));
 
         result.getMetaClass().undefineMethod("new");
         result.getMetaClass().undefineMethod("allocate");
 
         return result;
     }
     
     public static synchronized JavaClass forNameVerbose(Ruby runtime, String className) {
-        Class klass = runtime.getJavaSupport().loadJavaClassVerbose(className);
+        Class<?> klass = runtime.getJavaSupport().loadJavaClassVerbose(className);
         return JavaClass.get(runtime, klass);
     }
     
     public static synchronized JavaClass forNameQuiet(Ruby runtime, String className) {
         Class klass = runtime.getJavaSupport().loadJavaClassQuiet(className);
         return JavaClass.get(runtime, klass);
     }
 
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
 
+    public RubyModule ruby_class() {
+        // Java.getProxyClass deals with sync issues, so we won't duplicate the logic here
+        return Java.getProxyClass(getRuntime(), this);
+    }
+
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
     
+    public RubyBoolean enum_p() {
+        return getRuntime().newBoolean(javaClass().isEnum());
+    }
+    
+    public RubyBoolean annotation_p() {
+        return getRuntime().newBoolean(javaClass().isAnnotation());
+    }
+    
+    public RubyBoolean anonymous_class_p() {
+        return getRuntime().newBoolean(javaClass().isAnonymousClass());
+    }
+    
+    public RubyBoolean local_class_p() {
+        return getRuntime().newBoolean(javaClass().isLocalClass());
+    }
+    
+    public RubyBoolean member_class_p() {
+        return getRuntime().newBoolean(javaClass().isMemberClass());
+    }
+    
+    public IRubyObject synthetic_p() {
+        return getRuntime().newBoolean(javaClass().isSynthetic());
+    }
+
     public RubyString name() {
         return getRuntime().newString(javaClass().getName());
     }
 
+    public IRubyObject canonical_name() {
+        String canonicalName = javaClass().getCanonicalName();
+        if (canonicalName != null) {
+            return getRuntime().newString(canonicalName);
+        }
+        return getRuntime().getNil();
+    }
+    
+    public IRubyObject get_package() {
+        return Java.getInstance(getRuntime(), javaClass().getPackage());
+    }
+
     public IRubyObject class_loader() {
-        return Java.java_to_ruby(this, JavaObject.wrap(getRuntime(),javaClass().getClassLoader()), Block.NULL_BLOCK);
+        return Java.getInstance(getRuntime(), javaClass().getClassLoader());
     }
 
     public IRubyObject protection_domain() {
-        return Java.java_to_ruby(this, JavaObject.wrap(getRuntime(),javaClass().getProtectionDomain()), Block.NULL_BLOCK);
+        return Java.getInstance(getRuntime(), javaClass().getProtectionDomain());
+    }
+    
+    public IRubyObject resource(IRubyObject name) {
+        return Java.getInstance(getRuntime(), javaClass().getResource(name.asJavaString()));
+    }
+
+    public IRubyObject resource_as_stream(IRubyObject name) {
+        return Java.getInstance(getRuntime(), javaClass().getResourceAsStream(name.asJavaString()));
+    }
+    
+    public IRubyObject resource_as_string(IRubyObject name) {
+        InputStream in = javaClass().getResourceAsStream(name.asJavaString());
+        if (in == null) return getRuntime().getNil();
+        ByteArrayOutputStream out = new ByteArrayOutputStream();
+        try {
+            int len;
+            byte[] buf = new byte[4096];
+            while ((len = in.read(buf)) >= 0) {
+                out.write(buf, 0, len);
+            }
+        } catch (IOException e) {
+            throw getRuntime().newIOErrorFromException(e);
+        }
+        return getRuntime().newString(new ByteList(out.toByteArray(), false));
+    }
+    
+    @SuppressWarnings("unchecked")
+    public IRubyObject annotation(IRubyObject annoClass) {
+        if (!(annoClass instanceof JavaClass)) {
+            throw getRuntime().newTypeError(annoClass, getRuntime().getJavaSupport().getJavaClassClass());
+        }
+        return Java.getInstance(getRuntime(), javaClass().getAnnotation(((JavaClass)annoClass).javaClass()));
+    }
+    
+    public IRubyObject annotations() {
+        // note: intentionally returning the actual array returned from Java, rather
+        // than wrapping it in a RubyArray. wave of the future, when java_class will
+        // return the actual class, rather than a JavaClass wrapper.
+        return Java.getInstance(getRuntime(), javaClass().getAnnotations());
+    }
+    
+    public RubyBoolean annotations_p() {
+        return getRuntime().newBoolean(javaClass().getAnnotations().length > 0);
+    }
+    
+    public IRubyObject declared_annotations() {
+        // see note above re: return type
+        return Java.getInstance(getRuntime(), javaClass().getDeclaredAnnotations());
+    }
+    
+    public RubyBoolean declared_annotations_p() {
+        return getRuntime().newBoolean(javaClass().getDeclaredAnnotations().length > 0);
+    }
+    
+    @SuppressWarnings("unchecked")
+    public IRubyObject annotation_present_p(IRubyObject annoClass) {
+        if (!(annoClass instanceof JavaClass)) {
+            throw getRuntime().newTypeError(annoClass, getRuntime().getJavaSupport().getJavaClassClass());
+        }
+        return getRuntime().newBoolean(javaClass().isAnnotationPresent(((JavaClass)annoClass).javaClass()));
+    }
+    
+    public IRubyObject modifiers() {
+        return getRuntime().newFixnum(javaClass().getModifiers());
     }
 
-    private static String getSimpleName(Class class_) {
- 		if (class_.isArray()) {
- 			return getSimpleName(class_.getComponentType()) + "[]";
+    public IRubyObject declaring_class() {
+        Class<?> clazz = javaClass().getDeclaringClass();
+        if (clazz != null) {
+            return JavaClass.get(getRuntime(), clazz);
+        }
+        return getRuntime().getNil();
+    }
+
+    public IRubyObject enclosing_class() {
+        return Java.getInstance(getRuntime(), javaClass().getEnclosingClass());
+    }
+    
+    public IRubyObject enclosing_constructor() {
+        Constructor<?> ctor = javaClass().getEnclosingConstructor();
+        if (ctor != null) {
+            return new JavaConstructor(getRuntime(), ctor);
+        }
+        return getRuntime().getNil();
+    }
+
+    public IRubyObject enclosing_method() {
+        Method meth = javaClass().getEnclosingMethod();
+        if (meth != null) {
+            return new JavaMethod(getRuntime(), meth);
+        }
+        return getRuntime().getNil();
+    }
+
+    public IRubyObject enum_constants() {
+        return Java.getInstance(getRuntime(), javaClass().getEnumConstants());
+    }
+
+    public IRubyObject generic_interfaces() {
+        return Java.getInstance(getRuntime(), javaClass().getGenericInterfaces());
+    }
+    
+    public IRubyObject generic_superclass() {
+        return Java.getInstance(getRuntime(), javaClass().getGenericSuperclass());
+    }
+    
+    public IRubyObject type_parameters() {
+        return Java.getInstance(getRuntime(), javaClass().getTypeParameters());
+    }
+    
+    public IRubyObject signers() {
+        return Java.getInstance(getRuntime(), javaClass().getSigners());
+    }
+    
+    private static String getSimpleName(Class<?> clazz) {
+ 		if (clazz.isArray()) {
+ 			return getSimpleName(clazz.getComponentType()) + "[]";
  		}
  
- 		String className = class_.getName();
- 
+ 		String className = clazz.getName();
+ 		int len = className.length();
         int i = className.lastIndexOf('$');
  		if (i != -1) {
             do {
  				i++;
- 			} while (i < className.length() && Character.isDigit(className.charAt(i)));
+ 			} while (i < len && Character.isDigit(className.charAt(i)));
  			return className.substring(i);
  		}
  
  		return className.substring(className.lastIndexOf('.') + 1);
  	}
 
     public RubyString simple_name() {
         return getRuntime().newString(getSimpleName(javaClass()));
     }
 
     public IRubyObject superclass() {
-        Class superclass = javaClass().getSuperclass();
+        Class<?> superclass = javaClass().getSuperclass();
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
-        Class[] argumentTypes = buildArgumentTypes(args);
+        Class<?>[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.create(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     public JavaMethod declared_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asJavaString();
-        Class[] argumentTypes = buildArgumentTypes(args);
+        Class<?>[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.createDeclared(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
-    private Class[] buildArgumentTypes(IRubyObject[] args) throws ClassNotFoundException {
+    private Class<?>[] buildArgumentTypes(IRubyObject[] args) throws ClassNotFoundException {
         if (args.length < 1) {
             throw getRuntime().newArgumentError(args.length, 1);
         }
-        Class[] argumentTypes = new Class[args.length - 1];
+        Class<?>[] argumentTypes = new Class[args.length - 1];
         for (int i = 1; i < args.length; i++) {
             JavaClass type = for_name(this, args[i]);
             argumentTypes[i - 1] = type.javaClass();
         }
         return argumentTypes;
     }
 
     public RubyArray constructors() {
-        if (constructors == null) {
-            constructors = buildConstructors(javaClass().getConstructors());
-        }
-        return constructors;
+        RubyArray ctors;
+        if ((ctors = constructors) != null) return ctors;
+        return constructors = buildConstructors(javaClass().getConstructors());
     }
     
+    public RubyArray classes() {
+        return JavaClass.getRubyArray(getRuntime(), javaClass().getClasses());
+    }
+
     public RubyArray declared_classes() {
         Ruby runtime = getRuntime();
         RubyArray result = runtime.newArray();
-        Class javaClass = javaClass();
+        Class<?> javaClass = javaClass();
         try {
-            Class[] classes = javaClass.getDeclaredClasses();
+            Class<?>[] classes = javaClass.getDeclaredClasses();
             for (int i = 0; i < classes.length; i++) {
                 if (Modifier.isPublic(classes[i].getModifiers())) {
                     result.append(get(runtime, classes[i]));
                 }
             }
         } catch (SecurityException e) {
             // restrictive security policy; no matter, we only want public
             // classes anyway
             try {
-                Class[] classes = javaClass.getClasses();
+                Class<?>[] classes = javaClass.getClasses();
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
 
-    private RubyArray buildConstructors(Constructor[] constructors) {
+    private RubyArray buildConstructors(Constructor<?>[] constructors) {
         RubyArray result = getRuntime().newArray(constructors.length);
         for (int i = 0; i < constructors.length; i++) {
             result.append(new JavaConstructor(getRuntime(), constructors[i]));
         }
         return result;
     }
 
     public JavaConstructor constructor(IRubyObject[] args) {
         try {
-            Class[] parameterTypes = buildClassArgs(args);
-            Constructor constructor;
-            constructor = javaClass().getConstructor(parameterTypes);
+            Class<?>[] parameterTypes = buildClassArgs(args);
+            Constructor<?> constructor = javaClass().getConstructor(parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
             throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     public JavaConstructor declared_constructor(IRubyObject[] args) {
         try {
-            Class[] parameterTypes = buildClassArgs(args);
-            Constructor constructor;
-            constructor = javaClass().getDeclaredConstructor (parameterTypes);
+            Class<?>[] parameterTypes = buildClassArgs(args);
+            Constructor<?> constructor = javaClass().getDeclaredConstructor (parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
             throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
-    private Class[] buildClassArgs(IRubyObject[] args) {
-        Class[] parameterTypes = new Class[args.length];
-        for (int i = 0; i < args.length; i++) {
+    private Class<?>[] buildClassArgs(IRubyObject[] args) {
+        JavaSupport javaSupport = getRuntime().getJavaSupport();
+        Class<?>[] parameterTypes = new Class<?>[args.length];
+        for (int i = args.length; --i >= 0; ) {
             String name = args[i].asJavaString();
-            parameterTypes[i] = getRuntime().getJavaSupport().loadJavaClassVerbose(name);
+            parameterTypes[i] = javaSupport.loadJavaClassVerbose(name);
         }
         return parameterTypes;
     }
 
     public JavaClass array_class() {
         return JavaClass.get(getRuntime(), Array.newInstance(javaClass(), 0).getClass());
     }
    
     public JavaObject new_array(IRubyObject lengthArgument) {
         if (lengthArgument instanceof RubyInteger) {
             // one-dimensional array
-        int length = (int) ((RubyInteger) lengthArgument).getLongValue();
-        return new JavaArray(getRuntime(), Array.newInstance(javaClass(), length));
+            int length = (int) ((RubyInteger) lengthArgument).getLongValue();
+            return new JavaArray(getRuntime(), Array.newInstance(javaClass(), length));
         } else if (lengthArgument instanceof RubyArray) {
             // n-dimensional array
             List list = ((RubyArray)lengthArgument).getList();
             int length = list.size();
             if (length == 0) {
                 throw getRuntime().newArgumentError("empty dimensions specifier for java array");
-    }
+            }
             int[] dimensions = new int[length];
             for (int i = length; --i >= 0; ) {
                 IRubyObject dimensionLength = (IRubyObject)list.get(i);
                 if ( !(dimensionLength instanceof RubyInteger) ) {
                     throw getRuntime()
-                        .newTypeError(dimensionLength, getRuntime().getInteger());
+                    .newTypeError(dimensionLength, getRuntime().getInteger());
                 }
                 dimensions[i] = (int) ((RubyInteger) dimensionLength).getLongValue();
             }
             return new JavaArray(getRuntime(), Array.newInstance(javaClass(), dimensions));
         } else {
             throw getRuntime().newArgumentError(
-                  "invalid length or dimensions specifier for java array" +
-                  " - must be Integer or Array of Integer");
+                    "invalid length or dimensions specifier for java array" +
+            " - must be Integer or Array of Integer");
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
-        Class[] interfaces = javaClass().getInterfaces();
-        RubyArray result = getRuntime().newArray(interfaces.length);
-        for (int i = 0; i < interfaces.length; i++) {
-            result.append(JavaClass.get(getRuntime(), interfaces[i]));
-        }
-        return result;
+        return JavaClass.getRubyArray(getRuntime(), javaClass().getInterfaces());
     }
 
     public RubyBoolean primitive_p() {
         return getRuntime().newBoolean(isPrimitive());
     }
 
     public RubyBoolean assignable_from_p(IRubyObject other) {
         if (! (other instanceof JavaClass)) {
             throw getRuntime().newTypeError("assignable_from requires JavaClass (" + other.getType() + " given)");
         }
 
-        Class otherClass = ((JavaClass) other).javaClass();
+        Class<?> otherClass = ((JavaClass) other).javaClass();
         return assignable(javaClass(), otherClass) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
-    static boolean assignable(Class thisClass, Class otherClass) {
+    static boolean assignable(Class<?> thisClass, Class<?> otherClass) {
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
diff --git a/src/org/jruby/javasupport/JavaConstructor.java b/src/org/jruby/javasupport/JavaConstructor.java
index df0b709ac1..1923ec5574 100644
--- a/src/org/jruby/javasupport/JavaConstructor.java
+++ b/src/org/jruby/javasupport/JavaConstructor.java
@@ -1,131 +1,163 @@
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
 
+import java.lang.annotation.Annotation;
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
+import java.lang.reflect.Type;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaConstructor extends JavaCallable {
-    private final Constructor constructor;
-    private final Class[] parameterTypes;
+    private final Constructor<?> constructor;
+    private final Class<?>[] parameterTypes;
 
     public static RubyClass createJavaConstructorClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result =
                 javaModule.defineClassUnder("JavaConstructor", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(JavaConstructor.class);
 
-        JavaCallable.registerRubyMethods(runtime, result, JavaConstructor.class);
-        result.defineFastMethod("arity", callbackFactory.getFastMethod("arity"));
-        result.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
-        result.defineFastMethod("argument_types", callbackFactory.getFastMethod("argument_types"));
+        JavaAccessibleObject.registerRubyMethods(runtime, result);
+        JavaCallable.registerRubyMethods(runtime, result);
+
         result.defineFastMethod("new_instance", callbackFactory.getFastOptMethod("new_instance"));
+        result.defineFastMethod("type_parameters", callbackFactory.getFastMethod("type_parameters"));
         
         return result;
     }
 
-    public JavaConstructor(Ruby runtime, Constructor constructor) {
+    public JavaConstructor(Ruby runtime, Constructor<?> constructor) {
         super(runtime, runtime.getJavaSupport().getJavaModule().fastGetClass("JavaConstructor"));
         this.constructor = constructor;
         this.parameterTypes = constructor.getParameterTypes();
     }
 
     public boolean equals(Object other) {
         return other instanceof JavaConstructor &&
             this.constructor == ((JavaConstructor)other).constructor;
     }
     
     public int hashCode() {
         return constructor.hashCode();
     }
 
     public int getArity() {
         return parameterTypes.length;
     }
+    
+    protected String nameOnInspection() {
+        return getType().toString();
+    }
+
+    public Class<?>[] getParameterTypes() {
+        return parameterTypes;
+    }
+
+    public Class<?>[] getExceptionTypes() {
+        return constructor.getExceptionTypes();
+    }
+
+    public Type[] getGenericParameterTypes() {
+        return constructor.getGenericParameterTypes();
+    }
+
+    public Type[] getGenericExceptionTypes() {
+        return constructor.getGenericExceptionTypes();
+    }
+
+    public Annotation[][] getParameterAnnotations() {
+        return constructor.getParameterAnnotations();
+    }
+    
+    public boolean isVarArgs() {
+        return constructor.isVarArgs();
+    }
+
+    public int getModifiers() {
+        return constructor.getModifiers();
+    }
+    
+    public String toGenericString() {
+        return constructor.toGenericString();
+    }
+
+    protected AccessibleObject accessibleObject() {
+        return constructor;
+    }
+    
+    public IRubyObject type_parameters() {
+        return Java.getInstance(getRuntime(), constructor.getTypeParameters());
+    }
+
 
     public IRubyObject new_instance(IRubyObject[] args) {
-        if (args.length != getArity()) {
-            throw getRuntime().newArgumentError(args.length, getArity());
+        int length = args.length;
+        Class<?>[] types = parameterTypes;
+        if (length != types.length) {
+            throw getRuntime().newArgumentError(length, types.length);
         }
-        Object[] constructorArguments = new Object[args.length];
-        Class[] types = parameterTypes;
-        for (int i = 0; i < args.length; i++) {
+        Object[] constructorArguments = new Object[length];
+        for (int i = length; --i >= 0; ) {
             constructorArguments[i] = JavaUtil.convertArgument(args[i], types[i]);
         }
         try {
             Object result = constructor.newInstance(constructorArguments);
             return JavaObject.wrap(getRuntime(), result);
 
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError("expected " + argument_types().inspect() +
                                               ", got [" + constructorArguments[0].getClass().getName() + ", ...]");
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError("illegal access");
         } catch (InvocationTargetException ite) {
             getRuntime().getJavaSupport().handleNativeException(ite.getTargetException());
             // not reached
             assert false;
             return null;
         } catch (InstantiationException ie) {
             throw getRuntime().newTypeError("can't make instance of " + constructor.getDeclaringClass().getName());
         }
     }
-
-
-    protected String nameOnInspection() {
-        return getType().toString();
-    }
-
-    protected Class[] parameterTypes() {
-        return parameterTypes;
-    }
-
-    protected int getModifiers() {
-        return constructor.getModifiers();
-    }
-
-    protected AccessibleObject accessibleObject() {
-        return constructor;
-    }
+    
 }
diff --git a/src/org/jruby/javasupport/JavaField.java b/src/org/jruby/javasupport/JavaField.java
index 877f510b71..917480374c 100644
--- a/src/org/jruby/javasupport/JavaField.java
+++ b/src/org/jruby/javasupport/JavaField.java
@@ -1,199 +1,215 @@
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
 
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.Field;
 import java.lang.reflect.Modifier;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaField extends JavaAccessibleObject {
     private Field field;
 
     public static RubyClass createJavaFieldClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = javaModule.defineClassUnder("JavaField", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(JavaField.class);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
+
         result.defineFastMethod("value_type", callbackFactory.getFastMethod("value_type"));
         result.defineFastMethod("public?", callbackFactory.getFastMethod("public_p"));
         result.defineFastMethod("static?", callbackFactory.getFastMethod("static_p"));
         result.defineFastMethod("value", callbackFactory.getFastMethod("value", IRubyObject.class));
         result.defineFastMethod("set_value", callbackFactory.getFastMethod("set_value", IRubyObject.class, IRubyObject.class));
         result.defineFastMethod("set_static_value", callbackFactory.getFastMethod("set_static_value", IRubyObject.class));
         result.defineFastMethod("final?", callbackFactory.getFastMethod("final_p"));
         result.defineFastMethod("static_value", callbackFactory.getFastMethod("static_value"));
         result.defineFastMethod("name", callbackFactory.getFastMethod("name"));
         result.defineFastMethod("==", callbackFactory.getFastMethod("op_equal", IRubyObject.class));
         result.defineAlias("===", "==");
+        result.defineFastMethod("enum_constant?", callbackFactory.getFastMethod("enum_constant_p"));
+        result.defineFastMethod("to_generic_string", callbackFactory.getFastMethod("to_generic_string"));
+        result.defineFastMethod("type", callbackFactory.getFastMethod("field_type"));
 
         return result;
     }
 
     public JavaField(Ruby runtime, Field field) {
         super(runtime, (RubyClass) runtime.getJavaSupport().getJavaModule().fastGetClass("JavaField"));
         this.field = field;
     }
 
     public boolean equals(Object other) {
         return other instanceof JavaField &&
             this.field == ((JavaField)other).field;
     }
     
     public int hashCode() {
         return field.hashCode();
     }
 
     public RubyString value_type() {
         return getRuntime().newString(field.getType().getName());
     }
 
     public IRubyObject op_equal(IRubyObject other) {
     	if (!(other instanceof JavaField)) {
     		return getRuntime().getFalse();
     	}
     	
         return getRuntime().newBoolean(field.equals(((JavaField) other).field));
     }
 
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(field.getModifiers()));
     }
 
     public RubyBoolean static_p() {
         return getRuntime().newBoolean(Modifier.isStatic(field.getModifiers()));
     }
+    
+    public RubyBoolean enum_constant_p() {
+        return getRuntime().newBoolean(field.isEnumConstant());
+    }
+
+    public RubyString to_generic_string() {
+        return getRuntime().newString(field.toGenericString());
+    }
+    
+    public IRubyObject field_type() {
+        return JavaClass.get(getRuntime(), field.getType());
+    }
 
     public JavaObject value(IRubyObject object) {
         if (! (object instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object");
         }
         Object javaObject = ((JavaObject) object).getValue();
         try {
             return JavaObject.wrap(getRuntime(), field.get(javaObject));
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError("illegal access");
         }
     }
 
     public JavaObject set_value(IRubyObject object, IRubyObject value) {
         if (! (object instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object: " + object);
         }
         if (! (value instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object:" + value);
         }
         Object javaObject = ((JavaObject) object).getValue();
         try {
             Object convertedValue = JavaUtil.convertArgument(((JavaObject) value).getValue(),
                                                              field.getType());
 
             field.set(javaObject, convertedValue);
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError(
                                 "illegal access on setting variable: " + iae.getMessage());
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError(
                                 "wrong type for " + field.getType().getName() + ": " +
                                 ((JavaObject) value).getValue().getClass().getName());
         }
         return (JavaObject) value;
     }
 
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(field.getModifiers()));
     }
 
     public JavaObject static_value() {
         
         if (Ruby.isSecurityRestricted())
             return null;
         else {
             try {
                 // TODO: Only setAccessible to account for pattern found by
                 // accessing constants included from a non-public interface.
                 // (aka java.util.zip.ZipConstants being implemented by many
                 // classes)
                 field.setAccessible(true);
                 return JavaObject.wrap(getRuntime(), field.get(null));
             } catch (IllegalAccessException iae) {
                 throw getRuntime().newTypeError("illegal static value access: " + iae.getMessage());
             }
         }
     }
 
     public JavaObject set_static_value(IRubyObject value) {
         if (! (value instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object:" + value);
         }
         try {
             Object convertedValue = JavaUtil.convertArgument(((JavaObject) value).getValue(),
                                                              field.getType());
             // TODO: Only setAccessible to account for pattern found by
             // accessing constants included from a non-public interface.
             // (aka java.util.zip.ZipConstants being implemented by many
             // classes)
             // TODO: not sure we need this at all, since we only expose
             // public fields.
             //field.setAccessible(true);
             field.set(null, convertedValue);
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError(
                                 "illegal access on setting static variable: " + iae.getMessage());
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError(
                                 "wrong type for " + field.getType().getName() + ": " +
                                 ((JavaObject) value).getValue().getClass().getName());
         }
         return (JavaObject) value;
     }
     
     public RubyString name() {
         return getRuntime().newString(field.getName());
     }
     
     protected AccessibleObject accessibleObject() {
         return field;
     }
 }
diff --git a/src/org/jruby/javasupport/JavaMethod.java b/src/org/jruby/javasupport/JavaMethod.java
index 7ec3d9a19c..42aed92ac7 100644
--- a/src/org/jruby/javasupport/JavaMethod.java
+++ b/src/org/jruby/javasupport/JavaMethod.java
@@ -1,269 +1,302 @@
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
 
+import java.lang.annotation.Annotation;
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
+import java.lang.reflect.Type;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.javasupport.proxy.InternalJavaProxy;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaMethod extends JavaCallable {
     private final Method method;
-    private final Class[] parameterTypes;
+    private final Class<?>[] parameterTypes;
     private final JavaUtil.JavaConverter returnConverter;
 
     public static RubyClass createJavaMethodClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = 
             javaModule.defineClassUnder("JavaMethod", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(JavaMethod.class);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
+        JavaCallable.registerRubyMethods(runtime, result);
         
         result.defineFastMethod("name", callbackFactory.getFastMethod("name"));
-        result.defineFastMethod("arity", callbackFactory.getFastMethod("arity"));
-        result.defineFastMethod("public?", callbackFactory.getFastMethod("public_p"));
         result.defineFastMethod("final?", callbackFactory.getFastMethod("final_p"));
         result.defineFastMethod("static?", callbackFactory.getFastMethod("static_p"));
+        result.defineFastMethod("bridge?", callbackFactory.getFastMethod("bridge_p"));
         result.defineFastMethod("invoke", callbackFactory.getFastOptMethod("invoke"));
         result.defineFastMethod("invoke_static", callbackFactory.getFastOptMethod("invoke_static"));
-        result.defineFastMethod("argument_types", callbackFactory.getFastMethod("argument_types"));
-        result.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         result.defineFastMethod("return_type", callbackFactory.getFastMethod("return_type"));
+        result.defineFastMethod("type_parameters", callbackFactory.getFastMethod("type_parameters"));
 
         return result;
     }
 
     public JavaMethod(Ruby runtime, Method method) {
         super(runtime, (RubyClass) runtime.getJavaSupport().getJavaModule().fastGetClass("JavaMethod"));
         this.method = method;
         this.parameterTypes = method.getParameterTypes();
 
         // Special classes like Collections.EMPTY_LIST are inner classes that are private but 
         // implement public interfaces.  Their methods are all public methods for the public 
         // interface.  Let these public methods execute via setAccessible(true). 
         if (Modifier.isPublic(method.getModifiers()) &&
             Modifier.isPublic(method.getClass().getModifiers()) &&
             !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
             accessibleObject().setAccessible(true);
         }
         
         returnConverter = JavaUtil.getJavaConverter(method.getReturnType());
     }
 
     public static JavaMethod create(Ruby runtime, Method method) {
         return new JavaMethod(runtime, method);
     }
 
-    public static JavaMethod create(Ruby runtime, Class javaClass, String methodName, Class[] argumentTypes) {
+    public static JavaMethod create(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         try {
             Method method = javaClass.getMethod(methodName, argumentTypes);
             return create(runtime, method);
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
-    public static JavaMethod createDeclared(Ruby runtime, Class javaClass, String methodName, Class[] argumentTypes) {
+    public static JavaMethod createDeclared(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         try {
             Method method = javaClass.getDeclaredMethod(methodName, argumentTypes);
             return create(runtime, method);
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
     public boolean equals(Object other) {
         return other instanceof JavaMethod &&
             this.method == ((JavaMethod)other).method;
     }
     
     public int hashCode() {
         return method.hashCode();
     }
 
     public RubyString name() {
         return getRuntime().newString(method.getName());
     }
 
-    protected int getArity() {
+    public int getArity() {
         return parameterTypes.length;
     }
 
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(method.getModifiers()));
     }
 
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(method.getModifiers()));
     }
 
     public IRubyObject invoke(IRubyObject[] args) {
         if (args.length != 1 + getArity()) {
             throw getRuntime().newArgumentError(args.length, 1 + getArity());
         }
 
         IRubyObject invokee = args[0];
         if (! (invokee instanceof JavaObject)) {
             throw getRuntime().newTypeError("invokee not a java object");
         }
         Object javaInvokee = ((JavaObject) invokee).getValue();
         Object[] arguments = new Object[args.length - 1];
         convertArguments(arguments, args, 1);
 
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
         return invokeWithExceptionHandling(method, javaInvokee, arguments);
     }
 
     public IRubyObject invoke_static(IRubyObject[] args) {
         if (args.length != getArity()) {
             throw getRuntime().newArgumentError(args.length, getArity());
         }
         Object[] arguments = new Object[args.length];
         System.arraycopy(args, 0, arguments, 0, arguments.length);
         convertArguments(arguments, args, 0);
         return invokeWithExceptionHandling(method, null, arguments);
     }
 
     public IRubyObject return_type() {
-        Class klass = method.getReturnType();
+        Class<?> klass = method.getReturnType();
         
         if (klass.equals(void.class)) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), klass);
     }
+    
+    public IRubyObject type_parameters() {
+        return Java.getInstance(getRuntime(), method.getTypeParameters());
+    }
 
     private IRubyObject invokeWithExceptionHandling(Method method, Object javaInvokee, Object[] arguments) {
         try {
             Object result = method.invoke(javaInvokee, arguments);
             return returnConverter.convert(getRuntime(), result);
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError("expected " + argument_types().inspect() + "; got: "
                         + dumpArgTypes(arguments)
                         + "; error: " + iae.getMessage());
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError("illegal access on '" + method.getName() + "': " + iae.getMessage());
         } catch (InvocationTargetException ite) {
             getRuntime().getJavaSupport().handleNativeException(ite.getTargetException());
             // This point is only reached if there was an exception handler installed.
             return getRuntime().getNil();
         }
     }
 
     private String dumpArgTypes(Object[] arguments) {
         StringBuffer str = new StringBuffer("[");
         for (int i = 0; i < arguments.length; i++) {
             if (i > 0) {
                 str.append(",");
             }
             if (arguments[i] == null) {
                 str.append("null");
             } else {
                 str.append(arguments[i].getClass().getName());
             }
         }
         str.append("]");
         return str.toString();
     }
 
     private void convertArguments(Object[] arguments, Object[] args, int from) {
-        Class[] parameterTypes = parameterTypes();
-        for (int i = 0; i < arguments.length; i++) {
-            arguments[i] = JavaUtil.convertArgument(args[i+from], parameterTypes[i]);
+        Class<?>[] types = parameterTypes;
+        for (int i = arguments.length; --i >= 0; ) {
+            arguments[i] = JavaUtil.convertArgument(args[i+from], types[i]);
         }
     }
 
-    protected Class[] parameterTypes() {
+    public Class<?>[] getParameterTypes() {
         return parameterTypes;
     }
 
+    public Class<?>[] getExceptionTypes() {
+        return method.getExceptionTypes();
+    }
+
+    public Type[] getGenericParameterTypes() {
+        return method.getGenericParameterTypes();
+    }
+
+    public Type[] getGenericExceptionTypes() {
+        return method.getGenericExceptionTypes();
+    }
+    
+    public Annotation[][] getParameterAnnotations() {
+        return method.getParameterAnnotations();
+    }
+
+    public boolean isVarArgs() {
+        return method.isVarArgs();
+    }
+
     protected String nameOnInspection() {
         return "#<" + getType().toString() + "/" + method.getName() + "(";
     }
 
     public RubyBoolean static_p() {
         return getRuntime().newBoolean(isStatic());
     }
+    
+    public RubyBoolean bridge_p() {
+        return getRuntime().newBoolean(method.isBridge());
+    }
 
     private boolean isStatic() {
         return Modifier.isStatic(method.getModifiers());
     }
 
-    protected int getModifiers() {
+    public int getModifiers() {
         return method.getModifiers();
     }
 
+    public String toGenericString() {
+        return method.toGenericString();
+    }
+
     protected AccessibleObject accessibleObject() {
         return method;
     }
 }
diff --git a/src/org/jruby/javasupport/JavaObject.java b/src/org/jruby/javasupport/JavaObject.java
index f3da438c8d..9dbac8e751 100644
--- a/src/org/jruby/javasupport/JavaObject.java
+++ b/src/org/jruby/javasupport/JavaObject.java
@@ -1,203 +1,204 @@
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
         if (value != null) {
             if (value instanceof Class) {
-                return JavaClass.get(runtime, (Class)value);
+                return JavaClass.get(runtime, (Class<?>)value);
             } else if (value.getClass().isArray()) {
                 return new JavaArray(runtime, value);
             }
         }
         return new JavaObject(runtime, value);
     }
 
-    public Class getJavaClass() {
+    public Class<?> getJavaClass() {
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
 
     public boolean equals(Object other) {
         return other instanceof JavaObject &&
             this.value == ((JavaObject)other).value;
     }
     
     public int hashCode() {
         if (value != null) return value.hashCode();
         return 0;
     }
 
 	public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
 
     public IRubyObject to_s() {
-        String s = value == null ? "" : value.toString();
-
-        return s == null ? getRuntime().getNil() : RubyString.newUnicodeString(getRuntime(), s);
+        if (value != null) {
+            return RubyString.newUnicodeString(getRuntime(), value.toString());
+        }
+        return getRuntime().newString("");
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
diff --git a/src/org/jruby/javasupport/ParameterTypes.java b/src/org/jruby/javasupport/ParameterTypes.java
new file mode 100644
index 0000000000..be0f2f484c
--- /dev/null
+++ b/src/org/jruby/javasupport/ParameterTypes.java
@@ -0,0 +1,7 @@
+package org.jruby.javasupport;
+
+public interface ParameterTypes {
+    Class<?>[] getParameterTypes();
+    Class<?>[] getExceptionTypes();
+    boolean isVarArgs();
+}
diff --git a/src/org/jruby/javasupport/proxy/JavaProxyClass.java b/src/org/jruby/javasupport/proxy/JavaProxyClass.java
index 0b7e365a14..8080a05cb3 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyClass.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyClass.java
@@ -1,725 +1,729 @@
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
 
 package org.jruby.javasupport.proxy;
 
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.security.AccessController;
 import java.security.PrivilegedActionException;
 import java.security.PrivilegedExceptionAction;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyNil;
 import org.jruby.RubyString;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.JavaClass;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Generalized proxy for classes and interfaces.
  * 
  * API looks a lot like java.lang.reflect.Proxy, except that you can specify a
  * super class in addition to a set of interfaces.
  * 
  * The main implication for users of this class is to handle the case where a
  * proxy method overrides an existing method, because in this case the
  * invocation handler should "default" to calling the super implementation
  * {JavaProxyMethod.invokeSuper}.
  * 
  * 
  * @author krab@trifork.com
  * @see java.lang.reflect.Proxy
  * 
  */
 public class JavaProxyClass extends JavaProxyReflectionObject {
     static ThreadLocal<Ruby> runtimeTLS = new ThreadLocal<Ruby>();
     private final Class proxyClass;
     private final ArrayList<JavaProxyMethod> methods = new ArrayList<JavaProxyMethod>();
     private final HashMap<String, List<JavaProxyMethod>> methodMap = new HashMap<String, List<JavaProxyMethod>>();
     private final RubyArray constructors;
 
     /* package scope */
     JavaProxyClass(Class proxyClass) {
         super(getThreadLocalRuntime(), 
                 (RubyClass) getThreadLocalRuntime().fastGetModule("Java").fastGetClass("JavaProxyClass"));
         
         this.proxyClass = proxyClass;
         this.constructors = buildRubyArray(getConstructors());
     }
 
     public boolean equals(Object other) {
         return other instanceof JavaProxyClass &&
             this.proxyClass == ((JavaProxyClass)other).proxyClass;
     }
     
     public int hashCode() {
         return proxyClass.hashCode();
     }
 
     public Object getValue() {
         return this;
     }
 
     private static Ruby getThreadLocalRuntime() {
         return runtimeTLS.get();
     }
 
     public static JavaProxyClass getProxyClass(Ruby runtime, Class superClass,
             Class[] interfaces, Set names) throws InvocationTargetException {
         Ruby save = runtimeTLS.get();
         runtimeTLS.set(runtime);
         try {
             ClassLoader loader = runtime.getJRubyClassLoader();
 
             return JavaProxyClassFactory.newProxyClass(loader, null, superClass, interfaces, names);
         } finally {
             runtimeTLS.set(save);
         }
     }
 
     public static JavaProxyClass getProxyClass(Ruby runtime, Class superClass,
             Class[] interfaces) throws InvocationTargetException {
         return getProxyClass(runtime,superClass,interfaces,null);
     }
     
     public static Object newProxyInstance(Ruby runtime, Class superClass, Class[] interfaces, 
             Class[] constructorParameters, Object[] constructorArgs, 
             JavaProxyInvocationHandler handler) throws IllegalArgumentException, 
             InstantiationException, IllegalAccessException, InvocationTargetException,
             SecurityException, NoSuchMethodException {
         JavaProxyClass jpc = getProxyClass(runtime, superClass, interfaces);
         JavaProxyConstructor cons = jpc.getConstructor(constructorParameters == null ? 
                 new Class[0] : constructorParameters);
         
         return cons.newInstance(constructorArgs, handler);
 
     }
 
     public Class getSuperclass() {
         return proxyClass.getSuperclass();
     }
 
     public Class[] getInterfaces() {
         Class[] ifaces = proxyClass.getInterfaces();
         Class[] result = new Class[ifaces.length - 1];
         int pos = 0;
         for (int i = 0; i < ifaces.length; i++) {
             if (ifaces[i] != InternalJavaProxy.class) {
                 result[pos++] = ifaces[i];
             }
         }
         return result;
     }
 
     public JavaProxyConstructor[] getConstructors() {
         Constructor[] cons = proxyClass.getConstructors();
         JavaProxyConstructor[] result = new JavaProxyConstructor[cons.length];
         for (int i = 0; i < cons.length; i++) {
             result[i] = new JavaProxyConstructor(getRuntime(), this, cons[i]);
         }
         return result;
     }
 
     public JavaProxyConstructor getConstructor(Class[] args)
             throws SecurityException, NoSuchMethodException {
 
         Class[] realArgs = new Class[args.length + 1];
         System.arraycopy(args, 0, realArgs, 0, args.length);
         realArgs[args.length] = JavaProxyInvocationHandler.class;
 
         Constructor constructor = proxyClass.getConstructor(realArgs);
         return new JavaProxyConstructor(getRuntime(), this, constructor);
     }
 
     public JavaProxyMethod[] getMethods() {
         return methods.toArray(new JavaProxyMethod[methods.size()]);
     }
 
     public JavaProxyMethod getMethod(String name, Class[] parameterTypes) {
         List<JavaProxyMethod> methods = methodMap.get(name);
         if (methods != null) {
             for (int i = methods.size(); --i >= 0; ) {
                 ProxyMethodImpl jpm = (ProxyMethodImpl) methods.get(i);
                 if (jpm.matches(name, parameterTypes)) return jpm;
             }
         }
         return null;
     }
 
     /** return the class of instances of this proxy class */
     Class getProxyClass() {
         return proxyClass;
     }
 
     public static class ProxyMethodImpl extends JavaProxyReflectionObject
             implements JavaProxyMethod {
         private final Method m;
 
         private Object state;
 
         private final Method sm;
         private final Class[] parameterTypes;
 
         private final JavaProxyClass clazz;
 
         public ProxyMethodImpl(Ruby runtime, JavaProxyClass clazz, Method m,
                 Method sm) {
             super(runtime, runtime.getJavaSupport().getJavaModule()
                     .fastGetClass("JavaProxyMethod"));
             this.m = m;
             this.parameterTypes = m.getParameterTypes();
             this.sm = sm;
             this.clazz = clazz;
         }
 
         public boolean equals(Object other) {
             return other instanceof ProxyMethodImpl &&
                 this.m == ((ProxyMethodImpl)other).m;
         }
         
         public int hashCode() {
             return m.hashCode();
         }
 
         public Method getMethod() {
             return m;
         }
 
         public Method getSuperMethod() {
             return sm;
         }
 
         public int getModifiers() {
             return m.getModifiers();
         }
 
         public String getName() {
             return m.getName();
         }
 
-        public Class[] getExceptionTypes() {
+        public Class<?>[] getExceptionTypes() {
             return m.getExceptionTypes();
         }
 
-        public Class[] getParameterTypes() {
+        public Class<?>[] getParameterTypes() {
             return parameterTypes;
         }
+        
+        public boolean isVarArgs() {
+            return m.isVarArgs();
+        }
 
         public Object getState() {
             return state;
         }
 
         public boolean hasSuperImplementation() {
             return sm != null;
         }
 
         public Object invoke(Object proxy, Object[] args) throws IllegalArgumentException, 
             IllegalAccessException, InvocationTargetException, NoSuchMethodException {
             
             if (!hasSuperImplementation()) throw new NoSuchMethodException();
 
             return sm.invoke(proxy, args);
         }
 
         public void setState(Object state) {
             this.state = state;
         }
 
         public String toString() {
             return m.toString();
         }
 
         public Object defaultResult() {
             Class rt = m.getReturnType();
             
             if (rt == Void.TYPE) return null;
             if (rt == Boolean.TYPE) return Boolean.FALSE;
             if (rt == Byte.TYPE) return new Byte((byte) 0);
             if (rt == Short.TYPE) return new Short((short) 0);
             if (rt == Integer.TYPE) return new Integer(0);
             if (rt == Long.TYPE) return new Long(0L);
             if (rt == Float.TYPE) return new Float(0.0f);
             if (rt == Double.TYPE) return new Double(0.0);
 
             return null;
         }
 
         public boolean matches(String name, Class[] parameterTypes) {
             return m.getName().equals(name) && Arrays.equals(this.parameterTypes, parameterTypes);
         }
 
         public Class getReturnType() {
             return m.getReturnType();
         }
         
         public static RubyClass createJavaProxyMethodClass(Ruby runtime, RubyModule javaProxyModule) {
             RubyClass result = javaProxyModule.defineClassUnder("JavaProxyMethod", 
                     runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
             CallbackFactory callbackFactory = 
                 runtime.callbackFactory(JavaProxyClass.ProxyMethodImpl.class);
 
             JavaProxyReflectionObject.registerRubyMethods(runtime, result);
 
             result.defineFastMethod("argument_types", callbackFactory.getFastMethod("argument_types"));
             result.defineFastMethod("declaring_class", callbackFactory.getFastMethod("getDeclaringClass"));
             result.defineFastMethod("super?", callbackFactory.getFastMethod("super_p"));
             result.defineFastMethod("arity", callbackFactory.getFastMethod("arity"));
             result.defineFastMethod("name", callbackFactory.getFastMethod("name"));
             result.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
             result.defineFastMethod("invoke", callbackFactory.getFastOptMethod("do_invoke"));
 
             return result;
         }
 
         public RubyObject name() {
             return getRuntime().newString(getName());
         }
 
         public JavaProxyClass getDeclaringClass() {
             return clazz;
         }
 
         public RubyArray argument_types() {
             return buildRubyArray(getParameterTypes());
         }
 
         public IRubyObject super_p() {
             return hasSuperImplementation() ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         public RubyFixnum arity() {
             return getRuntime().newFixnum(getArity());
         }
 
         protected String nameOnInspection() {
             return getDeclaringClass().nameOnInspection() + "/" + getName();
         }
 
         public IRubyObject inspect() {
             StringBuffer result = new StringBuffer();
             result.append(nameOnInspection());
             result.append("(");
             Class[] parameterTypes = getParameterTypes();
             for (int i = 0; i < parameterTypes.length; i++) {
                 result.append(parameterTypes[i].getName());
                 if (i < parameterTypes.length - 1) {
                     result.append(',');
                 }
             }
             result.append(")>");
             return getRuntime().newString(result.toString());
         }
 
         public IRubyObject do_invoke(IRubyObject[] nargs) {
             if (nargs.length != 1 + getArity()) {
                 throw getRuntime().newArgumentError(nargs.length, 1 + getArity());
             }
 
             IRubyObject invokee = nargs[0];
             if (!(invokee instanceof JavaObject)) {
                 throw getRuntime().newTypeError("invokee not a java object");
             }
             Object receiver_value = ((JavaObject) invokee).getValue();
             Object[] arguments = new Object[nargs.length - 1];
             System.arraycopy(nargs, 1, arguments, 0, arguments.length);
 
             Class[] parameterTypes = getParameterTypes();
             for (int i = 0; i < arguments.length; i++) {
                 arguments[i] = 
                     JavaUtil.convertRubyToJava((IRubyObject) arguments[i], parameterTypes[i]);
             }
 
             try {
                 Object javaResult = sm.invoke(receiver_value, arguments);
                 return JavaUtil.convertJavaToRuby(getRuntime(), javaResult, getReturnType());
             } catch (IllegalArgumentException e) {
                 throw getRuntime().newTypeError("expected " + argument_types().inspect());
             } catch (IllegalAccessException iae) {
                 throw getRuntime().newTypeError("illegal access on '" + sm.getName() + "': " + 
                         iae.getMessage());
             } catch (InvocationTargetException ite) {
                 ite.getTargetException().printStackTrace();
                 getRuntime().getJavaSupport().handleNativeException(ite.getTargetException());
                 // This point is only reached if there was an exception handler
                 // installed.
                 return getRuntime().getNil();
             }
         }
 
         private int getArity() {
             return getParameterTypes().length;
         }
 
     }
 
     JavaProxyMethod initMethod(String name, String desc, boolean hasSuper) {
         Class proxy = proxyClass;
         try {
             Class[] parms = parse(proxy.getClassLoader(), desc);
             Method m = proxy.getDeclaredMethod(name, parms);
             Method sm = null;
             if (hasSuper) {
                 sm = proxy.getDeclaredMethod("__super$" + name, parms);
             }
 
             JavaProxyMethod jpm = new ProxyMethodImpl(getRuntime(), this, m, sm);
             methods.add(jpm);
             List<JavaProxyMethod> methodsWithName = methodMap.get(name);
             if (methodsWithName == null) {
                 methodsWithName = new ArrayList<JavaProxyMethod>(2);
                 methodMap.put(name,methodsWithName);
             }
             methodsWithName.add(jpm);
             
             return jpm;
         } catch (ClassNotFoundException e) {
             throw new InternalError(e.getMessage());
         } catch (SecurityException e) {
             throw new InternalError(e.getMessage());
         } catch (NoSuchMethodException e) {
             throw new InternalError(e.getMessage());
         }
     }
 
     private static Class[] parse(final ClassLoader loader, String desc)
             throws ClassNotFoundException {
         List<Class> al = new ArrayList<Class>();
         int idx = 1;
         while (desc.charAt(idx) != ')') {
 
             int arr = 0;
             while (desc.charAt(idx) == '[') {
                 idx += 1;
                 arr += 1;
             }
 
             Class type;
 
             switch (desc.charAt(idx)) {
             case 'L':
                 int semi = desc.indexOf(';', idx);
                 final String name = desc.substring(idx + 1, semi);
                 idx = semi;
                 try {
                     type = AccessController.doPrivileged(new PrivilegedExceptionAction<Class>() {
                                 public Class run() throws ClassNotFoundException {
                                     return Class.forName(name.replace('/', '.'), false, loader);
                                 }
                             });
                 } catch (PrivilegedActionException e) {
                     throw (ClassNotFoundException) e.getException();
                 }
                 break;
 
             case 'B': type = Byte.TYPE; break;
             case 'C': type = Character.TYPE; break;
             case 'Z': type = Boolean.TYPE; break;
             case 'S': type = Short.TYPE; break;
             case 'I': type = Integer.TYPE; break;
             case 'J': type = Long.TYPE; break;
             case 'F': type = Float.TYPE; break;
             case 'D': type = Double.TYPE; break;
             default:
                 throw new InternalError("cannot parse " + desc + "[" + idx + "]");
             }
 
             idx += 1;
 
             if (arr != 0) {
                 type = Array.newInstance(type, new int[arr]).getClass();
             }
 
             al.add(type);
         }
 
         return (Class[]) al.toArray(new Class[al.size()]);
     }
 
     //
     // Ruby-level methods
     //
         
     public static RubyClass createJavaProxyClassClass(Ruby runtime, RubyModule javaModule) {
         RubyClass result = javaModule.defineClassUnder("JavaProxyClass",
                 runtime.getObject(),ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(JavaProxyClass.class);
 
         JavaProxyReflectionObject.registerRubyMethods(runtime, result);
 
         result.defineFastMethod("constructors", callbackFactory.getFastMethod("constructors"));
         result.defineFastMethod("superclass", callbackFactory.getFastMethod("superclass"));
         result.defineFastMethod("interfaces", callbackFactory.getFastMethod("interfaces"));
         result.defineFastMethod("methods", callbackFactory.getFastMethod("methods"));
 
         result.getMetaClass().defineFastMethod("get", 
                 callbackFactory.getFastSingletonMethod("get", JavaClass.class));
         result.getMetaClass().defineFastMethod("get_with_class", 
                 callbackFactory.getFastSingletonMethod("get_with_class", RubyClass.class));
 
         return result;
     }
 
     public static RubyObject get(IRubyObject recv, JavaClass type) {
         try {
             return getProxyClass(recv.getRuntime(), (Class) type.getValue(), new Class[0]);
         } catch (Error e) {
             RaiseException ex = recv.getRuntime().newArgumentError("unable to create proxy class for " + type.getValue());
             ex.initCause(e);
             throw ex;
         } catch (InvocationTargetException e) {
             RaiseException ex = recv.getRuntime().newArgumentError("unable to create proxy class for " + type.getValue());
             ex.initCause(e);
             throw ex;
         }
     }
     
     private static final HashSet<String> EXCLUDE_MODULES = new HashSet<String>();
     static {
         EXCLUDE_MODULES.add("Kernel");
         EXCLUDE_MODULES.add("Java");
         EXCLUDE_MODULES.add("JavaProxyMethods");
         EXCLUDE_MODULES.add("Enumerable");
     }
 
     private static final HashSet<String> EXCLUDE_METHODS = new HashSet<String>();
     static {
         EXCLUDE_METHODS.add("class");
         EXCLUDE_METHODS.add("finalize");
         EXCLUDE_METHODS.add("initialize");
         EXCLUDE_METHODS.add("java_class");
         EXCLUDE_METHODS.add("java_object");
         EXCLUDE_METHODS.add("__jcreate!");
         EXCLUDE_METHODS.add("__jsend!");
     }
 
     public static RubyObject get_with_class(IRubyObject recv, RubyClass clazz) {
         Ruby runtime = recv.getRuntime();
         
         // Let's only generate methods for those the user may actually 
         // intend to override.  That includes any defined in the current
         // class, and any ancestors that are also JavaProxyClasses (but none
         // from any other ancestor classes). Methods defined in mixins will
         // be considered intentionally overridden, except those from Kernel,
         // Java, and JavaProxyMethods, as well as Enumerable. 
         // TODO: may want to exclude other common mixins?
 
         JavaClass javaClass = null;
         Set<String> names = new HashSet<String>(); // need names ordered for key generation later
         List<Class> interfaceList = new ArrayList<Class>();
 
         List<IRubyObject> ancestors = clazz.getAncestorList();
         boolean skipRemainingClasses = false;
         for (IRubyObject ancestorObject: ancestors) {
             RubyModule ancestor = (RubyModule) ancestorObject;
             if (ancestor instanceof RubyClass) {
                 if (skipRemainingClasses) continue;
                 // we only collect methods and interfaces for 
                 // user-defined proxy classes.
                 if (!ancestor.getInstanceVariables().fastHasInstanceVariable("@java_proxy_class")) {
                     skipRemainingClasses = true;
                     continue;
                 }
 
                 // get JavaClass if this is the new proxy class; verify it
                 // matches if this is a superclass proxy.
                 IRubyObject var = ancestor.getInstanceVariables().fastGetInstanceVariable("@java_class");
                 if (var == null) {
                     throw runtime.newTypeError(
                             "no java_class defined for proxy (or ancestor): " + ancestor);
                 } else if (!(var instanceof JavaClass)) {
                     throw runtime.newTypeError(
                             "invalid java_class defined for proxy (or ancestor): " +
                             ancestor + ": " + var);
                 }
                 if (javaClass == null) {
                     javaClass = (JavaClass)var;
                 } else if (javaClass != var) {
                     throw runtime.newTypeError(
                             "java_class defined for " + clazz + " (" + javaClass +
                             ") does not match java_class for ancestor " + ancestor +
                             " (" + var + ")");
                 }
                 // get any included interfaces
                 var = ancestor.getInstanceVariables().fastGetInstanceVariable("@java_interfaces");
                 if (var != null && !(var instanceof RubyNil)) {
                     if (!(var instanceof RubyArray)) {
                         throw runtime.newTypeError(
                                 "invalid java_interfaces defined for proxy (or ancestor): " +
                                 ancestor + ": " + var);
                     }
                     RubyArray ifcArray = (RubyArray)var;
                     int size = ifcArray.size();
                     for (int i = size; --i >= 0; ) {
                         IRubyObject ifc = ifcArray.eltInternal(i);
                         if (!(ifc instanceof JavaClass)) {
                             throw runtime.newTypeError(
                                 "invalid java interface defined for proxy (or ancestor): " +
                                 ancestor + ": " + ifc);
                         }
                         Class interfaceClass = ((JavaClass)ifc).javaClass();
                         if (!interfaceClass.isInterface()) {
                             throw runtime.newTypeError(
                                     "invalid java interface defined for proxy (or ancestor): " +
                                     ancestor + ": " + ifc + " (not an interface)");
                         }
                         if (!interfaceList.contains(interfaceClass)) {
                             interfaceList.add(interfaceClass);
                         }
                     }
                 }
                 // set this class's method names in var @__java_ovrd_methods if this
                 // is the new class; otherwise, get method names from there if this is
                 // a proxy superclass.
                 
                 // FIXME: shouldn't need @__java_ovrd_methods, just query locally defined methods.
                 
                 var = ancestor.getInstanceVariables().fastGetInstanceVariable("@__java_ovrd_methods");
                 if (var == null) {
                     // lock in the overridden methods for the new class, and any as-yet
                     // uninstantiated ancestor class.
                     Map<String, DynamicMethod> methods;
                     RubyArray methodNames;
                     synchronized(methods = ancestor.getMethods()) {
                         methodNames = RubyArray.newArrayLight(runtime,methods.size());
                         for (String methodName: methods.keySet()) {
                             if (!EXCLUDE_METHODS.contains(methodName)) {
                                 names.add(methodName);
                                 methodNames.add(runtime.newString(methodName));
                             }
                         }
                     }
                     ancestor.fastSetInstanceVariable("@__java_ovrd_methods",methodNames);
                 } else {
                     if (!(var instanceof RubyArray)) {
                         throw runtime.newTypeError(
                                 "invalid @__java_ovrd_methods defined for proxy: " +
                                 ancestor + ": " + var);
                     }
                     RubyArray methodNames = (RubyArray)var;
                     int size = methodNames.size();
                     for (int i = size; --i >= 0; ) {
                         IRubyObject methodName = methodNames.eltInternal(i);
                         if (!(methodName instanceof RubyString)) {
                             throw runtime.newTypeError(
                                     "invalid method name defined for proxy (or ancestor): " +
                                     ancestor + ": " + methodName);
                         }
                         names.add(methodName.asJavaString());
                     }
                 }
             } else if (!EXCLUDE_MODULES.contains(ancestor.getName())) {
                 Map<String, DynamicMethod> methods;
                 synchronized(methods = ancestor.getMethods()) {
                     for (String methodName: methods.keySet()) {
                         if (!EXCLUDE_METHODS.contains(methodName)) {
                             names.add(methodName);
                         }
                     }
                 }
             }
         }
 
         if (javaClass == null) {
             throw runtime.newArgumentError("unable to create proxy class: no java_class defined for " + clazz);
         }
         
         int interfaceCount = interfaceList.size();
         Class[] interfaces = new Class[interfaceCount];
         for (int i = interfaceCount; --i >= 0; ) {
             interfaces[i] = (Class)interfaceList.get(i);
         }
        
         try {
             return getProxyClass(recv.getRuntime(), javaClass.javaClass(), interfaces, names);
         } catch (Error e) {
             RaiseException ex = recv.getRuntime().newArgumentError("unable to create proxy class for " + javaClass.getValue() + " : " + e.getMessage());
             //e.printStackTrace();
             ex.initCause(e);
             throw ex;
         } catch (InvocationTargetException e) {
             RaiseException ex = recv.getRuntime().newArgumentError("unable to create proxy class for " + javaClass.getValue() + " : " + e.getMessage());
             //e.printStackTrace();
             ex.initCause(e);
             throw ex;
         }
     }
 
 
     public RubyObject superclass() {
         return JavaClass.get(getRuntime(), getSuperclass());
     }
 
     public RubyArray methods() {
         return buildRubyArray(getMethods());
     }
 
     public RubyArray interfaces() {
         return buildRubyArray(getInterfaces());
     }
 
     public RubyArray constructors() {
         return this.constructors;
     }
 
     public static void createJavaProxyModule(Ruby runtime) {
         // TODO Auto-generated method stub
 
         RubyModule javaProxyModule = runtime.getJavaSupport().getJavaModule();
         JavaProxyClass.createJavaProxyClassClass(runtime, javaProxyModule);
         ProxyMethodImpl.createJavaProxyMethodClass(runtime, javaProxyModule);
         JavaProxyConstructor.createJavaProxyConstructorClass(runtime, javaProxyModule);
     }
 
     public String nameOnInspection() {
         return "[Proxy:" + getSuperclass().getName() + "]";
     }
 }
diff --git a/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java b/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java
index 4a7118f0da..0cdcaae798 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java
@@ -1,265 +1,275 @@
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
 
 package org.jruby.javasupport.proxy;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.JavaUtil;
+import org.jruby.javasupport.ParameterTypes;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
-public class JavaProxyConstructor extends JavaProxyReflectionObject {
+public class JavaProxyConstructor extends JavaProxyReflectionObject implements ParameterTypes {
 
-    private final Constructor proxyConstructor;
-    private final Class[] parameterTypes;
+    private final Constructor<?> proxyConstructor;
+    private final Class<?>[] apparentParameterTypes;
 
     private final JavaProxyClass declaringProxyClass;
 
     JavaProxyConstructor(Ruby runtime, JavaProxyClass pClass,
-            Constructor constructor) {
+            Constructor<?> constructor) {
         super(runtime, runtime.getJavaSupport().getJavaModule().fastGetClass(
                 "JavaProxyConstructor"));
         this.declaringProxyClass = pClass;
         this.proxyConstructor = constructor;
-        this.parameterTypes = proxyConstructor.getParameterTypes();
+        Class<?>[] parameterTypes = constructor.getParameterTypes();
+        int len = parameterTypes.length - 1;
+        this.apparentParameterTypes = new Class<?>[len];
+        System.arraycopy(parameterTypes, 0, apparentParameterTypes, 0, len);
     }
 
-    public Class[] getParameterTypes() {
-        Class[] result = new Class[parameterTypes.length - 1];
-        System.arraycopy(parameterTypes, 0, result, 0, result.length);
-        return result;
+    public Class<?>[] getParameterTypes() {
+        return apparentParameterTypes;
+    }
+
+    public Class<?>[] getExceptionTypes() {
+        return proxyConstructor.getExceptionTypes();
+    }
+    
+    public boolean isVarArgs() {
+        return proxyConstructor.isVarArgs();
     }
 
     public JavaProxyClass getDeclaringClass() {
         return declaringProxyClass;
     }
 
     public Object newInstance(Object[] args, JavaProxyInvocationHandler handler)
             throws IllegalArgumentException, InstantiationException,
             IllegalAccessException, InvocationTargetException {
-        if (args.length + 1 != parameterTypes.length) {
+        if (args.length != apparentParameterTypes.length) {
             throw new IllegalArgumentException("wrong number of parameters");
         }
 
         Object[] realArgs = new Object[args.length + 1];
         System.arraycopy(args, 0, realArgs, 0, args.length);
         realArgs[args.length] = handler;
 
         return proxyConstructor.newInstance(realArgs);
     }
 
     public static RubyClass createJavaProxyConstructorClass(Ruby runtime,
             RubyModule javaProxyModule) {
-        RubyClass result = javaProxyModule.defineClassUnder(
-                                                            "JavaProxyConstructor", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
+        RubyClass result = javaProxyModule.defineClassUnder("JavaProxyConstructor",
+                runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         CallbackFactory callbackFactory = runtime
                 .callbackFactory(JavaProxyConstructor.class);
 
         JavaProxyReflectionObject.registerRubyMethods(runtime, result);
 
         result.defineFastMethod("argument_types", callbackFactory
                 .getFastMethod("argument_types"));
 
         result.defineFastMethod("declaring_class", callbackFactory
                 .getFastMethod("getDeclaringClass"));
 
         result.defineMethod("new_instance", callbackFactory
                 .getOptMethod("new_instance"));
         
         result.defineMethod("new_instance2", callbackFactory.getOptMethod("new_instance2"));
 
         result.defineFastMethod("arity", callbackFactory.getFastMethod("arity"));
 
         return result;
 
     }
 
     public RubyFixnum arity() {
         return getRuntime().newFixnum(getParameterTypes().length);
     }
     
     public boolean equals(Object other) {
         return other instanceof JavaProxyConstructor &&
             this.proxyConstructor == ((JavaProxyConstructor)other).proxyConstructor;
     }
     
     public int hashCode() {
         return proxyConstructor.hashCode();
     }
 
     protected String nameOnInspection() {
         return getDeclaringClass().nameOnInspection();
     }
 
     public IRubyObject inspect() {
         StringBuffer result = new StringBuffer();
         result.append(nameOnInspection());
-        Class[] parameterTypes = getParameterTypes();
+        Class<?>[] parameterTypes = getParameterTypes();
         for (int i = 0; i < parameterTypes.length; i++) {
             result.append(parameterTypes[i].getName());
             if (i < parameterTypes.length - 1) {
                 result.append(',');
             }
         }
         result.append(")>");
         return getRuntime().newString(result.toString());
     }
 
     public RubyArray argument_types() {
         return buildRubyArray(getParameterTypes());
     }
     
     public RubyObject new_instance2(IRubyObject[] args, Block unusedBlock) {
         Arity.checkArgumentCount(getRuntime(), args, 2, 2);
 
         final IRubyObject self = args[0];
         final Ruby runtime = self.getRuntime();
         final RubyModule javaUtilities = runtime.getJavaSupport().getJavaUtilitiesModule();
         RubyArray constructor_args = (RubyArray) args[1];
-        Class[] parameterTypes = getParameterTypes();
+        Class<?>[] parameterTypes = getParameterTypes();
         int count = (int) constructor_args.length().getLongValue();
         Object[] converted = new Object[count];
         
         for (int i = 0; i < count; i++) {
             // TODO: call ruby method
             IRubyObject ith = constructor_args.aref(new IRubyObject[] { getRuntime().newFixnum(i) });
             converted[i] = JavaUtil.convertArgument(Java.ruby_to_java(this, ith, Block.NULL_BLOCK), parameterTypes[i]);
         }
 
         JavaProxyInvocationHandler handler = new JavaProxyInvocationHandler() {
             public Object invoke(Object proxy, JavaProxyMethod m, Object[] nargs) throws Throwable {
                 String name = m.getName();
                 DynamicMethod method = self.getMetaClass().searchMethod(name);
                 int v = method.getArity().getValue();
                 IRubyObject[] newArgs = new IRubyObject[nargs.length];
                 for (int i = nargs.length; --i >= 0; ) {
                     newArgs[i] = Java.java_to_ruby(
                             javaUtilities,
                             JavaObject.wrap(runtime, nargs[i]),
                             Block.NULL_BLOCK);
                 }
                 
                 if (v < 0 || v == (newArgs.length)) {
                     return JavaUtil.convertRubyToJava(RuntimeHelpers.invoke(runtime.getCurrentContext(), self, name, newArgs, CallType.FUNCTIONAL, Block.NULL_BLOCK), m.getReturnType());
                 } else {
                     RubyClass superClass = self.getMetaClass().getSuperClass();
                     return JavaUtil.convertRubyToJava(RuntimeHelpers.invokeAs(runtime.getCurrentContext(), superClass, self, name, newArgs, CallType.SUPER, Block.NULL_BLOCK), m.getReturnType());
                 }
             }
         };
 
         try {
             return JavaObject.wrap(getRuntime(), newInstance(converted, handler));
         } catch (Exception e) {
             RaiseException ex = getRuntime().newArgumentError(
                     "Constructor invocation failed: " + e.getMessage());
             ex.initCause(e);
             throw ex;
         }
     }
 
     public RubyObject new_instance(IRubyObject[] args, Block block) {
         int size = Arity.checkArgumentCount(getRuntime(), args, 1, 2) - 1;
         final RubyProc proc;
 
         // Is there a supplied proc argument or do we assume a block was
         // supplied
         if (args[size] instanceof RubyProc) {
             proc = (RubyProc) args[size];
         } else {
             proc = getRuntime().newProc(Block.Type.PROC,block);
             size++;
         }
 
         RubyArray constructor_args = (RubyArray) args[0];
-        Class[] parameterTypes = getParameterTypes();
+        Class<?>[] parameterTypes = getParameterTypes();
 
         int count = (int) constructor_args.length().getLongValue();
         Object[] converted = new Object[count];
         for (int i = 0; i < count; i++) {
             // TODO: call ruby method
             IRubyObject ith = constructor_args.aref(new IRubyObject[] { getRuntime().newFixnum(i) });
             converted[i] = JavaUtil.convertArgument(Java.ruby_to_java(this, ith, Block.NULL_BLOCK), parameterTypes[i]);
         }
 
         final IRubyObject recv = this;
 
         JavaProxyInvocationHandler handler = new JavaProxyInvocationHandler() {
 
             public Object invoke(Object proxy, JavaProxyMethod method,
                     Object[] nargs) throws Throwable {
                 int length = nargs == null ? 0 : nargs.length;
                 IRubyObject[] rubyArgs = new IRubyObject[length + 2];
                 rubyArgs[0] = JavaObject.wrap(recv.getRuntime(), proxy);
                 rubyArgs[1] = method;
                 for (int i = 0; i < length; i++) {
                     rubyArgs[i + 2] = JavaUtil.convertJavaToRuby(getRuntime(),
                             nargs[i]);
                 }
                 IRubyObject call_result = proc.call(rubyArgs);
                 Object converted_result = JavaUtil.convertRubyToJava(
                         call_result, method.getReturnType());
                 return converted_result;
             }
 
         };
 
         Object result;
         try {
             result = newInstance(converted, handler);
         } catch (Exception e) {
             RaiseException ex = getRuntime().newArgumentError(
                     "Constructor invocation failed: " + e.getMessage());
             ex.initCause(e);
             throw ex;
         }
 
         return JavaObject.wrap(getRuntime(), result);
 
     }
 
 }
diff --git a/src/org/jruby/javasupport/proxy/JavaProxyMethod.java b/src/org/jruby/javasupport/proxy/JavaProxyMethod.java
index c116ca44b6..db502150c4 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyMethod.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyMethod.java
@@ -1,77 +1,74 @@
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
 
 package org.jruby.javasupport.proxy;
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 
+import org.jruby.javasupport.ParameterTypes;
 import org.jruby.runtime.builtin.IRubyObject;
 
-public interface JavaProxyMethod extends IRubyObject {
+public interface JavaProxyMethod extends IRubyObject, ParameterTypes {
 
     Method getSuperMethod();
 
     /** get state (cache target callable?) in the proxy method */
     Object getState();
 
     /** store state (cache target callable?) in the proxy method */
     void setState(Object state);
 
     /** is it possible to call the super method? */
     boolean hasSuperImplementation();
 
     /**
      * @param proxy
      * @param args
      * @return
      * @throws IllegalArgumentException
      * @throws IllegalAccessException
      * @throws InvocationTargetException
      * @throws NoSuchMethodException
      *             if this ProxyMethod has no super implementation
      */
     Object invoke(Object proxy, Object[] args) throws IllegalArgumentException,
             IllegalAccessException, InvocationTargetException,
             NoSuchMethodException;
 
     Object defaultResult();
 
     String getName();
 
-    Class[] getParameterTypes();
-
-    Class[] getExceptionTypes();
-
-    Class getReturnType();
+    Class<?> getReturnType();
 
     JavaProxyClass getDeclaringClass();
 
     int getModifiers();
 
 }
\ No newline at end of file
diff --git a/test/org/jruby/javasupport/test/test_java_class_resource_methods.properties b/test/org/jruby/javasupport/test/test_java_class_resource_methods.properties
new file mode 100644
index 0000000000..f6ba564f51
--- /dev/null
+++ b/test/org/jruby/javasupport/test/test_java_class_resource_methods.properties
@@ -0,0 +1,2 @@
+foo=bar
+bool=true
\ No newline at end of file
diff --git a/test/test_higher_javasupport.rb b/test/test_higher_javasupport.rb
index 3da7683573..5f0a038007 100644
--- a/test/test_higher_javasupport.rb
+++ b/test/test_higher_javasupport.rb
@@ -1,693 +1,743 @@
 require 'java'
 require 'test/unit'
 
 TopLevelConstantExistsProc = Proc.new do
   include_class 'java.lang.String'
 end
 
 class TestHigherJavasupport < Test::Unit::TestCase
   TestHelper = org.jruby.test.TestHelper
   JArray = ArrayList = java.util.ArrayList
   FinalMethodBaseTest = org.jruby.test.FinalMethodBaseTest
+  Annotation = java.lang.annotation.Annotation
 
   def test_java_passing_class
     assert_equal("java.util.ArrayList", TestHelper.getClassName(ArrayList))
   end
 
   @@include_java_lang = Proc.new {
       include_package "java.lang"
       java_alias :JavaInteger, :Integer
   }
 
   def test_java_class_loading_and_class_name_collisions
     assert_raises(NameError) { System }
     @@include_java_lang.call
     assert_nothing_raised { System }
     assert_equal(10, JavaInteger.new(10).intValue)
     assert_raises(NoMethodError) { Integer.new(10) }
   end
 
   Random = java.util.Random
   Double = java.lang.Double
   def test_constructors_and_instance_methods
     r = Random.new
     assert_equal(Random, r.class)
     r = Random.new(1001)
     assert_equal(10.0, Double.new(10).doubleValue())
     assert_equal(10.0, Double.new("10").doubleValue())
 
     assert_equal(Random, r.class)
     assert_equal(Fixnum, r.nextInt.class)
     assert_equal(Fixnum, r.nextInt(10).class)
   end
 
   Long = java.lang.Long
   def test_instance_methods_differing_only_on_argument_type
     l1 = Long.new(1234)
     l2 = Long.new(1000)
     assert(l1.compareTo(l2) > 0)
   end
 
   def test_dispatching_on_nil
     sb = TestHelper.getInterfacedInstance()
     assert_equal(nil , sb.dispatchObject(nil))
   end
 
   def test_class_methods
     result = java.lang.System.currentTimeMillis()
     assert_equal(Fixnum, result.class)
   end
 
   Boolean = java.lang.Boolean
   def test_class_methods_differing_only_on_argument_type
     assert_equal(true, Boolean.valueOf("true"))
     assert_equal(false, Boolean.valueOf(false))
   end
 
   Character = java.lang.Character
   def test_constants
     assert_equal(9223372036854775807, Long::MAX_VALUE)
     assert(! defined? Character::Y_DATA)  # Known private field in Character
     # class definition with "_" constant causes error
     assert_nothing_raised { org.jruby.javasupport.test.ConstantHolder }
   end
 
   def test_using_arrays
     list = JArray.new
     list.add(10)
     list.add(20)
     array = list.toArray
     assert_equal(10, array[0])
     assert_equal(20, array[1])
     assert_equal(2, array.length)
     array[1] = 1234
     assert_equal(10, array[0])
     assert_equal(1234, array[1])
     assert_equal([10, 1234], array.entries)
     assert_equal(10, array.min)
   end
 
   def test_creating_arrays
     array = Double[3].new
     assert_equal(3, array.length)
     array[0] = 3.14
     array[2] = 17.0
     assert_equal(3.14, array[0])
     assert_equal(17.0, array[2])
   end
 
   Pipe = java.nio.channels.Pipe
   def test_inner_classes
     assert_equal("java.nio.channels.Pipe$SinkChannel",
                  Pipe::SinkChannel.java_class.name)
     assert(Pipe::SinkChannel.instance_methods.include?("keyFor"))
   end
 
   def test_subclasses_and_their_return_types
     l = ArrayList.new
     r = Random.new
     l.add(10)
     assert_equal(10, l.get(0))
     l.add(r)
     r_returned = l.get(1)
     # Since Random is a public class we should get the value casted as that
     assert_equal("java.util.Random", r_returned.java_class.name)
     assert(r_returned.nextInt.kind_of?(Fixnum))
   end
 
   HashMap = java.util.HashMap
   def test_private_classes_interfaces_and_return_types
     h = HashMap.new
     assert_equal(HashMap, h.class)
     h.put("a", 1)
     iter = h.entrySet.iterator
     inner_instance_entry = iter.next
     # The class implements a public interface, MapEntry, so the methods
     # on that should be available, even though the instance is of a
     # private class.
     assert_equal("a", inner_instance_entry.getKey)
   end
 
   class FooArrayList < ArrayList
     $ensureCapacity = false
     def foo
       size
     end
     def ensureCapacity(howmuch)
       $ensureCapacity = true
       super
     end
   end
 
   def test_extending_java_classes
     l = FooArrayList.new
     assert_equal(0, l.foo)
     l.add(100)
     assert_equal(1, l.foo)
     assert_equal(true, $ensureCapacity)
   end
 
   def test_extending_java_interfaces
     if java.lang.Comparable.instance_of?(Module)
       anonymous = Class.new(Object)
       anonymous.send :include, java.lang.Comparable
       anonymous.send :include, java.lang.Runnable
       assert anonymous < java.lang.Comparable
       assert anonymous < java.lang.Runnable
       assert anonymous.new.kind_of?(java.lang.Runnable)
       assert anonymous.new.kind_of?(java.lang.Comparable)
     else
       assert Class.new(java.lang.Comparable)
     end
   end
 
   def test_support_of_other_class_loaders
     assert_helper_class = Java::JavaClass.for_name("org.jruby.test.TestHelper")
     assert_helper_class2 = Java::JavaClass.for_name("org.jruby.test.TestHelper")
     assert(assert_helper_class.java_class == assert_helper_class2.java_class, "Successive calls return the same class")
     method = assert_helper_class.java_method('loadAlternateClass')
     alt_assert_helper_class = method.invoke_static()
 
     constructor = alt_assert_helper_class.constructor();
     alt_assert_helper = constructor.new_instance();
     identityMethod = alt_assert_helper_class.java_method('identityTest')
     identity = Java.java_to_primitive(identityMethod.invoke(alt_assert_helper))
     assert_equal("ABCDEFGH",  identity)
   end
 
   module Foo
     include_class("java.util.ArrayList")
   end
 
   include_class("java.lang.String") {|package,name| "J#{name}" }
   include_class ["java.util.Hashtable", "java.util.Vector"]
 
   def test_class_constants_defined_under_correct_modules
     assert_equal(0, Foo::ArrayList.new.size)
     assert_equal("a", JString.new("a").to_s)
     assert_equal(0, Vector.new.size)
     assert_equal(0, Hashtable.new.size)
   end
 
   def test_high_level_java_should_only_deal_with_proxies_and_not_low_level_java_class
     a = JString.new
     assert(a.getClass().class != "Java::JavaClass")
   end
 
   # We had a problem with accessing singleton class versus class earlier. Sanity check
   # to make sure we are not writing class methods to the same place.
   include_class 'org.jruby.test.AlphaSingleton'
   include_class 'org.jruby.test.BetaSingleton'
 
   def test_make_sure_we_are_not_writing_class_methods_to_the_same_place
     assert_nothing_raised { AlphaSingleton.getInstance.alpha }
   end
 
   include_class 'org.jruby.javasupport.test.Color'
   def test_lazy_proxy_method_tests_for_alias_and_respond_to
     color = Color.new('green')
     assert_equal(true, color.respond_to?(:setColor))
     assert_equal(false, color.respond_to?(:setColorBogus))
   end
 
   class MyColor < Color
     alias_method :foo, :getColor
     def alias_test
       alias_method :foo2, :setColorReallyBogus
     end
   end
 
   def test_accessor_methods
     my_color = MyColor.new('blue')
     assert_equal('blue', my_color.foo)
     assert_raises(NoMethodError) { my_color.alias_test }
     my_color.color = 'red'
     assert_equal('red', my_color.color)
     my_color.setDark(true)
     assert_equal(true, my_color.dark?)
     my_color.dark = false
     assert_equal(false, my_color.dark?)
   end
 
   # No explicit test, but implicitly EMPTY_LIST.each should not blow up interpreter
   # Old error was EMPTY_LIST is a private class implementing a public interface with public methods
   include_class 'java.util.Collections'
   def test_empty_list_each_should_not_blow_up_interpreter
     assert_nothing_raised { Collections::EMPTY_LIST.each {|element| } }
   end
 
   def test_already_loaded_proxies_should_still_see_extend_proxy
     JavaUtilities.extend_proxy('java.util.List') do
       def foo
         true
       end
     end
     assert_equal(true, Foo::ArrayList.new.foo)
   end
     
   def test_same_proxy_does_not_raise
     # JString already included and it is the same proxy, so do not throw an error
     # (e.g. intent of include_class already satisfied)
     assert_nothing_raised do
       begin
         old_stream = $stderr.dup
         $stderr.reopen(RUBY_PLATFORM =~ /mswin/ ? 'NUL:' : '/dev/null')
         $stderr.sync = true
         class << self
           include_class("java.lang.String") {|package,name| "J#{name}" }
         end
       ensure
         $stderr.reopen(old_stream)
       end
     end
   end
 
   include_class 'java.util.Calendar'
   def test_date_time_conversion
     # Test java.util.Date <=> Time implicit conversion
     calendar = Calendar.getInstance
     calendar.setTime(Time.at(0))
     java_date = calendar.getTime
 
     assert_equal(java_date.getTime, Time.at(0).to_i)
   end
 
   def test_expected_java_string_methods_exist
     # test that the list of JString methods contains selected methods from Java
     jstring_methods = %w[bytes charAt char_at compareTo compareToIgnoreCase compare_to
       compare_to_ignore_case concat contentEquals content_equals endsWith
       ends_with equals equalsIgnoreCase equals_ignore_case getBytes getChars
       getClass get_bytes get_chars get_class hashCode hash_code indexOf
       index_of intern java_class java_object java_object= lastIndexOf last_index_of
       length matches notify notifyAll notify_all regionMatches region_matches replace
       replaceAll replaceFirst replace_all replace_first split startsWith starts_with
       subSequence sub_sequence substring taint tainted? toCharArray toLowerCase
       toString toUpperCase to_char_array to_java_object to_lower_case to_string
       to_upper_case trim wait]
 
     jstring_methods.each { |method| assert(JString.public_instance_methods.include?(method), "#{method} is missing from JString") }
   end
 
   def test_direct_package_access
     a = java.util.ArrayList.new
     assert_equal(0, a.size)
   end
 
   Properties = Java::java.util.Properties
   def test_declare_constant
     p = Properties.new
     p.setProperty("a", "b")
     assert_equal("b", p.getProperty("a"))
   end
 
   if java.awt.event.ActionListener.instance_of?(Module)
     class MyBadActionListener
       include java.awt.event.ActionListener
     end
   else
     class MyBadActionListener < java.awt.event.ActionListener
     end
   end
 
   def test_expected_missing_interface_method
     assert_raises(NoMethodError) { MyBadActionListener.new.actionPerformed }
   end
 
   def test_that_misspelt_fq_class_names_dont_stop_future_fq_class_names_with_same_inner_most_package
     assert_raises(NameError) { Java::java.til.zip.ZipFile }
     assert_nothing_raised { Java::java.util.zip.ZipFile }
   end
 
   def test_that_subpackages_havent_leaked_into_other_packages
     assert_equal(false, Java::java.respond_to?(:zip))
     assert_equal(false, Java::com.respond_to?(:util))
   end
 
   def test_that_sub_packages_called_java_javax_com_org_arent_short_circuited
     #to their top-level conterparts
     assert(!com.equal?(java.flirble.com))
   end
 
   def test_that_we_get_the_same_package_instance_on_subsequent_calls
     assert(com.flirble.equal?(com.flirble))
   end
 
   @@include_proc = Proc.new do
     Thread.stop
     include_class "java.lang.System"
     include_class "java.lang.Runtime"
     Thread.current[:time] = System.currentTimeMillis
     Thread.current[:mem] = Runtime.getRuntime.freeMemory
   end
 
   # Disabled temporarily...keeps failing for no obvious reason
 =begin
   def test_that_multiple_threads_including_classes_dont_step_on_each_other
     # we swallow the output to $stderr, so testers don't have to see the
     # warnings about redefining constants over and over again.
     threads = []
 
     begin
       old_stream = $stderr.dup
       $stderr.reopen(RUBY_PLATFORM =~ /mswin/ ? 'NUL:' : '/dev/null')
       $stderr.sync = true
 
       50.times do
         threads << Thread.new(&@@include_proc)
       end
 
       # wait for threads to all stop, then wake them up
       threads.each {|t| Thread.pass until t.stop?}
       threads.each {|t| t.run}
       # join each to let them run
       threads.each {|t| t.join }
       # confirm they all successfully called currentTimeMillis and freeMemory
     ensure
       $stderr.reopen(old_stream)
     end
 
     threads.each do |t|
       assert(t[:time])
       assert(t[:mem])
     end
   end
 =end
 
   unless (java.lang.System.getProperty("java.specification.version") == "1.4")
     if javax.xml.namespace.NamespaceContext.instance_of?(Module)
       class NSCT
         include javax.xml.namespace.NamespaceContext
         # JRUBY-66: No super here...make sure we still work.
         def initialize(arg)
         end
         def getNamespaceURI(prefix)
           'ape:sex'
         end
       end
     else
       class NSCT < javax.xml.namespace.NamespaceContext
         # JRUBY-66: No super here...make sure we still work.
         def initialize(arg)
         end
         def getNamespaceURI(prefix)
           'ape:sex'
         end
       end
     end
     def test_no_need_to_call_super_in_initialize_when_implementing_java_interfaces
       # No error is a pass here for JRUBY-66
       assert_nothing_raised do
         javax.xml.xpath.XPathFactory.newInstance.newXPath.setNamespaceContext(NSCT.new(1))
       end
     end
   end
 
   def test_can_see_inner_class_constants_with_same_name_as_top_level
     # JRUBY-425: make sure we can reference inner class names that match
     # the names of toplevel constants
     ell = java.awt.geom.Ellipse2D
     assert_nothing_raised { ell::Float.new }
   end
 
   def test_that_class_methods_are_being_camel_cased
     assert(java.lang.System.respond_to?("current_time_millis"))
   end
 
   if Java::java.lang.Runnable.instance_of?(Module)
     class TestInitBlock
       include Java::java.lang.Runnable
       def initialize(&block)
         raise if !block
         @bar = block.call
       end
       def bar; @bar; end
     end
   else
     class TestInitBlock < Java::java.lang.Runnable
       def initialize(&block)
         raise if !block
         @bar = block.call
       end
       def bar; @bar; end
     end
   end
 
   def test_that_blocks_are_passed_through_to_the_constructor_for_an_interface_impl
     assert_nothing_raised {
       assert_equal("foo", TestInitBlock.new { "foo" }.bar)
     }
   end
 
   def test_no_collision_with_ruby_allocate_and_java_allocate
     # JRUBY-232
     assert_nothing_raised { java.nio.ByteBuffer.allocate(1) }
   end
 
   # JRUBY-636 and other "extending Java classes"-issues
   class BigInt < java.math.BigInteger
     def initialize(val)
       super(val)
     end
     def test
       "Bit count = #{bitCount}"
     end
   end
 
   def test_extend_java_class
     assert_equal 2, BigInt.new("10").bitCount
     assert_equal "Bit count = 2", BigInt.new("10").test
   end
 
   class TestOS < java.io.OutputStream
     attr_reader :written
     def write(p)
       @written = true
     end
   end
 
   def test_extend_output_stream
     _anos = TestOS.new
     bos = java.io.BufferedOutputStream.new _anos
     bos.write 32
     bos.flush
     assert _anos.written
   end
 
   def test_impl_shortcut
     has_run = false
     java.lang.Runnable.impl do
       has_run = true
     end.run
 
     assert has_run
   end
 
   # JRUBY-674
   OuterClass = org.jruby.javasupport.test.OuterClass
   def test_inner_class_proxies
     assert defined?(OuterClass::PublicStaticInnerClass)
     assert OuterClass::PublicStaticInnerClass.instance_methods.include?("a")
 
     assert !defined?(OuterClass::ProtectedStaticInnerClass)
     assert !defined?(OuterClass::DefaultStaticInnerClass)
     assert !defined?(OuterClass::PrivateStaticInnerClass)
 
     assert defined?(OuterClass::PublicInstanceInnerClass)
     assert OuterClass::PublicInstanceInnerClass.instance_methods.include?("a")
 
     assert !defined?(OuterClass::ProtectedInstanceInnerClass)
     assert !defined?(OuterClass::DefaultInstanceInnerClass)
     assert !defined?(OuterClass::PrivateInstanceInnerClass)
   end
   
   # Test the new "import" syntax
   def test_import
     
     assert_nothing_raised { 
       import java.nio.ByteBuffer
       ByteBuffer.allocate(10)
     }
   end
 
   def test_java_exception_handling
     list = ArrayList.new
     begin
       list.get(5)
       assert(false)
     rescue java.lang.IndexOutOfBoundsException => e
       assert_equal("java.lang.IndexOutOfBoundsException: Index: 5, Size: 0", e.message)
     end
   end
 
   # test for JRUBY-698
   def test_java_method_returns_null
     include_class 'org.jruby.test.ReturnsNull'
     rn = ReturnsNull.new
 
     assert_equal("", rn.returnNull.to_s)
   end
   
   # test for JRUBY-664
   class FinalMethodChildClass < FinalMethodBaseTest
   end
 
   def test_calling_base_class_final_method
     assert_equal("In foo", FinalMethodBaseTest.new.foo)
     assert_equal("In foo", FinalMethodChildClass.new.foo)
   end
 
   # test case for JRUBY-679
   # class Weather < java.util.Observable
   #   def initialize(temp)
   #     super()
   #     @temp = temp
   #   end
   # end
   # class Meteorologist < java.util.Observer
   #   attr_reader :updated
   #   def initialize(weather)
   #     weather.addObserver(self)
   #   end
   #   def update(obs, arg)
   #     @updated = true
   #   end
   # end
   # def test_should_be_able_to_have_different_ctor_arity_between_ruby_subclass_and_java_superclass
   #   assert_nothing_raised do
   #     w = Weather.new(32)
   #     m = Meteorologist.new(w)
   #     w.notifyObservers
   #     assert(m.updated)
   #   end
   # end
   
   class A < java.lang.Object
     include org.jruby.javasupport.test.Interface1
     
     def method1
     end
   end
   A.new
   
   class B < A
   	include org.jruby.javasupport.test.Interface2
   	
   	def method2
   	end
   end
   B.new
   
   class C < B
   end
   C.new
  
   def test_interface_methods_seen
      ci = org.jruby.javasupport.test.ConsumeInterfaces.new
      ci.addInterface1(A.new)
      ci.addInterface1(B.new)
      ci.addInterface2(B.new)
      ci.addInterface1(C.new)
      ci.addInterface2(C.new)
   	
   end
   
   class LCTestA < java::lang::Object
     include org::jruby::javasupport::test::Interface1
 
     def method1
     end
   end
   LCTestA.new
   
   class LCTestB < LCTestA
   	include org::jruby::javasupport::test::Interface2
   	
   	def method2
   	end
   end
   LCTestB.new
   
   class java::lang::Object
     def boo
       'boo!'
     end
   end
    
   def test_lowercase_colon_package_syntax
     assert_equal(java::lang::String, java.lang.String)
     assert_equal('boo!', java.lang.String.new('xxx').boo)
     ci = org::jruby::javasupport::test::ConsumeInterfaces.new
     assert_equal('boo!', ci.boo)
     assert_equal('boo!', LCTestA.new.boo)
     assert_equal('boo!', LCTestB.new.boo)
     ci.addInterface1(LCTestA.new)
     ci.addInterface1(LCTestB.new)
     ci.addInterface2(LCTestB.new)
   end
   
   def test_marsal_java_object_fails
     assert_raises(TypeError) { Marshal.dump(java::lang::Object.new) }
   end
 
   def test_string_from_bytes
     assert_equal('foo', String.from_java_bytes('foo'.to_java_bytes))
   end
   
   # JRUBY-2088
   def test_package_notation_with_arguments
     assert_raises(ArgumentError) do 
       java.lang("ABC").String
     end
 
     assert_raises(ArgumentError) do 
       java.lang.String(123)
     end
     
     assert_raises(ArgumentError) do 
       Java::se("foobar").com.Foobar
     end
   end
   
   # JRUBY-1545
   def test_creating_subclass_to_java_interface_raises_type_error 
     assert_raises(TypeError) do 
       eval(<<CLASSDEF)
 class FooXBarBarBar < Java::JavaLang::Runnable
 end
 CLASSDEF
     end
   end
 
   # JRUBY-781
   def test_that_classes_beginning_with_small_letter_can_be_referenced 
     assert_equal Module, org.jruby.test.smallLetterClazz.class
     assert_equal Class, org.jruby.test.smallLetterClass.class
   end
   
   # JRUBY-1076
   def test_package_module_aliased_methods
     assert java.lang.respond_to?(:__constants__)
     assert java.lang.respond_to?(:__methods__)
 
     java.lang.String # ensure java.lang.String has been loaded
     assert java.lang.__constants__.include?('String')
   end
 
   # JRUBY-2106
   def test_package_load_doesnt_set_error
     $! = nil
     undo = javax.swing.undo
     assert_nil($!)
   end
 
   # JRUBY-2106
   def test_top_level_package_load_doesnt_set_error
     $! = nil
     Java::boom
     assert_nil($!)
 
     $! = nil
     Java::Boom
     assert_nil($!)
   end
+  
+  # JRUBY-2169
+  def test_java_class_resource_methods
+    # FIXME? not sure why this works, didn't modify build.xml
+    # to copy this file, yet it finds it anyway
+    props_file = 'test_java_class_resource_methods.properties'
+    
+    # nothing special about this class, selected at random for testing
+    jc = org.jruby.javasupport.test.RubyTestObject.java_class
+    
+    # get resource as URL
+    url = jc.resource(props_file)
+    assert(java.net.URL === url)
+    assert(/^foo=bar/ =~ java.io.DataInputStream.new(url.content).read_line)
+
+    # get resource as stream
+    is = jc.resource_as_stream(props_file)
+    assert(java.io.InputStream === is)
+    assert(/^foo=bar/ =~ java.io.DataInputStream.new(is).read_line)
+    
+
+    # get resource as string
+    str = jc.resource_as_string(props_file)
+    assert(/^foo=bar/ =~ str)
+  end
+  
+  # JRUBY-2169
+  def test_ji_extended_methods_for_java_1_5
+    jc = java.lang.String.java_class
+    ctor = jc.constructors[0]
+    meth = jc.java_instance_methods[0]
+    field = jc.fields[0]
+    
+    # annotations
+    assert(Annotation[] === jc.annotations)
+    assert(Annotation[] === ctor.annotations)
+    assert(Annotation[] === meth.annotations)
+    assert(Annotation[] === field.annotations)
+    
+    # TODO: more extended methods to test
+    
+    
+  end
+  
+  # JRUBY-2169
+  def test_java_class_ruby_class
+    assert java.lang.Object.java_class.ruby_class == java.lang.Object
+    assert java.lang.Runnable.java_class.ruby_class == java.lang.Runnable
+  end
 end
