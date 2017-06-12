diff --git a/rubyspecs.revision b/rubyspecs.revision
index 40626fb306..7092eeab84 100644
--- a/rubyspecs.revision
+++ b/rubyspecs.revision
@@ -1,6 +1,6 @@
 # These are the pointers to the 'stable/frozen' versions of
 # mspec and rubyspecs, used to for our CI runs.
 
 mspec.revision=0faab128f7b7b9cebe71a48cce8f4b7502ab2995
 
-rubyspecs.revision=71a0fbeee14056b2896c2f69cd81b1423aa9705a
+rubyspecs.revision=6264df69a560e074ce43b8e8c24a38d3364f5804
diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 630ab9bcaf..aa506ee4eb 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1185 +1,1189 @@
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
 
 import org.jcodings.Encoding;
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
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
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
 public class RubyFile extends RubyIO implements EncodingCapable {
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
 
     public Encoding getEncoding() {
         return null;
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
 
+    public String getPath() {
+        return path;
+    }
+
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
             IRubyObject fd = TypeConverter.convertToTypeWithCheck(args[0], getRuntime().getFixnum(), "to_int");
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
         
         path = filename.convertToString().getUnicodeValue();
         
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
 
     protected void sysopenInternal(String path, ModeFlags modes, int perm) throws InvalidValueException {
         openFile = new OpenFile();
         
         openFile.setPath(path);
         openFile.setMode(modes.getOpenFileFlags());
         
         ChannelDescriptor descriptor = sysopen(path, modes, perm);
         openFile.setMainStream(fdopen(descriptor, modes));
         
         registerDescriptor(descriptor);
     }
     
     protected void openInternal(String path, String modeString) throws InvalidValueException {
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
 
             RubyString fileString = filename.convertToString();
             if (!RubyFileTest.exist_p(filename, fileString).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == runtime.getPosix().chown(fileString.getUnicodeValue(), owner, group);
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject dirname(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyString filename = RubyString.stringValue(arg);
         
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
         return context.getRuntime().newFileStat(filename.convertToString().getUnicodeValue(), true).ftype();
     }
 
     private static String inspectJoin(ThreadContext context, IRubyObject recv, RubyArray parent, RubyArray array) {
         Ruby runtime = context.getRuntime();
         
         // If already inspecting, there is no need to register/unregister again.
         if (runtime.isInspecting(parent)) return join(context, recv, array).toString();
 
         try {
diff --git a/src/org/jruby/RubyFileTest.java b/src/org/jruby/RubyFileTest.java
index 316b89e1b9..5bef0ac699 100644
--- a/src/org/jruby/RubyFileTest.java
+++ b/src/org/jruby/RubyFileTest.java
@@ -1,277 +1,279 @@
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
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.exceptions.RaiseException;
 
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.JRubyFile;
 
 @JRubyModule(name="FileTest")
 public class RubyFileTest {
     public static RubyModule createFileTestModule(Ruby runtime) {
         RubyModule fileTestModule = runtime.defineModule("FileTest");
         runtime.setFileTest(fileTestModule);
         
         fileTestModule.defineAnnotatedMethods(RubyFileTest.class);
         
         return fileTestModule;
     }
 
     @JRubyMethod(name = "blockdev?", required = 1, module = true)
     public static IRubyObject blockdev_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isBlockDev());
     }
     
     @JRubyMethod(name = "chardev?", required = 1, module = true)
     public static IRubyObject chardev_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isCharDev());
     }
 
     @JRubyMethod(name = "directory?", required = 1, module = true)
     public static IRubyObject directory_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isDirectory());
     }
     
     @JRubyMethod(name = "executable?", required = 1, module = true)
     public static IRubyObject executable_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isExecutable());
     }
     
     @JRubyMethod(name = "executable_real?", required = 1, module = true)
     public static IRubyObject executable_real_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isExecutableReal());
     }
     
     @JRubyMethod(name = {"exist?", "exists?"}, required = 1, module = true)
     public static IRubyObject exist_p(IRubyObject recv, IRubyObject filename) {
         if (Ruby.isSecurityRestricted()) {
             return recv.getRuntime().getFalse();
         }
 
         if(filename.convertToString().toString().startsWith("file:")) {
             String file = filename.convertToString().toString().substring(5);
             int bang = file.indexOf('!');
             if (bang == -1 || bang == file.length() - 1) {
                 return recv.getRuntime().getFalse();
             }
             String jar = file.substring(0, bang);
             String after = file.substring(bang + 2);
             try {
                 java.util.jar.JarFile jf = new java.util.jar.JarFile(jar);
                 if(jf.getJarEntry(after) != null) {
                     return recv.getRuntime().getTrue();
                 } else {
                     return recv.getRuntime().getFalse();
                 }
             } catch(Exception e) {
                 return recv.getRuntime().getFalse();
             }
         }
 
         return recv.getRuntime().newBoolean(file(filename).exists());
     }
 
     @JRubyMethod(name = "file?", required = 1, module = true)
     public static RubyBoolean file_p(IRubyObject recv, IRubyObject filename) {
         JRubyFile file = file(filename);
 
         return filename.getRuntime().newBoolean(file.exists() && file.isFile());
     }
 
     @JRubyMethod(name = "grpowned?", required = 1, module = true)
     public static IRubyObject grpowned_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isGroupOwned());
     }
     
     @JRubyMethod(name = "identical?", required = 2, module = true)
     public static IRubyObject identical_p(IRubyObject recv, IRubyObject filename1, IRubyObject filename2) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file1 = file(filename1);
         JRubyFile file2 = file(filename2);
         
         return runtime.newBoolean(file1.exists() && file2.exists() &&
                 runtime.getPosix().stat(file1.getAbsolutePath()).isIdentical(runtime.getPosix().stat(file2.getAbsolutePath())));
     }
 
     @JRubyMethod(name = "owned?", required = 1, module = true)
     public static IRubyObject owned_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isOwned());
     }
     
     @JRubyMethod(name = "pipe?", required = 1, module = true)
     public static IRubyObject pipe_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isNamedPipe());
     }
 
     // We use file test since it is faster than a stat; also euid == uid in Java always
     @JRubyMethod(name = {"readable?", "readable_real?"}, required = 1, module = true)
     public static IRubyObject readable_p(IRubyObject recv, IRubyObject filename) {
         JRubyFile file = file(filename);
 
         return recv.getRuntime().newBoolean(file.exists() && file.canRead());
     }
 
     // Not exposed by filetest, but so similiar in nature that it is stored here
     public static IRubyObject rowned_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isROwned());
     }
     
     @JRubyMethod(name = "setgid?", required = 1, module = true)
     public static IRubyObject setgid_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isSetgid());
     }
     
     @JRubyMethod(name = "setuid?", required = 1, module = true)
     public static IRubyObject setuid_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isSetuid());
     }
     
     @JRubyMethod(name = "size", required = 1, module = true)
     public static IRubyObject size(IRubyObject recv, IRubyObject filename) {
         JRubyFile file = file(filename);
 
         if (!file.exists()) noFileError(filename);
         
         return recv.getRuntime().newFixnum(file.length());
     }
     
     @JRubyMethod(name = "size?", required = 1, module = true)
     public static IRubyObject size_p(IRubyObject recv, IRubyObject filename) {
         JRubyFile file = file(filename);
 
         if (!file.exists()) {
             return recv.getRuntime().getNil();
         }
 
         long length =  file.length();
         if (length > 0) {
             return recv.getRuntime().newFixnum(length);
         } else {
             return recv.getRuntime().getNil();
         }
     }
     
     @JRubyMethod(name = "socket?", required = 1, module = true)
     public static IRubyObject socket_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isSocket());
     }
     
     @JRubyMethod(name = "sticky?", required = 1, module = true)
     public static IRubyObject sticky_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isSticky());
     }
         
     @JRubyMethod(name = "symlink?", required = 1, module = true)
     public static RubyBoolean symlink_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
 
         try {
             // Note: We can't use file.exists() to check whether the symlink
             // exists or not, because that method returns false for existing
             // but broken symlink. So, we try without the existence check,
             // but in the try-catch block.
             // MRI behavior: symlink? on broken symlink should return true.
             return runtime.newBoolean(runtime.getPosix().lstat(file.getAbsolutePath()).isSymlink());
         } catch (RaiseException re) {
             return runtime.getFalse();
         }
     }
 
     // We do both writable and writable_real through the same method because
     // in our java process effective and real userid will always be the same.
     @JRubyMethod(name = {"writable?", "writable_real?"}, required = 1, module = true)
     public static RubyBoolean writable_p(IRubyObject recv, IRubyObject filename) {
         return filename.getRuntime().newBoolean(file(filename).canWrite());
     }
     
     @JRubyMethod(name = "zero?", required = 1, module = true)
     public static RubyBoolean zero_p(IRubyObject recv, IRubyObject filename) {
         JRubyFile file = file(filename);
         
         return filename.getRuntime().newBoolean(file.exists() && file.length() == 0L);
     }
 
-    private static JRubyFile file(IRubyObject path) {
-        //String filename = path.convertToString().toString();
-        String filename = path.convertToString().getUnicodeValue();
-        
-        
-        return JRubyFile.create(path.getRuntime().getCurrentDirectory(), filename);
+    private static JRubyFile file(IRubyObject pathOrFile) {
+        Ruby runtime = pathOrFile.getRuntime();
+
+        if (pathOrFile instanceof RubyFile) {
+            return JRubyFile.create(runtime.getCurrentDirectory(), ((RubyFile)pathOrFile).getPath());
+        } else {
+            return JRubyFile.create(runtime.getCurrentDirectory(), pathOrFile.convertToString().getUnicodeValue());
+        }
     }
     
     private static void noFileError(IRubyObject filename) {
         throw filename.getRuntime().newErrnoENOENTError("No such file or directory - " + 
                 filename.convertToString());
     }
 }
diff --git a/test/test_file.rb b/test/test_file.rb
index 064e837b8d..e99a5b99db 100644
--- a/test/test_file.rb
+++ b/test/test_file.rb
@@ -1,847 +1,853 @@
 # -*- coding: iso-8859-1 -*-
 require 'test/unit'
 require 'rbconfig'
 require 'fileutils'
 
 class TestFile < Test::Unit::TestCase
   WINDOWS = Config::CONFIG['host_os'] =~ /Windows|mswin/
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
   if Config::CONFIG['target_os'] =~ /Windows|mswin/
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
 
     arch = java.lang.System.getProperty('sun.arch.data.model')
     if (WINDOWS && arch == '64')
       # TODO: We have a bug on Windows with x64 JVM
       # See JRUBY-2819
       return
     end
 
     assert_equal(mask, File.umask)
     # Subsequent calls should still return the same umask, not zero
     assert_equal(mask, File.umask)
   ensure
     File.umask(orig_mask)
   end
 
   def test_allow_override_of_make_tmpname
     require 'tempfile'
     # mimics behavior of attachment_fu, which overrides private #make_tmpname
     Tempfile.class_eval do
       def make_tmpname(basename, n)
         ext = nil
         sprintf("%s%d-%d%s", basename.to_s.gsub(/\.\w+$/) { |s| ext = s; '' }, $$, n, ext)
       end
     end
 
     t = Tempfile.new "tcttac.jpg", File.dirname(__FILE__)
     assert t.path =~ /\.jpg$/
   end
 
   def test_mode_of_tempfile_is_600
     t = Tempfile.new "tcttac.jpg"
     assert_equal 0100600, File.stat(t.path).mode
   end
 
   # See JRUBY-2694; we don't have 1.8.7 support yet
 =begin
   def test_tempfile_with_suffix
     require 'tempfile'
     Tempfile.open(['prefix', 'suffix']) { |f|
       assert_match(/^prefix/, File.basename(f.path))
       assert_match(/suffix$/, f.path)
     }
   end
 =end
+
+  def test_file_size
+    size = File.size('build.xml')
+    assert(size > 0)
+    assert_equal(size, File.size(File.new('build.xml')))
+  end
 end
