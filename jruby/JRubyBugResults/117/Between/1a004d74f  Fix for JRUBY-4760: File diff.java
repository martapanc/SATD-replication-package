diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 4825367de8..b278383375 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1703 +1,1727 @@
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
+import java.util.zip.ZipEntry;
+import java.util.zip.ZipFile;
 
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
 import org.jruby.util.io.PermissionDeniedException;
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
     public static IRubyObject umask(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int oldMask = 0;
         if (args.length == 0) {
             oldMask = runtime.getPosix().umask(0);
             runtime.getPosix().umask(oldMask);
         } else if (args.length == 1) {
             oldMask = runtime.getPosix().umask((int) args[0].convertToInteger().getLongValue()); 
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
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
 
             if (!lToDelete.delete()) {
                 throw runtime.newErrnoEACCESError(filename.toString());
             }
         }
         
         return runtime.newFixnum(args.length);
     }
 
+    public static ZipEntry getFileEntry(ZipFile zf, String path) throws IOException {
+        ZipEntry entry = zf.getEntry(path);
+        if (entry == null) {
+            // try canonicalizing the path to eliminate . and .. (JRUBY-4760)
+            entry = zf.getEntry(new File("/" + path).getCanonicalPath().substring(1));
+        }
+        return entry;
+    }
+
+    public static ZipEntry getDirOrFileEntry(ZipFile zf, String path) throws IOException {
+        ZipEntry entry = zf.getEntry(path + "/"); // first try as directory
+        if (entry == null) {
+            // try canonicalizing the path to eliminate . and .. (JRUBY-4760)
+            entry = zf.getEntry(new File("/" + path + "/").getCanonicalPath().substring(1));
+            if (entry == null) {
+                // try as file
+                entry = getFileEntry(zf, path);
+            }
+        }
+        return entry;
+    }
+
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
diff --git a/src/org/jruby/RubyFileStat.java b/src/org/jruby/RubyFileStat.java
index 6059bdc98c..581f94e59d 100644
--- a/src/org/jruby/RubyFileStat.java
+++ b/src/org/jruby/RubyFileStat.java
@@ -1,597 +1,597 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
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
 
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipFile;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.ext.posix.FileStat;
 import org.jruby.ext.posix.util.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.JRubyFile;
 
 /**
  * Implements File::Stat
  */
 @JRubyClass(name="File::Stat", include="Comparable")
 public class RubyFileStat extends RubyObject {
     private static final long serialVersionUID = 1L;
     
     private JRubyFile file;
     private FileStat stat;
 
     private static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyFileStat(runtime, klass);
         }
     };
 
     public static RubyClass createFileStatClass(Ruby runtime) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
         final RubyClass fileStatClass = runtime.getFile().defineClassUnder("Stat",runtime.getObject(), ALLOCATOR);
         runtime.setFileStat(fileStatClass);
 
         fileStatClass.includeModule(runtime.fastGetModule("Comparable"));
         fileStatClass.defineAnnotatedMethods(RubyFileStat.class);
 
         return fileStatClass;
     }
 
     protected RubyFileStat(Ruby runtime, RubyClass clazz) {
         super(runtime, clazz);
 
     }
     
     public static RubyFileStat newFileStat(Ruby runtime, String filename, boolean lstat) {
         RubyFileStat stat = new RubyFileStat(runtime, runtime.getFileStat());
 
         stat.setup(filename, lstat);
         
         return stat;
     }
 
     public static RubyFileStat newFileStat(Ruby runtime, FileDescriptor descriptor) {
         RubyFileStat stat = new RubyFileStat(runtime, runtime.getFileStat());
         
         stat.setup(descriptor);
         
         return stat;
     }
 
     private void setup(FileDescriptor descriptor) {
         stat = getRuntime().getPosix().fstat(descriptor);
     }
     
     private void setup(String filename, boolean lstat) {
         if (Platform.IS_WINDOWS && filename.length() == 2
                 && filename.charAt(1) == ':' && Character.isLetter(filename.charAt(0))) {
             filename += "/";
         }
 
         if (filename.startsWith("file:") && filename.indexOf('!') != -1) {
             // file: URL handling
             String zipFileEntry = filename.substring(filename.indexOf("!") + 1);
             if (zipFileEntry.length() > 0) {
                 if (zipFileEntry.charAt(0) == '/') {
                     if (zipFileEntry.length() > 1) {
                         zipFileEntry = zipFileEntry.substring(1);
                     } else {
                         throw getRuntime().newErrnoENOENTError("invalid jar/file URL: " + filename);
                     }
                 }
             } else {
                 throw getRuntime().newErrnoENOENTError("invalid jar/file URL: " + filename);
             }
             String zipfilename = filename.substring(5, filename.indexOf("!"));
             
             try {
                 ZipFile zipFile = new ZipFile(zipfilename);
-                ZipEntry zipEntry = zipFile.getEntry(zipFileEntry);
+                ZipEntry zipEntry = RubyFile.getFileEntry(zipFile, zipFileEntry);
 
                 if (zipEntry == null) {
                     throw getRuntime().newErrnoENOENTError("invalid jar/file URL: " + filename);
                 }
                 stat = new ZipFileStat(zipEntry);
                 return;
             } catch (IOException ioe) {
                 // fall through and use the zip file as the file to stat
             }
 
             filename = zipfilename;
         }
             
         file = JRubyFile.create(getRuntime().getCurrentDirectory(), filename);
 
         if (lstat) {
             stat = getRuntime().getPosix().lstat(file.getAbsolutePath());
         } else {
             stat = getRuntime().getPosix().stat(file.getAbsolutePath());
         }
     }
 
     public static class ZipFileStat implements FileStat {
         private final ZipEntry zipEntry;
 
         public ZipFileStat(ZipEntry zipEntry) {
             this.zipEntry = zipEntry;
         }
         
         public long atime() {
             return zipEntry.getTime();
         }
 
         public long blocks() {
             return zipEntry.getSize();
         }
 
         public long blockSize() {
             return 1L;
         }
 
         public long ctime() {
             return zipEntry.getTime();
         }
 
         public long dev() {
             return -1;
         }
 
         public String ftype() {
             return "zip file entry";
         }
 
         public int gid() {
             return -1;
         }
 
         public boolean groupMember(int i) {
             return false;
         }
 
         public long ino() {
             return -1;
         }
 
         public boolean isBlockDev() {
             return false;
         }
 
         public boolean isCharDev() {
             return false;
         }
 
         public boolean isDirectory() {
             return zipEntry.isDirectory();
         }
 
         public boolean isEmpty() {
             return zipEntry.getSize() == 0;
         }
 
         public boolean isExecutable() {
             return false;
         }
 
         public boolean isExecutableReal() {
             return false;
         }
 
         public boolean isFifo() {
             return false;
         }
 
         public boolean isFile() {
             return !zipEntry.isDirectory();
         }
 
         public boolean isGroupOwned() {
             return false;
         }
 
         public boolean isIdentical(FileStat fs) {
             return fs instanceof ZipFileStat && ((ZipFileStat)fs).zipEntry.equals(zipEntry);
         }
 
         public boolean isNamedPipe() {
             return false;
         }
 
         public boolean isOwned() {
             return false;
         }
 
         public boolean isROwned() {
             return false;
         }
 
         public boolean isReadable() {
             return true;
         }
 
         public boolean isReadableReal() {
             return true;
         }
 
         public boolean isWritable() {
             return false;
         }
 
         public boolean isWritableReal() {
             return false;
         }
 
         public boolean isSetgid() {
             return false;
         }
 
         public boolean isSetuid() {
             return false;
         }
 
         public boolean isSocket() {
             return false;
         }
 
         public boolean isSticky() {
             return false;
         }
 
         public boolean isSymlink() {
             return false;
         }
 
         public int major(long l) {
             return -1;
         }
 
         public int minor(long l) {
             return -1;
         }
 
         public int mode() {
             return -1;
         }
 
         public long mtime() {
             return zipEntry.getTime();
         }
 
         public int nlink() {
             return -1;
         }
 
         public long rdev() {
             return -1;
         }
 
         public long st_size() {
             return zipEntry.getSize();
         }
 
         public int uid() {
             return 0;
         }
 
     }
 
     @JRubyMethod(name = "initialize", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject fname, Block unusedBlock) {
         setup(fname.convertToString().toString(), false);
 
         return this;
     }
     
     @JRubyMethod(name = "atime")
     public IRubyObject atime() {
         return getRuntime().newTime(stat.atime() * 1000);
     }
     
     @JRubyMethod(name = "blksize")
     public RubyFixnum blksize() {
         return getRuntime().newFixnum(stat.blockSize());
     }
 
     @JRubyMethod(name = "blockdev?")
     public IRubyObject blockdev_p() {
         return getRuntime().newBoolean(stat.isBlockDev());
     }
 
     @JRubyMethod(name = "blocks")
     public IRubyObject blocks() {
         return getRuntime().newFixnum(stat.blocks());
     }
 
     @JRubyMethod(name = "chardev?")
     public IRubyObject chardev_p() {
         return getRuntime().newBoolean(stat.isCharDev());
     }
 
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject cmp(IRubyObject other) {
         if (!(other instanceof RubyFileStat)) getRuntime().getNil();
         
         long time1 = stat.mtime();
         long time2 = ((RubyFileStat) other).stat.mtime();
         
         if (time1 == time2) {
             return getRuntime().newFixnum(0);
         } else if (time1 < time2) {
             return getRuntime().newFixnum(-1);
         } 
 
         return getRuntime().newFixnum(1);
     }
 
     @JRubyMethod(name = "ctime")
     public IRubyObject ctime() {
         return getRuntime().newTime(stat.ctime() * 1000);
     }
 
     @JRubyMethod(name = "dev")
     public IRubyObject dev() {
         return getRuntime().newFixnum(stat.dev());
     }
     
     @JRubyMethod(name = "dev_major")
     public IRubyObject devMajor() {
         return getRuntime().newFixnum(stat.major(stat.dev()));
     }
 
     @JRubyMethod(name = "dev_minor")
     public IRubyObject devMinor() {
         return getRuntime().newFixnum(stat.minor(stat.dev()));
     }
 
     @JRubyMethod(name = "directory?")
     public RubyBoolean directory_p() {
         return getRuntime().newBoolean(stat.isDirectory());
     }
 
     @JRubyMethod(name = "executable?")
     public IRubyObject executable_p() {
         return getRuntime().newBoolean(stat.isExecutable());
     }
 
     @JRubyMethod(name = "executable_real?")
     public IRubyObject executableReal_p() {
         return getRuntime().newBoolean(stat.isExecutableReal());
     }
 
     @JRubyMethod(name = "file?")
     public RubyBoolean file_p() {
         return getRuntime().newBoolean(stat.isFile());
     }
 
     @JRubyMethod(name = "ftype")
     public RubyString ftype() {
         return getRuntime().newString(stat.ftype());
     }
 
     @JRubyMethod(name = "gid")
     public IRubyObject gid() {
         return getRuntime().newFixnum(stat.gid());
     }
     
     @JRubyMethod(name = "grpowned?")
     public IRubyObject group_owned_p() {
         return getRuntime().newBoolean(stat.isGroupOwned());
     }
     
     @JRubyMethod(name = "initialize_copy", required = 1)
     public IRubyObject initialize_copy(IRubyObject original) {
         if (!(original instanceof RubyFileStat)) {
             throw getRuntime().newTypeError("wrong argument class");
         }
         
         RubyFileStat originalFileStat = (RubyFileStat) original;
         
         file = originalFileStat.file;
         stat = originalFileStat.stat;
         
         return this;
     }
     
     @JRubyMethod(name = "ino")
     public IRubyObject ino() {
         return getRuntime().newFixnum(stat.ino());
     }
 
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect() {
         StringBuilder buf = new StringBuilder("#<");
         buf.append(getMetaClass().getRealClass().getName());
         buf.append(" ");
         // FIXME: Obvious issue that not all platforms can display all attributes.  Ugly hacks.
         // Using generic posix library makes pushing inspect behavior into specific system impls
         // rather painful.
         try { buf.append("dev=0x").append(Long.toHexString(stat.dev())).append(", "); } catch (Exception e) {}
         try { buf.append("ino=").append(stat.ino()).append(", "); } catch (Exception e) {}
         buf.append("mode=0").append(Integer.toOctalString(stat.mode())).append(", "); 
         try { buf.append("nlink=").append(stat.nlink()).append(", "); } catch (Exception e) {}
         try { buf.append("uid=").append(stat.uid()).append(", "); } catch (Exception e) {}
         try { buf.append("gid=").append(stat.gid()).append(", "); } catch (Exception e) {}
         try { buf.append("rdev=0x").append(Long.toHexString(stat.rdev())).append(", "); } catch (Exception e) {}
         buf.append("size=").append(sizeInternal()).append(", ");
         try { buf.append("blksize=").append(stat.blockSize()).append(", "); } catch (Exception e) {}
         try { buf.append("blocks=").append(stat.blocks()).append(", "); } catch (Exception e) {}
         
         buf.append("atime=").append(atime()).append(", ");
         buf.append("mtime=").append(mtime()).append(", ");
         buf.append("ctime=").append(ctime());
         buf.append(">");
         
         return getRuntime().newString(buf.toString());
     }
 
     @JRubyMethod(name = "uid")
     public IRubyObject uid() {
         return getRuntime().newFixnum(stat.uid());
     }
     
     @JRubyMethod(name = "mode")
     public IRubyObject mode() {
         return getRuntime().newFixnum(stat.mode());
     }
 
     @JRubyMethod(name = "mtime")
     public IRubyObject mtime() {
         return getRuntime().newTime(stat.mtime() * 1000);
     }
     
     public IRubyObject mtimeEquals(IRubyObject other) {
         return getRuntime().newBoolean(stat.mtime() == newFileStat(getRuntime(), other.convertToString().toString(), false).stat.mtime()); 
     }
 
     public IRubyObject mtimeGreaterThan(IRubyObject other) {
         return getRuntime().newBoolean(stat.mtime() > newFileStat(getRuntime(), other.convertToString().toString(), false).stat.mtime()); 
     }
 
     public IRubyObject mtimeLessThan(IRubyObject other) {
         return getRuntime().newBoolean(stat.mtime() < newFileStat(getRuntime(), other.convertToString().toString(), false).stat.mtime()); 
     }
 
     @JRubyMethod(name = "nlink")
     public IRubyObject nlink() {
         return getRuntime().newFixnum(stat.nlink());
     }
 
     @JRubyMethod(name = "owned?")
     public IRubyObject owned_p() {
         return getRuntime().newBoolean(stat.isOwned());
     }
 
     @JRubyMethod(name = "pipe?")
     public IRubyObject pipe_p() {
         return getRuntime().newBoolean(stat.isNamedPipe());
     }
 
     @JRubyMethod(name = "rdev")
     public IRubyObject rdev() {
         return getRuntime().newFixnum(stat.rdev());
     }
     
     @JRubyMethod(name = "rdev_major")
     public IRubyObject rdevMajor() {
         return getRuntime().newFixnum(stat.major(stat.rdev()));
     }
 
     @JRubyMethod(name = "rdev_minor")
     public IRubyObject rdevMinor() {
         return getRuntime().newFixnum(stat.minor(stat.rdev()));
     }
 
     @JRubyMethod(name = "readable?")
     public IRubyObject readable_p() {
         return getRuntime().newBoolean(stat.isReadable());
     }
 
     @JRubyMethod(name = "readable_real?")
     public IRubyObject readableReal_p() {
         return getRuntime().newBoolean(stat.isReadableReal());
     }
 
     @JRubyMethod(name = "setgid?")
     public IRubyObject setgid_p() {
         return getRuntime().newBoolean(stat.isSetgid());
     }
 
     @JRubyMethod(name = "setuid?")
     public IRubyObject setuid_p() {
         return getRuntime().newBoolean(stat.isSetuid());
     }
 
     private long sizeInternal() {
         // Workaround for JRUBY-4149
         if (Platform.IS_WINDOWS && file != null) {
             try {
                 return file.length();
             } catch (SecurityException ex) {
                 return 0L;
             }
         } else {
             return stat.st_size();
         }
     }
 
     @JRubyMethod(name = "size")
     public IRubyObject size() {
         return getRuntime().newFixnum(sizeInternal());
     }
     
     @JRubyMethod(name = "size?")
     public IRubyObject size_p() {
         long size = sizeInternal();
         
         if (size == 0) return getRuntime().getNil();
         
         return getRuntime().newFixnum(size);
     }
 
     @JRubyMethod(name = "socket?")
     public IRubyObject socket_p() {
         return getRuntime().newBoolean(stat.isSocket());
     }
     
     @JRubyMethod(name = "sticky?")
     public IRubyObject sticky_p() {
         return getRuntime().newBoolean(stat.isSticky());
     }
 
     @JRubyMethod(name = "symlink?")
     public IRubyObject symlink_p() {
         return getRuntime().newBoolean(stat.isSymlink());
     }
 
     @JRubyMethod(name = "writable?")
     public IRubyObject writable_p() {
     	return getRuntime().newBoolean(stat.isWritable());
     }
     
     @JRubyMethod(name = "writable_real?")
     public IRubyObject writableReal_p() {
         return getRuntime().newBoolean(stat.isWritableReal());
     }
     
     @JRubyMethod(name = "zero?")
     public IRubyObject zero_p() {
         return getRuntime().newBoolean(stat.isEmpty());
     }
 }
diff --git a/src/org/jruby/RubyFileTest.java b/src/org/jruby/RubyFileTest.java
index 2ea53dea2f..b437276c6c 100644
--- a/src/org/jruby/RubyFileTest.java
+++ b/src/org/jruby/RubyFileTest.java
@@ -1,389 +1,387 @@
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
  * Copyright (C) 2004-2005, 2009 Charles O Nutter <headius@headius.com>
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
 
+import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.exceptions.RaiseException;
 
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.JRubyFile;
 
 import static org.jruby.RubyFile.get_path;
 
 @JRubyModule(name = "FileTest")
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
         return directory_p(recv.getRuntime(), filename);
     }
 
     public static IRubyObject directory_p(Ruby runtime, IRubyObject filename) {
         ZipEntry entry = file_in_archive(filename);
         if (entry != null) {
             return entry.isDirectory() ? runtime.getTrue() : runtime.getFalse();
         }
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
         Ruby runtime = recv.getRuntime();
         if (existsOnClasspath(filename)) {
             return runtime.getTrue();
         }
 
         if (Ruby.isSecurityRestricted()) {
             return runtime.getFalse();
         }
 
 
         if (file_in_archive(filename) != null) {
             return runtime.getTrue();
         }
 
         return runtime.newBoolean(file(filename).exists());
     }
 
     @JRubyMethod(name = "file?", required = 1, module = true)
     public static RubyBoolean file_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         ZipEntry entry = file_in_archive(filename);
         if (entry != null) {
             return entry.isDirectory() ?
                 recv.getRuntime().getFalse() :
                 recv.getRuntime().getTrue();
         }
 
         JRubyFile file = file(filename);
 
         return runtime.newBoolean(file.exists() && file.isFile());
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
         Ruby runtime = recv.getRuntime();
         ZipEntry entry = file_in_archive(filename);
         if (entry != null) {
             return entry.isDirectory() ?
                 recv.getRuntime().getFalse() :
                 recv.getRuntime().getTrue();
         }
 
         JRubyFile file = file(filename);
 
         return runtime.newBoolean(file.exists() && file.canRead());
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
         Ruby runtime = recv.getRuntime();
         ZipEntry entry = file_in_archive(filename);
         if (entry != null) {
             return runtime.newFixnum(entry.getSize());
         }
 
         JRubyFile file = file(filename);
 
         if (!file.exists()) {
             noFileError(filename);
         }
 
         return runtime.newFixnum(file.length());
     }
 
     @JRubyMethod(name = "size?", required = 1, module = true)
     public static IRubyObject size_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         ZipEntry entry = file_in_archive(filename);
         if (entry != null) {
             long size = entry.getSize();
             if (size > 0) {
                 return runtime.newFixnum(size);
             } else {
                 return runtime.getNil();
             }
         }
 
         JRubyFile file = file(filename);
 
         if (!file.exists()) {
             return runtime.getNil();
         }
 
         long length = file.length();
         if (length > 0) {
             return runtime.newFixnum(length);
         } else {
             return runtime.getNil();
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
         } catch (SecurityException re) {
             return runtime.getFalse();
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
         Ruby runtime = recv.getRuntime();
         ZipEntry entry = file_in_archive(filename);
         if (entry != null) {
             return runtime.newBoolean(entry.getSize() == 0L);
         }
 
         JRubyFile file = file(filename);
 
         return runtime.newBoolean(file.exists() && file.length() == 0L);
     }
 
     private static JRubyFile file(IRubyObject pathOrFile) {
         Ruby runtime = pathOrFile.getRuntime();
 
         if (pathOrFile instanceof RubyFile) {
             return JRubyFile.create(runtime.getCurrentDirectory(), ((RubyFile) pathOrFile).getPath());
         } else {
             RubyString path = get_path(runtime.getCurrentContext(), pathOrFile);
             return JRubyFile.create(runtime.getCurrentDirectory(), path.getUnicodeValue());
         }
     }
 
     private static ZipEntry file_in_archive(IRubyObject path) {
         Ruby runtime = path.getRuntime();
 
         if (path instanceof RubyFile) {
             return null;
         }
 
         RubyString pathStr = get_path(runtime.getCurrentContext(), path);
         String pathJStr = pathStr.getUnicodeValue();
         if (pathJStr.startsWith("file:")) {
             String file = pathJStr.substring(5);
             int bang = file.indexOf('!');
             if (bang == -1 || bang == file.length() - 1) {
                 return null;
             }
             String jar = file.substring(0, bang);
             String after = file.substring(bang + 2);
             try {
                 JarFile jf = new JarFile(jar);
-                ZipEntry entry = jf.getEntry(after + "/"); // first try as directory
-                if (entry == null) {
-                    entry = jf.getEntry(after); // next as regular file
-                }
+                ZipEntry entry = RubyFile.getDirOrFileEntry(jf, after);
                 return entry;
             } catch (Exception e) {
             }
         }
 
         return null;
     }
 
     private static boolean existsOnClasspath(IRubyObject path) {
         if (path instanceof RubyFile) {
             return false;
         }
 
         Ruby runtime = path.getRuntime();
         RubyString pathStr = get_path(runtime.getCurrentContext(), path);
         String pathJStr = pathStr.getUnicodeValue();
         if (pathJStr.startsWith("classpath:/")) {
             pathJStr = pathJStr.substring("classpath:/".length());
 
             ClassLoader classLoader = runtime.getJRubyClassLoader();
             // handle security-sensitive case
             if (Ruby.isSecurityRestricted() && classLoader == null) {
                 classLoader = runtime.getInstanceConfig().getLoader();
             }
 
             InputStream is = classLoader.getResourceAsStream(pathJStr);
             if (is != null) {
                 try {
                     is.close();
                 } catch (IOException ignore) {
                 } catch (NullPointerException wtf) {
                     // that's what sometimes happens, weird
                 }
                 return true;
             }
         }
         return false;
     }
 
     private static void noFileError(IRubyObject filename) {
         throw filename.getRuntime().newErrnoENOENTError("No such file or directory - " +
                 filename.convertToString());
     }
 }
diff --git a/src/org/jruby/util/Dir.java b/src/org/jruby/util/Dir.java
index f1cfedde4a..630b7ff785 100644
--- a/src/org/jruby/util/Dir.java
+++ b/src/org/jruby/util/Dir.java
@@ -1,803 +1,805 @@
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
  * Copyright (C) 2007, 2008 Ola Bini <ola@ologix.com>
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
 package org.jruby.util;
 
 import java.io.File;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Enumeration;
 import java.util.List;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
+import java.util.zip.ZipEntry;
 import org.jruby.RubyEncoding;
+import org.jruby.RubyFile;
 
 import org.jruby.ext.posix.JavaSecuredFile;
 import org.jruby.platform.Platform;
 
 /**
  * This class exists as a counterpart to the dir.c file in 
  * MRI source. It contains many methods useful for 
  * File matching and Globbing.
  *
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class Dir {
     public final static boolean DOSISH = Platform.IS_WINDOWS;
     public final static boolean CASEFOLD_FILESYSTEM = DOSISH;
 
     public final static int FNM_NOESCAPE = 0x01;
     public final static int FNM_PATHNAME = 0x02;
     public final static int FNM_DOTMATCH = 0x04;
     public final static int FNM_CASEFOLD = 0x08;
 
     public final static int FNM_SYSCASE = CASEFOLD_FILESYSTEM ? FNM_CASEFOLD : 0;
 
     public final static int FNM_NOMATCH = 1;
     public final static int FNM_ERROR   = 2;
 
     public final static byte[] EMPTY = new byte[0];
     public final static byte[] SLASH = new byte[]{'/'};
     public final static byte[] STAR = new byte[]{'*'};
     public final static byte[] DOUBLE_STAR = new byte[]{'*','*'};
 
     private static boolean isdirsep(byte c) {
         return DOSISH ? (c == '\\' || c == '/') : c == '/';
     }
 
     private static int rb_path_next(byte[] _s, int s, int send) {
         while(s < send && !isdirsep(_s[s])) {
             s++;
         }
         return s;
     }
 
     private static int fnmatch_helper(byte[] bytes, int pstart, int pend, byte[] string, int sstart, int send, int flags) {
         char test;
         int s = sstart;
         int pat = pstart;
         boolean escape = (flags & FNM_NOESCAPE) == 0;
         boolean pathname = (flags & FNM_PATHNAME) != 0;
         boolean period = (flags & FNM_DOTMATCH) == 0;
         boolean nocase = (flags & FNM_CASEFOLD) != 0;
 
         while(pat<pend) {
             byte c = bytes[pat++];
             switch(c) {
             case '?':
                 if(s >= send || (pathname && isdirsep(string[s])) || 
                    (period && string[s] == '.' && (s == 0 || (pathname && isdirsep(string[s-1]))))) {
                     return FNM_NOMATCH;
                 }
                 s++;
                 break;
             case '*':
                 while(pat < pend && (c = bytes[pat++]) == '*');
                 if(s < send && (period && string[s] == '.' && (s == 0 || (pathname && isdirsep(string[s-1]))))) {
                     return FNM_NOMATCH;
                 }
                 if(pat > pend || (pat == pend && c == '*')) {
                     if(pathname && rb_path_next(string, s, send) < send) {
                         return FNM_NOMATCH;
                     } else {
                         return 0;
                     }
                 } else if((pathname && isdirsep(c))) {
                     s = rb_path_next(string, s, send);
                     if(s < send) {
                         s++;
                         break;
                     }
                     return FNM_NOMATCH;
                 }
                 test = (char)((escape && c == '\\' && pat < pend ? bytes[pat] : c)&0xFF);
                 test = Character.toLowerCase(test);
                 pat--;
                 while(s < send) {
                     if((c == '?' || c == '[' || Character.toLowerCase((char) string[s]) == test) &&
                        fnmatch(bytes, pat, pend, string, s, send, flags | FNM_DOTMATCH) == 0) {
                         return 0;
                     } else if((pathname && isdirsep(string[s]))) {
                         break;
                     }
                     s++;
                 }
                 return FNM_NOMATCH;
             case '[':
                 if(s >= send || (pathname && isdirsep(string[s]) || 
                                  (period && string[s] == '.' && (s == 0 || (pathname && isdirsep(string[s-1])))))) {
                     return FNM_NOMATCH;
                 }
                 pat = range(bytes, pat, pend, (char)(string[s]&0xFF), flags);
                 if(pat == -1) {
                     return FNM_NOMATCH;
                 }
                 s++;
                 break;
             case '\\':
                 if (escape) {
                     if (pat >= pend) {
                         c = '\\';
                     } else {
                         c = bytes[pat++];
                     }
                 }
             default:
                 if(s >= send) {
                     return FNM_NOMATCH;
                 }
                 if(DOSISH && (pathname && isdirsep(c) && isdirsep(string[s]))) {
                 } else {
                     if (nocase) {
                         if(Character.toLowerCase((char)c) != Character.toLowerCase((char)string[s])) {
                             return FNM_NOMATCH;
                         }
                         
                     } else {
                         if(c != (char)string[s]) {
                             return FNM_NOMATCH;
                         }
                     }
                     
                 }
                 s++;
                 break;
             }
         }
         return s >= send ? 0 : FNM_NOMATCH;
     }
 
     public static int fnmatch(
             byte[] bytes, int pstart, int pend,
             byte[] string, int sstart, int send, int flags) {
         
         // This method handles '**/' patterns and delegates to
         // fnmatch_helper for the main work.
 
         boolean period = (flags & FNM_DOTMATCH) == 0;
         boolean pathname = (flags & FNM_PATHNAME) != 0;
 
         int pat_pos = pstart;
         int str_pos = sstart;
         int ptmp = -1;
         int stmp = -1;
 
         if (pathname) {
             while (true) {
                 if (isDoubleStarAndSlash(bytes, pat_pos)) {
                     do { pat_pos += 3; } while (isDoubleStarAndSlash(bytes, pat_pos));
                     ptmp = pat_pos;
                     stmp = str_pos;
                 }
 
                 int patSlashIdx = nextSlashIndex(bytes, pat_pos, pend);
                 int strSlashIdx = nextSlashIndex(string, str_pos, send);
 
                 if (fnmatch_helper(bytes, pat_pos, patSlashIdx,
                         string, str_pos, strSlashIdx, flags) == 0) {
                     if (patSlashIdx < pend && strSlashIdx < send) {
                         pat_pos = ++patSlashIdx;
                         str_pos = ++strSlashIdx;
                         continue;
                     }
                     if (patSlashIdx == pend && strSlashIdx == send) {
                         return 0;
                     }
                 }
                 /* failed : try next recursion */
                 if (ptmp != -1 && stmp != -1 && !(period && string[stmp] == '.')) {
                     stmp = nextSlashIndex(string, stmp, send);
                     if (stmp < send) {
                         pat_pos = ptmp;
                         stmp++;
                         str_pos = stmp;
                         continue;
                     }
                 }
                 return FNM_NOMATCH;
             }
         } else {
             return fnmatch_helper(bytes, pstart, pend, string, sstart, send, flags);
         }
 
     }
 
     // are we at '**/'
     private static boolean isDoubleStarAndSlash(byte[] bytes, int pos) {
         if ((bytes.length - pos) <= 2) {
             return false; // not enough bytes
         }
 
         return bytes[pos] == '*'
             && bytes[pos + 1] == '*'
             && bytes[pos + 2] == '/';
     }
 
     // Look for slash, starting from 'start' position, until 'end'.
     private static int nextSlashIndex(byte[] bytes, int start, int end) {
         int idx = start;
         while (idx < end && idx < bytes.length && bytes[idx] != '/') {
             idx++;
         }
         return idx;
     }
 
     public static int range(byte[] _pat, int pat, int pend, char test, int flags) {
         boolean not;
         boolean ok = false;
         boolean nocase = (flags & FNM_CASEFOLD) != 0;
         boolean escape = (flags & FNM_NOESCAPE) == 0;
 
         not = _pat[pat] == '!' || _pat[pat] == '^';
         if(not) {
             pat++;
         }
 
         if (nocase) {
             test = Character.toLowerCase(test);
         }
 
         while(_pat[pat] != ']') {
             char cstart, cend;
             if(escape && _pat[pat] == '\\') {
                 pat++;
             }
             if(pat >= pend) {
                 return -1;
             }
             cstart = cend = (char)(_pat[pat++]&0xFF);
             if(_pat[pat] == '-' && _pat[pat+1] != ']') {
                 pat++;
                 if(escape && _pat[pat] == '\\') {
                     pat++;
                 }
                 if(pat >= pend) {
                     return -1;
                 }
 
                 cend = (char)(_pat[pat++] & 0xFF);
             }
 
             if (nocase) {
                 if (Character.toLowerCase(cstart) <= test
                         && test <= Character.toLowerCase(cend)) {
                     ok = true;
                 }
             } else {
                 if (cstart <= test && test <= cend) {
                     ok = true;
                 }
             }
         }
 
         return ok == not ? -1 : pat + 1;
     }
 
     public static List<ByteList> push_glob(String cwd, ByteList globByteList, int flags) {
         List<ByteList> result = new ArrayList<ByteList>();
         if (globByteList.length() > 0) {
             push_braces(cwd, result, new GlobPattern(globByteList, flags));
         }
 
         return result;
     }
     
     private static class GlobPattern {
         final byte[] bytes;        
         final int begin;
         final int end;
         
         int flags;
         int index;
 
         public GlobPattern(ByteList bytelist, int flags) {
             this(bytelist.getUnsafeBytes(), bytelist.getBegin(),  bytelist.getBegin() + bytelist.getRealSize(), flags);
         }
         
         public GlobPattern(byte[] bytes, int index, int end, int flags) {
             this.bytes = bytes;
             this.index = index;
             this.begin = index;
             this.end = end;
             this.flags = flags;
         }
         
         public int findClosingIndexOf(int leftTokenIndex) {
             if (leftTokenIndex == -1 || leftTokenIndex > end) return -1;
             
             byte leftToken = bytes[leftTokenIndex];
             byte rightToken;
             
             switch (leftToken) {
             case '{': rightToken = '}'; break;
             case '[': rightToken = ']'; break;
             default: return -1;
             }
             
             int nest = 1; // leftToken made us start as nest 1
             index = leftTokenIndex + 1;
             while (hasNext()) {
                 byte c = next();
                 
                 if (c == leftToken) {
                     nest++;
                 } else if (c == rightToken && --nest == 0) {
                     return index();
                 }
             }
             
             return -1;
         }
         
         public boolean hasNext() {
             return index < end;
         }
         
         public void reset() {
             index = begin;
         }
         
         public void setIndex(int value) {
             index = value;
         }
         
         // Get index of last read byte
         public int index() {
             return index - 1;
         }
         
         public int indexOf(byte c) {
             while (hasNext()) if (next() == c) return index();
             
             return -1;
         }
         
         public byte next() {
             return bytes[index++];
         }
 
     }
 
     private static interface GlobFunc {
         int call(byte[] ptr, int p, int len, Object ary);
     }
 
     private static class GlobArgs {
         GlobFunc func;
         int c = -1;
         List<ByteList> v;
         
         public GlobArgs(GlobFunc func, List<ByteList> arg) {
             this.func = func;
             this.v = arg;
         }
     }
 
     public final static GlobFunc push_pattern = new GlobFunc() {
             @SuppressWarnings("unchecked")
             public int call(byte[] ptr, int p, int len, Object ary) {
                 ((List) ary).add(new ByteList(ptr, p, len));
                 return 0;
             }
         };
     public final static GlobFunc glob_caller = new GlobFunc() {
         public int call(byte[] ptr, int p, int len, Object ary) {
             GlobArgs args = (GlobArgs)ary;
             args.c = p;
             return args.func.call(ptr, args.c, len, args.v);
         }
     };
 
     /*
      * Process {}'s (example: Dir.glob("{jruby,jython}/README*") 
      */
     private static int push_braces(String cwd, List<ByteList> result, GlobPattern pattern) {
         pattern.reset();
         int lbrace = pattern.indexOf((byte) '{'); // index of left-most brace
         int rbrace = pattern.findClosingIndexOf(lbrace);// index of right-most brace
 
         // No or mismatched braces..Move along..nothing to see here
         if (lbrace == -1 || rbrace == -1) return push_globs(cwd, result, pattern); 
 
         // Peel onion...make subpatterns out of outer layer of glob and recall with each subpattern 
         // Example: foo{a{c},b}bar -> fooa{c}bar, foobbar
         ByteList buf = new ByteList(20);
         int middleRegionIndex;
         int i = lbrace;
         while (pattern.bytes[i] != '}') {
             middleRegionIndex = i + 1;
             for(i = middleRegionIndex; i < pattern.end && pattern.bytes[i] != '}' && pattern.bytes[i] != ','; i++) {
                 if (pattern.bytes[i] == '{') i = pattern.findClosingIndexOf(i); // skip inner braces
             }
 
             buf.length(0);
             buf.append(pattern.bytes, pattern.begin, lbrace - pattern.begin);
             buf.append(pattern.bytes, middleRegionIndex, i - middleRegionIndex);
             buf.append(pattern.bytes, rbrace + 1, pattern.end - (rbrace + 1));
             int status = push_braces(cwd, result, new GlobPattern(buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize(),pattern.flags));
             if(status != 0) return status;
         }
         
         return 0; // All braces pushed..
     }
 
     private static int push_globs(String cwd, List<ByteList> ary, GlobPattern pattern) {
         pattern.flags |= FNM_SYSCASE;
         return glob_helper(cwd, pattern.bytes, pattern.begin, pattern.end, -1, pattern.flags, glob_caller, new GlobArgs(push_pattern, ary));
     }
 
     private static boolean has_magic(byte[] bytes, int begin, int end, int flags) {
         boolean escape = (flags & FNM_NOESCAPE) == 0;
         boolean nocase = (flags & FNM_CASEFOLD) != 0;
         int open = 0;
 
         for (int i = begin; i < end; i++) {
             switch(bytes[i]) {
             case '?':
             case '*':
                 return true;
             case '[':	/* Only accept an open brace if there is a close */
                 open++;	/* brace to match it.  Bracket expressions must be */
                 continue;	/* complete, according to Posix.2 */
             case ']':
                 if (open > 0) return true;
 
                 continue;
             case '\\':
                 if (escape && i == end) return false;
 
                 break;
             default:
                 if (FNM_SYSCASE == 0 && nocase && Character.isLetter((char)(bytes[i]&0xFF))) return true;
             }
         }
 
         return false;
     }
 
     private static int remove_backslashes(byte[] bytes, int index, int len) {
         int t = index;
         
         for (; index < len; index++, t++) {
             if (bytes[index] == '\\' && ++index == len) break;
             
             bytes[t] = bytes[index];
         }
         
         return t;
     }
 
     private static int strchr(byte[] bytes, int begin, int end, byte ch) {
         for (int i = begin; i < end; i++) {
             if (bytes[i] == ch) return i;
         }
         
         return -1;
     }
 
     private static byte[] extract_path(byte[] bytes, int begin, int end) {
         int len = end - begin;
         
         if (len > 1 && bytes[end-1] == '/' && (!DOSISH || (len < 2 || bytes[end-2] != ':'))) len--;
 
         byte[] alloc = new byte[len];
         System.arraycopy(bytes,begin,alloc,0,len);
         return alloc;
     }
 
     private static byte[] extract_elem(byte[] bytes, int begin, int end) {
         int elementEnd = strchr(bytes, begin, end, (byte)'/');
         if (elementEnd == -1) elementEnd = end;
         
         return extract_path(bytes, begin, elementEnd);
     }
 
     private static boolean BASE(byte[] base) {
         return DOSISH ? 
             (base.length > 0 && !((isdirsep(base[0]) && base.length < 2) || (base.length > 2 && base[1] == ':' && isdirsep(base[2]) && base.length < 4)))
             :
             (base.length > 0 && !(isdirsep(base[0]) && base.length < 2));
     }
     
     private static boolean isJarFilePath(byte[] bytes, int begin, int end) {
         return end > 6 && bytes[begin] == 'f' && bytes[begin+1] == 'i' &&
             bytes[begin+2] == 'l' && bytes[begin+3] == 'e' && bytes[begin+4] == ':';
     }
 
     private static String[] files(File directory) {
         String[] files = directory.list();
         
         if (files != null) {
             String[] filesPlusDotFiles = new String[files.length + 2];
             System.arraycopy(files, 0, filesPlusDotFiles, 2, files.length);
             filesPlusDotFiles[0] = ".";
             filesPlusDotFiles[1] = "..";
 
             return filesPlusDotFiles;
         } else {
             return new String[0];
         }
     }
 
     private static int glob_helper(String cwd, byte[] bytes, int begin, int end, int sub, int flags, GlobFunc func, GlobArgs arg) {
         int p,m;
         int status = 0;
         byte[] newpath = null;
         File st;
         p = sub != -1 ? sub : begin;
         if (!has_magic(bytes, p, end, flags)) {
             if (DOSISH || (flags & FNM_NOESCAPE) == 0) {
                 newpath = new byte[end];
                 System.arraycopy(bytes,0,newpath,0,end);
                 if (sub != -1) {
                     p = (sub - begin);
                     end = remove_backslashes(newpath, p, end);
                     sub = p;
                 } else {
                     end = remove_backslashes(newpath, 0, end);
                     bytes = newpath;
                 }
             }
 
             if (bytes[begin] == '/' || (DOSISH && begin+2<end && bytes[begin+1] == ':' && isdirsep(bytes[begin+2]))) {
                 if (new JavaSecuredFile(newStringFromUTF8(bytes, begin, end - begin)).exists()) {
                     status = func.call(bytes, begin, end - begin, arg);
                 }
             } else if (isJarFilePath(bytes, begin, end)) {
                 int ix = end;
                 for(int i = 0;i<end;i++) {
                     if(bytes[begin+i] == '!') {
                         ix = i;
                         break;
                     }
                 }
 
                 st = new JavaSecuredFile(newStringFromUTF8(bytes, begin+5, ix-5));
                 try {
                     String jar = newStringFromUTF8(bytes, begin+ix+1, end-(ix+1));
                     JarFile jf = new JarFile(st);
                     
                     if (jar.startsWith("/")) jar = jar.substring(1);
-                    if (jf.getEntry(jar + "/") != null) jar = jar + "/";
-                    if (jf.getEntry(jar) != null) {
+                    ZipEntry entry = RubyFile.getDirOrFileEntry(jf, jar);
+                    if (entry != null) {
                         status = func.call(bytes, begin, end, arg);
                     }
                 } catch(Exception e) {}
             } else if ((end - begin) > 0) { // Length check is a hack.  We should not be reeiving "" as a filename ever. 
                 if (new JavaSecuredFile(cwd, newStringFromUTF8(bytes, begin, end - begin)).exists()) {
                     status = func.call(bytes, begin, end - begin, arg);
                 }
             }
 
             return status;
         }
         
         ByteList buf = new ByteList(20);
         List<ByteList> link = new ArrayList<ByteList>();
         mainLoop: while(p != -1 && status == 0) {
             if (bytes[p] == '/') p++;
 
             m = strchr(bytes, p, end, (byte)'/');
             if(has_magic(bytes, p, m == -1 ? end : m, flags)) {
                 finalize: do {
                     byte[] base = extract_path(bytes, begin, p);
                     byte[] dir = begin == p ? new byte[]{'.'} : base; 
                     byte[] magic = extract_elem(bytes,p,end);
                     boolean recursive = false;
                     String jar = null;
                     JarFile jf = null;
 
                     if(dir[0] == '/'  || (DOSISH && 2<dir.length && dir[1] == ':' && isdirsep(dir[2]))) {
                         st = new JavaSecuredFile(newStringFromUTF8(dir));
                     } else if(isJarFilePath(dir, 0, dir.length)) {
                         int ix = dir.length;
                         for(int i = 0;i<dir.length;i++) {
                             if(dir[i] == '!') {
                                 ix = i;
                                 break;
                             }
                         }
 
                         st = new JavaSecuredFile(newStringFromUTF8(dir, 5, ix-5));
                         jar = newStringFromUTF8(dir, ix+1, dir.length-(ix+1));
                         try {
                             jf = new JarFile(st);
 
                             if (jar.startsWith("/")) jar = jar.substring(1);
                             if (jf.getEntry(jar + "/") != null) jar = jar + "/";
                         } catch(Exception e) {
                             jar = null;
                             jf = null;
                         }
                     } else {
                         st = new JavaSecuredFile(cwd, newStringFromUTF8(dir));
                     }
 
                     if((jf != null && ("".equals(jar) || (jf.getJarEntry(jar) != null && jf.getJarEntry(jar).isDirectory()))) || st.isDirectory()) {
                         if(m != -1 && Arrays.equals(magic, DOUBLE_STAR)) {
                             int n = base.length;
                             recursive = true;
                             buf.length(0);
                             buf.append(base);
                             buf.append(bytes, (base.length > 0 ? m : m + 1), end - (base.length > 0 ? m : m + 1));
                             status = glob_helper(cwd, buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize(), n, flags, func, arg);
                             if(status != 0) {
                                 break finalize;
                             }
                         }
                     } else {
                         break mainLoop;
                     }
 
                     if(jar == null) {
                         String[] dirp = files(st);
 
                         for(int i=0;i<dirp.length;i++) {
                             if(recursive) {
                                 byte[] bs = getBytesInUTF8(dirp[i]);
                                 if (fnmatch(STAR,0,1,bs,0,bs.length,flags) != 0) {
                                     continue;
                                 }
                                 buf.length(0);
                                 buf.append(base);
                                 buf.append( BASE(base) ? SLASH : EMPTY );
                                 buf.append(getBytesInUTF8(dirp[i]));
                                 if (buf.getUnsafeBytes()[0] == '/' || (DOSISH && 2<buf.getRealSize() && buf.getUnsafeBytes()[1] == ':' && isdirsep(buf.getUnsafeBytes()[2]))) {
                                     st = new JavaSecuredFile(newStringFromUTF8(buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize()));
                                 } else {
                                     st = new JavaSecuredFile(cwd, newStringFromUTF8(buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize()));
                                 }
                                 if(st.isDirectory() && !".".equals(dirp[i]) && !"..".equals(dirp[i])) {
                                     int t = buf.getRealSize();
                                     buf.append(SLASH);
                                     buf.append(DOUBLE_STAR);
                                     buf.append(bytes, m, end - m);
                                     status = glob_helper(cwd, buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize(), t, flags, func, arg);
                                     if(status != 0) {
                                         break;
                                     }
                                 }
                                 continue;
                             }
                             byte[] bs = getBytesInUTF8(dirp[i]);
                             if(fnmatch(magic,0,magic.length,bs,0, bs.length,flags) == 0) {
                                 buf.length(0);
                                 buf.append(base);
                                 buf.append( BASE(base) ? SLASH : EMPTY );
                                 buf.append(getBytesInUTF8(dirp[i]));
                                 if(m == -1) {
                                     status = func.call(buf.getUnsafeBytes(),0, buf.getRealSize(),arg);
                                     if(status != 0) {
                                         break;
                                     }
                                     continue;
                                 }
                                 link.add(buf);
                                 buf = new ByteList(20);
                             }
                         }
                     } else {
                         try {
                             List<JarEntry> dirp = new ArrayList<JarEntry>();
                             for(Enumeration<JarEntry> eje = jf.entries(); eje.hasMoreElements(); ) {
                                 JarEntry je = eje.nextElement();
                                 String name = je.getName();
                                 int ix = name.indexOf('/', jar.length());
                                 if (ix == -1 || ix == name.length()-1) {
                                     if("/".equals(jar) || (name.startsWith(jar) && name.length()>jar.length())) {
                                         dirp.add(je);
                                     }
                                 }
                             }
                             for(JarEntry je : dirp) {
                                 byte[] bs = getBytesInUTF8(je.getName());
                                 int len = bs.length;
 
                                 if(je.isDirectory()) {
                                     len--;
                                 }
 
                                 if(recursive) {
                                     if(fnmatch(STAR,0,1,bs,0,len,flags) != 0) {
                                         continue;
                                     }
                                     buf.length(0);
                                     buf.append(base, 0, base.length - jar.length());
                                     buf.append( BASE(base) ? SLASH : EMPTY );
                                     buf.append(bs, 0, len);
 
                                     if(je.isDirectory()) {
                                         int t = buf.getRealSize();
                                         buf.append(SLASH);
                                         buf.append(DOUBLE_STAR);
                                         buf.append(bytes, m, end - m);
                                         status = glob_helper(cwd, buf.getUnsafeBytes(), buf.getBegin(), buf.getRealSize(), t, flags, func, arg);
                                         if(status != 0) {
                                             break;
                                         }
                                     }
                                     continue;
                                 }
 
                                 if(fnmatch(magic,0,magic.length,bs,0,len,flags) == 0) {
                                     buf.length(0);
                                     buf.append(base, 0, base.length - jar.length());
                                     buf.append( BASE(base) ? SLASH : EMPTY );
                                     buf.append(bs, 0, len);
                                     if(m == -1) {
                                         status = func.call(buf.getUnsafeBytes(),0, buf.getRealSize(),arg);
                                         if(status != 0) {
                                             break;
                                         }
                                         continue;
                                     }
                                     link.add(buf);
                                     buf = new ByteList(20);
                                 }
                             }
                         } catch(Exception e) {}
                     }
                 } while(false);
 
                 if (link.size() > 0) {
                     for (ByteList b : link) {
                         if (status == 0) {
                             if(b.getUnsafeBytes()[0] == '/'  || (DOSISH && 2<b.getRealSize() && b.getUnsafeBytes()[1] == ':' && isdirsep(b.getUnsafeBytes()[2]))) {
                                 st = new JavaSecuredFile(newStringFromUTF8(b.getUnsafeBytes(), 0, b.getRealSize()));
                             } else {
                                 st = new JavaSecuredFile(cwd, newStringFromUTF8(b.getUnsafeBytes(), 0, b.getRealSize()));
                             }
 
                             if(st.isDirectory()) {
                                 int len = b.getRealSize();
                                 buf.length(0);
                                 buf.append(b);
                                 buf.append(bytes, m, end - m);
                                 status = glob_helper(cwd, buf.getUnsafeBytes(),0, buf.getRealSize(),len,flags,func,arg);
                             }
                         }
                     }
                     break mainLoop;
                 }
             }
             p = m;
         }
         return status;
     }
 
     private static byte[] getBytesInUTF8(String s) {
         return RubyEncoding.encodeUTF8(s);
     }
 
     private static String newStringFromUTF8(byte[] buf, int offset, int len) {
         return RubyEncoding.decodeUTF8(buf, offset, len);
     }
 
     private static String newStringFromUTF8(byte[] buf) {
         return RubyEncoding.decodeUTF8(buf);
     }
 }
diff --git a/src/org/jruby/util/io/ChannelDescriptor.java b/src/org/jruby/util/io/ChannelDescriptor.java
index 321d649b11..db4e0fe57b 100644
--- a/src/org/jruby/util/io/ChannelDescriptor.java
+++ b/src/org/jruby/util/io/ChannelDescriptor.java
@@ -1,762 +1,763 @@
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
+import org.jruby.RubyFile;
 
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
-                ZipEntry zf = jf.getEntry(internalPath);
+                ZipEntry entry = RubyFile.getFileEntry(jf, internalPath);
 
-                if(zf == null) {
+                if (entry == null) {
                     throw new FileNotFoundException(path);
                 }
 
-                InputStream is = jf.getInputStream(zf);
+                InputStream is = jf.getInputStream(entry);
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
