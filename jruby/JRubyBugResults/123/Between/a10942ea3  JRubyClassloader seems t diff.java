diff --git a/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java b/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
index 6e24721984..6ba7b0f0ce 100644
--- a/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
+++ b/core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
@@ -1,262 +1,265 @@
 package org.jruby.runtime.load;
 
 import org.jruby.Ruby;
 import org.jruby.RubyHash;
 import org.jruby.RubyString;
 import org.jruby.ast.executable.Script;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.LoadService.SuffixType;
 import org.jruby.util.FileResource;
 import org.jruby.util.JRubyFile;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.MalformedURLException;
 import java.net.URI;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.util.Map;
 
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
 
         for (IRubyObject loadPathEntry : loadService.loadPath.toJavaArray()) {
             String loadPathString = loadPathEntry.convertToString().asJavaString();
             FoundLibrary library = findFileResourceWithLoadPath(baseName, suffix, loadPathString);
             if (library != null) {
                 return library;
             }
         }
 
         return null;
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
 
         return new File(path).isAbsolute();
     }
 
     protected String resolveLoadName(FileResource resource, String ruby18path) {
         return resource.absolutePath();
     }
 
     protected String resolveScriptName(FileResource resource, String ruby18Path) {
         return resource.absolutePath();
     }
 
     static class ResourceLibrary implements Library {
         private final String searchName;
         private final String scriptName;
         private final InputStream is;
         private final String location;
 
         public ResourceLibrary(String searchName, String scriptName, FileResource resource) {
             this.searchName = searchName;
             this.scriptName = scriptName;
             this.location = resource.absolutePath();
 
             // getInputStream may return a null to denote that it cannot really read the resource.
             // We should raise LoadError in the end, but probably only once we actually try to load
             // the library
             this.is = resource.getInputStream();
         }
 
         @Override
         public void load(Ruby runtime, boolean wrap) {
             if (is == null) {
                 throw runtime.newLoadError("no such file to load -- " + searchName, searchName);
             }
 
             if (location.endsWith(".jar")) {
                 loadJar(runtime, wrap);
             } else if (location.endsWith(".class")) {
                 loadClass(runtime, wrap);
             } else {
                 loadScript(runtime, wrap);
             }
         }
 
         private void loadScript(Ruby runtime, boolean wrap) {
             runtime.loadFile(scriptName, is, wrap);
         }
 
         private void loadClass(Ruby runtime, boolean wrap) {
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
                 URL url = new File(location).toURI().toURL();
+                if ( location.contains( "!") ) {
+                    url = new URL( "jar:" + url );
+                }
                 runtime.getJRubyClassLoader().addURL(url);
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
