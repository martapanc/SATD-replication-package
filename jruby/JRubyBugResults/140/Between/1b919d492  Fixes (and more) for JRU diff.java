diff --git a/spec/java_integration/types/array_spec.rb b/spec/java_integration/types/array_spec.rb
index c27d0cd984..557717ed67 100644
--- a/spec/java_integration/types/array_spec.rb
+++ b/spec/java_integration/types/array_spec.rb
@@ -1,851 +1,851 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 import "java_integration.fixtures.ArrayReceiver"
 import "java_integration.fixtures.ArrayReturningInterface"
 import "java_integration.fixtures.ArrayReturningInterfaceConsumer"
 
 describe "A Java primitive Array of type" do
   describe "boolean" do 
     it "should be possible to create empty array" do 
       arr = Java::boolean[0].new
       arr.java_class.to_s.should == "[Z"
       arr.should be_empty
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::boolean[10].new
       arr.java_class.to_s.should == "[Z"
       arr.should_not be_empty
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::boolean[10,10].new
       arr.java_class.to_s.should == "[[Z"
       arr.should_not be_empty
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
       arr.should be_empty
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::byte[10].new
       arr.java_class.to_s.should == "[B"
       arr.should_not be_empty
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::byte[10,10].new
       arr.java_class.to_s.should == "[[B"
       arr.should_not be_empty
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
       arr.should be_empty
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::char[10].new
       arr.java_class.to_s.should == "[C"
       arr.should_not be_empty
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::char[10,10].new
       arr.java_class.to_s.should == "[[C"
       arr.should_not be_empty
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
       arr.should be_empty
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::double[10].new
       arr.java_class.to_s.should == "[D"
       arr.should_not be_empty
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::double[10,10].new
       arr.java_class.to_s.should == "[[D"
       arr.should_not be_empty
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
       arr.should be_empty
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::float[10].new
       arr.java_class.to_s.should == "[F"
       arr.should_not be_empty
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::float[10,10].new
       arr.java_class.to_s.should == "[[F"
       arr.should_not be_empty
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
       arr.should be_empty
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::int[10].new
       arr.java_class.to_s.should == "[I"
       arr.should_not be_empty
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::int[10,10].new
       arr.java_class.to_s.should == "[[I"
       arr.should_not be_empty
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
       arr.should be_empty
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::long[10].new
       arr.java_class.to_s.should == "[J"
       arr.should_not be_empty
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::long[10,10].new
       arr.java_class.to_s.should == "[[J"
       arr.should_not be_empty
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
       arr.should be_empty
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = Java::short[10].new
       arr.java_class.to_s.should == "[S"
       arr.should_not be_empty
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = Java::short[10,10].new
       arr.java_class.to_s.should == "[[S"
       arr.should_not be_empty
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
       arr.should be_empty
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = java.lang.String[10].new
       arr.java_class.to_s.should == "[Ljava.lang.String;"
       arr.should_not be_empty
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = java.lang.String[10,10].new
       arr.java_class.to_s.should == "[[Ljava.lang.String;"
       arr.should_not be_empty
     end
 
     it "should be possible to create primitive array from Ruby array" do 
       # Check with symbol name
       arr = ["foo", :bar].to_java :string
       arr.java_class.to_s.should == "[Ljava.lang.String;"
 
       arr.length.should == 2
 
       arr[0].should == "foo"
       arr[1].should == "bar"
 
 
       # Check with type
       arr = ["foo", :bar].to_java java.lang.String
       arr.java_class.to_s.should == "[Ljava.lang.String;"
 
       arr.length.should == 2
 
       arr[0].should == "foo"
       arr[1].should == "bar"
     end
     
     it "should be possible to set values in primitive array" do 
       arr = java.lang.String[5].new
       arr[0] = "12"
       arr[1] = :blah
       arr[2] = "42"
       
       arr[0].should == "12"
       arr[1].should == "blah"
       arr[2].should == "42"
       arr[3].should be_nil
       arr[4].should be_nil
     end
 
     it "should be possible to get values from primitive array" do 
       arr = ["flurg", :glax, "morg"].to_java :string
       arr[0].should == "flurg"
       arr[1].should == "glax"
       arr[2].should == "morg"
     end
 
     it "should be possible to call methods that take primitive array" do 
       arr = ["flurg", :glax, "morg"].to_java :string
       ret = ArrayReceiver::call_with_string(arr)
       ret.to_a.should == ["flurg", "glax", "morg"]
     end
   end
 
   describe "Object" do
     it "should be possible to create empty array" do 
       arr = java.util.HashMap[0].new
       arr.java_class.to_s.should == "[Ljava.util.HashMap;"
       arr.should be_empty
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = java.util.HashMap[10].new
       arr.java_class.to_s.should == "[Ljava.util.HashMap;"
       arr.should_not be_empty
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = java.util.HashMap[10,10].new
       arr.java_class.to_s.should == "[[Ljava.util.HashMap;"
       arr.should_not be_empty
     end
 
     it "should be possible to create primitive array from Ruby array" do
       h1 = java.util.HashMap.new
       h1["foo"] = "max"
 
       h2 = java.util.HashMap.new
       h2["max"] = "foo"
 
       arr = [h1, h2].to_java java.util.HashMap
       arr.java_class.to_s.should == "[Ljava.util.HashMap;"
 
       arr.length.should == 2
 
       arr[0].should be_equal(h1)
       arr[1].should be_equal(h2)
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
       arr[0].should be_equal(h1)
       arr[1].should be_equal(h2)
       arr[2].should be_equal(h3)
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
 
     it "should coerce strings, booleans, and numerics via []" do
       ary = [1, 1.0, "blah", true, false, nil].to_java
 
       ary[0].class.should == Fixnum
       ary[1].class.should == Float
       ary[2].class.should == String
       ary[3].class.should == TrueClass
       ary[4].class.should == FalseClass
       ary[5].class.should == NilClass
     end
 
-    it "should raise ArgumentError when types can't be coerced" do
-      lambda { [Time.new].to_java :string }.should raise_error(ArgumentError)
+    it "should raise TypeError when types can't be coerced" do
+      lambda { [Time.new].to_java :string }.should raise_error(TypeError)
     end
   end
 
   describe "Class ref" do 
     it "should be possible to create empty array" do 
       arr = java.lang.Class[0].new
       arr.java_class.to_s.should == "[Ljava.lang.Class;"
       arr.should be_empty
     end
     
     it "should be possible to create uninitialized single dimensional array" do 
       arr = java.lang.Class[10].new
       arr.java_class.to_s.should == "[Ljava.lang.Class;"
       arr.should_not be_empty
     end
     
     it "should be possible to create uninitialized multi dimensional array" do 
       arr = java.lang.Class[10,10].new
       arr.java_class.to_s.should == "[[Ljava.lang.Class;"
       arr.should_not be_empty
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
 
 describe "A Ruby array with a nil element" do
   it "can be coerced to an array of objects" do
     ary = [nil]
     result = ary.to_java java.lang.Runnable
     result[0].should be_nil
   end
 
   it "can be coerced to an array of classes" do
     ary = [nil]
     result = ary.to_java java.lang.Class
     result[0].should be_nil
   end
 end
 
 describe "A multi-dimensional Ruby array" do
   it "can be coerced to a multi-dimensional Java array" do
     ary = [[1,2],[3,4],[5,6],[7,8],[9,0]]
     java_ary = ary.to_java(Java::long[])
     java_ary.class.should == Java::long[][]
     java_ary[0].class.should == Java::long[]
 
     java_ary = ary.to_java(Java::double[])
     java_ary.class.should == Java::double[][]
     java_ary[0].class.should == Java::double[]
 
     ary = [[[1]]]
     java_ary = ary.to_java(Java::long[][])
     java_ary.class.should == Java::long[][][]
     java_ary[0].class.should == Java::long[][]
     java_ary[0][0].class.should == Java::long[]
   end
 end
 
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
 
 # JRUBY-3175: Cloning java byte array returns incorrect object
 describe "A Java byte array" do
   it "should clone to another usable Java byte array" do
     s = "switch me to bytes".to_java_bytes
     c = s.clone
     String.from_java_bytes(s).should == "switch me to bytes"
     String.from_java_bytes(c).should == "switch me to bytes"
     String.from_java_bytes(s).should == String.from_java_bytes(c)
   end
 end
 
diff --git a/spec/java_integration/types/coercion_spec.rb b/spec/java_integration/types/coercion_spec.rb
index 7e0b911cf9..29bf049ab8 100644
--- a/spec/java_integration/types/coercion_spec.rb
+++ b/spec/java_integration/types/coercion_spec.rb
@@ -1,710 +1,784 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 import "java_integration.fixtures.CoreTypeMethods"
 import "java_integration.fixtures.JavaFields"
 import "java_integration.fixtures.ValueReceivingInterface"
 import "java_integration.fixtures.ValueReceivingInterfaceHandler"
 
 import "java_integration.fixtures.PackageConstructor"
 import "java_integration.fixtures.ProtectedConstructor"
 import "java_integration.fixtures.PrivateConstructor"
 
 describe "Java String and primitive-typed methods" do
   it "should coerce to Ruby types when returned" do 
     CoreTypeMethods.getString.should be_kind_of(String)
     CoreTypeMethods.getString.should == "foo";
     
     CoreTypeMethods.getByte.should be_kind_of(Fixnum)
     CoreTypeMethods.getByte.should == 1
     CoreTypeMethods.getShort.should be_kind_of(Fixnum)
     CoreTypeMethods.getShort.should == 2
     CoreTypeMethods.getChar.should be_kind_of(Fixnum)
     CoreTypeMethods.getChar.should == 2
     CoreTypeMethods.getInt.should be_kind_of(Fixnum)
     CoreTypeMethods.getInt.should == 4
     CoreTypeMethods.getLong.should be_kind_of(Fixnum)
     CoreTypeMethods.getLong.should == 8
     
     CoreTypeMethods.getFloat.should be_kind_of(Float)
     CoreTypeMethods.getFloat.should == 4.5
     CoreTypeMethods.getDouble.should be_kind_of(Float)
     CoreTypeMethods.getDouble.should == 8.5
     
     CoreTypeMethods.getBooleanTrue.should be_kind_of(TrueClass)
     CoreTypeMethods.getBooleanTrue.should == true
     CoreTypeMethods.getBooleanFalse.should be_kind_of(FalseClass)
     CoreTypeMethods.getBooleanFalse.should == false
     
     CoreTypeMethods.getNull.should be_kind_of(NilClass)
     CoreTypeMethods.getNull.should == nil
     
     CoreTypeMethods.getVoid.should == nil
 
     CoreTypeMethods.getBigInteger.should == 1234567890123456789012345678901234567890
   end
   
   it "should be coerced from Ruby types when passing parameters" do
     CoreTypeMethods.setString("string").should == "string"
     
     CoreTypeMethods.setByte(1).should == "1"
     CoreTypeMethods.setShort(1).should == "1"
     CoreTypeMethods.setChar(1).should == "\001"
     CoreTypeMethods.setInt(1).should == "1"
     CoreTypeMethods.setLong(1).should == "1"
     
     CoreTypeMethods.setFloat(1).should == "1.0"
     CoreTypeMethods.setDouble(1).should == "1.0"
 
     CoreTypeMethods.setByte(1.5).should == "1"
     CoreTypeMethods.setShort(1.5).should == "1"
     CoreTypeMethods.setChar(1.5).should == "\001"
     CoreTypeMethods.setInt(1.5).should == "1"
     CoreTypeMethods.setLong(1.5).should == "1"
 
     CoreTypeMethods.setFloat(1.5).should == "1.5"
     CoreTypeMethods.setDouble(1.5).should == "1.5"
     
     CoreTypeMethods.setBooleanTrue(true).should == "true"
     CoreTypeMethods.setBooleanFalse(false).should == "false"
 
     CoreTypeMethods.setByte(nil).should == "0"
     CoreTypeMethods.setShort(nil).should == "0"
     CoreTypeMethods.setChar(nil).should == "\000"
     CoreTypeMethods.setInt(nil).should == "0"
     CoreTypeMethods.setLong(nil).should == "0"
 
     CoreTypeMethods.setFloat(nil).should == "0.0"
     CoreTypeMethods.setDouble(nil).should == "0.0"
 
     CoreTypeMethods.setBooleanTrue(nil).should == "false"
     CoreTypeMethods.setBooleanFalse(nil).should == "false"
 
     CoreTypeMethods.setBigInteger(1234567890123456789012345678901234567890).should ==
       "1234567890123456789012345678901234567890"
 
     CoreTypeMethods.setByteObj(1).should == "1"
     CoreTypeMethods.setShortObj(1).should == "1"
     CoreTypeMethods.setCharObj(1).should == "\001"
     CoreTypeMethods.setIntObj(1).should == "1"
     CoreTypeMethods.setLongObj(1).should == "1"
 
     CoreTypeMethods.setFloatObj(1).should == "1.0"
     CoreTypeMethods.setDoubleObj(1).should == "1.0"
 
     CoreTypeMethods.setByteObj(1.5).should == "1"
     CoreTypeMethods.setShortObj(1.5).should == "1"
     CoreTypeMethods.setCharObj(1.5).should == "\001"
     CoreTypeMethods.setIntObj(1.5).should == "1"
     CoreTypeMethods.setLongObj(1.5).should == "1"
 
     CoreTypeMethods.setFloatObj(1.5).should == "1.5"
     CoreTypeMethods.setDoubleObj(1.5).should == "1.5"
 
     CoreTypeMethods.setBooleanTrueObj(true).should == "true"
     CoreTypeMethods.setBooleanFalseObj(false).should == "false"
 
     CoreTypeMethods.setByteObj(nil).should == "null"
     CoreTypeMethods.setShortObj(nil).should == "null"
     CoreTypeMethods.setCharObj(nil).should == "null"
     CoreTypeMethods.setIntObj(nil).should == "null"
     CoreTypeMethods.setLongObj(nil).should == "null"
 
     CoreTypeMethods.setFloatObj(nil).should == "null"
     CoreTypeMethods.setDoubleObj(nil).should == "null"
 
     CoreTypeMethods.setBooleanTrueObj(nil).should == "null"
     CoreTypeMethods.setBooleanFalseObj(nil).should == "null"
     
     CoreTypeMethods.setNull(nil).should == "null"
   end
   
   it "should raise errors when passed values can not be precisely coerced" do
     lambda { CoreTypeMethods.setByte(1 << 8) }.should raise_error(RangeError)
     lambda { CoreTypeMethods.setShort(1 << 16) }.should raise_error(RangeError)
     lambda { CoreTypeMethods.setChar(1 << 16) }.should raise_error(RangeError)
     lambda { CoreTypeMethods.setInt(1 << 32) }.should raise_error(RangeError)
     lambda { CoreTypeMethods.setLong(1 << 64) }.should raise_error(RangeError)
   end
   
   it "should select the most narrow and precise overloaded method" do
     pending "selection based on precision is not supported yet" do
       CoreTypeMethods.getType(1).should == "byte"
       CoreTypeMethods.getType(1 << 8).should == "short"
       CoreTypeMethods.getType(1 << 16).should == "int"
       CoreTypeMethods.getType(1.0).should == "float"
     end
     CoreTypeMethods.getType(1 << 32).should == "long"
     
     CoreTypeMethods.getType(2.0 ** 128).should == "double"
     
     CoreTypeMethods.getType("foo").should == "String"
     pending "passing null to overloaded methods randomly selects from them" do
       CoreTypeMethods.getType(nil).should == "CharSequence"
     end
 
     CoreTypeMethods.getType(BigDecimal.new('1.1')).should == "BigDecimal"
   end
 end
 
 describe "Java Object-typed methods" do
   it "should coerce primitive Ruby types to a single, specific Java type" do
     CoreTypeMethods.getObjectType("foo").should == "class java.lang.String"
 
     CoreTypeMethods.getObjectType(0).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Byte::MAX_VALUE).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Byte::MIN_VALUE).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Byte::MAX_VALUE + 1).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Byte::MIN_VALUE - 1).should == "class java.lang.Long"
 
     CoreTypeMethods.getObjectType(java::lang::Short::MAX_VALUE).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Short::MIN_VALUE).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Short::MAX_VALUE + 1).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Short::MIN_VALUE - 1).should == "class java.lang.Long"
 
     CoreTypeMethods.getObjectType(java::lang::Integer::MAX_VALUE).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Integer::MIN_VALUE).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Integer::MAX_VALUE + 1).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Integer::MIN_VALUE - 1).should == "class java.lang.Long"
 
     CoreTypeMethods.getObjectType(java::lang::Long::MAX_VALUE).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Long::MIN_VALUE).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Long::MAX_VALUE + 1).should == "class java.math.BigInteger"
     CoreTypeMethods.getObjectType(java::lang::Long::MIN_VALUE - 1).should == "class java.math.BigInteger"
 
     CoreTypeMethods.getObjectType(java::lang::Float::MAX_VALUE).should == "class java.lang.Double"
     CoreTypeMethods.getObjectType(java::lang::Float::MIN_VALUE).should == "class java.lang.Double"
     CoreTypeMethods.getObjectType(-java::lang::Float::MAX_VALUE).should == "class java.lang.Double"
     CoreTypeMethods.getObjectType(-java::lang::Float::MIN_VALUE).should == "class java.lang.Double"
 
     CoreTypeMethods.getObjectType(java::lang::Float::NaN).should == "class java.lang.Double"
     CoreTypeMethods.getObjectType(0.0).should == "class java.lang.Double"
 
     CoreTypeMethods.getObjectType(java::lang::Double::MAX_VALUE).should == "class java.lang.Double"
     CoreTypeMethods.getObjectType(java::lang::Double::MIN_VALUE).should == "class java.lang.Double"
     CoreTypeMethods.getObjectType(-java::lang::Double::MAX_VALUE).should == "class java.lang.Double"
     CoreTypeMethods.getObjectType(-java::lang::Double::MIN_VALUE).should == "class java.lang.Double"
 
     CoreTypeMethods.getObjectType(true).should == "class java.lang.Boolean"
     CoreTypeMethods.getObjectType(1 << 128).should == "class java.math.BigInteger"
   end
 end
 
 describe "Java String and primitive-typed fields" do
   it "coerce to Ruby types when retrieved" do
     # static
     JavaFields.stringStaticField.should be_kind_of(String)
     JavaFields.stringStaticField.should == "foo";
     
     JavaFields.byteStaticField.should be_kind_of(Fixnum)
     JavaFields.byteStaticField.should == 1
     JavaFields.shortStaticField.should be_kind_of(Fixnum)
     JavaFields.shortStaticField.should == 2
     JavaFields.charStaticField.should be_kind_of(Fixnum)
     JavaFields.charStaticField.should == 2
     JavaFields.intStaticField.should be_kind_of(Fixnum)
     JavaFields.intStaticField.should == 4
     JavaFields.longStaticField.should be_kind_of(Fixnum)
     JavaFields.longStaticField.should == 8
     
     JavaFields.floatStaticField.should be_kind_of(Float)
     JavaFields.floatStaticField.should == 4.5
     JavaFields.doubleStaticField.should be_kind_of(Float)
     JavaFields.doubleStaticField.should == 8.5
     
     JavaFields.trueStaticField.should be_kind_of(TrueClass)
     JavaFields.trueStaticField.should == true
     JavaFields.falseStaticField.should be_kind_of(FalseClass)
     JavaFields.falseStaticField.should == false
     
     JavaFields.nullStaticField.should be_kind_of(NilClass)
     JavaFields.nullStaticField.should == nil
 
     JavaFields.bigIntegerStaticField.should be_kind_of(Bignum)
     JavaFields.bigIntegerStaticField.should ==
       1234567890123456789012345678901234567890
     
     # instance
     jf = JavaFields.new
     jf.stringField.should be_kind_of(String)
     jf.stringField.should == "foo";
     
     jf.byteField.should be_kind_of(Fixnum)
     jf.byteField.should == 1
     jf.shortField.should be_kind_of(Fixnum)
     jf.shortField.should == 2
     jf.charField.should be_kind_of(Fixnum)
     jf.charField.should == 2
     jf.intField.should be_kind_of(Fixnum)
     jf.intField.should == 4
     jf.longField.should be_kind_of(Fixnum)
     jf.longField.should == 8
     
     jf.floatField.should be_kind_of(Float)
     jf.floatField.should == 4.5
     jf.doubleField.should be_kind_of(Float)
     jf.doubleField.should == 8.5
     
     jf.trueField.should be_kind_of(TrueClass)
     jf.trueField.should == true
     jf.falseField.should be_kind_of(FalseClass)
     jf.falseField.should == false
     
     jf.nullField.should be_kind_of(NilClass)
     jf.nullField.should == nil
 
     jf.bigIntegerField.should be_kind_of(Bignum)
     jf.bigIntegerField.should ==
       1234567890123456789012345678901234567890
   end
 end
 
 describe "Java primitive-box-typed fields" do
   it "coerce to Ruby types when retrieved" do
     # static
     JavaFields.byteObjStaticField.should be_kind_of(Fixnum)
     JavaFields.byteObjStaticField.should == 1
     JavaFields.shortObjStaticField.should be_kind_of(Fixnum)
     JavaFields.shortObjStaticField.should == 2
     JavaFields.charObjStaticField.should be_kind_of(Fixnum)
     JavaFields.charObjStaticField.should == 2
     JavaFields.intObjStaticField.should be_kind_of(Fixnum)
     JavaFields.intObjStaticField.should == 4
     JavaFields.longObjStaticField.should be_kind_of(Fixnum)
     JavaFields.longObjStaticField.should == 8
     
     JavaFields.floatObjStaticField.should be_kind_of(Float)
     JavaFields.floatObjStaticField.should == 4.5
     JavaFields.doubleObjStaticField.should be_kind_of(Float)
     JavaFields.doubleObjStaticField.should == 8.5
     
     JavaFields.trueObjStaticField.should be_kind_of(TrueClass)
     JavaFields.trueObjStaticField.should == true
     JavaFields.falseObjStaticField.should be_kind_of(FalseClass)
     JavaFields.falseObjStaticField.should == false
     
     # instance
     jf = JavaFields.new
     jf.byteObjField.should be_kind_of(Fixnum)
     jf.byteObjField.should == 1
     jf.shortObjField.should be_kind_of(Fixnum)
     jf.shortObjField.should == 2
     jf.charObjField.should be_kind_of(Fixnum)
     jf.charObjField.should == 2
     jf.intObjField.should be_kind_of(Fixnum)
     jf.intObjField.should == 4
     jf.longObjField.should be_kind_of(Fixnum)
     jf.longObjField.should == 8
     
     jf.floatObjField.should be_kind_of(Float)
     jf.floatObjField.should == 4.5
     jf.doubleObjField.should be_kind_of(Float)
     jf.doubleObjField.should == 8.5
     
     jf.trueObjField.should be_kind_of(TrueClass)
     jf.trueObjField.should == true
     jf.falseObjField.should be_kind_of(FalseClass)
     jf.falseObjField.should == false
   end
 end
 
 describe "Java String, primitive, and object-typed interface methods" do
   it "should coerce or wrap to usable Ruby types for the implementer" do
     impl = Class.new {
       attr_accessor :result
       include ValueReceivingInterface
       
       def receiveObject(obj)
         self.result = obj
         obj
       end
       
       def receiveLongAndDouble(l, d)
         str = (l + d).to_s
         self.result = str
         str
       end
       
       %w[String Byte Short Char Int Long Float Double Null True False].each do |type|
         alias_method "receive#{type}".intern, :receiveObject
       end
     }
     
     vri = impl.new
     vri_handler = ValueReceivingInterfaceHandler.new(vri);
     
     obj = java.lang.Object.new
     vri_handler.receiveObject(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == java.lang.Object
     
     obj = "foo"
     vri_handler.receiveString(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == String
     
     obj = 1
     
     vri_handler.receiveByte(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Fixnum
     
     vri_handler.receiveShort(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Fixnum
 
     vri_handler.receiveChar(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Fixnum
     
     vri_handler.receiveInt(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Fixnum
     
     vri_handler.receiveLong(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Fixnum
     
     vri_handler.receiveFloat(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Float
     
     vri_handler.receiveDouble(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Float
     
     vri_handler.receiveNull(nil).should == nil
     vri.result.should == nil
     vri.result.class.should == NilClass
     
     vri_handler.receiveTrue(true).should == true
     vri.result.should == true
     vri.result.class.should == TrueClass
     
     vri_handler.receiveFalse(false).should == false
     vri.result.should == false
     vri.result.class.should == FalseClass
     
     vri_handler.receiveLongAndDouble(1, 1.0).should == "2.0"
     vri.result.should == "2.0"
     vri.result.class.should == String
   end
 end
 
 describe "Java primitive-typed interface methods" do
   it "should coerce nil to zero-magnitude primitives" do
     impl = Class.new {
       attr_accessor :result
       include ValueReceivingInterface
 
       def receive_primitive(obj)
         self.result = obj
         nil
       end
 
       %w[Byte Short Char Int Long Float Double Null True False].each do |type|
         alias_method "receive#{type}".intern, :receive_primitive
       end
     }
     
     vri = impl.new
     vri_handler = ValueReceivingInterfaceHandler.new(vri);
 
     vri_handler.receiveByte(nil).should == 0
     vri.result.should == 0
     vri.result.class.should == Fixnum
 
     vri_handler.receiveShort(nil).should == 0
     vri.result.should == 0
     vri.result.class.should == Fixnum
 
     vri_handler.receiveChar(nil).should == 0
     vri.result.should == 0
     vri.result.class.should == Fixnum
 
     vri_handler.receiveInt(nil).should == 0
     vri.result.should == 0
     vri.result.class.should == Fixnum
 
     vri_handler.receiveLong(nil).should == 0
     vri.result.should == 0
     vri.result.class.should == Fixnum
 
     vri_handler.receiveFloat(nil).should == 0.0
     vri.result.should == 0.0
     vri.result.class.should == Float
 
     vri_handler.receiveDouble(nil).should == 0.0
     vri.result.should == 0.0
     vri.result.class.should == Float
 
     vri_handler.receiveTrue(nil).should == false
     vri.result.should == false
     vri.result.class.should == FalseClass
 
     vri_handler.receiveFalse(nil).should == false
     vri.result.should == false
     vri.result.class.should == FalseClass
   end
 end
 
 describe "Java primitive-box-typed interface methods" do
   it "should coerce to Ruby types for the implementer" do
     impl = Class.new {
       attr_accessor :result
       include ValueReceivingInterface
 
       def receiveByte(obj)
         self.result = obj
         obj
       end
 
       alias_method :receiveByteObj, :receiveByte
 
       %w[Short Char Int Long Float Double True False].each do |type|
         alias_method "receive#{type}".intern, :receiveByte
         alias_method "receive#{type}Obj".intern, :receiveByte
       end
     }
 
     vri = impl.new
     vri_handler = ValueReceivingInterfaceHandler.new(vri);
 
     obj = 1
 
     vri_handler.receiveByteObj(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Fixnum
 
     vri_handler.receiveShortObj(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Fixnum
 
     vri_handler.receiveCharObj(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Fixnum
 
     vri_handler.receiveIntObj(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Fixnum
 
     vri_handler.receiveLongObj(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Fixnum
 
     vri_handler.receiveFloatObj(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Float
 
     vri_handler.receiveDoubleObj(obj).should == obj
     vri.result.should == obj
     vri.result.class.should == Float
 
     vri_handler.receiveTrueObj(true).should == true
     vri.result.should == true
     vri.result.class.should == TrueClass
 
     vri_handler.receiveFalseObj(false).should == false
     vri.result.should == false
     vri.result.class.should == FalseClass
   end
 
   it "should coerce to null" do
     impl = Class.new {
       attr_accessor :result
       include ValueReceivingInterface
 
       def receive_primitive_box(obj)
         self.result = obj
         nil
       end
 
       %w[Byte Short Char Int Long Float Double True False].each do |type|
         alias_method :"receive#{type}Obj", :receive_primitive_box
       end
     }
 
     vri = impl.new
     vri_handler = ValueReceivingInterfaceHandler.new(vri);
 
     obj = 1
 
     vri_handler.receiveByteObj(nil).should == nil
     vri.result.should == nil
     vri.result.class.should == NilClass
 
     vri_handler.receiveShortObj(nil).should == nil
     vri.result.should == nil
     vri.result.class.should == NilClass
 
     vri_handler.receiveCharObj(nil).should == nil
     vri.result.should == nil
     vri.result.class.should == NilClass
 
     vri_handler.receiveIntObj(nil).should == nil
     vri.result.should == nil
     vri.result.class.should == NilClass
 
     vri_handler.receiveLongObj(nil).should == nil
     vri.result.should == nil
     vri.result.class.should == NilClass
 
     vri_handler.receiveFloatObj(nil).should == nil
     vri.result.should == nil
     vri.result.class.should == NilClass
 
     vri_handler.receiveDoubleObj(nil).should == nil
     vri.result.should == nil
     vri.result.class.should == NilClass
 
     vri_handler.receiveTrueObj(nil).should == nil
     vri.result.should == nil
     vri.result.class.should == NilClass
 
     vri_handler.receiveFalseObj(nil).should == nil
     vri.result.should == nil
     vri.result.class.should == NilClass
   end
 end
 
 describe "Java types with package or private constructors" do
   it "should not be constructible" do
     lambda { PackageConstructor.new }.should raise_error(TypeError)
     lambda { PrivateConstructor.new }.should raise_error(TypeError)
   end
 end
 
 describe "Java types with protected constructors" do
   it "should not be constructible" do
     lambda { ProtectedConstructor.new }.should raise_error(TypeError)
   end
 end
 
 describe "Fixnum\#to_java" do
   it "should coerce to java.lang.Long by default" do
     long = 123.to_java
     long.class.should == java.lang.Long
   end
 
   it "should allow coercing to other primitive types using symbolic names" do
     byte = 123.to_java :byte
     short = 123.to_java :short
     char = 123.to_java :char
     int = 123.to_java :int
     long = 123.to_java :long
     float = 123.to_java :float
     double = 123.to_java :double
 
     byte.class.should == java.lang.Byte
     short.class.should == java.lang.Short
     char.class.should == java.lang.Character
     int.class.should == java.lang.Integer
     long.class.should == java.lang.Long
     float.class.should == java.lang.Float
     double.class.should == java.lang.Double
   end
   
   it "coerces to java.lang.Long when asked to coerce to java.lang.Object" do
     obj = 123.to_java java.lang.Object
     obj2 = 123.to_java :object
 
     obj.class.should == java.lang.Long
     obj2.class.should == java.lang.Long
   end
 
   it "should allow coercing to other primitive types using boxed classes" do
     byte = 123.to_java java.lang.Byte
     short = 123.to_java java.lang.Short
     char = 123.to_java java.lang.Character
     int = 123.to_java java.lang.Integer
     long = 123.to_java java.lang.Long
     float = 123.to_java java.lang.Float
     double = 123.to_java java.lang.Double
 
     byte.class.should == java.lang.Byte
     short.class.should == java.lang.Short
     char.class.should == java.lang.Character
     int.class.should == java.lang.Integer
     long.class.should == java.lang.Long
     float.class.should == java.lang.Float
     double.class.should == java.lang.Double
   end
 
   it "should allow coercing to other primitive types using boxed classes" do
     byte = 123.to_java Java::byte
     short = 123.to_java Java::short
     char = 123.to_java Java::char
     int = 123.to_java Java::int
     long = 123.to_java Java::long
     float = 123.to_java Java::float
     double = 123.to_java Java::double
 
     byte.class.should == java.lang.Byte
     short.class.should == java.lang.Short
     char.class.should == java.lang.Character
     int.class.should == java.lang.Integer
     long.class.should == java.lang.Long
     float.class.should == java.lang.Float
     double.class.should == java.lang.Double
   end
 end
 
 describe "String\#to_java" do
   it "coerces to java.lang.String by default" do
     str = "123".to_java
     str.class.should == java.lang.String
   end
-  
-  it "coerces to java.lang.String when Object or CharSequence are requested" do
-    obj = "123".to_java java.lang.Object
-    cs = "123".to_java java.lang.CharSequence
 
-    obj.class.should == java.lang.String
-    cs.class.should == java.lang.String
+  describe "when passed java.lang.String" do
+    it "coerces to java.lang.String" do
+      cs = "123".to_java java.lang.String
+
+      cs.class.should == java.lang.String
+    end
+  end
+
+  describe "when passed java.lang.CharSequence" do
+    it "coerces to java.lang.String" do
+      cs = "123".to_java java.lang.CharSequence
+
+      cs.class.should == java.lang.String
+    end
+  end
+
+  describe "when passed java.lang.Object" do
+    it "coerces to java.lang.String" do
+      cs = "123".to_java java.lang.Object
+
+      cs.class.should == java.lang.String
+    end
+  end
+
+  describe "when passed void (java.lang.Void.TYPE)" do
+    it "coerces to null" do
+      cs = "123".to_java Java::java.lang.Void::TYPE
+
+      cs.class.should == NilClass
+    end
   end
 end
 
 describe "Class\#to_java" do
   describe "when passed java.lang.Class.class" do
     cls = java.lang.Class
     it "coerces core classes to their Java class object" do
       # TODO: add all core, native types here
       [Object, Array, String, Hash, File, IO].each do |rubycls|
         rubycls.to_java(cls).should == eval("cls.forName('org.jruby.Ruby#{rubycls}')")
       end
     end
 
     it "provides nearest reified class for unreified user classes" do
       rubycls = Class.new
       rubycls.to_java(cls).should == cls.forName('org.jruby.RubyObject');
     end
 
     it "returns reified class for reified used classes" do
       rubycls = Class.new
       rubycls.become_java!
       rubycls.to_java(cls).should == JRuby.reference(rubycls).reified_class
     end
 
     it "converts Java proxy classes to their JavaClass/java.lang.Class equivalent" do
       java.util.ArrayList.to_java(cls).should == java.util.ArrayList.java_class
     end
   end
 
   describe "when passed java.lang.Object.class" do
     cls = java.lang.Object
     it "coerces core classes to their Ruby class object" do
       # TODO: add all core, native types here
       [Object, Array, String, Hash, File, IO].each do |rubycls|
         rubycls.to_java(cls).should == rubycls
       end
     end
 
     it "coerces user classes to their Ruby class object" do
       rubycls = Class.new
       rubycls.to_java(cls).should == rubycls;
     end
 
     it "converts Java proxy classes to their proxy class (Ruby class) equivalent" do
       java.util.ArrayList.to_java(cls).should == java.util.ArrayList
     end
   end
+end
+
+describe "Time\"to_java" do
+  describe "when passed java.util.Date" do
+    it "coerces to java.util.Date" do
+      t = Time.now
+      d = t.to_java(java.util.Date)
+      d.class.should == java.util.Date
+    end
+  end
+
+  describe "when passed java.util.Calendar" do
+    it "coerces to java.util.Calendar" do
+      t = Time.now
+      d = t.to_java(java.util.Calendar)
+      d.class.should < java.util.Calendar
+    end
+  end
+
+  describe "when passed java.sql.Date" do
+    it "coerces to java.sql.Date" do
+      t = Time.now
+      d = t.to_java(java.sql.Date)
+      d.class.should == java.sql.Date
+    end
+  end
+
+  describe "when passed java.sql.Time" do
+    it "coerces to java.sql.Time" do
+      t = Time.now
+      d = t.to_java(java.sql.Time)
+      d.class.should == java.sql.Time
+    end
+  end
+
+  describe "when passed java.sql.Timestamp" do
+    it "coerces to java.sql.Timestamp" do
+      t = Time.now
+      d = t.to_java(java.sql.Timestamp)
+      d.class.should == java.sql.Timestamp
+    end
+  end
+
+  describe "when passed org.joda.time.DateTime" do
+    it "coerces to org.joda.time.DateTime" do
+      t = Time.now
+      d = t.to_java(org.joda.time.DateTime)
+      d.class.should == org.joda.time.DateTime
+    end
+  end
 end
\ No newline at end of file
diff --git a/src/org/jruby/RubyBasicObject.java b/src/org/jruby/RubyBasicObject.java
index 92f8b3ce27..cbe85c2a0e 100644
--- a/src/org/jruby/RubyBasicObject.java
+++ b/src/org/jruby/RubyBasicObject.java
@@ -1,1545 +1,1550 @@
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
  * Copyright (C) 2008 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.atomic.AtomicBoolean;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.InstanceVariables;
 import org.jruby.runtime.builtin.InternalVariables;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.CoreObjectType;
 import org.jruby.util.IdUtil;
 import org.jruby.util.TypeConverter;
 
 /**
  *
  * @author enebo
  */
 public class RubyBasicObject implements Cloneable, IRubyObject, Serializable, Comparable<IRubyObject>, CoreObjectType, InstanceVariables, InternalVariables {
     private static final boolean DEBUG = false;
     private static final Object[] NULL_OBJECT_ARRAY = new Object[0];
     
     // The class of this object
     protected transient RubyClass metaClass;
 
     // zeroed by jvm
     protected int flags;
 
     // variable table, lazily allocated as needed (if needed)
     private volatile Object[] varTable = NULL_OBJECT_ARRAY;
 
     /**
      * The error message used when some one tries to modify an
      * instance variable in a high security setting.
      */
     protected static final String ERR_INSECURE_SET_INST_VAR  = "Insecure: can't modify instance variable";
 
     public static final int ALL_F = -1;
     public static final int FALSE_F = 1 << 0;
     /**
      * This flag is a bit funny. It's used to denote that this value
      * is nil. It's a bit counterintuitive for a Java programmer to
      * not use subclassing to handle this case, since we have a
      * RubyNil subclass anyway. Well, the reason for it being a flag
      * is that the {@link #isNil()} method is called extremely often. So often
      * that it gives a good speed boost to make it monomorphic and
      * final. It turns out using a flag for this actually gives us
      * better performance than having a polymorphic {@link #isNil()} method.
      */
     public static final int NIL_F = 1 << 1;
     public static final int FROZEN_F = 1 << 2;
     public static final int TAINTED_F = 1 << 3;
     public static final int UNTRUSTED_F = 1 << 4;
 
     public static final int FL_USHIFT = 5;
 
     public static final int USER0_F = (1<<(FL_USHIFT+0));
     public static final int USER1_F = (1<<(FL_USHIFT+1));
     public static final int USER2_F = (1<<(FL_USHIFT+2));
     public static final int USER3_F = (1<<(FL_USHIFT+3));
     public static final int USER4_F = (1<<(FL_USHIFT+4));
     public static final int USER5_F = (1<<(FL_USHIFT+5));
     public static final int USER6_F = (1<<(FL_USHIFT+6));
     public static final int USER7_F = (1<<(FL_USHIFT+7));
 
     public static final int COMPARE_BY_IDENTITY_F = (1<<(FL_USHIFT+8));
 
     /**
      *  A value that is used as a null sentinel in among other places
      *  the RubyArray implementation. It will cause large problems to
      *  call any methods on this object.
      */
     public static final IRubyObject NEVER = new RubyBasicObject();
 
     /**
      * A value that specifies an undefined value. This value is used
      * as a sentinel for undefined constant values, and other places
      * where neither null nor NEVER makes sense.
      */
     public static final IRubyObject UNDEF = new RubyBasicObject();
 
     /**
      * It's not valid to create a totally empty RubyObject. Since the
      * RubyObject is always defined in relation to a runtime, that
      * means that creating RubyObjects from outside the class might
      * cause problems.
      */
     private RubyBasicObject(){};
 
     /**
      * Default allocator instance for all Ruby objects. The only
      * reason to not use this allocator is if you actually need to
      * have all instances of something be a subclass of RubyObject.
      *
      * @see org.jruby.runtime.ObjectAllocator
      */
     public static final ObjectAllocator OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyBasicObject(runtime, klass);
         }
     };
 
     /**
      * Will create the Ruby class Object in the runtime
      * specified. This method needs to take the actual class as an
      * argument because of the Object class' central part in runtime
      * initialization.
      */
     public static RubyClass createBasicObjectClass(Ruby runtime, RubyClass objectClass) {
         objectClass.index = ClassIndex.OBJECT;
 
         objectClass.defineAnnotatedMethods(BasicObjectMethods.class);
         objectClass.defineAnnotatedMethods(RubyBasicObject.class);
 
         return objectClass;
     }
 
     /**
      * Interestingly, the Object class doesn't really have that many
      * methods for itself. Instead almost all of the Object methods
      * are really defined on the Kernel module. This class is a holder
      * for all Object methods.
      *
      * @see RubyKernel
      */
     public static class BasicObjectMethods {
         @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE)
         public static IRubyObject intialize(IRubyObject self) {
             return self.getRuntime().getNil();
         }
     }
 
     /**
      * Standard path for object creation. Objects are entered into ObjectSpace
      * only if ObjectSpace is enabled.
      */
     public RubyBasicObject(Ruby runtime, RubyClass metaClass) {
         assert metaClass != null: "NULL Metaclass!!?!?!";
 
         this.metaClass = metaClass;
 
         if (runtime.isObjectSpaceEnabled()) addToObjectSpace(runtime);
         if (runtime.getSafeLevel() >= 3) taint(runtime);
     }
 
     /**
      * Path for objects that don't taint and don't enter objectspace.
      */
     public RubyBasicObject(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * Path for objects who want to decide whether they don't want to be in
      * ObjectSpace even when it is on. (notably used by objects being
      * considered immediate, they'll always pass false here)
      */
     protected RubyBasicObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace, boolean canBeTainted) {
         this.metaClass = metaClass;
 
         if (useObjectSpace) addToObjectSpace(runtime);
         if (canBeTainted && runtime.getSafeLevel() >= 3) taint(runtime);
     }
 
     protected RubyBasicObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         this.metaClass = metaClass;
 
         if (useObjectSpace) addToObjectSpace(runtime);
         if (runtime.getSafeLevel() >= 3) taint(runtime);
     }
 
     private void addToObjectSpace(Ruby runtime) {
         assert runtime.isObjectSpaceEnabled();
         runtime.getObjectSpace().add(this);
     }
 
     protected void taint(Ruby runtime) {
         runtime.secure(4);
         if (!isTaint()) {
         	testFrozen();
             setTaint(true);
         }
     }
 
     /** rb_frozen_class_p
      *
      * Helper to test whether this object is frozen, and if it is will
      * throw an exception based on the message.
      */
    protected final void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message);
        }
    }
 
     /** rb_frozen_class_p
      *
      * Helper to test whether this object is frozen, and if it is will
      * throw an exception based on the message.
      */
    protected final void testFrozen() {
        if (isFrozen()) {
            throw getRuntime().newFrozenError("object");
        }
    }
 
     /**
      * Sets or unsets a flag on this object. The only flags that are
      * guaranteed to be valid to use as the first argument is:
      *
      * <ul>
      *  <li>{@link #FALSE_F}</li>
      *  <li>{@link NIL_F}</li>
      *  <li>{@link FROZEN_F}</li>
      *  <li>{@link TAINTED_F}</li>
      *  <li>{@link USER0_F}</li>
      *  <li>{@link USER1_F}</li>
      *  <li>{@link USER2_F}</li>
      *  <li>{@link USER3_F}</li>
      *  <li>{@link USER4_F}</li>
      *  <li>{@link USER5_F}</li>
      *  <li>{@link USER6_F}</li>
      *  <li>{@link USER7_F}</li>
      * </ul>
      *
      * @param flag the actual flag to set or unset.
      * @param set if true, the flag will be set, if false, the flag will be unset.
      */
     public final void setFlag(int flag, boolean set) {
         if (set) {
             flags |= flag;
         } else {
             flags &= ~flag;
         }
     }
 
     /**
      * Get the value of a custom flag on this object. The only
      * guaranteed flags that can be sent in to this method is:
      *
      * <ul>
      *  <li>{@link #FALSE_F}</li>
      *  <li>{@link NIL_F}</li>
      *  <li>{@link FROZEN_F}</li>
      *  <li>{@link TAINTED_F}</li>
      *  <li>{@link USER0_F}</li>
      *  <li>{@link USER1_F}</li>
      *  <li>{@link USER2_F}</li>
      *  <li>{@link USER3_F}</li>
      *  <li>{@link USER4_F}</li>
      *  <li>{@link USER5_F}</li>
      *  <li>{@link USER6_F}</li>
      *  <li>{@link USER7_F}</li>
      * </ul>
      *
      * @param flag the flag to get
      * @return true if the flag is set, false otherwise
      */
     public final boolean getFlag(int flag) {
         return (flags & flag) != 0;
     }
 
     /**
      * See org.jruby.javasupport.util.RuntimeHelpers#invokeSuper
      */
     @Deprecated
     public IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block) {
         return RuntimeHelpers.invokeSuper(context, this, args, block);
     }
 
     /**
      * Will invoke a named method with no arguments and no block if that method or a custom
      * method missing exists. Otherwise returns null. 1.9: rb_check_funcall
      */
     public final IRubyObject checkCallMethod(ThreadContext context, String name) {
         return RuntimeHelpers.invokeChecked(context, this, name);
     }
 
     /**
      * Will invoke a named method with no arguments and no block.
      */
     public final IRubyObject callMethod(ThreadContext context, String name) {
         return RuntimeHelpers.invoke(context, this, name);
     }
 
     /**
      * Will invoke a named method with one argument and no block with
      * functional invocation.
      */
      public final IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, this, name, arg);
     }
 
     /**
      * Will invoke a named method with the supplied arguments and no
      * block with functional invocation.
      */
     public final IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return RuntimeHelpers.invoke(context, this, name, args);
     }
 
     public final IRubyObject callMethod(String name, IRubyObject... args) {
         return RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, name, args);
     }
 
     public final IRubyObject callMethod(String name) {
         return RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, name);
     }
 
     /**
      * Will invoke a named method with the supplied arguments and
      * supplied block with functional invocation.
      */
     public final IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return RuntimeHelpers.invoke(context, this, name, args, block);
     }
 
     /**
      * Will invoke an indexed method with the no arguments and no
      * block.
      */
     @Deprecated
     public final IRubyObject callMethod(ThreadContext context, int methodIndex, String name) {
         return RuntimeHelpers.invoke(context, this, name);
     }
 
     /**
      * Will invoke an indexed method with the one argument and no
      * block with a functional invocation.
      */
     @Deprecated
     public final IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, this, name, arg, Block.NULL_BLOCK);
     }
 
 
     /**
      * Does this object represent nil? See the docs for the {@link
      * #NIL_F} flag for more information.
      */
     public final boolean isNil() {
         return (flags & NIL_F) != 0;
     }
 
     /**
      * Is this value a true value or not? Based on the {@link #FALSE_F} flag.
      */
     public final boolean isTrue() {
         return (flags & FALSE_F) == 0;
     }
 
     /**
      * Is this value a false value or not? Based on the {@link #FALSE_F} flag.
      */
     public final boolean isFalse() {
         return (flags & FALSE_F) != 0;
     }
 
     /**
      * Gets the taint. Shortcut for getFlag(TAINTED_F).
      *
      * @return true if this object is tainted
      */
     public boolean isTaint() {
         return (flags & TAINTED_F) != 0;
     }
 
     /**
      * Sets the taint flag. Shortcut for setFlag(TAINTED_F, taint)
      *
      * @param taint should this object be tainted or not?
      */
     public void setTaint(boolean taint) {
         // JRUBY-4113: callers should not call setTaint on immediate objects
         if (isImmediate()) return;
         
         if (taint) {
             flags |= TAINTED_F;
         } else {
             flags &= ~TAINTED_F;
         }
     }
 
 
     /** OBJ_INFECT
      *
      * Infects this object with traits from the argument obj. In real
      * terms this currently means that if obj is tainted, this object
      * will get tainted too. It's possible to hijack this method to do
      * other infections if that would be interesting.
      */
     public IRubyObject infectBy(IRubyObject obj) {
         if (obj.isTaint()) setTaint(true);
         if (obj.isUntrusted()) setUntrusted(true);
         return this;
     }
 
     final RubyBasicObject infectBy(RubyBasicObject obj) {
         flags |= (obj.flags & (TAINTED_F | UNTRUSTED_F));
         return this;
     }
 
     final RubyBasicObject infectBy(int tuFlags) {
         flags |= (tuFlags & (TAINTED_F | UNTRUSTED_F));
         return this;
     }
 
     /**
      * Is this value frozen or not? Shortcut for doing
      * getFlag(FROZEN_F).
      *
      * @return true if this object is frozen, false otherwise
      */
     public boolean isFrozen() {
         return (flags & FROZEN_F) != 0;
     }
 
     /**
      * Sets whether this object is frozen or not. Shortcut for doing
      * setFlag(FROZEN_F, frozen).
      *
      * @param frozen should this object be frozen?
      */
     public void setFrozen(boolean frozen) {
         if (frozen) {
             flags |= FROZEN_F;
         } else {
             flags &= ~FROZEN_F;
         }
     }
 
 
     /**
      * Is this value untrusted or not? Shortcut for doing
      * getFlag(UNTRUSTED_F).
      *
      * @return true if this object is frozen, false otherwise
      */
     public boolean isUntrusted() {
         return (flags & UNTRUSTED_F) != 0;
     }
 
     /**
      * Sets whether this object is frozen or not. Shortcut for doing
      * setFlag(FROZEN_F, frozen).
      *
      * @param frozen should this object be frozen?
      */
     public void setUntrusted(boolean untrusted) {
         if (untrusted) {
             flags |= UNTRUSTED_F;
         } else {
             flags &= ~UNTRUSTED_F;
         }
     }
 
     /**
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public final RubyClass getMetaClass() {
         return metaClass;
     }
 
     /** rb_singleton_class
      *
      * Note: this method is specialized for RubyFixnum, RubySymbol,
      * RubyNil and RubyBoolean
      *
      * Will either return the existing singleton class for this
      * object, or create a new one and return that.
      */
     public RubyClass getSingletonClass() {
         RubyClass klass;
 
         if (getMetaClass().isSingleton() && ((MetaClass)getMetaClass()).getAttached() == this) {
             klass = getMetaClass();
         } else {
             klass = makeMetaClass(getMetaClass());
         }
 
         klass.setTaint(isTaint());
         if (isFrozen()) klass.setFrozen(true);
 
         return klass;
     }
 
     /** rb_make_metaclass
      *
      * Will create a new meta class, insert this in the chain of
      * classes for this specific object, and return the generated meta
      * class.
      */
     public RubyClass makeMetaClass(RubyClass superClass) {
         MetaClass klass = new MetaClass(getRuntime(), superClass, this); // rb_class_boot
         setMetaClass(klass);
 
         klass.setMetaClass(superClass.getRealClass().getMetaClass());
 
         superClass.addSubclass(klass);
 
         return klass;
     }
 
     /**
      * Makes it possible to change the metaclass of an object. In
      * practice, this is a simple version of Smalltalks Become, except
      * that it doesn't work when we're dealing with subclasses. In
      * practice it's used to change the singleton/meta class used,
      * without changing the "real" inheritance chain.
      */
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return getMetaClass().getRealClass();
     }
 
     /**
      * Does this object respond to the specified message? Uses a
      * shortcut if it can be proved that respond_to? haven't been
      * overridden.
      */
     public final boolean respondsTo(String name) {
         if(getMetaClass().searchMethod("respond_to?") == getRuntime().getRespondToMethod()) {
             return getMetaClass().isMethodBound(name, false);
         } else {
             return callMethod(getRuntime().getCurrentContext(),"respond_to?",getRuntime().newSymbol(name)).isTrue();
         }
     }
 
     /**
      * Will return the runtime that this object is associated with.
      *
      * @return current runtime
      */
     public final Ruby getRuntime() {
         return getMetaClass().getClassRuntime();
     }
 
     /**
      * Will return the Java interface that most closely can represent
      * this object, when working through JAva integration
      * translations.
      */
     public Class getJavaClass() {
         Object obj = dataGetStruct();
         if (obj instanceof JavaObject) {
             return ((JavaObject)obj).getValue().getClass();
         }
         return getClass();
     }
 
     /** rb_to_id
      *
      * Will try to convert this object to a String using the Ruby
      * "to_str" if the object isn't already a String. If this still
      * doesn't work, will throw a Ruby TypeError.
      *
      */
     public String asJavaString() {
         IRubyObject asString = checkStringType();
         if(!asString.isNil()) return ((RubyString)asString).asJavaString();
         throw getRuntime().newTypeError(inspect().toString() + " is not a string");
     }
 
     /** rb_obj_as_string
      *
      * First converts this object into a String using the "to_s"
      * method, infects it with the current taint and returns it. If
      * to_s doesn't return a Ruby String, {@link #anyToString} is used
      * instead.
      */
     public RubyString asString() {
         IRubyObject str = RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "to_s");
 
         if (!(str instanceof RubyString)) return (RubyString)anyToString();
         if (isTaint()) str.setTaint(true);
         return (RubyString) str;
     }
 
  /**
      * Tries to convert this object to a Ruby Array using the "to_ary"
      * method.
      */
     public RubyArray convertToArray() {
         return (RubyArray) TypeConverter.convertToType(this, getRuntime().getArray(), "to_ary");
     }
 
     /**
      * Tries to convert this object to a Ruby Hash using the "to_hash"
      * method.
      */
     public RubyHash convertToHash() {
         return (RubyHash)TypeConverter.convertToType(this, getRuntime().getHash(), "to_hash");
     }
 
     /**
      * Tries to convert this object to a Ruby Float using the "to_f"
      * method.
      */
     public RubyFloat convertToFloat() {
         return (RubyFloat) TypeConverter.convertToType(this, getRuntime().getFloat(), "to_f");
     }
 
     /**
      * Tries to convert this object to a Ruby Integer using the "to_int"
      * method.
      */
     public RubyInteger convertToInteger() {
         return convertToInteger("to_int");
     }
 
     @Deprecated
     public RubyInteger convertToInteger(int methodIndex, String convertMethod) {
         return convertToInteger(convertMethod);
     }
 
     /**
      * Tries to convert this object to a Ruby Integer using the
      * supplied conversion method.
      */
     public RubyInteger convertToInteger(String convertMethod) {
         IRubyObject val = TypeConverter.convertToType(this, getRuntime().getInteger(), convertMethod, true);
         if (!(val instanceof RubyInteger)) throw getRuntime().newTypeError(getMetaClass().getName() + "#" + convertMethod + " should return Integer");
         return (RubyInteger)val;
     }
 
     /**
      * Tries to convert this object to a Ruby String using the
      * "to_str" method.
      */
     public RubyString convertToString() {
         return (RubyString) TypeConverter.convertToType(this, getRuntime().getString(), "to_str");
     }
 
     /**
      * Internal method that helps to convert any object into the
      * format of a class name and a hex string inside of #<>.
      */
     public IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     /** rb_check_string_type
      *
      * Tries to return a coerced string representation of this object,
      * using "to_str". If that returns something other than a String
      * or nil, an empty String will be returned.
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = TypeConverter.convertToTypeWithCheck(this, getRuntime().getString(), "to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = RubyString.newEmptyString(getRuntime());
         }
         return str;
     }
 
     /** rb_check_string_type
      *
      * Tries to return a coerced string representation of this object,
      * using "to_str". If that returns something other than a String
      * or nil, an empty String will be returned.
      *
      */
     public IRubyObject checkStringType19() {
         IRubyObject str = TypeConverter.convertToTypeWithCheck19(this, getRuntime().getString(), "to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = RubyString.newEmptyString(getRuntime());
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     * Returns the result of trying to convert this object to an Array
     * with "to_ary".
     */
     public IRubyObject checkArrayType() {
         return TypeConverter.convertToTypeWithCheck(this, getRuntime().getArray(), "to_ary");
     }
 
     // 1.9 rb_check_to_integer
     IRubyObject checkIntegerType(Ruby runtime, IRubyObject obj, String method) {
         if (obj instanceof RubyFixnum) return obj;
         IRubyObject conv = TypeConverter.convertToType(obj, getRuntime().getInteger(), method, false);
         return conv instanceof RubyInteger ? conv : obj.getRuntime().getNil();
     }
 
     /**
      * @see IRubyObject.toJava
      */
     public Object toJava(Class target) {
-        if (dataGetStruct() instanceof JavaObject) {
+        // for callers that unconditionally pass null retval type (JRUBY-4737)
+        if (target == void.class) return null;
+
+        if (target.isAssignableFrom(getClass())) {
+            return this;
+        } else if (dataGetStruct() instanceof JavaObject) {
             // for interface impls
 
             JavaObject innerWrapper = (JavaObject)dataGetStruct();
 
             // ensure the object is associated with the wrapper we found it in,
             // so that if it comes back we don't re-wrap it
-            getRuntime().getJavaSupport().getObjectProxyCache().put(innerWrapper.getValue(), this);
+            if (target.isAssignableFrom(innerWrapper.getValue().getClass())) {
+                getRuntime().getJavaSupport().getObjectProxyCache().put(innerWrapper.getValue(), this);
 
-            return innerWrapper.getValue();
+                return innerWrapper.getValue();
+            }
         } else {
             if (JavaUtil.isDuckTypeConvertable(getClass(), target)) {
                 if (!respondsTo("java_object")) {
                     return JavaUtil.convertProcToInterface(getRuntime().getCurrentContext(), this, target);
                 }
             }
-
-            // it's either as converted as we can make it via above logic or it's
-            // not one of the types we convert, so just pass it out as-is without wrapping
-            return this;
         }
+        
+        throw getRuntime().newTypeError("cannot convert instance of " + getClass() + " to " + target);
     }
 
     public IRubyObject dup() {
         if (isImmediate()) throw getRuntime().newTypeError("can't dup " + getMetaClass().getName());
 
         IRubyObject dup = getMetaClass().getRealClass().allocate();
         if (isTaint()) dup.setTaint(true);
         if (isUntrusted()) dup.setUntrusted(true);
 
         initCopy(dup, this);
 
         return dup;
     }
 
     /** init_copy
      *
      * Initializes a copy with variable and special instance variable
      * information, and then call the initialize_copy Ruby method.
      */
     private static void initCopy(IRubyObject clone, IRubyObject original) {
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         original.copySpecialInstanceVariables(clone);
 
         if (original.hasVariables()) clone.syncVariables(original.getVariableList());
         if (original instanceof RubyModule) ((RubyModule) clone).syncConstants((RubyModule) original);
 
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /**
      * Lots of MRI objects keep their state in non-lookupable ivars
      * (e:g. Range, Struct, etc). This method is responsible for
      * dupping our java field equivalents
      */
     public void copySpecialInstanceVariables(IRubyObject clone) {
     }
 
     /** rb_inspect
      *
      * The internal helper that ensures a RubyString instance is returned
      * so dangerous casting can be omitted
      * Prefered over callMethod(context, "inspect")
      */
     static RubyString inspect(ThreadContext context, IRubyObject object) {
         return RubyString.objAsString(context, object.callMethod(context, "inspect"));
     }
 
     public IRubyObject rbClone() {
         if (isImmediate()) throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
 
         // We're cloning ourselves, so we know the result should be a RubyObject
         RubyObject clone = (RubyObject)getMetaClass().getRealClass().allocate();
         clone.setMetaClass(getSingletonClassClone());
         if (isTaint()) clone.setTaint(true);
 
         initCopy(clone, this);
 
         if (isFrozen()) clone.setFrozen(true);
         if (isUntrusted()) clone.setUntrusted(true);
         return clone;
     }
 
     /** rb_singleton_class_clone
      *
      * Will make sure that if the current objects class is a
      * singleton, it will get cloned.
      *
      * @return either a real class, or a clone of the current singleton class
      */
     protected RubyClass getSingletonClassClone() {
         RubyClass klass = getMetaClass();
 
         if (!klass.isSingleton()) {
             return klass;
         }
 
         MetaClass clone = new MetaClass(getRuntime(), klass.getSuperClass(), ((MetaClass) klass).getAttached());
         clone.flags = flags;
 
         if (this instanceof RubyClass) {
             clone.setMetaClass(clone);
         } else {
             clone.setMetaClass(klass.getSingletonClassClone());
         }
 
         if (klass.hasVariables()) {
             clone.syncVariables(klass.getVariableList());
         }
         clone.syncConstants(klass);
 
         klass.cloneMethods(clone);
 
         ((MetaClass) clone.getMetaClass()).setAttached(clone);
 
         return clone;
     }
 
     /**
      * Specifically polymorphic method that are meant to be overridden
      * by modules to specify that they are modules in an easy way.
      */
     public boolean isModule() {
         return false;
     }
 
     /**
      * Specifically polymorphic method that are meant to be overridden
      * by classes to specify that they are classes in an easy way.
      */
     public boolean isClass() {
         return false;
     }
 
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct()
      */
     public synchronized void dataWrapStruct(Object obj) {
         if (obj == null) {
             removeInternalVariable("__wrap_struct__");
         } else {
             fastSetInternalVariable("__wrap_struct__", obj);
         }
     }
 
     // The dataStruct is a place where custom information can be
     // contained for core implementations that doesn't necessarily
     // want to go to the trouble of creating a subclass of
     // RubyObject. The OpenSSL implementation uses this heavily to
     // save holder objects containing Java cryptography objects.
     // Java integration uses this to store the Java object ref.
     //protected transient Object dataStruct;
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataGetStruct()
      */
     public synchronized Object dataGetStruct() {
         return fastGetInternalVariable("__wrap_struct__");
     }
 
     // Equivalent of Data_Get_Struct
     // This will first check that the object in question is actually a T_DATA equivalent.
     public synchronized Object dataGetStructChecked() {
         TypeConverter.checkData(this);
         return this.fastGetInternalVariable("__wrap_struct__");
     }
 
     /** rb_obj_id
      *
      * Return the internal id of an object.
      */
     public IRubyObject id() {
         Ruby runtime = getRuntime();
         Long id;
 
         // The logic here is to use the special objectId accessor slot from the
         // parent as a lazy store for an object ID. IDs are generated atomically,
         // in serial, and guaranteed unique for up to 2^63 objects. The special
         // objectId slot is managed separately from the "normal" vars so it
         // does not marshal, clone/dup, or refuse to be initially set when the
         // object is frozen.
         synchronized (this) {
             RubyClass.VariableAccessor objectIdAccessor = getMetaClass().getRealClass().getObjectIdAccessorForWrite();
             id = (Long)objectIdAccessor.get(this);
             if (id == null) {
                 if (runtime.isObjectSpaceEnabled()) {
                     id = runtime.getObjectSpace().idOf(this);
                 } else {
                     id = ObjectSpace.calculateObjectID(this);
                 }
                 // we use a direct path here to avoid frozen checks
                 setObjectId(objectIdAccessor.getIndex(), id);
             }
         }
         return runtime.newFixnum(id);
     }
 
     /** rb_obj_inspect
      *
      *  call-seq:
      *     obj.inspect   => string
      *
      *  Returns a string containing a human-readable representation of
      *  <i>obj</i>. If not overridden, uses the <code>to_s</code> method to
      *  generate the string.
      *
      *     [ 1, 2, 3..4, 'five' ].inspect   #=> "[1, 2, 3..4, \"five\"]"
      *     Time.new.inspect                 #=> "Wed Apr 09 08:54:39 CDT 2003"
      */
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         if ((!isImmediate()) && !(this instanceof RubyModule) && hasVariables()) {
             return hashyInspect();
         }
 
         if (isNil()) return RubyNil.inspect(this);
         return RuntimeHelpers.invoke(runtime.getCurrentContext(), this, "to_s");
     }
 
     public IRubyObject hashyInspect() {
         Ruby runtime = getRuntime();
         StringBuilder part = new StringBuilder();
         String cname = getMetaClass().getRealClass().getName();
         part.append("#<").append(cname).append(":0x");
         part.append(Integer.toHexString(System.identityHashCode(this)));
 
         if (runtime.isInspecting(this)) {
             /* 6:tags 16:addr 1:eos */
             part.append(" ...>");
             return runtime.newString(part.toString());
         }
         try {
             runtime.registerInspecting(this);
             return runtime.newString(inspectObj(part).toString());
         } finally {
             runtime.unregisterInspecting(this);
         }
     }
 
     /** inspect_obj
      *
      * The internal helper method that takes care of the part of the
      * inspection that inspects instance variables.
      */
     private StringBuilder inspectObj(StringBuilder part) {
         ThreadContext context = getRuntime().getCurrentContext();
         String sep = "";
 
         for (Variable<IRubyObject> ivar : getInstanceVariableList()) {
             part.append(sep).append(" ").append(ivar.getName()).append("=");
             part.append(ivar.getValue().callMethod(context, "inspect"));
             sep = ",";
         }
         part.append(">");
         return part;
     }
 
     // Methods of the Object class (rb_obj_*):
 
 
     @JRubyMethod(name = "!", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_not(ThreadContext context) {
         return context.getRuntime().newBoolean(!this.isTrue());
     }
 
     @JRubyMethod(name = "!=", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_not_equal(ThreadContext context, IRubyObject other) {
         return context.getRuntime().newBoolean(!op_equal(context, other).isTrue());
     }
 
     public int compareTo(IRubyObject other) {
         return (int)callMethod(getRuntime().getCurrentContext(), "<=>", other).convertToInteger().getLongValue();
     }
 
     public IRubyObject op_equal(ThreadContext context, IRubyObject obj) {
         // Remain unimplemented due to problems with the double java hierarchy
         return context.getRuntime().getNil();
     }
 
     /** rb_obj_equal
      *
      * Will by default use identity equality to compare objects. This
      * follows the Ruby semantics.
      *
      * The name of this method doesn't follow the convention because hierarchy problems
      */
     @JRubyMethod(name = "==", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_equal_19(ThreadContext context, IRubyObject obj) {
         return this == obj ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
     }
 
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         // Remain unimplemented due to problems with the double java hierarchy
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "equal?", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject equal_p19(ThreadContext context, IRubyObject other) {
         return op_equal_19(context, other);
     }
 
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "==" method.
      */
     protected static boolean equalInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         return that == other || that.callMethod(context, "==", other).isTrue();
     }
 
     /** method used for Hash key comparison (specialized for String, Symbol and Fixnum)
      *
      * Will by default just call the Ruby method "eql?"
      */
     public boolean eql(IRubyObject other) {
         return callMethod(getRuntime().getCurrentContext(), "eql?", other).isTrue();
     }
 
     /**
      * Adds the specified object as a finalizer for this object.
      */
     public void addFinalizer(IRubyObject f) {
         Finalizer finalizer = (Finalizer)fastGetInternalVariable("__finalizer__");
         if (finalizer == null) {
             finalizer = new Finalizer(getRuntime().getObjectSpace().idOf(this));
             fastSetInternalVariable("__finalizer__", finalizer);
             getRuntime().addFinalizer(finalizer);
         }
         finalizer.addFinalizer(f);
     }
 
     /**
      * Remove all the finalizers for this object.
      */
     public void removeFinalizers() {
         Finalizer finalizer = (Finalizer)fastGetInternalVariable("__finalizer__");
         if (finalizer != null) {
             finalizer.removeFinalizers();
             removeInternalVariable("__finalizer__");
             getRuntime().removeFinalizer(finalizer);
         }
     }
 
     private Object[] getVariableTableForRead() {
         return varTable;
     }
 
     private synchronized Object[] getVariableTableForWrite(int index) {
         if (varTable == NULL_OBJECT_ARRAY) {
             if (DEBUG) System.out.println("resizing from " + varTable.length + " to " + getMetaClass().getRealClass().getVariableTableSizeWithObjectId());
             varTable = new Object[getMetaClass().getRealClass().getVariableTableSizeWithObjectId()];
         } else if (varTable.length <= index) {
             if (DEBUG) System.out.println("resizing from " + varTable.length + " to " + getMetaClass().getRealClass().getVariableTableSizeWithObjectId());
             Object[] newTable = new Object[getMetaClass().getRealClass().getVariableTableSizeWithObjectId()];
             System.arraycopy(varTable, 0, newTable, 0, varTable.length);
             varTable = newTable;
         }
         return varTable;
     }
 
     public Object getVariable(int index) {
         if (index < 0) return null;
         Object[] ivarTable = getVariableTableForRead();
         if (ivarTable.length > index) return ivarTable[index];
         return null;
     }
 
     public synchronized void setVariable(int index, Object value) {
         ensureInstanceVariablesSettable();
         if (index < 0) return;
         Object[] ivarTable = getVariableTableForWrite(index);
         ivarTable[index] = value;
     }
 
     private synchronized void setObjectId(int index, long value) {
         if (index < 0) return;
         Object[] ivarTable = getVariableTableForWrite(index);
         ivarTable[index] = value;
     }
 
     //
     // COMMON VARIABLE METHODS
     //
 
     /**
      * Returns true if object has any variables, defined as:
      * <ul>
      * <li> instance variables
      * <li> class variables
      * <li> constants
      * <li> internal variables, such as those used when marshaling Ranges and Exceptions
      * </ul>
      * @return true if object has any variables, else false
      */
     public boolean hasVariables() {
         // we check both to exclude object_id
         return getMetaClass().getRealClass().getVariableTableSize() > 0 && varTable.length > 0;
     }
 
     /**
      * Returns the amount of instance variables, class variables,
      * constants and internal variables this object has.
      */
     @Deprecated
     public int getVariableCount() {
         // we use min to exclude object_id
         return Math.min(varTable.length, getMetaClass().getRealClass().getVariableTableSize());
     }
 
     /**
      * Gets a list of all variables in this object.
      */
     // TODO: must override in RubyModule to pick up constants
     public List<Variable<Object>> getVariableList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getRealClass().getVariableAccessorsForRead();
         ArrayList<Variable<Object>> list = new ArrayList<Variable<Object>>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null) continue;
             list.add(new VariableEntry<Object>(entry.getKey(), value));
         }
         return list;
     }
 
     /**
      * Gets a name list of all variables in this object.
      */
    // TODO: must override in RubyModule to pick up constants
    public List<String> getVariableNameList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getRealClass().getVariableAccessorsForRead();
         ArrayList<String> list = new ArrayList<String>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null) continue;
             list.add(entry.getKey());
         }
         return list;
     }
 
     /**
      * Checks if the variable table contains a variable of the
      * specified name.
      */
     protected boolean variableTableContains(String name) {
         return getMetaClass().getRealClass().getVariableAccessorForRead(name).get(this) != null;
     }
 
     /**
      * Checks if the variable table contains the the variable of the
      * specified name, where the precondition is that the name must be
      * an interned Java String.
      */
     protected boolean variableTableFastContains(String internedName) {
         return variableTableContains(internedName);
     }
 
     /**
      * Fetch an object from the variable table based on the name.
      *
      * @return the object or null if not found
      */
     protected Object variableTableFetch(String name) {
         return getMetaClass().getRealClass().getVariableAccessorForRead(name).get(this);
     }
 
     /**
      * Fetch an object from the variable table based on the name,
      * where the name must be an interned Java String.
      *
      * @return the object or null if not found
      */
     protected Object variableTableFastFetch(String internedName) {
         return variableTableFetch(internedName);
     }
 
     /**
      * Store a value in the variable store under the specific name.
      */
     protected Object variableTableStore(String name, Object value) {
         getMetaClass().getRealClass().getVariableAccessorForWrite(name).set(this, value);
         return value;
     }
 
     /**
      * Will store the value under the specified name, where the name
      * needs to be an interned Java String.
      */
     protected Object variableTableFastStore(String internedName, Object value) {
         return variableTableStore(internedName, value);
     }
 
     /**
      * Removes the entry with the specified name from the variable
      * table, and returning the removed value.
      */
     protected Object variableTableRemove(String name) {
         synchronized(this) {
             Object value = getMetaClass().getRealClass().getVariableAccessorForRead(name).get(this);
             getMetaClass().getRealClass().getVariableAccessorForWrite(name).set(this, null);
             return value;
         }
     }
 
     /**
      * Synchronize the variable table with the argument. In real terms
      * this means copy all entries into a newly allocated table.
      */
     protected void variableTableSync(List<Variable<Object>> vars) {
         synchronized(this) {
             for (Variable<Object> var : vars) {
                 variableTableStore(var.getName(), var.getValue());
             }
         }
     }
 
     //
     // INTERNAL VARIABLE METHODS
     //
 
     /**
      * Dummy method to avoid a cast, and to avoid polluting the
      * IRubyObject interface with all the instance variable management
      * methods.
      */
     public InternalVariables getInternalVariables() {
         return this;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#hasInternalVariable
      */
     public boolean hasInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableContains(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#fastHasInternalVariable
      */
     public boolean fastHasInternalVariable(String internedName) {
         assert !IdUtil.isRubyVariable(internedName);
         return variableTableFastContains(internedName);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#getInternalVariable
      */
     public Object getInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableFetch(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#fastGetInternalVariable
      */
     public Object fastGetInternalVariable(String internedName) {
         assert !IdUtil.isRubyVariable(internedName);
         return variableTableFastFetch(internedName);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#setInternalVariable
      */
     public void setInternalVariable(String name, Object value) {
         assert !IdUtil.isRubyVariable(name);
         variableTableStore(name, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#fastSetInternalVariable
      */
     public void fastSetInternalVariable(String internedName, Object value) {
         assert !IdUtil.isRubyVariable(internedName);
         variableTableFastStore(internedName, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#removeInternalVariable
      */
     public Object removeInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableRemove(name);
     }
 
     /**
      * Sync one variable table with another - this is used to make
      * rbClone work correctly.
      */
     public void syncVariables(List<Variable<Object>> variables) {
         variableTableSync(variables);
     }
 
     //
     // INSTANCE VARIABLE API METHODS
     //
 
     /**
      * Dummy method to avoid a cast, and to avoid polluting the
      * IRubyObject interface with all the instance variable management
      * methods.
      */
     public InstanceVariables getInstanceVariables() {
         return this;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#hasInstanceVariable
      */
     public boolean hasInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         return variableTableContains(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#fastHasInstanceVariable
      */
     public boolean fastHasInstanceVariable(String internedName) {
         assert IdUtil.isInstanceVariable(internedName);
         return variableTableFastContains(internedName);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariable
      */
     public IRubyObject getInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         return (IRubyObject)variableTableFetch(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#fastGetInstanceVariable
      */
     public IRubyObject fastGetInstanceVariable(String internedName) {
         assert IdUtil.isInstanceVariable(internedName);
         return (IRubyObject)variableTableFastFetch(internedName);
     }
 
     /** rb_iv_set / rb_ivar_set
     *
     * @see org.jruby.runtime.builtin.InstanceVariables#setInstanceVariable
     */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         assert IdUtil.isInstanceVariable(name) && value != null;
         ensureInstanceVariablesSettable();
         return (IRubyObject)variableTableStore(name, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#fastSetInstanceVariable
      */
     public IRubyObject fastSetInstanceVariable(String internedName, IRubyObject value) {
         assert IdUtil.isInstanceVariable(internedName) && value != null;
         ensureInstanceVariablesSettable();
         return (IRubyObject)variableTableFastStore(internedName, value);
      }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#removeInstanceVariable
      */
     public IRubyObject removeInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         ensureInstanceVariablesSettable();
         return (IRubyObject)variableTableRemove(name);
     }
 
     /**
      * Gets a list of all variables in this object.
      */
     // TODO: must override in RubyModule to pick up constants
     public List<Variable<IRubyObject>> getInstanceVariableList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getVariableAccessorsForRead();
         ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null || !(value instanceof IRubyObject) || !IdUtil.isInstanceVariable(entry.getKey())) continue;
             list.add(new VariableEntry<IRubyObject>(entry.getKey(), (IRubyObject)value));
         }
         return list;
     }
 
     /**
      * Gets a name list of all variables in this object.
      */
    // TODO: must override in RubyModule to pick up constants
    public List<String> getInstanceVariableNameList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getRealClass().getVariableAccessorsForRead();
         ArrayList<String> list = new ArrayList<String>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null || !(value instanceof IRubyObject) || !IdUtil.isInstanceVariable(entry.getKey())) continue;
             list.add(entry.getKey());
         }
         return list;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariableNameList
      */
     public void copyInstanceVariablesInto(final InstanceVariables other) {
         for (Variable<IRubyObject> var : getInstanceVariableList()) {
             synchronized (this) {
                 other.setInstanceVariable(var.getName(), var.getValue());
             }
         }
     }
 
     /**
      * Makes sure that instance variables can be set on this object,
      * including information about whether this object is frozen, or
      * tainted. Will throw a suitable exception in that case.
      */
     protected final void ensureInstanceVariablesSettable() {
         if (!isFrozen() && (getRuntime().getSafeLevel() < 4 || isTaint())) {
             return;
         }
 
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError(ERR_INSECURE_SET_INST_VAR);
         }
         if (isFrozen()) {
             if (this instanceof RubyModule) {
                 throw getRuntime().newFrozenError("class/module ");
             } else {
                 throw getRuntime().newFrozenError("");
             }
         }
     }
 
     public int getNativeTypeIndex() {
         throw new UnsupportedOperationException("Not supported yet.");
     }
 
     /**
      * A method to determine whether the method named by methodName is a builtin
      * method.  This means a method with a JRubyMethod annotation written in
      * Java.
      *
      * @param methodName to look for.
      * @return true if so
      */
     public boolean isBuiltin(String methodName) {
         DynamicMethod method = getMetaClass().searchMethodInner(methodName);
 
         return method != null && method.isBuiltin();
     }
 
     /**
      * Class that keeps track of the finalizers for the object under
      * operation.
      */
     public class Finalizer implements Finalizable {
         private long id;
         private IRubyObject firstFinalizer;
         private List<IRubyObject> finalizers;
         private AtomicBoolean finalized;
 
         public Finalizer(long id) {
             this.id = id;
             this.finalized = new AtomicBoolean(false);
         }
 
         public void addFinalizer(IRubyObject finalizer) {
             if (firstFinalizer == null) {
                 firstFinalizer = finalizer;
             } else {
                 if (finalizers == null) finalizers = new ArrayList<IRubyObject>(4);
                 finalizers.add(finalizer);
             }
         }
 
         public void removeFinalizers() {
             firstFinalizer = null;
             finalizers = null;
         }
 
         @Override
         public void finalize() {
             if (finalized.compareAndSet(false, true)) {
                 if (firstFinalizer != null) callFinalizer(firstFinalizer);
                 if (finalizers != null) {
                     for (int i = 0; i < finalizers.size(); i++) {
                         callFinalizer(finalizers.get(i));
                     }
                 }
             }
         }
         
         private void callFinalizer(IRubyObject finalizer) {
             RuntimeHelpers.invoke(
                     finalizer.getRuntime().getCurrentContext(),
                     finalizer, "call", RubyBasicObject.this.id());
         }
     }
 }
diff --git a/src/org/jruby/RubyBoolean.java b/src/org/jruby/RubyBoolean.java
index b24a620aa6..8634f337b3 100644
--- a/src/org/jruby/RubyBoolean.java
+++ b/src/org/jruby/RubyBoolean.java
@@ -1,175 +1,180 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name={"TrueClass", "FalseClass"})
 public class RubyBoolean extends RubyObject {
     
     public RubyBoolean(Ruby runtime, boolean value) {
         super(runtime, (value ? runtime.getTrueClass() : runtime.getFalseClass()), // Don't initialize with class
                 false, false); // Don't put in object space and don't taint
 
         if (!value) flags = FALSE_F;
     }
     
     @Override
     public int getNativeTypeIndex() {
         return (flags & FALSE_F) == 0 ? ClassIndex.TRUE : ClassIndex.FALSE;
     }
     
     @Override
     public boolean isImmediate() {
         return true;
     }
 
     @Override
     public RubyClass getSingletonClass() {
         return metaClass;
     }
 
     @Override
     public Class<?> getJavaClass() {
         return boolean.class;
     }
 
     public static RubyClass createFalseClass(Ruby runtime) {
         RubyClass falseClass = runtime.defineClass("FalseClass", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setFalseClass(falseClass);
         falseClass.index = ClassIndex.FALSE;
         falseClass.setReifiedClass(RubyBoolean.class);
         
         falseClass.defineAnnotatedMethods(False.class);
         
         falseClass.getMetaClass().undefineMethod("new");
         
         return falseClass;
     }
     
     public static RubyClass createTrueClass(Ruby runtime) {
         RubyClass trueClass = runtime.defineClass("TrueClass", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setTrueClass(trueClass);
         trueClass.index = ClassIndex.TRUE;
         trueClass.setReifiedClass(RubyBoolean.class);
         
         trueClass.defineAnnotatedMethods(True.class);
         
         trueClass.getMetaClass().undefineMethod("new");
         
         return trueClass;
     }
     
     public static RubyBoolean newBoolean(Ruby runtime, boolean value) {
         return value ? runtime.getTrue() : runtime.getFalse();
     }
     
     public static class False {
         @JRubyMethod(name = "&")
         public static IRubyObject false_and(IRubyObject f, IRubyObject oth) {
             return f;
         }
 
         @JRubyMethod(name = "|")
         public static IRubyObject false_or(IRubyObject f, IRubyObject oth) {
             return oth.isTrue() ? f.getRuntime().getTrue() : f;
         }
 
         @JRubyMethod(name = "^")
         public static IRubyObject false_xor(IRubyObject f, IRubyObject oth) {
             return oth.isTrue() ? f.getRuntime().getTrue() : f;
         }
 
         @JRubyMethod(name = "to_s")
         public static IRubyObject false_to_s(IRubyObject f) {
             return f.getRuntime().newString("false");
         }
     }
     
     public static class True {
         @JRubyMethod(name = "&")
         public static IRubyObject true_and(IRubyObject t, IRubyObject oth) {
             return oth.isTrue() ? t : t.getRuntime().getFalse();
         }
 
         @JRubyMethod(name = "|")
         public static IRubyObject true_or(IRubyObject t, IRubyObject oth) {
             return t;
         }
 
         @JRubyMethod(name = "^")
         public static IRubyObject true_xor(IRubyObject t, IRubyObject oth) {
             return oth.isTrue() ? t.getRuntime().getFalse() : t;
         }
 
         @JRubyMethod(name = "to_s")
         public static IRubyObject true_to_s(IRubyObject t) {
             return t.getRuntime().newString("true");
         }
     }
     
     @Override
     public RubyFixnum id() {
         if ((flags & FALSE_F) == 0) {
             return RubyFixnum.newFixnum(getRuntime(), 2);
         } else {
             return RubyFixnum.zero(getRuntime());
         }
     }
 
     @Override
     public IRubyObject taint(ThreadContext context) {
         return this;
     }
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         output.write(isTrue() ? 'T' : 'F');
     }
 
+    @Override
     public Object toJava(Class target) {
-        if (isFalse()) return Boolean.FALSE;
+        if (target.isAssignableFrom(Boolean.class) || target.equals(boolean.class)) {
+            if (isFalse()) return Boolean.FALSE;
 
-        return Boolean.TRUE;
+            return Boolean.TRUE;
+        } else {
+            return super.toJava(target);
+        }
     }
 }
 
diff --git a/src/org/jruby/RubyClass.java b/src/org/jruby/RubyClass.java
index cd818cbf37..324f4f13bb 100644
--- a/src/org/jruby/RubyClass.java
+++ b/src/org/jruby/RubyClass.java
@@ -1,1415 +1,1413 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby;
 
 import java.io.IOException;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 
 import org.jruby.exceptions.RaiseException;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.java.codegen.RealClassGenerator;
 import org.jruby.javasupport.Java;
-import org.jruby.javasupport.JavaClass;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.JRubyClassLoader;
 import static org.jruby.util.CodegenUtils.*;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.collections.WeakHashSet;
 import org.objectweb.asm.AnnotationVisitor;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.MethodVisitor;
 
 import static org.objectweb.asm.Opcodes.*;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Class", parent="Module")
 public class RubyClass extends RubyModule {
     public static void createClassClass(Ruby runtime, RubyClass classClass) {
         classClass.index = ClassIndex.CLASS;
         classClass.setReifiedClass(RubyClass.class);
         classClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyClass;
             }
         };
         
         classClass.undefineMethod("module_function");
         classClass.undefineMethod("append_features");
         classClass.undefineMethod("extend_object");
         
         classClass.defineAnnotatedMethods(RubyClass.class);
         
         classClass.addMethod("new", new SpecificArityNew(classClass, Visibility.PUBLIC));
         
         // This is a non-standard method; have we decided to start extending Ruby?
         //classClass.defineFastMethod("subclasses", callbackFactory.getFastOptMethod("subclasses"));
         
         // FIXME: for some reason this dispatcher causes a VerifyError...
         //classClass.dispatcher = callbackFactory.createDispatcher(classClass);
     }
     
     public static final ObjectAllocator CLASS_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyClass clazz = new RubyClass(runtime);
             clazz.allocator = ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR; // Class.allocate object is not allocatable before it is initialized
             return clazz;
         }
     };
 
     public ObjectAllocator getAllocator() {
         return allocator;
     }
 
     public void setAllocator(ObjectAllocator allocator) {
         this.allocator = allocator;
     }
 
     /**
      * Set a reflective allocator that calls a no-arg constructor on the given
      * class.
      *
      * @param cls The class on which to call the default constructor to allocate
      */
     public void setClassAllocator(final Class cls) {
         this.allocator = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                 try {
                     return (IRubyObject)cls.newInstance();
                 } catch (InstantiationException ie) {
                     throw runtime.newTypeError("could not allocate " + cls + " with default constructor:\n" + ie);
                 } catch (IllegalAccessException iae) {
                     throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible default constructor:\n" + iae);
                 }
             }
         };
         
         this.reifiedClass = cls;
     }
 
     /**
      * Set a reflective allocator that calls the "standard" Ruby object
      * constructor (Ruby, RubyClass) on the given class.
      *
      * @param cls The class from which to grab a standard Ruby constructor
      */
     public void setRubyClassAllocator(final Class cls) {
         try {
             final Constructor constructor = cls.getConstructor(Ruby.class, RubyClass.class);
             
             this.allocator = new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                     try {
                         return (IRubyObject)constructor.newInstance(runtime, klazz);
                     } catch (InvocationTargetException ite) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ite);
                     } catch (InstantiationException ie) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ie);
                     } catch (IllegalAccessException iae) {
                         throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible (Ruby, RubyClass) constructor:\n" + iae);
                     }
                 }
             };
 
             this.reifiedClass = cls;
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 
     /**
      * Set a reflective allocator that calls the "standard" Ruby object
      * constructor (Ruby, RubyClass) on the given class via a static
      * __allocate__ method intermediate.
      *
      * @param cls The class from which to grab a standard Ruby __allocate__
      *            method.
      */
     public void setRubyStaticAllocator(final Class cls) {
         try {
             final Method method = cls.getDeclaredMethod("__allocate__", Ruby.class, RubyClass.class);
 
             this.allocator = new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                     try {
                         return (IRubyObject)method.invoke(null, runtime, klazz);
                     } catch (InvocationTargetException ite) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ite);
                     } catch (IllegalAccessException iae) {
                         throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible (Ruby, RubyClass) constructor:\n" + iae);
                     }
                 }
             };
 
             this.reifiedClass = cls;
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 
     @JRubyMethod(name = "allocate")
     public IRubyObject allocate() {
         if (superClass == null) {
             if(!(runtime.is1_9() && this == runtime.getBasicObject())) {
                 throw runtime.newTypeError("can't instantiate uninitialized class");
             }
         }
         IRubyObject obj = allocator.allocate(runtime, this);
         if (obj.getMetaClass().getRealClass() != getRealClass()) throw runtime.newTypeError("wrong instance allocation");
         return obj;
     }
 
     public CallSite[] getBaseCallSites() {
         return baseCallSites;
     }
     
     public CallSite[] getExtraCallSites() {
         return extraCallSites;
     }
 
     public static class VariableAccessor {
         private int index;
         private final int classId;
         public VariableAccessor(int index, int classId) {
             this.index = index;
             this.classId = classId;
         }
         public int getClassId() {
             return classId;
         }
         public int getIndex() {
             return index;
         }
         public Object get(Object object) {
             return ((IRubyObject)object).getVariable(index);
         }
         public void set(Object object, Object value) {
             ((IRubyObject)object).setVariable(index, value);
         }
         public static final VariableAccessor DUMMY_ACCESSOR = new VariableAccessor(-1, -1);
     }
 
     public Map<String, VariableAccessor> getVariableAccessorsForRead() {
         return variableAccessors;
     }
 
     public synchronized Map<String, VariableAccessor> getVariableAccessorsForWrite() {
         if (variableAccessors == Collections.EMPTY_MAP) variableAccessors = new Hashtable<String, VariableAccessor>(1);
         return variableAccessors;
     }
     
     private volatile int accessorCount = 0;
     private volatile VariableAccessor objectIdAccessor = VariableAccessor.DUMMY_ACCESSOR;
 
     private synchronized final VariableAccessor allocateVariableAccessor() {
         return new VariableAccessor(accessorCount++, this.id);
     }
 
     public synchronized VariableAccessor getVariableAccessorForWrite(String name) {
         Map<String, VariableAccessor> myVariableAccessors = getVariableAccessorsForWrite();
         VariableAccessor ivarAccessor = myVariableAccessors.get(name);
         if (ivarAccessor == null) {
             ivarAccessor = allocateVariableAccessor();
             myVariableAccessors.put(name, ivarAccessor);
         }
         return ivarAccessor;
     }
 
     public VariableAccessor getVariableAccessorForRead(String name) {
         VariableAccessor accessor = getVariableAccessorsForRead().get(name);
         if (accessor == null) accessor = VariableAccessor.DUMMY_ACCESSOR;
         return accessor;
     }
 
     public synchronized VariableAccessor getObjectIdAccessorForWrite() {
         if (objectIdAccessor == VariableAccessor.DUMMY_ACCESSOR) objectIdAccessor = allocateVariableAccessor();
         return objectIdAccessor;
     }
 
     public VariableAccessor getObjectIdAccessorForRead() {
         return objectIdAccessor;
     }
 
     public int getVariableTableSize() {
         return variableAccessors.size();
     }
 
     public int getVariableTableSizeWithObjectId() {
         return variableAccessors.size() + (objectIdAccessor == VariableAccessor.DUMMY_ACCESSOR ? 0 : 1);
     }
 
     public Map<String, VariableAccessor> getVariableTableCopy() {
         return new HashMap<String, VariableAccessor>(getVariableAccessorsForRead());
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.CLASS;
     }
     
     @Override
     public boolean isModule() {
         return false;
     }
 
     @Override
     public boolean isClass() {
         return true;
     }
 
     @Override
     public boolean isSingleton() {
         return false;
     }
 
     /** boot_defclass
      * Create an initial Object meta class before Module and Kernel dependencies have
      * squirreled themselves together.
      * 
      * @param runtime we need it
      * @return a half-baked meta class for object
      */
     public static RubyClass createBootstrapClass(Ruby runtime, String name, RubyClass superClass, ObjectAllocator allocator) {
         RubyClass obj;
 
         if (superClass == null ) {  // boot the Object class 
             obj = new RubyClass(runtime);
             obj.marshal = DEFAULT_OBJECT_MARSHAL;
         } else {                    // boot the Module and Class classes
             obj = new RubyClass(runtime, superClass);
         }
         obj.setAllocator(allocator);
         obj.setBaseName(name);
         return obj;
     }
 
     /** separate path for MetaClass and IncludedModuleWrapper construction
      *  (rb_class_boot version for MetaClasses)
      *  no marshal, allocator initialization and addSubclass(this) here!
      */
     protected RubyClass(Ruby runtime, RubyClass superClass, boolean objectSpace) {
         super(runtime, runtime.getClassClass(), objectSpace);
         this.runtime = runtime;
         setSuperClass(superClass); // this is the only case it might be null here (in MetaClass construction)
     }
 
     /** separate path for MetaClass and IncludedModuleWrapper construction
      *  (rb_class_boot version for MetaClasses)
      *  no marshal, allocator initialization and addSubclass(this) here!
      */
     protected RubyClass(Ruby runtime, RubyClass superClass, Generation generation, boolean objectSpace) {
         super(runtime, runtime.getClassClass(), generation, objectSpace);
         this.runtime = runtime;
         setSuperClass(superClass); // this is the only case it might be null here (in MetaClass construction)
     }
     
     /** used by CLASS_ALLOCATOR (any Class' class will be a Class!)
      *  also used to bootstrap Object class
      */
     protected RubyClass(Ruby runtime) {
         super(runtime, runtime.getClassClass());
         this.runtime = runtime;
         index = ClassIndex.CLASS;
     }
     
     /** rb_class_boot (for plain Classes)
      *  also used to bootstrap Module and Class classes 
      */
     protected RubyClass(Ruby runtime, RubyClass superClazz) {
         this(runtime);
         setSuperClass(superClazz);
         marshal = superClazz.marshal; // use parent's marshal
         superClazz.addSubclass(this);
         allocator = superClazz.allocator;
         
         infectBy(superClass);        
     }
     
     /** 
      * A constructor which allows passing in an array of supplementary call sites.
      */
     protected RubyClass(Ruby runtime, RubyClass superClazz, CallSite[] extraCallSites) {
         this(runtime);
         setSuperClass(superClazz);
         this.marshal = superClazz.marshal; // use parent's marshal
         superClazz.addSubclass(this);
         
         this.extraCallSites = extraCallSites;
         
         infectBy(superClass);        
     }
 
     /** 
      * Construct a new class with the given name scoped under Object (global)
      * and with Object as its immediate superclass.
      * Corresponds to rb_class_new in MRI.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass) {
         if (superClass == runtime.getClassClass()) throw runtime.newTypeError("can't make subclass of Class");
         if (superClass.isSingleton()) throw runtime.newTypeError("can't make subclass of virtual class");
         return new RubyClass(runtime, superClass);        
     }
 
     /** 
      * A variation on newClass that allow passing in an array of supplementary
      * call sites to improve dynamic invocation.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, CallSite[] extraCallSites) {
         if (superClass == runtime.getClassClass()) throw runtime.newTypeError("can't make subclass of Class");
         if (superClass.isSingleton()) throw runtime.newTypeError("can't make subclass of virtual class");
         return new RubyClass(runtime, superClass, extraCallSites);        
     }
 
     /** 
      * Construct a new class with the given name, allocator, parent class,
      * and containing class. If setParent is true, the class's parent will be
      * explicitly set to the provided parent (rather than the new class just
      * being assigned to a constant in that parent).
      * Corresponds to rb_class_new/rb_define_class_id/rb_name_class/rb_set_class_path
      * in MRI.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, String name, ObjectAllocator allocator, RubyModule parent, boolean setParent) {
         RubyClass clazz = newClass(runtime, superClass);
         clazz.setBaseName(name);
         clazz.setAllocator(allocator);
         clazz.makeMetaClass(superClass.getMetaClass());
         if (setParent) clazz.setParent(parent);
         parent.setConstant(name, clazz);
         clazz.inherit(superClass);
         return clazz;
     }
 
     /** 
      * A variation on newClass that allows passing in an array of supplementary
      * call sites to improve dynamic invocation performance.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, String name, ObjectAllocator allocator, RubyModule parent, boolean setParent, CallSite[] extraCallSites) {
         RubyClass clazz = newClass(runtime, superClass, extraCallSites);
         clazz.setBaseName(name);
         clazz.setAllocator(allocator);
         clazz.makeMetaClass(superClass.getMetaClass());
         if (setParent) clazz.setParent(parent);
         parent.setConstant(name, clazz);
         clazz.inherit(superClass);
         return clazz;
     }
 
     /** rb_make_metaclass
      *
      */
     @Override
     public RubyClass makeMetaClass(RubyClass superClass) {
         if (isSingleton()) { // could be pulled down to RubyClass in future
             MetaClass klass = new MetaClass(runtime, superClass, this); // rb_class_boot
             setMetaClass(klass);
 
             klass.setMetaClass(klass);
             klass.setSuperClass(getSuperClass().getRealClass().getMetaClass());
             
             return klass;
         } else {
             return super.makeMetaClass(superClass);
         }
     }
     
     @Deprecated
     public IRubyObject invoke(ThreadContext context, IRubyObject self, int methodIndex, String name, IRubyObject[] args, CallType callType, Block block) {
         return invoke(context, self, name, args, callType, block);
     }
     
     public boolean notVisibleAndNotMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return !method.isCallableFrom(caller, callType) && !name.equals("method_missing");
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, block);
         }
         return method.call(context, self, this, name, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, block);
         }
         return method.call(context, self, this, name, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, args, block);
         }
         return method.call(context, self, this, name, args, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, Block block) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, args, block);
         }
         return method.call(context, self, this, name, args, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg, block);
         }
         return method.call(context, self, this, name, arg, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg, block);
         }
         return method.call(context, self, this, name, arg, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, block);
         }
         return method.call(context, self, this, name, arg0, arg1, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, block);
         }
         return method.call(context, self, this, name, arg0, arg1, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, arg2, block);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, arg2, block);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name);
     }
 
     public IRubyObject finvokeChecked(ThreadContext context, IRubyObject self, String name) {
         DynamicMethod method = searchMethod(name);
         if(method.isUndefined()) {
             DynamicMethod methodMissing = searchMethod("method_missing");
             if(methodMissing.isUndefined() || methodMissing == context.getRuntime().getDefaultMethodMissing()) {
                 return null;
             }
 
             try {
                 return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, Block.NULL_BLOCK);
             } catch(RaiseException e) {
                 if(context.getRuntime().getNoMethodError().isInstance(e.getException())) {
                     if(self.respondsTo(name)) {
                         throw e;
                     } else {
                         return null;
                     }
                 } else {
                     throw e;
                 }
             }
         }
         return method.call(context, self, this, name);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, CallType callType) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, args, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, args);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, args, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, args);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2);
     }
 
     private void dumpReifiedClass(String dumpDir, String javaPath, byte[] classBytes) {
         if (dumpDir != null) {
             if (dumpDir.equals("")) {
                 dumpDir = ".";
             }
             java.io.FileOutputStream classStream = null;
             try {
                 java.io.File classFile = new java.io.File(dumpDir, javaPath + ".class");
                 classFile.getParentFile().mkdirs();
                 classStream = new java.io.FileOutputStream(classFile);
                 classStream.write(classBytes);
             } catch (IOException io) {
                 getRuntime().getWarnings().warn("unable to dump class file: " + io.getMessage());
             } finally {
                 if (classStream != null) {
                     try {
                         classStream.close();
                     } catch (IOException ignored) {
                     }
                 }
             }
         }
     }
 
     private void generateMethodAnnotations(Map<Class, Map<String, Object>> methodAnnos, SkinnyMethodAdapter m, List<Map<Class, Map<String, Object>>> parameterAnnos) {
         if (methodAnnos != null && methodAnnos.size() != 0) {
             for (Map.Entry<Class, Map<String, Object>> entry : methodAnnos.entrySet()) {
                 m.visitAnnotationWithFields(ci(entry.getKey()), true, entry.getValue());
             }
         }
         if (parameterAnnos != null && parameterAnnos.size() != 0) {
             for (int i = 0; i < parameterAnnos.size(); i++) {
                 Map<Class, Map<String, Object>> annos = parameterAnnos.get(i);
                 if (annos != null && annos.size() != 0) {
                     for (Iterator<Map.Entry<Class, Map<String, Object>>> it = annos.entrySet().iterator(); it.hasNext();) {
                         Map.Entry<Class, Map<String, Object>> entry = it.next();
                         m.visitParameterAnnotationWithFields(i, ci(entry.getKey()), true, entry.getValue());
                     }
                 }
             }
         }
     }
     
     private boolean shouldCallMethodMissing(DynamicMethod method) {
         return method.isUndefined();
     }
     private boolean shouldCallMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return method.isUndefined() || notVisibleAndNotMethodMissing(method, name, caller, callType);
     }
     
     public IRubyObject invokeInherited(ThreadContext context, IRubyObject self, IRubyObject subclass) {
         DynamicMethod method = getMetaClass().searchMethod("inherited");
 
         if (method.isUndefined()) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), "inherited", CallType.FUNCTIONAL, Block.NULL_BLOCK);
         }
 
         return method.call(context, self, getMetaClass(), "inherited", subclass, Block.NULL_BLOCK);
     }
 
     /** rb_class_new_instance
     *
     */
     public IRubyObject newInstance(ThreadContext context, IRubyObject[] args, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, this, obj, args, block);
         return obj;
     }
     
     public static class SpecificArityNew extends JavaMethod {
         public SpecificArityNew(RubyModule implClass, Visibility visibility) {
             super(implClass, visibility);
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, args, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, arg1, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, arg1, arg2, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
     }
 
     /** rb_class_initialize
      * 
      */
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_8, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(runtime.getObject(), block, false);
     }
         
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_8, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon((RubyClass)superObject, block, false);
     }
         
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_9, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize19(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(runtime.getObject(), block, true);
     }
         
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_9, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize19(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon((RubyClass)superObject, block, true);
     }
 
     private IRubyObject initializeCommon(RubyClass superClazz, Block block, boolean callInheritBeforeSuper) {
         setSuperClass(superClazz);
         allocator = superClazz.allocator;
         makeMetaClass(superClazz.getMetaClass());
 
         marshal = superClazz.marshal;
 
         superClazz.addSubclass(this);
 
         if (callInheritBeforeSuper) {
             inherit(superClazz);
             super.initialize(block);
         } else {
             super.initialize(block);
             inherit(superClazz);
         }
 
         return this;
     }
 
     /** rb_class_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = Visibility.PRIVATE)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         checkNotInitialized();
         if (original instanceof MetaClass) throw runtime.newTypeError("can't copy singleton class");        
         
         super.initialize_copy(original);
         allocator = ((RubyClass)original).allocator; 
         return this;        
     }
 
     protected void setModuleSuperClass(RubyClass superClass) {
         // remove us from old superclass's child classes
         if (this.superClass != null) this.superClass.removeSubclass(this);
         // add us to new superclass's child classes
         superClass.addSubclass(this);
         // update superclass reference
         setSuperClass(superClass);
     }
     
     public Collection subclasses(boolean includeDescendants) {
         Set<RubyClass> mySubclasses = subclasses;
         if (mySubclasses != null) {
             Collection<RubyClass> mine = new ArrayList<RubyClass>(mySubclasses);
             if (includeDescendants) {
                 for (RubyClass i: mySubclasses) {
                     mine.addAll(i.subclasses(includeDescendants));
                 }
             }
 
             return mine;
         } else {
             return Collections.EMPTY_LIST;
         }
     }
 
     /**
      * Add a new subclass to the weak set of subclasses.
      *
      * This version always constructs a new set to avoid having to synchronize
      * against the set when iterating it for invalidation in
      * invalidateCacheDescendants.
      *
      * @param subclass The subclass to add
      */
     public synchronized void addSubclass(RubyClass subclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) subclasses = oldSubclasses = new WeakHashSet<RubyClass>(4);
             oldSubclasses.add(subclass);
         }
     }
     
     /**
      * Remove a subclass from the weak set of subclasses.
      *
      * @param subclass The subclass to remove
      */
     public synchronized void removeSubclass(RubyClass subclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) return;
 
             oldSubclasses.remove(subclass);
         }
     }
 
     /**
      * Replace an existing subclass with a new one.
      *
      * @param subclass The subclass to remove
      * @param newSubclass The subclass to replace it with
      */
     public synchronized void replaceSubclass(RubyClass subclass, RubyClass newSubclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) return;
 
             oldSubclasses.remove(subclass);
             oldSubclasses.add(newSubclass);
         }
     }
 
     public void becomeSynchronized() {
         // make this class and all subclasses sync
         synchronized (getRuntime().getHierarchyLock()) {
             super.becomeSynchronized();
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.becomeSynchronized();
             }
         }
     }
 
     /**
      * Invalidate all subclasses of this class by walking the set of all
      * subclasses and asking them to invalidate themselves.
      *
      * Note that this version works against a reference to the current set of
      * subclasses, which could be replaced by the time this iteration is
      * complete. In theory, there may be a path by which invalidation would
      * miss a class added during the invalidation process, but the exposure is
      * minimal if it exists at all. The only way to prevent it would be to
      * synchronize both invalidation and subclass set modification against a
      * global lock, which we would like to avoid.
      */
     @Override
     public void invalidateCacheDescendants() {
         super.invalidateCacheDescendants();
         // update all subclasses
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.invalidateCacheDescendants();
             }
         }
     }
     
     public Ruby getClassRuntime() {
         return runtime;
     }
 
     public RubyClass getRealClass() {
         return this;
     }    
 
     @JRubyMethod(name = "inherited", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject inherited(ThreadContext context, IRubyObject arg) {
         return runtime.getNil();
     }
 
     /** rb_class_inherited (reversed semantics!)
      * 
      */
     public void inherit(RubyClass superClazz) {
         if (superClazz == null) superClazz = runtime.getObject();
 
         superClazz.invokeInherited(runtime.getCurrentContext(), superClazz, this);
     }
 
     /** Return the real super class of this class.
      * 
      * rb_class_superclass
      *
      */    
     @JRubyMethod(name = "superclass")
     public IRubyObject superclass(ThreadContext context) {
         RubyClass superClazz = superClass;
         
         if (superClazz == null) {
             if (metaClass == runtime.getBasicObject().getMetaClass()) return runtime.getNil();
             throw runtime.newTypeError("uninitialized class");
         }
 
         while (superClazz != null && superClazz.isIncluded()) superClazz = superClazz.superClass;
 
         return superClazz != null ? superClazz : runtime.getNil();
     }
 
     private void checkNotInitialized() {
         if (superClass != null || (runtime.is1_9() && this == runtime.getBasicObject())) {
             throw runtime.newTypeError("already initialized class");
         }
     }
     /** rb_check_inheritable
      * 
      */
     public static void checkInheritable(IRubyObject superClass) {
         if (!(superClass instanceof RubyClass)) {
             throw superClass.getRuntime().newTypeError("superclass must be a Class (" + superClass.getMetaClass() + " given)"); 
         }
         if (((RubyClass)superClass).isSingleton()) {
             throw superClass.getRuntime().newTypeError("can't make subclass of virtual class");
         }        
     }
 
     public final ObjectMarshal getMarshal() {
         return marshal;
     }
     
     public final void setMarshal(ObjectMarshal marshal) {
         this.marshal = marshal;
     }
     
     public final void marshal(Object obj, MarshalStream marshalStream) throws IOException {
         getMarshal().marshalTo(runtime, obj, this, marshalStream);
     }
     
     public final Object unmarshal(UnmarshalStream unmarshalStream) throws IOException {
         return getMarshal().unmarshalFrom(runtime, this, unmarshalStream);
     }
     
     public static void marshalTo(RubyClass clazz, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(clazz);
         output.writeString(MarshalStream.getPathFromClass(clazz));
     }
 
     public static RubyClass unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = RubyString.byteListToString(input.unmarshalString());
         RubyClass result = UnmarshalStream.getClassFromPath(input.getRuntime(), name);
         input.registerLinkTarget(result);
         return result;
     }
 
     protected static final ObjectMarshal DEFAULT_OBJECT_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             IRubyObject object = (IRubyObject)obj;
             
             marshalStream.registerLinkTarget(object);
             marshalStream.dumpVariables(object.getVariableList());
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             IRubyObject result = type.allocate();
             
             unmarshalStream.registerLinkTarget(result);
 
             unmarshalStream.defaultVariablesUnmarshal(result);
 
             return result;
         }
     };
 
     public synchronized void reify() {
         reify(null);
     }
 
     /**
      * Stand up a real Java class for the backing store of this object
      * @param classDumpDir Directory to save reified java class
      */
     public synchronized void reify(String classDumpDir) {
         Class reifiedParent = RubyObject.class;
 
         // calculate an appropriate name, using "Anonymous####" if none is present
         String name;
         if (getBaseName() == null) {
             name = "Anonymous" + id;
         } else {
             name = getName();
         }
         
         String javaName = "ruby." + name.replaceAll("::", ".");
         String javaPath = "ruby/" + name.replaceAll("::", "/");
         JRubyClassLoader parentCL = runtime.getJRubyClassLoader();
 
         if (superClass.reifiedClass != null) {
             reifiedParent = superClass.reifiedClass;
         }
 
         Class[] interfaces = Java.getInterfacesFromRubyClass(this);
         String[] interfaceNames = new String[interfaces.length];
         for (int i = 0; i < interfaces.length; i++) {
             interfaceNames[i] = p(interfaces[i]);
         }
 
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, javaPath, null, p(reifiedParent),
                 interfaceNames);
 
         if (classAnnotations != null && classAnnotations.size() != 0) {
             for (Map.Entry<Class,Map<String,Object>> entry : classAnnotations.entrySet()) {
                 Class annoType = entry.getKey();
                 Map<String,Object> fields = entry.getValue();
 
                 AnnotationVisitor av = cw.visitAnnotation(ci(annoType), true);
                 CodegenUtils.visitAnnotationFields(av, fields);
                 av.visitEnd();
             }
         }
 
         // fields to hold Ruby and RubyClass references
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "ruby", ci(Ruby.class), null, null);
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "rubyClass", ci(RubyClass.class), null, null);
 
         // static initializing method
         SkinnyMethodAdapter m = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "clinit", sig(void.class, Ruby.class, RubyClass.class), null, null));
         m.start();
         m.aload(0);
         m.putstatic(javaPath, "ruby", ci(Ruby.class));
         m.aload(1);
         m.putstatic(javaPath, "rubyClass", ci(RubyClass.class));
         m.voidreturn();
         m.end();
 
         // standard constructor that accepts Ruby, RubyClass
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", sig(void.class, Ruby.class, RubyClass.class), null, null);
         m = new SkinnyMethodAdapter(mv);
         m.aload(0);
         m.aload(1);
         m.aload(2);
         m.invokespecial(p(reifiedParent), "<init>", sig(void.class, Ruby.class, RubyClass.class));
         m.voidreturn();
         m.end();
 
         // no-arg constructor using static references to Ruby and RubyClass
         mv = cw.visitMethod(ACC_PUBLIC, "<init>", CodegenUtils.sig(void.class), null, null);
         m = new SkinnyMethodAdapter(mv);
         m.aload(0);
         m.getstatic(javaPath, "ruby", ci(Ruby.class));
         m.getstatic(javaPath, "rubyClass", ci(RubyClass.class));
         m.invokespecial(p(reifiedParent), "<init>", sig(void.class, Ruby.class, RubyClass.class));
         m.voidreturn();
         m.end();
 
         // toJava method to always pass the actual object
         mv = cw.visitMethod(ACC_PUBLIC, "toJava", CodegenUtils.sig(Object.class, Class.class), null, null);
         m = new SkinnyMethodAdapter(mv);
         m.aload(0);
         m.areturn();
         m.end();
 
         for (Map.Entry<String,DynamicMethod> methodEntry : getMethods().entrySet()) {
             String methodName = methodEntry.getKey();
             String javaMethodName = JavaNameMangler.mangleStringForCleanJavaIdentifier(methodName);
             Map<Class,Map<String,Object>> methodAnnos = getMethodAnnotations().get(methodName);
             List<Map<Class,Map<String,Object>>> parameterAnnos = getParameterAnnotations().get(methodName);
             Class[] methodSignature = getMethodSignatures().get(methodName);
 
             if (methodSignature == null) {
                 // non-signature signature with just IRubyObject
                 switch (methodEntry.getValue().getArity().getValue()) {
                 case 0:
                     mv = cw.visitMethod(ACC_PUBLIC | ACC_VARARGS, javaMethodName, sig(IRubyObject.class), null, null);
                     m = new SkinnyMethodAdapter(mv);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.aload(0);
                     m.ldc(methodName);
                     m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class));
                     break;
                 default:
                     mv = cw.visitMethod(ACC_PUBLIC | ACC_VARARGS, javaMethodName, sig(IRubyObject.class, IRubyObject[].class), null, null);
                     m = new SkinnyMethodAdapter(mv);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.aload(0);
                     m.ldc(methodName);
                     m.aload(1);
                     m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class));
                 }
                 m.areturn();
             } else {
                 // generate a real method signature for the method, with to/from coercions
 
                 // indices for temp values
                 Class[] params = new Class[methodSignature.length - 1];
                 System.arraycopy(methodSignature, 1, params, 0, params.length);
                 int baseIndex = 1;
                 for (Class paramType : params) {
                     if (paramType == double.class || paramType == long.class) {
                         baseIndex += 2;
                     } else {
                         baseIndex += 1;
                     }
                 }
                 int rubyIndex = baseIndex;
 
                 mv = cw.visitMethod(ACC_PUBLIC | ACC_VARARGS, javaMethodName, sig(methodSignature[0], params), null, null);
                 m = new SkinnyMethodAdapter(mv);
                 generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                 m.getstatic(javaPath, "ruby", ci(Ruby.class));
                 m.astore(rubyIndex);
 
                 m.aload(0); // self
                 m.ldc(methodName); // method name
                 RealClassGenerator.coerceArgumentsToRuby(m, params, rubyIndex);
                 m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class));
 
                 RealClassGenerator.coerceResultAndReturn(m, methodSignature[0]);
             }
 
             m.end();
         }
 
         cw.visitEnd();
         byte[] classBytes = cw.toByteArray();
         dumpReifiedClass(classDumpDir, javaPath, classBytes);
         Class result = parentCL.defineClass(javaName, classBytes);
 
         try {
             java.lang.reflect.Method clinit = result.getDeclaredMethod("clinit", Ruby.class, RubyClass.class);
             clinit.invoke(null, runtime, this);
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
 
         setClassAllocator(result);
         reifiedClass = result;
     }
 
     public void setReifiedClass(Class newReifiedClass) {
         this.reifiedClass = newReifiedClass;
     }
 
     public Class getReifiedClass() {
         return reifiedClass;
     }
 
     public Map<String, List<Map<Class, Map<String,Object>>>> getParameterAnnotations() {
         if (parameterAnnotations == null) return Collections.EMPTY_MAP;
         return parameterAnnotations;
     }
 
     public synchronized void addParameterAnnotation(String method, int i, Class annoClass, Map<String,Object> value) {
         if (parameterAnnotations == null) parameterAnnotations = new Hashtable<String,List<Map<Class,Map<String,Object>>>>();
         List<Map<Class,Map<String,Object>>> paramList = parameterAnnotations.get(method);
         if (paramList == null) {
             paramList = new ArrayList<Map<Class,Map<String,Object>>>(i + 1);
             parameterAnnotations.put(method, paramList);
         }
         if (paramList.size() < i + 1) {
             for (int j = paramList.size(); j < i + 1; j++) {
                 paramList.add(null);
             }
         }
         if (annoClass != null && value != null) {
             Map<Class, Map<String, Object>> annos = paramList.get(i);
             if (annos == null) {
                 annos = new HashMap<Class, Map<String, Object>>();
                 paramList.set(i, annos);
             }
             annos.put(annoClass, value);
         } else {
             paramList.set(i, null);
         }
     }
 
     public Map<String,Map<Class,Map<String,Object>>> getMethodAnnotations() {
         if (methodAnnotations == null) return Collections.EMPTY_MAP;
 
         return methodAnnotations;
     }
 
     public synchronized void addMethodAnnotation(String methodName, Class annotation, Map fields) {
         if (methodAnnotations == null) methodAnnotations = new Hashtable<String,Map<Class,Map<String,Object>>>();
 
         Map<Class,Map<String,Object>> annos = methodAnnotations.get(methodName);
         if (annos == null) {
             annos = new Hashtable<Class,Map<String,Object>>();
             methodAnnotations.put(methodName, annos);
         }
 
         annos.put(annotation, fields);
     }
 
     public Map<String,Class[]> getMethodSignatures() {
         if (methodSignatures == null) return Collections.EMPTY_MAP;
 
         return methodSignatures;
     }
 
     public synchronized void addMethodSignature(String methodName, Class[] types) {
         if (methodSignatures == null) methodSignatures = new Hashtable<String,Class[]>();
 
         methodSignatures.put(methodName, types);
     }
 
     public Map<Class,Map<String,Object>> getClassAnnotations() {
         if (classAnnotations == null) return Collections.EMPTY_MAP;
 
         return classAnnotations;
     }
 
     public synchronized void addClassAnnotation(Class annotation, Map fields) {
         if (classAnnotations == null) classAnnotations = new Hashtable<Class,Map<String,Object>>();
 
         classAnnotations.put(annotation, fields);
     }
 
     @Override
     public Object toJava(Class klass) {
         Class returnClass = null;
 
         if (klass == Class.class) {
             // Class requested; try java_class or else return nearest reified class
             if (respondsTo("java_class")) {
                 return callMethod("java_class").toJava(klass);
             } else {
                 for (RubyClass current = this; current != null; current = current.getSuperClass()) {
                     returnClass = current.getReifiedClass();
                     if (returnClass != null) return returnClass;
                 }
             }
             // should never fall through, since RubyObject has a reified class
         }
 
         if (klass.isAssignableFrom(RubyClass.class)) {
             // they're asking for something RubyClass extends, give them that
             return this;
         }
 
-        // they're asking for something we can't provide
-        throw getRuntime().newTypeError("cannot convert instance of " + getClass() + " to " + klass);
+        return super.toJava(klass);
     }
 
     protected final Ruby runtime;
     private ObjectAllocator allocator; // the default allocator
     protected ObjectMarshal marshal;
     private Set<RubyClass> subclasses;
     public static final int CS_IDX_INITIALIZE = 0;
     public static final String[] CS_NAMES = {
         "initialize"
     };
     private final CallSite[] baseCallSites = new CallSite[CS_NAMES.length];
     {
         for(int i = 0; i < CS_NAMES.length; i++) {
             baseCallSites[i] = MethodIndex.getFunctionalCallSite(CS_NAMES[i]);
         }
     }
 
     private CallSite[] extraCallSites;
 
     private Class reifiedClass;
 
     @SuppressWarnings("unchecked")
     private Map<String, VariableAccessor> variableAccessors = (Map<String, VariableAccessor>)Collections.EMPTY_MAP;
 
     private volatile boolean hasObjectID = false;
     public boolean hasObjectID() {
         return hasObjectID;
     }
 
     private Map<String, List<Map<Class, Map<String,Object>>>> parameterAnnotations;
 
     private Map<String, Map<Class, Map<String,Object>>> methodAnnotations;
 
     private Map<String, Class[]> methodSignatures;
 
     private Map<Class, Map<String,Object>> classAnnotations;
 }
diff --git a/src/org/jruby/RubyNil.java b/src/org/jruby/RubyNil.java
index 1daed0595f..53a1e89bd4 100644
--- a/src/org/jruby/RubyNil.java
+++ b/src/org/jruby/RubyNil.java
@@ -1,233 +1,232 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
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
 package org.jruby;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
-import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="NilClass")
 public class RubyNil extends RubyObject {
     public RubyNil(Ruby runtime) {
         super(runtime, runtime.getNilClass(), false, false);
         flags |= NIL_F | FALSE_F;
     }
     
     public static final ObjectAllocator NIL_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return runtime.getNil();
         }
     };
     
     public static RubyClass createNilClass(Ruby runtime) {
         RubyClass nilClass = runtime.defineClass("NilClass", runtime.getObject(), NIL_ALLOCATOR);
         runtime.setNilClass(nilClass);
         nilClass.index = ClassIndex.NIL;
         nilClass.setReifiedClass(RubyNil.class);
         
         nilClass.defineAnnotatedMethods(RubyNil.class);
         
         nilClass.getMetaClass().undefineMethod("new");
         
         // FIXME: This is causing a verification error for some reason
         //nilClass.dispatcher = callbackFactory.createDispatcher(nilClass);
         
         return nilClass;
     }
     
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.NIL;
     }
 
     @Override
     public boolean isImmediate() {
         return true;
     }
 
     @Override
     public RubyClass getSingletonClass() {
         return metaClass;
     }
     
     @Override
     public Class<?> getJavaClass() {
         return void.class;
     }
     
     // Methods of the Nil Class (nil_*):
     
     /** nil_to_i
      *
      */
     @JRubyMethod(name = "to_i")
     public static RubyFixnum to_i(IRubyObject recv) {
         return RubyFixnum.zero(recv.getRuntime());
     }
     
     /**
      * nil_to_f
      *
      */
     @JRubyMethod(name = "to_f")
     public static RubyFloat to_f(IRubyObject recv) {
         return RubyFloat.newFloat(recv.getRuntime(), 0.0D);
     }
     
     /** nil_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     public static RubyString to_s(IRubyObject recv) {
         return RubyString.newEmptyString(recv.getRuntime());
     }
     
     /** nil_to_a
      *
      */
     @JRubyMethod(name = "to_a")
     public static RubyArray to_a(IRubyObject recv) {
         return recv.getRuntime().newEmptyArray();
     }
     
     /** nil_inspect
      *
      */
     @JRubyMethod(name = "inspect")
     public static RubyString inspect(IRubyObject recv) {
         return recv.getRuntime().newString("nil");
     }
     
     /** nil_type
      *
      */
     @JRubyMethod(name = "type")
     public static RubyClass type(IRubyObject recv) {
         return recv.getRuntime().getNilClass();
     }
     
     /** nil_and
      *
      */
     @JRubyMethod(name = "&", required = 1)
     public static RubyBoolean op_and(IRubyObject recv, IRubyObject obj) {
         return recv.getRuntime().getFalse();
     }
     
     /** nil_or
      *
      */
     @JRubyMethod(name = "|", required = 1)
     public static RubyBoolean op_or(IRubyObject recv, IRubyObject obj) {
         return recv.getRuntime().newBoolean(obj.isTrue());
     }
     
     /** nil_xor
      *
      */
     @JRubyMethod(name = "^", required = 1)
     public static RubyBoolean op_xor(IRubyObject recv, IRubyObject obj) {
         return recv.getRuntime().newBoolean(obj.isTrue());
     }
 
     @JRubyMethod(name = "nil?")
     public IRubyObject nil_p() {
         return getRuntime().getTrue();
     }
     
     @Override
     public RubyFixnum id() {
         return getRuntime().newFixnum(4);
     }
     
     @Override
     public IRubyObject taint(ThreadContext context) {
         return this;
     }
 
     /** nilclass_to_c
      * 
      */
     @JRubyMethod(name = "to_c", compat = CompatVersion.RUBY1_9)
     public static IRubyObject to_c(ThreadContext context, IRubyObject recv) {
         return RubyComplex.newComplexCanonicalize(context, RubyFixnum.zero(context.getRuntime()));
     }
     
     /** nilclass_to_r
      * 
      */
     @JRubyMethod(name = "to_r", compat = CompatVersion.RUBY1_9)
     public static IRubyObject to_r(ThreadContext context, IRubyObject recv) {
         return RubyRational.newRationalCanonicalize(context, RubyFixnum.zero(context.getRuntime()));
     }
 
     /** nilclass_rationalize
      *
      */
     @JRubyMethod(name = "rationalize", optional = 1, compat = CompatVersion.RUBY1_9)
     public static IRubyObject rationalize(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return to_r(context, recv);
     }
 
     @Override
     public Object toJava(Class target) {
         if (target.isPrimitive()) {
             if (target == Boolean.TYPE) {
                 return Boolean.FALSE;
             } else if (target == Byte.TYPE) {
                 return (byte)0;
             } else if (target == Short.TYPE) {
                 return (short)0;
             } else if (target == Character.TYPE) {
                 return (char)0;
             } else if (target == Integer.TYPE) {
                 return 0;
             } else if (target == Long.TYPE) {
                 return (long)0;
             } else if (target == Float.TYPE) {
                 return (float)0;
             } else if (target == Double.TYPE) {
                 return (double)0;
             }
         }
         return null;
     }
 }
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index 120547a617..b67c05f1d6 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -6184,1161 +6184,1161 @@ public class RubyString extends RubyObject implements EncodingCapable {
             while (s < send) {
                 if ((c = trans[sbytes[s] & 0xff]) >= 0) {
                     sbytes[s] = (byte)(c & 0xff);
                     modify = true;
                 }
                 s++;
             }
         }
 
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject trTrans19(ThreadContext context, IRubyObject src, IRubyObject repl, boolean sflag) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) return runtime.getNil();
 
         RubyString replStr = repl.convertToString();
         ByteList replList = replStr.value;
         if (replList.getRealSize() == 0) return delete_bang19(context, src);
 
         RubyString srcStr = src.convertToString();
         ByteList srcList = srcStr.value;
         Encoding e1 = checkEncoding(srcStr);
         Encoding e2 = checkEncoding(replStr);
         Encoding enc = e1 == e2 ? e1 : srcStr.checkEncoding(replStr);
 
         int cr = getCodeRange();
 
         final TR trSrc = new TR(srcList);
         boolean cflag = false;
         if (value.getRealSize() > 1) {
             if (enc.isAsciiCompatible()) {
                 if (trSrc.buf.length > 0 && (trSrc.buf[trSrc.p] & 0xff) == '^' && trSrc.p + 1 < trSrc.pend) {
                     cflag = true;
                     trSrc.p++;
                 }
             } else {
                 int cl = StringSupport.preciseLength(enc, trSrc.buf, trSrc.p, trSrc.pend);
                 if (enc.mbcToCode(trSrc.buf, trSrc.p, trSrc.pend) == '^' && trSrc.p + cl < trSrc.pend) {
                     cflag = true;
                     trSrc.p += cl;
                 }
             }            
         }
 
         boolean singlebyte = true;
         int c;
         final int[]trans = new int[TRANS_SIZE];
         IntHash<Integer> hash = null;
         final TR trRepl = new TR(replList);
 
         if (cflag) {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = 1;
             
             while ((c = trNext(trSrc, runtime, enc)) >= 0) {
                 if (c < TRANS_SIZE) {
                     trans[c & 0xff] = -1;
                 } else {
                     if (hash == null) hash = new IntHash<Integer>();
                     hash.put(c, 1); // QTRUE
                 }
             }
             while ((c = trNext(trRepl, runtime, enc)) >= 0) {}  /* retrieve last replacer */
             int last = trRepl.now;
             for (int i=0; i<TRANS_SIZE; i++) {
                 if (trans[i] >= 0) trans[i] = last;
             }
         } else {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = -1;
             
             while ((c = trNext(trSrc, runtime, enc)) >= 0) {
                 int r = trNext(trRepl, runtime, enc);
                 if (r == -1) r = trRepl.now;
                 if (c < TRANS_SIZE) {
                     trans[c & 0xff] = r;
                     if (r > TRANS_SIZE - 1) singlebyte = false;
                 } else {
                     if (hash == null) hash = new IntHash<Integer>();
                     hash.put(c, r);
                 }
             }
         }
 
         if (cr == CR_VALID) cr = CR_7BIT;
         modifyAndKeepCodeRange();
         int s = value.getBegin();
         int send = s + value.getRealSize();
         byte sbytes[] = value.getUnsafeBytes();
         int max = value.getRealSize();
         boolean modify = false;
 
         int last = -1;
         int clen, tlen, c0;
 
         if (sflag) {
             int save = -1;
             byte[]buf = new byte[max];
             int t = 0;
             while (s < send) {
                 boolean mayModify = false;
                 c0 = c = codePoint(runtime, e1, sbytes, s, send);
                 clen = codeLength(runtime, e1, c);
                 tlen = enc == e1 ? clen : codeLength(runtime, enc, c);
                 s += clen;
                 c = trCode(c, trans, hash, cflag, last);
 
                 if (c != -1) {
                     if (save == c) {
                         if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                         continue;
                     }
                     save = c;
                     tlen = codeLength(runtime, enc, c);
                     modify = true;
                 } else {
                     save = -1;
                     c = c0;
                     if (enc != e1) mayModify = true;
                 }
 
                 while (t + tlen >= max) {
                     max <<= 1;
                     byte[]tbuf = new byte[max];
                     System.arraycopy(buf, 0, tbuf, 0, buf.length);
                     buf = tbuf;
                 }
                 enc.codeToMbc(c, buf, t);
                 if (mayModify && (tlen == 1 ? sbytes[s] != buf[t] : ByteList.memcmp(sbytes, s, buf, t, tlen) != 0)) modify = true;
                 if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                 t += tlen;
             }
             value.setUnsafeBytes(buf);
             value.setRealSize(t);
         } else if (enc.isSingleByte() || (singlebyte && hash == null)) {
             while (s < send) {
                 c = sbytes[s] & 0xff;
                 if (trans[c] != -1) {
                     if (!cflag) {
                         c = trans[c];
                         sbytes[s] = (byte)c;
                     } else {
                         sbytes[s] = (byte)last;
                     }
                     modify = true;
                 }
                 if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                 s++;
             }
         } else {
             max += max >> 1;
             byte[]buf = new byte[max];
             int t = 0;
 
             while (s < send) {
                 boolean mayModify = false;
                 c0 = c = codePoint(runtime, e1, sbytes, s, send);
                 clen = codeLength(runtime, e1, c);
                 tlen = enc == e1 ? clen : codeLength(runtime, enc, c);
 
                 c = trCode(c, trans, hash, cflag, last);
 
                 if (c != -1) {
                     tlen = codeLength(runtime, enc, c);
                     modify = true;
                 } else {
                     c = c0;
                     if (enc != e1) mayModify = true;
                 }
                 while (t + tlen >= max) {
                     max <<= 1;
                     byte[]tbuf = new byte[max];
                     System.arraycopy(buf, 0, tbuf, 0, buf.length);
                     buf = tbuf;
                 }
 
                 enc.codeToMbc(c, buf, t);
 
                 if (mayModify && (tlen == 1 ? sbytes[s] != buf[t] : ByteList.memcmp(sbytes, s, buf, t, tlen) != 0)) modify = true;
                 if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                 s += clen;
                 t += tlen;
             }
             value.setUnsafeBytes(buf);
             value.setRealSize(t);
         }
 
         if (modify) {
             if (cr != CR_BROKEN) setCodeRange(cr);
             associateEncoding(enc);
             return this;
         }
         return runtime.getNil();
     }
 
     private int trCode(int c, int[]trans, IntHash<Integer> hash, boolean cflag, int last) {
         if (c < TRANS_SIZE) {
             return trans[c];
         } else if (hash != null) {
             Integer tmp = hash.get(c);
             if (tmp == null) {
                 return cflag ? last : -1;
             } else {
                 return cflag ? -1 : tmp;
             }
         } else {
             return -1;
         }
     }
 
     /** trnext
     *
     */    
     private int trNext(TR t) {
         byte[]buf = t.buf;
         
         for (;;) {
             if (!t.gen) {
                 if (t.p == t.pend) return -1;
                 if (t.p < t.pend -1 && buf[t.p] == '\\') t.p++;
                 t.now = buf[t.p++] & 0xff;
                 if (t.p < t.pend - 1 && buf[t.p] == '-') {
                     t.p++;
                     if (t.p < t.pend) {
                         if (t.now > (buf[t.p] & 0xff)) {
                             t.p++;
                             continue;
                         }
                         t.gen = true;
                         t.max = buf[t.p++] & 0xff;
                     }
                 }
                 return t.now;
             } else if (++t.now < t.max) {
                 return t.now;
             } else {
                 t.gen = false;
                 return t.max;
             }
         }
     }
 
     private int trNext(TR t, Ruby runtime, Encoding enc) {
         byte[]buf = t.buf;
         
         for (;;) {
             if (!t.gen) {
                 if (t.p == t.pend) return -1;
                 if (t.p < t.pend -1 && buf[t.p] == '\\') t.p++;
                 t.now = codePoint(runtime, enc, buf, t.p, t.pend);
                 t.p += codeLength(runtime, enc, t.now);
                 if (t.p < t.pend - 1 && buf[t.p] == '-') {
                     t.p++;
                     if (t.p < t.pend) {
                         int c = codePoint(runtime, enc, buf, t.p, t.pend);
                         t.p += codeLength(runtime, enc, c);
                         if (t.now > c) continue;
                         t.gen = true;
                         t.max = c;
                     }
                 }
                 return t.now;
             } else if (++t.now < t.max) {
                 return t.now;
             } else {
                 t.gen = false;
                 return t.max;
             }
         }
     }
 
     /** rb_str_tr_s / rb_str_tr_s_bang
      *
      */
     @JRubyMethod(name ="tr_s", compat = CompatVersion.RUBY1_8)
     public IRubyObject tr_s(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.trTrans(context, src, repl, true);
         return str;
     }
 
     @JRubyMethod(name = "tr_s!", compat = CompatVersion.RUBY1_8)
     public IRubyObject tr_s_bang(ThreadContext context, IRubyObject src, IRubyObject repl) {
         return trTrans(context, src, repl, true);
     }
 
     @JRubyMethod(name ="tr_s", compat = CompatVersion.RUBY1_9)
     public IRubyObject tr_s19(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.trTrans19(context, src, repl, true);
         return str;
     }
 
     @JRubyMethod(name = "tr_s!", compat = CompatVersion.RUBY1_9)
     public IRubyObject tr_s_bang19(ThreadContext context, IRubyObject src, IRubyObject repl) {
         return trTrans19(context, src, repl, true);
     }
 
     /** rb_str_each_line
      *
      */
     public IRubyObject each_line(ThreadContext context, Block block) {
         return each_lineCommon(context, context.getRuntime().getGlobalVariables().get("$/"), block);
     }
 
     public IRubyObject each_line(ThreadContext context, IRubyObject arg, Block block) {
         return each_lineCommon(context, arg, block);
     }
 
     public IRubyObject each_lineCommon(ThreadContext context, IRubyObject sep, Block block) {        
         Ruby runtime = context.getRuntime();
         if (sep.isNil()) {
             block.yield(context, this);
             return this;
         }
 
         RubyString sepStr = sep.convertToString();
         ByteList sepValue = sepStr.value;
         int rslen = sepValue.getRealSize();
 
         final byte newline;
         if (rslen == 0) {
             newline = '\n';
         } else {
             newline = sepValue.getUnsafeBytes()[sepValue.getBegin() + rslen - 1];
         }
 
         int p = value.getBegin();
         int end = p + value.getRealSize();
         int ptr = p, s = p;
         int len = value.getRealSize();
         byte[] bytes = value.getUnsafeBytes();
 
         p += rslen;
 
         for (; p < end; p++) {
             if (rslen == 0 && bytes[p] == '\n') {
                 if (bytes[++p] != '\n') continue;
                 while(p < end && bytes[p] == '\n') p++;
             }
             if (ptr < p && bytes[p - 1] == newline &&
                (rslen <= 1 || 
                 ByteList.memcmp(sepValue.getUnsafeBytes(), sepValue.getBegin(), rslen, bytes, p - rslen, rslen) == 0)) {
                 block.yield(context, makeShared(runtime, s - ptr, p - s).infectBy(this));
                 modifyCheck(bytes, len);
                 s = p;
             }
         }
 
         if (s != end) {
             if (p > end) p = end;
             block.yield(context, makeShared(runtime, s - ptr, p - s).infectBy(this));
         }
 
         return this;
     }
 
     @JRubyMethod(name = "each", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject each18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "each");
     }
 
     @JRubyMethod(name = "each", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject each18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each", arg);
     }
 
     @JRubyMethod(name = "each_line", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject each_line18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     @JRubyMethod(name = "each_line", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject each_line18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each_line", arg);
     }
 
     @JRubyMethod(name = "lines", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject lines18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "lines");
     }
 
     @JRubyMethod(name = "lines", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject lines18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "lines", arg);
     }
 
     @JRubyMethod(name = "each_line", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_line19(ThreadContext context, Block block) {
         return block.isGiven() ? each_lineCommon19(context, block) : 
             enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     @JRubyMethod(name = "each_line", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_line19(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon19(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each_line", arg);
     }
 
     @JRubyMethod(name = "lines", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject lines(ThreadContext context, Block block) {
         return block.isGiven() ? each_lineCommon19(context, block) : 
             enumeratorize(context.getRuntime(), this, "lines");
     }
 
     @JRubyMethod(name = "lines", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject lines(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon19(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "lines", arg);
     }
 
     private IRubyObject each_lineCommon19(ThreadContext context, Block block) {
         return each_lineCommon19(context, context.getRuntime().getGlobalVariables().get("$/"), block);
     }
 
     private IRubyObject each_lineCommon19(ThreadContext context, IRubyObject sep, Block block) {        
         Ruby runtime = context.getRuntime();
         if (sep.isNil()) {
             block.yield(context, this);
             return this;
         }
 
         ByteList val = value.shallowDup();
         int p = val.getBegin();
         int s = p;
         int len = val.getRealSize();
         int end = p + len;
         byte[]bytes = val.getUnsafeBytes();
 
         final Encoding enc;
         RubyString sepStr = sep.convertToString();
         if (sepStr == runtime.getGlobalVariables().getDefaultSeparator()) {
             enc = val.getEncoding();
             while (p < end) {
                 if (bytes[p] == (byte)'\n') {
                     int p0 = enc.leftAdjustCharHead(bytes, s, p, end);
                     if (enc.isNewLine(bytes, p0, end)) {
                         p = p0 + StringSupport.length(enc, bytes, p0, end);
                         block.yield(context, makeShared19(runtime, val, s, p - s).infectBy(this));
                         s = p++;
                     }
                 }
                 p++;
             }
         } else {
             enc = checkEncoding(sepStr);
             ByteList sepValue = sepStr.value;
             final int newLine;
             int rslen = sepValue.getRealSize();
             if (rslen == 0) {
                 newLine = '\n';
             } else {
                 newLine = codePoint(runtime, enc, sepValue.getUnsafeBytes(), sepValue.getBegin(), sepValue.getBegin() + sepValue.getRealSize());
             }
 
             while (p < end) {
                 int c = codePoint(runtime, enc, bytes, p, end);
                 again: do {
                     int n = codeLength(runtime, enc, c);
                     if (rslen == 0 && c == newLine) {
                         p += n;
                         if (p < end && (c = codePoint(runtime, enc, bytes, p, end)) != newLine) continue again;
                         while (p < end && codePoint(runtime, enc, bytes, p, end) == newLine) p += n;
                         p -= n;
                     }
                     if (c == newLine && (rslen <= 1 ||
                             ByteList.memcmp(sepValue.getUnsafeBytes(), sepValue.getBegin(), rslen, bytes, p, rslen) == 0)) {
                         block.yield(context, makeShared19(runtime, val, s, p - s + (rslen != 0 ? rslen : n)).infectBy(this));
                         s = p + (rslen != 0 ? rslen : n);
                     }
                     p += n;
                 } while (false);
             }
         }
 
         if (s != end) {
             block.yield(context, makeShared19(runtime, val, s, end - s).infectBy(this));
         }
         return this;
     }
 
     /**
      * rb_str_each_byte
      */
     public RubyString each_byte(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         // Check the length every iteration, since
         // the block can modify this string.
         for (int i = 0; i < value.length(); i++) {
             block.yield(context, runtime.newFixnum(value.get(i) & 0xFF));
         }
         return this;
     }
 
     @JRubyMethod(name = "each_byte", frame = true)
     public IRubyObject each_byte19(ThreadContext context, Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod(name = "bytes", frame = true)
     public IRubyObject bytes(ThreadContext context, Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "bytes");
     }
 
     /** rb_str_each_char
      * 
      */
     @JRubyMethod(name = "each_char", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject each_char18(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon18(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod(name = "chars", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject chars18(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon18(context, block) : enumeratorize(context.getRuntime(), this, "chars");
     }
 
     private IRubyObject each_charCommon18(ThreadContext context, Block block) {
         byte bytes[] = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
 
         Ruby runtime = context.getRuntime();
         Encoding enc = runtime.getKCode().getEncoding();
         ByteList val = value.shallowDup();
         while (p < end) {
             int n = StringSupport.length(enc, bytes, p, end);
             block.yield(context, makeShared19(runtime, val, p-val.getBegin(), n));
             p += n;
         }
         return this;
     }
 
     @JRubyMethod(name = "each_char", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_char19(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon19(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod(name = "chars", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject chars19(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon19(context, block) : enumeratorize(context.getRuntime(), this, "chars");
     }
 
     private IRubyObject each_charCommon19(ThreadContext context, Block block) {
         byte bytes[] = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
         Encoding enc = value.getEncoding();
 
         Ruby runtime = context.getRuntime();
         ByteList val = value.shallowDup();
         while (p < end) {
             int n = StringSupport.length(enc, bytes, p, end);
             block.yield(context, makeShared19(runtime, val, p-value.getBegin(), n));
             p += n;
         }
         return this;
     }
 
     /** rb_str_each_codepoint
      * 
      */
     @JRubyMethod(name = "each_codepoint", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_codepoint(ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "each_codepoint");
         return singleByteOptimizable() ? each_byte(context, block) : each_codepointCommon(context, block);
     }
 
     @JRubyMethod(name = "codepoints", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject codepoints(ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "codepoints");
         return singleByteOptimizable() ? each_byte(context, block) : each_codepointCommon(context, block);
     }
 
     private IRubyObject each_codepointCommon(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         byte bytes[] = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
         Encoding enc = value.getEncoding();
 
         while (p < end) {
             int c = codePoint(runtime, enc, bytes, p, end);
             int n = codeLength(runtime, enc, c);
             block.yield(context, runtime.newFixnum(c));
             p += n;
         }
         return this;
     }
 
     /** rb_str_intern
      *
      */
     private RubySymbol to_sym() {
         RubySymbol symbol = getRuntime().getSymbolTable().getSymbol(value);
         if (symbol.getBytes() == value) shareLevel = SHARE_LEVEL_BYTELIST;
         return symbol;
     }
 
     @JRubyMethod(name = {"to_sym", "intern"}, compat = CompatVersion.RUBY1_8)
     public RubySymbol intern() {
         if (value.getRealSize() == 0) throw getRuntime().newArgumentError("interning empty string");
         for (int i = 0; i < value.getRealSize(); i++) {
             if (value.getUnsafeBytes()[value.getBegin() + i] == 0) throw getRuntime().newArgumentError("symbol string may not contain '\\0'");
         }
         return to_sym();
     }
 
     @JRubyMethod(name = {"to_sym", "intern"}, compat = CompatVersion.RUBY1_9)
     public RubySymbol intern19() {
         return to_sym();
     }
 
     @JRubyMethod(name = "ord", compat = CompatVersion.RUBY1_9)
     public IRubyObject ord(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return RubyFixnum.newFixnum(runtime, codePoint(runtime, value.getEncoding(), value.getUnsafeBytes(), value.getBegin(),
                                                                 value.getBegin() + value.getRealSize()));
     }
 
     @JRubyMethod(name = "sum")
     public IRubyObject sum(ThreadContext context) {
         return sumCommon(context, 16);
     }
 
     @JRubyMethod(name = "sum")
     public IRubyObject sum(ThreadContext context, IRubyObject arg) {
         return sumCommon(context, RubyNumeric.num2long(arg));
     }
 
     public IRubyObject sumCommon(ThreadContext context, long bits) {
         Ruby runtime = context.getRuntime();
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         int end = p + len; 
 
         if (bits >= 8 * 8) { // long size * bits in byte
             IRubyObject one = RubyFixnum.one(runtime);
             IRubyObject sum = RubyFixnum.zero(runtime);
             while (p < end) {
                 modifyCheck(bytes, len);
                 sum = sum.callMethod(context, "+", RubyFixnum.newFixnum(runtime, bytes[p++] & 0xff));
             }
             if (bits != 0) {
                 IRubyObject mod = one.callMethod(context, "<<", RubyFixnum.newFixnum(runtime, bits));
                 sum = sum.callMethod(context, "&", mod.callMethod(context, "-", one));
             }
             return sum;
         } else {
             long sum = 0;
             while (p < end) {
                 modifyCheck(bytes, len);
                 sum += bytes[p++] & 0xff;
             }
             return RubyFixnum.newFixnum(runtime, bits == 0 ? sum : sum & (1L << bits) - 1L);
         }
     }
 
     /** string_to_c
      * 
      */
     @JRubyMethod(name = "to_c", reads = BACKREF, writes = BACKREF, compat = CompatVersion.RUBY1_9)
     public IRubyObject to_c(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         DynamicScope scope = context.getCurrentScope();
         IRubyObject backref = scope.getBackRef(runtime);
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
 
         IRubyObject s = RuntimeHelpers.invoke(
                 context, this, "gsub",
                 RubyRegexp.newDummyRegexp(runtime, Numeric.ComplexPatterns.underscores_pat),
                 runtime.newString(new ByteList(new byte[]{'_'})));
 
         RubyArray a = RubyComplex.str_to_c_internal(context, s);
 
         scope.setBackRef(backref);
 
         if (!a.eltInternal(0).isNil()) {
             return a.eltInternal(0);
         } else {
             return RubyComplex.newComplexCanonicalize(context, RubyFixnum.zero(runtime));
         }
     }
 
     /** string_to_r
      * 
      */
     @JRubyMethod(name = "to_r", reads = BACKREF, writes = BACKREF, compat = CompatVersion.RUBY1_9)
     public IRubyObject to_r(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         DynamicScope scope = context.getCurrentScope();
         IRubyObject backref = scope.getBackRef(runtime);
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
 
         IRubyObject s = RuntimeHelpers.invoke(
                 context, this, "gsub",
                 RubyRegexp.newDummyRegexp(runtime, Numeric.ComplexPatterns.underscores_pat),
                 runtime.newString(new ByteList(new byte[]{'_'})));
 
         RubyArray a = RubyRational.str_to_r_internal(context, s);
 
         scope.setBackRef(backref);
 
         if (!a.eltInternal(0).isNil()) {
             return a.eltInternal(0);
         } else {
             return RubyRational.newRationalCanonicalize(context, RubyFixnum.zero(runtime));
         }
     }    
 
     public static RubyString unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyString result = newString(input.getRuntime(), input.unmarshalString());
         input.registerLinkTarget(result);
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#unpack
      */
     @JRubyMethod(name = "unpack")
     public RubyArray unpack(IRubyObject obj) {
         return Pack.unpack(getRuntime(), this.value, stringValue(obj).value);
     }
 
     public void empty() {
         value = ByteList.EMPTY_BYTELIST;
         shareLevel = SHARE_LEVEL_BYTELIST;
     }
 
     @JRubyMethod(name = "encoding", compat = CompatVersion.RUBY1_9)
     public IRubyObject encoding(ThreadContext context) {
         return context.getRuntime().getEncodingService().getEncoding(value.getEncoding());
     }
 
     @JRubyMethod(name = "encode!", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context) {
         modify19();
         IRubyObject defaultInternal = RubyEncoding.getDefaultInternal(context.getRuntime());
         if (!defaultInternal.isNil()) {
             encode_bang(context, defaultInternal);
         }
         return this;
     }
 
     @JRubyMethod(name = "encode!", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context, IRubyObject enc) {
         modify19();
 
         Ruby runtime = context.getRuntime();
         this.value = encodeCommon(context, runtime, this.value, enc, runtime.getNil(),
             runtime.getNil());
 
         return this;
     }
 
     @JRubyMethod(name = "encode!", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context, IRubyObject enc, IRubyObject arg) {
         modify19();
 
         Ruby runtime = context.getRuntime();
         IRubyObject fromEnc = arg;
         IRubyObject opts = runtime.getNil();
         if (arg instanceof RubyHash) {
             fromEnc = runtime.getNil();
             opts = arg;
         }
         this.value = encodeCommon(context, runtime, this.value, enc, fromEnc, opts);
 
         return this;
     }
 
     @JRubyMethod(name = "encode!", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context, IRubyObject enc, IRubyObject fromEnc, IRubyObject opts) {
         modify19();
         this.value = encodeCommon(context, context.getRuntime(), this.value, enc, fromEnc, opts);
         return this;
     }
 
     @JRubyMethod(name = "encode", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode(ThreadContext context) {
         modify19();
         Ruby runtime = context.getRuntime();
         IRubyObject defaultInternal = RubyEncoding.getDefaultInternal(runtime);
 
         if (!defaultInternal.isNil()) {
             ByteList encoded = encodeCommon(context, runtime, value, defaultInternal,
                                             runtime.getNil(), runtime.getNil());
             return runtime.newString(encoded);
         } else {
             return dup();
         }
     }
 
     @JRubyMethod(name = "encode", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc) {
         modify19();
         Ruby runtime = context.getRuntime();
 
         ByteList encoded = encodeCommon(context, runtime, value, enc, runtime.getNil(),
             runtime.getNil());
         return runtime.newString(encoded);
     }
 
     @JRubyMethod(name = "encode", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc, IRubyObject arg) {
         modify19();
         Ruby runtime = context.getRuntime();
 
         IRubyObject fromEnc = arg;
         IRubyObject opts = runtime.getNil();
         if (arg instanceof RubyHash) {
             fromEnc = runtime.getNil();
             opts = arg;
         }
         ByteList encoded = encodeCommon(context, runtime, value, enc, fromEnc, opts);
         return runtime.newString(encoded);
     }
 
     @JRubyMethod(name = "encode", compat = CompatVersion.RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc, IRubyObject fromEnc, IRubyObject opts) {
         modify19();
         Ruby runtime = context.getRuntime();
 
         ByteList encoded = encodeCommon(context, runtime, value, enc, fromEnc, opts);
         return runtime.newString(encoded);
     }
 
     private ByteList encodeCommon(ThreadContext context, Ruby runtime, ByteList value,
             IRubyObject toEnc, IRubyObject fromEnc, IRubyObject opts) {
         Charset from = fromEnc.isNil() ? getCharset(runtime, value.getEncoding()) : getCharset(runtime, fromEnc);
 
         Encoding encoding = getEncoding(runtime, toEnc);
         Charset to = getCharset(runtime, encoding);
 
         CharsetEncoder encoder = getEncoder(context, runtime, to, opts);
 
         // decode from "from" and encode to "to"
         ByteBuffer fromBytes = ByteBuffer.wrap(value.getUnsafeBytes(), value.begin(), value.length());
         ByteBuffer toBytes;
         try {
             toBytes = encoder.encode(from.decode(fromBytes));
         } catch (CharacterCodingException e) {
             throw runtime.newInvalidByteSequenceError("");
         }
 
         // CharsetEncoder#encode guarantees a newly-allocated buffer, so
         // it's safe for us to take ownership of it without copying
         ByteList result = new ByteList(toBytes.array(), toBytes.arrayOffset(),
                 toBytes.limit() - toBytes.arrayOffset(), false);
         result.setEncoding(encoding);
         return result;
     }
 
     private CharsetEncoder getEncoder(ThreadContext context, Ruby runtime, Charset charset, IRubyObject opts) {
         CharsetEncoder encoder = charset.newEncoder();
 
         if (!opts.isNil()) {
             RubyHash hash = (RubyHash) opts;
             CodingErrorAction action = CodingErrorAction.REPLACE;
             
             IRubyObject replace = hash.fastARef(runtime.newSymbol("replace"));
             if (replace != null && !replace.isNil()) {
                 String replaceWith = replace.toString();
                 if (replaceWith.length() > 0) {
                     encoder.replaceWith(replaceWith.getBytes());
                 } else {
                     action = CodingErrorAction.IGNORE;
                 }
             }
             
             IRubyObject invalid = hash.fastARef(runtime.newSymbol("invalid"));
             if (invalid != null && invalid.op_equal(context, runtime.newSymbol("replace")).isTrue()) {
                 encoder.onMalformedInput(action);
             }
 
             IRubyObject undef = hash.fastARef(runtime.newSymbol("undef"));
             if (undef != null && undef.op_equal(context, runtime.newSymbol("replace")).isTrue()) {
                 encoder.onUnmappableCharacter(action);
             }
 
 //            FIXME: Parse the option :xml
 //            The value must be +:text+ or +:attr+. If the
 //            value is +:text+ +#encode+ replaces undefined
 //            characters with their (upper-case hexadecimal)
 //            numeric character references. '&', '<', and
 //            '>' are converted to "&amp;", "&lt;", and
 //            "&gt;", respectively. If the value is +:attr+,
 //            +#encode+ also quotes the replacement result
 //            (using '"'), and replaces '"' with "&quot;".
         }
 
         return encoder;
     }
 
     private Encoding getEncoding(Ruby runtime, IRubyObject toEnc) {
         try {
             return RubyEncoding.getEncodingFromObject(runtime, toEnc);
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + toEnc.toString() + ")");
         }
     }
 
     private Charset getCharset(Ruby runtime, IRubyObject toEnc) {
         try {
             Encoding encoding = RubyEncoding.getEncodingFromObject(runtime, toEnc);
 
             return getCharset(runtime, encoding);
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + toEnc.toString() + ")");
         }
     }
 
     private Charset getCharset(Ruby runtime, Encoding encoding) {
         try {
             // special-casing ASCII* to ASCII
             return encoding.toString().startsWith("ASCII") ?
                 Charset.forName("ASCII") :
                 Charset.forName(encoding.toString());
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + encoding.toString() + ")");
         }
     }
 
     @JRubyMethod(name = "force_encoding", compat = CompatVersion.RUBY1_9)
     public IRubyObject force_encoding(ThreadContext context, IRubyObject enc) {
         modify19();
         Encoding encoding = RubyEncoding.getEncodingFromObject(context.getRuntime(), enc);
         associateEncoding(encoding);
         return this;
     }
 
     @JRubyMethod(name = "valid_encoding?", compat = CompatVersion.RUBY1_9)
     public IRubyObject valid_encoding_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return scanForCodeRange() == CR_BROKEN ? runtime.getFalse() : runtime.getTrue();
     }
 
     @JRubyMethod(name = "ascii_only?", compat = CompatVersion.RUBY1_9)
     public IRubyObject ascii_only_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return scanForCodeRange() == CR_7BIT ? runtime.getTrue() : runtime.getFalse();
     }
 
     /**
      * Mutator for internal string representation.
      *
      * @param value The new java.lang.String this RubyString should encapsulate
      * @deprecated
      */
     public void setValue(CharSequence value) {
         view(ByteList.plain(value));
     }
 
     public void setValue(ByteList value) {
         view(value);
     }
 
     public CharSequence getValue() {
         return toString();
     }
 
     public byte[] getBytes() {
         return value.bytes();
     }
 
     public ByteList getByteList() {
         return value;
     }
 
     /** used by ar-jdbc
      * 
      */
     public String getUnicodeValue() {
         return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.getBegin(), value.getRealSize());
     }
 
     @Override
     public Object toJava(Class target) {
         if (target.isAssignableFrom(String.class)) {
             try {
                 // 1.9 support for encodings
                 // TODO: Fix charset use for JRUBY-4553
                 if (getRuntime().is1_9()) {
                     return new String(value.getUnsafeBytes(), value.begin(), value.length(), getEncoding().toString());
                 }
 
                 return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.begin(), value.length());
             } catch (UnsupportedEncodingException uee) {
                 return toString();
             }
         } else if (target.isAssignableFrom(ByteList.class)) {
             return value;
         } else {
-            throw getRuntime().newTypeError("cannot convert instance of String to " + target);
+            return super.toJava(target);
         }
     }
 
     /**
      * Variable-arity versions for compatibility. Not bound to Ruby.
      * @deprecated Use the versions with zero or one arguments
      */
 
     @Deprecated
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         switch (args.length) {
         case 0: return this;
         case 1: return initialize(args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject sub(ThreadContext context, IRubyObject[] args, Block block) {
         RubyString str = strDup(context.getRuntime());
         str.sub_bang(context, args, block);
         return str;
     }
 
     @Deprecated
     public IRubyObject sub_bang(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1: return sub_bang(context, args[0], block);
         case 2: return sub_bang(context, args[0], args[1], block);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject gsub(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1: return gsub(context, args[0], block);
         case 2: return gsub(context, args[0], args[1], block);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject gsub_bang(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1: return gsub_bang(context, args[0], block);
         case 2: return gsub_bang(context, args[0], args[1], block);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject index(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return index(context, args[0]);
         case 2: return index(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject rindex(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return rindex(context, args[0]);
         case 2: return rindex(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject op_aref(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return op_aref(context, args[0]);
         case 2: return op_aref(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject op_aset(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 2: return op_aset(context, args[0], args[1]);
         case 3: return op_aset(context, args[0], args[1], args[2]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 2, 3); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject slice_bang(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return slice_bang(context, args[0]);
         case 2: return slice_bang(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject to_i(IRubyObject[] args) {
         switch (args.length) {
         case 0: return to_i();
         case 1: return to_i(args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 
     @Deprecated
     public RubyArray split(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 0: return split(context);
         case 1: return split(context, args[0]);
         case 2: return split(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 0, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject ljust(IRubyObject [] args) {
         switch (args.length) {
         case 1: return ljust(args[0]);
         case 2: return ljust(args[0], args[1]);
         default: Arity.raiseArgumentError(getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject rjust(IRubyObject [] args) {
         switch (args.length) {
         case 1: return rjust(args[0]);
         case 2: return rjust(args[0], args[1]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject center(IRubyObject [] args) {
         switch (args.length) {
         case 1: return center(args[0]);
         case 2: return center(args[0], args[1]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public RubyString chomp(IRubyObject[] args) {
         switch (args.length) {
         case 0:return chomp(getRuntime().getCurrentContext());
         case 1:return chomp(getRuntime().getCurrentContext(), args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject chomp_bang(IRubyObject[] args) {
         switch (args.length) {
         case 0: return chomp_bang(getRuntime().getCurrentContext());
         case 1: return chomp_bang(getRuntime().getCurrentContext(), args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 }
diff --git a/src/org/jruby/RubySymbol.java b/src/org/jruby/RubySymbol.java
index 7f48d6a9b6..65e0cbff87 100644
--- a/src/org/jruby/RubySymbol.java
+++ b/src/org/jruby/RubySymbol.java
@@ -1,827 +1,828 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Derek Berner <derek.berner@state.nm.us>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 package org.jruby;
 
 import static org.jruby.util.StringSupport.codeLength;
 import static org.jruby.util.StringSupport.codePoint;
 
 import java.util.concurrent.locks.ReentrantLock;
 
 import org.jcodings.Encoding;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 
 /**
  * Represents a Ruby symbol (e.g. :bar)
  */
 @JRubyClass(name="Symbol")
 public class RubySymbol extends RubyObject {
     private final String symbol;
     private final int id;
     private final ByteList symbolBytes;
     
     /**
      * 
      * @param runtime
      * @param internedSymbol the String value of the new Symbol. This <em>must</em>
      *                       have been previously interned
      */
     private RubySymbol(Ruby runtime, String internedSymbol, ByteList symbolBytes) {
         super(runtime, runtime.getSymbol(), false, false);
         // symbol string *must* be interned
 
         //        assert internedSymbol == internedSymbol.intern() : internedSymbol + " is not interned";
 
         int length = symbolBytes.getBegin() + symbolBytes.getRealSize();
         for (int i = symbolBytes.getBegin(); i < length; i++) {
             if (symbolBytes.getUnsafeBytes()[i] == 0) {
                 throw runtime.newSyntaxError("symbol cannot contain '\\0'");
             }
         }
 
         this.symbol = internedSymbol;
         this.symbolBytes = symbolBytes;
         this.id = runtime.allocSymbolId();
     }
 
     private RubySymbol(Ruby runtime, String internedSymbol) {
         this(runtime, internedSymbol, ByteList.create(internedSymbol));
     }
 
     public static RubyClass createSymbolClass(Ruby runtime) {
         RubyClass symbolClass = runtime.defineClass("Symbol", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setSymbol(symbolClass);
         RubyClass symbolMetaClass = symbolClass.getMetaClass();
         symbolClass.index = ClassIndex.SYMBOL;
         symbolClass.setReifiedClass(RubySymbol.class);
         symbolClass.kindOf = new RubyModule.KindOf() {
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubySymbol;
             }
         };
 
         symbolClass.defineAnnotatedMethods(RubySymbol.class);
         symbolMetaClass.undefineMethod("new");
         
         return symbolClass;
     }
     
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.SYMBOL;
     }
 
     /** rb_to_id
      * 
      * @return a String representation of the symbol 
      */
     @Override
     public String asJavaString() {
         return symbol;
     }
 
     @Override
     public RubyString convertToString() {
         Ruby runtime = getRuntime();
         return runtime.is1_9() ? newShared(runtime) : super.convertToString(); 
     }
 
     @Override
     public String toString() {
         return symbol;
     }
 
     final ByteList getBytes() {
         return symbolBytes;
     }
 
     /** short circuit for Symbol key comparison
      * 
      */
     @Override
     public final boolean eql(IRubyObject other) {
         return other == this;
     }
 
     @Override
     public boolean isImmediate() {
     	return true;
     }
 
     @Override
     public RubyClass getSingletonClass() {
         throw getRuntime().newTypeError("can't define singleton");
     }
 
     public static RubySymbol getSymbolLong(Ruby runtime, long id) {
         return runtime.getSymbolTable().lookup(id);
     }
 
     /* Symbol class methods.
      * 
      */
 
     public static RubySymbol newSymbol(Ruby runtime, String name) {
         return runtime.getSymbolTable().getSymbol(name);
     }
 
     @Deprecated
     public RubyFixnum to_i() {
         return to_i(getRuntime());
     }
 
     @JRubyMethod(name = "to_i", compat = CompatVersion.RUBY1_8)
     public RubyFixnum to_i(ThreadContext context) {
         return to_i(context.getRuntime());
     }
 
     private final RubyFixnum to_i(Ruby runtime) {
         return runtime.newFixnum(id);
     }
 
     @Deprecated
     public RubyFixnum to_int() {
         return to_int(getRuntime());
     }
 
     @JRubyMethod(name = "to_int", compat = CompatVersion.RUBY1_8)
     public RubyFixnum to_int(ThreadContext context) {
         return to_int(context.getRuntime());
     }
 
     private final RubyFixnum to_int(Ruby runtime) {
         if (runtime.isVerbose()) {
             runtime.getWarnings().warn(ID.SYMBOL_AS_INTEGER, "treating Symbol as an integer");
         }
         return to_i(runtime);
     }
 
     @Deprecated
     @Override
     public IRubyObject inspect() {
         return inspect(getRuntime());
     }
     @JRubyMethod(name = "inspect", compat = CompatVersion.RUBY1_8)
     public IRubyObject inspect(ThreadContext context) {
         return inspect(context.getRuntime());
     }
     private final IRubyObject inspect(Ruby runtime) {
         
         final ByteList bytes;
         if (isSymbolName(symbol)) {
             bytes = symbolBytes;
         } else {
             bytes = ((RubyString)RubyString.newString(runtime, symbolBytes).dump()).getByteList();
         }
         ByteList result = new ByteList(bytes.getRealSize() + 1);
         result.append((byte)':');
         result.append(bytes);
 
         return RubyString.newString(runtime, result);
     }
 
     @Deprecated
     public IRubyObject inspect19() {
         return inspect19(getRuntime());
     }
     @JRubyMethod(name = "inspect", compat = CompatVersion.RUBY1_9)
     public IRubyObject inspect19(ThreadContext context) {
         return inspect19(context.getRuntime());
     }
     private final IRubyObject inspect19(Ruby runtime) {
         
         ByteList result = new ByteList(symbolBytes.getRealSize() + 1);
         result.setEncoding(symbolBytes.getEncoding());
         result.append((byte)':');
         result.append(symbolBytes);
 
         RubyString str = RubyString.newString(runtime, result); 
         if (isPrintable() && isSymbolName(symbol)) { // TODO: 1.9 rb_enc_symname_p
             return str;
         } else {
             str = (RubyString)str.inspect19();
             ByteList bytes = str.getByteList();
             bytes.set(0, ':');
             bytes.set(1, '"');
             return str;
         }
     }
 
     @Override
     public IRubyObject to_s() {
         return to_s(getRuntime());
     }
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s(ThreadContext context) {
         return to_s(context.getRuntime());
     }
     private final IRubyObject to_s(Ruby runtime) {
         return RubyString.newStringShared(runtime, symbolBytes);
     }
 
     public IRubyObject id2name() {
         return to_s(getRuntime());
     }
     @JRubyMethod(name = "id2name")
     public IRubyObject id2name(ThreadContext context) {
         return to_s(context);
     }
 
     @JRubyMethod(name = "===", required = 1)
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return super.op_equal(context, other);
     }
 
     @Deprecated
     @Override
     public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
 
     @JRubyMethod(name = "hash")
     public RubyFixnum hash(ThreadContext context) {
         return context.getRuntime().newFixnum(hashCode());
     }
     
     @Override
     public int hashCode() {
         return id;
     }
 
     public int getId() {
         return id;
     }
     
     @Override
     public boolean equals(Object other) {
         return other == this;
     }
     
     @JRubyMethod(name = "to_sym")
     public IRubyObject to_sym() {
         return this;
     }
 
     @Override
     public IRubyObject taint(ThreadContext context) {
         return this;
     }
 
     private RubyString newShared(Ruby runtime) {
         return RubyString.newStringShared(runtime, symbolBytes);
     }
 
     @JRubyMethod(name = {"succ", "next"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject succ(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return newSymbol(runtime, newShared(runtime).succ19(context).toString());
     }
 
     @JRubyMethod(name = "<=>", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (other instanceof RubySymbol) {
             return (newShared(runtime).op_cmp19(context, ((RubySymbol)other).newShared(runtime)));
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "casecmp", compat = CompatVersion.RUBY1_9)
     public IRubyObject casecmp(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (other instanceof RubySymbol) {
             return newShared(runtime).casecmp19(context, ((RubySymbol) other).newShared(runtime));
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = {"=~", "match"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_match19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         return newShared(runtime).op_match19(context, other);
     }
 
     @JRubyMethod(name = {"[]", "slice"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_aref(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         return newShared(runtime).op_aref19(context, arg);
     }
 
     @JRubyMethod(name = {"[]", "slice"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_aref(ThreadContext context, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
         return newShared(runtime).op_aref19(context, arg1, arg2);
     }
 
     @JRubyMethod(name = {"length", "size"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject length() {
         return newShared(getRuntime()).length19();
     }
 
     @JRubyMethod(name = "empty?", compat = CompatVersion.RUBY1_9)
     public IRubyObject empty_p(ThreadContext context) {
         return newShared(context.getRuntime()).empty_p(context);
     }
 
     @JRubyMethod(name = "upcase", compat = CompatVersion.RUBY1_9)
     public IRubyObject upcase(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return newSymbol(runtime, newShared(runtime).upcase19(context).toString());
     }
 
     @JRubyMethod(name = "downcase", compat = CompatVersion.RUBY1_9)
     public IRubyObject downcase(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return newSymbol(runtime, newShared(runtime).downcase19(context).toString());
     }
 
     @JRubyMethod(name = "capitalize", compat = CompatVersion.RUBY1_9)
     public IRubyObject capitalize(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return newSymbol(runtime, newShared(runtime).capitalize19(context).toString());
     }
 
     @JRubyMethod(name = "swapcase", compat = CompatVersion.RUBY1_9)
     public IRubyObject swapcase(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return newSymbol(runtime, newShared(runtime).swapcase19(context).toString());
     }
 
     @JRubyMethod(name = "encoding", compat = CompatVersion.RUBY1_9)
     public IRubyObject encoding(ThreadContext context) {
         return context.getRuntime().getEncodingService().getEncoding(symbolBytes.getEncoding());
     }
 
     private static class ToProcCallback implements BlockCallback {
         private RubySymbol symbol;
         public ToProcCallback(RubySymbol symbol) {
             this.symbol = symbol;
         }
 
         public IRubyObject call(ThreadContext ctx, IRubyObject[] args, Block blk) {
             IRubyObject[] currentArgs = args;
             switch(currentArgs.length) {
             case 0: throw symbol.getRuntime().newArgumentError("no receiver given");
             case 1: {
                 if((currentArgs[0] instanceof RubyArray) && ((RubyArray)currentArgs[0]).getLength() != 0) {
                     // This is needed to unpack stuff
                     currentArgs = ((RubyArray)currentArgs[0]).toJavaArrayMaybeUnsafe();
                     IRubyObject[] args2 = new IRubyObject[currentArgs.length-1];
                     System.arraycopy(currentArgs, 1, args2, 0, args2.length);
                     return RuntimeHelpers.invoke(ctx, currentArgs[0], symbol.symbol, args2);
                 } else {
                     return RuntimeHelpers.invoke(ctx, currentArgs[0], symbol.symbol);
                 }
             }
             default: {
                 IRubyObject[] args2 = new IRubyObject[currentArgs.length-1];
                 System.arraycopy(currentArgs, 1, args2, 0, args2.length);
                 return RuntimeHelpers.invoke(ctx, currentArgs[0], symbol.symbol, args2);
             }
             }
         }
     }
     /*
     @JRubyMethod
     public IRubyObject to_proc() {
         return RubyProc.newProc(getRuntime(),
                                 CallBlock.newCallClosure(this, getRuntime().getSymbol(), Arity.noArguments(), new ToProcCallback(this), getRuntime().getCurrentContext()),
                                 Block.Type.PROC);
     }
     */
     private static boolean isIdentStart(char c) {
         return ((c >= 'a' && c <= 'z')|| (c >= 'A' && c <= 'Z')
                 || c == '_');
     }
     private static boolean isIdentChar(char c) {
         return ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')
                 || c == '_');
     }
     
     private static boolean isIdentifier(String s) {
         if (s == null || s.length() <= 0) {
             return false;
         } 
         
         if (!isIdentStart(s.charAt(0))) {
             return false;
         }
         for (int i = 1; i < s.length(); i++) {
             if (!isIdentChar(s.charAt(i))) {
                 return false;
             }
         }
         
         return true;
     }
     
     /**
      * is_special_global_name from parse.c.  
      * @param s
      * @return
      */
     private static boolean isSpecialGlobalName(String s) {
         if (s == null || s.length() <= 0) {
             return false;
         }
 
         int length = s.length();
            
         switch (s.charAt(0)) {        
         case '~': case '*': case '$': case '?': case '!': case '@': case '/': case '\\':        
         case ';': case ',': case '.': case '=': case ':': case '<': case '>': case '\"':        
         case '&': case '`': case '\'': case '+': case '0':
             return length == 1;            
         case '-':
             return (length == 1 || (length == 2 && isIdentChar(s.charAt(1))));
             
         default:
             // we already confirmed above that length > 0
             for (int i = 0; i < length; i++) {
                 if (!Character.isDigit(s.charAt(i))) {
                     return false;
                 }
             }
         }
         return true;
     }
 
     private boolean isPrintable() {
         Ruby runtime = getRuntime();
         int p = symbolBytes.getBegin();
         int end = p + symbolBytes.getRealSize();
         byte[]bytes = symbolBytes.getUnsafeBytes();
         Encoding enc = symbolBytes.getEncoding();
 
         while (p < end) {
             int c = codePoint(runtime, enc, bytes, p, end);
             if (!enc.isPrint(c)) return false;
             p += codeLength(runtime, enc, c);
         }
         return true;
     }
 
     private static boolean isSymbolName(String s) {
         if (s == null || s.length() < 1) return false;
 
         int length = s.length();
 
         char c = s.charAt(0);
         switch (c) {
         case '$':
             if (length > 1 && isSpecialGlobalName(s.substring(1))) {
                 return true;
             }
             return isIdentifier(s.substring(1));
         case '@':
             int offset = 1;
             if (length >= 2 && s.charAt(1) == '@') {
                 offset++;
             }
 
             return isIdentifier(s.substring(offset));
         case '<':
             return (length == 1 || (length == 2 && (s.equals("<<") || s.equals("<="))) || 
                     (length == 3 && s.equals("<=>")));
         case '>':
             return (length == 1) || (length == 2 && (s.equals(">>") || s.equals(">=")));
         case '=':
             return ((length == 2 && (s.equals("==") || s.equals("=~"))) || 
                     (length == 3 && s.equals("===")));
         case '*':
             return (length == 1 || (length == 2 && s.equals("**")));
         case '+':
             return (length == 1 || (length == 2 && s.equals("+@")));
         case '-':
             return (length == 1 || (length == 2 && s.equals("-@")));
         case '|': case '^': case '&': case '/': case '%': case '~': case '`':
             return length == 1;
         case '[':
             return s.equals("[]") || s.equals("[]=");
         }
         
         if (!isIdentStart(c)) return false;
 
         boolean localID = (c >= 'a' && c <= 'z');
         int last = 1;
         
         for (; last < length; last++) {
             char d = s.charAt(last);
             
             if (!isIdentChar(d)) {
                 break;
             }
         }
                     
         if (last == length) {
             return true;
         } else if (localID && last == length - 1) {
             char d = s.charAt(last);
             
             return d == '!' || d == '?' || d == '=';
         }
         
         return false;
     }
     
     @JRubyMethod(name = "all_symbols", meta = true)
     public static IRubyObject all_symbols(ThreadContext context, IRubyObject recv) {
         return context.getRuntime().getSymbolTable().all_symbols();
     }
     @Deprecated
     public static IRubyObject all_symbols(IRubyObject recv) {
         return recv.getRuntime().getSymbolTable().all_symbols();
     }
 
     public static RubySymbol unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubySymbol result = newSymbol(input.getRuntime(), RubyString.byteListToString(input.unmarshalString()));
         input.registerLinkTarget(result);
         return result;
     }
 
+    @Override
     public Object toJava(Class target) {
         if (target == String.class || target == CharSequence.class) {
             return symbol;
         }
         return super.toJava(target);
     }
 
     public static final class SymbolTable {
         static final int DEFAULT_INITIAL_CAPACITY = 2048; // *must* be power of 2!
         static final int MAXIMUM_CAPACITY = 1 << 30;
         static final float DEFAULT_LOAD_FACTOR = 0.75f;
         
         private final ReentrantLock tableLock = new ReentrantLock();
         private volatile SymbolEntry[] symbolTable;
         private int size;
         private int threshold;
         private final float loadFactor;
         private final Ruby runtime;
         
         public SymbolTable(Ruby runtime) {
             this.runtime = runtime;
             this.loadFactor = DEFAULT_LOAD_FACTOR;
             this.threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
             this.symbolTable = new SymbolEntry[DEFAULT_INITIAL_CAPACITY];
         }
         
         // note all fields are final -- rehash creates new entries when necessary.
         // as documented in java.util.concurrent.ConcurrentHashMap.java, that will
         // statistically affect only a small percentage (< 20%) of entries for a given rehash.
         static class SymbolEntry {
             final int hash;
             final String name;
             final RubySymbol symbol;
             final SymbolEntry next;
             
             SymbolEntry(int hash, String name, RubySymbol symbol, SymbolEntry next) {
                 this.hash = hash;
                 this.name = name;
                 this.symbol = symbol;
                 this.next = next;
             }
         }
 
         public RubySymbol getSymbol(String name) {
             int hash = name.hashCode();
             SymbolEntry[] table = symbolTable;
             SymbolEntry e = getEntryFromTable(table, hash);
             for (; e != null; e = e.next) {
                 if (isSymbolMatch(name, hash, e)) return e.symbol;
             }
             return createSymbol(name, ByteList.create(name), hash, table);
         }
 
         public RubySymbol getSymbol(ByteList bytes) {
             String name = bytes.toString();
             int hash = name.hashCode();
             SymbolEntry[] table = symbolTable;
             SymbolEntry e = getEntryFromTable(table, hash);
             for (; e != null; e = e.next) {
                 if (isSymbolMatch(name, hash, e)) return e.symbol;
             }
             return createSymbol(name, bytes, hash, table);
         }
 
         public RubySymbol fastGetSymbol(String internedName) {
             //            assert internedName == internedName.intern() : internedName + " is not interned";
             SymbolEntry[] table = symbolTable;
             SymbolEntry e = getEntryFromTable(symbolTable, internedName.hashCode());
             for (; e != null; e = e.next) {
                 if (isSymbolMatch(internedName, e)) return e.symbol;
             }
             return fastCreateSymbol(internedName, table);
         }
 
         private static SymbolEntry getEntryFromTable(SymbolEntry[] table, int hash) {
             return table[hash & (table.length - 1)];
         }
 
         private static boolean isSymbolMatch(String name, int hash, SymbolEntry entry) {
             return hash == entry.hash && name.equals(entry.name);
         }
 
         private static boolean isSymbolMatch(String internedName, SymbolEntry entry) {
             return internedName == entry.name;
         }
 
         private RubySymbol createSymbol(String name, ByteList value, int hash, SymbolEntry[] table) {
             ReentrantLock lock;
             (lock = tableLock).lock();
             try {
                 int potentialNewSize;
                 if ((potentialNewSize = size + 1) > threshold) {
                     table = rehash();
                 } else {
                     table = symbolTable;
                 }
                 int index;
                 // try lookup again under lock
                 for (SymbolEntry e = table[index = hash & (table.length - 1)]; e != null; e = e.next) {
                     if (hash == e.hash && name.equals(e.name)) {
                         return e.symbol;
                     }
                 }
                 String internedName;
                 RubySymbol symbol = new RubySymbol(runtime, internedName = name.intern(), value);
                 table[index] = new SymbolEntry(hash, internedName, symbol, table[index]);
                 size = potentialNewSize;
                 // write-volatile
                 symbolTable = table;
                 return symbol;
             } finally {
                 lock.unlock();
             }
         }
 
         private RubySymbol fastCreateSymbol(String internedName, SymbolEntry[] table) {
             ReentrantLock lock;
             (lock = tableLock).lock();
             try {
                 int potentialNewSize;
                 if ((potentialNewSize = size + 1) > threshold) {
                     table = rehash();
                 } else {
                     table = symbolTable;
                 }
                 int index;
                 int hash;
                 // try lookup again under lock
                 for (SymbolEntry e = table[index = (hash = internedName.hashCode()) & (table.length - 1)]; e != null; e = e.next) {
                     if (internedName == e.name) {
                         return e.symbol;
                     }
                 }
                 RubySymbol symbol = new RubySymbol(runtime, internedName);
                 table[index] = new SymbolEntry(hash, internedName, symbol, table[index]);
                 size = potentialNewSize;
                 // write-volatile
                 symbolTable = table;
                 return symbol;
             } finally {
                 lock.unlock();
             }
         }
         
         // backwards-compatibility, but threadsafe now
         public RubySymbol lookup(String name) {
             int hash = name.hashCode();
             SymbolEntry[] table;
             for (SymbolEntry e = (table = symbolTable)[hash & (table.length - 1)]; e != null; e = e.next) {
                 if (hash == e.hash && name.equals(e.name)) {
                     return e.symbol;
                 }
             }
             return null;
         }
         
         public RubySymbol lookup(long id) {
             SymbolEntry[] table = symbolTable;
             for (int i = table.length; --i >= 0; ) {
                 for (SymbolEntry e = table[i]; e != null; e = e.next) {
                     if (id == e.symbol.id) {
                         return e.symbol;
                     }
                 }
             }
             return null;
         }
         
         public RubyArray all_symbols() {
             SymbolEntry[] table = this.symbolTable;
             RubyArray array = runtime.newArray(this.size);
             for (int i = table.length; --i >= 0; ) {
                 for (SymbolEntry e = table[i]; e != null; e = e.next) {
                     array.append(e.symbol);
                 }
             }
             return array;
         }
         
         // not so backwards-compatible here, but no one should have been
         // calling this anyway.
         @Deprecated
         public void store(RubySymbol symbol) {
             throw new UnsupportedOperationException();
         }
         
         private SymbolEntry[] rehash() {
             SymbolEntry[] oldTable = symbolTable;
             int oldCapacity;
             if ((oldCapacity = oldTable.length) >= MAXIMUM_CAPACITY) {
                 return oldTable;
             }
             
             int newCapacity = oldCapacity << 1;
             SymbolEntry[] newTable = new SymbolEntry[newCapacity];
             threshold = (int)(newCapacity * loadFactor);
             int sizeMask = newCapacity - 1;
             SymbolEntry e;
             for (int i = oldCapacity; --i >= 0; ) {
                 // We need to guarantee that any existing reads of old Map can
                 //  proceed. So we cannot yet null out each bin.
                 e = oldTable[i];
 
                 if (e != null) {
                     SymbolEntry next = e.next;
                     int idx = e.hash & sizeMask;
 
                     //  Single node on list
                     if (next == null)
                         newTable[idx] = e;
 
                     else {
                         // Reuse trailing consecutive sequence at same slot
                         SymbolEntry lastRun = e;
                         int lastIdx = idx;
                         for (SymbolEntry last = next;
                              last != null;
                              last = last.next) {
                             int k = last.hash & sizeMask;
                             if (k != lastIdx) {
                                 lastIdx = k;
                                 lastRun = last;
                             }
                         }
                         newTable[lastIdx] = lastRun;
 
                         // Clone all remaining nodes
                         for (SymbolEntry p = e; p != lastRun; p = p.next) {
                             int k = p.hash & sizeMask;
                             SymbolEntry n = newTable[k];
                             newTable[k] = new SymbolEntry(p.hash, p.name, p.symbol, n);
                         }
                     }
                 }
             }
             symbolTable = newTable;
             return newTable;
         }
         
     }
 }
diff --git a/src/org/jruby/RubyTime.java b/src/org/jruby/RubyTime.java
index 1f57f8fe2a..e414e9a0a8 100644
--- a/src/org/jruby/RubyTime.java
+++ b/src/org/jruby/RubyTime.java
@@ -1,937 +1,956 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2009 Joseph LaFata <joe@quibb.org>
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
 package org.jruby;
 
+import java.util.Calendar;
 import java.util.Date;
+import java.util.GregorianCalendar;
 import java.util.HashMap;
 import java.util.Locale;
 import java.util.Map;
 import java.util.TimeZone;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeZone;
 import org.joda.time.format.DateTimeFormat;
 import org.joda.time.format.DateTimeFormatter;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.RubyDateFormat;
 
 /** The Time class.
  * 
  * @author chadfowler, jpetersen
  */
 @JRubyClass(name="Time", include="Comparable")
 public class RubyTime extends RubyObject {
     public static final String UTC = "UTC";
     private DateTime dt;
     private long usec;
     
     private final static DateTimeFormatter ONE_DAY_CTIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM  d HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
     private final static DateTimeFormatter TWO_DAY_CTIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
 
     private final static DateTimeFormatter TO_S_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy").withLocale(Locale.ENGLISH);
     private final static DateTimeFormatter TO_S_UTC_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy").withLocale(Locale.ENGLISH);
 
     // There are two different popular TZ formats: legacy (AST+3:00:00, GMT-3), and
     // newer one (US/Pacific, America/Los_Angeles). This pattern is to detect
     // the legacy TZ format in order to convert it to the newer format
     // understood by Java API.
     private static final Pattern TZ_PATTERN
             = Pattern.compile("(\\D+?)([\\+-]?)(\\d+)(:\\d+)?(:\\d+)?");
     
     private static final ByteList TZ_STRING = ByteList.create("TZ");
 
     public static DateTimeZone getLocalTimeZone(Ruby runtime) {
         RubyString tzVar = runtime.newString(TZ_STRING);
         RubyHash h = ((RubyHash)runtime.getObject().fastGetConstant("ENV"));
         IRubyObject tz = h.op_aref(runtime.getCurrentContext(), tzVar);
 
         if (tz == null || ! (tz instanceof RubyString)) {
             return DateTimeZone.getDefault();
         } else {
             return getTimeZone(runtime, tz.toString());
         }
     }
      
     public static DateTimeZone getTimeZone(Ruby runtime, String zone) {
         DateTimeZone cachedZone = runtime.getTimezoneCache().get(zone);
 
         if (cachedZone != null) return cachedZone;
 
         String originalZone = zone;
 
         // Value of "TZ" property is of a bit different format,
         // which confuses the Java's TimeZone.getTimeZone(id) method,
         // and so, we need to convert it.
 
         Matcher tzMatcher = TZ_PATTERN.matcher(zone);
         if (tzMatcher.matches()) {                    
             String sign = tzMatcher.group(2);
             String hours = tzMatcher.group(3);
             String minutes = tzMatcher.group(4);
                 
             // GMT+00:00 --> Etc/GMT, see "MRI behavior"
             // comment below.
             if (("00".equals(hours) || "0".equals(hours))
                     && (minutes == null || ":00".equals(minutes) || ":0".equals(minutes))) {
                 zone = "Etc/GMT";
             } else {
                 // Invert the sign, since TZ format and Java format
                 // use opposite signs, sigh... Also, Java API requires
                 // the sign to be always present, be it "+" or "-".
                 sign = ("-".equals(sign)? "+" : "-");
 
                 // Always use "GMT" since that's required by Java API.
                 zone = "GMT" + sign + hours;
 
                 if (minutes != null) {
                     zone += minutes;
                 }
             }
         }
 
         // MRI behavior: With TZ equal to "GMT" or "UTC", Time.now
         // is *NOT* considered as a proper GMT/UTC time:
         //   ENV['TZ']="GMT"
         //   Time.now.gmt? ==> false
         //   ENV['TZ']="UTC"
         //   Time.now.utc? ==> false
         // Hence, we need to adjust for that.
         if ("GMT".equalsIgnoreCase(zone) || "UTC".equalsIgnoreCase(zone)) {
             zone = "Etc/" + zone;
         }
 
         // For JRUBY-2759, when MET choose CET timezone to work around Joda
         if ("MET".equalsIgnoreCase(zone)) {
             zone = "CET";
         }
 
         DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getTimeZone(zone));
         runtime.getTimezoneCache().put(originalZone, dtz);
         return dtz;
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass, DateTime dt) {
         super(runtime, rubyClass);
         this.dt = dt;
     }
 
     private static ObjectAllocator TIME_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             DateTimeZone dtz = getLocalTimeZone(runtime);
             DateTime dt = new DateTime(dtz);
             RubyTime rt =  new RubyTime(runtime, klass, dt);
             rt.setUSec(0);
 
             return rt;
         }
     };
 
     public static RubyClass createTimeClass(Ruby runtime) {
         RubyClass timeClass = runtime.defineClass("Time", runtime.getObject(), TIME_ALLOCATOR);
 
         timeClass.index = ClassIndex.TIME;
         timeClass.setReifiedClass(RubyTime.class);
         
         runtime.setTime(timeClass);
         
         timeClass.includeModule(runtime.getComparable());
         
         timeClass.defineAnnotatedMethods(RubyTime.class);
         
         return timeClass;
     }
     
     public void setUSec(long usec) {
         this.usec = usec;
     }
     
     public long getUSec() {
         return usec;
     }
     
     public void updateCal(DateTime dt) {
         this.dt = dt;
     }
     
     protected long getTimeInMillis() {
         return dt.getMillis();  // For JDK 1.4 we can use "cal.getTimeInMillis()"
     }
     
     public static RubyTime newTime(Ruby runtime, long milliseconds) {
         return newTime(runtime, new DateTime(milliseconds));
     }
     
     public static RubyTime newTime(Ruby runtime, DateTime dt) {
         return new RubyTime(runtime, runtime.getTime(), dt);
     }
     
     public static RubyTime newTime(Ruby runtime, DateTime dt, long usec) {
         RubyTime t = new RubyTime(runtime, runtime.getTime(), dt);
         t.setUSec(usec);
         return t;
     }
     
     @Override
     public Class<?> getJavaClass() {
         return Date.class;
     }
 
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original) {
         if (!(original instanceof RubyTime)) {
             throw getRuntime().newTypeError("Expecting an instance of class Time");
         }
         
         RubyTime originalTime = (RubyTime) original;
         
         // We can just use dt, since it is immutable
         dt = originalTime.dt;
         usec = originalTime.usec;
         
         return this;
     }
 
     @JRubyMethod(name = "succ")
     public RubyTime succ() {
         return newTime(getRuntime(),dt.plusSeconds(1));
     }
 
     @JRubyMethod(name = {"gmtime", "utc"})
     public RubyTime gmtime() {
         dt = dt.withZone(DateTimeZone.UTC);
         return this;
     }
 
     @JRubyMethod(name = "localtime")
     public RubyTime localtime() {
         dt = dt.withZone(getLocalTimeZone(getRuntime()));
         return this;
     }
     
     @JRubyMethod(name = {"gmt?", "utc?", "gmtime?"})
     public RubyBoolean gmt() {
         return getRuntime().newBoolean(dt.getZone().getID().equals("UTC"));
     }
     
     @JRubyMethod(name = {"getgm", "getutc"})
     public RubyTime getgm() {
         return newTime(getRuntime(), dt.withZone(DateTimeZone.UTC), getUSec());
     }
 
     @JRubyMethod(name = "getlocal")
     public RubyTime getlocal() {
         return newTime(getRuntime(), dt.withZone(getLocalTimeZone(getRuntime())), getUSec());
     }
 
     @JRubyMethod(name = "strftime", required = 1)
     public RubyString strftime(IRubyObject format) {
         final RubyDateFormat rubyDateFormat = new RubyDateFormat("-", Locale.US, getRuntime().is1_9());
         rubyDateFormat.applyPattern(format.convertToString().getUnicodeValue());
         rubyDateFormat.setDateTime(dt);
         String result = rubyDateFormat.format(null);
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = "==", required = 1, compat= CompatVersion.RUBY1_9)
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         if (other.isNil()) {
             return RubyBoolean.newBoolean(getRuntime(), false);
         } else if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) == 0);
         }
 
         return RubyComparable.op_equal(context, this, other);
     }
     
     @JRubyMethod(name = ">=", required = 1)
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) >= 0);
         }
         
         return RubyComparable.op_ge(context, this, other);
     }
     
     @JRubyMethod(name = ">", required = 1)
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) > 0);
         }
         
         return RubyComparable.op_gt(context, this, other);
     }
     
     @JRubyMethod(name = "<=", required = 1)
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) <= 0);
         }
         
         return RubyComparable.op_le(context, this, other);
     }
     
     @JRubyMethod(name = "<", required = 1)
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) < 0);
         }
         
         return RubyComparable.op_lt(context, this, other);
     }
     
     private int cmp(RubyTime other) {
         long millis = getTimeInMillis();
 		long millis_other = other.getTimeInMillis();
         long usec_other = other.usec;
         
 		if (millis > millis_other || (millis == millis_other && usec > usec_other)) {
 		    return 1;
 		} else if (millis < millis_other || (millis == millis_other && usec < usec_other)) {
 		    return -1;
 		} 
 
         return 0;
     }
     
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(IRubyObject other) {
         long time = getTimeInMillis();
 
         if (other instanceof RubyTime) {
             throw getRuntime().newTypeError("time + time ?");
         }
         
         long adjustment = Math.round(RubyNumeric.num2dbl(other) * 1000000);
         long micro = adjustment % 1000;
         adjustment = adjustment / 1000;
 
         time += adjustment;
 
         if ((getUSec() + micro) >= 1000) {
             time++;
             micro = (getUSec() + micro) - 1000;
         } else {
             micro = getUSec() + micro;
         }
 
         RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
         newTime.dt = new DateTime(time).withZone(dt.getZone());
         newTime.setUSec(micro);
 
         return newTime;
     }
     
     private IRubyObject opMinus(RubyTime other) {
         long time = getTimeInMillis() * 1000 + getUSec();
 
         time -= other.getTimeInMillis() * 1000 + other.getUSec();
         
         return RubyFloat.newFloat(getRuntime(), time / 1000000.0); // float number of seconds
     }
 
     @JRubyMethod(name = "-", required = 1)
     public IRubyObject op_minus(IRubyObject other) {
         if (other instanceof RubyTime) return opMinus((RubyTime) other);
         
         long time = getTimeInMillis();
         long adjustment = Math.round(RubyNumeric.num2dbl(other) * 1000000);
         long micro = adjustment % 1000;
         adjustment = adjustment / 1000;
 
         time -= adjustment;
 
         if (getUSec() < micro) {
             time--;
             micro = 1000 - (micro - getUSec());
         } else {
             micro = getUSec() - micro;
         }
 
         RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
         newTime.dt = new DateTime(time).withZone(dt.getZone());
         newTime.setUSec(micro);
 
         return newTime;
     }
 
     @JRubyMethod(name = "===", required = 1)
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return (RubyNumeric.fix2int(callMethod(context, "<=>", other)) == 0) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return context.getRuntime().newFixnum(cmp((RubyTime) other));
         }
 
         return context.getRuntime().getNil();
     }
     
     @JRubyMethod(name = "eql?", required = 1)
     @Override
     public IRubyObject eql_p(IRubyObject other) {
         if (other instanceof RubyTime) {
             RubyTime otherTime = (RubyTime)other; 
             return (usec == otherTime.usec && getTimeInMillis() == otherTime.getTimeInMillis()) ? getRuntime().getTrue() : getRuntime().getFalse();
         }
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(name = {"asctime", "ctime"})
     public RubyString asctime() {
         DateTimeFormatter simpleDateFormat;
 
         if (dt.getDayOfMonth() < 10) {
             simpleDateFormat = ONE_DAY_CTIME_FORMATTER;
         } else {
             simpleDateFormat = TWO_DAY_CTIME_FORMATTER;
         }
         String result = simpleDateFormat.print(dt);
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = {"to_s", "inspect"})
     @Override
     public IRubyObject to_s() {
         DateTimeFormatter simpleDateFormat;
         if (dt.getZone() == DateTimeZone.UTC) {
             simpleDateFormat = TO_S_UTC_FORMATTER;
         } else {
             simpleDateFormat = TO_S_FORMATTER;
         }
 
         String result = simpleDateFormat.print(dt);
 
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = "to_a")
     @Override
     public RubyArray to_a() {
         return getRuntime().newArrayNoCopy(new IRubyObject[] { sec(), min(), hour(), mday(), month(), 
                 year(), wday(), yday(), isdst(), zone() });
     }
 
     @JRubyMethod(name = "to_f")
     public RubyFloat to_f() {
         long time = getTimeInMillis();
         time = time * 1000 + usec;
         return RubyFloat.newFloat(getRuntime(), time / 1000000.0);
     }
 
     @JRubyMethod(name = {"to_i", "tv_sec"})
     public RubyInteger to_i() {
         return getRuntime().newFixnum(getTimeInMillis() / 1000);
     }
 
     @JRubyMethod(name = "to_r", backtrace = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject to_r(ThreadContext context) {
         IRubyObject rational = to_f().to_r(context);
         if (rational instanceof RubyRational) {
             IRubyObject denominator = ((RubyRational)rational).denominator(context);
             if (RubyNumeric.num2long(denominator) == 1) {
                 return ((RubyRational)rational).numerator(context);
             }
         }
 
         return rational;
     }
 
     @JRubyMethod(name = {"usec", "tv_usec"})
     public RubyInteger usec() {
         return getRuntime().newFixnum(dt.getMillisOfSecond() * 1000 + getUSec());
     }
 
     public void setMicroseconds(long mic) {
         long millis = getTimeInMillis() % 1000;
         long withoutMillis = getTimeInMillis() - millis;
         withoutMillis += (mic / 1000);
         dt = dt.withMillis(withoutMillis);
         usec = mic % 1000;
     }
     
     public long microseconds() {
     	return getTimeInMillis() % 1000 * 1000 + usec;
     }
 
     @JRubyMethod(name = "sec")
     public RubyInteger sec() {
         return getRuntime().newFixnum(dt.getSecondOfMinute());
     }
 
     @JRubyMethod(name = "min")
     public RubyInteger min() {
         return getRuntime().newFixnum(dt.getMinuteOfHour());
     }
 
     @JRubyMethod(name = "hour")
     public RubyInteger hour() {
         return getRuntime().newFixnum(dt.getHourOfDay());
     }
 
     @JRubyMethod(name = {"mday", "day"})
     public RubyInteger mday() {
         return getRuntime().newFixnum(dt.getDayOfMonth());
     }
 
     @JRubyMethod(name = {"month", "mon"})
     public RubyInteger month() {
         return getRuntime().newFixnum(dt.getMonthOfYear());
     }
 
     @JRubyMethod(name = "year")
     public RubyInteger year() {
         return getRuntime().newFixnum(dt.getYear());
     }
 
     @JRubyMethod(name = "wday")
     public RubyInteger wday() {
         return getRuntime().newFixnum((dt.getDayOfWeek()%7));
     }
 
     @JRubyMethod(name = "yday")
     public RubyInteger yday() {
         return getRuntime().newFixnum(dt.getDayOfYear());
     }
 
     @JRubyMethod(name = {"gmt_offset", "gmtoff", "utc_offset"})
     public RubyInteger gmt_offset() {
         int offset = dt.getZone().getOffsetFromLocal(dt.getMillis());
         
         return getRuntime().newFixnum((int)(offset/1000));
     }
 
     @JRubyMethod(name = {"isdst", "dst?"})
     public RubyBoolean isdst() {
         return getRuntime().newBoolean(!dt.getZone().isStandardOffset(dt.getMillis()));
     }
 
     @JRubyMethod(name = "zone")
     public RubyString zone() {
         String zone = dt.getZone().getShortName(dt.getMillis());
         if(zone.equals("+00:00")) {
             zone = "GMT";
         }
         return getRuntime().newString(zone);
     }
 
     public void setDateTime(DateTime dt) {
         this.dt = dt;
     }
 
     public DateTime getDateTime() {
         return this.dt;
     }
 
     public Date getJavaDate() {
         return this.dt.toDate();
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
     	// modified to match how hash is calculated in 1.8.2
         return getRuntime().newFixnum((int)(((dt.getMillis() / 1000) ^ microseconds()) << 1) >> 1);
     }    
 
     @JRubyMethod(name = "_dump", optional = 1, frame = true)
     public RubyString dump(IRubyObject[] args, Block unusedBlock) {
         RubyString str = (RubyString) mdump(new IRubyObject[] { this });
         str.syncVariables(this.getVariableList());
         return str;
     }    
 
     public RubyObject mdump(final IRubyObject[] args) {
         RubyTime obj = (RubyTime)args[0];
         DateTime dateTime = obj.dt;
         byte dumpValue[] = new byte[8];
         int pe = 
             0x1                                 << 31 |
             ((obj.gmt().isTrue())? 0x1 : 0x0)   << 30 |
             (dateTime.getYear()-1900)           << 14 |
             (dateTime.getMonthOfYear()-1)       << 10 |
             dateTime.getDayOfMonth()            << 5  |
             dateTime.getHourOfDay();
         int se =
             dateTime.getMinuteOfHour()          << 26 |
             dateTime.getSecondOfMinute()        << 20 |
             (dateTime.getMillisOfSecond() * 1000 + (int)usec); // dump usec, not msec
 
         for(int i = 0; i < 4; i++) {
             dumpValue[i] = (byte)(pe & 0xFF);
             pe >>>= 8;
         }
         for(int i = 4; i < 8 ;i++) {
             dumpValue[i] = (byte)(se & 0xFF);
             se >>>= 8;
         }
         return RubyString.newString(obj.getRuntime(), new ByteList(dumpValue,false));
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(Block block) {
         return this;
     }
     
     /* Time class methods */
     
     public static IRubyObject s_new(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, new DateTime(getLocalTimeZone(runtime)));
         time.callInit(args,block);
         return time;
     }
 
     /**
      * @deprecated Use {@link #newInstance(ThreadContext, IRubyObject)}
      */
     @Deprecated
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return newInstance(context, recv);
     }
 
     @JRubyMethod(name = "times", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject times(ThreadContext context, IRubyObject recv) {
         context.getRuntime().getWarnings().warn("obsolete method Time::times; use Process::times");
         return RubyProcess.times(context, recv, Block.NULL_BLOCK);
     }
 
     @JRubyMethod(name = "now", backtrace = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv) {
         IRubyObject obj = ((RubyClass) recv).allocate();
         obj.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, obj);
         return obj;
     }
 
     @JRubyMethod(name = "at",  meta = true)
     public static IRubyObject at(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         final RubyTime time;
 
         if (arg instanceof RubyTime) {
             RubyTime other = (RubyTime) arg;
             time = new RubyTime(runtime, (RubyClass) recv, other.dt);
             time.setUSec(other.getUSec());
         } else {
             time = new RubyTime(runtime, (RubyClass) recv,
                     new DateTime(0L, getLocalTimeZone(runtime)));
 
             long seconds = RubyNumeric.num2long(arg);
             long millisecs = 0;
             long microsecs = 0;
 
             // In the case of two arguments, MRI will discard the portion of
             // the first argument after a decimal point (i.e., "floor").
             // However in the case of a single argument, any portion after
             // the decimal point is honored.
             if (arg instanceof RubyFloat) {
                 double dbl = ((RubyFloat) arg).getDoubleValue();
                 long micro = Math.round((dbl - seconds) * 1000000);
                 if(dbl < 0)
                     micro += 1000000;
                 millisecs = micro / 1000;
                 microsecs = micro % 1000;
             }
             time.setUSec(microsecs);
             time.dt = time.dt.withMillis(seconds * 1000 + millisecs);
         }
 
         time.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, time);
 
         return time;
     }
 
     @JRubyMethod(name = "at", meta = true)
     public static IRubyObject at(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
 
         RubyTime time = new RubyTime(runtime, (RubyClass) recv,
                 new DateTime(0L, getLocalTimeZone(runtime)));
 
             long seconds = RubyNumeric.num2long(arg1);
             long millisecs = 0;
             long microsecs = 0;
 
             long tmp = RubyNumeric.num2long(arg2);
             millisecs = tmp / 1000;
             microsecs = tmp % 1000;
 
             time.setUSec(microsecs);
             time.dt = time.dt.withMillis(seconds * 1000 + millisecs);
 
             time.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, time);
 
         return time;
     }
 
     @JRubyMethod(name = {"local", "mktime"}, required = 1, optional = 9, meta = true)
     public static RubyTime new_local(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, false);
     }
 
     @JRubyMethod(name = {"utc", "gm"}, required = 1, optional = 9, meta = true)
     public static RubyTime new_utc(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, true);
     }
 
     @JRubyMethod(name = "_load", required = 1, frame = true, meta = true)
     public static RubyTime load(IRubyObject recv, IRubyObject from, Block block) {
         return s_mload(recv, (RubyTime)(((RubyClass)recv).allocate()), from);
     }
 
+    @Override
     public Object toJava(Class target) {
-        return getJavaDate();
+        if (target.equals(Date.class)) {
+            return getJavaDate();
+        } else if (target.equals(Calendar.class)) {
+            Calendar cal = GregorianCalendar.getInstance();
+            cal.setTime(getJavaDate());
+            return cal;
+        } else if (target.equals(DateTime.class)) {
+            return this.dt;
+        } else if (target.equals(java.sql.Date.class)) {
+            return new java.sql.Date(dt.getMillis());
+        } else if (target.equals(java.sql.Time.class)) {
+            return new java.sql.Time(dt.getMillis());
+        } else if (target.equals(java.sql.Timestamp.class)) {
+            return new java.sql.Timestamp(dt.getMillis());
+        } else {
+            return super.toJava(target);
+        }
     }
     
     protected static RubyTime s_mload(IRubyObject recv, RubyTime time, IRubyObject from) {
         Ruby runtime = recv.getRuntime();
 
         DateTime dt = new DateTime(getLocalTimeZone(runtime));
 
         byte[] fromAsBytes = null;
         fromAsBytes = from.convertToString().getBytes();
         if(fromAsBytes.length != 8) {
             throw runtime.newTypeError("marshaled time format differ");
         }
         int p=0;
         int s=0;
         for (int i = 0; i < 4; i++) {
             p |= ((int)fromAsBytes[i] & 0xFF) << (8 * i);
         }
         for (int i = 4; i < 8; i++) {
             s |= ((int)fromAsBytes[i] & 0xFF) << (8 * (i - 4));
         }
         if ((p & (1<<31)) == 0) {
             dt = dt.withMillis(p * 1000L);
             time.setUSec((s & 0xFFFFF) % 1000);
         } else {
             p &= ~(1<<31);
             if((p >>> 30 & 0x1) == 0x1) dt = dt.withZone(DateTimeZone.UTC);
             dt = dt.withYear(((p >>> 14) & 0xFFFF) + 1900);
             dt = dt.withMonthOfYear(((p >>> 10) & 0xF) + 1);
             dt = dt.withDayOfMonth(((p >>> 5)  & 0x1F));
             dt = dt.withHourOfDay((p & 0x1F));
             dt = dt.withMinuteOfHour(((s >>> 26) & 0x3F));
             dt = dt.withSecondOfMinute(((s >>> 20) & 0x3F));
             // marsaling dumps usec, not msec
             dt = dt.withMillisOfSecond((s & 0xFFFFF) / 1000);
             time.setUSec((s & 0xFFFFF) % 1000);
         }
         time.setDateTime(dt);
 
         from.getInstanceVariables().copyInstanceVariablesInto(time);
         return time;
     }
 
     private static final String[] MONTHS = {"jan", "feb", "mar", "apr", "may", "jun",
                                             "jul", "aug", "sep", "oct", "nov", "dec"};
 
     private static final Map<String, Integer> MONTHS_MAP = new HashMap<String, Integer>();
     static {
         for (int i = 0; i < MONTHS.length; i++) {
             MONTHS_MAP.put(MONTHS[i], i + 1);
         }
     }
 
     private static final int[] time_min = {1, 0, 0, 0, Integer.MIN_VALUE};
     private static final int[] time_max = {31, 23, 59, 60, Integer.MAX_VALUE};
 
     private static final int ARG_SIZE = 7;
 
     private static RubyTime createTime(IRubyObject recv, IRubyObject[] args, boolean gmt) {
         Ruby runtime = recv.getRuntime();
         int len = ARG_SIZE;
         Boolean isDst = null;
 
         DateTimeZone dtz;
         if (gmt) {
             dtz = DateTimeZone.UTC;
         } else if (args.length == 10 && args[9] instanceof RubyString) {
             dtz = getTimeZone(runtime, ((RubyString) args[9]).toString());
         } else {
             dtz = getLocalTimeZone(runtime);
         }
  
         if (args.length == 10) {
 	    if(args[8] instanceof RubyBoolean) {
 	        isDst = ((RubyBoolean)args[8]).isTrue();
 	    }
             args = new IRubyObject[] { args[5], args[4], args[3], args[2], args[1], args[0], runtime.getNil() };
         } else {
             // MRI accepts additional wday argument which appears to be ignored.
             len = args.length;
 
             if (len < ARG_SIZE) {
                 IRubyObject[] newArgs = new IRubyObject[ARG_SIZE];
                 System.arraycopy(args, 0, newArgs, 0, args.length);
                 for (int i = len; i < ARG_SIZE; i++) {
                     newArgs[i] = runtime.getNil();
                 }
                 args = newArgs;
                 len = ARG_SIZE;
             }
         }
 
         if (args[0] instanceof RubyString) {
             args[0] = RubyNumeric.str2inum(runtime, (RubyString) args[0], 10, false);
         }
 
         int year = (int) RubyNumeric.num2long(args[0]);
         int month = 1;
 
         if (len > 1) {
             if (!args[1].isNil()) {
                 IRubyObject tmp = args[1].checkStringType();
                 if (!tmp.isNil()) {
                     String monthString = tmp.toString().toLowerCase();
                     Integer monthInt = MONTHS_MAP.get(monthString);
 
                     if (monthInt != null) {
                         month = monthInt;
                     } else {
                         try {
                             month = Integer.parseInt(monthString);
                         } catch (NumberFormatException nfExcptn) {
                             throw runtime.newArgumentError("Argument out of range.");
                         }
                     }
                 } else {
                     month = (int) RubyNumeric.num2long(args[1]);
                 }
             }
             if (1 > month || month > 12) {
                 throw runtime.newArgumentError("Argument out of range: for month: " + month);
             }
         }
 
         int[] int_args = { 1, 0, 0, 0, 0, 0 };
 
         for (int i = 0; int_args.length >= i + 2; i++) {
             if (!args[i + 2].isNil()) {
                 if (!(args[i + 2] instanceof RubyNumeric)) {
                     args[i + 2] = args[i + 2].callMethod(
                             runtime.getCurrentContext(), "to_i");
                 }
 
                 long value = RubyNumeric.num2long(args[i + 2]);
                 if (time_min[i] > value || value > time_max[i]) {
                     throw runtime.newArgumentError("argument out of range.");
                 }
                 int_args[i] = (int) value;
             }
         }
 
         if (0 <= year && year < 39) {
             year += 2000;
         } else if (69 <= year && year < 139) {
             year += 1900;
         }
 
         DateTime dt;
         // set up with min values and then add to allow rolling over
         try {
             dt = new DateTime(year, 1, 1, 0, 0 , 0, 0, DateTimeZone.UTC);
 
             dt = dt.plusMonths(month - 1)
                     .plusDays(int_args[0] - 1)
                     .plusHours(int_args[1])
                     .plusMinutes(int_args[2])
                     .plusSeconds(int_args[3]);
 
 	    dt = dt.withZoneRetainFields(dtz);
 
 	    // we might need to perform a DST correction
 	    if(isDst != null) {
                 // the instant at which we will ask dtz what the difference between DST and
                 // standard time is
                 long offsetCalculationInstant = dt.getMillis();
 
                 // if we might be moving this time from !DST -> DST, the offset is assumed
                 // to be the same as it was just before we last moved from DST -> !DST
                 if(dtz.isStandardOffset(dt.getMillis()))
                     offsetCalculationInstant = dtz.previousTransition(offsetCalculationInstant);
 
                 int offset = dtz.getStandardOffset(offsetCalculationInstant) -
                              dtz.getOffset(offsetCalculationInstant);
 
                 if (!isDst && !dtz.isStandardOffset(dt.getMillis())) {
                     dt = dt.minusMillis(offset);
                 }
                 if (isDst &&  dtz.isStandardOffset(dt.getMillis())) {
                     dt = dt.plusMillis(offset);
                 }
 	    }
         } catch (org.joda.time.IllegalFieldValueException e) {
             throw runtime.newArgumentError("time out of range");
         }
 
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, dt);
         // Ignores usec if 8 args (for compatibility with parsedate) or if not supplied.
         if (args.length != 8 && !args[6].isNil()) {
             int usec = int_args[4] % 1000;
             int msec = int_args[4] / 1000;
 
             if (int_args[4] < 0) {
                 msec -= 1;
                 usec += 1000;
             }
             time.dt = dt.withMillis(dt.getMillis() + msec);
             time.setUSec(usec);
         }
 
         time.callInit(IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         return time;
     }
 }
diff --git a/src/org/jruby/java/addons/KernelJavaAddons.java b/src/org/jruby/java/addons/KernelJavaAddons.java
index dc6ad05f9d..e1c7a2839a 100644
--- a/src/org/jruby/java/addons/KernelJavaAddons.java
+++ b/src/org/jruby/java/addons/KernelJavaAddons.java
@@ -1,114 +1,121 @@
 package org.jruby.java.addons;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.javasupport.*;
 import org.jruby.RubyKernel;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.java.proxies.ConcreteJavaProxy;
+import org.jruby.java.proxies.JavaProxy;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.unsafe.UnsafeFactory;
 
 public class KernelJavaAddons {
     @JRubyMethod(name = "raise", optional = 3, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject rbRaise(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         
         if (args.length == 1 && args[0] instanceof ConcreteJavaProxy) {
             // looks like someone's trying to raise a Java exception. Let them.
             Object maybeThrowable = ((ConcreteJavaProxy)args[0]).getObject();
             
             if (maybeThrowable instanceof Throwable) {
                 // yes, we're cheating here.
                 UnsafeFactory.getUnsafe().throwException((Throwable)maybeThrowable);
                 return recv; // not reached
             } else {
                 throw context.getRuntime().newTypeError("can't raise a non-Throwable Java object");
             }
         } else {
             return RubyKernel.raise(context, recv, args, block);
         }
     }
 
     @JRubyMethod(backtrace = true)
     public static IRubyObject to_java(ThreadContext context, IRubyObject fromObject) {
         if (fromObject instanceof RubyArray) {
             return context.getRuntime().getJavaSupport().getObjectJavaClass().javaArrayFromRubyArray(context, fromObject);
         } else {
             return Java.getInstance(context.getRuntime(), fromObject.toJava(Object.class));
         }
     }
     
     @JRubyMethod(backtrace = true)
     public static IRubyObject to_java(ThreadContext context, IRubyObject fromObject, IRubyObject type) {
         if (type.isNil()) {
             return to_java(context, fromObject);
         }
 
         Ruby runtime = context.getRuntime();
         JavaClass targetType = getTargetType(context, runtime, type);
 
         if (fromObject instanceof RubyArray) {
             return targetType.javaArrayFromRubyArray(context, fromObject);
         } else {
             return Java.getInstance(runtime, fromObject.toJava(targetType.javaClass()));
         }
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_signature(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_name(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_implements(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_annotation(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_require(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_package(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     private static JavaClass getTargetType(ThreadContext context, Ruby runtime, IRubyObject type) {
         JavaClass targetType;
 
         if (type instanceof RubyString || type instanceof RubySymbol) {
             targetType = runtime.getJavaSupport().getNameClassMap().get(type.asJavaString());
             if (targetType == null) targetType = JavaClass.forNameVerbose(runtime, type.asJavaString());
         } else if (type instanceof RubyModule && type.respondsTo("java_class")) {
             targetType = (JavaClass)RuntimeHelpers.invoke(context, type, "java_class");
+        } else if (type instanceof JavaProxy) {
+            if  (((JavaProxy)type).getObject() instanceof Class) {
+                targetType = JavaClass.get(runtime, (Class)((JavaProxy)type).getObject());
+            } else {
+                throw runtime.newTypeError("not a valid target type: " + type);
+            }
         } else {
-            throw runtime.newTypeError("unable to convert array to type: " + type);
+            throw runtime.newTypeError("unable to convert to type: " + type);
         }
 
         return targetType;
     }
 }
diff --git a/src/org/jruby/java/proxies/JavaProxy.java b/src/org/jruby/java/proxies/JavaProxy.java
index d9fe0db96e..4afc19a443 100644
--- a/src/org/jruby/java/proxies/JavaProxy.java
+++ b/src/org/jruby/java/proxies/JavaProxy.java
@@ -1,418 +1,422 @@
 package org.jruby.java.proxies;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyHash;
 import org.jruby.RubyHash.Visitor;
 import org.jruby.RubyMethod;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.java.invokers.InstanceFieldGetter;
 import org.jruby.java.invokers.InstanceFieldSetter;
 import org.jruby.java.invokers.InstanceMethodInvoker;
 import org.jruby.java.invokers.MethodInvoker;
 import org.jruby.java.invokers.StaticMethodInvoker;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaClass;
 import org.jruby.javasupport.JavaMethod;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.CodegenUtils;
 
 public class JavaProxy extends RubyObject {
     private static final boolean DEBUG = false;
     private JavaObject javaObject;
     protected Object object;
     
     public JavaProxy(Ruby runtime, RubyClass klazz) {
         super(runtime, klazz);
     }
 
     @Override
     public Object dataGetStruct() {
         // for investigating and eliminating code that causes JavaObject to live
         if (DEBUG) Thread.dumpStack();
         lazyJavaObject();
         return javaObject;
     }
 
     @Override
     public void dataWrapStruct(Object object) {
         this.javaObject = (JavaObject)object;
         this.object = javaObject.getValue();
     }
 
     public Object getObject() {
         // FIXME: Added this because marshal_spec seemed to reconstitute objects without calling dataWrapStruct
         // this resulted in object being null after unmarshalling...
         if (object == null) {
             if (javaObject == null) {
                 throw getRuntime().newRuntimeError("Java wrapper with no contents: " + this);
             } else {
                 object = javaObject.getValue();
             }
         }
         return object;
     }
 
     public void setObject(Object object) {
         this.object = object;
     }
 
     private JavaObject getJavaObject() {
         lazyJavaObject();
         return (JavaObject)dataGetStruct();
     }
 
     private void lazyJavaObject() {
         if (javaObject == null) {
             javaObject = JavaObject.wrap(getRuntime(), object);
         }
     }
 
     @Override
     public Class getJavaClass() {
         return object.getClass();
     }
     
     public static RubyClass createJavaProxy(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         RubyClass javaProxy = runtime.defineClass("JavaProxy", runtime.getObject(), new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                 return new JavaProxy(runtime, klazz);
             }
         });
         
         RubyClass singleton = javaProxy.getSingletonClass();
         
         singleton.addReadWriteAttribute(context, "java_class");
         
         javaProxy.defineAnnotatedMethods(JavaProxy.class);
         javaProxy.includeModule(runtime.fastGetModule("JavaProxyMethods"));
         
         return javaProxy;
     }
     
     @JRubyMethod(frame = true, meta = true)
     public static IRubyObject inherited(ThreadContext context, IRubyObject recv, IRubyObject subclass) {
         IRubyObject subJavaClass = RuntimeHelpers.invoke(context, subclass, "java_class");
         if (subJavaClass.isNil()) {
             subJavaClass = RuntimeHelpers.invoke(context, recv, "java_class");
             RuntimeHelpers.invoke(context, subclass, "java_class=", subJavaClass);
         }
         return RuntimeHelpers.invokeSuper(context, recv, subclass, Block.NULL_BLOCK);
     }
     
     @JRubyMethod(meta = true)
     public static IRubyObject singleton_class(IRubyObject recv) {
         return ((RubyClass)recv).getSingletonClass();
     }
     
     @JRubyMethod(name = "[]", meta = true, rest = true)
     public static IRubyObject op_aref(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject javaClass = RuntimeHelpers.invoke(context, recv, "java_class");
         if (args.length > 0) {
             // construct new array proxy (ArrayJavaProxy)
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             newArgs[0] = javaClass;
             System.arraycopy(args, 0, newArgs, 1, args.length);
             return context.getRuntime().fastGetClass("ArrayJavaProxyCreator").newInstance(context, newArgs, Block.NULL_BLOCK);
         } else {
             return Java.get_proxy_class(javaClass, RuntimeHelpers.invoke(context, javaClass, "array_class"));
         }
     }
 
     @Override
     public IRubyObject initialize_copy(IRubyObject original) {
         super.initialize_copy(original);
         // because we lazily init JavaObject in the data-wrapped slot, explicitly copy over the object
         setObject(((JavaProxy)original).object);
         return this;
     }
 
     private static Class<?> getJavaClass(ThreadContext context, RubyModule module) {
         try {
         IRubyObject jClass = RuntimeHelpers.invoke(context, module, "java_class");
 
 
         return !(jClass instanceof JavaClass) ? null : ((JavaClass) jClass).javaClass();
         } catch (Exception e) { return null; }
     }
     
     /**
      * Create a name/newname map of fields to be exposed as methods.
      */
     private static Map<String, String> getFieldListFromArgs(IRubyObject[] args) {
         final Map<String, String> map = new HashMap<String, String>();
         
         // Get map of all fields we want to define.  
         for (int i = 0; i < args.length; i++) {
             if (args[i] instanceof RubyHash) {
                 ((RubyHash) args[i]).visitAll(new Visitor() {
                     @Override
                     public void visit(IRubyObject key, IRubyObject value) {
                         map.put(key.asString().toString(), value.asString().toString());
                     }
                 });
             } else {
                 String value = args[i].asString().toString();
                 map.put(value, value);
             }
         }
         
         return map;
     }
 
     // Look through all mappings to find a match entry for this field
     private static void installField(ThreadContext context, Map<String, String> fieldMap,
             Field field, RubyModule module, boolean asReader, boolean asWriter) {
         boolean isFinal = Modifier.isFinal(field.getModifiers());
 
         for (Iterator<Map.Entry<String,String>> iter = fieldMap.entrySet().iterator(); iter.hasNext();) {
             Map.Entry<String,String> entry = iter.next();
             String key = entry.getKey();
             if (key.equals(field.getName())) {
                 if (Ruby.isSecurityRestricted() && !Modifier.isPublic(field.getModifiers())) {
                     throw context.getRuntime().newSecurityError("Cannot change accessibility on fields in a restricted mode: field '" + field.getName() + "'");
                 }
                 
                 String asName = entry.getValue();
 
                 if (asReader) module.addMethod(asName, new InstanceFieldGetter(key, module, field));
                 if (asWriter) {
                     if (isFinal) throw context.getRuntime().newSecurityError("Cannot change final field '" + field.getName() + "'");
                     module.addMethod(asName + "=", new InstanceFieldSetter(key, module, field));
                 }
                 
                 iter.remove();
                 break;
             }
         }
     }    
 
     private static void findFields(ThreadContext context, RubyModule topModule,
             IRubyObject args[], boolean asReader, boolean asWriter) {
         Map<String, String> fieldMap = getFieldListFromArgs(args);
         
         for (RubyModule module = topModule; module != null; module = module.getSuperClass()) {
             Class<?> javaClass = getJavaClass(context, module);
             
             // Hit a non-java proxy class (included Modules can be a cause of this...skip)
             if (javaClass == null) continue;
 
             Field[] fields = JavaClass.getDeclaredFields(javaClass);
             for (int j = 0; j < fields.length; j++) {
                 installField(context, fieldMap, fields[j], module, asReader, asWriter);
             }
         }
         
         // We could not find all of them print out first one (we could print them all?)
         if (!fieldMap.isEmpty()) {
             throw JavaClass.undefinedFieldError(context.getRuntime(),
                     topModule.getName(), fieldMap.keySet().iterator().next());
         }
 
     }
     
     @JRubyMethod(meta = true, rest = true)
     public static IRubyObject field_accessor(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         findFields(context, (RubyModule) recv, args, true, true);
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(meta = true, rest = true)
     public static IRubyObject field_reader(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         findFields(context, (RubyModule) recv, args, true, false);
 
         return context.getRuntime().getNil();
     }
     
     @JRubyMethod(meta = true, rest = true)
     public static IRubyObject field_writer(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         findFields(context, (RubyModule) recv, args, false, true);
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "equal?")
     public IRubyObject equal_p(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (other instanceof JavaProxy) {
             boolean equal = getObject() == ((JavaProxy)other).getObject();
             return runtime.newBoolean(equal);
         }
         return runtime.getFalse();
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject java_send(ThreadContext context, IRubyObject rubyName) {
         String name = rubyName.asJavaString();
         Ruby runtime = context.getRuntime();
         
         JavaMethod method = new JavaMethod(runtime, getMethod(name));
         return method.invokeDirect(getObject());
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject java_send(ThreadContext context, IRubyObject rubyName, IRubyObject argTypes) {
         String name = rubyName.asJavaString();
         RubyArray argTypesAry = argTypes.convertToArray();
         Ruby runtime = context.getRuntime();
 
         if (argTypesAry.size() != 0) {
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
             throw JavaMethod.newArgSizeMismatchError(runtime, argTypesClasses);
         }
 
         JavaMethod method = new JavaMethod(runtime, getMethod(name));
         return method.invokeDirect(getObject());
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject java_send(ThreadContext context, IRubyObject rubyName, IRubyObject argTypes, IRubyObject arg0) {
         String name = rubyName.asJavaString();
         RubyArray argTypesAry = argTypes.convertToArray();
         Ruby runtime = context.getRuntime();
 
         if (argTypesAry.size() != 1) {
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
             throw JavaMethod.newArgSizeMismatchError(runtime, argTypesClasses);
         }
 
         Class argTypeClass = (Class)argTypesAry.eltInternal(0).toJava(Class.class);
 
         JavaMethod method = new JavaMethod(runtime, getMethod(name, argTypeClass));
         return method.invokeDirect(getObject(), arg0.toJava(argTypeClass));
     }
 
     @JRubyMethod(required = 4, rest = true, backtrace = true)
     public IRubyObject java_send(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         
         String name = args[0].asJavaString();
         RubyArray argTypesAry = args[1].convertToArray();
         int argsLen = args.length - 2;
 
         if (argTypesAry.size() != argsLen) {
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
             throw JavaMethod.newArgSizeMismatchError(runtime, argTypesClasses);
         }
 
         Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argsLen]);
 
         Object[] argsAry = new Object[argsLen];
         for (int i = 0; i < argsLen; i++) {
             argsAry[i] = args[i + 2].toJava(argTypesClasses[i]);
         }
 
         JavaMethod method = new JavaMethod(runtime, getMethod(name, argTypesClasses));
         return method.invokeDirect(getObject(), argsAry);
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject java_method(ThreadContext context, IRubyObject rubyName) {
         String name = rubyName.asJavaString();
 
         return getRubyMethod(name);
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject java_method(ThreadContext context, IRubyObject rubyName, IRubyObject argTypes) {
         String name = rubyName.asJavaString();
         RubyArray argTypesAry = argTypes.convertToArray();
         Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
 
         return getRubyMethod(name, argTypesClasses);
     }
 
     @JRubyMethod(frame = true)
     public IRubyObject marshal_dump() {
         if (Serializable.class.isAssignableFrom(object.getClass())) {
             try {
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos);
 
                 oos.writeObject(object);
 
                 return getRuntime().newString(new ByteList(baos.toByteArray()));
             } catch (IOException ioe) {
                 throw getRuntime().newIOErrorFromException(ioe);
             }
         } else {
             throw getRuntime().newTypeError("no marshal_dump is defined for class " + getJavaClass());
         }
     }
 
     @JRubyMethod(frame = true)
     public IRubyObject marshal_load(ThreadContext context, IRubyObject str) {
         try {
             ByteList byteList = str.convertToString().getByteList();
             ByteArrayInputStream bais = new ByteArrayInputStream(byteList.getUnsafeBytes(), byteList.getBegin(), byteList.getRealSize());
             ObjectInputStream ois = new ObjectInputStream(bais);
 
             object = ois.readObject();
 
             return this;
         } catch (IOException ioe) {
             throw context.getRuntime().newIOErrorFromException(ioe);
         } catch (ClassNotFoundException cnfe) {
             throw context.getRuntime().newTypeError("Class not found unmarshaling Java type: " + cnfe.getLocalizedMessage());
         }
     }
 
     private Method getMethod(String name, Class... argTypes) {
         try {
             return getObject().getClass().getMethod(name, argTypes);
         } catch (NoSuchMethodException nsme) {
             throw JavaMethod.newMethodNotFoundError(getRuntime(), getObject().getClass(), name + CodegenUtils.prettyParams(argTypes), name);
         }
     }
 
     private MethodInvoker getMethodInvoker(Method method) {
         if (Modifier.isStatic(method.getModifiers())) {
             return new StaticMethodInvoker(metaClass.getMetaClass(), method);
         } else {
             return new InstanceMethodInvoker(metaClass, method);
         }
     }
 
     private RubyMethod getRubyMethod(String name, Class... argTypes) {
         Method jmethod = getMethod(name, argTypes);
         if (Modifier.isStatic(jmethod.getModifiers())) {
             return RubyMethod.newMethod(metaClass.getSingletonClass(), CodegenUtils.prettyParams(argTypes), metaClass.getSingletonClass(), name, getMethodInvoker(jmethod), getMetaClass());
         } else {
             return RubyMethod.newMethod(metaClass, CodegenUtils.prettyParams(argTypes), metaClass, name, getMethodInvoker(jmethod), this);
         }
     }
 
     @Override
     public Object toJava(Class type) {
-        if (Java.OBJECT_PROXY_CACHE) getRuntime().getJavaSupport().getObjectProxyCache().put(getObject(), this);
-        return getObject();
+        if (type.isAssignableFrom(getObject().getClass())) {
+            if (Java.OBJECT_PROXY_CACHE) getRuntime().getJavaSupport().getObjectProxyCache().put(getObject(), this);
+            return getObject();
+        } else {
+            return super.toJava(type);
+        }
     }
     
     public Object unwrap() {
         return getObject();
     }
 }
diff --git a/src/org/jruby/javasupport/JavaArray.java b/src/org/jruby/javasupport/JavaArray.java
index b25591701f..c2869b36f8 100644
--- a/src/org/jruby/javasupport/JavaArray.java
+++ b/src/org/jruby/javasupport/JavaArray.java
@@ -1,181 +1,181 @@
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
 import org.jruby.anno.JRubyClass;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 @JRubyClass(name="Java::JavaArray", parent="Java::JavaObject")
 public class JavaArray extends JavaObject {
     private JavaUtil.JavaConverter javaConverter;
     
     public JavaArray(Ruby runtime, Object array) {
         super(runtime, runtime.getJavaSupport().getJavaArrayClass(), array);
         assert array.getClass().isArray();
         javaConverter = JavaUtil.getJavaConverter(array.getClass().getComponentType());
     }
 
     public static RubyClass createJavaArrayClass(Ruby runtime, RubyModule javaModule) {
         // FIXME: NOT_ALLOCATABLE_ALLOCATOR is probably not right here, since we might
         // eventually want JavaArray to be marshallable. JRUBY-414
         return javaModule.defineClassUnder("JavaArray", javaModule.fastGetClass("JavaObject"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
     }
     
     public Class getComponentType() {
         return getValue().getClass().getComponentType();
     }
 
     public RubyFixnum length() {
         return getRuntime().newFixnum(getLength());
     }
 
     private int getLength() {
         return Array.getLength(getValue());
     }
 
     public boolean equals(Object other) {
         return other instanceof JavaArray &&
             this.getValue() == ((JavaArray)other).getValue();
     }
 
     @Deprecated
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
 
     public IRubyObject arefDirect(int intIndex) {
         return arefDirect(getRuntime(), javaConverter, getValue(), intIndex);
     }
 
     public static IRubyObject arefDirect(Ruby runtime, JavaUtil.JavaConverter converter, Object array, int intIndex) {
         int length = Array.getLength(array);
         if (intIndex < 0 || intIndex >= length) {
             throw runtime.newArgumentError(
                                     "index out of bounds for java array (" + intIndex +
                                     " for length " + length + ")");
         }
         return JavaUtil.convertJavaArrayElementToRuby(runtime, converter, array, intIndex);
     }
 
     @Deprecated
     public IRubyObject at(int index) {
         Object result = Array.get(getValue(), index);
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
         setWithExceptionHandling(intIndex, javaObject);
         return value;
     }
     
     public void setWithExceptionHandling(int intIndex, Object javaObject) {
         try {
             Array.set(getValue(), intIndex, javaObject);
         } catch (IndexOutOfBoundsException e) {
             throw getRuntime().newArgumentError(
                                     "index out of bounds for java array (" + intIndex +
                                     " for length " + getLength() + ")");
         } catch (ArrayStoreException e) {
-            throw getRuntime().newArgumentError(
+            throw getRuntime().newTypeError(
                                     "wrong element type " + javaObject.getClass() + "(array contains " +
                                     getValue().getClass().getComponentType().getName() + ")");
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newArgumentError(
                                     "wrong element type " + javaObject.getClass() + "(array contains " +
                                     getValue().getClass().getComponentType().getName() + ")");
         }
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
         fillWithExceptionHandling(intIndex, intEndIndex, javaObject);
         return value;
     }
     
     public void fillWithExceptionHandling(int intIndex, int intEndIndex, Object javaObject) {
         try {
           for ( ; intIndex < intEndIndex; intIndex++) {
             Array.set(getValue(), intIndex, javaObject);
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
     }
 }
diff --git a/src/org/jruby/javasupport/JavaObject.java b/src/org/jruby/javasupport/JavaObject.java
index 22a041d2c7..14a108aed5 100644
--- a/src/org/jruby/javasupport/JavaObject.java
+++ b/src/org/jruby/javasupport/JavaObject.java
@@ -1,343 +1,344 @@
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
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.java.proxies.JavaProxy;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Java::JavaObject")
 public class JavaObject extends RubyObject {
 
     private static Object NULL_LOCK = new Object();
     private final RubyClass.VariableAccessor objectAccessor;
 
     protected JavaObject(Ruby runtime, RubyClass rubyClass, Object value) {
         super(runtime, rubyClass);
         objectAccessor = rubyClass.getVariableAccessorForWrite("__wrap_struct__");
         dataWrapStruct(value);
     }
 
     @Override
     public Object dataGetStruct() {
         return objectAccessor.get(this);
     }
 
     @Override
     public void dataWrapStruct(Object object) {
         objectAccessor.set(this, object);
     }
 
     protected JavaObject(Ruby runtime, Object value) {
         this(runtime, runtime.getJavaSupport().getJavaObjectClass(), value);
     }
 
     public static JavaObject wrap(Ruby runtime, Object value) {
         if (value != null) {
             if (value instanceof Class) {
                 return JavaClass.get(runtime, (Class<?>) value);
             } else if (value.getClass().isArray()) {
                 return new JavaArray(runtime, value);
             }
         }
         return new JavaObject(runtime, value);
     }
 
     @JRubyMethod(meta = true)
     public static IRubyObject wrap(ThreadContext context, IRubyObject self, IRubyObject object) {
         Ruby runtime = context.getRuntime();
         Object obj = getWrappedObject(object, NEVER);
 
         if (obj == NEVER) return runtime.getNil();
 
         return wrap(runtime, obj);
     }
 
     @Override
     public Class<?> getJavaClass() {
         Object dataStruct = dataGetStruct();
         return dataStruct != null ? dataStruct.getClass() : Void.TYPE;
     }
 
     public Object getValue() {
         return dataGetStruct();
     }
 
     public static RubyClass createJavaObjectClass(Ruby runtime, RubyModule javaModule) {
         // FIXME: Ideally JavaObject instances should be marshallable, which means that
         // the JavaObject metaclass should have an appropriate allocator. JRUBY-414
         RubyClass result = javaModule.defineClassUnder("JavaObject", runtime.getObject(), JAVA_OBJECT_ALLOCATOR);
 
         registerRubyMethods(runtime, result);
 
         result.getMetaClass().undefineMethod("new");
         result.getMetaClass().undefineMethod("allocate");
 
         return result;
     }
 
     protected static void registerRubyMethods(Ruby runtime, RubyClass result) {
         result.defineAnnotatedMethods(JavaObject.class);
     }
 
     @Override
     public boolean equals(Object other) {
         Ruby runtime = getRuntime();
         Object myValue = getValue();
         Object otherValue = other;
         if (other instanceof IRubyObject) {
             otherValue = getWrappedObject((IRubyObject)other, NEVER);
         }
 
         if (otherValue == NEVER) {
             // not a wrapped object
             return false;
         }
         return myValue == otherValue;
     }
 
     @Override
     public int hashCode() {
         Object dataStruct = dataGetStruct();
         if (dataStruct != null) {
             return dataStruct.hashCode();
         }
         return 0;
     }
 
     @JRubyMethod
     @Override
     public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
 
     @JRubyMethod
     @Override
     public IRubyObject to_s() {
         return to_s(getRuntime(), dataGetStruct());
     }
 
     public static IRubyObject to_s(Ruby runtime, Object dataStruct) {
         if (dataStruct != null) {
             String stringValue = dataStruct.toString();
             if (stringValue != null) {
                 return RubyString.newUnicodeString(runtime, dataStruct.toString());
             }
 
             return runtime.getNil();
         }
         return RubyString.newEmptyString(runtime);
     }
 
     @JRubyMethod(name = {"==", "eql?"}, required = 1)
     public IRubyObject op_equal(IRubyObject other) {
         Object myValue = getValue();
         return opEqualShared(myValue, other);
     }
 
     public static IRubyObject op_equal(JavaProxy self, IRubyObject other) {
         Object myValue = self.getObject();
         return opEqualShared(myValue, other);
     }
 
     private static IRubyObject opEqualShared(Object myValue, IRubyObject other) {
         Ruby runtime = other.getRuntime();
         Object otherValue = getWrappedObject(other, NEVER);
 
         if (other == NEVER) {
             // not a wrapped object
             return runtime.getFalse();
         }
 
         if (myValue == null && otherValue == null) {
             return runtime.getTrue();
         }
 
         return runtime.newBoolean(myValue.equals(otherValue));
     }
 
     @JRubyMethod(name = "equal?", required = 1)
     public IRubyObject same(IRubyObject other) {
         Ruby runtime = getRuntime();
         Object myValue = getValue();
         Object otherValue = getWrappedObject(other, NEVER);
 
         if (other == NEVER) {
             // not a wrapped object
             return runtime.getFalse();
         }
 
         if (myValue == null && otherValue == null) {
             return getRuntime().getTrue();
         }
 
         boolean isSame = getValue() == ((JavaObject) other).getValue();
         return isSame ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     private static Object getWrappedObject(IRubyObject other, Object def) {
         if (other instanceof JavaObject) {
             return ((JavaObject)other).getValue();
         } else if (other instanceof JavaProxy) {
             return ((JavaProxy)other).getObject();
         } else {
             return def;
         }
     }
 
     @JRubyMethod
     public RubyString java_type() {
         return getRuntime().newString(getJavaClass().getName());
     }
 
     @JRubyMethod
     public IRubyObject java_class() {
         return JavaClass.get(getRuntime(), getJavaClass());
     }
 
     @JRubyMethod
     public RubyFixnum length() {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject aref(IRubyObject index) {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "[]=", required = 2)
     public IRubyObject aset(IRubyObject index, IRubyObject someValue) {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "fill", required = 3)
     public IRubyObject afill(IRubyObject beginIndex, IRubyObject endIndex, IRubyObject someValue) {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "java_proxy?")
     public IRubyObject is_java_proxy() {
         return getRuntime().getTrue();
     }
 
     @JRubyMethod(name = "synchronized")
     public IRubyObject ruby_synchronized(ThreadContext context, Block block) {
         Object lock = getValue();
         synchronized (lock != null ? lock : NULL_LOCK) {
             return block.yield(context, null);
         }
     }
     
     public static IRubyObject ruby_synchronized(ThreadContext context, Object lock, Block block) {
         synchronized (lock != null ? lock : NULL_LOCK) {
             return block.yield(context, null);
         }
     }
     
     @JRubyMethod(frame = true)
     public IRubyObject marshal_dump() {
         if (Serializable.class.isAssignableFrom(getJavaClass())) {
             try {
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos);
 
                 oos.writeObject(getValue());
 
                 return getRuntime().newString(new ByteList(baos.toByteArray()));
             } catch (IOException ioe) {
                 throw getRuntime().newIOErrorFromException(ioe);
             }
         } else {
             throw getRuntime().newTypeError("no marshal_dump is defined for class " + getJavaClass());
         }
     }
 
     @JRubyMethod(frame = true)
     public IRubyObject marshal_load(ThreadContext context, IRubyObject str) {
         try {
             ByteList byteList = str.convertToString().getByteList();
             ByteArrayInputStream bais = new ByteArrayInputStream(byteList.getUnsafeBytes(), byteList.getBegin(), byteList.getRealSize());
             ObjectInputStream ois = new ObjectInputStream(bais);
 
             dataWrapStruct(ois.readObject());
 
             return this;
         } catch (IOException ioe) {
             throw context.getRuntime().newIOErrorFromException(ioe);
         } catch (ClassNotFoundException cnfe) {
             throw context.getRuntime().newTypeError("Class not found unmarshaling Java type: " + cnfe.getLocalizedMessage());
         }
     }
 
     @Override
     public Object toJava(Class cls) {
         if (getValue() == null) {
             // THIS SHOULD NEVER HAPPEN, but it DOES
             return getValue();
         }
         
         if (cls.isAssignableFrom(getValue().getClass())) {
             return getValue();
         }
-        throw getRuntime().newTypeError("cannot convert instance of " + getValue().getClass() + " to " + cls);
+        
+        return super.toJava(cls);
     }
 
     private static final ObjectAllocator JAVA_OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
             return new JavaObject(runtime, klazz, null);
         }
     };
 
 }
