diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index f17e8dbb4d..d01b662630 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -820,2001 +820,2005 @@ public class RubyIO extends RubyObject {
                 } catch (EOFException e) {
                     n = -1;
                 }
 
                 if (n == -1) {
                     if (!readStream.isBlocking() && (readStream instanceof ChannelStream)) {
                         checkDescriptor(runtime, ((ChannelStream)readStream).getDescriptor());
                         continue;
                     } else {
                         break;
                     }
                 }
 
                 update = true;
             } while (c != delim);
 
             if (!update) {
                 return runtime.getNil();
             } else {
                 incrementLineno(runtime, openFile);
                 RubyString str = RubyString.newString(runtime, cache != null ? new ByteList(buf) : buf);
                 str.setTaint(true);
                 return str;
             }
         }
         finally {
             if(cache != null) {
                 cache.release(buf);
             }
         }
     }
     // IO class methods.
 
     @JRubyMethod(name = {"new", "for_fd"}, rest = true, frame = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass)recv;
         
         if (block.isGiven()) {
             String className = klass.getName();
             context.getRuntime().getWarnings().warn(
                     ID.BLOCK_NOT_ACCEPTED,
                     className + "::new() does not take block; use " + className + "::open() instead",
                     className + "::open()");
         }
         
         return klass.newInstance(context, args, block);
     }
 
     private IRubyObject initializeCommon19(int fileno, ModeFlags modes) {
         try {
             ChannelDescriptor descriptor = getDescriptorByFileno(fileno);
 
             if (descriptor == null) throw getRuntime().newErrnoEBADFError();
 
             descriptor.checkOpen();
 
             if (modes == null) modes = descriptor.getOriginalModes();
 
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
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, Block unusedBlock) {
         return initializeCommon19(RubyNumeric.fix2int(fileNumber), null);
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject second, Block unusedBlock) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         ModeFlags modes;
         if (second instanceof RubyHash) {
             modes = parseOptions(context, second, null);
         } else {
             modes = parseModes19(context, second);
         }
 
         return initializeCommon19(fileno, modes);
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject modeValue, IRubyObject options, Block unusedBlock) {
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
             IRubyObject[] fullEncoding = modes19.split(context, RubyString.newString(context.getRuntime(), ":")).toJavaArray();
 
             IRubyObject externalEncodingOption = fullEncoding[initialPosition];
             IRubyObject internalEncodingOption = null;
             if (fullEncoding.length > (initialPosition + 1)) {
                 internalEncodingOption = fullEncoding[initialPosition + 1];
                 set_encoding(context, externalEncodingOption, internalEncodingOption);
             } else {
                 set_encoding(context, externalEncodingOption);
             }
         }
     }
 
     @JRubyMethod(name = "initialize", required = 1, optional = 1, frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         int argCount = args.length;
         ModeFlags modes;
         
         int fileno = RubyNumeric.fix2int(args[0]);
         
         try {
             ChannelDescriptor descriptor = getDescriptorByFileno(fileno);
             
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
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject external_encoding(ThreadContext context) {
         return externalEncoding != null ? externalEncoding : RubyEncoding.getDefaultExternal(context.getRuntime());
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject internal_encoding(ThreadContext context) {
         return internalEncoding != null ? internalEncoding : context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=CompatVersion.RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString) {
         setExternalEncoding(context, encodingString);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=CompatVersion.RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding, IRubyObject options) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     private void setExternalEncoding(ThreadContext context, IRubyObject encoding) {
         externalEncoding = getEncodingCommon(context, encoding);
     }
 
 
     private void setInternalEncoding(ThreadContext context, IRubyObject encoding) {
         IRubyObject internalEncodingOption = getEncodingCommon(context, encoding);
 
         if (internalEncodingOption.toString().equals(external_encoding(context).toString())) {
             context.getRuntime().getWarnings().warn("Ignoring internal encoding " + encoding
                     + ": it is identical to external encoding " + external_encoding(context));
         } else {
             internalEncoding = internalEncodingOption;
         }
     }
 
     private IRubyObject getEncodingCommon(ThreadContext context, IRubyObject encoding) {
         IRubyObject rubyEncoding = null;
         if (encoding instanceof RubyEncoding) {
             rubyEncoding = encoding;
         } else {
             Encoding encodingObj = RubyEncoding.getEncodingFromObject(context.getRuntime(), encoding);
             rubyEncoding = RubyEncoding.convertEncodingToRubyEncoding(context.getRuntime(), encodingObj);            
         }
         return rubyEncoding;
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, meta = true)
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
                     } else {
                         throw re;
                     }
                 }
             }
         }
 
         return io;
     }
 
     @JRubyMethod(name = "sysopen", required = 1, optional = 2, frame = true, meta = true)
     public static IRubyObject sysopen(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         IRubyObject pathString = args[0].convertToString();
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
                                        path, modes, perms, runtime.getPosix());
             runtime.registerDescriptor(descriptor,true); // isRetained=true
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
 
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
         if (isClosed()) {
             throw getRuntime().newIOError("closed stream");
         }
         openFile.setBinmode();
         return this;
     }
 
     @JRubyMethod(name = "binmode?", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_binmode(ThreadContext context) {
         return RubyBoolean.newBoolean(context.getRuntime(), openFile.isBinmode());
     }
     
     /** @deprecated will be removed in 1.2 */
     protected void checkInitialized() {
         if (openFile == null) {
             throw getRuntime().newIOError("uninitialized stream");
         }
     }
     
     /** @deprecated will be removed in 1.2 */
     protected void checkClosed() {
         if (openFile.getMainStream() == null && openFile.getPipeStream() == null) {
             throw getRuntime().newIOError("closed stream");
         }
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
             
             int read = writeStream.getDescriptor().write(string.getByteList());
             
             if (read == -1) {
                 // TODO? I think this ends up propagating from normal Java exceptions
                 // sys_fail(openFile.getPath())
             }
             
             return runtime.newFixnum(read);
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newSystemCallError(e.getMessage());
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
         }  catch (PipeException ex) {
             throw context.getRuntime().newErrnoEPIPEError();
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
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         }
     }
 
     protected boolean waitWritable(ChannelDescriptor descriptor) throws IOException {
         Channel channel = descriptor.getChannel();
         if (channel == null || !(channel instanceof SelectableChannel)) {
             return false;
         }
 
         SelectableChannel selectable = (SelectableChannel)channel;
         Selector selector = null;
         synchronized (selectable.blockingLock()) {
             boolean oldBlocking = selectable.isBlocking();
             try {
                 selector = Selector.open();
 
                 selectable.configureBlocking(false);
                 int real_ops = selectable.validOps() & SelectionKey.OP_WRITE;
                 SelectionKey key = selectable.keyFor(selector);
 
                 if (key == null) {
                     selectable.register(selector, real_ops, descriptor);
                 } else {
                     key.interestOps(key.interestOps()|real_ops);
                 }
 
                 while(selector.select() == 0);
 
                 for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                     SelectionKey skey = (SelectionKey) i.next();
                     if ((skey.interestOps() & skey.readyOps() & (SelectionKey.OP_WRITE)) != 0) {
                         if(skey.attachment() == descriptor) {
                             return true;
                         }
                     }
                 }
                 return false;
             } finally {
                 if (selector != null) {
                     try {
                         selector.close();
                     } catch (Exception e) {
                     }
                 }
                 selectable.configureBlocking(oldBlocking);
             }
         }
     }
 
     /*
      * Throw bad file descriptor is we can not read on supplied descriptor.
      */
     private void checkDescriptor(Ruby runtime, ChannelDescriptor descriptor) throws IOException {
         if (!(waitReadable(descriptor))) throw runtime.newIOError("bad file descriptor: " + openFile.getPath());
     }
 
     protected boolean waitReadable(ChannelDescriptor descriptor) throws IOException {
         Channel channel = descriptor.getChannel();
         if (channel == null || !(channel instanceof SelectableChannel)) {
             return false;
         }
 
         SelectableChannel selectable = (SelectableChannel)channel;
         Selector selector = null;
         synchronized (selectable.blockingLock()) {
             boolean oldBlocking = selectable.isBlocking();
             try {
                 selector = Selector.open();
 
                 selectable.configureBlocking(false);
                 int real_ops = selectable.validOps() & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT);
                 SelectionKey key = selectable.keyFor(selector);
 
                 if (key == null) {
                     selectable.register(selector, real_ops, descriptor);
                 } else {
                     key.interestOps(key.interestOps()|real_ops);
                 }
 
                 while(selector.select() == 0);
 
                 for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                     SelectionKey skey = (SelectionKey) i.next();
                     if ((skey.interestOps() & skey.readyOps() & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0) {
                         if(skey.attachment() == descriptor) {
                             return true;
                         }
                     }
                 }
                 return false;
             } finally {
                 if (selector != null) {
                     try {
                         selector.close();
                     } catch (Exception e) {
                     }
                 }
                 selectable.configureBlocking(oldBlocking);
             }
         }
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
 
                     if(eagain && waitWritable(writeStream.getDescriptor())) {
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
         return context.getRuntime().newFixnum(getOpenFileChecked().getMainStream().getDescriptor().getFileno());
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
         return context.getRuntime().newBoolean(getOpenFileChecked().getMainStream().isSync());
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
     
     /**
      * @deprecated
      * @return
      */
     public boolean writeDataBuffered() {
         return openFile.getMainStream().writeDataBuffered();
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos(ThreadContext context) {
         try {
             return context.getRuntime().newFixnum(getOpenFileChecked().getMainStream().fgetpos());
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException bde) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
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
             myOpenFile.getMainStream().lseek(offset, Stream.SEEK_SET);
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
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
             } catch (PipeException ex) {
                 throw context.getRuntime().newErrnoEPIPEError();
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
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
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
             
             pos = myOpenFile.getMainStream().getDescriptor().lseek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return context.getRuntime().newFixnum(pos);
     }
 
     @JRubyMethod(name = "rewind")
     public RubyFixnum rewind(ThreadContext context) {
         OpenFile myOpenfile = getOpenFileChecked();
         
         try {
             myOpenfile.getMainStream().lseek(0L, Stream.SEEK_SET);
             myOpenfile.getMainStream().clearerr();
             
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
             throw context.getRuntime().newIOError(e.getMessage());
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
         
-            myOpenFile.getWriteStream().sync();
+            Stream writeStream = myOpenFile.getWriteStream();
+
+            writeStream.fflush();
+            writeStream.sync();
+
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
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
         getOpenFileChecked().setSync(newSync.isTrue());
         getOpenFileChecked().getMainStream().setSync(newSync.isTrue());
 
         return this;
     }
 
     @JRubyMethod(name = {"eof?", "eof"})
     public RubyBoolean eof_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             if (myOpenFile.getMainStream().feof()) {
                 return runtime.getTrue();
             }
             
             if (myOpenFile.getMainStream().readDataBuffered()) {
                 return runtime.getFalse();
             }
             
             readCheck(myOpenFile.getMainStream());
             
             myOpenFile.getMainStream().clearerr();
             
             int c = myOpenFile.getMainStream().fgetc();
             
             if (c != -1) {
                 myOpenFile.getMainStream().ungetc(c);
                 return runtime.getFalse();
             }
             
             myOpenFile.checkClosed(runtime);
             
             myOpenFile.getMainStream().clearerr();
             
             return runtime.getTrue();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = {"tty?", "isatty"})
     public RubyBoolean tty_p(ThreadContext context) {
         return context.getRuntime().newBoolean(context.getRuntime().getPosix().isatty(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor()));
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
                 originalFile.getMainStream().lseek(0, Stream.SEEK_CUR);
             } else if (originalFile.isWritable()) {
                 originalFile.getMainStream().fflush();
             } else {
                 originalFile.getMainStream().lseek(0, Stream.SEEK_CUR);
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
                     modes = originalFile.getMainStream().getModes();
                 }
             }
             
             ChannelDescriptor descriptor = originalFile.getMainStream().getDescriptor().dup();
 
             newFile.setMainStream(ChannelStream.fdopen(runtime, descriptor, modes));
 
             newFile.getMainStream().setSync(originalFile.getMainStream().isSync());
             
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
             
             // Register the new descriptor
             registerDescriptor(newFile.getMainStream().getDescriptor());
         } catch (IOException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
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
             IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(runtime, openFile.getProcess().exitValue());
             runtime.getGlobalVariables().set("$?", processResult);
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
                 myOpenFile.getMainStream().fclose();
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
             throw getRuntime().newIOError(e.getMessage());
         }
 
         return this;
     }
 
     @Deprecated
     public IRubyObject gets(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? gets(context) : gets(context, args[0]);
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_8)
     public IRubyObject gets(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         IRubyObject result = getline(runtime, separator(runtime.getRecordSeparatorVar().get()));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_8)
     public IRubyObject gets(ThreadContext context, IRubyObject separatorArg) {
         IRubyObject result = getline(context.getRuntime(), separator(separatorArg));
 
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
         ByteList separator;
         long limit = -1;
         if (arg instanceof RubyInteger) {
             limit = RubyInteger.fix2long(arg);
             separator = separator(context.getRuntime());
         } else {
             separator = separator(arg);
         }
 
         IRubyObject result = getline(context.getRuntime(), separator, limit);
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context, IRubyObject separator, IRubyObject limit_arg) {
         long limit = limit_arg.isNil() ? -1 : RubyNumeric.fix2long(limit_arg);
         IRubyObject result = getline(context.getRuntime(), separator(separator), limit);
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     public boolean getBlocking() {
         return ((ChannelStream) openFile.getMainStream()).isBlocking();
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
         if (realCmd == FcntlLibrary.FD_CLOEXEC) {
             // Do nothing.  FD_CLOEXEC has no meaning in JVM since we cannot really exec.
             // And why the hell does webrick pass this in as a first argument!!!!!
         } else if (realCmd == Fcntl.F_SETFL.value() || realCmd == Fcntl.F_SETFD.value()) {
             if ((nArg & FcntlLibrary.FD_CLOEXEC) == FcntlLibrary.FD_CLOEXEC) {
                 // Do nothing.  FD_CLOEXEC has no meaning in JVM since we cannot really exec.
             } else {
                 try {
                     boolean block = (nArg & ModeFlags.NONBLOCK) != ModeFlags.NONBLOCK;
 
                     myOpenFile.getMainStream().setBlocking(block);
                 } catch (IOException e) {
                     throw runtime.newIOError(e.getMessage());
                 }
             }
         } else if (realCmd == Fcntl.F_GETFL.value()) {
             return myOpenFile.getMainStream().isBlocking() ? RubyFixnum.zero(runtime) : RubyFixnum.newFixnum(runtime, ModeFlags.NONBLOCK);
         } else {
             throw runtime.newNotImplementedError("JRuby only supports F_SETFL and F_GETFL with NONBLOCK for fcntl/ioctl");
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
                 line = NIL_BYTELIST;
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
 
     @Deprecated
     public IRubyObject readline(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? readline(context) : readline(context, args[0]);
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
     @JRubyMethod(name = {"getc", "getbyte"})
     public IRubyObject getc() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(getRuntime());
             myOpenFile.setReadBuffered();
 
             Stream stream = myOpenFile.getMainStream();
             
             readCheck(stream);
             stream.clearerr();
         
             int c = myOpenFile.getMainStream().fgetc();
             
             if (c == -1) {
                 // TODO: check for ferror, clear it, and try once more up above readCheck
 //                if (ferror(f)) {
 //                    clearerr(f);
 //                    if (!rb_io_wait_readable(fileno(f)))
 //                        rb_sys_fail(fptr->path);
 //                    goto retry;
 //                }
                 return getRuntime().getNil();
             }
         
             return getRuntime().newFixnum(c);
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
     @JRubyMethod(name = "ungetc", required = 1)
     public IRubyObject ungetc(IRubyObject number) {
         int ch = RubyNumeric.fix2int(number);
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (!myOpenFile.isReadBuffered()) {
             throw getRuntime().newIOError("unread stream");
         }
         
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
         
