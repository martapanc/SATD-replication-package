diff --git a/bench/bench_io_foreach.rb b/bench/bench_io_foreach.rb
index 6560a05bb6..5790071548 100644
--- a/bench/bench_io_foreach.rb
+++ b/bench/bench_io_foreach.rb
@@ -1,23 +1,27 @@
 require 'benchmark'
 
 MAX  = 1000
 BLOCKSIZE = 16 * 1024
-LINES = 10000
+LINE_SIZE = 10000
+LINES = 10
 FILE = 'io_test_bench_file.txt'
 
 File.open(FILE, 'w'){ |fh|
-   LINES.times{ |n|
-      fh.puts "This is line: #{n}"
-   }
+  LINES.times{ |n|
+    LINE_SIZE.times { |t|
+      fh.print "This is time: #{t} "
+    }
+    fh.puts
+  }
 }
 stat = File.stat(FILE)
 (ARGV[0] || 5).to_i.times do
   Benchmark.bm(30) do |x|
     x.report('IO.foreach(file)'){
       MAX.times{ IO.foreach(FILE){} }
     }
 
   end
 end
 File.delete(FILE) if File.exists?(FILE)
 
diff --git a/spec/tags/ruby/core/kernel/srand_tags.txt b/spec/tags/ruby/core/kernel/srand_tags.txt
deleted file mode 100644
index e1a0583a4f..0000000000
--- a/spec/tags/ruby/core/kernel/srand_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-3298):Kernel.srand calls #to_i on number
diff --git a/spec/tags/ruby/core/string/modulo_tags.txt b/spec/tags/ruby/core/string/modulo_tags.txt
index 44a824b8bd..5f38b9d3a6 100644
--- a/spec/tags/ruby/core/string/modulo_tags.txt
+++ b/spec/tags/ruby/core/string/modulo_tags.txt
@@ -1,2 +1 @@
-fails:String#% supports unsigned formats using %u
 fails:String#% supports negative bignums by prefixing the value with zeros
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index d2be9a768b..42d5a755ba 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -188,1146 +188,1146 @@ public class RubyKernel {
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
         Ruby runtime = context.getRuntime();
         runtime.getWarnings().warn(ID.EMPTY_IMPLEMENTATION, "Kernel#callcc: Continuations are not implemented in JRuby and will not work", "Kernel#callcc");
         IRubyObject cc = runtime.getContinuation().callMethod(context, "new");
         cc.dataWrapStruct(block);
         return block.yield(context, cc);
     }
 
     @JRubyMethod(name = "caller", optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject caller(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
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
     public static IRubyObject loop(ThreadContext context, IRubyObject recv, Block block) {
         IRubyObject nil = context.getRuntime().getNil();
         while (true) {
             block.yield(context, nil);
 
             context.pollThreadEvents();
         }
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
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         RubyString string = aString.convertToString();
         int resultCode = ShellLauncher.runAndWait(runtime, new IRubyObject[] {string}, output);
         
         runtime.getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return RubyString.newStringNoCopy(runtime, output.toByteArray());
     }
     
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE)
     public static RubyInteger srand(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
 
         // Not sure how well this works, but it works much better than
         // just currentTimeMillis by itself.
+        long oldRandomSeed = runtime.getRandomSeed();
         runtime.setRandomSeed(System.currentTimeMillis() ^
                recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
                runtime.getRandom().nextInt(Math.max(1, Math.abs((int)runtime.getRandomSeed()))));
 
-        long oldRandomSeed = runtime.getRandomSeed();
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
     
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE)
     public static RubyInteger srand(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyInteger integerSeed = arg.convertToInteger("to_int");
         Ruby runtime = context.getRuntime();
 
         long oldRandomSeed = runtime.getRandomSeed();
         runtime.setRandomSeed(integerSeed.getLongValue());
 
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE)
     public static RubyNumeric rand(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         return RubyFloat.newFloat(runtime, runtime.getRandom().nextDouble());
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE)
     public static RubyNumeric rand(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         Random random = runtime.getRandom();
 
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
 
     @JRubyMethod(name = "syscall", required = 1, optional = 9, module = true, visibility = PRIVATE)
     public static IRubyObject syscall(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         throw context.getRuntime().newNotImplementedError("Kernel#syscall is not implemented in JRuby");
     }
 
     @JRubyMethod(name = {"system"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static RubyBoolean system(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int resultCode;
         try {
             resultCode = ShellLauncher.runAndWait(runtime, args);
         } catch (Exception e) {
             resultCode = 127;
         }
         runtime.getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return runtime.newBoolean(resultCode == 0);
     }
     
     @JRubyMethod(name = {"exec"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject exec(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int resultCode;
         try {
             // TODO: exec should replace the current process.
             // This could be possible with JNA. 
             resultCode = ShellLauncher.execAndWait(runtime, args);
         } catch (Exception e) {
             throw runtime.newErrnoENOENTError("cannot execute");
         }
         
         return exit(recv, new IRubyObject[] {runtime.newFixnum(resultCode)});
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
 
     @JRubyMethod(frame = true, module = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject tap(ThreadContext context, IRubyObject recv, Block block) {
         block.yield(context, recv);
         return recv;
     }
 
     @JRubyMethod(name = {"to_enum", "enum_for"}, rest = true, compat = CompatVersion.RUBY1_9)
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
 
     @JRubyMethod(name = { "__method__", "__callee__" }, module = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject __method__(ThreadContext context, IRubyObject recv) {
         Frame f = context.getCurrentFrame();
         String name = f != null ? f.getName() : null;
         return name != null ? context.getRuntime().newSymbol(name) : context.getRuntime().getNil();
     }
 }
diff --git a/src/org/jruby/util/Sprintf.java b/src/org/jruby/util/Sprintf.java
index 0748c8642d..45259d688a 100644
--- a/src/org/jruby/util/Sprintf.java
+++ b/src/org/jruby/util/Sprintf.java
@@ -364,1040 +364,1040 @@ public class Sprintf {
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
                             c = bytes.unsafeBytes()[bytes.begin()];
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
                             buf.append(bytes.unsafeBytes(),bytes.begin(),len);
                             buf.fill(' ',width);
                         } else {
                             buf.fill(' ',width);
                             buf.append(bytes.unsafeBytes(),bytes.begin(),len);
                         }
                     } else {
                         buf.append(bytes.unsafeBytes(),bytes.begin(),len);
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
                             arg = RubyNumeric.str2inum(arg.getRuntime(),(RubyString)arg,0,true);
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
                     if ((flags & FLAG_SHARP) != 0) {
                         switch (fchar) {
                         case 'o': prefix = PREFIX_OCTAL; break;
                         case 'x': prefix = PREFIX_HEX_LC; break;
                         case 'X': prefix = PREFIX_HEX_UC; break;
                         case 'b': prefix = PREFIX_BINARY_LC; break;
                         case 'B': prefix = PREFIX_BINARY_UC; break;
                         }
                         if (prefix != null) width -= prefix.length;
                     }
                     // We depart here from strict adherence to MRI code, as MRI
                     // uses C-sprintf, in part, to format numeric output, while
                     // we'll use Java's numeric formatting code (and our own).
                     if (type == ClassIndex.FIXNUM) {
                         negative = ((RubyFixnum)arg).getLongValue() < 0;
                         if (negative && fchar == 'u') {
                             bytes = getUnsignedNegativeBytes((RubyFixnum)arg);
                         } else {
                             bytes = getFixnumBytes((RubyFixnum)arg,base,sign,fchar=='X');
                         }
                     } else {
                         negative = ((RubyBignum)arg).getValue().signum() < 0;
                         if (negative && fchar == 'u') {
                             bytes = getUnsignedNegativeBytes((RubyBignum)arg);
                         } else {
                             bytes = getBignumBytes((RubyBignum)arg,base,sign,fchar=='X');
                         }
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
                             buf.fill('0', precision - len);
                         } else if (leadChar == '.') {
                             buf.fill(leadChar,precision-len);
                             buf.append(PREFIX_NEGATIVE);
                         } else {
                             buf.fill(leadChar,precision-len+1); // the 1 is for the stripped sign char
                         }
                     } else if (leadChar != 0) {
                         if ((flags & (FLAG_PRECISION | FLAG_ZERO)) == 0) {
                             buf.append(PREFIX_NEGATIVE);
                         }
                         if (leadChar != '.') buf.append(leadChar);
                     }
                     buf.append(bytes,first,numlen);
 
                     if (width > 0) buf.fill(' ',width);
                                         
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
                         arg = RubyKernel.new_float(arg,arg);
                     }
                     double dval = ((RubyFloat)arg).getDoubleValue();
                     boolean nan = dval != dval;
                     boolean inf = dval == Double.POSITIVE_INFINITY || dval == Double.NEGATIVE_INFINITY;
                     boolean negative = dval < 0.0d;
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
                                 int n = round(digits,nDigits,intDigits+precision-decZeroes-1,precision!=0);
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
                 return Convert.intToByteArray((int)val,base,upper);
             } else {
                 switch(base) {
                 case 2:  return Convert.intToBinaryBytes((int)val);
                 case 8:  return Convert.intToOctalBytes((int)val);
                 case 10:
                 default: return Convert.intToCharBytes((int)val);
                 case 16: return Convert.intToHexBytes((int)val,upper);
                 }
             }
         } else {
             if (sign) {
                 return Convert.longToByteArray(val,base,upper);
             } else {
                 switch(base) {
                 case 2:  return Convert.longToBinaryBytes(val);
                 case 8:  return Convert.longToOctalBytes(val);
                 case 10:
                 default: return Convert.longToCharBytes(val);
                 case 16: return Convert.longToHexBytes(val,upper);
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
         case 2:  return Convert.twosComplementToBinaryBytes(bytes);
         case 8:  return Convert.twosComplementToOctalBytes(bytes);
         case 16: return Convert.twosComplementToHexBytes(bytes,upper);
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
-            if (longval >= (long)Integer.MIN_VALUE << 1) {
-                return Convert.longToCharBytes((((long)Integer.MAX_VALUE + 1L) << 1) + longval);
+            if (longval >= Long.MIN_VALUE << 1) {
+                return Convert.longToCharBytes(((Long.MAX_VALUE + 1L) << 1) + longval);
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
