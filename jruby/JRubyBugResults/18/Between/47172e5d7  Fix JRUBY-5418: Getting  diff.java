diff --git a/spec/java_integration/methods/dispatch_spec.rb b/spec/java_integration/methods/dispatch_spec.rb
index d1214003b6..946d225dbf 100644
--- a/spec/java_integration/methods/dispatch_spec.rb
+++ b/spec/java_integration/methods/dispatch_spec.rb
@@ -1,326 +1,358 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 import "java_integration.fixtures.ClassWithVarargs"
 import "java_integration.fixtures.CoreTypeMethods"
 import "java_integration.fixtures.StaticMethodSelection"
 
 describe "Non-overloaded static Java methods" do
   it "should raise ArgumentError when called with incorrect arity" do
     lambda do
       java.util.Collections.empty_list('foo')
     end.should raise_error(ArgumentError)
   end
 end
 
 describe "An overloaded Java static method" do
   it "should be called with the most exact overload" do
     obj = java.lang.Integer.new(1)
     CoreTypeMethods.getType(1).should == "long"
     CoreTypeMethods.getType(1, obj).should == "long,object"
     CoreTypeMethods.getType(1, obj, obj).should == "long,object,object"
     CoreTypeMethods.getType(1, obj, obj, obj).should == "long,object,object,object"
     CoreTypeMethods.getType(1.0).should == "double"
     CoreTypeMethods.getType(1.0, obj).should == "double,object"
     CoreTypeMethods.getType(1.0, obj, obj).should == "double,object,object"
     CoreTypeMethods.getType(1.0, obj, obj, obj).should == "double,object,object,object"
     
     obj = "foo"
     CoreTypeMethods.getType(1).should == "long"
     CoreTypeMethods.getType(1, obj).should == "long,string"
     CoreTypeMethods.getType(1, obj, obj).should == "long,string,string"
     CoreTypeMethods.getType(1, obj, obj, obj).should == "long,string,string,string"
     CoreTypeMethods.getType(1.0).should == "double"
     CoreTypeMethods.getType(1.0, obj).should == "double,string"
     CoreTypeMethods.getType(1.0, obj, obj).should == "double,string,string"
     CoreTypeMethods.getType(1.0, obj, obj, obj).should == "double,string,string,string"
   end
 
   it "should raise error when called with too many args" do
     lambda do
       obj = java.lang.Integer.new(1)
       CoreTypeMethods.getType(1, obj, obj, obj, obj)
     end.should raise_error(ArgumentError)
       
     lambda do
       obj = "foo"
       CoreTypeMethods.getType(1, obj, obj, obj, obj)
     end.should raise_error(ArgumentError)
   end
 
   it "should raise error when called with too few args" do
     lambda do
       CoreTypeMethods.getType()
     end.should raise_error(ArgumentError)
 
     lambda do
       CoreTypeMethods.getType()
     end.should raise_error(ArgumentError)
   end
 end
 
 describe "The return value of an overridden Java static method" do
   before(:each) do
     @return_value = StaticMethodSelection.produce
   end
   it "should not be nil" do
     @return_value.should_not be_nil
   end
   it "should be of the correct type" do
     @return_value.should be_an_instance_of(StaticMethodSelection)
   end 
 end
 
 describe "An overloaded Java instance method" do
   it "should be called with the most exact overload" do
     obj = java.lang.Integer.new(1)
     ctm = CoreTypeMethods.new
     ctm.getTypeInstance(1).should == "long"
     ctm.getTypeInstance(1, obj).should == "long,object"
     ctm.getTypeInstance(1, obj, obj).should == "long,object,object"
     ctm.getTypeInstance(1, obj, obj, obj).should == "long,object,object,object"
     ctm.getTypeInstance(1.0).should == "double"
     ctm.getTypeInstance(1.0, obj).should == "double,object"
     ctm.getTypeInstance(1.0, obj, obj).should == "double,object,object"
     ctm.getTypeInstance(1.0, obj, obj, obj).should == "double,object,object,object"
     
     obj = "foo"
     ctm = CoreTypeMethods.new
     ctm.getTypeInstance(1).should == "long"
     ctm.getTypeInstance(1, obj).should == "long,string"
     ctm.getTypeInstance(1, obj, obj).should == "long,string,string"
     ctm.getTypeInstance(1, obj, obj, obj).should == "long,string,string,string"
     ctm.getTypeInstance(1.0).should == "double"
     ctm.getTypeInstance(1.0, obj).should == "double,string"
     ctm.getTypeInstance(1.0, obj, obj).should == "double,string,string"
     ctm.getTypeInstance(1.0, obj, obj, obj).should == "double,string,string,string"
   end
 
   it "should raise error when called with too many args" do
     lambda do
       obj = java.lang.Integer.new(1)
       CoreTypeMethods.new.getTypeInstance(1, obj, obj, obj, obj)
     end.should raise_error(ArgumentError)
       
     lambda do
       obj = "foo"
       CoreTypeMethods.new.getTypeInstance(1, obj, obj, obj, obj)
     end.should raise_error(ArgumentError)
   end
 
   it "should raise error when called with too few args" do
     lambda do
       CoreTypeMethods.new.getTypeInstance()
     end.should raise_error(ArgumentError)
 
     lambda do
       CoreTypeMethods.new.getTypeInstance()
     end.should raise_error(ArgumentError)
   end
 end
 
 describe "A class with varargs constructors" do
   it "should be called with the most exact overload" do
     lambda do
       obj = ClassWithVarargs.new(1)
       obj.constructor.should == "0: [1]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new(1,2)
       obj.constructor.should == "0: [1, 2]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new(1,2,3)
       obj.constructor.should == "0: [1, 2, 3]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new(1,2,3,4)
       obj.constructor.should == "0: [1, 2, 3, 4]"
     end.should_not raise_error
 
     lambda do
       obj = ClassWithVarargs.new('foo', 1)
       obj.constructor.should == "1: [1]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new('foo', 1, 2)
       obj.constructor.should == "1: [1, 2]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new('foo', 1, 2, 3)
       obj.constructor.should == "1: [1, 2, 3]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new('foo', 1, 2, 3, 4)
       obj.constructor.should == "1: [1, 2, 3, 4]"
     end.should_not raise_error
 
     lambda do
       obj = ClassWithVarargs.new('foo', 'bar', 1)
       obj.constructor.should == "2: [1]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new('foo', 'bar', 1, 2)
       obj.constructor.should == "2: [1, 2]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new('foo', 'bar', 1, 2, 3)
       obj.constructor.should == "2: [1, 2, 3]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new('foo', 'bar', 1, 2, 3, 4)
       obj.constructor.should == "2: [1, 2, 3, 4]"
     end.should_not raise_error
 
     lambda do
       obj = ClassWithVarargs.new('foo', 'bar', 'baz', 1)
       obj.constructor.should == "3: [1]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new('foo', 'bar', 'baz', 1, 2)
       obj.constructor.should == "3: [1, 2]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new('foo', 'bar', 'baz', 1, 2, 3)
       obj.constructor.should == "3: [1, 2, 3]"
     end.should_not raise_error
     lambda do
       obj = ClassWithVarargs.new('foo', 'bar', 'baz', 1, 2, 3, 4)
       obj.constructor.should == "3: [1, 2, 3, 4]"
     end.should_not raise_error
   end
 
   it "should be callable with an array" do
     ClassWithVarargs.new([1,2,3].to_java).constructor.should == "0: [1, 2, 3]"
     ClassWithVarargs.new('foo', [1,2,3].to_java).constructor.should == "1: [1, 2, 3]"
     ClassWithVarargs.new('foo', 'bar', [1,2,3].to_java).constructor.should == "2: [1, 2, 3]"
     ClassWithVarargs.new('foo', 'bar', 'baz', [1,2,3].to_java).constructor.should == "3: [1, 2, 3]"
   end
 end
 
 describe "A class with varargs instance methods" do
   it "should be called with the most exact overload" do
     obj = ClassWithVarargs.new(1)
     lambda do
       obj.varargs(1).should == "0: [1]";
     end.should_not raise_error
     lambda do
       obj.varargs(1,2).should == "0: [1, 2]";
     end.should_not raise_error
     lambda do
       obj.varargs(1,2,3).should == "0: [1, 2, 3]";
     end.should_not raise_error
     lambda do
       obj.varargs(1,2,3,4).should == "0: [1, 2, 3, 4]";
     end.should_not raise_error
 
     lambda do
       obj.varargs('foo', 1).should == "1: [1]";
     end.should_not raise_error
     lambda do
       obj.varargs('foo', 1, 2).should == "1: [1, 2]";
     end.should_not raise_error
     lambda do
       obj.varargs('foo', 1, 2, 3).should == "1: [1, 2, 3]";
     end.should_not raise_error
     lambda do
       obj.varargs('foo', 1, 2, 3, 4).should == "1: [1, 2, 3, 4]";
     end.should_not raise_error
 
     lambda do
       obj.varargs('foo', 'bar', 1).should == "2: [1]";
     end.should_not raise_error
     lambda do
       obj.varargs('foo', 'bar', 1, 2).should == "2: [1, 2]";
     end.should_not raise_error
     lambda do
       obj.varargs('foo', 'bar', 1, 2, 3).should == "2: [1, 2, 3]";
     end.should_not raise_error
     lambda do
       obj.varargs('foo', 'bar', 1, 2, 3, 4).should == "2: [1, 2, 3, 4]";
     end.should_not raise_error
 
     lambda do
       obj.varargs('foo', 'bar', 'baz', 1).should == "3: [1]";
     end.should_not raise_error
     lambda do
       obj.varargs('foo', 'bar', 'baz', 1, 2).should == "3: [1, 2]";
     end.should_not raise_error
     lambda do
       obj.varargs('foo', 'bar', 'baz', 1, 2, 3).should == "3: [1, 2, 3]";
     end.should_not raise_error
     lambda do
       obj.varargs('foo', 'bar', 'baz', 1, 2, 3, 4).should == "3: [1, 2, 3, 4]";
     end.should_not raise_error
   end
 
   it "should be callable with an array" do
     obj = ClassWithVarargs.new(1)
     obj.varargs([1,2,3].to_java).should == "0: [1, 2, 3]"
     obj.varargs('foo', [1,2,3].to_java).should == "1: [1, 2, 3]"
     obj.varargs('foo', 'bar', [1,2,3].to_java).should == "2: [1, 2, 3]"
     obj.varargs('foo', 'bar', 'baz', [1,2,3].to_java).should == "3: [1, 2, 3]"
   end
 end
 
 describe "A class with varargs static methods" do
   it "should be called with the most exact overload" do
     lambda do
       ClassWithVarargs.varargs_static(1).should == "0: [1]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static(1,2).should == "0: [1, 2]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static(1,2,3).should == "0: [1, 2, 3]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static(1,2,3,4).should == "0: [1, 2, 3, 4]";
     end.should_not raise_error
 
     lambda do
       ClassWithVarargs.varargs_static('foo', 1).should == "1: [1]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static('foo', 1, 2).should == "1: [1, 2]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static('foo', 1, 2, 3).should == "1: [1, 2, 3]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static('foo', 1, 2, 3, 4).should == "1: [1, 2, 3, 4]";
     end.should_not raise_error
 
     lambda do
       ClassWithVarargs.varargs_static('foo', 'bar', 1).should == "2: [1]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static('foo', 'bar', 1, 2).should == "2: [1, 2]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static('foo', 'bar', 1, 2, 3).should == "2: [1, 2, 3]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static('foo', 'bar', 1, 2, 3, 4).should == "2: [1, 2, 3, 4]";
     end.should_not raise_error
 
     lambda do
       ClassWithVarargs.varargs_static('foo', 'bar', 'baz', 1).should == "3: [1]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static('foo', 'bar', 'baz', 1, 2).should == "3: [1, 2]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static('foo', 'bar', 'baz', 1, 2, 3).should == "3: [1, 2, 3]";
     end.should_not raise_error
     lambda do
       ClassWithVarargs.varargs_static('foo', 'bar', 'baz', 1, 2, 3, 4).should == "3: [1, 2, 3, 4]";
     end.should_not raise_error
   end
 
   it "should be callable with an array" do
     ClassWithVarargs.varargs_static([1,2,3].to_java).should == "0: [1, 2, 3]"
     ClassWithVarargs.varargs_static('foo', [1,2,3].to_java).should == "1: [1, 2, 3]"
     ClassWithVarargs.varargs_static('foo', 'bar', [1,2,3].to_java).should == "2: [1, 2, 3]"
     ClassWithVarargs.varargs_static('foo', 'bar', 'baz', [1,2,3].to_java).should == "3: [1, 2, 3]"
   end
+end
+
+# JRUBY-5418
+describe "A Java method dispatch downstream from a Kernel#catch block" do
+  it "should propagate rather than wrap the 'throw' exception" do
+    lambda do
+      catch(:foo) do
+        UsesSingleMethodInterface.new { throw :foo }
+      end
+    end.should_not raise_error
+    lambda do
+      catch(:foo) do
+        UsesSingleMethodInterface.new(nil) { throw :foo }
+      end
+    end.should_not raise_error
+    lambda do
+      catch(:foo) do
+        UsesSingleMethodInterface.new(nil, nil) { throw :foo }
+      end
+    end.should_not raise_error
+    lambda do
+      catch(:foo) do
+        UsesSingleMethodInterface.new(nil, nil, nil) { throw :foo }
+      end
+    end.should_not raise_error
+    # 3 normal args is our cutoff for specific-arity optz, so test four
+    lambda do
+      catch(:foo) do
+        UsesSingleMethodInterface.new(nil, nil, nil, nil) { throw :foo }
+      end
+    end.should_not raise_error
+  end
 end
\ No newline at end of file
diff --git a/src/org/jruby/javasupport/JavaSupport.java b/src/org/jruby/javasupport/JavaSupport.java
index bdc7095321..a40d42e0fc 100644
--- a/src/org/jruby/javasupport/JavaSupport.java
+++ b/src/org/jruby/javasupport/JavaSupport.java
@@ -1,334 +1,343 @@
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
 
 import java.lang.reflect.Member;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.exceptions.RaiseException;
+import org.jruby.exceptions.Unrescuable;
 import org.jruby.javasupport.util.ObjectProxyCache;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.WeakIdentityHashMap;
 
 public class JavaSupport {
     private static final Map<String,Class> PRIMITIVE_CLASSES = new HashMap<String,Class>();
     static {
         PRIMITIVE_CLASSES.put("boolean", Boolean.TYPE);
         PRIMITIVE_CLASSES.put("byte", Byte.TYPE);
         PRIMITIVE_CLASSES.put("char", Character.TYPE);
         PRIMITIVE_CLASSES.put("short", Short.TYPE);
         PRIMITIVE_CLASSES.put("int", Integer.TYPE);
         PRIMITIVE_CLASSES.put("long", Long.TYPE);
         PRIMITIVE_CLASSES.put("float", Float.TYPE);
         PRIMITIVE_CLASSES.put("double", Double.TYPE);
     }
     
     public static Class getPrimitiveClass(String primitiveType) {
         return PRIMITIVE_CLASSES.get(primitiveType);
     }
 
     private final Ruby runtime;
     
     private final ObjectProxyCache<IRubyObject,RubyClass> objectProxyCache = 
         // TODO: specifying soft refs, may want to compare memory consumption,
         // behavior with weak refs (specify WEAK in place of SOFT below)
         new ObjectProxyCache<IRubyObject,RubyClass>(ObjectProxyCache.ReferenceType.WEAK) {
 
         public IRubyObject allocateProxy(Object javaObject, RubyClass clazz) {
             return Java.allocateProxy(javaObject, clazz);
         }
     };
     
     private boolean active;
     
     // There's not a compelling reason to keep JavaClass instances in a weak map
     // (any proxies created are [were] kept in a non-weak map, so in most cases they will
     // stick around anyway), and some good reasons not to (JavaClass creation is
     // expensive, for one; many lookups are performed when passing parameters to/from
     // methods; etc.).
     // TODO: faster custom concurrent map
     private final ConcurrentHashMap<Class,JavaClass> javaClassCache =
         new ConcurrentHashMap<Class, JavaClass>(128);
     
     // FIXME: needs to be rethought
     private final Map matchCache = Collections.synchronizedMap(new HashMap(128));
 
     private RubyModule javaModule;
     private RubyModule javaUtilitiesModule;
     private RubyModule javaArrayUtilitiesModule;
     private RubyClass javaObjectClass;
     private JavaClass objectJavaClass;
     private RubyClass javaClassClass;
     private RubyClass javaArrayClass;
     private RubyClass javaProxyClass;
     private RubyClass arrayJavaProxyCreatorClass;
     private RubyClass javaFieldClass;
     private RubyClass javaMethodClass;
     private RubyClass javaConstructorClass;
     private RubyModule javaInterfaceTemplate;
     private RubyModule packageModuleTemplate;
     private RubyClass arrayProxyClass;
     private RubyClass concreteProxyClass;
     private RubyClass mapJavaProxy;
     
     private final Map<String, JavaClass> nameClassMap = new HashMap<String, JavaClass>();
 
     private final Map<Object, Object[]> javaObjectVariables = new WeakIdentityHashMap();
     
     public JavaSupport(Ruby ruby) {
         this.runtime = ruby;
     }
     
     final Map getMatchCache() {
         return matchCache;
     }
     
     public boolean isActive() {
         return active;
     }
     
     public void setActive(boolean active) {
         this.active = active;
     }
     
     private Class loadJavaClass(String className) throws ClassNotFoundException {
         Class primitiveClass;
         if ((primitiveClass = PRIMITIVE_CLASSES.get(className)) == null) {
             if (!Ruby.isSecurityRestricted()) {
                 return Class.forName(className, true, runtime.getJRubyClassLoader());
             }
             return Class.forName(className);
         }
         return primitiveClass;
     }
     
     public Class loadJavaClassVerbose(String className) {
         try {
             return loadJavaClass(className);
         } catch (ClassNotFoundException cnfExcptn) {
             throw runtime.newNameError("cannot load Java class " + className, className, cnfExcptn);
         } catch (ExceptionInInitializerError eiie) {
             throw runtime.newNameError("cannot initialize Java class " + className, className, eiie);
         } catch (LinkageError le) {
             throw runtime.newNameError("cannot link Java class " + className + ", probable missing dependency: " + le.getLocalizedMessage(), className, le);
         } catch (SecurityException se) {
             if (runtime.isVerbose()) se.printStackTrace(runtime.getErrorStream());
             throw runtime.newSecurityError(se.getLocalizedMessage());
         }
     }
     
     public Class loadJavaClassQuiet(String className) {
         try {
             return loadJavaClass(className);
         } catch (ClassNotFoundException cnfExcptn) {
             throw runtime.newNameError("cannot load Java class " + className, className, cnfExcptn, false);
         } catch (ExceptionInInitializerError eiie) {
             throw runtime.newNameError("cannot initialize Java class " + className, className, eiie, false);
         } catch (LinkageError le) {
             throw runtime.newNameError("cannot link Java class " + className, className, le, false);
         } catch (SecurityException se) {
             throw runtime.newSecurityError(se.getLocalizedMessage());
         }
     }
 
     public JavaClass getJavaClassFromCache(Class clazz) {
         return javaClassCache.get(clazz);
     }
     
     public void putJavaClassIntoCache(JavaClass clazz) {
         javaClassCache.put(clazz.javaClass(), clazz);
     }
 
     public void handleNativeException(Throwable exception, Member target) {
         if (exception instanceof RaiseException) {
+            // allow RaiseExceptions to propagate
             throw (RaiseException) exception;
+        } else if (exception instanceof Unrescuable) {
+            // allow "unrescuable" flow-control exceptions to propagate
+            if (exception instanceof Error) {
+                throw (Error)exception;
+            } else if (exception instanceof RuntimeException) {
+                throw (RuntimeException)exception;
+            }
         }
         throw createRaiseException(exception, target);
     }
 
     private RaiseException createRaiseException(Throwable exception, Member target) {
         return RaiseException.createNativeRaiseException(runtime, exception, target);
     }
 
     public ObjectProxyCache<IRubyObject,RubyClass> getObjectProxyCache() {
         return objectProxyCache;
     }
 
     // not synchronizing these methods, no harm if these values get set more
     // than once.
     // (also note that there's no chance of getting a partially initialized
     // class/module, as happens-before is guaranteed by volatile write/read
     // of constants table.)
     
     public Map<String, JavaClass> getNameClassMap() {
         return nameClassMap;
     }
 
     public void setJavaObjectVariable(Object o, int i, Object v) {
         synchronized (javaObjectVariables) {
             Object[] vars = javaObjectVariables.get(o);
             if (vars == null) {
                 vars = new Object[i + 1];
                 javaObjectVariables.put(o, vars);
             } else if (vars.length <= i) {
                 Object[] newVars = new Object[i + 1];
                 System.arraycopy(vars, 0, newVars, 0, vars.length);
                 javaObjectVariables.put(o, newVars);
                 vars = newVars;
             }
             vars[i] = v;
         }
     }
     
     public Object getJavaObjectVariable(Object o, int i) {
         if (i == -1) return null;
         
         synchronized (javaObjectVariables) {
             Object[] vars = javaObjectVariables.get(o);
             if (vars == null || vars.length <= i) return null;
             return vars[i];
         }
     }
     
     public RubyModule getJavaModule() {
         RubyModule module;
         if ((module = javaModule) != null) return module;
         return javaModule = runtime.fastGetModule("Java");
     }
     
     public RubyModule getJavaUtilitiesModule() {
         RubyModule module;
         if ((module = javaUtilitiesModule) != null) return module;
         return javaUtilitiesModule = runtime.fastGetModule("JavaUtilities");
     }
     
     public RubyModule getJavaArrayUtilitiesModule() {
         RubyModule module;
         if ((module = javaArrayUtilitiesModule) != null) return module;
         return javaArrayUtilitiesModule = runtime.fastGetModule("JavaArrayUtilities");
     }
     
     public RubyClass getJavaObjectClass() {
         RubyClass clazz;
         if ((clazz = javaObjectClass) != null) return clazz;
         return javaObjectClass = getJavaModule().fastGetClass("JavaObject");
     }
     
     public JavaClass getObjectJavaClass() {
         return objectJavaClass;
     }
     
     public void setObjectJavaClass(JavaClass objectJavaClass) {
         this.objectJavaClass = objectJavaClass;
     }
 
     public RubyClass getJavaArrayClass() {
         RubyClass clazz;
         if ((clazz = javaArrayClass) != null) return clazz;
         return javaArrayClass = getJavaModule().fastGetClass("JavaArray");
     }
     
     public RubyClass getJavaClassClass() {
         RubyClass clazz;
         if ((clazz = javaClassClass) != null) return clazz;
         return javaClassClass = getJavaModule().fastGetClass("JavaClass");
     }
 
     public RubyModule getJavaInterfaceTemplate() {
         RubyModule module;
         if ((module = javaInterfaceTemplate) != null) return module;
         return javaInterfaceTemplate = runtime.fastGetModule("JavaInterfaceTemplate");
     }
 
     public RubyModule getPackageModuleTemplate() {
         RubyModule module;
         if ((module = packageModuleTemplate) != null) return module;
         return packageModuleTemplate = runtime.fastGetModule("JavaPackageModuleTemplate");
     }
     
     public RubyClass getJavaProxyClass() {
         RubyClass clazz;
         if ((clazz = javaProxyClass) != null) return clazz;
         return javaProxyClass = runtime.fastGetClass("JavaProxy");
     }
 
     public RubyClass getArrayJavaProxyCreatorClass() {
         RubyClass clazz;
         if ((clazz = arrayJavaProxyCreatorClass) != null) return clazz;
         return arrayJavaProxyCreatorClass = runtime.fastGetClass("ArrayJavaProxyCreator");
     }
     
     public RubyClass getConcreteProxyClass() {
         RubyClass clazz;
         if ((clazz = concreteProxyClass) != null) return clazz;
         return concreteProxyClass = runtime.fastGetClass("ConcreteJavaProxy");
     }
 
     public RubyClass getMapJavaProxyClass() {
         RubyClass clazz;
         if ((clazz = mapJavaProxy) != null) return clazz;
         return mapJavaProxy = runtime.fastGetClass("MapJavaProxy");
     }
     
     public RubyClass getArrayProxyClass() {
         RubyClass clazz;
         if ((clazz = arrayProxyClass) != null) return clazz;
         return arrayProxyClass = runtime.fastGetClass("ArrayJavaProxy");
     }
     
     public RubyClass getJavaFieldClass() {
         RubyClass clazz;
         if ((clazz = javaFieldClass) != null) return clazz;
         return javaFieldClass = getJavaModule().fastGetClass("JavaField");
     }
 
     public RubyClass getJavaMethodClass() {
         RubyClass clazz;
         if ((clazz = javaMethodClass) != null) return clazz;
         return javaMethodClass = getJavaModule().fastGetClass("JavaMethod");
     }
 
     public RubyClass getJavaConstructorClass() {
         RubyClass clazz;
         if ((clazz = javaConstructorClass) != null) return clazz;
         return javaConstructorClass = getJavaModule().fastGetClass("JavaConstructor");
     }
 
 }
