diff --git a/spec/java_integration/fixtures/PackageConstructor.java b/spec/java_integration/fixtures/PackageConstructor.java
new file mode 100644
index 0000000000..2b0783a1cb
--- /dev/null
+++ b/spec/java_integration/fixtures/PackageConstructor.java
@@ -0,0 +1,6 @@
+package java_integration.fixtures;
+
+public class PackageConstructor {
+  PackageConstructor() {
+  }
+}
\ No newline at end of file
diff --git a/spec/java_integration/fixtures/PrivateConstructor.java b/spec/java_integration/fixtures/PrivateConstructor.java
new file mode 100644
index 0000000000..09707bc915
--- /dev/null
+++ b/spec/java_integration/fixtures/PrivateConstructor.java
@@ -0,0 +1,6 @@
+package java_integration.fixtures;
+
+public class PrivateConstructor {
+  private PrivateConstructor() {
+  }
+}
\ No newline at end of file
diff --git a/spec/java_integration/fixtures/ProtectedConstructor.java b/spec/java_integration/fixtures/ProtectedConstructor.java
new file mode 100644
index 0000000000..3ec528abd2
--- /dev/null
+++ b/spec/java_integration/fixtures/ProtectedConstructor.java
@@ -0,0 +1,7 @@
+package java_integration.fixtures;
+
+public class ProtectedConstructor {
+  protected String theProtectedMethod() {
+    return "42";
+  }
+}
\ No newline at end of file
diff --git a/spec/java_integration/types/coercion_spec.rb b/spec/java_integration/types/coercion_spec.rb
index e1e0a39f1e..b9732b55b2 100644
--- a/spec/java_integration/types/coercion_spec.rb
+++ b/spec/java_integration/types/coercion_spec.rb
@@ -1,380 +1,400 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 import "java_integration.fixtures.CoreTypeMethods"
 import "java_integration.fixtures.JavaFields"
 import "java_integration.fixtures.ValueReceivingInterface"
 import "java_integration.fixtures.ValueReceivingInterfaceHandler"
 
+import "java_integration.fixtures.PackageConstructor"
+import "java_integration.fixtures.ProtectedConstructor"
+import "java_integration.fixtures.PrivateConstructor"
+
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
     
     CoreTypeMethods.setNull(nil).should == "null"
   end
   
   it "should raise errors when passed values can not be precisely coerced" do
     pending("precision failure does not raise error") do
       lambda { CoreTypeMethods.setByte(1 << 8) }.should raise_error(TypeError)
       lambda { CoreTypeMethods.setShort(1 << 16) }.should raise_error(TypeError)
       lambda { CoreTypeMethods.setChar(1 << 16) }.should raise_error(TypeError)
       lambda { CoreTypeMethods.setInt(1 << 32) }.should raise_error(TypeError)
       lambda { CoreTypeMethods.setLong(1 << 64) }.should raise_error(TypeError)
     end
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
   end
 end
 
 describe "Java Object-typed methods" do
   it "coerce primitive Ruby types to their default boxed Java type" do
     CoreTypeMethods.getObjectType("foo").should == "class java.lang.String"
     CoreTypeMethods.getObjectType(1).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(1.0).should == "class java.lang.Double"
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
     
     pending "char appears to be getting signed/unsigned-garbled" do
       vri_handler.receiveChar(obj).should == obj
       vri.result.should == obj
       vri.result.class.should == Fixnum
     end
     
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
 
 describe "Java primitive-box-typed interface methods" do
   it "should coerce to Ruby types for the implementer" do
     pending "We do not coerce numerics to boxed Java numerics (yet?)" do
       impl = Class.new {
         attr_accessor :result
         include ValueReceivingInterface
 
         def receiveByte(obj)
           self.result = obj
           obj
         end
 
         %w[Short Char Int Long Float Double True False].each do |type|
           alias_method "receive#{type}".intern, :receiveByte
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
   end
+end
+
+describe "Java types with package or private constructors" do
+  it "should not be construcible" do
+    lambda { PackageConstructor.new }.should raise_error(TypeError)
+    lambda { PrivateConstructor.new }.should raise_error(TypeError)
+  end
+end
+
+describe "Java types with protected constructors" do
+  it "should not be construcible" do
+    pc = nil
+    lambda { pc = ProtectedConstructor.new }.should_not raise_error(TypeError)
+    ProtectedConstructor.should === pc
+    pc.java_class.name.should == "java_integration.fixtures.ProtectedConstructor"
+  end
 end
\ No newline at end of file
diff --git a/spec/java_integration/types/construction_spec.rb b/spec/java_integration/types/construction_spec.rb
new file mode 100644
index 0000000000..840a9c9758
--- /dev/null
+++ b/spec/java_integration/types/construction_spec.rb
@@ -0,0 +1,792 @@
+require File.dirname(__FILE__) + "/../spec_helper"
+
+import "java_integration.fixtures.ArrayReceiver"
+import "java_integration.fixtures.ArrayReturningInterface"
+import "java_integration.fixtures.ArrayReturningInterfaceConsumer"
+
+describe "A Java primitive Array of type" do
+  describe "boolean" do 
+    it "should be possible to create empty array" do 
+      arr = Java::boolean[0].new
+      arr.java_class.to_s.should == "[Z"
+    end
+    
+    it "should be possible to create uninitialized single dimensional array" do 
+      arr = Java::boolean[10].new
+      arr.java_class.to_s.should == "[Z"
+    end
+    
+    it "should be possible to create uninitialized multi dimensional array" do 
+      arr = Java::boolean[10,10].new
+      arr.java_class.to_s.should == "[[Z"
+    end
+
+    it "should be possible to create primitive array from Ruby array" do 
+      # Check with symbol name
+      arr = [true, false].to_java :boolean
+      arr.java_class.to_s.should == "[Z"
+
+      arr.length.should == 2
+
+      arr[0].should be_true
+      arr[1].should be_false
+
+
+      # Check with type
+      arr = [true, false].to_java Java::boolean
+      arr.java_class.to_s.should == "[Z"
+
+      arr.length.should == 2
+
+      arr[0].should be_true
+      arr[1].should be_false
+    end
+    
+    it "should be possible to set values in primitive array" do 
+      arr = Java::boolean[5].new
+      arr[3] = true
+      
+      arr[0].should be_false
+      arr[1].should be_false
+      arr[2].should be_false
+      arr[3].should be_true
+      arr[4].should be_false
+    end
+
+    it "should be possible to get values from primitive array" do 
+      arr = [false, true, false].to_java :boolean
+      arr[0].should be_false
+      arr[1].should be_true
+      arr[2].should be_false
+    end
+
+    it "should be possible to call methods that take primitive array" do 
+      arr = [false, true, false].to_java :boolean
+      ret = ArrayReceiver::call_with_boolean(arr)
+      ret.to_a.should == [false, true, false]
+    end
+  end
+
+  describe "byte" do 
+    it "should be possible to create empty array" do 
+      arr = Java::byte[0].new
+      arr.java_class.to_s.should == "[B"
+    end
+    
+    it "should be possible to create uninitialized single dimensional array" do 
+      arr = Java::byte[10].new
+      arr.java_class.to_s.should == "[B"
+    end
+    
+    it "should be possible to create uninitialized multi dimensional array" do 
+      arr = Java::byte[10,10].new
+      arr.java_class.to_s.should == "[[B"
+    end
+
+    it "should be possible to create primitive array from Ruby array" do 
+      # Check with symbol name
+      arr = [1,2].to_java :byte
+      arr.java_class.to_s.should == "[B"
+
+      arr.length.should == 2
+
+      arr[0].should == 1
+      arr[1].should == 2
+
+
+      # Check with type
+      arr = [1,2].to_java Java::byte
+      arr.java_class.to_s.should == "[B"
+
+      arr.length.should == 2
+
+      arr[0].should == 1
+      arr[1].should == 2
+    end
+    
+    it "should be possible to set values in primitive array" do 
+      arr = Java::byte[5].new
+      arr[0] = 12
+      arr[1] = 20
+      arr[2] = 42
+      
+      arr[0].should == 12
+      arr[1].should == 20
+      arr[2].should == 42
+      arr[3].should == 0
+      arr[4].should == 0
+    end
+
+    it "should be possible to get values from primitive array" do 
+      arr = [13, 42, 120].to_java :byte
+      arr[0].should == 13
+      arr[1].should == 42
+      arr[2].should == 120
+    end
+
+    it "should be possible to call methods that take primitive array" do 
+      arr = [13, 42, 120].to_java :byte
+      ret = ArrayReceiver::call_with_byte(arr)
+      ret.to_a.should == [13, 42, 120]
+    end
+  end
+
+  describe "char" do 
+    it "should be possible to create empty array" do 
+      arr = Java::char[0].new
+      arr.java_class.to_s.should == "[C"
+    end
+    
+    it "should be possible to create uninitialized single dimensional array" do 
+      arr = Java::char[10].new
+      arr.java_class.to_s.should == "[C"
+    end
+    
+    it "should be possible to create uninitialized multi dimensional array" do 
+      arr = Java::char[10,10].new
+      arr.java_class.to_s.should == "[[C"
+    end
+
+    it "should be possible to create primitive array from Ruby array" do 
+      # Check with symbol name
+      arr = [1,2].to_java :char
+      arr.java_class.to_s.should == "[C"
+
+      arr.length.should == 2
+
+      arr[0].should == 1
+      arr[1].should == 2
+
+
+      # Check with type
+      arr = [1,2].to_java Java::char
+      arr.java_class.to_s.should == "[C"
+
+      arr.length.should == 2
+
+      arr[0].should == 1
+      arr[1].should == 2
+    end
+    
+    it "should be possible to set values in primitive array" do 
+      arr = Java::char[5].new
+      arr[0] = 12
+      arr[1] = 20
+      arr[2] = 42
+      
+      arr[0].should == 12
+      arr[1].should == 20
+      arr[2].should == 42
+      arr[3].should == 0
+      arr[4].should == 0
+    end
+
+    it "should be possible to get values from primitive array" do 
+      arr = [13, 42, 120].to_java :char
+      arr[0].should == 13
+      arr[1].should == 42
+      arr[2].should == 120
+    end
+
+    it "should be possible to call methods that take primitive array" do 
+      arr = [13, 42, 120].to_java :char
+      ret = ArrayReceiver::call_with_char(arr)
+      ret.to_a.should == [13, 42, 120]
+    end
+  end
+
+  describe "double" do 
+    it "should be possible to create empty array" do 
+      arr = Java::double[0].new
+      arr.java_class.to_s.should == "[D"
+    end
+    
+    it "should be possible to create uninitialized single dimensional array" do 
+      arr = Java::double[10].new
+      arr.java_class.to_s.should == "[D"
+    end
+    
+    it "should be possible to create uninitialized multi dimensional array" do 
+      arr = Java::double[10,10].new
+      arr.java_class.to_s.should == "[[D"
+    end
+
+    it "should be possible to create primitive array from Ruby array" do 
+      # Check with symbol name
+      arr = [1.2,2.3].to_java :double
+      arr.java_class.to_s.should == "[D"
+
+      arr.length.should == 2
+
+      arr[0].should == 1.2
+      arr[1].should == 2.3
+
+
+      # Check with type
+      arr = [1.2,2.3].to_java Java::double
+      arr.java_class.to_s.should == "[D"
+
+      arr.length.should == 2
+
+      arr[0].should == 1.2
+      arr[1].should == 2.3
+    end
+    
+    it "should be possible to set values in primitive array" do 
+      arr = Java::double[5].new
+      arr[0] = 12.2
+      arr[1] = 20.3
+      arr[2] = 42.4
+      
+      arr[0].should == 12.2
+      arr[1].should == 20.3
+      arr[2].should == 42.4
+      arr[3].should == 0.0
+      arr[4].should == 0.0
+    end
+
+    it "should be possible to get values from primitive array" do 
+      arr = [13.2, 42.3, 120.4].to_java :double
+      arr[0].should == 13.2
+      arr[1].should == 42.3
+      arr[2].should == 120.4
+    end
+
+    it "should be possible to call methods that take primitive array" do 
+      arr = [13.2, 42.3, 120.4].to_java :double
+      ret = ArrayReceiver::call_with_double(arr)
+      ret.to_a.should == [13.2, 42.3, 120.4]
+    end
+  end
+
+  describe "float" do 
+    it "should be possible to create empty array" do 
+      arr = Java::float[0].new
+      arr.java_class.to_s.should == "[F"
+    end
+    
+    it "should be possible to create uninitialized single dimensional array" do 
+      arr = Java::float[10].new
+      arr.java_class.to_s.should == "[F"
+    end
+    
+    it "should be possible to create uninitialized multi dimensional array" do 
+      arr = Java::float[10,10].new
+      arr.java_class.to_s.should == "[[F"
+    end
+
+    it "should be possible to create primitive array from Ruby array" do 
+      # Check with symbol name
+      arr = [1.2,2.3].to_java :float
+      arr.java_class.to_s.should == "[F"
+
+      arr.length.should == 2
+
+      arr[0].should be_close(1.2, 0.00001)
+      arr[1].should be_close(2.3, 0.00001)
+
+
+      # Check with type
+      arr = [1.2,2.3].to_java Java::float
+      arr.java_class.to_s.should == "[F"
+
+      arr.length.should == 2
+
+      arr[0].should be_close(1.2, 0.00001)
+      arr[1].should be_close(2.3, 0.00001)
+    end
+    
+    it "should be possible to set values in primitive array" do 
+      arr = Java::float[5].new
+      arr[0] = 12.2
+      arr[1] = 20.3
+      arr[2] = 42.4
+      
+      arr[0].should be_close(12.2, 0.00001)
+      arr[1].should be_close(20.3, 0.00001)
+      arr[2].should be_close(42.4, 0.00001)
+      arr[3].should == 0.0
+      arr[4].should == 0.0
+    end
+
+    it "should be possible to get values from primitive array" do 
+      arr = [13.2, 42.3, 120.4].to_java :float
+
+      arr[0].should be_close(13.2, 0.00001)
+      arr[1].should be_close(42.3, 0.00001)
+      arr[2].should be_close(120.4, 0.00001)
+    end
+
+    it "should be possible to call methods that take primitive array" do 
+      arr = [13.2, 42.3, 120.4].to_java :float
+      ret = ArrayReceiver::call_with_float(arr)
+      ret.length.should == 3
+      ret[0].should be_close(13.2, 0.00001)
+      ret[1].should be_close(42.3, 0.00001)
+      ret[2].should be_close(120.4, 0.00001)
+    end
+  end
+
+  describe "int" do 
+    it "should be possible to create empty array" do 
+      arr = Java::int[0].new
+      arr.java_class.to_s.should == "[I"
+    end
+    
+    it "should be possible to create uninitialized single dimensional array" do 
+      arr = Java::int[10].new
+      arr.java_class.to_s.should == "[I"
+    end
+    
+    it "should be possible to create uninitialized multi dimensional array" do 
+      arr = Java::int[10,10].new
+      arr.java_class.to_s.should == "[[I"
+    end
+
+    it "should be possible to create primitive array from Ruby array" do 
+      # Check with symbol name
+      arr = [1,2].to_java :int
+      arr.java_class.to_s.should == "[I"
+
+      arr.length.should == 2
+
+      arr[0].should == 1
+      arr[1].should == 2
+
+
+      # Check with type
+      arr = [1,2].to_java Java::int
+      arr.java_class.to_s.should == "[I"
+
+      arr.length.should == 2
+
+      arr[0].should == 1
+      arr[1].should == 2
+    end
+    
+    it "should be possible to set values in primitive array" do 
+      arr = Java::int[5].new
+      arr[0] = 12
+      arr[1] = 20
+      arr[2] = 42
+      
+      arr[0].should == 12
+      arr[1].should == 20
+      arr[2].should == 42
+      arr[3].should == 0
+      arr[4].should == 0
+    end
+
+    it "should be possible to get values from primitive array" do 
+      arr = [13, 42, 120].to_java :int
+      arr[0].should == 13
+      arr[1].should == 42
+      arr[2].should == 120
+    end
+
+    it "should be possible to call methods that take primitive array" do 
+      arr = [13, 42, 120].to_java :int
+      ret = ArrayReceiver::call_with_int(arr)
+      ret.to_a.should == [13, 42, 120]
+    end
+  end
+
+  describe "long" do 
+    it "should be possible to create empty array" do 
+      arr = Java::long[0].new
+      arr.java_class.to_s.should == "[J"
+    end
+    
+    it "should be possible to create uninitialized single dimensional array" do 
+      arr = Java::long[10].new
+      arr.java_class.to_s.should == "[J"
+    end
+    
+    it "should be possible to create uninitialized multi dimensional array" do 
+      arr = Java::long[10,10].new
+      arr.java_class.to_s.should == "[[J"
+    end
+
+    it "should be possible to create primitive array from Ruby array" do 
+      # Check with symbol name
+      arr = [1,2].to_java :long
+      arr.java_class.to_s.should == "[J"
+
+      arr.length.should == 2
+
+      arr[0].should == 1
+      arr[1].should == 2
+
+
+      # Check with type
+      arr = [1,2].to_java Java::long
+      arr.java_class.to_s.should == "[J"
+
+      arr.length.should == 2
+
+      arr[0].should == 1
+      arr[1].should == 2
+    end
+    
+    it "should be possible to set values in primitive array" do 
+      arr = Java::long[5].new
+      arr[0] = 12
+      arr[1] = 20
+      arr[2] = 42
+      
+      arr[0].should == 12
+      arr[1].should == 20
+      arr[2].should == 42
+      arr[3].should == 0
+      arr[4].should == 0
+    end
+
+    it "should be possible to get values from primitive array" do 
+      arr = [13, 42, 120].to_java :long
+      arr[0].should == 13
+      arr[1].should == 42
+      arr[2].should == 120
+    end
+
+    it "should be possible to call methods that take primitive array" do 
+      arr = [13, 42, 120].to_java :long
+      ret = ArrayReceiver::call_with_long(arr)
+      ret.to_a.should == [13, 42, 120]
+    end
+  end
+
+  describe "short" do 
+    it "should be possible to create empty array" do 
+      arr = Java::short[0].new
+      arr.java_class.to_s.should == "[S"
+    end
+    
+    it "should be possible to create uninitialized single dimensional array" do 
+      arr = Java::short[10].new
+      arr.java_class.to_s.should == "[S"
+    end
+    
+    it "should be possible to create uninitialized multi dimensional array" do 
+      arr = Java::short[10,10].new
+      arr.java_class.to_s.should == "[[S"
+    end
+
+    it "should be possible to create primitive array from Ruby array" do 
+      # Check with symbol name
+      arr = [1,2].to_java :short
+      arr.java_class.to_s.should == "[S"
+
+      arr.length.should == 2
+
+      arr[0].should == 1
+      arr[1].should == 2
+
+
+      # Check with type
+      arr = [1,2].to_java Java::short
+      arr.java_class.to_s.should == "[S"
+
+      arr.length.should == 2
+
+      arr[0].should == 1
+      arr[1].should == 2
+    end
+    
+    it "should be possible to set values in primitive array" do 
+      arr = Java::short[5].new
+      arr[0] = 12
+      arr[1] = 20
+      arr[2] = 42
+      
+      arr[0].should == 12
+      arr[1].should == 20
+      arr[2].should == 42
+      arr[3].should == 0
+      arr[4].should == 0
+    end
+
+    it "should be possible to get values from primitive array" do 
+      arr = [13, 42, 120].to_java :short
+      arr[0].should == 13
+      arr[1].should == 42
+      arr[2].should == 120
+    end
+
+    it "should be possible to call methods that take primitive array" do 
+      arr = [13, 42, 120].to_java :short
+      ret = ArrayReceiver::call_with_short(arr)
+      ret.to_a.should == [13, 42, 120]
+    end
+  end
+
+  describe "string" do 
+    it "should be possible to create empty array" do 
+      arr = java.lang.String[0].new
+      arr.java_class.to_s.should == "[Ljava.lang.String;"
+    end
+    
+    it "should be possible to create uninitialized single dimensional array" do 
+      arr = java.lang.String[10].new
+      arr.java_class.to_s.should == "[Ljava.lang.String;"
+    end
+    
+    it "should be possible to create uninitialized multi dimensional array" do 
+      arr = java.lang.String[10,10].new
+      arr.java_class.to_s.should == "[[Ljava.lang.String;"
+    end
+
+    it "should be possible to create primitive array from Ruby array" do 
+      # Check with symbol name
+      arr = ["foo","bar"].to_java :string
+      arr.java_class.to_s.should == "[Ljava.lang.String;"
+
+      arr.length.should == 2
+
+      arr[0].should == "foo"
+      arr[1].should == "bar"
+
+
+      # Check with type
+      arr = ["foo","bar"].to_java java.lang.String
+      arr.java_class.to_s.should == "[Ljava.lang.String;"
+
+      arr.length.should == 2
+
+      arr[0].should == "foo"
+      arr[1].should == "bar"
+    end
+    
+    it "should be possible to set values in primitive array" do 
+      arr = java.lang.String[5].new
+      arr[0] = "12"
+      arr[1] = "20"
+      arr[2] = "42"
+      
+      arr[0].should == "12"
+      arr[1].should == "20"
+      arr[2].should == "42"
+      arr[3].should be_nil
+      arr[4].should be_nil
+    end
+
+    it "should be possible to get values from primitive array" do 
+      arr = ["flurg", "glax", "morg"].to_java :string
+      arr[0].should == "flurg"
+      arr[1].should == "glax"
+      arr[2].should == "morg"
+    end
+
+    it "should be possible to call methods that take primitive array" do 
+      arr = ["flurg", "glax", "morg"].to_java :string
+      ret = ArrayReceiver::call_with_string(arr)
+      ret.to_a.should == ["flurg", "glax", "morg"]
+    end
+  end
+
+  describe "Object ref" do 
+    it "should be possible to create empty array" do 
+      arr = java.util.HashMap[0].new
+      arr.java_class.to_s.should == "[Ljava.util.HashMap;"
+    end
+    
+    it "should be possible to create uninitialized single dimensional array" do 
+      arr = java.util.HashMap[10].new
+      arr.java_class.to_s.should == "[Ljava.util.HashMap;"
+    end
+    
+    it "should be possible to create uninitialized multi dimensional array" do 
+      arr = java.util.HashMap[10,10].new
+      arr.java_class.to_s.should == "[[Ljava.util.HashMap;"
+    end
+
+    it "should be possible to create primitive array from Ruby array" do
+      h1 = java.util.HashMap.new
+      h1["foo"] = "max"
+
+      h2 = java.util.HashMap.new
+      h2["max"] = "foo"
+
+      arr = [h1, h2].to_java java.util.HashMap
+      arr.java_class.to_s.should == "[Ljava.util.HashMap;"
+
+      arr.length.should == 2
+
+      arr[0].should == h1
+      arr[1].should == h2
+    end
+    
+    it "should be possible to set values in primitive array" do 
+      h1 = java.util.HashMap.new
+      h1["foo"] = "max"
+
+      h2 = java.util.HashMap.new
+      h2["max"] = "foo"
+
+      h3 = java.util.HashMap.new
+      h3["flix"] = "mux"
+
+      arr = java.util.HashMap[5].new
+      arr[0] = h1
+      arr[1] = h2
+      arr[2] = h3
+      
+      arr[0].should == h1
+      arr[1].should == h2
+      arr[2].should == h3
+      arr[3].should be_nil
+      arr[4].should be_nil
+    end
+
+    it "should be possible to get values from primitive array" do
+      h1 = java.util.HashMap.new
+      h1["foo"] = "max"
+
+      h2 = java.util.HashMap.new
+      h2["max"] = "foo"
+
+      h3 = java.util.HashMap.new
+      h3["flix"] = "mux"
+
+      arr = [h1, h2, h3].to_java java.util.HashMap
+      arr[0].should == h1
+      arr[1].should == h2
+      arr[2].should == h3
+    end
+
+    it "should be possible to call methods that take primitive array" do
+      h1 = java.util.HashMap.new
+      h1["foo"] = "max"
+
+      h2 = java.util.HashMap.new
+      h2["max"] = "foo"
+
+      h3 = java.util.HashMap.new
+      h3["flix"] = "mux"
+
+      arr = [h1, h2, h3].to_java java.util.HashMap
+      ret = ArrayReceiver::call_with_object(arr)
+      ret.to_a.should == [h1, h2, h3]
+    end
+  end
+
+  describe "Class ref" do 
+    it "should be possible to create empty array" do 
+      arr = java.lang.Class[0].new
+      arr.java_class.to_s.should == "[Ljava.lang.Class;"
+    end
+    
+    it "should be possible to create uninitialized single dimensional array" do 
+      arr = java.lang.Class[10].new
+      arr.java_class.to_s.should == "[Ljava.lang.Class;"
+    end
+    
+    it "should be possible to create uninitialized multi dimensional array" do 
+      arr = java.lang.Class[10,10].new
+      arr.java_class.to_s.should == "[[Ljava.lang.Class;"
+    end
+
+    it "should be possible to create primitive array from Ruby array" do
+        h1 = java.lang.String.java_class
+        h2 = java.util.HashMap.java_class
+
+        arr = [h1, h2].to_java java.lang.Class
+        arr.java_class.to_s.should == "[Ljava.lang.Class;"
+
+        arr.length.should == 2
+
+        arr[0].should == h1
+        arr[1].should == h2
+    end
+    
+    it "should be possible to set values in primitive array" do 
+        h1 = java.util.Set.java_class
+        h2 = java.util.HashMap.java_class
+        h3 = java.lang.ref.SoftReference.java_class
+
+        arr = java.lang.Class[5].new
+        arr[0] = h1
+        arr[1] = h2
+        arr[2] = h3
+        
+        arr[0].should == h1
+        arr[1].should == h2
+        arr[2].should == h3
+        arr[3].should be_nil
+        arr[4].should be_nil
+    end
+
+    it "should be possible to get values from primitive array" do
+        h1 = java.util.Set.java_class
+        h2 = java.util.HashMap.java_class
+        h3 = java.lang.ref.SoftReference.java_class
+
+        arr = [h1, h2, h3].to_java java.lang.Class
+        arr[0].should == h1
+        arr[1].should == h2
+        arr[2].should == h3
+    end
+
+    it "should be possible to call methods that take primitive array" do
+        h1 = java.util.Set.java_class
+        h2 = java.util.HashMap.java_class
+        h3 = java.lang.ref.SoftReference.java_class
+
+        arr = [h1, h2, h3].to_java java.lang.Class
+        ret = ArrayReceiver::call_with_object(arr)
+        ret.to_a.should == [h1, h2, h3]
+    end
+  end
+end
+
+describe "A Ruby array with a nil element" do
+  it "can be coerced to an array of objects" do
+    ary = [nil]
+    result = ary.to_java java.lang.Runnable
+    result[0].should be_nil
+  end
+
+  it "can be coerced to an array of classes" do
+    ary = [nil]
+    result = ary.to_java java.lang.Class
+    result[0].should be_nil
+  end
+end
+
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
+# From JRUBY-2944; probably could be reduced a bit more.
+describe "A Ruby class implementing an interface returning a Java Object[]" do
+  it "should return an Object[]" do
+    class MyHash < Hash; end
+
+    class Bar
+      include ArrayReturningInterface
+      def blah()
+        a = []
+        a << MyHash.new
+        return a.to_java
+      end
+    end
+    ArrayReturningInterfaceConsumer.new.eat(Bar.new).should_not == nil
+    ArrayReturningInterfaceConsumer.new.eat(Bar.new).java_object.class.name.should == 'Java::JavaArray'
+    ArrayReturningInterfaceConsumer.new.eat(Bar.new).java_object.class.should == Java::JavaArray
+  end
+end
+
diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index cb6a7ef836..0b5888693c 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,1654 +1,1662 @@
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
 
 import org.jruby.java.invokers.StaticFieldGetter;
 import org.jruby.java.invokers.StaticMethodInvoker;
 import org.jruby.java.invokers.InstanceFieldGetter;
 import org.jruby.java.invokers.InstanceFieldSetter;
 import org.jruby.java.invokers.InstanceMethodInvoker;
 import org.jruby.java.invokers.StaticFieldSetter;
 import java.io.ByteArrayOutputStream;
 import java.io.InputStream;
 import java.io.IOException;
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.locks.ReentrantLock;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.java.addons.ArrayJavaAddons;
 import org.jruby.java.proxies.ArrayJavaProxy;
 import org.jruby.java.invokers.ConstructorInvoker;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.ByteList;
 import org.jruby.util.IdUtil;
 
 
 @JRubyClass(name="Java::JavaClass", parent="Java::JavaObject")
 public class JavaClass extends JavaObject {
     /**
      * Assigned names only override based priority of an assigned type, the type must be less than
      * or equal to the assigned type. For example, field name (FIELD) in a subclass will override
      * an alias (ALIAS) in a superclass, but not a method (METHOD).
      */
     private enum Priority {
         RESERVED(0), METHOD(1), FIELD(2), PROTECTED_METHOD(3),
         WEAKLY_RESERVED(4), ALIAS(5), PROTECTED_FIELD(6);
 
         private int value;
 
         Priority(int value) {
             this.value = value;
         }
 
         public boolean asImportantAs(AssignedName other) {
             return other != null && other.type.value == value;
         }
         
         public boolean lessImportantThan(AssignedName other) {
             return other != null && other.type.value < value;
         }
         
         public boolean moreImportantThan(AssignedName other) {
             return other == null || other.type.value > value;
         }
     }
 
     private static class AssignedName {
         String name;
         Priority type;
         
         AssignedName () {}
         AssignedName(String name, Priority type) {
             this.name = name;
             this.type = type;
         }
     }
 
     // TODO: other reserved names?
     private static final Map<String, AssignedName> RESERVED_NAMES = new HashMap<String, AssignedName>();
     static {
         RESERVED_NAMES.put("__id__", new AssignedName("__id__", Priority.RESERVED));
         RESERVED_NAMES.put("__send__", new AssignedName("__send__", Priority.RESERVED));
         RESERVED_NAMES.put("class", new AssignedName("class", Priority.RESERVED));
         RESERVED_NAMES.put("initialize", new AssignedName("initialize", Priority.RESERVED));
         RESERVED_NAMES.put("object_id", new AssignedName("object_id", Priority.RESERVED));
         RESERVED_NAMES.put("private", new AssignedName("private", Priority.RESERVED));
         RESERVED_NAMES.put("protected", new AssignedName("protected", Priority.RESERVED));
         RESERVED_NAMES.put("public", new AssignedName("public", Priority.RESERVED));
 
         // weakly reserved names
         RESERVED_NAMES.put("id", new AssignedName("id", Priority.WEAKLY_RESERVED));
     }
     private static final Map<String, AssignedName> STATIC_RESERVED_NAMES = new HashMap<String, AssignedName>(RESERVED_NAMES);
     static {
         STATIC_RESERVED_NAMES.put("new", new AssignedName("new", Priority.RESERVED));
         STATIC_RESERVED_NAMES.put("inspect", new AssignedName("inspect", Priority.RESERVED));
     }
     private static final Map<String, AssignedName> INSTANCE_RESERVED_NAMES = new HashMap<String, AssignedName>(RESERVED_NAMES);
 
     private static abstract class NamedInstaller {
         static final int STATIC_FIELD = 1;
         static final int STATIC_METHOD = 2;
         static final int INSTANCE_FIELD = 3;
         static final int INSTANCE_METHOD = 4;
         static final int CONSTRUCTOR = 5;
         String name;
         int type;
         Visibility visibility = Visibility.PUBLIC;
         boolean isProtected;
         NamedInstaller () {}
         NamedInstaller (String name, int type) {
             this.name = name;
             this.type = type;
         }
         abstract void install(RubyModule proxy);
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
 
     private static abstract class FieldInstaller extends NamedInstaller {
         Field field;
         FieldInstaller(){}
         FieldInstaller(String name, int type, Field field) {
             super(name,type);
             this.field = field;
         }
     }
 
     private static class StaticFieldGetterInstaller extends FieldInstaller {
         StaticFieldGetterInstaller(){}
         StaticFieldGetterInstaller(String name, Field field) {
             super(name,STATIC_FIELD,field);
         }
         void install(RubyModule proxy) {
             if (Modifier.isPublic(field.getModifiers())) {
                 proxy.getSingletonClass().addMethod(name, new StaticFieldGetter(name, proxy, field));
             }
         }
     }
 
     private static class StaticFieldSetterInstaller extends FieldInstaller {
         StaticFieldSetterInstaller(){}
         StaticFieldSetterInstaller(String name, Field field) {
             super(name,STATIC_FIELD,field);
         }
         void install(RubyModule proxy) {
             if (Modifier.isPublic(field.getModifiers())) {
                 proxy.getSingletonClass().addMethod(name, new StaticFieldSetter(name, proxy, field));
             }
         }
     }
 
     private static class InstanceFieldGetterInstaller extends FieldInstaller {
         InstanceFieldGetterInstaller(){}
         InstanceFieldGetterInstaller(String name, Field field) {
             super(name,INSTANCE_FIELD,field);
         }
         void install(RubyModule proxy) {
             if (Modifier.isPublic(field.getModifiers())) {
                 proxy.addMethod(name, new InstanceFieldGetter(name, proxy, field));
             }
         }
     }
 
     private static class InstanceFieldSetterInstaller extends FieldInstaller {
         InstanceFieldSetterInstaller(){}
         InstanceFieldSetterInstaller(String name, Field field) {
             super(name,INSTANCE_FIELD,field);
         }
         void install(RubyModule proxy) {
             if (Modifier.isPublic(field.getModifiers())) {
                 proxy.addMethod(name, new InstanceFieldSetter(name, proxy, field));
             }
         }
     }
 
     private static abstract class MethodInstaller extends NamedInstaller {
         private boolean haveLocalMethod;
         protected List<Method> methods;
         protected List<String> aliases;
         MethodInstaller(){}
         MethodInstaller(String name, int type) {
             super(name,type);
         }
 
         // called only by initializing thread; no synchronization required
         void addMethod(Method method, Class<?> javaClass) {
             if (methods == null) {
                 methods = new ArrayList<Method>();
             }
             if (!Ruby.isSecurityRestricted()) {
                 method.setAccessible(true);
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
         @Override
         boolean hasLocalMethod () {
             return haveLocalMethod;
         }
     }
 
     private static class ConstructorInvokerInstaller extends MethodInstaller {
         private boolean haveLocalConstructor;
         protected List<Constructor> constructors;
         
         ConstructorInvokerInstaller(String name) {
             super(name,STATIC_METHOD);
         }
 
         // called only by initializing thread; no synchronization required
         void addConstructor(Constructor ctor, Class<?> javaClass) {
             if (constructors == null) {
                 constructors = new ArrayList<Constructor>();
             }
             if (!Ruby.isSecurityRestricted()) {
                 ctor.setAccessible(true);
             }
             constructors.add(ctor);
             haveLocalConstructor |= javaClass == ctor.getDeclaringClass();
         }
         
         void install(RubyModule proxy) {
             if (haveLocalConstructor) {
                 DynamicMethod method = new ConstructorInvoker(proxy, constructors);
                 proxy.addMethod(name, method);
+            } else {
+                // if there's no constructor, we must prevent construction
+                proxy.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod() {
+                    @Override
+                    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
+                        throw context.getRuntime().newTypeError("no public constructors for " + clazz);
+                    }
+                });
             }
         }
     }
 
     private static class StaticMethodInvokerInstaller extends MethodInstaller {
         StaticMethodInvokerInstaller(String name) {
             super(name,STATIC_METHOD);
         }
 
         void install(RubyModule proxy) {
             if (hasLocalMethod()) {
                 RubyClass singleton = proxy.getSingletonClass();
                 DynamicMethod method = new StaticMethodInvoker(singleton, methods);
                 singleton.addMethod(name, method);
                 if (aliases != null && isPublic() ) {
                     singleton.defineAliases(aliases, this.name);
                     aliases = null;
                 }
             }
         }
     }
 
     private static class InstanceMethodInvokerInstaller extends MethodInstaller {
         InstanceMethodInvokerInstaller(String name) {
             super(name,INSTANCE_METHOD);
         }
         void install(RubyModule proxy) {
             if (hasLocalMethod()) {
                 DynamicMethod method = new InstanceMethodInvoker(proxy, methods);
                 proxy.addMethod(name, method);
                 if (aliases != null && isPublic()) {
                     proxy.defineAliases(aliases, this.name);
                     aliases = null;
                 }
             }
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
                 // TODO: catch exception if constant is already set by other
                 // thread
                 if (!Ruby.isSecurityRestricted()) {
                     field.setAccessible(true);
                 }
                 try {
                     proxy.setConstant(field.getName(), JavaUtil.convertJavaToUsableRubyObject(proxy.getRuntime(), field.get(null)));
                 } catch (IllegalAccessException iae) {
                     throw proxy.getRuntime().newTypeError(
                                         "illegal access on setting variable: " + iae.getMessage());
                 }
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
     private Map<String, NamedInstaller> staticInstallers;
     private Map<String, NamedInstaller> instanceInstallers;
     private ConstructorInvokerInstaller constructorInstaller;
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
     
     @Override
     public boolean equals(Object other) {
         return other instanceof JavaClass &&
             this.getValue() == ((JavaClass)other).getValue();
     }
     
     private void initializeInterface(Class<?> javaClass) {
         Map<String, AssignedName> staticNames  = new HashMap<String, AssignedName>(STATIC_RESERVED_NAMES);
         List<ConstantField> constants = new ArrayList<ConstantField>(); 
         Map<String, NamedInstaller> staticCallbacks = new HashMap<String, NamedInstaller>();
         Field[] fields = getDeclaredFields(javaClass); 
 
         for (int i = fields.length; --i >= 0; ) {
             Field field = fields[i];
             if (javaClass != field.getDeclaringClass()) continue;
             if (ConstantField.isConstant(field)) constants.add(new ConstantField(field));
             
             int modifiers = field.getModifiers();
             if (Modifier.isStatic(modifiers)) addField(staticCallbacks, staticNames, field, Modifier.isFinal(modifiers), true);
         }
         this.staticAssignedNames = staticNames;
         this.staticInstallers = staticCallbacks;        
         this.constantFields = constants;
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
         Map<String, NamedInstaller> staticCallbacks = new HashMap<String, NamedInstaller>();
         Map<String, NamedInstaller> instanceCallbacks = new HashMap<String, NamedInstaller>();
         List<ConstantField> constantFields = new ArrayList<ConstantField>();
         
         setupClassFields(javaClass, constantFields, staticNames, staticCallbacks, instanceNames, instanceCallbacks);
         setupClassMethods(javaClass, staticNames, staticCallbacks, instanceNames, instanceCallbacks);
         setupClassConstructors(javaClass);
         
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
 
         installClassFields(proxy);
         installClassMethods(proxy);
         installClassConstructors(proxy);
         installClassConstants(javaClass, proxy);
         
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
         if (name == null) return;
 
         AssignedName assignedName = assignedNames.get(name);
         // TODO: missing additional logic for dealing with conflicting protected fields.
         if (Priority.ALIAS.moreImportantThan(assignedName)) {
             installer.addAlias(name);
             assignedNames.put(name, new AssignedName(name, Priority.ALIAS));
         } else if (Priority.ALIAS.asImportantAs(assignedName)) {
             installer.addAlias(name);
         }
     }
 
     private void installClassConstants(final Class<?> javaClass, final RubyClass proxy) {
         // setup constants for public inner classes
         Class<?>[] classes = getClasses(javaClass);
 
         for (int i = classes.length; --i >= 0;) {
             if (javaClass == classes[i].getDeclaringClass()) {
                 Class<?> clazz = classes[i];
                 String simpleName = getSimpleName(clazz);
                 if (simpleName.length() == 0) {
                     continue;
                 }
                 // Ignore bad constant named inner classes pending JRUBY-697
                 if (IdUtil.isConstant(simpleName) && proxy.getConstantAt(simpleName) == null) {
                     proxy.setConstant(simpleName, Java.get_proxy_class(JAVA_UTILITIES, get(getRuntime(), clazz)));
                 }
             }
         }
     }
 
     private void installClassConstructors(final RubyClass proxy) {
         if (constructorInstaller != null) {
             constructorInstaller.install(proxy);
         }
     }
 
     private void installClassFields(final RubyClass proxy) {
         for (ConstantField field : constantFields) {
             field.install(proxy);
         }
     }
 
     private void installClassMethods(final RubyClass proxy) {
         for (NamedInstaller installer : staticInstallers.values()) {
             if (installer.type == NamedInstaller.STATIC_METHOD && installer.hasLocalMethod()) {
                 assignAliases((MethodInstaller) installer, staticAssignedNames);
             }
             installer.install(proxy);            
         }
         for (NamedInstaller installer : instanceInstallers.values()) {
             if (installer.type == NamedInstaller.INSTANCE_METHOD && installer.hasLocalMethod()) {
                 assignAliases((MethodInstaller) installer, instanceAssignedNames);
             }
             installer.install(proxy);
         }
     }
 
     private void setupClassConstructors(Class<?> javaClass) {
         // TODO: protected methods.  this is going to require a rework
         // of some of the mechanism.
         Constructor[] constructors = getConstructors(javaClass);
+        
+        // create constructorInstaller; if there are no constructors, it will disable construction
+        constructorInstaller = new ConstructorInvokerInstaller("__jcreate!");
 
         for (int i = constructors.length; --i >= 0;) {
             // we need to collect all methods, though we'll only
             // install the ones that are named in this class
             Constructor ctor = constructors[i];
-            if (constructorInstaller == null) {
-                constructorInstaller = new ConstructorInvokerInstaller("__jcreate!");
-            }
             constructorInstaller.addConstructor(ctor, javaClass);
         }
     }
     
     private void addField(Map <String, NamedInstaller> callbacks, Map<String, AssignedName> names,
             Field field, boolean isFinal, boolean isStatic) {
         String name = field.getName();
 
         if (Priority.FIELD.lessImportantThan(names.get(name))) return;
 
         names.put(name, new AssignedName(name, Priority.FIELD));
         callbacks.put(name, isStatic ? new StaticFieldGetterInstaller(name, field) :
             new InstanceFieldGetterInstaller(name, field));
 
         if (!isFinal) {
             String setName = name + '=';
             callbacks.put(setName, isStatic ? new StaticFieldSetterInstaller(setName, field) :
                 new InstanceFieldSetterInstaller(setName, field));
         }
     }
     
     private void setupClassFields(Class<?> javaClass, List<ConstantField> constantFields, Map<String, AssignedName> staticNames, Map<String, NamedInstaller> staticCallbacks, Map<String, AssignedName> instanceNames, Map<String, NamedInstaller> instanceCallbacks) {
         Field[] fields = getFields(javaClass);
         
         for (int i = fields.length; --i >= 0;) {
             Field field = fields[i];
             if (javaClass != field.getDeclaringClass()) continue;
 
             if (ConstantField.isConstant(field)) {
                 constantFields.add(new ConstantField(field));
                 continue;
             }
 
             int modifiers = field.getModifiers();
             if (Modifier.isStatic(modifiers)) {
                 addField(staticCallbacks, staticNames, field, Modifier.isFinal(modifiers), true);
             } else {
                 addField(instanceCallbacks, instanceNames, field, Modifier.isFinal(modifiers), false);
             }
         }
     }
 
     private void setupClassMethods(Class<?> javaClass, Map<String, AssignedName> staticNames, Map<String, NamedInstaller> staticCallbacks, Map<String, AssignedName> instanceNames, Map<String, NamedInstaller> instanceCallbacks) {
         // TODO: protected methods.  this is going to require a rework of some of the mechanism.
         Method[] methods = getMethods(javaClass);
 
         for (int i = methods.length; --i >= 0;) {
             // we need to collect all methods, though we'll only
             // install the ones that are named in this class
             Method method = methods[i];
             String name = method.getName();
             if (Modifier.isStatic(method.getModifiers())) {
                 AssignedName assignedName = staticNames.get(name);
                 if (assignedName == null) {
                     staticNames.put(name, new AssignedName(name, Priority.METHOD));
                 } else {
                     if (Priority.METHOD.lessImportantThan(assignedName)) continue;
                     if (!Priority.METHOD.asImportantAs(assignedName)) {
                         staticCallbacks.remove(name);
                         staticCallbacks.remove(name + '=');
                         staticNames.put(name, new AssignedName(name, Priority.METHOD));
                     }
                 }
                 StaticMethodInvokerInstaller invoker = (StaticMethodInvokerInstaller) staticCallbacks.get(name);
                 if (invoker == null) {
                     invoker = new StaticMethodInvokerInstaller(name);
                     staticCallbacks.put(name, invoker);
                 }
                 invoker.addMethod(method, javaClass);
             } else {
                 AssignedName assignedName = instanceNames.get(name);
                 if (assignedName == null) {
                     instanceNames.put(name, new AssignedName(name, Priority.METHOD));
                 } else {
                     if (Priority.METHOD.lessImportantThan(assignedName)) continue;
                     if (!Priority.METHOD.asImportantAs(assignedName)) {
                         instanceCallbacks.remove(name);
                         instanceCallbacks.remove(name + '=');
                         instanceNames.put(name, new AssignedName(name, Priority.METHOD));
                     }
                 }
                 InstanceMethodInvokerInstaller invoker = (InstanceMethodInvokerInstaller) instanceCallbacks.get(name);
                 if (invoker == null) {
                     invoker = new InstanceMethodInvokerInstaller(name);
                     instanceCallbacks.put(name, invoker);
                 }
                 invoker.addMethod(method, javaClass);
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
         for (NamedInstaller installer : staticInstallers.values()) {
             if (installer.type == NamedInstaller.STATIC_METHOD && installer.hasLocalMethod()) {
                 assignAliases((MethodInstaller)installer,staticAssignedNames);
             }
             installer.install(module);
         }        
         // setup constants for public inner classes
         Class<?>[] classes = getClasses(javaClass);
 
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
 
     private static Map<String, Class> PRIMITIVE_TO_CLASS = new HashMap<String,Class>();
 
     static {
         PRIMITIVE_TO_CLASS.put("byte", byte.class);
         PRIMITIVE_TO_CLASS.put("boolean", boolean.class);
         PRIMITIVE_TO_CLASS.put("short", short.class);
         PRIMITIVE_TO_CLASS.put("char", char.class);
         PRIMITIVE_TO_CLASS.put("int", int.class);
         PRIMITIVE_TO_CLASS.put("long", long.class);
         PRIMITIVE_TO_CLASS.put("float", float.class);
         PRIMITIVE_TO_CLASS.put("double", double.class);
     }
     
     public static synchronized JavaClass forNameVerbose(Ruby runtime, String className) {
         Class <?> klass = null;
         if (className.indexOf(".") == -1 && Character.isLowerCase(className.charAt(0))) {
             // one word type name that starts lower-case...it may be a primitive type
             klass = PRIMITIVE_TO_CLASS.get(className);
         }
 
         if (klass == null) {
             klass = runtime.getJavaSupport().loadJavaClassVerbose(className);
         }
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
                     return RuntimeHelpers.invokeAs(self.getRuntime().getCurrentContext(), superClass, self, name, newArgs, block);
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
         Class<?>[] parameterTypes = new Class<?>[args.length];
         for (int i = 0; i < args.length; i++) {
             JavaClass type;
             if (args[i] instanceof JavaClass) {
                 type = (JavaClass)args[i];
             } else if (args[i].respondsTo("java_class")) {
                 type = (JavaClass)args[i].callMethod(getRuntime().getCurrentContext(), "java_class");
             } else {
                 type = for_name(this, args[i]);
             }
             parameterTypes[i] = type.javaClass();
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
         
         if (javaClass().isArray()) {
             // if it's an array of arrays, recurse with the component type
             for (int i = 0; i < rubyArray.size(); i++) {
                 JavaClass componentType = component_type();
                 IRubyObject wrappedComponentArray = componentType.javaArrayFromRubyArray(context, rubyArray.eltInternal(i));
                 javaArray.setWithExceptionHandling(i, JavaUtil.unwrapJavaObject(wrappedComponentArray));
             }
         } else {
             ArrayJavaAddons.copyDataToJavaArray(context, rubyArray, javaArray);
         }
         
         RubyClass proxyClass = (RubyClass)Java.get_proxy_class(javaArray, array_class());
 
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
     public JavaField field(ThreadContext context, IRubyObject name) {
         Class<?> javaClass = javaClass();
         Ruby runtime = context.getRuntime();
         String stringName = name.asJavaString();
 
         try {
             return new JavaField(runtime, javaClass.getField(stringName));
         } catch (NoSuchFieldException nsfe) {
             String newName = JavaUtil.getJavaCasedName(stringName);
             if(newName != null) {
                 try {
                     return new JavaField(runtime, javaClass.getField(newName));
                 } catch (NoSuchFieldException nsfe2) {}
             }
             throw undefinedFieldError(runtime, javaClass.getName(), stringName);
          }
     }
 
     @JRubyMethod(required = 1)
     public JavaField declared_field(ThreadContext context, IRubyObject name) {
         Class<?> javaClass = javaClass();
         Ruby runtime = context.getRuntime();
         String stringName = name.asJavaString();
         
         try {
             return new JavaField(runtime, javaClass.getDeclaredField(stringName));
         } catch (NoSuchFieldException nsfe) {
             String newName = JavaUtil.getJavaCasedName(stringName);
             if(newName != null) {
                 try {
                     return new JavaField(runtime, javaClass.getDeclaredField(newName));
                 } catch (NoSuchFieldException nsfe2) {}
             }
             throw undefinedFieldError(runtime, javaClass.getName(), stringName);
         }
     }
     
     public static RaiseException undefinedFieldError(Ruby runtime, String javaClassName, String name) {
         return runtime.newNameError("undefined field '" + name + "' for class '" + javaClassName + "'", name);
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
diff --git a/src/org/jruby/javasupport/JavaUtil.java b/src/org/jruby/javasupport/JavaUtil.java
index 9e6ef6b270..707c21c4e2 100644
--- a/src/org/jruby/javasupport/JavaUtil.java
+++ b/src/org/jruby/javasupport/JavaUtil.java
@@ -1,1226 +1,1226 @@
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
 import java.lang.reflect.Method;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.HashMap;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 
 import java.util.Set;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.jruby.Ruby;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyNil;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.RubyTime;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.java.proxies.JavaProxy;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.util.ByteList;
 import org.jruby.util.TypeConverter;
 
 public class JavaUtil {
 
     public static Object convertRubyToJava(IRubyObject rubyObject) {
         return convertRubyToJava(rubyObject, Object.class);
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
                         context, "to_i")).getLongValue());
             }
             return new Byte((byte) 0);
         }
     };
     
     public static final RubyConverter RUBY_SHORT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Short((short) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return new Short((short) 0);
         }
     };
     
     public static final RubyConverter RUBY_CHAR_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Character((char) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return new Character((char) 0);
         }
     };
     
     public static final RubyConverter RUBY_INTEGER_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Integer((int) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return new Integer(0);
         }
     };
     
     public static final RubyConverter RUBY_LONG_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Long(((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return new Long(0);
         }
     };
     
     public static final RubyConverter RUBY_FLOAT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Float((float) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_f")).getDoubleValue());
             }
             return new Float(0.0);
         }
     };
     
     public static final RubyConverter RUBY_DOUBLE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Double(((RubyNumeric) rubyObject.callMethod(
                         context, "to_f")).getDoubleValue());
             }
             return new Double(0.0);
         }
     };
     
     public static final RubyConverter ARRAY_BOOLEAN_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject == context.getRuntime().getFalse() || rubyObject.isNil()) {
                 return Boolean.FALSE;
             } else if (rubyObject == context.getRuntime().getTrue()) {
                 return Boolean.TRUE;
             } else if (rubyObject instanceof RubyNumeric) {
                 return ((RubyNumeric)rubyObject).getLongValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
             } else if (rubyObject instanceof RubyString) {
                 return Boolean.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return integer.getLongValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_BYTE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Byte.valueOf((byte)((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return Byte.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Byte.valueOf((byte)integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_SHORT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Short.valueOf((short)((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return Short.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Short.valueOf((short)integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
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
                 return convertRubyToJava(rubyObject);
             }
         }
     };
     
     public static final RubyConverter ARRAY_CLASS_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof JavaClass) {
                 return ((JavaClass)rubyObject).javaClass();
             } else {
                 return convertRubyToJava(rubyObject);
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
         
         if (rubyObject.dataGetStruct() instanceof JavaObject) {
             rubyObject = (JavaObject) rubyObject.dataGetStruct();
         } else if (rubyObject.respondsTo("java_object")) {
             rubyObject = rubyObject.callMethod(context, "java_object");
         } else if (rubyObject.respondsTo("to_java_object")) {
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
 
             String s = ((RubyString)TypeConverter.convertToType(rubyObject, rubyObject.getRuntime().getString(), "to_s", true)).getUnicodeValue();
             if (s.length() > 0) {
                 return new Character(s.charAt(0));
             }
             return new Character('\0');
         } else if (javaClass == String.class) {
             RubyString rubyString = (RubyString) rubyObject.callMethod(context, "to_s");
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
          		RubyNumeric rubyNumeric = ((RubyNumeric)rubyObject.callMethod(context, "to_f"));
  				return  BigInteger.valueOf (rubyNumeric.getLongValue());
          	}
         } else if (javaClass == BigDecimal.class && !(rubyObject instanceof JavaObject)) {
          	if (rubyObject.respondsTo("to_f")) {
              	double double_value = ((RubyNumeric)rubyObject.callMethod(context, "to_f")).getDoubleValue();
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
 
             singletonClass.addMethod("method_missing", new DynamicMethod(singletonClass, Visibility.PUBLIC, CallConfiguration.NO_FRAME_NO_SCOPE) {
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (!(self instanceof RubyProc)) {
                         throw context.getRuntime().newTypeError("interface impl method_missing for block used with non-Proc object");
                     }
                     RubyProc proc = (RubyProc)self;
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
         JavaObject jo = (JavaObject) RuntimeHelpers.invoke(context, rubyObject, "__jcreate_meta!");
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
-            
+
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
         if (javaObject != null && isDuckTypeConvertable(javaObject.getClass(), target)) {
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
         return coerceNumericToType(fixnum, target);
     }
         
     public static Object coerceBignumToType(RubyBignum bignum, Class target) {
         return coerceNumericToType(bignum, target);
     }
     
     public static Object coerceFloatToType(RubyFloat flote, Class target) {
         return coerceNumericToType(flote, target);
     }
 
     public static Object coerceNumericToType(RubyNumeric numeric, Class target) {
         // TODO: this could be faster
         if (target.isPrimitive()) {
             if (target == Byte.TYPE) {
                 return Byte.valueOf((byte)numeric.getLongValue());
             } else if (target == Short.TYPE) {
                 return Short.valueOf((short)numeric.getLongValue());
             } else if (target == Character.TYPE) {
                 return Character.valueOf((char)numeric.getLongValue());
             } else if (target == Integer.TYPE) {
                 return Integer.valueOf((int)numeric.getLongValue());
             } else if (target == Long.TYPE) {
                 return Long.valueOf(numeric.getLongValue());
             } else if (target == Double.TYPE) {
                 return Double.valueOf(numeric.getDoubleValue());
             } else if (target == Float.TYPE) {
                 return Float.valueOf((float)numeric.getDoubleValue());
             }
         } else if (target == Byte.class) {
             return Byte.valueOf((byte)numeric.getLongValue());
         } else if (target == Short.class) {
             return Short.valueOf((short)numeric.getLongValue());
         } else if (target == Character.class) {
             return Character.valueOf((char)numeric.getLongValue());
         } else if (target == Integer.class) {
             return Integer.valueOf((int)numeric.getLongValue());
         } else if (target == Long.class) {
             return Long.valueOf((long)numeric.getLongValue());
         } else if (target == Float.class) {
             return Float.valueOf((float)numeric.getDoubleValue());
         } else if (target == Double.class) {
             return Double.valueOf((double)numeric.getDoubleValue());
         } else if (target == Object.class) {
             // for Object, default to natural wrapper type
             if (numeric instanceof RubyFixnum) {
                 return Long.valueOf(numeric.getLongValue());
             } else if (numeric instanceof RubyFloat) {
                 return Double.valueOf(numeric.getDoubleValue());
             } else if (numeric instanceof RubyBignum) {
                 return ((RubyBignum)numeric).getValue();
             }
         }
         throw numeric.getRuntime().newTypeError("could not coerce " + numeric.getMetaClass() + " to " + target);
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
 
     public static Object unwrapJavaValue(Ruby runtime, IRubyObject obj, String errorMessage) {
         if(obj instanceof JavaMethod) {
             return ((JavaMethod)obj).getValue();
         } else if(obj instanceof JavaConstructor) {
             return ((JavaConstructor)obj).getValue();
         } else if(obj instanceof JavaField) {
             return ((JavaField)obj).getValue();
         } else if(obj instanceof JavaObject) {
             return ((JavaObject)obj).getValue();
         } else if(obj.dataGetStruct() != null && (obj.dataGetStruct() instanceof IRubyObject)) {
             return unwrapJavaValue(runtime, ((IRubyObject)obj.dataGetStruct()), errorMessage);
         } else {
             throw runtime.newTypeError(errorMessage);
         }
     }
 }
