diff --git a/core/src/main/java/org/jruby/RubyIO.java b/core/src/main/java/org/jruby/RubyIO.java
index a48009ed94..0e7b305e25 100644
--- a/core/src/main/java/org/jruby/RubyIO.java
+++ b/core/src/main/java/org/jruby/RubyIO.java
@@ -533,2358 +533,2358 @@ public class RubyIO extends RubyObject implements IOEncodable {
             if (fptr.readconv != null)
                 fptr.readconv.binmode();
             if (fptr.writeconv != null)
                 fptr.writeconv.binmode();
             fptr.setBinmode();
             fptr.clearTextMode();
             fptr.writeconvPreEcflags &= ~EConvFlags.NEWLINE_DECORATOR_MASK;
             if (OpenFlags.O_BINARY.defined()) {
                 // TODO: Windows
                 //            if (fptr.readconv == null) {
                 //                SET_BINARY_MODE_WITH_SEEK_CUR(fptr);
                 //            }
                 //            else {
                 // TODO: setmode O_BINARY means what via NIO?
                 //                setmode(fptr->fd, O_BINARY);
                 //            }
             }
         } finally {
             if (locked) fptr.unlock();
         }
         return;
     }
 
     // MRI: rb_io_reopen
     @JRubyMethod(name = "reopen", required = 1, optional = 1)
     public IRubyObject reopen(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         RubyIO file = this;
         IRubyObject fname = context.nil, nmode = context.nil, opt = context.nil;
         int[] oflags_p = {0};
         OpenFile fptr;
 
         switch (args.length) {
             case 3:
                 opt = TypeConverter.checkHashType(runtime, args[2]);
                 if (opt.isNil()) throw getRuntime().newArgumentError(3, 2);
             case 2:
                 if (opt.isNil()) {
                     opt = TypeConverter.checkHashType(runtime, args[1]);
                     if (opt.isNil()) {
                         nmode = args[1];
                         opt = context.nil;
                     }
                 } else {
                     nmode = args[1];
                 }
             case 1:
                 fname = args[0];
         }
         if (args.length == 1) {
             IRubyObject tmp = TypeConverter.ioCheckIO(runtime, fname);
             if (!tmp.isNil()) {
                 return file.reopenIO(context, (RubyIO)tmp);
             }
         }
 
         fname = StringSupport.checkEmbeddedNulls(runtime, RubyFile.get_path(context, fname));
         // Not implemented
 //        fname.checkTaint();
         fptr = file.openFile;
         if (fptr == null) {
             fptr = file.openFile = MakeOpenFile();
         }
 
         boolean locked = fptr.lock();
         try {
             if (!nmode.isNil() || !opt.isNil()) {
                 ConvConfig convconfig = new ConvConfig();
                 Object vmode_vperm = vmodeVperm(nmode, null);
                 int[] fmode_p = {0};
 
                 EncodingUtils.extractModeEncoding(context, convconfig, vmode_vperm, opt, oflags_p, fmode_p);
                 if (fptr.IS_PREP_STDIO() &&
                         ((fptr.getMode() & OpenFile.READWRITE) & (fmode_p[0] & OpenFile.READWRITE)) !=
                                 (fptr.getMode() & OpenFile.READWRITE)) {
                     throw runtime.newArgumentError(fptr.PREP_STDIO_NAME() + " can't change access mode from \"" + fptr.getModeAsString(runtime) + "\" to \"" + OpenFile.getStringFromMode(fmode_p[0]));
                 }
                 fptr.setMode(fmode_p[0]);
                 fptr.encs = convconfig;
             } else {
                 oflags_p[0] = OpenFile.getModeFlagsAsIntFrom(fptr.getMode());
             }
 
             fptr.setPath(fname.toString());
             if (fptr.fd() == null) {
                 fptr.setFD(sysopen(runtime, fptr.getPath(), oflags_p[0], 0666));
                 fptr.clearStdio();
                 return file;
             }
 
             if (fptr.isWritable()) {
                 if (fptr.io_fflush(context) < 0)
                     throw runtime.newErrnoFromErrno(fptr.errno(), fptr.getPath());
             }
             fptr.rbuf.off = fptr.rbuf.len = 0;
 
             if (fptr.isStdio()) {
                 // Logic here reopens the stdio FILE* with a new path and mode. For our purposes, we skip this
                 // since we do not want to damage the stdio streams
                 //            if (freopen(RSTRING_PTR(fptr.pathv), rb_io_oflags_modestr(oflags), fptr.stdio_file) == 0) {
                 //                rb_sys_fail_path(fptr.pathv);
                 //            }
                 fptr.setFD(sysopen(runtime, fptr.getPath(), oflags_p[0], 0666));
 
                 // This logic fixes the original stdio file descriptor by clearing any CLOEXEC that might have
                 // come across with the newly opened file. Since we do not yet support CLOEXEC, we skip this.
                 //            fptr.fd = fileno(fptr.stdio_file);
                 //            rb_fd_fix_cloexec(fptr.fd);
 
                 // This logic configures buffering (none, line, full) and buffer size to match the original stdio
                 // stream associated with this IO. I don't believe we can do this.
                 //                #ifdef USE_SETVBUF
                 //                if (setvbuf(fptr.stdio_file, NULL, _IOFBF, 0) != 0)
                 //                    rb_warn("setvbuf() can't be honoured for %"PRIsVALUE, fptr.pathv);
                 //                #endif
                 //                if (fptr.stdio_file == stderr) {
                 //                    if (setvbuf(fptr.stdio_file, NULL, _IONBF, BUFSIZ) != 0)
                 //                        rb_warn("setvbuf() can't be honoured for %"PRIsVALUE, fptr.pathv);
                 //                }
                 //                else if (fptr.stdio_file == stdout && isatty(fptr.fd)) {
                 //                    if (setvbuf(fptr.stdio_file, NULL, _IOLBF, BUFSIZ) != 0)
                 //                        rb_warn("setvbuf() can't be honoured for %"PRIsVALUE, fptr.pathv);
                 //                }
             } else {
                 ChannelFD tmpfd = sysopen(runtime, fptr.getPath(), oflags_p[0], 0666);
                 Errno err = null;
                 if (OpenFile.cloexecDup2(fptr.posix, tmpfd, fptr.fd()) < 0)
                     err = fptr.errno();
 
                 if (err != null) {
                     throw runtime.newErrnoFromErrno(err, fptr.getPath());
                 }
                 fptr.setFD(tmpfd);
             }
         } finally {
             if (locked) fptr.unlock();
         }
 
         return file;
     }
 
     public IRubyObject getline(ThreadContext context, IRubyObject separator) {
         return getline(context, separator, -1, null);
     }
 
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     public IRubyObject getline(ThreadContext context, IRubyObject separator, long limit) {
         return getline(context, separator, limit, null);
     }
 
     private IRubyObject getline(ThreadContext context, IRubyObject separator, long limit, ByteListCache cache) {
         return getlineInner(context, separator, (int)limit, cache);
     }
 
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      * mri: rb_io_getline_1 (mostly)
      */
     private IRubyObject getlineInner(ThreadContext context, IRubyObject rs, int _limit, ByteListCache cache) {
         Ruby runtime = context.runtime;
         IRubyObject str = context.nil;
         boolean noLimit = false;
         Encoding enc;
 
         OpenFile fptr = getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             fptr.checkCharReadable(context);
 
             if (rs.isNil() && _limit < 0) {
                 str = fptr.readAll(context, 0, context.nil);
                 if (((RubyString) str).size() == 0) return context.nil;
             } else if (_limit == 0) {
                 return RubyString.newEmptyString(runtime, fptr.readEncoding(runtime));
             } else if (
                     rs == runtime.getGlobalVariables().getDefaultSeparator()
                             && _limit < 0
                             && !fptr.needsReadConversion()
                             && (enc = fptr.readEncoding(runtime)).isAsciiCompatible()) {
                 fptr.NEED_NEWLINE_DECORATOR_ON_READ_CHECK();
                 return fptr.getlineFast(context, enc, this);
             }
 
             // slow path logic
             int c, newline = -1;
             byte[] rsptrBytes = null;
             int rsptr = 0;
             int rslen = 0;
             boolean rspara = false;
             int extraLimit = 16;
 
             fptr.SET_BINARY_MODE();
             enc = getReadEncoding();
 
             if (!rs.isNil()) {
                 RubyString rsStr = (RubyString) rs;
                 ByteList rsByteList = rsStr.getByteList();
                 rslen = rsByteList.getRealSize();
                 if (rslen == 0) {
                     rsptrBytes = PARAGRAPH_SEPARATOR.unsafeBytes();
                     rsptr = PARAGRAPH_SEPARATOR.getBegin();
                     rslen = 2;
                     rspara = true;
                     fptr.swallow(context, '\n');
                     if (!enc.isAsciiCompatible()) {
                         rs = RubyString.newUsAsciiStringShared(runtime, rsptrBytes, rsptr, rslen);
                         rs = EncodingUtils.rbStrEncode(context, rs, runtime.getEncodingService().convertEncodingToRubyEncoding(enc), 0, context.nil);
                         rs.setFrozen(true);
                         rsStr = (RubyString) rs;
                         rsByteList = rsStr.getByteList();
                         rsptrBytes = rsByteList.getUnsafeBytes();
                         rsptr = rsByteList.getBegin();
                         rslen = rsByteList.getRealSize();
                     }
                 } else {
                     rsptrBytes = rsByteList.unsafeBytes();
                     rsptr = rsByteList.getBegin();
                 }
                 newline = rsptrBytes[rsptr + rslen - 1] & 0xFF;
             }
 
             ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
             try {
                 boolean bufferString = str instanceof RubyString;
                 ByteList[] strPtr = { bufferString ? ((RubyString) str).getByteList() : null };
 
                 int[] limit_p = {_limit};
                 while ((c = fptr.appendline(context, newline, strPtr, limit_p)) != OpenFile.EOF) {
                     int s, p, pp, e;
 
                     if (c == newline) {
                         if (strPtr[0].getRealSize() < rslen) continue;
                         s = strPtr[0].getBegin();
                         e = s + strPtr[0].getRealSize();
                         p = e - rslen;
                         pp = enc.leftAdjustCharHead(strPtr[0].getUnsafeBytes(), s, p, e);
                         if (pp != p) continue;
                         if (ByteList.memcmp(strPtr[0].getUnsafeBytes(), p, rsptrBytes, rsptr, rslen) == 0) break;
                     }
                     if (limit_p[0] == 0) {
                         s = strPtr[0].getBegin();
                         p = s + strPtr[0].getRealSize();
                         pp = enc.leftAdjustCharHead(strPtr[0].getUnsafeBytes(), s, p - 1, p);
                         if (extraLimit != 0 &&
                                 StringSupport.MBCLEN_NEEDMORE_P(StringSupport.preciseLength(enc, strPtr[0].getUnsafeBytes(), pp, p))) {
                             limit_p[0] = 1;
                             extraLimit--;
                         } else {
                             noLimit = true;
                             break;
                         }
                     }
                 }
                 _limit = limit_p[0];
                 if (strPtr[0] != null) {
                     if (bufferString) {
                         if (strPtr[0] != ((RubyString) str).getByteList()) {
                             ((RubyString) str).setValue(strPtr[0]);
                         } else {
                             // same BL as before
                         }
                     } else {
                         // create string
                         str = runtime.newString(strPtr[0]);
                     }
                 }
 
                 if (rspara && c != OpenFile.EOF) {
                     // FIXME: This may block more often than it should, to clean up extraneous newlines
                     fptr.swallow(context, '\n');
                 }
                 if (!str.isNil()) {
                     str = EncodingUtils.ioEncStr(runtime, str, fptr);
                 }
             } finally {
                 if (cache != null) cache.release(buf);
             }
 
             if (!str.isNil() && !noLimit) {
                 fptr.incrementLineno(runtime);
             }
         } finally {
             if (locked) fptr.unlock();
         }
 
         return str;
     }
 
     // fptr->enc and codeconv->enc
     public Encoding getEnc() {
         return openFile.encs.enc;
     }
 
     // mri: io_read_encoding
     public Encoding getReadEncoding() {
         return openFile.readEncoding(getRuntime());
     }
 
     // fptr->enc2 and codeconv->enc2
     public Encoding getEnc2() {
         return openFile.encs.enc2;
     }
 
     // mri: io_input_encoding
     public Encoding getInputEncoding() {
         return openFile.inputEncoding(getRuntime());
     }
 
     private static final String VENDOR;
     static { String v = SafePropertyAccessor.getProperty("java.VENDOR") ; VENDOR = (v == null) ? "" : v; };
     private static final String msgEINTR = "Interrupted system call";
 
     // FIXME: We needed to use this to raise an appropriate error somewhere...find where...I think IRB related when suspending process?
     public static boolean restartSystemCall(Exception e) {
         return VENDOR.startsWith("Apple") && e.getMessage().equals(msgEINTR);
     }
 
     // IO class methods.
 
     @JRubyMethod(name = "new", rest = true, meta = true)
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
 
     @JRubyMethod(rest = true, meta = true)
     public static IRubyObject for_fd(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass)recv;
 
         return klass.newInstance(context, args, block);
     }
 
     private IRubyObject initializeCommon(ThreadContext context, int fileno, IRubyObject vmodeArg, IRubyObject opt) {
         Ruby runtime = context.runtime;
 
         ChannelFD fd;
 
         if (!FilenoUtil.isFake(fileno)) {
             // try using existing ChannelFD, then fall back on creating a new one
             fd = runtime.getFilenoUtil().getWrapperFromFileno(fileno);
 
             if (fd == null) {
                 fd = new ChannelFD(new NativeDeviceChannel(fileno), runtime.getPosix(), runtime.getFilenoUtil());
             }
         } else {
             ChannelFD descriptor = runtime.getFilenoUtil().getWrapperFromFileno(fileno);
 
             if (descriptor == null) throw runtime.newErrnoEBADFError();
 
             fd = descriptor;
         }
 
         if (!fd.ch.isOpen()) {
             throw runtime.newErrnoEBADFError();
         }
 
         return initializeCommon(context, fd, vmodeArg, opt);
     }
 
     private IRubyObject initializeCommon(ThreadContext context, ChannelFD fd, IRubyObject vmodeArg, IRubyObject opt) {
         Ruby runtime = context.runtime;
 
         int ofmode;
         int[] oflags_p = {ModeFlags.RDONLY};
 
         if(opt != null && !opt.isNil() && !(opt instanceof RubyHash) && !(opt.respondsTo("to_hash"))) {
             throw runtime.newArgumentError("last argument must be a hash!");
         }
 
         if (opt != null && !opt.isNil()) {
             opt = opt.convertToHash();
         }
 
         if (!fd.ch.isOpen()) {
             throw runtime.newErrnoEBADFError();
         }
 
         Object pm = EncodingUtils.vmodeVperm(vmodeArg, runtime.newFixnum(0));
         int[] fmode_p = {0};
         ConvConfig convconfig = new ConvConfig();
         EncodingUtils.extractModeEncoding(context, convconfig, pm, opt, oflags_p, fmode_p);
 
         { // normally done with fcntl...which we *could* do too...but this is just checking read/write
             oflags_p[0] = ModeFlags.oflagsFrom(runtime.getPosix(), fd.ch);
 
             ofmode = ModeFlags.getOpenFileFlagsFor(oflags_p[0]);
             if (EncodingUtils.vmode(pm) == null || EncodingUtils.vmode(pm).isNil()) {
                 fmode_p[0] = ofmode;
             } else if (((~ofmode & fmode_p[0]) & OpenFile.READWRITE) != 0) {
                 throw runtime.newErrnoEINVALError();
             }
         }
 
         if (opt != null && !opt.isNil() && ((RubyHash)opt).op_aref(context, runtime.newSymbol("autoclose")) == runtime.getFalse()) {
             fmode_p[0] |= OpenFile.PREP;
         }
 
         // JRUBY-4650: Make sure we clean up the old data, if it's present.
         MakeOpenFile();
 
         openFile.setFD(fd);
         openFile.setMode(fmode_p[0]);
         openFile.encs = convconfig;
         openFile.clearCodeConversion();
 
         openFile.checkTTY();
         switch (fd.bestFileno()) {
             case 0:
                 openFile.stdio_file = System.in;
                 break;
             case 1:
                 openFile.stdio_file = System.out;
                 break;
             case 2:
                 openFile.stdio_file = System.err;
                 break;
         }
 
         if (openFile.isBOM()) {
             EncodingUtils.ioSetEncodingByBOM(context, this);
         }
 
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject fileNumber, Block unused) {
         return initializeCommon(context, RubyNumeric.fix2int(fileNumber), null, context.nil);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject fileNumber, IRubyObject second, Block unused) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         IRubyObject vmode = null;
         IRubyObject options;
         IRubyObject hashTest = TypeConverter.checkHashType(context.runtime, second);
         if (hashTest instanceof RubyHash) {
             options = hashTest;
         } else {
             options = context.nil;
             vmode = second;
         }
 
         return initializeCommon(context, fileno, vmode, options);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject fileNumber, IRubyObject modeValue, IRubyObject options, Block unused) {
         int fileno = RubyNumeric.fix2int(fileNumber);
 
         return initializeCommon(context, fileno, modeValue, options);
     }
 
     // Encoding processing
     protected IOOptions parseIOOptions(IRubyObject arg) {
         Ruby runtime = getRuntime();
 
         if (arg instanceof RubyFixnum) return newIOOptions(runtime, (int) RubyFixnum.fix2long(arg));
 
         String modeString = arg.convertToString().toString();
         try {
             return new IOOptions(runtime, modeString);
         } catch (InvalidValueException ive) {
             throw runtime.newArgumentError("invalid access mode " + modeString);
         }
     }
 
     @JRubyMethod
     public IRubyObject external_encoding(ThreadContext context) {
         EncodingService encodingService = context.runtime.getEncodingService();
 
         if (openFile.encs.enc2 != null) return encodingService.getEncoding(openFile.encs.enc2);
 
         if (openFile.isWritable()) {
             return openFile.encs.enc == null ? context.runtime.getNil() : encodingService.getEncoding(openFile.encs.enc);
         }
 
         return encodingService.getEncoding(getReadEncoding());
     }
 
     @JRubyMethod
     public IRubyObject internal_encoding(ThreadContext context) {
         if (openFile.encs.enc2 == null) return context.nil;
 
         return context.runtime.getEncodingService().getEncoding(getReadEncoding());
     }
 
     @JRubyMethod
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingObj) {
         setEncoding(context, encodingObj, context.nil, context.nil);
 
         return context.nil;
     }
 
     @JRubyMethod
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding) {
         IRubyObject opt = TypeConverter.checkHashType(context.runtime, internalEncoding);
         if (!opt.isNil()) {
             setEncoding(context, encodingString, context.nil, opt);
         } else {
             setEncoding(context, encodingString, internalEncoding, context.nil);
         }
 
         return context.nil;
     }
 
     @JRubyMethod
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding, IRubyObject options) {
         setEncoding(context, encodingString, internalEncoding, options);
 
         return context.nil;
     }
 
     // mri: io_encoding_set
     public void setEncoding(ThreadContext context, IRubyObject v1, IRubyObject v2, IRubyObject opt) {
         IOEncodable.ConvConfig holder = new IOEncodable.ConvConfig();
         int ecflags = openFile.encs.ecflags;
         IRubyObject[] ecopts_p = {context.nil};
         IRubyObject tmp;
 
         if (!v2.isNil()) {
             holder.enc2 = EncodingUtils.rbToEncoding(context, v1);
             tmp = v2.checkStringType19();
 
             if (!tmp.isNil()) {
                 RubyString internalAsString = (RubyString)tmp;
 
                 // No encoding '-'
                 if (internalAsString.size() == 1 && internalAsString.asJavaString().equals("-")) {
                     /* Special case - "-" => no transcoding */
                     holder.enc = holder.enc2;
                     holder.enc2 = null;
                 } else {
                     holder.enc = EncodingUtils.rbToEncoding(context, internalAsString);
                 }
 
                 if (holder.enc == holder.enc2) {
                     /* Special case - "-" => no transcoding */
                     holder.enc2 = null;
                 }
             } else {
                 holder.enc = EncodingUtils.rbToEncoding(context, v2);
 
                 if (holder.enc == holder.enc2) {
                     /* Special case - "-" => no transcoding */
                     holder.enc2 = null;
                 }
             }
             EncodingUtils.SET_UNIVERSAL_NEWLINE_DECORATOR_IF_ENC2(holder.getEnc2(), ecflags);
             ecflags = EncodingUtils.econvPrepareOptions(context, opt, ecopts_p, ecflags);
         } else {
             if (v1.isNil()) {
                 EncodingUtils.ioExtIntToEncs(context, holder, null, null, 0);
                 EncodingUtils.SET_UNIVERSAL_NEWLINE_DECORATOR_IF_ENC2(holder.getEnc2(), ecflags);
                 ecopts_p[0] = context.nil;
             } else {
                 tmp = v1.checkStringType19();
                 if (!tmp.isNil() && EncodingUtils.encAsciicompat(EncodingUtils.encGet(context, tmp))) {
                     EncodingUtils.parseModeEncoding(context, holder, tmp.asJavaString(), null);
                     EncodingUtils.SET_UNIVERSAL_NEWLINE_DECORATOR_IF_ENC2(holder.getEnc2(), ecflags);
                     ecflags = EncodingUtils.econvPrepareOptions(context, opt, ecopts_p, ecflags);
                 } else {
                     EncodingUtils.ioExtIntToEncs(context, holder, EncodingUtils.rbToEncoding(context, v1), null, 0);
                     EncodingUtils.SET_UNIVERSAL_NEWLINE_DECORATOR_IF_ENC2(holder.getEnc2(), ecflags);
                 }
             }
             // enc, enc2 should be set by now
         }
 
         int[] fmode_p = {openFile.getMode()};
         EncodingUtils.validateEncodingBinmode(context, fmode_p, ecflags, holder);
         openFile.setMode(fmode_p[0]);
 
         openFile.encs.enc = holder.enc;
         openFile.encs.enc2 = holder.enc2;
         openFile.encs.ecflags = ecflags;
         openFile.encs.ecopts = ecopts_p[0];
 
         openFile.clearCodeConversion();
     }
 
     // rb_io_s_open, 2014/5/16
     @JRubyMethod(required = 1, rest = true, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject io = ((RubyClass)recv).newInstance(context, args, Block.NULL_BLOCK);
 
         return ensureYieldClose(context, io, block);
     }
 
     public static IRubyObject ensureYieldClose(ThreadContext context, IRubyObject port, Block block) {
         if (block.isGiven()) {
             Ruby runtime = context.runtime;
             try {
                 return block.yield(context, port);
             } finally {
                 ioClose(runtime, port);
             }
         }
         return port;
     }
 
     public static IRubyObject sysopen(IRubyObject recv, IRubyObject[] args, Block block) {
         return sysopen19(recv.getRuntime().getCurrentContext(), recv, args, block);
     }
 
     // rb_io_s_sysopen
     @JRubyMethod(name = "sysopen", required = 1, optional = 2, meta = true)
     public static IRubyObject sysopen19(ThreadContext context, IRubyObject recv, IRubyObject[] argv, Block block) {
         Ruby runtime = context.runtime;
         IRubyObject fname, vmode, vperm;
         fname = vmode = vperm = context.nil;
         IRubyObject intmode;
         int oflags;
         ChannelFD fd;
         int perm;
 
         switch (argv.length) {
             case 3:
                 vperm = argv[2];
             case 2:
                 vmode = argv[1];
             case 1:
                 fname = argv[0];
         }
         fname = StringSupport.checkEmbeddedNulls(runtime, RubyFile.get_path(context, fname));
 
         if (vmode.isNil())
             oflags = OpenFlags.O_RDONLY.intValue();
         else if (!(intmode = TypeConverter.checkIntegerType(runtime, vmode, "to_int")).isNil())
             oflags = RubyNumeric.num2int(intmode);
         else {
             vmode = vmode.convertToString();
             oflags = OpenFile.ioModestrOflags(runtime, vmode.toString());
         }
         if (vperm.isNil()) perm = 0666;
         else              perm = RubyNumeric.num2int(vperm);
 
         StringSupport.checkStringSafety(context.runtime, fname);
         fname = ((RubyString)fname).dupFrozen();
         fd = sysopen(runtime, fname.toString(), oflags, perm);
         return runtime.newFixnum(fd.bestFileno());
     }
 
     private static class Sysopen {
         String fname;
         int oflags;
         int perm;
         Errno errno;
     }
 
     // rb_sysopen
     protected static ChannelFD sysopen(Ruby runtime, String fname, int oflags, int perm) {
         ChannelFD fd;
         Sysopen data = new Sysopen();
 
         data.fname = fname;
         data.oflags = oflags;
         data.perm = perm;
 
         fd = sysopenInternal(runtime, data);
         if (fd == null) {
             if (data.errno == Errno.EMFILE || data.errno == Errno.ENFILE) {
                 System.gc();
                 data.errno = null;
                 fd = sysopenInternal(runtime, data);
             }
             if (fd == null) {
                 if (data.errno != null) {
                     throw runtime.newErrnoFromErrno(data.errno, fname);
                 }
                 throw runtime.newSystemCallError(fname);
             }
         }
         return fd;
     }
 
     // rb_sysopen_internal
     private static ChannelFD sysopenInternal(Ruby runtime, Sysopen data) {
         ChannelFD fd;
         // TODO: thread eventing as in MRI
         fd = sysopenFunc(runtime, data);
 //        if (0 <= fd)
 //            rb_update_max_fd(fd);
         return fd;
     }
 
     // sysopen_func
     private static ChannelFD sysopenFunc(Ruby runtime, Sysopen data) {
         return cloexecOpen(runtime, data);
     }
 
     // rb_cloexec_open
     private static ChannelFD cloexecOpen(Ruby runtime, Sysopen data)
     {
         Channel ret = null;
 //        #ifdef O_CLOEXEC
 //            /* O_CLOEXEC is available since Linux 2.6.23.  Linux 2.6.18 silently ignore it. */
 //            flags |= O_CLOEXEC;
 //        #elif defined O_NOINHERIT
 //            flags |= O_NOINHERIT;
 //        #endif
         PosixShim shim = new PosixShim(runtime);
         ret = shim.open(runtime.getCurrentDirectory(), data.fname, ModeFlags.createModeFlags(data.oflags), data.perm);
         if (ret == null) {
             data.errno = shim.errno;
             return null;
         }
         // TODO, if we need it?
 //        rb_maygvl_fd_fix_cloexec(ret);
         return new ChannelFD(ret, runtime.getPosix(), runtime.getFilenoUtil());
     }
 
     // MRI: rb_io_autoclose_p
     public boolean isAutoclose() {
         OpenFile fptr;
         fptr = getOpenFileChecked();
         return fptr.isAutoclose();
     }
 
     // MRI: rb_io_set_autoclose
     public void setAutoclose(boolean autoclose) {
         OpenFile fptr;
         fptr = getOpenFileChecked();
         fptr.setAutoclose(autoclose);
     }
 
     @JRubyMethod(name = "autoclose?")
     public IRubyObject autoclose(ThreadContext context) {
         return context.runtime.newBoolean(isAutoclose());
     }
 
     @JRubyMethod(name = "autoclose=")
     public IRubyObject autoclose_set(ThreadContext context, IRubyObject autoclose) {
         setAutoclose(autoclose.isTrue());
         return context.nil;
     }
 
     // MRI: rb_io_binmode_m
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
         setAscii8bitBinmode();
 
         RubyIO write_io = GetWriteIO();
         if (write_io != this)
              write_io.setAscii8bitBinmode();
 
         return this;
     }
 
     // MRI: rb_io_binmode_p
     @JRubyMethod(name = "binmode?")
     public IRubyObject op_binmode(ThreadContext context) {
         return RubyBoolean.newBoolean(context.runtime, getOpenFileChecked().isBinmode());
     }
 
     // rb_io_syswrite
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(ThreadContext context, IRubyObject str) {
        Ruby runtime = context.runtime;
         OpenFile fptr;
         long n;
 
         if (!(str instanceof RubyString))
             str = str.asString();
 
         RubyIO io = GetWriteIO();
         fptr = io.getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             fptr.checkWritable(context);
 
             str = str.convertToString().dupFrozen();
 
             if (fptr.wbuf.len != 0) {
                 runtime.getWarnings().warn("syswrite for buffered IO");
             }
 
             ByteList strByteList = ((RubyString) str).getByteList();
             n = OpenFile.writeInternal(context, fptr, fptr.fd(), strByteList.unsafeBytes(), strByteList.begin(), strByteList.getRealSize());
 
             if (n == -1) throw runtime.newErrnoFromErrno(fptr.errno(), fptr.getPath());
         } finally {
             if (locked) fptr.unlock();
         }
 
         return runtime.newFixnum(n);
     }
 
     // MRI: rb_io_write_nonblock
     @JRubyMethod(name = "write_nonblock", required = 1, optional = 1)
     public IRubyObject write_nonblock(ThreadContext context, IRubyObject[] argv) {
         Ruby runtime = context.runtime;
         IRubyObject str;
         IRubyObject opts = context.nil;
 
         boolean exception = ArgsUtil.extractKeywordArg(context, "exception", argv) != runtime.getFalse();
 
         str = argv[0];
 
         return ioWriteNonblock(context, runtime, str, !exception);
     }
 
     // MRI: io_write_nonblock
     private IRubyObject ioWriteNonblock(ThreadContext context, Ruby runtime, IRubyObject str, boolean no_exception) {
         OpenFile fptr;
         long n;
 
         if (!(str instanceof RubyString))
             str = str.asString();
 
         RubyIO io = GetWriteIO();
         fptr = io.getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             fptr.checkWritable(context);
 
             if (fptr.io_fflush(context) < 0)
                 throw runtime.newErrnoFromErrno(fptr.errno(), fptr.getPath());
 
             fptr.setNonblock(runtime);
 
             ByteList strByteList = ((RubyString) str).getByteList();
             n = fptr.posix.write(fptr.fd(), strByteList.unsafeBytes(), strByteList.begin(), strByteList.getRealSize(), true);
 
             if (n == -1) {
                 if (fptr.posix.errno == Errno.EWOULDBLOCK || fptr.posix.errno == Errno.EAGAIN) {
                     if (no_exception) {
                         return runtime.newSymbol("wait_writable");
                     } else {
                         throw runtime.newErrnoEAGAINWritableError("write would block");
                     }
                 }
                 throw runtime.newErrnoFromErrno(fptr.posix.errno, fptr.getPath());
             }
         } finally {
             if (locked) fptr.unlock();
         }
 
         return runtime.newFixnum(n);
     }
 
     public RubyIO GetWriteIO() {
         RubyIO writeIO;
         checkInitialized();
         writeIO = openFile.tiedIOForWriting;
         if (writeIO != null) {
             return writeIO;
         }
         return this;
     }
 
     private void checkInitialized() {
         if (openFile == null) {
             throw getRuntime().newIOError("uninitialized stream");
         }
     }
 
     /** io_write_m
      *
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(ThreadContext context, IRubyObject str) {
         return write(context, str, false);
     }
 
     // io_write
     public IRubyObject write(ThreadContext context, IRubyObject str, boolean nosync) {
         Ruby runtime = context.runtime;
         OpenFile fptr;
         long n;
         IRubyObject tmp;
 
         RubyIO io = GetWriteIO();
 
         str = str.asString();
         tmp = TypeConverter.ioCheckIO(runtime, io);
         if (tmp.isNil()) {
 	        /* port is not IO, call write method for it. */
             return io.callMethod(context, "write", str);
         }
         io = (RubyIO)tmp;
         if (((RubyString)str).size() == 0) return RubyFixnum.zero(runtime);
 
         fptr = io.getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             fptr = io.getOpenFileChecked();
             fptr.checkWritable(context);
 
             n = fptr.fwrite(context, str, nosync);
             if (n == -1) throw runtime.newErrnoFromErrno(fptr.errno(), fptr.getPath());
         } finally {
             if (locked) fptr.unlock();
         }
 
         return RubyFixnum.newFixnum(runtime, n);
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
         return context.runtime.newFixnum(getOpenFileChecked().getFileno());
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
      * MRI: rb_io_sync
      *
      * @return the current sync mode.
      */
     @JRubyMethod
     public RubyBoolean sync(ThreadContext context) {
         Ruby runtime = context.runtime;
         OpenFile fptr;
 
         RubyIO io = GetWriteIO();
         fptr = io.getOpenFileChecked();
         fptr.lock();
         try {
             return (fptr.getMode() & OpenFile.SYNC) != 0 ? runtime.getTrue() : runtime.getFalse();
         } finally {
             fptr.unlock();
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
     @JRubyMethod
     public IRubyObject pid(ThreadContext context) {
         OpenFile myOpenFile = getOpenFileChecked();
 
         if (myOpenFile.getProcess() == null) {
             return context.runtime.getNil();
         }
 
         // Of course this isn't particularly useful.
         long pid = myOpenFile.getPid();
 
         return context.runtime.newFixnum(pid);
     }
 
     // rb_io_pos
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos(ThreadContext context) {
         OpenFile fptr = getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             long pos = fptr.tell(context);
-            if (pos < 0 && fptr.errno() != null) throw context.runtime.newErrnoFromErrno(fptr.errno(), fptr.getPath());
+            if (pos == -1 && fptr.errno() != null) throw context.runtime.newErrnoFromErrno(fptr.errno(), fptr.getPath());
             pos -= fptr.rbuf.len;
             return context.runtime.newFixnum(pos);
         } finally {
             if (locked) fptr.unlock();
         }
     }
 
     // rb_io_set_pos
     @JRubyMethod(name = "pos=", required = 1)
     public RubyFixnum pos_set(ThreadContext context, IRubyObject offset) {
         OpenFile fptr;
         long pos;
 
         pos = offset.convertToInteger().getLongValue();
         fptr = getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             pos = fptr.seek(context, pos, PosixShim.SEEK_SET);
-            if (pos < 0 && fptr.errno() != null) throw context.runtime.newErrnoFromErrno(fptr.errno(), fptr.getPath());
+            if (pos == -1 && fptr.errno() != null) throw context.runtime.newErrnoFromErrno(fptr.errno(), fptr.getPath());
         } finally {
             if (locked) fptr.unlock();
         }
 
         return context.runtime.newFixnum(pos);
     }
 
     /** Print some objects to the stream.
      *
      */
     @JRubyMethod(rest = true, reads = FrameField.LASTLINE)
     public IRubyObject print(ThreadContext context, IRubyObject[] args) {
         return print(context, this, args);
     }
 
     /**
      * Print some objects to the stream.
      *
      * MRI: rb_io_print
      */
     public static IRubyObject print(ThreadContext context, IRubyObject out, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         int i;
         IRubyObject line;
         int argc = args.length;
 
         /* if no argument given, print `$_' */
         if (argc == 0) {
             argc = 1;
             line = context.getLastLine();
             args = new IRubyObject[]{line};
         }
         for (i=0; i<argc; i++) {
             IRubyObject outputFS = runtime.getGlobalVariables().get("$,");
             if (!outputFS.isNil() && i>0) {
                 write(context, out, outputFS);
             }
             write(context, out, args[i]);
         }
         IRubyObject outputRS = runtime.getGlobalVariables().get("$\\");
         if (argc > 0 && !outputRS.isNil()) {
             write(context, out, outputRS);
         }
 
         return context.nil;
     }
 
     @JRubyMethod(required = 1, rest = true)
     public IRubyObject printf(ThreadContext context, IRubyObject[] args) {
         write(context, this, RubyKernel.sprintf(context, this, args));
         return context.nil;
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject putc(ThreadContext context, IRubyObject ch) {
         Ruby runtime = context.runtime;
         IRubyObject str;
         if (ch instanceof RubyString) {
             str = ((RubyString)ch).substr(runtime, 0, 1);
         }
         else {
             str = RubyString.newStringShared(runtime, RubyFixnum.SINGLE_CHAR_BYTELISTS19[RubyNumeric.num2chr(ch) & 0xFF]);
         }
         write(context, str);
         return ch;
     }
 
     public static IRubyObject putc(ThreadContext context, IRubyObject maybeIO, IRubyObject object) {
         if (maybeIO instanceof RubyIO) {
             ((RubyIO)maybeIO).putc(context, object);
         } else {
             byte c = RubyNumeric.num2chr(object);
             IRubyObject str = RubyString.newStringShared(context.runtime, RubyFixnum.SINGLE_CHAR_BYTELISTS19[c & 0xFF]);
             maybeIO.callMethod(context, "write", str);
         }
 
         return object;
     }
 
     public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {
         int whence = PosixShim.SEEK_SET;
 
         if (args.length > 1) {
             whence = interpretSeekWhence(args[1]);
         }
 
         return doSeek(context, args[0], whence);
     }
 
     @JRubyMethod
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0) {
         int whence = PosixShim.SEEK_SET;
 
         return doSeek(context, arg0, whence);
     }
 
     @JRubyMethod
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         int whence = interpretSeekWhence(arg1);
 
         return doSeek(context, arg0, whence);
     }
 
     // rb_io_seek
     private RubyFixnum doSeek(ThreadContext context, IRubyObject offset, int whence) {
         OpenFile fptr;
         long pos;
 
         pos = RubyNumeric.num2long(offset);
         fptr = getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             pos = fptr.seek(context, pos, whence);
             if (pos < 0 && fptr.errno() != null) throw getRuntime().newErrnoFromErrno(fptr.errno(), fptr.getPath());
         } finally {
             if (locked) fptr.unlock();
         }
 
         return RubyFixnum.zero(context.runtime);
     }
 
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(required = 1, optional = 1)
     public RubyFixnum sysseek(ThreadContext context, IRubyObject[] args) {
         final Ruby runtime = context.runtime;
         IRubyObject offset = context.nil;
         int whence = PosixShim.SEEK_SET;
         OpenFile fptr;
         long pos;
 
         switch (args.length) {
             case 2:
                 IRubyObject ptrname = args[1];
                 whence = interpretSeekWhence(ptrname);
             case 1:
                 offset = args[0];
         }
         pos = offset.convertToInteger().getLongValue();
         fptr = getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             if ((fptr.isReadable()) &&
                     (fptr.READ_DATA_BUFFERED() || fptr.READ_CHAR_PENDING())) {
                 throw runtime.newIOError("sysseek for buffered IO");
             }
             if (fptr.isWritable() && fptr.wbuf.len != 0) {
                 runtime.getWarnings().warn("sysseek for buffered IO");
             }
             fptr.errno(null);
             pos = fptr.posix.lseek(fptr.fd(), pos, whence);
             if (pos == -1 && fptr.errno() != null) throw runtime.newErrnoFromErrno(fptr.errno(), fptr.getPath());
         } finally {
             if (locked) fptr.unlock();
         }
 
         return RubyFixnum.newFixnum(runtime, pos);
     }
 
     private static int interpretSeekWhence(IRubyObject vwhence) {
         if (vwhence instanceof RubySymbol) {
             if (vwhence.toString() == "SET")
                 return PosixShim.SEEK_SET;
             if (vwhence.toString() == "CUR")
                 return PosixShim.SEEK_CUR;
             if (vwhence.toString() == "END")
                 return PosixShim.SEEK_END;
         }
         return (int)vwhence.convertToInteger().getLongValue();
     }
 
     // rb_io_rewind
     @JRubyMethod
     public RubyFixnum rewind(ThreadContext context) {
         Ruby runtime = context.runtime;
         OpenFile fptr;
 
         fptr = getOpenFileChecked();
         boolean locked = fptr.lock();
         try {
-            if (fptr.seek(context, 0L, 0) < 0 && fptr.errno() != null)
+            if (fptr.seek(context, 0L, 0) == -1 && fptr.errno() != null)
                 throw context.runtime.newErrnoFromErrno(fptr.errno(), fptr.getPath());
             RubyArgsFile.ArgsFileData data = RubyArgsFile.ArgsFileData.getDataFrom(runtime.getArgsFile());
             if (this == data.currentFile) {
                 data.currentLineNumber -= fptr.getLineNumber();
             }
             fptr.setLineNumber(0);
             if (fptr.readconv != null) {
                 fptr.clearReadConversion();
             }
         } finally {
             if (locked) fptr.unlock();
         }
 
         return RubyFixnum.zero(runtime);
     }
 
     // rb_io_fsync
     @JRubyMethod
     public RubyFixnum fsync(ThreadContext context) {
         Ruby runtime = context.runtime;
         OpenFile fptr;
 
         RubyIO io = GetWriteIO();
         fptr = io.getOpenFileChecked();
 
         if (fptr.io_fflush(context) < 0)
             throw runtime.newSystemCallError("");
 
 //        # ifndef _WIN32	/* already called in io_fflush() */
 //        if ((int)rb_thread_io_blocking_region(nogvl_fsync, fptr, fptr->fd) < 0)
 //            rb_sys_fail_path(fptr->pathv);
 //        # endif
         return RubyFixnum.zero(runtime);
     }
 
     /** Sets the current sync mode.
      *
      * MRI: rb_io_set_sync
      *
      * @param sync The new sync mode.
      */
     @JRubyMethod(name = "sync=", required = 1)
     public IRubyObject sync_set(IRubyObject sync) {
         setSync(sync.isTrue());
 
         return sync;
     }
 
     public void setSync(boolean sync) {
         RubyIO io = GetWriteIO();
         OpenFile fptr = io.getOpenFileChecked();
         fptr.setSync(sync);
     }
 
     public boolean getSync() {
         RubyIO io = GetWriteIO();
         OpenFile fptr = io.getOpenFileChecked();
         return fptr.isSync();
     }
 
     // rb_io_eof
     @JRubyMethod(name = {"eof?", "eof"})
     public RubyBoolean eof_p(ThreadContext context) {
         Ruby runtime = context.runtime;
         OpenFile fptr;
 
         fptr = getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             fptr.checkCharReadable(context);
 
             if (fptr.READ_CHAR_PENDING()) return runtime.getFalse();
             if (fptr.READ_DATA_PENDING()) return runtime.getFalse();
             fptr.READ_CHECK(context);
             //        #if defined(RUBY_TEST_CRLF_ENVIRONMENT) || defined(_WIN32)
             //        if (!NEED_READCONV(fptr) && NEED_NEWLINE_DECORATOR_ON_READ(fptr)) {
             //            return eof(fptr->fd) ? Qtrue : Qfalse;
             //        }
             //        #endif
             if (fptr.fillbuf(context) < 0) {
                 return runtime.getTrue();
             }
         } finally {
             if (locked) fptr.unlock();
         }
 
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = {"tty?", "isatty"})
     public RubyBoolean tty_p(ThreadContext context) {
         Ruby runtime = context.runtime;
         POSIX posix = runtime.getPosix();
         OpenFile fptr;
 
         fptr = getOpenFileChecked();
 
         fptr.lock();
         try {
             if (posix.isNative() && fptr.fd().realFileno != -1) {
                 return posix.libc().isatty(fptr.getFileno()) == 0 ? runtime.getFalse() : runtime.getTrue();
             } else if (fptr.isStdio()) {
                 // This is a bit of a hack for platforms where we can't do native stdio
                 return runtime.getTrue();
             }
         } finally {
             fptr.unlock();
         }
 
         return runtime.getFalse();
     }
 
     // rb_io_init_copy
     @JRubyMethod(required = 1, visibility = Visibility.PRIVATE)
     @Override
     public IRubyObject initialize_copy(IRubyObject _io){
         RubyIO dest = this;
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         OpenFile fptr, orig;
         ChannelFD fd;
         RubyIO write_io;
         long pos;
 
         RubyIO io = TypeConverter.ioGetIO(runtime, _io);
         if (!OBJ_INIT_COPY(dest, io)) return dest;
         orig = io.getOpenFileChecked();
         fptr = dest.MakeOpenFile();
 
         // orig is the visible one here but we lock both anyway
         boolean locked1 = orig.lock();
         boolean locked2 = fptr.lock();
         try {
             io.flush(context);
 
             /* copy rb_io_t structure */
             fptr.setMode(orig.getMode() & ~OpenFile.PREP);
             fptr.encs = orig.encs;
             fptr.setProcess(orig.getProcess());
             fptr.setLineNumber(orig.getLineNumber());
             if (orig.getPath() != null) fptr.setPath(orig.getPath());
             fptr.setFinalizer(orig.getFinalizer());
             // TODO: not using pipe_finalize yet
             //        #if defined (__CYGWIN__) || !defined(HAVE_FORK)
             //        if (fptr.finalize == pipe_finalize)
             //            pipe_add_fptr(fptr);
             //        #endif
 
             fd = orig.fd().dup();
             fptr.setFD(fd);
             pos = orig.tell(context);
-            if (0 <= pos)
+            if (pos == -1)
                 fptr.seek(context, pos, PosixShim.SEEK_SET);
         } finally {
             if (locked2) fptr.unlock();
             if (locked1) orig.unlock();
         }
 
         if (fptr.isBinmode()) {
             dest.setBinmode();
         }
 
         write_io = io.GetWriteIO();
         if (io != write_io) {
             write_io = (RubyIO)write_io.dup();
             fptr.tiedIOForWriting = write_io;
             dest.getInstanceVariables().setInstanceVariable("@tied_io_for_writing", write_io);
         }
 
         return dest;
     }
 
     @JRubyMethod(name = "closed?")
     public RubyBoolean closed_p(ThreadContext context) {
         return context.runtime.newBoolean(isClosed());
     }
 
     /**
      * Is this IO closed
      *
      * MRI: rb_io_closed
      *
      * @return true if closed
      */
     public boolean isClosed() {
         OpenFile fptr;
         RubyIO write_io;
         OpenFile write_fptr;
 
         write_io = GetWriteIO();
         if (this != write_io) {
             write_fptr = write_io.openFile;
             if (write_fptr != null && write_fptr.fd() != null) {
                 return false;
             }
         }
 
         fptr = openFile;
         checkInitialized();
         return fptr.fd() == null;
     }
 
     /**
      * <p>Closes all open resources for the IO.  It also removes
      * it from our magical all open file descriptor pool.</p>
      *
      * @return The IO. Returns nil if the IO was already closed.
      *
      * MRI: rb_io_close_m
      */
     @JRubyMethod
     public IRubyObject close() {
         Ruby runtime = getRuntime();
         if (isClosed()) {
             return runtime.getNil();
         }
         return rbIoClose(runtime);
     }
 
     // io_close
     protected static IRubyObject ioClose(Ruby runtime, IRubyObject io) {
         ThreadContext context = runtime.getCurrentContext();
         IRubyObject closed = io.checkCallMethod(context, "closed?");
         if (closed != null && closed.isTrue()) return io;
         IRubyObject oldExc = runtime.getGlobalVariables().get("$!"); // Save $!
         try {
             return io.checkCallMethod(context, "close");
         } catch (RaiseException re) {
             if (re.getMessage().contains(CLOSED_STREAM_MSG)) {
                 // ignore
                 runtime.getGlobalVariables().set("$!", oldExc); // Restore $!
                 return context.nil;
             } else {
                 throw re;
             }
         }
     }
 
     // rb_io_close
     protected IRubyObject rbIoClose(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         OpenFile fptr;
         RubyIO write_io;
         OpenFile write_fptr;
 
         write_io = GetWriteIO();
         if (this != write_io) {
             write_fptr = write_io.openFile;
 
             boolean locked = write_fptr.lock();
             try {
                 if (write_fptr != null && write_fptr.fd() != null) {
                     write_fptr.cleanup(runtime, true);
                 }
             } finally {
                 if (locked) write_fptr.unlock();
             }
         }
 
         fptr = openFile;
 
         boolean locked = fptr.lock();
         try {
             if (fptr == null) return runtime.getNil();
             if (fptr.fd() == null) return runtime.getNil();
 
             // interrupt waiting threads
             fptr.interruptBlockingThreads();
             fptr.cleanup(runtime, false);
 
             if (fptr.getProcess() != null) {
                 context.setLastExitStatus(context.nil);
 
                 if (runtime.getPosix().isNative() && fptr.getProcess() instanceof POSIXProcess) {
                     // We do not need to nuke native-launched child process, since we now have full control
                     // over child process pipes.
                     IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(runtime, ((POSIXProcess) fptr.getProcess()).status(), fptr.getPid());
                     context.setLastExitStatus(processResult);
                 } else {
                     // If this is not a popen3/popen4 stream and it has a process, attempt to shut down that process
                     if (!popenSpecial) {
                         obliterateProcess(fptr.getProcess());
                         // RubyStatus uses real native status now, so we unshift Java's shifted exit status
                         IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(runtime, fptr.getProcess().exitValue() << 8, fptr.getPid());
                         context.setLastExitStatus(processResult);
                     }
                 }
                 fptr.setProcess(null);
             }
         } finally {
             if (locked) fptr.unlock();
         }
 
         return runtime.getNil();
     }
 
     // MRI: rb_io_close_write
     @JRubyMethod
     public IRubyObject close_write(ThreadContext context) {
         Ruby runtime = context.runtime;
         OpenFile fptr;
         RubyIO write_io;
 
         write_io = GetWriteIO();
         fptr = write_io.getOpenFileInitialized();
         if (!fptr.isOpen()) return context.nil;
 
         boolean locked = fptr.lock();
         try {
             if (fptr.socketChannel() != null) {
                 try {
                     fptr.socketChannel().shutdownOutput();
                 } catch (IOException ioe) {
                     throw runtime.newErrnoFromErrno(Helpers.errnoFromException(ioe), fptr.getPath());
                 }
                 fptr.setMode(fptr.getMode() & ~OpenFile.WRITABLE);
                 if (!fptr.isReadable())
                     return write_io.rbIoClose(runtime);
                 return context.nil;
             }
 
             if (fptr.isReadable() && !fptr.isDuplex()) {
                 throw runtime.newIOError("closing non-duplex IO for writing");
             }
         } finally {
             if (locked) fptr.unlock();
         }
 
 
         if (this != write_io) {
             fptr = getOpenFileInitialized();
 
             locked = fptr.lock();
             try {
                 fptr.tiedIOForWriting = null;
             } finally {
                 if (locked) fptr.unlock();
             }
         }
 
         write_io.rbIoClose(runtime);
         return context.nil;
     }
 
     @JRubyMethod
     public IRubyObject close_read(ThreadContext context) {
         Ruby runtime = context.runtime;
         OpenFile fptr;
         RubyIO write_io;
 
         fptr = getOpenFileInitialized();
         if (!fptr.isOpen()) return context.nil;
 
         boolean locked = fptr.lock();
         try {
             if (fptr.socketChannel() != null) {
                 try {
                     fptr.socketChannel().socket().shutdownInput();
                 } catch (IOException ioe) {
                     throw runtime.newErrnoFromErrno(Helpers.errnoFromException(ioe), fptr.getPath());
                 }
                 fptr.setMode(fptr.getMode() & ~OpenFile.READABLE);
                 if (!fptr.isWritable())
                     return rbIoClose(runtime);
                 return context.nil;
             }
 
             write_io = GetWriteIO();
             if (this != write_io) {
                 OpenFile wfptr;
                 wfptr = write_io.getOpenFileInitialized();
 
                 boolean locked2 = wfptr.lock();
                 try {
                     wfptr.setProcess(fptr.getProcess());
                     wfptr.setPid(fptr.getPid());
                     fptr.setProcess(null);
                     fptr.setPid(-1);
                     this.openFile = wfptr;
                     /* bind to write_io temporarily to get rid of memory/fd leak */
                     fptr.tiedIOForWriting = null;
                     write_io.openFile = fptr;
                     fptr.cleanup(runtime, false);
                     /* should not finalize fptr because another thread may be reading it */
                     return context.nil;
                 } finally {
                     if (locked2) wfptr.unlock();
                 }
             }
 
             if (fptr.isWritable() && !fptr.isDuplex()) {
                 throw runtime.newIOError("closing non-duplex IO for reading");
             }
         } finally {
             if (locked) fptr.unlock();
         }
 
         return rbIoClose(runtime);
     }
 
     @JRubyMethod(name = "close_on_exec=", notImplemented = true)
     public IRubyObject close_on_exec_set(ThreadContext context, IRubyObject arg) {
         // TODO: rb_io_set_close_on_exec
         throw context.runtime.newNotImplementedError("close_on_exec=");
     }
 
     @JRubyMethod(name = "close_on_exec?", notImplemented = true)
     public IRubyObject close_on_exec_p(ThreadContext context) {
         // TODO: rb_io_close_on_exec_p
         throw context.runtime.newNotImplementedError("close_on_exec=");
     }
 
     /** Flushes the IO output stream.
      *
      * MRI: rb_io_flush
      *
      * @return The IO.
      */
     @JRubyMethod
     public RubyIO flush(ThreadContext context) {
         return flushRaw(context, true);
     }
 
     // rb_io_flush_raw
     protected RubyIO flushRaw(ThreadContext context, boolean sync) {
         OpenFile fptr;
 
         // not possible here
 //        if (!RB_TYPE_P(io, T_FILE)) {
 //            return rb_funcall(io, id_flush, 0);
 //        }
 
         RubyIO io = GetWriteIO();
         fptr = io.getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             if ((fptr.getMode() & OpenFile.WRITABLE) != 0) {
                 if (fptr.io_fflush(context) < 0)
                     throw context.runtime.newErrnoFromErrno(fptr.errno(), "");
                 //            #ifdef _WIN32
                 //            if (sync && GetFileType((HANDLE)rb_w32_get_osfhandle(fptr->fd)) == FILE_TYPE_DISK) {
                 //                rb_thread_io_blocking_region(nogvl_fsync, fptr, fptr->fd);
                 //            }
                 //            #endif
             }
             if ((fptr.getMode() & OpenFile.READABLE) != 0) {
                 fptr.unread(context);
             }
         } finally {
             if (locked) fptr.unlock();
         }
 
         return io;
     }
 
     /** Read a line.
      *
      */
 
     // rb_io_gets_m
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE)
     public IRubyObject gets(ThreadContext context) {
         IRubyObject separator = prepareGetsSeparator(context, null, null);
         IRubyObject result = getline(context, separator);
 
         if (!result.isNil()) context.setLastLine(result);
 
         return result;
     }
 
     // rb_io_gets_m
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE)
     public IRubyObject gets(ThreadContext context, IRubyObject arg) {
         IRubyObject separator = prepareGetsSeparator(context, arg, null);
         long limit = prepareGetsLimit(context, arg, null);
 
         IRubyObject result = getline(context, separator, limit);
 
         if (!result.isNil()) context.setLastLine(result);
 
         return result;
     }
 
     // rb_io_gets_m
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE)
     public IRubyObject gets(ThreadContext context, IRubyObject rs, IRubyObject limit_arg) {
         rs = prepareGetsSeparator(context, rs, limit_arg);
         long limit = prepareGetsLimit(context, rs, limit_arg);
         IRubyObject result = getline(context, rs, limit);
 
         if (!result.isNil()) context.setLastLine(result);
 
         return result;
     }
 
     private IRubyObject prepareGetsSeparator(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
             case 0:
                 return prepareGetsSeparator(context, null, null);
             case 1:
                 return prepareGetsSeparator(context, args[0], null);
             case 2:
                 return prepareGetsSeparator(context, args[0], args[1]);
         }
         throw new RuntimeException("invalid size for gets args: " + args.length);
     }
 
     // MRI: prepare_getline_args, separator logic
     private IRubyObject prepareGetsSeparator(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         Ruby runtime = context.runtime;
         IRubyObject rs = runtime.getRecordSeparatorVar().get();
         if (arg0 != null && arg1 == null) { // argc == 1
             IRubyObject tmp = context.nil;
 
             if (arg0.isNil() || !(tmp = TypeConverter.checkStringType(runtime, arg0)).isNil()) {
                 rs = tmp;
             }
         } else if (arg0 != null && arg1 != null) { // argc >= 2
             rs = arg0;
             if (!rs.isNil()) {
                 rs = rs.convertToString();
             }
         }
         if (!rs.isNil()) {
             Encoding enc_rs, enc_io;
 
             OpenFile fptr = getOpenFileChecked();
             enc_rs = ((RubyString)rs).getEncoding();
             enc_io = fptr.readEncoding(runtime);
             if (enc_io != enc_rs &&
                     (((RubyString)rs).scanForCodeRange() != StringSupport.CR_7BIT ||
                             (((RubyString)rs).size() > 0 && !enc_io.isAsciiCompatible()))) {
                 if (rs == runtime.getGlobalVariables().getDefaultSeparator()) {
                     rs = RubyString.newStringLight(runtime, 0, enc_io);
                     ((RubyString)rs).catAscii(NEWLINE_BYTES, 0, 1);
                 }
                 else {
                     throw runtime.newArgumentError("encoding mismatch: " + enc_io + " IO with " + enc_rs + " RS");
                 }
             }
         }
         return rs;
     }
 
     private long prepareGetsLimit(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
             case 0:
                 return prepareGetsLimit(context, null, null);
             case 1:
                 return prepareGetsLimit(context, args[0], null);
             case 2:
                 return prepareGetsLimit(context, args[0], args[1]);
         }
         throw new RuntimeException("invalid size for gets args: " + args.length);
     }
 
     // MRI: prepare_getline_args, limit logic
     private long prepareGetsLimit(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         Ruby runtime = context.runtime;
         IRubyObject lim = context.nil;
         if (arg0 != null && arg1 == null) { // argc == 1
             IRubyObject tmp = context.nil;
 
             if (arg0.isNil() || !(tmp = TypeConverter.checkStringType(runtime, arg0)).isNil()) {
                 // only separator logic
             } else {
                 lim = arg0;
             }
         } else if (arg0 != null && arg1 != null) { // argc >= 2
             lim = arg1;
         }
         return lim.isNil() ? -1 : lim.convertToInteger().getLongValue();
     }
 
     private IRubyObject gets(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
             case 0:
                 return gets(context);
             case 1:
                 return gets(context, args[0]);
             case 2:
                 return gets(context, args[0], args[1]);
             default:
                 Arity.raiseArgumentError(context, args.length, 0, 2);
                 return null; // not reached
         }
     }
 
     public boolean getBlocking() {
         return openFile.isBlocking();
     }
 
     public void setBlocking(boolean blocking) {
         openFile.setBlocking(getRuntime(), blocking);
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
             return runtime.newFixnum(OpenFile.ioFmodeOflags(myOpenFile.getMode()));
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
 
         OpenFile fptr = getOpenFileChecked();
 
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
         } else if (realCmd == Fcntl.F_SETFL.intValue() || realCmd == Fcntl.F_SETFD.intValue()) {
             if ((nArg & FcntlLibrary.FD_CLOEXEC) == FcntlLibrary.FD_CLOEXEC) {
                 // Do nothing.  FD_CLOEXEC has no meaning in JVM since we cannot really exec.
             } else {
                 boolean block = (nArg & ModeFlags.NONBLOCK) != ModeFlags.NONBLOCK;
 
                 fptr.setBlocking(runtime, block);
             }
         } else if (realCmd == Fcntl.F_GETFL.intValue()) {
             return fptr.isBlocking() ? RubyFixnum.zero(runtime) : RubyFixnum.newFixnum(runtime, ModeFlags.NONBLOCK);
         } else {
             throw runtime.newNotImplementedError("JRuby only supports F_SETFL and F_GETFL with NONBLOCK for fcntl/ioctl");
         }
 
         return runtime.newFixnum(0);
     }
 
     @JRubyMethod(name = "puts")
     public IRubyObject puts(ThreadContext context) {
         return puts0(context, this);
     }
 
     @JRubyMethod(name = "puts")
     public IRubyObject puts(ThreadContext context, IRubyObject arg0) {
         return puts1(context, this, arg0);
     }
 
     @JRubyMethod(name = "puts")
     public IRubyObject puts(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         return puts2(context, this, arg0, arg1);
     }
 
     @JRubyMethod(name = "puts")
     public IRubyObject puts(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         return puts3(context, this, arg0, arg1, arg2);
     }
 
     @JRubyMethod(name = "puts", rest = true)
     public IRubyObject puts(ThreadContext context, IRubyObject[] args) {
         return puts(context, this, args);
     }
 
     public static IRubyObject puts0(ThreadContext context, IRubyObject maybeIO) {
         return writeSeparator(context, maybeIO);
     }
 
     public static IRubyObject puts1(ThreadContext context, IRubyObject maybeIO, IRubyObject arg0) {
         Ruby runtime = context.runtime;
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         putsSingle(context, runtime, maybeIO, arg0, separator);
 
         return context.nil;
     }
 
     public static IRubyObject puts2(ThreadContext context, IRubyObject maybeIO, IRubyObject arg0, IRubyObject arg1) {
         Ruby runtime = context.runtime;
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         putsSingle(context, runtime, maybeIO, arg0, separator);
         putsSingle(context, runtime, maybeIO, arg1, separator);
 
         return context.nil;
     }
 
     public static IRubyObject puts3(ThreadContext context, IRubyObject maybeIO, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.runtime;
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         putsSingle(context, runtime, maybeIO, arg0, separator);
         putsSingle(context, runtime, maybeIO, arg1, separator);
         putsSingle(context, runtime, maybeIO, arg2, separator);
 
         return context.nil;
     }
 
     public static IRubyObject puts(ThreadContext context, IRubyObject maybeIO, IRubyObject... args) {
         if (args.length == 0) {
             return writeSeparator(context, maybeIO);
         }
 
         return putsArray(context, maybeIO, args);
     }
 
     private static IRubyObject writeSeparator(ThreadContext context, IRubyObject maybeIO) {
         Ruby runtime = context.runtime;
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         write(context, maybeIO, separator);
         return runtime.getNil();
     }
 
     private static IRubyObject putsArray(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         for (int i = 0; i < args.length; i++) {
             putsSingle(context, runtime, maybeIO, args[i], separator);
         }
 
         return runtime.getNil();
     }
 
     private static final ByteList RECURSIVE_BYTELIST = ByteList.create("[...]");
 
     private static void putsSingle(ThreadContext context, Ruby runtime, IRubyObject maybeIO, IRubyObject arg, RubyString separator) {
         ByteList line;
 
         if (arg.isNil()) {
             line = getNilByteList(runtime);
         } else if (runtime.isInspecting(arg)) {
             line = RECURSIVE_BYTELIST;
         } else if (arg instanceof RubyArray) {
             inspectPuts(context, maybeIO, (RubyArray) arg);
             return;
         } else {
             line = arg.asString().getByteList();
         }
 
         write(context, maybeIO, line);
 
         if (line.length() == 0 || !line.endsWith(separator.getByteList())) {
             write(context, maybeIO, separator.getByteList());
         }
     }
 
     private static IRubyObject inspectPuts(ThreadContext context, IRubyObject maybeIO, RubyArray array) {
         try {
             context.runtime.registerInspecting(array);
             return putsArray(context, maybeIO, array.toJavaArray());
         } finally {
             context.runtime.unregisterInspecting(array);
         }
     }
 
     protected IRubyObject write(ThreadContext context, ByteList byteList) {
         return callMethod(context, "write", RubyString.newStringShared(context.runtime, byteList));
     }
 
     protected static IRubyObject write(ThreadContext context, IRubyObject maybeIO, ByteList byteList) {
         return maybeIO.callMethod(context, "write", RubyString.newStringShared(context.runtime, byteList));
     }
 
     public static IRubyObject write(ThreadContext context, IRubyObject maybeIO, IRubyObject str) {
         return maybeIO.callMethod(context, "write", str);
     }
 
     @Override
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
 
         if (openFile == null) return super.inspect();
 
         String className = getMetaClass().getRealClass().getName();
         String path = openFile.getPath();
         String status = "";
 
         if (path == null) {
             if (openFile.fd() == null) {
                 path = "";
                 status = "(closed)";
             } else {
                 path = "fd " + openFile.fd().bestFileno();
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
     public IRubyObject getc() {
         return getbyte(getRuntime().getCurrentContext());
     }
 
     // rb_io_readchar
     @JRubyMethod
     public IRubyObject readchar(ThreadContext context) {
         IRubyObject c = getc19(context);
 
         if (c.isNil()) {
             throw context.runtime.newEOFError();
         }
         return c;
     }
 
     // rb_io_getbyte
     @JRubyMethod
     public IRubyObject getbyte(ThreadContext context) {
         int c = getByte(context);
 
         if (c == -1) return context.nil;
 
         return RubyNumeric.int2fix(context.runtime, c & 0xff);
     }
 
     // rb_io_getbyte
     public int getByte(ThreadContext context) {
         int c;
 
         OpenFile fptr = getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             fptr.checkByteReadable(context);
             fptr.READ_CHECK(context);
             // TODO: tty flushing
             //        if (fptr->fd == 0 && (fptr->mode & FMODE_TTY) && RB_TYPE_P(rb_stdout, T_FILE)) {
             //            rb_io_t *ofp;
             //            GetOpenFile(rb_stdout, ofp);
             //            if (ofp->mode & FMODE_TTY) {
             //                rb_io_flush(rb_stdout);
             //            }
             //        }
             if (fptr.fillbuf(context) < 0) {
                 return -1;
             }
             fptr.rbuf.off++;
             fptr.rbuf.len--;
             return fptr.rbuf.ptr[fptr.rbuf.off - 1] & 0xFF;
         } finally {
             if (locked) fptr.unlock();
         }
     }
 
     // rb_io_readbyte
     @JRubyMethod
     public IRubyObject readbyte(ThreadContext context) {
         IRubyObject c = getbyte(context);
 
         if (c.isNil()) {
             throw getRuntime().newEOFError();
         }
         return c;
     }
 
     // rb_io_getc
     @JRubyMethod(name = "getc")
     public IRubyObject getc19(ThreadContext context) {
         Ruby runtime = context.runtime;
         Encoding enc;
 
         OpenFile fptr = getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             fptr.checkCharReadable(context);
 
             enc = fptr.inputEncoding(runtime);
             fptr.READ_CHECK(context);
             return fptr.getc(context, enc);
         } finally {
             if (locked) fptr.unlock();
         }
     }
 
     // rb_io_ungetbyte
     @JRubyMethod
     public IRubyObject ungetbyte(ThreadContext context, IRubyObject b) {
         OpenFile fptr = getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             fptr.checkByteReadable(context);
             if (b.isNil()) return context.nil;
             if (b instanceof RubyFixnum) {
                 byte cc = (byte) RubyNumeric.fix2int(b);
                 b = RubyString.newStringNoCopy(context.runtime, new byte[]{cc});
             } else {
                 b = b.convertToString();
             }
             fptr.ungetbyte(context, b);
         } finally {
             if (locked) fptr.unlock();
         }
 
         return context.nil;
     }
 
     // MRI: rb_io_ungetc
     @JRubyMethod
     public IRubyObject ungetc(ThreadContext context, IRubyObject c) {
         Ruby runtime = context.runtime;
 
         final OpenFile fptr = getOpenFileChecked();
 
         boolean locked = fptr.lock();
         try {
             fptr.checkCharReadable(context);
             if (c.isNil()) return c;
             if (c instanceof RubyFixnum) {
                 c = EncodingUtils.encUintChr(context, (int) ((RubyFixnum) c).getLongValue(), fptr.readEncoding(runtime));
             } else if (c instanceof RubyBignum) {
                 c = EncodingUtils.encUintChr(context, (int) ((RubyBignum) c).getLongValue(), fptr.readEncoding(runtime));
             } else {
                 c = c.convertToString();
             }
             if (fptr.needsReadConversion()) {
                 fptr.SET_BINARY_MODE();
                 final int len = ((RubyString) c).size();
                 //            #if SIZEOF_LONG > SIZEOF_INT
                 //            if (len > INT_MAX)
                 //                rb_raise(rb_eIOError, "ungetc failed");
                 //            #endif
                 fptr.makeReadConversion(context, len);
                 if (fptr.cbuf.capa - fptr.cbuf.len < len)
                     throw runtime.newIOError("ungetc failed");
                 // shift cbuf back to 0
                 if (fptr.cbuf.off < len) {
                     System.arraycopy(
                             fptr.cbuf.ptr, fptr.cbuf.off,
                             fptr.cbuf.ptr, fptr.cbuf.capa - fptr.cbuf.len, // this should be 0
                             fptr.cbuf.len);
                     fptr.cbuf.off = fptr.cbuf.capa - fptr.cbuf.len; // this should be 0 too
                 }
                 fptr.cbuf.off -= len;
                 fptr.cbuf.len += len;
                 ByteList cByteList = ((RubyString) c).getByteList();
                 System.arraycopy(cByteList.unsafeBytes(), cByteList.begin(), fptr.cbuf.ptr, fptr.cbuf.off, len);
             } else {
                 fptr.NEED_NEWLINE_DECORATOR_ON_READ_CHECK();
                 fptr.ungetbyte(context, c);
             }
         } finally {
             if (locked) fptr.unlock();
         }
 
         return context.nil;
     }
 
     @JRubyMethod(name = "read_nonblock", required = 1, optional = 2)
     public IRubyObject read_nonblock(ThreadContext context, IRubyObject[] args) {
         return doReadNonblock(context, args, true);
     }
 
     // MRI: io_read_nonblock
     public IRubyObject doReadNonblock(ThreadContext context, IRubyObject[] args, boolean useException) {
         final Ruby runtime = context.runtime;
 
         boolean exception = ArgsUtil.extractKeywordArg(context, "exception", args) != runtime.getFalse();
 
         IRubyObject ret = getPartial(context, args, true, !exception);
 
         return ret.isNil() ? nonblockEOF(runtime, !exception) : ret;
     }
 
     // MRI: io_nonblock_eof(VALUE opts)
     static IRubyObject nonblockEOF(final Ruby runtime, final boolean noException) {
         if ( noException ) return runtime.getNil();
         throw runtime.newEOFError();
     }
 
     @JRubyMethod(name = "readpartial", required = 1, optional = 1)
     public IRubyObject readpartial(ThreadContext context, IRubyObject[] args) {
         IRubyObject value = getPartial(context, args, false, false);
 
         if (value.isNil()) {
             throw context.runtime.newEOFError();
         }
 
         return value;
     }
 
     // MRI: io_getpartial
     IRubyObject getPartial(ThreadContext context, IRubyObject[] args, boolean nonblock, boolean noException) {
         Ruby runtime = context.runtime;
         OpenFile fptr;
         IRubyObject length, str;
 
         switch (args.length) {
             case 3:
                 length = args[0];
                 str = args[1];
                 args[2].convertToHash();
                 break;
             case 2:
                 length = args[0];
                 str = TypeConverter.checkHashType(runtime, args[1]);
                 str = str.isNil() ? args[1] : context.nil;
                 break;
             case 1:
                 length = args[0];
                 str = context.nil;
                 break;
             default:
                 length = context.nil;
                 str = context.nil;
         }
 
         final int len;
         if ( ( len = RubyNumeric.num2int(length) ) < 0 ) {
             throw runtime.newArgumentError("negative length " + len + " given");
         }
 
         str = EncodingUtils.setStrBuf(runtime, str, len);
         str.setTaint(true);
 
         fptr = getOpenFileChecked();
 
         final boolean locked = fptr.lock(); int n;
         try {
             fptr.checkByteReadable(context);
 
             if ( len == 0 ) return str;
 
             if ( ! nonblock ) fptr.READ_CHECK(context);
 
             ByteList strByteList = ((RubyString) str).getByteList();
             n = fptr.readBufferedData(strByteList.unsafeBytes(), strByteList.begin(), len);
             if (n <= 0) {
                 again:
                 while (true) {
                     if (nonblock) {
                         fptr.setNonblock(runtime);
                     }
                     str = EncodingUtils.setStrBuf(runtime, str, len);
                     strByteList = ((RubyString) str).getByteList();
                     //                arg.fd = fptr->fd;
                     //                arg.str_ptr = RSTRING_PTR(str);
                     //                arg.len = len;
                     //                rb_str_locktmp_ensure(str, read_internal_call, (VALUE)&arg);
                     //                n = arg.len;
                     n = OpenFile.readInternal(context, fptr, fptr.fd(), strByteList.unsafeBytes(), strByteList.begin(), len);
                     if (n < 0) {
                         if (!nonblock && fptr.waitReadable(context))
                             continue again;
                         if (nonblock && (fptr.errno() == Errno.EWOULDBLOCK || fptr.errno() == Errno.EAGAIN)) {
                             if (noException) return runtime.newSymbol("wait_readable");
                             throw runtime.newErrnoEAGAINReadableError("read would block");
                         }
                         throw runtime.newEOFError(fptr.getPath());
                     }
                     break;
                 }
             }
         }
         finally {
             if ( locked ) fptr.unlock();
         }
 
         ((RubyString) str).setReadLength(n);
 
         return n == 0 ? context.nil : str;
     }
 
     // MRI: rb_io_sysread
     @JRubyMethod(name = "sysread", required = 1, optional = 1)
     public IRubyObject sysread(ThreadContext context, IRubyObject[] args) {
         final Ruby runtime = context.runtime;
 
         final int length = RubyNumeric.num2int(args.length >= 1 ? args[0] : context.nil);
         RubyString str = EncodingUtils.setStrBuf(runtime, args.length >= 2 ? args[1] : context.nil, length);
         if (length == 0) return str;
 
         final OpenFile fptr = getOpenFileChecked();
 
         final int n;
         boolean locked = fptr.lock();
         try {
diff --git a/core/src/main/java/org/jruby/util/io/OpenFile.java b/core/src/main/java/org/jruby/util/io/OpenFile.java
index 780a806ae3..3453da1540 100644
--- a/core/src/main/java/org/jruby/util/io/OpenFile.java
+++ b/core/src/main/java/org/jruby/util/io/OpenFile.java
@@ -1,2665 +1,2665 @@
 package org.jruby.util.io;
 
 import jnr.constants.platform.Errno;
 import jnr.constants.platform.OpenFlags;
 import jnr.posix.FileStat;
 import org.jcodings.Encoding;
 import org.jcodings.Ptr;
 import org.jcodings.transcode.EConv;
 import org.jcodings.transcode.EConvFlags;
 import org.jcodings.transcode.EConvResult;
 import org.jruby.Finalizable;
 import org.jruby.Ruby;
 import org.jruby.RubyArgsFile;
 import org.jruby.RubyBasicObject;
 import org.jruby.RubyBignum;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyIO;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.RubyThread;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.StringSupport;
 
 import java.io.Closeable;
 import java.io.IOException;
 import java.nio.channels.Channel;
 import java.nio.channels.FileChannel;
 import java.nio.channels.FileLock;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.SeekableByteChannel;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.nio.channels.SocketChannel;
 import java.nio.channels.WritableByteChannel;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.concurrent.locks.ReentrantLock;
 import java.util.concurrent.locks.ReentrantReadWriteLock;
 
 public class OpenFile implements Finalizable {
 
     // RB_IO_FPTR_NEW, minus fields that Java already initializes the same way
     public OpenFile(IRubyObject nil) {
         runtime = nil.getRuntime();
         writeconvAsciicompat = nil;
         writeconvPreEcopts = nil;
         encs.ecopts = nil;
         posix = new PosixShim(runtime);
     }
 
     // IO Mode flags
     public static final int READABLE           = 0x00000001;
     public static final int WRITABLE           = 0x00000002;
     public static final int READWRITE          = READABLE | WRITABLE;
     public static final int BINMODE            = 0x00000004;
     public static final int SYNC               = 0x00000008;
     public static final int TTY                = 0x00000010;
     public static final int DUPLEX             = 0x00000020;
     public static final int APPEND             = 0x00000040;
     public static final int CREATE             = 0x00000080;
     public static final int WSPLIT             = 0x00000200;
     public static final int WSPLIT_INITIALIZED = 0x00000400;
     public static final int TRUNC              = 0x00000800;
     public static final int TEXTMODE           = 0x00001000;
     public static final int SETENC_BY_BOM      = 0x00100000;
     public static final int PREP         = (1<<16);
 
     public static final int SYNCWRITE = SYNC | WRITABLE;
 
     public static final int PIPE_BUF = 512; // value of _POSIX_PIPE_BUF from Mac OS X 10.9
     public static final int BUFSIZ = 1024; // value of BUFSIZ from Mac OS X 10.9 stdio.h
 
     public void ascii8bitBinmode(Ruby runtime) {
         if (readconv != null) {
             readconv.close();
             readconv = null;
         }
         if (writeconv != null) {
             writeconv.close();
             writeconv = null;
         }
         setBinmode();
         clearTextMode();
         // TODO: Windows
         //SET_BINARY_MODE_WITH_SEEK_CUR()
         encs.enc = EncodingUtils.ascii8bitEncoding(runtime);
         encs.enc2 = null;
         encs.ecflags = 0;
         encs.ecopts = runtime.getNil();
         clearCodeConversion();
     }
 
     public void checkReopenSeek(ThreadContext context, Ruby runtime, long pos) {
-        if (seek(context, pos, PosixShim.SEEK_SET) < 0 && errno() != null) {
+        if (seek(context, pos, PosixShim.SEEK_SET) == -1 && errno() != null) {
             throw runtime.newErrnoFromErrno(errno(), getPath());
         }
     }
 
     public static interface Finalizer {
         public void finalize(Ruby runtime, OpenFile fptr, boolean noraise);
     }
 
     private ChannelFD fd = null;
     private int mode;
     private long pid = -1;
     private Process process;
     private int lineno;
     private String pathv;
     private Finalizer finalizer;
     public Closeable stdio_file;
     public volatile FileLock currentLock;
 
     public static class Buffer {
         public byte[] ptr;
         public int start;
         public int off;
         public int len;
         public int capa;
     }
 
     public IOEncodable.ConvConfig encs = new IOEncodable.ConvConfig();
 
     public EConv readconv;
     public EConv writeconv;
     public IRubyObject writeconvAsciicompat;
     public int writeconvPreEcflags;
     public IRubyObject writeconvPreEcopts;
     public boolean writeconvInitialized;
 
     public volatile ReentrantReadWriteLock write_lock;
     private final ReentrantLock lock = new ReentrantLock();
 
     public final Buffer wbuf = new Buffer(), rbuf = new Buffer(), cbuf = new Buffer();
 
     public RubyIO tiedIOForWriting;
 
     private boolean nonblock = false;
 
     public final PosixShim posix;
 
     private final Ruby runtime;
 
     protected List<RubyThread> blockingThreads;
 
     public void clearStdio() {
         stdio_file = null;
     }
 
     public String PREP_STDIO_NAME() {
         return pathv;
     }
 
     public boolean READ_DATA_PENDING() {return rbuf.len != 0;}
     public int READ_DATA_PENDING_COUNT() {return rbuf.len;}
     // goes with READ_DATA_PENDING_OFF
     public byte[] READ_DATA_PENDING_PTR() {return rbuf.ptr;}
     public int READ_DATA_PENDING_OFF() {return rbuf.off;}
     public int READ_DATA_PENDING_START() {return rbuf.start;}
     public boolean READ_DATA_BUFFERED() {return READ_DATA_PENDING();}
 
     public boolean READ_CHAR_PENDING() {return cbuf.len != 0;}
     public int READ_CHAR_PENDING_COUNT() {return cbuf.len;}
     // goes with READ_CHAR_PENDING_OFF
     public byte[] READ_CHAR_PENDING_PTR() {return cbuf.ptr;}
     public int READ_CHAR_PENDING_OFF() {return cbuf.off;}
     public int READ_CHAR_PENDING_START() {return cbuf.start;}
 
     public void READ_CHECK(ThreadContext context) {
         if (!READ_DATA_PENDING()) {
             checkClosed();
         }
     }
 
     public boolean IS_PREP_STDIO() {
         return (mode & PREP) == PREP;
     }
 
     public void setFD(ChannelFD fd) {
         this.fd = fd;
     }
 
     public void setChannel(Channel fd) {
         this.fd = new ChannelFD(fd, runtime.getPosix(), runtime.getFilenoUtil());
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
 
     public static int getModeFlagsAsIntFrom(int fmode) {
         int oflags = 0;
 
         if ((fmode & READABLE) != 0) {
             if ((fmode & WRITABLE) != 0) {
                 oflags |= ModeFlags.RDWR;
             } else {
                 oflags |= ModeFlags.RDONLY;
             }
         } else if ((fmode & WRITABLE) != 0) {
             oflags |= ModeFlags.WRONLY;
         }
 
         if ((fmode & APPEND) != 0) oflags |= ModeFlags.APPEND;
         if ((fmode & CREATE) != 0) oflags |= ModeFlags.CREAT;
         if ((fmode & BINMODE) != 0) oflags |= ModeFlags.BINARY;
         if ((fmode & TEXTMODE) != 0) oflags |= ModeFlags.TEXT;
         if ((fmode & TRUNC) != 0) oflags |= ModeFlags.TRUNC;
 
         return oflags;
     }
 
     // MRI: rb_io_oflags_modestr
     public static String ioOflagsModestr(Ruby runtime, int oflags) {
         int accmode = oflags & (OpenFlags.O_RDONLY.intValue()|OpenFlags.O_WRONLY.intValue()|OpenFlags.O_RDWR.intValue());
         if ((oflags & OpenFlags.O_APPEND.intValue()) != 0) {
             if (accmode == OpenFlags.O_WRONLY.intValue()) {
                 return MODE_BINARY(oflags, "a", "ab");
             }
             if (accmode == OpenFlags.O_RDWR.intValue()) {
                 return MODE_BINARY(oflags, "a+", "ab+");
             }
         }
         switch (OpenFlags.valueOf(oflags & (OpenFlags.O_RDONLY.intValue()|OpenFlags.O_WRONLY.intValue()|OpenFlags.O_RDWR.intValue()))) {
             default:
                 throw runtime.newArgumentError("invalid access oflags 0x" + Integer.toHexString(oflags));
             case O_RDONLY:
                 return MODE_BINARY(oflags, "r", "rb");
             case O_WRONLY:
                 return MODE_BINARY(oflags, "w", "wb");
             case O_RDWR:
                 return MODE_BINARY(oflags, "r+", "rb+");
         }
     }
 
     // MRI: rb_io_modestr_oflags
     public static int ioModestrOflags(Ruby runtime, String modestr) {
         return ioFmodeOflags(ioModestrFmode(runtime, modestr));
     }
 
     // MRI: rb_io_fmode_oflags
     public static int ioFmodeOflags(int fmode) {
         int oflags = 0;
 
         switch (fmode & OpenFile.READWRITE) {
             case OpenFile.READABLE:
                 oflags |= OpenFlags.O_RDONLY.intValue();
                 break;
             case OpenFile.WRITABLE:
                 oflags |= OpenFlags.O_WRONLY.intValue();
                 break;
             case OpenFile.READWRITE:
                 oflags |= OpenFlags.O_RDWR.intValue();
                 break;
         }
 
         if ((fmode & OpenFile.APPEND) != 0) {
             oflags |= OpenFlags.O_APPEND.intValue();
         }
         if ((fmode & OpenFile.TRUNC) != 0) {
             oflags |= OpenFlags.O_TRUNC.intValue();
         }
         if ((fmode & OpenFile.CREATE) != 0) {
             oflags |= OpenFlags.O_CREAT.intValue();
         }
         if (OpenFlags.O_BINARY.defined()) {
             if ((fmode & OpenFile.BINMODE) != 0) {
                 oflags |= OpenFlags.O_BINARY.intValue();
             }
         }
 
         return oflags;
     }
 
     public static int ioModestrFmode(Ruby runtime, String modestr) {
         int fmode = 0;
         char[] mChars = modestr.toCharArray(), pChars = null;
         int m = 0, p = 0;
 
         if (mChars.length == 0) throw runtime.newArgumentError("invalid access mode " + modestr);
 
         switch (mChars[m++]) {
             case 'r':
                 fmode |= OpenFile.READABLE;
                 break;
             case 'w':
                 fmode |= OpenFile.WRITABLE | OpenFile.TRUNC | OpenFile.CREATE;
                 break;
             case 'a':
                 fmode |= OpenFile.WRITABLE | OpenFile.APPEND | OpenFile.CREATE;
                 break;
             default:
                 throw runtime.newArgumentError("invalid access mode " + modestr);
         }
 
         loop: while (m < mChars.length) {
             switch (mChars[m++]) {
                 case 'b':
                     fmode |= OpenFile.BINMODE;
                     break;
                 case 't':
                     fmode |= OpenFile.TEXTMODE;
                     break;
                 case '+':
                     fmode |= OpenFile.READWRITE;
                     break;
                 default:
                     throw runtime.newArgumentError("invalid access mode " + modestr);
                 case ':':
                     pChars = mChars;
                     p = m;
                     if ((fmode & OpenFile.BINMODE) != 0 && (fmode & OpenFile.TEXTMODE) != 0)
                         throw runtime.newArgumentError("invalid access mode " + modestr);
                     break loop;
             }
         }
 
         if ((fmode & OpenFile.BINMODE) != 0 && (fmode & OpenFile.TEXTMODE) != 0)
             throw runtime.newArgumentError("invalid access mode " + modestr);
         if (p != 0 && ioEncnameBomP(new String(pChars, p, pChars.length - p), 0))
             fmode |= OpenFile.SETENC_BY_BOM;
 
         return fmode;
     }
 
     static boolean ioEncnameBomP(String name, long len) {
         String bom_prefix = "bom|utf-";
         int bom_prefix_len = bom_prefix.length();
         if (len == 0) {
             int p = name.indexOf(':');
             len = p != -1 ? p : name.length();
         }
         return len > bom_prefix_len && name.compareToIgnoreCase(bom_prefix) == 0;
     }
 
     private static String MODE_BINARY(int oflags, String a, String b) {
         if (OpenFlags.O_BINARY.defined() && (oflags & OpenFlags.O_BINARY.intValue()) != 0) {
             return b;
         }
         return a;
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
 
     // rb_io_check_char_readable
     public void checkCharReadable(ThreadContext context) {
         checkClosed();
 
         if ((mode & READABLE) == 0) {
             throw runtime.newIOError("not opened for reading");
         }
 
         if (wbuf.len != 0) {
             if (io_fflush(context) < 0) {
                 throw runtime.newErrnoFromErrno(posix.errno, "error flushing");
             }
         }
         if (tiedIOForWriting != null) {
             OpenFile wfptr;
             wfptr = tiedIOForWriting.getOpenFileChecked();
             if (wfptr.io_fflush(context) < 0)
                 throw runtime.newErrnoFromErrno(wfptr.posix.errno, wfptr.getPath());
         }
     }
 
     // rb_io_check_byte_readable
     public void checkByteReadable(ThreadContext context) {
         checkCharReadable(context);
         if (READ_CHAR_PENDING()) {
             throw runtime.newIOError("byte oriented read for character buffered IO");
         }
     }
 
     // rb_io_check_readable
     public void checkReadable(ThreadContext context) {
         checkByteReadable(context);
     }
 
     // io_fflush
     public int io_fflush(ThreadContext context) {
         boolean locked = lock();
         try {
             checkClosed();
 
             if (wbuf.len == 0) return 0;
 
             checkClosed();
 
             while (wbuf.len > 0 && flushBuffer() != 0) {
                 if (!waitWritable(context)) {
                     return -1;
                 }
                 checkClosed();
             }
         } finally {
             if (locked) unlock();
         }
 
         return 0;
     }
 
     // rb_io_wait_writable
     public boolean waitWritable(ThreadContext context, long timeout) {
         boolean locked = lock();
         try {
             if (posix.errno == null) return false;
 
             checkClosed();
 
             switch (posix.errno) {
                 case EINTR:
                     //            case ERESTART: // not defined in jnr-constants
                     runtime.getCurrentContext().pollThreadEvents();
                     return true;
                 case EAGAIN:
                 case EWOULDBLOCK:
                     ready(runtime, context.getThread(), SelectExecutor.WRITE_CONNECT_OPS, timeout);
                     return true;
                 default:
                     return false;
             }
         } finally {
             if (locked) unlock();
         }
     }
 
     // rb_io_wait_writable
     public boolean waitWritable(ThreadContext context) {
         return waitWritable(context, 0);
     }
 
     // rb_io_wait_readable
     public boolean waitReadable(ThreadContext context, long timeout) {
         boolean locked = lock();
         try {
             if (posix.errno == null) return false;
 
             checkClosed();
 
             switch (posix.errno) {
                 case EINTR:
                     //            case ERESTART: // not defined in jnr-constants
                     runtime.getCurrentContext().pollThreadEvents();
                     return true;
                 case EAGAIN:
                 case EWOULDBLOCK:
                     ready(runtime, context.getThread(), SelectionKey.OP_READ, timeout);
                     return true;
                 default:
                     return false;
             }
         } finally {
             if (locked) unlock();
         }
     }
 
     // rb_io_wait_readable
     public boolean waitReadable(ThreadContext context) {
         return waitReadable(context, 0);
     }
 
     /**
      * Wait until the channel is available for the given operations or the timeout expires.
      *
      * @see org.jruby.RubyThread#select(java.nio.channels.Channel, OpenFile, int, long)
      *
      * @param runtime
      * @param ops
      * @param timeout
      * @return
      */
     public boolean ready(Ruby runtime, RubyThread thread, int ops, long timeout) {
         boolean locked = lock();
         try {
             if (fd.chSelect != null) {
                 return thread.select(fd.chSelect, this, ops & fd.chSelect.validOps(), timeout);
 
             } else if (fd.chSeek != null) {
                 return fd.chSeek.position() != -1
                         && fd.chSeek.size() != -1
                         && fd.chSeek.position() < fd.chSeek.size();
             }
 
             return false;
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
         } finally {
             if (locked) unlock();
         }
     }
 
     /**
      * Like {@link OpenFile#ready(org.jruby.Ruby, org.jruby.RubyThread, int, long)} but returns a result
      * immediately.
      *
      * @param context
      * @return
      */
     public boolean readyNow(ThreadContext context) {
         return ready(context.runtime, context.getThread(), SelectionKey.OP_READ, 0);
     }
 
     // io_flush_buffer
     public int flushBuffer() {
         if (write_lock != null) {
             write_lock.writeLock().lock();
             try {
                 return flushBufferAsync2();
             } finally {
                 write_lock.writeLock().unlock();
             }
         }
         return flushBufferAsync2();
     }
 
     // io_flush_buffer_async2
     public int flushBufferAsync2() {
         // GVL-free call to io_flush_buffer_sync2 here in MRI
 
         return flushBufferSync2();
 
         // logic after here was to interpret the retval of rb_thread_call_without_gvl2
     }
 
     // io_flush_buffer_sync2
     private int flushBufferSync2() {
         int result = flushBufferSync();
 
         return result;
 //        return result == 0 ? 1 : result;
     }
 
     // io_flush_buffer_sync
     private int flushBufferSync() {
         int l = writableLength(wbuf.len);
         int r = posix.write(fd, wbuf.ptr, wbuf.off, l, nonblock);
 
         if (wbuf.len <= r) {
             wbuf.off = 0;
             wbuf.len = 0;
             return 0;
         }
         if (0 <= r) {
             wbuf.off += (int)r;
             wbuf.len -= (int)r;
             posix.errno = Errno.EAGAIN;
         }
         return -1;
     }
 
     // io_writable_length
     private int writableLength(int l) {
         // We don't use wsplit mode, so we just pass length back directly.
 //        if (PIPE_BUF < l &&
 //                // we should always assume other threads, so we don't use rb_thread_alone
 ////                !rb_thread_alone() &&
 //                wsplit()) {
 //            l = PIPE_BUF;
 //        }
         return l;
     }
 
     /**
      * wsplit mode selects a smaller write size based on the internal buffer of
      * things like pipes, in order to help guarantee it will not block when
      * emptying our write buffer. This must be guaranteed to allow MRI to re-try
      * flushing the rest of the buffer with the GVL released, which happens when
      * flushBufferSync above produces EAGAIN.
      *
      * In JRuby, where we don't have to release a lock, we skip this logic and
      * always just let writes do what writes do.
      *
      * MRI: wsplit_p
      * @return
      */
     // wsplit_p
     private boolean wsplit()
     {
         int r;
 
 //        if ((mode & WSPLIT_INITIALIZED) == 0) {
 ////            struct stat buf;
 //            if (fd.chFile == null
 ////            if (fstat(fptr->fd, &buf) == 0 &&
 ////                    !S_ISREG(buf.st_mode)
 ////                    && (r = fcntl(fptr->fd, F_GETFL)) != -1 &&
 ////                    !(r & O_NONBLOCK)
 //            ) {
 //                mode |= WSPLIT;
 //            }
 //            mode |= WSPLIT_INITIALIZED;
 //        }
         return (mode & WSPLIT) != 0;
     }
 
     // io_seek
     public long seek(ThreadContext context, long offset, int whence) {
         boolean locked = lock();
         try {
             flushBeforeSeek(context);
             return posix.lseek(fd, offset, whence);
         } finally {
             if (locked) unlock();
         }
     }
 
     // flush_before_seek
     private void flushBeforeSeek(ThreadContext context) {
         boolean locked = lock();
         try {
             if (io_fflush(context) < 0)
                 throw context.runtime.newErrnoFromErrno(posix.errno, "");
             unread(context);
             posix.errno = null;
         } finally {
             if (locked) unlock();
         }
     }
 
     public void checkWritable(ThreadContext context) {
         boolean locked = lock();
         try {
             checkClosed();
             if ((mode & WRITABLE) == 0) {
                 throw context.runtime.newIOError("not opened for writing");
             }
             if (rbuf.len != 0) {
                 unread(context);
             }
         } finally {
             if (locked) unlock();
         }
     }
 
     public void checkClosed() {
         if (fd == null) {
             throw runtime.newIOError(RubyIO.CLOSED_STREAM_MSG);
         }
     }
 
     public boolean isBinmode() {
         return (mode & BINMODE) != 0;
     }
 
     public boolean isTextMode() {
         return (mode & TEXTMODE) != 0;
     }
 
     public void setTextMode() {
         mode |= TEXTMODE;
     }
 
     public void clearTextMode() {
         mode &= ~TEXTMODE;
     }
 
     public void setBinmode() {
         mode |= BINMODE;
     }
 
     public boolean isOpen() {
         return fd != null;
     }
 
     public boolean isReadable() {
         return (mode & READABLE) != 0;
     }
 
     public boolean isWritable() {
         return (mode & WRITABLE) != 0;
     }
 
     public boolean isDuplex() {
         return (mode & DUPLEX) != 0;
     }
 
     public boolean isReadBuffered() {
         return READ_DATA_BUFFERED();
     }
 
     public boolean isWriteBuffered() {
         return false;
     }
 
     public void setSync(boolean sync) {
         boolean locked = lock();
         try {
             if (sync) {
                 mode = mode | SYNC;
             } else {
                 mode = mode & ~SYNC;
             }
         } finally {
             if (locked) unlock();
         }
     }
 
     public boolean isSync() {
         return (mode & (SYNC | TTY)) != 0;
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
         if (pid != -1) return pid;
 
         return ShellLauncher.getPidFromProcess(process);
     }
 
     public void setPid(long pid) {
         this.pid = pid;
     }
 
     public int getLineNumber() {
         return lineno;
     }
 
     public void setLineNumber(int lineNumber) {
         this.lineno = lineNumber;
     }
 
     public String getPath() {
         return pathv;
     }
 
     public void setPath(String path) {
         this.pathv = path;
     }
 
     public boolean isAutoclose() {
         return (mode & PREP) == 0;
     }
 
     public void setAutoclose(boolean autoclose) {
         boolean locked = lock();
         try {
             if (!autoclose)
                 mode |= PREP;
             else
                 mode &= ~PREP;
         } finally {
             if (locked) unlock();
         }
     }
 
     public Finalizer getFinalizer() {
         return finalizer;
     }
 
     public void setFinalizer(Finalizer finalizer) {
         this.finalizer = finalizer;
     }
 
     public void cleanup(Ruby runtime, boolean noraise) {
         boolean locked = lock();
         try {
             if (finalizer != null) {
                 finalizer.finalize(runtime, this, noraise);
             } else {
                 finalize(runtime.getCurrentContext(), noraise);
             }
         } finally {
             if (locked) unlock();
         }
     }
 
     public static final Finalizer PIPE_FINALIZE = new Finalizer() {
         @Override
         public void finalize(Ruby runtime, OpenFile fptr, boolean noraise) {
             if (!Platform.IS_WINDOWS) { // #if !defined(HAVE_FORK) && !defined(_WIN32)
                 int status = 0;
                 if (fptr.stdio_file != null) {
                     // unsure how to do this
 //                    status = pclose(fptr->stdio_file);
                     fptr.posix.close(fptr.stdio_file);
                 }
                 fptr.setFD(null);
                 fptr.stdio_file = null;
                 // no status from above, so can't really do this
 //                runtime.getCurrentContext().setLastExitStatus();
             } else {
                 fptr.finalize(runtime.getCurrentContext(), noraise);
             }
 //            pipe_del_fptr(fptr);
         }
     };
 
     public void finalize() {
         if (fd != null && isAutoclose()) finalize(runtime.getCurrentContext(), true);
     }
 
     public void finalize(ThreadContext context, boolean noraise) {
         IRubyObject err = runtime.getNil();
         ChannelFD fd = this.fd();
         Closeable stdio_file = this.stdio_file;
 
         if (writeconv != null) {
             if (write_lock != null && !noraise) {
                 // TODO: interruptible version
                 write_lock.writeLock().lock();
                 try {
                     finishWriteconv(context, noraise);
                 } finally {
                     write_lock.writeLock().unlock();
                 }
             }
             else {
                 err = finishWriteconv(context, noraise);
             }
         }
         if (wbuf.len != 0) {
             if (noraise) {
                 if (flushBufferSync() < 0 && err.isNil())
                     err = runtime.getTrue();
             }
             else {
                 if (io_fflush(runtime.getCurrentContext()) < 0 && err.isNil()) {
                     err = RubyFixnum.newFixnum(runtime, posix.errno == null ? 0 :posix.errno.longValue());
                 }
             }
         }
 
         this.fd = null;
         this.clearStdio();
         mode &= ~(READABLE|WRITABLE);
 
         if (IS_PREP_STDIO() || isStdio()) {
 	        /* need to keep FILE objects of stdin, stdout and stderr */
         } else if (stdio_file != null) {
 	        /* stdio_file is deallocated anyway
              * even if fclose failed.  */
             if (posix.close(stdio_file) < 0 && err.isNil())
                 err = noraise ? runtime.getTrue() : RubyNumeric.int2fix(runtime, posix.errno.intValue());
         } else if (fd != null) {
             /* fptr->fd may be closed even if close fails.
              * POSIX doesn't specify it.
              * We assumes it is closed.  */
             if ((posix.close(fd) < 0) && err.isNil())
                 err = noraise ? runtime.getTrue() : runtime.newFixnum(posix.errno.intValue());
         }
 
         if (!err.isNil() && !noraise) {
             if (err instanceof RubyFixnum || err instanceof RubyBignum) {
                 posix.errno = Errno.valueOf(RubyNumeric.num2int(err));
                 throw runtime.newErrnoFromErrno(posix.errno, pathv);
             } else {
                 throw new RaiseException((RubyException)err);
             }
         }
     }
 
     // MRI: NEED_READCONV
     public boolean needsReadConversion() {
         return Platform.IS_WINDOWS ?
                 (encs.enc2 != null || (encs.ecflags & ~EConvFlags.CRLF_NEWLINE_DECORATOR) != 0) || isTextMode()
                 :
                 (encs.enc2 != null || NEED_NEWLINE_DECORATOR_ON_READ());
     }
 
     // MRI: NEED_WRITECONV
     public boolean needsWriteConversion(ThreadContext context) {
         Encoding ascii8bit = context.runtime.getEncodingService().getAscii8bitEncoding();
 
         return Platform.IS_WINDOWS ?
                 ((encs.enc != null && encs.enc != ascii8bit) || (encs.ecflags & ((EConvFlags.DECORATOR_MASK & ~EConvFlags.CRLF_NEWLINE_DECORATOR)|EConvFlags.STATEFUL_DECORATOR_MASK)) != 0)
                 :
                 ((encs.enc != null && encs.enc != ascii8bit) || NEED_NEWLINE_DECORATOR_ON_WRITE() || (encs.ecflags & (EConvFlags.DECORATOR_MASK|EConvFlags.STATEFUL_DECORATOR_MASK)) != 0);
     }
 
     // MRI: make_readconv
     public void makeReadConversion(ThreadContext context, int size) {
         if (readconv == null) {
             int ecflags;
             IRubyObject ecopts;
             byte[] sname, dname;
             ecflags = encs.ecflags & ~EConvFlags.NEWLINE_DECORATOR_WRITE_MASK;
             ecopts = encs.ecopts;
             if (encs.enc2 != null) {
                 sname = encs.enc2.getName();
                 dname = encs.enc.getName();
             }
             else {
                 sname = dname = EMPTY_BYTE_ARRAY;
             }
             readconv = EncodingUtils.econvOpenOpts(context, sname, dname, ecflags, ecopts);
             if (readconv == null)
                 throw EncodingUtils.econvOpenExc(context, sname, dname, ecflags);
             cbuf.off = 0;
             cbuf.len = 0;
             if (size < IO_CBUF_CAPA_MIN) size = IO_CBUF_CAPA_MIN;
             cbuf.capa = size;
             cbuf.ptr = new byte[cbuf.capa];
         }
     }
 
     public void makeReadConversion(ThreadContext context) {
         makeReadConversion(context, IO_CBUF_CAPA_MIN);
     }
 
     // MRI: make_writeconv
     public void makeWriteConversion(ThreadContext context) {
         if (writeconvInitialized) return;
 
         byte[] senc;
         byte[] denc;
         Encoding enc;
         int ecflags;
         IRubyObject ecopts;
 
         writeconvInitialized = true;
 
         ecflags = encs.ecflags & ~EConvFlags.NEWLINE_DECORATOR_READ_MASK;
         ecopts = encs.ecopts;
 
         Encoding ascii8bit = context.runtime.getEncodingService().getAscii8bitEncoding();
         if (encs.enc == null || (encs.enc == ascii8bit && encs.enc2 == null)) {
             /* no encoding conversion */
             writeconvPreEcflags = 0;
             writeconvPreEcopts = context.nil;
             writeconv = EncodingUtils.econvOpenOpts(context, EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, ecflags, ecopts);
             if (writeconv == null) {
                 throw EncodingUtils.econvOpenExc(context, EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, ecflags);
             }
             writeconvAsciicompat = context.nil;
         }
         else {
             enc = encs.enc2 != null ? encs.enc2 : encs.enc;
             Encoding tmpEnc = EncodingUtils.econvAsciicompatEncoding(enc);
             senc = tmpEnc == null ? null : tmpEnc.getName();
             if (senc == null && (encs.ecflags & EConvFlags.STATEFUL_DECORATOR_MASK) == 0) {
                 /* single conversion */
                 writeconvPreEcflags = ecflags;
                 writeconvPreEcopts = ecopts;
                 writeconv = null;
                 writeconvAsciicompat = context.nil;
             }
             else {
                 /* double conversion */
                 writeconvPreEcflags = ecflags & ~EConvFlags.STATEFUL_DECORATOR_MASK;
                 writeconvPreEcopts = ecopts;
                 if (senc != null) {
                     denc = enc.getName();
                     writeconvAsciicompat = RubyString.newString(context.runtime, senc);
                 }
                 else {
                     senc = denc = EMPTY_BYTE_ARRAY;
                     writeconvAsciicompat = RubyString.newString(context.runtime, enc.getName());
                 }
                 ecflags = encs.ecflags & (EConvFlags.ERROR_HANDLER_MASK | EConvFlags.STATEFUL_DECORATOR_MASK);
                 ecopts = encs.ecopts;
                 writeconv = EncodingUtils.econvOpenOpts(context, senc, denc, ecflags, ecopts);
                 if (writeconv == null) {
                     throw EncodingUtils.econvOpenExc(context, senc, denc, ecflags);
                 }
             }
         }
     }
 
     public void clearReadConversion() {
         readconv = null;
     }
 
     public void clearCodeConversion() {
         readconv = null;
         writeconv = null;
     }
 
     public static final int MORE_CHAR_SUSPENDED = 0;
     public static final int MORE_CHAR_FINISHED = 1;
     public static final int EOF = -1;
 
     public static final int IO_RBUF_CAPA_MIN = 8192;
     public static final int IO_CBUF_CAPA_MIN = (128*1024);
     public int IO_RBUF_CAPA_FOR() {
         return needsReadConversion() ? IO_CBUF_CAPA_MIN : IO_RBUF_CAPA_MIN;
     }
     public static final int IO_WBUF_CAPA_MIN = 8192;
 
     private static final byte[] EMPTY_BYTE_ARRAY = ByteList.NULL_ARRAY;
 
     // MRI: appendline
     public int appendline(ThreadContext context, int delim, ByteList[] strp, int[] lp) {
         ByteList str = strp[0];
         int limit = lp[0];
 
         if (needsReadConversion()) {
             SET_BINARY_MODE();
             makeReadConversion(context);
             do {
                 int p, e;
                 int searchlen = READ_CHAR_PENDING_COUNT();
                 if (searchlen > 0) {
                     byte[] pBytes = READ_CHAR_PENDING_PTR();
                     p = READ_CHAR_PENDING_OFF();
                     if (0 < limit && limit < searchlen)
                         searchlen = limit;
                     e = memchr(pBytes, p, delim, searchlen);
                     if (e != -1) {
                         int len = (int)(e-p+1);
                         if (str == null) {
                             strp[0] = str = new ByteList(pBytes, p, len);
                         } else {
                             str.append(pBytes, p, len);
                         }
                         cbuf.off += len;
                         cbuf.len -= len;
                         limit -= len;
                         lp[0] = limit;
                         return delim;
                     }
 
                     if (str == null) {
                         strp[0] = str = new ByteList(pBytes, p, searchlen);
                     } else {
                         EncodingUtils.rbStrBufCat(context.runtime, str, pBytes, p, searchlen);
                     }
                     cbuf.off += searchlen;
                     cbuf.len -= searchlen;
                     limit -= searchlen;
 
                     if (limit == 0) {
                         lp[0] = limit;
                         return str.get(str.getRealSize() - 1) & 0xFF;
                     }
                 }
             } while (moreChar(context) != MORE_CHAR_FINISHED);
             clearReadConversion();
             lp[0] = limit;
             return EOF;
         }
 
         NEED_NEWLINE_DECORATOR_ON_READ_CHECK();
         do {
             int pending = READ_DATA_PENDING_COUNT();
             if (pending > 0) {
                 byte[] pBytes = READ_DATA_PENDING_PTR();
                 int p = READ_DATA_PENDING_OFF();
                 int e = -1;
                 int last;
 
                 if (limit > 0 && pending > limit) pending = limit;
                 e = memchr(pBytes, p, delim, pending);
                 if (e != -1) pending = e - p + 1;
                 if (str != null) {
                     last = str.getRealSize();
                     str.length(last + pending);
                 }
                 else {
                     last = 0;
                     strp[0] = str = new ByteList(pending);
                     str.setRealSize(pending);
                 }
                 readBufferedData(str.getUnsafeBytes(), str.getBegin() + last, pending); /* must not fail */
                 limit -= pending;
                 lp[0] = limit;
                 if (e != -1) return delim;
                 if (limit == 0) {
                     return str.get(str.getRealSize() - 1) & 0xFF;
                 }
             }
             READ_CHECK(context);
         } while (fillbuf(context) >= 0);
         lp[0] = limit;
         return EOF;
     }
 
     private int memchr(byte[] pBytes, int p, int delim, int length) {
         for (int i = p; i < p + length; i++) {
             if ((pBytes[i] & 0xFF) == delim) {
                 return i;
             }
         }
         return -1;
     }
 
     public void NEED_NEWLINE_DECORATOR_ON_READ_CHECK() {
         if (NEED_NEWLINE_DECORATOR_ON_READ()) {
             if (isReadable() &&
                     (encs.ecflags & EConvFlags.NEWLINE_DECORATOR_MASK) == 0) {
                 SET_BINARY_MODE();
             } else {
                 SET_TEXT_MODE();
             }
         }
     }
 
     public boolean NEED_NEWLINE_DECORATOR_ON_READ() {
         return isTextMode();
     }
 
     public boolean NEED_NEWLINE_DECORATOR_ON_WRITE() {
         return isTextMode();
     }
 
     public int moreChar(ThreadContext context) {
         Object v;
         v = fillCbuf(context, EConvFlags.AFTER_OUTPUT);
         if (!(v instanceof Integer) ||
                 ((Integer)v != MORE_CHAR_SUSPENDED && (Integer)v != MORE_CHAR_FINISHED))
             throw (RaiseException)v;
         return (Integer)v;
     }
 
     private Object fillCbuf(ThreadContext context, int ec_flags) {
         int ss, se;
         int ds, de;
         EConvResult res;
         int putbackable;
         int cbuf_len0;
         RaiseException exc;
 
         ec_flags |= EConvFlags.PARTIAL_INPUT;
 
         boolean locked = lock();
         try {
             if (cbuf.len == cbuf.capa)
                 return MORE_CHAR_SUSPENDED; /* cbuf full */
             if (cbuf.len == 0)
                 cbuf.off = 0;
             else if (cbuf.off + cbuf.len == cbuf.capa) {
                 System.arraycopy(cbuf.ptr, cbuf.off, cbuf.ptr, 0, cbuf.len);
                 cbuf.off = 0;
             }
 
             cbuf_len0 = cbuf.len;
 
             Ptr spPtr = new Ptr();
             Ptr dpPtr = new Ptr();
 
             while (true) {
                 ss = spPtr.p = rbuf.off;
                 se = spPtr.p + rbuf.len;
                 ds = dpPtr.p = cbuf.off + cbuf.len;
                 de = cbuf.capa;
                 res = readconv.convert(rbuf.ptr, spPtr, se, cbuf.ptr, dpPtr, de, ec_flags);
                 rbuf.off += (int) (spPtr.p - ss);
                 rbuf.len -= (int) (spPtr.p - ss);
                 cbuf.len += (int) (dpPtr.p - ds);
 
                 putbackable = readconv.putbackable();
                 if (putbackable != 0) {
                     readconv.putback(rbuf.ptr, rbuf.off - putbackable, putbackable);
                     rbuf.off -= putbackable;
                     rbuf.len += putbackable;
                 }
 
                 exc = EncodingUtils.makeEconvException(context.runtime, readconv);
                 if (exc != null)
                     return exc;
 
                 if (cbuf_len0 != cbuf.len)
                     return MORE_CHAR_SUSPENDED;
 
                 if (res == EConvResult.Finished) {
                     return MORE_CHAR_FINISHED;
                 }
 
                 if (res == EConvResult.SourceBufferEmpty) {
                     if (rbuf.len == 0) {
                         READ_CHECK(context);
                         if (fillbuf(context) == -1) {
                             if (readconv == null) {
                                 return MORE_CHAR_FINISHED;
                             }
                             ds = dpPtr.p = cbuf.off + cbuf.len;
                             de = cbuf.capa;
                             res = readconv.convert(null, null, 0, cbuf.ptr, dpPtr, de, 0);
                             cbuf.len += (int) (dpPtr.p - ds);
                             EncodingUtils.econvCheckError(context, readconv);
                             break;
                         }
                     }
                 }
             }
             if (cbuf_len0 != cbuf.len)
                 return MORE_CHAR_SUSPENDED;
         } finally {
             if (locked) unlock();
         }
 
         return MORE_CHAR_FINISHED;
     }
 
     // read_buffered_data
     public int readBufferedData(byte[] ptrBytes, int ptr, int len) {
         boolean locked = lock();
         try {
             int n = rbuf.len;
 
             if (n <= 0) return n;
             if (n > len) n = len;
             System.arraycopy(rbuf.ptr, rbuf.start + rbuf.off, ptrBytes, ptr, n);
             rbuf.off += n;
             rbuf.len -= n;
             return n;
         } finally {
             if (locked) unlock();
         }
     }
 
     // io_fillbuf
     public int fillbuf(ThreadContext context) {
         int r;
 
         boolean locked = lock();
         try {
             if (rbuf.ptr == null) {
                 rbuf.off = 0;
                 rbuf.len = 0;
                 rbuf.capa = IO_RBUF_CAPA_FOR();
                 rbuf.ptr = new byte[rbuf.capa];
                 if (Platform.IS_WINDOWS) {
                     rbuf.capa--;
                 }
             }
             if (rbuf.len == 0) {
                 retry:
                 while (true) {
                     r = readInternal(context, this, fd, rbuf.ptr, 0, rbuf.capa);
 
                     if (r < 0) {
                         if (waitReadable(context, fd)) {
                             continue retry;
                         }
                         throw context.runtime.newErrnoFromErrno(posix.errno, "channel: " + fd + (pathv != null ? " " + pathv : ""));
                     }
                     break;
                 }
                 rbuf.off = 0;
                 rbuf.len = (int) r; /* r should be <= rbuf_capa */
                 if (r == 0)
                     return -1; /* EOF */
             }
         } finally {
             if (locked) unlock();
         }
         return 0;
     }
 
     public static class InternalReadStruct {
         InternalReadStruct(OpenFile fptr, ChannelFD fd, byte[] bufBytes, int buf, int count) {
             this.fptr = fptr;
             this.fd = fd;
             this.bufBytes = bufBytes;
             this.buf = buf;
             this.capa = count;
         }
 
         public OpenFile fptr;
         public ChannelFD fd;
         public byte[] bufBytes;
         public int buf;
         public int capa;
         public Selector selector;
     }
 
     final static RubyThread.Task<InternalReadStruct, Integer> readTask = new RubyThread.Task<InternalReadStruct, Integer>() {
         @Override
         public Integer run(ThreadContext context, InternalReadStruct iis) throws InterruptedException {
             ChannelFD fd = iis.fd;
             OpenFile fptr = iis.fptr;
 
             assert fptr.lockedByMe();
 
             fptr.unlock();
             try {
                 return fptr.posix.read(fd, iis.bufBytes, iis.buf, iis.capa, fptr.nonblock);
             } finally {
                 fptr.lock();
             }
         }
 
         @Override
         public void wakeup(RubyThread thread, InternalReadStruct data) {
             thread.getNativeThread().interrupt();
         }
     };
 
     final static RubyThread.Task<InternalWriteStruct, Integer> writeTask = new RubyThread.Task<InternalWriteStruct, Integer>() {
         @Override
         public Integer run(ThreadContext context, InternalWriteStruct iis) throws InterruptedException {
             OpenFile fptr = iis.fptr;
 
             assert fptr.lockedByMe();
 
             fptr.unlock();
             try {
                 return iis.fptr.posix.write(iis.fd, iis.bufBytes, iis.buf, iis.capa, iis.fptr.nonblock);
             } finally {
                 fptr.lock();
             }
         }
 
         @Override
         public void wakeup(RubyThread thread, InternalWriteStruct data) {
             // FIXME: NO! This will kill many native channels. Must be nonblocking to interrupt.
             thread.getNativeThread().interrupt();
         }
     };
 
     // rb_read_internal
     public static int readInternal(ThreadContext context, OpenFile fptr, ChannelFD fd, byte[] bufBytes, int buf, int count) {
         InternalReadStruct iis = new InternalReadStruct(fptr, fd, bufBytes, buf, count);
 
         // if we can do selection and this is not a non-blocking call, do selection
 
         /*
             NOTE CON: We only do this selection because on the JDK, blocking calls to NIO channels can't be
             interrupted, and we need to be able to interrupt blocking reads. In MRI, this logic is always just a
             simple read(2) because EINTR does not damage the descriptor.
 
             Doing selects here on ENXIO native channels caused FIFOs to block for read all the time, even when no
             writers are connected. This may or may not be correct behavior for selects against FIFOs, but in any
             case since MRI does not do a select here at all I believe correct logic is to skip the select when
             working with any native descriptor.
          */
 
         fptr.unlock();
         try {
             if (fd.chSelect != null
                     && fd.chNative == null // MRI does not select for rb_read_internal on native descriptors
                     && !iis.fptr.nonblock) {
                 context.getThread().select(fd.chSelect, fptr, SelectionKey.OP_READ);
             }
         } finally {
             fptr.lock();
         }
 
         try {
             return context.getThread().executeTask(context, iis, readTask);
         } catch (InterruptedException ie) {
             throw context.runtime.newConcurrencyError("IO operation interrupted");
         }
     }
 
     /**
      * Logic to match (as well as possible) rb_io_wait_readable from MRI. We do not
      * have the luxury of treating all file descriptors the same, so there's a bit
      * of special-casing here when the channel is not selectable.
      *
      * Note also the EBADF on closed channels; I believe this is what *would*
      * happen in MRI if we always called the selection logic and were given a
      * closed channel.
      *
      * MRI: rb_io_wait_readable
      */
     boolean waitReadable(ThreadContext context, ChannelFD fd) {
         checkClosed();
 
         boolean locked = lock();
         try {
             if (!fd.ch.isOpen()) {
                 posix.errno = Errno.EBADF;
                 return false;
             }
 
             if (fd.chSelect != null) {
                 unlock();
                 try {
                     return context.getThread().select(fd.chSelect, this, SelectionKey.OP_READ);
                 } finally {
                     lock();
                 }
             }
 
             /*
             Seekable channels (usually FileChannel) are treated as ready always. There are
             three kinds we typically see:
             1. files, which always select(2) as ready
             2. stdio, which we can't select and can't check .size for available data
             3. subprocess stdio, which we can't select and can't check .size either
             In all three cases, without native fd logic, we can't do anything to determine
             if the stream is ready, so we just assume it is and hope for the best.
              */
             if (fd.chSeek != null) {
                 return true;
             }
         } finally {
             if (locked) unlock();
         }
 
         // we can't determine if it is readable
         return false;
 
         /*
         switch (errno) {
             case EINTR:
                 #if defined(ERESTART)
             case ERESTART:
                 #endif
                 rb_thread_check_ints();
                 return TRUE;
 
             case EAGAIN:
                 #if defined(EWOULDBLOCK) && EWOULDBLOCK != EAGAIN
             case EWOULDBLOCK:
                 #endif
                 rb_thread_wait_fd(f);
                 return TRUE;
 
             default:
                 return FALSE;
         }*/
     }
 
     // io_read_encoding
     public Encoding readEncoding(Ruby runtime) {
         return encs.enc != null ? encs.enc : EncodingUtils.defaultExternalEncoding(runtime);
     }
 
     // io_input_encoding
     public Encoding inputEncoding(Ruby runtime) {
         return encs.enc2 != null ? encs.enc2 : readEncoding(runtime);
     }
 
     // swallow
     public boolean swallow(ThreadContext context, int term) {
         Ruby runtime = context.runtime;
 
         boolean locked = lock();
         try {
             if (needsReadConversion()) {
                 Encoding enc = readEncoding(runtime);
                 boolean needconv = enc.minLength() != 1;
                 SET_BINARY_MODE();
                 makeReadConversion(context);
                 do {
                     int cnt;
                     int[] i = {0};
                     while ((cnt = READ_CHAR_PENDING_COUNT()) > 0) {
                         byte[] pBytes = READ_CHAR_PENDING_PTR();
                         int p = READ_CHAR_PENDING_OFF();
                         i[0] = 0;
                         if (!needconv) {
                             if (pBytes[p] != term) return true;
                             i[0] = (int) cnt;
                             while ((--i[0] != 0) && pBytes[++p] == term) ;
                         } else {
                             int e = p + cnt;
                             if (EncodingUtils.encAscget(pBytes, p, e, i, enc) != term) return true;
                             while ((p += i[0]) < e && EncodingUtils.encAscget(pBytes, p, e, i, enc) == term) ;
                             i[0] = (int) (e - p);
                         }
                         shiftCbuf(context, (int) cnt - i[0], null);
                     }
                 } while (moreChar(context) != MORE_CHAR_FINISHED);
                 return false;
             }
 
             NEED_NEWLINE_DECORATOR_ON_READ_CHECK();
             do {
                 int cnt;
                 while ((cnt = READ_DATA_PENDING_COUNT()) > 0) {
                     byte[] buf = new byte[1024];
                     byte[] pBytes = READ_DATA_PENDING_PTR();
                     int p = READ_DATA_PENDING_OFF();
                     int i;
                     if (cnt > buf.length) cnt = buf.length;
                     if ((pBytes[p] & 0xFF) != term) return true;
                     i = (int) cnt;
                     while (--i != 0 && (pBytes[++p] & 0xFF) == term) ;
                     if (readBufferedData(buf, 0, cnt - i) == 0) /* must not fail */
                         throw context.runtime.newRuntimeError("failure copying buffered IO bytes");
                 }
                 READ_CHECK(context);
             } while (fillbuf(context) == 0);
         } finally {
             if (locked) unlock();
         }
 
         return false;
     }
 
     // io_shift_cbuf
     public IRubyObject shiftCbuf(ThreadContext context, final int len, final IRubyObject strp) {
         boolean locked = lock();
         try {
             IRubyObject str = null;
             if (strp != null) {
                 str = strp;
                 if (str.isNil()) {
                     str = RubyString.newString(context.runtime, cbuf.ptr, cbuf.off, len);
                 } else {
                     ((RubyString) str).cat(cbuf.ptr, cbuf.off, len);
                 }
                 str.setTaint(true);
                 EncodingUtils.encAssociateIndex(str, encs.enc);
             }
             cbuf.off += len;
             cbuf.len -= len;
             /* xxx: set coderange */
             if (cbuf.len == 0)
                 cbuf.off = 0;
             else if (cbuf.capa / 2 < cbuf.off) {
                 System.arraycopy(cbuf.ptr, cbuf.off, cbuf.ptr, 0, cbuf.len);
                 cbuf.off = 0;
             }
             return str;
         } finally {
             if (locked) unlock();
         }
     }
 
     // rb_io_getline_fast
     public IRubyObject getlineFast(ThreadContext context, Encoding enc, RubyIO io) {
         Ruby runtime = context.runtime;
         IRubyObject str = null;
         ByteList strByteList;
         int len = 0;
         int pos = 0;
         int cr = 0;
 
         boolean locked = lock();
         try {
             do {
                 int pending = READ_DATA_PENDING_COUNT();
 
                 if (pending > 0) {
                     byte[] pBytes = READ_DATA_PENDING_PTR();
                     int p = READ_DATA_PENDING_OFF();
                     int e;
 
                     e = memchr(pBytes, p, '\n', pending);
                     if (e != -1) {
                         pending = (int) (e - p + 1);
                     }
                     if (str == null) {
                         str = RubyString.newString(runtime, pBytes, p, pending);
                         strByteList = ((RubyString) str).getByteList();
                         rbuf.off += pending;
                         rbuf.len -= pending;
                     } else {
                         ((RubyString) str).resize(len + pending);
                         strByteList = ((RubyString) str).getByteList();
                         readBufferedData(strByteList.unsafeBytes(), strByteList.begin() + len, pending);
                     }
                     len += pending;
                     if (cr != StringSupport.CR_BROKEN)
                         pos += StringSupport.codeRangeScanRestartable(enc, strByteList.unsafeBytes(), strByteList.begin() + pos, strByteList.begin() + len, cr);
                     if (e != -1) break;
                 }
                 READ_CHECK(context);
             } while (fillbuf(context) >= 0);
             if (str == null) return context.nil;
             str = EncodingUtils.ioEncStr(runtime, str, this);
             ((RubyString) str).setCodeRange(cr);
             incrementLineno(runtime);
         } finally {
             if (locked) unlock();
         }
 
         return str;
     }
 
     public void incrementLineno(Ruby runtime) {
         boolean locked = lock();
         try {
             lineno++;
             runtime.setCurrentLine(lineno);
             RubyArgsFile.setCurrentLineNumber(runtime.getArgsFile(), lineno);
         } finally {
             if (locked) unlock();
         }
     }
 
     // read_all, 2014-5-13
     public IRubyObject readAll(ThreadContext context, int siz, IRubyObject str) {
         Ruby runtime = context.runtime;
         int bytes;
         int n;
         int pos;
         Encoding enc;
         int cr;
 
         boolean locked = lock();
         try {
             if (needsReadConversion()) {
                 SET_BINARY_MODE();
                 str = EncodingUtils.setStrBuf(runtime, str, 0);
                 makeReadConversion(context);
                 while (true) {
                     Object v;
                     if (cbuf.len != 0) {
                         str = shiftCbuf(context, cbuf.len, str);
                     }
                     v = fillCbuf(context, 0);
                     if (!v.equals(MORE_CHAR_SUSPENDED) && !v.equals(MORE_CHAR_FINISHED)) {
                         if (cbuf.len != 0) {
                             str = shiftCbuf(context, cbuf.len, str);
                         }
                         throw (RaiseException) v;
                     }
                     if (v.equals(MORE_CHAR_FINISHED)) {
                         clearReadConversion();
                         return EncodingUtils.ioEncStr(runtime, str, this);
                     }
                 }
             }
 
             NEED_NEWLINE_DECORATOR_ON_READ_CHECK();
             bytes = 0;
             pos = 0;
 
             enc = readEncoding(runtime);
             cr = 0;
 
             if (siz == 0) siz = BUFSIZ;
             str = EncodingUtils.setStrBuf(runtime, str, siz);
             for (; ; ) {
                 READ_CHECK(context);
                 n = fread(context, str, bytes, siz - bytes);
                 if (n == 0 && bytes == 0) {
                     ((RubyString) str).resize(0);
                     break;
                 }
                 bytes += n;
                 ByteList strByteList = ((RubyString) str).getByteList();
                 strByteList.setRealSize(bytes);
                 if (cr != StringSupport.CR_BROKEN)
                     pos += StringSupport.codeRangeScanRestartable(enc, strByteList.unsafeBytes(), strByteList.begin() + pos, strByteList.begin() + bytes, cr);
                 if (bytes < siz) break;
                 siz += BUFSIZ;
                 ((RubyString) str).modify(BUFSIZ);
             }
             str = EncodingUtils.ioEncStr(runtime, str, this);
         } finally {
             if (locked) unlock();
         }
 
         ((RubyString)str).setCodeRange(cr);
 
         return str;
     }
 
     // io_bufread
     private int ioBufread(ThreadContext context, byte[] ptrBytes, int ptr, int len) {
         int offset = 0;
         int n = len;
         int c;
 
         boolean locked = lock();
         try {
             if (!READ_DATA_PENDING()) {
                 outer:
                 while (n > 0) {
                     again:
                     while (true) {
                         c = readInternal(context, this, fd, ptrBytes, ptr + offset, n);
                         if (c == 0) break outer;
                         if (c < 0) {
                             if (waitReadable(context, fd))
                                 continue again;
                             return -1;
                         }
                         break;
                     }
                     offset += c;
                     if ((n -= c) <= 0) break outer;
                 }
                 return len - n;
             }
 
             while (n > 0) {
                 c = readBufferedData(ptrBytes, ptr + offset, n);
                 if (c > 0) {
                     offset += c;
                     if ((n -= c) <= 0) break;
                 }
                 checkClosed();
                 if (fillbuf(context) < 0) {
                     break;
                 }
             }
         } finally {
             if (locked) unlock();
         }
         return len - n;
     }
 
     private static class BufreadArg {
         byte[] strPtrBytes;
         int strPtr;
         int len;
         OpenFile fptr;
     };
 
     static IRubyObject bufreadCall(ThreadContext context, BufreadArg p) {
         p.len = p.fptr.ioBufread(context, p.strPtrBytes, p.strPtr, p.len);
         return RubyBasicObject.UNDEF;
     }
 
     // io_fread
     public int fread(ThreadContext context, IRubyObject str, int offset, int size) {
         int len;
         BufreadArg arg = new BufreadArg();
 
         str = EncodingUtils.setStrBuf(context.runtime, str, offset + size);
         ByteList strByteList = ((RubyString)str).getByteList();
         arg.strPtrBytes = strByteList.unsafeBytes();
         arg.strPtr = strByteList.begin() + offset;
         arg.len = size;
         arg.fptr = this;
         // we don't support string locking
 //        rb_str_locktmp_ensure(str, bufread_call, (VALUE)&arg);
         bufreadCall(context, arg);
         len = arg.len;
         // should be errno
         if (len < 0) throw context.runtime.newErrnoFromErrno(posix.errno, pathv);
         return len;
     }
 
     public void ungetbyte(ThreadContext context, IRubyObject str) {
         int len = ((RubyString)str).size();
 
         boolean locked = lock();
         try {
             if (rbuf.ptr == null) {
                 int min_capa = IO_RBUF_CAPA_FOR();
                 rbuf.off = 0;
                 rbuf.len = 0;
                 //            #if SIZEOF_LONG > SIZEOF_INT
                 //            if (len > INT_MAX)
                 //                rb_raise(rb_eIOError, "ungetbyte failed");
                 //            #endif
                 if (len > min_capa)
                     rbuf.capa = (int) len;
                 else
                     rbuf.capa = min_capa;
                 rbuf.ptr = new byte[rbuf.capa];
             }
             if (rbuf.capa < len + rbuf.len) {
                 throw context.runtime.newIOError("ungetbyte failed");
             }
             if (rbuf.off < len) {
                 System.arraycopy(rbuf.ptr, rbuf.off, rbuf.ptr, rbuf.capa - rbuf.len, rbuf.len);
                 rbuf.off = rbuf.capa - rbuf.len;
             }
             rbuf.off -= (int) len;
             rbuf.len += (int) len;
             ByteList strByteList = ((RubyString) str).getByteList();
             System.arraycopy(strByteList.unsafeBytes(), strByteList.begin(), rbuf.ptr, rbuf.off, len);
         } finally {
             if (locked) unlock();
         }
     }
 
     // io_getc
     public IRubyObject getc(ThreadContext context, Encoding enc) {
         Ruby runtime = context.runtime;
         int r, n, cr = 0;
         IRubyObject str;
 
         boolean locked = lock();
         try {
             if (needsReadConversion()) {
                 str = context.nil;
                 Encoding read_enc = readEncoding(runtime);
 
                 SET_BINARY_MODE();
                 makeReadConversion(context, 0);
 
                 while (true) {
                     if (cbuf.len != 0) {
                         r = StringSupport.preciseLength(read_enc, cbuf.ptr, cbuf.off, cbuf.off + cbuf.len);
                         if (!StringSupport.MBCLEN_NEEDMORE_P(r))
                             break;
                         if (cbuf.len == cbuf.capa) {
                             throw runtime.newIOError("too long character");
                         }
                     }
 
                     if (moreChar(context) == MORE_CHAR_FINISHED) {
                         if (cbuf.len == 0) {
                             clearReadConversion();
                             return context.nil;
                         }
                         /* return an unit of an incomplete character just before EOF */
                         str = RubyString.newString(runtime, cbuf.ptr, cbuf.off, 1, read_enc);
                         cbuf.off += 1;
                         cbuf.len -= 1;
                         if (cbuf.len == 0) clearReadConversion();
                         ((RubyString) str).setCodeRange(StringSupport.CR_BROKEN);
                         return str;
                     }
                 }
                 if (StringSupport.MBCLEN_INVALID_P(r)) {
                     r = read_enc.length(cbuf.ptr, cbuf.off, cbuf.off + cbuf.len);
                     str = shiftCbuf(context, r, str);
                     cr = StringSupport.CR_BROKEN;
                 } else {
                     str = shiftCbuf(context, StringSupport.MBCLEN_CHARFOUND_LEN(r), str);
                     cr = StringSupport.CR_VALID;
                     if (StringSupport.MBCLEN_CHARFOUND_LEN(r) == 1 && read_enc.isAsciiCompatible() &&
                             Encoding.isAscii(((RubyString) str).getByteList().get(0))) {
                         cr = StringSupport.CR_7BIT;
                     }
                 }
                 str = EncodingUtils.ioEncStr(runtime, str, this);
 
                 ((RubyString)str).setCodeRange(cr);
 
                 return str;
             }
 
             NEED_NEWLINE_DECORATOR_ON_READ_CHECK();
             if (fillbuf(context) < 0) {
                 return context.nil;
             }
             if (enc.isAsciiCompatible() && Encoding.isAscii(rbuf.ptr[rbuf.off])) {
                 str = RubyString.newString(runtime, rbuf.ptr, rbuf.off, 1);
                 rbuf.off += 1;
                 rbuf.len -= 1;
                 cr = StringSupport.CR_7BIT;
             }
             else {
                 r = StringSupport.preciseLength(enc, rbuf.ptr, rbuf.off, rbuf.off + rbuf.len);
                 if (StringSupport.MBCLEN_CHARFOUND_P(r) &&
                         (n = StringSupport.MBCLEN_CHARFOUND_LEN(r)) <= rbuf.len) {
                     str = RubyString.newString(runtime, rbuf.ptr, rbuf.off, n);
                     rbuf.off += n;
                     rbuf.len -= n;
                     cr = StringSupport.CR_VALID;
                 }
                 else if (StringSupport.MBCLEN_NEEDMORE_P(r)) {
                     str = RubyString.newString(runtime, rbuf.ptr, rbuf.off, rbuf.len);
                     rbuf.len = 0;
                     getc_needmore: while (true) {
                         if (fillbuf(context) != -1) {
                             ((RubyString)str).cat(rbuf.ptr[rbuf.off]);
                             rbuf.off++;
                             rbuf.len--;
                             ByteList strByteList = ((RubyString)str).getByteList();
                             r = StringSupport.preciseLength(enc, strByteList.unsafeBytes(), strByteList.getBegin(), strByteList.getBegin() + strByteList.length());
                             if (StringSupport.MBCLEN_NEEDMORE_P(r)) {
                                 continue getc_needmore;
                             }
                             else if (StringSupport.MBCLEN_CHARFOUND_P(r)) {
                                 cr = StringSupport.CR_VALID;
                             }
                         }
                         break;
                     }
                 }
                 else {
                     str = RubyString.newString(runtime, rbuf.ptr, rbuf.off, 1);
                     rbuf.off++;
                     rbuf.len--;
                 }
             }
             if (cr == 0) cr = StringSupport.CR_BROKEN;
             str = EncodingUtils.ioEncStr(runtime, str, this);
         } finally {
             if (locked) unlock();
         }
 
         ((RubyString)str).setCodeRange(cr);
 
         return str;
     }
 
     // io_tell
     public synchronized long tell(ThreadContext context) {
         flushBeforeSeek(context);
         return posix.lseek(fd, 0, PosixShim.SEEK_CUR);
     }
 
     public synchronized void unread(ThreadContext context) {
         if (Platform.IS_WINDOWS) {
             unreadWindows(context);
         } else {
             unreadUnix(context);
         }
     }
 
     // io_unread, UNIX version
     private synchronized void unreadUnix(ThreadContext context) {
         long r;
         boolean locked = lock();
         try {
             checkClosed();
             if (rbuf.len == 0 || (mode & DUPLEX) != 0)
                 return;
             /* xxx: target position may be negative if buffer is filled by ungetc */
             posix.errno = null;
             r = posix.lseek(fd, -rbuf.len, PosixShim.SEEK_CUR);
-            if (r < 0 && posix.errno != null) {
+            if (r == -1 && posix.errno != null) {
                 if (posix.errno == Errno.ESPIPE)
                     mode |= DUPLEX;
                 return;
             }
             rbuf.off = 0;
             rbuf.len = 0;
         } finally {
             if (locked) unlock();
         }
         return;
     }
 
     // io_unread, Windows version
     private void unreadWindows(ThreadContext context) {
         Ruby runtime = context.runtime;
         long r, pos;
         int read_size;
         long i;
         int newlines = 0;
         long extra_max;
         byte[] pBytes;
         int p;
         byte[] bufBytes;
         int buf = 0;
 
         boolean locked = lock();
         try {
             checkClosed();
             if (rbuf.len == 0 || (mode & DUPLEX) != 0) {
                 return;
             }
 
             // TODO...
             //        if (!rb_w32_fd_is_text(fptr->fd)) {
             //            r = lseek(fptr->fd, -fptr->rbuf.len, SEEK_CUR);
             //            if (r < 0 && errno) {
             //                if (errno == ESPIPE)
             //                    fptr->mode |= FMODE_DUPLEX;
             //                return;
             //            }
             //
             //            fptr->rbuf.off = 0;
             //            fptr->rbuf.len = 0;
             //            return;
             //        }
 
             pos = posix.lseek(fd, 0, PosixShim.SEEK_CUR);
-            if (pos < 0 && posix.errno != null) {
+            if (pos == -1 && posix.errno != null) {
                 if (posix.errno == Errno.ESPIPE)
                     mode |= DUPLEX;
                 return;
             }
 
             /* add extra offset for removed '\r' in rbuf */
             extra_max = (long) (pos - rbuf.len);
             pBytes = rbuf.ptr;
             p = rbuf.off;
 
             /* if the end of rbuf is '\r', rbuf doesn't have '\r' within rbuf.len */
             if (rbuf.ptr[rbuf.capa - 1] == '\r') {
                 newlines++;
             }
 
             for (i = 0; i < rbuf.len; i++) {
                 if (pBytes[p] == '\n') newlines++;
                 if (extra_max == newlines) break;
                 p++;
             }
 
             bufBytes = new byte[rbuf.len + newlines];
             while (newlines >= 0) {
                 r = posix.lseek(fd, pos - rbuf.len - newlines, PosixShim.SEEK_SET);
                 if (newlines == 0) break;
-                if (r < 0) {
+                if (r == -1) {
                     newlines--;
                     continue;
                 }
                 read_size = readInternal(context, this, fd, bufBytes, buf, rbuf.len + newlines);
                 if (read_size < 0) {
                     throw runtime.newErrnoFromErrno(posix.errno, pathv);
                 }
                 if (read_size == rbuf.len) {
                     posix.lseek(fd, r, PosixShim.SEEK_SET);
                     break;
                 } else {
                     newlines--;
                 }
             }
             rbuf.off = 0;
             rbuf.len = 0;
         } finally {
             if (locked) unlock();
         }
         return;
     }
 
     // MRI: io_fwrite
     public long fwrite(ThreadContext context, IRubyObject str, boolean nosync) {
         // The System.console null check is our poor-man's isatty for Windows. See jruby/jruby#3292
         if (Platform.IS_WINDOWS && isStdio() && System.console() != null) {
             return rbW32WriteConsole((RubyString)str);
         }
 
         str = doWriteconv(context, str);
         ByteList strByteList = ((RubyString)str).getByteList();
         return binwrite(context, str, strByteList.unsafeBytes(), strByteList.begin(), strByteList.length(), nosync);
     }
 
     // MRI: rb_w32_write_console
     public static long rbW32WriteConsole(RubyString buffer) {
         // The actual port in MRI uses win32 APIs, but System.console seems to do what we want. See jruby/jruby#3292.
         // FIXME: This assumes the System.console() is the right one to write to. Can you have multiple active?
         System.console().printf("%s", buffer.asJavaString());
 
         return buffer.size();
     }
 
     // do_writeconv
     public IRubyObject doWriteconv(ThreadContext context, IRubyObject str)
     {
         boolean locked = lock();
         try {
             if (needsWriteConversion(context)) {
                 IRubyObject common_encoding = context.nil;
                 SET_BINARY_MODE();
 
                 makeWriteConversion(context);
 
                 if (writeconv != null) {
                     int fmode = mode;
                     if (!writeconvAsciicompat.isNil())
                         common_encoding = writeconvAsciicompat;
                     else if (EncodingUtils.MODE_BTMODE(fmode, EncodingUtils.DEFAULT_TEXTMODE, 0, 1) != 0 && !((RubyString) str).getEncoding().isAsciiCompatible()) {
                         throw context.runtime.newArgumentError("ASCII incompatible string written for text mode IO without encoding conversion: %s" + ((RubyString) str).getEncoding().toString());
                     }
                 } else {
                     if (encs.enc2 != null)
                         common_encoding = context.runtime.getEncodingService().convertEncodingToRubyEncoding(encs.enc2);
                     else if (encs.enc != EncodingUtils.ascii8bitEncoding(context.runtime))
                         common_encoding = context.runtime.getEncodingService().convertEncodingToRubyEncoding(encs.enc);
                 }
 
                 if (!common_encoding.isNil()) {
                     str = EncodingUtils.rbStrEncode(context, str, common_encoding, writeconvPreEcflags, writeconvPreEcopts);
                 }
 
                 if (writeconv != null) {
                     ((RubyString) str).setValue(
                             EncodingUtils.econvStrConvert(context, writeconv, ((RubyString) str).getByteList(), EConvFlags.PARTIAL_INPUT));
                 }
             }
             //        #if defined(RUBY_TEST_CRLF_ENVIRONMENT) || defined(_WIN32)
             //        #define fmode (fptr->mode)
             //        else if (MODE_BTMODE(DEFAULT_TEXTMODE,0,1)) {
             //        if ((fptr->mode & FMODE_READABLE) &&
             //                !(fptr->encs.ecflags & ECONV_NEWLINE_DECORATOR_MASK)) {
             //            setmode(fptr->fd, O_BINARY);
             //        }
             //        else {
             //            setmode(fptr->fd, O_TEXT);
             //        }
             //        if (!rb_enc_asciicompat(rb_enc_get(str))) {
             //            rb_raise(rb_eArgError, "ASCII incompatible string written for text mode IO without encoding conversion: %s",
             //                    rb_enc_name(rb_enc_get(str)));
             //        }
             //    }
             //        #undef fmode
             //        #endif
         } finally {
             if (locked) unlock();
         }
         return str;
     }
 
     private static class BinwriteArg {
         OpenFile fptr;
         IRubyObject str;
         byte[] ptrBytes;
         int ptr;
         int length;
     }
 
     // io_binwrite
     public long binwrite(ThreadContext context, IRubyObject str, byte[] ptrBytes, int ptr, int len, boolean nosync) {
         int n, r, offset = 0;
 
         /* don't write anything if current thread has a pending interrupt. */
         context.pollThreadEvents();
 
         boolean locked = lock();
         try {
             if ((n = len) <= 0) return n;
             if (wbuf.ptr == null && !(!nosync && (mode & SYNC) != 0)) {
                 wbuf.off = 0;
                 wbuf.len = 0;
                 wbuf.capa = IO_WBUF_CAPA_MIN;
                 wbuf.ptr = new byte[wbuf.capa];
                 //            write_lock = new ReentrantReadWriteLock();
                 // ???
                 //            rb_mutex_allow_trap(fptr->write_lock, 1);
             }
 
             // Translation: If we are not nosync (if we can do sync write) and sync or tty mode are set, OR
             //              if the write buffer does not have enough capacity to store all incoming data...unbuffered write
             if ((!nosync && (mode & (SYNC | TTY)) != 0) ||
                     (wbuf.ptr != null && wbuf.capa <= wbuf.len + len)) {
                 BinwriteArg arg = new BinwriteArg();
 
                 if (wbuf.len != 0 && wbuf.len + len <= wbuf.capa) {
                     if (wbuf.capa < wbuf.off + wbuf.len + len) {
                         System.arraycopy(wbuf.ptr, wbuf.off, wbuf.ptr, 0, wbuf.len);
                         wbuf.off = 0;
                     }
                     System.arraycopy(ptrBytes, ptr + offset, wbuf.ptr, wbuf.off + wbuf.len, len);
                     wbuf.len += (int) len;
                     n = 0;
                 }
                 if (io_fflush(context) < 0)
                     return -1L;
                 if (n == 0)
                     return len;
 
                 checkClosed();
                 arg.fptr = this;
                 arg.str = str;
                 retry:
                 while (true) {
                     arg.ptrBytes = ptrBytes;
                     arg.ptr = ptr + offset;
                     arg.length = n;
                     if (write_lock != null) {
                         // FIXME: not interruptible by Ruby
                         //                r = rb_mutex_synchronize(fptr->write_lock, io_binwrite_string, (VALUE)&arg);
                         write_lock.writeLock().lock();
                         try {
                             r = binwriteString(context, arg);
                         } finally {
                             write_lock.writeLock().unlock();
                         }
                     } else {
                         int l = writableLength(n);
                         r = writeInternal(context, this, fd, ptrBytes, ptr + offset, l);
                     }
                     /* xxx: other threads may modify given string. */
                     if (r == n) return len;
                     if (0 <= r) {
                         offset += r;
                         n -= r;
                         posix.errno = Errno.EAGAIN;
                     }
                     if (waitWritable(context)) {
                         checkClosed();
                         if (offset < len)
                             continue retry;
                     }
                     return -1L;
                 }
             }
 
             if (wbuf.off != 0) {
                 if (wbuf.len != 0)
                     System.arraycopy(wbuf.ptr, wbuf.off, wbuf.ptr, 0, wbuf.len);
                 wbuf.off = 0;
             }
             System.arraycopy(ptrBytes, ptr + offset, wbuf.ptr, wbuf.off + wbuf.len, len);
             wbuf.len += (int) len;
         } finally {
             if (locked) unlock();
         }
         return len;
     }
 
     // io_binwrite_string
     static int binwriteString(ThreadContext context, BinwriteArg arg) {
         BinwriteArg p = arg;
         int l = p.fptr.writableLength(p.length);
         return p.fptr.writeInternal2(p.fptr.fd, p.ptrBytes, p.ptr, l);
     }
 
     public static class InternalWriteStruct {
         InternalWriteStruct(OpenFile fptr, ChannelFD fd, byte[] bufBytes, int buf, int count) {
             this.fptr = fptr;
             this.fd = fd;
             this.bufBytes = bufBytes;
             this.buf = buf;
             this.capa = count;
         }
 
         public final OpenFile fptr;
         public final ChannelFD fd;
         public final byte[] bufBytes;
         public final int buf;
         public int capa;
     }
 
     // rb_write_internal
     public static int writeInternal(ThreadContext context, OpenFile fptr, ChannelFD fd, byte[] bufBytes, int buf, int count) {
         InternalWriteStruct iis = new InternalWriteStruct(fptr, fd, bufBytes, buf, count);
 
         try {
             return context.getThread().executeTask(context, iis, writeTask);
         } catch (InterruptedException ie) {
             throw context.runtime.newConcurrencyError("IO operation interrupted");
         }
     }
 
     // rb_write_internal2 (no GVL version...we just don't use executeTask as above.
     int writeInternal2(ChannelFD fd, byte[] bufBytes, int buf, int count) {
         return posix.write(fd, bufBytes, buf, count, nonblock);
     }
 
     public ChannelFD fd() {
         return fd;
     }
 
     public Channel channel() {
         assert(fd != null);
         return fd.ch;
     }
 
     public ReadableByteChannel readChannel() {
         assert(fd != null);
         return fd.chRead;
     }
 
     public WritableByteChannel writeChannel() {
         assert(fd != null);
         return fd.chWrite;
     }
 
     public SeekableByteChannel seekChannel() {
         assert(fd != null);
         return fd.chSeek;
     }
 
     public SelectableChannel selectChannel() {
         assert(fd != null);
         return fd.chSelect;
     }
 
     public FileChannel fileChannel() {
         assert(fd != null);
         return fd.chFile;
     }
 
     public SocketChannel socketChannel() {
         assert(fd != null);
         return fd.chSock;
     }
 
     IRubyObject finishWriteconv(ThreadContext context, boolean noalloc) {
         byte[] dsBytes;
         int ds, de;
         Ptr dpPtr = new Ptr();
         EConvResult res;
 
         boolean locked = lock();
         try {
             if (wbuf.ptr == null) {
                 byte[] buf = new byte[1024];
                 long r;
 
                 res = EConvResult.DestinationBufferFull;
                 while (res == EConvResult.DestinationBufferFull) {
                     dsBytes = buf;
                     ds = dpPtr.p = 0;
                     de = buf.length;
                     dpPtr.p = 0;
                     res = writeconv.convert(null, null, 0, dsBytes, dpPtr, de, 0);
                     outer:
                     while ((dpPtr.p - ds) != 0) {
                         retry:
                         while (true) {
                             if (write_lock != null && write_lock.isWriteLockedByCurrentThread())
                                 r = writeInternal2(fd, dsBytes, ds, dpPtr.p - ds);
                             else
                                 r = writeInternal(runtime.getCurrentContext(), this, fd, dsBytes, ds, dpPtr.p - ds);
                             if (r == dpPtr.p - ds)
                                 break outer;
                             if (0 <= r) {
                                 ds += r;
                             }
                             if (waitWritable(context)) {
                                 if (fd == null)
                                     return noalloc ? runtime.getTrue() : runtime.newIOError(RubyIO.CLOSED_STREAM_MSG).getException();
                                 continue retry;
                             }
                             break retry;
                         }
                         return noalloc ? runtime.getTrue() : RubyFixnum.newFixnum(runtime, (posix.errno == null) ? 0 : posix.errno.longValue());
                     }
                     if (res == EConvResult.InvalidByteSequence ||
                             res == EConvResult.IncompleteInput ||
                             res == EConvResult.UndefinedConversion) {
                         return noalloc ? runtime.getTrue() : EncodingUtils.makeEconvException(runtime, writeconv).getException();
                     }
                 }
 
                 return runtime.getNil();
             }
 
             res = EConvResult.DestinationBufferFull;
             while (res == EConvResult.DestinationBufferFull) {
                 if (wbuf.len == wbuf.capa) {
                     if (io_fflush(context) < 0)
                         return noalloc ? runtime.getTrue() : runtime.newFixnum(posix.errno == null ? 0 : posix.errno.longValue());
                 }
 
                 dsBytes = wbuf.ptr;
                 ds = dpPtr.p = wbuf.off + wbuf.len;
                 de = wbuf.capa;
                 res = writeconv.convert(null, null, 0, dsBytes, dpPtr, de, 0);
                 wbuf.len += (int) (dpPtr.p - ds);
                 if (res == EConvResult.InvalidByteSequence ||
                         res == EConvResult.IncompleteInput ||
                         res == EConvResult.UndefinedConversion) {
                     return noalloc ? runtime.getTrue() : EncodingUtils.makeEconvException(runtime, writeconv).getException();
                 }
             }
         } finally {
             if (locked) unlock();
         }
         return runtime.getNil();
     }
 
     // rb_io_set_nonblock
     public void setNonblock(Ruby runtime) {
         setBlocking(runtime, false);
     }
 
     public void setBlock(Ruby runtime) {
         setBlocking(runtime, true);
     }
 
     public void setBlocking(Ruby runtime, boolean blocking) {
         boolean locked = lock();
         try {
             // Not all NIO channels are non-blocking, so we need to maintain this flag
             // and make those channels act like non-blocking
             nonblock = !blocking;
 
             ChannelFD fd = this.fd;
 
             checkClosed();
 
             if (fd.chSelect != null) {
                 try {
                     fd.chSelect.configureBlocking(blocking);
                     return;
                 } catch (IOException ioe) {
                     throw runtime.newIOErrorFromException(ioe);
                 }
             }
         } finally {
             if (locked) unlock();
         }
     }
 
     public boolean isBlocking() {
         return !nonblock;
     }
 
     // MRI: check_tty
     public void checkTTY() {
         // TODO: native descriptors? Is this only used for stdio?
         if (stdio_file != null) {
             boolean locked = lock();
             try {
                 mode |= TTY | DUPLEX;
             } finally {
                 if (locked) unlock();
             }
         }
     }
 
     public boolean isBOM() {
         return (mode & SETENC_BY_BOM) != 0;
     }
 
     public void setBOM(boolean bom) {
         boolean locked = lock();
         try {
             if (bom) {
                 mode |= SETENC_BY_BOM;
             } else {
                 mode &= ~SETENC_BY_BOM;
             }
         } finally {
             if (locked) unlock();
         }
     }
 
     public boolean isStdio() {
         return stdio_file != null;
     }
 
     public int readPending() {
         lock();
         try {
             if (READ_CHAR_PENDING()) return 1;
             return READ_DATA_PENDING_COUNT();
         } finally {
             unlock();
         }
     }
 
     @Deprecated
     public static int getFModeFromString(String modesString) throws InvalidValueException {
         int fmode = 0;
         int length = modesString.length();
 
         if (length == 0) {
             throw new InvalidValueException();
         }
 
         switch (modesString.charAt(0)) {
             case 'r' :
                 fmode |= READABLE;
                 break;
             case 'w' :
                 fmode |= WRITABLE | TRUNC | CREATE;
                 break;
             case 'a' :
                 fmode |= WRITABLE | APPEND | CREATE;
                 break;
             default :
                 throw new InvalidValueException();
         }
 
         ModifierLoop: for (int n = 1; n < length; n++) {
             switch (modesString.charAt(n)) {
                 case 'b':
                     fmode |= BINMODE;
                     break;
                 case 't' :
                     fmode |= TEXTMODE;
                     break;
                 case '+':
                     fmode |= READWRITE;
                     break;
                 case ':':
                     break ModifierLoop;
                 default:
                     throw new InvalidValueException();
             }
         }
 
         return fmode;
     }
 
     public int getFileno() {
         int fileno = fd.realFileno;
         if (fileno != -1) return fileno;
         return fd.fakeFileno;
     }
 
     // rb_thread_flock
     public int threadFlock(ThreadContext context, final int lockMode) {
 //        #ifdef __CYGWIN__
 //        int old_errno = errno;
 //        #endif
         int ret = 0;
         try {
             ret = context.getThread().executeTask(context, this, new RubyThread.Task<OpenFile, Integer>() {
                 @Override
                 public Integer run(ThreadContext context, OpenFile openFile) throws InterruptedException {
                     return posix.flock(fd, lockMode);
                 }
 
                 @Override
                 public void wakeup(RubyThread thread, OpenFile openFile) {
                     // unlikely to help a native downcall, but we'll try it
                     thread.getNativeThread().interrupt();
                 }
             });
         } catch (InterruptedException ie) {
             // ignore?
         }
 
 //        #ifdef __CYGWIN__
 //        if (GetLastError() == ERROR_NOT_LOCKED) {
 //            ret = 0;
 //            errno = old_errno;
 //        }
 //        #endif
         return ret;
     }
 
     public Errno errno() {
         return posix.errno;
     }
 
     public void errno(Errno newErrno) {
         posix.errno = newErrno;
     }
 
     public static int cloexecDup2(PosixShim posix, ChannelFD oldfd, ChannelFD newfd) {
         int ret;
         /* When oldfd == newfd, dup2 succeeds but dup3 fails with EINVAL.
          * rb_cloexec_dup2 succeeds as dup2.  */
         if (oldfd == newfd) {
             ret = 0;
         }
         else {
 //            #if defined(HAVE_DUP3) && defined(O_CLOEXEC)
 //            static int try_dup3 = 1;
 //            if (2 < newfd && try_dup3) {
 //                ret = dup3(oldfd, newfd, O_CLOEXEC);
 //                if (ret != -1)
 //                    return ret;
 //            /* dup3 is available since Linux 2.6.27, glibc 2.9. */
 //                if (errno == ENOSYS) {
 //                    try_dup3 = 0;
 //                    ret = dup2(oldfd, newfd);
 //                }
 //            }
 //            else {
 //                ret = dup2(oldfd, newfd);
 //            }
 //            #else
             ret = posix.dup2(oldfd, newfd);
 //            #endif
             if (ret == -1) return -1;
         }
         // TODO?
 //        rb_maygvl_fd_fix_cloexec(ret);
         return ret;
     }
 
     /**
      * Add a thread to the list of blocking threads for this IO.
      *
      * @param thread A thread blocking on this IO
      */
     public void addBlockingThread(RubyThread thread) {
         boolean locked = lock();
         try {
             if (blockingThreads == null) {
                 blockingThreads = new ArrayList<RubyThread>(1);
             }
             blockingThreads.add(thread);
         } finally {
             if (locked) unlock();
         }
     }
 
     /**
      * Remove a thread from the list of blocking threads for this IO.
      *
      * @param thread A thread blocking on this IO
      */
     public synchronized void removeBlockingThread(RubyThread thread) {
         boolean locked = lock();
         try {
             if (blockingThreads == null) {
                 return;
             }
             for (int i = 0; i < blockingThreads.size(); i++) {
                 if (blockingThreads.get(i) == thread) {
                     // not using remove(Object) here to avoid the equals() call
                     blockingThreads.remove(i);
                 }
             }
         } finally {
             if (locked) unlock();
         }
     }
 
     /**
      * Fire an IOError in all threads blocking on this IO object
      */
     public void interruptBlockingThreads() {
         boolean locked = lock();
         try {
             if (blockingThreads == null) {
                 return;
             }
             for (int i = 0; i < blockingThreads.size(); i++) {
                 RubyThread thread = blockingThreads.get(i);
 
                 // raise will also wake the thread from selection
                 thread.raise(new IRubyObject[]{runtime.newIOError("stream closed").getException()}, Block.NULL_BLOCK);
             }
         } finally {
             if (locked) unlock();
         }
     }
 
     public void SET_BINARY_MODE() {
         // FIXME: this only does something if we have O_BINARY at open(2) level
     }
 
     private void SET_TEXT_MODE() {
         // FIXME: this only does something if we have O_TEXT at open(2) level
     }
 
     public int remainSize() {
         int siz = READ_DATA_PENDING_COUNT();
         long size;
         long pos;
 
         if ((size = posix.size(fd)) >= 0 &&
-                (pos = posix.lseek(fd, 0, PosixShim.SEEK_CUR)) >= 0 &&
+                (pos = posix.lseek(fd, 0, PosixShim.SEEK_CUR)) != -1 &&
                 size > pos) {
             if (siz + (size - pos) > Integer.MAX_VALUE) {
                 throw runtime.newIOError("file too big for single read");
             }
             siz += size - pos;
         } else {
             siz += BUFSIZ;
         }
         return siz;
     }
 
     public boolean lock() {
         if (lock.isHeldByCurrentThread()) {
             return false;
         } else {
             lock.lock();
             return true;
         }
     }
 
     public void unlock() {
         assert lock.isHeldByCurrentThread();
 
         lock.unlock();
     }
 
     public boolean lockedByMe() {
         return lock.isHeldByCurrentThread();
     }
 }
diff --git a/core/src/main/java/org/jruby/util/io/PosixShim.java b/core/src/main/java/org/jruby/util/io/PosixShim.java
index 1c122dc3ef..df9d03e1ea 100644
--- a/core/src/main/java/org/jruby/util/io/PosixShim.java
+++ b/core/src/main/java/org/jruby/util/io/PosixShim.java
@@ -1,643 +1,643 @@
 package org.jruby.util.io;
 
 import java.io.Closeable;
 import java.io.IOException;
 import java.nio.ByteBuffer;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.FileLock;
 import java.nio.channels.OverlappingFileLockException;
 import java.nio.channels.Pipe;
 
 import jnr.constants.platform.Errno;
 import jnr.constants.platform.Fcntl;
 import jnr.posix.FileStat;
 import jnr.posix.POSIX;
 
 import org.jruby.Ruby;
 import org.jruby.ext.fcntl.FcntlLibrary;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Helpers;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.ResourceException;
 
 /**
  * Representations of as many native posix functions as possible applied to an NIO channel
  */
 public class PosixShim {
     public static final int LOCK_SH = 1;
     public static final int LOCK_EX = 2;
     public static final int LOCK_NB = 4;
     public static final int LOCK_UN = 8;
 
     public static final int SEEK_SET = 0;
     public static final int SEEK_CUR = 1;
     public static final int SEEK_END = 2;
 
     public PosixShim(Ruby runtime) {
         this.runtime = runtime;
         this.posix = runtime.getPosix();
     }
     
     // pseudo lseek(2)
     public long lseek(ChannelFD fd, long offset, int type) {
         clear();
 
         if (fd.chSeek != null) {
             int adj = 0;
             try {
                 switch (type) {
                     case SEEK_SET:
                         return fd.chSeek.position(offset).position();
                     case SEEK_CUR:
                         return fd.chSeek.position(fd.chSeek.position() - adj + offset).position();
                     case SEEK_END:
                         return fd.chSeek.position(fd.chSeek.size() + offset).position();
                     default:
                         errno = Errno.EINVAL;
                         return -1;
                 }
             } catch (IllegalArgumentException e) {
                 errno = Errno.EINVAL;
                 return -1;
             } catch (IOException ioe) {
                 errno = Helpers.errnoFromException(ioe);
                 return -1;
             }
         } else if (fd.chNative != null) {
             // native channel, use native lseek
             int ret = posix.lseek(fd.chNative.getFD(), offset, type);
-            if (ret < 0) errno = Errno.valueOf(posix.errno());
+            if (ret == -1) errno = Errno.valueOf(posix.errno());
             return ret;
         }
 
         if (fd.chSelect != null) {
             // For other channel types, we can't get at a native descriptor to lseek, and we can't use FileChannel
             // .position, so we have to treat them as unseekable and raise EPIPE
             //
             // TODO: It's perhaps just a coincidence that all the channels for
             // which we should raise are instanceof SelectableChannel, since
             // stdio is not...so this bothers me slightly. -CON
             //
             // Original change made in 66b024fedbb2ee32316ccd9de8387931d07993ec
             errno = Errno.EPIPE;
             return -1;
         }
 
         return 0;
     }
 
     public int write(ChannelFD fd, byte[] bytes, int offset, int length, boolean nonblock) {
         clear();
 
         // FIXME: don't allocate every time
         ByteBuffer tmp = ByteBuffer.wrap(bytes, offset, length);
         try {
             if (nonblock) {
                 // TODO: figure out what nonblocking writes against atypical streams (files?) actually do
                 // If we can't set the channel nonblocking, I'm not sure what we can do to
                 // pretend the channel is nonblocking.
             }
 
             if (fd.chWrite == null) {
                 errno = Errno.EACCES;
                 return -1;
             }
             int written = fd.chWrite.write(tmp);
 
             if (written == 0 && length > 0) {
                 // if it's a nonblocking write against a file and we've hit EOF, do EAGAIN
                 if (nonblock) {
                     errno = Errno.EAGAIN;
                     return -1;
                 }
             }
 
             return written;
         } catch (IOException ioe) {
             errno = Helpers.errnoFromException(ioe);
             error = ioe;
             return -1;
         }
     }
 
     private static final int NATIVE_EOF = 0;
     private static final int JAVA_EOF = -1;
 
     public int read(ChannelFD fd, byte[] target, int offset, int length, boolean nonblock) {
         clear();
 
         try {
             if (nonblock) {
                 // need to ensure channels that don't support nonblocking IO at least
                 // appear to be nonblocking
                 if (fd.chSelect != null) {
                     // ok...we should have set it nonblocking already in setNonblock
                 } else {
                     if (fd.chFile != null) {
                         long position = fd.chFile.position();
                         long size = fd.chFile.size();
                         if (position != -1 && size != -1 && position < size) {
                             // there should be bytes available...proceed
                         } else {
                             errno = Errno.EAGAIN;
                             return -1;
                         }
                     } else if (fd.chNative != null && fd.isNativeFile) {
                         // it's a native file, so we don't do selection or nonblock
                     } else {
                         errno = Errno.EAGAIN;
                         return -1;
                     }
                 }
             }
 
             // FIXME: inefficient to recreate ByteBuffer every time
             ByteBuffer buffer = ByteBuffer.wrap(target, offset, length);
             int read = fd.chRead.read(buffer);
 
             if (nonblock) {
                 if (read == JAVA_EOF) {
                     read = NATIVE_EOF; // still treat EOF as EOF
                 } else if (read == 0) {
                     errno = Errno.EAGAIN;
                     return -1;
                 } else {
                     return read;
                 }
             } else {
                 // NIO channels will always raise for errors, so -1 only means EOF.
                 if (read == JAVA_EOF) read = NATIVE_EOF;
             }
 
             return read;
         } catch (IOException ioe) {
             errno = Helpers.errnoFromException(ioe);
             return -1;
         }
     }
 
     // rb_thread_flock
     public int flock(ChannelFD fd, int lockMode) {
         // TODO: null channel always succeeds for all locking operations
 //        if (descriptor.isNull()) return RubyFixnum.zero(runtime);
 
         Channel channel = fd.ch;
         clear();
 
         int real_fd = fd.realFileno;
 
         if (real_fd != -1 && real_fd < FilenoUtil.FIRST_FAKE_FD && !Platform.IS_SOLARIS) {
             // we have a real fd and not on Solaris...try native flocking
             // see jruby/jruby#3254 and jnr/jnr-posix#60
             int result = posix.flock(real_fd, lockMode);
             if (result < 0) {
                 errno = Errno.valueOf(posix.errno());
                 return -1;
             }
             return 0;
         }
 
         if (fd.chFile != null) {
             int ret = checkSharedExclusive(fd, lockMode);
             if (ret < 0) return ret;
 
             if (!lockStateChanges(fd.currentLock, lockMode)) return 0;
 
             try {
                 synchronized (fd.chFile) {
                     // check again, to avoid unnecessary overhead
                     if (!lockStateChanges(fd.currentLock, lockMode)) return 0;
 
                     switch (lockMode) {
                         case LOCK_UN:
                         case LOCK_UN | LOCK_NB:
                             return unlock(fd);
                         case LOCK_EX:
                             return lock(fd, true);
                         case LOCK_EX | LOCK_NB:
                             return tryLock(fd, true);
                         case LOCK_SH:
                             return lock(fd, false);
                         case LOCK_SH | LOCK_NB:
                             return tryLock(fd, false);
                     }
                 }
             } catch (IOException ioe) {
                 errno = Helpers.errnoFromException(ioe);
                 return -1;
             } catch (OverlappingFileLockException ioe) {
                 errno = Errno.EINVAL;
                 errmsg = "overlapping file locks";
             }
             return lockFailedReturn(lockMode);
         } else {
             // We're not actually a real file, so we can't flock
             // FIXME: This needs to be ENOTSUP
             errno = Errno.EINVAL;
             errmsg = "stream is not a file";
             return -1;
         }
     }
 
     public int dup2(ChannelFD filedes, ChannelFD filedes2) {
         return filedes2.dup2From(posix, filedes);
     }
 
     public int close(ChannelFD fd) {
         return close((Closeable)fd);
     }
 
     public int close(Closeable closeable) {
         clear();
 
         try {
             closeable.close();
             return 0;
         } catch (IOException ioe) {
             Errno errno = Helpers.errnoFromException(ioe);
             if (errno == null) {
                 throw new RuntimeException("unknown IOException: " + ioe);
             }
             this.errno = errno;
             return -1;
         }
     }
 
     public Channel[] pipe() {
         clear();
         try {
             Pipe pipe = Pipe.open();
             Channel source = pipe.source(), sink = pipe.sink();
 
             if (posix.isNative() && !Platform.IS_WINDOWS) {
                 // set cloexec if possible
                 int read = FilenoUtil.filenoFrom(source);
                 int write = FilenoUtil.filenoFrom(sink);
                 setCloexec(read, true);
                 setCloexec(write, true);
             }
 
             return new Channel[]{source, sink};
         } catch (IOException ioe) {
             errno = Helpers.errnoFromException(ioe);
             return null;
         }
     }
     
     public interface WaitMacros {
         public abstract boolean WIFEXITED(long status);
         public abstract boolean WIFSIGNALED(long status);
         public abstract int WTERMSIG(long status);
         public abstract int WEXITSTATUS(long status);
         public abstract int WSTOPSIG(long status);
         public abstract boolean WIFSTOPPED(long status);
         public abstract boolean WCOREDUMP(long status);
     }
 
     public static class BSDWaitMacros implements WaitMacros {
         public final long _WSTOPPED = 0177;
 
         // Only confirmed on Darwin
         public final long WCOREFLAG = 0200;
 
         public long _WSTATUS(long status) {
             return status & _WSTOPPED;
         }
 
         public boolean WIFEXITED(long status) {
             return _WSTATUS(status) == 0;
         }
 
         public boolean WIFSIGNALED(long status) {
             return _WSTATUS(status) != _WSTOPPED && _WSTATUS(status) != 0;
         }
 
         public int WTERMSIG(long status) {
             return (int)_WSTATUS(status);
         }
 
         public int WEXITSTATUS(long status) {
             // not confirmed on all platforms
             return (int)((status >>> 8) & 0xFF);
         }
 
         public int WSTOPSIG(long status) {
             return (int)(status >>> 8);
         }
 
         public boolean WIFSTOPPED(long status) {
             return _WSTATUS(status) == _WSTOPPED && WSTOPSIG(status) != 0x13;
         }
 
         public boolean WCOREDUMP(long status) {
             return (status & WCOREFLAG) != 0;
         }
     }
 
     public static class LinuxWaitMacros implements WaitMacros {
         private int __WAIT_INT(long status) { return (int)status; }
 
         private int __W_EXITCODE(int ret, int sig) { return (ret << 8) | sig; }
         private int __W_STOPCODE(int sig) { return (sig << 8) | 0x7f; }
         private static int __W_CONTINUED = 0xffff;
         private static int __WCOREFLAG = 0x80;
 
         /* If WIFEXITED(STATUS), the low-order 8 bits of the status.  */
         private int __WEXITSTATUS(long status) { return (int)((status & 0xff00) >> 8); }
 
         /* If WIFSIGNALED(STATUS), the terminating signal.  */
         private int __WTERMSIG(long status) { return (int)(status & 0x7f); }
 
         /* If WIFSTOPPED(STATUS), the signal that stopped the child.  */
         private int __WSTOPSIG(long status) { return __WEXITSTATUS(status); }
 
         /* Nonzero if STATUS indicates normal termination.  */
         private boolean __WIFEXITED(long status) { return __WTERMSIG(status) == 0; }
 
         /* Nonzero if STATUS indicates termination by a signal.  */
         private boolean __WIFSIGNALED(long status) {
             return ((status & 0x7f) + 1) >> 1 > 0;
         }
 
         /* Nonzero if STATUS indicates the child is stopped.  */
         private boolean __WIFSTOPPED(long status) { return (status & 0xff) == 0x7f; }
 
         /* Nonzero if STATUS indicates the child dumped core.  */
         private boolean __WCOREDUMP(long status) { return (status & __WCOREFLAG) != 0; }
 
         /* Macros for constructing status values.  */
         public int WEXITSTATUS(long status) { return __WEXITSTATUS (__WAIT_INT (status)); }
         public int WTERMSIG(long status) { return __WTERMSIG(__WAIT_INT(status)); }
         public int WSTOPSIG(long status) { return __WSTOPSIG(__WAIT_INT(status)); }
         public boolean WIFEXITED(long status) { return __WIFEXITED(__WAIT_INT(status)); }
         public boolean WIFSIGNALED(long status) { return __WIFSIGNALED(__WAIT_INT(status)); }
         public boolean WIFSTOPPED(long status) { return __WIFSTOPPED(__WAIT_INT(status)); }
         public boolean WCOREDUMP(long status) { return __WCOREDUMP(__WAIT_INT(status)); }
     }
 
     public static final WaitMacros WAIT_MACROS;
     static {
         if (Platform.IS_BSD) {
             WAIT_MACROS = new BSDWaitMacros();
         } else {
             // need other platforms
             WAIT_MACROS = new LinuxWaitMacros();
         }
     }
 
     public int setCloexec(int fd, boolean cloexec) {
         int ret = posix.fcntl(fd, Fcntl.F_GETFD);
         if (ret == -1) {
             errno = Errno.valueOf(posix.errno());
             return -1;
         }
         if (
                 (cloexec && (ret & FcntlLibrary.FD_CLOEXEC) == FcntlLibrary.FD_CLOEXEC)
                 || (!cloexec && (ret & FcntlLibrary.FD_CLOEXEC) == 0)) {
             return 0;
         }
         ret = cloexec ?
                 ret | FcntlLibrary.FD_CLOEXEC :
                 ret & ~FcntlLibrary.FD_CLOEXEC;
         ret = posix.fcntlInt(fd, Fcntl.F_SETFD, ret);
         if (ret == -1) errno = Errno.valueOf(posix.errno());
         return ret;
     }
 
     public Channel open(String cwd, String path, ModeFlags flags, int perm) {
         if ((path.equals("/dev/null") || path.equalsIgnoreCase("nul")) && Platform.IS_WINDOWS) {
             path = "NUL:";
         }
 
         try {
             return JRubyFile.createResource(runtime, cwd, path).openChannel(flags, perm);
         } catch (ResourceException.FileExists e) {
             errno = Errno.EEXIST;
         } catch (ResourceException.FileIsDirectory e) {
             errno = Errno.EISDIR;
         } catch (ResourceException.NotFound e) {
             errno = Errno.ENOENT;
         } catch (ResourceException.PermissionDenied e) {
             errno = Errno.EACCES;
         } catch (ResourceException.TooManySymlinks e) {
             errno = Errno.ELOOP;
         } catch (IOException e) {
             throw new RuntimeException("Unhandled IOException: " + e.getLocalizedMessage(), e);
         }
         return null;
     }
 
     @Deprecated // special case is already handled with JRubyFile.createResource
     public Channel open(String cwd, String path, ModeFlags flags, int perm, ClassLoader classLoader) {
         if (path.startsWith("classpath:/") && classLoader != null) {
             path = path.substring("classpath:/".length());
             return Channels.newChannel(classLoader.getResourceAsStream(path));
         }
 
         return open(cwd, path, flags, perm);
     }
 
     /**
      * Joy of POSIX, only way to get the umask is to set the umask,
      * then set it back. That's unsafe in a threaded program. We
      * minimize but may not totally remove this race by caching the
      * obtained or previously set (see umask() above) umask and using
      * that as the initial set value which, cross fingers, is a
      * no-op. The cache access is then synchronized. TODO: Better?
      */
     public static int umask(POSIX posix) {
         synchronized (_umaskLock) {
             final int umask = posix.umask(_cachedUmask);
             if (_cachedUmask != umask ) {
                 posix.umask(umask);
                 _cachedUmask = umask;
             }
             return umask;
         }
     }
 
     public static int umask(POSIX posix, int newMask) {
         int oldMask;
         synchronized (_umaskLock) {
             oldMask = posix.umask(newMask);
             _cachedUmask = newMask;
         }
         return oldMask;
     }
 
     public int ftruncate(ChannelFD fd, long pos) {
         if (fd.chNative != null) {
             int ret = posix.ftruncate(fd.chNative.getFD(), pos);
             if (ret == -1) errno = Errno.valueOf(posix.errno());
             return ret;
         } else if (fd.chFile != null) {
             try {
                 fd.chFile.truncate(pos);
             } catch (IOException ioe) {
                 errno = Helpers.errnoFromException(ioe);
                 return -1;
             }
         } else {
             errno = Errno.EINVAL;
             return -1;
         }
         return 0;
     }
 
     public long size(ChannelFD fd) {
         if (fd.chNative != null) { // native fd, use fstat
             FileStat stat = posix.allocateStat();
             int ret = posix.fstat(fd.chNative.getFD(), stat);
             if (ret == -1) {
                 errno = Errno.valueOf(posix.errno());
                 return -1;
             }
             return stat.st_size();
         } else if (fd.chSeek != null) { // if it is seekable, get size directly
             try {
                 return fd.chSeek.size();
             } catch (IOException ioe) {
                 errno = Helpers.errnoFromException(ioe);
                 return -1;
             }
         } else {
             // otherwise just return -1 (should be rare, since size is only defined on File
             errno = Errno.EINVAL;
             return -1;
         }
     }
 
     private void clear() {
         errno = null;
         errmsg = null;
     }
 
     private int checkSharedExclusive(ChannelFD fd, int lockMode) {
         // This logic used to attempt a shared lock instead of an exclusive
         // lock, because LOCK_EX on some systems (as reported in JRUBY-1214)
         // allow exclusively locking a read-only file. However, the JDK
         // APIs do not allow acquiring an exclusive lock on files that are
         // not open for read, and there are other platforms (such as Solaris,
         // see JRUBY-5627) that refuse at an *OS* level to exclusively lock
         // files opened only for read. As a result, this behavior is platform-
         // dependent, and so we will obey the JDK's policy of disallowing
         // exclusive locks on files opened only for read.
         if (fd.chWrite == null && (lockMode & LOCK_EX) > 0) {
             errno = Errno.EINVAL;
             errmsg = "cannot acquire exclusive lock on File not opened for write";
             return -1;
         }
 
         // Likewise, JDK does not allow acquiring a shared lock on files
         // that have not been opened for read. We comply here.
         if (fd.chRead == null && (lockMode & LOCK_SH) > 0) {
             errno = Errno.EINVAL;
             errmsg = "cannot acquire shared lock on File not opened for read";
             return -1;
         }
 
         return 0;
     }
 
     private static int lockFailedReturn(int lockMode) {
         return (lockMode & LOCK_EX) == 0 ? 0 : -1;
     }
 
     private static boolean lockStateChanges(FileLock lock, int lockMode) {
         if (lock == null) {
             // no lock, only proceed if we are acquiring
             switch (lockMode & 0xF) {
                 case LOCK_UN:
                 case LOCK_UN | LOCK_NB:
                     return false;
                 default:
                     return true;
             }
         } else {
             // existing lock, only proceed if we are unlocking or changing
             switch (lockMode & 0xF) {
                 case LOCK_UN:
                 case LOCK_UN | LOCK_NB:
                     return true;
                 case LOCK_EX:
                 case LOCK_EX | LOCK_NB:
                     return lock.isShared();
                 case LOCK_SH:
                 case LOCK_SH | LOCK_NB:
                     return !lock.isShared();
                 default:
                     return false;
             }
         }
     }
 
     private int unlock(ChannelFD fd) throws IOException {
         if (fd.currentLock != null) {
             fd.currentLock.release();
             fd.currentLock = null;
 
             return 0;
         }
         return -1;
     }
 
     private int lock(ChannelFD fd, boolean exclusive) throws IOException {
         if (fd.currentLock != null) fd.currentLock.release();
 
         fd.currentLock = fd.chFile.lock(0L, Long.MAX_VALUE, !exclusive);
 
         if (fd.currentLock != null) {
             return 0;
         }
 
         return lockFailedReturn(exclusive ? LOCK_EX : LOCK_SH);
     }
 
     private int tryLock(ChannelFD fd, boolean exclusive) throws IOException {
         if (fd.currentLock != null) fd.currentLock.release();
 
         fd.currentLock = fd.chFile.tryLock(0L, Long.MAX_VALUE, !exclusive);
 
         if (fd.currentLock != null) {
             return 0;
         }
 
         return lockFailedReturn(exclusive ? LOCK_EX : LOCK_SH);
     }
 
     /**
      * The last Throwable exception raised by a call.
      */
     public Throwable error;
 
     /**
      * The appropriate errno value for the last thrown error, if any.
      */
     public Errno errno;
 
     /**
      * The recommended error message, if any.
      */
     public String errmsg;
 
     /**
      * The POSIX instance to use for native calls
      */
     private final POSIX posix;
 
     /**
      * The current runtime
      */
     private final Ruby runtime;
 
     /**
      * An object to synchronize calls to umask
      */
     private static final Object _umaskLock = new Object();
 
     /**
      * The last umask we set
      */
     private static int _cachedUmask = 0;
 }
