diff --git a/spec/java_integration/types/extension_spec.rb b/spec/java_integration/types/extension_spec.rb
index 77dcc08913..a459e178de 100644
--- a/spec/java_integration/types/extension_spec.rb
+++ b/spec/java_integration/types/extension_spec.rb
@@ -1,16 +1,25 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 import "java.util.ArrayList"
 
 describe "A Ruby subclass of a Java concrete class" do
   it "should allow access to the proxy object for the class" do
     my_arraylist = Class.new(ArrayList)
     lambda { my_arraylist.java_proxy_class }.should_not raise_error
   end
 
   it "should allow access to the actual generated class via java_class" do
     my_arraylist = Class.new(ArrayList)
     class_name = my_arraylist.java_proxy_class.to_s
     class_name.index('Proxy').should_not == -1
   end
 end
+
+describe "A final Java class" do
+  it "should not be allowed as a superclass" do
+    lambda do
+      substring = Class.new(java.lang.String)
+      substring.new
+    end.should raise_error(TypeError)
+  end
+end
diff --git a/src/org/jruby/javasupport/proxy/JavaProxyClass.java b/src/org/jruby/javasupport/proxy/JavaProxyClass.java
index 153eecbe91..1f71dda4f5 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyClass.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyClass.java
@@ -1,739 +1,739 @@
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
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.JavaClass;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.JavaUtil;
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
 
-            return JavaProxyClassFactory.newProxyClass(loader, null, superClass, interfaces, names);
+            return JavaProxyClassFactory.newProxyClass(save, loader, null, superClass, interfaces, names);
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
     
     @Override
     public Class getJavaClass() {
         return proxyClass;
     }
 
     @JRubyClass(name="JavaProxy::JavaProxyMethod")
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
 
         public Class<?>[] getExceptionTypes() {
             return m.getExceptionTypes();
         }
 
         public Class<?>[] getParameterTypes() {
             return parameterTypes;
         }
         
         public boolean isVarArgs() {
             return m.isVarArgs();
         }
 
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
 
             JavaProxyReflectionObject.registerRubyMethods(runtime, result);
 
             result.defineAnnotatedMethods(ProxyMethodImpl.class);
 
             return result;
         }
 
         public RubyObject name() {
             return getRuntime().newString(getName());
         }
 
         @JRubyMethod(name = "declaring_class")
         public JavaProxyClass getDeclaringClass() {
             return clazz;
         }
 
         @JRubyMethod
         public RubyArray argument_types() {
             return buildRubyArray(getParameterTypes());
         }
 
         @JRubyMethod(name = "super?")
         public IRubyObject super_p() {
             return hasSuperImplementation() ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         @JRubyMethod
         public RubyFixnum arity() {
             return getRuntime().newFixnum(getArity());
         }
 
         protected String nameOnInspection() {
             return getDeclaringClass().nameOnInspection() + "/" + getName();
         }
 
         @JRubyMethod
         public IRubyObject inspect() {
             StringBuilder result = new StringBuilder();
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
 
         @JRubyMethod(name = "invoke", rest = true)
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
                 if (getRuntime().getDebug().isTrue()) ite.getTargetException().printStackTrace();
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
 
         JavaProxyReflectionObject.registerRubyMethods(runtime, result);
 
         result.defineAnnotatedMethods(JavaProxyClass.class);
 
         return result;
     }
 
     @JRubyMethod(meta = true)
     public static RubyObject get(IRubyObject recv, IRubyObject obj) {
         if (!(obj instanceof JavaClass)) {
             throw recv.getRuntime().newTypeError(obj, recv.getRuntime().getJavaSupport().getJavaClassClass());
         }
         JavaClass type = (JavaClass)obj;
         
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
 
     @JRubyMethod(meta = true)
     public static RubyObject get_with_class(IRubyObject recv, IRubyObject obj) {
         Ruby runtime = recv.getRuntime();
         
         if (!(obj instanceof RubyClass)) {
             throw runtime.newTypeError(obj, runtime.getClassClass());
         }
         
         RubyClass clazz = (RubyClass)obj;
         
         // Let's only generate methods for those the user may actually 
         // intend to override.  That includes any defined in the current
         // class, and any ancestors that are also JavaProxyClasses (but none
         // from any other ancestor classes). Methods defined in mixins will
         // be considered intentionally overridden, except those from Kernel,
         // Java, and JavaProxyMethods, as well as Enumerable. 
         // TODO: may want to exclude other common mixins?
 
         JavaClass javaClass = null;
         Set<String> names = new HashSet<String>(); // need names ordered for key generation later
         List<Class<?>> interfaceList = new ArrayList<Class<?>>();
 
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
                                 methodNames.append(runtime.newString(methodName));
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
         Class<?>[] interfaces = new Class<?>[interfaceCount];
         for (int i = interfaceCount; --i >= 0; ) {
             interfaces[i] = interfaceList.get(i);
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
 
     @JRubyMethod
     public RubyObject superclass() {
         return JavaClass.get(getRuntime(), getSuperclass());
     }
 
     @JRubyMethod
     public RubyArray methods() {
         return buildRubyArray(getMethods());
     }
 
     @JRubyMethod
     public RubyArray interfaces() {
         return buildRubyArray(getInterfaces());
     }
 
     @JRubyMethod
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
diff --git a/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java b/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
index ff47731844..40cecca668 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
@@ -1,802 +1,808 @@
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
 import java.lang.reflect.Field;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.UndeclaredThrowableException;
 import java.security.AccessController;
 import java.security.PrivilegedAction;
 import java.security.ProtectionDomain;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
+import org.jruby.Ruby;
 import org.objectweb.asm.ClassVisitor;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.FieldVisitor;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.Type;
 import org.objectweb.asm.commons.GeneratorAdapter;
 
 public class JavaProxyClassFactory {
 
     private static final Type JAVA_LANG_CLASS_TYPE = Type.getType(Class.class);
 
     private static final Type[] EMPTY_TYPE_ARR = new Type[0];
 
     private static final org.objectweb.asm.commons.Method HELPER_GET_PROXY_CLASS_METHOD = org.objectweb.asm.commons.Method
             .getMethod(JavaProxyClass.class.getName()
                     + " initProxyClass(java.lang.Class)");
 
     private static final org.objectweb.asm.commons.Method CLASS_FORNAME_METHOD = org.objectweb.asm.commons.Method
             .getMethod("java.lang.Class forName(java.lang.String)");
 
     private static final String INVOCATION_HANDLER_FIELD_NAME = "__handler";
 
     private static final String PROXY_CLASS_FIELD_NAME = "__proxy_class";
 
     private static final Class[] EMPTY_CLASS_ARR = new Class[0];
 
     private static final Type INVOCATION_HANDLER_TYPE = Type
             .getType(JavaProxyInvocationHandler.class);
 
     private static final Type PROXY_METHOD_TYPE = Type
             .getType(JavaProxyMethod.class);
 
     private static final Type PROXY_CLASS_TYPE = Type
             .getType(JavaProxyClass.class);
 
     private static final org.objectweb.asm.commons.Method INVOCATION_HANDLER_INVOKE_METHOD = org.objectweb.asm.commons.Method
             .getMethod("java.lang.Object invoke(java.lang.Object, "
                     + PROXY_METHOD_TYPE.getClassName()
                     + ", java.lang.Object[])");
 
     private static final Type PROXY_HELPER_TYPE = Type
             .getType(InternalJavaProxyHelper.class);
 
     private static final org.objectweb.asm.commons.Method PROXY_HELPER_GET_METHOD = org.objectweb.asm.commons.Method
             .getMethod(PROXY_METHOD_TYPE.getClassName() + " initProxyMethod("
                     + JavaProxyClass.class.getName()
                     + ",java.lang.String,java.lang.String,boolean)");
 
     private static final Type JAVA_PROXY_TYPE = Type
             .getType(InternalJavaProxy.class);
 
     private static int counter;
 
     private static Map proxies = Collections.synchronizedMap(new HashMap());
 
     private static Method defineClass_method; // statically initialized below
 
     private static synchronized int nextId() {
         return counter++;
     }
     
+    @Deprecated
+    static JavaProxyClass newProxyClass(ClassLoader loader,
+            String targetClassName, Class superClass, Class[] interfaces, Set names)
+            throws InvocationTargetException {
+        return newProxyClass(JavaProxyClass.runtimeTLS.get(), loader, targetClassName, superClass, interfaces, names);
+    }
+    
     // TODO: we should be able to optimize this quite a bit post-1.0.  JavaClass already
     // has all the methods organized by method name; the next version (supporting protected
     // methods/fields) will have them organized even further. So collectMethods here can
     // just lookup the overridden methods in the JavaClass map, should be much faster.
-    static JavaProxyClass newProxyClass(ClassLoader loader,
+    static JavaProxyClass newProxyClass(Ruby runtime, ClassLoader loader,
             String targetClassName, Class superClass, Class[] interfaces, Set names)
             throws InvocationTargetException {
         if (loader == null) {
             loader = JavaProxyClassFactory.class.getClassLoader();
         }
 
         if (superClass == null) {
             superClass = Object.class;
         }
 
         if (interfaces == null) {
             interfaces = EMPTY_CLASS_ARR;
         }
 
         Set key = new HashSet();
         key.add(superClass);
         for (int i = 0; i < interfaces.length; i++) {
             key.add(interfaces[i]);
         }
 
         // add (potentially) overridden names to the key.
         // TODO: see note above re: optimizations
         if (names != null) {
             key.addAll(names);
         }
 
         JavaProxyClass proxyClass = (JavaProxyClass) proxies.get(key);
         if (proxyClass == null) {
 
             if (targetClassName == null) {
                 // We always prepend an org.jruby.proxy package to the beginning
                 // because java and javax packages are protected and signed
                 // jars prevent us generating new classes with those package
                 // names. See JRUBY-2439.
                 String pkg = "org.jruby.proxy." + packageName(superClass);
                 String fullName = superClass.getName();
                 int ix = fullName.lastIndexOf('.');
                 String cName = fullName;
                 if(ix != -1) {
                     cName = fullName.substring(ix+1);
                 }
                 targetClassName = pkg + "." + cName + "$Proxy" + nextId();
             }
 
-            validateArgs(targetClassName, superClass);
+            validateArgs(runtime, targetClassName, superClass);
 
             Map methods = new HashMap();
             collectMethods(superClass, interfaces, methods, names);
 
             Type selfType = Type.getType("L"
                     + toInternalClassName(targetClassName) + ";");
             proxyClass = generate(loader, targetClassName, superClass,
                     interfaces, methods, selfType);
 
             proxies.put(key, proxyClass);
         }
 
         return proxyClass;
     }
 
     static JavaProxyClass newProxyClass(ClassLoader loader,
             String targetClassName, Class superClass, Class[] interfaces)
             throws InvocationTargetException {
         return newProxyClass(loader,targetClassName,superClass,interfaces,null);
     }
 
     private static JavaProxyClass generate(final ClassLoader loader,
             final String targetClassName, final Class superClass,
             final Class[] interfaces, final Map methods, final Type selfType) {
         ClassWriter cw = beginProxyClass(targetClassName, superClass,
                 interfaces);
 
         GeneratorAdapter clazzInit = createClassInitializer(selfType, cw);
 
         generateConstructors(superClass, selfType, cw);
 
         generateGetProxyClass(selfType, cw);
 
         generateGetInvocationHandler(selfType, cw);
 
         generateProxyMethods(superClass, methods, selfType, cw, clazzInit);
 
         // finish class initializer
         clazzInit.returnValue();
         clazzInit.endMethod();
 
         // end class
         cw.visitEnd();
 
         byte[] data = cw.toByteArray();
 
         /*
          * try { FileOutputStream o = new
          * FileOutputStream(targetClassName.replace( '/', '.') + ".class");
          * o.write(data); o.close(); } catch (IOException ex) {
          * ex.printStackTrace(); }
          */
 
         Class clazz = invokeDefineClass(loader, selfType.getClassName(), data);
 
         // trigger class initialization for the class
         try {
             Field proxy_class = clazz.getDeclaredField(PROXY_CLASS_FIELD_NAME);
             proxy_class.setAccessible(true);
             return (JavaProxyClass) proxy_class.get(clazz);
         } catch (Exception ex) {
             InternalError ie = new InternalError();
             ie.initCause(ex);
             throw ie;
         }
     }
 
     static {
         AccessController.doPrivileged(new PrivilegedAction() {
             public Object run() {
                 try {
                     defineClass_method = ClassLoader.class.getDeclaredMethod(
                             "defineClass", new Class[] { String.class,
                                     byte[].class, int.class, int.class, ProtectionDomain.class });
                 } catch (Exception e) {
                     // should not happen!
                     e.printStackTrace();
                     return null;
                 }
                 defineClass_method.setAccessible(true);
                 return null;
             }
         });
     }
 
     private static Class invokeDefineClass(ClassLoader loader,
             String className, byte[] data) {
         try {
             return (Class) defineClass_method
                     .invoke(loader, new Object[] { className, data,
                             new Integer(0), new Integer(data.length), JavaProxyClassFactory.class.getProtectionDomain() });
         } catch (IllegalArgumentException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         } catch (IllegalAccessException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         } catch (InvocationTargetException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         }
     }
 
     private static ClassWriter beginProxyClass(final String targetClassName,
             final Class superClass, final Class[] interfaces) {
 
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
 
         int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC;
         String name = toInternalClassName(targetClassName);
         String signature = null;
         String supername = toInternalClassName(superClass);
         String[] interfaceNames = new String[interfaces.length + 1];
         for (int i = 0; i < interfaces.length; i++) {
             interfaceNames[i] = toInternalClassName(interfaces[i]);
         }
         interfaceNames[interfaces.length] = toInternalClassName(InternalJavaProxy.class);
 
         // start class
         cw.visit(Opcodes.V1_3, access, name, signature, supername,
                 interfaceNames);
 
         cw.visitField(Opcodes.ACC_PRIVATE, INVOCATION_HANDLER_FIELD_NAME,
                 INVOCATION_HANDLER_TYPE.getDescriptor(), null, null).visitEnd();
 
         cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                 PROXY_CLASS_FIELD_NAME, PROXY_CLASS_TYPE.getDescriptor(), null,
                 null).visitEnd();
 
         return cw;
     }
 
     private static void generateProxyMethods(Class superClass, Map methods,
             Type selfType, ClassVisitor cw, GeneratorAdapter clazzInit) {
         Iterator it = methods.values().iterator();
         while (it.hasNext()) {
             MethodData md = (MethodData) it.next();
             Type superClassType = Type.getType(superClass);
             generateProxyMethod(selfType, superClassType, cw, clazzInit, md);
         }
     }
 
     private static void generateGetInvocationHandler(Type selfType,
             ClassVisitor cw) {
         // make getter for handler
         GeneratorAdapter gh = new GeneratorAdapter(Opcodes.ACC_PUBLIC,
                 new org.objectweb.asm.commons.Method("___getInvocationHandler",
                         INVOCATION_HANDLER_TYPE, EMPTY_TYPE_ARR), null,
                 EMPTY_TYPE_ARR, cw);
 
         gh.loadThis();
         gh.getField(selfType, INVOCATION_HANDLER_FIELD_NAME,
                 INVOCATION_HANDLER_TYPE);
         gh.returnValue();
         gh.endMethod();
     }
 
     private static void generateGetProxyClass(Type selfType, ClassVisitor cw) {
         // make getter for proxy class
         GeneratorAdapter gpc = new GeneratorAdapter(Opcodes.ACC_PUBLIC,
                 new org.objectweb.asm.commons.Method("___getProxyClass",
                         PROXY_CLASS_TYPE, EMPTY_TYPE_ARR), null,
                 EMPTY_TYPE_ARR, cw);
         gpc.getStatic(selfType, PROXY_CLASS_FIELD_NAME, PROXY_CLASS_TYPE);
         gpc.returnValue();
         gpc.endMethod();
     }
 
     private static void generateConstructors(Class superClass, Type selfType,
             ClassVisitor cw) {
         Constructor[] cons = superClass.getDeclaredConstructors();
         for (int i = 0; i < cons.length; i++) {
             Constructor constructor = cons[i];
 
             int acc = constructor.getModifiers();
             if (Modifier.isProtected(acc) || Modifier.isPublic(acc)) {
                 // ok, it's publix or protected
             } else if (!Modifier.isPrivate(acc)
                     && packageName(constructor.getDeclaringClass()).equals(
                             packageName(selfType.getClassName()))) {
                 // ok, it's package scoped and we're in the same package
             } else {
                 // it's unaccessible
                 continue;
             }
 
             generateConstructor(selfType, constructor, cw);
         }
     }
 
     private static GeneratorAdapter createClassInitializer(Type selfType,
             ClassVisitor cw) {
         GeneratorAdapter clazzInit;
         clazzInit = new GeneratorAdapter(Opcodes.ACC_PRIVATE
                 | Opcodes.ACC_STATIC, new org.objectweb.asm.commons.Method(
                 "<clinit>", Type.VOID_TYPE, EMPTY_TYPE_ARR), null,
                 EMPTY_TYPE_ARR, cw);
 
         clazzInit.visitLdcInsn(selfType.getClassName());
         clazzInit.invokeStatic(JAVA_LANG_CLASS_TYPE, CLASS_FORNAME_METHOD);
         clazzInit
                 .invokeStatic(PROXY_HELPER_TYPE, HELPER_GET_PROXY_CLASS_METHOD);
         clazzInit.dup();
         clazzInit.putStatic(selfType, PROXY_CLASS_FIELD_NAME, PROXY_CLASS_TYPE);
         return clazzInit;
     }
 
     private static void generateProxyMethod(Type selfType, Type superType,
             ClassVisitor cw, GeneratorAdapter clazzInit, MethodData md) {
         if (!md.generateProxyMethod()) {
             return;
         }
 
         org.objectweb.asm.commons.Method m = md.getMethod();
         Type[] ex = toType(md.getExceptions());
 
         String field_name = "__mth$" + md.getName() + md.scrambledSignature();
 
         // create static private method field
         FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE
                 | Opcodes.ACC_STATIC, field_name, PROXY_METHOD_TYPE
                 .getDescriptor(), null, null);
         fv.visitEnd();
 
         clazzInit.dup();
         clazzInit.push(m.getName());
         clazzInit.push(m.getDescriptor());
         clazzInit.push(md.isImplemented());
         clazzInit.invokeStatic(PROXY_HELPER_TYPE, PROXY_HELPER_GET_METHOD);
         clazzInit.putStatic(selfType, field_name, PROXY_METHOD_TYPE);
 
         org.objectweb.asm.commons.Method sm = new org.objectweb.asm.commons.Method(
                 "__super$" + m.getName(), m.getReturnType(), m
                         .getArgumentTypes());
 
         //
         // construct the proxy method
         //
         GeneratorAdapter ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null,
                 ex, cw);
 
         ga.loadThis();
         ga.getField(selfType, INVOCATION_HANDLER_FIELD_NAME,
                 INVOCATION_HANDLER_TYPE);
 
         // if the method is extending something, then we have
         // to test if the handler is initialized...
 
         if (md.isImplemented()) {
             ga.dup();
             Label ok = ga.newLabel();
             ga.ifNonNull(ok);
 
             ga.loadThis();
             ga.loadArgs();
             ga.invokeConstructor(superType, m);
             ga.returnValue();
             ga.mark(ok);
         }
 
         ga.loadThis();
         ga.getStatic(selfType, field_name, PROXY_METHOD_TYPE);
 
         if (m.getArgumentTypes().length == 0) {
             // load static empty array
             ga.getStatic(JAVA_PROXY_TYPE, "NO_ARGS", Type
                     .getType(Object[].class));
         } else {
             // box arguments
             ga.loadArgArray();
         }
 
         Label before = ga.mark();
 
         ga.invokeInterface(INVOCATION_HANDLER_TYPE,
                 INVOCATION_HANDLER_INVOKE_METHOD);
 
         Label after = ga.mark();
 
         ga.unbox(m.getReturnType());
         ga.returnValue();
 
         // this is a simple rethrow handler
         Label rethrow = ga.mark();
         ga.visitInsn(Opcodes.ATHROW);
 
         for (int i = 0; i < ex.length; i++) {
             ga.visitTryCatchBlock(before, after, rethrow, ex[i]
                     .getInternalName());
         }
 
         ga.visitTryCatchBlock(before, after, rethrow, "java/lang/Error");
         ga.visitTryCatchBlock(before, after, rethrow,
                 "java/lang/RuntimeException");
 
         Type thr = Type.getType(Throwable.class);
         Label handler = ga.mark();
         Type udt = Type.getType(UndeclaredThrowableException.class);
         int loc = ga.newLocal(thr);
         ga.storeLocal(loc, thr);
         ga.newInstance(udt);
         ga.dup();
         ga.loadLocal(loc, thr);
         ga.invokeConstructor(udt, org.objectweb.asm.commons.Method
                 .getMethod("void <init>(java.lang.Throwable)"));
         ga.throwException();
 
         ga.visitTryCatchBlock(before, after, handler, "java/lang/Throwable");
 
         ga.endMethod();
 
         //
         // construct the super-proxy method
         //
         if (md.isImplemented()) {
 
             GeneratorAdapter ga2 = new GeneratorAdapter(Opcodes.ACC_PUBLIC, sm,
                     null, ex, cw);
 
             ga2.loadThis();
             ga2.loadArgs();
             ga2.invokeConstructor(superType, m);
             ga2.returnValue();
             ga2.endMethod();
         }
     }
 
     private static Class[] generateConstructor(Type selfType,
             Constructor constructor, ClassVisitor cw) {
 
         Class[] superConstructorParameterTypes = constructor
                 .getParameterTypes();
         Class[] newConstructorParameterTypes = new Class[superConstructorParameterTypes.length + 1];
         System.arraycopy(superConstructorParameterTypes, 0,
                 newConstructorParameterTypes, 0,
                 superConstructorParameterTypes.length);
         newConstructorParameterTypes[superConstructorParameterTypes.length] = JavaProxyInvocationHandler.class;
 
         int access = Opcodes.ACC_PUBLIC;
         String name1 = "<init>";
         String signature = null;
         Class[] superConstructorExceptions = constructor.getExceptionTypes();
 
         org.objectweb.asm.commons.Method super_m = new org.objectweb.asm.commons.Method(
                 name1, Type.VOID_TYPE, toType(superConstructorParameterTypes));
         org.objectweb.asm.commons.Method m = new org.objectweb.asm.commons.Method(
                 name1, Type.VOID_TYPE, toType(newConstructorParameterTypes));
 
         GeneratorAdapter ga = new GeneratorAdapter(access, m, signature,
                 toType(superConstructorExceptions), cw);
 
         ga.loadThis();
         ga.loadArgs(0, superConstructorParameterTypes.length);
         ga.invokeConstructor(Type.getType(constructor.getDeclaringClass()),
                 super_m);
 
         ga.loadThis();
         ga.loadArg(superConstructorParameterTypes.length);
         ga.putField(selfType, INVOCATION_HANDLER_FIELD_NAME,
                 INVOCATION_HANDLER_TYPE);
 
         // do a void return
         ga.returnValue();
         ga.endMethod();
         return newConstructorParameterTypes;
     }
 
     private static String toInternalClassName(Class clazz) {
         return toInternalClassName(clazz.getName());
     }
 
     private static String toInternalClassName(String name) {
         return name.replace('.', '/');
     }
 
     private static Type[] toType(Class[] parameterTypes) {
         Type[] result = new Type[parameterTypes.length];
         for (int i = 0; i < result.length; i++) {
             result[i] = Type.getType(parameterTypes[i]);
         }
         return result;
     }
 
     private static void collectMethods(Class superClass, Class[] interfaces,
             Map methods, Set names) {
         HashSet allClasses = new HashSet();
         addClass(allClasses, methods, superClass, names);
         addInterfaces(allClasses, methods, interfaces, names);
     }
 
     static class MethodData {
         Set methods = new HashSet();
 
         final Method mostSpecificMethod;
         final Class[] mostSpecificParameterTypes;
 
         boolean hasPublicDecl = false;
 
         MethodData(Method method) {
             this.mostSpecificMethod = method;
             this.mostSpecificParameterTypes = mostSpecificMethod.getParameterTypes();
             hasPublicDecl = method.getDeclaringClass().isInterface()
                     || Modifier.isPublic(method.getModifiers());
         }
 
         public String scrambledSignature() {
             StringBuilder sb = new StringBuilder();
             Class[] parms = getParameterTypes();
             for (int i = 0; i < parms.length; i++) {
                 sb.append('$');
                 String name = parms[i].getName();
                 name = name.replace('[', '1');
                 name = name.replace('.', '_');
                 name = name.replace(';', '2');
                 sb.append(name);
             }
             return sb.toString();
         }
 
         public Class getDeclaringClass() {
             return mostSpecificMethod.getDeclaringClass();
         }
 
         public org.objectweb.asm.commons.Method getMethod() {
             return new org.objectweb.asm.commons.Method(getName(), Type
                     .getType(getReturnType()), getType(getParameterTypes()));
         }
 
         private Type[] getType(Class[] parameterTypes) {
             Type[] result = new Type[parameterTypes.length];
             for (int i = 0; i < parameterTypes.length; i++) {
                 result[i] = Type.getType(parameterTypes[i]);
             }
             return result;
         }
 
         private String getName() {
             return mostSpecificMethod.getName();
         }
 
         private Class[] getParameterTypes() {
             return mostSpecificParameterTypes;
         }
 
         public Class[] getExceptions() {
 
             Set all = new HashSet();
 
             Iterator it = methods.iterator();
             while (it.hasNext()) {
                 Method m = (Method) it.next();
                 Class[] ex = m.getExceptionTypes();
                 for (int i = 0; i < ex.length; i++) {
                     Class exx = ex[i];
 
                     if (all.contains(exx)) {
                         continue;
                     }
 
                     boolean add = true;
                     Iterator it2 = all.iterator();
                     while (it2.hasNext()) {
                         Class de = (Class) it2.next();
 
                         if (de.isAssignableFrom(exx)) {
                             add = false;
                             break;
                         } else if (exx.isAssignableFrom(de)) {
                             it2.remove();
                             add = true;
                         }
 
                     }
 
                     if (add) {
                         all.add(exx);
                     }
                 }
             }
 
             return (Class[]) all.toArray(new Class[all.size()]);
         }
 
         public boolean generateProxyMethod() {
             return !isFinal() && !isPrivate();
         }
 
         public void add(Method method) {
             methods.add(method);
             hasPublicDecl |= Modifier.isPublic(method.getModifiers());
         }
 
         Class getReturnType() {
             return mostSpecificMethod.getReturnType();
         }
 
         boolean isFinal() {
             if (mostSpecificMethod.getDeclaringClass().isInterface()) {
                 return false;
             }
 
             int mod = mostSpecificMethod.getModifiers();
             return Modifier.isFinal(mod);
         }
 
         boolean isPrivate() {
             if (mostSpecificMethod.getDeclaringClass().isInterface()) {
                 return false;
             }
 
             int mod = mostSpecificMethod.getModifiers();
             return Modifier.isPrivate(mod);
         }
 
         boolean isImplemented() {
             if (mostSpecificMethod.getDeclaringClass().isInterface()) {
                 return false;
             }
 
             int mod = mostSpecificMethod.getModifiers();
             return !Modifier.isAbstract(mod);
         }
     }
 
     static class MethodKey {
         private String name;
 
         private Class[] arguments;
 
         MethodKey(Method m) {
             this.name = m.getName();
             this.arguments = m.getParameterTypes();
         }
 
         public boolean equals(Object obj) {
             if (obj instanceof MethodKey) {
                 MethodKey key = (MethodKey) obj;
 
                 return name.equals(key.name)
                         && Arrays.equals(arguments, key.arguments);
             }
 
             return false;
         }
 
         public int hashCode() {
             return name.hashCode();
         }
     }
 
     private static void addInterfaces(Set allClasses, Map methods,
             Class[] interfaces, Set names) {
         for (int i = 0; i < interfaces.length; i++) {
             addInterface(allClasses, methods, interfaces[i], names);
         }
     }
 
     private static void addInterface(Set allClasses, Map methods,
             Class interfaze, Set names) {
         if (allClasses.add(interfaze)) {
             addMethods(methods, interfaze, names);
             addInterfaces(allClasses, methods, interfaze.getInterfaces(), names);
         }
     }
 
     private static void addMethods(Map methods, Class classOrInterface, Set names) {
         Method[] mths = classOrInterface.getDeclaredMethods();
         for (int i = 0; i < mths.length; i++) {
             if (names == null || names.contains(mths[i].getName())) {
                 addMethod(methods, mths[i]);
             }
         }
     }
 
     private static void addMethod(Map methods, Method method) {
         int acc = method.getModifiers();
         
         if (Modifier.isStatic(acc) || Modifier.isPrivate(acc)) {
             return;
         }
 
         MethodKey mk = new MethodKey(method);
         MethodData md = (MethodData) methods.get(mk);
         if (md == null) {
             md = new MethodData(method);
             methods.put(mk, md);
         }
         md.add(method);
     }
 
     private static void addClass(Set allClasses, Map methods, Class clazz, Set names) {
         if (allClasses.add(clazz)) {
             addMethods(methods, clazz, names);
             Class superClass = clazz.getSuperclass();
             if (superClass != null) {
                 addClass(allClasses, methods, superClass, names);
             }
 
             addInterfaces(allClasses, methods, clazz.getInterfaces(), names);
         }
     }
 
-    private static void validateArgs(String targetClassName, Class superClass) {
+    private static void validateArgs(Ruby runtime, String targetClassName, Class superClass) {
 
         if (Modifier.isFinal(superClass.getModifiers())) {
-            throw new IllegalArgumentException("cannot extend final class");
+            throw runtime.newTypeError("cannot extend final class " + superClass.getName());
         }
 
         String targetPackage = packageName(targetClassName);
 
         String pkg = targetPackage.replace('.', '/');
         if (pkg.startsWith("java")) {
-            throw new IllegalArgumentException("cannor add classes to package "
-                    + pkg);
+            throw runtime.newTypeError("cannot add classes to package " + pkg);
         }
 
         Package p = Package.getPackage(pkg);
         if (p != null) {
             if (p.isSealed()) {
-                throw new IllegalArgumentException("package " + p
-                        + " is sealed");
+                throw runtime.newTypeError("package " + p + " is sealed");
             }
         }
     }
 
     private static String packageName(Class clazz) {
         String clazzName = clazz.getName();
         return packageName(clazzName);
     }
 
     private static String packageName(String clazzName) {
         int idx = clazzName.lastIndexOf('.');
         if (idx == -1) {
             return "";
         } else {
             return clazzName.substring(0, idx);
         }
     }
 
 }
