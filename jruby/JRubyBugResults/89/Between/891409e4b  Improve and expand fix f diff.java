diff --git a/src/org/jruby/RubyEncoding.java b/src/org/jruby/RubyEncoding.java
index 93a62e356f..c2e39a6fe6 100644
--- a/src/org/jruby/RubyEncoding.java
+++ b/src/org/jruby/RubyEncoding.java
@@ -1,429 +1,401 @@
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
 
 import java.nio.ByteBuffer;
 import java.nio.charset.Charset;
 import org.jcodings.Encoding;
 import org.jcodings.EncodingDB.Entry;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jcodings.util.CaseInsensitiveBytesHash;
 import org.jcodings.util.Hash.HashEntryIterator;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
+import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.runtime.encoding.EncodingService;
 import org.jruby.util.ByteList;
 import org.jruby.util.StringSupport;
 import static org.jruby.CompatVersion.*;
 
 @JRubyClass(name="Encoding")
 public class RubyEncoding extends RubyObject {
     public static final Charset UTF8 = Charset.forName("UTF-8");
     public static final ByteList LOCALE = ByteList.create("locale");
     public static final ByteList EXTERNAL = ByteList.create("external");
 
     public static RubyClass createEncodingClass(Ruby runtime) {
         RubyClass encodingc = runtime.defineClass("Encoding", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setEncoding(encodingc);
         encodingc.index = ClassIndex.ENCODING;
         encodingc.setReifiedClass(RubyEncoding.class);
         encodingc.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyEncoding;
             }
         };
 
         encodingc.getSingletonClass().undefineMethod("allocate");
         encodingc.defineAnnotatedMethods(RubyEncoding.class);
 
         return encodingc;
     }
 
     private Encoding encoding;
     private final ByteList name;
     private final boolean isDummy;
 
     private RubyEncoding(Ruby runtime, byte[]name, int p, int end, boolean isDummy) {
         super(runtime, runtime.getEncoding());
         this.name = new ByteList(name, p, end);
         this.isDummy = isDummy;
     }
     
     private RubyEncoding(Ruby runtime, byte[]name, boolean isDummy) {
         this(runtime, name, 0, name.length, isDummy);
     }
 
     private RubyEncoding(Ruby runtime, Encoding encoding) {
         super(runtime, runtime.getEncoding());
         this.name = new ByteList(encoding.getName());
         this.isDummy = false;
         this.encoding = encoding;
     }
 
     public static RubyEncoding newEncoding(Ruby runtime, byte[]name, int p, int end, boolean isDummy) {
         return new RubyEncoding(runtime, name, p, end, isDummy);
     }
 
     public static RubyEncoding newEncoding(Ruby runtime, byte[]name, boolean isDummy) {
         return new RubyEncoding(runtime, name, isDummy);
     }
 
     public static RubyEncoding newEncoding(Ruby runtime, Encoding encoding) {
         return new RubyEncoding(runtime, encoding);
     }
 
     public final Encoding getEncoding() {
         // TODO: make threadsafe
         if (encoding == null) encoding = getRuntime().getEncodingService().loadEncoding(name);
         return encoding;
     }
 
     public static Encoding areCompatible(IRubyObject obj1, IRubyObject obj2) {
         if (obj1 instanceof EncodingCapable && obj2 instanceof EncodingCapable) {
             Encoding enc1 = ((EncodingCapable)obj1).getEncoding();
             Encoding enc2 = ((EncodingCapable)obj2).getEncoding();
 
             if (enc1 == enc2) return enc1;
 
             if (obj2 instanceof RubyString && ((RubyString) obj2).getByteList().getRealSize() == 0) return enc1;
             if (obj1 instanceof RubyString && ((RubyString) obj1).getByteList().getRealSize() == 0) return enc2;
 
             if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
 
             if (!(obj2 instanceof RubyString) && enc2 instanceof USASCIIEncoding) return enc1;
             if (!(obj1 instanceof RubyString) && enc1 instanceof USASCIIEncoding) return enc2;
 
             if(!(obj1 instanceof RubyString)) {
                 IRubyObject objTmp = obj1;
                 obj1 = obj2;
                 obj1 = objTmp;
 
                 Encoding encTmp = enc1;
                 enc1 = enc2;
                 enc2 = encTmp;
             }
 
             if (obj1 instanceof RubyString) {
                 int cr1 = ((RubyString)obj1).scanForCodeRange();
                 if (obj2 instanceof RubyString) {
                     int cr2 = ((RubyString)obj2).scanForCodeRange();
                     return areCompatible(enc1, cr1, enc2, cr2);
                 }
                 if (cr1 == StringSupport.CR_7BIT) return enc2;
             }
         }
         return null;
     }
 
     static Encoding areCompatible(Encoding enc1, int cr1, Encoding enc2, int cr2) {
         if (cr1 != cr2) {
             /* may need to handle ENC_CODERANGE_BROKEN */
             if (cr1 == StringSupport.CR_7BIT) return enc2;
             if (cr2 == StringSupport.CR_7BIT) return enc1;
         }
         if (cr2 == StringSupport.CR_7BIT) {
             if (enc1 instanceof ASCIIEncoding) return enc2;
             return enc1;
         }
         if (cr1 == StringSupport.CR_7BIT) return enc2;
         return null;
     }
 
     public static byte[] encodeUTF8(CharSequence cs) {
         return encode(cs, UTF8);
     }
 
     public static byte[] encodeUTF8(String str) {
         return encode(str, UTF8);
     }
 
     public static byte[] encode(CharSequence cs, Charset charset) {
         ByteBuffer buffer = charset.encode(cs.toString());
         byte[] bytes = new byte[buffer.limit()];
         buffer.get(bytes);
         return bytes;
     }
 
     public static byte[] encode(String str, Charset charset) {
         ByteBuffer buffer = charset.encode(str);
         byte[] bytes = new byte[buffer.limit()];
         buffer.get(bytes);
         return bytes;
     }
 
     public static String decodeUTF8(byte[] bytes, int start, int length) {
         return decode(bytes, start, length, UTF8);
     }
 
     public static String decodeUTF8(byte[] bytes) {
         return decode(bytes, UTF8);
     }
 
     public static String decode(byte[] bytes, int start, int length, Charset charset) {
         return charset.decode(ByteBuffer.wrap(bytes, start, length)).toString();
     }
 
     public static String decode(byte[] bytes, Charset charset) {
         return charset.decode(ByteBuffer.wrap(bytes)).toString();
     }
 
     @JRubyMethod(name = "list", meta = true)
     public static IRubyObject list(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         return RubyArray.newArrayNoCopy(runtime, runtime.getEncodingService().getEncodingList(), 0);
     }
 
     @JRubyMethod(name = "locale_charmap", meta = true)
     public static IRubyObject locale_charmap(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         EncodingService service = runtime.getEncodingService();
         ByteList name = new ByteList(service.getLocaleEncoding().getName());
         
         return RubyString.newUsAsciiStringNoCopy(runtime, name);
     }
 
     @SuppressWarnings("unchecked")
     @JRubyMethod(name = "name_list", meta = true)
     public static IRubyObject name_list(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         EncodingService service = runtime.getEncodingService();
         
         RubyArray result = runtime.newArray(service.getEncodings().size() + service.getAliases().size());
         HashEntryIterator i;
         i = service.getEncodings().entryIterator();
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
         }
         i = service.getAliases().entryIterator();        
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
         }
 
         result.append(runtime.newString(EXTERNAL));
         result.append(runtime.newString(LOCALE));
         
         return result;
     }
 
     @SuppressWarnings("unchecked")
     @JRubyMethod(name = "aliases", meta = true)
     public static IRubyObject aliases(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         EncodingService service = runtime.getEncodingService();
 
         IRubyObject list[] = service.getEncodingList();
         HashEntryIterator i = service.getAliases().entryIterator();
         RubyHash result = RubyHash.newHash(runtime);
 
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             IRubyObject alias = RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context);
             IRubyObject name = RubyString.newUsAsciiStringShared(runtime, 
                                 ((RubyEncoding)list[e.value.getIndex()]).name).freeze(context);
             result.fastASet(alias, name);
         }
 
         result.fastASet(runtime.newString(EXTERNAL),
                 runtime.newString(new ByteList(runtime.getDefaultExternalEncoding().getName())));
         result.fastASet(runtime.newString(LOCALE),
                 runtime.newString(new ByteList(service.getLocaleEncoding().getName())));
 
         return result;
     }
 
-    private static IRubyObject findWithError(Ruby runtime, ByteList name) {
-        EncodingService service = runtime.getEncodingService();
-        Entry e = service.findEncodingOrAliasEntry(name);
-
-        if (e == null) throw runtime.newArgumentError("unknown encoding name - " + name);
-
-        return service.getEncodingList()[e.getIndex()];
-    }
-
     @JRubyMethod(name = "find", meta = true)
     public static IRubyObject find(ThreadContext context, IRubyObject recv, IRubyObject str) {
         Ruby runtime = context.getRuntime();
 
-        // TODO: check for ascii string
-        ByteList name = str.convertToString().getByteList();
-        if (name.caseInsensitiveCmp(LOCALE) == 0) {
-            name = new ByteList(runtime.getEncodingService().getLocaleEncoding().getName());
-        } else if (name.caseInsensitiveCmp(EXTERNAL) == 0) {
-            name = new ByteList(runtime.getDefaultExternalEncoding().getName());
-        }
-
-        return findWithError(runtime, name);
+        return runtime.getEncodingService().rubyEncodingFromObject(str);
     }
 
     @JRubyMethod(name = "_dump")
     public IRubyObject _dump(ThreadContext context, IRubyObject arg) {
         return to_s(context);
     }
 
     @JRubyMethod(name = "_load", meta = true)
     public static IRubyObject _load(ThreadContext context, IRubyObject recv, IRubyObject str) {
         return find(context, recv, str);
     }
 
     @JRubyMethod(name = "ascii_compatible?")
     public IRubyObject asciiCompatible_p(ThreadContext context) {
         return context.getRuntime().newBoolean(getEncoding().isAsciiCompatible());
     }
 
     @JRubyMethod(name = {"to_s", "name"})
     public IRubyObject to_s(ThreadContext context) {
         // TODO: rb_usascii_str_new2
         return RubyString.newUsAsciiStringShared(context.getRuntime(), name);
     }
 
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect(ThreadContext context) {
         ByteList bytes = new ByteList();
         bytes.append("#<Encoding:".getBytes());
         bytes.append(name);
         if (isDummy) bytes.append(" (dummy)".getBytes());
         bytes.append('>');
         return RubyString.newUsAsciiStringNoCopy(context.getRuntime(), bytes);
     }
 
     @SuppressWarnings("unchecked")
     @JRubyMethod(name = "names")
     public IRubyObject names(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         EncodingService service = runtime.getEncodingService();
         Entry entry = service.findEncodingOrAliasEntry(name);
 
         RubyArray result = runtime.newArray();
         HashEntryIterator i;
         i = service.getEncodings().entryIterator();
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             if (e.value == entry) {
                 result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
             }
         }
         i = service.getAliases().entryIterator();        
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             if (e.value == entry) {
                 result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
             }
         }
         result.append(runtime.newString(EXTERNAL));
         result.append(runtime.newString(LOCALE));
         
         return result;
     }
 
     @JRubyMethod(name = "dummy?")
     public IRubyObject dummy_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isDummy);
     }
 
     @JRubyMethod(name = "compatible?", meta = true)
     public static IRubyObject compatible_p(ThreadContext context, IRubyObject self, IRubyObject first, IRubyObject second) {
         Ruby runtime = context.getRuntime();
         Encoding enc = areCompatible(first, second);
 
         return enc == null ? runtime.getNil() : runtime.getEncodingService().getEncoding(enc);
     }
 
     @JRubyMethod(name = "default_external", meta = true, compat = RUBY1_9)
     public static IRubyObject getDefaultExternal(IRubyObject recv) {
-        return getDefaultExternal(recv.getRuntime());
-    }
-
-    public static IRubyObject getDefaultExternal(Ruby runtime) {
-        IRubyObject defaultExternal = convertEncodingToRubyEncoding(runtime, runtime.getDefaultExternalEncoding());
-
-        if (defaultExternal.isNil()) {
-            // TODO: MRI seems to default blindly to US-ASCII and we were using Charset default from Java...which is right?
-            ByteList encodingName = ByteList.create("US-ASCII");
-            Encoding encoding = runtime.getEncodingService().loadEncoding(encodingName);
-
-            runtime.setDefaultExternalEncoding(encoding);
-            defaultExternal = convertEncodingToRubyEncoding(runtime, encoding);
-        }
-
-        return defaultExternal;
+        return recv.getRuntime().getEncodingService().getDefaultExternal();
     }
 
     @JRubyMethod(name = "default_external=", meta = true, compat = RUBY1_9)
     public static void setDefaultExternal(IRubyObject recv, IRubyObject encoding) {
+        Ruby runtime = recv.getRuntime();
+        EncodingService service = runtime.getEncodingService();
         if (encoding.isNil()) {
             throw recv.getRuntime().newArgumentError("default_external can not be nil");
         }
-        recv.getRuntime().setDefaultExternalEncoding(getEncodingFromObject(recv.getRuntime(), encoding));
+        runtime.setDefaultExternalEncoding(service.getEncodingFromObject(encoding));
     }
 
     @JRubyMethod(name = "default_internal", meta = true, compat = RUBY1_9)
     public static IRubyObject getDefaultInternal(IRubyObject recv) {
-        return getDefaultInternal(recv.getRuntime());
-    }
-
-    public static IRubyObject getDefaultInternal(Ruby runtime) {
-        return convertEncodingToRubyEncoding(runtime, runtime.getDefaultInternalEncoding());
+        return recv.getRuntime().getEncodingService().getDefaultInternal();
     }
 
     @JRubyMethod(name = "default_internal=", required = 1, meta = true, compat = RUBY1_9)
     public static void setDefaultInternal(IRubyObject recv, IRubyObject encoding) {
+        Ruby runtime = recv.getRuntime();
+        EncodingService service = runtime.getEncodingService();
         if (encoding.isNil()) {
             recv.getRuntime().newArgumentError("default_internal can not be nil");
         }
-        recv.getRuntime().setDefaultInternalEncoding(getEncodingFromObject(recv.getRuntime(), encoding));
+        recv.getRuntime().setDefaultInternalEncoding(service.getEncodingFromObject(encoding));
+    }
+
+    @Deprecated
+    public static IRubyObject getDefaultExternal(Ruby runtime) {
+        return runtime.getEncodingService().getDefaultExternal();
+    }
+
+    @Deprecated
+    public static IRubyObject getDefaultInternal(Ruby runtime) {
+        return runtime.getEncodingService().getDefaultInternal();
     }
 
+    @Deprecated
     public static IRubyObject convertEncodingToRubyEncoding(Ruby runtime, Encoding defaultEncoding) {
-        return defaultEncoding != null ?
-            runtime.getEncodingService().getEncoding(defaultEncoding) : runtime.getNil();
+        return runtime.getEncodingService().convertEncodingToRubyEncoding(defaultEncoding);
     }
 
+    @Deprecated
     public static Encoding getEncodingFromObject(Ruby runtime, IRubyObject arg) {
-        if (arg == null) return null;
-
-        Encoding encoding = null;
-        if (arg instanceof RubyEncoding) {
-            encoding = ((RubyEncoding) arg).getEncoding();
-        } else if (!arg.isNil()) {
-            encoding = arg.convertToString().toEncoding(runtime);
-        }
-        return encoding;
+        return runtime.getEncodingService().getEncodingFromObject(arg);
     }
 }
diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 4d19db0274..64403a931c 100755
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -79,1802 +79,1802 @@ import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 import static org.jruby.CompatVersion.*;
 
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
 
     private static int _cachedUmask = 0;
     private static final Object _umaskLock = new Object();
 
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
 
     public void setEncoding(Encoding encoding) {
         // :)
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
             this.openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(in))));
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         }
         this.openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
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
 
         // For JRUBY-5276, physically define FileTest methods on File's singleton
         fileClass.getSingletonClass().defineAnnotatedMethods(RubyFileTest.FileTestFileMethods.class);
         
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
             } catch (java.nio.channels.OverlappingFileLockException ioe) {
                 if (context.getRuntime().getDebug().isTrue()) {
                     ioe.printStackTrace(System.err);
                 }
             }
             return (lockMode & LOCK_EX) == 0 ? RubyFixnum.zero(context.getRuntime()) : context.getRuntime().getFalse();
         } else {
             // We're not actually a real file, so we can't flock
             return context.getRuntime().getFalse();
         }
     }
 
     @JRubyMethod(required = 1, optional = 2, visibility = PRIVATE, compat = RUBY1_8)
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
 
     @JRubyMethod(name = "initialize", required = 1, optional = 2, visibility = PRIVATE, compat = RUBY1_9)
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
         Ruby runtime = context.getRuntime();
         RubyString filename = get_path(context, args[0]);
         runtime.checkSafeString(filename);
 
         path = adjustRootPathOnWindows(runtime, filename.getUnicodeValue(), runtime.getCurrentDirectory());
 
         String modeString = "r";
         ModeFlags modes = new ModeFlags();
         int perm = 0;
 
         try {
             if (args.length > 1) {
                 modes = parseModes19(context, args[1]);
                 if (args[1] instanceof RubyFixnum) {
                     perm = getFilePermissions(args);
                 } else {
                     modeString = args[1].convertToString().toString();
                 }
             } else {
                 modes = parseModes19(context, RubyString.newString(runtime, modeString));
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
             throw runtime.newErrnoEINVALError();
         }
 
         return this;
     }
     
     private IRubyObject openFile(IRubyObject args[]) {
         Ruby runtime = getRuntime();
         RubyString filename = get_path(runtime.getCurrentContext(), args[0]);
         runtime.checkSafeString(filename);
         
         path = adjustRootPathOnWindows(runtime, filename.getUnicodeValue(), runtime.getCurrentDirectory());
         
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
 
         int umask = getUmaskSafe( getRuntime() );
         perm = perm - (perm & umask);
         
         ChannelDescriptor descriptor = sysopen(path, modes, perm);
         openFile.setMainStream(fdopen(descriptor, modes));
     }
 
     protected void openInternal(String path, String modeString, ModeFlags modes) throws InvalidValueException {
         openFile = new OpenFile();
 
         openFile.setMode(modes.getOpenFileFlags());
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modeString));
     }
     
     protected void openInternal(String path, String modeString) throws InvalidValueException {
         openFile = new OpenFile();
 
         openFile.setMode(getIOModes(getRuntime(), modeString).getOpenFileFlags());
         openFile.setPath(path);
         openFile.setMainStream(fopen(path, modeString));
     }
     
     private ChannelDescriptor sysopen(String path, ModeFlags modes, int perm) throws InvalidValueException {
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
         checkClosed(context);
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw context.getRuntime().newErrnoENOENTError(path);
         }
 
         return context.getRuntime().newFixnum(context.getRuntime().getPosix().chmod(path, mode));
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
             throw context.getRuntime().newErrnoENOENTError(path);
         }
 
         return context.getRuntime().newFixnum(context.getRuntime().getPosix().chown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject atime(ThreadContext context) {
         checkClosed(context);
         return context.getRuntime().newFileStat(path, false).atime();
     }
 
     @JRubyMethod
     public IRubyObject ctime(ThreadContext context) {
         checkClosed(context);
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
         checkClosed(context);
         return context.getRuntime().newFileStat(path, true);
     }
     
     @JRubyMethod
     public IRubyObject mtime(ThreadContext context) {
         checkClosed(context);
         return getLastModified(context.getRuntime(), path);
     }
 
     @JRubyMethod(meta = true, compat = RUBY1_9)
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
             RubyString pathStr = get_path(runtime.getCurrentContext(), pathOrFile);
             String path = pathStr.getUnicodeValue();
             String[] pathParts = splitURI(path);
             if (pathParts != null && pathParts[0].startsWith("file:")) {
                 path = pathParts[1];
             }
             return JRubyFile.create(runtime.getCurrentDirectory(), path);
         }
     }
 
     @JRubyMethod(name = {"path", "to_path"})
     public IRubyObject path(ThreadContext context) {
         IRubyObject newPath = context.getRuntime().getNil();
         if (path != null) {
             newPath = context.getRuntime().newString(path);
             newPath.setTaint(true);
         }
         return newPath;
     }
 
     @JRubyMethod
     @Override
     public IRubyObject stat(ThreadContext context) {
         checkClosed(context);
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
     @JRubyMethod(required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject expand_path(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, true);
     }
 
     @JRubyMethod(name = "expand_path", required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject expand_path19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         RubyString path = (RubyString) expandPathInternal(context, recv, args, true);
-        path.force_encoding(context, RubyEncoding.getDefaultExternal(context.getRuntime()));
+        path.force_encoding(context, context.getRuntime().getEncodingService().getDefaultExternal());
 
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
             throw context.getRuntime().newErrnoENOENTError(file.toString());
         }
         return file;
     }
 
     private static IRubyObject expandPathInternal(ThreadContext context, IRubyObject recv, IRubyObject[] args, boolean expandUser) {
         Ruby runtime = context.getRuntime();
 
         String relativePath = get_path(context, args[0]).getUnicodeValue();
         String[] uriParts = splitURI(relativePath);
         String cwd = null;
 
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
 
     private static Pattern URI_PREFIX = Pattern.compile("^[a-z]{2,}:");
     public static String[] splitURI(String path) {
         Matcher m = URI_PREFIX.matcher(path);
         if (m.find()) {
             try {
                 URI u = new URI(path);
                 String pathPart = u.getPath();
                 return new String[] {path.substring(0, path.indexOf(pathPart)), pathPart};
             } catch (Exception e) {
                 try {
                     URL u = new URL(path);
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
 
     private static final String[] SLASHES = {"", "/", "//"};
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
 
     @JRubyMethod(name = "size", backtrace = true, compat = RUBY1_9)
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
 
     private void checkClosed(ThreadContext context) {
         openFile.checkClosed(context.getRuntime());
     }
 }
diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index eccbc8841d..ee6cd3b629 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -56,2001 +56,2001 @@ import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jcodings.Encoding;
 import org.jruby.anno.FrameField;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.libraries.FcntlLibrary;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.Stream;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.DirectoryAsFileException;
 import org.jruby.util.io.STDIO;
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.ChannelDescriptor;
 
 import org.jruby.util.io.SelectorFactory;
 import java.nio.channels.spi.SelectorProvider;
 import java.util.Arrays;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.javasupport.util.RuntimeHelpers;
 
 import static org.jruby.CompatVersion.*;
 import static org.jruby.RubyEnumerator.enumeratorize;
 
 /**
  * 
  * @author jpetersen
  */
 @JRubyClass(name="IO", include="Enumerable")
 public class RubyIO extends RubyObject {
     // This should only be called by this and RubyFile.
     // It allows this object to be created without a IOHandler.
     public RubyIO(Ruby runtime, RubyClass type) {
         super(runtime, type);
         
         openFile = new OpenFile();
     }
 
     public RubyIO(Ruby runtime, OutputStream outputStream) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (outputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(outputStream))));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.WRITABLE | OpenFile.APPEND);
     }
     
     public RubyIO(Ruby runtime, InputStream inputStream) {
         super(runtime, runtime.getIO());
         
         if (inputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(inputStream))));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.READABLE);
     }
     
     public RubyIO(Ruby runtime, Channel channel) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (channel == null) {
             throw runtime.newRuntimeError("Opening null channelpo");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(channel)));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
     }
 
     public RubyIO(Ruby runtime, ShellLauncher.POpenProcess process, ModeFlags modes) {
     	super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         
         openFile.setMode(modes.getOpenFileFlags() | OpenFile.SYNC);
         openFile.setProcess(process);
 
         try {
             if (openFile.isReadable()) {
                 Channel inChannel;
                 if (process.getInput() != null) {
                     // NIO-based
                     inChannel = process.getInput();
                 } else {
                     // Stream-based
                     inChannel = Channels.newChannel(process.getInputStream());
                 }
                 
                 ChannelDescriptor main = new ChannelDescriptor(
                         inChannel);
                 main.setCanBeSeekable(false);
                 
                 openFile.setMainStream(ChannelStream.open(getRuntime(), main));
             }
             
             if (openFile.isWritable() && process.hasOutput()) {
                 Channel outChannel;
                 if (process.getOutput() != null) {
                     // NIO-based
                     outChannel = process.getOutput();
                 } else {
                     outChannel = Channels.newChannel(process.getOutputStream());
                 }
 
                 ChannelDescriptor pipe = new ChannelDescriptor(
                         outChannel);
                 pipe.setCanBeSeekable(false);
                 
                 if (openFile.getMainStream() != null) {
                     openFile.setPipeStream(ChannelStream.open(getRuntime(), pipe));
                 } else {
                     openFile.setMainStream(ChannelStream.open(getRuntime(), pipe));
                 }
             }
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
     
     public RubyIO(Ruby runtime, STDIO stdio) {
         super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         ChannelDescriptor descriptor;
 
         try {
             switch (stdio) {
             case IN:
                 // special constructor that accepts stream, not channel
                 descriptor = new ChannelDescriptor(runtime.getIn(), new ModeFlags(ModeFlags.RDONLY), FileDescriptor.in);
                 runtime.putFilenoMap(0, descriptor.getFileno());
                 openFile.setMainStream(
                         ChannelStream.open(
                             runtime, 
                             descriptor));
                 break;
             case OUT:
                 descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getOut()), new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out);
                 runtime.putFilenoMap(1, descriptor.getFileno());
                 openFile.setMainStream(
                         ChannelStream.open(
                             runtime, 
                             descriptor));
                 openFile.getMainStream().setSync(true);
                 break;
             case ERR:
                 descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getErr()), new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.err);
                 runtime.putFilenoMap(2, descriptor.getFileno());
                 openFile.setMainStream(
                         ChannelStream.open(
                             runtime, 
                             descriptor));
                 openFile.getMainStream().setSync(true);
                 break;
             }
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         // never autoclose stdio streams
         openFile.setAutoclose(false);
     }
     
     public static RubyIO newIO(Ruby runtime, Channel channel) {
         return new RubyIO(runtime, channel);
     }
     
     public OpenFile getOpenFile() {
         return openFile;
     }
     
     protected OpenFile getOpenFileChecked() {
         openFile.checkClosed(getRuntime());
         return openFile;
     }
     
     private static ObjectAllocator IO_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIO(runtime, klass);
         }
     };
 
     /*
      * We use FILE versus IO to match T_FILE in MRI.
      */
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.FILE;
     }
 
     public static RubyClass createIOClass(Ruby runtime) {
         RubyClass ioClass = runtime.defineClass("IO", runtime.getObject(), IO_ALLOCATOR);
 
         ioClass.index = ClassIndex.IO;
         ioClass.setReifiedClass(RubyIO.class);
 
         ioClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyIO;
             }
         };
 
         ioClass.includeModule(runtime.getEnumerable());
         
         // TODO: Implement tty? and isatty.  We have no real capability to
         // determine this from java, but if we could set tty status, then
         // we could invoke jruby differently to allow stdin to return true
         // on this.  This would allow things like cgi.rb to work properly.
         
         ioClass.defineAnnotatedMethods(RubyIO.class);
 
         // Constants for seek
         ioClass.fastSetConstant("SEEK_SET", runtime.newFixnum(Stream.SEEK_SET));
         ioClass.fastSetConstant("SEEK_CUR", runtime.newFixnum(Stream.SEEK_CUR));
         ioClass.fastSetConstant("SEEK_END", runtime.newFixnum(Stream.SEEK_END));
 
         if (runtime.is1_9()) {
             ioClass.defineModuleUnder("WaitReadable");
             ioClass.defineModuleUnder("WaitWritable");
         }
 
         return ioClass;
     }
 
     public OutputStream getOutStream() {
         return getHandler().newOutputStream();
     }
 
     public InputStream getInStream() {
         return getHandler().newInputStream();
     }
 
     public Channel getChannel() {
         return getHandler().getChannel();
     }
     
     public Stream getHandler() {
         return getOpenFileChecked().getMainStream();
     }
 
     protected void reopenPath(Ruby runtime, IRubyObject[] args) {
         if (runtime.is1_9() && !(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             args[0] = args[0].callMethod(runtime.getCurrentContext(), "to_path");
         }
         IRubyObject pathString = args[0].convertToString();
 
         // TODO: check safe, taint on incoming string
 
         try {
             ModeFlags modes;
             if (args.length > 1) {
                 IRubyObject modeString = args[1].convertToString();
                 modes = getIOModes(runtime, modeString.toString());
 
                 openFile.setMode(modes.getOpenFileFlags());
             } else {
                 modes = getIOModes(runtime, "r");
             }
 
             String path = pathString.toString();
 
             // Ruby code frequently uses a platform check to choose "NUL:" on windows
             // but since that check doesn't work well on JRuby, we help it out
 
             openFile.setPath(path);
 
             if (openFile.getMainStream() == null) {
                 try {
                     openFile.setMainStream(ChannelStream.fopen(runtime, path, modes));
                 } catch (FileExistsException fee) {
                     throw runtime.newErrnoEEXISTError(path);
                 }
 
                 if (openFile.getPipeStream() != null) {
                     openFile.getPipeStream().fclose();
                     openFile.setPipeStream(null);
                 }
             } else {
                 // TODO: This is an freopen in MRI, this is close, but not quite the same
                 openFile.getMainStream().freopen(runtime, path, getIOModes(runtime, openFile.getModeAsString(runtime)));
                 
                 if (openFile.getPipeStream() != null) {
                     // TODO: pipe handler to be reopened with path and "w" mode
                 }
             }
         } catch (PipeException pe) {
             throw runtime.newErrnoEPIPEError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     protected void reopenIO(Ruby runtime, RubyIO ios) {
         try {
             if (ios.openFile == this.openFile) return;
 
             OpenFile originalFile = ios.getOpenFileChecked();
             OpenFile selfFile = getOpenFileChecked();
 
             long pos = 0;
             if (originalFile.isReadable()) {
                 pos = originalFile.getMainStream().fgetpos();
             }
 
             if (originalFile.getPipeStream() != null) {
                 originalFile.getPipeStream().fflush();
             } else if (originalFile.isWritable()) {
                 originalFile.getMainStream().fflush();
             }
 
             if (selfFile.isWritable()) {
                 selfFile.getWriteStream().fflush();
             }
 
             selfFile.setMode(originalFile.getMode());
             selfFile.setProcess(originalFile.getProcess());
             selfFile.setLineNumber(originalFile.getLineNumber());
             selfFile.setPath(originalFile.getPath());
             selfFile.setFinalizer(originalFile.getFinalizer());
 
             ChannelDescriptor selfDescriptor = selfFile.getMainStream().getDescriptor();
             ChannelDescriptor originalDescriptor = originalFile.getMainStream().getDescriptor();
 
             // confirm we're not reopening self's channel
             if (selfDescriptor.getChannel() != originalDescriptor.getChannel()) {
                 // check if we're a stdio IO, and ensure we're not badly mutilated
                 if (runtime.getFileno(selfDescriptor) >= 0 && runtime.getFileno(selfDescriptor) <= 2) {
                     selfFile.getMainStream().clearerr();
 
                     // dup2 new fd into self to preserve fileno and references to it
                     originalDescriptor.dup2Into(selfDescriptor);
                 } else {
                     Stream pipeFile = selfFile.getPipeStream();
                     int mode = selfFile.getMode();
                     selfFile.getMainStream().fclose();
                     selfFile.setPipeStream(null);
 
                     // TODO: turn off readable? am I reading this right?
                     // This only seems to be used while duping below, since modes gets
                     // reset to actual modes afterward
                     //fptr->mode &= (m & FMODE_READABLE) ? ~FMODE_READABLE : ~FMODE_WRITABLE;
 
                     if (pipeFile != null) {
                         selfFile.setMainStream(ChannelStream.fdopen(runtime, originalDescriptor, new ModeFlags()));
                         selfFile.setPipeStream(pipeFile);
                     } else {
                         // only use internal fileno here, stdio is handled above
                         selfFile.setMainStream(
                                 ChannelStream.open(
                                 runtime,
                                 originalDescriptor.dup2(selfDescriptor.getFileno())));
 
                         // since we're not actually duping the incoming channel into our handler, we need to
                         // copy the original sync behavior from the other handler
                         selfFile.getMainStream().setSync(selfFile.getMainStream().isSync());
                     }
                     selfFile.setMode(mode);
                 }
 
                 // TODO: anything threads attached to original fd are notified of the close...
                 // see rb_thread_fd_close
 
                 if (originalFile.isReadable() && pos >= 0) {
                     selfFile.seek(pos, Stream.SEEK_SET);
                     originalFile.seek(pos, Stream.SEEK_SET);
                 }
             }
 
             // only use internal fileno here, stdio is handled above
             if (selfFile.getPipeStream() != null && selfDescriptor.getFileno() != selfFile.getPipeStream().getDescriptor().getFileno()) {
                 int fd = selfFile.getPipeStream().getDescriptor().getFileno();
 
                 if (originalFile.getPipeStream() == null) {
                     selfFile.getPipeStream().fclose();
                     selfFile.setPipeStream(null);
                 } else if (fd != originalFile.getPipeStream().getDescriptor().getFileno()) {
                     selfFile.getPipeStream().fclose();
                     ChannelDescriptor newFD2 = originalFile.getPipeStream().getDescriptor().dup2(fd);
                     selfFile.setPipeStream(ChannelStream.fdopen(runtime, newFD2, getIOModes(runtime, "w")));
                 }
             }
 
             // TODO: restore binary mode
             //            if (fptr->mode & FMODE_BINMODE) {
             //                rb_io_binmode(io);
             //            }
 
             // TODO: set our metaclass to target's class (i.e. scary!)
 
         } catch (IOException ex) { // TODO: better error handling
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (BadDescriptorException ex) {
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (PipeException ex) {
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (InvalidValueException ive) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     @JRubyMethod(name = "reopen", required = 1, optional = 1)
     public IRubyObject reopen(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
     	IRubyObject tmp = TypeConverter.convertToTypeWithCheck(args[0], runtime.getIO(), "to_io");
         
     	if (!tmp.isNil()) {
             reopenIO(runtime, (RubyIO) tmp);
         } else {
             reopenPath(runtime, args);
         }
         
         return this;
     }
     
     public static ModeFlags getIOModes(Ruby runtime, String modesString) throws InvalidValueException {
         return new ModeFlags(getIOModesIntFromString(runtime, modesString));
     }
         
     public static int getIOModesIntFromString(Ruby runtime, String modesString) {
         int modes = 0;
         int length = modesString.length();
 
         if (length == 0) {
             throw runtime.newArgumentError("illegal access mode");
         }
 
         switch (modesString.charAt(0)) {
         case 'r' :
             modes |= ModeFlags.RDONLY;
             break;
         case 'a' :
             modes |= ModeFlags.APPEND | ModeFlags.WRONLY | ModeFlags.CREAT;
             break;
         case 'w' :
             modes |= ModeFlags.WRONLY | ModeFlags.TRUNC | ModeFlags.CREAT;
             break;
         default :
             throw runtime.newArgumentError("illegal access mode " + modesString);
         }
 
         ModifierLoop: for (int n = 1; n < length; n++) {
             switch (modesString.charAt(n)) {
             case 'b':
                 modes |= ModeFlags.BINARY;
                 break;
             case '+':
                 modes = (modes & ~ModeFlags.ACCMODE) | ModeFlags.RDWR;
                 break;
             case 't' :
                 // FIXME: add text mode to mode flags
                 break;
             case ':':
                 break ModifierLoop;
             default:
                 throw runtime.newArgumentError("illegal access mode " + modesString);
             }
         }
 
         return modes;
     }
 
     /*
      * Ensure that separator is valid otherwise give it the default paragraph separator.
      */
     private static ByteList separator(Ruby runtime) {
         return separator(runtime.getRecordSeparatorVar().get());
     }
 
     private static ByteList separator(IRubyObject separatorValue) {
         ByteList separator = separatorValue.isNil() ? null :
             separatorValue.convertToString().getByteList();
 
         if (separator != null && separator.getRealSize() == 0) separator = Stream.PARAGRAPH_DELIMETER;
 
         return separator;
     }
 
     private static ByteList getSeparatorFromArgs(Ruby runtime, IRubyObject[] args, int idx) {
         return separator(args.length > idx ? args[idx] : runtime.getRecordSeparatorVar().get());
     }
 
     private ByteList getSeparatorForGets(Ruby runtime, IRubyObject[] args) {
         return getSeparatorFromArgs(runtime, args, 0);
     }
 
     private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {
         return getline(runtime, separator, -1, cache);
     }
 
     public IRubyObject getline(Ruby runtime, ByteList separator) {
         return getline(runtime, separator, -1, null);
     }
 
 
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     public IRubyObject getline(Ruby runtime, ByteList separator, long limit) {
         return getline(runtime, separator, limit, null);
     }
     
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     private IRubyObject getline(Ruby runtime, ByteList separator, long limit, ByteListCache cache) {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             boolean isParagraph = separator == Stream.PARAGRAPH_DELIMETER;
             separator = isParagraph ? Stream.PARAGRAPH_SEPARATOR : separator;
             
             if (isParagraph) swallow('\n');
             
             if (separator == null && limit < 0) {
                 RubyString str = readAll();
                 if (str.getByteList().length() == 0) {
                     return runtime.getNil();
                 }
                 incrementLineno(runtime, myOpenFile);
                 return str;
             } else if (limit == 0) {
                 return RubyString.newEmptyString(runtime, externalEncoding);
             } else if (separator.length() == 1 && limit < 0) {
                 return getlineFast(runtime, separator.get(0) & 0xFF, cache);
             } else {
                 Stream readStream = myOpenFile.getMainStream();
                 int c = -1;
                 int n = -1;
                 int newline = separator.get(separator.length() - 1) & 0xFF;
 
                 ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
                 try {
                     boolean update = false;
                     boolean limitReached = false;
 
                     while (true) {
                         do {
                             readCheck(readStream);
                             readStream.clearerr();
 
                             try {
                                 runtime.getCurrentContext().getThread().beforeBlockingCall();
                                 if (limit == -1) {
                                     n = readStream.getline(buf, (byte) newline);
                                 } else {
                                     n = readStream.getline(buf, (byte) newline, limit);
                                     limit -= n;
                                     if (limit <= 0) {
                                         update = limitReached = true;
                                         break;
                                     }
                                 }
 
                                 c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                             } catch (EOFException e) {
                                 n = -1;
                             } finally {
                                 runtime.getCurrentContext().getThread().afterBlockingCall();
                             }
                             
                             // CRuby checks ferror(f) and retry getc for
                             // non-blocking IO.
                             if (n == 0) {
                                 waitReadable(readStream);
                                 continue;
                             } else if (n == -1) {
                                 break;
                             }
 
                             update = true;
                         } while (c != newline); // loop until we see the nth separator char
 
                         // if we hit EOF or reached limit then we're done
                         if (n == -1 || limitReached) break;
 
                         // if we've found the last char of the separator,
                         // and we've found at least as many characters as separator length,
                         // and the last n characters of our buffer match the separator, we're done
                         if (c == newline && buf.length() >= separator.length() &&
                                 0 == ByteList.memcmp(buf.getUnsafeBytes(), buf.getBegin() + buf.getRealSize() - separator.length(), separator.getUnsafeBytes(), separator.getBegin(), separator.getRealSize())) {
                             break;
                         }
                     }
                     
                     if (isParagraph && c != -1) swallow('\n');
                     if (!update) return runtime.getNil();
 
                     incrementLineno(runtime, myOpenFile);
 
                     return makeString(runtime, buf, cache != null);
                 }
                 finally {
                     if (cache != null) cache.release(buf);
                 }
             }
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (EOFException e) {
             return runtime.getNil();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     private Encoding getExternalEncoding(Ruby runtime) {
         return externalEncoding != null ? externalEncoding : runtime.getDefaultExternalEncoding();
     }
 
     private RubyString makeString(Ruby runtime, ByteList buffer, boolean isCached) {
         ByteList newBuf = isCached ? new ByteList(buffer) : buffer;
         Encoding encoding = getExternalEncoding(runtime);
 
         if (encoding != null) newBuf.setEncoding(encoding);
 
         RubyString str = RubyString.newString(runtime, newBuf);
         str.setTaint(true);
 
         return str;
     }
 
     private void incrementLineno(Ruby runtime, OpenFile myOpenFile) {
         int lineno = myOpenFile.getLineNumber() + 1;
         myOpenFile.setLineNumber(lineno);
         runtime.setCurrentLine(lineno);
         RubyArgsFile.setCurrentLineNumber(runtime.getArgsFile(), lineno);
     }
 
     protected boolean swallow(int term) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStream();
         int c;
         
         do {
             readCheck(readStream);
             
             try {
                 c = readStream.fgetc();
             } catch (EOFException e) {
                 c = -1;
             }
             
             if (c != term) {
                 readStream.ungetc(c);
                 return true;
             }
         } while (c != -1);
         
         return false;
     }
     
     private static String vendor;
     static { String v = SafePropertyAccessor.getProperty("java.vendor") ; vendor = (v == null) ? "" : v; };
     private static String msgEINTR = "Interrupted system call";
 
     public static boolean restartSystemCall(Exception e) {
         return vendor.startsWith("Apple") && e.getMessage().equals(msgEINTR);
     }
     
     private IRubyObject getlineFast(Ruby runtime, int delim, ByteListCache cache) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStream();
         int c = -1;
 
         ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
         try {
             boolean update = false;
             do {
                 readCheck(readStream);
                 readStream.clearerr();
                 int n;
                 try {
                     runtime.getCurrentContext().getThread().beforeBlockingCall();
                     n = readStream.getline(buf, (byte) delim);
                     c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                 } catch (EOFException e) {
                     n = -1;
                 } finally {
                     runtime.getCurrentContext().getThread().afterBlockingCall();
                 }
 
                 // CRuby checks ferror(f) and retry getc for non-blocking IO.
                 if (n == 0) {
                     waitReadable(readStream);
                     continue;
                 } else if (n == -1) {
                     break;
                 }
                 
                 update = true;
             } while (c != delim);
 
             if (!update) return runtime.getNil();
                 
             incrementLineno(runtime, openFile);
 
             return makeString(runtime, buf, cache != null);
         } finally {
             if (cache != null) cache.release(buf);
         }
     }
     // IO class methods.
 
     @JRubyMethod(name = {"new", "for_fd"}, rest = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass)recv;
         
         if (block.isGiven()) {
             String className = klass.getName();
             context.getRuntime().getWarnings().warn(
                     ID.BLOCK_NOT_ACCEPTED,
                     className + "::new() does not take block; use " + className + "::open() instead",
                     className + "::open()");
         }
         
         return klass.newInstance(context, args, block);
     }
 
     private IRubyObject initializeCommon19(int fileno, ModeFlags modes) {
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(getRuntime().getFilenoExtMap(fileno));
 
             if (descriptor == null) throw getRuntime().newErrnoEBADFError();
 
             descriptor.checkOpen();
 
             if (modes == null) modes = descriptor.getOriginalModes();
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(getRuntime(), false);
             }
 
             openFile.setMode(modes.getOpenFileFlags());
             openFile.setMainStream(fdopen(descriptor, modes));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ive) {
             throw getRuntime().newErrnoEINVALError();
         }
 
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, Block unusedBlock) {
         return initializeCommon19(RubyNumeric.fix2int(fileNumber), null);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject second, Block unusedBlock) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         ModeFlags modes;
         if (second instanceof RubyHash) {
             modes = parseOptions(context, second, null);
         } else {
             modes = parseModes19(context, second);
         }
 
         return initializeCommon19(fileno, modes);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject modeValue, IRubyObject options, Block unusedBlock) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         ModeFlags modes = parseModes19(context, modeValue);
 
         modes = parseOptions(context, options, modes);
         return initializeCommon19(fileno, modes);
     }
 
     protected ModeFlags parseModes(IRubyObject arg) {
         try {
             if (arg instanceof RubyFixnum) return new ModeFlags(RubyFixnum.fix2long(arg));
 
             return getIOModes(getRuntime(), arg.convertToString().toString());
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
 
     protected ModeFlags parseModes19(ThreadContext context, IRubyObject arg) {
         ModeFlags modes = parseModes(arg);
 
         if (arg instanceof RubyString) {
             parseEncodingFromString(context, arg, 1);
         }
 
         return modes;
     }
 
     private void parseEncodingFromString(ThreadContext context, IRubyObject arg, int initialPosition) {
         RubyString modes19 = arg.convertToString();
         if (modes19.toString().contains(":")) {
             IRubyObject[] fullEncoding = modes19.split(context, RubyString.newString(context.getRuntime(), ":")).toJavaArray();
 
             IRubyObject externalEncodingOption = fullEncoding[initialPosition];
             if (fullEncoding.length > (initialPosition + 1)) {
                 IRubyObject internalEncodingOption = fullEncoding[initialPosition + 1];
                 set_encoding(context, externalEncodingOption, internalEncodingOption);
             } else {
                 set_encoding(context, externalEncodingOption);
             }
         }
     }
 
     @JRubyMethod(required = 1, optional = 1, visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         int argCount = args.length;
         ModeFlags modes;
         
         int fileno = RubyNumeric.fix2int(args[0]);
         
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(getRuntime().getFilenoExtMap(fileno));
             
             if (descriptor == null) {
                 throw getRuntime().newErrnoEBADFError();
             }
             
             descriptor.checkOpen();
             
             if (argCount == 2) {
                 if (args[1] instanceof RubyFixnum) {
                     modes = new ModeFlags(RubyFixnum.fix2long(args[1]));
                 } else {
                     modes = getIOModes(getRuntime(), args[1].convertToString().toString());
                 }
             } else {
                 // use original modes
                 modes = descriptor.getOriginalModes();
             }
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(getRuntime(), false);
             }
 
             openFile.setMode(modes.getOpenFileFlags());
         
             openFile.setMainStream(fdopen(descriptor, modes));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ive) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         return this;
     }
     
     protected Stream fdopen(ChannelDescriptor existingDescriptor, ModeFlags modes) throws InvalidValueException {
         // See if we already have this descriptor open.
         // If so then we can mostly share the handler (keep open
         // file, but possibly change the mode).
         
         if (existingDescriptor == null) {
             // redundant, done above as well
             
             // this seems unlikely to happen unless it's a totally bogus fileno
             // ...so do we even need to bother trying to create one?
             
             // IN FACT, we should probably raise an error, yes?
             throw getRuntime().newErrnoEBADFError();
             
 //            if (mode == null) {
 //                mode = "r";
 //            }
 //            
 //            try {
 //                openFile.setMainStream(streamForFileno(getRuntime(), fileno));
 //            } catch (BadDescriptorException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            } catch (IOException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            }
 //            //modes = new IOModes(getRuntime(), mode);
 //            
 //            registerStream(openFile.getMainStream());
         } else {
             // We are creating a new IO object that shares the same
             // IOHandler (and fileno).
             return ChannelStream.fdopen(getRuntime(), existingDescriptor, modes);
         }
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject external_encoding(ThreadContext context) {
         return externalEncoding != null ?
             context.getRuntime().getEncodingService().getEncoding(externalEncoding) :
             context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject internal_encoding(ThreadContext context) {
         return internalEncoding != null ?
             context.getRuntime().getEncodingService().getEncoding(internalEncoding) :
             context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString) {
         setExternalEncoding(context, encodingString);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding, IRubyObject options) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     private void setExternalEncoding(ThreadContext context, IRubyObject encoding) {
         externalEncoding = getEncodingCommon(context, encoding);
     }
 
 
     private void setInternalEncoding(ThreadContext context, IRubyObject encoding) {
         Encoding internalEncodingOption = getEncodingCommon(context, encoding);
 
         if (internalEncodingOption == externalEncoding) {
             context.getRuntime().getWarnings().warn("Ignoring internal encoding " + encoding
                     + ": it is identical to external encoding " + external_encoding(context));
         } else {
             internalEncoding = internalEncodingOption;
         }
     }
 
     private Encoding getEncodingCommon(ThreadContext context, IRubyObject encoding) {
         if (encoding instanceof RubyEncoding) return ((RubyEncoding) encoding).getEncoding();
         
-        return RubyEncoding.getEncodingFromObject(context.getRuntime(), encoding);
+        return context.getRuntime().getEncodingService().getEncodingFromObject(encoding);
     }
 
     @JRubyMethod(required = 1, optional = 2, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         RubyClass klass = (RubyClass)recv;
         
         RubyIO io = (RubyIO)klass.newInstance(context, args, block);
 
         if (block.isGiven()) {
             try {
                 return block.yield(context, io);
             } finally {
                 try {
                     io.getMetaClass().finvoke(context, io, "close", IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
                 } catch (RaiseException re) {
                     RubyException rubyEx = re.getException();
                     if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                         // MRI behavior: swallow StandardErorrs
                         runtime.getGlobalVariables().clear("$!");
                     } else {
                         throw re;
                     }
                 }
             }
         }
 
         return io;
     }
 
     @JRubyMethod(required = 1, optional = 2, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject sysopen(IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject pathString = args[0].convertToString();
         return sysopenCommon(recv, args, block, pathString);
     }
     
     @JRubyMethod(name = "sysopen", required = 1, optional = 2, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject sysopen19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject pathString;
         if (!(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             pathString = args[0].callMethod(context, "to_path");
         } else {
             pathString = args[0].convertToString();
         }
         return sysopenCommon(recv, args, block, pathString);
     }
 
     private static IRubyObject sysopenCommon(IRubyObject recv, IRubyObject[] args, Block block, IRubyObject pathString) {
         Ruby runtime = recv.getRuntime();
         runtime.checkSafeString(pathString);
         String path = pathString.toString();
 
         ModeFlags modes = null;
         int perms = -1; // -1 == don't set permissions
         try {
             if (args.length > 1) {
                 IRubyObject modeString = args[1].convertToString();
                 modes = getIOModes(runtime, modeString.toString());
             } else {
                 modes = getIOModes(runtime, "r");
             }
             if (args.length > 2) {
                 RubyInteger permsInt =
                     args.length >= 3 ? args[2].convertToInteger() : null;
                 perms = RubyNumeric.fix2int(permsInt);
             }
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
 
         int fileno = -1;
         try {
             ChannelDescriptor descriptor =
                 ChannelDescriptor.open(runtime.getCurrentDirectory(),
                                        path, modes, perms, runtime.getPosix(),
                                        runtime.getJRubyClassLoader());
             // always a new fileno, so ok to use internal only
             fileno = descriptor.getFileno();
         }
         catch (FileNotFoundException fnfe) {
             throw runtime.newErrnoENOENTError(path);
         } catch (DirectoryAsFileException dafe) {
             throw runtime.newErrnoEISDirError(path);
         } catch (FileExistsException fee) {
             throw runtime.newErrnoEEXISTError(path);
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
         }
         return runtime.newFixnum(fileno);
     }
 
     public boolean isAutoclose() {
         return openFile.isAutoclose();
     }
 
     public void setAutoclose(boolean autoclose) {
         openFile.setAutoclose(autoclose);
     }
 
     @JRubyMethod
     public IRubyObject autoclose(ThreadContext context) {
         return context.runtime.newBoolean(isAutoclose());
     }
 
     @JRubyMethod(name = "autoclose=")
     public IRubyObject autoclose_set(ThreadContext context, IRubyObject autoclose) {
         setAutoclose(autoclose.isTrue());
         return context.nil;
     }
 
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
         if (isClosed()) throw getRuntime().newIOError("closed stream");
 
         Ruby runtime = getRuntime();
         if (getExternalEncoding(runtime) == USASCIIEncoding.INSTANCE) {
             externalEncoding = ASCIIEncoding.INSTANCE;
         }
         openFile.setBinmode();
         return this;
     }
 
     @JRubyMethod(name = "binmode?", compat = RUBY1_9)
     public IRubyObject op_binmode(ThreadContext context) {
         return RubyBoolean.newBoolean(context.getRuntime(), openFile.isBinmode());
     }
 
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         
         try {
             RubyString string = obj.asString();
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
             
             Stream writeStream = myOpenFile.getWriteStream();
             
             if (myOpenFile.isWriteBuffered()) {
                 runtime.getWarnings().warn(ID.SYSWRITE_BUFFERED_IO, "syswrite for buffered IO");
             }
             
             if (!writeStream.getDescriptor().isWritable()) {
                 myOpenFile.checkClosed(runtime);
             }
             
             context.getThread().beforeBlockingCall();
             int read = writeStream.getDescriptor().write(string.getByteList());
             
             if (read == -1) {
                 // TODO? I think this ends up propagating from normal Java exceptions
                 // sys_fail(openFile.getPath())
             }
             
             return runtime.newFixnum(read);
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newSystemCallError(e.getMessage());
         } finally {
             context.getThread().afterBlockingCall();
         }
     }
     
     @JRubyMethod(name = "write_nonblock", required = 1)
     public IRubyObject write_nonblock(ThreadContext context, IRubyObject obj) {
         // MRI behavior: always check whether the file is writable
         // or not, even if we are to write 0 bytes.
         OpenFile myOpenFile = getOpenFileChecked();
 
         try {
             myOpenFile.checkWritable(context.getRuntime());
             RubyString str = obj.asString();
             if (str.getByteList().length() == 0) {
                 return context.getRuntime().newFixnum(0);
             }
 
             if (myOpenFile.isWriteBuffered()) {
                 context.getRuntime().getWarnings().warn(ID.SYSWRITE_BUFFERED_IO, "write_nonblock for buffered IO");
             }
             int written = myOpenFile.getWriteStream().getDescriptor().write(str.getByteList());
             return context.getRuntime().newFixnum(written);
         } catch (IOException ex) {
             throw context.getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         }  catch (PipeException ex) {
             throw context.getRuntime().newErrnoEPIPEError();
         }
     }
     
     /** io_write
      * 
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         
         runtime.secure(4);
         
         RubyString str = obj.asString();
 
         // TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
         
         if (str.getByteList().length() == 0) {
             return runtime.newFixnum(0);
         }
 
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
 
             context.getThread().beforeBlockingCall();
             int written = fwrite(str.getByteList());
 
             if (written == -1) {
                 // TODO: sys fail
             }
 
             // if not sync, we switch to write buffered mode
             if (!myOpenFile.isSync()) {
                 myOpenFile.setWriteBuffered();
             }
 
             return runtime.newFixnum(written);
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } finally {
             context.getThread().afterBlockingCall();
         }
     }
     
     private boolean waitWritable(Stream stream) {
         Channel ch = stream.getChannel();
         if (ch instanceof SelectableChannel) {
             getRuntime().getCurrentContext().getThread().select(ch, this, SelectionKey.OP_WRITE);
             return true;
         }
         return false;
     }
 
     private boolean waitReadable(Stream stream) {
         if (stream.readDataBuffered()) {
             return true;
         }
         Channel ch = stream.getChannel();
         if (ch instanceof SelectableChannel) {
             getRuntime().getCurrentContext().getThread().select(ch, this, SelectionKey.OP_READ);
             return true;
         }
         return false;
     }
 
     protected int fwrite(ByteList buffer) {
         int n, r, l, offset = 0;
         boolean eagain = false;
         Stream writeStream = openFile.getWriteStream();
 
         int len = buffer.length();
         
         if ((n = len) <= 0) return n;
 
         try {
             if (openFile.isSync()) {
                 openFile.fflush(writeStream);
 
                 // TODO: why is this guarded?
     //            if (!rb_thread_fd_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //            }
                
                 while(offset<len) {
                     l = n;
 
                     // TODO: Something about pipe buffer length here
 
                     r = writeStream.getDescriptor().write(buffer,offset,l);
 
                     if(r == len) {
                         return len; //Everything written
                     }
 
                     if (0 <= r) {
                         offset += r;
                         n -= r;
                         eagain = true;
                     }
 
                     if(eagain && waitWritable(writeStream)) {
                         openFile.checkClosed(getRuntime());
                         if(offset >= buffer.length()) {
                             return -1;
                         }
                         eagain = false;
                     } else {
                         return -1;
                     }
                 }
 
 
                 // TODO: all this stuff...some pipe logic, some async thread stuff
     //          retry:
     //            l = n;
     //            if (PIPE_BUF < l &&
     //                !rb_thread_critical &&
     //                !rb_thread_alone() &&
     //                wsplit_p(fptr)) {
     //                l = PIPE_BUF;
     //            }
     //            TRAP_BEG;
     //            r = write(fileno(f), RSTRING(str)->ptr+offset, l);
     //            TRAP_END;
     //            if (r == n) return len;
     //            if (0 <= r) {
     //                offset += r;
     //                n -= r;
     //                errno = EAGAIN;
     //            }
     //            if (rb_io_wait_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //                if (offset < RSTRING(str)->len)
     //                    goto retry;
     //            }
     //            return -1L;
             }
 
             // TODO: handle errors in buffered write by retrying until finished or file is closed
             return writeStream.fwrite(buffer);
     //        while (errno = 0, offset += (r = fwrite(RSTRING(str)->ptr+offset, 1, n, f)), (n -= r) > 0) {
     //            if (ferror(f)
     //            ) {
     //                if (rb_io_wait_writable(fileno(f))) {
     //                    rb_io_check_closed(fptr);
     //                    clearerr(f);
     //                    if (offset < RSTRING(str)->len)
     //                        continue;
     //                }
     //                return -1L;
     //            }
     //        }
 
 //            return len - n;
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     /** rb_io_addstr
      * 
      */
     @JRubyMethod(name = "<<", required = 1)
     public IRubyObject op_append(ThreadContext context, IRubyObject anObject) {
         // Claims conversion is done via 'to_s' in docs.
         callMethod(context, "write", anObject);
         
         return this; 
     }
 
     @JRubyMethod(name = "fileno", alias = "to_i")
     public RubyFixnum fileno(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         // map to external fileno
         return runtime.newFixnum(runtime.getFileno(getOpenFileChecked().getMainStream().getDescriptor()));
     }
     
     /** Returns the current line number.
      * 
      * @return the current line number.
      */
     @JRubyMethod(name = "lineno")
     public RubyFixnum lineno(ThreadContext context) {
         return context.getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Sets the current line number.
      * 
      * @param newLineNumber The new line number.
      */
     @JRubyMethod(name = "lineno=", required = 1)
     public RubyFixnum lineno_set(ThreadContext context, IRubyObject newLineNumber) {
         getOpenFileChecked().setLineNumber(RubyNumeric.fix2int(newLineNumber));
 
         return context.getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Returns the current sync mode.
      * 
      * @return the current sync mode.
      */
     @JRubyMethod(name = "sync")
     public RubyBoolean sync(ThreadContext context) {
         return context.getRuntime().newBoolean(getOpenFileChecked().getMainStream().isSync());
     }
     
     /**
      * <p>Return the process id (pid) of the process this IO object
      * spawned.  If no process exists (popen was not called), then
      * nil is returned.  This is not how it appears to be defined
      * but ruby 1.8 works this way.</p>
      * 
      * @return the pid or nil
      */
     @JRubyMethod(name = "pid")
     public IRubyObject pid(ThreadContext context) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (myOpenFile.getProcess() == null) {
             return context.getRuntime().getNil();
         }
         
         // Of course this isn't particularly useful.
         long pid = myOpenFile.getPid();
         
         return context.getRuntime().newFixnum(pid); 
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos(ThreadContext context) {
         try {
             return context.getRuntime().newFixnum(getOpenFileChecked().getMainStream().fgetpos());
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException bde) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
     }
     
     @JRubyMethod(name = "pos=", required = 1)
     public RubyFixnum pos_set(ThreadContext context, IRubyObject newPosition) {
         long offset = RubyNumeric.num2long(newPosition);
 
         if (offset < 0) {
             throw context.getRuntime().newSystemCallError("Negative seek offset");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.getMainStream().lseek(offset, Stream.SEEK_SET);
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return context.getRuntime().newFixnum(offset);
     }
     
     /** Print some objects to the stream.
      * 
      */
     @JRubyMethod(name = "print", rest = true, reads = FrameField.LASTLINE)
     public IRubyObject print(ThreadContext context, IRubyObject[] args) {
         return print(context, this, args);
     }
 
     /** Print some objects to the stream.
      *
      */
     public static IRubyObject print(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { context.getCurrentScope().getLastLine(context.getRuntime()) };
         }
 
         Ruby runtime = context.getRuntime();
         IRubyObject fs = runtime.getGlobalVariables().get("$,");
         IRubyObject rs = runtime.getGlobalVariables().get("$\\");
 
         for (int i = 0; i < args.length; i++) {
             if (i > 0 && !fs.isNil()) {
                 maybeIO.callMethod(context, "write", fs);
             }
             if (args[i].isNil()) {
                 maybeIO.callMethod(context, "write", runtime.newString("nil"));
             } else {
                 maybeIO.callMethod(context, "write", args[i]);
             }
         }
         if (!rs.isNil()) {
             maybeIO.callMethod(context, "write", rs);
         }
 
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "printf", required = 1, rest = true)
     public IRubyObject printf(ThreadContext context, IRubyObject[] args) {
         callMethod(context, "write", RubyKernel.sprintf(context, this, args));
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "putc", required = 1, backtrace = true)
     public IRubyObject putc(ThreadContext context, IRubyObject object) {
         return putc(context, this, object);
     }
 
     public static IRubyObject putc(ThreadContext context, IRubyObject maybeIO, IRubyObject object) {
         int c = RubyNumeric.num2chr(object);
         if (maybeIO instanceof RubyIO) {
             // FIXME we should probably still be dyncalling 'write' here
             RubyIO io = (RubyIO)maybeIO;
             try {
                 OpenFile myOpenFile = io.getOpenFileChecked();
                 myOpenFile.checkWritable(context.getRuntime());
                 Stream writeStream = myOpenFile.getWriteStream();
                 writeStream.fputc(c);
                 if (myOpenFile.isSync()) myOpenFile.fflush(writeStream);
             } catch (IOException ex) {
                 throw context.getRuntime().newIOErrorFromException(ex);
             } catch (BadDescriptorException e) {
                 throw context.getRuntime().newErrnoEBADFError();
             } catch (InvalidValueException ex) {
                 throw context.getRuntime().newErrnoEINVALError();
             } catch (PipeException ex) {
                 throw context.getRuntime().newErrnoEPIPEError();
             }
         } else {
             maybeIO.callMethod(context, "write",
                     RubyString.newStringNoCopy(context.getRuntime(), new byte[] {(byte)c}));
         }
 
         return object;
     }
 
     public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         return doSeek(context, offset, whence);
     }
 
     @JRubyMethod(name = "seek")
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0) {
         long offset = RubyNumeric.num2long(arg0);
         int whence = Stream.SEEK_SET;
         
         return doSeek(context, offset, whence);
     }
 
     @JRubyMethod(name = "seek")
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         long offset = RubyNumeric.num2long(arg0);
         int whence = RubyNumeric.fix2int(arg1.convertToInteger());
         
         return doSeek(context, offset, whence);
     }
     
     private RubyFixnum doSeek(ThreadContext context, long offset, int whence) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.seek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return RubyFixnum.zero(context.getRuntime());
     }
     
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "sysseek", required = 1, optional = 1)
     public RubyFixnum sysseek(ThreadContext context, IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         long pos;
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             
             if (myOpenFile.isReadable() && myOpenFile.isReadBuffered()) {
                 throw context.getRuntime().newIOError("sysseek for buffered IO");
             }
             if (myOpenFile.isWritable() && myOpenFile.isWriteBuffered()) {
                 context.getRuntime().getWarnings().warn(ID.SYSSEEK_BUFFERED_IO, "sysseek for buffered IO");
             }
             
             pos = myOpenFile.getMainStream().getDescriptor().lseek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return context.getRuntime().newFixnum(pos);
     }
 
     @JRubyMethod(name = "rewind")
     public RubyFixnum rewind(ThreadContext context) {
         OpenFile myOpenfile = getOpenFileChecked();
         
         try {
             myOpenfile.getMainStream().lseek(0L, Stream.SEEK_SET);
             myOpenfile.getMainStream().clearerr();
             
             // TODO: This is some goofy global file value from MRI..what to do?
 //            if (io == current_file) {
 //                gets_lineno -= fptr->lineno;
 //            }
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
 
         // Must be back on first line on rewind.
         myOpenfile.setLineNumber(0);
         
         return RubyFixnum.zero(context.getRuntime());
     }
     
     @JRubyMethod(name = "fsync")
     public RubyFixnum fsync(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
         
             Stream writeStream = myOpenFile.getWriteStream();
 
             writeStream.fflush();
             writeStream.sync();
 
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         }
 
         return RubyFixnum.zero(runtime);
     }
 
     /** Sets the current sync mode.
      * 
      * @param newSync The new sync mode.
      */
     @JRubyMethod(name = "sync=", required = 1)
     public IRubyObject sync_set(IRubyObject newSync) {
         getOpenFileChecked().setSync(newSync.isTrue());
         getOpenFileChecked().getMainStream().setSync(newSync.isTrue());
 
         return this;
     }
 
     @JRubyMethod(name = {"eof?", "eof"})
     public RubyBoolean eof_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             if (myOpenFile.getMainStream().feof()) {
                 return runtime.getTrue();
             }
             
             if (myOpenFile.getMainStream().readDataBuffered()) {
                 return runtime.getFalse();
             }
             
             readCheck(myOpenFile.getMainStream());
             waitReadable(myOpenFile.getMainStream());
             
             myOpenFile.getMainStream().clearerr();
             
             int c = myOpenFile.getMainStream().fgetc();
             
             if (c != -1) {
                 myOpenFile.getMainStream().ungetc(c);
                 return runtime.getFalse();
             }
             
             myOpenFile.checkClosed(runtime);
             
             myOpenFile.getMainStream().clearerr();
             
             return runtime.getTrue();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = {"tty?", "isatty"})
     public RubyBoolean tty_p(ThreadContext context) {
         return context.getRuntime().newBoolean(context.getRuntime().getPosix().isatty(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor()));
     }
     
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         Ruby runtime = getRuntime();
         
         if (this == original) return this;
 
         RubyIO originalIO = (RubyIO) TypeConverter.convertToTypeWithCheck(original, runtime.getIO(), "to_io");
         
         OpenFile originalFile = originalIO.getOpenFileChecked();
         OpenFile newFile = openFile;
         
         try {
             if (originalFile.getPipeStream() != null) {
                 originalFile.getPipeStream().fflush();
                 originalFile.getMainStream().lseek(0, Stream.SEEK_CUR);
             } else if (originalFile.isWritable()) {
                 originalFile.getMainStream().fflush();
             } else {
                 originalFile.getMainStream().lseek(0, Stream.SEEK_CUR);
             }
 
             newFile.setMode(originalFile.getMode());
             newFile.setProcess(originalFile.getProcess());
             newFile.setLineNumber(originalFile.getLineNumber());
             newFile.setPath(originalFile.getPath());
             newFile.setFinalizer(originalFile.getFinalizer());
             
             ModeFlags modes;
             if (newFile.isReadable()) {
                 if (newFile.isWritable()) {
                     if (newFile.getPipeStream() != null) {
                         modes = new ModeFlags(ModeFlags.RDONLY);
                     } else {
                         modes = new ModeFlags(ModeFlags.RDWR);
                     }
                 } else {
                     modes = new ModeFlags(ModeFlags.RDONLY);
                 }
             } else {
                 if (newFile.isWritable()) {
                     modes = new ModeFlags(ModeFlags.WRONLY);
                 } else {
                     modes = originalFile.getMainStream().getModes();
                 }
             }
             
             ChannelDescriptor descriptor = originalFile.getMainStream().getDescriptor().dup();
 
             newFile.setMainStream(ChannelStream.fdopen(runtime, descriptor, modes));
 
             newFile.getMainStream().setSync(originalFile.getMainStream().isSync());
             
             // TODO: the rest of this...seeking to same position is unnecessary since we share a channel
             // but some of this may be needed?
             
 //    fseeko(fptr->f, ftello(orig->f), SEEK_SET);
 //    if (orig->f2) {
 //	if (fileno(orig->f) != fileno(orig->f2)) {
 //	    fd = ruby_dup(fileno(orig->f2));
 //	}
 //	fptr->f2 = rb_fdopen(fd, "w");
 //	fseeko(fptr->f2, ftello(orig->f2), SEEK_SET);
 //    }
 //    if (fptr->mode & FMODE_BINMODE) {
 //	rb_io_binmode(dest);
 //    }
         } catch (IOException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         } catch (PipeException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         } catch (InvalidValueException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         }
         
         return this;
     }
     
     @JRubyMethod(name = "closed?")
     public RubyBoolean closed_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isClosed());
     }
 
     /**
      * Is this IO closed
      * 
      * @return true if closed
      */
     public boolean isClosed() {
         return openFile.getMainStream() == null && openFile.getPipeStream() == null;
     }
 
     /** 
      * <p>Closes all open resources for the IO.  It also removes
      * it from our magical all open file descriptor pool.</p>
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         Ruby runtime = getRuntime();
         
         if (runtime.getSafeLevel() >= 4 && isTaint()) {
             throw runtime.newSecurityError("Insecure: can't close");
         }
         
         openFile.checkClosed(runtime);
         return close2(runtime);
     }
         
     protected IRubyObject close2(Ruby runtime) {
         if (openFile == null) return runtime.getNil();
         
         interruptBlockingThreads();
 
         /* FIXME: Why did we go to this trouble and not use these descriptors?
         ChannelDescriptor main, pipe;
         if (openFile.getPipeStream() != null) {
             pipe = openFile.getPipeStream().getDescriptor();
         } else {
             if (openFile.getMainStream() == null) {
                 return runtime.getNil();
             }
             pipe = null;
         }
         
         main = openFile.getMainStream().getDescriptor(); */
         
         // cleanup, raising errors if any
         openFile.cleanup(runtime, true);
         
         // TODO: notify threads waiting on descriptors/IO? probably not...
         
         if (openFile.getProcess() != null) {
             obliterateProcess(openFile.getProcess());
             IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(runtime, openFile.getProcess().exitValue());
             runtime.getCurrentContext().setLastExitStatus(processResult);
         }
         
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "close_write")
     public IRubyObject close_write(ThreadContext context) {
         try {
             if (context.getRuntime().getSafeLevel() >= 4 && isTaint()) {
                 throw context.getRuntime().newSecurityError("Insecure: can't close");
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isReadable()) {
                 throw context.getRuntime().newIOError("closing non-duplex IO for writing");
             }
             
             if (myOpenFile.getPipeStream() == null) {
                 close();
             } else{
                 myOpenFile.getPipeStream().fclose();
                 myOpenFile.setPipeStream(null);
                 myOpenFile.setMode(myOpenFile.getMode() & ~OpenFile.WRITABLE);
                 // TODO
                 // n is result of fclose; but perhaps having a SysError below is enough?
                 // if (n != 0) rb_sys_fail(fptr->path);
             }
         } catch (BadDescriptorException bde) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (IOException ioe) {
             // hmmmm
         }
         return this;
     }
 
     @JRubyMethod(name = "close_read")
     public IRubyObject close_read(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         try {
             if (runtime.getSafeLevel() >= 4 && isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't close");
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isWritable()) {
                 throw runtime.newIOError("closing non-duplex IO for reading");
             }
             
             if (myOpenFile.getPipeStream() == null) {
                 close();
             } else{
                 myOpenFile.getMainStream().fclose();
                 myOpenFile.setMode(myOpenFile.getMode() & ~OpenFile.READABLE);
                 myOpenFile.setMainStream(myOpenFile.getPipeStream());
                 myOpenFile.setPipeStream(null);
                 // TODO
                 // n is result of fclose; but perhaps having a SysError below is enough?
                 // if (n != 0) rb_sys_fail(fptr->path);
             }
         } catch (BadDescriptorException bde) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException ioe) {
             // I believe Ruby bails out with a "bug" if closing fails
             throw runtime.newIOErrorFromException(ioe);
         }
         return this;
     }
 
     /** Flushes the IO output stream.
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "flush")
     public RubyIO flush() {
         try { 
             getOpenFileChecked().getWriteStream().fflush();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
 
         return this;
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_8)
     public IRubyObject gets(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         IRubyObject result = getline(runtime, separator(runtime.getRecordSeparatorVar().get()));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_8)
     public IRubyObject gets(ThreadContext context, IRubyObject separatorArg) {
         IRubyObject result = getline(context.getRuntime(), separator(separatorArg));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
@@ -2949,1134 +2949,1134 @@ public class RubyIO extends RubyObject {
             }
 
             return this;
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (EOFException e) {
             return runtime.getNil();
     	} catch (IOException e) {
     	    throw runtime.newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod
     public IRubyObject each_byte(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_byteInternal(context, block) : enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod(name = "bytes")
     public IRubyObject bytes(final ThreadContext context) {
         return enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod(name = "lines", compat = CompatVersion.RUBY1_8)
     public IRubyObject lines(final ThreadContext context, Block block) {
         return enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     @JRubyMethod(name = "lines", compat = CompatVersion.RUBY1_9)
     public IRubyObject lines19(final ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "each_line");
         return each_lineInternal(context, NULL_ARRAY, block);
     }
 
     public IRubyObject each_charInternal(final ThreadContext context, final Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject ch;
 
         while(!(ch = getc()).isNil()) {
             byte c = (byte)RubyNumeric.fix2int(ch);
             int n = runtime.getKCode().getEncoding().length(c);
             RubyString str = runtime.newString();
             if (externalEncoding != null) str.setEncoding(externalEncoding);
             str.setTaint(true);
             str.cat(c);
 
             while(--n > 0) {
                 if((ch = getc()).isNil()) {
                     block.yield(context, str);
                     return this;
                 }
                 c = (byte)RubyNumeric.fix2int(ch);
                 str.cat(c);
             }
             block.yield(context, str);
         }
         return this;
     }
 
     @JRubyMethod
     public IRubyObject each_char(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod
     public IRubyObject chars(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal(context, block) : enumeratorize(context.getRuntime(), this, "chars");
     }
 
     @JRubyMethod
     public IRubyObject codepoints(final ThreadContext context, final Block block) {
         return eachCodePointCommon(context, block, "codepoints");
     }
 
     @JRubyMethod
     public IRubyObject each_codepoint(final ThreadContext context, final Block block) {
         return eachCodePointCommon(context, block, "each_codepoint");
     }
 
     private IRubyObject eachCharCommon(final ThreadContext context, final Block block, final String methodName) {
         return block.isGiven() ? each_char(context, block) : enumeratorize(context.getRuntime(), this, methodName);
     }
 
     private IRubyObject eachCodePointCommon(final ThreadContext context, final Block block, final String methodName) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, methodName);
         IRubyObject ch;
 
         while(!(ch = getc()).isNil()) {
             block.yield(context, ch);
         }
         return this;
     }
 
     /** 
      * <p>Invoke a block for each line.</p>
      */
     public RubyIO each_lineInternal(ThreadContext context, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         ByteList separator = getSeparatorForGets(runtime, args);
 
         ByteListCache cache = new ByteListCache();
         for (IRubyObject line = getline(runtime, separator); !line.isNil(); 
 		line = getline(runtime, separator, cache)) {
             block.yield(context, line);
         }
         
         return this;
     }
 
     @JRubyMethod(optional = 1)
     public IRubyObject each(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_lineInternal(context, args, block) : enumeratorize(context.getRuntime(), this, "each", args);
     }
 
     @JRubyMethod(optional = 1)
     public IRubyObject each_line(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_lineInternal(context, args, block) : enumeratorize(context.getRuntime(), this, "each_line", args);
     }
 
     @JRubyMethod(optional = 1)
     public RubyArray readlines(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         IRubyObject[] separatorArgs = args.length > 0 ? new IRubyObject[] { args[0] } : IRubyObject.NULL_ARRAY;
         ByteList separator = getSeparatorForGets(runtime, separatorArgs);
         RubyArray result = runtime.newArray();
         IRubyObject line;
         
         while (! (line = getline(runtime, separator)).isNil()) {
             result.append(line);
         }
         return result;
     }
     
     @JRubyMethod(name = "to_io")
     public RubyIO to_io() {
     	return this;
     }
 
     @Override
     public String toString() {
         return "RubyIO(" + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStream().getDescriptor()) + ")";
     }
     
     /* class methods for IO */
     
     /** rb_io_s_foreach
     *
     */
     public static IRubyObject foreachInternal(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject filename = args[0].convertToString();
         runtime.checkSafeString(filename);
 
         RubyIO io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
         
         ByteListCache cache = new ByteListCache();
         if (!io.isNil()) {
             try {
                 ByteList separator = getSeparatorFromArgs(runtime, args, 1);
                 IRubyObject str = io.getline(runtime, separator, cache);
                 while (!str.isNil()) {
                     block.yield(context, str);
                     str = io.getline(runtime, separator, cache);
                     if (runtime.is1_9()) {
                         separator = getSeparatorFromArgs(runtime, args, 1);
                     }
                 }
             } finally {
                 io.close();
             }
         }
        
         return runtime.getNil();
     }
     
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject foreach(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), recv, "foreach", args);
 
         if (!(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             args[0] = args[0].callMethod(context, "to_path");
         }
         return foreachInternal(context, recv, args, block);
     }
 
     private static RubyIO convertToIO(ThreadContext context, IRubyObject obj) {
         return (RubyIO)TypeConverter.convertToType(obj, context.getRuntime().getIO(), "to_io");
     }
    
     private static boolean registerSelect(ThreadContext context, Selector selector, IRubyObject obj, RubyIO ioObj, int ops) throws IOException {
        Channel channel = ioObj.getChannel();
        if (channel == null || !(channel instanceof SelectableChannel)) {
            return false;
        }
        
        ((SelectableChannel) channel).configureBlocking(false);
        int real_ops = ((SelectableChannel) channel).validOps() & ops;
        SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
        
        if (key == null) {
            ((SelectableChannel) channel).register(selector, real_ops, obj);
        } else {
            key.interestOps(key.interestOps()|real_ops);
        }
        
        return true;
     }
    
     @JRubyMethod(name = "select", required = 1, optional = 3, meta = true)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return select_static(context, context.getRuntime(), args);
     }
 
     private static void checkArrayType(Ruby runtime, IRubyObject obj) {
         if (!(obj instanceof RubyArray)) {
             throw runtime.newTypeError("wrong argument type "
                     + obj.getMetaClass().getName() + " (expected Array)");
         }
     }
 
     public static IRubyObject select_static(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         Selector selector = null;
        try {
             Set pending = new HashSet();
             Set unselectable_reads = new HashSet();
             Set unselectable_writes = new HashSet();
             Map<RubyIO, Boolean> blocking = new HashMap();
             
             selector = SelectorFactory.openWithRetryFrom(context.getRuntime(), SelectorProvider.provider());
             if (!args[0].isNil()) {
                 // read
                 checkArrayType(runtime, args[0]);
                 for (Iterator i = ((RubyArray)args[0]).getList().iterator(); i.hasNext();) {
                     IRubyObject obj = (IRubyObject)i.next();
                     RubyIO ioObj = convertToIO(context, obj);
 
                     // save blocking state
                     if (ioObj.getChannel() instanceof SelectableChannel) blocking.put(ioObj, ((SelectableChannel)ioObj.getChannel()).isBlocking());
                     
                     if (registerSelect(context, selector, obj, ioObj, SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) {
                         if (ioObj.writeDataBuffered()) {
                             pending.add(obj);
                         }
                     } else {
                         if ((ioObj.openFile.getMode() & OpenFile.READABLE) != 0) {
                             unselectable_reads.add(obj);
                         }
                     }
                 }
             }
 
             if (args.length > 1 && !args[1].isNil()) {
                 // write
                 checkArrayType(runtime, args[1]);
                 for (Iterator i = ((RubyArray)args[1]).getList().iterator(); i.hasNext();) {
                     IRubyObject obj = (IRubyObject)i.next();
                     RubyIO ioObj = convertToIO(context, obj);
 
                     // save blocking state
                     if (!blocking.containsKey(ioObj) && ioObj.getChannel() instanceof SelectableChannel) blocking.put(ioObj, ((SelectableChannel)ioObj.getChannel()).isBlocking());
 
                     if (!registerSelect(context, selector, obj, ioObj, SelectionKey.OP_WRITE)) {
                         if ((ioObj.openFile.getMode() & OpenFile.WRITABLE) != 0) {
                             unselectable_writes.add(obj);
                         }
                     }
                 }
             }
 
             if (args.length > 2 && !args[2].isNil()) {
                 checkArrayType(runtime, args[2]);
             // Java's select doesn't do anything about this, so we leave it be.
             }
 
             final boolean has_timeout = (args.length > 3 && !args[3].isNil());
             long timeout = 0;
             if (has_timeout) {
                 IRubyObject timeArg = args[3];
                 if (timeArg instanceof RubyFloat) {
                     timeout = Math.round(((RubyFloat)timeArg).getDoubleValue() * 1000);
                 } else if (timeArg instanceof RubyFixnum) {
                     timeout = Math.round(((RubyFixnum)timeArg).getDoubleValue() * 1000);
                 } else { // TODO: MRI also can hadle Bignum here
                     throw runtime.newTypeError("can't convert " + timeArg.getMetaClass().getName() + " into time interval");
                 }
 
                 if (timeout < 0) {
                     throw runtime.newArgumentError("negative timeout given");
                 }
             }
 
             if (pending.isEmpty() && unselectable_reads.isEmpty() && unselectable_writes.isEmpty()) {
                 if (has_timeout) {
                     if (timeout == 0) {
                         selector.selectNow();
                     } else {
                         selector.select(timeout);
                     }
                 } else {
                     selector.select();
                 }
             } else {
                 selector.selectNow();
             }
 
             List r = new ArrayList();
             List w = new ArrayList();
             List e = new ArrayList();
             for (Iterator i = selector.selectedKeys().iterator(); i.hasNext();) {
                 SelectionKey key = (SelectionKey)i.next();
                 try {
                     int interestAndReady = key.interestOps() & key.readyOps();
                     if ((interestAndReady & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT | SelectionKey.OP_CONNECT)) != 0) {
                         r.add(key.attachment());
                         pending.remove(key.attachment());
                     }
                     if ((interestAndReady & (SelectionKey.OP_WRITE)) != 0) {
                         w.add(key.attachment());
                     }
                 } catch (CancelledKeyException cke) {
                     // TODO: is this the right thing to do?
                     pending.remove(key.attachment());
                     e.add(key.attachment());
                 }
             }
             r.addAll(pending);
             r.addAll(unselectable_reads);
             w.addAll(unselectable_writes);
 
             // make all sockets blocking as configured again
             selector.close(); // close unregisters all channels, so we can safely reset blocking modes
             for (Map.Entry blockingEntry : blocking.entrySet()) {
                 SelectableChannel channel = (SelectableChannel)((RubyIO)blockingEntry.getKey()).getChannel();
                 synchronized (channel.blockingLock()) {
                     channel.configureBlocking((Boolean)blockingEntry.getValue());
                 }
             }
 
             if (r.isEmpty() && w.isEmpty() && e.isEmpty()) {
                 return runtime.getNil();
             }
 
             List ret = new ArrayList();
 
             ret.add(RubyArray.newArray(runtime, r));
             ret.add(RubyArray.newArray(runtime, w));
             ret.add(RubyArray.newArray(runtime, e));
 
             return RubyArray.newArray(runtime, ret);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         } finally {
             if (selector != null) {
                 try {
                     selector.close();
                 } catch (Exception e) {
                 }
             }
         }
    }
    
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         switch (args.length) {
         case 0: throw context.getRuntime().newArgumentError(0, 1);
         case 1: return readStatic(context, recv, args[0]);
         case 2: return readStatic(context, recv, args[0], args[1]);
         case 3: return readStatic(context, recv, args[0], args[1], args[2]);
         default: throw context.getRuntime().newArgumentError(args.length, 3);
         }
    }
 
     private static RubyIO newFile(ThreadContext context, IRubyObject recv, IRubyObject... args) {
        return (RubyIO) RubyKernel.open(context, recv, args, Block.NULL_BLOCK);
     }
 
     public static void failIfDirectory(Ruby runtime, RubyString pathStr) {
         if (RubyFileTest.directory_p(runtime, pathStr).isTrue()) {
             if (Platform.IS_WINDOWS) {
                 throw runtime.newErrnoEACCESError(pathStr.asJavaString());
             } else {
                 throw runtime.newErrnoEISDirError(pathStr.asJavaString());
             }
         }
     }
 
     @Deprecated
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, Block unusedBlock) {
         return readStatic(context, recv, path);
     }
     @Deprecated
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length) {
         return readStatic(context, recv, path, length);
     }
     @Deprecated
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset) {
         return readStatic(context, recv, path, length, offset);
     }
    
     @JRubyMethod(name = "read", meta = true, compat = RUBY1_8)
     public static IRubyObject readStatic(ThreadContext context, IRubyObject recv, IRubyObject path) {
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
 
        try {
            return file.read(context);
        } finally {
            file.close();
        }
     }
    
     @JRubyMethod(name = "read", meta = true, compat = RUBY1_8)
     public static IRubyObject readStatic(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length) {
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
        
         try {
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     @JRubyMethod(name = "read", meta = true, compat = RUBY1_8)
     public static IRubyObject readStatic(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset) {
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     /**
      *  options is a hash which can contain:
      *    encoding: string or encoding
      *    mode: string
      *    open_args: array of string
      */
     private static IRubyObject read19(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset, RubyHash options) {
         // FIXME: process options
 
         RubyString pathStr = RubyFile.get_path(context, path);
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     /**
      * binread is just like read, except it doesn't take options and it forces
      * mode to be "rb:ASCII-8BIT"
      *
      * @param context the current ThreadContext
      * @param recv the target of the call (IO or a subclass)
      * @param args arguments; path [, length [, offset]]
      * @return the binary contents of the given file, at specified length and offset
      */
     @JRubyMethod(meta = true, required = 1, optional = 2, compat = RUBY1_9)
     public static IRubyObject binread(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject nil = context.getRuntime().getNil();
         IRubyObject path = args[0];
         IRubyObject length = nil;
         IRubyObject offset = nil;
         Ruby runtime = context.runtime;
 
         if (args.length > 2) {
             offset = args[2];
             length = args[1];
         } else if (args.length > 1) {
             length = args[1];
         }
         RubyIO file = (RubyIO)RuntimeHelpers.invoke(context, runtime.getFile(), "new", path, runtime.newString("rb:ASCII-8BIT"));
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     // Enebo: annotation processing forced me to do pangea method here...
     @JRubyMethod(name = "read", meta = true, required = 1, optional = 3, compat = RUBY1_9)
     public static IRubyObject read19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         IRubyObject nil = context.getRuntime().getNil();
         IRubyObject path = args[0];
         IRubyObject length = nil;
         IRubyObject offset = nil;
         RubyHash options = null;
         if (args.length > 3) {
             if (!(args[3] instanceof RubyHash)) throw context.getRuntime().newTypeError("Must be a hash");
             options = (RubyHash) args[3];
             offset = args[2];
             length = args[1];
         } else if (args.length > 2) {
             if (args[2] instanceof RubyHash) {
                 options = (RubyHash) args[2];
             } else {
                 offset = args[2];
             }
             length = args[1];
         } else if (args.length > 1) {
             if (args[1] instanceof RubyHash) {
                 options = (RubyHash) args[1];
             } else {
                 length = args[1];
             }
         }
 
         return read19(context, recv, path, length, offset, (RubyHash) options);
     }
 
     @JRubyMethod(name = "readlines", required = 1, optional = 1, meta = true)
     public static RubyArray readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         int count = args.length;
 
         IRubyObject[] fileArguments = new IRubyObject[]{ args[0].convertToString() };
         IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
         RubyIO file = (RubyIO) RubyKernel.open(context, recv, fileArguments, Block.NULL_BLOCK);
         try {
             return file.readlines(context, separatorArguments);
         } finally {
             file.close();
         }
     }
    
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true, compat = RUBY1_8)
     public static IRubyObject popen(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         int mode;
 
         IRubyObject cmdObj = null;
         if (Platform.IS_WINDOWS) {
             String[] tokens = args[0].convertToString().toString().split(" ", 2);
             String commandString = tokens[0].replace('/', '\\') +
                     (tokens.length > 1 ? ' ' + tokens[1] : "");
             cmdObj = runtime.newString(commandString);
         } else {
             cmdObj = args[0].convertToString();
         }
         runtime.checkSafeString(cmdObj);
 
         if ("-".equals(cmdObj.toString())) {
             throw runtime.newNotImplementedError("popen(\"-\") is unimplemented");
         }
 
         try {
             if (args.length == 1) {
                 mode = ModeFlags.RDONLY;
             } else if (args[1] instanceof RubyFixnum) {
                 mode = RubyFixnum.num2int(args[1]);
             } else {
                 mode = getIOModesIntFromString(runtime, args[1].convertToString().toString());
             }
 
             ModeFlags modes = new ModeFlags(mode);
 
             ShellLauncher.POpenProcess process = ShellLauncher.popen(runtime, cmdObj, modes);
 
             // Yes, this is gross. java.lang.Process does not appear to be guaranteed
             // "ready" when we get it back from Runtime#exec, so we try to give it a
             // chance by waiting for 10ms before we proceed. Only doing this on 1.5
             // since Hotspot 1.6+ does not seem to exhibit the problem.
             if (System.getProperty("java.specification.version", "").equals("1.5")) {
                 synchronized (process) {
                     try {
                         process.wait(100);
                     } catch (InterruptedException ie) {}
                 }
             }
             
             RubyIO io = new RubyIO(runtime, process, modes);
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, io);
                 } finally {
                     if (io.openFile.isOpen()) {
                         io.close();
                     }
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor())));
                 }
             }
             return io;
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject popen19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         int mode;
 
         IRubyObject[] cmdPlusArgs = null;
         RubyHash env = null;
         RubyHash opts = null;
         IRubyObject cmdObj = null;
         IRubyObject arg0 = args[0].checkArrayType();
         
         if (!arg0.isNil()) {
             List argList = new ArrayList(Arrays.asList(((RubyArray)arg0).toJavaArray()));
             if (argList.isEmpty()) throw runtime.newArgumentError("wrong number of arguments");
             if (argList.get(0) instanceof RubyHash) {
                 // leading hash, use for env
                 env = (RubyHash)argList.remove(0);
             }
             if (argList.isEmpty()) throw runtime.newArgumentError("wrong number of arguments");
             if (argList.size() > 1 && argList.get(argList.size() - 1) instanceof RubyHash) {
                 // trailing hash, use for opts
                 env = (RubyHash)argList.get(argList.size() - 1);
             }
             cmdPlusArgs = (IRubyObject[])argList.toArray(new IRubyObject[argList.size()]);
 
             if (Platform.IS_WINDOWS) {
                 String commandString = cmdPlusArgs[0].convertToString().toString().replace('/', '\\');
                 cmdPlusArgs[0] = runtime.newString(commandString);
             } else {
                 cmdPlusArgs[0] = cmdPlusArgs[0].convertToString();
             }
             cmdObj = cmdPlusArgs[0];
         } else {
             if (Platform.IS_WINDOWS) {
                 String[] tokens = args[0].convertToString().toString().split(" ", 2);
                 String commandString = tokens[0].replace('/', '\\') +
                         (tokens.length > 1 ? ' ' + tokens[1] : "");
                 cmdObj = runtime.newString(commandString);
             } else {
                 cmdObj = args[0].convertToString();
             }
         }
         
         runtime.checkSafeString(cmdObj);
 
         if ("-".equals(cmdObj.toString())) {
             throw runtime.newNotImplementedError("popen(\"-\") is unimplemented");
         }
 
         try {
             if (args.length == 1) {
                 mode = ModeFlags.RDONLY;
             } else if (args[1] instanceof RubyFixnum) {
                 mode = RubyFixnum.num2int(args[1]);
             } else {
                 mode = getIOModesIntFromString(runtime, args[1].convertToString().toString());
             }
 
             ModeFlags modes = new ModeFlags(mode);
 
             ShellLauncher.POpenProcess process;
             if (cmdPlusArgs == null) {
                 process = ShellLauncher.popen(runtime, cmdObj, modes);
             } else {
                 process = ShellLauncher.popen(runtime, cmdPlusArgs, env, modes);
             }
 
             // Yes, this is gross. java.lang.Process does not appear to be guaranteed
             // "ready" when we get it back from Runtime#exec, so we try to give it a
             // chance by waiting for 10ms before we proceed. Only doing this on 1.5
             // since Hotspot 1.6+ does not seem to exhibit the problem.
             if (System.getProperty("java.specification.version", "").equals("1.5")) {
                 synchronized (process) {
                     try {
                         process.wait(100);
                     } catch (InterruptedException ie) {}
                 }
             }
 
             RubyIO io = new RubyIO(runtime, process, modes);
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, io);
                 } finally {
                     if (io.openFile.isOpen()) {
                         io.close();
                     }
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor())));
                 }
             }
             return io;
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
    
     @JRubyMethod(rest = true, meta = true)
     public static IRubyObject popen3(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         try {
             POpenTuple tuple = popenSpecial(context, args);
 
             RubyArray yieldArgs = RubyArray.newArrayLight(runtime,
                     tuple.output,
                     tuple.input,
                     tuple.error);
             
             if (block.isGiven()) {
                 try {
                     return block.yield(context, yieldArgs);
                 } finally {
                     cleanupPOpen(tuple);
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, tuple.process.waitFor()));
                 }
             }
             return yieldArgs;
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     @JRubyMethod(rest = true, meta = true)
     public static IRubyObject popen4(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         try {
             POpenTuple tuple = popenSpecial(context, args);
 
             RubyArray yieldArgs = RubyArray.newArrayLight(runtime,
                     runtime.newFixnum(ShellLauncher.getPidFromProcess(tuple.process)),
                     tuple.output,
                     tuple.input,
                     tuple.error);
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, yieldArgs);
                 } finally {
                     cleanupPOpen(tuple);
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, tuple.process.waitFor()));
                 }
             }
             return yieldArgs;
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     private static void cleanupPOpen(POpenTuple tuple) {
         if (tuple.input.openFile.isOpen()) {
             tuple.input.close();
         }
         if (tuple.output.openFile.isOpen()) {
             tuple.output.close();
         }
         if (tuple.error.openFile.isOpen()) {
             tuple.error.close();
         }
     }
 
     private static class POpenTuple {
         public POpenTuple(RubyIO i, RubyIO o, RubyIO e, Process p) {
             input = i; output = o; error = e; process = p;
         }
         public final RubyIO input;
         public final RubyIO output;
         public final RubyIO error;
         public final Process process;
     }
 
     public static POpenTuple popenSpecial(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         try {
             ShellLauncher.POpenProcess process = ShellLauncher.popen3(runtime, args);
             RubyIO input = process.getInput() != null ?
                 new RubyIO(runtime, process.getInput()) :
                 new RubyIO(runtime, process.getInputStream());
             RubyIO output = process.getOutput() != null ?
                 new RubyIO(runtime, process.getOutput()) :
                 new RubyIO(runtime, process.getOutputStream());
             RubyIO error = process.getError() != null ?
                 new RubyIO(runtime, process.getError()) :
                 new RubyIO(runtime, process.getErrorStream());
 
             input.getOpenFile().getMainStream().getDescriptor().
               setCanBeSeekable(false);
             output.getOpenFile().getMainStream().getDescriptor().
               setCanBeSeekable(false);
             error.getOpenFile().getMainStream().getDescriptor().
               setCanBeSeekable(false);
 
             return new POpenTuple(input, output, error, process);
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     // NIO based pipe
     @JRubyMethod(name = "pipe", meta = true)
     public static IRubyObject pipe(ThreadContext context, IRubyObject recv) {
         // TODO: This isn't an exact port of MRI's pipe behavior, so revisit
        Ruby runtime = context.getRuntime();
        try {
            Pipe pipe = Pipe.open();
 
            RubyIO source = new RubyIO(runtime, pipe.source());
            RubyIO sink = new RubyIO(runtime, pipe.sink());
 
            sink.openFile.getMainStream().setSync(true);
            return runtime.newArrayNoCopy(new IRubyObject[] { source, sink });
        } catch (IOException ioe) {
            throw runtime.newIOErrorFromException(ioe);
        }
    }
     
     @JRubyMethod(name = "copy_stream", meta = true, compat = RUBY1_9)
     public static IRubyObject copy_stream(ThreadContext context, IRubyObject recv, 
             IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
 
         RubyIO io1 = null;
         RubyIO io2 = null;
 
         try {
             if (arg1 instanceof RubyString) {
                 io1 = (RubyIO) RubyFile.open(context, runtime.getFile(), new IRubyObject[] {arg1}, Block.NULL_BLOCK);
             } else if (arg1 instanceof RubyIO) {
                 io1 = (RubyIO) arg1;
             } else {
                 throw runtime.newTypeError("Should be String or IO");
             }
 
             if (arg2 instanceof RubyString) {
                 io2 = (RubyIO) RubyFile.open(context, runtime.getFile(), new IRubyObject[] {arg2, runtime.newString("w")}, Block.NULL_BLOCK);
             } else if (arg2 instanceof RubyIO) {
                 io2 = (RubyIO) arg2;
             } else {
                 throw runtime.newTypeError("Should be String or IO");
             }
 
             ChannelDescriptor d1 = io1.openFile.getMainStream().getDescriptor();
             if (!d1.isSeekable()) {
                 throw context.getRuntime().newTypeError("only supports file-to-file copy");
             }
             ChannelDescriptor d2 = io2.openFile.getMainStream().getDescriptor();
             if (!d2.isSeekable()) {
                 throw context.getRuntime().newTypeError("only supports file-to-file copy");
             }
 
             FileChannel f1 = (FileChannel)d1.getChannel();
             FileChannel f2 = (FileChannel)d2.getChannel();
 
             try {
                 long size = f1.size();
 
                 f1.transferTo(f2.position(), size, f2);
 
                 return context.getRuntime().newFixnum(size);
             } catch (IOException ioe) {
                 throw runtime.newIOErrorFromException(ioe);
             }
         } finally {
             try {
                 if (io1 != null) {
                     io1.close();
                 }
             } finally {
                 if (io2 != null) {
                     io2.close();
                 }
             }
         }
     }
 
     @JRubyMethod(name = "try_convert", meta = true, backtrace = true, compat = RUBY1_9)
     public static IRubyObject tryConvert(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return arg.respondsTo("to_io") ? convertToIO(context, arg) : context.getRuntime().getNil();
     }
 
     private static ByteList getNilByteList(Ruby runtime) {
         return runtime.is1_9() ? ByteList.EMPTY_BYTELIST : NIL_BYTELIST;
     }
     
     /**
      * Add a thread to the list of blocking threads for this IO.
      * 
      * @param thread A thread blocking on this IO
      */
     public synchronized void addBlockingThread(RubyThread thread) {
         if (blockingThreads == null) {
             blockingThreads = new ArrayList<RubyThread>(1);
         }
         blockingThreads.add(thread);
     }
     
     /**
      * Remove a thread from the list of blocking threads for this IO.
      * 
      * @param thread A thread blocking on this IO
      */
     public synchronized void removeBlockingThread(RubyThread thread) {
         if (blockingThreads == null) {
             return;
         }
         for (int i = 0; i < blockingThreads.size(); i++) {
             if (blockingThreads.get(i) == thread) {
                 // not using remove(Object) here to avoid the equals() call
                 blockingThreads.remove(i);
             }
         }
     }
     
     /**
      * Fire an IOError in all threads blocking on this IO object
      */
     protected synchronized void interruptBlockingThreads() {
         if (blockingThreads == null) {
             return;
         }
         for (int i = 0; i < blockingThreads.size(); i++) {
             RubyThread thread = blockingThreads.get(i);
             
             // raise will also wake the thread from selection
             thread.raise(new IRubyObject[] {getRuntime().newIOError("stream closed").getException()}, Block.NULL_BLOCK);
         }
     }
 
     /**
      * Caching reference to allocated byte-lists, allowing for internal byte[] to be
      * reused, rather than reallocated.
      *
      * Predominately used on {@link RubyIO#getline(Ruby, ByteList)} and variants.
      *
      * @author realjenius
      */
     private static class ByteListCache {
         private byte[] buffer = new byte[0];
         public void release(ByteList l) {
             buffer = l.getUnsafeBytes();
         }
 
         public ByteList allocate(int size) {
             ByteList l = new ByteList(buffer, 0, size, false);
             return l;
         }
     }
 
     /**
      *
      *  ==== Options
      *  <code>opt</code> can have the following keys
      *  :mode ::
      *    same as <code>mode</code> parameter
      *  :external_encoding ::
      *    external encoding for the IO. "-" is a
      *    synonym for the default external encoding.
      *  :internal_encoding ::
      *    internal encoding for the IO.
      *    "-" is a synonym for the default internal encoding.
      *    If the value is nil no conversion occurs.
      *  :encoding ::
      *    specifies external and internal encodings as "extern:intern".
      *  :textmode ::
      *    If the value is truth value, same as "b" in argument <code>mode</code>.
      *  :binmode ::
      *    If the value is truth value, same as "t" in argument <code>mode</code>.
      *
      *  Also <code>opt</code> can have same keys in <code>String#encode</code> for
      *  controlling conversion between the external encoding and the internal encoding.
      *
      */
     protected ModeFlags parseOptions(ThreadContext context, IRubyObject options, ModeFlags modes) {
         Ruby runtime = context.getRuntime();
 
         RubyHash rubyOptions = (RubyHash) options;
 
         IRubyObject internalEncodingOption = rubyOptions.fastARef(runtime.newSymbol("internal_encoding"));
         IRubyObject externalEncodingOption = rubyOptions.fastARef(runtime.newSymbol("external_encoding"));
         RubyString dash = runtime.newString("-");
         if (externalEncodingOption != null && !externalEncodingOption.isNil()) {
             if (dash.eql(externalEncodingOption)) {
-                externalEncodingOption = RubyEncoding.getDefaultExternal(runtime);
+                externalEncodingOption = context.getRuntime().getEncodingService().getDefaultExternal();
             }
             setExternalEncoding(context, externalEncodingOption);
         }
 
         if (internalEncodingOption != null && !internalEncodingOption.isNil()) {
             if (dash.eql(internalEncodingOption)) {
-                internalEncodingOption = RubyEncoding.getDefaultInternal(runtime);
+                internalEncodingOption = context.getRuntime().getEncodingService().getDefaultInternal();
             }
             setInternalEncoding(context, internalEncodingOption);
         }
 
         IRubyObject encoding = rubyOptions.fastARef(runtime.newSymbol("encoding"));
         if (encoding != null && !encoding.isNil()) {
             if (externalEncodingOption != null && !externalEncodingOption.isNil()) {
                 context.getRuntime().getWarnings().warn("Ignoring encoding parameter '"+ encoding +"': external_encoding is used");
             } else if (internalEncodingOption != null && !internalEncodingOption.isNil()) {
                 context.getRuntime().getWarnings().warn("Ignoring encoding parameter '"+ encoding +"': internal_encoding is used");
             } else {
                 parseEncodingFromString(context, encoding, 0);
             }
         }
 
         if (rubyOptions.containsKey(runtime.newSymbol("mode"))) {
             modes = parseModes19(context, rubyOptions.fastARef(runtime.newSymbol("mode")).asString());
         }
 
 //      FIXME: check how ruby 1.9 handles this
 
 //        if (rubyOptions.containsKey(runtime.newSymbol("textmode")) &&
 //                rubyOptions.fastARef(runtime.newSymbol("textmode")).isTrue()) {
 //            try {
 //                modes = getIOModes(runtime, "t");
 //            } catch (InvalidValueException e) {
 //                throw getRuntime().newErrnoEINVALError();
 //            }
 //        }
 //
 //        if (rubyOptions.containsKey(runtime.newSymbol("binmode")) &&
 //                rubyOptions.fastARef(runtime.newSymbol("binmode")).isTrue()) {
 //            try {
 //                modes = getIOModes(runtime, "b");
 //            } catch (InvalidValueException e) {
 //                throw getRuntime().newErrnoEINVALError();
 //            }
 //        }
 
         return modes;
     }
 
     /**
      * Try for around 1s to destroy the child process. This is to work around
      * issues on some JVMs where if you try to destroy the process too quickly
      * it may not be ready and may ignore the destroy. A subsequent waitFor
      * will then hang. This version tries to destroy and call exitValue
      * repeatedly for up to 1000 calls with 1ms delay between iterations, with
      * the intent that the target process ought to be "ready to die" fairly
      * quickly and we don't get stuck in a blocking waitFor call.
      *
      * @param runtime The Ruby runtime, for raising an error
      * @param process The process to obliterate
      */
     public static void obliterateProcess(Process process) {
         int i = 0;
         Object waitLock = new Object();
         while (true) {
             // only try 1000 times with a 1ms sleep between, so we don't hang
             // forever on processes that ignore SIGTERM
             if (i >= 1000) {
                 throw new RuntimeException("could not shut down process: " + process);
             }
 
             // attempt to destroy (SIGTERM on UNIX, TerminateProcess on Windows)
             process.destroy();
             
             try {
                 // get the exit value; succeeds if it has terminated, throws
                 // IllegalThreadStateException if not.
                 process.exitValue();
             } catch (IllegalThreadStateException itse) {
                 // increment count and try again after a 1ms sleep
                 i += 1;
                 synchronized (waitLock) {
                     try {waitLock.wait(1);} catch (InterruptedException ie) {}
                 }
                 continue;
             }
             // success!
             break;
         }
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor, boolean isRetained) {
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor) {
     }
 
     @Deprecated
     public void unregisterDescriptor(int aFileno) {
     }
 
     @Deprecated
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         return ChannelDescriptor.getDescriptorByFileno(aFileno);
     }
 
     @Deprecated
     public static int getNewFileno() {
         return ChannelDescriptor.getNewFileno();
     }
 
     @Deprecated
     public boolean writeDataBuffered() {
         return openFile.getMainStream().writeDataBuffered();
     }
 
     @Deprecated
     public IRubyObject gets(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? gets(context) : gets(context, args[0]);
     }
 
     @Deprecated
     public IRubyObject readline(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? readline(context) : readline(context, args[0]);
     }
     
     protected OpenFile openFile;
     protected List<RubyThread> blockingThreads;
     protected Encoding externalEncoding;
     protected Encoding internalEncoding;
 }
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index c9099f218a..ef5d2252c4 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -1,1198 +1,1175 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
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
 import static org.jruby.anno.FrameField.BACKREF;
 import static org.jruby.util.StringSupport.CR_7BIT;
 import static org.jruby.util.StringSupport.CR_BROKEN;
 import static org.jruby.util.StringSupport.CR_MASK;
 import static org.jruby.util.StringSupport.CR_UNKNOWN;
 import static org.jruby.util.StringSupport.CR_VALID;
 import static org.jruby.util.StringSupport.codeLength;
 import static org.jruby.util.StringSupport.codePoint;
 import static org.jruby.util.StringSupport.codeRangeScan;
 import static org.jruby.util.StringSupport.searchNonAscii;
 import static org.jruby.util.StringSupport.strLengthWithCodeRange;
 import static org.jruby.util.StringSupport.toLower;
 import static org.jruby.util.StringSupport.toUpper;
 import static org.jruby.util.StringSupport.unpackArg;
 import static org.jruby.util.StringSupport.unpackResult;
 
 import java.io.UnsupportedEncodingException;
 import java.nio.ByteBuffer;
 import java.nio.charset.CharacterCodingException;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetEncoder;
 import java.nio.charset.CodingErrorAction;
 import java.util.Arrays;
 import java.util.Locale;
 
 import org.jcodings.Encoding;
 import org.jcodings.EncodingDB.Entry;
 import org.jcodings.ascii.AsciiTables;
 import org.jcodings.constants.CharacterType;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jcodings.specific.UTF8Encoding;
 import org.jcodings.util.IntHash;
 import org.joni.Matcher;
 import org.joni.Option;
 import org.joni.Regex;
 import org.joni.Region;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.cext.RString;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.ConvertBytes;
 import org.jruby.util.Numeric;
 import org.jruby.util.Pack;
 import org.jruby.util.Sprintf;
 import org.jruby.util.StringSupport;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.string.JavaCrypt;
 
 /**
  * Implementation of Ruby String class
  * 
  * Concurrency: no synchronization is required among readers, but
  * all users must synchronize externally with writers.
  *
  */
 @JRubyClass(name="String", include={"Enumerable", "Comparable"})
 public class RubyString extends RubyObject implements EncodingCapable {
     private static final ASCIIEncoding ASCII = ASCIIEncoding.INSTANCE;
     private static final UTF8Encoding UTF8 = UTF8Encoding.INSTANCE;
     private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
 
     // string doesn't share any resources
     private static final int SHARE_LEVEL_NONE = 0;
     // string has it's own ByteList, but it's pointing to a shared buffer (byte[])
     private static final int SHARE_LEVEL_BUFFER = 1;
     // string doesn't have it's own ByteList (values)
     private static final int SHARE_LEVEL_BYTELIST = 2;
 
     private volatile int shareLevel = SHARE_LEVEL_NONE;
 
     private ByteList value;
 
     private RString rstring;
 
     public static RubyClass createStringClass(Ruby runtime) {
         RubyClass stringClass = runtime.defineClass("String", runtime.getObject(), STRING_ALLOCATOR);
         runtime.setString(stringClass);
         stringClass.index = ClassIndex.STRING;
         stringClass.setReifiedClass(RubyString.class);
         stringClass.kindOf = new RubyModule.KindOf() {
             @Override
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyString;
                 }
             };
 
         stringClass.includeModule(runtime.getComparable());
         if (!runtime.is1_9()) stringClass.includeModule(runtime.getEnumerable());
         stringClass.defineAnnotatedMethods(RubyString.class);
 
         return stringClass;
     }
 
     private static ObjectAllocator STRING_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return RubyString.newEmptyString(runtime, klass);
         }
     };
 
     public Encoding getEncoding() {
         return value.getEncoding();
     }
 
     public void setEncoding(Encoding encoding) {
         value.setEncoding(encoding);
     }
 
     public void associateEncoding(Encoding enc) {
         if (value.getEncoding() != enc) {
             if (!isCodeRangeAsciiOnly() || !enc.isAsciiCompatible()) clearCodeRange();
             value.setEncoding(enc);
         }
     }
 
     public final void setEncodingAndCodeRange(Encoding enc, int cr) {
         value.setEncoding(enc);
         setCodeRange(cr);
     }
 
-    private static final ByteList EXTERNAL_BL = ByteList.create("external");
-    private static final ByteList INTERNAL_BL = ByteList.create("internal");
-    private static final ByteList LOCALE_BL = ByteList.create("locale");
-    private static final ByteList FILESYSTEM_BL = ByteList.create("filesystem");
-
     public final Encoding toEncoding(Ruby runtime) {
-        if (!value.getEncoding().isAsciiCompatible()) {
-            throw runtime.newArgumentError("invalid name encoding (non ASCII)");
-        }
-        Entry entry = runtime.getEncodingService().findEncodingOrAliasEntry(value);
-        if (entry == null) {
-            // attempt to look it up with one of the special aliases
-            if (value.equal(EXTERNAL_BL)) return runtime.getDefaultExternalEncoding();
-            else if(value.equal(INTERNAL_BL)) return runtime.getDefaultInternalEncoding();
-            else if(value.equal(LOCALE_BL)) {
-                entry = runtime.getEncodingService().findEncodingOrAliasEntry(ByteList.create(Charset.defaultCharset().name()));
-            } else if (value.equal(FILESYSTEM_BL)) {
-                // This needs to do something different on Windows. See encoding.c,
-                // in the enc_set_filesystem_encoding function.
-                return runtime.getDefaultExternalEncoding();
-            } else {
-                throw runtime.newArgumentError("unknown encoding name - " + value);
-            }
-        }
-        return entry.getEncoding();
+        return runtime.getEncodingService().findEncoding(this);
     }
 
     public final int getCodeRange() {
         return flags & CR_MASK;
     }
 
     public final void setCodeRange(int codeRange) {
         flags |= codeRange & CR_MASK;
     }
 
     public final RString getRString() {
         return rstring;
     }
 
     public final void setRString(RString rstring) {
         this.rstring = rstring;
     }
 
     public final void clearCodeRange() {
         flags &= ~CR_MASK;
     }
 
     private void keepCodeRange() {
         if (getCodeRange() == CR_BROKEN) clearCodeRange();
     }
 
     // ENC_CODERANGE_ASCIIONLY
     public final boolean isCodeRangeAsciiOnly() {
         return getCodeRange() == CR_7BIT;
     }
 
     // rb_enc_str_asciionly_p
     public final boolean isAsciiOnly() {
         return value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT;
     }
 
     public final boolean isCodeRangeValid() {
         return (flags & CR_VALID) != 0;
     }
 
     public final boolean isCodeRangeBroken() {
         return (flags & CR_BROKEN) != 0;
     }
 
     static int codeRangeAnd(int cr1, int cr2) {
         if (cr1 == CR_7BIT) return cr2;
         if (cr1 == CR_VALID) return cr2 == CR_7BIT ? CR_VALID : cr2;
         return CR_UNKNOWN;
     }
 
     private void copyCodeRangeForSubstr(RubyString from, Encoding enc) {
         int fromCr = from.getCodeRange();
         if (fromCr == CR_7BIT) {
             setCodeRange(fromCr);
         } else if (fromCr == CR_VALID) {
             if (!enc.isAsciiCompatible() || searchNonAscii(value) != -1) {
                 setCodeRange(CR_VALID);
             } else {
                 setCodeRange(CR_7BIT);
             }
         } else{ 
             if (value.getRealSize() == 0) {
                 setCodeRange(!enc.isAsciiCompatible() ? CR_VALID : CR_7BIT);
             }
         }
     }
 
     private void copyCodeRange(RubyString from) {
         value.setEncoding(from.value.getEncoding());
         setCodeRange(from.getCodeRange());
     }
 
     // rb_enc_str_coderange
     final int scanForCodeRange() {
         int cr = getCodeRange();
         if (cr == CR_UNKNOWN) {
             cr = codeRangeScan(value.getEncoding(), value);
             setCodeRange(cr);
         }
         return cr;
     }
 
     final boolean singleByteOptimizable() {
         return getCodeRange() == CR_7BIT || value.getEncoding().isSingleByte();
     }
 
     final boolean singleByteOptimizable(Encoding enc) {
         return getCodeRange() == CR_7BIT || enc.isSingleByte();
     }
 
     private Encoding isCompatibleWith(RubyString other) { 
         Encoding enc1 = value.getEncoding();
         Encoding enc2 = other.value.getEncoding();
 
         if (enc1 == enc2) return enc1;
 
         if (other.value.getRealSize() == 0) return enc1;
         if (value.getRealSize() == 0) return enc2;
 
         if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
 
         return RubyEncoding.areCompatible(enc1, scanForCodeRange(), enc2, other.scanForCodeRange());
     }
 
     final Encoding isCompatibleWith(EncodingCapable other) {
         if (other instanceof RubyString) return checkEncoding((RubyString)other);
         Encoding enc1 = value.getEncoding();
         Encoding enc2 = other.getEncoding();
 
         if (enc1 == enc2) return enc1;
         if (value.getRealSize() == 0) return enc2;
         if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
         if (enc2 instanceof USASCIIEncoding) return enc1;
         if (scanForCodeRange() == CR_7BIT) return enc2;
         return null;
     }
 
     final Encoding checkEncoding(RubyString other) {
         Encoding enc = isCompatibleWith(other);
         if (enc == null) throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + 
                                 value.getEncoding() + " and " + other.value.getEncoding());
         return enc;
     }
 
     final Encoding checkEncoding(EncodingCapable other) {
         Encoding enc = isCompatibleWith(other);
         if (enc == null) throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + 
                                 value.getEncoding() + " and " + other.getEncoding());
         return enc;
     }
 
     private Encoding checkDummyEncoding() {
         Encoding enc = value.getEncoding();
         if (enc.isDummy()) throw getRuntime().newEncodingCompatibilityError(
                 "incompatible encoding with this operation: " + enc);
         return enc;
     }
 
     private boolean isComparableWith(RubyString other) {
         ByteList otherValue = other.value;
         if (value.getEncoding() == otherValue.getEncoding() ||
             value.getRealSize() == 0 || otherValue.getRealSize() == 0) return true;
         return isComparableViaCodeRangeWith(other);
     }
 
     private boolean isComparableViaCodeRangeWith(RubyString other) {
         int cr1 = scanForCodeRange();
         int cr2 = other.scanForCodeRange();
 
         if (cr1 == CR_7BIT && (cr2 == CR_7BIT || other.value.getEncoding().isAsciiCompatible())) return true;
         if (cr2 == CR_7BIT && value.getEncoding().isAsciiCompatible()) return true;
         return false;
     }
 
     private int strLength(Encoding enc) {
         if (singleByteOptimizable(enc)) return value.getRealSize();
         return strLength(value, enc);
     }
 
     final int strLength() {
         if (singleByteOptimizable()) return value.getRealSize();
         return strLength(value);
     }
 
     private int strLength(ByteList bytes) {
         return strLength(bytes, bytes.getEncoding());
     }
 
     private int strLength(ByteList bytes, Encoding enc) {
         if (isCodeRangeValid() && enc instanceof UTF8Encoding) return StringSupport.utf8Length(value);
 
         long lencr = strLengthWithCodeRange(bytes, enc);
         int cr = unpackArg(lencr);
         if (cr != 0) setCodeRange(cr);
         return unpackResult(lencr);
     }
 
     final int subLength(int pos) {
         if (singleByteOptimizable() || pos < 0) return pos;
         return StringSupport.strLength(value.getEncoding(), value.getUnsafeBytes(), value.getBegin(), value.getBegin() + pos);
     }
 
     /** short circuit for String key comparison
      * 
      */
     @Override
     public final boolean eql(IRubyObject other) {
         Ruby runtime = getRuntime();
         if (getMetaClass() != runtime.getString() || getMetaClass() != other.getMetaClass()) return super.eql(other);
         return runtime.is1_9() ? eql19(runtime, other) : eql18(runtime, other);
     }
 
     private boolean eql18(Ruby runtime, IRubyObject other) {
         return value.equal(((RubyString)other).value);
     }
 
     // rb_str_hash_cmp
     private boolean eql19(Ruby runtime, IRubyObject other) {
         RubyString otherString = (RubyString)other;
         return isComparableWith(otherString) && value.equal(((RubyString)other).value);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass) {
         this(runtime, rubyClass, EMPTY_BYTE_ARRAY);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, CharSequence value) {
         super(runtime, rubyClass);
         assert value != null;
         byte[] bytes = RubyEncoding.encodeUTF8(value);
         this.value = new ByteList(bytes, false);
         this.value.setEncoding(UTF8);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, byte[] value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = new ByteList(value);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = value;
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, boolean objectSpace) {
         super(runtime, rubyClass, objectSpace);
         assert value != null;
         this.value = value;
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding enc, int cr) {
         this(runtime, rubyClass, value);
         value.setEncoding(enc);
         flags |= cr;
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding enc) {
         this(runtime, rubyClass, value);
         value.setEncoding(enc);
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, int cr) {
         this(runtime, rubyClass, value);
         flags |= cr;
     }
 
     // Deprecated String construction routines
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *  @deprecated  
      */
     @Deprecated
     public RubyString newString(CharSequence s) {
         return new RubyString(getRuntime(), getType(), s);
     }
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *  @deprecated
      */
     @Deprecated
     public RubyString newString(ByteList s) {
         return new RubyString(getRuntime(), getMetaClass(), s);
     }
 
     @Deprecated
     public static RubyString newString(Ruby runtime, RubyClass clazz, CharSequence str) {
         return new RubyString(runtime, clazz, str);
     }
 
     public static RubyString newStringLight(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes, false);
     }
 
     public static RubyString newStringLight(Ruby runtime, int size) {
         return new RubyString(runtime, runtime.getString(), new ByteList(size), false);
     }
   
     public static RubyString newString(Ruby runtime, CharSequence str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
 
     public static RubyString newString(Ruby runtime, String str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
     
     public static RubyString newString(Ruby runtime, byte[] bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newString(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] copy = new byte[length];
         System.arraycopy(bytes, start, copy, 0, length);
         return new RubyString(runtime, runtime.getString(), new ByteList(copy, false));
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes, Encoding encoding) {
         return new RubyString(runtime, runtime.getString(), bytes, encoding);
     }
     
     public static RubyString newUnicodeString(Ruby runtime, String str) {
         return new RubyString(runtime, runtime.getString(), new ByteList(RubyEncoding.encodeUTF8(str), false));
     }
 
     // String construction routines by NOT byte[] buffer and making the target String shared 
     public static RubyString newStringShared(Ruby runtime, RubyString orig) {
         orig.shareLevel = SHARE_LEVEL_BYTELIST;
         RubyString str = new RubyString(runtime, runtime.getString(), orig.value);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }       
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes) {
         return newStringShared(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes, Encoding encoding) {
         return newStringShared(runtime, runtime.getString(), bytes, encoding);
     }
 
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes, int codeRange) {
         RubyString str = new RubyString(runtime, runtime.getString(), bytes, codeRange);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes) {
         RubyString str = new RubyString(runtime, clazz, bytes);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes, Encoding encoding) {
         RubyString str = new RubyString(runtime, clazz, bytes, encoding);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, byte[] bytes) {
         return newStringShared(runtime, new ByteList(bytes, false));
     }
 
     public static RubyString newStringShared(Ruby runtime, byte[] bytes, int start, int length) {
         return newStringShared(runtime, new ByteList(bytes, start, length, false));
     }
 
     public static RubyString newEmptyString(Ruby runtime) {
         return newEmptyString(runtime, runtime.getString());
     }
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass) {
         RubyString empty = new RubyString(runtime, metaClass, ByteList.EMPTY_BYTELIST);
         empty.shareLevel = SHARE_LEVEL_BYTELIST;
         return empty;
     }
 
     // String construction routines by NOT byte[] buffer and NOT making the target String shared 
     public static RubyString newStringNoCopy(Ruby runtime, ByteList bytes) {
         return newStringNoCopy(runtime, runtime.getString(), bytes);
     }    
 
     public static RubyString newStringNoCopy(Ruby runtime, RubyClass clazz, ByteList bytes) {
         return new RubyString(runtime, clazz, bytes);
     }    
 
     public static RubyString newStringNoCopy(Ruby runtime, byte[] bytes, int start, int length) {
         return newStringNoCopy(runtime, new ByteList(bytes, start, length, false));
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, byte[] bytes) {
         return newStringNoCopy(runtime, new ByteList(bytes, false));
     }
 
     /** Encoding aware String construction routines for 1.9
      * 
      */
     private static final class EmptyByteListHolder {
         final ByteList bytes;
         final int cr;
         EmptyByteListHolder(Encoding enc) {
             this.bytes = new ByteList(ByteList.NULL_ARRAY, enc);
             this.cr = bytes.getEncoding().isAsciiCompatible() ? CR_7BIT : CR_VALID;
         }
     }
 
     private static EmptyByteListHolder EMPTY_BYTELISTS[] = new EmptyByteListHolder[4];
 
     static EmptyByteListHolder getEmptyByteList(Encoding enc) {
         int index = enc.getIndex();
         EmptyByteListHolder bytes;
         if (index < EMPTY_BYTELISTS.length && (bytes = EMPTY_BYTELISTS[index]) != null) {
             return bytes;
         }
         return prepareEmptyByteList(enc);
     }
 
     private static EmptyByteListHolder prepareEmptyByteList(Encoding enc) {
         int index = enc.getIndex();
         if (index >= EMPTY_BYTELISTS.length) {
             EmptyByteListHolder tmp[] = new EmptyByteListHolder[index + 4];
             System.arraycopy(EMPTY_BYTELISTS,0, tmp, 0, EMPTY_BYTELISTS.length);
             EMPTY_BYTELISTS = tmp;
         }
         return EMPTY_BYTELISTS[index] = new EmptyByteListHolder(enc);
     }
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass, Encoding enc) {
         EmptyByteListHolder holder = getEmptyByteList(enc);
         RubyString empty = new RubyString(runtime, metaClass, holder.bytes, holder.cr);
         empty.shareLevel = SHARE_LEVEL_BYTELIST;
         return empty;
     }
 
     public static RubyString newEmptyString(Ruby runtime, Encoding enc) {
         return newEmptyString(runtime, runtime.getString(), enc);
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, RubyClass clazz, ByteList bytes, Encoding enc, int cr) {
         return new RubyString(runtime, clazz, bytes, enc, cr);
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, ByteList bytes, Encoding enc, int cr) {
         return newStringNoCopy(runtime, runtime.getString(), bytes, enc, cr);
     }
 
     public static RubyString newUsAsciiStringNoCopy(Ruby runtime, ByteList bytes) {
         return newStringNoCopy(runtime, bytes, USASCIIEncoding.INSTANCE, CR_7BIT);
     }
 
     public static RubyString newUsAsciiStringShared(Ruby runtime, ByteList bytes) {
         RubyString str = newStringNoCopy(runtime, bytes, USASCIIEncoding.INSTANCE, CR_7BIT);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
     
     public static RubyString newUsAsciiStringShared(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] copy = new byte[length];
         System.arraycopy(bytes, start, copy, 0, length);
         return newUsAsciiStringShared(runtime, new ByteList(copy, false));
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.STRING;
     }
 
     @Override
     public Class getJavaClass() {
         return String.class;
     }
 
     @Override
     public RubyString convertToString() {
         return this;
     }
 
     @Override
     public String toString() {
         return value.toString();
     }
 
     /** rb_str_dup
      * 
      */
     @Deprecated
     public final RubyString strDup() {
         return strDup(getRuntime(), getMetaClass());
     }
     
     public final RubyString strDup(Ruby runtime) {
         return strDup(runtime, getMetaClass());
     }
     
     @Deprecated
     final RubyString strDup(RubyClass clazz) {
         return strDup(getRuntime(), getMetaClass());
     }
 
     final RubyString strDup(Ruby runtime, RubyClass clazz) {
         shareLevel = SHARE_LEVEL_BYTELIST;
         RubyString dup = new RubyString(runtime, clazz, value);
         dup.shareLevel = SHARE_LEVEL_BYTELIST;
         dup.flags |= flags & (CR_MASK | TAINTED_F | UNTRUSTED_F);
 
         return dup;
     }
 
     /* rb_str_subseq */
     public final RubyString makeSharedString(Ruby runtime, int index, int len) {
         return makeShared(runtime, runtime.getString(), index, len);
     }
 
     public final RubyString makeShared(Ruby runtime, int index, int len) {
         return makeShared(runtime, getType(), index, len);
     }
 
     public final RubyString makeShared(Ruby runtime, RubyClass meta, int index, int len) {
         final RubyString shared;
         if (len == 0) {
             shared = newEmptyString(runtime, meta);
         } else if (len == 1) {
             shared = newStringShared(runtime, meta, 
                     RubyInteger.SINGLE_CHAR_BYTELISTS[value.getUnsafeBytes()[value.getBegin() + index] & 0xff]);
         } else {
             if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
             shared = new RubyString(runtime, meta, value.makeShared(index, len));
             shared.shareLevel = SHARE_LEVEL_BUFFER;
         }
 
         shared.infectBy(this);
         return shared;
     }
 
     public final RubyString makeShared19(Ruby runtime, int index, int len) {
         return makeShared19(runtime, value, index, len);
     }
 
     private RubyString makeShared19(Ruby runtime, ByteList value, int index, int len) {
         final RubyString shared;
         Encoding enc = value.getEncoding();
         RubyClass meta = getType();
 
         if (len == 0) {
             shared = newEmptyString(runtime, meta, enc);
         } else {
             if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
             shared = new RubyString(runtime, meta, value.makeShared(index, len));
             shared.shareLevel = SHARE_LEVEL_BUFFER;
             shared.copyCodeRangeForSubstr(this, enc); // no need to assign encoding, same bytelist shared
         }
         shared.infectBy(this);
         return shared;
     }
 
     final void modifyCheck() {
         frozenCheck();
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify string");
         }
     }
 
     private final void modifyCheck(byte[] b, int len) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len) throw getRuntime().newRuntimeError("string modified");
     }
 
     private final void modifyCheck(byte[] b, int len, Encoding enc) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len || value.getEncoding() != enc) throw getRuntime().newRuntimeError("string modified");
     }
 
     private void frozenCheck() {
         frozenCheck(false);
     }
 
     private void frozenCheck(boolean runtimeError) {
         if (isFrozen()) throw getRuntime().newFrozenError("string", runtimeError);
     }
 
     /** rb_str_modify
      * 
      */
     public final void modify() {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 value = value.dup();
             } else {
                 value.unshare();
             }
             shareLevel = SHARE_LEVEL_NONE;
         }
 
         value.invalidate();
     }
 
     public final void modify19() {
         modify();
         clearCodeRange();
     }
 
     private void modifyAndKeepCodeRange() {
         modify();
         keepCodeRange();
     }
     
     /** rb_str_modify (with length bytes ensured)
      * 
      */    
     public final void modify(int length) {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 value = value.dup(length);
             } else {
                 value.unshare(length);
             }
             shareLevel = SHARE_LEVEL_NONE;
         } else {
             value.ensure(length);
         }
 
         value.invalidate();
     }
     
     public final void modify19(int length) {
         modify(length);
         clearCodeRange();
     }
     
     /** rb_str_resize
      */
     public final void resize(int length) {
         modify();
         if (value.getRealSize() > length) {
             value.setRealSize(length);
         } else if (value.length() < length) {
             value.length(length);
         }
     }
 
     final void view(ByteList bytes) {
         modifyCheck();
 
         value = bytes;
         shareLevel = SHARE_LEVEL_NONE;
     }
 
     private final void view(byte[]bytes) {
         modifyCheck();        
 
         value.replace(bytes);
         shareLevel = SHARE_LEVEL_NONE;
 
         value.invalidate();        
     }
 
     private final void view(int index, int len) {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 // if len == 0 then shared empty
                 value = value.makeShared(index, len);
                 shareLevel = SHARE_LEVEL_BUFFER;
             } else {
                 value.view(index, len);
             }
         } else {        
             value.view(index, len);
             // FIXME this below is temporary, but its much safer for COW (it prevents not shared Strings with begin != 0)
             // this allows now e.g.: ByteList#set not to be begin aware
             shareLevel = SHARE_LEVEL_BUFFER;
         }
 
         value.invalidate();
     }
 
     public static String bytesToString(byte[] bytes, int beg, int len) {
         return new String(ByteList.plain(bytes, beg, len));
     }
 
     public static String byteListToString(ByteList bytes) {
         return bytesToString(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
     }
 
     public static String bytesToString(byte[] bytes) {
         return bytesToString(bytes, 0, bytes.length);
     }
 
     public static byte[] stringToBytes(String string) {
         return ByteList.plain(string);
     }
 
     @Override
     public RubyString asString() {
         return this;
     }
 
     @Override
     public IRubyObject checkStringType() {
         return this;
     }
 
     @Override
     public IRubyObject checkStringType19() {
         return this;
     }
 
     @JRubyMethod(name = "try_convert", meta = true, compat = RUBY1_9)
     public static IRubyObject try_convert(ThreadContext context, IRubyObject recv, IRubyObject str) {
         return str.checkStringType();
     }
 
     @JRubyMethod(name = {"to_s", "to_str"})
     @Override
     public IRubyObject to_s() {
         Ruby runtime = getRuntime();
         if (getMetaClass().getRealClass() != runtime.getString()) {
             return strDup(runtime, runtime.getString());
         }
         return this;
     }
 
     @Override
     public final int compareTo(IRubyObject other) {
         Ruby runtime = getRuntime();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             return runtime.is1_9() ? op_cmp19(otherString) : op_cmp(otherString);
         }
         return (int)op_cmpCommon(runtime.getCurrentContext(), other).convertToInteger().getLongValue();
     }
 
     /* rb_str_cmp_m */
     @JRubyMethod(name = "<=>", compat = RUBY1_8)
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.getRuntime().newFixnum(op_cmp((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     @JRubyMethod(name = "<=>", compat = RUBY1_9)
     public IRubyObject op_cmp19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.getRuntime().newFixnum(op_cmp19((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     private IRubyObject op_cmpCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         // deal with case when "other" is not a string
         if (other.respondsTo("to_str") && other.respondsTo("<=>")) {
             IRubyObject result = other.callMethod(context, "<=>", this);
             if (result.isNil()) return result;
             if (result instanceof RubyFixnum) {
                 return RubyFixnum.newFixnum(runtime, -((RubyFixnum)result).getLongValue());
             } else {
                 return RubyFixnum.zero(runtime).callMethod(context, "-", result);
             }
         }
         return runtime.getNil();        
     }
         
     /** rb_str_equal
      * 
      */
     @JRubyMethod(name = "==", compat = RUBY1_8)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             return value.equal(((RubyString)other).value) ? runtime.getTrue() : runtime.getFalse();    
         }
         return op_equalCommon(context, other);
     }
 
     @JRubyMethod(name = "==", compat = RUBY1_9)
     public IRubyObject op_equal19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             return isComparableWith(otherString) && value.equal(otherString.value) ? runtime.getTrue() : runtime.getFalse();    
         }
         return op_equalCommon(context, other);
     }
 
     private IRubyObject op_equalCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (!other.respondsTo("to_str")) return runtime.getFalse();
         return other.callMethod(context, "==", this).isTrue() ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_8, argTypes = RubyString.class)
     public IRubyObject op_plus(ThreadContext context, RubyString str) {
         RubyString resultStr = newString(context.getRuntime(), addByteLists(value, str.value));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
     public IRubyObject op_plus(ThreadContext context, IRubyObject other) {
         return op_plus(context, other.convertToString());
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_9)
     public IRubyObject op_plus19(ThreadContext context, RubyString str) {
         Encoding enc = checkEncoding(str);
         RubyString resultStr = newStringNoCopy(context.getRuntime(), addByteLists(value, str.value),
                                     enc, codeRangeAnd(getCodeRange(), str.getCodeRange()));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
     public IRubyObject op_plus19(ThreadContext context, IRubyObject other) {
         return op_plus19(context, other.convertToString());
     }
 
     private ByteList addByteLists(ByteList value1, ByteList value2) {
         ByteList result = new ByteList(value1.getRealSize() + value2.getRealSize());
         result.setRealSize(value1.getRealSize() + value2.getRealSize());
         System.arraycopy(value1.getUnsafeBytes(), value1.getBegin(), result.getUnsafeBytes(), 0, value1.getRealSize());
         System.arraycopy(value2.getUnsafeBytes(), value2.getBegin(), result.getUnsafeBytes(), value1.getRealSize(), value2.getRealSize());
         return result;
     }
 
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_8)
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         return multiplyByteList(context, other);
     }
 
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_9)
     public IRubyObject op_mul19(ThreadContext context, IRubyObject other) {
         RubyString result = multiplyByteList(context, other);
         result.value.setEncoding(value.getEncoding());
         result.copyCodeRange(this);
         return result;
     }
 
     private RubyString multiplyByteList(ThreadContext context, IRubyObject arg) {
         int len = RubyNumeric.num2int(arg);
         if (len < 0) throw context.getRuntime().newArgumentError("negative argument");
 
         // we limit to int because ByteBuffer can only allocate int sizes
         if (len > 0 && Integer.MAX_VALUE / len < value.getRealSize()) {
             throw context.getRuntime().newArgumentError("argument too big");
         }
 
         ByteList bytes = new ByteList(len *= value.getRealSize());
         if (len > 0) {
             bytes.setRealSize(len);
             int n = value.getRealSize();
             System.arraycopy(value.getUnsafeBytes(), value.getBegin(), bytes.getUnsafeBytes(), 0, n);
             while (n <= len >> 1) {
                 System.arraycopy(bytes.getUnsafeBytes(), 0, bytes.getUnsafeBytes(), n, n);
                 n <<= 1;
             }
             System.arraycopy(bytes.getUnsafeBytes(), 0, bytes.getUnsafeBytes(), n, len - n);
         }
         RubyString result = new RubyString(context.getRuntime(), getMetaClass(), bytes);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "%", required = 1)
     public IRubyObject op_format(ThreadContext context, IRubyObject arg) {
         return opFormatCommon(context, arg, context.getRuntime().getInstanceConfig().getCompatVersion());
     }
 
     private IRubyObject opFormatCommon(ThreadContext context, IRubyObject arg, CompatVersion compat) {
         IRubyObject tmp = arg.checkArrayType();
         if (tmp.isNil()) tmp = arg;
 
         // FIXME: Should we make this work with platform's locale,
         // or continue hardcoding US?
         ByteList out = new ByteList(value.getRealSize());
         boolean tainted;
         switch (compat) {
         case RUBY1_8:
             tainted = Sprintf.sprintf(out, Locale.US, value, tmp);
             break;
         case RUBY1_9:
             tainted = Sprintf.sprintf1_9(out, Locale.US, value, tmp);
             break;
         default:
             throw new RuntimeException("invalid compat version for sprintf: " + compat);
         }
         RubyString str = newString(context.getRuntime(), out);
 
         str.setTaint(tainted || isTaint());
         return str;
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
         Ruby runtime = getRuntime();
         return RubyFixnum.newFixnum(runtime, strHashCode(runtime));
     }
 
     @Override
     public int hashCode() {
         return strHashCode(getRuntime());
     }
 
     private int strHashCode(Ruby runtime) {
         if (runtime.is1_9()) {
             return value.hashCode() ^ (value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT ? 0 : value.getEncoding().getIndex());
         } else {
             return value.hashCode();
         }
     }
 
     @Override
     public boolean equals(Object other) {
         if (this == other) return true;
 
         if (other instanceof RubyString) {
             if (((RubyString) other).value.equal(value)) return true;
         }
 
         return false;
     }
 
     /** rb_obj_as_string
      *
      */
     public static RubyString objAsString(ThreadContext context, IRubyObject obj) {
         if (obj instanceof RubyString) return (RubyString) obj;
         IRubyObject str = obj.callMethod(context, "to_s");
         if (!(str instanceof RubyString)) return (RubyString) obj.anyToString();
         if (obj.isTaint()) str.setTaint(true);
         return (RubyString) str;
     }
 
     /** rb_str_cmp
      *
      */
     public final int op_cmp(RubyString other) {
         return value.cmp(other.value);
     }
 
     public final int op_cmp19(RubyString other) {
         int ret = value.cmp(other.value);
         if (ret == 0 && !isComparableWith(other)) {
             return value.getEncoding().getIndex() > other.value.getEncoding().getIndex() ? 1 : -1;
         }
         return ret;
     }
 
     /** rb_to_id
      *
      */
     @Override
     public String asJavaString() {
         return toString();
     }
 
     public IRubyObject doClone(){
         return newString(getRuntime(), value.dup());
     }
 
     public final RubyString cat(byte[] str) {
         modify(value.getRealSize() + str.length);
         System.arraycopy(str, 0, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), str.length);
         value.setRealSize(value.getRealSize() + str.length);
         return this;
     }
 
     public final RubyString cat(byte[] str, int beg, int len) {
         modify(value.getRealSize() + len);
         System.arraycopy(str, beg, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
         value.setRealSize(value.getRealSize() + len);
         return this;
     }
 
     // // rb_str_buf_append
     public final RubyString cat19(RubyString str) {
         ByteList strValue = str.value;
         int strCr = str.getCodeRange();
         strCr = cat(strValue.getUnsafeBytes(), strValue.getBegin(), strValue.getRealSize(), strValue.getEncoding(), strCr, strCr);
         infectBy(str);
         str.setCodeRange(strCr);
         return this;
     }
 
     public final RubyString cat(ByteList str) {
         modify(value.getRealSize() + str.getRealSize());
         System.arraycopy(str.getUnsafeBytes(), str.getBegin(), value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), str.getRealSize());
         value.setRealSize(value.getRealSize() + str.getRealSize());
         return this;
     }
 
     public final RubyString cat(byte ch) {
         modify(value.getRealSize() + 1);
         value.getUnsafeBytes()[value.getBegin() + value.getRealSize()] = ch;
         value.setRealSize(value.getRealSize() + 1);
         return this;
     }
 
     public final RubyString cat(int ch) {
         return cat((byte)ch);
     }
 
     public final RubyString cat(int code, Encoding enc) {
         int n = codeLength(getRuntime(), enc, code);
         modify(value.getRealSize() + n);
@@ -6074,1413 +6051,1413 @@ public class RubyString extends RubyObject implements EncodingCapable {
     public IRubyObject squeeze_bang19(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         RubyString otherStr = args[0].convertToString();
         Encoding enc = checkEncoding(otherStr);
         final boolean squeeze[] = new boolean[TRANS_SIZE];
         TrTables tables = otherStr.trSetupTable(runtime, squeeze, null, true, enc);
 
         boolean singlebyte = singleByteOptimizable() && otherStr.singleByteOptimizable();
         for (int i=1; i<args.length; i++) {
             otherStr = args[i].convertToString();
             enc = checkEncoding(otherStr);
             singlebyte = singlebyte && otherStr.singleByteOptimizable();
             tables = otherStr.trSetupTable(runtime, squeeze, tables, false, enc);
         }
 
         modifyAndKeepCodeRange();
         if (singlebyte) {
             return squeezeCommon(runtime, squeeze); // 1.8
         } else {
             return squeezeCommon19(runtime, squeeze, tables, enc, true);
         }
     }
 
     private IRubyObject squeezeCommon19(Ruby runtime, boolean squeeze[], TrTables tables, Encoding enc, boolean isArg) {
         int s = value.getBegin();
         int t = s;
         int send = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
         int save = -1;
         int c;
 
         while (s < send) {
             if (enc.isAsciiCompatible() && Encoding.isAscii(c = bytes[s] & 0xff)) {
                 if (c != save || (isArg && !squeeze[c])) bytes[t++] = (byte)(save = c);
                 s++;
             } else {
                 c = codePoint(runtime, enc, bytes, s, send);
                 int cl = codeLength(runtime, enc, c);
                 if (c != save || (isArg && !trFind(c, squeeze, tables))) {
                     if (t != s) enc.codeToMbc(c, bytes, t);
                     save = c;
                     t += cl;
                 }
                 s += cl;
             }
         }
 
         if (t - value.getBegin() != value.getRealSize()) { // modified
             value.setRealSize(t - value.getBegin());
             return this;
         }
 
         return runtime.getNil();   
     }
 
     /** rb_str_tr / rb_str_tr_bang
      *
      */
     @JRubyMethod(name = "tr", compat = RUBY1_8)
     public IRubyObject tr(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.trTrans(context, src, repl, false);
         return str;
     }
 
     @JRubyMethod(name = "tr!", compat = RUBY1_8)
     public IRubyObject tr_bang(ThreadContext context, IRubyObject src, IRubyObject repl) {
         return trTrans(context, src, repl, false);
     }    
 
     @JRubyMethod(name = "tr", compat = RUBY1_9)
     public IRubyObject tr19(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.trTrans19(context, src, repl, false);
         return str;
     }
 
     @JRubyMethod(name = "tr!")
     public IRubyObject tr_bang19(ThreadContext context, IRubyObject src, IRubyObject repl) {
         return trTrans19(context, src, repl, false);
     }    
 
     private static final class TR {
         TR(ByteList bytes) {
             p = bytes.getBegin();
             pend = bytes.getRealSize() + p;
             buf = bytes.getUnsafeBytes();
             now = max = 0;
             gen = false;
         }
 
         int p, pend, now, max;
         boolean gen;
         byte[]buf;
     }
 
     private static final int TRANS_SIZE = 256;
 
     /** tr_setup_table
      * 
      */
     private void trSetupTable(boolean[]table, boolean init) {
         final TR tr = new TR(value);
         boolean cflag = false;
         if (value.getRealSize() > 1 && value.getUnsafeBytes()[value.getBegin()] == '^') {
             cflag = true;
             tr.p++;
         }
 
         if (init) for (int i=0; i<TRANS_SIZE; i++) table[i] = true;
 
         final boolean[]buf = new boolean[TRANS_SIZE];
         for (int i=0; i<TRANS_SIZE; i++) buf[i] = cflag;
 
         int c;
         while ((c = trNext(tr)) >= 0) buf[c & 0xff] = !cflag;
         for (int i=0; i<TRANS_SIZE; i++) table[i] = table[i] && buf[i];
     }
 
     private static final class TrTables {
         private IntHash<IRubyObject> del, noDel;
     }
 
     private TrTables trSetupTable(Ruby runtime, boolean[]table, TrTables tables, boolean init, Encoding enc) {
         final TR tr = new TR(value);
         boolean cflag = false;
         if (value.getRealSize() > 1) {
             if (enc.isAsciiCompatible()) {
                 if ((value.getUnsafeBytes()[value.getBegin()] & 0xff) == '^') {
                     cflag = true;
                     tr.p++;
                 }
             } else {
                 int l = StringSupport.preciseLength(enc, tr.buf, tr.p, tr.pend);
                 if (enc.mbcToCode(tr.buf, tr.p, tr.pend) == '^') {
                     cflag = true;
                     tr.p += l;
                 }
             }
         }
 
         if (init) for (int i=0; i<TRANS_SIZE; i++) table[i] = true;
 
         final boolean[]buf = new boolean[TRANS_SIZE];
         for (int i=0; i<TRANS_SIZE; i++) buf[i] = cflag;
 
         int c;
         IntHash<IRubyObject> hash = null, phash = null;
         while ((c = trNext(tr, runtime, enc)) >= 0) {
             if (c < TRANS_SIZE) {
                 buf[c & 0xff] = !cflag;
             } else {
                 if (hash == null) {
                     hash = new IntHash<IRubyObject>();
                     if (tables == null) tables = new TrTables();
                     if (cflag) {
                         phash = tables.noDel;
                         tables.noDel = hash;
                     } else {
                         phash  = tables.del;
                         tables.del = hash;
                     }
                 }
                 if (phash == null || phash.get(c) != null) hash.put(c, NEVER);
             }
         }
 
         for (int i=0; i<TRANS_SIZE; i++) table[i] = table[i] && buf[i];
         return tables;
     }
 
     private boolean trFind(int c, boolean[]table, TrTables tables) {
         return c < TRANS_SIZE ? table[c] : tables != null && 
                 ((tables.del != null && tables.del.get(c) != null) &&
                 (tables.noDel == null || tables.noDel.get(c) == null));
     }
 
     /** tr_trans
     *
     */    
     private IRubyObject trTrans(ThreadContext context, IRubyObject src, IRubyObject repl, boolean sflag) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) return runtime.getNil();
 
         ByteList replList = repl.convertToString().value;
         if (replList.getRealSize() == 0) return delete_bang(context, src);
 
         ByteList srcList = src.convertToString().value;
         final TR trSrc = new TR(srcList);
         boolean cflag = false;
         if (srcList.getRealSize() >= 2 && srcList.getUnsafeBytes()[srcList.getBegin()] == '^') {
             cflag = true;
             trSrc.p++;
         }       
 
         int c;
         final int[]trans = new int[TRANS_SIZE];
         final TR trRepl = new TR(replList);
         if (cflag) {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = 1;
             while ((c = trNext(trSrc)) >= 0) trans[c & 0xff] = -1;
             while ((c = trNext(trRepl)) >= 0) {}
             for (int i=0; i<TRANS_SIZE; i++) {
                 if (trans[i] >= 0) trans[i] = trRepl.now;
             }
         } else {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = -1;
             while ((c = trNext(trSrc)) >= 0) {
                 int r = trNext(trRepl);
                 if (r == -1) r = trRepl.now;
                 trans[c & 0xff] = r;
             }
         }
 
         modify();
 
         int s = value.getBegin();
         int send = s + value.getRealSize();
         byte sbytes[] = value.getUnsafeBytes();
         boolean modify = false;
         if (sflag) {
             int t = s;
             int last = -1;
             while (s < send) {
                 int c0 = sbytes[s++];
                 if ((c = trans[c0 & 0xff]) >= 0) {
                     if (last == c) continue;
                     last = c;
                     sbytes[t++] = (byte)(c & 0xff);
                     modify = true;
                 } else {
                     last = -1;
                     sbytes[t++] = (byte)c0;
                 }
             }
 
             if (value.getRealSize() > (t - value.getBegin())) {
                 value.setRealSize(t - value.getBegin());
                 modify = true;
             }
         } else {
             while (s < send) {
                 if ((c = trans[sbytes[s] & 0xff]) >= 0) {
                     sbytes[s] = (byte)(c & 0xff);
                     modify = true;
                 }
                 s++;
             }
         }
 
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject trTrans19(ThreadContext context, IRubyObject src, IRubyObject repl, boolean sflag) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) return runtime.getNil();
 
         RubyString replStr = repl.convertToString();
         ByteList replList = replStr.value;
         if (replList.getRealSize() == 0) return delete_bang19(context, src);
 
         RubyString srcStr = src.convertToString();
         ByteList srcList = srcStr.value;
         Encoding e1 = checkEncoding(srcStr);
         Encoding e2 = checkEncoding(replStr);
         Encoding enc = e1 == e2 ? e1 : srcStr.checkEncoding(replStr);
 
         int cr = getCodeRange();
 
         final TR trSrc = new TR(srcList);
         boolean cflag = false;
         if (value.getRealSize() > 1) {
             if (enc.isAsciiCompatible()) {
                 if (trSrc.buf.length > 0 && (trSrc.buf[trSrc.p] & 0xff) == '^' && trSrc.p + 1 < trSrc.pend) {
                     cflag = true;
                     trSrc.p++;
                 }
             } else {
                 int cl = StringSupport.preciseLength(enc, trSrc.buf, trSrc.p, trSrc.pend);
                 if (enc.mbcToCode(trSrc.buf, trSrc.p, trSrc.pend) == '^' && trSrc.p + cl < trSrc.pend) {
                     cflag = true;
                     trSrc.p += cl;
                 }
             }            
         }
 
         boolean singlebyte = true;
         int c;
         final int[]trans = new int[TRANS_SIZE];
         IntHash<Integer> hash = null;
         final TR trRepl = new TR(replList);
 
         if (cflag) {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = 1;
             
             while ((c = trNext(trSrc, runtime, enc)) >= 0) {
                 if (c < TRANS_SIZE) {
                     trans[c & 0xff] = -1;
                 } else {
                     if (hash == null) hash = new IntHash<Integer>();
                     hash.put(c, 1); // QTRUE
                 }
             }
             while ((c = trNext(trRepl, runtime, enc)) >= 0) {}  /* retrieve last replacer */
             int last = trRepl.now;
             for (int i=0; i<TRANS_SIZE; i++) {
                 if (trans[i] >= 0) trans[i] = last;
             }
         } else {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = -1;
             
             while ((c = trNext(trSrc, runtime, enc)) >= 0) {
                 int r = trNext(trRepl, runtime, enc);
                 if (r == -1) r = trRepl.now;
                 if (c < TRANS_SIZE) {
                     trans[c & 0xff] = r;
                     if (r > TRANS_SIZE - 1) singlebyte = false;
                 } else {
                     if (hash == null) hash = new IntHash<Integer>();
                     hash.put(c, r);
                 }
             }
         }
 
         if (cr == CR_VALID) cr = CR_7BIT;
         modifyAndKeepCodeRange();
         int s = value.getBegin();
         int send = s + value.getRealSize();
         byte sbytes[] = value.getUnsafeBytes();
         int max = value.getRealSize();
         boolean modify = false;
 
         int last = -1;
         int clen, tlen, c0;
 
         if (sflag) {
             int save = -1;
             byte[]buf = new byte[max];
             int t = 0;
             while (s < send) {
                 boolean mayModify = false;
                 c0 = c = codePoint(runtime, e1, sbytes, s, send);
                 clen = codeLength(runtime, e1, c);
                 tlen = enc == e1 ? clen : codeLength(runtime, enc, c);
                 s += clen;
                 c = trCode(c, trans, hash, cflag, last);
 
                 if (c != -1) {
                     if (save == c) {
                         if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                         continue;
                     }
                     save = c;
                     tlen = codeLength(runtime, enc, c);
                     modify = true;
                 } else {
                     save = -1;
                     c = c0;
                     if (enc != e1) mayModify = true;
                 }
 
                 while (t + tlen >= max) {
                     max <<= 1;
                     byte[]tbuf = new byte[max];
                     System.arraycopy(buf, 0, tbuf, 0, buf.length);
                     buf = tbuf;
                 }
                 enc.codeToMbc(c, buf, t);
                 if (mayModify && (tlen == 1 ? sbytes[s] != buf[t] : ByteList.memcmp(sbytes, s, buf, t, tlen) != 0)) modify = true;
                 if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                 t += tlen;
             }
             value.setUnsafeBytes(buf);
             value.setRealSize(t);
         } else if (enc.isSingleByte() || (singlebyte && hash == null)) {
             while (s < send) {
                 c = sbytes[s] & 0xff;
                 if (trans[c] != -1) {
                     if (!cflag) {
                         c = trans[c];
                         sbytes[s] = (byte)c;
                     } else {
                         sbytes[s] = (byte)last;
                     }
                     modify = true;
                 }
                 if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                 s++;
             }
         } else {
             max += max >> 1;
             byte[]buf = new byte[max];
             int t = 0;
 
             while (s < send) {
                 boolean mayModify = false;
                 c0 = c = codePoint(runtime, e1, sbytes, s, send);
                 clen = codeLength(runtime, e1, c);
                 tlen = enc == e1 ? clen : codeLength(runtime, enc, c);
 
                 c = trCode(c, trans, hash, cflag, last);
 
                 if (c != -1) {
                     tlen = codeLength(runtime, enc, c);
                     modify = true;
                 } else {
                     c = c0;
                     if (enc != e1) mayModify = true;
                 }
                 while (t + tlen >= max) {
                     max <<= 1;
                     byte[]tbuf = new byte[max];
                     System.arraycopy(buf, 0, tbuf, 0, buf.length);
                     buf = tbuf;
                 }
 
                 enc.codeToMbc(c, buf, t);
 
                 if (mayModify && (tlen == 1 ? sbytes[s] != buf[t] : ByteList.memcmp(sbytes, s, buf, t, tlen) != 0)) modify = true;
                 if (cr == CR_7BIT && !Encoding.isAscii(c)) cr = CR_VALID;
                 s += clen;
                 t += tlen;
             }
             value.setUnsafeBytes(buf);
             value.setRealSize(t);
         }
 
         if (modify) {
             if (cr != CR_BROKEN) setCodeRange(cr);
             associateEncoding(enc);
             return this;
         }
         return runtime.getNil();
     }
 
     private int trCode(int c, int[]trans, IntHash<Integer> hash, boolean cflag, int last) {
         if (c < TRANS_SIZE) {
             return trans[c];
         } else if (hash != null) {
             Integer tmp = hash.get(c);
             if (tmp == null) {
                 return cflag ? last : -1;
             } else {
                 return cflag ? -1 : tmp;
             }
         } else {
             return -1;
         }
     }
 
     /** trnext
     *
     */    
     private int trNext(TR t) {
         byte[]buf = t.buf;
         
         for (;;) {
             if (!t.gen) {
                 if (t.p == t.pend) return -1;
                 if (t.p < t.pend -1 && buf[t.p] == '\\') t.p++;
                 t.now = buf[t.p++] & 0xff;
                 if (t.p < t.pend - 1 && buf[t.p] == '-') {
                     t.p++;
                     if (t.p < t.pend) {
                         if (t.now > (buf[t.p] & 0xff)) {
                             t.p++;
                             continue;
                         }
                         t.gen = true;
                         t.max = buf[t.p++] & 0xff;
                     }
                 }
                 return t.now;
             } else if (++t.now < t.max) {
                 return t.now;
             } else {
                 t.gen = false;
                 return t.max;
             }
         }
     }
 
     private int trNext(TR t, Ruby runtime, Encoding enc) {
         byte[]buf = t.buf;
         
         for (;;) {
             if (!t.gen) {
                 if (t.p == t.pend) return -1;
                 if (t.p < t.pend -1 && buf[t.p] == '\\') t.p++;
                 t.now = codePoint(runtime, enc, buf, t.p, t.pend);
                 t.p += codeLength(runtime, enc, t.now);
                 if (t.p < t.pend - 1 && buf[t.p] == '-') {
                     t.p++;
                     if (t.p < t.pend) {
                         int c = codePoint(runtime, enc, buf, t.p, t.pend);
                         t.p += codeLength(runtime, enc, c);
                         if (t.now > c) {
                             if (t.now < 0x80 && c < 0x80) {
                                 throw runtime.newArgumentError("invalid range \""
                                         + (char) t.now + "-" + (char) c + "\" in string transliteration");
                             }
 
                             throw runtime.newArgumentError("invalid range in string transliteration");
                         }
                         t.gen = true;
                         t.max = c;
                     }
                 }
                 return t.now;
             } else if (++t.now < t.max) {
                 return t.now;
             } else {
                 t.gen = false;
                 return t.max;
             }
         }
     }
 
     /** rb_str_tr_s / rb_str_tr_s_bang
      *
      */
     @JRubyMethod(name ="tr_s", compat = RUBY1_8)
     public IRubyObject tr_s(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.trTrans(context, src, repl, true);
         return str;
     }
 
     @JRubyMethod(name = "tr_s!", compat = RUBY1_8)
     public IRubyObject tr_s_bang(ThreadContext context, IRubyObject src, IRubyObject repl) {
         return trTrans(context, src, repl, true);
     }
 
     @JRubyMethod(name ="tr_s", compat = RUBY1_9)
     public IRubyObject tr_s19(ThreadContext context, IRubyObject src, IRubyObject repl) {
         RubyString str = strDup(context.getRuntime());
         str.trTrans19(context, src, repl, true);
         return str;
     }
 
     @JRubyMethod(name = "tr_s!", compat = RUBY1_9)
     public IRubyObject tr_s_bang19(ThreadContext context, IRubyObject src, IRubyObject repl) {
         return trTrans19(context, src, repl, true);
     }
 
     /** rb_str_each_line
      *
      */
     public IRubyObject each_line(ThreadContext context, Block block) {
         return each_lineCommon(context, context.getRuntime().getGlobalVariables().get("$/"), block);
     }
 
     public IRubyObject each_line(ThreadContext context, IRubyObject arg, Block block) {
         return each_lineCommon(context, arg, block);
     }
 
     public IRubyObject each_lineCommon(ThreadContext context, IRubyObject sep, Block block) {        
         Ruby runtime = context.getRuntime();
         if (sep.isNil()) {
             block.yield(context, this);
             return this;
         }
 
         RubyString sepStr = sep.convertToString();
         ByteList sepValue = sepStr.value;
         int rslen = sepValue.getRealSize();
 
         final byte newline;
         if (rslen == 0) {
             newline = '\n';
         } else {
             newline = sepValue.getUnsafeBytes()[sepValue.getBegin() + rslen - 1];
         }
 
         int p = value.getBegin();
         int end = p + value.getRealSize();
         int ptr = p, s = p;
         int len = value.getRealSize();
         byte[] bytes = value.getUnsafeBytes();
 
         p += rslen;
 
         for (; p < end; p++) {
             if (rslen == 0 && bytes[p] == '\n') {
                 if (++p == end || bytes[p] != '\n') continue;
                 while(p < end && bytes[p] == '\n') p++;
             }
             if (ptr < p && bytes[p - 1] == newline &&
                (rslen <= 1 || 
                 ByteList.memcmp(sepValue.getUnsafeBytes(), sepValue.getBegin(), rslen, bytes, p - rslen, rslen) == 0)) {
                 block.yield(context, makeShared(runtime, s - ptr, p - s).infectBy(this));
                 modifyCheck(bytes, len);
                 s = p;
             }
         }
 
         if (s != end) {
             if (p > end) p = end;
             block.yield(context, makeShared(runtime, s - ptr, p - s).infectBy(this));
         }
 
         return this;
     }
 
     @JRubyMethod(name = "each", compat = RUBY1_8)
     public IRubyObject each18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "each");
     }
 
     @JRubyMethod(name = "each", compat = RUBY1_8)
     public IRubyObject each18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each", arg);
     }
 
     @JRubyMethod(name = "each_line", compat = RUBY1_8)
     public IRubyObject each_line18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     @JRubyMethod(name = "each_line", compat = RUBY1_8)
     public IRubyObject each_line18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each_line", arg);
     }
 
     @JRubyMethod(name = "lines", compat = RUBY1_8)
     public IRubyObject lines18(ThreadContext context, Block block) {
         return block.isGiven() ? each_line(context, block) : 
             enumeratorize(context.getRuntime(), this, "lines");
     }
 
     @JRubyMethod(name = "lines", compat = RUBY1_8)
     public IRubyObject lines18(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "lines", arg);
     }
 
     @JRubyMethod(name = "each_line", compat = RUBY1_9)
     public IRubyObject each_line19(ThreadContext context, Block block) {
         return block.isGiven() ? each_lineCommon19(context, block) : 
             enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     @JRubyMethod(name = "each_line", compat = RUBY1_9)
     public IRubyObject each_line19(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon19(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "each_line", arg);
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject lines(ThreadContext context, Block block) {
         return block.isGiven() ? each_lineCommon19(context, block) : 
             enumeratorize(context.getRuntime(), this, "lines");
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject lines(ThreadContext context, IRubyObject arg, Block block) {
         return block.isGiven() ? each_lineCommon19(context, arg, block) : 
             enumeratorize(context.getRuntime(), this, "lines", arg);
     }
 
     private IRubyObject each_lineCommon19(ThreadContext context, Block block) {
         return each_lineCommon19(context, context.getRuntime().getGlobalVariables().get("$/"), block);
     }
 
     private IRubyObject each_lineCommon19(ThreadContext context, IRubyObject sep, Block block) {        
         Ruby runtime = context.getRuntime();
         if (sep.isNil()) {
             block.yield(context, this);
             return this;
         }
         if (! sep.respondsTo("to_str")) {
             throw runtime.newTypeError("can't convert " + sep.getMetaClass() + " into String");
         }
 
         ByteList val = value.shallowDup();
         int p = val.getBegin();
         int s = p;
         int len = val.getRealSize();
         int end = p + len;
         byte[]bytes = val.getUnsafeBytes();
 
         final Encoding enc;
         RubyString sepStr = sep.convertToString();
         if (sepStr == runtime.getGlobalVariables().getDefaultSeparator()) {
             enc = val.getEncoding();
             while (p < end) {
                 if (bytes[p] == (byte)'\n') {
                     int p0 = enc.leftAdjustCharHead(bytes, s, p, end);
                     if (enc.isNewLine(bytes, p0, end)) {
                         p = p0 + StringSupport.length(enc, bytes, p0, end);
                         block.yield(context, makeShared19(runtime, val, s, p - s).infectBy(this));
                         s = p++;
                     }
                 }
                 p++;
             }
         } else {
             enc = checkEncoding(sepStr);
             ByteList sepValue = sepStr.value;
             final int newLine;
             int rslen = sepValue.getRealSize();
             if (rslen == 0) {
                 newLine = '\n';
             } else {
                 newLine = codePoint(runtime, enc, sepValue.getUnsafeBytes(), sepValue.getBegin(), sepValue.getBegin() + sepValue.getRealSize());
             }
 
             while (p < end) {
                 int c = codePoint(runtime, enc, bytes, p, end);
                 again: do {
                     int n = codeLength(runtime, enc, c);
                     if (rslen == 0 && c == newLine) {
                         p += n;
                         if (p < end && (c = codePoint(runtime, enc, bytes, p, end)) != newLine) continue again;
                         while (p < end && codePoint(runtime, enc, bytes, p, end) == newLine) p += n;
                         p -= n;
                     }
                     if (c == newLine && (rslen <= 1 ||
                             ByteList.memcmp(sepValue.getUnsafeBytes(), sepValue.getBegin(), rslen, bytes, p, rslen) == 0)) {
                         block.yield(context, makeShared19(runtime, val, s, p - s + (rslen != 0 ? rslen : n)).infectBy(this));
                         s = p + (rslen != 0 ? rslen : n);
                     }
                     p += n;
                 } while (false);
             }
         }
 
         if (s != end) {
             block.yield(context, makeShared19(runtime, val, s, end - s).infectBy(this));
         }
         return this;
     }
 
     /**
      * rb_str_each_byte
      */
     public RubyString each_byte(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         // Check the length every iteration, since
         // the block can modify this string.
         for (int i = 0; i < value.length(); i++) {
             block.yield(context, runtime.newFixnum(value.get(i) & 0xFF));
         }
         return this;
     }
 
     @JRubyMethod(name = "each_byte")
     public IRubyObject each_byte19(ThreadContext context, Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod
     public IRubyObject bytes(ThreadContext context, Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "bytes");
     }
 
     /** rb_str_each_char
      * 
      */
     @JRubyMethod(name = "each_char", compat = RUBY1_8)
     public IRubyObject each_char18(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon18(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod(name = "chars", compat = RUBY1_8)
     public IRubyObject chars18(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon18(context, block) : enumeratorize(context.getRuntime(), this, "chars");
     }
 
     private IRubyObject each_charCommon18(ThreadContext context, Block block) {
         byte bytes[] = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
 
         Ruby runtime = context.getRuntime();
         Encoding enc = runtime.getKCode().getEncoding();
         ByteList val = value.shallowDup();
         while (p < end) {
             int n = StringSupport.length(enc, bytes, p, end);
             block.yield(context, makeShared19(runtime, val, p-val.getBegin(), n));
             p += n;
         }
         return this;
     }
 
     @JRubyMethod(name = "each_char", compat = RUBY1_9)
     public IRubyObject each_char19(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon19(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod(name = "chars", compat = RUBY1_9)
     public IRubyObject chars19(ThreadContext context, Block block) {
         return block.isGiven() ? each_charCommon19(context, block) : enumeratorize(context.getRuntime(), this, "chars");
     }
 
     private IRubyObject each_charCommon19(ThreadContext context, Block block) {
         byte bytes[] = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
         Encoding enc = value.getEncoding();
 
         Ruby runtime = context.getRuntime();
         ByteList val = value.shallowDup();
         while (p < end) {
             int n = StringSupport.length(enc, bytes, p, end);
             block.yield(context, makeShared19(runtime, val, p-value.getBegin(), n));
             p += n;
         }
         return this;
     }
 
     /** rb_str_each_codepoint
      * 
      */
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject each_codepoint(ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "each_codepoint");
         return singleByteOptimizable() ? each_byte(context, block) : each_codepointCommon(context, block);
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject codepoints(ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "codepoints");
         return singleByteOptimizable() ? each_byte(context, block) : each_codepointCommon(context, block);
     }
 
     private IRubyObject each_codepointCommon(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         byte bytes[] = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
         Encoding enc = value.getEncoding();
 
         while (p < end) {
             int c = codePoint(runtime, enc, bytes, p, end);
             int n = codeLength(runtime, enc, c);
             block.yield(context, runtime.newFixnum(c));
             p += n;
         }
         return this;
     }
 
     /** rb_str_intern
      *
      */
     private RubySymbol to_sym() {
         RubySymbol symbol = getRuntime().getSymbolTable().getSymbol(value);
         if (symbol.getBytes() == value) shareLevel = SHARE_LEVEL_BYTELIST;
         return symbol;
     }
 
     @JRubyMethod(name = {"to_sym", "intern"}, compat = RUBY1_8)
     public RubySymbol intern() {
         if (value.getRealSize() == 0) throw getRuntime().newArgumentError("interning empty string");
         for (int i = 0; i < value.getRealSize(); i++) {
             if (value.getUnsafeBytes()[value.getBegin() + i] == 0) throw getRuntime().newArgumentError("symbol string may not contain '\\0'");
         }
         return to_sym();
     }
 
     @JRubyMethod(name = {"to_sym", "intern"}, compat = RUBY1_9)
     public RubySymbol intern19() {
         return to_sym();
     }
 
     @JRubyMethod(name = "ord", compat = RUBY1_9)
     public IRubyObject ord(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return RubyFixnum.newFixnum(runtime, codePoint(runtime, value.getEncoding(), value.getUnsafeBytes(), value.getBegin(),
                                                                 value.getBegin() + value.getRealSize()));
     }
 
     @JRubyMethod(name = "sum")
     public IRubyObject sum(ThreadContext context) {
         return sumCommon(context, 16);
     }
 
     @JRubyMethod(name = "sum")
     public IRubyObject sum(ThreadContext context, IRubyObject arg) {
         return sumCommon(context, RubyNumeric.num2long(arg));
     }
 
     public IRubyObject sumCommon(ThreadContext context, long bits) {
         Ruby runtime = context.getRuntime();
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         int end = p + len; 
 
         if (bits >= 8 * 8) { // long size * bits in byte
             IRubyObject one = RubyFixnum.one(runtime);
             IRubyObject sum = RubyFixnum.zero(runtime);
             while (p < end) {
                 modifyCheck(bytes, len);
                 sum = sum.callMethod(context, "+", RubyFixnum.newFixnum(runtime, bytes[p++] & 0xff));
             }
             if (bits != 0) {
                 IRubyObject mod = one.callMethod(context, "<<", RubyFixnum.newFixnum(runtime, bits));
                 sum = sum.callMethod(context, "&", mod.callMethod(context, "-", one));
             }
             return sum;
         } else {
             long sum = 0;
             while (p < end) {
                 modifyCheck(bytes, len);
                 sum += bytes[p++] & 0xff;
             }
             return RubyFixnum.newFixnum(runtime, bits == 0 ? sum : sum & (1L << bits) - 1L);
         }
     }
 
     /** string_to_c
      * 
      */
     @JRubyMethod(name = "to_c", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject to_c(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         DynamicScope scope = context.getCurrentScope();
         IRubyObject backref = scope.getBackRef(runtime);
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
 
         IRubyObject s = RuntimeHelpers.invoke(
                 context, this, "gsub",
                 RubyRegexp.newDummyRegexp(runtime, Numeric.ComplexPatterns.underscores_pat),
                 runtime.newString(new ByteList(new byte[]{'_'})));
 
         RubyArray a = RubyComplex.str_to_c_internal(context, s);
 
         scope.setBackRef(backref);
 
         if (!a.eltInternal(0).isNil()) {
             return a.eltInternal(0);
         } else {
             return RubyComplex.newComplexCanonicalize(context, RubyFixnum.zero(runtime));
         }
     }
 
     /** string_to_r
      * 
      */
     @JRubyMethod(name = "to_r", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject to_r(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         DynamicScope scope = context.getCurrentScope();
         IRubyObject backref = scope.getBackRef(runtime);
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
 
         IRubyObject s = RuntimeHelpers.invoke(
                 context, this, "gsub",
                 RubyRegexp.newDummyRegexp(runtime, Numeric.ComplexPatterns.underscores_pat),
                 runtime.newString(new ByteList(new byte[]{'_'})));
 
         RubyArray a = RubyRational.str_to_r_internal(context, s);
 
         scope.setBackRef(backref);
 
         if (!a.eltInternal(0).isNil()) {
             return a.eltInternal(0);
         } else {
             return RubyRational.newRationalCanonicalize(context, RubyFixnum.zero(runtime));
         }
     }    
 
     public static RubyString unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyString result = newString(input.getRuntime(), input.unmarshalString());
         input.registerLinkTarget(result);
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#unpack
      */
     @JRubyMethod(name = "unpack")
     public RubyArray unpack(IRubyObject obj) {
         return Pack.unpack(getRuntime(), this.value, stringValue(obj).value);
     }
 
     public void empty() {
         value = ByteList.EMPTY_BYTELIST;
         shareLevel = SHARE_LEVEL_BYTELIST;
     }
 
     @JRubyMethod(name = "encoding", compat = RUBY1_9)
     public IRubyObject encoding(ThreadContext context) {
         return context.getRuntime().getEncodingService().getEncoding(value.getEncoding());
     }
 
     @JRubyMethod(name = "encode!", compat = RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context) {
         modify19();
-        IRubyObject defaultInternal = RubyEncoding.getDefaultInternal(context.getRuntime());
+        IRubyObject defaultInternal = context.getRuntime().getEncodingService().getDefaultInternal();
         if (!defaultInternal.isNil()) {
             encode_bang(context, defaultInternal);
         }
         return this;
     }
 
     @JRubyMethod(name = "encode!", compat = RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context, IRubyObject enc) {
         modify19();
 
         Ruby runtime = context.getRuntime();
         this.value = encodeCommon(context, runtime, this.value, enc, runtime.getNil(),
             runtime.getNil());
 
         return this;
     }
 
     @JRubyMethod(name = "encode!", compat = RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context, IRubyObject enc, IRubyObject arg) {
         modify19();
 
         Ruby runtime = context.getRuntime();
         IRubyObject fromEnc = arg;
         IRubyObject opts = runtime.getNil();
         if (arg instanceof RubyHash) {
             fromEnc = runtime.getNil();
             opts = arg;
         }
         this.value = encodeCommon(context, runtime, this.value, enc, fromEnc, opts);
 
         return this;
     }
 
     @JRubyMethod(name = "encode!", compat = RUBY1_9)
     public IRubyObject encode_bang(ThreadContext context, IRubyObject enc, IRubyObject fromEnc, IRubyObject opts) {
         modify19();
         this.value = encodeCommon(context, context.getRuntime(), this.value, enc, fromEnc, opts);
         return this;
     }
 
     @JRubyMethod(name = "encode", compat = RUBY1_9)
     public IRubyObject encode(ThreadContext context) {
         Ruby runtime = context.getRuntime();
-        IRubyObject defaultInternal = RubyEncoding.getDefaultInternal(runtime);
+        IRubyObject defaultInternal = runtime.getEncodingService().getDefaultInternal();
 
         if (!defaultInternal.isNil()) {
             ByteList encoded = encodeCommon(context, runtime, value, defaultInternal,
                                             runtime.getNil(), runtime.getNil());
             return runtime.newString(encoded);
         } else {
             return dup();
         }
     }
 
     @JRubyMethod(name = "encode", compat = RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc) {
         Ruby runtime = context.getRuntime();
 
         ByteList encoded = encodeCommon(context, runtime, value, enc, runtime.getNil(),
             runtime.getNil());
         return runtime.newString(encoded);
     }
 
     @JRubyMethod(name = "encode", compat = RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
 
         IRubyObject fromEnc = arg;
         IRubyObject opts = runtime.getNil();
         if (arg instanceof RubyHash) {
             fromEnc = runtime.getNil();
             opts = arg;
         }
         ByteList encoded = encodeCommon(context, runtime, value, enc, fromEnc, opts);
         return runtime.newString(encoded);
     }
 
     @JRubyMethod(name = "encode", compat = RUBY1_9)
     public IRubyObject encode(ThreadContext context, IRubyObject enc, IRubyObject fromEnc, IRubyObject opts) {
         Ruby runtime = context.getRuntime();
 
         ByteList encoded = encodeCommon(context, runtime, value, enc, fromEnc, opts);
         return runtime.newString(encoded);
     }
 
     private static ByteList encodeCommon(ThreadContext context, Ruby runtime, ByteList value,
             IRubyObject toEnc, IRubyObject fromEnc, IRubyObject opts) {
         Charset from = fromEnc.isNil() ? getCharset(runtime, value.getEncoding()) : getCharset(runtime, fromEnc);
 
         Encoding encoding = getEncoding(runtime, toEnc);
         Charset to = getCharset(runtime, encoding);
 
         CharsetEncoder encoder = getEncoder(context, runtime, to, opts);
 
         // decode from "from" and encode to "to"
         ByteBuffer fromBytes = ByteBuffer.wrap(value.getUnsafeBytes(), value.begin(), value.length());
         ByteBuffer toBytes;
         try {
             toBytes = encoder.encode(from.decode(fromBytes));
         } catch (CharacterCodingException e) {
             throw runtime.newInvalidByteSequenceError("");
         }
 
         // CharsetEncoder#encode guarantees a newly-allocated buffer, so
         // it's safe for us to take ownership of it without copying
         ByteList result = new ByteList(toBytes.array(), toBytes.arrayOffset(),
                 toBytes.limit() - toBytes.arrayOffset(), false);
         result.setEncoding(encoding);
         return result;
     }
 
     private static CharsetEncoder getEncoder(ThreadContext context, Ruby runtime, Charset charset, IRubyObject opts) {
         CharsetEncoder encoder = charset.newEncoder();
 
         if (!opts.isNil()) {
             RubyHash hash = (RubyHash) opts;
             CodingErrorAction action = CodingErrorAction.REPLACE;
             
             IRubyObject replace = hash.fastARef(runtime.newSymbol("replace"));
             if (replace != null && !replace.isNil()) {
                 String replaceWith = replace.toString();
                 if (replaceWith.length() > 0) {
                     encoder.replaceWith(replaceWith.getBytes());
                 } else {
                     action = CodingErrorAction.IGNORE;
                 }
             }
             
             IRubyObject invalid = hash.fastARef(runtime.newSymbol("invalid"));
             if (invalid != null && invalid.op_equal(context, runtime.newSymbol("replace")).isTrue()) {
                 encoder.onMalformedInput(action);
             }
 
             IRubyObject undef = hash.fastARef(runtime.newSymbol("undef"));
             if (undef != null && undef.op_equal(context, runtime.newSymbol("replace")).isTrue()) {
                 encoder.onUnmappableCharacter(action);
             }
 
 //            FIXME: Parse the option :xml
 //            The value must be +:text+ or +:attr+. If the
 //            value is +:text+ +#encode+ replaces undefined
 //            characters with their (upper-case hexadecimal)
 //            numeric character references. '&', '<', and
 //            '>' are converted to "&amp;", "&lt;", and
 //            "&gt;", respectively. If the value is +:attr+,
 //            +#encode+ also quotes the replacement result
 //            (using '"'), and replaces '"' with "&quot;".
         }
 
         return encoder;
     }
 
     private static Encoding getEncoding(Ruby runtime, IRubyObject toEnc) {
         try {
-            return RubyEncoding.getEncodingFromObject(runtime, toEnc);
+            return runtime.getEncodingService().getEncodingFromObject(toEnc);
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + toEnc.toString() + ")");
         }
     }
 
     private static Charset getCharset(Ruby runtime, IRubyObject toEnc) {
         try {
-            Encoding encoding = RubyEncoding.getEncodingFromObject(runtime, toEnc);
+            Encoding encoding = runtime.getEncodingService().getEncodingFromObject(toEnc);
 
             return getCharset(runtime, encoding);
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + toEnc.toString() + ")");
         }
     }
 
     private static Charset getCharset(Ruby runtime, Encoding encoding) {
         try {
             // special-casing ASCII* to ASCII
             return encoding.toString().startsWith("ASCII") ?
                 Charset.forName("ASCII") :
                 Charset.forName(encoding.toString());
         } catch (Exception e) {
             throw runtime.newConverterNotFoundError("code converter not found (" + encoding.toString() + ")");
         }
     }
 
     @JRubyMethod(name = "force_encoding", compat = RUBY1_9)
     public IRubyObject force_encoding(ThreadContext context, IRubyObject enc) {
         modify19();
-        Encoding encoding = RubyEncoding.getEncodingFromObject(context.getRuntime(), enc);
+        Encoding encoding = context.runtime.getEncodingService().getEncodingFromObject(enc);
         associateEncoding(encoding);
         return this;
     }
 
     @JRubyMethod(name = "valid_encoding?", compat = RUBY1_9)
     public IRubyObject valid_encoding_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return scanForCodeRange() == CR_BROKEN ? runtime.getFalse() : runtime.getTrue();
     }
 
     @JRubyMethod(name = "ascii_only?", compat = RUBY1_9)
     public IRubyObject ascii_only_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         return scanForCodeRange() == CR_7BIT ? runtime.getTrue() : runtime.getFalse();
     }
 
     /**
      * Mutator for internal string representation.
      *
      * @param value The new java.lang.String this RubyString should encapsulate
      * @deprecated
      */
     public void setValue(CharSequence value) {
         view(ByteList.plain(value));
     }
 
     public void setValue(ByteList value) {
         view(value);
     }
 
     public CharSequence getValue() {
         return toString();
     }
 
     public byte[] getBytes() {
         return value.bytes();
     }
 
     public ByteList getByteList() {
         return value;
     }
 
     /** used by ar-jdbc
      * 
      */
     public String getUnicodeValue() {
         return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.getBegin(), value.getRealSize());
     }
 
     @Override
     public Object toJava(Class target) {
         if (target.isAssignableFrom(String.class)) {
             try {
                 // 1.9 support for encodings
                 // TODO: Fix charset use for JRUBY-4553
                 if (getRuntime().is1_9()) {
                     return new String(value.getUnsafeBytes(), value.begin(), value.length(), getEncoding().toString());
                 }
 
                 return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.begin(), value.length());
             } catch (UnsupportedEncodingException uee) {
                 return toString();
             }
         } else if (target.isAssignableFrom(ByteList.class)) {
             return value;
         } else {
             return super.toJava(target);
         }
     }
 
     /**
      * Variable-arity versions for compatibility. Not bound to Ruby.
      * @deprecated Use the versions with zero or one arguments
      */
 
     @Deprecated
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         switch (args.length) {
         case 0: return this;
         case 1: return initialize(args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject sub(ThreadContext context, IRubyObject[] args, Block block) {
         RubyString str = strDup(context.getRuntime());
         str.sub_bang(context, args, block);
         return str;
     }
 
     @Deprecated
     public IRubyObject sub_bang(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1: return sub_bang(context, args[0], block);
         case 2: return sub_bang(context, args[0], args[1], block);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject gsub(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1: return gsub(context, args[0], block);
         case 2: return gsub(context, args[0], args[1], block);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject gsub_bang(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1: return gsub_bang(context, args[0], block);
         case 2: return gsub_bang(context, args[0], args[1], block);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject index(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return index(context, args[0]);
         case 2: return index(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject rindex(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return rindex(context, args[0]);
         case 2: return rindex(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject op_aref(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return op_aref(context, args[0]);
         case 2: return op_aref(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject op_aset(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 2: return op_aset(context, args[0], args[1]);
         case 3: return op_aset(context, args[0], args[1], args[2]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 2, 3); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject slice_bang(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 1: return slice_bang(context, args[0]);
         case 2: return slice_bang(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject to_i(IRubyObject[] args) {
         switch (args.length) {
         case 0: return to_i();
         case 1: return to_i(args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 
     @Deprecated
     public RubyArray split(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
         case 0: return split(context);
         case 1: return split(context, args[0]);
         case 2: return split(context, args[0], args[1]);
         default:Arity.raiseArgumentError(context.getRuntime(), args.length, 0, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject ljust(IRubyObject [] args) {
         switch (args.length) {
         case 1: return ljust(args[0]);
         case 2: return ljust(args[0], args[1]);
         default: Arity.raiseArgumentError(getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject rjust(IRubyObject [] args) {
         switch (args.length) {
         case 1: return rjust(args[0]);
         case 2: return rjust(args[0], args[1]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject center(IRubyObject [] args) {
         switch (args.length) {
         case 1: return center(args[0]);
         case 2: return center(args[0], args[1]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 1, 2); return null; // not reached
         }
     }
 
     @Deprecated
     public RubyString chomp(IRubyObject[] args) {
         switch (args.length) {
         case 0:return chomp(getRuntime().getCurrentContext());
         case 1:return chomp(getRuntime().getCurrentContext(), args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 
     @Deprecated
     public IRubyObject chomp_bang(IRubyObject[] args) {
         switch (args.length) {
         case 0: return chomp_bang(getRuntime().getCurrentContext());
         case 1: return chomp_bang(getRuntime().getCurrentContext(), args[0]);
         default:Arity.raiseArgumentError(getRuntime(), args.length, 0, 1); return null; // not reached
         }
     }
 }
diff --git a/src/org/jruby/runtime/encoding/EncodingService.java b/src/org/jruby/runtime/encoding/EncodingService.java
index 1e35d8904e..9497bfa266 100644
--- a/src/org/jruby/runtime/encoding/EncodingService.java
+++ b/src/org/jruby/runtime/encoding/EncodingService.java
@@ -1,173 +1,340 @@
 package org.jruby.runtime.encoding;
 
 import java.nio.charset.Charset;
 import org.jcodings.Encoding;
 import org.jcodings.EncodingDB;
 import org.jcodings.EncodingDB.Entry;
 import org.jcodings.ascii.AsciiTables;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.util.CaseInsensitiveBytesHash;
 import org.jcodings.util.Hash.HashEntryIterator;
 import org.jruby.Ruby;
 import org.jruby.RubyEncoding;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 public final class EncodingService {
     private final CaseInsensitiveBytesHash<Entry> encodings;
     private final CaseInsensitiveBytesHash<Entry> aliases;
 
     // for fast lookup: encoding entry => org.jruby.RubyEncoding
     private final IRubyObject[] encodingList;
     // for fast lookup: org.joni.encoding.Encoding => org.jruby.RubyEncoding
     private RubyEncoding[] encodingIndex = new RubyEncoding[4];
+    // the runtime
+    private Ruby runtime;
 
     public EncodingService (Ruby runtime) {
-        // TODO: make it cross runtime safe by COW or eager copy
+        this.runtime = runtime;
         encodings = EncodingDB.getEncodings();
         aliases = EncodingDB.getAliases();
 
         encodingList = new IRubyObject[encodings.size()];
-        defineEncodings(runtime);
-        defineAliases(runtime);
+        defineEncodings();
+        defineAliases();
     }
 
     public CaseInsensitiveBytesHash<Entry> getEncodings() {
         return encodings;
     }
 
     public CaseInsensitiveBytesHash<Entry> getAliases() {
         return aliases;
     }
 
     public Entry findEncodingEntry(ByteList bytes) {
         return encodings.get(bytes.getUnsafeBytes(), bytes.getBegin(), bytes.getBegin() + bytes.getRealSize());
     }
 
     public Entry findAliasEntry(ByteList bytes) {
         return aliases.get(bytes.getUnsafeBytes(), bytes.getBegin(), bytes.getBegin() + bytes.getRealSize());
     }
 
     public Entry findEncodingOrAliasEntry(ByteList bytes) {
         Entry e = findEncodingEntry(bytes);
         return e != null ? e : findAliasEntry(bytes);
     }
 
     // rb_locale_charmap...mostly
     public Encoding getLocaleEncoding() {
         Entry entry = findEncodingOrAliasEntry(new ByteList(Charset.defaultCharset().name().getBytes()));
         return entry == null ? ASCIIEncoding.INSTANCE : entry.getEncoding();
     }
 
     public IRubyObject[] getEncodingList() {
         return encodingList;
     }
 
     public Encoding loadEncoding(ByteList name) {
         Entry entry = findEncodingOrAliasEntry(name);
         if (entry == null) return null;
         Encoding enc = entry.getEncoding(); // load the encoding
         int index = enc.getIndex();
         if (index >= encodingIndex.length) {
             RubyEncoding tmp[] = new RubyEncoding[index + 4];
             System.arraycopy(encodingIndex, 0, tmp, 0, encodingIndex.length);
             encodingIndex = tmp;
         }
         encodingIndex[index] = (RubyEncoding)encodingList[entry.getIndex()];
         return enc;
     }
 
     public RubyEncoding getEncoding(Encoding enc) {
         int index = enc.getIndex();
         RubyEncoding rubyEncoding;
         if (index < encodingIndex.length && (rubyEncoding = encodingIndex[index]) != null) {
             return rubyEncoding;
         }
         enc = loadEncoding(new ByteList(enc.getName(), false));
         return encodingIndex[enc.getIndex()];
     }
 
-    private void defineEncodings(Ruby runtime) {
+    private void defineEncodings() {
         HashEntryIterator hei = encodings.entryIterator();
         while (hei.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)hei.next());
             Entry ee = e.value;
             RubyEncoding encoding = RubyEncoding.newEncoding(runtime, e.bytes, e.p, e.end, ee.isDummy());
             encodingList[ee.getIndex()] = encoding;
             defineEncodingConstants(runtime, encoding, e.bytes, e.p, e.end);
         }
     }
 
-    private void defineAliases(Ruby runtime) {
+    private void defineAliases() {
         HashEntryIterator hei = aliases.entryIterator();
         while (hei.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)hei.next());
             Entry ee = e.value; 
             RubyEncoding encoding = (RubyEncoding)encodingList[ee.getIndex()];  
             defineEncodingConstants(runtime, encoding, e.bytes, e.p, e.end);
         }
     }
 
     private void defineEncodingConstants(Ruby runtime, RubyEncoding encoding, byte[]name, int p,
             int end) {
         Encoding enc = ASCIIEncoding.INSTANCE;
         int s = p;
 
         int code = name[s] & 0xff;
         if (enc.isDigit(code)) return;
 
         boolean hasUpper = false;
         boolean hasLower = false;
         if (enc.isUpper(code)) {
             hasUpper = true;
             while (++s < end && (enc.isAlnum(name[s] & 0xff) || name[s] == (byte)'_')) {
                 if (enc.isLower(name[s] & 0xff)) hasLower = true;
             }
         }
 
         boolean isValid = false;
         if (s >= end) {
             isValid = true;
             defineEncodingConstant(runtime, encoding, name, p, end);
         }
 
         if (!isValid || hasLower) {
             if (!hasLower || !hasUpper) {
                 do {
                     code = name[s] & 0xff;
                     if (enc.isLower(code)) hasLower = true;
                     if (enc.isUpper(code)) hasUpper = true;
                 } while (++s < end && (!hasLower || !hasUpper));
             }
 
             byte[]constName = new byte[end - p];
             System.arraycopy(name, p, constName, 0, end - p);
             s = 0;
             code = constName[s] & 0xff;
 
             if (!isValid) {
                 if (enc.isLower(code)) constName[s] = AsciiTables.ToUpperCaseTable[code];
                 for (; s < constName.length; ++s) {
                     if (!enc.isAlnum(constName[s] & 0xff)) constName[s] = (byte)'_';
                 }
                 if (hasUpper) {
                     defineEncodingConstant(runtime, encoding, constName, 0, constName.length);
                 }
             }
             if (hasLower) {
                 for (s = 0; s < constName.length; ++s) {
                     code = constName[s] & 0xff;
                     if (enc.isLower(code)) constName[s] = AsciiTables.ToUpperCaseTable[code];
                 }
                 defineEncodingConstant(runtime, encoding, constName, 0, constName.length);
             }
         }
     }
 
     private void defineEncodingConstant(Ruby runtime, RubyEncoding encoding, byte[]constName,
             int constP, int constEnd) {
         runtime.getEncoding().defineConstant(new String(constName, constP , constEnd), encoding);
     }
+
+    public IRubyObject getDefaultExternal() {
+        IRubyObject defaultExternal = convertEncodingToRubyEncoding(runtime.getDefaultExternalEncoding());
+
+        if (defaultExternal.isNil()) {
+            // TODO: MRI seems to default blindly to US-ASCII and we were using Charset default from Java...which is right?
+            ByteList encodingName = ByteList.create("US-ASCII");
+            Encoding encoding = runtime.getEncodingService().loadEncoding(encodingName);
+
+            runtime.setDefaultExternalEncoding(encoding);
+            defaultExternal = convertEncodingToRubyEncoding(encoding);
+        }
+
+        return defaultExternal;
+    }
+
+    public IRubyObject getDefaultInternal() {
+        return convertEncodingToRubyEncoding(runtime.getDefaultInternalEncoding());
+    }
+
+    public IRubyObject convertEncodingToRubyEncoding(Encoding defaultEncoding) {
+        return defaultEncoding != null ? getEncoding(defaultEncoding) : runtime.getNil();
+    }
+
+    public Encoding getEncodingFromObject(IRubyObject arg) {
+        if (arg == null) return null;
+
+        Encoding encoding = null;
+        if (arg instanceof RubyEncoding) {
+            encoding = ((RubyEncoding) arg).getEncoding();
+        } else if (!arg.isNil()) {
+            encoding = arg.convertToString().toEncoding(runtime);
+        }
+        return encoding;
+    }
+
+    /**
+     * Find an encoding given a Ruby object, coercing it to a String in the process.
+     *
+     * @param runtime current Ruby instance
+     * @param str the object to coerce and use to look up encoding. The coerced String
+     * must be ASCII-compatible.
+     * @return the Encoding object found, nil (for internal), or raises ArgumentError
+     */
+    public Encoding findEncoding(IRubyObject str) {
+        ByteList name = str.convertToString().getByteList();
+        checkAsciiEncodingName(name);
+
+        SpecialEncoding special = SpecialEncoding.valueOf(name);
+        if (special != null) {
+            return special.toEncoding(runtime);
+        }
+
+        return findEncodingWithError(name);
+    }
+
+    /**
+     * Find an encoding given a Ruby object, coercing it to a String in the process.
+     *
+     * @param runtime current Ruby instance
+     * @param str the object to coerce and use to look up encoding. The coerced String
+     * must be ASCII-compatible.
+     * @return the Encoding object found, nil (for internal), or raises ArgumentError
+     */
+    public Entry findEntry(IRubyObject str) {
+        ByteList name = str.convertToString().getByteList();
+        checkAsciiEncodingName(name);
+
+        SpecialEncoding special = SpecialEncoding.valueOf(name);
+        if (special != null) {
+            return findEntryFromEncoding(special.toEncoding(runtime));
+        }
+
+        return findEntryWithError(name);
+    }
+
+    /**
+     * Look up the pre-existing RubyEncoding object for an EncodingDB.Entry.
+     *
+     * @param runtime
+     * @param entry
+     * @return
+     */
+    public IRubyObject rubyEncodingFromObject(IRubyObject str) {
+        Entry entry = findEntry(str);
+        if (entry == null) return runtime.getNil();
+        return getEncodingList()[entry.getIndex()];
+    }
+
+    private void checkAsciiEncodingName(ByteList name) {
+        if (!name.getEncoding().isAsciiCompatible()) {
+            throw runtime.newArgumentError("invalid name encoding (non ASCII)");
+        }
+    }
+
+    private static final ByteList LOCALE_BL = ByteList.create("locale");
+    private static final ByteList EXTERNAL_BL = ByteList.create("external");
+    private static final ByteList INTERNAL_BL = ByteList.create("internal");
+    private static final ByteList FILESYSTEM_BL = ByteList.create("filesystem");
+
+    /**
+     * Represents one of the four "special" internal encoding names: internal,
+     * external, locale, or filesystem.
+     */
+    private enum SpecialEncoding {
+        LOCALE, EXTERNAL, INTERNAL, FILESYSTEM;
+        public static SpecialEncoding valueOf(ByteList name) {
+            if (name.caseInsensitiveCmp(LOCALE_BL) == 0) {
+                return LOCALE;
+            } else if (name.caseInsensitiveCmp(EXTERNAL_BL) == 0) {
+                return EXTERNAL;
+            } else if (name.caseInsensitiveCmp(INTERNAL_BL) == 0) {
+                return INTERNAL;
+            } else if (name.caseInsensitiveCmp(FILESYSTEM_BL) == 0) {
+                return FILESYSTEM;
+            }
+            return null;
+        }
+
+        public Encoding toEncoding(Ruby runtime) {
+            EncodingService service = runtime.getEncodingService();
+            switch (this) {
+            case LOCALE: return service.getLocaleEncoding();
+            case EXTERNAL: return runtime.getDefaultExternalEncoding();
+            case INTERNAL: return runtime.getDefaultInternalEncoding();
+            case FILESYSTEM:
+                // This needs to do something different on Windows. See encoding.c,
+                // in the enc_set_filesystem_encoding function.
+                return runtime.getDefaultExternalEncoding();
+            default:
+                throw new RuntimeException("invalid SpecialEncoding: " + this);
+            }
+        }
+    }
+
+    /**
+     * Find a non-special encoding, raising argument error if it does not exist.
+     *
+     * @param runtime current Ruby instance
+     * @param name the name of the encoding to look up
+     * @return the Encoding object found, or raises ArgumentError
+     */
+    private Encoding findEncodingWithError(ByteList name) {
+        return findEntryWithError(name).getEncoding();
+    }
+
+    /**
+     * Find a non-special encoding Entry, raising argument error if it does not exist.
+     *
+     * @param runtime current Ruby instance
+     * @param name the name of the encoding to look up
+     * @return the EncodingDB.Entry object found, or raises ArgumentError
+     */
+    private Entry findEntryWithError(ByteList name) {
+        Entry e = findEncodingOrAliasEntry(name);
+
+        if (e == null) throw runtime.newArgumentError("unknown encoding name - " + name);
+
+        return e;
+    }
+
+    private Entry findEntryFromEncoding(Encoding e) {
+        if (e == null) return null;
+        return findEncodingEntry(new ByteList(e.getName()));
+    }
 }
\ No newline at end of file
diff --git a/src/org/jruby/util/Pack.java b/src/org/jruby/util/Pack.java
index abe90843ff..f96f70ae7f 100644
--- a/src/org/jruby/util/Pack.java
+++ b/src/org/jruby/util/Pack.java
@@ -1190,1275 +1190,1275 @@ public class Pack {
                     break;
                  case 'X':
                      if (occurrences == IS_STAR) {
                          // MRI behavior: Contrary to what seems to be logical,
                          // when '*' is given, MRI calculates the distance
                          // to the end, in order to go backwards.
                          occurrences = /*encode.limit() - */encode.remaining();
                      }
 
                      try {
                          encode.position(encode.position() - occurrences);
                      } catch (IllegalArgumentException e) {
                          throw runtime.newArgumentError("in `unpack': X outside of string");
                      }
                      break;
                  case 'x':
                       if (occurrences == IS_STAR) {
                            occurrences = encode.remaining();
                       }
 
                       try {
                           encode.position(encode.position() + occurrences);
                       } catch (IllegalArgumentException e) {
                           throw runtime.newArgumentError("in `unpack': x outside of string");
                       }
 
                      break;
                 case 'w':
                     if (occurrences == IS_STAR || occurrences > encode.remaining()) {
                         occurrences = encode.remaining();
                     }
 
                     long ul = 0;
                     long ulmask = (0xfe << 56) & 0xffffffff;
                     RubyBignum big128 = RubyBignum.newBignum(runtime, 128);
                     int pos = encode.position();
 
                     while (occurrences > 0 && pos < encode.limit()) {
                         ul <<= 7;
                         ul |= encode.get(pos) & 0x7f;
                         if((encode.get(pos++) & 0x80) == 0) {
                             result.append(RubyFixnum.newFixnum(runtime, ul));
                             occurrences--;
                             ul = 0;
                         } else if((ul & ulmask) == 0) {
                             RubyBignum big = RubyBignum.newBignum(runtime, ul);
                             while(occurrences > 0 && pos < encode.limit()) {
                                 big = (RubyBignum)big.op_mul(runtime.getCurrentContext(), big128);
                                 IRubyObject v = big.op_plus(runtime.getCurrentContext(),
                                         RubyBignum.newBignum(runtime, encode.get(pos) & 0x7f));
                                 if(v instanceof RubyFixnum) {
                                     big = RubyBignum.newBignum(runtime, RubyNumeric.fix2long(v));
                                 } else if (v instanceof RubyBignum) {
                                     big = (RubyBignum)v;
                                 }
                                 if((encode.get(pos++) & 0x80) == 0) {
                                     result.add(big);
                                     occurrences--;
                                     ul = 0;
                                     break;
                                 }
                             }
                         }
                     }
                     try {
                         encode.position(pos);
                     } catch (IllegalArgumentException e) {
                         throw runtime.newArgumentError("in `unpack': poorly encoded input");
                     }
                     break;
             }
         }
         return result;
     }
 
     /** rb_uv_to_utf8
      *
      */
     public static int utf8Decode(Ruby runtime, byte[]to, int p, int code) {
         if (code <= 0x7f) {
             to[p] = (byte)code;
             return 1;
         }
         if (code <= 0x7ff) {
             to[p + 0] = (byte)(((code >>> 6) & 0xff) | 0xc0);
             to[p + 1] = (byte)((code & 0x3f) | 0x80);
             return 2;
         }
         if (code <= 0xffff) {
             to[p + 0] = (byte)(((code >>> 12) & 0xff) | 0xe0);
             to[p + 1] = (byte)(((code >>> 6) & 0x3f) | 0x80);
             to[p + 2] = (byte)((code & 0x3f) | 0x80);
             return 3;
         }
         if (code <= 0x1fffff) {
             to[p + 0] = (byte)(((code >>> 18) & 0xff) | 0xf0);
             to[p + 1] = (byte)(((code >>> 12) & 0x3f) | 0x80);
             to[p + 2] = (byte)(((code >>> 6) & 0x3f) | 0x80);
             to[p + 3] = (byte)((code & 0x3f) | 0x80);
             return 4;
         }
         if (code <= 0x3ffffff) {
             to[p + 0] = (byte)(((code >>> 24) & 0xff) | 0xf8);
             to[p + 1] = (byte)(((code >>> 18) & 0x3f) | 0x80);
             to[p + 2] = (byte)(((code >>> 12) & 0x3f) | 0x80);
             to[p + 3] = (byte)(((code >>> 6) & 0x3f) | 0x80);
             to[p + 4] = (byte)((code & 0x3f) | 0x80);
             return 5;
         }
         if (code <= 0x7fffffff) {
             to[p + 0] = (byte)(((code >>> 30) & 0xff) | 0xfc);
             to[p + 1] = (byte)(((code >>> 24) & 0x3f) | 0x80);
             to[p + 2] = (byte)(((code >>> 18) & 0x3f) | 0x80);
             to[p + 3] = (byte)(((code >>> 12) & 0x3f) | 0x80);
             to[p + 4] = (byte)(((code >>> 6) & 0x3f) | 0x80);
             to[p + 5] = (byte)((code & 0x3f) | 0x80);
             return 6;
         }
         throw runtime.newRangeError("pack(U): value out of range");
     }
 
     /** utf8_to_uv
      */
     private static int utf8Decode(ByteBuffer buffer) {        
         int c = buffer.get() & 0xFF;
         int uv = c;
         int n;
 
         if ((c & 0x80) == 0) {
             return c;
         }
 
         if ((c & 0x40) == 0) {
             throw new IllegalArgumentException("malformed UTF-8 character");
         }
         
       if      ((uv & 0x20) == 0) { n = 2; uv &= 0x1f; }
       else if ((uv & 0x10) == 0) { n = 3; uv &= 0x0f; }
       else if ((uv & 0x08) == 0) { n = 4; uv &= 0x07; }
       else if ((uv & 0x04) == 0) { n = 5; uv &= 0x03; }
       else if ((uv & 0x02) == 0) { n = 6; uv &= 0x01; }
       else {
           throw new IllegalArgumentException("malformed UTF-8 character");
       }
       if (n > buffer.remaining() + 1) {
           throw new IllegalArgumentException(
                   "malformed UTF-8 character (expected " + n + " bytes, "
                   + "given " + (buffer.remaining() + 1)  + " bytes)");
       }
 
       int limit = n - 1;
 
       n--;
 
       if (n != 0) {
           while (n-- != 0) {
               c = buffer.get() & 0xff;
               if ((c & 0xc0) != 0x80) {
                   throw new IllegalArgumentException("malformed UTF-8 character");
               }
               else {
                   c &= 0x3f;
                   uv = uv << 6 | c;
               }
           }
       }
 
       if (uv < utf8_limits[limit]) {
           throw new IllegalArgumentException("redundant UTF-8 sequence");
       }
 
       return uv;
     }
 
     private static final long utf8_limits[] = {
         0x0,                        /* 1 */
         0x80,                       /* 2 */
         0x800,                      /* 3 */
         0x10000,                    /* 4 */
         0x200000,                   /* 5 */
         0x4000000,                  /* 6 */
         0x80000000,                 /* 7 */
     };
 
     private static int safeGet(ByteBuffer encode) {
         return encode.hasRemaining() ? encode.get() & 0xff : 0;
     }
 
     private static int safeGetIgnoreNull(ByteBuffer encode) {
         int next = 0;
         while (encode.hasRemaining() && next == 0) {
             next = safeGet(encode);
         }
         return next;
     }
 
     public static void decode(Ruby runtime, ByteBuffer encode, int occurrences,
             RubyArray result, Converter converter) {
         int lPadLength = 0;
 
         if (occurrences == IS_STAR) {
             occurrences = encode.remaining() / converter.size;
         } else if (occurrences > encode.remaining() / converter.size) {
             lPadLength = occurrences - encode.remaining() / converter.size;
             occurrences = encode.remaining() / converter.size;
         }
         for (; occurrences-- > 0;) {
             result.append(converter.decode(runtime, encode));
         }
 
         // MRI behavior:  for 'Q', do not add trailing nils
         if (converter != converters['Q']) {
             for (; lPadLength-- > 0;)
             result.append(runtime.getNil());
         }
     }
 
     public static int encode(Ruby runtime, int occurrences, ByteList result,
             RubyArray list, int index, ConverterExecutor converter) {
         int listSize = list.size();
 
         while (occurrences-- > 0) {
             if (listSize-- <= 0 || index >= list.size()) {
                 throw runtime.newArgumentError(sTooFew);
             }
 
             IRubyObject from = list.eltInternal(index++);
 
             converter.encode(runtime, from, result);
         }
 
         return index;
     }
 
     private abstract static class ConverterExecutor {
         protected Converter converter;
         public void setConverter(Converter converter) {
             this.converter = converter;
         }
 
         public abstract IRubyObject decode(Ruby runtime, ByteBuffer format);
         public abstract void encode(Ruby runtime, IRubyObject from, ByteList result);
     }
 
     private static ConverterExecutor executor18 = new ConverterExecutor() {
         @Override
         public IRubyObject decode(Ruby runtime, ByteBuffer format) {
             return converter.decode(runtime, format);
         }
 
         @Override
         public void encode(Ruby runtime, IRubyObject from, ByteList result) {
             converter.encode(runtime, from, result);
         }
     };
 
     private static ConverterExecutor executor19 = new ConverterExecutor() {
         @Override
         public IRubyObject decode(Ruby runtime, ByteBuffer format) {
             return converter.decode19(runtime, format);
         }
 
         @Override
         public void encode(Ruby runtime, IRubyObject from, ByteList result) {
             converter.encode19(runtime, from, result);
         }
     };
 
     public abstract static class Converter {
         public int size;
 
         public Converter(int size) {
             this.size = size;
         }
 
         public abstract IRubyObject decode(Ruby runtime, ByteBuffer format);
         public abstract void encode(Ruby runtime, IRubyObject from, ByteList result);
 
         public IRubyObject decode19(Ruby runtime, ByteBuffer format) {
             return decode(runtime, format);
         }
 
         public void encode19(Ruby runtime, IRubyObject from, ByteList result) {
             encode(runtime, from, result);
         }
     }
 
     private abstract static class QuadConverter extends Converter{
         public QuadConverter(int size) {
             super(size);
         }
 
         protected int overflowQuad(long quad) {
             return (int) (quad & 0xffff);
         }
 
         protected void encodeShortByByteOrder(ByteList result, int s) {
             if (Platform.BYTE_ORDER == Platform.BIG_ENDIAN) {
                 encodeShortBigEndian(result, s);
             } else {
                 encodeShortLittleEndian(result, s);
             }
         }
 
         protected void encodeLongByByteOrder(ByteList result, long l) {
             if (Platform.BYTE_ORDER == Platform.BIG_ENDIAN) {
                 encodeLongBigEndian(result, l);
             } else {
                 encodeLongLittleEndian(result, l);
             }
         }
     }
 
     /**
      * shrinks a stringbuffer.
      * shrinks a stringbuffer by a number of characters.
      * @param i2Shrink the stringbuffer
      * @param iLength how much to shrink
      * @return the stringbuffer
      **/
     private static final ByteList shrink(ByteList i2Shrink, int iLength) {
         iLength = i2Shrink.length() - iLength;
 
         if (iLength < 0) {
             throw new IllegalArgumentException();
         }
         i2Shrink.length(iLength);
         return i2Shrink;
     }
 
     /**
      * grows a stringbuffer.
      * uses the Strings to pad the buffer for a certain length
      * @param i2Grow the buffer to grow
      * @param iPads the string used as padding
      * @param iLength how much padding is needed
      * @return the padded buffer
      **/
     private static final ByteList grow(ByteList i2Grow, byte[]iPads, int iLength) {
         int lPadLength = iPads.length;
         while (iLength >= lPadLength) {
             i2Grow.append(iPads);
             iLength -= lPadLength;
         }
         i2Grow.append(iPads, 0, iLength);
         return i2Grow;
     }
 
     /**
      * pack_pack
      *
      * Template characters for Array#pack Directive  Meaning
      *              <table class="codebox" cellspacing="0" border="0" cellpadding="3">
      * <tr bgcolor="#ff9999">
      *   <td valign="top">
      *                     <b>Directive</b>
      *                   </td>
      *   <td valign="top">
      *                     <b>Meaning</b>
      *                   </td>
      * </tr>
      * <tr>
      *   <td valign="top">@</td>
      *   <td valign="top">Moves to absolute position</td>
      * </tr>
      * <tr>
      *   <td valign="top">A</td>
      *   <td valign="top">ASCII string (space padded, count is width)</td>
      * </tr>
      * <tr>
      *   <td valign="top">a</td>
      *   <td valign="top">ASCII string (null padded, count is width)</td>
      * </tr>
      * <tr>
      *   <td valign="top">B</td>
      *   <td valign="top">Bit string (descending bit order)</td>
      * </tr>
      * <tr>
      *   <td valign="top">b</td>
      *   <td valign="top">Bit string (ascending bit order)</td>
      * </tr>
      * <tr>
      *   <td valign="top">C</td>
      *   <td valign="top">Unsigned char</td>
      * </tr>
      * <tr>
      *   <td valign="top">c</td>
      *   <td valign="top">Char</td>
      * </tr>
      * <tr>
      *   <td valign="top">d</td>
      *   <td valign="top">Double-precision float, native format</td>
      * </tr>
      * <tr>
      *   <td valign="top">E</td>
      *   <td valign="top">Double-precision float, little-endian byte order</td>
      * </tr>
      * <tr>
      *   <td valign="top">e</td>
      *   <td valign="top">Single-precision float, little-endian byte order</td>
      * </tr>
      * <tr>
      *   <td valign="top">f</td>
      *   <td valign="top">Single-precision float, native format</td>
      * </tr>
      * <tr>
      *   <td valign="top">G</td>
      *   <td valign="top">Double-precision float, network (big-endian) byte order</td>
      * </tr>
      * <tr>
      *   <td valign="top">g</td>
      *   <td valign="top">Single-precision float, network (big-endian) byte order</td>
      * </tr>
      * <tr>
      *   <td valign="top">H</td>
      *   <td valign="top">Hex string (high nibble first)</td>
      * </tr>
      * <tr>
      *   <td valign="top">h</td>
      *   <td valign="top">Hex string (low nibble first)</td>
      * </tr>
      * <tr>
      *   <td valign="top">I</td>
      *   <td valign="top">Unsigned integer</td>
      * </tr>
      * <tr>
      *   <td valign="top">i</td>
      *   <td valign="top">Integer</td>
      * </tr>
      * <tr>
      *   <td valign="top">L</td>
      *   <td valign="top">Unsigned long</td>
      * </tr>
      * <tr>
      *   <td valign="top">l</td>
      *   <td valign="top">Long</td>
      * </tr>
      * <tr>
      *   <td valign="top">M</td>
      *   <td valign="top">Quoted printable, MIME encoding (see RFC2045)</td>
      * </tr>
      * <tr>
      *   <td valign="top">m</td>
      *   <td valign="top">Base64 encoded string</td>
      * </tr>
      * <tr>
      *   <td valign="top">N</td>
      *   <td valign="top">Long, network (big-endian) byte order</td>
      * </tr>
      * <tr>
      *   <td valign="top">n</td>
      *   <td valign="top">Short, network (big-endian) byte-order</td>
      * </tr>
      * <tr>
      *   <td valign="top">P</td>
      *   <td valign="top">Pointer to a structure (fixed-length string)</td>
      * </tr>
      * <tr>
      *   <td valign="top">p</td>
      *   <td valign="top">Pointer to a null-terminated string</td>
      * </tr>
      * <tr>
      *   <td valign="top">Q</td>
      *   <td valign="top">Unsigned 64-bit number</td>
      * </tr>
      * <tr>
      *   <td valign="top">q</td>
      *   <td valign="top">64-bit number</td>
      * </tr>
      * <tr>
      *   <td valign="top">S</td>
      *   <td valign="top">Unsigned short</td>
      * </tr>
      * <tr>
      *   <td valign="top">s</td>
      *   <td valign="top">Short</td>
      * </tr>
      * <tr>
      *   <td valign="top">U</td>
      *   <td valign="top">UTF-8</td>
      * </tr>
      * <tr>
      *   <td valign="top">u</td>
      *   <td valign="top">UU-encoded string</td>
      * </tr>
      * <tr>
      *   <td valign="top">V</td>
      *   <td valign="top">Long, little-endian byte order</td>
      * </tr>
      * <tr>
      *   <td valign="top">v</td>
      *   <td valign="top">Short, little-endian byte order</td>
      * </tr>
      * <tr>
      *   <td valign="top">X</td>
      *   <td valign="top">Back up a byte</td>
      * </tr>
      * <tr>
      *   <td valign="top">x</td>
      *   <td valign="top">Null byte</td>
      * </tr>
      * <tr>
      *   <td valign="top">Z</td>
      *   <td valign="top">Same as ``A''</td>
      * </tr>
      * <tr>
      *                   <td colspan="9" bgcolor="#ff9999" height="2"><img src="dot.gif" width="1" height="1"></td>
      *                 </tr>
      *               </table>
      *
      *
      * Packs the contents of arr into a binary sequence according to the directives in
      * aTemplateString (see preceding table).
      * Directives ``A,'' ``a,'' and ``Z'' may be followed by a count, which gives the
      * width of the resulting field.
      * The remaining directives also may take a count, indicating the number of array
      * elements to convert.
      * If the count is an asterisk (``*''] = all remaining array elements will be
      * converted.
      * Any of the directives ``sSiIlL'' may be followed by an underscore (``_'') to use
      * the underlying platform's native size for the specified type; otherwise, they
      * use a platform-independent size. Spaces are ignored in the template string.
      * @see RubyString#unpack
      **/
     @SuppressWarnings("fallthrough")
     public static RubyString pack(Ruby runtime, RubyArray list, ByteList formatString, boolean taint) {
         return packCommon(runtime, list, formatString, taint, executor18);
     }
 
     /**
      * Same as pack(Ruby, RubyArray, ByteList) but defaults tainting of output to false.
      */
     public static RubyString pack(Ruby runtime, RubyArray list, ByteList formatString) {
         return packCommon(runtime, list, formatString, false, executor18);
     }
 
     @SuppressWarnings("fallthrough")
     public static RubyString pack19(ThreadContext context, Ruby runtime, RubyArray list, RubyString formatString) {
         RubyString pack = packCommon(runtime, list, formatString.getByteList(), formatString.isTaint(), executor19);
         pack = (RubyString) pack.infectBy(formatString);
 
         for (IRubyObject element : list.toJavaArray()) {
             if (element.isUntrusted()) {
                 pack = (RubyString) pack.untrust(context);
                 break;
             }
         }
 
         return pack;
     }
 
     private static RubyString packCommon(Ruby runtime, RubyArray list, ByteList formatString, boolean tainted, ConverterExecutor executor) {
         ByteBuffer format = ByteBuffer.wrap(formatString.getUnsafeBytes(), formatString.begin(), formatString.length());
         ByteList result = new ByteList();
         boolean taintOutput = tainted;
         int listSize = list.size();
         int type = 0;
         int next = safeGet(format);
 
         int idx = 0;
         ByteList lCurElemString;
 
         int enc_info = 1;
 
         mainLoop: while (next != 0) {
             type = next;
             next = safeGet(format);
             if (PACK_IGNORE_NULL_CODES.indexOf(type) != -1 && next == 0) {
                 next = safeGetIgnoreNull(format);
             }
 
             // Skip all whitespace in pack format string
             while (ASCII.isSpace(type)) {
                 if (next == 0) break mainLoop;
                 type = next;
                 next = safeGet(format);
             }
 
             // Skip embedded comments in pack format string
             if (type == '#') {
                 while (type != '\n') {
                     if (next == 0) break mainLoop;
                     type = next;
                     next = safeGet(format);
                 }
             }
 
             if (next == '!' || next == '_') {
                 int index = NATIVE_CODES.indexOf(type);
                 if (index == -1) {
                     throw runtime.newArgumentError("'" + next +
                             "' allowed only after types " + NATIVE_CODES);
                 }
                 int typeBeforeMap = type;
                 type = MAPPED_CODES.charAt(index);
 
                 next = safeGet(format);
                 if (PACK_IGNORE_NULL_CODES_WITH_MODIFIERS.indexOf(typeBeforeMap) != -1 && next == 0) {
                     next = safeGetIgnoreNull(format);
                 }
             }
 
             // Determine how many of type are needed (default: 1)
             int occurrences = 1;
             boolean isStar = false;
             boolean ignoreStar = false;
             if (next != 0) {
                 if (next == '*') {
                     if ("@XxumM".indexOf(type) != -1) {
                         occurrences = 0;
                         ignoreStar = true;
                     } else {
                         occurrences = list.size() - idx;
                         isStar = true;
                     }
                     next = safeGet(format);
                 } else if (ASCII.isDigit(next)) {
                     occurrences = 0;
                     do {
                         occurrences = occurrences * 10 + Character.digit((char)(next & 0xFF), 10);
                         next = safeGet(format);
                     } while (next != 0 && ASCII.isDigit(next));
                 }
             }
 
             if (runtime.is1_9()) {
                 switch (type) {
                     case 'U':
                         if (enc_info == 1) enc_info = 2;
                         break;
                     case 'm':
                     case 'M':
                     case 'u':
                         break;
                     default:
                         enc_info = 0;
                         break;
                 }
             }
 
             Converter converter = converters[type];
 
             if (converter != null) {
                 executor.setConverter(converter);
                 idx = encode(runtime, occurrences, result, list, idx, executor);
                 continue;
             }
 
             switch (type) {
                 case '%' :
                     throw runtime.newArgumentError("% is not supported");
                 case 'A' :
                 case 'a' :
                 case 'Z' :
                 case 'B' :
                 case 'b' :
                 case 'H' :
                 case 'h' :
                     {
                         if (listSize-- <= 0) {
                             throw runtime.newArgumentError(sTooFew);
                         }
 
                         IRubyObject from = list.eltInternal(idx++);
                         if(from.isTaint()) {
                             taintOutput = true;
                         }
                         lCurElemString = from == runtime.getNil() ? ByteList.EMPTY_BYTELIST : from.convertToString().getByteList();
 
                         if (isStar) {
                             occurrences = lCurElemString.length();
                             // 'Z' adds extra null pad (versus 'a')
                             if (type == 'Z') occurrences++;
                         }
 
                         switch (type) {
                             case 'a' :
                             case 'A' :
                             case 'Z' :
                                 if (lCurElemString.length() >= occurrences) {
                                     result.append(lCurElemString.getUnsafeBytes(), lCurElemString.getBegin(), occurrences);
                                 } else {//need padding
                                     //I'm fairly sure there is a library call to create a
                                     //string filled with a given char with a given length but I couldn't find it
                                     result.append(lCurElemString);
                                     occurrences -= lCurElemString.length();
 
                                     switch (type) {
                                       case 'a':
                                       case 'Z':
                                           grow(result, sNil10, occurrences);
                                           break;
                                       default:
                                           grow(result, sSp10, occurrences);
                                           break;
                                     }
                                 }
                             break;
                             case 'b' :
                                 {
                                     int currentByte = 0;
                                     int padLength = 0;
 
                                     if (occurrences > lCurElemString.length()) {
                                         padLength = (occurrences - lCurElemString.length()) / 2 + occurrences % 2;
                                         occurrences = lCurElemString.length();
                                     }
 
                                     for (int i = 0; i < occurrences;) {
                                         if ((lCurElemString.charAt(i++) & 1) != 0) {//if the low bit is set
                                             currentByte |= 128; //set the high bit of the result
                                         }
 
                                         if ((i & 7) == 0) {
                                             result.append((byte) (currentByte & 0xff));
                                             currentByte = 0;
                                             continue;
                                         }
 
                                            //if the index is not a multiple of 8, we are not on a byte boundary
                                            currentByte >>= 1; //shift the byte
                                     }
 
                                     if ((occurrences & 7) != 0) { //if the length is not a multiple of 8
                                         currentByte >>= 7 - (occurrences & 7); //we need to pad the last byte
                                         result.append((byte) (currentByte & 0xff));
                                     }
 
                                     //do some padding, I don't understand the padding strategy
                                     result.length(result.length() + padLength);
                                 }
                             break;
                             case 'B' :
                                 {
                                     int currentByte = 0;
                                     int padLength = 0;
 
                                     if (occurrences > lCurElemString.length()) {
                                         padLength = (occurrences - lCurElemString.length()) / 2 + occurrences % 2;
                                         occurrences = lCurElemString.length();
                                     }
 
                                     for (int i = 0; i < occurrences;) {
                                         currentByte |= lCurElemString.charAt(i++) & 1;
 
                                         // we filled up current byte; append it and create next one
                                         if ((i & 7) == 0) {
                                             result.append((byte) (currentByte & 0xff));
                                             currentByte = 0;
                                             continue;
                                         }
 
                                         //if the index is not a multiple of 8, we are not on a byte boundary
                                         currentByte <<= 1;
                                     }
 
                                     if ((occurrences & 7) != 0) { //if the length is not a multiple of 8
                                         currentByte <<= 7 - (occurrences & 7); //we need to pad the last byte
                                         result.append((byte) (currentByte & 0xff));
                                     }
 
                                     result.length(result.length() + padLength);
                                 }
                             break;
                             case 'h' :
                                 {
                                     int currentByte = 0;
                                     int padLength = 0;
 
                                     if (occurrences > lCurElemString.length()) {
                                         padLength = occurrences - lCurElemString.length() + 1;
                                         occurrences = lCurElemString.length();
                                     }
 
                                     for (int i = 0; i < occurrences;) {
                                         byte currentChar = (byte)lCurElemString.charAt(i++);
 
                                         if (Character.isJavaIdentifierStart(currentChar)) {
                                             //this test may be too lax but it is the same as in MRI
                                             currentByte |= (((currentChar & 15) + 9) & 15) << 4;
                                         } else {
                                             currentByte |= (currentChar & 15) << 4;
                                         }
 
                                         if ((i & 1) != 0) {
                                             currentByte >>= 4;
                                         } else {
                                             result.append((byte) (currentByte & 0xff));
                                             currentByte = 0;
                                         }
                                     }
 
                                     if ((occurrences & 1) != 0) {
                                         result.append((byte) (currentByte & 0xff));
                                         if(padLength > 0) {
                                             padLength--;
                                         }
                                     }
 
                                     result.length(result.length() + padLength / 2);
                                 }
                             break;
                             case 'H' :
                                 {
                                     int currentByte = 0;
                                     int padLength = 0;
 
                                     if (occurrences > lCurElemString.length()) {
                                         padLength = occurrences - lCurElemString.length() + 1;
                                         occurrences = lCurElemString.length();
                                     }
 
                                     for (int i = 0; i < occurrences;) {
                                         byte currentChar = (byte)lCurElemString.charAt(i++);
 
                                         if (Character.isJavaIdentifierStart(currentChar)) {
                                             //this test may be too lax but it is the same as in MRI
                                             currentByte |= ((currentChar & 15) + 9) & 15;
                                         } else {
                                             currentByte |= currentChar & 15;
                                         }
 
                                         if ((i & 1) != 0) {
                                             currentByte <<= 4;
                                         } else {
                                             result.append((byte) (currentByte & 0xff));
                                             currentByte = 0;
                                         }
                                     }
 
                                     if ((occurrences & 1) != 0) {
                                         result.append((byte) (currentByte & 0xff));
                                         if(padLength > 0) {
                                             padLength--;
                                         }
                                     }
 
                                     result.length(result.length() + padLength / 2);
                                 }
                             break;
                         }
                         break;
                     }
 
                 case 'x' :
                     grow(result, sNil10, occurrences);
                     break;
                 case 'X' :
                     try {
                         shrink(result, occurrences);
                     } catch (IllegalArgumentException e) {
                         throw runtime.newArgumentError("in `pack': X outside of string");
                     }
                     break;
                 case '@' :
                     occurrences -= result.length();
                     if (occurrences > 0) {
                         grow(result, sNil10, occurrences);
                     }
                     occurrences = -occurrences;
                     if (occurrences > 0) {
                         shrink(result, occurrences);
                     }
                     break;
                 case 'u' :
                 case 'm' :
                     {
                         if (listSize-- <= 0) {
                             throw runtime.newArgumentError(sTooFew);
                         }
                         IRubyObject from = list.eltInternal(idx++);
                         lCurElemString = from == runtime.getNil() ?
                             ByteList.EMPTY_BYTELIST :
                             from.convertToString().getByteList();
                         if (runtime.is1_9() && occurrences == 0 && type == 'm' && !ignoreStar) {
                             encodes(runtime, result, lCurElemString.getUnsafeBytes(),
                                     lCurElemString.getBegin(), lCurElemString.length(),
                                     lCurElemString.length(), (byte)type, false);
                             break;
                         }
 
                         occurrences = occurrences <= 2 ? 45 : occurrences / 3 * 3;
                         if (lCurElemString.length() == 0) break;
 
                         byte[] charsToEncode = lCurElemString.getUnsafeBytes();
                         for (int i = 0; i < lCurElemString.length(); i += occurrences) {
                             encodes(runtime, result, charsToEncode,
                                     i + lCurElemString.getBegin(), lCurElemString.length() - i,
                                     occurrences, (byte)type, true);
                         }
                     }
                     break;
                 case 'M' :
                     {
                        if (listSize-- <= 0) {
                            throw runtime.newArgumentError(sTooFew);
                        }
 
                        IRubyObject from = list.eltInternal(idx++);
                        lCurElemString = from == runtime.getNil() ? ByteList.EMPTY_BYTELIST : from.asString().getByteList();
 
                        if (occurrences <= 1) {
                            occurrences = 72;
                        }
 
                        qpencode(result, lCurElemString, occurrences);
                     }
                     break;
                 case 'U' :
                     while (occurrences-- > 0) {
                         if (listSize-- <= 0) {
                            throw runtime.newArgumentError(sTooFew);
                         }
 
                         IRubyObject from = list.eltInternal(idx++);
                         int code = from == runtime.getNil() ? 0 : RubyNumeric.num2int(from);
 
                         if (code < 0) {
                             throw runtime.newRangeError("pack(U): value out of range");
                         }
 
                         result.ensure(result.getRealSize() + 6);
                         result.setRealSize(result.getRealSize() + utf8Decode(runtime, result.getUnsafeBytes(), result.getBegin() + result.getRealSize(), code));
                     }
                     break;
                 case 'w' :
                     while (occurrences-- > 0) {
                         if (listSize-- <= 0) {
                             throw runtime.newArgumentError(sTooFew);
                         }
 
                         ByteList buf = new ByteList();
                         IRubyObject from = list.eltInternal(idx++);
 
                         if(from.isNil()) {
                             throw runtime.newTypeError("pack('w') does not take nil");
                         }
 
 
                         if(from instanceof RubyBignum) {
                             RubyBignum big128 = RubyBignum.newBignum(runtime, 128);
                             while (from instanceof RubyBignum) {
                                 RubyBignum bignum = (RubyBignum)from;
                                 RubyArray ary = (RubyArray)bignum.divmod(runtime.getCurrentContext(), big128);
                                 buf.append((byte)(RubyNumeric.fix2int(ary.at(RubyFixnum.one(runtime))) | 0x80) & 0xff);
                                 from = ary.at(RubyFixnum.zero(runtime));
                             }
                         }
 
                         long l = RubyNumeric.num2long(from);
 
                         // we don't deal with negatives.
                         if (l >= 0) {
 
                             while(l != 0) {
                                 buf.append((byte)(((l & 0x7f) | 0x80) & 0xff));
                                 l >>= 7;
                             }
 
                             int left = 0;
                             int right = buf.getRealSize() - 1;
 
                             if(right >= 0) {
                                 buf.getUnsafeBytes()[0] &= 0x7F;
                             } else {
                                 buf.append(0);
                             }
 
                             while(left < right) {
                                 byte tmp = buf.getUnsafeBytes()[left];
                                 buf.getUnsafeBytes()[left] = buf.getUnsafeBytes()[right];
                                 buf.getUnsafeBytes()[right] = tmp;
 
                                 left++;
                                 right--;
                             }
 
                             result.append(buf);
                         } else {
                             throw runtime.newArgumentError("can't compress negative numbers");
                         }
                     }
 
                     break;
             }
         }        
 
         RubyString output = runtime.newString(result);
         if(taintOutput) {
             output.taint(runtime.getCurrentContext());
         }
 
         if (runtime.is1_9()) {
             switch (enc_info)
             {
                 case 1:
                     output.setEncodingAndCodeRange(USASCII, RubyObject.USER8_F);
                     break;
                 case 2:
                     output.force_encoding(runtime.getCurrentContext(),
-                            RubyEncoding.convertEncodingToRubyEncoding(runtime, UTF8));
+                            runtime.getEncodingService().convertEncodingToRubyEncoding(UTF8));
                     break;
                 default:
                     /* do nothing, keep ASCII-8BIT */
             }
         }
 
         return output;
     }
 
     /**
      * Retrieve an encoded int in little endian starting at index in the
      * string value.
      *
      * @param encode string to get int from
      * @return the decoded integer
      */
     private static int decodeIntLittleEndian(ByteBuffer encode) {
         encode.order(ByteOrder.LITTLE_ENDIAN);
         int value = encode.getInt();
         encode.order(ByteOrder.BIG_ENDIAN);
         return value;
     }
 
     /**
      * Retrieve an encoded int in little endian starting at index in the
      * string value.
      *
      * @param encode string to get int from
      * @return the decoded integer
      */
     private static int decodeIntBigEndian(ByteBuffer encode) {
         return encode.getInt();
     }
 
     /**
      * Retrieve an encoded int in big endian starting at index in the string
      * value.
      *
      * @param encode string to get int from
      * @return the decoded integer
      */
     private static long decodeIntUnsignedBigEndian(ByteBuffer encode) {
         return (long)encode.getInt() & 0xFFFFFFFFL;
     }
 
     /**
      * Retrieve an encoded int in little endian starting at index in the
      * string value.
      *
      * @param encode the encoded string
      * @return the decoded integer
      */
     private static long decodeIntUnsignedLittleEndian(ByteBuffer encode) {
         encode.order(ByteOrder.LITTLE_ENDIAN);
         long value = encode.getInt() & 0xFFFFFFFFL;
         encode.order(ByteOrder.BIG_ENDIAN);
         return value;
     }
 
     /**
      * Encode an int in little endian format into a packed representation.
      *
      * @param result to be appended to
      * @param s the integer to encode
      */
     private static void encodeIntLittleEndian(ByteList result, int s) {
         result.append((byte) (s & 0xff)).append((byte) ((s >> 8) & 0xff));
         result.append((byte) ((s>>16) & 0xff)).append((byte) ((s>>24) &0xff));
     }
 
     /**
      * Encode an int in big-endian format into a packed representation.
      *
      * @param result to be appended to
      * @param s the integer to encode
      */
     private static void encodeIntBigEndian(ByteList result, int s) {
         result.append((byte) ((s>>24) &0xff)).append((byte) ((s>>16) &0xff));
         result.append((byte) ((s >> 8) & 0xff)).append((byte) (s & 0xff));
     }
 
     /**
      * Decode a long in big-endian format from a packed value
      *
      * @param encode string to get int from
      * @return the long value
      */
     private static long decodeLongBigEndian(ByteBuffer encode) {
         int c1 = decodeIntBigEndian(encode);
         int c2 = decodeIntBigEndian(encode);
 
         return ((long) c1 << 32) + (c2 & 0xffffffffL);
     }
 
     /**
      * Decode a long in little-endian format from a packed value
      *
      * @param encode string to get int from
      * @return the long value
      */
     private static long decodeLongLittleEndian(ByteBuffer encode) {
         int c1 = decodeIntLittleEndian(encode);
         int c2 = decodeIntLittleEndian(encode);
 
         return ((long) c2 << 32) + (c1 & 0xffffffffL);
     }
 
     /**
      * Encode a long in little-endian format into a packed value
      *
      * @param result to pack long into
      * @param l is the long to encode
      */
     private static void encodeLongLittleEndian(ByteList result, long l) {
         encodeIntLittleEndian(result, (int) (l & 0xffffffff));
         encodeIntLittleEndian(result, (int) (l >>> 32));
     }
 
     /**
      * Encode a long in big-endian format into a packed value
      *
      * @param result to pack long into
      * @param l is the long to encode
      */
     private static void encodeLongBigEndian(ByteList result, long l) {
         encodeIntBigEndian(result, (int) (l >>> 32));
         encodeIntBigEndian(result, (int) (l & 0xffffffff));
     }
 
     /**
      * Decode a double from a packed value
      *
      * @param encode string to get int from
      * @return the double value
      */
     private static double decodeDoubleLittleEndian(ByteBuffer encode) {
         return Double.longBitsToDouble(decodeLongLittleEndian(encode));
     }
 
     /**
      * Decode a double in big-endian from a packed value
      *
      * @param encode string to get int from
      * @return the double value
      */
     private static double decodeDoubleBigEndian(ByteBuffer encode) {
         return Double.longBitsToDouble(decodeLongBigEndian(encode));
     }
 
     /**
      * Encode a double in little endian format into a packed value
      *
      * @param result to pack double into
      * @param d is the double to encode
      */
     private static void encodeDoubleLittleEndian(ByteList result, double d) {
         encodeLongLittleEndian(result, Double.doubleToRawLongBits(d));
     }
 
     /**
      * Encode a double in big-endian format into a packed value
      *
      * @param result to pack double into
      * @param d is the double to encode
      */
     private static void encodeDoubleBigEndian(ByteList result, double d) {
         encodeLongBigEndian(result, Double.doubleToRawLongBits(d));
     }
 
     /**
      * Decode a float in big-endian from a packed value
      *
      * @param encode string to get int from
      * @return the double value
      */
     private static float decodeFloatBigEndian(ByteBuffer encode) {
         return Float.intBitsToFloat(decodeIntBigEndian(encode));
     }
 
     /**
      * Decode a float in little-endian from a packed value
      *
      * @param encode string to get int from
      * @return the double value
      */
     private static float decodeFloatLittleEndian(ByteBuffer encode) {
         return Float.intBitsToFloat(decodeIntLittleEndian(encode));
     }
 
     /**
      * Encode a float in little endian format into a packed value
      * @param result to pack float into
      * @param f is the float to encode
      */
     private static void encodeFloatLittleEndian(ByteList result, float f) {
         encodeIntLittleEndian(result, Float.floatToRawIntBits(f));
     }
 
     /**
      * Encode a float in big-endian format into a packed value
      * @param result to pack float into
      * @param f is the float to encode
      */
     private static void encodeFloatBigEndian(ByteList result, float f) {
         encodeIntBigEndian(result, Float.floatToRawIntBits(f));
     }
 
     /**
      * Decode a short in little-endian from a packed value
      *
      * @param encode string to get int from
      * @return the short value
      */
     private static int decodeShortUnsignedLittleEndian(ByteBuffer encode) {
         encode.order(ByteOrder.LITTLE_ENDIAN);
         int value = encode.getShort() & 0xFFFF;
         encode.order(ByteOrder.BIG_ENDIAN);
         return value;
     }
 
     /**
      * Decode a short in big-endian from a packed value
      *
      * @param encode string to get int from
      * @return the short value
      */
     private static int decodeShortUnsignedBigEndian(ByteBuffer encode) {
         int value = encode.getShort() & 0xFFFF;
         return value;
     }
 
     /**
      * Decode a short in little-endian from a packed value
      *
      * @param encode string to get int from
      * @return the short value
      */
     private static int decodeShortLittleEndian(ByteBuffer encode) {
         encode.order(ByteOrder.LITTLE_ENDIAN);
         int value = encode.getShort();
         encode.order(ByteOrder.BIG_ENDIAN);
         return value;
     }
 
     /**
      * Decode a short in big-endian from a packed value
      *
      * @param encode string to get int from
      * @return the short value
      */
     private static short decodeShortBigEndian(ByteBuffer encode) {
         return encode.getShort();
     }
 
     /**
      * Encode an short in little endian format into a packed representation.
      *
      * @param result to be appended to
      * @param s the short to encode
      */
     private static void encodeShortLittleEndian(ByteList result, int s) {
         result.append((byte) (s & 0xff)).append((byte) ((s & 0xff00) >> 8));
     }
 
     /**
      * Encode an shortin big-endian format into a packed representation.
      *
      * @param result to be appended to
      * @param s the short to encode
      */
     private static void encodeShortBigEndian(ByteList result, int s) {
         result.append((byte) ((s & 0xff00) >> 8)).append((byte) (s & 0xff));
     }
 }
