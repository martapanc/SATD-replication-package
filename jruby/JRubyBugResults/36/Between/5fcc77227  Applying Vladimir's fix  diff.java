diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index 97d3ce8f07..b7687c401e 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -1,1046 +1,1060 @@
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
                     JavaClass.forName(runtime, packageName.asJavaString() + name));
         }
         
         public RubyModule defineModuleUnder(RubyModule pkg, String name) {
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) return null;
 
             Ruby runtime = pkg.getRuntime();
             return (RubyModule)get_interface_module(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forName(runtime, packageName.asJavaString() + name));
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
             return runtime.getJavaSupport().getObjectProxyCache()
                 .getOrCreate(rawJavaObject,
                         (RubyClass)getProxyClass(runtime,
                             JavaClass.get(runtime, rawJavaObject.getClass())));
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
                 return getProxyClass(runtime, JavaClass.forName(runtime, fullName));
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
             return getProxyClass(runtime, JavaClass.forName(runtime, fullName));
 
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
                 return getProxyClass(runtime, JavaClass.forName(runtime, name));
+            } catch (RaiseException re) { /* not primitive or lc class */
+                RubyException rubyEx = re.getException();
+                if (rubyEx.kind_of_p(runtime.getStandardError()).isTrue()) {
+                    RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
+                }
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
                 return getProxyClass(runtime, JavaClass.forName(runtime, name));
-            } catch (Exception e) {
-                return getPackageModule(runtime, name);
-            }
+            } catch (RaiseException re) { /* not a class */
+                RubyException rubyEx = re.getException();
+                if (rubyEx.kind_of_p(runtime.getStandardError()).isTrue()) {
+                    RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
+                }
+            } catch (Exception e) { /* not a class */ }
+
+            // upper-case package name
+            // TODO: top-level upper-case package was supported in the previous (Ruby-based)
+            // implementation, so leaving as is.  see note at #getProxyOrPackageUnderPackage
+            // re: future approach below the top-level.
+            return getPackageModule(runtime, name);
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
         return getInstance(recv.getRuntime(), ((JavaObject)java_object).getValue());
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
diff --git a/test/test_higher_javasupport.rb b/test/test_higher_javasupport.rb
index e01e5fa925..3da7683573 100644
--- a/test/test_higher_javasupport.rb
+++ b/test/test_higher_javasupport.rb
@@ -1,682 +1,693 @@
 require 'java'
 require 'test/unit'
 
 TopLevelConstantExistsProc = Proc.new do
   include_class 'java.lang.String'
 end
 
 class TestHigherJavasupport < Test::Unit::TestCase
   TestHelper = org.jruby.test.TestHelper
   JArray = ArrayList = java.util.ArrayList
   FinalMethodBaseTest = org.jruby.test.FinalMethodBaseTest
 
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
-  def test_package_load_doesn_set_error
+  def test_package_load_doesnt_set_error
     $! = nil
     undo = javax.swing.undo
     assert_nil($!)
   end
+
+  # JRUBY-2106
+  def test_top_level_package_load_doesnt_set_error
+    $! = nil
+    Java::boom
+    assert_nil($!)
+
+    $! = nil
+    Java::Boom
+    assert_nil($!)
+  end
 end
