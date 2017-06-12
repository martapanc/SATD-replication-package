diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 17d7a6f30b..1da7a535e9 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1409 +1,1408 @@
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
 
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.ChannelDescriptor;
 import java.io.File;
 import java.io.FileDescriptor;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.FileLock;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.DirectoryAsFileException;
 import org.jruby.util.io.Stream;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 
 /**
  * Ruby File class equivalent in java.
  **/
 public class RubyFile extends RubyIO {
     private static final long serialVersionUID = 1L;
     
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
     private static boolean startsWithDriveLetterOnWindows(String path) {
         return (path != null)
             && IS_WINDOWS && path.length() > 1
             && isWindowsDriveLetter(path.charAt(0))
             && path.charAt(1) == ':';
     }
     // adjusts paths started with '/' or '\\', on windows.
     static String adjustRootPathOnWindows(Ruby runtime, String path, String dir) {
         if (path == null) return path;
         if (IS_WINDOWS) {
             // MRI behavior on Windows: it treats '/' as a root of
             // a current drive (but only if SINGLE slash is present!):
             // E.g., if current work directory is
             // 'D:/home/directory', then '/' means 'D:/'.
             //
             // Basically, '/path' is treated as a *RELATIVE* path,
             // relative to the current drive. '//path' is treated
             // as absolute one.
             if (path.startsWith("/") || path.startsWith("\\")) {
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
         }
         return path;
     }
 
     protected String path;
     private FileLock currentLock;
     
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
             this.openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(in), getNewFileno(), new FileDescriptor())));
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         }
         this.openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         registerDescriptor(openFile.getMainStream().getDescriptor());
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
         fileClass.fastSetConstant("BINARY", runtime.newFixnum(ModeFlags.BINARY));
         fileClass.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         fileClass.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         fileClass.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_CASEFOLD));
         fileClass.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
         fileClass.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
         
         // Create constants for open flags
         fileClass.fastSetConstant("RDONLY", runtime.newFixnum(ModeFlags.RDONLY));
         fileClass.fastSetConstant("WRONLY", runtime.newFixnum(ModeFlags.WRONLY));
         fileClass.fastSetConstant("RDWR", runtime.newFixnum(ModeFlags.RDWR));
         fileClass.fastSetConstant("CREAT", runtime.newFixnum(ModeFlags.CREAT));
         fileClass.fastSetConstant("EXCL", runtime.newFixnum(ModeFlags.EXCL));
         fileClass.fastSetConstant("NOCTTY", runtime.newFixnum(ModeFlags.NOCTTY));
         fileClass.fastSetConstant("TRUNC", runtime.newFixnum(ModeFlags.TRUNC));
         fileClass.fastSetConstant("APPEND", runtime.newFixnum(ModeFlags.APPEND));
         fileClass.fastSetConstant("NONBLOCK", runtime.newFixnum(ModeFlags.NONBLOCK));
         
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
         constants.fastSetConstant("RDONLY", runtime.newFixnum(ModeFlags.RDONLY));
         constants.fastSetConstant("WRONLY", runtime.newFixnum(ModeFlags.WRONLY));
         constants.fastSetConstant("RDWR", runtime.newFixnum(ModeFlags.RDWR));
         constants.fastSetConstant("CREAT", runtime.newFixnum(ModeFlags.CREAT));
         constants.fastSetConstant("EXCL", runtime.newFixnum(ModeFlags.EXCL));
         constants.fastSetConstant("NOCTTY", runtime.newFixnum(ModeFlags.NOCTTY));
         constants.fastSetConstant("TRUNC", runtime.newFixnum(ModeFlags.TRUNC));
         constants.fastSetConstant("APPEND", runtime.newFixnum(ModeFlags.APPEND));
         constants.fastSetConstant("NONBLOCK", runtime.newFixnum(ModeFlags.NONBLOCK));
         
         // Create constants for flock
         constants.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         constants.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         constants.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         constants.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         runtime.getFileTest().extend_object(fileClass);
         
         fileClass.defineAnnotatedMethods(RubyFile.class);
         fileClass.dispatcher = callbackFactory.createDispatcher(fileClass);
         
         return fileClass;
     }
     
     public void openInternal(String newPath, ModeFlags newModes) {
         this.path = newPath;
         this.openFile.setMode(newModes.getOpenFileFlags());
         
-        // Ruby code frequently uses a platform check to choose "NUL:" on windows
-        // but since that check doesn't work well on JRuby, we help it out
-        if ("/dev/null".equals(path) && SafePropertyAccessor.getProperty("os.name").contains("Windows")) {
-            path = "NUL:";
-        }
-        
         try {
             if(newPath.startsWith("file:")) {
                 String filePath = path.substring(5, path.indexOf("!"));
                 String internalPath = path.substring(path.indexOf("!") + 2);
 
                 java.util.jar.JarFile jf;
                 try {
                     jf = new java.util.jar.JarFile(filePath);
                 } catch(IOException e) {
                     throw getRuntime().newErrnoENOENTError(
                                                            "File not found - " + newPath);
                 }
 
                 java.util.zip.ZipEntry zf = jf.getEntry(internalPath);
                 
                 if(zf == null) {
                     throw getRuntime().newErrnoENOENTError(
                                                            "File not found - " + newPath);
                 }
 
                 InputStream is = jf.getInputStream(zf);
                 openFile.setMainStream(
                         new ChannelStream(getRuntime(), new ChannelDescriptor(Channels.newChannel(is), getNewFileno(), new FileDescriptor())));
             } else {
                 try {
                     openFile.setMainStream(ChannelStream.fopen(getRuntime(), newPath, newModes));
                 } catch (BadDescriptorException e) {
                     throw getRuntime().newErrnoEBADFError();
                 } catch (FileExistsException fee) {
                     throw getRuntime().newErrnoEEXISTError(fee.getMessage());
                 }
             }
             
             registerDescriptor(openFile.getMainStream().getDescriptor());
         } catch (PipeException e) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (DirectoryAsFileException e) {
             throw getRuntime().newErrnoEISDirError();
         } catch (FileNotFoundException e) {
             // FNFException can be thrown in both cases, when the file
             // is not found, or when permission is denied.
             if (Ruby.isSecurityRestricted() || new File(newPath).exists()) {
                 throw getRuntime().newErrnoEACCESError(
                         "Permission denied - " + newPath);
             }
             throw getRuntime().newErrnoENOENTError(
                     "File not found - " + newPath);
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         } catch (SecurityException se) {
             throw getRuntime().newIOError(se.getMessage());
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
-        assert openFile.getMainStream().getDescriptor().isSeekable();
+        // TODO: port exact behavior from MRI, and move most locking logic into ChannelDescriptor
+        // TODO: for all LOCK_NB cases, return false if they would block
+        ChannelDescriptor descriptor = openFile.getMainStream().getDescriptor();
+        
+        // null channel always succeeds for all locking operations
+        if (descriptor.isNull()) return RubyFixnum.zero(getRuntime());
         
-        FileChannel fileChannel = (FileChannel)openFile.getMainStream().getDescriptor().getChannel();
+        FileChannel fileChannel = (FileChannel)descriptor.getChannel();
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
 
-                        return getRuntime().newFixnum(0);
+                        return RubyFixnum.zero(getRuntime());
                     }
                     break;
                 case LOCK_EX:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
                     currentLock = fileChannel.lock();
                     if (currentLock != null) {
-                        return getRuntime().newFixnum(0);
+                        return RubyFixnum.zero(getRuntime());
                     }
 
                     break;
                 case LOCK_EX | LOCK_NB:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
                     currentLock = fileChannel.tryLock();
                     if (currentLock != null) {
-                        return getRuntime().newFixnum(0);
+                        return RubyFixnum.zero(getRuntime());
                     }
 
                     break;
                 case LOCK_SH:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
 
                     currentLock = fileChannel.lock(0L, Long.MAX_VALUE, true);
                     if (currentLock != null) {
-                        return getRuntime().newFixnum(0);
+                        return RubyFixnum.zero(getRuntime());
                     }
 
                     break;
                 case LOCK_SH | LOCK_NB:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
 
                     currentLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true);
                     if (currentLock != null) {
-                        return getRuntime().newFixnum(0);
+                        return RubyFixnum.zero(getRuntime());
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
         if (openFile == null) {
             throw getRuntime().newRuntimeError("reinitializing File");
         }
         
         if (args.length > 0 && args.length < 3) {
             IRubyObject fd = TypeConverter.convertToTypeWithCheck(args[0], getRuntime().getFixnum(), MethodIndex.TO_INT, "to_int");
             if (!fd.isNil()) {
                 args[0] = fd;
                 return super.initialize(args, block);
             }
         }
 
         return openFile(args);
     }
     
     private IRubyObject openFile(IRubyObject args[]) {
         IRubyObject filename = args[0].convertToString();
         getRuntime().checkSafeString(filename);
         
         path = filename.toString();
         
         String modeString;
         ModeFlags modes;
         int perm;
         
         try {
             if ((args.length > 1 && args[1] instanceof RubyFixnum) || (args.length > 2 && !args[2].isNil())) {
                 if (args[1] instanceof RubyFixnum) {
                     modes = new ModeFlags(RubyNumeric.num2int(args[1]));
                 } else {
                     modeString = args[1].convertToString().toString();
                     modes = getIOModes(getRuntime(), modeString);
                 }
                 if (args.length > 2 && !args[2].isNil()) {
                     perm = RubyNumeric.num2int(args[2]);
                 } else {
                     perm = 438; // 0666
                 }
 
                 sysopenInternal(path, modes, perm);
             } else {
                 modeString = "r";
                 if (args.length > 1) {
                     if (!args[1].isNil()) {
                         modeString = args[1].convertToString().toString();
                     }
                 }
                 openInternal(path, modeString);
             }
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } finally {}
         
         return this;
     }
     
     private void sysopenInternal(String path, ModeFlags modes, int perm) throws InvalidValueException {
         openFile = new OpenFile();
         
         openFile.setPath(path);
         openFile.setMode(modes.getOpenFileFlags());
         
         ChannelDescriptor descriptor = sysopen(path, modes, perm);
         openFile.setMainStream(fdopen(descriptor, modes));
         
         registerDescriptor(descriptor);
     }
     
     private void openInternal(String path, String modeString) throws InvalidValueException {
         openFile = new OpenFile();
 
         openFile.setMode(getIOModes(getRuntime(), modeString).getOpenFileFlags());
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modeString));
         
         registerDescriptor(openFile.getMainStream().getDescriptor());
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
         } catch (FileNotFoundException fnfe) {
             throw getRuntime().newErrnoENOENTError();
         } catch (DirectoryAsFileException dafe) {
             throw getRuntime().newErrnoEISDirError();
         } catch (FileExistsException fee) {
             throw getRuntime().newErrnoEEXISTError("file exists: " + path);
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
         } catch (FileNotFoundException ex) {
             throw getRuntime().newErrnoENOENTError();
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
         }
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
         openFile.checkClosed(getRuntime());
         return getRuntime().newFileStat(path, false);
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject truncate(IRubyObject arg) {
         RubyInteger newLength = arg.convertToInteger();
         if (newLength.getLongValue() < 0) {
             throw getRuntime().newErrnoEINVALError("invalid argument: " + path);
         }
         try {
             openFile.checkWritable(getRuntime());
             openFile.getMainStream().ftruncate(newLength.getLongValue());
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (IOException e) {
             // Should we do anything?
         }
 
         return RubyFixnum.zero(getRuntime());
     }
 
     public String toString() {
         return "RubyFile(" + path + ", " + openFile.getMode() + ", " + openFile.getMainStream().getDescriptor().getFileno() + ")";
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
     public IRubyObject inspect() {
         StringBuffer val = new StringBuffer();
         val.append("#<File:").append(path);
         if(!openFile.isOpen()) {
             val.append(" (closed)");
         }
         val.append(">");
         return getRuntime().newString(val.toString());
     }
     
     /* File class methods */
     
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject basename(IRubyObject recv, IRubyObject[] args) {
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
         int minPathLength = 1;
         boolean trimmedSlashes = false;
 
         boolean startsWithDriveLetterOnWindows
                 = startsWithDriveLetterOnWindows(name);
 
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
                     return recv.getRuntime().newString(
                             jfilename.substring(0, 2) + ".");
                 } else {
                     return recv.getRuntime().newString(".");
                 }
             }
             if (index == 0) return recv.getRuntime().newString("/");
 
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
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject expand_path(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         
         String relativePath = RubyString.stringValue(args[0]).toString();
 
         boolean isAbsoluteWithFilePrefix = relativePath.startsWith("file:");
 
         String cwd = null;
         
         // Handle ~user paths 
         relativePath = expandUserPath(recv, relativePath);
         
         // If there's a second argument, it's the path to which the first 
         // argument is relative.
         if (args.length == 2 && !args[1].isNil()) {
             
             String cwdArg = RubyString.stringValue(args[1]).toString();
             
             // Handle ~user paths.
             cwd = expandUserPath(recv, cwdArg);
 
             cwd = adjustRootPathOnWindows(runtime, cwd, null);
 
             boolean startsWithSlashNotOnWindows = (cwd != null)
                     && !IS_WINDOWS && cwd.length() > 0
                     && cwd.charAt(0) == '/';
 
             // TODO: better detection when path is absolute or not.
             // If the path isn't absolute, then prepend the current working
             // directory to the path.
             if (!startsWithSlashNotOnWindows && !startsWithDriveLetterOnWindows(cwd)) {
                 cwd = new File(runtime.getCurrentDirectory(), cwd)
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
         if (!IS_WINDOWS) {
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
             // skip it
             if (canonicalPath != null && canonicalPath.length() == 0 ) canonicalPath += "/";
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
     public static IRubyObject fnmatch(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int flags;
         if (args.length == 3) {
             flags = RubyNumeric.num2int(args[2]);
         } else {
             flags = 0;
         }
 
         ByteList pattern = args[0].convertToString().getByteList();
         ByteList path = args[1].convertToString().getByteList();
 
         if (org.jruby.util.Dir.fnmatch(pattern.bytes, pattern.begin, pattern.begin+pattern.realSize, 
                                        path.bytes, path.begin, path.begin+path.realSize, flags) == 0) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
     
     @JRubyMethod(name = "ftype", required = 1, meta = true)
     public static IRubyObject ftype(IRubyObject recv, IRubyObject filename) {
         return recv.getRuntime().newFileStat(filename.convertToString().toString(), true).ftype();
     }
 
     private static String inspectJoin(IRubyObject recv, RubyArray parent, RubyArray array) {
         Ruby runtime = recv.getRuntime();
 
         // If already inspecting, there is no need to register/unregister again.
         if (runtime.isInspecting(parent)) {
             return join(recv, array).toString();
         }
 
         try {
             runtime.registerInspecting(parent);
             return join(recv, array).toString();
         } finally {
             runtime.unregisterInspecting(parent);
         }
     }
 
     private static RubyString join(IRubyObject recv, RubyArray ary) {
         IRubyObject[] args = ary.toJavaArray();
         boolean isTainted = false;
         StringBuffer buffer = new StringBuffer();
         Ruby runtime = recv.getRuntime();
         
         for (int i = 0; i < args.length; i++) {
             if (args[i].isTaint()) {
                 isTainted = true;
             }
             String element;
             if (args[i] instanceof RubyString) {
                 element = args[i].toString();
             } else if (args[i] instanceof RubyArray) {
                 if (runtime.isInspecting(args[i])) {
                     element = "[...]";
                 } else {
                     element = inspectJoin(recv, ary, ((RubyArray)args[i]));
                 }
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
     
     /*
      * Fixme:  This does not have exact same semantics as RubyArray.join, but they
      * probably could be consolidated (perhaps as join(args[], sep, doChomp)).
      */
     @JRubyMethod(rest = true, meta = true)
     public static RubyString join(IRubyObject recv, IRubyObject[] args) {
         return join(recv, RubyArray.newArrayNoCopyLight(recv.getRuntime(), args));
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
         String f = filename.convertToString().toString();
         if(f.startsWith("file:")) {
             f = f.substring(5, f.indexOf("!"));
         }
         return recv.getRuntime().newFileStat(f, true);
     }
 
     @JRubyMethod(name = "stat", required = 1, meta = true)
     public static IRubyObject stat(IRubyObject recv, IRubyObject filename) {
         String f = filename.convertToString().toString();
         if(f.startsWith("file:")) {
             f = f.substring(5, f.indexOf("!"));
         }
         return recv.getRuntime().newFileStat(f, false);
     }
 
     @JRubyMethod(name = "atime", required = 1, meta = true)
     public static IRubyObject atime(IRubyObject recv, IRubyObject filename) {
         String f = filename.convertToString().toString();
         if(f.startsWith("file:")) {
             f = f.substring(5, f.indexOf("!"));
         }
         return recv.getRuntime().newFileStat(f, false).atime();
     }
 
     @JRubyMethod(name = "ctime", required = 1, meta = true)
     public static IRubyObject ctime(IRubyObject recv, IRubyObject filename) {
         String f = filename.convertToString().toString();
         if(f.startsWith("file:")) {
             f = f.substring(5, f.indexOf("!"));
         }
         return recv.getRuntime().newFileStat(f, false).ctime();
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject lchmod(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         
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
         RubyString fromStr = RubyString.stringValue(from);
         RubyString toStr = RubyString.stringValue(to);
         try {
             if (recv.getRuntime().getPosix().link(
                     fromStr.toString(),toStr.toString()) == -1) {
                 // FIXME: When we get JNA3 we need to properly write this to errno.
                 throw recv.getRuntime().newErrnoEEXISTError("File exists - " 
                                + fromStr + " or " + toStr);
             }
         } catch (java.lang.UnsatisfiedLinkError ule) {
             throw recv.getRuntime().newNotImplementedError(
                     "link() function is unimplemented on this machine");
         }
         
         return recv.getRuntime().newFixnum(0);
     }
 
     @JRubyMethod(name = "mtime", required = 1, meta = true)
     public static IRubyObject mtime(IRubyObject recv, IRubyObject filename) {
         return getLastModified(recv.getRuntime(), filename.convertToString().toString());
     }
     
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, boolean tryToYield, Block block) {
         Ruby runtime = recv.getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         
         RubyFile file;
 
         if (args[0] instanceof RubyInteger) { // open with file descriptor
             file = new RubyFile(runtime, (RubyClass) recv);
             file.initialize(args, Block.NULL_BLOCK);            
         } else {
             RubyString pathString = RubyString.stringValue(args[0]);
             runtime.checkSafeString(pathString);
             String path = pathString.toString();
 
             ModeFlags modes;
             try {
                 modes = args.length >= 2 ? getModes(runtime, args[1]) : new ModeFlags(ModeFlags.RDONLY);
             } catch (InvalidValueException ex) {
                 throw runtime.newErrnoEINVALError();
             }
             file = new RubyFile(runtime, (RubyClass) recv);
 
             RubyInteger fileMode = args.length >= 3 ? args[2].convertToInteger() : null;
 
             file.openInternal(path, modes);
 
             if (fileMode != null) chmod(recv, new IRubyObject[] {fileMode, pathString});
         }
         
         if (tryToYield && block.isGiven()) {
             try {
                 return block.yield(tc, file);
             } finally {
                 if (file.openFile.isOpen()) {
                     file.close();
                 }
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
diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index 72d4742526..c70c3607d4 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,1491 +1,1488 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Evan Buswell <ebuswell@gmail.com>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import static java.lang.System.out;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.STDIO;
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.ChannelDescriptor;
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.lang.ref.Reference;
 import java.lang.ref.WeakReference;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.Pipe;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.Stream;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 
 /**
  * 
  * @author jpetersen
  */
 public class RubyIO extends RubyObject {
     protected OpenFile openFile;
     
     public void registerDescriptor(ChannelDescriptor descriptor) {
         getRuntime().getDescriptors().put(new Integer(descriptor.getFileno()), new WeakReference<ChannelDescriptor>(descriptor));
     }
     
     public void unregisterDescriptor(int aFileno) {
         getRuntime().getDescriptors().remove(new Integer(aFileno));
     }
     
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         Reference<ChannelDescriptor> reference = getRuntime().getDescriptors().get(new Integer(aFileno));
         if (reference == null) {
             return null;
         }
         return (ChannelDescriptor) reference.get();
     }
     
     // FIXME can't use static; would interfere with other runtimes in the same JVM
     protected static int filenoIndex = 2;
     
     public static int getNewFileno() {
         filenoIndex++;
         
         return filenoIndex;
     }
 
     // This should only be called by this and RubyFile.
     // It allows this object to be created without a IOHandler.
     public RubyIO(Ruby runtime, RubyClass type) {
         super(runtime, type);
         
         openFile = new OpenFile();
     }
 
     public RubyIO(Ruby runtime, OutputStream outputStream) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (outputStream == null) {
             throw runtime.newIOError("Opening invalid stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(outputStream), getNewFileno(), new FileDescriptor())));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.WRITABLE | OpenFile.APPEND);
         
         registerDescriptor(openFile.getMainStream().getDescriptor());
     }
     
     public RubyIO(Ruby runtime, InputStream inputStream) {
         super(runtime, runtime.getIO());
         
         if (inputStream == null) {
             throw runtime.newIOError("Opening invalid stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(inputStream), getNewFileno(), new FileDescriptor())));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.READABLE);
         
         registerDescriptor(openFile.getMainStream().getDescriptor());
     }
     
     public RubyIO(Ruby runtime, Channel channel) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (channel == null) {
             throw runtime.newIOError("Opening invalid stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(channel, getNewFileno(), new FileDescriptor())));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         
         registerDescriptor(openFile.getMainStream().getDescriptor());
     }
 
     public RubyIO(Ruby runtime, Process process, ModeFlags modes) {
     	super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         
         openFile.setMode(modes.getOpenFileFlags() | OpenFile.SYNC);
         openFile.setProcess(process);
 
         try {
             InputStream pipeIn = process.getInputStream();
             ChannelDescriptor main = new ChannelDescriptor(
                     Channels.newChannel(pipeIn),
                     getNewFileno(),
                     new FileDescriptor());
             
             OutputStream pipeOut = process.getOutputStream();
             ChannelDescriptor pipe = new ChannelDescriptor(
                     Channels.newChannel(pipeOut),
                     getNewFileno(),
                     new FileDescriptor());
             
             if (!openFile.isReadable()) {
                 main.close();
                 pipeIn.close();
             } else {
                 openFile.setMainStream(new ChannelStream(getRuntime(), main));
             }
             
             if (!openFile.isWritable()) {
                 pipe.close();
                 pipeOut.close();
             } else {
                 if (openFile.getMainStream() != null) {
                     openFile.setPipeStream(new ChannelStream(getRuntime(), pipe));
                 } else {
                     openFile.setMainStream(new ChannelStream(getRuntime(), pipe));
                 }
             }
             
             registerDescriptor(main);
             registerDescriptor(pipe);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
     
     public RubyIO(Ruby runtime, STDIO stdio) {
         super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
 
         try {
             switch (stdio) {
             case IN:
                 openFile.setMainStream(
                         new ChannelStream(
                             runtime, 
                             // special constructor that accepts stream, not channel
                             new ChannelDescriptor(runtime.getIn(), 0, new ModeFlags(ModeFlags.RDONLY), FileDescriptor.in),
                             FileDescriptor.in));
                 break;
             case OUT:
                 openFile.setMainStream(
                         new ChannelStream(
                             runtime, 
                             new ChannelDescriptor(Channels.newChannel(runtime.getOut()), 1, new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out),
                             FileDescriptor.out));
                 openFile.getMainStream().setSync(true);
                 break;
             case ERR:
                 openFile.setMainStream(
                         new ChannelStream(
                             runtime, 
                             new ChannelDescriptor(Channels.newChannel(runtime.getErr()), 2, new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out), 
                             FileDescriptor.err));
                 openFile.getMainStream().setSync(true);
                 break;
             }
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         
         registerDescriptor(openFile.getMainStream().getDescriptor());        
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
 
     public static RubyClass createIOClass(Ruby runtime) {
         RubyClass ioClass = runtime.defineClass("IO", runtime.getObject(), IO_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyIO.class);   
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
         ioClass.fastSetConstant("SEEK_SET", runtime.newFixnum(Stream.SEEK_SET));
         ioClass.fastSetConstant("SEEK_CUR", runtime.newFixnum(Stream.SEEK_CUR));
         ioClass.fastSetConstant("SEEK_END", runtime.newFixnum(Stream.SEEK_END));
         
         ioClass.dispatcher = callbackFactory.createDispatcher(ioClass);
 
         return ioClass;
     }
 
     public OutputStream getOutStream() {
         return getOpenFileChecked().getMainStream().newOutputStream();
     }
 
     public InputStream getInStream() {
         return getOpenFileChecked().getMainStream().newInputStream();
     }
 
     public Channel getChannel() {
         if (getOpenFileChecked().getMainStream() instanceof ChannelStream) {
             return ((ChannelStream) openFile.getMainStream()).getDescriptor().getChannel();
         } else {
             return null;
         }
     }
     
     public Stream getHandler() {
         return getOpenFileChecked().getMainStream();
     }
 
     @JRubyMethod(name = "reopen", required = 1, optional = 1)
     public IRubyObject reopen(IRubyObject[] args) throws InvalidValueException {
     	if (args.length < 1) {
             throw getRuntime().newArgumentError("wrong number of arguments");
     	}
     	
     	IRubyObject tmp = TypeConverter.convertToTypeWithCheck(args[0],
     	        getRuntime().getIO(), MethodIndex.getIndex("to_io"), "to_io");
     	if (!tmp.isNil()) {
             try {
                 RubyIO ios = (RubyIO) tmp;
 
                 if (ios.openFile == this.openFile) {
                     return this;
                 }
 
                 OpenFile originalFile = ios.getOpenFileChecked();
                 OpenFile selfFile = getOpenFileChecked();
 
                 long pos = 0;
                 if (originalFile.isReadable()) {
                     pos = originalFile.getMainStream().fgetpos();
                 }
 
                 if (originalFile.getPipeStream() != null) {
                     originalFile.getPipeStream().fflush();
                 } else if (originalFile.isWritable()) {
                     originalFile.getMainStream().fflush();
                 }
 
                 if (selfFile.isWritable()) {
                     selfFile.getWriteStream().fflush();
                 }
 
                 selfFile.setMode(originalFile.getMode());
                 selfFile.setProcess(originalFile.getProcess());
                 selfFile.setLineNumber(originalFile.getLineNumber());
                 selfFile.setPath(originalFile.getPath());
                 selfFile.setFinalizer(originalFile.getFinalizer());
 
                 ChannelDescriptor selfDescriptor = selfFile.getMainStream().getDescriptor();
                 ChannelDescriptor originalDescriptor = originalFile.getMainStream().getDescriptor();
 
                 // confirm we're not reopening self's channel
                 if (selfDescriptor.getChannel() != originalDescriptor.getChannel()) {
                     // check if we're a stdio IO, and ensure we're not badly mutilated
                     if (selfDescriptor.getFileno() >=0 && selfDescriptor.getFileno() <= 2) {
                         selfFile.getMainStream().clearerr();
                         
                         // dup2 new fd into self to preserve fileno and references to it
                         originalDescriptor.dup2(selfDescriptor);
                         
                         // re-register, since fileno points at something new now
                         registerDescriptor(selfFile.getMainStream().getDescriptor());
                     } else {
                         Stream pipeFile = selfFile.getPipeStream();
                         int mode = selfFile.getMode();
                         selfFile.getMainStream().fclose();
                         selfFile.setPipeStream(null);
 
                         // TODO: turn off readable? am I reading this right?
                         // This only seems to be used while duping below, since modes gets
                         // reset to actual modes afterward
                         //fptr->mode &= (m & FMODE_READABLE) ? ~FMODE_READABLE : ~FMODE_WRITABLE;
 
                         if (pipeFile != null) {
                             selfFile.setMainStream(ChannelStream.fdopen(getRuntime(), originalDescriptor, new ModeFlags()));
                             selfFile.setPipeStream(pipeFile);
                         } else {
                             selfFile.setMainStream(
                                     new ChannelStream(
                                         getRuntime(),
                                         originalDescriptor.dup2(selfDescriptor.getFileno())));
                             
                             // re-register the descriptor
                             registerDescriptor(selfFile.getMainStream().getDescriptor());
                             
                             // since we're not actually duping the incoming channel into our handler, we need to
                             // copy the original sync behavior from the other handler
                             selfFile.getMainStream().setSync(selfFile.getMainStream().isSync());
                         }
                         selfFile.setMode(mode);
                     }
                     // TODO: anything threads attached to original fd are notified of the close...
                     // see rb_thread_fd_close
                     
                     if (originalFile.isReadable() && pos >= 0) {
                         selfFile.seek(pos, Stream.SEEK_SET);
                         originalFile.seek(pos, Stream.SEEK_SET);
                     }
                 }
 
                 // TODO: more pipe logic
     //            if (fptr->f2 && fd != fileno(fptr->f2)) {
     //                fd = fileno(fptr->f2);
     //                if (!orig->f2) {
     //                    fclose(fptr->f2);
     //                    rb_thread_fd_close(fd);
     //                    fptr->f2 = 0;
     //                }
     //                else if (fd != (fd2 = fileno(orig->f2))) {
     //                    fclose(fptr->f2);
     //                    rb_thread_fd_close(fd);
     //                    if (dup2(fd2, fd) < 0)
     //                        rb_sys_fail(orig->path);
     //                    fptr->f2 = rb_fdopen(fd, "w");
     //                }
     //            }
                 
                 // TODO: restore binary mode
     //            if (fptr->mode & FMODE_BINMODE) {
     //                rb_io_binmode(io);
     //            }
                 
                 // TODO: set our metaclass to target's class (i.e. scary!)
 
             } catch (IOException ex) { // TODO: better error handling
                 throw getRuntime().newIOError("could not reopen: " + ex.getMessage());
             } catch (BadDescriptorException ex) {
                 throw getRuntime().newIOError("could not reopen: " + ex.getMessage());
             } catch (PipeException ex) {
                 throw getRuntime().newIOError("could not reopen: " + ex.getMessage());
             }
         } else {
             IRubyObject pathString = args[0].convertToString();
             
             // TODO: check safe, taint on incoming string
             
             if (openFile == null) {
                 openFile = new OpenFile();
             }
             
             try {
                 ModeFlags modes;
                 if (args.length > 1) {
                     IRubyObject modeString = args[1].convertToString();
                     modes = getIOModes(getRuntime(), modeString.toString());
 
                     openFile.setMode(modes.getOpenFileFlags());
                 } else {
                     modes = getIOModes(getRuntime(), "r");
                 }
 
                 String path = pathString.toString();
                 
                 // Ruby code frequently uses a platform check to choose "NUL:" on windows
                 // but since that check doesn't work well on JRuby, we help it out
-                if ("/dev/null".equals(path) && System.getProperty("os.name").contains("Windows")) {
-                    path = "NUL:";
-                }
                 
                 openFile.setPath(path);
             
                 if (openFile.getMainStream() == null) {
                     try {
                         openFile.setMainStream(ChannelStream.fopen(getRuntime(), path, modes));
                     } catch (FileExistsException fee) {
                         throw getRuntime().newErrnoEEXISTError(path);
                     }
                     
                     registerDescriptor(openFile.getMainStream().getDescriptor());
                     if (openFile.getPipeStream() != null) {
                         openFile.getPipeStream().fclose();
                         unregisterDescriptor(openFile.getPipeStream().getDescriptor().getFileno());
                         openFile.setPipeStream(null);
                     }
                     return this;
                 } else {
                     // TODO: This is an freopen in MRI, this is close, but not quite the same
                     openFile.getMainStream().freopen(path, getIOModes(getRuntime(), openFile.getModeAsString(getRuntime())));
 
                     // re-register
                     registerDescriptor(openFile.getMainStream().getDescriptor());
 
                     if (openFile.getPipeStream() != null) {
                         // TODO: pipe handler to be reopened with path and "w" mode
                     }
                 }
             } catch (PipeException pe) {
                 throw getRuntime().newErrnoEPIPEError();
             } catch (IOException ex) {
                 throw getRuntime().newIOErrorFromException(ex);
             } catch (BadDescriptorException ex) {
                 throw getRuntime().newErrnoEBADFError();
             } catch (InvalidValueException e) {
             	throw getRuntime().newErrnoEINVALError();
             }
         }
         
         // A potentially previously close IO is being 'reopened'.
         return this;
     }
     
     public static ModeFlags getIOModes(Ruby runtime, String modesString) throws InvalidValueException {
         return new ModeFlags(getIOModesIntFromString(runtime, modesString));
     }
         
     public static int getIOModesIntFromString(Ruby runtime, String modesString) {
         int modes = 0;
         int length = modesString.length();
 
         if (length == 0) {
             throw runtime.newArgumentError("illegal access mode");
         }
 
         switch (modesString.charAt(0)) {
         case 'r' :
             modes |= ModeFlags.RDONLY;
             break;
         case 'a' :
             modes |= ModeFlags.APPEND | ModeFlags.WRONLY | ModeFlags.CREAT;
             break;
         case 'w' :
             modes |= ModeFlags.WRONLY | ModeFlags.TRUNC | ModeFlags.CREAT;
             break;
         default :
             throw runtime.newArgumentError("illegal access mode " + modes);
         }
 
         for (int n = 1; n < length; n++) {
             switch (modesString.charAt(n)) {
             case 'b':
                 modes |= ModeFlags.BINARY;
                 break;
             case '+':
                 modes = (modes & ~ModeFlags.ACCMODE) | ModeFlags.RDWR;
                 break;
             default:
                 throw runtime.newArgumentError("illegal access mode " + modes);
             }
         }
 
         return modes;
     }
 
     private static ByteList getSeparatorFromArgs(Ruby runtime, IRubyObject[] args, int idx) {
         IRubyObject sepVal;
 
         if (args.length > idx) {
             sepVal = args[idx];
         } else {
             sepVal = runtime.getRecordSeparatorVar().get();
         }
 
         ByteList separator = sepVal.isNil() ? null : sepVal.convertToString().getByteList();
 
         if (separator != null && separator.realSize == 0) {
             separator = Stream.PARAGRAPH_DELIMETER;
         }
 
         return separator;
     }
 
     private ByteList getSeparatorForGets(IRubyObject[] args) {
         return getSeparatorFromArgs(getRuntime(), args, 0);
     }
 
     public IRubyObject getline(ByteList separator) {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
             boolean isParagraph = separator == Stream.PARAGRAPH_DELIMETER;
             separator = (separator == Stream.PARAGRAPH_DELIMETER) ?
                     Stream.PARAGRAPH_SEPARATOR : separator;
             
             if (isParagraph) {
                 swallow('\n');
             }
             
             if (separator == null) {
                 IRubyObject str = readAll(null);
                 if (((RubyString)str).getByteList().length() == 0) {
                     return getRuntime().getNil();
                 }
                 incrementLineno(myOpenFile);
                 return str;
             } else if (separator.length() == 1) {
                 return getlineFast(separator.get(0));
             } else {
                 Stream readStream = myOpenFile.getMainStream();
                 int c = -1;
                 int newline = separator.get(separator.length() - 1) & 0xFF;
 
                 ByteList buf = new ByteList(1024);
                 boolean update = false;
                 
                 while (true) {
                     do {
                         readCheck(readStream);
                         readStream.clearerr();
 
                         try {
                             c = readStream.fgetc();
                         } catch (EOFException e) {
                             c = -1;
                         }
 
                         if (c == -1) {
                             // TODO: clear error, wait for it to become readable
                             break;
                         }
                         
                         buf.append(c);
             
                         update = true;
                     } while (c != newline); // loop until we see the nth separator char
                     
                     // if we hit EOF, we're done
                     if (c == -1) {
                         break;
                     }
                     
                     // if we've found the last char of the separator,
                     // and we've found at least as many characters as separator length,
                     // and the last n characters of our buffer match the separator, we're done
                     if (c == newline && buf.length() >= separator.length() &&
                             0 == ByteList.memcmp(buf.unsafeBytes(), buf.begin + buf.realSize - separator.length(), separator.unsafeBytes(), separator.begin, separator.realSize)) {
                         break;
                     }
                 }
                 
                 if (isParagraph) {
                     if (c != -1) {
                         swallow('\n');
                     }
                 }
                 
                 if (!update) {
                     return getRuntime().getNil();
                 } else {
                     incrementLineno(myOpenFile);
                     RubyString str = RubyString.newString(getRuntime(), buf);
                     str.setTaint(true);
 
                     return str;
                 }
             }
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (EOFException e) {
             return getRuntime().getNil();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     private void incrementLineno(OpenFile myOpenFile) {
         Ruby runtime = getRuntime();
         int lineno = myOpenFile.getLineNumber() + 1;
         myOpenFile.setLineNumber(lineno);
         runtime.getGlobalVariables().set("$.", runtime.newFixnum(lineno));
         // this is for a range check, near as I can tell
         RubyNumeric.int2fix(getRuntime(), myOpenFile.getLineNumber());
     }
 
     protected boolean swallow(int term) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStream();
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
     
     public IRubyObject getlineFast(int delim) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStream();
         int c = -1;
 
         ByteList buf = new ByteList(1024);
         boolean update = false;
         do {
             readCheck(readStream);
             readStream.clearerr();
 
             try {
                 c = readStream.fgetc();
             } catch (EOFException e) {
                 c = -1;
             }
 
             if (c == -1) {
                 // TODO: clear error, wait for it to become readable
                 break;
             }
             
             buf.append(c);
             
             update = true;
         } while (c != delim);
 
         if (!update) {
             return getRuntime().getNil();
         } else {
             incrementLineno(openFile);
             RubyString str = RubyString.newString(getRuntime(), buf);
             str.setTaint(true);
 
             return str;
         }
     }
     // IO class methods.
 
     @JRubyMethod(name = {"new", "for_fd"}, rest = true, frame = true, meta = true)
     public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass)recv;
         
         if (block.isGiven()) {
             String className = klass.getName();
             recv.getRuntime().getWarnings().warn(
                     ID.BLOCK_NOT_ACCEPTED,
                     className + "::new() does not take block; use " + className + "::open() instead",
                     className + "::open()");
         }
         
         return klass.newInstance(args, block);
     }
 
     @JRubyMethod(name = "initialize", required = 1, optional = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         int argCount = args.length;
         ModeFlags modes;
         
         int fileno = RubyNumeric.fix2int(args[0]);
         
         try {
             ChannelDescriptor descriptor = getDescriptorByFileno(fileno);
             
             if (descriptor == null) {
                 throw getRuntime().newErrnoEBADFError();
             }
             
             descriptor.checkOpen();
             
             if (argCount == 2) {
                 if (args[1] instanceof RubyFixnum) {
                     modes = new ModeFlags(RubyFixnum.fix2long(args[1]));
                 } else {
                     modes = getIOModes(getRuntime(), (args[1].convertToString().toString()));
                 }
             } else {
                 // use original modes
                 modes = descriptor.getOriginalModes();
             }
 
             openFile.setMode(modes.getOpenFileFlags());
         
             openFile.setMainStream(fdopen(descriptor, modes));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ive) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         return this;
     }
     
     protected Stream fdopen(ChannelDescriptor existingDescriptor, ModeFlags modes) throws InvalidValueException {
         // See if we already have this descriptor open.
         // If so then we can mostly share the handler (keep open
         // file, but possibly change the mode).
         
         if (existingDescriptor == null) {
             // redundant, done above as well
             
             // this seems unlikely to happen unless it's a totally bogus fileno
             // ...so do we even need to bother trying to create one?
             
             // IN FACT, we should probably raise an error, yes?
             throw getRuntime().newErrnoEBADFError();
             
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
             return ChannelStream.fdopen(getRuntime(), existingDescriptor, modes);
         }
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, meta = true)
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyClass klass = (RubyClass)recv;
         
         RubyIO io = (RubyIO)klass.newInstance(args, block);
 
         if (block.isGiven()) {
             try {
                 return block.yield(runtime.getCurrentContext(), io);
             } finally {
                 try {
                     io.getMetaClass().invoke(runtime.getCurrentContext(), io, "close", IRubyObject.NULL_ARRAY, CallType.FUNCTIONAL, Block.NULL_BLOCK);
                 } catch (RaiseException re) {
                     RubyException rubyEx = re.getException();
                     if (rubyEx.kind_of_p(runtime.getStandardError()).isTrue()) {
                         // MRI behavior: swallow StandardErorrs
                     } else {
                         throw re;
                     }
                 }
             }
         }
 
         return io;
     }
 
     // This appears to be some windows-only mode.  On a java platform this is a no-op
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
             return this;
     }
     
     protected void checkInitialized() {
         if (openFile == null) {
             throw getRuntime().newIOError("uninitialized stream");
         }
     }
     
     protected void checkClosed() {
         if (openFile.getMainStream() == null && openFile.getPipeStream() == null) {
             throw getRuntime().newIOError("closed stream");
         }
     }
     
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(IRubyObject obj) {
         try {
             RubyString string = obj.asString();
 
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(getRuntime());
             
             Stream writeStream = myOpenFile.getWriteStream();
             
             if (myOpenFile.isWriteBuffered()) {
                 getRuntime().getWarnings().warn(
                         ID.SYSWRITE_BUFFERED_IO,
                         "syswrite for buffered IO");
             }
             
             if (!writeStream.getDescriptor().isWritable()) {
                 myOpenFile.checkClosed(getRuntime());
             }
             
             int read = writeStream.getDescriptor().write(string.getByteList());
             
             if (read == -1) {
                 // TODO? I think this ends up propagating from normal Java exceptions
                 // sys_fail(openFile.getPath())
             }
             
             return getRuntime().newFixnum(read);
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             e.printStackTrace();
             throw getRuntime().newSystemCallError(e.getMessage());
         }
     }
     
     @JRubyMethod(name = "write_nonblock", required = 1)
     public IRubyObject write_nonblock(IRubyObject obj) {
         // TODO: Obviously, we're not doing a non-blocking write here
         return write(obj);
     }
     
     /** io_write
      * 
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(IRubyObject obj) {
         getRuntime().secure(4);
         
         RubyString str = obj.asString();
 
         // TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
         
         if (str.getByteList().length() == 0) {
             return getRuntime().newFixnum(0);
         }
 
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(getRuntime());
 
             int written = fwrite(str.getByteList());
 
             if (written == -1) {
                 // TODO: sys fail
             }
 
             // if not sync, we switch to write buffered mode
             if (!myOpenFile.isSync()) {
                 myOpenFile.setWriteBuffered();
             }
 
             return getRuntime().newFixnum(written);
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         }
     }
     
     protected int fwrite(ByteList buffer) {
         int n, r, l, offset = 0;
         Stream writeStream = openFile.getWriteStream();
 
         int len = buffer.length();
         
 //        if ((n = len) <= 0) return n;
         if (len == 0) return 0;
         
         try {
             if (openFile.isSync()) {
                 openFile.fflush(writeStream);
 
                 // TODO: why is this guarded?
     //            if (!rb_thread_fd_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //            }
                 // TODO: loop until it's all written
                 //while (offset < len) {
                     writeStream.getDescriptor().write(buffer);
                 //}
                 return len;
 
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
     public IRubyObject op_append(IRubyObject anObject) {
         // Claims conversion is done via 'to_s' in docs.
         callMethod(getRuntime().getCurrentContext(), "write", anObject);
         
         return this; 
     }
 
     @JRubyMethod(name = "fileno", alias = "to_i")
     public RubyFixnum fileno() {
         return getRuntime().newFixnum(getOpenFileChecked().getMainStream().getDescriptor().getFileno());
     }
     
     /** Returns the current line number.
      * 
      * @return the current line number.
      */
     @JRubyMethod(name = "lineno")
     public RubyFixnum lineno() {
         return getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Sets the current line number.
      * 
      * @param newLineNumber The new line number.
      */
     @JRubyMethod(name = "lineno=", required = 1)
     public RubyFixnum lineno_set(IRubyObject newLineNumber) {
         getOpenFileChecked().setLineNumber(RubyNumeric.fix2int(newLineNumber));
 
         return getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Returns the current sync mode.
      * 
      * @return the current sync mode.
      */
     @JRubyMethod(name = "sync")
     public RubyBoolean sync() {
         return getRuntime().newBoolean(getOpenFileChecked().getMainStream().isSync());
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
     public IRubyObject pid() {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (myOpenFile.getProcess() == null) {
             return getRuntime().getNil();
         }
         
         // Of course this isn't particularly useful.
         int pid = myOpenFile.getProcess().hashCode();
         
         return getRuntime().newFixnum(pid); 
     }
     
     /**
      * @deprecated
      * @return
      */
     public boolean writeDataBuffered() {
         return openFile.getMainStream().writeDataBuffered();
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos() {
         try {
             return getRuntime().newFixnum(getOpenFileChecked().getMainStream().fgetpos());
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException bde) {
             throw getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
     
     @JRubyMethod(name = "pos=", required = 1)
     public RubyFixnum pos_set(IRubyObject newPosition) {
         long offset = RubyNumeric.num2long(newPosition);
 
         if (offset < 0) {
             throw getRuntime().newSystemCallError("Negative seek offset");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.getMainStream().fseek(offset, Stream.SEEK_SET);
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return (RubyFixnum) newPosition;
     }
     
     /** Print some objects to the stream.
      * 
      */
     @JRubyMethod(name = "print", rest = true)
     public IRubyObject print(IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getCurrentContext().getCurrentFrame().getLastLine() };
         }
 
         IRubyObject fs = getRuntime().getGlobalVariables().get("$,");
         IRubyObject rs = getRuntime().getGlobalVariables().get("$\\");
         ThreadContext context = getRuntime().getCurrentContext();
         
         for (int i = 0; i < args.length; i++) {
             if (i > 0 && !fs.isNil()) {
                 callMethod(context, "write", fs);
             }
             if (args[i].isNil()) {
                 callMethod(context, "write", getRuntime().newString("nil"));
             } else {
                 callMethod(context, "write", args[i]);
             }
         }
         if (!rs.isNil()) {
             callMethod(context, "write", rs);
         }
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "printf", required = 1, rest = true)
     public IRubyObject printf(IRubyObject[] args) {
         callMethod(getRuntime().getCurrentContext(), "write", RubyKernel.sprintf(this, args));
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "putc", required = 1)
     public IRubyObject putc(IRubyObject object) {
         int c;
         
         if (getRuntime().getString().isInstance(object)) {
             String value = ((RubyString) object).toString();
             
             if (value.length() > 0) {
                 c = value.charAt(0);
             } else {
                 throw getRuntime().newTypeError("Cannot convert String to Integer");
             }
         } else if (getRuntime().getFixnum().isInstance(object)){
             c = RubyNumeric.fix2int(object);
         } else { // What case will this work for?
             c = RubyNumeric.fix2int(object.callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_I, "to_i"));
         }
 
         try {
             getOpenFileChecked().getMainStream().fputc(c);
         } catch (BadDescriptorException e) {
             return RubyFixnum.zero(getRuntime());
         } catch (IOException e) {
             return RubyFixnum.zero(getRuntime());
         }
         
         return object;
     }
     
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "seek", required = 1, optional = 1)
     public RubyFixnum seek(IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.seek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return RubyFixnum.zero(getRuntime());
     }
     
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "sysseek", required = 1, optional = 1)
     public RubyFixnum sysseek(IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         long pos;
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             
             if (myOpenFile.isReadable() && myOpenFile.isReadBuffered()) {
                 throw getRuntime().newIOError("sysseek for buffered IO");
             }
             if (myOpenFile.isWritable() && myOpenFile.isWriteBuffered()) {
                 getRuntime().getWarnings().warn(
                         ID.SYSSEEK_BUFFERED_IO,
                         "sysseek for buffered IO");
             }
             
             pos = myOpenFile.getMainStream().getDescriptor().lseek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return getRuntime().newFixnum(pos);
     }
 
     @JRubyMethod(name = "rewind")
     public RubyFixnum rewind() {
         OpenFile myOpenfile = getOpenFileChecked();
         
         try {
             myOpenfile.getMainStream().fseek(0L, Stream.SEEK_SET);
             myOpenfile.getMainStream().clearerr();
             
             // TODO: This is some goofy global file value from MRI..what to do?
 //            if (io == current_file) {
 //                gets_lineno -= fptr->lineno;
 //            }
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
 
         // Must be back on first line on rewind.
         myOpenfile.setLineNumber(0);
         
         return RubyFixnum.zero(getRuntime());
     }
     
     @JRubyMethod(name = "fsync")
     public RubyFixnum fsync() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(getRuntime());
         
             myOpenFile.getWriteStream().sync();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
 
         return RubyFixnum.zero(getRuntime());
     }
 
     /** Sets the current sync mode.
      * 
      * @param newSync The new sync mode.
      */
     @JRubyMethod(name = "sync=", required = 1)
     public IRubyObject sync_set(IRubyObject newSync) {
         getOpenFileChecked().getMainStream().setSync(newSync.isTrue());
 
         return this;
     }
 
     @JRubyMethod(name = {"eof?", "eof"})
     public RubyBoolean eof_p() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
             
             if (myOpenFile.getMainStream().feof()) {
                 return getRuntime().getTrue();
             }
             
             if (myOpenFile.getMainStream().readDataBuffered()) {
                 return getRuntime().getFalse();
             }
             
             readCheck(myOpenFile.getMainStream());
             
             myOpenFile.getMainStream().clearerr();
             
             int c = myOpenFile.getMainStream().fgetc();
             
             if (c != -1) {
                 myOpenFile.getMainStream().ungetc(c);
                 return getRuntime().getFalse();
             }
             
             myOpenFile.checkClosed(getRuntime());
             
             myOpenFile.getMainStream().clearerr();
             
             return getRuntime().getTrue();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = {"tty?", "isatty"})
     public RubyBoolean tty_p() {
         return getRuntime().newBoolean(getRuntime().getPosix().isatty(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor()));
     }
     
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         if (this == original) return this;
 
         RubyIO originalIO = (RubyIO) TypeConverter.convertToTypeWithCheck(original, getRuntime().getIO(), MethodIndex.TO_IO, "to_io");
         
         OpenFile originalFile = originalIO.getOpenFileChecked();
         OpenFile newFile = openFile;
         
         try {
             // TODO: I didn't see where MRI has this check, but it seems to be the right place
             originalFile.checkClosed(getRuntime());
             
             if (originalFile.getPipeStream() != null) {
                 originalFile.getPipeStream().fflush();
                 originalFile.getMainStream().fseek(0, Stream.SEEK_CUR);
             } else if (originalFile.isWritable()) {
                 originalFile.getMainStream().fflush();
             } else {
                 originalFile.getMainStream().fseek(0, Stream.SEEK_CUR);
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
 
             newFile.setMainStream(ChannelStream.fdopen(getRuntime(), descriptor, modes));
             
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
             throw getRuntime().newIOError("could not init copy: " + ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newIOError("could not init copy: " + ex);
         } catch (PipeException ex) {
diff --git a/src/org/jruby/util/io/ChannelDescriptor.java b/src/org/jruby/util/io/ChannelDescriptor.java
index ea4aa1dac8..f68034fd23 100644
--- a/src/org/jruby/util/io/ChannelDescriptor.java
+++ b/src/org/jruby/util/io/ChannelDescriptor.java
@@ -1,661 +1,669 @@
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
 
 import java.io.File;
 import java.io.FileDescriptor;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.RandomAccessFile;
 import java.nio.ByteBuffer;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.WritableByteChannel;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.jar.JarFile;
 import static java.util.logging.Logger.getLogger;
 import java.util.zip.ZipEntry;
 import org.jruby.RubyIO;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyFile;
 import static org.jruby.util.io.ModeFlags.*;
 
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
     /** Whether to log debugging information */
     private static final boolean DEBUG = false;
     
     /** The java.nio.channels.Channel this descriptor wraps. */
     private Channel channel;
     /**
      * The file number (equivalent to the int file descriptor value in POSIX)
      * for this descriptor.
      */
     private final int fileno;
     /** The java.io.FileDescriptor object for this descriptor. */
     private final FileDescriptor fileDescriptor;
     /**
      * The original org.jruby.util.io.ModeFlags with which the specified
      * channel was opened.
      */
     private final ModeFlags originalModes;
     /** 
      * The reference count for the provided channel.
      * Only counts references through ChannelDescriptor instances.
      */
     private final AtomicInteger refCounter;
 
     /**
      * Used to work-around blocking problems with STDIN. In most cases <code>null</code>.
      * See {@link #ChannelDescriptor(InputStream, int, ModeFlags, FileDescriptor)}
      * for more details. You probably should not use it.
      */
     private InputStream baseInputStream;
     
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
      */
     private ChannelDescriptor(Channel channel, int fileno, ModeFlags originalModes, FileDescriptor fileDescriptor, AtomicInteger refCounter) {
         this.refCounter = refCounter;
         this.channel = channel;
         this.fileno = fileno;
         this.originalModes = originalModes;
         this.fileDescriptor = fileDescriptor;
     }
 
     /**
      * Construct a new ChannelDescriptor with the given channel, file number, mode flags,
      * and file descriptor object. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed.
      * 
      * @param channel The channel for the new descriptor
      * @param fileno The file number for the new descriptor
      * @param originalModes The mode flags for the new descriptor
      * @param fileDescriptor The java.io.FileDescriptor object for the new descriptor
      */
     public ChannelDescriptor(Channel channel, int fileno, ModeFlags originalModes, FileDescriptor fileDescriptor) {
         this(channel, fileno, originalModes, fileDescriptor, new AtomicInteger(1));
     }
 
     /**
      * Special constructor to create the ChannelDescriptor out of the stream, file number,
      * mode flags, and file descriptor object. The channel will be created from the
      * provided stream. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed. <b>Note:</b> in most cases, you should not
      * use this constructor, it's reserved mostly for STDIN.
      *
      * @param baseInputStream The stream to create the channel for the new descriptor
      * @param fileno The file number for the new descriptor
      * @param originalModes The mode flags for the new descriptor
      * @param fileDescriptor The java.io.FileDescriptor object for the new descriptor
      */
     public ChannelDescriptor(InputStream baseInputStream, int fileno, ModeFlags originalModes, FileDescriptor fileDescriptor) {
         // The reason why we need the stream is to be able to invoke available() on it.
         // STDIN in Java is non-interruptible, non-selectable, and attempt to read
         // on such stream might lead to thread being blocked without *any* way to unblock it.
         // That's where available() comes it, so at least we could check whether
         // anything is available to be read without blocking.
         this(Channels.newChannel(baseInputStream), fileno, originalModes, fileDescriptor, new AtomicInteger(1));
         this.baseInputStream = baseInputStream;
     }
 
     /**
      * Construct a new ChannelDescriptor with the given channel, file number,
      * and file descriptor object. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed. The channel's capabilities will be used
      * to determine the "original" set of mode flags.
      * 
      * @param channel The channel for the new descriptor
      * @param fileno The file number for the new descriptor
      * @param fileDescriptor The java.io.FileDescriptor object for the new descriptor
      */
     public ChannelDescriptor(Channel channel, int fileno, FileDescriptor fileDescriptor) throws InvalidValueException {
         this(channel, fileno, getModesFromChannel(channel), fileDescriptor);
     }
 
     /**
      * Get this descriptor's file number.
      * 
      * @return the fileno for this descriptor
      */
     public int getFileno() {
         return fileno;
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
      * See {@link #ChannelDescriptor(InputStream, int, ModeFlags, FileDescriptor)}
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
         return channel instanceof FileChannel;
     }
+    
+    /**
+     * Whether the channel associated with this descriptor is a NullChannel,
+     * for which many operations are simply noops.
+     */
+    public boolean isNull() {
+        return channel instanceof NullChannel;
+    }
 
     /**
      * Whether the channel associated with this descriptor is writable (i.e.
      * whether it is instanceof WritableByteChannel).
      * 
      * @return true if the associated channel is writable, false otherwise
      */
     public boolean isWritable() {
         return channel instanceof WritableByteChannel;
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
 
             int newFileno = RubyIO.getNewFileno();
             
             if (DEBUG) getLogger("ChannelDescriptor").info("Reopen fileno " + newFileno + ", refs now: " + refCounter.get());
 
             return new ChannelDescriptor(channel, newFileno, originalModes, fileDescriptor, refCounter);
         }
     }
     
     /**
      * Mimics the POSIX dup2(2) function for a "file descriptor" that's already
      * open.
      * 
      * @param other The descriptor into which we should dup this one
      */
     public void dup2(ChannelDescriptor other) {
         other.channel = channel;
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
 
             if (DEBUG) getLogger("ChannelDescriptor").info("Reopen fileno " + fileno + ", refs now: " + refCounter.get());
 
             return new ChannelDescriptor(channel, fileno, originalModes, fileDescriptor, refCounter);
         }
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
         if (channel instanceof FileChannel) {
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
      * @see java.util.ByteList
      */
     public int read(int number, ByteList byteList) throws IOException, BadDescriptorException {
         checkOpen();
         
         ByteBuffer buffer = ByteBuffer.allocate(number);
         int bytesRead = read(buffer);
 
         byte[] ret;
         if (buffer.hasRemaining()) {
             buffer.flip();
             ret = new byte[buffer.remaining()];
             buffer.get(ret);
         } else {
             ret = buffer.array();
         }
         byteList.append(ret);
 
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
         
         ReadableByteChannel readChannel = (ReadableByteChannel) channel;
         int bytesRead = 0;
         bytesRead = readChannel.read(buffer);
 
         return bytesRead;
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
     public int internalWrite(ByteBuffer buffer) throws IOException, BadDescriptorException {
         checkOpen();
         
         WritableByteChannel writeChannel = (WritableByteChannel)channel;
         
         if (isSeekable() && originalModes.isAppendable()) {
             FileChannel fileChannel = (FileChannel)channel;
             fileChannel.position(fileChannel.size());
         }
         
         return writeChannel.write(buffer);
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
         
         return internalWrite(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin(), buf.length()));
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
         return open(cwd, path, flags, 0, null);
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
         boolean fileCreated = false;
         if (path.equals("/dev/null") || path.equalsIgnoreCase("nul:") || path.equalsIgnoreCase("nul")) {
             Channel nullChannel = new NullChannel();
             // FIXME: don't use RubyIO for this
             return new ChannelDescriptor(nullChannel, RubyIO.getNewFileno(), flags, new FileDescriptor());
         } else if(path.startsWith("file:")) {
             String filePath = path.substring(5, path.indexOf("!"));
             String internalPath = path.substring(path.indexOf("!") + 2);
 
             if (!new File(filePath).exists()) {
                 throw new FileNotFoundException(path);
             }
             
             JarFile jf = new JarFile(filePath);
             ZipEntry zf = jf.getEntry(internalPath);
 
             if(zf == null) {
                 throw new FileNotFoundException(path);
             }
 
             InputStream is = jf.getInputStream(zf);
             // FIXME: don't use RubyIO for this
             return new ChannelDescriptor(Channels.newChannel(is), RubyIO.getNewFileno(), flags, new FileDescriptor());
         } else {
             JRubyFile theFile = JRubyFile.create(cwd,path);
 
             if (theFile.isDirectory() && flags.isWritable()) {
                 throw new DirectoryAsFileException();
             }
 
             if (flags.isCreate()) {
                 if (theFile.exists() && flags.isExclusive()) {
                     throw new FileExistsException(path);
                 }
                 fileCreated = theFile.createNewFile();
             } else {
                 if (!theFile.exists()) {
                     throw new FileNotFoundException(path);
                 }
             }
 
             // We always open this rw since we can only open it r or rw.
             RandomAccessFile file = new RandomAccessFile(theFile, flags.toJavaModeString());
 
             // call chmod after we created the RandomAccesFile
             // because otherwise, the file could be read-only
             if (fileCreated) {
                 // attempt to set the permissions, if we have been passed a POSIX instance,
                 // and only if the file was created in this call.
                 if (posix != null && perm != -1) {
                     posix.chmod(theFile.getPath(), perm);
                 }
             }
 
             if (flags.isTruncate()) file.setLength(0L);
 
             // TODO: append should set the FD to end, no? But there is no seek(int) in libc!
             //if (modes.isAppendable()) seek(0, Stream.SEEK_END);
 
             return new ChannelDescriptor(file.getChannel(), RubyIO.getNewFileno(), flags, file.getFD());
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
 
             if (DEBUG) getLogger("ChannelDescriptor").info("Descriptor for fileno " + fileno + " refs: " + count);
 
             if (count <= 0) {
                 channel.close();
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
             }
             modes = new ModeFlags(RDONLY);
         } else if (channel instanceof WritableByteChannel) {
             modes = new ModeFlags(WRONLY);
         } else {
             // FIXME: I don't like this
             modes = new ModeFlags(RDWR);
         }
         
         return modes;
     }
 }
diff --git a/src/org/jruby/util/io/ChannelStream.java b/src/org/jruby/util/io/ChannelStream.java
index 5223fa2bc5..3faeed65c1 100644
--- a/src/org/jruby/util/io/ChannelStream.java
+++ b/src/org/jruby/util/io/ChannelStream.java
@@ -1,854 +1,860 @@
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
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 package org.jruby.util.io;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.RandomAccessFile;
 import java.nio.ByteBuffer;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 
 import java.nio.channels.Channel;
 import java.nio.channels.IllegalBlockingModeException;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.WritableByteChannel;
 import static java.util.logging.Logger.getLogger;
 import org.jruby.Finalizable;
 import org.jruby.Ruby;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyFile;
 
 /**
  * <p>This file implements a seekable IO file.</p>
  */
 public class ChannelStream implements Stream, Finalizable {
     private final static boolean DEBUG = false;
     private final static int BUFSIZE = 16 * 1024;
     
     private Ruby runtime;
     protected ModeFlags modes;
     protected boolean sync = false;
     
     protected ByteBuffer buffer; // r/w buffer
     protected boolean reading; // are we reading or writing?
     private ChannelDescriptor descriptor;
     private boolean blocking = true;
     protected int ungotc = -1;
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes, FileDescriptor fileDescriptor) throws InvalidValueException {
         descriptor.checkNewModes(modes);
         
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = modes;
         this.buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
         
         // this constructor is used by fdopen, so we don't increment descriptor ref count
     }
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor) {
         this(runtime, descriptor, descriptor.getFileDescriptor());
     }
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor, FileDescriptor fileDescriptor) {
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = descriptor.getOriginalModes();
         buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
     }
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
         descriptor.checkNewModes(modes);
         
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = modes;
         buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
     }
 
     public Ruby getRuntime() {
         return runtime;
     }
     
     public void checkReadable() throws IOException {
         if (!modes.isReadable()) throw new IOException("not opened for reading");
     }
 
     public void checkWritable() throws IOException {
         if (!modes.isWritable()) throw new IOException("not opened for writing");
     }
 
     public void checkPermissionsSubsetOf(ModeFlags subsetModes) {
         subsetModes.isSubsetOf(modes);
     }
     
     public ModeFlags getModes() {
     	return modes;
     }
     
     public boolean isSync() {
         return sync;
     }
 
     public void setSync(boolean sync) {
         this.sync = sync;
     }
 
     /**
      * Implement IO#wait as per io/wait in MRI.
      * waits until input available or timed out and returns self, or nil when EOF reached.
      *
      * The default implementation loops while ready returns 0.
      */
     public void waitUntilReady() throws IOException, InterruptedException {
         while (ready() == 0) {
             Thread.sleep(10);
         }
     }
     
     public boolean readDataBuffered() {
         return reading && buffer.hasRemaining();
     }
     
     public boolean writeDataBuffered() {
         return !reading && buffer.position() > 0;
     }
 
     public synchronized ByteList fgets(ByteList separatorString) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
 
         if (separatorString == null) {
             return readall();
         }
 
         final ByteList separator = (separatorString == PARAGRAPH_DELIMETER) ?
             PARAGRAPH_SEPARATOR : separatorString;
 
         descriptor.checkOpen();
         
         if (feof()) {
             return null;
         }
         
         int c = read();
         
         if (c == -1) {
             return null;
         }
         
         // unread back
         buffer.position(buffer.position() - 1);
 
         ByteList buf = new ByteList(40);
         
         byte first = separator.bytes[separator.begin];
 
         LineLoop : while (true) {
             ReadLoop: while (true) {
                 byte[] bytes = buffer.array();
                 int offset = buffer.position();
                 int max = buffer.limit();
                 
                 // iterate over remainder of buffer until we find a match
                 for (int i = offset; i < max; i++) {
                     c = bytes[i];
                     if (c == first) {
                         // terminate and advance buffer when we find our char
                         buf.append(bytes, offset, i - offset);
                         if (i >= max) {
                             buffer.clear();
                         } else {
                             buffer.position(i + 1);
                         }
                         break ReadLoop;
                     }
                 }
                 
                 // no match, append remainder of buffer and continue with next block
                 buf.append(bytes, offset, buffer.remaining());
                 buffer.clear();
                 int read = ((ReadableByteChannel)descriptor.getChannel()).read(buffer);
                 buffer.flip();
                 if (read == -1) break LineLoop;
             }
             
             // found a match above, check if remaining separator characters match, appending as we go
             for (int i = 0; i < separator.realSize; i++) {
                 if (c == -1) {
                     break LineLoop;
                 } else if (c != separator.bytes[separator.begin + i]) {
                     buf.append(c);
                     continue LineLoop;
                 }
                 buf.append(c);
                 if (i < separator.realSize - 1) {
                     c = read();
                 }
             }
             break;
         }
 
         if (separatorString == PARAGRAPH_DELIMETER) {
             while (c == separator.bytes[separator.begin]) {
                 c = read();
             }
             ungetc(c);
         }
 
         return buf;
     }
     
     public synchronized ByteList readall() throws IOException, BadDescriptorException {
         if (descriptor.isSeekable()) {
             invalidateBuffer();
             FileChannel channel = (FileChannel)descriptor.getChannel();
             long left = channel.size() - channel.position();
             if (left == 0) {
                 eof = true;
                 return null;
             }
 
             return fread((int) left);
+        } else if (descriptor.isNull()) {
+            return new ByteList(0);
         } else {
             checkReadable();
 
             ByteList byteList = new ByteList();
             ByteList read = fread(BUFSIZE);
             
             if (read == null) {
                 eof = true;
                 return byteList;
             }
 
             while (read != null) {
                 byteList.append(read);
                 read = fread(BUFSIZE);
             }
 
             return byteList;
         } 
     }
     
     /**
      * <p>Close IO handler resources.</p>
      * @throws IOException 
      * @throws BadDescriptorException 
      * 
      * @see org.jruby.util.IOHandler#close()
      */
     public synchronized void fclose() throws IOException, BadDescriptorException {
         close(false); // not closing from finalise
     }
     
     /**
      * Internal close, to safely work for finalizing.
      * @param finalizing true if this is in a finalizing context
      * @throws IOException 
      * @throws BadDescriptorException
      */
     private void close(boolean finalizing) throws IOException, BadDescriptorException {
         try {
             flushWrite();
 
             descriptor.close();
 
             if (DEBUG) getLogger("ChannelStream").info("Descriptor for fileno " + descriptor.getFileno() + " closed by stream");
         } finally {
             if (!finalizing) getRuntime().removeInternalFinalizer(this);
         }
     }
     
     /**
      * Internal close, to safely work for finalizing.
      * @param finalizing true if this is in a finalizing context
      * @throws IOException 
      * @throws BadDescriptorException
      */
     private void closeForFinalize() {
         try {
             close(true);
         } catch (BadDescriptorException ex) {
             // silence
         } catch (IOException ex) {
             // silence
         }
     }
 
     /**
      * @throws IOException 
      * @throws BadDescriptorException 
      * @see org.jruby.util.IOHandler#flush()
      */
     public synchronized int fflush() throws IOException, BadDescriptorException {
         checkWritable();
         try {
             flushWrite();
         } catch (EOFException eof) {
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
             
         buffer.flip();
         descriptor.write(buffer);
         buffer.clear();
     }
 
     /**
      * @see org.jruby.util.IOHandler#getInputStream()
      */
     public InputStream newInputStream() {
         InputStream in = descriptor.getBaseInputStream();
         if (in == null) {
             return new BufferedInputStream(Channels.newInputStream((ReadableByteChannel)descriptor.getChannel()));
         } else {
             return in;
         }
     }
 
     /**
      * @see org.jruby.util.IOHandler#getOutputStream()
      */
     public OutputStream newOutputStream() {
         return new BufferedOutputStream(Channels.newOutputStream((WritableByteChannel)descriptor.getChannel()));
     }
     
     private boolean eof = false;
     
     public void clearerr() {
         eof = false;
     }
     
     /**
      * @throws IOException 
      * @throws BadDescriptorException 
      * @see org.jruby.util.IOHandler#isEOF()
      */
     public synchronized boolean feof() throws IOException, BadDescriptorException {
         checkReadable();
         
         if (eof) {
             return true;
         } else {
             return false;
         }
 //        
 //        if (reading && buffer.hasRemaining()) return false;
 //        
 //        if (descriptor.isSeekable()) {
 //            FileChannel fileChannel = (FileChannel)descriptor.getChannel();
 //            return (fileChannel.size() == fileChannel.position());
 //        } else if (descriptor.getChannel() instanceof SocketChannel) {
 //            return false;
 //        } else {
 //            checkReadable();
 //            ensureRead();
 //
 //            if (ungotc > 0) {
 //                return false;
 //            }
 //            // TODO: this is new to replace what's below
 //            ungotc = read();
 //            if (ungotc == -1) {
 //                eof = true;
 //                return true;
 //            }
 //            // FIXME: this was here before; need a better way?
 ////            if (fillInBuffer() < 0) {
 ////                return true;
 ////            }
 //            return false;
 //        }
     }
     
     /**
      * @throws IOException 
      * @see org.jruby.util.IOHandler#pos()
      */
     public synchronized long fgetpos() throws IOException, PipeException, InvalidValueException, BadDescriptorException {
         // Correct position for read / write buffering (we could invalidate, but expensive)
         if (descriptor.isSeekable()) {
             fseek(0, SEEK_CUR);
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             return fileChannel.position();
+        } else if (descriptor.isNull()) {
+            return 0;
         } else {
             throw new PipeException();
         }
     }
     
     /**
      * @throws IOException 
      * @throws InvalidValueException 
      * @see org.jruby.util.IOHandler#seek(long, int)
      */
     public synchronized void fseek(long offset, int type) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
-        if (descriptor.getChannel() instanceof FileChannel) {
+        if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             if (reading) {
                 if (buffer.remaining() > 0) {
                     fileChannel.position(fileChannel.position() - buffer.remaining());
                 }
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
                     fileChannel.position(fileChannel.position() + offset);
                     break;
                 case SEEK_END:
                     fileChannel.position(fileChannel.size() + offset);
                     break;
                 }
             } catch (IllegalArgumentException e) {
                 throw new InvalidValueException();
             }
+        } else if (descriptor.isNull()) {
+            return;
         } else {
             throw new PipeException();
         }
     }
 
     /**
      * @see org.jruby.util.IOHandler#sync()
      */
     public void sync() throws IOException, BadDescriptorException {
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
                 throw getRuntime().newIOError("sysread for buffered IO");
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
         
         return byteList;
     }
 
     private ByteList bufferedRead(int number) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
         
         ByteList result = new ByteList();
         int len = -1;
         if (buffer.hasRemaining()) { // already have some bytes buffered
             len = (number <= buffer.remaining()) ? number : buffer.remaining();
             result.append(buffer, len);
         }
         
         ReadableByteChannel readChannel = (ReadableByteChannel)descriptor.getChannel();
         int read = BUFSIZE;
         while (read == BUFSIZE && result.length() != number) { // not complete. try to read more
             buffer.clear(); 
             read = readChannel.read(buffer);
             buffer.flip();
             if (read == -1) break;
             int desired = number - result.length();
             len = (desired < read) ? desired : read;
             result.append(buffer, len);
         }
         
         if (result.length() == 0 && number != 0) throw new java.io.EOFException();
         return result;
     }
     
     private int bufferedRead() throws IOException, BadDescriptorException {
         ensureRead();
         
         if (!buffer.hasRemaining()) {
             buffer.clear();
             int read = descriptor.read(buffer);
             buffer.flip();
             
             if (read <= 0) {
                 eof = true;
                 return -1;
             }
         }
         return buffer.get() & 0xFF;
     }
     
     /**
      * @throws IOException 
      * @throws BadDescriptorException 
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(ByteList buf) throws IOException, BadDescriptorException {
         getRuntime().secure(4);
         checkWritable();
         ensureWrite();
         
         // Ruby ignores empty syswrites
         if (buf == null || buf.length() == 0) return 0;
         
         if (buf.length() > buffer.capacity()) { // Doesn't fit in buffer. Write immediately.
             flushWrite(); // ensure nothing left to write
             
             descriptor.write(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin(), buf.length()));
         } else {
             if (buf.length() > buffer.remaining()) flushWrite();
             
             buffer.put(buf.unsafeBytes(), buf.begin(), buf.length());
         }
         
         if (isSync()) sync();
         
         return buf.realSize;
     }
     
     /**
      * @throws IOException 
      * @throws BadDescriptorException 
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(int c) throws IOException, BadDescriptorException {
         getRuntime().secure(4);
         checkWritable();
         ensureWrite();
 
         if (!buffer.hasRemaining()) flushWrite();
         
         buffer.put((byte) c);
             
         if (isSync()) sync();
             
         return 1;
     }
     
     public synchronized void ftruncate(long newLength) throws IOException, BadDescriptorException {
         invalidateBuffer();
         FileChannel fileChannel = (FileChannel)descriptor.getChannel();
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
      * Ensure close (especially flush) when we're finished with
      */
     public void finalize() {
         // FIXME: I got a bunch of NPEs when I didn't check for nulls here...HOW?!
         if (descriptor != null && descriptor.isSeekable() && descriptor.isOpen()) closeForFinalize(); // close without removing from finalizers
     }
     
     public int ready() throws IOException {
         return newInputStream().available();
     }
 
     public synchronized void fputc(int c) throws IOException, BadDescriptorException {
         try {
             bufferedWrite(c);
             fflush();
         } catch (IOException e) {
         }
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
 
     public synchronized ByteList fread(int number) throws IOException, BadDescriptorException {
         try {
             if (number == 0) {
                 if (eof) {
                     return null;
                 } else {
                     return new ByteList(0);
                 }
             }
 
             if (ungotc >= 0) {
                 ByteList buf2 = bufferedRead(number - 1);
                 buf2.prepend((byte)ungotc);
                 ungotc = -1;
                 return buf2;
             }
 
             return bufferedRead(number);
         } catch (EOFException e) {
             eof = true;
             return null;
         }
     }
 
     public synchronized ByteList readpartial(int number) throws IOException, BadDescriptorException, EOFException {
         if (descriptor.getChannel() instanceof SelectableChannel) {
             if (ungotc >= 0) {
                 ByteList buf2 = bufferedRead(number - 1);
                 buf2.prepend((byte)ungotc);
                 ungotc = -1;
                 return buf2;
             } else {
                 return bufferedRead(number);
             }
         } else {
             return null;
         }
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
 
     public synchronized void freopen(String path, ModeFlags modes) throws DirectoryAsFileException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         // flush first
         flushWrite();
 
         // reset buffer
         buffer.clear();
         if (reading) {
             buffer.flip();
         }
 
         this.modes = modes;
         String cwd = getRuntime().getCurrentDirectory();
         JRubyFile theFile = JRubyFile.create(cwd,path);
 
         if (theFile.isDirectory() && modes.isWritable()) throw new DirectoryAsFileException();
         
         if (modes.isCreate()) {
             if (theFile.exists() && modes.isExclusive()) {
                 throw getRuntime().newErrnoEEXISTError("File exists - " + path);
             }
             theFile.createNewFile();
         } else {
             if (!theFile.exists()) {
                 throw getRuntime().newErrnoENOENTError("file not found - " + path);
             }
         }
         
         if (descriptor.isOpen()) {
             descriptor.close();
         }
 
         // We always open this rw since we can only open it r or rw.
         RandomAccessFile file = new RandomAccessFile(theFile, modes.toJavaModeString());
 
         if (modes.isTruncate()) file.setLength(0L);
 
         descriptor = new ChannelDescriptor(file.getChannel(), descriptor.getFileno(), modes, file.getFD());
         
         if (modes.isAppendable()) fseek(0, SEEK_END);
     }
     
     public static Stream fopen(Ruby runtime, String path, ModeFlags modes) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         String cwd = runtime.getCurrentDirectory();
         
         ChannelDescriptor descriptor = ChannelDescriptor.open(cwd, path, modes);
         
         Stream stream = fdopen(runtime, descriptor, modes);
         
         if (modes.isAppendable()) stream.fseek(0, Stream.SEEK_END);
         
         return stream;
     }
     
     public static Stream fdopen(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
         Stream handler = new ChannelStream(runtime, descriptor, modes, descriptor.getFileDescriptor());
         
         return handler;
     }
 }
diff --git a/src/org/jruby/util/io/NullChannel.java b/src/org/jruby/util/io/NullChannel.java
index d28247fce0..c06ea857b1 100644
--- a/src/org/jruby/util/io/NullChannel.java
+++ b/src/org/jruby/util/io/NullChannel.java
@@ -1,66 +1,67 @@
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
 
 import java.io.EOFException;
 import java.io.IOException;
 import java.nio.ByteBuffer;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.WritableByteChannel;
 
 /**
  *
  * @author headius
  */
 public class NullChannel implements WritableByteChannel, ReadableByteChannel {
     private boolean isOpen = true;
 
     public int write(ByteBuffer buffer) throws IOException {
         if (!isOpen) {
             throw new EOFException();
         }
-        buffer.position(buffer.position() + buffer.remaining());
-        return buffer.remaining();
+        int length = buffer.remaining();
+        buffer.position(buffer.position());
+        return length;
     }
 
     public boolean isOpen() {
         return isOpen;
     }
 
     public void close() throws IOException {
         isOpen = false;
     }
 
     public int read(ByteBuffer dst) throws IOException {
         if (!isOpen) {
             throw new EOFException();
         }
         return -1;
     }
 }
