diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index 4356201ffa..907b451720 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,1431 +1,1419 @@
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
 
 import java.io.ByteArrayOutputStream;
 import java.io.InputStream;
 import java.io.IOException;
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
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.ByteList;
 import org.jruby.util.IdUtil;
 import org.jruby.util.collections.IntHashMap;
 
 @JRubyClass(name="Java::JavaClass", parent="Java::JavaObject")
 public class JavaClass extends JavaObject {
 
     // some null objects to simplify later code
     private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[] {};
     private static final Method[] EMPTY_METHOD_ARRAY = new Method[] {};
     private static final Field[] EMPTY_FIELD_ARRAY = new Field[] {};
 
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
-        protected IntHashMap matchingMethods;
+        protected volatile boolean initialized;
         MethodCallback(){}
         MethodCallback(String name, int type) {
             super(name,type);
         }
 
         // called only by initializing thread; no synchronization required
         void addMethod(Method method, Class<?> javaClass) {
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
-            if (methods != null) {
-                if (methods.size() == 1) {
-                    javaMethod = JavaMethod.create(runtime, methods.get(0));
-                } else {
-                    javaMethods = new IntHashMap();
-                    matchingMethods = new IntHashMap();
-                    for (Method method: methods) {
-                        // TODO: deal with varargs
-                        int arity = method.getParameterTypes().length;
-                        RubyArray methodsForArity = (RubyArray)javaMethods.get(arity);
-                        if (methodsForArity == null) {
-                            methodsForArity = RubyArray.newArrayLight(runtime);
-                            javaMethods.put(arity,methodsForArity);
+            if (!initialized) { // read-volatile
+                if (methods != null) {
+                    if (methods.size() == 1) {
+                        javaMethod = JavaMethod.create(runtime, methods.get(0));
+                    } else {
+                        javaMethods = new IntHashMap();
+                        for (Method method: methods) {
+                            // TODO: deal with varargs
+                            int arity = method.getParameterTypes().length;
+                            RubyArray methodsForArity = (RubyArray)javaMethods.get(arity);
+                            if (methodsForArity == null) {
+                                methodsForArity = RubyArray.newArrayLight(runtime);
+                                javaMethods.put(arity,methodsForArity);
+                            }
+                            methodsForArity.append(JavaMethod.create(runtime,method));
                         }
-                        methodsForArity.append(JavaMethod.create(runtime,method));
                     }
+                    methods = null;
                 }
-                methods = null;
+                initialized = true; // write-volatile
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
                     singleton.defineAliases(aliases, this.name);
                     aliases = null;
                 }
             }
         }
 
-        // synchronized due to modification of matchingMethods
-        synchronized public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
-            createJavaMethods(self.getRuntime());
+        public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
+            if (!initialized) { // read-volatile
+                createJavaMethods(self.getRuntime());
+            }
             // TODO: ok to convert args in place, rather than new array?
             int len = args.length;
             IRubyObject[] convertedArgs = new IRubyObject[len];
             for (int i = len; --i >= 0; ) {
                 convertedArgs[i] = Java.ruby_to_java(self,args[i],Block.NULL_BLOCK);
             }
-            if (javaMethods == null) {
-                return Java.java_to_ruby(self,javaMethod.invoke_static(convertedArgs),Block.NULL_BLOCK); 
-            } else {
-                int argsTypeHash = 0;
-                for (int i = len; --i >= 0; ) {
-                    argsTypeHash += 3*args[i].getMetaClass().id;
+            JavaMethod method;
+            if ((method = javaMethod) == null) {
+                // TODO: varargs?
+                RubyArray methods = (RubyArray)javaMethods.get(len);
+                if (methods == null) {
+                    raiseNoMatchingMethodError(self,convertedArgs,0);
                 }
-                IRubyObject match = (IRubyObject)matchingMethods.get(argsTypeHash);
-                if (match == null) {
-                    // TODO: varargs?
-                    RubyArray methods = (RubyArray)javaMethods.get(len);
-                    if (methods == null) {
-                        raiseNoMatchingMethodError(self,convertedArgs,0);
-                    }
-                    match = Java.matching_method_internal(JAVA_UTILITIES, methods, convertedArgs, 0, len);
-                }
-                return Java.java_to_ruby(self, ((JavaMethod)match).invoke_static(convertedArgs), Block.NULL_BLOCK);
+                method = (JavaMethod)Java.matching_method_internal(JAVA_UTILITIES, methods, convertedArgs, 0, len);
             }
+            return Java.java_to_ruby(self, method.invoke_static(convertedArgs), Block.NULL_BLOCK);
         }
+
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
                     proxy.defineAliases(aliases, this.name);
                     aliases = null;
                 }
             }
         }
 
-        // synchronized due to modification of matchingMethods
-        synchronized public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
-            createJavaMethods(self.getRuntime());
+        public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
+            if (!initialized) { // read-volatile
+                createJavaMethods(self.getRuntime());
+            }
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
-
-            if (javaMethods == null) {
-                return Java.java_to_ruby(self,javaMethod.invoke(convertedArgs),Block.NULL_BLOCK);
-            } else {
-                int argsTypeHash = 0;
-                for (i = len; --i >= 0; ) {
-                    argsTypeHash += 3*args[i].getMetaClass().id;
+            JavaMethod method;
+            if ((method = javaMethod) == null) {
+                // TODO: varargs?
+                RubyArray methods = (RubyArray)javaMethods.get(len);
+                if (methods == null) {
+                    raiseNoMatchingMethodError(self,convertedArgs,1);
                 }
-                IRubyObject match = (IRubyObject)matchingMethods.get(argsTypeHash);
-                if (match == null) {
-                    // TODO: varargs?
-                    RubyArray methods = (RubyArray)javaMethods.get(len);
-                    if (methods == null) {
-                        raiseNoMatchingMethodError(self,convertedArgs,1);
-                    }
-                    match = Java.matching_method_internal(JAVA_UTILITIES, methods, convertedArgs, 1, len);
-                    matchingMethods.put(argsTypeHash, match);
-                }
-                return Java.java_to_ruby(self,((JavaMethod)match).invoke(convertedArgs),Block.NULL_BLOCK);
+                method = (JavaMethod)Java.matching_method_internal(JAVA_UTILITIES, methods, convertedArgs, 1, len);
             }
+            return Java.java_to_ruby(self, method.invoke(convertedArgs), Block.NULL_BLOCK);
         }
+
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
     private Map<String, NamedCallback> staticCallbacks;
     private Map<String, NamedCallback> instanceCallbacks;
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
     
     private JavaClass(Ruby runtime, Class<?> javaClass) {
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
     
     private void initializeInterface(Class<?> javaClass) {
         Map<String, AssignedName> staticNames  = new HashMap<String, AssignedName>(STATIC_RESERVED_NAMES);
         List<ConstantField> constantFields = new ArrayList<ConstantField>(); 
         Field[] fields = EMPTY_FIELD_ARRAY;
         try {
             fields = javaClass.getDeclaredFields();
         } catch (SecurityException e) {
             try {
                 fields = javaClass.getFields();
             } catch (SecurityException e2) {
             }
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
 
     private void initializeClass(Class<?> javaClass) {
         Class<?> superclass = javaClass.getSuperclass();
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
         Map<String, NamedCallback> staticCallbacks = new HashMap<String, NamedCallback>();
         Map<String, NamedCallback> instanceCallbacks = new HashMap<String, NamedCallback>();
         List<ConstantField> constantFields = new ArrayList<ConstantField>(); 
         Field[] fields = EMPTY_FIELD_ARRAY;
         try {
             fields = javaClass.getFields();
         } catch (SecurityException e) {
         }
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
                 AssignedName assignedName = staticNames.get(name);
                 if (assignedName != null && assignedName.type < AssignedName.FIELD)
                     continue;
                 staticNames.put(name,new AssignedName(name,AssignedName.FIELD));
                 staticCallbacks.put(name,new StaticFieldGetter(name,field));
                 if (!Modifier.isFinal(modifiers)) {
                     String setName = name + '=';
                     staticCallbacks.put(setName,new StaticFieldSetter(setName,field));
                 }
             } else {
                 AssignedName assignedName = instanceNames.get(name);
                 if (assignedName != null && assignedName.type < AssignedName.FIELD)
                     continue;
                 instanceNames.put(name, new AssignedName(name,AssignedName.FIELD));
                 instanceCallbacks.put(name, new InstanceFieldGetter(name,field));
                 if (!Modifier.isFinal(modifiers)) {
                     String setName = name + '=';
                     instanceCallbacks.put(setName, new InstanceFieldSetter(setName,field));
                 }
             }
         }
         // TODO: protected methods.  this is going to require a rework 
         // of some of the mechanism.  
         Method[] methods = EMPTY_METHOD_ARRAY;
         for (Class c = javaClass; c != null; c = c.getSuperclass()) {
             try {
                 methods = javaClass.getMethods();
                 break;
             } catch (SecurityException e) {
             }
         }
         for (int i = methods.length; --i >= 0; ) {
             // we need to collect all methods, though we'll only
             // install the ones that are named in this class
             Method method = methods[i];
             String name = method.getName();
             if (Modifier.isStatic(method.getModifiers())) {
                 AssignedName assignedName = staticNames.get(name);
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
                 AssignedName assignedName = instanceNames.get(name);
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
         final Class<?> javaClass = javaClass();
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
         for (Iterator<NamedCallback> iter = staticCallbacks.values().iterator(); iter.hasNext(); ) {
             NamedCallback callback = iter.next();
             if (callback.type == NamedCallback.STATIC_METHOD && callback.hasLocalMethod()) {
                 assignAliases((MethodCallback)callback,staticAssignedNames);
             }
             callback.install(proxy);
         }
         for (Iterator<NamedCallback> iter = instanceCallbacks.values().iterator(); iter.hasNext(); ) {
             NamedCallback callback = iter.next();
             if (callback.type == NamedCallback.INSTANCE_METHOD && callback.hasLocalMethod()) {
                 assignAliases((MethodCallback)callback,instanceAssignedNames);
             }
             callback.install(proxy);
         }
         // setup constants for public inner classes
         Class<?>[] classes = EMPTY_CLASS_ARRAY;
         try {
             classes = javaClass.getClasses();
         } catch (SecurityException e) {
         }
         for (int i = classes.length; --i >= 0; ) {
             if (javaClass == classes[i].getDeclaringClass()) {
                 Class<?> clazz = classes[i];
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
             Class<?>[] argTypes = method.getParameterTypes();
             Class<?> resultType = method.getReturnType();
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
         Class<?> javaClass = javaClass();
         for (ConstantField field: constantFields) {
             field.install(module);
         }
         // setup constants for public inner classes
         Class<?>[] classes = EMPTY_CLASS_ARRAY;
         try {
             classes = javaClass.getClasses();
         } catch (SecurityException e) {
         }
         for (int i = classes.length; --i >= 0; ) {
             if (javaClass == classes[i].getDeclaringClass()) {
                 Class<?> clazz = classes[i];
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
     
     public static synchronized JavaClass forNameVerbose(Ruby runtime, String className) {
         Class<?> klass = runtime.getJavaSupport().loadJavaClassVerbose(className);
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
     public JavaMethod java_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asJavaString();
         Class<?>[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.create(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     @JRubyMethod(required = 1, rest = true)
     public JavaMethod declared_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asJavaString();
         Class<?>[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.createDeclared(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     @JRubyMethod(required = 1, rest = true)
     public JavaCallable declared_method_smart(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asJavaString();
         Class<?>[] argumentTypes = buildArgumentTypes(args);
  
         JavaCallable callable = getMatchingCallable(getRuntime(), javaClass(), methodName, argumentTypes);
 
         if (callable != null) return callable;
 
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
diff --git a/src/org/jruby/javasupport/JavaUtil.java b/src/org/jruby/javasupport/JavaUtil.java
index e63aa6ef4c..4c36e6f4d9 100644
--- a/src/org/jruby/javasupport/JavaUtil.java
+++ b/src/org/jruby/javasupport/JavaUtil.java
@@ -1,503 +1,504 @@
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
         public Object convert(ThreadContext context, IRubyObject rubyObject);
     }
     
     public static final RubyConverter RUBY_BOOLEAN_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return Boolean.valueOf(rubyObject.isTrue());
         }
     };
     
     public static final RubyConverter RUBY_BYTE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Byte((byte) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Byte((byte) 0);
         }
     };
     
     public static final RubyConverter RUBY_SHORT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Short((short) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Short((short) 0);
         }
     };
     
     public static final RubyConverter RUBY_INTEGER_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Integer((int) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Integer(0);
         }
     };
     
     public static final RubyConverter RUBY_LONG_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Long(((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Long(0);
         }
     };
     
     public static final RubyConverter RUBY_FLOAT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Float((float) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_F, "to_f")).getDoubleValue());
             }
             return new Float(0.0);
         }
     };
     
     public static final RubyConverter RUBY_DOUBLE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Double(((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_F, "to_f")).getDoubleValue());
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
             
             return convertArgument(rubyObject.getRuntime(), value, javaClass);
             
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
         }
         return convertJavaToRuby(runtime, object, object.getClass());
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
         JavaConverter converter = JAVA_CONVERTERS.get(object.getClass());
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             return Java.getInstance(runtime, object);
         }
         return converter.convert(runtime, object);
     }
 
-    public static Class primitiveToWrapper(Class type) {
-        if (type == Double.TYPE) {
-            return Double.class;
-        } else if (type == Float.TYPE) {
-            return Float.class;
-        } else if (type == Integer.TYPE) {
-            return Integer.class;
-        } else if (type == Long.TYPE) {
-            return Long.class;
-        } else if (type == Short.TYPE) {
-            return Short.class;
-        } else if (type == Byte.TYPE) {
-            return Byte.class;
-        } else if (type == Character.TYPE) {
-            return Character.class;
-        } else if (type == Void.TYPE) {
-            return Void.class;
-        } else if (type == Boolean.TYPE) {
-            return Boolean.class;
-        } else {
-            return type;
+    public static Class<?> primitiveToWrapper(Class<?> type) {
+        if (type.isPrimitive()) {
+            if (type == Integer.TYPE) {
+                return Integer.class;
+            } else if (type == Double.TYPE) {
+                return Double.class;
+            } else if (type == Boolean.TYPE) {
+                return Boolean.class;
+            } else if (type == Byte.TYPE) {
+                return Byte.class;
+            } else if (type == Character.TYPE) {
+                return Character.class;
+            } else if (type == Float.TYPE) {
+                return Float.class;
+            } else if (type == Long.TYPE) {
+                return Long.class;
+            } else if (type == Void.TYPE) {
+                return Void.class;
+            } else if (type == Short.TYPE) {
+                return Short.class;
+            }
         }
+        return type;
     }
 
-    public static Object convertArgument(Ruby runtime, Object argument, Class parameterType) {
+    public static Object convertArgument(Ruby runtime, Object argument, Class<?> parameterType) {
         if (argument == null && parameterType.isPrimitive()) {
             throw runtime.newTypeError("primitives do not accept null");
         }
         
         if (argument instanceof JavaObject) {
             argument = ((JavaObject) argument).getValue();
             if (argument == null) {
                 return null;
             }
         }
-        Class type = primitiveToWrapper(parameterType);
+        Class<?> type = primitiveToWrapper(parameterType);
         if (type == Void.class) {
             return null;
         }
         if (argument instanceof Number) {
             final Number number = (Number) argument;
             if (type == Long.class) {
                 return new Long(number.longValue());
             } else if (type == Integer.class) {
                 return new Integer(number.intValue());
-            } else if (type == Short.class) {
-                return new Short(number.shortValue());
             } else if (type == Byte.class) {
                 return new Byte(number.byteValue());
             } else if (type == Character.class) {
                 return new Character((char) number.intValue());
             } else if (type == Double.class) {
                 return new Double(number.doubleValue());
             } else if (type == Float.class) {
                 return new Float(number.floatValue());
+            } else if (type == Short.class) {
+                return new Short(number.shortValue());
             }
         }
         if (isDuckTypeConvertable(argument.getClass(), parameterType)) {
             RubyObject rubyObject = (RubyObject) argument;
             if (!rubyObject.respondsTo("java_object")) {
                 IRubyObject javaUtilities = runtime.getJavaSupport().getJavaUtilitiesModule();
                 IRubyObject javaInterfaceModule = Java.get_interface_module(javaUtilities, JavaClass.get(runtime, parameterType));
                 if (!((RubyModule)javaInterfaceModule).isInstance(rubyObject)) {
                     rubyObject.extend(new IRubyObject[] {javaInterfaceModule});
                 }
                 ThreadContext context = runtime.getCurrentContext();
                 if (rubyObject instanceof RubyProc) {
                     // Proc implementing an interface, pull in the catch-all code that lets the proc get invoked
                     // no matter what method is called on the interface
                     rubyObject.instance_eval(context, new IRubyObject[] {
                         runtime.newString("extend Proc::CatchAll")}, Block.NULL_BLOCK);
                 }
                 JavaObject jo = (JavaObject) rubyObject.instance_eval(context, new IRubyObject[] {
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
