diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index f2e5cdefc1..4aab8acb83 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1596 +1,1610 @@
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
 
 import com.kenai.constantine.platform.OpenFlags;
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
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.DirectoryAsFileException;
+import org.jruby.util.io.PermissionDeniedException;
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
             this.openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(in), getNewFileno(), new FileDescriptor())));
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
 
     public String getPath() {
         return path;
     }
 
     @JRubyModule(name="File::Constants")
     public static class Constants {}
     
     public static RubyClass createFileClass(Ruby runtime) {
         RubyClass fileClass = runtime.defineClass("File", runtime.getIO(), FILE_ALLOCATOR);
         // Create Constants class
         RubyModule constants = fileClass.defineModuleUnder("Constants");
         runtime.setFile(fileClass);
 
         fileClass.index = ClassIndex.FILE;
         fileClass.setReifiedClass(RubyFile.class);
 
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
         fileClass.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         fileClass.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         fileClass.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_SYSCASE));
         fileClass.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
         fileClass.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
         
         // Create constants for open flags
         for (OpenFlags f : OpenFlags.values()) {
             // Strip off the O_ prefix, so they become File::RDONLY, and so on
             final String name = f.name();
             if (name.startsWith("O_")) {
                 final String cname = name.substring(2);
                 // Special case for handling ACCMODE, since constantine will generate
                 // an invalid value if it is not defined by the platform.
                 final RubyFixnum cvalue = f == OpenFlags.O_ACCMODE
                         ? runtime.newFixnum(ModeFlags.ACCMODE)
                         : runtime.newFixnum(f.value());
                 fileClass.fastSetConstant(cname, cvalue);
                 constants.fastSetConstant(cname, cvalue);
             }
         }
         
         // Create constants for flock
         fileClass.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         fileClass.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         fileClass.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         fileClass.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         constants.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         constants.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         constants.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_SYSCASE));
         constants.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
         constants.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
         
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
                 // Return false here
             } catch (java.nio.channels.OverlappingFileLockException ioe) {
                 if (context.getRuntime().getDebug().isTrue()) {
                     ioe.printStackTrace(System.err);
                 }
                 // Return false here
             }
 
             return context.getRuntime().getFalse();
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
 
         String modeString;
         ModeFlags modes = new ModeFlags();
         int perm = 0;
 
         try {
             if (args.length > 1) {
                 modeString = args[1].convertToString().toString();
                 modes = parseModes19(context, args[1]);
             } else {
                 modeString = "r";
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
         } finally {}
 
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
         int umask = getRuntime().getPosix().umask(0);
         perm = perm - (perm & umask);
         
         ChannelDescriptor descriptor = sysopen(path, modes, perm);
         openFile.setMainStream(fdopen(descriptor, modes));
         
         registerDescriptor(descriptor);
     }
 
     protected void openInternal(String path, String modeString, ModeFlags modes) throws InvalidValueException {
         openFile = new OpenFile();
 
         openFile.setMode(modes.getOpenFileFlags());
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modeString));
 
         registerDescriptor(openFile.getMainStream().getDescriptor());
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
+        } catch (PermissionDeniedException pde) {
+            // PDException can be thrown only when creating the file and
+            // permission is denied.  See JavaDoc of PermissionDeniedException.
+            throw getRuntime().newErrnoEACCESError(path);
         } catch (FileNotFoundException fnfe) {
-            throw getRuntime().newErrnoENOENTError();
+            // FNFException can be thrown in both cases, when the file
+            // is not found, or when permission is denied.
+            if (Ruby.isSecurityRestricted() || new File(path).exists()) {
+                throw getRuntime().newErrnoEACCESError(path);
+            }
+            throw getRuntime().newErrnoENOENTError(path);
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
+        } catch (PermissionDeniedException pde) {
+            // PDException can be thrown only when creating the file and
+            // permission is denied.  See JavaDoc of PermissionDeniedException.
+            throw getRuntime().newErrnoEACCESError(path);
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
             IRubyObject str = obj.checkStringType();
             if (!str.isNil()) {
                 return (RubyString) str;
             }
             if (obj.respondsTo("to_path")) {
                 obj = obj.callMethod(context, "to_path");
             }
         }
 
         return obj.convertToString();
     }
 
     @JRubyMethod(name = {"path", "to_path"})
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
             RubyString filename = get_path(context, args[i]);
             
             if (!RubyFileTest.exist_p(filename, filename).isTrue()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
             
             boolean result = 0 == runtime.getPosix().chmod(filename.getUnicodeValue(), (int)mode.getLongValue());
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
             RubyString filename = get_path(context, args[i]);
 
             if (!RubyFileTest.exist_p(filename, filename).isTrue()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
             
             boolean result = 0 == runtime.getPosix().chown(filename.getUnicodeValue(), owner, group);
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
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject expand_path(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, true);
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
         if(f.startsWith("file:") && f.indexOf('!') != -1) {
             f = f.substring(5, f.indexOf("!"));
         }
         return context.getRuntime().newFileStat(f, true);
     }
 
     @JRubyMethod(name = "stat", required = 1, meta = true)
     public static IRubyObject stat(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         if(f.startsWith("file:") && f.indexOf('!') != -1) {
             f = f.substring(5, f.indexOf("!"));
         }
         return context.getRuntime().newFileStat(f, false);
     }
 
     @JRubyMethod(name = "atime", required = 1, meta = true)
     public static IRubyObject atime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         if(f.startsWith("file:") && f.indexOf('!') != -1) {
             f = f.substring(5, f.indexOf("!"));
         }
         return context.getRuntime().newFileStat(f, false).atime();
     }
 
     @JRubyMethod(name = "ctime", required = 1, meta = true)
     public static IRubyObject ctime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
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
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject truncate(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
         RubyString filename = arg1.convertToString(); // TODO: SafeStringValue here
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
diff --git a/src/org/jruby/util/io/ChannelDescriptor.java b/src/org/jruby/util/io/ChannelDescriptor.java
index 019db8cb83..321d649b11 100644
--- a/src/org/jruby/util/io/ChannelDescriptor.java
+++ b/src/org/jruby/util/io/ChannelDescriptor.java
@@ -1,760 +1,762 @@
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
 
 import static java.util.logging.Logger.getLogger;
 import static org.jruby.util.io.ModeFlags.RDONLY;
 import static org.jruby.util.io.ModeFlags.RDWR;
 import static org.jruby.util.io.ModeFlags.WRONLY;
 
 import java.io.File;
 import java.io.FileDescriptor;
 import java.io.FileNotFoundException;
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
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
 import org.jruby.RubyIO;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyFile;
 
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
     private int fileno;
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
      * See {@link #ChannelDescriptor(InputStream, int, ModeFlags, FileDescriptor)}
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
     private ChannelDescriptor(Channel channel, int fileno, ModeFlags originalModes, FileDescriptor fileDescriptor, AtomicInteger refCounter, boolean canBeSeekable) {
         this.refCounter = refCounter;
         this.channel = channel;
         this.fileno = fileno;
         this.originalModes = originalModes;
         this.fileDescriptor = fileDescriptor;
         this.canBeSeekable = canBeSeekable;
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
         this(channel, fileno, originalModes, fileDescriptor, new AtomicInteger(1), true);
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
         this(Channels.newChannel(baseInputStream), fileno, originalModes, fileDescriptor, new AtomicInteger(1), true);
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
         return canBeSeekable && channel instanceof FileChannel;
     }
     
     /**
      * Set the channel to be explicitly seekable or not, for streams that appear
      * to be seekable with the instanceof FileChannel check.
      * 
      * @param seekable Whether the channel is seekable or not.
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
 
             return new ChannelDescriptor(channel, newFileno, originalModes, fileDescriptor, refCounter, canBeSeekable);
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
 
             if (DEBUG) getLogger("ChannelDescriptor").info("Reopen fileno " + fileno + ", refs now: " + refCounter.get());
 
             return new ChannelDescriptor(channel, fileno, originalModes, fileDescriptor, refCounter, canBeSeekable);
         }
     }
     
     /**
      * Mimics the POSIX dup2(2) function, returning a new descriptor that references
      * the same open channel but with a specified fileno. This differs from the fileno
      * version by making the target descriptor into a new reference to the current
      * descriptor's channel, closing what it originally pointed to and preserving
      * its original fileno.
      * 
      * @param fileno The fileno to use for the new descriptor
      * @return A duplicate ChannelDescriptor based on this one
      */
     public void dup2Into(ChannelDescriptor other) throws BadDescriptorException, IOException {
         synchronized (refCounter) {
             refCounter.incrementAndGet();
 
             if (DEBUG) getLogger("ChannelDescriptor").info("Reopen fileno " + fileno + ", refs now: " + refCounter.get());
 
             other.close();
             
             other.channel = channel;
             other.originalModes = originalModes;
             other.fileDescriptor = fileDescriptor;
             other.refCounter = refCounter;
             other.canBeSeekable = canBeSeekable;
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
         if (!(channel instanceof ReadableByteChannel)) {
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
      * @param buf the byte list containing the bytes to be written
      * @return the number of bytes actually written
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      */
     public int internalWrite(ByteBuffer buffer) throws IOException, BadDescriptorException {
         checkOpen();
 
         // TODO: It would be nice to throw a better error for this
         if (!(channel instanceof WritableByteChannel)) {
             throw new BadDescriptorException();
         }
         
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
         
         return internalWrite(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin(), buf.length()));
     }
 
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
         } else if (path.startsWith("file:")) {
             int bangIndex = path.indexOf("!");
             if (bangIndex > 0) {
                 String filePath = path.substring(5, bangIndex);
                 String internalPath = path.substring(bangIndex + 2);
 
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
                 // raw file URL, just open directly
                 URL url = new URL(path);
                 InputStream is = url.openStream();
                 // FIXME: don't use RubyIO for this
                 return new ChannelDescriptor(Channels.newChannel(is), RubyIO.getNewFileno(), flags, new FileDescriptor());
             }
         } else if (path.startsWith("classpath:/")) {
             path = path.substring("classpath:/".length());
             InputStream is = ByteList.EMPTY_BYTELIST.getClass().getClassLoader().getResourceAsStream(path);
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
+                    } else if (!theFile.canWrite()) {
+                        throw new PermissionDeniedException(path);
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
 }
diff --git a/src/org/jruby/util/io/PermissionDeniedException.java b/src/org/jruby/util/io/PermissionDeniedException.java
new file mode 100644
index 0000000000..b8a63b6c87
--- /dev/null
+++ b/src/org/jruby/util/io/PermissionDeniedException.java
@@ -0,0 +1,65 @@
+/*
+ ***** BEGIN LICENSE BLOCK *****
+ * Version: CPL 1.0/GPL 2.0/LGPL 2.1
+ *
+ * The contents of this file are subject to the Common Public
+ * License Version 1.0 (the "License"); you may not use this file
+ * except in compliance with the License. You may obtain a copy of
+ * the License at http://www.eclipse.org/legal/cpl-v10.html
+ *
+ * Software distributed under the License is distributed on an "AS
+ * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
+ * implied. See the License for the specific language governing
+ * rights and limitations under the License.
+ *
+ * Copyright (C) 2010 Hiroshi Nakamura <nahi@ruby-lang.org>
+ * 
+ * Alternatively, the contents of this file may be used under the terms of
+ * either of the GNU General Public License Version 2 or later (the "GPL"),
+ * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
+ * in which case the provisions of the GPL or the LGPL are applicable instead
+ * of those above. If you wish to allow use of your version of this file only
+ * under the terms of either the GPL or the LGPL, and not to allow others to
+ * use your version of this file under the terms of the CPL, indicate your
+ * decision by deleting the provisions above and replace them with the notice
+ * and other provisions required by the GPL or the LGPL. If you do not delete
+ * the provisions above, a recipient may use your version of this file under
+ * the terms of any one of the CPL, the GPL or the LGPL.
+ ***** END LICENSE BLOCK *****/
+package org.jruby.util.io;
+
+import java.io.FileNotFoundException;
+
+/**
+ * Signals that an attempt to open the file denoted by a specified pathname
+ * has failed by 'Permission Denied'.
+ *
+ * <p>This exception might be thrown by the {@link ChannelDescriptor#open}
+ * when trying to create new file and the specified pathname cannot be written.
+ * Bear in mind that {@link ChannelDescriptor#open} throws
+ * not PermissionDeniedException but FileNotFindException as same as Java
+ * manner when trying to read existing but unreadable file.
+ * See org.jruby.RubyFile#fopen and sysopen how we handle that situation.</p>
+ */
+public class PermissionDeniedException extends FileNotFoundException {
+
+    /**
+     * Constructs a PermissionDeniedException with null as its error detail
+     * message.
+     */
+    public PermissionDeniedException() {
+        super();
+    }
+
+    /**
+     * Constructs a PermissionDeniedException with the specified detail
+     * message.  The string msg can be retrieved later by the
+     * {@link java.lang.Throwable#getMessage} method of class
+     * java.lang.Throwable.
+     *
+     * @param msg the detail message.
+     */
+    public PermissionDeniedException(String msg) {
+        super(msg);
+    }
+}
diff --git a/test/test_file.rb b/test/test_file.rb
index beee13b636..67c026259d 100644
--- a/test/test_file.rb
+++ b/test/test_file.rb
@@ -1,959 +1,975 @@
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
 
+  # JRUBY-4537: File.open raises Errno::ENOENT instead of Errno::EACCES
+  def test_write_open_permission_denied
+    t = Tempfile.new('tmp' + File.basename(__FILE__))
+    t.close
+    File.open(t.path, 'w') {}
+    File.chmod(0555, t.path)
+    # jruby 1.4 raises ENOENT here
+    assert_raises(Errno::EACCES) do
+      File.open(t.path, 'w') {}
+    end
+    # jruby 1.4 raises ENOENT here
+    assert_raises(Errno::EACCES) do
+      File.open(t.path, File::WRONLY) {}
+    end
+  end
+
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
 end
diff --git a/test/test_system_error.rb b/test/test_system_error.rb
index f771443b07..1fb81c9a07 100644
--- a/test/test_system_error.rb
+++ b/test/test_system_error.rb
@@ -1,128 +1,135 @@
 require 'test/unit'
 require 'rbconfig'
 
 class TestSystemError < Test::Unit::TestCase
   def setup
     # FIXME: Might be fragile, depends on platform data.
     # sucked these right from ruby 1.8.6 on osx, adjusted on Linux.
     @r186_errcodes=[
       ["ENOTCONN", 57, "is not connected"], ["EXDEV", 18, /cross-device link/i],
       ["ENOLCK", 77, "No locks available"], ["ENOTSOCK", 38, "Socket operation on non-socket"],
       ["ENOLINK", 97, /link/i], ["ENETDOWN", 50, "Network is down"],
       ["EAGAIN", 35, "Resource temporarily unavailable"], ["EROFS", 30, "Read-only file system"],
       ["ENOMSG", 91, "No message of desired type"], ["EPROTONOSUPPORT", 43, "Protocol not supported"],
       ["EHOSTDOWN", 64, "Host is down"], ["EINTR", 4, "Interrupted system call"],
       ["ENFILE", 23, "Too many open files in system"], ["EBUSY", 16, /resource busy/i],
       ["EDEADLK", 11, "Resource deadlock avoided"], ["EILSEQ", 92, /Illegal|Invalid .*/],
       ["ENOBUFS", 55, "No buffer space available"], ["EBADF", 9, "Bad file descriptor"],
       ["ENOSPC", 28, "No space left on device"], ["ENOSR", 98, /STREAM.* resources/i],
       ["EADDRINUSE", 48, "Address already in use"],
       ["EDQUOT", 69, "quota exceeded"], ["ENOENT", 2, "No such file or directory"],
       ["EISDIR", 21, "Is a directory"], ["ELOOP", 62, "Too many levels of symbolic links"],
       ["EPROTOTYPE", 41, "Protocol wrong type for socket"], ["ETIMEDOUT", 60, "timed out"],
       ["ECONNABORTED", 53, "Software caused connection abort"], ["EFAULT", 14, "Bad address"],
       ["EDOM", 33, "Numerical argument out of domain"], ["EBADMSG", 94, "Bad message"],
       ["EPFNOSUPPORT", 46, "Protocol family not supported"], ["EINPROGRESS", 36, "Operation now in progress"],
       ["E2BIG", 7, "Argument list too long"], ["ETXTBSY", 26, "Text file busy"],
       ["ENODATA", 96, /No (data|message) available/], ["ENOSYS", 78, "Function not implemented"],
       ["EDESTADDRREQ", 39, "Destination address required"], ["ESHUTDOWN", 58, /Can.*t send after .* shutdown/],
       ["ENODEV", 19, /device/], ["EMLINK", 31, "Too many links"],
       ["EPROTO", 100, "Protocol error"], ["ENETUNREACH", 51, "Network is unreachable"],
       ["ENOMEM", 12, "Cannot allocate memory"], ["EIO", 5, "Input/output error"],
       ["EMFILE", 24, "Too many open files"], ["EIDRM", 90, "Identifier removed"],
       ["ESOCKTNOSUPPORT", 44, "Socket type not supported"], ["EHOSTUNREACH", 65, "No route to host"],
       ["EEXIST", 17, "File exists"], ["ENAMETOOLONG", 63, "File name too long"],
       ["EUSERS", 68, "Too many users"], ["EISCONN", 56, "is already connected"],
       ["ECHILD", 10, "No child processes"], ["ESPIPE", 29, "Illegal seek"],
       ["EREMOTE", 71, "remote"], ["EADDRNOTAVAIL", 49, "assign requested address"],
       ["ENOPROTOOPT", 42, "Protocol not available"],
       ["ECONNREFUSED", 61, "Connection refused"], ["ESRCH", 3, "No such process"],
       ["EINVAL", 22, "Invalid argument"], ["EOVERFLOW", 84, "Value too large"],
       ["ECONNRESET", 54, "Connection reset by peer"], ["ENOTBLK", 15, "Block device required"],
       ["ERANGE", 34, /result/i], ["ENOEXEC", 8, "Exec format error"],
       ["EAFNOSUPPORT", 47, "Address family not supported by protocol"],
       ["ETIME", 101, /time/i], ["EFBIG", 27, "File too large"],
       ["ESTALE", 70, "Stale NFS file handle"], ["EPERM", 1, "Operation not permitted"],
       ["EMSGSIZE", 40, "Message too long"], ["ENOTEMPTY", 66, "Directory not empty"],
       ["ENOTDIR", 20, "Not a directory"],
       ["ETOOMANYREFS", 59, /Too many references: can.*t splice/],
       ["EMULTIHOP", 95, /MULTIHOP/i], ["EPIPE", 32, "Broken pipe"],
       ["EACCES", 13, "Permission denied"], ["ENETRESET", 52, "Network dropped connection on reset"],
       ["EOPNOTSUPP", 102, "Operation not supported"], ["ENOSTR", 99, /Not a STREAM/i],
       ["ENOTTY", 25, "Inappropriate ioctl for device"],
       ["EALREADY", 37, "Operation already in progress"]].sort_by{|v| v[1] }
 
       if ($VERBOSE)
         print_report
       end
   end
 
 #  def test_has_186_ERRNO_constants
 #    @r186_errcodes.each do |e,c,m|
 #      assert Errno.constants.include?(e), "missing constant #{e}"
 #    end
 #    allcodes = Errno.constants.map{|x| eval "Errno::#{x}::Errno"}
 #    dupes = allcodes.delete_if{|k| allcodes.rindex(k) == allcodes.index(k)}.uniq
 #    #puts "dupes: #{dupes.join(',')}"
 #    assert_equal [35, 35], allcodes #EAGAIN and EWOULDBLOCK
 #    assert_equal [35], dupes
 #  end
 
   def test_can_raise_errno_without_message
     @r186_errcodes.each do |e,c,m|
       err = Errno.const_get(e)
       assert_raise_msg(err,m) do 
         raise err
       end
     end if Config::CONFIG['host_os'].downcase =~ /windows|mswin|darwin|linux/ 
   end
 
   def assert_raise_msg(error, message)
     begin 
       yield
     rescue Exception => e
       assert_kind_of error, e
       assert_match message, e.message
     end
   end
   
   def print_report
     @r186_errcodes.each do |e,c|
       if Errno.constants.include?(e)
         a = (eval "Errno::#{e}::Errno")
         if a != c
           puts "mismatch code val #{e} should be #{c}, was #{a} "
         end
       else
         puts "     int    #{e} = #{c};"
       end
     end if Config::CONFIG['host_os'].downcase =~ /windows|mswin|darwin|linux/ 
   end
 
   def test_no_duplicated_msg_enoent
     no_such_file = __FILE__ + '.nosuchfile'
     begin
       File.open(no_such_file, 'r') {}
       fail
     rescue Errno::ENOENT => e
       # jruby 1.4 has duplicated error msg such as
       # 'No such file or directory - File not found - path'
       assert_equal 'No such file or directory - ' + no_such_file, e.message
     end
   end
 
   def test_no_duplicated_msg_eacces
     require 'tempfile'
     t = Tempfile.new('tmp' + File.basename(__FILE__))
     t.close
     File.open(t.path, 'w') {}
     File.chmod(0555, t.path)
     begin
       File.open(t.path, 'w') {}
       fail
     rescue Errno::EACCES => e
       # jruby 1.4 has duplicated error msg as well
       assert_equal 'Permission denied - ' + t.path, e.message
     end
+    #
+    begin
+      File.open(t.path, File::WRONLY) {}
+      fail
+    rescue Errno::EACCES => e
+      assert_equal 'Permission denied - ' + t.path, e.message
+    end
   end
 end
