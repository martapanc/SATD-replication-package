diff --git a/spec/java_integration/fixtures/ProtectedConstructor.java b/spec/java_integration/fixtures/ProtectedConstructor.java
index 3ec528abd2..ee20d61f81 100644
--- a/spec/java_integration/fixtures/ProtectedConstructor.java
+++ b/spec/java_integration/fixtures/ProtectedConstructor.java
@@ -1,7 +1,6 @@
 package java_integration.fixtures;
 
 public class ProtectedConstructor {
-  protected String theProtectedMethod() {
-    return "42";
+  protected ProtectedConstructor() {
   }
 }
\ No newline at end of file
diff --git a/spec/java_integration/types/coercion_spec.rb b/spec/java_integration/types/coercion_spec.rb
index 3a6eb311d7..e89ff16bbf 100644
--- a/spec/java_integration/types/coercion_spec.rb
+++ b/spec/java_integration/types/coercion_spec.rb
@@ -1,446 +1,443 @@
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
   it "should coerce primitive Ruby types to the narrowest possible boxed Java type" do
     CoreTypeMethods.getObjectType("foo").should == "class java.lang.String"
 
     CoreTypeMethods.getObjectType(0).should == "class java.lang.Byte"
     CoreTypeMethods.getObjectType(java::lang::Byte::MAX_VALUE).should == "class java.lang.Byte"
     CoreTypeMethods.getObjectType(java::lang::Byte::MIN_VALUE).should == "class java.lang.Byte"
     CoreTypeMethods.getObjectType(java::lang::Byte::MAX_VALUE + 1).should == "class java.lang.Short"
     CoreTypeMethods.getObjectType(java::lang::Byte::MIN_VALUE - 1).should == "class java.lang.Short"
 
     CoreTypeMethods.getObjectType(java::lang::Short::MAX_VALUE).should == "class java.lang.Short"
     CoreTypeMethods.getObjectType(java::lang::Short::MIN_VALUE).should == "class java.lang.Short"
     CoreTypeMethods.getObjectType(java::lang::Short::MAX_VALUE + 1).should == "class java.lang.Integer"
     CoreTypeMethods.getObjectType(java::lang::Short::MIN_VALUE - 1).should == "class java.lang.Integer"
 
     CoreTypeMethods.getObjectType(java::lang::Integer::MAX_VALUE).should == "class java.lang.Integer"
     CoreTypeMethods.getObjectType(java::lang::Integer::MIN_VALUE).should == "class java.lang.Integer"
     CoreTypeMethods.getObjectType(java::lang::Integer::MAX_VALUE + 1).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Integer::MIN_VALUE - 1).should == "class java.lang.Long"
 
     CoreTypeMethods.getObjectType(java::lang::Long::MAX_VALUE).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Long::MIN_VALUE).should == "class java.lang.Long"
     CoreTypeMethods.getObjectType(java::lang::Long::MAX_VALUE + 1).should == "class java.math.BigInteger"
     CoreTypeMethods.getObjectType(java::lang::Long::MIN_VALUE - 1).should == "class java.math.BigInteger"
 
     CoreTypeMethods.getObjectType(java::lang::Float::MAX_VALUE).should == "class java.lang.Float"
     CoreTypeMethods.getObjectType(java::lang::Float::MIN_VALUE).should == "class java.lang.Float"
     CoreTypeMethods.getObjectType(-java::lang::Float::MAX_VALUE).should == "class java.lang.Float"
     CoreTypeMethods.getObjectType(-java::lang::Float::MIN_VALUE).should == "class java.lang.Float"
 
     CoreTypeMethods.getObjectType(java::lang::Float::NaN).should == "class java.lang.Float"
     CoreTypeMethods.getObjectType(0.0).should == "class java.lang.Float"
 
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
 end
 
 describe "Java types with package or private constructors" do
-  it "should not be construcible" do
+  it "should not be constructible" do
     lambda { PackageConstructor.new }.should raise_error(TypeError)
     lambda { PrivateConstructor.new }.should raise_error(TypeError)
   end
 end
 
 describe "Java types with protected constructors" do
-  it "should not be construcible" do
-    pc = nil
-    lambda { pc = ProtectedConstructor.new }.should_not raise_error(TypeError)
-    ProtectedConstructor.should === pc
-    pc.java_class.name.should == "java_integration.fixtures.ProtectedConstructor"
+  it "should not be constructible" do
+    lambda { ProtectedConstructor.new }.should raise_error(TypeError)
   end
 end
diff --git a/spec/java_integration/types/construction_spec.rb b/spec/java_integration/types/construction_spec.rb
index 840a9c9758..9949c43f1f 100644
--- a/spec/java_integration/types/construction_spec.rb
+++ b/spec/java_integration/types/construction_spec.rb
@@ -1,792 +1,839 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 import "java_integration.fixtures.ArrayReceiver"
 import "java_integration.fixtures.ArrayReturningInterface"
 import "java_integration.fixtures.ArrayReturningInterfaceConsumer"
+import "java_integration.fixtures.PublicConstructor"
+import "java_integration.fixtures.ProtectedConstructor"
+import "java_integration.fixtures.PackageConstructor"
+import "java_integration.fixtures.PrivateConstructor"
 
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
 
+describe "A Ruby class extending a Java class" do
+  it "should fail when constructing through private superclass constructor" do
+    cls = Class.new(PrivateConstructor) do
+      def initialize
+        super()
+      end
+    end
+
+    lambda {cls.new}.should raise_error
+  end
+
+  it "should fail when constructing through package superclass constructor" do
+    cls = Class.new(PackageConstructor) do
+      def initialize
+        super()
+      end
+    end
+
+    lambda {cls.new}.should raise_error
+  end
+
+  it "should succeed when constructing through public superclass constructor" do
+    cls = Class.new(PublicConstructor) do
+      def initialize
+        super()
+      end
+    end
+
+    lambda {cls.new}.should_not raise_error
+  end
+
+  it "should succeed when constructing through protected superclass constructor" do
+    cls = Class.new(ProtectedConstructor) do
+      def initialize
+        super()
+      end
+    end
+
+    lambda {cls.new}.should_not raise_error
+  end
+
+end
+
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 1deb1bd4d5..624fe51d4f 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1068 +1,1063 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Kiel Hodges <jruby-devel@selfsosoft.com>
  * Copyright (C) 2006 Evan Buswell <evan@heron.sytes.net>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
  * Copyright (C) 2008 Joseph LaFata <joe@quibb.org>
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
 
 import java.io.ByteArrayOutputStream;
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.Random;
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.anno.FrameField.*;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.javasupport.util.RuntimeHelpers;
-import org.jruby.parser.StaticScope;
-import org.jruby.runtime.Arity;
-import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
-import org.jruby.runtime.Block.Type;
-import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.IdUtil;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  */
 @JRubyModule(name="Kernel")
 public class RubyKernel {
     public final static Class<?> IRUBY_OBJECT = IRubyObject.class;
 
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         runtime.setKernel(module);
 
         module.defineAnnotatedMethods(RubyKernel.class);
         module.defineAnnotatedMethods(RubyObject.class);
         
         runtime.setRespondToMethod(module.searchMethod("respond_to?"));
         
         module.setFlag(RubyObject.USER7_F, false); //Kernel is the only Module that doesn't need an implementor
 
         return module;
     }
 
     @JRubyMethod(name = "at_exit", frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject at_exit(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().pushExitBlock(context.getRuntime().newProc(Block.Type.PROC, block));
     }
 
     @JRubyMethod(name = "autoload?", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject autoload_p(ThreadContext context, final IRubyObject recv, IRubyObject symbol) {
         Ruby runtime = context.getRuntime();
         RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : runtime.getObject();
         String name = module.getName() + "::" + symbol.asJavaString();
         
         IAutoloadMethod autoloadMethod = runtime.getLoadService().autoloadFor(name);
         if (autoloadMethod == null) return runtime.getNil();
 
         return runtime.newString(autoloadMethod.file());
     }
 
     @JRubyMethod(name = "autoload", required = 2, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         Ruby runtime = recv.getRuntime(); 
         final LoadService loadService = runtime.getLoadService();
         String nonInternedName = symbol.asJavaString();
         
         if (!IdUtil.isValidConstantName(nonInternedName)) {
             throw runtime.newNameError("autoload must be constant name", nonInternedName);
         }
         
         RubyString fileString = file.convertToString();
         
         if (fileString.isEmpty()) {
             throw runtime.newArgumentError("empty file name");
         }
         
         final String baseName = symbol.asJavaString().intern(); // interned, OK for "fast" methods
         final RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : runtime.getObject();
         String nm = module.getName() + "::" + baseName;
         
         IRubyObject existingValue = module.fastFetchConstant(baseName); 
         if (existingValue != null && existingValue != RubyObject.UNDEF) return runtime.getNil();
         
         module.fastStoreConstant(baseName, RubyObject.UNDEF);
         
         loadService.addAutoload(nm, new IAutoloadMethod() {
             public String file() {
                 return file.toString();
             }
             /**
              * @see org.jruby.runtime.load.IAutoloadMethod#load(Ruby, String)
              */
             public IRubyObject load(Ruby runtime, String name) {
                 boolean required = loadService.require(file());
                 
                 // File to be loaded by autoload has already been or is being loaded.
                 if (!required) return null;
                 
                 return module.fastGetConstant(baseName);
             }
         });
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "method_missing", rest = true, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject method_missing(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         if (args.length == 0 || !(args[0] instanceof RubySymbol)) throw runtime.newArgumentError("no id given");
 
         Visibility lastVis = context.getLastVisibility();
         CallType lastCallType = context.getLastCallType();
 
         // create a lightweight thunk
         IRubyObject msg = new RubyNameError.RubyNameErrorMessage(runtime, 
                                                                  recv,
                                                                  args[0],
                                                                  lastVis,
                                                                  lastCallType);
         final IRubyObject[]exArgs;
         final RubyClass exc;
         if (lastCallType != CallType.VARIABLE) {
             exc = runtime.getNoMethodError();
             exArgs = new IRubyObject[]{msg, args[0], RubyArray.newArrayNoCopy(runtime, args, 1)};
         } else {
             exc = runtime.getNameError();
             exArgs = new IRubyObject[]{msg, args[0]};
         }
         
         throw new RaiseException((RubyException)exc.newInstance(context, exArgs, Block.NULL_BLOCK));
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         String arg = args[0].convertToString().toString();
         Ruby runtime = context.getRuntime();
 
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
             // exec process, create IO with process
             return RubyIO.popen(context, runtime.getIO(), new IRubyObject[] {runtime.newString(command)}, block);
         } 
 
         return RubyFile.open(context, runtime.getFile(), args, block);
     }
 
     @JRubyMethod(name = "getc", module = true, visibility = PRIVATE)
     public static IRubyObject getc(ThreadContext context, IRubyObject recv) {
         context.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "getc is obsolete; use STDIN.getc instead", "getc", "STDIN.getc");
         IRubyObject defin = context.getRuntime().getGlobalVariables().get("$stdin");
         return defin.callMethod(context, "getc");
     }
 
     @JRubyMethod(name = "gets", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject gets(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.gets(context, context.getRuntime().getGlobalVariables().get("$<"), args);
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject abort(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if(args.length == 1) {
             context.getRuntime().getGlobalVariables().get("$stderr").callMethod(context,"puts",args[0]);
         }
         throw new MainExitException(1,true);
     }
 
     @JRubyMethod(name = "Array", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_array(ThreadContext context, IRubyObject recv, IRubyObject object) {
         IRubyObject value = object.checkArrayType();
 
         if (value.isNil()) {
             if (object.getMetaClass().searchMethod("to_a").getImplementationClass() != context.getRuntime().getKernel()) {
                 value = object.callMethod(context, "to_a");
                 if (!(value instanceof RubyArray)) throw context.getRuntime().newTypeError("`to_a' did not return Array");
                 return value;
             } else {
                 return context.getRuntime().newArray(object);
             }
         }
         return value;
     }
 
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert");
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg);
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg0, arg1);
     }
     
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert");
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg);
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg0, arg1);
     }
 
     @JRubyMethod(name = "Float", module = true, visibility = PRIVATE)
     public static RubyFloat new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return (RubyFloat)object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString)object).getByteList().realSize == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = (RubyFloat)TypeConverter.convertToType(object, recv.getRuntime().getFloat(), "to_f");
             if (Double.isNaN(rFloat.getDoubleValue())) throw recv.getRuntime().newArgumentError("invalid value for Float()");
             return rFloat;
         }
     }
 
     @JRubyMethod(name = "Integer", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_integer(ThreadContext context, IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue(); 
             if (val > (double) RubyFixnum.MAX && val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(context.getRuntime(),((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(context.getRuntime(),(RubyString)object,0,true);
         }
         
         IRubyObject tmp = TypeConverter.convertToType(object, context.getRuntime().getInteger(), "to_int", false);
         if (tmp.isNil()) return object.convertToInteger("to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_string(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType(object, context.getRuntime().getString(), "to_s");
     }
 
     @JRubyMethod(name = "p", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject p(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         IRubyObject defout = runtime.getGlobalVariables().get("$>");
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod(context, "write", RubyObject.inspect(context, args[i]));
                 defout.callMethod(context, "write", runtime.newString("\n"));
             }
         }
 
         IRubyObject result = runtime.getNil();
         if (runtime.getInstanceConfig().getCompatVersion() == CompatVersion.RUBY1_9) {
             if (args.length == 1) {
                 result = args[0];
             } else if (args.length > 1) {
                 result = runtime.newArray(args);
             }
         }
 
         if (defout instanceof RubyFile) {
             ((RubyFile)defout).flush();
         }
 
         return result;
     }
 
     /** rb_f_putc
      */
     @JRubyMethod(name = "putc", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject putc(ThreadContext context, IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
         return defout.callMethod(context, "putc", ch);
     }
 
     @JRubyMethod(name = "puts", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject puts(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
         
         defout.callMethod(context, "puts", args);
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "print", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject print(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
         defout.callMethod(context, "print", args);
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "printf", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject printf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             defout.callMethod(context, "write", RubyKernel.sprintf(recv, args));
         }
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "readline", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject readline(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(context, recv, args);
 
         if (line.isNil()) throw context.getRuntime().newEOFError();
 
         return line;
     }
 
     @JRubyMethod(name = "readlines", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.readlines(context, context.getRuntime().getGlobalVariables().get("$<"), args);
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(ThreadContext context, Ruby runtime) {
         IRubyObject line = context.getPreviousFrame().getLastLine();
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, args, block);
     }
 
     @JRubyMethod(name = "sub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "sub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, arg1, block);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, args, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, arg1, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, args, block);
     }
 
     @JRubyMethod(name = "gsub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "gsub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, arg1, block);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, args, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, arg1, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chop!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chop_bang(ThreadContext context, IRubyObject recv, Block block) {
         return getLastlineString(context, context.getRuntime()).chop_bang(context);
     }
 
     @JRubyMethod(name = "chop", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chop(ThreadContext context, IRubyObject recv, Block block) {
         RubyString str = getLastlineString(context, context.getRuntime());
 
         if (str.getByteList().realSize > 0) {
             str = (RubyString) str.dup();
             str.chop_bang(context);
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the zero or one-arg versions.
      */
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(args);
     }
 
     @JRubyMethod(name = "chomp!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(context);
     }
 
     @JRubyMethod(name = "chomp!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(context, arg0);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the zero or one-arg versions.
      */
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(args).isNil()) {
             return str;
         } 
 
         context.getPreviousFrame().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(name = "chomp", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context).isNil()) {
             return str;
         } 
 
         context.getPreviousFrame().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(name = "chomp", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context, arg0).isNil()) {
             return str;
         } 
 
         context.getPreviousFrame().setLastLine(dup);
         return dup;
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * 
      * @param context The thread context for the current thread
      * @param recv The receiver of the method (usually a class that has included Kernel)
      * @return
      * @deprecated Use the versions with zero, one, or two args.
      */
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(context, context.getRuntime()).split(context, args);
     }
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF})
     public static IRubyObject split(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).split(context);
     }
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF})
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0);
     }
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF})
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0, arg1);
     }
 
     @JRubyMethod(name = "scan", required = 1, frame = true, module = true, visibility = PRIVATE, reads = {LASTLINE, BACKREF}, writes = {LASTLINE, BACKREF})
     public static IRubyObject scan(ThreadContext context, IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(context, context.getRuntime()).scan(context, pattern, block);
     }
 
     @JRubyMethod(name = "select", required = 1, optional = 3, module = true, visibility = PRIVATE)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyIO.select_static(context, context.getRuntime(), args);
     }
 
     @JRubyMethod(name = "sleep", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject sleep(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         long milliseconds;
 
         if (args.length == 0) {
             // Zero sleeps forever
             milliseconds = 0;
         } else {
             if (!(args[0] instanceof RubyNumeric)) {
                 throw context.getRuntime().newTypeError("can't convert " + args[0].getMetaClass().getName() + "into time interval");
             }
             milliseconds = (long) (args[0].convertToFloat().getDoubleValue() * 1000);
             if (milliseconds < 0) {
                 throw context.getRuntime().newArgumentError("time interval must be positive");
             } else if (milliseconds == 0) {
                 // Explicit zero in MRI returns immediately
                 return context.getRuntime().newFixnum(0);
             }
         }
         long startTime = System.currentTimeMillis();
         
         RubyThread rubyThread = context.getThread();
 
         // Spurious wakeup-loop
         do {
             long loopStartTime = System.currentTimeMillis();
             try {
                 // We break if we know this sleep was explicitly woken up/interrupted
                 if (!rubyThread.sleep(milliseconds)) break;
             } catch (InterruptedException iExcptn) {
             }
             milliseconds -= (System.currentTimeMillis() - loopStartTime);
         } while (milliseconds > 0);
 
         return context.getRuntime().newFixnum(Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
     @JRubyMethod(name = "exit", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         exit(recv.getRuntime(), args, false);
         return recv.getRuntime().getNil(); // not reached
     }
 
     @JRubyMethod(name = "exit!", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         exit(recv.getRuntime(), args, true);
         return recv.getRuntime().getNil(); // not reached
     }
     
     private static void exit(Ruby runtime, IRubyObject[] args, boolean hard) {
         runtime.secure(4);
 
         int status = 1;
         if (args.length > 0) {
             RubyObject argument = (RubyObject)args[0];
             if (argument instanceof RubyFixnum) {
                 status = RubyNumeric.fix2int(argument);
             } else {
                 status = argument.isFalse() ? 1 : 0;
             }
         }
 
         if (hard) {
             throw new MainExitException(status, true);
         } else {
             throw runtime.newSystemExit(status);
         }
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
     @JRubyMethod(name = "global_variables", module = true, visibility = PRIVATE)
     public static RubyArray global_variables(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         RubyArray globalVariables = runtime.newArray();
 
         for (String globalVariableName : runtime.getGlobalVariables().getNames()) {
             globalVariables.append(runtime.newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     /** Returns an Array with the names of all local variables.
      *
      */
     @JRubyMethod(name = "local_variables", module = true, visibility = PRIVATE)
     public static RubyArray local_variables(ThreadContext context, IRubyObject recv) {
         final Ruby runtime = context.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         for (String name: context.getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newString(name));
         }
 
         return localVariables;
     }
 
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyBinding binding(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime(), recv);
     }
 
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyBinding binding_1_9(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime());
     }
 
     @JRubyMethod(name = {"block_given?", "iterator?"}, frame = true, module = true, visibility = PRIVATE)
     public static RubyBoolean block_given_p(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newBoolean(context.getPreviousFrame().getBlock().isGiven());
     }
 
 
     @Deprecated
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         return sprintf(recv.getRuntime().getCurrentContext(), recv, args);
     }
 
     @JRubyMethod(name = {"sprintf", "format"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject sprintf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw context.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = context.getRuntime().newArrayNoCopy(args);
         newArgs.shift(context);
 
         return str.op_format(context, newArgs);
     }
 
     @JRubyMethod(name = {"raise", "fail"}, optional = 3, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject raise(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         Ruby runtime = context.getRuntime();
 
         if (args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if (lastException.isNil()) {
                 throw new RaiseException(runtime, runtime.getRuntimeError(), "", false);
             } 
             throw new RaiseException((RubyException) lastException);
         }
 
         IRubyObject exception;
         
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException((RubyException)runtime.getRuntimeError().newInstance(context, args, block));
             }
             
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             exception = args[0].callMethod(context, "exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
 
             exception = args[0].callMethod(context, "exception", args[1]);
         }
         
         if (!runtime.fastGetClass("Exception").isInstance(exception)) {
             throw runtime.newTypeError("exception object expected");
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
 
         if (runtime.getDebug().isTrue()) {
             printExceptionSummary(context, runtime, (RubyException) exception);
         }
 
         throw new RaiseException((RubyException) exception);
     }
 
     private static void printExceptionSummary(ThreadContext context, Ruby runtime, RubyException rEx) {
         Frame currentFrame = context.getCurrentFrame();
 
         String msg = String.format("Exception `%s' at %s:%s - %s\n",
                 rEx.getMetaClass(),
                 currentFrame.getFile(), currentFrame.getLine() + 1,
                 rEx.to_s());
 
         IRubyObject errorStream = runtime.getGlobalVariables().get("$stderr");
         errorStream.callMethod(context, "write", runtime.newString(msg));
     }
 
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     @JRubyMethod(name = "require", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         Ruby runtime = recv.getRuntime();
         
         if (runtime.getLoadService().require(name.convertToString().toString())) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = "load", required = 1, optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyString file = args[0].convertToString();
         boolean wrap = args.length == 2 ? args[1].isTrue() : false;
 
         runtime.getLoadService().load(file.getByteList().toString(), wrap);
         
         return runtime.getTrue();
     }
 
     @JRubyMethod(name = "eval", required = 1, optional = 3, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject eval(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
             
         // string to eval
         RubyString src = args[0].convertToString();
         runtime.checkSafeString(src);
         
         IRubyObject scope = args.length > 1 && !args[1].isNil() ? args[1] : null;
         String file;
         if (args.length > 2) {
             file = args[2].convertToString().toString();
         } else if (scope == null) {
             file = "(eval)";
         } else {
             file = null;
         }
         int line;
         if (args.length > 3) {
             line = (int) args[3].convertToInteger().getLongValue();
         } else if (scope == null) {
             line = 0;
         } else {
             line = -1;
         }
         if (scope == null) scope = RubyBinding.newBindingForEval(context);
         
         return ASTInterpreter.evalWithBinding(context, src, scope, file, line);
     }
 
     @JRubyMethod(name = "callcc", frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject callcc(ThreadContext context, IRubyObject recv, Block block) {
         RubyContinuation continuation = new RubyContinuation(context.getRuntime());
         return continuation.enter(context, block);
     }
 
     @JRubyMethod(name = "caller", optional = 1, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject caller(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw context.getRuntime().newArgumentError("negative level(" + level + ')');
         }
 
         return context.createCallerBacktrace(context.getRuntime(), level);
     }
 
     @JRubyMethod(name = "caller", optional = 1, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject caller1_9(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw context.getRuntime().newArgumentError("negative level(" + level + ')');
         }
 
         return context.createCallerBacktrace(context.getRuntime(), level);
     }
 
     @JRubyMethod(name = "catch", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject rbCatch(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         CatchTarget target = new CatchTarget(tag.asJavaString());
         try {
             context.pushCatch(target);
             return block.yield(context, tag);
         } catch (JumpException.ThrowJump tj) {
             if (tj.getTarget() == target) return (IRubyObject) tj.getValue();
             
             throw tj;
         } finally {
             context.popCatch();
         }
     }
     
     public static class CatchTarget implements JumpTarget {
         private final String tag;
         public CatchTarget(String tag) { this.tag = tag; }
         public String getTag() { return tag; }
     }
 
     @JRubyMethod(name = "throw", required = 1, frame = true, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject rbThrow(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         String tag = args[0].asJavaString();
         CatchTarget[] catches = context.getActiveCatches();
 
         String message = "uncaught throw `" + tag + "'";
 
         // Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i].getTag())) {
                 //Catch active, throw for catch to handle
                 throw new JumpException.ThrowJump(catches[i], args.length > 1 ? args[1] : runtime.getNil());
             }
         }
 
         // No catch active for this throw
         RubyThread currentThread = context.getThread();
         if (currentThread == runtime.getThreadService().getMainThread()) {
             throw runtime.newNameError(message, tag);
         } else {
             throw runtime.newThreadError(message + " in thread 0x" + Integer.toHexString(RubyInteger.fix2int(currentThread.id())));
         }
     }
 
     @JRubyMethod(name = "trap", required = 1, frame = true, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject trap(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         context.getRuntime().getLoadService().require("jsignal");
         return RuntimeHelpers.invoke(context, recv, "__jtrap", args, block);
     }
     
     @JRubyMethod(name = "warn", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject warn(ThreadContext context, IRubyObject recv, IRubyObject message) {
         Ruby runtime = context.getRuntime();
         
         if (runtime.warningsEnabled()) {
             IRubyObject out = runtime.getGlobalVariables().get("$stderr");
             RuntimeHelpers.invoke(context, out, "puts", message);
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "set_trace_func", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject set_trace_func(ThreadContext context, IRubyObject recv, IRubyObject trace_func, Block block) {
         if (trace_func.isNil()) {
             context.getRuntime().setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw context.getRuntime().newTypeError("trace_func needs to be Proc.");
         } else {
             context.getRuntime().setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     @JRubyMethod(name = "trace_var", required = 1, optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject trace_var(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw context.getRuntime().newArgumentError(0, 1);
         RubyProc proc = null;
         String var = args.length > 1 ? args[0].toString() : null;
         // ignore if it's not a global var
         if (var.charAt(0) != '$') return context.getRuntime().getNil();
         if (args.length == 1) proc = RubyProc.newProc(context.getRuntime(), block, Block.Type.PROC);
         if (args.length == 2) {
             proc = (RubyProc)TypeConverter.convertToType(args[1], context.getRuntime().getProc(), "to_proc", true);
         }
         
         context.getRuntime().getGlobalVariables().setTraceVar(var, proc);
         
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "untrace_var", required = 1, optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject untrace_var(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw context.getRuntime().newArgumentError(0, 1);
         String var = args.length >= 1 ? args[0].toString() : null;
 
         // ignore if it's not a global var
         if (var.charAt(0) != '$') return context.getRuntime().getNil();
         
         if (args.length > 1) {
             ArrayList<IRubyObject> success = new ArrayList<IRubyObject>();
             for (int i = 1; i < args.length; i++) {
                 if (context.getRuntime().getGlobalVariables().untraceVar(var, args[i])) {
                     success.add(args[i]);
                 }
             }
             return RubyArray.newArray(context.getRuntime(), success);
         } else {
             context.getRuntime().getGlobalVariables().untraceVar(var);
         }
         
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_added", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_added(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_removed", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_removed(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_undefined", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_undefined(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
     
     @JRubyMethod(name = {"proc", "lambda"}, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyProc proc(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @Deprecated
     public static RubyProc proc(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = {"lambda"}, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyProc lambda(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = {"proc"}, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyProc proc_1_9(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.PROC, block);
     }
 
     @JRubyMethod(name = "loop", frame = true, module = true, visibility = PRIVATE)
diff --git a/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java b/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
index 2e55385d12..7a4e121c23 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
@@ -1,818 +1,807 @@
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
                 String pkg = proxyPackageName(superClass);
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
 
-            int acc = constructor.getModifiers();
-            if (Modifier.isProtected(acc) || Modifier.isPublic(acc)) {
-                // ok, it's publix or protected
-            } else if (!Modifier.isPrivate(acc)
-                    && packageName(constructor.getDeclaringClass()).equals(
-                            packageName(selfType.getClassName()))) {
-                // ok, it's package scoped and we're in the same package
-            } else {
-                // it's unaccessible
-                continue;
-            }
-
+            // We generate all constructors and let some fail during invocation later
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
 
     private static String proxyPackageName(Class clazz) {
         String clazzName = clazz.getName();
         int idx = clazzName.lastIndexOf('.');
         if (idx == -1) {
             return "org.jruby.proxy";
         } else {
             return "org.jruby.proxy." + clazzName.substring(0, idx);
         }
     }
 
 }
diff --git a/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java b/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java
index 92394696a2..5a3ebbd214 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java
@@ -1,313 +1,315 @@
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
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.ParameterTypes;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
-import org.jruby.runtime.CallType;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaProxyConstructor extends JavaProxyReflectionObject implements ParameterTypes {
 
     private final Constructor<?> proxyConstructor;
     private final Class<?>[] apparentParameterTypes;
 
     private final JavaProxyClass declaringProxyClass;
 
     JavaProxyConstructor(Ruby runtime, JavaProxyClass pClass,
             Constructor<?> constructor) {
         super(runtime, runtime.getJavaSupport().getJavaModule().fastGetClass(
                 "JavaProxyConstructor"));
         this.declaringProxyClass = pClass;
         this.proxyConstructor = constructor;
         Class<?>[] parameterTypes = constructor.getParameterTypes();
         int len = parameterTypes.length - 1;
         this.apparentParameterTypes = new Class<?>[len];
         System.arraycopy(parameterTypes, 0, apparentParameterTypes, 0, len);
     }
 
     public Class<?>[] getParameterTypes() {
         return apparentParameterTypes;
     }
 
     public Class<?>[] getExceptionTypes() {
         return proxyConstructor.getExceptionTypes();
     }
     
     public boolean isVarArgs() {
         return proxyConstructor.isVarArgs();
     }
 
     @JRubyMethod(name = "declaring_class")
     public JavaProxyClass getDeclaringClass() {
         return declaringProxyClass;
     }
 
     public Object newInstance(Object[] args, JavaProxyInvocationHandler handler)
             throws IllegalArgumentException, InstantiationException,
             IllegalAccessException, InvocationTargetException {
         if (args.length != apparentParameterTypes.length) {
             throw new IllegalArgumentException("wrong number of parameters");
         }
 
         Object[] realArgs = new Object[args.length + 1];
         System.arraycopy(args, 0, realArgs, 0, args.length);
         realArgs[args.length] = handler;
 
         return proxyConstructor.newInstance(realArgs);
     }
 
     public static RubyClass createJavaProxyConstructorClass(Ruby runtime,
             RubyModule javaProxyModule) {
         RubyClass result = javaProxyModule.defineClassUnder("JavaProxyConstructor",
                 runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         JavaProxyReflectionObject.registerRubyMethods(runtime, result);
 
         result.defineAnnotatedMethods(JavaProxyConstructor.class);
 
         return result;
 
     }
 
     @JRubyMethod
     public RubyFixnum arity() {
         return getRuntime().newFixnum(getParameterTypes().length);
     }
     
     public boolean equals(Object other) {
         return other instanceof JavaProxyConstructor &&
             this.proxyConstructor == ((JavaProxyConstructor)other).proxyConstructor;
     }
     
     public int hashCode() {
         return proxyConstructor.hashCode();
     }
 
     protected String nameOnInspection() {
         return getDeclaringClass().nameOnInspection();
     }
 
     public IRubyObject inspect() {
         StringBuilder result = new StringBuilder();
         result.append(nameOnInspection());
         Class<?>[] parameterTypes = getParameterTypes();
         for (int i = 0; i < parameterTypes.length; i++) {
             result.append(parameterTypes[i].getName());
             if (i < parameterTypes.length - 1) {
                 result.append(',');
             }
         }
         result.append(")>");
         return getRuntime().newString(result.toString());
     }
 
     @JRubyMethod
     public RubyArray argument_types() {
         return buildRubyArray(getParameterTypes());
     }
     
     @JRubyMethod(frame = true, rest = true)
     public RubyObject new_instance2(IRubyObject[] args, Block unusedBlock) {
         Arity.checkArgumentCount(getRuntime(), args, 2, 2);
 
         final IRubyObject self = args[0];
         final Ruby runtime = self.getRuntime();
         final RubyModule javaUtilities = runtime.getJavaSupport().getJavaUtilitiesModule();
         RubyArray constructor_args = (RubyArray) args[1];
         Class<?>[] parameterTypes = getParameterTypes();
         int count = (int) constructor_args.length().getLongValue();
         Object[] converted = new Object[count];
         
         for (int i = 0; i < count; i++) {
             // TODO: call ruby method
             IRubyObject ith = constructor_args.aref(getRuntime().newFixnum(i));
             converted[i] = JavaUtil.convertArgument(getRuntime(), Java.ruby_to_java(this, ith, Block.NULL_BLOCK), parameterTypes[i]);
         }
 
         JavaProxyInvocationHandler handler = new JavaProxyInvocationHandler() {
             public IRubyObject getOrig() {
                 return self;
             }
 
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
                     return JavaUtil.convertRubyToJava(RuntimeHelpers.invoke(runtime.getCurrentContext(), self, name, newArgs), m.getReturnType());
                 } else {
                     RubyClass superClass = self.getMetaClass().getSuperClass();
                     return JavaUtil.convertRubyToJava(RuntimeHelpers.invokeAs(runtime.getCurrentContext(), superClass, self, name, newArgs, Block.NULL_BLOCK), m.getReturnType());
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
     
     public JavaObject newInstance(final IRubyObject self, Object[] args) {
         final Ruby runtime = self.getRuntime();
         final RubyModule javaUtilities = runtime.getJavaSupport().getJavaUtilitiesModule();
 
         JavaProxyInvocationHandler handler = new JavaProxyInvocationHandler() {
             public IRubyObject getOrig() {
                 return self;
             }
 
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
                     return JavaUtil.convertRubyToJava(RuntimeHelpers.invoke(runtime.getCurrentContext(), self, name, newArgs), m.getReturnType());
                 } else {
                     RubyClass superClass = self.getMetaClass().getSuperClass();
                     return JavaUtil.convertRubyToJava(RuntimeHelpers.invokeAs(runtime.getCurrentContext(), superClass, self, name, newArgs, Block.NULL_BLOCK), m.getReturnType());
                 }
             }
         };
 
         try {
             return JavaObject.wrap(getRuntime(), newInstance(args, handler));
-        } catch (Exception e) {
+        } catch (Throwable t) {
+            while (t.getCause() != null) {
+                t = t.getCause();
+            }
             RaiseException ex = getRuntime().newArgumentError(
-                    "Constructor invocation failed: " + e.getMessage());
-            ex.initCause(e);
+                    "Constructor invocation failed: " + t.getMessage());
+            ex.initCause(t);
             throw ex;
         }
     }
 
     @JRubyMethod(required = 1, optional = 1, frame = true)
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
         Class<?>[] parameterTypes = getParameterTypes();
 
         int count = (int) constructor_args.length().getLongValue();
         Object[] converted = new Object[count];
         for (int i = 0; i < count; i++) {
             // TODO: call ruby method
             IRubyObject ith = constructor_args.aref(getRuntime().newFixnum(i));
             converted[i] = JavaUtil.convertArgument(getRuntime(), Java.ruby_to_java(this, ith, Block.NULL_BLOCK), parameterTypes[i]);
         }
 
         final IRubyObject recv = this;
 
         JavaProxyInvocationHandler handler = new JavaProxyInvocationHandler() {
             public IRubyObject getOrig() {
                 return null;
             }
 
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
                 IRubyObject call_result = proc.call(getRuntime().getCurrentContext(), rubyArgs);
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
