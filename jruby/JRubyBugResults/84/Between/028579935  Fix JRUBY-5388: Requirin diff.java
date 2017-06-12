diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index e6936b0f8a..4dbb335351 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -81,2001 +81,2001 @@ import org.jruby.util.TypeConverter;
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
             runtime.getGlobalVariables().get("$stderr").callMethod(context,"puts",args[0]);
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
 
-        runtime.getLoadService().load(file.getByteList().toString(), wrap);
+        runtime.getLoadService().load(file.toString(), wrap);
 
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
 
     @JRubyMethod(name = {"object_id", "__id__"})
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
 
     @JRubyMethod(name = "type")
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
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index ef5d2252c4..ca4d0fd509 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -1,1645 +1,1664 @@
 /*
  **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
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
 import static org.jruby.anno.FrameField.BACKREF;
 import static org.jruby.util.StringSupport.CR_7BIT;
 import static org.jruby.util.StringSupport.CR_BROKEN;
 import static org.jruby.util.StringSupport.CR_MASK;
 import static org.jruby.util.StringSupport.CR_UNKNOWN;
 import static org.jruby.util.StringSupport.CR_VALID;
 import static org.jruby.util.StringSupport.codeLength;
 import static org.jruby.util.StringSupport.codePoint;
 import static org.jruby.util.StringSupport.codeRangeScan;
 import static org.jruby.util.StringSupport.searchNonAscii;
 import static org.jruby.util.StringSupport.strLengthWithCodeRange;
 import static org.jruby.util.StringSupport.toLower;
 import static org.jruby.util.StringSupport.toUpper;
 import static org.jruby.util.StringSupport.unpackArg;
 import static org.jruby.util.StringSupport.unpackResult;
 
 import java.io.UnsupportedEncodingException;
 import java.nio.ByteBuffer;
 import java.nio.charset.CharacterCodingException;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetEncoder;
 import java.nio.charset.CodingErrorAction;
 import java.util.Arrays;
 import java.util.Locale;
 
 import org.jcodings.Encoding;
 import org.jcodings.EncodingDB.Entry;
 import org.jcodings.ascii.AsciiTables;
 import org.jcodings.constants.CharacterType;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jcodings.specific.UTF8Encoding;
 import org.jcodings.util.IntHash;
 import org.joni.Matcher;
 import org.joni.Option;
 import org.joni.Regex;
 import org.joni.Region;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.cext.RString;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.ConvertBytes;
 import org.jruby.util.Numeric;
 import org.jruby.util.Pack;
 import org.jruby.util.Sprintf;
 import org.jruby.util.StringSupport;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.string.JavaCrypt;
 
 /**
  * Implementation of Ruby String class
  * 
  * Concurrency: no synchronization is required among readers, but
  * all users must synchronize externally with writers.
  *
  */
 @JRubyClass(name="String", include={"Enumerable", "Comparable"})
 public class RubyString extends RubyObject implements EncodingCapable {
     private static final ASCIIEncoding ASCII = ASCIIEncoding.INSTANCE;
     private static final UTF8Encoding UTF8 = UTF8Encoding.INSTANCE;
     private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
 
     // string doesn't share any resources
     private static final int SHARE_LEVEL_NONE = 0;
     // string has it's own ByteList, but it's pointing to a shared buffer (byte[])
     private static final int SHARE_LEVEL_BUFFER = 1;
     // string doesn't have it's own ByteList (values)
     private static final int SHARE_LEVEL_BYTELIST = 2;
 
     private volatile int shareLevel = SHARE_LEVEL_NONE;
 
     private ByteList value;
 
     private RString rstring;
 
     public static RubyClass createStringClass(Ruby runtime) {
         RubyClass stringClass = runtime.defineClass("String", runtime.getObject(), STRING_ALLOCATOR);
         runtime.setString(stringClass);
         stringClass.index = ClassIndex.STRING;
         stringClass.setReifiedClass(RubyString.class);
         stringClass.kindOf = new RubyModule.KindOf() {
             @Override
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyString;
                 }
             };
 
         stringClass.includeModule(runtime.getComparable());
         if (!runtime.is1_9()) stringClass.includeModule(runtime.getEnumerable());
         stringClass.defineAnnotatedMethods(RubyString.class);
 
         return stringClass;
     }
 
     private static ObjectAllocator STRING_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return RubyString.newEmptyString(runtime, klass);
         }
     };
 
     public Encoding getEncoding() {
         return value.getEncoding();
     }
 
     public void setEncoding(Encoding encoding) {
         value.setEncoding(encoding);
     }
 
     public void associateEncoding(Encoding enc) {
         if (value.getEncoding() != enc) {
             if (!isCodeRangeAsciiOnly() || !enc.isAsciiCompatible()) clearCodeRange();
             value.setEncoding(enc);
         }
     }
 
     public final void setEncodingAndCodeRange(Encoding enc, int cr) {
         value.setEncoding(enc);
         setCodeRange(cr);
     }
 
     public final Encoding toEncoding(Ruby runtime) {
         return runtime.getEncodingService().findEncoding(this);
     }
 
     public final int getCodeRange() {
         return flags & CR_MASK;
     }
 
     public final void setCodeRange(int codeRange) {
         flags |= codeRange & CR_MASK;
     }
 
     public final RString getRString() {
         return rstring;
     }
 
     public final void setRString(RString rstring) {
         this.rstring = rstring;
     }
 
     public final void clearCodeRange() {
         flags &= ~CR_MASK;
     }
 
     private void keepCodeRange() {
         if (getCodeRange() == CR_BROKEN) clearCodeRange();
     }
 
     // ENC_CODERANGE_ASCIIONLY
     public final boolean isCodeRangeAsciiOnly() {
         return getCodeRange() == CR_7BIT;
     }
 
     // rb_enc_str_asciionly_p
     public final boolean isAsciiOnly() {
         return value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT;
     }
 
     public final boolean isCodeRangeValid() {
         return (flags & CR_VALID) != 0;
     }
 
     public final boolean isCodeRangeBroken() {
         return (flags & CR_BROKEN) != 0;
     }
 
     static int codeRangeAnd(int cr1, int cr2) {
         if (cr1 == CR_7BIT) return cr2;
         if (cr1 == CR_VALID) return cr2 == CR_7BIT ? CR_VALID : cr2;
         return CR_UNKNOWN;
     }
 
     private void copyCodeRangeForSubstr(RubyString from, Encoding enc) {
         int fromCr = from.getCodeRange();
         if (fromCr == CR_7BIT) {
             setCodeRange(fromCr);
         } else if (fromCr == CR_VALID) {
             if (!enc.isAsciiCompatible() || searchNonAscii(value) != -1) {
                 setCodeRange(CR_VALID);
             } else {
                 setCodeRange(CR_7BIT);
             }
         } else{ 
             if (value.getRealSize() == 0) {
                 setCodeRange(!enc.isAsciiCompatible() ? CR_VALID : CR_7BIT);
             }
         }
     }
 
     private void copyCodeRange(RubyString from) {
         value.setEncoding(from.value.getEncoding());
         setCodeRange(from.getCodeRange());
     }
 
     // rb_enc_str_coderange
     final int scanForCodeRange() {
         int cr = getCodeRange();
         if (cr == CR_UNKNOWN) {
             cr = codeRangeScan(value.getEncoding(), value);
             setCodeRange(cr);
         }
         return cr;
     }
 
     final boolean singleByteOptimizable() {
         return getCodeRange() == CR_7BIT || value.getEncoding().isSingleByte();
     }
 
     final boolean singleByteOptimizable(Encoding enc) {
         return getCodeRange() == CR_7BIT || enc.isSingleByte();
     }
 
     private Encoding isCompatibleWith(RubyString other) { 
         Encoding enc1 = value.getEncoding();
         Encoding enc2 = other.value.getEncoding();
 
         if (enc1 == enc2) return enc1;
 
         if (other.value.getRealSize() == 0) return enc1;
         if (value.getRealSize() == 0) return enc2;
 
         if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
 
         return RubyEncoding.areCompatible(enc1, scanForCodeRange(), enc2, other.scanForCodeRange());
     }
 
     final Encoding isCompatibleWith(EncodingCapable other) {
         if (other instanceof RubyString) return checkEncoding((RubyString)other);
         Encoding enc1 = value.getEncoding();
         Encoding enc2 = other.getEncoding();
 
         if (enc1 == enc2) return enc1;
         if (value.getRealSize() == 0) return enc2;
         if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
         if (enc2 instanceof USASCIIEncoding) return enc1;
         if (scanForCodeRange() == CR_7BIT) return enc2;
         return null;
     }
 
     final Encoding checkEncoding(RubyString other) {
         Encoding enc = isCompatibleWith(other);
         if (enc == null) throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + 
                                 value.getEncoding() + " and " + other.value.getEncoding());
         return enc;
     }
 
     final Encoding checkEncoding(EncodingCapable other) {
         Encoding enc = isCompatibleWith(other);
         if (enc == null) throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + 
                                 value.getEncoding() + " and " + other.getEncoding());
         return enc;
     }
 
     private Encoding checkDummyEncoding() {
         Encoding enc = value.getEncoding();
         if (enc.isDummy()) throw getRuntime().newEncodingCompatibilityError(
                 "incompatible encoding with this operation: " + enc);
         return enc;
     }
 
     private boolean isComparableWith(RubyString other) {
         ByteList otherValue = other.value;
         if (value.getEncoding() == otherValue.getEncoding() ||
             value.getRealSize() == 0 || otherValue.getRealSize() == 0) return true;
         return isComparableViaCodeRangeWith(other);
     }
 
     private boolean isComparableViaCodeRangeWith(RubyString other) {
         int cr1 = scanForCodeRange();
         int cr2 = other.scanForCodeRange();
 
         if (cr1 == CR_7BIT && (cr2 == CR_7BIT || other.value.getEncoding().isAsciiCompatible())) return true;
         if (cr2 == CR_7BIT && value.getEncoding().isAsciiCompatible()) return true;
         return false;
     }
 
     private int strLength(Encoding enc) {
         if (singleByteOptimizable(enc)) return value.getRealSize();
         return strLength(value, enc);
     }
 
     final int strLength() {
         if (singleByteOptimizable()) return value.getRealSize();
         return strLength(value);
     }
 
     private int strLength(ByteList bytes) {
         return strLength(bytes, bytes.getEncoding());
     }
 
     private int strLength(ByteList bytes, Encoding enc) {
         if (isCodeRangeValid() && enc instanceof UTF8Encoding) return StringSupport.utf8Length(value);
 
         long lencr = strLengthWithCodeRange(bytes, enc);
         int cr = unpackArg(lencr);
         if (cr != 0) setCodeRange(cr);
         return unpackResult(lencr);
     }
 
     final int subLength(int pos) {
         if (singleByteOptimizable() || pos < 0) return pos;
         return StringSupport.strLength(value.getEncoding(), value.getUnsafeBytes(), value.getBegin(), value.getBegin() + pos);
     }
 
     /** short circuit for String key comparison
      * 
      */
     @Override
     public final boolean eql(IRubyObject other) {
         Ruby runtime = getRuntime();
         if (getMetaClass() != runtime.getString() || getMetaClass() != other.getMetaClass()) return super.eql(other);
         return runtime.is1_9() ? eql19(runtime, other) : eql18(runtime, other);
     }
 
     private boolean eql18(Ruby runtime, IRubyObject other) {
         return value.equal(((RubyString)other).value);
     }
 
     // rb_str_hash_cmp
     private boolean eql19(Ruby runtime, IRubyObject other) {
         RubyString otherString = (RubyString)other;
         return isComparableWith(otherString) && value.equal(((RubyString)other).value);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass) {
         this(runtime, rubyClass, EMPTY_BYTE_ARRAY);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, CharSequence value) {
         super(runtime, rubyClass);
         assert value != null;
         byte[] bytes = RubyEncoding.encodeUTF8(value);
         this.value = new ByteList(bytes, false);
         this.value.setEncoding(UTF8);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, byte[] value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = new ByteList(value);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = value;
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, boolean objectSpace) {
         super(runtime, rubyClass, objectSpace);
         assert value != null;
         this.value = value;
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding enc, int cr) {
         this(runtime, rubyClass, value);
         value.setEncoding(enc);
         flags |= cr;
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding enc) {
         this(runtime, rubyClass, value);
         value.setEncoding(enc);
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, int cr) {
         this(runtime, rubyClass, value);
         flags |= cr;
     }
 
     // Deprecated String construction routines
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *  @deprecated  
      */
     @Deprecated
     public RubyString newString(CharSequence s) {
         return new RubyString(getRuntime(), getType(), s);
     }
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *  @deprecated
      */
     @Deprecated
     public RubyString newString(ByteList s) {
         return new RubyString(getRuntime(), getMetaClass(), s);
     }
 
     @Deprecated
     public static RubyString newString(Ruby runtime, RubyClass clazz, CharSequence str) {
         return new RubyString(runtime, clazz, str);
     }
 
     public static RubyString newStringLight(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes, false);
     }
 
     public static RubyString newStringLight(Ruby runtime, int size) {
         return new RubyString(runtime, runtime.getString(), new ByteList(size), false);
     }
   
     public static RubyString newString(Ruby runtime, CharSequence str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
 
     public static RubyString newString(Ruby runtime, String str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
     
     public static RubyString newString(Ruby runtime, byte[] bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newString(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] copy = new byte[length];
         System.arraycopy(bytes, start, copy, 0, length);
         return new RubyString(runtime, runtime.getString(), new ByteList(copy, false));
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes, Encoding encoding) {
         return new RubyString(runtime, runtime.getString(), bytes, encoding);
     }
     
     public static RubyString newUnicodeString(Ruby runtime, String str) {
         return new RubyString(runtime, runtime.getString(), new ByteList(RubyEncoding.encodeUTF8(str), false));
     }
 
     // String construction routines by NOT byte[] buffer and making the target String shared 
     public static RubyString newStringShared(Ruby runtime, RubyString orig) {
         orig.shareLevel = SHARE_LEVEL_BYTELIST;
         RubyString str = new RubyString(runtime, runtime.getString(), orig.value);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }       
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes) {
         return newStringShared(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes, Encoding encoding) {
         return newStringShared(runtime, runtime.getString(), bytes, encoding);
     }
 
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes, int codeRange) {
         RubyString str = new RubyString(runtime, runtime.getString(), bytes, codeRange);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes) {
         RubyString str = new RubyString(runtime, clazz, bytes);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes, Encoding encoding) {
         RubyString str = new RubyString(runtime, clazz, bytes, encoding);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, byte[] bytes) {
         return newStringShared(runtime, new ByteList(bytes, false));
     }
 
     public static RubyString newStringShared(Ruby runtime, byte[] bytes, int start, int length) {
         return newStringShared(runtime, new ByteList(bytes, start, length, false));
     }
 
     public static RubyString newEmptyString(Ruby runtime) {
         return newEmptyString(runtime, runtime.getString());
     }
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass) {
         RubyString empty = new RubyString(runtime, metaClass, ByteList.EMPTY_BYTELIST);
         empty.shareLevel = SHARE_LEVEL_BYTELIST;
         return empty;
     }
 
     // String construction routines by NOT byte[] buffer and NOT making the target String shared 
     public static RubyString newStringNoCopy(Ruby runtime, ByteList bytes) {
         return newStringNoCopy(runtime, runtime.getString(), bytes);
     }    
 
     public static RubyString newStringNoCopy(Ruby runtime, RubyClass clazz, ByteList bytes) {
         return new RubyString(runtime, clazz, bytes);
     }    
 
     public static RubyString newStringNoCopy(Ruby runtime, byte[] bytes, int start, int length) {
         return newStringNoCopy(runtime, new ByteList(bytes, start, length, false));
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, byte[] bytes) {
         return newStringNoCopy(runtime, new ByteList(bytes, false));
     }
 
     /** Encoding aware String construction routines for 1.9
      * 
      */
     private static final class EmptyByteListHolder {
         final ByteList bytes;
         final int cr;
         EmptyByteListHolder(Encoding enc) {
             this.bytes = new ByteList(ByteList.NULL_ARRAY, enc);
             this.cr = bytes.getEncoding().isAsciiCompatible() ? CR_7BIT : CR_VALID;
         }
     }
 
     private static EmptyByteListHolder EMPTY_BYTELISTS[] = new EmptyByteListHolder[4];
 
     static EmptyByteListHolder getEmptyByteList(Encoding enc) {
         int index = enc.getIndex();
         EmptyByteListHolder bytes;
         if (index < EMPTY_BYTELISTS.length && (bytes = EMPTY_BYTELISTS[index]) != null) {
             return bytes;
         }
         return prepareEmptyByteList(enc);
     }
 
     private static EmptyByteListHolder prepareEmptyByteList(Encoding enc) {
         int index = enc.getIndex();
         if (index >= EMPTY_BYTELISTS.length) {
             EmptyByteListHolder tmp[] = new EmptyByteListHolder[index + 4];
             System.arraycopy(EMPTY_BYTELISTS,0, tmp, 0, EMPTY_BYTELISTS.length);
             EMPTY_BYTELISTS = tmp;
         }
         return EMPTY_BYTELISTS[index] = new EmptyByteListHolder(enc);
     }
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass, Encoding enc) {
         EmptyByteListHolder holder = getEmptyByteList(enc);
         RubyString empty = new RubyString(runtime, metaClass, holder.bytes, holder.cr);
         empty.shareLevel = SHARE_LEVEL_BYTELIST;
         return empty;
     }
 
     public static RubyString newEmptyString(Ruby runtime, Encoding enc) {
         return newEmptyString(runtime, runtime.getString(), enc);
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, RubyClass clazz, ByteList bytes, Encoding enc, int cr) {
         return new RubyString(runtime, clazz, bytes, enc, cr);
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, ByteList bytes, Encoding enc, int cr) {
         return newStringNoCopy(runtime, runtime.getString(), bytes, enc, cr);
     }
 
     public static RubyString newUsAsciiStringNoCopy(Ruby runtime, ByteList bytes) {
         return newStringNoCopy(runtime, bytes, USASCIIEncoding.INSTANCE, CR_7BIT);
     }
 
     public static RubyString newUsAsciiStringShared(Ruby runtime, ByteList bytes) {
         RubyString str = newStringNoCopy(runtime, bytes, USASCIIEncoding.INSTANCE, CR_7BIT);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
     
     public static RubyString newUsAsciiStringShared(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] copy = new byte[length];
         System.arraycopy(bytes, start, copy, 0, length);
         return newUsAsciiStringShared(runtime, new ByteList(copy, false));
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.STRING;
     }
 
     @Override
     public Class getJavaClass() {
         return String.class;
     }
 
     @Override
     public RubyString convertToString() {
         return this;
     }
 
     @Override
     public String toString() {
-        return value.toString();
+        return decodeString();
+    }
+
+    /**
+     * Convert this Ruby string to a Java String. This version is encoding-aware.
+     *
+     * @return A decoded Java String, based on this Ruby string's encoding.
+     */
+    public String decodeString() {
+        try {
+            // 1.9 support for encodings
+            // TODO: Fix charset use for JRUBY-4553
+            if (getRuntime().is1_9()) {
+                return new String(value.getUnsafeBytes(), value.begin(), value.length(), getEncoding().toString());
+            }
+
+            return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.begin(), value.length());
+        } catch (UnsupportedEncodingException uee) {
+            return toString();
+        }
     }
 
     /** rb_str_dup
      * 
      */
     @Deprecated
     public final RubyString strDup() {
         return strDup(getRuntime(), getMetaClass());
     }
     
     public final RubyString strDup(Ruby runtime) {
         return strDup(runtime, getMetaClass());
     }
     
     @Deprecated
     final RubyString strDup(RubyClass clazz) {
         return strDup(getRuntime(), getMetaClass());
     }
 
     final RubyString strDup(Ruby runtime, RubyClass clazz) {
         shareLevel = SHARE_LEVEL_BYTELIST;
         RubyString dup = new RubyString(runtime, clazz, value);
         dup.shareLevel = SHARE_LEVEL_BYTELIST;
         dup.flags |= flags & (CR_MASK | TAINTED_F | UNTRUSTED_F);
 
         return dup;
     }
 
     /* rb_str_subseq */
     public final RubyString makeSharedString(Ruby runtime, int index, int len) {
         return makeShared(runtime, runtime.getString(), index, len);
     }
 
     public final RubyString makeShared(Ruby runtime, int index, int len) {
         return makeShared(runtime, getType(), index, len);
     }
 
     public final RubyString makeShared(Ruby runtime, RubyClass meta, int index, int len) {
         final RubyString shared;
         if (len == 0) {
             shared = newEmptyString(runtime, meta);
         } else if (len == 1) {
             shared = newStringShared(runtime, meta, 
                     RubyInteger.SINGLE_CHAR_BYTELISTS[value.getUnsafeBytes()[value.getBegin() + index] & 0xff]);
         } else {
             if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
             shared = new RubyString(runtime, meta, value.makeShared(index, len));
             shared.shareLevel = SHARE_LEVEL_BUFFER;
         }
 
         shared.infectBy(this);
         return shared;
     }
 
     public final RubyString makeShared19(Ruby runtime, int index, int len) {
         return makeShared19(runtime, value, index, len);
     }
 
     private RubyString makeShared19(Ruby runtime, ByteList value, int index, int len) {
         final RubyString shared;
         Encoding enc = value.getEncoding();
         RubyClass meta = getType();
 
         if (len == 0) {
             shared = newEmptyString(runtime, meta, enc);
         } else {
             if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
             shared = new RubyString(runtime, meta, value.makeShared(index, len));
             shared.shareLevel = SHARE_LEVEL_BUFFER;
             shared.copyCodeRangeForSubstr(this, enc); // no need to assign encoding, same bytelist shared
         }
         shared.infectBy(this);
         return shared;
     }
 
     final void modifyCheck() {
         frozenCheck();
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify string");
         }
     }
 
     private final void modifyCheck(byte[] b, int len) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len) throw getRuntime().newRuntimeError("string modified");
     }
 
     private final void modifyCheck(byte[] b, int len, Encoding enc) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len || value.getEncoding() != enc) throw getRuntime().newRuntimeError("string modified");
     }
 
     private void frozenCheck() {
         frozenCheck(false);
     }
 
     private void frozenCheck(boolean runtimeError) {
         if (isFrozen()) throw getRuntime().newFrozenError("string", runtimeError);
     }
 
     /** rb_str_modify
      * 
      */
     public final void modify() {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 value = value.dup();
             } else {
                 value.unshare();
             }
             shareLevel = SHARE_LEVEL_NONE;
         }
 
         value.invalidate();
     }
 
     public final void modify19() {
         modify();
         clearCodeRange();
     }
 
     private void modifyAndKeepCodeRange() {
         modify();
         keepCodeRange();
     }
     
     /** rb_str_modify (with length bytes ensured)
      * 
      */    
     public final void modify(int length) {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 value = value.dup(length);
             } else {
                 value.unshare(length);
             }
             shareLevel = SHARE_LEVEL_NONE;
         } else {
             value.ensure(length);
         }
 
         value.invalidate();
     }
     
     public final void modify19(int length) {
         modify(length);
         clearCodeRange();
     }
     
     /** rb_str_resize
      */
     public final void resize(int length) {
         modify();
         if (value.getRealSize() > length) {
             value.setRealSize(length);
         } else if (value.length() < length) {
             value.length(length);
         }
     }
 
     final void view(ByteList bytes) {
         modifyCheck();
 
         value = bytes;
         shareLevel = SHARE_LEVEL_NONE;
     }
 
     private final void view(byte[]bytes) {
         modifyCheck();        
 
         value.replace(bytes);
         shareLevel = SHARE_LEVEL_NONE;
 
         value.invalidate();        
     }
 
     private final void view(int index, int len) {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 // if len == 0 then shared empty
                 value = value.makeShared(index, len);
                 shareLevel = SHARE_LEVEL_BUFFER;
             } else {
                 value.view(index, len);
             }
         } else {        
             value.view(index, len);
             // FIXME this below is temporary, but its much safer for COW (it prevents not shared Strings with begin != 0)
             // this allows now e.g.: ByteList#set not to be begin aware
             shareLevel = SHARE_LEVEL_BUFFER;
         }
 
         value.invalidate();
     }
 
     public static String bytesToString(byte[] bytes, int beg, int len) {
         return new String(ByteList.plain(bytes, beg, len));
     }
 
     public static String byteListToString(ByteList bytes) {
         return bytesToString(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
     }
 
     public static String bytesToString(byte[] bytes) {
         return bytesToString(bytes, 0, bytes.length);
     }
 
     public static byte[] stringToBytes(String string) {
         return ByteList.plain(string);
     }
 
     @Override
     public RubyString asString() {
         return this;
     }
 
     @Override
     public IRubyObject checkStringType() {
         return this;
     }
 
     @Override
     public IRubyObject checkStringType19() {
         return this;
     }
 
     @JRubyMethod(name = "try_convert", meta = true, compat = RUBY1_9)
     public static IRubyObject try_convert(ThreadContext context, IRubyObject recv, IRubyObject str) {
         return str.checkStringType();
     }
 
     @JRubyMethod(name = {"to_s", "to_str"})
     @Override
     public IRubyObject to_s() {
         Ruby runtime = getRuntime();
         if (getMetaClass().getRealClass() != runtime.getString()) {
             return strDup(runtime, runtime.getString());
         }
         return this;
     }
 
     @Override
     public final int compareTo(IRubyObject other) {
         Ruby runtime = getRuntime();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             return runtime.is1_9() ? op_cmp19(otherString) : op_cmp(otherString);
         }
         return (int)op_cmpCommon(runtime.getCurrentContext(), other).convertToInteger().getLongValue();
     }
 
     /* rb_str_cmp_m */
     @JRubyMethod(name = "<=>", compat = RUBY1_8)
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.getRuntime().newFixnum(op_cmp((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     @JRubyMethod(name = "<=>", compat = RUBY1_9)
     public IRubyObject op_cmp19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.getRuntime().newFixnum(op_cmp19((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     private IRubyObject op_cmpCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         // deal with case when "other" is not a string
         if (other.respondsTo("to_str") && other.respondsTo("<=>")) {
             IRubyObject result = other.callMethod(context, "<=>", this);
             if (result.isNil()) return result;
             if (result instanceof RubyFixnum) {
                 return RubyFixnum.newFixnum(runtime, -((RubyFixnum)result).getLongValue());
             } else {
                 return RubyFixnum.zero(runtime).callMethod(context, "-", result);
             }
         }
         return runtime.getNil();        
     }
         
     /** rb_str_equal
      * 
      */
     @JRubyMethod(name = "==", compat = RUBY1_8)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             return value.equal(((RubyString)other).value) ? runtime.getTrue() : runtime.getFalse();    
         }
         return op_equalCommon(context, other);
     }
 
     @JRubyMethod(name = "==", compat = RUBY1_9)
     public IRubyObject op_equal19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             return isComparableWith(otherString) && value.equal(otherString.value) ? runtime.getTrue() : runtime.getFalse();    
         }
         return op_equalCommon(context, other);
     }
 
     private IRubyObject op_equalCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (!other.respondsTo("to_str")) return runtime.getFalse();
         return other.callMethod(context, "==", this).isTrue() ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_8, argTypes = RubyString.class)
     public IRubyObject op_plus(ThreadContext context, RubyString str) {
         RubyString resultStr = newString(context.getRuntime(), addByteLists(value, str.value));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
     public IRubyObject op_plus(ThreadContext context, IRubyObject other) {
         return op_plus(context, other.convertToString());
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_9)
     public IRubyObject op_plus19(ThreadContext context, RubyString str) {
         Encoding enc = checkEncoding(str);
         RubyString resultStr = newStringNoCopy(context.getRuntime(), addByteLists(value, str.value),
                                     enc, codeRangeAnd(getCodeRange(), str.getCodeRange()));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
     public IRubyObject op_plus19(ThreadContext context, IRubyObject other) {
         return op_plus19(context, other.convertToString());
     }
 
     private ByteList addByteLists(ByteList value1, ByteList value2) {
         ByteList result = new ByteList(value1.getRealSize() + value2.getRealSize());
         result.setRealSize(value1.getRealSize() + value2.getRealSize());
         System.arraycopy(value1.getUnsafeBytes(), value1.getBegin(), result.getUnsafeBytes(), 0, value1.getRealSize());
         System.arraycopy(value2.getUnsafeBytes(), value2.getBegin(), result.getUnsafeBytes(), value1.getRealSize(), value2.getRealSize());
         return result;
     }
 
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_8)
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         return multiplyByteList(context, other);
     }
 
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_9)
     public IRubyObject op_mul19(ThreadContext context, IRubyObject other) {
         RubyString result = multiplyByteList(context, other);
         result.value.setEncoding(value.getEncoding());
         result.copyCodeRange(this);
         return result;
     }
 
     private RubyString multiplyByteList(ThreadContext context, IRubyObject arg) {
         int len = RubyNumeric.num2int(arg);
         if (len < 0) throw context.getRuntime().newArgumentError("negative argument");
 
         // we limit to int because ByteBuffer can only allocate int sizes
         if (len > 0 && Integer.MAX_VALUE / len < value.getRealSize()) {
             throw context.getRuntime().newArgumentError("argument too big");
         }
 
         ByteList bytes = new ByteList(len *= value.getRealSize());
         if (len > 0) {
             bytes.setRealSize(len);
             int n = value.getRealSize();
             System.arraycopy(value.getUnsafeBytes(), value.getBegin(), bytes.getUnsafeBytes(), 0, n);
             while (n <= len >> 1) {
                 System.arraycopy(bytes.getUnsafeBytes(), 0, bytes.getUnsafeBytes(), n, n);
                 n <<= 1;
             }
             System.arraycopy(bytes.getUnsafeBytes(), 0, bytes.getUnsafeBytes(), n, len - n);
         }
         RubyString result = new RubyString(context.getRuntime(), getMetaClass(), bytes);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "%", required = 1)
     public IRubyObject op_format(ThreadContext context, IRubyObject arg) {
         return opFormatCommon(context, arg, context.getRuntime().getInstanceConfig().getCompatVersion());
     }
 
     private IRubyObject opFormatCommon(ThreadContext context, IRubyObject arg, CompatVersion compat) {
         IRubyObject tmp = arg.checkArrayType();
         if (tmp.isNil()) tmp = arg;
 
         // FIXME: Should we make this work with platform's locale,
         // or continue hardcoding US?
         ByteList out = new ByteList(value.getRealSize());
         boolean tainted;
         switch (compat) {
         case RUBY1_8:
             tainted = Sprintf.sprintf(out, Locale.US, value, tmp);
             break;
         case RUBY1_9:
             tainted = Sprintf.sprintf1_9(out, Locale.US, value, tmp);
             break;
         default:
             throw new RuntimeException("invalid compat version for sprintf: " + compat);
         }
         RubyString str = newString(context.getRuntime(), out);
 
         str.setTaint(tainted || isTaint());
         return str;
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
         Ruby runtime = getRuntime();
         return RubyFixnum.newFixnum(runtime, strHashCode(runtime));
     }
 
     @Override
     public int hashCode() {
         return strHashCode(getRuntime());
     }
 
     private int strHashCode(Ruby runtime) {
         if (runtime.is1_9()) {
             return value.hashCode() ^ (value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT ? 0 : value.getEncoding().getIndex());
         } else {
             return value.hashCode();
         }
     }
 
     @Override
     public boolean equals(Object other) {
         if (this == other) return true;
 
         if (other instanceof RubyString) {
             if (((RubyString) other).value.equal(value)) return true;
         }
 
         return false;
     }
 
     /** rb_obj_as_string
      *
      */
     public static RubyString objAsString(ThreadContext context, IRubyObject obj) {
         if (obj instanceof RubyString) return (RubyString) obj;
         IRubyObject str = obj.callMethod(context, "to_s");
         if (!(str instanceof RubyString)) return (RubyString) obj.anyToString();
         if (obj.isTaint()) str.setTaint(true);
         return (RubyString) str;
     }
 
     /** rb_str_cmp
      *
      */
     public final int op_cmp(RubyString other) {
         return value.cmp(other.value);
     }
 
     public final int op_cmp19(RubyString other) {
         int ret = value.cmp(other.value);
         if (ret == 0 && !isComparableWith(other)) {
             return value.getEncoding().getIndex() > other.value.getEncoding().getIndex() ? 1 : -1;
         }
         return ret;
     }
 
     /** rb_to_id
      *
      */
     @Override
     public String asJavaString() {
         return toString();
     }
 
     public IRubyObject doClone(){
         return newString(getRuntime(), value.dup());
     }
 
     public final RubyString cat(byte[] str) {
         modify(value.getRealSize() + str.length);
         System.arraycopy(str, 0, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), str.length);
         value.setRealSize(value.getRealSize() + str.length);
         return this;
     }
 
     public final RubyString cat(byte[] str, int beg, int len) {
         modify(value.getRealSize() + len);
         System.arraycopy(str, beg, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
         value.setRealSize(value.getRealSize() + len);
         return this;
     }
 
     // // rb_str_buf_append
     public final RubyString cat19(RubyString str) {
         ByteList strValue = str.value;
         int strCr = str.getCodeRange();
         strCr = cat(strValue.getUnsafeBytes(), strValue.getBegin(), strValue.getRealSize(), strValue.getEncoding(), strCr, strCr);
         infectBy(str);
         str.setCodeRange(strCr);
         return this;
     }
 
     public final RubyString cat(ByteList str) {
         modify(value.getRealSize() + str.getRealSize());
         System.arraycopy(str.getUnsafeBytes(), str.getBegin(), value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), str.getRealSize());
         value.setRealSize(value.getRealSize() + str.getRealSize());
         return this;
     }
 
     public final RubyString cat(byte ch) {
         modify(value.getRealSize() + 1);
         value.getUnsafeBytes()[value.getBegin() + value.getRealSize()] = ch;
         value.setRealSize(value.getRealSize() + 1);
         return this;
     }
 
     public final RubyString cat(int ch) {
         return cat((byte)ch);
     }
 
     public final RubyString cat(int code, Encoding enc) {
         int n = codeLength(getRuntime(), enc, code);
         modify(value.getRealSize() + n);
         enc.codeToMbc(code, value.getUnsafeBytes(), value.getBegin() + value.getRealSize());
         value.setRealSize(value.getRealSize() + n);
         return this;
     }
 
     public final int cat(byte[]bytes, int p, int len, Encoding enc, int cr, int cr2) {
         modify(value.getRealSize() + len);
         int toCr = getCodeRange();
         Encoding toEnc = value.getEncoding();
 
         if (toEnc == enc) {
             if (toCr == CR_UNKNOWN || (toEnc == ASCIIEncoding.INSTANCE && toCr != CR_7BIT)) { 
                 cr = CR_UNKNOWN;
             } else if (cr == CR_UNKNOWN) {
                 cr = codeRangeScan(enc, bytes, p, len);
             }
         } else {
             if (!toEnc.isAsciiCompatible() || !enc.isAsciiCompatible()) {
                 if (len == 0) return cr2;
                 if (value.getRealSize() == 0) {
                     System.arraycopy(bytes, p, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
                     value.setRealSize(value.getRealSize() + len);
                     setEncodingAndCodeRange(enc, cr);
                     return cr2;
                 }
                 throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + toEnc + " and " + enc);
             }
             if (cr == CR_UNKNOWN) cr = codeRangeScan(enc, bytes, p, len);
             if (toCr == CR_UNKNOWN) {
                 if (toEnc == ASCIIEncoding.INSTANCE || cr != CR_7BIT) toCr = scanForCodeRange(); 
             }
         }
         if (cr2 != 0) cr2 = cr;
 
         if (toEnc != enc && toCr != CR_7BIT && cr != CR_7BIT) {        
             throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + toEnc + " and " + enc);
         }
 
         final int resCr;
         final Encoding resEnc;
         if (toCr == CR_UNKNOWN) {
             resEnc = toEnc;
             resCr = CR_UNKNOWN;
         } else if (toCr == CR_7BIT) {
             if (cr == CR_7BIT) {
                 resEnc = toEnc == ASCIIEncoding.INSTANCE ? toEnc : enc;
                 resCr = CR_7BIT;
             } else {
                 resEnc = enc;
                 resCr = cr;
             }
         } else if (toCr == CR_VALID) {
             resEnc = toEnc;
             resCr = toCr;
         } else {
             resEnc = toEnc;
             resCr = len > 0 ? CR_UNKNOWN : toCr;
         }
         
         if (len < 0) throw getRuntime().newArgumentError("negative string size (or size too big)");            
 
         System.arraycopy(bytes, p, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
         value.setRealSize(value.getRealSize() + len);
         setEncodingAndCodeRange(resEnc, resCr);
 
         return cr2;
     }
 
     public final int cat(byte[]bytes, int p, int len, Encoding enc) {
         return cat(bytes, p, len, enc, CR_UNKNOWN, 0);
     }
 
     public final RubyString catAscii(byte[]bytes, int p, int len) {
         Encoding enc = value.getEncoding();
         if (enc.isAsciiCompatible()) {
             cat(bytes, p, len, enc, CR_7BIT, 0);
         } else {
             byte buf[] = new byte[enc.maxLength()];
             int end = p + len;
             while (p < end) {
                 int c = bytes[p];
                 int cl = codeLength(getRuntime(), enc, c);
                 enc.codeToMbc(c, buf, 0);
                 cat(buf, 0, cl, enc, CR_VALID, 0);
                 p++;
             }
         }
         return this;
     }
 
     /** rb_str_replace_m
      *
      */
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1, compat = RUBY1_8)
     public IRubyObject replace(IRubyObject other) {
         if (this == other) return this;
         replaceCommon(other);
         return this;
     }
 
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1, compat = RUBY1_9)
     public RubyString replace19(IRubyObject other) {
         modifyCheck();
         if (this == other) return this;        
         setCodeRange(replaceCommon(other).getCodeRange()); // encoding doesn't have to be copied.
         return this;
     }
 
     private RubyString replaceCommon(IRubyObject other) {
         modifyCheck();
         RubyString otherStr = other.convertToString();
         otherStr.shareLevel = shareLevel = SHARE_LEVEL_BYTELIST;
         value = otherStr.value;
         infectBy(otherStr);
         return otherStr;
     }
 
     @JRubyMethod(name = "clear", compat = RUBY1_9)
     public RubyString clear() {
         modifyCheck();
         Encoding enc = value.getEncoding();
 
         EmptyByteListHolder holder = getEmptyByteList(enc); 
         value = holder.bytes;
         shareLevel = SHARE_LEVEL_BYTELIST;
         setCodeRange(holder.cr);
         return this;
     }
 
     @JRubyMethod(name = "reverse", compat = RUBY1_8)
     public IRubyObject reverse(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() <= 1) return strDup(context.getRuntime());
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         byte[]obytes = new byte[len];
 
         for (int i = 0; i <= len >> 1; i++) {
             obytes[i] = bytes[p + len - i - 1];
             obytes[len - i - 1] = bytes[p + i];
         }
 
         return new RubyString(runtime, getMetaClass(), new ByteList(obytes, false)).infectBy(this);
     }
 
     @JRubyMethod(name = "reverse", compat = RUBY1_9)
     public IRubyObject reverse19(ThreadContext context) {        
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() <= 1) return strDup(context.getRuntime());
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         byte[]obytes = new byte[len];
 
         boolean single = true;
         Encoding enc = value.getEncoding();
         // this really needs to be inlined here
         if (singleByteOptimizable(enc)) {
             for (int i = 0; i <= len >> 1; i++) {
                 obytes[i] = bytes[p + len - i - 1];
                 obytes[len - i - 1] = bytes[p + i];
             }
         } else {
             int end = p + len;
             int op = len;
             while (p < end) {
                 int cl = StringSupport.length(enc, bytes, p, end);
                 if (cl > 1 || (bytes[p] & 0x80) != 0) {
                     single = false;
                     op -= cl;
                     System.arraycopy(bytes, p, obytes, op, cl);
                     p += cl;
                 } else {
                     obytes[--op] = bytes[p++];
                 }
             }
         }
 
         RubyString result = new RubyString(runtime, getMetaClass(), new ByteList(obytes, false));
 
         if (getCodeRange() == CR_UNKNOWN) setCodeRange(single ? CR_7BIT : CR_VALID);
         Encoding encoding = value.getEncoding();
         result.value.setEncoding(encoding);
         result.copyCodeRangeForSubstr(this, encoding);
         return result.infectBy(this);
     }
 
     @JRubyMethod(name = "reverse!", compat = RUBY1_8)
     public RubyString reverse_bang(ThreadContext context) {
         if (value.getRealSize() > 1) {
             modify();
             byte[]bytes = value.getUnsafeBytes();
             int p = value.getBegin();
             int len = value.getRealSize();
             for (int i = 0; i < len >> 1; i++) {
                 byte b = bytes[p + i];
                 bytes[p + i] = bytes[p + len - i - 1];
                 bytes[p + len - i - 1] = b;
             }
         }
 
         return this;
     }
 
     @JRubyMethod(name = "reverse!", compat = RUBY1_9)
     public RubyString reverse_bang19(ThreadContext context) {
         modifyCheck();
         if (value.getRealSize() > 1) {
             modifyAndKeepCodeRange();
             byte[]bytes = value.getUnsafeBytes();
             int p = value.getBegin();
             int len = value.getRealSize();
             
             Encoding enc = value.getEncoding();
             // this really needs to be inlined here
             if (singleByteOptimizable(enc)) {
                 for (int i = 0; i < len >> 1; i++) {
                     byte b = bytes[p + i];
                     bytes[p + i] = bytes[p + len - i - 1];
                     bytes[p + len - i - 1] = b;
                 }
             } else {
                 int end = p + len;
                 int op = len;
                 byte[]obytes = new byte[len];
                 boolean single = true;
                 while (p < end) {
                     int cl = StringSupport.length(enc, bytes, p, end);
                     if (cl > 1 || (bytes[p] & 0x80) != 0) {
                         single = false;
                         op -= cl;
                         System.arraycopy(bytes, p, obytes, op, cl);
                         p += cl;
                     } else {
                         obytes[--op] = bytes[p++];
                     }
                 }
                 value.setUnsafeBytes(obytes);
                 if (getCodeRange() == CR_UNKNOWN) setCodeRange(single ? CR_7BIT : CR_VALID);
             }
         }
         return this;
     }
 
     /** rb_str_s_new
      *
      */
     public static RubyString newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString newString = newStringShared(recv.getRuntime(), ByteList.EMPTY_BYTELIST);
         newString.setMetaClass((RubyClass) recv);
         newString.callInit(args, block);
         return newString;
     }
 
     @JRubyMethod(visibility = PRIVATE, compat = RUBY1_8)
     @Override
     public IRubyObject initialize() {
         return this;
     }
 
     @JRubyMethod(visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize(IRubyObject arg0) {
         replace(arg0);
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19() {
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(IRubyObject arg0) {
         replace19(arg0);
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject casecmp(ThreadContext context, IRubyObject other) {
         return RubyFixnum.newFixnum(context.getRuntime(), value.caseInsensitiveCmp(other.convertToString().value));
     }
 
     @JRubyMethod(name = "casecmp", compat = RUBY1_9)
     public IRubyObject casecmp19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         RubyString otherStr = other.convertToString();
         Encoding enc = isCompatibleWith(otherStr);
         if (enc == null) return runtime.getNil();
         
         if (singleByteOptimizable() && otherStr.singleByteOptimizable()) {
             return RubyFixnum.newFixnum(runtime, value.caseInsensitiveCmp(otherStr.value));
         } else {
             return multiByteCasecmp(runtime, enc, value, otherStr.value);
         }
     }
 
     private IRubyObject multiByteCasecmp(Ruby runtime, Encoding enc, ByteList value, ByteList otherValue) {
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
 
         byte[]obytes = otherValue.getUnsafeBytes();
         int op = otherValue.getBegin();
         int oend = op + otherValue.getRealSize();
 
         while (p < end && op < oend) {
             final int c, oc;
             if (enc.isAsciiCompatible()) {
                 c = bytes[p] & 0xff;
                 oc = obytes[op] & 0xff;
             } else {
                 c = StringSupport.preciseCodePoint(enc, bytes, p, end);
                 oc = StringSupport.preciseCodePoint(enc, obytes, op, oend);                
             }
 
             int cl, ocl;
             if (Encoding.isAscii(c) && Encoding.isAscii(oc)) {
                 byte uc = AsciiTables.ToUpperCaseTable[c];
                 byte uoc = AsciiTables.ToUpperCaseTable[oc];
                 if (uc != uoc) {
                     return uc < uoc ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
                 }
                 cl = ocl = 1;
             } else {
                 cl = StringSupport.length(enc, bytes, p, end);
                 ocl = StringSupport.length(enc, obytes, op, oend);
                 // TODO: opt for 2 and 3 ?
                 int ret = StringSupport.caseCmp(bytes, p, obytes, op, cl < ocl ? cl : ocl);
                 if (ret != 0) return ret < 0 ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
                 if (cl != ocl) return cl < ocl ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
             }
 
             p += cl;
             op += ocl;
         }
         if (end - p == oend - op) return RubyFixnum.zero(runtime);
         return end - p > oend - op ? RubyFixnum.one(runtime) : RubyFixnum.minus_one(runtime);
     }
 
     /** rb_str_match
      *
      */
     @JRubyMethod(name = "=~", compat = RUBY1_8, writes = BACKREF)
     @Override
     public IRubyObject op_match(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).op_match(context, this);
         if (other instanceof RubyString) throw context.getRuntime().newTypeError("type mismatch: String given");
         return other.callMethod(context, "=~", this);
     }
 
     @JRubyMethod(name = "=~", compat = RUBY1_9, writes = BACKREF)
     public IRubyObject op_match19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).op_match19(context, this);
         if (other instanceof RubyString) throw context.getRuntime().newTypeError("type mismatch: String given");
         return other.callMethod(context, "=~", this);
     }
     /**
      * String#match(pattern)
      *
      * rb_str_match_m
      *
      * @param pattern Regexp or String
      */
     @JRubyMethod(compat = RUBY1_8, reads = BACKREF)
     public IRubyObject match(ThreadContext context, IRubyObject pattern) {
         return getPattern(pattern).callMethod(context, "match", this);
     }
 
     @JRubyMethod(name = "match", compat = RUBY1_9, reads = BACKREF)
     public IRubyObject match19(ThreadContext context, IRubyObject pattern, Block block) {
         IRubyObject result = getPattern(pattern).callMethod(context, "match", this);
         return block.isGiven() && !result.isNil() ? block.yield(context, result) : result;
     }
 
     @JRubyMethod(name = "match", required = 2, rest = true, compat = RUBY1_9, reads = BACKREF)
     public IRubyObject match19(ThreadContext context, IRubyObject[]args, Block block) {
         RubyRegexp pattern = getPattern(args[0]);
         args[0] = this;
         IRubyObject result = pattern.callMethod(context, "match", args);
         return block.isGiven() && !result.isNil() ? block.yield(context, result) : result;
     }
 
     /** rb_str_capitalize / rb_str_capitalize_bang
      *
      */
     @JRubyMethod(name = "capitalize", compat = RUBY1_8)
     public IRubyObject capitalize(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.capitalize_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "capitalize!", compat = RUBY1_8)
     public IRubyObject capitalize_bang(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modify();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
         boolean modify = false;
         
         int c = bytes[s] & 0xff;
         if (ASCII.isLower(c)) {
             bytes[s] = AsciiTables.ToUpperCaseTable[c];
             modify = true;
         }
 
         while (++s < end) {
             c = bytes[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 bytes[s] = AsciiTables.ToLowerCaseTable[c];
                 modify = true;
             }
         }
 
         return modify ? this : runtime.getNil();
     }
 
     @JRubyMethod(name = "capitalize", compat = RUBY1_9)
     public IRubyObject capitalize19(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.capitalize_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "capitalize!", compat = RUBY1_9)
     public IRubyObject capitalize_bang19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         Encoding enc = checkDummyEncoding();
 
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
         boolean modify = false;
 
         int c = codePoint(runtime, enc, bytes, s, end);
         if (enc.isLower(c)) {
             enc.codeToMbc(toUpper(enc, c), bytes, s);
             modify = true;
         }
 
         s += codeLength(runtime, enc, c);
         while (s < end) {
             c = codePoint(runtime, enc, bytes, s, end);
             if (enc.isUpper(c)) {
                 enc.codeToMbc(toLower(enc, c), bytes, s);
                 modify = true;
             }
             s += codeLength(runtime, enc, c);
         }
 
         return modify ? this : runtime.getNil();
     }
 
@@ -6289,1175 +6308,1165 @@ public class RubyString extends RubyObject implements EncodingCapable {
                 }
             }
 
             if (value.getRealSize() > (t - value.getBegin())) {
                 value.setRealSize(t - value.getBegin());
                 modify = true;
             }
         } else {
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
                         if (t.now > c) {
                             if (t.now < 0x80 && c < 0x80) {
                                 throw runtime.newArgumentError("invalid range \""
                                         + (char) t.now + "-" + (char) c + "\" in string transliteration");
                             }
 
                             throw runtime.newArgumentError("invalid range in string transliteration");
                         }
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
     @JRubyMethod(name ="tr_s", compat = RUBY1_8)
     public IRubyObject tr_s(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.trTrans(context, src, repl, true);
         return str;
     }
 
     @JRubyMethod(name = "tr_s!", compat = RUBY1_8)
     public IRubyObject tr_s_bang(ThreadContext context, IRubyObject src, IRubyObject repl) {
         return trTrans(context, src, repl, true);
     }
 
     @JRubyMethod(name ="tr_s", compat = RUBY1_9)
     public IRubyObject tr_s19(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.trTrans19(context, src, repl, true);
         return str;
     }
 
     @JRubyMethod(name = "tr_s!", compat = RUBY1_9)
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
                 if (++p == end || bytes[p] != '\n') continue;
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
 
     @JRubyMethod(name = "each", compat = RUBY1_8)
     public IRubyObject each18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "each");
     }
 
     @JRubyMethod(name = "each", compat = RUBY1_8)
     public IRubyObject each18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each", arg);
     }
 
     @JRubyMethod(name = "each_line", compat = RUBY1_8)
     public IRubyObject each_line18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     @JRubyMethod(name = "each_line", compat = RUBY1_8)
     public IRubyObject each_line18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each_line", arg);
     }
 
     @JRubyMethod(name = "lines", compat = RUBY1_8)
     public IRubyObject lines18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "lines");
     }
 
     @JRubyMethod(name = "lines", compat = RUBY1_8)
     public IRubyObject lines18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "lines", arg);
     }
 
     @JRubyMethod(name = "each_line", compat = RUBY1_9)
     public IRubyObject each_line19(ThreadContext context, Block block) {
         return block.isGiven() ? each_lineCommon19(context, block) : 
             enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     @JRubyMethod(name = "each_line", compat = RUBY1_9)
     public IRubyObject each_line19(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon19(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each_line", arg);
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject lines(ThreadContext context, Block block) {
         return block.isGiven() ? each_lineCommon19(context, block) : 
             enumeratorize(context.getRuntime(), this, "lines");
     }
 
     @JRubyMethod(compat = RUBY1_9)
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
         if (! sep.respondsTo("to_str")) {
             throw runtime.newTypeError("can't convert " + sep.getMetaClass() + " into String");
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
 
     @JRubyMethod(name = "each_byte")
     public IRubyObject each_byte19(ThreadContext context, Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod
     public IRubyObject bytes(ThreadContext context, Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "bytes");
     }
 
     /** rb_str_each_char
      * 
      */
     @JRubyMethod(name = "each_char", compat = RUBY1_8)
     public IRubyObject each_char18(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon18(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod(name = "chars", compat = RUBY1_8)
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
 
     @JRubyMethod(name = "each_char", compat = RUBY1_9)
     public IRubyObject each_char19(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon19(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod(name = "chars", compat = RUBY1_9)
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
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject each_codepoint(ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "each_codepoint");
         return singleByteOptimizable() ? each_byte(context, block) : each_codepointCommon(context, block);
     }
 
     @JRubyMethod(compat = RUBY1_9)
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
 
     @JRubyMethod(name = {"to_sym", "intern"}, compat = RUBY1_8)
     public RubySymbol intern() {
         if (value.getRealSize() == 0) throw getRuntime().newArgumentError("interning empty string");
         for (int i = 0; i < value.getRealSize(); i++) {
             if (value.getUnsafeBytes()[value.getBegin() + i] == 0) throw getRuntime().newArgumentError("symbol string may not contain '\\0'");
         }
         return to_sym();
     }
 
     @JRubyMethod(name = {"to_sym", "intern"}, compat = RUBY1_9)
     public RubySymbol intern19() {
         return to_sym();
     }
 
     @JRubyMethod(name = "ord", compat = RUBY1_9)
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
     @JRubyMethod(name = "to_c", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
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
     @JRubyMethod(name = "to_r", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
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
 
     @JRubyMethod(name = "encoding", compat = RUBY1_9)
     public IRubyObject encoding(ThreadContext context) {
         return context.getRuntime().getEncodingService().getEncoding(value.getEncoding());
     }
 
     @JRubyMethod(name = "encode!", compat = RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context) {
         modify19();
         IRubyObject defaultInternal = context.getRuntime().getEncodingService().getDefaultInternal();
         if (!defaultInternal.isNil()) {
             encode_bang(context, defaultInternal);
         }
         return this;
     }
 
     @JRubyMethod(name = "encode!", compat = RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context, IRubyObject enc) {
         modify19();
 
         Ruby runtime = context.getRuntime();
         this.value = encodeCommon(context, runtime, this.value, enc, runtime.getNil(),
             runtime.getNil());
 
         return this;
     }
 
     @JRubyMethod(name = "encode!", compat = RUBY1_9)
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
 
     @JRubyMethod(name = "encode!", compat = RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context, IRubyObject enc, IRubyObject fromEnc, IRubyObject opts) {
         modify19();
         this.value = encodeCommon(context, context.getRuntime(), this.value, enc, fromEnc, opts);
         return this;
     }
 
     @JRubyMethod(name = "encode", compat = RUBY1_9)
     public IRubyObject encode(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         IRubyObject defaultInternal = runtime.getEncodingService().getDefaultInternal();
 
         if (!defaultInternal.isNil()) {
             ByteList encoded = encodeCommon(context, runtime, value, defaultInternal,
                                             runtime.getNil(), runtime.getNil());
             return runtime.newString(encoded);
         } else {
             return dup();
         }
     }
 
     @JRubyMethod(name = "encode", compat = RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc) {
         Ruby runtime = context.getRuntime();
 
         ByteList encoded = encodeCommon(context, runtime, value, enc, runtime.getNil(),
             runtime.getNil());
         return runtime.newString(encoded);
     }
 
     @JRubyMethod(name = "encode", compat = RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc, IRubyObject arg) {
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
 
     @JRubyMethod(name = "encode", compat = RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc, IRubyObject fromEnc, IRubyObject opts) {
         Ruby runtime = context.getRuntime();
 
         ByteList encoded = encodeCommon(context, runtime, value, enc, fromEnc, opts);
         return runtime.newString(encoded);
     }
 
     private static ByteList encodeCommon(ThreadContext context, Ruby runtime, ByteList value,
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
 
     private static CharsetEncoder getEncoder(ThreadContext context, Ruby runtime, Charset charset, IRubyObject opts) {
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
 
     private static Encoding getEncoding(Ruby runtime, IRubyObject toEnc) {
         try {
             return runtime.getEncodingService().getEncodingFromObject(toEnc);
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + toEnc.toString() + ")");
         }
     }
 
     private static Charset getCharset(Ruby runtime, IRubyObject toEnc) {
         try {
             Encoding encoding = runtime.getEncodingService().getEncodingFromObject(toEnc);
 
             return getCharset(runtime, encoding);
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + toEnc.toString() + ")");
         }
     }
 
     private static Charset getCharset(Ruby runtime, Encoding encoding) {
         try {
             // special-casing ASCII* to ASCII
             return encoding.toString().startsWith("ASCII") ?
                 Charset.forName("ASCII") :
                 Charset.forName(encoding.toString());
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + encoding.toString() + ")");
         }
     }
 
     @JRubyMethod(name = "force_encoding", compat = RUBY1_9)
     public IRubyObject force_encoding(ThreadContext context, IRubyObject enc) {
         modify19();
         Encoding encoding = context.runtime.getEncodingService().getEncodingFromObject(enc);
         associateEncoding(encoding);
         return this;
     }
 
     @JRubyMethod(name = "valid_encoding?", compat = RUBY1_9)
     public IRubyObject valid_encoding_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return scanForCodeRange() == CR_BROKEN ? runtime.getFalse() : runtime.getTrue();
     }
 
     @JRubyMethod(name = "ascii_only?", compat = RUBY1_9)
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
-            try {
-                // 1.9 support for encodings
-                // TODO: Fix charset use for JRUBY-4553
-                if (getRuntime().is1_9()) {
-                    return new String(value.getUnsafeBytes(), value.begin(), value.length(), getEncoding().toString());
-                }
-
-                return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.begin(), value.length());
-            } catch (UnsupportedEncodingException uee) {
-                return toString();
-            }
+            return decodeString();
         } else if (target.isAssignableFrom(ByteList.class)) {
             return value;
         } else {
             return super.toJava(target);
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
