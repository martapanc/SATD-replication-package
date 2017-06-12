diff --git a/src/org/jruby/RubyDir.java b/src/org/jruby/RubyDir.java
index 482d75d805..23f5dd6d37 100644
--- a/src/org/jruby/RubyDir.java
+++ b/src/org/jruby/RubyDir.java
@@ -1,684 +1,684 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
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
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.ext.posix.util.Platform;
 
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.Dir;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.ByteList;
 import org.jruby.util.TypeConverter;
 
 /**
  * .The Ruby built-in class Dir.
  *
  * @author  jvoegele
  */
 @JRubyClass(name = "Dir", include = "Enumerable")
 public class RubyDir extends RubyObject {
     private RubyString path;       // What we passed to the constructor for method 'path'
     protected JRubyFile dir;
     private String[] snapshot;     // snapshot of contents of directory
     private int pos;               // current position in directory
     private boolean isOpen = true;
 
     public RubyDir(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     private static final ObjectAllocator DIR_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyDir(runtime, klass);
         }
     };
 
     public static RubyClass createDirClass(Ruby runtime) {
         RubyClass dirClass = runtime.defineClass("Dir", runtime.getObject(), DIR_ALLOCATOR);
         runtime.setDir(dirClass);
 
         dirClass.index = ClassIndex.DIR;
         dirClass.setReifiedClass(RubyDir.class);
 
         dirClass.includeModule(runtime.getEnumerable());
         dirClass.defineAnnotatedMethods(RubyDir.class);
 
         return dirClass;
     }
 
     private final void checkDir() {
         if (!isTaint() && getRuntime().getSafeLevel() >= 4)throw getRuntime().newSecurityError("Insecure: operation on untainted Dir");
 
         testFrozen("Dir");
 
         if (!isOpen) throw getRuntime().newIOError("closed directory");
     }
 
     /**
      * Creates a new <code>Dir</code>.  This method takes a snapshot of the
      * contents of the directory at creation time, so changes to the contents
      * of the directory will not be reflected during the lifetime of the
      * <code>Dir</code> object returned, so a new <code>Dir</code> instance
      * must be created to reflect changes to the underlying file system.
      */
     @JRubyMethod(name = "initialize", required = 1, frame = true)
     public IRubyObject initialize(IRubyObject _newPath, Block unusedBlock) {
         RubyString newPath = _newPath.convertToString();
         path = newPath;
         pos = 0;
 
         getRuntime().checkSafeString(newPath);
 
         String adjustedPath = RubyFile.adjustRootPathOnWindows(getRuntime(), newPath.toString(), null);
         checkDirIsTwoSlashesOnWindows(getRuntime(), adjustedPath);
 
         List<String> snapshotList = RubyDir.getEntries(getRuntime(), adjustedPath);
         snapshot = (String[]) snapshotList.toArray(new String[snapshotList.size()]);
 
         return this;
     }
 
 // ----- Ruby Class Methods ----------------------------------------------------
     
     private static List<ByteList> dirGlobs(String cwd, IRubyObject[] args, int flags) {
         List<ByteList> dirs = new ArrayList<ByteList>();
 
         for (int i = 0; i < args.length; i++) {
             ByteList globPattern = args[i].convertToString().getByteList();
             dirs.addAll(Dir.push_glob(cwd, globPattern, flags));
         }
 
         return dirs;
     }
 
     private static IRubyObject asRubyStringList(Ruby runtime, List<ByteList> dirs) {
         List<RubyString> allFiles = new ArrayList<RubyString>();
 
         for (ByteList dir : dirs) {
             allFiles.add(RubyString.newString(runtime, dir));
         }
 
         IRubyObject[] tempFileList = new IRubyObject[allFiles.size()];
         allFiles.toArray(tempFileList);
 
         return runtime.newArrayNoCopy(tempFileList);
     }
 
     private static String getCWD(Ruby runtime) {
         try {
             return new org.jruby.util.NormalizedFile(runtime.getCurrentDirectory()).getCanonicalPath();
         } catch (Exception e) {
             return runtime.getCurrentDirectory();
         }
     }
 
     @JRubyMethod(name = "[]", required = 1, rest = true, meta = true)
     public static IRubyObject aref(IRubyObject recv, IRubyObject[] args) {
         List<ByteList> dirs;
         if (args.length == 1) {
             ByteList globPattern = args[0].convertToString().getByteList();
             dirs = Dir.push_glob(getCWD(recv.getRuntime()), globPattern, 0);
         } else {
             dirs = dirGlobs(getCWD(recv.getRuntime()), args, 0);
         }
 
         return asRubyStringList(recv.getRuntime(), dirs);
     }
 
     /**
      * Returns an array of filenames matching the specified wildcard pattern
      * <code>pat</code>. If a block is given, the array is iterated internally
      * with each filename is passed to the block in turn. In this case, Nil is
      * returned.  
      */
     @JRubyMethod(name = "glob", required = 1, optional = 1, frame = true, meta = true)
     public static IRubyObject glob(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         int flags = args.length == 2 ? RubyNumeric.num2int(args[1]) : 0;
 
         List<ByteList> dirs;
         IRubyObject tmp = args[0].checkArrayType();
         if (tmp.isNil()) {
             ByteList globPattern = args[0].convertToString().getByteList();
             dirs = Dir.push_glob(runtime.getCurrentDirectory(), globPattern, flags);
         } else {
             dirs = dirGlobs(getCWD(runtime), ((RubyArray) tmp).toJavaArray(), flags);
         }
 
         if (block.isGiven()) {
             for (int i = 0; i < dirs.size(); i++) {
                 block.yield(context, RubyString.newString(runtime, dirs.get(i)));
             }
 
             return runtime.getNil();
         }
 
         return asRubyStringList(runtime, dirs);
     }
 
     /**
      * @return all entries for this Dir
      */
     @JRubyMethod(name = "entries")
     public RubyArray entries() {
         return getRuntime().newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(getRuntime(), snapshot));
     }
 
     /**
      * Returns an array containing all of the filenames in the given directory.
      */
     @JRubyMethod(name = "entries", required = 1, meta = true, compat = CompatVersion.RUBY1_8)
     public static RubyArray entries(IRubyObject recv, IRubyObject path) {
-        return entriesCommon(recv.getRuntime(), path.convertToString().toString());
+        return entriesCommon(recv.getRuntime(), path.convertToString().getUnicodeValue());
     }
 
     @JRubyMethod(name = "entries", required = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static RubyArray entries19(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return entriesCommon(context.getRuntime(), getPath19(context, arg));
     }
 
     private static RubyArray entriesCommon(Ruby runtime, String path) {
         String adjustedPath = RubyFile.adjustRootPathOnWindows(runtime, path, null);
         checkDirIsTwoSlashesOnWindows(runtime, adjustedPath);
 
         Object[] files = getEntries(runtime, adjustedPath).toArray();
         return runtime.newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(runtime, files));
     }
 
     private static List<String> getEntries(Ruby runtime, String path) {
         if (!RubyFileTest.directory_p(runtime, RubyString.newString(runtime, path)).isTrue()) {
             throw runtime.newErrnoENOENTError("No such directory");
         }
 
         if (path.startsWith("file:")) return entriesIntoAJarFile(runtime, path);
 
         return entriesIntoADirectory(runtime, path);
     }
 
     private static List<String> entriesIntoADirectory(Ruby runtime, String path) {
         final JRubyFile directory = JRubyFile.create(runtime.getCurrentDirectory(), path);
 
         List<String> fileList = getContents(directory);
         fileList.add(0, ".");
         fileList.add(1, "..");
         return fileList;
     }
 
     private static List<String> entriesIntoAJarFile(Ruby runtime, String path) {
         List<ByteList> dirs = Dir.push_glob(runtime.getCurrentDirectory(),
                 RubyString.newString(runtime, path + "/*").getByteList(), Dir.FNM_DOTMATCH);
 
         List<String> fileList = new ArrayList<String>();
         for (ByteList file : dirs) {
             String[] split = file.toString().split("/");
             fileList.add(split[split.length - 1]);
         }
 
         return fileList;
     }
 
     // MRI behavior: just plain '//' or '\\\\' are considered illegal on Windows.
     private static void checkDirIsTwoSlashesOnWindows(Ruby runtime, String path) {
         if (Platform.IS_WINDOWS && ("//".equals(path) || "\\\\".equals(path))) {
             throw runtime.newErrnoEINVALError("Invalid argument - " + path);
         }
     }
 
     /** Changes the current directory to <code>path</code> */
     @JRubyMethod(name = "chdir", optional = 1, frame = true, meta = true)
     public static IRubyObject chdir(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         RubyString path = args.length == 1 ?
             RubyFile.get_path(context, args[0]) : getHomeDirectoryPath(context);
         String adjustedPath = RubyFile.adjustRootPathOnWindows(runtime, path.getUnicodeValue(), null);
         checkDirIsTwoSlashesOnWindows(runtime, adjustedPath);
         JRubyFile dir = getDir(runtime, adjustedPath, true);
         String realPath = null;
         String oldCwd = runtime.getCurrentDirectory();
 
         // We get canonical path to try and flatten the path out.
         // a dir '/subdir/..' should return as '/'
         // cnutter: Do we want to flatten path out?
         try {
             realPath = dir.getCanonicalPath();
         } catch (IOException e) {
             realPath = dir.getAbsolutePath();
         }
 
         IRubyObject result = null;
         if (block.isGiven()) {
             // FIXME: Don't allow multiple threads to do this at once
             runtime.setCurrentDirectory(realPath);
             try {
                 result = block.yield(context, path);
             } finally {
                 dir = getDir(runtime, oldCwd, true);
                 runtime.setCurrentDirectory(oldCwd);
             }
         } else {
             runtime.setCurrentDirectory(realPath);
             result = runtime.newFixnum(0);
         }
 
         return result;
     }
 
     /**
      * Changes the root directory (only allowed by super user).  Not available
      * on all platforms.
      */
     @JRubyMethod(name = "chroot", required = 1, meta = true)
     public static IRubyObject chroot(IRubyObject recv, IRubyObject path) {
         throw recv.getRuntime().newNotImplementedError("chroot not implemented: chroot is non-portable and is not supported.");
     }
 
     /**
      * Deletes the directory specified by <code>path</code>.  The directory must
      * be empty.
      */
     @JRubyMethod(name = {"rmdir", "unlink", "delete"}, required = 1, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject rmdir(IRubyObject recv, IRubyObject path) {
         return rmdirCommon(recv.getRuntime(), path.convertToString().getUnicodeValue());
     }
 
     @JRubyMethod(name = {"rmdir", "unlink", "delete"}, required = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject rmdir19(ThreadContext context, IRubyObject recv, IRubyObject path) {
         return rmdirCommon(context.getRuntime(), getPath19(context, path));
     }
 
     private static IRubyObject rmdirCommon(Ruby runtime, String path) {
         JRubyFile directory = getDir(runtime, path, true);
 
         if (!directory.delete()) {
             throw runtime.newSystemCallError("No such directory");
         }
 
         return runtime.newFixnum(0);
     }
 
     /**
      * Executes the block once for each file in the directory specified by
      * <code>path</code>.
      */
     @JRubyMethod(name = "foreach", frame = true, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject foreach(ThreadContext context, IRubyObject recv, IRubyObject _path, Block block) {
         RubyString pathString = _path.convertToString();
 
         return foreachCommon(context, recv, context.getRuntime(), pathString, block);
     }
 
     @JRubyMethod(name = "foreach", frame = true, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject foreach19(ThreadContext context, IRubyObject recv, IRubyObject arg, Block block) {
         RubyString pathString = arg instanceof RubyString ? (RubyString) arg : arg.callMethod(context, "to_path").convertToString();
 
         return foreachCommon(context, recv, context.getRuntime(), pathString, block);
     }
 
     private static IRubyObject foreachCommon(ThreadContext context, IRubyObject recv, Ruby runtime, RubyString _path, Block block) {
         if (block.isGiven()) {
             runtime.checkSafeString(_path);
 
             RubyClass dirClass = runtime.getDir();
             RubyDir dir = (RubyDir) dirClass.newInstance(context, new IRubyObject[]{_path}, block);
 
             dir.each(context, block);
             return runtime.getNil();
         }
 
         return enumeratorize(runtime, recv, "foreach", _path);
     }
 
     /** Returns the current directory. */
     @JRubyMethod(name = {"getwd", "pwd"}, meta = true)
     public static RubyString getwd(IRubyObject recv) {
         Ruby ruby = recv.getRuntime();
 
         return RubyString.newUnicodeString(ruby, getCWD(ruby));
     }
 
     /**
      * Returns the home directory of the current user or the named user if given.
      */
     @JRubyMethod(name = "home", optional = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject home(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length > 0) return getHomeDirectoryPath(context, args[0].toString());
 
         return getHomeDirectoryPath(context);
     }
 
     /**
      * Creates the directory specified by <code>path</code>.  Note that the
      * <code>mode</code> parameter is provided only to support existing Ruby
      * code, and is ignored.
      */
     @JRubyMethod(name = "mkdir", required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject mkdir(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         RubyString stringArg = args[0].convertToString();
         runtime.checkSafeString(stringArg);
 
         return mkdirCommon(runtime, stringArg.getUnicodeValue(), args);
     }
 
     @JRubyMethod(name = "mkdir", required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject mkdir19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return mkdirCommon(context.getRuntime(), getPath19(context, args[0]), args);
     }
 
     private static IRubyObject mkdirCommon(Ruby runtime, String path, IRubyObject[] args) {
         File newDir = getDir(runtime, path, false);
 
         if (File.separatorChar == '\\') newDir = new File(newDir.getPath());
 
         int mode = args.length == 2 ? ((int) args[1].convertToInteger().getLongValue()) : 0777;
 
         if (runtime.getPosix().mkdir(newDir.getAbsolutePath(), mode) < 0) {
             // FIXME: This is a system error based on errno
             throw runtime.newSystemCallError("mkdir failed");
         }
 
         return RubyFixnum.zero(runtime);
     }
 
     /**
      * Returns a new directory object for <code>path</code>.  If a block is
      * provided, a new directory object is passed to the block, which closes the
      * directory object before terminating.
      */
     @JRubyMethod(name = "open", required = 1, frame = true, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject path, Block block) {
         RubyDir directory = (RubyDir) context.getRuntime().getDir().newInstance(context,
                 new IRubyObject[]{path}, Block.NULL_BLOCK);
 
         if (!block.isGiven())return directory;
 
         try {
             return block.yield(context, directory);
         } finally {
             directory.close();
         }
     }
 
     @JRubyMethod(name = "open", required = 1, frame = true, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject open19(ThreadContext context, IRubyObject recv, IRubyObject path, Block block) {
         RubyString pathString = path instanceof RubyString ? (RubyString) path : path.callMethod(context, "to_path").convertToString();
         return open(context, recv, pathString, block);
     }
 
 // ----- Ruby Instance Methods -------------------------------------------------
     /**
      * Closes the directory stream.
      */
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         // Make sure any read()s after close fail.
         checkDir();
 
         isOpen = false;
 
         return getRuntime().getNil();
     }
 
     /**
      * Executes the block once for each entry in the directory.
      */
     public IRubyObject each(ThreadContext context, Block block) {
         checkDir();
 
         String[] contents = snapshot;
         for (pos = 0; pos < contents.length; pos++) {
             block.yield(context, getRuntime().newString(contents[pos]));
         }
 
         return this;
     }
 
     @JRubyMethod(name = "each", frame = true)
     public IRubyObject each19(ThreadContext context, Block block) {
         return block.isGiven() ? each(context, block) : enumeratorize(context.getRuntime(), this, "each");
     }
 
     @Override
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         StringBuilder part = new StringBuilder();
         String cname = getMetaClass().getRealClass().getName();
         part.append("#<").append(cname).append(":").append(path.asJavaString()).append(">");
 
         return runtime.newString(part.toString());
     }
 
     /**
      * Returns the current position in the directory.
      */
     @JRubyMethod(name = {"tell", "pos"})
     public RubyInteger tell() {
         checkDir();
         return getRuntime().newFixnum(pos);
     }
 
     /**
      * Moves to a position <code>d</code>.  <code>pos</code> must be a value
      * returned by <code>tell</code> or 0.
      */
     @JRubyMethod(name = "seek", required = 1)
     public IRubyObject seek(IRubyObject newPos) {
         checkDir();
 
         set_pos(newPos);
         return this;
     }
 
     @JRubyMethod(name = "pos=", required = 1)
     public IRubyObject set_pos(IRubyObject newPos) {
         this.pos = RubyNumeric.fix2int(newPos);
         return newPos;
     }
 
     @JRubyMethod(name = "path")
     public IRubyObject path(ThreadContext context) {
         return path.strDup(context.getRuntime());
     }
 
     /** Returns the next entry from this directory. */
     @JRubyMethod(name = "read")
     public IRubyObject read() {
         checkDir();
 
         if (pos >= snapshot.length) return getRuntime().getNil();
 
         RubyString result = getRuntime().newString(snapshot[pos]);
         pos++;
         return result;
     }
 
     /** Moves position in this directory to the first entry. */
     @JRubyMethod(name = "rewind")
     public IRubyObject rewind() {
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) throw getRuntime().newSecurityError("Insecure: can't close");
         checkDir();
 
         pos = 0;
         return this;
     }
 
     @JRubyMethod(name = {"exists?", "exist?"}, meta = true, frame = true, required = 1, compat = CompatVersion.RUBY1_9)
     public static IRubyObject exist(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         try {
             return context.getRuntime().newFileStat(getPath19(context, arg), false).directory_p();
         } catch (Exception e) {
             return context.getRuntime().newBoolean(false);
         }
     }
 
 // ----- Helper Methods --------------------------------------------------------
     protected static String getPath19(ThreadContext context, IRubyObject arg) {
         RubyString pathObject = arg instanceof RubyString ? (RubyString) arg : arg.callMethod(context, "to_path").convertToString();
         return pathObject.getUnicodeValue();
     }
 
     /** Returns a Java <code>File</code> object for the specified path.  If
      * <code>path</code> is not a directory, throws <code>IOError</code>.
      *
      * @param   path path for which to return the <code>File</code> object.
      * @param   mustExist is true the directory must exist.  If false it must not.
      * @throws  IOError if <code>path</code> is not a directory.
      */
     protected static JRubyFile getDir(final Ruby runtime, final String path, final boolean mustExist) {
         JRubyFile result = JRubyFile.create(runtime.getCurrentDirectory(), path);
         if (mustExist && !result.exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + path);
         }
         boolean isDirectory = result.isDirectory();
 
         if (path.startsWith("file:") || (mustExist && !isDirectory)) {
             throw runtime.newErrnoENOTDIRError(path + " is not a directory");
         } else if (!mustExist && isDirectory) {
             throw runtime.newErrnoEEXISTError("File exists - " + path);
         }
 
         return result;
     }
 
     /**
      * Returns the contents of the specified <code>directory</code> as an
      * <code>ArrayList</code> containing the names of the files as Java Strings.
      */
     protected static List<String> getContents(File directory) {
         String[] contents = directory.list();
         List<String> result = new ArrayList<String>();
 
         // If an IO exception occurs (something odd, but possible)
         // A directory may return null.
         if (contents != null) {
             for (int i = 0; i < contents.length; i++) {
                 result.add(contents[i]);
             }
         }
         return result;
     }
 
     /**
      * Returns the contents of the specified <code>directory</code> as an
      * <code>ArrayList</code> containing the names of the files as Ruby Strings.
      */
     protected static List<RubyString> getContents(File directory, Ruby runtime) {
         List<RubyString> result = new ArrayList<RubyString>();
         String[] contents = directory.list();
 
         for (int i = 0; i < contents.length; i++) {
             result.add(runtime.newString(contents[i]));
         }
         return result;
     }
 
     /**
      * Returns the home directory of the specified <code>user</code> on the
      * system. If the home directory of the specified user cannot be found,
      * an <code>ArgumentError it thrown</code>.
      */
     public static IRubyObject getHomeDirectoryPath(ThreadContext context, String user) {
         /*
          * TODO: This version is better than the hackish previous one. Windows
          *       behavior needs to be defined though. I suppose this version
          *       could be improved more too.
          * TODO: /etc/passwd is also inadequate for MacOSX since it does not
          *       use /etc/passwd for regular user accounts
          */
         Ruby runtime = context.getRuntime();
 
         try {
             // try to use POSIX for this first
             return runtime.newString(runtime.getPosix().getpwnam(user).getHome());
         } catch (Exception e) {
             // otherwise fall back on the old way
             String passwd = null;
             try {
                 FileInputStream stream = new FileInputStream("/etc/passwd");
                 int totalBytes = stream.available();
                 byte[] bytes = new byte[totalBytes];
                 stream.read(bytes);
                 stream.close();
                 passwd = new String(bytes);
             } catch (IOException ioe) {
                 return runtime.getNil();
             }
 
             String[] rows = passwd.split("\n");
             int rowCount = rows.length;
             for (int i = 0; i < rowCount; i++) {
                 String[] fields = rows[i].split(":");
                 if (fields[0].equals(user)) {
                     return runtime.newString(fields[5]);
                 }
             }
         }
 
         throw runtime.newArgumentError("user " + user + " doesn't exist");
     }
 
     public static RubyString getHomeDirectoryPath(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         RubyHash systemHash = (RubyHash) runtime.getObject().fastGetConstant("ENV_JAVA");
         RubyHash envHash = (RubyHash) runtime.getObject().fastGetConstant("ENV");
         IRubyObject home = envHash.op_aref(context, runtime.newString("HOME"));
 
         if (home == null || home.isNil()) {
             home = systemHash.op_aref(context, runtime.newString("user.home"));
         }
 
         if (home == null || home.isNil()) {
             home = envHash.op_aref(context, runtime.newString("LOGDIR"));
         }
 
         if (home == null || home.isNil()) {
             throw runtime.newArgumentError("user.home/LOGDIR not set");
         }
 
         return (RubyString) home;
     }
 }
diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 2b059bfbdf..9686a0d2cc 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -705,1114 +705,1114 @@ public class RubyFile extends RubyIO implements EncodingCapable {
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
             if (obj instanceof RubyString) {
                 return (RubyString)obj;
             }
             
             if (obj.respondsTo("to_path")) {
                 obj = obj.callMethod(context, "to_path");
             }
         }
 
         return obj.convertToString();
     }
 
     /**
      * Get the fully-qualified JRubyFile object for the path, taking into
      * account the runtime's current directory.
      */
     public static JRubyFile file(IRubyObject pathOrFile) {
         Ruby runtime = pathOrFile.getRuntime();
 
         if (pathOrFile instanceof RubyFile) {
             return JRubyFile.create(runtime.getCurrentDirectory(), ((RubyFile) pathOrFile).getPath());
         } else {
             RubyString path = get_path(runtime.getCurrentContext(), pathOrFile);
             return JRubyFile.create(runtime.getCurrentDirectory(), path.getUnicodeValue());
         }
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
         return "RubyFile(" + path + ", " + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStream().getDescriptor()) + ")";
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
 
     @JRubyMethod(name = {"realdirpath"}, required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject realdirpath(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, false);
     }
 
     @JRubyMethod(name = {"realpath"}, required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject realpath(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject file = expandPathInternal(context, recv, args, false);
         if (!RubyFileTest.exist_p(recv, file).isTrue()) {
             throw context.getRuntime().newErrnoENOENTError(file.toString());
         }
         return file;
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
 
             if (!isAbsoluteWithFilePrefix) {
                 isAbsoluteWithFilePrefix = cwd.startsWith("file:");
             }
 
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
     @JRubyMethod(required = 2, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject truncate(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {        
         return truncateCommon(context, recv, arg1, arg2);
     }
 
     @JRubyMethod(name = "truncate", required = 2, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject truncate19(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         if (!(arg1 instanceof RubyString) && arg1.respondsTo("to_path")) {
             arg1 = arg1.callMethod(context, "to_path");
         }
         return truncateCommon(context, recv, arg1, arg2);
     }
 
     private static IRubyObject truncateCommon(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         RubyString filename = arg1.convertToString(); // TODO: SafeStringValue here
         Ruby runtime = context.getRuntime();
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
-                throw runtime.newErrnoENOENTError(filename.toString());
+                throw runtime.newErrnoENOENTError(filename.getUnicodeValue());
             }
 
             if (lToDelete.isDirectory() && !isSymlink) {
-                throw runtime.newErrnoEPERMError(filename.toString());
+                throw runtime.newErrnoEPERMError(filename.getUnicodeValue());
             }
 
             if (!lToDelete.delete()) {
-                throw runtime.newErrnoEACCESError(filename.toString());
+                throw runtime.newErrnoEACCESError(filename.getUnicodeValue());
             }
         }
         
         return runtime.newFixnum(args.length);
     }
 
     @JRubyMethod(name = "size", backtrace = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject size(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if ((openFile.getMode() & OpenFile.WRITABLE) != 0) {
             flush();
         }
 
         FileStat stat = runtime.getPosix().fstat(
                 getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor());
         if (stat == null) {
             throw runtime.newErrnoEACCESError(path);
         }
 
         return runtime.newFixnum(stat.st_size());
     }
 
     public static ZipEntry getFileEntry(ZipFile zf, String path) throws IOException {
         ZipEntry entry = zf.getEntry(path);
         if (entry == null) {
             // try canonicalizing the path to eliminate . and .. (JRUBY-4760, JRUBY-4879)
             String prefix = new File(".").getCanonicalPath();
             entry = zf.getEntry(new File(path).getCanonicalPath().substring(prefix.length() + 1).replaceAll("\\\\", "/"));
         }
         return entry;
     }
 
     public static ZipEntry getDirOrFileEntry(ZipFile zf, String path) throws IOException {
         ZipEntry entry = zf.getEntry(path + "/"); // first try as directory
         if (entry == null) {
             // try canonicalizing the path to eliminate . and .. (JRUBY-4760, JRUBY-4879)
             String prefix = new File(".").getCanonicalPath();
             entry = zf.getEntry(new File(path + "/").getCanonicalPath().substring(prefix.length() + 1).replaceAll("\\\\", "/"));
 
             if (entry == null) {
                 // try as file
                 entry = getFileEntry(zf, path);
             }
         }
         return entry;
     }
 
     /**
      * Joy of POSIX, only way to get the umask is to set the umask,
      * then set it back. That's unsafe in a threaded program. We
      * minimize but may not totally remove this race by caching the
      * obtained or previously set (see umask() above) umask and using
      * that as the initial set value which, cross fingers, is a
      * no-op. The cache access is then synchronized. TODO: Better?
      */
     private static int getUmaskSafe( Ruby runtime ) {
         synchronized (_umaskLock) {
             final int umask = runtime.getPosix().umask(_cachedUmask);
             if (_cachedUmask != umask ) {
                 runtime.getPosix().umask(umask);
                 _cachedUmask = umask;
             }
             return umask;
         }
     }
 
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
diff --git a/test/test_dir.rb b/test/test_dir.rb
index 700c348bcf..0ba6952965 100644
--- a/test/test_dir.rb
+++ b/test/test_dir.rb
@@ -1,376 +1,392 @@
 require 'test/unit'
 require 'rbconfig'
 
 class TestDir < Test::Unit::TestCase
   WINDOWS = Config::CONFIG['host_os'] =~ /Windows|mswin/
 
   def setup
     @save_dir = Dir.pwd
     1.upto(5) do |i|
       Dir["testDir_#{i}/*"].each do |f|
         File.unlink f rescue nil
       end
       Dir.delete("testDir_#{i}") rescue nil
     end
   end
 
   def teardown
     Dir.chdir(@save_dir)
     setup
   end
 
   def test_pwd_and_getwd_equivalent
     assert_equal(Dir.pwd, Dir.getwd)
   end
 
   def test_dir_enumerable
     Dir.mkdir("./testDir_1")
 
     d = Dir.new("./testDir_1")
     assert(d.kind_of?(Enumerable))
     assert_equal(['.', '..'], d.entries)
   end
 
   def test_dir_entries
     Dir.mkdir("./testDir_1")
     (1..2).each {|i|
       File.open("./testDir_1/file" + i.to_s, "w") {|f|
         f.write("hello")
       }
     }
 
     assert_equal(['.', '..', "file1", "file2"], Dir.entries('./testDir_1').sort)
     assert_equal(['.', '..', "file1", "file2"], Dir.new('./testDir_1').entries.sort)
     Dir.chdir("./testDir_1")
     assert_equal(['.', '..', "file1", "file2"], Dir.entries('.').sort)
     Dir.chdir("..")
 
     files = []
     Dir.foreach('./testDir_1') {|f| files << f }
     assert_equal(['.', '..', "file1", "file2"], files.sort)
   end
 
   def test_bogus_glob
     # Test unescaped special char that is meant to be used with another 
     # (i.e. bogus glob pattern)
     assert_equal([], Dir.glob("{"))
   end
 
   def test_glob_empty_string
     assert_equal([], Dir.glob(''))
     assert_equal([], Dir[''])
   end
 
   def test_glob_double_star
     # Test that glob expansion of ** works ok with non-patterns as path 
     # elements. This used to throw NPE.
     Dir.mkdir("testDir_2")
     open("testDir_2/testDir_tmp1", "w").close
     Dir.glob('./**/testDir_tmp1').each {|f| assert File.exist?(f) }
   end
 
   def test_glob_with_blocks
     Dir.mkdir("testDir_3")
     open("testDir_3/testDir_tmp1", "w").close
     vals = []
     glob_val = Dir.glob('**/*tmp1'){|f| vals << f}
     assert_equal(true, glob_val.nil?)
     assert_equal(1, vals.size)
     assert_equal(true, File.exists?(vals[0])) unless vals.empty?
   end
 
   def test_dir_dot_does_not_throw_exception
     # just makes sure this doesn't throw a Java exception
     Dir['.']
   end
 
   # JRUBY-2717
   def test_more_than_two_arguments_to_aref_does_not_throw_exception
     Dir['.','.','.','.']
   end
 
   def test_glob_on_shared_string
     Dir["blahtest/test_argf.rb"[4..-1]]
   end
 
   # http://jira.codehaus.org/browse/JRUBY-300
   def test_chdir_and_pwd
     java_test_classes = File.expand_path(File.dirname(__FILE__) + '/../build/classes/test')
     java_test_classes = File.expand_path(File.dirname(__FILE__) + '/..') unless File.exist?(java_test_classes)
     Dir.mkdir("testDir_4")
     Dir.chdir("testDir_4") do
       pwd = `ruby -e "puts Dir.pwd"`
       pwd.gsub! '\\', '/'
       assert_equal("testDir_4", pwd.split("/")[-1].strip)
 
       pwd = `jruby -e "puts Dir.pwd"`
       pwd.gsub! '\\', '/'
       assert_equal("testDir_4", pwd.split("/")[-1].strip)
 
       pwd = `java -cp "#{java_test_classes}" org.jruby.util.Pwd`
       pwd.gsub! '\\', '/'
       assert_equal("testDir_4", pwd.split("/")[-1].strip)
     end
     Dir.chdir("testDir_4")
     pwd = `java -cp "#{java_test_classes}" org.jruby.util.Pwd`
     pwd.gsub! '\\', '/'
     assert_equal("testDir_4", pwd.split("/")[-1].strip)
   end
 
   def test_glob_inside_jar_file
     require 'test/dir with spaces/test_jar.jar'
     require 'inside_jar'
 
     prefix = WINDOWS ? "file:/" : "file:"
     
     first = File.expand_path(File.join(File.dirname(__FILE__), '..'))
     
     jar_file = prefix + File.join(first, "test", "dir with spaces", "test_jar.jar") + "!"
 
     ["#{jar_file}/abc", "#{jar_file}/inside_jar.rb", "#{jar_file}/second_jar.rb"].each do |f|
       assert $__glob_value.include?(f)
     end
     ["#{jar_file}/abc", "#{jar_file}/abc/foo.rb", "#{jar_file}/inside_jar.rb", "#{jar_file}/second_jar.rb"].each do |f|
       assert $__glob_value2.include?(f)
     end
     assert_equal ["#{jar_file}/abc"], Dir["#{jar_file}/abc"]
   end
 
   # JRUBY-4177
   # FIXME: Excluded due to JRUBY-4082
   def xxx_test_mktmpdir
     require 'tmpdir'
     assert_nothing_raised do
       Dir.mktmpdir('xx') {}
     end
   end
 
+  # JRUBY-4983
+  def test_entries_unicode
+    Dir.mkdir("./testDir_1")
+    Dir.mkdir("./testDir_1/glück")
+
+    assert_nothing_raised { Dir.entries("./testDir_1/glück") }
+    require 'fileutils'
+    assert_nothing_raised do
+      FileUtils.cp_r("./testDir_1/glück", "./testDir_1/target")
+      FileUtils.rm_r("./testDir_1/glück")
+    end
+  ensure
+    Dir.unlink "./testDir_1/target" rescue nil
+    Dir.unlink "./testDir_1/glück" rescue nil
+  end
+
   if WINDOWS
     def test_chdir_slash_windows
       @orig_pwd = Dir.pwd
       def restore_cwd
         Dir.chdir(@orig_pwd)
       end
       slashes = ['/', '\\']
       slashes.each { |slash|
         current_drive_letter = Dir.pwd[0..2]
         Dir.chdir(slash)
         assert_equal(current_drive_letter, Dir.pwd, "slash - #{slash}")
         restore_cwd
 
         letters = ['C:/', 'D:/', 'E:/', 'F:/', 'C:\\', 'D:\\', 'E:\\']
         letters.each { |letter|
           next unless File.exists?(letter)
           Dir.chdir(letter)
           pwd = Dir.pwd
           Dir.chdir(slash)
           slash_pwd = Dir.pwd
           assert_equal(pwd, slash_pwd, "slash - #{slash}")
           restore_cwd
         }
       }
     ensure
       Dir.chdir(@orig_pwd)
     end
 
     def test_chdir_exceptions_windows
       orig_pwd = Dir.pwd
       assert_raise(Errno::EINVAL) {
         Dir.chdir('//') # '//' is not a valid thing on Windows 
       }
       assert_raise(Errno::ENOENT) {
         Dir.chdir('//blah-blah-blah') # doesn't exist
       }
       assert_raise(Errno::EINVAL) {
         Dir.chdir('\\\\') # '\\\\' is not a valid thing on Windows
       }
       assert_raise(Errno::ENOENT) {
         Dir.chdir('\\\\blah-blah-blah') # doesn't exist
       }
       assert_raise(Errno::ENOENT) {
         Dir.chdir('///') # doesn't exist
       }
       assert_raise(Errno::ENOENT) {
         Dir.chdir('\\\\\\') # doesn't exist
       }
     ensure
       Dir.chdir(orig_pwd)
     end
     
     def test_new_windows
       slashes = ['/', '\\']
       slashes.each { |slash|
         current_drive_letter = Dir.pwd[0..2]
         slash_dir = Dir.new(slash)
 
         slash_entries = []
         slash_dir.each { |file|
           slash_entries << file
         }
 
         drive_root_entries = Dir.entries(current_drive_letter).sort
         slash_entries.sort!
         assert_equal(drive_root_entries, slash_entries, "slash - #{slash}")
       }
     end
     
     def test_new_with_drive_letter
       current_drive_letter = Dir.pwd[0..2]
 
       # Check that 'C:' == 'C:/' == 'C:\\'
       assert_equal(
         Dir.new(current_drive_letter + "/").entries,
         Dir.new(current_drive_letter).entries)
       assert_equal(
         Dir.new(current_drive_letter + "\\").entries,
         Dir.new(current_drive_letter).entries)
     end
     
     def test_entries_with_drive_letter
       current_drive_letter = Dir.pwd[0..2]
 
       # Check that 'C:' == 'C:/' == 'C:\\'
       assert_equal(
         Dir.entries(current_drive_letter + "/"),
         Dir.entries(current_drive_letter))
       assert_equal(
         Dir.entries(current_drive_letter + "\\"),
         Dir.entries(current_drive_letter))
     end
     
     def test_open_windows
       slashes = ['/', '\\']
       slashes.each { |slash|
         current_drive_letter = Dir.pwd[0..2]
         slash_dir = Dir.open(slash)
 
         slash_entries = []
         slash_dir.each { |file|
           slash_entries << file
         }
 
         drive_root_entries = Dir.entries(current_drive_letter).sort
         slash_entries.sort!
         assert_equal(drive_root_entries, slash_entries, "slash - #{slash}")
       }
     end
     
     def test_dir_new_exceptions_windows
       assert_raise(Errno::ENOENT) {
         Dir.new('')
       }
       assert_raise(Errno::EINVAL) {
         Dir.new('//') # '//' is not a valid thing on Windows 
       }
       assert_raise(Errno::ENOENT) {
         Dir.new('//blah-blah-blah') # doesn't exist
       }
       assert_raise(Errno::EINVAL) {
         Dir.new('\\\\') # '\\\\' is not a valid thing on Windows
       }
       assert_raise(Errno::ENOENT) {
         Dir.new('\\\\blah-blah-blah') # doesn't exist
       }
       assert_raise(Errno::ENOENT) {
         Dir.new('///') # doesn't exist
       }
       assert_raise(Errno::ENOENT) {
         Dir.new('\\\\\\') # doesn't exist
       }
     end
     
     def test_entries_windows
       slashes = ['/', '\\']
       slashes.each { |slash|
         current_drive_letter = Dir.pwd[0..2]
         drive_root_entries = Dir.entries(current_drive_letter).sort
         slash_entries = Dir.entries(slash).sort
         assert_equal(drive_root_entries, slash_entries, "slash - #{slash}")
       }
     end
 
     def test_entries_exceptions_windows
       assert_raise(Errno::ENOENT) {
         Dir.entries('')
       }
       assert_raise(Errno::EINVAL) {
         Dir.entries('//') # '//' is not a valid thing on Windows 
       }
       assert_raise(Errno::ENOENT) {
         Dir.entries('//blah-blah-blah') # doesn't exist
       }
       assert_raise(Errno::EINVAL) {
         Dir.entries('\\\\') # '\\\\' is not a valid thing on Windows
       }
       assert_raise(Errno::ENOENT) {
         Dir.entries('\\\\blah-blah-blah') # doesn't exist
       }
       assert_raise(Errno::ENOENT) {
         Dir.entries('///') # doesn't exist
       }
       assert_raise(Errno::ENOENT) {
         Dir.entries('\\\\\\') # doesn't exist
       }
     end
 
     def test_glob_windows
       current_drive_letter = Dir.pwd[0..2]
 
       slash_entries = Dir.glob( "/*").sort.map { |e|
         # remove slash
         e[1..-1]
       }
       drive_root_entries = Dir.glob(current_drive_letter + "*").sort.map { |e|
         # remove drive letter
         e[3..-1]
       }
       assert_equal(drive_root_entries, slash_entries)
     end
 
     def test_path_windows
       assert_equal(Dir.new('/').path, '/')
       assert_equal(Dir.new('\\').path, '\\')
 
       current_drive_letter = Dir.pwd[0, 2]
       assert_equal(Dir.new(current_drive_letter).path, current_drive_letter)
       assert_equal(
         Dir.new(current_drive_letter + "/").path,
         current_drive_letter + "/")
       assert_equal(
         Dir.new(current_drive_letter + "\\").path,
         current_drive_letter + "\\")
       assert_equal(
         Dir.new(current_drive_letter + '/blah/..').path,
         current_drive_letter + '/blah/..')
     end
 
     def test_drive_letter_dirname_leaves_trailing_slash
       assert_equal "C:/", File.dirname('C:/Temp')
       assert_equal "c:\\", File.dirname('c:\\temp')
     end
 
     def test_pathname_realpath_works_with_drive_letters
       require 'pathname'
       win_dir = nil
       if FileTest.exist?('C:/windows')
         win_dir = "windows" 
       elsif FileTest.exist?('C:/winnt')
         win_dir = "winnt" 
       end
         
       if (win_dir != nil)
         Pathname.new("C:\\#{win_dir}").realpath.to_s
         Pathname.new("C:\\#{win_dir}\\..\\#{win_dir}").realpath.to_s
       end
     end
   else
     # http://jira.codehaus.org/browse/JRUBY-1375
     def test_mkdir_on_protected_directory_fails
       Dir.mkdir("testDir_5") unless File.exists?("testDir_5")
       File.chmod(0400, 'testDir_5')
       assert_raises(Errno::EACCES) do
         Dir.mkdir("testDir_5/another_dir")
       end
     end
   end
 end
