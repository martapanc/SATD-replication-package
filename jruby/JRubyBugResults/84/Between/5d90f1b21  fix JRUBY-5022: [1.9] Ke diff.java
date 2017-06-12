diff --git a/spec/tags/1.9/ruby/core/string/modulo_tags.txt b/spec/tags/1.9/ruby/core/string/modulo_tags.txt
deleted file mode 100644
index 00646c9477..0000000000
--- a/spec/tags/1.9/ruby/core/string/modulo_tags.txt
+++ /dev/null
@@ -1,6 +0,0 @@
-fails:String#% supports negative bignums with %u or %d
-fails:String#% behaves as if calling Kernel#Float for %e arguments, when the passed argument is hexadecimal string
-fails:String#% behaves as if calling Kernel#Float for %E arguments, when the passed argument is hexadecimal string
-fails:String#% behaves as if calling Kernel#Float for %f arguments, when the passed argument is hexadecimal string
-fails:String#% behaves as if calling Kernel#Float for %g arguments, when the passed argument is hexadecimal string
-fails:String#% behaves as if calling Kernel#Float for %G arguments, when the passed argument is hexadecimal string
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 689c02e92e..ba3a573c29 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1381 +1,1380 @@
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
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodNBlock;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
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
 
     public static abstract class MethodMissingMethod extends JavaMethodNBlock {
         public MethodMissingMethod(RubyModule implementationClass) {
             super(implementationClass, Visibility.PRIVATE, CallConfiguration.FrameFullScopeNone);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             try {
                 preFrameOnly(context, self, name, block);
                 return methodMissing(context, self, clazz, name, args, block);
             } finally {
                 postFrameOnly(context);
             }
         }
 
         public abstract IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block);
 
     }
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         runtime.setKernel(module);
 
         module.defineAnnotatedMethods(RubyKernel.class);
         module.defineAnnotatedMethods(RubyObject.class);
         
         runtime.setRespondToMethod(module.searchMethod("respond_to?"));
         
         module.setFlag(RubyObject.USER7_F, false); //Kernel is the only Module that doesn't need an implementor
 
         runtime.setPrivateMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PRIVATE, CallType.NORMAL, args, block);
             }
         });
 
         runtime.setProtectedMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PROTECTED, CallType.NORMAL, args, block);
             }
         });
 
         runtime.setVariableMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PUBLIC, CallType.VARIABLE, args, block);
             }
         });
 
         runtime.setSuperMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PUBLIC, CallType.SUPER, args, block);
             }
         });
 
         runtime.setNormalMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PUBLIC, CallType.NORMAL, args, block);
             }
         });
 
         if (!runtime.is1_9()) { // method_missing is in BasicObject in 1.9
             runtime.setDefaultMethodMissing(module.searchMethod("method_missing"));
         }
 
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
 
         if (!runtime.is1_9() && !(file instanceof RubyString)) throw runtime.newTypeError(file, runtime.getString());
 
         RubyString fileString = RubyFile.get_path(runtime.getCurrentContext(), file);
         
         if (fileString.isEmpty()) throw runtime.newArgumentError("empty file name");
         
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
 
     @JRubyMethod(name = "method_missing", rest = true, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject method_missing(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Visibility lastVis = context.getLastVisibility();
         CallType lastCallType = context.getLastCallType();
 
         if (args.length == 0 || !(args[0] instanceof RubySymbol)) throw context.getRuntime().newArgumentError("no id given");
 
         return methodMissingDirect(context, recv, (RubySymbol)args[0], lastVis, lastCallType, args, block);
     }
 
     protected static IRubyObject methodMissingDirect(ThreadContext context, IRubyObject recv, RubySymbol symbol, Visibility lastVis, CallType lastCallType, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         
         // create a lightweight thunk
         IRubyObject msg = new RubyNameError.RubyNameErrorMessage(runtime,
                                                                  recv,
                                                                  symbol,
                                                                  lastVis,
                                                                  lastCallType);
         final IRubyObject[]exArgs;
         final RubyClass exc;
         if (lastCallType != CallType.VARIABLE) {
             exc = runtime.getNoMethodError();
             exArgs = new IRubyObject[]{msg, symbol, RubyArray.newArrayNoCopy(runtime, args, 1)};
         } else {
             exc = runtime.getNameError();
             exArgs = new IRubyObject[]{msg, symbol};
         }
 
         throw new RaiseException((RubyException)exc.newInstance(context, exArgs, Block.NULL_BLOCK));
     }
 
     private static IRubyObject methodMissing(ThreadContext context, IRubyObject recv, String name, Visibility lastVis, CallType lastCallType, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         // TODO: pass this in?
         RubySymbol symbol = runtime.newSymbol(name);
 
         // create a lightweight thunk
         IRubyObject msg = new RubyNameError.RubyNameErrorMessage(runtime,
                                                                  recv,
                                                                  symbol,
                                                                  lastVis,
                                                                  lastCallType);
         final IRubyObject[]exArgs;
         final RubyClass exc;
         if (lastCallType != CallType.VARIABLE) {
             exc = runtime.getNoMethodError();
             exArgs = new IRubyObject[]{msg, symbol, RubyArray.newArrayNoCopy(runtime, args)};
         } else {
             exc = runtime.getNameError();
             exArgs = new IRubyObject[]{msg, symbol};
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
         return RubyArgsFile.gets(context, context.getRuntime().getArgsFile(), args);
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject abort(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         if(args.length == 1) {
             runtime.getGlobalVariables().get("$stderr").callMethod(context,"puts",args[0]);
         }
         
         exit(runtime, new IRubyObject[] { runtime.getFalse() }, false);
         return runtime.getNil(); // not reached
     }
 
     @JRubyMethod(name = "Array", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_array(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return RuntimeHelpers.arrayValue(context, context.getRuntime(), object);
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
 
     @JRubyMethod(name = "Float", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return (RubyFloat)object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString) object).getByteList().getRealSize() == 0){ // rb_cstr_to_dbl case
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
 
     @JRubyMethod(name = "Float", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat new_float19(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return (RubyFloat)object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString) object).getByteList().getRealSize() == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
-            return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
+            return RubyNumeric.str2fnum19(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
-            RubyFloat rFloat = (RubyFloat)TypeConverter.convertToType19(object, recv.getRuntime().getFloat(), "to_f");
-            return rFloat;
+            return (RubyFloat)TypeConverter.convertToType19(object, recv.getRuntime().getFloat(), "to_f");
         }
     }
 
     @JRubyMethod(name = "Integer", required = 1, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
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
 
     @JRubyMethod(name = "Integer", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_integer19(ThreadContext context, IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue(); 
             if (val > (double) RubyFixnum.MAX && val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(context.getRuntime(),((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(context.getRuntime(),(RubyString)object,0,true);
         } else if(object instanceof RubyNil) {
             throw context.getRuntime().newTypeError("can't convert nil into Integer");
         }
         
         IRubyObject tmp = TypeConverter.convertToType(object, context.getRuntime().getInteger(), "to_int", false);
         if (tmp.isNil()) return object.convertToInteger("to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "Integer", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_integer19(ThreadContext context, IRubyObject recv, IRubyObject object, IRubyObject base) {
         int bs = RubyNumeric.num2int(base);
         if(object instanceof RubyString) {
             return RubyNumeric.str2inum(context.getRuntime(),(RubyString)object,bs,true);
         } else {
             IRubyObject tmp = object.checkStringType();
             if(!tmp.isNil()) {
                 return RubyNumeric.str2inum(context.getRuntime(),(RubyString)tmp,bs,true);
             }
         }
         throw context.getRuntime().newArgumentError("base specified for non string value");
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject new_string(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType(object, context.getRuntime().getString(), "to_s");
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_string19(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType19(object, context.getRuntime().getString(), "to_s");
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
         if (runtime.is1_9()) {
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
 
     @JRubyMethod(name = "public_method", required = 1, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject public_method(ThreadContext context, IRubyObject recv, IRubyObject symbol) {
         return recv.getMetaClass().newMethod(recv, symbol.asJavaString(), true, PUBLIC);
     }
 
     /** rb_f_putc
      */
     @JRubyMethod(name = "putc", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject putc(ThreadContext context, IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
         
         return RubyIO.putc(context, defout, ch);
     }
 
     @JRubyMethod(name = "puts", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject puts(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
         return RubyIO.puts(context, defout, args);
     }
 
     @JRubyMethod(name = "print", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject print(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
         return RubyIO.print(context, defout, args);
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
         return RubyArgsFile.readlines(context, context.getRuntime().getArgsFile(), args);
     }
 
     @JRubyMethod(name = "respond_to_missing?", module = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject respond_to_missing_p(ThreadContext context, IRubyObject recv, IRubyObject symbol, IRubyObject isPrivate) {
         return context.getRuntime().getFalse();
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(ThreadContext context, Ruby runtime) {
         IRubyObject line = context.getCurrentScope().getLastLine(runtime);
 
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
 
     @JRubyMethod(name = "sub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "sub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, compat = CompatVersion.RUBY1_8)
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
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, arg1, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
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
 
     @JRubyMethod(name = "gsub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "gsub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
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
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, arg1, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chop!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject chop_bang(ThreadContext context, IRubyObject recv, Block block) {
         return getLastlineString(context, context.getRuntime()).chop_bang(context);
     }
 
     @JRubyMethod(name = "chop", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject chop(ThreadContext context, IRubyObject recv, Block block) {
         RubyString str = getLastlineString(context, context.getRuntime());
 
         if (str.getByteList().getRealSize() > 0) {
             str = (RubyString) str.dup();
             str.chop_bang(context);
             context.getCurrentScope().setLastLine(str);
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
 
     @JRubyMethod(name = "chomp!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(context);
     }
 
     @JRubyMethod(name = "chomp!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
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
 
         context.getCurrentScope().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(name = "chomp", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context).isNil()) {
             return str;
         } 
 
         context.getCurrentScope().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(name = "chomp", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context, arg0).isNil()) {
             return str;
         } 
 
         context.getCurrentScope().setLastLine(dup);
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
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = CompatVersion.RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).split(context);
     }
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = CompatVersion.RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0);
     }
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = CompatVersion.RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0, arg1);
     }
 
     @JRubyMethod(name = "scan", required = 1, frame = true, module = true, visibility = PRIVATE, reads = {LASTLINE, BACKREF}, writes = {LASTLINE, BACKREF}, compat = CompatVersion.RUBY1_8)
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
 
         int status = 0;
 
         if (args.length > 0) {
             RubyObject argument = (RubyObject) args[0];
             if (argument instanceof RubyBoolean) {
                 status = argument.isFalse() ? 1 : 0;
             } else {
                 status = RubyNumeric.fix2int(argument);
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
 
     // In 1.9, return symbols
     @JRubyMethod(name = "global_variables", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyArray global_variables19(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         RubyArray globalVariables = runtime.newArray();
 
         for (String globalVariableName : runtime.getGlobalVariables().getNames()) {
             globalVariables.append(runtime.newSymbol(globalVariableName));
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
 
     // In 1.9, return symbols
     @JRubyMethod(name = "local_variables", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyArray local_variables19(ThreadContext context, IRubyObject recv) {
         final Ruby runtime = context.getRuntime();
         RubyArray localVariables = runtime.newArray();
 
         for (String name: context.getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newSymbol(name));
         }
 
         return localVariables;
     }
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyBinding binding(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime(), context.currentBinding(recv));
     }
 
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyBinding binding_1_9(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime(), context.currentBinding());
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
 
         RaiseException raise;
         switch (args.length) {
             case 0:
                 IRubyObject lastException = runtime.getGlobalVariables().get("$!");
                 if (lastException.isNil()) {
                     raise = new RaiseException(runtime, runtime.getRuntimeError(), "", false);
                 } else {
                     // non RubyException value is allowed to be assigned as $!.
                     raise = new RaiseException((RubyException) lastException);
                 }
                 break;
             case 1:
                 if (args[0] instanceof RubyString) {
                     raise = new RaiseException((RubyException) runtime.getRuntimeError().newInstance(context, args, block));
                 } else {
                     raise = new RaiseException(convertToException(runtime, args[0], null));
                 }
                 break;
             default:
                 raise = new RaiseException(convertToException(runtime, args[0], args[1]));
                 if (args.length > 2) {
                     raise.getException().set_backtrace(args[2]);
                 }
                 break;
         }
         if (runtime.getDebug().isTrue()) {
             printExceptionSummary(context, runtime, raise.getException());
         }
         raise.preRaise(context);
         throw raise;
     }
 
     private static RubyException convertToException(Ruby runtime, IRubyObject obj, IRubyObject optionalMessage) {
         if (!obj.respondsTo("exception")) {
             throw runtime.newTypeError("exception class/object expected");
         }
         IRubyObject exception;
         if (optionalMessage == null) {
             exception = obj.callMethod(runtime.getCurrentContext(), "exception");
         } else {
             exception = obj.callMethod(runtime.getCurrentContext(), "exception", optionalMessage);
         }
         try {
             return (RubyException) exception;
         } catch (ClassCastException cce) {
             throw runtime.newTypeError("exception object expected");
         }
     }
 
     private static void printExceptionSummary(ThreadContext context, Ruby runtime, RubyException rEx) {
         Frame currentFrame = context.getCurrentFrame();
 
         String msg = String.format("Exception `%s' at %s:%s - %s\n",
                 rEx.getMetaClass(),
                 currentFrame.getFile(), currentFrame.getLine() + 1,
                 rEx.convertToString().toString());
 
         runtime.getErrorStream().print(msg);
     }
 
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     @JRubyMethod(name = "require", required = 1, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         return requireCommon(recv.getRuntime(), recv, name, block);
     }
 
     @JRubyMethod(name = "require", required = 1, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject require19(ThreadContext context, IRubyObject recv, IRubyObject name, Block block) {
         Ruby runtime = context.getRuntime();
 
         IRubyObject tmp = name.checkStringType();
         if (!tmp.isNil()) {
             return requireCommon(runtime, recv, tmp, block);
         }
 
         return requireCommon(runtime, recv,
                 name.respondsTo("to_path") ? name.callMethod(context, "to_path") : name, block);
     }
 
     private static IRubyObject requireCommon(Ruby runtime, IRubyObject recv, IRubyObject name, Block block) {
         if (runtime.getLoadService().lockAndRequire(name.convertToString().toString())) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = "load", required = 1, optional = 1, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         return loadCommon(args[0], recv.getRuntime(), args, block);
     }
 
     @JRubyMethod(name = "load", required = 1, optional = 1, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject load19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject file = args[0];
         if (!(file instanceof RubyString) && file.respondsTo("to_path")) {
             file = file.callMethod(context, "to_path");
         }
 
         return loadCommon(file, context.getRuntime(), args, block);
     }
 
     private static IRubyObject loadCommon(IRubyObject fileName, Ruby runtime, IRubyObject[] args, Block block) {
         RubyString file = fileName.convertToString();
 
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
 
         boolean bindingGiven = args.length > 1 && !args[1].isNil();
         Binding binding = bindingGiven ? convertToBinding(args[1]) : context.previousBinding();
         if (args.length > 2) {
             // file given, use it and force it into binding
             binding.setFile(args[2].convertToString().toString());
         } else {
             // file not given
             if (bindingGiven) {
                 // binding given, use binding's file
             } else {
                 // no binding given, use (eval)
                 binding.setFile("(eval)");
             }
         }
         if (args.length > 3) {
             // file given, use it and force it into binding
             // -1 because parser uses zero offsets and other code compensates
             binding.setLine(((int) args[3].convertToInteger().getLongValue()) - 1);
         } else {
             // no binding given, use 0 for both
             binding.setLine(0);
         }
         
         return ASTInterpreter.evalWithBinding(context, src, binding);
     }
 
     private static Binding convertToBinding(IRubyObject scope) {
         if (scope instanceof RubyBinding) {
             return ((RubyBinding)scope).getBinding().clone();
         } else {
             if (scope instanceof RubyProc) {
                 return ((RubyProc) scope).getBlock().getBinding().clone();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw scope.getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
     }
 
     @JRubyMethod(name = "callcc", frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject callcc(ThreadContext context, IRubyObject recv, Block block) {
         RubyContinuation continuation = new RubyContinuation(context.getRuntime());
         return continuation.enter(context, block);
     }
 
     public static IRubyObject caller(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw context.getRuntime().newArgumentError("negative level (" + level + ')');
         }
 
         return context.createCallerBacktrace(context.getRuntime(), level);
     }
 
     @JRubyMethod(name = "caller", optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject caller1_9(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw context.getRuntime().newArgumentError("negative level (" + level + ')');
         }
 
         return context.createCallerBacktrace(context.getRuntime(), level);
     }
 
     @JRubyMethod(name = "catch", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject rbCatch(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         RubyContinuation rbContinuation = new RubyContinuation(context.getRuntime(), tag.asJavaString());
         try {
             context.pushCatch(rbContinuation.getContinuation());
             return rbContinuation.enter(context, block);
         } finally {
             context.popCatch();
         }
     }
 
     @JRubyMethod(name = "throw", frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject rbThrow(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         return rbThrowInternal(context, tag.asJavaString(), IRubyObject.NULL_ARRAY, block);
     }
 
     @JRubyMethod(name = "throw", frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject rbThrow(ThreadContext context, IRubyObject recv, IRubyObject tag, IRubyObject arg, Block block) {
         return rbThrowInternal(context, tag.asJavaString(), new IRubyObject[] {arg}, block);
     }
 
     public static IRubyObject rbThrowInternal(ThreadContext context, String tag, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         RubyContinuation.Continuation continuation = context.getActiveCatch(tag.intern());
 
         if (continuation != null) {
             continuation.args = args;
             throw continuation;
         }
 
         // No catch active for this throw
         String message = "uncaught throw `" + tag + "'";
         RubyThread currentThread = context.getThread();
 
         if (currentThread == runtime.getThreadService().getMainThread()) {
             throw runtime.newNameError(message, tag);
         } else {
             throw runtime.newThreadError(message + " in thread 0x" + Integer.toHexString(RubyInteger.fix2int(currentThread.id())));
         }
     }
 
     @JRubyMethod(name = "trap", required = 1, frame = true, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject trap(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         context.getRuntime().getLoadService().require("jsignal_internal");
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
 
     @JRubyMethod(name = "singleton_method_added", required = 1, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject singleton_method_added(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_removed", required = 1, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject singleton_method_removed(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_undefined", required = 1, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject singleton_method_undefined(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "define_singleton_method", required = 1, optional = 1, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject define_singleton_method(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw context.getRuntime().newArgumentError(0, 1);
 
         RubyClass singleton_class = recv.getSingletonClass();
         IRubyObject obj = args.length > 1 ?
             singleton_class.define_method(context, args[0], args[1], block) :
             singleton_class.define_method(context, args[0], block);
         return obj;
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
 
     @JRubyMethod(name = {"loop"}, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject loop_1_9(ThreadContext context, IRubyObject recv, Block block) {
         IRubyObject nil = context.getRuntime().getNil();
         RubyClass stopIteration = context.getRuntime().getStopIteration();
         try {
             while (true) {
                 block.yield(context, nil);
 
                 context.pollThreadEvents();
             }
         } catch (RaiseException ex) {
             if (!stopIteration.op_eqq(context, ex.getException()).isTrue()) {
                 throw ex;
             }
         }
         return nil;
     }
 
     @JRubyMethod(name = "test", required = 2, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject test(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) throw context.getRuntime().newArgumentError("wrong number of arguments");
 
         int cmd;
         if (args[0] instanceof RubyFixnum) {
             cmd = (int)((RubyFixnum) args[0]).getLongValue();
         } else if (args[0] instanceof RubyString &&
                 ((RubyString) args[0]).getByteList().length() > 0) {
             // MRI behavior: use first byte of string value if len > 0
             cmd = ((RubyString) args[0]).getByteList().charAt(0);
         } else {
             cmd = (int) args[0].convertToInteger().getLongValue();
         }
         
         // MRI behavior: raise ArgumentError for 'unknown command' before
         // checking number of args.
         switch(cmd) {
         case 'A': case 'b': case 'c': case 'C': case 'd': case 'e': case 'f': case 'g': case 'G': 
         case 'k': case 'M': case 'l': case 'o': case 'O': case 'p': case 'r': case 'R': case 's':
         case 'S': case 'u': case 'w': case 'W': case 'x': case 'X': case 'z': case '=': case '<':
         case '>': case '-':
             break;
         default:
             throw context.getRuntime().newArgumentError("unknown command ?" + (char) cmd);
         }
 
         // MRI behavior: now check arg count
 
         switch(cmd) {
         case '-': case '=': case '<': case '>':
             if (args.length != 3) throw context.getRuntime().newArgumentError(args.length, 3);
             break;
         default:
             if (args.length != 2) throw context.getRuntime().newArgumentError(args.length, 2);
             break;
         }
         
         switch (cmd) {
         case 'A': // ?A  | Time    | Last access time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).atime();
         case 'b': // ?b  | boolean | True if file1 is a block device
             return RubyFileTest.blockdev_p(recv, args[1]);
         case 'c': // ?c  | boolean | True if file1 is a character device
             return RubyFileTest.chardev_p(recv, args[1]);
         case 'C': // ?C  | Time    | Last change time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).ctime();
         case 'd': // ?d  | boolean | True if file1 exists and is a directory
             return RubyFileTest.directory_p(recv, args[1]);
         case 'e': // ?e  | boolean | True if file1 exists
             return RubyFileTest.exist_p(recv, args[1]);
         case 'f': // ?f  | boolean | True if file1 exists and is a regular file
             return RubyFileTest.file_p(recv, args[1]);
         case 'g': // ?g  | boolean | True if file1 has the \CF{setgid} bit
             return RubyFileTest.setgid_p(recv, args[1]);
         case 'G': // ?G  | boolean | True if file1 exists and has a group ownership equal to the caller's group
             return RubyFileTest.grpowned_p(recv, args[1]);
         case 'k': // ?k  | boolean | True if file1 exists and has the sticky bit set
             return RubyFileTest.sticky_p(recv, args[1]);
         case 'M': // ?M  | Time    | Last modification time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtime();
         case 'l': // ?l  | boolean | True if file1 exists and is a symbolic link
             return RubyFileTest.symlink_p(recv, args[1]);
         case 'o': // ?o  | boolean | True if file1 exists and is owned by the caller's effective uid
             return RubyFileTest.owned_p(recv, args[1]);
         case 'O': // ?O  | boolean | True if file1 exists and is owned by the caller's real uid 
             return RubyFileTest.rowned_p(recv, args[1]);
         case 'p': // ?p  | boolean | True if file1 exists and is a fifo
             return RubyFileTest.pipe_p(recv, args[1]);
         case 'r': // ?r  | boolean | True if file1 is readable by the effective uid/gid of the caller
             return RubyFileTest.readable_p(recv, args[1]);
         case 'R': // ?R  | boolean | True if file is readable by the real uid/gid of the caller
diff --git a/src/org/jruby/RubyNumeric.java b/src/org/jruby/RubyNumeric.java
index 18525492fb..7ab9cc85d3 100644
--- a/src/org/jruby/RubyNumeric.java
+++ b/src/org/jruby/RubyNumeric.java
@@ -1,999 +1,1002 @@
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
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Antti Karanta <Antti.Karanta@napa.fi>
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
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.util.Numeric.f_abs;
 import static org.jruby.util.Numeric.f_arg;
 import static org.jruby.util.Numeric.f_mul;
 import static org.jruby.util.Numeric.f_negative_p;
 
 import java.math.BigInteger;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.ConvertDouble;
 import org.jruby.util.ConvertBytes;
 
 /**
  * Base class for all numerical types in ruby.
  */
 // TODO: Numeric.new works in Ruby and it does here too.  However trying to use
 //   that instance in a numeric operation should generate an ArgumentError. Doing
 //   this seems so pathological I do not see the need to fix this now.
 @JRubyClass(name="Numeric", include="Comparable")
 public class RubyNumeric extends RubyObject {
     
     public static RubyClass createNumericClass(Ruby runtime) {
         RubyClass numeric = runtime.defineClass("Numeric", runtime.getObject(), NUMERIC_ALLOCATOR);
         runtime.setNumeric(numeric);
 
         numeric.index = ClassIndex.NUMERIC;
         numeric.setReifiedClass(RubyNumeric.class);
 
         numeric.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyNumeric;
             }
         };
 
         numeric.includeModule(runtime.getComparable());
         numeric.defineAnnotatedMethods(RubyNumeric.class);
 
         return numeric;
     }
 
     protected static final ObjectAllocator NUMERIC_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyNumeric(runtime, klass);
         }
     };
 
     public static final double DBL_EPSILON=2.2204460492503131e-16;
 
     private static IRubyObject convertToNum(double val, Ruby runtime) {
 
         if (val >= (double) RubyFixnum.MAX || val < (double) RubyFixnum.MIN) {
             return RubyBignum.newBignum(runtime, val);
         }
         return RubyFixnum.newFixnum(runtime, (long) val);
     }
     
     public RubyNumeric(Ruby runtime, RubyClass metaClass) {
         super(runtime, metaClass);
     }
 
     public RubyNumeric(RubyClass metaClass) {
         super(metaClass);
     }
 
     public RubyNumeric(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         super(runtime, metaClass, useObjectSpace);
     }    
 
     public RubyNumeric(Ruby runtime, RubyClass metaClass, boolean useObjectSpace, boolean canBeTainted) {
         super(runtime, metaClass, useObjectSpace, canBeTainted);
     }    
     
     // The implementations of these are all bonus (see TODO above)  I was going
     // to throw an error from these, but it appears to be the wrong place to
     // do it.
     public double getDoubleValue() {
         return 0;
     }
 
     public long getLongValue() {
         return 0;
     }
 
     public BigInteger getBigIntegerValue() {
         return BigInteger.ZERO;
     }
     
     public static RubyNumeric newNumeric(Ruby runtime) {
     	return new RubyNumeric(runtime, runtime.getNumeric());
     }
 
     /*  ================
      *  Utility Methods
      *  ================ 
      */
 
     /** rb_num2int, NUM2INT
      * 
      */
     public static int num2int(IRubyObject arg) {
         long num = num2long(arg);
 
         checkInt(arg, num);
         return (int)num;
     }
     
     /** check_int
      * 
      */
     public static void checkInt(IRubyObject arg, long num){
         if (num < Integer.MIN_VALUE) {
             tooSmall(arg, num);
         } else if (num > Integer.MAX_VALUE) {
             tooBig(arg, num);
         } else {
             return;
         }
     }
     
     private static void tooSmall(IRubyObject arg, long num) {
         throw arg.getRuntime().newRangeError("integer " + num + " too small to convert to `int'");
     }
     
     private static void tooBig(IRubyObject arg, long num) {
         throw arg.getRuntime().newRangeError("integer " + num + " too big to convert to `int'");
     }
 
     /**
      * NUM2CHR
      */
     public static byte num2chr(IRubyObject arg) {
         if (arg instanceof RubyString) {
             String value = ((RubyString) arg).toString();
 
             if (value != null && value.length() > 0) return (byte) value.charAt(0);
         } 
 
         return (byte) num2int(arg);
     }
 
     /** rb_num2long and FIX2LONG (numeric.c)
      * 
      */
     public static long num2long(IRubyObject arg) {
         if (arg instanceof RubyFixnum) {
             return ((RubyFixnum) arg).getLongValue();
         } else {
             return other2long(arg);
         }
     }
 
     private static long other2long(IRubyObject arg) throws RaiseException {
         if (arg.isNil()) {
             throw arg.getRuntime().newTypeError("no implicit conversion from nil to integer");
         } else if (arg instanceof RubyFloat) {
             return float2long((RubyFloat)arg);
         } else if (arg instanceof RubyBignum) {
             return RubyBignum.big2long((RubyBignum) arg);
         }
         return arg.convertToInteger().getLongValue();
     }
     
     private static long float2long(RubyFloat flt) {
         double aFloat = flt.getDoubleValue();
         if (aFloat <= (double) Long.MAX_VALUE && aFloat >= (double) Long.MIN_VALUE) {
             return (long) aFloat;
         } else {
             // TODO: number formatting here, MRI uses "%-.10g", 1.4 API is a must?
             throw flt.getRuntime().newRangeError("float " + aFloat + " out of range of integer");
         }
     }
 
     /** rb_dbl2big + LONG2FIX at once (numeric.c)
      * 
      */
     /** rb_dbl2big + LONG2FIX at once (numeric.c)
      * 
      */
     public static IRubyObject dbl2num(Ruby runtime, double val) {
         if (Double.isInfinite(val)) {
             throw runtime.newFloatDomainError(val < 0 ? "-Infinity" : "Infinity");
         }
         if (Double.isNaN(val)) {
             throw runtime.newFloatDomainError("NaN");
         }
         return convertToNum(val,runtime);
     }
 
     /** rb_num2dbl and NUM2DBL
      * 
      */
     public static double num2dbl(IRubyObject arg) {
         if (arg instanceof RubyFloat) {
             return ((RubyFloat) arg).getDoubleValue();
         } else if (arg instanceof RubyString) {
             throw arg.getRuntime().newTypeError("no implicit conversion to float from string");
         } else if (arg == arg.getRuntime().getNil()) {
             throw arg.getRuntime().newTypeError("no implicit conversion to float from nil");
         }
         return RubyKernel.new_float(arg, arg).getDoubleValue();
     }
 
     /** rb_dbl_cmp (numeric.c)
      * 
      */
     public static IRubyObject dbl_cmp(Ruby runtime, double a, double b) {
         if (Double.isNaN(a) || Double.isNaN(b)) return runtime.getNil();
         return a == b ? RubyFixnum.zero(runtime) : a > b ?
                 RubyFixnum.one(runtime) : RubyFixnum.minus_one(runtime);
     }
 
     public static long fix2long(IRubyObject arg) {
         return ((RubyFixnum) arg).getLongValue();
     }
 
     public static int fix2int(IRubyObject arg) {
         long num = arg instanceof RubyFixnum ? fix2long(arg) : num2long(arg);
         checkInt(arg, num);
         return (int) num;
     }
 
     public static int fix2int(RubyFixnum arg) {
         long num = arg.getLongValue();
         checkInt(arg, num);
         return (int) num;
     }
 
     public static RubyInteger str2inum(Ruby runtime, RubyString str, int base) {
         return str2inum(runtime,str,base,false);
     }
 
     public static RubyNumeric int2fix(Ruby runtime, long val) {
         return RubyFixnum.newFixnum(runtime,val);
     }
 
     /** rb_num2fix
      * 
      */
     public static IRubyObject num2fix(IRubyObject val) {
         if (val instanceof RubyFixnum) {
             return val;
         }
         if (val instanceof RubyBignum) {
             // any BigInteger is bigger than Fixnum and we don't have FIXABLE
             throw val.getRuntime().newRangeError("integer " + val + " out of range of fixnum");
         }
         return RubyFixnum.newFixnum(val.getRuntime(), num2long(val));
     }
 
     /**
      * Converts a string representation of an integer to the integer value. 
      * Parsing starts at the beginning of the string (after leading and 
      * trailing whitespace have been removed), and stops at the end or at the 
      * first character that can't be part of an integer.  Leading signs are
      * allowed. If <code>base</code> is zero, strings that begin with '0[xX]',
      * '0[bB]', or '0' (optionally preceded by a sign) will be treated as hex, 
      * binary, or octal numbers, respectively.  If a non-zero base is given, 
      * only the prefix (if any) that is appropriate to that base will be 
      * parsed correctly.  For example, if the base is zero or 16, the string
      * "0xff" will be converted to 256, but if the base is 10, it will come out 
      * as zero, since 'x' is not a valid decimal digit.  If the string fails 
      * to parse as a number, zero is returned.
      * 
      * @param runtime  the ruby runtime
      * @param str   the string to be converted
      * @param base  the expected base of the number (for example, 2, 8, 10, 16),
      *              or 0 if the method should determine the base automatically 
      *              (defaults to 10). Values 0 and 2-36 are permitted. Any other
      *              value will result in an ArgumentError.
      * @param strict if true, enforce the strict criteria for String encoding of
      *               numeric values, as required by Integer('n'), and raise an
      *               exception when those criteria are not met. Otherwise, allow
      *               lax expression of values, as permitted by String#to_i, and
      *               return a value in almost all cases (excepting illegal radix).
      *               TODO: describe the rules/criteria
      * @return  a RubyFixnum or (if necessary) a RubyBignum representing 
      *          the result of the conversion, which will be zero if the 
      *          conversion failed.
      */
     public static RubyInteger str2inum(Ruby runtime, RubyString str, int base, boolean strict) {
         ByteList s = str.getByteList();
         return ConvertBytes.byteListToInum(runtime, s, base, strict);
     }
 
     public static RubyFloat str2fnum(Ruby runtime, RubyString arg) {
         return str2fnum(runtime,arg,false);
     }
 
     /**
      * Converts a string representation of a floating-point number to the 
      * numeric value.  Parsing starts at the beginning of the string (after 
      * leading and trailing whitespace have been removed), and stops at the 
      * end or at the first character that can't be part of a number.  If 
      * the string fails to parse as a number, 0.0 is returned.
      * 
      * @param runtime  the ruby runtime
      * @param arg   the string to be converted
      * @param strict if true, enforce the strict criteria for String encoding of
      *               numeric values, as required by Float('n'), and raise an
      *               exception when those criteria are not met. Otherwise, allow
      *               lax expression of values, as permitted by String#to_f, and
      *               return a value in all cases.
      *               TODO: describe the rules/criteria
      * @return  a RubyFloat representing the result of the conversion, which
      *          will be 0.0 if the conversion failed.
      */
     public static RubyFloat str2fnum(Ruby runtime, RubyString arg, boolean strict) {
         return str2fnumCommon(runtime, arg, strict, biteListCaller18);
     }
     
     public static RubyFloat str2fnum19(Ruby runtime, RubyString arg, boolean strict) {
+        if (arg.toString().startsWith("0x")) {
+            return ConvertBytes.byteListToInum19(runtime, arg.getByteList(), 16, true).toFloat();
+        }
         return str2fnumCommon(runtime, arg, strict, biteListCaller19);
     }
 
     private static RubyFloat str2fnumCommon(Ruby runtime, RubyString arg, boolean strict, ByteListCaller caller) {
         final double ZERO = 0.0;
         try {
             return new RubyFloat(runtime, caller.yield(arg, strict));
         } catch (NumberFormatException e) {
             if (strict) {
                 throw runtime.newArgumentError("invalid value for Float(): "
                         + arg.callMethod(runtime.getCurrentContext(), "inspect").toString());
             }
             return new RubyFloat(runtime,ZERO);
         }
     }
 
     private static interface ByteListCaller {
         public double yield(RubyString arg, boolean strict);
     }
 
     private static class ByteListCaller18 implements ByteListCaller {
         public double yield(RubyString arg, boolean strict) {
             return ConvertDouble.byteListToDouble(arg.getByteList(),strict);
         }
     }
     private static final ByteListCaller18 biteListCaller18 = new ByteListCaller18();
 
     private static class ByteListCaller19 implements ByteListCaller {
         public double yield(RubyString arg, boolean strict) {
             return ConvertDouble.byteListToDouble19(arg.getByteList(),strict);
         }
     }
     private static final ByteListCaller19 biteListCaller19 = new ByteListCaller19();
 
     
     /** Numeric methods. (num_*)
      *
      */
     
     protected IRubyObject[] getCoerced(ThreadContext context, IRubyObject other, boolean error) {
         IRubyObject result;
         
         try {
             result = other.callMethod(context, "coerce", this);
         } catch (RaiseException e) {
             if (error) {
                 throw getRuntime().newTypeError(
                         other.getMetaClass().getName() + " can't be coerced into " + getMetaClass().getName());
             }
              
             return null;
         }
         
         if (!(result instanceof RubyArray) || ((RubyArray)result).getLength() != 2) {
             throw getRuntime().newTypeError("coerce must return [x, y]");
         }
         
         return ((RubyArray)result).toJavaArray();
     }
 
     protected IRubyObject callCoerced(ThreadContext context, String method, IRubyObject other, boolean err) {
         IRubyObject[] args = getCoerced(context, other, err);
         if(args == null) {
             return getRuntime().getNil();
         }
         return args[0].callMethod(context, method, args[1]);
     }
 
     protected IRubyObject callCoerced(ThreadContext context, String method, IRubyObject other) {
         IRubyObject[] args = getCoerced(context, other, false);
         if(args == null) {
             return getRuntime().getNil();
         }
         return args[0].callMethod(context, method, args[1]);
     }
     
     // beneath are rewritten coercions that reflect MRI logic, the aboves are used only by RubyBigDecimal
 
     /** coerce_body
      *
      */
     protected final IRubyObject coerceBody(ThreadContext context, IRubyObject other) {
         return other.callMethod(context, "coerce", this);
     }
 
     /** do_coerce
      * 
      */
     protected final RubyArray doCoerce(ThreadContext context, IRubyObject other, boolean err) {
         IRubyObject result;
         try {
             result = coerceBody(context, other);
         } catch (RaiseException e) {
             if (err) {
                 throw getRuntime().newTypeError(
                         other.getMetaClass().getName() + " can't be coerced into " + getMetaClass().getName());
             }
             return null;
         }
     
         if (!(result instanceof RubyArray) || ((RubyArray) result).getLength() != 2) {
             throw getRuntime().newTypeError("coerce must return [x, y]");
         }
         return (RubyArray) result;
     }
 
     /** rb_num_coerce_bin
      *  coercion taking two arguments
      */
     protected final IRubyObject coerceBin(ThreadContext context, String method, IRubyObject other) {
         RubyArray ary = doCoerce(context, other, true);
         return (ary.eltInternal(0)).callMethod(context, method, ary.eltInternal(1));
     }
     
     /** rb_num_coerce_cmp
      *  coercion used for comparisons
      */
     protected final IRubyObject coerceCmp(ThreadContext context, String method, IRubyObject other) {
         RubyArray ary = doCoerce(context, other, false);
         if (ary == null) {
             return getRuntime().getNil(); // MRI does it!
         } 
         return (ary.eltInternal(0)).callMethod(context, method, ary.eltInternal(1));
     }
         
     /** rb_num_coerce_relop
      *  coercion used for relative operators
      */
     protected final IRubyObject coerceRelOp(ThreadContext context, String method, IRubyObject other) {
         RubyArray ary = doCoerce(context, other, false);
         if (ary == null) {
             return RubyComparable.cmperr(this, other);
         }
 
         return unwrapCoerced(context, method, other, ary);
     }
     
     private final IRubyObject unwrapCoerced(ThreadContext context, String method, IRubyObject other, RubyArray ary) {
         IRubyObject result = (ary.eltInternal(0)).callMethod(context, method, ary.eltInternal(1));
         if (result.isNil()) {
             return RubyComparable.cmperr(this, other);
         }
         return result;
     }
         
     public RubyNumeric asNumeric() {
         return this;
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** num_sadded
      *
      */
     @JRubyMethod(name = "singleton_method_added")
     public IRubyObject sadded(IRubyObject name) {
         throw getRuntime().newTypeError("can't define singleton method " + name + " for " + getType().getName());
     } 
         
     /** num_init_copy
      *
      */
     @Override
     @JRubyMethod(name = "initialize_copy", visibility = Visibility.PRIVATE)
     public IRubyObject initialize_copy(IRubyObject arg) {
         throw getRuntime().newTypeError("can't copy " + getType().getName());
     }
     
     /** num_coerce
      *
      */
     @JRubyMethod(name = "coerce")
     public IRubyObject coerce(IRubyObject other) {
         if (getMetaClass() == other.getMetaClass()) return getRuntime().newArray(other, this);
 
         IRubyObject cdr = RubyKernel.new_float(this, this);
         IRubyObject car = RubyKernel.new_float(this, other);
 
         return getRuntime().newArray(car, cdr);
     }
 
     /** num_uplus
      *
      */
     @JRubyMethod(name = "+@")
     public IRubyObject op_uplus() {
         return this;
     }
 
     /** num_imaginary
      *
      */
     @JRubyMethod(name = "i", compat = CompatVersion.RUBY1_9)
     public IRubyObject num_imaginary(ThreadContext context) {
         return RubyComplex.newComplexRaw(context.getRuntime(), RubyFixnum.zero(context.getRuntime()), this);
     }
 
     /** num_uminus
      *
      */
     @JRubyMethod(name = "-@")
     public IRubyObject op_uminus(ThreadContext context) {
         RubyArray ary = RubyFixnum.zero(context.getRuntime()).doCoerce(context, this, true);
         return ary.eltInternal(0).callMethod(context, "-", ary.eltInternal(1));
     }
     
     /** num_cmp
      *
      */
     @JRubyMethod(name = "<=>")
     public IRubyObject op_cmp(IRubyObject other) {
         if (this == other) { // won't hurt fixnums
             return RubyFixnum.zero(getRuntime());
         }
         return getRuntime().getNil();
     }
 
     /** num_eql
      *
      */
     @JRubyMethod(name = "eql?")
     public IRubyObject eql_p(ThreadContext context, IRubyObject other) {
         if (getClass() != other.getClass()) return getRuntime().getFalse();
         return equalInternal(context, this, other) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** num_quo
      *
      */
     @JRubyMethod(name = "quo")
     public IRubyObject quo(ThreadContext context, IRubyObject other) {
         return callMethod(context, "/", other);
     }
     
     /** num_quo
     *
     */
     @JRubyMethod(name = "quo", compat = CompatVersion.RUBY1_9)
     public IRubyObject quo_19(ThreadContext context, IRubyObject other) {
         return RubyRational.newRationalRaw(context.getRuntime(), this).callMethod(context, "/", other);
     }
 
     /** num_div
      * 
      */
     @JRubyMethod(name = "div")
     public IRubyObject div(ThreadContext context, IRubyObject other) {
         return callMethod(context, "/", other).convertToFloat().floor();
     }
 
     /** num_divmod
      * 
      */
     @JRubyMethod(name = "divmod")
     public IRubyObject divmod(ThreadContext context, IRubyObject other) {
         return RubyArray.newArray(getRuntime(), div(context, other), modulo(context, other));
     }
     
     /** num_fdiv (1.9) */
     @JRubyMethod(name = "fdiv", compat = CompatVersion.RUBY1_9)
     public IRubyObject fdiv(ThreadContext context, IRubyObject other) {
         return RuntimeHelpers.invoke(context, this.convertToFloat(), "/", other);
     }
 
     /** num_modulo
      *
      */
     @JRubyMethod(name = "modulo")
     public IRubyObject modulo(ThreadContext context, IRubyObject other) {
         return callMethod(context, "%", other);
     }
 
     /** num_remainder
      *
      */
     @JRubyMethod(name = "remainder")
     public IRubyObject remainder(ThreadContext context, IRubyObject dividend) {
         IRubyObject z = callMethod(context, "%", dividend);
         IRubyObject x = this;
         RubyFixnum zero = RubyFixnum.zero(getRuntime());
 
         if (!equalInternal(context, z, zero) &&
                 ((x.callMethod(context, "<", zero).isTrue() &&
                 dividend.callMethod(context, ">", zero).isTrue()) ||
                 (x.callMethod(context, ">", zero).isTrue() &&
                 dividend.callMethod(context, "<", zero).isTrue()))) {
             return z.callMethod(context, "-", dividend);
         } else {
             return z;
         }
     }
 
     /** num_abs
      *
      */
     @JRubyMethod(name = "abs")
     public IRubyObject abs(ThreadContext context) {
         if (callMethod(context, "<", RubyFixnum.zero(getRuntime())).isTrue()) {
             return callMethod(context, "-@");
         }
         return this;
     }
 
     /** num_abs/1.9
      * 
      */
     @JRubyMethod(name = "magnitude", compat = CompatVersion.RUBY1_9)
     public IRubyObject magnitude(ThreadContext context) {
         return abs(context);
     }
 
     /** num_to_int
      * 
      */
     @JRubyMethod(name = "to_int")
     public IRubyObject to_int(ThreadContext context) {
         return RuntimeHelpers.invoke(context, this, "to_i");
     }
 
     /** num_real_p
     *
     */
     @JRubyMethod(name = "real?", compat = CompatVersion.RUBY1_9)
     public IRubyObject scalar_p() {
         return getRuntime().getTrue();
     }
 
     /** num_int_p
      *
      */
     @JRubyMethod(name = "integer?")
     public IRubyObject integer_p() {
         return getRuntime().getFalse();
     }
     
     /** num_zero_p
      *
      */
     @JRubyMethod(name = "zero?")
     public IRubyObject zero_p(ThreadContext context) {
         return equalInternal(context, this, RubyFixnum.zero(getRuntime())) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
     
     /** num_nonzero_p
      *
      */
     @JRubyMethod(name = "nonzero?")
     public IRubyObject nonzero_p(ThreadContext context) {
         if (callMethod(context, "zero?").isTrue()) {
             return getRuntime().getNil();
         }
         return this;
     }
 
     /** num_floor
      *
      */
     @JRubyMethod(name = "floor")
     public IRubyObject floor() {
         return convertToFloat().floor();
     }
         
     /** num_ceil
      *
      */
     @JRubyMethod(name = "ceil")
     public IRubyObject ceil() {
         return convertToFloat().ceil();
     }
 
     /** num_round
      *
      */
     @JRubyMethod(name = "round")
     public IRubyObject round() {
         return convertToFloat().round();
     }
 
     /** num_truncate
      *
      */
     @JRubyMethod(name = "truncate")
     public IRubyObject truncate() {
         return convertToFloat().truncate();
     }
 
     @Deprecated
     public IRubyObject step(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 0: throw context.getRuntime().newArgumentError(0, 1);
         case 1: return step(context, args[0], block);
         case 2: return step(context, args[0], args[1], block);
         default: throw context.getRuntime().newArgumentError(args.length, 2);
         }
     }
 
     public IRubyObject step(ThreadContext context, IRubyObject arg0, Block block) {
         return step(context, arg0, RubyFixnum.one(context.getRuntime()), block);
     }
 
     public IRubyObject step(ThreadContext context, IRubyObject to, IRubyObject step, Block block) {
         Ruby runtime = context.getRuntime();
         if (this instanceof RubyFixnum && to instanceof RubyFixnum && step instanceof RubyFixnum) {
             fixnumStep(context, runtime, ((RubyFixnum)this).getLongValue(),
                                          ((RubyFixnum)to).getLongValue(),
                                          ((RubyFixnum)step).getLongValue(),
                                           block);
         } else if (this instanceof RubyFloat || to instanceof RubyFloat || step instanceof RubyFloat) {
             floatStep(context, runtime, this, to, step, block);
         } else {
             duckStep(context, runtime, this, to, step, block);
         }
         return this;
     }
 
     @JRubyMethod(name = "step", frame = true)
     public IRubyObject step19(ThreadContext context, IRubyObject arg0, Block block) {
         return block.isGiven() ? stepCommon19(context, arg0, RubyFixnum.one(context.getRuntime()), block) : enumeratorize(context.getRuntime(), this, "step", arg0);
     }
 
     @JRubyMethod(name = "step", frame = true)
     public IRubyObject step19(ThreadContext context, IRubyObject to, IRubyObject step, Block block) {
         return block.isGiven() ? stepCommon19(context, to, step, block) : enumeratorize(context.getRuntime(), this, "step", new IRubyObject[] {to, step});
     }
 
     private IRubyObject stepCommon19(ThreadContext context, IRubyObject to, IRubyObject step, Block block) {
         Ruby runtime = context.getRuntime();
         if (this instanceof RubyFixnum && to instanceof RubyFixnum && step instanceof RubyFixnum) {
             fixnumStep(context, runtime, ((RubyFixnum)this).getLongValue(),
                                          ((RubyFixnum)to).getLongValue(),
                                          ((RubyFixnum)step).getLongValue(),
                                           block);
         } else if (this instanceof RubyFloat || to instanceof RubyFloat || step instanceof RubyFloat) {
             floatStep19(context, runtime, this, to, step, false, block);
         } else {
             duckStep(context, runtime, this, to, step, block);
         }
         return this;
     }
 
     private static void fixnumStep(ThreadContext context, Ruby runtime, long value, long end, long diff, Block block) {
         if (diff == 0) throw runtime.newArgumentError("step cannot be 0");
         if (diff > 0) {
             for (long i = value; i <= end; i += diff) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
         } else {
             for (long i = value; i >= end; i += diff) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
         }
     }
 
     protected static void floatStep(ThreadContext context, Ruby runtime, IRubyObject from, IRubyObject to, IRubyObject step, Block block) { 
         double beg = num2dbl(from);
         double end = num2dbl(to);
         double unit = num2dbl(step);
 
         if (unit == 0) throw runtime.newArgumentError("step cannot be 0");
 
         double n = (end - beg)/unit;
         double err = (Math.abs(beg) + Math.abs(end) + Math.abs(end - beg)) / Math.abs(unit) * DBL_EPSILON;
 
         if (err > 0.5) err = 0.5;            
         n = Math.floor(n + err) + 1;
 
         for (long i = 0; i < n; i++) {
             block.yield(context, RubyFloat.newFloat(runtime, i * unit + beg));
         }
     }
 
     static void floatStep19(ThreadContext context, Ruby runtime, IRubyObject from, IRubyObject to, IRubyObject step, boolean excl, Block block) { 
         double beg = num2dbl(from);
         double end = num2dbl(to);
         double unit = num2dbl(step);
 
         // TODO: remove
         if (unit == 0) throw runtime.newArgumentError("step cannot be 0");
 
         double n = (end - beg)/unit;
         double err = (Math.abs(beg) + Math.abs(end) + Math.abs(end - beg)) / Math.abs(unit) * DBL_EPSILON;
 
         if (Double.isInfinite(unit)) {
             if (unit > 0) block.yield(context, RubyFloat.newFloat(runtime, beg));
         } else {
             if (err > 0.5) err = 0.5;            
             n = Math.floor(n + err);
             if (!excl) n++;
             for (long i = 0; i < n; i++){
                 block.yield(context, RubyFloat.newFloat(runtime, i * unit + beg));
             }
         }
     }
 
     private static void duckStep(ThreadContext context, Ruby runtime, IRubyObject from, IRubyObject to, IRubyObject step, Block block) {
         IRubyObject i = from;
         String cmpString = step.callMethod(context, ">", RubyFixnum.zero(runtime)).isTrue() ? ">" : "<";
 
         while (true) {
             if (i.callMethod(context, cmpString, to).isTrue()) break;
             block.yield(context, i);
             i = i.callMethod(context, "+", step);
         }
     }
 
     /** num_equal, doesn't override RubyObject.op_equal
      *
      */
     protected final IRubyObject op_num_equal(ThreadContext context, IRubyObject other) {
         // it won't hurt fixnums
         if (this == other)  return getRuntime().getTrue();
 
         return other.callMethod(context, "==", this);
     }
 
     /** num_numerator
      * 
      */
     @JRubyMethod(name = "numerator", compat = CompatVersion.RUBY1_9)
     public IRubyObject numerator(ThreadContext context) {
         return RubyRational.newRationalConvert(context, this).callMethod(context, "numerator");
     }
     
     /** num_denominator
      * 
      */
     @JRubyMethod(name = "denominator", compat = CompatVersion.RUBY1_9)
     public IRubyObject denominator(ThreadContext context) {
         return RubyRational.newRationalConvert(context, this).callMethod(context, "denominator");
     }
 
     /** numeric_to_c
      * 
      */
     @JRubyMethod(name = "to_c", compat = CompatVersion.RUBY1_9)
     public IRubyObject to_c(ThreadContext context) {
         return RubyComplex.newComplexCanonicalize(context, this);
     }
 
     /** numeric_real
      * 
      */
     @JRubyMethod(name = "real", compat = CompatVersion.RUBY1_9)
     public IRubyObject real(ThreadContext context) {
         return this;
     }
 
     /** numeric_image
      * 
      */
     @JRubyMethod(name = {"imaginary", "imag"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject image(ThreadContext context) {
         return RubyFixnum.zero(context.getRuntime());
     }
 
     /** numeric_abs2
      * 
      */
     @JRubyMethod(name = "abs2", compat = CompatVersion.RUBY1_9)
     public IRubyObject abs2(ThreadContext context) {
         return f_mul(context, this, this);
     }
 
     /** numeric_arg
      * 
      */
     @JRubyMethod(name = {"arg", "angle", "phase"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject arg(ThreadContext context) {
         double value = this.getDoubleValue();
         if (Double.isNaN(value)) {
             return RubyFloat.newFloat(context.getRuntime(), Double.NaN);
         }
         if (f_negative_p(context, this) || (value == 0.0 && 1/value == Double.NEGATIVE_INFINITY)) {
             // negative or -0.0
             return context.getRuntime().getMath().fastGetConstant("PI");
         }
         return RubyFixnum.zero(context.getRuntime());
     }    
 
     /** numeric_rect
      * 
      */
     @JRubyMethod(name = {"rectangular", "rect"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject rect(ThreadContext context) {
         return context.getRuntime().newArray(this, RubyFixnum.zero(context.getRuntime()));
     }    
 
     /** numeric_polar
      * 
      */
     @JRubyMethod(name = "polar", compat = CompatVersion.RUBY1_9)
     public IRubyObject polar(ThreadContext context) {
         return context.getRuntime().newArray(f_abs(context, this), f_arg(context, this));
     }    
 
     /** numeric_real
      * 
      */
     @JRubyMethod(name = {"conjugate", "conj"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject conjugate(ThreadContext context) {
         return this;
     }
 
     @Override
     public Object toJava(Class target) {
         return JavaUtil.getNumericConverter(target).coerce(this, target);
     }
 
     public static class InvalidIntegerException extends NumberFormatException {
         private static final long serialVersionUID = 55019452543252148L;
         
         public InvalidIntegerException() {
             super();
         }
         public InvalidIntegerException(String message) {
             super(message);
         }
         @Override
         public Throwable fillInStackTrace() {
             return this;
         }
     }
     
     public static class NumberTooLargeException extends NumberFormatException {
         private static final long serialVersionUID = -1835120694982699449L;
         public NumberTooLargeException() {
             super();
         }
         public NumberTooLargeException(String message) {
             super(message);
         }
         @Override
         public Throwable fillInStackTrace() {
             return this;
         }
     }
 }
diff --git a/src/org/jruby/util/Sprintf.java b/src/org/jruby/util/Sprintf.java
index 3f4154131a..222d5750a5 100644
--- a/src/org/jruby/util/Sprintf.java
+++ b/src/org/jruby/util/Sprintf.java
@@ -1,1432 +1,1436 @@
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
 package org.jruby.util;
 
 import java.math.BigInteger;
 import java.text.DecimalFormatSymbols;
 import java.util.Locale;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyInteger;
 import org.jruby.RubyKernel;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.builtin.IRubyObject;
 
 
 /**
  * @author Bill Dortch
  *
  */
 public class Sprintf {
     private static final int FLAG_NONE        = 0;
     private static final int FLAG_SPACE       = 1 << 0;
     private static final int FLAG_ZERO        = 1 << 1;
     private static final int FLAG_PLUS        = 1 << 2;
     private static final int FLAG_MINUS       = 1 << 3;
     private static final int FLAG_SHARP       = 1 << 4;
     private static final int FLAG_WIDTH       = 1 << 5;
     private static final int FLAG_PRECISION   = 1 << 6;
     
     private static final byte[] PREFIX_OCTAL     = {'0'};
     private static final byte[] PREFIX_HEX_LC    = {'0','x'};
     private static final byte[] PREFIX_HEX_UC    = {'0','X'};
     private static final byte[] PREFIX_BINARY_LC = {'0','b'};
     private static final byte[] PREFIX_BINARY_UC = {'0','B'};
     
     private static final byte[] PREFIX_NEGATIVE = {'.','.'};
     
     private static final byte[] NAN_VALUE       = {'N','a','N'};
     private static final byte[] INFINITY_VALUE  = {'I','n','f'};
        
     private static final BigInteger BIG_32 = BigInteger.valueOf(((long)Integer.MAX_VALUE + 1L) << 1);
     private static final BigInteger BIG_64 = BIG_32.shiftLeft(32);
     private static final BigInteger BIG_MINUS_32 = BigInteger.valueOf((long)Integer.MIN_VALUE << 1);
     private static final BigInteger BIG_MINUS_64 = BIG_MINUS_32.shiftLeft(32);
 
     private static final String ERR_MALFORMED_FORMAT = "malformed format string";
     private static final String ERR_MALFORMED_NUM = "malformed format string - %[0-9]";
     private static final String ERR_MALFORMED_DOT_NUM = "malformed format string - %.[0-9]";
     private static final String ERR_MALFORMED_STAR_NUM = "malformed format string - %*[0-9]";
     private static final String ERR_ILLEGAL_FORMAT_CHAR = "illegal format character - %";
     
     
     private static final class Args {
         private final Ruby runtime;
         private final Locale locale;
         private final IRubyObject rubyObject;
         private final RubyArray rubyArray;
         private final int length;
         private int unnumbered; // last index (+1) accessed by next()
         private int numbered;   // last index (+1) accessed by get()
         
         Args(Locale locale, IRubyObject rubyObject) {
             if (rubyObject == null) throw new IllegalArgumentException("null IRubyObject passed to sprintf");
             this.locale = locale == null ? Locale.getDefault() : locale;
             this.rubyObject = rubyObject;
             if (rubyObject instanceof RubyArray) {
                 this.rubyArray = ((RubyArray)rubyObject);
                 this.length = rubyArray.size();
             } else {
                 this.length = 1;
                 this.rubyArray = null;
             }
             this.runtime = rubyObject.getRuntime();
         }
         
         Args(IRubyObject rubyObject) {
             this(Locale.getDefault(),rubyObject);
         }
 
         // temporary hack to handle non-Ruby values
         // will come up with better solution shortly
         Args(Ruby runtime, long value) {
             this(RubyFixnum.newFixnum(runtime,value));
         }
         
         void raiseArgumentError(String message) {
             throw runtime.newArgumentError(message);
         }
         
         void warn(ID id, String message) {
             runtime.getWarnings().warn(id, message);
         }
         
         void warning(ID id, String message) {
             if (runtime.isVerbose()) runtime.getWarnings().warning(id, message);
         }
         
         IRubyObject next() {
             // this is the order in which MRI does these two tests
             if (numbered > 0) raiseArgumentError("unnumbered" + (unnumbered + 1) + "mixed with numbered");
             if (unnumbered >= length) raiseArgumentError("too few arguments");
             IRubyObject object = rubyArray == null ? rubyObject : rubyArray.eltInternal(unnumbered);
             unnumbered++;
             return object;
         }
         
         IRubyObject get(int index) {
             // this is the order in which MRI does these tests
             if (unnumbered > 0) raiseArgumentError("numbered("+numbered+") after unnumbered("+unnumbered+")");
             if (index < 0) raiseArgumentError("invalid index - " + (index + 1) + '$');
             if (index >= length) raiseArgumentError("too few arguments");
             numbered = index + 1;
             return rubyArray == null ? rubyObject : rubyArray.eltInternal(index);
         }
         
         IRubyObject getNth(int formatIndex) {
             return get(formatIndex - 1);
         }
         
         int nextInt() {
             return intValue(next());
         }
         
         int getInt(int index) {
             return intValue(get(index));
         }
 
         int getNthInt(int formatIndex) {
             return intValue(get(formatIndex - 1));
         }
         
         int intValue(IRubyObject obj) {
             if (obj instanceof RubyNumeric) return (int)((RubyNumeric)obj).getLongValue();
 
             // basically just forcing a TypeError here to match MRI
             obj = TypeConverter.convertToType(obj, obj.getRuntime().getFixnum(), "to_int", true);
             return (int)((RubyFixnum)obj).getLongValue();
         }
         
         byte getDecimalSeparator() {
             // not saving DFS instance, as it will only be used once (at most) per call
             return (byte)new DecimalFormatSymbols(locale).getDecimalSeparator();
         }
     } // Args
 
     // static methods only
     private Sprintf () {}
     
     // Special form of sprintf that returns a RubyString and handles
     // tainted strings correctly.
     public static boolean sprintf(ByteList to, Locale locale, CharSequence format, IRubyObject args) {
         return rubySprintfToBuffer(to, format, new Args(locale, args));
     }
 
     // Special form of sprintf that returns a RubyString and handles
     // tainted strings correctly. Version for 1.9.
     public static boolean sprintf1_9(ByteList to, Locale locale, CharSequence format, IRubyObject args) {
         return rubySprintfToBuffer(to, format, new Args(locale, args), false);
     }
 
     public static boolean sprintf(ByteList to, CharSequence format, IRubyObject args) {
         return rubySprintf(to, format, new Args(args));
     }
 
     public static boolean sprintf(Ruby runtime, ByteList to, CharSequence format, int arg) {
         return rubySprintf(to, format, new Args(runtime, (long)arg));
     }
 
     public static boolean sprintf(ByteList to, RubyString format, IRubyObject args) {
         return rubySprintf(to, format.getByteList(), new Args(args));
     }
 
     private static boolean rubySprintf(ByteList to, CharSequence charFormat, Args args) {
         return rubySprintfToBuffer(to, charFormat, args);
     }
 
     private static boolean rubySprintfToBuffer(ByteList buf, CharSequence charFormat, Args args) {
         return rubySprintfToBuffer(buf, charFormat, args, true);
     }
 
     private static boolean rubySprintfToBuffer(ByteList buf, CharSequence charFormat, Args args, boolean usePrefixForZero) {
         boolean tainted = false;
         final byte[] format;
 
         int offset;
         int length;
         int start;
         int mark;        
 
         if (charFormat instanceof ByteList) {
             ByteList list = (ByteList)charFormat;
             format = list.getUnsafeBytes();
             int begin = list.begin(); 
             offset = begin;
             length = begin + list.length();
             start = begin;
             mark = begin;
         } else {
             format = stringToBytes(charFormat, false);
             offset = 0;
             length = charFormat.length();
             start = 0;
             mark = 0;             
         }
 
         while (offset < length) {
             start = offset;
             for ( ; offset < length && format[offset] != '%'; offset++) ;
 
             if (offset > start) {
                 buf.append(format,start,offset-start);
                 start = offset;
             }
             if (offset++ >= length) break;
 
             IRubyObject arg = null;
             int flags = 0;
             int width = 0;
             int precision = 0;
             int number = 0;
             byte fchar = 0;
             boolean incomplete = true;
             for ( ; incomplete && offset < length ; ) {
                 switch (fchar = format[offset]) {
                 default:
                     if (fchar == '\0' && flags == FLAG_NONE) {
                         // MRI 1.8.6 behavior: null byte after '%'
                         // leads to "%" string. Null byte in
                         // other places, like "%5\0", leads to error.
                         buf.append('%');
                         buf.append(fchar);
                         incomplete = false;
                         offset++;
                         break;
                     } else if (isPrintable(fchar)) {
                         raiseArgumentError(args,"malformed format string - %" + (char)fchar);
                     } else {
                         raiseArgumentError(args,ERR_MALFORMED_FORMAT);
                     }
                     break;
 
                 case ' ':
                     flags |= FLAG_SPACE;
                     offset++;
                     break;
                 case '0':
                     flags |= FLAG_ZERO;
                     offset++;
                     break;
                 case '+':
                     flags |= FLAG_PLUS;
                     offset++;
                     break;
                 case '-':
                     flags |= FLAG_MINUS;
                     offset++;
                     break;
                 case '#':
                     flags |= FLAG_SHARP;
                     offset++;
                     break;
                 case '1':case '2':case '3':case '4':case '5':
                 case '6':case '7':case '8':case '9':
                     // MRI doesn't flag it as an error if width is given multiple
                     // times as a number (but it does for *)
                     number = 0;
                     for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                         number = extendWidth(args, number, fchar);
                     }
                     checkOffset(args,offset,length,ERR_MALFORMED_NUM);
                     if (fchar == '$') {
                         if (arg != null) {
                             raiseArgumentError(args,"value given twice - " + number + "$");
                         }
                         arg = args.getNth(number);
                         offset++;
                     } else {
                         width = number;
                         flags |= FLAG_WIDTH;
                     }
                     break;
                 
                 case '*':
                     if ((flags & FLAG_WIDTH) != 0) {
                         raiseArgumentError(args,"width given twice");
                     }
                     flags |= FLAG_WIDTH;
                     // TODO: factor this chunk as in MRI/YARV GETASTER
                     checkOffset(args,++offset,length,ERR_MALFORMED_STAR_NUM);
                     mark = offset;
                     number = 0;
                     for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                         number = extendWidth(args,number,fchar);
                     }
                     checkOffset(args,offset,length,ERR_MALFORMED_STAR_NUM);
                     if (fchar == '$') {
                         width = args.getNthInt(number);
                         if (width < 0) {
                             flags |= FLAG_MINUS;
                             width = -width;
                         }
                         offset++;
                     } else {
                         width = args.nextInt();
                         if (width < 0) {
                             flags |= FLAG_MINUS;
                             width = -width;
                         }
                         // let the width (if any), get processed in the next loop,
                         // so any leading 0 gets treated correctly 
                         offset = mark;
                     }
                     break;
                 
                 case '.':
                     if ((flags & FLAG_PRECISION) != 0) {
                         raiseArgumentError(args,"precision given twice");
                     }
                     flags |= FLAG_PRECISION;
                     checkOffset(args,++offset,length,ERR_MALFORMED_DOT_NUM);
                     fchar = format[offset];
                     if (fchar == '*') {
                         // TODO: factor this chunk as in MRI/YARV GETASTER
                         checkOffset(args,++offset,length,ERR_MALFORMED_STAR_NUM);
                         mark = offset;
                         number = 0;
                         for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                             number = extendWidth(args,number,fchar);
                         }
                         checkOffset(args,offset,length,ERR_MALFORMED_STAR_NUM);
                         if (fchar == '$') {
                             precision = args.getNthInt(number);
                             if (precision < 0) {
                                 flags &= ~FLAG_PRECISION;
                             }
                             offset++;
                         } else {
                             precision = args.nextInt();
                             if (precision < 0) {
                                 flags &= ~FLAG_PRECISION;
                             }
                             // let the width (if any), get processed in the next loop,
                             // so any leading 0 gets treated correctly 
                             offset = mark;
                         }
                     } else {
                         number = 0;
                         for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                             number = extendWidth(args,number,fchar);
                         }
                         checkOffset(args,offset,length,ERR_MALFORMED_DOT_NUM);
                         precision = number;
                     }
                     break;
 
                 case '\n':
                     offset--;
                 case '%':
                     if (flags != FLAG_NONE) {
                         raiseArgumentError(args,ERR_ILLEGAL_FORMAT_CHAR);
                     }
                     buf.append('%');
                     offset++;
                     incomplete = false;
                     break;
 
                 case 'c': {
                     if (arg == null) arg = args.next();
                     
                     int c = 0;
                     // MRI 1.8.5-p12 doesn't support 1-char strings, but
                     // YARV 0.4.1 does. I don't think it hurts to include
                     // this; sprintf('%c','a') is nicer than sprintf('%c','a'[0])
                     if (arg instanceof RubyString) {
                         ByteList bytes = ((RubyString)arg).getByteList();
                         if (bytes.length() == 1) {
                             c = bytes.getUnsafeBytes()[bytes.begin()];
                         } else {
                             raiseArgumentError(args,"%c requires a character");
                         }
                     } else {
                         c = args.intValue(arg);
                     }
                     if ((flags & FLAG_WIDTH) != 0 && width > 1) {
                         if ((flags & FLAG_MINUS) != 0) {
                             buf.append(c);
                             buf.fill(' ', width-1);
                         } else {
                             buf.fill(' ',width-1);
                             buf.append(c);
                         }
                     } else {
                         buf.append(c);
                     }
                     offset++;
                     incomplete = false;
                     break;
                 }
                 case 'p':
                 case 's': {
                     if (arg == null) arg = args.next();
 
                     if (fchar == 'p') {
                         arg = arg.callMethod(arg.getRuntime().getCurrentContext(),"inspect");
                     }
                     ByteList bytes = arg.asString().getByteList();
                     int len = bytes.length();
                     if (arg.isTaint()) tainted = true;
                     if ((flags & FLAG_PRECISION) != 0 && precision < len) {
                         len = precision;
                     }
                     // TODO: adjust length so it won't fall in the middle 
                     // of a multi-byte character. MRI's sprintf.c uses tables
                     // in a modified version of regex.c, which assume some
                     // particular  encoding for a given installation/application.
                     // (See regex.c#re_mbcinit in ruby-1.8.5-p12) 
                     //
                     // This is only an issue if the user specifies a precision
                     // that causes the string to be truncated. The same issue
                     // would arise taking a substring of a ByteList-backed RubyString.
 
                     if ((flags & FLAG_WIDTH) != 0 && width > len) {
                         width -= len;
                         if ((flags & FLAG_MINUS) != 0) {
                             buf.append(bytes.getUnsafeBytes(),bytes.begin(),len);
                             buf.fill(' ',width);
                         } else {
                             buf.fill(' ',width);
                             buf.append(bytes.getUnsafeBytes(),bytes.begin(),len);
                         }
                     } else {
                         buf.append(bytes.getUnsafeBytes(),bytes.begin(),len);
                     }
                     offset++;
                     incomplete = false;
                     break;
                 }
                 case 'd':
                 case 'i':
                 case 'o':
                 case 'x':
                 case 'X':
                 case 'b':
                 case 'B':
                 case 'u': {
                     if (arg == null) arg = args.next();
 
                     int type = arg.getMetaClass().index;
                     if (type != ClassIndex.FIXNUM && type != ClassIndex.BIGNUM) {
                         switch(type) {
                         case ClassIndex.FLOAT:
                             arg = RubyNumeric.dbl2num(arg.getRuntime(),((RubyFloat)arg).getValue());
                             break;
                         case ClassIndex.STRING:
                             arg = ((RubyString)arg).stringToInum(0, true);
                             break;
                         default:
                             if (arg.respondsTo("to_int")) {
                                 arg = TypeConverter.convertToType(arg, arg.getRuntime().getInteger(), "to_int", true);
                             } else {
                                 arg = TypeConverter.convertToType(arg, arg.getRuntime().getInteger(), "to_i", true);
                             }
                             break;
                         }
                         type = arg.getMetaClass().index;
                     }
                     byte[] bytes = null;
                     int first = 0;
                     byte[] prefix = null;
                     boolean sign;
                     boolean negative;
                     byte signChar = 0;
                     byte leadChar = 0;
                     int base;
 
                     // 'd' and 'i' are the same
                     if (fchar == 'i') fchar = 'd';
 
                     // 'u' with space or plus flags is same as 'd'
                     if (fchar == 'u' && (flags & (FLAG_SPACE | FLAG_PLUS)) != 0) {
                         fchar = 'd';
                     }
                     sign = (fchar == 'd' || (flags & (FLAG_SPACE | FLAG_PLUS)) != 0);
 
                     switch (fchar) {
                     case 'o':
                         base = 8; break;
                     case 'x':
                     case 'X':
                         base = 16; break;
                     case 'b':
                     case 'B':
                         base = 2; break;
                     case 'u':
                     case 'd':
                     default:
                         base = 10; break;
                     }
                     // We depart here from strict adherence to MRI code, as MRI
                     // uses C-sprintf, in part, to format numeric output, while
                     // we'll use Java's numeric formatting code (and our own).
                     boolean zero;
                     if (type == ClassIndex.FIXNUM) {
                         negative = ((RubyFixnum)arg).getLongValue() < 0;
                         zero = ((RubyFixnum)arg).getLongValue() == 0;
                         if (negative && fchar == 'u') {
                             bytes = getUnsignedNegativeBytes((RubyFixnum)arg);
                         } else {
                             bytes = getFixnumBytes((RubyFixnum)arg,base,sign,fchar=='X');
                         }
                     } else {
                         negative = ((RubyBignum)arg).getValue().signum() < 0;
                         zero = ((RubyBignum)arg).getValue().equals(BigInteger.ZERO);
-                        if (negative && fchar == 'u') {
+                        if (negative && fchar == 'u' && usePrefixForZero) {
                             bytes = getUnsignedNegativeBytes((RubyBignum)arg);
                         } else {
                             bytes = getBignumBytes((RubyBignum)arg,base,sign,fchar=='X');
                         }
                     }
                     if ((flags & FLAG_SHARP) != 0) {
                         if (!zero || usePrefixForZero) {
                             switch (fchar) {
                             case 'o': prefix = PREFIX_OCTAL; break;
                             case 'x': prefix = PREFIX_HEX_LC; break;
                             case 'X': prefix = PREFIX_HEX_UC; break;
                             case 'b': prefix = PREFIX_BINARY_LC; break;
                             case 'B': prefix = PREFIX_BINARY_UC; break;
                             }
                         }
                         if (prefix != null) width -= prefix.length;
                     }
                     int len = 0;
                     if (sign) {
                         if (negative) {
                             signChar = '-';
                             width--;
                             first = 1; // skip '-' in bytes, will add where appropriate
                         } else if ((flags & FLAG_PLUS) != 0) {
                             signChar = '+';
                             width--;
                         } else if ((flags & FLAG_SPACE) != 0) {
                             signChar = ' ';
                             width--;
                         }
                     } else if (negative) {
                         if (base == 10) {
                             warning(ID.NEGATIVE_NUMBER_FOR_U, args, "negative number for %u specifier");
                             leadChar = '.';
                             len += 2;
                         } else {
                             if ((flags & (FLAG_PRECISION | FLAG_ZERO)) == 0) len += 2; // ..
 
                             first = skipSignBits(bytes,base);
                             switch(fchar) {
                             case 'b':
                             case 'B':
                                 leadChar = '1';
                                 break;
                             case 'o':
                                 leadChar = '7';
                                 break;
                             case 'x':
                                 leadChar = 'f';
                                 break;
                             case 'X':
                                 leadChar = 'F';
                                 break;
                             }
                             if (leadChar != 0) len++;
                         }
                     }
                     int numlen = bytes.length - first;
                     len += numlen;
                     
                     if ((flags & (FLAG_ZERO|FLAG_PRECISION)) == FLAG_ZERO) {
                         precision = width;
                         width = 0;
                     } else {
                         if (precision < len) precision = len;
 
                         width -= precision;
                     }
                     if ((flags & FLAG_MINUS) == 0) {
                         buf.fill(' ',width);
                         width = 0;
                     }
                     if (signChar != 0) buf.append(signChar);
                     if (prefix != null) buf.append(prefix);
 
                     if (len < precision) {
                         if (leadChar == 0) {
                             if (fchar != 'd' || usePrefixForZero || !negative ||
                                     ((flags & FLAG_ZERO) != 0 && (flags & FLAG_MINUS) == 0)) {
                                 buf.fill('0', precision - len);
                             }
                         } else if (leadChar == '.') {
                             buf.fill(leadChar,precision-len);
                             buf.append(PREFIX_NEGATIVE);
                         } else if (!usePrefixForZero) {
                             buf.append(PREFIX_NEGATIVE);
                             buf.fill(leadChar,precision - len - 1);
                         } else {
                             buf.fill(leadChar,precision-len+1); // the 1 is for the stripped sign char
                         }
                     } else if (leadChar != 0) {
-                        if ((flags & (FLAG_PRECISION | FLAG_ZERO)) == 0 ||
+                        if (((flags & (FLAG_PRECISION | FLAG_ZERO)) == 0 && usePrefixForZero) ||
                                 (!usePrefixForZero && "xXbBo".indexOf(fchar) != -1)) {
                             buf.append(PREFIX_NEGATIVE);
                         }
                         if (leadChar != '.') buf.append(leadChar);
                     }
                     buf.append(bytes,first,numlen);
 
                     if (width > 0) buf.fill(' ',width);
                     if (len < precision && fchar == 'd' && negative && !usePrefixForZero
                             && (flags & FLAG_MINUS) != 0) {
                         buf.fill(' ', precision - len);
                     }
                                         
                     offset++;
                     incomplete = false;
                     break;
                 }
                 case 'E':
                 case 'e':
                 case 'f':
                 case 'G':
                 case 'g': {
                     if (arg == null) arg = args.next();
                     
                     if (!(arg instanceof RubyFloat)) {
                         // FIXME: what is correct 'recv' argument?
                         // (this does produce the desired behavior)
-                        arg = RubyKernel.new_float(arg,arg);
+                        if (usePrefixForZero) {
+                            arg = RubyKernel.new_float(arg,arg);
+                        } else {
+                            arg = RubyKernel.new_float19(arg,arg);
+                        }
                     }
                     double dval = ((RubyFloat)arg).getDoubleValue();
                     boolean nan = dval != dval;
                     boolean inf = dval == Double.POSITIVE_INFINITY || dval == Double.NEGATIVE_INFINITY;
                     boolean negative = dval < 0.0d || (dval == 0.0d && (new Float(dval)).equals(new Float(-0.0)));
                     byte[] digits;
                     int nDigits = 0;
                     int exponent = 0;
 
                     int len = 0;
                     byte signChar;
                     
                     if (nan || inf) {
                         if (nan) {
                             digits = NAN_VALUE;
                             len = NAN_VALUE.length;
                         } else {
                             digits = INFINITY_VALUE;
                             len = INFINITY_VALUE.length;
                         }
                         if (negative) {
                             signChar = '-';
                             width--;
                         } else if ((flags & FLAG_PLUS) != 0) {
                             signChar = '+';
                             width--;
                         } else if ((flags & FLAG_SPACE) != 0) {
                             signChar = ' ';
                             width--;
                         } else {
                             signChar = 0;
                         }
                         width -= len;
                         
                         if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                             buf.fill(' ',width);
                             width = 0;
                         }
                         if (signChar != 0) buf.append(signChar);
 
                         if (width > 0 && (flags & FLAG_MINUS) == 0) {
                             buf.fill('0',width);
                             width = 0;
                         }
                         buf.append(digits);
                         if (width > 0) buf.fill(' ', width);
 
                         offset++;
                         incomplete = false;
                         break;
                     }
 
                     
                     String str = Double.toString(dval);
                     
                     // grrr, arghh, want to subclass sun.misc.FloatingDecimal, but can't,
                     // so we must do all this (the next 70 lines of code), which has already
                     // been done by FloatingDecimal.
                     int strlen = str.length();
                     digits = new byte[strlen];
                     int nTrailingZeroes = 0;
                     int i = negative ? 1 : 0;
                     int decPos = 0;
                     byte ival;
                 int_loop:
                     for ( ; i < strlen ; ) {
                         switch(ival = (byte)str.charAt(i++)) {
                         case '0':
                             if (nDigits > 0) nTrailingZeroes++;
 
                             break; // switch
                         case '1': case '2': case '3': case '4':
                         case '5': case '6': case '7': case '8': case '9':
                             if (nTrailingZeroes > 0) {
                                 for ( ; nTrailingZeroes > 0 ; nTrailingZeroes-- ) {
                                     digits[nDigits++] = '0';
                                 }
                             }
                             digits[nDigits++] = ival;
                             break; // switch
                         case '.':
                             break int_loop;
                         }
                     }
                     decPos = nDigits + nTrailingZeroes;
                 dec_loop:
                     for ( ; i < strlen ; ) {
                         switch(ival = (byte)str.charAt(i++)) {
                         case '0':
                             if (nDigits > 0) {
                                 nTrailingZeroes++;
                             } else {
                                 exponent--;
                             }
                             break; // switch
                         case '1': case '2': case '3': case '4':
                         case '5': case '6': case '7': case '8': case '9':
                             if (nTrailingZeroes > 0) {
                                 for ( ; nTrailingZeroes > 0 ; nTrailingZeroes--  ) {
                                     digits[nDigits++] = '0';
                                 }
                             }
                             digits[nDigits++] = ival;
                             break; // switch
                         case 'E':
                             break dec_loop;
                         }
                     }
                     if (i < strlen) {
                         int expSign;
                         int expVal = 0;
                         if (str.charAt(i) == '-') {
                             expSign = -1;
                             i++;
                         } else {
                             expSign = 1;
                         }
                         for ( ; i < strlen ; ) {
                             expVal = expVal * 10 + ((int)str.charAt(i++)-(int)'0');
                         }
                         exponent += expVal * expSign;
                     }
                     exponent += decPos - nDigits;
 
                     // gotta have at least a zero...
                     if (nDigits == 0) {
                         digits[0] = '0';
                         nDigits = 1;
                         exponent = 0;
                     }
 
                     // OK, we now have the significand in digits[0...nDigits]
                     // and the exponent in exponent.  We're ready to format.
 
                     int intDigits, intZeroes, intLength;
                     int decDigits, decZeroes, decLength;
                     byte expChar;
 
                     if (negative) {
                         signChar = '-';
                         width--;
                     } else if ((flags & FLAG_PLUS) != 0) {
                         signChar = '+';
                         width--;
                     } else if ((flags & FLAG_SPACE) != 0) {
                         signChar = ' ';
                         width--;
                     } else {
                         signChar = 0;
                     }
                     if ((flags & FLAG_PRECISION) == 0) {
                         precision = 6;
                     }
                     
                     switch(fchar) {
                     case 'E':
                     case 'G':
                         expChar = 'E';
                         break;
                     case 'e':
                     case 'g':
                         expChar = 'e';
                         break;
                     default:
                         expChar = 0;
                     }
 
                     switch (fchar) {
                     case 'g':
                     case 'G':
                         // an empirically derived rule: precision applies to
                         // significand length, irrespective of exponent
 
                         // an official rule, clarified: if the exponent
                         // <clarif>after adjusting for exponent form</clarif>
                         // is < -4,  or the exponent <clarif>after adjusting 
                         // for exponent form</clarif> is greater than the
                         // precision, use exponent form
                         boolean expForm = (exponent + nDigits - 1 < -4 ||
                             exponent + nDigits > (precision == 0 ? 1 : precision));
                         // it would be nice (and logical!) if exponent form 
                         // behaved like E/e, and decimal form behaved like f,
                         // but no such luck. hence: 
                         if (expForm) {
                             // intDigits isn't used here, but if it were, it would be 1
                             /* intDigits = 1; */
                             decDigits = nDigits - 1;
                             // precision for G/g includes integer digits
                             precision = Math.max(0,precision - 1);
 
                             if (precision < decDigits) {
                                 int n = round(digits,nDigits,precision,precision!=0);
                                 if (n > nDigits) nDigits = n;
                                 decDigits = Math.min(nDigits - 1,precision);
                             }
                             exponent += nDigits - 1;
                             
                             boolean isSharp = (flags & FLAG_SHARP) != 0;
 
                             // deal with length/width
 			    
                             len++; // first digit is always printed
 
                             // MRI behavior: Be default, 2 digits
                             // in the exponent. Use 3 digits
                             // only when necessary.
                             // See comment for writeExp method for more details.
                             if (exponent > 99)
                             	len += 5; // 5 -> e+nnn / e-nnn
                             else
                             	len += 4; // 4 -> e+nn / e-nn
 
                             if (isSharp) {
                             	// in this mode, '.' is always printed
                             	len++;
                             }
 
                             if (precision > 0) {
                             	if (!isSharp) {
                             	    // MRI behavior: In this mode
                             	    // trailing zeroes are removed:
                             	    // 1.500E+05 -> 1.5E+05 
                             	    int j = decDigits;
                             	    for (; j >= 1; j--) {
                             	        if (digits[j]== '0') {
                             	            decDigits--;
                             	        } else {
                             	            break;
                             	        }
                             	    }
 
                             	    if (decDigits > 0) {
                             	        len += 1; // '.' is printed
                             	        len += decDigits;
                             	    }
                             	} else  {
                             	    // all precision numebers printed
                             	    len += precision;
                             	}
                             }
 
                             width -= len;
 
                             if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                                 buf.fill(' ',width);
                                 width = 0;
                             }
                             if (signChar != 0) {
                                 buf.append(signChar);
                             }
                             if (width > 0 && (flags & FLAG_MINUS) == 0) {
                                 buf.fill('0',width);
                                 width = 0;
                             }
 
                             // now some data...
                             buf.append(digits[0]);
 
                             boolean dotToPrint = isSharp
                                     || (precision > 0 && decDigits > 0);
 
                             if (dotToPrint) {
                             	buf.append(args.getDecimalSeparator()); // '.'
                             }
 
                             if (precision > 0 && decDigits > 0) {
                             	buf.append(digits, 1, decDigits);
                             	precision -= decDigits;
                             }
 
                             if (precision > 0 && isSharp) {
                             	buf.fill('0', precision);
                             }
 
                             writeExp(buf, exponent, expChar);
 
                             if (width > 0) {
                                 buf.fill(' ', width);
                             }
                         } else { // decimal form, like (but not *just* like!) 'f'
                             intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                             intZeroes = Math.max(0,exponent);
                             intLength = intDigits + intZeroes;
                             decDigits = nDigits - intDigits;
                             decZeroes = Math.max(0,-(decDigits + exponent));
                             decLength = decZeroes + decDigits;
                             precision = Math.max(0,precision - intLength);
                             
                             if (precision < decDigits) {
                                 int n = round(digits,nDigits,intDigits+precision-1,precision!=0);
                                 if (n > nDigits) {
                                     // digits array shifted, update all
                                     nDigits = n;
                                     intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                                     intLength = intDigits + intZeroes;
                                     decDigits = nDigits - intDigits;
                                     decZeroes = Math.max(0,-(decDigits + exponent));
                                     precision = Math.max(0,precision-1);
                                 }
                                 decDigits = precision;
                                 decLength = decZeroes + decDigits;
                             }
                             len += intLength;
                             if (decLength > 0) {
                                 len += decLength + 1;
                             } else {
                                 if ((flags & FLAG_SHARP) != 0) {
                                     len++; // will have a trailing '.'
                                     if (precision > 0) { // g fills trailing zeroes if #
                                         len += precision;
                                     }
                                 }
                             }
                             
                             width -= len;
                             
                             if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                                 buf.fill(' ',width);
                                 width = 0;
                             }
                             if (signChar != 0) {
                                 buf.append(signChar);
                             }
                             if (width > 0 && (flags & FLAG_MINUS) == 0) {
                                 buf.fill('0',width);
                                 width = 0;
                             }
                             // now some data...
                             if (intLength > 0){
                                 if (intDigits > 0) { // s/b true, since intLength > 0
                                     buf.append(digits,0,intDigits);
                                 }
                                 if (intZeroes > 0) {
                                     buf.fill('0',intZeroes);
                                 }
                             } else {
                                 // always need at least a 0
                                 buf.append('0');
                             }
                             if (decLength > 0 || (flags & FLAG_SHARP) != 0) {
                                 buf.append(args.getDecimalSeparator());
                             }
                             if (decLength > 0) {
                                 if (decZeroes > 0) {
                                     buf.fill('0',decZeroes);
                                     precision -= decZeroes;
                                 }
                                 if (decDigits > 0) {
                                     buf.append(digits,intDigits,decDigits);
                                     precision -= decDigits;
                                 }
                                 if ((flags & FLAG_SHARP) != 0 && precision > 0) {
                                     buf.fill('0',precision);
                                 }
                             }
                             if ((flags & FLAG_SHARP) != 0 && precision > 0) buf.fill('0',precision);
                             if (width > 0) buf.fill(' ', width);
                         }
                         break;
                     
                     case 'f':
                         intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                         intZeroes = Math.max(0,exponent);
                         intLength = intDigits + intZeroes;
                         decDigits = nDigits - intDigits;
                         decZeroes = Math.max(0,-(decDigits + exponent));
                         decLength = decZeroes + decDigits;                                     
 
                         if (precision < decLength) {
                             if (precision < decZeroes) {
                                 decDigits = 0;
                                 decZeroes = precision;
                             } else {
                                 int n = round(digits, nDigits, intDigits+precision-decZeroes-1, false);
                                 if (n > nDigits) {
                                     // digits arr shifted, update all
                                     nDigits = n;
                                     intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                                     intLength = intDigits + intZeroes;
                                     decDigits = nDigits - intDigits;
                                     decZeroes = Math.max(0,-(decDigits + exponent));
                                     decLength = decZeroes + decDigits;
                                 }
                                 decDigits = precision - decZeroes;
                             }
                             decLength = decZeroes + decDigits;
                         }
                         if (precision > 0) {
                             len += Math.max(1,intLength) + 1 + precision;
                             // (1|intlen).prec
                         } else {
                             len += Math.max(1,intLength);
                             // (1|intlen)
                             if ((flags & FLAG_SHARP) != 0) {
                                 len++; // will have a trailing '.'
                             }
                         }
                         
                         width -= len;
                         
                         if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                             buf.fill(' ',width);
                             width = 0;
                         }
                         if (signChar != 0) {
                             buf.append(signChar);
                         }
                         if (width > 0 && (flags & FLAG_MINUS) == 0) {
                             buf.fill('0',width);
                             width = 0;
                         }
                         // now some data...
                         if (intLength > 0){
                             if (intDigits > 0) { // s/b true, since intLength > 0
                                 buf.append(digits,0,intDigits);
                             }
                             if (intZeroes > 0) {
                                 buf.fill('0',intZeroes);
                             }
                         } else {
                             // always need at least a 0
                             buf.append('0');
                         }
                         if (precision > 0 || (flags & FLAG_SHARP) != 0) {
                             buf.append(args.getDecimalSeparator());
                         }
                         if (precision > 0) {
                             if (decZeroes > 0) {
                                 buf.fill('0',decZeroes);
                                 precision -= decZeroes;
                             }
                             if (decDigits > 0) {
                                 buf.append(digits,intDigits,decDigits);
                                 precision -= decDigits;
                             }
                             // fill up the rest with zeroes
                             if (precision > 0) {
                                 buf.fill('0',precision);
                             }
                         }
                         if (width > 0) {
                             buf.fill(' ', width);
                         }
                         break;
                     case 'E':
                     case 'e':
                         // intDigits isn't used here, but if it were, it would be 1
                         /* intDigits = 1; */
                         decDigits = nDigits - 1;
                         
                         if (precision < decDigits) {
                             int n = round(digits,nDigits,precision,precision!=0);
                             if (n > nDigits) {
                                 nDigits = n;
                             }
                             decDigits = Math.min(nDigits - 1,precision);
                         }
                         exponent += nDigits - 1;
 
                         boolean isSharp = (flags & FLAG_SHARP) != 0;
 
                         // deal with length/width
 
                         len++; // first digit is always printed
 
                         // MRI behavior: Be default, 2 digits
                         // in the exponent. Use 3 digits
                         // only when necessary.
                         // See comment for writeExp method for more details.
                         if (exponent > 99)
                             len += 5; // 5 -> e+nnn / e-nnn
                         else
                             len += 4; // 4 -> e+nn / e-nn
 
                         if (precision > 0) {
                             // '.' and all precision digits printed
                             len += 1 + precision;
                         } else  if (isSharp) {
                             len++;  // in this mode, '.' is always printed
                         }
 
                         width -= len;
 
                         if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                             buf.fill(' ',width);
                             width = 0;
                         }
                         if (signChar != 0) {
                             buf.append(signChar);
                         }
                         if (width > 0 && (flags & FLAG_MINUS) == 0) {
                             buf.fill('0',width);
                             width = 0;
                         }
                         // now some data...
                         buf.append(digits[0]);
                         if (precision > 0) {
                             buf.append(args.getDecimalSeparator()); // '.'
                             if (decDigits > 0) {
                                 buf.append(digits,1,decDigits);
                                 precision -= decDigits;
                             }
                             if (precision > 0) buf.fill('0',precision);
 
                         } else if ((flags & FLAG_SHARP) != 0) {
                             buf.append(args.getDecimalSeparator());
                         }
 
                         writeExp(buf, exponent, expChar);
 
                         if (width > 0) buf.fill(' ', width);
                         break;
                     } // switch (format char E,e,f,G,g)
                     
                     offset++;
                     incomplete = false;
                     break;
                 } // block (case E,e,f,G,g)
                 } // switch (each format char in spec)
             } // for (each format spec)
             
             // equivalent to MRI case '\0':
             if (incomplete) {
                 if (flags == FLAG_NONE) {
                     // dangling '%' char
                     buf.append('%');
                 } else {
                     raiseArgumentError(args,ERR_ILLEGAL_FORMAT_CHAR);
                 }
             }
         } // main while loop (offset < length)
 
         // MRI behavior: validate only the unnumbered arguments
         if ((args.numbered == 0) && args.unnumbered < args.length) {
             if (args.runtime.getDebug().isTrue()) {
                 args.raiseArgumentError("too many arguments for format string");
             } else if (args.runtime.isVerbose()) {
                 args.warn(ID.TOO_MANY_ARGUMENTS, "too many arguments for format string");
             }
         }
 
         return tainted;
     }
 
     private static void writeExp(ByteList buf, int exponent, byte expChar) {
         // Unfortunately, the number of digits in the exponent is
         // not clearly defined in Ruby documentation. This is a
         // platform/version-dependent behavior. On Linux/Mac/Cygwin/*nix,
         // two digits are used. On Windows, 3 digits are used.
         // It is desirable for JRuby to have consistent behavior, and
         // the two digits behavior was selected. This is also in sync
         // with "Java-native" sprintf behavior (java.util.Formatter).
         buf.append(expChar); // E or e
         buf.append(exponent >= 0 ? '+' : '-');
         if (exponent < 0) {
             exponent = -exponent;
         }
         if (exponent > 99) {                                
             buf.append(exponent / 100 + '0');
             buf.append(exponent % 100 / 10 + '0');
         } else {
             buf.append(exponent / 10 + '0');
         }
         buf.append(exponent % 10 + '0');
     }
 
     // debugging code, keeping for now
     /*
     private static final void showLiteral(byte[] format, int start, int offset) {
         System.out.println("literal: ["+ new String(format,start,offset-start)+ "], " +
                 " s="+ start + " o="+ offset);
     }
     
     // debugging code, keeping for now
     private static final void showVals(byte[] format,int start,int offset, byte fchar,
             int flags, int width, int precision, Object arg) {
         System.out.println(new StringBuffer()
         .append("value: ").append(new String(format,start,offset-start+1)).append('\n')
         .append("type: ").append((char)fchar).append('\n')
         .append("start: ").append(start).append('\n')
         .append("length: ").append(offset-start).append('\n')
         .append("flags: ").append(Integer.toBinaryString(flags)).append('\n')
         .append("width: ").append(width).append('\n')
         .append("precision: ").append(precision).append('\n')
         .append("arg: ").append(arg).append('\n')
         .toString());
         
     }
     */
     
     private static final void raiseArgumentError(Args args, String message) {
         args.raiseArgumentError(message);
     }
     
     private static final void warning(ID id, Args args, String message) {
         args.warning(id, message);
     }
     
     private static final void checkOffset(Args args, int offset, int length, String message) {
         if (offset >= length) {
             raiseArgumentError(args,message);
         }
     }
 
     private static final int extendWidth(Args args, int oldWidth, byte newChar) {
         int newWidth = oldWidth * 10 + (newChar - '0');
         if (newWidth / 10 != oldWidth) raiseArgumentError(args,"width too big");
         return newWidth;
     }
     
     private static final boolean isDigit(byte aChar) {
         return (aChar >= '0' && aChar <= '9');
     }
     
     private static final boolean isPrintable(byte aChar) {
         return (aChar > 32 && aChar < 127);
     }
 
     private static final int skipSignBits(byte[] bytes, int base) {
         int skip = 0;
         int length = bytes.length;
         byte b;
         switch(base) {
         case 2:
             for ( ; skip < length && bytes[skip] == '1'; skip++ ) ;
             break;
         case 8:
             if (length > 0 && bytes[0] == '3') skip++;
             for ( ; skip < length && bytes[skip] == '7'; skip++ ) ;
             break;
         case 10:
             if (length > 0 && bytes[0] == '-') skip++;
             break;
         case 16:
             for ( ; skip < length && ((b = bytes[skip]) == 'f' || b == 'F'); skip++ ) ;
         }
         return skip;
     }
     
     private static final int round(byte[] bytes, int nDigits, int roundPos, boolean roundDown) {
         int next = roundPos + 1;
         if (next >= nDigits || bytes[next] < '5' ||
                 // MRI rounds up on nnn5nnn, but not nnn5 --
                 // except for when they do
                 (roundDown && bytes[next] == '5' && next == nDigits - 1)) {
             return nDigits;
         }
         if (roundPos < 0) { // "%.0f" % 0.99
             System.arraycopy(bytes,0,bytes,1,nDigits);
             bytes[0] = '1';
             return nDigits + 1;
         }
         bytes[roundPos] += 1;
         while (bytes[roundPos] > '9') {
             bytes[roundPos] = '0';
             roundPos--;
             if (roundPos >= 0) {
                 bytes[roundPos] += 1;
             } else {
                 System.arraycopy(bytes,0,bytes,1,nDigits);
                 bytes[0] = '1';
                 return nDigits + 1;
             }
         }
         return nDigits;
     }
 
     private static final byte[] getFixnumBytes(RubyFixnum arg, int base, boolean sign, boolean upper) {
         long val = arg.getLongValue();
 
         // limit the length of negatives if possible (also faster)
         if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) {
             if (sign) {
                 return ConvertBytes.intToByteArray((int)val,base,upper);
             } else {
                 switch(base) {
                 case 2:  return ConvertBytes.intToBinaryBytes((int)val);
                 case 8:  return ConvertBytes.intToOctalBytes((int)val);
                 case 10:
                 default: return ConvertBytes.intToCharBytes((int)val);
                 case 16: return ConvertBytes.intToHexBytes((int)val,upper);
                 }
             }
         } else {
             if (sign) {
                 return ConvertBytes.longToByteArray(val,base,upper);
             } else {
                 switch(base) {
                 case 2:  return ConvertBytes.longToBinaryBytes(val);
                 case 8:  return ConvertBytes.longToOctalBytes(val);
                 case 10:
                 default: return ConvertBytes.longToCharBytes(val);
                 case 16: return ConvertBytes.longToHexBytes(val,upper);
                 }
             }
         }
     }
     
     private static final byte[] getBignumBytes(RubyBignum arg, int base, boolean sign, boolean upper) {
         BigInteger val = arg.getValue();
         if (sign || base == 10 || val.signum() >= 0) {
             return stringToBytes(val.toString(base),upper);
         }
 
         // negative values
         byte[] bytes = val.toByteArray();
         switch(base) {
         case 2:  return ConvertBytes.twosComplementToBinaryBytes(bytes);
         case 8:  return ConvertBytes.twosComplementToOctalBytes(bytes);
         case 16: return ConvertBytes.twosComplementToHexBytes(bytes,upper);
         default: return stringToBytes(val.toString(base),upper);
         }
     }
     
     private static final byte[] getUnsignedNegativeBytes(RubyInteger arg) {
         // calculation for negatives when %u specified
         // for values >= Integer.MIN_VALUE * 2, MRI uses (the equivalent of)
         //   long neg_u = (((long)Integer.MAX_VALUE + 1) << 1) + val
         // for smaller values, BigInteger math is required to conform to MRI's
         // result.
         long longval;
         BigInteger bigval;
         
         if (arg instanceof RubyFixnum) {
             // relatively cheap test for 32-bit values
             longval = ((RubyFixnum)arg).getLongValue();
             if (longval >= Long.MIN_VALUE << 1) {
                 return ConvertBytes.longToCharBytes(((Long.MAX_VALUE + 1L) << 1) + longval);
             }
             // no such luck...
             bigval = BigInteger.valueOf(longval);
         } else {
             bigval = ((RubyBignum)arg).getValue();
         }
         // ok, now it gets expensive...
         int shift = 0;
         // go through negated powers of 32 until we find one small enough 
         for (BigInteger minus = BIG_MINUS_64 ;
                 bigval.compareTo(minus) < 0 ;
                 minus = minus.shiftLeft(32), shift++) ;
         // add to the corresponding positive power of 32 for the result.
         // meaningful? no. conformant? yes. I just write the code...
         BigInteger nPower32 = shift > 0 ? BIG_64.shiftLeft(32 * shift) : BIG_64;
         return stringToBytes(nPower32.add(bigval).toString(),false);
     }
     
     private static final byte[] stringToBytes(CharSequence s, boolean upper) {
         int len = s.length();
         byte[] bytes = new byte[len];
         if (upper) {
             for (int i = len; --i >= 0; ) {
                 int b = (byte)((int)s.charAt(i) & (int)0xff);
                 if (b >= 'a' && b <= 'z') {
                     bytes[i] = (byte)(b & ~0x20);
                 } else {
                     bytes[i] = (byte)b;
                 }
             }
         } else {
             for (int i = len; --i >= 0; ) {
                 bytes[i] = (byte)((int)s.charAt(i) & (int)0xff); 
             }
         }
         return bytes;
     }
 }
