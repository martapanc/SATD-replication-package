diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index 7d64db1326..878fc1d2b1 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1080,3750 +1080,3756 @@ public class RubyIO extends RubyObject {
         if (!internal.isNil()) {
             Encoding enc;
             Encoding enc2 = getEncodingCommon(context, external);
             
             if (internal instanceof RubyString) {
                 RubyString internalAsString = (RubyString) internal;
                 
                 // No encoding '-'
                 if (internalAsString.size() == 1 && internalAsString.asJavaString().equals("-")) {
                     enc = enc2;
                     enc2 = null;
                 } else {
                     EncodingOption encodingOption = EncodingOption.getEncodingOptionFromString(context.runtime, internalAsString.asJavaString());
                     enc = encodingOption.getExternalEncoding(); // Not really external... :) and bom handling?
                 }
                 
                 if (enc == enc2) {
                     context.runtime.getWarnings().warn("Ignoring internal encoding " + 
                             enc + ": it is identical to external encoding " + enc2);
                     enc2 = null;
                 }
             } else {
                 enc = getEncodingCommon(context, internal);
 
                 if (enc2 == enc) {
                     context.runtime.getWarnings().warn("Ignoring internal encoding " + 
                             enc2 + ": it is identical to external encoding " + enc);
                     enc = null;
                 }
             }
             transcodingActions = CharsetTranscoder.getCodingErrorActions(context, options);            
             setupReadWriteEncodings(context, enc, enc2);            
             
         } else {
             if (external.isNil()) {
                 setupReadWriteEncodings(context, null, null);
             } else {
                 if (external instanceof RubyString) {
                     RubyString externalAsString = (RubyString) external;
                     
                     // FIXME: I think this can handle a:b syntax and I didn't (also BOM)
                     setEncodingFromOptions(EncodingOption.getEncodingOptionFromString(context.runtime, externalAsString.asJavaString()));
                 } else {
                     Encoding enc = getEncodingCommon(context, external);
                     setupReadWriteEncodings(context, null, enc);
                 }
                 transcodingActions = CharsetTranscoder.getCodingErrorActions(context, options);
             }
         }
 
         validateEncodingBinmode();
         clearCodeConversion();
     }
     
     private void validateEncodingBinmode() {
         if (openFile.isReadable() && writeEncoding == null && 
                 !openFile.isBinmode() && readEncoding != null && !readEncoding.isAsciiCompatible()) {
             throw getRuntime().newArgumentError("ASCII incompatible encoding needs binmode");
         }
         
         // FIXME: Replace false with ecflags equiv when impl'd
         if (openFile.isBinmode() && false) { // DEFAULT_TEXTMODE & ECONV_DEC_MASK w/ ecflags
             openFile.setTextMode();
         }
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding) {
         if (internalEncoding instanceof RubyHash) {
             setEncoding(context, encodingString, context.runtime.getNil(), internalEncoding);            
         } else {
             setEncoding(context, encodingString, internalEncoding, null);
         }
 
         return context.runtime.getNil();
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding, IRubyObject options) {
         setEncoding(context, encodingString, internalEncoding, options);
 
         return context.runtime.getNil();
     }
 
 
     private static Encoding getEncodingCommon(ThreadContext context, IRubyObject encoding) {
         if (encoding instanceof RubyEncoding) return ((RubyEncoding) encoding).getEncoding();
 
         return context.runtime.getEncodingService().getEncodingFromObject(encoding);
     }
 
     @JRubyMethod(required = 1, rest = true, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
         RubyClass klass = (RubyClass)recv;
         
         RubyIO io = (RubyIO)klass.newInstance(context, args, block);
 
         if (block.isGiven()) {
             try {
                 return block.yield(context, io);
             } finally {
                 try {
                     io.getMetaClass().finvoke(context, io, "close", IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
                 } catch (RaiseException re) {
                     RubyException rubyEx = re.getException();
                     if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                         // MRI behavior: swallow StandardErorrs
                         runtime.getGlobalVariables().clear("$!");
                     } else {
                         throw re;
                     }
                 }
             }
         }
 
         return io;
     }
 
     @JRubyMethod(required = 1, optional = 2, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject sysopen(IRubyObject recv, IRubyObject[] args, Block block) {
         StringSupport.checkStringSafety(recv.getRuntime(), args[0]);
         IRubyObject pathString = args[0].convertToString();
         return sysopenCommon(recv, args, block, pathString);
     }
     
     @JRubyMethod(name = "sysopen", required = 1, optional = 2, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject sysopen19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         StringSupport.checkStringSafety(context.runtime, args[0]);
         IRubyObject pathString;
         if (!(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             pathString = args[0].callMethod(context, "to_path");
         } else {
             pathString = args[0].convertToString();
         }
         return sysopenCommon(recv, args, block, pathString);
     }
 
     private static IRubyObject sysopenCommon(IRubyObject recv, IRubyObject[] args, Block block, IRubyObject pathString) {
         Ruby runtime = recv.getRuntime();
         String path = pathString.toString();
 
         IOOptions modes;
         int perms = -1; // -1 == don't set permissions
 
         if (args.length > 1) {
             IRubyObject modeString = args[1].convertToString();
             modes = newIOOptions(runtime, modeString.toString());
         } else {
             modes = newIOOptions(runtime, "r");
         }
         if (args.length > 2) {
             RubyInteger permsInt =
                 args.length >= 3 ? args[2].convertToInteger() : null;
             perms = RubyNumeric.fix2int(permsInt);
         }
 
         int fileno = -1;
         try {
             ChannelDescriptor descriptor =
                 ChannelDescriptor.open(runtime.getCurrentDirectory(),
                                        path, modes.getModeFlags(), perms, runtime.getPosix(),
                                        runtime.getJRubyClassLoader());
             // always a new fileno, so ok to use internal only
             fileno = descriptor.getFileno();
         }
         catch (FileNotFoundException fnfe) {
             throw runtime.newErrnoENOENTError(path);
         } catch (DirectoryAsFileException dafe) {
             throw runtime.newErrnoEISDirError(path);
         } catch (FileExistsException fee) {
             throw runtime.newErrnoEEXISTError(path);
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
         }
         return runtime.newFixnum(fileno);
     }
 
     public boolean isAutoclose() {
         return openFile.isAutoclose();
     }
 
     public void setAutoclose(boolean autoclose) {
         openFile.setAutoclose(autoclose);
     }
 
     @JRubyMethod
     public IRubyObject autoclose(ThreadContext context) {
         return context.runtime.newBoolean(isAutoclose());
     }
 
     @JRubyMethod(name = "autoclose=")
     public IRubyObject autoclose_set(ThreadContext context, IRubyObject autoclose) {
         setAutoclose(autoclose.isTrue());
         return context.nil;
     }
 
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
         if (isClosed()) throw getRuntime().newIOError("closed stream");
 
         setAscii8bitBinmode();
 
         // missing logic:
         // write_io = GetWriteIO(io);
         // if (write_io != io)
         //     rb_io_ascii8bit_binmode(write_io);
 
         return this;
     }
 
     @JRubyMethod(name = "binmode?", compat = RUBY1_9)
     public IRubyObject op_binmode(ThreadContext context) {
         return RubyBoolean.newBoolean(context.runtime, openFile.isBinmode());
     }
 
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.runtime;
         
         try {
             RubyString string = obj.asString();
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
             
             Stream writeStream = myOpenFile.getWriteStream();
             
             if (myOpenFile.isWriteBuffered()) {
                 runtime.getWarnings().warn(ID.SYSWRITE_BUFFERED_IO, "syswrite for buffered IO");
             }
             
             if (!writeStream.getDescriptor().isWritable()) {
                 myOpenFile.checkClosed(runtime);
             }
             
             context.getThread().beforeBlockingCall();
             int read = writeStream.getDescriptor().write(string.getByteList());
             
             if (read == -1) {
                 // TODO? I think this ends up propagating from normal Java exceptions
                 // sys_fail(openFile.getPath())
             }
             
             return runtime.newFixnum(read);
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             if (e.getMessage().equals("Broken pipe")) {
                 throw runtime.newErrnoEPIPEError();
             }
             if (e.getMessage().equals("Connection reset by peer")) {
                 throw runtime.newErrnoEPIPEError();
             }
             throw runtime.newSystemCallError(e.getMessage());
         } finally {
             context.getThread().afterBlockingCall();
         }
     }
     
     @JRubyMethod(name = "write_nonblock", required = 1)
     public IRubyObject write_nonblock(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.runtime;
 
         OpenFile myOpenFile = getOpenFileChecked();
 
         try {
             myOpenFile.checkWritable(context.runtime);
             RubyString str = obj.asString();
             if (str.getByteList().length() == 0) {
                 return context.runtime.newFixnum(0);
             }
 
             if (myOpenFile.isWriteBuffered()) {
                 context.runtime.getWarnings().warn(ID.SYSWRITE_BUFFERED_IO, "write_nonblock for buffered IO");
             }
 
             ChannelStream stream = (ChannelStream)myOpenFile.getWriteStream();
 
             int written = stream.writenonblock(str.getByteList());
             if (written == 0) {
                 if (runtime.is1_9()) {
                     throw runtime.newErrnoEAGAINWritableError("");
                 } else {
                     throw runtime.newErrnoEWOULDBLOCKError();
                 }
             }
 
             return context.runtime.newFixnum(written);
         } catch (IOException ex) {
             throw context.runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw context.runtime.newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw context.runtime.newErrnoEINVALError();
         }
     }
     
     /** io_write
      * 
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.runtime;
         
         RubyString str = obj.asString();
 
         // TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
         
         if (str.getByteList().length() == 0) {
             return runtime.newFixnum(0);
         }
 
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
 
             context.getThread().beforeBlockingCall();
             int written = fwrite(str.getByteList());
 
             if (written == -1) {
                 // TODO: sys fail
             }
 
             // if not sync, we switch to write buffered mode
             if (!myOpenFile.isSync()) {
                 myOpenFile.setWriteBuffered();
             }
 
             return runtime.newFixnum(written);
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } finally {
             context.getThread().afterBlockingCall();
         }
     }
     
     private boolean waitWritable(Stream stream) {
         Channel ch = stream.getChannel();
         if (ch instanceof SelectableChannel) {
             getRuntime().getCurrentContext().getThread().select(ch, this, SelectionKey.OP_WRITE);
             return true;
         }
         return false;
     }
 
     private boolean waitReadable(Stream stream) {
         if (stream.readDataBuffered()) {
             return true;
         }
         Channel ch = stream.getChannel();
         if (ch instanceof SelectableChannel) {
             getRuntime().getCurrentContext().getThread().select(ch, this, SelectionKey.OP_READ);
             return true;
         }
         return false;
     }
 
     protected int fwrite(ByteList buffer) {
         int n, r, l, offset = 0;
         boolean eagain = false;
         Stream writeStream = openFile.getWriteStream();
         
         if (getRuntime().is1_9()) {
             buffer = doWriteConversion(getRuntime().getCurrentContext(), buffer);
         }
 
         int len = buffer.length();
         
         if ((n = len) <= 0) return n;
 
         try {
             if (openFile.isSync()) {
                 openFile.fflush(writeStream);
 
                 // TODO: why is this guarded?
     //            if (!rb_thread_fd_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //            }
                
                 while(offset<len) {
                     l = n;
 
                     // TODO: Something about pipe buffer length here
 
                     r = writeStream.getDescriptor().write(buffer,offset,l);
 
                     if(r == len) {
                         return len; //Everything written
                     }
 
                     if (0 <= r) {
                         offset += r;
                         n -= r;
                         eagain = true;
                     }
 
                     if(eagain && waitWritable(writeStream)) {
                         openFile.checkClosed(getRuntime());
                         if(offset >= buffer.length()) {
                             return -1;
                         }
                         eagain = false;
                     } else {
                         return -1;
                     }
                 }
 
 
                 // TODO: all this stuff...some pipe logic, some async thread stuff
     //          retry:
     //            l = n;
     //            if (PIPE_BUF < l &&
     //                !rb_thread_critical &&
     //                !rb_thread_alone() &&
     //                wsplit_p(fptr)) {
     //                l = PIPE_BUF;
     //            }
     //            TRAP_BEG;
     //            r = write(fileno(f), RSTRING(str)->ptr+offset, l);
     //            TRAP_END;
     //            if (r == n) return len;
     //            if (0 <= r) {
     //                offset += r;
     //                n -= r;
     //                errno = EAGAIN;
     //            }
     //            if (rb_io_wait_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //                if (offset < RSTRING(str)->len)
     //                    goto retry;
     //            }
     //            return -1L;
             }
 
             // TODO: handle errors in buffered write by retrying until finished or file is closed
             return writeStream.fwrite(buffer);
     //        while (errno = 0, offset += (r = fwrite(RSTRING(str)->ptr+offset, 1, n, f)), (n -= r) > 0) {
     //            if (ferror(f)
     //            ) {
     //                if (rb_io_wait_writable(fileno(f))) {
     //                    rb_io_check_closed(fptr);
     //                    clearerr(f);
     //                    if (offset < RSTRING(str)->len)
     //                        continue;
     //                }
     //                return -1L;
     //            }
     //        }
 
 //            return len - n;
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     /** rb_io_addstr
      * 
      */
     @JRubyMethod(name = "<<", required = 1)
     public IRubyObject op_append(ThreadContext context, IRubyObject anObject) {
         // Claims conversion is done via 'to_s' in docs.
         callMethod(context, "write", anObject);
         
         return this; 
     }
 
     @JRubyMethod(name = "fileno", alias = "to_i")
     public RubyFixnum fileno(ThreadContext context) {
         Ruby runtime = context.runtime;
         // map to external fileno
         try {
             return runtime.newFixnum(runtime.getFileno(getOpenFileChecked().getMainStreamSafe().getDescriptor()));
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         }
     }
     
     /** Returns the current line number.
      * 
      * @return the current line number.
      */
     @JRubyMethod(name = "lineno")
     public RubyFixnum lineno(ThreadContext context) {
         return context.runtime.newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Sets the current line number.
      * 
      * @param newLineNumber The new line number.
      */
     @JRubyMethod(name = "lineno=", required = 1)
     public RubyFixnum lineno_set(ThreadContext context, IRubyObject newLineNumber) {
         getOpenFileChecked().setLineNumber(RubyNumeric.fix2int(newLineNumber));
 
         return context.runtime.newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Returns the current sync mode.
      * 
      * @return the current sync mode.
      */
     @JRubyMethod(name = "sync")
     public RubyBoolean sync(ThreadContext context) {
         try {
             return context.runtime.newBoolean(getOpenFileChecked().getMainStreamSafe().isSync());
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
     
     /**
      * <p>Return the process id (pid) of the process this IO object
      * spawned.  If no process exists (popen was not called), then
      * nil is returned.  This is not how it appears to be defined
      * but ruby 1.8 works this way.</p>
      * 
      * @return the pid or nil
      */
     @JRubyMethod(name = "pid")
     public IRubyObject pid(ThreadContext context) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (myOpenFile.getProcess() == null) {
             return context.runtime.getNil();
         }
         
         // Of course this isn't particularly useful.
         long pid = myOpenFile.getPid();
 
         return context.runtime.newFixnum(pid);
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos(ThreadContext context) {
         try {
             return context.runtime.newFixnum(getOpenFileChecked().getMainStreamSafe().fgetpos());
         } catch (InvalidValueException ex) {
             throw context.runtime.newErrnoEINVALError();
         } catch (BadDescriptorException bde) {
             throw context.runtime.newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.runtime.newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.runtime.newIOErrorFromException(e);
         }
     }
     
     @JRubyMethod(name = "pos=", required = 1)
     public RubyFixnum pos_set(ThreadContext context, IRubyObject newPosition) {
         long offset = RubyNumeric.num2long(newPosition);
 
         if (offset < 0) {
             throw context.runtime.newSystemCallError("Negative seek offset");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.getMainStreamSafe().lseek(offset, Stream.SEEK_SET);
         
             myOpenFile.getMainStreamSafe().clearerr();
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.runtime.newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.runtime.newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.runtime.newIOErrorFromException(e);
         }
 
         return context.runtime.newFixnum(offset);
     }
     
     /** Print some objects to the stream.
      * 
      */
     @JRubyMethod(name = "print", rest = true, reads = FrameField.LASTLINE)
     public IRubyObject print(ThreadContext context, IRubyObject[] args) {
         return print(context, this, args);
     }
 
     /** Print some objects to the stream.
      *
      */
     public static IRubyObject print(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { context.getCurrentScope().getLastLine(context.runtime) };
         }
 
         Ruby runtime = context.runtime;
         IRubyObject fs = runtime.getGlobalVariables().get("$,");
         IRubyObject rs = runtime.getGlobalVariables().get("$\\");
 
         for (int i = 0; i < args.length; i++) {
             if (i > 0 && !fs.isNil()) {
                 maybeIO.callMethod(context, "write", fs);
             }
             if (args[i].isNil()) {
                 maybeIO.callMethod(context, "write", runtime.newString("nil"));
             } else {
                 maybeIO.callMethod(context, "write", args[i]);
             }
         }
         if (!rs.isNil()) {
             maybeIO.callMethod(context, "write", rs);
         }
 
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "printf", required = 1, rest = true)
     public IRubyObject printf(ThreadContext context, IRubyObject[] args) {
         callMethod(context, "write", RubyKernel.sprintf(context, this, args));
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "putc", required = 1)
     public IRubyObject putc(ThreadContext context, IRubyObject object) {
         return putc(context, this, object);
     }
 
     public static IRubyObject putc(ThreadContext context, IRubyObject maybeIO, IRubyObject object) {
         int c = RubyNumeric.num2chr(object);
         if (maybeIO instanceof RubyIO) {
             // FIXME we should probably still be dyncalling 'write' here
             RubyIO io = (RubyIO)maybeIO;
             try {
                 OpenFile myOpenFile = io.getOpenFileChecked();
                 myOpenFile.checkWritable(context.runtime);
                 Stream writeStream = myOpenFile.getWriteStream();
                 writeStream.fputc(c);
                 if (myOpenFile.isSync()) myOpenFile.fflush(writeStream);
             } catch (IOException ex) {
                 throw context.runtime.newIOErrorFromException(ex);
             } catch (BadDescriptorException e) {
                 throw context.runtime.newErrnoEBADFError();
             } catch (InvalidValueException ex) {
                 throw context.runtime.newErrnoEINVALError();
             }
         } else {
             maybeIO.callMethod(context, "write",
                     RubyString.newStringNoCopy(context.runtime, new byte[] {(byte)c}));
         }
 
         return object;
     }
 
     public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         return doSeek(context, offset, whence);
     }
 
     @JRubyMethod(name = "seek")
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0) {
         long offset = RubyNumeric.num2long(arg0);
         int whence = Stream.SEEK_SET;
         
         return doSeek(context, offset, whence);
     }
 
     @JRubyMethod(name = "seek")
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         long offset = RubyNumeric.num2long(arg0);
         int whence = RubyNumeric.fix2int(arg1.convertToInteger());
         
         return doSeek(context, offset, whence);
     }
     
     private RubyFixnum doSeek(ThreadContext context, long offset, int whence) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.seek(offset, whence);
         
             myOpenFile.getMainStreamSafe().clearerr();
         } catch (BadDescriptorException ex) {
             throw context.runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.runtime.newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.runtime.newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.runtime.newIOErrorFromException(e);
         }
 
         return RubyFixnum.zero(context.runtime);
     }
     
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "sysseek", required = 1, optional = 1)
     public RubyFixnum sysseek(ThreadContext context, IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         long pos;
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             
             if (myOpenFile.isReadable() && myOpenFile.isReadBuffered()) {
                 throw context.runtime.newIOError("sysseek for buffered IO");
             }
             if (myOpenFile.isWritable() && myOpenFile.isWriteBuffered()) {
                 context.runtime.getWarnings().warn(ID.SYSSEEK_BUFFERED_IO, "sysseek for buffered IO");
             }
             
             pos = myOpenFile.getMainStreamSafe().getDescriptor().lseek(offset, whence);
         
             myOpenFile.getMainStreamSafe().clearerr();
         } catch (BadDescriptorException ex) {
             throw context.runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.runtime.newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.runtime.newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.runtime.newIOErrorFromException(e);
         }
 
         return context.runtime.newFixnum(pos);
     }
 
     @JRubyMethod(name = "rewind")
     public RubyFixnum rewind(ThreadContext context) {
         OpenFile myOpenfile = getOpenFileChecked();
         
         try {
             myOpenfile.getMainStreamSafe().lseek(0L, Stream.SEEK_SET);
             myOpenfile.getMainStreamSafe().clearerr();
             
             // TODO: This is some goofy global file value from MRI..what to do?
 //            if (io == current_file) {
 //                gets_lineno -= fptr->lineno;
 //            }
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.runtime.newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.runtime.newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.runtime.newIOErrorFromException(e);
         }
 
         // Must be back on first line on rewind.
         myOpenfile.setLineNumber(0);
 
         return RubyFixnum.zero(context.runtime);
     }
     
     @JRubyMethod(name = "fsync")
     public RubyFixnum fsync(ThreadContext context) {
         Ruby runtime = context.runtime;
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
         
             Stream writeStream = myOpenFile.getWriteStream();
 
             writeStream.fflush();
             writeStream.sync();
 
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         }
 
         return RubyFixnum.zero(runtime);
     }
 
     /** Sets the current sync mode.
      * 
      * @param newSync The new sync mode.
      */
     @JRubyMethod(name = "sync=", required = 1)
     public IRubyObject sync_set(IRubyObject newSync) {
         try {
             getOpenFileChecked().setSync(newSync.isTrue());
             getOpenFileChecked().getMainStreamSafe().setSync(newSync.isTrue());
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
 
         return this;
     }
 
     @JRubyMethod(name = {"eof?", "eof"})
     public RubyBoolean eof_p(ThreadContext context) {
         Ruby runtime = context.runtime;
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             if (myOpenFile.getMainStreamSafe().feof()) {
                 return runtime.getTrue();
             }
             
             if (myOpenFile.getMainStreamSafe().readDataBuffered()) {
                 return runtime.getFalse();
             }
             
             readCheck(myOpenFile.getMainStreamSafe());
             waitReadable(myOpenFile.getMainStreamSafe());
             
             myOpenFile.getMainStreamSafe().clearerr();
             
             int c = myOpenFile.getMainStreamSafe().fgetc();
             
             if (c != -1) {
                 myOpenFile.getMainStreamSafe().ungetc(c);
                 return runtime.getFalse();
             }
             
             myOpenFile.checkClosed(runtime);
             
             myOpenFile.getMainStreamSafe().clearerr();
             
             return runtime.getTrue();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     @JRubyMethod(name = {"tty?", "isatty"})
     public RubyBoolean tty_p(ThreadContext context) {
         try {
             return context.runtime.newBoolean(
                     context.runtime.getPosix().isatty(
                             getOpenFileChecked().getMainStreamSafe().getDescriptor().getFileDescriptor()));
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
     
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         Ruby runtime = getRuntime();
         
         if (this == original) return this;
 
         RubyIO originalIO = (RubyIO) TypeConverter.convertToTypeWithCheck(original, runtime.getIO(), "to_io");
         
         OpenFile originalFile = originalIO.getOpenFileChecked();
         OpenFile newFile = openFile;
         
         try {
             if (originalFile.getPipeStream() != null) {
                 originalFile.getPipeStream().fflush();
                 originalFile.getMainStreamSafe().lseek(0, Stream.SEEK_CUR);
             } else if (originalFile.isWritable()) {
                 originalFile.getMainStreamSafe().fflush();
             } else {
                 originalFile.getMainStreamSafe().lseek(0, Stream.SEEK_CUR);
             }
 
             newFile.setMode(originalFile.getMode());
             newFile.setProcess(originalFile.getProcess());
             newFile.setLineNumber(originalFile.getLineNumber());
             newFile.setPath(originalFile.getPath());
             newFile.setFinalizer(originalFile.getFinalizer());
             
             IOOptions modes;
             if (newFile.isReadable()) {
                 if (newFile.isWritable()) {
                     if (newFile.getPipeStream() != null) {
                         modes = newIOOptions(runtime, ModeFlags.RDONLY);
                     } else {
                         modes = newIOOptions(runtime, ModeFlags.RDWR);
                     }
                 } else {
                     modes = newIOOptions(runtime, ModeFlags.RDONLY);
                 }
             } else {
                 if (newFile.isWritable()) {
                     modes = newIOOptions(runtime, ModeFlags.WRONLY);
                 } else {
                     modes = newIOOptions(runtime, originalFile.getMainStreamSafe().getModes());
                 }
             }
             
             ChannelDescriptor descriptor = originalFile.getMainStreamSafe().getDescriptor().dup();
 
             newFile.setMainStream(ChannelStream.fdopen(runtime, descriptor, modes.getModeFlags()));
 
             newFile.getMainStream().setSync(originalFile.getMainStreamSafe().isSync());
             if (originalFile.getMainStreamSafe().isBinmode()) newFile.getMainStream().setBinmode();
             
             // TODO: the rest of this...seeking to same position is unnecessary since we share a channel
             // but some of this may be needed?
             
 //    fseeko(fptr->f, ftello(orig->f), SEEK_SET);
 //    if (orig->f2) {
 //	if (fileno(orig->f) != fileno(orig->f2)) {
 //	    fd = ruby_dup(fileno(orig->f2));
 //	}
 //	fptr->f2 = rb_fdopen(fd, "w");
 //	fseeko(fptr->f2, ftello(orig->f2), SEEK_SET);
 //    }
 //    if (fptr->mode & FMODE_BINMODE) {
 //	rb_io_binmode(dest);
 //    }
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         } catch (PipeException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         } catch (InvalidValueException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         }
         
         return this;
     }
     
     @JRubyMethod(name = "closed?")
     public RubyBoolean closed_p(ThreadContext context) {
         return context.runtime.newBoolean(isClosed());
     }
 
     /**
      * Is this IO closed
      * 
      * @return true if closed
      */
     public boolean isClosed() {
         return openFile.getMainStream() == null && openFile.getPipeStream() == null;
     }
 
     /** 
      * <p>Closes all open resources for the IO.  It also removes
      * it from our magical all open file descriptor pool.</p>
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         Ruby runtime = getRuntime();
         
         openFile.checkClosed(runtime);
         return close2(runtime);
     }
         
     protected IRubyObject close2(Ruby runtime) {
         if (openFile == null) return runtime.getNil();
         
         interruptBlockingThreads();
 
         /* FIXME: Why did we go to this trouble and not use these descriptors?
         ChannelDescriptor main, pipe;
         if (openFile.getPipeStream() != null) {
             pipe = openFile.getPipeStream().getDescriptor();
         } else {
             if (openFile.getMainStream() == null) {
                 return runtime.getNil();
             }
             pipe = null;
         }
         
         main = openFile.getMainStream().getDescriptor(); */
         
         // cleanup, raising errors if any
         openFile.cleanup(runtime, true);
         
         // TODO: notify threads waiting on descriptors/IO? probably not...
 
-        // If this is not a popen3/popen4 stream and it has a process, attempt to shut down that process
+        // If this is not a popen3/popen4 stream and it has a process, wait for result
         if (!popenSpecial && openFile.getProcess() != null) {
-            obliterateProcess(openFile.getProcess());
-            IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(runtime, openFile.getProcess().exitValue(), openFile.getPid());
-            runtime.getCurrentContext().setLastExitStatus(processResult);
+            ThreadContext context = runtime.getCurrentContext();
+            try {
+                int result = openFile.getProcess().waitFor();
+                IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(runtime, result, openFile.getPid());
+                openFile.setProcess(null);
+                context.setLastExitStatus(processResult);
+            } catch (InterruptedException ie) {
+                context.pollThreadEvents();
+            }
         }
         
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "close_write")
     public IRubyObject close_write(ThreadContext context) {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isReadable()) {
                 throw context.runtime.newIOError("closing non-duplex IO for writing");
             }
             
             if (myOpenFile.getPipeStream() == null) {
                 close();
             } else{
                 myOpenFile.getPipeStream().fclose();
                 myOpenFile.setPipeStream(null);
                 myOpenFile.setMode(myOpenFile.getMode() & ~OpenFile.WRITABLE);
                 // TODO
                 // n is result of fclose; but perhaps having a SysError below is enough?
                 // if (n != 0) rb_sys_fail(fptr->path);
             }
         } catch (BadDescriptorException bde) {
             throw context.runtime.newErrnoEBADFError();
         } catch (IOException ioe) {
             // hmmmm
         }
         return this;
     }
 
     @JRubyMethod(name = "close_read")
     public IRubyObject close_read(ThreadContext context) {
         Ruby runtime = context.runtime;
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isWritable()) {
                 throw runtime.newIOError("closing non-duplex IO for reading");
             }
             
             if (myOpenFile.getPipeStream() == null) {
                 close();
             } else{
                 myOpenFile.getMainStreamSafe().fclose();
                 myOpenFile.setMode(myOpenFile.getMode() & ~OpenFile.READABLE);
                 myOpenFile.setMainStream(myOpenFile.getPipeStream());
                 myOpenFile.setPipeStream(null);
                 // TODO
                 // n is result of fclose; but perhaps having a SysError below is enough?
                 // if (n != 0) rb_sys_fail(fptr->path);
             }
         } catch (BadDescriptorException bde) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException ioe) {
             // I believe Ruby bails out with a "bug" if closing fails
             throw runtime.newIOErrorFromException(ioe);
         }
         return this;
     }
 
     /** Flushes the IO output stream.
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "flush")
     public RubyIO flush() {
         try { 
             getOpenFileChecked().getWriteStream().fflush();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOErrorFromException(e);
         }
 
         return this;
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_8)
     public IRubyObject gets(ThreadContext context) {
         Ruby runtime = context.runtime;
         IRubyObject result = getline(runtime, separator(runtime, runtime.getRecordSeparatorVar().get()));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_8)
     public IRubyObject gets(ThreadContext context, IRubyObject separatorArg) {
         Ruby runtime = context.runtime;
         IRubyObject result = getline(runtime, separator(runtime, separatorArg));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context) {
         Ruby runtime = context.runtime;
         IRubyObject result = getline(runtime, separator(runtime));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.runtime;
         ByteList separator;
         long limit = -1;
         if (arg instanceof RubyInteger) {
             limit = RubyInteger.fix2long(arg);
             separator = separator(runtime);
         } else {
             separator = separator(runtime, arg);
         }
 
         IRubyObject result = getline(runtime, separator, limit);
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context, IRubyObject separator, IRubyObject limit_arg) {
         Ruby runtime = context.runtime;
         long limit = limit_arg.isNil() ? -1 : RubyNumeric.fix2long(limit_arg);
         IRubyObject result = getline(runtime, separator(runtime, separator), limit);
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     public boolean getBlocking() {
         try {
             return ((ChannelStream) openFile.getMainStreamSafe()).isBlocking();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     @JRubyMethod(name = "fcntl")
     public IRubyObject fcntl(ThreadContext context, IRubyObject cmd) {
         // TODO: This version differs from ioctl by checking whether fcntl exists
         // and raising notimplemented if it doesn't; perhaps no difference for us?
         return ctl(context.runtime, cmd, null);
     }
 
     @JRubyMethod(name = "fcntl")
     public IRubyObject fcntl(ThreadContext context, IRubyObject cmd, IRubyObject arg) {
         // TODO: This version differs from ioctl by checking whether fcntl exists
         // and raising notimplemented if it doesn't; perhaps no difference for us?
         return ctl(context.runtime, cmd, arg);
     }
 
     @JRubyMethod(name = "ioctl", required = 1, optional = 1)
     public IRubyObject ioctl(ThreadContext context, IRubyObject[] args) {
         IRubyObject cmd = args[0];
         IRubyObject arg;
         
         if (args.length == 2) {
             arg = args[1];
         } else {
             arg = context.runtime.getNil();
         }
 
         return ctl(context.runtime, cmd, arg);
     }
 
     public IRubyObject ctl(Ruby runtime, IRubyObject cmd, IRubyObject arg) {
         long realCmd = cmd.convertToInteger().getLongValue();
         long nArg = 0;
         
         // FIXME: Arg may also be true, false, and nil and still be valid.  Strangely enough, 
         // protocol conversion is not happening in Ruby on this arg?
         if (arg == null || arg.isNil() || arg == runtime.getFalse()) {
             nArg = 0;
         } else if (arg instanceof RubyFixnum) {
             nArg = RubyFixnum.fix2long(arg);
         } else if (arg == runtime.getTrue()) {
             nArg = 1;
         } else {
             throw runtime.newNotImplementedError("JRuby does not support string for second fcntl/ioctl argument yet");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
 
         // Fixme: Only F_SETFL and F_GETFL is current supported
         // FIXME: Only NONBLOCK flag is supported
         // FIXME: F_SETFL and F_SETFD are treated as the same thing here.  For the case of dup(fd) we
         //   should actually have F_SETFL only affect one (it is unclear how well we do, but this TODO
         //   is here to at least document that we might need to do more work here.  Mostly SETFL is
         //   for mode changes which should persist across fork() boundaries.  Since JVM has no fork
         //   this is not a problem for us.
         try {
             if (realCmd == FcntlLibrary.FD_CLOEXEC) {
                 // Do nothing.  FD_CLOEXEC has no meaning in JVM since we cannot really exec.
                 // And why the hell does webrick pass this in as a first argument!!!!!
             } else if (realCmd == Fcntl.F_SETFL.intValue() || realCmd == Fcntl.F_SETFD.intValue()) {
                 if ((nArg & FcntlLibrary.FD_CLOEXEC) == FcntlLibrary.FD_CLOEXEC) {
                     // Do nothing.  FD_CLOEXEC has no meaning in JVM since we cannot really exec.
                 } else {
                     boolean block = (nArg & ModeFlags.NONBLOCK) != ModeFlags.NONBLOCK;
 
                     myOpenFile.getMainStreamSafe().setBlocking(block);
                 }
             } else if (realCmd == Fcntl.F_GETFL.intValue()) {
                 return myOpenFile.getMainStreamSafe().isBlocking() ? RubyFixnum.zero(runtime) : RubyFixnum.newFixnum(runtime, ModeFlags.NONBLOCK);
             } else {
                 throw runtime.newNotImplementedError("JRuby only supports F_SETFL and F_GETFL with NONBLOCK for fcntl/ioctl");
             }
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
         
         return runtime.newFixnum(0);
     }
     
     private static final ByteList NIL_BYTELIST = ByteList.create("nil");
     private static final ByteList RECURSIVE_BYTELIST = ByteList.create("[...]");
 
     @JRubyMethod(name = "puts", rest = true)
     public IRubyObject puts(ThreadContext context, IRubyObject[] args) {
         return puts(context, this, args);
     }
 
     public static IRubyObject puts(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         if (args.length == 0) {
             return writeSeparator(context, maybeIO);
         }
 
         return putsArray(context, maybeIO, args);
     }
 
     private static IRubyObject writeSeparator(ThreadContext context, IRubyObject maybeIO) {
         Ruby runtime = context.runtime;
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         write(context, maybeIO, separator.getByteList());
         return runtime.getNil();
     }
 
     private static IRubyObject putsArray(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         for (int i = 0; i < args.length; i++) {
             ByteList line;
 
             if (args[i].isNil()) {
                 line = getNilByteList(runtime);
             } else if (runtime.isInspecting(args[i])) {
                 line = RECURSIVE_BYTELIST;
             } else if (args[i] instanceof RubyArray) {
                 inspectPuts(context, maybeIO, (RubyArray) args[i]);
                 continue;
             } else {
                 line = args[i].asString().getByteList();
             }
 
             write(context, maybeIO, line);
 
             if (line.length() == 0 || !line.endsWith(separator.getByteList())) {
                 write(context, maybeIO, separator.getByteList());
             }
         }
         return runtime.getNil();
     }
 
     protected void write(ThreadContext context, ByteList byteList) {
         callMethod(context, "write", RubyString.newStringShared(context.runtime, byteList));
     }
 
     protected static void write(ThreadContext context, IRubyObject maybeIO, ByteList byteList) {
         maybeIO.callMethod(context, "write", RubyString.newStringShared(context.runtime, byteList));
     }
 
     private static IRubyObject inspectPuts(ThreadContext context, IRubyObject maybeIO, RubyArray array) {
         try {
             context.runtime.registerInspecting(array);
             return putsArray(context, maybeIO, array.toJavaArray());
         } finally {
             context.runtime.unregisterInspecting(array);
         }
     }
     
     @Override
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         
         if (!runtime.is1_9()) return super.inspect();
         OpenFile openFile = getOpenFile();
         if (openFile == null) return super.inspect();
         
         Stream stream = openFile.getMainStream();
         String className = getMetaClass().getRealClass().getName();
         String path = openFile.getPath();
         String status = "";
         
         if (path == null) {
             if (stream == null) {
                 path = "";
                 status = "(closed)";
             } else {
                 path = "fd " + runtime.getFileno(stream.getDescriptor());
             }
         } else if (!openFile.isOpen()) {
             status = " (closed)";
         }
         
         String inspectStr = "#<" + className + ":" + path + status + ">";
         
         return runtime.newString(inspectStr);
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "readline", writes = FrameField.LASTLINE)
     public IRubyObject readline(ThreadContext context) {
         IRubyObject line = gets(context);
 
         if (line.isNil()) {
             throw context.runtime.newEOFError();
         }
         
         return line;
     }
 
     @JRubyMethod(name = "readline", writes = FrameField.LASTLINE)
     public IRubyObject readline(ThreadContext context, IRubyObject separator) {
         IRubyObject line = gets(context, separator);
 
         if (line.isNil()) {
             throw context.runtime.newEOFError();
         }
 
         return line;
     }
 
     /** Read a byte. On EOF returns nil.
      * 
      */
     @JRubyMethod(name = {"getc", "getbyte"}, compat = RUBY1_8)
     public IRubyObject getc() {
         int c = getcCommon();
 
         if (c == -1) {
             // CRuby checks ferror(f) and retry getc for non-blocking IO
             // read. We checks readability first if possible so retry should
             // not be needed I believe.
             return getRuntime().getNil();
         }
 
         return getRuntime().newFixnum(c);
     }
     
     @JRubyMethod(name = "readchar", compat = RUBY1_9)
     public IRubyObject readchar19(ThreadContext context) {
         IRubyObject value = getc19(context);
         
         if (value.isNil()) {
             throw context.runtime.newEOFError();
         }
         
         return value;
     }
 
     @JRubyMethod(name = "getbyte", compat = RUBY1_9)
     public IRubyObject getbyte19(ThreadContext context) {
         return getc(); // Yes 1.8 getc is 1.9 getbyte
     }
     
     @JRubyMethod
     public IRubyObject readbyte(ThreadContext context) {
         int c = getcCommon();
         if (c == -1) {
             throw getRuntime().newEOFError();
         }
         
         return context.runtime.newFixnum(c);
     }
     
     private IRubyObject getcTranscoded(ThreadContext context, Stream stream, int firstByte) throws IOException, 
             BadDescriptorException, InvalidValueException {        
         makeReadConversion(context);
         
         Encoding read = getInputEncoding(context.runtime); // MRI has readencoding
         int cr = 0;
         ByteList bytes = null;
         
         int length = read.length((byte) firstByte);
         byte[] byteAry = new byte[length];
 
         byteAry[0] = (byte)firstByte;
         for (int i = 1; i < length; i++) {
             int c = (byte) stream.fgetc();
             
             if (c == -1) {
                 bytes = new ByteList(byteAry, 0, i - 1, read, false);
                 cr = StringSupport.CR_BROKEN;
             }
             
             byteAry[i] = (byte)c;
         }
 
         if (bytes == null) {
             cr = StringSupport.CR_VALID;
             bytes = new ByteList(byteAry, read, false);
         }            
     
         bytes = readTranscoder.transcode(context, bytes);
         
         return RubyString.newStringNoCopy(context.runtime, bytes, bytes.getEncoding(), cr);
     }
     
     // get a char directly without needing to transcode
     private IRubyObject getcDirect(ThreadContext context, Stream stream, int firstByte) throws InvalidValueException, 
             BadDescriptorException, IOException {
         Encoding encoding = getInputEncoding(context.runtime);
         ByteList bytes = null;
         boolean shared = false;
         int cr = 0;
 
         if (encoding.isAsciiCompatible() && Encoding.isAscii((byte) firstByte)) {
             if (encoding == ASCIIEncoding.INSTANCE) {
                 bytes = RubyInteger.SINGLE_CHAR_BYTELISTS[(int) firstByte];
                 shared = true;
             } else {
                 bytes = new ByteList(new byte[]{(byte) firstByte}, encoding, false);
                 shared = false;
                 cr = StringSupport.CR_7BIT;
             }
         } else {
             // potential MBC
             int len = encoding.length((byte) firstByte);
             byte[] byteAry = new byte[len];
 
             byteAry[0] = (byte) firstByte;
             for (int i = 1; i < len; i++) {
                 int c = (byte) stream.fgetc();
                 if (c == -1) {
                     bytes = new ByteList(byteAry, 0, i - 1, encoding, false);
                     cr = StringSupport.CR_BROKEN;
                 }
                 byteAry[i] = (byte) c;
             }
 
             if (bytes == null) {
                 cr = StringSupport.CR_VALID;
                 bytes = new ByteList(byteAry, encoding, false);
             }
         }
 
         if (shared) return RubyString.newStringShared(context.runtime, bytes, cr);
 
         return RubyString.newStringNoCopy(context.runtime, bytes, encoding, cr);
     }    
     
     @JRubyMethod(name = "getc", compat = RUBY1_9)
     public IRubyObject getc19(ThreadContext context) {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(context.runtime);
             myOpenFile.setReadBuffered();
 
             Stream stream = myOpenFile.getMainStreamSafe();
             
             readCheck(stream);
             waitReadable(stream);
             stream.clearerr();
             
             int c = stream.fgetc();
         
             if (c == -1) {
                 // CRuby checks ferror(f) and retry getc for non-blocking IO
                 // read. We checks readability first if possible so retry should
                 // not be needed I believe.
                 return context.runtime.getNil();
             }            
             
             if (needsReadConversion()) return getcTranscoded(context, stream, c);
 
             return getcDirect(context, stream, c);
         } catch (InvalidValueException ex) {
             throw context.runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         } catch (EOFException e) {
             throw context.runtime.newEOFError();
         } catch (IOException e) {
             throw context.runtime.newIOErrorFromException(e);
         }
     }
 
     public int getcCommon() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(getRuntime());
             myOpenFile.setReadBuffered();
 
             Stream stream = myOpenFile.getMainStreamSafe();
             
             readCheck(stream);
             waitReadable(stream);
             stream.clearerr();
             
             return myOpenFile.getMainStreamSafe().fgetc();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
         } catch (IOException e) {
             throw getRuntime().newIOErrorFromException(e);
         }
     }
     
     // MRI: NEED_NEWLINE_DECORATOR_ON_READ_CHECK
     private void readCheck(Stream stream) {
         if (!stream.readDataBuffered()) {
             openFile.checkClosed(getRuntime());
         }
     }
     
     /** 
      * <p>Pushes char represented by int back onto IOS.</p>
      * 
      * @param number to push back
      */
     @JRubyMethod(name = "ungetc", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject ungetc(IRubyObject number) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (!myOpenFile.isReadBuffered()) {
             throw getRuntime().newIOError("unread stream");
         }
 
         ungetcCommon((int)number.convertToInteger().getLongValue());
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "ungetc", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject ungetc19(IRubyObject number) {
         Ruby runtime = getRuntime();
         OpenFile myOpenFile = getOpenFileChecked();
 
         if (!myOpenFile.isReadBuffered()) {
             return runtime.getNil();
         }
 
         if (number instanceof RubyFixnum) {
             int c = (int)number.convertToInteger().getLongValue();
 
             ungetcCommon(c);
         } else if (number instanceof RubyString) {
             RubyString str = (RubyString) number;
             if (str.isEmpty()) return runtime.getNil();
 
             int c =  str.getEncoding().mbcToCode(str.getBytes(), 0, 1);
 
             ungetcCommon(c);
         } else {
             throw runtime.newTypeError(number, runtime.getFixnum());
         }
 
         return runtime.getNil();
     }
 
     public void ungetcCommon(int ch) {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             myOpenFile.checkReadable(getRuntime());
             myOpenFile.setReadBuffered();
 
             if (myOpenFile.getMainStreamSafe().ungetc(ch) == -1 && ch != -1) {
                 throw getRuntime().newIOError("ungetc failed");
             }
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
         } catch (IOException e) {
             throw getRuntime().newIOErrorFromException(e);
         }
     }
 
     @JRubyMethod(name = "read_nonblock", required = 1, optional = 1)
     public IRubyObject read_nonblock(ThreadContext context, IRubyObject[] args) {
         IRubyObject value = getPartial(context, args, true);
 
         if (value.isNil()) {
             throw context.runtime.newEOFError();
         }
 
         if (value instanceof RubyString) {
             RubyString str = (RubyString) value;
             if (str.isEmpty()) {
                 Ruby ruby = context.runtime;
 
                 if (ruby.is1_9()) {
                     throw ruby.newErrnoEAGAINReadableError("");
                 } else {
                     throw ruby.newErrnoEAGAINError("");
                 }
             }
         }
 
         return value;
     }
 
     @JRubyMethod(name = "readpartial", required = 1, optional = 1)
     public IRubyObject readpartial(ThreadContext context, IRubyObject[] args) {
         IRubyObject value = getPartial(context, args, false);
 
         if (value.isNil()) {
             throw context.runtime.newEOFError();
         }
 
         return value;
     }
 
     // implements io_getpartial in io.c
     private IRubyObject getPartial(ThreadContext context, IRubyObject[] args, boolean isNonblocking) {
         Ruby runtime = context.runtime;
 
         // Length to read
         int length = RubyNumeric.fix2int(args[0]);
         if (length < 0) throw runtime.newArgumentError("negative length " + length + " given");
 
         // String/Buffer to read it into
         IRubyObject stringArg = args.length > 1 ? args[1] : runtime.getNil();
         RubyString string = stringArg.isNil() ? RubyString.newEmptyString(runtime) : stringArg.convertToString();
         string.empty();
         string.setTaint(true);
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             myOpenFile.checkReadable(runtime);
             
             if (length == 0) {
                 return string;
             }
 
             if (!(myOpenFile.getMainStreamSafe() instanceof ChannelStream)) { // cryptic for the uninitiated...
                 throw runtime.newNotImplementedError("readpartial only works with Nio based handlers");
             }
             ChannelStream stream = (ChannelStream) myOpenFile.getMainStreamSafe();
 
             // We don't check RubyString modification since JRuby doesn't have
             // GIL. Other threads are free to change anytime.
 
             ByteList buf = null;
             if (isNonblocking) {
                 buf = stream.readnonblock(length);
             } else {
                 while ((buf == null || buf.length() == 0) && !stream.feof()) {
                     waitReadable(stream);
                     buf = stream.readpartial(length);
                 }
             }
             boolean empty = buf == null || buf.length() == 0;
             ByteList newBuf = empty ? ByteList.EMPTY_BYTELIST.dup() : buf;
             
             string.view(newBuf);
 
             if (stream.feof() && empty) return runtime.getNil();
 
             return string;
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (EOFException e) {
             throw runtime.newEOFError(e.getMessage());
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
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
             
             if (myOpenFile.getMainStreamSafe().readDataBuffered()) {
                 throw getRuntime().newIOError("sysread for buffered IO");
             }
             
             // TODO: Ruby locks the string here
             
             waitReadable(myOpenFile.getMainStreamSafe());
             myOpenFile.checkClosed(getRuntime());
 
             // We don't check RubyString modification since JRuby doesn't have
             // GIL. Other threads are free to change anytime.
 
             int bytesRead = myOpenFile.getMainStreamSafe().getDescriptor().read(len, str.getByteList());
             
             // TODO: Ruby unlocks the string here
 
             if (bytesRead == -1 || (bytesRead == 0 && len > 0)) {
                 throw getRuntime().newEOFError();
             }
             
             str.setTaint(true);
             
             return str;
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
     	} catch (IOException e) {
             synthesizeSystemCallError(e);
             return null;
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
         } else if ("Connection reset by peer".equals(e.getMessage())
                 || "An existing connection was forcibly closed by the remote host".equals(e.getMessage())) {
             throw getRuntime().newErrnoECONNRESETError();
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
         Ruby runtime = context.runtime;
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
             return readAll();
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
         
         RubyString str = RubyString.newEmptyString(getRuntime());
 
         return readNotAll(context, myOpenFile, length, str);
     }
     
     @JRubyMethod(name = "read")
     public IRubyObject read(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (arg0.isNil()) {
             try {
                 myOpenFile.checkReadable(getRuntime());
                 myOpenFile.setReadBuffered();
                 if (arg1.isNil()) {
                     return readAll();
                 } else {
                     return readAll(arg1.convertToString());
                 }
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
 
         if (arg1.isNil()) {
             return readNotAll(context, myOpenFile, length);
         } else {
             // this readNotAll empties the string for us
             return readNotAll(context, myOpenFile, length, arg1.convertToString());
         }
     }
     
     // implements latter part of io_read in io.c
     private IRubyObject readNotAll(ThreadContext context, OpenFile myOpenFile, int length, RubyString str) {
         Ruby runtime = context.runtime;
         str.empty();
 
         try {
             ByteList newBuffer = readNotAllCommon(context, myOpenFile, length);
 
             if (emptyBufferOrEOF(newBuffer, myOpenFile)) {
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
         Ruby runtime = context.runtime;
 
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
         Ruby runtime = context.runtime;
 
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
 
     protected RubyString readAll() throws BadDescriptorException, EOFException, IOException {
         Ruby runtime = getRuntime();
 
         // TODO: handle writing into original buffer better
         ByteList buf = readAllCommon(runtime);
         
         if (runtime.is1_9() && buf != null) {
             makeReadConversion(runtime.getCurrentContext());
             
             buf = readTranscoder.transcode(runtime.getCurrentContext(), buf);
         }
 
         RubyString str;
         if (buf == null) {
             str = RubyString.newEmptyString(runtime);
         } else {
             str = makeString(runtime, buf, false);
         }
         str.setTaint(true);
         return str;
     }
 
     // mri: read_all
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
         openFile.checkClosed(context.runtime);
         try {
             return context.runtime.newFileStat(getOpenFileChecked().getMainStreamSafe().getDescriptor().getFileDescriptor());
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
 
     /** 
      * <p>Invoke a block for each byte.</p>
      */
     public IRubyObject each_byteInternal(ThreadContext context, Block block) {
         Ruby runtime = context.runtime;
         
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
         if (!block.isGiven()) {
             return enumeratorize(context.runtime, recv, "foreach", args);
         }
 
         if (!(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             args[0] = args[0].callMethod(context, "to_path");
         }
         return foreachInternal(context, recv, args, block);
     }
 
     @JRubyMethod(name = "foreach", required = 1, optional = 2, meta = true, compat = RUBY1_9)
     public static IRubyObject foreach19(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         if (!block.isGiven()) {
             return enumeratorize(context.runtime, recv, "foreach", args);
         }
 
         if (!(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             args[0] = args[0].callMethod(context, "to_path");
         }
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
-                        io.close();
+                        io.callMethod(context, "close");
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
-                        io.close();
+                        io.callMethod(context, "close");
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
         long size = from.size();
 
         // handle large files on 32-bit JVMs (JRUBY-4913)
         try {
             from.transferTo(from.position(), size, to);
         } catch (IOException ioe) {
             // if the failure is "Cannot allocate memory", do the transfer in 100MB max chunks
             if (ioe.getMessage().equals("Cannot allocate memory")) {
                 long _100M = 100 * 1024 * 1024;
                 while (size > 0) {
                     if (size > _100M) {
                         from.transferTo(from.position(), _100M, to);
                         size -= _100M;
                     } else {
                         from.transferTo(from.position(), size, to);
                         break;
                     }
                 }
             } else {
                 throw ioe;
             }
         }
 
         return size;
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
diff --git a/src/org/jruby/util/ShellLauncher.java b/src/org/jruby/util/ShellLauncher.java
index 244899cd59..baee9becab 100644
--- a/src/org/jruby/util/ShellLauncher.java
+++ b/src/org/jruby/util/ShellLauncher.java
@@ -1,1560 +1,1579 @@
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
 
 import jnr.posix.util.ProcessMaker;
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
 import org.jruby.util.io.IOOptions;
 
 /**
  * This mess of a class is what happens when all Java gives you is
  * Runtime.getRuntime().exec(). Thanks dude, that really helped.
  * @author nicksieger
  */
 @SuppressWarnings("deprecation")
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
 
     public static String[] getCurrentEnv(Ruby runtime) {
         return getCurrentEnv(runtime, null);
     }
 
     private static String[] getCurrentEnv(Ruby runtime, Map mergeEnv) {
         ThreadContext context = runtime.getCurrentContext();
 
         // disable tracing for the dup call below
         boolean traceEnabled = context.isEventHooksEnabled();
         context.setEventHooksEnabled(false);
 
         try {
             // dup for JRUBY-6603 (avoid concurrent modification while we walk it)
             RubyHash hash = (RubyHash)runtime.getObject().getConstant("ENV").dup();
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
 
         } finally {
             context.setEventHooksEnabled(traceEnabled);
         }
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
             validFile = tryFile(runtime, fdir, fname);
             if (validFile != null && isExec &&
                 (validFile.isDirectory() || !runtime.getPosix().stat(validFile.getAbsolutePath()).isExecutable())) {
                 throw runtime.newErrnoEACCESError(validFile.getAbsolutePath());
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
                     aProcess = buildProcess(runtime, cfg.getExecArgs(), getCurrentEnv(runtime, mergeEnv), pwd);
                 } else {
                     log(runtime, "Launching directly (no shell)");
                     cfg.verifyExecutableForDirect();
                     aProcess = buildProcess(runtime, cfg.getExecArgs(), getCurrentEnv(runtime, mergeEnv), pwd);
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
                     aProcess = buildProcess(runtime, cfg.getExecArgs(), getCurrentEnv(runtime, (Map)env), pwd);
                 } else {
                     log(runtime, "Launching directly (no shell)");
                     cfg.verifyExecutableForDirect();
                     aProcess = buildProcess(runtime, cfg.getExecArgs(), getCurrentEnv(runtime, (Map)env), pwd);
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
 
     public static Process buildProcess(Ruby runtime, String[] args, String[] env, File pwd) throws IOException {
         return runtime.getPosix().newProcessMaker(args)
                 .environment(env)
                 .directory(pwd)
                 .start();
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
 
     public static POpenProcess popen(Ruby runtime, IRubyObject string, IOOptions modes) throws IOException {
         return new POpenProcess(popenShared(runtime, new IRubyObject[] {string}, null, true), runtime, modes);
     }
 
     public static POpenProcess popen(Ruby runtime, IRubyObject[] strings, Map env, IOOptions modes) throws IOException {
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
         return popenShared(runtime, strings, env, false);
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
                     childProcess = buildProcess(runtime, argArray, getCurrentEnv(runtime, env), pwd);
                 } else {
                     childProcess = buildProcess(runtime, args, getCurrentEnv(runtime, env), pwd);
                 }
             } else {
                 if (useShell) {
                     String[] argArray = new String[args.length + 2];
                     argArray[0] = shell;
                     argArray[1] = shell.endsWith("sh") ? "-c" : "/c";
                     System.arraycopy(args, 0, argArray, 2, args.length);
                     childProcess = buildProcess(runtime, argArray, getCurrentEnv(runtime, env), pwd);
                 } else {
                     // direct invocation of the command
                     childProcess = buildProcess(runtime, args, getCurrentEnv(runtime, env), pwd);
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
 
         public POpenProcess(Process child, Ruby runtime, IOOptions modes) {
             this.child = child;
 
             if (modes.getModeFlags().isWritable()) {
                 this.waitForChild = true;
                 prepareOutput(child);
             } else {
                 this.waitForChild = false;
                 // close process output
                 // See JRUBY-3405; hooking up to parent process stdin caused
                 // problems for IRB etc using stdin.
                 try {child.getOutputStream().close();} catch (IOException ioe) {}
             }
 
             if (modes.getModeFlags().isReadable()) {
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
+            shutdownStreams();
             return child.waitFor();
         }
 
         @Override
         public int exitValue() {
             return child.exitValue();
         }
 
         @Override
         public void destroy() {
             try {
                 // We try to safely close all streams and channels to the greatest
                 // extent possible.
                 try {if (input != null) input.close();} catch (Exception e) {}
                 try {if (inerr != null) inerr.close();} catch (Exception e) {}
                 try {if (output != null) output.close();} catch (Exception e) {}
                 try {if (inputChannel != null) inputChannel.close();} catch (Exception e) {}
                 try {if (inerrChannel != null) inerrChannel.close();} catch (Exception e) {}
                 try {if (outputChannel != null) outputChannel.close();} catch (Exception e) {}
 
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
             } catch (InterruptedException ie) {
                 Thread.currentThread().interrupt();
             }
         }
 
+        private void shutdownStreams() {
+            // We try to safely close all streams and channels to the greatest
+            // extent possible.
+            try {if (input != null) input.close();} catch (Exception e) {}
+            try {if (inerr != null) inerr.close();} catch (Exception e) {}
+            try {if (output != null) output.close();} catch (Exception e) {}
+            try {if (inputChannel != null) inputChannel.close();} catch (Exception e) {}
+            try {if (inerrChannel != null) inerrChannel.close();} catch (Exception e) {}
+            try {if (outputChannel != null) outputChannel.close();} catch (Exception e) {}
+
+            // processes seem to have some peculiar locking sequences, so we
+            // need to ensure nobody is trying to close/destroy while we are
+            synchronized (this) {
+                if (inputPumper != null) synchronized(inputPumper) {inputPumper.quit();}
+                if (inerrPumper != null) synchronized(inerrPumper) {inerrPumper.quit();}
+            }
+        }
+
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
 
     public static class LaunchConfig {
         public LaunchConfig(Ruby runtime, IRubyObject[] rawArgs, boolean doExecutableSearch) {
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
         public boolean shouldRunInProcess() {
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
         public boolean shouldRunInShell() {
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
 
         public void verifyExecutableForShell() {
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
 
         public void verifyExecutableForDirect() {
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
 
         public String[] getExecArgs() {
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
             } else {
                 if (cfg.shouldRunInShell()) {
                     log(runtime, "Launching with shell");
                     // execute command with sh -c
                     // this does shell expansion of wildcards
                     cfg.verifyExecutableForShell();
                 } else {
                     log(runtime, "Launching directly (no shell)");
                     cfg.verifyExecutableForDirect();
                 }
 
                 aProcess = buildProcess(runtime, cfg.getExecArgs(), getCurrentEnv(runtime), pwd);
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
             stop();
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
             stop();
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
 
         // finally, forcibly stop the threads. Yeah, I know.
         t1.stop();
         t2.stop();
         t3.stop();
         try { t1.join(); } catch (InterruptedException ie) {}
         try { t2.join(); } catch (InterruptedException ie) {}
         try { t3.join(); } catch (InterruptedException ie) {}
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
             if (hasLeadingArgvArray(rawArgs)) {
                 // can't make use of it, discard the argv[0] entry
                 args = new String[] { getPathEntry((RubyArray) rawArgs[0]) };
             } else {
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
             }
         } else {
             args = new String[rawArgs.length];
             int start = 0;
             if (hasLeadingArgvArray(rawArgs)) {
                 start = 1;
                 args[0] = getPathEntry((RubyArray) rawArgs[0]);
             }
             for (int i = start; i < rawArgs.length; i++) {
                 args[i] = rawArgs[i].toString();
             }
         }
         return args;
     }
 
     /** Takes an argument array suitable for Kernel#exec or similar,
      * and indicates whether it has a leading two-element array giving
      * the path and argv[0] entries separately.
      *
      * We can't use the argv[0] entry through ProcessBuilder, so
      * we discard it.
      */
     private static boolean hasLeadingArgvArray(IRubyObject[] rawArgs) {
         return (rawArgs.length >= 1
                 && (rawArgs[0] instanceof RubyArray)
                 && (((RubyArray) rawArgs[0]).getLength() == 2));
     }
 
     private static String getPathEntry(RubyArray initArray) {
         return initArray.entry(0).toString();
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
