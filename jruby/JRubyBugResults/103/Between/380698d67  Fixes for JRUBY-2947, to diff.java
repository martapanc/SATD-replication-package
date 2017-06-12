diff --git a/spec/java_integration/types/array_spec.rb b/spec/java_integration/types/array_spec.rb
index 8af9b56a8a..c1c0d96a02 100644
--- a/spec/java_integration/types/array_spec.rb
+++ b/spec/java_integration/types/array_spec.rb
@@ -1,759 +1,778 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 import "java_integration.fixtures.ArrayReceiver"
 import "java_integration.fixtures.ArrayReturningInterface"
 import "java_integration.fixtures.ArrayReturningInterfaceConsumer"
 
 describe "A Java primitive Array of type" do
   describe "boolean" do 
     it "should be possible to create empty array" do 
       arr = Java::boolean[0].new
       arr.java_class.to_s.should == "[Z"
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::boolean[10].new
       arr.java_class.to_s.should == "[Z"
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::boolean[10,10].new
       arr.java_class.to_s.should == "[[Z"
     end
 
     it "should be possible to create primitive array from Ruby array" do 
       # Check with symbol name
       arr = [true, false].to_java :boolean
       arr.java_class.to_s.should == "[Z"
 
       arr.length.should == 2
 
       arr[0].should be_true
       arr[1].should be_false
 
 
       # Check with type
       arr = [true, false].to_java Java::boolean
       arr.java_class.to_s.should == "[Z"
 
       arr.length.should == 2
 
       arr[0].should be_true
       arr[1].should be_false
     end
     
     it "should be possible to set values in primitive array" do 
       arr = Java::boolean[5].new
       arr[3] = true
       
       arr[0].should be_false
       arr[1].should be_false
       arr[2].should be_false
       arr[3].should be_true
       arr[4].should be_false
     end
 
     it "should be possible to get values from primitive array" do 
       arr = [false, true, false].to_java :boolean
       arr[0].should be_false
       arr[1].should be_true
       arr[2].should be_false
     end
 
     it "should be possible to call methods that take primitive array" do 
       arr = [false, true, false].to_java :boolean
       ret = ArrayReceiver::call_with_boolean(arr)
       ret.to_a.should == [false, true, false]
     end
   end
 
   describe "byte" do 
     it "should be possible to create empty array" do 
       arr = Java::byte[0].new
       arr.java_class.to_s.should == "[B"
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::byte[10].new
       arr.java_class.to_s.should == "[B"
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::byte[10,10].new
       arr.java_class.to_s.should == "[[B"
     end
 
     it "should be possible to create primitive array from Ruby array" do 
       # Check with symbol name
       arr = [1,2].to_java :byte
       arr.java_class.to_s.should == "[B"
 
       arr.length.should == 2
 
       arr[0].should == 1
       arr[1].should == 2
 
 
       # Check with type
       arr = [1,2].to_java Java::byte
       arr.java_class.to_s.should == "[B"
 
       arr.length.should == 2
 
       arr[0].should == 1
       arr[1].should == 2
     end
     
     it "should be possible to set values in primitive array" do 
       arr = Java::byte[5].new
       arr[0] = 12
       arr[1] = 20
       arr[2] = 42
       
       arr[0].should == 12
       arr[1].should == 20
       arr[2].should == 42
       arr[3].should == 0
       arr[4].should == 0
     end
 
     it "should be possible to get values from primitive array" do 
       arr = [13, 42, 120].to_java :byte
       arr[0].should == 13
       arr[1].should == 42
       arr[2].should == 120
     end
 
     it "should be possible to call methods that take primitive array" do 
       arr = [13, 42, 120].to_java :byte
       ret = ArrayReceiver::call_with_byte(arr)
       ret.to_a.should == [13, 42, 120]
     end
   end
 
   describe "char" do 
     it "should be possible to create empty array" do 
       arr = Java::char[0].new
       arr.java_class.to_s.should == "[C"
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::char[10].new
       arr.java_class.to_s.should == "[C"
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::char[10,10].new
       arr.java_class.to_s.should == "[[C"
     end
 
     it "should be possible to create primitive array from Ruby array" do 
       # Check with symbol name
       arr = [1,2].to_java :char
       arr.java_class.to_s.should == "[C"
 
       arr.length.should == 2
 
       arr[0].should == 1
       arr[1].should == 2
 
 
       # Check with type
       arr = [1,2].to_java Java::char
       arr.java_class.to_s.should == "[C"
 
       arr.length.should == 2
 
       arr[0].should == 1
       arr[1].should == 2
     end
     
     it "should be possible to set values in primitive array" do 
       arr = Java::char[5].new
       arr[0] = 12
       arr[1] = 20
       arr[2] = 42
       
       arr[0].should == 12
       arr[1].should == 20
       arr[2].should == 42
       arr[3].should == 0
       arr[4].should == 0
     end
 
     it "should be possible to get values from primitive array" do 
       arr = [13, 42, 120].to_java :char
       arr[0].should == 13
       arr[1].should == 42
       arr[2].should == 120
     end
 
     it "should be possible to call methods that take primitive array" do 
       arr = [13, 42, 120].to_java :char
       ret = ArrayReceiver::call_with_char(arr)
       ret.to_a.should == [13, 42, 120]
     end
   end
 
   describe "double" do 
     it "should be possible to create empty array" do 
       arr = Java::double[0].new
       arr.java_class.to_s.should == "[D"
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::double[10].new
       arr.java_class.to_s.should == "[D"
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::double[10,10].new
       arr.java_class.to_s.should == "[[D"
     end
 
     it "should be possible to create primitive array from Ruby array" do 
       # Check with symbol name
       arr = [1.2,2.3].to_java :double
       arr.java_class.to_s.should == "[D"
 
       arr.length.should == 2
 
       arr[0].should == 1.2
       arr[1].should == 2.3
 
 
       # Check with type
       arr = [1.2,2.3].to_java Java::double
       arr.java_class.to_s.should == "[D"
 
       arr.length.should == 2
 
       arr[0].should == 1.2
       arr[1].should == 2.3
     end
     
     it "should be possible to set values in primitive array" do 
       arr = Java::double[5].new
       arr[0] = 12.2
       arr[1] = 20.3
       arr[2] = 42.4
       
       arr[0].should == 12.2
       arr[1].should == 20.3
       arr[2].should == 42.4
       arr[3].should == 0.0
       arr[4].should == 0.0
     end
 
     it "should be possible to get values from primitive array" do 
       arr = [13.2, 42.3, 120.4].to_java :double
       arr[0].should == 13.2
       arr[1].should == 42.3
       arr[2].should == 120.4
     end
 
     it "should be possible to call methods that take primitive array" do 
       arr = [13.2, 42.3, 120.4].to_java :double
       ret = ArrayReceiver::call_with_double(arr)
       ret.to_a.should == [13.2, 42.3, 120.4]
     end
   end
 
   describe "float" do 
     it "should be possible to create empty array" do 
       arr = Java::float[0].new
       arr.java_class.to_s.should == "[F"
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::float[10].new
       arr.java_class.to_s.should == "[F"
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::float[10,10].new
       arr.java_class.to_s.should == "[[F"
     end
 
     it "should be possible to create primitive array from Ruby array" do 
       # Check with symbol name
       arr = [1.2,2.3].to_java :float
       arr.java_class.to_s.should == "[F"
 
       arr.length.should == 2
 
       arr[0].should be_close(1.2, 0.00001)
       arr[1].should be_close(2.3, 0.00001)
 
 
       # Check with type
       arr = [1.2,2.3].to_java Java::float
       arr.java_class.to_s.should == "[F"
 
       arr.length.should == 2
 
       arr[0].should be_close(1.2, 0.00001)
       arr[1].should be_close(2.3, 0.00001)
     end
     
     it "should be possible to set values in primitive array" do 
       arr = Java::float[5].new
       arr[0] = 12.2
       arr[1] = 20.3
       arr[2] = 42.4
       
       arr[0].should be_close(12.2, 0.00001)
       arr[1].should be_close(20.3, 0.00001)
       arr[2].should be_close(42.4, 0.00001)
       arr[3].should == 0.0
       arr[4].should == 0.0
     end
 
     it "should be possible to get values from primitive array" do 
       arr = [13.2, 42.3, 120.4].to_java :float
 
       arr[0].should be_close(13.2, 0.00001)
       arr[1].should be_close(42.3, 0.00001)
       arr[2].should be_close(120.4, 0.00001)
     end
 
     it "should be possible to call methods that take primitive array" do 
       arr = [13.2, 42.3, 120.4].to_java :float
       ret = ArrayReceiver::call_with_float(arr)
       ret.length.should == 3
       ret[0].should be_close(13.2, 0.00001)
       ret[1].should be_close(42.3, 0.00001)
       ret[2].should be_close(120.4, 0.00001)
     end
   end
 
   describe "int" do 
     it "should be possible to create empty array" do 
       arr = Java::int[0].new
       arr.java_class.to_s.should == "[I"
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::int[10].new
       arr.java_class.to_s.should == "[I"
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::int[10,10].new
       arr.java_class.to_s.should == "[[I"
     end
 
     it "should be possible to create primitive array from Ruby array" do 
       # Check with symbol name
       arr = [1,2].to_java :int
       arr.java_class.to_s.should == "[I"
 
       arr.length.should == 2
 
       arr[0].should == 1
       arr[1].should == 2
 
 
       # Check with type
       arr = [1,2].to_java Java::int
       arr.java_class.to_s.should == "[I"
 
       arr.length.should == 2
 
       arr[0].should == 1
       arr[1].should == 2
     end
     
     it "should be possible to set values in primitive array" do 
       arr = Java::int[5].new
       arr[0] = 12
       arr[1] = 20
       arr[2] = 42
       
       arr[0].should == 12
       arr[1].should == 20
       arr[2].should == 42
       arr[3].should == 0
       arr[4].should == 0
     end
 
     it "should be possible to get values from primitive array" do 
       arr = [13, 42, 120].to_java :int
       arr[0].should == 13
       arr[1].should == 42
       arr[2].should == 120
     end
 
     it "should be possible to call methods that take primitive array" do 
       arr = [13, 42, 120].to_java :int
       ret = ArrayReceiver::call_with_int(arr)
       ret.to_a.should == [13, 42, 120]
     end
   end
 
   describe "long" do 
     it "should be possible to create empty array" do 
       arr = Java::long[0].new
       arr.java_class.to_s.should == "[J"
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::long[10].new
       arr.java_class.to_s.should == "[J"
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::long[10,10].new
       arr.java_class.to_s.should == "[[J"
     end
 
     it "should be possible to create primitive array from Ruby array" do 
       # Check with symbol name
       arr = [1,2].to_java :long
       arr.java_class.to_s.should == "[J"
 
       arr.length.should == 2
 
       arr[0].should == 1
       arr[1].should == 2
 
 
       # Check with type
       arr = [1,2].to_java Java::long
       arr.java_class.to_s.should == "[J"
 
       arr.length.should == 2
 
       arr[0].should == 1
       arr[1].should == 2
     end
     
     it "should be possible to set values in primitive array" do 
       arr = Java::long[5].new
       arr[0] = 12
       arr[1] = 20
       arr[2] = 42
       
       arr[0].should == 12
       arr[1].should == 20
       arr[2].should == 42
       arr[3].should == 0
       arr[4].should == 0
     end
 
     it "should be possible to get values from primitive array" do 
       arr = [13, 42, 120].to_java :long
       arr[0].should == 13
       arr[1].should == 42
       arr[2].should == 120
     end
 
     it "should be possible to call methods that take primitive array" do 
       arr = [13, 42, 120].to_java :long
       ret = ArrayReceiver::call_with_long(arr)
       ret.to_a.should == [13, 42, 120]
     end
   end
 
   describe "short" do 
     it "should be possible to create empty array" do 
       arr = Java::short[0].new
       arr.java_class.to_s.should == "[S"
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::short[10].new
       arr.java_class.to_s.should == "[S"
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::short[10,10].new
       arr.java_class.to_s.should == "[[S"
     end
 
     it "should be possible to create primitive array from Ruby array" do 
       # Check with symbol name
       arr = [1,2].to_java :short
       arr.java_class.to_s.should == "[S"
 
       arr.length.should == 2
 
       arr[0].should == 1
       arr[1].should == 2
 
 
       # Check with type
       arr = [1,2].to_java Java::short
       arr.java_class.to_s.should == "[S"
 
       arr.length.should == 2
 
       arr[0].should == 1
       arr[1].should == 2
     end
     
     it "should be possible to set values in primitive array" do 
       arr = Java::short[5].new
       arr[0] = 12
       arr[1] = 20
       arr[2] = 42
       
       arr[0].should == 12
       arr[1].should == 20
       arr[2].should == 42
       arr[3].should == 0
       arr[4].should == 0
     end
 
     it "should be possible to get values from primitive array" do 
       arr = [13, 42, 120].to_java :short
       arr[0].should == 13
       arr[1].should == 42
       arr[2].should == 120
     end
 
     it "should be possible to call methods that take primitive array" do 
       arr = [13, 42, 120].to_java :short
       ret = ArrayReceiver::call_with_short(arr)
       ret.to_a.should == [13, 42, 120]
     end
   end
 
   describe "string" do 
     it "should be possible to create empty array" do 
       arr = java.lang.String[0].new
       arr.java_class.to_s.should == "[Ljava.lang.String;"
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = java.lang.String[10].new
       arr.java_class.to_s.should == "[Ljava.lang.String;"
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = java.lang.String[10,10].new
       arr.java_class.to_s.should == "[[Ljava.lang.String;"
     end
 
     it "should be possible to create primitive array from Ruby array" do 
       # Check with symbol name
       arr = ["foo","bar"].to_java :string
       arr.java_class.to_s.should == "[Ljava.lang.String;"
 
       arr.length.should == 2
 
       arr[0].should == "foo"
       arr[1].should == "bar"
 
 
       # Check with type
       arr = ["foo","bar"].to_java java.lang.String
       arr.java_class.to_s.should == "[Ljava.lang.String;"
 
       arr.length.should == 2
 
       arr[0].should == "foo"
       arr[1].should == "bar"
     end
     
     it "should be possible to set values in primitive array" do 
       arr = java.lang.String[5].new
       arr[0] = "12"
       arr[1] = "20"
       arr[2] = "42"
       
       arr[0].should == "12"
       arr[1].should == "20"
       arr[2].should == "42"
       arr[3].should be_nil
       arr[4].should be_nil
     end
 
     it "should be possible to get values from primitive array" do 
       arr = ["flurg", "glax", "morg"].to_java :string
       arr[0].should == "flurg"
       arr[1].should == "glax"
       arr[2].should == "morg"
     end
 
     it "should be possible to call methods that take primitive array" do 
       arr = ["flurg", "glax", "morg"].to_java :string
       ret = ArrayReceiver::call_with_string(arr)
       ret.to_a.should == ["flurg", "glax", "morg"]
     end
   end
 
   describe "Object ref" do 
     it "should be possible to create empty array" do 
       arr = java.util.HashMap[0].new
       arr.java_class.to_s.should == "[Ljava.util.HashMap;"
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = java.util.HashMap[10].new
       arr.java_class.to_s.should == "[Ljava.util.HashMap;"
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = java.util.HashMap[10,10].new
       arr.java_class.to_s.should == "[[Ljava.util.HashMap;"
     end
 
     it "should be possible to create primitive array from Ruby array" do
       h1 = java.util.HashMap.new
       h1["foo"] = "max"
 
       h2 = java.util.HashMap.new
       h2["max"] = "foo"
 
       arr = [h1, h2].to_java java.util.HashMap
       arr.java_class.to_s.should == "[Ljava.util.HashMap;"
 
       arr.length.should == 2
 
       arr[0].should == h1
       arr[1].should == h2
     end
     
     it "should be possible to set values in primitive array" do 
       h1 = java.util.HashMap.new
       h1["foo"] = "max"
 
       h2 = java.util.HashMap.new
       h2["max"] = "foo"
 
       h3 = java.util.HashMap.new
       h3["flix"] = "mux"
 
       arr = java.util.HashMap[5].new
       arr[0] = h1
       arr[1] = h2
       arr[2] = h3
       
       arr[0].should == h1
       arr[1].should == h2
       arr[2].should == h3
       arr[3].should be_nil
       arr[4].should be_nil
     end
 
     it "should be possible to get values from primitive array" do
       h1 = java.util.HashMap.new
       h1["foo"] = "max"
 
       h2 = java.util.HashMap.new
       h2["max"] = "foo"
 
       h3 = java.util.HashMap.new
       h3["flix"] = "mux"
 
       arr = [h1, h2, h3].to_java java.util.HashMap
       arr[0].should == h1
       arr[1].should == h2
       arr[2].should == h3
     end
 
     it "should be possible to call methods that take primitive array" do
       h1 = java.util.HashMap.new
       h1["foo"] = "max"
 
       h2 = java.util.HashMap.new
       h2["max"] = "foo"
 
       h3 = java.util.HashMap.new
       h3["flix"] = "mux"
 
       arr = [h1, h2, h3].to_java java.util.HashMap
       ret = ArrayReceiver::call_with_object(arr)
       ret.to_a.should == [h1, h2, h3]
     end
   end
 
   describe "Class ref" do 
     it "should be possible to create empty array" do 
       arr = java.lang.Class[0].new
       arr.java_class.to_s.should == "[Ljava.lang.Class;"
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = java.lang.Class[10].new
       arr.java_class.to_s.should == "[Ljava.lang.Class;"
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = java.lang.Class[10,10].new
       arr.java_class.to_s.should == "[[Ljava.lang.Class;"
     end
 
     it "should be possible to create primitive array from Ruby array" do
         h1 = java.lang.String.java_class
         h2 = java.util.HashMap.java_class
 
         arr = [h1, h2].to_java java.lang.Class
         arr.java_class.to_s.should == "[Ljava.lang.Class;"
 
         arr.length.should == 2
 
         arr[0].should == h1
         arr[1].should == h2
     end
     
     it "should be possible to set values in primitive array" do 
         h1 = java.util.Set.java_class
         h2 = java.util.HashMap.java_class
         h3 = java.lang.ref.SoftReference.java_class
 
         arr = java.lang.Class[5].new
         arr[0] = h1
         arr[1] = h2
         arr[2] = h3
         
         arr[0].should == h1
         arr[1].should == h2
         arr[2].should == h3
         arr[3].should be_nil
         arr[4].should be_nil
     end
 
     it "should be possible to get values from primitive array" do
         h1 = java.util.Set.java_class
         h2 = java.util.HashMap.java_class
         h3 = java.lang.ref.SoftReference.java_class
 
         arr = [h1, h2, h3].to_java java.lang.Class
         arr[0].should == h1
         arr[1].should == h2
         arr[2].should == h3
     end
 
     it "should be possible to call methods that take primitive array" do
         h1 = java.util.Set.java_class
         h2 = java.util.HashMap.java_class
         h3 = java.lang.ref.SoftReference.java_class
 
         arr = [h1, h2, h3].to_java java.lang.Class
         ret = ArrayReceiver::call_with_object(arr)
         ret.to_a.should == [h1, h2, h3]
     end
   end
 end
 
+describe "A multi-dimensional Ruby array" do
+  it "can be coerced to a multi-dimensional Java array" do
+    ary = [[1,2],[3,4],[5,6],[7,8],[9,0]]
+    java_ary = ary.to_java(Java::long[])
+    java_ary.class.should == Java::long[][]
+    java_ary[0].class.should == Java::long[]
+
+    java_ary = ary.to_java(Java::double[])
+    java_ary.class.should == Java::double[][]
+    java_ary[0].class.should == Java::double[]
+
+    ary = [[[1]]]
+    java_ary = ary.to_java(Java::long[][])
+    java_ary.class.should == Java::long[][][]
+    java_ary[0].class.should == Java::long[][]
+    java_ary[0][0].class.should == Java::long[]
+  end
+end
+
 # From JRUBY-2944; probably could be reduced a bit more.
 describe "A Ruby class implementing an interface returning a Java Object[]" do
   it "should return an Object[]" do
     class MyHash < Hash; end
 
     class Bar
       include ArrayReturningInterface
       def blah()
         a = []
         a << MyHash.new
         return a.to_java
       end
     end
     ArrayReturningInterfaceConsumer.new.eat(Bar.new).should_not == nil
     ArrayReturningInterfaceConsumer.new.eat(Bar.new).java_object.class.name.should == 'Java::JavaArray'
     ArrayReturningInterfaceConsumer.new.eat(Bar.new).java_object.class.should == Java::JavaArray
   end
 end
 
diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index 651447baa2..4a6d5f52db 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -502,1130 +502,1141 @@ public class JavaClass extends JavaObject {
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
                 staticCallbacks.put(name,new StaticFieldGetterInstaller(name,field));
                 if (!Modifier.isFinal(modifiers)) {
                     String setName = name + '=';
                     staticCallbacks.put(setName,new StaticFieldSetterInstaller(setName,field));
                 }
             } else {
                 AssignedName assignedName = instanceNames.get(name);
                 if (assignedName != null && assignedName.type < AssignedName.FIELD)
                     continue;
                 instanceNames.put(name, new AssignedName(name,AssignedName.FIELD));
                 instanceCallbacks.put(name, new InstanceFieldGetterInstaller(name,field));
                 if (!Modifier.isFinal(modifiers)) {
                     String setName = name + '=';
                     instanceCallbacks.put(setName, new InstanceFieldSetterInstaller(setName,field));
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
                 StaticMethodInvokerInstaller invoker = (StaticMethodInvokerInstaller)staticCallbacks.get(name);
                 if (invoker == null) {
                     invoker = new StaticMethodInvokerInstaller(name);
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
                 InstanceMethodInvokerInstaller invoker = (InstanceMethodInvokerInstaller)instanceCallbacks.get(name);
                 if (invoker == null) {
                     invoker = new InstanceMethodInvokerInstaller(name);
                     instanceCallbacks.put(name,invoker);
                 }
                 invoker.addMethod(method,javaClass);
             }
         }
         // TODO: protected methods.  this is going to require a rework 
         // of some of the mechanism.  
         Constructor[] constructors = EMPTY_CONSTRUCTOR_ARRAY;
         try {
             constructors = javaClass.getConstructors();
         } catch (SecurityException e) {
         }
         for (int i = constructors.length; --i >= 0; ) {
             // we need to collect all methods, though we'll only
             // install the ones that are named in this class
             Constructor ctor = constructors[i];
             
             if (constructorInstaller == null) {
                 constructorInstaller = new ConstructorInvokerInstaller("__jcreate!");
             }
             constructorInstaller.addConstructor(ctor,javaClass);
         }
         
         this.staticAssignedNames = staticNames;
         this.instanceAssignedNames = instanceNames;
         this.staticInstallers = staticCallbacks;
         this.instanceInstallers = instanceCallbacks;
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
         for (Iterator<NamedInstaller> iter = staticInstallers.values().iterator(); iter.hasNext(); ) {
             NamedInstaller installer = iter.next();
             if (installer.type == NamedInstaller.STATIC_METHOD && installer.hasLocalMethod()) {
                 assignAliases((MethodInstaller)installer,staticAssignedNames);
             }
             installer.install(proxy);
         }
         for (Iterator<NamedInstaller> iter = instanceInstallers.values().iterator(); iter.hasNext(); ) {
             NamedInstaller installer = iter.next();
             if (installer.type == NamedInstaller.INSTANCE_METHOD && installer.hasLocalMethod()) {
                 assignAliases((MethodInstaller)installer,instanceAssignedNames);
             }
             installer.install(proxy);
         }
         
         if (constructorInstaller != null) {
             constructorInstaller.install(proxy);
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
 
     private static void assignAliases(MethodInstaller installer, Map<String, AssignedName> assignedNames) {
         String name = installer.name;
         String rubyCasedName = JavaUtil.getRubyCasedName(name);
         addUnassignedAlias(rubyCasedName,assignedNames,installer);
 
         String javaPropertyName = JavaUtil.getJavaPropertyName(name);
         String rubyPropertyName = null;
 
         for (Method method: installer.methods) {
             Class<?>[] argTypes = method.getParameterTypes();
             Class<?> resultType = method.getReturnType();
             int argCount = argTypes.length;
 
             // Add property name aliases
             if (javaPropertyName != null) {
                 if (rubyCasedName.startsWith("get_")) {
                     rubyPropertyName = rubyCasedName.substring(4);
                     if (argCount == 0 ||                                // getFoo      => foo
                         argCount == 1 && argTypes[0] == int.class) {    // getFoo(int) => foo(int)
 
                         addUnassignedAlias(javaPropertyName,assignedNames,installer);
                         addUnassignedAlias(rubyPropertyName,assignedNames,installer);
                     }
                 } else if (rubyCasedName.startsWith("set_")) {
                     rubyPropertyName = rubyCasedName.substring(4);
                     if (argCount == 1 && resultType == void.class) {    // setFoo(Foo) => foo=(Foo)
                         addUnassignedAlias(javaPropertyName+'=',assignedNames,installer);
                         addUnassignedAlias(rubyPropertyName+'=',assignedNames,installer);
                     }
                 } else if (rubyCasedName.startsWith("is_")) {
                     rubyPropertyName = rubyCasedName.substring(3);
                     if (resultType == boolean.class) {                  // isFoo() => foo, isFoo(*) => foo(*)
                         addUnassignedAlias(javaPropertyName,assignedNames,installer);
                         addUnassignedAlias(rubyPropertyName,assignedNames,installer);
                     }
                 }
             }
 
             // Additionally add ?-postfixed aliases to any boolean methods and properties.
             if (resultType == boolean.class) {
                 // is_something?, contains_thing?
                 addUnassignedAlias(rubyCasedName+'?',assignedNames,installer);
                 if (rubyPropertyName != null) {
                     // something?
                     addUnassignedAlias(rubyPropertyName+'?',assignedNames,installer);
                 }
             }
         }
     }
     
     private static void addUnassignedAlias(String name, Map<String, AssignedName> assignedNames,
             MethodInstaller installer) {
         if (name != null) {
             AssignedName assignedName = (AssignedName)assignedNames.get(name);
             if (assignedName == null) {
                 installer.addAlias(name);
                 assignedNames.put(name,new AssignedName(name,AssignedName.ALIAS));
             } else if (assignedName.type == AssignedName.ALIAS) {
                 installer.addAlias(name);
             } else if (assignedName.type > AssignedName.ALIAS) {
                 // TODO: there will be some additional logic in this branch
                 // dealing with conflicting protected fields. 
                 installer.addAlias(name);
                 assignedNames.put(name,new AssignedName(name,AssignedName.ALIAS));
             }
         }
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
         for (Iterator<NamedInstaller> iter = staticInstallers.values().iterator(); iter.hasNext(); ) {
             NamedInstaller installer = iter.next();
             if (installer.type == NamedInstaller.STATIC_METHOD && installer.hasLocalMethod()) {
                 assignAliases((MethodInstaller)installer,staticAssignedNames);
             }
             installer.install(module);
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
                     return RuntimeHelpers.invoke(self.getRuntime().getCurrentContext(), self, name, newArgs, block);
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
     public JavaConstructor declared_constructor(IRubyObject[] args) {
         try {
             Class<?>[] parameterTypes = buildClassArgs(args);
             Constructor<?> constructor = javaClass().getDeclaredConstructor (parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
             throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     private Class<?>[] buildClassArgs(IRubyObject[] args) {
         JavaSupport javaSupport = getRuntime().getJavaSupport();
         Class<?>[] parameterTypes = new Class<?>[args.length];
         for (int i = args.length; --i >= 0; ) {
             String name = args[i].asJavaString();
             parameterTypes[i] = javaSupport.loadJavaClassVerbose(name);
         }
         return parameterTypes;
     }
 
     @JRubyMethod
     public JavaClass array_class() {
         return JavaClass.get(getRuntime(), Array.newInstance(javaClass(), 0).getClass());
     }
    
     @JRubyMethod(required = 1)
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
    
     public IRubyObject emptyJavaArray(ThreadContext context) {
         JavaArray javaArray = new JavaArray(getRuntime(), Array.newInstance(javaClass(), 0));
         RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
         
         ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), proxyClass);
         proxy.dataWrapStruct(javaArray);
         
         return proxy;
     }
    
     public IRubyObject javaArraySubarray(ThreadContext context, JavaArray fromArray, int index, int size) {
         int actualLength = Array.getLength(fromArray.getValue());
         if (index >= actualLength) {
             return context.getRuntime().getNil();
         } else {
             if (index + size > actualLength) {
                 size = actualLength - index;
             }
             
             Object newArray = Array.newInstance(javaClass(), size);
             JavaArray javaArray = new JavaArray(getRuntime(), newArray);
             System.arraycopy(fromArray.getValue(), index, newArray, 0, size);
             RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
 
             ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), proxyClass);
             proxy.dataWrapStruct(javaArray);
 
             return proxy;
         }
     }
    
     /**
      * Contatenate two Java arrays into a new one. The component type of the
      * additional array must be assignable to the component type of the
      * original array.
      * 
      * @param context
      * @param original
      * @param additional
      * @return
      */
     public IRubyObject concatArrays(ThreadContext context, JavaArray original, JavaArray additional) {
         int oldLength = (int)original.length().getLongValue();
         int addLength = (int)additional.length().getLongValue();
         Object newArray = Array.newInstance(javaClass(), oldLength + addLength);
         JavaArray javaArray = new JavaArray(getRuntime(), newArray);
         System.arraycopy(original.getValue(), 0, newArray, 0, oldLength);
         System.arraycopy(additional.getValue(), 0, newArray, oldLength, addLength);
         RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
 
         ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), proxyClass);
         proxy.dataWrapStruct(javaArray);
 
         return proxy;
     }
    
     /**
      * The slow version for when concatenating a Java array of a different type.
      * 
      * @param context
      * @param original
      * @param additional
      * @return
      */
     public IRubyObject concatArrays(ThreadContext context, JavaArray original, IRubyObject additional) {
         int oldLength = (int)original.length().getLongValue();
         int addLength = (int)((RubyFixnum)RuntimeHelpers.invoke(context, additional, "length")).getLongValue();
         Object newArray = Array.newInstance(javaClass(), oldLength + addLength);
         JavaArray javaArray = new JavaArray(getRuntime(), newArray);
         System.arraycopy(original.getValue(), 0, newArray, 0, oldLength);
         RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
         ArrayJavaProxy proxy = new ArrayJavaProxy(context.getRuntime(), proxyClass);
         proxy.dataWrapStruct(javaArray);
         
         Ruby runtime = context.getRuntime();
         for (int i = 0; i < addLength; i++) {
             RuntimeHelpers.invoke(context, proxy, "[]=", runtime.newFixnum(oldLength + i), 
                     RuntimeHelpers.invoke(context, additional, "[]", runtime.newFixnum(i)));
         }
 
         return proxy;
     }
 
     public IRubyObject javaArrayFromRubyArray(ThreadContext context, IRubyObject fromArray) {
         Ruby runtime = context.getRuntime();
         if (!(fromArray instanceof RubyArray)) {
             throw runtime.newTypeError(fromArray, runtime.getArray());
         }
         RubyArray rubyArray = (RubyArray)fromArray;
         JavaArray javaArray = new JavaArray(getRuntime(), Array.newInstance(javaClass(), rubyArray.size()));
-        ArrayJavaAddons.copyDataToJavaArray(context, rubyArray, javaArray);
-        RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
         
+        if (javaClass().isArray()) {
+            // if it's an array of arrays, recurse with the component type
+            for (int i = 0; i < rubyArray.size(); i++) {
+                JavaClass componentType = component_type();
+                IRubyObject wrappedComponentArray = componentType.javaArrayFromRubyArray(context, rubyArray.eltInternal(i));
+                javaArray.setWithExceptionHandling(i, JavaUtil.unwrapJavaObject(wrappedComponentArray));
+            }
+        } else {
+            ArrayJavaAddons.copyDataToJavaArray(context, rubyArray, javaArray);
+        }
+        
+        RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
+
         ArrayJavaProxy proxy = new ArrayJavaProxy(runtime, proxyClass);
         proxy.dataWrapStruct(javaArray);
         
         return proxy;
     }
 
     @JRubyMethod
     public RubyArray fields() {
         return buildFieldResults(javaClass().getFields());
     }
 
     @JRubyMethod
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
 
     @JRubyMethod(required = 1)
     public JavaField field(IRubyObject name) {
         String stringName = name.asJavaString();
         Field field = null;
         try {
             field = javaClass().getField(stringName);
             return new JavaField(getRuntime(), field);
         } catch (NoSuchFieldException nsfe) {
             String newName = JavaUtil.getJavaCasedName(stringName);
             if(newName != null) {
                 try {
                     field = javaClass().getField(newName);
                     return new JavaField(getRuntime(), field);
                 } catch (NoSuchFieldException nsfe2) {}
             }
             throw undefinedFieldError(stringName);
          }
     }
 
     @JRubyMethod(required = 1)
     public JavaField declared_field(IRubyObject name) {
         String stringName = name.asJavaString();
         Field field = null;
         try {
             field = javaClass().getDeclaredField(stringName);
             return new JavaField(getRuntime(), field);
         } catch (NoSuchFieldException nsfe) {
             String newName = JavaUtil.getJavaCasedName(stringName);
             if(newName != null) {
                 try {
                     field = javaClass().getDeclaredField(newName);
                     return new JavaField(getRuntime(), field);
                 } catch (NoSuchFieldException nsfe2) {}
             }
             throw undefinedFieldError(stringName);
         }
     }
 
     private RaiseException undefinedFieldError(String name) {
         return getRuntime().newNameError("undefined field '" + name + "' for class '" + javaClass().getName() + "'", name);
     }
 
     @JRubyMethod
     public RubyArray interfaces() {
         return JavaClass.getRubyArray(getRuntime(), javaClass().getInterfaces());
     }
 
     @JRubyMethod(name = "primitive?")
     public RubyBoolean primitive_p() {
         return getRuntime().newBoolean(isPrimitive());
     }
 
     @JRubyMethod(name = "assignable_from?", required = 1)
     public RubyBoolean assignable_from_p(IRubyObject other) {
         if (! (other instanceof JavaClass)) {
             throw getRuntime().newTypeError("assignable_from requires JavaClass (" + other.getType() + " given)");
         }
 
         Class<?> otherClass = ((JavaClass) other).javaClass();
         return assignable(javaClass(), otherClass) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     static boolean assignable(Class<?> thisClass, Class<?> otherClass) {
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
 
     @JRubyMethod
     public JavaClass component_type() {
         if (! javaClass().isArray()) {
             throw getRuntime().newTypeError("not a java array-class");
         }
         return JavaClass.get(getRuntime(), javaClass().getComponentType());
     }
     
 }
