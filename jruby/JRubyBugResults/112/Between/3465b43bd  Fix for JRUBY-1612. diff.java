diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index ec1a15ae5b..69f6589a22 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -97,1065 +97,1069 @@ public class RubyFile extends RubyIO {
     
     // use static function because constructor call must be first statement in above constructor
     private static InputStream open(Ruby runtime, String path) {
         try {
             return new FileInputStream(path);
         } catch (FileNotFoundException e) {
             throw runtime.newIOError(e.getMessage());
         }
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
             this.handler = new IOHandlerUnseekable(runtime, in, null);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
         this.modes = handler.getModes();
         registerIOHandler(handler);
     }
 
     private static ObjectAllocator FILE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyFile instance = new RubyFile(runtime, klass);
             
             instance.setMetaClass(klass);
             
             return instance;
         }
     };
     
     public static RubyClass createFileClass(Ruby runtime) {
         RubyClass fileClass = runtime.defineClass("File", runtime.getIO(), FILE_ALLOCATOR);
         runtime.setFile(fileClass);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyFile.class);   
         RubyString separator = runtime.newString("/");
         
         fileClass.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyFile;
                 }
             };
 
         separator.freeze();
         fileClass.defineConstant("SEPARATOR", separator);
         fileClass.defineConstant("Separator", separator);
         
         if (File.separatorChar == '\\') {
             RubyString altSeparator = runtime.newString("\\");
             altSeparator.freeze();
             fileClass.defineConstant("ALT_SEPARATOR", altSeparator);
         } else {
             fileClass.defineConstant("ALT_SEPARATOR", runtime.getNil());
         }
         
         RubyString pathSeparator = runtime.newString(File.pathSeparator);
         pathSeparator.freeze();
         fileClass.defineConstant("PATH_SEPARATOR", pathSeparator);
         
         // TODO: These were missing, so we're not handling them elsewhere?
         // FIXME: The old value, 32786, didn't match what IOModes expected, so I reference
         // the constant here. THIS MAY NOT BE THE CORRECT VALUE.
         fileClass.fastSetConstant("BINARY", runtime.newFixnum(IOModes.BINARY));
         fileClass.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         fileClass.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         fileClass.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_CASEFOLD));
         fileClass.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
         fileClass.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
         
         // Create constants for open flags
         fileClass.fastSetConstant("RDONLY", runtime.newFixnum(IOModes.RDONLY));
         fileClass.fastSetConstant("WRONLY", runtime.newFixnum(IOModes.WRONLY));
         fileClass.fastSetConstant("RDWR", runtime.newFixnum(IOModes.RDWR));
         fileClass.fastSetConstant("CREAT", runtime.newFixnum(IOModes.CREAT));
         fileClass.fastSetConstant("EXCL", runtime.newFixnum(IOModes.EXCL));
         fileClass.fastSetConstant("NOCTTY", runtime.newFixnum(IOModes.NOCTTY));
         fileClass.fastSetConstant("TRUNC", runtime.newFixnum(IOModes.TRUNC));
         fileClass.fastSetConstant("APPEND", runtime.newFixnum(IOModes.APPEND));
         fileClass.fastSetConstant("NONBLOCK", runtime.newFixnum(IOModes.NONBLOCK));
         
         // Create constants for flock
         fileClass.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         fileClass.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         fileClass.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         fileClass.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         // Create Constants class
         RubyModule constants = fileClass.defineModuleUnder("Constants");
         
         // TODO: These were missing, so we're not handling them elsewhere?
         constants.fastSetConstant("BINARY", runtime.newFixnum(32768));
         constants.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(1));
         constants.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(8));
         constants.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(4));
         constants.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(2));
         
         // Create constants for open flags
         constants.fastSetConstant("RDONLY", runtime.newFixnum(IOModes.RDONLY));
         constants.fastSetConstant("WRONLY", runtime.newFixnum(IOModes.WRONLY));
         constants.fastSetConstant("RDWR", runtime.newFixnum(IOModes.RDWR));
         constants.fastSetConstant("CREAT", runtime.newFixnum(IOModes.CREAT));
         constants.fastSetConstant("EXCL", runtime.newFixnum(IOModes.EXCL));
         constants.fastSetConstant("NOCTTY", runtime.newFixnum(IOModes.NOCTTY));
         constants.fastSetConstant("TRUNC", runtime.newFixnum(IOModes.TRUNC));
         constants.fastSetConstant("APPEND", runtime.newFixnum(IOModes.APPEND));
         constants.fastSetConstant("NONBLOCK", runtime.newFixnum(IOModes.NONBLOCK));
         
         // Create constants for flock
         constants.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         constants.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         constants.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         constants.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         // TODO Singleton methods: blockdev?, chardev?, directory?
         // TODO Singleton methods: executable?, executable_real?,
         // TODO Singleton methods: ftype, grpowned?, lchmod, lchown, link, owned?
         // TODO Singleton methods: pipe?, readlink, setgid?, setuid?, socket?,
         // TODO Singleton methods: stat, sticky?, symlink?, umask
         
         runtime.getFileTest().extend_object(fileClass);
         
         // atime and ctime are implemented like mtime, since we don't have an atime API in Java
         
         // TODO: Define instance methods: lchmod, lchown, lstat
         // atime and ctime are implemented like mtime, since we don't have an atime API in Java
         
         fileClass.defineAnnotatedMethods(RubyFile.class);
         fileClass.dispatcher = callbackFactory.createDispatcher(fileClass);
         
         return fileClass;
     }
     
     public void openInternal(String newPath, IOModes newModes) {
         this.path = newPath;
         this.modes = newModes;
         
         try {
             if (newPath.equals("/dev/null")) {
                 handler = new IOHandlerNull(getRuntime(), newModes);
             } else {
                 handler = new IOHandlerSeekable(getRuntime(), newPath, newModes);
             }
             
             registerIOHandler(handler);
         } catch (InvalidValueException e) {
         	throw getRuntime().newErrnoEINVALError();
         } catch (DirectoryAsFileException e) {
             throw getRuntime().newErrnoEISDirError();
         } catch (FileNotFoundException e) {
             // FNFException can be thrown in both cases, when the file
             // is not found, or when permission is denied.
             if (new File(newPath).exists()) {
                 throw getRuntime().newErrnoEACCESError(
                         "Permission denied - " + newPath);
             }
             throw getRuntime().newErrnoENOENTError(
                     "File not found - " + newPath);
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
 		}
     }
     
     @JRubyMethod
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
     public IRubyObject flock(IRubyObject lockingConstant) {
         FileChannel fileChannel = handler.getFileChannel();
         int lockMode = RubyNumeric.num2int(lockingConstant);
 
         try {
             switch (lockMode) {
                 case LOCK_UN:
                 case LOCK_UN | LOCK_NB:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
 
                         return getRuntime().newFixnum(0);
                     }
                     break;
                 case LOCK_EX:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
                     currentLock = fileChannel.lock();
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 case LOCK_EX | LOCK_NB:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
                     currentLock = fileChannel.tryLock();
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 case LOCK_SH:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
 
                     currentLock = fileChannel.lock(0L, Long.MAX_VALUE, true);
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 case LOCK_SH | LOCK_NB:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
 
                     currentLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true);
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 default:
             }
         } catch (IOException ioe) {
             if (getRuntime().getDebug().isTrue()) {
                 ioe.printStackTrace(System.err);
             }
             // Return false here
         } catch (java.nio.channels.OverlappingFileLockException ioe) {
             if (getRuntime().getDebug().isTrue()) {
                 ioe.printStackTrace(System.err);
             }
             // Return false here
         }
 
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(required = 1, optional = 2, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError(0, 1);
         }
         else if (args.length < 3) {
             IRubyObject fd = TypeConverter.convertToTypeWithCheck(args[0], getRuntime().getFixnum(), MethodIndex.TO_INT, "to_int");
             if (!fd.isNil()) {
                 args[0] = fd;
                 return super.initialize(args, block);
             }
         }
 
         getRuntime().checkSafeString(args[0]);
         path = args[0].toString();
         modes = args.length > 1 ? getModes(getRuntime(), args[1]) : new IOModes(getRuntime(), IOModes.RDONLY);
 
         // One of the few places where handler may be null.
         // If handler is not null, it indicates that this object
         // is being reused.
         if (handler != null) {
             close();
         }
         openInternal(path, modes);
 
         if (block.isGiven()) {
             // getRuby().getRuntime().warn("File::new does not take block; use File::open instead");
         }
         return this;
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject chmod(IRubyObject arg) {
         RubyInteger mode = arg.convertToInteger();
 
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         int result = Ruby.getPosix().chmod(path, (int) mode.getLongValue());
 
         return getRuntime().newFixnum(result);
     }
 
     @JRubyMethod(required = 2)
     public IRubyObject chown(IRubyObject arg, IRubyObject arg2) {
         RubyInteger owner = arg.convertToInteger();
         RubyInteger group = arg2.convertToInteger();
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         int result = Ruby.getPosix().chown(path, (int) owner.getLongValue(), (int) group.getLongValue());
 
         return RubyFixnum.newFixnum(getRuntime(), result);
     }
 
     @JRubyMethod(name = {"atime", "ctime"})
     public IRubyObject atime() {
         return getRuntime().newTime(JRubyFile.create(getRuntime().getCurrentDirectory(), this.path).getParentFile().lastModified());
     }
 
     @JRubyMethod(name = {"mtime"})
     public IRubyObject mtime() {
         return getRuntime().newTime(JRubyFile.create(getRuntime().getCurrentDirectory(), this.path).lastModified());
     }
 
     @JRubyMethod
     public RubyString path() {
         return getRuntime().newString(path);
     }
 
     @JRubyMethod
     public IRubyObject stat() {
         return getRuntime().newRubyFileStat(path);
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject truncate(IRubyObject arg) {
         RubyInteger newLength = arg.convertToInteger();
         if (newLength.getLongValue() < 0) {
             throw getRuntime().newErrnoEINVALError("invalid argument: " + path);
         }
         try {
             handler.truncate(newLength.getLongValue());
         } catch (IOHandler.PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             // Should we do anything?
         }
 
         return RubyFixnum.zero(getRuntime());
     }
 
     public String toString() {
         return "RubyFile(" + path + ", " + modes + ", " + fileno + ")";
     }
 
     // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
     private static IOModes getModes(Ruby runtime, IRubyObject object) {
         if (object instanceof RubyString) {
             return new IOModes(runtime, ((RubyString) object).toString());
         } else if (object instanceof RubyFixnum) {
             return new IOModes(runtime, ((RubyFixnum) object).getLongValue());
         }
 
         throw runtime.newTypeError("Invalid type for modes");
     }
 
     @JRubyMethod
     public IRubyObject inspect() {
         StringBuffer val = new StringBuffer();
         val.append("#<File:").append(path);
         if(!isOpen()) {
             val.append(" (closed)");
         }
         val.append(">");
         return getRuntime().newString(val.toString());
     }
     
     /* File class methods */
     
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject basename(IRubyObject recv, IRubyObject[] args) {
         Arity.checkArgumentCount(recv.getRuntime(), args, 1, 2);
         
         String name = RubyString.stringValue(args[0]).toString();
 
         // MRI-compatible basename handling for windows drive letter paths
         if (IS_WINDOWS) {
             if (name.length() > 1 && name.charAt(1) == ':' && Character.isLetter(name.charAt(0))) {
                 switch (name.length()) {
                 case 2:
                     return recv.getRuntime().newString("").infectBy(args[0]);
                 case 3:
                     return recv.getRuntime().newString(name.substring(2)).infectBy(args[0]);
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
         return recv.getRuntime().newString(name).infectBy(args[0]);
     }
     
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject chmod(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 2, -1);
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == Ruby.getPosix().chmod(filename.toString(), (int)mode.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 3, rest = true, meta = true)
     public static IRubyObject chown(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 3, -1);
         
         int count = 0;
         RubyInteger owner = args[0].convertToInteger();
         RubyInteger group = args[1].convertToInteger();
         for (int i = 2; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == Ruby.getPosix().chown(filename.toString(), (int)owner.getLongValue(), (int)group.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject dirname(IRubyObject recv, IRubyObject arg) {
         RubyString filename = RubyString.stringValue(arg);
         String jfilename = filename.toString();
         String name = jfilename.replace('\\', '/');
         boolean trimmedSlashes = false;
 
         while (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
             trimmedSlashes = true;
             name = name.substring(0, name.length() - 1);
         }
 
         String result;
         if (IS_WINDOWS && name.length() == 2 &&
                 isWindowsDriveLetter(name.charAt(0)) && name.charAt(1) == ':') {
             // C:\ is returned unchanged (after slash trimming)
             if (trimmedSlashes) {
                 result = jfilename.substring(0, 3);
             } else {
                 result = jfilename.substring(0, 2) + '.';
             }
         } else {
             //TODO deal with UNC names
             int index = name.lastIndexOf('/');
             if (index == -1) return recv.getRuntime().newString(".");
             if (index == 0) return recv.getRuntime().newString("/");
 
             // Include additional path separator (e.g. C:\myfile.txt becomes C:\, not C:)
             if (IS_WINDOWS && index == 2 && 
                     isWindowsDriveLetter(name.charAt(0)) && name.charAt(1) == ':') {
                 index++;
             }
             
             result = jfilename.substring(0, index);
          }
 
          return recv.getRuntime().newString(result).infectBy(filename);
 
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
     public static IRubyObject extname(IRubyObject recv, IRubyObject arg) {
         IRubyObject baseFilename = basename(recv, new IRubyObject[] { arg });
         String filename = RubyString.stringValue(baseFilename).toString();
         String result = "";
         
         int dotIndex = filename.lastIndexOf(".");
         if (dotIndex > 0  && dotIndex != (filename.length() - 1)) {
             // Dot is not at beginning and not at end of filename. 
             result = filename.substring(dotIndex);
         }
 
         return recv.getRuntime().newString(result);
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
     @JRubyMethod(required = 1, optional = 2, meta = true)
     public static IRubyObject expand_path(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 1, 2);
         
         String relativePath = RubyString.stringValue(args[0]).toString();
         String cwd = null;
         
         // Handle ~user paths 
         relativePath = expandUserPath(recv, relativePath);
         
         // If there's a second argument, it's the path to which the first 
         // argument is relative.
         if (args.length == 2 && !args[1].isNil()) {
             
             String cwdArg = RubyString.stringValue(args[1]).toString();
             
             // Handle ~user paths.
             cwd = expandUserPath(recv, cwdArg);
             
             // If the path isn't absolute, then prepend the current working
             // directory to the path.
             if ( cwd.charAt(0) != '/' ) {
                 cwd = JRubyFile.create(runtime.getCurrentDirectory(), cwd)
                     .getAbsolutePath();
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
         
         // Find out which string to check.
         String padSlashes = "";
         if (relativePath.length() > 0 && relativePath.charAt(0) == '/') {
             padSlashes = countSlashes(relativePath);
         } else if (cwd.length() > 0 && cwd.charAt(0) == '/') {
             padSlashes = countSlashes(cwd);
         }
         
         JRubyFile path;
         
         if (relativePath.length() == 0) {
             path = JRubyFile.create(relativePath, cwd);
         } else {
             path = JRubyFile.create(cwd, relativePath);
         }
         
         return runtime.newString(padSlashes + canonicalize(path.getAbsolutePath()));
     }
     
     /**
      * This method checks a path, and if it starts with ~, then it expands 
      * the path to the absolute path of the user's home directory. If the 
      * string does not begin with ~, then the string is simply retuned 
      * unaltered.
      * @param recv
      * @param path Path to check
      * @return Expanded path
      */
     private static String expandUserPath( IRubyObject recv, String path ) {
         
         int pathLength = path.length();
 
         if (pathLength >= 1 && path.charAt(0) == '~') {
             // Enebo : Should ~frogger\\foo work (it doesnt in linux ruby)?
             int userEnd = path.indexOf('/');
             
             if (userEnd == -1) {
                 if (pathLength == 1) {
                     // Single '~' as whole path to expand
                     path = RubyDir.getHomeDirectoryPath(recv).toString();
                 } else {
                     // No directory delimeter.  Rest of string is username
                     userEnd = pathLength;
                 }
             }
             
             if (userEnd == 1) {
                 // '~/...' as path to expand
                 path = RubyDir.getHomeDirectoryPath(recv).toString() +
                         path.substring(1);
             } else if (userEnd > 1){
                 // '~user/...' as path to expand
                 String user = path.substring(1, userEnd);
                 IRubyObject dir = RubyDir.getHomeDirectoryPath(recv, user);
                 
                 if (dir.isNil()) {
                     Ruby runtime = recv.getRuntime();
                     throw runtime.newArgumentError("user " + user + " does not exist");
                 }
                 
                 path = "" + dir +
                         (pathLength == userEnd ? "" : path.substring(userEnd));
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
 
     private static String canonicalize(String path) {
         return canonicalize(null, path);
     }
 
     private static String canonicalize(String canonicalPath, String remaining) {
 
         if (remaining == null) return canonicalPath;
 
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
             // skip it
             if (canonicalPath != null && canonicalPath.length() == 0 ) canonicalPath += "/";
         } else if (child.equals("..")) {
             if (canonicalPath == null) throw new IllegalArgumentException("Cannot have .. at the start of an absolute path");
             int lastDir = canonicalPath.lastIndexOf('/');
             if (lastDir == -1) {
                 canonicalPath = "";
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
     public static IRubyObject fnmatch(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int flags;
         if (Arity.checkArgumentCount(runtime, args, 2, 3) == 3) {
             flags = RubyNumeric.num2int(args[2]);
         } else {
             flags = 0;
         }
         
         ByteList pattern = args[0].convertToString().getByteList();
         ByteList path = args[1].convertToString().getByteList();
         if (org.jruby.util.Dir.fnmatch(pattern.bytes, pattern.begin, pattern.realSize , path.bytes, path.begin, path.realSize, flags) == 0) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     /*
      * Fixme:  This does not have exact same semantics as RubyArray.join, but they
      * probably could be consolidated (perhaps as join(args[], sep, doChomp)).
      */
     @JRubyMethod(rest = true, meta = true)
     public static RubyString join(IRubyObject recv, IRubyObject[] args) {
         boolean isTainted = false;
         StringBuffer buffer = new StringBuffer();
         
         for (int i = 0; i < args.length; i++) {
             if (args[i].isTaint()) {
                 isTainted = true;
             }
             String element;
             if (args[i] instanceof RubyString) {
                 element = args[i].toString();
             } else if (args[i] instanceof RubyArray) {
                 // Fixme: Need infinite recursion check to put [...] and not go into a loop
                 element = join(recv, ((RubyArray) args[i]).toJavaArray()).toString();
             } else {
                 element = args[i].convertToString().toString();
             }
             
             chomp(buffer);
             if (i > 0 && !element.startsWith("/") && !element.startsWith("\\")) {
                 buffer.append("/");
             }
             buffer.append(element);
         }
         
         RubyString fixedStr = RubyString.newString(recv.getRuntime(), buffer.toString());
         fixedStr.setTaint(isTainted);
         return fixedStr;
     }
     
     private static void chomp(StringBuffer buffer) {
         int lastIndex = buffer.length() - 1;
         
         while (lastIndex >= 0 && (buffer.lastIndexOf("/") == lastIndex || buffer.lastIndexOf("\\") == lastIndex)) {
             buffer.setLength(lastIndex);
             lastIndex--;
         }
     }
     
     @JRubyMethod(name = {"lstat", "stat"}, required = 1, meta = true)
     public static IRubyObject lstat(IRubyObject recv, IRubyObject filename) {
         RubyString name = RubyString.stringValue(filename);
         return recv.getRuntime().newRubyFileStat(name.toString());
     }
     
     @JRubyMethod(name = {"atime", "ctime"}, required = 1, meta = true)
     public static IRubyObject atime(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         RubyString name = RubyString.stringValue(filename);
         JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), name.toString());
 
         if (!file.exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + name.toString());
         }
         
         return runtime.newTime(file.getParentFile().lastModified());
     }
     
     @JRubyMethod(name = {"mtime"}, required = 1, meta = true)
     public static IRubyObject mtime(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         RubyString name = RubyString.stringValue(filename);
         JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), name.toString());
 
         if (!file.exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + name.toString());
         }
         
         return runtime.newTime(file.lastModified());
     }
     
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, meta = true)
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         return open(recv, args, true, block);
     }
     
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, boolean tryToYield, Block block) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 1, -1);
         ThreadContext tc = runtime.getCurrentContext();
         
         RubyFile file;
 
         if (args[0] instanceof RubyInteger) { // open with file descriptor
             file = new RubyFile(runtime, (RubyClass) recv);
             file.initialize(args, Block.NULL_BLOCK);            
         } else {
             RubyString pathString = RubyString.stringValue(args[0]);
             runtime.checkSafeString(pathString);
             String path = pathString.toString();
 
             IOModes modes = args.length >= 2 ? getModes(runtime, args[1]) : new IOModes(runtime, IOModes.RDONLY);
             file = new RubyFile(runtime, (RubyClass) recv);
 
             RubyInteger fileMode = args.length >= 3 ? args[2].convertToInteger() : null;
 
             file.openInternal(path, modes);
 
             if (fileMode != null) chmod(recv, new IRubyObject[] {fileMode, pathString});
         }
         
         if (tryToYield && block.isGiven()) {
             try {
                 return block.yield(tc, file);
             } finally {
                 file.close();
             }
         }
         
         return file;
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject rename(IRubyObject recv, IRubyObject oldName, IRubyObject newName) {
         Ruby runtime = recv.getRuntime();
         RubyString oldNameString = RubyString.stringValue(oldName);
         RubyString newNameString = RubyString.stringValue(newName);
         runtime.checkSafeString(oldNameString);
         runtime.checkSafeString(newNameString);
         JRubyFile oldFile = JRubyFile.create(runtime.getCurrentDirectory(), oldNameString.toString());
         JRubyFile newFile = JRubyFile.create(runtime.getCurrentDirectory(), newNameString.toString());
         
         if (!oldFile.exists() || !newFile.getParentFile().exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + oldNameString + " or " + newNameString);
         }
         oldFile.renameTo(JRubyFile.create(runtime.getCurrentDirectory(), newNameString.toString()));
         
         return RubyFixnum.zero(runtime);
     }
     
     @JRubyMethod(name = "size?", required = 1, meta = true)
     public static IRubyObject size_p(IRubyObject recv, IRubyObject filename) {
         long size = 0;
         
         try {
             FileInputStream fis = new FileInputStream(new File(filename.toString()));
             FileChannel chan = fis.getChannel();
             size = chan.size();
             chan.close();
             fis.close();
         } catch (IOException ioe) {
             // missing files or inability to open should just return nil
         }
         
         if (size == 0) return recv.getRuntime().getNil();
         
         return recv.getRuntime().newFixnum(size);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static RubyArray split(IRubyObject recv, IRubyObject arg) {
         RubyString filename = RubyString.stringValue(arg);
         
         return filename.getRuntime().newArray(dirname(recv, filename),
                 basename(recv, new IRubyObject[] { filename }));
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject symlink(IRubyObject recv, IRubyObject from, IRubyObject to) {
         if (Ruby.getPosix().symlink(from.toString(),to.toString()) == -1) {
             // FIXME: When we get JNA3 we need to properly write this to errno.
             recv.getRuntime().newSystemCallError("bad symlink");
         }
         
         return recv.getRuntime().newFixnum(0);
     }
 
     @JRubyMethod(name = "symlink?", required = 1, meta = true)
     public static IRubyObject symlink_p(IRubyObject recv, IRubyObject arg1) {
         Ruby runtime = recv.getRuntime();
         RubyString filename = RubyString.stringValue(arg1);
         
         JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), filename.toString());
         
         try {
             // Only way to determine symlink is to compare canonical and absolute files
             // However symlinks in containing path must not produce false positives, so we check that first
             File absoluteParent = file.getAbsoluteFile().getParentFile();
             File canonicalParent = file.getAbsoluteFile().getParentFile().getCanonicalFile();
             
             if (canonicalParent.getAbsolutePath().equals(absoluteParent.getAbsolutePath())) {
                 // parent doesn't change when canonicalized, compare absolute and canonical file directly
                 return file.getAbsolutePath().equals(file.getCanonicalPath()) ? runtime.getFalse() : runtime.getTrue();
             }
             
             // directory itself has symlinks (canonical != absolute), so build new path with canonical parent and compare
             file = JRubyFile.create(runtime.getCurrentDirectory(), canonicalParent.getAbsolutePath() + "/" + file.getName());
             return file.getAbsolutePath().equals(file.getCanonicalPath()) ? runtime.getFalse() : runtime.getTrue();
         } catch (IOException ioe) {
             // problem canonicalizing the file; nothing we can do but return false
             return runtime.getFalse();
         }
     }
     
     // Can we produce IOError which bypasses a close?
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject truncate(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = recv.getRuntime();
         RubyString filename = arg1.convertToString(); // TODO: SafeStringValue here
         RubyInteger newLength = arg2.convertToInteger(); 
         
+        if (!new File(filename.getByteList().toString()).exists()) {
+            throw runtime.newErrnoENOENTError("No such file or directory - " + filename.getByteList().toString());
+        }
+
         if (newLength.getLongValue() < 0) {
             throw runtime.newErrnoEINVALError("invalid argument: " + filename);
         }
         
         IRubyObject[] args = new IRubyObject[] { filename, runtime.newString("r+") };
         RubyFile file = (RubyFile) open(recv, args, false, null);
         file.truncate(newLength);
         file.close();
         
         return RubyFixnum.zero(runtime);
     }
     
     /**
      * This method does NOT set atime, only mtime, since Java doesn't support anything else.
      */
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject utime(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 2, -1);
         
         // Ignore access_time argument since Java does not support it.
         
         long mtime;
         if (args[1] instanceof RubyTime) {
             mtime = ((RubyTime) args[1]).getJavaDate().getTime();
         } else if (args[1] instanceof RubyNumeric) {
             mtime = RubyNumeric.num2long(args[1]);
         } else {
             mtime = 0;
         }
         
         for (int i = 2, j = args.length; i < j; i++) {
             RubyString filename = RubyString.stringValue(args[i]);
             runtime.checkSafeString(filename);
             JRubyFile fileToTouch = JRubyFile.create(runtime.getCurrentDirectory(),filename.toString());
             
             if (!fileToTouch.exists()) {
                 throw runtime.newErrnoENOENTError(" No such file or directory - \"" + filename + "\"");
             }
             
             fileToTouch.setLastModified(mtime);
         }
         
         return runtime.newFixnum(args.length - 2);
     }
     
     @JRubyMethod(name = {"unlink", "delete"}, rest = true, meta = true)
     public static IRubyObject unlink(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         
         for (int i = 0; i < args.length; i++) {
             RubyString filename = RubyString.stringValue(args[i]);
             runtime.checkSafeString(filename);
             JRubyFile lToDelete = JRubyFile.create(runtime.getCurrentDirectory(),filename.toString());
             
             if (!lToDelete.exists()) {
                 throw runtime.newErrnoENOENTError(" No such file or directory - \"" + filename + "\"");
             }
             
             if (!lToDelete.delete()) return runtime.getFalse();
         }
         
         return runtime.newFixnum(args.length);
     }
 }
diff --git a/test/test_file.rb b/test/test_file.rb
index 9c58ce945b..a48aaeb82e 100644
--- a/test/test_file.rb
+++ b/test/test_file.rb
@@ -1,312 +1,322 @@
 require 'test/unit'
 require 'rbconfig'
 
 class TestFile < Test::Unit::TestCase
   
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
 
   # JRUBY-1116: these are currently broken on windows
   # what are these testing anyway?!?!
   if Config::CONFIG['target_os'] =~ /Windows|mswin/
     def test_windows_basename
       assert_equal "", File.basename("c:")
       assert_equal "\\", File.basename("c:\\")
       assert_equal "abc", File.basename("c:abc")
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
       assert_equal("//foo", File.expand_path("../foo", "//bar"))
       assert_equal("/bin", File.expand_path("../../../../../../../bin", "/"))
       assert_equal(File.join(Dir.pwd, "x/y/z/a/b"), File.expand_path("a/b", "x/y/z"))
       assert_equal(File.join(Dir.pwd, "bin"), File.expand_path("../../bin", "tmp/x"))
       assert_equal("/bin", File.expand_path("./../foo/./.././../bin", "/a/b"))
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
 
   def test_file_size_query
     assert(File.size?('build.xml'))
   end
 
   def test_file_open_utime
     filename = "__test__file"
     File.open(filename, "w") {|f| }
     time = Time.now - 3600
     File.utime(time, time, filename)
     # File mtime resolution may not be sub-second on all platforms (e.g., windows)
     # allow for some slop
     assert((time.to_i - File.mtime(filename).to_i).abs < 5)
     File.unlink(filename)
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
 
   def test_more_constants
     # FIXME: Not sure how I feel about pulling in Java here
     if Java::java.lang.System.get_property("file.separator") == '/'
       assert_equal(nil, File::ALT_SEPARATOR)
     else
       assert_equal("\\", File::ALT_SEPARATOR)
     end
 
     assert_equal(File::FNM_CASEFOLD, File::FNM_SYSCASE)
   end
 
   def test_truncate
     # JRUBY-1025: negative int passed to truncate should raise EINVAL
     filename = "__truncate_test_file"
     assert_raises(Errno::EINVAL) {
       File.open(filename, 'w').truncate(-1)
     }
     assert_raises(Errno::EINVAL) {
       File.truncate(filename, -1)
     }
     File.delete(filename)
   end
 
   def test_file_create
     filename = '2nnever'
     assert_equal(nil, File.new(filename, File::CREAT).read(1))
     File.delete(filename)
 
     assert_raises(IOError) { File.new(filename, File::CREAT) << 'b' }
     File.delete(filename)
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
+  
+  def test_truncate_doesnt_create_file
+    name = "___foo_bar___"
+    assert(!File.exists?(name))
+
+    assert_raises(Errno::ENOENT) { File.truncate(name, 100) }
+
+    assert(!File.exists?(name))
+  end
 end
+