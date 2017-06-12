diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index c40788c5b7..7e6d49143f 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1155 +1,1161 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2003 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2007 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.nio.channels.FileChannel;
 import java.nio.channels.FileLock;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.DirectoryAsFileException;
 import org.jruby.util.IOHandler;
 import org.jruby.util.IOHandlerNull;
 import org.jruby.util.IOHandlerSeekable;
 import org.jruby.util.IOHandlerUnseekable;
 import org.jruby.util.IOModes;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.IOHandler.InvalidValueException;
 import org.jruby.util.TypeConverter;
 
 /**
  * Ruby File class equivalent in java.
  **/
 public class RubyFile extends RubyIO {
     public static final int LOCK_SH = 1;
     public static final int LOCK_EX = 2;
     public static final int LOCK_NB = 4;
     public static final int LOCK_UN = 8;
 
     private static final int FNM_NOESCAPE = 1;
     private static final int FNM_PATHNAME = 2;
     private static final int FNM_DOTMATCH = 4;
     private static final int FNM_CASEFOLD = 8;
 
     static final boolean IS_WINDOWS;
     static {
         String osname = System.getProperty("os.name");
         IS_WINDOWS = osname != null && osname.toLowerCase().indexOf("windows") != -1;
     }
 
     protected String path;
     private FileLock currentLock;
     
     public RubyFile(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
     public RubyFile(Ruby runtime, String path) {
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
         
-        JRubyFile path = JRubyFile.create(cwd, relativePath);
-
+        JRubyFile path;
+        
+        if (relativePath.length() == 0) {
+            path = JRubyFile.create(relativePath, cwd);
+        } else {
+            path = JRubyFile.create(cwd, relativePath);
+        }
+        
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
     
     @JRubyMethod(required = 1, rest = true, frame = true, meta = true)
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
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index 6897bd374e..af05bdfb14 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -2338,1020 +2338,1032 @@ public class RubyString extends RubyObject {
                 if(ptr<p && (rslen ==0 || (buff[p-1] == lastVal &&
                                             (rslen <= 1 || 
                                              ByteList.memcmp(rsep.bytes, rsep.begin, rslen, buff, p-rslen, rslen) == 0)))) {
 
                     result.append(makeShared(s-ptr, (p - s) - rslen));
                     s = p;
                     if(limit) {
                         i++;
                         if(lim<=i) {
                             p = pend;
                             break;
                         }
                     }
                 }
             }
             
             if(s != pend) {
                 if(p > pend) {
                     p = pend;
                 }
                 if(!(limit && lim<=i) && ByteList.memcmp(rsep.bytes, rsep.begin, rslen, buff, p-rslen, rslen) == 0) {
                     result.append(makeShared(s-ptr, (p - s)-rslen));
                     if(lim < 0) {
                         result.append(newEmptyString(runtime, getMetaClass()));
                     }
                 } else {
                     result.append(makeShared(s-ptr, p - s));
                 }
             }
         } else { // regexp split
             spat = getPat(spat,true);
 
             int start = beg;
             int idx;
             boolean last_null = false;
             Region regs;
 
             while((end = ((RubyRegexp)spat).search(this, start, false)) >= 0) {
                 regs = ((RubyMatchData)getRuntime().getCurrentContext().getCurrentFrame().getBackRef()).regs;
                 if(start == end && regs.beg[0] == regs.end[0]) {
                     if(value.realSize == 0) {
                         result.append(newEmptyString(runtime, getMetaClass()));
                         break;
                     } else if(last_null) {
                         result.append(substr(beg, ((RubyRegexp)spat).getKCode().getEncoding().length(value.bytes[value.begin+beg])));
                         beg = start;
                     } else {
                         if(start < value.realSize) {
                             start += ((RubyRegexp)spat).getKCode().getEncoding().length(value.bytes[value.begin+start]);
                         } else {
                             start++;
                         }
 
                         last_null = true;
                         continue;
                     }
                 } else {
                     result.append(substr(beg,end-beg));
                     beg = start = regs.end[0];
                 }
                 last_null = false;
 
                 for(idx=1; idx < regs.numRegs; idx++) {
                     if(regs.beg[idx] == -1) {
                         continue;
                     }
                     if(regs.beg[idx] == regs.end[idx]) {
                         tmp = newEmptyString(runtime, getMetaClass());
                     } else {
                         tmp = substr(regs.beg[idx],regs.end[idx]-regs.beg[idx]);
                     }
                     result.append(tmp);
                 }
                 if(limit && lim <= ++i) {
                     break;
                 }
             }
             if (value.realSize > 0 && (limit || value.realSize > beg || lim < 0)) {
                 if (value.realSize == beg) {
                     result.append(newEmptyString(runtime, getMetaClass()));
                 } else {
                     result.append(substr(beg, value.realSize - beg));
                 }
             }
         } // regexp split
 
         if (!limit && lim == 0) {
             while (result.size() > 0 && ((RubyString)result.eltInternal(result.size() - 1)).value.realSize == 0)
                 result.pop();
         }
 
         return result;
     }
     
     /** get_pat
      * 
      */
     private final RubyRegexp getPat(IRubyObject pattern, boolean quote) {
         if (pattern instanceof RubyRegexp) {
             return (RubyRegexp)pattern;
         } else if (!(pattern instanceof RubyString)) {
             IRubyObject val = pattern.checkStringType();
             if(val.isNil()) throw getRuntime().newTypeError("wrong argument type " + pattern.getMetaClass() + " (expected Regexp)");
             pattern = val; 
         }
         
         RubyString strPattern = (RubyString)pattern;
         if (quote) pattern = RubyRegexp.quote(strPattern,(KCode)null);
 
         return RubyRegexp.newRegexp(pattern, 0, null);
     }
 
     /** rb_str_scan
      *
      */
     @JRubyMethod(name = "scan", required = 1, frame = true)
     public IRubyObject scan(IRubyObject arg, Block block) {
         IRubyObject result;
         int[] start = new int[]{0};
         IRubyObject match = getRuntime().getNil();
         RubyRegexp pattern = getPat(arg, true);
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         byte[]_p = value.bytes;
         int len = value.realSize;
         
         if(!block.isGiven()) {
             RubyArray ary = runtime.newArray();
             while(!(result = scan_once(pattern, start)).isNil()) {
                 match = context.getCurrentFrame().getBackRef();
                 ary.append(result);
             }
             context.getPreviousFrame().setBackRef(match);
             return ary;
         }
 
         while(!(result = scan_once(pattern, start)).isNil()) {
             match = context.getCurrentFrame().getBackRef();
             context.getPreviousFrame().setBackRef(match);
             if(match instanceof RubyMatchData) {
                 ((RubyMatchData)match).use();
             }
             block.yield(context,result);
             modifyCheck(_p,len);
             context.getPreviousFrame().setBackRef(match);
         }
         context.getPreviousFrame().setBackRef(match);
 
         return this;
     }
 
     /**
      * rb_enc_check
      */
     private Encoding encodingCheck(RubyRegexp pattern) {
         // For 1.9 compatibility, should check encoding compat between string and pattern
         return pattern.getKCode().getEncoding();
     }
 
     private IRubyObject scan_once(RubyRegexp pat, int[] start) {
         Encoding enc = encodingCheck(pat);
         Region regs;
         int i;
 
         if(pat.search(this, start[0], false) >= 0) {
             RubyMatchData match = (RubyMatchData)getRuntime().getCurrentContext().getCurrentFrame().getBackRef();
             regs = match.regs;
             if(regs.beg[0] == regs.end[0]) {
                 /*
                  * Always consume at least one character of the input string
                  */
                 if(value.realSize > regs.end[0]) {
                     start[0] = regs.end[0] + enc.length(value.bytes[value.begin+regs.end[0]]);
                 } else {
                     start[0] = regs.end[0] + 1;
                 }
             } else {
                 start[0] = regs.end[0];
             }
             if(regs.numRegs == 1) {
                 return RubyRegexp.nth_match(0,match);
             }
             RubyArray ary = getRuntime().newArray(regs.numRegs);
             for(i=1;i<regs.numRegs;i++) {
                 ary.append(RubyRegexp.nth_match(i,match));
             }
             return ary;
         }
         return getRuntime().getNil();
     }
     
     private final RubyString substr(Ruby runtime, String str, int beg, int len, boolean utf8) {
         if (utf8) {
             if (len == 0) return newEmptyString(runtime, getMetaClass());
             return new RubyString(runtime, getMetaClass(), new ByteList(toUTF(str.substring(beg, beg + len)), false));
         } else {
             return makeShared(beg, len);
         }
     }
     
     private final String toString(boolean utf8) {
         String str = toString();
         if (utf8) {
             try {
                 str = new String(ByteList.plain(str), "UTF8");
             } catch(Exception e){}
         }   
         return str;
     }
         
     private static final ByteList SPACE_BYTELIST = new ByteList(ByteList.plain(" "));
 
     
     private final IRubyObject justify(IRubyObject[]args, char jflag) {
         Ruby runtime = getRuntime();        
         Arity.scanArgs(runtime, args, 1, 1);
         
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
                 if ((buff[begin + len - 1] & 0xFF) == '\n') {
                     realSize--;
                     if (realSize > 0 && (buff[begin + realSize - 1] & 0xFF) == '\r') realSize--;
                     view(0, realSize);
                 } else if ((buff[begin + len - 1] & 0xFF) == '\r') {
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
             while (len > 0 && (buff[begin + len - 1] & 0xFF) == '\n') {
                 len--;
                 if (len > 0 && (buff[begin + len - 1] & 0xFF) == '\r') len--;
             }
             if (len < value.realSize) {
                 view(0, len);
                 return this;
             }
             return getRuntime().getNil();
         }
 
         if (rslen > len) return getRuntime().getNil();
         int newline = rs.value.bytes[rslen - 1] & 0xFF;
 
         if (rslen == 1 && newline == '\n') {
             buff = value.bytes;
             int realSize = value.realSize;
             if ((buff[begin + len - 1] & 0xFF) == '\n') {
                 realSize--;
                 if (realSize > 0 && (buff[begin + realSize - 1] & 0xFF) == '\r') realSize--;
                 view(0, realSize);
             } else if ((buff[begin + len - 1] & 0xFF) == '\r') {
                 realSize--;
                 view(0, realSize);
             } else {
                 modifyCheck();
                 return getRuntime().getNil();
             }
             return this;                
         }
 
         if ((buff[begin + len - 1] & 0xFF) == newline && rslen <= 1 || value.endsWith(rs.value)) {
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
 
     private final static boolean[] WHITESPACE = new boolean[256];
     static {
         WHITESPACE[((byte)' ')+128] = true;
         WHITESPACE[((byte)'\t')+128] = true;
         WHITESPACE[((byte)'\n')+128] = true;
         WHITESPACE[((byte)'\r')+128] = true;
         WHITESPACE[((byte)'\f')+128] = true;
     }
 
 
     /** rb_str_lstrip_bang
      */
     @JRubyMethod(name = "lstrip!")
     public IRubyObject lstrip_bang() {
         if (value.realSize == 0) return getRuntime().getNil();
         
         int i=0;
         while (i < value.realSize && WHITESPACE[value.bytes[value.begin+i]+128]) i++;
         
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
         while (i >= 0 && WHITESPACE[value.bytes[value.begin+i]+128]) i--;
 
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
     public IRubyObject delete(IRubyObject[] args) {
         RubyString str = strDup();
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
     public IRubyObject squeeze(IRubyObject[] args) {
         RubyString str = strDup();
         str.squeeze_bang(args);        
         return str;        
     }
 
     /** rb_str_squeeze_bang
      *
      */
     @JRubyMethod(name = "squeeze!", rest = true)
     public IRubyObject squeeze_bang(IRubyObject[] args) {
         if (value.realSize == 0) return getRuntime().getNil();
 
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
     @JRubyMethod(name = "tr", required = 2)
     public IRubyObject tr(IRubyObject src, IRubyObject repl) {
         RubyString str = strDup();
         str.tr_trans(src, repl, false);        
         return str;        
     }
     
     /** rb_str_tr_bang
     *
     */
     @JRubyMethod(name = "tr!", required = 2)
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
                         if (t.now > buf[t.p]) {
                             t.p++;
                             continue;
                         }
                         t.gen = 1;
                         t.max = buf[t.p++];
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
     @JRubyMethod(name = "tr_s", required = 2)
     public IRubyObject tr_s(IRubyObject src, IRubyObject repl) {
         RubyString str = strDup();
         str.tr_trans(src, repl, true);        
         return str;        
     }
 
     /** rb_str_tr_s_bang
      *
      */
     @JRubyMethod(name = "tr_s!", required = 2)
     public IRubyObject tr_s_bang(IRubyObject src, IRubyObject repl) {
         return tr_trans(src, repl, true);
     }
 
     /** rb_str_each_line
      *
      */
     @JRubyMethod(name = {"each_line", "each"}, required = 0, optional = 1, frame = true)
     public IRubyObject each_line(IRubyObject[] args, Block block) {
         byte newline;
         int p = value.begin;
         int pend = p + value.realSize;
         int s;
         int ptr = p;
         int len = value.realSize;
         int rslen;
         IRubyObject line;
         
 
         IRubyObject _rsep;
         if (Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 0) {
             _rsep = getRuntime().getGlobalVariables().get("$/");
         } else {
             _rsep = args[0];
         }
 
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if(_rsep.isNil()) {
             block.yield(tc, this);
             return this;
         }
         
         RubyString rsep = stringValue(_rsep);
         ByteList rsepValue = rsep.value;
         byte[] strBytes = value.bytes;
 
         rslen = rsepValue.realSize;
         
         if(rslen == 0) {
             newline = '\n';
         } else {
             newline = rsepValue.bytes[rsepValue.begin + rslen-1];
         }
 
         s = p;
         p+=rslen;
 
         for(; p < pend; p++) {
             if(rslen == 0 && strBytes[p] == '\n') {
                 if(strBytes[++p] != '\n') {
                     continue;
                 }
                 while(strBytes[p] == '\n') {
                     p++;
                 }
             }
             if(ptr<p && strBytes[p-1] == newline &&
                (rslen <= 1 || 
                 ByteList.memcmp(rsepValue.bytes, rsepValue.begin, rslen, strBytes, p-rslen, rslen) == 0)) {
                 line = RubyString.newStringShared(getRuntime(), getMetaClass(), this.value.makeShared(s-ptr, p-s));
                 line.infectBy(this);
                 block.yield(tc, line);
                 modifyCheck(strBytes,len);
                 s = p;
             }
         }
 
         if(s != pend) {
             if(p > pend) {
                 p = pend;
             }
             line = RubyString.newStringShared(getRuntime(), getMetaClass(), this.value.makeShared(s-ptr, p-s));
             line.infectBy(this);
             block.yield(tc, line);
         }
 
         return this;
     }
 
     /**
      * rb_str_each_byte
      */
     @JRubyMethod(name = "each_byte", frame = true)
     public RubyString each_byte(Block block) {
         int lLength = value.length();
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < lLength; i++) {
             block.yield(context, runtime.newFixnum(value.get(i) & 0xFF));
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
 
     public static RubyString unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyString result = newString(input.getRuntime(), input.unmarshalString());
         input.registerLinkTarget(result);
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#unpack
      */
     @JRubyMethod(name = "unpack", required = 1)
     public RubyArray unpack(IRubyObject obj) {
         return Pack.unpack(getRuntime(), this.value, stringValue(obj).value);
     }
 
     /**
      * Mutator for internal string representation.
      *
      * @param value The new java.lang.String this RubyString should encapsulate
      */
     public void setValue(CharSequence value) {
         view(ByteList.plain(value));
     }
 
     public void setValue(ByteList value) {
         view(value);
     }
 
+    public CharSequence getValue() {
+        return toString();
+    }
+
+    public String getUnicodeValue() {
+        try {
+            return new String(value.bytes,value.begin,value.realSize, "UTF8");
+        } catch (Exception e) {
+            throw new RuntimeException("Something's seriously broken with encodings", e);
+        }
+    }
+
     public static byte[] toUTF(String string) {
         try {
             return string.getBytes("UTF8");
         } catch (Exception e) {
             throw new RuntimeException("Something's seriously broken with encodings", e);
         }
     }
     
     public void setUnicodeValue(String newValue) {
         view(toUTF(newValue));
     }
 
     public byte[] getBytes() {
         return value.bytes();
     }
 
     public ByteList getByteList() {
         return value;
     }
 }
