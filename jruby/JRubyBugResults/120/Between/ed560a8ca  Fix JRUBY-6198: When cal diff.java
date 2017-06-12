diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index cc9130396f..4efe503d71 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -918,2000 +918,2001 @@ public class RubyIO extends RubyObject {
         int fileno = RubyNumeric.fix2int(fileNumber);
         ModeFlags modes = parseModes19(context, modeValue);
 
         modes = parseOptions(context, options, modes);
         return initializeCommon19(fileno, modes);
     }
 
     protected ModeFlags parseModes(IRubyObject arg) {
         try {
             if (arg instanceof RubyFixnum) return new ModeFlags(RubyFixnum.fix2long(arg));
 
             return getIOModes(getRuntime(), arg.convertToString().toString());
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
 
     protected ModeFlags parseModes19(ThreadContext context, IRubyObject arg) {
         ModeFlags modes = parseModes(arg);
 
         if (arg instanceof RubyString) {
             parseEncodingFromString(context, arg, 1);
         }
 
         return modes;
     }
 
     private void parseEncodingFromString(ThreadContext context, IRubyObject arg, int initialPosition) {
         RubyString modes19 = arg.convertToString();
         if (modes19.toString().contains(":")) {
             Ruby runtime = context.getRuntime();
 
             IRubyObject[] fullEncoding = modes19.split(context, RubyString.newString(runtime, ":")).toJavaArray();
 
             IRubyObject externalEncodingOption = fullEncoding[initialPosition];
             RubyString dash = runtime.newString("-");
             if (dash.eql(externalEncodingOption)) {
                 externalEncodingOption = runtime.getEncodingService().getDefaultExternal();
             }
 
             if (fullEncoding.length > (initialPosition + 1)) {
                 IRubyObject internalEncodingOption = fullEncoding[initialPosition + 1];
                 if (dash.eql(internalEncodingOption)) {
                     internalEncodingOption = runtime.getEncodingService().getDefaultInternal();
                 }
                 set_encoding(context, externalEncodingOption, internalEncodingOption);
             } else {
                 set_encoding(context, externalEncodingOption);
             }
         }
     }
 
     @JRubyMethod(required = 1, optional = 1, visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         int argCount = args.length;
         ModeFlags modes;
         
         int fileno = RubyNumeric.fix2int(args[0]);
         
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(getRuntime().getFilenoExtMap(fileno));
             
             if (descriptor == null) {
                 throw getRuntime().newErrnoEBADFError();
             }
             
             descriptor.checkOpen();
             
             if (argCount == 2) {
                 if (args[1] instanceof RubyFixnum) {
                     modes = new ModeFlags(RubyFixnum.fix2long(args[1]));
                 } else {
                     modes = getIOModes(getRuntime(), args[1].convertToString().toString());
                 }
             } else {
                 // use original modes
                 modes = descriptor.getOriginalModes();
             }
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(getRuntime(), false);
             }
 
             openFile.setMode(modes.getOpenFileFlags());
         
             openFile.setMainStream(fdopen(descriptor, modes));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ive) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         return this;
     }
     
     protected Stream fdopen(ChannelDescriptor existingDescriptor, ModeFlags modes) throws InvalidValueException {
         // See if we already have this descriptor open.
         // If so then we can mostly share the handler (keep open
         // file, but possibly change the mode).
         
         if (existingDescriptor == null) {
             // redundant, done above as well
             
             // this seems unlikely to happen unless it's a totally bogus fileno
             // ...so do we even need to bother trying to create one?
             
             // IN FACT, we should probably raise an error, yes?
             throw getRuntime().newErrnoEBADFError();
             
 //            if (mode == null) {
 //                mode = "r";
 //            }
 //            
 //            try {
 //                openFile.setMainStream(streamForFileno(getRuntime(), fileno));
 //            } catch (BadDescriptorException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            } catch (IOException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            }
 //            //modes = new IOModes(getRuntime(), mode);
 //            
 //            registerStream(openFile.getMainStream());
         } else {
             // We are creating a new IO object that shares the same
             // IOHandler (and fileno).
             return ChannelStream.fdopen(getRuntime(), existingDescriptor, modes);
         }
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject external_encoding(ThreadContext context) {
         return externalEncoding != null ?
             context.getRuntime().getEncodingService().getEncoding(externalEncoding) :
             context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject internal_encoding(ThreadContext context) {
         return internalEncoding != null ?
             context.getRuntime().getEncodingService().getEncoding(internalEncoding) :
             context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString) {
         setExternalEncoding(context, encodingString);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding, IRubyObject options) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     private void setExternalEncoding(ThreadContext context, IRubyObject encoding) {
         externalEncoding = getEncodingCommon(context, encoding);
     }
 
 
     private void setInternalEncoding(ThreadContext context, IRubyObject encoding) {
         Encoding internalEncodingOption = getEncodingCommon(context, encoding);
 
         if (internalEncodingOption == externalEncoding) {
             context.getRuntime().getWarnings().warn("Ignoring internal encoding " + encoding
                     + ": it is identical to external encoding " + external_encoding(context));
         } else {
             internalEncoding = internalEncodingOption;
         }
     }
 
     private Encoding getEncodingCommon(ThreadContext context, IRubyObject encoding) {
         if (encoding instanceof RubyEncoding) return ((RubyEncoding) encoding).getEncoding();
         
         return context.getRuntime().getEncodingService().getEncodingFromObject(encoding);
     }
 
     @JRubyMethod(required = 1, optional = 2, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
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
         StringSupport.checkStringSafety(context.getRuntime(), args[0]);
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
         runtime.checkSafeString(pathString);
         String path = pathString.toString();
 
         ModeFlags modes = null;
         int perms = -1; // -1 == don't set permissions
         try {
             if (args.length > 1) {
                 IRubyObject modeString = args[1].convertToString();
                 modes = getIOModes(runtime, modeString.toString());
             } else {
                 modes = getIOModes(runtime, "r");
             }
             if (args.length > 2) {
                 RubyInteger permsInt =
                     args.length >= 3 ? args[2].convertToInteger() : null;
                 perms = RubyNumeric.fix2int(permsInt);
             }
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
 
         int fileno = -1;
         try {
             ChannelDescriptor descriptor =
                 ChannelDescriptor.open(runtime.getCurrentDirectory(),
                                        path, modes, perms, runtime.getPosix(),
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
 
         Ruby runtime = getRuntime();
         if (getExternalEncoding(runtime) == USASCIIEncoding.INSTANCE) {
             externalEncoding = ASCIIEncoding.INSTANCE;
         }
         openFile.setBinmode();
         return this;
     }
 
     @JRubyMethod(name = "binmode?", compat = RUBY1_9)
     public IRubyObject op_binmode(ThreadContext context) {
         return RubyBoolean.newBoolean(context.getRuntime(), openFile.isBinmode());
     }
 
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         
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
             throw runtime.newSystemCallError(e.getMessage());
         } finally {
             context.getThread().afterBlockingCall();
         }
     }
     
     @JRubyMethod(name = "write_nonblock", required = 1)
     public IRubyObject write_nonblock(ThreadContext context, IRubyObject obj) {
         // MRI behavior: always check whether the file is writable
         // or not, even if we are to write 0 bytes.
         OpenFile myOpenFile = getOpenFileChecked();
 
         try {
             myOpenFile.checkWritable(context.getRuntime());
             RubyString str = obj.asString();
             if (str.getByteList().length() == 0) {
                 return context.getRuntime().newFixnum(0);
             }
 
             if (myOpenFile.isWriteBuffered()) {
                 context.getRuntime().getWarnings().warn(ID.SYSWRITE_BUFFERED_IO, "write_nonblock for buffered IO");
             }
             int written = myOpenFile.getWriteStream().getDescriptor().write(str.getByteList());
             return context.getRuntime().newFixnum(written);
         } catch (IOException ex) {
             throw context.getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         }
     }
     
     /** io_write
      * 
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         
         runtime.secure(4);
         
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
         Ruby runtime = context.getRuntime();
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
         return context.getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Sets the current line number.
      * 
      * @param newLineNumber The new line number.
      */
     @JRubyMethod(name = "lineno=", required = 1)
     public RubyFixnum lineno_set(ThreadContext context, IRubyObject newLineNumber) {
         getOpenFileChecked().setLineNumber(RubyNumeric.fix2int(newLineNumber));
 
         return context.getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Returns the current sync mode.
      * 
      * @return the current sync mode.
      */
     @JRubyMethod(name = "sync")
     public RubyBoolean sync(ThreadContext context) {
         try {
             return context.getRuntime().newBoolean(getOpenFileChecked().getMainStreamSafe().isSync());
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
             return context.getRuntime().getNil();
         }
         
         // Of course this isn't particularly useful.
         long pid = myOpenFile.getPid();
         
         return context.getRuntime().newFixnum(pid); 
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos(ThreadContext context) {
         try {
             return context.getRuntime().newFixnum(getOpenFileChecked().getMainStreamSafe().fgetpos());
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException bde) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOErrorFromException(e);
         }
     }
     
     @JRubyMethod(name = "pos=", required = 1)
     public RubyFixnum pos_set(ThreadContext context, IRubyObject newPosition) {
         long offset = RubyNumeric.num2long(newPosition);
 
         if (offset < 0) {
             throw context.getRuntime().newSystemCallError("Negative seek offset");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.getMainStreamSafe().lseek(offset, Stream.SEEK_SET);
         
             myOpenFile.getMainStreamSafe().clearerr();
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOErrorFromException(e);
         }
         
         return context.getRuntime().newFixnum(offset);
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
             args = new IRubyObject[] { context.getCurrentScope().getLastLine(context.getRuntime()) };
         }
 
         Ruby runtime = context.getRuntime();
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
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "putc", required = 1, backtrace = true)
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
                 myOpenFile.checkWritable(context.getRuntime());
                 Stream writeStream = myOpenFile.getWriteStream();
                 writeStream.fputc(c);
                 if (myOpenFile.isSync()) myOpenFile.fflush(writeStream);
             } catch (IOException ex) {
                 throw context.getRuntime().newIOErrorFromException(ex);
             } catch (BadDescriptorException e) {
                 throw context.getRuntime().newErrnoEBADFError();
             } catch (InvalidValueException ex) {
                 throw context.getRuntime().newErrnoEINVALError();
             }
         } else {
             maybeIO.callMethod(context, "write",
                     RubyString.newStringNoCopy(context.getRuntime(), new byte[] {(byte)c}));
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
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOErrorFromException(e);
         }
         
         return RubyFixnum.zero(context.getRuntime());
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
                 throw context.getRuntime().newIOError("sysseek for buffered IO");
             }
             if (myOpenFile.isWritable() && myOpenFile.isWriteBuffered()) {
                 context.getRuntime().getWarnings().warn(ID.SYSSEEK_BUFFERED_IO, "sysseek for buffered IO");
             }
             
             pos = myOpenFile.getMainStreamSafe().getDescriptor().lseek(offset, whence);
         
             myOpenFile.getMainStreamSafe().clearerr();
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOErrorFromException(e);
         }
         
         return context.getRuntime().newFixnum(pos);
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
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOErrorFromException(e);
         }
 
         // Must be back on first line on rewind.
         myOpenfile.setLineNumber(0);
         
         return RubyFixnum.zero(context.getRuntime());
     }
     
     @JRubyMethod(name = "fsync")
     public RubyFixnum fsync(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
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
         Ruby runtime = context.getRuntime();
         
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
             return context.getRuntime().newBoolean(
                     context.getRuntime().getPosix().isatty(
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
             
             ModeFlags modes;
             if (newFile.isReadable()) {
                 if (newFile.isWritable()) {
                     if (newFile.getPipeStream() != null) {
                         modes = new ModeFlags(ModeFlags.RDONLY);
                     } else {
                         modes = new ModeFlags(ModeFlags.RDWR);
                     }
                 } else {
                     modes = new ModeFlags(ModeFlags.RDONLY);
                 }
             } else {
                 if (newFile.isWritable()) {
                     modes = new ModeFlags(ModeFlags.WRONLY);
                 } else {
                     modes = originalFile.getMainStreamSafe().getModes();
                 }
             }
             
             ChannelDescriptor descriptor = originalFile.getMainStreamSafe().getDescriptor().dup();
 
             newFile.setMainStream(ChannelStream.fdopen(runtime, descriptor, modes));
 
             newFile.getMainStream().setSync(originalFile.getMainStreamSafe().isSync());
+            if (originalFile.getMainStreamSafe().isBinmode()) newFile.getMainStream().setBinmode();
             
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
         return context.getRuntime().newBoolean(isClosed());
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
         
         if (runtime.getSafeLevel() >= 4 && isTaint()) {
             throw runtime.newSecurityError("Insecure: can't close");
         }
         
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
         
         if (openFile.getProcess() != null) {
             obliterateProcess(openFile.getProcess());
             IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(runtime, openFile.getProcess().exitValue(), openFile.getPid());
             runtime.getCurrentContext().setLastExitStatus(processResult);
         }
         
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "close_write")
     public IRubyObject close_write(ThreadContext context) {
         try {
             if (context.getRuntime().getSafeLevel() >= 4 && isTaint()) {
                 throw context.getRuntime().newSecurityError("Insecure: can't close");
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isReadable()) {
                 throw context.getRuntime().newIOError("closing non-duplex IO for writing");
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
             throw context.getRuntime().newErrnoEBADFError();
         } catch (IOException ioe) {
             // hmmmm
         }
         return this;
     }
 
     @JRubyMethod(name = "close_read")
     public IRubyObject close_read(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         try {
             if (runtime.getSafeLevel() >= 4 && isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't close");
             }
             
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
         Ruby runtime = context.getRuntime();
         IRubyObject result = getline(runtime, separator(runtime, runtime.getRecordSeparatorVar().get()));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_8)
     public IRubyObject gets(ThreadContext context, IRubyObject separatorArg) {
         Ruby runtime = context.getRuntime();
         IRubyObject result = getline(runtime, separator(runtime, separatorArg));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         IRubyObject result = getline(runtime, separator(runtime));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
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
         Ruby runtime = context.getRuntime();
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
         return ctl(context.getRuntime(), cmd, null);
     }
 
     @JRubyMethod(name = "fcntl")
     public IRubyObject fcntl(ThreadContext context, IRubyObject cmd, IRubyObject arg) {
         // TODO: This version differs from ioctl by checking whether fcntl exists
         // and raising notimplemented if it doesn't; perhaps no difference for us?
         return ctl(context.getRuntime(), cmd, arg);
     }
 
     @JRubyMethod(name = "ioctl", required = 1, optional = 1)
     public IRubyObject ioctl(ThreadContext context, IRubyObject[] args) {
         IRubyObject cmd = args[0];
         IRubyObject arg;
         
         if (args.length == 2) {
             arg = args[1];
         } else {
             arg = context.getRuntime().getNil();
         }
         
         return ctl(context.getRuntime(), cmd, arg);
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
         Ruby runtime = context.getRuntime();
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         write(context, maybeIO, separator.getByteList());
         return runtime.getNil();
     }
 
     private static IRubyObject putsArray(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
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
         callMethod(context, "write", RubyString.newStringShared(context.getRuntime(), byteList));
     }
 
     protected static void write(ThreadContext context, IRubyObject maybeIO, ByteList byteList) {
         maybeIO.callMethod(context, "write", RubyString.newStringShared(context.getRuntime(), byteList));
     }
 
     private static IRubyObject inspectPuts(ThreadContext context, IRubyObject maybeIO, RubyArray array) {
         try {
             context.getRuntime().registerInspecting(array);
             return putsArray(context, maybeIO, array.toJavaArray());
         } finally {
             context.getRuntime().unregisterInspecting(array);
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
 
         if (line.isNil()) throw context.getRuntime().newEOFError();
         
         return line;
     }
 
     @JRubyMethod(name = "readline", writes = FrameField.LASTLINE)
     public IRubyObject readline(ThreadContext context, IRubyObject separator) {
         IRubyObject line = gets(context, separator);
 
         if (line.isNil()) throw context.getRuntime().newEOFError();
 
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
 
     private ByteList fromEncodedBytes(Ruby runtime, Encoding enc, int value) {
         int n;
         try {
             n = value < 0 ? 0 : enc.codeToMbcLength(value);
         } catch (EncodingException ee) {
             n = 0;
         }
 
         if (n <= 0) throw runtime.newRangeError(this.toString() + " out of char range");
 
         ByteList bytes = new ByteList(n);
         enc.codeToMbc(value, bytes.getUnsafeBytes(), 0);
         bytes.setRealSize(n);
         return bytes;
     }
     
     @JRubyMethod(name = "readchar", compat = RUBY1_9)
     public IRubyObject readchar19(ThreadContext context) {
         IRubyObject value = getc19(context);
         
         if (value.isNil()) throw context.getRuntime().newEOFError();
         
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
 
     @JRubyMethod(name = "getc", compat = RUBY1_9)
     public IRubyObject getc19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         int c = getcCommon();
 
         if (c == -1) {
             // CRuby checks ferror(f) and retry getc for non-blocking IO
             // read. We checks readability first if possible so retry should
             // not be needed I believe.
             return runtime.getNil();
         }
 
         Encoding external = getExternalEncoding(runtime);
         ByteList bytes = fromEncodedBytes(runtime, external, (int) c);
         Encoding internal = getInternalEncoding(runtime);
         
         if (internal != null) {
             bytes = RubyString.transcode(context, bytes, external, internal, runtime.getNil());
         }
 
         // TODO: This should be optimized like RubyInteger.chr is for ascii values
         return RubyString.newStringNoCopy(runtime, bytes, external, 0);
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
 
         return ungetcCommon(number, myOpenFile);
     }
 
     @JRubyMethod(name = "ungetc", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject ungetc19(IRubyObject number) {
         Ruby runtime = getRuntime();
         OpenFile myOpenFile = getOpenFileChecked();
 
         if (!myOpenFile.isReadBuffered()) {
             return runtime.getNil();
         }
 
         if (number instanceof RubyString) {
             RubyString str = (RubyString) number;
             if (str.isEmpty()) return runtime.getNil();
 
             int c =  str.getEncoding().mbcToCode(str.getBytes(), 0, 1);
             number = runtime.newFixnum(c);
         }
 
         return ungetcCommon(number, myOpenFile);
     }
 
     private IRubyObject ungetcCommon(IRubyObject number, OpenFile myOpenFile) {
         int ch = RubyNumeric.fix2int(number);
 
         try {
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
 
     // implements io_getpartial in io.c
     private IRubyObject getPartial(ThreadContext context, IRubyObject[] args, boolean isNonblocking) {
         Ruby runtime = context.getRuntime();
 
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
         Ruby runtime = context.getRuntime();
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
diff --git a/src/org/jruby/util/io/CRLFStreamWrapper.java b/src/org/jruby/util/io/CRLFStreamWrapper.java
index 23c3c57905..fbe11fb2d7 100644
--- a/src/org/jruby/util/io/CRLFStreamWrapper.java
+++ b/src/org/jruby/util/io/CRLFStreamWrapper.java
@@ -1,254 +1,258 @@
 package org.jruby.util.io;
 
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.nio.channels.Channel;
 import org.jruby.Ruby;
 import org.jruby.util.ByteList;
 
 /**
  * Wrapper around Stream that packs and unpacks LF <=> CRLF.
  * @author nicksieger
  */
 public class CRLFStreamWrapper implements Stream {
     private final Stream stream;
     private boolean binmode = false;
     private static final int CR = 13;
     private static final int LF = 10;
 
     public CRLFStreamWrapper(Stream stream) {
         this.stream = stream;
     }
 
     public ChannelDescriptor getDescriptor() {
         return stream.getDescriptor();
     }
 
     public void clearerr() {
         stream.clearerr();
     }
 
     public ModeFlags getModes() {
         return stream.getModes();
     }
 
     public boolean isSync() {
         return stream.isSync();
     }
 
     public void setSync(boolean sync) {
         stream.setSync(sync);
     }
 
     public void setBinmode() {
         binmode = true;
         stream.setBinmode();
     }
+    
+    public boolean isBinmode() {
+        return binmode;
+    }
 
     public boolean isAutoclose() {
         return stream.isAutoclose();
     }
 
     public void setAutoclose(boolean autoclose) {
         stream.setAutoclose(autoclose);
     }
 
     public ByteList fgets(ByteList separatorString) throws IOException, BadDescriptorException, EOFException {
         return convertCRLFToLF(stream.fgets(separatorString));
     }
 
     public ByteList readall() throws IOException, BadDescriptorException, EOFException {
         return convertCRLFToLF(stream.readall());
     }
 
     public int getline(ByteList dst, byte terminator) throws IOException, BadDescriptorException {
         if (binmode) {
             return stream.getline(dst, terminator);
         }
 
         ByteList intermediate = new ByteList();
         int result = stream.getline(intermediate, terminator);
         convertCRLFToLF(intermediate, dst);
         return result;
     }
 
     public int getline(ByteList dst, byte terminator, long limit) throws IOException, BadDescriptorException {
         if (binmode) {
             return stream.getline(dst, terminator, limit);
         }
 
         ByteList intermediate = new ByteList();
         int result = stream.getline(intermediate, terminator, limit);
         convertCRLFToLF(intermediate, dst);
         return result;
     }
 
     public ByteList fread(int number) throws IOException, BadDescriptorException, EOFException {
         if (number == 0) {
             if (stream.feof()) {
                 return null;
             } else {
                 return new ByteList(0);
             }
         }
         boolean eof = false;
         ByteList bl = new ByteList(number > ChannelStream.BUFSIZE ? ChannelStream.BUFSIZE : number);
         for (int i = 0; i < number; i++) {
             int c = fgetc();
             if (c == -1) {
                 eof = true;
                 break;
             }
             bl.append(c);
         }
         if (eof && bl.length() == 0) {
             return null;
         }
         return bl;
     }
 
     public int fwrite(ByteList string) throws IOException, BadDescriptorException {
         return stream.fwrite(convertLFToCRLF(string));
     }
 
     public int fgetc() throws IOException, BadDescriptorException, EOFException {
         int c = stream.fgetc();
         if (!binmode && c == CR) {
             c = stream.fgetc();
             if (c != LF) {
                 stream.ungetc(c);
                 return CR;
             }
         }
         return c;
     }
 
     public int ungetc(int c) {
         return stream.ungetc(c);
     }
 
     public void fputc(int c) throws IOException, BadDescriptorException {
         if (!binmode && c == LF) {
             stream.fputc(CR);
         }
         stream.fputc(c);
     }
 
     public ByteList read(int number) throws IOException, BadDescriptorException, EOFException {
         return convertCRLFToLF(stream.read(number));
     }
 
     public void fclose() throws IOException, BadDescriptorException {
         stream.fclose();
     }
 
     public int fflush() throws IOException, BadDescriptorException {
         return stream.fflush();
     }
 
     public void sync() throws IOException, BadDescriptorException {
         stream.sync();
     }
 
     public boolean feof() throws IOException, BadDescriptorException {
         return stream.feof();
     }
 
     public long fgetpos() throws IOException, PipeException, BadDescriptorException, InvalidValueException {
         return stream.fgetpos();
     }
 
     public void lseek(long offset, int type) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         stream.lseek(offset, type);
     }
 
     public void ftruncate(long newLength) throws IOException, PipeException, InvalidValueException, BadDescriptorException {
         stream.ftruncate(newLength);
     }
 
     public int ready() throws IOException {
         return stream.ready();
     }
 
     public void waitUntilReady() throws IOException, InterruptedException {
         stream.waitUntilReady();
     }
 
     public boolean readDataBuffered() {
         return stream.readDataBuffered();
     }
 
     public boolean writeDataBuffered() {
         return stream.writeDataBuffered();
     }
 
     public InputStream newInputStream() {
         return stream.newInputStream();
     }
 
     public OutputStream newOutputStream() {
         return stream.newOutputStream();
     }
 
     public boolean isBlocking() {
         return stream.isBlocking();
     }
 
     public void setBlocking(boolean blocking) throws IOException {
         stream.setBlocking(blocking);
     }
 
     public void freopen(Ruby runtime, String path, ModeFlags modes) throws DirectoryAsFileException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         stream.freopen(runtime, path, modes);
     }
 
     private ByteList convertCRLFToLF(ByteList input) {
         if (input == null) {
             return null;
         }
 
         if (binmode) {
             return input;
         }
 
         ByteList result = new ByteList();
         convertCRLFToLF(input, result);
         return result;
     }
 
     private void convertCRLFToLF(ByteList src, ByteList dst) {
         for (int i = 0; i < src.length(); i++) {
             int b = src.get(i);
             if (b == CR && i + 1 < src.length() && src.get(i + 1) == LF) {
                 continue;
             }
             dst.append(b);
         }
     }
 
     private ByteList convertLFToCRLF(ByteList input) {
         if (input == null) {
             return null;
         }
 
         if (binmode) {
             return input;
         }
 
         ByteList result = new ByteList();
         for (int i = 0; i < input.length(); i++) {
             int b = input.get(i);
             if (b == LF) {
                 result.append(CR);
             }
             result.append(b);
         }
         return result;
     }
 
     public Channel getChannel() {
         return stream.getChannel();
     }
 }
diff --git a/src/org/jruby/util/io/ChannelStream.java b/src/org/jruby/util/io/ChannelStream.java
index 2fee459aec..19f2cfe357 100644
--- a/src/org/jruby/util/io/ChannelStream.java
+++ b/src/org/jruby/util/io/ChannelStream.java
@@ -1,1153 +1,1157 @@
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
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2007 Damian Steer <pldms@mac.com>
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
 package org.jruby.util.io;
 
 
 import java.io.EOFException;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.RandomAccessFile;
 import java.nio.ByteBuffer;
 import java.nio.channels.Channel;
 import java.nio.channels.FileChannel;
 import java.nio.channels.IllegalBlockingModeException;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.SelectableChannel;
 
 import org.jruby.Finalizable;
 import org.jruby.Ruby;
 import org.jruby.platform.Platform;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 
 import java.nio.channels.spi.SelectorProvider;
 
 /**
  * This file implements a seekable IO file.
  */
 public class ChannelStream implements Stream, Finalizable {
 
     private static final Logger LOG = LoggerFactory.getLogger("ChannelStream");
 
     private final static boolean DEBUG = false;
 
     /**
      * The size of the read/write buffer allocated for this stream.
      *
      * This size has been scaled back from its original 16k because although
      * the larger buffer size results in raw File.open times being rather slow
      * (due to the cost of instantiating a relatively large buffer). We should
      * try to find a happy medium, or potentially pool buffers, or perhaps even
      * choose a value based on platform(??), but for now I am reducing it along
      * with changes for the "large read" patch from JRUBY-2657.
      */
     public final static int BUFSIZE = 4 * 1024;
 
     /**
      * The size at which a single read should turn into a chunkier bulk read.
      * Currently, this size is about 4x a normal buffer size.
      *
      * This size was not really arrived at experimentally, and could potentially
      * be increased. However, it seems like a "good size" and we should
      * probably only adjust it if it turns out we would perform better with a
      * larger buffer for large bulk reads.
      */
     private final static int BULK_READ_SIZE = 16 * 1024;
     private final static ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
 
     private volatile Ruby runtime;
     protected ModeFlags modes;
     protected boolean sync = false;
 
     protected volatile ByteBuffer buffer; // r/w buffer
     protected boolean reading; // are we reading or writing?
     private ChannelDescriptor descriptor;
     private boolean blocking = true;
     protected int ungotc = -1;
     private volatile boolean closedExplicitly = false;
 
     private volatile boolean eof = false;
     private volatile boolean autoclose = true;
 
     private ChannelStream(Ruby runtime, ChannelDescriptor descriptor, boolean autoclose) {
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = descriptor.getOriginalModes();
         buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
         this.autoclose = autoclose;
         runtime.addInternalFinalizer(this);
     }
 
     private ChannelStream(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes, boolean autoclose) {
         this(runtime, descriptor, autoclose);
         this.modes = modes;
     }
 
     public Ruby getRuntime() {
         return runtime;
     }
 
     public void checkReadable() throws IOException {
         if (!modes.isReadable()) throw new IOException("not opened for reading");
     }
 
     public void checkWritable() throws IOException {
         if (!modes.isWritable()) throw new IOException("not opened for writing");
     }
 
     public void checkPermissionsSubsetOf(ModeFlags subsetModes) {
         subsetModes.isSubsetOf(modes);
     }
 
     public ModeFlags getModes() {
     	return modes;
     }
 
     public boolean isSync() {
         return sync;
     }
 
     public void setSync(boolean sync) {
         this.sync = sync;
     }
 
     public void setBinmode() {
         // No-op here, no binmode handling needed.
     }
+    
+    public boolean isBinmode() {
+        return false;
+    }
 
     public boolean isAutoclose() {
         return autoclose;
     }
 
     public void setAutoclose(boolean autoclose) {
         this.autoclose = autoclose;
     }
 
     /**
      * Implement IO#wait as per io/wait in MRI.
      * waits until input available or timed out and returns self, or nil when EOF reached.
      *
      * The default implementation loops while ready returns 0.
      */
     public void waitUntilReady() throws IOException, InterruptedException {
         while (ready() == 0) {
             Thread.sleep(10);
         }
     }
 
     public boolean readDataBuffered() {
         return reading && (ungotc != -1 || buffer.hasRemaining());
     }
 
     public boolean writeDataBuffered() {
         return !reading && buffer.position() > 0;
     }
     private final int refillBuffer() throws IOException {
         buffer.clear();
         int n = ((ReadableByteChannel) descriptor.getChannel()).read(buffer);
         buffer.flip();
         return n;
     }
     public synchronized ByteList fgets(ByteList separatorString) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
 
         if (separatorString == null) {
             return readall();
         }
 
         final ByteList separator = (separatorString == PARAGRAPH_DELIMETER) ?
             PARAGRAPH_SEPARATOR : separatorString;
 
         descriptor.checkOpen();
 
         if (feof()) {
             return null;
         }
 
         int c = read();
 
         if (c == -1) {
             return null;
         }
 
         // unread back
         buffer.position(buffer.position() - 1);
 
         ByteList buf = new ByteList(40);
 
         byte first = separator.getUnsafeBytes()[separator.getBegin()];
 
         LineLoop : while (true) {
             ReadLoop: while (true) {
                 byte[] bytes = buffer.array();
                 int offset = buffer.position();
                 int max = buffer.limit();
 
                 // iterate over remainder of buffer until we find a match
                 for (int i = offset; i < max; i++) {
                     c = bytes[i];
                     if (c == first) {
                         // terminate and advance buffer when we find our char
                         buf.append(bytes, offset, i - offset);
                         if (i >= max) {
                             buffer.clear();
                         } else {
                             buffer.position(i + 1);
                         }
                         break ReadLoop;
                     }
                 }
 
                 // no match, append remainder of buffer and continue with next block
                 buf.append(bytes, offset, buffer.remaining());
                 int read = refillBuffer();
                 if (read == -1) break LineLoop;
             }
 
             // found a match above, check if remaining separator characters match, appending as we go
             for (int i = 0; i < separator.getRealSize(); i++) {
                 if (c == -1) {
                     break LineLoop;
                 } else if (c != separator.getUnsafeBytes()[separator.getBegin() + i]) {
                     buf.append(c);
                     continue LineLoop;
                 }
                 buf.append(c);
                 if (i < separator.getRealSize() - 1) {
                     c = read();
                 }
             }
             break;
         }
 
         if (separatorString == PARAGRAPH_DELIMETER) {
             while (c == separator.getUnsafeBytes()[separator.getBegin()]) {
                 c = read();
             }
             ungetc(c);
         }
 
         return buf;
     }
 
     public synchronized int getline(ByteList dst, byte terminator) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
         descriptor.checkOpen();
 
         int totalRead = 0;
         boolean found = false;
         if (ungotc != -1) {
             dst.append((byte) ungotc);
             found = ungotc == terminator;
             ungotc = -1;
             ++totalRead;
         }
         while (!found) {
             final byte[] bytes = buffer.array();
             final int begin = buffer.arrayOffset() + buffer.position();
             final int end = begin + buffer.remaining();
             int len = 0;
             for (int i = begin; i < end && !found; ++i) {
                 found = bytes[i] == terminator;
                 ++len;
             }
             if (len > 0) {
                 dst.append(buffer, len);
                 totalRead += len;
             }
             if (!found) {
                 int n = refillBuffer();
                 if (n <= 0) {
                     if (n < 0 && totalRead < 1) {
                         return -1;
                     }
                     break;
                 }
             }
         }
         return totalRead;
     }
 
     public synchronized int getline(ByteList dst, byte terminator, long limit) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
         descriptor.checkOpen();
 
         int totalRead = 0;
         boolean found = false;
         if (ungotc != -1) {
             dst.append((byte) ungotc);
             found = ungotc == terminator;
             ungotc = -1;
             limit--;
             ++totalRead;
         }
         while (!found) {
             final byte[] bytes = buffer.array();
             final int begin = buffer.arrayOffset() + buffer.position();
             final int end = begin + buffer.remaining();
             int len = 0;
             for (int i = begin; i < end && limit-- > 0 && !found; ++i) {
                 found = bytes[i] == terminator;
                 ++len;
             }
             if (limit < 1) found = true;
 
             if (len > 0) {
                 dst.append(buffer, len);
                 totalRead += len;
             }
             if (!found) {
                 int n = refillBuffer();
                 if (n <= 0) {
                     if (n < 0 && totalRead < 1) {
                         return -1;
                     }
                     break;
                 }
             }
         }
         return totalRead;
     }
 
     /**
      * @deprecated readall do busy loop for the IO which has NONBLOCK bit. You
      *             should implement the logic by yourself with fread().
      */
     @Deprecated
     public synchronized ByteList readall() throws IOException, BadDescriptorException {
         final long fileSize = descriptor.isSeekable() && descriptor.getChannel() instanceof FileChannel
                 ? ((FileChannel) descriptor.getChannel()).size() : 0;
         //
         // Check file size - special files in /proc have zero size and need to be
         // handled by the generic read path.
         //
         if (fileSize > 0) {
             ensureRead();
 
             FileChannel channel = (FileChannel)descriptor.getChannel();
             final long left = fileSize - channel.position() + bufferedInputBytesRemaining();
             if (left <= 0) {
                 eof = true;
                 return null;
             }
 
             if (left > Integer.MAX_VALUE) {
                 if (getRuntime() != null) {
                     throw getRuntime().newIOError("File too large");
                 } else {
                     throw new IOException("File too large");
                 }
             }
 
             ByteList result = new ByteList((int) left);
             ByteBuffer buf = ByteBuffer.wrap(result.getUnsafeBytes(),
                     result.begin(), (int) left);
 
             //
             // Copy any buffered data (including ungetc byte)
             //
             copyBufferedBytes(buf);
 
             //
             // Now read unbuffered directly from the file
             //
             while (buf.hasRemaining()) {
                 final int MAX_READ_CHUNK = 1 * 1024 * 1024;
                 //
                 // When reading into a heap buffer, the jvm allocates a temporary
                 // direct ByteBuffer of the requested size.  To avoid allocating
                 // a huge direct buffer when doing ludicrous reads (e.g. 1G or more)
                 // we split the read up into chunks of no more than 1M
                 //
                 ByteBuffer tmp = buf.duplicate();
                 if (tmp.remaining() > MAX_READ_CHUNK) {
                     tmp.limit(tmp.position() + MAX_READ_CHUNK);
                 }
                 int n = channel.read(tmp);
                 if (n <= 0) {
                     break;
                 }
                 buf.position(tmp.position());
             }
             eof = true;
             result.length(buf.position());
             return result;
         } else if (descriptor.isNull()) {
             return new ByteList(0);
         } else {
             checkReadable();
 
             ByteList byteList = new ByteList();
             ByteList read = fread(BUFSIZE);
 
             if (read == null) {
                 eof = true;
                 return byteList;
             }
 
             while (read != null) {
                 byteList.append(read);
                 read = fread(BUFSIZE);
             }
 
             return byteList;
         }
     }
 
     /**
      * Copies bytes from the channel buffer into a destination <tt>ByteBuffer</tt>
      *
      * @param dst A <tt>ByteBuffer</tt> to place the data in.
      * @return The number of bytes copied.
      */
     private final int copyBufferedBytes(ByteBuffer dst) {
         final int bytesToCopy = dst.remaining();
 
         if (ungotc != -1 && dst.hasRemaining()) {
             dst.put((byte) ungotc);
             ungotc = -1;
         }
 
         if (buffer.hasRemaining() && dst.hasRemaining()) {
 
             if (dst.remaining() >= buffer.remaining()) {
                 //
                 // Copy out any buffered bytes
                 //
                 dst.put(buffer);
 
             } else {
                 //
                 // Need to clamp source (buffer) size to avoid overrun
                 //
                 ByteBuffer tmp = buffer.duplicate();
                 tmp.limit(tmp.position() + dst.remaining());
                 dst.put(tmp);
                 buffer.position(tmp.position());
             }
         }
 
         return bytesToCopy - dst.remaining();
     }
 
     /**
      * Copies bytes from the channel buffer into a destination <tt>ByteBuffer</tt>
      *
      * @param dst A <tt>ByteBuffer</tt> to place the data in.
      * @return The number of bytes copied.
      */
     private final int copyBufferedBytes(byte[] dst, int off, int len) {
         int bytesCopied = 0;
 
         if (ungotc != -1 && len > 0) {
             dst[off++] = (byte) ungotc;
             ungotc = -1;
             ++bytesCopied;
         }
 
         final int n = Math.min(len - bytesCopied, buffer.remaining());
         buffer.get(dst, off, n);
         bytesCopied += n;
 
         return bytesCopied;
     }
 
     /**
      * Copies bytes from the channel buffer into a destination <tt>ByteBuffer</tt>
      *
      * @param dst A <tt>ByteList</tt> to place the data in.
      * @param len The maximum number of bytes to copy.
      * @return The number of bytes copied.
      */
     private final int copyBufferedBytes(ByteList dst, int len) {
         int bytesCopied = 0;
 
         dst.ensure(Math.min(len, bufferedInputBytesRemaining()));
 
         if (bytesCopied < len && ungotc != -1) {
             ++bytesCopied;
             dst.append((byte) ungotc);
             ungotc = -1;
         }
 
         //
         // Copy out any buffered bytes
         //
         if (bytesCopied < len && buffer.hasRemaining()) {
             int n = Math.min(buffer.remaining(), len - bytesCopied);
             dst.append(buffer, n);
             bytesCopied += n;
         }
 
         return bytesCopied;
     }
 
     /**
      * Returns a count of how many bytes are available in the read buffer
      *
      * @return The number of bytes that can be read without reading the underlying stream.
      */
     private final int bufferedInputBytesRemaining() {
         return reading ? (buffer.remaining() + (ungotc != -1 ? 1 : 0)) : 0;
     }
 
     /**
      * Tests if there are bytes remaining in the read buffer.
      *
      * @return <tt>true</tt> if there are bytes available in the read buffer.
      */
     private final boolean hasBufferedInputBytes() {
         return reading && (buffer.hasRemaining() || ungotc != -1);
     }
 
     /**
      * Returns a count of how many bytes of space is available in the write buffer.
      *
      * @return The number of bytes that can be written to the buffer without flushing
      * to the underlying stream.
      */
     private final int bufferedOutputSpaceRemaining() {
         return !reading ? buffer.remaining() : 0;
     }
 
     /**
      * Tests if there is space available in the write buffer.
      *
      * @return <tt>true</tt> if there are bytes available in the write buffer.
      */
     private final boolean hasBufferedOutputSpace() {
         return !reading && buffer.hasRemaining();
     }
 
     /**
      * Closes IO handler resources.
      *
      * @throws IOException
      * @throws BadDescriptorException
      */
     public void fclose() throws IOException, BadDescriptorException {
         try {
             synchronized (this) {
                 closedExplicitly = true;
                 close(); // not closing from finalize
             }
         } finally {
             Ruby localRuntime = getRuntime();
 
             // Make sure we remove finalizers while not holding self lock,
             // otherwise there is a possibility for a deadlock!
             if (localRuntime != null) localRuntime.removeInternalFinalizer(this);
 
             // clear runtime so it doesn't get stuck in memory (JRUBY-2933)
             runtime = null;
         }
     }
 
     /**
      * Internal close.
      *
      * @throws IOException
      * @throws BadDescriptorException
      */
     private void close() throws IOException, BadDescriptorException {
         // finish and close ourselves
         finish(true);
     }
 
     private void finish(boolean close) throws BadDescriptorException, IOException {
         try {
             flushWrite();
 
             if (DEBUG) LOG.info("Descriptor for fileno {} closed by stream", descriptor.getFileno());
         } finally {
             buffer = EMPTY_BUFFER;
 
             // clear runtime so it doesn't get stuck in memory (JRUBY-2933)
             runtime = null;
 
             // finish descriptor
             descriptor.finish(close);
         }
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      */
     public synchronized int fflush() throws IOException, BadDescriptorException {
         checkWritable();
         try {
             flushWrite();
         } catch (EOFException eofe) {
             return -1;
         }
         return 0;
     }
 
     /**
      * Flush the write buffer to the channel (if needed)
      * @throws IOException
      */
     private void flushWrite() throws IOException, BadDescriptorException {
         if (reading || !modes.isWritable() || buffer.position() == 0) return; // Don't bother
 
         int len = buffer.position();
         buffer.flip();
         int n = descriptor.write(buffer);
 
         if(n != len) {
             // TODO: check the return value here
         }
         buffer.clear();
     }
 
     /**
      * Flush the write buffer to the channel (if needed)
      * @throws IOException
      */
     private boolean flushWrite(final boolean block) throws IOException, BadDescriptorException {
         if (reading || !modes.isWritable() || buffer.position() == 0) return false; // Don't bother
         int len = buffer.position();
         int nWritten = 0;
         buffer.flip();
 
         // For Sockets, only write as much as will fit.
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     if (oldBlocking != block) {
                         selectableChannel.configureBlocking(block);
                     }
                     nWritten = descriptor.write(buffer);
                 } finally {
                     if (oldBlocking != block) {
                         selectableChannel.configureBlocking(oldBlocking);
                     }
                 }
             }
         } else {
             nWritten = descriptor.write(buffer);
         }
         if (nWritten != len) {
             buffer.compact();
             return false;
         }
         buffer.clear();
         return true;
     }
 
     /**
      * @see org.jruby.util.IOHandler#getInputStream()
      */
     public InputStream newInputStream() {
         InputStream in = descriptor.getBaseInputStream();
         return in == null ? new InputStreamAdapter(this) : in;
     }
 
     /**
      * @see org.jruby.util.IOHandler#getOutputStream()
      */
     public OutputStream newOutputStream() {
         return new OutputStreamAdapter(this);
     }
 
     public void clearerr() {
         eof = false;
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#isEOF()
      */
     public boolean feof() throws IOException, BadDescriptorException {
         checkReadable();
 
         if (eof) {
             return true;
         } else {
             return false;
         }
     }
 
     /**
      * @throws IOException
      * @see org.jruby.util.IOHandler#pos()
      */
     public synchronized long fgetpos() throws IOException, PipeException, InvalidValueException, BadDescriptorException {
         // Correct position for read / write buffering (we could invalidate, but expensive)
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             long pos = fileChannel.position();
             // Adjust for buffered data
             if (reading) {
                 pos -= buffer.remaining();
                 return pos - (pos > 0 && ungotc != -1 ? 1 : 0);
             } else {
                 return pos + buffer.position();
             }
         } else if (descriptor.isNull()) {
             return 0;
         } else {
             throw new PipeException();
         }
     }
 
     /**
      * Implementation of libc "lseek", which seeks on seekable streams, raises
      * EPIPE if the fd is assocated with a pipe, socket, or FIFO, and doesn't
      * do anything for other cases (like stdio).
      *
      * @throws IOException
      * @throws InvalidValueException
      * @see org.jruby.util.IOHandler#seek(long, int)
      */
     public synchronized void lseek(long offset, int type) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             ungotc = -1;
             int adj = 0;
             if (reading) {
                 // for SEEK_CUR, need to adjust for buffered data
                 adj = buffer.remaining();
                 buffer.clear();
                 buffer.flip();
             } else {
                 flushWrite();
             }
             try {
                 switch (type) {
                 case SEEK_SET:
                     fileChannel.position(offset);
                     break;
                 case SEEK_CUR:
                     fileChannel.position(fileChannel.position() - adj + offset);
                     break;
                 case SEEK_END:
                     fileChannel.position(fileChannel.size() + offset);
                     break;
                 }
             } catch (IllegalArgumentException e) {
                 throw new InvalidValueException();
             } catch (IOException ioe) {
                 throw ioe;
             }
         } else if (descriptor.getChannel() instanceof SelectableChannel) {
             // TODO: It's perhaps just a coincidence that all the channels for
             // which we should raise are instanceof SelectableChannel, since
             // stdio is not...so this bothers me slightly. -CON
             throw new PipeException();
         } else {
         }
     }
 
     /**
      * @see org.jruby.util.IOHandler#sync()
      */
     public synchronized void sync() throws IOException, BadDescriptorException {
         flushWrite();
     }
 
     /**
      * Ensure buffer is ready for reading, flushing remaining writes if required
      * @throws IOException
      */
     private void ensureRead() throws IOException, BadDescriptorException {
         if (reading) return;
         flushWrite();
         buffer.clear();
         buffer.flip();
         reading = true;
     }
 
     /**
      * Ensure buffer is ready for reading, flushing remaining writes if required
      * @throws IOException
      */
     private void ensureReadNonBuffered() throws IOException, BadDescriptorException {
         if (reading) {
             if (buffer.hasRemaining()) {
                 Ruby localRuntime = getRuntime();
                 if (localRuntime != null) {
                     throw localRuntime.newIOError("sysread for buffered IO");
                 } else {
                     throw new IOException("sysread for buffered IO");
                 }
             }
         } else {
             // libc flushes writes on any read from the actual file, so we flush here
             flushWrite();
             buffer.clear();
             buffer.flip();
             reading = true;
         }
     }
 
     private void resetForWrite() throws IOException {
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             if (buffer.hasRemaining()) { // we have read ahead, and need to back up
                 fileChannel.position(fileChannel.position() - buffer.remaining());
             }
         }
         // FIXME: Clearing read buffer here...is this appropriate?
         buffer.clear();
         reading = false;
     }
 
     /**
      * Ensure buffer is ready for writing.
      * @throws IOException
      */
     private void ensureWrite() throws IOException {
         if (!reading) return;
         resetForWrite();
     }
 
     public synchronized ByteList read(int number) throws IOException, BadDescriptorException {
         checkReadable();
         ensureReadNonBuffered();
 
         ByteList byteList = new ByteList(number);
 
         // TODO this should entry into error handling somewhere
         int bytesRead = descriptor.read(number, byteList);
 
         if (bytesRead == -1) {
             eof = true;
         }
 
         return byteList;
     }
 
     private ByteList bufferedRead(int number) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
 
         int resultSize = 0;
 
         // 128K seems to be the minimum at which the stat+seek is faster than reallocation
         final int BULK_THRESHOLD = 128 * 1024;
         if (number >= BULK_THRESHOLD && descriptor.isSeekable() && descriptor.getChannel() instanceof FileChannel) {
             //
             // If it is a file channel, then we can pre-allocate the output buffer
             // to the total size of buffered + remaining bytes in file
             //
             FileChannel fileChannel = (FileChannel) descriptor.getChannel();
             resultSize = (int) Math.min(fileChannel.size() - fileChannel.position() + bufferedInputBytesRemaining(), number);
         } else {
             //
             // Cannot discern the total read length - allocate at least enough for the buffered data
             //
             resultSize = Math.min(bufferedInputBytesRemaining(), number);
         }
 
         ByteList result = new ByteList(resultSize);
         bufferedRead(result, number);
         return result;
     }
 
     private int bufferedRead(ByteList dst, int number) throws IOException, BadDescriptorException {
 
         int bytesRead = 0;
 
         //
         // Copy what is in the buffer, if there is some buffered data
         //
         bytesRead += copyBufferedBytes(dst, number);
 
         boolean done = false;
         //
         // Avoid double-copying for reads that are larger than the buffer size
         //
         while ((number - bytesRead) >= BUFSIZE) {
             //
             // limit each iteration to a max of BULK_READ_SIZE to avoid over-size allocations
             //
             final int bytesToRead = Math.min(BULK_READ_SIZE, number - bytesRead);
             final int n = descriptor.read(bytesToRead, dst);
             if (n == -1) {
                 eof = true;
                 done = true;
                 break;
             } else if (n == 0) {
                 done = true;
                 break;
             }
             bytesRead += n;
         }
 
         //
         // Complete the request by filling the read buffer first
         //
         while (!done && bytesRead < number) {
             int read = refillBuffer();
 
             if (read == -1) {
                 eof = true;
                 break;
             } else if (read == 0) {
                 break;
             }
 
             // append what we read into our buffer and allow the loop to continue
             final int len = Math.min(buffer.remaining(), number - bytesRead);
             dst.append(buffer, len);
             bytesRead += len;
         }
 
         if (bytesRead == 0 && number != 0) {
             if (eof) {
                 throw new EOFException();
             }
         }
 
         return bytesRead;
     }
 
     private int bufferedRead(ByteBuffer dst, boolean partial) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
 
         boolean done = false;
         int bytesRead = 0;
 
         //
         // Copy what is in the buffer, if there is some buffered data
         //
         bytesRead += copyBufferedBytes(dst);
 
         //
         // Avoid double-copying for reads that are larger than the buffer size, or
         // the destination is a direct buffer.
         //
         while ((bytesRead < 1 || !partial) && (dst.remaining() >= BUFSIZE || dst.isDirect())) {
             ByteBuffer tmpDst = dst;
             if (!dst.isDirect()) {
                 //
                 // We limit reads to BULK_READ_SIZED chunks to avoid NIO allocating
                 // a huge temporary native buffer, when doing reads into a heap buffer
                 // If the dst buffer is direct, then no need to limit.
                 //
                 int bytesToRead = Math.min(BULK_READ_SIZE, dst.remaining());
                 if (bytesToRead < dst.remaining()) {
                     tmpDst = dst.duplicate();
                     tmpDst.limit(tmpDst.position() + bytesToRead);
                 }
             }
             int n = descriptor.read(tmpDst);
             if (n == -1) {
                 eof = true;
                 done = true;
                 break;
             } else if (n == 0) {
                 done = true;
                 break;
             } else {
                 bytesRead += n;
             }
         }
 
         //
         // Complete the request by filling the read buffer first
         //
         while (!done && dst.hasRemaining() && (bytesRead < 1 || !partial)) {
             int read = refillBuffer();
 
             if (read == -1) {
                 eof = true;
                 done = true;
                 break;
             } else if (read == 0) {
                 done = true;
                 break;
             } else {
                 // append what we read into our buffer and allow the loop to continue
                 bytesRead += copyBufferedBytes(dst);
             }
         }
 
         if (eof && bytesRead == 0 && dst.remaining() != 0) {
             throw new EOFException();
         }
 
         return bytesRead;
     }
 
     private int bufferedRead() throws IOException, BadDescriptorException {
         ensureRead();
 
         if (!buffer.hasRemaining()) {
             int len = refillBuffer();
             if (len == -1) {
                 eof = true;
                 return -1;
             } else if (len == 0) {
                 return -1;
             }
         }
         return buffer.get() & 0xFF;
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(ByteList buf) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         // Ruby ignores empty syswrites
         if (buf == null || buf.length() == 0) return 0;
 
         if (buf.length() > buffer.capacity()) { // Doesn't fit in buffer. Write immediately.
             flushWrite(); // ensure nothing left to write
 
 
             int n = descriptor.write(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin(), buf.length()));
             if(n != buf.length()) {
                 // TODO: check the return value here
             }
         } else {
             if (buf.length() > buffer.remaining()) flushWrite();
 
             buffer.put(buf.getUnsafeBytes(), buf.begin(), buf.length());
         }
 
         if (isSync()) flushWrite();
 
         return buf.getRealSize();
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(ByteBuffer buf) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         // Ruby ignores empty syswrites
         if (buf == null || !buf.hasRemaining()) return 0;
 
         final int nbytes = buf.remaining();
         if (nbytes >= buffer.capacity()) { // Doesn't fit in buffer. Write immediately.
             flushWrite(); // ensure nothing left to write
 
             descriptor.write(buf);
             // TODO: check the return value here
         } else {
             if (nbytes > buffer.remaining()) flushWrite();
 
             buffer.put(buf);
         }
 
         if (isSync()) flushWrite();
 
         return nbytes - buf.remaining();
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(int c) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         if (!buffer.hasRemaining()) flushWrite();
 
         buffer.put((byte) c);
 
         if (isSync()) flushWrite();
 
         return 1;
     }
 
     public synchronized void ftruncate(long newLength) throws IOException,
             BadDescriptorException, InvalidValueException {
         Channel ch = descriptor.getChannel();
         if (!(ch instanceof FileChannel)) {
             throw new InvalidValueException();
         }
         invalidateBuffer();
         FileChannel fileChannel = (FileChannel)ch;
         if (newLength > fileChannel.size()) {
             // truncate can't lengthen files, so we save position, seek/write, and go back
             long position = fileChannel.position();
             int difference = (int)(newLength - fileChannel.size());
 
             fileChannel.position(fileChannel.size());
             // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
             fileChannel.write(ByteBuffer.allocate(difference));
             fileChannel.position(position);
         } else {
             fileChannel.truncate(newLength);
         }
     }
 
     /**
      * Invalidate buffer before a position change has occurred (e.g. seek),
      * flushing writes if required, and correcting file position if reading
      * @throws IOException
      */
     private void invalidateBuffer() throws IOException, BadDescriptorException {
         if (!reading) flushWrite();
         int posOverrun = buffer.remaining(); // how far ahead we are when reading
         buffer.clear();
         if (reading) {
             buffer.flip();
             // if the read buffer is ahead, back up
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             if (posOverrun != 0) fileChannel.position(fileChannel.position() - posOverrun);
         }
     }
 
     /**
      * Ensure close (especially flush) when we're finished with.
      */
diff --git a/src/org/jruby/util/io/OpenFile.java b/src/org/jruby/util/io/OpenFile.java
index 83a7878606..2847440273 100644
--- a/src/org/jruby/util/io/OpenFile.java
+++ b/src/org/jruby/util/io/OpenFile.java
@@ -1,371 +1,374 @@
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
+        if (pipeStream != null) {
+            pipeStream.setBinmode();
+        }
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
                         // Newer JDKs actively close the process streams when
                         // the child exits, so we have to confirm it's still
                         // open to avoid raising an error here when we try to
                         // flush and close the stream.
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
                         // Newer JDKs actively close the process streams when
                         // the child exits, so we have to confirm it's still
                         // open to avoid raising an error here when we try to
                         // flush and close the stream.
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
diff --git a/src/org/jruby/util/io/Stream.java b/src/org/jruby/util/io/Stream.java
index 2f1566d832..7ef0142c39 100644
--- a/src/org/jruby/util/io/Stream.java
+++ b/src/org/jruby/util/io/Stream.java
@@ -1,178 +1,179 @@
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
  * Copyright (C) 2008-2009 The JRuby Community <www.jruby.org>
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
 package org.jruby.util.io;
 
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.nio.channels.Channel;
 import org.jruby.util.ByteList;
 import org.jruby.Ruby;
 
 /**
  */
 public interface Stream {
     static final int SEEK_SET = 0;
     static final int SEEK_CUR = 1;
     static final int SEEK_END = 2;
 
     // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it)
     static final ByteList PARAGRAPH_DELIMETER = ByteList.create("PARAGRPH_DELIM_MRK_ER");
 
     static final ByteList PARAGRAPH_SEPARATOR = ByteList.create("\n\n");
 
     ChannelDescriptor getDescriptor();
 
     void clearerr();
 
     ModeFlags getModes();
 
     boolean isSync();
 
     void setSync(boolean sync);
 
     ByteList fgets(ByteList separatorString) throws IOException, BadDescriptorException, EOFException;
     ByteList readall() throws IOException, BadDescriptorException, EOFException;
 
 
     /**
      * Read all bytes up to and including a terminator byte.
      *
      * <p>If the terminator byte is found, it will be the last byte in the output buffer.</p>
      *
      * @param dst The output buffer.
      * @param terminator The byte to terminate reading.
      * @return The number of bytes read, or -1 if EOF is reached.
      *
      * @throws java.io.IOException
      * @throws org.jruby.util.io.BadDescriptorException
      */
     int getline(ByteList dst, byte terminator) throws IOException, BadDescriptorException;
 
     /**
      * Reads all bytes up to and including a terminator byte or until limit is reached.
      *
      * <p>If the terminator byte is found, it will be the last byte in the output buffer.</p>
      *
      * @param dst The output buffer.
      * @param terminator The byte to terminate reading.
      * @param limit the number of bytes to read unless EOF or terminator is found
      * @return The number of bytes read, or -1 if EOF is reached.
      *
      * @throws java.io.IOException
      * @throws org.jruby.util.io.BadDescriptorException
      */
     int getline(ByteList dst, byte terminator, long limit) throws IOException, BadDescriptorException;
 
     // TODO: We overflow on large files...We could increase to long to limit
     // this, but then the impl gets more involved since java io APIs based on
     // int (means we have to chunk up a long into a series of int ops).
 
     ByteList fread(int number) throws IOException, BadDescriptorException, EOFException;
     int fwrite(ByteList string) throws IOException, BadDescriptorException;
 
     int fgetc() throws IOException, BadDescriptorException, EOFException;
     int ungetc(int c);
     void fputc(int c) throws IOException, BadDescriptorException;
 
     ByteList read(int number) throws IOException, BadDescriptorException, EOFException;
 
     void fclose() throws IOException, BadDescriptorException;
     int fflush() throws IOException, BadDescriptorException;
 
     /**
      * <p>Flush and sync all writes to the filesystem.</p>
      *
      * @throws IOException if the sync does not work
      */
     void sync() throws IOException, BadDescriptorException;
 
     /**
      * <p>Return true when at end of file (EOF).</p>
      *
      * @return true if at EOF; false otherwise
      * @throws IOException
      * @throws BadDescriptorException
      */
     boolean feof() throws IOException, BadDescriptorException;
 
     /**
      * <p>Get the current position within the file associated with this
      * handler.</p>
      *
      * @return the current position in the file.
      * @throws IOException
      * @throws PipeException ESPIPE (illegal seek) when not a file
      *
      */
     long fgetpos() throws IOException, PipeException, BadDescriptorException, InvalidValueException;
 
     /**
      * <p>Perform a seek based on pos().  </p>
      * @throws IOException
      * @throws PipeException
      * @throws InvalidValueException
      */
     void lseek(long offset, int type) throws IOException, InvalidValueException, PipeException, BadDescriptorException;
     void ftruncate(long newLength) throws IOException, PipeException,
             InvalidValueException, BadDescriptorException;
 
     /**
      * Implement IO#ready? as per io/wait in MRI.
      * returns non-nil if input available without blocking, or nil.
      */
     int ready() throws IOException;
 
     /**
      * Implement IO#wait as per io/wait in MRI.
      * waits until input available or timed out and returns self, or nil when EOF reached.
      *
      * The default implementation loops while ready returns 0.
      */
     void waitUntilReady() throws IOException, InterruptedException;
 
     boolean readDataBuffered();
     boolean writeDataBuffered();
 
     InputStream newInputStream();
 
     OutputStream newOutputStream();
 
     boolean isBlocking();
 
     void setBlocking(boolean blocking) throws IOException;
 
     void freopen(Ruby runtime, String path, ModeFlags modes) throws DirectoryAsFileException, IOException, InvalidValueException, PipeException, BadDescriptorException;
 
     void setBinmode();
+    boolean isBinmode();
     Channel getChannel();
 
     boolean isAutoclose();
     void setAutoclose(boolean autoclose);
 }
