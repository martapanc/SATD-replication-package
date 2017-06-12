diff --git a/.classpath b/.classpath
index 12960b82ca..b2094db91b 100644
--- a/.classpath
+++ b/.classpath
@@ -1,18 +1,19 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <classpath>
 	<classpathentry kind="src" path="src"/>
 	<classpathentry excluding="**/CVS/*" kind="src" path="test"/>
 	<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
 	<classpathentry kind="lib" path="build_lib/jna.jar"/>
 	<classpathentry kind="lib" path="build_lib/junit.jar"/>
 	<classpathentry kind="lib" path="build_lib/jline-0.9.91.jar"/>
 	<classpathentry kind="lib" path="lib/bsf.jar"/>
 	<classpathentry kind="lib" path="build_lib/backport-util-concurrent.jar"/>
 	<classpathentry kind="lib" path="build_lib/asm-util-3.0.jar"/>
 	<classpathentry kind="lib" path="build_lib/asm-commons-3.0.jar"/>
 	<classpathentry kind="lib" path="build_lib/asm-3.0.jar"/>
 	<classpathentry kind="lib" path="build_lib/asm-tree-3.0.jar"/>
 	<classpathentry kind="lib" path="build_lib/asm-analysis-3.0.jar"/>
 	<classpathentry kind="lib" path="build_lib/joni.jar"/>
+	<classpathentry kind="lib" path="build_lib/joda-time-1.5.1.jar"/>
 	<classpathentry kind="output" path="build/classes/jruby"/>
 </classpath>
diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index e87cf6d290..35762efd61 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -97,1113 +97,1131 @@ public class RubyFile extends RubyIO {
         this(runtime, path, open(runtime, path));
     }
     
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
         
         // TODO Singleton methods: readlink, umask 
         
         runtime.getFileTest().extend_object(fileClass);
         
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
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         return getRuntime().newFixnum(getRuntime().getPosix().chmod(path, mode));
     }
 
     @JRubyMethod(required = 2)
     public IRubyObject chown(IRubyObject arg1, IRubyObject arg2) {
         int owner = (int) arg1.convertToInteger().getLongValue();
         int group = (int) arg2.convertToInteger().getLongValue();
         
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         return getRuntime().newFixnum(getRuntime().getPosix().chown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject atime() {
         return getRuntime().newFileStat(path, false).atime();
     }
 
     @JRubyMethod
     public IRubyObject ctime() {
         return getRuntime().newFileStat(path, false).ctime();
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject lchmod(IRubyObject arg) {
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         return getRuntime().newFixnum(getRuntime().getPosix().lchmod(path, mode));
     }
 
     @JRubyMethod(required = 2)
     public IRubyObject lchown(IRubyObject arg1, IRubyObject arg2) {
         int owner = (int) arg1.convertToInteger().getLongValue();
         int group = (int) arg2.convertToInteger().getLongValue();
         
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         return getRuntime().newFixnum(getRuntime().getPosix().lchown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject lstat() {
         return getRuntime().newFileStat(path, true);
     }
     
     @JRubyMethod
     public IRubyObject mtime() {
         return getLastModified(getRuntime(), path);
     }
 
     @JRubyMethod
     public RubyString path() {
         return getRuntime().newString(path);
     }
 
     @JRubyMethod
     public IRubyObject stat() {
         return getRuntime().newFileStat(path, false);
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
             
             boolean result = 0 == runtime.getPosix().chmod(filename.toString(), (int)mode.getLongValue());
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
             
             boolean result = 0 == runtime.getPosix().chown(filename.toString(), (int)owner.getLongValue(), (int)group.getLongValue());
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
     
     @JRubyMethod(name = "ftype", required = 1)
     public static IRubyObject ftype(IRubyObject recv, IRubyObject filename) {
         return recv.getRuntime().newFileStat(filename.convertToString().toString(), true).ftype();
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
     
     @JRubyMethod(name = "lstat", required = 1, meta = true)
     public static IRubyObject lstat(IRubyObject recv, IRubyObject filename) {
         return recv.getRuntime().newFileStat(filename.convertToString().toString(), true);
     }
 
     @JRubyMethod(name = "stat", required = 1, meta = true)
     public static IRubyObject stat(IRubyObject recv, IRubyObject filename) {
         return recv.getRuntime().newFileStat(filename.convertToString().toString(), false);
     }
 
     @JRubyMethod(name = "atime", required = 1, meta = true)
     public static IRubyObject atime(IRubyObject recv, IRubyObject filename) {
         return recv.getRuntime().newFileStat(filename.convertToString().toString(), false).atime();
     }
 
     @JRubyMethod(name = "ctime", required = 1, meta = true)
     public static IRubyObject ctime(IRubyObject recv, IRubyObject filename) {
         return recv.getRuntime().newFileStat(filename.convertToString().toString(), false).ctime();
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject lchmod(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 2, -1);
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == runtime.getPosix().lchmod(filename.toString(), (int)mode.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 3, rest = true, meta = true)
     public static IRubyObject lchown(IRubyObject recv, IRubyObject[] args) {
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
             
             boolean result = 0 == runtime.getPosix().lchown(filename.toString(), (int)owner.getLongValue(), (int)group.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
 
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject link(IRubyObject recv, IRubyObject from, IRubyObject to) {
         if (recv.getRuntime().getPosix().link(from.toString(),to.toString()) == -1) {
             // FIXME: When we get JNA3 we need to properly write this to errno.
             recv.getRuntime().newSystemCallError("bad symlink");
         }
         
         return recv.getRuntime().newFixnum(0);
     }
 
     @JRubyMethod(name = "mtime", required = 1, meta = true)
     public static IRubyObject mtime(IRubyObject recv, IRubyObject filename) {
         return getLastModified(recv.getRuntime(), filename.convertToString().toString());
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
-            throw runtime.newErrnoENOENTError("No such file or directory - " + oldNameString + " or " + newNameString);
+            throw runtime.newErrnoENOENTError("No such file or directory - " + oldNameString + 
+                    " or " + newNameString);
         }
-        oldFile.renameTo(JRubyFile.create(runtime.getCurrentDirectory(), newNameString.toString()));
-        
-        return RubyFixnum.zero(runtime);
+
+        JRubyFile dest = JRubyFile.create(runtime.getCurrentDirectory(), newNameString.toString());
+
+        if (oldFile.renameTo(dest)) {  // rename is successful
+            return RubyFixnum.zero(runtime);
+        }
+
+        // rename via Java API call wasn't successful, let's try some tricks, similar to MRI 
+
+        if (newFile.exists()) {
+            recv.getRuntime().getPosix().chmod(newNameString.toString(), 0666);
+            newFile.delete();
+        }
+
+        if (oldFile.renameTo(dest)) { // try to rename one more time
+            return RubyFixnum.zero(runtime);
+        }
+
+        throw runtime.newErrnoEACCESError("Permission denied - " + oldNameString + " or " + 
+                newNameString);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static RubyArray split(IRubyObject recv, IRubyObject arg) {
         RubyString filename = RubyString.stringValue(arg);
         
         return filename.getRuntime().newArray(dirname(recv, filename),
                 basename(recv, new IRubyObject[] { filename }));
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject symlink(IRubyObject recv, IRubyObject from, IRubyObject to) {
         if (recv.getRuntime().getPosix().symlink(from.toString(),to.toString()) == -1) {
             // FIXME: When we get JNA3 we need to properly write this to errno.
             recv.getRuntime().newSystemCallError("bad symlink");
         }
         
         return recv.getRuntime().newFixnum(0);
     }
 
     // Can we produce IOError which bypasses a close?
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject truncate(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = recv.getRuntime();
         RubyString filename = arg1.convertToString(); // TODO: SafeStringValue here
         RubyInteger newLength = arg2.convertToInteger(); 
         
         if (!new File(filename.getByteList().toString()).exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + filename.getByteList().toString());
         }
 
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
 
     // Fast path since JNA stat is about 10x slower than this
     private static IRubyObject getLastModified(Ruby runtime, String path) {
         long lastModified = JRubyFile.create(runtime.getCurrentDirectory(), path).lastModified();
         
         // 0 according to API does is non-existent file or IOError
         if (lastModified == 0L) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + path);
         }
         
         return runtime.newTime(lastModified);
     }
 }
diff --git a/src/org/jruby/RubyRange.java b/src/org/jruby/RubyRange.java
index ff551793e7..9bc14a72c0 100644
--- a/src/org/jruby/RubyRange.java
+++ b/src/org/jruby/RubyRange.java
@@ -1,538 +1,534 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001 Ed Sinjiashvili <slorcim@users.sourceforge.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 package org.jruby;
 
 import java.io.IOException;
 import java.util.List;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /**
  * @author jpetersen
  */
 public class RubyRange extends RubyObject {
 
     private IRubyObject begin;
     private IRubyObject end;
     private boolean isExclusive;
 
     public RubyRange(Ruby runtime, RubyClass impl) {
         super(runtime, impl);
         begin = end = runtime.getNil();
     }
 
     public void init(IRubyObject aBegin, IRubyObject aEnd, RubyBoolean aIsExclusive) {
         if (!(aBegin instanceof RubyFixnum && aEnd instanceof RubyFixnum)) {
             try {
                 IRubyObject result = aBegin.callMethod(getRuntime().getCurrentContext(), MethodIndex.OP_SPACESHIP, "<=>", aEnd);
                 if (result.isNil()) throw getRuntime().newArgumentError("bad value for range");
             } catch (RaiseException rExcptn) {
                 throw getRuntime().newArgumentError("bad value for range");
             }
         }
 
         this.begin = aBegin;
         this.end = aEnd;
         this.isExclusive = aIsExclusive.isTrue();
     }
     
     private static ObjectAllocator RANGE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyRange(runtime, klass);
         }
     };
     
     protected void copySpecialInstanceVariables(IRubyObject clone) {
         RubyRange range = (RubyRange)clone;
         range.begin = begin;
         range.end = end;
         range.isExclusive = isExclusive;
     }
 
     private static final ObjectMarshal RANGE_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             RubyRange range = (RubyRange)obj;
 
             List<Variable<IRubyObject>> attrs = range.getVariableList();
 
             attrs.add(new VariableEntry<IRubyObject>("begin", range.begin));
             attrs.add(new VariableEntry<IRubyObject>("end", range.end));
             attrs.add(new VariableEntry<IRubyObject>(
                     "excl", range.isExclusive ? runtime.getTrue() : runtime.getFalse()));
 
             marshalStream.dumpVariables(attrs);
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             RubyRange range = (RubyRange)type.allocate();
             
             unmarshalStream.registerLinkTarget(range);
 
             unmarshalStream.defaultVariablesUnmarshal(range);
             
             range.begin = range.removeInternalVariable("begin");
             range.end = range.removeInternalVariable("end");
             range.isExclusive = range.removeInternalVariable("excl").isTrue();
 
             return range;
         }
     };
     
     public static RubyClass createRangeClass(Ruby runtime) {
         RubyClass result = runtime.defineClass("Range", runtime.getObject(), RANGE_ALLOCATOR);
         runtime.setRange(result);
         result.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyRange;
                 }
             };
         
         result.setMarshal(RANGE_MARSHAL);
         
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyRange.class);
         
         result.includeModule(runtime.getEnumerable());
 
         // We override Enumerable#member? since ranges in 1.8.1 are continuous.
         //        result.defineMethod("member?", callbackFactory.getMethod("include_p", RubyKernel.IRUBY_OBJECT));
         //        result.defineMethod("===", callbackFactory.getMethod("include_p", RubyKernel.IRUBY_OBJECT));
 
         result.defineAnnotatedMethods(RubyRange.class);
         
         CallbackFactory classCB = runtime.callbackFactory(RubyClass.class);
         result.getMetaClass().defineMethod("new", classCB.getOptMethod("newInstance"));
         
         result.dispatcher = callbackFactory.createDispatcher(result);
         
         return result;
     }
 
     /**
      * Converts this Range to a pair of integers representing a start position 
      * and length.  If either of the range's endpoints is negative, it is added to 
      * the <code>limit</code> parameter in an attempt to arrive at a position 
      * <i>p</i> such that <i>0&nbsp;&lt;=&nbsp;p&nbsp;&lt;=&nbsp;limit</i>. If 
      * <code>truncate</code> is true, the result will be adjusted, if possible, so 
      * that <i>begin&nbsp;+&nbsp;length&nbsp;&lt;=&nbsp;limit</i>.  If <code>strict</code> 
      * is true, an exception will be raised if the range can't be converted as 
      * described above; otherwise it just returns <b>null</b>. 
      * 
      * @param limit    the size of the object (e.g., a String or Array) that 
      *                 this range is being evaluated against.
      * @param truncate if true, result must fit within the range <i>(0..limit)</i>.
      * @param isStrict   if true, raises an exception if the range can't be converted.
      * @return         a two-element array representing a start value and a length, 
      *                 or <b>null</b> if the conversion failed.
      */
     public long[] getBeginLength(long limit, boolean truncate, boolean isStrict) {
         long beginLong = RubyNumeric.num2long(begin);
         long endLong = RubyNumeric.num2long(end);
 
         // Apparent legend for MRI 'err' param to JRuby 'truncate' and 'isStrict':
         // 0 =>  truncate && !strict
         // 1 => !truncate &&  strict
         // 2 =>  truncate &&  strict
         if (!isExclusive) {
             endLong++;
         }
 
         if (beginLong < 0) {
             beginLong += limit;
             if (beginLong < 0) {
                 if (isStrict) {
                     throw getRuntime().newRangeError(inspect().toString() + " out of range.");
                 }
                 return null;
             }
         }
 
         if (truncate && beginLong > limit) {
             if (isStrict) {
                 throw getRuntime().newRangeError(inspect().toString() + " out of range.");
             }
             return null;
         }
 
         if (truncate && endLong > limit) {
             endLong = limit;
         }
 
         if (endLong < 0 || (!isExclusive && endLong == 0)) {
             endLong += limit;
-            if (endLong < 0) {
-                if (isStrict) {
-                    throw getRuntime().newRangeError(inspect().toString() + " out of range.");
-                }
-                return null;
-            }
+            // don't check against negative endLong,
+            // that's how MRI behaves.
         }
 
         return new long[]{beginLong, Math.max(endLong - beginLong, 0L)};
     }
 
     public long[] begLen(long len, int err){
         long beg = RubyNumeric.num2long(this.begin);
         long end = RubyNumeric.num2long(this.end);
 
         if(beg < 0){
             beg += len;
             if(beg < 0){
                 if(err != 0){
                     throw getRuntime().newRangeError(beg + ".." + (isExclusive ? "." : "") + end + " out of range");
                 }
                 return null;
             }
         }
 
         if(err == 0 || err == 2){
             if(beg > len){
                 if(err != 0){
                     throw getRuntime().newRangeError(beg + ".." + (isExclusive ? "." : "") + end + " out of range");
                 }
                 return null;
             }
             if(end > len){
                 end = len;
             }
         }
         if(end < 0){
             end += len;
         }
         if(!isExclusive){
             end++;
         }
         len = end - beg;
         if(len < 0){
             len = 0;
         }
 
         return new long[]{beg, len};
     }    
 
     public static RubyRange newRange(Ruby runtime, IRubyObject begin, IRubyObject end, boolean isExclusive) {
         RubyRange range = new RubyRange(runtime, runtime.getRange());
         range.init(begin, end, isExclusive ? runtime.getTrue() : runtime.getFalse());
         return range;
     }
 
     @JRubyMethod(name = "initialize", required = 2, optional = 1, frame = true)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         if (args.length == 3) {
             init(args[0], args[1], args[2].isTrue() ? getRuntime().getTrue() : getRuntime().getFalse());
         } else if (args.length == 2) {
             init(args[0], args[1], getRuntime().getFalse());
         } else {
             throw getRuntime().newArgumentError("Wrong arguments. (anObject, anObject, aBoolean = false) expected");
         }
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = {"first", "begin"})
     public IRubyObject first() {
         return begin;
     }
 
     @JRubyMethod(name = {"last", "end"})
     public IRubyObject last() {
         return end;
     }
     
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         ThreadContext context = getRuntime().getCurrentContext();
         long baseHash = (isExclusive ? 1 : 0);
         long beginHash = ((RubyFixnum) begin.callMethod(context, MethodIndex.HASH, "hash")).getLongValue();
         long endHash = ((RubyFixnum) end.callMethod(context, MethodIndex.HASH, "hash")).getLongValue();
         
         long hash = baseHash;
         hash = hash ^ (beginHash << 1);
         hash = hash ^ (endHash << 9);
         hash = hash ^ (baseHash << 24);
         
         return getRuntime().newFixnum(hash);
     }
     
     private static byte[] DOTDOTDOT = "...".getBytes();
     private static byte[] DOTDOT = "..".getBytes();
 
     @JRubyMethod(name = "inspect", frame = true)
     public IRubyObject inspect(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();        
         RubyString str = RubyString.objAsString(begin.callMethod(context, "inspect")).strDup();
         RubyString str2 = RubyString.objAsString(end.callMethod(context, "inspect"));
 
         str.cat(isExclusive ? DOTDOTDOT : DOTDOT);
         str.concat(str2);
         str.infectBy(str2);
         return str;
     }
     
     @JRubyMethod(name = "to_s", frame = true)
     public IRubyObject to_s(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();        
         RubyString str = RubyString.objAsString(begin).strDup();
         RubyString str2 = RubyString.objAsString(end);
 
         str.cat(isExclusive ? DOTDOTDOT : DOTDOT);
         str.concat(str2);
         str.infectBy(str2);
         return str;
 
     }
 
     @JRubyMethod(name = "exclude_end?")
     public RubyBoolean exclude_end_p() {
         return getRuntime().newBoolean(isExclusive);
     }
 
     @JRubyMethod(name = "length", frame = true)
     public RubyFixnum length(Block block) {
         long size = 0;
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (begin.callMethod(context, MethodIndex.OP_GT, ">", end).isTrue()) {
             return getRuntime().newFixnum(0);
         }
 
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum) {
             size = ((RubyNumeric) end).getLongValue() - ((RubyNumeric) begin).getLongValue();
             if (!isExclusive) {
                 size++;
             }
         } else { // Support length for arbitrary classes
             IRubyObject currentObject = begin;
 	    int compareMethod = isExclusive ? MethodIndex.OP_LT : MethodIndex.OP_LE;
 
 	    while (currentObject.callMethod(context, compareMethod, (String)MethodIndex.NAMES.get(compareMethod), end).isTrue()) {
 		size++;
 		if (currentObject.equals(end)) {
 		    break;
 		}
 		currentObject = currentObject.callMethod(context, "succ");
 	    }
 	}
         return getRuntime().newFixnum(size);
     }
 
     @JRubyMethod(name = "==", required = 1, frame = true)
     public IRubyObject op_equal(IRubyObject other, Block block) {
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyRange)) return getRuntime().getFalse();
         RubyRange otherRange = (RubyRange) other;
         boolean result =
             begin.op_equal(otherRange.begin).isTrue() &&
             end.op_equal(otherRange.end).isTrue() &&
             isExclusive == otherRange.isExclusive;
         return getRuntime().newBoolean(result);
     }
     
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql_p(IRubyObject other) {
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyRange)) return getRuntime().getFalse();
         RubyRange otherRange = (RubyRange)other;
         if (!begin.equals(otherRange.begin) || !end.equals(otherRange.end) || isExclusive != otherRange.isExclusive) return getRuntime().getFalse();
         return getRuntime().getTrue();
     }
 
     @JRubyMethod(name = "each", frame = true)
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         if (!begin.respondsTo("succ")) throw getRuntime().newTypeError("can't iterate from " + begin.getMetaClass().getName());
 
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum) {
             long endLong = ((RubyNumeric) end).getLongValue();
             long i = ((RubyNumeric) begin).getLongValue();
 
             if (!isExclusive) {
                 endLong += 1;
             }
 
             for (; i < endLong; i++) {
                 block.yield(context, getRuntime().newFixnum(i));
             }
         } else if (begin instanceof RubyString) {
             ((RubyString) begin).upto(end, isExclusive, block);
         } else if (getRuntime().getNumeric().isInstance(begin)) {
             if (!isExclusive) {
                 end = end.callMethod(context, MethodIndex.OP_PLUS, "+", RubyFixnum.one(getRuntime()));
             }
             while (begin.callMethod(context, MethodIndex.OP_LT, "<", end).isTrue()) {
                 block.yield(context, begin);
                 begin = begin.callMethod(context, MethodIndex.OP_PLUS, "+", RubyFixnum.one(getRuntime()));
             }
         } else {
             IRubyObject v = begin;
 
             if (isExclusive) {
                 while (v.callMethod(context, MethodIndex.OP_LT, "<", end).isTrue()) {
                     if (v.equals(end)) {
                         break;
                     }
                     block.yield(context, v);
                     v = v.callMethod(context, "succ");
                 }
             } else {
                 while (v.callMethod(context, MethodIndex.OP_LE, "<=", end).isTrue()) {
                     block.yield(context, v);
                     if (v.equals(end)) {
                         break;
                     }
                     v = v.callMethod(context, "succ");
                 }
             }
         }
 
         return this;
     }
     
     @JRubyMethod(name = "step", optional = 1, frame = true)
     public IRubyObject step(IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         
         IRubyObject currentObject = begin;
         int compareMethod = isExclusive ? MethodIndex.OP_LT : MethodIndex.OP_LE;
         // int stepSize = (int) (args.length == 0 ? 1 : args[0].convertToInteger().getLongValue());
         double stepSize = 1.0;
         if (args.length != 0) {
             stepSize = Double.parseDouble(args[0].toString());
         }
         if (stepSize == 0) {
             throw getRuntime().newArgumentError("step can't be 0");
         }
         else if (stepSize < 0) {
             throw getRuntime().newArgumentError("step can't be negative");
         }
 
         ThreadContext context = getRuntime().getCurrentContext();
         
         if (begin instanceof RubyFloat && end instanceof RubyFloat) {
             RubyFloat stepNum = getRuntime().newFloat(stepSize);
             while (currentObject.callMethod(context, compareMethod, MethodIndex.NAMES.get(compareMethod), end).isTrue()) {
                 block.yield(context, currentObject);
                 currentObject = currentObject.callMethod(context, MethodIndex.OP_PLUS, "+", stepNum);
             }
         } else if (begin instanceof RubyNumeric && end instanceof RubyNumeric) {
             stepSize = Math.floor(stepSize);
             if (stepSize == 0) {
                 throw getRuntime().newArgumentError("step can't be 0");
             }
             RubyFixnum stepNum = getRuntime().newFixnum(Double.valueOf(stepSize).longValue());
             while (currentObject.callMethod(context, compareMethod, MethodIndex.NAMES.get(compareMethod), end).isTrue()) {
                 block.yield(context, currentObject);
                 currentObject = currentObject.callMethod(context, MethodIndex.OP_PLUS, "+", stepNum);
             }
         } else if(begin instanceof RubyString && end instanceof RubyString) {
           RubyString afterEnd = isExclusive ? (RubyString) end : (RubyString) end.callMethod(context, "succ");
           boolean pastEnd = isExclusive && currentObject.callMethod(context, MethodIndex.EQUALEQUAL, "==", end).isTrue();  
           while(pastEnd == false) {
               block.yield(context, currentObject);
               for (int i = 0; i < stepSize; i++) {
                   currentObject = currentObject.callMethod(context, "succ");
                   if(currentObject.callMethod(context, MethodIndex.EQUALEQUAL, "==", afterEnd).isTrue()) {
                     pastEnd = true;
                     break;
                   } 
               }
           }
         } else {
             while (currentObject.callMethod(context, compareMethod, MethodIndex.NAMES.get(compareMethod), end).isTrue()) {
                 block.yield(context, currentObject);
                 
                 for (int i = 0; i < stepSize; i++) {
                     currentObject = currentObject.callMethod(context, "succ");
                 }
             }
         }
         
         return this;
     }
 
     private boolean r_lt(IRubyObject a, IRubyObject b) {
         IRubyObject r = a.callMethod(getRuntime().getCurrentContext(),MethodIndex.OP_SPACESHIP, "<=>",b);
         if(r.isNil()) {
             return false;
         }
         if(RubyComparable.cmpint(r,a,b) < 0) {
             return true;
         }
         return false;
     }
 
     private boolean r_le(IRubyObject a, IRubyObject b) {
         IRubyObject r = a.callMethod(getRuntime().getCurrentContext(),MethodIndex.OP_SPACESHIP, "<=>",b);
         if(r.isNil()) {
             return false;
         }
         if(RubyComparable.cmpint(r,a,b) <= 0) {
             return true;
         }
         return false;
     }
 
     @JRubyMethod(name = {"include?", "member?", "==="}, required = 1, frame = true)
     public RubyBoolean include_p(IRubyObject obj, Block block) {
         RubyBoolean val = getRuntime().getFalse();
         if(r_le(begin,obj)) {
             if(isExclusive) {
                 if(r_lt(obj,end)) {
                     val = getRuntime().getTrue();
                 }
             } else {
                 if(r_le(obj,end)) {
                     val = getRuntime().getTrue();
                 }
             }
         }
         return val;
     }
 }
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index 31a9944bab..3fbe859a7b 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -1,2951 +1,2959 @@
 /*
  **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
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
 package org.jruby;
 
 import java.io.UnsupportedEncodingException;
 import java.util.HashMap;
 import java.util.Locale;
 
 import org.joni.Matcher;
 import org.joni.Option;
 import org.joni.Regex;
 import org.joni.Region;
 import org.joni.encoding.Encoding;
 import org.joni.encoding.specific.ASCIIEncoding;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.Pack;
 import org.jruby.util.Sprintf;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyString extends RubyObject {
     private static final ASCIIEncoding ASCII = ASCIIEncoding.INSTANCE;
     
     private static final int TMPLOCK_STR_F = 1 << 11;
     private static final int TMPLOCK_OR_FROZEN_STR_F = TMPLOCK_STR_F | FROZEN_F;
 
     // string doesn't have it's own ByteList (values) 
     private volatile boolean shared_buffer = false;
 
     // string has it's own ByteList, but it's pointing to a shared buffer (byte[])
     private volatile boolean shared_bytelist = false;
     
     private ByteList value;
 
     private static ObjectAllocator STRING_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyString newString = runtime.newString("");
             
             newString.setMetaClass(klass);
             
             return newString;
         }
     };
     
     public static RubyClass createStringClass(Ruby runtime) {
         RubyClass stringClass = runtime.defineClass("String", runtime.getObject(), STRING_ALLOCATOR);
         runtime.setString(stringClass);
         stringClass.index = ClassIndex.STRING;
         stringClass.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyString;
                 }
             };
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyString.class);
         
         stringClass.includeModule(runtime.getComparable());
         stringClass.includeModule(runtime.getEnumerable());
         
         stringClass.defineAnnotatedMethods(RubyString.class);
         stringClass.dispatcher = callbackFactory.createDispatcher(stringClass);
         
         return stringClass;
     }
 
     /** short circuit for String key comparison
      * 
      */
     public final boolean eql(IRubyObject other) {
         return other instanceof RubyString && value.equal(((RubyString)other).value);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, CharSequence value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = new ByteList(ByteList.plain(value),false);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, byte[] value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = new ByteList(value);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, ByteList value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = value;
     }
     
     private RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, boolean objectSpace) {
         super(runtime, rubyClass, objectSpace);
         assert value != null;
         this.value = value;
     }    
 
     public int getNativeTypeIndex() {
         return ClassIndex.STRING;
     }
 
     public Class getJavaClass() {
         return String.class;
     }
 
     public RubyString convertToString() {
         return this;
     }
 
     public String toString() {
         return value.toString();
     }
 
     /** rb_str_dup
      * 
      */
     public final RubyString strDup() {
         return strDup(getMetaClass());
     }
 
     final RubyString strDup(RubyClass clazz) {
         shared_bytelist = true;
         RubyString dup = new RubyString(getRuntime(), clazz, value);
         dup.shared_bytelist = true;
 
         dup.infectBy(this);
         return dup;
     }    
 
     public final RubyString makeShared(int index, int len) {
         if (len == 0) {
             RubyString s = newEmptyString(getRuntime(), getMetaClass());
             s.infectBy(this);
             return s;
         }
         
         if (!shared_bytelist) shared_buffer = true;
         RubyString shared = new RubyString(getRuntime(), getMetaClass(), value.makeShared(index, len));
         shared.shared_buffer = true;
 
         shared.infectBy(this);
         return shared;
     }
     
     private void tmpLock() {
         if ((flags & TMPLOCK_STR_F) != 0) throw getRuntime().newRuntimeError("temporal locking already locked string");
         flags |= TMPLOCK_STR_F;
     }
     
     private void tmpUnlock() {
         if ((flags & TMPLOCK_STR_F) == 0) throw getRuntime().newRuntimeError("temporal unlocking already unlocked string");
         flags &= ~TMPLOCK_STR_F;
     }
 
     private final void modifyCheck() {
         if ((flags & TMPLOCK_OR_FROZEN_STR_F) != 0) {
             if ((flags & FROZEN_F) != 0) throw getRuntime().newFrozenError("string" + getMetaClass().getName());           
             if ((flags & TMPLOCK_STR_F) != 0) throw getRuntime().newTypeError("can't modify string; temporarily locked");
         }
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify string");
         }
     }
     
     private final void modifyCheck(byte[] b, int len) {
         if (value.bytes != b || value.realSize != len) throw getRuntime().newRuntimeError("string modified");
     }
     
     private final void frozenCheck() {
         if (isFrozen()) throw getRuntime().newRuntimeError("string frozen");
     }
 
     /** rb_str_modify
      * 
      */
 
     public final void modify() {
         modifyCheck();
 
         if (shared_buffer || shared_bytelist) {
             if (shared_bytelist) {
                 value = value.dup();
             } else if (shared_buffer) {
                 value.unshare();
             }
             shared_buffer = false;
             shared_bytelist = false;
         }
 
         value.invalidate();
     }
     
     public final void modify(int length) {
         modifyCheck();
 
         if (shared_buffer || shared_bytelist) {
             if (shared_bytelist) {
                 value = value.dup(length);
             } else if (shared_buffer) {
                 value.unshare(length);
             }
             shared_buffer = false;
             shared_bytelist = false;
         } else {
             value = value.dup(length);
         }
         value.invalidate();
     }        
     
     private final void view(ByteList bytes) {
         modifyCheck();
 
         value = bytes;
         shared_buffer = false;
         shared_bytelist = false;
     }
 
     private final void view(byte[]bytes) {
         modifyCheck();        
 
         value.replace(bytes);
         shared_buffer = false;
         shared_bytelist = false;
 
         value.invalidate();        
     }
 
     private final void view(int index, int len) {
         modifyCheck();
 
         if (shared_buffer || shared_bytelist) {
             if (shared_bytelist) {
                 // if len == 0 then shared empty
                 value = value.makeShared(index, len);
                 shared_bytelist = false;
                 shared_buffer = true;
             } else if (shared_buffer) {
                 value.view(index, len);
             }
         } else {        
             value.view(index, len);
             // FIXME this below is temporary, but its much safer for COW (it prevents not shared Strings with begin != 0)
             // this allows now e.g.: ByteList#set not to be begin aware
             shared_buffer = true;
         }
 
         value.invalidate();
     }
 
     public static String bytesToString(byte[] bytes, int beg, int len) {
         return new String(ByteList.plain(bytes, beg, len));
     }
 
     public static String byteListToString(ByteList bytes) {
         return bytesToString(bytes.unsafeBytes(), bytes.begin(), bytes.length());
     }
 
     public static String bytesToString(byte[] bytes) {
         return bytesToString(bytes, 0, bytes.length);
     }
 
     public static byte[] stringToBytes(String string) {
         return ByteList.plain(string);
     }
 
     public static boolean isDigit(int c) {
         return c >= '0' && c <= '9';
     }
 
     public static boolean isUpper(int c) {
         return c >= 'A' && c <= 'Z';
     }
 
     public static boolean isLower(int c) {
         return c >= 'a' && c <= 'z';
     }
 
     public static boolean isLetter(int c) {
         return isUpper(c) || isLower(c);
     }
 
     public static boolean isAlnum(int c) {
         return isUpper(c) || isLower(c) || isDigit(c);
     }
 
     public static boolean isPrint(int c) {
         return c >= 0x20 && c <= 0x7E;
     }
 
     public RubyString asString() {
         return this;
     }
 
     public IRubyObject checkStringType() {
         return this;
     }
 
     @JRubyMethod(name = {"to_s", "to_str"})
     public IRubyObject to_s() {
         if (getMetaClass().getRealClass() != getRuntime().getString()) {
             return strDup(getRuntime().getString());
         }
         return this;
     }
 
     /* rb_str_cmp_m */
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newFixnum(op_cmp((RubyString)other));
         }
 
         // deal with case when "other" is not a string
         if (other.respondsTo("to_str") && other.respondsTo("<=>")) {
             IRubyObject result = other.callMethod(getRuntime().getCurrentContext(),
                     MethodIndex.OP_SPACESHIP, "<=>", this);
 
             if (result instanceof RubyNumeric) {
                 return ((RubyNumeric) result).op_uminus();
             }
         }
 
         return getRuntime().getNil();
     }
         
     /**
      * 
      */
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(IRubyObject other) {
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyString)) {
             if (!other.respondsTo("to_str")) return getRuntime().getFalse();
             Ruby runtime = getRuntime();
             return other.callMethod(runtime.getCurrentContext(), MethodIndex.EQUALEQUAL, "==", this).isTrue() ? runtime.getTrue() : runtime.getFalse();
         }
         return value.equal(((RubyString)other).value) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(IRubyObject other) {
         RubyString str = other.convertToString();
         
         ByteList result = new ByteList(value.realSize + str.value.realSize);
         result.realSize = value.realSize + str.value.realSize;
         System.arraycopy(value.bytes, value.begin, result.bytes, 0, value.realSize);
         System.arraycopy(str.value.bytes, str.value.begin, result.bytes, value.realSize, str.value.realSize);
       
         RubyString resultStr = newString(getRuntime(), result);
         if (isTaint() || str.isTaint()) resultStr.setTaint(true);
         return resultStr;
     }
 
     @JRubyMethod(name = "*", required = 1)
     public IRubyObject op_mul(IRubyObject other) {
         RubyInteger otherInteger = (RubyInteger) other.convertToInteger();
         long len = otherInteger.getLongValue();
 
         if (len < 0) throw getRuntime().newArgumentError("negative argument");
 
         // we limit to int because ByteBuffer can only allocate int sizes
         if (len > 0 && Integer.MAX_VALUE / len < value.length()) {
             throw getRuntime().newArgumentError("argument too big");
         }
         ByteList newBytes = new ByteList(value.length() * (int)len);
 
         for (int i = 0; i < len; i++) {
             newBytes.append(value);
         }
 
         RubyString newString = newString(getRuntime(), newBytes);
         newString.setTaint(isTaint());
         return newString;
     }
 
     @JRubyMethod(name = "%", required = 1)
     public IRubyObject op_format(IRubyObject arg) {
         // FIXME: Should we make this work with platform's locale, or continue hardcoding US?
         return getRuntime().newString((ByteList)Sprintf.sprintf(Locale.US, value, arg));
     }
 
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         return getRuntime().newFixnum(value.hashCode());
     }
 
     public int hashCode() {
         return value.hashCode();
     }
 
     public boolean equals(Object other) {
         if (this == other) return true;
 
         if (other instanceof RubyString) {
             RubyString string = (RubyString) other;
 
             if (string.value.equal(value)) return true;
         }
 
         return false;
     }
 
     /** rb_obj_as_string
      *
      */
     public static RubyString objAsString(IRubyObject obj) {
         if (obj instanceof RubyString) return (RubyString) obj;
 
         IRubyObject str = obj.callMethod(obj.getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s");
 
         if (!(str instanceof RubyString)) return (RubyString) obj.anyToString();
 
         if (obj.isTaint()) str.setTaint(true);
 
         return (RubyString) str;
     }
 
     /** rb_str_cmp
      *
      */
     public int op_cmp(RubyString other) {
         return value.cmp(other.value);
     }
 
     /** rb_to_id
      *
      */
     public String asJavaString() {
         // TODO: This used to intern; but it didn't appear to change anything
         // turning that off, and it's unclear if it was needed. Plus, we intern
         // 
         return toString();
     }
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *
      */
     public RubyString newString(CharSequence s) {
         return new RubyString(getRuntime(), getType(), s);
     }
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *
      */
     public RubyString newString(ByteList s) {
         return new RubyString(getRuntime(), getMetaClass(), s);
     }
 
     // Methods of the String class (rb_str_*):
 
     /** rb_str_new2
      *
      */
     public static RubyString newString(Ruby runtime, CharSequence str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
     
     public static RubyString newEmptyString(Ruby runtime) {
         return newEmptyString(runtime, runtime.getString());
     }
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass) {
         RubyString empty = new RubyString(runtime, metaClass, ByteList.EMPTY_BYTELIST);
         empty.shared_bytelist = true;
         return empty;
     }
 
     public static RubyString newUnicodeString(Ruby runtime, String str) {
         try {
             return new RubyString(runtime, runtime.getString(), new ByteList(str.getBytes("UTF8"), false));
         } catch (UnsupportedEncodingException uee) {
             return new RubyString(runtime, runtime.getString(), str);
         }
     }
 
     public static RubyString newString(Ruby runtime, RubyClass clazz, CharSequence str) {
         return new RubyString(runtime, clazz, str);
     }
 
     public static RubyString newString(Ruby runtime, byte[] bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newStringLight(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes, false);
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyString orig) {
         orig.shared_bytelist = true;
         RubyString str = new RubyString(runtime, runtime.getString(), orig.value);
         str.shared_bytelist = true;
         return str;
     }       
     
     public static RubyString newStringShared(Ruby runtime, ByteList bytes) {
         return newStringShared(runtime, runtime.getString(), bytes);
     }    
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes) {
         RubyString str = new RubyString(runtime, clazz, bytes);
         str.shared_bytelist = true;
         return str;
     }    
 
     public static RubyString newString(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] bytes2 = new byte[length];
         System.arraycopy(bytes, start, bytes2, 0, length);
         return new RubyString(runtime, runtime.getString(), new ByteList(bytes2, false));
     }
 
     public IRubyObject doClone(){
         return newString(getRuntime(), value.dup());
     }
 
     // FIXME: cat methods should be more aware of sharing to prevent unnecessary reallocations in certain situations 
     public RubyString cat(byte[] str) {
         modify();
         value.append(str);
         return this;
     }
 
     public RubyString cat(byte[] str, int beg, int len) {
         modify();        
         value.append(str, beg, len);
         return this;
     }
 
     public RubyString cat(ByteList str) {
         modify();        
         value.append(str);
         return this;
     }
 
     public RubyString cat(byte ch) {
         modify();        
         value.append(ch);
         return this;
     }
 
     /** rb_str_replace_m
      *
      */
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1)
     public RubyString replace(IRubyObject other) {
         if (this == other) return this;
 
         modifyCheck();
 
          
         RubyString otherStr =  stringValue(other);
 
         shared_bytelist = true;
         otherStr.shared_bytelist = true;
         
         value = otherStr.value;
 
         infectBy(other);
         return this;
     }
 
     @JRubyMethod(name = "reverse")
     public RubyString reverse() {
         if (value.length() <= 1) return strDup();
 
         ByteList buf = new ByteList(value.length()+2);
         buf.realSize = value.length();
         int src = value.length() - 1;
         int dst = 0;
         
         while (src >= 0) buf.set(dst++, value.get(src--));
 
         RubyString rev = new RubyString(getRuntime(), getMetaClass(), buf);
         rev.infectBy(this);
         return rev;
     }
 
     @JRubyMethod(name = "reverse!")
     public RubyString reverse_bang() {
         if (value.length() > 1) {
             modify();
             for (int i = 0; i < (value.length() / 2); i++) {
                 byte b = (byte) value.get(i);
                 
                 value.set(i, value.get(value.length() - i - 1));
                 value.set(value.length() - i - 1, b);
             }
         }
         
         return this;
     }
 
     /** rb_str_s_new
      *
      */
     public static RubyString newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString newString = newString(recv.getRuntime(), "");
         newString.setMetaClass((RubyClass) recv);
         newString.callInit(args, block);
         return newString;
     }
 
     @JRubyMethod(name = "initialize", optional = 1, frame = true)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         if (Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 1) replace(args[0]);
 
         return this;
     }
 
     @JRubyMethod(name = "casecmp", required = 1)
     public IRubyObject casecmp(IRubyObject other) {
         int compare = value.caseInsensitiveCmp(stringValue(other).value);
         return RubyFixnum.newFixnum(getRuntime(), compare);
     }
 
     /** rb_str_match
      *
      */
     @JRubyMethod(name = "=~", required = 1)
     public IRubyObject op_match(IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).op_match(this);
         if (other instanceof RubyString) {
             throw getRuntime().newTypeError("type mismatch: String given");
         }
         return other.callMethod(getRuntime().getCurrentContext(), "=~", this);
     }
 
     /** rb_str_match2
      *
      */
     @JRubyMethod(name = "~")
     public IRubyObject op_match2() {
         return RubyRegexp.newRegexp(getRuntime(), value, 0).op_match2();
     }
 
     /**
      * String#match(pattern)
      *
      * rb_str_match_m
      *
      * @param pattern Regexp or String
      */
     @JRubyMethod(name = "match", required = 1)
     public IRubyObject match(IRubyObject pattern) {
         return getPattern(pattern, false).callMethod(getRuntime().getCurrentContext(), "match", this);
     }
 
     /** rb_str_capitalize
      *
      */
     @JRubyMethod(name = "capitalize")
     public IRubyObject capitalize() {
         RubyString str = strDup();
         str.capitalize_bang();
         return str;
     }
 
     /** rb_str_capitalize_bang
      *
      */
     @JRubyMethod(name = "capitalize!")
     public IRubyObject capitalize_bang() {        
         if (value.realSize == 0) {
             modifyCheck();
             return getRuntime().getNil();
         }
         
         modify();
         
         int s = value.begin;
         int send = s + value.realSize;
         byte[]buf = value.bytes;
         
         
         
         boolean modify = false;
         
         int c = buf[s] & 0xff;
         if (ASCII.isLower(c)) {
             buf[s] = (byte)ASCIIEncoding.asciiToUpper(c);
             modify = true;
         }
         
         while (++s < send) {
             c = (char)(buf[s] & 0xff);
             if (ASCII.isUpper(c)) {
                 buf[s] = (byte)ASCIIEncoding.asciiToLower(c);
                 modify = true;
             }
         }
         
         if (modify) return this;
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = ">=", required = 1)
     public IRubyObject op_ge(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(op_cmp((RubyString) other) >= 0);
         }
 
         return RubyComparable.op_ge(this, other);
     }
 
     @JRubyMethod(name = ">", required = 1)
     public IRubyObject op_gt(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(op_cmp((RubyString) other) > 0);
         }
 
         return RubyComparable.op_gt(this, other);
     }
 
     @JRubyMethod(name = "<=", required = 1)
     public IRubyObject op_le(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(op_cmp((RubyString) other) <= 0);
         }
 
         return RubyComparable.op_le(this, other);
     }
 
     @JRubyMethod(name = "<", required = 1)
     public IRubyObject op_lt(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(op_cmp((RubyString) other) < 0);
         }
 
         return RubyComparable.op_lt(this, other);
     }
 
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql_p(IRubyObject other) {
         if (!(other instanceof RubyString)) return getRuntime().getFalse();
         RubyString otherString = (RubyString)other;
         return value.equal(otherString.value) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_str_upcase
      *
      */
     @JRubyMethod(name = "upcase")
     public RubyString upcase() {
         RubyString str = strDup();
         str.upcase_bang();
         return str;
     }
 
     /** rb_str_upcase_bang
      *
      */
     @JRubyMethod(name = "upcase!")
     public IRubyObject upcase_bang() {
         if (value.realSize == 0) {
             modifyCheck();
             return getRuntime().getNil();
         }
 
         modify();
 
         int s = value.begin;
         int send = s + value.realSize;
         byte []buf = value.bytes;
 
         boolean modify = false;
         while (s < send) {
             int c = buf[s] & 0xff;
             if (ASCII.isLower(c)) {
                 buf[s] = (byte)ASCIIEncoding.asciiToUpper(c);
                 modify = true;
             }
             s++;
         }
 
         if (modify) return this;
         return getRuntime().getNil();        
     }
 
     /** rb_str_downcase
      *
      */
     @JRubyMethod(name = "downcase")
     public RubyString downcase() {
         RubyString str = strDup();
         str.downcase_bang();
         return str;
     }
 
     /** rb_str_downcase_bang
      *
      */
     @JRubyMethod(name = "downcase!")
     public IRubyObject downcase_bang() {
         if (value.realSize == 0) {
             modifyCheck();
             return getRuntime().getNil();
         }
         
         modify();
         
         int s = value.begin;
         int send = s + value.realSize;
         byte []buf = value.bytes;
         
         boolean modify = false;
         while (s < send) {
             int c = buf[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 buf[s] = (byte)ASCIIEncoding.asciiToLower(c);
                 modify = true;
             }
             s++;
         }
         
         if (modify) return this;
         return getRuntime().getNil();
     }
 
     /** rb_str_swapcase
      *
      */
     @JRubyMethod(name = "swapcase")
     public RubyString swapcase() {
         RubyString str = strDup();
         str.swapcase_bang();
         return str;
     }
 
     /** rb_str_swapcase_bang
      *
      */
     @JRubyMethod(name = "swapcase!")
     public IRubyObject swapcase_bang() {
         if (value.realSize == 0) {
             modifyCheck();
             return getRuntime().getNil();        
         }
         
         modify();
         
         int s = value.begin;
         int send = s + value.realSize;
         byte[]buf = value.bytes;
         
         boolean modify = false;
         while (s < send) {
             int c = buf[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 buf[s] = (byte)ASCIIEncoding.asciiToLower(c);
                 modify = true;
             } else if (ASCII.isLower(c)) {
                 buf[s] = (byte)ASCIIEncoding.asciiToUpper(c);
                 modify = true;
             }
             s++;
         }
 
         if (modify) return this;
         return getRuntime().getNil();
     }
 
     /** rb_str_dump
      *
      */
     @JRubyMethod(name = "dump")
     public IRubyObject dump() {
         return inspect();
     }
 
     @JRubyMethod(name = "insert", required = 2)
     public IRubyObject insert(IRubyObject indexArg, IRubyObject stringArg) {
         int index = (int) indexArg.convertToInteger().getLongValue();
         if (index < 0) index += value.length() + 1;
 
         if (index < 0 || index > value.length()) {
             throw getRuntime().newIndexError("index " + index + " out of range");
         }
 
         modify();
         
         ByteList insert = ((RubyString)stringArg.convertToString()).value;
         value.unsafeReplace(index, 0, insert);
         return this;
     }
 
     /** rb_str_inspect
      *
      */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect() {
         final int length = value.length();
         Ruby runtime = getRuntime();
         ByteList sb = new ByteList(length + 2 + length / 100);
 
         sb.append('\"');
 
         // FIXME: This may not be unicode-safe
         for (int i = 0; i < length; i++) {
             int c = value.get(i) & 0xFF;
             if (isAlnum(c)) {
                 sb.append((char)c);
             } else if (runtime.getKCode() == KCode.UTF8 && c == 0xEF) {
                 // don't escape encoded UTF8 characters, leave them as bytes
                 // append byte order mark plus two character bytes
                 sb.append((char)c);
                 sb.append((char)(value.get(++i) & 0xFF));
                 sb.append((char)(value.get(++i) & 0xFF));
             } else if (c == '\"' || c == '\\') {
                 sb.append('\\').append((char)c);
             } else if (c == '#' && isEVStr(i, length)) {
                 sb.append('\\').append((char)c);
             } else if (isPrint(c)) {
                 sb.append((char)c);
             } else if (c == '\n') {
                 sb.append('\\').append('n');
             } else if (c == '\r') {
                 sb.append('\\').append('r');
             } else if (c == '\t') {
                 sb.append('\\').append('t');
             } else if (c == '\f') {
                 sb.append('\\').append('f');
             } else if (c == '\u000B') {
                 sb.append('\\').append('v');
             } else if (c == '\u0007') {
                 sb.append('\\').append('a');
+            } else if (c == '\u0008') {
+                sb.append('\\').append('b');
             } else if (c == '\u001B') {
                 sb.append('\\').append('e');
             } else {
                 sb.append(ByteList.plain(Sprintf.sprintf(runtime,"\\%.3o",c)));
             }
         }
 
         sb.append('\"');
         return getRuntime().newString(sb);
     }
     
     private boolean isEVStr(int i, int length) {
         if (i+1 >= length) return false;
         int c = value.get(i+1) & 0xFF;
         
         return c == '$' || c == '@' || c == '{';
     }
 
     /** rb_str_length
      *
      */
     @JRubyMethod(name = {"length", "size"})
     public RubyFixnum length() {
         return getRuntime().newFixnum(value.length());
     }
 
     /** rb_str_empty
      *
      */
     @JRubyMethod(name = "empty?")
     public RubyBoolean empty_p() {
         return isEmpty() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     private boolean isEmpty() {
         return value.length() == 0;
     }
 
     /** rb_str_append
      *
      */
     public RubyString append(IRubyObject other) {
         infectBy(other);
         return cat(stringValue(other).value);
     }
 
     /** rb_str_concat
      *
      */
     @JRubyMethod(name = {"concat", "<<"}, required = 1)
     public RubyString concat(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long value = ((RubyFixnum) other).getLongValue();
             if (value >= 0 && value < 256) return cat((byte) value);
         }
         return append(other);
     }
 
     /** rb_str_crypt
      *
      */
     @JRubyMethod(name = "crypt", required = 1)
     public RubyString crypt(IRubyObject other) {
         ByteList salt = stringValue(other).getByteList();
         if (salt.realSize < 2) {
             throw getRuntime().newArgumentError("salt too short(need >=2 bytes)");
         }
 
         salt = salt.makeShared(0, 2);
         return RubyString.newStringShared(getRuntime(),getMetaClass(), JavaCrypt.crypt(salt, this.getByteList()));
     }
 
     public static class JavaCrypt {
         private static final int ITERATIONS = 16;
 
         private static final int con_salt[] = {
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
             0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
             0x0A, 0x0B, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A,
             0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12,
             0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A,
             0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x20, 0x21, 0x22,
             0x23, 0x24, 0x25, 0x20, 0x21, 0x22, 0x23, 0x24,
             0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C,
             0x2D, 0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33, 0x34,
             0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C,
             0x3D, 0x3E, 0x3F, 0x00, 0x00, 0x00, 0x00, 0x00,
         };
 
         private static final boolean shifts2[] = {
             false, false, true, true, true, true, true, true,
             false, true,  true, true, true, true, true, false };
 
         private static final int skb[][] = {
             {
                 /* for C bits (numbered as per FIPS 46) 1 2 3 4 5 6 */
                 0x00000000, 0x00000010, 0x20000000, 0x20000010,
                 0x00010000, 0x00010010, 0x20010000, 0x20010010,
                 0x00000800, 0x00000810, 0x20000800, 0x20000810,
                 0x00010800, 0x00010810, 0x20010800, 0x20010810,
                 0x00000020, 0x00000030, 0x20000020, 0x20000030,
                 0x00010020, 0x00010030, 0x20010020, 0x20010030,
                 0x00000820, 0x00000830, 0x20000820, 0x20000830,
                 0x00010820, 0x00010830, 0x20010820, 0x20010830,
                 0x00080000, 0x00080010, 0x20080000, 0x20080010,
                 0x00090000, 0x00090010, 0x20090000, 0x20090010,
                 0x00080800, 0x00080810, 0x20080800, 0x20080810,
                 0x00090800, 0x00090810, 0x20090800, 0x20090810,
                 0x00080020, 0x00080030, 0x20080020, 0x20080030,
                 0x00090020, 0x00090030, 0x20090020, 0x20090030,
                 0x00080820, 0x00080830, 0x20080820, 0x20080830,
                 0x00090820, 0x00090830, 0x20090820, 0x20090830,
             },{
                 /* for C bits (numbered as per FIPS 46) 7 8 10 11 12 13 */
                 0x00000000, 0x02000000, 0x00002000, 0x02002000,
                 0x00200000, 0x02200000, 0x00202000, 0x02202000,
                 0x00000004, 0x02000004, 0x00002004, 0x02002004,
                 0x00200004, 0x02200004, 0x00202004, 0x02202004,
                 0x00000400, 0x02000400, 0x00002400, 0x02002400,
                 0x00200400, 0x02200400, 0x00202400, 0x02202400,
                 0x00000404, 0x02000404, 0x00002404, 0x02002404,
                 0x00200404, 0x02200404, 0x00202404, 0x02202404,
                 0x10000000, 0x12000000, 0x10002000, 0x12002000,
                 0x10200000, 0x12200000, 0x10202000, 0x12202000,
                 0x10000004, 0x12000004, 0x10002004, 0x12002004,
                 0x10200004, 0x12200004, 0x10202004, 0x12202004,
                 0x10000400, 0x12000400, 0x10002400, 0x12002400,
                 0x10200400, 0x12200400, 0x10202400, 0x12202400,
                 0x10000404, 0x12000404, 0x10002404, 0x12002404,
                 0x10200404, 0x12200404, 0x10202404, 0x12202404,
             },{
                 /* for C bits (numbered as per FIPS 46) 14 15 16 17 19 20 */
                 0x00000000, 0x00000001, 0x00040000, 0x00040001,
                 0x01000000, 0x01000001, 0x01040000, 0x01040001,
                 0x00000002, 0x00000003, 0x00040002, 0x00040003,
                 0x01000002, 0x01000003, 0x01040002, 0x01040003,
                 0x00000200, 0x00000201, 0x00040200, 0x00040201,
                 0x01000200, 0x01000201, 0x01040200, 0x01040201,
                 0x00000202, 0x00000203, 0x00040202, 0x00040203,
                 0x01000202, 0x01000203, 0x01040202, 0x01040203,
                 0x08000000, 0x08000001, 0x08040000, 0x08040001,
                 0x09000000, 0x09000001, 0x09040000, 0x09040001,
                 0x08000002, 0x08000003, 0x08040002, 0x08040003,
                 0x09000002, 0x09000003, 0x09040002, 0x09040003,
                 0x08000200, 0x08000201, 0x08040200, 0x08040201,
                 0x09000200, 0x09000201, 0x09040200, 0x09040201,
                 0x08000202, 0x08000203, 0x08040202, 0x08040203,
                 0x09000202, 0x09000203, 0x09040202, 0x09040203,
             },{
                 /* for C bits (numbered as per FIPS 46) 21 23 24 26 27 28 */
                 0x00000000, 0x00100000, 0x00000100, 0x00100100,
                 0x00000008, 0x00100008, 0x00000108, 0x00100108,
                 0x00001000, 0x00101000, 0x00001100, 0x00101100,
                 0x00001008, 0x00101008, 0x00001108, 0x00101108,
                 0x04000000, 0x04100000, 0x04000100, 0x04100100,
                 0x04000008, 0x04100008, 0x04000108, 0x04100108,
                 0x04001000, 0x04101000, 0x04001100, 0x04101100,
                 0x04001008, 0x04101008, 0x04001108, 0x04101108,
                 0x00020000, 0x00120000, 0x00020100, 0x00120100,
                 0x00020008, 0x00120008, 0x00020108, 0x00120108,
                 0x00021000, 0x00121000, 0x00021100, 0x00121100,
                 0x00021008, 0x00121008, 0x00021108, 0x00121108,
                 0x04020000, 0x04120000, 0x04020100, 0x04120100,
                 0x04020008, 0x04120008, 0x04020108, 0x04120108,
                 0x04021000, 0x04121000, 0x04021100, 0x04121100,
                 0x04021008, 0x04121008, 0x04021108, 0x04121108,
             },{
                 /* for D bits (numbered as per FIPS 46) 1 2 3 4 5 6 */
                 0x00000000, 0x10000000, 0x00010000, 0x10010000,
                 0x00000004, 0x10000004, 0x00010004, 0x10010004,
                 0x20000000, 0x30000000, 0x20010000, 0x30010000,
                 0x20000004, 0x30000004, 0x20010004, 0x30010004,
                 0x00100000, 0x10100000, 0x00110000, 0x10110000,
                 0x00100004, 0x10100004, 0x00110004, 0x10110004,
                 0x20100000, 0x30100000, 0x20110000, 0x30110000,
                 0x20100004, 0x30100004, 0x20110004, 0x30110004,
                 0x00001000, 0x10001000, 0x00011000, 0x10011000,
                 0x00001004, 0x10001004, 0x00011004, 0x10011004,
                 0x20001000, 0x30001000, 0x20011000, 0x30011000,
                 0x20001004, 0x30001004, 0x20011004, 0x30011004,
                 0x00101000, 0x10101000, 0x00111000, 0x10111000,
                 0x00101004, 0x10101004, 0x00111004, 0x10111004,
                 0x20101000, 0x30101000, 0x20111000, 0x30111000,
                 0x20101004, 0x30101004, 0x20111004, 0x30111004,
             },{
                 /* for D bits (numbered as per FIPS 46) 8 9 11 12 13 14 */
                 0x00000000, 0x08000000, 0x00000008, 0x08000008,
                 0x00000400, 0x08000400, 0x00000408, 0x08000408,
                 0x00020000, 0x08020000, 0x00020008, 0x08020008,
                 0x00020400, 0x08020400, 0x00020408, 0x08020408,
                 0x00000001, 0x08000001, 0x00000009, 0x08000009,
                 0x00000401, 0x08000401, 0x00000409, 0x08000409,
                 0x00020001, 0x08020001, 0x00020009, 0x08020009,
                 0x00020401, 0x08020401, 0x00020409, 0x08020409,
                 0x02000000, 0x0A000000, 0x02000008, 0x0A000008,
                 0x02000400, 0x0A000400, 0x02000408, 0x0A000408,
                 0x02020000, 0x0A020000, 0x02020008, 0x0A020008,
                 0x02020400, 0x0A020400, 0x02020408, 0x0A020408,
                 0x02000001, 0x0A000001, 0x02000009, 0x0A000009,
                 0x02000401, 0x0A000401, 0x02000409, 0x0A000409,
                 0x02020001, 0x0A020001, 0x02020009, 0x0A020009,
                 0x02020401, 0x0A020401, 0x02020409, 0x0A020409,
             },{
                 /* for D bits (numbered as per FIPS 46) 16 17 18 19 20 21 */
                 0x00000000, 0x00000100, 0x00080000, 0x00080100,
                 0x01000000, 0x01000100, 0x01080000, 0x01080100,
                 0x00000010, 0x00000110, 0x00080010, 0x00080110,
                 0x01000010, 0x01000110, 0x01080010, 0x01080110,
                 0x00200000, 0x00200100, 0x00280000, 0x00280100,
                 0x01200000, 0x01200100, 0x01280000, 0x01280100,
                 0x00200010, 0x00200110, 0x00280010, 0x00280110,
                 0x01200010, 0x01200110, 0x01280010, 0x01280110,
                 0x00000200, 0x00000300, 0x00080200, 0x00080300,
                 0x01000200, 0x01000300, 0x01080200, 0x01080300,
                 0x00000210, 0x00000310, 0x00080210, 0x00080310,
                 0x01000210, 0x01000310, 0x01080210, 0x01080310,
                 0x00200200, 0x00200300, 0x00280200, 0x00280300,
                 0x01200200, 0x01200300, 0x01280200, 0x01280300,
                 0x00200210, 0x00200310, 0x00280210, 0x00280310,
                 0x01200210, 0x01200310, 0x01280210, 0x01280310,
             },{
                 /* for D bits (numbered as per FIPS 46) 22 23 24 25 27 28 */
                 0x00000000, 0x04000000, 0x00040000, 0x04040000,
                 0x00000002, 0x04000002, 0x00040002, 0x04040002,
                 0x00002000, 0x04002000, 0x00042000, 0x04042000,
                 0x00002002, 0x04002002, 0x00042002, 0x04042002,
                 0x00000020, 0x04000020, 0x00040020, 0x04040020,
                 0x00000022, 0x04000022, 0x00040022, 0x04040022,
                 0x00002020, 0x04002020, 0x00042020, 0x04042020,
                 0x00002022, 0x04002022, 0x00042022, 0x04042022,
                 0x00000800, 0x04000800, 0x00040800, 0x04040800,
                 0x00000802, 0x04000802, 0x00040802, 0x04040802,
                 0x00002800, 0x04002800, 0x00042800, 0x04042800,
                 0x00002802, 0x04002802, 0x00042802, 0x04042802,
                 0x00000820, 0x04000820, 0x00040820, 0x04040820,
                 0x00000822, 0x04000822, 0x00040822, 0x04040822,
                 0x00002820, 0x04002820, 0x00042820, 0x04042820,
                 0x00002822, 0x04002822, 0x00042822, 0x04042822,
             }
         };
 
         private static final int SPtrans[][] = {
             {
                 /* nibble 0 */
                 0x00820200, 0x00020000, 0x80800000, 0x80820200,
                 0x00800000, 0x80020200, 0x80020000, 0x80800000,
                 0x80020200, 0x00820200, 0x00820000, 0x80000200,
                 0x80800200, 0x00800000, 0x00000000, 0x80020000,
                 0x00020000, 0x80000000, 0x00800200, 0x00020200,
                 0x80820200, 0x00820000, 0x80000200, 0x00800200,
                 0x80000000, 0x00000200, 0x00020200, 0x80820000,
                 0x00000200, 0x80800200, 0x80820000, 0x00000000,
                 0x00000000, 0x80820200, 0x00800200, 0x80020000,
                 0x00820200, 0x00020000, 0x80000200, 0x00800200,
                 0x80820000, 0x00000200, 0x00020200, 0x80800000,
                 0x80020200, 0x80000000, 0x80800000, 0x00820000,
                 0x80820200, 0x00020200, 0x00820000, 0x80800200,
                 0x00800000, 0x80000200, 0x80020000, 0x00000000,
                 0x00020000, 0x00800000, 0x80800200, 0x00820200,
                 0x80000000, 0x80820000, 0x00000200, 0x80020200,
             },{
                 /* nibble 1 */
                 0x10042004, 0x00000000, 0x00042000, 0x10040000,
                 0x10000004, 0x00002004, 0x10002000, 0x00042000,
                 0x00002000, 0x10040004, 0x00000004, 0x10002000,
                 0x00040004, 0x10042000, 0x10040000, 0x00000004,
                 0x00040000, 0x10002004, 0x10040004, 0x00002000,
                 0x00042004, 0x10000000, 0x00000000, 0x00040004,
                 0x10002004, 0x00042004, 0x10042000, 0x10000004,
                 0x10000000, 0x00040000, 0x00002004, 0x10042004,
                 0x00040004, 0x10042000, 0x10002000, 0x00042004,
                 0x10042004, 0x00040004, 0x10000004, 0x00000000,
                 0x10000000, 0x00002004, 0x00040000, 0x10040004,
                 0x00002000, 0x10000000, 0x00042004, 0x10002004,
                 0x10042000, 0x00002000, 0x00000000, 0x10000004,
                 0x00000004, 0x10042004, 0x00042000, 0x10040000,
                 0x10040004, 0x00040000, 0x00002004, 0x10002000,
                 0x10002004, 0x00000004, 0x10040000, 0x00042000,
             },{
                 /* nibble 2 */
                 0x41000000, 0x01010040, 0x00000040, 0x41000040,
                 0x40010000, 0x01000000, 0x41000040, 0x00010040,
                 0x01000040, 0x00010000, 0x01010000, 0x40000000,
                 0x41010040, 0x40000040, 0x40000000, 0x41010000,
                 0x00000000, 0x40010000, 0x01010040, 0x00000040,
                 0x40000040, 0x41010040, 0x00010000, 0x41000000,
                 0x41010000, 0x01000040, 0x40010040, 0x01010000,
                 0x00010040, 0x00000000, 0x01000000, 0x40010040,
                 0x01010040, 0x00000040, 0x40000000, 0x00010000,
                 0x40000040, 0x40010000, 0x01010000, 0x41000040,
                 0x00000000, 0x01010040, 0x00010040, 0x41010000,
                 0x40010000, 0x01000000, 0x41010040, 0x40000000,
                 0x40010040, 0x41000000, 0x01000000, 0x41010040,
                 0x00010000, 0x01000040, 0x41000040, 0x00010040,
                 0x01000040, 0x00000000, 0x41010000, 0x40000040,
                 0x41000000, 0x40010040, 0x00000040, 0x01010000,
             },{
                 /* nibble 3 */
                 0x00100402, 0x04000400, 0x00000002, 0x04100402,
                 0x00000000, 0x04100000, 0x04000402, 0x00100002,
                 0x04100400, 0x04000002, 0x04000000, 0x00000402,
                 0x04000002, 0x00100402, 0x00100000, 0x04000000,
                 0x04100002, 0x00100400, 0x00000400, 0x00000002,
                 0x00100400, 0x04000402, 0x04100000, 0x00000400,
                 0x00000402, 0x00000000, 0x00100002, 0x04100400,
                 0x04000400, 0x04100002, 0x04100402, 0x00100000,
                 0x04100002, 0x00000402, 0x00100000, 0x04000002,
                 0x00100400, 0x04000400, 0x00000002, 0x04100000,
                 0x04000402, 0x00000000, 0x00000400, 0x00100002,
                 0x00000000, 0x04100002, 0x04100400, 0x00000400,
                 0x04000000, 0x04100402, 0x00100402, 0x00100000,
                 0x04100402, 0x00000002, 0x04000400, 0x00100402,
                 0x00100002, 0x00100400, 0x04100000, 0x04000402,
                 0x00000402, 0x04000000, 0x04000002, 0x04100400,
             },{
                 /* nibble 4 */
                 0x02000000, 0x00004000, 0x00000100, 0x02004108,
                 0x02004008, 0x02000100, 0x00004108, 0x02004000,
                 0x00004000, 0x00000008, 0x02000008, 0x00004100,
                 0x02000108, 0x02004008, 0x02004100, 0x00000000,
                 0x00004100, 0x02000000, 0x00004008, 0x00000108,
                 0x02000100, 0x00004108, 0x00000000, 0x02000008,
                 0x00000008, 0x02000108, 0x02004108, 0x00004008,
                 0x02004000, 0x00000100, 0x00000108, 0x02004100,
                 0x02004100, 0x02000108, 0x00004008, 0x02004000,
                 0x00004000, 0x00000008, 0x02000008, 0x02000100,
                 0x02000000, 0x00004100, 0x02004108, 0x00000000,
                 0x00004108, 0x02000000, 0x00000100, 0x00004008,
                 0x02000108, 0x00000100, 0x00000000, 0x02004108,
                 0x02004008, 0x02004100, 0x00000108, 0x00004000,
                 0x00004100, 0x02004008, 0x02000100, 0x00000108,
                 0x00000008, 0x00004108, 0x02004000, 0x02000008,
             },{
                 /* nibble 5 */
                 0x20000010, 0x00080010, 0x00000000, 0x20080800,
                 0x00080010, 0x00000800, 0x20000810, 0x00080000,
                 0x00000810, 0x20080810, 0x00080800, 0x20000000,
                 0x20000800, 0x20000010, 0x20080000, 0x00080810,
                 0x00080000, 0x20000810, 0x20080010, 0x00000000,
                 0x00000800, 0x00000010, 0x20080800, 0x20080010,
                 0x20080810, 0x20080000, 0x20000000, 0x00000810,
                 0x00000010, 0x00080800, 0x00080810, 0x20000800,
                 0x00000810, 0x20000000, 0x20000800, 0x00080810,
                 0x20080800, 0x00080010, 0x00000000, 0x20000800,
                 0x20000000, 0x00000800, 0x20080010, 0x00080000,
                 0x00080010, 0x20080810, 0x00080800, 0x00000010,
                 0x20080810, 0x00080800, 0x00080000, 0x20000810,
                 0x20000010, 0x20080000, 0x00080810, 0x00000000,
                 0x00000800, 0x20000010, 0x20000810, 0x20080800,
                 0x20080000, 0x00000810, 0x00000010, 0x20080010,
             },{
                 /* nibble 6 */
                 0x00001000, 0x00000080, 0x00400080, 0x00400001,
                 0x00401081, 0x00001001, 0x00001080, 0x00000000,
                 0x00400000, 0x00400081, 0x00000081, 0x00401000,
                 0x00000001, 0x00401080, 0x00401000, 0x00000081,
                 0x00400081, 0x00001000, 0x00001001, 0x00401081,
                 0x00000000, 0x00400080, 0x00400001, 0x00001080,
                 0x00401001, 0x00001081, 0x00401080, 0x00000001,
                 0x00001081, 0x00401001, 0x00000080, 0x00400000,
                 0x00001081, 0x00401000, 0x00401001, 0x00000081,
                 0x00001000, 0x00000080, 0x00400000, 0x00401001,
                 0x00400081, 0x00001081, 0x00001080, 0x00000000,
                 0x00000080, 0x00400001, 0x00000001, 0x00400080,
                 0x00000000, 0x00400081, 0x00400080, 0x00001080,
                 0x00000081, 0x00001000, 0x00401081, 0x00400000,
                 0x00401080, 0x00000001, 0x00001001, 0x00401081,
                 0x00400001, 0x00401080, 0x00401000, 0x00001001,
             },{
                 /* nibble 7 */
                 0x08200020, 0x08208000, 0x00008020, 0x00000000,
                 0x08008000, 0x00200020, 0x08200000, 0x08208020,
                 0x00000020, 0x08000000, 0x00208000, 0x00008020,
                 0x00208020, 0x08008020, 0x08000020, 0x08200000,
                 0x00008000, 0x00208020, 0x00200020, 0x08008000,
                 0x08208020, 0x08000020, 0x00000000, 0x00208000,
                 0x08000000, 0x00200000, 0x08008020, 0x08200020,
                 0x00200000, 0x00008000, 0x08208000, 0x00000020,
                 0x00200000, 0x00008000, 0x08000020, 0x08208020,
                 0x00008020, 0x08000000, 0x00000000, 0x00208000,
                 0x08200020, 0x08008020, 0x08008000, 0x00200020,
                 0x08208000, 0x00000020, 0x00200020, 0x08008000,
                 0x08208020, 0x00200000, 0x08200000, 0x08000020,
                 0x00208000, 0x00008020, 0x08008020, 0x08200000,
                 0x00000020, 0x08208000, 0x00208020, 0x00000000,
                 0x08000000, 0x08200020, 0x00008000, 0x00208020
             }
         };
 
         private static final int cov_2char[] = {
             0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35,
             0x36, 0x37, 0x38, 0x39, 0x41, 0x42, 0x43, 0x44,
             0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C,
             0x4D, 0x4E, 0x4F, 0x50, 0x51, 0x52, 0x53, 0x54,
             0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x61, 0x62,
             0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A,
             0x6B, 0x6C, 0x6D, 0x6E, 0x6F, 0x70, 0x71, 0x72,
             0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A
         };
 
         private static final int byteToUnsigned(byte b) {
             return b & 0xFF;
         }
 
         private static int fourBytesToInt(byte b[], int offset) {
             int value;
             value  =  byteToUnsigned(b[offset++]);
             value |= (byteToUnsigned(b[offset++]) <<  8);
             value |= (byteToUnsigned(b[offset++]) << 16);
             value |= (byteToUnsigned(b[offset++]) << 24);
             return(value);
         }
 
         private static final void intToFourBytes(int iValue, byte b[], int offset) {
             b[offset++] = (byte)((iValue)        & 0xff);
             b[offset++] = (byte)((iValue >>> 8 ) & 0xff);
             b[offset++] = (byte)((iValue >>> 16) & 0xff);
             b[offset++] = (byte)((iValue >>> 24) & 0xff);
         }
 
         private static final void PERM_OP(int a, int b, int n, int m, int results[]) {
             int t;
 
             t = ((a >>> n) ^ b) & m;
             a ^= t << n;
             b ^= t;
 
             results[0] = a;
             results[1] = b;
         }
 
         private static final int HPERM_OP(int a, int n, int m) {
             int t;
 
             t = ((a << (16 - n)) ^ a) & m;
             a = a ^ t ^ (t >>> (16 - n));
 
             return a;
         }
 
         private static int [] des_set_key(byte key[]) {
             int schedule[] = new int[ITERATIONS * 2];
 
             int c = fourBytesToInt(key, 0);
             int d = fourBytesToInt(key, 4);
 
             int results[] = new int[2];
 
             PERM_OP(d, c, 4, 0x0f0f0f0f, results);
             d = results[0]; c = results[1];
 
             c = HPERM_OP(c, -2, 0xcccc0000);
             d = HPERM_OP(d, -2, 0xcccc0000);
 
             PERM_OP(d, c, 1, 0x55555555, results);
             d = results[0]; c = results[1];
 
             PERM_OP(c, d, 8, 0x00ff00ff, results);
             c = results[0]; d = results[1];
 
             PERM_OP(d, c, 1, 0x55555555, results);
             d = results[0]; c = results[1];
 
             d = (((d & 0x000000ff) <<  16) |  (d & 0x0000ff00)     |
                  ((d & 0x00ff0000) >>> 16) | ((c & 0xf0000000) >>> 4));
             c &= 0x0fffffff;
 
             int s, t;
             int j = 0;
 
             for(int i = 0; i < ITERATIONS; i ++) {
                 if(shifts2[i]) {
                     c = (c >>> 2) | (c << 26);
                     d = (d >>> 2) | (d << 26);
                 } else {
                     c = (c >>> 1) | (c << 27);
                     d = (d >>> 1) | (d << 27);
                 }
 
                 c &= 0x0fffffff;
                 d &= 0x0fffffff;
 
                 s = skb[0][ (c       ) & 0x3f                       ]|
                     skb[1][((c >>>  6) & 0x03) | ((c >>>  7) & 0x3c)]|
                     skb[2][((c >>> 13) & 0x0f) | ((c >>> 14) & 0x30)]|
                     skb[3][((c >>> 20) & 0x01) | ((c >>> 21) & 0x06) |
                            ((c >>> 22) & 0x38)];
 
                 t = skb[4][ (d     )  & 0x3f                       ]|
                     skb[5][((d >>> 7) & 0x03) | ((d >>>  8) & 0x3c)]|
                     skb[6][ (d >>>15) & 0x3f                       ]|
                     skb[7][((d >>>21) & 0x0f) | ((d >>> 22) & 0x30)];
 
                 schedule[j++] = ((t <<  16) | (s & 0x0000ffff)) & 0xffffffff;
                 s             = ((s >>> 16) | (t & 0xffff0000));
 
                 s             = (s << 4) | (s >>> 28);
                 schedule[j++] = s & 0xffffffff;
             }
             return(schedule);
         }
 
         private static final int D_ENCRYPT(int L, int R, int S, int E0, int E1, int s[]) {
             int t, u, v;
 
             v = R ^ (R >>> 16);
             u = v & E0;
             v = v & E1;
             u = (u ^ (u << 16)) ^ R ^ s[S];
             t = (v ^ (v << 16)) ^ R ^ s[S + 1];
             t = (t >>> 4) | (t << 28);
 
             L ^= SPtrans[1][(t       ) & 0x3f] |
                 SPtrans[3][(t >>>  8) & 0x3f] |
                 SPtrans[5][(t >>> 16) & 0x3f] |
                 SPtrans[7][(t >>> 24) & 0x3f] |
                 SPtrans[0][(u       ) & 0x3f] |
                 SPtrans[2][(u >>>  8) & 0x3f] |
                 SPtrans[4][(u >>> 16) & 0x3f] |
                 SPtrans[6][(u >>> 24) & 0x3f];
 
             return(L);
         }
 
         private static final int [] body(int schedule[], int Eswap0, int Eswap1) {
             int left = 0;
             int right = 0;
             int t     = 0;
 
             for(int j = 0; j < 25; j ++) {
                 for(int i = 0; i < ITERATIONS * 2; i += 4) {
                     left  = D_ENCRYPT(left,  right, i,     Eswap0, Eswap1, schedule);
                     right = D_ENCRYPT(right, left,  i + 2, Eswap0, Eswap1, schedule);
                 }
                 t     = left;
                 left  = right;
                 right = t;
             }
 
             t = right;
 
             right = (left >>> 1) | (left << 31);
             left  = (t    >>> 1) | (t    << 31);
 
             left  &= 0xffffffff;
             right &= 0xffffffff;
 
             int results[] = new int[2];
 
             PERM_OP(right, left, 1, 0x55555555, results);
             right = results[0]; left = results[1];
 
             PERM_OP(left, right, 8, 0x00ff00ff, results);
             left = results[0]; right = results[1];
 
             PERM_OP(right, left, 2, 0x33333333, results);
             right = results[0]; left = results[1];
 
             PERM_OP(left, right, 16, 0x0000ffff, results);
             left = results[0]; right = results[1];
 
             PERM_OP(right, left, 4, 0x0f0f0f0f, results);
             right = results[0]; left = results[1];
 
             int out[] = new int[2];
 
             out[0] = left; out[1] = right;
 
             return(out);
         }
 
         public static final ByteList crypt(ByteList salt, ByteList original) {
             ByteList buffer = new ByteList(new byte[]{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},false);
 
             byte charZero = salt.bytes[salt.begin];
             byte charOne  = salt.bytes[salt.begin+1];
 
             buffer.set(0,charZero);
             buffer.set(1,charOne);
 
             int Eswap0 = con_salt[(int)(charZero&0xFF)];
             int Eswap1 = con_salt[(int)(charOne&0xFF)] << 4;
 
             byte key[] = new byte[8];
 
             for(int i = 0; i < key.length; i ++) {
                 key[i] = (byte)0;
             }
 
             for(int i = 0; i < key.length && i < original.length(); i ++) {
                 int iChar = (int)(original.bytes[original.begin+i]&0xFF);
 
                 key[i] = (byte)(iChar << 1);
             }
 
             int schedule[] = des_set_key(key);
             int out[]      = body(schedule, Eswap0, Eswap1);
 
             byte b[] = new byte[9];
 
             intToFourBytes(out[0], b, 0);
             intToFourBytes(out[1], b, 4);
             b[8] = 0;
 
             for(int i = 2, y = 0, u = 0x80; i < 13; i ++) {
                 for(int j = 0, c = 0; j < 6; j ++) {
                     c <<= 1;
 
                     if(((int)b[y] & u) != 0)
                         c |= 1;
 
                     u >>>= 1;
 
                     if(u == 0) {
                         y++;
                         u = 0x80;
                     }
                     buffer.set(i, cov_2char[c]);
                 }
             }
             return buffer;
         }
     }
 
     /* RubyString aka rb_string_value */
     public static RubyString stringValue(IRubyObject object) {
         return (RubyString) (object instanceof RubyString ? object :
             object.convertToString());
     }
 
     /** rb_str_sub
      *
      */
     @JRubyMethod(name = "sub", required = 1, optional = 1, frame = true)
     public IRubyObject sub(IRubyObject[] args, Block block) {
         RubyString str = strDup();
         str.sub_bang(args, block);
         return str;
     }
 
     /** rb_str_sub_bang
      *
      */
     @JRubyMethod(name = "sub!", required = 1, optional = 1, frame = true)
     public IRubyObject sub_bang(IRubyObject[] args, Block block) {
         
         RubyString repl = null;
         final boolean iter;
         boolean tainted = false;
         
         if(args.length == 1 && block.isGiven()) {
             iter = true;
             tainted = false;
         } else if(args.length == 2) {
             repl = args[1].convertToString();
             iter = false;
             if (repl.isTaint()) tainted = true;
         } else {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 2)");
         }
 
         RubyRegexp rubyRegex = getPattern(args[0], true);
         Regex regex = rubyRegex.getPattern();
 
         int range = value.begin + value.realSize;
         Matcher matcher = regex.matcher(value.bytes, value.begin, range);
         
         ThreadContext context = getRuntime().getCurrentContext();
         Frame frame = context.getPreviousFrame();
         if (matcher.search(value.begin, range, Option.NONE) >= 0) {
             if (iter) {                
                 byte[]bytes = value.bytes;
                 int size = value.realSize;
                 rubyRegex.updateBackRef(this, frame, matcher).use();
                 if (regex.numberOfCaptures() == 0) {
                     repl = objAsString(block.yield(context, substr(matcher.getBegin(), matcher.getEnd() - matcher.getBegin())));
                 } else {
                     Region region = matcher.getRegion();
                     repl = objAsString(block.yield(context, substr(region.beg[0], region.end[0] - region.beg[0])));
                 }
                 modifyCheck(bytes, size);
                 frozenCheck();
                 rubyRegex.updateBackRef(this, frame, matcher);
             } else {
                 repl = rubyRegex.regsub(repl, this, matcher);
                 rubyRegex.updateBackRef(this, frame, matcher);
             }
 
             final int beg, plen;
             if (regex.numberOfCaptures() == 0) {
                 beg = matcher.getBegin();
                 plen = matcher.getEnd() - beg;
             } else {
                 Region region = matcher.getRegion();
                 beg = region.beg[0];
                 plen = region.end[0] - beg;
             }
 
             ByteList replValue = repl.value;
             if (replValue.realSize > plen) {
                 modify(value.realSize + replValue.realSize - plen);
             } else {
                 modify();
             }
             if (repl.isTaint()) tainted = true;            
 
             if (replValue.realSize != plen) {
                 int src = value.begin + beg + plen;
                 int dst = value.begin + beg + replValue.realSize;
                 int length = value.realSize - beg - plen;
                 System.arraycopy(value.bytes, src, value.bytes, dst, length);
             }
             System.arraycopy(replValue.bytes, replValue.begin, value.bytes, value.begin + beg, replValue.realSize);
             value.realSize += replValue.realSize - plen;
             if (tainted) setTaint(true);
             return this;            
         } else {
             frame.setBackRef(getRuntime().getNil());
             return getRuntime().getNil();
         }
     }
     
     /** rb_str_gsub
      *
      */
     @JRubyMethod(name = "gsub", required = 1, optional = 1, frame = true)
     public IRubyObject gsub(IRubyObject[] args, Block block) {
         return gsub(args, block, false);
     }
 
     /** rb_str_gsub_bang
      *
      */
     @JRubyMethod(name = "gsub!", required = 1, optional = 1, frame = true)
     public IRubyObject gsub_bang(IRubyObject[] args, Block block) {
         return gsub(args, block, true);
     }
 
     private final IRubyObject gsub(IRubyObject[] args, Block block, final boolean bang) {
         IRubyObject repl;
         final boolean iter;
         boolean tainted = false;
         
         if (args.length == 1 && block.isGiven()) {
             iter = true;
             repl = null;
         } else if (args.length == 2) {
             iter = false;
             repl = args[1].convertToString();
             if (repl.isTaint()) tainted = true; 
         } else {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + "for 2)");
         }
         
         RubyRegexp rubyRegex = getPattern(args[0], true);
         Regex regex = rubyRegex.getPattern();
 
         int begin = value.begin;
         int range = begin + value.realSize;
         Matcher matcher = regex.matcher(value.bytes, begin, range);
         
         int beg = matcher.search(begin, range, Option.NONE);
 
         if (beg < 0) return bang ? getRuntime().getNil() : strDup(); /* bang: true, no match, no substitution */
         
         int blen = value.realSize + 30; /* len + margin */
         ByteList dest = new ByteList(blen);
         dest.realSize = blen;
         int buf = 0, bp = 0;
         int cp = value.begin;
         
         ThreadContext context = getRuntime().getCurrentContext();
         Frame frame = context.getPreviousFrame();
         
         tmpLock();
         
         int n = 0;
         int offset = 0;
         RubyString val;
 
         while (beg >= 0) {
             n++;
             final int begz, endz;
             if (iter) {
                 byte[]bytes = value.bytes;
                 int size = value.realSize;
                 rubyRegex.updateBackRef(this, frame, matcher).use();
                 if (regex.numberOfCaptures() == 0) {
                     begz = matcher.getBegin();
                     endz = matcher.getEnd();
                     val = objAsString(block.yield(context, substr(begz, endz - begz)));
                 } else {
                     Region region = matcher.getRegion();
                     begz = region.beg[0];
                     endz = region.end[0];
                     val = objAsString(block.yield(context, substr(begz, endz - begz)));
  
                 }
                 modifyCheck(bytes, size);
                 if (bang) frozenCheck();
             } else {
                 val = rubyRegex.regsub((RubyString)repl, this, matcher);
                 if (regex.numberOfCaptures() == 0) {
                     begz = matcher.getBegin();
                     endz = matcher.getEnd();
                 } else {
                     Region region = matcher.getRegion();
                     begz = region.beg[0];
                     endz = region.end[0];
                 }
             }
             
             if (val.isTaint()) tainted = true;
             ByteList vbuf = val.value;
             int len = (bp - buf) + (beg - offset) + vbuf.realSize + 3;
             if (blen < len) {
                 while (blen < len) blen <<= 1;
                 len = bp - buf;
                 dest.realloc(blen);
                 dest.realSize = blen;
                 bp = buf + len;
             }
             len = beg - offset; /* copy pre-match substr */
             System.arraycopy(value.bytes, cp, dest.bytes, bp, len);
             bp += len;
             System.arraycopy(vbuf.bytes, vbuf.begin, dest.bytes, bp, vbuf.realSize);
             bp += vbuf.realSize;
             offset = endz;
             
             if (begz == endz) {
                 if (value.realSize <= endz) break;
                 len = regex.getEncoding().length(value.bytes[begin + endz]);
                 System.arraycopy(value.bytes, begin + endz, dest.bytes, bp, len);
                 bp += len;
                 offset = endz + len;
             }
             cp = begin + offset;
             if (offset > value.realSize) break;
             beg = matcher.search(cp, range, Option.NONE); 
         }
         
         if (value.realSize > offset) {
             int len = bp - buf;
             if (blen - len < value.realSize - offset) {
                 blen = len + value.realSize - offset;
                 dest.realloc(blen);
                 bp = buf + len;
             }
             System.arraycopy(value.bytes, cp, dest.bytes, bp, value.realSize - offset);
             bp += value.realSize - offset;
         }        
         
         rubyRegex.updateBackRef(this, frame, matcher);
         tmpUnlock(); // MRI doesn't rb_ensure this
 
         dest.realSize = bp - buf;
         if (bang) {
             view(dest);
             if (tainted) setTaint(true);
             return this;
         } else {
             RubyString destStr = new RubyString(getRuntime(), getMetaClass(), dest);
             destStr.infectBy(this);
             if (tainted) destStr.setTaint(true);
             return destStr;
         }
     }
 
     /** rb_str_index_m
      *
      */
     @JRubyMethod(name = "index", required = 1, optional = 1)
     public IRubyObject index(IRubyObject[] args) {
         int pos = Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2 ? RubyNumeric.num2int(args[1]) : 0;  
         IRubyObject sub = args[0];
         
         if (pos < 0) {
             pos += value.realSize;
             if (pos < 0) {
                 if (sub instanceof RubyRegexp) { 
                     getRuntime().getCurrentContext().getPreviousFrame().setBackRef(getRuntime().getNil());
                 }
                 return getRuntime().getNil();
             }
         }
 
         if (sub instanceof RubyRegexp) {
             RubyRegexp regSub = (RubyRegexp)sub;
             
             pos = regSub.adjustStartPos(this, pos, false);
             pos = regSub.search(this, pos, false);
         } else if (sub instanceof RubyFixnum) {
             byte c = (byte)RubyNumeric.fix2int(sub);
             byte[]bytes = value.bytes;
             int end = value.begin + value.realSize;
 
             pos += value.begin; 
             for (;pos<end; pos++) { 
                 if (bytes[pos] == c) return RubyFixnum.newFixnum(getRuntime(), pos - value.begin);
             }
             return getRuntime().getNil();
         } else if (sub instanceof RubyString) {
             pos = strIndex((RubyString)sub, pos);
         } else {
             IRubyObject tmp = sub.checkStringType();
             
             if (tmp.isNil()) throw getRuntime().newTypeError("type mismatch: " + sub.getMetaClass().getName() + " given");
             pos = strIndex((RubyString)tmp, pos);
         }
         
         return pos == -1  ? getRuntime().getNil() : RubyFixnum.newFixnum(getRuntime(), pos);        
     }
     
     private int strIndex(RubyString sub, int offset) {
         if (offset < 0) {
             offset += value.realSize;
             if (offset < 0) return -1;
         }
         
         if (value.realSize - offset < sub.value.realSize) return -1;
         if (sub.value.realSize == 0) return offset;
         return value.indexOf(sub.value, offset);
     }
 
     /** rb_str_rindex_m
      *
      */
     @JRubyMethod(name = "rindex", required = 1, optional = 1)
     public IRubyObject rindex(IRubyObject[] args) {
         int pos;
         final IRubyObject sub;
         
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2) {  
             sub = args[0];
             pos = RubyNumeric.num2int(args[1]);
 
             if (pos < 0) {
                 pos += value.realSize;
                 if (pos < 0) {
                     if (sub instanceof RubyRegexp) { 
                         getRuntime().getCurrentContext().getPreviousFrame().setBackRef(getRuntime().getNil());
                     }
                     return getRuntime().getNil();
                 }
             }            
             if (pos > value.realSize) pos = value.realSize;
         } else {
             sub = args[0];
             pos = value.realSize;
         }
         
         if (sub instanceof RubyRegexp) {
             RubyRegexp regSub = (RubyRegexp)sub;
             if(regSub.length() > 0) {
                 pos = regSub.adjustStartPos(this, pos, true);
                 if (pos == value.realSize) pos--;
                 pos = regSub.search(this, pos, true);
             }
             if (pos >= 0) return RubyFixnum.newFixnum(getRuntime(), pos);
         } else if (sub instanceof RubyString) {
             pos = strRindex((RubyString)sub, pos);
             if (pos >= 0) return RubyFixnum.newFixnum(getRuntime(), pos);
         } else if (sub instanceof RubyFixnum) {
-            byte c = (byte)RubyNumeric.fix2int(sub);
+            int c_int = RubyNumeric.fix2int(sub);
+            if (c_int < 0x00 || c_int > 0xFF) {
+                // out of byte range
+                // there will be no match for sure
+                return getRuntime().getNil();
+            }
+            byte c = (byte)c_int;
 
             byte[]bytes = value.bytes;
             int pbeg = value.begin;
             int p = pbeg + pos;
             
             if (pos == value.realSize) {
                 if (pos == 0) return getRuntime().getNil();
                 --p;
             }
             while (pbeg <= p) {
                 if (bytes[p] == c) return RubyFixnum.newFixnum(getRuntime(), p - value.begin);
                 p--;
             }
             return getRuntime().getNil();
         } else {
             throw getRuntime().newTypeError("type mismatch: " + sub.getMetaClass().getName() + " given");
         }
         
         return getRuntime().getNil();
     }
 
     private int strRindex(RubyString sub, int pos) {
         int subLength = sub.value.realSize;
         
         /* substring longer than string */
         if (value.realSize < subLength) return -1;
         if (value.realSize - pos < subLength) pos = value.realSize - subLength;
 
         return value.lastIndexOf(sub.value, pos);
     }
     
     /* rb_str_substr */
     public IRubyObject substr(int beg, int len) {
         int length = value.length();
         if (len < 0 || beg > length) return getRuntime().getNil();
 
         if (beg < 0) {
             beg += length;
             if (beg < 0) return getRuntime().getNil();
         }
         
         int end = Math.min(length, beg + len);
         return makeShared(beg, end - beg);
     }
 
     /* rb_str_replace */
     public IRubyObject replace(int beg, int len, RubyString replaceWith) {
         if (beg + len >= value.length()) len = value.length() - beg;
 
         modify();
         value.unsafeReplace(beg,len,replaceWith.value);
 
         return infectBy(replaceWith);
     }
 
     /** rb_str_aref, rb_str_aref_m
      *
      */
     @JRubyMethod(name = {"[]", "slice"}, required = 1, optional = 1)
     public IRubyObject op_aref(IRubyObject[] args) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2) {
             if (args[0] instanceof RubyRegexp) {
                 if(((RubyRegexp)args[0]).search(this,0,false) >= 0) {
                     return RubyRegexp.nth_match(RubyNumeric.fix2int(args[1]), getRuntime().getCurrentContext().getCurrentFrame().getBackRef());
                 }
                 return getRuntime().getNil();
             }
             return substr(RubyNumeric.fix2int(args[0]), RubyNumeric.fix2int(args[1]));
         }
 
         if (args[0] instanceof RubyRegexp) {
             if(((RubyRegexp)args[0]).search(this,0,false) >= 0) {
                 return RubyRegexp.nth_match(0, getRuntime().getCurrentContext().getCurrentFrame().getBackRef());
             }
             return getRuntime().getNil();
         } else if (args[0] instanceof RubyString) {
             return value.indexOf(stringValue(args[0]).value) != -1 ?
                 args[0] : getRuntime().getNil();
         } else if (args[0] instanceof RubyRange) {
             long[] begLen = ((RubyRange) args[0]).begLen(value.length(), 0);
             return begLen == null ? getRuntime().getNil() :
                 substr((int) begLen[0], (int) begLen[1]);
         }
         int idx = (int) args[0].convertToInteger().getLongValue();
         
         if (idx < 0) idx += value.length();
         if (idx < 0 || idx >= value.length()) return getRuntime().getNil();
 
         return getRuntime().newFixnum(value.get(idx) & 0xFF);
     }
 
     /**
      * rb_str_subpat_set
      *
      */
     private void subpatSet(RubyRegexp regexp, int nth, IRubyObject repl) {
         RubyMatchData match;
         int start, end, len;        
         if (regexp.search(this, 0, false) < 0) throw getRuntime().newIndexError("regexp not matched");
 
         match = (RubyMatchData)getRuntime().getCurrentContext().getCurrentFrame().getBackRef();
 
         if (match.regs == null) {
             if (nth >= 1) throw getRuntime().newIndexError("index " + nth + " out of regexp");
             if (nth < 0) {
                 if(-nth >= 1) throw getRuntime().newIndexError("index " + nth + " out of regexp");
                 nth += 1;
             }
             start = match.begin;
             if(start == -1) throw getRuntime().newIndexError("regexp group " + nth + " not matched");
             end = match.end;
         } else {
             if(nth >= match.regs.numRegs) throw getRuntime().newIndexError("index " + nth + " out of regexp");
             if(nth < 0) {
                 if(-nth >= match.regs.numRegs) throw getRuntime().newIndexError("index " + nth + " out of regexp");
                 nth += match.regs.numRegs;
             }
             start = match.regs.beg[nth];
             if(start == -1) throw getRuntime().newIndexError("regexp group " + nth + " not matched");
             end = match.regs.end[nth];
         }
         
         len = end - start;
         replace(start, len, stringValue(repl));
     }
 
     /** rb_str_aset, rb_str_aset_m
      *
      */
     @JRubyMethod(name = "[]=", required = 2, optional = 1)
     public IRubyObject op_aset(IRubyObject[] args) {
         int strLen = value.length();
         if (Arity.checkArgumentCount(getRuntime(), args, 2, 3) == 3) {
             if (args[0] instanceof RubyFixnum) {
                 RubyString repl = stringValue(args[2]);
                 int beg = RubyNumeric.fix2int(args[0]);
                 int len = RubyNumeric.fix2int(args[1]);
                 if (len < 0) throw getRuntime().newIndexError("negative length");
                 if (beg < 0) beg += strLen;
 
                 if (beg < 0 || (beg > 0 && beg > strLen)) {
                     throw getRuntime().newIndexError("string index out of bounds");
                 }
                 if (beg + len > strLen) len = strLen - beg;
 
                 replace(beg, len, repl);
                 return repl;
             }
             if (args[0] instanceof RubyRegexp) {
                 RubyString repl = stringValue(args[2]);
                 int nth = RubyNumeric.fix2int(args[1]);
                 subpatSet((RubyRegexp) args[0], nth, repl);
                 return repl;
             }
         }
         if (args[0] instanceof RubyFixnum || args[0].respondsTo("to_int")) { // FIXME: RubyNumeric or RubyInteger instead?
             int idx = 0;
 
             // FIXME: second instanceof check adds overhead?
             if (!(args[0] instanceof RubyFixnum)) {
                 // FIXME: ok to cast?
                 idx = (int)args[0].convertToInteger().getLongValue();
             } else {
                 idx = RubyNumeric.fix2int(args[0]); // num2int?
             }
             
             if (idx < 0) idx += value.length();
 
             if (idx < 0 || idx >= value.length()) {
                 throw getRuntime().newIndexError("string index out of bounds");
             }
             if (args[1] instanceof RubyFixnum) {
                 modify();
                 value.set(idx, (byte) RubyNumeric.fix2int(args[1]));
             } else {
                 replace(idx, 1, stringValue(args[1]));
             }
             return args[1];
         }
         if (args[0] instanceof RubyRegexp) {
             RubyString repl = stringValue(args[1]);
             subpatSet((RubyRegexp) args[0], 0, repl);
             return repl;
         }
         if (args[0] instanceof RubyString) {
             RubyString orig = stringValue(args[0]);
             int beg = value.indexOf(orig.value);
             if (beg != -1) {
                 replace(beg, orig.value.length(), stringValue(args[1]));
             }
             return args[1];
         }
         if (args[0] instanceof RubyRange) {
             long[] idxs = ((RubyRange) args[0]).getBeginLength(value.length(), true, true);
             replace((int) idxs[0], (int) idxs[1], stringValue(args[1]));
             return args[1];
         }
         throw getRuntime().newTypeError("wrong argument type");
     }
 
     /** rb_str_slice_bang
      *
      */
     @JRubyMethod(name = "slice!", required = 1, optional = 1)
     public IRubyObject slice_bang(IRubyObject[] args) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         IRubyObject[] newArgs = new IRubyObject[argc + 1];
         newArgs[0] = args[0];
         if (argc > 1) newArgs[1] = args[1];
 
         newArgs[argc] = newString("");
         IRubyObject result = op_aref(args);
         if (result.isNil()) return result;
 
         op_aset(newArgs);
         return result;
     }
 
     @JRubyMethod(name = {"succ", "next"})
     public IRubyObject succ() {
         return strDup().succ_bang();
     }
 
     @JRubyMethod(name = {"succ!", "next!"})
     public IRubyObject succ_bang() {
         if (value.length() == 0) {
             modifyCheck();
             return this;
         }
 
         modify();
         
         boolean alnumSeen = false;
         int pos = -1;
         int c = 0;
         int n = 0;
         for (int i = value.length() - 1; i >= 0; i--) {
             c = value.get(i) & 0xFF;
             if (isAlnum(c)) {
                 alnumSeen = true;
                 if ((isDigit(c) && c < '9') || (isLower(c) && c < 'z') || (isUpper(c) && c < 'Z')) {
                     value.set(i, (byte)(c + 1));
                     pos = -1;
                     break;
                 }
                 pos = i;
                 n = isDigit(c) ? '1' : (isLower(c) ? 'a' : 'A');
                 value.set(i, (byte)(isDigit(c) ? '0' : (isLower(c) ? 'a' : 'A')));
             }
         }
         if (!alnumSeen) {
             for (int i = value.length() - 1; i >= 0; i--) {
                 c = value.get(i) & 0xFF;
                 if (c < 0xff) {
                     value.set(i, (byte)(c + 1));
                     pos = -1;
                     break;
                 }
                 pos = i;
                 n = '\u0001';
                 value.set(i, 0);
             }
         }
         if (pos > -1) {
             // This represents left most digit in a set of incremented
             // values?  Therefore leftmost numeric must be '1' and not '0'
             // 999 -> 1000, not 999 -> 0000.  whereas chars should be
             // zzz -> aaaa and non-alnum byte values should be "\377" -> "\001\000"
             value.prepend((byte) n);
         }
         return this;
     }
 
     /** rb_str_upto_m
      *
      */
     @JRubyMethod(name = "upto", required = 1, frame = true)
     public IRubyObject upto(IRubyObject str, Block block) {
         return upto(str, false, block);
     }
 
     /* rb_str_upto */
     public IRubyObject upto(IRubyObject str, boolean excl, Block block) {
         // alias 'this' to 'beg' for ease of comparison with MRI
         RubyString beg = this;
         RubyString end = stringValue(str);
 
         int n = beg.op_cmp(end);
         if (n > 0 || (excl && n == 0)) return beg;
 
         RubyString afterEnd = stringValue(end.succ());
         RubyString current = beg;
 
         ThreadContext context = getRuntime().getCurrentContext();
         while (!current.equals(afterEnd)) {
             block.yield(context, current);
             if (!excl && current.equals(end)) break;
 
             current = (RubyString) current.succ();
             
             if (excl && current.equals(end)) break;
 
             if (current.length().getLongValue() > end.length().getLongValue()) break;
         }
 
         return beg;
 
     }
 
     /** rb_str_include
      *
      */
     @JRubyMethod(name = "include?", required = 1)
     public RubyBoolean include_p(IRubyObject obj) {
         if (obj instanceof RubyFixnum) {
             int c = RubyNumeric.fix2int(obj);
             for (int i = 0; i < value.length(); i++) {
                 if (value.get(i) == (byte)c) {
                     return getRuntime().getTrue();
                 }
             }
             return getRuntime().getFalse();
         }
         ByteList str = stringValue(obj).value;
         return getRuntime().newBoolean(value.indexOf(str) != -1);
     }
 
     /** rb_str_to_i
      *
      */
     @JRubyMethod(name = "to_i", optional = 1)
     public IRubyObject to_i(IRubyObject[] args) {
         long base = Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 0 ? 10 : args[0].convertToInteger().getLongValue();
         return RubyNumeric.str2inum(getRuntime(), this, (int) base);
     }
 
     /** rb_str_oct
      *
      */
     @JRubyMethod(name = "oct")
     public IRubyObject oct() {
         if (isEmpty()) {
             return getRuntime().newFixnum(0);
         }
 
         int base = 8;
 
         int ix = value.begin;
 
         while(ix < value.begin+value.realSize && ASCII.isSpace(value.bytes[ix] & 0xff)) {
             ix++;
         }
 
         int pos = (value.bytes[ix] == '-' || value.bytes[ix] == '+') ? ix+1 : ix;
         if((pos+1) < value.begin+value.realSize && value.bytes[pos] == '0') {
             if(value.bytes[pos+1] == 'x' || value.bytes[pos+1] == 'X') {
                 base = 16;
             } else if(value.bytes[pos+1] == 'b' || value.bytes[pos+1] == 'B') {
                 base = 2;
             }
         }
         return RubyNumeric.str2inum(getRuntime(), this, base);
     }
 
     /** rb_str_hex
      *
      */
     @JRubyMethod(name = "hex")
     public IRubyObject hex() {
         return RubyNumeric.str2inum(getRuntime(), this, 16);
     }
 
     /** rb_str_to_f
      *
      */
     @JRubyMethod(name = "to_f")
     public IRubyObject to_f() {
         return RubyNumeric.str2fnum(getRuntime(), this);
     }
 
     /** rb_str_split_m
      *
      */
     @JRubyMethod(name = "split", optional = 2)
     public RubyArray split(IRubyObject[] args) {
         final int i, lim;
         final boolean limit;
 
         if (Arity.checkArgumentCount(getRuntime(), args, 0, 2) == 2) {
             lim = RubyNumeric.fix2int(args[1]);
             if (lim <= 0) {
                 limit = false;
             } else {
                 if (lim == 1) return value.realSize == 0 ? getRuntime().newArray() : getRuntime().newArray(this);
                 limit = true;
             }
             i = 1;
         } else {
             i = 0;
             lim = 0;
             limit = false;
         }
         
         IRubyObject spat = (args.length == 0 || args[0].isNil()) ? null : args[0];
 
         final RubyArray result;
         if (spat == null && (spat = getRuntime().getGlobalVariables().get("$;")).isNil()) {
             result = awkSplit(limit, lim, i);
         } else {
             if (spat instanceof RubyString && ((RubyString)spat).value.realSize == 1) {
                 RubyString strSpat = (RubyString)spat;
                 if (strSpat.value.bytes[strSpat.value.begin] == (byte)' ') {
                     result = awkSplit(limit, lim, i);
                 } else {
                     result = split(spat, limit, lim, i);
                 }
             } else {
                 result = split(spat, limit, lim, i);
             }
         }
 
         if (!limit && lim == 0) {
             while (result.size() > 0 && ((RubyString)result.eltInternal(result.size() - 1)).value.realSize == 0)
                 result.pop();
         }
 
         return result;
     }
     
     private RubyArray split(IRubyObject pat, boolean limit, int lim, int i) {
         Ruby runtime = getRuntime();
         
         final Regex regex = getPattern(pat, true).getPattern();
         int beg, end, start;
         
         int begin = value.begin;
         start = begin;
         beg = 0;
         
         int range = value.begin + value.realSize;
         final Matcher matcher = regex.matcher(value.bytes, value.begin, range);
         
         boolean lastNull = false;
         RubyArray result = runtime.newArray();
         if (regex.numberOfCaptures() == 0) { // shorter path, no captures defined, no region will be returned 
             while ((end = matcher.search(start, range, Option.NONE)) >= 0) {
                 if (start == end + begin && matcher.getBegin() == matcher.getEnd()) {
                     if (value.realSize == 0) {
                         result.append(newEmptyString(runtime, getMetaClass()));
                         break;
                     } else if (lastNull) {
                         result.append(substr(beg, regex.getEncoding().length(value.bytes[begin + beg])));
                         beg = start - begin;
                     } else {
                         if (start == range) {
                             start++;
                         } else {
                             start += regex.getEncoding().length(value.bytes[start]);
                         }
                         lastNull = true;
                         continue;
                     }
                 } else {
                     result.append(substr(beg, end - beg));
                     beg = matcher.getEnd();
                     start = begin + matcher.getEnd();
                 }
                 lastNull = false;
                 if (limit && lim <= ++i) break;
             }
         } else {
             while ((end = matcher.search(start, range, Option.NONE)) >= 0) {
                 final Region region = matcher.getRegion();
                 if (start == end + begin && region.beg[0] == region.end[0]) {
                     if (value.realSize == 0) {                        
                         result.append(newEmptyString(runtime, getMetaClass()));
                         break;
                     } else if (lastNull) {
                         result.append(substr(beg, regex.getEncoding().length(value.bytes[begin + beg])));
                         beg = start - begin;
                     } else {
                         if (start == range) {
                             start++;
                         } else {
                             start += regex.getEncoding().length(value.bytes[start]);
                         }
                         lastNull = true;
                         continue;
                     }                    
                 } else {
                     result.append(substr(beg, end - beg));
                     beg = start = region.end[0];
                     start += begin;
                 }
                 lastNull = false;
                 
                 for (int idx=1; idx<region.numRegs; idx++) {
                     if (region.beg[idx] == -1) continue;
                     if (region.beg[idx] == region.end[idx]) {
                         result.append(newEmptyString(runtime, getMetaClass()));
                     } else {
                         result.append(substr(region.beg[idx], region.end[idx] - region.beg[idx]));
                     }
                 }
                 if (limit && lim <= ++i) break;
             }
         }
         
         // only this case affects backrefs 
         runtime.getCurrentContext().getCurrentFrame().setBackRef(runtime.getNil());
         
         if (value.realSize > 0 && (limit || value.realSize > beg || lim < 0)) {
             if (value.realSize == beg) {
                 result.append(newEmptyString(runtime, getMetaClass()));
             } else {
                 result.append(substr(beg, value.realSize - beg));
             }
         }
         
         return result;
     }
     
     private RubyArray awkSplit(boolean limit, int lim, int i) {
         RubyArray result = getRuntime().newArray();
         
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
                     result.append(makeShared(beg, end - beg));
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
                 result.append(newEmptyString(getRuntime(), getMetaClass()));
             } else {
                 result.append(makeShared(beg, value.realSize - beg));
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
         
         return ((RubyString)obj).getCachedPattern(quote);
     }
 
     /** rb_reg_regcomp
      * 
      */
     private final RubyRegexp getCachedPattern(boolean quote) {
         final HashMap<ByteList, RubyRegexp> cache = patternCache.get();
         RubyRegexp regexp = cache.get(value);
         if (regexp == null || regexp.getKCode() != getRuntime().getKCode()) {
             RubyString str = quote ? RubyRegexp.quote(this, (KCode)null) : this;
             regexp = RubyRegexp.newRegexp(getRuntime(), str.value, 0);
             cache.put(value, regexp);
         }
         return regexp;
     }
     
     // In future we should store joni Regexes (cross runtime cache)
     // for 1.9 cache, whole RubyString should be stored so the entry contains encoding information as well 
     private static final ThreadLocal<HashMap<ByteList, RubyRegexp>> patternCache = new ThreadLocal<HashMap<ByteList, RubyRegexp>>() {
         protected HashMap<ByteList, RubyRegexp> initialValue() {
             return new HashMap<ByteList, RubyRegexp>(5);
         }
     };
     
     /** rb_str_scan
      *
      */
     @JRubyMethod(name = "scan", required = 1, frame = true)
     public IRubyObject scan(IRubyObject arg, Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         Frame frame = context.getPreviousFrame();
         
         
         final RubyRegexp rubyRegex = getPattern(arg, true);
         final Regex regex = rubyRegex.getPattern();
         
         int range = value.begin + value.realSize;
         final Matcher matcher = regex.matcher(value.bytes, value.begin, range);
         matcher.value = 0; // implicit start argument to scanOnce(NG)
         
         IRubyObject result;
         if (!block.isGiven()) {
             RubyArray ary = runtime.newArray();
             
             if (regex.numberOfCaptures() == 0) {
                 while ((result = scanOnceNG(regex, matcher, range)) != null) ary.append(result);
             } else {
                 while ((result = scanOnce(regex, matcher, range)) != null) ary.append(result);
             }
             rubyRegex.updateBackRef(this, frame, matcher);
             return ary;
         } else {
             byte[]bytes = value.bytes;
             int size = value.realSize;
             
             if (regex.numberOfCaptures() == 0) {
                 while ((result = scanOnceNG(regex, matcher, range)) != null) {
                     rubyRegex.updateBackRef(this, frame, matcher).use();
                     block.yield(context, result);
                     modifyCheck(bytes, size);
                 }
             } else {
                 while ((result = scanOnce(regex, matcher, range)) != null) {
                     rubyRegex.updateBackRef(this, frame, matcher).use();
                     block.yield(context, result);
                     modifyCheck(bytes, size);
                 }
             }
             rubyRegex.updateBackRef(this, frame, matcher);
             return this;
         }
     }
 
     /**
      * rb_enc_check
      */
     @SuppressWarnings("unused")
     private Encoding encodingCheck(RubyRegexp pattern) {
         // For 1.9 compatibility, should check encoding compat between string and pattern
         return pattern.getKCode().getEncoding();
     }
     
     // no group version
     private IRubyObject scanOnceNG(Regex regex, Matcher matcher, int range) {    
         if (matcher.search(matcher.value + value.begin, range, Option.NONE) >= 0) {
             int end = matcher.getEnd();
             if (matcher.getBegin() == end) {
                 if (value.realSize > end) {
                     matcher.value = end + regex.getEncoding().length(value.bytes[value.begin + end]);
                 } else {
                     matcher.value = end + 1;
                 }
             } else {
                 matcher.value = end;
             }
             return substr(matcher.getBegin(), end - matcher.getBegin());
         }
         return null;
     }
     
     // group version
     private IRubyObject scanOnce(Regex regex, Matcher matcher, int range) {    
         if (matcher.search(matcher.value + value.begin, range, Option.NONE) >= 0) {
             Region region = matcher.getRegion();
             int end = region.end[0];
             if (region.beg[0] == end) {
                 if (value.realSize > end) {
                     matcher.value = end + regex.getEncoding().length(value.bytes[value.begin + end]);
                 } else {
                     matcher.value = end + 1;
                 }
             } else {
                 matcher.value = end;
             }
             
             RubyArray result = getRuntime().newArray(region.numRegs);
             for (int i=1; i<region.numRegs; i++) {
                 int beg = region.beg[i]; 
                 if (beg == -1) {
                     result.append(getRuntime().getNil());
                 } else {
                     result.append(substr(beg, region.end[i] - beg));
                 }
             }
             return result;
         }
         return null;
     }    
 
     private static final ByteList SPACE_BYTELIST = new ByteList(ByteList.plain(" "));
     
     private final IRubyObject justify(IRubyObject[]args, char jflag) {
         Ruby runtime = getRuntime();        
         Arity.checkArgumentCount(runtime, args, 1, 2);
         
         int width = RubyFixnum.num2int(args[0]);
         
         int f, flen = 0;
         byte[]fbuf;
         
         IRubyObject pad;
 
         if (args.length == 2) {
             pad = args[1].convertToString();
             ByteList fList = ((RubyString)pad).value;
             f = fList.begin;
             flen = fList.realSize;
 
             if (flen == 0) throw getRuntime().newArgumentError("zero width padding");
             
             fbuf = fList.bytes;
         } else {
             f = SPACE_BYTELIST.begin;
             flen = SPACE_BYTELIST.realSize;
             fbuf = SPACE_BYTELIST.bytes;
             pad = runtime.getNil();
         }
         
         if (width < 0 || value.realSize >= width) return strDup();
         
         ByteList res = new ByteList(width);
         res.realSize = width;
         
         int p = res.begin;
         int pend;
         byte[] pbuf = res.bytes;
         
         if (jflag != 'l') {
             int n = width - value.realSize;
             pend = p + ((jflag == 'r') ? n : n / 2);
             if (flen <= 1) {
                 while (p < pend) pbuf[p++] = fbuf[f];
             } else {
                 int q = f;
                 while (p + flen <= pend) {
                     System.arraycopy(fbuf, f, pbuf, p, flen);
                     p += flen;
                 }
                 while (p < pend) pbuf[p++] = fbuf[q++];
             }
         }
         
         System.arraycopy(value.bytes, value.begin, pbuf, p, value.realSize);
         
         if (jflag != 'r') {
             p += value.realSize;
             pend = res.begin + width;
             if (flen <= 1) {
                 while (p < pend) pbuf[p++] = fbuf[f];
             } else {
                 while (p + flen <= pend) {
                     System.arraycopy(fbuf, f, pbuf, p, flen);
                     p += flen;
                 }
                 while (p < pend) pbuf[p++] = fbuf[f++];
             }
             
         }
         
         RubyString resStr = new RubyString(runtime, getMetaClass(), res);
         resStr.infectBy(this);
         if (flen > 0) resStr.infectBy(pad);
         return resStr;
         
     }
 
     /** rb_str_ljust
      *
      */
     @JRubyMethod(name = "ljust", required = 1, optional = 1)
     public IRubyObject ljust(IRubyObject [] args) {
         return justify(args, 'l');
     }
 
     /** rb_str_rjust
      *
      */
     @JRubyMethod(name = "rjust", required = 1, optional = 1)
     public IRubyObject rjust(IRubyObject [] args) {
         return justify(args, 'r');
     }
 
     @JRubyMethod(name = "center", required = 1, optional = 1)
     public IRubyObject center(IRubyObject[] args) {
         return justify(args, 'c');
     }
 
     @JRubyMethod(name = "chop")
     public IRubyObject chop() {
         RubyString str = strDup();
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
 
     /** rb_str_chop
      * 
      */
     @JRubyMethod(name = "chomp", optional = 1)
     public RubyString chomp(IRubyObject[] args) {
         RubyString str = strDup();
         str.chomp_bang(args);
         return str;
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
     @JRubyMethod(name = "chomp!", optional = 1)
     public IRubyObject chomp_bang(IRubyObject[] args) {
         IRubyObject rsObj;
 
         if (Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 0) {
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
         } else {
             rsObj = args[0];
         }
 
         if (rsObj.isNil()) return getRuntime().getNil();
 
         RubyString rs = rsObj.convertToString();
         int len = value.realSize;
         int begin = value.begin;
         if (len == 0) return getRuntime().getNil();
         byte[]buff = value.bytes;
         int rslen = rs.value.realSize;
 
         if (rslen == 0) {
             while (len > 0 && buff[begin + len - 1] == (byte)'\n') {
                 len--;
                 if (len > 0 && buff[begin + len - 1] == (byte)'\r') len--;
             }
             if (len < value.realSize) {
                 view(0, len);
                 return this;
             }
             return getRuntime().getNil();
         }
 
         if (rslen > len) return getRuntime().getNil();
         byte newline = rs.value.bytes[rslen - 1];
 
         if (rslen == 1 && newline == (byte)'\n') {
             buff = value.bytes;
             int realSize = value.realSize;
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
 
         if (buff[begin + len - 1] == newline && rslen <= 1 || value.endsWith(rs.value)) {
             view(0, value.realSize - rslen);
             return this;            
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_str_lstrip
      * 
      */
     @JRubyMethod(name = "lstrip")
     public IRubyObject lstrip() {
         RubyString str = strDup();
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
     @JRubyMethod(name = "rstrip")
     public IRubyObject rstrip() {
         RubyString str = strDup();
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
     @JRubyMethod(name = "strip")
     public IRubyObject strip() {
         RubyString str = strDup();
         str.strip_bang();
         return str;
         }
 
     /** rb_str_strip_bang
      */
     @JRubyMethod(name = "strip!")
     public IRubyObject strip_bang() {
         IRubyObject l = lstrip_bang();
         IRubyObject r = rstrip_bang();
 
diff --git a/test/externals/bfts/test_string.rb b/test/externals/bfts/test_string.rb
index 2c5a78de96..f4ba9cb58d 100644
--- a/test/externals/bfts/test_string.rb
+++ b/test/externals/bfts/test_string.rb
@@ -1,1644 +1,1644 @@
 require 'test/unit'
 require 'rubicon_testcase'
 
 class TestStringSubclass < String; end # for test_to_s/test_to_str
 
 # Helper class to test String#=~.
 #
 class MatchDefiner
   def initialize(result)
     @result = result
   end
 
   def =~(other)
     [other, @result]
   end
 end
 
 class TestString < RubiconTestCase
 
   def initialize(*args)
     @cls = String
 
     begin
       S("Foo")[/./, 1]
       @aref_re_nth = true
     rescue
       @aref_re_nth = false
     end
     begin
       S("Foo")[/Bar/] = S("")
       @aref_re_silent = true
     rescue IndexError
       @aref_re_silent = false
     end
     begin
       S("Foo").slice!(4)
       @aref_slicebang_silent = true
     rescue
       @aref_slicebang_silent = false
     end
     super
   end
 
   def S(str)
     @cls.new(str)
   end
     
   def casetest(a, b, rev=false) # TODO: rename
     case a
       when b
         assert(!rev)
       else
         assert(rev)
     end
   end
 
   def test_capitalize
     assert_equal(S("Hello"),  S("hello").capitalize)
     assert_equal(S("Hello"),  S("hELLO").capitalize)
     assert_equal(S("Hello"),  S("Hello").capitalize)
     assert_equal(S("123abc"), S("123ABC").capitalize)
   end
 
   def test_capitalize_bang
     a = S("hello")
     assert_equal(S("Hello"), a.capitalize!)
     assert_equal(S("Hello"), a)
 
     a = S("Hello")
     assert_nil a.capitalize!
     assert_equal(S("Hello"), a)
 
     a = S("hELLO")
     assert_equal S("Hello"), a.capitalize!
     assert_equal S("Hello"), a
 
     a = S("123ABC")
     assert_equal S("123abc"), a.capitalize!
     assert_equal S("123abc"), a
   end
 
   def test_capitalize_bang_multibyte
     # TODO: flunk "No capitalize! multibyte tests yet"
   end
 
   def test_casecmp
     # 0
     assert_equal(0, S("123abc").casecmp(S("123ABC")))
     assert_equal(0, S("123AbC").casecmp(S("123aBc")))
     assert_equal(0, S("123ABC").casecmp(S("123ABC")))
     # 1
     assert_equal(1, S("1X3ABC").casecmp(S("123ABC")))
     assert_equal(1, S("123AXC").casecmp(S("123ABC")))
     assert_equal(1, S("123ABX").casecmp(S("123ABC")))
     assert_equal(1, S("123ABCX").casecmp(S("123ABC")))
     # -1
     assert_equal(-1, S("1#3ABC").casecmp(S("123ABC")))
     assert_equal(-1, S("123A#C").casecmp(S("123ABC")))
     assert_equal(-1, S("123AB#").casecmp(S("123ABC")))
     assert_equal(-1, S("123AB").casecmp(S("123ABC")))
 
     assert_raises TypeError do
       'foo'.casecmp Object.new
     end
   end
 
   def test_center
     s = S("")
     assert_not_equal s.object_id, s.center(0).object_id
 
     assert_equal S(""),         S("") .center(0)
     assert_equal S("@"),        S("@").center(0)
 
     assert_equal S(" "),        S("") .center(1)
     assert_equal S("@"),        S("@").center(1)
     assert_equal S("@ "),       S("@").center(2)
     assert_equal S(" @ "),      S("@").center(3)
     assert_equal S(" @  "),     S("@").center(4)
     assert_equal S("  @  "),    S("@").center(5)
 
     assert_equal S(" @@  "),    S("@@").center(5)
 
     assert_equal S(""),         S("") .center(0, 'X')
     assert_equal S("@"),        S("@").center(0, 'X')
     assert_equal S("X"),        S("") .center(1, 'X')
     assert_equal S("@"),        S("@").center(1, 'X')
     assert_equal S("@X"),       S("@").center(2, 'X')
     assert_equal S("X@X"),      S("@").center(3, 'X')
     assert_equal S("X@XX"),     S("@").center(4, 'X')
     assert_equal S("XX@XX"),    S("@").center(5, 'X')
 
     assert_equal S("X@XY"),     S("@").center(4, 'XY')
     assert_equal S("XY@XY"),    S("@").center(5, 'XY')
     assert_equal S("XY@XYX"),   S("@").center(6, 'XY')
     assert_equal S("XYX@XYX"),  S("@").center(7, 'XY')
 
     assert_raises ArgumentError, "Zero width padding not allowed" do
       S("").center 0, S("")
     end
   end
 
   def test_chomp
     assert_equal(S("hello"), S("hello").chomp("\n"))
     assert_equal(S("hello"), S("hello\n").chomp("\n"))
 
     $/ = "\n"
 
     assert_equal(S("hello"), S("hello").chomp)
     assert_equal(S("hello"), S("hello\n").chomp)
 
     $/ = "!"
     assert_equal(S("hello"), S("hello").chomp)
     assert_equal(S("hello"), S("hello!").chomp)
     $/ = "\n"
   end
 
   def test_chomp_bang
     a = S("")
     a.chomp!
     assert_equal('', a)
 
     a = S("hello")
     a.chomp!(S("\n"))
 
     assert_equal(S("hello"), a)
     assert_equal(nil, a.chomp!(S("\n")))
 
     a = S("hello\n")
     a.chomp!(S("\n"))
     assert_equal(S("hello"), a)
 
     $/ = "\n"
     a = S("hello")
     a.chomp!
     assert_equal(S("hello"), a)
 
     a = S("hello\n")
     a.chomp!
     assert_equal(S("hello"), a)
 
     $/ = "!"
     a = S("hello")
     a.chomp!
     assert_equal(S("hello"), a)
 
     a="hello!"
     a.chomp!
     assert_equal(S("hello"), a)
 
     $/ = "\n"
 
     a = S("hello\n")
     b = a.dup
     assert_equal(S("hello"), a.chomp!)
     assert_equal(S("hello\n"), b)
   end
 
   def test_chop
     assert_equal(S(""),        S("").chop)
     assert_equal(S(""),        S("h").chop)
     assert_equal(S("hell"),    S("hello").chop)
     assert_equal(S("hello"),   S("hello\r\n").chop)
     assert_equal(S("hello\n"), S("hello\n\r").chop)
     assert_equal(S(""),        S("\r\n").chop)
   end
 
   def test_chop_bang
     a = S("").chop!
     assert_nil(a)
 
     a = S("hello").chop!
     assert_equal(S("hell"), a)
 
     a = S("hello\r\n").chop!
     assert_equal(S("hello"), a)
 
     a = S("hello\n\r").chop!
     assert_equal(S("hello\n"), a)
 
     a = S("\r\n").chop!
     assert_equal(S(""), a)
   end
 
   def test_class_new
     assert_equal("RUBY", S("RUBY"))
   end
 
   def test_concat
     assert_equal(S("world!"), S("world").concat(33))
     assert_equal(S("world!"), S("world").concat(S('!')))
   end
 
   def test_count
     a = S("hello world")
     assert_equal(5, a.count(S("lo")))
     assert_equal(0, a.count(S("lo"), S("h")))
     assert_equal(2, a.count(S("lo"), S("o")))
     assert_equal(8, a.count(S("^l")))
     assert_equal(4, a.count(S("hello"), S("^l")))
     assert_equal(4, a.count(S("ej-m")))
     assert_equal(2, a.count(S("aeiou"), S("^e")))
   end
 
   def test_crypt
     assert_equal(S('aaGUC/JkO9/Sc'), S("mypassword").crypt(S("aa")))
     assert(S('aaGUC/JkO9/Sc') != S("mypassword").crypt(S("ab")))
 
     # "salt" should be at least 2 characters
     assert_raise(ArgumentError) { S("mypassword").crypt("a")}
   end
 
   def test_delete
     a = S("hello")
     assert_equal(S("heo"),   a.delete(S("l"), S("lo")))
     assert_equal(S("hello"), a.delete(S("lo"), S("h")))
     assert_equal(S("he"),    a.delete(S("lo")))
     assert_equal(S("hell"),  a.delete(S("aeiou"), S("^e")))
     assert_equal(S("ho"),    a.delete(S("ej-m")))
   end
 
   def test_delete_bang
     a = S("hello")
     a.delete!(S("l"), S("lo"))
     assert_equal(S("heo"), a)
 
     a = S("hello")
     a.delete!(S("lo"))
     assert_equal(S("he"), a)
 
     a = S("hello")
     a.delete!(S("aeiou"), S("^e"))
     assert_equal(S("hell"), a)
 
     a = S("hello")
     a.delete!(S("ej-m"))
     assert_equal(S("ho"), a)
 
     a = S("hello")
     assert_nil(a.delete!(S("z")))
 
     a = S("hello")
     b = a.dup
     a.delete!(S("lo"))
     assert_equal(S("he"), a)
     assert_equal(S("hello"), b)
   end
 
   def test_downcase
     assert_equal(S("hello"), S("helLO").downcase)
     assert_equal(S("hello"), S("hello").downcase)
     assert_equal(S("hello"), S("HELLO").downcase)
     assert_equal(S("abc hello 123"), S("abc HELLO 123").downcase)
   end
 
   def test_downcase_bang
     a = S("helLO")
     assert_equal(S("hello"), a.downcase!)
     assert_equal(S("hello"), a)
 
     a = S("hello")
     assert_nil(a.downcase!)
     assert_equal(S("hello"), a)
   end
 
   def test_downcase_bang_multibyte
     # TODO: flunk "No downcase! multibyte tests yet"
   end
 
   def test_dump
     a= S("Test") << 1 << 2 << 3 << 9 << 13 << 10
     assert_equal(S('"Test\\001\\002\\003\\t\\r\\n"'), a.dump)
   end
 
   def test_each
     $/ = "\n"
     res=[]
     S("hello\nworld").each {|x| res << x}
     assert_equal(S("hello\n"), res[0])
     assert_equal(S("world"),   res[1])
 
     res=[]
     S("hello\n\n\nworld").each(S('')) {|x| res << x}
     assert_equal(S("hello\n\n\n"), res[0])
     assert_equal(S("world"),       res[1])
 
     $/ = "!"
     res=[]
     S("hello!world").each {|x| res << x}
     assert_equal(S("hello!"), res[0])
     assert_equal(S("world"),  res[1])
 
     $/ = "\n"
   end
 
   def test_each_byte
     res = []
     S("ABC").each_byte {|x| res << x }
     assert_equal(65, res[0])
     assert_equal(66, res[1])
     assert_equal(67, res[2])
   end
 
   def test_each_line
     $/ = "\n"
     res=[]
     S("hello\nworld").each {|x| res << x}
     assert_equal(S("hello\n"), res[0])
     assert_equal(S("world"),   res[1])
 
     res=[]
     S("hello\n\n\nworld").each(S('')) {|x| res << x}
     assert_equal(S("hello\n\n\n"), res[0])
     assert_equal(S("world"),       res[1])
 
     $/ = "!"
     res=[]
     S("hello!world").each {|x| res << x}
     assert_equal(S("hello!"), res[0])
     assert_equal(S("world"),  res[1])
 
     $/ = "\n"
   end
 
   def test_empty_eh
     assert(S("").empty?)
     assert(!S("not").empty?)
   end
 
   def test_eql_eh
     a = S("hello")
     assert a.eql?(S("hello"))
     assert a.eql?(a)
   end
 
   def test_equals2
     assert_equal(false, S("foo") == :foo)
     assert_equal(false, S("foo") == :foo)
 
     assert(S("abcdef") == S("abcdef"))
 
     assert(S("CAT") != S('cat'))
     assert(S("CaT") != S('cAt'))
   end
 
   def test_equals3
     assert_equal(false, S("foo") === :foo)
     casetest(S("abcdef"), S("abcdef"))
     casetest(S("CAT"), S('cat'), true) # Reverse the test - we don't want to
     casetest(S("CaT"), S('cAt'), true) # find these in the case.
   end
 
   def test_equalstilde
     # str =~ str
     assert_raises TypeError do
       assert_equal 10,  S("FeeFieFoo-Fum") =~ S("Fum")
     end
 
     # "str =~ regexp" same as "regexp =~ str"
     assert_equal 10,  S("FeeFieFoo-Fum") =~ /Fum$/
     assert_equal nil, S("FeeFieFoo-Fum") =~ /FUM$/
 
     # "str =~ obj" calls  "obj =~ str"
     assert_equal ["aaa",  123],  "aaa" =~ MatchDefiner.new(123)
     assert_equal ["bbb", :foo],  "bbb" =~ MatchDefiner.new(:foo)
     assert_equal ["ccc",  nil],  "ccc" =~ MatchDefiner.new(nil)
 
     # default Object#=~ method.
     assert_equal false,  "a string" =~ Object.new
   end
 
   def test_gsub
     assert_equal(S("h*ll*"),     S("hello").gsub(/[aeiou]/, S('*')))
     assert_equal(S("h<e>ll<o>"), S("hello").gsub(/([aeiou])/, S('<\1>')))
     assert_equal(S("104 101 108 108 111 "),
                  S("hello").gsub(/./) { |s| s[0].to_s + S(' ')})
     assert_equal(S("HELL-o"), 
                  S("hello").gsub(/(hell)(.)/) { |s| $1.upcase + S('-') + $2 })
 
     a = S("hello")
     a.taint
     assert(a.gsub(/./, S('X')).tainted?)
   end
 
   def test_gsub_bang
     a = S("hello")
     b = a.dup
     a.gsub!(/[aeiou]/, S('*'))
     assert_equal(S("h*ll*"), a)
     assert_equal(S("hello"), b)
 
     a = S("hello")
     a.gsub!(/([aeiou])/, S('<\1>'))
     assert_equal(S("h<e>ll<o>"), a)
 
     a = S("hello")
     a.gsub!(/./) { |s| s[0].to_s + S(' ')}
     assert_equal(S("104 101 108 108 111 "), a)
 
     a = S("hello")
     a.gsub!(/(hell)(.)/) { |s| $1.upcase + S('-') + $2 }
     assert_equal(S("HELL-o"), a)
 
     r = S('X')
     r.taint
     a.gsub!(/./, r)
     assert(a.tainted?) 
 
     a = S("hello")
     assert_nil(a.sub!(S('X'), S('Y')))
   end
 
   def test_hash
     assert_equal(S("hello").hash, S("hello").hash)
     assert_not_equal(S("hello").hash, S("helLO").hash)
   end
 
   def test_hex
     assert_equal(0,    S("0").hex, "0")
     assert_equal(0,    S("0x0").hex, "0x0")
     assert_equal(255,  S("0xff").hex, "0xff")
     assert_equal(-255, S("-0xff").hex, "-0xff")
     assert_equal(255,  S("0xFF").hex, "0xFF")
     assert_equal(-255, S("-0xFF").hex, "-0xFF")
     assert_equal(255,  S("0Xff").hex, "0Xff")
     assert_equal(255,  S("ff").hex, "ff")
     assert_equal(-255, S("-ff").hex, "-ff")
     assert_equal(255,  S("FF").hex, "FF")
     assert_equal(-255, S("-FF").hex, "-FF")
     assert_equal(0,    S("-ralph").hex, '-ralph')
     assert_equal(-15,  S("-fred").hex, '-fred')
     assert_equal(15,   S("fred").hex, 'fred')
     assert_equal(-15,  S("-Fred").hex, '-Fred')
     assert_equal(15,   S("Fred").hex, 'Fred')
   end
 
   def test_include_eh
     assert_equal true,  S("foobar").include?(S("foo"))
     assert_equal false, S("foobar").include?(S("baz"))
 
     assert_equal true,  S("foobar").include?(?f)
     assert_equal false, S("foobar").include?(?z)
 
     assert_raises TypeError do
       S('').include? :junk
     end
   end
 
   def test_index
     assert_equal(65,  S("AooBar")[0])
     assert_equal(66,  S("FooBaB")[-1])
     assert_equal(nil, S("FooBar")[6])
     assert_equal(nil, S("FooBar")[-7])
 
     assert_equal(S("Foo"), S("FooBar")[0,3])
     assert_equal(S("Bar"), S("FooBar")[-3,3])
     assert_equal(S(""),    S("FooBar")[6,2])
     assert_equal(nil,      S("FooBar")[-7,10])
 
     assert_equal(S("Foo"), S("FooBar")[0..2])
     assert_equal(S("Foo"), S("FooBar")[0...3])
     assert_equal(S("Bar"), S("FooBar")[-3..-1])
     assert_equal("",       S("FooBar")[6..2])
     assert_equal(nil,      S("FooBar")[-10..-7])
 
     assert_equal(S("Foo"), S("FooBar")[/^F../])
     assert_equal(S("Bar"), S("FooBar")[/..r$/])
     assert_equal(nil,      S("FooBar")[/xyzzy/])
     assert_equal(nil,      S("FooBar")[/plugh/])
 
     assert_equal(S("Foo"), S("FooBar")[S("Foo")])
     assert_equal(S("Bar"), S("FooBar")[S("Bar")])
     assert_equal(nil,      S("FooBar")[S("xyzzy")])
     assert_equal(nil,      S("FooBar")[S("plugh")])
 
     if @aref_re_nth
       assert_equal(S("Foo"), S("FooBar")[/([A-Z]..)([A-Z]..)/, 1])
       assert_equal(S("Bar"), S("FooBar")[/([A-Z]..)([A-Z]..)/, 2])
       assert_equal(nil,      S("FooBar")[/([A-Z]..)([A-Z]..)/, 3])
       assert_equal(S("Bar"), S("FooBar")[/([A-Z]..)([A-Z]..)/, -1])
       assert_equal(S("Foo"), S("FooBar")[/([A-Z]..)([A-Z]..)/, -2])
       assert_equal(nil,      S("FooBar")[/([A-Z]..)([A-Z]..)/, -3])
     end
 
     # TODO: figure out why there were two test_index's and how to consolidate
 
     assert_equal 0, S("hello").index(?h)
     assert_equal 3, S("hello").index(?l, 3)
 
     assert_nil      S("hello").index(?z)
     assert_nil      S("hello").index(?z, 3)
 
     assert_equal 1, S("hello").index(S("ell"))
     assert_equal 3, S("hello").index(S("l"), 3)
 
     assert_nil      S("hello").index(/z./)
     assert_nil      S("hello").index(S("z"), 3)
 
     assert_equal 2, S("hello").index(/ll./)
     assert_equal 3, S("hello").index(/l./, 3)
 
     assert_nil      S("hello").index(S("z"))
     assert_nil      S("hello").index(/z./, 3)
 
 #    flunk "No backref tests" # HACK uncomment
   end
 
   def test_index_equals
     s = S("FooBar")
     s[0] = S('A')
     assert_equal(S("AooBar"), s)
 
     s[-1]= S('B')
     assert_equal(S("AooBaB"), s)
     assert_raises(IndexError) { s[-7] = S("xyz") }
     assert_equal(S("AooBaB"), s)
     s[0] = S("ABC")
     assert_equal(S("ABCooBaB"), s)
 
     s = S("FooBar")
     s[0,3] = S("A")
     assert_equal(S("ABar"),s)
     s[0] = S("Foo")
     assert_equal(S("FooBar"), s)
     s[-3,3] = S("Foo")
     assert_equal(S("FooFoo"), s)
     assert_raise(IndexError) { s[7,3] =  S("Bar") }
     assert_raise(IndexError) { s[-7,3] = S("Bar") }
 
     s = S("FooBar")
     s[0..2] = S("A")
     assert_equal(S("ABar"), s)
     s[1..3] = S("Foo")
     assert_equal(S("AFoo"), s)
     s[-4..-4] = S("Foo")
     assert_equal(S("FooFoo"), s)
     assert_raise(RangeError) { s[7..10]   = S("Bar") }
     assert_raise(RangeError) { s[-7..-10] = S("Bar") }
 
     s = S("FooBar")
     s[/^F../]= S("Bar")
     assert_equal(S("BarBar"), s)
     s[/..r$/] = S("Foo")
     assert_equal(S("BarFoo"), s)
     if @aref_re_silent
       s[/xyzzy/] = S("None")
       assert_equal(S("BarFoo"), s)
     else
       assert_raise(IndexError) { s[/xyzzy/] = S("None") }
     end
     if @aref_re_nth
       s[/([A-Z]..)([A-Z]..)/, 1] = S("Foo")
       assert_equal(S("FooFoo"), s)
       s[/([A-Z]..)([A-Z]..)/, 2] = S("Bar")
       assert_equal(S("FooBar"), s)
       assert_raise(IndexError) { s[/([A-Z]..)([A-Z]..)/, 3] = "None" }
       s[/([A-Z]..)([A-Z]..)/, -1] = S("Foo")
       assert_equal(S("FooFoo"), s)
       s[/([A-Z]..)([A-Z]..)/, -2] = S("Bar")
       assert_equal(S("BarFoo"), s)
       assert_raise(IndexError) { s[/([A-Z]..)([A-Z]..)/, -3] = "None" }
     end
 
     s = S("FooBar")
     s[S("Foo")] = S("Bar")
     assert_equal(S("BarBar"), s)
 
     s = S("a string")
     s[0..s.size] = S("another string")
     assert_equal(S("another string"), s)
   end
 
   def test_insert
     assert_equal S("BCAD"), S("AD").insert(0, S("BC"))
     assert_equal S("ABCD"), S("AD").insert(1, S("BC"))
     assert_equal S("ADBC"), S("AD").insert(2, S("BC"))
 
     assert_raises(IndexError) { S("AD").insert(3, S("BC")) }
 
     assert_equal S("ADBC"), S("AD").insert(-1, S("BC"))
     assert_equal S("ABCD"), S("AD").insert(-2, S("BC"))
     assert_equal S("BCAD"), S("AD").insert(-3, S("BC"))
 
     assert_raises(IndexError) { S("AD").insert(-4, S("BC")) }
 
     s = S("AD")
     s.insert 0, S("BC")
     assert_equal S("BCAD"), s
   end
 
   # Need to make sure that we get back exactly what we want, intern is safe
   # for this.  (test/unit calls inspect on results, which is useless for
   # debugging this.)
 
   def test_inspect
     assert_equal :'""',       S("").inspect.intern
     assert_equal :'"string"', S("string").inspect.intern
     assert_equal :'"\""',     S("\"").inspect.intern
     assert_equal :'"\\\\"',   S("\\").inspect.intern
     assert_equal :'"\n"',     S("\n").inspect.intern
     assert_equal :'"\r"',     S("\r").inspect.intern
     assert_equal :'"\t"',     S("\t").inspect.intern
     assert_equal :'"\f"',     S("\f").inspect.intern
     assert_equal :'"\001"',   S("\001").inspect.intern
-    assert_equal :'"\010"',   S("\010").inspect.intern
+    assert_equal :'"\b"',   S("\010").inspect.intern
     assert_equal :'"\177"',   S("\177").inspect.intern
     assert_equal :'"\377"',   S("\377").inspect.intern
 
     assert_equal :'"\\#{1}"', (S("#") + S("{1}")).inspect.intern
 
     assert_equal :'"\\#$f"',  (S("#") + S("$f")).inspect.intern
     assert_equal :'"\\#@f"',  (S("#") + S("@f")).inspect.intern
   end
 
   def test_intern
     assert_equal(:koala, S("koala").intern)
     assert(:koala !=     S("Koala").intern)
 
     # error cases
     assert_raise(ArgumentError) { S("").intern }
     assert_raise(ArgumentError) { S("with\0null\0inside").intern }
   end
 
   def test_length
     assert_equal(0, S("").length)
     assert_equal(4, S("1234").length)
     assert_equal(6, S("1234\r\n").length)
     assert_equal(7, S("\0011234\r\n").length)
   end
 
   def test_ljust
     assert_equal S(""),     S("").ljust(-1)
     assert_equal S(""),     S("").ljust(0)
     assert_equal S(" "),    S("").ljust(1)
     assert_equal S("  "),   S("").ljust(2)
     
     assert_equal S("@"),    S("@").ljust(0)
     assert_equal S("@"),    S("@").ljust(1)
     assert_equal S("@ "),   S("@").ljust(2)
     assert_equal S("@  "),  S("@").ljust(3)
 
     assert_equal S("@@"),   S("@@").ljust(1)
     assert_equal S("@@"),   S("@@").ljust(2)
     assert_equal S("@@ "),  S("@@").ljust(3)
     assert_equal S("@@  "), S("@@").ljust(4)
 
     assert_equal(S("@X"),   S("@").ljust(2, "X"))
     assert_equal(S("@XX"),  S("@").ljust(3, "X"))
 
     assert_equal(S("@X"),   S("@").ljust(2, "XY"))
     assert_equal(S("@XY"),  S("@").ljust(3, "XY"))
     assert_equal(S("@XY"),  S("@").ljust(3, "XY"))
     assert_equal(S("@XYX"), S("@").ljust(4, "XY"))
 
     assert_equal S("@@"),   S("@@").ljust(1, "XY")
     assert_equal S("@@"),   S("@@").ljust(2, "XY")
     assert_equal S("@@X"),  S("@@").ljust(3, "XY")
     assert_equal S("@@XY"), S("@@").ljust(4, "XY")
 
     # zero width padding
     assert_raises ArgumentError do
       S("hi").ljust(0, "")
     end
   end
 
   def test_lstrip
     a = S("  hello")
     assert_equal(S("hello"), a.lstrip)
     assert_equal(S("  hello"), a)
     assert_equal(S("hello "), S(" hello ").lstrip)
     assert_equal(S("hello"), S("hello").lstrip)
   end
 
   def test_lstrip_bang
     a = S("  abc")
     b = a.dup
     assert_equal(S("abc"), a.lstrip!)
     assert_equal(S("abc"), a)
     assert_equal(S("  abc"), b)
   end
 
   def test_lt2
     assert_equal(S("world!"), S("world") << 33)
     assert_equal(S("world!"), S("world") << S('!'))
   end
 
   def test_match
     a = S("cruel world")
 
     m = a.match(/\w+/)
     assert_kind_of MatchData, m
     assert_equal S("cruel"), m.to_s
 
     m = a.match '\w+'
     assert_kind_of MatchData, m
     assert_equal S("cruel"), m.to_s
 
     assert_raises TypeError do
       a.match Object.new
     end
 
     o = Object.new
     def o.to_str() return '\w+' end
     m = a.match o
     assert_kind_of MatchData, m
     assert_equal S("cruel"), m.to_s
   end
 
   def test_next
     assert_equal(S("abd"), S("abc").next)
     assert_equal(S("z"),   S("y").next)
     assert_equal(S("aaa"), S("zz").next)
 
     assert_equal(S("124"),  S("123").next)
     assert_equal(S("1000"), S("999").next)
 
     assert_equal(S("2000aaa"),  S("1999zzz").next)
     assert_equal(S("AAAAA000"), S("ZZZZ999").next)
 
     assert_equal(S("*+"), S("**").next)
   end
 
   def test_next_bang
     a = S("abc")
     b = a.dup
     assert_equal(S("abd"), a.next!)
     assert_equal(S("abd"), a)
     assert_equal(S("abc"), b)
 
     a = S("y")
     assert_equal(S("z"), a.next!)
     assert_equal(S("z"), a)
 
     a = S("zz")
     assert_equal(S("aaa"), a.next!)
     assert_equal(S("aaa"), a)
 
     a = S("123")
     assert_equal(S("124"), a.next!)
     assert_equal(S("124"), a)
 
     a = S("999")
     assert_equal(S("1000"), a.next!)
     assert_equal(S("1000"), a)
 
     a = S("1999zzz")
     assert_equal(S("2000aaa"), a.next!)
     assert_equal(S("2000aaa"), a)
 
     a = S("ZZZZ999")
     assert_equal(S("AAAAA000"), a.next!)
     assert_equal(S("AAAAA000"), a)
 
     a = S("**")
     assert_equal(S("*+"), a.next!)
     assert_equal(S("*+"), a)
   end
 
   def test_oct
     assert_equal(0,    S("0").oct, "0")
     assert_equal(255,  S("0377").oct, "0377")
     assert_equal(-255, S("-0377").oct, "-0377")
     assert_equal(255,  S("377").oct, "377")
     assert_equal(-255, S("-377").oct, "-377")
     assert_equal(24,   S("030X").oct, "030X")
     assert_equal(-24,  S("-030X").oct, "-030X")
     assert_equal(0,    S("ralph").oct, "ralph")
     assert_equal(0,    S("-ralph").oct, "-ralph")
   end
 
   def test_percent
     assert_equal(S("00123"), S("%05d") % 123)
     assert_equal(S("123  |00000001"), S("%-5s|%08x") % [123, 1])
     x = S("%3s %-4s%%foo %.0s%5d %#x%c%3.1f %b %x %X %#b %#x %#X") %
     [S("hi"),
       123,
       S("never seen"),
       456,
       0,
       ?A,
       3.0999,
       11,
       171,
       171,
       11,
       171,
       171]
 
     assert_equal(S(' hi 123 %foo   456 0x0A3.1 1011 ab AB 0b1011 0xab 0XAB'), x)
   end
 
   def test_plus
     s1 = S('')
     s2 = S('')
     s3 = s1 + s2
     assert_equal S(''), s3
     assert_not_equal s1.object_id, s3.object_id
     assert_not_equal s2.object_id, s3.object_id
 
     s1 = S('yo')
     s2 = S('')
     s3 = s1 + s2
     assert_equal S('yo'), s3
     assert_not_equal s1.object_id, s3.object_id
 
     s1 = S('')
     s2 = S('yo')
     s3 = s1 + s2
     assert_equal S('yo'), s3
     assert_not_equal s2.object_id, s3.object_id
 
     s1 = S('yo')
     s2 = S('del')
     s3 = s1 + s2
     assert_equal S('yodel'), s3
     assert_equal false, s3.tainted?
 
     s1 = S('yo')
     s2 = S('del')
     s1.taint
     s3 = s1 + s2
     assert_equal true, s3.tainted?
 
     s1 = S('yo')
     s2 = S('del')
     s2.taint
     s3 = s1 + s2
     assert_equal true, s3.tainted?
 
     s1 = S('yo')
     s2 = S('del')
     s1.taint
     s2.taint
     s3 = s1 + s2
     assert_equal true, s3.tainted?
   end
 
   def test_replace
     a = S("foo")
     assert_equal S("f"), a.replace(S("f"))
 
     a = S("foo")
     assert_equal S("foobar"), a.replace(S("foobar"))
 
     a = S("foo")
     a.taint
     b = a.replace S("xyz")
     assert_equal S("xyz"), b
 
     assert b.tainted?, "Replaced string should be tainted"
   end
 
   def test_reverse
     a = S("beta")
     assert_equal(S("ateb"), a.reverse)
     assert_equal(S("beta"), a)
   end
 
   def test_reverse_bang
     a = S("beta")
     assert_equal(S("ateb"), a.reverse!)
     assert_equal(S("ateb"), a)
   end
 
   def test_rindex
     # String
     assert_equal 0, S('').rindex(S(''))
     assert_equal 3, S('foo').rindex(S(''))
     assert_nil   S('').rindex(S('x'))
 
     assert_equal 6, S("ell, hello").rindex(S("ell"))
     assert_equal 3, S("hello,lo").rindex(S("l"), 3)
 
     assert_nil   S("hello").rindex(S("z"))
     assert_nil   S("hello").rindex(S("z"), 3)
 
     # Fixnum
     assert_nil S('').rindex(0)
 
     assert_equal 3, S("hello").rindex(?l)
     assert_equal 3, S("hello,lo").rindex(?l, 3)
 
     assert_nil S("hello").rindex(?z)
     assert_nil S("hello").rindex(?z, 3)
 
     assert_nil S('').rindex(256)
 
     assert_nil S('').rindex(-1)
 
     # Regexp
     assert_equal 0, S('').rindex(//)
     assert_equal 5, S('hello').rindex(//)
 
     assert_nil S('').rindex(/x/)
 
     assert_equal 7, S("ell, hello").rindex(/ll./)
     assert_equal 3, S("hello,lo").rindex(/l./, 3)
 
     assert_nil S("hello").rindex(/z./,   3)
     assert_nil S("hello").rindex(/z./)
   end
 
   def test_rjust
     assert_equal S(""),     S("").rjust(-1)
     assert_equal S(""),     S("").rjust(0)
     assert_equal S(" "),    S("").rjust(1)
     assert_equal S("  "),   S("").rjust(2)
     
     assert_equal S("@"),    S("@").rjust(0)
     assert_equal S("@"),    S("@").rjust(1)
     assert_equal S(" @"),   S("@").rjust(2)
     assert_equal S("  @"),  S("@").rjust(3)
 
     assert_equal S("@@"),   S("@@").rjust(1)
     assert_equal S("@@"),   S("@@").rjust(2)
     assert_equal S(" @@"),  S("@@").rjust(3)
     assert_equal S("  @@"), S("@@").rjust(4)
 
     assert_equal(S("X@"),   S("@").rjust(2, "X"))
     assert_equal(S("XX@"),  S("@").rjust(3, "X"))
 
     assert_equal(S("X@"),   S("@").rjust(2, "XY"))
     assert_equal(S("XY@"),  S("@").rjust(3, "XY"))
     assert_equal(S("XY@"),  S("@").rjust(3, "XY"))
     assert_equal(S("XYX@"), S("@").rjust(4, "XY"))
 
     assert_equal S("@@"),   S("@@").rjust(1, "XY")
     assert_equal S("@@"),   S("@@").rjust(2, "XY")
     assert_equal S("X@@"),  S("@@").rjust(3, "XY")
     assert_equal S("XY@@"), S("@@").rjust(4, "XY")
 
     # zero width padding
     assert_raises ArgumentError do
       S("hi").rjust(0, "")
     end
   end
 
   def test_rstrip
     a = S("hello  ")
     assert_equal(S("hello"), a.rstrip)
     assert_equal(S("hello  "), a)
     assert_equal(S(" hello"), S(" hello ").rstrip)
     assert_equal(S("hello"), S("hello").rstrip)
   end
 
   def test_rstrip_bang
     a = S("abc  ")
     b = a.dup
     assert_equal(S("abc"), a.rstrip!)
     assert_equal(S("abc"), a)
     assert_equal(S("abc  "), b)
   end
 
   def test_scan
     a = S("cruel world")
     assert_equal([S("cruel"), S("world")],a.scan(/\w+/))
     assert_equal([S("cru"), S("el "), S("wor")],a.scan(/.../))
     assert_equal([[S("cru")], [S("el ")], [S("wor")]],a.scan(/(...)/))
 
     res = []
     a.scan(/\w+/) { |w| res << w }
     assert_equal([S("cruel"), S("world") ],res)
 
     res = []
     a.scan(/.../) { |w| res << w }
     assert_equal([S("cru"), S("el "), S("wor")],res)
 
     res = []
     a.scan(/(...)/) { |w| res << w }
     assert_equal([[S("cru")], [S("el ")], [S("wor")]],res)
   end
 
   def test_size
     assert_equal(0, S("").size)
     assert_equal(4, S("1234").size)
     assert_equal(6, S("1234\r\n").size)
     assert_equal(7, S("\0011234\r\n").size)
   end
 
   def test_slice
     assert_equal(65, S("AooBar").slice(0))
     assert_equal(66, S("FooBaB").slice(-1))
     assert_nil(S("FooBar").slice(6))
     assert_nil(S("FooBar").slice(-7))
 
     assert_equal(S("Foo"), S("FooBar").slice(0,3))
     assert_equal(S(S("Bar")), S("FooBar").slice(-3,3))
     assert_nil(S("FooBar").slice(7,2))     # Maybe should be six?
     assert_nil(S("FooBar").slice(-7,10))
 
     assert_equal(S("Foo"), S("FooBar").slice(0..2))
     assert_equal(S("Bar"), S("FooBar").slice(-3..-1))
 #    Version.less_than("1.8.2") do
 #      assert_nil(S("FooBar").slice(6..2))
 #    end
 #    Version.greater_or_equal("1.8.2") do
       assert_equal("", S("FooBar").slice(6..2))
 #    end
     assert_nil(S("FooBar").slice(-10..-7))
 
     assert_equal(S("Foo"), S("FooBar").slice(/^F../))
     assert_equal(S("Bar"), S("FooBar").slice(/..r$/))
     assert_nil(S("FooBar").slice(/xyzzy/))
     assert_nil(S("FooBar").slice(/plugh/))
 
     assert_equal(S("Foo"), S("FooBar").slice(S("Foo")))
     assert_equal(S("Bar"), S("FooBar").slice(S("Bar")))
     assert_nil(S("FooBar").slice(S("xyzzy")))
     assert_nil(S("FooBar").slice(S("plugh")))
   end
 
   def test_slice_bang
     a = S("AooBar")
     b = a.dup
     assert_equal(65, a.slice!(0))
     assert_equal(S("ooBar"), a)
     assert_equal(S("AooBar"), b)
 
     a = S("FooBar")
     assert_equal(?r,a.slice!(-1))
     assert_equal(S("FooBa"), a)
 
     a = S("FooBar")
     if @aref_slicebang_silent
       assert_nil( a.slice!(6) )
     else
       assert_raises(:IndexError) { a.slice!(6) }
     end 
     assert_equal(S("FooBar"), a)
 
     if @aref_slicebang_silent
       assert_nil( a.slice!(-7) ) 
     else 
       assert_raises(:IndexError) { a.slice!(-7) }
     end
     assert_equal(S("FooBar"), a)
 
     a = S("FooBar")
     assert_equal(S("Foo"), a.slice!(0,3))
     assert_equal(S("Bar"), a)
 
     a = S("FooBar")
     assert_equal(S("Bar"), a.slice!(-3,3))
     assert_equal(S("Foo"), a)
 
     a=S("FooBar")
     if @aref_slicebang_silent
       assert_nil(a.slice!(7,2))      # Maybe should be six?
     else
       assert_raises(:IndexError) {a.slice!(7,2)}     # Maybe should be six?
     end
     assert_equal(S("FooBar"), a)
     if @aref_slicebang_silent
       assert_nil(a.slice!(-7,10))
     else
       assert_raises(:IndexError) {a.slice!(-7,10)}
     end
     assert_equal(S("FooBar"), a)
 
     a=S("FooBar")
     assert_equal(S("Foo"), a.slice!(0..2))
     assert_equal(S("Bar"), a)
 
     a=S("FooBar")
     assert_equal(S("Bar"), a.slice!(-3..-1))
     assert_equal(S("Foo"), a)
 
     a=S("FooBar")
     if @aref_slicebang_silent
 #      Version.less_than("1.8.2") do
 #        assert_nil(a.slice!(6..2))
 #      end
 #      Version.greater_or_equal("1.8.2") do
         assert_equal("", a.slice!(6..2))
 #      end
     else
       assert_raises(:RangeError) {a.slice!(6..2)}
     end
     assert_equal(S("FooBar"), a)
     if @aref_slicebang_silent
       assert_nil(a.slice!(-10..-7))
     else
       assert_raises(:RangeError) {a.slice!(-10..-7)}
     end
     assert_equal(S("FooBar"), a)
 
     a=S("FooBar")
     assert_equal(S("Foo"), a.slice!(/^F../))
     assert_equal(S("Bar"), a)
 
     a=S("FooBar")
     assert_equal(S("Bar"), a.slice!(/..r$/))
     assert_equal(S("Foo"), a)
 
     a=S("FooBar")
     if @aref_slicebang_silent
       assert_nil(a.slice!(/xyzzy/))
     else
       assert_raises(:IndexError) {a.slice!(/xyzzy/)}
     end
     assert_equal(S("FooBar"), a)
     if @aref_slicebang_silent
       assert_nil(a.slice!(/plugh/))
     else
       assert_raises(:IndexError) {a.slice!(/plugh/)}
     end
     assert_equal(S("FooBar"), a)
 
     a=S("FooBar")
     assert_equal(S("Foo"), a.slice!(S("Foo")))
     assert_equal(S("Bar"), a)
 
     a=S("FooBar")
     assert_equal(S("Bar"), a.slice!(S("Bar")))
     assert_equal(S("Foo"), a)
   end
 
   def test_spaceship
     assert_equal( 1, S("abcdef") <=> S("ABCDEF"))
     assert_equal(-1, S("ABCDEF") <=> S("abcdef"))
 
     assert_equal( 1, S("abcdef") <=> S("abcde") )
     assert_equal( 0, S("abcdef") <=> S("abcdef"))
     assert_equal(-1, S("abcde")  <=> S("abcdef"))
   end
 
   def test_split
     original_dollar_semi = $;.nil? ? $; : $;.dup
 
     assert_equal [], S("").split
     assert_equal [], S("").split(nil)
     assert_equal [], S("").split(' ')
     assert_equal [], S("").split(nil, 1)
     assert_equal [], S("").split(' ', 1)
 
     str = S("a")
     arr = str.split(nil, 1)
     assert_equal ["a"], arr
     assert_equal str.object_id, arr.first.object_id
 
     # Tests of #split's behavior with a pattern of ' ' or $; == nil
     $; = nil
     assert_equal [S("a"), S("b")], S("a b")       .split
     assert_equal [S("a"), S("b")], S("a  b")      .split
     assert_equal [S("a"), S("b")], S(" a b")      .split
     assert_equal [S("a"), S("b")], S(" a b ")     .split
     assert_equal [S("a"), S("b")], S("  a b ")    .split
     assert_equal [S("a"), S("b")], S("  a  b ")   .split
     assert_equal [S("a"), S("b")], S("  a b  ")   .split
     assert_equal [S("a"), S("b")], S("  a  b  ")  .split
 
     assert_equal [S("a"), S("b")], S("a\tb")      .split
     assert_equal [S("a"), S("b")], S("a\t\tb")    .split
     assert_equal [S("a"), S("b")], S("a\nb")      .split
     assert_equal [S("a"), S("b")], S("a\n\nb")    .split
     assert_equal [S("a"), S("b")], S("a\rb")      .split
     assert_equal [S("a"), S("b")], S("a\r\rb")    .split
 
     assert_equal [S("a"), S("b")], S("a\t b")     .split
     assert_equal [S("a"), S("b")], S("a\t b")     .split
     assert_equal [S("a"), S("b")], S("a\t\nb")    .split
     assert_equal [S("a"), S("b")], S("a\r\nb")    .split
     assert_equal [S("a"), S("b")], S("a\r\n\r\nb").split
 
     assert_equal [S("a"), S("b"), S("c")], S(" a   b\t c ").split
 
     assert_equal [S("a"), S("b")], S("a b")       .split(S(" "))
     assert_equal [S("a"), S("b")], S("a  b")      .split(S(" "))
     assert_equal [S("a"), S("b")], S(" a b")      .split(S(" "))
     assert_equal [S("a"), S("b")], S(" a b ")     .split(S(" "))
     assert_equal [S("a"), S("b")], S("  a b ")    .split(S(" "))
     assert_equal [S("a"), S("b")], S("  a  b ")   .split(S(" "))
     assert_equal [S("a"), S("b")], S("  a b  ")   .split(S(" "))
     assert_equal [S("a"), S("b")], S("  a  b  ")  .split(S(" "))
 
     assert_equal [S("a"), S("b")], S("a\tb")      .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\nb")      .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\rb")      .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\vb")      .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\t\tb")    .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\n\nb")    .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\r\rb")    .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\v\vb")    .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\v\vb")    .split(S(" "))
 
     assert_equal [S("a"), S("b")], S("a\t b")     .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\t b")     .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\t\nb")    .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\r\nb")    .split(S(" "))
     assert_equal [S("a"), S("b")], S("a\r\n\r\nb").split(S(" "))
 
     assert_equal [S("a"), S("b"), S("")], S("  a  b  ").split(S(" "), -2)
     assert_equal [S("a"), S("b"), S("")], S("  a  b  ").split(S(" "), -1)
     assert_equal [S("a"), S("b")],        S("  a  b  ").split(S(" "),  0)
     assert_equal [S("  a  b  ")],         S("  a  b  ").split(S(" "),  1)
     assert_equal [S("a"), S("b  ")],      S("  a  b  ").split(S(" "),  2)
     assert_equal [S("a"), S("b"), S("")], S("  a  b  ").split(S(" "),  3)
     assert_equal [S("a"), S("b"), S("")], S("  a  b  ").split(S(" "),  4)
 
     assert_equal [S("a"), S("b"), S("c")], S(" a   b\t c ").split(S(" "))
 
     assert_equal [S("a"), S("b")], S("a b").split(nil)
 
     # These tests are all for various patterns.
 
     assert_equal [S("a b")],                S("a b")     .split(S("  "))
     assert_equal [S("a"), S("b")],          S("a  b")    .split(S("  "))
     assert_equal [S(" a b")],               S(" a b")    .split(S("  "))
     assert_equal [S(" a b ")],              S(" a b ")   .split(S("  "))
     assert_equal [S(""),  S("a b ")],       S("  a b ")  .split(S("  "))
     assert_equal [S(""),  S("a"), S("b ")], S("  a  b ") .split(S("  "))
     assert_equal [S(""),  S("a b")],        S("  a b  ") .split(S("  "))
     assert_equal [S(""),  S("a"), S("b")],  S("  a  b  ").split(S("  "))
 
     $; = '|'
     assert_equal [S("a"), S("b")],                          S("a|b")     .split
     assert_equal [S("a"), S(""),  S("b")],                  S("a||b")    .split
     assert_equal [S(""),  S("a"), S("b")],                  S("|a|b")    .split
     assert_equal [S(""),  S("a"), S("b")],                  S("|a|b|")   .split
     assert_equal [S(""),  S(""),  S("a"),  S("b")],         S("||a|b|")  .split
     assert_equal [S(""),  S(""),  S("a"),  S(""),  S("b")], S("||a||b|") .split
     assert_equal [S(""),  S(""),  S("a"),  S("b")],         S("||a|b||") .split
     assert_equal [S(""),  S(""),  S("a"),  S(""),  S("b")], S("||a||b||").split
 
     assert_equal [S("a"), S("b"), S("c")],        S("a|b|c") .split(S("|"))
     assert_equal [S("a"), S(""), S("c")],         S("a||c")  .split(S("|"))
     assert_equal [S(""), S("a"), S("b"), S("c")], S("|a|b|c").split(S("|"))
 
     assert_equal [S("a b")], S("a b").split(nil)
     assert_equal [S("a"), S("b")], S("a|b").split(nil)
 
     # Regexp
 
     assert_equal [S("a"), S("b"), S("c")], S("abc").split(//)
     assert_equal [S("a"), S("b"), S("c")], S("abc").split(//i)
 
     assert_equal [S("a"), S("b"), S("c")], S("a|b|c").split(S('|'), -1)
     assert_equal [S("a"), S("b"), S("c")], S("a|b|c").split(S('|'),  0)
     assert_equal [S("a|b|c")],             S("a|b|c").split(S('|'),  1)
     assert_equal [S("a"), S("b|c")],       S("a|b|c").split(S('|'),  2)
     assert_equal [S("a"), S("b"), S("c")], S("a|b|c").split(S('|'),  3)
     assert_equal [S("a"), S("b"), S("c")], S("a|b|c").split(S('|'),  4)
 
     assert_equal [S("a"), S("b")],        S("a|b|").split(S('|'))
     assert_equal([S("a"), S("b"), S("")], S("a|b|").split(S('|'), -1))
 
     assert_equal [S("a"), S(""), S("b"), S("c")], S("a||b|c|").split(S('|'))
     assert_equal([S("a"), S(""), S("b"), S("c"), S("")],
                  S("a||b|c|").split(S('|'), -1))
 
     assert_equal [S("a"), S("b")],                 S("a b")   .split(/ /)
     assert_equal [S("a"), S(""),  S("b")],         S("a  b")  .split(/ /)
     assert_equal [S(""),  S("a"), S("b")],         S(" a b")  .split(/ /)
     assert_equal [S(""),  S("a"), S("b")],         S(" a b ") .split(/ /)
     assert_equal [S(""),  S(""),  S("a"), S("b")], S("  a b ").split(/ /)
     assert_equal([S(""),  S(""),  S("a"), S(""), S("b")],
                  S("  a  b ").split(/ /))
     assert_equal([S(""),  S(""),  S("a"), S("b")],
                  S("  a b  ").split(/ /))
     assert_equal([S(""),  S(""),  S("a"), S(""), S("b")],
                  S("  a  b  ").split(/ /))
 
     assert_equal [S("a b")],                S("a b")     .split(/  /)
     assert_equal [S("a"), S("b")],          S("a  b")    .split(/  /)
     assert_equal [S(" a b")],               S(" a b")    .split(/  /)
     assert_equal [S(" a b ")],              S(" a b ")   .split(/  /)
     assert_equal [S(""),  S("a b ")],       S("  a b ")  .split(/  /)
     assert_equal [S(""),  S("a"), S("b ")], S("  a  b ") .split(/  /)
     assert_equal [S(""),  S("a b")],        S("  a b  ") .split(/  /)
     assert_equal [S(""),  S("a"), S("b")],  S("  a  b  ").split(/  /)
 
     assert_equal([S("a"), S("b")],
                  S("a@b")     .split(/@/))
     assert_equal([S("a"), S(""),  S("b")],
                  S("a@@b")    .split(/@/))
     assert_equal([S(""),  S("a"), S("b")],
                  S("@a@b")    .split(/@/))
     assert_equal([S(""),  S("a"), S("b")],
                  S("@a@b@")   .split(/@/))
     assert_equal([S(""),  S(""),  S("a"),  S("b")],
                  S("@@a@b@")  .split(/@/))
     assert_equal([S(""),  S(""),  S("a"),  S(""),  S("b")],
                  S("@@a@@b@") .split(/@/))
     assert_equal([S(""),  S(""),  S("a"),  S("b")],
                  S("@@a@b@@") .split(/@/))
     assert_equal([S(""),  S(""),  S("a"),  S(""),  S("b")],
                  S("@@a@@b@@").split(/@/))
 
     assert_equal [S("a"), S("b"), S("c")],        S("a@b@c") .split(/@/)
     assert_equal [S("a"), S(""), S("c")],         S("a@@c")  .split(/@/)
     assert_equal [S(""), S("a"), S("b"), S("c")], S("@a@b@c").split(/@/)
 
     # grouping
 
     assert_equal([S('ab'), S('1'), S('cd'), S('2'), S('ef')],
                  S('ab1cd2ef').split(/(\d)/))
 
     assert_equal([S('ab'), S('1'), S('cd'), S('2'), S('ef')],
                  S('ab1cd2ef').split(/(\d)/, -2))
     assert_equal([S('ab'), S('1'), S('cd'), S('2'), S('ef')],
                  S('ab1cd2ef').split(/(\d)/, -1))
     assert_equal([S('ab'), S('1'), S('cd'), S('2'), S('ef')],
                  S('ab1cd2ef').split(/(\d)/,  0))
     assert_equal([S('ab1cd2ef')],
                  S('ab1cd2ef').split(/(\d)/,  1))
     assert_equal([S('ab'), S('1'), S('cd2ef')],
                  S('ab1cd2ef').split(/(\d)/,  2))
     assert_equal([S('ab'), S('1'), S('cd'), S('2'), S('ef')],
                  S('ab1cd2ef').split(/(\d)/,  3))
     assert_equal([S('ab'), S('1'), S('cd'), S('2'), S('ef')],
                  S('ab1cd2ef').split(/(\d)/,  4))
     assert_equal([S('ab'), S('1'),  S('cd'), S('2'), S('ef')],
                  S('ab1cd2ef').split(/(\d)/,  5))
 
     # mulitple grouping
 
     assert_equal([S('ab'), S('1'), S('2'), S('cd'), S('3'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d)(\d)/))
 
     assert_equal([S('ab'), S('1'), S('2'), S('cd'), S('3'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d)(\d)/, -2))
     assert_equal([S('ab'), S('1'), S('2'), S('cd'), S('3'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d)(\d)/, -1))
     assert_equal([S('ab'), S('1'), S('2'), S('cd'), S('3'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d)(\d)/,  0))
     assert_equal([S('ab12cd34ef')],
                  S('ab12cd34ef').split(/(\d)(\d)/,  1))
     assert_equal([S('ab'), S('1'), S('2'), S('cd34ef')],
                  S('ab12cd34ef').split(/(\d)(\d)/,  2))
     assert_equal([S('ab'), S('1'), S('2'), S('cd'), S('3'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d)(\d)/,  3))
     assert_equal([S('ab'), S('1'), S('2'), S('cd'), S('3'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d)(\d)/,  4))
     assert_equal([S('ab'), S('1'), S('2'), S('cd'), S('3'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d)(\d)/,  5))
 
     # nested grouping (puke)
 
     assert_equal([S('ab'), S('12'), S('2'), S('cd'), S('34'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d(\d))/))
 
     assert_equal([S('ab'), S('12'), S('2'), S('cd'), S('34'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d(\d))/, -2))
     assert_equal([S('ab'), S('12'), S('2'), S('cd'), S('34'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d(\d))/, -1))
     assert_equal([S('ab'), S('12'), S('2'), S('cd'), S('34'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d(\d))/,  0))
     assert_equal([S('ab12cd34ef')],
                  S('ab12cd34ef').split(/(\d(\d))/,  1))
     assert_equal([S('ab'), S('12'), S('2'), S('cd34ef')],
                  S('ab12cd34ef').split(/(\d(\d))/,  2))
     assert_equal([S('ab'), S('12'), S('2'), S('cd'), S('34'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d(\d))/,  3))
     assert_equal([S('ab'), S('12'), S('2'), S('cd'), S('34'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d(\d))/,  4))
     assert_equal([S('ab'), S('12'), S('2'), S('cd'), S('34'), S('4'), S('ef')],
                  S('ab12cd34ef').split(/(\d(\d))/,  5))
 
     # split any zero-length match
 
     assert_equal [S('a'), S('b'), S('c')], S('abc').split(/[z]|/)
 
   ensure
     $; = original_dollar_semi
   end
 
   def test_split_icky1
     # HACK soo bizarre and ugly.
     assert_equal([S('a'), S('b'), S('c'), S('z')],
                  S('abcz').split(/([z])|/),
                  "S('abcz').split(/([z])|/)")
   end
 
   def test_split_icky2
     assert_equal([S('c'), S('z'), S('a')],
                  S('cza').split(/([z]|)/),
                  "S('cz').split(/([z]|)/)")
     assert_equal([S('a'), S(''), S('b'), S(''), S('c'), S('z')],
                  S('abcz').split(/([z]|)/),
                  "S('abcz').split(/([z]|)/)")
   end
 
   def test_squeeze
     assert_equal(S("abc"), S("aaabbbbccc").squeeze)
     assert_equal(S("aa bb cc"), S("aa   bb      cc").squeeze(S(" ")))
     assert_equal(S("BxTyWz"), S("BxxxTyyyWzzzzz").squeeze(S("a-z")))
   end
 
   def test_squeeze_bang
     a = S("aaabbbbccc")
     b = a.dup
     assert_equal(S("abc"), a.squeeze!)
     assert_equal(S("abc"), a)
     assert_equal(S("aaabbbbccc"), b)
 
     a = S("aa   bb      cc")
     assert_equal(S("aa bb cc"), a.squeeze!(S(" ")))
     assert_equal(S("aa bb cc"), a)
 
     a = S("BxxxTyyyWzzzzz")
     assert_equal(S("BxTyWz"), a.squeeze!(S("a-z")))
     assert_equal(S("BxTyWz"), a)
 
     a=S("The quick brown fox")
     assert_nil(a.squeeze!)
   end
 
   def test_strip
     assert_equal(S("x"), S("      x        ").strip)
     assert_equal(S("x"), S(" \n\r\t     x  \t\r\n\n      ").strip)
   end
 
   def test_strip_bang
     a = S("      x        ")
     b = a.dup
     assert_equal(S("x") ,a.strip!)
     assert_equal(S("x") ,a)
     assert_equal(S("      x        "), b)
 
     a = S(" \n\r\t     x  \t\r\n\n      ")
     assert_equal(S("x"), a.strip!)
     assert_equal(S("x"), a)
 
     a = S("x")
     assert_nil(a.strip!)
     assert_equal(S("x") ,a)
   end
 
   def test_sub
     assert_equal(S("h*llo"),    S("hello").sub(/[aeiou]/, S('*')))
     assert_equal(S("h<e>llo"),  S("hello").sub(/([aeiou])/, S('<\1>')))
     assert_equal(S("104 ello"), S("hello").sub(/./) { |s| s[0].to_s + S(' ')})
     assert_equal(S("HELL-o"),   S("hello").sub(/(hell)(.)/) {
                    |s| $1.upcase + S('-') + $2
                    })
 
     assert_equal(S("a\\aba"), S("ababa").sub(/b/, '\\'))
     assert_equal(S("ab\\aba"), S("ababa").sub(/(b)/, '\1\\'))
     assert_equal(S("ababa"), S("ababa").sub(/(b)/, '\1'))
     assert_equal(S("ababa"), S("ababa").sub(/(b)/, '\\1'))
     assert_equal(S("a\\1aba"), S("ababa").sub(/(b)/, '\\\1'))
     assert_equal(S("a\\1aba"), S("ababa").sub(/(b)/, '\\\\1'))
     assert_equal(S("a\\baba"), S("ababa").sub(/(b)/, '\\\\\1'))
 
     assert_equal(S("a--ababababababababab"),
                  S("abababababababababab").sub(/(b)/, '-\9-'))
     assert_equal(S("1-b-0"),
                  S("1b2b3b4b5b6b7b8b9b0").
                  sub(/(b).(b).(b).(b).(b).(b).(b).(b).(b)/, '-\9-'))
     assert_equal(S("1-b-0"),
                  S("1b2b3b4b5b6b7b8b9b0").
                  sub(/(b).(b).(b).(b).(b).(b).(b).(b).(b)/, '-\\9-'))
     assert_equal(S("1-\\9-0"),
                  S("1b2b3b4b5b6b7b8b9b0").
                  sub(/(b).(b).(b).(b).(b).(b).(b).(b).(b)/, '-\\\9-'))
     assert_equal(S("k"),
                  S("1a2b3c4d5e6f7g8h9iAjBk").
                  sub(/.(.).(.).(.).(.).(.).(.).(.).(.).(.).(.).(.)/, '\+'))
 
     assert_equal(S("ab\\aba"), S("ababa").sub(/b/, '\&\\'))
     assert_equal(S("ababa"), S("ababa").sub(/b/, '\&'))
     assert_equal(S("ababa"), S("ababa").sub(/b/, '\\&'))
     assert_equal(S("a\\&aba"), S("ababa").sub(/b/, '\\\&'))
     assert_equal(S("a\\&aba"), S("ababa").sub(/b/, '\\\\&'))
     assert_equal(S("a\\baba"), S("ababa").sub(/b/, '\\\\\&'))
 
     a = S("hello")
     a.taint
     assert(a.sub(/./, S('X')).tainted?)
   end
 
   def test_sub_bang
     a = S("hello")
     assert_nil a.sub!(/X/, S('Y'))
 
     a = S("hello")
     assert_nil a.sub!(/X/) { |s| assert_nil s }
 
     a = S("hello")
     a.sub!(/[aeiou]/, S('*'))
     assert_equal(S("h*llo"), a)
 
     a = S("hello")
     a.sub!(/([aeiou])/, S('<\1>'))
     assert_equal(S("h<e>llo"), a)
 
     a = S("hello")
 
     a.sub!(/./) do |s|
       assert_equal S('h'), s
       s[0].to_s + S(' ')
     end
 
     assert_equal(S("104 ello"), a)
 
     a = S("hello")
     a.sub!(/(hell)(.)/) do |s|
       assert_equal S('hell'), $1
       assert_equal S('o'), $2
       $1.upcase + S('-') + $2
     end
     assert_equal(S("HELL-o"), a)
 
     r = S('X')
     r.taint
     a.sub!(/./, r)
     assert(a.tainted?) 
   end
 
   def test_succ
     assert_equal(S("abd"), S("abc").succ)
     assert_equal(S("z"),   S("y").succ)
     assert_equal(S("aaa"), S("zz").succ)
 
     assert_equal(S("124"),  S("123").succ)
     assert_equal(S("1000"), S("999").succ)
 
     assert_equal(S("2000aaa"),  S("1999zzz").succ)
     assert_equal(S("AAAAA000"), S("ZZZZ999").succ)
     assert_equal(S("*+"), S("**").succ)
 
     assert_equal(S("\001\000"), S("\377").succ)
   end
 
   def test_succ_bang
     a = S("abc")
     assert_equal(S("abd"), a.succ!)
     assert_equal(S("abd"), a)
 
     a = S("y")
     assert_equal(S("z"), a.succ!)
     assert_equal(S("z"), a)
 
     a = S("zz")
     assert_equal(S("aaa"), a.succ!)
     assert_equal(S("aaa"), a)
 
     a = S("123")
     assert_equal(S("124"), a.succ!)
     assert_equal(S("124"), a)
 
     a = S("999")
     assert_equal(S("1000"), a.succ!)
     assert_equal(S("1000"), a)
 
     a = S("1999zzz")
     assert_equal(S("2000aaa"), a.succ!)
     assert_equal(S("2000aaa"), a)
 
     a = S("ZZZZ999")
     assert_equal(S("AAAAA000"), a.succ!)
     assert_equal(S("AAAAA000"), a)
 
     a = S("**")
     assert_equal(S("*+"), a.succ!)
     assert_equal(S("*+"), a)
 
     a = S("\377")
     assert_equal(S("\001\000"), a.succ!)
     assert_equal(S("\001\000"), a)
   end
 
   def test_sum
     n = S("\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001")
     assert_equal(15, n.sum)
     assert_equal(15, n.sum(32))
     n += S("\001")
     assert_equal(16, n.sum(17))
     assert_equal(16, n.sum(32))
     n[0] = 2
     assert(15 != n.sum)
 
     # basic test of all reasonable "bit sizes"
     str = S("\xFF" * 257)
     2.upto(32) do |bits|
       assert_equal(65535 % (2**bits), str.sum(bits))
     end
 
     # with 16 bits the result is modulo 65536
     assert_equal(  255, S("\xFF").sum)
     assert_equal(  510, S("\xFF\xFF").sum)
     assert_equal(65535, S("\xFF" * 257).sum)
     assert_equal(  254, S("\xFF" * 258).sum)
 
     # with 32 bits the result is modulo 2**32
     assert_equal(  255, S("\xFF").sum(32))
     assert_equal(  510, S("\xFF\xFF").sum(32))
     assert_equal(65535, S("\xFF" * 257).sum(32))
     assert_equal(65790, S("\xFF" * 258).sum(32))
     # the following case takes 16MB and takes a long time to compute,
     # so it is commented out.
     #assert_equal(  254, S("\xFF" * (257 * 65537 + 1)).sum(32))
   end
 
   def test_swapcase
     assert_equal(S("hi&LOW"), S("HI&low").swapcase)
   end
 
   def test_swapcase_bang
     a = S("hi&LOW")
     b = a.dup
     assert_equal(S("HI&low"), a.swapcase!)
     assert_equal(S("HI&low"), a)
     assert_equal(S("hi&LOW"), b)
 
