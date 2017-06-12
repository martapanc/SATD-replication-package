diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 325ef49c59..d97e92b7d0 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -276,1547 +276,1547 @@ public class RubyFile extends RubyIO implements EncodingCapable {
         constants.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         constants.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         // File::Constants module is included in IO.
         runtime.getIO().includeModule(constants);
 
         runtime.getFileTest().extend_object(fileClass);
         
         fileClass.defineAnnotatedMethods(RubyFile.class);
         
         return fileClass;
     }
     
     @JRubyMethod
     @Override
     public IRubyObject close() {
         // Make sure any existing lock is released before we try and close the file
         if (currentLock != null) {
             try {
                 currentLock.release();
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
         }
         return super.close();
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject flock(ThreadContext context, IRubyObject lockingConstant) {
         // TODO: port exact behavior from MRI, and move most locking logic into ChannelDescriptor
         // TODO: for all LOCK_NB cases, return false if they would block
         ChannelDescriptor descriptor = openFile.getMainStream().getDescriptor();
         
         // null channel always succeeds for all locking operations
         if (descriptor.isNull()) return RubyFixnum.zero(context.getRuntime());
 
         if (descriptor.getChannel() instanceof FileChannel) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             int lockMode = RubyNumeric.num2int(lockingConstant);
 
             // Exclusive locks in Java require the channel to be writable, otherwise
             // an exception is thrown (terminating JRuby execution).
             // But flock behavior of MRI is that it allows
             // exclusive locks even on non-writable file. So we convert exclusive
             // lock to shared lock if the channel is not writable, to better match
             // the MRI behavior.
             if (!openFile.isWritable() && (lockMode & LOCK_EX) > 0) {
                 lockMode = (lockMode ^ LOCK_EX) | LOCK_SH;
             }
 
             try {
                 switch (lockMode) {
                     case LOCK_UN:
                     case LOCK_UN | LOCK_NB:
                         if (currentLock != null) {
                             currentLock.release();
                             currentLock = null;
 
                             return RubyFixnum.zero(context.getRuntime());
                         }
                         break;
                     case LOCK_EX:
                         if (currentLock != null) {
                             currentLock.release();
                             currentLock = null;
                         }
                         currentLock = fileChannel.lock();
                         if (currentLock != null) {
                             return RubyFixnum.zero(context.getRuntime());
                         }
 
                         break;
                     case LOCK_EX | LOCK_NB:
                         if (currentLock != null) {
                             currentLock.release();
                             currentLock = null;
                         }
                         currentLock = fileChannel.tryLock();
                         if (currentLock != null) {
                             return RubyFixnum.zero(context.getRuntime());
                         }
 
                         break;
                     case LOCK_SH:
                         if (currentLock != null) {
                             currentLock.release();
                             currentLock = null;
                         }
 
                         currentLock = fileChannel.lock(0L, Long.MAX_VALUE, true);
                         if (currentLock != null) {
                             return RubyFixnum.zero(context.getRuntime());
                         }
 
                         break;
                     case LOCK_SH | LOCK_NB:
                         if (currentLock != null) {
                             currentLock.release();
                             currentLock = null;
                         }
 
                         currentLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true);
                         if (currentLock != null) {
                             return RubyFixnum.zero(context.getRuntime());
                         }
 
                         break;
                     default:
                 }
             } catch (IOException ioe) {
                 if (context.getRuntime().getDebug().isTrue()) {
                     ioe.printStackTrace(System.err);
                 }
             } catch (java.nio.channels.OverlappingFileLockException ioe) {
                 if (context.getRuntime().getDebug().isTrue()) {
                     ioe.printStackTrace(System.err);
                 }
             }
             return (lockMode & LOCK_EX) == 0 ? RubyFixnum.zero(context.getRuntime()) : context.getRuntime().getFalse();
         } else {
             // We're not actually a real file, so we can't flock
             return context.getRuntime().getFalse();
         }
     }
 
     @JRubyMethod(required = 1, optional = 2, frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     @Override
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (openFile == null) {
             throw getRuntime().newRuntimeError("reinitializing File");
         }
         
         if (args.length > 0 && args.length < 3) {
             IRubyObject fd = TypeConverter.convertToTypeWithCheck(args[0], getRuntime().getFixnum(), "to_int");
             if (!fd.isNil()) {
                 args[0] = fd;
                 return super.initialize(args, block);
             }
         }
 
         return openFile(args);
     }
 
     @JRubyMethod(name = "initialize", required = 1, optional = 2, frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject[] args, Block block) {
         if (openFile == null) {
             throw context.getRuntime().newRuntimeError("reinitializing File");
         }
 
         if (args.length > 0 && args.length <= 3) {
             IRubyObject fd = TypeConverter.convertToTypeWithCheck(args[0], context.getRuntime().getFixnum(), "to_int");
             if (!fd.isNil()) {
                 args[0] = fd;
                 if (args.length == 1) {
                     return super.initialize19(context, args[0], block);
                 } else if (args.length == 2) {
                     return super.initialize19(context, args[0], args[1], block);
                 }
                 return super.initialize19(context, args[0], args[1], args[2], block);
             }
         }
 
         return openFile19(context, args);
     }
 
     private IRubyObject openFile19(ThreadContext context, IRubyObject args[]) {
         RubyString filename = get_path(context, args[0]);
         context.getRuntime().checkSafeString(filename);
 
         path = filename.getUnicodeValue();
 
         String modeString = "r";
         ModeFlags modes = new ModeFlags();
         int perm = 0;
 
         try {
             if (args.length > 1) {
                 modes = parseModes19(context, args[1]);
                 if (args[1] instanceof RubyFixnum) {
                     perm = getFilePermissions(args);
                 } else {
                     modeString = args[1].convertToString().toString();
                 }
             } else {
                 modes = parseModes19(context, RubyString.newString(context.getRuntime(), modeString));
             }
             if (args.length > 2 && !args[2].isNil()) {
                 if (args[2] instanceof RubyHash) {
                     modes = parseOptions(context, args[2], modes);
                 } else {
                     perm = getFilePermissions(args);
                 }
             }
             if (perm > 0) {
                 sysopenInternal(path, modes, perm);
             } else {
                 openInternal(path, modeString, modes);
             }
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         }
 
         return this;
     }
     
     private IRubyObject openFile(IRubyObject args[]) {
         RubyString filename = get_path(getRuntime().getCurrentContext(), args[0]);
         getRuntime().checkSafeString(filename);
         
         path = filename.getUnicodeValue();
         
         String modeString;
         ModeFlags modes;
         int perm;
         
         try {
             if ((args.length > 1 && args[1] instanceof RubyFixnum) || (args.length > 2 && !args[2].isNil())) {
                 modes = parseModes(args[1]);
                 perm = getFilePermissions(args);
 
                 sysopenInternal(path, modes, perm);
             } else {
                 modeString = "r";
                 if (args.length > 1 && !args[1].isNil()) {
                     modeString = args[1].convertToString().toString();
                 }
                 
                 openInternal(path, modeString);
             }
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } finally {}
         
         return this;
     }
 
     private int getFilePermissions(IRubyObject[] args) {
         return (args.length > 2 && !args[2].isNil()) ? RubyNumeric.num2int(args[2]) : 438;
     }
 
     protected void sysopenInternal(String path, ModeFlags modes, int perm) throws InvalidValueException {
         openFile = new OpenFile();
         
         openFile.setPath(path);
         openFile.setMode(modes.getOpenFileFlags());
 
         int umask = getUmaskSafe( getRuntime() );
         perm = perm - (perm & umask);
         
         ChannelDescriptor descriptor = sysopen(path, modes, perm);
         openFile.setMainStream(fdopen(descriptor, modes));
     }
 
     protected void openInternal(String path, String modeString, ModeFlags modes) throws InvalidValueException {
         openFile = new OpenFile();
 
         openFile.setMode(modes.getOpenFileFlags());
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modeString));
     }
     
     protected void openInternal(String path, String modeString) throws InvalidValueException {
         openFile = new OpenFile();
 
         openFile.setMode(getIOModes(getRuntime(), modeString).getOpenFileFlags());
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modeString));
     }
     
     private ChannelDescriptor sysopen(String path, ModeFlags modes, int perm) throws InvalidValueException {
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.open(
                     getRuntime().getCurrentDirectory(),
                     path,
                     modes,
                     perm,
                     getRuntime().getPosix());
 
             // TODO: check if too many open files, GC and try again
 
             return descriptor;
         } catch (PermissionDeniedException pde) {
             // PDException can be thrown only when creating the file and
             // permission is denied.  See JavaDoc of PermissionDeniedException.
             throw getRuntime().newErrnoEACCESError(path);
         } catch (FileNotFoundException fnfe) {
             // FNFException can be thrown in both cases, when the file
             // is not found, or when permission is denied.
             if (Ruby.isSecurityRestricted() || new File(path).exists()) {
                 throw getRuntime().newErrnoEACCESError(path);
             }
             throw getRuntime().newErrnoENOENTError(path);
         } catch (DirectoryAsFileException dafe) {
             throw getRuntime().newErrnoEISDirError();
         } catch (FileExistsException fee) {
             throw getRuntime().newErrnoEEXISTError(path);
         } catch (IOException ioe) {
             throw getRuntime().newIOErrorFromException(ioe);
         }
     }
     
     private Stream fopen(String path, String modeString) {
         try {
             Stream stream = ChannelStream.fopen(
                     getRuntime(),
                     path,
                     getIOModes(getRuntime(), modeString));
             
             if (stream == null) {
                 // TODO
     //            if (errno == EMFILE || errno == ENFILE) {
     //                rb_gc();
     //                file = fopen(fname, mode);
     //            }
     //            if (!file) {
     //                rb_sys_fail(fname);
     //            }
             }
 
             // Do we need to be in SETVBUF mode for buffering to make sense? This comes up elsewhere.
     //    #ifdef USE_SETVBUF
     //        if (setvbuf(file, NULL, _IOFBF, 0) != 0)
     //            rb_warn("setvbuf() can't be honoured for %s", fname);
     //    #endif
     //    #ifdef __human68k__
     //        fmode(file, _IOTEXT);
     //    #endif
             return stream;
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (PermissionDeniedException pde) {
             // PDException can be thrown only when creating the file and
             // permission is denied.  See JavaDoc of PermissionDeniedException.
             throw getRuntime().newErrnoEACCESError(path);
         } catch (FileNotFoundException ex) {
             // FNFException can be thrown in both cases, when the file
             // is not found, or when permission is denied.
             if (Ruby.isSecurityRestricted() || new File(path).exists()) {
                 throw getRuntime().newErrnoEACCESError(path);
             }
             throw getRuntime().newErrnoENOENTError(path);
         } catch (DirectoryAsFileException ex) {
             throw getRuntime().newErrnoEISDirError();
         } catch (FileExistsException ex) {
             throw getRuntime().newErrnoEEXISTError(path);
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (SecurityException ex) {
             throw getRuntime().newErrnoEACCESError(path);
         }
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject chmod(ThreadContext context, IRubyObject arg) {
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw context.getRuntime().newErrnoENOENTError(path);
         }
 
         return context.getRuntime().newFixnum(context.getRuntime().getPosix().chmod(path, mode));
     }
 
     @JRubyMethod(required = 2)
     public IRubyObject chown(ThreadContext context, IRubyObject arg1, IRubyObject arg2) {
         int owner = -1;
         if (!arg1.isNil()) {
             owner = RubyNumeric.num2int(arg1);
         }
 
         int group = -1;
         if (!arg2.isNil()) {
             group = RubyNumeric.num2int(arg2);
         }
 
         if (!new File(path).exists()) {
             throw context.getRuntime().newErrnoENOENTError(path);
         }
 
         return context.getRuntime().newFixnum(context.getRuntime().getPosix().chown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject atime(ThreadContext context) {
         return context.getRuntime().newFileStat(path, false).atime();
     }
 
     @JRubyMethod
     public IRubyObject ctime(ThreadContext context) {
         return context.getRuntime().newFileStat(path, false).ctime();
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject lchmod(ThreadContext context, IRubyObject arg) {
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw context.getRuntime().newErrnoENOENTError(path);
         }
 
         return context.getRuntime().newFixnum(context.getRuntime().getPosix().lchmod(path, mode));
     }
 
     // TODO: this method is not present in MRI!
     @JRubyMethod(required = 2)
     public IRubyObject lchown(ThreadContext context, IRubyObject arg1, IRubyObject arg2) {
         int owner = -1;
         if (!arg1.isNil()) {
             owner = RubyNumeric.num2int(arg1);
         }
 
         int group = -1;
         if (!arg2.isNil()) {
             group = RubyNumeric.num2int(arg2);
         }
 
         if (!new File(path).exists()) {
             throw context.getRuntime().newErrnoENOENTError(path);
         }
 
         return context.getRuntime().newFixnum(context.getRuntime().getPosix().lchown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject lstat(ThreadContext context) {
         return context.getRuntime().newFileStat(path, true);
     }
     
     @JRubyMethod
     public IRubyObject mtime(ThreadContext context) {
         return getLastModified(context.getRuntime(), path);
     }
 
     @JRubyMethod(meta = true, frame = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject path(ThreadContext context, IRubyObject self, IRubyObject str) {
         return get_path(context, str);
     }
 
     /**
      * similar in spirit to rb_get_path from 1.9 source
      * @param context
      * @param obj
      * @return
      */
     public static RubyString get_path(ThreadContext context, IRubyObject obj) {
         if (context.getRuntime().is1_9()) {
             if (obj instanceof RubyString) {
                 return (RubyString)obj;
             }
             
             if (obj.respondsTo("to_path")) {
                 obj = obj.callMethod(context, "to_path");
             }
         }
 
         return obj.convertToString();
     }
 
     /**
      * Get the fully-qualified JRubyFile object for the path, taking into
      * account the runtime's current directory.
      */
     public static JRubyFile file(IRubyObject pathOrFile) {
         Ruby runtime = pathOrFile.getRuntime();
 
         if (pathOrFile instanceof RubyFile) {
             return JRubyFile.create(runtime.getCurrentDirectory(), ((RubyFile) pathOrFile).getPath());
         } else {
             RubyString path = get_path(runtime.getCurrentContext(), pathOrFile);
             return JRubyFile.create(runtime.getCurrentDirectory(), path.getUnicodeValue());
         }
     }
 
     @JRubyMethod(name = {"path", "to_path"})
     public IRubyObject path(ThreadContext context) {
         return path == null ? context.getRuntime().getNil() : context.getRuntime().newString(path);
     }
 
     @JRubyMethod
     @Override
     public IRubyObject stat(ThreadContext context) {
         openFile.checkClosed(context.getRuntime());
         return context.getRuntime().newFileStat(path, false);
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject truncate(ThreadContext context, IRubyObject arg) {
         RubyInteger newLength = arg.convertToInteger();
         if (newLength.getLongValue() < 0) {
             throw context.getRuntime().newErrnoEINVALError(path);
         }
         try {
             openFile.checkWritable(context.getRuntime());
             openFile.getMainStream().ftruncate(newLength.getLongValue());
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (IOException e) {
             // Should we do anything?
         }
 
         return RubyFixnum.zero(context.getRuntime());
     }
 
     @Override
     public String toString() {
         return "RubyFile(" + path + ", " + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStream().getDescriptor()) + ")";
     }
 
     // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
     private static ModeFlags getModes(Ruby runtime, IRubyObject object) throws InvalidValueException {
         if (object instanceof RubyString) {
             return getIOModes(runtime, ((RubyString) object).toString());
         } else if (object instanceof RubyFixnum) {
             return new ModeFlags(((RubyFixnum) object).getLongValue());
         }
 
         throw runtime.newTypeError("Invalid type for modes");
     }
 
     @JRubyMethod
     @Override
     public IRubyObject inspect() {
         StringBuilder val = new StringBuilder();
         val.append("#<File:").append(path);
         if(!openFile.isOpen()) {
             val.append(" (closed)");
         }
         val.append(">");
         return getRuntime().newString(val.toString());
     }
     
     /* File class methods */
     
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject basename(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         String name = get_path(context,args[0]).getUnicodeValue();
 
         // MRI-compatible basename handling for windows drive letter paths
         if (Platform.IS_WINDOWS) {
             if (name.length() > 1 && name.charAt(1) == ':' && Character.isLetter(name.charAt(0))) {
                 switch (name.length()) {
                 case 2:
                     return RubyString.newEmptyString(context.getRuntime()).infectBy(args[0]);
                 case 3:
                     return context.getRuntime().newString(name.substring(2)).infectBy(args[0]);
                 default:
                     switch (name.charAt(2)) {
                     case '/':
                     case '\\':
                         break;
                     default:
                         // strip c: away from relative-pathed name
                         name = name.substring(2);
                         break;
                     }
                     break;
                 }
             }
         }
 
         while (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
             name = name.substring(0, name.length() - 1);
         }
         
         // Paths which end in "/" or "\\" must be stripped off.
         int slashCount = 0;
         int length = name.length();
         for (int i = length - 1; i >= 0; i--) {
             char c = name.charAt(i);
             if (c != '/' && c != '\\') {
                 break;
             }
             slashCount++;
         }
         if (slashCount > 0 && length > 1) {
             name = name.substring(0, name.length() - slashCount);
         }
         
         int index = name.lastIndexOf('/');
         if (index == -1) {
             // XXX actually only on windows...
             index = name.lastIndexOf('\\');
         }
         
         if (!name.equals("/") && index != -1) {
             name = name.substring(index + 1);
         }
         
         if (args.length == 2) {
             String ext = RubyString.stringValue(args[1]).toString();
             if (".*".equals(ext)) {
                 index = name.lastIndexOf('.');
                 if (index > 0) {  // -1 no match; 0 it is dot file not extension
                     name = name.substring(0, index);
                 }
             } else if (name.endsWith(ext)) {
                 name = name.substring(0, name.length() - ext.length());
             }
         }
         return context.getRuntime().newString(name).infectBy(args[0]);
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject chmod(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             JRubyFile filename = file(args[i]);
             
             if (!filename.exists()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
             
             boolean result = 0 == runtime.getPosix().chmod(filename.getAbsolutePath(), (int)mode.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 3, rest = true, meta = true)
     public static IRubyObject chown(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         int count = 0;
         int owner = -1;
         if (!args[0].isNil()) {
             owner = RubyNumeric.num2int(args[0]);
         }
 
         int group = -1;
         if (!args[1].isNil()) {
             group = RubyNumeric.num2int(args[1]);
         }
         for (int i = 2; i < args.length; i++) {
             JRubyFile filename = file(args[i]);
 
             if (!filename.exists()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
             
             boolean result = 0 == runtime.getPosix().chown(filename.getAbsolutePath(), owner, group);
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject dirname(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyString filename = get_path(context, arg);
         
         String jfilename = filename.getUnicodeValue();
         
         String name = jfilename.replace('\\', '/');
         int minPathLength = 1;
         boolean trimmedSlashes = false;
 
         boolean startsWithDriveLetterOnWindows = startsWithDriveLetterOnWindows(name);
 
         if (startsWithDriveLetterOnWindows) {
             minPathLength = 3;
         }
 
         while (name.length() > minPathLength && name.charAt(name.length() - 1) == '/') {
             trimmedSlashes = true;
             name = name.substring(0, name.length() - 1);
         }
 
         String result;
         if (startsWithDriveLetterOnWindows && name.length() == 2) {
             if (trimmedSlashes) {
                 // C:\ is returned unchanged
                 result = jfilename.substring(0, 3);
             } else {
                 result = jfilename.substring(0, 2) + '.';
             }
         } else {
             //TODO deal with UNC names
             int index = name.lastIndexOf('/');
             if (index == -1) {
                 if (startsWithDriveLetterOnWindows) {
                     return context.getRuntime().newString(jfilename.substring(0, 2) + ".");
                 } else {
                     return context.getRuntime().newString(".");
                 }
             }
             if (index == 0) return context.getRuntime().newString("/");
 
             if (startsWithDriveLetterOnWindows && index == 2) {
                 // Include additional path separator
                 // (so that dirname of "C:\file.txt" is  "C:\", not "C:")
                 index++;
             }
 
             result = jfilename.substring(0, index);
         }
 
         char endChar;
         // trim trailing slashes
         while (result.length() > minPathLength) {
             endChar = result.charAt(result.length() - 1);
             if (endChar == '/' || endChar == '\\') {
                 result = result.substring(0, result.length() - 1);
             } else {
                 break;
             }
         }
 
         return context.getRuntime().newString(result).infectBy(filename);
     }
 
     private static boolean isWindowsDriveLetter(char c) {
            return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
     }
 
 
     /**
      * Returns the extension name of the file. An empty string is returned if 
      * the filename (not the entire path) starts or ends with a dot.
      * @param recv
      * @param arg Path to get extension name of
      * @return Extension, including the dot, or an empty string
      */
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject extname(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         IRubyObject baseFilename = basename(context, recv, new IRubyObject[]{arg});
         
         String filename = RubyString.stringValue(baseFilename).getUnicodeValue();
         String result = "";
 
         int dotIndex = filename.lastIndexOf(".");
         if (dotIndex > 0 && dotIndex != (filename.length() - 1)) {
             // Dot is not at beginning and not at end of filename. 
             result = filename.substring(dotIndex);
         }
 
         return context.getRuntime().newString(result);
     }
 
     /**
      * Converts a pathname to an absolute pathname. Relative paths are 
      * referenced from the current working directory of the process unless 
      * a second argument is given, in which case it will be used as the 
      * starting point. If the second argument is also relative, it will 
      * first be converted to an absolute pathname.
      * @param recv
      * @param args 
      * @return Resulting absolute path as a String
      */
     @JRubyMethod(required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject expand_path(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, true);
     }
 
     @JRubyMethod(name = "expand_path", required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject expand_path19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         RubyString path = (RubyString) expandPathInternal(context, recv, args, true);
         path.force_encoding(context, RubyEncoding.getDefaultExternal(context.getRuntime()));
 
         return path;
     }
 
 
     /**
      * ---------------------------------------------------- File::absolute_path
      *      File.absolute_path(file_name [, dir_string] ) -> abs_file_name
      *
      *      From Ruby 1.9.1
      * ------------------------------------------------------------------------
      *      Converts a pathname to an absolute pathname. Relative paths are
      *      referenced from the current working directory of the process unless
      *      _dir_string_ is given, in which case it will be used as the
      *      starting point. If the given pathname starts with a ``+~+'' it is
      *      NOT expanded, it is treated as a normal directory name.
      *
      *         File.absolute_path("~oracle/bin")       #=> "<relative_path>/~oracle/bin"
      *
      * @param context
      * @param recv
      * @param args
      * @return
      */
     @JRubyMethod(required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject absolute_path(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, false);
     }
 
     @JRubyMethod(name = {"realdirpath"}, required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject realdirpath(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, false);
     }
 
     @JRubyMethod(name = {"realpath"}, required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject realpath(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject file = expandPathInternal(context, recv, args, false);
         if (!RubyFileTest.exist_p(recv, file).isTrue()) {
             throw context.getRuntime().newErrnoENOENTError(file.toString());
         }
         return file;
     }
 
     private static IRubyObject expandPathInternal(ThreadContext context, IRubyObject recv, IRubyObject[] args, boolean expandUser) {
         Ruby runtime = context.getRuntime();
 
         String relativePath = get_path(context, args[0]).getUnicodeValue();
 
         boolean isAbsoluteWithFilePrefix = relativePath.startsWith("file:");
 
         String cwd = null;
 
         // Handle ~user paths 
         if (expandUser) {
             relativePath = expandUserPath(context, relativePath);
         }
 
         // If there's a second argument, it's the path to which the first 
         // argument is relative.
         if (args.length == 2 && !args[1].isNil()) {
 
             cwd = get_path(context, args[1]).getUnicodeValue();
 
             if (!isAbsoluteWithFilePrefix) {
                 isAbsoluteWithFilePrefix = cwd.startsWith("file:");
             }
 
             // Handle ~user paths.
             if (expandUser) {
                 cwd = expandUserPath(context, cwd);
             }
 
             cwd = adjustRootPathOnWindows(runtime, cwd, null);
 
             boolean startsWithSlashNotOnWindows = (cwd != null)
                     && !Platform.IS_WINDOWS && cwd.length() > 0
                     && cwd.charAt(0) == '/';
 
             // TODO: better detection when path is absolute or not.
             // If the path isn't absolute, then prepend the current working
             // directory to the path.
             if (!startsWithSlashNotOnWindows && !startsWithDriveLetterOnWindows(cwd)) {
                 cwd = new File(runtime.getCurrentDirectory(), cwd).getAbsolutePath();
             }
         } else {
             // If there's no second argument, simply use the working directory 
             // of the runtime.
             cwd = runtime.getCurrentDirectory();
         }
         
         // Something wrong we don't know the cwd...
         // TODO: Is this behavior really desirable? /mov
         if (cwd == null) return runtime.getNil();
         
         /* The counting of slashes that follows is simply a way to adhere to 
          * Ruby's UNC (or something) compatibility. When Ruby's expand_path is 
          * called with "//foo//bar" it will return "//foo/bar". JRuby uses 
          * java.io.File, and hence returns "/foo/bar". In order to retain 
          * java.io.File in the lower layers and provide full Ruby 
          * compatibility, the number of extra slashes must be counted and 
          * prepended to the result.
          */ 
 
         // TODO: special handling on windows for some corner cases
 //        if (IS_WINDOWS) {
 //            if (relativePath.startsWith("//")) {
 //                if (relativePath.length() > 2 && relativePath.charAt(2) != '/') {
 //                    int nextSlash = relativePath.indexOf('/', 3);
 //                    if (nextSlash != -1) {
 //                        return runtime.newString(
 //                                relativePath.substring(0, nextSlash)
 //                                + canonicalize(relativePath.substring(nextSlash)));
 //                    } else {
 //                        return runtime.newString(relativePath);
 //                    }
 //                }
 //            }
 //        }
 
         // Find out which string to check.
         String padSlashes = "";
         if (!Platform.IS_WINDOWS) {
             if (relativePath.length() > 0 && relativePath.charAt(0) == '/') {
                 padSlashes = countSlashes(relativePath);
             } else if (cwd.length() > 0 && cwd.charAt(0) == '/') {
                 padSlashes = countSlashes(cwd);
             }
         }
         
         JRubyFile path;
         
         if (relativePath.length() == 0) {
             path = JRubyFile.create(relativePath, cwd);
         } else {
             relativePath = adjustRootPathOnWindows(runtime, relativePath, cwd);
             path = JRubyFile.create(cwd, relativePath);
         }
         
         String tempResult = padSlashes + canonicalize(path.getAbsolutePath());
 
         if(isAbsoluteWithFilePrefix) {
             tempResult = tempResult.substring(tempResult.indexOf("file:"));
         }
 
         return runtime.newString(tempResult);
     }
     
     /**
      * This method checks a path, and if it starts with ~, then it expands 
      * the path to the absolute path of the user's home directory. If the 
      * string does not begin with ~, then the string is simply returned.
      * unaltered.
      * @param recv
      * @param path Path to check
      * @return Expanded path
      */
     public static String expandUserPath(ThreadContext context, String path) {
         
         int pathLength = path.length();
 
         if (pathLength >= 1 && path.charAt(0) == '~') {
             // Enebo : Should ~frogger\\foo work (it doesnt in linux ruby)?
             int userEnd = path.indexOf('/');
             
             if (userEnd == -1) {
                 if (pathLength == 1) {
                     // Single '~' as whole path to expand
                     path = RubyDir.getHomeDirectoryPath(context).toString();
                 } else {
                     // No directory delimeter.  Rest of string is username
                     userEnd = pathLength;
                 }
             }
             
             if (userEnd == 1) {
                 // '~/...' as path to expand
                 path = RubyDir.getHomeDirectoryPath(context).toString() +
                         path.substring(1);
             } else if (userEnd > 1){
                 // '~user/...' as path to expand
                 String user = path.substring(1, userEnd);
                 IRubyObject dir = RubyDir.getHomeDirectoryPath(context, user);
                 
                 if (dir.isNil()) {
                     throw context.getRuntime().newArgumentError("user " + user + " does not exist");
                 }
                 
                 path = "" + dir + (pathLength == userEnd ? "" : path.substring(userEnd));
             }
         }
         return path;
     }
     
     /**
      * Returns a string consisting of <code>n-1</code> slashes, where 
      * <code>n</code> is the number of slashes at the beginning of the input 
      * string.
      * @param stringToCheck
      * @return
      */
     private static String countSlashes( String stringToCheck ) {
         
         // Count number of extra slashes in the beginning of the string.
         int slashCount = 0;
         for (int i = 0; i < stringToCheck.length(); i++) {
             if (stringToCheck.charAt(i) == '/') {
                 slashCount++;
             } else {
                 break;
             }
         }
 
         // If there are N slashes, then we want N-1.
         if (slashCount > 0) {
             slashCount--;
         }
         
         // Prepare a string with the same number of redundant slashes so that 
         // we easily can prepend it to the result.
         byte[] slashes = new byte[slashCount];
         for (int i = 0; i < slashCount; i++) {
             slashes[i] = '/';
         }
         return new String(slashes); 
         
     }
 
-    private static String canonicalize(String path) {
+    public static String canonicalize(String path) {
         return canonicalize(null, path);
     }
 
     private static String canonicalize(String canonicalPath, String remaining) {
         if (remaining == null) {
             if ("".equals(canonicalPath)) {
                 return "/";
             } else {
                 // compensate for missing slash after drive letter on windows
                 if (startsWithDriveLetterOnWindows(canonicalPath)
                         && canonicalPath.length() == 2) {
                     canonicalPath += "/";
                 }
             }
             return canonicalPath;
         }
 
         String child;
         int slash = remaining.indexOf('/');
         if (slash == -1) {
             child = remaining;
             remaining = null;
         } else {
             child = remaining.substring(0, slash);
             remaining = remaining.substring(slash + 1);
         }
 
         if (child.equals(".")) {
             // no canonical path yet or length is zero, and we have a / followed by a dot...
             if (slash == -1) {
                 // we don't have another slash after this, so replace /. with /
                 if (canonicalPath != null && canonicalPath.length() == 0 && slash == -1) canonicalPath += "/";
             } else {
                 // we do have another slash; omit both / and . (JRUBY-1606)
             }
         } else if (child.equals("..")) {
             if (canonicalPath == null) throw new IllegalArgumentException("Cannot have .. at the start of an absolute path");
             int lastDir = canonicalPath.lastIndexOf('/');
             if (lastDir == -1) {
                 if (startsWithDriveLetterOnWindows(canonicalPath)) {
                    // do nothing, we should not delete the drive letter
                 } else {
                     canonicalPath = "";
                 }
             } else {
                 canonicalPath = canonicalPath.substring(0, lastDir);
             }
         } else if (canonicalPath == null) {
             canonicalPath = child;
         } else {
             canonicalPath += "/" + child;
         }
 
         return canonicalize(canonicalPath, remaining);
     }
 
     /**
      * Returns true if path matches against pattern The pattern is not a regular expression;
      * instead it follows rules similar to shell filename globbing. It may contain the following
      * metacharacters:
      *   *:  Glob - match any sequence chars (re: .*).  If like begins with '.' then it doesn't.
      *   ?:  Matches a single char (re: .).
      *   [set]:  Matches a single char in a set (re: [...]).
      *
      */
     @JRubyMethod(name = {"fnmatch", "fnmatch?"}, required = 2, optional = 1, meta = true)
     public static IRubyObject fnmatch(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         int flags = args.length == 3 ? RubyNumeric.num2int(args[2]) : 0;
 
         ByteList pattern = args[0].convertToString().getByteList();
         ByteList path = get_path(context, args[1]).getByteList();
 
         if (org.jruby.util.Dir.fnmatch(pattern.getUnsafeBytes(), pattern.getBegin(), pattern.getBegin()+pattern.getRealSize(), path.getUnsafeBytes(), path.getBegin(), path.getBegin()+path.getRealSize(), flags) == 0) {
             return context.getRuntime().getTrue();
         }
         return context.getRuntime().getFalse();
     }
     
     @JRubyMethod(name = "ftype", required = 1, meta = true)
     public static IRubyObject ftype(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         return context.getRuntime().newFileStat(get_path(context, filename).getUnicodeValue(), true).ftype();
     }
 
     private static String inspectJoin(ThreadContext context, IRubyObject recv, RubyArray parent, RubyArray array) {
         Ruby runtime = context.getRuntime();
         
         // If already inspecting, there is no need to register/unregister again.
         if (runtime.isInspecting(parent)) return join(context, recv, array).toString();
 
         try {
             runtime.registerInspecting(parent);
             return join(context, recv, array).toString();
         } finally {
             runtime.unregisterInspecting(parent);
         }
     }
 
     private static RubyString join(ThreadContext context, IRubyObject recv, RubyArray ary) {
         IRubyObject[] args = ary.toJavaArray();
         boolean isTainted = false;
         StringBuilder buffer = new StringBuilder();
         Ruby runtime = context.getRuntime();
         
         for (int i = 0; i < args.length; i++) {
             if (args[i].isTaint()) {
                 isTainted = true;
             }
             String element;
             if (args[i] instanceof RubyString) {
                 element = args[i].convertToString().getUnicodeValue();
             } else if (args[i] instanceof RubyArray) {
                 if (runtime.isInspecting(args[i])) {
                     throw runtime.newArgumentError("recursive array");
                 } else {
                     element = inspectJoin(context, recv, ary, ((RubyArray)args[i]));
                 }
             } else {
                 RubyString path = get_path(context, args[i]);
                 element = path.getUnicodeValue();
             }
             
             chomp(buffer);
             if (i > 0 && !element.startsWith("/") && !element.startsWith("\\")) {
                 buffer.append("/");
             }
             buffer.append(element);
         }
         
         RubyString fixedStr = RubyString.newString(runtime, buffer.toString());
         fixedStr.setTaint(isTainted);
         return fixedStr;
     }
     
     /*
      * Fixme:  This does not have exact same semantics as RubyArray.join, but they
      * probably could be consolidated (perhaps as join(args[], sep, doChomp)).
      */
     @JRubyMethod(rest = true, meta = true)
     public static RubyString join(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return join(context, recv, RubyArray.newArrayNoCopyLight(context.getRuntime(), args));
     }
 
     private static void chomp(StringBuilder buffer) {
         int lastIndex = buffer.length() - 1;
         
         while (lastIndex >= 0 && (buffer.lastIndexOf("/") == lastIndex || buffer.lastIndexOf("\\") == lastIndex)) {
             buffer.setLength(lastIndex);
             lastIndex--;
         }
     }
     
     @JRubyMethod(name = "lstat", required = 1, meta = true)
     public static IRubyObject lstat(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.getRuntime().newFileStat(f, true);
     }
 
     @JRubyMethod(name = "stat", required = 1, meta = true)
     public static IRubyObject stat(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.getRuntime().newFileStat(f, false);
     }
 
     @JRubyMethod(name = "atime", required = 1, meta = true)
     public static IRubyObject atime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.getRuntime().newFileStat(f, false).atime();
     }
 
     @JRubyMethod(name = "ctime", required = 1, meta = true)
     public static IRubyObject ctime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.getRuntime().newFileStat(f, false).ctime();
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject lchmod(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             RubyString filename = get_path(context, args[i]);
             
             if (!RubyFileTest.exist_p(filename, filename).isTrue()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
             
             boolean result = 0 == runtime.getPosix().lchmod(filename.getUnicodeValue(), (int)mode.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject lchown(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int owner = !args[0].isNil() ? RubyNumeric.num2int(args[0]) : -1;
         int group = !args[1].isNil() ? RubyNumeric.num2int(args[1]) : -1;
         int count = 0;
 
         for (int i = 2; i < args.length; i++) {
             IRubyObject filename = args[i];
 
             if (0 != runtime.getPosix().lchown(filename.toString(), owner, group)) {
                 throw runtime.newErrnoFromLastPOSIXErrno();
             } else {
                 count++;
             }
         }
 
         return runtime.newFixnum(count);
     }
 
     @JRubyMethod(required = 2, meta = true, backtrace = true)
     public static IRubyObject link(ThreadContext context, IRubyObject recv, IRubyObject from, IRubyObject to) {
         Ruby runtime = context.getRuntime();
         RubyString fromStr = RubyString.stringValue(from);
         RubyString toStr = RubyString.stringValue(to);
 
         int ret = runtime.getPosix().link(fromStr.getUnicodeValue(), toStr.getUnicodeValue());
         if (ret != 0) {
             // In most cases, when there is an error during the call,
             // the POSIX handler throws an exception, but not in case
             // with pure Java POSIX layer (when native support is disabled),
             // so we deal with it like this:
             throw runtime.newErrnoEEXISTError(fromStr + " or " + toStr);
         }
         return runtime.newFixnum(ret);
     }
 
     @JRubyMethod(name = "mtime", required = 1, meta = true)
     public static IRubyObject mtime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         return getLastModified(context.getRuntime(), get_path(context, filename).getUnicodeValue());
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject rename(ThreadContext context, IRubyObject recv, IRubyObject oldName, IRubyObject newName) {
         Ruby runtime = context.getRuntime();
         RubyString oldNameString = RubyString.stringValue(oldName);
         RubyString newNameString = RubyString.stringValue(newName);
         runtime.checkSafeString(oldNameString);
         runtime.checkSafeString(newNameString);
 
         String newNameJavaString = newNameString.getUnicodeValue();
         String oldNameJavaString = oldNameString.getUnicodeValue();
         JRubyFile oldFile = JRubyFile.create(runtime.getCurrentDirectory(), oldNameJavaString);
         JRubyFile newFile = JRubyFile.create(runtime.getCurrentDirectory(), newNameJavaString);
         
         if (!oldFile.exists() || !newFile.getParentFile().exists()) {
             throw runtime.newErrnoENOENTError(oldNameJavaString + " or " + newNameJavaString);
         }
 
         JRubyFile dest = JRubyFile.create(runtime.getCurrentDirectory(), newNameJavaString);
 
         if (oldFile.renameTo(dest)) {  // rename is successful
             return RubyFixnum.zero(runtime);
         }
 
         // rename via Java API call wasn't successful, let's try some tricks, similar to MRI 
 
         if (newFile.exists()) {
             runtime.getPosix().chmod(newNameJavaString, 0666);
             newFile.delete();
         }
 
         if (oldFile.renameTo(dest)) { // try to rename one more time
             return RubyFixnum.zero(runtime);
         }
 
         throw runtime.newErrnoEACCESError(oldNameJavaString + " or " + newNameJavaString);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static RubyArray split(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyString filename = get_path(context, arg);
         
         return context.getRuntime().newArray(dirname(context, recv, filename),
                 basename(context, recv, new IRubyObject[] { filename }));
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject symlink(ThreadContext context, IRubyObject recv, IRubyObject from, IRubyObject to) {
         Ruby runtime = context.getRuntime();
         RubyString fromStr = get_path(context, from);
         RubyString toStr = get_path(context, to);
         String tovalue = toStr.getUnicodeValue();
         tovalue = JRubyFile.create(runtime.getCurrentDirectory(), tovalue).getAbsolutePath();
         try {
             if (runtime.getPosix().symlink(
                     fromStr.getUnicodeValue(), tovalue) == -1) {
                 // FIXME: When we get JNA3 we need to properly write this to errno.
                 throw runtime.newErrnoEEXISTError(fromStr + " or " + toStr);
             }
         } catch (java.lang.UnsatisfiedLinkError ule) {
             throw runtime.newNotImplementedError("symlink() function is unimplemented on this machine");
         }
         
         return runtime.newFixnum(0);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject readlink(ThreadContext context, IRubyObject recv, IRubyObject path) {
         Ruby runtime = context.getRuntime();
         
         try {
             String realPath = runtime.getPosix().readlink(path.convertToString().getUnicodeValue());
         
             if (!RubyFileTest.exist_p(recv, path).isTrue()) {
                 throw runtime.newErrnoENOENTError(path.toString());
             }
         
             if (!RubyFileTest.symlink_p(recv, path).isTrue()) {
                 throw runtime.newErrnoEINVALError(path.toString());
             }
         
             if (realPath == null) {
                 //FIXME: When we get JNA3 we need to properly write this to errno.
             }
 
             return runtime.newString(realPath);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     // Can we produce IOError which bypasses a close?
     @JRubyMethod(required = 2, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject truncate(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {        
         return truncateCommon(context, recv, arg1, arg2);
     }
 
     @JRubyMethod(name = "truncate", required = 2, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject truncate19(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         if (!(arg1 instanceof RubyString) && arg1.respondsTo("to_path")) {
             arg1 = arg1.callMethod(context, "to_path");
         }
         return truncateCommon(context, recv, arg1, arg2);
     }
 
     private static IRubyObject truncateCommon(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         RubyString filename = arg1.convertToString(); // TODO: SafeStringValue here
         Ruby runtime = context.getRuntime();
         RubyInteger newLength = arg2.convertToInteger();
 
         File testFile ;
         File childFile = new File(filename.getUnicodeValue() );
 
         if ( childFile.isAbsolute() ) {
           testFile = childFile ;
         } else {
           testFile = new File(runtime.getCurrentDirectory(), filename.getByteList().toString());
         }
 
         if (!testFile.exists()) {
             throw runtime.newErrnoENOENTError(filename.getByteList().toString());
         }
 
         if (newLength.getLongValue() < 0) {
             throw runtime.newErrnoEINVALError(filename.toString());
         }
 
         IRubyObject[] args = new IRubyObject[] { filename, runtime.newString("r+") };
         RubyFile file = (RubyFile) open(context, recv, args, Block.NULL_BLOCK);
         file.truncate(context, newLength);
         file.close();
 
         return RubyFixnum.zero(runtime);
     }
 
     @JRubyMethod(meta = true, optional = 1)
     public static IRubyObject umask(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int oldMask = 0;
         if (args.length == 0) {
             oldMask = getUmaskSafe( runtime );
         } else if (args.length == 1) {
             int newMask = (int) args[0].convertToInteger().getLongValue();
             synchronized (_umaskLock) {
                 oldMask = runtime.getPosix().umask(newMask);
                 _cachedUmask = newMask;
             }
         } else {
             runtime.newArgumentError("wrong number of arguments");
         }
         
         return runtime.newFixnum(oldMask);
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject utime(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         long[] atimeval = null;
         long[] mtimeval = null;
 
         if (args[0] != runtime.getNil() || args[1] != runtime.getNil()) {
             atimeval = extractTimeval(runtime, args[0]);
             mtimeval = extractTimeval(runtime, args[1]);
         }
 
         for (int i = 2, j = args.length; i < j; i++) {
             RubyString filename = get_path(context, args[i]);
             runtime.checkSafeString(filename);
             
             JRubyFile fileToTouch = JRubyFile.create(runtime.getCurrentDirectory(),filename.getUnicodeValue());
             
             if (!fileToTouch.exists()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
 
             runtime.getPosix().utimes(fileToTouch.getAbsolutePath(), atimeval, mtimeval);
         }
         
         return runtime.newFixnum(args.length - 2);
     }
     
     @JRubyMethod(name = {"unlink", "delete"}, rest = true, meta = true)
     public static IRubyObject unlink(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
          
         for (int i = 0; i < args.length; i++) {
             RubyString filename = get_path(context, args[i]);
             runtime.checkSafeString(filename);
             JRubyFile lToDelete = JRubyFile.create(runtime.getCurrentDirectory(), filename.getUnicodeValue());
             
             boolean isSymlink = RubyFileTest.symlink_p(recv, filename).isTrue();
             // Broken symlinks considered by exists() as non-existing,
             // so we need to check for symlinks explicitly.
             if (!lToDelete.exists() && !isSymlink) {
                 throw runtime.newErrnoENOENTError(filename.getUnicodeValue());
             }
 
             if (lToDelete.isDirectory() && !isSymlink) {
                 throw runtime.newErrnoEPERMError(filename.getUnicodeValue());
             }
 
             if (!lToDelete.delete()) {
                 throw runtime.newErrnoEACCESError(filename.getUnicodeValue());
             }
         }
         
         return runtime.newFixnum(args.length);
     }
 
     @JRubyMethod(name = "size", backtrace = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject size(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if ((openFile.getMode() & OpenFile.WRITABLE) != 0) {
             flush();
         }
 
         FileStat stat = runtime.getPosix().fstat(
                 getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor());
         if (stat == null) {
             throw runtime.newErrnoEACCESError(path);
         }
 
         return runtime.newFixnum(stat.st_size());
     }
 
     public static ZipEntry getFileEntry(ZipFile zf, String path) throws IOException {
         ZipEntry entry = zf.getEntry(path);
         if (entry == null) {
             // try canonicalizing the path to eliminate . and .. (JRUBY-4760, JRUBY-4879)
             String prefix = new File(".").getCanonicalPath();
             entry = zf.getEntry(new File(path).getCanonicalPath().substring(prefix.length() + 1).replaceAll("\\\\", "/"));
         }
         return entry;
     }
 
     public static ZipEntry getDirOrFileEntry(ZipFile zf, String path) throws IOException {
         ZipEntry entry = zf.getEntry(path + "/"); // first try as directory
         if (entry == null) {
             // try canonicalizing the path to eliminate . and .. (JRUBY-4760, JRUBY-4879)
             String prefix = new File(".").getCanonicalPath();
             entry = zf.getEntry(new File(path + "/").getCanonicalPath().substring(prefix.length() + 1).replaceAll("\\\\", "/"));
 
             if (entry == null) {
                 // try as file
                 entry = getFileEntry(zf, path);
             }
         }
         return entry;
     }
 
     /**
      * Joy of POSIX, only way to get the umask is to set the umask,
      * then set it back. That's unsafe in a threaded program. We
      * minimize but may not totally remove this race by caching the
      * obtained or previously set (see umask() above) umask and using
      * that as the initial set value which, cross fingers, is a
      * no-op. The cache access is then synchronized. TODO: Better?
      */
     private static int getUmaskSafe( Ruby runtime ) {
         synchronized (_umaskLock) {
             final int umask = runtime.getPosix().umask(_cachedUmask);
             if (_cachedUmask != umask ) {
                 runtime.getPosix().umask(umask);
                 _cachedUmask = umask;
             }
             return umask;
         }
     }
 
     /**
      * Extract a timeval (an array of 2 longs: seconds and microseconds from epoch) from
      * an IRubyObject.
      */
     private static long[] extractTimeval(Ruby runtime, IRubyObject value) {
         long[] timeval = new long[2];
 
         if (value instanceof RubyFloat) {
             timeval[0] = Platform.IS_32_BIT ? RubyNumeric.num2int(value) : RubyNumeric.num2long(value);
             double fraction = ((RubyFloat) value).getDoubleValue() % 1.0;
             timeval[1] = (long)(fraction * 1e6 + 0.5);
         } else if (value instanceof RubyNumeric) {
             timeval[0] = Platform.IS_32_BIT ? RubyNumeric.num2int(value) : RubyNumeric.num2long(value);
             timeval[1] = 0;
         } else {
             RubyTime time;
             if (value instanceof RubyTime) {
                 time = ((RubyTime) value);
             } else {
                 time = (RubyTime) TypeConverter.convertToType(value, runtime.getTime(), "to_time", true);
             }
             timeval[0] = Platform.IS_32_BIT ? RubyNumeric.num2int(time.to_i()) : RubyNumeric.num2long(time.to_i());
             timeval[1] = Platform.IS_32_BIT ? RubyNumeric.num2int(time.usec()) : RubyNumeric.num2long(time.usec());
         }
 
         return timeval;
     }
 
     // Fast path since JNA stat is about 10x slower than this
     private static IRubyObject getLastModified(Ruby runtime, String path) {
         JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), path);
         
         if (!file.exists()) {
             throw runtime.newErrnoENOENTError(path);
         }
         
         return runtime.newTime(file.lastModified());
     }
 }
diff --git a/src/org/jruby/runtime/load/ExternalScript.java b/src/org/jruby/runtime/load/ExternalScript.java
index 3e20bddc3f..cf3a4e985a 100644
--- a/src/org/jruby/runtime/load/ExternalScript.java
+++ b/src/org/jruby/runtime/load/ExternalScript.java
@@ -1,76 +1,81 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2005 Thomas E. Enebo <enebo@acm.org>
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
 package org.jruby.runtime.load;
 
 import java.io.IOException;
 import java.io.InputStream;
 import org.jruby.Ruby;
 
 import static org.jruby.util.JRubyFile.normalizeSeps;
+import static org.jruby.RubyFile.canonicalize;
 
 public class ExternalScript implements Library {
     private final LoadServiceResource resource;
     
     public ExternalScript(LoadServiceResource resource, String name) {
         this.resource = resource;
     }
 
     public void load(Ruby runtime, boolean wrap) {
         InputStream in = null;
         try {
             in = resource.getInputStream();
             String name = normalizeSeps(resource.getName());
 
             if (runtime.getInstanceConfig().getCompileMode().shouldPrecompileAll()) {
                 runtime.compileAndLoadFile(name, in, wrap);
             } else {
                 java.io.File path = resource.getPath();
 
                 if(path != null && !resource.isAbsolute()) {
-                    name = normalizeSeps(path.getCanonicalPath());
+                    // Note: We use RubyFile's canonicalize rather than Java's,
+                    // because Java's will follow symlinks and result in __FILE__
+                    // being set to the target of the symlink rather than the
+                    // filename provided.
+                    name = normalizeSeps(canonicalize(path.getPath()));
                 }
 
                 runtime.loadFile(name, in, wrap);
             }
 
 
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } finally {
             try { in.close(); } catch (Exception ex) {}
         }
     }
 
     @Override
     public String toString() {
         return "ExternalScript: " + resource.getName();
     }
 }
diff --git a/test/test_file.rb b/test/test_file.rb
index 56d601b326..b26c8966d9 100644
--- a/test/test_file.rb
+++ b/test/test_file.rb
@@ -1,1030 +1,1051 @@
 # -*- coding: utf-8 -*-
 require 'test/unit'
 require 'rbconfig'
 require 'fileutils'
 require 'tempfile'
 require 'pathname'
 
 class TestFile < Test::Unit::TestCase
   WINDOWS = Config::CONFIG['host_os'] =~ /Windows|mswin/
 
   def setup
     @teardown_blocks = []
   end
 
   def teardown
     @teardown_blocks.each do |b|
       b.call
     end
   end
 
   def jruby_specific_test
     flunk("JRuby specific test") unless defined?(JRUBY_VERSION)
   end
   
   def test_file_separator_constants_defined
     assert(File::SEPARATOR)
     assert(File::PATH_SEPARATOR)
   end
 
   def test_basename
     assert_equal("", File.basename(""))
     assert_equal("a", File.basename("a"))
     assert_equal("b", File.basename("a/b"))
     assert_equal("c", File.basename("a/b/c"))
     
     assert_equal("b", File.basename("a/b", ""))
     assert_equal("b", File.basename("a/bc", "c"))
     assert_equal("b", File.basename("a/b.c", ".c"))
     assert_equal("b", File.basename("a/b.c", ".*"))
     assert_equal(".c", File.basename("a/.c", ".*"))
     
     
     assert_equal("a", File.basename("a/"))
     assert_equal("b", File.basename("a/b/"))
     assert_equal("/", File.basename("/"))
   end
 
   def test_expand_path_cross_platform
     assert_equal(Dir.pwd, File.expand_path(""))
     assert_equal(Dir.pwd, File.expand_path("."))
     assert_equal(Dir.pwd, File.expand_path(".", "."))
     assert_equal(Dir.pwd, File.expand_path("", "."))
     assert_equal(Dir.pwd, File.expand_path(".", ""))
     assert_equal(Dir.pwd, File.expand_path("", ""))
 
     assert_equal(File.join(Dir.pwd, "x/y/z/a/b"), File.expand_path("a/b", "x/y/z"))
     assert_equal(File.join(Dir.pwd, "bin"), File.expand_path("../../bin", "tmp/x"))
     
     # JRUBY-2143
     assert_nothing_raised {
       File.expand_path("../../bin", "/tmp/x")
       File.expand_path("/bin", "/tmp/x")
       File.expand_path("../../bin", "C:/tmp/x")
       File.expand_path("../bin", "C:/tmp/x")
     }
   end
 
   def test_expand_path_nil
     assert_raise(TypeError) { File.expand_path(nil) }
     assert_raise(TypeError) { File.expand_path(nil, "/") }
     assert_raise(TypeError) { File.expand_path(nil, nil) }
   end
 
   # JRUBY-1116: these are currently broken on windows
   # what are these testing anyway?!?!
   if WINDOWS
     def test_windows_basename
       assert_equal "", File.basename("c:")
       assert_equal "\\", File.basename("c:\\")
       assert_equal "abc", File.basename("c:abc")
     end
 
     # JRUBY-2052: this is important for fileutils
     def test_windows_dirname
       assert_equal("C:/", File.dirname("C:/"))
       assert_equal("C:\\", File.dirname("C:\\"))
       assert_equal("C:/", File.dirname("C:///////"))
       assert_equal("C:\\", File.dirname("C:\\\\\\\\"))
       assert_equal("C:/", File.dirname("C:///////blah"))
       assert_equal("C:\\", File.dirname("C:\\\\\\\\blah"))
       assert_equal("C:.", File.dirname("C:blah"))
       assert_equal "C:/", File.dirname("C:/temp/")
       assert_equal "c:\\", File.dirname('c:\\temp')
       assert_equal "C:.", File.dirname("C:")
       assert_equal "C:/temp", File.dirname("C:/temp/foobar.txt")
     end
 
     def test_expand_path_windows
       assert_equal("C:/", File.expand_path("C:/"))
       assert_equal("C:/dir", File.expand_path("C:/dir"))
       assert_equal("C:/dir", File.expand_path("C:/dir/two/../"))
 
       assert_equal("C:/dir/two", File.expand_path("C:/dir/two/", "D:/"))
       assert_equal("C:/", File.expand_path("C:/", nil))
 
       # JRUBY-2161
       assert_equal("C:/", File.expand_path("C:/dir/../"))
       assert_equal("C:/", File.expand_path("C:/.."))
       assert_equal("C:/", File.expand_path("C:/../../"))
       assert_equal("C:/", File.expand_path("..", "C:/"))
       assert_equal("C:/", File.expand_path("..", "C:"))
       assert_equal("C:/", File.expand_path("C:/dir/two/../../"))
       assert_equal("C:/", File.expand_path("C:/dir/two/../../../../../"))
 
       # JRUBY-546
       current_drive_letter = Dir.pwd[0..2]
       assert_equal(current_drive_letter, File.expand_path(".", "/"))
       assert_equal(current_drive_letter, File.expand_path("..", "/"))
       assert_equal(current_drive_letter, File.expand_path("/", "/"))
       assert_equal(current_drive_letter, File.expand_path("../..", "/"))
       assert_equal(current_drive_letter, File.expand_path("../..", "/dir/two"))
       assert_equal(current_drive_letter + "dir",
         File.expand_path("../..", "/dir/two/three"))
       assert_equal(current_drive_letter, File.expand_path("/../..", "/"))
       assert_equal(current_drive_letter + "hello", File.expand_path("hello", "/"))
       assert_equal(current_drive_letter, File.expand_path("hello/..", "/"))
       assert_equal(current_drive_letter, File.expand_path("hello/../../..", "/"))
       assert_equal(current_drive_letter + "three/four",
         File.expand_path("/three/four", "/dir/two"))
       assert_equal(current_drive_letter + "two", File.expand_path("/two", "/one"))
       assert_equal(current_drive_letter + "three/four",
         File.expand_path("/three/four", "/dir/two"))
 
       assert_equal("C:/two", File.expand_path("/two", "C:/one"))
       assert_equal("C:/two", File.expand_path("/two", "C:/one/.."))
       assert_equal("C:/", File.expand_path("/two/..", "C:/one/.."))
       assert_equal("C:/", File.expand_path("/two/..", "C:/one"))
       
       assert_equal("//two", File.expand_path("//two", "/one"))
       assert_equal("//two", File.expand_path("//two", "//one"))
       assert_equal("//two", File.expand_path("//two", "///one"))
       assert_equal("//two", File.expand_path("//two", "////one"))
       assert_equal("//", File.expand_path("//", "//one"))
 
       # Corner cases that fail with JRuby on Windows (but pass with MRI 1.8.6)
       # 
       ### assert_equal("///two", File.expand_path("///two", "/one"))
       ### assert_equal("///two", File.expand_path("///two/..", "/one"))
       ### assert_equal("////two", File.expand_path("////two", "/one"))
       ### assert_equal("////two", File.expand_path("////two", "//one"))
       ### assert_equal("//two", File.expand_path("//two/..", "/one"))
       ### assert_equal("////two", File.expand_path("////two/..", "/one"))
       #
       ### assert_equal("//bar/foo", File.expand_path("../foo", "//bar"))
       ### assert_equal("///bar/foo", File.expand_path("../foo", "///bar"))
       ### assert_equal("//one/two", File.expand_path("/two", "//one"))
     end
 
     def test_pathname_windows
       assert_equal(Pathname('foo.bar.rb').expand_path.relative_path_from(Pathname(Dir.pwd)), Pathname('foo.bar.rb'))
     end
   else
     def test_expand_path
       assert_equal("/bin", File.expand_path("../../bin", "/foo/bar"))
       assert_equal("/foo/bin", File.expand_path("../bin", "/foo/bar"))
       assert_equal("//abc/def/jkl/mno", File.expand_path("//abc//def/jkl//mno"))
       assert_equal("//abc/def/jkl/mno/foo", File.expand_path("foo", "//abc//def/jkl//mno"))
       begin
         File.expand_path("~nonexistent")
         assert(false)
       rescue ArgumentError => e
         assert(true)
       rescue Exception => e
         assert(false)
       end
  
       assert_equal("/bin", File.expand_path("../../bin", "/tmp/x"))
       assert_equal("/bin", File.expand_path("../../bin", "/tmp"))
       assert_equal("/bin", File.expand_path("../../bin", "/"))
       assert_equal("/bin", File.expand_path("../../../../../../../bin", "/"))
       assert_equal(File.join(Dir.pwd, "x/y/z/a/b"), File.expand_path("a/b", "x/y/z"))
       assert_equal(File.join(Dir.pwd, "bin"), File.expand_path("../../bin", "tmp/x"))
       assert_equal("/bin", File.expand_path("./../foo/./.././../bin", "/a/b"))
 
       # JRUBY-2160
       assert_equal("/dir1/subdir", File.expand_path("/dir1/subdir/stuff/../"))
       assert_equal("///fedora/stuff/blah/MORE", File.expand_path("///fedora///stuff/blah/again//..////MORE/"))
       assert_equal("/", File.expand_path("/dir1/../"))
       assert_equal("/", File.expand_path("/"))
       assert_equal("/", File.expand_path("/.."))
       assert_equal("/hello", File.expand_path("/hello/world/three/../../"))
 
       assert_equal("/dir/two", File.expand_path("", "/dir/two"))
       assert_equal("/dir", File.expand_path("..", "/dir/two"))
 
       assert_equal("/file/abs", File.expand_path("/file/abs", '/abs/dir/here'))
 
       assert_equal("/", File.expand_path("/", nil))
       
       assert_equal("//two", File.expand_path("//two", "//one"))
       assert_equal("/", File.expand_path("/two/..", "//one"))
 
       # Corner cases that fail with JRuby on Linux (but pass with MRI 1.8.6)
       #
       # assert_equal("", File.expand_path("//two/..", "//one"))
       # assert_equal("", File.expand_path("///two/..", "//one"))
       # assert_equal("/blah", File.expand_path("///two/../blah", "//one"))
     end
 
     def test_expand_path_with_file_prefix
       jruby_specific_test
       assert_equal("file:/foo/bar", File.expand_path("file:/foo/bar"))
       assert_equal("file:/bar", File.expand_path("file:/foo/../bar"))
     end
 
     def test_expand_path_corner_case
       # this would fail on MRI 1.8.6 (MRI returns "/foo").
       assert_equal("//foo", File.expand_path("../foo", "//bar"))
     end
   end # if windows
 
   def test_dirname
     assert_equal(".", File.dirname(""))
     assert_equal(".", File.dirname("."))
     assert_equal(".", File.dirname(".."))
     assert_equal(".", File.dirname("a"))
     assert_equal(".", File.dirname("./a"))
     assert_equal("./a", File.dirname("./a/b"))
     assert_equal("/", File.dirname("/"))
     assert_equal("/", File.dirname("/a"))
     assert_equal("/a", File.dirname("/a/b"))
     assert_equal("/a", File.dirname("/a/b/"))
     assert_equal("/", File.dirname("/"))
   end
 
   def test_extname
     assert_equal("", File.extname(""))
     assert_equal("", File.extname("abc"))
     assert_equal(".foo", File.extname("abc.foo"))
     assert_equal(".foo", File.extname("abc.bar.foo"))
     assert_equal("", File.extname("abc.bar/foo"))
 
     assert_equal("", File.extname(".bashrc"))
     assert_equal("", File.extname("."))
     assert_equal("", File.extname("/."))
     assert_equal("", File.extname(".."))
     assert_equal("", File.extname(".foo."))
     assert_equal("", File.extname("foo."))
   end
 
   def test_fnmatch
     assert_equal(true, File.fnmatch('cat', 'cat'))
     assert_equal(false, File.fnmatch('cat', 'category'))
     assert_equal(false, File.fnmatch('c{at,ub}s', 'cats'))
     assert_equal(false, File.fnmatch('c{at,ub}s', 'cubs'))
     assert_equal(false, File.fnmatch('c{at,ub}s', 'cat'))
 
     assert_equal(true, File.fnmatch('c?t', 'cat'))
     assert_equal(false, File.fnmatch('c\?t', 'cat'))
     assert_equal(true, File.fnmatch('c\?t', 'c?t'))
     assert_equal(false, File.fnmatch('c??t', 'cat'))
     assert_equal(true, File.fnmatch('c*', 'cats'));
     assert_equal(true, File.fnmatch('c*t', 'cat'))
     #assert_equal(true, File.fnmatch('c\at', 'cat')) # Doesn't work correctly on both Unix and Win32
     assert_equal(false, File.fnmatch('c\at', 'cat', File::FNM_NOESCAPE))
     assert_equal(true, File.fnmatch('a?b', 'a/b'))
     assert_equal(false, File.fnmatch('a?b', 'a/b', File::FNM_PATHNAME))
     assert_equal(false, File.fnmatch('a?b', 'a/b', File::FNM_PATHNAME))
 
     assert_equal(false, File.fnmatch('*', '.profile'))
     assert_equal(true, File.fnmatch('*', '.profile', File::FNM_DOTMATCH))
     assert_equal(true, File.fnmatch('*', 'dave/.profile'))
     assert_equal(true, File.fnmatch('*', 'dave/.profile', File::FNM_DOTMATCH))
     assert_equal(false, File.fnmatch('*', 'dave/.profile', File::FNM_PATHNAME))
 
     assert_equal(false, File.fnmatch("/.ht*",""))
     assert_equal(false, File.fnmatch("/*~",""))
     assert_equal(false, File.fnmatch("/.ht*","/"))
     assert_equal(false, File.fnmatch("/*~","/"))
     assert_equal(false, File.fnmatch("/.ht*",""))
     assert_equal(false, File.fnmatch("/*~",""))
     assert_equal(false, File.fnmatch("/.ht*","/stylesheets"))
     assert_equal(false, File.fnmatch("/*~","/stylesheets"))
     assert_equal(false, File.fnmatch("/.ht*",""))
     assert_equal(false, File.fnmatch("/*~",""))
     assert_equal(false, File.fnmatch("/.ht*","/favicon.ico"))
     assert_equal(false, File.fnmatch("/*~","/favicon.ico"))
     
     # JRUBY-1986, make sure that fnmatch is sharing aware
     assert_equal(true, File.fnmatch("foobar"[/foo(.*)/, 1], "bar"))
   end
 
   # JRUBY-2196
   def test_fnmatch_double_star
     assert(File.fnmatch('**/foo', 'a/b/c/foo', File::FNM_PATHNAME))
     assert(File.fnmatch('**/foo', '/foo', File::FNM_PATHNAME))
     assert(!File.fnmatch('**/foo', 'a/.b/c/foo', File::FNM_PATHNAME))
     assert(File.fnmatch('**/foo', 'a/.b/c/foo', File::FNM_PATHNAME | File::FNM_DOTMATCH))
     assert(File.fnmatch('**/foo', '/root/foo', File::FNM_PATHNAME))
     assert(File.fnmatch('**/foo', 'c:/root/foo', File::FNM_PATHNAME))
     assert(File.fnmatch("lib/**/*.rb", "lib/a.rb", File::FNM_PATHNAME | File::FNM_DOTMATCH))
     assert(File.fnmatch("lib/**/*.rb", "lib/a/b.rb", File::FNM_PATHNAME | File::FNM_DOTMATCH))
     assert(File.fnmatch('**/b/**/*', 'c/a/b/c/t', File::FNM_PATHNAME))
     assert(File.fnmatch('c/**/b/**/*', 'c/a/b/c/t', File::FNM_PATHNAME))
     assert(File.fnmatch('c**/**/b/**/*', 'c/a/b/c/t', File::FNM_PATHNAME))
     assert(File.fnmatch('h**o/**/b/**/*', 'hello/a/b/c/t', File::FNM_PATHNAME))
     assert(!File.fnmatch('h**o/**/b/**', 'hello/a/b/c/t', File::FNM_PATHNAME))
     assert(File.fnmatch('**', 'hello', File::FNM_PATHNAME))
     assert(!File.fnmatch('**/', 'hello', File::FNM_PATHNAME))
     assert(File.fnmatch('**/*', 'hello', File::FNM_PATHNAME))
     assert(File.fnmatch("**/*", "one/two/three/four", File::FNM_PATHNAME))
     assert(!File.fnmatch("**", "one/two/three", File::FNM_PATHNAME))
     assert(!File.fnmatch("**/three", ".one/two/three", File::FNM_PATHNAME))
     assert(File.fnmatch("**/three", ".one/two/three", File::FNM_PATHNAME | File::FNM_DOTMATCH))
     assert(!File.fnmatch("*/**", "one/two/three", File::FNM_PATHNAME))
     assert(!File.fnmatch("*/**/", "one/two/three", File::FNM_PATHNAME))
     assert(File.fnmatch("*/**/*", "one/two/three", File::FNM_PATHNAME))
     assert(File.fnmatch("**/*", ".one/two/three/four", File::FNM_PATHNAME | File::FNM_DOTMATCH))
     assert(!File.fnmatch("**/.one/*", ".one/.two/.three/.four", File::FNM_PATHNAME | File::FNM_DOTMATCH))
   end
 
   # JRUBY-2199
   def test_fnmatch_bracket_pattern
     assert(!File.fnmatch('[a-z]', 'D'))
     assert(File.fnmatch('[a-z]', 'D', File::FNM_CASEFOLD))
     assert(!File.fnmatch('[a-zA-Y]', 'Z'))
     assert(File.fnmatch('[^a-zA-Y]', 'Z'))
     assert(File.fnmatch('[a-zA-Y]', 'Z', File::FNM_CASEFOLD))
     assert(File.fnmatch('[a-zA-Z]', 'Z'))
     assert(File.fnmatch('[a-zA-Z]', 'A'))
     assert(File.fnmatch('[a-zA-Z]', 'a'))
     assert(!File.fnmatch('[b-zA-Z]', 'a'))
     assert(File.fnmatch('[^b-zA-Z]', 'a'))
     assert(File.fnmatch('[b-zA-Z]', 'a', File::FNM_CASEFOLD))
   end
 
   def test_join
     [
       ["a", "b", "c", "d"],
       ["a"],
       [],
       ["a", "b", "..", "c"]
     ].each do |a|
       assert_equal(a.join(File::SEPARATOR), File.join(*a))
     end
 
     assert_equal("////heh////bar/heh", File.join("////heh////bar", "heh"))
     assert_equal("/heh/bar/heh", File.join("/heh/bar/", "heh"))
     assert_equal("/heh/bar/heh", File.join("/heh/bar/", "/heh"))
     assert_equal("/heh//bar/heh", File.join("/heh//bar/", "/heh"))
     assert_equal("/heh/bar/heh", File.join("/heh/", "/bar/", "/heh"))
     assert_equal("/HEH/BAR/FOO/HOH", File.join("/HEH", ["/BAR", "FOO"], "HOH"))
     assert_equal("/heh/bar", File.join("/heh//", "/bar"))
     assert_equal("/heh///bar", File.join("/heh", "///bar"))
   end
 
   def test_split
     assert_equal([".", ""], File.split(""))
     assert_equal([".", "."], File.split("."))
     assert_equal([".", ".."], File.split(".."))
     assert_equal(["/", "/"], File.split("/"))
     assert_equal([".", "a"], File.split("a"))
     assert_equal([".", "a"], File.split("a/"))
     assert_equal(["a", "b"], File.split("a/b"))
     assert_equal(["a/b", "c"], File.split("a/b/c"))
     assert_equal(["/", "a"], File.split("/a"))
     assert_equal(["/", "a"], File.split("/a/"))
     assert_equal(["/a", "b"], File.split("/a/b"))
     assert_equal(["/a/b", "c"], File.split("/a/b/c"))
     #assert_equal(["//", "a"], File.split("//a"))
     assert_equal(["../a/..", "b"], File.split("../a/../b/"))
   end
 
   def test_io_readlines
     # IO#readlines, IO::readlines, open, close, delete, ...
     assert_raises(Errno::ENOENT) { File.open("NO_SUCH_FILE_EVER") }
     f = open("testFile_tmp", "w")
     f.write("one\ntwo\nthree\n")
     f.close
 
     f = open("testFile_tmp")
     assert_equal(["one", "two", "three"],
                f.readlines.collect {|l| l.strip })
     f.close
 
     assert_equal(["one", "two", "three"],
                IO.readlines("testFile_tmp").collect {|l| l.strip })
     assert(File.delete("testFile_tmp"))
   end
 
   def test_mkdir
     begin
       Dir.mkdir("dir_tmp")
       assert(File.lstat("dir_tmp").directory?)
     ensure
       Dir.rmdir("dir_tmp")
     end
   end
 
   def test_file_query # - file?
     assert(File.file?('test/test_file.rb'))
     assert(! File.file?('test'))
   end
 
   def test_file_exist_query
     assert(File.exist?('test'))
   end
 
   def test_file_exist_in_jar_file
     jruby_specific_test
 
     assert(File.exist?("file:" + File.expand_path("test/dir with spaces/test_jar.jar") + "!/abc/foo.rb"))
     assert(File.exist?("file:" + File.expand_path("test/dir with spaces/test_jar.jar") + "!/inside_jar.rb"))
     assert(!File.exist?("file:" + File.expand_path("test/dir with spaces/test_jar.jar") + "!/inside_jar2.rb"))
     assert(!File.exist?("file:" + File.expand_path("test/dir with spaces/test_jar.jar") + "!/"))
   end
 
   def test_file_read_in_jar_file
     jruby_specific_test
 
     assert_equal("foobarx\n", File.read("file:" + File.expand_path("test/test_jar2.jar") + "!/test_value.rb"))
 
     assert_raises(Errno::ENOENT) do 
       File.read("file:" + File.expand_path("test/test_jar2.jar") + "!/inside_jar2.rb")
     end
 
     assert_raises(Errno::ENOENT) do 
       File.read("file:" + File.expand_path("test/test_jar3.jar") + "!/inside_jar2.rb")
     end
 
     val = ""
     open("file:" + File.expand_path("test/test_jar2.jar") + "!/test_value.rb") do |f|
       val = f.read
     end
     assert_equal "foobarx\n", val
 
     values = ""
     File.open("file:" + File.expand_path("test/test_jar2.jar") + "!/test_value.rb") do |f|
       f.each do |s|
         values << s
       end
     end
 
     assert_equal "foobarx\n", values
   end
   
   # JRUBY-2357
   def test_truncate_file_in_jar_file
     jruby_specific_test
     File.open("file:" + File.expand_path("test/test_jar2.jar") + "!/test_value.rb", "r+") do |f|
       assert_raise(Errno::EINVAL) { f.truncate(2) }
     end
   end
 
   # JRUBY-1886
   def test_file_truncated_after_changing_directory
     subdir = "./testDir_1"
     Dir.mkdir(subdir)
     Dir.chdir(subdir) { |dir|
       begin
         file = File.open("__dummy_file.txt", "wb") { |file|
           file.write("dummy text")
           file
         }
         assert_nothing_raised { File.truncate(file.path, 0) }
       ensure
         File.unlink(file.path)
       end
     }
   ensure
     Dir.rmdir(subdir)
   end
 
   def test_file_size_query
     assert(File.size?('build.xml'))
   end
 
   # JRUBY-2275
   def test_file_size_empty_file
     filename = "__empty_test__file"
     File.open(filename, "w+") { }
     assert_equal(nil, File.size?(filename))
     assert_equal(nil, FileTest.size?(filename))
     assert_equal(0, File.size(filename))
     assert_equal(0, FileTest.size(filename))
   ensure
     File.delete(filename)
   end
 
   # JRUBY-2524
   def test_filetest_exists_uri_prefixes
     assert(!FileTest.exists?("file:/"))
     assert(!FileTest.exists?("file:/!"))
   end
   
   # JRUBY-2524
   def test_file_stat_uri_prefixes
     assert_raise(Errno::ENOENT) do
       File.lstat("file:")
     end
     assert_raise(Errno::ENOENT) do
       File.lstat("file:!")
     end
     
     assert_raise(Errno::ENOENT) do
       File.stat("file:")
     end
     assert_raise(Errno::ENOENT) do
       File.stat("file:!")
     end
   end
   
   # JRUBY-2524
   def test_file_time_uri_prefixes
     assert_raise(Errno::ENOENT) do
       File.atime("file:")
     end
     assert_raise(Errno::ENOENT) do
       File.atime("file:!")
     end
     
     assert_raise(Errno::ENOENT) do
       File.ctime("file:")
     end
     assert_raise(Errno::ENOENT) do
       File.ctime("file:!") 
     end    
   end
   
   def test_file_open_utime
     filename = "__test__file"
     File.open(filename, "w") {|f| }
     time = Time.now - 3600
     File.utime(time, time, filename)
     # File mtime resolution may not be sub-second on all platforms (e.g., windows)
     # allow for some slop
     assert((time.to_i - File.atime(filename).to_i).abs < 5)
     assert((time.to_i - File.mtime(filename).to_i).abs < 5)
     File.unlink(filename)
   end
   
   def test_file_utime_nil
     filename = '__test__file'
     File.open(filename, 'w') {|f| }
     time = File.mtime(filename)
     sleep 2
     File.utime(nil, nil, filename)
     assert((File.atime(filename).to_i - time.to_i) >= 2)
     assert((File.mtime(filename).to_i - time.to_i) >= 2)
     File.unlink(filename)
   end
   
   def test_file_utime_bad_time_raises_typeerror
     args = [ [], {}, '4000' ]
     filename = '__test__file'
     File.open(filename, 'w') {|f| }
     args.each do |arg|
       assert_raises(TypeError) {  File.utime(arg, nil, filename) }
       assert_raises(TypeError) {  File.utime(nil, arg, filename) }
       assert_raises(TypeError) {  File.utime(arg, arg, filename) }
     end
     time = Time.now
     assert_raises(TypeError) {  File.utime(time, nil, filename) }
     assert_raises(TypeError) {  File.utime(nil, time, filename) }
     File.unlink(filename)
   end
   
   # JRUBY-1982 and JRUBY-1983
   def test_file_mtime_after_fileutils_touch
     filename = '__test__file'
     File.open(filename, 'w') {|f| }
     time = File.mtime(filename)
     
     FileUtils.touch(filename)
     # File mtime resolution may not be sub-second on all platforms (e.g., windows)
     # allow for some slop
     assert((time.to_i - File.mtime(filename).to_i).abs < 2)
   end
 
   def test_file_stat # File::Stat tests
     stat = File.stat('test');
     stat2 = File.stat('build.xml');
     assert(stat.directory?)
     assert(stat2.file?)
     assert_equal("directory", stat.ftype)
     assert_equal("file", stat2.ftype)
     assert(stat2.readable?)
   end
 
   if (WINDOWS)
     # JRUBY-2351
     def test_not_implemented_methods_on_windows
       # the goal here is to make sure that those "weird"
       # POSIX methods don't break JRuby, since there were
       # numerous regressions in this area
       begin 
         # TODO: See JRUBY-2818.
         File.readlink('build.xml')
       rescue NotImplementedError
       rescue Errno::EINVAL  # TODO: this exception is wrong (see bug above)
       end
 
       begin
         # TODO: See JRUBY-2817.
         File.chown(100, 100, 'build.xml')
       rescue NotImplementedError
       end
 
       assert_raise(NotImplementedError) { File.lchown(100, 100, 'build.xml') }
       assert_raise(NotImplementedError) { File.lchmod(0644, 'build.xml') }
     end
   end
 
   unless(WINDOWS) # no symlinks on Windows
     def test_file_symlink
       # Test File.symlink? if possible
       system("ln -s build.xml build.xml.link")
       if File.exist? "build.xml.link"
         assert(File.symlink?("build.xml.link"))
         # JRUBY-683 -  Test that symlinks don't effect Dir and File.expand_path
         assert_equal(['build.xml.link'], Dir['build.xml.link'])
         assert_equal(File.expand_path('.') + '/build.xml.link', File.expand_path('build.xml.link'))
         File.delete("build.xml.link")
       end
     end
+    def test_require_symlink
+      # Create a ruby file that sets a global variable to its view of __FILE__
+      f = File.open("real_file.rb", "w")
+      f.write("$test_require_symlink_filename=__FILE__")
+      f.close()
+      system("ln -s real_file.rb linked_file.rb")
+      assert(File.symlink?("linked_file.rb"))
+      # JRUBY-5167 - Test that symlinks don't effect __FILE__ during load or require
+      # Note: This bug only manifests for absolute paths that point to symlinks.
+      abs_path = File.join(Dir.pwd, "real_file.rb")
+      require abs_path
+      assert_equal($test_require_symlink_filename, abs_path)
+      abs_path_linked = File.join(Dir.pwd, "linked_file.rb")
+      require abs_path_linked
+      assert_equal($test_require_symlink_filename, abs_path_linked)
+      load abs_path_linked
+      assert_equal($test_require_symlink_filename, abs_path_linked)
+    ensure
+      File.delete("real_file.rb")
+      File.delete("linked_file.rb")
+    end
   end
 
   def test_file_times
     # Note: atime, mtime, ctime are all implemented using modification time
     assert_nothing_raised {
       File.mtime("build.xml")
       File.atime("build.xml")
       File.ctime("build.xml")
       File.new("build.xml").mtime
       File.new("build.xml").atime
       File.new("build.xml").ctime
     }
 
     assert_raises(Errno::ENOENT) { File.mtime("NO_SUCH_FILE_EVER") }
   end
 
   def test_file_times_types
     # Note: atime, mtime, ctime are all implemented using modification time
     assert_instance_of Time, File.mtime("build.xml")
     assert_instance_of Time, File.atime("build.xml")
     assert_instance_of Time, File.ctime("build.xml")
     assert_instance_of Time, File.new("build.xml").mtime
     assert_instance_of Time, File.new("build.xml").atime
     assert_instance_of Time, File.new("build.xml").ctime
   end
 
   def test_more_constants
     jruby_specific_test
     
     # FIXME: Not sure how I feel about pulling in Java here
     if Java::java.lang.System.get_property("file.separator") == '/'
       assert_equal(nil, File::ALT_SEPARATOR)
     else
       assert_equal("\\", File::ALT_SEPARATOR)
     end
   end
 
   # JRUBY-2572
   def test_fnm_syscase_constant
     if (WINDOWS)
       assert_equal(File::FNM_CASEFOLD, File::FNM_SYSCASE)
       assert File.fnmatch?('cat', 'CAT', File::FNM_SYSCASE)
     else
       assert_not_equal(File::FNM_CASEFOLD, File::FNM_SYSCASE)
       assert !File.fnmatch?('cat', 'CAT', File::FNM_SYSCASE)
     end
   end
 
   def test_truncate
     # JRUBY-1025: negative int passed to truncate should raise EINVAL
     filename = "__truncate_test_file"
     assert_raises(Errno::EINVAL) {
       begin
         f = File.open(filename, 'w')
         f.truncate(-1)
       ensure
         f.close
       end
     }
     assert_raises(Errno::EINVAL) {
       File.truncate(filename, -1)
     }
   ensure
     File.delete(filename)
   end
 
   def test_file_utf8
     # name contains a German "umlaut", maybe this should be encoded as Unicode integer (\u00FC) somehow
     filename = 'jrüby'  
     f = File.new(filename, File::CREAT)
     begin
       assert_equal(nil, f.read(1))
       
       assert File.file?(filename)
       assert File.exist?(filename)
     ensure
       f.close
       File.delete(filename)
       
       assert !File.file?(filename)
       assert !File.exist?(filename)
     end
   end
   
   def test_file_create
     filename = '2nnever'
     f = File.new(filename, File::CREAT)
     begin
       assert_equal(nil, f.read(1))
     ensure
       f.close
       File.delete(filename)
     end
 
     f = File.new(filename, File::CREAT)
     begin
       assert_raises(IOError) { f << 'b' }
     ensure
       f.close
       File.delete(filename)
     end
   end
 
   # http://jira.codehaus.org/browse/JRUBY-1023
   def test_file_reuse_fileno
     fh = File.new(STDIN.fileno, 'r')
     assert_equal(STDIN.fileno, fh.fileno)
   end
 
   # http://jira.codehaus.org/browse/JRUBY-1231
   def test_file_directory_empty_name
     assert !File.directory?("")
     assert !FileTest.directory?("")
     assert_raises(Errno::ENOENT) { File::Stat.new("") }
   end
   
   def test_file_test
     assert(FileTest.file?('test/test_file.rb'))
     assert(! FileTest.file?('test'))
   end
 
   def test_flock
     filename = '__lock_test__'
     file = File.open(filename,'w')
     file.flock(File::LOCK_EX | File::LOCK_NB)
     assert_equal(0, file.flock(File::LOCK_UN | File::LOCK_NB))
     file.close
     File.delete(filename)
   end
 
   # JRUBY-1214
   def test_flock_exclusive_on_readonly
     filename = '__lock_test_2_'
     File.open(filename, "w+") { }
     File.open(filename, "r") do |file|
       begin
         assert_nothing_raised {
           file.flock(File::LOCK_EX)
         }
       ensure
         file.flock(File::LOCK_UN)
       end
     end
     File.delete(filename)
   end
 
   def test_truncate_doesnt_create_file
     name = "___foo_bar___"
     assert(!File.exists?(name))
 
     assert_raises(Errno::ENOENT) { File.truncate(name, 100) }
 
     assert(!File.exists?(name))
   end
 
   # JRUBY-2340
   def test_opening_readonly_file_for_write_raises_eacces
     filename = "__read_only__"
 
     begin
       File.open(filename, "w+", 0444) { }
       assert_raise(Errno::EACCES) { File.open(filename, "w") { } }
     ensure
       File.delete(filename)
     end
   end
   
   # JRUBY-2397
   unless(WINDOWS)
     def test_chown_accepts_nil_and_minus_one
       # chown
       assert_equal(1, File.chown(-1, -1, 'build.xml'))
       assert_equal(1, File.chown(nil, nil, 'build.xml'))
       # lchown
       assert_equal(1, File.lchown(-1, -1, 'build.xml'))
       assert_equal(1, File.lchown(nil, nil, 'build.xml'))
 
       File.open('build.xml') { |file|
         # chown
         assert_equal(0, file.chown(-1, -1))
 	assert_equal(0, file.chown(nil, nil))
         # lchown
 	# NOTE: hmm, it seems that MRI
 	# doesn't have File#lchown method at all!
         assert_equal(0, file.lchown(-1, -1))
         assert_equal(0, file.lchown(nil, nil))
       }
     end
   end
 
   # JRUBY-2491
   def test_umask_noarg_does_not_zero
     mask = 0200
     orig_mask = File.umask(mask)
 
     return if windows_64bit?
 
     assert_equal(mask, File.umask)
     # Subsequent calls should still return the same umask, not zero
     assert_equal(mask, File.umask)
   ensure
     File.umask(orig_mask)
   end
   
   # JRUBY-4937
   def test_umask_respects_existing_umask_value
     return if windows_64bit?
     
     orig_mask = File.umask
     # Cleanup old test files just in case
     FileUtils.rm_rf %w[ file_test_out.1.0644 file_test_out.2 ]
 
     File.umask( 0172 ) # Set umask to fixed weird test value
     open( "file_test_out.1.0644", 'w', 0707 ) { |f| assert_equal(0172, File.umask) }
     open( "file_test_out.2",      'w'       ) { |f| assert_equal(0172, File.umask) }
     
   ensure
     FileUtils.rm_rf %w[ file_test_out.1.0644 file_test_out.2 ]
     File.umask(orig_mask)
   end
 
   def test_allow_override_of_make_tmpname
     # mimics behavior of attachment_fu, which overrides private #make_tmpname
     Tempfile.class_eval do
       alias_method :save_make_tmpname, :make_tmpname
       def make_tmpname(basename, n)
         ext = nil
         sprintf("%s%d-%d%s", basename.to_s.gsub(/\.\w+$/) { |s| ext = s; '' }, $$, n, ext)
       end
     end
 
     @teardown_blocks << proc do
       Tempfile.class_eval { alias_method :make_tmpname, :save_make_tmpname }
     end
 
     begin
       t = Tempfile.new "tcttac.jpg", File.dirname(__FILE__)
       assert t.path =~ /\.jpg$/
     ensure
       t.close
     end
   end
 
   unless WINDOWS
     def test_mode_of_tempfile_is_600
       t = Tempfile.new "tcttac.jpg"
       assert_equal 0100600, File.stat(t.path).mode
     end
   end
 
   # See JRUBY-2694; we don't have 1.8.7 support yet
   def test_tempfile_with_suffix
     Tempfile.open(['prefix', 'suffix']) { |f|
       assert_match(/^prefix/, File.basename(f.path))
       assert_match(/suffix$/, f.path)
     }
   end
 
   def test_file_size
     size = File.size('build.xml')
     assert(size > 0)
     assert_equal(size, File.size(File.new('build.xml')))
   end
 
   # JRUBY-4073
   def test_file_sizes
     filename = '100_bytes.bin'
     begin
       File.open(filename, 'wb+') { |f|
         f.write('0' * 100)
       }
       assert_equal(100, File.size(filename))
       assert_equal(100, File.stat(filename).size)
       assert_equal(100, File.stat(filename).size?)
       assert_match(/\ssize=100,/, File.stat(filename).inspect)
       assert_equal(100, File::Stat.new(filename).size)
       assert_equal(100, File::Stat.new(filename).size?)
       assert_match(/\ssize=100,/, File::Stat.new(filename).inspect)
     ensure
       File.unlink(filename)
     end
   end
 
   # JRUBY-4149
   def test_open_file_sizes
     filename = '100_bytes.bin'
     begin
       File.open(filename, 'wb+') { |f|
         f.write('0' * 100)
         f.flush; f.flush
 
         assert_equal(100, f.stat.size)
         assert_equal(100, f.stat.size?)
         assert_match(/\ssize=100,/, f.stat.inspect)
 
         assert_equal(100, File.size(filename))
         assert_equal(100, File.stat(filename).size)
         assert_equal(100, File.stat(filename).size?)
         assert_match(/\ssize=100,/, File.stat(filename).inspect)
 
         assert_equal(100, File::Stat.new(filename).size)
         assert_equal(100, File::Stat.new(filename).size?)
         assert_match(/\ssize=100,/, File::Stat.new(filename).inspect)
       }
     ensure
       File.unlink(filename)
     end
   end
 
   # JRUBY-4537: File.open raises Errno::ENOENT instead of Errno::EACCES
   def test_write_open_permission_denied
     t = Tempfile.new('tmp' + File.basename(__FILE__))
     t.close
     File.open(t.path, 'w') {}
     File.chmod(0555, t.path)
     # jruby 1.4 raises ENOENT here
     assert_raises(Errno::EACCES) do
       File.open(t.path, 'w') {}
     end
     # jruby 1.4 raises ENOENT here
     assert_raises(Errno::EACCES) do
       File.open(t.path, File::WRONLY) {}
     end
   end
 
   #JRUBY-4380: File.open raises IOError instead of Errno::ENOENT
   def test_open_with_nonexisting_directory
     file_path = "/foo/bar"
     assert(!File.exist?(file_path))
     assert_raises(Errno::ENOENT) { File.open(file_path, "wb") }
   end
 
   # JRUBY-3634: File.read or File.open with a url to a file resource fails with StringIndexOutOfBounds exception
   def test_file_url
     path = File.expand_path(__FILE__)
     expect = File.read(__FILE__)[0..100]
     got = File.read("file:///#{path}")[0..100]
 
     assert_equal(expect, got)
   end
 
   def test_basename_unicode
     filename = 'dir/a ⼈〉〃〄⨶↖.pdf'
     assert_equal("a \342\274\210\343\200\211\343\200\203\343\200\204\342\250\266\342\206\226.pdf", File.basename(filename))
   end
 
   #JRUBY-4387, JRUBY-4416
   def test_file_gets_separator
     filename = 'gets.out'
     begin
       File.open(filename, "wb") do |file|
         file.print "this is a test\xFFit is only a test\ndoes it work?"
       end
 
       file = File.open("gets.out", "rb") do |file|
         assert_equal("this is a test\377", file.gets("\xFF"))
       end
     ensure
       File.unlink(filename)
     end
   end
 
   def test_file_stat_with_missing_path
     assert_raise(Errno::ENOENT) {
       File::Stat.new("file:" + File.expand_path("test/test_jar2.jar") + "!/foo_bar.rb").file?
     }
   end
 
   # JRUBY-4859
   def test_file_delete_directory
     Dir.mkdir("dir_tmp")
     assert_raise(Errno::EPERM) {
       File.delete "dir_tmp"
     }
   ensure
     Dir.rmdir("dir_tmp")
   end
 
   # JRUBY-4927
   def test_chmod_when_chdir
     pwd = Dir.getwd
     path = Tempfile.new("somewhere").path
     FileUtils.rm_rf path
     FileUtils.mkpath path
     FileUtils.mkpath File.join(path, "src")
     Dir.chdir path
 
     1.upto(4) do |i|
       File.open("src/file#{i}", "w+") {|f| f.write "file#{i} raw"}
     end
     Dir['src/*'].each do |file|
       File.chmod(0o755, file)
       assert_equal 0o755, (File.stat(file).mode & 0o755)
     end
   ensure
     FileUtils.rm_rf(path)
     Dir.chdir(pwd)
   end
   
   def windows_64bit?
     arch = java.lang.System.getProperty('sun.arch.data.model')
     WINDOWS && arch == '64'
   end
   
 end
