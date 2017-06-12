diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index db3a0c982a..008859cfae 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -970,1784 +970,1778 @@ public class RubyIO extends RubyObject {
         }
 
         // TODO: Obviously, we're not doing a non-blocking write here
         return write(obj);
     }
     
     /** io_write
      * 
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(IRubyObject obj) {
         getRuntime().secure(4);
         
         RubyString str = obj.asString();
 
         // TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
         
         if (str.getByteList().length() == 0) {
             return getRuntime().newFixnum(0);
         }
 
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(getRuntime());
 
             int written = fwrite(str.getByteList());
 
             if (written == -1) {
                 // TODO: sys fail
             }
 
             // if not sync, we switch to write buffered mode
             if (!myOpenFile.isSync()) {
                 myOpenFile.setWriteBuffered();
             }
 
             return getRuntime().newFixnum(written);
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         }
     }
 
     protected boolean waitWritable(ChannelDescriptor descriptor) throws IOException {
         Channel channel = descriptor.getChannel();
         if (channel == null || !(channel instanceof SelectableChannel)) {
             return false;
         }
        
         Selector selector = Selector.open();
 
         ((SelectableChannel) channel).configureBlocking(false);
         int real_ops = ((SelectableChannel) channel).validOps() & SelectionKey.OP_WRITE;
         SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
        
         if (key == null) {
             ((SelectableChannel) channel).register(selector, real_ops, descriptor);
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
     }
 
     protected boolean waitReadable(ChannelDescriptor descriptor) throws IOException {
         Channel channel = descriptor.getChannel();
         if (channel == null || !(channel instanceof SelectableChannel)) {
             return false;
         }
        
         Selector selector = Selector.open();
 
         ((SelectableChannel) channel).configureBlocking(false);
         int real_ops = ((SelectableChannel) channel).validOps() & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT);
         SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
        
         if (key == null) {
             ((SelectableChannel) channel).register(selector, real_ops, descriptor);
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
     public RubyFixnum fileno() {
         return getRuntime().newFixnum(getOpenFileChecked().getMainStream().getDescriptor().getFileno());
     }
     
     /** Returns the current line number.
      * 
      * @return the current line number.
      */
     @JRubyMethod(name = "lineno")
     public RubyFixnum lineno() {
         return getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Sets the current line number.
      * 
      * @param newLineNumber The new line number.
      */
     @JRubyMethod(name = "lineno=", required = 1)
     public RubyFixnum lineno_set(IRubyObject newLineNumber) {
         getOpenFileChecked().setLineNumber(RubyNumeric.fix2int(newLineNumber));
 
         return getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Returns the current sync mode.
      * 
      * @return the current sync mode.
      */
     @JRubyMethod(name = "sync")
     public RubyBoolean sync() {
         return getRuntime().newBoolean(getOpenFileChecked().getMainStream().isSync());
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
     public IRubyObject pid() {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (myOpenFile.getProcess() == null) {
             return getRuntime().getNil();
         }
         
         // Of course this isn't particularly useful.
         int pid = myOpenFile.getProcess().hashCode();
         
         return getRuntime().newFixnum(pid); 
     }
     
     /**
      * @deprecated
      * @return
      */
     public boolean writeDataBuffered() {
         return openFile.getMainStream().writeDataBuffered();
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos() {
         try {
             return getRuntime().newFixnum(getOpenFileChecked().getMainStream().fgetpos());
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException bde) {
             throw getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
     
     @JRubyMethod(name = "pos=", required = 1)
     public RubyFixnum pos_set(IRubyObject newPosition) {
         long offset = RubyNumeric.num2long(newPosition);
 
         if (offset < 0) {
             throw getRuntime().newSystemCallError("Negative seek offset");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.getMainStream().lseek(offset, Stream.SEEK_SET);
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return getRuntime().newFixnum(offset);
     }
     
     /** Print some objects to the stream.
      * 
      */
     @JRubyMethod(name = "print", rest = true, reads = FrameField.LASTLINE)
     public IRubyObject print(ThreadContext context, IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { context.getCurrentFrame().getLastLine() };
         }
 
         IRubyObject fs = getRuntime().getGlobalVariables().get("$,");
         IRubyObject rs = getRuntime().getGlobalVariables().get("$\\");
         
         for (int i = 0; i < args.length; i++) {
             if (i > 0 && !fs.isNil()) {
                 callMethod(context, "write", fs);
             }
             if (args[i].isNil()) {
                 callMethod(context, "write", getRuntime().newString("nil"));
             } else {
                 callMethod(context, "write", args[i]);
             }
         }
         if (!rs.isNil()) {
             callMethod(context, "write", rs);
         }
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "printf", required = 1, rest = true)
     public IRubyObject printf(ThreadContext context, IRubyObject[] args) {
         callMethod(context, "write", RubyKernel.sprintf(this, args));
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "putc", required = 1, backtrace = true)
     public IRubyObject putc(ThreadContext context, IRubyObject object) {
         int c = RubyNumeric.num2chr(object);
 
         try {
             getOpenFileChecked().getMainStream().fputc(c);
         } catch (BadDescriptorException e) {
             return RubyFixnum.zero(getRuntime());
         } catch (IOException e) {
             return RubyFixnum.zero(getRuntime());
         }
 
         return object;
     }
 
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "seek", required = 1, optional = 1)
     public RubyFixnum seek(IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.seek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return RubyFixnum.zero(getRuntime());
     }
     
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "sysseek", required = 1, optional = 1)
     public RubyFixnum sysseek(IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         long pos;
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             
             if (myOpenFile.isReadable() && myOpenFile.isReadBuffered()) {
                 throw getRuntime().newIOError("sysseek for buffered IO");
             }
             if (myOpenFile.isWritable() && myOpenFile.isWriteBuffered()) {
                 getRuntime().getWarnings().warn(
                         ID.SYSSEEK_BUFFERED_IO,
                         "sysseek for buffered IO");
             }
             
             pos = myOpenFile.getMainStream().getDescriptor().lseek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return getRuntime().newFixnum(pos);
     }
 
     @JRubyMethod(name = "rewind")
     public RubyFixnum rewind() {
         OpenFile myOpenfile = getOpenFileChecked();
         
         try {
             myOpenfile.getMainStream().lseek(0L, Stream.SEEK_SET);
             myOpenfile.getMainStream().clearerr();
             
             // TODO: This is some goofy global file value from MRI..what to do?
 //            if (io == current_file) {
 //                gets_lineno -= fptr->lineno;
 //            }
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
 
         // Must be back on first line on rewind.
         myOpenfile.setLineNumber(0);
         
         return RubyFixnum.zero(getRuntime());
     }
     
     @JRubyMethod(name = "fsync")
     public RubyFixnum fsync() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(getRuntime());
         
             myOpenFile.getWriteStream().sync();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
 
         return RubyFixnum.zero(getRuntime());
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
     public RubyBoolean eof_p() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
             
             if (myOpenFile.getMainStream().feof()) {
                 return getRuntime().getTrue();
             }
             
             if (myOpenFile.getMainStream().readDataBuffered()) {
                 return getRuntime().getFalse();
             }
             
             readCheck(myOpenFile.getMainStream());
             
             myOpenFile.getMainStream().clearerr();
             
             int c = myOpenFile.getMainStream().fgetc();
             
             if (c != -1) {
                 myOpenFile.getMainStream().ungetc(c);
                 return getRuntime().getFalse();
             }
             
             myOpenFile.checkClosed(getRuntime());
             
             myOpenFile.getMainStream().clearerr();
             
             return getRuntime().getTrue();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = {"tty?", "isatty"})
     public RubyBoolean tty_p() {
         return getRuntime().newBoolean(getRuntime().getPosix().isatty(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor()));
     }
     
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         if (this == original) return this;
 
         RubyIO originalIO = (RubyIO) TypeConverter.convertToTypeWithCheck(original, getRuntime().getIO(), MethodIndex.TO_IO, "to_io");
         
         OpenFile originalFile = originalIO.getOpenFileChecked();
         OpenFile newFile = openFile;
         
         try {
             // TODO: I didn't see where MRI has this check, but it seems to be the right place
             originalFile.checkClosed(getRuntime());
             
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
 
             newFile.setMainStream(ChannelStream.fdopen(getRuntime(), descriptor, modes));
             
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
             throw getRuntime().newIOError("could not init copy: " + ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newIOError("could not init copy: " + ex);
         } catch (PipeException ex) {
             throw getRuntime().newIOError("could not init copy: " + ex);
         } catch (InvalidValueException ex) {
             throw getRuntime().newIOError("could not init copy: " + ex);
         }
         
         return this;
     }
     
     /** Closes the IO.
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "closed?")
     public RubyBoolean closed_p() {
         if (openFile.getMainStream() == null && openFile.getPipeStream() == null) {
             return getRuntime().getTrue();
         } else {
             return getRuntime().getFalse();
         }
     }
 
     /** 
      * <p>Closes all open resources for the IO.  It also removes
      * it from our magical all open file descriptor pool.</p>
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't close");
         }
         
         openFile.checkClosed(getRuntime());
         return close2();
     }
         
     protected IRubyObject close2() {
         if (openFile == null) {
             return getRuntime().getNil();
         }
         
         // These would be used when we notify threads...if we notify threads
         ChannelDescriptor main, pipe;
         if (openFile.getPipeStream() != null) {
             pipe = openFile.getPipeStream().getDescriptor();
         } else {
             if (openFile.getMainStream() == null) {
                 return getRuntime().getNil();
             }
             pipe = null;
         }
         
         main = openFile.getMainStream().getDescriptor();
         
         // cleanup, raising errors if any
         openFile.cleanup(getRuntime(), true);
         
         // TODO: notify threads waiting on descriptors/IO? probably not...
         
         if (openFile.getProcess() != null) {
             try {
                 IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(getRuntime(), openFile.getProcess().waitFor());
                 getRuntime().getGlobalVariables().set("$?", processResult);
             } catch (InterruptedException ie) {
                 // TODO: do something here?
             }
         }
         
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "close_write")
     public IRubyObject close_write() throws BadDescriptorException {
         try {
             if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
                 throw getRuntime().newSecurityError("Insecure: can't close");
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isReadable()) {
                 throw getRuntime().newIOError("closing non-duplex IO for writing");
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
         } catch (IOException ioe) {
             // hmmmm
         }
         return this;
     }
 
     @JRubyMethod(name = "close_read")
     public IRubyObject close_read() throws BadDescriptorException {
         try {
             if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
                 throw getRuntime().newSecurityError("Insecure: can't close");
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isWritable()) {
                 throw getRuntime().newIOError("closing non-duplex IO for reading");
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
             throw getRuntime().newIOErrorFromException(ioe);
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
         ByteList separator = getSeparatorForGets(args);
         
         IRubyObject result = getline(separator);
 
         if (!result.isNil()) context.getCurrentFrame().setLastLine(result);
 
         return result;
     }
 
     public boolean getBlocking() {
         return ((ChannelStream) openFile.getMainStream()).isBlocking();
     }
 
     @JRubyMethod(name = "fcntl", required = 2)
     public IRubyObject fcntl(IRubyObject cmd, IRubyObject arg) {
         // TODO: This version differs from ioctl by checking whether fcntl exists
         // and raising notimplemented if it doesn't; perhaps no difference for us?
         return ctl(cmd, arg);
     }
 
     @JRubyMethod(name = "ioctl", required = 1, optional = 1)
     public IRubyObject ioctl(IRubyObject[] args) {
         IRubyObject cmd = args[0];
         IRubyObject arg;
         
         if (args.length == 2) {
             arg = args[1];
         } else {
             arg = getRuntime().getNil();
         }
         
         return ctl(cmd, arg);
     }
 
     public IRubyObject ctl(IRubyObject cmd, IRubyObject arg) {
         long realCmd = cmd.convertToInteger().getLongValue();
         long nArg = 0;
         
         // FIXME: Arg may also be true, false, and nil and still be valid.  Strangely enough, 
         // protocol conversion is not happening in Ruby on this arg?
         if (arg.isNil() || arg == getRuntime().getFalse()) {
             nArg = 0;
         } else if (arg instanceof RubyFixnum) {
             nArg = RubyFixnum.fix2long(arg);
         } else if (arg == getRuntime().getTrue()) {
             nArg = 1;
         } else {
             throw getRuntime().newNotImplementedError("JRuby does not support string for second fcntl/ioctl argument yet");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
 
         // Fixme: Only F_SETFL is current supported
         if (realCmd == 1L) {  // cmd is F_SETFL
             boolean block = true;
             
             if ((nArg & ModeFlags.NONBLOCK) == ModeFlags.NONBLOCK) {
                 block = false;
             }
 
             try {
                 myOpenFile.getMainStream().setBlocking(block);
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
         } else {
             throw getRuntime().newNotImplementedError("JRuby only supports F_SETFL for fcntl/ioctl currently");
         }
         
         return getRuntime().newFixnum(0);
     }
     
     private static final ByteList NIL_BYTELIST = ByteList.create("nil");
     private static final ByteList RECURSIVE_BYTELIST = ByteList.create("[...]");
 
     @JRubyMethod(name = "puts", rest = true)
     public IRubyObject puts(ThreadContext context, IRubyObject[] args) {
         assert getRuntime().getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString)getRuntime().getGlobalVariables().getDefaultSeparator();
         
         if (args.length == 0) {
             write(context, separator.getByteList());
             return getRuntime().getNil();
         }
 
         for (int i = 0; i < args.length; i++) {
             ByteList line;
             
             if (args[i].isNil()) {
                 line = NIL_BYTELIST;
             } else if (getRuntime().isInspecting(args[i])) {
                 line = RECURSIVE_BYTELIST;
             } else if (args[i] instanceof RubyArray) {
                 inspectPuts(context, (RubyArray) args[i]);
                 continue;
             } else {
                 line = args[i].asString().getByteList();
             }
             
             write(context, line);
             
             if (line.length() == 0 || !line.endsWith(separator.getByteList())) {
                 write(context, separator.getByteList());
             }
         }
         return getRuntime().getNil();
     }
 
     protected void write(ThreadContext context, ByteList byteList) {
         callMethod(context, "write", RubyString.newStringShared(getRuntime(), byteList));
     }
 
     private IRubyObject inspectPuts(ThreadContext context, RubyArray array) {
         try {
             getRuntime().registerInspecting(array);
             return puts(context, array.toJavaArray());
         } finally {
             getRuntime().unregisterInspecting(array);
         }
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "readline", optional = 1, writes = FrameField.LASTLINE)
     public IRubyObject readline(ThreadContext context, IRubyObject[] args) {
         IRubyObject line = gets(context, args);
 
         if (line.isNil()) {
             throw getRuntime().newEOFError();
         }
         
         return line;
     }
 
     /** Read a byte. On EOF returns nil.
      * 
      */
     @JRubyMethod(name = "getc")
     public IRubyObject getc() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
 
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
     
-    @JRubyMethod(name = "readpartial", required = 1, optional = 1)
+    @JRubyMethod(name = {"readpartial", "read_nonblock"}, required = 1, optional = 1)
     public IRubyObject readpartial(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         openFile.checkClosed(runtime);
 
         if(!(openFile.getMainStream() instanceof ChannelStream)) {
             // cryptic for the uninitiated...
             throw runtime.newNotImplementedError("readpartial only works with Nio based handlers");
         }
         try {
             int maxLength = RubyNumeric.fix2int(args[0]);
             if (maxLength < 0) {
                 throw runtime.newArgumentError("negative length " + maxLength + " given");
             }
             ByteList buf = ((ChannelStream)openFile.getMainStream()).readpartial(RubyNumeric.fix2int(args[0]));
             IRubyObject strbuf = RubyString.newString(runtime, buf == null ? new ByteList(ByteList.NULL_ARRAY) : buf);
             if(args.length > 1) {
                 args[1].callMethod(context, MethodIndex.OP_LSHIFT, "<<", strbuf);
                 return args[1];
             }
 
             return strbuf;
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (EOFException e) {
             return runtime.getNil();
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
                     return RubyString.newStringShared(getRuntime(), ByteList.EMPTY_BYTELIST);
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
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
             
             if (myOpenFile.getMainStream().readDataBuffered()) {
                 throw getRuntime().newIOError("sysread for buffered IO");
             }
             
             // TODO: Ruby locks the string here
             
             context.getThread().beforeBlockingCall();
             myOpenFile.checkClosed(getRuntime());
             
             // TODO: Ruby re-checks that the buffer string hasn't been modified
             
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
     
-    @JRubyMethod(name = "read_nonblock", required = 1, optional = 1)
-    public IRubyObject read_nonblock(IRubyObject[] args) {
-        // TODO: Obviously, we're not doing a nonblocking read here...
-        return read(args);
-    }
-    
     @JRubyMethod(name = "read", optional = 2)
     public IRubyObject read(IRubyObject[] args) {
         int argCount = args.length;
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (argCount == 0 || args[0].isNil()) {
             try {
                 myOpenFile.checkReadable(getRuntime());
 
                 if (args.length == 2) {
                     return readAll(args[1]);
                 } else {
                     return readAll(getRuntime().getNil());
                 }
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
         
         int length = RubyNumeric.num2int(args[0]);
         
         if (length < 0) {
             throw getRuntime().newArgumentError("negative length " + length + " given");
         }
         
         RubyString str = null;
 //        ByteList buffer = null;
         if (args.length == 1 || args[1].isNil()) {
 //            buffer = new ByteList(length);
 //            str = RubyString.newString(getRuntime(), buffer);
         } else {
             str = args[1].convertToString();
             str.modify(length);
 
             if (length == 0) {
                 return str;
             }
 
 //            buffer = str.getByteList();
         }
 
         try {
             myOpenFile.checkReadable(getRuntime());
 
             if (myOpenFile.getMainStream().feof()) {
                 return getRuntime().getNil();
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
                     return getRuntime().getNil();
                 }
 
                 if (myOpenFile.getMainStream().feof()) {
                     // truncate buffer string to zero, if provided
                     if (str != null) {
                         str.setValue(ByteList.EMPTY_BYTELIST.dup());
                     }
                 
                     return getRuntime().getNil();
                 }
 
                 if (length > 0) {
                     // I think this is only partly correct; sys fail based on errno in Ruby
                     throw getRuntime().newEOFError();
                 }
             }
 
 
             // TODO: Ruby truncates string to specific size here, but our bytelist should handle this already?
 
             // FIXME: I don't like the null checks here
             if (str == null) {
                 if (newBuffer == null) {
                     str = RubyString.newEmptyString(getRuntime());
                 } else {
                     str = RubyString.newString(getRuntime(), newBuffer);
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
             throw getRuntime().newEOFError();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
     
     protected IRubyObject readAll(IRubyObject buffer) throws BadDescriptorException, EOFException, IOException {
         // TODO: handle writing into original buffer better
         
         RubyString str = null;
         if (buffer instanceof RubyString) {
             str = (RubyString)buffer;
         }
         
         // TODO: ruby locks the string here
         
         // READ_CHECK from MRI io.c
         if (openFile.getMainStream().readDataBuffered()) {
             openFile.checkClosed(getRuntime());
         }
         
         ByteList newBuffer = openFile.getMainStream().readall();
 
         // TODO same zero-length checks as file above
 
         if (str == null) {
             if (newBuffer == null) {
                 str = RubyString.newEmptyString(getRuntime());
             } else {
                 str = RubyString.newString(getRuntime(), newBuffer);
             }
         } else {
             if (newBuffer == null) {
                 str.empty();
             } else {
                 str.setValue(newBuffer);
             }
         }
 
         str.taint();
 
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
     public IRubyObject stat() {
         openFile.checkClosed(getRuntime());
         return getRuntime().newFileStat(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor());
     }
 
     /** 
      * <p>Invoke a block for each byte.</p>
      */
     @JRubyMethod(name = "each_byte", frame = true)
     public IRubyObject each_byte(ThreadContext context, Block block) {
     	try {
             Ruby runtime = getRuntime();
             OpenFile myOpenFile = getOpenFileChecked();
             
             while (true) {
                 myOpenFile.checkReadable(runtime);
                 
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
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             return getRuntime().getNil();
     	} catch (IOException e) {
     	    throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     /** 
      * <p>Invoke a block for each line.</p>
      */
     @JRubyMethod(name = {"each_line", "each"}, optional = 1, frame = true)
     public RubyIO each_line(ThreadContext context, IRubyObject[] args, Block block) {
         ByteList separator = getSeparatorForGets(args);
         
         for (IRubyObject line = getline(separator); !line.isNil(); 
         	line = getline(separator)) {
             block.yield(context, line);
         }
         
         return this;
     }
 
 
     @JRubyMethod(name = "readlines", optional = 1)
     public RubyArray readlines(IRubyObject[] args) {
         ByteList separator;
         if (args.length > 0) {
             separator = getSeparatorForGets(new IRubyObject[] { args[0] });
         } else {
             separator = getSeparatorForGets(IRubyObject.NULL_ARRAY);
         }
 
         RubyArray result = getRuntime().newArray();
         IRubyObject line;
         while (! (line = getline(separator)).isNil()) {
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
     @JRubyMethod(name = "foreach", required = 1, optional = 1, frame = true, meta = true)
     public static IRubyObject foreach(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         int count = args.length;
         IRubyObject filename = args[0].convertToString();
         runtime.checkSafeString(filename);
        
         ByteList separator = getSeparatorFromArgs(runtime, args, 1);
 
         RubyIO io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
         
         if (!io.isNil()) {
             try {
                 IRubyObject str = io.getline(separator);
                 while (!str.isNil()) {
                     block.yield(context, str);
                     str = io.getline(separator);
                 }
             } finally {
                 io.close();
             }
         }
        
         return runtime.getNil();
     }
    
     private static RubyIO registerSelect(ThreadContext context, Selector selector, IRubyObject obj, int ops) throws IOException {
        RubyIO ioObj = (RubyIO) TypeConverter.convertToType(
                obj, context.getRuntime().getIO(),
                MethodIndex.TO_IO, "to_io");
 
        Channel channel = ioObj.getChannel();
        if (channel == null || !(channel instanceof SelectableChannel)) {
            return null;
        }
        
        ((SelectableChannel) channel).configureBlocking(false);
        int real_ops = ((SelectableChannel) channel).validOps() & ops;
        SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
        
        if (key == null) {
            ((SelectableChannel) channel).register(selector, real_ops, obj);
        } else {
            key.interestOps(key.interestOps()|real_ops);
        }
        
        return ioObj;
    }
    
     @JRubyMethod(name = "select", required = 1, optional = 3, meta = true)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return select_static(context, recv.getRuntime(), args);
     }
 
     private static void checkArrayType(Ruby runtime, IRubyObject obj) {
         if (!(obj instanceof RubyArray)) {
             throw runtime.newTypeError("wrong argument type "
                     + obj.getMetaClass().getName() + " (expected Array)");
         }
     }
 
     public static IRubyObject select_static(ThreadContext context, Ruby runtime, IRubyObject[] args) {
        try {
            // FIXME: This needs to be ported
            boolean atLeastOneDescriptor = false;
            
            Set pending = new HashSet();
            Selector selector = Selector.open();
            if (!args[0].isNil()) {
                atLeastOneDescriptor = true;
                
                // read
                checkArrayType(runtime, args[0]);
                for (Iterator i = ((RubyArray) args[0]).getList().iterator(); i.hasNext(); ) {
                    IRubyObject obj = (IRubyObject) i.next();
                    RubyIO ioObj = registerSelect(context, selector, obj, 
                            SelectionKey.OP_READ | SelectionKey.OP_ACCEPT);
                    
                    if (ioObj!=null && ioObj.writeDataBuffered()) pending.add(obj);
                }
            }
 
            if (args.length > 1 && !args[1].isNil()) {
                atLeastOneDescriptor = true;
                // write
                checkArrayType(runtime, args[1]);
                for (Iterator i = ((RubyArray) args[1]).getList().iterator(); i.hasNext(); ) {
                    IRubyObject obj = (IRubyObject) i.next();
                    registerSelect(context, selector, obj, SelectionKey.OP_WRITE);
                }
            }
 
            if (args.length > 2 && !args[2].isNil()) {
                atLeastOneDescriptor = true;
                checkArrayType(runtime, args[2]);
                // Java's select doesn't do anything about this, so we leave it be.
            }
 
            long timeout = 0;
            if(args.length > 3 && !args[3].isNil()) {
                IRubyObject timeArg = args[3];
                if (timeArg instanceof RubyFloat) {
                    timeout = Math.round(((RubyFloat) timeArg).getDoubleValue() * 1000);
                } else if (timeArg instanceof RubyFixnum) {
                    timeout = Math.round(((RubyFixnum) timeArg).getDoubleValue() * 1000);
                } else { // TODO: MRI also can hadle Bignum here
                    throw runtime.newTypeError("can't convert "
                            + timeArg.getMetaClass().getName() + " into time interval");
                }
 
                if (timeout < 0) {
                    throw runtime.newArgumentError("negative timeout given");
                }
            }
            
            if (!atLeastOneDescriptor) {
                return runtime.getNil();
            }
            
            if (pending.isEmpty()) {
                if (args.length > 3) {
                    if (timeout==0) {
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
            for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                SelectionKey key = (SelectionKey) i.next();
                if ((key.interestOps() & key.readyOps()
                        & (SelectionKey.OP_READ|SelectionKey.OP_ACCEPT|SelectionKey.OP_CONNECT)) != 0) {
                    r.add(key.attachment());
                    pending.remove(key.attachment());
                }
                if ((key.interestOps() & key.readyOps() & (SelectionKey.OP_WRITE)) != 0) {
                    w.add(key.attachment());
                }
            }
            r.addAll(pending);
            
            // make all sockets blocking as configured again
            for (Iterator i = selector.keys().iterator(); i.hasNext(); ) {
                SelectionKey key = (SelectionKey) i.next();
                SelectableChannel channel = key.channel();
                synchronized(channel.blockingLock()) {
                    RubyIO originalIO = (RubyIO) TypeConverter.convertToType(
                            (IRubyObject) key.attachment(), runtime.getIO(),
                            MethodIndex.TO_IO, "to_io");
                    boolean blocking = originalIO.getBlocking();
                    key.cancel();
                    channel.configureBlocking(blocking);
                }
            }
            selector.close();
            
            if (r.size() == 0 && w.size() == 0 && e.size() == 0) {
                return runtime.getNil();
            }
            
            List ret = new ArrayList();
            
            ret.add(RubyArray.newArray(runtime, r));
            ret.add(RubyArray.newArray(runtime, w));
            ret.add(RubyArray.newArray(runtime, e));
            
            return RubyArray.newArray(runtime, ret);
        } catch(IOException e) {
            throw runtime.newIOError(e.getMessage());
        }
    }
    
     @JRubyMethod(name = "read", required = 1, optional = 2, meta = true)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
        IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
        RubyIO file = (RubyIO) RubyKernel.open(context, recv, fileArguments, block);
        IRubyObject[] readArguments;
        
        if (args.length >= 2 && !args[1].isNil()) {
            readArguments = new IRubyObject[] {args[1].convertToInteger()};
        } else {
            readArguments = new IRubyObject[] {};
        }
        
        try {
            
            if (args.length == 3 && !args[2].isNil()) {
                file.seek(new IRubyObject[] {args[2].convertToInteger()});
            }
            
            return file.read(readArguments);
        } finally {
            file.close();
        }
    }
    
     @JRubyMethod(name = "readlines", required = 1, optional = 1, meta = true)
     public static RubyArray readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int count = args.length;
 
         IRubyObject[] fileArguments = new IRubyObject[]{ args[0].convertToString() };
         IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
         RubyIO file = (RubyIO) RubyKernel.open(context, recv, fileArguments, block);
         try {
             return file.readlines(separatorArguments);
         } finally {
             file.close();
         }
     }
    
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true)
     public static IRubyObject popen(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         int mode;
 
         IRubyObject cmdObj = args[0].convertToString();
         runtime.checkSafeString(cmdObj);
 
         if ("-".equals(cmdObj.toString())) {
             throw recv.getRuntime().newNotImplementedError(
                     "popen(\"-\") is unimplemented");
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
         
             Process process = ShellLauncher.popen(runtime, cmdObj, modes);
             RubyIO io = new RubyIO(runtime, process, modes);
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, io);
                 } finally {
                     if (io.openFile.isOpen()) {
                         io.close();
                     }
                     runtime.getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor() * 256)));
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
 
     // NIO based pipe
     @JRubyMethod(name = "pipe", meta = true)
     public static IRubyObject pipe(IRubyObject recv) throws Exception {
         // TODO: This isn't an exact port of MRI's pipe behavior, so revisit
        Ruby runtime = recv.getRuntime();
        Pipe pipe = Pipe.open();
        
        RubyIO source = new RubyIO(runtime, pipe.source());
        RubyIO sink = new RubyIO(runtime, pipe.sink());
        
        sink.openFile.getMainStream().setSync(true);
        return runtime.newArrayNoCopy(new IRubyObject[]{
            source,
            sink
        });
    }
     
     @JRubyMethod(name = "copy_stream", meta = true, compat = RUBY1_9)
     public static IRubyObject copy_stream(
             IRubyObject recv, IRubyObject stream1, IRubyObject stream2)
             throws IOException {
         RubyIO io1 = (RubyIO)stream1;
         RubyIO io2 = (RubyIO)stream2;
 
         ChannelDescriptor d1 = io1.openFile.getMainStream().getDescriptor();
         if (!d1.isSeekable()) {
             throw recv.getRuntime().newTypeError("only supports file-to-file copy");
         }
         ChannelDescriptor d2 = io2.openFile.getMainStream().getDescriptor();
         if (!d2.isSeekable()) {
             throw recv.getRuntime().newTypeError("only supports file-to-file copy");
         }
 
         FileChannel f1 = (FileChannel)d1.getChannel();
         FileChannel f2 = (FileChannel)d2.getChannel();
 
         long size = f1.size();
 
         f1.transferTo(f2.position(), size, f2);
 
         return recv.getRuntime().newFixnum(size);
     }
 }
\ No newline at end of file
