diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 91e2870d8d..4d3c77d3fe 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -87,1871 +87,1844 @@ import static org.jruby.CompatVersion.*;
 /**
  * Ruby File class equivalent in java.
  **/
 @JRubyClass(name="File", parent="IO", include="FileTest")
 public class RubyFile extends RubyIO implements EncodingCapable {
     public static RubyClass createFileClass(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
 
         RubyClass fileClass = runtime.defineClass("File", runtime.getIO(), FILE_ALLOCATOR);
 
         runtime.setFile(fileClass);
 
         fileClass.defineAnnotatedMethods(RubyFile.class);
 
         fileClass.index = ClassIndex.FILE;
         fileClass.setReifiedClass(RubyFile.class);
 
         fileClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyFile;
             }
         };
 
         // file separator constants
         RubyString separator = runtime.newString("/");
         separator.freeze(context);
         fileClass.defineConstant("SEPARATOR", separator);
         fileClass.defineConstant("Separator", separator);
 
         if (File.separatorChar == '\\') {
             RubyString altSeparator = runtime.newString("\\");
             altSeparator.freeze(context);
             fileClass.defineConstant("ALT_SEPARATOR", altSeparator);
         } else {
             fileClass.defineConstant("ALT_SEPARATOR", runtime.getNil());
         }
 
         // path separator
         RubyString pathSeparator = runtime.newString(File.pathSeparator);
         pathSeparator.freeze(context);
         fileClass.defineConstant("PATH_SEPARATOR", pathSeparator);
 
         // For JRUBY-5276, physically define FileTest methods on File's singleton
         fileClass.getSingletonClass().defineAnnotatedMethods(RubyFileTest.FileTestFileMethods.class);
 
         // Create Constants class
         RubyModule constants = fileClass.defineModuleUnder("Constants");
 
         // open flags
         for (OpenFlags f : OpenFlags.values()) {
             // Strip off the O_ prefix, so they become File::RDONLY, and so on
             final String name = f.name();
             if (name.startsWith("O_")) {
                 final String cname = name.substring(2);
                 // Special case for handling ACCMODE, since constantine will generate
                 // an invalid value if it is not defined by the platform.
                 final RubyFixnum cvalue = f == OpenFlags.O_ACCMODE
                         ? runtime.newFixnum(ModeFlags.ACCMODE)
                         : runtime.newFixnum(f.intValue());
                 constants.setConstant(cname, cvalue);
             }
         }
 
         // case handling, escaping, path and dot matching
         constants.setConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         constants.setConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         constants.setConstant("FNM_SYSCASE", runtime.newFixnum(FNM_SYSCASE));
         constants.setConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
         constants.setConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
 
         // flock operations
         constants.setConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         constants.setConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         constants.setConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         constants.setConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
 
         // File::Constants module is included in IO.
         runtime.getIO().includeModule(constants);
 
         return fileClass;
     }
 
     private static ObjectAllocator FILE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyFile instance = new RubyFile(runtime, klass);
 
             instance.setMetaClass(klass);
 
             return instance;
         }
     };
 
     public RubyFile(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     // XXX This constructor is a hack to implement the __END__ syntax.
     //     Converting a reader back into an InputStream doesn't generally work.
     public RubyFile(Ruby runtime, String path, final Reader reader) {
         this(runtime, path, new InputStream() {
             public int read() throws IOException {
                 return reader.read();
             }
         });
     }
 
     public RubyFile(Ruby runtime, String path, InputStream in) {
         super(runtime, runtime.getFile());
         this.path = path;
         try {
             this.openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(in))));
             this.openFile.setMode(openFile.getMainStreamSafe().getModes().getOpenFileFlags());
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         }
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
         Ruby runtime = context.runtime;
         
         // TODO: port exact behavior from MRI, and move most locking logic into ChannelDescriptor
         // TODO: for all LOCK_NB cases, return false if they would block
         ChannelDescriptor descriptor;
         try {
             descriptor = openFile.getMainStreamSafe().getDescriptor();
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
 
         // null channel always succeeds for all locking operations
         if (descriptor.isNull()) return RubyFixnum.zero(runtime);
 
         if (descriptor.getChannel() instanceof FileChannel) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             int lockMode = RubyNumeric.num2int(lockingConstant);
 
             checkSharedExclusive(runtime, openFile, lockMode);
     
             if (!lockStateChanges(currentLock, lockMode)) return RubyFixnum.zero(runtime);
 
             try {
                 synchronized (fileChannel) {
                     // check again, to avoid unnecessary overhead
                     if (!lockStateChanges(currentLock, lockMode)) return RubyFixnum.zero(runtime);
                     
                     switch (lockMode) {
                         case LOCK_UN:
                         case LOCK_UN | LOCK_NB:
                             return unlock(runtime);
                         case LOCK_EX:
                             return lock(runtime, fileChannel, true);
                         case LOCK_EX | LOCK_NB:
                             return tryLock(runtime, fileChannel, true);
                         case LOCK_SH:
                             return lock(runtime, fileChannel, false);
                         case LOCK_SH | LOCK_NB:
                             return tryLock(runtime, fileChannel, false);
                     }
                 }
             } catch (IOException ioe) {
                 if (runtime.getDebug().isTrue()) {
                     ioe.printStackTrace(System.err);
                 }
             } catch (java.nio.channels.OverlappingFileLockException ioe) {
                 if (runtime.getDebug().isTrue()) {
                     ioe.printStackTrace(System.err);
                 }
             }
             return lockFailedReturn(runtime, lockMode);
         } else {
             // We're not actually a real file, so we can't flock
             return runtime.getFalse();
         }
     }
 
     @JRubyMethod(required = 1, optional = 2, visibility = PRIVATE, compat = RUBY1_8)
     @Override
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (openFile == null) {
             throw getRuntime().newRuntimeError("reinitializing File");
         }
         
         if (args.length > 0 && args.length < 3) {
             if (args[0] instanceof RubyInteger) {
                 return super.initialize(args, block);
             }
         }
 
         return openFile(args);
     }
 
     @JRubyMethod(name = "initialize", required = 1, optional = 2, visibility = PRIVATE, compat = RUBY1_9)
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
 
     @JRubyMethod(required = 1)
     public IRubyObject chmod(ThreadContext context, IRubyObject arg) {
         checkClosed(context);
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw context.getRuntime().newErrnoENOENTError(path);
         }
 
         return context.getRuntime().newFixnum(context.getRuntime().getPosix().chmod(path, mode));
     }
 
     @JRubyMethod(required = 2)
     public IRubyObject chown(ThreadContext context, IRubyObject arg1, IRubyObject arg2) {
         checkClosed(context);
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
         checkClosed(context);
         return context.getRuntime().newFileStat(path, false).atime();
     }
 
     @JRubyMethod
     public IRubyObject ctime(ThreadContext context) {
         checkClosed(context);
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
         checkClosed(context);
         return context.getRuntime().newFileStat(path, true);
     }
     
     @JRubyMethod
     public IRubyObject mtime(ThreadContext context) {
         checkClosed(context);
         return context.getRuntime().newFileStat(path, false).mtime();
     }
 
     @JRubyMethod(meta = true, compat = RUBY1_9)
     public static IRubyObject path(ThreadContext context, IRubyObject self, IRubyObject str) {
         return get_path(context, str);
     }
 
     @JRubyMethod(name = {"path", "to_path"})
     public IRubyObject path(ThreadContext context) {
         IRubyObject newPath = context.getRuntime().getNil();
         if (path != null) {
             newPath = context.getRuntime().newString(path);
             newPath.setTaint(true);
         }
         return newPath;
     }
 
     @JRubyMethod
     @Override
     public IRubyObject stat(ThreadContext context) {
         checkClosed(context);
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
             openFile.getMainStreamSafe().ftruncate(newLength.getLongValue());
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
         
         String jfilename = filename.asJavaString();
         
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
         path.force_encoding(context, context.getRuntime().getEncodingService().getDefaultExternal());
 
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
     @JRubyMethod(required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject absolute_path(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, false);
     }
 
     @JRubyMethod(name = {"realdirpath"}, required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject realdirpath(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, false);
     }
 
     @JRubyMethod(name = {"realpath"}, required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject realpath(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject file = expandPathInternal(context, recv, args, false);
         if (!RubyFileTest.exist_p(recv, file).isTrue()) {
             throw context.getRuntime().newErrnoENOENTError(file.toString());
         }
         return file;
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
     
     /*
      * Fixme:  This does not have exact same semantics as RubyArray.join, but they
      * probably could be consolidated (perhaps as join(args[], sep, doChomp)).
      */
     @JRubyMethod(rest = true, meta = true)
     public static RubyString join(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return join(context, recv, RubyArray.newArrayNoCopyLight(context.getRuntime(), args));
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
 
     @JRubyMethod(required = 1, rest = true, meta = true)
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
 
     @JRubyMethod(required = 2, meta = true)
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
         return context.getRuntime().newFileStat(get_path(context, filename).getUnicodeValue(), false).mtime();
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
     @JRubyMethod(required = 2, meta = true, compat = RUBY1_8)
     public static IRubyObject truncate(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {        
         return truncateCommon(context, recv, arg1, arg2);
     }
 
     @JRubyMethod(name = "truncate", required = 2, meta = true, compat = RUBY1_9)
     public static IRubyObject truncate19(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         if (!(arg1 instanceof RubyString) && arg1.respondsTo("to_path")) {
             arg1 = arg1.callMethod(context, "to_path");
         }
         return truncateCommon(context, recv, arg1, arg2);
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
 
             int result = runtime.getPosix().utimes(fileToTouch.getAbsolutePath(), atimeval, mtimeval);
             if (result == -1) {
                 throw runtime.newErrnoFromInt(runtime.getPosix().errno());
             }
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
 
     @JRubyMethod(name = "size", compat = RUBY1_9)
     public IRubyObject size(ThreadContext context) {
         Ruby runtime = context.getRuntime();
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
         Ruby runtime = context.getRuntime();
         RubyString filename = get_path(context, args[0]);
         runtime.checkSafeString(filename);
 
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
-
-                if (args[1] instanceof RubyFixnum) {
-                    perm = RubyNumeric.num2int(args[1]);
-                } else {
-                    modeString = args[1].convertToString().toString();
-                }
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
-            openInternal19(context, path, options, modeString, modes);
+            openInternal19(context, path, options, modes);
         }
 
         return this;
     }
 
     private IRubyObject openFile(IRubyObject args[]) {
         Ruby runtime = getRuntime();
         RubyString filename = get_path(runtime.getCurrentContext(), args[0]);
         runtime.checkSafeString(filename);
 
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
         if (modes.isBinary()) externalEncoding = ASCIIEncoding.INSTANCE;
 
         int umask = getUmaskSafe( getRuntime() );
         perm = perm - (perm & umask);
 
         ChannelDescriptor descriptor = sysopen(path, modes, perm);
         openFile.setMainStream(fdopen(descriptor, modes));
     }
 
-    protected void openInternal19(ThreadContext context, String path, RubyHash options, String modeString, IOOptions ioOptions) {
+    protected void openInternal19(ThreadContext context, String path, RubyHash options, IOOptions ioOptions) {
         ioOptions = updateIOOptionsFromOptions(context, options, ioOptions);
 
-        openInternal(path, modeString, ioOptions.getModeFlags());
+        openInternal(path, ioOptions.getModeFlags());
 
         setEncodingFromOptions(ioOptions.getEncodingOption());
     }
 
-    protected void openInternal(String path, String modeString, ModeFlags modes) {
+    protected void openInternal(String path, ModeFlags modes) {
         if (path.startsWith("jar:")) {
             path = path.substring(4);
         }
         openFile = new OpenFile();
 
         openFile.setMode(modes.getOpenFileFlags());
         if (modes.isBinary()) externalEncoding = ASCIIEncoding.INSTANCE;
         openFile.setPath(path);
-        openFile.setMainStream(fopen(path, modeString));
+        openFile.setMainStream(fopen(path, modes));
     }
 
     protected void openInternal(String path, String modeString) {
         if (path.startsWith("jar:")) {
             path = path.substring(4);
         }
         openFile = new OpenFile();
 
         IOOptions modes = newIOOptions(getRuntime(), modeString);
         openFile.setMode(modes.getModeFlags().getOpenFileFlags());
         if (modes.getModeFlags().isBinary()) externalEncoding = ASCIIEncoding.INSTANCE;
         openFile.setPath(path);
-        openFile.setMainStream(fopen(path, modeString));
+        openFile.setMainStream(fopen(path, modes.getModeFlags()));
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
 
-    private Stream fopen(String path, String modeString) {
+    private Stream fopen(String path, ModeFlags flags) {
         try {
-            Stream stream = ChannelStream.fopen(
+            return ChannelStream.fopen(
                     getRuntime(),
                     path,
-                    newModeFlags(getRuntime(), modeString));
-
-            if (stream == null) {
-                // TODO
-                //            if (errno == EMFILE || errno == ENFILE) {
-                //                rb_gc();
-                //                file = fopen(fname, mode);
-                //            }
-                //            if (!file) {
-                //                rb_sys_fail(fname);
-                //            }
-            }
-
-            // Do we need to be in SETVBUF mode for buffering to make sense? This comes up elsewhere.
-            //    #ifdef USE_SETVBUF
-            //        if (setvbuf(file, NULL, _IOFBF, 0) != 0)
-            //            rb_warn("setvbuf() can't be honoured for %s", fname);
-            //    #endif
-            //    #ifdef __human68k__
-            //        fmode(file, _IOTEXT);
-            //    #endif
-            return stream;
+                    flags);
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
         ZipFile zf = Ruby.getGlobalRuntime().getCurrentContext().getRuntime().getLoadService().getJarFile(jar);
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
         openFile.checkClosed(context.getRuntime());
     }
 
     private static boolean isWindowsDriveLetter(char c) {
         return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
     }
 
     private static IRubyObject expandPathInternal(ThreadContext context, IRubyObject recv, IRubyObject[] args, boolean expandUser) {
         Ruby runtime = context.getRuntime();
 
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
         Ruby runtime = context.getRuntime();
         RubyHash env = runtime.getENV();
         String home = (String) env.get(runtime.newString("HOME"));
         if (home == null || home.equals("")) {
             throw runtime.newArgumentError("couldn't find HOME environment -- expanding `~'");
         }
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
 
     private static void chomp(StringBuilder buffer) {
         int lastIndex = buffer.length() - 1;
 
         while (lastIndex >= 0 && (buffer.lastIndexOf("/") == lastIndex || buffer.lastIndexOf("\\") == lastIndex)) {
             buffer.setLength(lastIndex);
             lastIndex--;
         }
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
diff --git a/src/org/jruby/util/io/ChannelDescriptor.java b/src/org/jruby/util/io/ChannelDescriptor.java
index 858a73351a..062f510147 100644
--- a/src/org/jruby/util/io/ChannelDescriptor.java
+++ b/src/org/jruby/util/io/ChannelDescriptor.java
@@ -1,993 +1,993 @@
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
  * Copyright (C) 2008 Charles O Nutter <headius@headius.com>
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
 
 import static org.jruby.util.io.ModeFlags.RDONLY;
 import static org.jruby.util.io.ModeFlags.RDWR;
 import static org.jruby.util.io.ModeFlags.WRONLY;
 
 import java.io.File;
 import java.io.FileDescriptor;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.RandomAccessFile;
 import java.net.URL;
 import java.nio.ByteBuffer;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.WritableByteChannel;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 import org.jruby.RubyFile;
 
 import jnr.posix.POSIX;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 
 /**
  * ChannelDescriptor provides an abstraction similar to the concept of a
  * "file descriptor" on any POSIX system. In our case, it's a numbered object
  * (fileno) enclosing a Channel (@see java.nio.channels.Channel), FileDescriptor
  * (@see java.io.FileDescriptor), and flags under which the original open occured
  * (@see org.jruby.util.io.ModeFlags). Several operations you would normally
  * expect to use with a POSIX file descriptor are implemented here and used by
  * higher-level classes to implement higher-level IO behavior.
  * 
  * Note that the channel specified when constructing a ChannelDescriptor will
  * be reference-counted; that is, until all known references to it through this
  * class have gone away, it will be left open. This is to support operations
  * like "dup" which must produce two independent ChannelDescriptor instances
  * that can be closed separately without affecting the other.
  * 
  * At present there's no way to simulate the behavior on some platforms where
  * POSIX dup also allows independent positioning information.
  */
 public class ChannelDescriptor {
 
     private static final Logger LOG = LoggerFactory.getLogger("ChannelDescriptor");
 
     /** Whether to log debugging information */
     private static final boolean DEBUG = false;
     
     /** The java.nio.channels.Channel this descriptor wraps. */
     private Channel channel;
     /**
      * The file number (equivalent to the int file descriptor value in POSIX)
      * for this descriptor. This is generated new for most ChannelDescriptor
      * instances, except when they need to masquerade as another fileno.
      */
     private int internalFileno;
     /** The java.io.FileDescriptor object for this descriptor. */
     private FileDescriptor fileDescriptor;
     /**
      * The original org.jruby.util.io.ModeFlags with which the specified
      * channel was opened.
      */
     private ModeFlags originalModes;
     /** 
      * The reference count for the provided channel.
      * Only counts references through ChannelDescriptor instances.
      */
     private AtomicInteger refCounter;
 
     /**
      * Used to work-around blocking problems with STDIN. In most cases <code>null</code>.
      * See {@link ChannelDescriptor#ChannelDescriptor(java.io.InputStream, ModeFlags, java.io.FileDescriptor)}
      * for more details. You probably should not use it.
      */
     private InputStream baseInputStream;
     
     /**
      * Process streams get Channel.newChannel()ed into FileChannel but are not actually
      * seekable. So instead of just the isSeekable check doing instanceof FileChannel,
      * we must also add this boolean to check, which we set to false when it's known
      * that the incoming channel is from a process.
      * 
      * FIXME: This is gross, and it's NIO's fault for not providing a nice way to
      * tell if a channel is "really" seekable.
      */
     private boolean canBeSeekable = true;
     
     /**
      * If the incoming channel is already in append mode (i.e. it will do the
      * requisite seeking), we don't want to do our own additional seeks.
      */
     private boolean isInAppendMode = false;
     
     /**
      * Whether the current channe is writable or not.
      */
     private boolean readableChannel;
     
     /**
      * Whether the current channel is readable or not.
      */
     private boolean writableChannel;
     
     /**
      * Whether the current channel is seekable or not.
      */
     private boolean seekableChannel;
     
     /**
      * Construct a new ChannelDescriptor with the specified channel, file number,
      * mode flags, file descriptor object and reference counter. This constructor
      * is only used when constructing a new copy of an existing ChannelDescriptor
      * with an existing reference count, to allow the two instances to safely
      * share and appropriately close  a given channel.
      * 
      * @param channel The channel for the new descriptor, which will be shared with another
      * @param fileno The new file number for the new descriptor
      * @param originalModes The mode flags to use as the "origina" set for this descriptor
      * @param fileDescriptor The java.io.FileDescriptor object to associate with this ChannelDescriptor
      * @param refCounter The reference counter from another ChannelDescriptor being duped.
      * @param canBeSeekable If the underlying channel can be considered seekable.
      * @param isInAppendMode If the underlying channel is already in append mode.
      */
     private ChannelDescriptor(Channel channel, int fileno, ModeFlags originalModes, FileDescriptor fileDescriptor, AtomicInteger refCounter, boolean canBeSeekable, boolean isInAppendMode) {
         this.refCounter = refCounter;
         this.channel = channel;
         this.internalFileno = fileno;
         this.originalModes = originalModes;
         this.fileDescriptor = fileDescriptor;
         this.canBeSeekable = canBeSeekable;
         this.isInAppendMode = isInAppendMode;
         
         this.readableChannel = channel instanceof ReadableByteChannel;
         this.writableChannel = channel instanceof WritableByteChannel;
         this.seekableChannel = channel instanceof FileChannel;
 
         registerDescriptor(this);
     }
 
     private ChannelDescriptor(Channel channel, int fileno, ModeFlags originalModes, FileDescriptor fileDescriptor) {
         this(channel, fileno, originalModes, fileDescriptor, new AtomicInteger(1), true, false);
     }
 
     /**
      * Construct a new ChannelDescriptor with the given channel, file number, mode flags,
      * and file descriptor object. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed.
      * 
      * @param channel The channel for the new descriptor
      * @param originalModes The mode flags for the new descriptor
      * @param fileDescriptor The java.io.FileDescriptor object for the new descriptor
      */
     public ChannelDescriptor(Channel channel, ModeFlags originalModes, FileDescriptor fileDescriptor) {
         this(channel, getNewFileno(), originalModes, fileDescriptor, new AtomicInteger(1), true, false);
     }
 
     /**
      * Construct a new ChannelDescriptor with the given channel, file number, mode flags,
      * and file descriptor object. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed.
      * 
      * @param channel The channel for the new descriptor
      * @param originalModes The mode flags for the new descriptor
      * @param fileDescriptor The java.io.FileDescriptor object for the new descriptor
      */
     public ChannelDescriptor(Channel channel, ModeFlags originalModes, FileDescriptor fileDescriptor, boolean isInAppendMode) {
         this(channel, getNewFileno(), originalModes, fileDescriptor, new AtomicInteger(1), true, isInAppendMode);
     }
 
     /**
      * Construct a new ChannelDescriptor with the given channel, file number, mode flags,
      * and file descriptor object. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed.
      *
      * @param channel The channel for the new descriptor
      * @param originalModes The mode flags for the new descriptor
      */
     public ChannelDescriptor(Channel channel, ModeFlags originalModes) {
         this(channel, getNewFileno(), originalModes, new FileDescriptor(), new AtomicInteger(1), true, false);
     }
 
     /**
      * Special constructor to create the ChannelDescriptor out of the stream, file number,
      * mode flags, and file descriptor object. The channel will be created from the
      * provided stream. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed. <b>Note:</b> in most cases, you should not
      * use this constructor, it's reserved mostly for STDIN.
      *
      * @param baseInputStream The stream to create the channel for the new descriptor
      * @param originalModes The mode flags for the new descriptor
      * @param fileDescriptor The java.io.FileDescriptor object for the new descriptor
      */
     public ChannelDescriptor(InputStream baseInputStream, ModeFlags originalModes, FileDescriptor fileDescriptor) {
         // The reason why we need the stream is to be able to invoke available() on it.
         // STDIN in Java is non-interruptible, non-selectable, and attempt to read
         // on such stream might lead to thread being blocked without *any* way to unblock it.
         // That's where available() comes it, so at least we could check whether
         // anything is available to be read without blocking.
         this(Channels.newChannel(baseInputStream), getNewFileno(), originalModes, fileDescriptor, new AtomicInteger(1), true, false);
         this.baseInputStream = baseInputStream;
     }
 
     /**
      * Special constructor to create the ChannelDescriptor out of the stream, file number,
      * mode flags, and file descriptor object. The channel will be created from the
      * provided stream. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed. <b>Note:</b> in most cases, you should not
      * use this constructor, it's reserved mostly for STDIN.
      *
      * @param baseInputStream The stream to create the channel for the new descriptor
      * @param originalModes The mode flags for the new descriptor
      */
     public ChannelDescriptor(InputStream baseInputStream, ModeFlags originalModes) {
         // The reason why we need the stream is to be able to invoke available() on it.
         // STDIN in Java is non-interruptible, non-selectable, and attempt to read
         // on such stream might lead to thread being blocked without *any* way to unblock it.
         // That's where available() comes it, so at least we could check whether
         // anything is available to be read without blocking.
         this(Channels.newChannel(baseInputStream), getNewFileno(), originalModes, new FileDescriptor(), new AtomicInteger(1), true, false);
         this.baseInputStream = baseInputStream;
     }
 
     /**
      * Construct a new ChannelDescriptor with the given channel, file number,
      * and file descriptor object. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed. The channel's capabilities will be used
      * to determine the "original" set of mode flags.
      * 
      * @param channel The channel for the new descriptor
      * @param fileDescriptor The java.io.FileDescriptor object for the new descriptor
      */
     public ChannelDescriptor(Channel channel, FileDescriptor fileDescriptor) throws InvalidValueException {
         this(channel, getModesFromChannel(channel), fileDescriptor);
     }
     
     @Deprecated
     public ChannelDescriptor(Channel channel, int fileno, FileDescriptor fileDescriptor) throws InvalidValueException {
         this(channel, getModesFromChannel(channel), fileDescriptor);
     }
 
     /**
      * Construct a new ChannelDescriptor with the given channel, file number,
      * and file descriptor object. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed. The channel's capabilities will be used
      * to determine the "original" set of mode flags. This version generates a
      * new fileno.
      *
      * @param channel The channel for the new descriptor
      */
     public ChannelDescriptor(Channel channel) throws InvalidValueException {
         this(channel, getModesFromChannel(channel), new FileDescriptor());
     }
 
     /**
      * Get this descriptor's file number.
      * 
      * @return the fileno for this descriptor
      */
     public int getFileno() {
         return internalFileno;
     }
     
     /**
      * Get the FileDescriptor object associated with this descriptor. This is
      * not guaranteed to be a "valid" descriptor in the terms of the Java
      * implementation, but is provided for completeness and for cases where it
      * is possible to get a valid FileDescriptor for a given channel.
      * 
      * @return the java.io.FileDescriptor object associated with this descriptor
      */
     public FileDescriptor getFileDescriptor() {
         return fileDescriptor;
     }
 
     /**
      * The channel associated with this descriptor. The channel will be reference
      * counted through ChannelDescriptor and kept open until all ChannelDescriptor
      * objects have been closed. References that leave ChannelDescriptor through
      * this method will not be counted.
      * 
      * @return the java.nio.channels.Channel associated with this descriptor
      */
     public Channel getChannel() {
         return channel;
     }
 
     /**
      * This is intentionally non-public, since it should not be really
      * used outside of very limited use case (handling of STDIN).
      * See {@link ChannelDescriptor#ChannelDescriptor(java.io.InputStream, ModeFlags, java.io.FileDescriptor)}
      * for more info.
      */
     /*package-protected*/ InputStream getBaseInputStream() {
         return baseInputStream;
     }
 
     /**
      * Whether the channel associated with this descriptor is seekable (i.e.
      * whether it is instanceof FileChannel).
      * 
      * @return true if the associated channel is seekable, false otherwise
      */
     public boolean isSeekable() {
         return canBeSeekable && seekableChannel;
     }
     
     /**
      * Set the channel to be explicitly seekable or not, for streams that appear
      * to be seekable with the instanceof FileChannel check.
      * 
      * @param canBeSeekable Whether the channel is seekable or not.
      */
     public void setCanBeSeekable(boolean canBeSeekable) {
         this.canBeSeekable = canBeSeekable;
     }
     
     /**
      * Whether the channel associated with this descriptor is a NullChannel,
      * for which many operations are simply noops.
      */
     public boolean isNull() {
         return channel instanceof NullChannel;
     }
 
     /**
      * Whether the channel associated with this descriptor is writable (i.e.
      * whether it is instanceof WritableByteChannel).
      * 
      * @return true if the associated channel is writable, false otherwise
      */
     public boolean isWritable() {
         return writableChannel;
     }
 
     /**
      * Whether the channel associated with this descriptor is readable (i.e.
      * whether it is instanceof ReadableByteChannel).
      * 
      * @return true if the associated channel is readable, false otherwise
      */
     public boolean isReadable() {
         return readableChannel;
     }
     
     /**
      * Whether the channel associated with this descriptor is open.
      * 
      * @return true if the associated channel is open, false otherwise
      */
     public boolean isOpen() {
         return channel.isOpen();
     }
     
     /**
      * Check whether the isOpen returns true, raising a BadDescriptorException if
      * it returns false.
      * 
      * @throws org.jruby.util.io.BadDescriptorException if isOpen returns false
      */
     public void checkOpen() throws BadDescriptorException {
         if (!isOpen()) {
             throw new BadDescriptorException();
         }
     }
     
     /**
      * Get the original mode flags for the descriptor.
      * 
      * @return the original mode flags for the descriptor
      */
     public ModeFlags getOriginalModes() {
         return originalModes;
     }
     
     /**
      * Check whether a specified set of mode flags is a superset of this
      * descriptor's original set of mode flags.
      * 
      * @param newModes The modes to confirm as superset
      * @throws org.jruby.util.io.InvalidValueException if the modes are not a superset
      */
     public void checkNewModes(ModeFlags newModes) throws InvalidValueException {
         if (!newModes.isSubsetOf(originalModes)) {
             throw new InvalidValueException();
         }
     }
     
     /**
      * Mimics the POSIX dup(2) function, returning a new descriptor that references
      * the same open channel.
      * 
      * @return A duplicate ChannelDescriptor based on this one
      */
     public ChannelDescriptor dup() {
         synchronized (refCounter) {
             refCounter.incrementAndGet();
 
             int newFileno = getNewFileno();
             
             if (DEBUG) LOG.info("Reopen fileno {}, refs now: {}", newFileno, refCounter.get());
 
             return new ChannelDescriptor(channel, newFileno, originalModes, fileDescriptor, refCounter, canBeSeekable, isInAppendMode);
         }
     }
     
     /**
      * Mimics the POSIX dup2(2) function, returning a new descriptor that references
      * the same open channel but with a specified fileno.
      * 
      * @param fileno The fileno to use for the new descriptor
      * @return A duplicate ChannelDescriptor based on this one
      */
     public ChannelDescriptor dup2(int fileno) {
         synchronized (refCounter) {
             refCounter.incrementAndGet();
 
             if (DEBUG) LOG.info("Reopen fileno {}, refs now: {}", fileno, refCounter.get());
 
             return new ChannelDescriptor(channel, fileno, originalModes, fileDescriptor, refCounter, canBeSeekable, isInAppendMode);
         }
     }
     
     /**
      * Mimics the POSIX dup2(2) function, returning a new descriptor that references
      * the same open channel but with a specified fileno. This differs from the fileno
      * version by making the target descriptor into a new reference to the current
      * descriptor's channel, closing what it originally pointed to and preserving
      * its original fileno.
      *
      * @param other the descriptor to dup this one into
      */
     public void dup2Into(ChannelDescriptor other) throws BadDescriptorException, IOException {
         synchronized (refCounter) {
             refCounter.incrementAndGet();
 
             if (DEBUG) LOG.info("Reopen fileno {}, refs now: {}", internalFileno, refCounter.get());
 
             other.close();
             
             other.channel = channel;
             other.originalModes = originalModes;
             other.fileDescriptor = fileDescriptor;
             other.refCounter = refCounter;
             other.canBeSeekable = canBeSeekable;
         }
     }
 
     public ChannelDescriptor reopen(Channel channel, ModeFlags modes) {
         return new ChannelDescriptor(channel, internalFileno, modes, fileDescriptor);
     }
 
     public ChannelDescriptor reopen(RandomAccessFile file, ModeFlags modes) throws IOException {
         return new ChannelDescriptor(file.getChannel(), internalFileno, modes, file.getFD());
     }
     
     /**
      * Perform a low-level seek operation on the associated channel if it is
      * instanceof FileChannel, or raise PipeException if it is not a FileChannel.
      * Calls checkOpen to confirm the target channel is open. This is equivalent
      * to the lseek(2) POSIX function, and like that function it bypasses any
      * buffer flushing or invalidation as in ChannelStream.fseek.
      * 
      * @param offset the offset value to use
      * @param whence whence to seek
      * @throws java.io.IOException If there is an exception while seeking
      * @throws org.jruby.util.io.InvalidValueException If the value specified for
      * offset or whence is invalid
      * @throws org.jruby.util.io.PipeException If the target channel is not seekable
      * @throws org.jruby.util.io.BadDescriptorException If the target channel is
      * already closed.
      * @return the new offset into the FileChannel.
      */
     public long lseek(long offset, int whence) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         if (seekableChannel) {
             checkOpen();
             
             FileChannel fileChannel = (FileChannel)channel;
             try {
                 long pos;
                 switch (whence) {
                 case Stream.SEEK_SET:
                     pos = offset;
                     fileChannel.position(pos);
                     break;
                 case Stream.SEEK_CUR:
                     pos = fileChannel.position() + offset;
                     fileChannel.position(pos);
                     break;
                 case Stream.SEEK_END:
                     pos = fileChannel.size() + offset;
                     fileChannel.position(pos);
                     break;
                 default:
                     throw new InvalidValueException();
                 }
                 return pos;
             } catch (IllegalArgumentException e) {
                 throw new InvalidValueException();
             } catch (IOException ioe) {
                 // "invalid seek" means it's an ESPIPE, so we rethrow as a PipeException()
                 if (ioe.getMessage().equals("Illegal seek")) {
                     throw new PipeException();
                 }
                 throw ioe;
             }
         } else {
             throw new PipeException();
         }
     }
 
     /**
      * Perform a low-level read of the specified number of bytes into the specified
      * byte list. The incoming bytes will be appended to the byte list. This is
      * equivalent to the read(2) POSIX function, and like that function it
      * ignores read and write buffers defined elsewhere.
      * 
      * @param number the number of bytes to read
      * @param byteList the byte list on which to append the incoming bytes
      * @return the number of bytes actually read
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed.
      * @see org.jruby.util.ByteList
      */
     public int read(int number, ByteList byteList) throws IOException, BadDescriptorException {
         checkOpen();
         
         byteList.ensure(byteList.length() + number);
         int bytesRead = read(ByteBuffer.wrap(byteList.getUnsafeBytes(),
                 byteList.begin() + byteList.length(), number));
         if (bytesRead > 0) {
             byteList.length(byteList.length() + bytesRead);
         }
         return bytesRead;
     }
     
     /**
      * Perform a low-level read of the remaining number of bytes into the specified
      * byte buffer. The incoming bytes will be used to fill the remaining space in
      * the target byte buffer. This is equivalent to the read(2) POSIX function,
      * and like that function it ignores read and write buffers defined elsewhere.
      * 
      * @param buffer the java.nio.ByteBuffer in which to put the incoming bytes
      * @return the number of bytes actually read
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      * @see java.nio.ByteBuffer
      */
     public int read(ByteBuffer buffer) throws IOException, BadDescriptorException {
         checkOpen();
 
         // TODO: It would be nice to throw a better error for this
         if (!isReadable()) {
             throw new BadDescriptorException();
         }
         ReadableByteChannel readChannel = (ReadableByteChannel) channel;
         int bytesRead = 0;
         bytesRead = readChannel.read(buffer);
 
         return bytesRead;
     }
     
     /**
      * Write the bytes in the specified byte list to the associated channel.
      * 
      * @param buffer the byte list containing the bytes to be written
      * @return the number of bytes actually written
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      */
     public int internalWrite(ByteBuffer buffer) throws IOException, BadDescriptorException {
         checkOpen();
 
         // TODO: It would be nice to throw a better error for this
         if (!isWritable()) {
             throw new BadDescriptorException();
         }
         
         WritableByteChannel writeChannel = (WritableByteChannel)channel;
         
         // if appendable, we always seek to the end before writing
         if (isSeekable() && originalModes.isAppendable()) {
             // if already in append mode, we don't do our own seeking
             if (!isInAppendMode) {
                 FileChannel fileChannel = (FileChannel)channel;
                 fileChannel.position(fileChannel.size());
             }
         }
         
         return writeChannel.write(buffer);
     }
     
     /**
      * Write the bytes in the specified byte list to the associated channel.
      * 
      * @param buffer the byte list containing the bytes to be written
      * @return the number of bytes actually written
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      */
     public int write(ByteBuffer buffer) throws IOException, BadDescriptorException {
         checkOpen();
         
         return internalWrite(buffer);
     }
     
     /**
      * Write the bytes in the specified byte list to the associated channel.
      * 
      * @param buf the byte list containing the bytes to be written
      * @return the number of bytes actually written
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      */
     public int write(ByteList buf) throws IOException, BadDescriptorException {
         checkOpen();
         
         return internalWrite(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin(), buf.length()));
     }
     
     private final ByteBuffer directBuffer = ByteBuffer.allocateDirect(8192);
 
     /**
      * Write the bytes in the specified byte list to the associated channel.
      * 
      * @param buf the byte list containing the bytes to be written
      * @param offset the offset to start at. this is relative to the begin variable in the but
      * @param len the amount of bytes to write. this should not be longer than the buffer
      * @return the number of bytes actually written
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      */
     public int write(ByteList buf, int offset, int len) throws IOException, BadDescriptorException {
         checkOpen();
         
         return internalWrite(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin()+offset, len));
     }
     
     /**
      * Write the byte represented by the specified int to the associated channel.
      * 
      * @param c The byte to write
      * @return 1 if the byte was written, 0 if not and -1 if there was an error
      * (@see java.nio.channels.WritableByteChannel.write(java.nio.ByteBuffer))
      * @throws java.io.IOException If there was an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      */
     public int write(int c) throws IOException, BadDescriptorException {
         checkOpen();
         
         ByteBuffer buf = ByteBuffer.allocate(1);
         buf.put((byte)c);
         buf.flip();
         
         return internalWrite(buf);
     }
 
     /**
      * Open a new descriptor using the given working directory, file path,
      * mode flags, and file permission. This is equivalent to the open(2)
      * POSIX function. See org.jruby.util.io.ChannelDescriptor.open(String, String, ModeFlags, int, POSIX)
      * for the version that also sets file permissions.
      *
      * @param cwd the "current working directory" to use when opening the file
      * @param path the file path to open
      * @param flags the mode flags to use for opening the file
      * @return a new ChannelDescriptor based on the specified parameters
      * @throws java.io.FileNotFoundException if the target file could not be found
      * and the create flag was not specified
      * @throws org.jruby.util.io.DirectoryAsFileException if the target file is
      * a directory being opened as a file
      * @throws org.jruby.util.io.FileExistsException if the target file should
      * be created anew, but already exists
      * @throws java.io.IOException if there is an exception during IO
      */
     public static ChannelDescriptor open(String cwd, String path, ModeFlags flags) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException {
         return open(cwd, path, flags, 0, null, null);
     }
     
     /**
      * Open a new descriptor using the given working directory, file path,
      * mode flags, and file permission. This is equivalent to the open(2)
      * POSIX function. See org.jruby.util.io.ChannelDescriptor.open(String, String, ModeFlags, int, POSIX)
      * for the version that also sets file permissions.
      * 
      * @param cwd the "current working directory" to use when opening the file
      * @param path the file path to open
      * @param flags the mode flags to use for opening the file
      * @param classLoader a ClassLoader to use for classpath: resources
      * @return a new ChannelDescriptor based on the specified parameters
      * @throws java.io.FileNotFoundException if the target file could not be found
      * and the create flag was not specified
      * @throws org.jruby.util.io.DirectoryAsFileException if the target file is
      * a directory being opened as a file
      * @throws org.jruby.util.io.FileExistsException if the target file should
      * be created anew, but already exists
      * @throws java.io.IOException if there is an exception during IO
      */
     public static ChannelDescriptor open(String cwd, String path, ModeFlags flags, ClassLoader classLoader) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException {
         return open(cwd, path, flags, 0, null, classLoader);
     }
 
     /**
      * Open a new descriptor using the given working directory, file path,
      * mode flags, and file permission. This is equivalent to the open(2)
      * POSIX function.
      *
      * @param cwd the "current working directory" to use when opening the file
      * @param path the file path to open
      * @param flags the mode flags to use for opening the file
      * @param perm the file permissions to use when creating a new file (currently
      * unobserved)
      * @param posix a POSIX api implementation, used for setting permissions; if null, permissions are ignored
      * @return a new ChannelDescriptor based on the specified parameters
      * @throws java.io.FileNotFoundException if the target file could not be found
      * and the create flag was not specified
      * @throws org.jruby.util.io.DirectoryAsFileException if the target file is
      * a directory being opened as a file
      * @throws org.jruby.util.io.FileExistsException if the target file should
      * be created anew, but already exists
      * @throws java.io.IOException if there is an exception during IO
      */
     public static ChannelDescriptor open(String cwd, String path, ModeFlags flags, int perm, POSIX posix) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException {
         return open(cwd, path, flags, perm, posix, null);
     }
     
     /**
      * Open a new descriptor using the given working directory, file path,
      * mode flags, and file permission. This is equivalent to the open(2)
      * POSIX function.
      * 
      * @param cwd the "current working directory" to use when opening the file
      * @param path the file path to open
      * @param flags the mode flags to use for opening the file
      * @param perm the file permissions to use when creating a new file (currently
      * unobserved)
      * @param posix a POSIX api implementation, used for setting permissions; if null, permissions are ignored
      * @param classLoader a ClassLoader to use for classpath: resources
      * @return a new ChannelDescriptor based on the specified parameters
      * @throws java.io.FileNotFoundException if the target file could not be found
      * and the create flag was not specified
      * @throws org.jruby.util.io.DirectoryAsFileException if the target file is
      * a directory being opened as a file
      * @throws org.jruby.util.io.FileExistsException if the target file should
      * be created anew, but already exists
      * @throws java.io.IOException if there is an exception during IO
      */
     public static ChannelDescriptor open(String cwd, String path, ModeFlags flags, int perm, POSIX posix, ClassLoader classLoader) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException {
         boolean fileCreated = false;
         if (path.equals("/dev/null") || path.equalsIgnoreCase("nul:") || path.equalsIgnoreCase("nul")) {
             Channel nullChannel = new NullChannel();
             // FIXME: don't use RubyIO for this
             return new ChannelDescriptor(nullChannel, flags);
         } else if (path.startsWith("file:")) {
             int bangIndex = path.indexOf("!");
             if (bangIndex > 0) {
                 String filePath = path.substring(5, bangIndex);
                 String internalPath = path.substring(bangIndex + 2);
 
                 if (!new File(filePath).exists()) {
                     throw new FileNotFoundException(path);
                 }
 
                 JarFile jf = new JarFile(filePath);
                 ZipEntry entry = RubyFile.getFileEntry(jf, internalPath);
 
                 if (entry == null) {
                     throw new FileNotFoundException(path);
                 }
 
                 InputStream is = jf.getInputStream(entry);
                 // FIXME: don't use RubyIO for this
                 return new ChannelDescriptor(Channels.newChannel(is), flags);
             } else {
                 // raw file URL, just open directly
                 URL url = new URL(path);
                 InputStream is = url.openStream();
                 // FIXME: don't use RubyIO for this
                 return new ChannelDescriptor(Channels.newChannel(is), flags);
             }
         } else if (path.startsWith("classpath:/") && classLoader != null) {
             path = path.substring("classpath:/".length());
             InputStream is = classLoader.getResourceAsStream(path);
             // FIXME: don't use RubyIO for this
             return new ChannelDescriptor(Channels.newChannel(is), flags);
         } else {
             JRubyFile theFile = JRubyFile.create(cwd,path);
 
             if (theFile.isDirectory() && flags.isWritable()) {
                 throw new DirectoryAsFileException();
             }
 
             if (flags.isCreate()) {
                 if (theFile.exists() && flags.isExclusive()) {
                     throw new FileExistsException(path);
                 }
                 try {
                     fileCreated = theFile.createNewFile();
                 } catch (IOException ioe) {
                     // See JRUBY-4380.
                     // MRI behavior: raise Errno::ENOENT in case
                     // when the directory for the file doesn't exist.
                     // Java in such cases just throws IOException.
                     File parent = theFile.getParentFile();
                     if (parent != null && parent != theFile && !parent.exists()) {
                         throw new FileNotFoundException(path);
                     } else if (!theFile.canWrite()) {
                         throw new PermissionDeniedException(path);
                     } else {
                         // for all other IO errors, just re-throw the original exception
                         throw ioe;
                     }
                 }
             } else {
                 if (!theFile.exists()) {
                     throw new FileNotFoundException(path);
                 }
             }
 
             FileDescriptor fileDescriptor;
             FileChannel fileChannel;
             boolean isInAppendMode;
             if (flags.isWritable() && !flags.isReadable()) {
                 FileOutputStream fos = new FileOutputStream(theFile, flags.isAppendable());
                 fileChannel = fos.getChannel();
                 fileDescriptor = fos.getFD();
                 isInAppendMode = true;
             } else {
                 RandomAccessFile raf = new RandomAccessFile(theFile, flags.toJavaModeString());
                 fileChannel = raf.getChannel();
                 fileDescriptor = raf.getFD();
                 isInAppendMode = false;
             }
 
             // call chmod after we created the RandomAccesFile
             // because otherwise, the file could be read-only
             if (fileCreated) {
                 // attempt to set the permissions, if we have been passed a POSIX instance,
-                // and only if the file was created in this call.
-                if (posix != null && perm != -1) {
+                // perm is > 0, and only if the file was created in this call.
+                if (posix != null && perm > 0) {
                     posix.chmod(theFile.getPath(), perm);
                 }
             }
 
             try {
                 if (flags.isTruncate()) fileChannel.truncate(0);
             } catch (IOException ioe) {
                 if (ioe.getMessage().equals("Illegal seek")) {
                     // ignore; it's a pipe or fifo that can't be truncated
                 } else {
                     throw ioe;
                 }
             }
 
             // TODO: append should set the FD to end, no? But there is no seek(int) in libc!
             //if (modes.isAppendable()) seek(0, Stream.SEEK_END);
 
             return new ChannelDescriptor(fileChannel, flags, fileDescriptor, isInAppendMode);
         }
     }
     
     /**
      * Close this descriptor. If in closing the last ChannelDescriptor reference
      * to the associate channel is closed, the channel itself will be closed.
      * 
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      * @throws java.io.IOException if there is an exception during IO
      */
     public void close() throws BadDescriptorException, IOException {
         // tidy up
         finish(true);
 
     }
 
     void finish(boolean close) throws BadDescriptorException, IOException {
         synchronized (refCounter) {
             // if refcount is at or below zero, we're no longer valid
             if (refCounter.get() <= 0) {
                 throw new BadDescriptorException();
             }
 
             // if channel is already closed, we're no longer valid
             if (!channel.isOpen()) {
                 throw new BadDescriptorException();
             }
 
             // otherwise decrement and possibly close as normal
             int count = refCounter.decrementAndGet();
 
             if (DEBUG) LOG.info("Descriptor for fileno {} refs: {}", internalFileno, count);
 
             if (count <= 0) {
                 // if we're the last referrer, close the channel
                 try {
                     if (close) channel.close();
                 } finally {
                     unregisterDescriptor(internalFileno);
                 }
             }
         }
     }
     
     /**
      * Build a set of mode flags using the specified channel's actual capabilities.
      * 
      * @param channel the channel to examine for capabilities
      * @return the mode flags 
      * @throws org.jruby.util.io.InvalidValueException
      */
     private static ModeFlags getModesFromChannel(Channel channel) throws InvalidValueException {
         ModeFlags modes;
         if (channel instanceof ReadableByteChannel) {
             if (channel instanceof WritableByteChannel) {
                 modes = new ModeFlags(RDWR);
             } else {
                 modes = new ModeFlags(RDONLY);
             }
         } else if (channel instanceof WritableByteChannel) {
             modes = new ModeFlags(WRONLY);
         } else {
             // FIXME: I don't like this
             modes = new ModeFlags(RDWR);
         }
         
         return modes;
     }
 
     // FIXME shouldn't use static; would interfere with other runtimes in the same JVM
     protected static final AtomicInteger internalFilenoIndex = new AtomicInteger(2);
 
     public static int getNewFileno() {
         return internalFilenoIndex.incrementAndGet();
     }
 
     private static void registerDescriptor(ChannelDescriptor descriptor) {
         filenoDescriptorMap.put(descriptor.getFileno(), descriptor);
     }
 
     private static void unregisterDescriptor(int aFileno) {
         filenoDescriptorMap.remove(aFileno);
     }
 
     public static ChannelDescriptor getDescriptorByFileno(int aFileno) {
         return filenoDescriptorMap.get(aFileno);
     }
     
     private static final Map<Integer, ChannelDescriptor> filenoDescriptorMap = new ConcurrentHashMap<Integer, ChannelDescriptor>();
 }
diff --git a/src/org/jruby/util/io/ChannelStream.java b/src/org/jruby/util/io/ChannelStream.java
index 5abc2750ac..c0d2939392 100644
--- a/src/org/jruby/util/io/ChannelStream.java
+++ b/src/org/jruby/util/io/ChannelStream.java
@@ -454,1181 +454,1175 @@ public class ChannelStream implements Stream, Finalizable {
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
 
     public InputStream newInputStream() {
         InputStream in = descriptor.getBaseInputStream();
         return in == null ? new InputStreamAdapter(this) : in;
     }
 
     public OutputStream newOutputStream() {
         return new OutputStreamAdapter(this);
     }
 
     public void clearerr() {
         eof = false;
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
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
     @Override
     public void finalize() throws Throwable {
         super.finalize();
         
         if (closedExplicitly) return;
 
         if (DEBUG) {
             LOG.info("finalize() for not explicitly closed stream");
         }
 
         // FIXME: I got a bunch of NPEs when I didn't check for nulls here...HOW?!
         if (descriptor != null && descriptor.isOpen()) {
             // tidy up
             finish(autoclose);
         }
     }
 
     public int ready() throws IOException {
         if (descriptor.getChannel() instanceof SelectableChannel) {
             int ready_stat = 0;
             java.nio.channels.Selector sel = SelectorFactory.openWithRetryFrom(null, ((SelectableChannel) descriptor.getChannel()).provider());
             SelectableChannel selchan = (SelectableChannel)descriptor.getChannel();
             synchronized (selchan.blockingLock()) {
                 boolean is_block = selchan.isBlocking();
                 try {
                     selchan.configureBlocking(false);
                     selchan.register(sel, java.nio.channels.SelectionKey.OP_READ);
                     ready_stat = sel.selectNow();
                     sel.close();
                 } catch (Throwable ex) {
                 } finally {
                     if (sel != null) {
                         try {
                             sel.close();
                         } catch (Exception e) {
                         }
                     }
                     selchan.configureBlocking(is_block);
                 }
             }
             return ready_stat;
         } else {
             return newInputStream().available();
         }
     }
 
     public synchronized void fputc(int c) throws IOException, BadDescriptorException {
         bufferedWrite(c);
     }
 
     public int ungetc(int c) {
         if (c == -1) {
             return -1;
         }
 
         // putting a bit back, so we're not at EOF anymore
         eof = false;
 
         // save the ungot
         ungotc = c;
 
         return c;
     }
 
     public synchronized int fgetc() throws IOException, BadDescriptorException {
         if (eof) {
             return -1;
         }
 
         checkReadable();
 
         int c = read();
 
         if (c == -1) {
             eof = true;
             return c;
         }
 
         return c & 0xff;
     }
 
     public synchronized int fwrite(ByteList string) throws IOException, BadDescriptorException {
         return bufferedWrite(string);
     }
 
     public synchronized int write(ByteBuffer buf) throws IOException, BadDescriptorException {
         return bufferedWrite(buf);
     }
 
     public synchronized int writenonblock(ByteList buf) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         // Ruby ignores empty syswrites
         if (buf == null || buf.length() == 0) return 0;
 
         if (buffer.position() != 0 && !flushWrite(false)) return 0;
 
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     if (oldBlocking) {
                         selectableChannel.configureBlocking(false);
                     }
                     return descriptor.write(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin(), buf.length()));
                 } finally {
                     if (oldBlocking) {
                         selectableChannel.configureBlocking(oldBlocking);
                     }
                 }
             }
         } else {
             // can't set nonblocking, so go ahead with it...not much else we can do
             return descriptor.write(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin(), buf.length()));
         }
     }
 
     public synchronized ByteList fread(int number) throws IOException, BadDescriptorException {
         try {
             if (number == 0) {
                 if (eof) {
                     return null;
                 } else {
                     return new ByteList(0);
                 }
             }
 
             return bufferedRead(number);
         } catch (EOFException e) {
             eof = true;
             return null;
         }
     }
 
     public synchronized ByteList readnonblock(int number) throws IOException, BadDescriptorException, EOFException {
         assert number >= 0;
 
         if (number == 0) {
             return null;
         }
 
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     selectableChannel.configureBlocking(false);
                     return readpartial(number);
                 } finally {
                     selectableChannel.configureBlocking(oldBlocking);
                 }
             }
         } else if (descriptor.getChannel() instanceof FileChannel) {
             return fread(number);
         } else {
             return null;
         }
     }
 
     public synchronized ByteList readpartial(int number) throws IOException, BadDescriptorException, EOFException {
         assert number >= 0;
 
         if (number == 0) {
             return null;
         }
         if (descriptor.getChannel() instanceof FileChannel) {
             return fread(number);
         }
 
         if (hasBufferedInputBytes()) {
             // already have some bytes buffered, just return those
             return bufferedRead(Math.min(bufferedInputBytesRemaining(), number));
         } else {
             // otherwise, we try an unbuffered read to get whatever's available
             return read(number);
         }
     }
 
     public synchronized int read(ByteBuffer dst) throws IOException, BadDescriptorException, EOFException {
         return read(dst, !(descriptor.getChannel() instanceof FileChannel));
     }
 
     public synchronized int read(ByteBuffer dst, boolean partial) throws IOException, BadDescriptorException, EOFException {
         assert dst.hasRemaining();
 
         return bufferedRead(dst, partial);
     }
 
     public synchronized int read() throws IOException, BadDescriptorException {
         try {
             descriptor.checkOpen();
 
             if (ungotc >= 0) {
                 int c = ungotc;
                 ungotc = -1;
                 return c;
             }
 
             return bufferedRead();
         } catch (EOFException e) {
             eof = true;
             return -1;
         }
     }
 
     public ChannelDescriptor getDescriptor() {
         return descriptor;
     }
 
     public void setBlocking(boolean block) throws IOException {
         if (!(descriptor.getChannel() instanceof SelectableChannel)) {
             return;
         }
         synchronized (((SelectableChannel) descriptor.getChannel()).blockingLock()) {
             blocking = block;
             try {
                 ((SelectableChannel) descriptor.getChannel()).configureBlocking(block);
             } catch (IllegalBlockingModeException e) {
                 // ignore this; select() will set the correct mode when it is finished
             }
         }
     }
 
     public boolean isBlocking() {
         return blocking;
     }
 
     public synchronized void freopen(Ruby runtime, String path, ModeFlags modes) throws DirectoryAsFileException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         // flush first
         flushWrite();
 
         // reset buffer
         buffer.clear();
         if (reading) {
             buffer.flip();
         }
 
         this.modes = modes;
 
         if (descriptor.isOpen()) {
             descriptor.close();
         }
 
         if (path.equals("/dev/null") || path.equalsIgnoreCase("nul:") || path.equalsIgnoreCase("nul")) {
             descriptor = descriptor.reopen(new NullChannel(), modes);
         } else {
             String cwd = runtime.getCurrentDirectory();
             JRubyFile theFile = JRubyFile.create(cwd,path);
 
             if (theFile.isDirectory() && modes.isWritable()) throw new DirectoryAsFileException();
 
             if (modes.isCreate()) {
                 if (theFile.exists() && modes.isExclusive()) {
                     throw runtime.newErrnoEEXISTError("File exists - " + path);
                 }
                 theFile.createNewFile();
             } else {
                 if (!theFile.exists()) {
                     throw runtime.newErrnoENOENTError("file not found - " + path);
                 }
             }
 
             // We always open this rw since we can only open it r or rw.
             RandomAccessFile file = new RandomAccessFile(theFile, modes.toJavaModeString());
 
             if (modes.isTruncate()) file.setLength(0L);
 
             descriptor = descriptor.reopen(file, modes);
 
             try {
                 if (modes.isAppendable()) lseek(0, SEEK_END);
             } catch (PipeException pe) {
                 // ignore, it's a pipe or fifo
             }
         }
     }
 
     public static Stream open(Ruby runtime, ChannelDescriptor descriptor) {
         return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor, true), descriptor.getOriginalModes());
     }
 
     public static Stream fdopen(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
         // check these modes before constructing, so we don't finalize the partially-initialized stream
         descriptor.checkNewModes(modes);
         return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor, modes, true), modes);
     }
 
     public static Stream open(Ruby runtime, ChannelDescriptor descriptor, boolean autoclose) {
         return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor, autoclose), descriptor.getOriginalModes());
     }
 
     public static Stream fdopen(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes, boolean autoclose) throws InvalidValueException {
         // check these modes before constructing, so we don't finalize the partially-initialized stream
         descriptor.checkNewModes(modes);
         return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor, modes, autoclose), modes);
     }
 
     private static Stream maybeWrapWithLineEndingWrapper(Stream stream, ModeFlags modes) {
         if (Platform.IS_WINDOWS && stream.getDescriptor().getChannel() instanceof FileChannel && !modes.isBinary()) {
             return new CRLFStreamWrapper(stream);
         }
         return stream;
     }
 
     public static Stream fopen(Ruby runtime, String path, ModeFlags modes) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         ChannelDescriptor descriptor = ChannelDescriptor.open(runtime.getCurrentDirectory(), path, modes, runtime.getClassLoader());
         Stream stream = fdopen(runtime, descriptor, modes);
 
-        try {
-            if (modes.isAppendable()) stream.lseek(0, Stream.SEEK_END);
-        } catch (PipeException pe) {
-            // ignore; it's a pipe or fifo
-        }
-
         return stream;
     }
 
     public Channel getChannel() {
         return getDescriptor().getChannel();
     }
 
     private static final class InputStreamAdapter extends java.io.InputStream {
         private final ChannelStream stream;
 
         public InputStreamAdapter(ChannelStream stream) {
             this.stream = stream;
         }
 
         @Override
         public int read() throws IOException {
             synchronized (stream) {
                 // If it can be pulled direct from the buffer, don't go via the slow path
                 if (stream.hasBufferedInputBytes()) {
                     try {
                         return stream.read();
                     } catch (BadDescriptorException ex) {
                         throw new IOException(ex.getMessage());
                     }
                 }
             }
 
             byte[] b = new byte[1];
             // java.io.InputStream#read must return an unsigned value;
             return read(b, 0, 1) == 1 ? b[0] & 0xff: -1;
         }
 
         @Override
         public int read(byte[] bytes, int off, int len) throws IOException {
             if (bytes == null) {
                 throw new NullPointerException("null destination buffer");
             }
             if ((len | off | (off + len) | (bytes.length - (off + len))) < 0) {
                 throw new IndexOutOfBoundsException();
             }
             if (len == 0) {
                 return 0;
             }
 
             try {
                 synchronized(stream) {
                     final int available = stream.bufferedInputBytesRemaining();
                      if (available >= len) {
                         return stream.copyBufferedBytes(bytes, off, len);
                     } else if (stream.getDescriptor().getChannel() instanceof SelectableChannel) {
                         SelectableChannel ch = (SelectableChannel) stream.getDescriptor().getChannel();
                         synchronized (ch.blockingLock()) {
                             boolean oldBlocking = ch.isBlocking();
                             try {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(true);
                                 }
                                 return stream.bufferedRead(ByteBuffer.wrap(bytes, off, len), true);
                             } finally {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(oldBlocking);
                                 }
                             }
                         }
                     } else {
                         return stream.bufferedRead(ByteBuffer.wrap(bytes, off, len), true);
                     }
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             } catch (EOFException ex) {
                 return -1;
             }
         }
 
         @Override
         public int available() throws IOException {
             synchronized (stream) {
                 return !stream.eof ? stream.bufferedInputBytesRemaining() : 0;
             }
         }
 
         @Override
         public void close() throws IOException {
             try {
                 synchronized (stream) {
                     stream.fclose();
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
     }
 
     private static final class OutputStreamAdapter extends java.io.OutputStream {
         private final ChannelStream stream;
 
         public OutputStreamAdapter(ChannelStream stream) {
             this.stream = stream;
         }
 
         @Override
         public void write(int i) throws IOException {
             synchronized (stream) {
                 if (!stream.isSync() && stream.hasBufferedOutputSpace()) {
                     stream.buffer.put((byte) i);
                     return;
                 }
             }
             byte[] b = { (byte) i };
             write(b, 0, 1);
         }
 
         @Override
         public void write(byte[] bytes, int off, int len) throws IOException {
             if (bytes == null) {
                 throw new NullPointerException("null source buffer");
             }
             if ((len | off | (off + len) | (bytes.length - (off + len))) < 0) {
                 throw new IndexOutOfBoundsException();
             }
 
             try {
                 synchronized(stream) {
                     if (!stream.isSync() && stream.bufferedOutputSpaceRemaining() >= len) {
                         stream.buffer.put(bytes, off, len);
 
                     } else if (stream.getDescriptor().getChannel() instanceof SelectableChannel) {
                         SelectableChannel ch = (SelectableChannel) stream.getDescriptor().getChannel();
                         synchronized (ch.blockingLock()) {
                             boolean oldBlocking = ch.isBlocking();
                             try {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(true);
                                 }
                                 stream.bufferedWrite(ByteBuffer.wrap(bytes, off, len));
                             } finally {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(oldBlocking);
                                 }
                             }
                         }
                     } else {
                         stream.bufferedWrite(ByteBuffer.wrap(bytes, off, len));
                     }
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
 
 
         @Override
         public void close() throws IOException {
             try {
                 synchronized (stream) {
                     stream.fclose();
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
 
         @Override
         public void flush() throws IOException {
             try {
                 synchronized (stream) {
                     stream.flushWrite(true);
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
     }
 }
