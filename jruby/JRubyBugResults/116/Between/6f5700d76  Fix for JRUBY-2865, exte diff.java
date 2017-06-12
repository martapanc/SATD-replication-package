diff --git a/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java b/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
index 40cecca668..2e55385d12 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
@@ -1,808 +1,818 @@
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
 
 import org.jruby.Ruby;
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
     
     @Deprecated
     static JavaProxyClass newProxyClass(ClassLoader loader,
             String targetClassName, Class superClass, Class[] interfaces, Set names)
             throws InvocationTargetException {
         return newProxyClass(JavaProxyClass.runtimeTLS.get(), loader, targetClassName, superClass, interfaces, names);
     }
     
     // TODO: we should be able to optimize this quite a bit post-1.0.  JavaClass already
     // has all the methods organized by method name; the next version (supporting protected
     // methods/fields) will have them organized even further. So collectMethods here can
     // just lookup the overridden methods in the JavaClass map, should be much faster.
     static JavaProxyClass newProxyClass(Ruby runtime, ClassLoader loader,
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
-                String pkg = "org.jruby.proxy." + packageName(superClass);
+                String pkg = proxyPackageName(superClass);
                 String fullName = superClass.getName();
                 int ix = fullName.lastIndexOf('.');
                 String cName = fullName;
                 if(ix != -1) {
                     cName = fullName.substring(ix+1);
                 }
                 targetClassName = pkg + "." + cName + "$Proxy" + nextId();
             }
 
             validateArgs(runtime, targetClassName, superClass);
 
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
 
     private static void validateArgs(Ruby runtime, String targetClassName, Class superClass) {
 
         if (Modifier.isFinal(superClass.getModifiers())) {
             throw runtime.newTypeError("cannot extend final class " + superClass.getName());
         }
 
         String targetPackage = packageName(targetClassName);
 
         String pkg = targetPackage.replace('.', '/');
         if (pkg.startsWith("java")) {
             throw runtime.newTypeError("cannot add classes to package " + pkg);
         }
 
         Package p = Package.getPackage(pkg);
         if (p != null) {
             if (p.isSealed()) {
                 throw runtime.newTypeError("package " + p + " is sealed");
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
 
+    private static String proxyPackageName(Class clazz) {
+        String clazzName = clazz.getName();
+        int idx = clazzName.lastIndexOf('.');
+        if (idx == -1) {
+            return "org.jruby.proxy";
+        } else {
+            return "org.jruby.proxy." + clazzName.substring(0, idx);
+        }
+    }
+
 }
diff --git a/test/DefaultPackageClass.java b/test/DefaultPackageClass.java
new file mode 100644
index 0000000000..42b174b1d5
--- /dev/null
+++ b/test/DefaultPackageClass.java
@@ -0,0 +1,2 @@
+public class DefaultPackageClass {
+}
diff --git a/test/test_higher_javasupport.rb b/test/test_higher_javasupport.rb
index 4a0a2e34be..727eea23a9 100644
--- a/test/test_higher_javasupport.rb
+++ b/test/test_higher_javasupport.rb
@@ -1,777 +1,783 @@
 require 'java'
 require 'rbconfig'
 require 'test/unit'
 
 TopLevelConstantExistsProc = Proc.new do
   include_class 'java.lang.String'
 end
 
 class TestHigherJavasupport < Test::Unit::TestCase
   TestHelper = org.jruby.test.TestHelper
   JArray = ArrayList = java.util.ArrayList
   FinalMethodBaseTest = org.jruby.test.FinalMethodBaseTest
   Annotation = java.lang.annotation.Annotation
   ClassWithPrimitive = org.jruby.test.ClassWithPrimitive
   
   def test_java_int_primitive_assignment
     assert_raises(TypeError) { ClassWithPrimitive.new.an_int = nil }
   end
 
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
         $stderr.reopen(Config::CONFIG['target_os'] =~ /Windows|mswin/ ? 'NUL:' : '/dev/null')
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
 
   include_class 'java.math.BigDecimal'
   def test_big_decimal_interaction
     assert_equal(BigDecimal, BigDecimal.new("1.23").add(BigDecimal.new("2.34")).class)
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
       $stderr.reopen(Config::CONFIG['target_os'] =~ /Windows|mswin/ ? 'NUL:' : '/dev/null')
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
   
   # JRUBY-2169
   def test_java_class_resource_methods
     # FIXME? not sure why this works, didn't modify build.xml
     # to copy this file, yet it finds it anyway
     props_file = 'test_java_class_resource_methods.properties'
     
     # nothing special about this class, selected at random for testing
     jc = org.jruby.javasupport.test.RubyTestObject.java_class
     
     # get resource as URL
     url = jc.resource(props_file)
     assert(java.net.URL === url)
     assert(/^foo=bar/ =~ java.io.DataInputStream.new(url.content).read_line)
 
     # get resource as stream
     is = jc.resource_as_stream(props_file)
     assert(java.io.InputStream === is)
     assert(/^foo=bar/ =~ java.io.DataInputStream.new(is).read_line)
     
 
     # get resource as string
     str = jc.resource_as_string(props_file)
     assert(/^foo=bar/ =~ str)
   end
   
   # JRUBY-2169
   def test_ji_extended_methods_for_java_1_5
     jc = java.lang.String.java_class
     ctor = jc.constructors[0]
     meth = jc.java_instance_methods[0]
     field = jc.fields[0]
     
     # annotations
     assert(Annotation[] === jc.annotations)
     assert(Annotation[] === ctor.annotations)
     assert(Annotation[] === meth.annotations)
     assert(Annotation[] === field.annotations)
     
     # TODO: more extended methods to test
     
     
   end
   
   # JRUBY-2169
   def test_java_class_ruby_class
     assert java.lang.Object.java_class.ruby_class == java.lang.Object
     assert java.lang.Runnable.java_class.ruby_class == java.lang.Runnable
   end
   
   def test_null_toString
     assert nil == org.jruby.javasupport.test.NullToString.new.to_s
   end
   
   # JRUBY-2277
   # kind of a strange place for this test, but the error manifested
   # when JI was enabled.  the actual bug was a problem in alias_method,
   # and not related to JI; see related test in test_methods.rb 
   def test_alias_method_with_JI_enabled_does_not_raise
     name = Object.new
     def name.to_str
       "new_name"
     end
     assert_nothing_raised { String.send("alias_method", name, "to_str") }
   end
 
   # JRUBY-2671
   def test_coerce_array_to_java_with_javaobject_inside
     x = nil
     assert_nothing_raised { x = java.util.ArrayList.new([java.lang.Integer.new(1)]) }
     assert_equal("[1]", x.to_string)
   end
+
+  # JRUBY-2865
+  def test_extend_default_package_class
+    cls = Class.new(Java::DefaultPackageClass);
+    assert_nothing_raised { cls.new }
+  end
 end
