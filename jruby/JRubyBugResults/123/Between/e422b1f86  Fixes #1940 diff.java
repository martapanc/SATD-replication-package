diff --git a/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java b/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
index 1282bd2b46..1de6c36b62 100644
--- a/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
+++ b/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
@@ -1,305 +1,305 @@
 package org.jruby.runtime.load;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.RubyFile;
 import org.jruby.RubyHash;
 import org.jruby.RubyString;
 import org.jruby.ast.executable.Script;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.LoadService.SuffixType;
 import org.jruby.util.ClasspathResource;
 import org.jruby.util.FileResource;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.URLResource;
 
 class LibrarySearcher {
     static class Ruby18 extends LibrarySearcher {
         public Ruby18(LoadService loadService) {
             super(loadService);
         }
 
         @Override
         protected String resolveLoadName(FileResource unused, String ruby18Path) {
             return ruby18Path;
         }
 
         @Override
         protected String resolveScriptName(FileResource unused, String ruby18Path) {
             return ruby18Path;
         }
     }
 
     static class FoundLibrary implements Library {
         private final Library delegate;
         private final String loadName;
 
         public FoundLibrary(Library delegate, String loadName) {
             this.delegate = delegate;
             this.loadName = loadName;
         }
 
         @Override
         public void load(Ruby runtime, boolean wrap) throws IOException {
             delegate.load(runtime, wrap);
         }
 
         public String getLoadName() {
             return loadName;
         }
     }
 
     private final LoadService loadService;
     private final Ruby runtime;
     private final Map<String, Library> builtinLibraries;
 
     public LibrarySearcher(LoadService loadService) {
         this.loadService = loadService;
         this.runtime = loadService.runtime;
         this.builtinLibraries = loadService.builtinLibraries;
     }
 
     // TODO(ratnikov): Kill this helper once we kill LoadService.SearchState
     public FoundLibrary findBySearchState(LoadService.SearchState state) {
         FoundLibrary lib = findLibrary(state.searchFile, state.suffixType);
         if (lib != null) {
             state.library = lib;
             state.loadName = lib.getLoadName();
         }
         return lib;
     }
 
     public FoundLibrary findLibrary(String baseName, SuffixType suffixType) {
         for (String suffix : suffixType.getSuffixes()) {
             FoundLibrary library = findBuiltinLibrary(baseName, suffix);
             if (library == null) library = findResourceLibrary(baseName, suffix);
             if (library == null) library = findServiceLibrary(baseName, suffix);
 
             if (library != null) {
                 return library;
             }
         }
 
         return null;
     }
 
     private FoundLibrary findBuiltinLibrary(String name, String suffix) {
         String namePlusSuffix = name + suffix;
 
         DebugLog.Builtin.logTry(namePlusSuffix);
         if (builtinLibraries.containsKey(namePlusSuffix)) {
             DebugLog.Builtin.logFound(namePlusSuffix);
             return new FoundLibrary(
                     builtinLibraries.get(namePlusSuffix),
                     namePlusSuffix);
         }
         return null;
     }
 
     private FoundLibrary findServiceLibrary(String name, String ignored) {
         DebugLog.JarExtension.logTry(name);
         Library extensionLibrary = ClassExtensionLibrary.tryFind(runtime, name);
         if (extensionLibrary != null) {
             DebugLog.JarExtension.logFound(name);
             return new FoundLibrary(extensionLibrary, name);
         } else {
             return null;
         }
     }
 
     private FoundLibrary findResourceLibrary(String baseName, String suffix) {
         if (baseName.startsWith("./")) {
             return findFileResource(baseName, suffix);
         }
 
         if (baseName.startsWith("../")) {
             // Path should be canonicalized in the findFileResource
             return findFileResource(baseName, suffix);
         }
 
         if (baseName.startsWith("~/")) {
             RubyHash env = (RubyHash) runtime.getObject().getConstant("ENV");
             RubyString env_home = runtime.newString("HOME");
             if (env.has_key_p(env_home).isFalse()) {
                 return null;
             }
             String home = env.op_aref(runtime.getCurrentContext(), env_home).toString();
             String path = home + "/" + baseName.substring(2);
 
             return findFileResource(path, suffix);
         }
 
         // If path is considered absolute, bypass loadPath iteration and load as-is
         if (isAbsolute(baseName)) {
           return findFileResource(baseName, suffix);
         }
 
         try {
             for (IRubyObject loadPathEntry : loadService.loadPath.toJavaArray()) {
                 FoundLibrary library = findFileResourceWithLoadPath(baseName, suffix, getPath(loadPathEntry));
                 if (library != null) return library;
             }
         } catch (Throwable t) {
             t.printStackTrace();
         }
 
         return null;
     }
 
     // FIXME: to_path should not be called n times it should only be once and that means a cache which would
     // also reduce all this casting and/or string creates.
     private String getPath(IRubyObject loadPathEntry) {
         if (runtime.is1_8()) return loadPathEntry.convertToString().asJavaString();
 
         return RubyFile.get_path(runtime.getCurrentContext(), loadPathEntry).asJavaString();
     }
 
     private FoundLibrary findFileResource(String searchName, String suffix) {
         return findFileResourceWithLoadPath(searchName, suffix, null);
     }
 
     private FoundLibrary findFileResourceWithLoadPath(String searchName, String suffix, String loadPath) {
         String fullPath = loadPath != null ? loadPath + "/" + searchName : searchName;
         String pathWithSuffix = fullPath + suffix;
 
         DebugLog.Resource.logTry(pathWithSuffix);
         FileResource resource = JRubyFile.createResource(runtime, pathWithSuffix);
         if (resource.exists()) {
             DebugLog.Resource.logFound(pathWithSuffix);
             String scriptName = resolveScriptName(resource, pathWithSuffix);
             String loadName = resolveLoadName(resource, searchName + suffix);
 
             return new FoundLibrary(
                     new ResourceLibrary(searchName, scriptName, resource),
                     loadName);
         }
 
         return null;
     }
 
     private static boolean isAbsolute(String path) {
         // jar: prefix doesn't mean anything anymore, but we might still encounter it
         if (path.startsWith("jar:")) {
             path = path.substring(4);
         }
 
         if (path.startsWith("file:")) {
             // We treat any paths with a file schema as absolute, because apparently some tests
             // explicitely depend on such behavior (test/test_load.rb). On other hand, maybe it's
             // not too bad, since otherwise joining LOAD_PATH logic would be more complicated if
             // it'd have to worry about schema.
             return true;
         }
         if (path.startsWith("uri:")) {
             // uri: are absolute
             return true;
         }
         return new File(path).isAbsolute();
     }
 
     protected String resolveLoadName(FileResource resource, String ruby18path) {
-        return resource.absolutePath();
+        return resource.canonicalPath();
     }
 
     protected String resolveScriptName(FileResource resource, String ruby18Path) {
-        return resource.absolutePath();
+        return RubyFile.canonicalize(resource.absolutePath());
     }
 
     static class ResourceLibrary implements Library {
         private final String searchName;
         private final String scriptName;
         private final FileResource resource;
         private final String location;
 
         public ResourceLibrary(String searchName, String scriptName, FileResource resource) {
             this.searchName = searchName;
             this.scriptName = scriptName;
             this.location = resource.absolutePath();
 
             this.resource = resource;
         }
 
         @Override
         public void load(Ruby runtime, boolean wrap) {
             InputStream is = resource.openInputStream();
             if (is == null) {
                 throw runtime.newLoadError("no such file to load -- " + searchName, searchName);
             }
 
             try {
                 if (location.endsWith(".jar")) {
                     loadJar(runtime, wrap);
                 } else if (location.endsWith(".class")) {
                     loadClass(runtime, is, wrap);
                 } else {
                     loadScript(runtime, is, wrap);
                 }
             } finally {
                 try {
                     is.close();
                 } catch (IOException ioE) {
                     // At least we tried....
                 }
             }
         }
 
         private void loadScript(Ruby runtime, InputStream is, boolean wrap) {
             runtime.loadFile(scriptName, is, wrap);
         }
 
         private void loadClass(Ruby runtime, InputStream is, boolean wrap) {
             Script script = CompiledScriptLoader.loadScriptFromFile(runtime, is, searchName);
             if (script == null) {
                 // we're depending on the side effect of the load, which loads the class but does not turn it into a script
                 // I don't like it, but until we restructure the code a bit more, we'll need to quietly let it by here.
                 return;
             }
             script.setFilename(scriptName);
             runtime.loadScript(script, wrap);
         }
 
         private void loadJar(Ruby runtime, boolean wrap) {
             try {
                 URL url;
                 if (location.startsWith(ClasspathResource.CLASSPATH)){
                     // get URL directly from the classloader with its StreamHandler set
                     // by the classloader itself
                     url = ClasspathResource.getResourceURL(location);
                 }
                 else if (location.startsWith(URLResource.URI)){
                     url = null;
                     runtime.getJRubyClassLoader().addURLNoIndex(URLResource.getResourceURL(location));
                 }
                 else {
                     File f = new File(location);
                     if (f.exists() || location.contains( "!")){
                         url = f.toURI().toURL();
                         if ( location.contains( "!") ) {
                             url = new URL( "jar:" + url );
                         }
                     }
                     else {
                         url = new URL(location);
                     }
                 }
                 if ( url != null ) {
                     runtime.getJRubyClassLoader().addURL(url);
                 }
             } catch (MalformedURLException badUrl) {
                 runtime.newIOErrorFromException(badUrl);
             }
 
             // If an associated Service library exists, load it as well
             ClassExtensionLibrary serviceExtension = ClassExtensionLibrary.tryFind(runtime, searchName);
             if (serviceExtension != null) {
                 serviceExtension.load(runtime, wrap);
             }
         }
     }
 }
diff --git a/core/src/main/java/org/jruby/util/ClasspathResource.java b/core/src/main/java/org/jruby/util/ClasspathResource.java
index fdc51cc34c..0164603037 100644
--- a/core/src/main/java/org/jruby/util/ClasspathResource.java
+++ b/core/src/main/java/org/jruby/util/ClasspathResource.java
@@ -1,148 +1,153 @@
 package org.jruby.util;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 
 import jnr.posix.FileStat;
 import jnr.posix.POSIX;
 
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ModeFlags;
 
 public class ClasspathResource implements FileResource {
 
     public static final String CLASSPATH = "classpath:/";
 
     private final String uri;
     
     private final JarFileStat fileStat;
 
     private boolean isFile;
 
     ClasspathResource(String uri, URL url)
     {
         this.uri = uri;
         this.fileStat = new JarFileStat(this);
         this.isFile = url != null;
     }
 
     public static URL getResourceURL(String pathname) {
         String path = pathname.substring(CLASSPATH.length() );
         // this is the J2EE case
         URL url = Thread.currentThread().getContextClassLoader().getResource(path);
         if ( url != null ) {
             return url;
         }
         else if (ClasspathResource.class.getClassLoader() != null) {
             // this is OSGi case
             return ClasspathResource.class.getClassLoader().getResource(path);
         }
         return null;
     }
     
     public static FileResource create(String pathname) {
         if (!pathname.startsWith("classpath:")) {
             return null;
         }
         if (pathname.equals("classpath:")) {
             return new ClasspathResource(pathname, null);
         }
         
         URL url = getResourceURL(pathname);
         return new ClasspathResource(pathname, url);
     }
 
     @Override
     public String absolutePath()
     {
         return uri;
     }
 
     @Override
+    public String canonicalPath() {
+        return uri;
+    }
+
+    @Override
     public boolean exists()
     {
         return isFile;
     }
 
     @Override
     public boolean isDirectory()
     {
         return false;
     }
 
     @Override
     public boolean isFile()
     {
         return isFile;
     }
 
     @Override
     public long lastModified()
     {
         // TODO Auto-generated method stub
         return 0;
     }
 
     @Override
     public long length()
     {
         // TODO Auto-generated method stub
         return 0;
     }
 
     @Override
     public boolean canRead()
     {
         return true;
     }
 
     @Override
     public boolean canWrite()
     {
         return false;
     }
 
     @Override
     public String[] list()
     {
         return null;
     }
 
     @Override
     public boolean isSymLink()
     {
         return false;
     }
 
     @Override
     public FileStat stat() {
         return fileStat;
     }
 
     @Override
     public FileStat lstat() {
       return stat(); // we don't have symbolic links here, so lstat == stat
     }
 
     @Override
     public JRubyFile hackyGetJRubyFile() {
         // TODO Auto-generated method stub
         return null;
     }
 
     @Override
     public InputStream openInputStream() {
         try {
             return getResourceURL(uri).openStream();
         } catch (IOException ioE) {
             return null;
         }
     }
 
     @Override
     public ChannelDescriptor openDescriptor(ModeFlags flags, int perm) throws ResourceException {
         return null;
     }
     
 }
diff --git a/core/src/main/java/org/jruby/util/EmptyFileResource.java b/core/src/main/java/org/jruby/util/EmptyFileResource.java
index a134f4a0c1..77a21c5a09 100644
--- a/core/src/main/java/org/jruby/util/EmptyFileResource.java
+++ b/core/src/main/java/org/jruby/util/EmptyFileResource.java
@@ -1,97 +1,102 @@
 package org.jruby.util;
 
 import jnr.posix.FileStat;
 import jnr.posix.POSIX;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ModeFlags;
 import java.io.InputStream;
 
 class EmptyFileResource implements FileResource {
     // All empty resources are the same and immutable, so may as well
     // cache the instance
     private static final EmptyFileResource INSTANCE = new EmptyFileResource();
 
     public static EmptyFileResource create(String pathname) {
         return (pathname == null || "".equals(pathname)) ?
             INSTANCE : null;
     }
 
     @Override
     public String absolutePath() {
         return "";
     }
 
     @Override
+    public String canonicalPath() {
+        return "";
+    }
+
+    @Override
     public boolean exists() {
         return false;
     }
 
     @Override
     public boolean isDirectory() {
         return false;
     }
 
     @Override
     public boolean isFile() {
         return false;
     }
 
     @Override
     public boolean canRead() {
         return false;
     }
 
     @Override
     public boolean canWrite() {
         return false;
     }
 
     @Override
     public boolean isSymLink() {
         return false;
     }
 
     @Override
     public String[] list() {
         return new String[0];
     }
 
     @Override
     public long lastModified() {
         throw new UnsupportedOperationException();
     }
 
     @Override
     public long length() {
         throw new UnsupportedOperationException();
     }
 
     @Override
     public FileStat stat() {
         throw new UnsupportedOperationException();
     }
 
     @Override
     public FileStat lstat() {
         throw new UnsupportedOperationException();
     }
 
     @Override
     public JRubyFile hackyGetJRubyFile() {
         // It is somewhat weird that we're returning the NOT_EXIST instance that this resource is
         // intending to replace. However, that should go away once we get rid of the hacky method, so
         // should be okay for now.
         return JRubyNonExistentFile.NOT_EXIST;
     }
 
     @Override
     public InputStream openInputStream() {
       return null;
     }
 
     @Override
     public ChannelDescriptor openDescriptor(ModeFlags flags, int perm) throws ResourceException {
         throw new ResourceException.NotFound(absolutePath());
     }
 }
diff --git a/core/src/main/java/org/jruby/util/FileResource.java b/core/src/main/java/org/jruby/util/FileResource.java
index 738493ba6e..9d19e80f1f 100644
--- a/core/src/main/java/org/jruby/util/FileResource.java
+++ b/core/src/main/java/org/jruby/util/FileResource.java
@@ -1,45 +1,46 @@
 package org.jruby.util;
 
 import jnr.posix.FileStat;
 import jnr.posix.POSIX;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ModeFlags;
 import java.io.InputStream;
 
 /**
  * This is a shared interface for files loaded as {@link java.io.File} and {@link java.util.zip.ZipEntry}.
  */
 public interface FileResource {
     String absolutePath();
+    String canonicalPath();
 
     boolean exists();
     boolean isDirectory();
     boolean isFile();
 
     long lastModified();
     long length();
 
     boolean canRead();
     boolean canWrite();
 
     /**
      * @see java.io.File#list()
      */
     String[] list();
 
     boolean isSymLink();
 
     FileStat stat();
     FileStat lstat();
 
     // For transition to file resources only. Implementations should return
     // JRubyFile if this resource is backed by one, and NOT_FOUND JRubyFile
     // otherwise.
     JRubyFile hackyGetJRubyFile();
 
     // Opens a new input stream to read the contents of a resource and returns it.
     // Note that implementations may be allocating native memory for the stream, so
     // callers need to close this when they are done with it.
     InputStream openInputStream();
     ChannelDescriptor openDescriptor(ModeFlags flags, int perm) throws ResourceException;
 }
diff --git a/core/src/main/java/org/jruby/util/JarResource.java b/core/src/main/java/org/jruby/util/JarResource.java
index b0305d6dcb..3aaf142d39 100644
--- a/core/src/main/java/org/jruby/util/JarResource.java
+++ b/core/src/main/java/org/jruby/util/JarResource.java
@@ -1,124 +1,129 @@
 package org.jruby.util;
 
 import jnr.posix.FileStat;
 import jnr.posix.POSIX;
 
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.util.jar.JarEntry;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 abstract class JarResource implements FileResource {
     private static Pattern PREFIX_MATCH = Pattern.compile("^(?:jar:)?(?:file:)?(.*)$");
 
     private static final JarCache jarCache = new JarCache();
 
     public static JarResource create(String pathname) {
         if (!pathname.contains("!")) return null;  // Optimization: no ! no jar!
 
         Matcher matcher = PREFIX_MATCH.matcher(pathname);
         String sanitized = matcher.matches() ? matcher.group(1) : pathname;
 
         try {
             // since pathname is actually an uri we need to decode any url decoded characters like %20
             // which happens when directory names contain spaces
             sanitized = URLDecoder.decode(sanitized, "UTF-8");
         } catch (UnsupportedEncodingException e) {
             throw new RuntimeException( "hmm - system does not know UTF-8 string encoding :(" );
         }
 
         int bang = sanitized.indexOf('!');
         String jarPath = sanitized.substring(0, bang);
         String entryPath = sanitized.substring(bang + 1);
 
         // TODO: Do we really need to support both test.jar!foo/bar.rb and test.jar!/foo/bar.rb cases?
         JarResource resource = createJarResource(jarPath, entryPath, false);
 
         if (resource == null && entryPath.startsWith("/")) {
             resource = createJarResource(jarPath, entryPath.substring(1), true);
         }
 
         return resource;
     }
 
     private static JarResource createJarResource(String jarPath, String entryPath, boolean rootSlashPrefix) {
         JarCache.JarIndex index = jarCache.getIndex(jarPath);
 
         if (index == null) {
             // Jar doesn't exist
             return null;
         }
 
         // Try it as directory first, because jars tend to have foo/ entries
         // and it's not really possible disambiguate between files and directories.
         String[] entries = index.getDirEntries(entryPath);
         if (entries != null) {
             return new JarDirectoryResource(jarPath, rootSlashPrefix, entryPath, entries);
         }
 
         JarEntry jarEntry = index.getJarEntry(entryPath);
         if (jarEntry != null) {
             return new JarFileResource(jarPath, rootSlashPrefix, index, jarEntry);
         }
 
         return null;
     }
 
     private final String jarPrefix;
     private final JarFileStat fileStat;
 
     protected JarResource(String jarPath, boolean rootSlashPrefix) {
         this.jarPrefix = rootSlashPrefix ? jarPath + "!/" : jarPath + "!";
         this.fileStat = new JarFileStat(this);
     }
 
     @Override
     public String absolutePath() {
         return jarPrefix + entryName();
     }
 
     @Override
+    public String canonicalPath() {
+        return absolutePath();
+    }
+
+    @Override
     public boolean exists() {
         // If a jar resource got created, then it always corresponds to some kind of resource
         return true;
     }
 
     @Override
     public boolean canRead() {
         // Can always read from a jar
         return true;
     }
 
     @Override
     public boolean canWrite() {
         return false;
     }
 
     @Override
     public boolean isSymLink() {
         // Jar archives shouldn't contain symbolic links, or it would break portability. `jar`
         // command behavior seems to comform to that (it unwraps syumbolic links when creating a jar
         // and replaces symbolic links with regular file when extracting from a zip that contains
         // symbolic links). Also see:
         // http://www.linuxquestions.org/questions/linux-general-1/how-to-create-jar-files-with-symbolic-links-639381/
         return false;
     }
 
     @Override
     public FileStat stat() {
         return fileStat;
     }
 
     @Override
     public FileStat lstat() {
       return stat(); // jars don't have symbolic links, so lstat == stat
     }
 
     @Override
     public JRubyFile hackyGetJRubyFile() {
       return JRubyNonExistentFile.NOT_EXIST;
     }
 
     abstract protected String entryName();
 }
diff --git a/core/src/main/java/org/jruby/util/RegularFileResource.java b/core/src/main/java/org/jruby/util/RegularFileResource.java
index 5991641eee..a145519b11 100644
--- a/core/src/main/java/org/jruby/util/RegularFileResource.java
+++ b/core/src/main/java/org/jruby/util/RegularFileResource.java
@@ -1,237 +1,239 @@
 package org.jruby.util;
 
 import jnr.posix.FileStat;
 import jnr.posix.POSIX;
 import jnr.posix.POSIXFactory;
 import java.io.File;
 import java.io.FileDescriptor;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.InputStream;
 import java.io.IOException;
 import java.io.RandomAccessFile;
 import java.nio.channels.FileChannel;
 
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ModeFlags;
 
 /**
  * Represents a "regular" file, backed by regular file system.
  */
 class RegularFileResource implements FileResource {
     private final JRubyFile file;
     private final POSIX posix;
 
     RegularFileResource(POSIX posix, File file) {
         this(posix, file.getAbsolutePath());
     }
 
     protected RegularFileResource(POSIX posix, String filename) {
         this.file = new JRubyFile(filename);
         this.posix = posix;
     }
 
-    // TODO(ratnikov): This should likely be renamed to rubyPath, otherwise it's easy to get
-    // confused between java's absolute path (no symlink resolution) and ruby absolute path (which
-    // does symlink resolution).
     @Override
     public String absolutePath() {
+        return file.getAbsolutePath();
+    }
+
+    @Override
+    public String canonicalPath() {
         // Seems like for Ruby absolute path implies resolving system links,
         // so canonicalization is in order.
         try {
-          return file.getCanonicalPath();
+            return file.getCanonicalPath();
         } catch (IOException ioError) {
-          // I guess absolute path is next best thing?
-          return file.getAbsolutePath();
+            // I guess absolute path is next best thing?
+            return file.getAbsolutePath();
         }
     }
 
     @Override
     public long length() {
         return file.length();
     }
 
     @Override
     public long lastModified() {
         return file.lastModified();
     }
 
     @Override
     public boolean exists() {
         // MRI behavior: Even broken symlinks should return true.
         return file.exists() || isSymLink();
     }
 
     @Override
     public boolean isFile() {
         return file.isFile();
     }
 
     @Override
     public boolean isDirectory() {
         return file.isDirectory();
     }
 
     @Override
     public boolean isSymLink() {
         FileStat stat = posix.allocateStat();
 
         return posix.lstat(file.getAbsolutePath(), stat) < 0 ?
                 false : stat.isSymlink();
     }
 
     @Override
     public boolean canRead() {
         return file.canRead();
     }
 
     @Override
     public boolean canWrite() {
         return file.canWrite();
     }
 
     @Override
     public String[] list() {
         String[] fileList = file.list();
 
         if (fileList == null) return null;
 
         // If we got some entries, then it's probably a directory and in Ruby all file
         // directories should have '.' and '..' entries
         String[] list = new String[fileList.length + 2];
         list[0] = ".";
         list[1] = "..";
         System.arraycopy(fileList, 0, list, 2, fileList.length);
 
         return list;
     }
 
     @Override
     public FileStat stat() {
         return posix.stat(absolutePath());
     }
 
     @Override
     public FileStat lstat() {
         return posix.lstat(file.getAbsolutePath());
     }
 
     @Override
     public String toString() {
         return file.toString();
     }
 
     @Override
     public JRubyFile hackyGetJRubyFile() {
         return file;
     }
 
     @Override
     public InputStream openInputStream() {
         try {
             return new java.io.BufferedInputStream(new FileInputStream(file), 32768);
         } catch (FileNotFoundException fnfe) {
             return null;
         }
     }
 
     @Override
     public ChannelDescriptor openDescriptor(ModeFlags flags, int perm) throws ResourceException {
         if (flags.isCreate()) {
             boolean fileCreated;
             try {
                 fileCreated = file.createNewFile();
             } catch (IOException ioe) {
                 // See JRUBY-4380.
                 // when the directory for the file doesn't exist.
                 // Java in such cases just throws IOException.
                 File parent = file.getParentFile();
                 if (parent != null && parent != file && !parent.exists()) {
                     throw new ResourceException.NotFound(absolutePath());
                 } else if (!file.canWrite()) {
                     throw new ResourceException.PermissionDenied(absolutePath());
                 } else {
                     // for all other IO errors, we report it as general IO error
                     throw new ResourceException.IOError(ioe);
                 }
             }
 
             if (!fileCreated && flags.isExclusive()) {
                 throw new ResourceException.FileExists(absolutePath());
             }
 
             ChannelDescriptor descriptor = createDescriptor(flags);
 
             // attempt to set the permissions, if we have been passed a POSIX instance,
             // perm is > 0, and only if the file was created in this call.
             if (fileCreated && posix != null && perm > 0) {
                 if (perm > 0) posix.chmod(file.getPath(), perm);
             }
 
             return descriptor;
         }
 
         if (file.isDirectory() && flags.isWritable()) {
             throw new ResourceException.FileIsDirectory(absolutePath());
         }
 
         if (!file.exists()) {
             throw new ResourceException.NotFound(absolutePath());
         }
 
         return createDescriptor(flags);
      }
 
     private ChannelDescriptor createDescriptor(ModeFlags flags) throws ResourceException {
         FileDescriptor fileDescriptor;
         FileChannel fileChannel;
 
         /* Because RandomAccessFile does not provide a way to pass append
          * mode, we must manually seek if using RAF. FileOutputStream,
          * however, does properly honor append mode at the lowest levels,
          * reducing append write costs when we're only doing writes.
          *
          * The code here will use a FileOutputStream if we're only writing,
          * setting isInAppendMode to true to disable our manual seeking.
          *
          * RandomAccessFile does not handle append for us, so if we must
          * also be readable we pass false for isInAppendMode to indicate
          * we need manual seeking.
          */
         boolean isInAppendMode;
         try{
             if (flags.isWritable() && !flags.isReadable()) {
                 FileOutputStream fos = new FileOutputStream(file, flags.isAppendable());
                 fileChannel = fos.getChannel();
                 fileDescriptor = fos.getFD();
                 isInAppendMode = true;
             } else {
                 RandomAccessFile raf = new RandomAccessFile(file, flags.toJavaModeString());
                 fileChannel = raf.getChannel();
                 fileDescriptor = raf.getFD();
                 isInAppendMode = false;
             }
         } catch (FileNotFoundException fnfe) {
             // Jave throws FileNotFoundException both if the file doesn't exist or there were
             // permission issues, but Ruby needs to disambiguate those two cases
             throw file.exists() ?
                 new ResourceException.PermissionDenied(absolutePath()) :
                 new ResourceException.NotFound(absolutePath());
         } catch (IOException ioe) {
             throw new ResourceException.IOError(ioe);
         }
 
         try {
             if (flags.isTruncate()) fileChannel.truncate(0);
         } catch (IOException ioe) {
             // ignore; it's a pipe or fifo that can't be truncated (we only care about illegal seek).
             if (!ioe.getMessage().equals("Illegal seek")) throw new ResourceException.IOError(ioe);
         }
 
         // TODO: append should set the FD to end, no? But there is no seek(int) in libc!
         //if (modes.isAppendable()) seek(0, Stream.SEEK_END);
 
         return new ChannelDescriptor(fileChannel, flags, fileDescriptor, isInAppendMode);
     }
 }
diff --git a/core/src/main/java/org/jruby/util/URLResource.java b/core/src/main/java/org/jruby/util/URLResource.java
index dff9af65c6..c706eb81c3 100644
--- a/core/src/main/java/org/jruby/util/URLResource.java
+++ b/core/src/main/java/org/jruby/util/URLResource.java
@@ -1,291 +1,295 @@
 package org.jruby.util;
 
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Enumeration;
 import java.util.LinkedHashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Set;
 
 import jnr.posix.FileStat;
 import jnr.posix.POSIX;
 
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ModeFlags;
 
 public class URLResource implements FileResource {
 
     public static String URI = "uri:";
     public static String CLASSLOADER = "classloader:/";
     public static String URI_CLASSLOADER = URI + CLASSLOADER;
 
     private final String uri;
 
     private final String[] list;
 
     private final URL url;
     private final String pathname;
 
     private final JarFileStat fileStat;
 
     URLResource(String uri, URL url, String[] files) {
         this(uri, url, null, files);
     }
     
     URLResource(String uri, String pathname, String[] files) {
         this(uri, null, pathname, files);
     }
     
     private URLResource(String uri, URL url, String pathname, String[] files) {
         this.uri = uri;
         this.list = files;
         this.url = url;
         this.pathname = pathname;
         this.fileStat = new JarFileStat(this);
     }
     
     @Override
     public String absolutePath()
     {
         return uri;
     }
 
+    public String canonicalPath() {
+        return uri;
+    }
+
     @Override
     public boolean exists()
     {
         return url != null || pathname != null || list != null;
     }
 
     @Override
     public boolean isDirectory()
     {
         return list != null;
     }
 
     @Override
     public boolean isFile()
     {
         return list == null && (url != null || pathname != null);
     }
 
     @Override
     public long lastModified()
     {
         // TODO Auto-generated method stub
         return 0;
     }
 
     @Override
     public long length()
     {
         // TODO Auto-generated method stub
         return 0;
     }
 
     @Override
     public boolean canRead()
     {
         return true;
     }
 
     @Override
     public boolean canWrite()
     {
         return false;
     }
 
     @Override
     public String[] list()
     {
         return list;
     }
 
     @Override
     public boolean isSymLink()
     {
         return false;
     }
 
     @Override
     public FileStat stat() {
         return fileStat;
     }
 
     @Override
     public FileStat lstat() {
       return stat(); // URLs don't have symbolic links, so lstat == stat
     }
  
     @Override
     public JRubyFile hackyGetJRubyFile() {
         new RuntimeException().printStackTrace();
         return null;
     }
 
     @Override
     public InputStream openInputStream()
     {
         try
         {
             if (pathname != null) {
                 return Thread.currentThread().getContextClassLoader().getResourceAsStream(pathname);
             }
             return url.openStream();
         }
         catch (IOException e)
         {
             return null;
         }
     }
 
     @Override
     public ChannelDescriptor openDescriptor(ModeFlags flags, int perm) throws ResourceException {
         return new ChannelDescriptor(openInputStream(), flags);
     }
 
     public static FileResource createClassloaderURI(String pathname) {
         if (pathname.startsWith("/")) {
             pathname = pathname.substring(1);
         }
         InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathname);
         if (is != null) {
             try
             {
                 is.close();
             }
             // need Exception here due to strange NPE in some cases
             catch (Exception e) {}
         }
         String[] files = listClassLoaderFiles(pathname);
         return new URLResource(URI_CLASSLOADER + pathname,
                                is == null ? null : pathname,
                                files);
     }
 
     public static FileResource create(String pathname)
     {
         if (!pathname.startsWith(URI)) {
             return null;
         }
         pathname = pathname.substring(URI.length());
         if (pathname.startsWith(CLASSLOADER)) {
             return createClassloaderURI(pathname.substring(CLASSLOADER.length()));
         }
         return createRegularURI(pathname);
     }
     
     private static FileResource createRegularURI(String pathname) {
         URL url;
         try
         {
             // TODO NormalizedFile does too much - should leave uri: files as they are
             pathname = pathname.replaceFirst( ":/([^/])", "://$1" );
             url = new URL(pathname);
             // we do not want to deal with those url here like this though they are valid url/uri
             if (url.getProtocol().startsWith("http")){
                 return null;
             }
         }
         catch (MalformedURLException e)
         {
             // file does not exists
             return new URLResource(URI + pathname, (URL)null, null);
         }
         String[] files = listFiles(pathname);
         if (files != null) {
             return new URLResource(URI + pathname, (URL)null, files);
         }
         try {
             url.openStream().close();
             return new URLResource(URI + pathname, url, null);
         }
         catch (IOException e)
         {
             // can not open stream - treat it as not existing file
             return new URLResource(URI + pathname, (URL)null, null);
         }
     }
 
     private static String[] listFilesFromInputStream(InputStream is) {
         BufferedReader reader = null;
         try {
             List<String> files = new LinkedList<String>();
             reader = new BufferedReader(new InputStreamReader(is));
             String line = reader.readLine();
             while (line != null) {
                 files.add(line);
                 line = reader.readLine();
             }
             return files.toArray(new String[files.size()]);
         }
         catch (IOException e) {
             return null;
         }
         finally {
             if (reader != null) {
                 try
                 {
                     reader.close();
                 }
                 catch (IOException ignored)
                 {
                 }
             }
         }
     }
     private static String[] listClassLoaderFiles(String pathname) {
         try
         {
             Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(pathname + "/.jrubydir");
             if (!urls.hasMoreElements()) {
                 return null;
             }
             Set<String> result = new LinkedHashSet<String>();
             while( urls.hasMoreElements() ) {
                 URL url = urls.nextElement();
                 for( String entry: listFilesFromInputStream(url.openStream())) {
                     if (!result.contains(entry)) {
                         result.add(entry);
                     }
                 }
             }
             return result.toArray(new String[result.size()]);
         }
         catch (IOException e)
         {
             return null;
         }
     }
 
     private static String[] listFiles(String pathname) {
         try
         {
             return listFilesFromInputStream(new URL(pathname.replace("file://", "file:/") + "/.jrubydir").openStream());
         }
         catch (IOException e)
         {
             return null;
         }
     }
 
     public static URL getResourceURL(String location)
     {
         if (location.startsWith(URI + CLASSLOADER)){
             return Thread.currentThread().getContextClassLoader().getResource(location.substring(URI_CLASSLOADER.length()));
         }
         try
         {
             return new URL(location.replaceFirst("^" + URI, ""));
         }
         catch (MalformedURLException e)
         {
             throw new RuntimeException("BUG in " + URLResource.class);
         }
     }
     
 }
