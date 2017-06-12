diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 32287c147e..373a9a8238 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1319 +1,1323 @@
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
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodNBlock;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.backtrace.RubyStackTraceElement;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.util.ByteList;
 import org.jruby.util.ConvertBytes;
 import org.jruby.util.IdUtil;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.cli.Options;
 
 import java.io.ByteArrayOutputStream;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Map;
 
 import static org.jruby.CompatVersion.RUBY1_8;
 import static org.jruby.CompatVersion.RUBY1_9;
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.anno.FrameField.BACKREF;
 import static org.jruby.anno.FrameField.BLOCK;
 import static org.jruby.anno.FrameField.LASTLINE;
 import static org.jruby.anno.FrameField.METHODNAME;
 import static org.jruby.runtime.Visibility.PRIVATE;
 import static org.jruby.runtime.Visibility.PROTECTED;
 import static org.jruby.runtime.Visibility.PUBLIC;
 
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
             return methodMissing(context, self, clazz, name, args, block);
         }
 
         public abstract IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block);
 
     }
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         runtime.setKernel(module);
 
         module.defineAnnotatedMethods(RubyKernel.class);
         
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
 
         recacheBuiltinMethods(runtime);
 
         return module;
     }
 
     /**
      * Cache built-in versions of several core methods, to improve performance by using identity comparison (==) rather
      * than going ahead with dynamic dispatch.
      *
      * @param runtime
      */
     static void recacheBuiltinMethods(Ruby runtime) {
         RubyModule module = runtime.getKernel();
 
         runtime.setRespondToMethod(module.searchMethod("respond_to?"));
 
         if (!runtime.is1_9()) { // method_missing is in BasicObject in 1.9
             runtime.setDefaultMethodMissing(module.searchMethod("method_missing"));
         }
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject at_exit(ThreadContext context, IRubyObject recv, Block block) {
         return context.runtime.pushExitBlock(context.runtime.newProc(Block.Type.PROC, block));
     }
 
     @JRubyMethod(name = "autoload?", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject autoload_p(ThreadContext context, final IRubyObject recv, IRubyObject symbol) {
         Ruby runtime = context.runtime;
         final RubyModule module = getModuleForAutoload(runtime, recv);
         String name = symbol.asJavaString();
         
         String file = module.getAutoloadFile(name);
         return (file == null) ? runtime.getNil() : runtime.newString(file);
     }
 
     @JRubyMethod(required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         Ruby runtime = recv.getRuntime(); 
         String nonInternedName = symbol.asJavaString();
         
         if (!IdUtil.isValidConstantName(nonInternedName)) {
             throw runtime.newNameError("autoload must be constant name", nonInternedName);
         }
 
         if (!runtime.is1_9() && !(file instanceof RubyString)) throw runtime.newTypeError(file, runtime.getString());
 
         RubyString fileString = RubyFile.get_path(runtime.getCurrentContext(), file);
         
         if (fileString.isEmpty()) throw runtime.newArgumentError("empty file name");
         
         final String baseName = symbol.asJavaString().intern(); // interned, OK for "fast" methods
         final RubyModule module = getModuleForAutoload(runtime, recv);
         
         IRubyObject existingValue = module.fetchConstant(baseName); 
         if (existingValue != null && existingValue != RubyObject.UNDEF) return runtime.getNil();
 
         module.defineAutoload(baseName, new IAutoloadMethod() {
             public String file() {
                 return file.toString();
             }
 
             public void load(Ruby runtime) {
                 if (runtime.getLoadService().autoloadRequire(file())) {
                     // Do not finish autoloading by cyclic autoload 
                     module.finishAutoload(baseName);
                 }
             }
         });
         return runtime.getNil();
     }
 
     private static RubyModule getModuleForAutoload(Ruby runtime, IRubyObject recv) {
         RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : recv.getMetaClass().getRealClass();
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
 
         if (args.length == 0 || !(args[0] instanceof RubySymbol)) {
             throw context.runtime.newArgumentError("no id given");
         }
 
         return methodMissingDirect(context, recv, (RubySymbol)args[0], lastVis, lastCallType, args, block);
     }
 
     protected static IRubyObject methodMissingDirect(ThreadContext context, IRubyObject recv, RubySymbol symbol, Visibility lastVis, CallType lastCallType, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
         
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
         Ruby runtime = context.runtime;
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
     
 
     private static IRubyObject[] popenArgs(Ruby runtime, String pipedArg, IRubyObject[] args) {
             IRubyObject command = runtime.newString(pipedArg.substring(1));
 
             if (args.length >= 2) return new IRubyObject[] { command, args[1] };
 
             return new IRubyObject[] { command };
     }
     
     @JRubyMethod(required = 1, optional = 2, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         String arg = args[0].convertToString().toString();
         Ruby runtime = context.runtime;
 
         // exec process, create IO with process
         if (arg.startsWith("|")) return RubyIO.popen(context, runtime.getIO(), popenArgs(runtime, arg, args), block);
 
         return RubyFile.open(context, runtime.getFile(), args, block);
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject open19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
         if (args[0].respondsTo("to_open")) {
             args[0] = args[0].callMethod(context, "to_open");
             return RubyFile.open(context, runtime.getFile(), args, block);
         } 
+        
+        if (args[0].respondsTo("to_path")) {
+            args[0] = args[0].callMethod(context, "to_path");
+        }
 
         String arg = args[0].convertToString().toString();
 
         // exec process, create IO with process
         if (arg.startsWith("|")) return RubyIO.popen19(context, runtime.getIO(), popenArgs(runtime, arg, args), block);
         
         return RubyFile.open(context, runtime.getFile(), args, block);
     }
 
     @JRubyMethod(name = "getc", module = true, visibility = PRIVATE)
     public static IRubyObject getc(ThreadContext context, IRubyObject recv) {
         context.runtime.getWarnings().warn(ID.DEPRECATED_METHOD, "getc is obsolete; use STDIN.getc instead");
         IRubyObject defin = context.runtime.getGlobalVariables().get("$stdin");
         return defin.callMethod(context, "getc");
     }
 
     @JRubyMethod(name = "gets", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject gets(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.gets(context, context.runtime.getArgsFile(), args);
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject abort(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
 
         if(args.length == 1) {
             runtime.getGlobalVariables().get("$stderr").callMethod(context,"puts",args[0].convertToString());
         }
         
         exit(runtime, new IRubyObject[] { runtime.getFalse() }, false);
         return runtime.getNil(); // not reached
     }
 
     @JRubyMethod(name = "Array", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_array(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return RuntimeHelpers.arrayValue(context, context.runtime, object);
     }
 
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.runtime.getComplex(), "convert");
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.runtime.getComplex(), "convert", arg);
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.runtime.getComplex(), "convert", arg0, arg1);
     }
     
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.runtime.getRational(), "convert");
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.runtime.getRational(), "convert", arg);
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.runtime.getRational(), "convert", arg0, arg1);
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
     
     @JRubyMethod(name = "Hash", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_hash(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         IRubyObject tmp;
         Ruby runtime = recv.getRuntime();
         if (arg.isNil()) return RubyHash.newHash(runtime);
         tmp = TypeConverter.checkHashType(runtime, arg);
         if (tmp.isNil()) {
             if (arg instanceof RubyArray && ((RubyArray) arg).isEmpty()) {
                 return RubyHash.newHash(runtime);
             }
             throw runtime.newTypeError("can't convert " + arg.getMetaClass() + " into Hash");
         }
         return tmp;
     } 
 
     @JRubyMethod(name = "Integer", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject new_integer(ThreadContext context, IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue();
             if (val >= (double) RubyFixnum.MAX || val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(context.runtime,((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(context.runtime,(RubyString)object,0,true);
         }
 
         IRubyObject tmp = TypeConverter.convertToType(object, context.runtime.getInteger(), "to_int", false);
         if (tmp.isNil()) return object.convertToInteger("to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "Integer", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_integer19(ThreadContext context, IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue(); 
             if (val > (double) RubyFixnum.MAX && val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(context.runtime,((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(context.runtime,(RubyString)object,0,true);
         } else if(object instanceof RubyNil) {
             throw context.runtime.newTypeError("can't convert nil into Integer");
         }
 
         IRubyObject tmp = TypeConverter.convertToType(object, context.runtime.getInteger(), "to_int", false);
         if (tmp.isNil()) return object.convertToInteger("to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "Integer", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_integer19(ThreadContext context, IRubyObject recv, IRubyObject object, IRubyObject base) {
         int bs = RubyNumeric.num2int(base);
         if(object instanceof RubyString) {
             return RubyNumeric.str2inum(context.runtime,(RubyString)object,bs,true);
         } else {
             IRubyObject tmp = object.checkStringType();
             if(!tmp.isNil()) {
                 return RubyNumeric.str2inum(context.runtime,(RubyString)tmp,bs,true);
             }
         }
         throw context.runtime.newArgumentError("base specified for non string value");
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject new_string(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType(object, context.runtime.getString(), "to_s");
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_string19(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType19(object, context.runtime.getString(), "to_s");
     }
 
     @JRubyMethod(name = "p", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject p(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
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
         IRubyObject defout = context.runtime.getGlobalVariables().get("$>");
         
         return RubyIO.putc(context, defout, ch);
     }
 
     @JRubyMethod(name = "puts", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject puts(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.runtime.getGlobalVariables().get("$>");
 
         return RubyIO.puts(context, defout, args);
     }
 
     @JRubyMethod(name = "print", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject print(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.runtime.getGlobalVariables().get("$>");
 
         return RubyIO.print(context, defout, args);
     }
 
     @JRubyMethod(name = "printf", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject printf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = context.runtime.getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             defout.callMethod(context, "write", RubyKernel.sprintf(context, recv, args));
         }
 
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "readline", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject readline(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(context, recv, args);
 
         if (line.isNil()) {
             throw context.runtime.newEOFError();
         }
 
         return line;
     }
 
     @JRubyMethod(name = "readlines", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.readlines(context, context.runtime.getArgsFile(), args);
     }
 
     @JRubyMethod(name = "respond_to_missing?", module = true, compat = RUBY1_9)
     public static IRubyObject respond_to_missing_p(ThreadContext context, IRubyObject recv, IRubyObject symbol) {
         return context.runtime.getFalse();
     }
 
     @JRubyMethod(name = "respond_to_missing?", module = true, compat = RUBY1_9)
     public static IRubyObject respond_to_missing_p(ThreadContext context, IRubyObject recv, IRubyObject symbol, IRubyObject isPrivate) {
         return context.runtime.getFalse();
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
 
     @JRubyMethod(name = "sub!", module = true, visibility = PRIVATE, reads = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.runtime).sub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "sub!", module = true, visibility = PRIVATE, reads = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.runtime).sub_bang(context, arg0, arg1, block);
     }
 
     @JRubyMethod(name = "sub", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.runtime).dup();
 
         if (!str.sub_bang(context, arg0, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.runtime).dup();
 
         if (!str.sub_bang(context, arg0, arg1, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.runtime).gsub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "gsub!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.runtime).gsub_bang(context, arg0, arg1, block);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.runtime).dup();
 
         if (!str.gsub_bang(context, arg0, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.runtime).dup();
 
         if (!str.gsub_bang(context, arg0, arg1, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chop!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chop_bang(ThreadContext context, IRubyObject recv, Block block) {
         return getLastlineString(context, context.runtime).chop_bang(context);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chop(ThreadContext context, IRubyObject recv, Block block) {
         RubyString str = getLastlineString(context, context.runtime);
 
         if (str.getByteList().getRealSize() > 0) {
             str = (RubyString) str.dup();
             str.chop_bang(context);
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chomp!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.runtime).chomp_bang(context);
     }
 
     @JRubyMethod(name = "chomp!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.runtime).chomp_bang(context, arg0);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv) {
         RubyString str = getLastlineString(context, context.runtime);
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context).isNil()) {
             return str;
         } 
 
         context.getCurrentScope().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         RubyString str = getLastlineString(context, context.runtime);
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context, arg0).isNil()) {
             return str;
         } 
 
         context.getCurrentScope().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.runtime).split(context);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.runtime).split(context, arg0);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return getLastlineString(context, context.runtime).split(context, arg0, arg1);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = {LASTLINE, BACKREF}, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject scan(ThreadContext context, IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(context, context.runtime).scan(context, pattern, block);
     }
 
     @JRubyMethod(required = 1, optional = 3, module = true, visibility = PRIVATE)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyIO.select_static(context, context.runtime, args);
     }
 
     @JRubyMethod(optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject sleep(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         long milliseconds;
 
         if (args.length == 0) {
             // Zero sleeps forever
             milliseconds = 0;
         } else {
             if (!(args[0] instanceof RubyNumeric)) {
                 throw context.runtime.newTypeError("can't convert " + args[0].getMetaClass().getName() + "into time interval");
             }
             milliseconds = (long) (args[0].convertToFloat().getDoubleValue() * 1000);
             if (milliseconds < 0) {
                 throw context.runtime.newArgumentError("time interval must be positive");
             } else if (milliseconds == 0) {
                 // Explicit zero in MRI returns immediately
                 return context.runtime.newFixnum(0);
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
 
         return context.runtime.newFixnum(Math.round((System.currentTimeMillis() - startTime) / 1000.0));
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
         int status = hard ? 1 : 0;
 
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
         Ruby runtime = context.runtime;
         RubyArray globalVariables = runtime.newArray();
 
         for (String globalVariableName : runtime.getGlobalVariables().getNames()) {
             globalVariables.append(runtime.newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     // In 1.9, return symbols
     @JRubyMethod(name = "global_variables", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyArray global_variables19(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.runtime;
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
         final Ruby runtime = context.runtime;
         RubyArray localVariables = runtime.newArray();
         
         for (String name: context.getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newString(name));
         }
 
         return localVariables;
     }
 
     // In 1.9, return symbols
     @JRubyMethod(name = "local_variables", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyArray local_variables19(ThreadContext context, IRubyObject recv) {
         final Ruby runtime = context.runtime;
         RubyArray localVariables = runtime.newArray();
 
         for (String name: context.getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newSymbol(name));
         }
 
         return localVariables;
     }
     
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static RubyBinding binding(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.runtime, context.currentBinding(recv));
     }
     
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyBinding binding19(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.runtime, context.currentBinding());
     }
 
     @JRubyMethod(name = {"block_given?", "iterator?"}, module = true, visibility = PRIVATE, reads = BLOCK)
     public static RubyBoolean block_given_p(ThreadContext context, IRubyObject recv) {
         return context.runtime.newBoolean(context.getCurrentFrame().getBlock().isGiven());
     }
 
 
     @Deprecated
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         return sprintf(recv.getRuntime().getCurrentContext(), recv, args);
     }
 
     @JRubyMethod(name = {"sprintf", "format"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject sprintf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw context.runtime.newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         IRubyObject arg;
         if (context.runtime.is1_9() && args.length == 2 && args[1] instanceof RubyHash) {
             arg = args[1];
         } else {
             RubyArray newArgs = context.runtime.newArrayNoCopy(args);
             newArgs.shift(context);
             arg = newArgs;
         }
 
         return str.op_format(context, arg);
     }
 
     @JRubyMethod(name = {"raise", "fail"}, optional = 3, module = true, visibility = PRIVATE, omit = true)
     public static IRubyObject raise(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         Ruby runtime = context.runtime;
 
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
             case 2:
                 raise = new RaiseException(convertToException(runtime, args[0], args[1]));
                 break;
             default:
                 raise = new RaiseException(convertToException(runtime, args[0], args[1]), args[2]);
                 break;
         }
 
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
         RubyStackTraceElement[] elements = rEx.getBacktraceElements();
         RubyStackTraceElement firstElement = elements.length > 0 ? elements[0] : new RubyStackTraceElement("", "", "(empty)", 0, false);
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
         Ruby runtime = context.runtime;
 
         IRubyObject tmp = name.checkStringType();
         if (!tmp.isNil()) {
             return requireCommon(runtime, recv, tmp, block);
         }
 
         return requireCommon(runtime, recv,
                 name.respondsTo("to_path") ? name.callMethod(context, "to_path") : name, block);
     }
 
     private static IRubyObject requireCommon(Ruby runtime, IRubyObject recv, IRubyObject name, Block block) {
         if (runtime.getLoadService().require(name.convertToString().toString())) {
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
 
         return loadCommon(file, context.runtime, args, block);
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
         Ruby runtime = context.runtime;
         // string to eval
         RubyString src = args[0].convertToString();
 
         boolean bindingGiven = args.length > 1 && !args[1].isNil();
         Binding binding = bindingGiven ? evalBinding.convertToBinding(args[1]) : context.currentBinding();
 
         if (args.length > 2) {
             // file given, use it and force it into binding
             binding.setFile(args[2].convertToString().toString());
 
             if (args.length > 3) {
                 // line given, use it and force it into binding
                 // -1 because parser uses zero offsets and other code compensates
                 binding.setLine(((int) args[3].convertToInteger().getLongValue()) - 1);
             } else {
                 // filename given, but no line, start from the beginning.
                 binding.setLine(0);
             }
         } else if (bindingGiven) {
             // binding given, use binding's file and line-number
         } else {
             // no binding given, use (eval) and start from first line.
             binding.setFile("(eval)");
             binding.setLine(0);
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
         RubyContinuation continuation = new RubyContinuation(context.runtime);
         return continuation.enter(context, continuation, block);
     }
 
     @JRubyMethod(optional = 1, module = true, visibility = PRIVATE, omit = true)
     public static IRubyObject caller(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw context.runtime.newArgumentError("negative level (" + level + ')');
         }
 
         return context.createCallerBacktrace(context.runtime, level);
     }
 
     @JRubyMethod(name = "catch", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject rbCatch(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         Ruby runtime = context.runtime;
         RubySymbol sym = stringOrSymbol(tag);
         RubyContinuation rbContinuation = new RubyContinuation(runtime, sym);
         try {
             context.pushCatch(rbContinuation.getContinuation());
             return rbContinuation.enter(context, sym, block);
         } finally {
             context.popCatch();
         }
     }
 
     @JRubyMethod(name = "catch", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject rbCatch19(ThreadContext context, IRubyObject recv, Block block) {
         IRubyObject tag = new RubyObject(context.runtime.getObject());
         return rbCatch19Common(context, tag, block);
     }
 
     @JRubyMethod(name = "catch", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject rbCatch19(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         return rbCatch19Common(context, tag, block);
     }
 
     private static IRubyObject rbCatch19Common(ThreadContext context, IRubyObject tag, Block block) {
         RubyContinuation rbContinuation = new RubyContinuation(context.runtime, tag);
         try {
             context.pushCatch(rbContinuation.getContinuation());
             return rbContinuation.enter(context, tag, block);
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
         Ruby runtime = context.runtime;
 
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
     
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject warn(ThreadContext context, IRubyObject recv, IRubyObject message) {
         Ruby runtime = context.runtime;
         
         if (runtime.warningsEnabled()) {
             IRubyObject out = runtime.getGlobalVariables().get("$stderr");
             RuntimeHelpers.invoke(context, out, "write", message);
             RuntimeHelpers.invoke(context, out, "write", runtime.getGlobalVariables().getDefaultSeparator());
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject set_trace_func(ThreadContext context, IRubyObject recv, IRubyObject trace_func, Block block) {
         if (trace_func.isNil()) {
             context.runtime.setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw context.runtime.newTypeError("trace_func needs to be Proc.");
         } else {
             context.runtime.setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     @JRubyMethod(required = 1, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject trace_var(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyProc proc = null;
         String var = args[0].toString();
         // ignore if it's not a global var
         if (var.charAt(0) != '$') {
             return context.runtime.getNil();
         }
         if (args.length == 1) {
             proc = RubyProc.newProc(context.runtime, block, Block.Type.PROC);
         }
         if (args.length == 2) {
             proc = (RubyProc)TypeConverter.convertToType(args[1], context.runtime.getProc(), "to_proc", true);
         }
 
         context.runtime.getGlobalVariables().setTraceVar(var, proc);
 
         return context.runtime.getNil();
     }
 
     @JRubyMethod(required = 1, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject untrace_var(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) {
             throw context.runtime.newArgumentError(0, 1);
         }
         String var = args[0].toString();
 
         // ignore if it's not a global var
         if (var.charAt(0) != '$') {
             return context.runtime.getNil();
         }
         
         if (args.length > 1) {
             ArrayList<IRubyObject> success = new ArrayList<IRubyObject>();
             for (int i = 1; i < args.length; i++) {
                 if (context.runtime.getGlobalVariables().untraceVar(var, args[i])) {
                     success.add(args[i]);
                 }
