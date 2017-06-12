diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index d4f8d72b27..f3bc6f6bd7 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1848 +1,1848 @@
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
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.ConvertBytes;
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
 
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject at_exit(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().pushExitBlock(context.getRuntime().newProc(Block.Type.PROC, block));
     }
 
     @JRubyMethod(name = "autoload?", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject autoload_p(ThreadContext context, final IRubyObject recv, IRubyObject symbol) {
         Ruby runtime = context.getRuntime();
         final RubyModule module = getModuleForAutoload(runtime, recv);
         String name = module.getName() + "::" + symbol.asJavaString();
         
         IAutoloadMethod autoloadMethod = runtime.getLoadService().autoloadFor(name);
         if (autoloadMethod == null) return runtime.getNil();
 
         return runtime.newString(autoloadMethod.file());
     }
 
     @JRubyMethod(required = 2, module = true, visibility = PRIVATE)
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
         final RubyModule module = getModuleForAutoload(runtime, recv);
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
 
     private static RubyModule getModuleForAutoload(Ruby runtime, IRubyObject recv) {
         RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : runtime.getObject();
         if (module == runtime.getKernel()) {
             // special behavior if calling Kernel.autoload directly
             if (runtime.is1_9()) {
                 module = runtime.getObject().getSingletonClass();
             } else {
                 module = runtime.getObject();
             }
         }
         return module;
     }
 
     @JRubyMethod(rest = true, frame = true, visibility = PRIVATE, compat = RUBY1_8)
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
 
     @JRubyMethod(required = 1, optional = 2, module = true, visibility = PRIVATE, compat = RUBY1_8)
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
 
     @JRubyMethod(name = "open", required = 1, optional = 2, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject open19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         if (args[0].respondsTo("to_open")) {
             args[0] = args[0].callMethod(context, "to_open");
             return RubyFile.open(context, runtime.getFile(), args, block);
         } else {
             return open(context, recv, args, block);
         }
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
             runtime.getGlobalVariables().get("$stderr").callMethod(context,"puts",args[0].convertToString());
         }
         
         exit(runtime, new IRubyObject[] { runtime.getFalse() }, false);
         return runtime.getNil(); // not reached
     }
 
     @JRubyMethod(name = "Array", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_array(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return RuntimeHelpers.arrayValue(context, context.getRuntime(), object);
     }
 
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert");
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg);
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg0, arg1);
     }
     
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert");
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg);
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg0, arg1);
     }
 
     @JRubyMethod(name = "Float", module = true, visibility = PRIVATE, compat = RUBY1_8)
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
 
     @JRubyMethod(name = "Float", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyFloat new_float19(IRubyObject recv, IRubyObject object) {
         Ruby runtime = recv.getRuntime();
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(runtime, ((RubyFixnum)object).getDoubleValue());
         } else if (object instanceof RubyFloat) {
             return (RubyFloat)object;
         } else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(runtime, RubyBignum.big2dbl((RubyBignum)object));
         } else if(object instanceof RubyString){
             if(((RubyString) object).getByteList().getRealSize() == 0){ // rb_cstr_to_dbl case
                 throw runtime.newArgumentError("invalid value for Float(): " + object.inspect());
             }
             RubyString arg = (RubyString)object;
             if (arg.toString().startsWith("0x")) {
                 return ConvertBytes.byteListToInum19(runtime, arg.getByteList(), 16, true).toFloat();
             }
             return RubyNumeric.str2fnum19(runtime, arg,true);
         } else if(object.isNil()){
             throw runtime.newTypeError("can't convert nil into Float");
         } else {
             return (RubyFloat)TypeConverter.convertToType19(object, runtime.getFloat(), "to_f");
         }
     }
 
     @JRubyMethod(name = "Integer", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject new_integer(ThreadContext context, IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue();
             if (val >= (double) RubyFixnum.MAX || val < (double) RubyFixnum.MIN) {
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
 
     @JRubyMethod(name = "Integer", module = true, visibility = PRIVATE, compat = RUBY1_9)
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
 
     @JRubyMethod(name = "Integer", module = true, visibility = PRIVATE, compat = RUBY1_9)
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
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject new_string(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType(object, context.getRuntime().getString(), "to_s");
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_9)
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
 
     @JRubyMethod(name = "public_method",required = 1, module = true, compat = RUBY1_9)
     public static IRubyObject public_method(ThreadContext context, IRubyObject recv, IRubyObject symbol) {
         return recv.getMetaClass().newMethod(recv, symbol.asJavaString(), true, PUBLIC, true, false);
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
 
     @JRubyMethod(name = "respond_to_missing?", module = true, compat = RUBY1_9)
     public static IRubyObject respond_to_missing_p(ThreadContext context, IRubyObject recv, IRubyObject symbol) {
         return context.getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "respond_to_missing?", module = true, compat = RUBY1_9)
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
 
     @JRubyMethod(name = "sub!", module = true, visibility = PRIVATE, reads = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "sub!", module = true, visibility = PRIVATE, reads = LASTLINE, compat = RUBY1_8)
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
 
     @JRubyMethod(name = "sub", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
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
 
     @JRubyMethod(name = "gsub!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "gsub!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
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
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, arg1, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chop!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chop_bang(ThreadContext context, IRubyObject recv, Block block) {
         return getLastlineString(context, context.getRuntime()).chop_bang(context);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
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
 
     @JRubyMethod(name = "chomp!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(context);
     }
 
     @JRubyMethod(name = "chomp!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(context, arg0);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context).isNil()) {
             return str;
         } 
 
         context.getCurrentScope().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
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
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).split(context);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0, arg1);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = {LASTLINE, BACKREF}, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject scan(ThreadContext context, IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(context, context.getRuntime()).scan(context, pattern, block);
     }
 
     @JRubyMethod(required = 1, optional = 3, module = true, visibility = PRIVATE)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyIO.select_static(context, context.getRuntime(), args);
     }
 
     @JRubyMethod(optional = 1, module = true, visibility = PRIVATE)
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
 
-        int status = 1;
+        int status = hard ? 1 : 0;
 
         if (args.length > 0) {
             RubyObject argument = (RubyObject) args[0];
             if (argument instanceof RubyBoolean) {
                 status = argument.isFalse() ? 1 : 0;
             } else {
                 status = RubyNumeric.fix2int(argument);
             }
         }
 
         if (hard) {
             if (runtime.getInstanceConfig().isHardExit()) {
                 System.exit(status);
             } else {
                 throw new MainExitException(status, true);
             }
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
     @JRubyMethod(name = "global_variables", module = true, visibility = PRIVATE, compat = RUBY1_9)
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
     @JRubyMethod(name = "local_variables", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyArray local_variables19(ThreadContext context, IRubyObject recv) {
         final Ruby runtime = context.getRuntime();
         RubyArray localVariables = runtime.newArray();
 
         for (String name: context.getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newSymbol(name));
         }
 
         return localVariables;
     }
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyBinding binding(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime(), context.currentBinding(recv));
     }
 
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyBinding binding_1_9(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime(), context.currentBinding());
     }
 
     @JRubyMethod(name = {"block_given?", "iterator?"}, module = true, visibility = PRIVATE)
     public static RubyBoolean block_given_p(ThreadContext context, IRubyObject recv) {
         return context.getRuntime().newBoolean(context.getCurrentFrame().getBlock().isGiven());
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
 
     @JRubyMethod(name = {"raise", "fail"}, optional = 3, module = true, visibility = PRIVATE, omit = true)
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
         
         raise.preRaise(context);
 
         if (runtime.getDebug().isTrue()) {
             printExceptionSummary(context, runtime, raise.getException());
         }
 
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
         ThreadContext.RubyStackTraceElement[] elements = rEx.getBacktraceElements();
         ThreadContext.RubyStackTraceElement firstElement = elements[0];
         String msg = String.format("Exception `%s' at %s:%s - %s\n",
                 rEx.getMetaClass(),
                 firstElement.getFileName(), firstElement.getLineNumber(),
                 runtime.is1_9() ? TypeConverter.convertToType(rEx, runtime.getString(), "to_s") : rEx.convertToString().toString());
 
         runtime.getErrorStream().print(msg);
     }
 
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         return requireCommon(recv.getRuntime(), recv, name, block);
     }
 
     @JRubyMethod(name = "require", module = true, visibility = PRIVATE, compat = RUBY1_9)
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
 
     @JRubyMethod(required = 1, optional = 1, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         return loadCommon(args[0], recv.getRuntime(), args, block);
     }
 
     @JRubyMethod(name = "load", required = 1, optional = 1, module = true, visibility = PRIVATE, compat = RUBY1_9)
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
 
         runtime.getLoadService().load(file.toString(), wrap);
 
         return runtime.getTrue();
     }
 
     @JRubyMethod(required = 1, optional = 3, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject eval(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return evalCommon(context, recv, args, block, evalBinding18);
     }
 
     @JRubyMethod(name = "eval", required = 1, optional = 3, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject eval19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return evalCommon(context, recv, args, block, evalBinding19);
     }
 
     private static IRubyObject evalCommon(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block, EvalBinding evalBinding) {
         Ruby runtime = context.getRuntime();
         // string to eval
         RubyString src = args[0].convertToString();
         runtime.checkSafeString(src);
 
         boolean bindingGiven = args.length > 1 && !args[1].isNil();
         Binding binding = bindingGiven ? evalBinding.convertToBinding(args[1]) : context.currentBinding();
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
             if (bindingGiven) {
                 // binding given, use binding's line
             } else {
                 // no binding given, use 0 for both
                 binding.setLine(0);
             }
         }
 
         // set method to current frame's, which should be caller's
         String frameName = context.getFrameName();
         if (frameName != null) binding.setMethod(frameName);
 
         if (bindingGiven) recv = binding.getSelf();
 
         return ASTInterpreter.evalWithBinding(context, recv, src, binding);
     }
 
     private static abstract class EvalBinding {
         public abstract Binding convertToBinding(IRubyObject scope);
     }
 
     private static EvalBinding evalBinding18 = new EvalBinding() {
         public Binding convertToBinding(IRubyObject scope) {
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
     };
 
     private static EvalBinding evalBinding19 = new EvalBinding() {
         public Binding convertToBinding(IRubyObject scope) {
             if (scope instanceof RubyBinding) {
                 return ((RubyBinding)scope).getBinding().clone();
             } else {
                 throw scope.getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Binding)");
             }
         }
     };
 
 
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject callcc(ThreadContext context, IRubyObject recv, Block block) {
         RubyContinuation continuation = new RubyContinuation(context.getRuntime());
         return continuation.enter(context, continuation, block);
     }
 
     @JRubyMethod(optional = 1, module = true, visibility = PRIVATE, omit = true)
     public static IRubyObject caller(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw context.getRuntime().newArgumentError("negative level (" + level + ')');
         }
 
         return context.createCallerBacktrace(context.getRuntime(), level);
     }
 
     @JRubyMethod(name = "catch", module = true, visibility = PRIVATE)
     public static IRubyObject rbCatch(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         Ruby runtime = context.runtime;
         RubyContinuation rbContinuation = new RubyContinuation(runtime, stringOrSymbol(tag));
         try {
             context.pushCatch(rbContinuation.getContinuation());
             return rbContinuation.enter(context, rbContinuation, block);
         } finally {
             context.popCatch();
         }
     }
 
     @JRubyMethod(name = "catch", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject rbCatch19(ThreadContext context, IRubyObject recv, Block block) {
         IRubyObject tag = new RubyObject(context.runtime.getObject());
         return rbCatch19Common(context, tag, block, true);
     }
 
     @JRubyMethod(name = "catch", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject rbCatch19(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         return rbCatch19Common(context, tag, block, false);
     }
 
     private static IRubyObject rbCatch19Common(ThreadContext context, IRubyObject tag, Block block, boolean yieldTag) {
         RubyContinuation rbContinuation = new RubyContinuation(context.getRuntime(), tag);
         try {
             context.pushCatch(rbContinuation.getContinuation());
             return rbContinuation.enter(context, yieldTag ? tag : rbContinuation, block);
         } finally {
             context.popCatch();
         }
     }
 
     @JRubyMethod(name = "throw", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject rbThrow(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         return rbThrowInternal(context, stringOrSymbol(tag), IRubyObject.NULL_ARRAY, block, uncaught18);
     }
 
     @JRubyMethod(name = "throw", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject rbThrow(ThreadContext context, IRubyObject recv, IRubyObject tag, IRubyObject arg, Block block) {
         return rbThrowInternal(context, stringOrSymbol(tag), new IRubyObject[] {arg}, block, uncaught18);
     }
 
     private static RubySymbol stringOrSymbol(IRubyObject obj) {
         if (obj instanceof RubySymbol) {
             return (RubySymbol)obj;
         } else {
             return RubySymbol.newSymbol(obj.getRuntime(), obj.asJavaString().intern());
         }
     }
 
     @JRubyMethod(name = "throw", frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject rbThrow19(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         return rbThrowInternal(context, tag, IRubyObject.NULL_ARRAY, block, uncaught19);
     }
 
     @JRubyMethod(name = "throw", frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject rbThrow19(ThreadContext context, IRubyObject recv, IRubyObject tag, IRubyObject arg, Block block) {
         return rbThrowInternal(context, tag, new IRubyObject[] {arg}, block, uncaught19);
     }
 
     private static IRubyObject rbThrowInternal(ThreadContext context, IRubyObject tag, IRubyObject[] args, Block block, Uncaught uncaught) {
         Ruby runtime = context.getRuntime();
 
         RubyContinuation.Continuation continuation = context.getActiveCatch(tag);
 
         if (continuation != null) {
             continuation.args = args;
             throw continuation;
         }
 
         // No catch active for this throw
         String message = "uncaught throw `" + tag + "'";
         RubyThread currentThread = context.getThread();
 
         if (currentThread == runtime.getThreadService().getMainThread()) {
             throw uncaught.uncaughtThrow(runtime, message, tag);
         } else {
             message += " in thread 0x" + Integer.toHexString(RubyInteger.fix2int(currentThread.id()));
             if (runtime.is1_9()) {
                 throw runtime.newArgumentError(message);
             } else {
                 throw runtime.newThreadError(message);
             }
         }
     }
 
     private static abstract class Uncaught {
         public abstract RaiseException uncaughtThrow(Ruby runtime, String message, IRubyObject tag);
     }
 
     private static final Uncaught uncaught18 = new Uncaught() {
         public RaiseException uncaughtThrow(Ruby runtime, String message, IRubyObject tag) {
             return runtime.newNameError(message, tag.toString());
         }
     };
 
     private static final Uncaught uncaught19 = new Uncaught() {
         public RaiseException uncaughtThrow(Ruby runtime, String message, IRubyObject tag) {
             return runtime.newArgumentError(message);
         }
     };
 
     @JRubyMethod(required = 1, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject trap(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         context.getRuntime().getLoadService().require("jsignal_internal");
         return RuntimeHelpers.invoke(context, recv, "__jtrap", args, block);
     }
     
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject warn(ThreadContext context, IRubyObject recv, IRubyObject message) {
         Ruby runtime = context.getRuntime();
         
         if (runtime.warningsEnabled()) {
             IRubyObject out = runtime.getGlobalVariables().get("$stderr");
             RuntimeHelpers.invoke(context, out, "puts", message);
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE)
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
 
     @JRubyMethod(required = 1, optional = 1, module = true, visibility = PRIVATE)
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
 
     @JRubyMethod(required = 1, optional = 1, module = true, visibility = PRIVATE)
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
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject singleton_method_added(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject singleton_method_removed(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject singleton_method_undefined(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(required = 1, optional = 1, compat = RUBY1_9)
     public static IRubyObject define_singleton_method(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw context.getRuntime().newArgumentError(0, 1);
 
         RubyClass singleton_class = recv.getSingletonClass();
         if (args.length > 1) {
             IRubyObject arg1 = args[1];
             if (context.runtime.getUnboundMethod().isInstance(args[1])) {
                 RubyUnboundMethod method = (RubyUnboundMethod)arg1;
                 RubyModule owner = (RubyModule)method.owner(context);
                 if (owner.isSingleton() &&
                     !(recv.getMetaClass().isSingleton() && recv.getMetaClass().isKindOfModule(owner))) {
 
                     throw context.runtime.newTypeError("can't bind singleton method to a different class");
                 }
             }
             return singleton_class.define_method(context, args[0], args[1], block);
         } else {
             return singleton_class.define_method(context, args[0], block);
         }
     }
 
     @JRubyMethod(name = {"proc", "lambda"}, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyProc proc(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyProc lambda(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = "proc", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyProc proc_1_9(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.PROC, block);
     }
 
     @JRubyMethod(name = "loop", module = true, visibility = PRIVATE)
     public static IRubyObject loop(ThreadContext context, IRubyObject recv, Block block) {
         if (context.runtime.is1_9() && !block.isGiven()) {
             return RubyEnumerator.enumeratorize(context.runtime, recv, "loop");
         }
         IRubyObject nil = context.getRuntime().getNil();
         RubyClass stopIteration = context.getRuntime().getStopIteration();
         try {
             while (true) {
                 block.yieldSpecific(context);
 
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
             // FIXME: Need to implement an readable_real_p in FileTest
             return RubyFileTest.readable_p(recv, args[1]);
         case 's': // ?s  | int/nil | If file1 has nonzero size, return the size, otherwise nil
             return RubyFileTest.size_p(recv, args[1]);
         case 'S': // ?S  | boolean | True if file1 exists and is a socket
             return RubyFileTest.socket_p(recv, args[1]);
         case 'u': // ?u  | boolean | True if file1 has the setuid bit set
             return RubyFileTest.setuid_p(recv, args[1]);
         case 'w': // ?w  | boolean | True if file1 exists and is writable by effective uid/gid
             return RubyFileTest.writable_p(recv, args[1]);
         case 'W': // ?W  | boolean | True if file1 exists and is writable by the real uid/gid
             // FIXME: Need to implement an writable_real_p in FileTest
             return RubyFileTest.writable_p(recv, args[1]);
         case 'x': // ?x  | boolean | True if file1 exists and is executable by the effective uid/gid
             return RubyFileTest.executable_p(recv, args[1]);
         case 'X': // ?X  | boolean | True if file1 exists and is executable by the real uid/gid
             return RubyFileTest.executable_real_p(recv, args[1]);
         case 'z': // ?z  | boolean | True if file1 exists and has a zero length
             return RubyFileTest.zero_p(recv, args[1]);
         case '=': // ?=  | boolean | True if the modification times of file1 and file2 are equal
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeEquals(args[2]);
         case '<': // ?<  | boolean | True if the modification time of file1 is prior to that of file2
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeLessThan(args[2]);
         case '>': // ?>  | boolean | True if the modification time of file1 is after that of file2
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeGreaterThan(args[2]);
         case '-': // ?-  | boolean | True if file1 and file2 are identical
             return RubyFileTest.identical_p(recv, args[1], args[2]);
         default:
             throw new InternalError("unreachable code reached!");
         }
     }
 
     @JRubyMethod(name = "`", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject backquote(ThreadContext context, IRubyObject recv, IRubyObject aString) {
         Ruby runtime = context.getRuntime();
         RubyString string = aString.convertToString();
         IRubyObject[] args = new IRubyObject[] {string};
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         int resultCode;
 
         try {
             // NOTE: not searching executable path before invoking args
             resultCode = ShellLauncher.runAndWait(runtime, args, output, false);
         } catch (Exception e) {
             resultCode = 127;
         }
 
         context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
 
         byte[] out = output.toByteArray();
         int length = out.length;
 
         if (Platform.IS_WINDOWS) {
             // MRI behavior, replace '\r\n' by '\n'
             int newPos = 0;
             byte curr, next;
             for (int pos = 0; pos < length; pos++) {
                 curr = out[pos];
                 if (pos == length - 1) {
                     out[newPos++] = curr;
                     break;
                 }
                 next = out[pos + 1];
                 if (curr != '\r' || next != '\n') {
                     out[newPos++] = curr;
                 }
             }
 
             // trim the length
             length = newPos;
         }
 
         return RubyString.newStringNoCopy(runtime, out, 0, length);
     }
 
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyInteger srand(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
 
         // Not sure how well this works, but it works much better than
         // just currentTimeMillis by itself.
         long oldRandomSeed = runtime.getRandomSeed();
         runtime.setRandomSeed(System.currentTimeMillis() ^
                recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
                runtime.getRandom().nextInt(Math.max(1, Math.abs((int)runtime.getRandomSeed()))));
 
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
     
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyInteger srand(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         IRubyObject newRandomSeed = arg.convertToInteger("to_int");
         Ruby runtime = context.getRuntime();
 
         long seedArg = 0;
         if (newRandomSeed instanceof RubyBignum) {
             seedArg = ((RubyBignum)newRandomSeed).getValue().longValue();
         } else if (!arg.isNil()) {
             seedArg = RubyNumeric.num2long(newRandomSeed);
         }
 
         long oldRandomSeed = runtime.getRandomSeed();
         runtime.setRandomSeed(seedArg);
 
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
 
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject srand19(ThreadContext context, IRubyObject recv) {
         return RubyRandom.srand(context, recv);
     }
 
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject srand19(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RubyRandom.srandCommon(context, recv, arg.convertToInteger("to_int"), true);
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyNumeric rand(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         return RubyFloat.newFloat(runtime, runtime.getRandom().nextDouble());
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyNumeric rand(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         Random random = runtime.getRandom();
 
         return randCommon(context, runtime, random, recv, arg);
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyNumeric rand19(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         return RubyFloat.newFloat(runtime, RubyRandom.globalRandom.nextDouble());
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyNumeric rand19(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         Random random = RubyRandom.globalRandom;
 
         return randCommon(context, runtime, random, recv, arg);
     }
 
     private static RubyNumeric randCommon(ThreadContext context, Ruby runtime, Random random, IRubyObject recv, IRubyObject arg) {
         if (arg instanceof RubyBignum) {
             byte[] bigCeilBytes = ((RubyBignum) arg).getValue().toByteArray();
             BigInteger bigCeil = new BigInteger(bigCeilBytes).abs();
             byte[] randBytes = new byte[bigCeilBytes.length];
             random.nextBytes(randBytes);
             BigInteger result = new BigInteger(randBytes).abs().mod(bigCeil);
             return new RubyBignum(runtime, result);
         }
 
         RubyInteger integerCeil = (RubyInteger)RubyKernel.new_integer(context, recv, arg);
         long ceil = Math.abs(integerCeil.getLongValue());
         if (ceil == 0) return RubyFloat.newFloat(runtime, random.nextDouble());
         if (ceil > Integer.MAX_VALUE) return runtime.newFixnum(Math.abs(random.nextLong()) % ceil);
 
         return runtime.newFixnum(random.nextInt((int) ceil));
     }
 
     @JRubyMethod(name = "spawn", required = 1, rest = true, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyFixnum spawn(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         long pid = ShellLauncher.runWithoutWait(runtime, args);
         return RubyFixnum.newFixnum(runtime, pid);
     }
 
     @JRubyMethod(name = "syscall", required = 1, optional = 9, module = true, visibility = PRIVATE)
     public static IRubyObject syscall(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         throw context.getRuntime().newNotImplementedError("Kernel#syscall is not implemented in JRuby");
     }
 
     @JRubyMethod(name = "system", required = 1, rest = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyBoolean system(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         return systemCommon(context, recv, args) == 0 ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "system", required = 1, rest = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject system19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int resultCode = systemCommon(context, recv, args);
         switch (resultCode) {
             case 0: return runtime.getTrue();
             case 127: return runtime.getNil();
             default: return runtime.getFalse();
         }
     }
 
     private static int systemCommon(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int resultCode;
 
         try {
             if (! Platform.IS_WINDOWS && args[args.length -1].asJavaString().matches(".*[^&]&\\s*")) {
                 // looks like we need to send process to the background
                 ShellLauncher.runWithoutWait(runtime, args);
                 return 0;
             }
             resultCode = ShellLauncher.runAndWait(runtime, args);
         } catch (Exception e) {
             resultCode = 127;
         }
 
         context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return resultCode;
     }
     
     @JRubyMethod(name = {"exec"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject exec(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         // This is a fairly specific hack for empty string, but it does the job
         if (args.length == 1 && args[0].convertToString().isEmpty()) {
             throw runtime.newErrnoENOENTError(args[0].convertToString().toString());
         }
         
         int resultCode;
         try {
             // TODO: exec should replace the current process.
             // This could be possible with JNA. 
             resultCode = ShellLauncher.execAndWait(runtime, args);
         } catch (RaiseException e) {
             throw e; // no need to wrap this exception
         } catch (Exception e) {
             throw runtime.newErrnoENOENTError("cannot execute");
         }
         
         exit(runtime, new IRubyObject[] {runtime.newFixnum(resultCode)}, true);
 
         // not reached
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "fork", module = true, visibility = PRIVATE)
     public static IRubyObject fork(ThreadContext context, IRubyObject recv, Block block) {
         Ruby runtime = context.getRuntime();
         
         if (!RubyInstanceConfig.FORK_ENABLED) {
             throw runtime.newNotImplementedError("fork is unsafe and disabled by default on JRuby");
         }
         
         if (block.isGiven()) {
             int pid = runtime.getPosix().fork();
             
             if (pid == 0) {
                 try {
                     block.yield(context, runtime.getNil());
                 } catch (RaiseException re) {
                     if (re.getException() instanceof RubySystemExit) {
                         throw re;
                     }
                     return exit_bang(recv, new IRubyObject[] {RubyFixnum.minus_one(runtime)});
                 } catch (Throwable t) {
                     return exit_bang(recv, new IRubyObject[] {RubyFixnum.minus_one(runtime)});
                 }
                 return exit_bang(recv, new IRubyObject[] {RubyFixnum.zero(runtime)});
             } else {
                 return runtime.newFixnum(pid);
             }
         } else {
             int result = runtime.getPosix().fork();
         
             if (result == -1) {
                 return runtime.getNil();
             }
 
             return runtime.newFixnum(result);
         }
     }
 
     @JRubyMethod(module = true)
     public static IRubyObject tap(ThreadContext context, IRubyObject recv, Block block) {
         block.yield(context, recv);
         return recv;
     }
 
     @JRubyMethod(name = {"to_enum", "enum_for"}, rest = true, compat = RUBY1_9)
     public static IRubyObject to_enum(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         switch (args.length) {
         case 0: return enumeratorize(runtime, recv, "each");
         case 1: return enumeratorize(runtime, recv, args[0].asJavaString());
         case 2: return enumeratorize(runtime, recv, args[0].asJavaString(), args[1]);
         default:
             IRubyObject enumArgs[] = new IRubyObject[args.length - 1];
             System.arraycopy(args, 1, enumArgs, 0, enumArgs.length);
             return enumeratorize(runtime, recv, args[0].asJavaString(), enumArgs);
         }
     }
 
     @JRubyMethod(name = { "__method__", "__callee__" }, module = true, visibility = PRIVATE, reads = METHODNAME, omit = true)
     public static IRubyObject __method__(ThreadContext context, IRubyObject recv) {
         String frameName = context.getFrameName();
         if (frameName == null) {
             return context.nil;
         }
         return context.runtime.newSymbol(frameName);
     }
 
     @JRubyMethod(module = true, compat = RUBY1_9)
     public static IRubyObject singleton_class(IRubyObject recv) {
         return recv.getSingletonClass();
     }
 
     @JRubyMethod(rest = true, compat = RUBY1_9)
     public static IRubyObject public_send(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         recv.getMetaClass().checkMethodBound(context, args, PUBLIC);
         return ((RubyObject)recv).send19(context, args, Block.NULL_BLOCK);
     }
 
     // Moved binding of these methods here, since Kernel can be included into
     // BasicObject subclasses, and these methods must still work.
     // See JRUBY-4871
 
     @JRubyMethod(name = "==", required = 1, compat = RUBY1_8)
     public static IRubyObject op_equal(ThreadContext context, IRubyObject self, IRubyObject other) {
         return ((RubyBasicObject)self).op_equal(context, other);
     }
 
     @JRubyMethod(name = "equal?", required = 1, compat = RUBY1_8)
     public static IRubyObject equal_p(ThreadContext context, IRubyObject self, IRubyObject other) {
         return ((RubyBasicObject)self).equal_p(context, other);
     }
 
     @JRubyMethod(name = "eql?", required = 1)
     public static IRubyObject eql_p(IRubyObject self, IRubyObject obj) {
         return ((RubyBasicObject)self).eql_p(obj);
     }
 
     @JRubyMethod(name = "===", required = 1)
     public static IRubyObject op_eqq(ThreadContext context, IRubyObject self, IRubyObject other) {
         return ((RubyBasicObject)self).op_eqq(context, other);
     }
 
     @JRubyMethod(name = "<=>", required = 1, compat = RUBY1_9)
     public static IRubyObject op_cmp(ThreadContext context, IRubyObject self, IRubyObject other) {
         return ((RubyBasicObject)self).op_cmp(context, other);
     }
 
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = PRIVATE)
diff --git a/test/test_kernel.rb b/test/test_kernel.rb
index 1584384daf..f7fb492c1a 100644
--- a/test/test_kernel.rb
+++ b/test/test_kernel.rb
@@ -1,792 +1,802 @@
 require 'test/unit'
 require 'rbconfig'
 require 'test/test_helper'
 require 'pathname'
 
 class TestKernel < Test::Unit::TestCase
   include TestHelper
 
   def log(msg)
     $stderr.puts msg if $VERBOSE
   end
 
   TESTAPP_DIR = File.expand_path(File.join(File.dirname(__FILE__), 'testapp'))
 
   if (WINDOWS)
     # the testapp.exe exists for sure, it is pre-built
     TESTAPP_EXISTS = true
   else
     Dir.chdir(TESTAPP_DIR) {
       file = File.join(TESTAPP_DIR, 'testapp')
       `gcc testapp.c -o testapp` unless (File.exist?(file))
        TESTAPP_EXISTS = File.exist?(file)
     }
   end
 
   TESTAPP_REL = File.join(File.join(File.dirname(__FILE__), 'testapp'), 'testapp')
   TESTAPP_REL_NONORM = File.join(File.join(File.join(File.join(File.dirname(__FILE__), 'testapp'), '..'), 'testapp'), 'testapp')
   TESTAPP = File.expand_path(TESTAPP_REL)
   TESTAPP_NONORM = File.join(File.join(File.join(TESTAPP_DIR, '..'), 'testapp'), 'testapp')
 
   # TODO: path with spaces!
   TESTAPPS = [] << TESTAPP_REL << TESTAPP << TESTAPP_REL_NONORM << TESTAPP_NONORM
 
   if WINDOWS
     TESTAPPS << TESTAPP_REL + ".exe"
     TESTAPPS << (TESTAPP_REL + ".exe").gsub('/', '\\')
     TESTAPPS << TESTAPP_REL + ".bat"
     TESTAPPS << (TESTAPP_REL + ".bat").gsub('/', '\\')
     TESTAPPS << TESTAPP + ".exe"
     TESTAPPS << (TESTAPP + ".exe").gsub('/', '\\')
     TESTAPPS << TESTAPP + ".bat"
     TESTAPPS << (TESTAPP + ".bat").gsub('/', '\\')
   end
 
   def test_stuff_seemingly_out_of_context
     assert !Kernel.eval("defined? some_unknown_variable")
     assert_equal nil, true && defined?(unknownConstant)
 
     # JRUBY-117 - to_a should be public
     assert_equal ["to_a"], Object.public_instance_methods.grep("to_a")
 
     # JRUBY-117 - remove_instance_variable should be private
     assert_equal ["remove_instance_variable"], Object.private_instance_methods.grep("remove_instance_variable")
   end
 
 
   def test_Array_should_return_empty_array_for_nil
     assert_equal [], Kernel.Array(nil)
   end
 
   class ToAryReturnsAnArray; def to_ary() [1] end; end
   class ToAryReturnsAnInteger; def to_ary() 2 end; end
 
   def test_Array_should_use_to_ary_and_make_sure_that_it_returns_either_array_or_nil
     assert_equal [1], Kernel.Array(ToAryReturnsAnArray.new)
     assert_raises(TypeError) { Kernel.Array(ToAryReturnsAnInteger.new) }    
   end
 
   class BothToAryAndToADefined; def to_ary() [3] end; def to_a() raise "to_a() should not be called" end; end
   class OnlyToADefined; def to_a() [4] end; end
   def test_Array_should_fallback_on_to_a_if_to_ary_is_not_defined
     assert_equal [3], Kernel.Array(BothToAryAndToADefined.new)
     assert_equal [4], Kernel.Array(OnlyToADefined.new)
   end
 
   class NeitherToANorToAryDefined; end
   def test_Array_should_return_array_containing_argument_if_the_argument_has_neither_to_ary_nor_to_a
     assert_equal [1], Kernel.Array(1)
     assert_equal [:foo], Kernel.Array(:foo)
     obj = NeitherToANorToAryDefined.new 
     assert_equal [obj], Kernel.Array(obj)
   end
 
   class ToAryReturnsNil; def to_ary() nil end; end
   def test_Array_should_return_array_containing_arg_if_to_ary_returns_nil
     obj = ToAryReturnsNil.new
     assert_equal [obj], Kernel.Array(obj)
   end
 
   def test_Integer
     assert_equal 10, Kernel.Integer("0xA")
     assert_equal 8, Kernel.Integer("010")
     assert_equal 2, Kernel.Integer("0b10")
     assert_raises(ArgumentError) { Kernel.Integer("abc") }
     assert_raises(ArgumentError) { Kernel.Integer("x10") }
     assert_raises(ArgumentError) { Kernel.Integer("xxxx10000000000000000000000000000000000000000000000000000") }
   end
 
   def test_Float
     assert_equal 1.0, Kernel.Float("1")
     assert_equal 10.0, Kernel.Float("1e1")
     assert_raises(ArgumentError) { Kernel.Float("abc") }
     assert_raises(ArgumentError) { Kernel.Float("x10") }
     assert_raises(ArgumentError) { Kernel.Float("xxxx10000000000000000000000000000000000000000000000000000") }
   end
 
 #  String
 #  URI
 #  `
 #  abort
 #  at_exit
 #  binding
 #  block_given?
   class CheckBlockGiven; def self.go() block_given? end; end
   def test_iterator?
     assert !(Kernel.block_given?)
     assert(CheckBlockGiven.go { true })
     assert(!CheckBlockGiven.go)
     assert(!CheckBlockGiven.go(&Proc.new))
   end
 
 #  callcc
 #  caller
 
   def test_catch_throw
     been_where_it_shouldnt = false
 
     catch :fred do throw :fred; fail("shouldn't get here") end
     assert(!been_where_it_shouldnt)
 
     assert_raises (NameError) do
       catch :fred do throw :wilma end
     end
   end
 
   def test_throw_should_bubble_up_to_the_right_catch
     been_at_fred2 = false
     catch :fred1 do
       catch :fred2 do
         catch :fred3 do
           catch :fred4 do
             throw :fred2
             fail("should have jumped to fred2 catch")
           end
           fail("should have jumped to fred2 catch")
         end
       end
       been_at_fred2 = true
     end
     assert been_at_fred2 
   end
 
   def test_invalid_throw_after_inner_catch_should_unwind_the_stack_all_the_way_to_the_top
     been_at_fred1 = false
     been_at_fred2 = false
     assert_raises(NameError) do
       catch :fred1 do
         catch :fred2 do
           catch :fred3 do
             throw :fred2
             test_fail("should have jumped to after fred2 catch")
           end
           test_fail("should have jumped to after fred2 catch")
         end
         been_at_fred2 = true
         throw :wilma
       end
       been_at_fred1 = true
     end
     assert been_at_fred2
     assert !been_at_fred1
   end
 
   def throw_outside_of_any_block_shoudl_raise_an_error
     assert_raises (NameError) { throw :wilma }
   end
 
   def test_catch_stack_should_be_cleaned_up
     assert_raises(NameError) do
       catch :fred1 do
         catch :fred2 do
           catch :fred3 do
           end
         end
       end
       throw :fred2
       fail "catch stack should have been cleaned up"
     end
   end
   
 #  chomp
 #  chomp!
 
   # JRUBY-2527
   def test_chomp_no_block_doesnt_break
     $_ = "test"
     assert_equal("test", chomp)
     assert_equal("te", chomp("st"))
 
     $_ = "test"
     chomp!
     assert_equal("test", $_)
 
     chomp!("st")
     assert_equal("te", $_)
   end
 
 #  chop
 #  chop!
 
 
   #  eval
   def test_eval_should_use_local_variable_defined_in_parent_scope
     x = 1
     assert_equal 1, Kernel.eval('x')
     Kernel.eval("x = 2")
     assert_equal 2, x
   end
 
   def test_eval_should_not_bring_local_variables_defined_in_its_input_to_parent_scope
     existing_variable = 0
     another_variable = 1
     assert !(defined? new_variable)
 
     Kernel.eval("new_variable = another_variable = existing_variable")
 
     assert_equal 0, existing_variable 
     assert_equal 0, another_variable 
     assert !(defined? new_variable)
   end
 
 #  exec
   # should not really work, since there is no fork
 
 #  exit
 #  exit!
 #  fail
 #  fork
 
   def test_format
     assert_equal "Hello, world", Kernel.format("Hello, %s", "world") 
     assert_raises(TypeError) { Kernel.format("%01.3f", nil) }
   end
 
 #  getc
 #  gets
 #  global_variables
 #  gsub
 #  gsub!
 
   class CheckIterator; def self.go() iterator? end; end
   def test_iterator?
     assert !(Kernel.iterator?)
     assert(CheckIterator.go { true })
     assert(!CheckIterator.go)
   end
 
 #  lambda
 #  load
   class ToStrPointToRequireTarget
     attr_accessor :target_required
     def to_str() "#{File.dirname(__FILE__)}/require_target.rb" end
   end
 
   def test_load_should_call_to_str_on_arg
     $target_required = false
     Kernel.load ToStrPointToRequireTarget.new
     assert $target_required
 
     assert_raises(TypeError) { Kernel.load Object.new }
   end
 
   def test_shall_load_from_load_path
     tmp = ENV["TEMP"] || ENV["TMP"] || ENV["TMPDIR"] || "/tmp"
     Dir.chdir(tmp) do
       load_path_save = $LOAD_PATH
       begin
         File.open(File.expand_path('.') +'/file_to_be_loaded','w' ) do |f|
           f.puts "raise"
         end
         $LOAD_PATH.delete_if{|dir| dir=='.'}
         assert_raise(LoadError) {
           load 'file_to_be_loaded'
         }
       ensure
         File.delete(File.expand_path('.') +'/file_to_be_loaded')
         $LOAD_PATH.clear
         $LOAD_PATH << load_path_save
       end
     end
   end
 
   def test_local_variables
     if Config::CONFIG['ruby_install_name'] == 'jruby'
       a = lambda do
         assert_equal %w(a b), local_variables
         b = 1
         assert_equal %w(a b), local_variables
       end
       a.call
     else
       # This behaves like MRI 1.9, and fails on 1.8, so, skip it
     end
   end
 
 #  loop
 #  method_missing
 #  open
 #  p
 #  print
 
   def test_printf_should_raise_argument_error_when_asked_to_format_string_as_a_number
     assert_raises(ArgumentError) { Kernel.printf "%d", 's' }
   end
 
 #  proc
 #  putc
 #  puts
 
   def test_raise
     assert_raises(RuntimeError) { Kernel.raise }
     assert_raises(StandardError) { Kernel.raise(StandardError) }
     assert_raises(TypeError) { Kernel.raise(TypeError.new) }
 
     begin
       Kernel.raise(StandardError, 'oops')
       fail "shouldn't get here!"
     rescue => e1
       assert e1.is_a?(StandardError)
       assert_equal 'oops', e1.message
     end
 
     begin
       Kernel.raise('oops')
       fail "shouldn't get here!"
     rescue => e2
       assert e2.is_a?(StandardError)
       assert_equal 'oops', e2.message
     end
   end
 
   # JRUBY-2696
   def test_raise_in_debug_mode
     require 'stringio'
     old_debug, $DEBUG = $DEBUG, true
 
     # by Exception Class
     $stderr = StringIO.new
     raise StandardError rescue nil
     tobe = "Exception `StandardError' at #{__FILE__}:#{__LINE__ - 1} - StandardError"
     assert_equal(tobe, $stderr.string.split("\n")[0])
 
     # by String
     $stderr.reopen
     raise "TEST_ME" rescue nil
     tobe = "Exception `RuntimeError' at #{__FILE__}:#{__LINE__ - 1} - TEST_ME"
     assert_equal(tobe, $stderr.string.split("\n")[0])
 
     # by Exception
     $stderr.reopen
     raise RuntimeError.new("TEST_ME") rescue nil
     tobe = "Exception `RuntimeError' at #{__FILE__}:#{__LINE__ - 1} - TEST_ME"
     assert_equal(tobe, $stderr.string.split("\n")[0])
 
     # by re-raise
     $stderr.reopen
     raise "TEST_ME" rescue raise rescue nil
     tobe = "Exception `RuntimeError' at #{__FILE__}:#{__LINE__ - 1} - TEST_ME"
     traces = $stderr.string.split("\n")
     assert_equal(tobe, traces[0])
     assert_equal(tobe, traces[1])
   ensure
     $DEBUG = old_debug
     $stderr = STDERR
   end
 
 #  rand
 #  readline
 #  readlines
 #  scan
 #  select
 #  set_trace_func
 
 
   def test_sleep
     assert_raises(ArgumentError) { sleep(-10) }
     # FIXME: below is true for MRI, but not for JRuby 
     # assert_raises(TypeError) { sleep "foo" }
     assert_equal 0, sleep(0)
     t1 = Time.now
     sleep(0.1)
     t2 = Time.now
     # this should cover systems with 10 msec clock resolution
     assert t2 >= t1 + 0.08
   end
 
   def test_sprintf
     assert_equal 'Hello', Kernel.sprintf('Hello')
   end
 
   def test_sprintf_with_object
     obj = Object.new
     def obj.to_int() 4 end
     assert_equal '4', Kernel.sprintf('%d', obj)
   end
 
   # JRUBY-4802
   def test_sprintf_float
     assert_equal "0.00000", Kernel.sprintf("%.5f", 0.000004)
     assert_equal "0.00001", Kernel.sprintf("%.5f", 0.000005)
     assert_equal "0.00001", Kernel.sprintf("%.5f", 0.000006)
   end
 
   def test_srand
     Kernel.srand
     Kernel.srand(0)
     Kernel.srand(:foo)
     assert_raises(TypeError) { Kernel.srand([:foo]) }
   end
 
 #  sub
 #  sub!
 #  syscall
 
   def test_system
     assert_equal false, system('non_existant_command')
     # TODO: test for other OSes (Windows, for example, wouldn't know what to do with /dev/null)
     if Config::CONFIG['target_os'] == 'linux'
       assert_equal true, system('echo > /dev/null')
     end
   end
 
   def test_system_empty
     assert_equal false, system('')
   end
 
   def test_system_existing
     quiet do
       if (WINDOWS)
         res = system('cd')
       else
         res = system('pwd')
       end
       assert_equal true, res
     end
   end
 
   def test_system_existing_with_leading_space
     quiet do
       if (WINDOWS)
         res = system(' cd')
       else
         res = system(' pwd')
       end
       assert_equal true, res
     end
   end
 
   def test_system_existing_with_trailing_space
     quiet do
       if (WINDOWS)
         res = system('cd ')
       else
         res = system('pwd ')
       end
       assert_equal true, res
     end
   end
 
   def test_system_non_existing
     res =  system('program-that-doesnt-exist-for-sure')
     assert_equal false, res
   end
 
   def test_system_existing_with_arg
     if (WINDOWS)
       res = system('cd .')
     else
       res = system('pwd . 2>&1 > /dev/null')
     end
     assert_equal true, res
   end
 
   def test_system_non_existing_with_arg
     res = system('program-that-doesnt-exist-for-sure .')
     assert_equal false, res
   end
 
   def test_system_non_existing_with_args
     res = system('program-that-doesnt-exist-for-sure','arg1','arg2')
     assert_equal false, res
   end
 
   def test_exec_empty
       assert_raise(Errno::ENOENT) {
         exec("")
       }
   end
 
   def test_exec_non_existing
     assert_raise(Errno::ENOENT) {
       exec("program-that-doesnt-exist-for-sure")
     }
   end
 
   def test_exec_non_existing_with_args
     assert_raise(Errno::ENOENT) {
       exec("program-that-doesnt-exist-for-sure", "arg1", "arg2")
     }
   end
 
   # JRUBY-4834
   def test_backquote_with_changed_path
     orig_env = ENV['PATH']
 
     # Append a directory where testapp resides to the PATH
     paths = (ENV["PATH"] || "").split(File::PATH_SEPARATOR)
     paths.unshift TESTAPP_DIR
     ENV["PATH"] = paths.uniq.join(File::PATH_SEPARATOR)
 
     res = `testapp`.chomp
     assert_equal("NO_ARGS", res)
   ensure
     ENV['PATH'] = orig_env
   end
 
   # JRUBY-4127
   def test_backquote_with_quotes
     if (WINDOWS)
       result = `"#{TESTAPP_NONORM}" #{Dir.pwd}`.strip
     else
       result = `"pwd" .`.strip
     end
     expected = Dir.pwd
     assert_equal(expected, result)
   end
 
   def test_backquote1
     if (WINDOWS)
       result = `cmd /c cd`.strip.gsub('\\', '/')
     else
       result = `sh -c pwd`.strip
     end
     expected = Dir.pwd
     assert_equal(expected, result)
   end
 
   def test_backquote1_1
     if (WINDOWS)
       result = `cmd.exe /c cd`.strip.gsub('\\', '/')
     else
       result = `pwd`.strip
     end
     expected = Dir.pwd
     assert_equal(expected, result)
   end
 
   def test_backquote2
     TESTAPPS.each { |app|
       if (app =~ /\/.*\.bat/ && Pathname.new(app).relative?)
         # MRI can't launch relative BAT files with / in their paths
         log "-- skipping #{app}"
         next
       end
       log "testing #{app}"
 
       result = `#{app}`.strip
       assert_equal('NO_ARGS', result, "Can't properly launch '#{app}'")
     }
   end
 
   def test_backquote2_1
     TESTAPPS.each { |app|
       if (app =~ /\/.*\.bat/ && Pathname.new(app).relative?)
         # MRI can't launch relative BAT files with / in their paths
         log "-- skipping #{app}"
         next
       end
       log "testing #{app}"
 
       result = `#{app} one`.strip
       assert_equal('one', result, "Can't properly launch '#{app}'")
     }
   end
 
   def test_backquote3
     TESTAPPS.each { |app|
       if (app =~ /\// && Pathname.new(app).relative? && WINDOWS)
         log "-- skipping #{app}"
         next
       end
 
       if (TESTAPP_DIR =~ /\s/) # spaces in paths, quote!
         app = '"' + app + '"'
       end
 
       log "testing #{app}"
 
       result = `#{app} 2>&1`.strip
       assert_equal("NO_ARGS", result, "Can't properly launch '#{app}'")
     }
   end
 
   def test_backquote3_1
     TESTAPPS.each { |app|
       if (app =~ /\// && Pathname.new(app).relative? && WINDOWS)
         log "-- skipping #{app}"
         next
       end
 
       if (TESTAPP_DIR =~ /\s/) # spaces in paths, quote!
         app = '"' + app + '"'
       end
 
       log "testing #{app}"
 
       result = `#{app} one 2>&1`.strip
       assert_equal("one", result, "Can't properly launch '#{app}'")
     }
   end
 
   def test_backquote3_2
     TESTAPPS.each { |app|
       if (app =~ /\// && Pathname.new(app).relative? && WINDOWS)
         log "-- skipping #{app}"
         next
       end
 
       if (TESTAPP_DIR =~ /\s/) # spaces in paths, quote!
         app = '"' + app + '"'
       end
 
       log "testing #{app}"
 
       result = `#{app} one two three 2>&1`.strip.split(/[\r\n]+/)
       assert_equal(%w[one two three], result, "Can't properly launch '#{app}'")
 
       # TODO: \r\n vs \n
       # result = `#{app} one two three 2>&1`.strip
       # assert_equal("one\ntwo\nthree", result, "Can't properly launch '#{app}'")
     }
   end
 
   def test_backquote4
     TESTAPPS.each { |app|
       if (app =~ /\/.*\.bat/ && Pathname.new(app).relative?)
         # MRI can't launch relative BAT files with / in their paths
         log "-- skipping #{app}"
         next
       end
       log "testing #{app}"
 
       result = `#{app} "" two`.split(/[\r\n]+/)
       assert_equal(["", "two"], result, "Can't properly launch '#{app}'")
 
       result = `#{app} ""`.chomp
       assert_equal('', result, "Can't properly launch '#{app}'")
 
       result = `#{app} "   "`.chomp
       assert_equal('   ', result, "Can't properly launch '#{app}'")
 
       result = `#{app} a""`.chomp
       assert_equal('a', result, "Can't properly launch '#{app}'")
 
       result = `#{app} a" "`.chomp
       assert_equal('a ', result, "Can't properly launch '#{app}'")
 
       result = `#{app} ""b`.chomp
       assert_equal('b', result, "Can't properly launch '#{app}'")
 
       result = `#{app} " "b`.chomp
       assert_equal(' b', result, "Can't properly launch '#{app}'")
 
       result = `#{app} a""b`.chomp
       assert_equal('ab', result, "Can't properly launch '#{app}'")
 
       result = `#{app} a"   "b`.chomp
       assert_equal('a   b', result, "Can't properly launch '#{app}'")
 
       result = `#{app} a"   "b"  "c`.chomp
       assert_equal('a   b  c', result, "Can't properly launch '#{app}'")
 
       result = `#{app} a"   "b""c`.chomp
       assert_equal('a   bc', result, "Can't properly launch '#{app}'")
     }
   end
 
   def test_backquote4_1
     TESTAPPS.each { |app|
       if (app =~ /\// && Pathname.new(app).relative? && WINDOWS)
         log "-- skipping #{app}"
         next
       end
       log "testing #{app}"
       
       if (TESTAPP_DIR =~ /\s/) # spaces in paths, quote!
         app = '"' + app + '"'
       end
 
       result = `#{app} "   " 2>&1`.chomp
       assert_equal('   ', result, "Can't properly launch '#{app}'")
 
       result = `#{app} a"" 2>&1`.strip
       assert_equal('a', result, "Can't properly launch '#{app}'")
 
       result = `#{app} ""b 2>&1`.strip
       assert_equal('b', result, "Can't properly launch '#{app}'")
 
       result = `#{app} a""b 2>&1`.strip
       assert_equal('ab', result, "Can't properly launch '#{app}'")
 
       result = `#{app} a"   "b 2>&1`.strip
       assert_equal('a   b', result, "Can't properly launch '#{app}'")
     }
   end
 
   def test_backquote_with_executable_in_cwd
     Dir.chdir(TESTAPP_DIR) do
       result = `./testapp one`
       assert_equal 0, $?.exitstatus
       assert_equal "one", result.rstrip
     end
   end
 
   if (WINDOWS)
     def test_backquote_with_executable_in_cwd_2
       Dir.chdir(TESTAPP_DIR) do
         result = `testapp one`
         assert_equal 0, $?.exitstatus
         assert_equal "one", result.rstrip
       end
     end
   end
 
   # JRUBY-4518
   if (WINDOWS)
     def test_backquote_with_CRLF
       assert_no_match(/\r/, `cd`)
       assert_equal 0, $?.exitstatus
     end
   end
 
   def test_system_with_executable_in_cwd
     Dir.chdir(TESTAPP_DIR) do
       result = nil
       quiet do
         result = system("./testapp one")
       end
       assert_equal 0, $?.exitstatus
       assert result
     end
   end
 
   if (WINDOWS)
     def test_system_with_executable_in_cwd_2
       Dir.chdir(TESTAPP_DIR) do
         result = nil
         quiet do
           result = system("testapp one")
         end
         assert_equal 0, $?.exitstatus
         assert result
       end
     end
   end
 
   def test_test
     assert "Test file existence", test(?f, "README")
     assert "Test file non-existence", !test(?f, "READMEaewertsert45t4w5tgrsfdgrf")
     # Make sure that absolute paths work for testing
     assert "Test should handle absolute paths", test(?f, File.expand_path("README"))
   end
 
   # JRUBY-4348
   def test_exec_rubyopt
     old = ENV['RUBYOPT']
     ENV['RUBYOPT'] = "-v"
     result =  `ruby -e "a=1"`
     assert_equal 0, $?.exitstatus
     assert_match /ruby/i, result
   ensure
     ENV['RUBYOPT'] = old
   end
 
+  # JRUBY-5431
+  def test_exit_bang
+    `bin/jruby -e "exit!"`
+    assert_equal 1, $?.exitstatus
+    `bin/jruby -e "exit!(true)"`
+    assert_equal 0, $?.exitstatus
+    `bin/jruby -e "exit!(false)"`
+    assert_equal 1, $?.exitstatus
+  end
+
 #  test
 #  trace_var
 #  trap
 #  untrace_var
 #  warn
 
 end
 
