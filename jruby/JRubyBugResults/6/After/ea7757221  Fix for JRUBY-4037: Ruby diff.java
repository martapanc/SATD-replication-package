diff --git a/src/org/jruby/RubyRegexp.java b/src/org/jruby/RubyRegexp.java
index b61481c0fe..b711c7e101 100644
--- a/src/org/jruby/RubyRegexp.java
+++ b/src/org/jruby/RubyRegexp.java
@@ -1,1264 +1,1264 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Nick Sieger <nicksieger@gmail.com>
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
 
 import static org.jruby.anno.FrameField.BACKREF;
 import static org.jruby.anno.FrameField.LASTLINE;
 
 import java.lang.ref.SoftReference;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jcodings.Encoding;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jcodings.specific.UTF8Encoding;
 import org.joni.Matcher;
 import org.joni.NameEntry;
 import org.joni.Option;
 import org.joni.Regex;
 import org.joni.Region;
 import org.joni.Syntax;
 import org.joni.exception.JOniException;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.parser.ReOptions;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.Pack;
 import org.jruby.util.Sprintf;
 import org.jruby.util.StringSupport;
 import org.jruby.util.TypeConverter;
 
 @JRubyClass(name="Regexp")
 public class RubyRegexp extends RubyObject implements ReOptions, EncodingCapable {
     private KCode kcode;
     private Regex pattern;
     private ByteList str = ByteList.EMPTY_BYTELIST;
 
     private static final int REGEXP_LITERAL_F       =   USER1_F;
     private static final int REGEXP_KCODE_DEFAULT   =   USER2_F;
     private static final int REGEXP_ENCODING_NONE   =   USER3_F;
 
     private static final int ARG_OPTION_MASK        =   RE_OPTION_IGNORECASE | RE_OPTION_EXTENDED | RE_OPTION_MULTILINE; 
     private static final int ARG_ENCODING_FIXED     =   16;
     private static final int ARG_ENCODING_NONE      =   32;
 
     public void setLiteral() {
         flags |= REGEXP_LITERAL_F;
     }
 
     public void clearLiteral() {
         flags &= ~REGEXP_LITERAL_F;
     }
 
     public boolean isLiteral() {
         return (flags & REGEXP_LITERAL_F) != 0;
     }
 
     public void setKCodeDefault() {
         flags |= REGEXP_KCODE_DEFAULT;
     }
 
     public void clearKCodeDefault() {
         flags &= ~REGEXP_KCODE_DEFAULT;
     }
 
     public boolean isKCodeDefault() {
         return (flags & REGEXP_KCODE_DEFAULT) != 0;
     }
 
     public void setEncodingNone() {
         flags |= REGEXP_ENCODING_NONE;
     }
 
     public void clearEncodingNone() {
         flags &= ~REGEXP_ENCODING_NONE;
     }
 
     public boolean isEncodingNone() {
         return (flags & REGEXP_ENCODING_NONE) != 0;
     }
 
     public KCode getKCode() {
         return kcode;
     }
 
     public Encoding getEncoding() {
         return pattern.getEncoding();
     }
 
     private static final class RegexpCache {
         private volatile SoftReference<Map<ByteList, Regex>> cache = new SoftReference<Map<ByteList, Regex>>(null);
         private Map<ByteList, Regex> get() {
             Map<ByteList, Regex> patternCache = cache.get();
             if (patternCache == null) {
                 patternCache = new ConcurrentHashMap<ByteList, Regex>(5);
                 cache = new SoftReference<Map<ByteList, Regex>>(patternCache);
             }
             return patternCache;
         }
     }
 
     private static final RegexpCache patternCache = new RegexpCache();
     private static final RegexpCache quotedPatternCache = new RegexpCache();
     private static final RegexpCache preprocessedPatternCache = new RegexpCache();
 
     private static Regex makeRegexp(Ruby runtime, ByteList bytes, int flags, Encoding enc) {
         try {
             int p = bytes.begin;
             return new Regex(bytes.bytes, p, p + bytes.realSize, flags, enc, Syntax.DEFAULT, runtime.getWarnings());
         } catch (Exception e) {
             if (runtime.is1_9()) {
                 raiseRegexpError19(runtime, bytes, enc, flags, e.getMessage());
             } else {
                 raiseRegexpError(runtime, bytes, enc, flags, e.getMessage());
             }
             return null; // not reached
         }
     }
 
     static Regex getRegexpFromCache(Ruby runtime, ByteList bytes, Encoding enc, int options) {
         Map<ByteList, Regex> cache = patternCache.get();
         Regex regex = cache.get(bytes);
         if (regex != null && regex.getEncoding() == enc && regex.getOptions() == options) return regex;
         regex = makeRegexp(runtime, bytes, options, enc);
         cache.put(bytes, regex);
         return regex;
     }
 
     static Regex getQuotedRegexpFromCache(Ruby runtime, ByteList bytes, Encoding enc, int options) {
         Map<ByteList, Regex> cache = quotedPatternCache.get();
         Regex regex = cache.get(bytes);
         if (regex != null && regex.getEncoding() == enc && regex.getOptions() == options) return regex;
         regex = makeRegexp(runtime, quote(bytes, enc), options, enc);
         cache.put(bytes, regex);
         return regex;
     }
 
     static Regex getQuotedRegexpFromCache19(Ruby runtime, ByteList bytes, int options, boolean asciiOnly) {
         Map<ByteList, Regex> cache = quotedPatternCache.get();
         Regex regex = cache.get(bytes);
         Encoding enc = asciiOnly ? USASCIIEncoding.INSTANCE : bytes.encoding;
         if (regex != null && regex.getEncoding() == enc && regex.getOptions() == options) return regex;
         ByteList quoted = quote19(bytes, asciiOnly);
         regex = makeRegexp(runtime, quoted, options, quoted.encoding);
         regex.setUserObject(quoted);
         cache.put(bytes, regex);
         return regex;
     }
 
     private static Regex getPreprocessedRegexpFromCache(Ruby runtime, ByteList bytes, Encoding enc, int options, ErrorMode mode) {
         Map<ByteList, Regex> cache = preprocessedPatternCache.get();
         Regex regex = cache.get(bytes);
         if (regex != null && regex.getEncoding() == enc && regex.getOptions() == options) return regex;
         ByteList preprocessed = preprocess(runtime, bytes, enc, new Encoding[]{null}, ErrorMode.RAISE);
         regex = makeRegexp(runtime, preprocessed, options, enc);
         regex.setUserObject(preprocessed);
         cache.put(bytes, regex);
         return regex;
     }
 
     public static RubyClass createRegexpClass(Ruby runtime) {
         RubyClass regexpClass = runtime.defineClass("Regexp", runtime.getObject(), REGEXP_ALLOCATOR);
         runtime.setRegexp(regexpClass);
         regexpClass.index = ClassIndex.REGEXP;
         regexpClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyRegexp;
             }
         };
 
         regexpClass.defineConstant("IGNORECASE", runtime.newFixnum(RE_OPTION_IGNORECASE));
         regexpClass.defineConstant("EXTENDED", runtime.newFixnum(RE_OPTION_EXTENDED));
         regexpClass.defineConstant("MULTILINE", runtime.newFixnum(RE_OPTION_MULTILINE));
 
         if (runtime.is1_9()) regexpClass.defineConstant("FIXEDENCODING", runtime.newFixnum(ARG_ENCODING_FIXED));
 
         regexpClass.defineAnnotatedMethods(RubyRegexp.class);
 
         return regexpClass;
     }
 
     private static ObjectAllocator REGEXP_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyRegexp(runtime, klass);
         }
     };
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.REGEXP;
     }
 
     /** used by allocator
      */
     private RubyRegexp(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
     }
 
     /** default constructor
      */
     private RubyRegexp(Ruby runtime) {
         super(runtime, runtime.getRegexp());
     }
 
     private RubyRegexp(Ruby runtime, ByteList str) {
         this(runtime);
         setKCodeDefault();
         this.kcode = runtime.getKCode();
         this.str = str;
         this.pattern = getRegexpFromCache(runtime, str, kcode.getEncoding(), 0);
     }
 
     private RubyRegexp(Ruby runtime, ByteList str, int options) {
         this(runtime);
-        setKCode(runtime, options);
+        setKCode(runtime, options & 0x7f); // mask off "once" flag
         this.str = str;
         this.pattern = getRegexpFromCache(runtime, str, kcode.getEncoding(), options & 0xf);
     }
 
     // used only by the compiler/interpreter (will set the literal flag)
     public static RubyRegexp newRegexp(Ruby runtime, String pattern, int options) {
         return newRegexp(runtime, ByteList.create(pattern), options);
     }
 
     // used only by the compiler/interpreter (will set the literal flag)
     public static RubyRegexp newRegexp(Ruby runtime, ByteList pattern, int options) {
         try {
             return new RubyRegexp(runtime, pattern, options);
         } catch (RaiseException re) {
             throw runtime.newSyntaxError(re.getMessage());
         }
     }
 
     // used only by the compiler/interpreter (will set the literal flag)
     public static RubyRegexp newDRegexp(Ruby runtime, RubyString pattern, int options) {
         try {
             return new RubyRegexp(runtime, pattern.getByteList(), options);
         } catch (RaiseException re) {
             throw runtime.newRegexpError(re.getMessage());
         }
     }
 
     public static RubyRegexp newRegexp(Ruby runtime, ByteList pattern) {
         return new RubyRegexp(runtime, pattern);
     }
 
     static RubyRegexp newRegexp(Ruby runtime, ByteList str, Regex pattern) {
         RubyRegexp regexp = new RubyRegexp(runtime);
         regexp.str = str;
         regexp.setKCode(runtime, pattern.getOptions());
         regexp.pattern = pattern;
         return regexp;
     }
     
     // internal usage (Complex/Rational)
     static RubyRegexp newDummyRegexp(Ruby runtime, Regex regex) {
         RubyRegexp regexp = new RubyRegexp(runtime);
         regexp.pattern = regex;
         regexp.str = ByteList.EMPTY_BYTELIST;
         regexp.kcode = KCode.NONE;
         return regexp;
     }
 
     /** rb_get_kcode
      */
     private int getKcode() {
         if (kcode == KCode.NONE) {
             return 16;
         } else if (kcode == KCode.EUC) {
             return 32;
         } else if (kcode == KCode.SJIS) {
             return 48;
         } else if (kcode == KCode.UTF8) {
             return 64;
         }
         return 0;
     }
 
     /** rb_set_kcode
      */
     private void setKCode(Ruby runtime, int options) {
         clearKCodeDefault();
         switch (options & ~0xf) {
         case 0:
         default:
             setKCodeDefault();
             kcode = runtime.getKCode();
             break;
         case 16:
             kcode = KCode.NONE;
             break;
         case 32:
             kcode = KCode.EUC;
             break;
         case 48:
             kcode = KCode.SJIS;
             break;
         case 64:
             kcode = KCode.UTF8;
             break;
         }        
     }
 
     /** rb_reg_options
      */
     private int getOptions() {
         check();
         int options = (pattern.getOptions() & (RE_OPTION_IGNORECASE|RE_OPTION_MULTILINE|RE_OPTION_EXTENDED));
         if (!isKCodeDefault()) options |= getKcode();
         return options;
     }
 
     final Regex getPattern() {
         check();
         return pattern;
     }
 
     private static void encodingMatchError(Ruby runtime, Regex pattern, Encoding strEnc) {
         throw runtime.newEncodingCompatibilityError("incompatible encoding regexp match (" +
                 pattern.getEncoding() + " regexp with " + strEnc + " string)");
     }
 
     private Encoding checkEncoding(RubyString str, boolean warn) {
         if (str.scanForCodeRange() == StringSupport.CR_BROKEN) {
             throw getRuntime().newArgumentError("invalid byte sequence in " + str.getEncoding());
         }
         check();
         Encoding enc = str.getEncoding();
         if (!enc.isAsciiCompatible()) {
             if (enc != pattern.getEncoding()) encodingMatchError(getRuntime(), pattern, enc);
         } else if (!isKCodeDefault()) {
             if (enc != pattern.getEncoding() && 
                (!pattern.getEncoding().isAsciiCompatible() ||
                str.scanForCodeRange() != StringSupport.CR_7BIT)) encodingMatchError(getRuntime(), pattern, enc);
             enc = pattern.getEncoding();
         }
         if (warn && isEncodingNone() && enc != ASCIIEncoding.INSTANCE && str.scanForCodeRange() != StringSupport.CR_7BIT) {
             getRuntime().getWarnings().warn(ID.REGEXP_MATCH_AGAINST_STRING, "regexp match /.../n against to " + enc + " string");
         }
         return enc;
     }
 
     final Regex preparePattern(RubyString str) {
         check();
         Encoding enc = checkEncoding(str, true);
         if (enc == pattern.getEncoding()) return pattern;
         return getPreprocessedRegexpFromCache(getRuntime(), this.str, enc, pattern.getOptions(), ErrorMode.PREPROCESS);
     }
 
     static Regex preparePattern(Ruby runtime, Regex pattern, RubyString str) {
         if (str.scanForCodeRange() == StringSupport.CR_BROKEN) {
             throw runtime.newArgumentError("invalid byte sequence in " + str.getEncoding());
         }
         Encoding enc = str.getEncoding();
         if (!enc.isAsciiCompatible()) {
             if (enc != pattern.getEncoding()) encodingMatchError(runtime, pattern, enc);
         }
         // TODO: check for isKCodeDefault() somehow
 //        if (warn && isEncodingNone() && enc != ASCIIEncoding.INSTANCE && str.scanForCodeRange() != StringSupport.CR_7BIT) {
 //            getRuntime().getWarnings().warn(ID.REGEXP_MATCH_AGAINST_STRING, "regexp match /.../n against to " + enc + " string");
 //        }
         if (enc == pattern.getEncoding()) return pattern;
         return getPreprocessedRegexpFromCache(runtime, (ByteList)pattern.getUserObject(), enc, pattern.getOptions(), ErrorMode.PREPROCESS);
     }
 
     private static enum ErrorMode {RAISE, PREPROCESS, DESC} 
 
     private static int raisePreprocessError(Ruby runtime, ByteList str, String err, ErrorMode mode) {
         switch (mode) {
         case RAISE:
             raiseRegexpError19(runtime, str, str.encoding, 0, err);
         case PREPROCESS:
             throw runtime.newArgumentError("regexp preprocess failed: " + err);
         case DESC:
             // silent ?
         }
         return 0;
     }
 
     private static int readEscapedByte(Ruby runtime, byte[]to, int toP, byte[]bytes, int p, int end, ByteList str, ErrorMode mode) {
         if (p == end || bytes[p++] != (byte)'\\') raisePreprocessError(runtime, str, "too short escaped multibyte character", mode);
 
         boolean metaPrefix = false, ctrlPrefix = false;
         int code = 0;
         while (true) {
             if (p == end) raisePreprocessError(runtime, str, "too short escape sequence", mode);
 
             switch (bytes[p++]) {
             case '\\': code = '\\'; break;
             case 'n': code = '\n'; break;
             case 't': code = '\t'; break;
             case 'r': code = '\r'; break;
             case 'f': code = '\f'; break;
             case 'v': code = '\013'; break;
             case 'a': code = '\007'; break;
             case 'e': code = '\033'; break;
 
             /* \OOO */
             case '0': case '1': case '2': case '3':
             case '4': case '5': case '6': case '7':
                 p--;
                 int olen = end < p + 3 ? end - p : 3;
                 code = StringSupport.scanOct(bytes, p, olen);
                 p += StringSupport.octLength(bytes, p, olen);
                 break;
 
             case 'x': /* \xHH */
                 int hlen = end < p + 2 ? end - p : 2;
                 code = StringSupport.scanHex(bytes, p, hlen);
                 int len = StringSupport.hexLength(bytes, p, hlen);
                 if (len < 1) raisePreprocessError(runtime, str, "invalid hex escape", mode);
                 p += len;
                 break;
 
             case 'M': /* \M-X, \M-\C-X, \M-\cX */
                 if (metaPrefix) raisePreprocessError(runtime, str, "duplicate meta escape", mode);
                 metaPrefix = true;
                 if (p + 1 < end && bytes[p++] == (byte)'-' && (bytes[p] & 0x80) == 0) {
                     if (bytes[p] == (byte)'\\') {
                         p++;
                         continue;
                     } else {
                         code = bytes[p++] & 0xff;
                         break;
                     }
                 }
                 raisePreprocessError(runtime, str, "too short meta escape", mode);
 
             case 'C': /* \C-X, \C-\M-X */
                 if (p == end || bytes[p++] != (byte)'-') raisePreprocessError(runtime, str, "too short control escape", mode);
 
             case 'c': /* \cX, \c\M-X */
                 if (ctrlPrefix) raisePreprocessError(runtime, str, "duplicate control escape", mode);
                 ctrlPrefix = true;
                 if (p < end && (bytes[p] & 0x80) == 0) {
                     if (bytes[p] == (byte)'\\') {
                         p++;
                         continue;
                     } else {
                         code = bytes[p++] & 0xff;
                         break;
                     }
                 }
                 raisePreprocessError(runtime, str, "too short control escape", mode);
             default:
                 raisePreprocessError(runtime, str, "unexpected escape sequence", mode);
             } // switch
 
             if (code < 0 || code > 0xff) raisePreprocessError(runtime, str, "invalid escape code", mode);
 
             if (ctrlPrefix) code &= 0x1f;
             if (metaPrefix) code |= 0x80;
 
             to[toP] = (byte)code;
             return p;
         } // while
     }
 
     private static int unescapeEscapedNonAscii(Ruby runtime, ByteList to, byte[]bytes, int p, int end, Encoding enc, Encoding[]encp, ByteList str, ErrorMode mode) {
         byte[]chBuf = new byte[enc.maxLength()];
         int chLen = 0;
 
         p = readEscapedByte(runtime, chBuf, chLen++, bytes, p, end, str, mode);
         while (chLen < enc.maxLength() && StringSupport.preciseLength(enc, chBuf, 0, chLen) < -1) { // MBCLEN_NEEDMORE_P
             p = readEscapedByte(runtime, chBuf, chLen++, bytes, p, end, str, mode);
         }
 
         int cl = StringSupport.preciseLength(enc, chBuf, 0, chLen);
         if (cl == -1) raisePreprocessError(runtime, str, "invalid multibyte escape", mode); // MBCLEN_INVALID_P
 
         if (chLen > 1 || (chBuf[0] & 0x80) != 0) {
             to.append(chBuf, 0, chLen);
 
             if (encp[0] == null) {
                 encp[0] = enc;
             } else if (encp[0] != enc) {
                 raisePreprocessError(runtime, str, "escaped non ASCII character in UTF-8 regexp", mode);
             }
         } else {
             Sprintf.sprintf(runtime, to, "\\x%02X", chBuf[0] & 0xff);
         }
         return p;
     }
 
     private static void checkUnicodeRange(Ruby runtime, int code, ByteList str, ErrorMode mode) {
         // Unicode is can be only 21 bits long, int is enough
         if ((0xd800 <= code && code <= 0xdfff) /* Surrogates */ || 0x10ffff < code) {
             raisePreprocessError(runtime, str, "invalid Unicode range", mode);
         }
     }
 
     private static void appendUtf8(Ruby runtime, ByteList to, int code, Encoding[]enc, ByteList str, ErrorMode mode) {
         checkUnicodeRange(runtime, code, str, mode);
 
         if (code < 0x80) {
             Sprintf.sprintf(runtime, to, "\\x%02X", code);
         } else {
             to.ensure(to.realSize + 6);
             to.realSize += Pack.utf8Decode(runtime, to.bytes, to.begin + to.realSize, code);
             if (enc[0] == null) {
                 enc[0] = UTF8Encoding.INSTANCE;
             } else if (!(enc[0] instanceof UTF8Encoding)) { // do not load the class if not used
                 raisePreprocessError(runtime, str, "UTF-8 character in non UTF-8 regexp", mode);
             }
         }
     }
     
     private static int unescapeUnicodeList(Ruby runtime, ByteList to, byte[]bytes, int p, int end, Encoding[]encp, ByteList str, ErrorMode mode) {
         while (p < end && ASCIIEncoding.INSTANCE.isSpace(bytes[p] & 0xff)) p++;
 
         boolean hasUnicode = false; 
         while (true) {
             int code = StringSupport.scanHex(bytes, p, end - p);
             int len = StringSupport.hexLength(bytes, p, end - p);
             if (len == 0) break;
             if (len > 6) raisePreprocessError(runtime, str, "invalid Unicode range", mode);
             p += len;
             appendUtf8(runtime, to, code, encp, str, mode);
             hasUnicode = true;
             while (p < end && ASCIIEncoding.INSTANCE.isSpace(bytes[p] & 0xff)) p++;
         }
 
         if (!hasUnicode) raisePreprocessError(runtime, str, "invalid Unicode list", mode); 
         return p;
     }
 
     private static int unescapeUnicodeBmp(Ruby runtime, ByteList to, byte[]bytes, int p, int end, Encoding[]encp, ByteList str, ErrorMode mode) {
         if (p + 4 > end) raisePreprocessError(runtime, str, "invalid Unicode escape", mode);
         int code = StringSupport.scanHex(bytes, p, 4);
         int len = StringSupport.hexLength(bytes, p, 4);
         if (len != 4) raisePreprocessError(runtime, str, "invalid Unicode escape", mode);
         appendUtf8(runtime, to, code, encp, str, mode);
         return p + 4;
     }
 
     private static boolean unescapeNonAscii(Ruby runtime, ByteList to, byte[]bytes, int p, int end, Encoding enc, Encoding[]encp, ByteList str, ErrorMode mode) {
         boolean hasProperty = false;
 
         while (p < end) {
             int cl = StringSupport.preciseLength(enc, bytes, p, end);
             if (cl <= 0) raisePreprocessError(runtime, str, "invalid multibyte character", mode);
             if (cl > 1 || (bytes[p] & 0x80) != 0) {
                 to.append(bytes, p, cl);
                 p += cl;
                 if (encp[0] == null) {
                     encp[0] = enc;
                 } else if (encp[0] != enc) {
                     raisePreprocessError(runtime, str, "non ASCII character in UTF-8 regexp", mode);
                 }
                 continue;
             }
             int c;
             switch (c = bytes[p++] & 0xff) {
             case '\\':
                 if (p == end) raisePreprocessError(runtime, str, "too short escape sequence", mode);
 
                 switch (c = bytes[p++] & 0xff) {
                 case '1': case '2': case '3':
                 case '4': case '5': case '6': case '7': /* \O, \OO, \OOO or backref */
                     if (StringSupport.scanOct(bytes, p - 1, end - (p - 1)) <= 0177) {
                         to.append('\\').append(c);
                         break;
                     }
 
                 case '0': /* \0, \0O, \0OO */
                 case 'x': /* \xHH */
                 case 'c': /* \cX, \c\M-X */
                 case 'C': /* \C-X, \C-\M-X */
                 case 'M': /* \M-X, \M-\C-X, \M-\cX */
                     p = unescapeEscapedNonAscii(runtime, to, bytes, p - 2, end, enc, encp, str, mode);
                     break;
 
                 case 'u':
                     if (p == end) raisePreprocessError(runtime, str, "too short escape sequence", mode);
                     if (bytes[p] == (byte)'{') { /* \\u{H HH HHH HHHH HHHHH HHHHHH ...} */
                         p++;
                         p = unescapeUnicodeList(runtime, to, bytes, p, end, encp, str, mode);
                         if (p == end || bytes[p++] != (byte)'}') raisePreprocessError(runtime, str, "invalid Unicode list", mode);
                     } else { /* \\uHHHH */
                         p = unescapeUnicodeBmp(runtime, to, bytes, p, end, encp, str, mode);
                     }
                     break;
                 case 'p': /* \p{Hiragana} */
                     if (encp[0] == null) hasProperty = true;
                     to.append('\\').append(c);
                     break;
 
                 default:
                     to.append('\\').append(c);
                     break;
                 } // inner switch
                 break;
 
             default:
                 to.append(c);
             } // switch
         } // while
         return hasProperty;
     }
 
     private static ByteList preprocess(Ruby runtime, ByteList str, Encoding enc, Encoding[]fixedEnc, ErrorMode mode) {
         ByteList to = new ByteList(str.realSize);
 
         if (enc.isAsciiCompatible()) {
             fixedEnc[0] = null;
         } else {
             fixedEnc[0] = enc;
             to.encoding = enc;
         }
 
         boolean hasProperty = unescapeNonAscii(runtime, to, str.bytes, str.begin, str.begin + str.realSize, enc, fixedEnc, str, mode);
         if (hasProperty && fixedEnc[0] == null) fixedEnc[0] = enc;
         if (fixedEnc[0] != null) to.encoding = fixedEnc[0];
         return to;
     }
 
     public static void preprocessCheck(Ruby runtime, IRubyObject obj) {
         ByteList bytes = obj.convertToString().getByteList();
         preprocess(runtime, bytes, bytes.encoding, new Encoding[]{null}, ErrorMode.RAISE); 
     }
 
     private void check() {
         if (pattern == null) throw getRuntime().newTypeError("uninitialized Regexp");
     }
 
     @JRubyMethod(name = {"new", "compile"}, required = 1, optional = 2, meta = true)
     public static RubyRegexp newInstance(IRubyObject recv, IRubyObject[] args) {
         RubyClass klass = (RubyClass)recv;
 
         RubyRegexp re = (RubyRegexp) klass.allocate();
         re.callInit(args, Block.NULL_BLOCK);
         return re;
     }
 
     @JRubyMethod(name = "try_convert", meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject try_convert(ThreadContext context, IRubyObject recv, IRubyObject args) {
         return TypeConverter.convertToTypeWithCheck(args, context.getRuntime().getRegexp(), "to_regexp");
     }
 
     /** rb_reg_s_quote
      * 
      */
     @JRubyMethod(name = {"quote", "escape"}, required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_8)
     public static RubyString quote(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         final KCode code;
         if (args.length == 1 || args[1].isNil()) {
             code = runtime.getKCode();
         } else {
             code = KCode.create(runtime, args[1].toString());
         }
 
         RubyString src = args[0].convertToString();
         RubyString dst = RubyString.newStringShared(runtime, quote(src.getByteList(), code.getEncoding()));
         dst.infectBy(src);
         return dst;
     }
 
     @JRubyMethod(name = {"quote", "escape"}, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject quote19(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         RubyString str = operandCheck(runtime, arg);
         return RubyString.newStringShared(runtime, quote19(str.getByteList(), str.isAsciiOnly()));
     }
 
     /** rb_reg_quote
      *
      */
     private static ByteList quote(ByteList bs, Encoding enc) {
         int p = bs.begin;
         int end = p + bs.realSize;
         byte[]bytes = bs.bytes;
 
         metaFound: do {
             for(; p < end; p++) {
                 int c = bytes[p] & 0xff;
                 int cl = enc.length(bytes, p, end);
                 if (cl != 1) {
                     while (cl-- > 0 && p < end) p++;
                     p--;
                     continue;
                 }
                 switch (c) {
                 case '[': case ']': case '{': case '}':
                 case '(': case ')': case '|': case '-':
                 case '*': case '.': case '\\':
                 case '?': case '+': case '^': case '$':
                 case ' ': case '#':
                 case '\t': case '\f': case '\n': case '\r':
                     break metaFound;
                 }
             }
             return bs;
         } while (false);
 
         ByteList result = new ByteList(end * 2);
         byte[]obytes = result.bytes;
         int op = p - bs.begin;
         System.arraycopy(bytes, bs.begin, obytes, 0, op);
 
         for(; p < end; p++) {
             int c = bytes[p] & 0xff;
             int cl = enc.length(bytes, p, end);
             if (cl != 1) {
                 while (cl-- > 0 && p < end) obytes[op++] = bytes[p++];
                 p--;
                 continue;
             }
 
             switch (c) {
             case '[': case ']': case '{': case '}':
             case '(': case ')': case '|': case '-':
             case '*': case '.': case '\\':
             case '?': case '+': case '^': case '$':
             case '#': obytes[op++] = '\\'; break;
             case ' ': obytes[op++] = '\\'; obytes[op++] = ' '; continue;
             case '\t':obytes[op++] = '\\'; obytes[op++] = 't'; continue;
             case '\n':obytes[op++] = '\\'; obytes[op++] = 'n'; continue;
             case '\r':obytes[op++] = '\\'; obytes[op++] = 'r'; continue;
             case '\f':obytes[op++] = '\\'; obytes[op++] = 'f'; continue;
             }
             obytes[op++] = (byte)c;
         }
 
         result.realSize = op;
         return result;
     }
 
     static ByteList quote19(ByteList bs, boolean asciiOnly) {
         int p = bs.begin;
         int end = p + bs.realSize;
         byte[]bytes = bs.bytes;
         Encoding enc = bs.encoding;
 
         metaFound: do {
             while (p < end) {
                 final int c;
                 final int cl;
                 if (enc.isAsciiCompatible()) {
                     cl = 1;
                     c = bytes[p] & 0xff;
                 } else {
                     cl = StringSupport.preciseLength(enc, bytes, p, end);
                     c = enc.mbcToCode(bytes, p, end);
                 }
 
                 if (!Encoding.isAscii(c)) {
                     p += StringSupport.length(enc, bytes, p, end);
                     continue;
                 }
                 
                 switch (c) {
                 case '[': case ']': case '{': case '}':
                 case '(': case ')': case '|': case '-':
                 case '*': case '.': case '\\':
                 case '?': case '+': case '^': case '$':
                 case ' ': case '#':
                 case '\t': case '\f': case '\n': case '\r':
                     break metaFound;
                 }
                 p += cl;
             }
             if (asciiOnly) {
                 ByteList tmp = bs.shallowDup();
                 tmp.encoding = USASCIIEncoding.INSTANCE;
                 return tmp;
             }
             return bs;
         } while (false);
 
         ByteList result = new ByteList(end * 2);
         result.encoding = asciiOnly ? USASCIIEncoding.INSTANCE : bs.encoding;
         byte[]obytes = result.bytes;
         int op = p - bs.begin;
         System.arraycopy(bytes, bs.begin, obytes, 0, op);
 
         while (p < end) {
             final int c;
             final int cl;
             if (enc.isAsciiCompatible()) {
                 cl = 1;
                 c = bytes[p] & 0xff;
             } else {
                 cl = StringSupport.preciseLength(enc, bytes, p, end);
                 c = enc.mbcToCode(bytes, p, end);
             }
 
             if (!Encoding.isAscii(c)) {
                 int n = StringSupport.length(enc, bytes, p, end);
                 while (n-- > 0) obytes[op++] = bytes[p++];
                 continue;
             }
             p += cl;
             switch (c) {
             case '[': case ']': case '{': case '}':
             case '(': case ')': case '|': case '-':
             case '*': case '.': case '\\':
             case '?': case '+': case '^': case '$':
             case '#': 
                 op += enc.codeToMbc('\\', obytes, op);
                 break;
             case ' ':
                 op += enc.codeToMbc('\\', obytes, op);
                 op += enc.codeToMbc(' ', obytes, op);
                 continue;
             case '\t':
                 op += enc.codeToMbc('\\', obytes, op);
                 op += enc.codeToMbc('t', obytes, op);
                 continue;
             case '\n':
                 op += enc.codeToMbc('\\', obytes, op);
                 op += enc.codeToMbc('n', obytes, op);
                 continue;
             case '\r':
                 op += enc.codeToMbc('\\', obytes, op);
                 op += enc.codeToMbc('r', obytes, op);
                 continue;
             case '\f':
                 op += enc.codeToMbc('\\', obytes, op);
                 op += enc.codeToMbc('f', obytes, op);
                 continue;
             }
             op += enc.codeToMbc(c, obytes, op);
         }
 
         result.realSize = op;
         return result;
     }
     
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * @deprecated Use the versions with zero, one, or two args.
      */
     public static IRubyObject last_match_s(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         switch (args.length) {
         case 0:
             return last_match_s(context, recv);
         case 1:
             return last_match_s(context, recv, args[0]);
         default:
             Arity.raiseArgumentError(context.getRuntime(), args.length, 0, 1);
             return null; // not reached
         }
     }
 
     /** rb_reg_s_last_match / match_getter
     *
     */
     @JRubyMethod(name = "last_match", meta = true, reads = BACKREF)
     public static IRubyObject last_match_s(ThreadContext context, IRubyObject recv) {
         IRubyObject match = context.getCurrentScope().getBackRef(context.getRuntime());
         if (match instanceof RubyMatchData) ((RubyMatchData)match).use();
         return match;
     }
 
     /** rb_reg_s_last_match
     *
     */
     @JRubyMethod(name = "last_match", meta = true, reads = BACKREF)
     public static IRubyObject last_match_s(ThreadContext context, IRubyObject recv, IRubyObject nth) {
         IRubyObject match = context.getCurrentScope().getBackRef(context.getRuntime());
         if (match.isNil()) return match;
         return nth_match(((RubyMatchData)match).backrefNumber(nth), match);
     }
 
     /** rb_reg_s_union
     *
     */
     @JRubyMethod(name = "union", rest = true, meta = true)
     public static IRubyObject union(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         IRubyObject[] realArgs = args;
         if (args.length == 0) {
             return newRegexp(runtime, ByteList.create("(?!)"), 0);
         } else if (args.length == 1) {
             IRubyObject v = TypeConverter.convertToTypeWithCheck(args[0], runtime.getRegexp(), "to_regexp");
             if (!v.isNil()) {
                 return v;
             } else {
                 IRubyObject a = TypeConverter.convertToTypeWithCheck(args[0], runtime.getArray(), "to_ary");
                 if (!a.isNil()) {
                     RubyArray aa = (RubyArray)a;
                     int len = aa.getLength();
                     realArgs = new IRubyObject[len];
                     for(int i = 0; i<len; i++) {
                         realArgs[i] = aa.entry(i);
                     }
                 } else {
                     // newInstance here
                     return newRegexp(runtime, quote(context, recv, args).getByteList(), 0);
                 }
             }
         }
 
         KCode kcode = null;
         IRubyObject kcode_re = runtime.getNil();
         RubyString source = runtime.newString();
         IRubyObject[] _args = new IRubyObject[3];
 
         for (int i = 0; i < realArgs.length; i++) {
             if (0 < i) source.cat((byte)'|');
             IRubyObject v = TypeConverter.convertToTypeWithCheck(realArgs[i], runtime.getRegexp(), "to_regexp");
             if (!v.isNil()) {
                 if (!((RubyRegexp)v).isKCodeDefault()) {
                     if (kcode == null) {
                         kcode_re = v;
                         kcode = ((RubyRegexp)v).kcode;
                     } else if (((RubyRegexp)v).kcode != kcode) {
                         IRubyObject str1 = kcode_re.inspect();
                         IRubyObject str2 = v.inspect();
                         throw runtime.newArgumentError("mixed kcode " + str1 + " and " + str2);
                     }
                 }
                 v = ((RubyRegexp)v).to_s();
             } else {
                 v = quote(context, recv, new IRubyObject[]{realArgs[i]});
             }
             source.append(v);
         }
 
         _args[0] = source;
         _args[1] = runtime.getNil();
         if (kcode == null) {
             _args[2] = runtime.getNil();
         } else if (kcode == KCode.NONE) {
             _args[2] = runtime.newString("n");
         } else if (kcode == KCode.EUC) {
             _args[2] = runtime.newString("e");
         } else if (kcode == KCode.SJIS) {
             _args[2] = runtime.newString("s");
         } else if (kcode == KCode.UTF8) {
             _args[2] = runtime.newString("u");
         }
         return recv.callMethod(context, "new", _args);
     }
 
     // rb_reg_raise
     private static void raiseRegexpError(Ruby runtime, ByteList bytes, Encoding enc, int flags, String err) {
         throw runtime.newRegexpError(err + ": " + regexpDescription(runtime, bytes, enc, flags));
     }
 
     // rb_reg_desc
     private static ByteList regexpDescription(Ruby runtime, ByteList bytes, Encoding enc, int options) {
         return regexpDescription(runtime, bytes.bytes, bytes.begin, bytes.realSize, enc, options);
     }
     private static ByteList regexpDescription(Ruby runtime, byte[] bytes, int start, int len, Encoding enc, int options) {
         ByteList description = new ByteList();
         description.append((byte)'/');
         appendRegexpString(runtime, description, bytes, start, len, enc);
         description.append((byte)'/');
         appendOptions(description, options);
         return description;
     }
 
     // rb_enc_reg_raise
     private static void raiseRegexpError19(Ruby runtime, ByteList bytes, Encoding enc, int flags, String err) {
         // TODO: we loose encoding information here, fix it
         throw runtime.newRegexpError(err + ": " + regexpDescription19(runtime, bytes, flags, enc));
     }
 
     // rb_enc_reg_error_desc
     static ByteList regexpDescription19(Ruby runtime, ByteList bytes, int options, Encoding enc) {
         return regexpDescription19(runtime, bytes.bytes, bytes.begin, bytes.realSize, options, enc);
     }
     private static ByteList regexpDescription19(Ruby runtime, byte[] s, int start, int len, int options, Encoding enc) {
         ByteList description = new ByteList();
         description.encoding = enc;
         description.append((byte)'/');
         appendRegexpString19(runtime, description, s, start, len, enc);
         description.append((byte)'/');
         appendOptions(description, options);
         return description; 
     }
 
     /** rb_reg_init_copy
      */
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject re) {
         if (this == re) return this;
         checkFrozen();
 
         if (getMetaClass().getRealClass() != re.getMetaClass().getRealClass()) {
             throw getRuntime().newTypeError("wrong argument type");
         }
 
         RubyRegexp regexp = (RubyRegexp)re;
         regexp.check();
 
         return initializeCommon(regexp.str, regexp.getOptions());
     }
 
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public IRubyObject initialize_m(IRubyObject arg) {
         if (arg instanceof RubyRegexp) return initializeByRegexp((RubyRegexp)arg);
         return initializeCommon(arg.convertToString().getByteList(), 0);
     }
 
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public IRubyObject initialize_m(IRubyObject arg0, IRubyObject arg1) {
         if (arg0 instanceof RubyRegexp) {
             getRuntime().getWarnings().warn(ID.REGEXP_IGNORED_FLAGS, "flags ignored");            
             return initializeByRegexp((RubyRegexp)arg0);
         }
         
         int options = arg1 instanceof RubyFixnum ? RubyNumeric.fix2int(arg1) : arg1.isTrue() ? RE_OPTION_IGNORECASE : 0;
         return initializeCommon(arg0.convertToString().getByteList(), options);
     }
 
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public IRubyObject initialize_m(IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         if (arg0 instanceof RubyRegexp) {
             getRuntime().getWarnings().warn(ID.REGEXP_IGNORED_FLAGS, "flags and encoding ignored");            
             return initializeByRegexp((RubyRegexp)arg0);
         }
         int options = arg1 instanceof RubyFixnum ? RubyNumeric.fix2int(arg1) : arg1.isTrue() ? RE_OPTION_IGNORECASE : 0;
 
         if (!arg2.isNil()) {
             ByteList kcodeBytes = arg2.convertToString().getByteList();
             char first = kcodeBytes.length() > 0 ? kcodeBytes.charAt(0) : 0;
             options &= ~0x70;
             switch (first) {
             case 'n': case 'N':
                 options |= 16;
                 break;
             case 'e': case 'E':
                 options |= 32;
                 break;
             case 's': case 'S':
                 options |= 48;
                 break;
             case 'u': case 'U':
                 options |= 64;
                 break;
             default:
                 break;
             }
         }
         return initializeCommon(arg0.convertToString().getByteList(), options);
     }
 
     private IRubyObject initializeByRegexp(RubyRegexp regexp) {
         regexp.check();
 
         int options = regexp.pattern.getOptions();
         if (!regexp.isKCodeDefault() && regexp.kcode != null && regexp.kcode != KCode.NIL) {
             if (regexp.kcode == KCode.NONE) {
                 options |= 16;
             } else if (regexp.kcode == KCode.EUC) {
                 options |= 32;
             } else if (regexp.kcode == KCode.SJIS) {
                 options |= 48;
             } else if (regexp.kcode == KCode.UTF8) {
                 options |= 64;
             }
         }
         return initializeCommon(regexp.str, options);
     }
 
     private RubyRegexp initializeCommon(ByteList bytes, int options) {
         Ruby runtime = getRuntime();        
         if (!isTaint() && runtime.getSafeLevel() >= 4) throw runtime.newSecurityError("Insecure: can't modify regexp");
         checkFrozen();
         if (isLiteral()) throw runtime.newSecurityError("can't modify literal regexp");
         setKCode(runtime, options);
         pattern = getRegexpFromCache(runtime, bytes, kcode.getEncoding(), options & 0xf);
         str = bytes;
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize_m19(IRubyObject arg) {
         if (arg instanceof RubyRegexp) return initializeByRegexp19((RubyRegexp)arg);
         return initializeCommon19(arg.convertToString(), 0);
     }
 
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize_m19(IRubyObject arg0, IRubyObject arg1) {
         if (arg0 instanceof RubyRegexp) {
             getRuntime().getWarnings().warn(ID.REGEXP_IGNORED_FLAGS, "flags ignored");            
             return initializeByRegexp19((RubyRegexp)arg0);
         }
         
         int options = arg1 instanceof RubyFixnum ? RubyNumeric.fix2int(arg1) : arg1.isTrue() ? RE_OPTION_IGNORECASE : 0;
         return initializeCommon19(arg0.convertToString(), options);
     }
 
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize_m19(IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         if (arg0 instanceof RubyRegexp) {
             getRuntime().getWarnings().warn(ID.REGEXP_IGNORED_FLAGS, "flags ignored");            
             return initializeByRegexp19((RubyRegexp)arg0);
         }
         int options = arg1 instanceof RubyFixnum ? RubyNumeric.fix2int(arg1) : arg1.isTrue() ? RE_OPTION_IGNORECASE : 0;
 
         if (!arg2.isNil()) {
             ByteList kcodeBytes = arg2.convertToString().getByteList();
             if ((kcodeBytes.realSize > 0 && kcodeBytes.bytes[kcodeBytes.begin] == 'n') ||
                 (kcodeBytes.realSize > 1 && kcodeBytes.bytes[kcodeBytes.begin + 1] == 'N')) {
                 return initializeCommon19(arg0.convertToString().getByteList(), ASCIIEncoding.INSTANCE, options | ARG_ENCODING_NONE);
             } else {
                 getRuntime().getWarnings().warn("encoding option is ignored - " + kcodeBytes);
             }
         }
         return initializeCommon19(arg0.convertToString(), options);
     }
 
     private IRubyObject initializeByRegexp19(RubyRegexp regexp) {
         regexp.check();
         return initializeCommon19(regexp.str, regexp.getEncoding(), regexp.pattern.getOptions());
     }
 
     // rb_reg_initialize_str
     private RubyRegexp initializeCommon19(RubyString str, int options) {
         ByteList bytes = str.getByteList();
         Encoding enc = bytes.encoding;
         if ((options & REGEXP_ENCODING_NONE) != 0) {
             if (enc != ASCIIEncoding.INSTANCE) {
                 if (str.scanForCodeRange() != StringSupport.CR_7BIT) {
                     raiseRegexpError19(getRuntime(), bytes, enc, options, "/.../n has a non escaped non ASCII character in non ASCII-8BIT script");
                 }
                 enc = ASCIIEncoding.INSTANCE;
             }
         }
         return initializeCommon19(bytes, enc, options);
     }
 
     // rb_reg_initialize
     private RubyRegexp initializeCommon19(ByteList bytes, Encoding enc, int options) {
         Ruby runtime = getRuntime();        
         setKCode(runtime, options);
         if (!isTaint() && runtime.getSafeLevel() >= 4) throw runtime.newSecurityError("Insecure: can't modify regexp");
         checkFrozen();
         if (isLiteral()) throw runtime.newSecurityError("can't modify literal regexp");
         if (pattern != null) throw runtime.newTypeError("already initialized regexp");
         if (enc.isDummy()) raiseRegexpError19(runtime, bytes, enc, options, "can't make regexp with dummy encoding");
         
         Encoding[]fixedEnc = new Encoding[]{null};
         ByteList unescaped = preprocess(runtime, bytes, enc, fixedEnc, ErrorMode.RAISE);
         if (fixedEnc[0] != null) {
             if ((fixedEnc[0] != enc && (options & ARG_ENCODING_FIXED) != 0) ||
                (fixedEnc[0] != ASCIIEncoding.INSTANCE && (options & ARG_ENCODING_NONE) != 0)) {
                    raiseRegexpError19(runtime, bytes, enc, options, "incompatible character encoding");
             }
             if (fixedEnc[0] != ASCIIEncoding.INSTANCE) {
                 options |= ARG_ENCODING_FIXED;
                 enc = fixedEnc[0];
             }
         } else if ((options & ARG_ENCODING_FIXED) == 0) {
             enc = USASCIIEncoding.INSTANCE;
         }
 
         if ((options & ARG_ENCODING_FIXED) == 0 && fixedEnc[0] == null) setKCodeDefault();
         if ((options & ARG_ENCODING_NONE) != 0) setEncodingNone();
         pattern = getRegexpFromCache(runtime, unescaped, enc, options & ARG_OPTION_MASK);
         str = bytes;
         return this;
     }
 
     @JRubyMethod(name = "kcode")
     public IRubyObject kcode(ThreadContext context) {
         return (!isKCodeDefault() && kcode != null) ? 
             context.getRuntime().newString(kcode.name()) : context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
         check();
         int hash = pattern.getOptions();
         int len = str.realSize;
         int p = str.begin;
         byte[]bytes = str.bytes;
         while (len-- > 0) {
             hash = hash * 33 + bytes[p++];
         }
         return getRuntime().newFixnum(hash + (hash >> 5));
     }
 
     @JRubyMethod(name = {"==", "eql?"}, required = 1)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         if (this == other) return context.getRuntime().getTrue();
         if (!(other instanceof RubyRegexp)) return context.getRuntime().getFalse();
         RubyRegexp otherRegex = (RubyRegexp)other;
         
         check();
         otherRegex.check();
         
         return context.getRuntime().newBoolean(str.equal(otherRegex.str) && 
                 kcode == otherRegex.kcode && pattern.getOptions() == otherRegex.pattern.getOptions());
     }
 
     @JRubyMethod(name = "~", reads = {LASTLINE, BACKREF}, writes = BACKREF)
     public IRubyObject op_match2(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         IRubyObject line = context.getCurrentScope().getLastLine(runtime);
         if (line instanceof RubyString) {
             int start = search(context, (RubyString)line, 0, false);
             if (start < 0) return runtime.getNil();
             return runtime.newFixnum(start);
         }
         context.getCurrentScope().setBackRef(runtime.getNil());
         return runtime.getNil();
     }
 
     /** rb_reg_eqq
      * 
      */
     @JRubyMethod(name = "===", required = 1, writes = BACKREF, compat = CompatVersion.RUBY1_8)
     public IRubyObject eqq(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         final RubyString str;
         if (arg instanceof RubyString) {
             str = (RubyString)arg;
diff --git a/src/org/jruby/ast/executable/AbstractScript.java b/src/org/jruby/ast/executable/AbstractScript.java
index 257b32fbc2..e48db73ddc 100644
--- a/src/org/jruby/ast/executable/AbstractScript.java
+++ b/src/org/jruby/ast/executable/AbstractScript.java
@@ -1,702 +1,703 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.ast.executable;
 
 import java.math.BigInteger;
 import java.util.Arrays;
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.RubyClass.VariableAccessor;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.CacheEntry;
 import org.jruby.util.ByteList;
 
 /**
  *
  * @author headius
  */
 public abstract class AbstractScript implements Script {
     public AbstractScript() {
     }
 
     public IRubyObject __file__(ThreadContext context, IRubyObject self, Block block) {
         return __file__(context, self, IRubyObject.NULL_ARRAY, block);
     }
     
     public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg, Block block) {
         return __file__(context, self, new IRubyObject[] {arg}, block);
     }
     
     public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2, Block block) {
         return __file__(context, self, new IRubyObject[] {arg1, arg2}, block);
     }
     
     public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
         return __file__(context, self, new IRubyObject[] {arg1, arg2, arg3}, block);
     }
     
     public IRubyObject load(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         return null;
     }
     
     public IRubyObject run(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         return __file__(context, self, args, block);
     }
 
     public static class RuntimeCache {
         public RuntimeCache() {
             methodCache = new CacheEntry[100];
             Arrays.fill(methodCache, CacheEntry.NULL_CACHE);
         }
         
         public final StaticScope getScope(ThreadContext context, String varNamesDescriptor, int index) {
             StaticScope scope = scopes[index];
             if (scope == null) {
                 String[] varNames = varNamesDescriptor.split(";");
                 for (int i = 0; i < varNames.length; i++) varNames[i] = varNames[i].intern();
                 scope = scopes[index] = new LocalStaticScope(context.getCurrentScope().getStaticScope(), varNames);
             }
             return scope;
         }
 
         public final CallSite getCallSite(int index) {
             return callSites[index];
         }
 
         /**
          * descriptor format is
          *
          * closure_method_name,arity,varname1;varname2;varname3,has_multi_args_head,arg_type,light
          *
          * @param context
          * @param index
          * @param descriptor
          * @return
          */
         public final BlockBody getBlockBody(Object scriptObject, ThreadContext context, int index, String descriptor) {
             BlockBody body = blockBodies[index];
 
             if (body == null) {
                 return createBlockBody(scriptObject, context, index, descriptor);
             }
 
             return body;
         }
 
         /**
          * descriptor format is
          *
          * closure_method_name,arity,varname1;varname2;varname3,has_multi_args_head,arg_type,light
          *
          * @param context
          * @param index
          * @param descriptor
          * @return
          */
         public final BlockBody getBlockBody19(Object scriptObject, ThreadContext context, int index, String descriptor) {
             BlockBody body = blockBodies[index];
 
             if (body == null) {
                 return createBlockBody19(scriptObject, context, index, descriptor);
             }
 
             return body;
         }
 
         public final CompiledBlockCallback getBlockCallback(Object scriptObject, Ruby runtime, int index, String method) {
             CompiledBlockCallback callback = blockCallbacks[index];
 
             if (callback == null) {
                 return createCompiledBlockCallback(scriptObject, runtime, index, method);
             }
 
             return callback;
         }
 
         public final RubySymbol getSymbol(Ruby runtime, int index, String name) {
             RubySymbol symbol = symbols[index];
             if (symbol == null) return symbols[index] = runtime.newSymbol(name);
             return symbol;
         }
 
         public final RubyString getString(Ruby runtime, int index) {
             return RubyString.newStringShared(runtime, byteLists[index]);
         }
 
         public final RubyFixnum getFixnum(Ruby runtime, int index, int value) {
             RubyFixnum fixnum = fixnums[index];
             if (fixnum == null) return fixnums[index] = RubyFixnum.newFixnum(runtime, value);
             return fixnum;
         }
 
         public final RubyFixnum getFixnum(Ruby runtime, int index, long value) {
             RubyFixnum fixnum = fixnums[index];
             if (fixnum == null) return fixnums[index] = RubyFixnum.newFixnum(runtime, value);
             return fixnum;
         }
 
         public final RubyRegexp getRegexp(Ruby runtime, int index, String pattern, int options) {
             RubyRegexp regexp = regexps[index];
             if (regexp == null) {
                 regexp = RubyRegexp.newRegexp(runtime, pattern, options);
                 regexp.setLiteral();
                 regexps[index] = regexp;
             }
             return regexp;
         }
 
         public final RubyRegexp getRegexp(int index) {
             return regexps[index];
         }
 
-        public final void cacheRegexp(Ruby runtime, int index, ByteList pattern, int options) {
+        public final RubyRegexp cacheRegexp(int index, RubyString pattern, int options) {
             RubyRegexp regexp = regexps[index];
             if (regexp == null) {
-                regexp = RubyRegexp.newRegexp(runtime, pattern, options);
+                regexp = RubyRegexp.newRegexp(pattern.getRuntime(), pattern.getByteList(), options);
                 regexps[index] = regexp;
             }
+            return regexp;
         }
 
         public final BigInteger getBigInteger(Ruby runtime, int index, String pattern) {
             BigInteger bigint = bigIntegers[index];
             if (bigint == null) return bigIntegers[index] = new BigInteger(pattern, 16);
             return bigint;
         }
 
         public final IRubyObject getVariable(Ruby runtime, int index, String name, IRubyObject object) {
             VariableAccessor variableAccessor = variableReaders[index];
             RubyClass cls = object.getMetaClass().getRealClass();
             if (variableAccessor.getClassId() != cls.hashCode()) {
                 variableReaders[index] = variableAccessor = cls.getVariableAccessorForRead(name);
             }
             IRubyObject value = (IRubyObject)variableAccessor.get(object);
             if (value != null) return value;
             if (runtime.isVerbose()) warnAboutUninitializedIvar(runtime, name);
             return runtime.getNil();
         }
 
         private void warnAboutUninitializedIvar(Ruby runtime, String name) {
             runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + name + " not initialized");
         }
 
         public final IRubyObject setVariable(Ruby runtime, int index, String name, IRubyObject object, IRubyObject value) {
             VariableAccessor variableAccessor = variableWriters[index];
             RubyClass cls = object.getMetaClass().getRealClass();
             if (variableAccessor.getClassId() != cls.hashCode()) {
                 variableWriters[index] = variableAccessor = cls.getVariableAccessorForWrite(name);
             }
             variableAccessor.set(object, value);
             return value;
         }
 
         public final void initScopes(int size) {
             scopes = new StaticScope[size];
         }
 
         public final void initCallSites(int size) {
             callSites = new CallSite[size];
         }
 
         public final void initBlockBodies(int size) {
             blockBodies = new BlockBody[size];
         }
 
         public final void initBlockCallbacks(int size) {
             blockCallbacks = new CompiledBlockCallback[size];
         }
 
         public final void initSymbols(int size) {
             symbols = new RubySymbol[size];
         }
 
         public final ByteList[] initStrings(int size) {
             return byteLists = new ByteList[size];
         }
 
         public final void initFixnums(int size) {
             fixnums = new RubyFixnum[size];
         }
 
         public final void initRegexps(int size) {
             regexps = new RubyRegexp[size];
         }
 
         public final void initBigIntegers(int size) {
             bigIntegers = new BigInteger[size];
         }
 
         public final void initConstants(int size) {
             constants = new IRubyObject[size];
             constantTargetHashes = new int[size];
             constantGenerations = new int[size];
             Arrays.fill(constantGenerations, -1);
             Arrays.fill(constantTargetHashes, -1);
         }
 
         public final void initVariableReaders(int size) {
             variableReaders = new VariableAccessor[size];
             Arrays.fill(variableReaders, VariableAccessor.DUMMY_ACCESSOR);
         }
 
         public final void initVariableWriters(int size) {
             variableWriters = new VariableAccessor[size];
             Arrays.fill(variableWriters, VariableAccessor.DUMMY_ACCESSOR);
         }
 
         public final void initMethodCache(int size) {
             methodCache = new CacheEntry[size];
             Arrays.fill(methodCache, CacheEntry.NULL_CACHE);
         }
 
         public final IRubyObject getConstant(ThreadContext context, String name, int index) {
             IRubyObject value = getValue(context, name, index);
 
             // We can callsite cache const_missing if we want
             return value != null ? value :
                 context.getCurrentScope().getStaticScope().getModule().callMethod(context, "const_missing", context.getRuntime().fastNewSymbol(name));
         }
 
         public IRubyObject getValue(ThreadContext context, String name, int index) {
             IRubyObject value = constants[index]; // Store to temp so it does null out on us mid-stream
 
             return isCached(context, value, index) ? value : reCache(context, name, index);
         }
 
         private boolean isCached(ThreadContext context, IRubyObject value, int index) {
             return value != null && constantGenerations[index] == context.getRuntime().getConstantGeneration();
         }
 
         public IRubyObject reCache(ThreadContext context, String name, int index) {
             int newGeneration = context.getRuntime().getConstantGeneration();
             IRubyObject value = context.getConstant(name);
 
             constants[index] = value;
 
             if (value != null) constantGenerations[index] = newGeneration;
 
             return value;
         }
 
         public final IRubyObject getConstantFrom(RubyModule target, ThreadContext context, String name, int index) {
             IRubyObject value = getValueFrom(target, context, name, index);
 
             // We can callsite cache const_missing if we want
             return value != null ? value : target.fastGetConstantFromConstMissing(name);
         }
 
         public IRubyObject getValueFrom(RubyModule target, ThreadContext context, String name, int index) {
             IRubyObject value = constants[index]; // Store to temp so it does null out on us mid-stream
 
             return isCachedFrom(target, context, value, index) ? value : reCacheFrom(target, context, name, index);
         }
 
         private boolean isCachedFrom(RubyModule target, ThreadContext context, IRubyObject value, int index) {
             return
                     value != null &&
                     constantGenerations[index] == context.getRuntime().getConstantGeneration() &&
                     constantTargetHashes[index] == target.hashCode();
         }
 
         public IRubyObject reCacheFrom(RubyModule target, ThreadContext context, String name, int index) {
             int newGeneration = context.getRuntime().getConstantGeneration();
             IRubyObject value = target.fastGetConstantFromNoConstMissing(name);
 
             constants[index] = value;
 
             if (value != null) {
                 constantGenerations[index] = newGeneration;
                 constantTargetHashes[index] = target.hashCode();
             }
 
             return value;
         }
 
         private BlockBody createBlockBody(Object scriptObject, ThreadContext context, int index, String descriptor) throws NumberFormatException {
             String[] firstSplit = descriptor.split(",");
             String[] secondSplit;
 
             if (firstSplit[2].length() == 0) {
                 secondSplit = new String[0];
             } else {
                 secondSplit = firstSplit[2].split(";");
 
                 // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
                 for (int i = 0; i < secondSplit.length; i++) {
                     secondSplit[i] = secondSplit[i].intern();
                 }
             }
 
             BlockBody body = RuntimeHelpers.createCompiledBlockBody(
                     context,
                     scriptObject,
                     firstSplit[0],
                     Integer.parseInt(firstSplit[1]),
                     secondSplit,
                     Boolean.valueOf(firstSplit[3]),
                     Integer.parseInt(firstSplit[4]),
                     Boolean.valueOf(firstSplit[5]));
             return blockBodies[index] = body;
         }
 
         private BlockBody createBlockBody19(Object scriptObject, ThreadContext context, int index, String descriptor) throws NumberFormatException {
             String[] firstSplit = descriptor.split(",");
             String[] secondSplit;
 
             if (firstSplit[2].length() == 0) {
                 secondSplit = new String[0];
             } else {
                 secondSplit = firstSplit[2].split(";");
 
                 // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
                 for (int i = 0; i < secondSplit.length; i++) {
                     secondSplit[i] = secondSplit[i].intern();
                 }
             }
 
             BlockBody body = RuntimeHelpers.createCompiledBlockBody19(
                     context,
                     scriptObject,
                     firstSplit[0],
                     Integer.parseInt(firstSplit[1]),
                     secondSplit,
                     Boolean.valueOf(firstSplit[3]),
                     Integer.parseInt(firstSplit[4]),
                     Boolean.valueOf(firstSplit[5]));
             return blockBodies[index] = body;
         }
 
         private CompiledBlockCallback createCompiledBlockCallback(Object scriptObject, Ruby runtime, int index, String method) {
             CompiledBlockCallback callback = RuntimeHelpers.createBlockCallback(runtime, scriptObject, method);
             return blockCallbacks[index] = callback;
         }
 
         protected DynamicMethod getMethod(ThreadContext context, IRubyObject self, int index, String methodName) {
             RubyClass selfType = pollAndGetClass(context, self);
             CacheEntry myCache = getCacheEntry(index);
             if (myCache.typeOk(selfType)) {
                 return myCache.method;
             }
             return cacheAndGet(context, selfType, index, methodName);
         }
 
         private DynamicMethod cacheAndGet(ThreadContext context, RubyClass selfType, int index, String methodName) {
             CacheEntry entry = selfType.searchWithCache(methodName);
             DynamicMethod method = entry.method;
             if (method.isUndefined()) {
                 return RuntimeHelpers.selectMethodMissing(context, selfType, method.getVisibility(), methodName, CallType.FUNCTIONAL);
             }
             methodCache[index] = entry;
             return method;
         }
 
         private CacheEntry getCacheEntry(int index) {
             return methodCache[index];
         }
 
         public StaticScope[] scopes;
         public CallSite[] callSites;
         public CacheEntry[] methodCache;
         public BlockBody[] blockBodies;
         public CompiledBlockCallback[] blockCallbacks;
         public RubySymbol[] symbols;
         public ByteList[] byteLists;
         public RubyFixnum[] fixnums;
         public RubyRegexp[] regexps;
         public BigInteger[] bigIntegers;
         public VariableAccessor[] variableReaders;
         public VariableAccessor[] variableWriters;
         public IRubyObject[] constants;
         public int[] constantGenerations;
         public int[] constantTargetHashes;
     }
 
     public RuntimeCache runtimeCache;
 
     public static final int NUMBERED_SCOPE_COUNT = 10;
 
     public final StaticScope getScope(ThreadContext context, String varNamesDescriptor, int i) {return runtimeCache.getScope(context, varNamesDescriptor, i);}
     public final StaticScope getScope0(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 0);}
     public final StaticScope getScope1(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 1);}
     public final StaticScope getScope2(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 2);}
     public final StaticScope getScope3(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 3);}
     public final StaticScope getScope4(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 4);}
     public final StaticScope getScope5(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 5);}
     public final StaticScope getScope6(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 6);}
     public final StaticScope getScope7(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 7);}
     public final StaticScope getScope8(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 8);}
     public final StaticScope getScope9(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 9);}
 
     public static final int NUMBERED_CALLSITE_COUNT = 10;
 
     public final CallSite getCallSite(int i) {return runtimeCache.callSites[i];}
     public final CallSite getCallSite0() {return runtimeCache.callSites[0];}
     public final CallSite getCallSite1() {return runtimeCache.callSites[1];}
     public final CallSite getCallSite2() {return runtimeCache.callSites[2];}
     public final CallSite getCallSite3() {return runtimeCache.callSites[3];}
     public final CallSite getCallSite4() {return runtimeCache.callSites[4];}
     public final CallSite getCallSite5() {return runtimeCache.callSites[5];}
     public final CallSite getCallSite6() {return runtimeCache.callSites[6];}
     public final CallSite getCallSite7() {return runtimeCache.callSites[7];}
     public final CallSite getCallSite8() {return runtimeCache.callSites[8];}
     public final CallSite getCallSite9() {return runtimeCache.callSites[9];}
 
     public static final int NUMBERED_BLOCKBODY_COUNT = 10;
 
     public final BlockBody getBlockBody(ThreadContext context, int i, String descriptor) {return runtimeCache.getBlockBody(this, context, i, descriptor);}
     public final BlockBody getBlockBody0(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 0, descriptor);}
     public final BlockBody getBlockBody1(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 1, descriptor);}
     public final BlockBody getBlockBody2(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 2, descriptor);}
     public final BlockBody getBlockBody3(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 3, descriptor);}
     public final BlockBody getBlockBody4(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 4, descriptor);}
     public final BlockBody getBlockBody5(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 5, descriptor);}
     public final BlockBody getBlockBody6(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 6, descriptor);}
     public final BlockBody getBlockBody7(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 7, descriptor);}
     public final BlockBody getBlockBody8(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 8, descriptor);}
     public final BlockBody getBlockBody9(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 9, descriptor);}
 
     public final BlockBody getBlockBody19(ThreadContext context, int i, String descriptor) {return runtimeCache.getBlockBody19(this, context, i, descriptor);}
     public final BlockBody getBlockBody190(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 0, descriptor);}
     public final BlockBody getBlockBody191(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 1, descriptor);}
     public final BlockBody getBlockBody192(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 2, descriptor);}
     public final BlockBody getBlockBody193(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 3, descriptor);}
     public final BlockBody getBlockBody194(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 4, descriptor);}
     public final BlockBody getBlockBody195(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 5, descriptor);}
     public final BlockBody getBlockBody196(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 6, descriptor);}
     public final BlockBody getBlockBody197(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 7, descriptor);}
     public final BlockBody getBlockBody198(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 8, descriptor);}
     public final BlockBody getBlockBody199(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 9, descriptor);}
 
     public static final int NUMBERED_BLOCKCALLBACK_COUNT = 10;
 
     public final CompiledBlockCallback getBlockCallback(Ruby runtime, int i, String method) {return runtimeCache.getBlockCallback(this, runtime, i, method);}
     public final CompiledBlockCallback getBlockCallback0(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 0, method);}
     public final CompiledBlockCallback getBlockCallback1(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 1, method);}
     public final CompiledBlockCallback getBlockCallback2(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 2, method);}
     public final CompiledBlockCallback getBlockCallback3(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 3, method);}
     public final CompiledBlockCallback getBlockCallback4(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 4, method);}
     public final CompiledBlockCallback getBlockCallback5(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 5, method);}
     public final CompiledBlockCallback getBlockCallback6(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 6, method);}
     public final CompiledBlockCallback getBlockCallback7(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 7, method);}
     public final CompiledBlockCallback getBlockCallback8(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 8, method);}
     public final CompiledBlockCallback getBlockCallback9(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 9, method);}
 
     public static final int NUMBERED_SYMBOL_COUNT = 10;
 
     public final RubySymbol getSymbol(Ruby runtime, int i, String name) {return runtimeCache.getSymbol(runtime, i, name);}
     public final RubySymbol getSymbol0(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 0, name);}
     public final RubySymbol getSymbol1(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 1, name);}
     public final RubySymbol getSymbol2(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 2, name);}
     public final RubySymbol getSymbol3(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 3, name);}
     public final RubySymbol getSymbol4(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 4, name);}
     public final RubySymbol getSymbol5(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 5, name);}
     public final RubySymbol getSymbol6(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 6, name);}
     public final RubySymbol getSymbol7(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 7, name);}
     public final RubySymbol getSymbol8(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 8, name);}
     public final RubySymbol getSymbol9(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 9, name);}
 
     public static final int NUMBERED_STRING_COUNT = 10;
 
     public final RubyString getString(Ruby runtime, int i) {return runtimeCache.getString(runtime, i);}
     public final RubyString getString0(Ruby runtime) {return runtimeCache.getString(runtime, 0);}
     public final RubyString getString1(Ruby runtime) {return runtimeCache.getString(runtime, 1);}
     public final RubyString getString2(Ruby runtime) {return runtimeCache.getString(runtime, 2);}
     public final RubyString getString3(Ruby runtime) {return runtimeCache.getString(runtime, 3);}
     public final RubyString getString4(Ruby runtime) {return runtimeCache.getString(runtime, 4);}
     public final RubyString getString5(Ruby runtime) {return runtimeCache.getString(runtime, 5);}
     public final RubyString getString6(Ruby runtime) {return runtimeCache.getString(runtime, 6);}
     public final RubyString getString7(Ruby runtime) {return runtimeCache.getString(runtime, 7);}
     public final RubyString getString8(Ruby runtime) {return runtimeCache.getString(runtime, 8);}
     public final RubyString getString9(Ruby runtime) {return runtimeCache.getString(runtime, 9);}
 
     public static final int NUMBERED_FIXNUM_COUNT = 10;
 
     public final RubyFixnum getFixnum(Ruby runtime, int i, int value) {return runtimeCache.getFixnum(runtime, i, value);}
     public final RubyFixnum getFixnum(Ruby runtime, int i, long value) {return runtimeCache.getFixnum(runtime, i, value);}
     public final RubyFixnum getFixnum0(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 0, value);}
     public final RubyFixnum getFixnum1(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 1, value);}
     public final RubyFixnum getFixnum2(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 2, value);}
     public final RubyFixnum getFixnum3(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 3, value);}
     public final RubyFixnum getFixnum4(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 4, value);}
     public final RubyFixnum getFixnum5(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 5, value);}
     public final RubyFixnum getFixnum6(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 6, value);}
     public final RubyFixnum getFixnum7(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 7, value);}
     public final RubyFixnum getFixnum8(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 8, value);}
     public final RubyFixnum getFixnum9(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 9, value);}
 
     public static final int NUMBERED_REGEXP_COUNT = 10;
 
     public final RubyRegexp getRegexp(Ruby runtime, int i, String name, int options) {return runtimeCache.getRegexp(runtime, i, name, options);}
     public final RubyRegexp getRegexp0(Ruby runtime, String name, int options) {return runtimeCache.getRegexp(runtime, 0, name, options);}
     public final RubyRegexp getRegexp1(Ruby runtime, String name, int options) {return runtimeCache.getRegexp(runtime, 1, name, options);}
     public final RubyRegexp getRegexp2(Ruby runtime, String name, int options) {return runtimeCache.getRegexp(runtime, 2, name, options);}
     public final RubyRegexp getRegexp3(Ruby runtime, String name, int options) {return runtimeCache.getRegexp(runtime, 3, name, options);}
     public final RubyRegexp getRegexp4(Ruby runtime, String name, int options) {return runtimeCache.getRegexp(runtime, 4, name, options);}
     public final RubyRegexp getRegexp5(Ruby runtime, String name, int options) {return runtimeCache.getRegexp(runtime, 5, name, options);}
     public final RubyRegexp getRegexp6(Ruby runtime, String name, int options) {return runtimeCache.getRegexp(runtime, 6, name, options);}
     public final RubyRegexp getRegexp7(Ruby runtime, String name, int options) {return runtimeCache.getRegexp(runtime, 7, name, options);}
     public final RubyRegexp getRegexp8(Ruby runtime, String name, int options) {return runtimeCache.getRegexp(runtime, 8, name, options);}
     public final RubyRegexp getRegexp9(Ruby runtime, String name, int options) {return runtimeCache.getRegexp(runtime, 9, name, options);}
 
     public static final int NUMBERED_BIGINTEGER_COUNT = 10;
 
     public final BigInteger getBigInteger(Ruby runtime, int i, String name) {return runtimeCache.getBigInteger(runtime, i, name);}
     public final BigInteger getBigInteger0(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 0, name);}
     public final BigInteger getBigInteger1(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 1, name);}
     public final BigInteger getBigInteger2(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 2, name);}
     public final BigInteger getBigInteger3(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 3, name);}
     public final BigInteger getBigInteger4(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 4, name);}
     public final BigInteger getBigInteger5(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 5, name);}
     public final BigInteger getBigInteger6(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 6, name);}
     public final BigInteger getBigInteger7(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 7, name);}
     public final BigInteger getBigInteger8(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 8, name);}
     public final BigInteger getBigInteger9(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 9, name);}
 
     public static final int NUMBERED_VARIABLEREADER_COUNT = 10;
 
     public final IRubyObject getVariable(Ruby runtime, int i, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, i, name, object);}
     public final IRubyObject getVariable0(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 0, name, object);}
     public final IRubyObject getVariable1(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 1, name, object);}
     public final IRubyObject getVariable2(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 2, name, object);}
     public final IRubyObject getVariable3(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 3, name, object);}
     public final IRubyObject getVariable4(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 4, name, object);}
     public final IRubyObject getVariable5(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 5, name, object);}
     public final IRubyObject getVariable6(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 6, name, object);}
     public final IRubyObject getVariable7(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 7, name, object);}
     public final IRubyObject getVariable8(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 8, name, object);}
     public final IRubyObject getVariable9(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 9, name, object);}
 
     public static final int NUMBERED_VARIABLEWRITER_COUNT = 10;
 
     public final IRubyObject setVariable(Ruby runtime, int i, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, i, name, object, value);}
     public final IRubyObject setVariable0(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 0, name, object, value);}
     public final IRubyObject setVariable1(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 1, name, object, value);}
     public final IRubyObject setVariable2(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 2, name, object, value);}
     public final IRubyObject setVariable3(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 3, name, object, value);}
     public final IRubyObject setVariable4(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 4, name, object, value);}
     public final IRubyObject setVariable5(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 5, name, object, value);}
     public final IRubyObject setVariable6(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 6, name, object, value);}
     public final IRubyObject setVariable7(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 7, name, object, value);}
     public final IRubyObject setVariable8(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 8, name, object, value);}
     public final IRubyObject setVariable9(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 9, name, object, value);}
 
     public static final int NUMBERED_CONSTANT_COUNT = 10;
 
     public final IRubyObject getConstant(ThreadContext context, String name, int i) {return runtimeCache.getConstant(context, name, i);}
     public final IRubyObject getConstant0(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 0);}
     public final IRubyObject getConstant1(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 1);}
     public final IRubyObject getConstant2(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 2);}
     public final IRubyObject getConstant3(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 3);}
     public final IRubyObject getConstant4(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 4);}
     public final IRubyObject getConstant5(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 5);}
     public final IRubyObject getConstant6(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 6);}
     public final IRubyObject getConstant7(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 7);}
     public final IRubyObject getConstant8(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 8);}
     public final IRubyObject getConstant9(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 9);}
 
     public static final int NUMBERED_CONSTANTFROM_COUNT = 10;
 
     public final IRubyObject getConstantFrom(RubyModule target, ThreadContext context, String name, int i) {return runtimeCache.getConstantFrom(target, context, name, i);}
     public final IRubyObject getConstantFrom0(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 0);}
     public final IRubyObject getConstantFrom1(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 1);}
     public final IRubyObject getConstantFrom2(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 2);}
     public final IRubyObject getConstantFrom3(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 3);}
     public final IRubyObject getConstantFrom4(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 4);}
     public final IRubyObject getConstantFrom5(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 5);}
     public final IRubyObject getConstantFrom6(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 6);}
     public final IRubyObject getConstantFrom7(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 7);}
     public final IRubyObject getConstantFrom8(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 8);}
     public final IRubyObject getConstantFrom9(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 9);}
 
     public static final int NUMBERED_METHOD_COUNT = 10;
 
     protected DynamicMethod getMethod(ThreadContext context, IRubyObject self, int i, String methodName) {
         return runtimeCache. getMethod(context, self, i, methodName);
     }
     protected DynamicMethod getMethod0(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 0, methodName);
     }
     protected DynamicMethod getMethod1(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 1, methodName);
     }
     protected DynamicMethod getMethod2(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 2, methodName);
     }
     protected DynamicMethod getMethod3(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 3, methodName);
     }
     protected DynamicMethod getMethod4(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 4, methodName);
     }
     protected DynamicMethod getMethod5(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 5, methodName);
     }
     protected DynamicMethod getMethod6(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 6, methodName);
     }
     protected DynamicMethod getMethod7(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 7, methodName);
     }
     protected DynamicMethod getMethod8(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 8, methodName);
     }
     protected DynamicMethod getMethod9(ThreadContext context, IRubyObject self, String methodName) {
         return runtimeCache. getMethod(context, self, 9, methodName);
     }
 
     public static ByteList[] createByteList(ByteList[] byteLists, int index, String str) {
         byteLists[index] = ByteList.create(str);
         return byteLists;
     }
 
     private static RubyClass pollAndGetClass(ThreadContext context, IRubyObject self) {
         context.callThreadPoll();
         RubyClass selfType = self.getMetaClass();
         return selfType;
     }
 
     public static CallSite[] setCallSite(CallSite[] callSites, int index, String name) {
         callSites[index] = MethodIndex.getCallSite(name);
         return callSites;
     }
 
     public static CallSite[] setFunctionalCallSite(CallSite[] callSites, int index, String name) {
         callSites[index] = MethodIndex.getFunctionalCallSite(name);
         return callSites;
     }
 
     public static CallSite[] setVariableCallSite(CallSite[] callSites, int index, String name) {
         callSites[index] = MethodIndex.getVariableCallSite(name);
         return callSites;
     }
 
     public static CallSite[] setSuperCallSite(CallSite[] callSites, int index) {
         callSites[index] = MethodIndex.getSuperCallSite();
         return callSites;
     }
 
     public final void setFilename(String filename) {
         this.filename = filename;
     }
 
     public String filename;
 }
diff --git a/src/org/jruby/compiler/impl/InheritedCacheCompiler.java b/src/org/jruby/compiler/impl/InheritedCacheCompiler.java
index e5e9336160..08dbe8be92 100644
--- a/src/org/jruby/compiler/impl/InheritedCacheCompiler.java
+++ b/src/org/jruby/compiler/impl/InheritedCacheCompiler.java
@@ -1,619 +1,617 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.compiler.impl;
 
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.jruby.Ruby;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.executable.AbstractScript;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.CacheCompiler;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.objectweb.asm.Label;
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  *
  * @author headius
  */
 public class InheritedCacheCompiler implements CacheCompiler {
     protected StandardASMCompiler scriptCompiler;
     int scopeCount = 0;
     int callSiteCount = 0;
     List<String> callSiteList = new ArrayList<String>();
     List<CallType> callTypeList = new ArrayList<CallType>();
     Map<String, Integer> stringIndices = new HashMap<String, Integer>();
     Map<BigInteger, String> bigIntegers = new HashMap<BigInteger, String>();
     Map<String, Integer> symbolIndices = new HashMap<String, Integer>();
     Map<Long, Integer> fixnumIndices = new HashMap<Long, Integer>();
     int inheritedSymbolCount = 0;
     int inheritedStringCount = 0;
     int inheritedRegexpCount = 0;
     int inheritedBigIntegerCount = 0;
     int inheritedVariableReaderCount = 0;
     int inheritedVariableWriterCount = 0;
     int inheritedFixnumCount = 0;
     int inheritedConstantCount = 0;
     int inheritedBlockBodyCount = 0;
     int inheritedBlockCallbackCount = 0;
     int inheritedMethodCount = 0;
 
     boolean runtimeCacheInited = false;
     
     public InheritedCacheCompiler(StandardASMCompiler scriptCompiler) {
         this.scriptCompiler = scriptCompiler;
     }
 
     public void cacheStaticScope(BaseBodyCompiler method, StaticScope scope) {
         StringBuffer scopeNames = new StringBuffer();
         for (int i = 0; i < scope.getVariables().length; i++) {
             if (i != 0) scopeNames.append(';');
             scopeNames.append(scope.getVariables()[i]);
         }
 
         // retrieve scope from scopes array
         method.loadThis();
         method.loadThreadContext();
         method.method.ldc(scopeNames.toString());
         if (scopeCount < AbstractScript.NUMBERED_SCOPE_COUNT) {
             // use numbered access method
             method.method.invokevirtual(scriptCompiler.getClassname(), "getScope" + scopeCount, sig(StaticScope.class, ThreadContext.class, String.class));
         } else {
             method.method.pushInt(scopeCount);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getScope", sig(StaticScope.class, ThreadContext.class, String.class, int.class));
         }
 
         scopeCount++;
     }
     
     public void cacheCallSite(BaseBodyCompiler method, String name, CallType callType) {
         // retrieve call site from sites array
         method.loadThis();
         if (callSiteCount < AbstractScript.NUMBERED_CALLSITE_COUNT) {
             // use numbered access method
             method.method.invokevirtual(scriptCompiler.getClassname(), "getCallSite" + callSiteCount, sig(CallSite.class));
         } else {
             method.method.pushInt(callSiteCount);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getCallSite", sig(CallSite.class, int.class));
         }
 
         // add name to call site list
         callSiteList.add(name);
         callTypeList.add(callType);
         
         callSiteCount++;
     }
     
     public void cacheSymbol(BaseBodyCompiler method, String symbol) {
         Integer index = symbolIndices.get(symbol);
         if (index == null) {
             index = new Integer(inheritedSymbolCount++);
             symbolIndices.put(symbol, index);
         }
 
         method.loadThis();
         method.loadRuntime();
         if (index < AbstractScript.NUMBERED_SYMBOL_COUNT) {
             method.method.ldc(symbol);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getSymbol" + index, sig(RubySymbol.class, Ruby.class, String.class));
         } else {
             method.method.ldc(index.intValue());
             method.method.ldc(symbol);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getSymbol", sig(RubySymbol.class, Ruby.class, int.class, String.class));
         }
     }
 
     public void cacheRegexp(BaseBodyCompiler method, String pattern, int options) {
         method.loadThis();
         method.loadRuntime();
         int index = inheritedRegexpCount++;
         if (index < AbstractScript.NUMBERED_REGEXP_COUNT) {
             method.method.ldc(pattern);
             method.method.ldc(options);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getRegexp" + index, sig(RubyRegexp.class, Ruby.class, String.class, int.class));
         } else {
             method.method.pushInt(index);
             method.method.ldc(pattern);
             method.method.ldc(options);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getRegexp", sig(RubyRegexp.class, Ruby.class, int.class, String.class, int.class));
         }
     }
 
     public void cacheDRegexp(BaseBodyCompiler method, CompilerCallback createStringCallback, int options) {
         int index = inheritedRegexpCount++;
         Label alreadyCompiled = new Label();
 
         method.loadThis();
         method.method.getfield(scriptCompiler.getClassname(), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
-        method.method.dup();
         method.method.pushInt(index);
         method.method.invokevirtual(p(AbstractScript.RuntimeCache.class), "getRegexp", sig(RubyRegexp.class, int.class));
+        method.method.dup();
 
         method.ifNotNull(alreadyCompiled);
 
-        method.method.dup();
-        method.loadRuntime();
+        method.method.pop();
+        method.loadThis();
+        method.method.getfield(scriptCompiler.getClassname(), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
         method.method.pushInt(index);
         createStringCallback.call(method);
-        method.method.invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
         method.method.ldc(options);
-        method.method.invokevirtual(p(AbstractScript.RuntimeCache.class), "cacheRegexp", sig(void.class, Ruby.class, int.class, ByteList.class, int.class));
+        method.method.invokevirtual(p(AbstractScript.RuntimeCache.class), "cacheRegexp", sig(RubyRegexp.class, int.class, RubyString.class, int.class));
 
         method.method.label(alreadyCompiled);
-        method.method.pushInt(index);
-        method.method.invokevirtual(p(AbstractScript.RuntimeCache.class), "getRegexp", sig(RubyRegexp.class, int.class));
     }
     
     public void cacheFixnum(BaseBodyCompiler method, long value) {
         if (value <= 5 && value >= -1) {
             method.loadRuntime();
             switch ((int)value) {
             case -1:
                 method.method.invokestatic(p(RubyFixnum.class), "minus_one", sig(RubyFixnum.class, Ruby.class));
                 break;
             case 0:
                 method.method.invokestatic(p(RubyFixnum.class), "zero", sig(RubyFixnum.class, Ruby.class));
                 break;
             case 1:
                 method.method.invokestatic(p(RubyFixnum.class), "one", sig(RubyFixnum.class, Ruby.class));
                 break;
             case 2:
                 method.method.invokestatic(p(RubyFixnum.class), "two", sig(RubyFixnum.class, Ruby.class));
                 break;
             case 3:
                 method.method.invokestatic(p(RubyFixnum.class), "three", sig(RubyFixnum.class, Ruby.class));
                 break;
             case 4:
                 method.method.invokestatic(p(RubyFixnum.class), "four", sig(RubyFixnum.class, Ruby.class));
                 break;
             case 5:
                 method.method.invokestatic(p(RubyFixnum.class), "five", sig(RubyFixnum.class, Ruby.class));
                 break;
             default:
                 throw new RuntimeException("wtf?");
             }
         } else {
             Integer index = fixnumIndices.get(value);
             if (index == null) {
                 index = new Integer(inheritedFixnumCount++);
                 fixnumIndices.put(value, index);
             }
             
             method.loadThis();
             method.loadRuntime();
             if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                 if (index < AbstractScript.NUMBERED_FIXNUM_COUNT) {
                     method.method.pushInt((int)value);
                     method.method.invokevirtual(scriptCompiler.getClassname(), "getFixnum" + index, sig(RubyFixnum.class, Ruby.class, int.class));
                 } else {
                     method.method.pushInt(index.intValue());
                     method.method.pushInt((int)value);
                     method.method.invokevirtual(scriptCompiler.getClassname(), "getFixnum", sig(RubyFixnum.class, Ruby.class, int.class, int.class));
                 }
             } else {
                 method.method.pushInt(index.intValue());
                 method.method.ldc(value);
                 method.method.invokevirtual(scriptCompiler.getClassname(), "getFixnum", sig(RubyFixnum.class, Ruby.class, int.class, long.class));
             }
         }
     }
 
     public void cacheConstant(BaseBodyCompiler method, String constantName) {
         method.loadThis();
         method.loadThreadContext();
         method.method.ldc(constantName);
         if (inheritedConstantCount < AbstractScript.NUMBERED_CONSTANT_COUNT) {
             method.method.invokevirtual(scriptCompiler.getClassname(), "getConstant" + inheritedConstantCount, sig(IRubyObject.class, ThreadContext.class, String.class));
         } else {
             method.method.pushInt(inheritedConstantCount);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getConstant", sig(IRubyObject.class, ThreadContext.class, String.class, int.class));
         }
 
         inheritedConstantCount++;
     }
 
     public void cacheConstantFrom(BaseBodyCompiler method, String constantName) {
         // module is on top of stack
         method.loadThis();
         method.method.swap();
         method.loadThreadContext();
         method.method.ldc(constantName);
         if (inheritedConstantCount < AbstractScript.NUMBERED_CONSTANT_COUNT) {
             method.method.invokevirtual(scriptCompiler.getClassname(), "getConstantFrom" + inheritedConstantCount, sig(IRubyObject.class, RubyModule.class, ThreadContext.class, String.class));
         } else {
             method.method.pushInt(inheritedConstantCount);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getConstantFrom", sig(IRubyObject.class, RubyModule.class, ThreadContext.class, String.class, int.class));
         }
 
         inheritedConstantCount++;
     }
 
     public void cacheString(BaseBodyCompiler method, ByteList contents) {
         String asString = contents.toString();
         Integer index = stringIndices.get(asString);
         if (index == null) {
             index = new Integer(inheritedStringCount++);
             stringIndices.put(asString, index);
         }
 
         method.loadThis();
         method.loadRuntime();
         if (index < AbstractScript.NUMBERED_STRING_COUNT) {
             method.method.invokevirtual(scriptCompiler.getClassname(), "getString" + index, sig(RubyString.class, Ruby.class));
         } else {
             method.method.ldc(index.intValue());
             method.method.invokevirtual(scriptCompiler.getClassname(), "getString", sig(RubyString.class, Ruby.class, int.class));
         }
     }
 
     public void cacheBigInteger(BaseBodyCompiler method, BigInteger bigint) {
         method.loadThis();
         method.loadRuntime();
         int index = inheritedBigIntegerCount++;
         if (index < AbstractScript.NUMBERED_BIGINTEGER_COUNT) {
             method.method.ldc(bigint.toString(16));
             method.method.invokevirtual(scriptCompiler.getClassname(), "getBigInteger" + index, sig(BigInteger.class, Ruby.class, String.class));
         } else {
             method.method.pushInt(index);
             method.method.ldc(bigint.toString(16));
             method.method.invokevirtual(scriptCompiler.getClassname(), "getBigInteger", sig(BigInteger.class, Ruby.class, int.class, String.class));
         }
     }
 
     public void cachedGetVariable(BaseBodyCompiler method, String name) {
         method.loadThis();
         method.loadRuntime();
         int index = inheritedVariableReaderCount++;
         if (index < AbstractScript.NUMBERED_VARIABLEREADER_COUNT) {
             method.method.ldc(name);
             method.loadSelf();
             method.method.invokevirtual(scriptCompiler.getClassname(), "getVariable" + index, sig(IRubyObject.class, Ruby.class, String.class, IRubyObject.class));
         } else {
             method.method.pushInt(index);
             method.method.ldc(name);
             method.loadSelf();
             method.method.invokevirtual(scriptCompiler.getClassname(), "getVariable", sig(IRubyObject.class, Ruby.class, int.class, String.class, IRubyObject.class));
         }
     }
 
     public void cachedSetVariable(BaseBodyCompiler method, String name, CompilerCallback valueCallback) {
         method.loadThis();
         method.loadRuntime();
         int index = inheritedVariableWriterCount++;
         if (index < AbstractScript.NUMBERED_VARIABLEWRITER_COUNT) {
             method.method.ldc(name);
             method.loadSelf();
             valueCallback.call(method);
             method.method.invokevirtual(scriptCompiler.getClassname(), "setVariable" + index, sig(IRubyObject.class, Ruby.class, String.class, IRubyObject.class, IRubyObject.class));
         } else {
             method.method.pushInt(index);
             method.method.ldc(name);
             method.loadSelf();
             valueCallback.call(method);
             method.method.invokevirtual(scriptCompiler.getClassname(), "setVariable", sig(IRubyObject.class, Ruby.class, int.class, String.class, IRubyObject.class, IRubyObject.class));
         }
     }
 
     public void cacheClosure(BaseBodyCompiler method, String closureMethod, int arity, StaticScope scope, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector) {
         // build scope names string
         StringBuffer scopeNames = new StringBuffer();
         for (int i = 0; i < scope.getVariables().length; i++) {
             if (i != 0) scopeNames.append(';');
             scopeNames.append(scope.getVariables()[i]);
         }
 
         // build descriptor string
         String descriptor =
                 closureMethod + ',' +
                 arity + ',' +
                 scopeNames + ',' +
                 hasMultipleArgsHead + ',' +
                 BlockBody.asArgumentType(argsNodeId) + ',' +
                 !(inspector.hasClosure() || inspector.hasScopeAwareMethods());
 
         method.loadThis();
         method.loadThreadContext();
 
         if (inheritedBlockBodyCount < AbstractScript.NUMBERED_BLOCKBODY_COUNT) {
             method.method.ldc(descriptor);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getBlockBody" + inheritedBlockBodyCount, sig(BlockBody.class, ThreadContext.class, String.class));
         } else {
             method.method.pushInt(inheritedBlockBodyCount);
             method.method.ldc(descriptor);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getBlockBody", sig(BlockBody.class, ThreadContext.class, int.class, String.class));
         }
 
         inheritedBlockBodyCount++;
     }
 
     public void cacheClosure19(BaseBodyCompiler method, String closureMethod, int arity, StaticScope scope, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector) {
         // build scope names string
         StringBuffer scopeNames = new StringBuffer();
         for (int i = 0; i < scope.getVariables().length; i++) {
             if (i != 0) scopeNames.append(';');
             scopeNames.append(scope.getVariables()[i]);
         }
 
         // build descriptor string
         String descriptor =
                 closureMethod + ',' +
                 arity + ',' +
                 scopeNames + ',' +
                 hasMultipleArgsHead + ',' +
                 BlockBody.asArgumentType(argsNodeId) + ',' +
                 !(inspector.hasClosure() || inspector.hasScopeAwareMethods());
 
         method.loadThis();
         method.loadThreadContext();
 
         if (inheritedBlockBodyCount < AbstractScript.NUMBERED_BLOCKBODY_COUNT) {
             method.method.ldc(descriptor);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getBlockBody19" + inheritedBlockBodyCount, sig(BlockBody.class, ThreadContext.class, String.class));
         } else {
             method.method.pushInt(inheritedBlockBodyCount);
             method.method.ldc(descriptor);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getBlockBody19", sig(BlockBody.class, ThreadContext.class, int.class, String.class));
         }
 
         inheritedBlockBodyCount++;
     }
 
     public void cacheSpecialClosure(BaseBodyCompiler method, String closureMethod) {
         method.loadThis();
         method.loadRuntime();
 
         if (inheritedBlockCallbackCount < AbstractScript.NUMBERED_BLOCKCALLBACK_COUNT) {
             method.method.ldc(closureMethod);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getBlockCallback" + inheritedBlockCallbackCount, sig(CompiledBlockCallback.class, Ruby.class, String.class));
         } else {
             method.method.pushInt(inheritedBlockCallbackCount);
             method.method.ldc(closureMethod);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getBlockCallback", sig(CompiledBlockCallback.class, Ruby.class, int.class, String.class));
         }
 
         inheritedBlockCallbackCount++;
     }
 
     public void cacheMethod(BaseBodyCompiler method, String methodName) {
         method.loadThis();
         method.loadThreadContext();
         method.loadSelf();
 
         if (inheritedMethodCount < AbstractScript.NUMBERED_METHOD_COUNT) {
             method.method.ldc(methodName);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getMethod" + inheritedMethodCount, sig(DynamicMethod.class, ThreadContext.class, IRubyObject.class, String.class));
         } else {
             method.method.pushInt(inheritedMethodCount);
             method.method.ldc(methodName);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getMethod", sig(DynamicMethod.class, ThreadContext.class, IRubyObject.class, int.class, String.class));
         }
 
         inheritedMethodCount++;
     }
 
     public void cacheMethod(BaseBodyCompiler method, String methodName, int receiverLocal) {
         method.loadThis();
         method.loadThreadContext();
         method.method.aload(receiverLocal);
 
         if (inheritedMethodCount < AbstractScript.NUMBERED_METHOD_COUNT) {
             method.method.ldc(methodName);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getMethod" + inheritedMethodCount, sig(DynamicMethod.class, ThreadContext.class, IRubyObject.class, String.class));
         } else {
             method.method.pushInt(inheritedMethodCount);
             method.method.ldc(methodName);
             method.method.invokevirtual(scriptCompiler.getClassname(), "getMethod", sig(DynamicMethod.class, ThreadContext.class, IRubyObject.class, int.class, String.class));
         }
 
         inheritedMethodCount++;
     }
 
     public void finish() {
         SkinnyMethodAdapter initMethod = scriptCompiler.getInitMethod();
 
         // generate call sites initialization code
         int size = callSiteList.size();
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
             
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.dup();
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initCallSites", sig(void.class, int.class));
             initMethod.getfield(p(AbstractScript.RuntimeCache.class), "callSites", ci(CallSite[].class));
             
             for (int i = size - 1; i >= 0; i--) {
                 String name = callSiteList.get(i);
                 CallType callType = callTypeList.get(i);
 
                 initMethod.pushInt(i);
                 if (callType.equals(CallType.NORMAL)) {
                     initMethod.ldc(name);
                     initMethod.invokestatic(scriptCompiler.getClassname(), "setCallSite", sig(CallSite[].class, params(CallSite[].class, int.class, String.class)));
                 } else if (callType.equals(CallType.FUNCTIONAL)) {
                     initMethod.ldc(name);
                     initMethod.invokestatic(scriptCompiler.getClassname(), "setFunctionalCallSite", sig(CallSite[].class, params(CallSite[].class, int.class, String.class)));
                 } else if (callType.equals(CallType.VARIABLE)) {
                     initMethod.ldc(name);
                     initMethod.invokestatic(scriptCompiler.getClassname(), "setVariableCallSite", sig(CallSite[].class, params(CallSite[].class, int.class, String.class)));
                 } else if (callType.equals(CallType.SUPER)) {
                     initMethod.invokestatic(scriptCompiler.getClassname(), "setSuperCallSite", sig(CallSite[].class, params(CallSite[].class, int.class)));
                 }
             }
 
             initMethod.pop();
         }
 
         size = scopeCount;
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
 
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initScopes", sig(void.class, params(int.class)));
         }
 
         // generate symbols initialization code
         size = inheritedSymbolCount;
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
 
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initSymbols", sig(void.class, params(int.class)));
         }
 
         // generate fixnums initialization code
         size = inheritedFixnumCount;
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
 
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initFixnums", sig(void.class, params(int.class)));
         }
 
         // generate constants initialization code
         size = inheritedConstantCount;
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
 
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initConstants", sig(void.class, params(int.class)));
         }
 
         // generate regexps initialization code
         size = inheritedRegexpCount;
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
 
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initRegexps", sig(void.class, params(int.class)));
         }
 
         // generate regexps initialization code
         size = inheritedBigIntegerCount;
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
 
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initBigIntegers", sig(void.class, params(int.class)));
         }
 
         // generate variable readers initialization code
         size = inheritedVariableReaderCount;
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
 
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initVariableReaders", sig(void.class, params(int.class)));
         }
 
         // generate variable writers initialization code
         size = inheritedVariableWriterCount;
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
 
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initVariableWriters", sig(void.class, params(int.class)));
         }
 
         // generate block bodies initialization code
         size = inheritedBlockBodyCount;
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
 
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initBlockBodies", sig(void.class, params(int.class)));
         }
 
         // generate block bodies initialization code
         size = inheritedBlockCallbackCount;
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
 
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initBlockCallbacks", sig(void.class, params(int.class)));
         }
 
         // generate bytelists initialization code
         size = inheritedStringCount;
         if (inheritedStringCount > 0) {
             ensureRuntimeCacheInited(initMethod);
 
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initStrings", sig(ByteList[].class, params(int.class)));
 
             for (Map.Entry<String, Integer> entry : stringIndices.entrySet()) {
                 initMethod.ldc(entry.getValue());
                 initMethod.ldc(entry.getKey());
                 initMethod.invokestatic(p(AbstractScript.class), "createByteList", sig(ByteList[].class, ByteList[].class, int.class, String.class));
             }
             
             initMethod.pop();
         }
 
         // generate method cache initialization code
         size = inheritedMethodCount;
         if (size != 0) {
             ensureRuntimeCacheInited(initMethod);
             
             initMethod.aload(0);
             initMethod.getfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             initMethod.pushInt(size);
             initMethod.invokevirtual(p(AbstractScript.RuntimeCache.class), "initMethodCache", sig(void.class, params(int.class)));
         }
     }
 
     private void ensureRuntimeCacheInited(SkinnyMethodAdapter initMethod) {
         if (!runtimeCacheInited) {
             initMethod.aload(0);
             initMethod.newobj(p(AbstractScript.RuntimeCache.class));
             initMethod.dup();
             initMethod.invokespecial(p(AbstractScript.RuntimeCache.class), "<init>", sig(void.class));
             initMethod.putfield(p(AbstractScript.class), "runtimeCache", ci(AbstractScript.RuntimeCache.class));
             runtimeCacheInited = true;
         }
     }
 }
