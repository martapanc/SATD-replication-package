diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 9b1e5b3bce..c459f11ed0 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -803,1178 +803,1179 @@ public class RubyFile extends RubyIO implements EncodingCapable {
         String f = get_path(context, filename).getUnicodeValue();
         return context.runtime.newFileStat(f, true);
     }
 
     @JRubyMethod(name = "stat", required = 1, meta = true)
     public static IRubyObject stat(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.runtime.newFileStat(f, false);
     }
 
     @JRubyMethod(name = "atime", required = 1, meta = true)
     public static IRubyObject atime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.runtime.newFileStat(f, false).atime();
     }
 
     @JRubyMethod(name = "ctime", required = 1, meta = true)
     public static IRubyObject ctime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.runtime.newFileStat(f, false).ctime();
     }
 
     @JRubyMethod(required = 1, rest = true, meta = true)
     public static IRubyObject lchmod(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         
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
         Ruby runtime = context.runtime;
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
 
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject link(ThreadContext context, IRubyObject recv, IRubyObject from, IRubyObject to) {
         Ruby runtime = context.runtime;
         RubyString fromStr = get_path(context, from);
         RubyString toStr = get_path(context, to);
 
         int ret = runtime.getPosix().link(fromStr.getUnicodeValue(), toStr.getUnicodeValue());
         if (ret != 0) {
             if (runtime.getPosix().isNative()) {
                 throw runtime.newErrnoFromInt(runtime.getPosix().errno(), String.format("(%s, %s)", fromStr, toStr));
             } else {
                 // In most cases, when there is an error during the call,
                 // the POSIX handler throws an exception, but not in case
                 // with pure Java POSIX layer (when native support is disabled),
                 // so we deal with it like this:
                 throw runtime.newErrnoEEXISTError(fromStr + " or " + toStr);
             }
         }
         return runtime.newFixnum(ret);
     }
 
     @JRubyMethod(name = "mtime", required = 1, meta = true)
     public static IRubyObject mtime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         return context.runtime.newFileStat(get_path(context, filename).getUnicodeValue(), false).mtime();
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject rename(ThreadContext context, IRubyObject recv, IRubyObject oldName, IRubyObject newName) {
         Ruby runtime = context.runtime;
         RubyString oldNameString = RubyString.stringValue(oldName);
         RubyString newNameString = RubyString.stringValue(newName);
 
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
 
         return context.runtime.newArray(dirname(context, recv, filename),
                 basename(context, recv, new IRubyObject[]{filename}));
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject symlink(ThreadContext context, IRubyObject recv, IRubyObject from, IRubyObject to) {
         Ruby runtime = context.runtime;
         RubyString fromStr = get_path(context, from);
         RubyString toStr = get_path(context, to);
         String tovalue = toStr.getUnicodeValue();
         tovalue = JRubyFile.create(runtime.getCurrentDirectory(), tovalue).getAbsolutePath();
         try {
             if (runtime.getPosix().symlink(fromStr.getUnicodeValue(), tovalue) == -1) {
                 if (runtime.getPosix().isNative()) {
                     throw runtime.newErrnoFromInt(runtime.getPosix().errno(), String.format("(%s, %s)", fromStr, toStr));
                 } else {
                     throw runtime.newErrnoEEXISTError(String.format("(%s, %s)", fromStr, toStr));
                 }
             }
         } catch (java.lang.UnsatisfiedLinkError ule) {
             throw runtime.newNotImplementedError("symlink() function is unimplemented on this machine");
         }
         
         return RubyFixnum.zero(runtime);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject readlink(ThreadContext context, IRubyObject recv, IRubyObject path) {
         Ruby runtime = context.runtime;
         JRubyFile link = file(path);
         
         try {
             String realPath = runtime.getPosix().readlink(link.toString());
         
             if (!RubyFileTest.exist_p(recv, path).isTrue()) {
                 throw runtime.newErrnoENOENTError(path.toString());
             }
         
             if (!RubyFileTest.symlink_p(recv, path).isTrue()) {
                 // Can not check earlier, File.exist? might return false yet the symlink be there
                 if (!RubyFileTest.exist_p(recv, path).isTrue()) {
                     throw runtime.newErrnoENOENTError(path.toString());
                 }
                 throw runtime.newErrnoEINVALError(path.toString());
             }
         
             if (realPath == null) {
                 throw runtime.newErrnoFromLastPOSIXErrno();
             }
 
             return runtime.newString(realPath);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     // Can we produce IOError which bypasses a close?
     @JRubyMethod(required = 2, meta = true, compat = RUBY1_8)
     public static IRubyObject truncate(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {        
         return truncateCommon(context, recv, arg1, arg2);
     }
 
     @JRubyMethod(name = "truncate", required = 2, meta = true, compat = RUBY1_9)
     public static IRubyObject truncate19(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return truncateCommon(context, recv, get_path(context, arg1), arg2);
     }
 
     @JRubyMethod(meta = true, optional = 1)
     public static IRubyObject umask(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
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
         Ruby runtime = context.runtime;
         long[] atimeval = null;
         long[] mtimeval = null;
 
         if (args[0] != runtime.getNil() || args[1] != runtime.getNil()) {
             atimeval = extractTimeval(runtime, args[0]);
             mtimeval = extractTimeval(runtime, args[1]);
         }
 
         for (int i = 2, j = args.length; i < j; i++) {
             RubyString filename = get_path(context, args[i]);
             
             JRubyFile fileToTouch = JRubyFile.create(runtime.getCurrentDirectory(),filename.getUnicodeValue());
             
             if (!fileToTouch.exists()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
 
             int result = runtime.getPosix().utimes(fileToTouch.getAbsolutePath(), atimeval, mtimeval);
             if (result == -1) {
                 throw runtime.newErrnoFromInt(runtime.getPosix().errno());
             }
         }
         
         return runtime.newFixnum(args.length - 2);
     }
     
     @JRubyMethod(name = {"unlink", "delete"}, rest = true, meta = true)
     public static IRubyObject unlink(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
          
         for (int i = 0; i < args.length; i++) {
             RubyString filename = get_path(context, args[i]);
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
 
     @JRubyMethod(name = "size", compat = RUBY1_9)
     public IRubyObject size(ThreadContext context) {
         Ruby runtime = context.runtime;
         if ((openFile.getMode() & OpenFile.WRITABLE) != 0) {
             flush();
         }
 
         try {
             FileStat stat = runtime.getPosix().fstat(
                     getOpenFileChecked().getMainStreamSafe().getDescriptor().getFileDescriptor());
             if (stat == null) {
                 throw runtime.newErrnoEACCESError(path);
             }
 
             return runtime.newFixnum(stat.st_size());
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         }
     }
 
     public String getPath() {
         return path;
     }
 
     public Encoding getEncoding() {
         return null;
     }
 
     public void setEncoding(Encoding encoding) {
         // :)
     }
 
     private IRubyObject openFile19(ThreadContext context, IRubyObject args[]) {
         Ruby runtime = context.runtime;
         RubyString filename = get_path(context, args[0]);
 
         path = adjustRootPathOnWindows(runtime, filename.asJavaString(), runtime.getCurrentDirectory());
 
         String modeString = "r";
         IOOptions modes = newIOOptions(runtime, modeString);
         RubyHash options = null;
         int perm = 0;
 
         if (args.length > 1) {
             if (args[1] instanceof RubyHash) {
                 options = (RubyHash)args[1];
             } else {
                 modes = parseIOOptions19(args[1]);
             }
         } else {
             modes = parseIOOptions19(RubyString.newString(runtime, modeString));
         }
 
         if (args.length > 2 && !args[2].isNil()) {
             if (args[2] instanceof RubyHash) {
                 options = (RubyHash)args[2];
             } else {
                 perm = getFilePermissions(args);
             }
         }
 
         if (perm > 0) {
             sysopenInternal19(context, path, options, modes, perm);
         } else {
             openInternal19(context, path, options, modes);
         }
 
         return this;
     }
 
     private IRubyObject openFile(IRubyObject args[]) {
         Ruby runtime = getRuntime();
         RubyString filename = get_path(runtime.getCurrentContext(), args[0]);
 
         path = adjustRootPathOnWindows(runtime, filename.asJavaString(), runtime.getCurrentDirectory());
 
         String modeString;
         IOOptions modes;
         int perm;
 
         if ((args.length > 1 && args[1] instanceof RubyFixnum) || (args.length > 2 && !args[2].isNil())) {
             modes = parseIOOptions(args[1]);
             perm = getFilePermissions(args);
 
             sysopenInternal(path, modes.getModeFlags(), perm);
         } else {
             modeString = "r";
             if (args.length > 1 && !args[1].isNil()) {
                 modeString = args[1].convertToString().toString();
             }
 
             openInternal(path, modeString);
         }
 
         return this;
     }
 
     private int getFilePermissions(IRubyObject[] args) {
         return (args.length > 2 && !args[2].isNil()) ? RubyNumeric.num2int(args[2]) : 438;
     }
 
     protected void sysopenInternal19(ThreadContext context, String path, RubyHash options, IOOptions ioOptions, int perm) {
         ioOptions = updateIOOptionsFromOptions(context, options, ioOptions);
 
         sysopenInternal(path, ioOptions.getModeFlags(), perm);
 
         setEncodingFromOptions(ioOptions.getEncodingOption());
     }
 
     protected void sysopenInternal(String path, ModeFlags modes, int perm) {
         openFile = new OpenFile();
 
         openFile.setPath(path);
         openFile.setMode(modes.getOpenFileFlags());
         if (modes.isBinary()) readEncoding = ASCIIEncoding.INSTANCE;
 
         int umask = getUmaskSafe( getRuntime() );
         perm = perm - (perm & umask);
 
         ChannelDescriptor descriptor = sysopen(path, modes, perm);
         openFile.setMainStream(fdopen(descriptor, modes));
     }
 
     protected void openInternal19(ThreadContext context, String path, RubyHash options, IOOptions ioOptions) {
         ioOptions = updateIOOptionsFromOptions(context, options, ioOptions);
 
         openInternal(path, ioOptions.getModeFlags());
 
         setEncodingFromOptions(ioOptions.getEncodingOption());
     }
 
     protected void openInternal(String path, ModeFlags modes) {
         if (path.startsWith("jar:")) {
             path = path.substring(4);
         }
         openFile = new OpenFile();
 
         openFile.setMode(modes.getOpenFileFlags());
         if (modes.isBinary()) readEncoding = ASCIIEncoding.INSTANCE;
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modes));
     }
 
     protected void openInternal(String path, String modeString) {
         if (path.startsWith("jar:")) {
             path = path.substring(4);
         }
         openFile = new OpenFile();
 
         IOOptions modes = newIOOptions(getRuntime(), modeString);
         openFile.setMode(modes.getModeFlags().getOpenFileFlags());
         if (modes.getModeFlags().isBinary()) readEncoding = ASCIIEncoding.INSTANCE;
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modes.getModeFlags()));
     }
 
     private ChannelDescriptor sysopen(String path, ModeFlags modes, int perm) {
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.open(
                     getRuntime().getCurrentDirectory(),
                     path,
                     modes,
                     perm,
                     getRuntime().getPosix(),
                     getRuntime().getJRubyClassLoader());
 
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
 
     private Stream fopen(String path, ModeFlags flags) {
         try {
             return ChannelStream.fopen(
                     getRuntime(),
                     path,
                     flags);
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (PermissionDeniedException pde) {
             // PDException can be thrown only when creating the file and
             // permission is denied.  See JavaDoc of PermissionDeniedException.
             throw getRuntime().newErrnoEACCESError(path);
         } catch (FileNotFoundException ex) {
             // FNFException can be thrown in both cases, when the file
             // is not found, or when permission is denied.
             // FIXME: yes, this is indeed gross.
             String message = ex.getMessage();
             
             if (message.contains(/*P*/"ermission denied") ||
                 message.contains(/*A*/"ccess is denied")) {
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
 
     // mri: FilePathValue/rb_get_path/rb_get_patch_check
     public static RubyString get_path(ThreadContext context, IRubyObject path) {
         if (context.runtime.is1_9()) {
             if (path.respondsTo("to_path")) path = path.callMethod(context, "to_path");
             
             return filePathConvert(context, path.convertToString());
         } 
           
         return path.convertToString();
     }
     
     // FIXME: MRI skips this logic on windows?  Does not make sense to me why so I left it in.
     // mri: file_path_convert
     private static RubyString filePathConvert(ThreadContext context, RubyString path) {
         Ruby runtime = context.getRuntime();
         EncodingService encodingService = runtime.getEncodingService();
         Encoding pathEncoding = path.getEncoding();
 
         // If we are not ascii and do not match fs encoding then transcode to fs.
         if (runtime.getDefaultInternalEncoding() != null &&
                 pathEncoding != encodingService.getUSAsciiEncoding() &&
                 pathEncoding != encodingService.getAscii8bitEncoding() &&
                 pathEncoding != encodingService.getFileSystemEncoding(runtime) &&
                 !path.isAsciiOnly()) {
             ByteList bytes = CharsetTranscoder.transcode(context, path.getByteList(), pathEncoding, encodingService.getFileSystemEncoding(runtime), null);
             path = RubyString.newString(runtime, bytes);
         }                
 
         return path;
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
             RubyString pathStr = get_path(runtime.getCurrentContext(), pathOrFile);
             String path = pathStr.asJavaString();
             String[] pathParts = splitURI(path);
             if (pathParts != null && pathParts[0].equals("file:")) {
                 path = pathParts[1];
             }
             return JRubyFile.create(runtime.getCurrentDirectory(), path);
         }
     }
 
     @Override
     public String toString() {
         try {
             return "RubyFile(" + path + ", " + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStreamSafe().getDescriptor()) + ")";
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
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
 
     public static ZipEntry getDirOrFileEntry(String jar, String path) throws IOException {
         String dirPath = path + "/";
         ZipFile zf = Ruby.getGlobalRuntime().getCurrentContext().runtime.getLoadService().getJarFile(jar);
         ZipEntry entry = zf.getEntry(dirPath); // first try as directory
         if (entry == null) {
             // try canonicalizing the path to eliminate . and .. (JRUBY-4760, JRUBY-4879)
             String prefix = new File(".").getCanonicalPath();
             entry = zf.getEntry(new File(dirPath).getCanonicalPath().substring(prefix.length() + 1).replaceAll("\\\\", "/"));
 
             // JRUBY-6119
             if (entry == null) {
                 Enumeration<? extends ZipEntry> entries = zf.entries();
                 while (entries.hasMoreElements()) {
                     String zipEntry = entries.nextElement().getName();
                     if (zipEntry.startsWith(dirPath)) {
                         return new ZipEntry(dirPath);
                     }
                 }
             }
 
             if (entry == null) {
                 // try as file
                 entry = getFileEntry(zf, path);
             }
         }
         return entry;
     }
 
     private static boolean startsWithDriveLetterOnWindows(String path) {
         return (path != null)
                 && Platform.IS_WINDOWS &&
                 ((path.length()>1 && path.charAt(0) == '/') ?
                         (path.length() > 2
                                 && isWindowsDriveLetter(path.charAt(1))
                                 && path.charAt(2) == ':') :
                         (path.length() > 1
                                 && isWindowsDriveLetter(path.charAt(0))
                                 && path.charAt(1) == ':'));
     }
     // adjusts paths started with '/' or '\\', on windows.
     static String adjustRootPathOnWindows(Ruby runtime, String path, String dir) {
         if (path == null || !Platform.IS_WINDOWS) return path;
 
         // MRI behavior on Windows: it treats '/' as a root of
         // a current drive (but only if SINGLE slash is present!):
         // E.g., if current work directory is
         // 'D:/home/directory', then '/' means 'D:/'.
         //
         // Basically, '/path' is treated as a *RELATIVE* path,
         // relative to the current drive. '//path' is treated
         // as absolute one.
         if ((path.startsWith("/") && !(path.length() > 2 && path.charAt(2) == ':')) || path.startsWith("\\")) {
             if (path.length() > 1 && (path.charAt(1) == '/' || path.charAt(1) == '\\')) {
                 return path;
             }
 
             // First try to use drive letter from supplied dir value,
             // then try current work dir.
             if (!startsWithDriveLetterOnWindows(dir)) {
                 dir = runtime.getCurrentDirectory();
             }
             if (dir.length() >= 2) {
                 path = dir.substring(0, 2) + path;
             }
         } else if (startsWithDriveLetterOnWindows(path) && path.length() == 2) {
             // compensate for missing slash after drive letter on windows
             path += "/";
         }
 
         return path;
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
 
     private void checkClosed(ThreadContext context) {
         openFile.checkClosed(context.runtime);
     }
 
     private static boolean isWindowsDriveLetter(char c) {
         return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
     }
 
     private static IRubyObject expandPathInternal(ThreadContext context, IRubyObject recv, IRubyObject[] args, boolean expandUser) {
         Ruby runtime = context.runtime;
 
         String relativePath = get_path(context, args[0]).getUnicodeValue();
         String[] uriParts = splitURI(relativePath);
         String cwd = null;
 
         // Handle ~user paths
         if (expandUser) {
             relativePath = expandUserPath(context, relativePath);
         }
 
         if (uriParts != null) {
             relativePath = uriParts[1];
         }
 
         // If there's a second argument, it's the path to which the first
         // argument is relative.
         if (args.length == 2 && !args[1].isNil()) {
             cwd = get_path(context, args[1]).getUnicodeValue();
 
             // Handle ~user paths.
             if (expandUser) {
                 cwd = expandUserPath(context, cwd);
             }
 
             String[] cwdURIParts = splitURI(cwd);
             if (uriParts == null && cwdURIParts != null) {
                 uriParts = cwdURIParts;
                 cwd = cwdURIParts[1];
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
         if (uriParts != null) {
             padSlashes = uriParts[0];
         } else if (!Platform.IS_WINDOWS) {
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
 
         return runtime.newString(padSlashes + canonicalize(path.getAbsolutePath()));
     }
 
     public static String[] splitURI(String path) {
         Matcher m = URI_PREFIX.matcher(path);
         if (m.find()) {
             if (m.group(2).length() == 0) {
                 return new String[] {path, ""};
             }
             String pathWithoutJarPrefix;
             if (m.group(1) != null) {
                 pathWithoutJarPrefix = path.substring(4);
             } else {
                 pathWithoutJarPrefix = path;
             }
             try {
                 URI u = new URI(pathWithoutJarPrefix);
                 String pathPart = u.getPath();
                 return new String[] {path.substring(0, path.indexOf(pathPart)), pathPart};
             } catch (Exception e) {
                 try {
                     URL u = new URL(pathWithoutJarPrefix);
                     String pathPart = u.getPath();
                     return new String[] {path.substring(0, path.indexOf(pathPart)), pathPart};
                 } catch (Exception e2) {
                 }
             }
         }
         return null;
     }
 
     /**
      * This method checks a path, and if it starts with ~, then it expands
      * the path to the absolute path of the user's home directory. If the
      * string does not begin with ~, then the string is simply returned.
      * unaltered.
      * @param context
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
                     checkHome(context);
                     path = RubyDir.getHomeDirectoryPath(context).toString();
                 } else {
                     // No directory delimeter.  Rest of string is username
                     userEnd = pathLength;
                 }
             }
 
             if (userEnd == 1) {
                 // '~/...' as path to expand
                 checkHome(context);
                 path = RubyDir.getHomeDirectoryPath(context).toString() +
                         path.substring(1);
             } else if (userEnd > 1){
                 // '~user/...' as path to expand
                 String user = path.substring(1, userEnd);
                 IRubyObject dir = RubyDir.getHomeDirectoryPath(context, user);
 
                 if (dir.isNil()) {
                     throw context.runtime.newArgumentError("user " + user + " does not exist");
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
 
         if (slashCount < SLASHES.length) {
             return SLASHES[slashCount];
         }
 
         // Prepare a string with the same number of redundant slashes so that
         // we easily can prepend it to the result.
         char[] slashes = new char[slashCount];
         for (int i = 0; i < slashCount; i++) {
             slashes[i] = '/';
         }
         return new String(slashes);
     }
 
     public static String canonicalize(String path) {
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
      * Check if HOME environment variable is not nil nor empty
      * @param context 
      */
     private static void checkHome(ThreadContext context) {
         Ruby runtime = context.runtime;
         RubyHash env = runtime.getENV();
         String home = (String) env.get(runtime.newString("HOME"));
         if (home == null || home.equals("")) {
             throw runtime.newArgumentError("couldn't find HOME environment -- expanding `~'");
         }
     }
 
     private static String inspectJoin(ThreadContext context, IRubyObject recv, RubyArray parent, RubyArray array) {
         Ruby runtime = context.runtime;
 
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
         Ruby runtime = context.runtime;
+        String separator = context.getRuntime().getClass("File").getConstant("SEPARATOR").toString();
 
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
-            if (i > 0 && !element.startsWith("/") && !element.startsWith("\\")) {
-                buffer.append("/");
+            if (i > 0 && !element.startsWith(separator)) {
+                buffer.append(separator);
             }
             buffer.append(element);
         }
 
         RubyString fixedStr = RubyString.newString(runtime, buffer.toString());
         fixedStr.setTaint(isTainted);
         return fixedStr;
     }
 
     private static void chomp(StringBuilder buffer) {
         int lastIndex = buffer.length() - 1;
 
         while (lastIndex >= 0 && (buffer.lastIndexOf("/") == lastIndex || buffer.lastIndexOf("\\") == lastIndex)) {
             buffer.setLength(lastIndex);
             lastIndex--;
         }
     }
 
     private static IRubyObject truncateCommon(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         RubyString filename = arg1.convertToString(); // TODO: SafeStringValue here
         Ruby runtime = context.runtime;
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
 
     private static void checkSharedExclusive(Ruby runtime, OpenFile openFile, int lockMode) {
         // This logic used to attempt a shared lock instead of an exclusive
         // lock, because LOCK_EX on some systems (as reported in JRUBY-1214)
         // allow exclusively locking a read-only file. However, the JDK
         // APIs do not allow acquiring an exclusive lock on files that are
         // not open for read, and there are other platforms (such as Solaris,
         // see JRUBY-5627) that refuse at an *OS* level to exclusively lock
         // files opened only for read. As a result, this behavior is platform-
         // dependent, and so we will obey the JDK's policy of disallowing
         // exclusive locks on files opened only for read.
         if (!openFile.isWritable() && (lockMode & LOCK_EX) > 0) {
             throw runtime.newErrnoEBADFError("cannot acquire exclusive lock on File not opened for write");
         }
 
         // Likewise, JDK does not allow acquiring a shared lock on files
         // that have not been opened for read. We comply here.
         if (!openFile.isReadable() && (lockMode & LOCK_SH) > 0) {
             throw runtime.newErrnoEBADFError("cannot acquire shared lock on File not opened for read");
         }
     }
 
     private static IRubyObject lockFailedReturn(Ruby runtime, int lockMode) {
         return (lockMode & LOCK_EX) == 0 ? RubyFixnum.zero(runtime) : runtime.getFalse();
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
 
     private IRubyObject unlock(Ruby runtime) throws IOException {
         if (currentLock != null) {
             currentLock.release();
             currentLock = null;
 
             return RubyFixnum.zero(runtime);
         }
         return runtime.getFalse();
     }
 
     private IRubyObject lock(Ruby runtime, FileChannel fileChannel, boolean exclusive) throws IOException {
         if (currentLock != null) currentLock.release();
 
         currentLock = fileChannel.lock(0L, Long.MAX_VALUE, !exclusive);
 
         if (currentLock != null) {
             return RubyFixnum.zero(runtime);
         }
 
         return lockFailedReturn(runtime, exclusive ? LOCK_EX : LOCK_SH);
     }
 
     private IRubyObject tryLock(Ruby runtime, FileChannel fileChannel, boolean exclusive) throws IOException {
         if (currentLock != null) currentLock.release();
 
         currentLock = fileChannel.tryLock(0L, Long.MAX_VALUE, !exclusive);
 
         if (currentLock != null) {
             return RubyFixnum.zero(runtime);
         }
 
         return lockFailedReturn(runtime, exclusive ? LOCK_EX : LOCK_SH);
     }
 
     private static final long serialVersionUID = 1L;
 
     public static final int LOCK_SH = 1;
     public static final int LOCK_EX = 2;
     public static final int LOCK_NB = 4;
     public static final int LOCK_UN = 8;
 
     private static final int FNM_NOESCAPE = 1;
     private static final int FNM_PATHNAME = 2;
     private static final int FNM_DOTMATCH = 4;
     private static final int FNM_CASEFOLD = 8;
     private static final int FNM_SYSCASE = Platform.IS_WINDOWS ? FNM_CASEFOLD : 0;
 
     private static int _cachedUmask = 0;
     private static final Object _umaskLock = new Object();
     private static final String[] SLASHES = {"", "/", "//"};
     private static Pattern URI_PREFIX = Pattern.compile("^(jar:)?[a-z]{2,}:(.*)");
 
     protected String path;
     private volatile FileLock currentLock;
 }
