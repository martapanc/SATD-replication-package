diff --git a/src/org/jruby/RubyBigDecimal.java b/src/org/jruby/RubyBigDecimal.java
index 1b584951e9..894ac7d8d4 100644
--- a/src/org/jruby/RubyBigDecimal.java
+++ b/src/org/jruby/RubyBigDecimal.java
@@ -1,600 +1,614 @@
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
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 
 import java.math.BigDecimal;
 
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class RubyBigDecimal extends RubyNumeric {
     private static final ObjectAllocator BIGDECIMAL_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(IRuby runtime, RubyClass klass) {
             return new RubyBigDecimal(runtime, klass);
         }
     };
     
     public static RubyClass createBigDecimal(IRuby runtime) {
         RubyClass result = runtime.defineClass("BigDecimal",runtime.getClass("Numeric"), BIGDECIMAL_ALLOCATOR);
 
         result.setConstant("ROUND_DOWN",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_DOWN));
         result.setConstant("SIGN_POSITIVE_INFINITE",RubyNumeric.int2fix(runtime,3));
         result.setConstant("EXCEPTION_OVERFLOW",RubyNumeric.int2fix(runtime,1));
         result.setConstant("SIGN_POSITIVE_ZERO",RubyNumeric.int2fix(runtime,1));
         result.setConstant("EXCEPTION_ALL",RubyNumeric.int2fix(runtime,255));
         result.setConstant("ROUND_CEILING",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_CEILING));
         result.setConstant("ROUND_UP",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_UP));
         result.setConstant("SIGN_NEGATIVE_FINITE",RubyNumeric.int2fix(runtime,-2));
         result.setConstant("EXCEPTION_UNDERFLOW",RubyNumeric.int2fix(runtime, 4));
         result.setConstant("SIGN_NaN",RubyNumeric.int2fix(runtime, 0));
         result.setConstant("BASE",RubyNumeric.int2fix(runtime,10000));
         result.setConstant("ROUND_HALF_DOWN",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_HALF_DOWN));
         result.setConstant("ROUND_MODE",RubyNumeric.int2fix(runtime,256));
         result.setConstant("SIGN_POSITIVE_FINITE",RubyNumeric.int2fix(runtime,2));
         result.setConstant("EXCEPTION_INFINITY",RubyNumeric.int2fix(runtime,1));
         result.setConstant("ROUND_HALF_EVEN",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_HALF_EVEN));
         result.setConstant("ROUND_HALF_UP",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_HALF_UP));
         result.setConstant("SIGN_NEGATIVE_INFINITE",RubyNumeric.int2fix(runtime,-3));
         result.setConstant("EXCEPTION_ZERODIVIDE",RubyNumeric.int2fix(runtime,1));
         result.setConstant("SIGN_NEGATIVE_ZERO",RubyNumeric.int2fix(runtime,-1));
         result.setConstant("EXCEPTION_NaN",RubyNumeric.int2fix(runtime,2));
         result.setConstant("ROUND_FLOOR",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_FLOOR));
 
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyBigDecimal.class);
 
         runtime.getModule("Kernel").defineModuleFunction("BigDecimal",callbackFactory.getOptSingletonMethod("newBigDecimal"));
         result.defineFastSingletonMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));
         result.defineFastSingletonMethod("ver", callbackFactory.getSingletonMethod("ver"));
         result.defineSingletonMethod("_load", callbackFactory.getSingletonMethod("_load",IRubyObject.class));
         result.defineFastSingletonMethod("double_fig", callbackFactory.getSingletonMethod("double_fig"));
         result.defineFastSingletonMethod("limit", callbackFactory.getOptSingletonMethod("limit"));
         result.defineFastSingletonMethod("mode", callbackFactory.getSingletonMethod("mode",IRubyObject.class,IRubyObject.class));
 
         result.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         result.defineFastMethod("%", callbackFactory.getMethod("mod",IRubyObject.class));
         result.defineFastMethod("modulo", callbackFactory.getMethod("mod",IRubyObject.class));
         result.defineFastMethod("*", callbackFactory.getOptMethod("mult"));
         result.defineFastMethod("mult", callbackFactory.getOptMethod("mult"));
-        result.defineFastMethod("**", callbackFactory.getMethod("power",RubyInteger.class));
-        result.defineFastMethod("power", callbackFactory.getMethod("power",RubyInteger.class));
+        result.defineFastMethod("**", callbackFactory.getMethod("power",IRubyObject.class));
+        result.defineFastMethod("power", callbackFactory.getMethod("power",IRubyObject.class));
         result.defineFastMethod("+", callbackFactory.getOptMethod("add"));
         result.defineFastMethod("add", callbackFactory.getOptMethod("add"));
         result.defineFastMethod("-", callbackFactory.getOptMethod("sub"));
         result.defineFastMethod("sub", callbackFactory.getOptMethod("sub"));
         result.defineFastMethod("/", callbackFactory.getOptMethod("div"));
         result.defineFastMethod("div", callbackFactory.getOptMethod("div"));
         result.defineFastMethod("quo", callbackFactory.getOptMethod("div"));
         result.defineFastMethod("<=>", callbackFactory.getMethod("spaceship",IRubyObject.class));
         result.defineFastMethod("==", callbackFactory.getMethod("eql_p",IRubyObject.class));
         result.defineFastMethod("===", callbackFactory.getMethod("eql_p",IRubyObject.class));
         result.defineFastMethod("eql?", callbackFactory.getMethod("eql_p",IRubyObject.class));
         result.defineFastMethod("!=", callbackFactory.getMethod("ne",IRubyObject.class));
         result.defineFastMethod("<", callbackFactory.getMethod("lt",IRubyObject.class));
         result.defineFastMethod("<=", callbackFactory.getMethod("le",IRubyObject.class));
         result.defineFastMethod(">", callbackFactory.getMethod("gt",IRubyObject.class));
         result.defineFastMethod(">=", callbackFactory.getMethod("ge",IRubyObject.class));
         result.defineFastMethod("abs", callbackFactory.getMethod("abs"));
-        result.defineFastMethod("ceil", callbackFactory.getMethod("ceil",RubyInteger.class));
+        result.defineFastMethod("ceil", callbackFactory.getMethod("ceil",IRubyObject.class));
         result.defineFastMethod("coerce", callbackFactory.getMethod("coerce",IRubyObject.class));
         result.defineFastMethod("divmod", callbackFactory.getMethod("divmod",IRubyObject.class)); 
         result.defineFastMethod("exponent", callbackFactory.getMethod("exponent"));
         result.defineFastMethod("finite?", callbackFactory.getMethod("finite_p"));
         result.defineFastMethod("fix", callbackFactory.getMethod("fix"));
-        result.defineFastMethod("floor", callbackFactory.getMethod("floor",RubyInteger.class));
+        result.defineFastMethod("floor", callbackFactory.getMethod("floor",IRubyObject.class));
         result.defineFastMethod("frac", callbackFactory.getMethod("frac"));
         result.defineFastMethod("infinite?", callbackFactory.getMethod("infinite_p"));
         result.defineFastMethod("inspect", callbackFactory.getMethod("inspect"));
         result.defineFastMethod("nan?", callbackFactory.getMethod("nan_p"));
         result.defineFastMethod("nonzero?", callbackFactory.getMethod("nonzero_p"));
         result.defineFastMethod("precs", callbackFactory.getMethod("precs"));
         result.defineFastMethod("remainder", callbackFactory.getMethod("remainder",IRubyObject.class));
         result.defineFastMethod("round", callbackFactory.getOptMethod("round"));
         result.defineFastMethod("sign", callbackFactory.getMethod("sign"));
         result.defineFastMethod("split", callbackFactory.getMethod("split"));
         result.defineFastMethod("sqrt", callbackFactory.getOptMethod("sqrt"));
         result.defineFastMethod("to_f", callbackFactory.getMethod("to_f"));
         result.defineFastMethod("to_i", callbackFactory.getMethod("to_i"));
         result.defineFastMethod("to_int", callbackFactory.getMethod("to_int"));
         result.defineFastMethod("to_s", callbackFactory.getOptMethod("to_s"));
         result.defineFastMethod("truncate", callbackFactory.getOptMethod("truncate"));
         result.defineFastMethod("zero?", callbackFactory.getMethod("zero_p"));
 
         result.setClassVar("VpPrecLimit", RubyFixnum.zero(runtime));
 
         return result;
     }
 
     private BigDecimal value;
 
     public RubyBigDecimal(IRuby runtime, RubyClass klass) {
         super(runtime, klass);
     }
 
     public RubyBigDecimal(IRuby runtime, BigDecimal value) {
         super(runtime, runtime.getClass("BigDecimal"));
         this.value = value;
     }
 
     public static RubyBigDecimal newInstance(IRubyObject recv, IRubyObject[] args) {
-        RubyClass klass = null;
-        if(recv instanceof RubyClass) {
-            klass = (RubyClass)recv;
-        } else { // This happens when using the Kernel#BigDecimal method
-            klass = recv.getRuntime().getClass("BigDecimal");
-        }
-
+        RubyClass klass = (RubyClass)recv;
         RubyBigDecimal result = (RubyBigDecimal)klass.allocate();
         result.callInit(args);
         
         return result;
     }
 
     public static RubyBigDecimal newBigDecimal(IRubyObject recv, IRubyObject[] args) {
         return newInstance(recv.getRuntime().getClass("BigDecimal"), args);
     }
 
     public static IRubyObject ver(IRubyObject recv) {
         return recv.getRuntime().newString("1.0.1");
     }
 
     public static IRubyObject _load(IRubyObject recv, IRubyObject p1) {
         // TODO: implement
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject double_fig(IRubyObject recv) {
         return recv.getRuntime().newFixnum(20);
     }
     
     public static IRubyObject limit(IRubyObject recv, IRubyObject[] args) {
         RubyModule c = (RubyModule)recv;
         IRubyObject nCur = c.getClassVar("VpPrecLimit");
         if(recv.checkArgumentCount(args,0,1) == 1) {
             if(args[0].isNil()) {
                 return nCur;
             }
             c.setClassVar("VpPrecLimit",args[0]);
         }
 
         return nCur;
     }
 
     public static IRubyObject mode(IRubyObject recv, IRubyObject mode, IRubyObject value) {
         System.err.println("unimplemented: mode");
         // TODO: implement
         return recv.getRuntime().getNil();
     }
 
+    private RubyBigDecimal getVpValue(IRubyObject v, boolean must) {
+        if(v instanceof RubyBigDecimal) {
+            return (RubyBigDecimal)v;
+        } else if(v instanceof RubyFixnum || v instanceof RubyBignum) {
+            String s = v.toString();
+            return newInstance(getRuntime().getClass("BigDecimal"),new IRubyObject[]{getRuntime().newString(s)});
+        }
+        if(must) {
+            throw getRuntime().newTypeError(trueFalseNil(v.getMetaClass().getName() + " can't be coerced into BigDecimal"));
+        }
+        return null;
+    }
+
     public IRubyObject initialize(IRubyObject[] args) {
         String ss = args[0].convertToString().toString();
         if(ss.indexOf('.') != -1) {
             ss = removeTrailingZeroes(ss);
         }
         try {
             this.value = new BigDecimal(ss);
         } catch(NumberFormatException e) {
             this.value = new BigDecimal("0");
         }
 
         return this;
     }
 
     private RubyBigDecimal setResult() {
         return setResult(0);
     }
 
     private RubyBigDecimal setResult(int scale) {
         int prec = RubyFixnum.fix2int(getRuntime().getClass("BigDecimal").getClassVar("VpPrecLimit"));
         int prec2 = Math.max(scale,prec);
         if(prec2 > 0 && this.value.scale() > (prec2-exp())) {
             this.value = this.value.setScale(prec2-exp(),BigDecimal.ROUND_HALF_UP);
         }
         return this;
     }
 
     public IRubyObject mod(IRubyObject arg) {
         System.err.println("unimplemented: mod");
         // TODO: implement
         return this;
     }
 
     public IRubyObject mult(IRubyObject[] args) {
-        // TODO: better implementation
-        RubyBigDecimal val = null;
-        if(args[0] instanceof RubyBigDecimal) {
-            val = (RubyBigDecimal)args[0];
-        } else {
-            val = (RubyBigDecimal)args[0].callMethod(getRuntime().getCurrentContext(), "to_d");
+        RubyBigDecimal val = getVpValue(args[0],false);
+        if(val == null) {
+            return callCoerced("*",args[0]);
         }
+
         return new RubyBigDecimal(getRuntime(),value.multiply(val.value)).setResult();
     }
 
-    public IRubyObject power(RubyInteger arg) {
-        // TODO: MUCH better implementation
+    public IRubyObject power(IRubyObject arg) {
+        // TODO: better implementation
         BigDecimal val = value;
-        for(int i=0,j=RubyNumeric.fix2int(arg);i<j;i++) {
+        int times = RubyNumeric.fix2int(arg.convertToInteger());
+        for(int i=0;i<times;i++) {
             val = val.multiply(val);
         }
         return new RubyBigDecimal(getRuntime(),val).setResult();
     }
 
     public IRubyObject add(IRubyObject[] args) {
-        // TODO: better implementation
-        RubyBigDecimal val = null;
-        if(args[0] instanceof RubyBigDecimal) {
-            val = (RubyBigDecimal)args[0];
-        } else {
-            val = (RubyBigDecimal)args[0].callMethod(getRuntime().getCurrentContext(), "to_d");
+        RubyBigDecimal val = getVpValue(args[0],false);
+        if(val == null) {
+            return callCoerced("+",args[0]);
         }
         return new RubyBigDecimal(getRuntime(),value.add(val.value)).setResult();
     }
 
     public IRubyObject sub(IRubyObject[] args) {
-        // TODO: better implementation
-        RubyBigDecimal val = null;
-        if(args[0] instanceof RubyBigDecimal) {
-            val = (RubyBigDecimal)args[0];
-        } else {
-            val = (RubyBigDecimal)args[0].callMethod(getRuntime().getCurrentContext(), "to_d");
+        RubyBigDecimal val = getVpValue(args[0],false);
+        if(val == null) {
+            return callCoerced("-",args[0]);
         }
         return new RubyBigDecimal(getRuntime(),value.subtract(val.value)).setResult();
     }
 
     public IRubyObject div(IRubyObject[] args) {
-        // TODO: better implementation
-        RubyBigDecimal val = null;
         int scale = 0;
         if(checkArgumentCount(args,1,2) == 2) {
             scale = RubyNumeric.fix2int(args[1]);
         }
 
-        if(args[0] instanceof RubyBigDecimal) {
-            val = (RubyBigDecimal)args[0];
-        } else {
-            val = (RubyBigDecimal)args[0].callMethod(getRuntime().getCurrentContext(), "to_d");
+        RubyBigDecimal val = getVpValue(args[0],false);
+        if(val == null) {
+            return callCoerced("/",args[0]);
         }
 
         if(scale == 0) {
             return new RubyBigDecimal(getRuntime(),value.divide(val.value,200,BigDecimal.ROUND_HALF_UP)).setResult();
         } else {
             return new RubyBigDecimal(getRuntime(),value.divide(val.value,200,BigDecimal.ROUND_HALF_UP)).setResult(scale);
         }
     }
 
     private IRubyObject cmp(IRubyObject r, char op) {
         int e = 0;
-        if(!(r instanceof RubyBigDecimal)) {
-            e = RubyNumeric.fix2int(callCoerced("<=>",r));
+        RubyBigDecimal rb = getVpValue(r,false);
+        if(rb == null) {
+            e = RubyNumeric.fix2int(callCoerced("<=>",rb));
         } else {
-            RubyBigDecimal rb = (RubyBigDecimal)r;
             e = value.compareTo(rb.value);
         }
         switch(op) {
         case '*': return getRuntime().newFixnum(e);
         case '=': return (e==0)?getRuntime().getTrue():getRuntime().getFalse();
         case '!': return (e!=0)?getRuntime().getTrue():getRuntime().getFalse();
         case 'G': return (e>=0)?getRuntime().getTrue():getRuntime().getFalse();
         case '>': return (e> 0)?getRuntime().getTrue():getRuntime().getFalse();
         case 'L': return (e<=0)?getRuntime().getTrue():getRuntime().getFalse();
         case '<': return (e< 0)?getRuntime().getTrue():getRuntime().getFalse();
         }
         return getRuntime().getNil();
     }
 
     public IRubyObject spaceship(IRubyObject arg) {
         return cmp(arg,'*');
     }
 
     public IRubyObject eql_p(IRubyObject arg) {
         return cmp(arg,'=');
     }
 
     public IRubyObject ne(IRubyObject arg) {
         return cmp(arg,'!');
     }
 
     public IRubyObject lt(IRubyObject arg) {
         return cmp(arg,'<');
     }
 
     public IRubyObject le(IRubyObject arg) {
         return cmp(arg,'L');
     }
 
     public IRubyObject gt(IRubyObject arg) {
         return cmp(arg,'>');
     }
 
     public IRubyObject ge(IRubyObject arg) {
         return cmp(arg,'G');
     }
 
     public RubyNumeric abs() {
         return new RubyBigDecimal(getRuntime(),value.abs()).setResult();
     }
 
-    public IRubyObject ceil(RubyInteger arg) {
+    public IRubyObject ceil(IRubyObject arg) {
         System.err.println("unimplemented: ceil");
         // TODO: implement correctly
         return this;
     }
 
     public IRubyObject coerce(IRubyObject other) {
         IRubyObject obj;
         if(other instanceof RubyFloat) {
             obj = getRuntime().newArray(other,to_f());
         } else {
-            obj = getRuntime().newArray(newInstance(other,new IRubyObject[]{other.callMethod(getRuntime().getCurrentContext(),"to_s")}),this);
+            obj = getRuntime().newArray(getVpValue(other,true),this);
         }
         return obj;
     }
 
     public double getDoubleValue() { return value.doubleValue(); }
     public long getLongValue() { return value.longValue(); }
 
+    public RubyNumeric multiplyWith(RubyInteger value) { 
+        return (RubyNumeric)mult(new IRubyObject[]{value});
+    }
+
+    public RubyNumeric multiplyWith(RubyFloat value) { 
+        return (RubyNumeric)mult(new IRubyObject[]{value});
+    }
+
+    public RubyNumeric multiplyWith(RubyBignum value) { 
+        return (RubyNumeric)mult(new IRubyObject[]{value});
+    }
+
     public IRubyObject divmod(IRubyObject arg) {
         System.err.println("unimplemented: divmod");
         // TODO: implement
         return getRuntime().getNil();
     }
 
     public IRubyObject exponent() {
         return getRuntime().newFixnum(exp());
     }
 
     private int exp() {
         return value.abs().unscaledValue().toString().length() - value.abs().scale();
     }
 
     public IRubyObject finite_p() {
         System.err.println("unimplemented: finite?");
         // TODO: implement correctly
         return getRuntime().getTrue();
     }
 
     public IRubyObject fix() {
         System.err.println("unimplemented: fix");
         // TODO: implement correctly
         return this;
     }
 
-    public IRubyObject floor(RubyInteger arg) {
+    public IRubyObject floor(IRubyObject arg) {
         System.err.println("unimplemented: floor");
         // TODO: implement correctly
         return this;
     }
  
     public IRubyObject frac() {
         System.err.println("unimplemented: frac");
         // TODO: implement correctly
         return this;
     }
 
     public IRubyObject infinite_p() {
         System.err.println("unimplemented: infinite?");
         // TODO: implement correctly
         return getRuntime().getFalse();
     }
 
     public IRubyObject inspect() {
         StringBuffer val = new StringBuffer("#<BigDecimal:").append(Integer.toHexString(System.identityHashCode(this))).append(",");
         val.append("'").append(this.callMethod(getRuntime().getCurrentContext(), "to_s")).append("'").append(",");
         int len = value.abs().unscaledValue().toString().length();
         int pow = len/4;
         val.append(len).append("(").append((pow+1)*4).append(")").append(">");
         return getRuntime().newString(val.toString());
     }
 
     public IRubyObject nan_p() {
         System.err.println("unimplemented: nan?");
         // TODO: implement correctly
         return getRuntime().getFalse();
     }
 
     public IRubyObject nonzero_p() {
         return value.signum() != 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
  
     public IRubyObject precs() {
         System.err.println("unimplemented: precs");
         // TODO: implement
         return getRuntime().getNil();
     }
 
     public IRubyObject remainder(IRubyObject arg) {
         System.err.println("unimplemented: remainder");
         // TODO: implement
         return this;
     }
 
     public IRubyObject round(IRubyObject[] args) {
         System.err.println("unimplemented: round");
         // TODO: implement
         return this;
     }
 
     public IRubyObject sign() {
         System.err.println("unimplemented: sign");
         // TODO: implement correctly
         return getRuntime().newFixnum(value.signum());
     }
 
     public IRubyObject split() {
         System.err.println("unimplemented: split");
         // TODO: implement
         return getRuntime().getNil();
     }
 
     public IRubyObject sqrt(IRubyObject[] args) {
         System.err.println("unimplemented: sqrt");
         // TODO: implement correctly
         return new RubyBigDecimal(getRuntime(),new BigDecimal(Math.sqrt(value.doubleValue()))).setResult();
     }
 
     public IRubyObject to_f() {
         return RubyFloat.newFloat(getRuntime(),value.doubleValue());
     }
 
     public IRubyObject to_i() {
         return RubyNumeric.int2fix(getRuntime(),value.longValue());
     }
 
     public IRubyObject to_int() {
         // TODO: implement to handle infinity and stuff
         return RubyNumeric.int2fix(getRuntime(),value.longValue());
     }
 
     private String removeTrailingZeroes(String in) {
         while(in.length() > 0 && in.charAt(in.length()-1)=='0') {
             in = in.substring(0,in.length()-1);
         }
         return in;
     }
 
     public IRubyObject to_s(IRubyObject[] args) {
         boolean engineering = true;
         boolean pos_sign = false;
         boolean pos_space = false;
         int groups = 0;
 
         if(args.length != 0 && !args[0].isNil()) {
             String format = args[0].toString();
             int start = 0;
             int end = format.length();
             if(format.length() > 0 && format.charAt(0) == '+') {
                 pos_sign = true;
                 start++;
             } else if(format.length() > 0 && format.charAt(0) == ' ') {
                 pos_sign = true;
                 pos_space = true;
                 start++;
             }
             if(format.length() > 0 && format.charAt(format.length()-1) == 'F') {
                 engineering = false;
                 end--;
             } else if(format.length() > 0 && format.charAt(format.length()-1) == 'E') {
                 engineering = true;
                 end--;
             }
             String nums = format.substring(start,end);
             if(nums.length()>0) {
                 groups = Integer.parseInt(nums);
             }
         }
 
         String out = null;
         if(engineering) {
             BigDecimal abs = value.abs();
             String unscaled = abs.unscaledValue().toString();
             int exponent = exp();
             int signum = value.signum();
             StringBuffer build = new StringBuffer();
             build.append(signum == -1 ? "-" : (signum == 1 ? (pos_sign ? (pos_space ? " " : "+" ) : "") : ""));
             build.append("0.");
             if(0 == groups) {
-                build.append(removeTrailingZeroes(unscaled));
+                String s = removeTrailingZeroes(unscaled);
+                if("".equals(s)) {
+                    build.append("0");
+                } else {
+                    build.append(s);
+                }
             } else {
                 int index = 0;
                 String sep = "";
                 while(index < unscaled.length()) {
                     int next = index+groups;
                     if(next > unscaled.length()) {
                         next = unscaled.length();
                     }
                     build.append(sep).append(unscaled.substring(index,next));
                     sep = " ";
                     index += groups;
                 }
             }
             build.append("E").append(exponent);
             out = build.toString();
         } else {
             BigDecimal abs = value.abs();
             String unscaled = abs.unscaledValue().toString();
             int ix = abs.toString().indexOf('.');
             String whole = unscaled;
             String after = null;
             if(ix != -1) {
                 whole = unscaled.substring(0,ix);
                 after = unscaled.substring(ix);
             }
             int signum = value.signum();
             StringBuffer build = new StringBuffer();
             build.append(signum == -1 ? "-" : (signum == 1 ? (pos_sign ? (pos_space ? " " : "+" ) : "") : ""));
             if(0 == groups) {
                 build.append(whole);
                 if(null != after) {
                     build.append(".").append(after);
                 }
             } else {
                 int index = 0;
                 String sep = "";
                 while(index < whole.length()) {
                     int next = index+groups;
                     if(next > whole.length()) {
                         next = whole.length();
                     }
                     build.append(sep).append(whole.substring(index,next));
                     sep = " ";
                     index += groups;
                 }
                 if(null != after) {
                     build.append(".");
                     index = 0;
                     sep = "";
                     while(index < after.length()) {
                         int next = index+groups;
                         if(next > after.length()) {
                             next = after.length();
                         }
                         build.append(sep).append(after.substring(index,next));
                         sep = " ";
                         index += groups;
                     }
                 }
             }
             out = build.toString();
         }
 
         return getRuntime().newString(out);
     }
 
     public IRubyObject truncate(IRubyObject[] args) {
         System.err.println("unimplemented: truncate");
         // TODO: implement
         return this;
     }
 
     public RubyBoolean zero_p() {
         return value.signum() == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 }// RubyBigdecimal
diff --git a/src/org/jruby/RubyFixnum.java b/src/org/jruby/RubyFixnum.java
index 91f54b1137..29e56f3974 100644
--- a/src/org/jruby/RubyFixnum.java
+++ b/src/org/jruby/RubyFixnum.java
@@ -1,463 +1,469 @@
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
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Antti Karanta <antti.karanta@napa.fi>
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
 
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /** Implementation of the Fixnum class.
  *
  * @author jpetersen
  */
 public class RubyFixnum extends RubyInteger {
     private long value;
     private static final int BIT_SIZE = 64;
     public static final long MAX = (1L<<(BIT_SIZE - 1)) - 1;
     public static final long MIN = -1 * MAX - 1;
     private static final long MAX_MARSHAL_FIXNUM = (1L << 30) - 1;
     
     public static final byte OP_PLUS_SWITCHVALUE = 1;
     public static final byte OP_MINUS_SWITCHVALUE = 2;
     public static final byte OP_LT_SWITCHVALUE = 3;
 
     public RubyFixnum(IRuby runtime) {
         this(runtime, 0);
     }
 
     public RubyFixnum(IRuby runtime, long value) {
         super(runtime, runtime.getFixnum());
         this.value = value;
     }
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType) {
         switch (switchvalue) {
             case OP_PLUS_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
                 return op_plus(args[0]);
             case OP_MINUS_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
                 return op_minus(args[0]);
             case OP_LT_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
                 return op_lt(args[0]);
             case 0:
             default:
                 return super.callMethod(context, rubyclass, name, args, callType);
         }
     }
     
     public boolean isImmediate() {
     	return true;
     }
 
     public Class getJavaClass() {
         return Long.TYPE;
     }
 
     public double getDoubleValue() {
         return value;
     }
 
     public long getLongValue() {
         return value;
     }
 
     public static RubyFixnum zero(IRuby runtime) {
         return newFixnum(runtime, 0);
     }
 
     public static RubyFixnum one(IRuby runtime) {
         return newFixnum(runtime, 1);
     }
 
     public static RubyFixnum minus_one(IRuby runtime) {
         return newFixnum(runtime, -1);
     }
 
     protected int compareValue(RubyNumeric other) {
         if (other instanceof RubyBignum) {
             return -other.compareValue(this);
         } else if (other instanceof RubyFloat) {
             final double otherVal = other.getDoubleValue();
             return value > otherVal ? 1 : value < otherVal ? -1 : 0;
         } 
           
         long otherVal = other.getLongValue();
         
         return value > otherVal ? 1 : value < otherVal ? -1 : 0;
     }
 
     public RubyFixnum hash() {
         return newFixnum(hashCode());
     }
     
     public int hashCode() {
         return (int) value ^ (int) (value >> 32);
     }
     
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
 
     // Methods of the Fixnum Class (fix_*):
 
     public static RubyFixnum newFixnum(IRuby runtime, long value) {
         RubyFixnum fixnum;
         RubyFixnum[] fixnumCache = runtime.getFixnumCache();
         
         if (value >= 0 && value < fixnumCache.length) {
             fixnum = fixnumCache[(int) value];
             if (fixnum == null) {
                 fixnum = new RubyFixnum(runtime, value);
                 fixnumCache[(int) value] = fixnum;
             }
         } else {
             fixnum = new RubyFixnum(runtime, value);
         }
         return fixnum;
     }
 
     public RubyFixnum newFixnum(long newValue) {
         return newFixnum(getRuntime(), newValue);
     }
 
     public RubyNumeric multiplyWith(RubyFixnum other) {
         long otherValue = other.getLongValue();
         if (otherValue == 0) {
             return RubyFixnum.zero(getRuntime());
         }
         long result = value * otherValue;
         if (result > MAX || result < MIN || result / otherValue != value) {
             return (RubyNumeric) RubyBignum.newBignum(getRuntime(), getLongValue()).op_mul(other);
         }
 		return newFixnum(result);
     }
 
     public RubyNumeric multiplyWith(RubyInteger other) {
         return other.multiplyWith(this);
     }
 
     public RubyNumeric multiplyWith(RubyFloat other) {
        return other.multiplyWith(RubyFloat.newFloat(getRuntime(), getLongValue()));
     }
 
     public RubyNumeric quo(IRubyObject other) {
         return new RubyFloat(getRuntime(), ((RubyNumeric) op_div(other)).getDoubleValue());
     }
     
     public IRubyObject op_and(IRubyObject other) {
     	if (other instanceof RubyNumeric) {
             return newFixnum(value & ((RubyNumeric) other).getTruncatedLongValue());
     	}
     	
     	return callCoerced("&", other);
     }
 
     public IRubyObject op_div(IRubyObject other) {
         if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_div(other);
         } else if (other instanceof RubyBignum) {
             return RubyBignum.newBignum(getRuntime(), getLongValue()).op_div(other);
+        } else if (other instanceof RubyBigDecimal) {
+            //ugly hack until we support this correctly
         } else if (other instanceof RubyNumeric) {
             // Java / and % are not the same as ruby
             long x = getLongValue();
             long y = ((RubyNumeric) other).getLongValue();
             
             if (y == 0) {
             	throw getRuntime().newZeroDivisionError();
             }
             
             long div = x / y;
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 div -= 1;
             }
 
             return getRuntime().newFixnum(div);
         } 
         
         return callCoerced("/", other);
     }
 
     public IRubyObject op_lshift(IRubyObject other) {
     	if (other instanceof RubyNumeric) {
             long width = ((RubyNumeric) other).getLongValue();
             if (width < 0) {
                 return op_rshift(((RubyNumeric) other).op_uminus());
             }
             if (value > 0) {
                 if (width >= BIT_SIZE - 2 || value >> (BIT_SIZE - width) > 0) {
                     RubyBignum bigValue = 
                         RubyBignum.newBignum(getRuntime(), RubyBignum.bigIntValue(this));
                 
                     return bigValue.op_lshift(other);
 	            }
             } else {
 	            if (width >= BIT_SIZE - 1 || value >> (BIT_SIZE - width) < -1) {
                     RubyBignum bigValue = 
                         RubyBignum.newBignum(getRuntime(), RubyBignum.bigIntValue(this));
                 
                     return bigValue.op_lshift(other);
                 }
             }
 
             return newFixnum(value << width);
     	}
     	
     	return callCoerced("<<", other);
     }
 
     public IRubyObject op_minus(IRubyObject other) {
         if(other instanceof RubyFixnum) {
             long otherValue = ((RubyNumeric) other).getLongValue();
             long result = value - otherValue;
             
             if ((value <= 0 && otherValue >= 0 && (result > 0 || result < -MAX)) || 
                 (value >= 0 && otherValue <= 0 && (result < 0 || result > MAX))) {
                 return RubyBignum.newBignum(getRuntime(), value).op_minus(other);
             }
             
             return newFixnum(result);
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_minus(other);
         } else if (other instanceof RubyBignum) {
             return RubyBignum.newBignum(getRuntime(), value).op_minus(other);
+        } else if (other instanceof RubyBigDecimal) {
+            //ugly hack until we support this correctly
         } else if (other instanceof RubyNumeric) {
             long otherValue = ((RubyNumeric) other).getLongValue();
             long result = value - otherValue;
             
             if ((value <= 0 && otherValue >= 0 && (result > 0 || result < -MAX)) || 
                 (value >= 0 && otherValue <= 0 && (result < 0 || result > MAX))) {
                 return RubyBignum.newBignum(getRuntime(), value).op_minus(other);
             }
             
             return newFixnum(result);
         }
 
         return callCoerced("-", other);        
     }
 
     public IRubyObject op_mod(IRubyObject other) {
         if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_mod(other);
         } else if (other instanceof RubyBignum) {
             return RubyBignum.newBignum(getRuntime(), getLongValue()).op_mod(other);
+        } else if (other instanceof RubyBigDecimal) {
+            //ugly hack until we support this correctly
         } else if (other instanceof RubyNumeric) {
 	        // Java / and % are not the same as ruby
             long x = getLongValue();
             long y = ((RubyNumeric) other).getLongValue();
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 mod += y;
             }
 
             return getRuntime().newFixnum(mod);
         }
         
         return (RubyNumeric) callCoerced("%", other);
     }
 
     public IRubyObject op_mul(IRubyObject other) {
     	if (other instanceof RubyNumeric) {
         	return ((RubyNumeric) other).multiplyWith(this);
     	}
     	
     	return callCoerced("*", other);
     }
 
     public IRubyObject op_or(IRubyObject other) {
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).op_or(this);
         } else if (other instanceof RubyNumeric) {
             return newFixnum(value | ((RubyNumeric) other).getLongValue());
         }
         
         return (RubyInteger) callCoerced("|", other);
     }
 
     public IRubyObject op_plus(IRubyObject other) {
         if(other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum)other).getLongValue();
             long result = value + otherValue;
             if((value < 0 && otherValue < 0 && (result > 0 || result < -MAX)) || 
                 (value > 0 && otherValue > 0 && (result < 0 || result > MAX))) {
                 return RubyBignum.newBignum(getRuntime(), value).op_plus(other);
             }
             return newFixnum(result);
         }
         if(other instanceof RubyBignum) {
             return RubyBignum.newBignum(getRuntime(), value).op_plus(other);
         }
         if(other instanceof RubyFloat) {
             return getRuntime().newFloat(getDoubleValue() + ((RubyFloat)other).getDoubleValue());
         }
         return callCoerced("+", other);
     }
 
     public IRubyObject op_pow(IRubyObject other) {
         if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_pow(other);
         } else if (other instanceof RubyNumeric) {
             long longValue = ((RubyNumeric) other).getLongValue();
             
 		    if (longValue == 0) {
 		        return getRuntime().newFixnum(1);
 		    } else if (longValue == 1) {
 		        return this;
 		    } else if (longValue > 1) {
 		        return RubyBignum.newBignum(getRuntime(), getLongValue()).op_pow(other);
 		    } 
 		      
             return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_pow(other);
         }
         
         return callCoerced("**", other);
     }
 
     public IRubyObject op_rshift(IRubyObject other) {
     	if (other instanceof RubyNumeric) {
             long width = ((RubyNumeric) other).getLongValue();
             if (width < 0) {
 			    return op_lshift(((RubyNumeric) other).op_uminus());
 		    }
             return newFixnum(value >> width);
     	}
     	
     	return callCoerced(">>", other);
     }
 
     public IRubyObject op_xor(IRubyObject other) {
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).op_xor(this);
         } else if (other instanceof RubyNumeric) {
             return newFixnum(value ^ ((RubyNumeric) other).getLongValue());
         }
         
         return callCoerced("^", other);
     }
 
     public RubyString to_s(IRubyObject[] args) {
     	checkArgumentCount(args, 0, 1);
 
     	int radix = args.length == 0 ? 10 : (int) args[0].convertToInteger().getLongValue();
         
         return getRuntime().newString(Long.toString(getLongValue(), radix));
     }
     
     public RubyFloat to_f() {
         return RubyFloat.newFloat(getRuntime(), getDoubleValue());
     }
 
     public RubyFixnum size() {
         return newFixnum((long) Math.ceil(BIT_SIZE / 8.0));
     }
 
     public RubyFixnum aref(IRubyObject other) {
         long position = other.convertToInteger().getLongValue();
 
         // Seems mighty expensive to keep creating over and over again.
         // How else can this be done though?
         if (position > BIT_SIZE) {
             return RubyBignum.newBignum(getRuntime(), value).aref(other);
         }
 
         return newFixnum((value & 1L << position) == 0 ? 0 : 1);
     }
 
     public IRubyObject id2name() {
         String symbol = RubySymbol.getSymbol(getRuntime(), value);
         if (symbol != null) {
             return getRuntime().newString(symbol);
         }
         return getRuntime().getNil();
     }
 
     public RubyFixnum invert() {
         return newFixnum(~value);
     }
 
     public RubyFixnum id() {
         return newFixnum(value * 2 + 1);
     }
 
     public IRubyObject taint() {
         return this;
     }
 
     public IRubyObject freeze() {
         return this;
     }
 
     public IRubyObject times() {
         IRuby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (long i = 0; i < value; i++) {
             context.yield(newFixnum(runtime, i));
         }
         return this;
     }
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         if (value <= MAX_MARSHAL_FIXNUM) {
             output.write('i');
             output.dumpInt((int) value);
         } else {
             output.dumpObject(RubyBignum.newBignum(getRuntime(), value));
         }
     }
 
     public static RubyFixnum unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         return input.getRuntime().newFixnum(input.unmarshalInt());
     }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 50b63e989c..1a04e12f41 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1282 +1,1282 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
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
 
 import org.jruby.ast.Node;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicMethod;
 import org.jruby.runtime.Iter;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.collections.SinglyLinkedList;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyObject implements Cloneable, IRubyObject {
 	
     // The class of this object
     private RubyClass metaClass;
 
     // The instance variables of this object.
     protected Map instanceVariables;
 
     // The two properties frozen and taint
     private boolean frozen;
     private boolean taint;
 
     public RubyObject(IRuby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
 
     public RubyObject(IRuby runtime, RubyClass metaClass, boolean useObjectSpace) {
         this.metaClass = metaClass;
         this.frozen = false;
         this.taint = false;
 
         // Do not store any immediate objects into objectspace.
         if (useObjectSpace && !isImmediate()) {
             runtime.getObjectSpace().add(this);
         }
 
         // FIXME are there objects who shouldn't be tainted?
         // (mri: OBJSETUP)
         taint |= runtime.getSafeLevel() >= 3;
     }
     
     /*
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /**
      * Create a new meta class.
      *
      * @since Ruby 1.6.7
      */
     public MetaClass makeMetaClass(RubyClass type, SinglyLinkedList parentCRef) {
         MetaClass newMetaClass = type.newSingletonClass(parentCRef);
 		
 		if (!isNil()) {
 			setMetaClass(newMetaClass);
 		}
         newMetaClass.attachToObject(this);
         return newMetaClass;
     }
 
     public boolean singletonMethodsAllowed() {
         return true;
     }
 
     public Class getJavaClass() {
         return IRubyObject.class;
     }
     
     public static void puts(Object obj) {
         System.out.println(obj.toString());
     }
 
     /**
      * This method is just a wrapper around the Ruby "==" method,
      * provided so that RubyObjects can be used as keys in the Java
      * HashMap object underlying RubyHash.
      */
     public boolean equals(Object other) {
         return other == this || other instanceof IRubyObject && callMethod(getRuntime().getCurrentContext(), "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return ((RubyString) callMethod(getRuntime().getCurrentContext(), "to_s")).toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public IRuby getRuntime() {
         return metaClass.getRuntime();
     }
 
     public IRubyObject removeInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().remove(name);
     }
 
     /**
      * Returns an unmodifiable snapshot of the current state of instance variables.
      * This method synchronizes access to avoid deadlocks.
      */
     public Map getInstanceVariablesSnapshot() {
         synchronized(getInstanceVariables()) {
             return Collections.unmodifiableMap(new HashMap(getInstanceVariables()));
         }
     }
 
     public Map getInstanceVariables() {
     	// TODO: double checking may or may not be safe enough here
     	if (instanceVariables == null) {
 	    	synchronized (this) {
 	    		if (instanceVariables == null) {
                             instanceVariables = Collections.synchronizedMap(new HashMap());
 	    		}
 	    	}
     	}
         return instanceVariables;
     }
 
     public void setInstanceVariables(Map instanceVariables) {
         this.instanceVariables = Collections.synchronizedMap(instanceVariables);
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public RubyClass getMetaClass() {
         return metaClass;
     }
 
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * Gets the frozen.
      * @return Returns a boolean
      */
     public boolean isFrozen() {
         return frozen;
     }
 
     /**
      * Sets the frozen.
      * @param frozen The frozen to set
      */
     public void setFrozen(boolean frozen) {
         this.frozen = frozen;
     }
 
     /** rb_frozen_class_p
     *
     */
    protected void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message);
        }
    }
 
    protected void checkFrozen() {
        testFrozen("can't modify frozen " + getMetaClass().getName());
    }
 
     /**
      * Gets the taint.
      * @return Returns a boolean
      */
     public boolean isTaint() {
         return taint;
     }
 
     /**
      * Sets the taint.
      * @param taint The taint to set
      */
     public void setTaint(boolean taint) {
         this.taint = taint;
     }
 
     public boolean isNil() {
         return false;
     }
 
     public boolean isTrue() {
         return !isNil();
     }
 
     public boolean isFalse() {
         return isNil();
     }
 
     public boolean respondsTo(String name) {
         return getMetaClass().isMethodBound(name, false);
     }
 
     // Some helper functions:
 
     public int checkArgumentCount(IRubyObject[] args, int min, int max) {
         if (args.length < min) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for " + min + ")");
         }
         if (max > -1 && args.length > max) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for " + max + ")");
         }
         return args.length;
     }
 
     public boolean isKindOf(RubyModule type) {
         return getMetaClass().hasModuleInHierarchy(type);
     }
 
     /** rb_singleton_class
      *
      */
     public MetaClass getSingletonClass() {
         RubyClass type = getMetaClass();
         if (!type.isSingleton()) {
             type = makeMetaClass(type, type.getCRef());
         }
 
         assert type instanceof MetaClass;
 
 		if (!isNil()) {
 			type.setTaint(isTaint());
 			type.setFrozen(isFrozen());
 		}
 
         return (MetaClass)type;
     }
 
     /** rb_define_singleton_method
      *
      */
     public void defineSingletonMethod(String name, Callback method) {
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_singleton_method
      *
      */
     public void defineFastSingletonMethod(String name, Callback method) {
         getSingletonClass().defineFastMethod(name, method);
     }
 
     public void addSingletonMethod(String name, DynamicMethod method) {
         getSingletonClass().addMethod(name, method);
     }
 
     /* rb_init_ccopy */
     public void initCopy(IRubyObject original) {
         assert original != null;
         assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";
 
         setInstanceVariables(new HashMap(original.getInstanceVariables()));
 
         callMethod(getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         setTaint(isTaint() || obj.isTaint());
 
         return this;
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, byte methodIndex, String name,
             IRubyObject[] args, CallType callType) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, getRuntime().getSelectorTable().table[module.index][methodIndex], name, args, callType);
         } else {
             return callMethod(context, module, name, args, callType);
         }
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, name, args, callType);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name,
             IRubyObject[] args, CallType callType) {
         assert args != null;
         DynamicMethod method = null;
 
         method = rubyclass.searchMethod(name);
 
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(context.getFrameSelf(), callType))) {
             if (callType == CallType.SUPER) {
                 throw getRuntime().newNameError("super: no superclass method '" + name + "'", name);
             }
 
             // store call information so method_missing impl can use it
             context.setLastCallStatus(method.getVisibility(), callType);
 
             if (name.equals("method_missing")) {
                 return RubyKernel.method_missing(this, args);
             }
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(getRuntime(), name);
 
             return callMethod(context, "method_missing", newArgs);
         }
 
         RubyModule implementer = null;
         if (method.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             implementer = rubyclass.findImplementer(method.getImplementationClass());
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             implementer = method.getImplementationClass();
         }
 
         String originalName = method.getOriginalName();
         if (originalName != null) {
             name = originalName;
         }
 
         IRubyObject result = method.call(context, this, implementer, name, args, false);
 
         return result;
     }
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY);
     }
 
     /**
      * rb_funcall
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return callMethod(context, name, new IRubyObject[] { arg });
     }
 
     public IRubyObject instance_variable_get(IRubyObject var) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	IRubyObject variable = getInstanceVariable(varName);
 
     	// Pickaxe v2 says no var should show NameError, but ruby only sends back nil..
     	return variable == null ? getRuntime().getNil() : variable;
     }
 
     public IRubyObject getInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().get(name);
     }
 
     public IRubyObject instance_variable_set(IRubyObject var, IRubyObject value) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	return setInstanceVariable(var.asSymbol(), value);
     }
 
     public IRubyObject setInstanceVariable(String name, IRubyObject value,
             String taintError, String freezeError) {
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError(taintError);
         }
         testFrozen(freezeError);
 
         getInstanceVariables().put(name, value);
 
         return value;
     }
 
     /** rb_iv_set / rb_ivar_set
      *
      */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         return setInstanceVariable(name, value,
                 "Insecure: can't modify instance variable", "");
     }
 
     public Iterator instanceVariableNames() {
         return getInstanceVariables().keySet().iterator();
     }
 
     /** rb_eval
      *
      */
     public IRubyObject eval(Node n) {
         //return new EvaluationState(getRuntime(), this).begin(n);
         // need to continue evaluation with a new self, so save the old one (should be a stack?)
         return EvaluationState.eval(getRuntime().getCurrentContext(), n, this);
     }
 
     public void callInit(IRubyObject[] args) {
         ThreadContext tc = getRuntime().getCurrentContext();
 
         tc.setIfBlockAvailable();
         try {
             callMethod(getRuntime().getCurrentContext(), "initialize", args);
         } finally {
             tc.clearIfBlockAvailable();
         }
     }
 
     public void extendObject(RubyModule module) {
         getSingletonClass().includeModule(module);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
     public IRubyObject convertToTypeWithCheck(String targetType, String convertMethod) {
         if (targetType.equals(getMetaClass().getName())) {
             return this;
         }
 
         IRubyObject value = convertToType(targetType, convertMethod, false);
         if (value.isNil()) {
             return value;
         }
 
         if (!targetType.equals(value.getMetaClass().getName())) {
             throw getRuntime().newTypeError(value.getMetaClass().getName() + "#" + convertMethod +
                     "should return " + targetType);
         }
 
         return value;
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(String targetType, String convertMethod, boolean raise) {
         // No need to convert something already of the correct type.
         // XXXEnebo - Could this pass actual class reference instead of String?
         if (targetType.equals(getMetaClass().getName())) {
             return this;
         }
         
         if (!respondsTo(convertMethod)) {
             if (raise) {
                 throw getRuntime().newTypeError(
                     "can't convert " + trueFalseNil(getMetaClass().getName()) + " into " + trueFalseNil(targetType));
             } 
 
             return getRuntime().getNil();
         }
         return callMethod(getRuntime().getCurrentContext(), convertMethod);
     }
 
-    private String trueFalseNil(String v) {
+    protected String trueFalseNil(String v) {
         if("TrueClass".equals(v)) {
             return "true";
         } else if("FalseClass".equals(v)) {
             return "false";
         } else if("NilClass".equals(v)) {
             return "nil";
         }
         return v;
     }
 
     public RubyArray convertToArray() {
         return (RubyArray) convertToType("Array", "to_ary", true);
     }
 
     public RubyFloat convertToFloat() {
         return (RubyFloat) convertToType("Float", "to_f", true);
     }
 
     public RubyInteger convertToInteger() {
         return (RubyInteger) convertToType("Integer", "to_int", true);
     }
 
     public RubyString convertToString() {
         return (RubyString) convertToType("String", "to_str", true);
     }
 
     /** rb_convert_type
      *
      */
     public IRubyObject convertType(Class type, String targetType, String convertMethod) {
         if (type.isAssignableFrom(getClass())) {
             return this;
         }
 
         IRubyObject result = convertToType(targetType, convertMethod, true);
 
         if (!type.isAssignableFrom(result.getClass())) {
             throw getRuntime().newTypeError(
                 getMetaClass().getName() + "#" + convertMethod + " should return " + targetType + ".");
         }
 
         return result;
     }
 
     public void checkSafeString() {
         if (getRuntime().getSafeLevel() > 0 && isTaint()) {
             ThreadContext tc = getRuntime().getCurrentContext();
             if (tc.getFrameLastFunc() != null) {
                 throw getRuntime().newSecurityError("Insecure operation - " + tc.getFrameLastFunc());
             }
             throw getRuntime().newSecurityError("Insecure operation: -r");
         }
         getRuntime().secure(4);
         if (!(this instanceof RubyString)) {
             throw getRuntime().newTypeError(
                 "wrong argument type " + getMetaClass().getName() + " (expected String)");
         }
     }
 
     /** specific_eval
      *
      */
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args) {
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if (tc.isBlockGiven()) {
             if (args.length > 0) {
                 throw getRuntime().newArgumentError(args.length, 0);
             }
             return yieldUnder(mod);
         }
 		if (args.length == 0) {
 		    throw getRuntime().newArgumentError("block not supplied");
 		} else if (args.length > 3) {
 		    String lastFuncName = tc.getFrameLastFunc();
 		    throw getRuntime().newArgumentError(
 		        "wrong # of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
 		}
 		/*
 		if (ruby.getSecurityLevel() >= 4) {
 			Check_Type(argv[0], T_STRING);
 		} else {
 			Check_SafeStr(argv[0]);
 		}
 		*/
         
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         args[0].convertToString();
         
 		IRubyObject file = args.length > 1 ? args[1] : getRuntime().newString("(eval)");
 		IRubyObject line = args.length > 2 ? args[2] : RubyFixnum.one(getRuntime());
 
 		Visibility savedVisibility = tc.getCurrentVisibility();
         tc.setCurrentVisibility(Visibility.PUBLIC);
 		try {
 		    return evalUnder(mod, args[0], file, line);
 		} finally {
             tc.setCurrentVisibility(savedVisibility);
 		}
     }
 
     public IRubyObject evalUnder(RubyModule under, IRubyObject src, IRubyObject file, IRubyObject line) {
         /*
         if (ruby_safe_level >= 4) {
         	Check_Type(src, T_STRING);
         } else {
         	Check_SafeStr(src);
         	}
         */
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // FIXME: lineNumber is not supported
                 //IRubyObject lineNumber = args[3];
 
                 return args[0].evalSimple(source.getRuntime().getCurrentContext(),
                                   source, ((RubyString) filename).toString());
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line });
     }
 
     private IRubyObject yieldUnder(RubyModule under) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Block block = (Block) context.getCurrentBlock();
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield = args[0];
                     IRubyObject selfInYield = args[0];
                     return context.getFrameBlockOrRaise().yield(context, valueInYield, selfInYield, context.getRubyClass(), false);
                     //TODO: Should next and return also catch here?
                 } catch (JumpException je) {
                 	if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 		IRubyObject breakValue = (IRubyObject)je.getPrimaryData();
 
                 		return breakValue == null ? getRuntime().getNil() : breakValue;
                 	} else {
                 		throw je;
                 	}
                 } finally {
                     block.setVisibility(savedVisibility);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this });
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalWithBinding(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, String file) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         assert file != null;
 
         ThreadContext threadContext = getRuntime().getCurrentContext();
 
         ISourcePosition savedPosition = threadContext.getPosition();
         IRubyObject result = getRuntime().getNil();
 
         IRubyObject newSelf = null;
 
         if (!(scope instanceof RubyBinding)) {
             if (scope instanceof RubyProc) {
                 scope = ((RubyProc) scope).binding();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
 
         Block blockOfBinding = ((RubyBinding)scope).getBlock();
         try {
             // Binding provided for scope, use it
             threadContext.preEvalWithBinding(blockOfBinding);
             newSelf = threadContext.getFrameSelf();
 
             result = EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope()), newSelf);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             threadContext.postEvalWithBinding(blockOfBinding);
 
             // restore position
             threadContext.setPosition(savedPosition);
         }
         return result;
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalSimple(org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalSimple(ThreadContext context, IRubyObject src, String file) {
         // this is ensured by the callers
         assert file != null;
 
         ThreadContext threadContext = getRuntime().getCurrentContext();
 
         ISourcePosition savedPosition = threadContext.getPosition();
         // no binding, just eval in "current" frame (caller's frame)
         Iter iter = threadContext.getFrameIter();
         IRubyObject result = getRuntime().getNil();
 
         try {
             // hack to avoid using previous frame if we're the first frame, since this eval is used to start execution too
             if (threadContext.getPreviousFrame() != null) {
                 threadContext.setFrameIter(threadContext.getPreviousFrameIter());
             }
 
             result = EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file, threadContext.getCurrentScope()), this);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             // FIXME: this is broken for Proc, see above
             threadContext.setFrameIter(iter);
 
             // restore position
             threadContext.setPosition(savedPosition);
         }
 
         return result;
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     public IRubyObject equal(IRubyObject obj) {
         if (isNil()) {
             return getRuntime().newBoolean(obj.isNil());
         }
         return getRuntime().newBoolean(this == obj);
     }
 
 	public IRubyObject same(IRubyObject other) {
 		return this == other ? getRuntime().getTrue() : getRuntime().getFalse();
 	}
 
 	public IRubyObject initialize_copy(IRubyObject original) {
 	    if (this != original) {
 	        checkFrozen();
 	        if (!getClass().equals(original.getClass())) {
 	            throw getRuntime().newTypeError("initialize_copy should take same class object");
 	        }
 	    }
 
 	    return this;
 	}
 
     /**
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      */
     public RubyBoolean respond_to(IRubyObject[] args) {
         checkArgumentCount(args, 1, 2);
 
         String name = args[0].asSymbol();
         boolean includePrivate = args.length > 1 ? args[1].isTrue() : false;
 
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate));
     }
 
     /** Return the internal id of an object.
      *
      * <i>CRuby function: rb_obj_id</i>
      *
      */
     public synchronized RubyFixnum id() {
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
     
     public RubyFixnum hash() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
 
     public int hashCode() {
     	return (int) RubyNumeric.fix2long(callMethod(getRuntime().getCurrentContext(), "hash"));
     }
 
     /** rb_obj_type
      *
      */
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn("Object#type is deprecated; use Object#class");
         return type();
     }
 
     /** rb_obj_clone
      *
      */
     public IRubyObject rbClone() {
         IRubyObject clone = doClone();
         clone.setMetaClass(getMetaClass().getSingletonClassClone());
         clone.setTaint(this.isTaint());
         clone.initCopy(this);
         clone.setFrozen(isFrozen());
         return clone;
     }
 
     // Hack: allow RubyModule and RubyClass to override the allocation and return the the correct Java instance
     // Cloning a class object doesn't work otherwise and I don't really understand why --sma
     protected IRubyObject doClone() {
         RubyClass realClass = getMetaClass().getRealClass();
     	return realClass.getAllocator().allocate(getRuntime(), realClass);
     }
 
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(getRuntime().getCurrentContext(), "write", this);
 
         return getRuntime().getNil();
     }
 
     /** rb_obj_dup
      *
      */
     public IRubyObject dup() {
         IRubyObject dup = callMethod(getRuntime().getCurrentContext(), "clone");
         if (!dup.getClass().equals(getClass())) {
             throw getRuntime().newTypeError("duplicated object must be same type");
         }
 
         dup.setMetaClass(type());
         dup.setFrozen(false);
         return dup;
     }
 
     /** rb_obj_tainted
      *
      */
     public RubyBoolean tainted() {
         return getRuntime().newBoolean(isTaint());
     }
 
     /** rb_obj_taint
      *
      */
     public IRubyObject taint() {
         getRuntime().secure(4);
         if (!isTaint()) {
         	testFrozen("object");
             setTaint(true);
         }
         return this;
     }
 
     /** rb_obj_untaint
      *
      */
     public IRubyObject untaint() {
         getRuntime().secure(3);
         if (isTaint()) {
         	testFrozen("object");
             setTaint(false);
         }
         return this;
     }
 
     /** Freeze an object.
      *
      * rb_obj_freeze
      *
      */
     public IRubyObject freeze() {
         if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't freeze object");
         }
         setFrozen(true);
         return this;
     }
 
     /** rb_obj_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen());
     }
 
     /** rb_obj_inspect
      *
      */
     public IRubyObject inspect() {
         if(getInstanceVariables().size() > 0) {
             StringBuffer part = new StringBuffer();
             String cname = getMetaClass().getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(this)));
             if(!getRuntime().registerInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append(" ...>");
                 return getRuntime().newString(part.toString());
             }
             try {
                 String sep = "";
                 Map iVars = getInstanceVariablesSnapshot();
                 for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
                     String name = (String) iter.next();
                     if(IdUtil.isInstanceVariable(name)) {
                         part.append(" ");
                         part.append(sep);
                         part.append(name);
                         part.append("=");
                         part.append(((IRubyObject)(iVars.get(name))).callMethod(getRuntime().getCurrentContext(), "inspect"));
                         sep = ",";
                     }
                 }
                 part.append(">");
                 return getRuntime().newString(part.toString());
             } finally {
                 getRuntime().unregisterInspecting(this);
             }
         }
         return callMethod(getRuntime().getCurrentContext(), "to_s");
     }
 
     /** rb_obj_is_instance_of
      *
      */
     public RubyBoolean instance_of(IRubyObject type) {
         return getRuntime().newBoolean(type() == type);
     }
 
 
     public RubyArray instance_variables() {
         ArrayList names = new ArrayList();
         for(Iterator iter = getInstanceVariablesSnapshot().keySet().iterator();iter.hasNext();) {
             String name = (String) iter.next();
 
             // Do not include constants which also get stored in instance var list in classes.
             if (IdUtil.isInstanceVariable(name)) {
                 names.add(getRuntime().newString(name));
             }
         }
         return getRuntime().newArray(names);
     }
 
     /** rb_obj_is_kind_of
      *
      */
     public RubyBoolean kind_of(IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!type.isKindOf(getRuntime().getClass("Module"))) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw getRuntime().newTypeError(type, getRuntime().getClass("Module"));
         }
 
         return getRuntime().newBoolean(isKindOf((RubyModule)type));
     }
 
     /** rb_obj_methods
      *
      */
     public IRubyObject methods(IRubyObject[] args) {
     	checkArgumentCount(args, 0, 1);
 
     	if (args.length == 0) {
     		args = new IRubyObject[] { getRuntime().getTrue() };
     	}
 
         return getMetaClass().instance_methods(args);
     }
 
 	public IRubyObject public_methods(IRubyObject[] args) {
         return getMetaClass().public_instance_methods(args);
 	}
 
     /** rb_obj_protected_methods
      *
      */
     public IRubyObject protected_methods() {
         return getMetaClass().protected_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_private_methods
      *
      */
     public IRubyObject private_methods() {
         return getMetaClass().private_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_singleton_methods
      *
      */
     // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
     public RubyArray singleton_methods(IRubyObject[] args) {
         boolean all = true;
         if(checkArgumentCount(args,0,1) == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray result = getRuntime().newArray();
 
         for (RubyClass type = getMetaClass(); type != null && ((type instanceof MetaClass) || (all && type.isIncluded()));
              type = type.getSuperClass()) {
         	for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
 
                 // We do not want to capture cached methods
                 if (method.getImplementationClass() != type && !(all && type.isIncluded())) {
                 	continue;
                 }
 
                 RubyString methodName = getRuntime().newString((String) entry.getKey());
                 if (method.getVisibility().isPublic() && ! result.includes(methodName)) {
                     result.append(methodName);
                 }
             }
         }
 
         return result;
     }
 
     public IRubyObject method(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asSymbol(), true);
     }
 
     protected IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     public IRubyObject to_s() {
     	return anyToString();
     }
 
     public IRubyObject instance_eval(IRubyObject[] args) {
         return specificEval(getSingletonClass(), args);
     }
 
     public IRubyObject extend(IRubyObject[] args) {
         checkArgumentCount(args, 1, -1);
 
         // Make sure all arguments are modules before calling the callbacks
         RubyClass module = getRuntime().getClass("Module");
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(module)) {
                 throw getRuntime().newTypeError(args[i], module);
             }
         }
 
         for (int i = 0; i < args.length; i++) {
             args[i].callMethod(getRuntime().getCurrentContext(), "extend_object", this);
             args[i].callMethod(getRuntime().getCurrentContext(), "extended", this);
         }
         return this;
     }
 
     public IRubyObject inherited(IRubyObject arg) {
     	return getRuntime().getNil();
     }
     public IRubyObject initialize(IRubyObject[] args) {
     	return getRuntime().getNil();
     }
 
     public IRubyObject method_missing(IRubyObject[] args) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = callMethod(getRuntime().getCurrentContext(), "inspect").toString();
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = new PrintfFormat(format).sprintf(new Object[] { name, description,
             noClass ? "" : ":", noClass ? "" : getType().getName()});
 
         if (lastCallType == CallType.VARIABLE) {
         	throw getRuntime().newNameError(msg, name);
         }
         throw getRuntime().newNoMethodError(msg, name);
     }
 
     /**
      * send( aSymbol  [, args  ]*   ) -> anObject
      *
      * Invokes the method identified by aSymbol, passing it any arguments
      * specified. You can use __send__ if the name send clashes with an
      * existing method in this object.
      *
      * <pre>
      * class Klass
      *   def hello(*args)
      *     "Hello " + args.join(' ')
      *   end
      * end
      *
      * k = Klass.new
      * k.send :hello, "gentle", "readers"
      * </pre>
      *
      * @return the result of invoking the method identified by aSymbol.
      */
     public IRubyObject send(IRubyObject[] args) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asSymbol();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
         ThreadContext tc = getRuntime().getCurrentContext();
 
         tc.setIfBlockAvailable();
         try {
             return callMethod(getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL);
         } finally {
             tc.clearIfBlockAvailable();
         }
     }
     
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     public IRubyObject match(IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
    public IRubyObject remove_instance_variable(IRubyObject name) {
        String id = name.asSymbol();
 
        if (!IdUtil.isInstanceVariable(id)) {
            throw getRuntime().newNameError("wrong instance variable name " + id, id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw getRuntime().newSecurityError("Insecure: can't remove instance variable");
        }
        testFrozen("class/module");
 
        IRubyObject variable = removeInstanceVariable(id); 
        if (variable != null) {
            return variable;
        }
 
        throw getRuntime().newNameError("instance variable " + id + " not defined", id);
    }
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         output.write('o');
         RubySymbol classname = RubySymbol.newSymbol(getRuntime(), getMetaClass().getName());
         output.dumpObject(classname);
         Map iVars = getInstanceVariablesSnapshot();
         output.dumpInt(iVars.size());
         for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
             String name = (String) iter.next();
             IRubyObject value = (IRubyObject)iVars.get(name);
             
             output.dumpObject(RubySymbol.newSymbol(getRuntime(), name));
             output.dumpObject(value);
         }
     }
    
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#scanArgs()
      */
     public IRubyObject[] scanArgs(IRubyObject[] args, int required, int optional) {
         int total = required+optional;
         int real = checkArgumentCount(args,required,total);
         IRubyObject[] narr = new IRubyObject[total];
         System.arraycopy(args,0,narr,0,real);
         for(int i=real; i<total; i++) {
             narr[i] = getRuntime().getNil();
         }
         return narr;
     }
 
     private transient Object dataStruct;
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct()
      */
     public synchronized void dataWrapStruct(Object obj) {
         this.dataStruct = obj;
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataGetStruct()
      */
     public synchronized Object dataGetStruct() {
         return dataStruct;
     }
 }
diff --git a/test/testBigDecimal.rb b/test/testBigDecimal.rb
index 5a8c4bffcb..d1f5858a13 100644
--- a/test/testBigDecimal.rb
+++ b/test/testBigDecimal.rb
@@ -1,71 +1,94 @@
 require 'test/minirunit'
 test_check "Test BigDecimal"
 
 require 'bigdecimal'
 
 # no singleton methods on bigdecimal
 num = BigDecimal.new("0.001")
 test_exception(TypeError) { class << num ; def amethod ; end ; end }
 test_exception(TypeError) { def num.amethod ; end }
 
 test_ok BigDecimal.new("4")
 test_ok BigDecimal.new("3.14159")
 
 # JRUBY-153 issues
 # Implicit new
 test_ok BigDecimal("4")
 test_ok BigDecimal("3.14159")
 
 # Reject arguments not responding to #to_str
 test_exception(TypeError) { BigDecimal.new(4) }
 test_exception(TypeError) { BigDecimal.new(3.14159) }
 test_exception(TypeError) { BigDecimal(4) }
 test_exception(TypeError) { BigDecimal(3.14159) }
 
 test_equal BigDecimal("0.0"), BigDecimal("XXX")
 
 class X
   def to_str; "3.14159" end
 end
 
 x = X.new
 test_ok BigDecimal.new(x)
 test_ok BigDecimal(x)
 
 require "bigdecimal/newton"
 include Newton
 
 class Function
   def initialize()
     @zero = BigDecimal::new("0.0")
     @one  = BigDecimal::new("1.0")
     @two  = BigDecimal::new("2.0")
     @ten  = BigDecimal::new("10.0")
     @eps  = BigDecimal::new("1.0e-16")
   end
   def zero;@zero;end
   def one ;@one ;end
   def two ;@two ;end
   def ten ;@ten ;end
   def eps ;@eps ;end
   def values(x) # <= defines functions solved
     f = []
     f1 = x[0]*x[0] + x[1]*x[1] - @two # f1 = x**2 + y**2 - 2 => 0
     f2 = x[0] - x[1]                  # f2 = x    - y        => 0
     f <<= f1
     f <<= f2
     f
   end
 end
 f = BigDecimal::limit(100)
 f = Function.new
 x = [f.zero,f.zero]      # Initial values
 n = nlsolve(f,x)
 expected = [BigDecimal('0.1000000000262923315461642086010446338567975310185638386446002778855192224707966221794469725479649528E1'),
             BigDecimal('0.1000000000262923315461642086010446338567975310185638386446002778855192224707966221794469725479649528E1')]
 test_equal expected, x
 
 require "bigdecimal/math.rb"
 include BigMath
 expected = BigDecimal('0.3141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067E1')
 test_equal expected, PI(1000)
+
+one = BigDecimal("1")
+
+test_equal one * 1, one
+test_equal one / 1, one
+test_equal one + 1, BigDecimal("2")
+test_equal one - 1, BigDecimal("0")
+
+test_equal 1*one, one
+test_equal 1/one, one
+test_equal 1+one, BigDecimal("2")
+test_equal 1-one, BigDecimal("0")
+
+test_equal one * 1.0, 1.0
+test_equal one / 1.0, 1.0
+test_equal one + 1.0, 2.0
+test_equal one - 1.0, 0.0
+
+test_equal 1.0*one, 1.0
+test_equal 1.0/one, 1.0
+test_equal 1.0+one, 2.0
+test_equal 1.0-one, 0.0
+
