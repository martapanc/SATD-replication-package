diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 82f07a3b5e..0fd127e300 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1332 +1,1334 @@
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
             if(path.startsWith("file:")) {
                 String filePath = path.substring(5, path.indexOf("!"));
                 String internalPath = path.substring(path.indexOf("!") + 2);
 
                 java.util.jar.JarFile jf = new java.util.jar.JarFile(filePath);
                 return jf.getInputStream(jf.getEntry(internalPath));
             } else {
                 return new FileInputStream(path);
             }
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         } catch (SecurityException se) {
             throw runtime.newIOError(se.getMessage());
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
             } else if(newPath.startsWith("file:")) {
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
                 handler = new IOHandlerUnseekable(getRuntime(), is, null);
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
 
         IRubyObject filename = args[0].convertToString();
         getRuntime().checkSafeString(filename);
         path = filename.toString();
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
 
         while (result.length() > 1 && result.charAt(result.length() - 1) == '/') {
             result = result.substring(0, result.length() - 1);
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
         if (args.length == 3) {
             flags = RubyNumeric.num2int(args[2]);
         } else {
             flags = 0;
         }
-        
+
         ByteList pattern = args[0].convertToString().getByteList();
         ByteList path = args[1].convertToString().getByteList();
-        if (org.jruby.util.Dir.fnmatch(pattern.bytes, pattern.begin, pattern.realSize , path.bytes, path.begin, path.realSize, flags) == 0) {
+
+        if (org.jruby.util.Dir.fnmatch(pattern.bytes, pattern.begin, pattern.begin+pattern.realSize, 
+                                       path.bytes, path.begin, path.begin+path.realSize, flags) == 0) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
     
     @JRubyMethod(name = "ftype", required = 1, meta = true)
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
     
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, meta = true)
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         return open(recv, args, true, block);
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
             throw runtime.newErrnoENOENTError("No such file or directory - " + oldNameString + 
                     " or " + newNameString);
         }
 
         JRubyFile dest = JRubyFile.create(runtime.getCurrentDirectory(), newNameString.toString());
 
         if (oldFile.renameTo(dest)) {  // rename is successful
             return RubyFixnum.zero(runtime);
         }
 
         // rename via Java API call wasn't successful, let's try some tricks, similar to MRI 
 
         if (newFile.exists()) {
             recv.getRuntime().getPosix().chmod(newNameString.toString(), 0666);
             newFile.delete();
         }
 
         if (oldFile.renameTo(dest)) { // try to rename one more time
             return RubyFixnum.zero(runtime);
         }
 
         throw runtime.newErrnoEACCESError("Permission denied - " + oldNameString + " or " + 
                 newNameString);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static RubyArray split(IRubyObject recv, IRubyObject arg) {
         RubyString filename = RubyString.stringValue(arg);
         
         return filename.getRuntime().newArray(dirname(recv, filename),
                 basename(recv, new IRubyObject[] { filename }));
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject symlink(IRubyObject recv, IRubyObject from, IRubyObject to) {
         RubyString fromStr = RubyString.stringValue(from);
         RubyString toStr = RubyString.stringValue(to);
         try {
             if (recv.getRuntime().getPosix().symlink(
                     fromStr.toString(), toStr.toString()) == -1) {
                 // FIXME: When we get JNA3 we need to properly write this to errno.
                 throw recv.getRuntime().newErrnoEEXISTError("File exists - " 
                                + fromStr + " or " + toStr);
             }
         } catch (java.lang.UnsatisfiedLinkError ule) {
             throw recv.getRuntime().newNotImplementedError(
                     "symlink() function is unimplemented on this machine");
         }
         
         return recv.getRuntime().newFixnum(0);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject readlink(IRubyObject recv, IRubyObject path) {
         String realPath = recv.getRuntime().getPosix().readlink(path.toString());
         
         if (!RubyFileTest.exist_p(recv, path).isTrue()) {
             throw recv.getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
         
         if (!RubyFileTest.symlink_p(recv, path).isTrue()) {
             throw recv.getRuntime().newErrnoEINVALError("invalid argument - " + path);
         }
         
         if (realPath == null) {
             // FIXME: When we get JNA3 we need to properly write this to errno.
         }
 
         return recv.getRuntime().newString(realPath);
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
 
     @JRubyMethod(meta = true, optional = 1)
     public static IRubyObject umask(IRubyObject recv, IRubyObject[] args) {
         int oldMask = 0;
         if (args.length == 0) {
             oldMask = recv.getRuntime().getPosix().umask(0);
         } else if (args.length == 1) {
             oldMask = recv.getRuntime().getPosix().umask((int) args[0].convertToInteger().getLongValue()); 
         } else {
             recv.getRuntime().newArgumentError("wrong number of arguments");
         }
         
         return recv.getRuntime().newFixnum(oldMask);
     }
 
     /**
      * This method does NOT set atime, only mtime, since Java doesn't support anything else.
      */
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject utime(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         
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
             
             boolean isSymlink = RubyFileTest.symlink_p(recv, filename).isTrue();
             // Broken symlinks considered by exists() as non-existing,
             // so we need to check for symlinks explicitly.
             if (!lToDelete.exists() && !isSymlink) {
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
diff --git a/src/org/jruby/util/Dir.java b/src/org/jruby/util/Dir.java
index 7eb0b6b620..b09a2c3267 100644
--- a/src/org/jruby/util/Dir.java
+++ b/src/org/jruby/util/Dir.java
@@ -1,703 +1,702 @@
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
  * Copyright (C) 2007 Ola Bini <ola@ologix.com>
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
 import java.util.List;
 
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
 
 import java.util.Enumeration;
 
 /**
  * This class exists as a counterpart to the dir.c file in 
  * MRI source. It contains many methods useful for 
  * File matching and Globbing.
  *
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class Dir {
     public final static boolean DOSISH = System.getProperty("os.name").indexOf("Windows") != -1;
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
 
-    private static int rb_path_next(byte[] _s, int s, int len) {
-        while(s < len && !isdirsep(_s[s])) {
+    private static int rb_path_next(byte[] _s, int s, int send) {
+        while(s < send && !isdirsep(_s[s])) {
             s++;
         }
         return s;
     }
 
-    public static int fnmatch(byte[] bytes, int pstart, int plen, byte[] string, int sstart, int slen, int flags) {
+    public static int fnmatch(byte[] bytes, int pstart, int pend, byte[] string, int sstart, int send, int flags) {
         char test;
         int s = sstart;
         int pat = pstart;
-        int len = plen;
         boolean escape = (flags & FNM_NOESCAPE) == 0;
         boolean pathname = (flags & FNM_PATHNAME) != 0;
         boolean period = (flags & FNM_DOTMATCH) == 0;
         boolean nocase = (flags & FNM_CASEFOLD) != 0;
 
-        while(pat<len) {
+        while(pat<pend) {
             byte c = bytes[pat++];
             switch(c) {
             case '?':
-                if(s >= slen || (pathname && isdirsep(string[s])) || 
+                if(s >= send || (pathname && isdirsep(string[s])) || 
                    (period && string[s] == '.' && (s == 0 || (pathname && isdirsep(string[s-1]))))) {
                     return FNM_NOMATCH;
                 }
                 s++;
                 break;
             case '*':
-                while(pat < len && (c = bytes[pat++]) == '*');
-                if(s < slen && (period && string[s] == '.' && (s == 0 || (pathname && isdirsep(string[s-1]))))) {
+                while(pat < pend && (c = bytes[pat++]) == '*');
+                if(s < send && (period && string[s] == '.' && (s == 0 || (pathname && isdirsep(string[s-1]))))) {
                     return FNM_NOMATCH;
                 }
-                if(pat > len || (pat == len && c == '*')) {
-                    if(pathname && rb_path_next(string, s, slen) < slen) {
+                if(pat > pend || (pat == pend && c == '*')) {
+                    if(pathname && rb_path_next(string, s, send) < send) {
                         return FNM_NOMATCH;
                     } else {
                         return 0;
                     }
                 } else if((pathname && isdirsep(c))) {
-                    s = rb_path_next(string, s, slen);
-                    if(s < slen) {
+                    s = rb_path_next(string, s, send);
+                    if(s < send) {
                         s++;
                         break;
                     }
                     return FNM_NOMATCH;
                 }
-                test = (char)((escape && c == '\\' && pat < len ? bytes[pat] : c)&0xFF);
+                test = (char)((escape && c == '\\' && pat < pend ? bytes[pat] : c)&0xFF);
                 test = Character.toLowerCase(test);
                 pat--;
-                while(s < slen) {
+                while(s < send) {
                     if((c == '?' || c == '[' || Character.toLowerCase((char) string[s]) == test) &&
-                       fnmatch(bytes, pat, plen, string, s, slen, flags | FNM_DOTMATCH) == 0) {
+                       fnmatch(bytes, pat, pend, string, s, send, flags | FNM_DOTMATCH) == 0) {
                         return 0;
                     } else if((pathname && isdirsep(string[s]))) {
                         break;
                     }
                     s++;
                 }
                 return FNM_NOMATCH;
             case '[':
-                if(s >= slen || (pathname && isdirsep(string[s]) || 
+                if(s >= send || (pathname && isdirsep(string[s]) || 
                                  (period && string[s] == '.' && (s == 0 || (pathname && isdirsep(string[s-1])))))) {
                     return FNM_NOMATCH;
                 }
-                pat = range(bytes, pat, plen, (char)(string[s]&0xFF), flags);
+                pat = range(bytes, pat, pend, (char)(string[s]&0xFF), flags);
                 if(pat == -1) {
                     return FNM_NOMATCH;
                 }
                 s++;
                 break;
             case '\\':
                 if(escape &&
                    (!DOSISH ||
-                    (pat < len && "*?[]\\".indexOf((char)bytes[pat]) != -1))) {
-                    if(pat >= len) {
+                    (pat < pend && "*?[]\\".indexOf((char)bytes[pat]) != -1))) {
+                    if(pat >= pend) {
                         c = '\\';
                     } else {
                         c = bytes[pat++];
                     }
                 }
             default:
-                if(s >= slen) {
+                if(s >= send) {
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
-        return s >= slen ? 0 : FNM_NOMATCH;
+        return s >= send ? 0 : FNM_NOMATCH;
     }
 
-    public static int range(byte[] _pat, int pat, int len, char test, int flags) {
+    public static int range(byte[] _pat, int pat, int pend, char test, int flags) {
         boolean not;
         boolean ok = false;
         //boolean nocase = (flags & FNM_CASEFOLD) != 0;
         boolean escape = (flags & FNM_NOESCAPE) == 0;
 
         not = _pat[pat] == '!' || _pat[pat] == '^';
         if(not) {
             pat++;
         }
 
         test = Character.toLowerCase(test);
 
         while(_pat[pat] != ']') {
             int cstart, cend;
             if(escape && _pat[pat] == '\\') {
                 pat++;
             }
-            if(pat >= len) {
+            if(pat >= pend) {
                 return -1;
             }
             cstart = cend = (char)(_pat[pat++]&0xFF);
             if(_pat[pat] == '-' && _pat[pat+1] != ']') {
                 pat++;
                 if(escape && _pat[pat] == '\\') {
                     pat++;
                 }
-                if(pat >= len) {
+                if(pat >= pend) {
                     return -1;
                 }
 
                 cend = (char)(_pat[pat++] & 0xFF);
             }
             if (Character.toLowerCase((char) cstart) <= test && 
                     test <= Character.toLowerCase((char) cend)) {
                 ok = true;
             }
         }
 
         return ok == not ? -1 : pat + 1;
     }
 
     public static List<ByteList> push_glob(String cwd, ByteList globByteList, int flags) {
         List<ByteList> result = new ArrayList<ByteList>();
 
         push_braces(cwd, result, new GlobPattern(globByteList, flags));
 
         return result;
     }
     
     private static class GlobPattern {
         byte[] bytes;
         int index;
         int begin;
         int end;
         int flags;
         
         public GlobPattern(ByteList bytelist, int flags) {
             this(bytelist.bytes, bytelist.begin,  bytelist.begin + bytelist.realSize, flags);
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
                 if (pattern.bytes[i] == '{') pattern.findClosingIndexOf(i); // skip inner braces
             }
 
             buf.length(0);
             buf.append(pattern.bytes, pattern.begin, lbrace - pattern.begin);
             buf.append(pattern.bytes, middleRegionIndex, i - middleRegionIndex);
             buf.append(pattern.bytes, rbrace + 1, pattern.end - (rbrace + 1));
             int status = push_braces(cwd, result, new GlobPattern(buf.bytes, buf.begin, buf.realSize, pattern.flags));
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
         
         String[] filesPlusDotFiles = new String[files.length + 2];
         System.arraycopy(files, 0, filesPlusDotFiles, 2, files.length);
         filesPlusDotFiles[0] = ".";
         filesPlusDotFiles[1] = "..";
         
         return filesPlusDotFiles;
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
                 if (new File(new String(bytes, begin, end - begin)).exists()) {
                     status = func.call(bytes, begin, end, arg);
                 }
             } else if (isJarFilePath(bytes, begin, end)) {
                 int ix = -1;
                 for(int i = 0;i<end;i++) {
                     if(bytes[begin+i] == '!') {
                         ix = i;
                         break;
                     }
                 }
 
                 st = new File(new String(bytes, begin+5, ix-5));
                 String jar = new String(bytes, begin+ix+1, end-(ix+1));
                 try {
                     JarFile jf = new JarFile(st);
                     
                     if (jar.startsWith("/")) jar = jar.substring(1);
                     if (jf.getEntry(jar + "/") != null) jar = jar + "/";
                     if (jf.getEntry(jar) != null) {
                         status = func.call(bytes, begin, end, arg);
                     }
                 } catch(Exception e) {}
             } else if ((end - begin) > 0) { // Length check is a hack.  We should not be reeiving "" as a filename ever. 
                 if (new File(cwd, new String(bytes, begin, end - begin)).exists()) {
                     status = func.call(bytes, begin, end, arg);
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
                         st = new File(new String(dir));
                     } else if(isJarFilePath(dir, 0, dir.length)) {
                         int ix = -1;
                         for(int i = 0;i<dir.length;i++) {
                             if(dir[i] == '!') {
                                 ix = i;
                                 break;
                             }
                         }
 
                         st = new File(new String(dir, 5, ix-5));
                         jar = new String(dir, ix+1, dir.length-(ix+1));
                         try {
                             jf = new JarFile(st);
 
                             if (jar.startsWith("/")) jar = jar.substring(1);
                             if (jf.getEntry(jar + "/") != null) jar = jar + "/";
                         } catch(Exception e) {
                             jar = null;
                             jf = null;
                         }
                     } else {
                         st = new File(cwd, new String(dir));
                     }
 
                     if((jf != null && ("".equals(jar) || (jf.getJarEntry(jar) != null && jf.getJarEntry(jar).isDirectory()))) || st.isDirectory()) {
                         if(m != -1 && Arrays.equals(magic, DOUBLE_STAR)) {
                             int n = base.length;
                             recursive = true;
                             buf.length(0);
                             buf.append(base);
                             buf.append(bytes, (base.length > 0 ? m : m + 1), end - (base.length > 0 ? m : m + 1));
                             status = glob_helper(cwd, buf.bytes, buf.begin, buf.realSize, n, flags, func, arg);
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
                                 byte[] bs = dirp[i].getBytes();
                                 if (fnmatch(STAR,0,1,bs,0,bs.length,flags) != 0) {
                                     continue;
                                 }
                                 buf.length(0);
                                 buf.append(base);
                                 buf.append( BASE(base) ? SLASH : EMPTY );
                                 buf.append(dirp[i].getBytes());
                                 if (buf.bytes[0] == '/' || (DOSISH && 2<buf.realSize && buf.bytes[1] == ':' && isdirsep(buf.bytes[2]))) {
                                     st = new File(new String(buf.bytes, buf.begin, buf.realSize));
                                 } else {
                                     st = new File(cwd, new String(buf.bytes, buf.begin, buf.realSize));
                                 }
                                 if(st.isDirectory() && !".".equals(dirp[i]) && !"..".equals(dirp[i])) {
                                     int t = buf.realSize;
                                     buf.append(SLASH);
                                     buf.append(DOUBLE_STAR);
                                     buf.append(bytes, m, end - m);
                                     status = glob_helper(cwd, buf.bytes, buf.begin, buf.realSize, t, flags, func, arg);
                                     if(status != 0) {
                                         break;
                                     }
                                 }
                                 continue;
                             }
                             byte[] bs = dirp[i].getBytes();
                             if(fnmatch(magic,0,magic.length,bs,0, bs.length,flags) == 0) {
                                 buf.length(0);
                                 buf.append(base);
                                 buf.append( BASE(base) ? SLASH : EMPTY );
                                 buf.append(dirp[i].getBytes());
                                 if(m == -1) {
                                     status = func.call(buf.bytes,0,buf.realSize,arg);
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
                                 if((!name.startsWith("META-INF") && (ix == -1 || ix == name.length()-1))) {
                                     if("/".equals(jar) || (name.startsWith(jar) && name.length()>jar.length())) {
                                         dirp.add(je);
                                     }
                                 }
                             }
                             for(JarEntry je : dirp) {
                                 byte[] bs = je.getName().getBytes();
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
                                         int t = buf.realSize;
                                         buf.append(SLASH);
                                         buf.append(DOUBLE_STAR);
                                         buf.append(bytes, m, end - m);
                                         status = glob_helper(cwd, buf.bytes, buf.begin, buf.realSize, t, flags, func, arg);
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
                                         status = func.call(buf.bytes,0,buf.realSize,arg);
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
                             if(b.bytes[0] == '/'  || (DOSISH && 2<b.realSize && b.bytes[1] == ':' && isdirsep(b.bytes[2]))) {
                                 st = new File(new String(b.bytes, 0, b.realSize));
                             } else {
                                 st = new File(cwd, new String(b.bytes, 0, b.realSize));
                             }
 
                             if(st.isDirectory()) {
                                 int len = b.realSize;
                                 buf.length(0);
                                 buf.append(b);
                                 buf.append(bytes, m, end - m);
                                 status = glob_helper(cwd,buf.bytes,0,buf.realSize,len,flags,func,arg);
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
 }
diff --git a/test/test_file.rb b/test/test_file.rb
index 6fb712d27c..d79b80f7db 100644
--- a/test/test_file.rb
+++ b/test/test_file.rb
@@ -1,369 +1,372 @@
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
 
       assert_equal("file:/foo/bar", File.expand_path("file:/foo/bar"))
       assert_equal("file:/bar", File.expand_path("file:/foo/../bar"))
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
+    
+    # JRUBY-1986, make sure that fnmatch is sharing aware
+    assert_equal(true, File.fnmatch("foobar"[/foo(.*)/, 1], "bar"))
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
     assert(File.exist?("file:" + File.expand_path("test/dir with spaces/test_jar.jar") + "!/abc/foo.rb"))
     assert(File.exist?("file:" + File.expand_path("test/dir with spaces/test_jar.jar") + "!/inside_jar.rb"))
     assert(!File.exist?("file:" + File.expand_path("test/dir with spaces/test_jar.jar") + "!/inside_jar2.rb"))
     assert(!File.exist?("file:" + File.expand_path("test/dir with spaces/test_jar.jar") + "!/"))
   end
 
   def test_file_read_in_jar_file
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
   
   def test_truncate_doesnt_create_file
     name = "___foo_bar___"
     assert(!File.exists?(name))
 
     assert_raises(Errno::ENOENT) { File.truncate(name, 100) }
 
     assert(!File.exists?(name))
   end
 end
 
