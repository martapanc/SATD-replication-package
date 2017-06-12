diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index de916a8b7b..cc9130396f 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -2808,1474 +2808,1481 @@ public class RubyIO extends RubyObject {
                 return runtime.getNil();
             }
 
             str.setValue(newBuffer);
             str.setTaint(true);
 
             return str;
         } catch (EOFException ex) {
             throw runtime.newEOFError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         }
     }
 
     // implements latter part of io_read in io.c
     private IRubyObject readNotAll(ThreadContext context, OpenFile myOpenFile, int length) {
         Ruby runtime = context.getRuntime();
 
         try {
             ByteList newBuffer = readNotAllCommon(context, myOpenFile, length);
 
             if (emptyBufferOrEOF(newBuffer, myOpenFile)) {
                 return runtime.getNil();
             }
 
             RubyString str = RubyString.newString(runtime, newBuffer);
             str.setTaint(true);
 
             return str;
         } catch (EOFException ex) {
             throw runtime.newEOFError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         }
     }
 
     private ByteList readNotAllCommon(ThreadContext context, OpenFile myOpenFile, int length) {
         Ruby runtime = context.getRuntime();
 
         try {
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             if (myOpenFile.getMainStreamSafe().feof()) {
                 return null;
             }
 
             // READ_CHECK from MRI io.c
             readCheck(myOpenFile.getMainStreamSafe());
 
             ByteList newBuffer = fread(context.getThread(), length);
 
             return newBuffer;
         } catch (EOFException ex) {
             throw runtime.newEOFError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         }
     }
 
     protected static boolean emptyBufferOrEOF(ByteList buffer, OpenFile myOpenFile) throws BadDescriptorException, IOException {
         if (buffer == null) {
             return true;
         } else if (buffer.length() == 0) {
             if (myOpenFile.getMainStreamSafe() == null) {
                 return true;
             }
 
             if (myOpenFile.getMainStreamSafe().feof()) {
                 return true;
             }
         }
         return false;
     }
     
     // implements read_all() in io.c
     protected RubyString readAll(RubyString str) throws BadDescriptorException, EOFException, IOException {
         Ruby runtime = getRuntime();
 
         // TODO: handle writing into original buffer better
         ByteList buf = readAllCommon(runtime);
         
         if (buf == null) {
             str.empty();
         } else {
             str.setValue(buf);
         }
         str.setTaint(true);
         return str;
     }
 
     // implements read_all() in io.c
     protected RubyString readAll() throws BadDescriptorException, EOFException, IOException {
         Ruby runtime = getRuntime();
 
         // TODO: handle writing into original buffer better
         ByteList buf = readAllCommon(runtime);
 
         RubyString str;
         if (buf == null) {
             str = RubyString.newEmptyString(runtime);
         } else {
             str = makeString(runtime, buf, false);
         }
         str.setTaint(true);
         return str;
     }
 
     protected ByteList readAllCommon(Ruby runtime) throws BadDescriptorException, EOFException, IOException {
         ByteList buf = null;
         ChannelDescriptor descriptor = openFile.getMainStreamSafe().getDescriptor();
         try {
             // ChannelStream#readall knows what size should be allocated at first. Just use it.
             if (descriptor.isSeekable() && descriptor.getChannel() instanceof FileChannel) {
                 buf = openFile.getMainStreamSafe().readall();
             } else {
                 RubyThread thread = runtime.getCurrentContext().getThread();
                 try {
                     while (true) {
                         // TODO: ruby locks the string here
                         Stream stream = openFile.getMainStreamSafe();
                         readCheck(stream);
                         openFile.checkReadable(runtime);
                         ByteList read = fread(thread, ChannelStream.BUFSIZE);
                             
                         // TODO: Ruby unlocks the string here
                         if (read.length() == 0) {
                             break;
                         }
                         if (buf == null) {
                             buf = read;
                         } else {
                             buf.append(read);
                         }
                     }
                 } catch (InvalidValueException ex) {
                     throw runtime.newErrnoEINVALError();
                 }
             }
         } catch (NonReadableChannelException ex) {
             throw runtime.newIOError("not opened for reading");
         }
 
         return buf;
     }
 
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
 
             RubyIO io = new RubyIO(runtime, process, modes);
 
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
 
+            // ensure the OpenFile knows it's a process; see OpenFile#finalize
+            input.getOpenFile().setProcess(process);
+            output.getOpenFile().setProcess(process);
+            error.getOpenFile().setProcess(process);
+            
+            // process streams are not seekable
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
 
                 f1.transferTo(f2.position(), size, f2);
 
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
      *
      *  ==== Options
      *  <code>opt</code> can have the following keys
      *  :mode ::
      *    same as <code>mode</code> parameter
      *  :external_encoding ::
      *    external encoding for the IO. "-" is a
      *    synonym for the default external encoding.
      *  :internal_encoding ::
      *    internal encoding for the IO.
      *    "-" is a synonym for the default internal encoding.
      *    If the value is nil no conversion occurs.
      *  :encoding ::
      *    specifies external and internal encodings as "extern:intern".
      *  :textmode ::
      *    If the value is truth value, same as "b" in argument <code>mode</code>.
      *  :binmode ::
      *    If the value is truth value, same as "t" in argument <code>mode</code>.
      *
      *  Also <code>opt</code> can have same keys in <code>String#encode</code> for
      *  controlling conversion between the external encoding and the internal encoding.
      *
      */
     protected ModeFlags parseOptions(ThreadContext context, IRubyObject options, ModeFlags modes) {
         Ruby runtime = context.getRuntime();
 
         RubyHash rubyOptions = (RubyHash) options;
 
         IRubyObject internalEncodingOption = rubyOptions.fastARef(runtime.newSymbol("internal_encoding"));
         IRubyObject externalEncodingOption = rubyOptions.fastARef(runtime.newSymbol("external_encoding"));
         RubyString dash = runtime.newString("-");
         if (externalEncodingOption != null && !externalEncodingOption.isNil()) {
             if (dash.eql(externalEncodingOption)) {
                 externalEncodingOption = runtime.getEncodingService().getDefaultExternal();
             }
             setExternalEncoding(context, externalEncodingOption);
         }
 
         if (internalEncodingOption != null && !internalEncodingOption.isNil()) {
             if (dash.eql(internalEncodingOption)) {
                 internalEncodingOption = runtime.getEncodingService().getDefaultInternal();
             }
             setInternalEncoding(context, internalEncodingOption);
         }
 
         IRubyObject encoding = rubyOptions.fastARef(runtime.newSymbol("encoding"));
         if (encoding != null && !encoding.isNil()) {
             if (externalEncodingOption != null && !externalEncodingOption.isNil()) {
                 runtime.getWarnings().warn("Ignoring encoding parameter '"+ encoding +"': external_encoding is used");
             } else if (internalEncodingOption != null && !internalEncodingOption.isNil()) {
                 runtime.getWarnings().warn("Ignoring encoding parameter '"+ encoding +"': internal_encoding is used");
             } else {
                 parseEncodingFromString(context, encoding, 0);
             }
         }
 
         if (rubyOptions.containsKey(runtime.newSymbol("mode"))) {
             modes = parseModes19(context, rubyOptions.fastARef(runtime.newSymbol("mode")).asString());
         }
 
     // This duplicates the non-error behavior of MRI 1.9: the
     // :binmode option is ORed in with other options. It does
     // not obliterate what came before.
 
     if (rubyOptions.containsKey(runtime.newSymbol("binmode")) &&
         rubyOptions.fastARef(runtime.newSymbol("binmode")).isTrue()) {
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
         RubyHash opts = (RubyHash) options;
         if (opts.isNil()) {
             return null;
         }
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
         boolean defaultExt = false;
         if (extEncoding == null) {
             extEncoding = runtime.getDefaultExternalEncoding();
             defaultExt = true;
         }
         if (intEncoding == null
                 && extEncoding != runtime.getEncodingService().getAscii8bitEncoding()) {
             intEncoding = runtime.getDefaultInternalEncoding();
         }
         if (intEncoding == null || intEncoding == extEncoding) {
             if (defaultExt && intEncoding != extEncoding) {
                 intEncoding = null;
             } else {
                 intEncoding = extEncoding;
             }
             extEncoding = null;
         }
         return new EncodingOption(extEncoding, intEncoding, isBom);
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
      * @param runtime The Ruby runtime, for raising an error
      * @param process The process to obliterate
      */
     public static void obliterateProcess(Process process) {
         int i = 0;
         Object waitLock = new Object();
         while (true) {
             // only try 1000 times with a 1ms sleep between, so we don't hang
-            // forever on processes that ignore SIGTERM
+            // forever on processes that ignore SIGTERM. After that, not much
+            // we can do...
             if (i >= 1000) {
-                throw new RuntimeException("could not shut down process: " + process);
+                return;
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
 }
diff --git a/src/org/jruby/util/ShellLauncher.java b/src/org/jruby/util/ShellLauncher.java
index fbe691e914..3cf9599ff7 100644
--- a/src/org/jruby/util/ShellLauncher.java
+++ b/src/org/jruby/util/ShellLauncher.java
@@ -1,1510 +1,1509 @@
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
 import java.util.Collections;
 import java.util.HashMap;
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
 import jnr.posix.POSIX;
 import jnr.posix.util.FieldAccess;
 import jnr.posix.util.Platform;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.ext.rbconfig.RbConfigLibrary;
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
         RubyHash hash = (RubyHash)runtime.getObject().getConstant("ENV");
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
         RubyHash env = (RubyHash) runtime.getObject().getConstant("ENV");
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
 
     public static long[] runAndWaitPid(Ruby runtime, IRubyObject[] rawArgs) {
         return runAndWaitPid(runtime, rawArgs, runtime.getOutputStream(), true);
     }
 
     public static long runWithoutWait(Ruby runtime, IRubyObject[] rawArgs) {
         return runWithoutWait(runtime, rawArgs, runtime.getOutputStream());
     }
 
     public static int runExternalAndWait(Ruby runtime, IRubyObject[] rawArgs, Map mergeEnv) {
         OutputStream output = runtime.getOutputStream();
         OutputStream error = runtime.getErrorStream();
         InputStream input = runtime.getInputStream();
         Process aProcess = null;
         File pwd = new File(runtime.getCurrentDirectory());
         LaunchConfig cfg = new LaunchConfig(runtime, rawArgs, true);
 
         try {
             try {
                 if (cfg.shouldRunInShell()) {
                     log(runtime, "Launching with shell");
                     // execute command with sh -c
                     // this does shell expansion of wildcards
                     cfg.verifyExecutableForShell();
                     aProcess = Runtime.getRuntime().exec(cfg.getExecArgs(), getCurrentEnv(runtime, mergeEnv), pwd);
                 } else {
                     log(runtime, "Launching directly (no shell)");
                     cfg.verifyExecutableForDirect();
                     aProcess = Runtime.getRuntime().exec(cfg.getExecArgs(), getCurrentEnv(runtime, mergeEnv), pwd);
                 }
             } catch (SecurityException se) {
                 throw runtime.newSecurityError(se.getLocalizedMessage());
             }
             handleStreams(runtime, aProcess, input, output, error);
             return aProcess.waitFor();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     public static long runExternalWithoutWait(Ruby runtime, IRubyObject env, IRubyObject prog, IRubyObject options, IRubyObject args) {
         return runExternal(runtime, env, prog, options, args, false);
     }
 
     public static long runExternal(Ruby runtime, IRubyObject env, IRubyObject prog, IRubyObject options, IRubyObject args, boolean wait) {
         if (env.isNil() || !(env instanceof Map)) {
             env = null;
         }
         
         IRubyObject[] rawArgs = args.convertToArray().toJavaArray();
         
         OutputStream output = runtime.getOutputStream();
         OutputStream error = runtime.getErrorStream();
         InputStream input = runtime.getInputStream();
         
         try {
             Process aProcess = null;
             File pwd = new File(runtime.getCurrentDirectory());
             LaunchConfig cfg = new LaunchConfig(runtime, rawArgs, true);
 
             try {
                 if (cfg.shouldRunInShell()) {
                     log(runtime, "Launching with shell");
                     // execute command with sh -c
                     // this does shell expansion of wildcards
                     cfg.verifyExecutableForShell();
                     aProcess = Runtime.getRuntime().exec(cfg.getExecArgs(), getCurrentEnv(runtime, (Map)env), pwd);
                 } else {
                     log(runtime, "Launching directly (no shell)");
                     cfg.verifyExecutableForDirect();
                     aProcess = Runtime.getRuntime().exec(cfg.getExecArgs(), getCurrentEnv(runtime, (Map)env), pwd);
                 }
             } catch (SecurityException se) {
                 throw runtime.newSecurityError(se.getLocalizedMessage());
             }
             
             if (wait) {
                 handleStreams(runtime, aProcess, input, output, error);
                 try {
                     return aProcess.waitFor();
                 } catch (InterruptedException e) {
                     throw runtime.newThreadError("unexpected interrupt");
                 }
             } else {
                 handleStreamsNonblocking(runtime, aProcess, runtime.getOutputStream(), error);
                 return getPidFromProcess(aProcess);
             }
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     public static long runExternalWithoutWait(Ruby runtime, IRubyObject[] rawArgs) {
         return runWithoutWait(runtime, rawArgs, runtime.getOutputStream());
     }
 
     public static int execAndWait(Ruby runtime, IRubyObject[] rawArgs) {
         return execAndWait(runtime, rawArgs, Collections.EMPTY_MAP);
     }
 
     public static int execAndWait(Ruby runtime, IRubyObject[] rawArgs, Map mergeEnv) {
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
                         runtime, cfg.getExecArgs(), getCurrentEnv(runtime, mergeEnv), pwd, false);
                 ipScript.start();
                 return ipScript.waitFor();
             } catch (IOException e) {
                 throw runtime.newIOErrorFromException(e);
             } catch (InterruptedException e) {
                 throw runtime.newThreadError("unexpected interrupt");
             }
         } else {
             return runExternalAndWait(runtime, rawArgs, mergeEnv);
         }
     }
 
     public static int runAndWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output) {
         return runAndWait(runtime, rawArgs, output, true);
     }
 
     public static int runAndWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output, boolean doExecutableSearch) {
         return (int)runAndWaitPid(runtime, rawArgs, output, doExecutableSearch)[0];
     }
 
     public static long[] runAndWaitPid(Ruby runtime, IRubyObject[] rawArgs, OutputStream output, boolean doExecutableSearch) {
         OutputStream error = runtime.getErrorStream();
         InputStream input = runtime.getInputStream();
         try {
             Process aProcess = run(runtime, rawArgs, doExecutableSearch);
             handleStreams(runtime, aProcess, input, output, error);
             return new long[] {aProcess.waitFor(), getPidFromProcess(aProcess)};
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     private static long runWithoutWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output) {
         OutputStream error = runtime.getErrorStream();
         try {
             Process aProcess = run(runtime, rawArgs, true);
             handleStreamsNonblocking(runtime, aProcess, output, error);
             return getPidFromProcess(aProcess);
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     private static long runExternalWithoutWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output) {
         OutputStream error = runtime.getErrorStream();
         try {
             Process aProcess = run(runtime, rawArgs, true, true);
             handleStreamsNonblocking(runtime, aProcess, output, error);
             return getPidFromProcess(aProcess);
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
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
                                 return WindowsFFI.getKernel32().GetProcessId(hproc.intValue());
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
                             return WindowsFFI.getKernel32().GetProcessId(hproc.intValue());
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
 
     public static POpenProcess popen3(Ruby runtime, IRubyObject[] strings, boolean addShell) throws IOException {
         return new POpenProcess(popenShared(runtime, strings, null, addShell));
     }
 
     private static Process popenShared(Ruby runtime, IRubyObject[] strings) throws IOException {
         return popenShared(runtime, strings, null);
     }
 
     private static Process popenShared(Ruby runtime, IRubyObject[] strings, Map env) throws IOException {
         return popenShared(runtime, strings, env, true);
     }
 
     private static Process popenShared(Ruby runtime, IRubyObject[] strings, Map env, boolean addShell) throws IOException {
         String shell = getShell(runtime);
         Process childProcess = null;
         File pwd = new File(runtime.getCurrentDirectory());
 
         try {
             String[] args = parseCommandLine(runtime.getCurrentContext(), runtime, strings);
             boolean useShell = false;
             if (addShell) for (String arg : args) useShell |= shouldUseShell(arg);
             
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
         private final boolean waitForChild;
 
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
 
         public POpenProcess(Process child, Ruby runtime, ModeFlags modes) {
             this.child = child;
 
             if (modes.isWritable()) {
                 this.waitForChild = true;
                 prepareOutput(child);
             } else {
                 this.waitForChild = false;
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
             this.waitForChild = false;
 
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
             return child.waitFor();
         }
 
         @Override
         public int exitValue() {
             return child.exitValue();
         }
 
         @Override
         public void destroy() {
             try {
-                if (input != null) input.close();
-                if (inerr != null) inerr.close();
-                if (output != null) output.close();
-                if (inputChannel != null) inputChannel.close();
-                if (inerrChannel != null) inerrChannel.close();
-                if (outputChannel != null) outputChannel.close();
+                // We try to safely close all streams and channels to the greatest
+                // extent possible.
+                try {if (input != null) input.close();} catch (Exception e) {}
+                try {if (inerr != null) inerr.close();} catch (Exception e) {}
+                try {if (output != null) output.close();} catch (Exception e) {}
+                try {if (inputChannel != null) inputChannel.close();} catch (Exception e) {}
+                try {if (inerrChannel != null) inerrChannel.close();} catch (Exception e) {}
+                try {if (outputChannel != null) outputChannel.close();} catch (Exception e) {}
 
                 // processes seem to have some peculiar locking sequences, so we
                 // need to ensure nobody is trying to close/destroy while we are
                 synchronized (this) {
                     if (inputPumper != null) synchronized(inputPumper) {inputPumper.quit();}
                     if (inerrPumper != null) synchronized(inerrPumper) {inerrPumper.quit();}
                     if (waitForChild) {
                         waitFor();
                     } else {
                         RubyIO.obliterateProcess(child);
                     }
                 }
-            } catch (IOException ioe) {
-                throw new RuntimeException(ioe);
             } catch (InterruptedException ie) {
                 Thread.currentThread().interrupt();
-                throw new RuntimeException(ie);
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
             if (!runtime.getInstanceConfig().isRunRubyInProcess()
                     || RubyInstanceConfig.hasLoadedNativeExtensions()) {
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
                 if (args.length > 1) {
                     for (int i = 1; i < args.length; i++) {
                         checkGlobChar(args[i]);
                     }
                 }
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
         
         private void checkGlobChar(String word) {
             if (word.contains("*")
                     || word.contains("?")
                     || word.contains("[")
                     || word.contains("{")) {
                 runtime.getErr().println("Warning: treating '" + word + "' literally."
                         + " Consider passing -J-Djruby.launch.inproc=false.");
             }
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
         return run(runtime, rawArgs, doExecutableSearch, false);
     }
 
     public static Process run(Ruby runtime, IRubyObject[] rawArgs, boolean doExecutableSearch, boolean forceExternalProcess) throws IOException {
         Process aProcess = null;
         File pwd = new File(runtime.getCurrentDirectory());
         LaunchConfig cfg = new LaunchConfig(runtime, rawArgs, doExecutableSearch);
 
         try {
             if (!forceExternalProcess && cfg.shouldRunInProcess()) {
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
diff --git a/src/org/jruby/util/io/OpenFile.java b/src/org/jruby/util/io/OpenFile.java
index e4ddc42585..83a7878606 100644
--- a/src/org/jruby/util/io/OpenFile.java
+++ b/src/org/jruby/util/io/OpenFile.java
@@ -1,365 +1,371 @@
 package org.jruby.util.io;
 
 import java.io.IOException;
 import org.jruby.Ruby;
 import org.jruby.util.ShellLauncher;
 
 public class OpenFile {
 
     public static final int READABLE = 1;
     public static final int WRITABLE = 2;
     public static final int READWRITE = 3;
     public static final int APPEND = 64;
     public static final int CREATE = 128;
     public static final int BINMODE = 4;
     public static final int SYNC = 8;
     public static final int WBUF = 16;
     public static final int RBUF = 32;
     public static final int WSPLIT = 512;
     public static final int WSPLIT_INITIALIZED = 1024;
     public static final int SYNCWRITE = SYNC | WRITABLE;
 
     public static interface Finalizer {
 
         public void finalize(Ruby runtime, boolean raise);
     }
     private Stream mainStream;
     private Stream pipeStream;
     private int mode;
     private Process process;
     private int lineNumber = 0;
     private String path;
     private Finalizer finalizer;
 
     public Stream getMainStream() {
         return mainStream;
     }
 
     public Stream getMainStreamSafe() throws BadDescriptorException {
         Stream stream = mainStream;
         if (stream == null) throw new BadDescriptorException();
         return stream;
     }
 
     public void setMainStream(Stream mainStream) {
         this.mainStream = mainStream;
     }
 
     public Stream getPipeStream() {
         return pipeStream;
     }
 
     public Stream getPipeStreamSafe() throws BadDescriptorException {
         Stream stream = pipeStream;
         if (stream == null) throw new BadDescriptorException();
         return stream;
     }
 
     public void setPipeStream(Stream pipeStream) {
         this.pipeStream = pipeStream;
     }
 
     public Stream getWriteStream() {
         return pipeStream == null ? mainStream : pipeStream;
     }
 
     public Stream getWriteStreamSafe() throws BadDescriptorException {
         Stream stream = pipeStream == null ? mainStream : pipeStream;
         if (stream == null) throw new BadDescriptorException();
         return stream;
     }
 
     public int getMode() {
         return mode;
     }
 
     public String getModeAsString(Ruby runtime) {
         String modeString = getStringFromMode(mode);
 
         if (modeString == null) {
             throw runtime.newArgumentError("Illegal access modenum " + Integer.toOctalString(mode));
         }
 
         return modeString;
     }
 
     public static String getStringFromMode(int mode) {
         if ((mode & APPEND) != 0) {
             if ((mode & READWRITE) != 0) {
                 return "ab+";
             }
             return "ab";
         }
         switch (mode & READWRITE) {
         case READABLE:
             return "rb";
         case WRITABLE:
             return "wb";
         case READWRITE:
             if ((mode & CREATE) != 0) {
                 return "wb+";
             }
             return "rb+";
         }
         return null;
     }
 
     public void checkReadable(Ruby runtime) throws IOException, BadDescriptorException, InvalidValueException {
         checkClosed(runtime);
 
         if ((mode & READABLE) == 0) {
             throw runtime.newIOError("not opened for reading");
         }
 
         if (((mode & WBUF) != 0 || (mode & (SYNCWRITE | RBUF)) == SYNCWRITE) &&
                 !mainStream.feof() && pipeStream == null) {
             try {
                 // seek to force underlying buffer to flush
                 seek(0, Stream.SEEK_CUR);
             } catch (PipeException p) {
                 // ignore unseekable streams for purposes of checking readability
             } catch (IOException ioe) {
                 // MRI ignores seek errors, presumably for unseekable files like
                 // serial ports (JRUBY-2979), so we shall too.
             }
         }
     }
 
     public void seek(long offset, int whence) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         Stream stream = getWriteStreamSafe();
 
         seekInternal(stream, offset, whence);
     }
 
     private void seekInternal(Stream stream, long offset, int whence) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         flushBeforeSeek(stream);
 
         stream.lseek(offset, whence);
     }
 
     private void flushBeforeSeek(Stream stream) throws BadDescriptorException, IOException {
         if ((mode & WBUF) != 0) {
             fflush(stream);
         }
     }
 
     public void fflush(Stream stream) throws IOException, BadDescriptorException {
         while (true) {
             int n = stream.fflush();
             if (n != -1) {
                 break;
             }
         }
         mode &= ~WBUF;
     }
 
     public void checkWritable(Ruby runtime) throws IOException, BadDescriptorException, InvalidValueException {
         checkClosed(runtime);
         if ((mode & WRITABLE) == 0) {
             throw runtime.newIOError("not opened for writing");
         }
         if ((mode & RBUF) != 0 && !mainStream.feof() && pipeStream == null) {
             try {
                 // seek to force read buffer to invalidate
                 seek(0, Stream.SEEK_CUR);
             } catch (PipeException p) {
                 // ignore unseekable streams for purposes of checking readability
             } catch (IOException ioe) {
                 // MRI ignores seek errors, presumably for unseekable files like
                 // serial ports (JRUBY-2979), so we shall too.
             }
         }
         if (pipeStream == null) {
             mode &= ~RBUF;
         }
     }
 
     public void checkClosed(Ruby runtime) {
         if (mainStream == null && pipeStream == null) {
             throw runtime.newIOError("closed stream");
         }
     }
 
     public boolean isBinmode() {
         return (mode & BINMODE) != 0;
     }
 
     public void setBinmode() {
         mode |= BINMODE;
         if (mainStream != null) {
             mainStream.setBinmode();
         }
     }
 
     public boolean isOpen() {
         return mainStream != null || pipeStream != null;
     }
 
     public boolean isReadable() {
         return (mode & READABLE) != 0;
     }
 
     public boolean isWritable() {
         return (mode & WRITABLE) != 0;
     }
 
     public boolean isReadBuffered() {
         return (mode & RBUF) != 0;
     }
 
     public void setReadBuffered() {
         mode |= RBUF;
     }
 
     public boolean isWriteBuffered() {
         return (mode & WBUF) != 0;
     }
 
     public void setWriteBuffered() {
         mode |= WBUF;
     }
 
     public void setSync(boolean sync) {
         if(sync) {
             mode = mode | SYNC;
         } else {
             mode = mode & ~SYNC;
         }
     }
 
     public boolean isSync() {
         return (mode & SYNC) != 0;
     }
 
     public boolean areBothEOF() throws IOException, BadDescriptorException {
         return mainStream.feof() && (pipeStream != null ? pipeStream.feof() : true);
     }
 
     public void setMode(int modes) {
         this.mode = modes;
     }
 
     public Process getProcess() {
         return process;
     }
 
     public void setProcess(Process process) {
         this.process = process;
     }
 
     public long getPid() {
         return ShellLauncher.getPidFromProcess(process);
     }
 
     public int getLineNumber() {
         return lineNumber;
     }
 
     public void setLineNumber(int lineNumber) {
         this.lineNumber = lineNumber;
     }
 
     public String getPath() {
         return path;
     }
 
     public void setPath(String path) {
         this.path = path;
     }
 
     public boolean isAutoclose() {
         boolean autoclose = true;
         Stream myMain, myPipe;
         if ((myMain = mainStream) != null) autoclose &= myMain.isAutoclose();
         if ((myPipe = pipeStream) != null) autoclose &= myPipe.isAutoclose();
         return autoclose;
     }
 
     public void setAutoclose(boolean autoclose) {
         Stream myMain, myPipe;
         if ((myMain = mainStream) != null) myMain.setAutoclose(autoclose);
         if ((myPipe = pipeStream) != null) myPipe.setAutoclose(autoclose);
     }
 
     public Finalizer getFinalizer() {
         return finalizer;
     }
 
     public void setFinalizer(Finalizer finalizer) {
         this.finalizer = finalizer;
     }
 
     public void cleanup(Ruby runtime, boolean raise) {
         if (finalizer != null) {
             finalizer.finalize(runtime, raise);
         } else {
             finalize(runtime, raise);
         }
     }
 
     public void finalize(Ruby runtime, boolean raise) {
         try {
             ChannelDescriptor main = null;
             ChannelDescriptor pipe = null;
             
             // Recent JDKs shut down streams in the parent when child
             // terminates, so we can't trust that they'll be open for our
             // close. Check for that.
             
             boolean isProcess = process != null;
 
             synchronized (this) {
                 Stream ps = pipeStream;
                 if (ps != null) {
                     pipe = ps.getDescriptor();
 
                     try {
-                        // check for closed channel due to child exit
+                        // Newer JDKs actively close the process streams when
+                        // the child exits, so we have to confirm it's still
+                        // open to avoid raising an error here when we try to
+                        // flush and close the stream.
                         if (isProcess && ps.getChannel().isOpen()
                                 || !isProcess) {
                             ps.fflush();
                             ps.fclose();
                         }
                     } finally {
                         // make sure the pipe stream is set to null
                         pipeStream = null;
                     }
                 }
                 Stream ms = mainStream;
                 if (ms != null) {
                     // TODO: Ruby logic is somewhat more complicated here, see comments after
                     main = ms.getDescriptor();
                     runtime.removeFilenoIntMap(main.getFileno());
                     try {
-                        // check for closed channel due to child exit
+                        // Newer JDKs actively close the process streams when
+                        // the child exits, so we have to confirm it's still
+                        // open to avoid raising an error here when we try to
+                        // flush and close the stream.
                         if (isProcess && ms.getChannel().isOpen()
                                 || !isProcess) {
                             if (pipe == null && isWriteBuffered()) {
                                 ms.fflush();
                             }
                             ms.fclose();
                         }
                     } catch (BadDescriptorException bde) {
                         if (main == pipe) {
                         } else {
                             throw bde;
                         }
                     } finally {
                         // make sure the main stream is set to null
                         mainStream = null;
                     }
                 }
             }
         } catch (IOException ex) {
             if (raise) {
                 throw runtime.newIOErrorFromException(ex);
             }
         } catch (BadDescriptorException ex) {
             if (raise) {
                 throw runtime.newErrnoEBADFError();
             }
         } catch (Throwable t) {
             t.printStackTrace();
         }
     }
 }
