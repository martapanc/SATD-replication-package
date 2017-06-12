diff --git a/src/org/jruby/javasupport/JavaConstructor.java b/src/org/jruby/javasupport/JavaConstructor.java
index 9e4ac98780..598ea0c5a0 100644
--- a/src/org/jruby/javasupport/JavaConstructor.java
+++ b/src/org/jruby/javasupport/JavaConstructor.java
@@ -1,239 +1,243 @@
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
 
 import java.lang.annotation.Annotation;
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Type;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 @JRubyClass(name="Java::JavaConstructor")
 public class JavaConstructor extends JavaCallable {
     private final Constructor<?> constructor;
     private final Class<?>[] parameterTypes;
     private final JavaUtil.JavaConverter objectConverter;
 
+    public Object getValue() {
+        return constructor;
+    }
+
     public static RubyClass createJavaConstructorClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result =
                 javaModule.defineClassUnder("JavaConstructor", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
         JavaCallable.registerRubyMethods(runtime, result);
         
         result.defineAnnotatedMethods(JavaConstructor.class);
         
         return result;
     }
 
     public JavaConstructor(Ruby runtime, Constructor<?> constructor) {
         super(runtime, runtime.getJavaSupport().getJavaConstructorClass());
         this.constructor = constructor;
         this.parameterTypes = constructor.getParameterTypes();
         
         this.objectConverter = JavaUtil.getJavaConverter(constructor.getDeclaringClass());
     }
 
     public static JavaConstructor create(Ruby runtime, Constructor<?> constructor) {
         return new JavaConstructor(runtime, constructor);
     }
     
     public static JavaConstructor getMatchingConstructor(Ruby runtime, Class<?> javaClass, Class<?>[] argumentTypes) {
         try {
             return create(runtime, javaClass.getConstructor(argumentTypes));
         } catch (NoSuchMethodException e) {
             // Java reflection does not allow retrieving constructors like methods
             CtorSearch: for (Constructor<?> ctor : javaClass.getConstructors()) {
                 Class<?>[] targetTypes = ctor.getParameterTypes();
                 
                 // for zero args case we can stop searching
                 if (targetTypes.length != argumentTypes.length) {
                     continue CtorSearch;
                 } else if (targetTypes.length == 0 && argumentTypes.length == 0) {
                     return create(runtime, ctor);
                 } else {
                     boolean found = true;
                     
                     TypeScan: for (int i = 0; i < argumentTypes.length; i++) {
                         if (i >= targetTypes.length) found = false;
                         
                         if (targetTypes[i].isAssignableFrom(argumentTypes[i])) {
                             found = true;
                             continue TypeScan;
                         } else {
                             found = false;
                             continue CtorSearch;
                         }
                     }
 
                     // if we get here, we found a matching method, use it
                     // TODO: choose narrowest method by continuing to search
                     if (found) {
                         return create(runtime, ctor);
                     }
                 }
             }
         }
         // no matching ctor found
         return null;
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
     
     protected String nameOnInspection() {
         return getType().toString();
     }
 
     public Class<?>[] getParameterTypes() {
         return parameterTypes;
     }
 
     public Class<?>[] getExceptionTypes() {
         return constructor.getExceptionTypes();
     }
 
     public Type[] getGenericParameterTypes() {
         return constructor.getGenericParameterTypes();
     }
 
     public Type[] getGenericExceptionTypes() {
         return constructor.getGenericExceptionTypes();
     }
 
     public Annotation[][] getParameterAnnotations() {
         return constructor.getParameterAnnotations();
     }
     
     public boolean isVarArgs() {
         return constructor.isVarArgs();
     }
 
     public int getModifiers() {
         return constructor.getModifiers();
     }
     
     public String toGenericString() {
         return constructor.toGenericString();
     }
 
     protected AccessibleObject accessibleObject() {
         return constructor;
     }
     
     @JRubyMethod
     public IRubyObject type_parameters() {
         return Java.getInstance(getRuntime(), constructor.getTypeParameters());
     }
 
     @JRubyMethod
     public IRubyObject return_type() {
         return getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject new_instance(IRubyObject[] args) {
         int length = args.length;
         Class<?>[] types = parameterTypes;
         if (length != types.length) {
             throw getRuntime().newArgumentError(length, types.length);
         }
         Object[] constructorArguments = new Object[length];
         for (int i = length; --i >= 0; ) {
             constructorArguments[i] = JavaUtil.convertArgument(getRuntime(), args[i], types[i]);
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
 
     public IRubyObject new_instance(Object[] arguments) {
         if (arguments.length != getArity()) {
             throw getRuntime().newArgumentError(arguments.length, getArity());
         }
 
         try {
             Object result = constructor.newInstance(arguments);
             return JavaObject.wrap(getRuntime(), result);
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError("expected " + argument_types().inspect() +
                                               ", got [" + arguments[0].getClass().getName() + ", ...]");
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
     
 }
diff --git a/src/org/jruby/javasupport/JavaField.java b/src/org/jruby/javasupport/JavaField.java
index 372bfb65b7..ef0180165f 100644
--- a/src/org/jruby/javasupport/JavaField.java
+++ b/src/org/jruby/javasupport/JavaField.java
@@ -1,209 +1,211 @@
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
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 @JRubyClass(name="Java::JavaField")
 public class JavaField extends JavaAccessibleObject {
     private Field field;
 
+    public Object getValue() {
+        return field;
+    }
+
     public static RubyClass createJavaFieldClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = javaModule.defineClassUnder("JavaField", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
         
         result.defineAnnotatedMethods(JavaField.class);
 
         return result;
     }
 
     public JavaField(Ruby runtime, Field field) {
         super(runtime, runtime.getJavaSupport().getJavaFieldClass());
         this.field = field;
     }
 
     public boolean equals(Object other) {
         return other instanceof JavaField &&
             this.field == ((JavaField)other).field;
     }
     
     public int hashCode() {
         return field.hashCode();
     }
 
     @JRubyMethod
     public RubyString value_type() {
         return getRuntime().newString(field.getType().getName());
     }
 
     @JRubyMethod(name = {"==", "==="})
     public IRubyObject op_equal(IRubyObject other) {
     	if (!(other instanceof JavaField)) {
     		return getRuntime().getFalse();
     	}
     	
         return getRuntime().newBoolean(field.equals(((JavaField) other).field));
     }
 
     @JRubyMethod(name = "public?")
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(field.getModifiers()));
     }
 
     @JRubyMethod(name = "static?")
     public RubyBoolean static_p() {
         return getRuntime().newBoolean(Modifier.isStatic(field.getModifiers()));
     }
     
     @JRubyMethod(name = "enum_constant?")
     public RubyBoolean enum_constant_p() {
         return getRuntime().newBoolean(field.isEnumConstant());
     }
 
     @JRubyMethod
     public RubyString to_generic_string() {
         return getRuntime().newString(field.toGenericString());
     }
     
     @JRubyMethod(name = "type")
     public IRubyObject field_type() {
         return JavaClass.get(getRuntime(), field.getType());
     }
 
     @JRubyMethod
     public IRubyObject value(IRubyObject object) {
-        JavaObject obj = JavaUtil.unwrapJavaObject(getRuntime(), object, "not a java object");
-        Object javaObject = obj.getValue();
+        Object javaObject = JavaUtil.unwrapJavaValue(getRuntime(), object, "not a java object");
         try {
             return JavaUtil.convertJavaToRuby(getRuntime(), field.get(javaObject), field.getType());
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError("illegal access");
         }
     }
 
     @JRubyMethod
     public IRubyObject set_value(IRubyObject object, IRubyObject value) {
-        JavaObject obj = JavaUtil.unwrapJavaObject(getRuntime(), object, "not a java object: " + object);
+        Object javaObject  = JavaUtil.unwrapJavaValue(getRuntime(), object, "not a java object: " + object);
         IRubyObject val = value;
         if(val.dataGetStruct() instanceof JavaObject) {
             val = (IRubyObject)val.dataGetStruct();
         }
-        Object javaObject = obj.getValue();
         try {
             Object convertedValue = JavaUtil.convertArgumentToType(val.getRuntime().getCurrentContext(), val, field.getType());
 
             field.set(javaObject, convertedValue);
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError(
                                 "illegal access on setting variable: " + iae.getMessage());
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError(
                                 "wrong type for " + field.getType().getName() + ": " +
                                 val.getClass().getName());
         }
         return val;
     }
 
     @JRubyMethod(name = "final?")
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(field.getModifiers()));
     }
 
     @JRubyMethod
     public JavaObject static_value() {
         try {
             // TODO: Only setAccessible to account for pattern found by
             // accessing constants included from a non-public interface.
             // (aka java.util.zip.ZipConstants being implemented by many
             // classes)
             if (!Ruby.isSecurityRestricted()) {
                 field.setAccessible(true);
             }
             return JavaObject.wrap(getRuntime(), field.get(null));
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError("illegal static value access: " + iae.getMessage());
         }
     }
 
     @JRubyMethod
     public JavaObject set_static_value(IRubyObject value) {
         if (! (value instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object:" + value);
         }
         try {
             Object convertedValue = JavaUtil.convertArgument(getRuntime(), ((JavaObject) value).getValue(),
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
     
     @JRubyMethod
     public RubyString name() {
         return getRuntime().newString(field.getName());
     }
     
     protected AccessibleObject accessibleObject() {
         return field;
     }
 }
diff --git a/src/org/jruby/javasupport/JavaMethod.java b/src/org/jruby/javasupport/JavaMethod.java
index f89fe57f90..61b7fdb772 100644
--- a/src/org/jruby/javasupport/JavaMethod.java
+++ b/src/org/jruby/javasupport/JavaMethod.java
@@ -1,383 +1,387 @@
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
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.javasupport.proxy.InternalJavaProxy;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 @JRubyClass(name="Java::JavaMethod")
 public class JavaMethod extends JavaCallable {
     private final Method method;
     private final Class<?>[] parameterTypes;
     private final JavaUtil.JavaConverter returnConverter;
 
+    public Object getValue() {
+        return method;
+    }
+
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
         super(runtime, runtime.getJavaSupport().getJavaMethodClass());
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
     
     public boolean equals(Object other) {
         return other instanceof JavaMethod &&
             this.method == ((JavaMethod)other).method;
     }
     
     public int hashCode() {
         return method.hashCode();
     }
 
     @JRubyMethod
     public RubyString name() {
         return getRuntime().newString(method.getName());
     }
 
     public int getArity() {
         return parameterTypes.length;
     }
 
     @JRubyMethod(name = "public?")
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(method.getModifiers()));
     }
 
     @JRubyMethod(name = "final?")
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(method.getModifiers()));
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject invoke(IRubyObject[] args) {
         if (args.length != 1 + getArity()) {
             throw getRuntime().newArgumentError(args.length, 1 + getArity());
         }
 
         Object[] arguments = new Object[args.length - 1];
         convertArguments(getRuntime(), arguments, args, 1);
 
         IRubyObject invokee = args[0];
         if(invokee.isNil()) {
             return invokeWithExceptionHandling(method, null, arguments);
         }
 
         Object javaInvokee = JavaUtil.unwrapJavaObject(getRuntime(), invokee, "invokee not a java object").getValue();
 
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
 
     public IRubyObject invoke(IRubyObject self, Object[] args) {
         if (args.length != getArity()) {
             throw getRuntime().newArgumentError(args.length, getArity());
         }
 
         if (! (self instanceof JavaObject)) {
             throw getRuntime().newTypeError("invokee not a java object");
         }
         Object javaInvokee = ((JavaObject) self).getValue();
 
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
                 return invokeWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, args);
             }
         }
         return invokeWithExceptionHandling(method, javaInvokee, args);
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject invoke_static(IRubyObject[] args) {
         if (args.length != getArity()) {
             throw getRuntime().newArgumentError(args.length, getArity());
         }
         Object[] arguments = new Object[args.length];
         System.arraycopy(args, 0, arguments, 0, arguments.length);
         convertArguments(getRuntime(), arguments, args, 0);
         return invokeWithExceptionHandling(method, null, arguments);
     }
 
     public IRubyObject invoke_static(Object[] args) {
         if (args.length != getArity()) {
             throw getRuntime().newArgumentError(args.length, getArity());
         }
 
         return invokeWithExceptionHandling(method, null, args);
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
 
     private IRubyObject invokeWithExceptionHandling(Method method, Object javaInvokee, Object[] arguments) {
         try {
             Object result = method.invoke(javaInvokee, arguments);
             return returnConverter.convert(getRuntime(), result);
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError("for method " + method.getName() + " expected " + argument_types().inspect() + "; got: "
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
         StringBuilder str = new StringBuilder("[");
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
 
     private void convertArguments(Ruby runtime, Object[] arguments, Object[] args, int from) {
         Class<?>[] types = parameterTypes;
         for (int i = arguments.length; --i >= 0; ) {
             arguments[i] = JavaUtil.convertArgument(runtime, args[i+from], types[i]);
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
 }
diff --git a/src/org/jruby/javasupport/JavaUtil.java b/src/org/jruby/javasupport/JavaUtil.java
index 3dc319d4d8..f2fadbd194 100644
--- a/src/org/jruby/javasupport/JavaUtil.java
+++ b/src/org/jruby/javasupport/JavaUtil.java
@@ -213,1001 +213,1017 @@ public class JavaUtil {
         }
     };
     
     public static final RubyConverter ARRAY_CHAR_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Character.valueOf((char)((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return Character.valueOf(rubyObject.asJavaString().charAt(0));
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Character.valueOf((char)integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_INT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Integer.valueOf((int)((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return Integer.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Integer.valueOf((int)integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_LONG_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Long.valueOf(((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return Long.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Long.valueOf(integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_FLOAT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Float.valueOf((float)((RubyNumeric)rubyObject).getDoubleValue());
             } else if (rubyObject instanceof RubyString) {
                 return Float.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Float.valueOf((float)integer.getDoubleValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_DOUBLE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Double.valueOf(((RubyNumeric)rubyObject).getDoubleValue());
             } else if (rubyObject instanceof RubyString) {
                 return Double.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Double.valueOf(integer.getDoubleValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_OBJECT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyInteger) {
                 long value = ((RubyInteger)rubyObject).getLongValue();
                 if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                     return Integer.valueOf((int)value);
                 } else if (value >= Long.MIN_VALUE && value <= Long.MAX_VALUE) {
                     return Long.valueOf(value);
                 } else {
                     return new BigInteger(rubyObject.toString());
                 }
             } else if (rubyObject instanceof RubyFloat) {
                 return Double.valueOf(((RubyFloat)rubyObject).getDoubleValue());
             } else if (rubyObject instanceof JavaProxy) {
                 return ((JavaProxy)rubyObject).unwrap();
             } else {
                 return java_to_ruby(context.getRuntime(), rubyObject);
             }
         }
     };
     
     public static final RubyConverter ARRAY_CLASS_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof JavaClass) {
                 return ((JavaClass)rubyObject).javaClass();
             } else {
                 return java_to_ruby(context.getRuntime(), rubyObject);
             }
         }
     };
 
     public static final RubyConverter ARRAY_STRING_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyString) {
                 return ((RubyString)rubyObject).getUnicodeValue();
             } else {
                 return rubyObject.toString();
             }
         }
     };
     
     public static final RubyConverter ARRAY_BIGINTEGER_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return BigInteger.valueOf(((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return new BigDecimal(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return BigInteger.valueOf(integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_BIGDECIMAL_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return BigDecimal.valueOf(((RubyNumeric)rubyObject).getDoubleValue());
             } else if (rubyObject instanceof RubyString) {
                 return new BigDecimal(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_f")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_f");
                 return BigDecimal.valueOf(integer.getDoubleValue());
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return BigDecimal.valueOf(integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final Map<Class, RubyConverter> RUBY_CONVERTERS = new HashMap<Class, RubyConverter>();
     public static final Map<Class, RubyConverter> ARRAY_CONVERTERS = new HashMap<Class, RubyConverter>();
     
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
         
         ARRAY_CONVERTERS.put(Boolean.class, ARRAY_BOOLEAN_CONVERTER);
         ARRAY_CONVERTERS.put(Boolean.TYPE, ARRAY_BOOLEAN_CONVERTER);
         ARRAY_CONVERTERS.put(Byte.class, ARRAY_BYTE_CONVERTER);
         ARRAY_CONVERTERS.put(Byte.TYPE, ARRAY_BYTE_CONVERTER);
         ARRAY_CONVERTERS.put(Short.class, ARRAY_SHORT_CONVERTER);
         ARRAY_CONVERTERS.put(Short.TYPE, ARRAY_SHORT_CONVERTER);
         ARRAY_CONVERTERS.put(Character.class, ARRAY_CHAR_CONVERTER);
         ARRAY_CONVERTERS.put(Character.TYPE, ARRAY_CHAR_CONVERTER);
         ARRAY_CONVERTERS.put(Integer.class, ARRAY_INT_CONVERTER);
         ARRAY_CONVERTERS.put(Integer.TYPE, ARRAY_INT_CONVERTER);
         ARRAY_CONVERTERS.put(Long.class, ARRAY_LONG_CONVERTER);
         ARRAY_CONVERTERS.put(Long.TYPE, ARRAY_LONG_CONVERTER);
         ARRAY_CONVERTERS.put(Float.class, ARRAY_FLOAT_CONVERTER);
         ARRAY_CONVERTERS.put(Float.TYPE, ARRAY_FLOAT_CONVERTER);
         ARRAY_CONVERTERS.put(Double.class, ARRAY_DOUBLE_CONVERTER);
         ARRAY_CONVERTERS.put(Double.TYPE, ARRAY_DOUBLE_CONVERTER);
         ARRAY_CONVERTERS.put(String.class, ARRAY_STRING_CONVERTER);
         ARRAY_CONVERTERS.put(Class.class, ARRAY_CLASS_CONVERTER);
         ARRAY_CONVERTERS.put(BigInteger.class, ARRAY_BIGINTEGER_CONVERTER);
         ARRAY_CONVERTERS.put(BigDecimal.class, ARRAY_BIGDECIMAL_CONVERTER);
     }
     
     public static RubyConverter getArrayConverter(Class type) {
         RubyConverter converter = ARRAY_CONVERTERS.get(type);
         if (converter == null) {
             return ARRAY_OBJECT_CONVERTER;
         }
         return converter;
     }
     
     public static byte convertRubyToJavaByte(IRubyObject rubyObject) {
         return ((Byte)convertRubyToJava(rubyObject, byte.class)).byteValue();
     }
     
     public static short convertRubyToJavaShort(IRubyObject rubyObject) {
         return ((Short)convertRubyToJava(rubyObject, short.class)).shortValue();
     }
     
     public static char convertRubyToJavaChar(IRubyObject rubyObject) {
         return ((Character)convertRubyToJava(rubyObject, char.class)).charValue();
     }
     
     public static int convertRubyToJavaInt(IRubyObject rubyObject) {
         return ((Integer)convertRubyToJava(rubyObject, int.class)).intValue();
     }
     
     public static long convertRubyToJavaLong(IRubyObject rubyObject) {
         return ((Long)convertRubyToJava(rubyObject, long.class)).longValue();
     }
     
     public static float convertRubyToJavaFloat(IRubyObject rubyObject) {
         return ((Float)convertRubyToJava(rubyObject, float.class)).floatValue();
     }
     
     public static double convertRubyToJavaDouble(IRubyObject rubyObject) {
         return ((Double)convertRubyToJava(rubyObject, double.class)).doubleValue();
     }
     
     public static boolean convertRubyToJavaBoolean(IRubyObject rubyObject) {
         return ((Boolean)convertRubyToJava(rubyObject, boolean.class)).booleanValue();
     }
 
     public static Object convertRubyToJava(IRubyObject rubyObject, Class javaClass) {
         if (javaClass == void.class || rubyObject == null || rubyObject.isNil()) {
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
             
             return convertArgument(rubyObject.getRuntime(), value, value.getClass());
             
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
                 return converter.convert(context, rubyObject);
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
  				return  BigInteger.valueOf (((RubyNumeric)rubyObject).getLongValue());
          	} else if (rubyObject.respondsTo("to_i")) {
          		RubyNumeric rubyNumeric = ((RubyNumeric)rubyObject.callMethod(context,MethodIndex.TO_F, "to_f"));
  				return  BigInteger.valueOf (rubyNumeric.getLongValue());
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
             if (rubyObject.getRuntime().getDebug().isTrue()) ex.printStackTrace();
             return null;
         }
     }
 
     public static IRubyObject[] convertJavaArrayToRuby(Ruby runtime, Object[] objects) {
         if (objects == null) return IRubyObject.NULL_ARRAY;
         
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
  
             // Note: returns JavaObject instance, which is not
             // directly usable. probably too late to change this now,
             // supplying alternate method convertJavaToUsableRubyObject
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
     
     private static final Map<Class,JavaConverter> JAVA_CONVERTERS =
         new HashMap<Class,JavaConverter>();
     
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
         JavaConverter converter = JAVA_CONVERTERS.get(clazz);
         
         if (converter == null) {
             converter = JAVA_DEFAULT_CONVERTER;
         }
         
         return converter;
     }
 
     /**
      * Converts object to the corresponding Ruby type; however, for non-primitives,
      * a JavaObject instance is returned. This must be subsequently wrapped by
      * calling one of Java.wrap, Java.java_to_ruby, Java.new_instance_for, or
      * Java.getInstance, depending on context.
      * 
      * @param runtime
      * @param object 
      * @return corresponding Ruby type, or a JavaObject instance
      */
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object) {
         if (object == null) {
             return runtime.getNil();
         } else if (object instanceof IRubyObject) {
             return (IRubyObject)object;
         }
         return convertJavaToRuby(runtime, object, object.getClass());
     }
     
     public static IRubyObject convertJavaToRuby(Ruby runtime, int i) {
         return runtime.newFixnum(i);
     }
     
     public static IRubyObject convertJavaToRuby(Ruby runtime, long l) {
         return runtime.newFixnum(l);
     }
     
     public static IRubyObject convertJavaToRuby(Ruby runtime, float f) {
         return runtime.newFloat(f);
     }
     
     public static IRubyObject convertJavaToRuby(Ruby runtime, double d) {
         return runtime.newFloat(d);
     }
     
     public static IRubyObject convertJavaToRuby(Ruby runtime, boolean b) {
         return runtime.newBoolean(b);
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object, Class javaClass) {
         return getJavaConverter(javaClass).convert(runtime, object);
     }
     
     /**
      * Returns a usable RubyObject; for types that are not converted to Ruby native
      * types, a Java proxy will be returned. 
      * 
      * @param runtime
      * @param object
      * @return corresponding Ruby type, or a functional Java proxy
      */
     public static IRubyObject convertJavaToUsableRubyObject(Ruby runtime, Object object) {
         if (object == null) return runtime.getNil();
         
         // if it's already IRubyObject, don't re-wrap (JRUBY-2480)
         if (object instanceof IRubyObject) {
             return (IRubyObject)object;
         }
         
         JavaConverter converter = JAVA_CONVERTERS.get(object.getClass());
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             return Java.getInstance(runtime, object);
         }
         return converter.convert(runtime, object);
     }
 
     public static Class<?> primitiveToWrapper(Class<?> type) {
         if (type.isPrimitive()) {
             if (type == Integer.TYPE) {
                 return Integer.class;
             } else if (type == Double.TYPE) {
                 return Double.class;
             } else if (type == Boolean.TYPE) {
                 return Boolean.class;
             } else if (type == Byte.TYPE) {
                 return Byte.class;
             } else if (type == Character.TYPE) {
                 return Character.class;
             } else if (type == Float.TYPE) {
                 return Float.class;
             } else if (type == Long.TYPE) {
                 return Long.class;
             } else if (type == Void.TYPE) {
                 return Void.class;
             } else if (type == Short.TYPE) {
                 return Short.class;
             }
         }
         return type;
     }
 
     public static Object convertArgument(Ruby runtime, Object argument, Class<?> parameterType) {
         if (argument == null) {
           if(parameterType.isPrimitive()) {
             throw runtime.newTypeError("primitives do not accept null");
           } else {
             return null;
           }
         }
         
         if (argument instanceof JavaObject) {
             argument = ((JavaObject) argument).getValue();
             if (argument == null) {
                 return null;
             }
         }
         Class<?> type = primitiveToWrapper(parameterType);
         if (type == Void.class) {
             return null;
         }
         if (argument instanceof Number) {
             final Number number = (Number) argument;
             if (type == Long.class) {
                 return new Long(number.longValue());
             } else if (type == Integer.class) {
                 return new Integer(number.intValue());
             } else if (type == Byte.class) {
                 return new Byte(number.byteValue());
             } else if (type == Character.class) {
                 return new Character((char) number.intValue());
             } else if (type == Double.class) {
                 return new Double(number.doubleValue());
             } else if (type == Float.class) {
                 return new Float(number.floatValue());
             } else if (type == Short.class) {
                 return new Short(number.shortValue());
             }
         }
         if (isDuckTypeConvertable(argument.getClass(), parameterType)) {
             RubyObject rubyObject = (RubyObject) argument;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(runtime.getCurrentContext(), rubyObject, parameterType);
             }
         }
         return argument;
     }
     
     public static boolean isDuckTypeConvertable(Class providedArgumentType, Class parameterType) {
         return parameterType.isInterface() && !parameterType.isAssignableFrom(providedArgumentType) 
             && RubyObject.class.isAssignableFrom(providedArgumentType);
     }
     
     public static Object convertProcToInterface(ThreadContext context, RubyObject rubyObject, Class target) {
         Ruby runtime = context.getRuntime();
         IRubyObject javaUtilities = runtime.getJavaSupport().getJavaUtilitiesModule();
         IRubyObject javaInterfaceModule = Java.get_interface_module(javaUtilities, JavaClass.get(runtime, target));
         if (!((RubyModule) javaInterfaceModule).isInstance(rubyObject)) {
             rubyObject.extend(new IRubyObject[]{javaInterfaceModule});
         }
 
         if (rubyObject instanceof RubyProc) {
             // Proc implementing an interface, pull in the catch-all code that lets the proc get invoked
             // no matter what method is called on the interface
             RubyClass singletonClass = rubyObject.getSingletonClass();
             final RubyProc proc = (RubyProc) rubyObject;
 
             singletonClass.addMethod("method_missing", new DynamicMethod(singletonClass, Visibility.PUBLIC, CallConfiguration.NO_FRAME_NO_SCOPE) {
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     IRubyObject[] newArgs;
                     if (args.length == 1) {
                         newArgs = IRubyObject.NULL_ARRAY;
                     } else {
                         newArgs = new IRubyObject[args.length - 1];
                         System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                     }
                     return proc.call(context, newArgs);
                 }
 
                 @Override
                 public DynamicMethod dup() {
                     return this;
                 }
             });
         }
         JavaObject jo = (JavaObject) rubyObject.instance_eval(context, runtime.newString("send :__jcreate_meta!"), Block.NULL_BLOCK);
         return jo.getValue();
     }
 
     public static Object convertArgumentToType(ThreadContext context, IRubyObject arg, Class target) {
         if (arg instanceof JavaObject) {
             return coerceJavaObjectToType(context, ((JavaObject)arg).getValue(), target);
         } else if (arg.dataGetStruct() instanceof JavaObject) {
             JavaObject innerWrapper = (JavaObject)arg.dataGetStruct();
             
             // ensure the object is associated with the wrapper we found it in,
             // so that if it comes back we don't re-wrap it
             context.getRuntime().getJavaSupport().getObjectProxyCache().put(innerWrapper.getValue(), arg);
             
             return innerWrapper.getValue();
         } else {
             switch (arg.getMetaClass().index) {
             case ClassIndex.NIL:
                 return coerceNilToType((RubyNil)arg, target);
             case ClassIndex.FIXNUM:
                 return coerceFixnumToType((RubyFixnum)arg, target);
             case ClassIndex.BIGNUM:
                 return coerceBignumToType((RubyBignum)arg, target);
             case ClassIndex.FLOAT:
                 return coerceFloatToType((RubyFloat)arg, target);
             case ClassIndex.STRING:
                 return coerceStringToType((RubyString)arg, target);
             case ClassIndex.TRUE:
                 return Boolean.TRUE;
             case ClassIndex.FALSE:
                 return Boolean.FALSE;
             case ClassIndex.TIME:
                 return ((RubyTime) arg).getJavaDate();
             default:
                 return coerceOtherToType(context, arg, target);
             }
         }
     }
     
     public static Object coerceJavaObjectToType(ThreadContext context, Object javaObject, Class target) {
         if (isDuckTypeConvertable(javaObject.getClass(), target)) {
             RubyObject rubyObject = (RubyObject) javaObject;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(context, rubyObject, target);
             }
 
             // can't be converted any more, return it
             return javaObject;
         } else {
             return javaObject;
         }
     }
     
     public static Object coerceNilToType(RubyNil nil, Class target) {
         if(target.isPrimitive()) {
             throw nil.getRuntime().newTypeError("primitives do not accept null");
         } else {
             return null;
         }
     }
     
     public static Object coerceFixnumToType(RubyFixnum fixnum, Class target) {
         if (target.isPrimitive()) {
             if (target == Integer.TYPE) {
                 return Integer.valueOf((int)fixnum.getLongValue());
             } else if (target == Double.TYPE) {
                 return Double.valueOf(fixnum.getLongValue());
             } else if (target == Byte.TYPE) {
                 return Byte.valueOf((byte)fixnum.getLongValue());
             } else if (target == Character.TYPE) {
                 return Character.valueOf((char)fixnum.getLongValue());
             } else if (target == Float.TYPE) {
                 return Float.valueOf((float)fixnum.getLongValue());
             } else if (target == Long.TYPE) {
                 return Long.valueOf(fixnum.getLongValue());
             } else if (target == Short.TYPE) {
                 return Short.valueOf((short)fixnum.getLongValue());
             }
         }
         return Long.valueOf(fixnum.getLongValue());
     }
         
     public static Object coerceBignumToType(RubyBignum bignum, Class target) {
         if (target.isPrimitive()) {
             if (target == Integer.TYPE) {
                 return Integer.valueOf((int)bignum.getLongValue());
             } else if (target == Double.TYPE) {
                 return Double.valueOf(bignum.getLongValue());
             } else if (target == Byte.TYPE) {
                 return Byte.valueOf((byte)bignum.getLongValue());
             } else if (target == Character.TYPE) {
                 return Character.valueOf((char)bignum.getLongValue());
             } else if (target == Float.TYPE) {
                 return Float.valueOf((float)bignum.getLongValue());
             } else if (target == Long.TYPE) {
                 return Long.valueOf(bignum.getLongValue());
             } else if (target == Short.TYPE) {
                 return Short.valueOf((short)bignum.getLongValue());
             }
         }
         return bignum.getValue();
     }
     
     public static Object coerceFloatToType(RubyFloat flote, Class target) {
         if (target.isPrimitive()) {
             if (target == Integer.TYPE) {
                 return Integer.valueOf((int)flote.getLongValue());
             } else if (target == Double.TYPE) {
                 return Double.valueOf(flote.getDoubleValue());
             } else if (target == Byte.TYPE) {
                 return Byte.valueOf((byte)flote.getLongValue());
             } else if (target == Character.TYPE) {
                 return Character.valueOf((char)flote.getLongValue());
             } else if (target == Float.TYPE) {
                 return Float.valueOf((float)flote.getDoubleValue());
             } else if (target == Long.TYPE) {
                 return Long.valueOf(flote.getLongValue());
             } else if (target == Short.TYPE) {
                 return Short.valueOf((short)flote.getLongValue());
             }
         }
         return Double.valueOf(flote.getDoubleValue());
     }
     
     public static Object coerceStringToType(RubyString string, Class target) {
         try {
             ByteList bytes = string.getByteList();
             return new String(bytes.unsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
         } catch (UnsupportedEncodingException uee) {
             return string.toString();
         }
     }
     
     public static Object coerceOtherToType(ThreadContext context, IRubyObject arg, Class target) {
         Ruby runtime = context.getRuntime();
         
         if (isDuckTypeConvertable(arg.getClass(), target)) {
             RubyObject rubyObject = (RubyObject) arg;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(context, rubyObject, target);
             }
         } else if (arg.respondsTo("to_java_object")) {
             Object javaObject = arg.callMethod(context, "to_java_object");
             if (javaObject instanceof JavaObject) {
                 runtime.getJavaSupport().getObjectProxyCache().put(((JavaObject) javaObject).getValue(), arg);
                 javaObject = ((JavaObject)javaObject).getValue();
             }
             return javaObject;
         }
 
         // it's either as converted as we can make it via above logic or it's
         // not one of the types we convert, so just pass it out as-is without wrapping
         return arg;
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
         case ClassIndex.TIME:
             javaObject = ((RubyTime) object).getJavaDate();
             break;
         default:
             // it's not one of the types we convert, so just pass it out as-is without wrapping
             return object;
         }
 
         // we've found a Java type to which we've coerced the Ruby value, wrap it
         return JavaObject.wrap(runtime, javaObject);
     }
 
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version 
      */
     public static IRubyObject java_to_ruby(Ruby runtime, IRubyObject object) {
         if (object instanceof JavaObject) {
             return JavaUtil.convertJavaToUsableRubyObject(runtime, ((JavaObject) object).getValue());
         }
         return object;
     }
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     /**
      * High-level object conversion utility. 
      */
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object.respondsTo("to_java_object")) {
             IRubyObject result = (JavaObject)object.dataGetStruct();
             if (result == null) {
                 result = object.callMethod(recv.getRuntime().getCurrentContext(), "to_java_object");
             }
             if (result instanceof JavaObject) {
                 recv.getRuntime().getJavaSupport().getObjectProxyCache().put(((JavaObject) result).getValue(), object);
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
     
     public static boolean isJavaObject(IRubyObject candidate) {
         return candidate.dataGetStruct() instanceof JavaObject;
     }
     
     public static Object unwrapJavaObject(IRubyObject object) {
         return ((JavaObject)object.dataGetStruct()).getValue();
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
         return m.replaceAll("$1_$2").toLowerCase();
     }
 
     private static final Pattern RUBY_CASE_SPLITTER = Pattern.compile("([a-z][0-9]*)_([a-z])");    
     public static String getJavaCasedName(String javaCasedName) {
         Matcher m = RUBY_CASE_SPLITTER.matcher(javaCasedName);
         StringBuffer newName = new StringBuffer();
         if (!m.find()) {
             return null;
         }
         m.reset();
 
         while (m.find()) {
             m.appendReplacement(newName, m.group(1) + Character.toUpperCase(m.group(2).charAt(0)));
         }
 
         m.appendTail(newName);
 
         return newName.toString();
     }
     
     /**
      * Given a simple Java method name and the Java Method objects that represent
      * all its overloads, add to the given nameSet all possible Ruby names that would
      * be valid.
      * 
      * @param simpleName
      * @param nameSet
      * @param methods
      */
     public static Set<String> getRubyNamesForJavaName(String javaName, List<Method> methods) {
         String javaPropertyName = JavaUtil.getJavaPropertyName(javaName);
         String rubyName = JavaUtil.getRubyCasedName(javaName);
         Set<String> nameSet = new LinkedHashSet<String>();
         nameSet.add(javaName);
         nameSet.add(rubyName);
         String rubyPropertyName = null;
         for (Method method: methods) {
             Class<?>[] argTypes = method.getParameterTypes();
             Class<?> resultType = method.getReturnType();
             int argCount = argTypes.length;
 
             // Add property name aliases
             if (javaPropertyName != null) {
                 if (rubyName.startsWith("get_")) {
                     rubyPropertyName = rubyName.substring(4);
                     if (argCount == 0 ||                                // getFoo      => foo
                         argCount == 1 && argTypes[0] == int.class) {    // getFoo(int) => foo(int)
 
                         nameSet.add(javaPropertyName);
                         nameSet.add(rubyPropertyName);
                         if (resultType == boolean.class) {              // getFooBar() => fooBar?, foo_bar?(*)
                             nameSet.add(javaPropertyName + '?');
                             nameSet.add(rubyPropertyName + '?');
                         }
                     }
                 } else if (rubyName.startsWith("set_")) {
                     rubyPropertyName = rubyName.substring(4);
                     if (argCount == 1 && resultType == void.class) {    // setFoo(Foo) => foo=(Foo)
                         nameSet.add(javaPropertyName + '=');
                         nameSet.add(rubyPropertyName + '=');
                     }
                 } else if (rubyName.startsWith("is_")) {
                     rubyPropertyName = rubyName.substring(3);
                     if (resultType == boolean.class) {                  // isFoo() => foo, isFoo(*) => foo(*)
                         nameSet.add(javaPropertyName);
                         nameSet.add(rubyPropertyName);
                         nameSet.add(javaPropertyName + '?');
                         nameSet.add(rubyPropertyName + '?');
                     }
                 }
             } else {
                 // If not a property, but is boolean add ?-postfixed aliases.
                 if (resultType == boolean.class) {
                     // is_something?, contains_thing?
                     nameSet.add(javaName + '?');
                     nameSet.add(rubyName + '?');
                 }
             }
         }
         
         return nameSet;
     }
 
     public static JavaObject unwrapJavaObject(Ruby runtime, IRubyObject convertee, String errorMessage) {
         IRubyObject obj = convertee;
         if(!(obj instanceof JavaObject)) {
             if (obj.dataGetStruct() != null && (obj.dataGetStruct() instanceof JavaObject)) {
                 obj = (JavaObject)obj.dataGetStruct();
             } else {
                 throw runtime.newTypeError(errorMessage);
             }
         }
         return (JavaObject)obj;
     }
+
+    public static Object unwrapJavaValue(Ruby runtime, IRubyObject obj, String errorMessage) {
+        if(obj instanceof JavaMethod) {
+            return ((JavaMethod)obj).getValue();
+        } else if(obj instanceof JavaConstructor) {
+            return ((JavaConstructor)obj).getValue();
+        } else if(obj instanceof JavaField) {
+            return ((JavaField)obj).getValue();
+        } else if(obj instanceof JavaObject) {
+            return ((JavaObject)obj).getValue();
+        } else if(obj.dataGetStruct() != null && (obj.dataGetStruct() instanceof IRubyObject)) {
+            return unwrapJavaValue(runtime, ((IRubyObject)obj.dataGetStruct()), errorMessage);
+        } else {
+            throw runtime.newTypeError(errorMessage);
+        }
+    }
 }
