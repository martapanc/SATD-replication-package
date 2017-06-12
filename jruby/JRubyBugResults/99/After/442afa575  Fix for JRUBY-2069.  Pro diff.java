diff --git a/src/org/jruby/javasupport/JavaAccessibleObject.java b/src/org/jruby/javasupport/JavaAccessibleObject.java
index c01d712aba..c8f8c167aa 100644
--- a/src/org/jruby/javasupport/JavaAccessibleObject.java
+++ b/src/org/jruby/javasupport/JavaAccessibleObject.java
@@ -1,81 +1,90 @@
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyObject;
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
 	}
-	protected abstract AccessibleObject accesibleObject();
+	protected abstract AccessibleObject accessibleObject();
+
+    public boolean equals(Object other) {
+        return other instanceof JavaAccessibleObject &&
+            this.accessibleObject() == ((JavaAccessibleObject)other).accessibleObject();
+    }
+    
+    public int hashCode() {
+        return this.accessibleObject().hashCode();
+    }
 
 	public RubyFixnum hash() {
-		return getRuntime().newFixnum(accesibleObject().hashCode());
+		return getRuntime().newFixnum(hashCode());
     }
 
     public IRubyObject op_equal(IRubyObject other) {
-		return other instanceof JavaAccessibleObject && accesibleObject().equals(((JavaAccessibleObject)other).accesibleObject()) ? getRuntime().getTrue() : getRuntime().getFalse();
+		return other instanceof JavaAccessibleObject && accessibleObject().equals(((JavaAccessibleObject)other).accessibleObject()) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
    
 	public IRubyObject same(IRubyObject other) {
-		return other instanceof JavaAccessibleObject && accesibleObject() == ((JavaAccessibleObject)other).accesibleObject() ? getRuntime().getTrue() : getRuntime().getFalse();
+        return getRuntime().newBoolean(equals(other));
 	}
        
 	public RubyBoolean isAccessible() {
-		return new RubyBoolean(getRuntime(),accesibleObject().isAccessible());
+		return new RubyBoolean(getRuntime(),accessibleObject().isAccessible());
 	}
 
 	public IRubyObject setAccessible(IRubyObject object) {
-	    accesibleObject().setAccessible(object.isTrue());
+	    accessibleObject().setAccessible(object.isTrue());
 		return object;
 	}
 
 }
diff --git a/src/org/jruby/javasupport/JavaArray.java b/src/org/jruby/javasupport/JavaArray.java
index 9de28a501b..9863d2dccd 100644
--- a/src/org/jruby/javasupport/JavaArray.java
+++ b/src/org/jruby/javasupport/JavaArray.java
@@ -1,133 +1,139 @@
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
+import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaArray extends JavaObject {
 
     public JavaArray(Ruby runtime, Object array) {
         super(runtime, runtime.getJavaSupport().getJavaArrayClass(), array);
         assert array.getClass().isArray();
     }
 
     public static RubyClass createJavaArrayClass(Ruby runtime, RubyModule javaModule) {
         // FIXME: NOT_ALLOCATABLE_ALLOCATOR is probably not right here, since we might
         // eventually want JavaArray to be marshallable. JRUBY-414
         return javaModule.defineClassUnder("JavaArray", javaModule.fastGetClass("JavaObject"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
     }
 
     public RubyFixnum length() {
         return getRuntime().newFixnum(getLength());
     }
 
     private int getLength() {
         return Array.getLength(getValue());
     }
 
+    public boolean equals(Object other) {
+        return other instanceof JavaArray &&
+            this.getValue() == ((JavaArray)other).getValue();
+    }
+    
     public IRubyObject aref(IRubyObject index) {
         if (! (index instanceof RubyInteger)) {
             throw getRuntime().newTypeError(index, getRuntime().getInteger());
         }
         int intIndex = (int) ((RubyInteger) index).getLongValue();
         if (intIndex < 0 || intIndex >= getLength()) {
             throw getRuntime().newArgumentError(
                                     "index out of bounds for java array (" + intIndex +
                                     " for length " + getLength() + ")");
         }
         Object result = Array.get(getValue(), intIndex);
         if (result == null) {
             return getRuntime().getNil();
         }
         return JavaObject.wrap(getRuntime(), result);
     }
 
     public IRubyObject aset(IRubyObject index, IRubyObject value) {
          if (! (index instanceof RubyInteger)) {
             throw getRuntime().newTypeError(index, getRuntime().getInteger());
         }
         int intIndex = (int) ((RubyInteger) index).getLongValue();
         if (! (value instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object:" + value);
         }
         Object javaObject = ((JavaObject) value).getValue();
         try {
             Array.set(getValue(), intIndex, javaObject);
         } catch (IndexOutOfBoundsException e) {
             throw getRuntime().newArgumentError(
                                     "index out of bounds for java array (" + intIndex +
                                     " for length " + getLength() + ")");
         } catch (ArrayStoreException e) {
             throw getRuntime().newArgumentError(
                                     "wrong element type " + javaObject.getClass() + "(array is " +
                                     getValue().getClass() + ")");
         }
         return value;
     }
 
     public IRubyObject afill(IRubyObject beginIndex, IRubyObject endIndex, IRubyObject value) {
         if (! (beginIndex instanceof RubyInteger)) {
             throw getRuntime().newTypeError(beginIndex, getRuntime().getInteger());
         }
         int intIndex = (int) ((RubyInteger) beginIndex).getLongValue();
         if (! (endIndex instanceof RubyInteger)) {
             throw getRuntime().newTypeError(endIndex, getRuntime().getInteger());
         }
         int intEndIndex = (int) ((RubyInteger) endIndex).getLongValue();
         if (! (value instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object:" + value);
         }
         Object javaObject = ((JavaObject) value).getValue();
         Object self = getValue();
         try {
           for ( ; intIndex < intEndIndex; intIndex++) {
             Array.set(self, intIndex, javaObject);
           }
         } catch (IndexOutOfBoundsException e) {
             throw getRuntime().newArgumentError(
                                     "index out of bounds for java array (" + intIndex +
                                     " for length " + getLength() + ")");
         } catch (ArrayStoreException e) {
             throw getRuntime().newArgumentError(
                                     "wrong element type " + javaObject.getClass() + "(array is " +
                                     getValue().getClass() + ")");
         }
         return value;
     }
 }
diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index f4a5563d54..990e663794 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,1370 +1,1375 @@
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
         void addMethod(Method method, Class javaClass) {
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
     private Map staticCallbacks;
     private Map instanceCallbacks;
     private List<ConstantField> constantFields;
     // caching constructors, as they're accessed for each new instance
-    private RubyArray constructors;
+    private volatile RubyArray constructors;
     
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
     
     private JavaClass(Ruby runtime, Class javaClass) {
         super(runtime, (RubyClass) runtime.getJavaSupport().getJavaClassClass(), javaClass);
         if (javaClass.isInterface()) {
             initializeInterface(javaClass);
         } else if (!(javaClass.isArray() || javaClass.isPrimitive())) {
             // TODO: public only?
             initializeClass(javaClass);
         }
     }
     
+    public boolean equals(Object other) {
+        return other instanceof JavaClass &&
+            this.getValue() == ((JavaClass)other).getValue();
+    }
+    
     private void initializeInterface(Class javaClass) {
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
 
     private void initializeClass(Class javaClass) {
         Class superclass = javaClass.getSuperclass();
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
         Map staticCallbacks = new HashMap();
         Map instanceCallbacks = new HashMap();
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
 
         for (ConstantField field: constantFields) {
             field.install(proxy);
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
         final Class javaClass = javaClass();
         for (ConstantField field: constantFields) {
             field.install(module);
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
     
     public static JavaClass get(Ruby runtime, Class klass) {
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
diff --git a/src/org/jruby/javasupport/JavaConstructor.java b/src/org/jruby/javasupport/JavaConstructor.java
index 25b753bbc5..df0b709ac1 100644
--- a/src/org/jruby/javasupport/JavaConstructor.java
+++ b/src/org/jruby/javasupport/JavaConstructor.java
@@ -1,122 +1,131 @@
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
 
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaConstructor extends JavaCallable {
     private final Constructor constructor;
     private final Class[] parameterTypes;
 
     public static RubyClass createJavaConstructorClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result =
                 javaModule.defineClassUnder("JavaConstructor", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(JavaConstructor.class);
 
         JavaCallable.registerRubyMethods(runtime, result, JavaConstructor.class);
         result.defineFastMethod("arity", callbackFactory.getFastMethod("arity"));
         result.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         result.defineFastMethod("argument_types", callbackFactory.getFastMethod("argument_types"));
         result.defineFastMethod("new_instance", callbackFactory.getFastOptMethod("new_instance"));
         
         return result;
     }
 
     public JavaConstructor(Ruby runtime, Constructor constructor) {
         super(runtime, runtime.getJavaSupport().getJavaModule().fastGetClass("JavaConstructor"));
         this.constructor = constructor;
         this.parameterTypes = constructor.getParameterTypes();
     }
 
+    public boolean equals(Object other) {
+        return other instanceof JavaConstructor &&
+            this.constructor == ((JavaConstructor)other).constructor;
+    }
+    
+    public int hashCode() {
+        return constructor.hashCode();
+    }
+
     public int getArity() {
         return parameterTypes.length;
     }
 
     public IRubyObject new_instance(IRubyObject[] args) {
         if (args.length != getArity()) {
             throw getRuntime().newArgumentError(args.length, getArity());
         }
         Object[] constructorArguments = new Object[args.length];
         Class[] types = parameterTypes;
         for (int i = 0; i < args.length; i++) {
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
 
 
     protected String nameOnInspection() {
         return getType().toString();
     }
 
     protected Class[] parameterTypes() {
         return parameterTypes;
     }
 
     protected int getModifiers() {
         return constructor.getModifiers();
     }
 
-    protected AccessibleObject accesibleObject() {
+    protected AccessibleObject accessibleObject() {
         return constructor;
     }
 }
diff --git a/src/org/jruby/javasupport/JavaField.java b/src/org/jruby/javasupport/JavaField.java
index a8b30c05b0..877f510b71 100644
--- a/src/org/jruby/javasupport/JavaField.java
+++ b/src/org/jruby/javasupport/JavaField.java
@@ -1,190 +1,199 @@
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
 
         return result;
     }
 
     public JavaField(Ruby runtime, Field field) {
         super(runtime, (RubyClass) runtime.getJavaSupport().getJavaModule().fastGetClass("JavaField"));
         this.field = field;
     }
 
+    public boolean equals(Object other) {
+        return other instanceof JavaField &&
+            this.field == ((JavaField)other).field;
+    }
+    
+    public int hashCode() {
+        return field.hashCode();
+    }
+
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
     
-    protected AccessibleObject accesibleObject() {
+    protected AccessibleObject accessibleObject() {
         return field;
     }
 }
diff --git a/src/org/jruby/javasupport/JavaMethod.java b/src/org/jruby/javasupport/JavaMethod.java
index 0df68b68df..bc2aa59fa7 100644
--- a/src/org/jruby/javasupport/JavaMethod.java
+++ b/src/org/jruby/javasupport/JavaMethod.java
@@ -1,260 +1,270 @@
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
 
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.javasupport.proxy.InternalJavaProxy;
 import org.jruby.javasupport.proxy.JavaProxyClass;
+import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaMethod extends JavaCallable {
     private final Method method;
     private final Class[] parameterTypes;
     private final JavaUtil.JavaConverter returnConverter;
 
     public static RubyClass createJavaMethodClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = 
             javaModule.defineClassUnder("JavaMethod", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(JavaMethod.class);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
         
         result.defineFastMethod("name", callbackFactory.getFastMethod("name"));
         result.defineFastMethod("arity", callbackFactory.getFastMethod("arity"));
         result.defineFastMethod("public?", callbackFactory.getFastMethod("public_p"));
         result.defineFastMethod("final?", callbackFactory.getFastMethod("final_p"));
         result.defineFastMethod("static?", callbackFactory.getFastMethod("static_p"));
         result.defineFastMethod("invoke", callbackFactory.getFastOptMethod("invoke"));
         result.defineFastMethod("invoke_static", callbackFactory.getFastOptMethod("invoke_static"));
         result.defineFastMethod("argument_types", callbackFactory.getFastMethod("argument_types"));
         result.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         result.defineFastMethod("return_type", callbackFactory.getFastMethod("return_type"));
 
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
-            accesibleObject().setAccessible(true);
+            accessibleObject().setAccessible(true);
         }
         
         returnConverter = JavaUtil.getJavaConverter(method.getReturnType());
     }
 
     public static JavaMethod create(Ruby runtime, Method method) {
         return new JavaMethod(runtime, method);
     }
 
     public static JavaMethod create(Ruby runtime, Class javaClass, String methodName, Class[] argumentTypes) {
         try {
             Method method = javaClass.getMethod(methodName, argumentTypes);
             return create(runtime, method);
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
     public static JavaMethod createDeclared(Ruby runtime, Class javaClass, String methodName, Class[] argumentTypes) {
         try {
             Method method = javaClass.getDeclaredMethod(methodName, argumentTypes);
             return create(runtime, method);
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
+    public boolean equals(Object other) {
+        return other instanceof JavaMethod &&
+            this.method == ((JavaMethod)other).method;
+    }
+    
+    public int hashCode() {
+        return method.hashCode();
+    }
+
     public RubyString name() {
         return getRuntime().newString(method.getName());
     }
 
     protected int getArity() {
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
         Class klass = method.getReturnType();
         
         if (klass.equals(void.class)) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), klass);
     }
 
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
         Class[] parameterTypes = parameterTypes();
         for (int i = 0; i < arguments.length; i++) {
             arguments[i] = JavaUtil.convertArgument(args[i+from], parameterTypes[i]);
         }
     }
 
     protected Class[] parameterTypes() {
         return parameterTypes;
     }
 
     protected String nameOnInspection() {
         return "#<" + getType().toString() + "/" + method.getName() + "(";
     }
 
     public RubyBoolean static_p() {
         return getRuntime().newBoolean(isStatic());
     }
 
     private boolean isStatic() {
         return Modifier.isStatic(method.getModifiers());
     }
 
     protected int getModifiers() {
         return method.getModifiers();
     }
 
-    protected AccessibleObject accesibleObject() {
+    protected AccessibleObject accessibleObject() {
         return method;
     }
 }
diff --git a/src/org/jruby/javasupport/JavaObject.java b/src/org/jruby/javasupport/JavaObject.java
index d9af8524dc..43370c5334 100644
--- a/src/org/jruby/javasupport/JavaObject.java
+++ b/src/org/jruby/javasupport/JavaObject.java
@@ -1,193 +1,204 @@
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
+import org.jruby.javasupport.proxy.JavaProxyConstructor;
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
                 return JavaClass.get(runtime, (Class)value);
             } else if (value.getClass().isArray()) {
                 return new JavaArray(runtime, value);
             }
         }
         return new JavaObject(runtime, value);
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
 
+    public boolean equals(Object other) {
+        return other instanceof JavaObject &&
+            this.value == ((JavaObject)other).value;
+    }
+    
+    public int hashCode() {
+        if (value != null) return value.hashCode();
+        return 0;
+    }
+
 	public RubyFixnum hash() {
-        return getRuntime().newFixnum(value == null ? 0 : value.hashCode());
+        return getRuntime().newFixnum(hashCode());
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
diff --git a/src/org/jruby/javasupport/proxy/JavaProxyClass.java b/src/org/jruby/javasupport/proxy/JavaProxyClass.java
index 9419191589..0b7e365a14 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyClass.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyClass.java
@@ -1,705 +1,725 @@
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
-    private ArrayList<JavaProxyMethod> methods = new ArrayList<JavaProxyMethod>();
-    private HashMap<String, List<JavaProxyMethod>> methodMap = new HashMap<String, List<JavaProxyMethod>>();
+    private final ArrayList<JavaProxyMethod> methods = new ArrayList<JavaProxyMethod>();
+    private final HashMap<String, List<JavaProxyMethod>> methodMap = new HashMap<String, List<JavaProxyMethod>>();
+    private final RubyArray constructors;
 
     /* package scope */
     JavaProxyClass(Class proxyClass) {
         super(getThreadLocalRuntime(), 
                 (RubyClass) getThreadLocalRuntime().fastGetModule("Java").fastGetClass("JavaProxyClass"));
         
         this.proxyClass = proxyClass;
+        this.constructors = buildRubyArray(getConstructors());
+    }
+
+    public boolean equals(Object other) {
+        return other instanceof JavaProxyClass &&
+            this.proxyClass == ((JavaProxyClass)other).proxyClass;
+    }
+    
+    public int hashCode() {
+        return proxyClass.hashCode();
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
 
+        public boolean equals(Object other) {
+            return other instanceof ProxyMethodImpl &&
+                this.m == ((ProxyMethodImpl)other).m;
+        }
+        
+        public int hashCode() {
+            return m.hashCode();
+        }
+
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
 
         public Class[] getExceptionTypes() {
             return m.getExceptionTypes();
         }
 
         public Class[] getParameterTypes() {
             return parameterTypes;
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
-        return buildRubyArray(getConstructors());
+        return this.constructors;
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
index 4d4224ee9c..4a7118f0da 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java
@@ -1,256 +1,265 @@
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
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaProxyConstructor extends JavaProxyReflectionObject {
 
     private final Constructor proxyConstructor;
     private final Class[] parameterTypes;
 
     private final JavaProxyClass declaringProxyClass;
 
     JavaProxyConstructor(Ruby runtime, JavaProxyClass pClass,
             Constructor constructor) {
         super(runtime, runtime.getJavaSupport().getJavaModule().fastGetClass(
                 "JavaProxyConstructor"));
         this.declaringProxyClass = pClass;
         this.proxyConstructor = constructor;
         this.parameterTypes = proxyConstructor.getParameterTypes();
     }
 
     public Class[] getParameterTypes() {
         Class[] result = new Class[parameterTypes.length - 1];
         System.arraycopy(parameterTypes, 0, result, 0, result.length);
         return result;
     }
 
     public JavaProxyClass getDeclaringClass() {
         return declaringProxyClass;
     }
 
     public Object newInstance(Object[] args, JavaProxyInvocationHandler handler)
             throws IllegalArgumentException, InstantiationException,
             IllegalAccessException, InvocationTargetException {
         if (args.length + 1 != parameterTypes.length) {
             throw new IllegalArgumentException("wrong number of parameters");
         }
 
         Object[] realArgs = new Object[args.length + 1];
         System.arraycopy(args, 0, realArgs, 0, args.length);
         realArgs[args.length] = handler;
 
         return proxyConstructor.newInstance(realArgs);
     }
 
     public static RubyClass createJavaProxyConstructorClass(Ruby runtime,
             RubyModule javaProxyModule) {
         RubyClass result = javaProxyModule.defineClassUnder(
                                                             "JavaProxyConstructor", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
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
+    
+    public boolean equals(Object other) {
+        return other instanceof JavaProxyConstructor &&
+            this.proxyConstructor == ((JavaProxyConstructor)other).proxyConstructor;
+    }
+    
+    public int hashCode() {
+        return proxyConstructor.hashCode();
+    }
 
     protected String nameOnInspection() {
         return getDeclaringClass().nameOnInspection();
     }
 
     public IRubyObject inspect() {
         StringBuffer result = new StringBuffer();
         result.append(nameOnInspection());
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
 
     public RubyArray argument_types() {
         return buildRubyArray(getParameterTypes());
     }
     
     public RubyObject new_instance2(IRubyObject[] args, Block unusedBlock) {
         Arity.checkArgumentCount(getRuntime(), args, 2, 2);
 
         final IRubyObject self = args[0];
         final Ruby runtime = self.getRuntime();
         final RubyModule javaUtilities = runtime.getJavaSupport().getJavaUtilitiesModule();
         RubyArray constructor_args = (RubyArray) args[1];
         Class[] parameterTypes = getParameterTypes();
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
         Class[] parameterTypes = getParameterTypes();
 
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
