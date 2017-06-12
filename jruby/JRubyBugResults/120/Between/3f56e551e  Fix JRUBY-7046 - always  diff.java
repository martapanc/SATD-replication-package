diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index c5554f80a0..9cc2a147d6 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -3218,1607 +3218,1605 @@ public class RubyIO extends RubyObject {
                 
                 // CRuby checks ferror(f) and retry getc for
                 // non-blocking IO.
                 if (c == -1) {
                     break;
                 }
                 
                 assert c < 256;
                 block.yield(context, getRuntime().newFixnum(c));
             }
 
             return this;
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (EOFException e) {
             return runtime.getNil();
     	} catch (IOException e) {
     	    throw runtime.newIOErrorFromException(e);
         }
     }
 
     @JRubyMethod
     public IRubyObject each_byte(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_byteInternal(context, block) : enumeratorize(context.runtime, this, "each_byte");
     }
 
     @JRubyMethod(name = "bytes")
     public IRubyObject bytes(final ThreadContext context) {
         return enumeratorize(context.runtime, this, "each_byte");
     }
 
     @JRubyMethod(name = "lines", compat = CompatVersion.RUBY1_8)
     public IRubyObject lines(final ThreadContext context, Block block) {
         return enumeratorize(context.runtime, this, "each_line");
     }
 
     @JRubyMethod(name = "lines", compat = CompatVersion.RUBY1_9)
     public IRubyObject lines19(final ThreadContext context, Block block) {
         if (!block.isGiven()) {
             return enumeratorize(context.runtime, this, "each_line");
         }
         return each_lineInternal(context, NULL_ARRAY, block);
     }
 
     public IRubyObject each_charInternal(final ThreadContext context, final Block block) {
         Ruby runtime = context.runtime;
         IRubyObject ch;
 
         while(!(ch = getc()).isNil()) {
             byte c = (byte)RubyNumeric.fix2int(ch);
             int n = runtime.getKCode().getEncoding().length(c);
             RubyString str = runtime.newString();
             if (runtime.is1_9()) str.setEncoding(getReadEncoding(runtime));
             str.setTaint(true);
             str.cat(c);
 
             while(--n > 0) {
                 if((ch = getc()).isNil()) {
                     block.yield(context, str);
                     return this;
                 }
                 c = (byte)RubyNumeric.fix2int(ch);
                 str.cat(c);
             }
             block.yield(context, str);
         }
         return this;
     }
 
     public IRubyObject each_charInternal19(final ThreadContext context, final Block block) {
         IRubyObject ch;
 
         while(!(ch = getc19(context)).isNil()) {
             block.yield(context, ch);
         }
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject each_char(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal(context, block) : enumeratorize(context.runtime, this, "each_char");
     }
 
     @JRubyMethod(name = "each_char", compat = RUBY1_9)
     public IRubyObject each_char19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal19(context, block) : enumeratorize(context.runtime, this, "each_char");
     }
 
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject chars(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal(context, block) : enumeratorize(context.runtime, this, "chars");
     }
 
     @JRubyMethod(name = "chars", compat = RUBY1_9)
     public IRubyObject chars19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal19(context, block) : enumeratorize(context.runtime, this, "chars");
     }
 
     @JRubyMethod
     public IRubyObject codepoints(final ThreadContext context, final Block block) {
         return eachCodePointCommon(context, block, "codepoints");
     }
 
     @JRubyMethod
     public IRubyObject each_codepoint(final ThreadContext context, final Block block) {
         return eachCodePointCommon(context, block, "each_codepoint");
     }
 
     private IRubyObject eachCharCommon(final ThreadContext context, final Block block, final String methodName) {
         return block.isGiven() ? each_char(context, block) : enumeratorize(context.runtime, this, methodName);
     }
 
     private IRubyObject eachCodePointCommon(final ThreadContext context, final Block block, final String methodName) {
         Ruby runtime = context.runtime;
         if (!block.isGiven()) return enumeratorize(runtime, this, methodName);
         IRubyObject ch;
 
         while(!(ch = getc()).isNil()) {
             block.yield(context, ch);
         }
         return this;
     }
 
     /** 
      * <p>Invoke a block for each line.</p>
      */
     public RubyIO each_lineInternal(ThreadContext context, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
         ByteList separator = getSeparatorForGets(runtime, args);
 
         ByteListCache cache = new ByteListCache();
         for (IRubyObject line = getline(runtime, separator); !line.isNil(); 
 		line = getline(runtime, separator, cache)) {
             block.yield(context, line);
         }
         
         return this;
     }
 
     @JRubyMethod(optional = 1)
     public IRubyObject each(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_lineInternal(context, args, block) : enumeratorize(context.runtime, this, "each", args);
     }
 
     @JRubyMethod(optional = 1)
     public IRubyObject each_line(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_lineInternal(context, args, block) : enumeratorize(context.runtime, this, "each_line", args);
     }
 
     @JRubyMethod(optional = 1)
     public RubyArray readlines(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         IRubyObject[] separatorArgs = args.length > 0 ? new IRubyObject[] { args[0] } : IRubyObject.NULL_ARRAY;
         ByteList separator = getSeparatorForGets(runtime, separatorArgs);
         RubyArray result = runtime.newArray();
         IRubyObject line;
         
         while (! (line = getline(runtime, separator)).isNil()) {
             result.append(line);
         }
         return result;
     }
     
     @JRubyMethod(name = "to_io")
     public RubyIO to_io() {
     	return this;
     }
 
     @Override
     public String toString() {
         try {
             return "RubyIO(" + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStreamSafe().getDescriptor()) + ")";
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
     
     /* class methods for IO */
     
     /** rb_io_s_foreach
     *
     */
     public static IRubyObject foreachInternal(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
         IRubyObject filename = args[0].convertToString();
 
         RubyIO io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
 
         ByteListCache cache = new ByteListCache();
         if (!io.isNil()) {
             try {
                 ByteList separator = io.getSeparatorFromArgs(runtime, args, 1);
                 IRubyObject str = io.getline(runtime, separator, cache);
                 while (!str.isNil()) {
                     block.yield(context, str);
                     str = io.getline(runtime, separator, cache);
                 }
             } finally {
                 io.close();
             }
         }
        
         return runtime.getNil();
     }
 
     /** rb_io_s_foreach
     *
     */
     public static IRubyObject foreachInternal19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
         IRubyObject filename = args[0].convertToString();
 
         RubyIO io;
         // FIXME: This is gross; centralize options logic somewhere.
         switch (args.length) {
             case 1:
                 io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
                 break;
             case 2:
                 if (args[1] instanceof RubyHash) {
                     io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename, args[1] }, Block.NULL_BLOCK);
                     args = new IRubyObject[]{args[0]};
                 } else {
                     io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
                 }
                 break;
             case 3:
                 if (args[1] instanceof RubyHash) {
                     io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename, args[2] }, Block.NULL_BLOCK);
                     args = new IRubyObject[]{args[0], args[1]};
                 } else {
                     io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
                 }
                 break;
             default:
                 // Should never be reached.
                 Arity.checkArgumentCount(runtime, args.length, 1, 3);
                 throw runtime.newRuntimeError("invalid argument count in IO.foreach: " + args.length);
         }
 
         ByteListCache cache = new ByteListCache();
         if (!io.isNil()) {
             try {
                 ByteList separator = io.getSeparatorFromArgs(runtime, args, 1);
                 IRubyObject str = io.getline(runtime, separator, cache);
                 while (!str.isNil()) {
                     block.yield(context, str);
                     str = io.getline(runtime, separator, cache);
                     if (runtime.is1_9()) {
                         separator = io.getSeparatorFromArgs(runtime, args, 1);
                     }
                 }
             } finally {
                 io.close();
             }
         }
 
         return runtime.getNil();
     }
     
     @JRubyMethod(required = 1, optional = 1, meta = true, compat = RUBY1_8)
     public static IRubyObject foreach(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         if (!block.isGiven()) return enumeratorize(context.runtime, recv, "foreach", args);
 
         return foreachInternal(context, recv, args, block);
     }
 
     @JRubyMethod(name = "foreach", required = 1, optional = 2, meta = true, compat = RUBY1_9)
     public static IRubyObject foreach19(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         if (!block.isGiven()) return enumeratorize(context.runtime, recv, "foreach", args);
 
         args[0] = RubyFile.get_path(context, args[0]);
 
         return foreachInternal19(context, recv, args, block);
     }
 
     public static RubyIO convertToIO(ThreadContext context, IRubyObject obj) {
         if (obj instanceof RubyIO) return (RubyIO)obj;
         return (RubyIO)TypeConverter.convertToType(obj, context.runtime.getIO(), "to_io");
     }
    
     @JRubyMethod(name = "select", required = 1, optional = 3, meta = true)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return select_static(context, context.runtime, args);
     }
 
     public static IRubyObject select_static(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         return new SelectBlob().goForIt(context, runtime, args);
     }
    
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         switch (args.length) {
         case 0:
             throw context.runtime.newArgumentError(0, 1);
         case 1: return readStatic(context, recv, args[0]);
         case 2: return readStatic(context, recv, args[0], args[1]);
         case 3: return readStatic(context, recv, args[0], args[1], args[2]);
         default:
             throw context.runtime.newArgumentError(args.length, 3);
         }
    }
 
     private static RubyIO newFile(ThreadContext context, IRubyObject recv, IRubyObject... args) {
        return (RubyIO) RubyKernel.open(context, recv, args, Block.NULL_BLOCK);
     }
 
     private static RubyIO newFile19(ThreadContext context, IRubyObject recv, IRubyObject... args) {
         return (RubyIO) RubyKernel.open19(context, recv, args, Block.NULL_BLOCK);
     }
 
     public static void failIfDirectory(Ruby runtime, RubyString pathStr) {
         if (RubyFileTest.directory_p(runtime, pathStr).isTrue()) {
             if (Platform.IS_WINDOWS) {
                 throw runtime.newErrnoEACCESError(pathStr.asJavaString());
             } else {
                 throw runtime.newErrnoEISDirError(pathStr.asJavaString());
             }
         }
     }
 
     @Deprecated
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, Block unusedBlock) {
         return readStatic(context, recv, path);
     }
     @Deprecated
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length) {
         return readStatic(context, recv, path, length);
     }
     @Deprecated
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset) {
         return readStatic(context, recv, path, length, offset);
     }
    
     @JRubyMethod(name = "read", meta = true, compat = RUBY1_8)
     public static IRubyObject readStatic(ThreadContext context, IRubyObject recv, IRubyObject path) {
         StringSupport.checkStringSafety(context.runtime, path);
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.runtime;
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
 
        try {
            return file.read(context);
        } finally {
            file.close();
        }
     }
    
     @JRubyMethod(name = "read", meta = true, compat = RUBY1_8)
     public static IRubyObject readStatic(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length) {
         StringSupport.checkStringSafety(context.runtime, path);
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.runtime;
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
        
         try {
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     @JRubyMethod(name = "read", meta = true, compat = RUBY1_8)
     public static IRubyObject readStatic(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset) {
         StringSupport.checkStringSafety(context.runtime, path);
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.runtime;
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     /**
      *  options is a hash which can contain:
      *    encoding: string or encoding
      *    mode: string
      *    open_args: array of string
      */
     private static IRubyObject read19(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset, RubyHash options) {
         // FIXME: process options
 
         RubyString pathStr = RubyFile.get_path(context, path);
         Ruby runtime = context.runtime;
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile19(context, recv, pathStr, options);
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     /**
      *  options is a hash which can contain:
      *    encoding: string or encoding
      *    mode: string
      *    open_args: array of string
      */
     private static IRubyObject write19(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject str, IRubyObject offset, RubyHash options) {
         // FIXME: process options
 
         RubyString pathStr = RubyFile.get_path(context, path);
         Ruby runtime = context.runtime;
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr, context.runtime.newString("w"));
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return file.write(context, str);
         } finally  {
             file.close();
         }
     }
 
     /**
      * binread is just like read, except it doesn't take options and it forces
      * mode to be "rb:ASCII-8BIT"
      *
      * @param context the current ThreadContext
      * @param recv the target of the call (IO or a subclass)
      * @param args arguments; path [, length [, offset]]
      * @return the binary contents of the given file, at specified length and offset
      */
     @JRubyMethod(meta = true, required = 1, optional = 2, compat = RUBY1_9)
     public static IRubyObject binread(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject nil = context.runtime.getNil();
         IRubyObject path = RubyFile.get_path(context, args[0]);
         IRubyObject length = nil;
         IRubyObject offset = nil;
         Ruby runtime = context.runtime;
 
         if (args.length > 2) {
             offset = args[2];
             length = args[1];
         } else if (args.length > 1) {
             length = args[1];
         }
         RubyIO file = (RubyIO)RuntimeHelpers.invoke(context, runtime.getFile(), "new", path, runtime.newString("rb:ASCII-8BIT"));
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     // Enebo: annotation processing forced me to do pangea method here...
     @JRubyMethod(name = "read", meta = true, required = 1, optional = 3, compat = RUBY1_9)
     public static IRubyObject read19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         Ruby runtime = context.runtime;
         IRubyObject nil = runtime.getNil();
         IRubyObject path = args[0];
         IRubyObject length = nil;
         IRubyObject offset = nil;
         RubyHash options = null;
         if (args.length > 3) {
             if (!(args[3] instanceof RubyHash)) throw runtime.newTypeError("Must be a hash");
             options = (RubyHash) args[3];
             offset = args[2];
             length = args[1];
         } else if (args.length > 2) {
             if (args[2] instanceof RubyHash) {
                 options = (RubyHash) args[2];
             } else {
                 offset = args[2];
             }
             length = args[1];
         } else if (args.length > 1) {
             if (args[1] instanceof RubyHash) {
                 options = (RubyHash) args[1];
             } else {
                 length = args[1];
             }
         }
         if (options == null) {
             options = RubyHash.newHash(runtime);
         }
 
         return read19(context, recv, path, length, offset, options);
     }
 
     @JRubyMethod(meta = true, required = 2, optional = 1, compat = RUBY1_9)
     public static IRubyObject binwrite(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject nil = context.runtime.getNil();
         IRubyObject path = args[0];
         IRubyObject str = args[1];
         IRubyObject offset = nil;
         Ruby runtime = context.runtime;
 
         if (args.length > 2) {
             offset = args[2];
         }
         RubyIO file = (RubyIO)RuntimeHelpers.invoke(context, runtime.getFile(), "new", path, runtime.newString("wb:ASCII-8BIT"));
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return file.write(context, str);
         } finally  {
             file.close();
         }
     }
 
     @JRubyMethod(name = "write", meta = true, required = 2, optional = 2, compat = RUBY1_9)
     public static IRubyObject writeStatic(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         IRubyObject nil = context.nil;
         IRubyObject path = args[0];
         IRubyObject str = args[1];
         IRubyObject offset = nil;
         RubyHash options = null;
         if (args.length > 3) {
             if (!(args[3] instanceof RubyHash)) {
                 throw context.runtime.newTypeError("Must be a hash");
             }
             options = (RubyHash) args[3];
             offset = args[2];
         } else if (args.length > 2) {
             if (args[2] instanceof RubyHash) {
                 options = (RubyHash) args[2];
             } else {
                 offset = args[2];
             }
         }
 
         return write19(context, recv, path, str, offset, (RubyHash) options);
     }
 
     @JRubyMethod(name = "readlines", required = 1, optional = 1, meta = true)
     public static RubyArray readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         int count = args.length;
 
         IRubyObject[] fileArguments = new IRubyObject[]{ args[0].convertToString() };
         IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
         RubyIO file = (RubyIO) RubyKernel.open(context, recv, fileArguments, Block.NULL_BLOCK);
         try {
             return file.readlines(context, separatorArguments);
         } finally {
             file.close();
         }
     }
    
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true, compat = RUBY1_8)
     public static IRubyObject popen(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
         int mode;
 
         IRubyObject cmdObj;
         if (Platform.IS_WINDOWS) {
             String[] tokens = args[0].convertToString().toString().split(" ", 2);
             String commandString = tokens[0].replace('/', '\\') +
                     (tokens.length > 1 ? ' ' + tokens[1] : "");
             cmdObj = runtime.newString(commandString);
         } else {
             cmdObj = args[0].convertToString();
         }
 
         if ("-".equals(cmdObj.toString())) {
             throw runtime.newNotImplementedError("popen(\"-\") is unimplemented");
         }
 
         try {
             IOOptions ioOptions;
             if (args.length == 1) {
                 ioOptions = newIOOptions(runtime, ModeFlags.RDONLY);
             } else if (args[1] instanceof RubyFixnum) {
                 ioOptions = newIOOptions(runtime, RubyFixnum.num2int(args[1]));
             } else {
                 ioOptions = newIOOptions(runtime, args[1].convertToString().toString());
             }
 
             ShellLauncher.POpenProcess process = ShellLauncher.popen(runtime, cmdObj, ioOptions);
 
             // Yes, this is gross. java.lang.Process does not appear to be guaranteed
             // "ready" when we get it back from Runtime#exec, so we try to give it a
             // chance by waiting for 10ms before we proceed. Only doing this on 1.5
             // since Hotspot 1.6+ does not seem to exhibit the problem.
             if (System.getProperty("java.specification.version", "").equals("1.5")) {
                 synchronized (process) {
                     try {
                         process.wait(100);
                     } catch (InterruptedException ie) {}
                 }
             }
 
             RubyIO io = new RubyIO(runtime, process, ioOptions);
             if (recv instanceof RubyClass) {
                 io.setMetaClass((RubyClass) recv);
             }
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, io);
                 } finally {
                     if (io.openFile.isOpen()) {
                         io.close();
                     }
                 }
             }
             return io;
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     private static class Ruby19POpen {
         public final RubyString cmd;
         public final IRubyObject[] cmdPlusArgs;
         public final RubyHash env;
         
         public Ruby19POpen(Ruby runtime, IRubyObject[] args) {
             IRubyObject[] _cmdPlusArgs = null;
             RubyHash _env = null;
             IRubyObject _cmd;
             IRubyObject arg0 = args[0].checkArrayType();
 
             if (args[0] instanceof RubyHash) {
                 // use leading hash as env
                 if (args.length > 1) {
                     _env = (RubyHash)args[0];
                 } else {
                     Arity.raiseArgumentError(runtime, 0, 1, 2);
                 }
 
                 if (Platform.IS_WINDOWS) {
                     String[] tokens = args[1].convertToString().toString().split(" ", 2);
                     String commandString = tokens[0].replace('/', '\\') +
                             (tokens.length > 1 ? ' ' + tokens[1] : "");
                     _cmd = runtime.newString(commandString);
                 } else {
                     _cmd = args[1].convertToString();
                 }
             } else if (args[0] instanceof RubyArray) {
                 RubyArray arg0Ary = (RubyArray)arg0;
                 if (arg0Ary.isEmpty()) throw runtime.newArgumentError("wrong number of arguments");
                 if (arg0Ary.eltOk(0) instanceof RubyHash) {
                     // leading hash, use for env
                     _env = (RubyHash)arg0Ary.delete_at(0);
                 }
                 if (arg0Ary.isEmpty()) throw runtime.newArgumentError("wrong number of arguments");
                 if (arg0Ary.size() > 1 && arg0Ary.eltOk(arg0Ary.size() - 1) instanceof RubyHash) {
                     // trailing hash, use for opts
                     _env = (RubyHash)arg0Ary.eltOk(arg0Ary.size() - 1);
                 }
                 _cmdPlusArgs = (IRubyObject[])arg0Ary.toJavaArray();
 
                 if (Platform.IS_WINDOWS) {
                     String commandString = _cmdPlusArgs[0].convertToString().toString().replace('/', '\\');
                     _cmdPlusArgs[0] = runtime.newString(commandString);
                 } else {
                     _cmdPlusArgs[0] = _cmdPlusArgs[0].convertToString();
                 }
                 _cmd = _cmdPlusArgs[0];
             } else {
                 if (Platform.IS_WINDOWS) {
                     String[] tokens = args[0].convertToString().toString().split(" ", 2);
                     String commandString = tokens[0].replace('/', '\\') +
                             (tokens.length > 1 ? ' ' + tokens[1] : "");
                     _cmd = runtime.newString(commandString);
                 } else {
                     _cmd = args[0].convertToString();
                 }
             }
 
             this.cmd = (RubyString)_cmd;
             this.cmdPlusArgs = _cmdPlusArgs;
             this.env = _env;
         }
     }
 
     @JRubyMethod(name = "popen", required = 1, optional = 2, meta = true, compat = RUBY1_9)
     public static IRubyObject popen19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
         int mode;
         // yes, I know it's not used. See JRUBY-5942
         RubyHash options = null;
 
         // for 1.9 mode, strip off the trailing options hash, if there
         if (args.length > 1 && args[args.length - 1] instanceof RubyHash) {
             options = (RubyHash)args[args.length - 1];
             IRubyObject[] newArgs = new IRubyObject[args.length - 1];
             System.arraycopy(args, 0, newArgs, 0, args.length - 1);
             args = newArgs;
         }
         
         Ruby19POpen r19Popen = new Ruby19POpen(runtime, args);
 
         if ("-".equals(r19Popen.cmd.toString())) {
             throw runtime.newNotImplementedError("popen(\"-\") is unimplemented");
         }
 
         try {
             IOOptions ioOptions;
             if (args.length == 1) {
                 ioOptions = newIOOptions(runtime, ModeFlags.RDONLY);
             } else if (args[1] instanceof RubyFixnum) {
                 ioOptions = newIOOptions(runtime, RubyFixnum.num2int(args[1]));
             } else {
                 ioOptions = newIOOptions(runtime, args[1].convertToString().toString());
             }
 
             ShellLauncher.POpenProcess process;
             if (r19Popen.cmdPlusArgs == null) {
                 process = ShellLauncher.popen(runtime, r19Popen.cmd, ioOptions);
             } else {
                 process = ShellLauncher.popen(runtime, r19Popen.cmdPlusArgs, r19Popen.env, ioOptions);
             }
 
             // Yes, this is gross. java.lang.Process does not appear to be guaranteed
             // "ready" when we get it back from Runtime#exec, so we try to give it a
             // chance by waiting for 10ms before we proceed. Only doing this on 1.5
             // since Hotspot 1.6+ does not seem to exhibit the problem.
             if (System.getProperty("java.specification.version", "").equals("1.5")) {
                 synchronized (process) {
                     try {
                         process.wait(100);
                     } catch (InterruptedException ie) {}
                 }
             }
 
             checkPopenOptions(options);
 
             RubyIO io = new RubyIO(runtime, (RubyClass)recv, process, options, ioOptions);
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, io);
                 } finally {
                     if (io.openFile.isOpen()) {
                         io.close();
                     }
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, process.waitFor(), ShellLauncher.getPidFromProcess(process)));
                 }
             }
             return io;
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
    
     @JRubyMethod(rest = true, meta = true)
     public static IRubyObject popen3(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
 
         try {
             POpenTuple tuple = popenSpecial(context, args);
 
             RubyArray yieldArgs = RubyArray.newArrayLight(runtime,
                     tuple.output,
                     tuple.input,
                     tuple.error);
             
             if (block.isGiven()) {
                 try {
                     return block.yield(context, yieldArgs);
                 } finally {
                     cleanupPOpen(tuple);
                     context.setLastExitStatus(
                             RubyProcess.RubyStatus.newProcessStatus(runtime, tuple.process.waitFor(), ShellLauncher.getPidFromProcess(tuple.process)));
                 }
             }
             return yieldArgs;
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     @JRubyMethod(rest = true, meta = true)
     public static IRubyObject popen4(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
 
         try {
             POpenTuple tuple = popenSpecial(context, args);
 
             RubyArray yieldArgs = RubyArray.newArrayLight(runtime,
                     runtime.newFixnum(ShellLauncher.getPidFromProcess(tuple.process)),
                     tuple.output,
                     tuple.input,
                     tuple.error);
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, yieldArgs);
                 } finally {
                     cleanupPOpen(tuple);
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, tuple.process.waitFor(), ShellLauncher.getPidFromProcess(tuple.process)));
                 }
             }
             return yieldArgs;
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     private static void cleanupPOpen(POpenTuple tuple) {
         if (tuple.input.openFile.isOpen()) {
             tuple.input.close();
         }
         if (tuple.output.openFile.isOpen()) {
             tuple.output.close();
         }
         if (tuple.error.openFile.isOpen()) {
             tuple.error.close();
         }
     }
 
     private static class POpenTuple {
         public POpenTuple(RubyIO i, RubyIO o, RubyIO e, Process p) {
             input = i; output = o; error = e; process = p;
         }
         public final RubyIO input;
         public final RubyIO output;
         public final RubyIO error;
         public final Process process;
     }
 
     public static POpenTuple popenSpecial(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
 
         try {
             ShellLauncher.POpenProcess process = ShellLauncher.popen3(runtime, args, false);
             RubyIO input = process.getInput() != null ?
                 new RubyIO(runtime, process.getInput()) :
                 new RubyIO(runtime, process.getInputStream());
             RubyIO output = process.getOutput() != null ?
                 new RubyIO(runtime, process.getOutput()) :
                 new RubyIO(runtime, process.getOutputStream());
             RubyIO error = process.getError() != null ?
                 new RubyIO(runtime, process.getError()) :
                 new RubyIO(runtime, process.getErrorStream());
 
             // ensure the OpenFile knows it's a process; see OpenFile#finalize
             input.getOpenFile().setProcess(process);
             output.getOpenFile().setProcess(process);
             error.getOpenFile().setProcess(process);
 
             // set all streams as popenSpecial streams, so we don't shut down process prematurely
             input.popenSpecial = true;
             output.popenSpecial = true;
             error.popenSpecial = true;
             
             // process streams are not seekable
             input.getOpenFile().getMainStreamSafe().getDescriptor().
               setCanBeSeekable(false);
             output.getOpenFile().getMainStreamSafe().getDescriptor().
               setCanBeSeekable(false);
             error.getOpenFile().getMainStreamSafe().getDescriptor().
               setCanBeSeekable(false);
 
             return new POpenTuple(input, output, error, process);
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     @JRubyMethod(name = "pipe", meta = true, compat = RUBY1_8)
     public static IRubyObject pipe(ThreadContext context, IRubyObject recv) {
         // TODO: This isn't an exact port of MRI's pipe behavior, so revisit
         Ruby runtime = context.runtime;
         try {
             Pipe pipe = Pipe.open();
 
             RubyIO source = new RubyIO(runtime, pipe.source());
             RubyIO sink = new RubyIO(runtime, pipe.sink());
 
             sink.openFile.getMainStreamSafe().setSync(true);
             return runtime.newArrayNoCopy(new IRubyObject[]{source, sink});
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
         }
     }
 
     @JRubyMethod(name = "pipe", meta = true, compat = RUBY1_9)
     public static IRubyObject pipe19(ThreadContext context, IRubyObject recv) {
         return pipe(context, recv);
     }
 
     @JRubyMethod(name = "pipe", meta = true, compat = RUBY1_9)
     public static IRubyObject pipe19(ThreadContext context, IRubyObject recv, IRubyObject modes) {
         Ruby runtime = context.runtime;
         try {
             Pipe pipe = Pipe.open();
 
             RubyIO source = new RubyIO(runtime, pipe.source());
             source.setEncodingFromOptions(EncodingOption.getEncodingOptionFromString(runtime, modes.toString()));
             RubyIO sink = new RubyIO(runtime, pipe.sink());
 
 //            Encoding ascii8bit = context.runtime.getEncodingService().getAscii8bitEncoding();
 //            sink.setupReadWriteEncodings(context, ascii8bit, ascii8bit);
 
             sink.openFile.getMainStreamSafe().setSync(true);
             return runtime.newArrayNoCopy(new IRubyObject[]{source, sink});
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
         }
     }
 
     @JRubyMethod(name = "pipe", meta = true, compat = RUBY1_9)
     public static IRubyObject pipe19(ThreadContext context, IRubyObject recv, IRubyObject modes, IRubyObject options) {
         // TODO handle options
         return pipe19(context, recv, modes);
     }
     
     @JRubyMethod(name = "copy_stream", meta = true, compat = RUBY1_9)
     public static IRubyObject copy_stream(ThreadContext context, IRubyObject recv, 
             IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.runtime;
 
         RubyIO io1 = null;
         RubyIO io2 = null;
 
         try {
             if (arg1 instanceof RubyString) {
                 io1 = (RubyIO) RubyFile.open(context, runtime.getFile(), new IRubyObject[] {arg1}, Block.NULL_BLOCK);
             } else if (arg1 instanceof RubyIO) {
                 io1 = (RubyIO) arg1;
             } else {
                 throw runtime.newTypeError("Should be String or IO");
             }
 
             if (arg2 instanceof RubyString) {
                 io2 = (RubyIO) RubyFile.open(context, runtime.getFile(), new IRubyObject[] {arg2, runtime.newString("w")}, Block.NULL_BLOCK);
             } else if (arg2 instanceof RubyIO) {
                 io2 = (RubyIO) arg2;
             } else {
                 throw runtime.newTypeError("Should be String or IO");
             }
 
             ChannelDescriptor d1 = io1.openFile.getMainStreamSafe().getDescriptor();
             ChannelDescriptor d2 = io2.openFile.getMainStreamSafe().getDescriptor();
 
             if (!d1.isReadable()) throw runtime.newIOError("from IO is not readable");
             if (!d2.isWritable()) throw runtime.newIOError("from IO is not writable");
 
             try {
                 long size = 0;
                 if (!d1.isSeekable()) {
                     if (!d2.isSeekable()) {
                         throw context.runtime.newTypeError("only supports to file or from file copy");
                     } else {
                         ReadableByteChannel from = (ReadableByteChannel)d1.getChannel();
                         FileChannel to = (FileChannel)d2.getChannel();
 
                         size = transfer(from, to);
                     }
                 } else {
                     FileChannel from = (FileChannel)d1.getChannel();
                     WritableByteChannel to = (WritableByteChannel)d2.getChannel();
 
                     size = transfer(from, to);
                 }
 
                 return context.runtime.newFixnum(size);
             } catch (IOException ioe) {
                 throw runtime.newIOErrorFromException(ioe);
             }
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } finally {
             try {
                 if (io1 != null) {
                     io1.close();
                 }
             } finally {
                 if (io2 != null) {
                     io2.close();
                 }
             }
         }
     }
 
     private static long transfer(ReadableByteChannel from, FileChannel to) throws IOException {
         long transferred = 0;
         long bytes;
         while ((bytes = to.transferFrom(from, to.position(), 4196)) > 0) {
             transferred += bytes;
         }
 
         return transferred;
     }
 
     private static long transfer(FileChannel from, WritableByteChannel to) throws IOException {
-        long size = from.size();
+        
 
-        // handle large files on 32-bit JVMs (JRUBY-4913)
-        try {
-            from.transferTo(from.position(), size, to);
-        } catch (IOException ioe) {
-            // if the failure is "Cannot allocate memory", do the transfer in 100MB max chunks
-            if (ioe.getMessage().equals("Cannot allocate memory")) {
-                long _100M = 100 * 1024 * 1024;
-                while (size > 0) {
-                    if (size > _100M) {
-                        from.transferTo(from.position(), _100M, to);
-                        size -= _100M;
-                    } else {
-                        from.transferTo(from.position(), size, to);
-                        break;
-                    }
-                }
-            } else {
-                throw ioe;
+        // handle large files on 32-bit JVMs
+        long chunkSize = 128 * 1024 * 1024;
+        long size = from.size();
+        long remaining = size;
+        long position = from.position();
+        long transferred = 0;
+        
+        while (remaining > 0) {
+            long count = Math.min(remaining, chunkSize);
+            long n = from.transferTo(position, count, to);
+            if (n == 0) {
+                break;
             }
+            
+            position += n;
+            remaining -= n;
+            transferred += n;
         }
-
-        return size;
+        
+        return transferred;
     }
 
     @JRubyMethod(name = "try_convert", meta = true, compat = RUBY1_9)
     public static IRubyObject tryConvert(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return arg.respondsTo("to_io") ? convertToIO(context, arg) : context.runtime.getNil();
     }
 
     private static ByteList getNilByteList(Ruby runtime) {
         return runtime.is1_9() ? ByteList.EMPTY_BYTELIST : NIL_BYTELIST;
     }
     
     /**
      * Add a thread to the list of blocking threads for this IO.
      * 
      * @param thread A thread blocking on this IO
      */
     public synchronized void addBlockingThread(RubyThread thread) {
         if (blockingThreads == null) {
             blockingThreads = new ArrayList<RubyThread>(1);
         }
         blockingThreads.add(thread);
     }
     
     /**
      * Remove a thread from the list of blocking threads for this IO.
      * 
      * @param thread A thread blocking on this IO
      */
     public synchronized void removeBlockingThread(RubyThread thread) {
         if (blockingThreads == null) {
             return;
         }
         for (int i = 0; i < blockingThreads.size(); i++) {
             if (blockingThreads.get(i) == thread) {
                 // not using remove(Object) here to avoid the equals() call
                 blockingThreads.remove(i);
             }
         }
     }
     
     /**
      * Fire an IOError in all threads blocking on this IO object
      */
     protected synchronized void interruptBlockingThreads() {
         if (blockingThreads == null) {
             return;
         }
         for (int i = 0; i < blockingThreads.size(); i++) {
             RubyThread thread = blockingThreads.get(i);
             
             // raise will also wake the thread from selection
             thread.raise(new IRubyObject[] {getRuntime().newIOError("stream closed").getException()}, Block.NULL_BLOCK);
         }
     }
 
     /**
      * Caching reference to allocated byte-lists, allowing for internal byte[] to be
      * reused, rather than reallocated.
      *
      * Predominately used on {@link RubyIO#getline(Ruby, ByteList)} and variants.
      *
      * @author realjenius
      */
     private static class ByteListCache {
         private byte[] buffer = new byte[0];
         public void release(ByteList l) {
             buffer = l.getUnsafeBytes();
         }
 
         public ByteList allocate(int size) {
             ByteList l = new ByteList(buffer, 0, size, false);
             return l;
         }
     }
 
     /**
      * See http://ruby-doc.org/core-1.9.3/IO.html#method-c-new for the format of modes in options
      */
     protected IOOptions updateIOOptionsFromOptions(ThreadContext context, RubyHash options, IOOptions ioOptions) {
         if (options == null || options.isNil()) return ioOptions;
 
         Ruby runtime = context.runtime;
 
         if (options.containsKey(runtime.newSymbol("mode"))) {
             ioOptions = parseIOOptions19(options.fastARef(runtime.newSymbol("mode")));
         }
 
         // This duplicates the non-error behavior of MRI 1.9: the
         // :binmode option is ORed in with other options. It does
         // not obliterate what came before.
 
         if (options.containsKey(runtime.newSymbol("binmode")) &&
                 options.fastARef(runtime.newSymbol("binmode")).isTrue()) {
 
             ioOptions = newIOOptions(runtime, ioOptions, ModeFlags.BINARY);
         }
 
         // This duplicates the non-error behavior of MRI 1.9: the
         // :binmode option is ORed in with other options. It does
         // not obliterate what came before.
 
         if (options.containsKey(runtime.newSymbol("binmode")) &&
                 options.fastARef(runtime.newSymbol("binmode")).isTrue()) {
 
             ioOptions = newIOOptions(runtime, ioOptions, ModeFlags.BINARY);
         }
 
         if (options.containsKey(runtime.newSymbol("textmode")) &&
                 options.fastARef(runtime.newSymbol("textmode")).isTrue()) {
 
             ioOptions = newIOOptions(runtime, ioOptions, ModeFlags.TEXT);
         }
         
         // TODO: Waaaay different than MRI.  They uniformly have all opening logic
         // do a scan of args before anything opens.  We do this logic in a less
         // consistent way.  We should consider re-impling all IO/File construction
         // logic.
         if (options.containsKey(runtime.newSymbol("open_args"))) {
             IRubyObject args = options.fastARef(runtime.newSymbol("open_args"));
             
             RubyArray openArgs = args.convertToArray();
             
             for (int i = 0; i < openArgs.size(); i++) {
                 IRubyObject arg = openArgs.eltInternal(i);
                 
                 if (arg instanceof RubyString) { // Overrides all?
                     ioOptions = newIOOptions(runtime, arg.asJavaString());
                 } else if (arg instanceof RubyFixnum) {
                     ioOptions = newIOOptions(runtime, ((RubyFixnum) arg).getLongValue());
                 } else if (arg instanceof RubyHash) {
                     ioOptions = updateIOOptionsFromOptions(context, (RubyHash) arg, ioOptions);
                 }
             }
         }
 
         EncodingOption encodingOption = EncodingOption.getEncodingOptionFromObject(options);
         if (encodingOption != null) {
             ioOptions.setEncodingOption(encodingOption);
         }
 
         return ioOptions;
     }
 
     public void setEncodingFromOptions(EncodingOption option) {
         Encoding internal = null;
 
         Encoding external;
         if (option.hasBom()) {
             external = encodingFromBOM();
         } else if (option.getExternalEncoding() != null) {
             external = option.getExternalEncoding();
         } else {
             external = null;
         }
 
         if (option.getInternalEncoding() != null) internal = option.getInternalEncoding();
 
         setupReadWriteEncodings(getRuntime().getCurrentContext(), internal, external);
     }
 
     // io_strip_bom
     public Encoding encodingFromBOM() {
         int b1, b2, b3, b4;
 
         switch (b1 = getcCommon()) {
             case 0xEF:
                 b2 = getcCommon();
                 if (b2 == 0xBB) {
                     b3 = getcCommon();
                     if (b3 == 0xBF) {
                         return UTF8Encoding.INSTANCE;
                     }
                     ungetcCommon(b3);
                 }
                 ungetcCommon(b2);
                 break;
             case 0xFE:
                 b2 = getcCommon();
                 if (b2 == 0xFF) {
                     return UTF16BEEncoding.INSTANCE;
                 }
                 ungetcCommon(b2);
                 break;
             case 0xFF:
                 b2 = getcCommon();
                 if (b2 == 0xFE) {
                     b3 = getcCommon();
                     if (b3 == 0) {
                         b4 = getcCommon();
                         if (b4 == 0) {
                             return UTF32LEEncoding.INSTANCE;
                         }
                         ungetcCommon(b4);
                     } else {
                         ungetcCommon(b3);
                         return UTF16LEEncoding.INSTANCE;
                     }
                     ungetcCommon(b3);
                 }
                 ungetcCommon(b2);
                 break;
             case 0:
                 b2 = getcCommon();
                 if (b2 == 0) {
                     b3 = getcCommon();
                     if (b3 == 0xFE) {
                         b4 = getcCommon();
                         if (b4 == 0xFF) {
                             return UTF32BEEncoding.INSTANCE;
                         }
                         ungetcCommon(b4);
                     }
                     ungetcCommon(b3);
                 }
                 ungetcCommon(b2);
                 break;
         }
         ungetcCommon(b1);
         return null;
     }
 
     private static final Set<String> UNSUPPORTED_SPAWN_OPTIONS = new HashSet<String>(Arrays.asList(new String[] {
             "unsetenv_others",
             "prgroup",
             "rlimit_resourcename",
             "chdir",
             "umask",
             "in",
             "out",
             "err",
             "close_others"
     }));
 
     private static final Set<String> ALL_SPAWN_OPTIONS = new HashSet<String>(Arrays.asList(new String[] {
             "unsetenv_others",
             "prgroup",
             "rlimit_resourcename",
             "chdir",
             "umask",
             "in",
             "out",
             "err",
             "close_others"
     }));
 
     /**
      * Warn when using exec with unsupported options.
      *
      * @param options
      */
     public static void checkExecOptions(IRubyObject options) {
         checkUnsupportedOptions(options, UNSUPPORTED_SPAWN_OPTIONS, "unsupported exec option");
         checkValidOptions(options, ALL_SPAWN_OPTIONS);
     }
 
     /**
      * Warn when using spawn with unsupported options.
      *
      * @param options
      */
     public static void checkSpawnOptions(IRubyObject options) {
         checkUnsupportedOptions(options, UNSUPPORTED_SPAWN_OPTIONS, "unsupported spawn option");
         checkValidOptions(options, ALL_SPAWN_OPTIONS);
     }
 
     /**
      * Warn when using spawn with unsupported options.
      *
      * @param options
      */
     public static void checkPopenOptions(IRubyObject options) {
         checkUnsupportedOptions(options, UNSUPPORTED_SPAWN_OPTIONS, "unsupported popen option");
     }
 
     /**
      * Warn when using unsupported options.
      *
      * @param options
      */
     private static void checkUnsupportedOptions(IRubyObject options, Set<String> unsupported, String error) {
         if (options == null || options.isNil() || !(options instanceof RubyHash)) return;
 
         RubyHash optsHash = (RubyHash)options;
         Ruby runtime = optsHash.getRuntime();
 
         for (String key : unsupported) {
             if (optsHash.containsKey(runtime.newSymbol(key))) {
                 runtime.getWarnings().warn(error + ": " + key);
             }
         }
     }
 
     /**
      * Error when using unknown option.
      *
      * @param options
      */
     private static void checkValidOptions(IRubyObject options, Set<String> valid) {
         if (options == null || options.isNil() || !(options instanceof RubyHash)) return;
 
         RubyHash optsHash = (RubyHash)options;
         Ruby runtime = optsHash.getRuntime();
 
         for (Object opt : optsHash.keySet()) {
             if (opt instanceof RubySymbol || opt instanceof RubyFixnum || valid.contains(opt.toString())) {
                 continue;
             }
 
             throw runtime.newTypeError("wrong exec option: " + opt);
         }
     }
     
     /**
      * Try for around 1s to destroy the child process. This is to work around
      * issues on some JVMs where if you try to destroy the process too quickly
      * it may not be ready and may ignore the destroy. A subsequent waitFor
      * will then hang. This version tries to destroy and call exitValue
      * repeatedly for up to 1000 calls with 1ms delay between iterations, with
      * the intent that the target process ought to be "ready to die" fairly
      * quickly and we don't get stuck in a blocking waitFor call.
      *
      * @param process The process to obliterate
      */
     public static void obliterateProcess(Process process) {
         int i = 0;
         Object waitLock = new Object();
         while (true) {
             // only try 1000 times with a 1ms sleep between, so we don't hang
             // forever on processes that ignore SIGTERM. After that, not much
             // we can do...
             if (i >= 1000) {
                 return;
             }
 
             // attempt to destroy (SIGTERM on UNIX, TerminateProcess on Windows)
             process.destroy();
             
             try {
                 // get the exit value; succeeds if it has terminated, throws
                 // IllegalThreadStateException if not.
                 process.exitValue();
             } catch (IllegalThreadStateException itse) {
                 // increment count and try again after a 1ms sleep
                 i += 1;
                 synchronized (waitLock) {
                     try {waitLock.wait(1);} catch (InterruptedException ie) {}
                 }
                 continue;
             }
             // success!
             break;
         }
     }
 
     public static ModeFlags newModeFlags(Ruby runtime, long mode) {
         return newModeFlags(runtime, (int) mode);
     }
 
     public static ModeFlags newModeFlags(Ruby runtime, int mode) {
         try {
             return new ModeFlags(mode);
         } catch (InvalidValueException ive) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     public static ModeFlags newModeFlags(Ruby runtime, String mode) {
         try {
             return new ModeFlags(mode);
         } catch (InvalidValueException ive) {
             // This is used by File and StringIO, which seem to want an ArgumentError instead of EINVAL
             throw runtime.newArgumentError("illegal access mode " + mode);
         }
     }
 
     public static IOOptions newIOOptions(Ruby runtime, ModeFlags modeFlags) {
         return new IOOptions(modeFlags, EncodingOption.getEncodingNoOption(runtime, modeFlags));
     }
 
     public static IOOptions newIOOptions(Ruby runtime, long mode) {
         return newIOOptions(runtime, (int) mode);
     }
 
     public static IOOptions newIOOptions(Ruby runtime, int mode) {
         try {
             ModeFlags modeFlags = new ModeFlags(mode);
             return new IOOptions(modeFlags, EncodingOption.getEncodingNoOption(runtime, modeFlags));
         } catch (InvalidValueException ive) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     public static IOOptions newIOOptions(Ruby runtime, String mode) {
         try {
             return new IOOptions(runtime, mode);
         } catch (InvalidValueException ive) {
             // This is used by File and StringIO, which seem to want an ArgumentError instead of EINVAL
             throw runtime.newArgumentError("illegal access mode " + mode);
         }
     }
 
     public static IOOptions newIOOptions(Ruby runtime, IOOptions oldFlags, int orOflags) {
         try {
             return new IOOptions(new ModeFlags(oldFlags.getModeFlags().getFlags() | orOflags), oldFlags.getEncodingOption());
         } catch (InvalidValueException ive) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     public boolean writeDataBuffered() {
         return openFile.getMainStream().writeDataBuffered();
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor, boolean isRetained) {
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor) {
     }
 
     @Deprecated
     public void unregisterDescriptor(int aFileno) {
     }
 
     @Deprecated
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         return ChannelDescriptor.getDescriptorByFileno(aFileno);
     }
 
     @Deprecated
     public static int getNewFileno() {
         return ChannelDescriptor.getNewFileno();
     }
 
     @Deprecated
     public IRubyObject gets(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? gets(context) : gets(context, args[0]);
     }
 
     @Deprecated
     public IRubyObject readline(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? readline(context) : readline(context, args[0]);
     }
     
     // MRI: do_writeconv
     private ByteList doWriteConversion(ThreadContext context, ByteList str) {
         if (!needsWriteConversion(context)) return str;
         
         // openFile.setBinmode(); // In MRI this does not affect flags like we do in OpenFile
         makeWriteConversion(context);
         
         return writeTranscoder.transcode(context, str);
     }
     
     // MRI: NEED_READCONF (FIXME: Windows has slightly different version)
     private boolean needsReadConversion() {
         return writeEncoding != null || openFile.isTextMode();
     }
     
     // MRI: NEED_WRITECONV (FIXME: Windows has slightly different version)
     private boolean needsWriteConversion(ThreadContext context) {
         Encoding ascii8bit = context.runtime.getEncodingService().getAscii8bitEncoding();
         
         return (readEncoding != null && readEncoding != ascii8bit) || openFile.isTextMode();
         // This is basically from MRI and until I understand it better I am leaving it out
         // ||  ((ecflags & (DECORATOR_MASK|STATEFUL_DECORATOR_MASK)) != 0);
     }
     
     // MRI: make_readconv
     // Missing flags and doubling readTranscoder as transcoder and whether transcoder has been initializer (ick).
     private void makeReadConversion(ThreadContext context) {
         if (readTranscoder != null) return;
         
         if (writeEncoding != null) {
             readTranscoder = new CharsetTranscoder(context, readEncoding, writeEncoding, transcodingActions);
         } else {
             Encoding ascii8bit = context.runtime.getEncodingService().getAscii8bitEncoding();
             
             readTranscoder = new CharsetTranscoder(context, ascii8bit, ascii8bit, transcodingActions);
         }
     }
     
     // MRI: make_writeconv
     // Actually this is quite a bit different and simpler for now.
     private void makeWriteConversion(ThreadContext context) {
         if (writeTranscoder != null) return;
         
         Encoding ascii8bit = context.runtime.getEncodingService().getAscii8bitEncoding();
         
         if (readEncoding == null || (readEncoding == ascii8bit  && writeEncoding == null)) { // No encoding conversion
             // Leave for extra MRI bittwiddling which is missing from our IO
             // Hack to initialize transcoder but do no transcoding
             writeTranscoder = new CharsetTranscoder(context, ascii8bit, ascii8bit, transcodingActions);
         } else {
             Encoding fromEncoding = readEncoding;
             Encoding toEncoding;
             if (writeEncoding != null) {
                 toEncoding = writeEncoding;
             } else {
                 fromEncoding = null;
                 toEncoding = readEncoding;
             }
             // If no write then default -> readEncoding
             // If write then writeEncoding -> readEncoding
             // If no read (see if above)
             writeTranscoder = new CharsetTranscoder(context, toEncoding, fromEncoding, transcodingActions);
         }
     }
     
     private void clearCodeConversion() {
         readTranscoder = null;
         writeTranscoder = null;
     }
     
     // MRI: rb_io_ext_int_to_encs
     private void setupReadWriteEncodings(ThreadContext context, Encoding internal, Encoding external) {
         Encoding ascii8bit = context.runtime.getEncodingService().getAscii8bitEncoding();
         boolean defaultExternal = false;
         
         if (external == null) {
             external = context.runtime.getDefaultExternalEncoding();
             defaultExternal = true;
         }
         
         if (internal == null && external != ascii8bit) {
             internal = context.runtime.getDefaultInternalEncoding();
         }
         
         if (internal == null || internal == external) { // missing internal == nil?
             readEncoding = (defaultExternal && internal != external) ? null : external;
             writeEncoding = null;
         } else {
             readEncoding = internal;
             writeEncoding = external;
         }
     }
 
     // MRI: rb_io_ascii8bit_binmode
     protected void setAscii8bitBinmode() {
         Encoding ascii8bit = getRuntime().getEncodingService().getAscii8bitEncoding();
 
         openFile.setBinmode();
         openFile.clearTextMode();
         readEncoding = ascii8bit;
         writeEncoding = null;
     }
     
     protected CharsetTranscoder readTranscoder = null;
     protected CharsetTranscoder writeTranscoder = null;
     protected OpenFile openFile;
     protected List<RubyThread> blockingThreads;
     
     /**
      * readEncoding/writeEncoding deserve a paragraph explanation.  In spite
      * of appearing to be a better name than enc/enc as is used in MRI, it is
      * probably a wash.  readEncoding represents the encoding we want the string
      * to be.  If writeEncoding is not null this represents the source encoding
      * to use.
      * 
      * Reading:
      * So if we are reading and there is no writeEncoding then we assume that
      * the io is already readEncoding and read it as such.  If both are set
      * then we assume readEncoding is external encoding and we transcode to
      * writeEncoding (internal).
      * 
      * Writing:
      * If writeEncoding is null then we write the bytes as readEncoding.  If
      * writeEncoding is set then we convert from writeEncoding to readEncoding.
      * 
      * Note: This naming is clearly wrong, but it is no worse then enc/enc2 so
      * I did not feel the need to fix it.
      */
     protected Encoding readEncoding; // MRI:enc
     protected Encoding writeEncoding; // MRI:enc2
     protected CharsetTranscoder.CodingErrorActions transcodingActions;
     
     /**
      * If the stream is being used for popen, we don't want to destroy the process
      * when we close the stream.
      */
     protected boolean popenSpecial;
 }
