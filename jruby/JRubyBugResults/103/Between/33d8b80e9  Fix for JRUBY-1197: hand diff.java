diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index c48003de29..f96ecb9055 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -1,865 +1,865 @@
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
 import java.util.Iterator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.Proxy;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.RubyTime;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.ByteList;
 import org.jruby.util.ClassProvider;
 import org.jruby.util.collections.IntHashMap;
 
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
         javaModule.defineModuleFunction("set_deprecated_interface_syntax", callbackFactory.getSingletonMethod("set_deprecated_interface_syntax", IRubyObject.class));
 
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
         javaUtils.defineFastModuleFunction("add_proxy_extender", callbackFactory.getFastSingletonMethod("add_proxy_extender", IRubyObject.class));
 
         javaUtils.dataWrapStruct(new ProxyData(callbackFactory.getFastSingletonMethod("concrete_proxy_inherited", IRubyObject.class)));
 
         JavaArrayUtilities.createJavaArrayUtilitiesModule(runtime);
         
         RubyClass javaProxy = runtime.defineClass("JavaProxy", runtime.getObject(), runtime.getObject().getAllocator());
         javaProxy.getMetaClass().defineFastMethod("new_instance_for", callbackFactory.getFastSingletonMethod("new_instance_for", IRubyObject.class));
         javaProxy.getMetaClass().defineFastMethod("to_java_object", callbackFactory.getFastSingletonMethod("to_java_object"));
 
         return javaModule;
     }
 
     private static class JavaPackageClassProvider implements ClassProvider {
         public RubyClass defineClassUnder(RubyModule pkg, String name, RubyClass superClazz) {
             // shouldn't happen, but if a superclass is specified, it's not ours
             if (superClazz != null) {
                 return null;
             }
             IRubyObject packageName = pkg.getInstanceVariable("@package_name");
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if (packageName == null) return null;
 
             Ruby runtime = pkg.getRuntime();
             String className = packageName.asSymbol() + name;
             JavaClass javaClass = JavaClass.forName(runtime, className);
             return (RubyClass)get_proxy_class(runtime.getJavaSupport().getJavaUtilitiesModule(), javaClass);
         }
     }
     
     private static final ClassProvider JAVA_PACKAGE_CLASS_PROVIDER = new JavaPackageClassProvider();
         
     // JavaProxy
     public static IRubyObject new_instance_for(IRubyObject recv, IRubyObject java_object) {
         IRubyObject new_instance = ((RubyClass)recv).allocate();
         new_instance.setInstanceVariable("@java_object",java_object);
         return new_instance;
     }
 
     // If the proxy class itself is passed as a parameter this will be called by Java#ruby_to_java    
     public static IRubyObject to_java_object(IRubyObject recv) {
         return recv.getInstanceVariable("@java_class");
     }
 
     private final static class ProxyData {
         public final IntHashMap classes = new IntHashMap();
         public final IntHashMap interfaces = new IntHashMap();
         public final List extenders = new ArrayList();
         public final Map matchCache = new HashMap();
         public final Callback callback;
         public ProxyData(Callback c) { this.callback = c; };
     }
 
     // JavaUtilities
     
     /**
      * Add a new proxy extender. This is used by JavaUtilities to allow adding methods
      * to a given type's proxy and all types descending from that proxy's Java class.
      */
     public static IRubyObject add_proxy_extender(IRubyObject recv, IRubyObject extender) {
         ProxyData pdata = ((ProxyData)recv.dataGetStruct());
         pdata.extenders.add(extender);
         ThreadContext tc = recv.getRuntime().getCurrentContext();
         for(Iterator iter = pdata.classes.values().iterator(); iter.hasNext(); ) {
             extender.callMethod(tc, "extend_proxy", (IRubyObject)iter.next());
         }
         for(Iterator iter = pdata.interfaces.values().iterator(); iter.hasNext(); ) {
             extender.callMethod(tc, "extend_proxy", (IRubyObject)iter.next());
         }
         return recv.getRuntime().getNil();
     }
     
     private static boolean supportExtendableInterfaces = false;
     private static boolean supportExtendableInterfaces() {
         // TODO: some kind of user mechanism to enable this
         return supportExtendableInterfaces;
     }
     public static IRubyObject set_deprecated_interface_syntax(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         supportExtendableInterfaces = object.isTrue();
         return object;
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
         if ( !javaClass.javaClass().isInterface()) {
             throw runtime.newArgumentError(javaClass.toString() + " is not an interface");
         }
         int class_id = RubyNumeric.fix2int(javaClass.id());
         ProxyData pdata = ((ProxyData)recv.dataGetStruct());
         IntHashMap interfaces = pdata.interfaces;
         RubyModule interfaceModule;
         synchronized(javaClass) {
             if ((interfaceModule = (RubyModule)interfaces.get(class_id)) == null) {
                 interfaceModule = (RubyModule)runtime.getJavaSupport().getJavaInterfaceTemplate().dup();
                 interfaceModule.setInstanceVariable("@java_class",javaClass);
                 addToJavaPackageModule(interfaceModule,javaClass);
                 interfaces.put(class_id,interfaceModule);
                 javaClass.setupInterfaceModule(interfaceModule);
                 // include any interfaces we extend
                 Class[] extended = javaClass.javaClass().getInterfaces();
                 for (int i = extended.length; --i >= 0; ) {
                     JavaClass extendedClass = JavaClass.get(runtime,extended[i]);
                     RubyModule extModule;
                     if ((extModule =
                             (RubyModule)interfaces.get(RubyNumeric.fix2int(extendedClass.id()))) == null) {
                         extModule = (RubyModule)get_interface_module(recv,extendedClass);
                     }
                     interfaceModule.includeModule(extModule);
                 }
                 for(Iterator iter = pdata.extenders.iterator(); iter.hasNext(); ) {
                     ((IRubyObject)iter.next()).callMethod(
                             runtime.getCurrentContext(), "extend_proxy", interfaceModule);
                 }
             }
         }
         return interfaceModule;
     }
 
     // Note: this isn't really all that deprecated, as it is used for
     // internal purposes, at least for now. But users should be discouraged
     // from calling this directly; eventually it will go away.
     public static IRubyObject get_deprecated_interface_proxy(IRubyObject recv, IRubyObject java_class_object) {
         Ruby runtime = recv.getRuntime();
         JavaClass java_class;
         if (java_class_object instanceof RubyString) {
             java_class = JavaClass.for_name(recv, java_class_object);
         } else if (java_class_object instanceof JavaClass) {
             java_class = (JavaClass)java_class_object;
         } else  {
             throw runtime.newArgumentError("expected JavaClass, got " + java_class_object);
         }
         if (!java_class.javaClass().isInterface()) {
             throw runtime.newArgumentError("expected Java interface class, got " + java_class_object);
         }
         int class_id = RubyNumeric.fix2int(java_class.id());
         ProxyData pdata = ((ProxyData)recv.dataGetStruct());
         IntHashMap proxy_classes = pdata.classes;
         RubyClass proxy_class;
         synchronized(java_class) {
             if((proxy_class = (RubyClass)proxy_classes.get(class_id)) == null) {
                 RubyModule interfaceModule = (RubyModule)get_interface_module(recv,java_class);
                 proxy_class = createProxyClass(recv,runtime.getClass("InterfaceJavaProxy"),
                         java_class,true,proxy_classes,class_id);
                 // including interface module so old-style interface "subclasses" will
                 // respond correctly to #kind_of?, etc.
                 proxy_class.includeModule(interfaceModule);
                 // add reference to interface module
                 if (proxy_class.getConstantAt("Includable") == null) {
                     proxy_class.const_set(runtime.newSymbol("Includable"),interfaceModule);
                 }
 
                 for(Iterator iter = pdata.extenders.iterator(); iter.hasNext(); ) {
                     ((IRubyObject)iter.next()).callMethod(
                             runtime.getCurrentContext(), "extend_proxy", proxy_class);
                 }
             }
         }
         return proxy_class;
     }
 
     public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject java_class_object) {
         Ruby runtime = recv.getRuntime();
         JavaClass java_class;
         if (java_class_object instanceof RubyString) {
             java_class = JavaClass.for_name(recv, java_class_object);
         } else if (java_class_object instanceof JavaClass) {
             java_class = (JavaClass)java_class_object;
         } else  {
             throw runtime.newArgumentError("expected JavaClass, got " + java_class_object);
         }
         Class c;
         if ((c = java_class.javaClass()).isInterface() && !supportExtendableInterfaces()) {
             return get_interface_module(recv,java_class);
         }
         int class_id = RubyNumeric.fix2int(java_class.id());
         ProxyData pdata = ((ProxyData)recv.dataGetStruct());
         IntHashMap proxy_classes = pdata.classes;
         RubyClass proxy_class;
         synchronized(java_class) {
             if((proxy_class = (RubyClass)proxy_classes.get(class_id)) == null) {
 
                 if(c.isInterface()) {
                     RubyModule interfaceModule = (RubyModule)get_interface_module(recv,java_class);
                     // backwards compatibility mode
                     proxy_class = createProxyClass(recv,
                             runtime.getClass("InterfaceJavaProxy"),
                             java_class, true, proxy_classes, class_id);
                     // including interface module so old-style interface "subclasses" will
                     // respond correctly to #kind_of?, etc.
                     proxy_class.includeModule(interfaceModule);
                     // add reference to interface module
                     if (proxy_class.getConstantAt("Includable") == null) {
                         proxy_class.const_set(runtime.newSymbol("Includable"),interfaceModule);
                     }
                     
                 } else if(c.isArray()) {
                     proxy_class = createProxyClass(recv,
                             runtime.getJavaSupport().getArrayProxyClass(),
                             java_class, true, proxy_classes, class_id);
 
                 } else if (c.isPrimitive()) {
                     proxy_class = createProxyClass(recv,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             java_class, true, proxy_classes, class_id);
 
                 } else if (c == Object.class) {
                     // java.lang.Object is added at root of java proxy classes
                     proxy_class = createProxyClass(recv,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             java_class, true, proxy_classes, class_id);
                     proxy_class.getMetaClass().defineFastMethod("inherited", pdata.callback);
                     addToJavaPackageModule(proxy_class, java_class);
 
                 } else {
                     // other java proxy classes added under their superclass' java proxy
                     proxy_class = createProxyClass(recv,
                             get_proxy_class(recv,runtime.newString(c.getSuperclass().getName())),
                             java_class, false, proxy_classes, class_id);
 
                     // include interface modules into the proxy class
                     Class[] interfaces = c.getInterfaces();
                     for (int i = interfaces.length; --i >= 0; ) {
                         JavaClass ifc = JavaClass.get(runtime,interfaces[i]);
                         proxy_class.includeModule(get_interface_module(recv,ifc));
                     }
                     if (Modifier.isPublic(c.getModifiers())) {
                         addToJavaPackageModule(proxy_class, java_class);
                     }
                 }
                 for(Iterator iter = pdata.extenders.iterator(); iter.hasNext(); ) {
                     ((IRubyObject)iter.next()).callMethod(
                             runtime.getCurrentContext(), "extend_proxy", proxy_class);
                 }
             }
         }
         return proxy_class;
     }
     
     private static RubyClass createProxyClass(IRubyObject recv, IRubyObject baseType,
             JavaClass javaClass, boolean invokeInherited, IntHashMap proxyClasses, int classId ) {
         RubyClass proxyClass = RubyClass.newClass(recv, new IRubyObject[]{ baseType },
                 Block.NULL_BLOCK, invokeInherited);
         proxyClass.callMethod(recv.getRuntime().getCurrentContext(), "java_class=", javaClass);
         proxyClasses.put(classId, proxyClass);
         // We do not setup the proxy before we register it so that same-typed constants do
         // not try and create a fresh proxy class and go into an infinite loop
         javaClass.setupProxy(proxyClass);
         return proxyClass;
     }
 
     public static IRubyObject concrete_proxy_inherited(IRubyObject recv, IRubyObject subclass) {
         Ruby runtime = recv.getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         JavaSupport javaSupport = runtime.getJavaSupport();
         RubyClass javaProxyClass = javaSupport.getJavaProxyClass().getMetaClass();
         recv.callMethod(tc,javaProxyClass, "inherited", new IRubyObject[]{subclass},
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
 
     private static final IRubyObject[] EMPTY_ARGS = new IRubyObject[0];
 
     private static final HashSet PACKAGE_MODULE_KEEP_METHODS = new HashSet();
     static {
         // keeping some methods we must have, and some others that are not strictly
         // necessary, but won't collide with any potential package names. note that
         // inspect, to_s and respond_to? are needed for console display; otherwise
         // exceptions are raised.
         final String[] methods = {
                 "<", "<=", "<=>", "==", "===", ">", ">=", "=~", "__id__", "__send__",
                 "autoload?", "class", "const_defined?", "const_missing", "eql?",
                 "equal?", "frozen?", "include?", "inspect", "instance_of?", "is_a?",
                 "kind_of?", "method_defined?", "method_missing", "nil?", "respond_to?",
                 "tainted?", "to_s"
                 };
         for (int i = methods.length; --i >= 0; ) {
             PACKAGE_MODULE_KEEP_METHODS.add(methods[i]);
         }
     }
     
     private static RubyModule createPackageModule(RubyModule parent, String name, String packageString) {
         Ruby runtime = parent.getRuntime();
         RubyModule packageModule = (RubyModule)runtime.getJavaSupport()
                 .getPackageModuleTemplate().dup();
         packageModule.setInstanceVariable("@package_name",runtime.newString(
                 packageString.length() > 0 ? packageString + '.' : packageString));
 
         // this is where we'll get connected when classes are opened using
         // package module syntax.
         packageModule.addClassProvider(JAVA_PACKAGE_CLASS_PROVIDER);
 
         // undefined methods aren't carried over when the template is dup'ed
         // (see JRUBY-948), so we'll undef them here.
         // TODO: need to catch later additions to Object/Kernel. yuk. that's a lot
         // of processing for method_added et al... 
         RubyClass singleton = packageModule.getSingletonClass();
         Object[] snglMeths = singleton.public_instance_methods(EMPTY_ARGS).toArray();
         java.util.Arrays.sort(snglMeths);
         for (int i = snglMeths.length; --i >= 0; ) {
             String methodName = (String)snglMeths[i];
             if (!PACKAGE_MODULE_KEEP_METHODS.contains(methodName)) {
                 singleton.undef(methodName);
             }
         }
 
         parent.const_set(runtime.newSymbol(name), packageModule);
         return packageModule;
     }
     
-    private static final Pattern CAMEL_CASE_PACKAGE_SPLITTER = Pattern.compile("([a-z])([A-Z])");        
+    private static final Pattern CAMEL_CASE_PACKAGE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");
 
     public static IRubyObject get_package_module(IRubyObject recv, IRubyObject symObject) {
         String sym = symObject.asSymbol();
         RubyModule javaModule = recv.getRuntime().getJavaSupport().getJavaModule();
         IRubyObject value;
         if ((value = javaModule.getConstantAt(sym)) != null) {
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
         RubyModule module = getJavaPackageModule(runtime, dottedName.asSymbol());
         return module == null ? runtime.getNil() : module;
     }
     
     
     public static IRubyObject matching_method(IRubyObject recv, IRubyObject methods, IRubyObject args) {
         Map matchCache = ((ProxyData)recv.dataGetStruct()).matchCache;
 
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
         Map matchCache = ((ProxyData)recv.dataGetStruct()).matchCache;
 
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
         return new_instance_for(get_proxy_class(recv, ((JavaObject)java_object).java_class()),java_object);
     }
 
 	// Java methods
     public static IRubyObject define_exception_handler(IRubyObject recv, IRubyObject[] args, Block block) {
         String name = args[0].toString();
         RubyProc handler = null;
         if (args.length > 1) {
             handler = (RubyProc)args[1];
         } else {
             handler = recv.getRuntime().newProc(false, block);
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
         	object = JavaUtil.convertJavaToRuby(recv.getRuntime(), ((JavaObject) object).getValue());
 
             //if (object.isKindOf(recv.getRuntime().getModule("Java").getClass("JavaObject"))) {
             if(object instanceof JavaObject) {
                 return wrap(recv.getRuntime().getJavaSupport().getJavaUtilitiesModule(), object);
             }
         }
 
 		return object;
     }
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     /**
      * High-level object conversion utility. 
      */
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
     	if(object.respondsTo("to_java_object")) {
             IRubyObject result = object.getInstanceVariable("@java_object");
             if(result == null) {
                 result = object.callMethod(recv.getRuntime().getCurrentContext(), "to_java_object");
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
     		proc = recv.getRuntime().newProc(false, block);
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
         
         return JavaObject.wrap(recv.getRuntime(), Proxy.newProxyInstance(recv.getRuntime().getJavaSupport().getJavaClassLoader(), interfaces, new InvocationHandler() {
             private Map parameterTypeCache = new HashMap();
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
index 09241708d3..ae22fcc4b8 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,1494 +1,1494 @@
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
 
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.beans.PropertyDescriptor;
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubyMethod;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.exceptions.RaiseException;
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
 
     private static boolean DEBUG = false;
 
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
         void logMessage(IRubyObject self, IRubyObject[] args) {
             if (!DEBUG) {
                 return;
             }
             String type;
             switch (this.type) {
             case STATIC_FIELD: type = "static field"; break;
             case STATIC_METHOD: type = "static method"; break;
             case INSTANCE_FIELD: type = "instance field"; break;
             case INSTANCE_METHOD: type = "instance method"; break;
             default: type = "?"; break;
             }
             StringBuffer b = new StringBuffer(type).append(" => '").append(name)
                 .append("'; args.length = ").append(args.length);
             for (int i = 0; i < args.length; i++) {
                 b.append("\n   arg[").append(i).append("] = ").append(args[i]);
             }
             System.out.println(b);
         }
     }
 
     private static abstract class FieldCallback extends NamedCallback {
         Field field;
         JavaField javaField;
         FieldCallback(){}
         FieldCallback(String name, int type, Field field) {
             super(name,type);
             this.field = field;
 //            if (Modifier.isProtected(field.getModifiers())) {
 //                field.setAccessible(true);
 //                this.visibility = Visibility.PROTECTED;
 //            }
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
             logMessage(self,args);
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
             logMessage(self,args);
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
             logMessage(self,args);
             if (javaField == null) {
                 javaField = new JavaField(getRuntime(),field);
             }
             return Java.java_to_ruby(self,
                     javaField.value(self.getInstanceVariable("@java_object")),
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
             logMessage(self,args);
             if (javaField == null) {
                 javaField = new JavaField(getRuntime(),field);
             }
             return Java.java_to_ruby(self,
                     javaField.set_value(self.getInstanceVariable("@java_object"),
                             Java.ruby_to_java(self,args[0],Block.NULL_BLOCK)),
                     Block.NULL_BLOCK);
         }
         public Arity getArity() {
             return Arity.ONE_ARGUMENT;
         }
     }
 
     private static abstract class MethodCallback extends NamedCallback {
         boolean haveLocalMethod;
         List methods;
         List aliases;
         IntHashMap javaMethods;
         IntHashMap matchingMethods;
         JavaMethod javaMethod;
         MethodCallback(){}
         MethodCallback(String name, int type) {
             super(name,type);
         }
         void addMethod(Method method, Class javaClass) {
             if (methods == null) {
                 methods = new ArrayList();
             }
             methods.add(method);
 //            if (Modifier.isProtected(method.getModifiers())) {
 //                visibility = Visibility.PROTECTED;
 //            }
             haveLocalMethod |= javaClass == method.getDeclaringClass();
         }
         void addAlias(String alias) {
             if (aliases == null) {
                 aliases = new ArrayList();
             }
             if (!aliases.contains(alias))
                 aliases.add(alias);
         }
         boolean hasLocalMethod () {
             return haveLocalMethod;
         }
         // TODO: varargs?
         // TODO: rework Java.matching_methods_internal and
         // ProxyData.method_cache, since we really don't need to be passing
         // around RubyArray objects anymore.
         void createJavaMethods(Ruby runtime) {
             if (methods != null) {
                 if (methods.size() == 1) {
                     javaMethod = JavaMethod.create(runtime,(Method)methods.get(0));
                 } else {
                     javaMethods = new IntHashMap();
                     matchingMethods = new IntHashMap(); 
                     for (Iterator iter = methods.iterator(); iter.hasNext() ;) {
                         Method method = (Method)iter.next();
                         // TODO: deal with varargs
                         //int arity = method.isVarArgs() ? -1 : method.getParameterTypes().length;
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
             if (haveLocalMethod) {
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
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             logMessage(self,args);
             if (javaMethod == null && javaMethods == null) {
                 createJavaMethods(self.getRuntime());
             }
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
             if (haveLocalMethod) {
                 proxy.defineFastMethod(this.name,this,this.visibility);
                 if (aliases != null && isPublic()) {
                     for (Iterator iter = aliases.iterator(); iter.hasNext(); ) {
                         proxy.defineAlias((String)iter.next(), this.name);
                     }
                     aliases = null;
                 }
             }
         }
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             logMessage(self,args);
             if (javaMethod == null && javaMethods == null) {
                 createJavaMethods(self.getRuntime());
             }
             // TODO: ok to convert args in place, rather than new array?
             int len = args.length;
             if (block.isGiven()) { // convert block to argument
                 len += 1;
                 IRubyObject[] newArgs = new IRubyObject[args.length+1];
                 System.arraycopy(args, 0, newArgs, 0, args.length);
                 newArgs[args.length] = RubyProc.newProc(self.getRuntime(), block, true);
                 args = newArgs;
             }
             IRubyObject[] convertedArgs = new IRubyObject[len+1];
             convertedArgs[0] = self.getInstanceVariable("@java_object");
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
         Field field;
         ConstantField(Field field) {
             this.field = field;
         }
         void install(RubyModule proxy) {
             if (proxy.getConstantAt(field.getName()) == null) {
                 JavaField javaField = new JavaField(proxy.getRuntime(),field);
                 RubyString name = javaField.name();
                 proxy.const_set(name,Java.java_to_ruby(proxy,javaField.static_value(),Block.NULL_BLOCK));
             }
         }
         static boolean isConstant(Field field) {
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
     
     public void setupProxy(RubyClass proxy) {
         proxy.defineFastMethod("__jsend!", __jsend_method);
         Class javaClass = javaClass();
         if (javaClass.isInterface()) {
             setupInterfaceProxy(proxy);
             return;
         } else if (javaClass.isArray() || javaClass.isPrimitive()) {
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
                     proxy.const_set(getRuntime().newString(simpleName),
                         Java.get_proxy_class(JAVA_UTILITIES,get(getRuntime(),clazz)));
                 }
             }
         }
         // TODO: we can probably release our references to the constantFields
         // array and static/instance callback hashes at this point. I don't see
         // a case where the proxy would be GC'd and we'd have to reinitialize.
         // I suppose we could keep a reference to the proxy just to be sure...
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
                     addUnassignedAlias(getRubyCasedName(javaPropertyName),assignedNames,callback);
                     addUnassignedAlias(javaPropertyName,assignedNames,callback);
                 } else if (resultType == boolean.class && name.startsWith("is")) {
                     String rubyName = getRubyCasedName(name.substring(2));
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
                     addUnassignedAlias(getRubyCasedName(name.substring(3)),assignedNames,callback);
                     addUnassignedAlias(javaPropertyName,assignedNames,callback);
                 } else if (resultType == void.class && name.startsWith("set")) {
                     String rubyName = getRubyCasedName(name.substring(3));
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
 
-    private static final Pattern CAMEL_CASE_SPLITTER = Pattern.compile("([a-z])([A-Z])");    
+    private static final Pattern CAMEL_CASE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");    
     public static String getRubyCasedName(String javaCasedName) {
         Matcher m = CAMEL_CASE_SPLITTER.matcher(javaCasedName);
         String rubyCasedName = m.replaceAll("$1_$2").toLowerCase();
         if (rubyCasedName.equals(javaCasedName)) {
             return null;
         }
         return rubyCasedName;
     }
     
     
     public void setupInterfaceProxy(RubyClass proxy) {
         Class javaClass = javaClass();
         for (Iterator iter = constantFields.iterator(); iter.hasNext(); ){
             ((ConstantField)iter.next()).install(proxy);
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
                     proxy.const_set(getRuntime().newString(simpleName),
                         Java.get_proxy_class(JAVA_UTILITIES,get(getRuntime(),clazz)));
                 }
             }
         }
     }
     
     public void setupInterfaceModule(RubyModule module) {
         Class javaClass = javaClass();
         for (Iterator iter = constantFields.iterator(); iter.hasNext(); ){
             ((ConstantField)iter.next()).install(module);
         }
         // setup constants for public inner classes
         Class[] classes = javaClass.getClasses();
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
     }
 
     // unsynchronized, so create won't hold up get by other threads
     public static JavaClass get(Ruby runtime, Class klass) {
         JavaClass javaClass = runtime.getJavaSupport().getJavaClassFromCache(klass);
         if (javaClass == null) {
             javaClass = createJavaClass(runtime,klass);
         }
         return javaClass;
     }
 
     private static synchronized JavaClass createJavaClass(Ruby runtime,Class klass) {
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
         RubyClass result = javaModule.defineClassUnder("JavaClass", javaModule.getClass("JavaObject"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR); 
 
     	CallbackFactory callbackFactory = runtime.callbackFactory(JavaClass.class);
         
         result.includeModule(runtime.getModule("Comparable"));
         
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
         result.defineFastMethod("define_instance_methods_for_proxy", 
                 callbackFactory.getFastMethod("define_instance_methods_for_proxy", IRubyObject.class));
         
         result.getMetaClass().undefineMethod("new");
         result.getMetaClass().undefineMethod("allocate");
 
         return result;
     }
     
     public static synchronized JavaClass forName(Ruby runtime, String className) {
         Class klass = runtime.getJavaSupport().loadJavaClass(className);
         return JavaClass.get(runtime, klass);
     }
 
     public static JavaClass for_name(IRubyObject recv, IRubyObject name) {
         return forName(recv.getRuntime(), name.asSymbol());
     }
     
     // TODO: part of interim solution, can be removed soon
     private Set getPublicFieldNames(boolean isStatic) {
         // we're not checking for final here; since the purpose is to prevent
         // shortcut property names from being created that conflict with
         // field names, it won't make sense to allow the property setter (name=)
         // to be created but not the getter.
         int mask = Modifier.PUBLIC | Modifier.STATIC;
         int want = isStatic ? Modifier.PUBLIC | Modifier.STATIC : Modifier.PUBLIC;
         Set names = new HashSet();
         names.add("class");
         Field[] fields = ((Class)getValue()).getFields();
         for (int i = fields.length; --i >= 0; ) {
             if ((fields[i].getModifiers() & mask) == want) {
                 names.add(fields[i].getName());
             }
         }
         return names;
     }
     
     /**
      *  Get all methods grouped by name (e.g. 'new => {new(), new(int), new(int, int)}, ...')
      *  @param isStatic determines whether you want static or instance methods from the class
      */
     private Map getMethodsClumped(boolean isStatic, Set assignedNames) {
         System.out.println("JC.gmc");
         Map map = new HashMap();
         if(((Class)getValue()).isInterface()) {
             return map;
         }
 
         Method methods[] = javaClass().getMethods();
         
         for (int i = 0; i < methods.length; i++) {
             if (isStatic != Modifier.isStatic(methods[i].getModifiers())) {
                 continue;
             }
             
             String key = methods[i].getName();
             RubyArray methodsWithName = (RubyArray) map.get(key); 
             
             if (methodsWithName == null) {
                 methodsWithName = RubyArray.newArrayLight(getRuntime());
                 map.put(key, methodsWithName);
                 assignedNames.add(key);
             }
             
             methodsWithName.append(JavaMethod.create(getRuntime(), methods[i]));
         }
         
         return map;
     }
     
     private Map getPropertysClumped(Set assignedNames) {
         System.out.println("JC.gpc");
         Map map = new HashMap();
         BeanInfo info;
         
         try {
             info = Introspector.getBeanInfo(javaClass());
         } catch (IntrospectionException e) {
             return map;
         }
         
         PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
         System.out.println("got bean info for class " + javaClass().getName() + ": " + descriptors.length);
         for (int i = 0; i < descriptors.length; i++) {
             Method readMethod = descriptors[i].getReadMethod();
             
             if (readMethod != null) {
                 String key = readMethod.getName();
                 List aliases = (List) map.get(key);
                 
                 if (aliases == null) {
                     aliases = new ArrayList();
                     
                     map.put(key, aliases);    
                 }
 
                 if (readMethod.getReturnType() == Boolean.class ||
                     readMethod.getReturnType() == boolean.class) {
                     aliases.add(descriptors[i].getName() + "?");
                 }
                 if (!assignedNames.contains(descriptors[i].getName())) {
                     aliases.add(descriptors[i].getName());
                 }
             }
             
             Method writeMethod = descriptors[i].getWriteMethod();
 
             if (writeMethod != null) {
                 String key = writeMethod.getName();
                 List aliases = (List) map.get(key);
                 
                 if (aliases == null) {
                     aliases = new ArrayList();
                     map.put(key, aliases);
                 }
                 
                 if (!assignedNames.contains(descriptors[i].getName())) {
                     aliases.add(descriptors[i].getName()  + "=");
                 }
             }
         }
         
         return map;
     }
     
     private void define_instance_method_for_proxy(final RubyClass proxy, List names, 
             final RubyArray methods) {
         final RubyModule javaUtilities = JAVA_UTILITIES;
         Callback method;
         if(methods.size()>1) {
             method = new Callback() {
                     private IntHashMap matchingMethods = new IntHashMap();
                     public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                         int len = args.length;
                         IRubyObject[] argsArray = new IRubyObject[len + 1];
                 
                         argsArray[0] = self.getInstanceVariable("@java_object");
 
                         int argsTypeHash = 0;
                         for (int j = 0; j < len; j++) {
                             argsArray[j+1] = Java.ruby_to_java(proxy, args[j], Block.NULL_BLOCK);
                             argsTypeHash += 3*args[j].getMetaClass().id;
                         }
 
                         IRubyObject match = (IRubyObject)matchingMethods.get(argsTypeHash);
                         if (match == null) {
                             match = Java.matching_method_internal(javaUtilities, methods, argsArray, 1, len);
                             matchingMethods.put(argsTypeHash, match);
                         }
 
                         return Java.java_to_ruby(self, ((JavaMethod)match).invoke(argsArray), Block.NULL_BLOCK);
                     }
 
                     public Arity getArity() {
                         return Arity.optional();
                     }
                 };
         } else {
             final JavaMethod METHOD = (JavaMethod)methods.eltInternal(0);
             method = new Callback() {
                     public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                         int len = args.length;
                         IRubyObject[] argsArray = new IRubyObject[len + 1];
                         argsArray[0] = self.getInstanceVariable("@java_object");
                         for(int j = 0; j < len; j++) {
                             argsArray[j+1] = Java.ruby_to_java(proxy, args[j], Block.NULL_BLOCK);
                         }
                         return Java.java_to_ruby(self, METHOD.invoke(argsArray), Block.NULL_BLOCK);
                     }
 
                     public Arity getArity() {
                         return Arity.optional();
                     }
                 };
         }
         
         for(Iterator iter = names.iterator(); iter.hasNext(); ) {
             String methodName = (String) iter.next();
             
             // We do not override class since it is too important to be overridden by getClass
             // short name.
             if (!methodName.equals("class")) {
                 proxy.defineFastMethod(methodName, method);
                 
                 String rubyCasedName = getRubyCasedName(methodName);
                 if (rubyCasedName != null) {
                     proxy.defineAlias(rubyCasedName, methodName);
                 }
             }
         }
     }
     
     private static final Callback __jsend_method = new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 String name = args[0].asSymbol();
                 
                 // FIXME: why newMethod ?
                 RubyMethod method = (org.jruby.RubyMethod)self.getMetaClass().newMethod(self, name, true);
                 int v = RubyNumeric.fix2int(method.arity());
 
                 IRubyObject[] newArgs = new IRubyObject[args.length - 1];
                 System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                 if (DEBUG)
                     System.out.println("__jsend method => '" + name + "'; arity = " + v + ", args.length = " + newArgs.length);
                 
                 if(v < 0 || v == (newArgs.length)) {
                     if (DEBUG)
                         System.out.println("  calling self");
                     return self.callMethod(self.getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL, block);
                 } else {
                     if (DEBUG)
                         System.out.println("  calling super");
                     return self.callMethod(self.getRuntime().getCurrentContext(),self.getMetaClass().getSuperClass(), name, newArgs, CallType.SUPER, block);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         };
 
     public IRubyObject define_instance_methods_for_proxy(IRubyObject arg) {
         assert arg instanceof RubyClass;
         // shouldn't be getting called any more
         System.out.println("JC.define_instance_methods_for_proxy");
         Set assignedNames = getPublicFieldNames(false);
         Map methodsClump = getMethodsClumped(false,assignedNames);
         Map aliasesClump = getPropertysClumped(assignedNames);
         RubyClass proxy = (RubyClass) arg;
 
         proxy.defineFastMethod("__jsend!", __jsend_method);
         
         for (Iterator iter = methodsClump.keySet().iterator(); iter.hasNext(); ) {
             String name = (String) iter.next();
             RubyArray methods = (RubyArray) methodsClump.get(name);
             List aliases = (List) aliasesClump.get(name);
 
             if (aliases == null) {
                 aliases = new ArrayList();
             }
 
             aliases.add(name);
             
             define_instance_method_for_proxy(proxy, aliases, methods);
         }
         
         return getRuntime().getNil();
     }
 
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
         String methodName = args[0].asSymbol();
         Class[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.create(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     public JavaMethod declared_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asSymbol();
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
        // TODO: should be able to partially work around this by calling
        // javaClass().getClasses, which will return any public inner classes.
        if (Ruby.isSecurityRestricted()) // Can't even get inner classes?
            return getRuntime().newArray(0);
         Class[] classes = javaClass().getDeclaredClasses();
         List accessibleClasses = new ArrayList();
         for (int i = 0; i < classes.length; i++) {
             if (Modifier.isPublic(classes[i].getModifiers())) {
                 accessibleClasses.add(classes[i]);
             }
         }
         return buildClasses((Class[]) accessibleClasses.toArray(new Class[accessibleClasses.size()]));
     }
     
     private RubyArray buildClasses(Class [] classes) {
         RubyArray result = getRuntime().newArray(classes.length);
         for (int i = 0; i < classes.length; i++) {
             result.append(new JavaClass(getRuntime(), classes[i]));
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
             String name = args[i].asSymbol();
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
                         .newTypeError(dimensionLength, getRuntime().getClass("Integer"));
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
 		String stringName = name.asSymbol();
         try {
             Field field = javaClass().getField(stringName);
 			return new JavaField(getRuntime(),field);
         } catch (NoSuchFieldException nsfe) {
             throw undefinedFieldError(stringName);
         }
     }
 
 	public JavaField declared_field(IRubyObject name) {
 		String stringName = name.asSymbol();
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
