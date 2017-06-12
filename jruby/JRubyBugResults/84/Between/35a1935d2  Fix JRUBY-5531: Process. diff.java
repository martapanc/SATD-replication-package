diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 7c83c5595d..9477007882 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -670,1454 +670,1454 @@ public class RubyKernel {
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
         RubyStackTraceElement firstElement = elements[0];
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
-        long pid = ShellLauncher.runWithoutWait(runtime, args);
+        long pid = ShellLauncher.runExternalWithoutWait(runtime, args);
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
         throw runtime.newNotImplementedError("fork is not available on this platform");
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
diff --git a/src/org/jruby/RubyProcess.java b/src/org/jruby/RubyProcess.java
index 0057fd3292..39ce3f40c3 100644
--- a/src/org/jruby/RubyProcess.java
+++ b/src/org/jruby/RubyProcess.java
@@ -1,986 +1,986 @@
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 
 import com.kenai.constantine.platform.Errno;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.CallBlock;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ShellLauncher;
 
 import static org.jruby.ext.JRubyPOSIXHelper.checkErrno;
 
 
 /**
  */
 
 @JRubyModule(name="Process")
 public class RubyProcess {
 
     public static RubyModule createProcessModule(Ruby runtime) {
         RubyModule process = runtime.defineModule("Process");
         runtime.setProcess(process);
         
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
         RubyClass process_status = process.defineClassUnder("Status", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setProcStatus(process_status);
         
         RubyModule process_uid = process.defineModuleUnder("UID");
         runtime.setProcUID(process_uid);
         
         RubyModule process_gid = process.defineModuleUnder("GID");
         runtime.setProcGID(process_gid);
         
         RubyModule process_sys = process.defineModuleUnder("Sys");
         runtime.setProcSys(process_sys);
         
         process.defineAnnotatedMethods(RubyProcess.class);
         process_status.defineAnnotatedMethods(RubyStatus.class);
         process_uid.defineAnnotatedMethods(UserID.class);
         process_gid.defineAnnotatedMethods(GroupID.class);
         process_sys.defineAnnotatedMethods(Sys.class);
 
         runtime.loadConstantSet(process, com.kenai.constantine.platform.PRIO.class);
         runtime.loadConstantSet(process, com.kenai.constantine.platform.RLIM.class);
         runtime.loadConstantSet(process, com.kenai.constantine.platform.RLIMIT.class);
         
         process.defineConstant("WNOHANG", runtime.newFixnum(1));
         process.defineConstant("WUNTRACED", runtime.newFixnum(2));
         
         return process;
     }
     
     @JRubyClass(name="Process::Status")
     public static class RubyStatus extends RubyObject {
         private long status = 0L;
         
         private static final long EXIT_SUCCESS = 0L;
         public RubyStatus(Ruby runtime, RubyClass metaClass, long status) {
             super(runtime, metaClass);
             this.status = status;
         }
         
         public static RubyStatus newProcessStatus(Ruby runtime, long status) {
             return new RubyStatus(runtime, runtime.getProcStatus(), status);
         }
         
         // Bunch of methods still not implemented
         @JRubyMethod(name = {"to_int", "pid", "stopped?", "stopsig", "signaled?", "termsig?", "exited?", "coredump?"}, frame = true)
         public IRubyObject not_implemented() {
             String error = "Process::Status#" + getRuntime().getCurrentContext().getFrameName() + " not implemented";
             throw getRuntime().newNotImplementedError(error);
         }
         
         @JRubyMethod(name = {"&"}, frame = true)
         public IRubyObject not_implemented1(IRubyObject arg) {
             String error = "Process::Status#" + getRuntime().getCurrentContext().getFrameName() + " not implemented";
             throw getRuntime().newNotImplementedError(error);
         }
         
         @JRubyMethod
         public IRubyObject exitstatus() {
             return getRuntime().newFixnum(status);
         }
         
         @Deprecated
         public IRubyObject op_rshift(IRubyObject other) {
             return op_rshift(getRuntime(), other);
         }
         @JRubyMethod(name = ">>")
         public IRubyObject op_rshift(ThreadContext context, IRubyObject other) {
             return op_rshift(context.getRuntime(), other);
         }
         public IRubyObject op_rshift(Ruby runtime, IRubyObject other) {
             long shiftValue = other.convertToInteger().getLongValue();
             return runtime.newFixnum(status >> shiftValue);
         }
 
         @Override
         @JRubyMethod(name = "==")
         public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
             return other.callMethod(context, "==", this.to_i(context.getRuntime()));
         }
         
         @Deprecated
         public IRubyObject to_i() {
             return to_i(getRuntime());
         }
         @JRubyMethod
         public IRubyObject to_i(ThreadContext context) {
             return to_i(context.getRuntime());
         }
         public IRubyObject to_i(Ruby runtime) {
             return runtime.newFixnum(shiftedValue());
         }
         
         @Override
         public IRubyObject to_s() {
             return to_s(getRuntime());
         }
         @JRubyMethod
         public IRubyObject to_s(ThreadContext context) {
             return to_s(context.getRuntime());
         }
         public IRubyObject to_s(Ruby runtime) {
             return runtime.newString(String.valueOf(shiftedValue()));
         }
         
         @Override
         public IRubyObject inspect() {
             return inspect(getRuntime());
         }
         @JRubyMethod
         public IRubyObject inspect(ThreadContext context) {
             return inspect(context.getRuntime());
         }
         public IRubyObject inspect(Ruby runtime) {
             return runtime.newString("#<Process::Status: pid=????,exited(" + String.valueOf(status) + ")>");
         }
         
         @JRubyMethod(name = "success?")
         public IRubyObject success_p(ThreadContext context) {
             return context.getRuntime().newBoolean(status == EXIT_SUCCESS);
         }
         
         private long shiftedValue() {
             return status << 8;
         }
     }
 
     @JRubyModule(name="Process::UID")
     public static class UserID {
         @JRubyMethod(name = "change_privilege", module = true)
         public static IRubyObject change_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::UID::change_privilege not implemented yet");
         }
         
         @Deprecated
         public static IRubyObject eid(IRubyObject self) {
             return euid(self.getRuntime());
         }
         @JRubyMethod(name = "eid", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self) {
             return euid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self, IRubyObject arg) {
             return eid(self.getRuntime(), arg);
         }
         @JRubyMethod(name = "eid=", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self, IRubyObject arg) {
             return eid(context.getRuntime(), arg);
         }
         public static IRubyObject eid(Ruby runtime, IRubyObject arg) {
             return euid_set(runtime, arg);
         }
         
         @JRubyMethod(name = "grant_privilege", module = true)
         public static IRubyObject grant_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::UID::grant_privilege not implemented yet");
         }
         
         @JRubyMethod(name = "re_exchange", module = true)
         public static IRubyObject re_exchange(ThreadContext context, IRubyObject self) {
             return switch_rb(context, self, Block.NULL_BLOCK);
         }
         
         @JRubyMethod(name = "re_exchangeable?", module = true)
         public static IRubyObject re_exchangeable_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::re_exchangeable? not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject rid(IRubyObject self) {
             return rid(self.getRuntime());
         }
         @JRubyMethod(name = "rid", module = true)
         public static IRubyObject rid(ThreadContext context, IRubyObject self) {
             return rid(context.getRuntime());
         }
         public static IRubyObject rid(Ruby runtime) {
             return uid(runtime);
         }
         
         @JRubyMethod(name = "sid_available?", module = true)
         public static IRubyObject sid_available_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::sid_available not implemented yet");
         }
         
         @JRubyMethod(name = "switch", module = true, visibility = PRIVATE)
         public static IRubyObject switch_rb(ThreadContext context, IRubyObject self, Block block) {
             Ruby runtime = context.getRuntime();
             int uid = checkErrno(runtime, runtime.getPosix().getuid());
             int euid = checkErrno(runtime, runtime.getPosix().geteuid());
             
             if (block.isGiven()) {
                 try {
                     checkErrno(runtime, runtime.getPosix().seteuid(uid));
                     checkErrno(runtime, runtime.getPosix().setuid(euid));
                     
                     return block.yield(context, runtime.getNil());
                 } finally {
                     checkErrno(runtime, runtime.getPosix().seteuid(euid));
                     checkErrno(runtime, runtime.getPosix().setuid(uid));
                 }
             } else {
                 checkErrno(runtime, runtime.getPosix().seteuid(uid));
                 checkErrno(runtime, runtime.getPosix().setuid(euid));
                 
                 return RubyFixnum.zero(runtime);
             }
         }
     }
     
     @JRubyModule(name="Process::GID")
     public static class GroupID {
         @JRubyMethod(name = "change_privilege", module = true)
         public static IRubyObject change_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::GID::change_privilege not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self) {
             return eid(self.getRuntime());
         }
         @JRubyMethod(name = "eid", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self) {
             return eid(context.getRuntime());
         }
         public static IRubyObject eid(Ruby runtime) {
             return egid(runtime);
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self, IRubyObject arg) {
             return eid(self.getRuntime(), arg);
         }
         @JRubyMethod(name = "eid=", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self, IRubyObject arg) {
             return eid(context.getRuntime(), arg);
         }
         public static IRubyObject eid(Ruby runtime, IRubyObject arg) {
             return RubyProcess.egid_set(runtime, arg);
         }
         
         @JRubyMethod(name = "grant_privilege", module = true)
         public static IRubyObject grant_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::GID::grant_privilege not implemented yet");
         }
         
         @JRubyMethod(name = "re_exchange", module = true)
         public static IRubyObject re_exchange(ThreadContext context, IRubyObject self) {
             return switch_rb(context, self, Block.NULL_BLOCK);
         }
         
         @JRubyMethod(name = "re_exchangeable?", module = true)
         public static IRubyObject re_exchangeable_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::GID::re_exchangeable? not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject rid(IRubyObject self) {
             return rid(self.getRuntime());
         }
         @JRubyMethod(name = "rid", module = true)
         public static IRubyObject rid(ThreadContext context, IRubyObject self) {
             return rid(context.getRuntime());
         }
         public static IRubyObject rid(Ruby runtime) {
             return gid(runtime);
         }
         
         @JRubyMethod(name = "sid_available?", module = true)
         public static IRubyObject sid_available_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::GID::sid_available not implemented yet");
         }
         
         @JRubyMethod(name = "switch", module = true, visibility = PRIVATE)
         public static IRubyObject switch_rb(ThreadContext context, IRubyObject self, Block block) {
             Ruby runtime = context.getRuntime();
             int gid = checkErrno(runtime, runtime.getPosix().getgid());
             int egid = checkErrno(runtime, runtime.getPosix().getegid());
             
             if (block.isGiven()) {
                 try {
                     checkErrno(runtime, runtime.getPosix().setegid(gid));
                     checkErrno(runtime, runtime.getPosix().setgid(egid));
                     
                     return block.yield(context, runtime.getNil());
                 } finally {
                     checkErrno(runtime, runtime.getPosix().setegid(egid));
                     checkErrno(runtime, runtime.getPosix().setgid(gid));
                 }
             } else {
                 checkErrno(runtime, runtime.getPosix().setegid(gid));
                 checkErrno(runtime, runtime.getPosix().setgid(egid));
                 
                 return RubyFixnum.zero(runtime);
             }
         }
     }
     
     @JRubyModule(name="Process::Sys")
     public static class Sys {
         @Deprecated
         public static IRubyObject getegid(IRubyObject self) {
             return egid(self.getRuntime());
         }
         @JRubyMethod(name = "getegid", module = true, visibility = PRIVATE)
         public static IRubyObject getegid(ThreadContext context, IRubyObject self) {
             return egid(context.getRuntime());
         }
         
         @Deprecated
         public static IRubyObject geteuid(IRubyObject self) {
             return euid(self.getRuntime());
         }
         @JRubyMethod(name = "geteuid", module = true, visibility = PRIVATE)
         public static IRubyObject geteuid(ThreadContext context, IRubyObject self) {
             return euid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject getgid(IRubyObject self) {
             return gid(self.getRuntime());
         }
         @JRubyMethod(name = "getgid", module = true, visibility = PRIVATE)
         public static IRubyObject getgid(ThreadContext context, IRubyObject self) {
             return gid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject getuid(IRubyObject self) {
             return uid(self.getRuntime());
         }
         @JRubyMethod(name = "getuid", module = true, visibility = PRIVATE)
         public static IRubyObject getuid(ThreadContext context, IRubyObject self) {
             return uid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject setegid(IRubyObject recv, IRubyObject arg) {
             return egid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setegid", module = true, visibility = PRIVATE)
         public static IRubyObject setegid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return egid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject seteuid(IRubyObject recv, IRubyObject arg) {
             return euid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "seteuid", module = true, visibility = PRIVATE)
         public static IRubyObject seteuid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return euid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject setgid(IRubyObject recv, IRubyObject arg) {
             return gid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setgid", module = true, visibility = PRIVATE)
         public static IRubyObject setgid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return gid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject setuid(IRubyObject recv, IRubyObject arg) {
             return uid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setuid", module = true, visibility = PRIVATE)
         public static IRubyObject setuid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return uid_set(context.getRuntime(), arg);
         }
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject abort(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.abort(context, recv, args);
     }
 
     @JRubyMethod(name = "exit!", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.exit_bang(recv, args);
     }
 
     @JRubyMethod(name = "groups", module = true, visibility = PRIVATE)
     public static IRubyObject groups(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#groups not yet implemented");
     }
 
     @JRubyMethod(name = "setrlimit", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject setrlimit(IRubyObject recv, IRubyObject[] args) {
         throw recv.getRuntime().newNotImplementedError("Process#setrlimit not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject getpgrp(IRubyObject recv) {
         return getpgrp(recv.getRuntime());
     }
     @JRubyMethod(name = "getpgrp", module = true, visibility = PRIVATE)
     public static IRubyObject getpgrp(ThreadContext context, IRubyObject recv) {
         return getpgrp(context.getRuntime());
     }
     public static IRubyObject getpgrp(Ruby runtime) {
         return runtime.newFixnum(runtime.getPosix().getpgrp());
     }
 
     @JRubyMethod(name = "groups=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject groups_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#groups not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject waitpid(IRubyObject recv, IRubyObject[] args) {
         return waitpid(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "waitpid", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject waitpid(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid(context.getRuntime(), args);
     }
     public static IRubyObject waitpid(Ruby runtime, IRubyObject[] args) {
         int pid = -1;
         int flags = 0;
         if (args.length > 0) {
             pid = (int)args[0].convertToInteger().getLongValue();
         }
         if (args.length > 1) {
             flags = (int)args[1].convertToInteger().getLongValue();
         }
         
         int[] status = new int[1];
         runtime.getPosix().errno(0);
         pid = runtime.getPosix().waitpid(pid, status, flags);
         raiseErrnoIfSet(runtime, ECHILD);
         
         runtime.getCurrentContext().setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, status[0]));
         return runtime.newFixnum(pid);
     }
 
     private interface NonNativeErrno {
         public int handle(Ruby runtime, int result);
     }
 
     private static final NonNativeErrno ECHILD = new NonNativeErrno() {
         public int handle(Ruby runtime, int result) {
             throw runtime.newErrnoECHILDError();
         }
     };
 
     @Deprecated
     public static IRubyObject wait(IRubyObject recv, IRubyObject[] args) {
         return wait(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "wait", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject wait(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return wait(context.getRuntime(), args);
     }
     public static IRubyObject wait(Ruby runtime, IRubyObject[] args) {
         
         if (args.length > 0) {
             return waitpid(runtime, args);
         }
         
         int[] status = new int[1];
         runtime.getPosix().errno(0);
         int pid = runtime.getPosix().wait(status);
         raiseErrnoIfSet(runtime, ECHILD);
         
         runtime.getCurrentContext().setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, status[0]));
         return runtime.newFixnum(pid);
     }
 
 
     @Deprecated
     public static IRubyObject waitall(IRubyObject recv) {
         return waitall(recv.getRuntime());
     }
     @JRubyMethod(name = "waitall", module = true, visibility = PRIVATE)
     public static IRubyObject waitall(ThreadContext context, IRubyObject recv) {
         return waitall(context.getRuntime());
     }
     public static IRubyObject waitall(Ruby runtime) {
         POSIX posix = runtime.getPosix();
         RubyArray results = runtime.newArray();
         
         int[] status = new int[1];
         int result = posix.wait(status);
         while (result != -1) {
             results.append(runtime.newArray(runtime.newFixnum(result), RubyProcess.RubyStatus.newProcessStatus(runtime, status[0])));
             result = posix.wait(status);
         }
         
         return results;
     }
 
     @Deprecated
     public static IRubyObject setsid(IRubyObject recv) {
         return setsid(recv.getRuntime());
     }
     @JRubyMethod(name = "setsid", module = true, visibility = PRIVATE)
     public static IRubyObject setsid(ThreadContext context, IRubyObject recv) {
         return setsid(context.getRuntime());
     }
     public static IRubyObject setsid(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setsid()));
     }
 
     @Deprecated
     public static IRubyObject setpgrp(IRubyObject recv) {
         return setpgrp(recv.getRuntime());
     }
     @JRubyMethod(name = "setpgrp", module = true, visibility = PRIVATE)
     public static IRubyObject setpgrp(ThreadContext context, IRubyObject recv) {
         return setpgrp(context.getRuntime());
     }
     public static IRubyObject setpgrp(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setpgid(0, 0)));
     }
 
     @Deprecated
     public static IRubyObject egid_set(IRubyObject recv, IRubyObject arg) {
         return egid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "egid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject egid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return egid_set(context.getRuntime(), arg);
     }
     public static IRubyObject egid_set(Ruby runtime, IRubyObject arg) {
         checkErrno(runtime, runtime.getPosix().setegid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject euid(IRubyObject recv) {
         return euid(recv.getRuntime());
     }
     @JRubyMethod(name = "euid", module = true, visibility = PRIVATE)
     public static IRubyObject euid(ThreadContext context, IRubyObject recv) {
         return euid(context.getRuntime());
     }
     public static IRubyObject euid(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().geteuid()));
     }
 
     @Deprecated
     public static IRubyObject uid_set(IRubyObject recv, IRubyObject arg) {
         return uid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "uid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject uid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return uid_set(context.getRuntime(), arg);
     }
     public static IRubyObject uid_set(Ruby runtime, IRubyObject arg) {
         checkErrno(runtime, runtime.getPosix().setuid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject gid(IRubyObject recv) {
         return gid(recv.getRuntime());
     }
     @JRubyMethod(name = "gid", module = true, visibility = PRIVATE)
     public static IRubyObject gid(ThreadContext context, IRubyObject recv) {
         return gid(context.getRuntime());
     }
     public static IRubyObject gid(Ruby runtime) {
         if (Platform.IS_WINDOWS) {
             // MRI behavior on Windows
             return RubyFixnum.zero(runtime);
         }
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getgid()));
     }
 
     @JRubyMethod(name = "maxgroups", module = true, visibility = PRIVATE)
     public static IRubyObject maxgroups(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#maxgroups not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject getpriority(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return getpriority(recv.getRuntime(), arg1, arg2);
     }
     @JRubyMethod(name = "getpriority", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject getpriority(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return getpriority(context.getRuntime(), arg1, arg2);
     }
     public static IRubyObject getpriority(Ruby runtime, IRubyObject arg1, IRubyObject arg2) {
         int which = (int)arg1.convertToInteger().getLongValue();
         int who = (int)arg2.convertToInteger().getLongValue();
         int result = checkErrno(runtime, runtime.getPosix().getpriority(which, who));
         
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject uid(IRubyObject recv) {
         return uid(recv.getRuntime());
     }
     @JRubyMethod(name = "uid", module = true, visibility = PRIVATE)
     public static IRubyObject uid(ThreadContext context, IRubyObject recv) {
         return uid(context.getRuntime());
     }
     public static IRubyObject uid(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getuid()));
     }
 
     @Deprecated
     public static IRubyObject waitpid2(IRubyObject recv, IRubyObject[] args) {
         return waitpid2(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "waitpid2", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject waitpid2(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid2(context.getRuntime(), args);
     }
     public static IRubyObject waitpid2(Ruby runtime, IRubyObject[] args) {
         int pid = -1;
         int flags = 0;
         if (args.length > 0) {
             pid = (int)args[0].convertToInteger().getLongValue();
         }
         if (args.length > 1) {
             flags = (int)args[1].convertToInteger().getLongValue();
         }
         
         int[] status = new int[1];
         pid = checkErrno(runtime, runtime.getPosix().waitpid(pid, status, flags), ECHILD);
         
         return runtime.newArray(runtime.newFixnum(pid), RubyProcess.RubyStatus.newProcessStatus(runtime, status[0]));
     }
 
     @JRubyMethod(name = "initgroups", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject initgroups(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         throw recv.getRuntime().newNotImplementedError("Process#initgroups not yet implemented");
     }
 
     @JRubyMethod(name = "maxgroups=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject maxgroups_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#maxgroups_set not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject ppid(IRubyObject recv) {
         return ppid(recv.getRuntime());
     }
     @JRubyMethod(name = "ppid", module = true, visibility = PRIVATE)
     public static IRubyObject ppid(ThreadContext context, IRubyObject recv) {
         return ppid(context.getRuntime());
     }
     public static IRubyObject ppid(Ruby runtime) {
         int result = checkErrno(runtime, runtime.getPosix().getppid());
 
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject gid_set(IRubyObject recv, IRubyObject arg) {
         return gid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "gid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject gid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return gid_set(context.getRuntime(), arg);
     }
     public static IRubyObject gid_set(Ruby runtime, IRubyObject arg) {
         int result = checkErrno(runtime, runtime.getPosix().setgid((int)arg.convertToInteger().getLongValue()));
 
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject wait2(IRubyObject recv, IRubyObject[] args) {
         return waitpid2(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "wait2", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject wait2(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid2(context.getRuntime(), args);
     }
 
     @Deprecated
     public static IRubyObject euid_set(IRubyObject recv, IRubyObject arg) {
         return euid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "euid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject euid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return euid_set(context.getRuntime(), arg);
     }
     public static IRubyObject euid_set(Ruby runtime, IRubyObject arg) {
         checkErrno(runtime, runtime.getPosix().seteuid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject setpriority(IRubyObject recv, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return setpriority(recv.getRuntime(), arg1, arg2, arg3);
     }
     @JRubyMethod(name = "setpriority", required = 3, module = true, visibility = PRIVATE)
     public static IRubyObject setpriority(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return setpriority(context.getRuntime(), arg1, arg2, arg3);
     }
     public static IRubyObject setpriority(Ruby runtime, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         int which = (int)arg1.convertToInteger().getLongValue();
         int who = (int)arg2.convertToInteger().getLongValue();
         int prio = (int)arg3.convertToInteger().getLongValue();
         runtime.getPosix().errno(0);
         int result = checkErrno(runtime, runtime.getPosix().setpriority(which, who, prio));
         
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject setpgid(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return setpgid(recv.getRuntime(), arg1, arg2);
     }
     @JRubyMethod(name = "setpgid", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject setpgid(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return setpgid(context.getRuntime(), arg1, arg2);
     }
     public static IRubyObject setpgid(Ruby runtime, IRubyObject arg1, IRubyObject arg2) {
         int pid = (int)arg1.convertToInteger().getLongValue();
         int gid = (int)arg2.convertToInteger().getLongValue();
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setpgid(pid, gid)));
     }
 
     @Deprecated
     public static IRubyObject getpgid(IRubyObject recv, IRubyObject arg) {
         return getpgid(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "getpgid", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject getpgid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return getpgid(context.getRuntime(), arg);
     }
     public static IRubyObject getpgid(Ruby runtime, IRubyObject arg) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getpgid((int)arg.convertToInteger().getLongValue())));
     }
 
     @Deprecated
     public static IRubyObject getrlimit(IRubyObject recv, IRubyObject arg) {
         return getrlimit(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "getrlimit", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject getrlimit(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return getrlimit(context.getRuntime(), arg);
     }
     public static IRubyObject getrlimit(Ruby runtime, IRubyObject arg) {
         throw runtime.newNotImplementedError("Process#getrlimit not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject egid(IRubyObject recv) {
         return egid(recv.getRuntime());
     }
     @JRubyMethod(name = "egid", module = true, visibility = PRIVATE)
     public static IRubyObject egid(ThreadContext context, IRubyObject recv) {
         return egid(context.getRuntime());
     }
     public static IRubyObject egid(Ruby runtime) {
         if (Platform.IS_WINDOWS) {
             // MRI behavior on Windows
             return RubyFixnum.zero(runtime);
         }
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getegid()));
     }
     
     private static String[] signals = new String[] {"EXIT", "HUP", "INT", "QUIT", "ILL", "TRAP", 
         "ABRT", "POLL", "FPE", "KILL", "BUS", "SEGV", "SYS", "PIPE", "ALRM", "TERM", "URG", "STOP",
         "TSTP", "CONT", "CHLD", "TTIN", "TTOU", "XCPU", "XFSZ", "VTALRM", "PROF", "USR1", "USR2"};
     
     private static int parseSignalString(Ruby runtime, String value) {
         int startIndex = 0;
         boolean negative = value.startsWith("-");
         
         if (negative) startIndex++;
         
         boolean signalString = value.startsWith("SIG", startIndex);
         
         if (signalString) startIndex += 3;
        
         String signalName = value.substring(startIndex);
         
         // FIXME: This table will get moved into POSIX library so we can get all actual supported
         // signals.  This is a quick fix to support basic signals until that happens.
         for (int i = 0; i < signals.length; i++) {
             if (signals[i].equals(signalName)) return negative ? -i : i;
         }
         
         throw runtime.newArgumentError("unsupported name `SIG" + signalName + "'");
     }
 
     @Deprecated
     public static IRubyObject kill(IRubyObject recv, IRubyObject[] args) {
         return kill(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "kill", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject kill(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return kill(context.getRuntime(), args);
     }
     public static IRubyObject kill(Ruby runtime, IRubyObject[] args) {
         if (args.length < 2) {
             throw runtime.newArgumentError("wrong number of arguments -- kill(sig, pid...)");
         }
 
         // Windows does not support these functions, so we won't even try
         // This also matches Ruby behavior for JRUBY-2353.
         if (Platform.IS_WINDOWS) {
             return runtime.getNil();
         }
         
         int signal;
         if (args[0] instanceof RubyFixnum) {
             signal = (int) ((RubyFixnum) args[0]).getLongValue();
         } else if (args[0] instanceof RubySymbol) {
             signal = parseSignalString(runtime, args[0].toString());
         } else if (args[0] instanceof RubyString) {
             signal = parseSignalString(runtime, args[0].toString());
         } else {
             signal = parseSignalString(runtime, args[0].checkStringType().toString());
         }
 
         boolean processGroupKill = signal < 0;
         
         if (processGroupKill) signal = -signal;
         
         POSIX posix = runtime.getPosix();
         for (int i = 1; i < args.length; i++) {
             int pid = RubyNumeric.num2int(args[i]);
 
             // FIXME: It may be possible to killpg on systems which support it.  POSIX library
             // needs to tell whether a particular method works or not
             if (pid == 0) pid = runtime.getPosix().getpid();
             checkErrno(runtime, posix.kill(processGroupKill ? -pid : pid, signal));
         }
         
         return runtime.newFixnum(args.length - 1);
 
     }
 
     @JRubyMethod(name = "detach", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject detach(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         final int pid = (int)arg.convertToInteger().getLongValue();
         Ruby runtime = context.getRuntime();
         
         BlockCallback callback = new BlockCallback() {
             public IRubyObject call(ThreadContext context, IRubyObject[] args, Block block) {
                 int[] status = new int[1];
                 Ruby runtime = context.runtime;
                 int result = checkErrno(runtime, runtime.getPosix().waitpid(pid, status, 0));
                 
                 return runtime.newFixnum(result);
             }
         };
         
         return RubyThread.newInstance(
                 runtime.getThread(),
                 IRubyObject.NULL_ARRAY,
                 CallBlock.newCallClosure(recv, (RubyModule)recv, Arity.NO_ARGUMENTS, callback, context));
     }
 
     @Deprecated
     public static IRubyObject times(IRubyObject recv, Block unusedBlock) {
         return times(recv.getRuntime());
     }
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject times(ThreadContext context, IRubyObject recv, Block unusedBlock) {
         return times(context.getRuntime());
     }
     public static IRubyObject times(Ruby runtime) {
         double currentTime = System.currentTimeMillis() / 1000.0;
         double startTime = runtime.getStartTime() / 1000.0;
         RubyFloat zero = runtime.newFloat(0.0);
         return RubyStruct.newStruct(runtime.getTmsStruct(), 
                 new IRubyObject[] { runtime.newFloat(currentTime - startTime), zero, zero, zero }, 
                 Block.NULL_BLOCK);
     }
 
     @Deprecated
     public static IRubyObject pid(IRubyObject recv) {
         return pid(recv.getRuntime());
     }
     @JRubyMethod(name = "pid", module = true, visibility = PRIVATE)
     public static IRubyObject pid(ThreadContext context, IRubyObject recv) {
         return pid(context.getRuntime());
     }
     public static IRubyObject pid(Ruby runtime) {
         return runtime.newFixnum(runtime.getPosix().getpid());
     }
     
     @JRubyMethod(name = "fork", module = true, visibility = PRIVATE)
     public static IRubyObject fork(ThreadContext context, IRubyObject recv, Block block) {
         return RubyKernel.fork(context, recv, block);
     }
 
     @JRubyMethod(name = "spawn", required = 1, rest = true, module = true, compat = CompatVersion.RUBY1_9)
     public static RubyFixnum spawn(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
-        long pid = ShellLauncher.runWithoutWait(runtime, args);
+        long pid = ShellLauncher.runExternalWithoutWait(runtime, args);
         return RubyFixnum.newFixnum(runtime, pid);
     }
     
     @JRubyMethod(name = "exit", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.exit(recv, args);
     }
     
     private static final NonNativeErrno IGNORE = new NonNativeErrno() {
         public int handle(Ruby runtime, int result) {return result;}
     };
 
     private static int checkErrno(Ruby runtime, int result) {
         return checkErrno(runtime, result, IGNORE);
     }
 
     private static int checkErrno(Ruby runtime, int result, NonNativeErrno nonNative) {
         if (result == -1) {
             if (runtime.getPosix().isNative()) {
                 raiseErrnoIfSet(runtime, nonNative);
             } else {
                 nonNative.handle(runtime, result);
             }
         }
         return result;
     }
 
     private static void raiseErrnoIfSet(Ruby runtime, NonNativeErrno nonNative) {
         if (runtime.getPosix().errno() != 0) {
             throw runtime.newErrnoFromInt(runtime.getPosix().errno());
         }
     }
 }
diff --git a/src/org/jruby/util/ShellLauncher.java b/src/org/jruby/util/ShellLauncher.java
index 353c7ea76a..82993ac9e5 100644
--- a/src/org/jruby/util/ShellLauncher.java
+++ b/src/org/jruby/util/ShellLauncher.java
@@ -1,1377 +1,1396 @@
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
  * Copyright (C) 2007-2011 JRuby Team <team@jruby.org>
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
 
 import com.kenai.jaffl.FFIProvider;
 import static java.lang.System.out;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.FilterOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PipedInputStream;
 import java.io.PipedOutputStream;
 import java.io.PrintStream;
 import java.lang.reflect.Field;
 import java.nio.ByteBuffer;
 import java.nio.channels.FileChannel;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Main;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyHash;
 import org.jruby.RubyIO;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.ext.posix.util.FieldAccess;
 import org.jruby.ext.posix.util.Platform;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.libraries.RbConfigLibrary;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.io.ModeFlags;
 
 /**
  * This mess of a class is what happens when all Java gives you is
  * Runtime.getRuntime().exec(). Thanks dude, that really helped.
  * @author nicksieger
  */
 public class ShellLauncher {
     private static final boolean DEBUG = false;
 
     private static final String PATH_ENV = "PATH";
 
     // from MRI -- note the unixy file separators
     private static final String[] DEFAULT_PATH =
         { "/usr/local/bin", "/usr/ucb", "/usr/bin", "/bin" };
 
     private static final String[] WINDOWS_EXE_SUFFIXES =
         { ".exe", ".com", ".bat", ".cmd" }; // the order is important
 
     private static final String[] WINDOWS_INTERNAL_CMDS = {
         "assoc", "break", "call", "cd", "chcp",
         "chdir", "cls", "color", "copy", "ctty", "date", "del", "dir", "echo", "endlocal",
         "erase", "exit", "for", "ftype", "goto", "if", "lfnfor", "lh", "lock", "md", "mkdir",
         "move", "path", "pause", "popd", "prompt", "pushd", "rd", "rem", "ren", "rename",
         "rmdir", "set", "setlocal", "shift", "start", "time", "title", "truename", "type",
         "unlock", "ver", "verify", "vol", };
 
     // TODO: better check is needed, with quoting/escaping
     private static final Pattern SHELL_METACHARACTER_PATTERN =
         Pattern.compile("[*?{}\\[\\]<>()~&|$;'`\\\\\"\\n]");
 
     private static final Pattern WIN_ENVVAR_PATTERN = Pattern.compile("%\\w+%");
 
     private static class ScriptThreadProcess extends Process implements Runnable {
         private final String[] argArray;
         private final String[] env;
         private final File pwd;
         private final boolean pipedStreams;
         private final PipedInputStream processOutput;
         private final PipedInputStream processError;
         private final PipedOutputStream processInput;
 
         private RubyInstanceConfig config;
         private Thread processThread;
         private int result;
         private Ruby parentRuntime;
 
         public ScriptThreadProcess(Ruby parentRuntime, final String[] argArray, final String[] env, final File dir) {
             this(parentRuntime, argArray, env, dir, true);
         }
 
         public ScriptThreadProcess(Ruby parentRuntime, final String[] argArray, final String[] env, final File dir, final boolean pipedStreams) {
             this.parentRuntime = parentRuntime;
             this.argArray = argArray;
             this.env = env;
             this.pwd = dir;
             this.pipedStreams = pipedStreams;
             if (pipedStreams) {
                 processOutput = new PipedInputStream();
                 processError = new PipedInputStream();
                 processInput = new PipedOutputStream();
             } else {
                 processOutput = processError = null;
                 processInput = null;
             }
         }
         public void run() {
             try {
                 this.result = (new Main(config).run(argArray)).getStatus();
             } catch (Throwable throwable) {
                 throwable.printStackTrace(this.config.getError());
                 this.result = -1;
             } finally {
                 this.config.getOutput().close();
                 this.config.getError().close();
                 try {this.config.getInput().close();} catch (IOException ioe) {}
             }
         }
 
         private Map<String, String> environmentMap(String[] env) {
             Map<String, String> m = new HashMap<String, String>();
             for (int i = 0; i < env.length; i++) {
                 String[] kv = env[i].split("=", 2);
                 m.put(kv[0], kv[1]);
             }
             return m;
         }
 
         public void start() throws IOException {
             config = new RubyInstanceConfig(parentRuntime.getInstanceConfig()) {{
                 setEnvironment(environmentMap(env));
                 setCurrentDirectory(pwd.toString());
             }};
             if (pipedStreams) {
                 config.setInput(new PipedInputStream(processInput));
                 config.setOutput(new PrintStream(new PipedOutputStream(processOutput)));
                 config.setError(new PrintStream(new PipedOutputStream(processError)));
             }
             String procName = "piped";
             if (argArray.length > 0) {
                 procName = argArray[0];
             }
             processThread = new Thread(this, "ScriptThreadProcess: " + procName);
             processThread.setDaemon(true);
             processThread.start();
         }
 
         public OutputStream getOutputStream() {
             return processInput;
         }
 
         public InputStream getInputStream() {
             return processOutput;
         }
 
         public InputStream getErrorStream() {
             return processError;
         }
 
         public int waitFor() throws InterruptedException {
             processThread.join();
             return result;
         }
 
         public int exitValue() {
             return result;
         }
 
         public void destroy() {
             if (pipedStreams) {
                 closeStreams();
             }
             processThread.interrupt();
         }
 
         private void closeStreams() {
             try { processInput.close(); } catch (IOException io) {}
             try { processOutput.close(); } catch (IOException io) {}
             try { processError.close(); } catch (IOException io) {}
         }
     }
 
     private static String[] getCurrentEnv(Ruby runtime) {
         return getCurrentEnv(runtime, null);
     }
 
     private static String[] getCurrentEnv(Ruby runtime, Map mergeEnv) {
         RubyHash hash = (RubyHash)runtime.getObject().fastGetConstant("ENV");
         String[] ret;
         
         if (mergeEnv != null && !mergeEnv.isEmpty()) {
             ret = new String[hash.size() + mergeEnv.size()];
         } else {
             ret = new String[hash.size()];
         }
 
         int i=0;
         for(Map.Entry e : (Set<Map.Entry>)hash.directEntrySet()) {
             ret[i] = e.getKey().toString() + "=" + e.getValue().toString();
             i++;
         }
         if (mergeEnv != null) for(Map.Entry e : (Set<Map.Entry>)mergeEnv.entrySet()) {
             ret[i] = e.getKey().toString() + "=" + e.getValue().toString();
             i++;
         }
 
         return ret;
     }
 
     private static boolean filenameIsPathSearchable(String fname, boolean forExec) {
         boolean isSearchable = true;
         if (fname.startsWith("/")   ||
             fname.startsWith("./")  ||
             fname.startsWith("../") ||
             (forExec && (fname.indexOf("/") != -1))) {
             isSearchable = false;
         }
         if (Platform.IS_WINDOWS) {
             if (fname.startsWith("\\")  ||
                 fname.startsWith(".\\") ||
                 fname.startsWith("..\\") ||
                 ((fname.length() > 2) && fname.startsWith(":",1)) ||
                 (forExec && (fname.indexOf("\\") != -1))) {
                 isSearchable = false;
             }
         }
         return isSearchable;
     }
 
     private static File tryFile(Ruby runtime, String fdir, String fname) {
         File pathFile;
         if (fdir == null) {
             pathFile = new File(fname);
         } else {
             pathFile = new File(fdir, fname);
         }
 
         if (!pathFile.isAbsolute()) {
             pathFile = new File(runtime.getCurrentDirectory(), pathFile.getPath());
         }
 
         log(runtime, "Trying file " + pathFile);
         if (pathFile.exists()) {
             return pathFile;
         } else {
             return null;
         }
     }
 
     private static boolean withExeSuffix(String fname) {
         String lowerCaseFname = fname.toLowerCase();
         for (String suffix : WINDOWS_EXE_SUFFIXES) {
             if (lowerCaseFname.endsWith(suffix)) {
                 return true;
             }
         }
         return false;
     }
 
     private static File isValidFile(Ruby runtime, String fdir, String fname, boolean isExec) {
         File validFile = null;
         if (isExec && Platform.IS_WINDOWS) {
             if (withExeSuffix(fname)) {
                 validFile = tryFile(runtime, fdir, fname);
             } else {
                 for (String suffix: WINDOWS_EXE_SUFFIXES) {
                     validFile = tryFile(runtime, fdir, fname + suffix);
                     if (validFile != null) {
                         // found a valid file, no need to search further
                         break;
                     }
                 }
             }
         } else {
             File pathFile = tryFile(runtime, fdir, fname);
             if (pathFile != null) {
                 if (isExec) {
                     if (!pathFile.isDirectory()) {
                         String pathFileStr = pathFile.getAbsolutePath();
                         POSIX posix = runtime.getPosix();
                         if (posix.stat(pathFileStr).isExecutable()) {
                             validFile = pathFile;
                         }
                     }
                 } else {
                     validFile = pathFile;
                 }
             }
         }
         return validFile;
     }
 
     private static File isValidFile(Ruby runtime, String fname, boolean isExec) {
         String fdir = null;
         return isValidFile(runtime, fdir, fname, isExec);
     }
 
     private static File findPathFile(Ruby runtime, String fname, String[] path, boolean isExec) {
         File pathFile = null;
         boolean doPathSearch = filenameIsPathSearchable(fname, isExec);
         if (doPathSearch) {
             for (String fdir: path) {
                 // NOTE: Jruby's handling of tildes is more complete than
                 //       MRI's, which can't handle user names after the tilde
                 //       when searching the executable path
                 pathFile = isValidFile(runtime, fdir, fname, isExec);
                 if (pathFile != null) {
                     break;
                 }
             }
         } else {
             pathFile = isValidFile(runtime, fname, isExec);
         }
         return pathFile;
     }
 
     private static File findPathExecutable(Ruby runtime, String fname) {
         RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
         IRubyObject pathObject = env.op_aref(runtime.getCurrentContext(), RubyString.newString(runtime, PATH_ENV));
         String[] pathNodes = null;
         if (pathObject == null) {
             pathNodes = DEFAULT_PATH; // ASSUME: not modified by callee
         }
         else {
             String pathSeparator = System.getProperty("path.separator");
             String path = pathObject.toString();
             if (Platform.IS_WINDOWS) {
                 // Windows-specific behavior
                 path = "." + pathSeparator + path;
             }
             pathNodes = path.split(pathSeparator);
         }
         return findPathFile(runtime, fname, pathNodes, true);
     }
 
     public static int runAndWait(Ruby runtime, IRubyObject[] rawArgs) {
         return runAndWait(runtime, rawArgs, runtime.getOutputStream());
     }
 
     public static long runWithoutWait(Ruby runtime, IRubyObject[] rawArgs) {
         return runWithoutWait(runtime, rawArgs, runtime.getOutputStream());
     }
 
+    public static long runExternalWithoutWait(Ruby runtime, IRubyObject[] rawArgs) {
+        return runWithoutWait(runtime, rawArgs, runtime.getOutputStream());
+    }
+
     public static int execAndWait(Ruby runtime, IRubyObject[] rawArgs) {
         File pwd = new File(runtime.getCurrentDirectory());
         LaunchConfig cfg = new LaunchConfig(runtime, rawArgs, true);
 
         if (cfg.shouldRunInProcess()) {
             log(runtime, "ExecAndWait in-process");
             try {
                 // exec needs to behave differently in-process, because it's technically
                 // supposed to replace the calling process. So if we're supposed to run
                 // in-process, we allow it to use the default streams and not use
                 // pumpers at all. See JRUBY-2156 and JRUBY-2154.
                 ScriptThreadProcess ipScript = new ScriptThreadProcess(
                         runtime, cfg.getExecArgs(), getCurrentEnv(runtime), pwd, false);
                 ipScript.start();
                 return ipScript.waitFor();
             } catch (IOException e) {
                 throw runtime.newIOErrorFromException(e);
             } catch (InterruptedException e) {
                 throw runtime.newThreadError("unexpected interrupt");
             }
         } else {
             return runAndWait(runtime, rawArgs);
         }
     }
 
     public static int runAndWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output) {
         return runAndWait(runtime, rawArgs, output, true);
     }
 
     public static int runAndWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output, boolean doExecutableSearch) {
         OutputStream error = runtime.getErrorStream();
         InputStream input = runtime.getInputStream();
         try {
             Process aProcess = run(runtime, rawArgs, doExecutableSearch);
             handleStreams(runtime, aProcess, input, output, error);
             return aProcess.waitFor();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
-    public static long runWithoutWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output) {
+    private static long runWithoutWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output) {
         OutputStream error = runtime.getErrorStream();
         try {
             Process aProcess = run(runtime, rawArgs, true);
             handleStreamsNonblocking(runtime, aProcess, output, error);
             return getPidFromProcess(aProcess);
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
+    private static long runExternalWithoutWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output) {
+        OutputStream error = runtime.getErrorStream();
+        try {
+            Process aProcess = run(runtime, rawArgs, true, true);
+            handleStreamsNonblocking(runtime, aProcess, output, error);
+            return getPidFromProcess(aProcess);
+        } catch (IOException e) {
+            throw runtime.newIOErrorFromException(e);
+        }
+    }
+
     public static long getPidFromProcess(Process process) {
         if (process instanceof ScriptThreadProcess) {
             return process.hashCode();
         } else if (process instanceof POpenProcess) {
             return reflectPidFromProcess(((POpenProcess)process).getChild());
         } else {
             return reflectPidFromProcess(process);
         }
     }
     
     private static final Class UNIXProcess;
     private static final Field UNIXProcess_pid;
     private static final Class ProcessImpl;
     private static final Field ProcessImpl_handle;
     private interface PidGetter { public long getPid(Process process); }
     private static final PidGetter PID_GETTER;
     
     static {
         // default PidGetter
         PidGetter pg = new PidGetter() {
             public long getPid(Process process) {
                 return process.hashCode();
             }
         };
         
         Class up = null;
         Field pid = null;
         try {
             up = Class.forName("java.lang.UNIXProcess");
             pid = up.getDeclaredField("pid");
             pid.setAccessible(true);
         } catch (Exception e) {
             // ignore and try windows version
         }
         UNIXProcess = up;
         UNIXProcess_pid = pid;
 
         Class pi = null;
         Field handle = null;
         try {
             pi = Class.forName("java.lang.ProcessImpl");
             handle = pi.getDeclaredField("handle");
             handle.setAccessible(true);
         } catch (Exception e) {
             // ignore and use hashcode
         }
         ProcessImpl = pi;
         ProcessImpl_handle = handle;
 
         if (UNIXProcess_pid != null) {
             if (ProcessImpl_handle != null) {
                 // try both
                 pg = new PidGetter() {
                     public long getPid(Process process) {
                         try {
                             if (UNIXProcess.isInstance(process)) {
                                 return (Integer)UNIXProcess_pid.get(process);
                             } else if (ProcessImpl.isInstance(process)) {
                                 Long hproc = (Long) ProcessImpl_handle.get(process);
                                 return WindowsFFI.getKernel32(FFIProvider.getProvider())
                                     .GetProcessId(new com.kenai.jaffl.NativeLong(hproc));
                             }
                         } catch (Exception e) {
                             // ignore and use hashcode
                         }
                         return process.hashCode();
                     }
                 };
             } else {
                 // just unix
                 pg = new PidGetter() {
                     public long getPid(Process process) {
                         try {
                             if (UNIXProcess.isInstance(process)) {
                                 return (Integer)UNIXProcess_pid.get(process);
                             }
                         } catch (Exception e) {
                             // ignore and use hashcode
                         }
                         return process.hashCode();
                     }
                 };
             }
         } else if (ProcessImpl_handle != null) {
             // just windows
             pg = new PidGetter() {
                 public long getPid(Process process) {
                     try {
                         if (ProcessImpl.isInstance(process)) {
                             Long hproc = (Long) ProcessImpl_handle.get(process);
                             return WindowsFFI.getKernel32(FFIProvider.getProvider())
                                 .GetProcessId(new com.kenai.jaffl.NativeLong(hproc));
                         }
 
                     } catch (Exception e) {
                         // ignore and use hashcode
                     }
                     return process.hashCode();
                 }
             };
         } else {
             // neither
             pg = new PidGetter() {
                 public long getPid(Process process) {
                     return process.hashCode();
                 }
             };
         }
         PID_GETTER = pg;
     }
 
     public static long reflectPidFromProcess(Process process) {
         return PID_GETTER.getPid(process);
     }
 
     public static Process run(Ruby runtime, IRubyObject string) throws IOException {
         return run(runtime, new IRubyObject[] {string}, false);
     }
 
     public static POpenProcess popen(Ruby runtime, IRubyObject string, ModeFlags modes) throws IOException {
         return new POpenProcess(popenShared(runtime, new IRubyObject[] {string}), runtime, modes);
     }
 
     public static POpenProcess popen(Ruby runtime, IRubyObject[] strings, Map env, ModeFlags modes) throws IOException {
         return new POpenProcess(popenShared(runtime, strings, env), runtime, modes);
     }
 
     public static POpenProcess popen3(Ruby runtime, IRubyObject[] strings) throws IOException {
         return new POpenProcess(popenShared(runtime, strings));
     }
 
     private static Process popenShared(Ruby runtime, IRubyObject[] strings) throws IOException {
         return popenShared(runtime, strings, null);
     }
 
     private static Process popenShared(Ruby runtime, IRubyObject[] strings, Map env) throws IOException {
         String shell = getShell(runtime);
         Process childProcess = null;
         File pwd = new File(runtime.getCurrentDirectory());
 
         try {
             String[] args = parseCommandLine(runtime.getCurrentContext(), runtime, strings);
             boolean useShell = false;
             for (String arg : args) useShell |= shouldUseShell(arg);
             
             // CON: popen is a case where I think we should just always shell out.
             if (strings.length == 1) {
                 if (useShell) {
                     // single string command, pass to sh to expand wildcards
                     String[] argArray = new String[3];
                     argArray[0] = shell;
                     argArray[1] = shell.endsWith("sh") ? "-c" : "/c";
                     argArray[2] = strings[0].asJavaString();
                     childProcess = Runtime.getRuntime().exec(argArray, getCurrentEnv(runtime, env), pwd);
                 } else {
                     childProcess = Runtime.getRuntime().exec(args, getCurrentEnv(runtime, env), pwd);
                 }
             } else {
                 if (useShell) {
                     String[] argArray = new String[args.length + 2];
                     argArray[0] = shell;
                     argArray[1] = shell.endsWith("sh") ? "-c" : "/c";
                     System.arraycopy(args, 0, argArray, 2, args.length);
                     childProcess = Runtime.getRuntime().exec(argArray, getCurrentEnv(runtime, env), pwd);
                 } else {
                     // direct invocation of the command
                     childProcess = Runtime.getRuntime().exec(args, getCurrentEnv(runtime, env), pwd);
                 }
             }
         } catch (SecurityException se) {
             throw runtime.newSecurityError(se.getLocalizedMessage());
         }
 
         return childProcess;
     }
 
     /**
      * Unwrap all filtering streams between the given stream and its actual
      * unfiltered stream. This is primarily to unwrap streams that have
      * buffers that would interfere with interactivity.
      *
      * @param filteredStream The stream to unwrap
      * @return An unwrapped stream, presumably unbuffered
      */
     public static OutputStream unwrapBufferedStream(OutputStream filteredStream) {
         if (RubyInstanceConfig.NO_UNWRAP_PROCESS_STREAMS) return filteredStream;
         while (filteredStream instanceof FilterOutputStream) {
             try {
                 filteredStream = (OutputStream)
                     FieldAccess.getProtectedFieldValue(FilterOutputStream.class,
                         "out", filteredStream);
             } catch (Exception e) {
                 break; // break out if we've dug as deep as we can
             }
         }
         return filteredStream;
     }
 
     /**
      * Unwrap all filtering streams between the given stream and its actual
      * unfiltered stream. This is primarily to unwrap streams that have
      * buffers that would interfere with interactivity.
      *
      * @param filteredStream The stream to unwrap
      * @return An unwrapped stream, presumably unbuffered
      */
     public static InputStream unwrapBufferedStream(InputStream filteredStream) {
         if (RubyInstanceConfig.NO_UNWRAP_PROCESS_STREAMS) return filteredStream;
         while (filteredStream instanceof BufferedInputStream) {
             try {
                 filteredStream = (InputStream)
                     FieldAccess.getProtectedFieldValue(BufferedInputStream.class,
                         "in", filteredStream);
             } catch (Exception e) {
                 break; // break out if we've dug as deep as we can
             }
         }
         return filteredStream;
     }
 
     public static class POpenProcess extends Process {
         private final Process child;
 
         // real stream references, to keep them from being GCed prematurely
         private InputStream realInput;
         private OutputStream realOutput;
         private InputStream realInerr;
 
         private InputStream input;
         private OutputStream output;
         private InputStream inerr;
         private FileChannel inputChannel;
         private FileChannel outputChannel;
         private FileChannel inerrChannel;
         private Pumper inputPumper;
         private Pumper inerrPumper;
         private Pumper outputPumper;
 
         public POpenProcess(Process child, Ruby runtime, ModeFlags modes) {
             this.child = child;
 
             if (modes.isWritable()) {
                 prepareOutput(child);
             } else {
                 // close process output
                 // See JRUBY-3405; hooking up to parent process stdin caused
                 // problems for IRB etc using stdin.
                 try {child.getOutputStream().close();} catch (IOException ioe) {}
             }
 
             if (modes.isReadable()) {
                 prepareInput(child);
             } else {
                 pumpInput(child, runtime);
             }
 
             pumpInerr(child, runtime);
         }
 
         public POpenProcess(Process child) {
             this.child = child;
 
             prepareOutput(child);
             prepareInput(child);
             prepareInerr(child);
         }
 
         @Override
         public OutputStream getOutputStream() {
             return output;
         }
 
         @Override
         public InputStream getInputStream() {
             return input;
         }
 
         @Override
         public InputStream getErrorStream() {
             return inerr;
         }
 
         public FileChannel getInput() {
             return inputChannel;
         }
 
         public FileChannel getOutput() {
             return outputChannel;
         }
 
         public FileChannel getError() {
             return inerrChannel;
         }
 
         public boolean hasOutput() {
             return output != null || outputChannel != null;
         }
 
         public Process getChild() {
             return child;
         }
 
         @Override
         public int waitFor() throws InterruptedException {
             if (outputPumper == null) {
                 try {
                     if (output != null) output.close();
                 } catch (IOException ioe) {
                     // ignore, we're on the way out
                 }
             } else {
                 outputPumper.quit();
             }
 
             int result = child.waitFor();
 
             return result;
         }
 
         @Override
         public int exitValue() {
             return child.exitValue();
         }
 
         @Override
         public void destroy() {
             try {
                 if (input != null) input.close();
                 if (inerr != null) inerr.close();
                 if (output != null) output.close();
                 if (inputChannel != null) inputChannel.close();
                 if (inerrChannel != null) inerrChannel.close();
                 if (outputChannel != null) outputChannel.close();
 
                 // processes seem to have some peculiar locking sequences, so we
                 // need to ensure nobody is trying to close/destroy while we are
                 synchronized (this) {
                     RubyIO.obliterateProcess(child);
                     if (inputPumper != null) synchronized(inputPumper) {inputPumper.quit();}
                     if (inerrPumper != null) synchronized(inerrPumper) {inerrPumper.quit();}
                     if (outputPumper != null) synchronized(outputPumper) {outputPumper.quit();}
                 }
             } catch (IOException ioe) {
                 throw new RuntimeException(ioe);
             }
         }
 
         private void prepareInput(Process child) {
             // popen callers wants to be able to read, provide subprocess in directly
             realInput = child.getInputStream();
             input = unwrapBufferedStream(realInput);
             if (input instanceof FileInputStream) {
                 inputChannel = ((FileInputStream) input).getChannel();
             } else {
                 inputChannel = null;
             }
             inputPumper = null;
         }
 
         private void prepareInerr(Process child) {
             // popen callers wants to be able to read, provide subprocess in directly
             realInerr = child.getErrorStream();
             inerr = unwrapBufferedStream(realInerr);
             if (inerr instanceof FileInputStream) {
                 inerrChannel = ((FileInputStream) inerr).getChannel();
             } else {
                 inerrChannel = null;
             }
             inerrPumper = null;
         }
 
         private void prepareOutput(Process child) {
             // popen caller wants to be able to write, provide subprocess out directly
             realOutput = child.getOutputStream();
             output = unwrapBufferedStream(realOutput);
             if (output instanceof FileOutputStream) {
                 outputChannel = ((FileOutputStream) output).getChannel();
             } else {
                 outputChannel = null;
             }
             outputPumper = null;
         }
 
         private void pumpInput(Process child, Ruby runtime) {
             // no read requested, hook up read to parents output
             InputStream childIn = unwrapBufferedStream(child.getInputStream());
             FileChannel childInChannel = null;
             if (childIn instanceof FileInputStream) {
                 childInChannel = ((FileInputStream) childIn).getChannel();
             }
             OutputStream parentOut = unwrapBufferedStream(runtime.getOut());
             FileChannel parentOutChannel = null;
             if (parentOut instanceof FileOutputStream) {
                 parentOutChannel = ((FileOutputStream) parentOut).getChannel();
             }
             if (childInChannel != null && parentOutChannel != null) {
                 inputPumper = new ChannelPumper(runtime, childInChannel, parentOutChannel, Pumper.Slave.IN, this);
             } else {
                 inputPumper = new StreamPumper(runtime, childIn, parentOut, false, Pumper.Slave.IN, this);
             }
             inputPumper.start();
             input = null;
             inputChannel = null;
         }
 
         private void pumpInerr(Process child, Ruby runtime) {
             // no read requested, hook up read to parents output
             InputStream childIn = unwrapBufferedStream(child.getErrorStream());
             FileChannel childInChannel = null;
             if (childIn instanceof FileInputStream) {
                 childInChannel = ((FileInputStream) childIn).getChannel();
             }
             OutputStream parentOut = unwrapBufferedStream(runtime.getOut());
             FileChannel parentOutChannel = null;
             if (parentOut instanceof FileOutputStream) {
                 parentOutChannel = ((FileOutputStream) parentOut).getChannel();
             }
             if (childInChannel != null && parentOutChannel != null) {
                 inerrPumper = new ChannelPumper(runtime, childInChannel, parentOutChannel, Pumper.Slave.IN, this);
             } else {
                 inerrPumper = new StreamPumper(runtime, childIn, parentOut, false, Pumper.Slave.IN, this);
             }
             inerrPumper.start();
             inerr = null;
             inerrChannel = null;
         }
     }
 
     private static class LaunchConfig {
         LaunchConfig(Ruby runtime, IRubyObject[] rawArgs, boolean doExecutableSearch) {
             this.runtime = runtime;
             this.rawArgs = rawArgs;
             this.doExecutableSearch = doExecutableSearch;
             shell = getShell(runtime);
             args = parseCommandLine(runtime.getCurrentContext(), runtime, rawArgs);
         }
 
         /**
          * Only run an in-process script if the script name has "ruby", ".rb",
          * or "irb" in the name.
          */
         private boolean shouldRunInProcess() {
             if (!runtime.getInstanceConfig().isRunRubyInProcess()) {
                 return false;
             }
 
             // Check for special shell characters [<>|] at the beginning
             // and end of each command word and don't run in process if we find them.
             for (int i = 0; i < args.length; i++) {
                 String c = args[i];
                 if (c.trim().length() == 0) {
                     continue;
                 }
                 char[] firstLast = new char[] {c.charAt(0), c.charAt(c.length()-1)};
                 for (int j = 0; j < firstLast.length; j++) {
                     switch (firstLast[j]) {
                     case '<': case '>': case '|': case ';':
                     case '*': case '?': case '{': case '}':
                     case '[': case ']': case '(': case ')':
                     case '~': case '&': case '$': case '"':
                     case '`': case '\n': case '\\': case '\'':
                         return false;
                     case '2':
                         if(c.length() > 1 && c.charAt(1) == '>') {
                             return false;
                         }
                     }
                 }
             }
 
             String command = args[0];
 
             if (Platform.IS_WINDOWS) {
                 command = command.toLowerCase();
             }
 
             // handle both slash types, \ and /.
             String[] slashDelimitedTokens = command.split("[/\\\\]");
             String finalToken = slashDelimitedTokens[slashDelimitedTokens.length - 1];
             boolean inProc = (finalToken.endsWith("ruby")
                     || (Platform.IS_WINDOWS && finalToken.endsWith("ruby.exe"))
                     || finalToken.endsWith(".rb")
                     || finalToken.endsWith("irb"));
 
             if (!inProc) {
                 return false;
             } else {
                 // snip off ruby or jruby command from list of arguments
                 // leave alone if the command is the name of a script
                 int startIndex = command.endsWith(".rb") ? 0 : 1;
                 if (command.trim().endsWith("irb")) {
                     startIndex = 0;
                     args[0] = runtime.getJRubyHome() + File.separator + "bin" + File.separator + "jirb";
                 }
                 execArgs = new String[args.length - startIndex];
                 System.arraycopy(args, startIndex, execArgs, 0, execArgs.length);
                 return true;
             }
         }
 
         /**
          * This hack is to work around a problem with cmd.exe on windows where it can't
          * interpret a filename with spaces in the first argument position as a command.
          * In that case it's better to try passing the bare arguments to runtime.exec.
          * On all other platforms we'll always run the command in the shell.
          */
         private boolean shouldRunInShell() {
             if (rawArgs.length != 1) {
                 // this is the case when exact executable and its parameters passed,
                 // in such cases MRI just executes it, without any shell.
                 return false;
             }
 
             // in one-arg form, we always use shell, except for Windows
             if (!Platform.IS_WINDOWS) return true;
 
             // now, deal with Windows
             if (shell == null) return false;
 
             // TODO: Better name for the method
             // Essentially, we just check for shell meta characters.
             // TODO: we use args here and rawArgs in upper method.
             for (String arg : args) {
                 if (!shouldVerifyPathExecutable(arg.trim())) {
                     return true;
                 }
             }
 
             // OK, so no shell meta-chars, now check that the command does exist
             executable = args[0].trim();
             executableFile = findPathExecutable(runtime, executable);
 
             // if the executable exists, start it directly with no shell
             if (executableFile != null) {
                 log(runtime, "Got it: " + executableFile);
                 // TODO: special processing for BAT/CMD files needed at all?
                 // if (isBatch(executableFile)) {
                 //    log(runtime, "This is a BAT/CMD file, will start in shell");
                 //    return true;
                 // }
                 return false;
             } else {
                 log(runtime, "Didn't find executable: " + executable);
             }
 
             if (isCmdBuiltin(executable)) {
                 cmdBuiltin = true;
                 return true;
             }
 
             // TODO: maybe true here?
             return false;
         }
 
         private void verifyExecutableForShell() {
             String cmdline = rawArgs[0].toString().trim();
             if (doExecutableSearch && shouldVerifyPathExecutable(cmdline) && !cmdBuiltin) {
                 verifyExecutable();
             }
 
             // now, prepare the exec args
 
             execArgs = new String[3];
             execArgs[0] = shell;
             execArgs[1] = shell.endsWith("sh") ? "-c" : "/c";
 
             if (Platform.IS_WINDOWS) {
                 // that's how MRI does it too
                 execArgs[2] = "\"" + cmdline + "\"";
             } else {
                 execArgs[2] = cmdline;
             }
         }
 
         private void verifyExecutableForDirect() {
             verifyExecutable();
             execArgs = args;
             try {
                 execArgs[0] = executableFile.getCanonicalPath();
             } catch (IOException ioe) {
                 // can't get the canonical path, will use as-is
             }
         }
 
         private void verifyExecutable() {
             if (executableFile == null) {
                 if (executable == null) {
                     executable = args[0].trim();
                 }
                 executableFile = findPathExecutable(runtime, executable);
             }
             if (executableFile == null) {
                 throw runtime.newErrnoENOENTError(executable);
             }
         }
 
         private String[] getExecArgs() {
             return execArgs;
         }
 
         private static boolean isBatch(File f) {
             String path = f.getPath();
             return (path.endsWith(".bat") || path.endsWith(".cmd"));
         }
 
         private boolean isCmdBuiltin(String cmd) {
             if (!shell.endsWith("sh")) { // assume cmd.exe
                 int idx = Arrays.binarySearch(WINDOWS_INTERNAL_CMDS, cmd.toLowerCase());
                 if (idx >= 0) {
                     log(runtime, "Found Windows shell's built-in command: " + cmd);
                     // Windows shell internal command, launch in shell then
                     return true;
                 }
             }
             return false;
         }
 
         /**
          * Checks a command string to determine if it has I/O redirection
          * characters that require it to be executed by a command interpreter.
          */
         private static boolean hasRedirection(String cmdline) {
             if (Platform.IS_WINDOWS) {
                  // Scan the string, looking for redirection characters (< or >), pipe
                  // character (|) or newline (\n) that are not in a quoted string
                  char quote = '\0';
                  for (int idx = 0; idx < cmdline.length();) {
                      char ptr = cmdline.charAt(idx);
                      switch (ptr) {
                      case '\'':
                      case '\"':
                          if (quote == '\0') {
                              quote = ptr;
                          } else if (quote == ptr) {
                              quote = '\0';
                          }
                          idx++;
                          break;
                      case '>':
                      case '<':
                      case '|':
                      case '\n':
                          if (quote == '\0') {
                              return true;
                          }
                          idx++;
                          break;
                      case '%':
                          // detect Windows environment variables: %ABC%
                          Matcher envVarMatcher = WIN_ENVVAR_PATTERN.matcher(cmdline.substring(idx));
                          if (envVarMatcher.find()) {
                              return true;
                          } else {
                              idx++;
                          }
                          break;
                      case '\\':
                          // slash serves as escape character
                          idx++;
                      default:
                          idx++;
                          break;
                      }
                  }
                  return false;
             } else {
                 // TODO: better check here needed, with quoting/escaping
                 Matcher metaMatcher = SHELL_METACHARACTER_PATTERN.matcher(cmdline);
                 return metaMatcher.find();
             }
         }
 
         // Should we try to verify the path executable, or just punt to the shell?
         private static boolean shouldVerifyPathExecutable(String cmdline) {
             boolean verifyPathExecutable = true;
             if (hasRedirection(cmdline)) {
                 return false;
             }
             return verifyPathExecutable;
         }
 
         private Ruby runtime;
         private boolean doExecutableSearch;
         private IRubyObject[] rawArgs;
         private String shell;
         private String[] args;
         private String[] execArgs;
         private boolean cmdBuiltin = false;
 
         private String executable;
         private File executableFile;
     }
 
     public static Process run(Ruby runtime, IRubyObject[] rawArgs, boolean doExecutableSearch) throws IOException {
+        return run(runtime, rawArgs, doExecutableSearch, false);
+    }
+
+    public static Process run(Ruby runtime, IRubyObject[] rawArgs, boolean doExecutableSearch, boolean forceExternalProcess) throws IOException {
         Process aProcess = null;
         File pwd = new File(runtime.getCurrentDirectory());
         LaunchConfig cfg = new LaunchConfig(runtime, rawArgs, doExecutableSearch);
 
         try {
-            if (cfg.shouldRunInProcess()) {
+            if (!forceExternalProcess && cfg.shouldRunInProcess()) {
                 log(runtime, "Launching in-process");
                 ScriptThreadProcess ipScript = new ScriptThreadProcess(
                         runtime, cfg.getExecArgs(), getCurrentEnv(runtime), pwd);
                 ipScript.start();
                 return ipScript;
             } else if (cfg.shouldRunInShell()) {
                 log(runtime, "Launching with shell");
                 // execute command with sh -c
                 // this does shell expansion of wildcards
                 cfg.verifyExecutableForShell();
                 aProcess = Runtime.getRuntime().exec(cfg.getExecArgs(), getCurrentEnv(runtime), pwd);
             } else {
                 log(runtime, "Launching directly (no shell)");
                 cfg.verifyExecutableForDirect();
                 aProcess = Runtime.getRuntime().exec(cfg.getExecArgs(), getCurrentEnv(runtime), pwd);
             }
         } catch (SecurityException se) {
             throw runtime.newSecurityError(se.getLocalizedMessage());
         }
         
         return aProcess;
     }
 
     private interface Pumper extends Runnable {
         public enum Slave { IN, OUT };
         public void start();
         public void quit();
     }
 
     private static class StreamPumper extends Thread implements Pumper {
         private final InputStream in;
         private final OutputStream out;
         private final boolean onlyIfAvailable;
         private final Object waitLock = new Object();
         private final Object sync;
         private final Slave slave;
         private volatile boolean quit;
         private final Ruby runtime;
 
         StreamPumper(Ruby runtime, InputStream in, OutputStream out, boolean avail, Slave slave, Object sync) {
             this.in = unwrapBufferedStream(in);
             this.out = unwrapBufferedStream(out);
             this.onlyIfAvailable = avail;
             this.slave = slave;
             this.sync = sync;
             this.runtime = runtime;
             setDaemon(true);
         }
         @Override
         public void run() {
             runtime.getCurrentContext().setEventHooksEnabled(false);
             byte[] buf = new byte[1024];
             int numRead;
             boolean hasReadSomething = false;
             try {
                 while (!quit) {
                     // The problem we trying to solve below: STDIN in Java
                     // is blocked and non-interruptible, so if we invoke read
                     // on it, we might never be able to interrupt such thread.
                     // So, we use in.available() to see if there is any input
                     // ready, and only then read it. But this approach can't
                     // tell whether the end of stream reached or not, so we
                     // might end up looping right at the end of the stream.
                     // Well, at least, we can improve the situation by checking
                     // if some input was ever available, and if so, not
                     // checking for available anymore, and just go to read.
                     if (onlyIfAvailable && !hasReadSomething) {
                         if (in.available() == 0) {
                             synchronized (waitLock) {
                                 waitLock.wait(10);
                             }
                             continue;
                         } else {
                             hasReadSomething = true;
                         }
                     }
 
                     if ((numRead = in.read(buf)) == -1) {
                         break;
                     }
                     out.write(buf, 0, numRead);
                 }
             } catch (Exception e) {
             } finally {
                 if (onlyIfAvailable) {
                     synchronized (sync) {
                         // We need to close the out, since some
                         // processes would just wait for the stream
                         // to be closed before they process its content,
                         // and produce the output. E.g.: "cat".
                         if (slave == Slave.OUT) {
                             // we only close out if it's the slave stream, to avoid
                             // closing a directly-mapped stream from parent process
                             try { out.close(); } catch (IOException ioe) {}
                         }
                     }
                 }
             }
         }
         public void quit() {
             this.quit = true;
             synchronized (waitLock) {
                 waitLock.notify();
             }
         }
     }
 
     private static class ChannelPumper extends Thread implements Pumper {
         private final FileChannel inChannel;
         private final FileChannel outChannel;
         private final Slave slave;
         private final Object sync;
         private volatile boolean quit;
         private final Ruby runtime;
 
         ChannelPumper(Ruby runtime, FileChannel inChannel, FileChannel outChannel, Slave slave, Object sync) {
             if (DEBUG) out.println("using channel pumper");
             this.inChannel = inChannel;
             this.outChannel = outChannel;
             this.slave = slave;
             this.sync = sync;
             this.runtime = runtime;
             setDaemon(true);
         }
         @Override
         public void run() {
             runtime.getCurrentContext().setEventHooksEnabled(false);
             ByteBuffer buf = ByteBuffer.allocateDirect(1024);
             buf.clear();
             try {
                 while (!quit && inChannel.isOpen() && outChannel.isOpen()) {
                     int read = inChannel.read(buf);
                     if (read == -1) break;
                     buf.flip();
                     outChannel.write(buf);
                     buf.clear();
                 }
             } catch (Exception e) {
             } finally {
                 // processes seem to have some peculiar locking sequences, so we
                 // need to ensure nobody is trying to close/destroy while we are
                 synchronized (sync) {
                     switch (slave) {
                     case OUT:
                         try { outChannel.close(); } catch (IOException ioe) {}
                         break;
                     case IN:
                         try { inChannel.close(); } catch (IOException ioe) {}
                     }
                 }
             }
         }
         public void quit() {
             interrupt();
             this.quit = true;
         }
     }
 
     private static void handleStreams(Ruby runtime, Process p, InputStream in, OutputStream out, OutputStream err) throws IOException {
         InputStream pOut = p.getInputStream();
         InputStream pErr = p.getErrorStream();
         OutputStream pIn = p.getOutputStream();
 
         StreamPumper t1 = new StreamPumper(runtime, pOut, out, false, Pumper.Slave.IN, p);
         StreamPumper t2 = new StreamPumper(runtime, pErr, err, false, Pumper.Slave.IN, p);
 
         // The assumption here is that the 'in' stream provides
         // proper available() support. If available() always
         // returns 0, we'll hang!
         StreamPumper t3 = new StreamPumper(runtime, in, pIn, true, Pumper.Slave.OUT, p);
 
         t1.start();
         t2.start();
         t3.start();
 
         try { t1.join(); } catch (InterruptedException ie) {}
         try { t2.join(); } catch (InterruptedException ie) {}
         t3.quit();
 
         try { err.flush(); } catch (IOException io) {}
         try { out.flush(); } catch (IOException io) {}
 
         try { pIn.close(); } catch (IOException io) {}
         try { pOut.close(); } catch (IOException io) {}
         try { pErr.close(); } catch (IOException io) {}
 
         // Force t3 to quit, just in case if it's stuck.
         // Note: On some platforms, even interrupt might not
         // have an effect if the thread is IO blocked.
         try { t3.interrupt(); } catch (SecurityException se) {}
     }
 
     private static void handleStreamsNonblocking(Ruby runtime, Process p, OutputStream out, OutputStream err) throws IOException {
         InputStream pOut = p.getInputStream();
         InputStream pErr = p.getErrorStream();
 
         StreamPumper t1 = new StreamPumper(runtime, pOut, out, false, Pumper.Slave.IN, p);
         StreamPumper t2 = new StreamPumper(runtime, pErr, err, false, Pumper.Slave.IN, p);
 
         t1.start();
         t2.start();
     }
 
     // TODO: move inside the LaunchConfig
     private static String[] parseCommandLine(ThreadContext context, Ruby runtime, IRubyObject[] rawArgs) {
         String[] args;
         if (rawArgs.length == 1) {
             synchronized (runtime.getLoadService()) {
                 runtime.getLoadService().require("jruby/path_helper");
             }
             RubyModule pathHelper = runtime.getClassFromPath("JRuby::PathHelper");
             RubyArray parts = (RubyArray) RuntimeHelpers.invoke(
                     context, pathHelper, "smart_split_command", rawArgs);
             args = new String[parts.getLength()];
             for (int i = 0; i < parts.getLength(); i++) {
                 args[i] = parts.entry(i).toString();
             }
         } else {
             args = new String[rawArgs.length];
             for (int i = 0; i < rawArgs.length; i++) {
                 args[i] = rawArgs[i].toString();
             }
         }
         return args;
     }
 
     private static String getShell(Ruby runtime) {
         return RbConfigLibrary.jrubyShell();
     }
 
     private static boolean shouldUseShell(String command) {
         boolean useShell = false;
         for (char c : command.toCharArray()) {
             if (c != ' ' && !Character.isLetter(c) && "*?{}[]<>()~&|\\$;'`\"\n".indexOf(c) != -1) {
                 useShell = true;
             }
         }
         if (Platform.IS_WINDOWS && command.charAt(0) == '@') {
             // JRUBY-5522
             useShell = true;
         }
         return useShell;
     }
 
     static void log(Ruby runtime, String msg) {
         if (RubyInstanceConfig.DEBUG_LAUNCHING) {
             runtime.getErr().println("ShellLauncher: " + msg);
         }
     }
 }
