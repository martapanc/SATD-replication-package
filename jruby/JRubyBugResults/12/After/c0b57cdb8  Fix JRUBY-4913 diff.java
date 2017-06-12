diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index 36f118571f..62962b39aa 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -2981,1440 +2981,1459 @@ public class RubyIO extends RubyObject {
 
     // implements io_fread in io.c
     private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {
         Stream stream = openFile.getMainStreamSafe();
         int rest = length;
         waitReadable(stream);
         ByteList buf = blockingFRead(stream, thread, length);
         if (buf != null) {
             rest -= buf.length();
         }
         while (rest > 0) {
             waitReadable(stream);
             openFile.checkClosed(getRuntime());
             stream.clearerr();
             ByteList newBuffer = blockingFRead(stream, thread, rest);
             if (newBuffer == null) {
                 // means EOF
                 break;
             }
             int len = newBuffer.length();
             if (len == 0) {
                 // TODO: warn?
                 // rb_warning("nonblocking IO#read is obsolete; use IO#readpartial or IO#sysread")
                 continue;
             }
             if (buf == null) {
                 buf = newBuffer;
             } else {
                 buf.append(newBuffer);
             }
             rest -= len;
         }
         if (buf == null) {
             return ByteList.EMPTY_BYTELIST.dup();
         } else {
             return buf;
         }
     }
 
     private ByteList blockingFRead(Stream stream, RubyThread thread, int length) throws IOException, BadDescriptorException {
         try {
             thread.beforeBlockingCall();
             return stream.fread(length);
         } finally {
             thread.afterBlockingCall();
         }
     }
     
     /** Read a byte. On EOF throw EOFError.
      * 
      */
     @JRubyMethod(name = "readchar", compat = RUBY1_8)
     public IRubyObject readchar() {
         IRubyObject c = getc();
         
         if (c.isNil()) throw getRuntime().newEOFError();
         
         return c;
     }
     
     @JRubyMethod
     public IRubyObject stat(ThreadContext context) {
         openFile.checkClosed(context.getRuntime());
         try {
             return context.getRuntime().newFileStat(getOpenFileChecked().getMainStreamSafe().getDescriptor().getFileDescriptor());
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
 
     /** 
      * <p>Invoke a block for each byte.</p>
      */
     public IRubyObject each_byteInternal(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         
     	try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             while (true) {
                 myOpenFile.checkReadable(runtime);
                 myOpenFile.setReadBuffered();
                 waitReadable(myOpenFile.getMainStream());
                 
                 int c = myOpenFile.getMainStreamSafe().fgetc();
                 
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
         return block.isGiven() ? each_byteInternal(context, block) : enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod(name = "bytes")
     public IRubyObject bytes(final ThreadContext context) {
         return enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod(name = "lines", compat = CompatVersion.RUBY1_8)
     public IRubyObject lines(final ThreadContext context, Block block) {
         return enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     @JRubyMethod(name = "lines", compat = CompatVersion.RUBY1_9)
     public IRubyObject lines19(final ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "each_line");
         return each_lineInternal(context, NULL_ARRAY, block);
     }
 
     public IRubyObject each_charInternal(final ThreadContext context, final Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject ch;
 
         while(!(ch = getc()).isNil()) {
             byte c = (byte)RubyNumeric.fix2int(ch);
             int n = runtime.getKCode().getEncoding().length(c);
             RubyString str = runtime.newString();
             if (runtime.is1_9()) str.setEncoding(getExternalEncoding(runtime));
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
 
     @JRubyMethod
     public IRubyObject each_char(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod
     public IRubyObject chars(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal(context, block) : enumeratorize(context.getRuntime(), this, "chars");
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
         return block.isGiven() ? each_char(context, block) : enumeratorize(context.getRuntime(), this, methodName);
     }
 
     private IRubyObject eachCodePointCommon(final ThreadContext context, final Block block, final String methodName) {
         Ruby runtime = context.getRuntime();
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
         Ruby runtime = context.getRuntime();
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
         return block.isGiven() ? each_lineInternal(context, args, block) : enumeratorize(context.getRuntime(), this, "each", args);
     }
 
     @JRubyMethod(optional = 1)
     public IRubyObject each_line(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_lineInternal(context, args, block) : enumeratorize(context.getRuntime(), this, "each_line", args);
     }
 
     @JRubyMethod(optional = 1)
     public RubyArray readlines(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
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
         Ruby runtime = context.getRuntime();
         IRubyObject filename = args[0].convertToString();
         runtime.checkSafeString(filename);
 
         RubyIO io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
         
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
 
     /** rb_io_s_foreach
     *
     */
     public static IRubyObject foreachInternal19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject filename = args[0].convertToString();
         runtime.checkSafeString(filename);
 
         boolean hasOptions = false;
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
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), recv, "foreach", args);
 
         if (!(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             args[0] = args[0].callMethod(context, "to_path");
         }
         return foreachInternal(context, recv, args, block);
     }
 
     @JRubyMethod(name = "foreach", required = 1, optional = 2, meta = true, compat = RUBY1_9)
     public static IRubyObject foreach19(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), recv, "foreach", args);
 
         if (!(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             args[0] = args[0].callMethod(context, "to_path");
         }
         return foreachInternal19(context, recv, args, block);
     }
 
     public static RubyIO convertToIO(ThreadContext context, IRubyObject obj) {
         if (obj instanceof RubyIO) return (RubyIO)obj;
         return (RubyIO)TypeConverter.convertToType(obj, context.getRuntime().getIO(), "to_io");
     }
    
     @JRubyMethod(name = "select", required = 1, optional = 3, meta = true)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return select_static(context, context.getRuntime(), args);
     }
 
     public static IRubyObject select_static(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         return new SelectBlob().goForIt(context, runtime, args);
     }
    
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         switch (args.length) {
         case 0: throw context.getRuntime().newArgumentError(0, 1);
         case 1: return readStatic(context, recv, args[0]);
         case 2: return readStatic(context, recv, args[0], args[1]);
         case 3: return readStatic(context, recv, args[0], args[1], args[2]);
         default: throw context.getRuntime().newArgumentError(args.length, 3);
         }
    }
 
     private static RubyIO newFile(ThreadContext context, IRubyObject recv, IRubyObject... args) {
        return (RubyIO) RubyKernel.open(context, recv, args, Block.NULL_BLOCK);
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
         StringSupport.checkStringSafety(context.getRuntime(), path);
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
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
         StringSupport.checkStringSafety(context.getRuntime(), path);
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
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
         StringSupport.checkStringSafety(context.getRuntime(), path);
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
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
         Ruby runtime = context.getRuntime();
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
     private static IRubyObject write19(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject str, IRubyObject offset, RubyHash options) {
         // FIXME: process options
 
         RubyString pathStr = RubyFile.get_path(context, path);
         Ruby runtime = context.getRuntime();
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
         IRubyObject nil = context.getRuntime().getNil();
         IRubyObject path = args[0];
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
         IRubyObject nil = context.getRuntime().getNil();
         IRubyObject path = args[0];
         IRubyObject length = nil;
         IRubyObject offset = nil;
         RubyHash options = null;
         if (args.length > 3) {
             if (!(args[3] instanceof RubyHash)) throw context.getRuntime().newTypeError("Must be a hash");
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
 
         return read19(context, recv, path, length, offset, (RubyHash) options);
     }
 
     @JRubyMethod(meta = true, required = 2, optional = 1, compat = RUBY1_9)
     public static IRubyObject binwrite(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject nil = context.getRuntime().getNil();
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
             if (!(args[3] instanceof RubyHash)) throw context.getRuntime().newTypeError("Must be a hash");
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
         Ruby runtime = context.getRuntime();
         int mode;
 
         IRubyObject cmdObj = null;
         if (Platform.IS_WINDOWS) {
             String[] tokens = args[0].convertToString().toString().split(" ", 2);
             String commandString = tokens[0].replace('/', '\\') +
                     (tokens.length > 1 ? ' ' + tokens[1] : "");
             cmdObj = runtime.newString(commandString);
         } else {
             cmdObj = args[0].convertToString();
         }
         runtime.checkSafeString(cmdObj);
 
         if ("-".equals(cmdObj.toString())) {
             throw runtime.newNotImplementedError("popen(\"-\") is unimplemented");
         }
 
         try {
             if (args.length == 1) {
                 mode = ModeFlags.RDONLY;
             } else if (args[1] instanceof RubyFixnum) {
                 mode = RubyFixnum.num2int(args[1]);
             } else {
                 mode = getIOModesIntFromString(runtime, args[1].convertToString().toString());
             }
 
             ModeFlags modes = new ModeFlags(mode);
 
             ShellLauncher.POpenProcess process = ShellLauncher.popen(runtime, cmdObj, modes);
 
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
 
             RubyIO io = new RubyIO(runtime, process, modes);
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
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
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
             IRubyObject _cmd = null;
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
 
             runtime.checkSafeString(_cmd);
 
             this.cmd = (RubyString)_cmd;
             this.cmdPlusArgs = _cmdPlusArgs;
             this.env = _env;
         }
     }
 
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject popen19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
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
             if (args.length == 1) {
                 mode = ModeFlags.RDONLY;
             } else if (args[1] instanceof RubyFixnum) {
                 mode = RubyFixnum.num2int(args[1]);
             } else {
                 mode = getIOModesIntFromString(runtime, args[1].convertToString().toString());
             }
 
             ModeFlags modes = new ModeFlags(mode);
 
             ShellLauncher.POpenProcess process;
             if (r19Popen.cmdPlusArgs == null) {
                 process = ShellLauncher.popen(runtime, r19Popen.cmd, modes);
             } else {
                 process = ShellLauncher.popen(runtime, r19Popen.cmdPlusArgs, r19Popen.env, modes);
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
 
             RubyIO io = new RubyIO(runtime, (RubyClass)recv, process, options, modes);
 
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
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
    
     @JRubyMethod(rest = true, meta = true)
     public static IRubyObject popen3(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
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
         Ruby runtime = context.getRuntime();
 
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
         Ruby runtime = context.getRuntime();
 
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
 
     // NIO based pipe
     @JRubyMethod(name = "pipe", meta = true)
     public static IRubyObject pipe(ThreadContext context, IRubyObject recv) {
         // TODO: This isn't an exact port of MRI's pipe behavior, so revisit
         Ruby runtime = context.getRuntime();
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
     
     @JRubyMethod(name = "copy_stream", meta = true, compat = RUBY1_9)
     public static IRubyObject copy_stream(ThreadContext context, IRubyObject recv, 
             IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
 
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
             if (!d1.isSeekable()) {
                 throw context.getRuntime().newTypeError("only supports file-to-file copy");
             }
             ChannelDescriptor d2 = io2.openFile.getMainStreamSafe().getDescriptor();
             if (!d2.isSeekable()) {
                 throw context.getRuntime().newTypeError("only supports file-to-file copy");
             }
 
             FileChannel f1 = (FileChannel)d1.getChannel();
             FileChannel f2 = (FileChannel)d2.getChannel();
 
             try {
                 long size = f1.size();
 
-                f1.transferTo(f2.position(), size, f2);
+                // handle large files on 32-bit JVMs (JRUBY-4913)
+                try {
+                    f1.transferTo(f2.position(), size, f2);
+                } catch (IOException ioe) {
+                    // if the failure is "Cannot allocate memory", do the transfer in 100MB max chunks
+                    if (ioe.getMessage().equals("Cannot allocate memory")) {
+                        long _100M = 100 * 1024 * 1024;
+                        while (size > 0) {
+                            if (size > _100M) {
+                                f1.transferTo(f2.position(), _100M, f2);
+                                size -= _100M;
+                            } else {
+                                f1.transferTo(f2.position(), size, f2);
+                                break;
+                            }
+                        }
+                    } else {
+                        throw ioe;
+                    }
+                }
 
                 return context.getRuntime().newFixnum(size);
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
 
     @JRubyMethod(name = "try_convert", meta = true, backtrace = true, compat = RUBY1_9)
     public static IRubyObject tryConvert(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return arg.respondsTo("to_io") ? convertToIO(context, arg) : context.getRuntime().getNil();
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
     protected ModeFlags updateModesFromOptions(ThreadContext context, RubyHash options, ModeFlags modes) {
         if (options == null || options.isNil()) return modes;
 
         Ruby runtime = context.getRuntime();
 
         if (options.containsKey(runtime.newSymbol("mode"))) {
             modes = parseModes19(context, options.fastARef(runtime.newSymbol("mode")).asString());
         }
 
         // This duplicates the non-error behavior of MRI 1.9: the
         // :binmode option is ORed in with other options. It does
         // not obliterate what came before.
 
         if (options.containsKey(runtime.newSymbol("binmode")) &&
                 options.fastARef(runtime.newSymbol("binmode")).isTrue()) {
             try {
                 modes = new ModeFlags(modes.getFlags() | ModeFlags.BINARY);
             } catch (InvalidValueException e) {
                 /* n.b., this should be unreachable
                     because we are changing neither read-only nor append
                 */
                 throw getRuntime().newErrnoEINVALError();
             }
         }
 
         // This duplicates the non-error behavior of MRI 1.9: the
         // :binmode option is ORed in with other options. It does
         // not obliterate what came before.
 
         if (options.containsKey(runtime.newSymbol("binmode")) &&
                 options.fastARef(runtime.newSymbol("binmode")).isTrue()) {
             try {
                 modes = new ModeFlags(modes.getFlags() | ModeFlags.BINARY);
             } catch (InvalidValueException e) {
                 /* n.b., this should be unreachable
                     because we are changing neither read-only nor append
                 */
                 throw getRuntime().newErrnoEINVALError();
             }
         }
 
 //      FIXME: check how ruby 1.9 handles this
 
 //        if (rubyOptions.containsKey(runtime.newSymbol("textmode")) &&
 //                rubyOptions.fastARef(runtime.newSymbol("textmode")).isTrue()) {
 //            try {
 //                modes = getIOModes(runtime, "t");
 //            } catch (InvalidValueException e) {
 //                throw getRuntime().newErrnoEINVALError();
 //            }
 //        }
 //
 //        if (rubyOptions.containsKey(runtime.newSymbol("binmode")) &&
 //                rubyOptions.fastARef(runtime.newSymbol("binmode")).isTrue()) {
 //            try {
 //                modes = getIOModes(runtime, "b");
 //            } catch (InvalidValueException e) {
 //                throw getRuntime().newErrnoEINVALError();
 //            }
 //        }
 
         return modes;
     }
 
     /**
      * See http://ruby-doc.org/core-1.9.3/IO.html#method-c-new for the format of encodings in options
      */
     protected void setEncodingsFromOptions(ThreadContext context, RubyHash options) {
         if (options == null || options.isNil()) return;
 
         EncodingOption encodingOption = extractEncodingOptions(options);
 
         if (encodingOption == null) return;
 
         externalEncoding = encodingOption.externalEncoding;
         if (encodingOption.internalEncoding == externalEncoding) return;
         internalEncoding = encodingOption.internalEncoding;
     }
 
     public static class EncodingOption {
         private Encoding externalEncoding;
         private Encoding internalEncoding;
         private boolean bom;
 
         public EncodingOption(Encoding externalEncoding, Encoding internalEncoding, boolean bom) {
             this.externalEncoding = externalEncoding;
             this.internalEncoding = internalEncoding;
             this.bom = bom;
         }
 
         public Encoding getExternalEncoding() {
             return externalEncoding;
         }
 
         public Encoding getInternalEncoding() {
             return internalEncoding;
         }
 
         public boolean hasBom() {
             return bom;
         }
     }
 
     // c: rb_io_extract_encoding_option
     public static EncodingOption extractEncodingOptions(IRubyObject options) {
         if (options == null || options.isNil() || !(options instanceof RubyHash)) return null;
 
         RubyHash opts = (RubyHash) options;
 
         Ruby runtime = options.getRuntime();
         IRubyObject encOption = opts.fastARef(runtime.newSymbol("encoding"));
         IRubyObject extOption = opts.fastARef(runtime.newSymbol("external_encoding"));
         IRubyObject intOption = opts.fastARef(runtime.newSymbol("internal_encoding"));
         if (encOption != null && !encOption.isNil()) {
             if (extOption != null) {
                 runtime.getWarnings().warn(
                         "Ignoring encoding parameter '" + encOption
                                 + "': external_encoding is used");
                 encOption = runtime.getNil();
             } else if (intOption != null) {
                 runtime.getWarnings().warn(
                         "Ignoring encoding parameter '" + encOption
                                 + "': internal_encoding is used");
                 encOption = runtime.getNil();
             } else {
                 IRubyObject tmp = encOption.checkStringType19();
                 if (!tmp.isNil()) {
                     return parseModeEncodingOption(runtime, tmp.convertToString().toString());
                 }
                 return createEncodingOption(runtime, runtime.getEncodingService()
                         .getEncodingFromObject(encOption), null, false);
             }
         }
         boolean set = false;
         Encoding extEncoding = null;
         Encoding intEncoding = null;
 
         if (extOption != null) {
             set = true;
             if (!extOption.isNil()) {
                 extEncoding = runtime.getEncodingService().getEncodingFromObject(extOption);
             }
         }
         if (intOption != null) {
             set = true;
             if (intOption.isNil()) {
                 // null;
             } else if (intOption.convertToString().toString().equals("-")) {
                 // null;
             } else {
                 intEncoding = runtime.getEncodingService().getEncodingFromObject(intOption);
             }
         }
         if (!set)
             return null;
 
         return createEncodingOption(runtime, extEncoding, intEncoding, false);
     }
 
     // c: parse_mode_enc
     private static EncodingOption parseModeEncodingOption(Ruby runtime, String option) {
         Encoding extEncoding = null;
         Encoding intEncoding = null;
         boolean isBom = false;
         String[] encs = option.split(":", 2);
         if (encs[0].toLowerCase().startsWith("bom|utf-")) {
             isBom = true;
             encs[0] = encs[0].substring(4);
         }
         extEncoding = runtime.getEncodingService().getEncodingFromObject(runtime.newString(encs[0]));
         if (encs.length > 1) {
             if (encs[1].equals("-")) {
                 // null;
             } else {
                 intEncoding = runtime.getEncodingService().getEncodingFromObject(runtime.newString(encs[1]));
             }
         }
         return createEncodingOption(runtime, extEncoding, intEncoding, isBom);
     }
 
     private static EncodingOption createEncodingOption(Ruby runtime, Encoding extEncoding,
             Encoding intEncoding, boolean isBom) {
         if (extEncoding == null) {
             extEncoding = runtime.getDefaultExternalEncoding();
         }
         if (intEncoding == null) {
             intEncoding = runtime.getDefaultInternalEncoding();
         }
         // NOTE: This logic used to do checks for int == ext, etc, like in rb_io_ext_int_to_encs,
         // but that logic seems specific to how MRI's IO sets up "enc" and "enc2". We explicitly separate
         // external and internal, so consumers should decide how to deal with int == ext.
         return new EncodingOption(extEncoding, intEncoding, isBom);
     }
 
     private static final String[] UNSUPPORTED_SPAWN_OPTIONS = new String[] {
             "unsetenv_others",
             "prgroup",
             "rlimit_resourcename",
             "chdir",
             "umask",
             "in",
             "out",
             "err",
             "close_others"
     };
 
     private static final String[] UNSUPPORTED_EXEC_OPTIONS = UNSUPPORTED_SPAWN_OPTIONS;
     private static final String[] UNSUPPORTED_POPEN_OPTIONS = UNSUPPORTED_SPAWN_OPTIONS;
 
     /**
      * Warn when using exec with unsupported options.
      *
      * @param options
      */
     public static void checkExecOptions(IRubyObject options) {
         checkOptions(options, UNSUPPORTED_SPAWN_OPTIONS, "unsupported exec option");
     }
 
     /**
      * Warn when using spawn with unsupported options.
      *
      * @param options
      */
     public static void checkSpawnOptions(IRubyObject options) {
         checkOptions(options, UNSUPPORTED_SPAWN_OPTIONS, "unsupported spawn option");
     }
 
     /**
      * Warn when using spawn with unsupported options.
      *
      * @param options
      */
     public static void checkPopenOptions(IRubyObject options) {
         checkOptions(options, UNSUPPORTED_POPEN_OPTIONS, "unsupported popen option");
     }
 
     /**
      * Warn when using spawn with unsupported options.
      *
      * @param options
      */
     private static void checkOptions(IRubyObject options, String[] unsupported, String error) {
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
     public boolean writeDataBuffered() {
         return openFile.getMainStream().writeDataBuffered();
     }
 
     @Deprecated
     public IRubyObject gets(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? gets(context) : gets(context, args[0]);
     }
 
     @Deprecated
     public IRubyObject readline(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? readline(context) : readline(context, args[0]);
     }
     
     protected OpenFile openFile;
     protected List<RubyThread> blockingThreads;
     protected Encoding externalEncoding;
     protected Encoding internalEncoding;
     /**
      * If the stream is being used for popen, we don't want to destroy the process
      * when we close the stream.
      */
     protected boolean popenSpecial;
 }
