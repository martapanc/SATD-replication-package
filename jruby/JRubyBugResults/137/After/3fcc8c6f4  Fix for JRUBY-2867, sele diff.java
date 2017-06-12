diff --git a/spec/java_integration/types/coercion_spec.rb b/spec/java_integration/types/coercion_spec.rb
index 1b2c454250..52bf53cce2 100644
--- a/spec/java_integration/types/coercion_spec.rb
+++ b/spec/java_integration/types/coercion_spec.rb
@@ -1,327 +1,328 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 import "java_integration.fixtures.CoreTypeMethods"
 import "java_integration.fixtures.JavaFields"
 import "java_integration.fixtures.ValueReceivingInterface"
 import "java_integration.fixtures.ValueReceivingInterfaceHandler"
 
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
     
     CoreTypeMethods.setFloat(1.5).should == "1.5"
     CoreTypeMethods.setDouble(1.5).should == "1.5"
     
     CoreTypeMethods.setBooleanTrue(true).should == "true"
     CoreTypeMethods.setBooleanFalse(false).should == "false"
     
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
-    pending("Fixnum always selects long method") do
-      CoreTypeMethods.getType(1).should == "byte"
-      CoreTypeMethods.getType(1 << 8).should == "short"
-      CoreTypeMethods.getType(1 << 16).should == "int"
-    end
+    CoreTypeMethods.getType(1).should == "byte"
+    CoreTypeMethods.getType(1 << 8).should == "short"
+    CoreTypeMethods.getType(1 << 16).should == "int"
     CoreTypeMethods.getType(1 << 32).should == "long"
     
-    CoreTypeMethods.getType(1.0).should == "double"
+    CoreTypeMethods.getType(1.0).should == "float"
+    CoreTypeMethods.getType(2.0 ** 128).should == "double"
     
     CoreTypeMethods.getType("foo").should == "String"
-    CoreTypeMethods.getType(nil).should == "CharSequence"
+    pending "passing null to overloaded methods randomly selects from them" do
+      CoreTypeMethods.getType(nil).should == "CharSequence"
+    end
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
\ No newline at end of file
diff --git a/src/org/jruby/RubyFixnum.java b/src/org/jruby/RubyFixnum.java
index 7f922ab69c..9b7fc97d85 100644
--- a/src/org/jruby/RubyFixnum.java
+++ b/src/org/jruby/RubyFixnum.java
@@ -1,860 +1,869 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Antti Karanta <antti.karanta@napa.fi>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.math.BigInteger;
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.java.MiniJava;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.Convert;
 import org.jruby.util.TypeCoercer;
 
 /** 
  * Implementation of the Fixnum class.
  */
 @JRubyClass(name="Fixnum", parent="Integer", include="Precision")
 public class RubyFixnum extends RubyInteger {
     
     public static RubyClass createFixnumClass(Ruby runtime) {
         RubyClass fixnum = runtime.defineClass("Fixnum", runtime.getInteger(),
                 ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setFixnum(fixnum);
         fixnum.index = ClassIndex.FIXNUM;
         fixnum.kindOf = new RubyModule.KindOf() {
                 @Override
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyFixnum;
                 }
             };
 
         fixnum.includeModule(runtime.getPrecision());
         
         fixnum.defineAnnotatedMethods(RubyFixnum.class);
         
         for (int i = 0; i < runtime.fixnumCache.length; i++) {
             runtime.fixnumCache[i] = new RubyFixnum(runtime, fixnum, i - 128);
         }
 
         return fixnum;
     }    
     
     private final long value;
     private static final int BIT_SIZE = 64;
     public static final long SIGN_BIT = (1L << (BIT_SIZE - 1));
     public static final long MAX = (1L<<(BIT_SIZE - 1)) - 1;
     public static final long MIN = -1 * MAX - 1;
     public static final long MAX_MARSHAL_FIXNUM = (1L << 30) - 1; // 0x3fff_ffff
     public static final long MIN_MARSHAL_FIXNUM = - (1L << 30);   // -0x4000_0000
 
     private static IRubyObject fixCoerce(IRubyObject x) {
         do {
             x = x.convertToInteger();
         } while (!(x instanceof RubyFixnum) && !(x instanceof RubyBignum));
         return x;
     }
     
     public RubyFixnum(Ruby runtime) {
         this(runtime, 0);
     }
 
     public RubyFixnum(Ruby runtime, long value) {
         super(runtime, runtime.getFixnum(), false);
         this.value = value;
     }
     
     private RubyFixnum(Ruby runtime, RubyClass klazz, long value) {
         super(runtime, klazz, false);
         this.value = value;
     }
     
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.FIXNUM;
     }
     
     /** 
      * short circuit for Fixnum key comparison
      */
     @Override
     public final boolean eql(IRubyObject other) {
         return other instanceof RubyFixnum && value == ((RubyFixnum)other).value;
     }
     
     @Override
     public boolean isImmediate() {
     	return true;
     }
     
     @Override
     public RubyClass getSingletonClass() {
         throw getRuntime().newTypeError("can't define singleton");
     }
 
     @Override
     public Class<?> getJavaClass() {
+        if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
+            return byte.class;
+        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
+            return short.class;
+        } else if (value >= Character.MIN_VALUE && value <= Character.MAX_VALUE) {
+            return char.class;
+        } else if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
+            return int.class;
+        }
         return long.class;
     }
 
     @Override
     public double getDoubleValue() {
         return value;
     }
 
     @Override
     public long getLongValue() {
         return value;
     }
 
     private static final int CACHE_OFFSET = 128;
     
     public static RubyFixnum newFixnum(Ruby runtime, long value) {
         if (isInCacheRange(value)) {
             return runtime.fixnumCache[(int) value + CACHE_OFFSET];
         }
         return new RubyFixnum(runtime, value);
     }
     
     private static boolean isInCacheRange(long value) {
         return value <= 127 && value >= -128;
     }
 
     public RubyFixnum newFixnum(long newValue) {
         return newFixnum(getRuntime(), newValue);
     }
 
     public static RubyFixnum zero(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET];
     }
 
     public static RubyFixnum one(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET + 1];
     }
     
     public static RubyFixnum two(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET + 2];
     }
     
     public static RubyFixnum three(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET + 3];
     }
     
     public static RubyFixnum four(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET + 4];
     }
     
     public static RubyFixnum five(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET + 5];
     }
 
     public static RubyFixnum minus_one(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET - 1];
     }
 
     @Override
     public RubyFixnum hash() {
         return newFixnum(hashCode());
     }
 
     @Override
     public final int hashCode() {
         return (int)(value ^ value >>> 32);
     }
 
     @Override
     public boolean equals(Object other) {
         if (other == this) {
             return true;
         }
         
         if (other instanceof RubyFixnum) { 
             RubyFixnum num = (RubyFixnum)other;
             
             if (num.value == value) {
                 return true;
             }
         }
         
         return false;
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** fix_to_s
      * 
      */
     public RubyString to_s(IRubyObject[] args) {
         switch (args.length) {
         case 0: return to_s();
         case 1: return to_s(args[0]);
         default: throw getRuntime().newArgumentError(args.length, 1);
         }
     }
     
     @JRubyMethod
     @Override
     public RubyString to_s() {
         int base = 10;
         return getRuntime().newString(Convert.longToByteList(value, base));
     }
     
     @JRubyMethod
     public RubyString to_s(IRubyObject arg0) {
         int base = num2int(arg0);
         if (base < 2 || base > 36) {
             throw getRuntime().newArgumentError("illegal radix " + base);
         }
         return getRuntime().newString(Convert.longToByteList(value, base));
     }
 
     /** fix_id2name
      * 
      */
     @JRubyMethod
     public IRubyObject id2name() {
         RubySymbol symbol = RubySymbol.getSymbolLong(getRuntime(), value);
         
         if (symbol != null) return getRuntime().newString(symbol.asJavaString());
 
         return getRuntime().getNil();
     }
 
     /** fix_to_sym
      * 
      */
     @JRubyMethod
     public IRubyObject to_sym() {
         RubySymbol symbol = RubySymbol.getSymbolLong(getRuntime(), value);
         
         return symbol != null ? symbol : getRuntime().getNil(); 
     }
 
     /** fix_uminus
      * 
      */
     @JRubyMethod(name = "-@")
     public IRubyObject op_uminus() {
         if (value == MIN) { // a gotcha
             return RubyBignum.newBignum(getRuntime(), BigInteger.valueOf(value).negate());
         }
         return RubyFixnum.newFixnum(getRuntime(), -value);
         }
 
     /** fix_plus
      * 
      */
     @JRubyMethod(name = "+")
     public IRubyObject op_plus(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return addFixnum(context, (RubyFixnum)other);
         }
         return addOther(context, other);
     }
     
     private IRubyObject addFixnum(ThreadContext context, RubyFixnum other) {
         long otherValue = other.value;
         long result = value + otherValue;
         if (additionOverflowed(value, otherValue, result)) {
             return addAsBignum(context, other);
         }
         return newFixnum(context.getRuntime(), result);
     }
     
     private static boolean additionOverflowed(long original, long other, long result) {
         return (~(original ^ other) & (original ^ result) & SIGN_BIT) != 0;
     }
     
     private static boolean subtractionOverflowed(long original, long other, long result) {
         return (~(original ^ ~other) & (original ^ result) & SIGN_BIT) != 0;
     }
     
     private IRubyObject addAsBignum(ThreadContext context, RubyFixnum other) {
         return RubyBignum.newBignum(context.getRuntime(), value).op_plus(context, other);
     }
     
     private IRubyObject addOther(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).op_plus(context, this);
         }
         if (other instanceof RubyFloat) {
             return context.getRuntime().newFloat((double) value + ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin(context, "+", other);
     }
 
     /** fix_minus
      * 
      */
     @JRubyMethod(name = "-")
     public IRubyObject op_minus(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return subtractFixnum(context, (RubyFixnum)other);
         }
         return subtractOther(context, other);
     }
     
     private IRubyObject subtractFixnum(ThreadContext context, RubyFixnum other) {
         long otherValue = other.value;
         long result = value - otherValue;
         if (subtractionOverflowed(value, otherValue, result)) {
             return subtractAsBignum(context, other);
         }
         return newFixnum(context.getRuntime(), result);
     }
     
     private IRubyObject subtractAsBignum(ThreadContext context, RubyFixnum other) {
         return RubyBignum.newBignum(context.getRuntime(), value).op_minus(context, other);
     }
     
     private IRubyObject subtractOther(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyBignum) {
             return RubyBignum.newBignum(context.getRuntime(), value).op_minus(context, other);
         } else if (other instanceof RubyFloat) {
             return context.getRuntime().newFloat((double) value - ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin(context, "-", other);
     }
 
     /** fix_mul
      * 
      */
     @JRubyMethod(name = "*")
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             if (value == 0) {
                 return RubyFixnum.zero(context.getRuntime());
             }
             long result = value * otherValue;
             IRubyObject r = newFixnum(context.getRuntime(),result);
             if(RubyNumeric.fix2long(r) != result || result/value != otherValue) {
                 return (RubyNumeric) RubyBignum.newBignum(context.getRuntime(), value).op_mul(context, other);
             }
             return r;
         } else if (other instanceof RubyBignum) {
             return ((RubyBignum) other).op_mul(context, this);
         } else if (other instanceof RubyFloat) {
             return context.getRuntime().newFloat((double) value * ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin(context, "*", other);
     }
     
     /** fix_div
      * here is terrible MRI gotcha:
      * 1.div 3.0 -> 0
      * 1 / 3.0   -> 0.3333333333333333
      * 
      * MRI is also able to do it in one place by looking at current frame in rb_num_coerce_bin:
      * rb_funcall(x, ruby_frame->orig_func, 1, y);
      * 
      * also note that RubyFloat doesn't override Numeric.div
      */
     @JRubyMethod(name = "div")
     public IRubyObject div_div(ThreadContext context, IRubyObject other) {
         return idiv(context, other, "div");
     }
     	
     @JRubyMethod(name = "/")
     public IRubyObject op_div(ThreadContext context, IRubyObject other) {
         return idiv(context, other, "/");
     }
 
     @JRubyMethod(name = {"odd?"})
     public RubyBoolean odd_p() {
         if(value%2 != 0) {
             return getRuntime().getTrue();
         }
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(name = {"even?"})
     public RubyBoolean even_p() {
         if(value%2 == 0) {
             return getRuntime().getTrue();
         }
         return getRuntime().getFalse();
     }
 
     @JRubyMethod
     public IRubyObject pred() {
         return getRuntime().newFixnum(value-1);
     }
 
     public IRubyObject idiv(ThreadContext context, IRubyObject other, String method) {
         if (other instanceof RubyFixnum) {
             long x = value;
             long y = ((RubyFixnum) other).value;
             
             if (y == 0) {
                 throw context.getRuntime().newZeroDivisionError();
             }
             
             long div = x / y;
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 div -= 1;
             }
 
             return context.getRuntime().newFixnum(div);
         } 
         return coerceBin(context, method, other);
     }
         
     /** fix_mod
      * 
      */
     @JRubyMethod(name = {"%", "modulo"})
     public IRubyObject op_mod(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             // Java / and % are not the same as ruby
             long x = value;
             long y = ((RubyFixnum) other).value;
 
             if (y == 0) {
                 throw context.getRuntime().newZeroDivisionError();
             }
 
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 mod += y;
             }
 
             return context.getRuntime().newFixnum(mod);
         }
         return coerceBin(context, "%", other);
     }
                 
     /** fix_divmod
      * 
      */
     @JRubyMethod
     @Override
     public IRubyObject divmod(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long x = value;
             long y = ((RubyFixnum) other).value;
             final Ruby runtime = context.getRuntime();
 
             if (y == 0) {
                 throw runtime.newZeroDivisionError();
             }
 
             long div = x / y;
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 div -= 1;
                 mod += y;
             }
 
             IRubyObject fixDiv = RubyFixnum.newFixnum(runtime, div);
             IRubyObject fixMod = RubyFixnum.newFixnum(runtime, mod);
 
             return RubyArray.newArray(runtime, fixDiv, fixMod);
 
         }
         return coerceBin(context, "divmod", other);
     }
     	
     /** fix_quo
      * 
      */
     @JRubyMethod
     @Override
     public IRubyObject quo(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyFloat.newFloat(context.getRuntime(), (double) value / (double) ((RubyFixnum) other).value);
         }
         return coerceBin(context, "quo", other);
     }
 
     /** fix_pow 
      * 
      */
     @JRubyMethod(name = "**")
     public IRubyObject op_pow(ThreadContext context, IRubyObject other) {
         if(other instanceof RubyFixnum) {
             long b = ((RubyFixnum) other).value;
             if (b == 0) {
                 return RubyFixnum.one(context.getRuntime());
             }
             if (b == 1) {
                 return this;
             }
             if (b > 0) {
                 return RubyBignum.newBignum(context.getRuntime(), value).op_pow(context, other);
             }
             return RubyFloat.newFloat(context.getRuntime(), Math.pow(value, b));
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(context.getRuntime(), Math.pow(value, ((RubyFloat) other)
                     .getDoubleValue()));
         }
         return coerceBin(context, "**", other);
     }
             
     /** fix_abs
      * 
      */
     @JRubyMethod
     public IRubyObject abs() {
         if (value < 0) {
             // A gotcha for Long.MIN_VALUE: value = -value
             if (value == Long.MIN_VALUE) {
                 return RubyBignum.newBignum(
                         getRuntime(), BigInteger.valueOf(value).negate());
             }
             return RubyFixnum.newFixnum(getRuntime(), -value);
         }
         return this;
     }
             
     /** fix_equal
      * 
      */
     @JRubyMethod(name = "==")
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(context.getRuntime(), value == ((RubyFixnum) other).value);
         }
         return super.op_num_equal(context, other);
     }
 
     /** fix_cmp
      * 
      */
     @JRubyMethod(name = "<=>")
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return compareFixnum(context, (RubyFixnum)other);
         }
         return coerceCmp(context, "<=>", other);
     }
     
     private IRubyObject compareFixnum(ThreadContext context, RubyFixnum other) {
         long otherValue = ((RubyFixnum) other).value;
         if (value == otherValue) {
             return RubyFixnum.zero(context.getRuntime());
         }
         if (value > otherValue) {
             return RubyFixnum.one(context.getRuntime());
         }
         return RubyFixnum.minus_one(context.getRuntime());
     }
 
     /** fix_gt
      * 
      */
     @JRubyMethod(name = ">")
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(context.getRuntime(), value > ((RubyFixnum) other).value);
     }
         return coerceRelOp(context, ">", other);
     }
 
     /** fix_ge
      * 
      */
     @JRubyMethod(name = ">=")
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(context.getRuntime(), value >= ((RubyFixnum) other).value);
             }
         return coerceRelOp(context, ">=", other);
     }
 
     /** fix_lt
      * 
      */
     @JRubyMethod(name = "<")
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(context.getRuntime(), value < ((RubyFixnum) other).value);
         }
         
         return coerceRelOp(context, "<", other);
     }
         
     /** fix_le
      * 
      */
     @JRubyMethod(name = "<=")
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(context.getRuntime(), value <= ((RubyFixnum) other).value);
         }
         
         return coerceRelOp(context, "<=", other);
     }
 
     /** fix_rev
      * 
      */
     @JRubyMethod(name = "~")
     public IRubyObject op_neg() {
         return newFixnum(~value);
     }
     	
     /** fix_and
      * 
      */
     @JRubyMethod(name = "&")
     public IRubyObject op_and(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum || (other = fixCoerce(other)) instanceof RubyFixnum) {
             return newFixnum(context.getRuntime(), value & ((RubyFixnum) other).value);
         }
         return ((RubyBignum) other).op_and(context, this);
     }
 
     /** fix_or 
      * 
      */
     @JRubyMethod(name = "|")
     public IRubyObject op_or(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum || (other = fixCoerce(other)) instanceof RubyFixnum) {
             return newFixnum(context.getRuntime(), value | ((RubyFixnum) other).value);
         }
         return ((RubyBignum) other).op_or(context, this);
     }
 
     /** fix_xor 
      * 
      */
     @JRubyMethod(name = "^")
     public IRubyObject op_xor(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum || (other = fixCoerce(other)) instanceof RubyFixnum) {
             return newFixnum(context.getRuntime(), value ^ ((RubyFixnum) other).value);
         }
         return ((RubyBignum) other).op_xor(context, this); 
     }
 
     /** fix_aref 
      * 
      */
     @JRubyMethod(name = "[]")
     public IRubyObject op_aref(IRubyObject other) {
         if(!(other instanceof RubyFixnum) && !((other = fixCoerce(other)) instanceof RubyFixnum)) {
             RubyBignum big = (RubyBignum) other;
             RubyObject tryFix = RubyBignum.bignorm(getRuntime(), big.getValue());
             if (!(tryFix instanceof RubyFixnum)) {
                 return big.getValue().signum() == 0 || value >= 0 ? RubyFixnum.zero(getRuntime()) : RubyFixnum.one(getRuntime());
             }
         }
 
         long otherValue = fix2long(other);
 
         if (otherValue < 0) return RubyFixnum.zero(getRuntime());
 
         if (BIT_SIZE - 1 < otherValue) {
             return value < 0 ? RubyFixnum.one(getRuntime()) : RubyFixnum.zero(getRuntime());
         }
 
         return (value & (1L << otherValue)) == 0 ? RubyFixnum.zero(getRuntime()) : RubyFixnum.one(getRuntime());
     }
 
     /** fix_lshift 
      * 
      */
     @JRubyMethod(name = "<<")
     public IRubyObject op_lshift(IRubyObject other) {
         if (!(other instanceof RubyFixnum)) return RubyBignum.newBignum(getRuntime(), value).op_lshift(other);
 
         long width = ((RubyFixnum)other).getLongValue();
 
         return width < 0 ? rshift(-width) : lshift(width); 
     }
     
     private IRubyObject lshift(long width) {
         if (width > BIT_SIZE - 1 || ((~0L << BIT_SIZE - width - 1) & value) != 0) {
             return RubyBignum.newBignum(getRuntime(), value).op_lshift(RubyFixnum.newFixnum(getRuntime(), width));
         }
         return RubyFixnum.newFixnum(getRuntime(), value << width);
     }
 
     /** fix_rshift 
      * 
      */
     @JRubyMethod(name = ">>")
     public IRubyObject op_rshift(IRubyObject other) {
         if (!(other instanceof RubyFixnum)) return RubyBignum.newBignum(getRuntime(), value).op_rshift(other);
 
         long width = ((RubyFixnum)other).getLongValue();
 
         if (width == 0) return this;
 
         return width < 0 ? lshift(-width) : rshift(width);  
     }
     
     private IRubyObject rshift(long width) { 
         if (width >= BIT_SIZE - 1) {
             return value < 0 ? RubyFixnum.minus_one(getRuntime()) : RubyFixnum.zero(getRuntime()); 
         }
         return RubyFixnum.newFixnum(getRuntime(), value >> width);
     }
 
     /** fix_to_f 
      * 
      */
     @JRubyMethod
     public IRubyObject to_f() {
         return RubyFloat.newFloat(getRuntime(), (double) value);
     }
 
     /** fix_size 
      * 
      */
     @JRubyMethod
     public IRubyObject size() {
         return newFixnum((long) ((BIT_SIZE + 7) / 8));
     }
 
     /** fix_zero_p 
      * 
      */
     @JRubyMethod(name = "zero?")
     public IRubyObject zero_p() {
         return RubyBoolean.newBoolean(getRuntime(), value == 0);
     }
 
     @JRubyMethod
     @Override
     public IRubyObject id() {
         if (value <= Long.MAX_VALUE / 2 && value >= Long.MIN_VALUE / 2) {
             return newFixnum(2 * value + 1);
         }
         
         return super.id();
     }
 
     @Override
     public IRubyObject taint(ThreadContext context) {
         return this;
     }
 
     @Override
     public IRubyObject freeze(ThreadContext context) {
         return this;
     }
     
     // Piece of mri rb_to_id
     @Override
     public String asJavaString() {
         getRuntime().getWarnings().warn(ID.FIXNUMS_NOT_SYMBOLS, "do not use Fixnums as Symbols");
         
         // FIXME: I think this chunk is equivalent to MRI id2name (and not our public method 
         // id2name).  Make into method if used more than once.  
         RubySymbol symbol = RubySymbol.getSymbolLong(getRuntime(), value);
         
         if (symbol == null) {
             throw getRuntime().newArgumentError("" + value + " is not a symbol");
         }
         
         return symbol.asJavaString();
     }
 
     public static RubyFixnum unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         return input.getRuntime().newFixnum(input.unmarshalInt());
     }
 
     /*  ================
      *  Singleton Methods
      *  ================ 
      */
 
     /** rb_fix_induced_from
      * 
      */
     @JRubyMethod(meta = true)
     public static IRubyObject induced_from(IRubyObject recv, IRubyObject other) {
         return RubyNumeric.num2fix(other);
     }
 
     @Override
     public IRubyObject to_java() {
         return MiniJava.javaToRuby(getRuntime(), Long.valueOf(value));
     }
 
     @Override
     public IRubyObject as(Class javaClass) {
         return MiniJava.javaToRuby(getRuntime(), coerceToJavaType(getRuntime(), this, javaClass));
     }
     
     private static Object coerceToJavaType(Ruby ruby, RubyFixnum self, Class javaClass) {
         if (!Number.class.isAssignableFrom(javaClass)) {
             throw ruby.newTypeError(javaClass.getCanonicalName() + " is not a numeric type");
         }
         
         TypeCoercer coercer = JAVA_COERCERS.get(javaClass);
         
         if (coercer == null) {
             throw ruby.newTypeError("Cannot coerce Fixnum to " + javaClass.getCanonicalName());
         }
         
         return coercer.coerce(self);
     }
     
     private static final Map<Class, TypeCoercer> JAVA_COERCERS = new HashMap<Class, TypeCoercer>();
     
     static {
         TypeCoercer intCoercer = new TypeCoercer() {
             public Object coerce(IRubyObject self) {
                 RubyFixnum fixnum = (RubyFixnum)self;
                 
                 if (fixnum.value > Integer.MAX_VALUE) {
                     throw self.getRuntime().newRangeError("Fixnum " + fixnum.value + " is too large for Java int");
                 }
                 
                 return Integer.valueOf((int)fixnum.value);
             }
         };
         JAVA_COERCERS.put(int.class, intCoercer);
         JAVA_COERCERS.put(Integer.class, intCoercer);
     }
 }
diff --git a/src/org/jruby/RubyFloat.java b/src/org/jruby/RubyFloat.java
index c9e7d50246..626e94d097 100644
--- a/src/org/jruby/RubyFloat.java
+++ b/src/org/jruby/RubyFloat.java
@@ -1,589 +1,592 @@
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
  * Copyright (C) 2002 Don Schwartz <schwardo@users.sourceforge.net>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.text.DecimalFormat;
 import java.text.DecimalFormatSymbols;
 import java.util.Locale;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /**
   * A representation of a float object
  */
 @JRubyClass(name="Float", parent="Numeric", include="Precision")
 public class RubyFloat extends RubyNumeric {
 
     public static RubyClass createFloatClass(Ruby runtime) {
         RubyClass floatc = runtime.defineClass("Float", runtime.getNumeric(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setFloat(floatc);
         floatc.index = ClassIndex.FLOAT;
         floatc.kindOf = new RubyModule.KindOf() {
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyFloat;
             }
         };        
         
         floatc.getSingletonClass().undefineMethod("new");
         floatc.includeModule(runtime.getPrecision());
 
         // Java Doubles are 64 bit long:            
         floatc.defineConstant("ROUNDS", RubyFixnum.newFixnum(runtime, 1));
         floatc.defineConstant("RADIX", RubyFixnum.newFixnum(runtime, 2));
         floatc.defineConstant("MANT_DIG", RubyFixnum.newFixnum(runtime, 53));
         floatc.defineConstant("DIG", RubyFixnum.newFixnum(runtime, 15));
         // Double.MAX_EXPONENT since Java 1.6
         floatc.defineConstant("MIN_EXP", RubyFixnum.newFixnum(runtime, -1021));
         // Double.MAX_EXPONENT since Java 1.6            
         floatc.defineConstant("MAX_EXP", RubyFixnum.newFixnum(runtime, 1024));
         floatc.defineConstant("MIN_10_EXP", RubyFixnum.newFixnum(runtime, -307));
         floatc.defineConstant("MAX_10_EXP", RubyFixnum.newFixnum(runtime, 308));
         floatc.defineConstant("MIN", RubyFloat.newFloat(runtime, Double.MIN_VALUE));
         floatc.defineConstant("MAX", RubyFloat.newFloat(runtime, Double.MAX_VALUE));
         floatc.defineConstant("EPSILON", RubyFloat.newFloat(runtime, 2.2204460492503131e-16));
         
         floatc.defineAnnotatedMethods(RubyFloat.class);
 
         return floatc;
     }
 
     private final double value;
     
     public int getNativeTypeIndex() {
         return ClassIndex.FLOAT;
     }
 
     public RubyFloat(Ruby runtime) {
         this(runtime, 0.0);
     }
 
     public RubyFloat(Ruby runtime, double value) {
         super(runtime, runtime.getFloat());
         this.value = value;
     }
 
     public Class<?> getJavaClass() {
+        if (value >= Float.MIN_VALUE && value <= Float.MAX_VALUE) {
+            return float.class;
+        }
         return double.class;
     }
 
     /** Getter for property value.
      * @return Value of property value.
      */
     public double getValue() {
         return this.value;
     }
 
     public double getDoubleValue() {
         return value;
     }
 
     public long getLongValue() {
         return (long) value;
     }
     
     public RubyFloat convertToFloat() {
     	return this;
     }
 
     protected int compareValue(RubyNumeric other) {
         double otherVal = other.getDoubleValue();
         return getValue() > otherVal ? 1 : getValue() < otherVal ? -1 : 0;
     }
 
     public static RubyFloat newFloat(Ruby runtime, double value) {
         return new RubyFloat(runtime, value);
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** rb_flo_induced_from
      * 
      */
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject induced_from(ThreadContext context, IRubyObject recv, IRubyObject number) {
         if (number instanceof RubyFixnum || number instanceof RubyBignum) {
             return number.callMethod(context, MethodIndex.TO_F, "to_f");
         }
         if (number instanceof RubyFloat) {
             return number;
         }
         throw recv.getRuntime().newTypeError(
                 "failed to convert " + number.getMetaClass() + " into Float");
     }
 
     private final static DecimalFormat FORMAT = new DecimalFormat("##############0.0##############",
             new DecimalFormatSymbols(Locale.ENGLISH));
 
     /** flo_to_s
      * 
      */
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
         if (Double.isInfinite(value)) {
             return RubyString.newString(getRuntime(), value < 0 ? "-Infinity" : "Infinity");
         }
 
         if (Double.isNaN(value)) {
             return RubyString.newString(getRuntime(), "NaN");
         }
 
         String val = ""+value;
 
         if(val.indexOf('E') != -1) {
             String v2 = FORMAT.format(value);
             int ix = v2.length()-1;
             while(v2.charAt(ix) == '0' && v2.charAt(ix-1) != '.') {
                 ix--;
             }
             if(ix > 15 || "0.0".equals(v2.substring(0,ix+1))) {
                 val = val.replaceFirst("E(\\d)","e+$1").replaceFirst("E-","e-");
             } else {
                 val = v2.substring(0,ix+1);
             }
         }
 
         return RubyString.newString(getRuntime(), val);
     }
 
     /** flo_coerce
      * 
      */
     @JRubyMethod(name = "coerce", required = 1)
     public IRubyObject coerce(IRubyObject other) {
         return getRuntime().newArray(RubyKernel.new_float(this, other), this);
     }
 
     /** flo_uminus
      * 
      */
     @JRubyMethod(name = "-@")
     public IRubyObject op_uminus() {
         return RubyFloat.newFloat(getRuntime(), -value);
     }
 
     /** flo_plus
      * 
      */
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyFloat.newFloat(getRuntime(), value + ((RubyNumeric) other).getDoubleValue());
         default:
             return coerceBin(context, "+", other);
         }
     }
 
     /** flo_minus
      * 
      */
     @JRubyMethod(name = "-", required = 1)
     public IRubyObject op_minus(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyFloat.newFloat(getRuntime(), value - ((RubyNumeric) other).getDoubleValue());
         default:
             return coerceBin(context, "-", other);
         }
     }
 
     /** flo_mul
      * 
      */
     @JRubyMethod(name = "*", required = 1)
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyFloat.newFloat(
                     getRuntime(), value * ((RubyNumeric) other).getDoubleValue());
         default:
             return coerceBin(context, "*", other);
         }
     }
     
     /** flo_div
      * 
      */
     @JRubyMethod(name = "/", required = 1)
     public IRubyObject op_fdiv(ThreadContext context, IRubyObject other) { // don't override Numeric#div !
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyFloat.newFloat(getRuntime(), value / ((RubyNumeric) other).getDoubleValue());
         default:
             return coerceBin(context, "/", other);
         }
     }
 
     /** flo_mod
      * 
      */
     @JRubyMethod(name = {"%", "modulo"}, required = 1)
     public IRubyObject op_mod(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double y = ((RubyNumeric) other).getDoubleValue();
             // Modelled after c ruby implementation (java /,% not same as ruby)
             double x = value;
 
             double mod = Math.IEEEremainder(x, y);
             if (y * mod < 0) {
                 mod += y;
             }
 
             return RubyFloat.newFloat(getRuntime(), mod);
         default:
             return coerceBin(context, "%", other);
         }
     }
 
     /** flo_divmod
      * 
      */
     @JRubyMethod(name = "divmod", required = 1)
     public IRubyObject divmod(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double y = ((RubyNumeric) other).getDoubleValue();
             double x = value;
 
             double mod = Math.IEEEremainder(x, y);
             // MRI behavior:
             if (Double.isNaN(mod)) {
                 throw getRuntime().newFloatDomainError("NaN");
             }
             double div = Math.floor(x / y);
 
             if (y * mod < 0) {
                 mod += y;
             }
             final Ruby runtime = getRuntime();
             IRubyObject car = dbl2num(runtime, div);
             RubyFloat cdr = RubyFloat.newFloat(runtime, mod);
             return RubyArray.newArray(runtime, car, cdr);
         default:
             return coerceBin(context, "divmod", other);
         }
     }
     	
     /** flo_pow
      * 
      */
     @JRubyMethod(name = "**", required = 1)
     public IRubyObject op_pow(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyFloat.newFloat(getRuntime(), Math.pow(value, ((RubyNumeric) other)
                     .getDoubleValue()));
         default:
             return coerceBin(context, "**", other);
         }
     }
 
     /** flo_eq
      * 
      */
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         if (Double.isNaN(value)) {
             return getRuntime().getFalse();
         }
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyBoolean.newBoolean(getRuntime(), value == ((RubyNumeric) other)
                     .getDoubleValue());
         default:
             // Numeric.equal            
             return super.op_num_equal(context, other);
         }
     }
 
     /** flo_cmp
      * 
      */
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double b = ((RubyNumeric) other).getDoubleValue();
             return dbl_cmp(getRuntime(), value, b);
         default:
             return coerceCmp(context, "<=>", other);
         }
     }
 
     /** flo_gt
      * 
      */
     @JRubyMethod(name = ">", required = 1)
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double b = ((RubyNumeric) other).getDoubleValue();
             return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value > b);
         default:
             return coerceRelOp(context, ">", other);
         }
     }
 
     /** flo_ge
      * 
      */
     @JRubyMethod(name = ">=", required = 1)
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double b = ((RubyNumeric) other).getDoubleValue();
             return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value >= b);
         default:
             return coerceRelOp(context, ">=", other);
         }
     }
 
     /** flo_lt
      * 
      */
     @JRubyMethod(name = "<", required = 1)
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double b = ((RubyNumeric) other).getDoubleValue();
             return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value < b);
         default:
             return coerceRelOp(context, "<", other);
 		}
     }
 
     /** flo_le
      * 
      */
     @JRubyMethod(name = "<=", required = 1)
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double b = ((RubyNumeric) other).getDoubleValue();
             return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value <= b);
         default:
             return coerceRelOp(context, "<=", other);
 		}
 	}
 	
     /** flo_eql
      * 
      */
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql_p(IRubyObject other) {
         if (other instanceof RubyFloat) {
             double b = ((RubyFloat) other).value;
             if (Double.isNaN(value) || Double.isNaN(b)) {
                 return getRuntime().getFalse();
             }
             if (value == b) {
                 return getRuntime().getTrue();
             }
         }
         return getRuntime().getFalse();
     }
 
     /** flo_hash
      * 
      */
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
 
     public final int hashCode() {
         long l = Double.doubleToLongBits(value);
         return (int)(l ^ l >>> 32);
     }    
 
     /** flo_fo 
      * 
      */
     @JRubyMethod(name = "to_f")
     public IRubyObject to_f() {
         return this;
     }
         
     /** flo_abs
      * 
      */
     @JRubyMethod(name = "abs")
     public IRubyObject abs() {
         if (value < 0) {
             return RubyFloat.newFloat(getRuntime(), Math.abs(value));
         }
         return this;
     }
     
     /** flo_zero_p
      * 
      */
     @JRubyMethod(name = "zero?")
     public IRubyObject zero_p() {
         return RubyBoolean.newBoolean(getRuntime(), value == 0.0);
     }
 
     /** flo_truncate
      * 
      */
     @JRubyMethod(name = {"truncate", "to_i", "to_int"})
     public IRubyObject truncate() {
         double f = value;
         if (f > 0.0) f = Math.floor(f);
         if (f < 0.0) f = Math.ceil(f);
 
         return dbl2num(getRuntime(), f);
     }
         
     /** floor
      * 
      */
     @JRubyMethod(name = "floor")
     public IRubyObject floor() {
         return dbl2num(getRuntime(), Math.floor(value));
     }
 
     /** flo_ceil
      * 
      */
     @JRubyMethod(name = "ceil")
     public IRubyObject ceil() {
         return dbl2num(getRuntime(), Math.ceil(value));
     }
 
     /** flo_round
      * 
      */
     @JRubyMethod(name = "round")
     public IRubyObject round() {
         double f = value;
         if (f > 0.0) {
             f = Math.floor(f + 0.5);
         }
         if (f < 0.0) {
             f = Math.ceil(f - 0.5);
         }
         return dbl2num(getRuntime(), f);
     }
         
     /** flo_is_nan_p
      * 
      */
     @JRubyMethod(name = "nan?")
     public IRubyObject nan_p() {
         return RubyBoolean.newBoolean(getRuntime(), Double.isNaN(value));
     }
 
     /** flo_is_infinite_p
      * 
      */
     @JRubyMethod(name = "infinite?")
     public IRubyObject infinite_p() {
         if (Double.isInfinite(value)) {
             return RubyFixnum.newFixnum(getRuntime(), value < 0 ? -1 : 1);
         }
         return getRuntime().getNil();
     }
             
     /** flo_is_finite_p
      * 
      */
     @JRubyMethod(name = "finite?")
     public IRubyObject finite_p() {
         if (Double.isInfinite(value) || Double.isNaN(value)) {
             return getRuntime().getFalse();
         }
         return getRuntime().getTrue();
     }
 
     public static void marshalTo(RubyFloat aFloat, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(aFloat);
 
         String strValue = aFloat.toString();
     
         if (Double.isInfinite(aFloat.value)) {
             strValue = aFloat.value < 0 ? "-inf" : "inf";
         } else if (Double.isNaN(aFloat.value)) {
             strValue = "nan";
         }
         output.writeString(strValue);
     }
         
     public static RubyFloat unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyFloat result = RubyFloat.newFloat(input.getRuntime(), org.jruby.util.Convert.byteListToDouble(input.unmarshalString(),false));
         input.registerLinkTarget(result);
         return result;
     }
 }
diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index 38ef8d4bd3..3f4f2ee15e 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -38,1333 +38,1347 @@ import java.lang.reflect.Constructor;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.Proxy;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyClassPathVariable;
 import org.jruby.RubyException;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.util.ClassProvider;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.java.MiniJava;
 import org.jruby.runtime.callback.Callback;
 
 @JRubyModule(name = "Java")
 public class Java implements Library {
 
     public void load(Ruby runtime, boolean wrap) throws IOException {
         createJavaModule(runtime);
         runtime.getLoadService().smartLoad("builtin/javasupport");
         RubyClassPathVariable.createClassPathVariable(runtime);
     }
 
     public static RubyModule createJavaModule(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         RubyModule javaModule = runtime.defineModule("Java");
         
         javaModule.defineAnnotatedMethods(Java.class);
 
         JavaObject.createJavaObjectClass(runtime, javaModule);
         JavaArray.createJavaArrayClass(runtime, javaModule);
         JavaClass.createJavaClassClass(runtime, javaModule);
         JavaMethod.createJavaMethodClass(runtime, javaModule);
         JavaConstructor.createJavaConstructorClass(runtime, javaModule);
         JavaField.createJavaFieldClass(runtime, javaModule);
         
         // set of utility methods for Java-based proxy objects
         JavaProxyMethods.createJavaProxyMethods(context);
         
         // the proxy (wrapper) type hierarchy
         JavaProxy.createJavaProxy(context);
         ConcreteJavaProxy.createConcreteJavaProxy(context);
 
         // also create the JavaProxy* classes
         JavaProxyClass.createJavaProxyModule(runtime);
 
         RubyModule javaUtils = runtime.defineModule("JavaUtilities");
         
         javaUtils.defineAnnotatedMethods(JavaUtilities.class);
 
         runtime.getJavaSupport().setConcreteProxyCallback(new Callback() {
             public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
                 Arity.checkArgumentCount(recv.getRuntime(), args, 1, 1);
                 
                 return Java.concrete_proxy_inherited(recv, args[0]);
             }
 
             public Arity getArity() {
                 return Arity.ONE_ARGUMENT;
             }
         });
 
         JavaArrayUtilities.createJavaArrayUtilitiesModule(runtime);
 
         RubyClass javaProxy = runtime.defineClass("JavaProxy", runtime.getObject(), runtime.getObject().getAllocator());
         javaProxy.defineAnnotatedMethods(JavaProxy.class);
         
         // Now attach Java-related extras to core classes
         runtime.getArray().defineAnnotatedMethods(ArrayJavaAddons.class);
 
         return javaModule;
     }
 
     @JRubyModule(name = "JavaUtilities")
     public static class JavaUtilities {
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject wrap(IRubyObject recv, IRubyObject arg0) {
             return Java.wrap(recv, arg0);
         }
         
         @JRubyMethod(name = "valid_constant_name?", module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject valid_constant_name_p(IRubyObject recv, IRubyObject arg0) {
             return Java.valid_constant_name_p(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject primitive_match(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return Java.primitive_match(recv, arg0, arg1);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject access(IRubyObject recv, IRubyObject arg0) {
             return Java.access(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject matching_method(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return Java.matching_method(recv, arg0, arg1);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject set_java_object(IRubyObject recv, IRubyObject self, IRubyObject java_object) {
             self.getInstanceVariables().fastSetInstanceVariable("@java_object", java_object);
             self.dataWrapStruct(java_object);
             return java_object;
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_deprecated_interface_proxy(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
             return Java.get_deprecated_interface_proxy(context, recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_interface_module(IRubyObject recv, IRubyObject arg0) {
             return Java.get_interface_module(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_package_module(IRubyObject recv, IRubyObject arg0) {
             return Java.get_package_module(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_package_module_dot_format(IRubyObject recv, IRubyObject arg0) {
             return Java.get_package_module_dot_format(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject arg0) {
             return Java.get_proxy_class(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject is_primitive_type(IRubyObject recv, IRubyObject arg0) {
             return Java.is_primitive_type(recv, arg0);
         }
 
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject create_proxy_class(IRubyObject recv, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             return Java.create_proxy_class(recv, arg0, arg1, arg2);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_java_class(IRubyObject recv, IRubyObject arg0) {
             return Java.get_java_class(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_top_level_proxy_or_package(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
             return Java.get_top_level_proxy_or_package(context, recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_proxy_or_package_under_package(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return Java.get_proxy_or_package_under_package(context, recv, arg0, arg1);
         }
         
         @Deprecated
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject add_proxy_extender(IRubyObject recv, IRubyObject arg0) {
             return Java.add_proxy_extender(recv, arg0);
         }
     }
 
     private static final ClassProvider JAVA_PACKAGE_CLASS_PROVIDER = new ClassProvider() {
 
         public RubyClass defineClassUnder(RubyModule pkg, String name, RubyClass superClazz) {
             // shouldn't happen, but if a superclass is specified, it's not ours
             if (superClazz != null) {
                 return null;
             }
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) {
                 return null;
             }
             Ruby runtime = pkg.getRuntime();
             return (RubyClass) get_proxy_class(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
 
         public RubyModule defineModuleUnder(RubyModule pkg, String name) {
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) {
                 return null;
             }
             Ruby runtime = pkg.getRuntime();
             return (RubyModule) get_interface_module(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
     };
     private static final Map<String, Boolean> JAVA_PRIMITIVES = new HashMap<String, Boolean>();
     
 
     static {
         String[] primitives = {"boolean", "byte", "char", "short", "int", "long", "float", "double"};
         for (String primitive : primitives) {
             JAVA_PRIMITIVES.put(primitive, Boolean.TRUE);
         }
     }
 
     public static IRubyObject is_primitive_type(IRubyObject recv, IRubyObject sym) {
         return recv.getRuntime().newBoolean(JAVA_PRIMITIVES.containsKey(sym.asJavaString()));
     }
 
     public static IRubyObject create_proxy_class(
             IRubyObject recv,
             IRubyObject constant,
             IRubyObject javaClass,
             IRubyObject module) {
         if (!(module instanceof RubyModule)) {
             throw recv.getRuntime().newTypeError(module, recv.getRuntime().getModule());
         }
         return ((RubyModule) module).const_set(constant, get_proxy_class(recv, javaClass));
     }
 
     public static IRubyObject get_java_class(IRubyObject recv, IRubyObject name) {
         try {
             return JavaClass.for_name(recv, name);
         } catch (Exception e) {
             return recv.getRuntime().getNil();
         }
     }
 
     /**
      * Returns a new proxy instance of type (RubyClass)recv for the wrapped java_object,
      * or the cached proxy if we've already seen this object.
      * 
      * @param recv the class for this object
      * @param java_object the java object wrapped in a JavaObject wrapper
      * @return the new or cached proxy for the specified Java object
      */
     public static IRubyObject new_instance_for(IRubyObject recv, IRubyObject java_object) {
         // FIXME: note temporary double-allocation of JavaObject as we move to cleaner interface
         if (java_object instanceof JavaObject) {
             return getInstance(((JavaObject) java_object).getValue(), (RubyClass) recv);
         }
         // in theory we should never get here, keeping around temporarily
         IRubyObject new_instance = ((RubyClass) recv).allocate();
         new_instance.getInstanceVariables().fastSetInstanceVariable("@java_object", java_object);
         new_instance.dataWrapStruct(java_object);
         return new_instance;
     }
 
     /**
      * Returns a new proxy instance of type clazz for rawJavaObject, or the cached
      * proxy if we've already seen this object.
      * 
      * @param rawJavaObject
      * @param clazz
      * @return the new or cached proxy for the specified Java object
      */
     public static IRubyObject getInstance(Object rawJavaObject, RubyClass clazz) {
         return clazz.getRuntime().getJavaSupport().getObjectProxyCache().getOrCreate(rawJavaObject, clazz);
     }
 
     /**
      * Returns a new proxy instance of a type corresponding to rawJavaObject's class,
      * or the cached proxy if we've already seen this object.  Note that primitives
      * and strings are <em>not</em> coerced to corresponding Ruby types; use
      * JavaUtil.convertJavaToUsableRubyObject to get coerced types or proxies as
      * appropriate.
      * 
      * @param runtime
      * @param rawJavaObject
      * @return the new or cached proxy for the specified Java object
      * @see JavaUtil.convertJavaToUsableRubyObject
      */
     public static IRubyObject getInstance(Ruby runtime, Object rawJavaObject) {
         if (rawJavaObject != null) {
             return runtime.getJavaSupport().getObjectProxyCache().getOrCreate(rawJavaObject,
                     (RubyClass) getProxyClass(runtime, JavaClass.get(runtime, rawJavaObject.getClass())));
         }
         return runtime.getNil();
     }
 
     // If the proxy class itself is passed as a parameter this will be called by Java#ruby_to_java    
     public static IRubyObject to_java_object(IRubyObject recv) {
         return recv.getInstanceVariables().fastGetInstanceVariable("@java_class");
     }
 
     // JavaUtilities
     /**
      * Add a new proxy extender. This is used by JavaUtilities to allow adding methods
      * to a given type's proxy and all types descending from that proxy's Java class.
      */
     @Deprecated
     public static IRubyObject add_proxy_extender(IRubyObject recv, IRubyObject extender) {
         // hacky workaround in case any users call this directly.
         // most will have called JavaUtilities.extend_proxy instead.
         recv.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "JavaUtilities.add_proxy_extender is deprecated - use JavaUtilities.extend_proxy instead", "add_proxy_extender", "JavaUtilities.extend_proxy");
         IRubyObject javaClassVar = extender.getInstanceVariables().fastGetInstanceVariable("@java_class");
         if (!(javaClassVar instanceof JavaClass)) {
             throw recv.getRuntime().newArgumentError("extender does not have a valid @java_class");
         }
         ((JavaClass) javaClassVar).addProxyExtender(extender);
         return recv.getRuntime().getNil();
     }
 
     public static RubyModule getInterfaceModule(Ruby runtime, JavaClass javaClass) {
         if (!javaClass.javaClass().isInterface()) {
             throw runtime.newArgumentError(javaClass.toString() + " is not an interface");
         }
         RubyModule interfaceModule;
         if ((interfaceModule = javaClass.getProxyModule()) != null) {
             return interfaceModule;
         }
         javaClass.lockProxy();
         try {
             if ((interfaceModule = javaClass.getProxyModule()) == null) {
                 interfaceModule = (RubyModule) runtime.getJavaSupport().getJavaInterfaceTemplate().dup();
                 interfaceModule.fastSetInstanceVariable("@java_class", javaClass);
                 addToJavaPackageModule(interfaceModule, javaClass);
                 javaClass.setupInterfaceModule(interfaceModule);
                 // include any interfaces we extend
                 Class<?>[] extended = javaClass.javaClass().getInterfaces();
                 for (int i = extended.length; --i >= 0;) {
                     JavaClass extendedClass = JavaClass.get(runtime, extended[i]);
                     RubyModule extModule = getInterfaceModule(runtime, extendedClass);
                     interfaceModule.includeModule(extModule);
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return interfaceModule;
     }
 
     public static IRubyObject get_interface_module(IRubyObject recv, IRubyObject javaClassObject) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, javaClassObject);
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass) javaClassObject;
         } else {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         return getInterfaceModule(runtime, javaClass);
     }
 
     // Note: this isn't really all that deprecated, as it is used for
     // internal purposes, at least for now. But users should be discouraged
     // from calling this directly; eventually it will go away.
     public static IRubyObject get_deprecated_interface_proxy(ThreadContext context, IRubyObject recv, IRubyObject javaClassObject) {
         Ruby runtime = context.getRuntime();
         JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, javaClassObject);
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass) javaClassObject;
         } else {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         if (!javaClass.javaClass().isInterface()) {
             throw runtime.newArgumentError("expected Java interface class, got " + javaClassObject);
         }
         RubyClass proxyClass;
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         javaClass.lockProxy();
         try {
             if ((proxyClass = javaClass.getProxyClass()) == null) {
                 RubyModule interfaceModule = getInterfaceModule(runtime, javaClass);
                 RubyClass interfaceJavaProxy = runtime.fastGetClass("InterfaceJavaProxy");
                 proxyClass = RubyClass.newClass(runtime, interfaceJavaProxy);
                 proxyClass.setAllocator(interfaceJavaProxy.getAllocator());
                 proxyClass.makeMetaClass(interfaceJavaProxy.getMetaClass());
                 // parent.setConstant(name, proxyClass); // where the name should come from ?
                 proxyClass.inherit(interfaceJavaProxy);
                 proxyClass.callMethod(context, "java_class=", javaClass);
                 // including interface module so old-style interface "subclasses" will
                 // respond correctly to #kind_of?, etc.
                 proxyClass.includeModule(interfaceModule);
                 javaClass.setupProxy(proxyClass);
                 // add reference to interface module
                 if (proxyClass.fastGetConstantAt("Includable") == null) {
                     proxyClass.fastSetConstant("Includable", interfaceModule);
                 }
 
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
 
     public static RubyModule getProxyClass(Ruby runtime, JavaClass javaClass) {
         RubyClass proxyClass;
         Class<?> c;
         if ((c = javaClass.javaClass()).isInterface()) {
             return getInterfaceModule(runtime, javaClass);
         }
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         javaClass.lockProxy();
         try {
             if ((proxyClass = javaClass.getProxyClass()) == null) {
 
                 if (c.isArray()) {
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getArrayProxyClass(),
                             javaClass, true);
 
                 } else if (c.isPrimitive()) {
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
 
                 } else if (c == Object.class) {
                     // java.lang.Object is added at root of java proxy classes
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
                     proxyClass.getMetaClass().defineFastMethod("inherited",
                             runtime.getJavaSupport().getConcreteProxyCallback());
                     addToJavaPackageModule(proxyClass, javaClass);
 
                 } else {
                     // other java proxy classes added under their superclass' java proxy
                     proxyClass = createProxyClass(runtime,
                             (RubyClass) getProxyClass(runtime, JavaClass.get(runtime, c.getSuperclass())),
                             javaClass, false);
 
                     // include interface modules into the proxy class
                     Class<?>[] interfaces = c.getInterfaces();
                     for (int i = interfaces.length; --i >= 0;) {
                         JavaClass ifc = JavaClass.get(runtime, interfaces[i]);
                         proxyClass.includeModule(getInterfaceModule(runtime, ifc));
                     }
                     if (Modifier.isPublic(c.getModifiers())) {
                         addToJavaPackageModule(proxyClass, javaClass);
                     }
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
 
     public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject java_class_object) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
         if (java_class_object instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, java_class_object);
         } else if (java_class_object instanceof JavaClass) {
             javaClass = (JavaClass) java_class_object;
         } else {
             throw runtime.newTypeError(java_class_object, runtime.getJavaSupport().getJavaClassClass());
         }
         return getProxyClass(runtime, javaClass);
     }
 
     private static RubyClass createProxyClass(Ruby runtime, RubyClass baseType,
             JavaClass javaClass, boolean invokeInherited) {
         // this needs to be split, since conditional calling #inherited doesn't fit standard ruby semantics
         RubyClass.checkInheritable(baseType);
         RubyClass superClass = (RubyClass) baseType;
         RubyClass proxyClass = RubyClass.newClass(runtime, superClass);
         proxyClass.makeMetaClass(superClass.getMetaClass());
         proxyClass.setAllocator(superClass.getAllocator());
         if (invokeInherited) {
             proxyClass.inherit(superClass);
         }
         proxyClass.callMethod(runtime.getCurrentContext(), "java_class=", javaClass);
         javaClass.setupProxy(proxyClass);
         return proxyClass;
     }
 
     public static IRubyObject concrete_proxy_inherited(IRubyObject recv, IRubyObject subclass) {
         Ruby runtime = recv.getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         JavaSupport javaSupport = runtime.getJavaSupport();
         RubyClass javaProxyClass = javaSupport.getJavaProxyClass().getMetaClass();
         RuntimeHelpers.invokeAs(tc, javaProxyClass, recv, "inherited", new IRubyObject[]{subclass},
                 org.jruby.runtime.CallType.SUPER, Block.NULL_BLOCK);
         // TODO: move to Java
         return javaSupport.getJavaUtilitiesModule().callMethod(tc, "setup_java_subclass",
                 new IRubyObject[]{subclass, recv.callMethod(tc, "java_class")});
     }
 
     // package scheme 2: separate module for each full package name, constructed 
     // from the camel-cased package segments: Java::JavaLang::Object, 
     private static void addToJavaPackageModule(RubyModule proxyClass, JavaClass javaClass) {
         Class<?> clazz = javaClass.javaClass();
         String fullName;
         if ((fullName = clazz.getName()) == null) {
             return;
         }
         int endPackage = fullName.lastIndexOf('.');
         // we'll only map conventional class names to modules 
         if (fullName.indexOf('$') != -1 || !Character.isUpperCase(fullName.charAt(endPackage + 1))) {
             return;
         }
         Ruby runtime = proxyClass.getRuntime();
         String packageString = endPackage < 0 ? "" : fullName.substring(0, endPackage);
         RubyModule packageModule = getJavaPackageModule(runtime, packageString);
         if (packageModule != null) {
             String className = fullName.substring(endPackage + 1);
             if (packageModule.getConstantAt(className) == null) {
                 packageModule.const_set(runtime.newSymbol(className), proxyClass);
             }
         }
     }
 
     private static RubyModule getJavaPackageModule(Ruby runtime, String packageString) {
         String packageName;
         int length = packageString.length();
         if (length == 0) {
             packageName = "Default";
         } else {
             StringBuilder buf = new StringBuilder();
             for (int start = 0, offset = 0; start < length; start = offset + 1) {
                 if ((offset = packageString.indexOf('.', start)) == -1) {
                     offset = length;
                 }
                 buf.append(Character.toUpperCase(packageString.charAt(start))).append(packageString.substring(start + 1, offset));
             }
             packageName = buf.toString();
         }
 
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject packageModule = javaModule.getConstantAt(packageName);
         if (packageModule == null) {
             return createPackageModule(javaModule, packageName, packageString);
         } else if (packageModule instanceof RubyModule) {
             return (RubyModule) packageModule;
         } else {
             return null;
         }
     }
 
     private static RubyModule createPackageModule(RubyModule parent, String name, String packageString) {
         Ruby runtime = parent.getRuntime();
         RubyModule packageModule = (RubyModule) runtime.getJavaSupport().getPackageModuleTemplate().dup();
         packageModule.fastSetInstanceVariable("@package_name", runtime.newString(
                 packageString.length() > 0 ? packageString + '.' : packageString));
 
         // this is where we'll get connected when classes are opened using
         // package module syntax.
         packageModule.addClassProvider(JAVA_PACKAGE_CLASS_PROVIDER);
 
         parent.const_set(runtime.newSymbol(name), packageModule);
         MetaClass metaClass = (MetaClass) packageModule.getMetaClass();
         metaClass.setAttached(packageModule);
         return packageModule;
     }
     private static final Pattern CAMEL_CASE_PACKAGE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");
 
     public static RubyModule getPackageModule(Ruby runtime, String name) {
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject value;
         if ((value = javaModule.getConstantAt(name)) instanceof RubyModule) {
             return (RubyModule) value;
         }
         String packageName;
         if ("Default".equals(name)) {
             packageName = "";
         } else {
             Matcher m = CAMEL_CASE_PACKAGE_SPLITTER.matcher(name);
             packageName = m.replaceAll("$1.$2").toLowerCase();
         }
         return createPackageModule(javaModule, name, packageName);
     }
 
     public static IRubyObject get_package_module(IRubyObject recv, IRubyObject symObject) {
         return getPackageModule(recv.getRuntime(), symObject.asJavaString());
     }
 
     public static IRubyObject get_package_module_dot_format(IRubyObject recv, IRubyObject dottedName) {
         Ruby runtime = recv.getRuntime();
         RubyModule module = getJavaPackageModule(runtime, dottedName.asJavaString());
         return module == null ? runtime.getNil() : module;
     }
 
     public static RubyModule getProxyOrPackageUnderPackage(ThreadContext context, final Ruby runtime, 
             RubyModule parentPackage, String sym) {
         IRubyObject packageNameObj = parentPackage.fastGetInstanceVariable("@package_name");
         if (packageNameObj == null) {
             throw runtime.newArgumentError("invalid package module");
         }
         String packageName = packageNameObj.asJavaString();
         final String name = sym.trim().intern();
         if (name.length() == 0) {
             throw runtime.newArgumentError("empty class or package name");
         }
         String fullName = packageName + name;
         if (Character.isLowerCase(name.charAt(0))) {
             // TODO: should check against all Java reserved names here, not just primitives
             if (JAVA_PRIMITIVES.containsKey(name)) {
                 throw runtime.newArgumentError("illegal package name component: " + name);
             // this covers the rare case of lower-case class names (and thus will
             // fail 99.999% of the time). fortunately, we'll only do this once per
             // package name. (and seriously, folks, look into best practices...)
             }
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, fullName));
             } catch (RaiseException re) { /* expected */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* expected */ }
 
             RubyModule packageModule;
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if ((packageModule = getJavaPackageModule(runtime, fullName)) == null) {
                 return null;
             // save package module as ivar in parent, and add method to parent so
             // we don't have to come back here.
             }
             final String ivarName = ("@__pkg__" + name).intern();
             parentPackage.fastSetInstanceVariable(ivarName, packageModule);
             RubyClass singleton = parentPackage.getSingletonClass();
             singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, Visibility.PUBLIC) {
 
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) {
                         Arity.raiseArgumentError(runtime, args.length, 0, 0);
                     }
                     IRubyObject variable;
                     if ((variable = ((RubyModule) self).fastGetInstanceVariable(ivarName)) != null) {
                         return variable;
                     }
                     return runtime.getNil();
                 }
 
                 @Override
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             return packageModule;
         } else {
             // upper case name, so most likely a class
             return getProxyClass(runtime, JavaClass.forNameVerbose(runtime, fullName));
 
         // FIXME: we should also support orgs that use capitalized package
         // names (including, embarrassingly, the one I work for), but this
         // should be enabled by a system property, as the expected default
         // behavior for an upper-case value should be (and is) to treat it
         // as a class name, and raise an exception if it's not found 
 
 //            try {
 //                return getProxyClass(runtime, JavaClass.forName(runtime, fullName));
 //            } catch (Exception e) {
 //                // but for those not hip to conventions and best practices,
 //                // we'll try as a package
 //                return getJavaPackageModule(runtime, fullName);
 //            }
         }
     }
 
     public static IRubyObject get_proxy_or_package_under_package(
             ThreadContext context,
             IRubyObject recv,
             IRubyObject parentPackage,
             IRubyObject sym) {
         Ruby runtime = recv.getRuntime();
         if (!(parentPackage instanceof RubyModule)) {
             throw runtime.newTypeError(parentPackage, runtime.getModule());
         }
         RubyModule result;
         if ((result = getProxyOrPackageUnderPackage(context, runtime,
                 (RubyModule) parentPackage, sym.asJavaString())) != null) {
             return result;
         }
         return runtime.getNil();
     }
 
     public static RubyModule getTopLevelProxyOrPackage(ThreadContext context, final Ruby runtime, String sym) {
         final String name = sym.trim().intern();
         if (name.length() == 0) {
             throw runtime.newArgumentError("empty class or package name");
         }
         if (Character.isLowerCase(name.charAt(0))) {
             // this covers primitives and (unlikely) lower-case class names
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not primitive or lc class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not primitive or lc class */ }
 
             // TODO: check for Java reserved names and raise exception if encountered
 
             RubyModule packageModule;
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if ((packageModule = getJavaPackageModule(runtime, name)) == null) {
                 return null;
             }
             RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
             if (javaModule.getMetaClass().isMethodBound(name, false)) {
                 return packageModule;
             // save package module as ivar in parent, and add method to parent so
             // we don't have to come back here.
             }
             final String ivarName = ("@__pkg__" + name).intern();
             javaModule.fastSetInstanceVariable(ivarName, packageModule);
             RubyClass singleton = javaModule.getSingletonClass();
             singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, Visibility.PUBLIC) {
 
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) {
                         Arity.raiseArgumentError(runtime, args.length, 0, 0);
                     }
                     IRubyObject variable;
                     if ((variable = ((RubyModule) self).fastGetInstanceVariable(ivarName)) != null) {
                         return variable;
                     }
                     return runtime.getNil();
                 }
 
                 @Override
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             return packageModule;
         } else {
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not a class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not a class */ }
 
             // upper-case package name
             // TODO: top-level upper-case package was supported in the previous (Ruby-based)
             // implementation, so leaving as is.  see note at #getProxyOrPackageUnderPackage
             // re: future approach below the top-level.
             return getPackageModule(runtime, name);
         }
     }
 
     public static IRubyObject get_top_level_proxy_or_package(ThreadContext context, IRubyObject recv, IRubyObject sym) {
         Ruby runtime = context.getRuntime();
         RubyModule result;
         if ((result = getTopLevelProxyOrPackage(context, runtime, sym.asJavaString())) != null) {
             return result;
         }
         return runtime.getNil();
     }
 
     public static IRubyObject matching_method(IRubyObject recv, IRubyObject methods, IRubyObject args) {
         Map matchCache = recv.getRuntime().getJavaSupport().getMatchCache();
 
         List<Class<?>> arg_types = new ArrayList<Class<?>>();
         int alen = ((RubyArray) args).getLength();
         IRubyObject[] aargs = ((RubyArray) args).toJavaArrayMaybeUnsafe();
         for (int i = 0; i < alen; i++) {
             if (aargs[i] instanceof JavaObject) {
                 arg_types.add(((JavaClass) ((JavaObject) aargs[i]).java_class()).javaClass());
             } else {
                 arg_types.add(aargs[i].getClass());
             }
         }
 
         Map ms = (Map) matchCache.get(methods);
         if (ms == null) {
             ms = new HashMap();
             matchCache.put(methods, ms);
         } else {
             IRubyObject method = (IRubyObject) ms.get(arg_types);
             if (method != null) {
                 return method;
             }
         }
 
         int mlen = ((RubyArray) methods).getLength();
         IRubyObject[] margs = ((RubyArray) methods).toJavaArrayMaybeUnsafe();
 
         for (int i = 0; i < 2; i++) {
             for (int k = 0; k < mlen; k++) {
                 IRubyObject method = margs[k];
                 List<Class<?>> types = Arrays.asList(((ParameterTypes) method).getParameterTypes());
 
                 // Compatible (by inheritance)
                 if (arg_types.size() == types.size()) {
                     // Exact match
                     if (types.equals(arg_types)) {
                         ms.put(arg_types, method);
                         return method;
                     }
 
                     boolean match = true;
                     for (int j = 0; j < types.size(); j++) {
                         if (!(JavaClass.assignable(types.get(j), arg_types.get(j)) &&
                                 (i > 0 || primitive_match(types.get(j), arg_types.get(j)))) && !JavaUtil.isDuckTypeConvertable(arg_types.get(j), types.get(j))) {
                             match = false;
                             break;
                         }
                     }
                     if (match) {
                         ms.put(arg_types, method);
                         return method;
                     }
                 } // Could check for varargs here?
 
             }
         }
 
         Object o1 = margs[0];
 
         if (o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw recv.getRuntime().newNameError("no constructor with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         } else {
             throw recv.getRuntime().newNameError("no " + ((JavaMethod) o1).name() + " with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         }
     }
     
     public static int argsHashCode(Object[] a) {
         if (a == null)
             return 0;
  
         int result = 1;
  
         for (Object element : a)
             result = 31 * result + (element == null ? 0 : element.getClass().hashCode());
  
         return result;
     }
     
     public static int argsHashCode(IRubyObject a0) {
         return 31 + classHashCode(a0);
     }
     
     public static int argsHashCode(IRubyObject a0, IRubyObject a1) {
         return 31 * (31 + classHashCode(a0)) + classHashCode(a1);
     }
     
     public static int argsHashCode(IRubyObject a0, IRubyObject a1, IRubyObject a2) {
         return 31 * (31 * (31 + classHashCode(a0)) + classHashCode(a1)) + classHashCode(a2);
     }
     
     public static int argsHashCode(IRubyObject a0, IRubyObject a1, IRubyObject a2, IRubyObject a3) {
         return 31 * (31 * (31 * (31 + classHashCode(a0)) + classHashCode(a1)) + classHashCode(a2)) + classHashCode(a3);
     }
     
     private static int classHashCode(IRubyObject o) {
         return o == null ? 0 : o.getJavaClass().hashCode();
     }
     
     public static int argsHashCode(IRubyObject[] a) {
         if (a == null)
             return 0;
  
         int result = 1;
  
         for (IRubyObject element : a)
             result = 31 * result + classHashCode(element);
  
         return result;
     }
     
     public static Class argClass(Object a) {
         if (a == null) return void.class;
         
         return a.getClass();
     }
     
     public static Class argClass(IRubyObject a) {
         if (a == null) return void.class;
         
         return a.getJavaClass();
     }
 
     public static JavaCallable matching_method_internal(IRubyObject recv, Map cache, JavaCallable[] methods, Object[] args, int len) {
         int signatureCode = argsHashCode(args);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method != null) {
             return method;
         }
 
         int mlen = methods.length;
 
         mfor:
         for (int k = 0; k < mlen; k++) {
             method = methods[k];
             Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
             // Compatible (by inheritance)
             if (len == types.length) {
                 // Exact match
                 boolean same = true;
                 for (int x = 0, y = len; x < y; x++) {
                     if (!types[x].equals(argClass(args[x]))) {
                         same = false;
                         break;
                     }
                 }
                 if (same) {
                     cache.put(signatureCode, method);
                     return method;
                 }
 
                 for (int j = 0, m = len; j < m; j++) {
                     if (!(JavaClass.assignable(types[j], argClass(args[j])) &&
                             primitive_match(types[j], argClass(args[j])))) {
                         continue mfor;
                     }
                 }
                 cache.put(signatureCode, method);
                 return method;
             }
         }
 
         mfor:
         for (int k = 0; k < mlen; k++) {
             method = methods[k];
             Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
             // Compatible (by inheritance)
             if (len == types.length) {
                 for (int j = 0, m = len; j < m; j++) {
                     if (!JavaClass.assignable(types[j], argClass(args[j])) && !JavaUtil.isDuckTypeConvertable(argClass(args[j]), types[j])) {
                         continue mfor;
                     }
                 }
                 cache.put(signatureCode, method);
                 return method;
             }
         }
 
         // We've fallen and can't get up...prepare for error message
         Object o1 = methods[0];
         ArrayList argTypes = new ArrayList(args.length);
         for (Object o : args) argTypes.add(argClass(o));
 
         if (o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw recv.getRuntime().newNameError("no constructor with arguments matching " + argTypes + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         } else {
             Thread.dumpStack();
             throw recv.getRuntime().newNameError("no " + ((JavaMethod) o1).name() + " with arguments matching " + argTypes + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         }
     }
     
     public static JavaCallable matchingCallableArityN(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject[] args, int argsLength) {
         int signatureCode = argsHashCode(args);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method != null) {
             return method;
         }
 
         for (int k = 0; k < methods.length; k++) {
             method = methods[k];
             Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
             
             assert(types.length == argsLength);
             
             if (argTypesWillWork(types, args, argsLength)) {
                 cache.put(signatureCode, method);
                 return method;
             }
         }
 
         // We've fallen and can't get up...prepare for error message
         throw argTypesDoNotMatch(recv.getRuntime(), recv, methods, args);
     }
     
     public static JavaCallable matchingCallableArityOne(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0) {
         int signatureCode = argsHashCode(arg0);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method != null) {
             return method;
         }
+        
+        // first look for an exact match
+        for (int k = 0; k < methods.length; k++) {
+            method = methods[k];
+            Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
+            
+            assert types.length == 1;
+            
+            if (exactMatch(types[0], arg0)) {
+                cache.put(signatureCode, method);
+                return method;
+            }
+        }
 
+        // then a broader search
         for (int k = 0; k < methods.length; k++) {
             method = methods[k];
             Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
             
             assert types.length == 1;
             
             if (anyMatch(types[0], arg0)) {
                 cache.put(signatureCode, method);
                 return method;
             }
         }
 
         // We've fallen and can't get up...prepare for error message
         throw argTypesDoNotMatch(recv.getRuntime(), recv, methods, arg0);
     }
     
     public static JavaCallable matchingCallableArityTwo(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1) {
         int signatureCode = argsHashCode(arg0, arg1);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method != null) {
             return method;
         }
 
         for (int k = 0; k < methods.length; k++) {
             method = methods[k];
             Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
             
             assert types.length == 2;
             
             if (anyMatch(types[0], arg0) &&
                     anyMatch(types[1], arg1)) {
                 cache.put(signatureCode, method);
                 return method;
             }
         }
 
         // We've fallen and can't get up...prepare for error message
         throw argTypesDoNotMatch(recv.getRuntime(), recv, methods, arg0, arg1);
     }
     
     public static JavaCallable matchingCallableArityThree(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         int signatureCode = argsHashCode(arg0, arg1, arg2);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method != null) {
             return method;
         }
 
         for (int k = 0; k < methods.length; k++) {
             method = methods[k];
             Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
             
             assert types.length == 3;
             
             if (anyMatch(types[0], arg0) &&
                     anyMatch(types[1], arg1) &&
                     anyMatch(types[2], arg2)) {
                 cache.put(signatureCode, method);
                 return method;
             }
         }
 
         // We've fallen and can't get up...prepare for error message
         throw argTypesDoNotMatch(recv.getRuntime(), recv, methods, arg0, arg1, arg2);
     }
     
     public static JavaCallable matchingCallableArityFour(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         int signatureCode = argsHashCode(arg0, arg1, arg2, arg3);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method != null) {
             return method;
         }
 
         for (int k = 0; k < methods.length; k++) {
             method = methods[k];
             Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
             
             assert types.length == 4;
             
             if (anyMatch(types[0], arg0) &&
                     anyMatch(types[1], arg1) &&
                     anyMatch(types[2], arg2) &&
                     anyMatch(types[3], arg3)) {
                 cache.put(signatureCode, method);
                 return method;
             }
         }
 
         // We've fallen and can't get up...prepare for error message
         throw argTypesDoNotMatch(recv.getRuntime(), recv, methods, arg0, arg1, arg2, arg3);
     }
     
     private static boolean argTypesWillWork(Class[] types, IRubyObject[] args, int argsLength) {
         for (int x = 0, y = argsLength; x < y; x++) {
             if (!anyMatch(types[x], args[x])) {
                 return false;
             }
         }
         return true;
     }
     
     private static boolean anyMatch(Class type, IRubyObject arg) {
         return exactMatch(type, arg) ||
                 assignable(type, arg) ||
                 primativable(type, arg) ||
                 duckable(type, arg);
     }   
     
     private static boolean exactMatch(Class type, IRubyObject arg) {
         return type.equals(argClass(arg));
     }
     
     private static boolean assignable(Class type, IRubyObject arg) {
         return JavaClass.assignable(type, argClass(arg));
     }
     
     private static boolean primativable(Class type, IRubyObject arg) {
         Class argClass = argClass(arg);
         if (type.isPrimitive()) {
             if (type == Integer.TYPE || type == Long.TYPE || type == Short.TYPE || type == Character.TYPE) {
                 return argClass == Integer.class ||
                         argClass == Long.class ||
                         argClass == Short.class ||
                         argClass == Character.class;
             } else if (type == Float.TYPE || type == Double.TYPE) {
                 return argClass == Float.class ||
                         argClass == Double.class;
             } else if (type == Boolean.TYPE) {
                 return argClass == Boolean.class;
             }
         }
         return false;
     }
     
     private static boolean duckable(Class type, IRubyObject arg) {
         return JavaUtil.isDuckTypeConvertable(argClass(arg), type);
     }
     
     private static RaiseException argTypesDoNotMatch(Ruby runtime, IRubyObject receiver, JavaCallable[] methods, IRubyObject... args) {
         Object o1 = methods[0];
         ArrayList argTypes = new ArrayList(args.length);
         for (Object o : args) argTypes.add(argClass(o));
 
         if (o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw runtime.newNameError("no constructor with arguments matching " + argTypes + " on object " + receiver.callMethod(runtime.getCurrentContext(), "inspect"), null);
         } else {
             throw runtime.newNameError("no " + ((JavaMethod) o1).name() + " with arguments matching " + argTypes + " on object " + receiver.callMethod(runtime.getCurrentContext(), "inspect"), null);
         }
     }
     
     public static IRubyObject access(IRubyObject recv, IRubyObject java_type) {
         int modifiers = ((JavaClass) java_type).javaClass().getModifiers();
         return recv.getRuntime().newString(Modifier.isPublic(modifiers) ? "public" : (Modifier.isProtected(modifiers) ? "protected" : "private"));
     }
 
     public static IRubyObject valid_constant_name_p(IRubyObject recv, IRubyObject name) {
         RubyString sname = name.convertToString();
         if (sname.getByteList().length() == 0) {
             return recv.getRuntime().getFalse();
         }
         return Character.isUpperCase(sname.getByteList().charAt(0)) ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
 
     public static boolean primitive_match(Object v1, Object v2) {
         if (((Class) v1).isPrimitive()) {
             if (v1 == Integer.TYPE || v1 == Long.TYPE || v1 == Short.TYPE || v1 == Character.TYPE) {
                 return v2 == Integer.class ||
                         v2 == Long.class ||
                         v2 == Short.class ||
                         v2 == Character.class;
             } else if (v1 == Float.TYPE || v1 == Double.TYPE) {
                 return v2 == Float.class ||
                         v2 == Double.class;
             } else if (v1 == Boolean.TYPE) {
                 return v2 == Boolean.class;
             }
             return false;
         }
         return true;
     }
 
     public static IRubyObject primitive_match(IRubyObject recv, IRubyObject t1, IRubyObject t2) {
         if (((JavaClass) t1).primitive_p().isTrue()) {
             Object v1 = ((JavaObject) t1).getValue();
             Object v2 = ((JavaObject) t2).getValue();
             return primitive_match(v1, v2) ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
         }
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject wrap(IRubyObject recv, IRubyObject java_object) {
         return getInstance(recv.getRuntime(), ((JavaObject) java_object).getValue());
     }
 
     public static IRubyObject wrap(Ruby runtime, IRubyObject java_object) {
         return getInstance(runtime, ((JavaObject) java_object).getValue());
     }
 
     // Java methods
     @JRubyMethod(required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject define_exception_handler(IRubyObject recv, IRubyObject[] args, Block block) {
         String name = args[0].toString();
         RubyProc handler = null;
         if (args.length > 1) {
             handler = (RubyProc) args[1];
         } else {
             handler = recv.getRuntime().newProc(Block.Type.PROC, block);
         }
         recv.getRuntime().getJavaSupport().defineExceptionHandler(name, handler);
 
         return recv;
     }
 
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject primitive_to_java(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.primitive_to_java(recv, object, unusedBlock);
     }
 
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version 
      */
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject java_to_ruby(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.java_to_ruby(recv, object, unusedBlock);
     }
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     /**
      * High-level object conversion utility. 
      */
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.ruby_to_java(recv, object, unusedBlock);
     }
 
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject java_to_primitive(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.java_to_primitive(recv, object, unusedBlock);
     }
 
     @JRubyMethod(required = 1, rest = true, frame = true, module = true, visibility = Visibility.PRIVATE)
     @Deprecated
     public static IRubyObject new_proxy_instance(final IRubyObject recv, IRubyObject[] args, Block block) {
         int size = Arity.checkArgumentCount(recv.getRuntime(), args, 1, -1) - 1;
         final RubyProc proc;
 
         // Is there a supplied proc argument or do we assume a block was supplied
         if (args[size] instanceof RubyProc) {
             proc = (RubyProc) args[size];
         } else {
             proc = recv.getRuntime().newProc(Block.Type.PROC, block);
             size++;
         }
 
         // Create list of interfaces to proxy (and make sure they really are interfaces)
         Class[] interfaces = new Class[size];
         for (int i = 0; i < size; i++) {
             if (!(args[i] instanceof JavaClass) || !((JavaClass) args[i]).interface_p().isTrue()) {
                 throw recv.getRuntime().newArgumentError("Java interface expected. got: " + args[i]);
             }
             interfaces[i] = ((JavaClass) args[i]).javaClass();
         }
 
         return JavaObject.wrap(recv.getRuntime(), Proxy.newProxyInstance(recv.getRuntime().getJRubyClassLoader(), interfaces, new InvocationHandler() {
 
             private Map parameterTypeCache = new ConcurrentHashMap();
 
             public Object invoke(Object proxy, Method method, Object[] nargs) throws Throwable {
                 Class[] parameterTypes = (Class[]) parameterTypeCache.get(method);
                 if (parameterTypes == null) {
                     parameterTypes = method.getParameterTypes();
                     parameterTypeCache.put(method, parameterTypes);
                 }
                 int methodArgsLength = parameterTypes.length;
                 String methodName = method.getName();
 
                 if (methodName.equals("toString") && methodArgsLength == 0) {
                     return proxy.getClass().getName();
                 } else if (methodName.equals("hashCode") && methodArgsLength == 0) {
                     return new Integer(proxy.getClass().hashCode());
                 } else if (methodName.equals("equals") && methodArgsLength == 1 && parameterTypes[0].equals(Object.class)) {
                     return Boolean.valueOf(proxy == nargs[0]);
                 }
                 Ruby runtime = recv.getRuntime();
                 int length = nargs == null ? 0 : nargs.length;
                 IRubyObject[] rubyArgs = new IRubyObject[length + 2];
                 rubyArgs[0] = JavaObject.wrap(runtime, proxy);
                 rubyArgs[1] = new JavaMethod(runtime, method);
                 for (int i = 0; i < length; i++) {
                     rubyArgs[i + 2] = JavaObject.wrap(runtime, nargs[i]);
                 }
                 return JavaUtil.convertArgument(runtime, proc.call(runtime.getCurrentContext(), rubyArgs), method.getReturnType());
             }
         }));
     }
 
     @JRubyMethod(required = 2, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_proxy_instance2(IRubyObject recv, final IRubyObject wrapper, IRubyObject ifcs, Block block) {
         IRubyObject[] javaClasses = ((RubyArray)ifcs).toJavaArray();
         final Ruby runtime = recv.getRuntime();
 
         // Create list of interface names to proxy (and make sure they really are interfaces)
         Class[] interfaces = new Class[javaClasses.length];
         for (int i = 0; i < javaClasses.length; i++) {
             if (!(javaClasses[i] instanceof JavaClass) || !((JavaClass) javaClasses[i]).interface_p().isTrue()) {
                 throw recv.getRuntime().newArgumentError("Java interface expected. got: " + javaClasses[i]);
             }
             interfaces[i] = ((JavaClass) javaClasses[i]).javaClass();
         }
         
         // TODO: cache this class!
         String implClassName = "InterfaceImpl" + wrapper.getMetaClass().hashCode();
         Class proxyImplClass;
         try {
             proxyImplClass = Class.forName(implClassName, true, runtime.getJRubyClassLoader());
         } catch (ClassNotFoundException cnfe) {
             proxyImplClass = MiniJava.createOldStyleImplClass(interfaces, wrapper.getMetaClass(), runtime, implClassName);
         }
         
         try {
             Constructor proxyConstructor = proxyImplClass.getConstructor(IRubyObject.class);
             return JavaObject.wrap(recv.getRuntime(), proxyConstructor.newInstance(wrapper));
         } catch (NoSuchMethodException nsme) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + nsme);
         } catch (InvocationTargetException ite) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ite);
         } catch (InstantiationException ie) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ie);
         } catch (IllegalAccessException iae) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + iae);
         }
     }
 }
