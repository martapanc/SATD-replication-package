diff --git a/src/org/jruby/RubyDir.java b/src/org/jruby/RubyDir.java
index bdaa780654..8ef934606d 100644
--- a/src/org/jruby/RubyDir.java
+++ b/src/org/jruby/RubyDir.java
@@ -1,917 +1,932 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
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
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.List;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.zip.ZipEntry;
 import jnr.posix.FileStat;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import jnr.posix.util.Platform;
 import org.jcodings.Encoding;
 import org.jcodings.specific.UTF8Encoding;
 
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.Dir;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.ByteList;
 import static org.jruby.CompatVersion.*;
 
 /**
  * .The Ruby built-in class Dir.
  *
  * @author  jvoegele
  */
 @JRubyClass(name = "Dir", include = "Enumerable")
 public class RubyDir extends RubyObject {
     private RubyString path;       // What we passed to the constructor for method 'path'
     protected JRubyFile dir;
     private long lastModified = Long.MIN_VALUE;
     private String[] snapshot;     // snapshot of contents of directory
     private int pos;               // current position in directory
     private boolean isOpen = true;
 
     private final static Encoding UTF8 = UTF8Encoding.INSTANCE;
 
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
         testFrozen("Dir");
         update();
 
         if (!isOpen) throw getRuntime().newIOError("closed directory");
     }
 
     private void update() {
         if (snapshot == null || dir.exists() && dir.lastModified() > lastModified) {
             lastModified = dir.lastModified();
             List<String> snapshotList = new ArrayList<String>();
             snapshotList.add(".");
             snapshotList.add("..");
             snapshotList.addAll(getContents(dir));
             snapshot = (String[]) snapshotList.toArray(new String[snapshotList.size()]);
         }
     }
 
     /**
      * Creates a new <code>Dir</code>.  This method takes a snapshot of the
      * contents of the directory at creation time, so changes to the contents
      * of the directory will not be reflected during the lifetime of the
      * <code>Dir</code> object returned, so a new <code>Dir</code> instance
      * must be created to reflect changes to the underlying file system.
      */
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject initialize(IRubyObject arg) {
         RubyString newPath = arg.convertToString();
         path = newPath;
         pos = 0;
 
         String adjustedPath = RubyFile.adjustRootPathOnWindows(getRuntime(), newPath.toString(), null);
         checkDirIsTwoSlashesOnWindows(getRuntime(), adjustedPath);
 
         dir = JRubyFile.create(getRuntime().getCurrentDirectory(), adjustedPath);
         List<String> snapshotList = RubyDir.getEntries(getRuntime(), adjustedPath);
         snapshot = (String[]) snapshotList.toArray(new String[snapshotList.size()]);
 
         return this;
     }
 
     @JRubyMethod(name = "initialize", compat = RUBY1_9)
     public IRubyObject initialize19(IRubyObject arg) {
         return initialize(RubyFile.get_path(getRuntime().getCurrentContext(), arg));
     }
 
 // ----- Ruby Class Methods ----------------------------------------------------
 
     private static List<ByteList> dirGlobs(ThreadContext context, String cwd, IRubyObject[] args, int flags) {
         List<ByteList> dirs = new ArrayList<ByteList>();
 
         for (int i = 0; i < args.length; i++) {
             dirs.addAll(Dir.push_glob(cwd, globArgumentAsByteList(context, args[i]), flags));
         }
 
         return dirs;
     }
 
     private static IRubyObject asRubyStringList(Ruby runtime, List<ByteList> dirs) {
         List<RubyString> allFiles = new ArrayList<RubyString>();
         Encoding enc = runtime.getDefaultExternalEncoding();
         if (enc == null) {
             enc = UTF8;
         }
 
         for (ByteList dir : dirs) {
             allFiles.add(RubyString.newString(runtime, dir, enc));
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
     public static IRubyObject aref(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         List<ByteList> dirs;
         if (args.length == 1) {
             Pattern pattern = Pattern.compile("file:(.*)!/(.*)");
             String glob = args[0].toString();
             Matcher matcher = pattern.matcher(glob);
             if (matcher.find()) {
                 String jarFileName = matcher.group(1);
                 String jarUri = "file:" + jarFileName + "!/";
                 String fileGlobString = matcher.group(2);
                 String filePatternString = convertGlobToRegEx(fileGlobString);
                 Pattern filePattern = Pattern.compile(filePatternString);
                 try {
                     JarFile jarFile = new JarFile(jarFileName);
                     List<RubyString> allFiles = new ArrayList<RubyString>();
                     Enumeration<JarEntry> entries = jarFile.entries();
                     while (entries.hasMoreElements()) {
                         String entry = entries.nextElement().getName();
                         String chomped_entry = entry.endsWith("/") ? entry.substring(0, entry.length() - 1) : entry;
                         if (filePattern.matcher(chomped_entry).find()) {
                             allFiles.add(RubyString.newString(runtime, jarUri + chomped_entry.toString()));
                         }
                     }
                     IRubyObject[] tempFileList = new IRubyObject[allFiles.size()];
                     allFiles.toArray(tempFileList);
                     return runtime.newArrayNoCopy(tempFileList);
                 } catch (IOException e) {
                     return runtime.newArrayNoCopy(new IRubyObject[0]);
                 }
             }
 
             dirs = Dir.push_glob(getCWD(runtime), globArgumentAsByteList(context, args[0]), 0);
         } else {
             dirs = dirGlobs(context, getCWD(runtime), args, 0);
         }
 
         return asRubyStringList(runtime, dirs);
     }
 
     private static ByteList globArgumentAsByteList(ThreadContext context, IRubyObject arg) {
         if (context.runtime.is1_9()) return RubyFile.get_path(context, arg).getByteList();
 
         return arg.convertToString().getByteList();
     }
 
     private static String convertGlobToRegEx(String line) {
         line = line.trim();
         StringBuilder sb = new StringBuilder(line.length());
         sb.append("^");
         boolean escaping = false;
         int inCurlies = 0;
         for (char currentChar : line.toCharArray()) {
             switch (currentChar) {
             case '*':
                 if (escaping)
                     sb.append("\\*");
                 else
                     sb.append("[^/]*");
                 escaping = false;
                 break;
             case '?':
                 if (escaping)
                     sb.append("\\?");
                 else
                     sb.append('.');
                 escaping = false;
                 break;
             case '.':
             case '(':
             case ')':
             case '+':
             case '|':
             case '^':
             case '$':
             case '@':
             case '%':
                 sb.append('\\');
                 sb.append(currentChar);
                 escaping = false;
                 break;
             case '\\':
                 if (escaping) {
                     sb.append("\\\\");
                     escaping = false;
                 } else
                     escaping = true;
                 break;
             case '{':
                 if (escaping) {
                     sb.append("\\{");
                 } else {
                     sb.append('(');
                     inCurlies++;
                 }
                 escaping = false;
                 break;
             case '}':
                 if (inCurlies > 0 && !escaping) {
                     sb.append(')');
                     inCurlies--;
                 } else if (escaping)
                     sb.append("\\}");
                 else
                     sb.append("}");
                 escaping = false;
                 break;
             case ',':
                 if (inCurlies > 0 && !escaping) {
                     sb.append('|');
                 } else if (escaping)
                     sb.append("\\,");
                 else
                     sb.append(",");
                 break;
             default:
                 escaping = false;
                 sb.append(currentChar);
             }
         }
         sb.append("$");
         return sb.toString().replace("[^/]*[^/]*/", ".*").replace("[^/]*[^/]*", ".*");
     }
 
     /**
      * Returns an array of filenames matching the specified wildcard pattern
      * <code>pat</code>. If a block is given, the array is iterated internally
      * with each filename is passed to the block in turn. In this case, Nil is
      * returned.
      */
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject glob(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
         int flags = args.length == 2 ? RubyNumeric.num2int(args[1]) : 0;
 
         List<ByteList> dirs;
         IRubyObject tmp = args[0].checkArrayType();
         if (tmp.isNil()) {
             dirs = Dir.push_glob(runtime.getCurrentDirectory(), globArgumentAsByteList(context, args[0]), flags);
         } else {
             dirs = dirGlobs(context, getCWD(runtime), ((RubyArray) tmp).toJavaArray(), flags);
         }
 
         if (block.isGiven()) {
             for (int i = 0; i < dirs.size(); i++) {
                 Encoding enc = runtime.getDefaultExternalEncoding();
                 if (enc == null) {
                     enc = UTF8;
                 }
                 block.yield(context, RubyString.newString(runtime, dirs.get(i), enc));
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
     @JRubyMethod(name = "entries", meta = true, compat = RUBY1_8)
     public static RubyArray entries(IRubyObject recv, IRubyObject path) {
         return entriesCommon(recv.getRuntime(), path.convertToString().getUnicodeValue());
     }
 
     @JRubyMethod(name = "entries", meta = true, compat = RUBY1_9)
     public static RubyArray entries19(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return entriesCommon(context.runtime, RubyFile.get_path(context, arg).asJavaString());
     }
 
     @JRubyMethod(name = "entries", meta = true, compat = RUBY1_9)
     public static RubyArray entries19(ThreadContext context, IRubyObject recv, IRubyObject arg, IRubyObject opts) {
         // FIXME: do something with opts
         return entriesCommon(context.runtime, RubyFile.get_path(context, arg).asJavaString());
     }
 
     private static RubyArray entriesCommon(Ruby runtime, String path) {
         String adjustedPath = RubyFile.adjustRootPathOnWindows(runtime, path, null);
         checkDirIsTwoSlashesOnWindows(runtime, adjustedPath);
 
         Object[] files = getEntries(runtime, adjustedPath).toArray();
         return runtime.newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(runtime, files));
     }
 
     private static List<String> getEntries(Ruby runtime, String path) {
         if (!RubyFileTest.directory_p(runtime, RubyString.newString(runtime, path)).isTrue()) {
             throw runtime.newErrnoENOENTError("No such directory: " + path);
         }
 
         if (path.startsWith("jar:")) path = path.substring(4);
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
         String file = path.substring(5);
         int bang = file.indexOf('!');
         if (bang == -1) {
           return entriesIntoADirectory(runtime, path.substring(5));
         }
         if (bang == file.length() - 1) {
             file = file + "/";
         }
         String jar = file.substring(0, bang);
         String after = file.substring(bang + 2);
         if (after.length() > 0 && after.charAt(after.length() - 1) != '/') {
             after = after + "/";
         }
         JarFile jf;
         try {
             jf = new JarFile(jar);
         } catch (IOException e) {
             throw new RuntimeException("Valid JAR file expected", e);
         }
 
         List<String> fileList = new ArrayList<String>();
         Enumeration<? extends ZipEntry> entries = jf.entries();
         while (entries.hasMoreElements()) {
             String zipEntry = entries.nextElement().getName();
             if (zipEntry.matches(after + "[^/]+(/.*)?")) {
                 int end_index = zipEntry.indexOf('/', after.length());
                 if (end_index == -1) {
                     end_index = zipEntry.length();
                 }
                 String entry_str = zipEntry.substring(after.length(), end_index);
                 if (!fileList.contains(entry_str)) {
                     fileList.add(entry_str);
                 }
             }
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
     @JRubyMethod(optional = 1, meta = true)
     public static IRubyObject chdir(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
         RubyString path = args.length == 1 ?
             RubyFile.get_path(context, args[0]) : getHomeDirectoryPath(context);
         String adjustedPath = RubyFile.adjustRootPathOnWindows(runtime, path.asJavaString(), null);
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
     @JRubyMethod(name = {"rmdir", "unlink", "delete"}, required = 1, meta = true, compat = RUBY1_8)
     public static IRubyObject rmdir(IRubyObject recv, IRubyObject path) {
         return rmdirCommon(recv.getRuntime(), path.convertToString().getUnicodeValue());
     }
 
     @JRubyMethod(name = {"rmdir", "unlink", "delete"}, required = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject rmdir19(ThreadContext context, IRubyObject recv, IRubyObject path) {
         return rmdirCommon(context.runtime, RubyFile.get_path(context, path).asJavaString());
     }
 
     private static IRubyObject rmdirCommon(Ruby runtime, String path) {
         JRubyFile directory = getDirForRmdir(runtime, path);
 
         // at this point, only thing preventing delete should be non-emptiness
         if (runtime.getPosix().rmdir(directory.toString()) < 0) {
             throw runtime.newErrnoENOTEMPTYError(path);
         }
 
         return runtime.newFixnum(0);
     }
 
     /**
      * Executes the block once for each file in the directory specified by
      * <code>path</code>.
      */
     @JRubyMethod(meta = true, compat = RUBY1_8)
     public static IRubyObject foreach(ThreadContext context, IRubyObject recv, IRubyObject _path, Block block) {
         RubyString pathString = _path.convertToString();
 
         return foreachCommon(context, recv, context.runtime, pathString, block);
     }
 
     @JRubyMethod(name = "foreach", meta = true, compat = RUBY1_9)
     public static IRubyObject foreach19(ThreadContext context, IRubyObject recv, IRubyObject arg, Block block) {
         RubyString pathString = RubyFile.get_path(context, arg);
 
         return foreachCommon(context, recv, context.runtime, pathString, block);
     }
 
     private static IRubyObject foreachCommon(ThreadContext context, IRubyObject recv, Ruby runtime, RubyString _path, Block block) {
         if (block.isGiven()) {
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
 
         RubyString pwd = RubyString.newUnicodeString(ruby, getCWD(ruby));
         pwd.setTaint(true);
         return pwd;
     }
 
     /**
      * Returns the home directory of the current user or the named user if given.
      */
     @JRubyMethod(name = "home", optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject home(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length > 0 && !args[0].isNil()) return getHomeDirectoryPath(context, args[0].toString());
 
         return getHomeDirectoryPath(context);
     }
 
     /**
      * Creates the directory specified by <code>path</code>.  Note that the
      * <code>mode</code> parameter is provided only to support existing Ruby
      * code, and is ignored.
      */
     @JRubyMethod(name = "mkdir", required = 1, optional = 1, meta = true, compat = RUBY1_8)
     public static IRubyObject mkdir(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         RubyString stringArg = args[0].convertToString();
 
         return mkdirCommon(runtime, stringArg.getUnicodeValue(), args);
     }
 
     @JRubyMethod(name = "mkdir", required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject mkdir19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return mkdirCommon(context.runtime, RubyFile.get_path(context, args[0]).asJavaString(), args);
     }
 
     private static IRubyObject mkdirCommon(Ruby runtime, String path, IRubyObject[] args) {
         File newDir = getDir(runtime, path, false);
+        
+        
+        String name = path.replace('\\', '/');
+
+        boolean startsWithDriveLetterOnWindows = RubyFile.startsWithDriveLetterOnWindows(name);
+        
+        // don't attempt to create a dir for drive letters
+        if (startsWithDriveLetterOnWindows) {
+            // path is just drive letter plus :
+            if (path.length() == 2) return RubyFixnum.zero(runtime);
+            // path is drive letter plus : plus leading or trailing /
+            if (path.length() == 3 && (path.charAt(0) == '/' || path.charAt(2) == '/')) return RubyFixnum.zero(runtime);
+            // path is drive letter plus : plus leading and trailing /
+            if (path.length() == 4 && (path.charAt(0) == '/' && path.charAt(3) == '/')) return RubyFixnum.zero(runtime);
+        }
 
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
     @JRubyMethod(meta = true, compat = RUBY1_8)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject path, Block block) {
         RubyDir directory = (RubyDir) context.runtime.getDir().newInstance(context,
                 new IRubyObject[]{path}, Block.NULL_BLOCK);
 
         if (!block.isGiven()) return directory;
 
         try {
             return block.yield(context, directory);
         } finally {
             directory.close();
         }
     }
 
     @JRubyMethod(name = "open", meta = true, compat = RUBY1_9)
     public static IRubyObject open19(ThreadContext context, IRubyObject recv, IRubyObject path, Block block) {
         return open(context, recv, RubyFile.get_path(context, path), block);
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
 
     @JRubyMethod(name = "each")
     public IRubyObject each19(ThreadContext context, Block block) {
         return block.isGiven() ? each(context, block) : enumeratorize(context.runtime, this, "each");
     }
 
     @Override
     @JRubyMethod
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
         int pos2 = RubyNumeric.fix2int(newPos);
         if (pos2 >= 0) this.pos = pos2;
         return newPos;
     }
 
     @JRubyMethod(name = "path")
     public IRubyObject path(ThreadContext context) {
         return path.strDup(context.runtime);
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
         checkDir();
 
         pos = 0;
         return this;
     }
 
     @JRubyMethod(name = {"exists?", "exist?"}, meta = true, compat = RUBY1_9)
     public static IRubyObject exist(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         // Capture previous exception if any.
         IRubyObject exception = context.runtime.getGlobalVariables().get("$!");
         try {
             return context.runtime.newFileStat(RubyFile.get_path(context, arg).asJavaString(), false).directory_p();
         } catch (Exception e) {
             // Restore $!
             context.runtime.getGlobalVariables().set("$!", exception);
             return context.runtime.newBoolean(false);
         }
     }
 
 // ----- Helper Methods --------------------------------------------------------
     /** Returns a Java <code>File</code> object for the specified path.  If
      * <code>path</code> is not a directory, throws <code>IOError</code>.
      *
      * @param   path path for which to return the <code>File</code> object.
      * @param   mustExist is true the directory must exist.  If false it must not.
      * @throws  IOError if <code>path</code> is not a directory.
      */
     protected static JRubyFile getDir(final Ruby runtime, final String path, final boolean mustExist) {
         String dir = dirFromPath(path, runtime);
 
         JRubyFile result = JRubyFile.create(runtime.getCurrentDirectory(), dir);
 
         if (mustExist && !result.exists()) {
             throw runtime.newErrnoENOENTError(dir);
         }
 
         boolean isDirectory = result.isDirectory();
 
         if (mustExist && !isDirectory) {
             throw runtime.newErrnoENOTDIRError(path);
         }
 
         if (!mustExist && isDirectory) {
             throw runtime.newErrnoEEXISTError(dir);
         }
 
         return result;
     }
 
     /**
      * Similar to getDir, but performs different checks to match rmdir behavior.
      * @param runtime
      * @param path
      * @param mustExist
      * @return
      */
     protected static JRubyFile getDirForRmdir(final Ruby runtime, final String path) {
         String dir = dirFromPath(path, runtime);
 
         JRubyFile directory = JRubyFile.create(runtime.getCurrentDirectory(), dir);
 
         // Order is important here...File.exists() will return false if the parent
         // dir can't be read, so we check permissions first
 
         // no permission
         if (directory.getParentFile().exists() &&
                 !directory.getParentFile().canWrite()) {
             throw runtime.newErrnoEACCESError(path);
         }
 
         // Since we transcode we depend on posix to lookup stat stuff since
         // java.io.File does not seem to cut it.  A failed stat will throw ENOENT.
         FileStat stat = runtime.getPosix().stat(directory.toString());
 
         // is not directory
         if (!stat.isDirectory()) throw runtime.newErrnoENOTDIRError(path);
 
         return directory;
     }
 
     private static String dirFromPath(final String path, final Ruby runtime) throws RaiseException {
         String dir = path;
         String[] pathParts = RubyFile.splitURI(path);
         if (pathParts != null) {
             if (pathParts[0].equals("file:") && pathParts[1].length() > 0 && pathParts[1].indexOf("!/") == -1) {
                 dir = pathParts[1];
             } else {
                 throw runtime.newErrnoENOTDIRError(dir);
             }
         }
         return dir;
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
         Ruby runtime = context.runtime;
 
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
         Ruby runtime = context.runtime;
         IRubyObject systemHash = runtime.getObject().getConstant("ENV_JAVA");
         RubyHash envHash = (RubyHash) runtime.getObject().getConstant("ENV");
         IRubyObject home = null;
 
         if (home == null || home.isNil()) {
             home = envHash.op_aref(context, runtime.newString("LOGDIR"));
         }
 
         if (home == null || home.isNil()) {
             home = envHash.op_aref(context, runtime.newString("HOME"));
         }
 
         if (home == null || home.isNil()) {
             home = systemHash.callMethod(context, "[]", runtime.newString("user.home"));
         }
 
         if (home == null || home.isNil()) {
             throw runtime.newArgumentError("user.home/LOGDIR not set");
         }
 
         return (RubyString) home;
     }
 }
diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index df483ae466..b950936aac 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -415,1582 +415,1583 @@ public class RubyFile extends RubyIO implements EncodingCapable {
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
             
             if (0 != runtime.getPosix().chmod(filename.getAbsolutePath(), (int)mode.getLongValue())) {
                 throw runtime.newErrnoFromLastPOSIXErrno();
             } else {
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
             
             if (0 != runtime.getPosix().chown(filename.getAbsolutePath(), owner, group)) {
                 throw runtime.newErrnoFromLastPOSIXErrno();
             } else {
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
             JRubyFile file = file(args[i]);
             if (0 != runtime.getPosix().lchmod(file.toString(), (int)mode.getLongValue())) {
                 throw runtime.newErrnoFromLastPOSIXErrno();
             } else {
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
             JRubyFile file = file(args[i]);
 
             if (0 != runtime.getPosix().lchown(file.toString(), owner, group)) {
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
         String fromStr = file(from).toString();
         String toStr = file(to).toString();
 
         int ret = runtime.getPosix().link(fromStr, toStr);
         if (ret != 0) {
             if (runtime.getPosix().isNative()) {
                 throw runtime.newErrnoFromInt(runtime.getPosix().errno(), String.format("(%s, %s)", fromStr, toStr));
             } else {
                 // In most cases, when there is an error during the call,
                 // the POSIX handler throws an exception, but not in case
                 // with pure Java POSIX layer (when native support is disabled),
                 // so we deal with it like this:
                 throw runtime.newErrnoEEXISTError(fromStr + " or " + toStr);
             }
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
             if (runtime.getPosix().symlink(fromStr.getUnicodeValue(), tovalue) == -1) {
                 if (runtime.getPosix().isNative()) {
                     throw runtime.newErrnoFromInt(runtime.getPosix().errno(), String.format("(%s, %s)", fromStr, toStr));
                 } else {
                     throw runtime.newErrnoEEXISTError(String.format("(%s, %s)", fromStr, toStr));
                 }
             }
         } catch (java.lang.UnsatisfiedLinkError ule) {
             throw runtime.newNotImplementedError("symlink() function is unimplemented on this machine");
         }
         
         return RubyFixnum.zero(runtime);
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
         return truncateCommon(context, recv, get_path(context, arg1), arg2);
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
 
     @Override
     public Encoding getEncoding() {
         return null;
     }
 
     @Override
     public void setEncoding(Encoding encoding) {
         // :)
     }
 
     // mri: rb_open_file + rb_scan_open_args
     private IRubyObject openFile19(ThreadContext context, IRubyObject args[]) {
         Ruby runtime = context.runtime;
         RubyString filename = get_path(context, args[0]);
 
         path = adjustRootPathOnWindows(runtime, filename.asJavaString(), runtime.getCurrentDirectory());
 
         IRubyObject[] pm = new IRubyObject[]{null, null};
         RubyHash options = null;
         
         switch(args.length) {
             case 1:
                 break;
             case 2: {
                 IRubyObject test = TypeConverter.checkHashType(runtime, args[1]);
                 if (test instanceof RubyHash) {
                     options = (RubyHash) test;
                 } else {
                     pm[EncodingUtils.VMODE] = args[1];
                 }
                 break;
             }
             case 3: {
                 IRubyObject test = TypeConverter.checkHashType(runtime, args[2]);
                 if (test instanceof RubyHash) {
                     options = (RubyHash) test;
                 } else {
                     pm[EncodingUtils.PERM] = args[2];
                 }
                 pm[EncodingUtils.VMODE] = args[1];                
                 break;
             }
             case 4:
                 options = args[3].convertToHash();
                 pm[EncodingUtils.PERM] = args[2];
                 pm[EncodingUtils.VMODE] = args[1];
                 break;
         }
         
         int oflags = EncodingUtils.extractModeEncoding(context, this, pm, options, false);
         int perm = (pm[EncodingUtils.PERM] != null && !pm[EncodingUtils.PERM].isNil()) ? 
                 RubyNumeric.num2int(pm[EncodingUtils.PERM]) : 0666;
         
         sysopenInternal(path, ModeFlags.createModeFlags(oflags), perm);
 
         return this;
     }
 
     // 1.8
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
 
     // mri: rb_file_open_generic
     protected void sysopenInternal(String path, ModeFlags modes, int perm) {
         if (path.startsWith("jar:")) path = path.substring(4);
         
         openFile = new OpenFile();
 
         openFile.setPath(path);
         openFile.setMode(modes.getOpenFileFlags());
 
         int umask = getUmaskSafe( getRuntime() );
         perm = perm - (perm & umask);
 
         ChannelDescriptor descriptor = sysopen(path, modes, perm);
         openFile.setMainStream(fdopen(descriptor, modes));
         if (hasBom) {
             // FIXME: Wonky that we acquire RubyEncoding to pass these encodings through
             Ruby runtime = getRuntime();
             Encoding bomEncoding = encodingFromBOM();
             if (bomEncoding != null) {
                 IRubyObject theBom = runtime.getEncodingService().getEncoding(bomEncoding);
                 Encoding internalEncoding = getInternalEncoding(getRuntime());
                 IRubyObject theInternal = internalEncoding == null ? 
                         runtime.getNil() : runtime.getEncodingService().getEncoding(internalEncoding);
 
                 setEncoding(runtime.getCurrentContext(), theInternal, theBom, null);
             }
         }
     }
 
     @Deprecated
     protected void openInternal(String path, ModeFlags modes) {
         if (path.startsWith("jar:")) path = path.substring(4);
 
         openFile = new OpenFile();
 
         openFile.setMode(modes.getOpenFileFlags());
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modes));
     }
 
     protected void openInternal(String path, String modeString) {
         if (path.startsWith("jar:")) {
             path = path.substring(4);
         }
         openFile = new OpenFile();
 
         IOOptions modes = newIOOptions(getRuntime(), modeString);
         openFile.setMode(modes.getModeFlags().getOpenFileFlags());
         if (modes.getModeFlags().isBinary()) readEncoding = ASCIIEncoding.INSTANCE;
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modes.getModeFlags()));
     }
 
     private ChannelDescriptor sysopen(String path, ModeFlags modes, int perm) {
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.open(
                     getRuntime().getCurrentDirectory(),
                     path,
                     modes,
                     perm,
                     getRuntime().getPosix(),
                     getRuntime().getJRubyClassLoader());
 
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
 
     private Stream fopen(String path, ModeFlags flags) {
         try {
             return ChannelStream.fopen(
                     getRuntime(),
                     path,
                     flags);
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (PermissionDeniedException pde) {
             // PDException can be thrown only when creating the file and
             // permission is denied.  See JavaDoc of PermissionDeniedException.
             throw getRuntime().newErrnoEACCESError(path);
         } catch (FileNotFoundException ex) {
             // FNFException can be thrown in both cases, when the file
             // is not found, or when permission is denied.
             // FIXME: yes, this is indeed gross.
             String message = ex.getMessage();
             
             if (message.contains(/*P*/"ermission denied") ||
                 message.contains(/*A*/"ccess is denied")) {
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
 
     // mri: FilePathValue/rb_get_path/rb_get_patch_check
     public static RubyString get_path(ThreadContext context, IRubyObject path) {
         if (context.runtime.is1_9()) {
             if (path.respondsTo("to_path")) path = path.callMethod(context, "to_path");
             
             return filePathConvert(context, path.convertToString());
         } 
           
         return path.convertToString();
     }
     
     // FIXME: MRI skips this logic on windows?  Does not make sense to me why so I left it in.
     // mri: file_path_convert
     private static RubyString filePathConvert(ThreadContext context, RubyString path) {
         Ruby runtime = context.getRuntime();
         EncodingService encodingService = runtime.getEncodingService();
         Encoding pathEncoding = path.getEncoding();
 
         // If we are not ascii and do not match fs encoding then transcode to fs.
         if (runtime.getDefaultInternalEncoding() != null &&
                 pathEncoding != encodingService.getUSAsciiEncoding() &&
                 pathEncoding != encodingService.getAscii8bitEncoding() &&
                 pathEncoding != encodingService.getFileSystemEncoding(runtime) &&
                 !path.isAsciiOnly()) {
             ByteList bytes = CharsetTranscoder.transcode(context, path.getByteList(), pathEncoding, encodingService.getFileSystemEncoding(runtime), null);
             path = RubyString.newString(runtime, bytes);
         }                
 
         return path;
     }
 
     /**
      * Get the fully-qualified JRubyFile object for the path, taking into
      * account the runtime's current directory.
      */
     public static JRubyFile file(IRubyObject pathOrFile) {
         Ruby runtime = pathOrFile.getRuntime();
 
         if (pathOrFile instanceof RubyFile) {
             return JRubyFile.create(runtime.getCurrentDirectory(), ((RubyFile) pathOrFile).getPath());
         } else if (pathOrFile instanceof RubyIO) {
             return JRubyFile.create(runtime.getCurrentDirectory(), ((RubyIO) pathOrFile).openFile.getPath());
         } else {
             RubyString pathStr = get_path(runtime.getCurrentContext(), pathOrFile);
             String path = pathStr.asJavaString();
             String[] pathParts = splitURI(path);
             if (pathParts != null && pathParts[0].equals("file:")) {
                 path = pathParts[1];
             }
             return JRubyFile.create(runtime.getCurrentDirectory(), path);
         }
     }
 
     @Override
     public String toString() {
         try {
             return "RubyFile(" + path + ", " + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStreamSafe().getDescriptor()) + ")";
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
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
 
     public static ZipEntry getDirOrFileEntry(String jar, String path) throws IOException {
         String dirPath = path + "/";
         ZipFile zf = Ruby.getGlobalRuntime().getCurrentContext().runtime.getLoadService().getJarFile(jar);
         ZipEntry entry = zf.getEntry(dirPath); // first try as directory
         if (entry == null) {
             if (dirPath.length() == 1) {
                 return new ZipEntry(dirPath);
             }
             // try canonicalizing the path to eliminate . and .. (JRUBY-4760, JRUBY-4879)
             String prefix = new File(".").getCanonicalPath();
             entry = zf.getEntry(new File(dirPath).getCanonicalPath().substring(prefix.length() + 1).replaceAll("\\\\", "/"));
 
             // JRUBY-6119
             if (entry == null) {
                 Enumeration<? extends ZipEntry> entries = zf.entries();
                 while (entries.hasMoreElements()) {
                     String zipEntry = entries.nextElement().getName();
                     if (zipEntry.startsWith(dirPath)) {
                         return new ZipEntry(dirPath);
                     }
                 }
             }
 
             if (entry == null) {
                 // try as file
                 entry = getFileEntry(zf, path);
             }
         }
         return entry;
     }
 
-    private static boolean startsWithDriveLetterOnWindows(String path) {
+    public static boolean startsWithDriveLetterOnWindows(String path) {
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
+    
     // adjusts paths started with '/' or '\\', on windows.
     static String adjustRootPathOnWindows(Ruby runtime, String path, String dir) {
         if (path == null || !Platform.IS_WINDOWS) return path;
 
         // MRI behavior on Windows: it treats '/' as a root of
         // a current drive (but only if SINGLE slash is present!):
         // E.g., if current work directory is
         // 'D:/home/directory', then '/' means 'D:/'.
         //
         // Basically, '/path' is treated as a *RELATIVE* path,
         // relative to the current drive. '//path' is treated
         // as absolute one.
         if ((path.startsWith("/") && !(path.length() > 2 && path.charAt(2) == ':')) || path.startsWith("\\")) {
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
 
         return path;
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
 
     private void checkClosed(ThreadContext context) {
         openFile.checkClosed(context.runtime);
     }
 
     private static boolean isWindowsDriveLetter(char c) {
         return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
     }
 
     private static IRubyObject expandPathInternal(ThreadContext context, IRubyObject recv, IRubyObject[] args, boolean expandUser) {
         Ruby runtime = context.runtime;
 
         String relativePath = get_path(context, args[0]).getUnicodeValue();
         String[] uriParts = splitURI(relativePath);
         String cwd;
 
         // Handle ~user paths
         if (expandUser) {
             relativePath = expandUserPath(context, relativePath);
         }
 
         if (uriParts != null) {
             relativePath = uriParts[1];
         }
 
         // If there's a second argument, it's the path to which the first
         // argument is relative.
         if (args.length == 2 && !args[1].isNil()) {
             cwd = get_path(context, args[1]).getUnicodeValue();
 
             // Handle ~user paths.
             if (expandUser) {
                 cwd = expandUserPath(context, cwd);
             }
 
             String[] cwdURIParts = splitURI(cwd);
             if (uriParts == null && cwdURIParts != null) {
                 uriParts = cwdURIParts;
                 cwd = cwdURIParts[1];
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
         if (uriParts != null) {
             padSlashes = uriParts[0];
         } else if (!Platform.IS_WINDOWS) {
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
 
         return runtime.newString(padSlashes + canonicalize(path.getAbsolutePath()));
     }
 
     public static String[] splitURI(String path) {
         Matcher m = URI_PREFIX.matcher(path);
         if (m.find()) {
             if (m.group(2).length() == 0) {
                 return new String[] {path, ""};
             }
             String pathWithoutJarPrefix;
             if (m.group(1) != null) {
                 pathWithoutJarPrefix = path.substring(4);
             } else {
                 pathWithoutJarPrefix = path;
             }
             try {
                 URI u = new URI(pathWithoutJarPrefix);
                 String pathPart = u.getPath();
                 return new String[] {path.substring(0, path.indexOf(pathPart)), pathPart};
             } catch (Exception e) {
                 try {
                     URL u = new URL(pathWithoutJarPrefix);
                     String pathPart = u.getPath();
                     return new String[] {path.substring(0, path.indexOf(pathPart)), pathPart};
                 } catch (Exception e2) {
                 }
             }
         }
         return null;
     }
 
     /**
      * This method checks a path, and if it starts with ~, then it expands
      * the path to the absolute path of the user's home directory. If the
      * string does not begin with ~, then the string is simply returned.
      * unaltered.
      * @param context
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
                     checkHome(context);
                     path = RubyDir.getHomeDirectoryPath(context).toString();
                 } else {
                     // No directory delimeter.  Rest of string is username
                     userEnd = pathLength;
                 }
             }
 
             if (userEnd == 1) {
                 // '~/...' as path to expand
                 checkHome(context);
                 path = RubyDir.getHomeDirectoryPath(context).toString() +
                         path.substring(1);
             } else if (userEnd > 1){
                 // '~user/...' as path to expand
                 String user = path.substring(1, userEnd);
                 IRubyObject dir = RubyDir.getHomeDirectoryPath(context, user);
 
                 if (dir.isNil()) {
                     throw context.runtime.newArgumentError("user " + user + " does not exist");
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
 
         if (slashCount < SLASHES.length) {
             return SLASHES[slashCount];
         }
 
         // Prepare a string with the same number of redundant slashes so that
         // we easily can prepend it to the result.
         char[] slashes = new char[slashCount];
         for (int i = 0; i < slashCount; i++) {
             slashes[i] = '/';
         }
         return new String(slashes);
     }
 
     public static String canonicalize(String path) {
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
      * Check if HOME environment variable is not nil nor empty
      * @param context 
      */
     private static void checkHome(ThreadContext context) {
         Ruby runtime = context.runtime;
         RubyHash env = runtime.getENV();
         String home = (String) env.get(runtime.newString("HOME"));
         if (home == null || home.equals("")) {
             throw runtime.newArgumentError("couldn't find HOME environment -- expanding `~'");
         }
     }
 
     private static String inspectJoin(ThreadContext context, IRubyObject recv, RubyArray parent, RubyArray array) {
         Ruby runtime = context.runtime;
 
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
         Ruby runtime = context.runtime;
         String separator = context.getRuntime().getClass("File").getConstant("SEPARATOR").toString();
 
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
             if (i > 0 && !element.startsWith(separator)) {
                 buffer.append(separator);
             }
             buffer.append(element);
         }
 
         RubyString fixedStr = RubyString.newString(runtime, buffer.toString());
         fixedStr.setTaint(isTainted);
         return fixedStr;
     }
 
     private static void chomp(StringBuilder buffer) {
         int lastIndex = buffer.length() - 1;
 
         while (lastIndex >= 0 && (buffer.lastIndexOf("/") == lastIndex || buffer.lastIndexOf("\\") == lastIndex)) {
             buffer.setLength(lastIndex);
             lastIndex--;
         }
     }
 
     private static IRubyObject truncateCommon(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         RubyString filename = arg1.convertToString(); // TODO: SafeStringValue here
         Ruby runtime = context.runtime;
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
 
     private static void checkSharedExclusive(Ruby runtime, OpenFile openFile, int lockMode) {
         // This logic used to attempt a shared lock instead of an exclusive
         // lock, because LOCK_EX on some systems (as reported in JRUBY-1214)
         // allow exclusively locking a read-only file. However, the JDK
         // APIs do not allow acquiring an exclusive lock on files that are
         // not open for read, and there are other platforms (such as Solaris,
         // see JRUBY-5627) that refuse at an *OS* level to exclusively lock
         // files opened only for read. As a result, this behavior is platform-
         // dependent, and so we will obey the JDK's policy of disallowing
         // exclusive locks on files opened only for read.
         if (!openFile.isWritable() && (lockMode & LOCK_EX) > 0) {
             throw runtime.newErrnoEBADFError("cannot acquire exclusive lock on File not opened for write");
         }
 
         // Likewise, JDK does not allow acquiring a shared lock on files
         // that have not been opened for read. We comply here.
         if (!openFile.isReadable() && (lockMode & LOCK_SH) > 0) {
             throw runtime.newErrnoEBADFError("cannot acquire shared lock on File not opened for read");
         }
     }
 
     private static IRubyObject lockFailedReturn(Ruby runtime, int lockMode) {
         return (lockMode & LOCK_EX) == 0 ? RubyFixnum.zero(runtime) : runtime.getFalse();
     }
 
     private static boolean lockStateChanges(FileLock lock, int lockMode) {
         if (lock == null) {
             // no lock, only proceed if we are acquiring
             switch (lockMode & 0xF) {
                 case LOCK_UN:
                 case LOCK_UN | LOCK_NB:
                     return false;
                 default:
                     return true;
             }
         } else {
             // existing lock, only proceed if we are unlocking or changing
             switch (lockMode & 0xF) {
                 case LOCK_UN:
                 case LOCK_UN | LOCK_NB:
                     return true;
                 case LOCK_EX:
                 case LOCK_EX | LOCK_NB:
                     return lock.isShared();
                 case LOCK_SH:
                 case LOCK_SH | LOCK_NB:
                     return !lock.isShared();
                 default:
                     return false;
             }
         }
     }
 
     private IRubyObject unlock(Ruby runtime) throws IOException {
         if (currentLock != null) {
             currentLock.release();
             currentLock = null;
 
             return RubyFixnum.zero(runtime);
         }
         return runtime.getFalse();
     }
 
     private IRubyObject lock(Ruby runtime, FileChannel fileChannel, boolean exclusive) throws IOException {
         if (currentLock != null) currentLock.release();
 
         currentLock = fileChannel.lock(0L, Long.MAX_VALUE, !exclusive);
 
         if (currentLock != null) {
             return RubyFixnum.zero(runtime);
         }
 
         return lockFailedReturn(runtime, exclusive ? LOCK_EX : LOCK_SH);
     }
 
     private IRubyObject tryLock(Ruby runtime, FileChannel fileChannel, boolean exclusive) throws IOException {
         if (currentLock != null) currentLock.release();
 
         currentLock = fileChannel.tryLock(0L, Long.MAX_VALUE, !exclusive);
 
         if (currentLock != null) {
             return RubyFixnum.zero(runtime);
         }
 
         return lockFailedReturn(runtime, exclusive ? LOCK_EX : LOCK_SH);
     }
 
     private static final long serialVersionUID = 1L;
 
     public static final int LOCK_SH = 1;
     public static final int LOCK_EX = 2;
     public static final int LOCK_NB = 4;
     public static final int LOCK_UN = 8;
 
     private static final int FNM_NOESCAPE = 1;
     private static final int FNM_PATHNAME = 2;
     private static final int FNM_DOTMATCH = 4;
     private static final int FNM_CASEFOLD = 8;
     private static final int FNM_SYSCASE = Platform.IS_WINDOWS ? FNM_CASEFOLD : 0;
 
     private static int _cachedUmask = 0;
     private static final Object _umaskLock = new Object();
     private static final String[] SLASHES = {"", "/", "//"};
     private static Pattern URI_PREFIX = Pattern.compile("^(jar:)?[a-z]{2,}:(.*)");
 
     protected String path;
     private volatile FileLock currentLock;
 }
