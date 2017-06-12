diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index 3f5b8b1d28..962101d019 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1173,1855 +1173,1855 @@ public class RubyIO extends RubyObject {
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
         int pid = myOpenFile.getProcess().hashCode();
         
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
         if (args.length == 0) {
             args = new IRubyObject[] { context.getCurrentFrame().getLastLine() };
         }
 
         Ruby runtime = context.getRuntime();
         IRubyObject fs = runtime.getGlobalVariables().get("$,");
         IRubyObject rs = runtime.getGlobalVariables().get("$\\");
         
         for (int i = 0; i < args.length; i++) {
             if (i > 0 && !fs.isNil()) {
                 callMethod(context, "write", fs);
             }
             if (args[i].isNil()) {
                 callMethod(context, "write", runtime.newString("nil"));
             } else {
                 callMethod(context, "write", args[i]);
             }
         }
         if (!rs.isNil()) {
             callMethod(context, "write", rs);
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
 
         try {
             OpenFile myOpenFile = getOpenFileChecked();            
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
             // TODO: I didn't see where MRI has this check, but it seems to be the right place
             originalFile.checkClosed(runtime);
             
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
     
     /** Closes the IO.
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "closed?")
     public RubyBoolean closed_p(ThreadContext context) {
         return context.getRuntime().newBoolean(openFile.getMainStream() == null && openFile.getPipeStream() == null);
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
         
         // These would be used when we notify threads...if we notify threads
         interruptBlockingThreads();
         
         ChannelDescriptor main, pipe;
         if (openFile.getPipeStream() != null) {
             pipe = openFile.getPipeStream().getDescriptor();
         } else {
             if (openFile.getMainStream() == null) {
                 return runtime.getNil();
             }
             pipe = null;
         }
         
         main = openFile.getMainStream().getDescriptor();
         
         // cleanup, raising errors if any
         openFile.cleanup(runtime, true);
         
         // TODO: notify threads waiting on descriptors/IO? probably not...
         
         if (openFile.getProcess() != null) {
             try {
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
 
         if (!result.isNil()) context.getCurrentFrame().setLastLine(result);
 
         return result;
     }
 
     public boolean getBlocking() {
         return ((ChannelStream) openFile.getMainStream()).isBlocking();
     }
 
     @JRubyMethod(name = "fcntl", required = 2)
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
         if (arg.isNil() || arg == runtime.getFalse()) {
             nArg = 0;
         } else if (arg instanceof RubyFixnum) {
             nArg = RubyFixnum.fix2long(arg);
         } else if (arg == runtime.getTrue()) {
             nArg = 1;
         } else {
             throw runtime.newNotImplementedError("JRuby does not support string for second fcntl/ioctl argument yet");
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
                 throw runtime.newIOError(e.getMessage());
             }
         } else {
             throw runtime.newNotImplementedError("JRuby only supports F_SETFL for fcntl/ioctl currently");
         }
         
         return runtime.newFixnum(0);
     }
     
     private static final ByteList NIL_BYTELIST = ByteList.create("nil");
     private static final ByteList RECURSIVE_BYTELIST = ByteList.create("[...]");
 
     @JRubyMethod(name = "puts", rest = true)
     public IRubyObject puts(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
         
         if (args.length == 0) {
             write(context, separator.getByteList());
             return runtime.getNil();
         }
 
         for (int i = 0; i < args.length; i++) {
             ByteList line;
             
             if (args[i].isNil()) {
                 line = NIL_BYTELIST;
             } else if (runtime.isInspecting(args[i])) {
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
         return runtime.getNil();
     }
 
     protected void write(ThreadContext context, ByteList byteList) {
         callMethod(context, "write", RubyString.newStringShared(context.getRuntime(), byteList));
     }
 
     private IRubyObject inspectPuts(ThreadContext context, RubyArray array) {
         try {
             context.getRuntime().registerInspecting(array);
             return puts(context, array.toJavaArray());
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
     @JRubyMethod(name = "getc")
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
         Ruby runtime = context.getRuntime();
 
         openFile.checkClosed(runtime);
 
         if(!(openFile.getMainStream() instanceof ChannelStream)) {
             // cryptic for the uninitiated...
             throw runtime.newNotImplementedError("read_nonblock only works with Nio based handlers");
         }
         try {
             int maxLength = RubyNumeric.fix2int(args[0]);
             if (maxLength < 0) {
                 throw runtime.newArgumentError("negative length " + maxLength + " given");
             }
             ByteList buf = ((ChannelStream)openFile.getMainStream()).readnonblock(RubyNumeric.fix2int(args[0]));
             IRubyObject strbuf = RubyString.newString(runtime, buf == null ? new ByteList(ByteList.NULL_ARRAY) : buf);
             if(args.length > 1) {
                 args[1].callMethod(context, "<<", strbuf);
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
     
     @JRubyMethod(name = "readpartial", required = 1, optional = 1)
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
                 args[1].callMethod(context, "<<", strbuf);
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
 
-            return readAll(getRuntime().getNil());
+            return readAll(context.getRuntime().getNil());
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
     @JRubyMethod(name = "each_byte", frame = true)
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
 
     @JRubyMethod(name = "each_byte", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_byte19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     /** 
      * <p>Invoke a block for each line.</p>
      */
     @JRubyMethod(name = {"each_line", "each"}, optional = 1, frame = true)
     public RubyIO each_line(ThreadContext context, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         ByteList separator = getSeparatorForGets(runtime, args);
         
         for (IRubyObject line = getline(runtime, separator); !line.isNil(); 
         	line = getline(runtime, separator)) {
             block.yield(context, line);
         }
         
         return this;
     }
 
     @JRubyMethod(name = "each", optional = 1, frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each19(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_line(context, args, block) : enumeratorize(context.getRuntime(), this, "each", args);
     }
 
     @JRubyMethod(name = "each_line", optional = 1, frame = true, compat = CompatVersion.RUBY1_9)
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
     @JRubyMethod(name = "foreach", required = 1, optional = 1, frame = true, meta = true)
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
     
     @JRubyMethod(name = "foreach", required = 1, optional = 1, frame = true, meta = true, compat = CompatVersion.RUBY1_9)
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
        try {
            Set pending = new HashSet();
            Set unselectable_reads = new HashSet();
            Set unselectable_writes = new HashSet();
            Selector selector = Selector.open();
            if (!args[0].isNil()) {
                // read
                checkArrayType(runtime, args[0]);
                for (Iterator i = ((RubyArray) args[0]).getList().iterator(); i.hasNext(); ) {
                    IRubyObject obj = (IRubyObject) i.next();
                    RubyIO ioObj = convertToIO(context, obj);
                    if (registerSelect(context, selector, obj, ioObj, SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) {
                        if (ioObj.writeDataBuffered()) {
                            pending.add(obj);
                        }
                    } else {
                        if (( ioObj.openFile.getMode() & OpenFile.READABLE ) != 0) {
                            unselectable_reads.add(obj);
                        }
                    }
                }
            }
 
            if (args.length > 1 && !args[1].isNil()) {
                // write
                checkArrayType(runtime, args[1]);
                for (Iterator i = ((RubyArray) args[1]).getList().iterator(); i.hasNext(); ) {
                    IRubyObject obj = (IRubyObject) i.next();
                    RubyIO ioObj = convertToIO(context, obj);
                    if (!registerSelect(context, selector, obj, ioObj, SelectionKey.OP_WRITE)) {
                        if (( ioObj.openFile.getMode() & OpenFile.WRITABLE ) != 0) {
                            unselectable_writes.add(obj);
                        }
                    }
                }
            }
 
            if (args.length > 2 && !args[2].isNil()) {
                checkArrayType(runtime, args[2]);
                // Java's select doesn't do anything about this, so we leave it be.
            }
 
            final boolean has_timeout = ( args.length > 3 && !args[3].isNil() );
            long timeout = 0;
            if(has_timeout) {
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
            
            if (pending.isEmpty() && unselectable_reads.isEmpty() && unselectable_writes.isEmpty()) {
                if (has_timeout) {
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
                try {
                    int interestAndReady = key.interestOps() & key.readyOps();
                    if ((interestAndReady
                            & (SelectionKey.OP_READ|SelectionKey.OP_ACCEPT|SelectionKey.OP_CONNECT)) != 0) {
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
            for (Iterator i = selector.keys().iterator(); i.hasNext(); ) {
                SelectionKey key = (SelectionKey) i.next();
                SelectableChannel channel = key.channel();
                synchronized(channel.blockingLock()) {
                    RubyIO originalIO = (RubyIO) TypeConverter.convertToType(
                            (IRubyObject) key.attachment(), runtime.getIO(), "to_io");
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
    
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 0: throw context.getRuntime().newArgumentError(0, 1);
         case 1: return read(context, recv, args[0], block);
         case 2: return read(context, recv, args[0], args[1], block);
         case 3: return read(context, recv, args[0], args[1], args[2], block);
         default: throw context.getRuntime().newArgumentError(args.length, 3);
         }
    }
    
     @JRubyMethod(name = "read", meta = true)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
        IRubyObject[] fileArguments = new IRubyObject[] {arg0};
        RubyIO file = (RubyIO) RubyKernel.open(context, recv, fileArguments, block);
        
        try {
            return file.read(context);
        } finally {
            file.close();
        }
    }
    
     @JRubyMethod(name = "read", meta = true)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
        IRubyObject[] fileArguments = new IRubyObject[] {arg0};
        RubyIO file = (RubyIO) RubyKernel.open(context, recv, fileArguments, block);
        
         try {
             if (!arg1.isNil()) {
                 return file.read(context, arg1);
             } else {
                 return file.read(context);
             }
         } finally  {
             file.close();
         }
    }
    
     @JRubyMethod(name = "read", meta = true)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         IRubyObject[] fileArguments = new IRubyObject[]{arg0};
         RubyIO file = (RubyIO) RubyKernel.open(context, recv, fileArguments, block);
 
         if (!arg2.isNil()) {
             file.seek(context, arg2);
         }
 
         try {
             if (!arg1.isNil()) {
                 return file.read(context, arg1);
             } else {
                 return file.read(context);
             }
         } finally  {
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
             return file.readlines(context, separatorArguments);
         } finally {
             file.close();
         }
     }
    
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true)
     public static IRubyObject popen(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         int mode;
 
         IRubyObject cmdObj = args[0].convertToString();
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
    
     @JRubyMethod(required = 1, rest = true, frame = true, meta = true)
     public static IRubyObject popen3(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
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
 
             RubyArray yieldArgs = RubyArray.newArrayLight(runtime, output, input, error);
             if (block.isGiven()) {
                 try {
                     return block.yield(context, yieldArgs);
                 } finally {
                     if (input.openFile.isOpen()) {
                         input.close();
                     }
                     if (output.openFile.isOpen()) {
                         output.close();
                     }
                     if (error.openFile.isOpen()) {
                         error.close();
                     }
                     runtime.getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor() * 256)));
                 }
             }
             return yieldArgs;
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     // NIO based pipe
     @JRubyMethod(name = "pipe", meta = true)
     public static IRubyObject pipe(ThreadContext context, IRubyObject recv) throws Exception {
         // TODO: This isn't an exact port of MRI's pipe behavior, so revisit
        Ruby runtime = context.getRuntime();
        Pipe pipe = Pipe.open();
        
        RubyIO source = new RubyIO(runtime, pipe.source());
        RubyIO sink = new RubyIO(runtime, pipe.sink());
        
        sink.openFile.getMainStream().setSync(true);
        return runtime.newArrayNoCopy(new IRubyObject[] { source, sink });
    }
     
     @JRubyMethod(name = "copy_stream", meta = true, compat = RUBY1_9)
     public static IRubyObject copy_stream(ThreadContext context, IRubyObject recv, 
             IRubyObject stream1, IRubyObject stream2) throws IOException {
         RubyIO io1 = (RubyIO)stream1;
         RubyIO io2 = (RubyIO)stream2;
 
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
 
         long size = f1.size();
 
         f1.transferTo(f2.position(), size, f2);
 
         return context.getRuntime().newFixnum(size);
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
 }
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index aded9328b3..9f9611fcaa 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -2504,1310 +2504,1298 @@ public class RubyString extends RubyObject implements EncodingCapable {
                 } else if (lastNull) {
                     result.append(substr(runtime, beg, enc.length(bytes, begin + beg, range)));
                     beg = start - begin;
                 } else {
                     if (start == range) {
                         start++;
                     } else {
                         start += enc.length(bytes, start, range);
                     }
                     lastNull = true;
                     continue;
                 }
             } else {
                 result.append(substr(runtime, beg, end - beg));
                 beg = matcher.getEnd();
                 start = begin + matcher.getEnd();
             }
             lastNull = false;
 
             if (captures) populateCapturesForSplit(runtime, result, matcher);
 
             if (limit && lim <= ++i) break;
         }
         return beg;
     }
 
     private void populateCapturesForSplit(Ruby runtime, RubyArray result, Matcher matcher) {
         Region region = matcher.getRegion();
         for (int i = 1; i < region.numRegs; i++) {
             if (region.beg[i] == -1) continue;
             if (region.beg[i] == region.end[i]) {
                 result.append(newEmptyString(runtime, getMetaClass()));
             } else {
                 result.append(substr(runtime , region.beg[i], region.end[i] - region.beg[i]));
             }
         }
     }
 
     private RubyArray awkSplit(boolean limit, int lim, int i) {
         Ruby runtime = getRuntime();
         RubyArray result = runtime.newArray();
 
         byte[]bytes = value.bytes;
         int p = value.begin; 
         int endp = p + value.realSize;
 
         boolean skip = true;
 
         int end, beg = 0;        
         for (end = beg = 0; p < endp; p++) {
             if (skip) {
                 if (ASCII.isSpace(bytes[p] & 0xff)) {
                     beg++;
                 } else {
                     end = beg + 1;
                     skip = false;
                     if (limit && lim <= i) break;
                 }
             } else {
                 if (ASCII.isSpace(bytes[p] & 0xff)) {
                     result.append(makeShared(runtime, beg, end - beg));
                     skip = true;
                     beg = end + 1;
                     if (limit) i++;
                 } else {
                     end++;
                 }
             }
         }
 
         if (value.realSize > 0 && (limit || value.realSize > beg || lim < 0)) {
             if (value.realSize == beg) {
                 result.append(newEmptyString(runtime, getMetaClass()));
             } else {
                 result.append(makeShared(runtime, beg, value.realSize - beg));
             }
         }
         return result;
     }
 
     /** get_pat
      * 
      */
     private final RubyRegexp getPattern(IRubyObject obj, boolean quote) {
         if (obj instanceof RubyRegexp) {
             return (RubyRegexp)obj;
         } else if (!(obj instanceof RubyString)) {
             IRubyObject val = obj.checkStringType();
             if (val.isNil()) throw getRuntime().newTypeError("wrong argument type " + obj.getMetaClass() + " (expected Regexp)");
             obj = val; 
         }
 
         return RubyRegexp.newRegexp(getRuntime(), ((RubyString)obj).value, 0, quote);
     }
 
     /** rb_str_scan
      *
      */
     @JRubyMethod(name = "scan", required = 1, frame = true, reads = BACKREF, writes = BACKREF, compat = CompatVersion.RUBY1_8)
     public IRubyObject scan(ThreadContext context, IRubyObject arg, Block block) {
         return scan(context, arg, context.getRuntime().getKCode().getEncoding(), block);
     }
     
     @JRubyMethod(name = "scan", required = 1, frame = true, reads = BACKREF, writes = BACKREF, compat = CompatVersion.RUBY1_9)
     public IRubyObject scan19(ThreadContext context, IRubyObject arg, Block block) {
         return scan(context, arg, value.encoding, block);
     }
     
     private IRubyObject scan(ThreadContext context, IRubyObject arg, Encoding enc, Block block) {
         final RubyRegexp rubyRegex = getPattern(arg, true);
         final Regex regex = rubyRegex.getPattern();
 
         int begin = value.begin;
         int range = begin + value.realSize;
         final Matcher matcher = regex.matcher(value.bytes, begin, range);
 
         if (block.isGiven()) {
             return scanIter(context, rubyRegex, matcher, enc, block, begin, range);
         } else {
             return scanNoIter(context, rubyRegex, matcher, enc, begin, range);
         }
     }
 
     private IRubyObject scanIter(ThreadContext context, RubyRegexp rubyRegex, Matcher matcher, Encoding enc, Block block, int begin, int range) {
         Ruby runtime = context.getRuntime();
         byte[]bytes = value.bytes;
         int size = value.realSize;
         RubyMatchData match = null;
         Frame frame = context.getPreviousFrame();
 
         int end = 0;
         if (rubyRegex.getPattern().numberOfCaptures() == 0) {
             while (matcher.search(begin + end, range, Option.NONE) >= 0) {
                 end = positionEnd(matcher, enc, begin, range);
                 match = rubyRegex.updateBackRef(context, this, frame, matcher);
                 match.use();
                 block.yield(context, substr(runtime, matcher.getBegin(), matcher.getEnd() - matcher.getBegin()).infectBy(rubyRegex));
                 modifyCheck(bytes, size);
             }
         } else {
             while (matcher.search(begin + end, range, Option.NONE) >= 0) {
                 end = positionEnd(matcher, enc, begin, range);
                 match = rubyRegex.updateBackRef(context, this, frame, matcher);
                 match.use();
                 block.yield(context, populateCapturesForScan(runtime, rubyRegex, matcher, range));
                 modifyCheck(bytes, size);
             }
         }
         frame.setBackRef(match == null ? context.getRuntime().getNil() : match);
         return this;
     }
 
     private IRubyObject scanNoIter(ThreadContext context, RubyRegexp rubyRegex, Matcher matcher, Encoding enc, int begin, int range) {
         Ruby runtime = context.getRuntime();
         RubyArray ary = runtime.newArray();
 
         int end = 0;
         if (rubyRegex.getPattern().numberOfCaptures() == 0) {
             while (matcher.search(begin + end, range, Option.NONE) >= 0) {
                 end = positionEnd(matcher, enc, begin, range);
                 ary.append(substr(runtime, matcher.getBegin(), matcher.getEnd() - matcher.getBegin()).infectBy(rubyRegex));
             }
         } else {
             while (matcher.search(begin + end, range, Option.NONE) >= 0) {
                 end = positionEnd(matcher, enc, begin, range);
                 ary.append(populateCapturesForScan(runtime, rubyRegex, matcher, range));
             }
         }
 
         Frame frame = context.getPreviousFrame();
         if (ary.size() > 0) {
             rubyRegex.updateBackRef(context, this, frame, matcher);
         } else {
             frame.setBackRef(runtime.getNil());
         }
         return ary;
     }
 
     private int positionEnd(Matcher matcher, Encoding enc, int begin, int range) {
         int end = matcher.getEnd();
         if (matcher.getBegin() == end) {
             if (value.realSize > end) {
                 return end + enc.length(value.bytes, begin + end, range);
             } else {
                 return end + 1;
             }
         } else {
             return end;
         }
     }
 
     private IRubyObject populateCapturesForScan(Ruby runtime, RubyRegexp regex, Matcher matcher, int range) {
         Region region = matcher.getRegion();
         RubyArray result = getRuntime().newArray(region.numRegs);
         for (int i=1; i<region.numRegs; i++) {
             int beg = region.beg[i]; 
             if (beg == -1) {
                 result.append(getRuntime().getNil());
             } else {
                 result.append(substr(runtime, beg, region.end[i] - beg).infectBy(regex));
             }
         }
         return result;
     }
 
     private static final ByteList SPACE_BYTELIST = new ByteList(ByteList.plain(" "));
     
     private final IRubyObject justify(IRubyObject arg0, char jflag) {
         Ruby runtime = getRuntime();
         
         int width = RubyFixnum.num2int(arg0);
         
         int f, flen = 0;
         byte[]fbuf;
         
         IRubyObject pad;
 
         f = SPACE_BYTELIST.begin;
         flen = SPACE_BYTELIST.realSize;
         fbuf = SPACE_BYTELIST.bytes;
         pad = runtime.getNil();
         
         return justifyCommon(width, jflag, flen, fbuf, f, runtime, pad);
     }
     
     private final IRubyObject justify(IRubyObject arg0, IRubyObject arg1, char jflag) {
         Ruby runtime = getRuntime();
         
         int width = RubyFixnum.num2int(arg0);
         
         int f, flen = 0;
         byte[]fbuf;
         
         IRubyObject pad;
 
         pad = arg1.convertToString();
         ByteList fList = ((RubyString)pad).value;
         f = fList.begin;
         flen = fList.realSize;
 
         if (flen == 0) throw runtime.newArgumentError("zero width padding");
 
         fbuf = fList.bytes;
         
         return justifyCommon(width, jflag, flen, fbuf, f, runtime, pad);
     }
 
     private IRubyObject justifyCommon(int width, char jflag, int flen, byte[] fbuf, int f, Ruby runtime, IRubyObject pad) {
         if (width < 0 || value.realSize >= width) return strDup(runtime);
 
         ByteList res = new ByteList(width);
         res.realSize = width;
 
         int p = res.begin;
         int pend;
         byte[] pbuf = res.bytes;
 
         if (jflag != 'l') {
             int n = width - value.realSize;
             pend = p + ((jflag == 'r') ? n : n / 2);
             if (flen <= 1) {
                 while (p < pend) {
                     pbuf[p++] = fbuf[f];
                 }
             } else {
                 int q = f;
                 while (p + flen <= pend) {
                     System.arraycopy(fbuf, f, pbuf, p, flen);
                     p += flen;
                 }
                 while (p < pend) {
                     pbuf[p++] = fbuf[q++];
                 }
             }
         }
 
         System.arraycopy(value.bytes, value.begin, pbuf, p, value.realSize);
 
         if (jflag != 'r') {
             p += value.realSize;
             pend = res.begin + width;
             if (flen <= 1) {
                 while (p < pend) {
                     pbuf[p++] = fbuf[f];
                 }
             } else {
                 while (p + flen <= pend) {
                     System.arraycopy(fbuf, f, pbuf, p, flen);
                     p += flen;
                 }
                 while (p < pend) {
                     pbuf[p++] = fbuf[f++];
                 }
             }
         }
 
         RubyString resStr = new RubyString(runtime, getMetaClass(), res);
         resStr.infectBy(this);
         if (flen > 0) {
             resStr.infectBy(pad);
         }
         return resStr;
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated use the one or two argument versions.
      */
     public IRubyObject ljust(IRubyObject [] args) {
         switch (args.length) {
         case 1:
             return ljust(args[0]);
         case 2:
             return ljust(args[0], args[1]);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 1, 2);
             return null; // not reached
         }
     }
 
     /** rb_str_ljust
      *
      */
     @JRubyMethod
     public IRubyObject ljust(IRubyObject arg0) {
         return justify(arg0, 'l');
     }
 
     /** rb_str_ljust
      *
      */
     @JRubyMethod
     public IRubyObject ljust(IRubyObject arg0, IRubyObject arg1) {
         return justify(arg0, arg1, 'l');
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated use the one or two argument versions.
      */
     public IRubyObject rjust(IRubyObject [] args) {
         switch (args.length) {
         case 1:
             return rjust(args[0]);
         case 2:
             return rjust(args[0], args[1]);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 1, 2);
             return null; // not reached
         }
     }
 
     /** rb_str_rjust
      *
      */
     @JRubyMethod
     public IRubyObject rjust(IRubyObject arg0) {
         return justify(arg0, 'r');
     }
 
     /** rb_str_rjust
      *
      */
     @JRubyMethod
     public IRubyObject rjust(IRubyObject arg0, IRubyObject arg1) {
         return justify(arg0, arg1, 'r');
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated use the one or two argument versions.
      */
     public IRubyObject center(IRubyObject [] args) {
         switch (args.length) {
         case 1:
             return center(args[0]);
         case 2:
             return center(args[0], args[1]);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 1, 2);
             return null; // not reached
         }
     }
 
     /** rb_str_center
      *
      */
     @JRubyMethod
     public IRubyObject center(IRubyObject arg0) {
         return justify(arg0, 'c');
     }
 
     /** rb_str_center
      *
      */
     @JRubyMethod
     public IRubyObject center(IRubyObject arg0, IRubyObject arg1) {
         return justify(arg0, arg1, 'c');
     }
 
     @JRubyMethod(name = "chop")
     public IRubyObject chop(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.chop_bang();
         return str;
     }
 
     /** rb_str_chop_bang
      * 
      */
     @JRubyMethod(name = "chop!")
     public IRubyObject chop_bang() {
         int end = value.realSize - 1;
         if (end < 0) return getRuntime().getNil(); 
 
         if ((value.bytes[value.begin + end]) == '\n') {
             if (end > 0 && (value.bytes[value.begin + end - 1]) == '\r') end--;
         }
 
         view(0, end);
         return this;
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby
      * 
      * @param args
      * @return
      * @deprecated Use the zero or one argument versions.
      */
     public RubyString chomp(IRubyObject[] args) {
         switch (args.length) {
         case 0:
             return chomp();
         case 1:
             return chomp(args[0]);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 0, 1);
             return null; // not reached
         }
     }
 
     /** rb_str_chop
      * 
      */
     @JRubyMethod
     public RubyString chomp() {
         RubyString str = strDup(getRuntime());
         str.chomp_bang();
         return str;
     }
 
     /** rb_str_chop
      * 
      */
     @JRubyMethod
     public RubyString chomp(IRubyObject arg0) {
         RubyString str = strDup(getRuntime());
         str.chomp_bang(arg0);
         return str;
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the zero or one argument versions.
      */
     public IRubyObject chomp_bang(IRubyObject[] args) {
         switch (args.length) {
         case 0:
             return chomp_bang();
         case 1:
             return chomp_bang(args[0]);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 0, 1);
             return null; // not reached
         }
     }
 
     /**
      * rb_str_chomp_bang
      *
      * In the common case, removes CR and LF characters in various ways depending on the value of
      *   the optional args[0].
      * If args.length==0 removes one instance of CR, CRLF or LF from the end of the string.
      * If args.length>0 and args[0] is "\n" then same behaviour as args.length==0 .
      * If args.length>0 and args[0] is "" then removes trailing multiple LF or CRLF (but no CRs at
      *   all(!)).
      * @param args See method description.
      */
     @JRubyMethod(name = "chomp!")
     public IRubyObject chomp_bang() {
         IRubyObject rsObj;
 
         int len = value.length();
         if (len == 0) return getRuntime().getNil();
         byte[]buff = value.bytes;
 
         rsObj = getRuntime().getGlobalVariables().get("$/");
 
         if (rsObj == getRuntime().getGlobalVariables().getDefaultSeparator()) {
             int realSize = value.realSize;
             int begin = value.begin;
             if (buff[begin + len - 1] == (byte)'\n') {
                 realSize--;
                 if (realSize > 0 && buff[begin + realSize - 1] == (byte)'\r') realSize--;
                 view(0, realSize);
             } else if (buff[begin + len - 1] == (byte)'\r') {
                 realSize--;
                 view(0, realSize);
             } else {
                 modifyCheck();
                 return getRuntime().getNil();
             }
             return this;                
         }
         
         return chompBangCommon(rsObj);
     }
 
     /**
      * rb_str_chomp_bang
      *
      * In the common case, removes CR and LF characters in various ways depending on the value of
      *   the optional args[0].
      * If args.length==0 removes one instance of CR, CRLF or LF from the end of the string.
      * If args.length>0 and args[0] is "\n" then same behaviour as args.length==0 .
      * If args.length>0 and args[0] is "" then removes trailing multiple LF or CRLF (but no CRs at
      *   all(!)).
      * @param args See method description.
      */
     @JRubyMethod(name = "chomp!")
     public IRubyObject chomp_bang(IRubyObject arg0) {
         return chompBangCommon(arg0);
     }
 
     private IRubyObject chompBangCommon(IRubyObject rsObj) {
 
         if (rsObj.isNil()) {
             return getRuntime().getNil();
         }
         RubyString rs = rsObj.convertToString();
         int len = value.realSize;
         int begin = value.begin;
         if (len == 0) {
             return getRuntime().getNil();
         }
         byte[] buff = value.bytes;
         int rslen = rs.value.realSize;
 
         if (rslen == 0) {
             while (len > 0 && buff[begin + len - 1] == (byte) '\n') {
                 len--;
                 if (len > 0 && buff[begin + len - 1] == (byte) '\r') {
                     len--;
                 }
             }
             if (len < value.realSize) {
                 view(0, len);
                 return this;
             }
             return getRuntime().getNil();
         }
 
         if (rslen > len) {
             return getRuntime().getNil();
         }
         byte newline = rs.value.bytes[rslen - 1];
 
         if (rslen == 1 && newline == (byte) '\n') {
             buff = value.bytes;
             int realSize = value.realSize;
             if (buff[begin + len - 1] == (byte) '\n') {
                 realSize--;
                 if (realSize > 0 && buff[begin + realSize - 1] == (byte) '\r') {
                     realSize--;
                 }
                 view(0, realSize);
             } else if (buff[begin + len - 1] == (byte) '\r') {
                 realSize--;
                 view(0, realSize);
             } else {
                 modifyCheck();
                 return getRuntime().getNil();
             }
             return this;
         }
 
         if (buff[begin + len - 1] == newline && rslen <= 1 || value.endsWith(rs.value)) {
             view(0, value.realSize - rslen);
             return this;
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_str_lstrip
      * 
      */
     @JRubyMethod
     public IRubyObject lstrip(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.lstrip_bang();
         return str;
     }
 
     /** rb_str_lstrip_bang
      */
     @JRubyMethod(name = "lstrip!")
     public IRubyObject lstrip_bang() {
         if (value.realSize == 0) return getRuntime().getNil();
         
         int i=0;
         while (i < value.realSize && ASCII.isSpace(value.bytes[value.begin + i] & 0xff)) i++;
         
         if (i > 0) {
             view(i, value.realSize - i);
             return this;
         }
         
         return getRuntime().getNil();
     }
 
     /** rb_str_rstrip
      *  
      */
     @JRubyMethod
     public IRubyObject rstrip(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.rstrip_bang();
         return str;
     }
 
     /** rb_str_rstrip_bang
      */ 
     @JRubyMethod(name = "rstrip!")
     public IRubyObject rstrip_bang() {
         if (value.realSize == 0) return getRuntime().getNil();
         int i=value.realSize - 1;
 
         while (i >= 0 && value.bytes[value.begin+i] == 0) i--;
         while (i >= 0 && ASCII.isSpace(value.bytes[value.begin + i] & 0xff)) i--;
 
         if (i < value.realSize - 1) {
             view(0, i + 1);
             return this;
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_str_strip
      *
      */
     @JRubyMethod
     public IRubyObject strip(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.strip_bang();
         return str;
     }
 
     /** rb_str_strip_bang
      */
     @JRubyMethod(name = "strip!")
     public IRubyObject strip_bang() {
         IRubyObject l = lstrip_bang();
         IRubyObject r = rstrip_bang();
 
         if(l.isNil() && r.isNil()) {
             return l;
         }
         return this;
     }
 
     /** rb_str_count
      *
      */
     @JRubyMethod(name = "count", required = 1, rest = true)
     public IRubyObject count(IRubyObject[] args) {
         if (args.length < 1) throw getRuntime().newArgumentError("wrong number of arguments");
         if (value.realSize == 0) return getRuntime().newFixnum(0);
 
         boolean[]table = new boolean[TRANS_SIZE];
         boolean init = true;
         for (int i=0; i<args.length; i++) {
             RubyString s = args[i].convertToString();
             s.setup_table(table, init);
             init = false;
         }
 
         int s = value.begin;
         int send = s + value.realSize;
         byte[]buf = value.bytes;
         int i = 0;
 
         while (s < send) if (table[buf[s++] & 0xff]) i++;
 
         return getRuntime().newFixnum(i);
     }
 
     /** rb_str_delete
      *
      */
     @JRubyMethod(name = "delete", required = 1, rest = true)
     public IRubyObject delete(ThreadContext context, IRubyObject[] args) {
         RubyString str = strDup(context.getRuntime());
         str.delete_bang(args);
         return str;
     }
 
     /** rb_str_delete_bang
      *
      */
     @JRubyMethod(name = "delete!", required = 1, rest = true)
     public IRubyObject delete_bang(IRubyObject[] args) {
         if (args.length < 1) throw getRuntime().newArgumentError("wrong number of arguments");
         
         boolean[]squeeze = new boolean[TRANS_SIZE];
 
         boolean init = true;
         for (int i=0; i<args.length; i++) {
             RubyString s = args[i].convertToString();
             s.setup_table(squeeze, init);
             init = false;
         }
         
         modify();
         
         if (value.realSize == 0) return getRuntime().getNil();
         int s = value.begin;
         int t = s;
         int send = s + value.realSize;
         byte[]buf = value.bytes;
         boolean modify = false;
         
         while (s < send) {
             if (squeeze[buf[s] & 0xff]) {
                 modify = true;
             } else {
                 buf[t++] = buf[s];
             }
             s++;
         }
         value.realSize = t - value.begin;
         
         if (modify) return this;
         return getRuntime().getNil();
     }
 
     /** rb_str_squeeze
      *
      */
     @JRubyMethod(name = "squeeze", rest = true)
     public IRubyObject squeeze(ThreadContext context, IRubyObject[] args) {
         RubyString str = strDup(context.getRuntime());
         str.squeeze_bang(args);
         return str;
     }
 
     /** rb_str_squeeze_bang
      *
      */
     @JRubyMethod(name = "squeeze!", rest = true)
     public IRubyObject squeeze_bang(IRubyObject[] args) {
         if (value.realSize == 0) {
             modifyCheck();
             return getRuntime().getNil();
         }
 
         final boolean squeeze[] = new boolean[TRANS_SIZE];
 
         if (args.length == 0) {
             for (int i=0; i<TRANS_SIZE; i++) squeeze[i] = true;
         } else {
             boolean init = true;
             for (int i=0; i<args.length; i++) {
                 RubyString s = args[i].convertToString();
                 s.setup_table(squeeze, init);
                 init = false;
             }
         }
 
         modify();
 
         int s = value.begin;
         int t = s;
         int send = s + value.realSize;
         byte[]buf = value.bytes;
         int save = -1;
 
         while (s < send) {
             int c = buf[s++] & 0xff;
             if (c != save || !squeeze[c]) buf[t++] = (byte)(save = c);
         }
 
         if (t - value.begin != value.realSize) { // modified
             value.realSize = t - value.begin; 
             return this;
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_str_tr
      *
      */
     @JRubyMethod
     public IRubyObject tr(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.tr_trans(src, repl, false);
         return str;
     }
     
     /** rb_str_tr_bang
     *
     */
     @JRubyMethod(name = "tr!")
     public IRubyObject tr_bang(IRubyObject src, IRubyObject repl) {
         return tr_trans(src, repl, false);
     }    
     
     private static final class TR {
         int gen, now, max;
         int p, pend;
         byte[]buf;
     }
 
     private static final int TRANS_SIZE = 256;
     
     /** tr_setup_table
      * 
      */
     private final void setup_table(boolean[]table, boolean init) {
         final boolean[]buf = new boolean[TRANS_SIZE];
         final TR tr = new TR();
         int c;
         
         boolean cflag = false;
         
         tr.p = value.begin;
         tr.pend = value.begin + value.realSize;
         tr.buf = value.bytes;
         tr.gen = tr.now = tr.max = 0;
         
         if (value.realSize > 1 && value.bytes[value.begin] == '^') {
             cflag = true;
             tr.p++;
         }
         
         if (init) for (int i=0; i<TRANS_SIZE; i++) table[i] = true;
         
         for (int i=0; i<TRANS_SIZE; i++) buf[i] = cflag;
         while ((c = trnext(tr)) >= 0) buf[c & 0xff] = !cflag;
         for (int i=0; i<TRANS_SIZE; i++) table[i] = table[i] && buf[i];
     }
     
     /** tr_trans
     *
     */    
     private final IRubyObject tr_trans(IRubyObject src, IRubyObject repl, boolean sflag) {
         if (value.realSize == 0) return getRuntime().getNil();
         
         ByteList replList = repl.convertToString().value;
         
         if (replList.realSize == 0) return delete_bang(new IRubyObject[]{src});
 
         ByteList srcList = src.convertToString().value;
         
         final TR trsrc = new TR();
         final TR trrepl = new TR();
         
         boolean cflag = false;
         boolean modify = false;
         
         trsrc.p = srcList.begin;
         trsrc.pend = srcList.begin + srcList.realSize;
         trsrc.buf = srcList.bytes;
         if (srcList.realSize >= 2 && srcList.bytes[srcList.begin] == '^') {
             cflag = true;
             trsrc.p++;
         }       
         
         trrepl.p = replList.begin;
         trrepl.pend = replList.begin + replList.realSize;
         trrepl.buf = replList.bytes;
         
         trsrc.gen = trrepl.gen = 0;
         trsrc.now = trrepl.now = 0;
         trsrc.max = trrepl.max = 0;
         
         int c;
         final int[]trans = new int[TRANS_SIZE];
         if (cflag) {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = 1;
             while ((c = trnext(trsrc)) >= 0) trans[c & 0xff] = -1;
             while ((c = trnext(trrepl)) >= 0); 
             for (int i=0; i<TRANS_SIZE; i++) {
                 if (trans[i] >= 0) trans[i] = trrepl.now;
             }
         } else {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = -1;
             while ((c = trnext(trsrc)) >= 0) {
                 int r = trnext(trrepl);
                 if (r == -1) r = trrepl.now;
                 trans[c & 0xff] = r;
             }
         }
         
         modify();
         
         int s = value.begin;
         int send = s + value.realSize;
         byte sbuf[] = value.bytes;
         
         if (sflag) {
             int t = s;
             int c0, last = -1;
             while (s < send) {
                 c0 = sbuf[s++];
                 if ((c = trans[c0 & 0xff]) >= 0) {
                     if (last == c) continue;
                     last = c;
                     sbuf[t++] = (byte)(c & 0xff);
                     modify = true;
                 } else {
                     last = -1;
                     sbuf[t++] = (byte)c0;
                 }
             }
             
             if (value.realSize > (t - value.begin)) {
                 value.realSize = t - value.begin;
                 modify = true;
             }
         } else {
             while (s < send) {
                 if ((c = trans[sbuf[s] & 0xff]) >= 0) {
                     sbuf[s] = (byte)(c & 0xff);
                     modify = true;
                 }
                 s++;
             }
         }
         
         if (modify) return this;
         return getRuntime().getNil();
     }
 
     /** trnext
     *
     */    
     private final int trnext(TR t) {
         byte [] buf = t.buf;
         
         for (;;) {
             if (t.gen == 0) {
                 if (t.p == t.pend) return -1;
                 if (t.p < t.pend -1 && buf[t.p] == '\\') t.p++;
                 t.now = buf[t.p++];
                 if (t.p < t.pend - 1 && buf[t.p] == '-') {
                     t.p++;
                     if (t.p < t.pend) {
                         if (t.now > ((int)buf[t.p] & 0xFF)) {
                             t.p++;
                             continue;
                         }
                         t.gen = 1;
                         t.max = (int)buf[t.p++] & 0xFF;
                     }
                 }
                 return t.now & 0xff;
             } else if (++t.now < t.max) {
                 return t.now & 0xff;
             } else {
                 t.gen = 0;
                 return t.max & 0xff;
             }
         }
     }    
 
     /** rb_str_tr_s
      *
      */
     @JRubyMethod
     public IRubyObject tr_s(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.tr_trans(src, repl, true);
         return str;
     }
 
     /** rb_str_tr_s_bang
      *
      */
     @JRubyMethod(name = "tr_s!")
     public IRubyObject tr_s_bang(IRubyObject src, IRubyObject repl) {
         return tr_trans(src, repl, true);
     }
 
     /** rb_str_each_line
      *
      */
-    @JRubyMethod(name = {"each_line", "each"}, required = 0, optional = 1, frame = true)
-    public IRubyObject each_line(ThreadContext context, IRubyObject[] args, Block block) {
-        byte newline;
-        int p = value.begin;
-        int pend = p + value.realSize;
-        int s;
-        int ptr = p;
-        int len = value.realSize;
-        int rslen;
-        IRubyObject line;
-        
+    @JRubyMethod(name = {"each_line", "each"}, frame = true)
+    public IRubyObject each_line(ThreadContext context, Block block) {
+        return each_lineCommon(context, context.getRuntime().getGlobalVariables().get("$/"), block);
+    }
 
-        IRubyObject _rsep;
-        if (args.length == 0) {
-            _rsep = getRuntime().getGlobalVariables().get("$/");
-        } else {
-            _rsep = args[0];
-        }
+    @JRubyMethod(name = {"each_line", "each"}, frame = true)
+    public IRubyObject each_line(ThreadContext context, IRubyObject arg, Block block) {
+        return each_lineCommon(context, arg, block);
+    }
+
+    public IRubyObject each_lineCommon(ThreadContext context, IRubyObject sep, Block block) {        
+        Ruby runtime = context.getRuntime();
 
-        if(_rsep.isNil()) {
+        if (sep.isNil()) {
             block.yield(context, this);
             return this;
         }
-        
-        RubyString rsep = stringValue(_rsep);
+
+        RubyString rsep = sep.convertToString();
         ByteList rsepValue = rsep.value;
-        byte[] strBytes = value.bytes;
+        int rslen = rsepValue.realSize;
 
-        rslen = rsepValue.realSize;
-        
-        if(rslen == 0) {
+        final byte newline;
+        if (rslen == 0) {
             newline = '\n';
         } else {
-            newline = rsepValue.bytes[rsepValue.begin + rslen-1];
+            newline = rsepValue.bytes[rsepValue.begin + rslen - 1];
         }
 
-        s = p;
-        p+=rslen;
+        int p = value.begin;
+        int end = p + value.realSize;
+        int ptr = p;
+        int len = value.realSize;
 
-        for(; p < pend; p++) {
-            if(rslen == 0 && strBytes[p] == '\n') {
-                if(strBytes[++p] != '\n') {
-                    continue;
-                }
-                while(p < pend && strBytes[p] == '\n') {
-                    p++;
-                }
+        int s = p;
+        p += rslen;
+        byte[] strBytes = value.bytes;
+        for (; p < end; p++) {
+            if (rslen == 0 && strBytes[p] == '\n') {
+                if (strBytes[++p] != '\n') continue;
+                while(p < end && strBytes[p] == '\n') p++;
             }
-            if(ptr<p && strBytes[p-1] == newline &&
+            if (ptr < p && strBytes[p - 1] == newline &&
                (rslen <= 1 || 
-                ByteList.memcmp(rsepValue.bytes, rsepValue.begin, rslen, strBytes, p-rslen, rslen) == 0)) {
-                line = RubyString.newStringShared(getRuntime(), getMetaClass(), this.value.makeShared(s-ptr, p-s));
-                line.infectBy(this);
-                block.yield(context, line);
-                modifyCheck(strBytes,len);
+                ByteList.memcmp(rsepValue.bytes, rsepValue.begin, rslen, strBytes, p - rslen, rslen) == 0)) {
+                block.yield(context, makeShared(runtime, s - ptr, p - s).infectBy(this));
+                modifyCheck(strBytes, len);
                 s = p;
             }
         }
 
-        if(s != pend) {
-            if(p > pend) {
-                p = pend;
-            }
-            line = RubyString.newStringShared(getRuntime(), getMetaClass(), this.value.makeShared(s-ptr, p-s));
-            line.infectBy(this);
-            block.yield(context, line);
+        if (s != end) {
+            if (p > end) p = end;
+            block.yield(context, makeShared(runtime, s - ptr, p - s).infectBy(this));
         }
 
         return this;
     }
 
     /**
      * rb_str_each_byte
      */
     @JRubyMethod(name = "each_byte", frame = true, compat = CompatVersion.RUBY1_8)
     public RubyString each_byte(ThreadContext context, Block block) {
         Ruby runtime = getRuntime();
         // Check the length every iteration, since
         // the block can modify this string.
         for (int i = 0; i < value.length(); i++) {
             block.yield(context, runtime.newFixnum(value.get(i) & 0xFF));
         }
         return this;
     }
 
     @JRubyMethod(name = "each_byte", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_byte19(ThreadContext context, Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     /** rb_str_each_char
      * 
      */
     @JRubyMethod(name = "each_char", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_char(ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "each_char");
 
         byte bytes[] = value.bytes;
         int p = value.begin;
         int end = p + value.realSize;
         Encoding enc = value.encoding;
 
         while (p < end) {
             int n = StringSupport.length(enc, bytes, p, end);
             block.yield(context, substr(context.getRuntime(), p, n)); // TODO: 1.9 version of substr.
             p += n;
         }
         return this;
     }
 
     /** rb_str_each_codepoint
      * 
      */
     @JRubyMethod(name = "each_codepoint", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each_codepoint(ThreadContext context, Block block) {
         if (singleByteOptimizable()) return each_byte19(context, block);
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "each_codepoint");
 
         Ruby runtime = context.getRuntime();
         byte bytes[] = value.bytes;
         int p = value.begin;
         int end = p + value.realSize;
         Encoding enc = value.encoding;
 
         while (p < end) {
             int c = codePoint(runtime, enc, bytes, p, end);
             int n = codeLength(runtime, enc, c);
             block.yield(context, runtime.newFixnum(c));
             p += n;
         }
         return this;
     }
 
     /** rb_str_intern
      *
      */
     public RubySymbol intern() {
         String s = toString();
         if (s.length() == 0) {
             throw getRuntime().newArgumentError("interning empty string");
         }
         if (s.indexOf('\0') >= 0) {
             throw getRuntime().newArgumentError("symbol string may not contain '\\0'");
         }
         return getRuntime().newSymbol(s);
     }
 
     @JRubyMethod(name = {"to_sym", "intern"})
     public RubySymbol to_sym() {
         return intern();
     }
 
     @JRubyMethod(name = "sum", optional = 1)
     public RubyInteger sum(IRubyObject[] args) {
         if (args.length > 1) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 1)");
         }
         
         long bitSize = 16;
         if (args.length == 1) {
             long bitSizeArg = ((RubyInteger) args[0].convertToInteger()).getLongValue();
             if (bitSizeArg > 0) {
                 bitSize = bitSizeArg;
             }
         }
 
         long result = 0;
         for (int i = 0; i < value.length(); i++) {
             result += value.get(i) & 0xFF;
         }
         return getRuntime().newFixnum(bitSize == 0 ? result : result % (long) Math.pow(2, bitSize));
     }
     
     /** string_to_c
      * 
      */
     @JRubyMethod(name = "to_c", reads = BACKREF, writes = BACKREF, compat = CompatVersion.RUBY1_9)
     public IRubyObject to_c(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         Frame frame = context.getCurrentFrame();
         IRubyObject backref = frame.getBackRef();
         if (backref != null && backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
 
         IRubyObject s = RuntimeHelpers.invoke(
                 context, this, "gsub",
                 RubyRegexp.newRegexp(runtime, Numeric.ComplexPatterns.underscores_pat),
                 runtime.newString(new ByteList(new byte[]{'_'})));
 
         RubyArray a = RubyComplex.str_to_c_internal(context, s);
 
         frame.setBackRef(backref);
 
         if (!a.eltInternal(0).isNil()) {
             return a.eltInternal(0);
         } else {
             return RubyComplex.newComplexCanonicalize(context, RubyFixnum.zero(runtime));
         }
     }
 
     /** string_to_r
      * 
      */
     @JRubyMethod(name = "to_r", reads = BACKREF, writes = BACKREF, compat = CompatVersion.RUBY1_9)
     public IRubyObject to_r(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         Frame frame = context.getCurrentFrame();
         IRubyObject backref = frame.getBackRef();
         if (backref != null && backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
 
         IRubyObject s = RuntimeHelpers.invoke(
                 context, this, "gsub",
                 RubyRegexp.newRegexp(runtime, Numeric.ComplexPatterns.underscores_pat),
                 runtime.newString(new ByteList(new byte[]{'_'})));
 
         RubyArray a = RubyRational.str_to_r_internal(context, s);
 
         frame.setBackRef(backref);
 
         if (!a.eltInternal(0).isNil()) {
             return a.eltInternal(0);
         } else {
             return RubyRational.newRationalCanonicalize(context, RubyFixnum.zero(runtime));
         }
     }    
 
     public static RubyString unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyString result = newString(input.getRuntime(), input.unmarshalString());
         input.registerLinkTarget(result);
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#unpack
      */
     @JRubyMethod
     public RubyArray unpack(IRubyObject obj) {
         return Pack.unpack(getRuntime(), this.value, stringValue(obj).value);
     }
 
     public void empty() {
         value = ByteList.EMPTY_BYTELIST;
         shareLevel = SHARE_LEVEL_BYTELIST;
     }
 
     @JRubyMethod(name = "encoding", compat = CompatVersion.RUBY1_9)
     public IRubyObject encoding(ThreadContext context) {
         return context.getRuntime().getEncodingService().getEncoding(value.encoding);
     }
     
     @JRubyMethod(name = "force_encoding", compat = CompatVersion.RUBY1_9)
     public IRubyObject force_encoding(ThreadContext context, IRubyObject enc) {
         modify();
         associateEncoding(enc.convertToString().toEncoding(context.getRuntime()));
         return this;
     }
 
     @JRubyMethod(name = "valid_encoding?", compat = CompatVersion.RUBY1_9)
     public IRubyObject valid_encoding_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return scanForCodeRange() == CR_BROKEN ? runtime.getFalse() : runtime.getTrue();
     }
 
     @JRubyMethod(name = "ascii_only?", compat = CompatVersion.RUBY1_9)
     public IRubyObject ascii_only_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return scanForCodeRange() == CR_7BIT ? runtime.getFalse() : runtime.getTrue();
     }
 
     /**
      * Mutator for internal string representation.
      *
      * @param value The new java.lang.String this RubyString should encapsulate
      * @deprecated
      */
     public void setValue(CharSequence value) {
         view(ByteList.plain(value));
     }
 
     public void setValue(ByteList value) {
         view(value);
     }
 
     public CharSequence getValue() {
         return toString();
     }
 
     public byte[] getBytes() {
         return value.bytes();
     }
 
     public ByteList getByteList() {
         return value;
     }
 
     /** used by ar-jdbc
      * 
      */
     public String getUnicodeValue() {
         try {
             return new String(value.bytes, value.begin, value.realSize, "UTF-8");
         } catch (Exception e) {
             throw new RuntimeException("Something's seriously broken with encodings", e);
         }
     }
 
     @Override
     public IRubyObject to_java() {
         return MiniJava.javaToRuby(getRuntime(), new String(getBytes()));
     }
 }
