diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 74a937cf5c..a690096da4 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1414 +1,1414 @@
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
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.ext.posix.util.Platform;
 import org.jruby.runtime.Block;
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
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 
 /**
  * Ruby File class equivalent in java.
  **/
 @JRubyClass(name="File", parent="IO", include="FileTest")
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
     private static final int FNM_SYSCASE;
 
     static {
         if (Platform.IS_WINDOWS) {
             FNM_SYSCASE = FNM_CASEFOLD;
         } else {
             FNM_SYSCASE = 0;
         }
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
         if (path == null) return path;
         if (Platform.IS_WINDOWS) {
             // MRI behavior on Windows: it treats '/' as a root of
             // a current drive (but only if SINGLE slash is present!):
             // E.g., if current work directory is
             // 'D:/home/directory', then '/' means 'D:/'.
             //
             // Basically, '/path' is treated as a *RELATIVE* path,
             // relative to the current drive. '//path' is treated
             // as absolute one.
             if ((path.startsWith("/") && !(path.length()>2 && path.charAt(2) == ':')) || path.startsWith("\\")) {
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
 
     @JRubyModule(name="File::Constants")
     public static class Constants {}
     
     public static RubyClass createFileClass(Ruby runtime) {
         RubyClass fileClass = runtime.defineClass("File", runtime.getIO(), FILE_ALLOCATOR);
         runtime.setFile(fileClass);
         RubyString separator = runtime.newString("/");
         ThreadContext context = runtime.getCurrentContext();
         
         fileClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyFile;
             }
         };
 
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
         
         RubyString pathSeparator = runtime.newString(File.pathSeparator);
         pathSeparator.freeze(context);
         fileClass.defineConstant("PATH_SEPARATOR", pathSeparator);
 
         // TODO: why are we duplicating the constants here, and then in
         // File::Constants below? File::Constants is included in IO.
 
         // TODO: These were missing, so we're not handling them elsewhere?
         // FIXME: The old value, 32786, didn't match what IOModes expected, so I reference
         // the constant here. THIS MAY NOT BE THE CORRECT VALUE.
         fileClass.fastSetConstant("BINARY", runtime.newFixnum(ModeFlags.BINARY));
         fileClass.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         fileClass.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         fileClass.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_SYSCASE));
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
         constants.fastSetConstant("BINARY", runtime.newFixnum(ModeFlags.BINARY));
         constants.fastSetConstant("SYNC", runtime.newFixnum(0x1000));
         constants.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         constants.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         constants.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_SYSCASE));
         constants.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
         constants.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
         
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
             // Return false here
         } catch (java.nio.channels.OverlappingFileLockException ioe) {
             if (context.getRuntime().getDebug().isTrue()) {
                 ioe.printStackTrace(System.err);
             }
             // Return false here
         }
 
         return context.getRuntime().getFalse();
     }
 
     @JRubyMethod(required = 1, optional = 2, frame = true, visibility = Visibility.PRIVATE)
     @Override
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
         
-        path = filename.toString();
+        path = filename.convertToString().getUnicodeValue();
         
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
             // FNFException can be thrown in both cases, when the file
             // is not found, or when permission is denied.
             if (Ruby.isSecurityRestricted() || new File(path).exists()) {
                 throw getRuntime().newErrnoEACCESError(
                         "Permission denied - " + path);
             }
             throw getRuntime().newErrnoENOENTError(
                     "File not found - " + path);
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
     public IRubyObject chmod(ThreadContext context, IRubyObject arg) {
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw context.getRuntime().newErrnoENOENTError("No such file or directory - " + path);
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
             throw context.getRuntime().newErrnoENOENTError("No such file or directory - " + path);
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
             throw context.getRuntime().newErrnoENOENTError("No such file or directory - " + path);
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
             throw context.getRuntime().newErrnoENOENTError("No such file or directory - " + path);
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
 
     @JRubyMethod
     public RubyString path(ThreadContext context) {
         return context.getRuntime().newString(path);
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
             throw context.getRuntime().newErrnoEINVALError("invalid argument: " + path);
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
         String name = RubyString.stringValue(args[0]).toString();
 
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
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == runtime.getPosix().chown(filename.toString(), owner, group);
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject dirname(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyString filename = RubyString.stringValue(arg);
         String jfilename = filename.toString();
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
         String filename = RubyString.stringValue(baseFilename).toString();
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
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject expand_path(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         
         String relativePath = RubyString.stringValue(args[0]).toString();
 
         boolean isAbsoluteWithFilePrefix = relativePath.startsWith("file:");
 
         String cwd = null;
         
         // Handle ~user paths 
         relativePath = expandUserPath(context, relativePath);
         
         // If there's a second argument, it's the path to which the first 
         // argument is relative.
         if (args.length == 2 && !args[1].isNil()) {
             
             String cwdArg = RubyString.stringValue(args[1]).toString();
             
             // Handle ~user paths.
             cwd = expandUserPath(context, cwdArg);
 
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
     public static IRubyObject fnmatch(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         int flags = args.length == 3 ? RubyNumeric.num2int(args[2]) : 0;
 
         ByteList pattern = args[0].convertToString().getByteList();
         ByteList path = args[1].convertToString().getByteList();
 
         if (org.jruby.util.Dir.fnmatch(pattern.bytes, pattern.begin, pattern.begin+pattern.realSize, 
                                        path.bytes, path.begin, path.begin+path.realSize, flags) == 0) {
             return context.getRuntime().getTrue();
         }
         return context.getRuntime().getFalse();
     }
     
     @JRubyMethod(name = "ftype", required = 1, meta = true)
     public static IRubyObject ftype(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         return context.getRuntime().newFileStat(filename.convertToString().toString(), true).ftype();
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
                 element = args[i].toString();
             } else if (args[i] instanceof RubyArray) {
                 if (runtime.isInspecting(args[i])) {
                     element = "[...]";
                 } else {
                     element = inspectJoin(context, recv, ary, ((RubyArray)args[i]));
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
         String f = filename.convertToString().toString();
         if(f.startsWith("file:") && f.indexOf('!') != -1) {
             f = f.substring(5, f.indexOf("!"));
         }
         return context.getRuntime().newFileStat(f, true);
     }
 
     @JRubyMethod(name = "stat", required = 1, meta = true)
     public static IRubyObject stat(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = filename.convertToString().toString();
         if(f.startsWith("file:") && f.indexOf('!') != -1) {
             f = f.substring(5, f.indexOf("!"));
         }
         return context.getRuntime().newFileStat(f, false);
     }
 
     @JRubyMethod(name = "atime", required = 1, meta = true)
     public static IRubyObject atime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = filename.convertToString().toString();
         if(f.startsWith("file:") && f.indexOf('!') != -1) {
             f = f.substring(5, f.indexOf("!"));
         }
         return context.getRuntime().newFileStat(f, false).atime();
     }
 
     @JRubyMethod(name = "ctime", required = 1, meta = true)
     public static IRubyObject ctime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = filename.convertToString().toString();
         if(f.startsWith("file:") && f.indexOf('!') != -1) {
             f = f.substring(5, f.indexOf("!"));
         }
         return context.getRuntime().newFileStat(f, false).ctime();
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject lchmod(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         
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
     public static IRubyObject lchown(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int owner = !args[0].isNil() ? RubyNumeric.num2int(args[0]) : -1;
         int group = !args[1].isNil() ? RubyNumeric.num2int(args[1]) : -1;
         int count = 0;
         
         for (int i = 2; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == runtime.getPosix().lchown(filename.toString(), owner, group);
             if (result) {
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
         try {
             if (runtime.getPosix().link(
                     fromStr.toString(),toStr.toString()) == -1) {
                 // FIXME: When we get JNA3 we need to properly write this to errno.
                 throw runtime.newErrnoEEXISTError("File exists - " 
                                + fromStr + " or " + toStr);
             }
         } catch (java.lang.UnsatisfiedLinkError ule) {
             throw runtime.newNotImplementedError("link() function is unimplemented on this machine");
         }
         
         return runtime.newFixnum(0);
     }
 
     @JRubyMethod(name = "mtime", required = 1, meta = true)
     public static IRubyObject mtime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         return getLastModified(context.getRuntime(), filename.convertToString().toString());
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject rename(ThreadContext context, IRubyObject recv, IRubyObject oldName, IRubyObject newName) {
         Ruby runtime = context.getRuntime();
         RubyString oldNameString = RubyString.stringValue(oldName);
         RubyString newNameString = RubyString.stringValue(newName);
         runtime.checkSafeString(oldNameString);
         runtime.checkSafeString(newNameString);
         JRubyFile oldFile = JRubyFile.create(runtime.getCurrentDirectory(), oldNameString.toString());
         JRubyFile newFile = JRubyFile.create(runtime.getCurrentDirectory(), newNameString.toString());
         
         if (!oldFile.exists() || !newFile.getParentFile().exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + oldNameString + 
                     " or " + newNameString);
         }
 
         JRubyFile dest = JRubyFile.create(runtime.getCurrentDirectory(), newNameString.toString());
 
         if (oldFile.renameTo(dest)) {  // rename is successful
             return RubyFixnum.zero(runtime);
         }
 
         // rename via Java API call wasn't successful, let's try some tricks, similar to MRI 
 
         if (newFile.exists()) {
             runtime.getPosix().chmod(newNameString.toString(), 0666);
             newFile.delete();
         }
 
         if (oldFile.renameTo(dest)) { // try to rename one more time
             return RubyFixnum.zero(runtime);
         }
 
         throw runtime.newErrnoEACCESError("Permission denied - " + oldNameString + " or " + 
                 newNameString);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static RubyArray split(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyString filename = RubyString.stringValue(arg);
         
         return context.getRuntime().newArray(dirname(context, recv, filename),
                 basename(context, recv, new IRubyObject[] { filename }));
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject symlink(ThreadContext context, IRubyObject recv, IRubyObject from, IRubyObject to) {
         Ruby runtime = context.getRuntime();
         RubyString fromStr = RubyString.stringValue(from);
         RubyString toStr = RubyString.stringValue(to);
         try {
             if (runtime.getPosix().symlink(
                     fromStr.toString(), toStr.toString()) == -1) {
                 // FIXME: When we get JNA3 we need to properly write this to errno.
                 throw runtime.newErrnoEEXISTError("File exists - " 
                                + fromStr + " or " + toStr);
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
             String realPath = runtime.getPosix().readlink(path.toString());
         
             if (!RubyFileTest.exist_p(recv, path).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + path);
             }
         
diff --git a/src/org/jruby/RubyGlobal.java b/src/org/jruby/RubyGlobal.java
index 6572375541..3723be9415 100644
--- a/src/org/jruby/RubyGlobal.java
+++ b/src/org/jruby/RubyGlobal.java
@@ -1,624 +1,624 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
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
 
 import org.jruby.util.io.STDIO;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.environment.OSEnvironmentReaderExcepton;
 import org.jruby.environment.OSEnvironment;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ReadonlyGlobalVariable;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.KCode;
 
 /** This class initializes global variables and constants.
  * 
  * @author jpetersen
  */
 public class RubyGlobal {
     
     /**
      * Obligate string-keyed and string-valued hash, used for ENV and ENV_JAVA
      * 
      */
     public static class StringOnlyRubyHash extends RubyHash {
         
         public StringOnlyRubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
             super(runtime, valueMap, defaultValue);
         }
 
         @Override
         public RubyHash to_hash() {
             Ruby runtime = getRuntime();
             RubyHash hash = RubyHash.newHash(runtime);
             hash.replace(runtime.getCurrentContext(), this);
             return hash;
         }
 
         @Override
         public IRubyObject op_aref(ThreadContext context, IRubyObject key) {
             return super.op_aref(context, key.convertToString());
         }
 
         @Override
         public IRubyObject op_aset(ThreadContext context, IRubyObject key, IRubyObject value) {
             if (!key.respondsTo("to_str")) {
                 throw getRuntime().newTypeError("can't convert " + key.getMetaClass() + " into String");
             }
             if (!value.respondsTo("to_str") && !value.isNil()) {
                 throw getRuntime().newTypeError("can't convert " + value.getMetaClass() + " into String");
             }
 
             if (value.isNil()) {
                 return super.delete(context, key, org.jruby.runtime.Block.NULL_BLOCK);
             }
             
             //return super.aset(getRuntime().newString("sadfasdF"), getRuntime().newString("sadfasdF"));
             return super.op_aset(context, RuntimeHelpers.invoke(context, key, MethodIndex.TO_STR, "to_str", IRubyObject.NULL_ARRAY),
                     value.isNil() ? getRuntime().getNil() : RuntimeHelpers.invoke(context, value, MethodIndex.TO_STR, "to_str", IRubyObject.NULL_ARRAY));
         }
         
         @JRubyMethod
         @Override
         public IRubyObject to_s(){
             return getRuntime().newString("ENV");
         }
     }
     
     public static void createGlobals(ThreadContext context, Ruby runtime) {
         runtime.defineGlobalConstant("TOPLEVEL_BINDING", runtime.newBinding());
         
         runtime.defineGlobalConstant("TRUE", runtime.getTrue());
         runtime.defineGlobalConstant("FALSE", runtime.getFalse());
         runtime.defineGlobalConstant("NIL", runtime.getNil());
         
         // define ARGV and $* for this runtime
         RubyArray argvArray = runtime.newArray();
         String[] argv = runtime.getInstanceConfig().getArgv();
         for (int i = 0; i < argv.length; i++) {
-            argvArray.append(runtime.newString(argv[i]));
+            argvArray.append(RubyString.newUnicodeString(runtime, argv[i]));
         }
         runtime.defineGlobalConstant("ARGV", argvArray);
         runtime.getGlobalVariables().defineReadonly("$*", new ValueAccessor(argvArray));
 
         IAccessor d = new ValueAccessor(runtime.newString(
                 runtime.getInstanceConfig().displayedFileName()));
         runtime.getGlobalVariables().define("$PROGRAM_NAME", d);
         runtime.getGlobalVariables().define("$0", d);
 
         // Version information:
         IRubyObject version = runtime.newString(Constants.RUBY_VERSION).freeze(context);
         IRubyObject release = runtime.newString(Constants.COMPILE_DATE).freeze(context);
         IRubyObject platform = runtime.newString(Constants.PLATFORM).freeze(context);
         IRubyObject engine = runtime.newString(Constants.ENGINE).freeze(context);
 
         runtime.defineGlobalConstant("RUBY_VERSION", version);
         runtime.defineGlobalConstant("RUBY_PATCHLEVEL", runtime.newString(Constants.RUBY_PATCHLEVEL).freeze(context));
         runtime.defineGlobalConstant("RUBY_RELEASE_DATE", release);
         runtime.defineGlobalConstant("RUBY_PLATFORM", platform);
         runtime.defineGlobalConstant("RUBY_ENGINE", engine);
 
         runtime.defineGlobalConstant("VERSION", version);
         runtime.defineGlobalConstant("RELEASE_DATE", release);
         runtime.defineGlobalConstant("PLATFORM", platform);
         
         IRubyObject jrubyVersion = runtime.newString(Constants.VERSION).freeze(context);
         runtime.defineGlobalConstant("JRUBY_VERSION", jrubyVersion);
 		
         GlobalVariable kcodeGV = new KCodeGlobalVariable(runtime, "$KCODE", runtime.newString("NONE"));
         runtime.defineVariable(kcodeGV);
         runtime.defineVariable(new GlobalVariable.Copy(runtime, "$-K", kcodeGV));
         IRubyObject defaultRS = runtime.newString(runtime.getInstanceConfig().getRecordSeparator()).freeze(context);
         GlobalVariable rs = new StringGlobalVariable(runtime, "$/", defaultRS);
         runtime.defineVariable(rs);
         runtime.setRecordSeparatorVar(rs);
         runtime.getGlobalVariables().setDefaultSeparator(defaultRS);
         runtime.defineVariable(new StringGlobalVariable(runtime, "$\\", runtime.getNil()));
         runtime.defineVariable(new StringGlobalVariable(runtime, "$,", runtime.getNil()));
 
         runtime.defineVariable(new LineNumberGlobalVariable(runtime, "$.", RubyFixnum.one(runtime)));
         runtime.defineVariable(new LastlineGlobalVariable(runtime, "$_"));
         runtime.defineVariable(new LastExitStatusVariable(runtime, "$?"));
 
         runtime.defineVariable(new ErrorInfoGlobalVariable(runtime, "$!", runtime.getNil()));
         runtime.defineVariable(new NonEffectiveGlobalVariable(runtime, "$=", runtime.getFalse()));
 
         if(runtime.getInstanceConfig().getInputFieldSeparator() == null) {
             runtime.defineVariable(new GlobalVariable(runtime, "$;", runtime.getNil()));
         } else {
             runtime.defineVariable(new GlobalVariable(runtime, "$;", RubyRegexp.newRegexp(runtime, runtime.getInstanceConfig().getInputFieldSeparator(), 0)));
         }
         
         Boolean verbose = runtime.getInstanceConfig().getVerbose();
         IRubyObject verboseValue = null;
         if (verbose == null) {
             verboseValue = runtime.getNil();
         } else if(verbose == Boolean.TRUE) {
             verboseValue = runtime.getTrue();
         } else {
             verboseValue = runtime.getFalse();
         }
         runtime.defineVariable(new VerboseGlobalVariable(runtime, "$VERBOSE", verboseValue));
         
         IRubyObject debug = runtime.newBoolean(runtime.getInstanceConfig().isDebug());
         runtime.defineVariable(new DebugGlobalVariable(runtime, "$DEBUG", debug));
         runtime.defineVariable(new DebugGlobalVariable(runtime, "$-d", debug));
 
         runtime.defineVariable(new SafeGlobalVariable(runtime, "$SAFE"));
 
         runtime.defineVariable(new BacktraceGlobalVariable(runtime, "$@"));
 
         IRubyObject stdin = new RubyIO(runtime, STDIO.IN);
         IRubyObject stdout = new RubyIO(runtime, STDIO.OUT);
         IRubyObject stderr = new RubyIO(runtime, STDIO.ERR);
 
         runtime.defineVariable(new InputGlobalVariable(runtime, "$stdin", stdin));
 
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stdout", stdout));
         runtime.getGlobalVariables().alias("$>", "$stdout");
         runtime.getGlobalVariables().alias("$defout", "$stdout");
 
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stderr", stderr));
         runtime.getGlobalVariables().alias("$deferr", "$stderr");
 
         runtime.defineGlobalConstant("STDIN", stdin);
         runtime.defineGlobalConstant("STDOUT", stdout);
         runtime.defineGlobalConstant("STDERR", stderr);
 
         runtime.defineVariable(new LoadedFeatures(runtime, "$\""));
         runtime.defineVariable(new LoadedFeatures(runtime, "$LOADED_FEATURES"));
 
         runtime.defineVariable(new LoadPath(runtime, "$:"));
         runtime.defineVariable(new LoadPath(runtime, "$-I"));
         runtime.defineVariable(new LoadPath(runtime, "$LOAD_PATH"));
         
         runtime.defineVariable(new MatchMatchGlobalVariable(runtime, "$&"));
         runtime.defineVariable(new PreMatchGlobalVariable(runtime, "$`"));
         runtime.defineVariable(new PostMatchGlobalVariable(runtime, "$'"));
         runtime.defineVariable(new LastMatchGlobalVariable(runtime, "$+"));
         runtime.defineVariable(new BackRefGlobalVariable(runtime, "$~"));
 
         // On platforms without a c-library accessable through JNA, getpid will return hashCode 
         // as $$ used to. Using $$ to kill processes could take down many runtimes, but by basing
         // $$ on getpid() where available, we have the same semantics as MRI.
         runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(runtime.getPosix().getpid())));
 
         // after defn of $stderr as the call may produce warnings
         defineGlobalEnvConstants(runtime);
         
         // Fixme: Do we need the check or does Main.java not call this...they should consolidate 
         if (runtime.getGlobalVariables().get("$*").isNil()) {
             runtime.getGlobalVariables().defineReadonly("$*", new ValueAccessor(runtime.newArray()));
         }
         
         runtime.getGlobalVariables().defineReadonly("$-p", 
                 new ValueAccessor(runtime.getInstanceConfig().isAssumePrinting() ? runtime.getTrue() : runtime.getNil()));
         runtime.getGlobalVariables().defineReadonly("$-n", 
                 new ValueAccessor(runtime.getInstanceConfig().isAssumeLoop() ? runtime.getTrue() : runtime.getNil()));
         runtime.getGlobalVariables().defineReadonly("$-a", 
                 new ValueAccessor(runtime.getInstanceConfig().isSplit() ? runtime.getTrue() : runtime.getNil()));
         runtime.getGlobalVariables().defineReadonly("$-l", 
                 new ValueAccessor(runtime.getInstanceConfig().isProcessLineEnds() ? runtime.getTrue() : runtime.getNil()));
 
         // ARGF, $< object
         RubyArgsFile.initArgsFile(runtime);
     }
 
     private static void defineGlobalEnvConstants(Ruby runtime) {
 
     	Map environmentVariableMap = null;
     	OSEnvironment environment = new OSEnvironment();
     	try {
     		environmentVariableMap = environment.getEnvironmentVariableMap(runtime);
     	} catch (OSEnvironmentReaderExcepton e) {
     		// If the environment variables are not accessible shouldn't terminate 
     		runtime.getWarnings().warn(ID.MISCELLANEOUS, e.getMessage());
     	}
 		
     	if (environmentVariableMap == null) {
             // if the environment variables can't be obtained, define an empty ENV
     		environmentVariableMap = new HashMap();
     	}
 
         StringOnlyRubyHash h1 = new StringOnlyRubyHash(runtime,
                                                        environmentVariableMap, runtime.getNil());
         h1.getSingletonClass().defineAnnotatedMethods(StringOnlyRubyHash.class);
         runtime.defineGlobalConstant("ENV", h1);
 
         // Define System.getProperties() in ENV_JAVA
         Map systemProps = environment.getSystemPropertiesMap(runtime);
         runtime.defineGlobalConstant("ENV_JAVA", new StringOnlyRubyHash(
                 runtime, systemProps, runtime.getNil()));
         
     }
 
     private static class NonEffectiveGlobalVariable extends GlobalVariable {
         public NonEffectiveGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             runtime.getWarnings().warn(ID.INEFFECTIVE_GLOBAL, "warning: variable " + name + " is no longer effective; ignored", name);
             return value;
         }
 
         @Override
         public IRubyObject get() {
             runtime.getWarnings().warn(ID.INEFFECTIVE_GLOBAL, "warning: variable " + name + " is no longer effective", name);
             return runtime.getFalse();
         }
     }
 
     private static class LastExitStatusVariable extends GlobalVariable {
         public LastExitStatusVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             IRubyObject lastExitStatus = runtime.getCurrentContext().getLastExitStatus();
             return lastExitStatus == null ? runtime.getNil() : lastExitStatus;
         }
         
         @Override
         public IRubyObject set(IRubyObject lastExitStatus) {
             runtime.getCurrentContext().setLastExitStatus(lastExitStatus);
             
             return lastExitStatus;
         }
     }
 
     private static class MatchMatchGlobalVariable extends GlobalVariable {
         public MatchMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.last_match(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class PreMatchGlobalVariable extends GlobalVariable {
         public PreMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.match_pre(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class PostMatchGlobalVariable extends GlobalVariable {
         public PostMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.match_post(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class LastMatchGlobalVariable extends GlobalVariable {
         public LastMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.match_last(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class BackRefGlobalVariable extends GlobalVariable {
         public BackRefGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RuntimeHelpers.getBackref(runtime, runtime.getCurrentContext());
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             RuntimeHelpers.setBackref(runtime, runtime.getCurrentContext(), value);
             return value;
         }
     }
 
     // Accessor methods.
 
     private static class LineNumberGlobalVariable extends GlobalVariable {
         public LineNumberGlobalVariable(Ruby runtime, String name, RubyFixnum value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             RubyArgsFile.setCurrentLineNumber(runtime.getGlobalVariables().get("$<"),RubyNumeric.fix2int(value));
             return super.set(value);
         }
     }
 
     private static class ErrorInfoGlobalVariable extends GlobalVariable {
         public ErrorInfoGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, null);
             set(value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (!value.isNil() && ! runtime.getException().isInstance(value)) {
                 throw runtime.newTypeError("assigning non-exception to $!");
             }
             
             return runtime.getCurrentContext().setErrorInfo(value);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.getCurrentContext().getErrorInfo();
         }
     }
 
     // FIXME: move out of this class!
     public static class StringGlobalVariable extends GlobalVariable {
         public StringGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (!value.isNil() && ! (value instanceof RubyString)) {
                 throw runtime.newTypeError("value of " + name() + " must be a String");
             }
             return super.set(value);
         }
     }
 
     public static class KCodeGlobalVariable extends GlobalVariable {
         public KCodeGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.getKCode().kcode(runtime);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             runtime.setKCode(KCode.create(runtime, value.convertToString().toString()));
             return value;
         }
     }
 
     private static class SafeGlobalVariable extends GlobalVariable {
         public SafeGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.newFixnum(runtime.getSafeLevel());
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
 //            int level = RubyNumeric.fix2int(value);
 //            if (level < runtime.getSafeLevel()) {
 //            	throw runtime.newSecurityError("tried to downgrade safe level from " + 
 //            			runtime.getSafeLevel() + " to " + level);
 //            }
 //            runtime.setSafeLevel(level);
             // thread.setSafeLevel(level);
             runtime.getWarnings().warn(ID.SAFE_NOT_SUPPORTED, "SAFE levels are not supported in JRuby");
             return RubyFixnum.newFixnum(runtime, runtime.getSafeLevel());
         }
     }
 
     private static class VerboseGlobalVariable extends GlobalVariable {
         public VerboseGlobalVariable(Ruby runtime, String name, IRubyObject initialValue) {
             super(runtime, name, initialValue);
             set(initialValue);
         }
         
         @Override
         public IRubyObject get() {
             return runtime.getVerbose();
         }
 
         @Override
         public IRubyObject set(IRubyObject newValue) {
             if (newValue.isNil()) {
                 runtime.setVerbose(newValue);
             } else {
                 runtime.setVerbose(runtime.newBoolean(newValue.isTrue()));
             }
 
             return newValue;
         }
     }
 
     private static class DebugGlobalVariable extends GlobalVariable {
         public DebugGlobalVariable(Ruby runtime, String name, IRubyObject initialValue) {
             super(runtime, name, initialValue);
             set(initialValue);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.getDebug();
         }
 
         @Override
         public IRubyObject set(IRubyObject newValue) {
             if (newValue.isNil()) {
                 runtime.setDebug(newValue);
             } else {
                 runtime.setDebug(runtime.newBoolean(newValue.isTrue()));
             }
 
             return newValue;
         }
     }
 
     private static class BacktraceGlobalVariable extends GlobalVariable {
         public BacktraceGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         @Override
         public IRubyObject get() {
             IRubyObject errorInfo = runtime.getGlobalVariables().get("$!");
             IRubyObject backtrace = errorInfo.isNil() ? runtime.getNil() : errorInfo.callMethod(errorInfo.getRuntime().getCurrentContext(), "backtrace");
             //$@ returns nil if $!.backtrace is not an array
             if (!(backtrace instanceof RubyArray)) {
                 backtrace = runtime.getNil();
             }
             return backtrace;
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (runtime.getGlobalVariables().get("$!").isNil()) {
                 throw runtime.newArgumentError("$! not set.");
             }
             runtime.getGlobalVariables().get("$!").callMethod(value.getRuntime().getCurrentContext(), "set_backtrace", value);
             return value;
         }
     }
 
     private static class LastlineGlobalVariable extends GlobalVariable {
         public LastlineGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         @Override
         public IRubyObject get() {
             return RuntimeHelpers.getLastLine(runtime, runtime.getCurrentContext());
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             RuntimeHelpers.setLastLine(runtime, runtime.getCurrentContext(), value);
             return value;
         }
     }
 
     private static class InputGlobalVariable extends GlobalVariable {
         public InputGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (value == get()) {
                 return value;
             }
             
             return super.set(value);
         }
     }
 
     private static class OutputGlobalVariable extends GlobalVariable {
         public OutputGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (value == get()) {
                 return value;
             }
             if (value instanceof RubyIO) {
                 RubyIO io = (RubyIO)value;
                 
                 // HACK: in order to have stdout/err act like ttys and flush always,
                 // we set anything assigned to stdout/stderr to sync
                 io.getHandler().setSync(true);
             }
 
             if (!value.respondsTo("write")) {
                 throw runtime.newTypeError(name() + " must have write method, " +
                                     value.getType().getName() + " given");
             }
 
             return super.set(value);
         }
     }
 
     private static class LoadPath extends ReadonlyGlobalVariable {
         public LoadPath(Ruby runtime, String name) {
             super(runtime, name, null);
         }
         
         /**
          * @see org.jruby.runtime.GlobalVariable#get()
          */
         @Override
         public IRubyObject get() {
             return runtime.getLoadService().getLoadPath();
         }
     }
 
     private static class LoadedFeatures extends ReadonlyGlobalVariable {
         public LoadedFeatures(Ruby runtime, String name) {
             super(runtime, name, null);
         }
         
         /**
          * @see org.jruby.runtime.GlobalVariable#get()
          */
         public IRubyObject get() {
             return runtime.getLoadService().getLoadedFeatures();
         }
     }
 }
diff --git a/src/org/jruby/RubyInstanceConfig.java b/src/org/jruby/RubyInstanceConfig.java
index 5064e2f272..7adb853a47 100644
--- a/src/org/jruby/RubyInstanceConfig.java
+++ b/src/org/jruby/RubyInstanceConfig.java
@@ -28,1198 +28,1194 @@
 package org.jruby;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 
 import java.util.Set;
 import java.util.StringTokenizer;
 import org.jruby.ast.executable.Script;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.ClassCache;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.KCode;
 import org.jruby.util.NormalizedFile;
 import org.jruby.util.SafePropertyAccessor;
 import org.objectweb.asm.Opcodes;
 
 public class RubyInstanceConfig {
 
     /**
      * The max count of active methods eligible for JIT-compilation.
      */
     private static final int JIT_MAX_METHODS_LIMIT = 4096;
 
     /**
      * The max size of JIT-compiled methods (full class size) allowed.
      */
     private static final int JIT_MAX_SIZE_LIMIT = Integer.MAX_VALUE;
 
     /**
      * The JIT threshold to the specified method invocation count.
      */
     private static final int JIT_THRESHOLD = 50;
     
     /** The version to use for generated classes. Set to current JVM version by default */
     public static final int JAVA_VERSION;
     
     /**
      * Default size for chained compilation.
      */
     private static final int CHAINED_COMPILE_LINE_COUNT_DEFAULT = 500;
     
     /**
      * The number of lines at which a method, class, or block body is split into
      * chained methods (to dodge 64k method-size limit in JVM).
      */
     public static final int CHAINED_COMPILE_LINE_COUNT
             = SafePropertyAccessor.getInt("jruby.compile.chainsize", CHAINED_COMPILE_LINE_COUNT_DEFAULT);
 
     public enum CompileMode {
         JIT, FORCE, OFF;
 
         public boolean shouldPrecompileCLI() {
             switch (this) {
             case JIT: case FORCE:
                 return true;
             }
             return false;
         }
 
         public boolean shouldJIT() {
             switch (this) {
             case JIT: case FORCE:
                 return true;
             }
             return false;
         }
 
         public boolean shouldPrecompileAll() {
             return this == FORCE;
         }
     }
     private InputStream input          = System.in;
     private PrintStream output         = System.out;
     private PrintStream error          = System.err;
     private Profile profile            = Profile.DEFAULT;
     private boolean objectSpaceEnabled
             = SafePropertyAccessor.getBoolean("jruby.objectspace.enabled", false);
 
     private CompileMode compileMode = CompileMode.JIT;
     private boolean runRubyInProcess   = true;
     private String currentDirectory;
     private Map environment;
     private String[] argv = {};
 
     private final boolean jitLogging;
     private final boolean jitLoggingVerbose;
     private final int jitLogEvery;
     private final int jitThreshold;
     private final int jitMax;
     private final int jitMaxSize;
     private final boolean samplingEnabled;
     private CompatVersion compatVersion;
 
     private ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
     private ClassLoader loader = contextLoader == null ? RubyInstanceConfig.class.getClassLoader() : contextLoader;
 
     private ClassCache<Script> classCache;
 
     // from CommandlineParser
     private List<String> loadPaths = new ArrayList<String>();
     private Set<String> excludedMethods = new HashSet<String>();
     private StringBuffer inlineScript = new StringBuffer();
     private boolean hasInlineScript = false;
     private String scriptFileName = null;
     private List<String> requiredLibraries = new ArrayList<String>();
     private boolean benchmarking = false;
     private boolean argvGlobalsOn = false;
     private boolean assumeLoop = false;
     private boolean assumePrinting = false;
     private Map optionGlobals = new HashMap();
     private boolean processLineEnds = false;
     private boolean split = false;
     // This property is a Boolean, to allow three values, so it can match MRI's nil, false and true
     private Boolean verbose = Boolean.FALSE;
     private boolean debug = false;
     private boolean showVersion = false;
     private boolean showBytecode = false;
     private boolean showCopyright = false;
     private boolean endOfArguments = false;
     private boolean shouldRunInterpreter = true;
     private boolean shouldPrintUsage = false;
     private boolean shouldPrintProperties=false;
     private boolean yarv = false;
     private boolean rubinius = false;
     private boolean yarvCompile = false;
     private KCode kcode = KCode.NONE;
     private String recordSeparator = "\n";
     private boolean shouldCheckSyntax = false;
     private String inputFieldSeparator = null;
     private boolean managementEnabled = true;
 
     private int safeLevel = 0;
 
     private String jrubyHome;
 
     public static final boolean FASTEST_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.fastest");
     public static final boolean BOXED_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.boxed");
     public static final boolean FASTOPS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.fastops");
     public static final boolean FRAMELESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.frameless");
     public static final boolean POSITIONLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.positionless");
     public static final boolean THREADLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.threadless");
     public static final boolean LAZYHANDLES_COMPILE = SafePropertyAccessor.getBoolean("jruby.compile.lazyHandles", false);
     public static final boolean INDEXED_METHODS
             = SafePropertyAccessor.getBoolean("jruby.indexed.methods");
     public static final boolean FORK_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.fork.enabled");
     public static final boolean POOLING_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.thread.pool.enabled");
     public static final int POOL_MAX
             = SafePropertyAccessor.getInt("jruby.thread.pool.max", Integer.MAX_VALUE);
     public static final int POOL_MIN
             = SafePropertyAccessor.getInt("jruby.thread.pool.min", 0);
     public static final int POOL_TTL
             = SafePropertyAccessor.getInt("jruby.thread.pool.ttl", 60);
 
     public static final boolean NATIVE_NET_PROTOCOL
             = SafePropertyAccessor.getBoolean("jruby.native.net.protocol", false);
 
     public static boolean FULL_TRACE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.debug.fullTrace", false);
 
     public static final String COMPILE_EXCLUDE
             = SafePropertyAccessor.getProperty("jruby.jit.exclude");
     public static boolean nativeEnabled = true;
     
 
     public static interface LoadServiceCreator {
         LoadService create(Ruby runtime);
 
         LoadServiceCreator DEFAULT = new LoadServiceCreator() {
                 public LoadService create(Ruby runtime) {
                     return new LoadService(runtime);
                 }
             };
     }
 
     private LoadServiceCreator creator = LoadServiceCreator.DEFAULT;
 
 
     static {
         String specVersion = null;
         try {
             specVersion = System.getProperty("jruby.bytecode.version");
             if (specVersion == null) {
                 specVersion = System.getProperty("java.specification.version");
             }
             if (System.getProperty("jruby.native.enabled") != null) {
                 nativeEnabled = Boolean.getBoolean("jruby.native.enabled");
             }
         } catch (SecurityException se) {
             nativeEnabled = false;
             specVersion = "1.5";
         }
         
         if (specVersion.equals("1.5")) {
             JAVA_VERSION = Opcodes.V1_5;
         } else {
             JAVA_VERSION = Opcodes.V1_6;
         }
     }
 
     public int characterIndex = 0;
 
     public RubyInstanceConfig() {
         if (Ruby.isSecurityRestricted())
             currentDirectory = "/";
         else {
             currentDirectory = JRubyFile.getFileProperty("user.dir");
         }
 
         samplingEnabled = SafePropertyAccessor.getBoolean("jruby.sampling.enabled", false);
         String compatString = SafePropertyAccessor.getProperty("jruby.compat.version", "RUBY1_8");
         if (compatString.equalsIgnoreCase("RUBY1_8")) {
             compatVersion = CompatVersion.RUBY1_8;
         } else if (compatString.equalsIgnoreCase("RUBY1_9")) {
             compatVersion = CompatVersion.RUBY1_9;
         } else {
             System.err.println("Compatibility version `" + compatString + "' invalid; use RUBY1_8 or RUBY1_9. Using RUBY1_8.");
             compatVersion = CompatVersion.RUBY1_8;
         }
 
         if (Ruby.isSecurityRestricted()) {
             compileMode = CompileMode.OFF;
             jitLogging = false;
             jitLoggingVerbose = false;
             jitLogEvery = 0;
             jitThreshold = -1;
             jitMax = 0;
             jitMaxSize = -1;
             managementEnabled = false;
         } else {
             String threshold = SafePropertyAccessor.getProperty("jruby.jit.threshold");
             String max = SafePropertyAccessor.getProperty("jruby.jit.max");
             String maxSize = SafePropertyAccessor.getProperty("jruby.jit.maxsize");
             
             if (COMPILE_EXCLUDE != null) {
                 String[] elements = COMPILE_EXCLUDE.split(",");
                 for (String element : elements) excludedMethods.add(element);
             }
             
             managementEnabled = SafePropertyAccessor.getBoolean("jruby.management.enabled", true);
             runRubyInProcess = SafePropertyAccessor.getBoolean("jruby.launch.inproc", true);
             boolean jitProperty = SafePropertyAccessor.getProperty("jruby.jit.enabled") != null;
             if (jitProperty) {
                 error.print("jruby.jit.enabled property is deprecated; use jruby.compile.mode=(OFF|JIT|FORCE) for -C, default, and +C flags");
                 compileMode = SafePropertyAccessor.getBoolean("jruby.jit.enabled") ? CompileMode.JIT : CompileMode.OFF;
             } else {
                 String jitModeProperty = SafePropertyAccessor.getProperty("jruby.compile.mode", "JIT");
 
                 if (jitModeProperty.equals("OFF")) {
                     compileMode = CompileMode.OFF;
                 } else if (jitModeProperty.equals("JIT")) {
                     compileMode = CompileMode.JIT;
                 } else if (jitModeProperty.equals("FORCE")) {
                     compileMode = CompileMode.FORCE;
                 } else {
                     error.print("jruby.compile.mode property must be OFF, JIT, FORCE, or unset; defaulting to JIT");
                     compileMode = CompileMode.JIT;
                 }
             }
             jitLogging = SafePropertyAccessor.getBoolean("jruby.jit.logging");
             jitLoggingVerbose = SafePropertyAccessor.getBoolean("jruby.jit.logging.verbose");
             String logEvery = SafePropertyAccessor.getProperty("jruby.jit.logEvery");
             jitLogEvery = logEvery == null ? 0 : Integer.parseInt(logEvery);
             jitThreshold = threshold == null ?
                     JIT_THRESHOLD : Integer.parseInt(threshold);
             jitMax = max == null ?
                     JIT_MAX_METHODS_LIMIT : Integer.parseInt(max);
             jitMaxSize = maxSize == null ?
                     JIT_MAX_SIZE_LIMIT : Integer.parseInt(maxSize);
         }
 
         // default ClassCache using jitMax as a soft upper bound
         classCache = new ClassCache<Script>(loader, jitMax);
 
         if (FORK_ENABLED) {
             error.print("WARNING: fork is highly unlikely to be safe or stable on the JVM. Have fun!\n");
         }
     }
 
     public LoadServiceCreator getLoadServiceCreator() {
         return creator;
     }
 
     public void setLoadServiceCreator(LoadServiceCreator creator) {
         this.creator = creator;
     }
 
     public LoadService createLoadService(Ruby runtime) {
         return this.creator.create(runtime);
     }
 
     public String getBasicUsageHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("Usage: jruby [switches] [--] [programfile] [arguments]\n")
                 .append("  -0[octal]       specify record separator (\0, if no argument)\n")
                 .append("  -a              autosplit mode with -n or -p (splits $_ into $F)\n")
                 .append("  -b              benchmark mode, times the script execution\n")
                 .append("  -c              check syntax only\n")
                 .append("  -Cdirectory     cd to directory, before executing your script\n")
                 .append("  -d              set debugging flags (set $DEBUG to true)\n")
                 .append("  -e 'command'    one line of script. Several -e's allowed. Omit [programfile]\n")
                 .append("  -Fpattern       split() pattern for autosplit (-a)\n")
                 //.append("  -i[extension]   edit ARGV files in place (make backup if extension supplied)\n")
                 .append("  -Idirectory     specify $LOAD_PATH directory (may be used more than once)\n")
                 .append("  -J[java option] pass an option on to the JVM (e.g. -J-Xmx512m)\n")
                 .append("                    use --properties to list JRuby properties\n")
                 .append("                    run 'java -help' for a list of other Java options\n")
                 .append("  -Kkcode         specifies code-set (e.g. -Ku for Unicode\n")
                 .append("  -l              enable line ending processing\n")
                 .append("  -n              assume 'while gets(); ... end' loop around your script\n")
                 .append("  -p              assume loop like -n but print line also like sed\n")
                 .append("  -rlibrary       require the library, before executing your script\n")
                 .append("  -s              enable some switch parsing for switches after script name\n")
                 .append("  -S              look for the script in bin or using PATH environment variable\n")
                 .append("  -T[level]       turn on tainting checks\n")
                 .append("  -v              print version number, then turn on verbose mode\n")
                 .append("  -w              turn warnings on for your script\n")
                 .append("  -W[level]       set warning level; 0=silence, 1=medium, 2=verbose (default)\n")
                 //.append("  -x[directory]   strip off text before #!ruby line and perhaps cd to directory\n")
                 .append("  -X[option]      enable extended option (omit option to list)\n")
                 .append("  --copyright     print the copyright\n")
                 .append("  --debug         sets the execution mode most suitable for debugger functionality\n")
                 .append("  --jdb           runs JRuby process under JDB\n")
                 .append("  --properties    List all configuration Java properties (pass -J-Dproperty=value)\n")
                 .append("  --sample        run with profiling using the JVM's sampling profiler\n")
                 .append("  --client        use the non-optimizing \"client\" JVM (improves startup; default)\n")
                 .append("  --server        use the optimizing \"server\" JVM (improves perf)\n")
                 .append("  --manage        enable remote JMX management and monitoring of the VM and JRuby\n")
                 .append("  --1.8           specify Ruby 1.8.x compatibility (default)\n")
                 .append("  --1.9           specify Ruby 1.9.x compatibility\n")
                 .append("  --bytecode      show the JVM bytecode produced by compiling specified code\n")
                 .append("  --version       print the version\n");
 
         return sb.toString();
     }
 
     public String getExtendedHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("These flags are for extended JRuby options.\n")
                 .append("Specify them by passing -X<option>\n")
                 .append("  -O              run with ObjectSpace disabled (default; improves performance)\n")
                 .append("  +O              run with ObjectSpace enabled (reduces performance)\n")
                 .append("  -C              disable all compilation\n")
                 .append("  +C              force compilation of all scripts before they are run (except eval)\n")
                 .append("  -y              read a YARV-compiled Ruby script and run that (EXPERIMENTAL)\n")
                 .append("  -Y              compile a Ruby script into YARV bytecodes and run this (EXPERIMENTAL)\n")
                 .append("  -R              read a Rubinius-compiled Ruby script and run that (EXPERIMENTAL)\n");
 
         return sb.toString();
     }
 
     public String getPropertyHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("These properties can be used to alter runtime behavior for perf or compatibility.\n")
                 .append("Specify them by passing -J-D<property>=<value>\n")
                 .append("\nCOMPILER SETTINGS:\n")
                 .append("    jruby.compile.mode=JIT|FORCE|OFF\n")
                 .append("       Set compilation mode. JIT is default; FORCE compiles all, OFF disables\n")
                 .append("    jruby.compile.fastest=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on all experimental compiler optimizations\n")
                 .append("    jruby.compile.boxed=true|false\n")
                 .append("       (EXPERIMENTAL) Use boxed variables; this can speed up some methods. Default is false\n")
                 .append("    jruby.compile.frameless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on frameless compilation where possible\n")
                 .append("    jruby.compile.positionless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on compilation that avoids updating Ruby position info. Default is false\n")
                 .append("    jruby.compile.threadless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on compilation without polling for \"unsafe\" thread events. Default is false\n")
                 .append("    jruby.compile.fastops=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on fast operators for Fixnum. Default is false\n")
                 .append("    jruby.compile.chainsize=<line count>\n")
                 .append("       Set the number of lines at which compiled bodies are \"chained\". Default is " + CHAINED_COMPILE_LINE_COUNT_DEFAULT + "\n")
                 .append("    jruby.compile.lazyHandles=true|false\n")
                 .append("       Generate method bindings (handles) for compiled methods lazily. Default is false.")
                 .append("\nJIT SETTINGS:\n")
                 .append("    jruby.jit.threshold=<invocation count>\n")
                 .append("       Set the JIT threshold to the specified method invocation count. Default is " + JIT_THRESHOLD + ".\n")
                 .append("    jruby.jit.max=<method count>\n")
                 .append("       Set the max count of active methods eligible for JIT-compilation.\n")
                 .append("       Default is " + JIT_MAX_METHODS_LIMIT + " per runtime. A value of 0 disables JIT, -1 disables max.\n")
                 .append("    jruby.jit.maxsize=<jitted method size (full .class)>\n")
                 .append("       Set the maximum full-class byte size allowed for jitted methods. Default is Integer.MAX_VALUE\n")
                 .append("    jruby.jit.logging=true|false\n")
                 .append("       Enable JIT logging (reports successful compilation). Default is false\n")
                 .append("    jruby.jit.logging.verbose=true|false\n")
                 .append("       Enable verbose JIT logging (reports failed compilation). Default is false\n")
                 .append("    jruby.jit.logEvery=<method count>\n")
                 .append("       Log a message every n methods JIT compiled. Default is 0 (off).\n")
                 .append("    jruby.jit.exclude=<ClsOrMod,ClsOrMod::method_name,-::method_name>\n")
                 .append("       Exclude methods from JIT by class/module short name, c/m::method_name,\n")
                 .append("       or -::method_name for anon/singleton classes/modules. Comma-delimited.\n")
                 .append("\nNATIVE SUPPORT:\n")
                 .append("    jruby.native.enabled=true|false\n")
                 .append("       Enable/disable native extensions (like JNA for non-Java APIs; Default is true\n")
                 .append("       (This affects all JRuby instances in a given JVM)\n")
                 .append("    jruby.native.verbose=true|false\n")
                 .append("       Enable verbose logging of native extension loading. Default is false.\n")
                 .append("    jruby.fork.enabled=true|false\n")
                 .append("       (EXPERIMENTAL, maybe dangerous) Enable fork(2) on platforms that support it.\n")
                 .append("\nTHREAD POOLING:\n")
                 .append("    jruby.thread.pool.enabled=true|false\n")
                 .append("       Enable reuse of native backing threads via a thread pool. Default is false.\n")
                 .append("    jruby.thread.pool.min=<min thread count>\n")
                 .append("       The minimum number of threads to keep alive in the pool. Default is 0.\n")
                 .append("    jruby.thread.pool.max=<max thread count>\n")
                 .append("       The maximum number of threads to allow in the pool. Default is unlimited.\n")
                 .append("    jruby.thread.pool.ttl=<time to live, in seconds>\n")
                 .append("       The maximum number of seconds to keep alive an idle thread. Default is 60.\n")
                 .append("\nMISCELLANY:\n")
                 .append("    jruby.compat.version=RUBY1_8|RUBY1_9\n")
                 .append("       Specify the major Ruby version to be compatible with; Default is RUBY1_8\n")
                 .append("    jruby.indexed.methods=true|false\n")
                 .append("       Generate \"invokers\" for core classes using a single indexed class\n")
                 .append("    jruby.objectspace.enabled=true|false\n")
                 .append("       Enable or disable ObjectSpace.each_object (default is disabled)\n")
                 .append("    jruby.launch.inproc=true|false\n")
                 .append("       Set in-process launching of e.g. system('ruby ...'). Default is true\n")
                 .append("    jruby.bytecode.version=1.5|1.6\n")
                 .append("       Set bytecode version for JRuby to generate. Default is current JVM version.\n")
                 .append("    jruby.management.enabled=true|false\n")
                 .append("       Set whether JMX management is enabled. Default is true.\n")
                 .append("    jruby.debug.fullTrace=true|false\n")
                 .append("       Set whether full traces are enabled (c-call/c-return). Default is false.\n");
 
         return sb.toString();
     }
 
     public String getVersionString() {
         String ver = Constants.RUBY_VERSION;
         switch (compatVersion) {
         case RUBY1_8:
             ver = Constants.RUBY_VERSION;
             break;
         case RUBY1_9:
             ver = Constants.RUBY1_9_VERSION;
             break;
         }
 
         String fullVersion = String.format(
                 "jruby %s (ruby %s patchlevel %s) (%s rev %s) [%s-java]\n",
                 Constants.VERSION, ver, Constants.RUBY_PATCHLEVEL,
                 Constants.COMPILE_DATE, Constants.REVISION,
                 SafePropertyAccessor.getProperty("os.arch", "unknown")
                 );
 
         return fullVersion;
     }
 
     public String getCopyrightString() {
         return "JRuby - Copyright (C) 2001-2008 The JRuby Community (and contribs)\n";
     }
 
     public void processArguments(String[] arguments) {
         new ArgumentProcessor(arguments).processArguments();
     }
 
     public CompileMode getCompileMode() {
         return compileMode;
     }
 
     public void setCompileMode(CompileMode compileMode) {
         this.compileMode = compileMode;
     }
 
     public boolean isJitLogging() {
         return jitLogging;
     }
 
     public boolean isJitLoggingVerbose() {
         return jitLoggingVerbose;
     }
 
     public int getJitLogEvery() {
         return jitLogEvery;
     }
 
     public boolean isSamplingEnabled() {
         return samplingEnabled;
     }
 
     public int getJitThreshold() {
         return jitThreshold;
     }
 
     public int getJitMax() {
         return jitMax;
     }
 
     public int getJitMaxSize() {
         return jitMaxSize;
     }
 
     public boolean isRunRubyInProcess() {
         return runRubyInProcess;
     }
 
     public void setRunRubyInProcess(boolean flag) {
         this.runRubyInProcess = flag;
     }
 
     public void setInput(InputStream newInput) {
         input = newInput;
     }
 
     public InputStream getInput() {
         return input;
     }
 
     public CompatVersion getCompatVersion() {
         return compatVersion;
     }
 
     public void setOutput(PrintStream newOutput) {
         output = newOutput;
     }
 
     public PrintStream getOutput() {
         return output;
     }
 
     public void setError(PrintStream newError) {
         error = newError;
     }
 
     public PrintStream getError() {
         return error;
     }
 
     public void setCurrentDirectory(String newCurrentDirectory) {
         currentDirectory = newCurrentDirectory;
     }
 
     public String getCurrentDirectory() {
         return currentDirectory;
     }
 
     public void setProfile(Profile newProfile) {
         profile = newProfile;
     }
 
     public Profile getProfile() {
         return profile;
     }
 
     public void setObjectSpaceEnabled(boolean newObjectSpaceEnabled) {
         objectSpaceEnabled = newObjectSpaceEnabled;
     }
 
     public boolean isObjectSpaceEnabled() {
         return objectSpaceEnabled;
     }
 
     public void setEnvironment(Map newEnvironment) {
         environment = newEnvironment;
     }
 
     public Map getEnvironment() {
         return environment;
     }
 
     public ClassLoader getLoader() {
         return loader;
     }
 
     public void setLoader(ClassLoader loader) {
         // Setting the loader needs to reset the class cache
         if(this.loader != loader) {
             this.classCache = new ClassCache<Script>(loader, this.classCache.getMax());
         }
         this.loader = loader;
     }
 
     public String[] getArgv() {
         return argv;
     }
 
     public void setArgv(String[] argv) {
         this.argv = argv;
     }
 
     public String getJRubyHome() {
         if (jrubyHome == null) {
             if (Ruby.isSecurityRestricted()) {
                 return "SECURITY RESTRICTED";
             }
             jrubyHome = verifyHome(SafePropertyAccessor.getProperty("jruby.home",
                     SafePropertyAccessor.getProperty("user.home") + "/.jruby"));
 
             try {
                 // This comment also in rbConfigLibrary
                 // Our shell scripts pass in non-canonicalized paths, but even if we didn't
                 // anyone who did would become unhappy because Ruby apps expect no relative
                 // operators in the pathname (rubygems, for example).
                 jrubyHome = new NormalizedFile(jrubyHome).getCanonicalPath();
             } catch (IOException e) { }
 
             jrubyHome = new NormalizedFile(jrubyHome).getAbsolutePath();
         }
         return jrubyHome;
     }
 
     public void setJRubyHome(String home) {
         jrubyHome = verifyHome(home);
     }
 
     // We require the home directory to be absolute
     private String verifyHome(String home) {
         if (home.equals(".")) {
             home = System.getProperty("user.dir");
         }
         if (!home.startsWith("file:")) {
             NormalizedFile f = new NormalizedFile(home);
             if (!f.isAbsolute()) {
                 home = f.getAbsolutePath();
             }
             f.mkdirs();
         }
         return home;
     }
 
     private class ArgumentProcessor {
         private String[] arguments;
         private int argumentIndex = 0;
 
         public ArgumentProcessor(String[] arguments) {
             this.arguments = arguments;
         }
 
         public void processArguments() {
             while (argumentIndex < arguments.length && isInterpreterArgument(arguments[argumentIndex])) {
                 processArgument();
                 argumentIndex++;
             }
 
             if (!hasInlineScript && scriptFileName == null) {
                 if (argumentIndex < arguments.length) {
                     setScriptFileName(arguments[argumentIndex]); //consume the file name
                     argumentIndex++;
                 }
             }
 
             processArgv();
         }
 
         private void processArgv() {
             List<String> arglist = new ArrayList<String>();
             for (; argumentIndex < arguments.length; argumentIndex++) {
                 String arg = arguments[argumentIndex];
                 if (argvGlobalsOn && arg.startsWith("-")) {
                     arg = arg.substring(1);
                     if (arg.indexOf('=') > 0) {
                         String[] keyvalue = arg.split("=", 2);
                         optionGlobals.put(keyvalue[0], keyvalue[1]);
                     } else {
                         optionGlobals.put(arg, null);
                     }
                 } else {
                     argvGlobalsOn = false;
                     arglist.add(arg);
                 }
             }
 
             // Remaining arguments are for the script itself
             argv = arglist.toArray(new String[arglist.size()]);
         }
 
         private boolean isInterpreterArgument(String argument) {
             return (argument.charAt(0) == '-' || argument.charAt(0) == '+') && !endOfArguments;
         }
 
         private String getArgumentError(String additionalError) {
             return "jruby: invalid argument\n" + additionalError + "\n";
         }
 
         private void processArgument() {
             String argument = arguments[argumentIndex];
             FOR : for (characterIndex = 1; characterIndex < argument.length(); characterIndex++) {
                 switch (argument.charAt(characterIndex)) {
                 case '0': {
                     String temp = grabOptionalValue();
                     if (null == temp) {
                         recordSeparator = "\u0000";
                     } else if (temp.equals("0")) {
                         recordSeparator = "\n\n";
                     } else if (temp.equals("777")) {
                         recordSeparator = "\uFFFF"; // Specify something that can't separate
                     } else {
                         try {
                             int val = Integer.parseInt(temp, 8);
                             recordSeparator = "" + (char) val;
                         } catch (Exception e) {
                             MainExitException mee = new MainExitException(1, getArgumentError(" -0 must be followed by either 0, 777, or a valid octal value"));
                             mee.setUsageError(true);
                             throw mee;
                         }
                     }
                     break FOR;
                 }
                 case 'a':
                     split = true;
                     break;
                 case 'b':
                     benchmarking = true;
                     break;
                 case 'c':
                     shouldCheckSyntax = true;
                     break;
                 case 'C':
                     try {
                         String saved = grabValue(getArgumentError(" -C must be followed by a directory expression"));
                         File base = new File(currentDirectory);
                         File newDir = new File(saved);
                         if (newDir.isAbsolute()) {
                             currentDirectory = newDir.getCanonicalPath();
                         } else {
                             currentDirectory = new File(base, newDir.getPath()).getCanonicalPath();
                         }
                         if (!(new File(currentDirectory).isDirectory())) {
                             MainExitException mee = new MainExitException(1, "jruby: Can't chdir to " + saved + " (fatal)");
                             mee.setUsageError(true);
                             throw mee;
                         }
                     } catch (IOException e) {
                         MainExitException mee = new MainExitException(1, getArgumentError(" -C must be followed by a valid directory"));
                         mee.setUsageError(true);
                         throw mee;
                     }
                     break;
                 case 'd':
                     debug = true;
                     verbose = Boolean.TRUE;
                     break;
                 case 'e':
                     inlineScript.append(grabValue(getArgumentError(" -e must be followed by an expression to evaluate")));
                     inlineScript.append('\n');
                     hasInlineScript = true;
                     break FOR;
                 case 'F':
                     inputFieldSeparator = grabValue(getArgumentError(" -F must be followed by a pattern for input field separation"));
                     break;
                 case 'h':
                     shouldPrintUsage = true;
                     shouldRunInterpreter = false;
                     break;
                 // FIXME: -i flag not supported
 //                    case 'i' :
 //                        break;
                 case 'I':
                     String s = grabValue(getArgumentError("-I must be followed by a directory name to add to lib path"));
                     String[] ls = s.split(java.io.File.pathSeparator);
                     for (int i = 0; i < ls.length; i++) {
                         loadPaths.add(ls[i]);
                     }
                     break FOR;
                 case 'K':
                     // FIXME: No argument seems to work for -K in MRI plus this should not
                     // siphon off additional args 'jruby -K ~/scripts/foo'.  Also better error
                     // processing.
                     String eArg = grabValue(getArgumentError("provide a value for -K"));
                     kcode = KCode.create(null, eArg);
                     break;
                 case 'l':
                     processLineEnds = true;
                     break;
                 case 'n':
                     assumeLoop = true;
                     break;
                 case 'p':
                     assumePrinting = true;
                     assumeLoop = true;
                     break;
                 case 'r':
                     requiredLibraries.add(grabValue(getArgumentError("-r must be followed by a package to require")));
                     break FOR;
                 case 's' :
                     argvGlobalsOn = true;
                     break;
                 case 'S':
                     runBinScript();
                     break FOR;
                 case 'T' :{
                     String temp = grabOptionalValue();
                     int value = 1;
 
                     if(temp!=null) {
                         try {
                             value = Integer.parseInt(temp, 8);
                         } catch(Exception e) {
                             value = 1;
                         }
                     }
 
                     safeLevel = value;
 
                     break FOR;
                 }
                 case 'v':
                     verbose = Boolean.TRUE;
                     setShowVersion(true);
                     break;
                 case 'w':
                     verbose = Boolean.TRUE;
                     break;
                 case 'W': {
                     String temp = grabOptionalValue();
                     int value = 2;
                     if (null != temp) {
                         if (temp.equals("2")) {
                             value = 2;
                         } else if (temp.equals("1")) {
                             value = 1;
                         } else if (temp.equals("0")) {
                             value = 0;
                         } else {
                             MainExitException mee = new MainExitException(1, getArgumentError(" -W must be followed by either 0, 1, 2 or nothing"));
                             mee.setUsageError(true);
                             throw mee;
                         }
                     }
                     switch (value) {
                     case 0:
                         verbose = null;
                         break;
                     case 1:
                         verbose = Boolean.FALSE;
                         break;
                     case 2:
                         verbose = Boolean.TRUE;
                         break;
                     }
 
 
                     break FOR;
                 }
                 // FIXME: -x flag not supported
 //                    case 'x' :
 //                        break;
                 case 'X':
                     String extendedOption = grabOptionalValue();
 
                     if (extendedOption == null) {
                         throw new MainExitException(0, "jruby: missing extended option, listing available options\n" + getExtendedHelp());
                     } else if (extendedOption.equals("-O")) {
                         objectSpaceEnabled = false;
                     } else if (extendedOption.equals("+O")) {
                         objectSpaceEnabled = true;
                     } else if (extendedOption.equals("-C")) {
                         compileMode = CompileMode.OFF;
                     } else if (extendedOption.equals("+C")) {
                         compileMode = CompileMode.FORCE;
                     } else if (extendedOption.equals("y")) {
                         yarv = true;
                     } else if (extendedOption.equals("Y")) {
                         yarvCompile = true;
                     } else if (extendedOption.equals("R")) {
                         rubinius = true;
                     } else {
                         MainExitException mee =
                                 new MainExitException(1, "jruby: invalid extended option " + extendedOption + " (-X will list valid options)\n");
                         mee.setUsageError(true);
 
                         throw mee;
                     }
                     break FOR;
                 case '-':
                     if (argument.equals("--command") || argument.equals("--bin")) {
                         characterIndex = argument.length();
                         runBinScript();
                         break;
                     } else if (argument.equals("--compat")) {
                         characterIndex = argument.length();
                         compatVersion = CompatVersion.getVersionFromString(grabValue(getArgumentError("--compat must be RUBY1_8 or RUBY1_9")));
                         if (compatVersion == null) {
                             compatVersion = CompatVersion.RUBY1_8;
                         }
                         break FOR;
                     } else if (argument.equals("--copyright")) {
                         setShowCopyright(true);
                         shouldRunInterpreter = false;
                         break FOR;
                     } else if (argument.equals("--debug")) {
                         compileMode = CompileMode.OFF;
                         FULL_TRACE_ENABLED = true;
                         System.setProperty("jruby.reflection", "true");
                         break FOR;
                     } else if (argument.equals("--jdb")) {
                         debug = true;
                         verbose = Boolean.TRUE;
                         break;
                     } else if (argument.equals("--help")) {
                         shouldPrintUsage = true;
                         shouldRunInterpreter = false;
                         break;
                     } else if (argument.equals("--properties")) {
                         shouldPrintProperties = true;
                         shouldRunInterpreter = false;
                         break;
                     } else if (argument.equals("--version")) {
                         setShowVersion(true);
                         break FOR;
                     } else if (argument.equals("--bytecode")) {
                         setShowBytecode(true);
                         break FOR;
                     } else {
                         if (argument.equals("--")) {
                             // ruby interpreter compatibilty
                             // Usage: ruby [switches] [--] [programfile] [arguments])
                             endOfArguments = true;
                             break;
                         }
                     }
                 default:
                     throw new MainExitException(1, "jruby: unknown option " + argument);
                 }
             }
         }
 
         private void runBinScript() {
             String scriptName = grabValue("jruby: provide a bin script to execute");
             if (scriptName.equals("irb")) {
                 scriptName = "jirb";
             }
 
             scriptFileName = scriptName;
 
             if (!new File(scriptFileName).exists()) {
                 try {
                     String jrubyHome = JRubyFile.create(System.getProperty("user.dir"), JRubyFile.getFileProperty("jruby.home")).getCanonicalPath();
                     scriptFileName = JRubyFile.create(jrubyHome + JRubyFile.separator + "bin", scriptName).getCanonicalPath();
                 } catch (IOException io) {
                     MainExitException mee = new MainExitException(1, "jruby: Can't determine script filename");
                     mee.setUsageError(true);
                     throw mee;
                 }
             }
 
             // route 'gem' through ruby code in case we're running out of the complete jar
             if (scriptName.equals("gem") || !new File(scriptFileName).exists()) {
                 requiredLibraries.add("jruby/commands");
                 inlineScript.append("JRuby::Commands." + scriptName);
                 inlineScript.append("\n");
                 hasInlineScript = true;
             }
             endOfArguments = true;
         }
 
         private String grabValue(String errorMessage) {
             characterIndex++;
             if (characterIndex < arguments[argumentIndex].length()) {
                 return arguments[argumentIndex].substring(characterIndex);
             }
             argumentIndex++;
             if (argumentIndex < arguments.length) {
                 return arguments[argumentIndex];
             }
 
             MainExitException mee = new MainExitException(1, errorMessage);
             mee.setUsageError(true);
 
             throw mee;
         }
 
         private String grabOptionalValue() {
             characterIndex++;
             if (characterIndex < arguments[argumentIndex].length()) {
                 return arguments[argumentIndex].substring(characterIndex);
             }
             return null;
         }
     }
 
     public byte[] inlineScript() {
-        try {
-            return inlineScript.toString().getBytes("UTF-8");
-        } catch (UnsupportedEncodingException e) {
-            return inlineScript.toString().getBytes();
-        }
+        return inlineScript.toString().getBytes();
     }
 
     public List<String> requiredLibraries() {
         return requiredLibraries;
     }
 
     public List<String> loadPaths() {
         return loadPaths;
     }
 
     public boolean shouldRunInterpreter() {
         if(isShowVersion() && (hasInlineScript || scriptFileName != null)) {
             return true;
         }
         return isShouldRunInterpreter();
     }
 
     public boolean shouldPrintUsage() {
         return shouldPrintUsage;
     }
 
     public boolean shouldPrintProperties() {
         return shouldPrintProperties;
     }
 
     private boolean isSourceFromStdin() {
         return getScriptFileName() == null;
     }
 
     public boolean isInlineScript() {
         return hasInlineScript;
     }
 
     public InputStream getScriptSource() {
         try {
             // KCode.NONE is used because KCODE does not affect parse in Ruby 1.8
             // if Ruby 2.0 encoding pragmas are implemented, this will need to change
             if (hasInlineScript) {
                 return new ByteArrayInputStream(inlineScript());
             } else if (isSourceFromStdin()) {
                 // can't use -v and stdin
                 if (isShowVersion()) {
                     return null;
                 }
                 return getInput();
             } else {
                 File file = JRubyFile.create(getCurrentDirectory(), getScriptFileName());
                 return new BufferedInputStream(new FileInputStream(file));
             }
         } catch (IOException e) {
             throw new MainExitException(1, "Error opening script file: " + e.getMessage());
         }
     }
 
     public String displayedFileName() {
         if (hasInlineScript) {
             if (scriptFileName != null) {
                 return scriptFileName;
             } else {
                 return "-e";
             }
         } else if (isSourceFromStdin()) {
             return "-";
         } else {
             return getScriptFileName();
         }
     }
 
     private void setScriptFileName(String scriptFileName) {
         this.scriptFileName = scriptFileName;
     }
 
     public String getScriptFileName() {
         return scriptFileName;
     }
 
     public boolean isBenchmarking() {
         return benchmarking;
     }
 
     public boolean isAssumeLoop() {
         return assumeLoop;
     }
 
     public boolean isAssumePrinting() {
         return assumePrinting;
     }
 
     public boolean isProcessLineEnds() {
         return processLineEnds;
     }
 
     public boolean isSplit() {
         return split;
     }
 
     public boolean isVerbose() {
         return verbose == Boolean.TRUE;
     }
 
     public Boolean getVerbose() {
         return verbose;
     }
 
     public boolean isDebug() {
         return debug;
     }
 
     public boolean isShowVersion() {
         return showVersion;
     }
     
     public boolean isShowBytecode() {
         return showBytecode;
     }
 
     public boolean isShowCopyright() {
         return showCopyright;
     }
 
     protected void setShowVersion(boolean showVersion) {
         this.showVersion = showVersion;
     }
     
     protected void setShowBytecode(boolean showBytecode) {
         this.showBytecode = showBytecode;
     }
 
     protected void setShowCopyright(boolean showCopyright) {
         this.showCopyright = showCopyright;
     }
 
     public boolean isShouldRunInterpreter() {
         return shouldRunInterpreter;
     }
 
     public boolean isShouldCheckSyntax() {
         return shouldCheckSyntax;
     }
 
     public boolean isYARVEnabled() {
         return yarv;
     }
 
     public String getInputFieldSeparator() {
         return inputFieldSeparator;
     }
 
     public boolean isRubiniusEnabled() {
         return rubinius;
     }
 
     public boolean isYARVCompileEnabled() {
         return yarvCompile;
     }
 
     public KCode getKCode() {
         return kcode;
     }
 
     public String getRecordSeparator() {
         return recordSeparator;
     }
 
     public int getSafeLevel() {
         return safeLevel;
     }
 
     public void setRecordSeparator(String recordSeparator) {
         this.recordSeparator = recordSeparator;
     }
 
     public ClassCache getClassCache() {
         return classCache;
     }
 
     public void setClassCache(ClassCache classCache) {
         this.classCache = classCache;
     }
 
     public Map getOptionGlobals() {
         return optionGlobals;
     }
     
     public boolean isManagementEnabled() {
         return managementEnabled;
     }
     
     public Set getExcludedMethods() {
         return excludedMethods;
     }
     
 }
diff --git a/src/org/jruby/util/JRubyFile.java b/src/org/jruby/util/JRubyFile.java
index 4679fb6b74..e7d97c6b2d 100644
--- a/src/org/jruby/util/JRubyFile.java
+++ b/src/org/jruby/util/JRubyFile.java
@@ -1,201 +1,196 @@
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
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 /**
  * $Id$
  */
 package org.jruby.util;
 
 import java.io.File;
 import java.io.FileFilter;
 import java.io.FilenameFilter;
 import java.io.IOException;
 
 import org.jruby.Ruby;
 
 /**
  * <p>This file acts as an alternative to NormalizedFile, due to the problems with current working 
  * directory.</p>
  *
  */
 public class JRubyFile extends File {
     private static final long serialVersionUID = 435364547567567L;
 
     public static JRubyFile create(String cwd, String pathname) {
-        try {
-            pathname = new String(pathname.getBytes("ISO-8859-1"), "UTF-8");
-        } catch (java.io.UnsupportedEncodingException ex) {
-            // NOT REACHED HERE
-        }
         return createNoUnicodeConversion(cwd, pathname);
     }
 
     private static JRubyFile createNoUnicodeConversion(String cwd, String pathname) {
         if (pathname == null || pathname.equals("") || Ruby.isSecurityRestricted()) {
             return JRubyNonExistentFile.NOT_EXIST;
         }
         File internal = new File(pathname);
         if(!internal.isAbsolute()) {
             internal = new File(cwd,pathname);
             if(!internal.isAbsolute()) {
                 throw new IllegalArgumentException("Neither current working directory ("+cwd+") nor pathname ("+pathname+") led to an absolute path");
             }
         }
         return new JRubyFile(internal);
     }
 
     public static String getFileProperty(String property) {
         String value = SafePropertyAccessor.getProperty(property, "/");
         
         return value.replace(File.separatorChar, '/');
     }
 
     private JRubyFile(File file) {
         this(file.getAbsolutePath());
     }
 
     protected JRubyFile(String filename) {
         super(filename);
     }
 
     public String getAbsolutePath() {
         return new File(super.getPath()).getAbsolutePath().replace(File.separatorChar, '/'); 
     }
 
     public String getCanonicalPath() throws IOException {
         String canonicalPath = super.getCanonicalPath().replace(File.separatorChar, '/');
         
         // Java 1.4 canonicalPath does not strip off '.'
         if (canonicalPath.endsWith("/.")) {
             canonicalPath = canonicalPath.substring(0, canonicalPath.length() - 1);
         }
         
         return canonicalPath;
     }
 
     public String getPath() {
         return super.getPath().replace(File.separatorChar, '/');
     }
 
     public String toString() {
         return super.toString().replace(File.separatorChar, '/');
     }
 
     public File getAbsoluteFile() {
         return new JRubyFile(getAbsolutePath());
     }
 
     public File getCanonicalFile() throws IOException {
         return new JRubyFile(getCanonicalPath());
     }
 
     public String getParent() {
         String par = super.getParent();
         if (par != null) {
             par = par.replace(File.separatorChar, '/');
         }
         return par;
     }
 
     public File getParentFile() {
         String par = getParent();
         if (par == null) {
             return this;
         } else {
             return new JRubyFile(par);
         }
     }
     
     public static File[] listRoots() {
         File[] roots = File.listRoots();
         JRubyFile[] smartRoots = new JRubyFile[roots.length];
         for(int i = 0, j = roots.length; i < j; i++) {
             smartRoots[i] = new JRubyFile(roots[i].getPath());
         }
         return smartRoots;
     }
     
     public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
         return new JRubyFile(File.createTempFile(prefix, suffix,directory));
     }
     
     public static File createTempFile(String prefix, String suffix) throws IOException {
         return new JRubyFile(File.createTempFile(prefix, suffix));
     }
 
     public String[] list(FilenameFilter filter) {
         String[] files = super.list(filter);
         if (files == null) {
             return null;
         }
         
         String[] smartFiles = new String[files.length];
         for (int i = 0; i < files.length; i++) {
             smartFiles[i] = files[i].replace(File.separatorChar, '/');
         }
         return smartFiles;
     }
 
     public File[] listFiles() {
         File[] files = super.listFiles();
         if (files == null) {
             return null;
         }
         
         JRubyFile[] smartFiles = new JRubyFile[files.length];
         for (int i = 0, j = files.length; i < j; i++) {
             smartFiles[i] = createNoUnicodeConversion(super.getAbsolutePath(), files[i].getPath());
         }
         return smartFiles;
     }
 
     public File[] listFiles(final FileFilter filter) {
         final File[] files = super.listFiles(filter);
         if (files == null) {
             return null;
         }
         
         JRubyFile[] smartFiles = new JRubyFile[files.length];
         for (int i = 0,j = files.length; i < j; i++) {
             smartFiles[i] = createNoUnicodeConversion(super.getAbsolutePath(), files[i].getPath());
         }
         return smartFiles;
     }
 
     public File[] listFiles(final FilenameFilter filter) {
         final File[] files = super.listFiles(filter);
         if (files == null) {
             return null;
         }
         
         JRubyFile[] smartFiles = new JRubyFile[files.length];
         for (int i = 0,j = files.length; i < j; i++) {
             smartFiles[i] = createNoUnicodeConversion(super.getAbsolutePath(), files[i].getPath());
         }
         return smartFiles;
     }
 }
