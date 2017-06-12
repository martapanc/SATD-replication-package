diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index e002ce6bd1..b66a25afbb 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -124,2001 +124,2001 @@ public class RubyIO extends RubyObject {
     }
 
     public RubyIO(Ruby runtime, OutputStream outputStream, boolean autoclose) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (outputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(outputStream)), autoclose));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.WRITABLE | OpenFile.APPEND);
     }
     
     public RubyIO(Ruby runtime, InputStream inputStream) {
         super(runtime, runtime.getIO());
         
         if (inputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(inputStream))));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.READABLE);
     }
     
     public RubyIO(Ruby runtime, Channel channel) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (channel == null) {
             throw runtime.newRuntimeError("Opening null channel");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(channel)));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
     }
 
     public RubyIO(Ruby runtime, ShellLauncher.POpenProcess process, IOOptions ioOptions) {
         this(runtime, runtime.getIO(), process, null, ioOptions);
     }
 
     public RubyIO(Ruby runtime, RubyClass cls, ShellLauncher.POpenProcess process, RubyHash options, IOOptions ioOptions) {
         super(runtime, cls);
 
         ioOptions = updateIOOptionsFromOptions(runtime.getCurrentContext(), (RubyHash) options, ioOptions);
         setEncodingFromOptions(ioOptions.getEncodingOption());
 
         openFile = new OpenFile();
         
         openFile.setMode(ioOptions.getModeFlags().getOpenFileFlags() | OpenFile.SYNC);
         openFile.setProcess(process);
 
         try {
             if (openFile.isReadable()) {
                 Channel inChannel;
                 if (process.getInput() != null) {
                     // NIO-based
                     inChannel = process.getInput();
                 } else {
                     // Stream-based
                     inChannel = Channels.newChannel(process.getInputStream());
                 }
                 
                 ChannelDescriptor main = new ChannelDescriptor(
                         inChannel);
                 main.setCanBeSeekable(false);
                 
                 openFile.setMainStream(ChannelStream.open(getRuntime(), main));
             }
             
             if (openFile.isWritable() && process.hasOutput()) {
                 Channel outChannel;
                 if (process.getOutput() != null) {
                     // NIO-based
                     outChannel = process.getOutput();
                 } else {
                     outChannel = Channels.newChannel(process.getOutputStream());
                 }
 
                 ChannelDescriptor pipe = new ChannelDescriptor(
                         outChannel);
                 pipe.setCanBeSeekable(false);
                 
                 if (openFile.getMainStream() != null) {
                     openFile.setPipeStream(ChannelStream.open(getRuntime(), pipe));
                 } else {
                     openFile.setMainStream(ChannelStream.open(getRuntime(), pipe));
                 }
             }
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
     
     public RubyIO(Ruby runtime, STDIO stdio) {
         super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         ChannelDescriptor descriptor;
         Stream mainStream;
 
         switch (stdio) {
         case IN:
             // special constructor that accepts stream, not channel
             descriptor = new ChannelDescriptor(runtime.getIn(), newModeFlags(runtime, ModeFlags.RDONLY), FileDescriptor.in);
             runtime.putFilenoMap(0, descriptor.getFileno());
             mainStream = ChannelStream.open(runtime, descriptor);
             openFile.setMainStream(mainStream);
             break;
         case OUT:
             descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getOut()), newModeFlags(runtime, ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out);
             runtime.putFilenoMap(1, descriptor.getFileno());
             mainStream = ChannelStream.open(runtime, descriptor);
             openFile.setMainStream(mainStream);
             openFile.getMainStream().setSync(true);
             break;
         case ERR:
             descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getErr()), newModeFlags(runtime, ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.err);
             runtime.putFilenoMap(2, descriptor.getFileno());
             mainStream = ChannelStream.open(runtime, descriptor);
             openFile.setMainStream(mainStream);
             openFile.getMainStream().setSync(true);
             break;
         }
 
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         // never autoclose stdio streams
         openFile.setAutoclose(false);
     }
     
     public static RubyIO newIO(Ruby runtime, Channel channel) {
         return new RubyIO(runtime, channel);
     }
     
     public OpenFile getOpenFile() {
         return openFile;
     }
     
     protected OpenFile getOpenFileChecked() {
         openFile.checkClosed(getRuntime());
         return openFile;
     }
     
     private static ObjectAllocator IO_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIO(runtime, klass);
         }
     };
 
     /*
      * We use FILE versus IO to match T_FILE in MRI.
      */
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.FILE;
     }
 
     public static RubyClass createIOClass(Ruby runtime) {
         RubyClass ioClass = runtime.defineClass("IO", runtime.getObject(), IO_ALLOCATOR);
 
         ioClass.index = ClassIndex.IO;
         ioClass.setReifiedClass(RubyIO.class);
 
         ioClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyIO;
             }
         };
 
         ioClass.includeModule(runtime.getEnumerable());
         
         // TODO: Implement tty? and isatty.  We have no real capability to
         // determine this from java, but if we could set tty status, then
         // we could invoke jruby differently to allow stdin to return true
         // on this.  This would allow things like cgi.rb to work properly.
         
         ioClass.defineAnnotatedMethods(RubyIO.class);
 
         // Constants for seek
         ioClass.setConstant("SEEK_SET", runtime.newFixnum(Stream.SEEK_SET));
         ioClass.setConstant("SEEK_CUR", runtime.newFixnum(Stream.SEEK_CUR));
         ioClass.setConstant("SEEK_END", runtime.newFixnum(Stream.SEEK_END));
 
         if (runtime.is1_9()) {
             ioClass.defineModuleUnder("WaitReadable");
             ioClass.defineModuleUnder("WaitWritable");
         }
 
         return ioClass;
     }
 
     public OutputStream getOutStream() {
         try {
             return getOpenFileChecked().getMainStreamSafe().newOutputStream();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     public InputStream getInStream() {
         try {
             return getOpenFileChecked().getMainStreamSafe().newInputStream();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     public Channel getChannel() {
         try {
             return getOpenFileChecked().getMainStreamSafe().getChannel();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     @Deprecated
     public Stream getHandler() throws BadDescriptorException {
         return getOpenFileChecked().getMainStreamSafe();
     }
 
     protected void reopenPath(Ruby runtime, IRubyObject[] args) {
         if (runtime.is1_9() && !(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             args[0] = args[0].callMethod(runtime.getCurrentContext(), "to_path");
         }
         IRubyObject pathString = args[0].convertToString();
 
         // TODO: check safe, taint on incoming string
 
         try {
             IOOptions modes;
             if (args.length > 1) {
                 IRubyObject modeString = args[1].convertToString();
                 modes = newIOOptions(runtime, modeString.toString());
 
                 openFile.setMode(modes.getModeFlags().getOpenFileFlags());
             } else {
                 modes = newIOOptions(runtime, "r");
             }
 
             String path = pathString.toString();
 
             // Ruby code frequently uses a platform check to choose "NUL:" on windows
             // but since that check doesn't work well on JRuby, we help it out
 
             openFile.setPath(path);
 
             if (openFile.getMainStream() == null) {
                 try {
                     openFile.setMainStream(ChannelStream.fopen(runtime, path, modes.getModeFlags()));
                 } catch (FileExistsException fee) {
                     throw runtime.newErrnoEEXISTError(path);
                 }
 
                 if (openFile.getPipeStream() != null) {
                     openFile.getPipeStream().fclose();
                     openFile.setPipeStream(null);
                 }
             } else {
                 // TODO: This is an freopen in MRI, this is close, but not quite the same
                 openFile.getMainStreamSafe().freopen(runtime, path, newIOOptions(runtime, openFile.getModeAsString(runtime)).getModeFlags());
                 
                 if (openFile.getPipeStream() != null) {
                     // TODO: pipe handler to be reopened with path and "w" mode
                 }
             }
         } catch (PipeException pe) {
             throw runtime.newErrnoEPIPEError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     protected void reopenIO(Ruby runtime, RubyIO ios) {
         try {
             if (ios.openFile == this.openFile) return;
 
             OpenFile origFile = ios.getOpenFileChecked();
             OpenFile selfFile = getOpenFileChecked();
 
             long pos = 0;
             Stream origStream = origFile.getMainStreamSafe();
             ChannelDescriptor origDescriptor = origStream.getDescriptor();
             boolean origIsSeekable = origDescriptor.isSeekable();
 
             if (origFile.isReadable() && origIsSeekable) {
                 pos = origFile.getMainStreamSafe().fgetpos();
             }
 
             if (origFile.getPipeStream() != null) {
                 origFile.getPipeStream().fflush();
             } else if (origFile.isWritable()) {
                 origStream.fflush();
             }
 
             if (selfFile.isWritable()) {
                 selfFile.getWriteStreamSafe().fflush();
             }
 
             selfFile.setMode(origFile.getMode());
             selfFile.setProcess(origFile.getProcess());
             selfFile.setLineNumber(origFile.getLineNumber());
             selfFile.setPath(origFile.getPath());
             selfFile.setFinalizer(origFile.getFinalizer());
 
             Stream selfStream = selfFile.getMainStreamSafe();
             ChannelDescriptor selfDescriptor = selfFile.getMainStreamSafe().getDescriptor();
             boolean selfIsSeekable = selfDescriptor.isSeekable();
 
             // confirm we're not reopening self's channel
             if (selfDescriptor.getChannel() != origDescriptor.getChannel()) {
                 // check if we're a stdio IO, and ensure we're not badly mutilated
                 if (runtime.getFileno(selfDescriptor) >= 0 && runtime.getFileno(selfDescriptor) <= 2) {
                     selfFile.getMainStreamSafe().clearerr();
 
                     // dup2 new fd into self to preserve fileno and references to it
                     origDescriptor.dup2Into(selfDescriptor);
                 } else {
                     Stream pipeFile = selfFile.getPipeStream();
                     int mode = selfFile.getMode();
                     selfFile.getMainStreamSafe().fclose();
                     selfFile.setPipeStream(null);
 
                     // TODO: turn off readable? am I reading this right?
                     // This only seems to be used while duping below, since modes gets
                     // reset to actual modes afterward
                     //fptr->mode &= (m & FMODE_READABLE) ? ~FMODE_READABLE : ~FMODE_WRITABLE;
 
                     if (pipeFile != null) {
                         selfFile.setMainStream(ChannelStream.fdopen(runtime, origDescriptor, origDescriptor.getOriginalModes()));
                         selfFile.setPipeStream(pipeFile);
                     } else {
                         // only use internal fileno here, stdio is handled above
                         selfFile.setMainStream(
                                 ChannelStream.open(
                                 runtime,
                                 origDescriptor.dup2(selfDescriptor.getFileno())));
 
                         // since we're not actually duping the incoming channel into our handler, we need to
                         // copy the original sync behavior from the other handler
                         selfFile.getMainStreamSafe().setSync(selfFile.getMainStreamSafe().isSync());
                     }
                     selfFile.setMode(mode);
                 }
 
                 // TODO: anything threads attached to original fd are notified of the close...
                 // see rb_thread_fd_close
 
                 if (origFile.isReadable() && pos >= 0) {
                     if (selfIsSeekable) {
                         selfFile.seek(pos, Stream.SEEK_SET);
                     }
 
                     if (origIsSeekable) {
                         origFile.seek(pos, Stream.SEEK_SET);
                     }
                 }
             }
 
             // only use internal fileno here, stdio is handled above
             if (selfFile.getPipeStream() != null && selfDescriptor.getFileno() != selfFile.getPipeStream().getDescriptor().getFileno()) {
                 int fd = selfFile.getPipeStream().getDescriptor().getFileno();
 
                 if (origFile.getPipeStream() == null) {
                     selfFile.getPipeStream().fclose();
                     selfFile.setPipeStream(null);
                 } else if (fd != origFile.getPipeStream().getDescriptor().getFileno()) {
                     selfFile.getPipeStream().fclose();
                     ChannelDescriptor newFD2 = origFile.getPipeStream().getDescriptor().dup2(fd);
                     selfFile.setPipeStream(ChannelStream.fdopen(runtime, newFD2, newIOOptions(runtime, "w").getModeFlags()));
                 }
             }
 
             // TODO: restore binary mode
             //            if (fptr->mode & FMODE_BINMODE) {
             //                rb_io_binmode(io);
             //            }
 
             // TODO: set our metaclass to target's class (i.e. scary!)
 
         } catch (IOException ex) { // TODO: better error handling
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (PipeException ex) {
             ex.printStackTrace();
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (InvalidValueException ive) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     @JRubyMethod(name = "reopen", required = 1, optional = 1)
     public IRubyObject reopen(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
     	IRubyObject tmp = TypeConverter.convertToTypeWithCheck(args[0], runtime.getIO(), "to_io");
         
     	if (!tmp.isNil()) {
             reopenIO(runtime, (RubyIO) tmp);
         } else {
             reopenPath(runtime, args);
         }
         
         return this;
     }
 
     @Deprecated
     public static ModeFlags getIOModes(Ruby runtime, String modesString) {
         return newModeFlags(runtime, modesString);
     }
 
     @Deprecated
     public static int getIOModesIntFromString(Ruby runtime, String modesString) {
         try {
             return ModeFlags.getOFlagsFromString(modesString);
         } catch (InvalidValueException ive) {
             throw runtime.newArgumentError("illegal access mode");
         }
     }
 
     /*
      * Ensure that separator is valid otherwise give it the default paragraph separator.
      */
     private ByteList separator(Ruby runtime) {
         return separator(runtime, runtime.getRecordSeparatorVar().get());
     }
 
     private ByteList separator(Ruby runtime, IRubyObject separatorValue) {
         ByteList separator = separatorValue.isNil() ? null :
             separatorValue.convertToString().getByteList();
 
         if (separator != null) {
             if (separator.getRealSize() == 0) return Stream.PARAGRAPH_DELIMETER;
 
             if (runtime.is1_9()) {
                 if (separator.getEncoding() != getReadEncoding(runtime)) {
                     separator = CharsetTranscoder.transcode(runtime.getCurrentContext(), separator,
                             getInputEncoding(runtime), getReadEncoding(runtime), runtime.getNil());
                 }
             }
         }
 
         return separator;
     }
 
     private ByteList getSeparatorFromArgs(Ruby runtime, IRubyObject[] args, int idx) {
         return separator(runtime, args.length > idx ? args[idx] : runtime.getRecordSeparatorVar().get());
     }
 
     private ByteList getSeparatorForGets(Ruby runtime, IRubyObject[] args) {
         return getSeparatorFromArgs(runtime, args, 0);
     }
 
     private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {
         return getline(runtime, separator, -1, cache);
     }
 
     public IRubyObject getline(Ruby runtime, ByteList separator) {
         return getline(runtime, separator, -1, null);
     }
 
 
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     public IRubyObject getline(Ruby runtime, ByteList separator, long limit) {
         return getline(runtime, separator, limit, null);
     }
 
     private IRubyObject getline(Ruby runtime, ByteList separator, long limit, ByteListCache cache) {
         return getlineInner(runtime, separator, limit, cache);
     }
     
     private IRubyObject getlineEmptyString(Ruby runtime) {
         if (runtime.is1_9()) return RubyString.newEmptyString(runtime, getReadEncoding(runtime));
 
         return RubyString.newEmptyString(runtime);
     }
     
     private IRubyObject getlineAll(Ruby runtime, OpenFile myOpenFile) throws IOException, BadDescriptorException {
         RubyString str = readAll();
 
         if (str.getByteList().length() == 0) return runtime.getNil();
         incrementLineno(runtime, myOpenFile);
         
         return str;
     }
     
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      * mri: rb_io_getline_1 (mostly)
      */
     private IRubyObject getlineInner(Ruby runtime, ByteList separator, long limit, ByteListCache cache) {
         try {
             boolean is19 = runtime.is1_9();
             
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             boolean isParagraph = separator == Stream.PARAGRAPH_DELIMETER;
             separator = isParagraph ? Stream.PARAGRAPH_SEPARATOR : separator;
             
             if (isParagraph) swallow('\n');
             
             if (separator == null && limit < 0) {
                 return getlineAll(runtime, myOpenFile);
             } else if (limit == 0) {
                 return getlineEmptyString(runtime);
             } else if (separator != null && separator.length() == 1 && limit < 0 && 
                     (!is19 || (!needsReadConversion() && getReadEncoding(runtime).isAsciiCompatible()))) {
                 return getlineFast(runtime, separator.get(0) & 0xFF, cache);
             } else {
                 Stream readStream = myOpenFile.getMainStreamSafe();
                 int c = -1;
                 int n = -1;
                 int newline = (separator != null) ? (separator.get(separator.length() - 1) & 0xFF) : -1;
                 
                 // FIXME: Change how we consume streams to match MRI (see append_line/more_char/fill_cbuf)
                 // Awful hack.  MRI pre-transcodes lines into read-ahead whereas
                 // we read a single line at a time PRE-transcoded.  To keep our
                 // logic we need to do one additional transcode of the sep to
                 // match the pre-transcoded encoding.  This is gross and we should
                 // mimick MRI.
                 if (is19 && separator != null && separator.getEncoding() != getInputEncoding(runtime)) {
                     separator = CharsetTranscoder.transcode(runtime.getCurrentContext(), separator, separator.getEncoding(), getInputEncoding(runtime), null);
                     newline = separator.get(separator.length() - 1) & 0xFF;
                 }
 
                 ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
                 try {
                     ThreadContext context = runtime.getCurrentContext();
                     boolean update = false;
                     boolean limitReached = false;
                     
                     if (is19) makeReadConversion(context);
                     
                     while (true) {
                         do {
                             readCheck(readStream);
                             readStream.clearerr();
 
                             try {
                                 runtime.getCurrentContext().getThread().beforeBlockingCall();
                                 if (limit == -1) {
                                     n = readStream.getline(buf, (byte) newline);
                                 } else {
                                     n = readStream.getline(buf, (byte) newline, limit);
                                     limit -= n;
                                     if (limit <= 0) {
                                         update = limitReached = true;
                                         break;
                                     }
                                 }
 
                                 c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                             } catch (EOFException e) {
                                 n = -1;
                             } finally {
                                 runtime.getCurrentContext().getThread().afterBlockingCall();
                             }
                             
                             // CRuby checks ferror(f) and retry getc for
                             // non-blocking IO.
                             if (n == 0) {
                                 waitReadable(readStream);
                                 continue;
                             } else if (n == -1) {
                                 break;
                             }
 
                             update = true;
                         } while (c != newline); // loop until we see the nth separator char
 
 
                         // if we hit EOF or reached limit then we're done
                         if (n == -1 || limitReached) {
                             break;
                         }
 
                         // if we've found the last char of the separator,
                         // and we've found at least as many characters as separator length,
                         // and the last n characters of our buffer match the separator, we're done
                         if (c == newline && separator != null && buf.length() >= separator.length() &&
                                 0 == ByteList.memcmp(buf.getUnsafeBytes(), buf.getBegin() + buf.getRealSize() - separator.length(), separator.getUnsafeBytes(), separator.getBegin(), separator.getRealSize())) {
                             break;
                         }
                     }
                     
                     if (is19) buf = readTranscoder.transcode(context, buf);
                     
                     if (isParagraph && c != -1) swallow('\n');
                     if (!update) return runtime.getNil();
 
                     incrementLineno(runtime, myOpenFile);
 
                     return makeString(runtime, buf, cache != null);
                 }
                 finally {
                     if (cache != null) cache.release(buf);
                 }
             }
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (EOFException e) {
             return runtime.getNil();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     // mri: io_read_encoding
     private Encoding getReadEncoding(Ruby runtime) {
         return readEncoding != null ? readEncoding : runtime.getDefaultExternalEncoding();
     }
     
     // mri: io_input_encoding
     private Encoding getInputEncoding(Ruby runtime) {
         return writeEncoding != null ? writeEncoding : getReadEncoding(runtime);
     }
 
     private RubyString makeString(Ruby runtime, ByteList buffer, boolean isCached) {
         ByteList newBuf = isCached ? new ByteList(buffer) : buffer;
 
         if (runtime.is1_9()) newBuf.setEncoding(getReadEncoding(runtime));
 
         RubyString str = RubyString.newString(runtime, newBuf);
         str.setTaint(true);
 
         return str;
     }
 
     private void incrementLineno(Ruby runtime, OpenFile myOpenFile) {
         int lineno = myOpenFile.getLineNumber() + 1;
         myOpenFile.setLineNumber(lineno);
         runtime.setCurrentLine(lineno);
         RubyArgsFile.setCurrentLineNumber(runtime.getArgsFile(), lineno);
     }
 
     protected boolean swallow(int term) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStreamSafe();
         int c;
         
         do {
             readCheck(readStream);
             
             try {
                 c = readStream.fgetc();
             } catch (EOFException e) {
                 c = -1;
             }
             
             if (c != term) {
                 readStream.ungetc(c);
                 return true;
             }
         } while (c != -1);
         
         return false;
     }
     
     private static String vendor;
     static { String v = SafePropertyAccessor.getProperty("java.vendor") ; vendor = (v == null) ? "" : v; };
     private static String msgEINTR = "Interrupted system call";
 
     public static boolean restartSystemCall(Exception e) {
         return vendor.startsWith("Apple") && e.getMessage().equals(msgEINTR);
     }
     
     private IRubyObject getlineFast(Ruby runtime, int delim, ByteListCache cache) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStreamSafe();
         int c = -1;
 
         ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
         try {
             boolean update = false;
             do {
                 readCheck(readStream);
                 readStream.clearerr();
                 int n;
                 try {
                     runtime.getCurrentContext().getThread().beforeBlockingCall();
                     n = readStream.getline(buf, (byte) delim);
                     c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                 } catch (EOFException e) {
                     n = -1;
                 } finally {
                     runtime.getCurrentContext().getThread().afterBlockingCall();
                 }
 
                 // CRuby checks ferror(f) and retry getc for non-blocking IO.
                 if (n == 0) {
                     waitReadable(readStream);
                     continue;
                 } else if (n == -1) {
                     break;
                 }
                 
                 update = true;
             } while (c != delim);
 
             if (!update) return runtime.getNil();
                 
             incrementLineno(runtime, openFile);
 
             return makeString(runtime, buf, cache != null);
         } finally {
             if (cache != null) cache.release(buf);
         }
     }
     // IO class methods.
 
     @JRubyMethod(name = {"new", "for_fd"}, rest = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass)recv;
         
         if (block.isGiven()) {
             String className = klass.getName();
             context.runtime.getWarnings().warn(
                     ID.BLOCK_NOT_ACCEPTED,
                     className + "::new() does not take block; use " + className + "::open() instead");
         }
         
         return klass.newInstance(context, args, block);
     }
 
     private IRubyObject initializeCommon19(ThreadContext context, int fileno, IRubyObject options, IOOptions ioOptions) {
         Ruby runtime = context.runtime;
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(runtime.getFilenoExtMap(fileno));
 
             if (descriptor == null) throw runtime.newErrnoEBADFError();
 
             descriptor.checkOpen();
 
             if (options != null && !(options instanceof RubyHash)) {
                 throw context.runtime.newTypeError(options, runtime.getHash());
             }
 
             if (ioOptions == null) {
                 ioOptions = newIOOptions(runtime, descriptor.getOriginalModes());
             }
 
             ioOptions = updateIOOptionsFromOptions(context, (RubyHash) options, ioOptions);
             setEncodingFromOptions(ioOptions.getEncodingOption());
 
             if (ioOptions == null) ioOptions = newIOOptions(runtime, descriptor.getOriginalModes());
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(runtime, false);
             }
 
             openFile.setMode(ioOptions.getModeFlags().getOpenFileFlags());
             openFile.setMainStream(fdopen(descriptor, ioOptions.getModeFlags()));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         }
 
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, Block unused) {
         return initializeCommon19(context, RubyNumeric.fix2int(fileNumber), null, null);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject second, Block unused) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         IOOptions ioOptions = null;
         RubyHash options = null;
         if (second instanceof RubyHash) {
             options = (RubyHash)second;
         } else {
             ioOptions = parseIOOptions19(second);
         }
 
         return initializeCommon19(context, fileno, options, ioOptions);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject modeValue, IRubyObject options, Block unused) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         IOOptions ioOptions = parseIOOptions19(modeValue);
 
         return initializeCommon19(context, fileno, options, ioOptions);
     }
 
     // No encoding processing
     protected IOOptions parseIOOptions(IRubyObject arg) {
         Ruby runtime = getRuntime();
 
         if (arg instanceof RubyFixnum) return newIOOptions(runtime, (int) RubyFixnum.fix2long(arg));
 
         return newIOOptions(runtime, newModeFlags(runtime, arg.convertToString().toString()));
     }
 
     // Encoding processing
     protected IOOptions parseIOOptions19(IRubyObject arg) {
         Ruby runtime = getRuntime();
 
         if (arg instanceof RubyFixnum) return newIOOptions(runtime, (int) RubyFixnum.fix2long(arg));
 
         String modeString = arg.convertToString().toString();
         try {
             return new IOOptions(runtime, modeString);
         } catch (InvalidValueException ive) {
             throw runtime.newArgumentError("invalid access mode " + modeString);
         }
     }
 
     @JRubyMethod(required = 1, optional = 1, visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         Ruby runtime = getRuntime();
         int argCount = args.length;
         IOOptions ioOptions;
         
         int fileno = RubyNumeric.fix2int(args[0]);
         
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(runtime.getFilenoExtMap(fileno));
             
             if (descriptor == null) {
                 throw runtime.newErrnoEBADFError();
             }
             
             descriptor.checkOpen();
             
             if (argCount == 2) {
                 if (args[1] instanceof RubyFixnum) {
                     ioOptions = newIOOptions(runtime, RubyFixnum.fix2long(args[1]));
                 } else {
                     ioOptions = newIOOptions(runtime, args[1].convertToString().toString());
                 }
             } else {
                 // use original modes
                 ioOptions = newIOOptions(runtime, descriptor.getOriginalModes());
             }
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(runtime, false);
             }
 
             openFile.setMode(ioOptions.getModeFlags().getOpenFileFlags());
         
             openFile.setMainStream(fdopen(descriptor, ioOptions.getModeFlags()));
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         }
         
         return this;
     }
     
     protected Stream fdopen(ChannelDescriptor existingDescriptor, ModeFlags modes) {
         Ruby runtime = getRuntime();
 
         // See if we already have this descriptor open.
         // If so then we can mostly share the handler (keep open
         // file, but possibly change the mode).
         
         if (existingDescriptor == null) {
             // redundant, done above as well
             
             // this seems unlikely to happen unless it's a totally bogus fileno
             // ...so do we even need to bother trying to create one?
             
             // IN FACT, we should probably raise an error, yes?
             throw runtime.newErrnoEBADFError();
             
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
             try {
                 return ChannelStream.fdopen(runtime, existingDescriptor, modes);
             } catch (InvalidValueException ive) {
                 throw runtime.newErrnoEINVALError();
             }
         }
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject external_encoding(ThreadContext context) {
         EncodingService encodingService = context.runtime.getEncodingService();
         
         if (writeEncoding != null) return encodingService.getEncoding(writeEncoding);
         
         if (openFile.isWritable()) {
             return readEncoding == null ? context.runtime.getNil() : encodingService.getEncoding(readEncoding);
         }
         
         return encodingService.getEncoding(getReadEncoding(context.runtime));
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject internal_encoding(ThreadContext context) {
         if (writeEncoding == null) return context.runtime.getNil();
         
         return context.runtime.getEncodingService().getEncoding(getReadEncoding(context.runtime));
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingObj) {
         setEncoding(context, encodingObj, context.runtime.getNil(), null);
 
         return context.runtime.getNil();
     }
     
     // mri: io_encoding_set
     private void setEncoding(ThreadContext context, IRubyObject external, IRubyObject internal, IRubyObject options) {        
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
-                    setupReadWriteEncodings(context, enc, null);
+                    setupReadWriteEncodings(context, null, enc);
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
 
         // rb_econv_binmode({read/write}_conv) stuff missing
 /*        Ruby runtime = getRuntime();
         if (getExternalEncoding(runtime) == USASCIIEncoding.INSTANCE) {
             externalEncoding = ASCIIEncoding.INSTANCE;
         }*/
         openFile.setBinmode();
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
 
         // If this is not a popen3/popen4 stream and it has a process, attempt to shut down that process
         if (!popenSpecial && openFile.getProcess() != null) {
             obliterateProcess(openFile.getProcess());
             IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(runtime, openFile.getProcess().exitValue(), openFile.getPid());
             runtime.getCurrentContext().setLastExitStatus(processResult);
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
diff --git a/test/externals/ruby1.9/excludes/TestIO_M17N.rb b/test/externals/ruby1.9/excludes/TestIO_M17N.rb
index cc3c6669f3..0f1f41dd68 100644
--- a/test/externals/ruby1.9/excludes/TestIO_M17N.rb
+++ b/test/externals/ruby1.9/excludes/TestIO_M17N.rb
@@ -1,59 +1,58 @@
 exclude :test_binmode, "needs investigation"
 exclude :test_binmode2, "needs investigation"
 exclude :test_binmode3, "needs investigation"
 exclude :test_both_textmode_binmode, "needs investigation"
 exclude :test_cbuf, "needs investigation"
 exclude :test_cr_decorator_on_stdout, "needs investigation"
 exclude :test_crlf_decorator_on_stdout, "needs investigation"
 exclude :test_dup, "needs investigation"
 exclude :test_dup_undef, "needs investigation"
 exclude :test_error_nonascii, "needs investigation"
 exclude :test_getc_invalid, "needs investigation"
 exclude :test_getc_invalid2, "needs investigation"
 exclude :test_getc_invalid3, "needs investigation"
 exclude :test_getc_newlineconv_invalid, "needs investigation"
 exclude :test_getc_stateful_conversion, "needs investigation"
 exclude :test_gets_invalid, "needs investigation"
 exclude :test_gets_limit, "needs investigation"
 exclude :test_inspect_nonascii, "needs investigation"
 exclude :test_invalid_r, "needs investigation"
 exclude :test_invalid_w, "needs investigation"
 exclude :test_lf_decorator_on_stdout, "needs investigation"
 exclude :test_nonascii_terminator, "needs investigation"
 exclude :test_open_pipe_r_enc2, "needs investigation"
 exclude :test_open_w, "needs investigation"
 exclude :test_pipe, "needs investigation"
 exclude :test_puts_widechar, "needs investigation"
 exclude :test_read_all_invalid, "needs investigation"
 exclude :test_read_mode, "needs investigation"
 exclude :test_read_newline_conversion_error, "needs investigation"
 exclude :test_read_newline_conversion_with_encoding_conversion, "needs investigation"
 exclude :test_s_pipe_invalid, "needs investigation"
 exclude :test_s_pipe_undef, "needs investigation"
 exclude :test_s_pipe_undef_replace_string, "needs investigation"
-exclude :test_set_encoding_enc, "needs investigation"
 exclude :test_set_encoding_undef_replace, "charset transcoding only supports single char replacements"
 exclude :test_stdin_external_encoding_with_reopen, "needs investigation"
 exclude :test_terminator_conversion2, "needs investigation"
 exclude :test_terminator_stateful_conversion, "needs investigation"
 exclude :test_text_mode, "needs investigation"
 exclude :test_text_mode_ungetc_eof, "needs investigation"
 exclude :test_textmode_decode_universal_newline_getc, "needs investigation"
 exclude :test_textmode_decode_universal_newline_gets, "needs investigation"
 exclude :test_textmode_decode_universal_newline_read, "needs investigation"
 exclude :test_textmode_decode_universal_newline_utf16, "needs investigation"
 exclude :test_textmode_paragraph_binaryread, "needs investigation"
 exclude :test_textmode_paragraphmode, "needs investigation"
 exclude :test_undef_r, "needs investigation"
 exclude :test_undef_w_stateful, "needs investigation"
 exclude :test_undef_w_stateless, "needs investigation"
 exclude :test_ungetc_int, "needs investigation"
 exclude :test_ungetc_stateful_conversion, "needs investigation"
 exclude :test_ungetc_stateful_conversion2, "needs investigation"
 exclude :test_ungetc_str, "needs investigation"
 exclude :test_w_xml_attr, "needs investigation"
 exclude :test_write_conversion_anyenc_stateful, "needs investigation"
 exclude :test_write_conversion_anyenc_stateful_nosync, "needs investigation"
 exclude :test_write_conversion_fixenc, "needs investigation"
 exclude :test_write_mode, "needs investigation"
 exclude :test_write_noenc, "our def enc makes this fail"
