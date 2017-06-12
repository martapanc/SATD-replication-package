diff --git a/spec/java_integration/interfaces/implementation_spec.rb b/spec/java_integration/interfaces/implementation_spec.rb
index ec30f3b7ec..95eaaa6e32 100644
--- a/spec/java_integration/interfaces/implementation_spec.rb
+++ b/spec/java_integration/interfaces/implementation_spec.rb
@@ -1,587 +1,605 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 import "java_integration.fixtures.SingleMethodInterface"
 import "java_integration.fixtures.UsesSingleMethodInterface"
 import "java_integration.fixtures.DescendantOfSingleMethodInterface"
 import "java_integration.fixtures.UsesDescendantOfSingleMethodInterface"
 import "java_integration.fixtures.BeanLikeInterface"
 import "java_integration.fixtures.BeanLikeInterfaceHandler"
 import "java_integration.fixtures.ConstantHoldingInterface"
 import "java_integration.fixtures.ReturnsInterface"
 import "java_integration.fixtures.ReturnsInterfaceConsumer"
 import "java_integration.fixtures.AnotherRunnable"
 import "java.lang.Runnable"
 
 describe "Single-method Java interfaces implemented in Ruby" do
   before :all do
     @value_holder1 = Class.new do
       include SingleMethodInterface
       def initialize(val)
         @value = val
       end
       def callIt
         @value
       end
     end
 
     @value_holder2 = Class.new do
       include SingleMethodInterface
       def initialize(val)
         @value = val
       end
       def call_it
         @value
       end
     end
   end
  
   it "should be kind_of? the interface" do
     @value_holder1.new(1).should be_kind_of(SingleMethodInterface)
     SingleMethodInterface.should === @value_holder1.new(1)
   end
 
   it "should be implemented with 'include InterfaceClass'" do
     UsesSingleMethodInterface.callIt(@value_holder1.new(1)).should == 1
     UsesSingleMethodInterface.callIt(@value_holder2.new(1)).should == 1
   end
 
   it "should be cast-able to the interface on the Java side" do
     UsesSingleMethodInterface.castAndCallIt(@value_holder1.new(2)).should == 2
     UsesSingleMethodInterface.castAndCallIt(@value_holder2.new(2)).should == 2
   end
   
   it "should allow implementation using the underscored version" do
     UsesSingleMethodInterface.callIt(@value_holder2.new(3)).should == 3
   end
   
   it "should allow reopening implementations" do
     @value_holder3 = Class.new do
       include SingleMethodInterface
       def initialize(val)
         @value = val
       end
       def callIt
         @value
       end
     end
     obj = @value_holder3.new(4);
     UsesSingleMethodInterface.callIt(obj).should == 4
     @value_holder3.class_eval do
       def callIt
         @value + @value
       end
     end
     UsesSingleMethodInterface.callIt(obj).should == 8
     
     @value_holder3 = Class.new do
       include SingleMethodInterface
       def initialize(val)
         @value = val
       end
       def call_it
         @value
       end
     end
     obj = @value_holder3.new(4);
     UsesSingleMethodInterface.callIt(obj).should == 4
     @value_holder3.class_eval do
       def call_it
         @value + @value
       end
     end
     UsesSingleMethodInterface.callIt(obj).should == 8
   end
   
   it "should use Object#equals if there is no Ruby equals defined" do 
     c = Class.new
     c.send :include, java.util.Map
     arr = java.util.ArrayList.new
     v = c.new
     arr.add(v)
     arr.contains(v).should be_true
   end
 
   it "should use Object#hashCode if there is no Ruby hashCode defined" do 
     c = Class.new
     c.send :include, java.util.Map
     UsesSingleMethodInterface.hashCode(c.new)
   end
 
   it "should use Object#toString if there is no Ruby toString defined" do 
     c = Class.new
     c.send :include, java.util.Map
     UsesSingleMethodInterface.toString(c.new)
   end
 
   it "should allow including the same interface twice" do
     c = Class.new do
       include SingleMethodInterface
       include SingleMethodInterface
 
       def initialize(val)
         @value = val
       end
       def callIt
         @value
       end
     end
     UsesSingleMethodInterface.callIt(c.new(1)).should == 1
   end
 end
 
 describe "Single-method Java interfaces" do
   it "can be coerced from a block passed to a constructor" do
     UsesSingleMethodInterface.new { 1 }.result.should == 1
     UsesSingleMethodInterface.new(nil) { 1 }.result.should == 1
     UsesSingleMethodInterface.new(nil, nil) { 1 }.result.should == 1
     UsesSingleMethodInterface.new(nil, nil, nil) { 1 }.result.should == 1
     # 3 normal args is our cutoff for specific-arity optz, so test four
     UsesSingleMethodInterface.new(nil, nil, nil, nil) { 1 }.result.should == 1
   end
   
   it "can be coerced from a block passed to a static method" do
     UsesSingleMethodInterface.callIt { 1 }.should == 1
     UsesSingleMethodInterface.callIt(nil) { 1 }.should == 1
     UsesSingleMethodInterface.callIt(nil, nil) { 1 }.should == 1
     UsesSingleMethodInterface.callIt(nil, nil, nil) { 1 }.should == 1
     # 3 normal args is our cutoff for specific-arity optz, so test four
     UsesSingleMethodInterface.callIt(nil, nil, nil, nil) { 1 }.should == 1
   end
   
   it "can be coerced from a block passed to a instance method" do
     UsesSingleMethodInterface.new.callIt2 do 1 end.should == 1
     UsesSingleMethodInterface.new.callIt2(nil) do 1 end.should == 1
     UsesSingleMethodInterface.new.callIt2(nil, nil) do 1 end.should == 1
     UsesSingleMethodInterface.new.callIt2(nil, nil, nil) do 1 end.should == 1
     # 3 normal args is our cutoff for specific-arity optz, so test four
     UsesSingleMethodInterface.new.callIt2(nil, nil, nil, nil) do 1 end.should == 1
   end
   
   it "should be implementable with .impl" do
     impl = SingleMethodInterface.impl {|name| name}
     impl.should be_kind_of(SingleMethodInterface)
     SingleMethodInterface.should === impl
     
     UsesSingleMethodInterface.callIt(impl).should == :callIt
   end
 
   it "should allow assignable equivalents to be passed to a method" do
     impl = DescendantOfSingleMethodInterface.impl {|name| name}
     impl.should be_kind_of(SingleMethodInterface)
     DescendantOfSingleMethodInterface.should === impl
     UsesSingleMethodInterface.callIt(impl).should == :callIt
     UsesSingleMethodInterface.new.callIt2(impl).should == :callIt
   end
 
   it "should maintain Ruby object equality when passed through Java and back" do
     result = SingleMethodInterface.impl {|name| name}
     callable = mock "callable"
     callable.should_receive(:call).and_return result
     UsesSingleMethodInterface.new.callIt3(callable).should == result
   end
 end
 
 describe "A bean-like Java interface" do
   it "allows implementation with attr* methods" do
     myimpl1 = Class.new do
       include BeanLikeInterface
       attr_accessor :value, :my_value, :foo, :my_foo
     end
     myimpl2 = Class.new do
       include BeanLikeInterface
       attr_accessor :value, :myValue, :foo, :myFoo
     end
     [myimpl1, myimpl2].each do |impl|
       bli = impl.new
       blih = BeanLikeInterfaceHandler.new(bli)
       lambda do
         blih.setValue(1)
         blih.setMyValue(2)
         blih.setFoo(true)
         blih.setMyFoo(true)
         blih.getValue().should == 1
         blih.getMyValue().should == 2
         blih.isFoo().should == true
         blih.isMyFoo().should == true
       end.should_not raise_error
     end
   end
   
   it "allows implementing boolean methods with ? names" do
     # Java name before Ruby name (un-beaned)
     myimpl1 = Class.new do
       include BeanLikeInterface
       def isMyFoo; true; end
       def is_my_foo; false; end
     end
     BeanLikeInterfaceHandler.new(myimpl1.new).isMyFoo().should == true
     # Ruby name before beaned Java name
     myimpl2 = Class.new do
       include BeanLikeInterface
       def is_my_foo; true; end
       def myFoo; false; end
     end
     BeanLikeInterfaceHandler.new(myimpl2.new).isMyFoo().should == true
     # Beaned Java name before beaned Ruby name
     myimpl3 = Class.new do
       include BeanLikeInterface
       def myFoo; true; end
       def my_foo; false; end
     end
     BeanLikeInterfaceHandler.new(myimpl3.new).isMyFoo().should == true
     # Beaned Ruby name before q-marked beaned Java name
     myimpl4 = Class.new do
       include BeanLikeInterface
       def my_foo; true; end
       def myFoo?; false; end
     end
     BeanLikeInterfaceHandler.new(myimpl4.new).isMyFoo().should == true
     # Q-marked beaned Java name before Q-marked beaned Ruby name
     myimpl5 = Class.new do
       include BeanLikeInterface
       def myFoo?; true; end
       def my_foo?; false; end
     end
     BeanLikeInterfaceHandler.new(myimpl5.new).isMyFoo().should == true
     # Confirm q-marked beaned Ruby name works
     myimpl6 = Class.new do
       include BeanLikeInterface
       def my_foo?; true; end
     end
     BeanLikeInterfaceHandler.new(myimpl6.new).isMyFoo().should == true
 
     # Java name before Ruby name
     myimpl1 = Class.new do
       include BeanLikeInterface
       def supahFriendly; true; end
       def supah_friendly; false; end
     end
     BeanLikeInterfaceHandler.new(myimpl1.new).supahFriendly().should == true
     # Ruby name before q-marked Java name
     myimpl2 = Class.new do
       include BeanLikeInterface
       def supah_friendly; true; end
       def supahFriendly?; false; end
     end
     BeanLikeInterfaceHandler.new(myimpl2.new).supahFriendly().should == true
     # Q-marked Java name before Q-marked Ruby name
     myimpl3 = Class.new do
       include BeanLikeInterface
       def supahFriendly?; true; end
       def supah_friendly?; false; end
     end
     BeanLikeInterfaceHandler.new(myimpl3.new).supahFriendly().should == true
     # confirm Q-marked Ruby name works
     myimpl4 = Class.new do
       include BeanLikeInterface
       def supah_friendly?; true; end
     end
     BeanLikeInterfaceHandler.new(myimpl4.new).supahFriendly().should == true
   end
   
   it "searches for implementation names in a predictable order" do
     myimpl1 = Class.new do
       include BeanLikeInterface
       def foo?
         true
       end
       def my_foo?
         true
       end
       def friendly?
         true
       end
       def supah_friendly?
         true
       end
     end
     myimpl2 = Class.new do
       include BeanLikeInterface
       def foo?
         true
       end
       def myFoo?
         true
       end
       def friendly?
         true
       end
       def supahFriendly?
         true
       end
     end
     [myimpl1, myimpl2].each do |impl|
       bli = impl.new
       blih = BeanLikeInterfaceHandler.new(bli)
       lambda do
         blih.isFoo().should == true
         blih.isMyFoo().should == true
         blih.friendly().should == true
         blih.supahFriendly().should == true
       end.should_not raise_error
     end
   end
   
   it "does not honor beanified implementations of methods that don't match javabean spec" do
     myimpl1 = Class.new do
       include BeanLikeInterface
       def something_foo(x)
         x
       end
       def something_foo=(x,y)
         y
       end
     end
     myimpl2 = Class.new do
       include BeanLikeInterface
       def somethingFoo(x)
         x
       end
       def somethingFoo=(x,y)
         y
       end
     end
     [myimpl1, myimpl2].each do |impl|
       bli = impl.new
       blih = BeanLikeInterfaceHandler.new(bli)
       lambda { blih.getSomethingFoo(1) }.should raise_error(NameError)
       lambda { blih.setSomethingFoo(1,2) }.should raise_error(NameError)
     end
   end
 end
 
 describe "A Ruby class including a descendant interface" do
   it "implements all methods from that interface and parents" do
     impl = Class.new do
       include DescendantOfSingleMethodInterface
       
       def callIt; "foo"; end
       def callThat; "bar"; end
     end
     
     dosmi = impl.new
     
     UsesSingleMethodInterface.callIt(dosmi).should == "foo"
     UsesDescendantOfSingleMethodInterface.callThat(dosmi).should == "bar"
   end
+
+  it "inherits implementation of super-interface methods from superclass" do
+    super_impl = Class.new do
+      include SingleMethodInterface
+      
+      def callIt; "foo"; end
+    end
+    
+    impl = Class.new(super_impl) do
+      include DescendantOfSingleMethodInterface
+      def callThat; "bar"; end
+    end
+    
+    dosmi = impl.new
+
+    UsesSingleMethodInterface.callIt(dosmi).should == "foo"
+    UsesDescendantOfSingleMethodInterface.callThat(dosmi).should == "bar"
+  end
 end
 
 describe "Single object implementing methods of interface" do 
   before(:each) do 
     @impl = Class.new do 
       include SingleMethodInterface
     end
   end
   
   it "should not be possible to call methods on instances of class from Java" do 
     proc do 
       SingleMethodInterface::Caller.call(@impl.new)
     end.should raise_error(NoMethodError)
   end
   
   it "should be possible to call methods on specific object that implements a method" do 
     obj = @impl.new
     def obj.callIt
       "foo"
     end
     SingleMethodInterface::Caller.call(obj).should == "foo"
   end
 end
 
 describe "Calling include to include a Java interface into a Ruby class" do
   it "should implement all interfaces specified into it" do
     m = Module.new do
       include SingleMethodInterface
       include BeanLikeInterface
     end
 
     c = Class.new do
       include m
       def call_it; "bar"; end
       def value; 1; end
     end
 
     obj = c.new
     blih = BeanLikeInterfaceHandler.new(obj)
 
     SingleMethodInterface::Caller.call(obj).should == "bar"
     blih.value.should == 1
   end
 
   it "should incorporate constants from the interface into the class's metaclass" do
     c = Class.new do
       include ConstantHoldingInterface
     end
 
     c::MY_INT.should == 1
     c::MY_STRING.should == "foo"
   end
 end
 
 describe "A ruby module used as a carrier for Java interfaces" do
   it "allows multiple interfaces" do
     m = Module.new do
       include SingleMethodInterface
       include BeanLikeInterface
 
       def self.java_interfaces; @java_interface_mods; end
     end
     m.java_interfaces.should include(SingleMethodInterface)
     m.java_interfaces.should include(BeanLikeInterface)
   end
 
   it "calls append_features on each interface" do
     my_smi = SingleMethodInterface.dup
     my_bli = BeanLikeInterface.dup
     [my_smi, my_bli].each do |my|
       class << my
         alias :old_af :append_features
         def append_features(cls)
           if @append_features_called
             @append_features_called += 1
           else
             @append_features_called = 1
           end
           old_af(cls)
         end
         def called; @append_features_called; end
       end
     end
 
     m = Module.new do
       include my_smi
       include my_bli
     end
 
     my_smi.called.should == 1
     my_bli.called.should == 1
 
     c = Class.new do
       include m
     end
 
     my_smi.called.should == 2
     my_bli.called.should == 2
   end
 
   it "causes an including class to implement all interfaces" do
     m = Module.new do
       include SingleMethodInterface
       include BeanLikeInterface
     end
 
     c = Class.new do
       include m
       def call_it; "bar"; end
       def value; 1; end
     end
 
     obj = c.new
     blih = BeanLikeInterfaceHandler.new(obj)
     
     SingleMethodInterface::Caller.call(obj).should == "bar"
     blih.value.should == 1
   end
 end
 
 describe "Coercion of normal ruby objects" do
   it "should allow an object passed to a java method to be coerced to the interface" do
     ri = mock "returns interface"
     consumer = ReturnsInterfaceConsumer.new
     consumer.set_returns_interface ri
     ri.should be_kind_of(ReturnsInterface)
   end
 
   it "should allow an object passed to a java constructor to be coerced to the interface" do
     ri = mock "returns interface"
     ReturnsInterfaceConsumer.new(ri)
     ri.should be_kind_of(ReturnsInterface)
   end
 
   it "should allow an object to be coerced as a return type of a java method" do
     ri = mock "returns interface"
     value = mock "return value runnable"
     ri.stub!(:getRunnable).and_return value
 
     consumer = ReturnsInterfaceConsumer.new(ri)
     runnable = consumer.getRunnable
     runnable.should == value
     value.should be_kind_of(Java::JavaLang::Runnable)
   end
 end
 
 describe "A child extending a Ruby class that includes Java interfaces" do
   it "should implement all those interfaces" do
     sup = Class.new { include BeanLikeInterface }
     child = Class.new(sup) { def value; 1; end }
 
     obj = child.new
     blih = BeanLikeInterfaceHandler.new(obj)
 
     blih.value.should == 1
   end
 end
 
 describe "Calling methods through interface on Ruby objects with methods defined on singleton class" do
   
   before(:each) do
     @klass = Class.new do
       include SingleMethodInterface
     end
     
     @obj1 = @klass.new
     @obj2 = @klass.new
   end
   
   it "should handle one object using instance_eval" do
     @obj1.instance_eval("def callIt; return :ok; end;")
     
     UsesSingleMethodInterface.callIt(@obj1).should == :ok
   end
   
   it "should handle one object extending module" do
     @module = Module.new { def callIt; return :ok; end; }
     @obj1.extend(@module)
     
     UsesSingleMethodInterface.callIt(@obj1).should == :ok
   end
   
   it "should handle two object with combo of instance_eval and module extension" do
     pending "Needs work in ifc impl method lookup logic" do
       @obj1.instance_eval("def callIt; return :one; end;")
       @module = Module.new { def callIt; return :two; end; }
       @obj2.extend(@module)
 
       UsesSingleMethodInterface.callIt(@obj1).should == :one
       UsesSingleMethodInterface.callIt(@obj2).should == :two
     end
   end
   
   it "should handle two object with combo of instance_eval and module extension in opposite order" do
     pending "Needs work in ifc impl method lookup logic" do
       @obj1.instance_eval("def callIt; return :one; end;")
       @module = Module.new { def callIt; return :two; end; }
       @obj2.extend(@module)
 
       UsesSingleMethodInterface.callIt(@obj2).should == :two
       UsesSingleMethodInterface.callIt(@obj1).should == :one
     end
   end
 end
 
 # JRUBY-2999
 describe "A Ruby class implementing Java interfaces with overlapping methods" do
   it "should implement without error" do
     cls = Class.new do
       include AnotherRunnable
       include Runnable
       attr_accessor :foo
       def run
         @foo = 'success'
       end
     end
 
     obj = nil
     lambda {obj = cls.new}.should_not raise_error
   end
 end
\ No newline at end of file
diff --git a/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java b/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java
index d26ef9a136..8adfd05d45 100644
--- a/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java
+++ b/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java
@@ -1,870 +1,880 @@
 /*
  * SkinnyMethodAdapter.java
  *
  * Created on March 10, 2007, 2:52 AM
  *
  * To change this template, choose Tools | Template Manager
  * and open the template in the editor.
  */
 
 package org.jruby.compiler.impl;
 
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import org.jruby.util.SafePropertyAccessor;
 import static org.jruby.util.CodegenUtils.*;
 import org.objectweb.asm.AnnotationVisitor;
 import org.objectweb.asm.Attribute;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.MethodVisitor;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.util.TraceMethodVisitor;
 
 /**
  *
  * @author headius
  */
 public class SkinnyMethodAdapter implements MethodVisitor, Opcodes {
     private final static boolean DEBUG = SafePropertyAccessor.getBoolean("jruby.compile.dump");
     private MethodVisitor method;
     
     /** Creates a new instance of SkinnyMethodAdapter */
     public SkinnyMethodAdapter(MethodVisitor method) {
         if (DEBUG) {
             this.method = new TraceMethodVisitor(method);
         } else {
             this.method = method;
         }
     }
     
     public SkinnyMethodAdapter() {
     }
     
     public MethodVisitor getMethodVisitor() {
         return method;
     }
     
     public void setMethodVisitor(MethodVisitor mv) {
         if (DEBUG) {
             this.method = new TraceMethodVisitor(mv);
         } else {
             this.method = mv;
         }
     }
     
     public void aload(int arg0) {
         getMethodVisitor().visitVarInsn(ALOAD, arg0);
     }
     
     public void iload(int arg0) {
         getMethodVisitor().visitVarInsn(ILOAD, arg0);
     }
     
     public void lload(int arg0) {
         getMethodVisitor().visitVarInsn(LLOAD, arg0);
     }
     
     public void fload(int arg0) {
         getMethodVisitor().visitVarInsn(FLOAD, arg0);
     }
     
     public void dload(int arg0) {
         getMethodVisitor().visitVarInsn(DLOAD, arg0);
     }
     
     public void astore(int arg0) {
         getMethodVisitor().visitVarInsn(ASTORE, arg0);
     }
     
     public void istore(int arg0) {
         getMethodVisitor().visitVarInsn(ISTORE, arg0);
     }
     
     public void lstore(int arg0) {
         getMethodVisitor().visitVarInsn(LSTORE, arg0);
     }
     
     public void fstore(int arg0) {
         getMethodVisitor().visitVarInsn(FSTORE, arg0);
     }
     
     public void dstore(int arg0) {
         getMethodVisitor().visitVarInsn(DSTORE, arg0);
     }
     
     public void ldc(Object arg0) {
         getMethodVisitor().visitLdcInsn(arg0);
     }
     
     public void bipush(int arg) {
         getMethodVisitor().visitIntInsn(BIPUSH, arg);
     }
     
     public void sipush(int arg) {
         getMethodVisitor().visitIntInsn(SIPUSH, arg);
     }
         
     public void pushInt(int value) {
         if (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE) {
             switch (value) {
             case -1:
                 iconst_m1();
                 break;
             case 0:
                 iconst_0();
                 break;
             case 1:
                 iconst_1();
                 break;
             case 2:
                 iconst_2();
                 break;
             case 3:
                 iconst_3();
                 break;
             case 4:
                 iconst_4();
                 break;
             case 5:
                 iconst_5();
                 break;
             default:
                 bipush(value);
                 break;
             }
         } else if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE) {
             sipush(value);
         } else {
             ldc(value);
         }
     }
         
     public void pushBoolean(boolean bool) {
         if (bool) iconst_1(); else iconst_0();
     }
     
     public void invokestatic(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitMethodInsn(INVOKESTATIC, arg1, arg2, arg3);
     }
     
     public void invokespecial(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitMethodInsn(INVOKESPECIAL, arg1, arg2, arg3);
     }
     
     public void invokevirtual(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitMethodInsn(INVOKEVIRTUAL, arg1, arg2, arg3);
     }
     
     public void invokeinterface(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitMethodInsn(INVOKEINTERFACE, arg1, arg2, arg3);
     }
     
     public void aprintln() {
         dup();
         getstatic(p(System.class), "out", ci(PrintStream.class));
         swap();
         invokevirtual(p(PrintStream.class), "println", sig(void.class, params(Object.class)));
     }
     
     public void areturn() {
         getMethodVisitor().visitInsn(ARETURN);
     }
     
     public void ireturn() {
         getMethodVisitor().visitInsn(IRETURN);
     }
     
     public void freturn() {
         getMethodVisitor().visitInsn(FRETURN);
     }
     
     public void lreturn() {
         getMethodVisitor().visitInsn(LRETURN);
     }
     
     public void dreturn() {
         getMethodVisitor().visitInsn(DRETURN);
     }
     
     public void newobj(String arg0) {
         getMethodVisitor().visitTypeInsn(NEW, arg0);
     }
     
     public void dup() {
         getMethodVisitor().visitInsn(DUP);
     }
     
     public void swap() {
         getMethodVisitor().visitInsn(SWAP);
     }
     
     public void swap2() {
         dup2_x2();
         pop2();
     }
     
     public void getstatic(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitFieldInsn(GETSTATIC, arg1, arg2, arg3);
     }
     
     public void putstatic(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitFieldInsn(PUTSTATIC, arg1, arg2, arg3);
     }
     
     public void getfield(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitFieldInsn(GETFIELD, arg1, arg2, arg3);
     }
     
     public void putfield(String arg1, String arg2, String arg3) {
         getMethodVisitor().visitFieldInsn(PUTFIELD, arg1, arg2, arg3);
     }
     
     public void voidreturn() {
         getMethodVisitor().visitInsn(RETURN);
     }
     
     public void anewarray(String arg0) {
         getMethodVisitor().visitTypeInsn(ANEWARRAY, arg0);
     }
     
     public void multianewarray(String arg0, int dims) {
         getMethodVisitor().visitMultiANewArrayInsn(arg0, dims);
     }
     
     public void newarray(int arg0) {
         getMethodVisitor().visitIntInsn(NEWARRAY, arg0);
     }
     
     public void iconst_m1() {
         getMethodVisitor().visitInsn(ICONST_M1);
     }
     
     public void iconst_0() {
         getMethodVisitor().visitInsn(ICONST_0);
     }
     
     public void iconst_1() {
         getMethodVisitor().visitInsn(ICONST_1);
     }
     
     public void iconst_2() {
         getMethodVisitor().visitInsn(ICONST_2);
     }
     
     public void iconst_3() {
         getMethodVisitor().visitInsn(ICONST_3);
     }
     
     public void iconst_4() {
         getMethodVisitor().visitInsn(ICONST_4);
     }
     
     public void iconst_5() {
         getMethodVisitor().visitInsn(ICONST_5);
     }
     
     public void lconst_0() {
         getMethodVisitor().visitInsn(LCONST_0);
     }
     
     public void aconst_null() {
         getMethodVisitor().visitInsn(ACONST_NULL);
     }
     
     public void label(Label label) {
         getMethodVisitor().visitLabel(label);
     }
     
     public void nop() {
         getMethodVisitor().visitInsn(NOP);
     }
     
     public void pop() {
         getMethodVisitor().visitInsn(POP);
     }
     
     public void pop2() {
         getMethodVisitor().visitInsn(POP2);
     }
     
     public void arrayload() {
         getMethodVisitor().visitInsn(AALOAD);
     }
     
     public void arraystore() {
         getMethodVisitor().visitInsn(AASTORE);
     }
     
     public void iarrayload() {
         getMethodVisitor().visitInsn(IALOAD);
     }
     
     public void barrayload() {
         getMethodVisitor().visitInsn(BALOAD);
     }
     
     public void barraystore() {
         getMethodVisitor().visitInsn(BASTORE);
     }
     
     public void aaload() {
         getMethodVisitor().visitInsn(AALOAD);
     }
     
     public void aastore() {
         getMethodVisitor().visitInsn(AASTORE);
     }
     
     public void iaload() {
         getMethodVisitor().visitInsn(IALOAD);
     }
     
     public void iastore() {
         getMethodVisitor().visitInsn(IASTORE);
     }
     
     public void laload() {
         getMethodVisitor().visitInsn(LALOAD);
     }
     
     public void lastore() {
         getMethodVisitor().visitInsn(LASTORE);
     }
     
     public void baload() {
         getMethodVisitor().visitInsn(BALOAD);
     }
     
     public void bastore() {
         getMethodVisitor().visitInsn(BASTORE);
     }
     
     public void saload() {
         getMethodVisitor().visitInsn(SALOAD);
     }
     
     public void sastore() {
         getMethodVisitor().visitInsn(SASTORE);
     }
     
     public void caload() {
         getMethodVisitor().visitInsn(CALOAD);
     }
     
     public void castore() {
         getMethodVisitor().visitInsn(CASTORE);
     }
     
     public void faload() {
         getMethodVisitor().visitInsn(FALOAD);
     }
     
     public void fastore() {
         getMethodVisitor().visitInsn(FASTORE);
     }
     
     public void daload() {
         getMethodVisitor().visitInsn(DALOAD);
     }
     
     public void dastore() {
         getMethodVisitor().visitInsn(DASTORE);
     }
     
     public void fcmpl() {
         getMethodVisitor().visitInsn(FCMPL);
     }
     
     public void fcmpg() {
         getMethodVisitor().visitInsn(FCMPG);
     }
     
     public void dcmpl() {
         getMethodVisitor().visitInsn(DCMPL);
     }
     
     public void dcmpg() {
         getMethodVisitor().visitInsn(DCMPG);
     }
     
     public void dup_x2() {
         getMethodVisitor().visitInsn(DUP_X2);
     }
     
     public void dup_x1() {
         getMethodVisitor().visitInsn(DUP_X1);
     }
     
     public void dup2_x2() {
         getMethodVisitor().visitInsn(DUP2_X2);
     }
     
     public void dup2_x1() {
         getMethodVisitor().visitInsn(DUP2_X1);
     }
     
     public void dup2() {
         getMethodVisitor().visitInsn(DUP2);
     }
     
     public void trycatch(Label arg0, Label arg1, Label arg2,
                                    String arg3) {
         getMethodVisitor().visitTryCatchBlock(arg0, arg1, arg2, arg3);
     }
     
     public void trycatch(String type, Runnable body, Runnable catchBody) {
         Label before = new Label();
         Label after = new Label();
         Label catchStart = new Label();
         Label done = new Label();
 
         trycatch(before, after, catchStart, type);
         label(before);
         body.run();
         label(after);
         go_to(done);
         if (catchBody != null) {
             label(catchStart);
             catchBody.run();
         }
         label(done);
     }
     
     public void go_to(Label arg0) {
         getMethodVisitor().visitJumpInsn(GOTO, arg0);
     }
     
     public void lookupswitch(Label arg0, int[] arg1, Label[] arg2) {
         getMethodVisitor().visitLookupSwitchInsn(arg0, arg1, arg2);
     }
     
     public void athrow() {
         getMethodVisitor().visitInsn(ATHROW);
     }
     
     public void instance_of(String arg0) {
         getMethodVisitor().visitTypeInsn(INSTANCEOF, arg0);
     }
     
     public void ifeq(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFEQ, arg0);
     }
     
     public void ifne(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFNE, arg0);
     }
     
     public void if_acmpne(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ACMPNE, arg0);
     }
     
     public void if_acmpeq(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ACMPEQ, arg0);
     }
     
     public void if_icmple(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ICMPLE, arg0);
     }
     
     public void if_icmpgt(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ICMPGT, arg0);
     }
     
     public void if_icmplt(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ICMPLT, arg0);
     }
     
     public void if_icmpne(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ICMPNE, arg0);
     }
     
     public void if_icmpeq(Label arg0) {
         getMethodVisitor().visitJumpInsn(IF_ICMPEQ, arg0);
     }
     
     public void checkcast(String arg0) {
         getMethodVisitor().visitTypeInsn(CHECKCAST, arg0);
     }
     
     public void start() {
         getMethodVisitor().visitCode();
     }
     
     public void end() {
         if (DEBUG) {
             PrintWriter pw = new PrintWriter(System.out);
             pw.write("*** Dumping ***\n");
             ((TraceMethodVisitor)getMethodVisitor()).print(pw);
             pw.flush();
         }
         getMethodVisitor().visitMaxs(1, 1);
         getMethodVisitor().visitEnd();
     }
+
+    public void line(int line) {
+        Label label = new Label();
+        label(label);
+        visitLineNumber(line, label);
+    }
+
+    public void line(int line, Label label) {
+        visitLineNumber(line, label);
+    }
     
     public void ifnonnull(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFNONNULL, arg0);
     }
     
     public void ifnull(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFNULL, arg0);
     }
     
     public void iflt(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFLT, arg0);
     }
     
     public void ifle(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFLE, arg0);
     }
     
     public void ifgt(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFGT, arg0);
     }
     
     public void ifge(Label arg0) {
         getMethodVisitor().visitJumpInsn(IFGE, arg0);
     }
     
     public void arraylength() {
         getMethodVisitor().visitInsn(ARRAYLENGTH);
     }
     
     public void ishr() {
         getMethodVisitor().visitInsn(ISHR);
     }
     
     public void ishl() {
         getMethodVisitor().visitInsn(ISHL);
     }
     
     public void iushr() {
         getMethodVisitor().visitInsn(IUSHR);
     }
     
     public void lshr() {
         getMethodVisitor().visitInsn(LSHR);
     }
     
     public void lshl() {
         getMethodVisitor().visitInsn(LSHL);
     }
     
     public void lushr() {
         getMethodVisitor().visitInsn(LUSHR);
     }
     
     public void lcmp() {
         getMethodVisitor().visitInsn(LCMP);
     }
     
     public void iand() {
         getMethodVisitor().visitInsn(IAND);
     }
     
     public void ior() {
         getMethodVisitor().visitInsn(IOR);
     }
     
     public void ixor() {
         getMethodVisitor().visitInsn(IXOR);
     }
     
     public void land() {
         getMethodVisitor().visitInsn(LAND);
     }
     
     public void lor() {
         getMethodVisitor().visitInsn(LOR);
     }
     
     public void lxor() {
         getMethodVisitor().visitInsn(LXOR);
     }
     
     public void iadd() {
         getMethodVisitor().visitInsn(IADD);
     }
     
     public void ladd() {
         getMethodVisitor().visitInsn(LADD);
     }
     
     public void fadd() {
         getMethodVisitor().visitInsn(FADD);
     }
     
     public void dadd() {
         getMethodVisitor().visitInsn(DADD);
     }
     
     public void isub() {
         getMethodVisitor().visitInsn(ISUB);
     }
     
     public void lsub() {
         getMethodVisitor().visitInsn(LSUB);
     }
     
     public void fsub() {
         getMethodVisitor().visitInsn(FSUB);
     }
     
     public void dsub() {
         getMethodVisitor().visitInsn(DSUB);
     }
     
     public void idiv() {
         getMethodVisitor().visitInsn(IDIV);
     }
     
     public void irem() {
         getMethodVisitor().visitInsn(IREM);
     }
     
     public void ineg() {
         getMethodVisitor().visitInsn(INEG);
     }
     
     public void i2d() {
         getMethodVisitor().visitInsn(I2D);
     }
     
     public void i2l() {
         getMethodVisitor().visitInsn(I2L);
     }
     
     public void i2f() {
         getMethodVisitor().visitInsn(I2F);
     }
     
     public void i2s() {
         getMethodVisitor().visitInsn(I2S);
     }
     
     public void i2c() {
         getMethodVisitor().visitInsn(I2C);
     }
     
     public void i2b() {
         getMethodVisitor().visitInsn(I2B);
     }
     
     public void ldiv() {
         getMethodVisitor().visitInsn(LDIV);
     }
     
     public void lrem() {
         getMethodVisitor().visitInsn(LREM);
     }
     
     public void lneg() {
         getMethodVisitor().visitInsn(LNEG);
     }
     
     public void l2d() {
         getMethodVisitor().visitInsn(L2D);
     }
     
     public void l2i() {
         getMethodVisitor().visitInsn(L2I);
     }
     
     public void l2f() {
         getMethodVisitor().visitInsn(L2F);
     }
     
     public void fdiv() {
         getMethodVisitor().visitInsn(FDIV);
     }
     
     public void frem() {
         getMethodVisitor().visitInsn(FREM);
     }
     
     public void fneg() {
         getMethodVisitor().visitInsn(FNEG);
     }
     
     public void f2d() {
         getMethodVisitor().visitInsn(F2D);
     }
     
     public void f2i() {
         getMethodVisitor().visitInsn(F2D);
     }
     
     public void f2l() {
         getMethodVisitor().visitInsn(F2L);
     }
     
     public void ddiv() {
         getMethodVisitor().visitInsn(DDIV);
     }
     
     public void drem() {
         getMethodVisitor().visitInsn(DREM);
     }
     
     public void dneg() {
         getMethodVisitor().visitInsn(DNEG);
     }
     
     public void d2f() {
         getMethodVisitor().visitInsn(D2F);
     }
     
     public void d2i() {
         getMethodVisitor().visitInsn(D2I);
     }
     
     public void d2l() {
         getMethodVisitor().visitInsn(D2L);
     }
     
     public void imul() {
         getMethodVisitor().visitInsn(IMUL);
     }
     
     public void lmul() {
         getMethodVisitor().visitInsn(LMUL);
     }
     
     public void fmul() {
         getMethodVisitor().visitInsn(FMUL);
     }
     
     public void dmul() {
         getMethodVisitor().visitInsn(DMUL);
     }
     
     public void iinc(int arg0, int arg1) {
         getMethodVisitor().visitIincInsn(arg0, arg1);
     }
     
     public void monitorenter() {
         getMethodVisitor().visitInsn(MONITORENTER);
     }
     
     public void monitorexit() {
         getMethodVisitor().visitInsn(MONITOREXIT);
     }
     
     public void jsr(Label branch) {
         getMethodVisitor().visitJumpInsn(JSR, branch);
     }
     
     public void ret(int arg0) {
         getMethodVisitor().visitVarInsn(RET, arg0);
     }
     
     public AnnotationVisitor visitAnnotationDefault() {
         return getMethodVisitor().visitAnnotationDefault();
     }
 
     public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
         return getMethodVisitor().visitAnnotation(arg0, arg1);
     }
 
     public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1,
                                                       boolean arg2) {
         return getMethodVisitor().visitParameterAnnotation(arg0, arg1, arg2);
     }
 
     public void visitAttribute(Attribute arg0) {
         getMethodVisitor().visitAttribute(arg0);
     }
 
     public void visitCode() {
         getMethodVisitor().visitCode();
     }
 
     public void visitInsn(int arg0) {
         getMethodVisitor().visitInsn(arg0);
     }
 
     public void visitIntInsn(int arg0, int arg1) {
         getMethodVisitor().visitIntInsn(arg0, arg1);
     }
 
     public void visitVarInsn(int arg0, int arg1) {
         getMethodVisitor().visitVarInsn(arg0, arg1);
     }
 
     public void visitTypeInsn(int arg0, String arg1) {
         getMethodVisitor().visitTypeInsn(arg0, arg1);
     }
 
     public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {
         getMethodVisitor().visitFieldInsn(arg0, arg1, arg2, arg3);
     }
 
     public void visitMethodInsn(int arg0, String arg1, String arg2, String arg3) {
         getMethodVisitor().visitMethodInsn(arg0, arg1, arg2, arg3);
     }
 
     public void visitJumpInsn(int arg0, Label arg1) {
         getMethodVisitor().visitJumpInsn(arg0, arg1);
     }
 
     public void visitLabel(Label arg0) {
         getMethodVisitor().visitLabel(arg0);
     }
 
     public void visitLdcInsn(Object arg0) {
         getMethodVisitor().visitLdcInsn(arg0);
     }
 
     public void visitIincInsn(int arg0, int arg1) {
         getMethodVisitor().visitIincInsn(arg0, arg1);
     }
 
     public void visitTableSwitchInsn(int arg0, int arg1, Label arg2,
                                      Label[] arg3) {
         getMethodVisitor().visitTableSwitchInsn(arg0, arg1, arg2, arg3);
     }
 
     public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
         getMethodVisitor().visitLookupSwitchInsn(arg0, arg1, arg2);
     }
 
     public void visitMultiANewArrayInsn(String arg0, int arg1) {
         getMethodVisitor().visitMultiANewArrayInsn(arg0, arg1);
     }
 
     public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2,
                                    String arg3) {
         getMethodVisitor().visitTryCatchBlock(arg0, arg1, arg2, arg3);
     }
 
     public void visitLocalVariable(String arg0, String arg1, String arg2,
                                    Label arg3, Label arg4, int arg5) {
         getMethodVisitor().visitLocalVariable(arg0, arg1, arg2, arg3, arg4, arg5);
     }
 
     public void visitLineNumber(int arg0, Label arg1) {
         getMethodVisitor().visitLineNumber(arg0, arg1);
     }
 
     public void visitMaxs(int arg0, int arg1) {
         if (DEBUG) {
             PrintWriter pw = new PrintWriter(System.out);
             pw.write("*** Dumping ***\n");
             ((TraceMethodVisitor)getMethodVisitor()).print(pw);
             pw.flush();
         }
         getMethodVisitor().visitMaxs(arg0, arg1);
     }
 
     public void visitEnd() {
         getMethodVisitor().visitEnd();
     }
     
     public void tableswitch(int min, int max, Label defaultLabel, Label[] cases) {
         getMethodVisitor().visitTableSwitchInsn(min, max, defaultLabel, cases);
     }
 
     public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
         getMethodVisitor().visitFrame(arg0, arg1, arg2, arg3, arg4);
     }
 
 }
diff --git a/src/org/jruby/java/MiniJava.java b/src/org/jruby/java/MiniJava.java
index 3fc0b87035..d3cd0ca4c5 100644
--- a/src/org/jruby/java/MiniJava.java
+++ b/src/org/jruby/java/MiniJava.java
@@ -1,1528 +1,1543 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 package org.jruby.java;
 
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.compiler.util.HandleFactory;
 import org.jruby.compiler.util.HandleFactory.Handle;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import static org.jruby.util.CodegenUtils.*;
 import org.jruby.util.IdUtil;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Type;
 import static org.objectweb.asm.Opcodes.*;
 
 /**
  *
  * @author headius
  */
 public class MiniJava implements Library {
     private static final boolean DEBUG = false;
     
     public void load(Ruby runtime, boolean wrap) {
         runtime.getErr().print("Warning: minijava is experimental and subject to change\n");
         
         runtime.getKernel().defineAnnotatedMethods(MiniJava.class);
 
         // load up object and add a few useful methods
         RubyModule javaObject = getMirrorForClass(runtime, Object.class);
 
         javaObject.addMethod("to_s", new JavaMethod.JavaMethodZero(javaObject, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 return context.getRuntime().newString(((JavaObjectWrapper) self).object.toString());
             }
         });
 
         javaObject.addMethod("hash", new JavaMethod.JavaMethodZero(javaObject, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 return self.getRuntime().newFixnum(((JavaObjectWrapper) self).object.hashCode());
             }
         });
 
         javaObject.addMethod("==", new JavaMethod.JavaMethodOne(javaObject, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                 if (arg instanceof JavaObjectWrapper) {
                     return context.getRuntime().newBoolean(((JavaObjectWrapper) self).object.equals(((JavaObjectWrapper) arg).object));
                 } else {
                     return context.getRuntime().getFalse();
                 }
             }
         });
 
         // open up the 'to_java' and 'as' coercion methods on Ruby Objects, via Kernel
         RubyModule rubyKernel = runtime.getKernel();
         rubyKernel.addModuleFunction("to_java", new JavaMethod.JavaMethodZeroOrOne(rubyKernel, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 return ((RubyObject) self).to_java();
             }
 
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                 return ((RubyObject) self).as(getJavaClassFromObject(arg));
             }
         });
     }
     
     @JRubyMethod(name = "new_class", rest = true, module = true)
     public static IRubyObject new_class(ThreadContext context, IRubyObject self, IRubyObject[] interfaces) {
         Class[] javaInterfaces = new Class[interfaces.length];
         for (int i = 0; i < interfaces.length; i++) {
             javaInterfaces[i] = getJavaClassFromObject(interfaces[i]);
         }
         
         return createImplClass(javaInterfaces, context.getRuntime(), "I" + System.currentTimeMillis());
     }
 
     @JRubyMethod(name = "import", module = true)
     public static IRubyObject rb_import(ThreadContext context, IRubyObject self, IRubyObject name) {
         String className = name.toString();
         try {
             Class cls = findClass(context.getRuntime().getJRubyClassLoader(), className);
 
             RubyModule namespace;
             if (self instanceof RubyModule) {
                 namespace = (RubyModule) self;
             } else {
                 namespace = self.getMetaClass().getRealClass();
             }
 
             namespace.defineConstant(cls.getSimpleName(), getMirrorForClass(context.getRuntime(), cls));
 
             return context.getRuntime().getNil();
         } catch (Exception e) {
             if (context.getRuntime().getDebug().isTrue()) {
                 e.printStackTrace();
             }
             throw context.getRuntime().newTypeError("Could not find class " + className + ", exception: " + e);
         }
     }
 
     @JRubyMethod(name = "import", module = true)
     public static IRubyObject rb_import(ThreadContext context, IRubyObject self, IRubyObject name, IRubyObject as) {
         String className = name.toString();
         try {
             Class cls = findClass(context.getRuntime().getJRubyClassLoader(), className);
 
             RubyModule namespace;
             if (self instanceof RubyModule) {
                 namespace = (RubyModule) self;
             } else {
                 namespace = self.getMetaClass().getRealClass();
             }
 
             namespace.defineConstant(as.toString(), getMirrorForClass(context.getRuntime(), cls));
 
             return context.getRuntime().getNil();
         } catch (Exception e) {
             if (context.getRuntime().getDebug().isTrue()) {
                 e.printStackTrace();
             }
             throw context.getRuntime().newTypeError("Could not find class " + className + ", exception: " + e);
         }
     }
 
     public static RubyClass createImplClass(Class[] superTypes, Ruby ruby, String name) {
         String[] superTypeNames = new String[superTypes.length];
         Map<String, List<Method>> simpleToAll = new HashMap<String, List<Method>>();
         for (int i = 0; i < superTypes.length; i++) {
             superTypeNames[i] = p(superTypes[i]);
             
             for (Method method : superTypes[i].getMethods()) {
                 List<Method> methods = simpleToAll.get(method.getName());
                 if (methods == null) simpleToAll.put(method.getName(), methods = new ArrayList<Method>());
                 methods.add(method);
             }
         }
         
         Class newClass = defineImplClass(ruby, name, superTypeNames, simpleToAll);
         RubyClass rubyCls = populateImplClass(ruby, newClass, simpleToAll);
         
         return rubyCls;
     }
 
     public static Class createOldStyleImplClass(Class[] superTypes, RubyClass rubyClass, Ruby ruby, String name) {
         String[] superTypeNames = new String[superTypes.length];
         Map<String, List<Method>> simpleToAll = new HashMap<String, List<Method>>();
         for (int i = 0; i < superTypes.length; i++) {
             superTypeNames[i] = p(superTypes[i]);
             
             for (Method method : superTypes[i].getMethods()) {
                 List<Method> methods = simpleToAll.get(method.getName());
                 if (methods == null) simpleToAll.put(method.getName(), methods = new ArrayList<Method>());
                 methods.add(method);
             }
         }
         
         Class newClass = defineOldStyleImplClass(ruby, name, superTypeNames, simpleToAll);
         populateOldStyleImplClass(ruby, rubyClass, newClass, simpleToAll);
         
         return newClass;
     }
     
     public static Class defineImplClass(Ruby ruby, String name, String[] superTypeNames, Map<String, List<Method>> simpleToAll) {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         
         // construct the class, implementing all supertypes
         cw.visit(V1_5, ACC_PUBLIC | ACC_SUPER, name, null, p(Object.class), superTypeNames);
         
         // fields needed for dispatch and such
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "ruby", ci(Ruby.class), null, null).visitEnd();
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "rubyClass", ci(RubyClass.class), null, null).visitEnd();
         cw.visitField(ACC_PRIVATE | ACC_FINAL, "self", ci(IRubyObject.class), null, null).visitEnd();
         
         // create constructor
         SkinnyMethodAdapter initMethod = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "<init>", sig(void.class), null, null));
         initMethod.aload(0);
         initMethod.invokespecial(p(Object.class), "<init>", sig(void.class));
         
         // wrap self and store the wrapper
         initMethod.aload(0);
         initMethod.getstatic(name, "ruby", ci(Ruby.class));
         initMethod.aload(0);
         initMethod.invokestatic(p(MiniJava.class), "javaToRuby", sig(IRubyObject.class, Ruby.class, Object.class));
         initMethod.putfield(name, "self", ci(IRubyObject.class));
         
         // end constructor
         initMethod.voidreturn();
         initMethod.end();
         
         // start setup method
         SkinnyMethodAdapter setupMethod = new SkinnyMethodAdapter(cw.visitMethod(ACC_STATIC | ACC_PUBLIC | ACC_SYNTHETIC, "__setup__", sig(void.class, RubyClass.class), null, null));
         setupMethod.start();
         
         // set RubyClass
         setupMethod.aload(0);
         setupMethod.dup();
         setupMethod.putstatic(name, "rubyClass", ci(RubyClass.class));
         
         // set Ruby
         setupMethod.invokevirtual(p(RubyClass.class), "getClassRuntime", sig(Ruby.class));
         setupMethod.putstatic(name, "ruby", ci(Ruby.class));
         
         // for each simple method name, implement the complex methods, calling the simple version
         for (Map.Entry<String, List<Method>> entry : simpleToAll.entrySet()) {
             String simpleName = entry.getKey();
             
             // all methods dispatch to the simple version by default, which is method_missing normally
             cw.visitField(ACC_STATIC | ACC_PUBLIC | ACC_VOLATILE, simpleName, ci(DynamicMethod.class), null, null).visitEnd();
             
             for (Method method : entry.getValue()) {
                 Class[] paramTypes = method.getParameterTypes();
                 Class returnType = method.getReturnType();
                 
                 SkinnyMethodAdapter mv = new SkinnyMethodAdapter(
                         cw.visitMethod(ACC_PUBLIC, simpleName, sig(returnType, paramTypes), null, null));
                 mv.start();
 
                 String fieldName = mangleMethodFieldName(simpleName, paramTypes);
                 
                 // try specific name first, falling back on simple name
                 Label dispatch = new Label();
                 cw.visitField(ACC_STATIC | ACC_PUBLIC | ACC_VOLATILE, fieldName, ci(DynamicMethod.class), null, null).visitEnd();
                 mv.getstatic(name, fieldName, ci(DynamicMethod.class));
                 mv.dup();
                 mv.ifnonnull(dispatch);
                 mv.pop();
                 mv.getstatic(name, simpleName, ci(DynamicMethod.class));
                 mv.dup();
                 mv.ifnonnull(dispatch);
                 mv.pop();
                 mv.getstatic(name, "rubyClass", ci(RubyClass.class));
                 mv.ldc("method_missing");
                 mv.invokevirtual(p(RubyClass.class), "searchMethod", sig(DynamicMethod.class, String.class));
                 mv.label(dispatch);
                 
                 // get current context
                 mv.getstatic(name, "ruby", ci(Ruby.class));
                 mv.invokevirtual(p(Ruby.class), "getCurrentContext", sig(ThreadContext.class));
                 
                 // load self, class, and name
                 mv.aload(0);
                 mv.getfield(name, "self", ci(IRubyObject.class));
                 mv.getstatic(name, "rubyClass", ci(RubyClass.class));
                 mv.ldc(simpleName);
                 
                 // load arguments into IRubyObject[] for dispatch
                 if (method.getParameterTypes().length != 0) {
                     mv.pushInt(method.getParameterTypes().length);
                     mv.anewarray(p(IRubyObject.class));
                     
                     for (int i = 0; i < paramTypes.length; i++) {
                         mv.dup();
                         mv.pushInt(i);
                         // convert to IRubyObject
                         mv.getstatic(name, "ruby", ci(Ruby.class));
                         mv.aload(i + 1);
                         mv.invokestatic(p(MiniJava.class), "javaToRuby", sig(IRubyObject.class, Ruby.class, Object.class));
                         mv.aastore();
                     }
                 } else {
                     mv.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
                 }
                 
                 // load null block
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
                 
                 // invoke method
                 mv.invokevirtual(p(DynamicMethod.class), "call", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class, Block.class));
                 
                 // if we expect a return value, unwrap it
                 if (method.getReturnType() != void.class) {
                     mv.invokestatic(p(MiniJava.class), "rubyToJava", sig(Object.class, IRubyObject.class));
                     mv.checkcast(p(returnType));
 
                     mv.areturn();
                 } else {
                     mv.voidreturn();
                 }
 
                 mv.end();
             }
         }
         
         // end setup method
         setupMethod.voidreturn();
         setupMethod.end();
         
         // end class
         cw.visitEnd();
         
         // create the class
         byte[] bytes = cw.toByteArray();
         Class newClass = ruby.getJRubyClassLoader().defineClass(name, cw.toByteArray());
         
         if (DEBUG) {
             FileOutputStream fos = null;
             try {
                 fos = new FileOutputStream(name + ".class");
                 fos.write(bytes);
             } catch (IOException ioe) {
                 ioe.printStackTrace();
             } finally {
                 try {fos.close();} catch (Exception e) {}
             }
         }
         
         return newClass;
     }
     
     /**
      * This variation on defineImplClass uses all the classic type coercion logic
      * for passing args and returning results.
      * 
      * @param ruby
      * @param name
      * @param superTypeNames
      * @param simpleToAll
      * @return
      */
     public static Class defineOldStyleImplClass(Ruby ruby, String name, String[] superTypeNames, Map<String, List<Method>> simpleToAll) {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         String pathName = name.replace('.', '/');
         
         // construct the class, implementing all supertypes
         cw.visit(V1_5, ACC_PUBLIC | ACC_SUPER, pathName, null, p(Object.class), superTypeNames);
+        cw.visitSource(pathName + ".gen", null);
         
         // fields needed for dispatch and such
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "ruby", ci(Ruby.class), null, null).visitEnd();
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "rubyClass", ci(RubyClass.class), null, null).visitEnd();
         cw.visitField(ACC_PRIVATE | ACC_FINAL, "self", ci(IRubyObject.class), null, null).visitEnd();
         
         // create constructor
         SkinnyMethodAdapter initMethod = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "<init>", sig(void.class, IRubyObject.class), null, null));
         initMethod.aload(0);
         initMethod.invokespecial(p(Object.class), "<init>", sig(void.class));
         
         // store the wrapper
         initMethod.aload(0);
         initMethod.aload(1);
         initMethod.putfield(pathName, "self", ci(IRubyObject.class));
         
         // end constructor
         initMethod.voidreturn();
         initMethod.end();
         
         // start setup method
         SkinnyMethodAdapter setupMethod = new SkinnyMethodAdapter(cw.visitMethod(ACC_STATIC | ACC_PUBLIC | ACC_SYNTHETIC, "__setup__", sig(void.class, RubyClass.class), null, null));
         setupMethod.start();
         
         // set RubyClass
         setupMethod.aload(0);
         setupMethod.dup();
         setupMethod.putstatic(pathName, "rubyClass", ci(RubyClass.class));
         
         // set Ruby
         setupMethod.invokevirtual(p(RubyClass.class), "getClassRuntime", sig(Ruby.class));
         setupMethod.putstatic(pathName, "ruby", ci(Ruby.class));
         
         // for each simple method name, implement the complex methods, calling the simple version
         for (Map.Entry<String, List<Method>> entry : simpleToAll.entrySet()) {
             String simpleName = entry.getKey();
             Set<String> nameSet = JavaUtil.getRubyNamesForJavaName(simpleName, entry.getValue());
                 
             // all methods dispatch to the simple version by default, or method_missing if it's not present
             cw.visitField(ACC_STATIC | ACC_PUBLIC | ACC_VOLATILE, simpleName, ci(DynamicMethod.class), null, null).visitEnd();
 
             Set<String> implementedNames = new HashSet<String>();
             
             for (Method method : entry.getValue()) {
                 Class[] paramTypes = method.getParameterTypes();
                 Class returnType = method.getReturnType();
 
                 String fullName = simpleName + prettyParams(paramTypes);
                 if (implementedNames.contains(fullName)) continue;
                 implementedNames.add(fullName);
                 
                 SkinnyMethodAdapter mv = new SkinnyMethodAdapter(
                         cw.visitMethod(ACC_PUBLIC, simpleName, sig(returnType, paramTypes), null, null));
                 mv.start();
+                mv.line(1);
                 
                 // TODO: this code should really check if a Ruby equals method is implemented or not.
                 if(simpleName.equals("equals") && paramTypes.length == 1 && paramTypes[0] == Object.class && returnType == Boolean.TYPE) {
+                    mv.line(2);
                     mv.aload(0);
                     mv.aload(1);
                     mv.invokespecial(p(Object.class), "equals", sig(Boolean.TYPE, params(Object.class)));
                     mv.ireturn();
                 } else if(simpleName.equals("hashCode") && paramTypes.length == 0 && returnType == Integer.TYPE) {
+                    mv.line(3);
                     mv.aload(0);
                     mv.invokespecial(p(Object.class), "hashCode", sig(Integer.TYPE));
                     mv.ireturn();
                 } else if(simpleName.equals("toString") && paramTypes.length == 0 && returnType == String.class) {
+                    mv.line(4);
                     mv.aload(0);
                     mv.invokespecial(p(Object.class), "toString", sig(String.class));
                     mv.areturn();
                 } else {
+                    mv.line(5);
+                    
                     Label dispatch = new Label();
                     Label end = new Label();
                     Label recheckMethod = new Label();
 
                     // Try to look up field for simple name
 
                     // get field; if nonnull, go straight to dispatch
                     mv.getstatic(pathName, simpleName, ci(DynamicMethod.class));
                     mv.dup();
                     mv.ifnonnull(dispatch);
 
                     // field is null, lock class and try to populate
+                    mv.line(6);
                     mv.pop();
                     mv.getstatic(pathName, "rubyClass", ci(RubyClass.class));
                     mv.monitorenter();
 
                     // try/finally block to ensure unlock
                     Label tryStart = new Label();
                     Label tryEnd = new Label();
                     Label finallyStart = new Label();
                     Label finallyEnd = new Label();
+                    mv.line(7);
                     mv.label(tryStart);
 
                     mv.aload(0);
                     mv.getfield(pathName, "self", ci(IRubyObject.class));
                     for (String eachName : nameSet) {
                         mv.ldc(eachName);
                     }
                     mv.invokestatic(p(MiniJava.class), "searchMethod", sig(DynamicMethod.class, params(IRubyObject.class, String.class, nameSet.size())));
                     mv.dup();
                 
                     // if it's not undefined...
                     mv.getstatic(p(UndefinedMethod.class), "INSTANCE", ci(UndefinedMethod.class));
                     Label noStore = new Label();
                     mv.if_acmpeq(noStore);
 
                     // store it
+                    mv.line(8);
                     mv.dup();
                     mv.putstatic(pathName, simpleName, ci(DynamicMethod.class));
 
                     // all done with lookup attempts, pop any result and release monitor
                     mv.label(noStore);
                     mv.pop();
                     mv.getstatic(pathName, "rubyClass", ci(RubyClass.class));
                     mv.monitorexit();
                     mv.go_to(recheckMethod);
 
                     // end of try block
                     mv.label(tryEnd);
 
                     // finally block to release monitor
                     mv.label(finallyStart);
+                    mv.line(9);
                     mv.getstatic(pathName, "rubyClass", ci(RubyClass.class));
                     mv.monitorexit();
                     mv.label(finallyEnd);
                     mv.athrow();
 
                     // exception handling for monitor release
                     mv.trycatch(tryStart, tryEnd, finallyStart, null);
                     mv.trycatch(finallyStart, finallyEnd, finallyStart, null);
 
                     // re-get, re-check method; if not null now, go to dispatch
                     mv.label(recheckMethod);
+                    mv.line(10);
                     mv.getstatic(pathName, simpleName, ci(DynamicMethod.class));
                     mv.dup();
                     mv.ifnonnull(dispatch);
 
                     // method still not available, call method_missing
+                    mv.line(11);
                     mv.pop();
                     // exit monitor before making call
                     // FIXME: this not being in a finally is a little worrisome
                     mv.aload(0);
                     mv.getfield(pathName, "self", ci(IRubyObject.class));
                     mv.ldc(simpleName);
                     coerceArgumentsToRuby(mv, paramTypes, pathName);
                     mv.invokestatic(p(RuntimeHelpers.class), "invokeMethodMissing", sig(IRubyObject.class, IRubyObject.class, String.class, IRubyObject[].class));
                     mv.go_to(end);
                 
                     // perform the dispatch
                     mv.label(dispatch);
+                    mv.line(12, dispatch);
                     // get current context
                     mv.getstatic(pathName, "ruby", ci(Ruby.class));
                     mv.invokevirtual(p(Ruby.class), "getCurrentContext", sig(ThreadContext.class));
                 
                     // load self, class, and name
                     mv.aload(0);
                     mv.getfield(pathName, "self", ci(IRubyObject.class));
                     mv.getstatic(pathName, "rubyClass", ci(RubyClass.class));
                     mv.ldc(simpleName);
                 
                     // coerce arguments
                     coerceArgumentsToRuby(mv, paramTypes, pathName);
                 
                     // load null block
                     mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
                 
                     // invoke method
+                    mv.line(13);
                     mv.invokevirtual(p(DynamicMethod.class), "call", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class, Block.class));
                 
                     mv.label(end);
                     coerceResultAndReturn(method, mv, returnType);
                 }                
                 mv.end();
             }
         }
         
         // end setup method
         setupMethod.voidreturn();
         setupMethod.end();
         
         // end class
         cw.visitEnd();
         
         // create the class
         byte[] bytes = cw.toByteArray();
         Class newClass;
         synchronized (ruby.getJRubyClassLoader()) {
             // try to load the specified name; only if that fails, try to define the class
             try {
                 newClass = ruby.getJRubyClassLoader().loadClass(name);
             } catch (ClassNotFoundException cnfe) {
                 newClass = ruby.getJRubyClassLoader().defineClass(name, cw.toByteArray());
             }
         }
         
         if (DEBUG) {
             FileOutputStream fos = null;
             try {
                 fos = new FileOutputStream(name + ".class");
                 fos.write(bytes);
             } catch (IOException ioe) {
                 ioe.printStackTrace();
             } finally {
                 try {fos.close();} catch (Exception e) {}
             }
         }
         
         return newClass;
     }
 
     private static void coerceArgumentsToRuby(SkinnyMethodAdapter mv, Class[] paramTypes, String name) {
         // load arguments into IRubyObject[] for dispatch
         if (paramTypes.length != 0) {
             mv.pushInt(paramTypes.length);
             mv.anewarray(p(IRubyObject.class));
 
             // TODO: make this do specific-arity calling
             for (int i = 0, argIndex = 1; i < paramTypes.length; i++) {
                 Class paramType = paramTypes[i];
                 mv.dup();
                 mv.pushInt(i);
                 // convert to IRubyObject
                 mv.getstatic(name, "ruby", ci(Ruby.class));
                 if (paramTypes[i].isPrimitive()) {
                     if (paramType == byte.class || paramType == short.class || paramType == char.class || paramType == int.class) {
                         mv.iload(argIndex++);
                         mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, int.class));
                     } else if (paramType == long.class) {
                         mv.lload(argIndex);
                         argIndex += 2; // up two slots, for long's two halves
                         mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, long.class));
                     } else if (paramType == float.class) {
                         mv.fload(argIndex++);
                         mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, float.class));
                     } else if (paramType == double.class) {
                         mv.dload(argIndex);
                         argIndex += 2; // up two slots, for long's two halves
                         mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, double.class));
                     } else if (paramType == boolean.class) {
                         mv.iload(argIndex++);
                         mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, boolean.class));
                     }
                 } else {
                     mv.aload(argIndex++);
                     mv.invokestatic(p(JavaUtil.class), "convertJavaToUsableRubyObject", sig(IRubyObject.class, Ruby.class, Object.class));
                 }
                 mv.aastore();
             }
         } else {
             mv.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
         }
     }
 
     private static void coerceResultAndReturn(Method method, SkinnyMethodAdapter mv, Class returnType) {
         // if we expect a return value, unwrap it
         if (method.getReturnType() != void.class) {
             if (method.getReturnType().isPrimitive()) {
                 if (method.getReturnType() == byte.class) {
                     mv.invokestatic(p(JavaUtil.class), "convertRubyToJavaByte", sig(byte.class, IRubyObject.class));
                     mv.ireturn();
                 } else if (method.getReturnType() == short.class) {
                     mv.invokestatic(p(JavaUtil.class), "convertRubyToJavaShort", sig(short.class, IRubyObject.class));
                     mv.ireturn();
                 } else if (method.getReturnType() == char.class) {
                     mv.invokestatic(p(JavaUtil.class), "convertRubyToJavaChar", sig(char.class, IRubyObject.class));
                     mv.ireturn();
                 } else if (method.getReturnType() == int.class) {
                     mv.invokestatic(p(JavaUtil.class), "convertRubyToJavaInt", sig(int.class, IRubyObject.class));
                     mv.ireturn();
                 } else if (method.getReturnType() == long.class) {
                     mv.invokestatic(p(JavaUtil.class), "convertRubyToJavaLong", sig(long.class, IRubyObject.class));
                     mv.lreturn();
                 } else if (method.getReturnType() == float.class) {
                     mv.invokestatic(p(JavaUtil.class), "convertRubyToJavaFloat", sig(float.class, IRubyObject.class));
                     mv.freturn();
                 } else if (method.getReturnType() == double.class) {
                     mv.invokestatic(p(JavaUtil.class), "convertRubyToJavaDouble", sig(double.class, IRubyObject.class));
                     mv.dreturn();
                 } else if (method.getReturnType() == boolean.class) {
                     mv.invokestatic(p(JavaUtil.class), "convertRubyToJavaBoolean", sig(boolean.class, IRubyObject.class));
                     mv.ireturn();
                 }
             } else {
                 mv.ldc(Type.getType(method.getReturnType()));
                 mv.invokestatic(p(JavaUtil.class), "convertRubyToJava", sig(Object.class, IRubyObject.class, Class.class));
                 mv.checkcast(p(returnType));
 
                 mv.areturn();
             }
         } else {
             mv.voidreturn();
         }
     }
 
     public static RubyClass populateImplClass(Ruby ruby, Class newClass, Map<String, List<Method>> simpleToAll) {
         RubyClass rubyCls = (RubyClass)getMirrorForClass(ruby, newClass);
         
         // setup the class
         try {
             newClass.getMethod("__setup__", new Class[]{RubyClass.class}).invoke(null, new Object[]{rubyCls});
         } catch (IllegalAccessException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         } catch (IllegalArgumentException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         } catch (InvocationTargetException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         } catch (NoSuchMethodException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         }
 
         // now, create a method_added that can replace the DynamicMethod fields as they're redefined
         final Map<String, Field> allFields = new HashMap<String, Field>();
         try {
             for (Map.Entry<String, List<Method>> entry : simpleToAll.entrySet()) {
                 String simpleName = entry.getKey();
 
                 Field simpleField = newClass.getField(simpleName);
                 allFields.put(simpleName, simpleField);
 
                 for (Method method : entry.getValue()) {
                     String complexName = simpleName + prettyParams(method.getParameterTypes());
                     String fieldName = mangleMethodFieldName(simpleName, method.getParameterTypes());
                     allFields.put(complexName, newClass.getField(fieldName));
                 }
             }
         } catch (IllegalArgumentException ex) {
             throw error(ruby, ex, "Could not prepare method fields: " + newClass);
         } catch (NoSuchFieldException ex) {
             throw error(ruby, ex, "Could not prepare method fields: " + newClass);
         }
 
         DynamicMethod method_added = new JavaMethod(rubyCls.getSingletonClass(), Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 RubyClass selfClass = (RubyClass)self;
                 Ruby ruby = selfClass.getClassRuntime();
                 String methodName = args[0].asJavaString();
                 Field field = allFields.get(methodName);
 
                 if (field == null) {
                     // do nothing, it's a non-impl method
                 } else {
                     try {
                         field.set(null, selfClass.searchMethod(methodName));
                     } catch (IllegalAccessException iae) {
                         throw error(ruby, iae, "Could not set new method into field: " + selfClass + "." + methodName);
                     } catch (IllegalArgumentException iae) {
                         throw error(ruby, iae, "Could not set new method into field: " + selfClass + "." + methodName);
                     }
                 }
 
                 return context.getRuntime().getNil();
             }
         };
         rubyCls.getSingletonClass().addMethod("method_added", method_added);
         
         return rubyCls;
     }
 
     public static void populateOldStyleImplClass(Ruby ruby, RubyClass rubyCls, final Class newClass, Map<String, List<Method>> simpleToAll) {
         // setup the class
         try {
             newClass.getMethod("__setup__", new Class[]{RubyClass.class}).invoke(null, rubyCls);
         } catch (IllegalAccessException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         } catch (IllegalArgumentException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         } catch (InvocationTargetException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         } catch (NoSuchMethodException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         }
 
         // now, create a method_added that can replace the DynamicMethod fields as they're redefined
         final Map<String, Field> allFields = new HashMap<String, Field>();
         try {
             for (Map.Entry<String, List<Method>> entry : simpleToAll.entrySet()) {
                 String simpleName = entry.getKey();
                 Field simpleField = newClass.getField(simpleName);
                 Set<String> nameSet = JavaUtil.getRubyNamesForJavaName(simpleName, entry.getValue());
                 
                 for (String name : nameSet) {
                     allFields.put(name, simpleField);
                 }
             }
         } catch (IllegalArgumentException ex) {
             throw error(ruby, ex, "Could not prepare method fields: " + newClass);
         } catch (NoSuchFieldException ex) {
             throw error(ruby, ex, "Could not prepare method fields: " + newClass);
         }
 
         DynamicMethod method_added = new JavaMethod(rubyCls.getSingletonClass(), Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 RubyClass selfClass = (RubyClass)self;
                 Ruby ruby = selfClass.getClassRuntime();
                 String methodName = args[0].asJavaString();
                 Field field = allFields.get(methodName);
 
                 if (field == null) {
                     // do nothing, it's a non-impl method
                 } else {
                     try {
                         synchronized (self) {
                             DynamicMethod method = selfClass.searchMethod(methodName);
                             if (method != UndefinedMethod.INSTANCE) {
                                 field.set(null, method);
                             }
                         }
                     } catch (IllegalAccessException iae) {
                         throw error(ruby, iae, "Could not set new method into field: " + selfClass + "." + methodName);
                     } catch (IllegalArgumentException iae) {
                         throw error(ruby, iae, "Could not set new method into field: " + selfClass + "." + methodName);
                     }
                 }
 
                 return context.getRuntime().getNil();
             }
         };
         rubyCls.getSingletonClass().addMethod("method_added", method_added);
     }
     
     protected static String mangleMethodFieldName(String baseName, Class[] paramTypes) {
         String fieldName = baseName + prettyParams(paramTypes);
         fieldName = fieldName.replace('.', '\\');
         
         return fieldName;
     }
     
     protected static Class findClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
         if (className.indexOf('.') == -1 && Character.isLowerCase(className.charAt(0))) {
             // probably a primitive
             switch (className.charAt(0)) {
             case 'b':
                 return byte.class;
             case 's':
                 return short.class;
             case 'c':
                 return char.class;
             case 'i':
                 return int.class;
             case 'l':
                 return long.class;
             case 'f':
                 return float.class;
             case 'd':
                 return double.class;
             default:
                 return classLoader.loadClass(className);
             }
         } else {
             return classLoader.loadClass(className);
         }
     }
     static Map<Class, RubyModule> classMap = new HashMap<Class, RubyModule>();
 
     public static RubyModule getMirrorForClass(Ruby ruby, Class cls) {
         if (cls == null) {
             return ruby.getObject();
         }
 
         RubyModule rubyCls = classMap.get(cls);
 
         if (rubyCls == null) {
             rubyCls = createMirrorForClass(ruby, cls);
 
             classMap.put(cls, rubyCls);
             populateMirrorForClass(rubyCls, cls);
             rubyCls = classMap.get(cls);
         }
 
         return rubyCls;
     }
 
     protected static RubyModule createMirrorForClass(Ruby ruby, Class cls) {
         if (cls.isInterface()) {
             // interfaces are handled as modules
             RubyModule rubyMod = RubyModule.newModule(ruby);
             return rubyMod;
         } else {
             // construct the mirror class and parent classes
             RubyClass rubyCls = RubyClass.newClass(ruby, (RubyClass) getMirrorForClass(ruby, cls.getSuperclass()));
             return rubyCls;
         }
     }
 
     protected static void populateMirrorForClass(RubyModule rubyMod, final Class cls) {
         Ruby ruby = rubyMod.getRuntime();
 
         // set the full name
         rubyMod.setBaseName(cls.getCanonicalName());
 
         // include all interfaces
         Class[] interfaces = cls.getInterfaces();
         for (Class ifc : interfaces) {
             rubyMod.includeModule(getMirrorForClass(ruby, ifc));
         }
 
         // if it's an inner class and it's not public, we can't access it;
         // skip population of declared elements
         if (cls.getEnclosingClass() != null && !Modifier.isPublic(cls.getModifiers())) {
             return;
         }
 
         RubyModule rubySing = rubyMod.getSingletonClass();
 
         // if it's an array, only add methods for aref, aset, and length
         if (cls.isArray()) {
             populateMirrorForArrayClass(rubyMod, cls);
         } else {
             populateDeclaredMethods(rubyMod, cls, true);
 
             populateConstructors(rubySing, cls);
 
             populateArrayConstructors(rubySing);
 
             populateFields(rubyMod, cls);
         }
 
         populateSpecialMethods(rubyMod, rubySing, cls);
     }
     private static void populateArrayConstructors(RubyModule rubySing) {
         // add array construction methods
         rubySing.addMethod("[]", new JavaMethod.JavaMethodOneOrTwoOrThree(rubySing, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                 Class javaClass = getJavaClassFromObject(self);
                 int size = RubyFixnum.fix2int(arg.convertToInteger());
                 return javaToRuby(context.getRuntime(), Array.newInstance(javaClass, size));
             }
 
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
                 Class javaClass = getJavaClassFromObject(self);
                 int x = RubyFixnum.fix2int(arg0.convertToInteger());
                 int y = RubyFixnum.fix2int(arg1.convertToInteger());
                 return javaToRuby(context.getRuntime(), Array.newInstance(javaClass, new int[]{x, y}));
             }
 
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
                 Class javaClass = getJavaClassFromObject(self);
                 int x = RubyFixnum.fix2int(arg0.convertToInteger());
                 int y = RubyFixnum.fix2int(arg1.convertToInteger());
                 int z = RubyFixnum.fix2int(arg2.convertToInteger());
                 return javaToRuby(context.getRuntime(), Array.newInstance(javaClass, new int[]{x, y, z}));
             }
         });
     }
 
     private static void populateConstructors(RubyModule rubySing, final Class cls) {
         final Ruby ruby = rubySing.getRuntime();
         
         // add all public constructors (note: getConstructors only returns public ones)
         Constructor[] constructors = cls.getConstructors();
         for (final Constructor constructor : constructors) {
             DynamicMethod dynMethod;
             if (constructor.getParameterTypes().length == 0) {
                 dynMethod = new JavaMethod.JavaMethodZero(rubySing, Visibility.PUBLIC) {
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                         try {
                             return javaToRuby(context.getRuntime(), constructor.newInstance());
                         } catch (InstantiationException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (IllegalAccessException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (IllegalArgumentException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (InvocationTargetException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         }
                     }
                 };
             } else {
                 dynMethod = new JavaMethod.JavaMethodN(rubySing, Visibility.PUBLIC) {
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] rubyArgs) {
                         Object[] args = new Object[rubyArgs.length];
 
                         for (int i = 0; i < args.length; i++) {
                             args[i] = rubyToJava(rubyArgs[i]);
                         }
                         try {
                             return javaToRuby(ruby, constructor.newInstance(args));
                         } catch (InstantiationException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (IllegalAccessException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (IllegalArgumentException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (InvocationTargetException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         }
                     }
                 };
             }
 
             // if not already defined, we add a 'new' that guesses at which signature to use
             // TODO: just adding first one right now...add in signature-guessing logic
             if (rubySing.getMethods().get("new") == null) {
                 rubySing.addMethod("new", dynMethod);
             }
 
             // add 'new' with full signature, so it's guaranteed to be directly accessible
             // TODO: no need for this to be a full, formal JVM signature
             rubySing.addMethod("new" + prettyParams(constructor.getParameterTypes()), dynMethod);
         }
     }
 
     private static void populateDeclaredMethods(RubyModule rubyMod, final Class cls, boolean includeStatic) throws SecurityException {
         // add all instance and static methods
         Method[] methods = cls.getDeclaredMethods();
         for (final Method method : methods) {
             String name = method.getName();
             RubyModule target;
 
             // only public methods
             if (!Modifier.isPublic(method.getModifiers())) {
                 continue;
             }
 
             if (Modifier.isStatic(method.getModifiers())) {
                 if (!includeStatic) continue; // only include static methods if specified
                 
                 target = rubyMod.getSingletonClass();
             } else {
                 target = rubyMod;
             }
 
             JavaMethodFactory factory = getMethodFactory(method.getReturnType());
             DynamicMethod dynMethod = factory.createMethod(target, method);
 
             // if not overloaded, we add a method that guesses at which signature to use
             // TODO: just adding first one right now...add in signature-guessing logic
             if (target.getMethods().get(name) == null) {
                 target.addMethod(name, dynMethod);
             }
 
             // add method with full signature, so it's guaranteed to be directly accessible
             // TODO: no need for this to be a full, formal JVM signature
             name = name + prettyParams(method.getParameterTypes());
             target.addMethod(name, dynMethod);
         }
     }
     
     private static void populateSpecialMethods(RubyModule rubyMod, RubyModule rubySing, final Class cls) {
         final Ruby ruby = rubyMod.getRuntime();
         
         // add a few type-specific special methods
         rubySing.addMethod("java_class", new JavaMethod.JavaMethodZero(rubySing, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 return javaToRuby(ruby, cls);
             }
         });
     }
 
     private static void populateFields(RubyModule rubyMod, final Class cls) throws RaiseException, SecurityException {
         Ruby ruby = rubyMod.getRuntime();
         
         // add all static variables
         Field[] fields = cls.getDeclaredFields();
         for (Field field : fields) {
             // only public static fields that are valid constants
             if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && IdUtil.isConstant(field.getName())) {
                 Object value = null;
                 try {
                     value = field.get(null);
                 } catch (Exception e) {
                     throw ruby.newTypeError("Could not access field " + cls.getCanonicalName() + "::" + field.getName() + " using " + ci(field.getType()));
                 }
                 rubyMod.defineConstant(field.getName(), new JavaObjectWrapper((RubyClass) getMirrorForClass(ruby, value.getClass()), value));
             }
         }
     }
 
     protected static void populateMirrorForArrayClass(RubyModule rubyMod, Class cls) {
         final Ruby ruby = rubyMod.getRuntime();
         
         rubyMod.addMethod("[]", new JavaMethod.JavaMethodOneOrTwo(rubyMod, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                 Object array = rubyToJava(self);
                 int x = RubyFixnum.fix2int(arg.convertToInteger());
                 return javaToRuby(ruby, Array.get(array, x));
             }
 
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
                 Object array = rubyToJava(self);
                 int x = RubyFixnum.fix2int(arg0.convertToInteger());
                 int y = RubyFixnum.fix2int(arg1.convertToInteger());
                 return javaToRuby(ruby, Array.get(Array.get(array, x), y));
             }
         });
 
         rubyMod.addMethod("[]=", new JavaMethod.JavaMethodTwoOrThree(rubyMod, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
                 Object array = rubyToJava(self);
                 int x = RubyFixnum.fix2int(arg0.convertToInteger());
                 Object obj = rubyToJava(arg1);
                 Array.set(array, x, obj);
 
                 return arg1;
             }
 
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
                 Object array = rubyToJava(self);
                 int x = RubyFixnum.fix2int(arg0.convertToInteger());
                 int y = RubyFixnum.fix2int(arg1.convertToInteger());
                 Object obj = rubyToJava(arg2);
                 Array.set(Array.get(array, x), y, obj);
 
                 return arg2;
             }
         });
 
         rubyMod.addMethod("length", new JavaMethod.JavaMethodZero(rubyMod, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 Object array = rubyToJava(self);
                 return javaToRuby(ruby, Array.getLength(array));
             }
         });
     }
     static final Map<Class, JavaMethodFactory> methodFactories = new HashMap();
     static final JavaMethodFactory JAVA_OBJECT_METHOD_FACTORY = new JavaMethodFactory() {
         public DynamicMethod createMethod(RubyClass klazz, Method method) {
             return new JavaObjectWrapperMethod(klazz, method);
         }
     };
 
     protected static JavaMethodFactory getMethodFactory(Class returnType) {
         JavaMethodFactory factory = methodFactories.get(returnType);
 
         if (factory == null) {
             return JAVA_OBJECT_METHOD_FACTORY;
         }
 
         return factory;
     }
     
 
     static {
         methodFactories.put(void.class, new JavaMethodFactory() {
             @Override
             public DynamicMethod createMethod(RubyModule klazz, Method method) {
                 Class[] parameters = method.getParameterTypes();
                 if (parameters.length > 0) {
                     return new JavaVoidWrapperMethod(klazz, method);
                 } else {
                     return new JavaVoidWrapperMethodZero(klazz, method);
                 }
             }
         });
     }
 
     public static class JavaMethodFactory {
         public DynamicMethod createMethod(RubyModule klazz, Method method) {
             Class[] params = method.getParameterTypes();
             if (params.length > 0) {
                 return new JavaObjectWrapperMethod(klazz, method);
             } else {
                 return new JavaObjectWrapperMethodZero(klazz, method);
             }
         }
     }
 
     public static abstract class AbstractJavaWrapperMethodZero extends JavaMethod.JavaMethodZero {
         protected final Handle handle;
         protected final boolean isStatic;
         protected final String className;
         protected final String methodName;
         protected final String prettySig;
         protected final Ruby ruby;
 
         public AbstractJavaWrapperMethodZero(RubyModule klazz, Method method) {
             super(klazz, Visibility.PUBLIC);
 
             this.handle = HandleFactory.createHandle(klazz.getRuntime().getJRubyClassLoader(), method);
             this.isStatic = Modifier.isStatic(method.getModifiers());
             this.className = method.getDeclaringClass().getCanonicalName();
             this.methodName = method.getName();
             this.prettySig = prettyParams(method.getParameterTypes());
             this.ruby = klazz.getRuntime();
         }
 
         protected RaiseException error(ThreadContext context, Exception e) throws RaiseException {
             if (ruby.getDebug().isTrue()) {
                 e.printStackTrace();
             }
             throw ruby.newTypeError("Could not dispatch to " + className + "#" + methodName + " using " + prettySig);
         }
     }
 
     public static abstract class AbstractJavaWrapperMethod extends JavaMethod {
         protected final Handle handle;
         protected final boolean isStatic;
         protected final String className;
         protected final String methodName;
         protected final String prettySig;
         protected final Ruby ruby;
 
         public AbstractJavaWrapperMethod(RubyModule klazz, Method method) {
             super(klazz, Visibility.PUBLIC);
 
             this.handle = HandleFactory.createHandle(klazz.getRuntime().getJRubyClassLoader(), method);
             this.isStatic = Modifier.isStatic(method.getModifiers());
             this.className = method.getDeclaringClass().getCanonicalName();
             this.methodName = method.getName();
             this.prettySig = prettyParams(method.getParameterTypes());
             this.ruby = klazz.getRuntime();
         }
 
         protected RaiseException error(Exception e) throws RaiseException {
             return MiniJava.error(ruby, e, "Could not dispatch to " + className + "#" + methodName + " using " + prettySig);
         }
     }
     
     protected static RaiseException error(Ruby ruby, Exception e, String message) throws RaiseException {
         if (ruby.getDebug().isTrue()) {
             e.printStackTrace();
         }
         throw ruby.newTypeError(message);
     }
 
     protected static class JavaObjectWrapperMethodZero extends AbstractJavaWrapperMethodZero {
         public JavaObjectWrapperMethodZero(RubyModule klazz, Method method) {
             super(klazz, method);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return javaToRuby(ruby, result);
         }
     }
 
     protected static class JavaObjectWrapperMethod extends AbstractJavaWrapperMethod {
         public JavaObjectWrapperMethod(RubyModule klazz, Method method) {
             super(klazz, method);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             Object[] newArgs = new Object[args.length];
             for (int i = 0; i < args.length; i++) {
                 IRubyObject arg = args[i];
                 newArgs[i] = rubyToJava(arg);
             }
 
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, newArgs);
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return javaToRuby(ruby, result);
     }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0));
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1));
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1), rubyToJava(arg2));
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             Object[] newArgs = new Object[args.length];
             for (int i = 0; i < args.length; i++) {
                 IRubyObject arg = args[i];
                 newArgs[i] = rubyToJava(arg);
             }
 
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, newArgs);
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0));
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1));
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1), rubyToJava(arg2));
 
             return javaToRuby(ruby, result);
         }
     }
 
     protected static class JavaVoidWrapperMethod extends AbstractJavaWrapperMethod {
         public JavaVoidWrapperMethod(RubyModule klazz, Method method) {
             super(klazz, method);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             Object[] newArgs = new Object[args.length];
             for (int i = 0; i < args.length; i++) {
                 IRubyObject arg = args[i];
                 newArgs[i] = rubyToJava(arg);
             }
 
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, newArgs);
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0));
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1));
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1), rubyToJava(arg2));
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             Object[] newArgs = new Object[args.length];
             for (int i = 0; i < args.length; i++) {
                 IRubyObject arg = args[i];
                 newArgs[i] = rubyToJava(arg);
             }
 
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, newArgs);
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0));
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1));
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1), rubyToJava(arg2));
 
             return self;
         }
     }
 
     protected static class JavaVoidWrapperMethodZero extends AbstractJavaWrapperMethodZero {
         public JavaVoidWrapperMethodZero(RubyModule klazz, Method method) {
             super(klazz, method);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return self;
         }
     }
 
     public static Object rubyToJava(IRubyObject object) {
         if (object.isNil()) {
             return null;
         } else if (object instanceof JavaObjectWrapper) {
             return ((JavaObjectWrapper) object).object;
         } else {
             return object;
         }
     }
 
     public static IRubyObject javaToRuby(Ruby ruby, Object object) {
         if (object == null) {
             return ruby.getNil();
         } else if (object instanceof IRubyObject) {
             return (IRubyObject) object;
         } else {
             return new JavaObjectWrapper((RubyClass) getMirrorForClass(ruby, object.getClass()), object);
         }
     }
 
     public static class JavaObjectWrapper extends RubyObject {
         Object object;
 
         public JavaObjectWrapper(RubyClass klazz, Object object) {
             super(klazz.getRuntime(), klazz);
             this.object = object;
         }
     };
 
     public static Class getJavaClassFromObject(IRubyObject obj) {
         if (!obj.respondsTo("java_class")) {
             throw obj.getRuntime().newTypeError(obj.getMetaClass().getBaseName() + " is not a Java type");
         } else {
             return (Class) rubyToJava(obj.callMethod(obj.getRuntime().getCurrentContext(), "java_class"));
         }
     }
     
     public static DynamicMethod searchMethod(RubyClass clazz, String name1) {
         return clazz.searchMethod(name1);
     }
     
     public static DynamicMethod searchMethod(RubyClass clazz, String name1, String name2) {
         DynamicMethod method = clazz.searchMethod(name1);
         if (method == UndefinedMethod.INSTANCE) {
             return searchMethod(clazz, name2);
         }
         return method;
     }
     
     public static DynamicMethod searchMethod(RubyClass clazz, String name1, String name2, String name3) {
         DynamicMethod method = clazz.searchMethod(name1);
         if (method == UndefinedMethod.INSTANCE) {
             return searchMethod(clazz, name2, name3);
         }
         return method;
     }
     
     public static DynamicMethod searchMethod(RubyClass clazz, String name1, String name2, String name3, String name4) {
         DynamicMethod method = clazz.searchMethod(name1);
         if (method == UndefinedMethod.INSTANCE) {
             return searchMethod(clazz, name2, name3, name4);
         }
         return method;
     }
     
     public static DynamicMethod searchMethod(RubyClass clazz, String name1, String name2, String name3, String name4, String name5) {
         DynamicMethod method = clazz.searchMethod(name1);
         if (method == UndefinedMethod.INSTANCE) {
             return searchMethod(clazz, name2, name3, name4, name5);
         }
         return method;
     }
     
     public static DynamicMethod searchMethod(RubyClass clazz, String name1, String name2, String name3, String name4, String name5, String name6) {
         DynamicMethod method = clazz.searchMethod(name1);
         if (method == UndefinedMethod.INSTANCE) {
             return searchMethod(clazz, name2, name3, name4, name5, name6);
         }
         return method;
     }
     
     public static DynamicMethod searchMethod(RubyClass clazz, String name1, String name2, String name3, String name4, String name5, String name6, String name7) {
         DynamicMethod method = clazz.searchMethod(name1);
         if (method == UndefinedMethod.INSTANCE) {
             return searchMethod(clazz, name2, name3, name4, name5, name6, name7);
         }
         return method;
     }
     
     public static DynamicMethod searchMethod(RubyClass clazz, String name1, String name2, String name3, String name4, String name5, String name6, String name7, String name8) {
         DynamicMethod method = clazz.searchMethod(name1);
         if (method == UndefinedMethod.INSTANCE) {
             return searchMethod(clazz, name2, name3, name4, name5, name6, name7, name8);
         }
         return method;
     }
 
     public static DynamicMethod searchMethod(IRubyObject obj, String name1) {
         return searchMethod(obj.getMetaClass(), name1);
     }
     
     public static DynamicMethod searchMethod(IRubyObject obj, String name1, String name2) {
         return searchMethod(obj.getMetaClass(), name1, name2);
     }
     
     public static DynamicMethod searchMethod(IRubyObject obj, String name1, String name2, String name3) {
         return searchMethod(obj.getMetaClass(), name1, name2, name3);
     }
     
     public static DynamicMethod searchMethod(IRubyObject obj, String name1, String name2, String name3, String name4) {
         return searchMethod(obj.getMetaClass(), name1, name2, name3, name4);
     }
     
     public static DynamicMethod searchMethod(IRubyObject obj, String name1, String name2, String name3, String name4, String name5) {
         return searchMethod(obj.getMetaClass(), name1, name2, name3, name4, name5);
     }
     
     public static DynamicMethod searchMethod(IRubyObject obj, String name1, String name2, String name3, String name4, String name5, String name6) {
         return searchMethod(obj.getMetaClass(), name1, name2, name3, name4, name5, name6);
     }
     
     public static DynamicMethod searchMethod(IRubyObject obj, String name1, String name2, String name3, String name4, String name5, String name6, String name7) {
         return searchMethod(obj.getMetaClass(), name1, name2, name3, name4, name5, name6, name7);
     }
     
     public static DynamicMethod searchMethod(IRubyObject obj, String name1, String name2, String name3, String name4, String name5, String name6, String name7, String name8) {
         return searchMethod(obj.getMetaClass(), name1, name2, name3, name4, name5, name6, name7, name8);
     }