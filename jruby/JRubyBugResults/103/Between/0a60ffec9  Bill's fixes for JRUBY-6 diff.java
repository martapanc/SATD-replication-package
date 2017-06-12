diff --git a/src/builtin/javasupport/proxy/concrete.rb b/src/builtin/javasupport/proxy/concrete.rb
index 867cdd80a3..d616fa71a9 100644
--- a/src/builtin/javasupport/proxy/concrete.rb
+++ b/src/builtin/javasupport/proxy/concrete.rb
@@ -1,23 +1,24 @@
 class ConcreteJavaProxy < JavaProxy
   class << self
     alias_method :new_proxy, :new
 
     def new(*args,&block)
       proxy = new_proxy *args,&block
       proxy.__jcreate!(*args) unless proxy.java_object
       proxy
     end
   end
   
   def __jcreate!(*args)
-    raise NameError, "not instantiatable" if self.java_class.constructors.length == 0
-    constructors = self.java_class.constructors.select {|c| c.arity == args.length }
+    constructors = self.java_class.constructors
+    raise NameError, "not instantiatable" if constructors.length == 0
+    constructors = constructors.select {|c| c.arity == args.length }
     raise NameError, "wrong # of arguments for constructor" if constructors.empty?
     args.collect! { |v| Java.ruby_to_java(v) }
     self.java_object = JavaUtilities.matching_method(constructors, args).new_instance(*args)
   end
   
   def initialize(*args, &block)
     __jcreate!(*args)
   end
 end
\ No newline at end of file
diff --git a/src/builtin/javasupport/proxy/interface.rb b/src/builtin/javasupport/proxy/interface.rb
index 7981c91eb8..ccb37e72a1 100644
--- a/src/builtin/javasupport/proxy/interface.rb
+++ b/src/builtin/javasupport/proxy/interface.rb
@@ -1,92 +1,93 @@
 class JavaInterfaceExtender
   def initialize(java_class_name, &block)
     @java_class = Java::JavaClass.for_name(java_class_name)
     @block = block
   end
   
   def extend_proxy(proxy_class)
     proxy_class.class_eval &@block if @java_class.assignable_from? proxy_class.java_class
   end
 end
 class InterfaceJavaProxy < JavaProxy
   class << self  
     alias_method :new_proxy, :new
 
     def new(*args, &block)
-      proxy = new_proxy(*args, &block)
+      proxy = allocate
       proxy.java_object = Java.new_proxy_instance(proxy.class.java_class) { |proxy2, method, *args|
         args.collect! { |arg| Java.java_to_ruby(arg) }
         Java.ruby_to_java(proxy.send(method.name, *args))
       }
+      proxy.send(:initialize,*args,&block)
       proxy
     end
     
     def +(other)
       MultipleInterfaceJavaProxy.new(lambda{|*args| new_proxy(*args)}, self, other)
     end
     
     alias_method :old_eqq, :===
     
     def ===(other)
       if other.respond_to?(:java_object)
         other.java_object.java_class.interfaces.include?(self.java_class)
       else
         old_eqq(other)
       end
     end
   end
     
   def self.impl(*meths, &block)
     block = lambda {|*args| send(:method_missing, *args) } unless block
 
     Class.new(self) do
       define_method(:method_missing) do |name, *args|
         return block.call(name, *args) if meths.empty? || meths.include?(name)
         super
       end
     end.new
   end
 end
 
 class MultipleInterfaceJavaProxy
   attr_reader :interfaces
     
   def initialize(creator, *args)
     @creator = creator
     @interfaces = args.map{ |v| into_arr(v) }.flatten
   end
 
   def <<(other)
     @interfaces += into_arr(other)
   end
 
   def +(other)
     MultipleInterfaceJavaProxy.new @creator, *(@interfaces + into_arr(other))
   end
     
   def new(*args, &block)
     @interfaces.freeze unless @interfaces.frozen?
     proxy = @creator.call(*args)
     proxy.java_object = Java.new_proxy_instance(*@interfaces) { |proxy2, method, *args|
       args.collect! { |arg| Java.java_to_ruby(arg) }
       Java.ruby_to_java(proxy.__jsend!(method.name, *args))
     }
     proxy
   end
 
   def ===(other)
     if other.respond_to?(:java_object)
       (@interfaces - other.java_object.java_class.interfaces) == []
     else
       super
     end
   end
 
   private
   def into_arr(other)
     case other
       when MultipleInterfaceJavaProxy: other.interfaces
       else [other.java_class]
     end
   end
 end
\ No newline at end of file
diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index 4eb6902691..4cb0ab9f87 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,1405 +1,1410 @@
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
 import org.jruby.RubyString;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
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
         static final int WEAKLY_RESERVED = 3; // we'll be peeved, but not devastated, if you override
         static final int ALIAS = 4;
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
         RESERVED_NAMES.put("class", new AssignedName("class", AssignedName.RESERVED));
         RESERVED_NAMES.put("__id__", new AssignedName("__id__", AssignedName.RESERVED));
         RESERVED_NAMES.put("object_id", new AssignedName("object_id", AssignedName.RESERVED));
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
         }
     }
 
     private class StaticFieldGetter extends FieldCallback {
         StaticFieldGetter(){}
         StaticFieldGetter(String name, Field field) {
             super(name,STATIC_FIELD,field);
         }
         void install(RubyClass proxy) {
             proxy.getSingletonClass().defineFastMethod(this.name,this);
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
             proxy.getSingletonClass().defineFastMethod(this.name,this);
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
             proxy.defineFastMethod(this.name,this);
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
             proxy.defineFastMethod(this.name,this);
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
             haveLocalMethod = (haveLocalMethod || javaClass == method.getDeclaringClass());
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
             throw proxy.getRuntime().newNameError("no " + this.name + " with arguments matching " + argTypes, null);
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
                 singleton.defineFastMethod(this.name,this);
                 if (aliases != null) {
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
                 proxy.defineFastMethod(this.name,this);
                 if (aliases != null) {
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
             IRubyObject[] convertedArgs = new IRubyObject[len+1];
             convertedArgs[0] = self.getInstanceVariable("@java_object");
             for (int i = len; --i >= 0; ) {
                 convertedArgs[i+1] = Java.ruby_to_java(self,args[i],Block.NULL_BLOCK);
             }
             if (javaMethods == null) {
                 return Java.java_to_ruby(self,javaMethod.invoke(convertedArgs),Block.NULL_BLOCK); 
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
         void install(RubyClass proxy) {
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
     
     
     private final RubyModule JAVA_UTILITIES = getRuntime().getModule("JavaUtilities");
     
     private Map staticAssignedNames;
     private Map instanceAssignedNames;
     private Map staticCallbacks;
     private Map instanceCallbacks;
     private List constantFields;
+    // caching constructors, as they're accessed for each new instance
+    private RubyArray constructors;
     
     protected Map getStaticAssignedNames() {
         return staticAssignedNames;
     }
     protected Map getInstanceAssignedNames() {
         return instanceAssignedNames;
     }
     
     private JavaClass(Ruby runtime, Class javaClass) {
         super(runtime, (RubyClass) runtime.getModule("Java").getClass("JavaClass"), javaClass);
         if (javaClass.isInterface()) {
             initializeInterface(javaClass);
         } else if (!(javaClass.isArray() || javaClass.isPrimitive())) {
             // TODO: public only?
             initializeClass(javaClass);
         }
     }
     
     private void initializeInterface(Class javaClass) {
         Class superclass = javaClass.getSuperclass();
         Map staticNames;
         if (superclass == null) {
             staticNames = new HashMap();
         } else {
             JavaClass superJavaClass = get(getRuntime(),superclass);
             staticNames = new HashMap(superJavaClass.getStaticAssignedNames());
         }
         staticNames.putAll(STATIC_RESERVED_NAMES);
         List constantFields = new ArrayList(); 
         Field[] fields = javaClass.getFields();
         for (int i = fields.length; --i >= 0; ) {
             Field field = fields[i];
             // treating constants specially until interface modules
             // are implemented. we'll define any as-yet undefined
             // constant, regardless of declaring class. this will
             // slow us down a bit in setupProxy.
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
             // treating constants specially until interface modules
             // are implemented. we'll define any as-yet undefined
             // constant, regardless of declaring class. this will
             // slow us down a bit in setupProxy.
             if (ConstantField.isConstant(field)) {
                 constantFields.add(new ConstantField(field));
                 continue;
             }
             // for everything else, must be declared in this class
             if (!javaClass.equals(field.getDeclaringClass()))
                 continue;
             String name = field.getName();
             if (Modifier.isStatic(field.getModifiers())) {
                 AssignedName assignedName = (AssignedName)staticNames.get(name);
                 if (assignedName != null && assignedName.type < AssignedName.FIELD)
                     continue;
                 staticNames.put(name,new AssignedName(name,AssignedName.FIELD));
                 staticCallbacks.put(name,new StaticFieldGetter(name,field));
                 if (!Modifier.isFinal(field.getModifiers())) {
                     String setName = name + '=';
                     staticCallbacks.put(setName,new StaticFieldSetter(setName,field));
                 }
             } else {
                 AssignedName assignedName = (AssignedName)instanceNames.get(name);
                 if (assignedName != null && assignedName.type < AssignedName.FIELD)
                     continue;
                 instanceNames.put(name,new AssignedName(name,AssignedName.FIELD));
                 instanceCallbacks.put(name,new InstanceFieldGetter(name,field));
                 if (!Modifier.isFinal(field.getModifiers())) {
                     String setName = name + '=';
                     instanceCallbacks.put(setName,new InstanceFieldSetter(setName,field));
                 }
             }
         }
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
                 if (simpleName.length() == 0)
                     continue;
                 if (proxy.getConstantAt(simpleName) == null) {
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
     
     private static void addUnassignedAlias(String name, Map assignedNames, MethodCallback callback ) {
         if (name != null) {
             AssignedName assignedName = (AssignedName)assignedNames.get(name);
             if (assignedName == null || assignedName.type >= AssignedName.ALIAS) {
                 callback.addAlias(name);
                 if (assignedName == null || assignedName.type != AssignedName.ALIAS) {
                     assignedNames.put(name,new AssignedName(name,AssignedName.ALIAS));
                 }
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
 
     private static final Pattern CAMEL_CASE_SPLITTER = Pattern.compile("([a-z])([A-Z])");    
     public static String getRubyCasedName(String javaCasedName) {
         Matcher m = CAMEL_CASE_SPLITTER.matcher(javaCasedName);
         String rubyCasedName = m.replaceAll("$1_$2").toLowerCase();
         if (rubyCasedName.equals(javaCasedName)) {
             return null;
         }
         return rubyCasedName;
     }
     
     
     public void setupInterfaceProxy(RubyClass proxy) {
         for (Iterator iter = constantFields.iterator(); iter.hasNext(); ){
             ((ConstantField)iter.next()).install(proxy);
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
     
     public static synchronized JavaClass for_name(IRubyObject recv, IRubyObject name) {
         String className = name.asSymbol();
         Class klass = recv.getRuntime().getJavaSupport().loadJavaClass(className);
         return JavaClass.get(recv.getRuntime(), klass);
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
         final RubyModule javaUtilities = getRuntime().getModule("JavaUtilities");
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
                 String methodSymbol = args[0].asSymbol();
                 RubyMethod method = (org.jruby.RubyMethod)self.getMetaClass().newMethod(self, methodSymbol, true);
                 int v = RubyNumeric.fix2int(method.arity());
                 // TODO: why twice?
                 String name = args[0].asSymbol();
 
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
 
 	Class javaClass() {
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
-        return buildConstructors(javaClass().getConstructors());
+        if (constructors == null) {
+            constructors = buildConstructors(javaClass().getConstructors());
+        }
+        return constructors;
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
diff --git a/src/org/jruby/javasupport/JavaMethod.java b/src/org/jruby/javasupport/JavaMethod.java
index 710de5cdc3..efdefc6535 100644
--- a/src/org/jruby/javasupport/JavaMethod.java
+++ b/src/org/jruby/javasupport/JavaMethod.java
@@ -1,265 +1,265 @@
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
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.proxy.InternalJavaProxy;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaMethod extends JavaCallable {
     private final Method method;
     private final Class[] parameterTypes;
 
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
         super(runtime, (RubyClass) runtime.getModule("Java").getClass("JavaMethod"));
         this.method = method;
         this.parameterTypes = method.getParameterTypes();
 
         // Special classes like Collections.EMPTY_LIST are inner classes that are private but 
         // implement public interfaces.  Their methods are all public methods for the public 
         // interface.  Let these public methods execute via setAccessible(true). 
         if (Modifier.isPublic(method.getModifiers()) &&
             Modifier.isPublic(method.getClass().getModifiers()) &&
             !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
             accesibleObject().setAccessible(true);
         }
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
-        if (javaInvokee instanceof InternalJavaProxy) {
+        if (javaInvokee instanceof InternalJavaProxy &&
+                // don't bother to check if final method, it won't be there
+                !Modifier.isFinal(method.getModifiers())) {
             JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee)
                     .___getProxyClass();
             JavaProxyMethod jpm;
             try {
                 jpm = jpc.getMethod(method.getName(), parameterTypes);
             } catch (NoSuchMethodException e) {
-                RaiseException err = getRuntime().newTypeError(
-                        "mismatch with proxy/super method?");
-                err.initCause(e);
-                throw err;
+                // ok, this just means there's no generated proxy method (which
+                // there wouldn't be for final methods, possibly other cases?).
+                // Try to invoke anyway.
+                return invokeWithExceptionHandling(method, javaInvokee, arguments);
             }
             if (jpm.hasSuperImplementation()) {
                 return invokeWithExceptionHandling(jpm.getSuperMethod(),
                         javaInvokee, arguments);
             }
-
         }
-        
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
             return JavaObject.wrap(getRuntime(), result);
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
 
     protected AccessibleObject accesibleObject() {
         return method;
     }
 }
diff --git a/src/org/jruby/javasupport/proxy/JavaProxyClass.java b/src/org/jruby/javasupport/proxy/JavaProxyClass.java
index ad0d672fc9..ec38994255 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyClass.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyClass.java
@@ -1,513 +1,523 @@
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
 
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.security.AccessController;
 import java.security.PrivilegedActionException;
 import java.security.PrivilegedExceptionAction;
 import java.util.ArrayList;
 import java.util.Arrays;
+import java.util.HashMap;
 import java.util.List;
+import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.exceptions.RaiseException;
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
     static ThreadLocal runtimeTLS = new ThreadLocal();
     private final Class proxyClass;
     private ArrayList methods = new ArrayList();
+    private HashMap methodMap = new HashMap();
 
     /* package scope */
     JavaProxyClass(Class proxyClass) {
         super(getThreadLocalRuntime(), 
                 (RubyClass) getThreadLocalRuntime().getModule("Java").getClass("JavaProxyClass"));
         
         this.proxyClass = proxyClass;
     }
 
     public Object getValue() {
         return this;
     }
 
     private static Ruby getThreadLocalRuntime() {
         return (Ruby) runtimeTLS.get();
     }
 
     public static JavaProxyClass getProxyClass(Ruby runtime, Class superClass,
             Class[] interfaces) throws InvocationTargetException {
         Object save = runtimeTLS.get();
         runtimeTLS.set(runtime);
         try {
             ClassLoader loader = runtime.getJavaSupport().getJavaClassLoader();
 
             return JavaProxyClassFactory.newProxyClass(loader, null, superClass, interfaces);
         } finally {
             runtimeTLS.set(save);
         }
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
         return (JavaProxyMethod[]) methods.toArray(new JavaProxyMethod[methods.size()]);
     }
 
     public JavaProxyMethod getMethod(String name, Class[] parameterTypes)
             throws NoSuchMethodException {
-        JavaProxyMethod[] all = getMethods();
-        for (int i = 0; i < all.length; i++) {
-            ProxyMethodImpl jpm = (ProxyMethodImpl) all[i];
-            
-            if (jpm.matches(name, parameterTypes)) return jpm;
+        List methods = (List)methodMap.get(name);
+        if (methods != null) {
+            for (int i = methods.size(); --i >= 0; ) {
+                ProxyMethodImpl jpm = (ProxyMethodImpl) methods.get(i);
+                if (jpm.matches(name, parameterTypes)) return jpm;
+            }
         }
         throw new NoSuchMethodException();
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
             super(runtime, runtime.getModule("Java")
                     .getClass("JavaProxyMethod"));
             this.m = m;
             this.parameterTypes = m.getParameterTypes();
             this.sm = sm;
             this.clazz = clazz;
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
+            List methodsWithName = (List)methodMap.get(name);
+            if (methodsWithName == null) {
+                methodsWithName = new ArrayList(2);
+                methodMap.put(name,methodsWithName);
+            }
+            methodsWithName.add(jpm);
             
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
         List al = new ArrayList();
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
                     type = (Class) AccessController
                             .doPrivileged(new PrivilegedExceptionAction() {
                                 public Object run()
                                         throws ClassNotFoundException {
                                     return Class.forName(
                                             name.replace('/', '.'), false,
                                             loader);
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
         return buildRubyArray(getConstructors());
     }
 
     public static void createJavaProxyModule(Ruby runtime) {
         // TODO Auto-generated method stub
 
         RubyModule javaProxyModule = runtime.getModule("Java");
         JavaProxyClass.createJavaProxyClassClass(runtime, javaProxyModule);
         ProxyMethodImpl.createJavaProxyMethodClass(runtime, javaProxyModule);
         JavaProxyConstructor.createJavaProxyConstructorClass(runtime, javaProxyModule);
     }
 
     public String nameOnInspection() {
         return "[Proxy:" + getSuperclass().getName() + "]";
     }
 }
diff --git a/test/org/jruby/test/FinalMethodBaseTest.java b/test/org/jruby/test/FinalMethodBaseTest.java
new file mode 100644
index 0000000000..16790e1c70
--- /dev/null
+++ b/test/org/jruby/test/FinalMethodBaseTest.java
@@ -0,0 +1,7 @@
+package org.jruby.test;
+
+public class FinalMethodBaseTest {
+    public final String foo() {
+        return "In foo";
+    }
+}
diff --git a/test/test_higher_javasupport.rb b/test/test_higher_javasupport.rb
index 98c7cf3d06..52c1c6c28b 100644
--- a/test/test_higher_javasupport.rb
+++ b/test/test_higher_javasupport.rb
@@ -1,523 +1,533 @@
 require 'java'
 require 'test/unit'
 
 TopLevelConstantExistsProc = Proc.new do
   include_class 'java.lang.String'
 end
 
 class TestHigherJavasupport < Test::Unit::TestCase
   TestHelper = org.jruby.test.TestHelper
   JArray = ArrayList = java.util.ArrayList
+  FinalMethodBaseTest = org.jruby.test.FinalMethodBaseTest
 
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
     assert Class.new(java.lang.Comparable)
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
     
   def test_attempt_to_include_string_at_top_level_gives_constant_already_exists
     assert_raises(ConstantAlreadyExistsError) do
       TopLevelConstantExistsProc.call
     end
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
 
   class MyBadActionListener < java.awt.event.ActionListener
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
 
   unless (java.lang.System.getProperty("java.specification.version") == "1.4")
     class NSCT < javax.xml.namespace.NamespaceContext
       # JRUBY-66: No super here...make sure we still work.
       def initialize(arg)
       end
       def getNamespaceURI(prefix)
         'ape:sex'
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
 
   class TestInitBlock < Java::java.lang.Runnable
     def initialize(&block)
       raise if !block
       @bar = block.call
     end
     def bar; @bar; end
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
+  
+  # test for JRUBY-664
+  class FinalMethodChildClass < FinalMethodBaseTest
+  end
+
+  def test_calling_base_class_final_method
+    assert_equal("In foo", FinalMethodBaseTest.new.foo)
+    assert_equal("In foo", FinalMethodChildClass.new.foo)
+  end
 
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
 end
