diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 7b95482d3c..b785882218 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1299 +1,1316 @@
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
         
-        // TODO Singleton methods: readlink
-        
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
         if (recv.getRuntime().getPosix().symlink(from.toString(),to.toString()) == -1) {
             // FIXME: When we get JNA3 we need to properly write this to errno.
             recv.getRuntime().newSystemCallError("bad symlink");
         }
         
         return recv.getRuntime().newFixnum(0);
     }
+    
+    @JRubyMethod(required = 1, meta = true)
+    public static IRubyObject readlink(IRubyObject recv, IRubyObject path) {
+        String realPath = recv.getRuntime().getPosix().readlink(path.toString());
+        
+        if (!RubyFileTest.exist_p(recv, path).isTrue()) {
+            throw recv.getRuntime().newErrnoENOENTError("No such file or directory - " + path);
+        }
+        
+        if (!RubyFileTest.symlink_p(recv, path).isTrue()) {
+            throw recv.getRuntime().newErrnoEINVALError("invalid argument - " + path);
+        }
+        
+        if (realPath == null) {
+            // FIXME: When we get JNA3 we need to properly write this to errno.
+        }
+
+        return recv.getRuntime().newString(realPath);
+    }
 
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
diff --git a/src/org/jruby/RubyYAML.java b/src/org/jruby/RubyYAML.java
index d780f423d9..126e48b997 100644
--- a/src/org/jruby/RubyYAML.java
+++ b/src/org/jruby/RubyYAML.java
@@ -1,654 +1,659 @@
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
  * Copyright (C) 2007 Ola Bini <ola.bini@gmail.com>
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
 
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import java.util.regex.Pattern;
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.javasupport.JavaEmbedUtils;
 
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.Visibility;
 
 import org.jruby.yaml.JRubyRepresenter;
 import org.jruby.yaml.JRubyConstructor;
 import org.jruby.yaml.JRubySerializer;
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 
 import org.jvyamlb.Representer;
 import org.jvyamlb.Constructor;
 import org.jvyamlb.ParserImpl;
 import org.jvyamlb.Scanner;
 import org.jvyamlb.ScannerImpl;
 import org.jvyamlb.ComposerImpl;
 import org.jvyamlb.Serializer;
 import org.jvyamlb.ResolverImpl;
 import org.jvyamlb.EmitterImpl;
 import org.jvyamlb.YAMLException;
 import org.jvyamlb.YAMLConfig;
 import org.jvyamlb.YAML;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class RubyYAML {
     public static RubyModule createYAMLModule(Ruby runtime) {
         runtime.getKernel().callMethod(runtime.getCurrentContext(),"require", runtime.newString("date"));
         RubyModule result = runtime.defineModule("YAML");
 
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyYAML.class);
 
         result.defineAnnotatedMethods(RubyYAML.class);
 
         RubyClass obj = runtime.getObject();
         RubyClass clazz = runtime.getClassClass();
         RubyClass hash = runtime.getHash();
         RubyClass array = runtime.getArray();
         RubyClass struct = runtime.getStructClass();
         RubyClass exception = runtime.getException();
         RubyClass string = runtime.getString();
         RubyClass symbol = runtime.getSymbol();
         RubyClass range = runtime.getRange();
         RubyClass regexp = runtime.getRegexp();
         RubyClass time = runtime.getTime();
         RubyClass date = runtime.fastGetClass("Date"); 
         RubyClass fixnum = runtime.getFixnum(); 
         RubyClass bignum = runtime.getBignum(); 
         RubyClass flt = runtime.getFloat(); 
         RubyClass trueClass = runtime.getTrueClass(); 
         RubyClass falseClass = runtime.getFalseClass(); 
         RubyClass nilClass = runtime.getNilClass(); 
 
         clazz.defineAnnotatedMethods(YAMLClassMethods.class);
         
         obj.defineAnnotatedMethods(YAMLObjectMethods.class);
         
         hash.defineAnnotatedMethods(YAMLHashMethods.class);
 
         array.defineAnnotatedMethods(YAMLArrayMethods.class);
 
         struct.defineAnnotatedMethods(YAMLStructMethods.class);
 
         exception.defineAnnotatedMethods(YAMLExceptionMethods.class);
 
         string.defineAnnotatedMethods(YAMLStringMethods.class);
 
         symbol.defineAnnotatedMethods(YAMLSymbolMethods.class);
 
         range.defineAnnotatedMethods(YAMLRangeMethods.class);
 
         regexp.defineAnnotatedMethods(YAMLRegexpMethods.class);
 
         time.defineAnnotatedMethods(YAMLTimeMethods.class);
 
         date.defineAnnotatedMethods(YAMLDateMethods.class);
 
         bignum.defineAnnotatedMethods(YAMLNumericMethods.class);
         bignum.defineAnnotatedMethods(YAMLFixnumMethods.class);
 
         fixnum.defineAnnotatedMethods(YAMLNumericMethods.class);
         fixnum.defineAnnotatedMethods(YAMLFixnumMethods.class);
 
         flt.defineAnnotatedMethods(YAMLNumericMethods.class);
         flt.defineAnnotatedMethods(YAMLFloatMethods.class);
 
         trueClass.defineAnnotatedMethods(YAMLTrueMethods.class);
 
         falseClass.defineAnnotatedMethods(YAMLFalseMethods.class);
 
         nilClass.defineAnnotatedMethods(YAMLNilMethods.class);
 
         return result;
     }
 
     @JRubyMethod(name = "dump", required = 1, optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject dump(IRubyObject self, IRubyObject[] args) {
         IRubyObject obj = args[0];
         IRubyObject val = self.getRuntime().newArray(obj);
         if(args.length>1) {
             return self.callMethod(self.getRuntime().getCurrentContext(),"dump_all", new IRubyObject[]{val,args[1]});
         } else {
             return self.callMethod(self.getRuntime().getCurrentContext(),"dump_all", val);
         }
     }
 
     @JRubyMethod(name = "dump_all", required = 1, optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject dump_all(IRubyObject self, IRubyObject[] args) {
         ThreadContext context = self.getRuntime().getCurrentContext();
         RubyArray objs = (RubyArray)args[0];
         IRubyObject io = null;
         IRubyObject io2 = null;
         if(args.length == 2 && args[1] != null && !args[1].isNil()) {
             io = args[1];
         }
         YAMLConfig cfg = YAML.config().version("1.0");
         IOOutputStream iox = null;
         if(null == io) {
             self.getRuntime().getKernel().callMethod(context,"require", self.getRuntime().newString("stringio"));
             io2 = self.getRuntime().fastGetClass("StringIO").callMethod(context, "new");
             iox = new IOOutputStream(io2);
         } else {
             iox = new IOOutputStream(io);
         }
         Serializer ser = new JRubySerializer(new EmitterImpl(iox,cfg),new ResolverImpl(),cfg);
         try {
             ser.open();
             Representer r = new JRubyRepresenter(ser, cfg);
             for(Iterator iter = objs.getList().iterator();iter.hasNext();) {
                 r.represent(iter.next());
             }
             ser.close();
         } catch(IOException e) {
             throw self.getRuntime().newIOErrorFromException(e);
         }
         if(null == io) {
             io2.callMethod(context, "rewind");
             return io2.callMethod(context, "read");
         } else {
             return io;
         }
     }
 
     @JRubyMethod(name = "load", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject load(IRubyObject self, IRubyObject arg) {
         IRubyObject io = check_yaml_port(arg);
         Scanner scn = null;
         try {
             if(io instanceof RubyString) {
                 scn = new ScannerImpl(((RubyString)io).getByteList());
             } else {
                 scn = new ScannerImpl(new IOInputStream(io));
             }
             Constructor ctor = new JRubyConstructor(self,new ComposerImpl(new ParserImpl(scn,YAML.config().version("1.0")),new ResolverImpl()));
             if(ctor.checkData()) {
                 return JavaEmbedUtils.javaToRuby(self.getRuntime(),ctor.getData());
             }
             return self.getRuntime().getNil();
         } catch(YAMLException e) {
             if(self.getRuntime().getDebug().isTrue()) e.printStackTrace();
             throw self.getRuntime().newArgumentError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = "load_file", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject load_file(IRubyObject self, IRubyObject arg) {
         ThreadContext context = self.getRuntime().getCurrentContext();
         IRubyObject io = self.getRuntime().getFile().callMethod(context,"open", new IRubyObject[]{arg,self.getRuntime().newString("r")});
         IRubyObject val = self.callMethod(context,"load", io);
         io.callMethod(context, "close");
         return val;
     }
 
     @JRubyMethod(name = "each_document", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject each_document(IRubyObject self, IRubyObject arg, Block block) {
         ThreadContext context = self.getRuntime().getCurrentContext();
         IRubyObject io = arg;
         Scanner scn = null;
         try {
             if(io instanceof RubyString) {
                 scn = new ScannerImpl(((RubyString)io).getByteList());
             } else {
                 scn = new ScannerImpl(new IOInputStream(io));
             }
             Constructor ctor = new JRubyConstructor(self,new ComposerImpl(new ParserImpl(scn,YAML.config().version("1.0")),new ResolverImpl()));
             while(ctor.checkData()) {
                 block.yield(context, JavaEmbedUtils.javaToRuby(self.getRuntime(),ctor.getData()));
             }
             return self.getRuntime().getNil();
         } catch(YAMLException e) {
             if(self.getRuntime().getDebug().isTrue()) e.printStackTrace();
             throw self.getRuntime().newArgumentError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = "load_documents", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject load_documents(IRubyObject self, IRubyObject arg, Block block) {
         ThreadContext context = self.getRuntime().getCurrentContext();
         IRubyObject io = check_yaml_port(arg);
         Scanner scn = null;
         try {
             if(io instanceof RubyString) {
                 scn = new ScannerImpl(((RubyString)io).getByteList());
             } else {
                 scn = new ScannerImpl(new IOInputStream(io));
             }
             Constructor ctor = new JRubyConstructor(self,new ComposerImpl(new ParserImpl(scn,YAML.config().version("1.0")),new ResolverImpl()));
             while(ctor.checkData()) {
                 block.yield(context, JavaEmbedUtils.javaToRuby(self.getRuntime(),ctor.getData()));
             }
             return self.getRuntime().getNil();
         } catch(YAMLException e) {
             if(self.getRuntime().getDebug().isTrue()) e.printStackTrace();
             throw self.getRuntime().newArgumentError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = "load_stream", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject load_stream(IRubyObject self, IRubyObject arg) {
         ThreadContext context = self.getRuntime().getCurrentContext();
         IRubyObject d = self.getRuntime().getNil();
         IRubyObject io = arg;
         Scanner scn = null;
         try {
             if(io instanceof RubyString) {
                 scn = new ScannerImpl(((RubyString)io).getByteList());
             } else {
                 scn = new ScannerImpl(new IOInputStream(io));
             }
             Constructor ctor = new JRubyConstructor(self,new ComposerImpl(new ParserImpl(scn,YAML.config().version("1.0")),new ResolverImpl()));
             while(ctor.checkData()) {
                 if(d.isNil()) {
                     d = self.getRuntime().fastGetModule("YAML").fastGetClass("Stream").callMethod(context,"new", d);
                 }
                 d.callMethod(context,"add", JavaEmbedUtils.javaToRuby(self.getRuntime(),ctor.getData()));
             }
             return d;
         } catch(YAMLException e) {
             if(self.getRuntime().getDebug().isTrue()) e.printStackTrace();
             throw self.getRuntime().newArgumentError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = "dump_stream", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject dump_stream(IRubyObject self, IRubyObject[] args) {
         ThreadContext context = self.getRuntime().getCurrentContext();
         IRubyObject stream = self.getRuntime().fastGetModule("YAML").fastGetClass("Stream").callMethod(context, "new");
         for(int i=0,j=args.length;i<j;i++) {
             stream.callMethod(context,"add", args[i]);
         }
         return stream.callMethod(context, "emit");
     }
 
     @JRubyMethod(name = "quick_emit_node", required = 1, rest = true, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject quick_emit_node(IRubyObject self, IRubyObject[] args, Block block) {
         return block.yield(self.getRuntime().getCurrentContext(), args[0]);
     }
 
     @JRubyMethod(name = "quick_emit_node", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject quick_emit(IRubyObject self, IRubyObject[] args) {
         return self.getRuntime().getNil();
     }
+    
+    @JRubyMethod(name = "tagurize", required = 1, module = true, visibility = Visibility.PRIVATE)
+    public static IRubyObject tagurize(IRubyObject self, IRubyObject arg) {
+        return arg.callMethod(self.getRuntime().getCurrentContext(), "taguri");
+    }
 
     // prepares IO port type for load (ported from ext/syck/rubyext.c)
     private static IRubyObject check_yaml_port(IRubyObject port) {
         if (port instanceof RubyString) {
             // OK
         }
         else if (port.respondsTo("read")) {
             if (port.respondsTo("binmode")) {
                 ThreadContext context = port.getRuntime().getCurrentContext();
                 port.callMethod(context, "binmode");
             }
         }
         else {
             throw port.getRuntime().newTypeError("instance of IO needed");
         }
         return port;
     }
 
     public static class YAMLHashMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject hash_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             return arg.callMethod(context,"map", new IRubyObject[]{self.callMethod(context, "taguri"),self,self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject hash_taguri(IRubyObject self) {
             String className = self.getType().getName();
             if("Hash".equals(className)) {
                 return self.getRuntime().newString("tag:yaml.org,2002:map");
             } else {
                 return self.getRuntime().newString("tag:yaml.org,2002:map:" + className);
             }
         }
     }
     
     public static class YAMLObjectMethods {
         @JRubyMethod(name = "to_yaml_properties")
         public static IRubyObject obj_to_yaml_properties(IRubyObject self) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             return self.callMethod(context, "instance_variables").callMethod(context, "sort");
         }
         @JRubyMethod(name = "to_yaml_style")
         public static IRubyObject obj_to_yaml_style(IRubyObject self) {
             return self.getRuntime().getNil();
         }
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject obj_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             Map mep = (Map)(new RubyHash(self.getRuntime()));
             RubyArray props = (RubyArray)self.callMethod(context, "to_yaml_properties");
             for(Iterator iter = props.getList().iterator(); iter.hasNext();) {
                 String m = iter.next().toString();
                 mep.put(self.getRuntime().newString(m.substring(1)), self.callMethod(context,"instance_variable_get", self.getRuntime().newString(m)));
             }
             return arg.callMethod(context,"map", new IRubyObject[]{self.callMethod(context, "taguri"),(IRubyObject)mep,self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "to_yaml", rest = true)
         public static IRubyObject obj_to_yaml(IRubyObject self, IRubyObject[] args) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             return self.getRuntime().fastGetModule("YAML").callMethod(context,"dump", self);
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject obj_taguri(IRubyObject self) {
             return self.getRuntime().newString("!ruby/object:" + self.getType().getName());
         }
     }
     
     public static class YAMLClassMethods {
         @JRubyMethod(name = "to_yaml", rest = true)
         public static IRubyObject class_to_yaml(IRubyObject self, IRubyObject[] args) {
             throw self.getRuntime().newTypeError("can't dump anonymous class " + self.getType().getName());
         }
     }
     
     public static class YAMLArrayMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject array_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             return arg.callMethod(context,"seq", new IRubyObject[]{self.callMethod(context, "taguri"),self,self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject array_taguri(IRubyObject self) {
             String className = self.getType().getName();
             if("Array".equals(className)) {
                 return self.getRuntime().newString("tag:yaml.org,2002:seq");
             } else {
                 return self.getRuntime().newString("tag:yaml.org,2002:seq:" + className);
             }
         }
     }
 
     public static class YAMLStructMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject struct_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             Map mep = (Map)(new RubyHash(self.getRuntime()));
             for(Iterator iter = ((RubyArray)self.callMethod(context, "members")).getList().iterator();iter.hasNext();) {
                 IRubyObject key = self.getRuntime().newString(iter.next().toString());
                 mep.put(key,self.callMethod(context,MethodIndex.AREF, "[]", key));
             }            
             for(Iterator iter = ((RubyArray)self.callMethod(context, "to_yaml_properties")).getList().iterator(); iter.hasNext();) {
                 String m = iter.next().toString();
                 mep.put(self.getRuntime().newString(m.substring(1)), self.callMethod(context,"instance_variable_get", self.getRuntime().newString(m)));
             }
             return arg.callMethod(context,"map", new IRubyObject[]{self.callMethod(context, "taguri"),(IRubyObject)mep,self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject struct_taguri(IRubyObject self) {
             return self.getRuntime().newString("!ruby/struct:" + self.getType().getName());
         }
     }
     
     public static class YAMLExceptionMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject exception_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             Map mep = (Map)(new RubyHash(self.getRuntime()));
             mep.put(self.getRuntime().newString("message"),self.callMethod(context, "message"));
             for(Iterator iter = ((RubyArray)self.callMethod(context, "to_yaml_properties")).getList().iterator(); iter.hasNext();) {
                 String m = iter.next().toString();
                 mep.put(self.getRuntime().newString(m.substring(1)), self.callMethod(context,"instance_variable_get", self.getRuntime().newString(m)));
             }
             return arg.callMethod(context,"map", new IRubyObject[]{self.callMethod(context, "taguri"),(IRubyObject)mep,self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject exception_taguri(IRubyObject self) {
             return self.getRuntime().newString("!ruby/exception:" + self.getType().getName());
         }
     }
     
     private static final Pattern AFTER_NEWLINE = Pattern.compile("\n.+", Pattern.DOTALL);
     public static class YAMLStringMethods {
         @JRubyMethod(name = "is_complex_yaml?")
         public static IRubyObject string_is_complex(IRubyObject self) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             return (self.callMethod(context, "to_yaml_style").isTrue() ||
                     ((List)self.callMethod(context, "to_yaml_properties")).isEmpty() ||
                     AFTER_NEWLINE.matcher(self.toString()).find()) ? self.getRuntime().getTrue() : self.getRuntime().getFalse();
         }
         @JRubyMethod(name = "is_binary_data?")
         public static IRubyObject string_is_binary(IRubyObject self) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             if(self.callMethod(context, MethodIndex.EMPTY_P, "empty?").isTrue()) {
                 return self.getRuntime().getNil();
             }
             return self.toString().indexOf('\0') != -1 ? self.getRuntime().getTrue() : self.getRuntime().getFalse();
         }
         private static org.jruby.yaml.JRubyRepresenter into(IRubyObject arg) {
             IRubyObject jobj = arg.getInstanceVariables().fastGetInstanceVariable("@java_object");
             if(jobj != null) {
                 return (org.jruby.yaml.JRubyRepresenter)(((org.jruby.javasupport.JavaObject)jobj).getValue());
             }
             return null;
         }
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject string_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             Ruby rt = self.getRuntime();
             if(self.callMethod(context, "is_binary_data?").isTrue()) {
                 return arg.callMethod(context,"scalar", new IRubyObject[]{rt.newString("tag:yaml.org,2002:binary"),rt.newArray(self).callMethod(context,"pack", rt.newString("m")),rt.newString("|")});
             }
             if(((List)self.callMethod(context, "to_yaml_properties")).isEmpty()) {
                 org.jruby.yaml.JRubyRepresenter rep = into(arg);
                 if(rep != null) {
                     try {
                         return org.jruby.javasupport.JavaUtil.convertJavaToRuby(rt,rep.scalar(self.callMethod(context, "taguri").toString(),self.convertToString().getByteList(),self.toString().startsWith(":") ? "\"" : self.callMethod(context, "to_yaml_style").toString()));
                     } catch(IOException e) {
                         throw rt.newIOErrorFromException(e);
                     }
                 } else {
                     return arg.callMethod(context,"scalar", new IRubyObject[]{self.callMethod(context, "taguri"),self,self.toString().startsWith(":") ? rt.newString("\"") : self.callMethod(context, "to_yaml_style")});
                 }
             }
 
             Map mep = (Map)(new RubyHash(self.getRuntime()));
             mep.put(self.getRuntime().newString("str"),rt.newString(self.toString()));
             for(Iterator iter = ((RubyArray)self.callMethod(context, "to_yaml_properties")).getList().iterator(); iter.hasNext();) {
                 String m = iter.next().toString();
                 mep.put(self.getRuntime().newString(m.substring(1)), self.callMethod(context,"instance_variable_get", self.getRuntime().newString(m)));
             }
             return arg.callMethod(context,"map", new IRubyObject[]{self.callMethod(context, "taguri"),(IRubyObject)mep,self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject string_taguri(IRubyObject self) {
             return self.getRuntime().newString("tag:yaml.org,2002:str");
         }
     }
     
     public static class YAMLSymbolMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject symbol_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             return arg.callMethod(context,"scalar", new IRubyObject[]{self.callMethod(context, "taguri"),self.callMethod(context, "inspect"),self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject symbol_taguri(IRubyObject self) {
             return self.getRuntime().newString("tag:yaml.org,2002:str");
         }
     }
     
     public static class YAMLNumericMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject numeric_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             String val = self.toString();
             if("Infinity".equals(val)) {
                 val = ".Inf";
             } else if("-Infinity".equals(val)) {
                 val = "-.Inf";
             } else if("NaN".equals(val)) {
                 val = ".NaN";
             }
             return arg.callMethod(context,"scalar", new IRubyObject[]{self.callMethod(context, "taguri"),self.getRuntime().newString(val),self.callMethod(context, "to_yaml_style")});
         }
     }
 
     public static class YAMLRangeMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject range_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             Map mep = (Map)(new RubyHash(self.getRuntime()));
             mep.put(self.getRuntime().newString("begin"),self.callMethod(context, "begin"));
             mep.put(self.getRuntime().newString("end"),self.callMethod(context, "end"));
             mep.put(self.getRuntime().newString("excl"),self.callMethod(context, "exclude_end?"));
             for(Iterator iter = ((RubyArray)self.callMethod(context, "to_yaml_properties")).getList().iterator(); iter.hasNext();) {
                 String m = iter.next().toString();
                 mep.put(self.getRuntime().newString(m.substring(1)), self.callMethod(context,"instance_variable_get", self.getRuntime().newString(m)));
             }
             return arg.callMethod(context,"map", new IRubyObject[]{self.callMethod(context, "taguri"),(IRubyObject)mep,self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject range_taguri(IRubyObject self) {
             return self.getRuntime().newString("!ruby/range");
         }
     }
     
     public static class YAMLRegexpMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject regexp_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             return arg.callMethod(context,"scalar", new IRubyObject[]{self.callMethod(context, "taguri"),self.callMethod(context, "inspect"),self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject regexp_taguri(IRubyObject self) {
             return self.getRuntime().newString("!ruby/regexp");
         }
     }
     
     public static class YAMLTimeMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject time_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             IRubyObject tz = self.getRuntime().newString("Z");
             IRubyObject difference_sign = self.getRuntime().newString("-");
             self = self.dup();
             if(!self.callMethod(context, "utc?").isTrue()) {
                 IRubyObject utc_same_instant = self.callMethod(context, "utc");
                 IRubyObject utc_same_writing = self.getRuntime().getTime().callMethod(context,"utc", new IRubyObject[]{
                         self.callMethod(context, "year"),self.callMethod(context, "month"),self.callMethod(context, "day"),self.callMethod(context, "hour"),
                         self.callMethod(context, "min"),self.callMethod(context, "sec"),self.callMethod(context, "usec")});
                 IRubyObject difference_to_utc = utc_same_writing.callMethod(context,MethodIndex.OP_MINUS, "-", utc_same_instant);
                 IRubyObject absolute_difference;
                 if(difference_to_utc.callMethod(context,MethodIndex.OP_LT, "<", RubyFixnum.zero(self.getRuntime())).isTrue()) {
                     difference_sign = self.getRuntime().newString("-");
                     absolute_difference = RubyFixnum.zero(self.getRuntime()).callMethod(context,MethodIndex.OP_MINUS, "-", difference_to_utc);
                 } else {
                     difference_sign = self.getRuntime().newString("+");
                     absolute_difference = difference_to_utc;
                 }
                 IRubyObject difference_minutes = absolute_difference.callMethod(context,"/", self.getRuntime().newFixnum(60)).callMethod(context, "round");
                 tz = self.getRuntime().newString("%s%02d:%02d").callMethod(context,"%", self.getRuntime().newArrayNoCopy(new IRubyObject[]{difference_sign,difference_minutes.callMethod(context,"/", self.getRuntime().newFixnum(60)),difference_minutes.callMethod(context,"%", self.getRuntime().newFixnum(60))}));
             }
             IRubyObject standard = self.callMethod(context,"strftime", self.getRuntime().newString("%Y-%m-%d %H:%M:%S"));
             if(self.callMethod(context, "usec").callMethod(context, "nonzero?").isTrue()) {
                 standard = standard.callMethod(context, MethodIndex.OP_PLUS, "+", self.getRuntime().newString(".%06d").callMethod(context,"%", self.getRuntime().newArray(self.callMethod(context, "usec"))));
             }
             standard = standard.callMethod(context,MethodIndex.OP_PLUS, "+", self.getRuntime().newString(" %s").callMethod(context,"%", self.getRuntime().newArray(tz)));
             return arg.callMethod(context,"scalar", new IRubyObject[]{self.callMethod(context, "taguri"),standard,self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject time_taguri(IRubyObject self) {
             return self.getRuntime().newString("tag:yaml.org,2002:timestamp");
         }
     }
     
     public static class YAMLDateMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject date_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             return arg.callMethod(context,"scalar", new IRubyObject[]{self.callMethod(context, "taguri"),self.callMethod(context, MethodIndex.TO_S, "to_s"),self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject date_taguri(IRubyObject self) {
             return self.getRuntime().newString("tag:yaml.org,2002:timestamp#ymd");
         }
     }
     
     public static class YAMLFixnumMethods {
         @JRubyMethod(name = "taguri")
         public static IRubyObject fixnum_taguri(IRubyObject self) {
             return self.getRuntime().newString("tag:yaml.org,2002:int");
         }
     }
     
     public static class YAMLFloatMethods {
         @JRubyMethod(name = "taguri")
         public static IRubyObject float_taguri(IRubyObject self) {
             return self.getRuntime().newString("tag:yaml.org,2002:float");
         }
     }
 
     public static class YAMLTrueMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject true_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             return arg.callMethod(context,"scalar", new IRubyObject[]{self.callMethod(context, "taguri"),self.callMethod(context, MethodIndex.TO_S, "to_s"),self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject true_taguri(IRubyObject self) {
             return self.getRuntime().newString("tag:yaml.org,2002:bool");
         }
     }
     
     public static class YAMLFalseMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject false_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             return arg.callMethod(context,"scalar", new IRubyObject[]{self.callMethod(context, "taguri"),self.callMethod(context, MethodIndex.TO_S, "to_s"),self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject false_taguri(IRubyObject self) {
             return self.getRuntime().newString("tag:yaml.org,2002:bool");
         }
     }
     
     public static class YAMLNilMethods {
         @JRubyMethod(name = "to_yaml_node", required = 1)
         public static IRubyObject nil_to_yaml_node(IRubyObject self, IRubyObject arg) {
             ThreadContext context = self.getRuntime().getCurrentContext();
             return arg.callMethod(context,"scalar", new IRubyObject[]{self.callMethod(context, "taguri"),self.getRuntime().newString(""),self.callMethod(context, "to_yaml_style")});
         }
         @JRubyMethod(name = "taguri")
         public static IRubyObject nil_taguri(IRubyObject self) {
             return self.getRuntime().newString("tag:yaml.org,2002:null");
         }
     }
 }// RubyYAML
diff --git a/src/org/jruby/ext/posix/BasePOSIX.java b/src/org/jruby/ext/posix/BasePOSIX.java
index 480b6ad1bb..34c2167d74 100644
--- a/src/org/jruby/ext/posix/BasePOSIX.java
+++ b/src/org/jruby/ext/posix/BasePOSIX.java
@@ -1,97 +1,113 @@
 package org.jruby.ext.posix;
 
+import java.nio.ByteBuffer;
+import java.nio.charset.Charset;
+import java.nio.charset.CharsetDecoder;
+
 public abstract class BasePOSIX implements POSIX {
     protected LibC libc;
     protected POSIXHandler handler;
     
     public BasePOSIX(LibC libc, POSIXHandler handler) {
         this.libc = libc;
         this.handler = handler;
     }
 
     public int chmod(String filename, int mode) {
         return libc.chmod(filename, mode);
     }
 
     public int chown(String filename, int user, int group) {
         return libc.chown(filename, user, group);
     }
 
     public int getegid() {
         return libc.getegid();
     }
 
     public int geteuid() {
         return libc.geteuid();
     }
 
     public int getgid() {
         return libc.getgid();
     }
 
     public int getpgid() {
         return libc.getpgid();
     }
 
     public int getpgrp() {
         return libc.getpgrp();
     }
 
     public int getpid() {
         return libc.getpid();
     }
 
     public int getppid() {
         return libc.getppid();
     }
 
     public int getuid() {
         return libc.getuid();
     }
 
     public int kill(int pid, int signal) {
         return libc.kill(pid, signal);
     }
 
     public int lchmod(String filename, int mode) {
         return libc.lchmod(filename, mode);
     }
 
     public int lchown(String filename, int user, int group) {
         return libc.lchown(filename, user, group);
     }
 
     public int link(String oldpath, String newpath) {
         return libc.link(oldpath, newpath);
     }
     
     public FileStat lstat(String path) {
         FileStat stat = allocateStat();
 
         if (libc.lstat(path, stat) < 0) handler.error(ERRORS.ENOENT, path);
         
         return stat;
     }
     
     public int mkdir(String path, int mode) {
         return libc.mkdir(path, mode);
     }
 
     public FileStat stat(String path) {
         FileStat stat = allocateStat(); 
 
         if (libc.stat(path, stat) < 0) handler.error(ERRORS.ENOENT, path);
         
         return stat;
     }
 
     public int symlink(String oldpath, String newpath) {
         return libc.symlink(oldpath, newpath);
     }
     
+    public String readlink(String oldpath) {
+        // TODO: this should not be hardcoded to 256 bytes
+        ByteBuffer buffer = ByteBuffer.allocateDirect(256);
+        int result = libc.readlink(oldpath, buffer, buffer.capacity());
+        
+        if (result == -1) return null;
+        
+        buffer.position(0);
+        buffer.limit(result);
+        return Charset.forName("ASCII").decode(buffer).toString();
+    }
+    
     public int umask(int mask) {
         return libc.umask(mask);
     }
     
     public abstract FileStat allocateStat();
 }
diff --git a/src/org/jruby/ext/posix/JavaLibC.java b/src/org/jruby/ext/posix/JavaLibC.java
index 9f44a443cd..9d9c026d5f 100644
--- a/src/org/jruby/ext/posix/JavaLibC.java
+++ b/src/org/jruby/ext/posix/JavaLibC.java
@@ -1,218 +1,241 @@
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
  * Copyright (C) 2007 
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
 package org.jruby.ext.posix;
 
 
+import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 
+import java.nio.ByteBuffer;
 import org.jruby.Ruby;
 import org.jruby.ext.posix.POSIX.ERRORS;
 import org.jruby.ext.posix.util.Chmod;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ShellLauncher;
 
 /**
  * This libc implementation is created one per runtime instance versus the others which
  * are expected to be one static instance for whole JVM.  Because of this it is no big
  * deal to make reference to a POSIXHandler directly.
  */
 public class JavaLibC implements LibC {
     private POSIXHandler handler;
     private Ruby runtime;
     
     public JavaLibC(POSIXHandler handler) {
         this.handler = handler;
         // FIXME: Remove this and runtime field when we make a JRuby-agnostic shell launcher
         this.runtime = ((JRubyPOSIXHandler) handler).runtime;
     }
 
     public int chmod(String filename, int mode) {
         return Chmod.chmod(new File(filename), Integer.toOctalString(mode));
     }
 
     public int chown(String filename, int user, int group) {
         int chownResult = -1;
         int chgrpResult = -1;
         try {
             if (user != -1) {
                 Process chown = Runtime.getRuntime().exec("chown " + user + " " + filename);
                 chownResult = chown.waitFor();
             }
             if (group != -1) {
                 Process chgrp = Runtime.getRuntime().exec("chgrp " + user + " " + filename);
                 chgrpResult = chgrp.waitFor();
             }
         } catch (IOException ioe) {
             // FIXME: ignore?
         } catch (InterruptedException ie) {
             // FIXME: ignore?
         }
         if (chownResult != -1 && chgrpResult != -1) return 0;
         return -1;
     }
 
     public int getegid() {
         handler.unimplementedError("getegid");
         
         return -1;
     }
     
     public int geteuid() {
         handler.unimplementedError("geteuid");
         
         return -1;
     }
 
     public int getgid() {
         handler.unimplementedError("getgid");
         
         return -1;
     }
 
     public int getpgid() {
         handler.unimplementedError("getpgid");
         
         return -1;
     }
 
     public int getpgrp() {
         handler.unimplementedError("getpgrp");
         
         return -1;
     }
 
     public int getpid() {
       return runtime.hashCode();
     }
 
     public int getppid() {
         handler.unimplementedError("getppid");
         
         return -1;
     }
 
     public int getuid() {
         handler.unimplementedError("getuid");
         
         return -1;
     }
 
     // FIXME: Can be implemented
     public int kill(int pid, int signal) {
         handler.unimplementedError("kill");
         
         return -1;
     }
 
     // FIXME: Can be implemented
     public int lchmod(String filename, int mode) {
         handler.unimplementedError("getuid");
         
         return -1;
     }
 
     // FIXME: Can be implemented
     public int lchown(String filename, int user, int group) {
         handler.unimplementedError("lchown");
         
         return -1;
     }
 
     public int link(String oldpath, String newpath) {
         try {
             return new ShellLauncher(runtime).runAndWait(new IRubyObject[] {
                     runtime.newString("ln"), 
                     runtime.newString(oldpath), runtime.newString(newpath)
             });
         } catch (Exception e) { // We tried and failed for some reason. Indicate error.
             return -1;
         }
     }
     
     public int lstat(String path, FileStat stat) {
         // FIXME: Bulletproof this or no?
         JavaFileStat jstat = (JavaFileStat) stat;
         
         jstat.setup(path);
 
         // TODO: Add error reporting for cases we can calculate: ENOTDIR, ENAMETOOLONG, ENOENT
         // EACCES, ELOOP, EFAULT, EIO
 
         return 0;
     }
     
     public int mkdir(String path, int mode) {
         File dir = new File(path);
         
         if (!dir.mkdir()) return -1;
 
         chmod(path, mode);
         
         return 0;
     }
     
     public int stat(String path, FileStat stat) {
         // FIXME: Bulletproof this or no?
         JavaFileStat jstat = (JavaFileStat) stat;
         
         try {
             File file = new File(path);
             
             if (!file.exists()) handler.error(ERRORS.ENOENT, path);
                 
             jstat.setup(file.getCanonicalPath());
         } catch (IOException e) {
             // TODO: Throw error when we have problems stat'ing canonicalizing
         }
 
         // TODO: Add error reporting for cases we can calculate: ENOTDIR, ENAMETOOLONG, ENOENT
         // EACCES, ELOOP, EFAULT, EIO
 
         return 0;
     }
  
     public int symlink(String oldpath, String newpath) {
         try {
             // Replace with non-JRuby-specific shell lancher
             return new ShellLauncher(runtime).runAndWait(new IRubyObject[] {
                     runtime.newString("ln"), runtime.newString("-s"), 
                     runtime.newString(oldpath), runtime.newString(newpath)
             });
         } catch (Exception e) { // We tried and failed for some reason. Indicate error.
             return -1;
         }
     }
+    
+    public int readlink(String oldpath, ByteBuffer buffer, int length) {
+        ByteArrayOutputStream baos = new ByteArrayOutputStream();
+        try {
+            // Replace with non-JRuby-specific shell lancher
+            ShellLauncher launcher = new ShellLauncher(runtime);
+            launcher.runAndWait(
+                    new IRubyObject[] {runtime.newString("readlink"), runtime.newString(oldpath)},
+                    baos);
+            
+            byte[] bytes = baos.toByteArray();
+            
+            if (bytes.length > length) return -1;
+            // trim off \n
+            buffer.put(bytes, 0, bytes.length - 1);
+            
+            return buffer.position();
+        } catch (Exception e) { // We tried and failed for some reason. Indicate error.
+            return -1;
+        }
+    }
 
     public int umask(int mask) {
         // TODO: We can possibly maintain an internal mask and try and apply it to individual
         // libc methods.  
         return 0;
     }
 }
diff --git a/src/org/jruby/ext/posix/LibC.java b/src/org/jruby/ext/posix/LibC.java
index 1efa6bd2c0..9e1ba6e858 100644
--- a/src/org/jruby/ext/posix/LibC.java
+++ b/src/org/jruby/ext/posix/LibC.java
@@ -1,54 +1,56 @@
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
  * Copyright (C) 2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 package org.jruby.ext.posix;
 
 import com.sun.jna.Library;
+import java.nio.ByteBuffer;
 
 public interface LibC extends Library {
     public int chmod(String filename, int mode);
     public int chown(String filename, int user, int group);
     public int getegid();
     public int geteuid();
     public int getgid();
     public int getpgid();
     public int getpgrp();
     public int getppid();
     public int getpid();
     public int getuid();
     public int kill(int pid, int signal);
     public int lchmod(String filename, int mode);
     public int lchown(String filename, int user, int group);
     public int link(String oldpath,String newpath);
     public int lstat(String path, FileStat stat);
     public int mkdir(String path, int mode);
     public int stat(String path, FileStat stat);
     public int symlink(String oldpath,String newpath);
+    public int readlink(String oldpath,ByteBuffer buffer,int len);
     public int umask(int mask);
 }
diff --git a/src/org/jruby/ext/posix/POSIX.java b/src/org/jruby/ext/posix/POSIX.java
index c2e5466a0c..95c1724c5c 100644
--- a/src/org/jruby/ext/posix/POSIX.java
+++ b/src/org/jruby/ext/posix/POSIX.java
@@ -1,28 +1,30 @@
 package org.jruby.ext.posix;
 
+
 public interface POSIX {
     // When we use JNA-3 we can get errno, Then these values should match proper machine values.
     // If by chance these are not the same values on all systems we will have to have concrete
     // impls provide these values and stop using an enum.
     public enum ERRORS { ENOENT, ENOTDIR, ENAMETOOLONG, EACCESS, ELOOP, EFAULT, EIO, EBADF };
 
     public int chmod(String filename, int mode);
     public int chown(String filename, int user, int group);
     public int getegid();
     public int geteuid();
     public int getgid();
     public int getpgid();
     public int getpgrp();
     public int getppid();
     public int getpid();
     public int getuid();
     public int kill(int pid, int signal);
     public int lchmod(String filename, int mode);
     public int lchown(String filename, int user, int group);
     public int link(String oldpath,String newpath);
     public FileStat lstat(String path);
     public int mkdir(String path, int mode);
     public FileStat stat(String path);
     public int symlink(String oldpath,String newpath);
+    public String readlink(String path);
     public int umask(int mask);
 }
