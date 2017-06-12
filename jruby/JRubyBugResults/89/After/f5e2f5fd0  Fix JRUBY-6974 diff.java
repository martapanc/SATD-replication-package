diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index bdce4849b1..d2b6a9ce52 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -424,2010 +424,2010 @@ public class RubyIO extends RubyObject {
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
     
-    private boolean waitWritable(Stream stream) {
+    protected boolean waitWritable(Stream stream) {
         Channel ch = stream.getChannel();
         if (ch instanceof SelectableChannel) {
             getRuntime().getCurrentContext().getThread().select(ch, this, SelectionKey.OP_WRITE);
             return true;
         }
         return false;
     }
 
-    private boolean waitReadable(Stream stream) {
+    protected boolean waitReadable(Stream stream) {
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
 
         if (realCmd == Fcntl.F_GETFL.intValue()) {
             OpenFile myOpenFile = getOpenFileChecked();
             return runtime.newFixnum(myOpenFile.getMainStream().getModes().getFcntlFileFlags());
         }
         
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
diff --git a/src/org/jruby/ext/socket/RubyUDPSocket.java b/src/org/jruby/ext/socket/RubyUDPSocket.java
index b7a5a8a2b0..3da7807c43 100644
--- a/src/org/jruby/ext/socket/RubyUDPSocket.java
+++ b/src/org/jruby/ext/socket/RubyUDPSocket.java
@@ -1,536 +1,544 @@
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
 package org.jruby.ext.socket;
 
 import java.io.IOException;
 import java.net.ConnectException;
 import java.net.InetAddress;
 import java.net.InetSocketAddress;
 import java.net.PortUnreachableException;
 import java.net.SocketException;
 import java.net.MulticastSocket;
 import java.net.UnknownHostException;
 import java.net.DatagramPacket;
 import java.nio.ByteBuffer;
 import java.nio.channels.Channel;
 import java.nio.channels.DatagramChannel;
 import java.nio.channels.IllegalBlockingModeException;
 import java.nio.channels.NotYetConnectedException;
 
 import jnr.netdb.Service;
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.io.ChannelDescriptor;
 
 /**
  * @author <a href="mailto:pldms@mac.com">Damian Steer</a>
  */
 @JRubyClass(name="UDPSocket", parent="IPSocket")
 public class RubyUDPSocket extends RubyIPSocket {
 
     static void createUDPSocket(Ruby runtime) {
         RubyClass rb_cUDPSocket = runtime.defineClass("UDPSocket", runtime.getClass("IPSocket"), UDPSOCKET_ALLOCATOR);
         
         rb_cUDPSocket.includeModule(runtime.getClass("Socket").getConstant("Constants"));
 
         rb_cUDPSocket.defineAnnotatedMethods(RubyUDPSocket.class);
 
         runtime.getObject().setConstant("UDPsocket", rb_cUDPSocket);
     }
 
     private static ObjectAllocator UDPSOCKET_ALLOCATOR = new ObjectAllocator() {
 
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyUDPSocket(runtime, klass);
         }
     };
 
     public RubyUDPSocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     @JRubyMethod(visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context) {
         Ruby runtime = context.runtime;
 
         try {
             DatagramChannel channel = DatagramChannel.open();
             initSocket(runtime, new ChannelDescriptor(channel, newModeFlags(runtime, ModeFlags.RDWR)));
 
         } catch (ConnectException e) {
             throw runtime.newErrnoECONNREFUSEDError();
 
         } catch (UnknownHostException e) {
             throw SocketUtils.sockerr(runtime, "initialize: name or service not known");
 
         } catch (IOException e) {
             throw SocketUtils.sockerr(runtime, "initialize: name or service not known");
         }
 
         return this;
     }
 
     @JRubyMethod(visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject protocol) {
         // we basically ignore protocol. let someone report it...
         return initialize(context);
     }
 
     @JRubyMethod
     public IRubyObject bind(ThreadContext context, IRubyObject host, IRubyObject _port) {
         Ruby runtime = context.runtime;
         InetSocketAddress addr = null;
 
         try {
             Channel channel = getChannel();
             int port = SocketUtils.portToInt(_port);
 
             if (host.isNil()
                 || ((host instanceof RubyString)
                 && ((RubyString) host).isEmpty())) {
 
                 // host is nil or the empty string, bind to INADDR_ANY
                 addr = new InetSocketAddress(port);
 
             } else if (host instanceof RubyFixnum) {
 
                 // passing in something like INADDR_ANY
                 int intAddr = RubyNumeric.fix2int(host);
                 RubyModule socketMod = runtime.getModule("Socket");
                 if (intAddr == RubyNumeric.fix2int(socketMod.getConstant("INADDR_ANY"))) {
                     addr = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), port);
                 }
 
             } else {
                 // passing in something like INADDR_ANY
                 addr = new InetSocketAddress(InetAddress.getByName(host.convertToString().toString()), port);
             }
 
             if (multicastStateManager == null) {
                 ((DatagramChannel) channel).socket().bind(addr);
             } else {
                 multicastStateManager.rebindToPort(port);
             }
 
             return RubyFixnum.zero(runtime);
 
         } catch (UnknownHostException e) {
             throw SocketUtils.sockerr(runtime, "bind: name or service not known");
 
         } catch (SocketException e) {
             throw SocketUtils.sockerr(runtime, "bind: name or service not known");
 
         } catch (IOException e) {
             throw SocketUtils.sockerr(runtime, "bind: name or service not known");
 
         } catch (Error e) {
 
             // Workaround for a bug in Sun's JDK 1.5.x, see
             // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6303753
             if (e.getCause() instanceof SocketException) {
                 throw SocketUtils.sockerr(runtime, "bind: name or service not known");
             } else {
                 throw e;
             }
 
         }
     }
 
     @JRubyMethod
     public IRubyObject connect(ThreadContext context, IRubyObject host, IRubyObject port) {
         Ruby runtime = context.runtime;
 
         try {
             InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName(host.convertToString().toString()), SocketUtils.portToInt(port));
 
             ((DatagramChannel) this.getChannel()).connect(addr);
 
             return RubyFixnum.zero(runtime);
 
         } catch (UnknownHostException e) {
             throw SocketUtils.sockerr(runtime, "connect: name or service not known");
             
         } catch (IOException e) {
             throw SocketUtils.sockerr(runtime, "connect: name or service not known");
         }
     }
 
     @JRubyMethod
     public IRubyObject recvfrom_nonblock(ThreadContext context, IRubyObject _length) {
         Ruby runtime = context.runtime;
 
         try {
             int length = RubyNumeric.fix2int(_length);
 
             ReceiveTuple tuple = doReceiveNonblockTuple(runtime, length);
 
             IRubyObject addressArray = addrFor(context, tuple.sender, false);
 
             return runtime.newArray(tuple.result, addressArray);
 
         } catch (UnknownHostException e) {
             throw SocketUtils.sockerr(runtime, "recvfrom: name or service not known");
 
         } catch (PortUnreachableException e) {
             throw runtime.newErrnoECONNREFUSEDError();
 
         } catch (IOException e) {
             throw SocketUtils.sockerr(runtime, "recvfrom: name or service not known");
         }
     }
 
     @JRubyMethod
     public IRubyObject recvfrom_nonblock(ThreadContext context, IRubyObject _length, IRubyObject _flags) {
         // TODO: handle flags
         return recvfrom_nonblock(context, _length);
     }
 
     @JRubyMethod
     public IRubyObject send(ThreadContext context, IRubyObject _mesg, IRubyObject _flags) {
         // TODO: implement flags
         Ruby runtime = context.runtime;
 
         try {
             int written;
 
             RubyString data = _mesg.convertToString();
             ByteBuffer buf = ByteBuffer.wrap(data.getBytes());
 
+            waitWritable(openFile.getMainStream());
+
             written = ((DatagramChannel) this.getChannel()).write(buf);
 
             return runtime.newFixnum(written);
 
         } catch (NotYetConnectedException nyce) {
             throw runtime.newErrnoEDESTADDRREQError("send(2)");
 
         } catch (UnknownHostException e) {
             throw SocketUtils.sockerr(runtime, "send: name or service not known");
 
         } catch (IOException e) {
             throw SocketUtils.sockerr(runtime, "send: name or service not known");
         }
     }
 
     @JRubyMethod
     public IRubyObject send(ThreadContext context, IRubyObject _mesg, IRubyObject _flags, IRubyObject _to) {
         return send(context, _mesg, _flags);
     }
 
     @JRubyMethod(required = 2, optional = 2)
     public IRubyObject send(ThreadContext context, IRubyObject[] args) {
         // TODO: implement flags
         Ruby runtime = context.runtime;
         IRubyObject _mesg = args[0];
         IRubyObject _flags = args[1];
 
         try {
             int written;
 
             if (args.length == 2 || args.length == 3) {
                 return send(context, _mesg, _flags);
             }
             
             IRubyObject _host = args[2];
             IRubyObject _port = args[3];
 
             RubyString nameStr = _host.convertToString();
             RubyString data = _mesg.convertToString();
             ByteBuffer buf = ByteBuffer.wrap(data.getBytes());
 
             byte[] buf2 = data.getBytes();
             DatagramPacket sendDP = null;
 
             int port;
             if (_port instanceof RubyString) {
 
                 Service service = Service.getServiceByName(_port.asJavaString(), "udp");
 
                 if (service != null) {
                     port = service.getPort();
                 } else {
                     port = (int)_port.convertToInteger("to_i").getLongValue();
                 }
 
             } else {
                 port = (int)_port.convertToInteger().getLongValue();
             }
 
             InetAddress address = SocketUtils.getRubyInetAddress(nameStr.getByteList());
             InetSocketAddress addr = new InetSocketAddress(address, port);
 
+            waitWritable(openFile.getMainStream());
+
             if (this.multicastStateManager == null) {
                 written = ((DatagramChannel) this.getChannel()).send(buf, addr);
 
             } else {
                 sendDP = new DatagramPacket(buf2, buf2.length, address, port);
                 multicastStateManager.rebindToPort(port);
                 MulticastSocket ms = this.multicastStateManager.getMulticastSocket();
 
                 ms.send(sendDP);
                 written = sendDP.getLength();
             }
 
             return runtime.newFixnum(written);
 
         } catch (UnknownHostException e) {
             throw SocketUtils.sockerr(runtime, "send: name or service not known");
 
         } catch (IOException e) {
             throw SocketUtils.sockerr(runtime, "send: name or service not known");
         }
     }
 
     @JRubyMethod(rest = true, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyUDPSocket sock = (RubyUDPSocket) recv.callMethod(context, "new", args);
 
         if (!block.isGiven()) {
             return sock;
         }
 
         try {
             return block.yield(context, sock);
 
         } finally {
             if (sock.openFile.isOpen()) {
                 sock.close();
             }
         }
     }
 
     /**
      * Overrides IPSocket#recvfrom
      */
     @Override
     public IRubyObject recvfrom(ThreadContext context, IRubyObject _length) {
         Ruby runtime = context.runtime;
 
         try {
             int length = RubyNumeric.fix2int(_length);
 
             ReceiveTuple tuple = doReceiveTuple(runtime, length);
 
             IRubyObject addressArray = addrFor(context, tuple.sender, false);
 
             return runtime.newArray(tuple.result, addressArray);
 
         } catch (UnknownHostException e) {
             throw SocketUtils.sockerr(runtime, "recvfrom: name or service not known");
 
         } catch (PortUnreachableException e) {
             throw runtime.newErrnoECONNREFUSEDError();
 
         } catch (IOException e) {
             throw SocketUtils.sockerr(runtime, "recvfrom: name or service not known");
         }
     }
 
     /**
      * Overrides IPSocket#recvfrom
      */
     @Override
     public IRubyObject recvfrom(ThreadContext context, IRubyObject _length, IRubyObject _flags) {
         // TODO: handle flags
         return recvfrom(context, _length);
     }
 
     /**
      * Overrides BasicSocket#recv
      */
     @Override
     public IRubyObject recv(ThreadContext context, IRubyObject _length) {
         Ruby runtime = context.runtime;
 
         try {
             return doReceive(runtime, RubyNumeric.fix2int(_length));
 
         } catch (IOException e) {
             throw SocketUtils.sockerr(runtime, "recv: name or service not known");
 
         }
     }
 
     /**
      * Overrides BasicSocket#recv
      */
     @Override
     public IRubyObject recv(ThreadContext context, IRubyObject _length, IRubyObject _flags) {
         // TODO: implement flags
         return recv(context, _length);
     }
 
     private ReceiveTuple doReceiveTuple(Ruby runtime, int length) throws IOException {
         ReceiveTuple tuple = new ReceiveTuple();
 
         if (this.multicastStateManager == null) {
             doReceive(runtime, length, tuple);
         } else {
             doReceiveMulticast(runtime, length, tuple);
         }
 
         return tuple;
     }
 
     private ReceiveTuple doReceiveNonblockTuple(Ruby runtime, int length) throws IOException {
         DatagramChannel channel = (DatagramChannel)getChannel();
 
         synchronized (channel.blockingLock()) {
             boolean oldBlocking = channel.isBlocking();
 
             channel.configureBlocking(false);
 
             try {
                 return doReceiveTuple(runtime, length);
 
             } finally {
                 channel.configureBlocking(oldBlocking);
             }
         }
     }
 
     private static class ReceiveTuple {
         ReceiveTuple() {}
         ReceiveTuple(RubyString result, InetSocketAddress sender) {
             this.result = result;
             this.sender = sender;
         }
 
         RubyString result;
         InetSocketAddress sender;
     }
 
     private IRubyObject doReceive(Ruby runtime, int length) throws IOException {
         return doReceive(runtime, length, null);
     }
 
     private IRubyObject doReceive(Ruby runtime, int length, ReceiveTuple tuple) throws IOException {
         DatagramChannel channel = (DatagramChannel)getChannel();
 
         ByteBuffer buf = ByteBuffer.allocate(length);
 
+        waitReadable(openFile.getMainStream());
+
         InetSocketAddress sender = (InetSocketAddress)channel.receive(buf);
 
         if (sender == null) {
             // noblocking receive
             if (runtime.is1_9()) {
                 throw runtime.newErrnoEAGAINReadableError("recvfrom(2) would block");
             } else {
                 throw runtime.newErrnoEAGAINError("recvfrom(2) would block");
             }
         }
 
         // see JRUBY-4678
         if (sender == null) {
             throw runtime.newErrnoECONNRESETError();
         }
 
         RubyString result = runtime.newString(new ByteList(buf.array(), 0, buf.position()));
 
         if (tuple != null) {
             tuple.result = result;
             tuple.sender = sender;
         }
 
         return result;
     }
 
     private IRubyObject doReceiveMulticast(Ruby runtime, int length, ReceiveTuple tuple) throws IOException {
         byte[] buf2 = new byte[length];
         DatagramPacket recv = new DatagramPacket(buf2, buf2.length);
 
         MulticastSocket ms = this.multicastStateManager.getMulticastSocket();
 
+        waitReadable(openFile.getMainStream());
+
         try {
             ms.receive(recv);
         } catch (IllegalBlockingModeException ibme) {
             // MulticastSocket does not support nonblocking
             // TODO: Use Java 7 NIO.2 DatagramChannel to do multicast
             if (runtime.is1_9()) {
                 throw runtime.newErrnoEAGAINReadableError("multicast UDP does not support nonblocking");
             } else {
                 throw runtime.newErrnoEAGAINError("multicast UDP does not support nonblocking");
             }
         }
 
         InetSocketAddress sender = (InetSocketAddress) recv.getSocketAddress();
 
         // see JRUBY-4678
         if (sender == null) {
             throw runtime.newErrnoECONNRESETError();
         }
 
         RubyString result = runtime.newString(new ByteList(recv.getData(), 0, recv.getLength()));
 
         if (tuple != null) {
             tuple.result = result;
             tuple.sender = sender;
         }
 
         return result;
     }
 
     @Deprecated
     public IRubyObject bind(IRubyObject host, IRubyObject port) {
         return bind(getRuntime().getCurrentContext(), host, port);
     }
 
     @Deprecated
     public IRubyObject connect(IRubyObject host, IRubyObject port) {
         return connect(getRuntime().getCurrentContext(), host, port);
     }
 
     @Deprecated
     public IRubyObject recvfrom(IRubyObject[] args) {
         return recvfrom(getRuntime().getCurrentContext(), args);
     }
 
     @Deprecated
     public IRubyObject send(IRubyObject[] args) {
         return send(getRuntime().getCurrentContext(), args);
     }
 
     @Deprecated
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         return open(recv.getRuntime().getCurrentContext(), recv, args, block);
     }
 }// RubyUDPSocket
 
