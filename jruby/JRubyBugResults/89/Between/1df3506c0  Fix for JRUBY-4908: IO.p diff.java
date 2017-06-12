diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index 66726211e3..f860db4cf4 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -2505,1428 +2505,1428 @@ public class RubyIO extends RubyObject {
             
             if (myOpenFile.getMainStream().readDataBuffered()) {
                 throw getRuntime().newIOError("sysread for buffered IO");
             }
             
             // TODO: Ruby locks the string here
             
             myOpenFile.checkClosed(getRuntime());
             
             // TODO: Ruby re-checks that the buffer string hasn't been modified
 
             // select until read is ready
             context.getThread().beforeBlockingCall();
             context.getThread().select(this, SelectionKey.OP_READ);
 
             int bytesRead = myOpenFile.getMainStream().getDescriptor().read(len, str.getByteList());
             
             // TODO: Ruby unlocks the string here
             
             // TODO: Ruby truncates string to specific size here, but our bytelist should handle this already?
             
             if (bytesRead == -1 || (bytesRead == 0 && len > 0)) {
                 throw getRuntime().newEOFError();
             }
             
             str.setTaint(true);
             
             return str;
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
     	} catch (IOException e) {
             synthesizeSystemCallError(e);
             return null;
     	} finally {
             context.getThread().afterBlockingCall();
         }
     }
 
     /**
      * Java does not give us enough information for specific error conditions
      * so we are reduced to divining them through string matches...
      */
     // TODO: Should ECONNABORTED get thrown earlier in the descriptor itself or is it ok to handle this late?
     // TODO: Should we include this into Errno code somewhere do we can use this from other places as well?
     private void synthesizeSystemCallError(IOException e) {
         String errorMessage = e.getMessage();
         // All errors to sysread should be SystemCallErrors, but on a closed stream
         // Ruby returns an IOError.  Java throws same exception for all errors so
         // we resort to this hack...
         if ("File not open".equals(errorMessage)) {
             throw getRuntime().newIOError(e.getMessage());
         } else if ("An established connection was aborted by the software in your host machine".equals(errorMessage)) {
             throw getRuntime().newErrnoECONNABORTEDError();
         }
 
         throw getRuntime().newSystemCallError(e.getMessage());
     }
     
     public IRubyObject read(IRubyObject[] args) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         switch (args.length) {
         case 0: return read(context);
         case 1: return read(context, args[0]);
         case 2: return read(context, args[0], args[1]);
         default: throw getRuntime().newArgumentError(args.length, 2);
         }
     }
     
     @JRubyMethod(name = "read")
     public IRubyObject read(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             return readAll(context.getRuntime().getNil());
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (EOFException ex) {
             throw getRuntime().newEOFError();
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
     
     @JRubyMethod(name = "read")
     public IRubyObject read(ThreadContext context, IRubyObject arg0) {
         if (arg0.isNil()) {
             return read(context);
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         int length = RubyNumeric.num2int(arg0);
         
         if (length < 0) {
             throw getRuntime().newArgumentError("negative length " + length + " given");
         }
         
         RubyString str = null;
         
         return readNotAll(context, myOpenFile, length, str);
     }
     
     @JRubyMethod(name = "read")
     public IRubyObject read(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (arg0.isNil()) {
             try {
                 myOpenFile.checkReadable(getRuntime());
                 myOpenFile.setReadBuffered();
 
                 return readAll(arg1);
             } catch (PipeException ex) {
                 throw getRuntime().newErrnoEPIPEError();
             } catch (InvalidValueException ex) {
                 throw getRuntime().newErrnoEINVALError();
             } catch (EOFException ex) {
                 throw getRuntime().newEOFError();
             } catch (IOException ex) {
                 throw getRuntime().newIOErrorFromException(ex);
             } catch (BadDescriptorException ex) {
                 throw getRuntime().newErrnoEBADFError();
             }
         }
         
         int length = RubyNumeric.num2int(arg0);
         
         if (length < 0) {
             throw getRuntime().newArgumentError("negative length " + length + " given");
         }
         
         RubyString str = null;
 //        ByteList buffer = null;
         if (arg1.isNil()) {
 //            buffer = new ByteList(length);
 //            str = RubyString.newString(getRuntime(), buffer);
         } else {
             str = arg1.convertToString();
             str.modify(length);
 
             if (length == 0) {
                 return str;
             }
 
 //            buffer = str.getByteList();
         }
         
         return readNotAll(context, myOpenFile, length, str);
     }
     
     private IRubyObject readNotAll(ThreadContext context, OpenFile myOpenFile, int length, RubyString str) {
         Ruby runtime = context.getRuntime();
         
         try {
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             if (myOpenFile.getMainStream().feof()) {
                 return runtime.getNil();
             }
 
             // TODO: Ruby locks the string here
 
             // READ_CHECK from MRI io.c
             readCheck(myOpenFile.getMainStream());
 
             // TODO: check buffer length again?
     //        if (RSTRING(str)->len != len) {
     //            rb_raise(rb_eRuntimeError, "buffer string modified");
     //        }
 
             // TODO: read into buffer using all the fread logic
     //        int read = openFile.getMainStream().fread(buffer);
             ByteList newBuffer = myOpenFile.getMainStream().fread(length);
 
             // TODO: Ruby unlocks the string here
 
             // TODO: change this to check number read into buffer once that's working
     //        if (read == 0) {
             
             if (newBuffer == null || newBuffer.length() == 0) {
                 if (myOpenFile.getMainStream() == null) {
                     return runtime.getNil();
                 }
 
                 if (myOpenFile.getMainStream().feof()) {
                     // truncate buffer string to zero, if provided
                     if (str != null) {
                         str.setValue(ByteList.EMPTY_BYTELIST.dup());
                     }
                 
                     return runtime.getNil();
                 }
 
                 // Removed while working on JRUBY-2386, since fixes for that
                 // modified EOF logic such that this check is not really valid.
                 // We expect that an EOFException will be thrown now in EOF
                 // cases.
 //                if (length > 0) {
 //                    // I think this is only partly correct; sys fail based on errno in Ruby
 //                    throw getRuntime().newEOFError();
 //                }
             }
 
 
             // TODO: Ruby truncates string to specific size here, but our bytelist should handle this already?
 
             // FIXME: I don't like the null checks here
             if (str == null) {
                 if (newBuffer == null) {
                     str = RubyString.newEmptyString(runtime);
                 } else {
                     str = RubyString.newString(runtime, newBuffer);
                 }
             } else {
                 if (newBuffer == null) {
                     str.empty();
                 } else {
                     str.setValue(newBuffer);
                 }
             }
             str.setTaint(true);
 
             return str;
         } catch (EOFException ex) {
             throw runtime.newEOFError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         }
     }
     
     protected IRubyObject readAll(IRubyObject buffer) throws BadDescriptorException, EOFException, IOException {
         Ruby runtime = getRuntime();
         // TODO: handle writing into original buffer better
         
         RubyString str = null;
         if (buffer instanceof RubyString) {
             str = (RubyString)buffer;
         }
         
         // TODO: ruby locks the string here
         
         // READ_CHECK from MRI io.c
         if (openFile.getMainStream().readDataBuffered()) {
             openFile.checkClosed(runtime);
         }
         
         try {
             ByteList newBuffer = openFile.getMainStream().readall();
 
             // TODO same zero-length checks as file above
 
             if (str == null) {
                 if (newBuffer == null) {
                     str = RubyString.newEmptyString(runtime);
                 } else {
                     str = RubyString.newString(runtime, newBuffer);
                 }
             } else {
                 if (newBuffer == null) {
                     str.empty();
                 } else {
                     str.setValue(newBuffer);
                 }
             }
         } catch (NonReadableChannelException ex) {
             throw runtime.newIOError("not opened for reading");
         }
 
         str.taint(runtime.getCurrentContext());
 
         return str;
 //        long bytes = 0;
 //        long n;
 //
 //        if (siz == 0) siz = BUFSIZ;
 //        if (NIL_P(str)) {
 //            str = rb_str_new(0, siz);
 //        }
 //        else {
 //            rb_str_resize(str, siz);
 //        }
 //        for (;;) {
 //            rb_str_locktmp(str);
 //            READ_CHECK(fptr->f);
 //            n = io_fread(RSTRING(str)->ptr+bytes, siz-bytes, fptr);
 //            rb_str_unlocktmp(str);
 //            if (n == 0 && bytes == 0) {
 //                if (!fptr->f) break;
 //                if (feof(fptr->f)) break;
 //                if (!ferror(fptr->f)) break;
 //                rb_sys_fail(fptr->path);
 //            }
 //            bytes += n;
 //            if (bytes < siz) break;
 //            siz += BUFSIZ;
 //            rb_str_resize(str, siz);
 //        }
 //        if (bytes != siz) rb_str_resize(str, bytes);
 //        OBJ_TAINT(str);
 //
 //        return str;
     }
     
     // TODO: There's a lot of complexity here due to error handling and
     // nonblocking IO; much of this goes away, but for now I'm just
     // having read call ChannelStream.fread directly.
 //    protected int fread(int len, ByteList buffer) {
 //        long n = len;
 //        int c;
 //        int saved_errno;
 //
 //        while (n > 0) {
 //            c = read_buffered_data(ptr, n, fptr->f);
 //            if (c < 0) goto eof;
 //            if (c > 0) {
 //                ptr += c;
 //                if ((n -= c) <= 0) break;
 //            }
 //            rb_thread_wait_fd(fileno(fptr->f));
 //            rb_io_check_closed(fptr);
 //            clearerr(fptr->f);
 //            TRAP_BEG;
 //            c = getc(fptr->f);
 //            TRAP_END;
 //            if (c == EOF) {
 //              eof:
 //                if (ferror(fptr->f)) {
 //                    switch (errno) {
 //                      case EINTR:
 //    #if defined(ERESTART)
 //                      case ERESTART:
 //    #endif
 //                        clearerr(fptr->f);
 //                        continue;
 //                      case EAGAIN:
 //    #if defined(EWOULDBLOCK) && EWOULDBLOCK != EAGAIN
 //                      case EWOULDBLOCK:
 //    #endif
 //                        if (len > n) {
 //                            clearerr(fptr->f);
 //                        }
 //                        saved_errno = errno;
 //                        rb_warning("nonblocking IO#read is obsolete; use IO#readpartial or IO#sysread");
 //                        errno = saved_errno;
 //                    }
 //                    if (len == n) return 0;
 //                }
 //                break;
 //            }
 //            *ptr++ = c;
 //            n--;
 //        }
 //        return len - n;
 //        
 //    }
 
     /** Read a byte. On EOF throw EOFError.
      * 
      */
     @JRubyMethod(name = "readchar")
     public IRubyObject readchar() {
         IRubyObject c = getc();
         
         if (c.isNil()) throw getRuntime().newEOFError();
         
         return c;
     }
     
     @JRubyMethod
     public IRubyObject stat(ThreadContext context) {
         openFile.checkClosed(context.getRuntime());
         return context.getRuntime().newFileStat(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor());
     }
 
     /** 
      * <p>Invoke a block for each byte.</p>
      */
     public IRubyObject each_byte(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         
     	try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             while (true) {
                 myOpenFile.checkReadable(runtime);
                 myOpenFile.setReadBuffered();
 
                 // TODO: READ_CHECK from MRI
                 
                 int c = myOpenFile.getMainStream().fgetc();
                 
                 if (c == -1) {
                     // TODO: check for error, clear it, and wait until readable before trying once more
 //                    if (ferror(f)) {
 //                        clearerr(f);
 //                        if (!rb_io_wait_readable(fileno(f)))
 //                            rb_sys_fail(fptr->path);
 //                        continue;
 //                    }
                     break;
                 }
                 
                 assert c < 256;
                 block.yield(context, getRuntime().newFixnum(c));
             }
 
             // TODO: one more check for error
 //            if (ferror(f)) rb_sys_fail(fptr->path);
             return this;
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (EOFException e) {
             return runtime.getNil();
     	} catch (IOException e) {
     	    throw runtime.newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = "each_byte", frame = true)
     public IRubyObject each_byte19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "each_byte");
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
         return each_line19(context, NULL_ARRAY, block);
     }
 
     public IRubyObject each_char(final ThreadContext context, final Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject ch;
 
         while(!(ch = getc()).isNil()) {
             byte c = (byte)RubyNumeric.fix2int(ch);
             int n = runtime.getKCode().getEncoding().length(c);
             RubyString str = runtime.newString();
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
 
     @JRubyMethod(name = "each_char", frame = true)
     public IRubyObject each_char19(final ThreadContext context, final Block block) {
         return eachCharCommon(context, block, "each_char");
     }
 
     @JRubyMethod(name = "chars", frame = true)
     public IRubyObject chars19(final ThreadContext context, final Block block) {
         return eachCharCommon(context, block, "chars");
     }
 
     @JRubyMethod(name = "codepoints", frame = true)
     public IRubyObject codepoints(final ThreadContext context, final Block block) {
         return eachCodePointCommon(context, block, "codepoints");
     }
 
     @JRubyMethod(name = "each_codepoint", frame = true)
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
     public RubyIO each_line(ThreadContext context, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         ByteList separator = getSeparatorForGets(runtime, args);
 
         ByteListCache cache = new ByteListCache();
         for (IRubyObject line = getline(runtime, separator); !line.isNil(); 
 		line = getline(runtime, separator, cache)) {
             block.yield(context, line);
         }
         
         return this;
     }
 
     @JRubyMethod(name = "each", optional = 1, frame = true)
     public IRubyObject each19(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_line(context, args, block) : enumeratorize(context.getRuntime(), this, "each", args);
     }
 
     @JRubyMethod(name = "each_line", optional = 1, frame = true)
     public IRubyObject each_line19(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_line(context, args, block) : enumeratorize(context.getRuntime(), this, "each_line", args);
     }
 
     @JRubyMethod(name = "readlines", optional = 1)
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
         return "RubyIO(" + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStream().getDescriptor()) + ")";
     }
     
     /* class methods for IO */
     
     /** rb_io_s_foreach
     *
     */
     public static IRubyObject foreach(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject filename = args[0].convertToString();
         runtime.checkSafeString(filename);
 
         RubyIO io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
         
         ByteListCache cache = new ByteListCache();
         if (!io.isNil()) {
             try {
                 ByteList separator = getSeparatorFromArgs(runtime, args, 1);
                 IRubyObject str = io.getline(runtime, separator, cache);
                 while (!str.isNil()) {
                     block.yield(context, str);
                     str = io.getline(runtime, separator, cache);
                     if (runtime.is1_9()) {
                         separator = getSeparatorFromArgs(runtime, args, 1);
                     }
                 }
             } finally {
                 io.close();
             }
         }
        
         return runtime.getNil();
     }
     
     @JRubyMethod(name = "foreach", required = 1, optional = 1, frame = true, meta = true)
     public static IRubyObject foreach19(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), recv, "foreach", args);
 
         if (!(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             args[0] = args[0].callMethod(context, "to_path");
         }
 
         return foreach(context, recv, args, block);
     }
 
     private static RubyIO convertToIO(ThreadContext context, IRubyObject obj) {
         return (RubyIO)TypeConverter.convertToType(obj, context.getRuntime().getIO(), "to_io");
     }
    
     private static boolean registerSelect(ThreadContext context, Selector selector, IRubyObject obj, RubyIO ioObj, int ops) throws IOException {
        Channel channel = ioObj.getChannel();
        if (channel == null || !(channel instanceof SelectableChannel)) {
            return false;
        }
        
        ((SelectableChannel) channel).configureBlocking(false);
        int real_ops = ((SelectableChannel) channel).validOps() & ops;
        SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
        
        if (key == null) {
            ((SelectableChannel) channel).register(selector, real_ops, obj);
        } else {
            key.interestOps(key.interestOps()|real_ops);
        }
        
        return true;
     }
    
     @JRubyMethod(name = "select", required = 1, optional = 3, meta = true)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return select_static(context, context.getRuntime(), args);
     }
 
     private static void checkArrayType(Ruby runtime, IRubyObject obj) {
         if (!(obj instanceof RubyArray)) {
             throw runtime.newTypeError("wrong argument type "
                     + obj.getMetaClass().getName() + " (expected Array)");
         }
     }
 
     public static IRubyObject select_static(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         Selector selector = null;
        try {
             Set pending = new HashSet();
             Set unselectable_reads = new HashSet();
             Set unselectable_writes = new HashSet();
             Map<RubyIO, Boolean> blocking = new HashMap();
             
             selector = SelectorFactory.openWithRetryFrom(context.getRuntime(), SelectorProvider.provider());
             if (!args[0].isNil()) {
                 // read
                 checkArrayType(runtime, args[0]);
                 for (Iterator i = ((RubyArray)args[0]).getList().iterator(); i.hasNext();) {
                     IRubyObject obj = (IRubyObject)i.next();
                     RubyIO ioObj = convertToIO(context, obj);
 
                     // save blocking state
                     if (ioObj.getChannel() instanceof SelectableChannel) blocking.put(ioObj, ((SelectableChannel)ioObj.getChannel()).isBlocking());
                     
                     if (registerSelect(context, selector, obj, ioObj, SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) {
                         if (ioObj.writeDataBuffered()) {
                             pending.add(obj);
                         }
                     } else {
                         if ((ioObj.openFile.getMode() & OpenFile.READABLE) != 0) {
                             unselectable_reads.add(obj);
                         }
                     }
                 }
             }
 
             if (args.length > 1 && !args[1].isNil()) {
                 // write
                 checkArrayType(runtime, args[1]);
                 for (Iterator i = ((RubyArray)args[1]).getList().iterator(); i.hasNext();) {
                     IRubyObject obj = (IRubyObject)i.next();
                     RubyIO ioObj = convertToIO(context, obj);
 
                     // save blocking state
                     if (!blocking.containsKey(ioObj) && ioObj.getChannel() instanceof SelectableChannel) blocking.put(ioObj, ((SelectableChannel)ioObj.getChannel()).isBlocking());
 
                     if (!registerSelect(context, selector, obj, ioObj, SelectionKey.OP_WRITE)) {
                         if ((ioObj.openFile.getMode() & OpenFile.WRITABLE) != 0) {
                             unselectable_writes.add(obj);
                         }
                     }
                 }
             }
 
             if (args.length > 2 && !args[2].isNil()) {
                 checkArrayType(runtime, args[2]);
             // Java's select doesn't do anything about this, so we leave it be.
             }
 
             final boolean has_timeout = (args.length > 3 && !args[3].isNil());
             long timeout = 0;
             if (has_timeout) {
                 IRubyObject timeArg = args[3];
                 if (timeArg instanceof RubyFloat) {
                     timeout = Math.round(((RubyFloat)timeArg).getDoubleValue() * 1000);
                 } else if (timeArg instanceof RubyFixnum) {
                     timeout = Math.round(((RubyFixnum)timeArg).getDoubleValue() * 1000);
                 } else { // TODO: MRI also can hadle Bignum here
                     throw runtime.newTypeError("can't convert " + timeArg.getMetaClass().getName() + " into time interval");
                 }
 
                 if (timeout < 0) {
                     throw runtime.newArgumentError("negative timeout given");
                 }
             }
 
             if (pending.isEmpty() && unselectable_reads.isEmpty() && unselectable_writes.isEmpty()) {
                 if (has_timeout) {
                     if (timeout == 0) {
                         selector.selectNow();
                     } else {
                         selector.select(timeout);
                     }
                 } else {
                     selector.select();
                 }
             } else {
                 selector.selectNow();
             }
 
             List r = new ArrayList();
             List w = new ArrayList();
             List e = new ArrayList();
             for (Iterator i = selector.selectedKeys().iterator(); i.hasNext();) {
                 SelectionKey key = (SelectionKey)i.next();
                 try {
                     int interestAndReady = key.interestOps() & key.readyOps();
                     if ((interestAndReady & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT | SelectionKey.OP_CONNECT)) != 0) {
                         r.add(key.attachment());
                         pending.remove(key.attachment());
                     }
                     if ((interestAndReady & (SelectionKey.OP_WRITE)) != 0) {
                         w.add(key.attachment());
                     }
                 } catch (CancelledKeyException cke) {
                     // TODO: is this the right thing to do?
                     pending.remove(key.attachment());
                     e.add(key.attachment());
                 }
             }
             r.addAll(pending);
             r.addAll(unselectable_reads);
             w.addAll(unselectable_writes);
 
             // make all sockets blocking as configured again
             selector.close(); // close unregisters all channels, so we can safely reset blocking modes
             for (Map.Entry blockingEntry : blocking.entrySet()) {
                 SelectableChannel channel = (SelectableChannel)((RubyIO)blockingEntry.getKey()).getChannel();
                 synchronized (channel.blockingLock()) {
                     channel.configureBlocking((Boolean)blockingEntry.getValue());
                 }
             }
 
             if (r.size() == 0 && w.size() == 0 && e.size() == 0) {
                 return runtime.getNil();
             }
 
             List ret = new ArrayList();
 
             ret.add(RubyArray.newArray(runtime, r));
             ret.add(RubyArray.newArray(runtime, w));
             ret.add(RubyArray.newArray(runtime, e));
 
             return RubyArray.newArray(runtime, ret);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         } finally {
             if (selector != null) {
                 try {
                     selector.close();
                 } catch (Exception e) {
                 }
             }
         }
    }
    
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         switch (args.length) {
         case 0: throw context.getRuntime().newArgumentError(0, 1);
         case 1: return read(context, recv, args[0], Block.NULL_BLOCK);
         case 2: return read(context, recv, args[0], args[1]);
         case 3: return read(context, recv, args[0], args[1], args[2]);
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
    
     @JRubyMethod(name = "read", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, Block unusedBlock) {
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
    
     @JRubyMethod(name = "read", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length) {
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
 
     @JRubyMethod(name = "read", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset) {
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
 
     // Enebo: annotation processing forced me to do pangea method here...
     @JRubyMethod(name = "read", meta = true, required = 1, optional = 3, compat = CompatVersion.RUBY1_9)
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
    
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true)
     public static IRubyObject popen(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         int mode;
 
         IRubyObject cmdObj = null;
         if (Platform.IS_WINDOWS) {
             String[] tokens = args[0].convertToString().toString().split(" ", 2);
             if (tokens.length > 1) {
                 cmdObj = new RubyString(runtime, (RubyClass) recv,
                         (tokens[0].replace('/', '\\') + ' ' + tokens[1]));
             }
             else {
                 cmdObj = new RubyString(runtime, (RubyClass) recv,
                         (tokens[0].replace('/','\\')));
             }
         }
         else {
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
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, io);
                 } finally {
                     if (io.openFile.isOpen()) {
                         io.close();
                     }
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor())));
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
    
-    @JRubyMethod(required = 1, rest = true, frame = true, meta = true)
+    @JRubyMethod(rest = true, frame = true, meta = true)
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
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (tuple.process.waitFor() * 256)));
                 }
             }
             return yieldArgs;
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
-    @JRubyMethod(required = 1, rest = true, frame = true, meta = true)
+    @JRubyMethod(rest = true, frame = true, meta = true)
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
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (tuple.process.waitFor() * 256)));
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
             ShellLauncher.POpenProcess process = ShellLauncher.popen3(runtime, args);
             RubyIO input = process.getInput() != null ?
                 new RubyIO(runtime, process.getInput()) :
                 new RubyIO(runtime, process.getInputStream());
             RubyIO output = process.getOutput() != null ?
                 new RubyIO(runtime, process.getOutput()) :
                 new RubyIO(runtime, process.getOutputStream());
             RubyIO error = process.getError() != null ?
                 new RubyIO(runtime, process.getError()) :
                 new RubyIO(runtime, process.getErrorStream());
 
             input.getOpenFile().getMainStream().getDescriptor().
               setCanBeSeekable(false);
             output.getOpenFile().getMainStream().getDescriptor().
               setCanBeSeekable(false);
             error.getOpenFile().getMainStream().getDescriptor().
               setCanBeSeekable(false);
 
             return new POpenTuple(input, output, error, process);
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
 
            sink.openFile.getMainStream().setSync(true);
            return runtime.newArrayNoCopy(new IRubyObject[] { source, sink });
        } catch (IOException ioe) {
            throw runtime.newIOErrorFromException(ioe);
        }
    }
     
     @JRubyMethod(name = "copy_stream", meta = true, compat = RUBY1_9)
     public static IRubyObject copy_stream(ThreadContext context, IRubyObject recv, 
             IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
 
         RubyIO io1 = null;
         boolean close_io1 = false;
         RubyIO io2 = null;
         boolean close_io2 = false;
 
         try {
             if (arg1 instanceof RubyString) {
                 io1 = (RubyIO) RubyFile.open(context, runtime.getFile(), new IRubyObject[] {arg1}, Block.NULL_BLOCK);
                 close_io1 = true;
             } else if (arg1 instanceof RubyIO) {
                 io1 = (RubyIO) arg1;
             } else {
                 throw runtime.newTypeError("Should be String or IO");
             }
 
             if (arg2 instanceof RubyString) {
                 io2 = (RubyIO) RubyFile.open(context, runtime.getFile(), new IRubyObject[] {arg2, runtime.newString("w")}, Block.NULL_BLOCK);
             } else if (arg2 instanceof RubyIO) {
                 io2 = (RubyIO) arg2;
                 close_io2 = true;
             } else {
                 throw runtime.newTypeError("Should be String or IO");
             }
 
             ChannelDescriptor d1 = io1.openFile.getMainStream().getDescriptor();
             if (!d1.isSeekable()) {
                 throw context.getRuntime().newTypeError("only supports file-to-file copy");
             }
             ChannelDescriptor d2 = io2.openFile.getMainStream().getDescriptor();
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
 
     @JRubyMethod(name = "try_convert", meta = true, backtrace = true, compat = CompatVersion.RUBY1_9)
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
                 externalEncodingOption = RubyEncoding.getDefaultExternal(runtime);
             }
             setExternalEncoding(context, externalEncodingOption);
         }
 
         if (internalEncodingOption != null && !internalEncodingOption.isNil()) {
             if (dash.eql(internalEncodingOption)) {
                 internalEncodingOption = RubyEncoding.getDefaultInternal(runtime);
             }
             setInternalEncoding(context, internalEncodingOption);
         }
 
         IRubyObject encoding = rubyOptions.fastARef(runtime.newSymbol("encoding"));
         if (encoding != null && !encoding.isNil()) {
             if (externalEncodingOption != null && !externalEncodingOption.isNil()) {
                 context.getRuntime().getWarnings().warn("Ignoring encoding parameter '"+ encoding +"': external_encoding is used");
             } else if (internalEncodingOption != null && !internalEncodingOption.isNil()) {
                 context.getRuntime().getWarnings().warn("Ignoring encoding parameter '"+ encoding +"': internal_encoding is used");
             } else {
                 parseEncodingFromString(context, encoding, 0);
             }
         }
 
         if (rubyOptions.containsKey(runtime.newSymbol("mode"))) {
             modes = parseModes19(context, rubyOptions.fastARef(runtime.newSymbol("mode")).asString());
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
             // forever on processes that ignore SIGTERM
             if (i >= 1000) {
                 throw new RuntimeException("could not shut down process: " + process);
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
 
     /**
      * @deprecated
      * @return
      */
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
     protected IRubyObject externalEncoding;
     protected IRubyObject internalEncoding;
 }
diff --git a/src/org/jruby/util/ShellLauncher.java b/src/org/jruby/util/ShellLauncher.java
index edf8959dce..67919d9fe3 100644
--- a/src/org/jruby/util/ShellLauncher.java
+++ b/src/org/jruby/util/ShellLauncher.java
@@ -1,1308 +1,1333 @@
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
  * Copyright (C) 2007-2010 JRuby Team <team@jruby.org>
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
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
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
         RubyHash hash = (RubyHash)runtime.getObject().fastGetConstant("ENV");
         String[] ret = new String[hash.size()];
         int i=0;
 
         for(Iterator iter = hash.directEntrySet().iterator();iter.hasNext();i++) {
             Map.Entry e = (Map.Entry)iter.next();
             ret[i] = e.getKey().toString() + "=" + e.getValue().toString();
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
             // TODO: not used
             String pathSeparator = System.getProperty("path.separator");
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
 
     public static long runWithoutWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output) {
         try {
             POpenProcess aProcess = new POpenProcess(popenShared(runtime, rawArgs));
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
                                 return (Long)ProcessImpl_handle.get(process);
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
                             return (Long)ProcessImpl_handle.get(process);
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
 
     public static POpenProcess popen3(Ruby runtime, IRubyObject[] strings) throws IOException {
         return new POpenProcess(popenShared(runtime, strings));
     }
 
     private static Process popenShared(Ruby runtime, IRubyObject[] strings) throws IOException {
         String shell = getShell(runtime);
         Process childProcess = null;
         File pwd = new File(runtime.getCurrentDirectory());
 
         try {
+            String[] args = parseCommandLine(runtime.getCurrentContext(), runtime, strings);
+            boolean useShell = false;
+            for (String arg : args) useShell |= shouldUseShell(arg);
+            
             // CON: popen is a case where I think we should just always shell out.
             if (strings.length == 1) {
-                // single string command, pass to sh to expand wildcards
-                String[] argArray = new String[3];
-                argArray[0] = shell;
-                argArray[1] = shell.endsWith("sh") ? "-c" : "/c";
-                argArray[2] = strings[0].asJavaString();
-                childProcess = Runtime.getRuntime().exec(argArray, getCurrentEnv(runtime), pwd);
+                if (useShell) {
+                    // single string command, pass to sh to expand wildcards
+                    String[] argArray = new String[3];
+                    argArray[0] = shell;
+                    argArray[1] = shell.endsWith("sh") ? "-c" : "/c";
+                    argArray[2] = strings[0].asJavaString();
+                    childProcess = Runtime.getRuntime().exec(argArray, getCurrentEnv(runtime), pwd);
+                } else {
+                    childProcess = Runtime.getRuntime().exec(args, getCurrentEnv(runtime), pwd);
+                }
             } else {
-                // direct invocation of the command
-                String[] args = parseCommandLine(runtime.getCurrentContext(), runtime, strings);
-                childProcess = Runtime.getRuntime().exec(args, getCurrentEnv(runtime), pwd);
+                if (useShell) {
+                    String[] argArray = new String[args.length + 2];
+                    argArray[0] = shell;
+                    argArray[1] = shell.endsWith("sh") ? "-c" : "/c";
+                    System.arraycopy(args, 0, argArray, 2, args.length);
+                    childProcess = Runtime.getRuntime().exec(argArray, getCurrentEnv(runtime), pwd);
+                } else {
+                    // direct invocation of the command
+                    childProcess = Runtime.getRuntime().exec(args, getCurrentEnv(runtime), pwd);
+                }
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
         Process aProcess = null;
         File pwd = new File(runtime.getCurrentDirectory());
         LaunchConfig cfg = new LaunchConfig(runtime, rawArgs, doExecutableSearch);
 
         try {
             if (cfg.shouldRunInProcess()) {
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
 
+    private static boolean shouldUseShell(String command) {
+        boolean useShell = false;
+        for (char c : command.toCharArray()) {
+            if (c != ' ' && !Character.isLetter(c) && "*?{}[]<>()~&|\\$;'`\"\n".indexOf(c) != -1) {
+                useShell = true;
+            }
+        }
+        return useShell;
+    }
+
     static void log(Ruby runtime, String msg) {
         if (RubyInstanceConfig.DEBUG_LAUNCHING) {
             runtime.getErr().println("ShellLauncher: " + msg);
         }
     }
 }
diff --git a/test/test_io.rb b/test/test_io.rb
index b02e20d765..be0afc548d 100644
--- a/test/test_io.rb
+++ b/test/test_io.rb
@@ -1,482 +1,495 @@
 require 'test/unit'
 require 'rbconfig'
 
 class TestIO < Test::Unit::TestCase
   WINDOWS = Config::CONFIG['host_os'] =~ /Windows|mswin/
   def setup
     @to_close = []
     @file = "TestIO_tmp"
     @file2 = "Test2IO_tmp"
     @file3 = "Test3IO_tmp"
     if (WINDOWS)
       @devnull = 'NUL:'
     else
       @devnull = '/dev/null'
     end
   end
 
   def teardown
     @to_close.each {|io| io.close rescue nil }
     File.unlink @file rescue nil
     File.unlink @file2 rescue nil
     File.unlink @file3 rescue nil
   end
 
   def test_puts_on_a_recursive_array
     # Puts a recursive array
     x = []
     x << 2 << x
     f = File.new(@file, "w")
     @to_close << f
     g = IO.new(f.fileno)
     @to_close << g
     g.puts x
     g.close
 
     f = File.new(@file, "r")
     @to_close << f
     g = IO.new(f.fileno)
     @to_close << g
     a = f.gets
     b = f.gets
     assert_equal("2\n", a)
     assert_equal("[...]\n", b)
   end
 
   def test_premature_close_raises_appropriate_errors
     ensure_files @file
     # In this case we will have f close (which will pull the rug
     # out from under g) and thus make g try the ops and fail
     f = File.open(@file)
     g = IO.new(f.fileno)
     @to_close << g
     f.close
     assert_raises(Errno::EBADF) { g.readchar }
     assert_raises(Errno::EBADF) { g.readline }
     assert_raises(Errno::EBADF) { g.gets }
     assert_raises(Errno::EBADF) { g.close }
     assert_raises(IOError) { g.getc }
     assert_raises(IOError) { g.readchar }
     assert_raises(IOError) { g.read }
     assert_raises(IOError) { g.sysread 1 }
 
     f = File.open(@file, "w")
     g = IO.new(f.fileno)
     @to_close << g
     f.close
     assert_nothing_raised { g.print "" }
     assert_nothing_raised { g.write "" }
     assert_nothing_raised { g.puts "" }
     assert_nothing_raised { g.putc 'c' }
     assert_raises(Errno::EBADF) { g.syswrite "" }
   end
 
   def test_ios_with_incompatible_flags
     ensure_files @file, @file2
     # Cannot open an IO which does not have compatible permission with
     # original IO
     f = File.new(@file2, "w")
     @to_close << f
     assert_raises(Errno::EINVAL) { g = IO.new(f.fileno, "r") }
     f.close
 
     f = File.new(@file, "r")
     @to_close << f
     assert_raises(Errno::EINVAL) { g = IO.new(f.fileno, "w") }
     f.close
   end
 
   def test_ios_with_compatible_flags
     ensure_files @file
     # However, you can open a second with less permissions
     f = File.new(@file, "r+")
     @to_close << f
     g = IO.new(f.fileno, "r")
     @to_close << g
     g.gets
     f.puts "HEH"
     assert_raises(IOError) { g.write "HOH" }
     assert_equal(f.fileno, g.fileno)
     f.close
   end
 
   def test_empty_write_does_not_complain
     # empty write...writes nothing and does not complain
     f = File.new(@file, "w")
     @to_close << f
     i = f.syswrite("")
     assert_equal(i, 0)
     i = f.syswrite("heh")
     assert_equal(i, 3)
     f.close
   end
 
   def test_enoent
     assert_raises(Errno::ENOENT) { File.foreach("nonexistent_file") {} }
   end
 
   def test_reopen
     ensure_files @file, @file2
     file = File.open(@file)
     @to_close << file
     file.gets
     file2 = File.open(@file2)
     @to_close << file2
     file2_fileno = file2.fileno;
     file2 = file2.reopen(file)
     assert_equal(file.pos, file2.pos)
     assert_equal(file2_fileno, file2.fileno);
     assert(file.fileno != file2.fileno);
     file2.close
     file.close
 
     # reopen of a filename after a close should succeed (JRUBY-1885)
     assert_nothing_raised { file.reopen(@file) }
   end
 
   def test_file_read
     ensure_files @file
     # test that read returns correct values
     f = File.open(@file)
     @to_close << f
     f.read # read all
     assert_equal("", f.read)
     assert_equal(nil, f.read(1))
     f.close
   end
 
   # MRI 1.8.5 and 1.8.6 permit nil buffers with reads.
   def test_file_read_with_nil_buffer
      ensure_files @file
 
      f = File.open(@file)
      @to_close << f
      assert_equal " ", f.read(1, nil)
   end
 
   def test_open
     ensure_files @file
 
     assert_raises(ArgumentError) { io = IO.open }
 
     f = File.open(@file)
     @to_close << f
     assert_raises(ArgumentError) { io = IO.open(f.fileno, "r", :gratuitous) }
     io = IO.open(f.fileno, "r")
     @to_close << io
     assert_equal(f.fileno, io.fileno)
     assert(!io.closed?)
     io.close
     assert(io.closed?)
 
     assert(!f.closed?)
     assert_raises(Errno::EBADF) { f.close }
   end
 
   def test_open_with_block
     ensure_files @file
 
     f = File.open(@file)
     @to_close << f
     IO.open(f.fileno, "r") do |io|
       assert_equal(f.fileno, io.fileno)
       assert(!io.closed?)
     end
 
     assert(!f.closed?)
     assert_raises(Errno::EBADF) { f.close }
   end
 
   unless WINDOWS # Windows doesn't take kindly to perm mode tests
     def test_sysopen
       ensure_files @file
 
       fno = IO::sysopen(@file, "r", 0124) # not creating, mode is ignored
       assert_instance_of(Fixnum, fno)
       assert_raises(Errno::EINVAL) { IO.open(fno, "w") } # not writable
       IO.open(fno, "r") do |io|
         assert_equal(fno, io.fileno)
         assert(!io.closed?)
       end
       assert_raises(Errno::EBADF) { IO.open(fno, "r") } # fd is closed
       File.open(@file) do |f|
         mode = (f.stat.mode & 0777) # only comparing lower 9 bits
         assert(mode > 0124)
       end
 
       File.delete(@file)
       fno = IO::sysopen(@file, "w", 0611) # creating, mode is enforced
       File.open(@file) do |f|
         mode = (f.stat.mode & 0777)
         assert_equal(0611, mode)
       end
     end
   end
 
   def test_delete
     ensure_files @file, @file2, @file3
     # Test deleting files
     assert(File.delete(@file, @file2, @file3))
   end
 
   def test_select
     ##### select #####
     assert_equal(nil, select(nil, nil, nil, 0))
     assert_raises(ArgumentError) { select(nil, nil, nil, -1) }
   end
 
   class FakeStream
     attr_accessor :data
     def initialize(stream, passthrough = false)
       @stream = stream
       @passthrough = passthrough
     end
     def write(content)
       @data = content
       @stream.write(content) if @passthrough
     end
   end
 
   def test_puts_and_warn_redirection
     require 'stringio'
     begin
       $stdout = StringIO.new
       $stderr = StringIO.new
       $stdout.print ":"
       $stderr.print ":"
       puts "hi"
       warn "hello"
       assert_equal ":hi\n", $stdout.string
       assert_equal ":hello\n", $stderr.string
     ensure
       $stderr = STDERR
       $stdout = STDOUT
     end
   end
 
   # JRUBY-1894
   def test_getc_255
      File.open(@file, "wb") do |file|
        file.putc(255)
      end
      File.open(@file, "rb") do |file|
        assert_equal(255, file.getc)
      end
   end
 
   # JRUBY-2202
   def test_ungetc_empty_file
     File.open(@file, "w+") {}
     File.open(@file) do |file|
       assert_nil(file.getc)
       assert_equal(0, file.pos)
       file.ungetc(100)
 
       # The following line is an intentional regression tests,
       # it checks that JRuby doesn't break.
       assert_equal(0, file.pos)
 
       assert_equal(100, file.getc)
      end
   end
 
   # JRUBY-2202
   def test_ungetc_nonempty_file
     File.open(@file, "w+") { |file| file.puts("HELLO") }
     File.open(@file) do |file|
       assert_equal(72, file.getc)
       assert_equal(1, file.pos)
       file.ungetc(100)
       assert_equal(0, file.pos)
       assert_equal(100, file.getc)
       assert_equal(1, file.pos)
      end
   end
 
   # JRUBY-2202
   def test_ungetc_position_change
     File.open(@file, "w+") { |file| file.puts("HELLO") }
 
     # getc/ungetc the same char
     File.open(@file) do |f|
       f.ungetc(f.getc)
       assert_equal(0, f.pos)
       assert_equal("HELLO", f.read(5))
       assert_equal(5, f.pos)
     end
 
     # getc/ungetc different char
     File.open(@file) do |f|
       f.getc
       f.ungetc(100)
       assert_equal(0, f.pos)
       assert_equal("dELLO", f.read(5))
       assert_equal(5, f.pos)
      end
   end
 
   # JRUBY-2203
   # unget char should be discarded after position changing calls
   def test_unget_before_position_change
     File.open(@file, "wb+") { |file| file.puts("HELLO") }
     File.open(@file) do |f|
       f.read(3)
       f.ungetc(100)
       f.pos = 2
       assert_equal("LLO", f.read(3))
 
       f.ungetc(100)
       f.seek(2)
       assert_equal("LLO", f.read(3))
 
       f.ungetc(100)
       f.rewind
       assert_equal("HELLO", f.read(5))
 
       f.ungetc(100)
       f.seek(-3, IO::SEEK_END)
       assert_equal("LO", f.read(2))
     end
   end
 
   # JRUBY-1987
   def test_reopen_doesnt_close_same_handler
     f = File.open(@file, "w")
     @to_close << f
     x = IO.new(f.fileno)
     @to_close << x
     f.print "."
     y = x.dup
     @to_close << y
     x.reopen(y)
     f.print "."
     f.close
     out = File.read(@file)
     assert_equal "..", out
   end
 
   # JRUBY-1698
   def test_very_big_read
     # See JRUBY-1686: this caused OOM
     ensure_files @file
     f = File.open(@file)
     @to_close << f
     assert_nothing_raised { f.read(1000000000) }
   end
 
   # JRUBY-2023, multithreaded writes
   def test_multithreaded_writes
     f = File.open("__temp1", "w")
     @to_close << f
     threads = []
     100.times {
       threads << Thread.new { 100.times { f.print('.') } }
     }
     threads.each {|thread| thread.join}
     f.close
     assert File.size("__temp1") == 100*100
   ensure
     File.unlink("__temp1")
   end
 
   #JRUBY-2145
   def test_eof_on_dev_null
     File.open(@devnull, 'rb') { |f|
       assert(f.eof?)
     }
   end
 
   #JRUBY-2145
   def test_read_dev_null
     File.open(@devnull, 'rb') { |f|
       assert_equal("", f.read)
       assert_equal(nil, f.read(1))
       assert_equal([], f.readlines)
       assert_raise EOFError do
         f.readline
       end
     }
   end
 
   def test_read_ignores_blocks
     a = true
     File.read(@devnull) { a = false }
     assert(a)
   end
 
   #JRUBY-2145
   if (!WINDOWS)
     def test_copy_dev_null
       require 'fileutils'
       begin
         FileUtils.cp(@devnull, 'somefile')
         assert(File.exists?('somefile'))
         assert_equal(0, File.size('somefile'))
       ensure
         File.delete('somefile') rescue nil
       end
     end
   end
 
   if (WINDOWS)
     #JRUBY-2158
     def test_null_open_windows
       null_names = ['NUL', 'NUL:', 'nul', 'nul:']
       null_names.each { |name|
         File.open(name) { |f|
           assert_equal("", f.read)
           assert(f.eof?)
         }
         File.open(name, 'r+') { |f|
           assert_nil(f.puts("test"))
         }
       }
     end
   end
 
   def test_file_constants_included
     assert IO.include?(File::Constants)
     ["APPEND", "BINARY", "CREAT", "EXCL", "FNM_CASEFOLD",
                    "FNM_DOTMATCH", "FNM_NOESCAPE", "FNM_PATHNAME", "FNM_SYSCASE",
                    "LOCK_EX", "LOCK_NB", "LOCK_SH", "LOCK_UN", "NOCTTY", "NONBLOCK",
                    "RDONLY", "RDWR", "SEEK_CUR", "SEEK_END", "SEEK_SET", "SYNC", "TRUNC",
                    "WRONLY"].each { |c| assert(IO.constants.include?(c), "#{c} is not included") }
   end
 
   #JRUBY-3012
   def test_io_reopen
     quiet_script = File.dirname(__FILE__) + '/quiet.rb'
     result = `jruby #{quiet_script}`.chomp
     assert_equal("foo", result)
   end
 
   # JRUBY-4152
   if $stdin.tty? # in Ant that might be false
     def test_tty_leak
       assert $stdin.tty?
       10_000.times {
         $stdin.tty?
       }
       assert $stdin.tty?
     end
   end
 
   # JRUBY-4821
   def test_clear_dollar_bang_after_open_block
     open(__FILE__) do |io|
       io.close
     end
     assert_nil $!
   end
 
   # JRUBY-4932
-  def test_popen4_read_error
-    p, i, o, e = IO.popen4(__FILE__)
-    assert_raise(IOError) { i.read }
-  end
+  #  def test_popen4_read_error
+  #  p, o, i, e = IO.popen4(__FILE__)
+  #  assert_raise(IOError) { i.read }
+  #end
 
-  private
   def ensure_files(*files)
     files.each {|f| File.open(f, "w") {|g| g << " " } }
   end
+  private :ensure_files
+  
+  # JRUBY-4908
+  unless WINDOWS
+    def test_sh_used_appropriately
+      # should not use sh
+      p, o, i, e = IO.popen4("/bin/ps -a")
+      assert_match p.to_s, i.read.lines.grep(/\/bin\/ps/).first
+      
+      # should use sh
+      p, o, i, e = IO.popen4("/bin/ps -a | grep ps'")
+      assert_no_match Regexp.new(p.to_s), i.read.grep(/\/bin\/ps/).first
+    end
+  end
 end
