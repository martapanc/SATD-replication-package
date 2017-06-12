diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index 8076f017a3..baa664ebd6 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -2420,1514 +2420,1514 @@ public class RubyIO extends RubyObject {
         if (!myOpenFile.isReadBuffered()) {
             return getRuntime().getNil();
         }
 
         return ungetcCommon(number, myOpenFile);
     }
 
     private IRubyObject ungetcCommon(IRubyObject number, OpenFile myOpenFile) {
         int ch = RubyNumeric.fix2int(number);
 
         try {
             myOpenFile.checkReadable(getRuntime());
             myOpenFile.setReadBuffered();
 
             if (myOpenFile.getMainStream().ungetc(ch) == -1 && ch != -1) {
                 throw getRuntime().newIOError("ungetc failed");
             }
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "read_nonblock", required = 1, optional = 1, backtrace = true)
     public IRubyObject read_nonblock(ThreadContext context, IRubyObject[] args) {
         IRubyObject value = getPartial(context, args, true);
 
         if (value.isNil()) throw context.getRuntime().newEOFError();
 
         if (value instanceof RubyString) {
             RubyString str = (RubyString) value;
             if (str.isEmpty()) {
                 Ruby ruby = context.getRuntime();
                 RaiseException eagain = ruby.newErrnoEAGAINError("");
 
                 // FIXME: *oif* 1.9 actually does this
                 if (ruby.is1_9()) {
                     eagain.getException().extend(new IRubyObject[] {ruby.getIO().getConstant("WaitReadable")});
                 }
 
                 throw eagain;
             }
         }
 
         return value;
     }
 
     @JRubyMethod(name = "readpartial", required = 1, optional = 1)
     public IRubyObject readpartial(ThreadContext context, IRubyObject[] args) {
         IRubyObject value = getPartial(context, args, false);
 
         if (value.isNil()) throw context.getRuntime().newEOFError();
 
         return value;
     }
 
     private IRubyObject getPartial(ThreadContext context, IRubyObject[] args, boolean isNonblocking) {
         Ruby runtime = context.getRuntime();
 
         // Length to read
         int length = RubyNumeric.fix2int(args[0]);
         if (length < 0) throw runtime.newArgumentError("negative length " + length + " given");
 
         // String/Buffer to read it into
         IRubyObject stringArg = args.length > 1 ? args[1] : RubyString.newEmptyString(runtime);
         RubyString string = stringArg.isNil() ? RubyString.newEmptyString(runtime) : stringArg.convertToString();
         
         openFile.checkClosed(runtime);
 
         if (!(openFile.getMainStream() instanceof ChannelStream)) { // cryptic for the uninitiated...
             throw runtime.newNotImplementedError("readpartial only works with Nio based handlers");
         }
         
         try {
             ChannelStream stream = (ChannelStream) openFile.getMainStream();
             ByteList buf = isNonblocking ? stream.readnonblock(length) : stream.readpartial(length);
             boolean empty = buf == null || buf.length() == 0;
 
             string.view(empty ? ByteList.EMPTY_BYTELIST : buf);
 
             if (stream.feof() && empty) return runtime.getNil();
 
             return string;
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (EOFException e) {
             throw runtime.newEOFError(e.getMessage());
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = "sysread", required = 1, optional = 1)
     public IRubyObject sysread(ThreadContext context, IRubyObject[] args) {
         int len = (int)RubyNumeric.num2long(args[0]);
         if (len < 0) throw getRuntime().newArgumentError("Negative size");
 
         try {
             RubyString str;
             ByteList buffer;
             if (args.length == 1 || args[1].isNil()) {
                 if (len == 0) {
                     return RubyString.newEmptyString(getRuntime());
                 }
                 
                 buffer = new ByteList(len);
                 str = RubyString.newString(getRuntime(), buffer);
             } else {
                 str = args[1].convertToString();
                 str.modify(len);
                 
                 if (len == 0) {
                     return str;
                 }
                 
                 buffer = str.getByteList();
                 buffer.length(0);
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
             
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
 
-        RubyString pathStr = path.convertToString();
+        RubyString pathStr = RubyFile.get_path(context, path);
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
    
     @JRubyMethod(required = 1, rest = true, frame = true, meta = true)
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
 
     @JRubyMethod(required = 1, rest = true, frame = true, meta = true)
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
 }
