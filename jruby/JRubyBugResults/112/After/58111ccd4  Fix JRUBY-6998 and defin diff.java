diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index a264595dea..7cffc58539 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1179 +1,1196 @@
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
 
 import jnr.constants.platform.OpenFlags;
 import org.jcodings.Encoding;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.ChannelDescriptor;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.net.URI;
 import java.net.URL;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.FileLock;
 import java.util.Enumeration;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipFile;
 import org.jcodings.specific.ASCIIEncoding;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import jnr.posix.FileStat;
 import jnr.posix.util.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.DirectoryAsFileException;
 import org.jruby.util.io.PermissionDeniedException;
 import org.jruby.util.io.Stream;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.IOOptions;
 import org.jruby.util.FileResource;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 import static org.jruby.CompatVersion.*;
 
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
+        
+        // NULL device
+        if (runtime.is1_9() || runtime.is2_0()) {
+            constants.setConstant("NULL", runtime.newString(getNullDevice()));
+        }
 
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
 
+    private static String getNullDevice() {
+        // FIXME: MRI defines special null device for Amiga and VMS, but currently
+        // we lack ability to detect these platforms
+        String null_device;
+        if (Platform.IS_WINDOWS) {
+            null_device = "NUL";
+        } else {
+            null_device = "/dev/null";
+        }
+        return null_device;
+    }
+
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
             throw context.runtime.newRuntimeError("reinitializing File");
         }
 
         if (args.length > 0 && args.length <= 3) {
             IRubyObject fd = TypeConverter.convertToTypeWithCheck(args[0], context.runtime.getFixnum(), "to_int");
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
             throw context.runtime.newErrnoENOENTError(path);
         }
 
         return context.runtime.newFixnum(context.runtime.getPosix().chmod(path, mode));
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
             throw context.runtime.newErrnoENOENTError(path);
         }
 
         return context.runtime.newFixnum(context.runtime.getPosix().chown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject atime(ThreadContext context) {
         checkClosed(context);
         return context.runtime.newFileStat(path, false).atime();
     }
 
     @JRubyMethod
     public IRubyObject ctime(ThreadContext context) {
         checkClosed(context);
         return context.runtime.newFileStat(path, false).ctime();
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject lchmod(ThreadContext context, IRubyObject arg) {
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw context.runtime.newErrnoENOENTError(path);
         }
 
         return context.runtime.newFixnum(context.runtime.getPosix().lchmod(path, mode));
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
             throw context.runtime.newErrnoENOENTError(path);
         }
 
         return context.runtime.newFixnum(context.runtime.getPosix().lchown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject lstat(ThreadContext context) {
         checkClosed(context);
         return context.runtime.newFileStat(path, true);
     }
     
     @JRubyMethod
     public IRubyObject mtime(ThreadContext context) {
         checkClosed(context);
         return context.runtime.newFileStat(path, false).mtime();
     }
 
     @JRubyMethod(meta = true, compat = RUBY1_9)
     public static IRubyObject path(ThreadContext context, IRubyObject self, IRubyObject str) {
         return get_path(context, str);
     }
 
     @JRubyMethod(name = {"path", "to_path"})
     public IRubyObject path(ThreadContext context) {
         IRubyObject newPath = context.runtime.getNil();
         if (path != null) {
             newPath = context.runtime.newString(path);
             newPath.setTaint(true);
         }
         return newPath;
     }
 
     @JRubyMethod
     @Override
     public IRubyObject stat(ThreadContext context) {
         checkClosed(context);
         return context.runtime.newFileStat(path, false);
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject truncate(ThreadContext context, IRubyObject arg) {
         RubyInteger newLength = arg.convertToInteger();
         if (newLength.getLongValue() < 0) {
             throw context.runtime.newErrnoEINVALError(path);
         }
         try {
             openFile.checkWritable(context.runtime);
             openFile.getMainStreamSafe().ftruncate(newLength.getLongValue());
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.runtime.newErrnoESPIPEError();
         } catch (InvalidValueException ex) {
             throw context.runtime.newErrnoEINVALError();
         } catch (IOException e) {
             // Should we do anything?
         }
 
         return RubyFixnum.zero(context.runtime);
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
                     return RubyString.newEmptyString(context.runtime).infectBy(args[0]);
                 case 3:
                     return context.runtime.newString(name.substring(2)).infectBy(args[0]);
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
         return context.runtime.newString(name).infectBy(args[0]);
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject chmod(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         
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
         Ruby runtime = context.runtime;
 
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
                     return context.runtime.newString(jfilename.substring(0, 2) + ".");
                 } else {
                     return context.runtime.newString(".");
                 }
             }
             if (index == 0) {
                 return context.runtime.newString("/");
             }
 
             if (startsWithDriveLetterOnWindows && index == 2) {
                 // Include additional path separator
                 // (so that dirname of "C:\file.txt" is  "C:\", not "C:")
                 index++;
             }
 
             if (jfilename.startsWith("\\\\")) {
                 index = jfilename.length();
                 String[] splitted = jfilename.split(Pattern.quote("\\"));
                 int last = splitted.length-1;
                 if (splitted[last].contains(".")) {
                     index = jfilename.lastIndexOf("\\");
                 }
                 
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
 
         return context.runtime.newString(result).infectBy(filename);
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
 
         return context.runtime.newString(result);
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
         path.force_encoding(context, context.runtime.getEncodingService().getDefaultExternal());
 
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
             throw context.runtime.newErrnoENOENTError(file.toString());
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
             return context.runtime.getTrue();
         }
         return context.runtime.getFalse();
     }
     
     @JRubyMethod(name = "ftype", required = 1, meta = true)
     public static IRubyObject ftype(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         return context.runtime.newFileStat(get_path(context, filename).getUnicodeValue(), true).ftype();
     }
     
     /*
      * Fixme:  This does not have exact same semantics as RubyArray.join, but they
      * probably could be consolidated (perhaps as join(args[], sep, doChomp)).
      */
     @JRubyMethod(rest = true, meta = true)
     public static RubyString join(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return join(context, recv, RubyArray.newArrayNoCopyLight(context.runtime, args));
     }
     
     @JRubyMethod(name = "lstat", required = 1, meta = true)
     public static IRubyObject lstat(ThreadContext context, IRubyObject recv, IRubyObject filename) {
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
         if (!(arg1 instanceof RubyString) && arg1.respondsTo("to_path")) {
             arg1 = arg1.callMethod(context, "to_path");
         }
         return truncateCommon(context, recv, arg1, arg2);
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
