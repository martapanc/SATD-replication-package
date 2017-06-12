diff --git a/spec/java_integration/types/coercion_spec.rb b/spec/java_integration/types/coercion_spec.rb
index 29bf049ab8..a6cff348c7 100644
--- a/spec/java_integration/types/coercion_spec.rb
+++ b/spec/java_integration/types/coercion_spec.rb
@@ -1,784 +1,792 @@
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
 
   describe "when passed java.lang.String" do
     it "coerces to java.lang.String" do
       cs = "123".to_java java.lang.String
 
       cs.class.should == java.lang.String
     end
   end
 
   describe "when passed java.lang.CharSequence" do
     it "coerces to java.lang.String" do
       cs = "123".to_java java.lang.CharSequence
 
       cs.class.should == java.lang.String
     end
   end
 
   describe "when passed java.lang.Object" do
     it "coerces to java.lang.String" do
       cs = "123".to_java java.lang.Object
 
       cs.class.should == java.lang.String
     end
   end
 
   describe "when passed void (java.lang.Void.TYPE)" do
     it "coerces to null" do
       cs = "123".to_java Java::java.lang.Void::TYPE
 
       cs.class.should == NilClass
     end
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
 end
 
 describe "Time\"to_java" do
   describe "when passed java.util.Date" do
     it "coerces to java.util.Date" do
       t = Time.now
       d = t.to_java(java.util.Date)
       d.class.should == java.util.Date
     end
   end
 
   describe "when passed java.util.Calendar" do
     it "coerces to java.util.Calendar" do
       t = Time.now
       d = t.to_java(java.util.Calendar)
       d.class.should < java.util.Calendar
     end
   end
 
   describe "when passed java.sql.Date" do
     it "coerces to java.sql.Date" do
       t = Time.now
       d = t.to_java(java.sql.Date)
       d.class.should == java.sql.Date
     end
   end
 
   describe "when passed java.sql.Time" do
     it "coerces to java.sql.Time" do
       t = Time.now
       d = t.to_java(java.sql.Time)
       d.class.should == java.sql.Time
     end
   end
 
   describe "when passed java.sql.Timestamp" do
     it "coerces to java.sql.Timestamp" do
       t = Time.now
       d = t.to_java(java.sql.Timestamp)
       d.class.should == java.sql.Timestamp
     end
   end
 
   describe "when passed org.joda.time.DateTime" do
     it "coerces to org.joda.time.DateTime" do
       t = Time.now
       d = t.to_java(org.joda.time.DateTime)
       d.class.should == org.joda.time.DateTime
     end
   end
+
+  describe "when passed java.lang.Object" do
+    it "coerces to java.util.Date" do
+      t = Time.now
+      d = t.to_java(java.lang.Object)
+      d.class.should == java.util.Date
+    end
+  end
 end
\ No newline at end of file
diff --git a/src/org/jruby/RubyTime.java b/src/org/jruby/RubyTime.java
index e414e9a0a8..471b6a46d8 100644
--- a/src/org/jruby/RubyTime.java
+++ b/src/org/jruby/RubyTime.java
@@ -1,956 +1,958 @@
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
 
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
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
 
     @Override
     public Object toJava(Class target) {
         if (target.equals(Date.class)) {
             return getJavaDate();
         } else if (target.equals(Calendar.class)) {
             Calendar cal = GregorianCalendar.getInstance();
             cal.setTime(getJavaDate());
             return cal;
         } else if (target.equals(DateTime.class)) {
             return this.dt;
         } else if (target.equals(java.sql.Date.class)) {
             return new java.sql.Date(dt.getMillis());
         } else if (target.equals(java.sql.Time.class)) {
             return new java.sql.Time(dt.getMillis());
         } else if (target.equals(java.sql.Timestamp.class)) {
             return new java.sql.Timestamp(dt.getMillis());
+        } else if (target.isAssignableFrom(Date.class)) {
+            return getJavaDate();
         } else {
             return super.toJava(target);
         }
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
