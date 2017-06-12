diff --git a/spec/regression/JRUBY-6885_public_send_calls_method_missing_for_privates.rb b/spec/regression/JRUBY-6885_public_send_calls_method_missing_for_privates.rb
new file mode 100644
index 0000000000..48866d0e5c
--- /dev/null
+++ b/spec/regression/JRUBY-6885_public_send_calls_method_missing_for_privates.rb
@@ -0,0 +1,20 @@
+require 'rspec'
+
+if RUBY_VERSION =~ /1\.9/
+  describe 'Kernel#public_send' do
+    it 'invokes method missing when the name in question is defined but not public' do
+      obj = Class.new do
+	def method_missing(name, *)
+	  name
+	end
+	def foo; end
+	private :foo
+	def bar; end
+	protected :bar
+      end.new
+
+      obj.public_send(:foo).should == :foo
+      obj.public_send(:bar).should == :bar
+    end
+  end
+end
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index dc354fd39c..a3f36d45cd 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,2118 +1,2139 @@
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
+import org.jruby.internal.runtime.methods.DynamicMethod;
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
 import java.util.Map;
 
 import static org.jruby.CompatVersion.RUBY1_8;
 import static org.jruby.CompatVersion.RUBY1_9;
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.anno.FrameField.BACKREF;
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
 
     @JRubyMethod(name = {"block_given?", "iterator?"}, module = true, visibility = PRIVATE)
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
             }
             return RubyArray.newArray(context.runtime, success);
         } else {
             context.runtime.getGlobalVariables().untraceVar(var);
         }
 
         return context.runtime.getNil();
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject singleton_method_added(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject singleton_method_removed(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject singleton_method_undefined(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(required = 1, optional = 1, compat = RUBY1_9)
     public static IRubyObject define_singleton_method(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) {
             throw context.runtime.newArgumentError(0, 1);
         }
 
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
         return context.runtime.newProc(Block.Type.LAMBDA, block);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyProc lambda(ThreadContext context, IRubyObject recv, Block block) {
         return context.runtime.newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = "proc", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyProc proc_1_9(ThreadContext context, IRubyObject recv, Block block) {
         return context.runtime.newProc(Block.Type.PROC, block);
     }
 
     @JRubyMethod(name = "loop", module = true, visibility = PRIVATE)
     public static IRubyObject loop(ThreadContext context, IRubyObject recv, Block block) {
         if (context.runtime.is1_9() && !block.isGiven()) {
             return RubyEnumerator.enumeratorize(context.runtime, recv, "loop");
         }
         IRubyObject nil = context.runtime.getNil();
         RubyClass stopIteration = context.runtime.getStopIteration();
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
         if (args.length == 0) {
             throw context.runtime.newArgumentError("wrong number of arguments");
         }
 
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
             throw context.runtime.newArgumentError("unknown command ?" + (char) cmd);
         }
 
         // MRI behavior: now check arg count
 
         switch(cmd) {
         case '-': case '=': case '<': case '>':
             if (args.length != 3) {
                 throw context.runtime.newArgumentError(args.length, 3);
             }
             break;
         default:
             if (args.length != 2) {
                 throw context.runtime.newArgumentError(args.length, 2);
             }
             break;
         }
         
         switch (cmd) {
         case 'A': // ?A  | Time    | Last access time for file1
             return context.runtime.newFileStat(args[1].convertToString().toString(), false).atime();
         case 'b': // ?b  | boolean | True if file1 is a block device
             return RubyFileTest.blockdev_p(recv, args[1]);
         case 'c': // ?c  | boolean | True if file1 is a character device
             return RubyFileTest.chardev_p(recv, args[1]);
         case 'C': // ?C  | Time    | Last change time for file1
             return context.runtime.newFileStat(args[1].convertToString().toString(), false).ctime();
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
             return context.runtime.newFileStat(args[1].convertToString().toString(), false).mtime();
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
             return context.runtime.newFileStat(args[1].convertToString().toString(), false).mtimeEquals(args[2]);
         case '<': // ?<  | boolean | True if the modification time of file1 is prior to that of file2
             return context.runtime.newFileStat(args[1].convertToString().toString(), false).mtimeLessThan(args[2]);
         case '>': // ?>  | boolean | True if the modification time of file1 is after that of file2
             return context.runtime.newFileStat(args[1].convertToString().toString(), false).mtimeGreaterThan(args[2]);
         case '-': // ?-  | boolean | True if file1 and file2 are identical
             return RubyFileTest.identical_p(recv, args[1], args[2]);
         default:
             throw new InternalError("unreachable code reached!");
         }
     }
 
     @JRubyMethod(name = "`", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject backquote(ThreadContext context, IRubyObject recv, IRubyObject aString) {
         Ruby runtime = context.runtime;
         RubyString string = aString.convertToString();
         IRubyObject[] args = new IRubyObject[] {string};
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         long[] tuple;
 
         try {
             // NOTE: not searching executable path before invoking args
             tuple = ShellLauncher.runAndWaitPid(runtime, args, output, false);
         } catch (Exception e) {
             tuple = new long[] {127, -1};
         }
 
         context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, tuple[0], tuple[1]));
 
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
         ByteList buf = runtime.is1_9() ? new ByteList(out, 0, length, runtime.getDefaultExternalEncoding(), false) :
                 new ByteList(out, 0, length, false);
         RubyString newString = RubyString.newString(runtime, buf);
         
         return newString;
     }
 
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE)
     public static IRubyObject srand(ThreadContext context, IRubyObject recv) {
         return RubyRandom.srandCommon(context, recv);
     }
 
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE)
     public static IRubyObject srand(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RubyRandom.srandCommon(context, recv, arg);
     }
 
     @JRubyMethod(name = "rand", module = true, optional = 1, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject rand18(ThreadContext context, IRubyObject recv, IRubyObject[] arg) {
         return RubyRandom.randCommon18(context, recv, arg);
     }
 
     @JRubyMethod(name = "rand", module = true, optional = 1, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject rand19(ThreadContext context, IRubyObject recv, IRubyObject[] arg) {
         return RubyRandom.randCommon19(context, recv, arg);
     }
 
     /**
      * Now implemented in Ruby code. See Process::spawn in src/jruby/kernel19/process.rb
      * 
      * @deprecated 
      */
     public static RubyFixnum spawn(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         long pid = ShellLauncher.runExternalWithoutWait(runtime, args);
         return RubyFixnum.newFixnum(runtime, pid);
     }
 
     @JRubyMethod(name = "syscall", required = 1, optional = 9, module = true, visibility = PRIVATE)
     public static IRubyObject syscall(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         throw context.runtime.newNotImplementedError("Kernel#syscall is not implemented in JRuby");
     }
 
     @JRubyMethod(name = "system", required = 1, rest = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyBoolean system(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         return systemCommon(context, recv, args) == 0 ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "system", required = 1, rest = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject system19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         if (args[0] instanceof RubyHash) {
             RubyHash env = (RubyHash) args[0].convertToHash();
             if (env != null) {
                 runtime.getENV().merge_bang(context, env, Block.NULL_BLOCK);
             }
             // drop the first element for calling systemCommon()
             IRubyObject[] rest = new IRubyObject[args.length - 1];
             System.arraycopy(args, 1, rest, 0, args.length - 1);
             args = rest;
         }
         int resultCode = systemCommon(context, recv, args);
         switch (resultCode) {
             case 0: return runtime.getTrue();
             case 127: return runtime.getNil();
             default: return runtime.getFalse();
         }
     }
 
     private static int systemCommon(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         long[] tuple;
 
         try {
             if (! Platform.IS_WINDOWS && args[args.length -1].asJavaString().matches(".*[^&]&\\s*")) {
                 // looks like we need to send process to the background
                 ShellLauncher.runWithoutWait(runtime, args);
                 return 0;
             }
             tuple = ShellLauncher.runAndWaitPid(runtime, args);
         } catch (Exception e) {
             tuple = new long[] {127, -1};
         }
 
         context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, tuple[0], tuple[1]));
         return (int)tuple[0];
     }
     
     @JRubyMethod(name = {"exec"}, required = 1, rest = true, module = true, compat = RUBY1_8, visibility = PRIVATE)
     public static IRubyObject exec(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         
         return execCommon(runtime, null, args[0], null, args);
     }
     
     @JRubyMethod(required = 4, module = true, compat = RUBY1_9, visibility = PRIVATE)
     public static IRubyObject _exec_internal(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         
         IRubyObject env = args[0];
         IRubyObject prog = args[1];
         IRubyObject options = args[2];
         RubyArray cmdArgs = (RubyArray)args[3];
 
         RubyIO.checkExecOptions(options);
 
         return execCommon(runtime, env, prog, options, cmdArgs.toJavaArray());
     }
     
     private static IRubyObject execCommon(Ruby runtime, IRubyObject env, IRubyObject prog, IRubyObject options, IRubyObject[] args) {
         // This is a fairly specific hack for empty string, but it does the job
         if (args.length == 1 && args[0].convertToString().isEmpty()) {
             throw runtime.newErrnoENOENTError(args[0].convertToString().toString());
         }
 
         ThreadContext context = runtime.getCurrentContext();
         if (env != null && !env.isNil()) {
             RubyHash envMap = env.convertToHash();
             if (envMap != null) {
                 runtime.getENV().merge_bang(context, envMap, Block.NULL_BLOCK);
             }
         }
         
         boolean nativeFailed = false;
         boolean nativeExec = Options.NATIVE_EXEC.load();
 
         if (nativeExec) {
             try {
                 ShellLauncher.LaunchConfig cfg = new ShellLauncher.LaunchConfig(runtime, args, true);
 
                 // Duplicated in part from ShellLauncher.runExternalAndWait
                 if (cfg.shouldRunInShell()) {
                     // execute command with sh -c
                     // this does shell expansion of wildcards
                     cfg.verifyExecutableForShell();
                 } else {
                     cfg.verifyExecutableForDirect();
                 }
                 String progStr = cfg.getExecArgs()[0];
 
                 String[] argv = cfg.getExecArgs();
 
                 if (Platform.IS_WINDOWS) {
                     // Windows exec logic is much more elaborate; exec() in jnr-posix attempts to duplicate it
                     runtime.getPosix().exec(progStr, argv);
                 } else {
                     // TODO: other logic surrounding this call? In jnr-posix?
                     ArrayList envStrings = new ArrayList();
                     for (Map.Entry<String, String> envEntry : ((Map<String, String>)runtime.getENV()).entrySet()) {
                         envStrings.add(envEntry.getKey() + "=" + envEntry.getValue());
                     }
                     envStrings.add(null);
 
                     runtime.getPosix().execve(progStr, argv, (String[]) envStrings.toArray(new String[0]));
                 }
 
                 // Only here because native exec could not exec (always -1)
                 nativeFailed = true;
             } catch (RaiseException e) {
             } catch (Exception e) {
                 throw runtime.newErrnoENOENTError("cannot execute: " + e.getLocalizedMessage());
             }
         }
 
         // if we get here, either native exec failed or we should try an in-process exec
         if (nativeFailed) {
             throw runtime.newErrnoFromLastPOSIXErrno();
         }
         
         // Fall back onto our existing code if native not available
         // FIXME: Make jnr-posix Pure-Java backend do this as well
         int resultCode = ShellLauncher.execAndWait(runtime, args);
 
         exit(runtime, new IRubyObject[] {runtime.newFixnum(resultCode)}, true);
 
         // not reached
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "fork", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject fork(ThreadContext context, IRubyObject recv, Block block) {
         Ruby runtime = context.runtime;
         throw runtime.newNotImplementedError("fork is not available on this platform");
     }
 
     @JRubyMethod(name = "fork", module = true, visibility = PRIVATE, compat = RUBY1_9, notImplemented = true)
     public static IRubyObject fork19(ThreadContext context, IRubyObject recv, Block block) {
         Ruby runtime = context.runtime;
         throw runtime.newNotImplementedError("fork is not available on this platform");
     }
 
     @JRubyMethod(module = true)
     public static IRubyObject tap(ThreadContext context, IRubyObject recv, Block block) {
         block.yield(context, recv);
         return recv;
     }
 
     @JRubyMethod(name = {"to_enum", "enum_for"}, rest = true, compat = RUBY1_9)
     public static IRubyObject to_enum(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
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
-    public static IRubyObject public_send(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
-        recv.getMetaClass().checkMethodBound(context, args, PUBLIC);
-        return ((RubyObject)recv).send19(context, args, Block.NULL_BLOCK);
+    public static IRubyObject public_send(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
+        if (args.length == 0) {
+            throw context.runtime.newArgumentError("no method name given");
+        }
+
+        String name = args[0].asJavaString();
+        int newArgsLength = args.length - 1;
+
+        IRubyObject[] newArgs;
+        if (newArgsLength == 0) {
+            newArgs = IRubyObject.NULL_ARRAY;
+        } else {
+            newArgs = new IRubyObject[newArgsLength];
+            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
+        }
+
+        DynamicMethod method = recv.getMetaClass().searchMethod(name);
+
+        if (method.isUndefined() || method.getVisibility() != PUBLIC) {
+            return RuntimeHelpers.callMethodMissing(context, recv, method.getVisibility(), name, CallType.NORMAL, newArgs, block);
+        }
+
+        return method.call(context, recv, recv.getMetaClass(), name, newArgs, block);
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
     public static IRubyObject initialize_copy(IRubyObject self, IRubyObject original) {
         return ((RubyBasicObject)self).initialize_copy(original);
     }
 
     @JRubyMethod(name = "respond_to?", compat = RUBY1_8)
     public static RubyBoolean respond_to_p(IRubyObject self, IRubyObject mname) {
         return ((RubyBasicObject)self).respond_to_p(mname);
     }
 
     @JRubyMethod(name = "respond_to?", compat = RUBY1_9)
     public static IRubyObject respond_to_p19(IRubyObject self, IRubyObject mname) {
         return ((RubyBasicObject)self).respond_to_p19(mname);
     }
 
     @JRubyMethod(name = "respond_to?", compat = RUBY1_8)
     public static RubyBoolean respond_to_p(IRubyObject self, IRubyObject mname, IRubyObject includePrivate) {
         return ((RubyBasicObject)self).respond_to_p(mname, includePrivate);
     }
 
     @JRubyMethod(name = "respond_to?", compat = RUBY1_9)
     public static IRubyObject respond_to_p19(IRubyObject self, IRubyObject mname, IRubyObject includePrivate) {
         return ((RubyBasicObject)self).respond_to_p19(mname, includePrivate);
     }
 
     @JRubyMethod(name = {"object_id", "__id__"}, compat = RUBY1_8)
     public static IRubyObject id(IRubyObject self) {
         return ((RubyBasicObject)self).id();
     }
 
     @JRubyMethod(name = "id", compat = RUBY1_8)
     public static IRubyObject id_deprecated(IRubyObject self) {
         return ((RubyBasicObject)self).id_deprecated();
     }
 
     @JRubyMethod(name = "hash")
     public static RubyFixnum hash(IRubyObject self) {
         return ((RubyBasicObject)self).hash();
     }
 
     @JRubyMethod(name = "class")
     public static RubyClass type(IRubyObject self) {
         return ((RubyBasicObject)self).type();
     }
 
     @JRubyMethod(name = "type", compat = RUBY1_8)
     public static RubyClass type_deprecated(IRubyObject self) {
         return ((RubyBasicObject)self).type_deprecated();
     }
 
     @JRubyMethod(name = "clone")
     public static IRubyObject rbClone(IRubyObject self) {
         return ((RubyBasicObject)self).rbClone();
     }
 
     @JRubyMethod
     public static IRubyObject dup(IRubyObject self) {
         return ((RubyBasicObject)self).dup();
     }
 
     @JRubyMethod(name = "display", optional = 1)
     public static IRubyObject display(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).display(context, args);
     }
 
     @JRubyMethod(name = "tainted?")
     public static RubyBoolean tainted_p(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).tainted_p(context);
     }
 
     @JRubyMethod(name = "taint")
     public static IRubyObject taint(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).taint(context);
     }
 
     @JRubyMethod(name = "untaint")
     public static IRubyObject untaint(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).untaint(context);
     }
 
     @JRubyMethod(name = "freeze")
     public static IRubyObject freeze(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).freeze(context);
     }
 
     @JRubyMethod(name = "frozen?")
     public static RubyBoolean frozen_p(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).frozen_p(context);
     }
 
     @JRubyMethod(name = "untrusted?", compat = RUBY1_9)
     public static RubyBoolean untrusted_p(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).untrusted_p(context);
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public static IRubyObject untrust(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).untrust(context);
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public static IRubyObject trust(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).trust(context);
     }
 
     @JRubyMethod(name = "inspect")
     public static IRubyObject inspect(IRubyObject self) {
         return ((RubyBasicObject)self).inspect();
     }
 
     @JRubyMethod(name = "instance_of?", required = 1)
     public static RubyBoolean instance_of_p(ThreadContext context, IRubyObject self, IRubyObject type) {
         return ((RubyBasicObject)self).instance_of_p(context, type);
     }
 
     @JRubyMethod(name = {"kind_of?", "is_a?"}, required = 1)
     public static RubyBoolean kind_of_p(ThreadContext context, IRubyObject self, IRubyObject type) {
         return ((RubyBasicObject)self).kind_of_p(context, type);
     }
 
     @JRubyMethod(name = "methods", optional = 1, compat = RUBY1_8)
     public static IRubyObject methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).methods(context, args);
     }
     @JRubyMethod(name = "methods", optional = 1, compat = RUBY1_9)
     public static IRubyObject methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).methods19(context, args);
     }
 
     @JRubyMethod(name = "public_methods", optional = 1, compat = RUBY1_8)
     public static IRubyObject public_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).public_methods(context, args);
     }
 
     @JRubyMethod(name = "public_methods", optional = 1, compat = RUBY1_9)
     public static IRubyObject public_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).public_methods19(context, args);
     }
 
     @JRubyMethod(name = "protected_methods", optional = 1, compat = RUBY1_8)
     public static IRubyObject protected_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).protected_methods(context, args);
     }
 
     @JRubyMethod(name = "protected_methods", optional = 1, compat = RUBY1_9)
     public static IRubyObject protected_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).protected_methods19(context, args);
     }
 
     @JRubyMethod(name = "private_methods", optional = 1, compat = RUBY1_8)
     public static IRubyObject private_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).private_methods(context, args);
     }
 
     @JRubyMethod(name = "private_methods", optional = 1, compat = RUBY1_9)
     public static IRubyObject private_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).private_methods19(context, args);
     }
 
     @JRubyMethod(name = "singleton_methods", optional = 1, compat = RUBY1_8)
     public static RubyArray singleton_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).singleton_methods(context, args);
     }
 
     @JRubyMethod(name = "singleton_methods", optional = 1 , compat = RUBY1_9)
     public static RubyArray singleton_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).singleton_methods19(context, args);
     }
 
     @JRubyMethod(name = "method", required = 1)
     public static IRubyObject method(IRubyObject self, IRubyObject symbol) {
         return ((RubyBasicObject)self).method(symbol);
     }
 
     @JRubyMethod(name = "method", required = 1, compat = RUBY1_9)
     public static IRubyObject method19(IRubyObject self, IRubyObject symbol) {
         return ((RubyBasicObject)self).method19(symbol);
     }
 
     @JRubyMethod(name = "to_s")
     public static IRubyObject to_s(IRubyObject self) {
         return ((RubyBasicObject)self).to_s();
     }
 
     @JRubyMethod(name = "to_a", visibility = PUBLIC, compat = RUBY1_8)
     public static RubyArray to_a(IRubyObject self) {
         return ((RubyBasicObject)self).to_a();
     }
 
     @JRubyMethod(compat = RUBY1_8)
     public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, Block block) {
         return ((RubyBasicObject)self).instance_eval(context, block);
     }
     @JRubyMethod(compat = RUBY1_8)
     public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         return ((RubyBasicObject)self).instance_eval(context, arg0, block);
     }
     @JRubyMethod(compat = RUBY1_8)
     public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         return ((RubyBasicObject)self).instance_eval(context, arg0, arg1, block);
     }
     @JRubyMethod(compat = RUBY1_8)
     public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return ((RubyBasicObject)self).instance_eval(context, arg0, arg1, arg2, block);
     }
 
     @JRubyMethod(optional = 3, rest = true, compat = RUBY1_8)
     public static IRubyObject instance_exec(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         return ((RubyBasicObject)self).instance_exec(context, args, block);
     }
 
     @JRubyMethod(name = "extend", required = 1, rest = true)
     public static IRubyObject extend(IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).extend(args);
     }
 
     @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
     public static IRubyObject send(ThreadContext context, IRubyObject self, Block block) {
         return ((RubyBasicObject)self).send(context, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
     public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         return ((RubyBasicObject)self).send(context, arg0, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
     public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         return ((RubyBasicObject)self).send(context, arg0, arg1, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
     public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return ((RubyBasicObject)self).send(context, arg0, arg1, arg2, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, rest = true, compat = RUBY1_8)
     public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         return ((RubyBasicObject)self).send(context, args, block);
     }
 
     @JRubyMethod(name = {"send"}, compat = RUBY1_9)
     public static IRubyObject send19(ThreadContext context, IRubyObject self, Block block) {
         return ((RubyBasicObject)self).send19(context, block);
     }
     @JRubyMethod(name = {"send"}, compat = RUBY1_9)
     public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         return ((RubyBasicObject)self).send19(context, arg0, block);
     }
     @JRubyMethod(name = {"send"}, compat = RUBY1_9)
     public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         return ((RubyBasicObject)self).send19(context, arg0, arg1, block);
     }
     @JRubyMethod(name = {"send"}, compat = RUBY1_9)
     public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return ((RubyBasicObject)self).send19(context, arg0, arg1, arg2, block);
     }
     @JRubyMethod(name = {"send"}, rest = true, compat = RUBY1_9)
     public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         return ((RubyBasicObject)self).send19(context, args, block);
     }
 
     @JRubyMethod(name = "nil?")
     public static IRubyObject nil_p(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).nil_p(context);
     }
 
     @JRubyMethod(name = "=~", required = 1, compat = RUBY1_8)
     public static IRubyObject op_match(ThreadContext context, IRubyObject self, IRubyObject arg) {
         return ((RubyBasicObject)self).op_match(context, arg);
     }
 
     @JRubyMethod(name = "=~", required = 1, compat = RUBY1_9)
     public static IRubyObject op_match19(ThreadContext context, IRubyObject self, IRubyObject arg) {
         return ((RubyBasicObject)self).op_match19(context, arg);
     }
 
     @JRubyMethod(name = "!~", required = 1, compat = RUBY1_9)
     public static IRubyObject op_not_match(ThreadContext context, IRubyObject self, IRubyObject arg) {
         return ((RubyBasicObject)self).op_not_match(context, arg);
     }
 
     @JRubyMethod(name = "instance_variable_defined?", required = 1)
     public static IRubyObject instance_variable_defined_p(ThreadContext context, IRubyObject self, IRubyObject name) {
         return ((RubyBasicObject)self).instance_variable_defined_p(context, name);
     }
 
     @JRubyMethod(name = "instance_variable_get", required = 1)
     public static IRubyObject instance_variable_get(ThreadContext context, IRubyObject self, IRubyObject name) {
         return ((RubyBasicObject)self).instance_variable_get(context, name);
     }
 
     @JRubyMethod(name = "instance_variable_set", required = 2)
     public static IRubyObject instance_variable_set(IRubyObject self, IRubyObject name, IRubyObject value) {
         return ((RubyBasicObject)self).instance_variable_set(name, value);
     }
 
     @JRubyMethod(visibility = PRIVATE)
     public static IRubyObject remove_instance_variable(ThreadContext context, IRubyObject self, IRubyObject name, Block block) {
         return ((RubyBasicObject)self).remove_instance_variable(context, name, block);
     }
 
     @JRubyMethod(name = "instance_variables")
     public static RubyArray instance_variables(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).instance_variables(context);
     }
 
     @JRubyMethod(name = "instance_variables", compat = RUBY1_9)
     public static RubyArray instance_variables19(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).instance_variables19(context);
     }
 }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index ad2154a206..643d7d160f 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -157,3722 +157,3710 @@ public class RubyModule extends RubyObject {
     
     public static class ModuleKernelMethods {
         @JRubyMethod
         public static IRubyObject autoload(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return RubyKernel.autoload(recv, arg0, arg1);
         }
         
         @JRubyMethod(name = "autoload?")
         public static IRubyObject autoload_p(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
             return RubyKernel.autoload_p(context, recv, arg0);
         }
     }
     
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.MODULE;
     }
 
     @Override
     public boolean isModule() {
         return true;
     }
 
     @Override
     public boolean isClass() {
         return false;
     }
 
     public boolean isSingleton() {
         return false;
     }
 
     public static class KindOf {
         public static final KindOf DEFAULT_KIND_OF = new KindOf();
         public boolean isKindOf(IRubyObject obj, RubyModule type) {
             return obj.getMetaClass().hasModuleInHierarchy(type);
         }
     }
     
     public boolean isInstance(IRubyObject object) {
         return kindOf.isKindOf(object, this);
     }
 
     public Map<String, ConstantEntry> getConstantMap() {
         return constants;
     }
 
     public synchronized Map<String, ConstantEntry> getConstantMapForWrite() {
         return constants == Collections.EMPTY_MAP ? constants = new ConcurrentHashMap<String, ConstantEntry>(4, 0.9f, 1) : constants;
     }
     
     /**
      * AutoloadMap must be accessed after checking ConstantMap. Checking UNDEF value in constantMap works as a guard.
      * For looking up constant, check constantMap first then try to get an Autoload object from autoloadMap.
      * For setting constant, update constantMap first and remove an Autoload object from autoloadMap.
      */
     private Map<String, Autoload> getAutoloadMap() {
         return autoloads;
     }
     
     private synchronized Map<String, Autoload> getAutoloadMapForWrite() {
         return autoloads == Collections.EMPTY_MAP ? autoloads = new ConcurrentHashMap<String, Autoload>(4, 0.9f, 1) : autoloads;
     }
     
     public void addIncludingHierarchy(IncludedModuleWrapper hierarchy) {
         synchronized (getRuntime().getHierarchyLock()) {
             Set<RubyClass> oldIncludingHierarchies = includingHierarchies;
             if (oldIncludingHierarchies == Collections.EMPTY_SET) includingHierarchies = oldIncludingHierarchies = new WeakHashSet(4);
             oldIncludingHierarchies.add(hierarchy);
         }
     }
 
     /** separate path for MetaClass construction
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass, boolean objectSpace) {
         super(runtime, metaClass, objectSpace);
         id = runtime.allocModuleId();
         runtime.addModule(this);
         // if (parent == null) parent = runtime.getObject();
         setFlag(USER7_F, !isClass());
         generationObject = generation = runtime.getNextModuleGeneration();
         if (runtime.getInstanceConfig().isProfiling()) {
             cacheEntryFactory = new ProfilingCacheEntryFactory(NormalCacheEntryFactory);
         } else {
             cacheEntryFactory = NormalCacheEntryFactory;
         }
         
         // set up an invalidator for use in new optimization strategies
         methodInvalidator = OptoFactory.newMethodInvalidator(this);
     }
     
     /** used by MODULE_ALLOCATOR and RubyClass constructors
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
     
     /** standard path for Module construction
      * 
      */
     protected RubyModule(Ruby runtime) {
         this(runtime, runtime.getModule());
     }
 
     public boolean needsImplementer() {
         return getFlag(USER7_F);
     }
     
     /** rb_module_new
      * 
      */
     public static RubyModule newModule(Ruby runtime) {
         return new RubyModule(runtime);
     }
     
     /** rb_module_new/rb_define_module_id/rb_name_class/rb_set_class_path
      * 
      */
     public static RubyModule newModule(Ruby runtime, String name, RubyModule parent, boolean setParent) {
         RubyModule module = newModule(runtime);
         module.setBaseName(name);
         if (setParent) module.setParent(parent);
         parent.setConstant(name, module);
         return module;
     }
     
     // synchronized method per JRUBY-1173 (unsafe Double-Checked Locking)
     // FIXME: synchronization is still wrong in CP code
     public synchronized void addClassProvider(ClassProvider provider) {
         if (!classProviders.contains(provider)) {
             Set<ClassProvider> cp = new HashSet<ClassProvider>(classProviders);
             cp.add(provider);
             classProviders = cp;
         }
     }
 
     public synchronized void removeClassProvider(ClassProvider provider) {
         Set<ClassProvider> cp = new HashSet<ClassProvider>(classProviders);
         cp.remove(provider);
         classProviders = cp;
     }
 
     private void checkForCyclicInclude(RubyModule m) throws RaiseException {
         if (getNonIncludedClass() == m.getNonIncludedClass()) {
             throw getRuntime().newArgumentError("cyclic include detected");
         }
     }
 
     private RubyClass searchProvidersForClass(String name, RubyClass superClazz) {
         RubyClass clazz;
         for (ClassProvider classProvider: classProviders) {
             if ((clazz = classProvider.defineClassUnder(this, name, superClazz)) != null) {
                 return clazz;
             }
         }
         return null;
     }
 
     private RubyModule searchProvidersForModule(String name) {
         RubyModule module;
         for (ClassProvider classProvider: classProviders) {
             if ((module = classProvider.defineModuleUnder(this, name)) != null) {
                 return module;
             }
         }
         return null;
     }
 
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     public void setSuperClass(RubyClass superClass) {
         // update superclass reference
         this.superClass = superClass;
         if (superClass != null && superClass.isSynchronized()) becomeSynchronized();
     }
 
     public RubyModule getParent() {
         return parent;
     }
 
     public void setParent(RubyModule parent) {
         this.parent = parent;
     }
     
     public Map<String, DynamicMethod> getMethods() {
         return this.methods;
     }
 
     public synchronized Map<String, DynamicMethod> getMethodsForWrite() {
         Map<String, DynamicMethod> myMethods = this.methods;
         return myMethods == Collections.EMPTY_MAP ?
             this.methods = new ConcurrentHashMap<String, DynamicMethod>(0, 0.9f, 1) :
             myMethods;
     }
     
     // note that addMethod now does its own put, so any change made to
     // functionality here should be made there as well 
     private void putMethod(String name, DynamicMethod method) {
         getMethodsForWrite().put(name, method);
 
         getRuntime().addProfiledMethod(name, method);
     }
 
     /**
      * Is this module one that in an included one (e.g. an IncludedModuleWrapper). 
      */
     public boolean isIncluded() {
         return false;
     }
 
     public RubyModule getNonIncludedClass() {
         return this;
     }
 
     /**
      * Get the base name of this class, or null if it is an anonymous class.
      * 
      * @return base name of the class
      */
     public String getBaseName() {
         return baseName;
     }
 
     /**
      * Set the base name of the class. If null, the class effectively becomes
      * anonymous (though constants elsewhere may reference it).
      * @param name the new base name of the class
      */
     public void setBaseName(String name) {
         baseName = name;
     }
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (cachedName != null) return cachedName;
         return calculateName();
     }
     
     /**
      * Get the "simple" name for the class, which is either the "base" name or
      * the "anonymous" class name.
      * 
      * @return the "simple" name of the class
      */
     public String getSimpleName() {
         if (baseName != null) return baseName;
         return calculateAnonymousName();
     }
 
     /**
      * Recalculate the fully-qualified name of this class/module.
      */
     private String calculateName() {
         boolean cache = true;
 
         if (getBaseName() == null) {
             // we are anonymous, use anonymous name
             return calculateAnonymousName();
         }
         
         Ruby runtime = getRuntime();
         
         String name = getBaseName();
         RubyClass objectClass = runtime.getObject();
         
         // First, we count the parents
         int parentCount = 0;
         for (RubyModule p = getParent() ; p != null && p != objectClass ; p = p.getParent()) {
             parentCount++;
         }
         
         // Allocate a String array for all of their names and populate it
         String[] parentNames = new String[parentCount];
         int i = parentCount - 1;
         int totalLength = name.length() + parentCount * 2; // name length + enough :: for all parents
         for (RubyModule p = getParent() ; p != null && p != objectClass ; p = p.getParent(), i--) {
             String pName = p.getBaseName();
             
             // This is needed when the enclosing class or module is a singleton.
             // In that case, we generated a name such as null::Foo, which broke 
             // Marshalling, among others. The correct thing to do in this situation 
             // is to insert the generate the name of form #<Class:01xasdfasd> if 
             // it's a singleton module/class, which this code accomplishes.
             if(pName == null) {
                 cache = false;
                 pName = p.getName();
              }
             
             parentNames[i] = pName;
             totalLength += pName.length();
         }
         
         // Then build from the front using a StringBuilder
         StringBuilder builder = new StringBuilder(totalLength);
         for (String parentName : parentNames) {
             builder.append(parentName).append("::");
         }
         builder.append(name);
         
         String fullName = builder.toString();
 
         if (cache) cachedName = fullName;
 
         return fullName;
     }
 
     private String calculateAnonymousName() {
         if (anonymousName == null) {
             // anonymous classes get the #<Class:0xdeadbeef> format
             StringBuilder anonBase = new StringBuilder(isClass() ? "#<Class:0x" : "#<Module:0x");
             anonBase.append(Integer.toHexString(System.identityHashCode(this))).append('>');
             anonymousName = anonBase.toString();
         }
         return anonymousName;
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     @Deprecated
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
 
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
 
         return includedModule;
     }
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module;
         if ((module = getConstantAt(name)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     @Deprecated
     public RubyClass fastGetClass(String internedName) {
         return getClass(internedName);
     }
 
     /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
 
         testFrozen("module");
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
 
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         checkForCyclicInclude(module);
 
         infectBy(module);
 
         doIncludeModule(module);
         invalidateCoreClasses();
         invalidateCacheDescendants();
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 PRIVATE : PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
     
     public void defineAnnotatedMethod(Class clazz, String name) {
         // FIXME: This is probably not very efficient, since it loads all methods for each call
         boolean foundMethod = false;
         for (Method method : clazz.getDeclaredMethods()) {
             if (method.getName().equals(name) && defineAnnotatedMethod(method, MethodFactory.createFactory(getRuntime().getJRubyClassLoader()))) {
                 foundMethod = true;
             }
         }
 
         if (!foundMethod) {
             throw new RuntimeException("No JRubyMethod present for method " + name + "on class " + clazz.getName());
         }
     }
     
     public void defineAnnotatedConstants(Class clazz) {
         Field[] declaredFields = clazz.getDeclaredFields();
         for (Field field : declaredFields) {
             if (Modifier.isStatic(field.getModifiers())) {
                 defineAnnotatedConstant(field);
             }
         }
     }
 
     public boolean defineAnnotatedConstant(Field field) {
         JRubyConstant jrubyConstant = field.getAnnotation(JRubyConstant.class);
 
         if (jrubyConstant == null) return false;
 
         String[] names = jrubyConstant.value();
         if(names.length == 0) {
             names = new String[]{field.getName()};
         }
 
         Class tp = field.getType();
         IRubyObject realVal;
 
         try {
             if(tp == Integer.class || tp == Integer.TYPE || tp == Short.class || tp == Short.TYPE || tp == Byte.class || tp == Byte.TYPE) {
                 realVal = RubyNumeric.int2fix(getRuntime(), field.getInt(null));
             } else if(tp == Boolean.class || tp == Boolean.TYPE) {
                 realVal = field.getBoolean(null) ? getRuntime().getTrue() : getRuntime().getFalse();
             } else {
                 realVal = getRuntime().getNil();
             }
         } catch(Exception e) {
             realVal = getRuntime().getNil();
         }
 
         
         for(String name : names) {
             this.setConstant(name, realVal);
         }
 
         return true;
     }
 
     public void defineAnnotatedMethods(Class clazz) {
         defineAnnotatedMethodsIndividually(clazz);
     }
     
     public static class MethodClumper {
         Map<String, List<JavaMethodDescriptor>> annotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_8 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_8 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_9 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_9 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods2_0 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods2_0 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> allAnnotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         
         public void clump(Class cls) {
             Method[] declaredMethods = cls.getDeclaredMethods();
             for (Method method: declaredMethods) {
                 JRubyMethod anno = method.getAnnotation(JRubyMethod.class);
                 if (anno == null) continue;
                 
                 JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
                 
                 String name = anno.name().length == 0 ? method.getName() : anno.name()[0];
                 
                 List<JavaMethodDescriptor> methodDescs;
                 Map<String, List<JavaMethodDescriptor>> methodsHash = null;
                 if (desc.isStatic) {
                     if (anno.compat() == RUBY1_8) {
                         methodsHash = staticAnnotatedMethods1_8;
                     } else if (anno.compat() == RUBY1_9) {
                         methodsHash = staticAnnotatedMethods1_9;
                     } else if (anno.compat() == RUBY2_0) {
                         methodsHash = staticAnnotatedMethods2_0;
                     } else {
                         methodsHash = staticAnnotatedMethods;
                     }
                 } else {
                     if (anno.compat() == RUBY1_8) {
                         methodsHash = annotatedMethods1_8;
                     } else if (anno.compat() == RUBY1_9) {
                         methodsHash = annotatedMethods1_9;
                     } else if (anno.compat() == RUBY2_0) {
                         methodsHash = annotatedMethods2_0;
                     } else {
                         methodsHash = annotatedMethods;
                     }
                 }
 
                 // add to specific
                 methodDescs = methodsHash.get(name);
                 if (methodDescs == null) {
                     methodDescs = new ArrayList<JavaMethodDescriptor>();
                     methodsHash.put(name, methodDescs);
                 }
                 
                 methodDescs.add(desc);
 
                 // add to general
                 methodDescs = allAnnotatedMethods.get(name);
                 if (methodDescs == null) {
                     methodDescs = new ArrayList<JavaMethodDescriptor>();
                     allAnnotatedMethods.put(name, methodDescs);
                 }
 
                 methodDescs.add(desc);
             }
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAllAnnotatedMethods() {
             return allAnnotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods() {
             return annotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_8() {
             return annotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_9() {
             return annotatedMethods1_9;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods2_0() {
             return annotatedMethods2_0;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods() {
             return staticAnnotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_8() {
             return staticAnnotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_9() {
             return staticAnnotatedMethods1_9;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods2_0() {
             return staticAnnotatedMethods2_0;
         }
     }
     
     public void defineAnnotatedMethodsIndividually(Class clazz) {
         TypePopulator populator;
         
         if (RubyInstanceConfig.FULL_TRACE_ENABLED || RubyInstanceConfig.REFLECTED_HANDLES) {
             // we want reflected invokers or need full traces, use default (slow) populator
             if (DEBUG) LOG.debug("trace mode, using default populator");
             populator = TypePopulator.DEFAULT;
         } else {
             try {
                 String qualifiedName = "org.jruby.gen." + clazz.getCanonicalName().replace('.', '$');
 
                 if (DEBUG) LOG.debug("looking for " + qualifiedName + AnnotationBinder.POPULATOR_SUFFIX);
 
                 Class populatorClass = Class.forName(qualifiedName + AnnotationBinder.POPULATOR_SUFFIX);
                 populator = (TypePopulator)populatorClass.newInstance();
             } catch (Throwable t) {
                 if (DEBUG) LOG.debug("Could not find it, using default populator");
                 populator = TypePopulator.DEFAULT;
             }
         }
         
         populator.populate(this, clazz);
     }
     
     public boolean defineAnnotatedMethod(String name, List<JavaMethodDescriptor> methods, MethodFactory methodFactory) {
         JavaMethodDescriptor desc = methods.get(0);
         if (methods.size() == 1) {
             return defineAnnotatedMethod(desc, methodFactory);
         } else {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, methods);
             define(this, desc, dynamicMethod);
             
             return true;
         }
     }
     
     public boolean defineAnnotatedMethod(Method method, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
     
     public boolean defineAnnotatedMethod(JavaMethodDescriptor desc, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = desc.anno;
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 PRIVATE : PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method, Visibility visibility) {
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, PRIVATE));
     }
 
     public void defineFastProtectedMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, PROTECTED));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(ThreadContext context, String name) {
         Ruby runtime = context.runtime;
 
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.UNDEFINING_BAD, "undefining `"+ name +"' may cause serious problem");
         }
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             String s0 = " class";
             RubyModule c = this;
 
             if (c.isSingleton()) {
                 IRubyObject obj = ((MetaClass)c).getAttached();
 
                 if (obj != null && obj instanceof RubyModule) {
                     c = (RubyModule) obj;
                     s0 = "";
                 }
             } else if (c.isModule()) {
                 s0 = " module";
             }
 
             throw runtime.newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_undefined", runtime.newSymbol(name));
         } else {
             callMethod(context, "method_undefined", runtime.newSymbol(name));
         }
     }
 
     @JRubyMethod(name = "include?", required = 1)
     public IRubyObject include_p(ThreadContext context, IRubyObject arg) {
         if (!arg.isModule()) {
             throw context.runtime.newTypeError(arg, context.runtime.getModule());
         }
         RubyModule moduleToCompare = (RubyModule) arg;
 
         // See if module is in chain...Cannot match against itself so start at superClass.
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isSame(moduleToCompare)) {
                 return context.runtime.getTrue();
             }
         }
 
         return context.runtime.getFalse();
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         Ruby runtime = getRuntime();
 
         testFrozen("class/module");
 
         addMethodInternal(name, method);
     }
 
     public void addMethodInternal(String name, DynamicMethod method) {
         synchronized(getMethodsForWrite()) {
             addMethodAtBootTimeOnly(name, method);
             invalidateCoreClasses();
             invalidateCacheDescendants();
         }
     }
 
     /**
      * This method is not intended for use by normal users; it is a fast-path
      * method that skips synchronization and hierarchy invalidation to speed
      * boot-time method definition.
      *
      * @param name The name to which to bind the method
      * @param method The method to bind
      */
     public void addMethodAtBootTimeOnly(String name, DynamicMethod method) {
         getMethodsForWrite().put(name, method);
 
         getRuntime().addProfiledMethod(name, method);
     }
 
     public void removeMethod(ThreadContext context, String name) {
         Ruby runtime = context.runtime;
 
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethodsForWrite()) {
             DynamicMethod method = (DynamicMethod) getMethodsForWrite().remove(name);
             if (method == null) {
                 throw runtime.newNameError("method '" + name + "' not defined in " + getName(), name);
             }
 
             invalidateCoreClasses();
             invalidateCacheDescendants();
         }
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_removed", runtime.newSymbol(name));
         } else {
             callMethod(context, "method_removed", runtime.newSymbol(name));
         }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */    
     public DynamicMethod searchMethod(String name) {
         return searchWithCache(name).method;
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */    
     public CacheEntry searchWithCache(String name) {
         CacheEntry entry = cacheHit(name);
 
         if (entry != null) return entry;
 
         // we grab serial number first; the worst that will happen is we cache a later
         // update with an earlier serial number, which would just flush anyway
         int token = getGeneration();
         DynamicMethod method = searchMethodInner(name);
 
         if (method instanceof CacheableMethod) {
             method = ((CacheableMethod) method).getMethodForCaching();
         }
 
         return method != null ? addToCache(name, method, token) : addToCache(name, UndefinedMethod.getInstance(), token);
     }
     
     @Deprecated
     public final int getCacheToken() {
         return generation;
     }
     
     public final int getGeneration() {
         return generation;
     }
 
     public final Integer getGenerationObject() {
         return generationObject;
     }
 
     private final Map<String, CacheEntry> getCachedMethods() {
         return this.cachedMethods;
     }
 
     private final Map<String, CacheEntry> getCachedMethodsForWrite() {
         Map<String, CacheEntry> myCachedMethods = this.cachedMethods;
         return myCachedMethods == Collections.EMPTY_MAP ?
             this.cachedMethods = new ConcurrentHashMap<String, CacheEntry>(0, 0.75f, 1) :
             myCachedMethods;
     }
     
     private CacheEntry cacheHit(String name) {
         CacheEntry cacheEntry = getCachedMethods().get(name);
 
         if (cacheEntry != null) {
             if (cacheEntry.token == getGeneration()) {
                 return cacheEntry;
             }
         }
         
         return null;
     }
     
     protected static abstract class CacheEntryFactory {
         public abstract CacheEntry newCacheEntry(DynamicMethod method, int token);
 
         /**
          * Test all WrapperCacheEntryFactory instances in the chain for assignability
          * from the given class.
          *
          * @param cacheEntryFactoryClass the class from which to test assignability
          * @return whether the given class is assignable from any factory in the chain
          */
         public boolean hasCacheEntryFactory(Class cacheEntryFactoryClass) {
             CacheEntryFactory current = this;
             while (current instanceof WrapperCacheEntryFactory) {
                 if (cacheEntryFactoryClass.isAssignableFrom(current.getClass())) {
                     return true;
                 }
                 current = ((WrapperCacheEntryFactory)current).getPrevious();
             }
             if (cacheEntryFactoryClass.isAssignableFrom(current.getClass())) {
                 return true;
             }
             return false;
         }
     }
 
     /**
      * A wrapper CacheEntryFactory, for delegating cache entry creation along a chain.
      */
     protected static abstract class WrapperCacheEntryFactory extends CacheEntryFactory {
         /** The CacheEntryFactory being wrapped. */
         protected final CacheEntryFactory previous;
 
         /**
          * Construct a new WrapperCacheEntryFactory using the given CacheEntryFactory as
          * the "previous" wrapped factory.
          *
          * @param previous the wrapped factory
          */
         public WrapperCacheEntryFactory(CacheEntryFactory previous) {
             this.previous = previous;
         }
 
         public CacheEntryFactory getPrevious() {
             return previous;
         }
     }
 
     protected static final CacheEntryFactory NormalCacheEntryFactory = new CacheEntryFactory() {
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             return new CacheEntry(method, token);
         }
     };
 
     protected static class SynchronizedCacheEntryFactory extends WrapperCacheEntryFactory {
         public SynchronizedCacheEntryFactory(CacheEntryFactory previous) {
             super(previous);
         }
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             if (method.isUndefined()) {
                 return new CacheEntry(method, token);
             }
             // delegate up the chain
             CacheEntry delegated = previous.newCacheEntry(method, token);
             return new CacheEntry(new SynchronizedDynamicMethod(delegated.method), delegated.token);
         }
     }
 
     protected static class ProfilingCacheEntryFactory extends WrapperCacheEntryFactory {
         public ProfilingCacheEntryFactory(CacheEntryFactory previous) {
             super(previous);
         }
         @Override
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             if (method.isUndefined()) {
                 return new CacheEntry(method, token);
             }
             CacheEntry delegated = previous.newCacheEntry(method, token);
             return new CacheEntry(new ProfilingDynamicMethod(delegated.method), delegated.token);
         }
     }
 
     private volatile CacheEntryFactory cacheEntryFactory;
 
     // modifies this class only; used to make the Synchronized module synchronized
     public void becomeSynchronized() {
         cacheEntryFactory = new SynchronizedCacheEntryFactory(cacheEntryFactory);
     }
 
     public boolean isSynchronized() {
         return cacheEntryFactory.hasCacheEntryFactory(SynchronizedCacheEntryFactory.class);
     }
 
     private CacheEntry addToCache(String name, DynamicMethod method, int token) {
         CacheEntry entry = cacheEntryFactory.newCacheEntry(method, token);
         getCachedMethodsForWrite().put(name, entry);
 
         return entry;
     }
     
     public DynamicMethod searchMethodInner(String name) {
         DynamicMethod method = getMethods().get(name);
         
         if (method != null) return method;
         
         return superClass == null ? null : superClass.searchMethodInner(name);
     }
 
     public void invalidateCacheDescendants() {
         if (DEBUG) LOG.debug("invalidating descendants: {}", baseName);
 
         if (includingHierarchies.isEmpty()) {
             // it's only us; just invalidate directly
             methodInvalidator.invalidate();
             return;
         }
 
         List<Invalidator> invalidators = new ArrayList();
         invalidators.add(methodInvalidator);
         
         synchronized (getRuntime().getHierarchyLock()) {
             for (RubyClass includingHierarchy : includingHierarchies) {
                 includingHierarchy.addInvalidatorsAndFlush(invalidators);
             }
         }
         
         methodInvalidator.invalidateAll(invalidators);
     }
     
     protected void invalidateCoreClasses() {
         if (!getRuntime().isBooting()) {
             if (this == getRuntime().getFixnum()) {
                 getRuntime().setFixnumReopened(true);
             } else if (this == getRuntime().getFloat()) {
                 getRuntime().setFloatReopened(true);
             }
         }
     }
     
     public Invalidator getInvalidator() {
         return methodInvalidator;
     }
     
     public void updateGeneration() {
         generationObject = generation = getRuntime().getNextModuleGeneration();
     }
 
     @Deprecated
     protected void invalidateCacheDescendantsInner() {
         methodInvalidator.invalidate();
     }
     
     protected void invalidateConstantCache() {
         getRuntime().getConstantInvalidator().invalidate();
     }    
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod retrieveMethod(String name) {
         return getMethods().get(name);
     }
 
     /**
-     * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
+     * Find the given class in this hierarchy, considering modules along the way.
      * 
-     * @param name The name of the method to search for
+     * @param clazz the class to find
      * @return The method, or UndefinedMethod if not found
      */
     public RubyModule findImplementer(RubyModule clazz) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.isSame(clazz)) return module;
         }
 
         return null;
     }
 
     public void addModuleFunction(String name, DynamicMethod method) {
         addMethod(name, method);
         getSingletonClass().addMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineModuleFunction(String name, Callback method) {
         definePrivateMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void definePublicModuleFunction(String name, Callback method) {
         defineMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastModuleFunction(String name, Callback method) {
         defineFastPrivateMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastPublicModuleFunction(String name, Callback method) {
         defineFastMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_alias
      *
      */
     public synchronized void defineAlias(String name, String oldName) {
         testFrozen("module");
         if (oldName.equals(name)) {
             return;
         }
         Ruby runtime = getRuntime();
 
         // JRUBY-2435: Aliasing eval and other "special" methods should display a warning
         // We warn because we treat certain method names as "special" for purposes of
         // optimization. Hopefully this will be enough to convince people not to alias
         // them.
         if (SCOPE_CAPTURING_METHODS.contains(oldName)) {
             runtime.getWarnings().warn("`" + oldName + "' should not be aliased");
         }
 
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = runtime.getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw runtime.newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
 
         putMethod(name, new AliasMethod(this, method, oldName));
 
         invalidateCoreClasses();
         invalidateCacheDescendants();
     }
 
     public synchronized void defineAliases(List<String> aliases, String oldName) {
         testFrozen("module");
         Ruby runtime = getRuntime();
 
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = runtime.getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw runtime.newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
 
         for (String name: aliases) {
             if (oldName.equals(name)) continue;
 
             putMethod(name, new AliasMethod(this, method, oldName));
         }
         invalidateCoreClasses();
         invalidateCacheDescendants();
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
 
         Ruby runtime = getRuntime();
         IRubyObject classObj = getConstantAtSpecial(name);
         RubyClass clazz;
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw runtime.newTypeError(name + " is not a class");
             clazz = (RubyClass)classObj;
 
             if (superClazz != null) {
                 RubyClass tmp = clazz.getSuperClass();
                 while (tmp != null && tmp.isIncluded()) tmp = tmp.getSuperClass(); // need to skip IncludedModuleWrappers
                 if (tmp != null) tmp = tmp.getRealClass();
                 if (tmp != superClazz) throw runtime.newTypeError("superclass mismatch for class " + name);
                 // superClazz = null;
             }
         } else if (classProviders != null && (clazz = searchProvidersForClass(name, superClazz)) != null) {
             // reopen a java class
         } else {
             if (superClazz == null) superClazz = runtime.getObject();
             if (superClazz == runtime.getObject() && RubyInstanceConfig.REIFY_RUBY_CLASSES) {
                 clazz = RubyClass.newClass(runtime, superClazz, name, REIFYING_OBJECT_ALLOCATOR, this, true);
             } else {
                 clazz = RubyClass.newClass(runtime, superClazz, name, superClazz.getAllocator(), this, true);
             }
         }
 
         return clazz;
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyModule defineOrGetModuleUnder(String name) {
         // This method is intended only for defining new modules in Ruby code
         Ruby runtime = getRuntime();
         IRubyObject moduleObj = getConstantAtSpecial(name);
         RubyModule module;
         if (moduleObj != null) {
             if (!moduleObj.isModule()) throw runtime.newTypeError(name + " is not a module");
             module = (RubyModule)moduleObj;
         } else if (classProviders != null && (module = searchProvidersForModule(name)) != null) {
             // reopen a java module
         } else {
             module = RubyModule.newModule(runtime, name, this, true); 
         }
         return module;
     }
 
     /** rb_define_class_under
      *  this method should be used only as an API to define/open nested classes 
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator) {
         return getRuntime().defineClassUnder(name, superClass, allocator, this);
     }
 
     /** rb_define_module_under
      *  this method should be used only as an API to define/open nested module
      */
     public RubyModule defineModuleUnder(String name) {
         return getRuntime().defineModuleUnder(name, this);
     }
 
     private void addAccessor(ThreadContext context, String internedName, Visibility visibility, boolean readable, boolean writeable) {
         assert internedName == internedName.intern() : internedName + " is not interned";
 
         final Ruby runtime = context.runtime;
 
         if (visibility == PRIVATE) {
             //FIXME warning
         } else if (visibility == MODULE_FUNCTION) {
             visibility = PRIVATE;
             // FIXME warning
         }
 
         if (!(IdUtil.isLocal(internedName) || IdUtil.isConstant(internedName))) {
             throw runtime.newNameError("invalid attribute name", internedName);
         }
 
         final String variableName = ("@" + internedName).intern();
         if (readable) {
             addMethod(internedName, new AttrReaderMethod(this, visibility, CallConfiguration.FrameNoneScopeNone, variableName));
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
         if (writeable) {
             internedName = (internedName + "=").intern();
             addMethod(internedName, new AttrWriterMethod(this, visibility, CallConfiguration.FrameNoneScopeNone, variableName));
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
     }
 
     /** set_method_visibility
      *
      */
     public void setMethodVisibility(IRubyObject[] methods, Visibility visibility) {
         for (int i = 0; i < methods.length; i++) {
             exportMethod(methods[i].asJavaString(), visibility);
         }
     }
 
     /** rb_export_method
      *
      */
     public void exportMethod(String name, Visibility visibility) {
         Ruby runtime = getRuntime();
 
         DynamicMethod method = deepMethodSearch(name, runtime);
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 // FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
                 addMethod(name, new WrapperMethod(this, method, visibility));
             }
 
             invalidateCoreClasses();
             invalidateCacheDescendants();
         }
     }
 
     private DynamicMethod deepMethodSearch(String name, Ruby runtime) {
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined() && isModule()) {
             method = runtime.getObject().searchMethod(name);
         }
 
         if (method.isUndefined()) {
             throw runtime.newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
         return method;
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility() == PRIVATE);
         }
         return false;
     }
     
     public boolean isMethodBound(String name, boolean checkVisibility, boolean checkRespondTo) {
         if (!checkRespondTo) return isMethodBound(name, checkVisibility);
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined() && !method.isNotImplemented()) {
             return !(checkVisibility && method.getVisibility() == PRIVATE);
         }
         return false;
     }
 
-    public void checkMethodBound(ThreadContext context, IRubyObject[] args, Visibility visibility) {
-        if (args.length == 0) {
-            throw context.runtime.newArgumentError("no method name given");
-        }
-        String name = args[0].asJavaString();
-
-        DynamicMethod method = searchMethod(name);
-        if (!method.isUndefined() && method.getVisibility() != visibility) {
-            Ruby runtime = context.runtime;
-            RubyNameError.RubyNameErrorMessage message = new RubyNameError.RubyNameErrorMessage(runtime, this,
-                    runtime.newString(name), method.getVisibility(), CallType.NORMAL);
-
-            throw runtime.newNoMethodError(message.to_str(context).asJavaString(), name, NEVER);
-        }
-    }
-
     public IRubyObject newMethod(IRubyObject receiver, String methodName, boolean bound, Visibility visibility) {
         return newMethod(receiver, methodName, bound, visibility, false, true);
     }
 
     public IRubyObject newMethod(IRubyObject receiver, final String methodName, boolean bound, Visibility visibility, boolean respondToMissing) {
         return newMethod(receiver, methodName, bound, visibility, respondToMissing, true);
     }
 
     public static class RespondToMissingMethod extends JavaMethod.JavaMethodNBlock {
         final CallSite site;
         public RespondToMissingMethod(RubyModule implClass, Visibility vis, String methodName) {
             super(implClass, vis);
 
             site = new FunctionalCachingCallSite(methodName);
         }
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             return site.call(context, self, self, args, block);
         }
 
         public boolean equals(Object other) {
             if (!(other instanceof RespondToMissingMethod)) return false;
 
             RespondToMissingMethod rtmm = (RespondToMissingMethod)other;
 
             return this.site.methodName.equals(rtmm.site.methodName) &&
                     getImplementationClass() == rtmm.getImplementationClass();
         }
     }
 
     public IRubyObject newMethod(IRubyObject receiver, final String methodName, boolean bound, Visibility visibility, boolean respondToMissing, boolean priv) {
         DynamicMethod method = searchMethod(methodName);
 
         if (method.isUndefined() ||
             (visibility != null && method.getVisibility() != visibility)) {
             if (respondToMissing) { // 1.9 behavior
                 if (receiver.respondsToMissing(methodName, priv)) {
                     method = new RespondToMissingMethod(this, PUBLIC, methodName);
                 } else {
                     throw getRuntime().newNameError("undefined method `" + methodName +
                         "' for class `" + this.getName() + "'", methodName);
                 }
             } else {
                 throw getRuntime().newNameError("undefined method `" + methodName +
                     "' for class `" + this.getName() + "'", methodName);
             }
         }
 
         RubyModule implementationModule = method.getImplementationClass();
         RubyModule originModule = this;
         while (originModule != implementationModule && originModule.isSingleton()) {
             originModule = ((MetaClass)originModule).getRealClass();
         }
 
         RubyMethod newMethod;
         if (bound) {
             newMethod = RubyMethod.newMethod(implementationModule, methodName, originModule, methodName, method, receiver);
         } else {
             newMethod = RubyUnboundMethod.newUnboundMethod(implementationModule, methodName, originModule, methodName, method);
         }
         newMethod.infectBy(this);
 
         return newMethod;
     }
 
     @JRubyMethod(name = "define_method", visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, Block block) {
         Ruby runtime = context.runtime;
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = PUBLIC;
 
         RubyProc proc = runtime.newProc(Block.Type.LAMBDA, block);
 
         // a normal block passed to define_method changes to do arity checking; make it a lambda
         proc.getBlock().type = Block.Type.LAMBDA;
         
         newMethod = createProcMethod(name, visibility, proc);
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, visibility, context, runtime);
 
         return proc;
     }
     
     @JRubyMethod(name = "define_method", visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         Ruby runtime = context.runtime;
         IRubyObject body;
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = PUBLIC;
 
         if (runtime.getProc().isInstance(arg1)) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (RubyProc)arg1;
             body = proc;
 
             newMethod = createProcMethod(name, visibility, proc);
         } else if (runtime.getMethod().isInstance(arg1)) {
             RubyMethod method = (RubyMethod)arg1;
             body = method;
 
             newMethod = new MethodMethod(this, method.unbind(), visibility);
         } else {
             throw runtime.newTypeError("wrong argument type " + arg1.getType().getName() + " (expected Proc/Method)");
         }
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, visibility, context, runtime);
 
         return body;
     }
     @Deprecated
     public IRubyObject define_method(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1:
             return define_method(context, args[0], block);
         case 2:
             return define_method(context, args[0], args[1], block);
         default:
             throw context.runtime.newArgumentError("wrong number of arguments (" + args.length + " for 2)");
         }
     }
     
     private DynamicMethod createProcMethod(String name, Visibility visibility, RubyProc proc) {
         Block block = proc.getBlock();
         block.getBinding().getFrame().setKlazz(this);
         block.getBinding().getFrame().setName(name);
         block.getBinding().setMethod(name);
         
         StaticScope scope = block.getBody().getStaticScope();
 
         // for zsupers in define_method (blech!) we tell the proc scope to act as the "argument" scope
         scope.makeArgumentScope();
 
         Arity arity = block.arity();
         // just using required is broken...but no more broken than before zsuper refactoring
         scope.setRequiredArgs(arity.required());
 
         if(!arity.isFixed()) {
             scope.setRestArg(arity.required());
         }
 
         return new ProcMethod(this, proc, visibility);
     }
 
     @Deprecated
     public IRubyObject executeUnder(ThreadContext context, Callback method, IRubyObject[] args, Block block) {
         context.preExecuteUnder(this, block);
         try {
             return method.execute(this, args, block);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     @JRubyMethod(name = "name")
     public IRubyObject name() {
         Ruby runtime = getRuntime();
         if (getBaseName() == null) {
             return RubyString.newEmptyString(runtime);
         } else {
             return runtime.newString(getName());
         }
     }
 
     @JRubyMethod(name = "name", compat = RUBY1_9)
     public IRubyObject name19() {
         Ruby runtime = getRuntime();
         if (getBaseName() == null) {
             return runtime.getNil();
         } else {
             return runtime.newString(getName());
         }
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Map.Entry<String, DynamicMethod> entry : getMethods().entrySet()) {
             DynamicMethod method = entry.getValue();
             // Do not clone cached methods
             // FIXME: MRI copies all methods here
             if (method.getImplementationClass() == realType || method.isUndefined()) {
                 
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make DynamicMethod immutable
                 DynamicMethod clonedMethod = method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.putMethod(entry.getKey(), clonedMethod);
             }
         }
 
         return clone;
     }
 
     /** rb_mod_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original) {
         super.initialize_copy(original);
 
         RubyModule originalModule = (RubyModule)original;
 
         if (!getMetaClass().isSingleton()) setMetaClass(originalModule.getSingletonClassClone());
         setSuperClass(originalModule.getSuperClass());
         if (originalModule.hasVariables()) syncVariables(originalModule);
         syncConstants(originalModule);
 
         originalModule.cloneMethods(this);
 
         return this;
     }
 
     public void syncConstants(RubyModule other) {
         if (other.getConstantMap() != Collections.EMPTY_MAP) {
             getConstantMapForWrite().putAll(other.getConstantMap());
         }
     }
 
     public void syncClassVariables(RubyModule other) {
         if (other.getClassVariablesForRead() != Collections.EMPTY_MAP) {
             getClassVariables().putAll(other.getClassVariablesForRead());
         }
     }
 
     /** rb_mod_included_modules
      *
      */
     @JRubyMethod(name = "included_modules")
     public RubyArray included_modules(ThreadContext context) {
         RubyArray ary = context.runtime.newArray();
 
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isIncluded()) {
                 ary.append(p.getNonIncludedClass());
             }
         }
 
         return ary;
     }
 
     /** rb_mod_ancestors
      *
      */
     @JRubyMethod(name = "ancestors")
     public RubyArray ancestors(ThreadContext context) {
         return context.runtime.newArray(getAncestorList());
     }
     
     @Deprecated
     public RubyArray ancestors() {
         return getRuntime().newArray(getAncestorList());
     }
 
     public List<IRubyObject> getAncestorList() {
         ArrayList<IRubyObject> list = new ArrayList<IRubyObject>();
 
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if(!module.isSingleton()) list.add(module.getNonIncludedClass());
         }
 
         return list;
     }
 
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.getNonIncludedClass() == type) return true;
         }
 
         return false;
     }
 
     @Override
     public int hashCode() {
         return id;
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
         return getRuntime().newFixnum(id);
     }
 
     /** rb_mod_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     @Override
     public IRubyObject to_s() {
         if(isSingleton()){            
             IRubyObject attached = ((MetaClass)this).getAttached();
             StringBuilder buffer = new StringBuilder("#<Class:");
             if (attached != null) { // FIXME: figure out why we get null sometimes
                 if(attached instanceof RubyClass || attached instanceof RubyModule){
                     buffer.append(attached.inspect());
                 }else{
                     buffer.append(attached.anyToString());
                 }
             }
             buffer.append(">");
             return getRuntime().newString(buffer.toString());
         }
         return getRuntime().newString(getName());
     }
 
     /** rb_mod_eqq
      *
      */
     @JRubyMethod(name = "===", required = 1)
     @Override
     public RubyBoolean op_eqq(ThreadContext context, IRubyObject obj) {
         return context.runtime.newBoolean(isInstance(obj));
     }
 
     /**
      * We override equals here to provide a faster path, since equality for modules
      * is pretty cut and dried.
      * @param other The object to check for equality
      * @return true if reference equality, false otherwise
      */
     @Override
     public boolean equals(Object other) {
         return this == other;
     }
 
     @JRubyMethod(name = "==", required = 1)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         return super.op_equal(context, other);
     }
 
     /** rb_mod_freeze
      *
      */
     @JRubyMethod(name = "freeze")
     @Override
     public final IRubyObject freeze(ThreadContext context) {
         to_s();
         return super.freeze(context);
     }
 
     /** rb_mod_le
     *
     */
     @JRubyMethod(name = "<=", required = 1)
    public IRubyObject op_le(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         if (isKindOfModule((RubyModule) obj)) return getRuntime().getTrue();
         if (((RubyModule) obj).isKindOfModule(this)) return getRuntime().getFalse();
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_lt
     *
     */
     @JRubyMethod(name = "<", required = 1)
    public IRubyObject op_lt(IRubyObject obj) {
         return obj == this ? getRuntime().getFalse() : op_le(obj);
     }
 
     /** rb_mod_ge
     *
     */
     @JRubyMethod(name = ">=", required = 1)
    public IRubyObject op_ge(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         return ((RubyModule) obj).op_le(this);
     }
 
     /** rb_mod_gt
     *
     */
     @JRubyMethod(name = ">", required = 1)
    public IRubyObject op_gt(IRubyObject obj) {
         return this == obj ? getRuntime().getFalse() : op_ge(obj);
     }
 
     /** rb_mod_cmp
     *
     */
     @JRubyMethod(name = "<=>", required = 1)
    public IRubyObject op_cmp(IRubyObject obj) {
         if (this == obj) return getRuntime().newFixnum(0);
         if (!(obj instanceof RubyModule)) return getRuntime().getNil();
 
         RubyModule module = (RubyModule) obj;
 
         if (module.isKindOfModule(this)) return getRuntime().newFixnum(1);
         if (this.isKindOfModule(module)) return getRuntime().newFixnum(-1);
 
         return getRuntime().getNil();
     }
 
     public boolean isKindOfModule(RubyModule type) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.isSame(type)) return true;
         }
 
         return false;
     }
 
     protected boolean isSame(RubyModule module) {
         return this == module;
     }
 
     /** rb_mod_initialize
      *
      */
     @JRubyMethod(name = "initialize", frame = true, visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, Block block) {
         if (block.isGiven()) {
             module_exec(context, new IRubyObject[] {this}, block);
         }
 
         return getRuntime().getNil();
     }
     
     public void addReadWriteAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), PUBLIC, true, true);
     }
     
     public void addReadAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), PUBLIC, true, false);
     }
     
     public void addWriteAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), PUBLIC, false, true);
     }
 
     /** rb_mod_attr
      *
      */
     @JRubyMethod(name = "attr", required = 1, optional = 1, visibility = PRIVATE, reads = VISIBILITY, compat = RUBY1_8)
     public IRubyObject attr(ThreadContext context, IRubyObject[] args) {
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
         
         addAccessor(context, args[0].asJavaString().intern(), visibility, true, writeable);
 
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "attr", rest = true, visibility = PRIVATE, reads = VISIBILITY, compat = RUBY1_9)
     public IRubyObject attr19(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
 
         if (args.length == 2 && (args[1] == runtime.getTrue() || args[1] == runtime.getFalse())) {
             runtime.getWarnings().warn(ID.OBSOLETE_ARGUMENT, "optional boolean argument is obsoleted");
             addAccessor(context, args[0].asJavaString().intern(), context.getCurrentVisibility(), args[0].isTrue(), true);
             return runtime.getNil();
         }
 
         return attr_reader(context, args);
     }
 
     @Deprecated
     public IRubyObject attr_reader(IRubyObject[] args) {
         return attr_reader(getRuntime().getCurrentContext(), args);
     }
     
     /** rb_mod_attr_reader
      *
      */
     @JRubyMethod(name = "attr_reader", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_reader(ThreadContext context, IRubyObject[] args) {
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
 
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), visibility, true, false);
         }
 
         return context.runtime.getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     @JRubyMethod(name = "attr_writer", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_writer(ThreadContext context, IRubyObject[] args) {
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
 
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), visibility, false, true);
         }
 
         return context.runtime.getNil();
     }
 
 
     @Deprecated
     public IRubyObject attr_accessor(IRubyObject[] args) {
         return attr_accessor(getRuntime().getCurrentContext(), args);
     }
 
     /** rb_mod_attr_accessor
      *  Note: this method should not be called from Java in most cases, since
      *  it depends on Ruby frame state for visibility. Use add[Read/Write]Attribute instead.
      */
     @JRubyMethod(name = "attr_accessor", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_accessor(ThreadContext context, IRubyObject[] args) {
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
 
         for (int i = 0; i < args.length; i++) {
             // This is almost always already interned, since it will be called with a symbol in most cases
             // but when created from Java code, we might get an argument that needs to be interned.
             // addAccessor has as a precondition that the string MUST be interned
             addAccessor(context, args[i].asJavaString().intern(), visibility, true, true);
         }
 
         return context.runtime.getNil();
     }
 
     /**
      * Get a list of all instance methods names of the provided visibility unless not is true, then 
      * get all methods which are not the provided 
      * 
      * @param args passed into one of the Ruby instance_method methods
      * @param visibility to find matching instance methods against
      * @param not if true only find methods not matching supplied visibility
      * @return a RubyArray of instance method names
      */
     private RubyArray instance_methods(IRubyObject[] args, final Visibility visibility, boolean not, boolean useSymbols) {
         boolean includeSuper = args.length > 0 ? args[0].isTrue() : true;
         Ruby runtime = getRuntime();
         RubyArray ary = runtime.newArray();
         Set<String> seen = new HashSet<String>();
 
         populateInstanceMethodNames(seen, ary, visibility, not, useSymbols, includeSuper);
 
         return ary;
     }
 
     public void populateInstanceMethodNames(Set<String> seen, RubyArray ary, final Visibility visibility, boolean not, boolean useSymbols, boolean includeSuper) {
         Ruby runtime = getRuntime();
 
         for (RubyModule type = this; type != null; type = type.getSuperClass()) {
             RubyModule realType = type.getNonIncludedClass();
             for (Map.Entry entry : type.getMethods().entrySet()) {
                 String methodName = (String) entry.getKey();
 
                 if (! seen.contains(methodName)) {
                     seen.add(methodName);
 
                     DynamicMethod method = (DynamicMethod) entry.getValue();
                     if (method.getImplementationClass() == realType &&
                         (!not && method.getVisibility() == visibility || (not && method.getVisibility() != visibility)) &&
                         ! method.isUndefined()) {
 
                         ary.append(useSymbols ? runtime.newSymbol(methodName) : runtime.newString(methodName));
                     }
                 }
             }
 
             if (!includeSuper) {
                 break;
             }
         }
     }
 
     @JRubyMethod(name = "instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray instance_methods(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, true, false);
     }
 
     @JRubyMethod(name = "instance_methods", optional = 1, compat = RUBY1_9)
     public RubyArray instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, true, true);
     }
 
     @JRubyMethod(name = "public_instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray public_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PUBLIC, false, false);
     }
 
     @JRubyMethod(name = "public_instance_methods", optional = 1, compat = RUBY1_9)
     public RubyArray public_instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PUBLIC, false, true);
     }
 
     @JRubyMethod(name = "instance_method", required = 1)
     public IRubyObject instance_method(IRubyObject symbol) {
         return newMethod(null, symbol.asJavaString(), false, null);
     }
 
     /** rb_class_protected_instance_methods
      *
      */
     @JRubyMethod(name = "protected_instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray protected_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PROTECTED, false, false);
     }
 
     @JRubyMethod(name = "protected_instance_methods", optional = 1, compat = RUBY1_9)
     public RubyArray protected_instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PROTECTED, false, true);
     }
 
     /** rb_class_private_instance_methods
      *
      */
     @JRubyMethod(name = "private_instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray private_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, false, false);
     }
 
     @JRubyMethod(name = "private_instance_methods", optional = 1, compat = RUBY1_9)
     public RubyArray private_instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, false, true);
     }
 
     /** rb_mod_append_features
      *
      */
     @JRubyMethod(name = "append_features", required = 1, visibility = PRIVATE)
     public RubyModule append_features(IRubyObject module) {
         if (!(module instanceof RubyModule)) {
             // MRI error message says Class, even though Module is ok 
             throw getRuntime().newTypeError(module,getRuntime().getClassClass());
         }
         ((RubyModule) module).includeModule(this);
         return this;
     }
 
     /** rb_mod_extend_object
      *
      */
     @JRubyMethod(name = "extend_object", required = 1, visibility = PRIVATE)
     public IRubyObject extend_object(IRubyObject obj) {
         obj.getSingletonClass().includeModule(this);
         return obj;
     }
 
     /** rb_mod_include
      *
      */
     @JRubyMethod(name = "include", rest = true, visibility = PRIVATE)
     public RubyModule include(IRubyObject[] modules) {
         ThreadContext context = getRuntime().getCurrentContext();
         // MRI checks all types first:
         for (int i = modules.length; --i >= 0; ) {
             IRubyObject obj = modules[i];
             if (!obj.isModule()) {
                 throw context.runtime.newTypeError(obj, context.runtime.getModule());
             }
         }
         for (int i = modules.length - 1; i >= 0; i--) {
             modules[i].callMethod(context, "append_features", this);
             modules[i].callMethod(context, "included", this);
         }
 
         return this;
     }
 
     @JRubyMethod(name = "included", required = 1, visibility = PRIVATE)
     public IRubyObject included(ThreadContext context, IRubyObject other) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "extended", required = 1, frame = true, visibility = PRIVATE)
     public IRubyObject extended(ThreadContext context, IRubyObject other, Block block) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "mix", visibility = PRIVATE, compat = RUBY2_0)
     public IRubyObject mix(ThreadContext context, IRubyObject mod) {
         Ruby runtime = context.runtime;
 
         if (!mod.isModule()) {
             throw runtime.newTypeError(mod, runtime.getModule());
         }
 
         for (Map.Entry<String, DynamicMethod> entry : ((RubyModule)mod).methods.entrySet()) {
             if (methods.containsKey(entry.getKey())) {
                 throw runtime.newArgumentError("method would conflict - " + entry.getKey());
             }
         }
 
         for (Map.Entry<String, DynamicMethod> entry : ((RubyModule)mod).methods.entrySet()) {
             getMethodsForWrite().put(entry.getKey(), entry.getValue().dup());
         }
 
         return mod;
     }
 
     @JRubyMethod(name = "mix", visibility = PRIVATE, compat = RUBY2_0)
     public IRubyObject mix(ThreadContext context, IRubyObject mod, IRubyObject hash0) {
         Ruby runtime = context.runtime;
         RubyHash methodNames = null;
 
         if (!mod.isModule()) {
             throw runtime.newTypeError(mod, runtime.getModule());
         }
 
         if (hash0 instanceof RubyHash) {
             methodNames = (RubyHash)hash0;
         } else {
             throw runtime.newTypeError(hash0, runtime.getHash());
         }
         
         for (Map.Entry entry : (Set<Map.Entry<Object, Object>>)methodNames.directEntrySet()) {
             String name = entry.getValue().toString();
             if (methods.containsKey(entry.getValue().toString())) {
                 throw runtime.newArgumentError("constant would conflict - " + name);
             }
         }
 
         for (Map.Entry<String, DynamicMethod> entry : ((RubyModule)mod).methods.entrySet()) {
             if (methods.containsKey(entry.getKey())) {
                 throw runtime.newArgumentError("method would conflict - " + entry.getKey());
             }
         }
 
         for (Map.Entry<String, DynamicMethod> entry : ((RubyModule)mod).methods.entrySet()) {
             String name = entry.getKey();
             IRubyObject mapped = methodNames.fastARef(runtime.newSymbol(name));
             if (mapped == NEVER) {
                 // unmapped
             } else if (mapped == context.nil) {
                 // do not mix
                 continue;
             } else {
                 name = mapped.toString();
             }
             getMethodsForWrite().put(name, entry.getValue().dup());
         }
 
         return mod;
     }
 
     private void setVisibility(ThreadContext context, IRubyObject[] args, Visibility visibility) {
         if (args.length == 0) {
             // Note: we change current frames visibility here because the methods which call
             // this method are all "fast" (e.g. they do not created their own frame).
             context.setCurrentVisibility(visibility);
         } else {
             setMethodVisibility(args, visibility);
         }
     }
 
     /** rb_mod_public
      *
      */
     @JRubyMethod(name = "public", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule rbPublic(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, PUBLIC);
         return this;
     }
 
     /** rb_mod_protected
      *
      */
     @JRubyMethod(name = "protected", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule rbProtected(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, PROTECTED);
         return this;
     }
 
     /** rb_mod_private
      *
      */
     @JRubyMethod(name = "private", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule rbPrivate(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, PRIVATE);
         return this;
     }
 
     /** rb_mod_modfunc
      *
      */
     @JRubyMethod(name = "module_function", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule module_function(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
 
         if (args.length == 0) {
             context.setCurrentVisibility(MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, PRIVATE);
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asJavaString().intern();
                 DynamicMethod method = deepMethodSearch(name, runtime);
                 getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), method, PUBLIC));
                 callMethod(context, "singleton_method_added", context.runtime.fastNewSymbol(name));
             }
         }
         return this;
     }
 
     @JRubyMethod(name = "method_added", required = 1, visibility = PRIVATE)
     public IRubyObject method_added(ThreadContext context, IRubyObject nothing) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "method_removed", required = 1, visibility = PRIVATE)
     public IRubyObject method_removed(ThreadContext context, IRubyObject nothing) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "method_undefined", required = 1, visibility = PRIVATE)
     public IRubyObject method_undefined(ThreadContext context, IRubyObject nothing) {
         return context.runtime.getNil();
     }
     
     @JRubyMethod(name = "method_defined?", required = 1)
     public RubyBoolean method_defined_p(ThreadContext context, IRubyObject symbol) {
         return isMethodBound(symbol.asJavaString(), true) ? context.runtime.getTrue() : context.runtime.getFalse();
     }
 
     @JRubyMethod(name = "public_method_defined?", required = 1)
     public IRubyObject public_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
 
         return context.runtime.newBoolean(!method.isUndefined() && method.getVisibility() == PUBLIC);
     }
 
     @JRubyMethod(name = "protected_method_defined?", required = 1)
     public IRubyObject protected_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
 
         return context.runtime.newBoolean(!method.isUndefined() && method.getVisibility() == PROTECTED);
     }
 	
     @JRubyMethod(name = "private_method_defined?", required = 1)
     public IRubyObject private_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
 
         return context.runtime.newBoolean(!method.isUndefined() && method.getVisibility() == PRIVATE);
     }
 
     @JRubyMethod(name = "public_class_method", rest = true)
     public RubyModule public_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, PUBLIC);
         return this;
     }
 
     @JRubyMethod(name = "private_class_method", rest = true)
     public RubyModule private_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, PRIVATE);
         return this;
     }
 
     @JRubyMethod(name = "alias_method", required = 2, visibility = PRIVATE)
     public RubyModule alias_method(ThreadContext context, IRubyObject newId, IRubyObject oldId) {
         String newName = newId.asJavaString();
         defineAlias(newName, oldId.asJavaString());
         RubySymbol newSym = newId instanceof RubySymbol ? (RubySymbol)newId :
             context.runtime.newSymbol(newName);
         if (isSingleton()) {
             ((MetaClass)this).getAttached().callMethod(context, "singleton_method_added", newSym);
         } else {
             callMethod(context, "method_added", newSym);
         }
         return this;
     }
 
     @JRubyMethod(name = "undef_method", required = 1, rest = true, visibility = PRIVATE)
     public RubyModule undef_method(ThreadContext context, IRubyObject[] args) {
         for (int i=0; i<args.length; i++) {
             undef(context, args[i].asJavaString());
         }
         return this;
     }
 
     @JRubyMethod(name = {"module_eval", "class_eval"})
     public IRubyObject module_eval(ThreadContext context, Block block) {
         return specificEval(context, this, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"})
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, Block block) {
         return specificEval(context, this, arg0, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"})
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return specificEval(context, this, arg0, arg1, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"})
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return specificEval(context, this, arg0, arg1, arg2, block);
     }
     @Deprecated
     public IRubyObject module_eval(ThreadContext context, IRubyObject[] args, Block block) {
         return specificEval(context, this, args, block);
     }
 
     @JRubyMethod(name = {"module_exec", "class_exec"})
     public IRubyObject module_exec(ThreadContext context, Block block) {
         if (block.isGiven()) {
             return yieldUnder(context, this, IRubyObject.NULL_ARRAY, block);
         } else {
             throw context.runtime.newLocalJumpErrorNoBlock();
         }
     }
 
     @JRubyMethod(name = {"module_exec", "class_exec"}, rest = true)
     public IRubyObject module_exec(ThreadContext context, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             return yieldUnder(context, this, args, block);
         } else {
             throw context.runtime.newLocalJumpErrorNoBlock();
         }
     }
 
     @JRubyMethod(name = "remove_method", required = 1, rest = true, visibility = PRIVATE)
     public RubyModule remove_method(ThreadContext context, IRubyObject[] args) {
         for(int i=0;i<args.length;i++) {
             removeMethod(context, args[i].asJavaString());
         }
         return this;
     }
 
     public static void marshalTo(RubyModule module, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(module);
         output.writeString(MarshalStream.getPathFromClass(module));
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = RubyString.byteListToString(input.unmarshalString());
         RubyModule result = UnmarshalStream.getModuleFromPath(input.getRuntime(), name);
         input.registerLinkTarget(result);
         return result;
     }
 
     /* Module class methods */
     
     /** 
      * Return an array of nested modules or classes.
      */
     @JRubyMethod(name = "nesting", frame = true, meta = true)
     public static RubyArray nesting(ThreadContext context, IRubyObject recv, Block block) {
         Ruby runtime = context.runtime;
         RubyModule object = runtime.getObject();
         StaticScope scope = context.getCurrentScope().getStaticScope();
         RubyArray result = runtime.newArray();
         
         for (StaticScope current = scope; current.getModule() != object; current = current.getPreviousCRefScope()) {
             result.append(current.getModule());
         }
         
         return result;
     }
 
     /**
      * Include the given module and all related modules into the hierarchy above
      * this module/class. Inspects the hierarchy to ensure the same module isn't
      * included twice, and selects an appropriate insertion point for each incoming
      * module.
      * 
      * @param baseModule The module to include, along with any modules it itself includes
      */
     private void doIncludeModule(RubyModule baseModule) {
         List<RubyModule> modulesToInclude = gatherModules(baseModule);
         
         RubyModule currentInclusionPoint = this;
         ModuleLoop: for (RubyModule nextModule : modulesToInclude) {
             checkForCyclicInclude(nextModule);
 
             boolean superclassSeen = false;
 
             // scan class hierarchy for module
             for (RubyClass nextClass = this.getSuperClass(); nextClass != null; nextClass = nextClass.getSuperClass()) {
                 if (doesTheClassWrapTheModule(nextClass, nextModule)) {
                     // next in hierarchy is an included version of the module we're attempting,
                     // so we skip including it
                     
                     // if we haven't encountered a real superclass, use the found module as the new inclusion point
                     if (!superclassSeen) currentInclusionPoint = nextClass;
                     
                     continue ModuleLoop;
                 } else {
                     superclassSeen = true;
                 }
             }
 
             currentInclusionPoint = proceedWithInclude(currentInclusionPoint, nextModule);
         }
     }
     
     /**
      * Is the given class a wrapper for the specified module?
      * 
      * @param theClass The class to inspect
      * @param theModule The module we're looking for
      * @return true if the class is a wrapper for the module, false otherwise
      */
     private boolean doesTheClassWrapTheModule(RubyClass theClass, RubyModule theModule) {
         return theClass.isIncluded() &&
                 theClass.getNonIncludedClass() == theModule.getNonIncludedClass();
     }
 
     /**
      * Gather all modules that would be included by including the given module.
      * The resulting list contains the given module and its (zero or more)
      * module-wrapping superclasses.
      * 
      * @param baseModule The base module from which to aggregate modules
      * @return A list of all modules that would be included by including the given module
      */
     private List<RubyModule> gatherModules(RubyModule baseModule) {
         // build a list of all modules to consider for inclusion
         List<RubyModule> modulesToInclude = new ArrayList<RubyModule>();
         while (baseModule != null) {
             modulesToInclude.add(baseModule);
             baseModule = baseModule.getSuperClass();
         }
 
         return modulesToInclude;
     }
 
     /**
      * Actually proceed with including the specified module above the given target
      * in a hierarchy. Return the new module wrapper.
      * 
      * @param insertAbove The hierarchy target above which to include the wrapped module
      * @param moduleToInclude The module to wrap and include
      * @return The new module wrapper resulting from this include
      */
     private RubyModule proceedWithInclude(RubyModule insertAbove, RubyModule moduleToInclude) {
         // In the current logic, if we get here we know that module is not an
         // IncludedModuleWrapper, so there's no need to fish out the delegate. But just
         // in case the logic should change later, let's do it anyway
         RubyClass wrapper = new IncludedModuleWrapper(getRuntime(), insertAbove.getSuperClass(), moduleToInclude.getNonIncludedClass());
         
         // if the insertion point is a class, update subclass lists
         if (insertAbove instanceof RubyClass) {
             RubyClass insertAboveClass = (RubyClass)insertAbove;
             
             // if there's a non-null superclass, we're including into a normal class hierarchy;
             // update subclass relationships to avoid stale parent/child relationships
             if (insertAboveClass.getSuperClass() != null) {
                 insertAboveClass.getSuperClass().replaceSubclass(insertAboveClass, wrapper);
             }
             
             wrapper.addSubclass(insertAboveClass);
         }
         
         insertAbove.setSuperClass(wrapper);
         insertAbove = insertAbove.getSuperClass();
         return insertAbove;
     }
 
 
     //
     ////////////////// CLASS VARIABLE RUBY METHODS ////////////////
     //
 
     @JRubyMethod(name = "class_variable_defined?", required = 1)
     public IRubyObject class_variable_defined_p(ThreadContext context, IRubyObject var) {
         String internedName = validateClassVariable(var.asJavaString().intern());
         RubyModule module = this;
         do {
             if (module.hasClassVariable(internedName)) {
                 return context.runtime.getTrue();
             }
         } while ((module = module.getSuperClass()) != null);
 
         return context.runtime.getFalse();
     }
 
     /** rb_mod_cvar_get
      *
      */
     @JRubyMethod(name = "class_variable_get", visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject class_variable_get(IRubyObject var) {
         return getClassVar(validateClassVariable(var.asJavaString()).intern());
     }
 
     @JRubyMethod(name = "class_variable_get", compat = RUBY1_9)
     public IRubyObject class_variable_get19(IRubyObject var) {
         return class_variable_get(var);
     }
 
     /** rb_mod_cvar_set
      *
      */
     @JRubyMethod(name = "class_variable_set", visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject class_variable_set(IRubyObject var, IRubyObject value) {
         return setClassVar(validateClassVariable(var.asJavaString()).intern(), value);
     }
 
     @JRubyMethod(name = "class_variable_set", compat = RUBY1_9)
     public IRubyObject class_variable_set19(IRubyObject var, IRubyObject value) {
         return class_variable_set(var, value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     @JRubyMethod(name = "remove_class_variable", visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject remove_class_variable(ThreadContext context, IRubyObject name) {
         return removeClassVariable(name.asJavaString());
     }
 
     @JRubyMethod(name = "remove_class_variable", compat = RUBY1_9)
     public IRubyObject remove_class_variable19(ThreadContext context, IRubyObject name) {
         return remove_class_variable(context, name);
     }
 
     /** rb_mod_class_variables
      *
      */
     @JRubyMethod(name = "class_variables", compat = RUBY1_8)
     public RubyArray class_variables(ThreadContext context) {
         Ruby runtime = context.runtime;
         RubyArray ary = runtime.newArray();
         
         Collection<String> names = classVariablesCommon();
         ary.addAll(names);
         return ary;
     }
 
     @JRubyMethod(name = "class_variables", compat = RUBY1_9)
     public RubyArray class_variables19(ThreadContext context) {
         Ruby runtime = context.runtime;
         RubyArray ary = runtime.newArray();
         
         Collection<String> names = classVariablesCommon();
         for (String name : names) {
             ary.add(runtime.newSymbol(name));
         }
         return ary;
     }
 
     private Collection<String> classVariablesCommon() {
         Set<String> names = new HashSet<String>();
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             names.addAll(p.getClassVariableNameList());
         }
         return names;
     }
 
 
     //
     ////////////////// CONSTANT RUBY METHODS ////////////////
     //
 
     /** rb_mod_const_defined
      *
      */
     @JRubyMethod(name = "const_defined?", required = 1, compat = RUBY1_8)
     public RubyBoolean const_defined_p(ThreadContext context, IRubyObject symbol) {
         // Note: includes part of fix for JRUBY-1339
         return context.runtime.newBoolean(fastIsConstantDefined(validateConstant(symbol.asJavaString()).intern()));
     }
 
     @JRubyMethod(name = "const_defined?", required = 1, optional = 1, compat = RUBY1_9)
     public RubyBoolean const_defined_p19(ThreadContext context, IRubyObject[] args) {
         IRubyObject symbol = args[0];
         boolean inherit = args.length == 1 || (!args[1].isNil() && args[1].isTrue());
 
         return context.runtime.newBoolean(fastIsConstantDefined19(validateConstant(symbol.asJavaString()).intern(), inherit));
     }
 
     /** rb_mod_const_get
      *
      */
     @JRubyMethod(name = "const_get", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject const_get(IRubyObject symbol) {
         return getConstant(validateConstant(symbol.asJavaString()));
     }
 
     @JRubyMethod(name = "const_get", required = 1, optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject const_get(ThreadContext context, IRubyObject[] args) {
         IRubyObject symbol = args[0];
         boolean inherit = args.length == 1 || (!args[1].isNil() && args[1].isTrue());
 
         // 1.9 only includes Object when inherit = true or unspecified (JRUBY-6224)
         return getConstant(validateConstant(symbol.asJavaString()), inherit, inherit);
     }
 
     /** rb_mod_const_set
      *
      */
     @JRubyMethod(name = "const_set", required = 2)
     public IRubyObject const_set(IRubyObject symbol, IRubyObject value) {
         IRubyObject constant = setConstant(validateConstant(symbol.asJavaString()).intern(), value);
 
         if (constant instanceof RubyModule) {
             ((RubyModule)constant).calculateName();
         }
         return constant;
     }
 
     @JRubyMethod(name = "remove_const", required = 1, visibility = PRIVATE)
     public IRubyObject remove_const(ThreadContext context, IRubyObject rubyName) {
         String name = validateConstant(rubyName.asJavaString());
         IRubyObject value;
         if ((value = deleteConstant(name)) != null) {
             invalidateConstantCache();
             if (value != UNDEF) {
                 return value;
             }
             removeAutoload(name);
             // FIXME: I'm not sure this is right, but the old code returned
             // the undef, which definitely isn't right...
             return context.runtime.getNil();
         }
 
         if (hasConstantInHierarchy(name)) {
             throw cannotRemoveError(name);
         }
 
         throw context.runtime.newNameError("constant " + name + " not defined for " + getName(), name);
     }
 
     private boolean hasConstantInHierarchy(final String name) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.hasConstant(name)) {
                 return true;
         }
         }
         return false;
     }
     
     /**
      * Base implementation of Module#const_missing, throws NameError for specific missing constant.
      * 
-     * @param name The constant name which was found to be missing
+     * @param rubyName The constant name which was found to be missing
      * @return Nothing! Absolutely nothing! (though subclasses might choose to return something)
      */
     @JRubyMethod(name = "const_missing", required = 1, frame = true)
     public IRubyObject const_missing(ThreadContext context, IRubyObject rubyName, Block block) {
         Ruby runtime = context.runtime;
         String name;
         
         if (this != runtime.getObject()) {
             name = getName() + "::" + rubyName.asJavaString();
         } else {
             name = rubyName.asJavaString();
         }
 
         throw runtime.newNameError("uninitialized constant " + name, name);
     }
 
     @JRubyMethod(name = "constants", compat = RUBY1_8)
     public RubyArray constants(ThreadContext context) {
         Ruby runtime = context.runtime;
         RubyArray array = runtime.newArray();
         Collection<String> constantNames = constantsCommon(runtime, true, true);
         array.addAll(constantNames);
         
         return array;
     }
 
     @JRubyMethod(name = "constants", compat = RUBY1_9)
     public RubyArray constants19(ThreadContext context) {
         return constantsCommon19(context, true, true);
     }
 
     @JRubyMethod(name = "constants", compat = RUBY1_9)
     public RubyArray constants19(ThreadContext context, IRubyObject allConstants) {
         return constantsCommon19(context, false, allConstants.isTrue());
     }
     
     public RubyArray constantsCommon19(ThreadContext context, boolean replaceModule, boolean allConstants) {
         Ruby runtime = context.runtime;
         RubyArray array = runtime.newArray();
         
         Collection<String> constantNames = constantsCommon(runtime, replaceModule, allConstants, false);
         
         for (String name : constantNames) {
             array.add(runtime.newSymbol(name));
         }
         return array;
     }
 
     /** rb_mod_constants
      *
      */
     public Collection<String> constantsCommon(Ruby runtime, boolean replaceModule, boolean allConstants) {
         return constantsCommon(runtime, replaceModule, allConstants, true);
     }
 
 
     public Collection<String> constantsCommon(Ruby runtime, boolean replaceModule, boolean allConstants, boolean includePrivate) {
         RubyModule objectClass = runtime.getObject();
 
         Collection<String> constantNames = new HashSet<String>();
         if (allConstants) {
             if ((replaceModule && runtime.getModule() == this) || objectClass == this) {
                 constantNames = objectClass.getConstantNames(includePrivate);
             } else {
                 Set<String> names = new HashSet<String>();
                 for (RubyModule module = this; module != null && module != objectClass; module = module.getSuperClass()) {
                     names.addAll(module.getConstantNames(includePrivate));
                 }
                 constantNames = names;
             }
         } else {
             if ((replaceModule && runtime.getModule() == this) || objectClass == this) {
                 constantNames = objectClass.getConstantNames(includePrivate);
             } else {
                 constantNames = getConstantNames(includePrivate);
             }
         }
 
         return constantNames;
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject private_constant(ThreadContext context, IRubyObject name) {
         setConstantVisibility(context, validateConstant(name.asJavaString()), true);
         invalidateConstantCache();
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_9, required = 1, rest = true)
     public IRubyObject private_constant(ThreadContext context, IRubyObject[] names) {
         for (IRubyObject name : names) {
             setConstantVisibility(context, validateConstant(name.asJavaString()), true);
         }
         invalidateConstantCache();
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject public_constant(ThreadContext context, IRubyObject name) {
         setConstantVisibility(context, validateConstant(name.asJavaString()), false);
         invalidateConstantCache();
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_9, required = 1, rest = true)
     public IRubyObject public_constant(ThreadContext context, IRubyObject[] names) {
         for (IRubyObject name : names) {
             setConstantVisibility(context, validateConstant(name.asJavaString()), false);
         }
         invalidateConstantCache();
         return this;
     }
 
     private void setConstantVisibility(ThreadContext context, String name, boolean hidden) {
         ConstantEntry entry = getConstantMap().get(name);
 
         if (entry == null) {
             throw context.runtime.newNameError("constant " + getName() + "::" + name + " not defined", name);
         }
 
         getConstantMapForWrite().put(name, new ConstantEntry(entry.value, hidden));
     }
 
     //
     ////////////////// CLASS VARIABLE API METHODS ////////////////
     //
 
     /**
      * Set the named class variable to the given value, provided taint and freeze allow setting it.
      * 
      * Ruby C equivalent = "rb_cvar_set"
      * 
      * @param name The variable name to set
      * @param value The value to set it to
      */
     public IRubyObject setClassVar(String name, IRubyObject value) {
         RubyModule module = this;
         do {
             if (module.hasClassVariable(name)) {
                 return module.storeClassVariable(name, value);
             }
         } while ((module = module.getSuperClass()) != null);
         
         return storeClassVariable(name, value);
     }
 
     @Deprecated
     public IRubyObject fastSetClassVar(final String internedName, final IRubyObject value) {
         return setClassVar(internedName, value);
     }
 
     /**
      * Retrieve the specified class variable, searching through this module, included modules, and supermodules.
      * 
      * Ruby C equivalent = "rb_cvar_get"
      * 
      * @param name The name of the variable to retrieve
      * @return The variable's value, or throws NameError if not found
      */
     public IRubyObject getClassVar(String name) {
         assert IdUtil.isClassVariable(name);
         Object value;
         RubyModule module = this;
 
         do {
             if ((value = module.fetchClassVariable(name)) != null) return (IRubyObject)value;
         } while ((module = module.getSuperClass()) != null);
 
         throw getRuntime().newNameError("uninitialized class variable " + name + " in " + getName(), name);
     }
 
     @Deprecated
     public IRubyObject fastGetClassVar(String internedName) {
         return getClassVar(internedName);
     }
 
     /**
      * Is class var defined?
      * 
      * Ruby C equivalent = "rb_cvar_defined"
      * 
      * @param name The class var to determine "is defined?"
      * @return true if true, false if false
      */
     public boolean isClassVarDefined(String name) {
         RubyModule module = this;
         do {
             if (module.hasClassVariable(name)) return true;
         } while ((module = module.getSuperClass()) != null);
 
         return false;
     }
 
     @Deprecated
     public boolean fastIsClassVarDefined(String internedName) {
         return isClassVarDefined(internedName);
     }
     
     /** rb_mod_remove_cvar
      *
      * @deprecated - use {@link #removeClassVariable(String)}
      */
     @Deprecated
     public IRubyObject removeCvar(IRubyObject name) {
         return removeClassVariable(name.asJavaString());
     }
 
     public IRubyObject removeClassVariable(String name) {
         String javaName = validateClassVariable(name);
         IRubyObject value;
 
         if ((value = deleteClassVariable(javaName)) != null) {
             return value;
         }
 
         if (isClassVarDefined(javaName)) {
             throw cannotRemoveError(javaName);
         }
 
         throw getRuntime().newNameError("class variable " + javaName + " not defined for " + getName(), javaName);
     }
 
 
     //
     ////////////////// CONSTANT API METHODS ////////////////
     //
 
     /**
      * This version searches superclasses if we're starting with Object. This
      * corresponds to logic in rb_const_defined_0 that recurses for Object only.
      *
      * @param name the constant name to find
      * @return the constant, or null if it was not found
      */
     public IRubyObject getConstantAtSpecial(String name) {
         IRubyObject value;
         if (this == getRuntime().getObject()) {
             value = getConstantNoConstMissing(name);
         } else {
             value = fetchConstant(name);
         }
         
         return value == UNDEF ? resolveUndefConstant(getRuntime(), name) : value;
     }
 
     public IRubyObject getConstantAt(String name) {
         return getConstantAt(name, true);
     }
     
     public IRubyObject getConstantAt(String name, boolean includePrivate) {
         IRubyObject value = fetchConstant(name, includePrivate);
 
         return value == UNDEF ? resolveUndefConstant(getRuntime(), name) : value;
     }
 
     @Deprecated
     public IRubyObject fastGetConstantAt(String internedName) {
         return getConstantAt(internedName);
     }
 
     /**
      * Retrieve the named constant, invoking 'const_missing' should that be appropriate.
      * 
      * @param name The constant to retrieve
      * @return The value for the constant, or null if not found
      */
     public IRubyObject getConstant(String name) {
         return getConstant(name, true);
     }
 
     public IRubyObject getConstant(String name, boolean inherit) {
         return getConstant(name, inherit, true);
     }
 
     public IRubyObject getConstant(String name, boolean inherit, boolean includeObject) {
         IRubyObject value = getConstantNoConstMissing(name, inherit, includeObject);
         Ruby runtime = getRuntime();
 
         return value == null ? callMethod(runtime.getCurrentContext(), "const_missing",
                 runtime.newSymbol(name)) : value;
     }
 
     @Deprecated
     public IRubyObject fastGetConstant(String internedName) {
         return getConstant(internedName);
     }
 
     @Deprecated
     public IRubyObject fastGetConstant(String internedName, boolean inherit) {
         return getConstant(internedName, inherit);
     }
 
     public IRubyObject getConstantNoConstMissing(String name) {
         return getConstantNoConstMissing(name, true);
     }
 
     public IRubyObject getConstantNoConstMissing(String name, boolean inherit) {
         return getConstantNoConstMissing(name, inherit, true);
     }
 
     public IRubyObject getConstantNoConstMissing(String name, boolean inherit, boolean includeObject) {
         assert IdUtil.isConstant(name);
 
         IRubyObject constant = iterateConstantNoConstMissing(name, this, inherit);
 
         if (constant == null && !isClass() && includeObject) {
             constant = iterateConstantNoConstMissing(name, getRuntime().getObject(), inherit);
         }
 
         return constant;
     }
 
     private IRubyObject iterateConstantNoConstMissing(String name, RubyModule init, boolean inherit) {
         for (RubyModule p = init; p != null; p = p.getSuperClass()) {
             IRubyObject value = p.getConstantAt(name);
 
             if (value != null) return value == UNDEF ? null : value;
             if (!inherit) break;
         }
         return null;
     }
 
     // not actually called anywhere (all known uses call the fast version)
     public IRubyObject getConstantFrom(String name) {
         IRubyObject value = getConstantFromNoConstMissing(name);
 
         return value != null ? value : getConstantFromConstMissing(name);
     }
     
     @Deprecated
     public IRubyObject fastGetConstantFrom(String internedName) {
         return getConstantFrom(internedName);
     }
 
     public IRubyObject getConstantFromNoConstMissing(String name) {
         return getConstantFromNoConstMissing(name, true);
     }
 
     public IRubyObject getConstantFromNoConstMissing(String name, boolean includePrivate) {
         assert name == name.intern() : name + " is not interned";
         assert IdUtil.isConstant(name);
         Ruby runtime = getRuntime();
         RubyClass objectClass = runtime.getObject();
         IRubyObject value;
 
         RubyModule p = this;
 
         while (p != null) {
             if ((value = p.fetchConstant(name, false)) != null) {
                 if (value == UNDEF) {
                     return p.resolveUndefConstant(runtime, name);
                 }
 
                 if (p == objectClass && this != objectClass) {
                     String badCName = getName() + "::" + name;
                     runtime.getWarnings().warn(ID.CONSTANT_BAD_REFERENCE, "toplevel constant " +
                             name + " referenced by " + badCName);
                 }
 
                 return value;
             }
             p = p.getSuperClass();
         }
         return null;
     }
     
     @Deprecated
     public IRubyObject fastGetConstantFromNoConstMissing(String internedName) {
         return getConstantFromNoConstMissing(internedName);
     }
 
     public IRubyObject getConstantFromConstMissing(String name) {
         return callMethod(getRuntime().getCurrentContext(),
                 "const_missing", getRuntime().fastNewSymbol(name));
     }
     
     @Deprecated
     public IRubyObject fastGetConstantFromConstMissing(String internedName) {
         return getConstantFromConstMissing(internedName);
     }
     
     public IRubyObject resolveUndefConstant(Ruby runtime, String name) {
         return getAutoloadConstant(runtime, name);
     }
 
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name. This version does not
      * warn if the constant has already been set.
      *
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      */
     public IRubyObject setConstantQuiet(String name, IRubyObject value) {
         return setConstantCommon(name, value, false);
     }
 
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name.
      *
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      */
     public IRubyObject setConstant(String name, IRubyObject value) {
         return setConstantCommon(name, value, true);
     }
 
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name.
      *
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      */
     private IRubyObject setConstantCommon(String name, IRubyObject value, boolean warn) {
         IRubyObject oldValue = fetchConstant(name);
         if (oldValue != null) {
             if (oldValue == UNDEF) {
                 setAutoloadConstant(name, value);
             } else {
                 if (warn) {
                     getRuntime().getWarnings().warn(ID.CONSTANT_ALREADY_INITIALIZED, "already initialized constant " + name);
                 }
                 storeConstant(name, value);
             }
         } else {
             storeConstant(name, value);
         }
 
         invalidateConstantCache();
         
         // if adding a module under a constant name, set that module's basename to the constant name
         if (value instanceof RubyModule) {
             RubyModule module = (RubyModule)value;
             if (module != this && module.getBaseName() == null) {
                 module.setBaseName(name);
                 module.setParent(this);
             }
         }
         return value;
     }
 
     @Deprecated
     public IRubyObject fastSetConstant(String internedName, IRubyObject value) {
         return setConstant(internedName, value);
     }
     
     /** rb_define_const
      *
      */
     public void defineConstant(String name, IRubyObject value) {
         assert value != null;
 
         if (!IdUtil.isValidConstantName(name)) {
             throw getRuntime().newNameError("bad constant name " + name, name);
         }
 
         setConstant(name, value);
     }
 
     // Fix for JRUBY-1339 - search hierarchy for constant
     /** rb_const_defined_at
      * 
      */
     public boolean isConstantDefined(String name) {
         assert IdUtil.isConstant(name);
         boolean isObject = this == getRuntime().getObject();
 
         RubyModule module = this;
 
         do {
             Object value;
             if ((value = module.constantTableFetch(name)) != null) {
                 if (value != UNDEF) return true;
                 return getAutoloadMap().get(name) != null;
             }
 
         } while (isObject && (module = module.getSuperClass()) != null );
 
         return false;
     }
 
     public boolean fastIsConstantDefined(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         assert IdUtil.isConstant(internedName);
         boolean isObject = this == getRuntime().getObject();
 
         RubyModule module = this;
 
         do {
             Object value;
             if ((value = module.constantTableFetch(internedName)) != null) {
                 if (value != UNDEF) return true;
                 return getAutoloadMap().get(internedName) != null;
             }
 
         } while (isObject && (module = module.getSuperClass()) != null );
 
         return false;
     }
 
     public boolean fastIsConstantDefined19(String internedName) {
         return fastIsConstantDefined19(internedName, true);
     }
 
     public boolean fastIsConstantDefined19(String internedName, boolean inherit) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         assert IdUtil.isConstant(internedName);
 
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             Object value;
             if ((value = module.constantTableFetch(internedName)) != null) {
                 if (value != UNDEF) return true;
                 return getAutoloadMap().get(internedName) != null;
             }
             if (!inherit) {
                 break;
             }
         }
 
         return false;
     }
 
     //
     ////////////////// COMMON CONSTANT / CVAR METHODS ////////////////
     //
 
     private RaiseException cannotRemoveError(String id) {
         return getRuntime().newNameError("cannot remove " + id + " for " + getName(), id);
     }
 
 
     //
     ////////////////// INTERNAL MODULE VARIABLE API METHODS ////////////////
     //
     
     /**
      * Behaves similarly to {@link #getClassVar(String)}. Searches this
      * class/module <em>and its ancestors</em> for the specified internal
      * variable.
      * 
      * @param name the internal variable name
      * @return the value of the specified internal variable if found, else null
      * @see #setInternalModuleVariable(String, IRubyObject)
      */
     public boolean hasInternalModuleVariable(final String name) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.hasInternalVariable(name)) return true;
         }
 
         return false;
     }
     /**
      * Behaves similarly to {@link #getClassVar(String)}. Searches this
      * class/module <em>and its ancestors</em> for the specified internal
      * variable.
      * 
      * @param name the internal variable name
      * @return the value of the specified internal variable if found, else null
      * @see #setInternalModuleVariable(String, IRubyObject)
      */
     public IRubyObject searchInternalModuleVariable(final String name) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             IRubyObject value = (IRubyObject)module.getInternalVariable(name);
             if (value != null) return value;
         }
 
         return null;
     }
 
     /**
      * Behaves similarly to {@link #setClassVar(String, IRubyObject)}. If the
      * specified internal variable is found in this class/module <em>or an ancestor</em>,
      * it is set where found.  Otherwise it is set in this module. 
      * 
      * @param name the internal variable name
      * @param value the internal variable value
      * @see #searchInternalModuleVariable(String)
      */
     public void setInternalModuleVariable(final String name, final IRubyObject value) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.hasInternalVariable(name)) {
                 module.setInternalVariable(name, value);
                 return;
             }
         }
 
         setInternalVariable(name, value);
     }
 
     //
     ////////////////// LOW-LEVEL CLASS VARIABLE INTERFACE ////////////////
     //
     // fetch/store/list class variables for this module
     //
 
     protected Map<String, IRubyObject> getClassVariables() {
         if (CLASSVARS_UPDATER == null) {
             return getClassVariablesForWriteSynchronized();
         } else {
             return getClassVariablesForWriteAtomic();
         }
     }
 
     /**
      * Get the class variables for write. If it is not set or not of the right size,
      * synchronize against the object and prepare it accordingly.
      *
      * @return the class vars map, ready for assignment
      */
     private Map<String,IRubyObject> getClassVariablesForWriteSynchronized() {
         Map myClassVars = classVariables;
         if (myClassVars == Collections.EMPTY_MAP) {
             synchronized (this) {
                 myClassVars = classVariables;
 
                 if (myClassVars == Collections.EMPTY_MAP) {
                     return classVariables = new ConcurrentHashMap<String, IRubyObject>(4, 0.75f, 2);
                 } else {
                     return myClassVars;
                 }
             }
         }
 
         return myClassVars;
     }
 
 
     /**
      * Get the class variables for write. If it is not set or not of the right size,
      * atomically update it with an appropriate value.
      *
      * @return the class vars map, ready for assignment
      */
     private Map<String,IRubyObject> getClassVariablesForWriteAtomic() {
         while (true) {
             Map myClassVars = classVariables;
             Map newClassVars;
 
             if (myClassVars == Collections.EMPTY_MAP) {
                 newClassVars = new ConcurrentHashMap<String, IRubyObject>(4, 0.75f, 2);
             } else {
                 return myClassVars;
             }
 
             // proceed with atomic update of table, or retry
             if (CLASSVARS_UPDATER.compareAndSet(this, myClassVars, newClassVars)) {
                 return newClassVars;
             }
         }
     }
 
     protected Map<String, IRubyObject> getClassVariablesForRead() {
         return classVariables;
     }
     
     public boolean hasClassVariable(String name) {
         assert IdUtil.isClassVariable(name);
         return getClassVariablesForRead().containsKey(name);
     }
 
     @Deprecated
     public boolean fastHasClassVariable(String internedName) {
         return hasClassVariable(internedName);
     }
 
     public IRubyObject fetchClassVariable(String name) {
         assert IdUtil.isClassVariable(name);
         return getClassVariablesForRead().get(name);
     }
 
     @Deprecated
     public IRubyObject fastFetchClassVariable(String internedName) {
         return fetchClassVariable(internedName);
     }
 
     public IRubyObject storeClassVariable(String name, IRubyObject value) {
         assert IdUtil.isClassVariable(name) && value != null;
         ensureClassVariablesSettable();
         getClassVariables().put(name, value);
         return value;
     }
 
     @Deprecated
     public IRubyObject fastStoreClassVariable(String internedName, IRubyObject value) {
         return storeClassVariable(internedName, value);
     }
 
     public IRubyObject deleteClassVariable(String name) {
         assert IdUtil.isClassVariable(name);
         ensureClassVariablesSettable();
         return getClassVariablesForRead().remove(name);
     }
 
     public List<String> getClassVariableNameList() {
         return new ArrayList<String>(getClassVariablesForRead().keySet());
     }
 
     protected static final String ERR_INSECURE_SET_CLASS_VAR = "Insecure: can't modify class variable";
     protected static final String ERR_FROZEN_CVAR_TYPE = "class/module ";
    
     protected final String validateClassVariable(String name) {
         if (IdUtil.isValidClassVariableName(name)) {
             return name;
         }
         throw getRuntime().newNameError("`" + name + "' is not allowed as a class variable name", name);
     }
 
     protected final void ensureClassVariablesSettable() {
         Ruby runtime = getRuntime();
         
         if (!isFrozen()) {
             return;
         }
 
         if (this instanceof RubyModule) {
             throw runtime.newFrozenError(ERR_FROZEN_CONST_TYPE);
         } else {
             throw runtime.newFrozenError("");
         }
     }
 
     //
     ////////////////// LOW-LEVEL CONSTANT INTERFACE ////////////////
     //
     // fetch/store/list constants for this module
     //
 
     public boolean hasConstant(String name) {
         assert IdUtil.isConstant(name);
         return constantTableContains(name);
     }
 
     @Deprecated
     public boolean fastHasConstant(String internedName) {
         return hasConstant(internedName);
     }
 
     // returns the stored value without processing undefs (autoloads)
     public IRubyObject fetchConstant(String name) {
         return fetchConstant(name, true);
     }
 
     public IRubyObject fetchConstant(String name, boolean includePrivate) {
         assert IdUtil.isConstant(name);
         ConstantEntry entry = constantEntryFetch(name);
 
         if (entry == null) return null;
 
         if (entry.hidden && !includePrivate) {
             throw getRuntime().newNameError("private constant " + getName() + "::" + name + " referenced", name);
         }
 
         return entry.value;
     }
 
     @Deprecated
     public IRubyObject fastFetchConstant(String internedName) {
         return fetchConstant(internedName);
     }
 
     public IRubyObject storeConstant(String name, IRubyObject value) {
         assert IdUtil.isConstant(name) && value != null;
         ensureConstantsSettable();
         return constantTableStore(name, value);
     }
 
     @Deprecated
     public IRubyObject fastStoreConstant(String internedName, IRubyObject value) {
         return storeConstant(internedName, value);
     }
 
     // removes and returns the stored value without processing undefs (autoloads)
     public IRubyObject deleteConstant(String name) {
         assert IdUtil.isConstant(name);
         ensureConstantsSettable();
         return constantTableRemove(name);
     }
     
     @Deprecated
     public List<Variable<IRubyObject>> getStoredConstantList() {
         return null;
     }
 
     @Deprecated
     public List<String> getStoredConstantNameList() {
         return new ArrayList<String>(getConstantMap().keySet());
     }
 
     /**
      * @return a list of constant names that exists at time this was called
      */
     public Collection<String> getConstantNames() {
         return getConstantMap().keySet();
     }
 
     public Collection<String> getConstantNames(boolean includePrivate) {
         if (includePrivate) return getConstantNames();
 
         if (getConstantMap().size() == 0) {
             return Collections.EMPTY_SET;
         }
 
         HashSet<String> publicNames = new HashSet<String>(getConstantMap().size());
         
         for (Map.Entry<String, ConstantEntry> entry : getConstantMap().entrySet()) {
             if (entry.getValue().hidden) continue;
             publicNames.add(entry.getKey());
         }
         return publicNames;
     }
    
     protected final String validateConstant(String name) {
         if (getRuntime().is1_9() ?
                 IdUtil.isValidConstantName19(name) :
                 IdUtil.isValidConstantName(name)) {
             return name;
         }
         throw getRuntime().newNameError("wrong constant name " + name, name);
     }
 
     protected final void ensureConstantsSettable() {
         if (isFrozen()) throw getRuntime().newFrozenError(ERR_FROZEN_CONST_TYPE);
     }
 
     protected boolean constantTableContains(String name) {
         return getConstantMap().containsKey(name);
     }
     
     protected IRubyObject constantTableFetch(String name) {
         ConstantEntry entry = getConstantMap().get(name);
         if (entry == null) return null;
         return entry.value;
     }
 
     protected ConstantEntry constantEntryFetch(String name) {
         return getConstantMap().get(name);
     }
     
     protected IRubyObject constantTableStore(String name, IRubyObject value) {
         Map<String, ConstantEntry> constMap = getConstantMapForWrite();
         boolean hidden = false;
 
         ConstantEntry entry = constMap.get(name);
         if (entry != null) hidden = entry.hidden;
 
         constMap.put(name, new ConstantEntry(value, hidden));
         return value;
     }
     
     protected IRubyObject constantTableRemove(String name) {
         ConstantEntry entry = getConstantMapForWrite().remove(name);
         if (entry == null) return null;
         return entry.value;
     }
     
     /**
      * Define an autoload. ConstantMap holds UNDEF for the name as an autoload marker.
      */
     protected void defineAutoload(String name, IAutoloadMethod loadMethod) {
         Autoload existingAutoload = getAutoloadMap().get(name);
         if (existingAutoload == null || existingAutoload.getValue() == null) {
             storeConstant(name, RubyObject.UNDEF);
             getAutoloadMapForWrite().put(name, new Autoload(loadMethod));
         }
     }
     
     /**
      * Extract an Object which is defined by autoload thread from autoloadMap and define it as a constant.
      */
     protected IRubyObject finishAutoload(String name) {
         Autoload autoload = getAutoloadMap().get(name);
         if (autoload != null) {
             IRubyObject value = autoload.getValue();
             if (value != null) {
                 storeConstant(name, value);
             }
             removeAutoload(name);
             return value;
         }
         return null;
     }
     
     /**
      * Get autoload constant.
      * If it's first resolution for the constant, it tries to require the defined feature and returns the defined value.
      * Multi-threaded accesses are blocked and processed sequentially except if the caller is the autoloading thread.
      */
     public IRubyObject getAutoloadConstant(Ruby runtime, String name) {
         Autoload autoload = getAutoloadMap().get(name);
         if (autoload == null) {
             return null;
         }
         return autoload.getConstant(runtime.getCurrentContext());
     }
     
     /**
      * Set an Object as a defined constant in autoloading.
      */
     private void setAutoloadConstant(String name, IRubyObject value) {
         Autoload autoload = getAutoloadMap().get(name);
         if (autoload != null) {
             if (!autoload.setConstant(getRuntime().getCurrentContext(), value)) {
                 storeConstant(name, value);
                 removeAutoload(name);
             }
         } else {
             storeConstant(name, value);
         }
     }
     
     /**
      * Removes an Autoload object from autoloadMap. ConstantMap must be updated before calling this.
      */
     private void removeAutoload(String name) {
         getAutoloadMapForWrite().remove(name);
     }
     
     protected String getAutoloadFile(String name) {
         Autoload autoload = getAutoloadMap().get(name);
         if (autoload != null) {
             return autoload.getFile();
         }
         return null;
     }
 
     private static void define(RubyModule module, JavaMethodDescriptor desc, DynamicMethod dynamicMethod) {
         JRubyMethod jrubyMethod = desc.anno;
         if (jrubyMethod.frame()) {
             for (String name : jrubyMethod.name()) {
                 ASTInspector.FRAME_AWARE_METHODS.add(name);
             }
         }
         if(jrubyMethod.compat() == BOTH ||
                 module.getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             RubyModule singletonClass;
 
             if (jrubyMethod.meta()) {
                 singletonClass = module.getSingletonClass();
                 dynamicMethod.setImplementationClass(singletonClass);
 
                 String baseName;
                 if (jrubyMethod.name().length == 0) {
                     baseName = desc.name;
                     singletonClass.addMethod(baseName, dynamicMethod);
                 } else {
                     baseName = jrubyMethod.name()[0];
                     for (String name : jrubyMethod.name()) {
                         singletonClass.addMethod(name, dynamicMethod);
                     }
                 }
 
                 if (jrubyMethod.alias().length > 0) {
                     for (String alias : jrubyMethod.alias()) {
                         singletonClass.defineAlias(alias, baseName);
                     }
                 }
             } else {
                 String baseName;
                 if (jrubyMethod.name().length == 0) {
                     baseName = desc.name;
                     module.addMethod(baseName, dynamicMethod);
                 } else {
                     baseName = jrubyMethod.name()[0];
                     for (String name : jrubyMethod.name()) {
                         module.addMethod(name, dynamicMethod);
                     }
                 }
 
                 if (jrubyMethod.alias().length > 0) {
                     for (String alias : jrubyMethod.alias()) {
                         module.defineAlias(alias, baseName);
                     }
                 }
 
                 if (jrubyMethod.module()) {
                     singletonClass = module.getSingletonClass();
                     // module/singleton methods are all defined public
                     DynamicMethod moduleMethod = dynamicMethod.dup();
                     moduleMethod.setVisibility(PUBLIC);
 
                     if (jrubyMethod.name().length == 0) {
                         baseName = desc.name;
                         singletonClass.addMethod(desc.name, moduleMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             singletonClass.addMethod(name, moduleMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             singletonClass.defineAlias(alias, baseName);
                         }
                     }
                 }
             }
         }
     }
     
     @Deprecated
     public IRubyObject initialize(Block block) {
         return initialize(getRuntime().getCurrentContext());
     }
 
     public KindOf kindOf = KindOf.DEFAULT_KIND_OF;
 
     public final int id;
 
     /**
      * The class/module within whose namespace this class/module resides.
      */
     public RubyModule parent;
 
     /**
      * The base name of this class/module, excluding nesting. If null, this is
      * an anonymous class.
      */
     protected String baseName;
     
     /**
      * The cached anonymous class name, since it never changes and has a nonzero
      * cost to calculate.
      */
     private String anonymousName;
 
     /**
      * The cached name, only cached once this class and all containing classes are non-anonymous
      */
     private String cachedName;
 
     private volatile Map<String, ConstantEntry> constants = Collections.EMPTY_MAP;
 
     /**
      * Represents a constant value, possibly hidden (private).
      */
     public static class ConstantEntry {
         public final IRubyObject value;
         public final boolean hidden;
 
         public ConstantEntry(IRubyObject value, boolean hidden) {
             this.value = value;
             this.hidden = hidden;
         }
         
         public ConstantEntry dup() {
             return new ConstantEntry(value, hidden);
         }
     }
     
     /**
      * Objects for holding autoload state for the defined constant.
      * 
      * 'Module#autoload' creates this object and stores it in autoloadMap.
      * This object can be shared with multiple threads so take care to change volatile and synchronized definitions.
      */
     private class Autoload {
         // A ThreadContext which is executing autoload.
         private volatile ThreadContext ctx;
         // The lock for test-and-set the ctx.
         private final Object ctxLock = new Object();
         // An object defined for the constant while autoloading.
         private volatile IRubyObject value;
         // A method which actually requires a defined feature.
         private final IAutoloadMethod loadMethod;
 
         Autoload(IAutoloadMethod loadMethod) {
             this.ctx = null;
             this.value = null;
             this.loadMethod = loadMethod;
         }
 
         // Returns an object for the constant if the caller is the autoloading thread.
         // Otherwise, try to start autoloading and returns the defined object by autoload.
         IRubyObject getConstant(ThreadContext ctx) {
             synchronized (ctxLock) {
                 if (this.ctx == null) {
                     this.ctx = ctx;
                 } else if (isSelf(ctx)) {
                     return getValue();
                 }
                 // This method needs to be synchronized for removing Autoload
                 // from autoloadMap when it's loaded. 
                 getLoadMethod().load(ctx.runtime);
             }
             return getValue();
         }
         
         // Update an object for the constant if the caller is the autoloading thread.
         boolean setConstant(ThreadContext ctx, IRubyObject value) {
             synchronized(ctxLock) {
                 if (this.ctx == null) {
                     return false;
                 } else if (isSelf(ctx)) {
                     this.value = value;
                     return true;
                 }
                 return false;
             }
         }
         
         // Returns an object for the constant defined by autoload.
         IRubyObject getValue() {
             return value;
         }
         
         // Returns the assigned feature.
         String getFile() {
             return getLoadMethod().file();
         }
 
         private IAutoloadMethod getLoadMethod() {
             return loadMethod;
         }
 
         private boolean isSelf(ThreadContext rhs) {
             return ctx != null && ctx.getThread() == rhs.getThread();
         }
     }
     
     /**
      * Set whether this class is associated with (i.e. a proxy for) a normal
      * Java class or interface.
      */
     public void setJavaProxy(boolean javaProxy) {
         this.javaProxy = javaProxy;
     }
     
     /**
      * Get whether this class is associated with (i.e. a proxy for) a normal
      * Java class or interface.
      */
     public boolean getJavaProxy() {
         return javaProxy;
     }
 
     /**
      * Get whether this Java proxy class should try to keep its instances idempotent
      * and alive using the ObjectProxyCache.
      */
     public boolean getCacheProxy() {
         return getFlag(USER0_F);
     }
 
     /**
      * Set whether this Java proxy class should try to keep its instances idempotent
      * and alive using the ObjectProxyCache.
      */
     public void setCacheProxy(boolean cacheProxy) {
         setFlag(USER0_F, cacheProxy);
     }
+
+    @Deprecated
+    public void checkMethodBound(ThreadContext context, IRubyObject[] args, Visibility visibility) {
+    }
     
     private volatile Map<String, Autoload> autoloads = Collections.EMPTY_MAP;
     private volatile Map<String, DynamicMethod> methods = Collections.EMPTY_MAP;
     protected Map<String, CacheEntry> cachedMethods = Collections.EMPTY_MAP;
     protected int generation;
     protected Integer generationObject;
 
     protected volatile Set<RubyClass> includingHierarchies = Collections.EMPTY_SET;
 
     // ClassProviders return Java class/module (in #defineOrGetClassUnder and
     // #defineOrGetModuleUnder) when class/module is opened using colon syntax.
     private transient volatile Set<ClassProvider> classProviders = Collections.EMPTY_SET;
 
     // superClass may be null.
     protected RubyClass superClass;
 
     public int index;
 
     private volatile Map<String, IRubyObject> classVariables = Collections.EMPTY_MAP;
 
     private static final AtomicReferenceFieldUpdater CLASSVARS_UPDATER;
 
     static {
         AtomicReferenceFieldUpdater updater = null;
         try {
             updater = AtomicReferenceFieldUpdater.newUpdater(RubyModule.class, Map.class, "classVariables");
         } catch (RuntimeException re) {
             if (re.getCause() instanceof AccessControlException) {
                 // security prevented creation; fall back on synchronized assignment
             } else {
                 throw re;
             }
         }
         CLASSVARS_UPDATER = updater;
     }
     
     // Invalidator used for method caches
     protected final Invalidator methodInvalidator;
     
     /** Whether this class proxies a normal Java class */
     private boolean javaProxy = false;
 }
