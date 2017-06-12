diff --git a/rubyspecs.revision b/rubyspecs.revision
index 5531883e44..2121215499 100644
--- a/rubyspecs.revision
+++ b/rubyspecs.revision
@@ -1,7 +1,7 @@
 # These are the pointers to the 'stable/frozen' versions of
 # mspec and rubyspecs, used to for our CI runs.
 
 mspec.revision=ef4487f3cab0f9ce30c37e43cf9641c6ff791162
 
-rubyspecs.revision=14d651eac161efe4994c8cf8dcae60b341488786
+rubyspecs.revision=4469e2fd807e9d0b9bb27155a12f98d50f0bc60a
 
diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index 752e88d961..a60a13108c 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1104,2053 +1104,2056 @@ public class RubyIO extends RubyObject {
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
                 writeStream.fputc(RubyNumeric.num2chr(object));
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
             maybeIO.callMethod(context, "write", RubyString.newStringNoCopy(context.getRuntime(), new byte[] {(byte)c}));
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
         
             myOpenFile.getWriteStream().sync();
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
             try {
                 openFile.getProcess().destroy();
                 IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(runtime, openFile.getProcess().waitFor());
                 runtime.getGlobalVariables().set("$?", processResult);
             } catch (InterruptedException ie) {
                 // TODO: do something here?
             }
         }
         
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "close_write")
     public IRubyObject close_write(ThreadContext context) throws BadDescriptorException {
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
     public IRubyObject close_read(ThreadContext context) throws BadDescriptorException {
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
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "gets", optional = 1, writes = FrameField.LASTLINE)
     public IRubyObject gets(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         ByteList separator = getSeparatorForGets(runtime, args);
         
         IRubyObject result = getline(runtime, separator);
 
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
+        if (args.length == 0) {
+            return writeSeparator(context, maybeIO);
+        }
+
+        return putsArray(context, maybeIO, args);
+    }
+
+    private static IRubyObject writeSeparator(ThreadContext context, IRubyObject maybeIO) {
         Ruby runtime = context.getRuntime();
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
-        if (args.length == 0) {
-            write(context, maybeIO, separator.getByteList());
-            return runtime.getNil();
-        }
+        write(context, maybeIO, separator.getByteList());
+        return runtime.getNil();
+    }
+
+    private static IRubyObject putsArray(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
+        Ruby runtime = context.getRuntime();
+        assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
+        RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
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
 
-    private IRubyObject inspectPuts(ThreadContext context, RubyArray array) {
-        try {
-            context.getRuntime().registerInspecting(array);
-            return puts(context, array.toJavaArray());
-        } finally {
-            context.getRuntime().unregisterInspecting(array);
-        }
-    }
-
     private static IRubyObject inspectPuts(ThreadContext context, IRubyObject maybeIO, RubyArray array) {
         try {
             context.getRuntime().registerInspecting(array);
-            return puts(context, maybeIO, array.toJavaArray());
+            return putsArray(context, maybeIO, array.toJavaArray());
         } finally {
             context.getRuntime().unregisterInspecting(array);
         }
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "readline", optional = 1, writes = FrameField.LASTLINE)
     public IRubyObject readline(ThreadContext context, IRubyObject[] args) {
         IRubyObject line = gets(context, args);
 
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
     
     @JRubyMethod(name = "read_nonblock", required = 1, optional = 1)
     public IRubyObject read_nonblock(ThreadContext context, IRubyObject[] args) {
         IRubyObject value = getPartial(context, args, true);
 
         if (value.isNil()) throw context.getRuntime().newEOFError();
 
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
             // All errors to sysread should be SystemCallErrors, but on a closed stream
             // Ruby returns an IOError.  Java throws same exception for all errors so
             // we resort to this hack...
             if ("File not open".equals(e.getMessage())) {
                     throw getRuntime().newIOError(e.getMessage());
             }
     	    throw getRuntime().newSystemCallError(e.getMessage());
     	} finally {
             context.getThread().afterBlockingCall();
         }
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
 
     @JRubyMethod(name = "lines")
     public IRubyObject lines(final ThreadContext context) {
         return enumeratorize(context.getRuntime(), this, "each_line");
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
         return block.isGiven() ? each_char(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod(name = "chars", frame = true)
     public IRubyObject chars19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_char(context, block) : enumeratorize(context.getRuntime(), this, "chars");
     }
 
     /** 
      * <p>Invoke a block for each line.</p>
      */
     public RubyIO each_line(ThreadContext context, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         ByteList separator = getSeparatorForGets(runtime, args);
         
         for (IRubyObject line = getline(runtime, separator); !line.isNil(); 
         	line = getline(runtime, separator)) {
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
         return "RubyIO(" + openFile.getMode() + ", " + openFile.getMainStream().getDescriptor().getFileno() + ")";
     }
     
     /* class methods for IO */
     
     /** rb_io_s_foreach
     *
     */
     public static IRubyObject foreach(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         int count = args.length;
         IRubyObject filename = args[0].convertToString();
         runtime.checkSafeString(filename);
        
         ByteList separator = getSeparatorFromArgs(runtime, args, 1);
 
         RubyIO io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
         
         if (!io.isNil()) {
             try {
                 IRubyObject str = io.getline(runtime, separator);
                 while (!str.isNil()) {
                     block.yield(context, str);
                     str = io.getline(runtime, separator);
                 }
             } finally {
                 io.close();
             }
         }
        
         return runtime.getNil();
     }
     
     @JRubyMethod(name = "foreach", required = 1, optional = 1, frame = true, meta = true)
     public static IRubyObject foreach19(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         return block.isGiven() ? foreach(context, recv, args, block) : enumeratorize(context.getRuntime(), recv, "foreach", args);
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
             
             selector = Selector.open();
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
    
     @JRubyMethod(name = "read", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, Block unusedBlock) {
         RubyIO file = newFile(context, recv, path);
 
        try {
            return file.read(context);
        } finally {
            file.close();
        }
     }
    
     @JRubyMethod(name = "read", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length) {
         RubyIO file = newFile(context, recv, path);
        
         try {
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     @JRubyMethod(name = "read", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset) {
         RubyIO file = newFile(context, recv, path);
 
         if (!offset.isNil()) file.seek(context, offset);
 
         try {
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
 
         RubyIO file = newFile(context, recv, path);
 
         if (!offset.isNil()) file.seek(context, offset);
 
         try {
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
