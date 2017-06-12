diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index ac5b38dc4a..45f335d851 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -1547,2000 +1547,2009 @@ public class RubyString extends RubyObject implements EncodingCapable {
                 byte b = bytes[p + i];
                 bytes[p + i] = bytes[p + len - i - 1];
                 bytes[p + len - i - 1] = b;
             }
         }
 
         return this;
     }
 
     @JRubyMethod(name = "reverse!", compat = RUBY1_9)
     public RubyString reverse_bang19(ThreadContext context) {
         modifyCheck();
         if (value.getRealSize() > 1) {
             modifyAndKeepCodeRange();
             byte[]bytes = value.getUnsafeBytes();
             int p = value.getBegin();
             int len = value.getRealSize();
 
             Encoding enc = value.getEncoding();
             // this really needs to be inlined here
             if (singleByteOptimizable(enc)) {
                 for (int i = 0; i < len >> 1; i++) {
                     byte b = bytes[p + i];
                     bytes[p + i] = bytes[p + len - i - 1];
                     bytes[p + len - i - 1] = b;
                 }
             } else {
                 int end = p + len;
                 int op = len;
                 byte[]obytes = new byte[len];
                 boolean single = true;
                 while (p < end) {
                     int cl = StringSupport.length(enc, bytes, p, end);
                     if (cl > 1 || (bytes[p] & 0x80) != 0) {
                         single = false;
                         op -= cl;
                         System.arraycopy(bytes, p, obytes, op, cl);
                         p += cl;
                     } else {
                         obytes[--op] = bytes[p++];
                     }
                 }
                 value.setUnsafeBytes(obytes);
                 if (getCodeRange() == CR_UNKNOWN) setCodeRange(single ? CR_7BIT : CR_VALID);
             }
         }
         return this;
     }
 
     /** rb_str_s_new
      *
      */
     public static RubyString newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString newString = newStringShared(recv.getRuntime(), ByteList.EMPTY_BYTELIST);
         newString.setMetaClass((RubyClass) recv);
         newString.callInit(args, block);
         return newString;
     }
 
     @JRubyMethod(visibility = PRIVATE, compat = RUBY1_8)
     @Override
     public IRubyObject initialize(ThreadContext context) {
         return this;
     }
 
     @JRubyMethod(visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize(ThreadContext context, IRubyObject arg0) {
         replace(arg0);
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     @Override
     public IRubyObject initialize19(ThreadContext context) {
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject arg0) {
         replace19(arg0);
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject casecmp(ThreadContext context, IRubyObject other) {
         return RubyFixnum.newFixnum(context.runtime, value.caseInsensitiveCmp(other.convertToString().value));
     }
 
     @JRubyMethod(name = "casecmp", compat = RUBY1_9)
     public IRubyObject casecmp19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         RubyString otherStr = other.convertToString();
         Encoding enc = isCompatibleWith(otherStr);
         if (enc == null) return runtime.getNil();
 
         if (singleByteOptimizable() && otherStr.singleByteOptimizable()) {
             return RubyFixnum.newFixnum(runtime, value.caseInsensitiveCmp(otherStr.value));
         } else {
             return multiByteCasecmp(runtime, enc, value, otherStr.value);
         }
     }
 
     private IRubyObject multiByteCasecmp(Ruby runtime, Encoding enc, ByteList value, ByteList otherValue) {
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
 
         byte[]obytes = otherValue.getUnsafeBytes();
         int op = otherValue.getBegin();
         int oend = op + otherValue.getRealSize();
 
         while (p < end && op < oend) {
             final int c, oc;
             if (enc.isAsciiCompatible()) {
                 c = bytes[p] & 0xff;
                 oc = obytes[op] & 0xff;
             } else {
                 c = StringSupport.preciseCodePoint(enc, bytes, p, end);
                 oc = StringSupport.preciseCodePoint(enc, obytes, op, oend);
             }
 
             int cl, ocl;
             if (Encoding.isAscii(c) && Encoding.isAscii(oc)) {
                 byte uc = AsciiTables.ToUpperCaseTable[c];
                 byte uoc = AsciiTables.ToUpperCaseTable[oc];
                 if (uc != uoc) {
                     return uc < uoc ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
                 }
                 cl = ocl = 1;
             } else {
                 cl = StringSupport.length(enc, bytes, p, end);
                 ocl = StringSupport.length(enc, obytes, op, oend);
                 // TODO: opt for 2 and 3 ?
                 int ret = StringSupport.caseCmp(bytes, p, obytes, op, cl < ocl ? cl : ocl);
                 if (ret != 0) return ret < 0 ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
                 if (cl != ocl) return cl < ocl ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
             }
 
             p += cl;
             op += ocl;
         }
         if (end - p == oend - op) return RubyFixnum.zero(runtime);
         return end - p > oend - op ? RubyFixnum.one(runtime) : RubyFixnum.minus_one(runtime);
     }
 
     /** rb_str_match
      *
      */
     @JRubyMethod(name = "=~", compat = RUBY1_8, writes = BACKREF)
     @Override
     public IRubyObject op_match(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).op_match(context, this);
         if (other instanceof RubyString) throw context.runtime.newTypeError("type mismatch: String given");
         return other.callMethod(context, "=~", this);
     }
 
     @JRubyMethod(name = "=~", compat = RUBY1_9, writes = BACKREF)
     @Override
     public IRubyObject op_match19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).op_match19(context, this);
         if (other instanceof RubyString) throw context.runtime.newTypeError("type mismatch: String given");
         return other.callMethod(context, "=~", this);
     }
     /**
      * String#match(pattern)
      *
      * rb_str_match_m
      *
      * @param pattern Regexp or String
      */
     @JRubyMethod(compat = RUBY1_8, reads = BACKREF)
     public IRubyObject match(ThreadContext context, IRubyObject pattern) {
         return getPattern(pattern).callMethod(context, "match", this);
     }
 
     @JRubyMethod(name = "match", compat = RUBY1_9, reads = BACKREF)
     public IRubyObject match19(ThreadContext context, IRubyObject pattern, Block block) {
         IRubyObject result = getPattern(pattern).callMethod(context, "match", this);
         return block.isGiven() && !result.isNil() ? block.yield(context, result) : result;
     }
 
     @JRubyMethod(name = "match", required = 1, rest = true, compat = RUBY1_9, reads = BACKREF)
     public IRubyObject match19(ThreadContext context, IRubyObject[] args, Block block) {
         RubyRegexp pattern = getPattern(args[0]);
         args[0] = this;
         IRubyObject result = pattern.callMethod(context, "match", args);
         return block.isGiven() && !result.isNil() ? block.yield(context, result) : result;
     }
 
     /** rb_str_capitalize / rb_str_capitalize_bang
      *
      */
     @JRubyMethod(name = "capitalize", compat = RUBY1_8)
     public IRubyObject capitalize(ThreadContext context) {
         RubyString str = strDup(context.runtime);
         str.capitalize_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "capitalize!", compat = RUBY1_8)
     public IRubyObject capitalize_bang(ThreadContext context) {
         Ruby runtime = context.runtime;
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modify();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
         boolean modify = false;
 
         int c = bytes[s] & 0xff;
         if (ASCII.isLower(c)) {
             bytes[s] = AsciiTables.ToUpperCaseTable[c];
             modify = true;
         }
 
         while (++s < end) {
             c = bytes[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 bytes[s] = AsciiTables.ToLowerCaseTable[c];
                 modify = true;
             }
         }
 
         return modify ? this : runtime.getNil();
     }
 
     @JRubyMethod(name = "capitalize", compat = RUBY1_9)
     public IRubyObject capitalize19(ThreadContext context) {
         RubyString str = strDup(context.runtime);
         str.capitalize_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "capitalize!", compat = RUBY1_9)
     public IRubyObject capitalize_bang19(ThreadContext context) {
         Ruby runtime = context.runtime;
         Encoding enc = checkDummyEncoding();
 
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
         boolean modify = false;
 
         int c = codePoint(runtime, enc, bytes, s, end);
         if (enc.isLower(c)) {
             enc.codeToMbc(toUpper(enc, c), bytes, s);
             modify = true;
         }
 
         s += codeLength(runtime, enc, c);
         while (s < end) {
             c = codePoint(runtime, enc, bytes, s, end);
             if (enc.isUpper(c)) {
                 enc.codeToMbc(toLower(enc, c), bytes, s);
                 modify = true;
             }
             s += codeLength(runtime, enc, c);
         }
 
         return modify ? this : runtime.getNil();
     }
 
     @JRubyMethod(name = ">=", compat = RUBY1_8)
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.runtime.newBoolean(op_cmp((RubyString) other) >= 0);
         return RubyComparable.op_ge(context, this, other);
     }
 
     @JRubyMethod(name = ">=", compat = RUBY1_9)
     public IRubyObject op_ge19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.runtime.newBoolean(op_cmp19((RubyString) other) >= 0);
         return RubyComparable.op_ge(context, this, other);
     }
 
     @JRubyMethod(name = ">", compat = RUBY1_8)
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.runtime.newBoolean(op_cmp((RubyString) other) > 0);
         return RubyComparable.op_gt(context, this, other);
     }
 
     @JRubyMethod(name = ">", compat = RUBY1_9)
     public IRubyObject op_gt19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.runtime.newBoolean(op_cmp19((RubyString) other) > 0);
         return RubyComparable.op_gt(context, this, other);
     }
 
     @JRubyMethod(name = "<=", compat = RUBY1_8)
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.runtime.newBoolean(op_cmp((RubyString) other) <= 0);
         return RubyComparable.op_le(context, this, other);
     }
 
     @JRubyMethod(name = "<=", compat = RUBY1_9)
     public IRubyObject op_le19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.runtime.newBoolean(op_cmp19((RubyString) other) <= 0);
         return RubyComparable.op_le(context, this, other);
     }
 
     @JRubyMethod(name = "<", compat = RUBY1_8)
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.runtime.newBoolean(op_cmp((RubyString) other) < 0);
         return RubyComparable.op_lt(context, this, other);
     }
 
     @JRubyMethod(name = "<", compat = RUBY1_9)
     public IRubyObject op_lt19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.runtime.newBoolean(op_cmp19((RubyString) other) < 0);
         return RubyComparable.op_lt(context, this, other);
     }
 
     @JRubyMethod(name = "eql?", compat = RUBY1_8)
     public IRubyObject str_eql_p(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         if (other instanceof RubyString && value.equal(((RubyString)other).value)) return runtime.getTrue();
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = "eql?", compat = RUBY1_9)
     public IRubyObject str_eql_p19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             if (isComparableWith(otherString) && value.equal(otherString.value)) return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     /** rb_str_upcase / rb_str_upcase_bang
      *
      */
     @JRubyMethod(name = "upcase", compat = RUBY1_8)
     public RubyString upcase(ThreadContext context) {
         RubyString str = strDup(context.runtime);
         str.upcase_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "upcase!", compat = RUBY1_8)
     public IRubyObject upcase_bang(ThreadContext context) {
         Ruby runtime = context.runtime;
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
         modify();
         return singleByteUpcase(runtime, value.getUnsafeBytes(), value.getBegin(), value.getBegin() + value.getRealSize());
     }
 
     @JRubyMethod(name = "upcase", compat = RUBY1_9)
     public RubyString upcase19(ThreadContext context) {
         RubyString str = strDup(context.runtime);
         str.upcase_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "upcase!", compat = RUBY1_9)
     public IRubyObject upcase_bang19(ThreadContext context) {
         Ruby runtime = context.runtime;
         Encoding enc = checkDummyEncoding();
 
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
 
         if (singleByteOptimizable(enc)) {
             return singleByteUpcase(runtime, bytes, s, end);
         } else {
             return multiByteUpcase(runtime, enc, bytes, s, end);
         }
     }
 
     private IRubyObject singleByteUpcase(Ruby runtime, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = bytes[s] & 0xff;
             if (ASCII.isLower(c)) {
                 bytes[s] = AsciiTables.ToUpperCaseTable[c];
                 modify = true;
             }
             s++;
         }
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject multiByteUpcase(Ruby runtime, Encoding enc, byte[]bytes, int s, int end) {
         boolean modify = false;
         int c;
         while (s < end) {
             if (enc.isAsciiCompatible() && Encoding.isAscii(c = bytes[s] & 0xff)) {
                 if (ASCII.isLower(c)) {
                     bytes[s] = AsciiTables.ToUpperCaseTable[c];
                     modify = true;
                 }
                 s++;
             } else {
                 c = codePoint(runtime, enc, bytes, s, end);
                 if (enc.isLower(c)) {
                     enc.codeToMbc(toUpper(enc, c), bytes, s);
                     modify = true;
                 }
                 s += codeLength(runtime, enc, c);
             }
         }
         return modify ? this : runtime.getNil();
     }
 
     /** rb_str_downcase / rb_str_downcase_bang
      *
      */
     @JRubyMethod(name = "downcase", compat = RUBY1_8)
     public RubyString downcase(ThreadContext context) {
         RubyString str = strDup(context.runtime);
         str.downcase_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "downcase!", compat = RUBY1_8)
     public IRubyObject downcase_bang(ThreadContext context) {
         Ruby runtime = context.runtime;
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modify();
         return singleByteDowncase(runtime, value.getUnsafeBytes(), value.getBegin(), value.getBegin() + value.getRealSize());
     }
 
     @JRubyMethod(name = "downcase", compat = RUBY1_9)
     public RubyString downcase19(ThreadContext context) {
         RubyString str = strDup(context.runtime);
         str.downcase_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "downcase!", compat = RUBY1_9)
     public IRubyObject downcase_bang19(ThreadContext context) {
         Ruby runtime = context.runtime;
         Encoding enc = checkDummyEncoding();
 
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
 
         if (singleByteOptimizable(enc)) {
             return singleByteDowncase(runtime, bytes, s, end);
         } else {
             return multiByteDowncase(runtime, enc, bytes, s, end);
         }
     }
 
     private IRubyObject singleByteDowncase(Ruby runtime, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = bytes[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 bytes[s] = AsciiTables.ToLowerCaseTable[c];
                 modify = true;
             }
             s++;
         }
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject multiByteDowncase(Ruby runtime, Encoding enc, byte[]bytes, int s, int end) {
         boolean modify = false;
         int c;
         while (s < end) {
             if (enc.isAsciiCompatible() && Encoding.isAscii(c = bytes[s] & 0xff)) {
                 if (ASCII.isUpper(c)) {
                     bytes[s] = AsciiTables.ToLowerCaseTable[c];
                     modify = true;
                 }
                 s++;
             } else {
                 c = codePoint(runtime, enc, bytes, s, end);
                 if (enc.isUpper(c)) {
                     enc.codeToMbc(toLower(enc, c), bytes, s);
                     modify = true;
                 }
                 s += codeLength(runtime, enc, c);
             }
         }
         return modify ? this : runtime.getNil();
     }
 
 
     /** rb_str_swapcase / rb_str_swapcase_bang
      *
      */
     @JRubyMethod(name = "swapcase", compat = RUBY1_8)
     public RubyString swapcase(ThreadContext context) {
         RubyString str = strDup(context.runtime);
         str.swapcase_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "swapcase!", compat = RUBY1_8)
     public IRubyObject swapcase_bang(ThreadContext context) {
         Ruby runtime = context.runtime;
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
         modify();
         return singleByteSwapcase(runtime, value.getUnsafeBytes(), value.getBegin(), value.getBegin() + value.getRealSize());
     }
 
     @JRubyMethod(name = "swapcase", compat = RUBY1_9)
     public RubyString swapcase19(ThreadContext context) {
         RubyString str = strDup(context.runtime);
         str.swapcase_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "swapcase!", compat = RUBY1_9)
     public IRubyObject swapcase_bang19(ThreadContext context) {
         Ruby runtime = context.runtime;
         Encoding enc = checkDummyEncoding();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
 
         if (singleByteOptimizable(enc)) {
             return singleByteSwapcase(runtime, bytes, s, end);
         } else {
             return multiByteSwapcase(runtime, enc, bytes, s, end);
         }
     }
 
     private IRubyObject singleByteSwapcase(Ruby runtime, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = bytes[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 bytes[s] = AsciiTables.ToLowerCaseTable[c];
                 modify = true;
             } else if (ASCII.isLower(c)) {
                 bytes[s] = AsciiTables.ToUpperCaseTable[c];
                 modify = true;
             }
             s++;
         }
 
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject multiByteSwapcase(Ruby runtime, Encoding enc, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = codePoint(runtime, enc, bytes, s, end);
             if (enc.isUpper(c)) {
                 enc.codeToMbc(toLower(enc, c), bytes, s);
                 modify = true;
             } else if (enc.isLower(c)) {
                 enc.codeToMbc(toUpper(enc, c), bytes, s);
                 modify = true;
             }
             s += codeLength(runtime, enc, c);
         }
 
         return modify ? this : runtime.getNil();
     }
 
     /** rb_str_dump
      *
      */
     @JRubyMethod(name = "dump", compat = RUBY1_8)
     public IRubyObject dump() {
         return dumpCommon(false);
     }
 
     @JRubyMethod(name = "dump", compat = RUBY1_9)
     public IRubyObject dump19() {
         return dumpCommon(true);
     }
 
     private IRubyObject dumpCommon(boolean is1_9) {
         Ruby runtime = getRuntime();
         ByteList buf = null;
         Encoding enc = value.getEncoding();
 
         int p = value.getBegin();
         int end = p + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
 
         int len = 2;
         while (p < end) {
             int c = bytes[p++] & 0xff;
 
             switch (c) {
             case '"':case '\\':case '\n':case '\r':case '\t':case '\f':
             case '\013': case '\010': case '\007': case '\033':
                 len += 2;
                 break;
             case '#':
                 len += isEVStr(bytes, p, end) ? 2 : 1;
                 break;
             default:
                 if (ASCII.isPrint(c)) {
                     len++;
                 } else {
                     if (is1_9 && enc instanceof UTF8Encoding) {
                         int n = StringSupport.preciseLength(enc, bytes, p - 1, end) - 1;
                         if (n > 0) {
                             if (buf == null) buf = new ByteList();
                             int cc = codePoint(runtime, enc, bytes, p - 1, end);
                             Sprintf.sprintf(runtime, buf, "%x", cc);
                             len += buf.getRealSize() + 4;
                             buf.setRealSize(0);
                             p += n;
                             break;
                         }
                     }
                     len += 4;
                 }
                 break;
             }
         }
 
         if (is1_9 && !enc.isAsciiCompatible()) {
             len += ".force_encoding(\"".length() + enc.getName().length + "\")".length();
         }
 
         ByteList outBytes = new ByteList(len);
         byte out[] = outBytes.getUnsafeBytes();
         int q = 0;
         p = value.getBegin();
         end = p + value.getRealSize();
 
         out[q++] = '"';
         while (p < end) {
             int c = bytes[p++] & 0xff;
             if (c == '"' || c == '\\') {
                 out[q++] = '\\';
                 out[q++] = (byte)c;
             } else if (c == '#') {
                 if (isEVStr(bytes, p, end)) out[q++] = '\\';
                 out[q++] = '#';
             } else if (!is1_9 && ASCII.isPrint(c)) {
                 out[q++] = (byte)c;
             } else if (c == '\n') {
                 out[q++] = '\\';
                 out[q++] = 'n';
             } else if (c == '\r') {
                 out[q++] = '\\';
                 out[q++] = 'r';
             } else if (c == '\t') {
                 out[q++] = '\\';
                 out[q++] = 't';
             } else if (c == '\f') {
                 out[q++] = '\\';
                 out[q++] = 'f';
             } else if (c == '\013') {
                 out[q++] = '\\';
                 out[q++] = 'v';
             } else if (c == '\010') {
                 out[q++] = '\\';
                 out[q++] = 'b';
             } else if (c == '\007') {
                 out[q++] = '\\';
                 out[q++] = 'a';
             } else if (c == '\033') {
                 out[q++] = '\\';
                 out[q++] = 'e';
             } else if (is1_9 && ASCII.isPrint(c)) {
                 out[q++] = (byte)c;
             } else {
                 out[q++] = '\\';
                 if (is1_9) {
                     if (enc instanceof UTF8Encoding) {
                         int n = StringSupport.preciseLength(enc, bytes, p - 1, end) - 1;
                         if (n > 0) {
                             int cc = codePoint(runtime, enc, bytes, p - 1, end);
                             p += n;
                             outBytes.setRealSize(q);
                             Sprintf.sprintf(runtime, outBytes, "u{%x}", cc);
                             q = outBytes.getRealSize();
                             continue;
                         }
                     }
                     outBytes.setRealSize(q);
                     Sprintf.sprintf(runtime, outBytes, "x%02X", c);
                     q = outBytes.getRealSize();
                 } else {
                     outBytes.setRealSize(q);
                     Sprintf.sprintf(runtime, outBytes, "%03o", c);
                     q = outBytes.getRealSize();
                 }
             }
         }
         out[q++] = '"';
         outBytes.setRealSize(q);
         assert out == outBytes.getUnsafeBytes(); // must not reallocate
 
         final RubyString result = new RubyString(runtime, getMetaClass(), outBytes);
         if (is1_9) {
             if (!enc.isAsciiCompatible()) {
                 result.cat(".force_encoding(\"".getBytes());
                 result.cat(enc.getName());
                 result.cat((byte)'"').cat((byte)')');
                 enc = ASCII;
             }
             result.associateEncoding(enc);
             result.setCodeRange(CR_7BIT);
         }
         return result.infectBy(this);
     }
 
     @JRubyMethod(name = "insert", compat = RUBY1_8)
     public IRubyObject insert(ThreadContext context, IRubyObject indexArg, IRubyObject stringArg) {
         assert !context.runtime.is1_9();
         RubyString str = stringArg.convertToString();
         int index = RubyNumeric.num2int(indexArg);
         if (index == -1) return append(stringArg);
         if (index < 0) index++;
         replaceInternal(checkIndex(index, value.getRealSize()), 0, str);
         return this;
     }
 
     @JRubyMethod(name = "insert", compat = RUBY1_9)
     public IRubyObject insert19(ThreadContext context, IRubyObject indexArg, IRubyObject stringArg) {
         RubyString str = stringArg.convertToString();
         int index = RubyNumeric.num2int(indexArg);
         if (index == -1) return append19(stringArg);
         if (index < 0) index++;
         replaceInternal19(checkIndex(index, strLength()), 0, str);
         return this;
     }
 
     private int checkIndex(int beg, int len) {
         if (beg > len) raiseIndexOutOfString(beg);
         if (beg < 0) {
             if (-beg > len) raiseIndexOutOfString(beg);
             beg += len;
         }
         return beg;
     }
 
     private int checkIndexForRef(int beg, int len) {
         if (beg >= len) raiseIndexOutOfString(beg);
         if (beg < 0) {
             if (-beg > len) raiseIndexOutOfString(beg);
             beg += len;
         }
         return beg;
     }
 
     private int checkLength(int len) {
         if (len < 0) throw getRuntime().newIndexError("negative length " + len);
         return len;
     }
 
     private void raiseIndexOutOfString(int index) {
         throw getRuntime().newIndexError("index " + index + " out of string");
     }
 
     private void prefixEscapeCat(int c) {
         cat('\\');
         cat(c);
     }
 
     private boolean isEVStr(byte[]bytes, int p, int end) {
         return p < end ? isEVStr(bytes[p] & 0xff) : false;
     }
 
     public boolean isEVStr(int c) {
         return c == '$' || c == '@' || c == '{';
     }
 
     /** rb_str_inspect
      *
      */
     @JRubyMethod(name = "inspect", compat = RUBY1_8)
     @Override
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         byte bytes[] = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
         RubyString result = new RubyString(runtime, runtime.getString(), new ByteList(end - p));
         Encoding enc = runtime.getKCode().getEncoding();
 
         result.cat('"');
         while (p < end) {
             int c = bytes[p++] & 0xff;
             int n = enc.length((byte)c);
 
             if (n > 1 && p - 1 <= end - n) {
                 result.cat(bytes, p - 1, n);
                 p += n - 1;
                 continue;
             } else if (c == '"'|| c == '\\' || (c == '#' && isEVStr(bytes, p, end))) {
                 result.prefixEscapeCat(c);
             } else if (ASCII.isPrint(c)) {
                 result.cat(c);
             } else if (c == '\n') {
                 result.prefixEscapeCat('n');
             } else if (c == '\r') {
                 result.prefixEscapeCat('r');
             } else if (c == '\t') {
                 result.prefixEscapeCat('t');
             } else if (c == '\f') {
                 result.prefixEscapeCat('f');
             } else if (c == '\013') {
                 result.prefixEscapeCat('v');
             } else if (c == '\010') {
                 result.prefixEscapeCat('b');
             } else if (c == '\007') {
                 result.prefixEscapeCat('a');
             } else if (c == '\033') {
                 result.prefixEscapeCat('e');
             } else  {
                 Sprintf.sprintf(runtime, result.value, "\\%03o", c & 0377);
             }
         }
         result.cat('"');
         return result.infectBy(this);        
     }
 
     @JRubyMethod(name = "inspect", compat = RUBY1_9)
     public IRubyObject inspect19() {
         Ruby runtime = getRuntime();
         byte bytes[] = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
         RubyString result = new RubyString(runtime, runtime.getString(), new ByteList(end - p));
         Encoding enc = getEncoding();
 
         Encoding resultEnc = runtime.getDefaultInternalEncoding();
         if (resultEnc == null) resultEnc = runtime.getDefaultExternalEncoding();
         if (!resultEnc.isAsciiCompatible()) resultEnc = USASCIIEncoding.INSTANCE;
         result.associateEncoding(resultEnc);
 
         boolean isUnicode = StringSupport.isUnicode(enc);
 
         EncodingDB.Entry e = null;
         CaseInsensitiveBytesHash<EncodingDB.Entry> encodings = runtime.getEncodingService().getEncodings();
         if (enc == encodings.get("UTF-16".getBytes()).getEncoding() && end - p > 1) {
             int c0 = bytes[p] & 0xff;
             int c1 = bytes[p + 1] & 0xff;
             
             if (c0 == 0xFE && c1 == 0xFF) {
                 e = encodings.get("UTF-16BE".getBytes());
             } else if (c0 == 0xFF && c1 == 0xFE) {
                 e = encodings.get("UTF-16LE".getBytes());
             } else {
                 isUnicode = false;
             }
         } else if (enc == encodings.get("UTF-32".getBytes()).getEncoding() && end - p > 3) {
             int c0 = bytes[p] & 0xff;
             int c1 = bytes[p + 1] & 0xff;
             int c2 = bytes[p + 2] & 0xff;
             int c3 = bytes[p + 3] & 0xff;
             
             if (c0 == 0 && c1 == 0 && c2 == 0xFE && c3 == 0xFF) {
                 e = encodings.get("UTF-32BE".getBytes());
             } else if (c3 == 0 && c2 == 0 && c1 == 0xFE && c0 == 0xFF) {
                 e = encodings.get("UTF-32LE".getBytes());
             } else {
                 isUnicode = false;
             }
         }
 
         if (e != null) enc = e.getEncoding();
 
         result.cat('"');
         int prev = p;
         while (p < end) {
             int cc = 0;
 
             int n = StringSupport.preciseLength(enc, bytes, p, end);
             if (n <= 0) {
                 if (p > prev) result.cat(bytes, prev, p - prev);
                 n = enc.minLength();
                 if (end < p + n) n = end - p;
                 while (n-- > 0) {
                     Sprintf.sprintf(runtime, result.getByteList() ,"\\x%02X", bytes[p] & 0377);
                     prev = ++p;
                 }
                 continue;
             }
             int c = enc.mbcToCode(bytes, p, end);
             p += n;
             if ((enc.isAsciiCompatible() || isUnicode) &&
                     (c == '"' || c == '\\' ||
                         (c == '#' && p < end && (StringSupport.preciseLength(enc, bytes, p, end) > 0) &&
                         (cc = codePoint(runtime, enc, bytes, p, end)) == '$' || cc == '@' || cc == '{'))) {
                 if (p - n > prev) result.cat(bytes, prev, p - n - prev);
                 result.cat('\\');
                 if (enc.isAsciiCompatible() || enc == resultEnc) {
                     prev = p - n;
                     continue;
                 }
             }
 
             switch (c) {
             case '\n': cc = 'n'; break;
             case '\r': cc = 'r'; break;
             case '\t': cc = 't'; break;
             case '\f': cc = 'f'; break;
             case '\013': cc = 'v'; break;
             case '\010': cc = 'b'; break;
             case '\007': cc = 'a'; break;
             case 033: cc = 'e'; break;
             default: cc = 0; break;
             }
 
             if (cc != 0) {
                 if (p - n > prev) result.cat(bytes, prev, p - n - prev);
                 result.cat('\\');
                 result.cat(cc);
                 prev = p;
                 continue;
             }
 
             if ((enc == resultEnc && enc.isPrint(c)) || (enc.isAsciiCompatible() && Encoding.isAscii(c) && enc.isPrint(c))) {
                 continue;
             } else {
                 if (p - n > prev) result.cat(bytes, prev, p - n - prev);
                 Sprintf.sprintf(runtime, result.getByteList() , StringSupport.escapedCharFormat(c, isUnicode), c);
                 prev = p;
                 continue;
             }
         }
 
         if (p > prev) result.cat(bytes, prev, p - prev);
         result.cat('"');
         return result.infectBy(this);
     }
 
     public int size() {
         return value.getRealSize();
     }
 
     /** rb_str_length
      *
      */
     @JRubyMethod(name = {"length", "size"}, compat = RUBY1_8)
     public RubyFixnum length() {
         return getRuntime().newFixnum(value.getRealSize());
     }
 
     @JRubyMethod(name = {"length", "size"}, compat = RUBY1_9)
     public RubyFixnum length19() {
         return getRuntime().newFixnum(strLength());
     }
 
     @JRubyMethod(name = "bytesize")
     public RubyFixnum bytesize() {
         return length(); // use 1.8 impl
     }
     /** rb_str_empty
      *
      */
     @JRubyMethod(name = "empty?")
     public RubyBoolean empty_p(ThreadContext context) {
         return isEmpty() ? context.runtime.getTrue() : context.runtime.getFalse();
     }
 
     public boolean isEmpty() {
         return value.length() == 0;
     }
 
     /** rb_str_append
      *
      */
     public RubyString append(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            cat(ConvertBytes.longToByteList(((RubyFixnum)other).getLongValue()));
+            return this;
+        } else if (other instanceof RubyFloat) {
+            return cat((RubyString)((RubyFloat)other).to_s());
+        } else if (other instanceof RubySymbol) {
+            cat(((RubySymbol)other).getBytes());
+            return this;
+        }
         RubyString otherStr = other.convertToString();
         infectBy(otherStr);
         return cat(otherStr.value);
     }
 
     public RubyString append19(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             cat19(ConvertBytes.longToByteList(((RubyFixnum)other).getLongValue()), StringSupport.CR_7BIT);
             return this;
         } else if (other instanceof RubyFloat) {
             return cat19((RubyString)((RubyFloat)other).to_s());
         } else if (other instanceof RubySymbol) {
             cat19(((RubySymbol)other).getBytes(), 0);
             return this;
         }
         return cat19(other.convertToString());
     }
 
     /** rb_str_concat
      *
      */
     @JRubyMethod(name = {"concat", "<<"}, compat = RUBY1_8)
     public RubyString concat(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long longValue = ((RubyFixnum) other).getLongValue();
             if (longValue >= 0 && longValue < 256) return cat((byte) longValue);
         }
         return append(other);
     }
 
     @JRubyMethod(name = {"concat", "<<"}, compat = RUBY1_9)
     public RubyString concat19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         if (other instanceof RubyFixnum) {
             int c = RubyNumeric.num2int(other);
             if (c < 0) {
                 throw runtime.newRangeError("negative string size (or size too big)");
             }
             return concatNumeric(runtime, c);
         } else if (other instanceof RubyBignum) {
             if (((RubyBignum) other).getBigIntegerValue().signum() < 0) {
                 throw runtime.newRangeError("negative string size (or size too big)");
             }
             long c = ((RubyBignum) other).getLongValue();
             return concatNumeric(runtime, (int) c);
         }
         return append19(other);
     }
 
     private RubyString concatNumeric(Ruby runtime, int c) {
         Encoding enc = value.getEncoding();
         int cl = codeLength(runtime, enc, c);
         modify19(value.getRealSize() + cl);
         enc.codeToMbc(c, value.getUnsafeBytes(), value.getBegin() + value.getRealSize());
         value.setRealSize(value.getRealSize() + cl);
         return this;
     }
 
     /**
      * rb_str_prepend
      */
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject prepend(ThreadContext context, IRubyObject other) {
         return replace19(other.convertToString().op_plus19(context, this));
     }
 
     /** rb_str_crypt
      *
      */
     @JRubyMethod(name = "crypt")
     public RubyString crypt(ThreadContext context, IRubyObject other) {
         RubyString otherStr = other.convertToString();
         ByteList salt = otherStr.getByteList();
         if (salt.getRealSize() < 2) {
             throw context.runtime.newArgumentError("salt too short(need >=2 bytes)");
         }
 
         salt = salt.makeShared(0, 2);
         RubyString result = RubyString.newStringShared(context.runtime, JavaCrypt.crypt(salt, this.getByteList()));
         result.infectBy(this);
         result.infectBy(otherStr);
         return result;
     }
 
     /* RubyString aka rb_string_value */
     public static RubyString stringValue(IRubyObject object) {
         return (RubyString) (object instanceof RubyString ? object :
             object.convertToString());
     }
 
     /** rb_str_sub / rb_str_sub_bang
      *
      */
     @JRubyMethod(reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject sub(ThreadContext context, IRubyObject arg0, Block block) {
         RubyString str = strDup(context.runtime);
         str.sub_bang(context, arg0, block);
         return str;
     }
 
     @JRubyMethod(reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject sub(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = strDup(context.runtime);
         str.sub_bang(context, arg0, arg1, block);
         return str;
     }
 
     @JRubyMethod(name = "sub!", reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject sub_bang(ThreadContext context, IRubyObject arg0, Block block) {
         if (block.isGiven()) return subBangIter(context, getQuotedPattern(arg0), block);
         throw context.runtime.newArgumentError(1, 2);
     }
 
     @JRubyMethod(name = "sub!", reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject sub_bang(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return subBangNoIter(context, getQuotedPattern(arg0), arg1.convertToString());
     }
 
     private IRubyObject subBangIter(ThreadContext context, Regex pattern, Block block) {
         int range = value.getBegin() + value.getRealSize();
         Matcher matcher = pattern.matcher(value.getUnsafeBytes(), value.getBegin(), range);
 
         DynamicScope scope = context.getCurrentScope();
         if (matcher.search(value.getBegin(), range, Option.NONE) >= 0) {
             frozenCheck(true);
             byte[] bytes = value.getUnsafeBytes();
             int size = value.getRealSize();
             RubyMatchData match = RubyRegexp.updateBackRef(context, this, scope, matcher, pattern);
             RubyString repl = objAsString(context, block.yield(context,
                     makeShared(context.runtime, matcher.getBegin(), matcher.getEnd() - matcher.getBegin())));
             modifyCheck(bytes, size);
             frozenCheck(true);
             scope.setBackRef(match);
             return subBangCommon(context, pattern, matcher, repl, repl.flags);
         } else {
             return scope.setBackRef(context.runtime.getNil());
         }
     }
 
     private IRubyObject subBangNoIter(ThreadContext context, Regex pattern, RubyString repl) {
         int tuFlags = repl.flags;
         int range = value.getBegin() + value.getRealSize();
         Matcher matcher = pattern.matcher(value.getUnsafeBytes(), value.getBegin(), range);
 
         DynamicScope scope = context.getCurrentScope();
         if (matcher.search(value.getBegin(), range, Option.NONE) >= 0) {
             repl = RubyRegexp.regsub(repl, this, matcher, context.runtime.getKCode().getEncoding());
             RubyRegexp.updateBackRef(context, this, scope, matcher, pattern);
             return subBangCommon(context, pattern, matcher, repl, tuFlags);
         } else {
             return scope.setBackRef(context.runtime.getNil());
         }
     }
 
     private IRubyObject subBangCommon(ThreadContext context, Regex pattern, Matcher matcher, RubyString repl, int tuFlags) {
         final int beg = matcher.getBegin();
         final int plen = matcher.getEnd() - beg;
 
         ByteList replValue = repl.value;
         if (replValue.getRealSize() > plen) {
             modify(value.getRealSize() + replValue.getRealSize() - plen);
         } else {
             modify();
         }
 
         if (replValue.getRealSize() != plen) {
             int src = value.getBegin() + beg + plen;
             int dst = value.getBegin() + beg + replValue.getRealSize();
             int length = value.getRealSize() - beg - plen;
             System.arraycopy(value.getUnsafeBytes(), src, value.getUnsafeBytes(), dst, length);
         }
         System.arraycopy(replValue.getUnsafeBytes(), replValue.getBegin(), value.getUnsafeBytes(), value.getBegin() + beg, replValue.getRealSize());
         value.setRealSize(value.getRealSize() + replValue.getRealSize() - plen);
         infectBy(tuFlags);
         return this;
     }
 
     @JRubyMethod(name = "sub", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject sub19(ThreadContext context, IRubyObject arg0, Block block) {
         RubyString str = strDup(context.runtime);
         str.sub_bang19(context, arg0, block);
         return str;
     }
 
     @JRubyMethod(name = "sub", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject sub19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = strDup(context.runtime);
         str.sub_bang19(context, arg0, arg1, block);
         return str;
     }
 
     @JRubyMethod(name = "sub!", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject sub_bang19(ThreadContext context, IRubyObject arg0, Block block) {
         Ruby runtime = context.runtime;
         frozenCheck();
 
         final Regex pattern, prepared;
         final RubyRegexp regexp;
         if (arg0 instanceof RubyRegexp) {
             regexp = (RubyRegexp)arg0;
             pattern = regexp.getPattern();
             prepared = regexp.preparePattern(this);
         } else {
             regexp = null;
             pattern = getStringPattern19(runtime, arg0);
             prepared = RubyRegexp.preparePattern(runtime, pattern, this);
         }
 
         if (block.isGiven()) return subBangIter19(runtime, context, pattern, prepared, null, block, regexp);
         throw context.runtime.newArgumentError(1, 2);
     }
 
     @JRubyMethod(name = "sub!", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject sub_bang19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         Ruby runtime = context.runtime;
         IRubyObject hash = TypeConverter.convertToTypeWithCheck(arg1, runtime.getHash(), "to_hash");
         frozenCheck();
 
         final Regex pattern, prepared;
         final RubyRegexp regexp;
         if (arg0 instanceof RubyRegexp) {
             regexp = (RubyRegexp)arg0;
             pattern = regexp.getPattern();
             prepared = regexp.preparePattern(this);
         } else {
             regexp = null;
             pattern = getStringPattern19(runtime, arg0);
             prepared = RubyRegexp.preparePattern(runtime, pattern, this);
         }
 
         if (hash.isNil()) {
             return subBangNoIter19(runtime, context, pattern, prepared, arg1.convertToString(), regexp);
         } else {
             return subBangIter19(runtime, context, pattern, prepared, (RubyHash)hash, block, regexp);
         }
     }
 
     private IRubyObject subBangIter19(Ruby runtime, ThreadContext context, Regex pattern, Regex prepared, RubyHash hash, Block block, RubyRegexp regexp) {
         int begin = value.getBegin();
         int len = value.getRealSize();
         int range = begin + len;
         byte[]bytes = value.getUnsafeBytes();
         Encoding enc = value.getEncoding();
         final Matcher matcher = prepared.matcher(bytes, begin, range);
 
         DynamicScope scope = context.getCurrentScope();
         if (matcher.search(begin, range, Option.NONE) >= 0) {
             RubyMatchData match = RubyRegexp.updateBackRef19(context, this, scope, matcher, pattern);
             match.regexp = regexp;
             final RubyString repl;
             final int tuFlags;
             IRubyObject subStr = makeShared19(runtime, matcher.getBegin(), matcher.getEnd() - matcher.getBegin());
             if (hash == null) {
                 tuFlags = 0;
                 repl = objAsString(context, block.yield(context, subStr));
             } else {
                 tuFlags = hash.flags;
                 repl = objAsString(context, hash.op_aref(context, subStr));
             }
 
             modifyCheck(bytes, len, enc);
             frozenCheck();
             scope.setBackRef(match);
             return subBangCommon19(context, pattern, matcher, repl, tuFlags | repl.flags);
         } else {
             return scope.setBackRef(runtime.getNil());
         }
     }
 
     private IRubyObject subBangNoIter19(Ruby runtime, ThreadContext context, Regex pattern, Regex prepared, RubyString repl, RubyRegexp regexp) {
         int begin = value.getBegin();
         int range = begin + value.getRealSize();
         final Matcher matcher = prepared.matcher(value.getUnsafeBytes(), begin, range);
 
         DynamicScope scope = context.getCurrentScope();
         if (matcher.search(begin, range, Option.NONE) >= 0) {
             repl = RubyRegexp.regsub19(repl, this, matcher, pattern);
             RubyMatchData match = RubyRegexp.updateBackRef19(context, this, scope, matcher, pattern);
             match.regexp = regexp;
             return subBangCommon19(context, pattern, matcher, repl, repl.flags);
         } else {
             return scope.setBackRef(runtime.getNil());
         }
     }
 
     private IRubyObject subBangCommon19(ThreadContext context, Regex pattern, Matcher matcher, RubyString repl, int tuFlags) {
         final int beg = matcher.getBegin();
         final int end = matcher.getEnd();
         int cr = getCodeRange();
 
         Encoding enc = isCompatibleWith(repl);
         if (enc == null) enc = subBangVerifyEncoding(context, repl, beg, end);
 
         final int plen = end - beg;
         ByteList replValue = repl.value;
         if (replValue.getRealSize() > plen) {
             modify19(value.getRealSize() + replValue.getRealSize() - plen);
         } else {
             modify19();
         }
 
         associateEncoding(enc);
 
         if (cr > CR_UNKNOWN && cr < CR_BROKEN) {
             int cr2 = repl.getCodeRange();
             if (cr2 == CR_BROKEN || (cr == CR_VALID && cr2 == CR_7BIT)) {
                 cr = CR_UNKNOWN;
             } else {
                 cr = cr2;
             }
         }
 
         if (replValue.getRealSize() != plen) {
             int src = value.getBegin() + beg + plen;
             int dst = value.getBegin() + beg + replValue.getRealSize();
             int length = value.getRealSize() - beg - plen;
             System.arraycopy(value.getUnsafeBytes(), src, value.getUnsafeBytes(), dst, length);
         }
         System.arraycopy(replValue.getUnsafeBytes(), replValue.getBegin(), value.getUnsafeBytes(), value.getBegin() + beg, replValue.getRealSize());
         value.setRealSize(value.getRealSize() + replValue.getRealSize() - plen);
         setCodeRange(cr);
         return infectBy(tuFlags);
     }
 
     private Encoding subBangVerifyEncoding(ThreadContext context, RubyString repl, int beg, int end) {
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         Encoding strEnc = value.getEncoding();
         if (codeRangeScan(strEnc, bytes, p, beg) != CR_7BIT ||
             codeRangeScan(strEnc, bytes, p + end, len - end) != CR_7BIT) {
             throw context.runtime.newArgumentError(
                     "incompatible character encodings " + strEnc + " and " + repl.value.getEncoding());
         }
         return repl.value.getEncoding();
     }
 
     /** rb_str_gsub / rb_str_gsub_bang
      *
      */
     @JRubyMethod(reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject gsub(ThreadContext context, IRubyObject arg0, Block block) {
         return gsub(context, arg0, block, false);
     }
 
     @JRubyMethod(reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject gsub(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return gsub(context, arg0, arg1, block, false);
     }
 
     @JRubyMethod(name = "gsub!", reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject gsub_bang(ThreadContext context, IRubyObject arg0, Block block) {
         return gsub(context, arg0, block, true);
     }
 
     @JRubyMethod(name = "gsub!", reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject gsub_bang(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return gsub(context, arg0, arg1, block, true);
     }
 
     private IRubyObject gsub(ThreadContext context, IRubyObject arg0, Block block, final boolean bang) {
         if (block.isGiven()) {
             return gsubCommon(context, bang, arg0, block, null, 0);
         } else {
             return enumeratorize(context.runtime, this, bang ? "gsub!" : "gsub", arg0);
         }
     }
 
     private IRubyObject gsub(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block, final boolean bang) {
         RubyString repl = arg1.convertToString();
         return gsubCommon(context, bang, arg0, block, repl, repl.flags);
     }
 
     private IRubyObject gsubCommon(ThreadContext context, final boolean bang, IRubyObject arg, Block block, RubyString repl, int tuFlags) {
         Ruby runtime = context.runtime;
         DynamicScope scope = context.getCurrentScope();
         Regex pattern = getQuotedPattern(arg);
 
         int begin = value.getBegin();
         int slen = value.getRealSize();
         int range = begin + slen;
         byte[]bytes = value.getUnsafeBytes();
         Matcher matcher = pattern.matcher(bytes, begin, range);
 
         int beg = matcher.search(begin, range, Option.NONE);
         if (beg < 0) {
             scope.setBackRef(runtime.getNil());
             return bang ? runtime.getNil() : strDup(runtime); /* bang: true, no match, no substitution */
         } else if (repl == null && bang && isFrozen()) {
             throw getRuntime().newRuntimeError("can't modify frozen string");
         }
 
         int blen = slen + 30; /* len + margin */
         ByteList dest = new ByteList(blen);
         dest.setRealSize(blen);
         int offset = 0, buf = 0, bp = 0, cp = begin;
 
         Encoding enc = getEncodingForKCodeDefault(runtime, pattern, arg);
 
         RubyMatchData match = null;
         while (beg >= 0) {
             final RubyString val;
             final int begz = matcher.getBegin();
             final int endz = matcher.getEnd();
 
             if (repl == null) { // block given
                 match = RubyRegexp.updateBackRef(context, this, scope, matcher, pattern);
                 val = objAsString(context, block.yield(context, substr(runtime, begz, endz - begz)));
                 modifyCheck(bytes, slen);
                 if (bang) frozenCheck();
             } else {
                 val = RubyRegexp.regsub(repl, this, matcher, enc);
             }
 
             tuFlags |= val.flags;
 
             ByteList vbuf = val.value;
             int len = (bp - buf) + (beg - offset) + vbuf.getRealSize() + 3;
             if (blen < len) {
                 while (blen < len) blen <<= 1;
                 len = bp - buf;
                 dest.realloc(blen);
                 dest.setRealSize(blen);
                 bp = buf + len;
             }
             len = beg - offset; /* copy pre-match substr */
             System.arraycopy(bytes, cp, dest.getUnsafeBytes(), bp, len);
             bp += len;
             System.arraycopy(vbuf.getUnsafeBytes(), vbuf.getBegin(), dest.getUnsafeBytes(), bp, vbuf.getRealSize());
             bp += vbuf.getRealSize();
             offset = endz;
 
             if (begz == endz) {
                 if (slen <= endz) break;
                 len = enc.length(bytes, begin + endz, range);
                 System.arraycopy(bytes, begin + endz, dest.getUnsafeBytes(), bp, len);
                 bp += len;
                 offset = endz + len;
             }
             cp = begin + offset;
             if (offset > slen) break;
             beg = matcher.search(cp, range, Option.NONE);
         }
 
         if (repl == null) { // block given
             scope.setBackRef(match);
         } else {
             RubyRegexp.updateBackRef(context, this, scope, matcher, pattern);
         }
 
         if (slen > offset) {
             int len = bp - buf;
             if (blen - len < slen - offset) {
                 blen = len + slen - offset;
                 dest.realloc(blen);
                 bp = buf + len;
             }
             System.arraycopy(bytes, cp, dest.getUnsafeBytes(), bp, slen - offset);
             bp += slen - offset;
         }
 
         dest.setRealSize(bp - buf);
         if (bang) {
             view(dest);
             return infectBy(tuFlags);
         } else {
             return new RubyString(runtime, getMetaClass(), dest).infectBy(tuFlags | flags);
         }
     }
 
     @JRubyMethod(name = "gsub", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject gsub19(ThreadContext context, IRubyObject arg0, Block block) {
         return block.isGiven() ? gsubCommon19(context, block, null, null, arg0, false, 0) : enumeratorize(context.runtime, this, "gsub", arg0);
     }
 
     @JRubyMethod(name = "gsub", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject gsub19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return gsub19(context, arg0, arg1, block, false);
     }
 
     @JRubyMethod(name = "gsub!", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject gsub_bang19(ThreadContext context, IRubyObject arg0, Block block) {
         checkFrozen();
         return block.isGiven() ? gsubCommon19(context, block, null, null, arg0, true, 0) : enumeratorize(context.runtime, this, "gsub!", arg0);
     }
 
     @JRubyMethod(name = "gsub!", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject gsub_bang19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         checkFrozen();
         return gsub19(context, arg0, arg1, block, true);
     }
 
     private IRubyObject gsub19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block, final boolean bang) {
         Ruby runtime = context.runtime;
         IRubyObject tryHash = TypeConverter.convertToTypeWithCheck(arg1, runtime.getHash(), "to_hash");
 
         final RubyHash hash;
         final RubyString str;
         final int tuFlags;
         if (tryHash.isNil()) {
             hash = null;
             str = arg1.convertToString();
             tuFlags = str.flags;
         } else {
             hash = (RubyHash)tryHash;
             str = null;
             tuFlags = hash.flags & TAINTED_F;
         }
 
         return gsubCommon19(context, block, str, hash, arg0, bang, tuFlags);
     }
 
     private IRubyObject gsubCommon19(ThreadContext context, Block block, RubyString repl,
             RubyHash hash, IRubyObject arg0, final boolean bang, int tuFlags) {
         Ruby runtime = context.runtime;
 
         final Regex pattern, prepared;
         final RubyRegexp regexp;
         if (arg0 instanceof RubyRegexp) {
             regexp = (RubyRegexp)arg0;
             pattern = regexp.getPattern();
             prepared = regexp.preparePattern(this);
         } else {
             regexp = null;
             pattern = getStringPattern19(runtime, arg0);
             prepared = RubyRegexp.preparePattern(runtime, pattern, this);
         }
 
         final int begin = value.getBegin();
         int slen = value.getRealSize();
         final int range = begin + slen;
         byte[]bytes = value.getUnsafeBytes();
         final Matcher matcher = prepared.matcher(bytes, begin, range);
 
         final DynamicScope scope = context.getCurrentScope();
         int beg = matcher.search(begin, range, Option.NONE);
         if (beg < 0) {
             scope.setBackRef(runtime.getNil());
             return bang ? runtime.getNil() : strDup(runtime); /* bang: true, no match, no substitution */
         }
 
         RubyString dest = new RubyString(runtime, getMetaClass(), new ByteList(slen + 30));
         int offset = 0, cp = begin;
         Encoding enc = value.getEncoding();
         dest.setEncoding(enc);
         dest.setCodeRange(enc.isAsciiCompatible() ? CR_7BIT : CR_VALID);
 
         RubyMatchData match = null;
         do {
             final RubyString val;
             int begz = matcher.getBegin();
             int endz = matcher.getEnd();
 
             if (repl != null) {     // string given
                 val = RubyRegexp.regsub19(repl, this, matcher, pattern);
             } else {
                 final RubyString substr = makeShared19(runtime, begz, endz - begz);  
                 if (hash != null) { // hash given
                     val = objAsString(context, hash.op_aref(context, substr)); 
                 } else {            // block given
                     match = RubyRegexp.updateBackRef19(context, this, scope, matcher, pattern);
                     match.regexp = regexp;
                     val = objAsString(context, block.yield(context, substr));
                 }
                 modifyCheck(bytes, slen, enc);
                 if (bang) frozenCheck();
             }
 
             tuFlags |= val.flags;
 
             int len = beg - offset;
             if (len != 0) dest.cat(bytes, cp, len, enc);
             dest.cat19(val);
             offset = endz;
             if (begz == endz) {
                 if (slen <= endz) break;
                 len = StringSupport.length(enc, bytes, begin + endz, range);
                 dest.cat(bytes, begin + endz, len, enc);
                 offset = endz + len;
             }
             cp = begin + offset;
             if (offset > slen) break;
             beg = matcher.search(cp, range, Option.NONE);
         } while (beg >= 0);
 
         if (slen > offset) dest.cat(bytes, cp, slen - offset, enc);
 
         if (match != null) { // block given
             scope.setBackRef(match);
         } else {
             match = RubyRegexp.updateBackRef19(context, this, scope, matcher, pattern);
             match.regexp = regexp;
         }
 
         if (bang) {
             view(dest.value);
             setCodeRange(dest.getCodeRange());
             return infectBy(tuFlags);
         } else {
             return dest.infectBy(tuFlags | flags);
         }
     }
 
     /** rb_str_index_m
      *
      */
     @JRubyMethod(name = "index", reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject index(ThreadContext context, IRubyObject arg0) {
         return indexCommon(context.runtime, context, arg0, 0);
     }
 
     @JRubyMethod(name = "index", reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject index(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         int pos = RubyNumeric.num2int(arg1);
         Ruby runtime = context.runtime;
         if (pos < 0) {
             pos += value.getRealSize();
             if (pos < 0) {
                 if (arg0 instanceof RubyRegexp) {
                     context.getCurrentScope().setBackRef(runtime.getNil());
                 }
                 return runtime.getNil();
             }
         }
         return indexCommon(runtime, context, arg0, pos);
     }
 
     private IRubyObject indexCommon(Ruby runtime, ThreadContext context, IRubyObject sub, int pos) {
         if (sub instanceof RubyRegexp) {
             RubyRegexp regSub = (RubyRegexp) sub;
 
             pos = regSub.adjustStartPos(this, pos, false);
             pos = regSub.search(context, this, pos, false);
         } else if (sub instanceof RubyFixnum) {
             int c_int = RubyNumeric.fix2int((RubyFixnum)sub);
             if (c_int < 0x00 || c_int > 0xFF) {
                 // out of byte range
                 // there will be no match for sure
                 return runtime.getNil();
             }
             byte c = (byte) c_int;
             byte[] bytes = value.getUnsafeBytes();
             int end = value.getBegin() + value.getRealSize();
 
             pos += value.getBegin();
             for (; pos < end; pos++) {
                 if (bytes[pos] == c) return RubyFixnum.newFixnum(runtime, pos - value.getBegin());
             }
             return runtime.getNil();
         } else if (sub instanceof RubyString) {
             pos = strIndex((RubyString) sub, pos);
         } else {
             IRubyObject tmp = sub.checkStringType();
             if (tmp.isNil()) throw runtime.newTypeError("type mismatch: " + sub.getMetaClass().getName() + " given");
             pos = strIndex((RubyString) tmp, pos);
         }
 
         return pos == -1 ? runtime.getNil() : RubyFixnum.newFixnum(runtime, pos);
     }
 
     private int strIndex(RubyString sub, int offset) {
         ByteList byteList = value;
         if (offset < 0) {
             offset += byteList.getRealSize();
             if (offset < 0) return -1;
         }
 
         ByteList other = sub.value;
         if (sizeIsSmaller(byteList, offset, other)) return -1;
         if (other.getRealSize() == 0) return offset;
         return byteList.indexOf(other, offset);
     }
 
     private static boolean sizeIsSmaller(ByteList byteList, int offset, ByteList other) {
         return byteList.getRealSize() - offset < other.getRealSize();
     }
 
     @JRubyMethod(name = "index", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject index19(ThreadContext context, IRubyObject arg0) {
         return indexCommon19(context.runtime, context, arg0, 0);
     }
     
     @JRubyMethod(name = "index", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject index19(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         int pos = RubyNumeric.num2int(arg1);
         Ruby runtime = context.runtime;
         if (pos < 0) {
             pos += strLength();
             if (pos < 0) {
                 if (arg0 instanceof RubyRegexp) context.getCurrentScope().setBackRef(runtime.getNil());
                 return runtime.getNil();
             }
         }
         return indexCommon19(runtime, context, arg0, pos);
     }
 
     private IRubyObject indexCommon19(Ruby runtime, ThreadContext context, IRubyObject sub, int pos) {
         if (sub instanceof RubyRegexp) {
             RubyRegexp regSub = (RubyRegexp) sub;
             pos = singleByteOptimizable() ? pos : 
                     StringSupport.nth(checkEncoding(regSub), value.getUnsafeBytes(), value.getBegin(),
                             value.getBegin() + value.getRealSize(),
                                       pos) - value.getBegin();
             pos = regSub.adjustStartPos19(this, pos, false);
             pos = regSub.search19(context, this, pos, false);
             pos = subLength(pos);
         } else if (sub instanceof RubyString) {
             pos = strIndex19((RubyString) sub, pos);
             pos = subLength(pos);
         } else {
             IRubyObject tmp = sub.checkStringType();
             if (tmp.isNil()) throw runtime.newTypeError("type mismatch: " + sub.getMetaClass().getName() + " given");
             pos = strIndex19((RubyString) tmp, pos);
             pos = subLength(pos);
         }
 
         return pos == -1 ? runtime.getNil() : RubyFixnum.newFixnum(runtime, pos);
     }
 
     private int strIndex19(RubyString sub, int offset) {
         Encoding enc = checkEncoding(sub);
         if (sub.scanForCodeRange() == CR_BROKEN) return -1;
         int len = strLength(enc);
         int slen = sub.strLength(enc);
         if (offset < 0) {
             offset += len;
             if (offset < 0) return -1;
         }
 
         if (len - offset < slen) return -1;
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
         if (offset != 0) {
             offset = singleByteOptimizable() ? offset : StringSupport.offset(enc, bytes, p, end, offset);
             p += offset;
         }
         if (slen == 0) return offset;
 
         while (true) {
             int pos = value.indexOf(sub.value, p - value.getBegin());
             if (pos < 0) return pos;
             pos -= (p - value.getBegin());
             int t = enc.rightAdjustCharHead(bytes, p, p + pos, end);
             if (t == p + pos) return pos + offset;
             if ((len -= t - p) <= 0) return -1;
             offset += t - p;
             p = t;
         }
     }
 
     /** rb_str_rindex_m
      *
      */
     @JRubyMethod(name = "rindex", reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject rindex(ThreadContext context, IRubyObject arg0) {
         return rindexCommon(context.runtime, context, arg0, value.getRealSize());
     }
 
     @JRubyMethod(name = "rindex", reads = BACKREF, writes = BACKREF, compat = RUBY1_8)
     public IRubyObject rindex(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         int pos = RubyNumeric.num2int(arg1);
         Ruby runtime = context.runtime;
         if (pos < 0) {
             pos += value.getRealSize();
             if (pos < 0) {
                 if (arg0 instanceof RubyRegexp) context.getCurrentScope().setBackRef(runtime.getNil());
                 return runtime.getNil();
             }
         }            
         if (pos > value.getRealSize()) pos = value.getRealSize();
         return rindexCommon(runtime, context, arg0, pos);
     }
 
     private IRubyObject rindexCommon(Ruby runtime, ThreadContext context, final IRubyObject sub, int pos) {
         if (sub instanceof RubyRegexp) {
             RubyRegexp regSub = (RubyRegexp) sub;
             if (regSub.length() > 0) {
                 pos = regSub.adjustStartPos(this, pos, true);
                 pos = regSub.search(context, this, pos, true) - value.getBegin();
             }
         } else if (sub instanceof RubyString) {
             pos = strRindex((RubyString) sub, pos);
         } else if (sub instanceof RubyFixnum) {
             int c_int = RubyNumeric.fix2int((RubyFixnum)sub);
             if (c_int < 0x00 || c_int > 0xFF) {
                 // out of byte range
                 // there will be no match for sure
                 return runtime.getNil();
             }
             byte c = (byte) c_int;
 
             byte[] bytes = value.getUnsafeBytes();
             int pbeg = value.getBegin();
             int p = pbeg + pos;
 
             if (pos == value.getRealSize()) {
                 if (pos == 0) return runtime.getNil();
                 --p;
             }
             while (pbeg <= p) {
                 if (bytes[p] == c) return RubyFixnum.newFixnum(runtime, p - value.getBegin());
                 p--;
             }
             return runtime.getNil();
         } else {
             IRubyObject tmp = sub.checkStringType();
             if (tmp.isNil()) throw runtime.newTypeError("type mismatch: " + sub.getMetaClass().getName() + " given");
             pos = strRindex((RubyString) tmp, pos);
         }
         if (pos >= 0) return RubyFixnum.newFixnum(runtime, pos);
         return runtime.getNil();
     }
 
     private int strRindex(RubyString sub, int pos) {
         int subLength = sub.value.getRealSize();
         
         /* substring longer than string */
         if (value.getRealSize() < subLength) return -1;
         if (value.getRealSize() - pos < subLength) pos = value.getRealSize() - subLength;
 
         return value.lastIndexOf(sub.value, pos);
     }
 
     @JRubyMethod(name = "rindex", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject rindex19(ThreadContext context, IRubyObject arg0) {
         return rindexCommon19(context.runtime, context, arg0, strLength());
     }
 
     @JRubyMethod(name = "rindex", reads = BACKREF, writes = BACKREF, compat = RUBY1_9)
     public IRubyObject rindex19(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         int pos = RubyNumeric.num2int(arg1);
         Ruby runtime = context.runtime;
         int length = strLength();
         if (pos < 0) {
             pos += length;
             if (pos < 0) {
                 if (arg0 instanceof RubyRegexp) context.getCurrentScope().setBackRef(runtime.getNil());
                 return runtime.getNil();
             }
         }            
         if (pos > length) pos = length;
         return rindexCommon19(runtime, context, arg0, pos);
     }
 
     private IRubyObject rindexCommon19(Ruby runtime, ThreadContext context, final IRubyObject sub, int pos) {
         if (sub instanceof RubyRegexp) {
             RubyRegexp regSub = (RubyRegexp) sub;
             pos = singleByteOptimizable() ? pos :
                     StringSupport.nth(value.getEncoding(), value.getUnsafeBytes(), value.getBegin(),
                             value.getBegin() + value.getRealSize(),
                                       pos) - value.getBegin();
             if (regSub.length() > 0) {
                 pos = regSub.adjustStartPos19(this, pos, true);
                 pos = regSub.search19(context, this, pos, true);
                 pos = subLength(pos);
             }
         } else if (sub instanceof RubyString) {
             pos = strRindex19((RubyString) sub, pos);
         } else {
             IRubyObject tmp = sub.checkStringType();
             if (tmp.isNil()) throw runtime.newTypeError("type mismatch: " + sub.getMetaClass().getName() + " given");
             pos = strRindex19((RubyString) tmp, pos);
         }
         if (pos >= 0) return RubyFixnum.newFixnum(runtime, pos);
         return runtime.getNil();
     }
 
     private int strRindex19(RubyString sub, int pos) {
         Encoding enc = checkEncoding(sub);
         if (sub.scanForCodeRange() == CR_BROKEN) return -1;
         int len = strLength(enc);
         int slen = sub.strLength(enc);
 
         if (len < slen) return -1;
         if (len - pos < slen) pos = len - slen;
         if (len == 0) return pos;
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
 
         byte[]sbytes = sub.value.getUnsafeBytes();
         int sp = sub.value.getBegin();
         slen = sub.value.getRealSize();
 
         boolean singlebyte = singleByteOptimizable();
         while (true) {
             int s = singlebyte ? p + pos : StringSupport.nth(enc, bytes, p, end, pos);
             if (s == -1) return -1;
             if (ByteList.memcmp(bytes, s, sbytes, sp, slen) == 0) return pos;
             if (pos == 0) return -1;
             pos--;
         }
     }
 
     @Deprecated
     public final IRubyObject substr(int beg, int len) {
         return substr(getRuntime(), beg, len);
     }
 
     /* rb_str_substr */
     public final IRubyObject substr(Ruby runtime, int beg, int len) {    
         int length = value.length();
         if (len < 0 || beg > length) return runtime.getNil();
 
         if (beg < 0) {
             beg += length;
             if (beg < 0) return runtime.getNil();
         }
 
         int end = Math.min(length, beg + len);
         return makeShared(runtime, beg, end - beg);
     }
 
     /* str_byte_substr */
     private IRubyObject byteSubstr(Ruby runtime, int beg, int len) {
         int length = value.length();
         int s = value.getBegin();
         
         if (len < 0 || beg > length) return runtime.getNil();
 
         int p;
         if (beg < 0) {
             beg += length;
             if (beg < 0) return runtime.getNil();
         }
         if (beg + len > length) len = length - beg;
             
         if (len <= 0) {
             len = 0;
             p = 0;
         }
         else {
             p = s + beg;
         }
 
         return makeShared19(runtime, p, len);
     }
 
     /* str_byte_aref */
     private IRubyObject byteARef(Ruby runtime, IRubyObject idx) {
         final int index;
 
         if (idx instanceof RubyRange){
             int[] begLen = ((RubyRange) idx).begLenInt(getByteList().length(), 0);
             return begLen == null ? runtime.getNil() : byteSubstr(runtime, begLen[0], begLen[1]);
         } else if (idx instanceof RubyFixnum) {
             index = RubyNumeric.fix2int((RubyFixnum)idx);
         } else {
             index = RubyNumeric.num2int(idx);
         }
 
         IRubyObject obj = byteSubstr(runtime, index, 1);
         if (obj.isNil() || ((RubyString)obj).getByteList().length() == 0) return runtime.getNil();
         return obj;
     }
             
     public final IRubyObject substr19(Ruby runtime, int beg, int len) {
         if (len < 0) return runtime.getNil();
         int length = value.getRealSize();
         if (length == 0) len = 0; 
 
         Encoding enc = value.getEncoding();
         if (singleByteOptimizable(enc)) {
             if (beg > length) return runtime.getNil();
             if (beg < 0) {
                 beg += length;
                 if (beg < 0) return runtime.getNil();
             }
             if (beg + len > length) len = length - beg;
             if (len <= 0) len = beg = 0;
             return makeShared19(runtime, beg, len);
         } else {
             if (beg + len > length) len = length - beg;
             return multibyteSubstr19(runtime, enc, len, beg, length);
         }
     }
 
     private IRubyObject multibyteSubstr19(Ruby runtime, Encoding enc, int len, int beg, int length) {
         int p;
         int s = value.getBegin();
         int end = s + length;
         byte[]bytes = value.getUnsafeBytes();
 
         if (beg < 0) {
             if (len > -beg) len = -beg;
             if (-beg * enc.maxLength() < length >>> 3) {
                 beg = -beg;
                 int e = end;
                 while (beg-- > len && (e = enc.prevCharHead(bytes, s, e, e)) != -1) {} // nothing
                 p = e;
                 if (p == -1) return runtime.getNil();
                 while (len-- > 0 && (p = enc.prevCharHead(bytes, s, p, e)) != -1) {} // nothing
                 if (p == -1) return runtime.getNil();
                 return makeShared19(runtime, p - s, e - p);
             } else {
                 beg += strLength(enc);
                 if (beg < 0) return runtime.getNil();
             }
diff --git a/src/org/jruby/ast/DNode.java b/src/org/jruby/ast/DNode.java
index 4dc28c531b..f94f494455 100644
--- a/src/org/jruby/ast/DNode.java
+++ b/src/org/jruby/ast/DNode.java
@@ -1,99 +1,103 @@
 package org.jruby.ast;
 
 import org.jcodings.Encoding;
 import org.jruby.Ruby;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.DefinedMessage;
 import org.jruby.util.StringSupport;
 
 /**
  * Base class for all D (e.g. Dynamic) node types like DStrNode, DSymbolNode, etc...
  */
 public abstract class DNode extends ListNode {
     protected Encoding encoding; // If encoding is set then we should obey 1.9 semantics.
 
     public DNode(ISourcePosition position) {
         this(position, null);
     }
 
     public DNode(ISourcePosition position, Encoding encoding) {
         super(position);
 
         this.encoding = encoding;
     }
 
     public Encoding getEncoding() {
         return encoding;
     }
 
     @Override
     public IRubyObject interpret(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         return buildDynamicString(runtime, context, self, aBlock);
     }
 
     // Enebo: Without massive rethink of AST system I think we are stuck with an if check
     public boolean is19() {
         return encoding != null;
     }
 
     public boolean isSameEncoding(StrNode strNode) {
         return strNode.getValue().getEncoding() == encoding;
     }
 
     protected RubyString allocateString(Ruby runtime) {
         RubyString string = RubyString.newString(runtime, new ByteList());
 
         if (is19()) string.setEncoding(encoding);
 
         return string;
     }
 
     public void appendToString(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock, RubyString string, Node node) {
         if (node instanceof StrNode) {
             StrNode strNode = (StrNode)node;
             if (!is19() || isSameEncoding(strNode)) {
                 string.getByteList().append(strNode.getValue());
             } else {
                 string.cat19(strNode.getValue(), strNode.getCodeRange());
             }
         } else if (node instanceof EvStrNode) {
             EvStrNode evStrNode = (EvStrNode)node;
 
             Node bodyNode = evStrNode.getBody();
             if (bodyNode == null) return;
 
             IRubyObject body = bodyNode.interpret(runtime, context, self, aBlock);
-            RuntimeHelpers.shortcutAppend(string, body);
+            if (is19()) {
+                RuntimeHelpers.shortcutAppend(string, body);
+            } else {
+                RuntimeHelpers.shortcutAppend18(string, body);
+            }
         } else if (is19()) {
             string.append19(node.interpret(runtime, context, self, aBlock));
         } else {
             string.append(node.interpret(runtime, context, self, aBlock));
         }
     }
 
     public RubyString buildDynamicString(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         RubyString string = allocateString(runtime);
 
         int size = size();
         for (int i = 0; i < size; i++) {
             appendToString(runtime, context, self, aBlock, string, get(i));
         }
 
         return string;
     }
 
     @Override
     public RubyString definition(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         RubyString definition = super.definition(runtime, context, self, aBlock);
         return is19() && definition == null ? runtime.getDefinedMessage(DefinedMessage.EXPRESSION) : definition;
     }
 }
\ No newline at end of file
diff --git a/src/org/jruby/compiler/ASTCompiler.java b/src/org/jruby/compiler/ASTCompiler.java
index d2e2cfc48c..8b094f677b 100644
--- a/src/org/jruby/compiler/ASTCompiler.java
+++ b/src/org/jruby/compiler/ASTCompiler.java
@@ -1002,2001 +1002,2001 @@ public class ASTCompiler {
                     public void call(BodyCompiler context) {
                         compile(superNode, context, true);
                     }
                 };
         if (superNode == null) {
             superCallback = null;
         }
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         boolean oldIsAtRoot = isAtRoot;
                         isAtRoot = false;
                         if (classNode.getBodyNode() != null) {
                             compile(classNode.getBodyNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                         isAtRoot = oldIsAtRoot;
                     }
                 };
 
         CompilerCallback pathCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (cpathNode instanceof Colon2Node) {
                             Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
                             if (leftNode != null) {
                                 if (leftNode instanceof NilNode) {
                                     context.raiseTypeError("No outer class");
                                 } else {
                                     compile(leftNode, context, true);
                                 }
                             } else {
                                 context.loadNil();
                             }
                         } else if (cpathNode instanceof Colon3Node) {
                             context.loadObject();
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(classNode.getBodyNode());
 
         context.defineClass(classNode.getCPath().getName(), classNode.getScope(), superCallback, pathCallback, bodyCallback, null, inspector);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileSClass(Node node, BodyCompiler context, boolean expr) {
         final SClassNode sclassNode = (SClassNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(sclassNode.getReceiverNode(), context, true);
                     }
                 };
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         boolean oldIsAtRoot = isAtRoot;
                         isAtRoot = false;
                         if (sclassNode.getBodyNode() != null) {
                             compile(sclassNode.getBodyNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                         isAtRoot = oldIsAtRoot;
                     }
                 };
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(sclassNode.getBodyNode());
 
         context.defineClass("SCLASS", sclassNode.getScope(), null, null, bodyCallback, receiverCallback, inspector);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVar(Node node, BodyCompiler context, boolean expr) {
         ClassVarNode classVarNode = (ClassVarNode) node;
 
         context.retrieveClassVariable(classVarNode.getName());
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarAsgn(Node node, BodyCompiler context, boolean expr) {
         final ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(classVarAsgnNode.getValueNode(), context, true);
             }
         };
 
         context.assignClassVariable(classVarAsgnNode.getName(), value);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
 
         context.assignClassVariable(classVarAsgnNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarDecl(Node node, BodyCompiler context, boolean expr) {
         final ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(classVarDeclNode.getValueNode(), context, true);
             }
         };
         
         context.declareClassVariable(classVarDeclNode.getName(), value);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarDeclAssignment(Node node, BodyCompiler context, boolean expr) {
         ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         context.declareClassVariable(classVarDeclNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileConstDecl(Node node, BodyCompiler context, boolean expr) {
         // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
         ConstDeclNode constDeclNode = (ConstDeclNode) node;
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             compile(constDeclNode.getValueNode(), context,true);
 
             context.assignConstantInCurrent(constDeclNode.getName());
         } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
             compile(constDeclNode.getValueNode(), context,true);
             compile(((Colon2Node) constNode).getLeftNode(), context,true);
 
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             compile(constDeclNode.getValueNode(), context,true);
 
             context.assignConstantInObject(constDeclNode.getName());
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileConstDeclAssignment(Node node, BodyCompiler context, boolean expr) {
         // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
         ConstDeclNode constDeclNode = (ConstDeclNode) node;
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             context.assignConstantInCurrent(constDeclNode.getName());
         } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
             compile(((Colon2Node) constNode).getLeftNode(), context,true);
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             context.assignConstantInObject(constDeclNode.getName());
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileConst(Node node, BodyCompiler context, boolean expr) {
         ConstNode constNode = (ConstNode) node;
 
         context.retrieveConstant(constNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
         // XXX: const lookup can trigger const_missing; is that enough to warrant it always being executed?
     }
 
     public void compileColon2(Node node, BodyCompiler context, boolean expr) {
         final Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
         final String name = iVisited.getName();
 
         if (leftNode == null) {
             context.loadObject();
             context.retrieveConstantFromModule(name);
         } else {
             if (node instanceof Colon2ConstNode) {
                 compile(iVisited.getLeftNode(), context, true);
                 context.retrieveConstantFromModule(name);
             } else if (node instanceof Colon2MethodNode) {
                 final CompilerCallback receiverCallback = new CompilerCallback() {
                     public void call(BodyCompiler context) {
                         compile(iVisited.getLeftNode(), context,true);
                     }
                 };
                 
                 context.getInvocationCompiler().invokeDynamic(name, receiverCallback, null, CallType.FUNCTIONAL, null, false);
             } else {
                 compile(iVisited.getLeftNode(), context, true);
             }
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileColon3(Node node, BodyCompiler context, boolean expr) {
         Colon3Node iVisited = (Colon3Node) node;
         String name = iVisited.getName();
 
         context.retrieveConstantFromObject(name);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileGetDefinitionBase(final Node node, BodyCompiler context) {
         switch (node.getNodeType()) {
         case CLASSVARASGNNODE:
         case CLASSVARDECLNODE:
         case CONSTDECLNODE:
         case DASGNNODE:
         case GLOBALASGNNODE:
         case LOCALASGNNODE:
         case MULTIPLEASGNNODE:
         case OPASGNNODE:
         case OPELEMENTASGNNODE:
         case DVARNODE:
         case FALSENODE:
         case TRUENODE:
         case LOCALVARNODE:
         case INSTVARNODE:
         case BACKREFNODE:
         case SELFNODE:
         case VCALLNODE:
         case YIELDNODE:
         case GLOBALVARNODE:
         case CONSTNODE:
         case FCALLNODE:
         case CLASSVARNODE:
         case COLON2NODE:
         case COLON3NODE:
         case CALLNODE:
             // these are all simple cases that don't require the heavier defined logic
             compileGetDefinition(node, context);
             break;
         case NEWLINENODE:
             compileGetDefinitionBase(((NewlineNode)node).getNextNode(), context);
             break;
         default:
             compileGetDefinition(node, context);
         }
     }
 
     public void compileDefined(final Node node, BodyCompiler context, boolean expr) {
         if (expr) {
             compileGetDefinitionBase(((DefinedNode) node).getExpressionNode(), context);
             context.nullToNil();
         }
     }
 
     public void compileGetArgumentDefinition(final Node node, BodyCompiler context, String type) {
         if (node == null) {
             context.pushDefinedMessage(DefinedMessage.byText(type));
         } else if (node instanceof ArrayNode) {
             Object endToken = context.getNewEnding();
             for (int i = 0; i < ((ArrayNode) node).size(); i++) {
                 Node iterNode = ((ArrayNode) node).get(i);
                 compileGetDefinition(iterNode, context);
                 context.ifNull(endToken);
             }
             context.pushDefinedMessage(DefinedMessage.byText(type));
             Object realToken = context.getNewEnding();
             context.go(realToken);
             context.setEnding(endToken);
             context.pushNull();
             context.setEnding(realToken);
         } else {
             compileGetDefinition(node, context);
             Object endToken = context.getNewEnding();
             context.ifNull(endToken);
             context.pushDefinedMessage(DefinedMessage.byText(type));
             Object realToken = context.getNewEnding();
             context.go(realToken);
             context.setEnding(endToken);
             context.pushNull();
             context.setEnding(realToken);
         }
     }
 
     public void compileGetDefinition(final Node node, BodyCompiler context) {
         switch (node.getNodeType()) {
             case CLASSVARASGNNODE:
             case CLASSVARDECLNODE:
             case CONSTDECLNODE:
             case DASGNNODE:
             case GLOBALASGNNODE:
             case LOCALASGNNODE:
             case MULTIPLEASGNNODE:
             case MULTIPLEASGN19NODE:
             case OPASGNNODE:
             case OPASGNANDNODE:
             case OPASGNORNODE:
             case OPELEMENTASGNNODE:
             case INSTASGNNODE: // simple assignment cases
                 context.pushDefinedMessage(DefinedMessage.ASSIGNMENT);
                 break;
             case ANDNODE: // all these just evaluate and then do expression if there's no error
             case ORNODE:
             case DSTRNODE:
             case DREGEXPNODE:
                 compileDefinedAndOrDStrDRegexp(node, context);
                 break;
             case NOTNODE: // evaluates, and under 1.9 flips to "method" if result is nonnull
                 {
                     context.rescue(new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     compile(node, context, false);
                                     context.pushDefinedMessage(DefinedMessage.EXPRESSION);
                                 }
                             }, JumpException.class,
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.pushNull();
                                 }
                             }, RubyString.class);
                     context.definedNot();
                     break;
                 }
             case BACKREFNODE:
                 compileDefinedBackref(node, context);
                 break;
             case DVARNODE:
                 compileDefinedDVar(node, context);
                 break;
             case FALSENODE:
                 context.pushDefinedMessage(DefinedMessage.FALSE);
                 break;
             case TRUENODE:
                 context.pushDefinedMessage(DefinedMessage.TRUE);
                 break;
             case LOCALVARNODE:
                 context.pushDefinedMessage(DefinedMessage.LOCAL_VARIABLE);
                 break;
             case MATCH2NODE:
             case MATCH3NODE:
                 context.pushDefinedMessage(DefinedMessage.METHOD);
                 break;
             case NILNODE:
                 context.pushDefinedMessage(DefinedMessage.NIL);
                 break;
             case NTHREFNODE:
                 compileDefinedNthref(node, context);
                 break;
             case SELFNODE:
                 context.pushDefinedMessage(DefinedMessage.SELF);
                 break;
             case VCALLNODE:
                 context.loadSelf();
                 context.isMethodBound(((VCallNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushDefinedMessage(DefinedMessage.METHOD);
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case YIELDNODE:
                 context.hasBlock(new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushDefinedMessage(DefinedMessage.YIELD);
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case GLOBALVARNODE:
                 context.isGlobalDefined(((GlobalVarNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushDefinedMessage(DefinedMessage.GLOBAL_VARIABLE);
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case INSTVARNODE:
                 context.isInstanceVariableDefined(((InstVarNode) node).getName());
                 break;
             case CONSTNODE:
                 context.isConstantDefined(((ConstNode) node).getName());
                 break;
             case FCALLNODE:
                 context.loadSelf();
                 context.isMethodBound(((FCallNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 compileGetArgumentDefinition(((FCallNode) node).getArgsNode(), context, "method");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case COLON3NODE:
             case COLON2NODE:
                 {
                     final Colon3Node iVisited = (Colon3Node) node;
 
                     final String name = iVisited.getName();
 
                     BranchCallback setup = new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     if (iVisited instanceof Colon2Node) {
                                         final Node leftNode = ((Colon2Node) iVisited).getLeftNode();
                                         compile(leftNode, context,true);
                                     } else {
                                         context.loadObject();
                                     }
                                 }
                             };
                     context.isConstantBranch(setup, name);
                     break;
                 }
             case CALLNODE:
                 compileDefinedCall(node, context);
                 break;
             case CLASSVARNODE:
                 {
                     ClassVarNode iVisited = (ClassVarNode) node;
                     final Object ending = context.getNewEnding();
                     final Object failure = context.getNewEnding();
                     final Object singleton = context.getNewEnding();
                     Object second = context.getNewEnding();
                     Object third = context.getNewEnding();
 
                     context.loadCurrentModule(); //[RubyClass]
                     context.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     context.ifNotNull(second); //[RubyClass]
                     context.consumeCurrentValue(); //[]
                     context.loadSelf(); //[self]
                     context.metaclass(); //[RubyClass]
                     context.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     context.isClassVarDefined(iVisited.getName(),
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.consumeCurrentValue();
                                     context.pushDefinedMessage(DefinedMessage.CLASS_VARIABLE);
                                     context.go(ending);
                                 }
                             },
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                 }
                             });
                     context.setEnding(second);  //[RubyClass]
                     context.duplicateCurrentValue();
                     context.isClassVarDefined(iVisited.getName(),
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.consumeCurrentValue();
                                     context.pushDefinedMessage(DefinedMessage.CLASS_VARIABLE);
                                     context.go(ending);
                                 }
                             },
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                 }
                             });
                     context.setEnding(third); //[RubyClass]
                     context.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     context.ifSingleton(singleton); //[RubyClass]
                     context.consumeCurrentValue();//[]
                     context.go(failure);
                     context.setEnding(singleton);
                     context.attached();//[RubyClass]
                     context.notIsModuleAndClassVarDefined(iVisited.getName(), failure); //[]
                     context.pushDefinedMessage(DefinedMessage.CLASS_VARIABLE);
                     context.go(ending);
                     context.setEnding(failure);
                     context.pushNull();
                     context.setEnding(ending);
                 }
                 break;
             case ZSUPERNODE:
                 {
                     Object fail = context.getNewEnding();
                     Object fail2 = context.getNewEnding();
                     Object fail_easy = context.getNewEnding();
                     Object ending = context.getNewEnding();
 
                     context.getFrameName(); //[String]
                     context.duplicateCurrentValue(); //[String, String]
                     context.ifNull(fail); //[String]
                     context.getFrameKlazz(); //[String, RubyClass]
                     context.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
                     context.ifNull(fail2); //[String, RubyClass]
                     context.superClass();
                     context.ifNotSuperMethodBound(fail_easy);
 
                     context.pushDefinedMessage(DefinedMessage.SUPER);
                     context.go(ending);
 
                     context.setEnding(fail2);
                     context.consumeCurrentValue();
                     context.setEnding(fail);
                     context.consumeCurrentValue();
                     context.setEnding(fail_easy);
                     context.pushNull();
                     context.setEnding(ending);
                 }
                 break;
             case SUPERNODE:
                 {
                     Object fail = context.getNewEnding();
                     Object fail2 = context.getNewEnding();
                     Object fail_easy = context.getNewEnding();
                     Object ending = context.getNewEnding();
 
                     context.getFrameName(); //[String]
                     context.duplicateCurrentValue(); //[String, String]
                     context.ifNull(fail); //[String]
                     context.getFrameKlazz(); //[String, RubyClass]
                     context.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
                     context.ifNull(fail2); //[String, RubyClass]
                     context.superClass();
                     context.ifNotSuperMethodBound(fail_easy);
 
                     compileGetArgumentDefinition(((SuperNode) node).getArgsNode(), context, "super");
                     context.go(ending);
 
                     context.setEnding(fail2);
                     context.consumeCurrentValue();
                     context.setEnding(fail);
                     context.consumeCurrentValue();
                     context.setEnding(fail_easy);
                     context.pushNull();
                     context.setEnding(ending);
                     break;
                 }
             case ATTRASSIGNNODE:
                 {
                     final AttrAssignNode iVisited = (AttrAssignNode) node;
                     Object isnull = context.getNewEnding();
                     Object ending = context.getNewEnding();
                     compileGetDefinition(iVisited.getReceiverNode(), context);
                     context.ifNull(isnull);
 
                     context.rescue(new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     compile(iVisited.getReceiverNode(), context,true); //[IRubyObject]
                                     context.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
                                     context.metaclass(); //[IRubyObject, RubyClass]
                                     context.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
                                     context.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
                                     context.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
                                     final Object isfalse = context.getNewEnding();
                                     Object isreal = context.getNewEnding();
                                     Object ending = context.getNewEnding();
                                     context.isPrivate(isfalse, 3); //[IRubyObject, RubyClass, Visibility]
                                     context.isNotProtected(isreal, 1); //[IRubyObject, RubyClass]
                                     context.selfIsKindOf(isreal); //[IRubyObject]
                                     context.consumeCurrentValue();
                                     context.go(isfalse);
                                     context.setEnding(isreal); //[]
 
                                     context.isMethodBound(iVisited.getName(), new BranchCallback() {
 
                                                 public void branch(BodyCompiler context) {
                                                     compileGetArgumentDefinition(iVisited.getArgsNode(), context, "assignment");
                                                 }
                                             },
                                             new BranchCallback() {
 
                                                 public void branch(BodyCompiler context) {
                                                     context.go(isfalse);
                                                 }
                                             });
                                     context.go(ending);
                                     context.setEnding(isfalse);
                                     context.pushNull();
                                     context.setEnding(ending);
                                 }
                             }, JumpException.class,
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.pushNull();
                                 }
                             }, RubyString.class);
 
                     context.go(ending);
                     context.setEnding(isnull);
                     context.pushNull();
                     context.setEnding(ending);
                     break;
                 }
             default:
                 context.rescue(new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 compile(node, context,true);
                                 context.consumeCurrentValue();
                                 context.pushNull();
                             }
                         }, JumpException.class,
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         }, RubyString.class);
                 context.consumeCurrentValue();
                 context.pushDefinedMessage(DefinedMessage.EXPRESSION);
         }
     }
 
     protected void compileDefinedAndOrDStrDRegexp(final Node node, BodyCompiler context) {
         context.rescue(new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         compile(node, context, false);
                         context.pushDefinedMessage(DefinedMessage.EXPRESSION);
                     }
                 }, JumpException.class,
                 new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.pushNull();
                     }
                 }, RubyString.class);
     }
 
     protected void compileDefinedCall(final Node node, BodyCompiler context) {
             final CallNode iVisited = (CallNode) node;
             Object isnull = context.getNewEnding();
             Object ending = context.getNewEnding();
             compileGetDefinition(iVisited.getReceiverNode(), context);
             context.ifNull(isnull);
 
             context.rescue(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(iVisited.getReceiverNode(), context, true); //[IRubyObject]
                             context.definedCall(iVisited.getName());
                         }
                     }, JumpException.class,
                     new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.pushNull();
                         }
                     }, RubyString.class);
 
             //          context.swapValues();
     //context.consumeCurrentValue();
             context.go(ending);
             context.setEnding(isnull);
             context.pushNull();
             context.setEnding(ending);
     }
 
     protected void compileDefinedDVar(final Node node, BodyCompiler context) {
         context.pushDefinedMessage(DefinedMessage.LOCAL_VARIABLE_IN_BLOCK);
     }
 
     protected void compileDefinedBackref(final Node node, BodyCompiler context) {
         context.backref();
         context.isInstanceOf(RubyMatchData.class,
                 new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.pushDefinedMessage(DefinedMessage.byText("$" + ((BackRefNode) node).getType()));
                     }
                 },
                 new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.pushNull();
                     }
                 });
     }
 
     protected void compileDefinedNthref(final Node node, BodyCompiler context) {
         context.isCaptured(((NthRefNode) node).getMatchNumber(),
                 new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.pushDefinedMessage(DefinedMessage.byText("$" + ((NthRefNode) node).getMatchNumber()));
                     }
                 },
                 new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.pushNull();
                     }
                 });
     }
 
     public void compileDAsgn(Node node, BodyCompiler context, boolean expr) {
         final DAsgnNode dasgnNode = (DAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(dasgnNode.getValueNode(), context, true);
             }
         };
         
         context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth(), value, expr);
     }
 
     public void compileDAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         DAsgnNode dasgnNode = (DAsgnNode) node;
 
         context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth(), expr);
     }
 
     private Node currentBodyNode;
 
     public void compileDefn(Node node, BodyCompiler context, boolean expr) {
         final DefnNode defnNode = (DefnNode) node;
         final ArgsNode argsNode = defnNode.getArgsNode();
 
         CompilerCallback body = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (defnNode.getBodyNode() != null) {
                             Node oldBodyNode = currentBodyNode;
                             currentBodyNode = defnNode.getBodyNode();
                             if (defnNode.getBodyNode() instanceof RescueNode) {
                                 // if root of method is rescue, compile as a light rescue
                                 compileRescueInternal(defnNode.getBodyNode(), context, true);
                             } else {
                                 compile(defnNode.getBodyNode(), context, true);
                             }
                             currentBodyNode = oldBodyNode;
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         CompilerCallback args = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileArgs(argsNode, context, true);
                     }
                 };
 
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         // check args first, since body inspection can depend on args
         inspector.inspect(defnNode.getArgsNode());
 
         // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
         // TODO: this is gross.
         if (defnNode.getBodyNode() instanceof RescueNode) {
             RescueNode rescueNode = (RescueNode)defnNode.getBodyNode();
             inspector.inspect(rescueNode.getBodyNode());
             inspector.inspect(rescueNode.getElseNode());
             inspector.inspect(rescueNode.getRescueNode());
         } else {
             inspector.inspect(defnNode.getBodyNode());
         }
 
         context.defineNewMethod(
                 defnNode.getName(), defnNode.getArgsNode().getArity().getValue(),
                 defnNode.getScope(), body, args, null, inspector, isAtRoot,
                 defnNode.getPosition().getFile(), defnNode.getPosition().getStartLine(),
                 RuntimeHelpers.encodeParameterList(argsNode));
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileDefs(Node node, BodyCompiler context, boolean expr) {
         final DefsNode defsNode = (DefsNode) node;
         final ArgsNode argsNode = defsNode.getArgsNode();
 
         CompilerCallback receiver = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(defsNode.getReceiverNode(), context, true);
                     }
                 };
 
         CompilerCallback body = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (defsNode.getBodyNode() != null) {
                             if (defsNode.getBodyNode() instanceof RescueNode) {
                                 // if root of method is rescue, compile as light rescue
                                 compileRescueInternal(defsNode.getBodyNode(), context, true);
                             } else {
                                 compile(defsNode.getBodyNode(), context, true);
                             }
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         CompilerCallback args = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileArgs(argsNode, context, true);
                     }
                 };
 
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(defsNode.getArgsNode());
 
         // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
         // TODO: this is gross.
         if (defsNode.getBodyNode() instanceof RescueNode) {
             RescueNode rescueNode = (RescueNode)defsNode.getBodyNode();
             inspector.inspect(rescueNode.getBodyNode());
             inspector.inspect(rescueNode.getElseNode());
             inspector.inspect(rescueNode.getRescueNode());
         } else {
             inspector.inspect(defsNode.getBodyNode());
         }
 
         context.defineNewMethod(
                 defsNode.getName(), defsNode.getArgsNode().getArity().getValue(),
                 defsNode.getScope(), body, args, receiver, inspector, false,
                 defsNode.getPosition().getFile(), defsNode.getPosition().getStartLine(),
                 RuntimeHelpers.encodeParameterList(argsNode));
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileArgs(Node node, BodyCompiler context, boolean expr) {
         final ArgsNode argsNode = (ArgsNode) node;
 
         final int required = argsNode.getRequiredArgsCount();
         final int opt = argsNode.getOptionalArgsCount();
         final int rest = argsNode.getRestArg();
 
         ArrayCallback requiredAssignment = null;
         ArrayCallback optionalGiven = null;
         ArrayCallback optionalNotGiven = null;
         CompilerCallback restAssignment = null;
         CompilerCallback blockAssignment = null;
 
         if (required > 0) {
             requiredAssignment = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
                             context.getVariableCompiler().assignLocalVariable(index, false);
                         }
                     };
         }
 
         if (opt > 0) {
             optionalGiven = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             Node optArg = ((ListNode) object).get(index);
 
                             compileAssignment(optArg, context, false);
                         }
                     };
             optionalNotGiven = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             Node optArg = ((ListNode) object).get(index);
 
                             compile(optArg, context, false);
                         }
                     };
         }
 
         if (rest > -1) {
             restAssignment = new CompilerCallback() {
 
                         public void call(BodyCompiler context) {
                             context.getVariableCompiler().assignLocalVariable(argsNode.getRestArg(), false);
                         }
                     };
         }
 
         if (argsNode.getBlock() != null) {
             blockAssignment = new CompilerCallback() {
 
                         public void call(BodyCompiler context) {
                             context.getVariableCompiler().assignLocalVariable(argsNode.getBlock().getCount(), false);
                         }
                     };
         }
 
         context.getVariableCompiler().checkMethodArity(required, opt, rest);
         context.getVariableCompiler().assignMethodArguments(argsNode.getPre(),
                 argsNode.getRequiredArgsCount(),
                 argsNode.getOptArgs(),
                 argsNode.getOptionalArgsCount(),
                 requiredAssignment,
                 optionalGiven,
                 optionalNotGiven,
                 restAssignment,
                 blockAssignment);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileDot(Node node, BodyCompiler context, boolean expr) {
         final DotNode dotNode = (DotNode) node;
 
         if (expr) {
             CompilerCallback beginEndCallback = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     compile(dotNode.getBeginNode(), context, true);
                     compile(dotNode.getEndNode(), context, true);
                 }
             };
 
             context.createNewRange(beginEndCallback, dotNode.isExclusive());
         }
     }
 
     public void compileDRegexp(Node node, BodyCompiler context, boolean expr) {
         final DRegexpNode dregexpNode = (DRegexpNode) node;
 
         CompilerCallback createStringCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         ArrayCallback dstrCallback = new ArrayCallback() {
 
                                     public void nextValue(BodyCompiler context, Object sourceArray,
                                             int index) {
                                         compile(dregexpNode.get(index), context, true);
                                     }
                                 };
                         Encoding enc = null;
                         if (dregexpNode.is19()) {
                             enc = dregexpNode.getEncoding();
                         }
 
                         context.createNewString(dstrCallback, dregexpNode.size(), enc);
                     }
                 };
 
         if (expr) {
             context.createNewRegexp(createStringCallback, dregexpNode.getOptions().toEmbeddedOptions());
         } else {
             // not an expression, only compile the elements
             for (Node nextNode : dregexpNode.childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileDNode(Node node, BodyCompiler context, boolean expr) {
         final DNode dNode = (DNode) node;
 
         ArrayCallback dstrCallback = new ArrayCallback() {
             public void nextValue(BodyCompiler context, Object sourceArray,
                                   int index) {
                 Node nextNode = dNode.get(index);
 
                 switch (nextNode.getNodeType()) {
                     case STRNODE:
                         context.appendByteList(((StrNode) nextNode).getValue(), ((StrNode) nextNode).getCodeRange(), dNode.is19());
                         break;
                     case EVSTRNODE:
                         compile(((EvStrNode)nextNode).getBody(), context, true);
-                        context.shortcutAppend();
+                        context.shortcutAppend(dNode.is19());
                         break;
                     default:
                         compile(nextNode, context, true);
                         context.appendObject(dNode.is19());
                 }
             }
         };
 
         if (expr) {
             Encoding enc = null;
             if (dNode.is19()) {
                 enc = dNode.getEncoding();
             }
 
             context.buildNewString(dstrCallback, dNode.size(), enc);
         } else {
             // not an expression, only compile the elements
             for (Node nextNode : dNode.childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileDStr(Node node, BodyCompiler context, boolean expr) {
         compileDNode(node, context, expr);
     }
 
     public void compileDSymbol(Node node, BodyCompiler context, boolean expr) {
         final DSymbolNode dsymbolNode = (DSymbolNode) node;
 
         compileDNode(dsymbolNode, context, expr);
 
         if (expr) {
             context.stringToSymbol(dsymbolNode.is19());
         }
     }
 
     public void compileDVar(Node node, BodyCompiler context, boolean expr) {
         DVarNode dvarNode = (DVarNode) node;
 
         if (expr) context.getVariableCompiler().retrieveLocalVariable(dvarNode.getIndex(), dvarNode.getDepth());
     }
 
     public void compileDXStr(Node node, BodyCompiler context, boolean expr) {
         final DXStrNode dxstrNode = (DXStrNode) node;
 
         ArgumentsCallback argsCallback = new ArgumentsCallback() {
                     public int getArity() {
                         return 1;
                     }
 
                     public void call(BodyCompiler context) {
                         compileDNode(dxstrNode, context, true);
                     }
                 };
 
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileEnsureNode(Node node, BodyCompiler context, boolean expr) {
         final EnsureNode ensureNode = (EnsureNode) node;
 
         if (ensureNode.getEnsureNode() != null) {
             context.performEnsure(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             if (ensureNode.getBodyNode() != null) {
                                 compile(ensureNode.getBodyNode(), context, true);
                             } else {
                                 context.loadNil();
                             }
                         }
                     },
                     new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(ensureNode.getEnsureNode(), context, false);
                         }
                     });
         } else {
             if (ensureNode.getBodyNode() != null) {
                 compile(ensureNode.getBodyNode(), context,true);
             } else {
                 context.loadNil();
             }
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileEvStr(Node node, BodyCompiler context, boolean expr) {
         final EvStrNode evStrNode = (EvStrNode) node;
 
         compile(evStrNode.getBody(), context,true);
         context.asString();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileFalse(Node node, BodyCompiler context, boolean expr) {
         if (expr) {
             context.loadFalse();
             context.pollThreadEvents();
         }
     }
 
     public void compileFCall(Node node, BodyCompiler context, boolean expr) {
         final FCallNode fcallNode = (FCallNode) node;
 
         ArgumentsCallback argsCallback = getArgsCallback(fcallNode.getArgsNode());
         
         CompilerCallback closureArg = getBlock(fcallNode.getIterNode());
 
         if (fcallNode instanceof SpecialArgs) {
             context.getInvocationCompiler().invokeDynamicVarargs(fcallNode.getName(), null, argsCallback, CallType.FUNCTIONAL, closureArg, fcallNode.getIterNode() instanceof IterNode);
         } else {
             context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, argsCallback, CallType.FUNCTIONAL, closureArg, fcallNode.getIterNode() instanceof IterNode);
         }
         
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     private CompilerCallback getBlock(Node node) {
         if (node == null) {
             return null;
         }
 
         switch (node.getNodeType()) {
             case ITERNODE:
                 final IterNode iterNode = (IterNode) node;
 
                 return new CompilerCallback() {
 
                             public void call(BodyCompiler context) {
                                 compile(iterNode, context,true);
                             }
                         };
             case BLOCKPASSNODE:
                 final BlockPassNode blockPassNode = (BlockPassNode) node;
 
                 return new CompilerCallback() {
 
                             public void call(BodyCompiler context) {
                                 compile(blockPassNode.getBodyNode(), context,true);
                                 context.unwrapPassedBlock();
                             }
                         };
             default:
                 throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
         }
     }
 
     public void compileFixnum(Node node, BodyCompiler context, boolean expr) {
         FixnumNode fixnumNode = (FixnumNode) node;
 
         if (expr) context.createNewFixnum(fixnumNode.getValue());
     }
 
     public void compileFlip(Node node, BodyCompiler context, boolean expr) {
         final FlipNode flipNode = (FlipNode) node;
 
         context.getVariableCompiler().retrieveLocalVariable(flipNode.getIndex(), flipNode.getDepth());
 
         if (flipNode.isExclusive()) {
             context.performBooleanBranch(new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getEndNode(), context,true);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.loadFalse();
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                         }
                     });
                     context.loadTrue();
                 }
             }, new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getBeginNode(), context,true);
                     becomeTrueOrFalse(context);
                     context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), true);
                 }
             });
         } else {
             context.performBooleanBranch(new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getEndNode(), context,true);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.loadFalse();
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                         }
                     });
                     context.loadTrue();
                 }
             }, new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getBeginNode(), context,true);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(flipNode.getEndNode(), context,true);
                             flipTrueOrFalse(context);
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                             context.loadTrue();
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.loadFalse();
                         }
                     });
                 }
             });
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     private void becomeTrueOrFalse(BodyCompiler context) {
         context.performBooleanBranch(new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadTrue();
                     }
                 }, new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadFalse();
                     }
                 });
     }
 
     private void flipTrueOrFalse(BodyCompiler context) {
         context.performBooleanBranch(new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadFalse();
                     }
                 }, new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadTrue();
                     }
                 });
     }
 
     public void compileFloat(Node node, BodyCompiler context, boolean expr) {
         FloatNode floatNode = (FloatNode) node;
 
         if (expr) context.createNewFloat(floatNode.getValue());
     }
 
     public void compileFor(Node node, BodyCompiler context, boolean expr) {
         final ForNode forNode = (ForNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(forNode.getIterNode(), context, true);
                     }
                 };
 
         final CompilerCallback closureArg = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileForIter(forNode, context);
                     }
                 };
 
         context.getInvocationCompiler().invokeDynamic("each", receiverCallback, null, CallType.NORMAL, closureArg, true);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileForIter(Node node, BodyCompiler context) {
         final ForNode forNode = (ForNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (forNode.getBodyNode() != null) {
                             compile(forNode.getBodyNode(), context,true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         // create the closure class and instantiate it
         final CompilerCallback closureArgs = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (forNode.getVarNode() != null) {
                             compileAssignment(forNode.getVarNode(), context, false);
 
                             // consume the block, since for loops can't receive block
                             context.consumeCurrentValue();
                         }
                     }
                 };
 
         boolean hasMultipleArgsHead = false;
         if (forNode.getVarNode() instanceof MultipleAsgnNode) {
             hasMultipleArgsHead = ((MultipleAsgnNode) forNode.getVarNode()).getHeadNode() != null;
         }
 
         NodeType argsNodeId = null;
         if (forNode.getVarNode() != null) {
             argsNodeId = forNode.getVarNode().getNodeType();
         }
         
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(forNode.getBodyNode());
         inspector.inspect(forNode.getVarNode());
 
         // force heap-scope behavior, since it uses parent's scope
         inspector.setFlag(forNode, ASTInspector.CLOSURE);
 
         context.createNewForLoop(Arity.procArityOf(forNode.getVarNode()).getValue(),
                 closureBody, closureArgs, hasMultipleArgsHead, argsNodeId, inspector);
     }
 
     public void compileGlobalAsgn(Node node, BodyCompiler context, boolean expr) {
         final GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(globalAsgnNode.getValueNode(), context, true);
             }
         };
 
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
             case '_':
                 context.getVariableCompiler().assignLastLine(value);
                 break;
             case '~':
                 context.getVariableCompiler().assignBackRef(value);
                 break;
             default:
                 context.assignGlobalVariable(globalAsgnNode.getName(), value);
             }
         } else {
             context.assignGlobalVariable(globalAsgnNode.getName(), value);
         }
 
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileGlobalAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
 
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
             case '_':
                 context.getVariableCompiler().assignLastLine();
                 break;
             case '~':
                 context.getVariableCompiler().assignBackRef();
                 break;
             default:
                 context.assignGlobalVariable(globalAsgnNode.getName());
             }
         } else {
             context.assignGlobalVariable(globalAsgnNode.getName());
         }
         
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileGlobalVar(Node node, BodyCompiler context, boolean expr) {
         GlobalVarNode globalVarNode = (GlobalVarNode) node;
         
         if (expr) {
             if (globalVarNode.getName().length() == 2) {
                 switch (globalVarNode.getName().charAt(1)) {
                 case '_':
                     context.getVariableCompiler().retrieveLastLine();
                     break;
                 case '~':
                     context.getVariableCompiler().retrieveBackRef();
                     break;
                 default:
                     context.retrieveGlobalVariable(globalVarNode.getName());
                 }
             } else {
                 context.retrieveGlobalVariable(globalVarNode.getName());
             }
         }
     }
 
     public void compileHash(Node node, BodyCompiler context, boolean expr) {
         compileHashCommon((HashNode) node, context, expr);
     }
     
     protected void compileHashCommon(HashNode hashNode, BodyCompiler context, boolean expr) {
         if (expr) {
             if (hashNode.getListNode() == null || hashNode.getListNode().size() == 0) {
                 context.createEmptyHash();
                 return;
             }
 
             ArrayCallback hashCallback = new ArrayCallback() {
                 public void nextValue(BodyCompiler context, Object sourceArray,
                         int index) {
                     ListNode listNode = (ListNode) sourceArray;
                     int keyIndex = index * 2;
                     compile(listNode.get(keyIndex), context, true);
                     compile(listNode.get(keyIndex + 1), context, true);
                 }
             };
 
             if (isListAllLiterals(hashNode.getListNode())) {
                 context.createNewLiteralHash(hashNode.getListNode(), hashCallback, hashNode.getListNode().size() / 2);
             } else {
                 context.createNewHash(hashNode.getListNode(), hashCallback, hashNode.getListNode().size() / 2);
             }
         } else {
             for (Node nextNode : hashNode.getListNode().childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
     
     protected void createNewHash(BodyCompiler context, HashNode hashNode, ArrayCallback hashCallback) {
         context.createNewHash(hashNode.getListNode(), hashCallback, hashNode.getListNode().size() / 2);
     }
 
     public void compileIf(Node node, BodyCompiler context, final boolean expr) {
         final IfNode ifNode = (IfNode) node;
 
         // optimizations if we know ahead of time it will always be true or false
         Node actualCondition = ifNode.getCondition();
         while (actualCondition instanceof NewlineNode) {
             actualCondition = ((NewlineNode)actualCondition).getNextNode();
         }
 
         if (actualCondition.getNodeType().alwaysTrue()) {
             // compile condition as non-expr and just compile "then" body
             compile(actualCondition, context, false);
             compile(ifNode.getThenBody(), context, expr);
         } else if (actualCondition.getNodeType().alwaysFalse()) {
             // always false or nil
             compile(ifNode.getElseBody(), context, expr);
         } else {
             BranchCallback trueCallback = new BranchCallback() {
                 public void branch(BodyCompiler context) {
                     if (ifNode.getThenBody() != null) {
                         compile(ifNode.getThenBody(), context, expr);
                     } else {
                         if (expr) context.loadNil();
                     }
                 }
             };
 
             BranchCallback falseCallback = new BranchCallback() {
                 public void branch(BodyCompiler context) {
                     if (ifNode.getElseBody() != null) {
                         compile(ifNode.getElseBody(), context, expr);
                     } else {
                         if (expr) context.loadNil();
                     }
                 }
             };
             
             // normal
             compileCondition(actualCondition, context, true);
             context.performBooleanBranch2(trueCallback, falseCallback);
         }
     }
     
     public void compileCondition(Node node, BodyCompiler context, boolean expr) {
         switch (node.getNodeType()) {
             case CALLNODE:
             {
                 final CallNode callNode = (CallNode)node;
                 if (callNode.getArgsNode() != null) {
                     List<Node> args = callNode.getArgsNode().childNodes();
                     if (args.size() == 1 && args.get(0) instanceof FixnumNode) {
                         final FixnumNode fixnumNode = (FixnumNode)args.get(0);
 
                         if (callNode.getName().equals("<") ||
                                 callNode.getName().equals(">") ||
                                 callNode.getName().equals("<=") ||
                                 callNode.getName().equals(">=") ||
                                 callNode.getName().equals("==")) {
                             context.getInvocationCompiler().invokeBinaryBooleanFixnumRHS(
                                     callNode.getName(),
                                     new CompilerCallback() {
                                         public void call(BodyCompiler context) {
                                             compile(callNode.getReceiverNode(), context, true);
                                         }
                                     },
                                     fixnumNode.getValue());
                             return;
                         }
                     }
                 }
             }
         }
         
         // otherwise normal call
         compile(node, context, expr);
         context.isTrue();
     }
 
     public void compileInstAsgn(Node node, BodyCompiler context, boolean expr) {
         final InstAsgnNode instAsgnNode = (InstAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(instAsgnNode.getValueNode(), context, true);
             }
         };
 
         context.assignInstanceVariable(instAsgnNode.getName(), value);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileInstAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         InstAsgnNode instAsgnNode = (InstAsgnNode) node;
         context.assignInstanceVariable(instAsgnNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileInstVar(Node node, BodyCompiler context, boolean expr) {
         InstVarNode instVarNode = (InstVarNode) node;
 
         if (expr) context.retrieveInstanceVariable(instVarNode.getName());
     }
 
     public void compileIter(Node node, BodyCompiler context) {
         final IterNode iterNode = (IterNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (iterNode.getBodyNode() != null) {
                             compile(iterNode.getBodyNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         // create the closure class and instantiate it
         final CompilerCallback closureArgs = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 if (iterNode.getVarNode() != null) {
                     compileAssignment(iterNode.getVarNode(), context, false);
                 } else {
                     context.consumeCurrentValue();
                 }
 
                 if (iterNode.getBlockVarNode() != null) {
                     compileAssignment(iterNode.getBlockVarNode(), context, false);
                 } else {
                     context.consumeCurrentValue();
                 }
             }
         };
 
         boolean hasMultipleArgsHead = false;
         if (iterNode.getVarNode() instanceof MultipleAsgnNode) {
             hasMultipleArgsHead = ((MultipleAsgnNode) iterNode.getVarNode()).getHeadNode() != null;
         }
 
         NodeType argsNodeId = BlockBody.getArgumentTypeWackyHack(iterNode);
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(iterNode.getBodyNode());
         inspector.inspect(iterNode.getVarNode());
         
         context.createNewClosure(iterNode.getPosition().getFile(), iterNode.getPosition().getStartLine(), iterNode.getScope(), Arity.procArityOf(iterNode.getVarNode()).getValue(),
                 closureBody, closureArgs, hasMultipleArgsHead, argsNodeId, inspector);
     }
 
     public void compileLiteral(LiteralNode literal, BodyCompiler context) {
         context.literal(literal.getName());
     }
 
     public void compileLocalAsgn(Node node, BodyCompiler context, boolean expr) {
         final LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
 
         // just push nil for pragmas
         if (ASTInspector.PRAGMAS.contains(localAsgnNode.getName())) {
             if (expr) context.loadNil();
         } else {
             CompilerCallback value = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     compile(localAsgnNode.getValueNode(), context,true);
                 }
             };
 
             context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth(), value, expr);
         }
     }
 
     public void compileLocalAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         // "assignment" means the value is already on the stack
         LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
 
         context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth(), expr);
     }
 
     public void compileLocalVar(Node node, BodyCompiler context, boolean expr) {
         LocalVarNode localVarNode = (LocalVarNode) node;
 
         if (expr) context.getVariableCompiler().retrieveLocalVariable(localVarNode.getIndex(), localVarNode.getDepth());
     }
 
     public void compileMatch(Node node, BodyCompiler context, boolean expr) {
         MatchNode matchNode = (MatchNode) node;
 
         compile(matchNode.getRegexpNode(), context,true);
 
         context.match(is1_9());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileMatch2(Node node, BodyCompiler context, boolean expr) {
         final Match2Node matchNode = (Match2Node) node;
 
         compile(matchNode.getReceiverNode(), context,true);
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(matchNode.getValueNode(), context,true);
             }
         };
 
         context.match2(value, is1_9());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileMatch3(Node node, BodyCompiler context, boolean expr) {
         Match3Node matchNode = (Match3Node) node;
 
         compile(matchNode.getReceiverNode(), context,true);
         compile(matchNode.getValueNode(), context,true);
 
         context.match3(is1_9());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileModule(Node node, BodyCompiler context, boolean expr) {
         final ModuleNode moduleNode = (ModuleNode) node;
 
         final Node cpathNode = moduleNode.getCPath();
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (moduleNode.getBodyNode() != null) {
                             compile(moduleNode.getBodyNode(), context,true);
                         }
                         context.loadNil();
                     }
                 };
 
         CompilerCallback pathCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (cpathNode instanceof Colon2Node) {
                             Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
                             if (leftNode != null) {
                                 compile(leftNode, context,true);
                             } else {
                                 context.loadNil();
                             }
                         } else if (cpathNode instanceof Colon3Node) {
                             context.loadObject();
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(moduleNode.getBodyNode());
 
         context.defineModule(moduleNode.getCPath().getName(), moduleNode.getScope(), pathCallback, bodyCallback, inspector);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileMultipleAsgn(Node node, BodyCompiler context, boolean expr) {
         MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
 
         if (expr) {
             if (RubyInstanceConfig.FAST_MULTIPLE_ASSIGNMENT) {
                 // optimized version, but expr so return true
                 compileOptimizedMultipleAsgn(multipleAsgnNode, context, false);
                 context.loadTrue();
             } else {
                 // need the array, use unoptz version
                 compileUnoptimizedMultipleAsgn(multipleAsgnNode, context, expr);
             }
         } else {
             // try optz version
             compileOptimizedMultipleAsgn(multipleAsgnNode, context, expr);
         }
     }
 
     private void compileOptimizedMultipleAsgn(MultipleAsgnNode multipleAsgnNode, BodyCompiler context, boolean expr) {
         // expect value to be an array of nodes
         if (multipleAsgnNode.getValueNode() instanceof ArrayNode) {
             // head must not be null and there must be no "args" (like *arg)
             if (multipleAsgnNode.getHeadNode() != null && multipleAsgnNode.getArgsNode() == null) {
                 // sizes must match
                 if (multipleAsgnNode.getHeadNode().size() == ((ArrayNode)multipleAsgnNode.getValueNode()).size()) {
                     // "head" must have no non-trivial assigns (array groupings, basically)
                     boolean normalAssigns = true;
                     for (Node asgn : multipleAsgnNode.getHeadNode().childNodes()) {
                         if (asgn instanceof ListNode) {
                             normalAssigns = false;
                             break;
                         }
                     }
                     
                     if (normalAssigns) {
                         // only supports simple parallel assignment of up to 10 values to the same number of assignees
                         int size = multipleAsgnNode.getHeadNode().size();
                         if (size >= 2 && size <= 10) {
                             ArrayNode values = (ArrayNode)multipleAsgnNode.getValueNode();
                             for (Node value : values.childNodes()) {
                                 compile(value, context, true);
                             }
                             context.reverseValues(size);
                             for (Node asgn : multipleAsgnNode.getHeadNode().childNodes()) {
                                 compileAssignment(asgn, context, false);
                             }
                             return;
                         }
                     }
                 }
             }
         } else {
             // special case for x, *y = whatever
             if (multipleAsgnNode.getHeadNode() != null &&
                     multipleAsgnNode.getHeadNode().size() == 1 &&
                     multipleAsgnNode.getValueNode() instanceof ToAryNode &&
                     multipleAsgnNode.getArgsNode() != null) {
                 // emit the value
                 compile(multipleAsgnNode.getValueNode().childNodes().get(0), context, true);
                 if (multipleAsgnNode.getArgsNode() instanceof StarNode) {
                     // slice puts on stack in reverse order
                     context.preMultiAssign(1, false);
                     // assign
                     compileAssignment(multipleAsgnNode.getHeadNode().childNodes().get(0), context, false);
                 } else {
                     // slice puts on stack in reverse order
                     context.preMultiAssign(1, true);
                     // assign
                     compileAssignment(multipleAsgnNode.getHeadNode().childNodes().get(0), context, false);
                     compileAssignment(multipleAsgnNode.getArgsNode(), context, false);
                 }
                 return;
             }
         }
 
         // if we get here, no optz cases work; fall back on unoptz.
         compileUnoptimizedMultipleAsgn(multipleAsgnNode, context, expr);
     }
 
     private void compileUnoptimizedMultipleAsgn(MultipleAsgnNode multipleAsgnNode, BodyCompiler context, boolean expr) {
         compile(multipleAsgnNode.getValueNode(), context, true);
 
         compileMultipleAsgnAssignment(multipleAsgnNode, context, expr);
     }
 
     public void compileMultipleAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         final MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
 
         // normal items at the "head" of the masgn
         ArrayCallback headAssignCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         ListNode headNode = (ListNode) sourceArray;
                         Node assignNode = headNode.get(index);
 
                         // perform assignment for the next node
                         compileAssignment(assignNode, context, false);
                     }
                 };
 
         CompilerCallback argsCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         Node argsNode = multipleAsgnNode.getArgsNode();
                         if (argsNode instanceof StarNode) {
                             // done processing args
                             context.consumeCurrentValue();
                         } else {
                             // assign to appropriate variable
                             compileAssignment(argsNode, context, false);
                         }
                     }
                 };
 
         if (multipleAsgnNode.getHeadNode() == null) {
             if (multipleAsgnNode.getArgsNode() == null) {
                 throw new NotCompilableException("Something's wrong, multiple assignment with no head or args at: " + multipleAsgnNode.getPosition());
             } else {
                 if (multipleAsgnNode.getArgsNode() instanceof StarNode) {
                     // do nothing
                 } else {
                     context.ensureMultipleAssignableRubyArray(multipleAsgnNode.getHeadNode() != null);
 
                     context.forEachInValueArray(0, 0, null, null, argsCallback);
                 }
             }
         } else {
             context.ensureMultipleAssignableRubyArray(multipleAsgnNode.getHeadNode() != null);
             
             if (multipleAsgnNode.getArgsNode() == null) {
                 context.forEachInValueArray(0, multipleAsgnNode.getHeadNode().size(), multipleAsgnNode.getHeadNode(), headAssignCallback, null);
             } else {
                 context.forEachInValueArray(0, multipleAsgnNode.getHeadNode().size(), multipleAsgnNode.getHeadNode(), headAssignCallback, argsCallback);
             }
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileNewline(Node node, BodyCompiler context, boolean expr) {
         context.lineNumber(node.getPosition());
 
         context.setLinePosition(node.getPosition());
 
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) context.traceLine();
 
         NewlineNode newlineNode = (NewlineNode) node;
 
         compile(newlineNode.getNextNode(), context, expr);
     }
 
     public void compileNext(Node node, BodyCompiler context, boolean expr) {
         final NextNode nextNode = (NextNode) node;
 
         CompilerCallback valueCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (nextNode.getValueNode() != null) {
                             compile(nextNode.getValueNode(), context,true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         context.pollThreadEvents();
         context.issueNextEvent(valueCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileNthRef(Node node, BodyCompiler context, boolean expr) {
         NthRefNode nthRefNode = (NthRefNode) node;
 
         if (expr) context.nthRef(nthRefNode.getMatchNumber());
     }
 
     public void compileNil(Node node, BodyCompiler context, boolean expr) {
         if (expr) {
             context.loadNil();
         }
     }
 
     public void compileNot(Node node, BodyCompiler context, boolean expr) {
         NotNode notNode = (NotNode) node;
 
         compile(notNode.getConditionNode(), context, true);
 
         context.negateCurrentValue();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnAnd(Node node, BodyCompiler context, boolean expr) {
         final BinaryOperatorNode andNode = (BinaryOperatorNode) node;
 
         compile(andNode.getFirstNode(), context,true);
 
         BranchCallback longCallback = new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         compile(andNode.getSecondNode(), context,true);
                     }
                 };
 
         context.performLogicalAnd(longCallback);
         context.pollThreadEvents();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnOr(Node node, BodyCompiler context, boolean expr) {
         final OpAsgnOrNode orNode = (OpAsgnOrNode) node;
 
         if (needsDefinitionCheck(orNode.getFirstNode())) {
             compileGetDefinitionBase(orNode.getFirstNode(), context);
 
             context.isNull(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(orNode.getSecondNode(), context,true);
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(orNode.getFirstNode(), context,true);
                             context.duplicateCurrentValue();
                             context.performBooleanBranch(new BranchCallback() {
 
                                         public void branch(BodyCompiler context) {
                                         //Do nothing
                                         }
                                     },
                                     new BranchCallback() {
 
                                         public void branch(BodyCompiler context) {
                                             context.consumeCurrentValue();
                                             compile(orNode.getSecondNode(), context,true);
                                         }
                                     });
                         }
                     });
         } else {
             compile(orNode.getFirstNode(), context,true);
             context.duplicateCurrentValue();
             context.performBooleanBranch(new BranchCallback() {
                 public void branch(BodyCompiler context) {
                 //Do nothing
                 }
             },
             new BranchCallback() {
                 public void branch(BodyCompiler context) {
                     context.consumeCurrentValue();
                     compile(orNode.getSecondNode(), context,true);
                 }
             });
 
         }
 
         context.pollThreadEvents();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     /**
      * Check whether the given node is considered always "defined" or whether it
      * has some form of definition check.
      *
      * @param node Then node to check
      * @return Whether the type of node represents a possibly undefined construct
      */
     private boolean needsDefinitionCheck(Node node) {
         switch (node.getNodeType()) {
         case CLASSVARASGNNODE:
         case CLASSVARDECLNODE:
         case CONSTDECLNODE:
         case DASGNNODE:
         case GLOBALASGNNODE:
         case LOCALASGNNODE:
         case MULTIPLEASGNNODE:
         case OPASGNNODE:
         case OPELEMENTASGNNODE:
         case DVARNODE:
         case FALSENODE:
         case TRUENODE:
         case LOCALVARNODE:
         case MATCH2NODE:
         case MATCH3NODE:
diff --git a/src/org/jruby/compiler/BodyCompiler.java b/src/org/jruby/compiler/BodyCompiler.java
index 47a308ae5b..9b5e133000 100644
--- a/src/org/jruby/compiler/BodyCompiler.java
+++ b/src/org/jruby/compiler/BodyCompiler.java
@@ -1,711 +1,711 @@
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
 
 package org.jruby.compiler;
 
 import java.util.List;
 import java.util.Map;
 import org.jcodings.Encoding;
 import org.jruby.ast.NodeType;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.util.ByteList;
 import org.jruby.util.DefinedMessage;
 
 /**
  *
  * @author headius
  */
 public interface BodyCompiler {
     /**
      * End compilation for the method associated with the specified token. This should
      * close out all structures created for compilation of the method.
      * 
      * @param token A token identifying the method to be terminated.
      */
     public void endBody();
     
     /**
      * As code executes, values are assumed to be "generated", often by being pushed
      * on to some execution stack. Generally, these values are consumed by other
      * methods on the context, but occasionally a value must be "thrown out". This method
      * provides a way to discard the previous value generated by some other call(s).
      */
     public void consumeCurrentValue();
     
     /**
      * Push a copy the topmost value on the stack.
      */
     public void duplicateCurrentValue();
     
     /**
      * For logging, println the object reference currently atop the stack
      */
     public void aprintln();
     
     /**
      * Swap the top and second values on the stack.
      */
     public void swapValues();
 
     /**
      * Reverse the top n values on the stack.
      *
      * @param n The number of values to reverse.
      */
     public void reverseValues(int n);
     
     /**
      * This method provides a way to specify a line number for the current piece of code
      * being compiled. The compiler may use this information to create debugging
      * information in a bytecode-format-dependent way.
      * 
      * @param position The ISourcePosition information to use.
      */
     public void lineNumber(ISourcePosition position);
     
     public VariableCompiler getVariableCompiler();
     
     public InvocationCompiler getInvocationCompiler();
     
     /**
      * Retrieve the current "self" and put a reference on top of the stack.
      */
     public void retrieveSelf();
     
     /**
      * Retrieve the current "self" object's metaclass and put a reference on top of the stack
      */
     public void retrieveSelfClass();
     
     public void retrieveClassVariable(String name);
     
     public void assignClassVariable(String name);
     
     public void assignClassVariable(String name, CompilerCallback value);
     
     public void declareClassVariable(String name);
     
     public void declareClassVariable(String name, CompilerCallback value);
     
     /**
      * Generate a new "Fixnum" value.
      */
     public void createNewFixnum(long value);
 
     /**
      * Generate a new "Float" value.
      */
     public void createNewFloat(double value);
 
     /**
      * Generate a new "Bignum" value.
      */
     public void createNewBignum(java.math.BigInteger value);
     
     /**
      * Generate a new "String" value.
      */
     public void createNewString(ByteList value, int codeRange);
 
     /**
      * Generate a new dynamic "String" value.
      */
     public void createNewString(ArrayCallback callback, int count, Encoding encoding);
     public void createNewSymbol(ArrayCallback callback, int count, Encoding encoding);
 
     /**
      * Build a string using the given callback. A String will be create at the start,
      * and each iteration is expected to leave a String on the stack.
      */
     public void buildNewString(ArrayCallback callback, int count, Encoding encoding);
 
     /**
      * Append the given bytelist + coderange to the string currently on the stack.
      */
     public void appendByteList(ByteList value, int codeRange, boolean is19);
 
     /**
      * Append the object on stack to the string below it.
      */
     public void appendObject(boolean is19);
 
     /**
      * A "shortcut" append that skips conversions to String where possible.
      * Same stack requirements as appendObject.
      */
-    public void shortcutAppend();
+    public void shortcutAppend(boolean is19);
 
     /**
      * Convert a String on stack to a Symbol
      */
     public void stringToSymbol(boolean is19);
 
     /**
      * Generate a new "Symbol" value (or fetch the existing one).
      */
     public void createNewSymbol(String name);
     
     public void createObjectArray(Object[] elementArray, ArrayCallback callback);
 
     /**
      * Given an aggregated set of objects (likely created through a call to createObjectArray)
      * create a Ruby array object.
      */
     public void createNewArray(boolean lightweight);
 
     /**
      * Construct a Ruby array given an array of objects to feed to an ArrayCallback
      * to construct the elements of the array.
      *
      * @param sourceArray The objects that will be used to construct elements
      * @param callback The callback to which to pass the objects
      * @param lightweight Whether the array should be lightweight
      */
     public void createNewArray(Object[] sourceArray, ArrayCallback callback, boolean lightweight);
 
     /**
      * Construct a Ruby array given an array of objects to feed to an ArrayCallback
      * to construct the elements of the array. All the elements are guaranteed
      * to be literals, so the contents can safely be chunked if too large.
      *
      * @param sourceArray The objects that will be used to construct elements
      * @param callback The callback to which to pass the objects
      * @param lightweight Whether the array should be lightweight
      */
     public void createNewLiteralArray(Object[] sourceArray, ArrayCallback callback, boolean lightweight);
 
     /**
      * Create an empty Ruby array
      */
     public void createEmptyArray();
     
     /**
      * Create an empty Ruby Hash object and put a reference on top of the stack.
      */
     public void createEmptyHash();
     
     /**
      * Create a new hash by calling back to the specified ArrayCallback. It is expected that the keyCount
      * will be the actual count of key/value pairs, and the caller will handle passing an appropriate elements
      * collection in and dealing with the sequential indices passed to the callback.
      * 
      * @param elements An object holding the elements from which to create the Hash.
      * @param callback An ArrayCallback implementation to which the elements array and iteration counts
      * are passed in sequence.
      * @param keyCount the total count of key-value pairs to be constructed from the elements collection.
      */
     public void createNewHash(Object elements, ArrayCallback callback, int keyCount);
 
     /**
      * Create a new hash by calling back to the specified ArrayCallback. It is expected that the keyCount
      * will be the actual count of key/value pairs, and the caller will handle passing an appropriate elements
      * collection in and dealing with the sequential indices passed to the callback. This version expects
      * that all elements will be literals, and will break up the hash construction if it is too large.
      *
      * @param elements An object holding the elements from which to create the Hash.
      * @param callback An ArrayCallback implementation to which the elements array and iteration counts
      * are passed in sequence.
      * @param keyCount the total count of key-value pairs to be constructed from the elements collection.
      */
     public void createNewLiteralHash(Object elements, ArrayCallback callback, int keyCount);
     
     /**
     * @see createNewHash
     *
     * Create new hash running in ruby 1.9 compat version.
     */
     public void createNewHash19(Object elements, ArrayCallback callback, int keyCount);
     
     /**
      * Create a new range. It is expected that the stack will contain the end and begin values for the range as
      * its topmost and second topmost elements.
      * 
      * @param isExclusive Whether the range is exclusive or not (inclusive)
      */
     public void createNewRange(CompilerCallback beginEndCalback, boolean isExclusive);
 
     /**
      * Create a new literal lambda. The stack should contain a reference to the closure object.
      */
     public void createNewLambda(CompilerCallback closure);
     
     /**
      * Perform a boolean branch operation based on the Ruby "true" value of the top value
      * on the stack. If Ruby "true", invoke the true branch callback. Otherwise, invoke the false branch callback.
      * 
      * @param trueBranch The callback for generating code for the "true" condition
      * @param falseBranch The callback for generating code for the "false" condition
      */
     public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch);
     
     /**
      * Perform a boolean branch operation based on the boolean top value
      * on the stack. If true, invoke the true branch callback. Otherwise, invoke the false branch callback.
      * 
      * @param trueBranch The callback for generating code for the "true" condition
      * @param falseBranch The callback for generating code for the "false" condition
      */
     public void performBooleanBranch2(BranchCallback trueBranch, BranchCallback falseBranch);
     
     /**
      * Perform a logical short-circuited Ruby "and" operation, using Ruby notions of true and false.
      * If the value on top of the stack is false, it remains and the branch is not executed. If it is true,
      * the top of the stack is replaced with the result of the branch.
      * 
      * @param longBranch The branch to execute if the "and" operation does not short-circuit.
      */
     public void performLogicalAnd(BranchCallback longBranch);
     
     
     /**
      * Perform a logical short-circuited Ruby "or" operation, using Ruby notions of true and false.
      * If the value on top of the stack is true, it remains and the branch is not executed. If it is false,
      * the top of the stack is replaced with the result of the branch.
      * 
      * @param longBranch The branch to execute if the "or" operation does not short-circuit.
      */
     public void performLogicalOr(BranchCallback longBranch);
     
     /**
      * Perform a boolean loop using the given condition-calculating branch and body branch. For
      * while loops, pass true for checkFirst. For statement-modifier while loops, pass false. For
      * unless loops, reverse the result of the condition after calculating it.
      * 
      * This version ensures the stack is maintained so while results can be used in any context.
      * 
      * @param condition The code to execute for calculating the loop condition. A Ruby true result will
      * cause the body to be executed again.
      * @param body The body to executed for the loop.
      * @param checkFirst whether to check the condition the first time through or not.
      */
     public void performBooleanLoopSafe(BranchCallback condition, BranchCallback body, boolean checkFirst);
     
     /**
      * Perform a boolean loop using the given condition-calculating branch and body branch. For
      * while loops, pass true for checkFirst. For statement-modifier while loops, pass false. For
      * unless loops, reverse the result of the condition after calculating it.
      * 
      * @param condition The code to execute for calculating the loop condition. A Ruby true result will
      * cause the body to be executed again.
      * @param body The body to executed for the loop.
      * @param checkFirst whether to check the condition the first time through or not.
      */
     public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst);
     
     /**
      * Perform a boolean loop using the given condition-calculating branch and body branch. For
      * while loops, pass true for checkFirst. For statement-modifier while loops, pass false. For
      * unless loops, reverse the result of the condition after calculating it.
      * 
      * This version does not handle non-local flow control which can bubble out of
      * eval or closures, and only expects normal flow control to be used within
      * its body.
      * 
      * @param condition The code to execute for calculating the loop condition. A Ruby true result will
      * cause the body to be executed again.
      * @param body The body to executed for the loop.
      * @param checkFirst whether to check the condition the first time through or not.
      */
     public void performBooleanLoopLight(BranchCallback condition, BranchCallback body, boolean checkFirst);
     
     /**
      * Return the current value on the top of the stack, taking into consideration surrounding blocks.
      */
     public void performReturn();
 
     /**
      * Create a new closure (block) using the given lexical scope information, call arity, and
      * body generated by the body callback. The closure will capture containing scopes and related information.
      */
     public void createNewClosure(String file, int line, StaticScope scope, int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector);
 
     /**
      * Create a new closure (block) using the given lexical scope information, call arity, and
      * body generated by the body callback. The closure will capture containing scopes and related information.
      */
     public void createNewClosure19(String file, int line, StaticScope scope, int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, String parameterList, ASTInspector inspector);
     
     /**
      * Create a new closure (block) for a for loop with the given call arity and
      * body generated by the body callback.
      */
     public void createNewForLoop(int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector);
     
     /**
      * Define a new method with the given name, arity, local variable count, and body callback.
      * This will create a new compiled method and bind it to the given name at this point in
      * the program's execution.
      */
     public void defineNewMethod(String name, int methodArity, StaticScope scope,
             CompilerCallback body, CompilerCallback args,
             CompilerCallback receiver, ASTInspector inspector, boolean root,
             String filename, int line, String parameterDesc);
     
     /**
      * Define an alias for a new name to an existing oldName'd method.
      */
     public void defineAlias(CompilerCallback args);
     
     public void assignConstantInCurrent(String name);
     
     public void assignConstantInModule(String name);
     
     public void assignConstantInObject(String name);
     
     /**
      * Retrieve the constant with the specified name available at the current point in the
      * program's execution.
      * 
      * @param name The name of the constant
      */
     public void retrieveConstant(String name);
 
     /**
      * Retreive a named constant from the RubyModule/RubyClass that's just been pushed.
      * 
      * @param name The name of the constant
      */
     public void retrieveConstantFromModule(String name);
 
     /**
      * Retreive a named constant from the RubyModule/RubyClass that's just been pushed.
      *
      * @param name The name of the constant
      */
     public void retrieveConstantFromObject(String name);
     
     /**
      * Load a Ruby "false" value on top of the stack.
      */
     public void loadFalse();
     
     /**
      * Load a Ruby "true" value on top of the stack.
      */
     public void loadTrue();
     
     /**
      * Load a Ruby "nil" value on top of the stack.
      */
     public void loadNil();
     
     public void loadNull();
     
     /**
      * Load the Object class
      */
     public void loadObject();
     
     /**
      * Retrieve the instance variable with the given name, based on the current "self".
      * 
      * @param name The name of the instance variable to retrieve.
      */
     public void retrieveInstanceVariable(String name);
     
     /**
      * Assign the value on top of the stack to the instance variable with the specified name
      * on the current "self". The value is consumed.
      * 
      * @param name The name of the value to assign.
      */
     public void assignInstanceVariable(String name);
     
     /**
      * Assign the value on top of the stack to the instance variable with the specified name
      * on the current "self". The value is consumed.
      * 
      * @param name The name of the value to assign.
      * @param value A callback for compiling the value to assign
      */
     public void assignInstanceVariable(String name, CompilerCallback value);
     
     /**
      * Assign the top of the stack to the global variable with the specified name.
      * 
      * @param name The name of the global variable.
      */
     public void assignGlobalVariable(String name);
     
     /**
      * Assign the top of the stack to the global variable with the specified name.
      * 
      * @param name The name of the global variable.
      * @param value The callback to compile the value to assign
      */
     public void assignGlobalVariable(String name, CompilerCallback value);
     
     /**
      * Retrieve the global variable with the specified name to the top of the stack.
      * 
      * @param name The name of the global variable.
      */
     public void retrieveGlobalVariable(String name);
     
     /**
      * Perform a logical Ruby "not" operation on the value on top of the stack, leaving the
      * negated result.
      */
     public void negateCurrentValue();
     
     /**
      * Convert the current value into a "splatted value" suitable for passing as
      * method arguments or disassembling into multiple variables.
      */
     public void splatCurrentValue(String methodName);
     
     /**
      * Given a splatted value, extract a single value. If no splat or length is
      * zero, use nil
      */
     public void singlifySplattedValue();
 
     /**
      * 1.9 version of singlifySplattedValue.
      */
     public void singlifySplattedValue19();
     
     /**
      * Given an IRubyObject[] on the stack (or otherwise available as the present object)
      * call back to the provided ArrayCallback 'callback' for 'count' elements, starting with 'start'.
      * Each call to callback will have a value from the input array on the stack; once the items are exhausted,
      * the code in nilCallback will be invoked *with no value on the stack*.
      */
     public void forEachInValueArray(int count, int start, Object source, ArrayCallback callback, CompilerCallback argsCallback);
 
     /**
      * Given an IRubyObject[] on the stack (or otherwise available as the present object)
      * call back to the provided ArrayCallback 'callback' for 'count' elements, starting with 'start'.
      * Each call to callback will have a value from the input array on the stack; once the items are exhausted,
      * the code in nilCallback will be invoked *with no value on the stack*.
      */
     public void forEachInValueArray(int count, int preSize, Object preSource, int postSize, Object postSource, ArrayCallback callback, CompilerCallback argsCallback);
     
     /**
      * Ensures that the present value is an IRubyObject[] by wrapping it with one if it is not.
      */
     public void ensureRubyArray();
     
     /**
      * Ensures that the present value is an IRubyObject[] by wrapping it with one or coercing it if it is not.
      */
     public void ensureMultipleAssignableRubyArray(boolean masgnHasHead);
     
     public void issueBreakEvent(CompilerCallback value);
     
     public void issueNextEvent(CompilerCallback value);
     
     public void issueRedoEvent();
     
     public void issueRetryEvent();
 
     public void asString();
 
     public void nthRef(int match);
 
     public void match(boolean is19);
 
     public void match2(CompilerCallback value, boolean is19);
 
     public void match2Capture(CompilerCallback value, int[] scopeOffsets, boolean is19);
 
     public void match3(boolean is19);
 
     public void createNewRegexp(ByteList value, int options);
     public void createNewRegexp(CompilerCallback createStringCallback, int options);
     public void createDRegexp19(ArrayCallback arrayCallback, Object[] sourceArray, int options);
     
     public void pollThreadEvents();
 
     public void literal(String value);
 
     /**
      * Push the current back reference
      */
     public void backref();
     /**
      * Call a static helper method on RubyRegexp with the current backref 
      */
     public void backrefMethod(String methodName);
     
     public void nullToNil();
 
     /**
      * Makes sure that the code in protectedCode will always run after regularCode.
      */
     public void protect(BranchCallback regularCode, BranchCallback protectedCode, Class ret);
     public void rescue(BranchCallback regularCode, Class exception, BranchCallback protectedCode, Class ret);
     public void performRescue(BranchCallback regularCode, BranchCallback rubyCatchCode, BranchCallback rubyElseCode, boolean needsRetry);
     public void performRescueLight(BranchCallback regularCode, BranchCallback rubyCatchCode, BranchCallback rubyElseCode, boolean needsRetry);
     public void performEnsure(BranchCallback regularCode, BranchCallback ensuredCode);
     public void stringOrNil();
     public void pushNull();
     public void pushString(String strVal);
     public void pushByteList(ByteList bl);
     public void pushDefinedMessage(DefinedMessage definedMessage);
     public void isMethodBound(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public void hasBlock(BranchCallback trueBranch, BranchCallback falseBranch);
     public void isGlobalDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public void isConstantDefined(String name);
     public void isInstanceVariableDefined(String name);
     public void isClassVarDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public Object getNewEnding();
     public void ifNull(Object gotoToken);
     public void isNil(BranchCallback trueBranch, BranchCallback falseBranch);
     public void isNull(BranchCallback trueBranch, BranchCallback falseBranch);
     public void ifNotNull(Object gotoToken);
     public void setEnding(Object endingToken);
     public void go(Object gotoToken);
     public void isConstantBranch(BranchCallback setup, String name);
     public void metaclass();
     public void getVisibilityFor(String name);
     public void isPrivate(Object gotoToken, int toConsume);
     public void isNotProtected(Object gotoToken, int toConsume);
     public void selfIsKindOf(Object gotoToken);
     public void loadCurrentModule();
     public void notIsModuleAndClassVarDefined(String name, Object gotoToken);
     public void loadSelf();
     public void ifSingleton(Object gotoToken);
     public void getInstanceVariable(String name);
     public void getFrameName();
     public void getFrameKlazz(); 
     public void superClass();
     public void attached();    
     public void ifNotSuperMethodBound(Object token);
     public void isInstanceOf(Class clazz, BranchCallback trueBranch, BranchCallback falseBranch);
     public void isCaptured(int number, BranchCallback trueBranch, BranchCallback falseBranch);
     public void concatArrays();
     public void appendToArray();
     public void argsCatToArguments();
     public void argsCatToArguments19();
     public void splatToArguments();
     public void splatToArguments19();
     public void convertToJavaArray();
     public void aryToAry();
     public void toJavaString();
     public void aliasGlobal(String newName, String oldName);
     public void undefMethod(CompilerCallback nameArg);
     public void defineClass(String name, StaticScope staticScope, CompilerCallback superCallback, CompilerCallback pathCallback, CompilerCallback bodyCallback, CompilerCallback receiverCallback, ASTInspector inspector);
     public void defineModule(String name, StaticScope staticScope, CompilerCallback pathCallback, CompilerCallback bodyCallback, ASTInspector inspector);
     public void unwrapPassedBlock();
     public void performBackref(char type);
     public void callZSuper(CompilerCallback closure);
     public void appendToObjectArray();
     public void checkIsExceptionHandled(ArgumentsCallback rescueArgs);
     public void rethrowException();
     public void loadClass(String name);
     public void loadStandardError();
     public void unwrapRaiseException();
     public void loadException();
     public void setFilePosition(ISourcePosition position);
     public void setLinePosition(ISourcePosition position);
     public void checkWhenWithSplat();
     public void createNewEndBlock(CompilerCallback body);
     public void runBeginBlock(StaticScope scope, CompilerCallback body);
     public void rethrowIfSystemExit();
 
     public BodyCompiler chainToMethod(String name);
     public BodyCompiler outline(String methodName);
     public void wrapJavaException();
     public void literalSwitch(int[] caseInts, Object[] caseBodies, ArrayCallback casesCallback, CompilerCallback defaultCallback);
     public void typeCheckBranch(Class type, BranchCallback trueCallback, BranchCallback falseCallback);
     public void loadFilename();
 
     /**
      * Store the current live exception object in the $! thread-global.
      */
     public void storeExceptionInErrorInfo();
 
     /**
      * Store the current exception in $!, wrapping in NativeException if necessary.
      */
     public void storeNativeExceptionInErrorInfo();
 
     public void clearErrorInfo();
 
     public void compileSequencedConditional(
             CompilerCallback inputValue,
             FastSwitchType fastSwitchType,
             Map<CompilerCallback, int[]> switchCases,
             List<ArgumentsCallback> conditionals,
             List<CompilerCallback> bodies,
             CompilerCallback fallback);
 
     public void raiseTypeError(String string);
 
     public void traceLine();
     public void traceClass();
     public void traceEnd();
 
     public String getNativeMethodName();
 
     public void preMultiAssign(int head, boolean args);
 
     /**
      * Return true if this method compiled is a "simple" root compiler, i.e.
      * not chained and not a block/closure.
      * 
      * @return
      */
     public boolean isSimpleRoot();
 
     /**
      * Pass two stack elements, the first an array, to the "argsPush" utility method.
      */
     public void argsPush();
 
     /**
      * Pass two stack elements, converting the first to an array, to the "argsCat" utility method.
      */
     public void argsCat();
 
     /**
      * Load the specified encoding.
      */
     public void loadEncoding(Encoding encoding);
 
     /**
      * Check defined?() for a Call
      * @stack: target receiver object
      */
     public void definedCall(String name);
 
     /**
      * Check defined?() for a Not. Under 1.9, returns "method" if the contained
      * logic evaluates ok.
      * 
      * @stack: String defined for contained expression
      */
     public void definedNot();
     
     /**
      * Convert the top IRubyObject value on the stack to a primitive boolean
      * using IRubyObject.isTrue();
      */
     public void isTrue();
 }
diff --git a/src/org/jruby/compiler/impl/BaseBodyCompiler.java b/src/org/jruby/compiler/impl/BaseBodyCompiler.java
index 7b5c5d6146..60163437ed 100644
--- a/src/org/jruby/compiler/impl/BaseBodyCompiler.java
+++ b/src/org/jruby/compiler/impl/BaseBodyCompiler.java
@@ -1,1489 +1,1493 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 package org.jruby.compiler.impl;
 
 import java.io.PrintStream;
 import java.math.BigInteger;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.SortedMap;
 import java.util.TreeMap;
 import org.jcodings.Encoding;
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyHash;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyRange;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ArgumentsCallback;
 import org.jruby.compiler.ArrayCallback;
 import org.jruby.compiler.BranchCallback;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.compiler.InvocationCompiler;
 import org.jruby.compiler.BodyCompiler;
 import org.jruby.compiler.FastSwitchType;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.VariableCompiler;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.ReOptions;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.InstanceVariables;
 import org.jruby.runtime.opto.OptoFactory;
 import org.jruby.util.ByteList;
 import org.jruby.util.DefinedMessage;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.SafePropertyAccessor;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Type;
 import static org.objectweb.asm.Opcodes.*;
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  * BaseBodyCompiler encapsulates all common behavior between BodyCompiler
  * implementations.
  */
 public abstract class BaseBodyCompiler implements BodyCompiler {
     protected SkinnyMethodAdapter method;
     protected VariableCompiler variableCompiler;
     protected InvocationCompiler invocationCompiler;
     protected int argParamCount;
     protected Label[] currentLoopLabels;
     protected Label scopeStart = new Label();
     protected Label scopeEnd = new Label();
     protected Label redoJump;
     protected boolean inNestedMethod = false;
     private int lastLine = -1;
     private int lastPositionLine = -1;
     protected StaticScope scope;
     protected ASTInspector inspector;
     protected String methodName;
     protected String rubyName;
     protected StandardASMCompiler script;
 
     public BaseBodyCompiler(StandardASMCompiler scriptCompiler, String methodName, String rubyName, ASTInspector inspector, StaticScope scope) {
         this.script = scriptCompiler;
         this.scope = scope;
         this.inspector = inspector;
         this.methodName = methodName;
         this.rubyName = rubyName;
         this.argParamCount = getActualArgsCount(scope);
 
         method = new SkinnyMethodAdapter(script.getClassVisitor(), ACC_PUBLIC | ACC_STATIC, methodName, getSignature(), null, null);
 
         createVariableCompiler();
         invocationCompiler = OptoFactory.newInvocationCompiler(this, method);
     }
 
     public String getNativeMethodName() {
         return methodName;
     }
 
     public String getRubyName() {
         return rubyName;
     }
 
     protected boolean shouldUseBoxedArgs(StaticScope scope) {
         return scope.getRestArg() >= 0 || scope.getRestArg() == -2 || scope.getOptionalArgs() > 0 || scope.getRequiredArgs() > 3;
     }
 
     protected int getActualArgsCount(StaticScope scope) {
         if (shouldUseBoxedArgs(scope)) {
             return 1; // use IRubyObject[]
         } else {
             return scope.getRequiredArgs(); // specific arity
         }
     }
 
     protected abstract String getSignature();
 
     protected abstract void createVariableCompiler();
 
     public abstract void beginMethod(CompilerCallback args, StaticScope scope);
 
     public abstract void endBody();
 
     public BodyCompiler chainToMethod(String methodName) {
         BodyCompiler compiler = outline(methodName);
         endBody();
         return compiler;
     }
 
     public void beginChainedMethod() {
         method.start();
 
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
         method.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
         method.astore(getDynamicScopeIndex());
 
         // if more than 4 locals, get the locals array too
         if (scope.getNumberOfVariables() > 4) {
             method.aload(getDynamicScopeIndex());
             method.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             method.astore(getVarsArrayIndex());
         }
 
         // visit a label to start scoping for local vars in this method
         method.label(scopeStart);
     }
 
     public abstract BaseBodyCompiler outline(String methodName);
 
     public StandardASMCompiler getScriptCompiler() {
         return script;
     }
 
     public void lineNumber(ISourcePosition position) {
         int thisLine = position.getStartLine();
 
         // No point in updating number if last number was same value.
         if (thisLine != lastLine) {
             lastLine = thisLine;
         } else {
             return;
         }
 
         Label line = new Label();
         method.label(line);
         method.visitLineNumber(thisLine + 1, line);
     }
 
     public int getLastLine() {
         return lastLine;
     }
 
     public void loadThreadContext() {
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
     }
 
     public void loadSelf() {
         method.aload(StandardASMCompiler.SELF_INDEX);
     }
 
     protected int getClosureIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.CLOSURE_OFFSET;
     }
 
     protected int getPreviousExceptionIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.PREVIOUS_EXCEPTION_OFFSET;
     }
 
     protected int getDynamicScopeIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.DYNAMIC_SCOPE_OFFSET;
     }
 
     protected int getVarsArrayIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.VARS_ARRAY_OFFSET;
     }
 
     protected int getFirstTempIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.FIRST_TEMP_OFFSET;
     }
 
     protected int getExceptionIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.EXCEPTION_OFFSET;
     }
 
     public void loadThis() {
         method.aload(StandardASMCompiler.THIS);
     }
 
     public void loadRuntime() {
         loadThreadContext();
         method.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
     }
 
     public void loadBlock() {
         method.aload(getClosureIndex());
     }
 
     public void loadNil() {
         loadThreadContext();
         method.getfield(p(ThreadContext.class), "nil", ci(IRubyObject.class));
     }
 
     public void loadNull() {
         method.aconst_null();
     }
 
     public void loadObject() {
         loadRuntime();
 
         invokeRuby("getObject", sig(RubyClass.class, params()));
     }
 
     /**
      * This is for utility methods used by the compiler, to reduce the amount of code generation
      * necessary.  All of these live in CompilerHelpers.
      */
     public void invokeUtilityMethod(String methodName, String signature) {
         method.invokestatic(p(RuntimeHelpers.class), methodName, signature);
     }
 
     public void invokeThreadContext(String methodName, String signature) {
         method.invokevirtual(StandardASMCompiler.THREADCONTEXT, methodName, signature);
     }
 
     public void invokeRuby(String methodName, String signature) {
         method.invokevirtual(StandardASMCompiler.RUBY, methodName, signature);
     }
 
     public void invokeIRubyObject(String methodName, String signature) {
         method.invokeinterface(StandardASMCompiler.IRUBYOBJECT, methodName, signature);
     }
 
     public void consumeCurrentValue() {
         method.pop();
     }
 
     public void duplicateCurrentValue() {
         method.dup();
     }
 
     public void swapValues() {
         method.swap();
     }
 
     public void reverseValues(int count) {
         switch (count) {
         case 2:
             method.swap();
             break;
         case 3:
             method.dup_x2();
             method.pop();
             method.swap();
             break;
         case 4:
             method.swap();
             method.dup2_x2();
             method.pop2();
             method.swap();
             break;
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
             // up to ten, stuff into tmp locals, load in reverse order, and assign
             // FIXME: There's probably a slightly smarter way, but is it important?
             int[] tmpLocals = new int[count];
             for (int i = 0; i < count; i++) {
                 tmpLocals[i] = getVariableCompiler().grabTempLocal();
                 getVariableCompiler().setTempLocal(tmpLocals[i]);
             }
             for (int i = 0; i < count; i++) {
                 getVariableCompiler().getTempLocal(tmpLocals[i]);
                 getVariableCompiler().releaseTempLocal();
             }
             break;
         default:
             throw new NotCompilableException("can't reverse more than ten values on the stack");
         }
     }
 
     public void retrieveSelf() {
         loadSelf();
     }
 
     public void retrieveSelfClass() {
         loadSelf();
         metaclass();
     }
 
     public VariableCompiler getVariableCompiler() {
         return variableCompiler;
     }
 
     public InvocationCompiler getInvocationCompiler() {
         return invocationCompiler;
     }
 
     public void assignConstantInCurrent(String name) {
         loadThreadContext();
         method.ldc(name);
         invokeUtilityMethod("setConstantInCurrent", sig(IRubyObject.class, params(IRubyObject.class, ThreadContext.class, String.class)));
     }
 
     public void assignConstantInModule(String name) {
         method.ldc(name);
         loadThreadContext();
         invokeUtilityMethod("setConstantInModule", sig(IRubyObject.class, IRubyObject.class, IRubyObject.class, String.class, ThreadContext.class));
     }
 
     public void assignConstantInObject(String name) {
         // load Object under value
         loadObject();
 
         assignConstantInModule(name);
     }
 
     public void retrieveConstant(String name) {
         script.getCacheCompiler().cacheConstant(this, name);
     }
 
     public void retrieveConstantFromModule(String name) {
         invokeUtilityMethod("checkIsModule", sig(RubyModule.class, IRubyObject.class));
         script.getCacheCompiler().cacheConstantFrom(this, name);
     }
 
     public void retrieveConstantFromObject(String name) {
         loadObject();
         script.getCacheCompiler().cacheConstantFrom(this, name);
     }
 
     public void retrieveClassVariable(String name) {
         loadThreadContext();
         loadRuntime();
         loadSelf();
         method.ldc(name);
 
         invokeUtilityMethod("fastFetchClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class)));
     }
 
     public void assignClassVariable(String name) {
         loadThreadContext();
         method.swap();
         loadRuntime();
         method.swap();
         loadSelf();
         method.swap();
         method.ldc(name);
         method.swap();
 
         invokeUtilityMethod("fastSetClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void assignClassVariable(String name, CompilerCallback value) {
         loadThreadContext();
         loadRuntime();
         loadSelf();
         method.ldc(name);
         value.call(this);
 
         invokeUtilityMethod("fastSetClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void declareClassVariable(String name) {
         loadThreadContext();
         method.swap();
         loadRuntime();
         method.swap();
         loadSelf();
         method.swap();
         method.ldc(name);
         method.swap();
 
         invokeUtilityMethod("fastDeclareClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void declareClassVariable(String name, CompilerCallback value) {
         loadThreadContext();
         loadRuntime();
         loadSelf();
         method.ldc(name);
         value.call(this);
 
         invokeUtilityMethod("fastDeclareClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void createNewFloat(double value) {
         script.getCacheCompiler().cacheFloat(this, value);
     }
 
     public void createNewFixnum(long value) {
         script.getCacheCompiler().cacheFixnum(this, value);
     }
 
     public void createNewBignum(BigInteger value) {
         loadRuntime();
         script.getCacheCompiler().cacheBigInteger(this, value);
         method.invokestatic(p(RubyBignum.class), "newBignum", sig(RubyBignum.class, params(Ruby.class, BigInteger.class)));
     }
 
     public void createNewString(ArrayCallback callback, int count, Encoding encoding) {
         loadRuntime();
         method.ldc(StandardASMCompiler.STARTING_DSTR_FACTOR * count);
         if (encoding == null) {
             method.invokestatic(p(RubyString.class), "newStringLight", sig(RubyString.class, Ruby.class, int.class));
         } else {
             script.getCacheCompiler().cacheEncoding(this, encoding);
             method.invokestatic(p(RubyString.class), "newStringLight", sig(RubyString.class, Ruby.class, int.class, Encoding.class));
         }
 
         for (int i = 0; i < count; i++) {
             callback.nextValue(this, null, i);
             appendObject(encoding != null);
         }
     }
 
     public void buildNewString(ArrayCallback callback, int count, Encoding encoding) {
         loadRuntime();
         method.ldc(StandardASMCompiler.STARTING_DSTR_FACTOR * count);
         if (encoding == null) {
             method.invokestatic(p(RubyString.class), "newStringLight", sig(RubyString.class, Ruby.class, int.class));
         } else {
             script.getCacheCompiler().cacheEncoding(this, encoding);
             method.invokestatic(p(RubyString.class), "newStringLight", sig(RubyString.class, Ruby.class, int.class, Encoding.class));
         }
 
         for (int i = 0; i < count; i++) {
             callback.nextValue(this, null, i);
         }
     }
 
     public void appendByteList(ByteList value, int codeRange, boolean is19) {
         script.getCacheCompiler().cacheByteList(this, value);
         if (is19) {
             method.ldc(codeRange);
             invokeUtilityMethod("appendByteList19", sig(RubyString.class, RubyString.class, ByteList.class, int.class));
         } else {
             invokeUtilityMethod("appendByteList", sig(RubyString.class, RubyString.class, ByteList.class));
         }
     }
 
     public void appendObject(boolean is19) {
         if (is19) {
             method.invokevirtual(p(RubyString.class), "append19", sig(RubyString.class, params(IRubyObject.class)));
         } else {
             method.invokevirtual(p(RubyString.class), "append", sig(RubyString.class, params(IRubyObject.class)));
         }
     }
 
-    public void shortcutAppend() {
-        invokeUtilityMethod("shortcutAppend", sig(RubyString.class, RubyString.class, IRubyObject.class));
+    public void shortcutAppend(boolean is19) {
+        if (is19) {
+            invokeUtilityMethod("shortcutAppend", sig(RubyString.class, RubyString.class, IRubyObject.class));
+        } else {
+            invokeUtilityMethod("shortcutAppend18", sig(RubyString.class, RubyString.class, IRubyObject.class));
+        }
     }
 
     public void stringToSymbol(boolean is19) {
         if (is19) {
             method.invokevirtual(p(RubyString.class), "intern19", sig(RubySymbol.class));
         } else {
             method.invokevirtual(p(RubyString.class), "intern", sig(RubySymbol.class));
         }
     }
 
     public void createNewSymbol(ArrayCallback callback, int count, Encoding encoding) {
         loadRuntime();
         buildNewString(callback, count, encoding);
         toJavaString();
         invokeRuby("newSymbol", sig(RubySymbol.class, params(String.class)));
     }
 
     public void createNewString(ByteList value, int codeRange) {
         script.getCacheCompiler().cacheString(this, value, codeRange);
     }
 
     public void createNewSymbol(String name) {
         script.getCacheCompiler().cacheSymbol(this, name);
     }
 
     public void createNewArray(boolean lightweight) {
         loadRuntime();
         // put under object array already present
         method.swap();
 
         if (lightweight) {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopyLight", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         } else {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopy", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         }
     }
 
     public void createNewArray(Object[] sourceArray, ArrayCallback callback, boolean lightweight) {
         loadRuntime();
 
         buildRubyArray(sourceArray, callback, lightweight);
     }
 
     public void createNewLiteralArray(Object[] sourceArray, ArrayCallback callback, boolean lightweight) {
         buildRubyLiteralArray(sourceArray, callback, lightweight);
     }
 
     public void createEmptyArray() {
         loadRuntime();
 
         invokeRuby("newArray", sig(RubyArray.class));
     }
 
     public void createObjectArray(Object[] sourceArray, ArrayCallback callback) {
         buildObjectArray(StandardASMCompiler.IRUBYOBJECT, sourceArray, callback);
     }
 
     private void buildObjectArray(String type, Object[] sourceArray, ArrayCallback callback) {
         if (sourceArray.length == 0) {
             method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
         } else if (sourceArray.length <= RuntimeHelpers.MAX_SPECIFIC_ARITY_OBJECT_ARRAY) {
             // if we have a specific-arity helper to construct an array for us, use that
             for (int i = 0; i < sourceArray.length; i++) {
                 callback.nextValue(this, sourceArray, i);
             }
             invokeUtilityMethod("constructObjectArray", sig(IRubyObject[].class, params(IRubyObject.class, sourceArray.length)));
         } else {
             // brute force construction inline
             method.pushInt(sourceArray.length);
             method.anewarray(type);
 
             for (int i = 0; i < sourceArray.length; i++) {
                 method.dup();
                 method.pushInt(i);
 
                 callback.nextValue(this, sourceArray, i);
 
                 method.arraystore();
             }
         }
     }
 
     private void buildRubyArray(Object[] sourceArray, ArrayCallback callback, boolean light) {
         if (sourceArray.length == 0) {
             method.invokestatic(p(RubyArray.class), "newEmptyArray", sig(RubyArray.class, Ruby.class));
         } else if (sourceArray.length <= RuntimeHelpers.MAX_SPECIFIC_ARITY_OBJECT_ARRAY) {
             // if we have a specific-arity helper to construct an array for us, use that
             for (int i = 0; i < sourceArray.length; i++) {
                 callback.nextValue(this, sourceArray, i);
             }
             invokeUtilityMethod("constructRubyArray", sig(RubyArray.class, params(Ruby.class, IRubyObject.class, sourceArray.length)));
         } else {
             // brute force construction
             
             // construct array all at once
             method.pushInt(sourceArray.length);
             invokeUtilityMethod("anewarrayIRubyObjects", sig(IRubyObject[].class, int.class));
 
             // iterate over elements, stuffing every ten into array in batches
             int i = 0;
             for (; i < sourceArray.length; i++) {
                 callback.nextValue(this, sourceArray, i);
 
                 if ((i + 1) % 10 == 0) {
                     method.pushInt(i - 9);
                     invokeUtilityMethod("aastoreIRubyObjects", sig(IRubyObject[].class, params(IRubyObject[].class, IRubyObject.class, 10, int.class)));
                 }
             }
             
             // stuff remaining into array
             int remain = i % 10;
             if (remain != 0) {
                 method.pushInt(i - remain);
                 invokeUtilityMethod("aastoreIRubyObjects", sig(IRubyObject[].class, params(IRubyObject[].class, IRubyObject.class, remain, int.class)));
             }
             
             // construct RubyArray wrapper
             if (light) {
                 method.invokestatic(p(RubyArray.class), "newArrayNoCopyLight", sig(RubyArray.class, Ruby.class, IRubyObject[].class));
             } else {
                 method.invokestatic(p(RubyArray.class), "newArrayNoCopy", sig(RubyArray.class, Ruby.class, IRubyObject[].class));
             }
         }
     }
 
     private void buildRubyLiteralArray(Object[] sourceArray, ArrayCallback callback, boolean light) {
         if (sourceArray.length < 100) {
             // don't chunk arrays smaller than 100 elements
             loadRuntime();
             buildRubyArray(sourceArray, callback, light);
         } else {
             // populate the array in a separate series of methods
             SkinnyMethodAdapter oldMethod = method;
 
             // prepare the first builder in the chain
             String newMethodName = "array_builder_" + script.getAndIncrementMethodIndex() + "";
             method = new SkinnyMethodAdapter(
                     script.getClassVisitor(),
                     ACC_PRIVATE | ACC_SYNTHETIC | ACC_STATIC,
                     newMethodName,
                     sig(IRubyObject[].class, "L" + script.getClassname() + ";", ThreadContext.class, IRubyObject[].class),
                     null,
                     null);
             method.start();
 
             for (int i = 0; i < sourceArray.length; i++) {
                 // for every hundred elements, chain to the next call
                 if ((i + 1) % 100 == 0) {
                     String nextName = "array_builder_" + script.getAndIncrementMethodIndex() + "";
 
                     method.aloadMany(0, 1, 2);
                     method.invokestatic(script.getClassname(), nextName,
                             sig(IRubyObject[].class, "L" + script.getClassname() + ";", ThreadContext.class, IRubyObject[].class));
                     method.areturn();
                     method.end();
                     
                     method = new SkinnyMethodAdapter(
                             script.getClassVisitor(),
                             ACC_PRIVATE | ACC_SYNTHETIC | ACC_STATIC,
                             nextName,
                             sig(IRubyObject[].class, "L" + script.getClassname() + ";", ThreadContext.class, IRubyObject[].class),
                             null,
                             null);
                     method.start();
                 }
 
                 method.aload(2);
                 method.pushInt(i);
 
                 callback.nextValue(this, sourceArray, i);
 
                 method.arraystore();
             }
 
             // close out the last method in the chain
             method.aload(2);
             method.areturn();
             method.end();
 
             // restore original method, prepare runtime and array, and invoke the chain
             method = oldMethod;
 
             loadRuntime(); // for newArray* call below
 
             // chain invoke
             method.aload(StandardASMCompiler.THIS);
             method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             method.pushInt(sourceArray.length);
             method.anewarray(p(IRubyObject.class));
             method.invokestatic(script.getClassname(), newMethodName,
                     sig(IRubyObject[].class, "L" + script.getClassname() + ";", ThreadContext.class, IRubyObject[].class));
 
             // array construct
             if (light) {
                 method.invokestatic(p(RubyArray.class), "newArrayNoCopyLight", sig(RubyArray.class, Ruby.class, IRubyObject[].class));
             } else {
                 method.invokestatic(p(RubyArray.class), "newArrayNoCopy", sig(RubyArray.class, Ruby.class, IRubyObject[].class));
             }
         }
     }
 
     public void createEmptyHash() {
         loadRuntime();
 
         method.invokestatic(p(RubyHash.class), "newHash", sig(RubyHash.class, params(Ruby.class)));
     }
 
     public void createNewHash(Object elements, ArrayCallback callback, int keyCount) {
         createNewHashCommon(elements, callback, keyCount, "constructHash", "fastASetCheckString");
     }
 
     public void createNewLiteralHash(Object elements, ArrayCallback callback, int keyCount) {
         createNewLiteralHashCommon(elements, callback, keyCount, "constructHash", "fastASetCheckString");
     }
     
     public void createNewHash19(Object elements, ArrayCallback callback, int keyCount) {
         createNewHashCommon(elements, callback, keyCount, "constructHash19", "fastASetCheckString19");
     }
     
     private void createNewHashCommon(Object elements, ArrayCallback callback, int keyCount,
             String constructorName, String methodName) {
         loadRuntime();
 
         // use specific-arity for as much as possible
         int i = 0;
         for (; i < keyCount && i < RuntimeHelpers.MAX_SPECIFIC_ARITY_HASH; i++) {
             callback.nextValue(this, elements, i);
         }
 
         invokeUtilityMethod(constructorName, sig(RubyHash.class, params(Ruby.class, IRubyObject.class, i * 2)));
 
         for (; i < keyCount; i++) {
             method.dup();
             loadRuntime();
             callback.nextValue(this, elements, i);
             method.invokevirtual(p(RubyHash.class), methodName, sig(void.class, params(Ruby.class, IRubyObject.class, IRubyObject.class)));
         }
     }
 
     private void createNewLiteralHashCommon(Object elements, ArrayCallback callback, int keyCount,
             String constructorName, String methodName) {
         if (keyCount < 50) {
             // small hash, use standard construction
             createNewHashCommon(elements, callback, keyCount, constructorName, methodName);
         } else {
             // populate the hash in a separate series of methods
             SkinnyMethodAdapter oldMethod = method;
 
             // prepare the first builder in the chain
             String builderMethod = "hash_builder_" + script.getAndIncrementMethodIndex() + "";
             method = new SkinnyMethodAdapter(
                     script.getClassVisitor(),
                     ACC_PRIVATE | ACC_SYNTHETIC | ACC_STATIC,
                     builderMethod,
                     sig(RubyHash.class, "L" + script.getClassname() + ";", ThreadContext.class, RubyHash.class),
                     null,
                     null);
             method.start();
 
             for (int i = 0; i < keyCount; i++) {
                 // for every hundred keys, chain to the next call
                 if ((i + 1) % 100 == 0) {
                     String nextName = "hash_builder_" + script.getAndIncrementMethodIndex() + "";
 
                     method.aloadMany(0, 1, 2);
                     method.invokestatic(script.getClassname(), nextName,
                             sig(RubyHash.class, "L" + script.getClassname() + ";", ThreadContext.class, RubyHash.class));
                     method.areturn();
                     method.end();
 
                     method = new SkinnyMethodAdapter(
                             script.getClassVisitor(),
                             ACC_PRIVATE | ACC_SYNTHETIC | ACC_STATIC,
                             nextName,
                             sig(RubyHash.class, "L" + script.getClassname() + ";", ThreadContext.class, RubyHash.class),
                             null,
                             null);
                     method.start();
                 }
 
                 method.aload(2);
                 loadRuntime();
                 callback.nextValue(this, elements, i);
                 method.invokevirtual(p(RubyHash.class), methodName, sig(void.class, params(Ruby.class, IRubyObject.class, IRubyObject.class)));
             }
 
             // close out the last method in the chain
             method.aload(2);
             method.areturn();
             method.end();
 
             // restore original method
             method = oldMethod;
 
             // chain invoke
             method.aload(StandardASMCompiler.THIS);
             method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             loadRuntime();
             method.invokestatic(p(RubyHash.class), "newHash", sig(RubyHash.class, Ruby.class));
             method.invokestatic(script.getClassname(), builderMethod,
                     sig(RubyHash.class, "L" + script.getClassname() + ";", ThreadContext.class, RubyHash.class));
         }
     }
 
     public void createNewRange(CompilerCallback beginEndCallback, boolean isExclusive) {
         loadRuntime();
         loadThreadContext();
         beginEndCallback.call(this);
 
         if (isExclusive) {
             method.invokestatic(p(RubyRange.class), "newExclusiveRange", sig(RubyRange.class, params(Ruby.class, ThreadContext.class, IRubyObject.class, IRubyObject.class)));
         } else {
             method.invokestatic(p(RubyRange.class), "newInclusiveRange", sig(RubyRange.class, params(Ruby.class, ThreadContext.class, IRubyObject.class, IRubyObject.class)));
         }
     }
 
     public void createNewLambda(CompilerCallback closure) {
         loadThreadContext();
         closure.call(this);
         loadSelf();
 
         invokeUtilityMethod("newLiteralLambda", sig(RubyProc.class, ThreadContext.class, Block.class, IRubyObject.class));
     }
 
     /**
      * Invoke IRubyObject.isTrue
      */
     public void isTrue() {
         invokeIRubyObject("isTrue", sig(Boolean.TYPE));
     }
 
     public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch) {
         // call isTrue on the result
         isTrue();
         
         performBooleanBranch2(trueBranch, falseBranch);
     }
 
     public void performBooleanBranch2(BranchCallback trueBranch, BranchCallback falseBranch) {
         Label afterJmp = new Label();
         Label falseJmp = new Label();
 
         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(afterJmp);
 
         // FIXME: optimize for cases where we have no false branch
         method.label(falseJmp);
         falseBranch.branch(this);
 
         method.label(afterJmp);
     }
 
     public void performLogicalAnd(BranchCallback longBranch) {
         Label falseJmp = new Label();
 
         // dup it since we need to return appropriately if it's false
         method.dup();
 
         // call isTrue on the result
         isTrue();
 
         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
         // pop the extra result and replace with the send part of the AND
         method.pop();
         longBranch.branch(this);
         method.label(falseJmp);
     }
 
     public void performLogicalOr(BranchCallback longBranch) {
         // FIXME: after jump is not in here.  Will if ever be?
         //Label afterJmp = new Label();
         Label falseJmp = new Label();
 
         // dup it since we need to return appropriately if it's false
         method.dup();
 
         // call isTrue on the result
         isTrue();
 
         method.ifne(falseJmp); // EQ == 0 (i.e. false)
         // pop the extra result and replace with the send part of the AND
         method.pop();
         longBranch.branch(this);
         method.label(falseJmp);
     }
 
     public void performBooleanLoopSafe(BranchCallback condition, BranchCallback body, boolean checkFirst) {
         String mname = getNewRescueName();
         BaseBodyCompiler nested = outline(mname);
         nested.performBooleanLoopSafeInner(condition, body, checkFirst);
     }
 
     private void performBooleanLoopSafeInner(BranchCallback condition, BranchCallback body, boolean checkFirst) {
         performBooleanLoop(condition, body, checkFirst);
 
         endBody();
     }
 
     public void performBooleanLoop(BranchCallback condition, final BranchCallback body, boolean checkFirst) {
         Label tryBegin = new Label();
         Label tryEnd = new Label();
         Label catchNext = new Label();
         Label catchBreak = new Label();
         Label endOfBody = new Label();
         Label conditionCheck = new Label();
         final Label topOfBody = new Label();
         Label done = new Label();
         Label normalLoopEnd = new Label();
         method.trycatch(tryBegin, tryEnd, catchNext, p(JumpException.NextJump.class));
         method.trycatch(tryBegin, tryEnd, catchBreak, p(JumpException.BreakJump.class));
 
         method.label(tryBegin);
         {
 
             Label[] oldLoopLabels = currentLoopLabels;
 
             currentLoopLabels = new Label[]{endOfBody, topOfBody, done};
 
             // FIXME: if we terminate immediately, this appears to break while in method arguments
             // we need to push a nil for the cases where we will never enter the body
             if (checkFirst) {
                 method.go_to(conditionCheck);
             }
 
             method.label(topOfBody);
 
             Runnable redoBody = new Runnable() { public void run() {
                 Runnable raiseBody = new Runnable() { public void run() {
                     body.branch(BaseBodyCompiler.this);
                 }};
                 Runnable raiseCatch = new Runnable() { public void run() {
                     loadThreadContext();
                     invokeUtilityMethod("unwrapRedoNextBreakOrJustLocalJump", sig(Throwable.class, RaiseException.class, ThreadContext.class));
                     method.athrow();
                 }};
                 method.trycatch(p(RaiseException.class), raiseBody, raiseCatch);
             }};
             Runnable redoCatch = new Runnable() { public void run() {
                 method.pop();
                 method.go_to(topOfBody);
             }};
             method.trycatch(p(JumpException.RedoJump.class), redoBody, redoCatch);
 
             method.label(endOfBody);
 
             // clear body or next result after each successful loop
             method.pop();
 
             method.label(conditionCheck);
 
             // check the condition
             condition.branch(this);
             isTrue();
             method.ifne(topOfBody); // NE == nonzero (i.e. true)
 
             currentLoopLabels = oldLoopLabels;
         }
 
         method.label(tryEnd);
         // skip catch block
         method.go_to(normalLoopEnd);
 
         // catch logic for flow-control: next, break
         {
             // next jump
             {
                 method.label(catchNext);
                 method.pop();
                 // exceptionNext target is for a next that doesn't push a new value, like this one
                 method.go_to(conditionCheck);
             }
 
             // break jump
             {
                 method.label(catchBreak);
                 loadThreadContext();
                 invokeUtilityMethod("breakJumpInWhile", sig(IRubyObject.class, JumpException.BreakJump.class, ThreadContext.class));
                 method.go_to(done);
             }
         }
 
         method.label(normalLoopEnd);
         loadNil();
         method.label(done);
     }
 
     public void performBooleanLoopLight(BranchCallback condition, BranchCallback body, boolean checkFirst) {
         Label endOfBody = new Label();
         Label conditionCheck = new Label();
         Label topOfBody = new Label();
         Label done = new Label();
 
         Label[] oldLoopLabels = currentLoopLabels;
 
         currentLoopLabels = new Label[]{endOfBody, topOfBody, done};
 
         // FIXME: if we terminate immediately, this appears to break while in method arguments
         // we need to push a nil for the cases where we will never enter the body
         if (checkFirst) {
             method.go_to(conditionCheck);
         }
 
         method.label(topOfBody);
 
         body.branch(this);
 
         method.label(endOfBody);
 
         // clear body or next result after each successful loop
         method.pop();
 
         method.label(conditionCheck);
 
         // check the condition
         condition.branch(this);
         isTrue();
         method.ifne(topOfBody); // NE == nonzero (i.e. true)
 
         currentLoopLabels = oldLoopLabels;
 
         loadNil();
         method.label(done);
     }
 
     public void createNewClosure(
             String file,
             int line,
             StaticScope scope,
             int arity,
             CompilerCallback body,
             CompilerCallback args,
             boolean hasMultipleArgsHead,
             NodeType argsNodeId,
             ASTInspector inspector) {
         String blockInMethod = JavaNameMangler.mangleMethodName(rubyName);
         if (rubyName == null || rubyName.length() == 0) {
             blockInMethod = "__block__";
         }
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$" + blockInMethod;
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, rubyName, inspector, scope);
 
         closureCompiler.beginMethod(args, scope);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
 
         loadThreadContext();
         loadSelf();
         script.getCacheCompiler().cacheClosure(this, closureMethodName, arity, scope, file, line, hasMultipleArgsHead, argsNodeId, inspector);
 
         script.addBlockCallbackDescriptor(closureMethodName, file, line);
 
         invokeUtilityMethod("createBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, BlockBody.class)));
     }
 
     public void createNewClosure19(
             String file,
             int line,
             StaticScope scope,
             int arity,
             CompilerCallback body,
             CompilerCallback args,
             boolean hasMultipleArgsHead,
             NodeType argsNodeId,
             String parameterList,
             ASTInspector inspector) {
         String blockInMethod = JavaNameMangler.mangleMethodName(rubyName);
         if (rubyName == null || rubyName.length() == 0) {
             blockInMethod = "__block__";
         }
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$" + blockInMethod;
 
         ChildScopedBodyCompiler19 closureCompiler = new ChildScopedBodyCompiler19(script, closureMethodName, rubyName, inspector, scope);
 
         closureCompiler.beginMethod(args, scope);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
 
         loadThreadContext();
         loadSelf();
         script.getCacheCompiler().cacheClosure19(this, closureMethodName, arity, scope, file, line, hasMultipleArgsHead, argsNodeId, parameterList, inspector);
 
         script.addBlockCallback19Descriptor(closureMethodName, file, line);
 
         invokeUtilityMethod("createBlock19", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, BlockBody.class)));
     }
 
     public void runBeginBlock(StaticScope scope, CompilerCallback body) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__begin__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, rubyName, null, scope);
 
         closureCompiler.beginMethod(null, scope);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
 
         String scopeNames = RuntimeHelpers.encodeScope(scope);
         method.ldc(scopeNames);
 
         script.getCacheCompiler().cacheSpecialClosure(this, closureMethodName);
 
         invokeUtilityMethod("runBeginBlock", sig(IRubyObject.class,
                 params(ThreadContext.class, IRubyObject.class, String.class, CompiledBlockCallback.class)));
     }
 
     public void createNewForLoop(int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__for__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, rubyName, inspector, scope);
 
         closureCompiler.beginMethod(args, null);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
         method.pushInt(arity);
 
         script.getCacheCompiler().cacheSpecialClosure(this, closureMethodName);
 
         method.ldc(Boolean.valueOf(hasMultipleArgsHead));
         method.ldc(BlockBody.asArgumentType(argsNodeId));
 
         invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
     }
 
     public void createNewEndBlock(CompilerCallback body) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__end__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, rubyName, null, scope);
 
         closureCompiler.beginMethod(null, null);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
         method.iconst_0();
 
         script.getCacheCompiler().cacheSpecialClosure(this, closureMethodName);
 
         method.iconst_0(); // false
         method.iconst_0(); // zero
 
         invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
 
         loadRuntime();
         invokeUtilityMethod("registerEndBlock", sig(void.class, Block.class, Ruby.class));
         loadNil();
     }
 
     public void getCompiledClass() {
         method.ldc(Type.getType(script.getClassname()));
     }
 
     public void println() {
         method.dup();
         method.getstatic(p(System.class), "out", ci(PrintStream.class));
         method.swap();
 
         method.invokevirtual(p(PrintStream.class), "println", sig(Void.TYPE, params(Object.class)));
     }
 
     public void defineAlias(CompilerCallback args) {
         loadThreadContext();
         loadSelf();
         args.call(this);
         invokeUtilityMethod("defineAlias", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, Object.class, Object.class));
     }
 
     public void literal(String value) {
         method.ldc(value);
     }
 
     public void loadFalse() {
         // TODO: cache?
         loadRuntime();
         invokeRuby("getFalse", sig(RubyBoolean.class));
     }
 
     public void loadTrue() {
         // TODO: cache?
         loadRuntime();
         invokeRuby("getTrue", sig(RubyBoolean.class));
     }
 
     public void loadCurrentModule() {
         loadThreadContext();
         invokeThreadContext("getCurrentScope", sig(DynamicScope.class));
         method.invokevirtual(p(DynamicScope.class), "getStaticScope", sig(StaticScope.class));
         method.invokevirtual(p(StaticScope.class), "getModule", sig(RubyModule.class));
     }
 
     public void retrieveInstanceVariable(String name) {
         script.getCacheCompiler().cachedGetVariable(this, name);
     }
 
     public void assignInstanceVariable(String name) {
         final int tmp = getVariableCompiler().grabTempLocal();
         getVariableCompiler().setTempLocal(tmp);
         CompilerCallback callback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 context.getVariableCompiler().getTempLocal(tmp);
             }
         };
         script.getCacheCompiler().cachedSetVariable(this, name, callback);
     }
 
     public void assignInstanceVariable(String name, CompilerCallback value) {
         script.getCacheCompiler().cachedSetVariable(this, name, value);
     }
 
     public void retrieveGlobalVariable(String name) {
         loadRuntime();
         method.ldc(name);
         invokeUtilityMethod("getGlobalVariable", sig(IRubyObject.class, Ruby.class, String.class));
     }
 
     public void assignGlobalVariable(String name) {
         loadRuntime();
         method.ldc(name);
         invokeUtilityMethod("setGlobalVariable", sig(IRubyObject.class, IRubyObject.class, Ruby.class, String.class));
     }
 
     public void assignGlobalVariable(String name, CompilerCallback value) {
         value.call(this);
         loadRuntime();
         method.ldc(name);
         invokeUtilityMethod("setGlobalVariable", sig(IRubyObject.class, IRubyObject.class, Ruby.class, String.class));
     }
 
     public void negateCurrentValue() {
         loadRuntime();
         invokeUtilityMethod("negate", sig(IRubyObject.class, IRubyObject.class, Ruby.class));
     }
 
     public void splatCurrentValue(String methodName) {
         method.invokestatic(p(RuntimeHelpers.class), methodName, sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void singlifySplattedValue() {
         method.invokestatic(p(RuntimeHelpers.class), "aValueSplat", sig(IRubyObject.class, params(IRubyObject.class)));
     }
 
     public void singlifySplattedValue19() {
         method.invokestatic(p(RuntimeHelpers.class), "aValueSplat19", sig(IRubyObject.class, params(IRubyObject.class)));
     }
 
     public void aryToAry() {
         method.invokestatic(p(RuntimeHelpers.class), "aryToAry", sig(IRubyObject.class, params(IRubyObject.class)));
     }
 
     public void ensureRubyArray() {
         invokeUtilityMethod("ensureRubyArray", sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void ensureMultipleAssignableRubyArray(boolean masgnHasHead) {
         loadRuntime();
         method.pushBoolean(masgnHasHead);
         invokeUtilityMethod("ensureMultipleAssignableRubyArray", sig(RubyArray.class, params(IRubyObject.class, Ruby.class, boolean.class)));
     }
 
     public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback, CompilerCallback argsCallback) {
         if (start < count || argsCallback != null) {
             int tempLocal = getVariableCompiler().grabTempLocal();
             getVariableCompiler().setTempLocal(tempLocal);
             
             for (; start < count; start++) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 switch (start) {
                 case 0:
                     invokeUtilityMethod("arrayEntryOrNilZero", sig(IRubyObject.class, RubyArray.class));
                     break;
                 case 1:
                     invokeUtilityMethod("arrayEntryOrNilOne", sig(IRubyObject.class, RubyArray.class));
                     break;
                 case 2:
                     invokeUtilityMethod("arrayEntryOrNilTwo", sig(IRubyObject.class, RubyArray.class));
                     break;
                 default:
                     method.pushInt(start);
                     invokeUtilityMethod("arrayEntryOrNil", sig(IRubyObject.class, RubyArray.class, int.class));
                     break;
                 }
                 callback.nextValue(this, source, start);
             }
 
             if (argsCallback != null) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 loadRuntime();
                 method.pushInt(start);
                 invokeUtilityMethod("subarrayOrEmpty", sig(RubyArray.class, RubyArray.class, Ruby.class, int.class));
                 argsCallback.call(this);
             }
 
             getVariableCompiler().getTempLocal(tempLocal);
             getVariableCompiler().releaseTempLocal();
         }
     }
 
     public void forEachInValueArray(int start, int preCount, Object preSource, int postCount, Object postSource, ArrayCallback callback, CompilerCallback argsCallback) {
         if (start < preCount || argsCallback != null) {
             int tempLocal = getVariableCompiler().grabTempLocal();
             getVariableCompiler().setTempLocal(tempLocal);
 
             for (; start < preCount; start++) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 switch (start) {
                 case 0:
                     invokeUtilityMethod("arrayEntryOrNilZero", sig(IRubyObject.class, RubyArray.class));
                     break;
                 case 1:
                     invokeUtilityMethod("arrayEntryOrNilOne", sig(IRubyObject.class, RubyArray.class));
                     break;
                 case 2:
                     invokeUtilityMethod("arrayEntryOrNilTwo", sig(IRubyObject.class, RubyArray.class));
                     break;
                 default:
                     method.pushInt(start);
                     invokeUtilityMethod("arrayEntryOrNil", sig(IRubyObject.class, RubyArray.class, int.class));
                     break;
                 }
                 callback.nextValue(this, preSource, start);
             }
 
             if (argsCallback != null) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 loadRuntime();
                 method.pushInt(start);
                 method.pushInt(postCount);
                 invokeUtilityMethod("subarrayOrEmpty", sig(RubyArray.class, RubyArray.class, Ruby.class, int.class, int.class));
                 argsCallback.call(this);
             }
 
             for (int postStart = 0; postStart < postCount; postStart++) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 method.pushInt(preCount);
                 method.pushInt(postCount);
                 switch (postStart) {
                 case 0:
                     invokeUtilityMethod("arrayPostOrNilZero", sig(IRubyObject.class, RubyArray.class, int.class, int.class));
                     break;
                 case 1:
                     invokeUtilityMethod("arrayPostOrNilOne", sig(IRubyObject.class, RubyArray.class, int.class, int.class));
                     break;
                 case 2:
                     invokeUtilityMethod("arrayPostOrNilTwo", sig(IRubyObject.class, RubyArray.class, int.class, int.class));
                     break;
                 default:
                     method.pushInt(postStart);
                     invokeUtilityMethod("arrayPostOrNil", sig(IRubyObject.class, RubyArray.class, int.class, int.class, int.class));
                     break;
                 }
                 callback.nextValue(this, postSource, postStart);
             }
 
             getVariableCompiler().getTempLocal(tempLocal);
             getVariableCompiler().releaseTempLocal();
         }
     }
 
     public void asString() {
         method.invokeinterface(p(IRubyObject.class), "asString", sig(RubyString.class));
     }
 
     public void toJavaString() {
         method.invokevirtual(p(Object.class), "toString", sig(String.class));
     }
 
     public void nthRef(int match) {
         method.pushInt(match);
         backref();
         method.invokestatic(p(RubyRegexp.class), "nth_match", sig(IRubyObject.class, params(Integer.TYPE, IRubyObject.class)));
     }
 
     public void match(boolean is19) {
         loadThreadContext();
         method.invokevirtual(p(RubyRegexp.class), is19 ? "op_match2_19" : "op_match2", sig(IRubyObject.class, params(ThreadContext.class)));
     }
 
     public void match2(CompilerCallback value, boolean is19) {
         loadThreadContext();
         value.call(this);
         method.invokevirtual(p(RubyRegexp.class), is19 ? "op_match19" : "op_match", sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class)));
     }
 
     public void match2Capture(CompilerCallback value, int[] scopeOffsets, boolean is19) {
         loadThreadContext();
         value.call(this);
         method.ldc(RuntimeHelpers.encodeCaptureOffsets(scopeOffsets));
         invokeUtilityMethod(is19 ? "match2AndUpdateScope19" : "match2AndUpdateScope", sig(IRubyObject.class, params(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class)));
     }
 
     public void match3(boolean is19) {
         loadThreadContext();
         invokeUtilityMethod(is19 ? "match3_19" : "match3", sig(IRubyObject.class, RubyRegexp.class, IRubyObject.class, ThreadContext.class));
     }
 
     public void createNewRegexp(final ByteList value, final int options) {
         script.getCacheCompiler().cacheRegexp(this, value, options);
     }
 
     public void createNewRegexp(CompilerCallback createStringCallback, final int options) {
         boolean onceOnly = (options & ReOptions.RE_OPTION_ONCE) != 0;   // for regular expressions with the /o flag
 
         if (onceOnly) {
             script.getCacheCompiler().cacheDRegexp(this, createStringCallback, options);
         } else {
             loadRuntime();
             createStringCallback.call(this);
             method.pushInt(options);
             method.invokestatic(p(RubyRegexp.class), "newDRegexpEmbedded", sig(RubyRegexp.class, params(Ruby.class, RubyString.class, int.class))); //[reg]
         }
     }
     
     public void createDRegexp19(ArrayCallback arrayCallback, Object[] sourceArray, int options) {
         boolean onceOnly = (options & ReOptions.RE_OPTION_ONCE) != 0;   // for regular expressions with the /o flag
 
         if (onceOnly) {
             script.getCacheCompiler().cacheDRegexp19(this, arrayCallback, sourceArray, options);
         } else {
             loadRuntime();
             createObjectArray(sourceArray, arrayCallback);
             method.ldc(options);
             method.invokestatic(p(RubyRegexp.class), "newDRegexpEmbedded19", sig(RubyRegexp.class, params(Ruby.class, IRubyObject[].class, int.class))); //[reg]
         }
     }
 
     public void pollThreadEvents() {
         if (!RubyInstanceConfig.THREADLESS_COMPILE_ENABLED) {
             loadThreadContext();
             invokeThreadContext("pollThreadEvents", sig(Void.TYPE));
         }
     }
 
     public void nullToNil() {
         loadThreadContext();
         invokeUtilityMethod("nullToNil", sig(IRubyObject.class, IRubyObject.class, ThreadContext.class));
     }
 
     public void isInstanceOf(Class clazz, BranchCallback trueBranch, BranchCallback falseBranch) {
         method.instance_of(p(clazz));
 
         Label falseJmp = new Label();
         Label afterJmp = new Label();
 
         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
 
         method.go_to(afterJmp);
         method.label(falseJmp);
 
         falseBranch.branch(this);
 
         method.label(afterJmp);
     }
 
     public void isCaptured(final int number, final BranchCallback trueBranch, final BranchCallback falseBranch) {
         backref();
         method.dup();
         isInstanceOf(RubyMatchData.class, new BranchCallback() {
 
             public void branch(BodyCompiler context) {
                 method.visitTypeInsn(CHECKCAST, p(RubyMatchData.class));
                 method.pushInt(number);
                 method.invokevirtual(p(RubyMatchData.class), "group", sig(IRubyObject.class, params(int.class)));
                 method.invokeinterface(p(IRubyObject.class), "isNil", sig(boolean.class));
                 Label isNil = new Label();
                 Label after = new Label();
 
                 method.ifne(isNil);
                 trueBranch.branch(context);
                 method.go_to(after);
 
                 method.label(isNil);
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index 0ea5c94931..68fc500c5d 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -1765,1018 +1765,1026 @@ public class RuntimeHelpers {
         }
 
         return value.getRuntime().newArray(value);
     }
 
     public static IRubyObject aValueSplat(IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return value.getRuntime().getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first() : array;
     }
 
     public static IRubyObject aValueSplat19(IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             return value.getRuntime().getNil();
         }
 
         return (RubyArray) value;
     }
 
     public static RubyArray splatValue(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newArray(value);
         }
 
         return arrayValue(value);
     }
 
     public static RubyArray splatValue19(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newEmptyArray();
         }
 
         return arrayValue(value);
     }
     
     public static IRubyObject unsplatValue19(IRubyObject argsResult) {
         if (argsResult instanceof RubyArray) {
             RubyArray array = (RubyArray) argsResult;
                     
             if (array.size() == 1) {
                 IRubyObject newResult = array.eltInternal(0);
                 if (!((newResult instanceof RubyArray) && ((RubyArray) newResult).size() == 0)) {
                     argsResult = newResult;
                 }
             }
         }        
         return argsResult;
     }
 
     public static IRubyObject unsplatValue19IfArityOne(IRubyObject argsResult, Block block) {
         if (block.isGiven() && block.arity().getValue() > 1) argsResult = RuntimeHelpers.unsplatValue19(argsResult);
         return argsResult;
     }
         
     public static IRubyObject[] splatToArguments(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         
         if (value.isNil()) {
             return runtime.getSingleNilArray();
         }
         
         return splatToArgumentsCommon(runtime, value);
     }
     
     public static IRubyObject[] splatToArguments19(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         
         if (value.isNil()) {
             return IRubyObject.NULL_ARRAY;
         }
         
         return splatToArgumentsCommon(runtime, value);
     }
     
     private static IRubyObject[] splatToArgumentsCommon(Ruby runtime, IRubyObject value) {
         
         if (value.isNil()) {
             return runtime.getSingleNilArray();
         }
         
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             return convertSplatToJavaArray(runtime, value);
         }
         return ((RubyArray)tmp).toJavaArrayMaybeUnsafe();
     }
     
     private static IRubyObject[] convertSplatToJavaArray(Ruby runtime, IRubyObject value) {
         // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
         // remove this hack too.
 
         RubyClass metaClass = value.getMetaClass();
         DynamicMethod method = metaClass.searchMethod("to_a");
         if (method.isUndefined() || method.getImplementationClass() == runtime.getKernel()) {
             return new IRubyObject[] {value};
         }
 
         IRubyObject avalue = method.call(runtime.getCurrentContext(), value, metaClass, "to_a");
         if (!(avalue instanceof RubyArray)) {
             if (runtime.is1_9() && avalue.isNil()) {
                 return new IRubyObject[] {value};
             } else {
                 throw runtime.newTypeError("`to_a' did not return Array");
             }
         }
         return ((RubyArray)avalue).toJavaArray();
     }
     
     public static IRubyObject[] argsCatToArguments(IRubyObject[] args, IRubyObject cat) {
         IRubyObject[] ary = splatToArguments(cat);
         return argsCatToArgumentsCommon(args, ary, cat);
     }
     
     public static IRubyObject[] argsCatToArguments19(IRubyObject[] args, IRubyObject cat) {
         IRubyObject[] ary = splatToArguments19(cat);
         return argsCatToArgumentsCommon(args, ary, cat);
     }
     
     private static IRubyObject[] argsCatToArgumentsCommon(IRubyObject[] args, IRubyObject[] ary, IRubyObject cat) {
         if (ary.length > 0) {
             IRubyObject[] newArgs = new IRubyObject[args.length + ary.length];
             System.arraycopy(args, 0, newArgs, 0, args.length);
             System.arraycopy(ary, 0, newArgs, args.length, ary.length);
             args = newArgs;
         }
         
         return args;
     }
 
     public static void addInstanceMethod(RubyModule containingClass, String name, DynamicMethod method, Visibility visibility, ThreadContext context, Ruby runtime) {
         containingClass.addMethod(name, method);
 
         RubySymbol sym = runtime.fastNewSymbol(name);
         if (visibility == Visibility.MODULE_FUNCTION) {
             addModuleMethod(containingClass, name, method, context, sym);
         }
 
         callNormalMethodHook(containingClass, context, sym);
     }
 
     private static void addModuleMethod(RubyModule containingClass, String name, DynamicMethod method, ThreadContext context, RubySymbol sym) {
         containingClass.getSingletonClass().addMethod(name, new WrapperMethod(containingClass.getSingletonClass(), method, Visibility.PUBLIC));
         containingClass.callMethod(context, "singleton_method_added", sym);
     }
 
     private static void callNormalMethodHook(RubyModule containingClass, ThreadContext context, RubySymbol name) {
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             callSingletonMethodHook(((MetaClass) containingClass).getAttached(), context, name);
         } else {
             containingClass.callMethod(context, "method_added", name);
         }
     }
 
     private static void callSingletonMethodHook(IRubyObject receiver, ThreadContext context, RubySymbol name) {
         receiver.callMethod(context, "singleton_method_added", name);
     }
 
     private static DynamicMethod constructNormalMethod(
             MethodFactory factory,
             String javaName,
             String name,
             RubyModule containingClass,
             ISourcePosition position,
             int arity,
             StaticScope scope,
             Visibility visibility,
             Object scriptObject,
             CallConfiguration callConfig,
             String parameterDesc) {
         
         DynamicMethod method;
 
         if (name.equals("initialize") || name.equals("initialize_copy") || visibility == Visibility.MODULE_FUNCTION) {
             visibility = Visibility.PRIVATE;
         }
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             method = factory.getCompiledMethodLazily(
                     containingClass,
                     javaName,
                     Arity.createArity(arity),
                     visibility,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         } else {
             method = factory.getCompiledMethod(
                     containingClass,
                     javaName,
                     Arity.createArity(arity),
                     visibility,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         }
 
         return method;
     }
 
     private static DynamicMethod constructSingletonMethod(
             MethodFactory factory,
             String javaName,
             RubyClass rubyClass,
             ISourcePosition position,
             int arity,
             StaticScope scope,
             Object scriptObject,
             CallConfiguration callConfig,
             String parameterDesc) {
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             return factory.getCompiledMethodLazily(
                     rubyClass,
                     javaName,
                     Arity.createArity(arity),
                     Visibility.PUBLIC,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         } else {
             return factory.getCompiledMethod(
                     rubyClass,
                     javaName,
                     Arity.createArity(arity),
                     Visibility.PUBLIC,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         }
     }
 
     public static String encodeScope(StaticScope scope) {
         StringBuilder namesBuilder = new StringBuilder();
 
         boolean first = true;
         for (String name : scope.getVariables()) {
             if (!first) namesBuilder.append(';');
             first = false;
             namesBuilder.append(name);
         }
         namesBuilder
                 .append(',')
                 .append(scope.getRequiredArgs())
                 .append(',')
                 .append(scope.getOptionalArgs())
                 .append(',')
                 .append(scope.getRestArg());
 
         return namesBuilder.toString();
     }
 
     public static StaticScope decodeRootScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         StaticScope scope = context.runtime.getStaticScopeFactory().newLocalScope(null, decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     public static StaticScope decodeLocalScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         StaticScope scope = context.runtime.getStaticScopeFactory().newLocalScope(context.getCurrentScope().getStaticScope(), decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     public static StaticScope decodeLocalScope(ThreadContext context, StaticScope parent, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         StaticScope scope = context.runtime.getStaticScopeFactory().newLocalScope(parent, decodedScope[1]);
         scope.determineModule();
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     public static StaticScope decodeBlockScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         StaticScope scope = context.runtime.getStaticScopeFactory().newBlockScope(context.getCurrentScope().getStaticScope(), decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     private static String[][] decodeScopeDescriptor(String scopeString) {
         String[] scopeElements = scopeString.split(",");
         String[] scopeNames = scopeElements[0].length() == 0 ? new String[0] : getScopeNames(scopeElements[0]);
         return new String[][] {scopeElements, scopeNames};
     }
 
     private static void setAritiesFromDecodedScope(StaticScope scope, String[] scopeElements) {
         scope.setArities(Integer.parseInt(scopeElements[1]), Integer.parseInt(scopeElements[2]), Integer.parseInt(scopeElements[3]));
     }
 
     public static StaticScope createScopeForClass(ThreadContext context, String scopeString) {
         StaticScope scope = decodeLocalScope(context, scopeString);
         scope.determineModule();
 
         return scope;
     }
 
     private static void performNormalMethodChecks(RubyModule containingClass, Ruby runtime, String name) throws RaiseException {
 
         if (containingClass == runtime.getDummy()) {
             throw runtime.newTypeError("no class/module to add method");
         }
 
         if (containingClass == runtime.getObject() && name.equals("initialize")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining Object#initialize may cause infinite loop");
         }
 
         if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining `" + name + "' may cause serious problem");
         }
     }
 
     private static RubyClass performSingletonMethodChecks(Ruby runtime, IRubyObject receiver, String name) throws RaiseException {
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("can't define singleton method \"" + name + "\" for " + receiver.getMetaClass().getBaseName());
         }
 
         if (receiver.isFrozen()) {
             throw runtime.newFrozenError("object");
         }
         
         RubyClass rubyClass = receiver.getSingletonClass();
         
         return rubyClass;
     }
     
     public static IRubyObject arrayEntryOrNil(RubyArray array, int index) {
         if (index < array.getLength()) {
             return array.eltInternal(index);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilZero(RubyArray array) {
         if (0 < array.getLength()) {
             return array.eltInternal(0);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilOne(RubyArray array) {
         if (1 < array.getLength()) {
             return array.eltInternal(1);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilTwo(RubyArray array) {
         if (2 < array.getLength()) {
             return array.eltInternal(2);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNil(RubyArray array, int pre, int post, int index) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + index);
         } else if (pre + index < array.getLength()) {
             return array.eltInternal(pre + index);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilZero(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 0);
         } else if (pre + 0 < array.getLength()) {
             return array.eltInternal(pre + 0);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilOne(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 1);
         } else if (pre + 1 < array.getLength()) {
             return array.eltInternal(pre + 1);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilTwo(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 2);
         } else if (pre + 2 < array.getLength()) {
             return array.eltInternal(pre + 2);
         } else {
             return array.getRuntime().getNil();
         }
     }
     
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index) {
         if (index < array.getLength()) {
             return createSubarray(array, index);
         } else {
             return RubyArray.newEmptyArray(runtime);
         }
     }
 
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index, int post) {
         if (index + post < array.getLength()) {
             return createSubarray(array, index, post);
         } else {
             return RubyArray.newEmptyArray(runtime);
         }
     }
     
     public static RubyModule checkIsModule(IRubyObject maybeModule) {
         if (maybeModule instanceof RubyModule) return (RubyModule)maybeModule;
         
         throw maybeModule.getRuntime().newTypeError(maybeModule + " is not a class/module");
     }
     
     public static IRubyObject getGlobalVariable(Ruby runtime, String name) {
         return runtime.getGlobalVariables().get(name);
     }
     
     public static IRubyObject setGlobalVariable(IRubyObject value, Ruby runtime, String name) {
         return runtime.getGlobalVariables().set(name, value);
     }
 
     public static IRubyObject getInstanceVariable(IRubyObject self, Ruby runtime, String internedName) {
         IRubyObject result = self.getInstanceVariables().getInstanceVariable(internedName);
         if (result != null) return result;
         if (runtime.isVerbose()) warnAboutUninitializedIvar(runtime, internedName);
         return runtime.getNil();
     }
 
     private static void warnAboutUninitializedIvar(Ruby runtime, String internedName) {
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + internedName + " not initialized");
     }
 
     public static IRubyObject setInstanceVariable(IRubyObject value, IRubyObject self, String name) {
         return self.getInstanceVariables().setInstanceVariable(name, value);
     }
 
     public static RubyProc newLiteralLambda(ThreadContext context, Block block, IRubyObject self) {
         return RubyProc.newProc(context.runtime, block, Block.Type.LAMBDA);
     }
 
     public static void fillNil(IRubyObject[]arr, int from, int to, Ruby runtime) {
         IRubyObject nils[] = runtime.getNilPrefilledArray();
         int i;
 
         for (i = from; i + Ruby.NIL_PREFILLED_ARRAY_SIZE < to; i += Ruby.NIL_PREFILLED_ARRAY_SIZE) {
             System.arraycopy(nils, 0, arr, i, Ruby.NIL_PREFILLED_ARRAY_SIZE);
         }
         System.arraycopy(nils, 0, arr, i, to - i);
     }
 
     public static void fillNil(IRubyObject[]arr, Ruby runtime) {
         fillNil(arr, 0, arr.length, runtime);
     }
 
     public static boolean isFastSwitchableString(IRubyObject str) {
         return str instanceof RubyString;
     }
 
     public static boolean isFastSwitchableSingleCharString(IRubyObject str) {
         return str instanceof RubyString && ((RubyString)str).getByteList().length() == 1;
     }
 
     public static int getFastSwitchString(IRubyObject str) {
         ByteList byteList = ((RubyString)str).getByteList();
         return byteList.hashCode();
     }
 
     public static int getFastSwitchSingleCharString(IRubyObject str) {
         ByteList byteList = ((RubyString)str).getByteList();
         return byteList.get(0);
     }
 
     public static boolean isFastSwitchableSymbol(IRubyObject sym) {
         return sym instanceof RubySymbol;
     }
 
     public static boolean isFastSwitchableSingleCharSymbol(IRubyObject sym) {
         return sym instanceof RubySymbol && ((RubySymbol)sym).asJavaString().length() == 1;
     }
 
     public static int getFastSwitchSymbol(IRubyObject sym) {
         String str = ((RubySymbol)sym).asJavaString();
         return str.hashCode();
     }
 
     public static int getFastSwitchSingleCharSymbol(IRubyObject sym) {
         String str = ((RubySymbol)sym).asJavaString();
         return (int)str.charAt(0);
     }
 
     public static Block getBlock(ThreadContext context, IRubyObject self, Node node) {
         IterNode iter = (IterNode)node;
         iter.getScope().determineModule();
 
         // Create block for this iter node
         // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
         if (iter.getBlockBody() instanceof InterpretedBlock) {
             return InterpretedBlock.newInterpretedClosure(context, iter.getBlockBody(), self);
         } else {
             return Interpreted19Block.newInterpretedClosure(context, iter.getBlockBody(), self);
         }
     }
 
     public static Block getBlock(Ruby runtime, ThreadContext context, IRubyObject self, Node node, Block aBlock) {
         return RuntimeHelpers.getBlockFromBlockPassBody(runtime, node.interpret(runtime, context, self, aBlock), aBlock);
     }
 
     /**
      * Equivalent to rb_equal in MRI
      *
      * @param context
      * @param a
      * @param b
      * @return
      */
     public static RubyBoolean rbEqual(ThreadContext context, IRubyObject a, IRubyObject b) {
         Ruby runtime = context.runtime;
         if (a == b) return runtime.getTrue();
         IRubyObject res = invokedynamic(context, a, OP_EQUAL, b);
         return runtime.newBoolean(res.isTrue());
     }
 
     public static void traceLine(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.runtime.callEventHooks(context, RubyEvent.LINE, context.getFile(), context.getLine(), name, type);
     }
 
     public static void traceClass(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.runtime.callEventHooks(context, RubyEvent.CLASS, context.getFile(), context.getLine(), name, type);
     }
 
     public static void traceEnd(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.runtime.callEventHooks(context, RubyEvent.END, context.getFile(), context.getLine(), name, type);
     }
 
     /**
      * Some of this code looks scary.  All names for an alias or undef is a
      * fitem in 1.8/1.9 grammars.  This means it is guaranteed to be either
      * a LiteralNode of a DSymbolNode.  Nothing else is possible.  Also
      * Interpreting a DSymbolNode will always yield a RubySymbol.
      */
     public static String interpretAliasUndefName(Node nameNode, Ruby runtime,
             ThreadContext context, IRubyObject self, Block aBlock) {
         String name;
 
         if (nameNode instanceof LiteralNode) {
             name = ((LiteralNode) nameNode).getName();
         } else {
             assert nameNode instanceof DSymbolNode: "Alias or Undef not literal or dsym";
             name = ((RubySymbol) nameNode.interpret(runtime, context, self, aBlock)).asJavaString();
         }
 
         return name;
     }
 
     /**
      * Used by the compiler to simplify arg checking in variable-arity paths
      *
      * @param context thread context
      * @param args arguments array
      * @param min minimum required
      * @param max maximum allowed
      */
     public static void checkArgumentCount(ThreadContext context, IRubyObject[] args, int min, int max) {
         checkArgumentCount(context, args.length, min, max);
     }
 
     /**
      * Used by the compiler to simplify arg checking in variable-arity paths
      *
      * @param context thread context
      * @param args arguments array
      * @param req required number
      */
     public static void checkArgumentCount(ThreadContext context, IRubyObject[] args, int req) {
         checkArgumentCount(context, args.length, req, req);
     }
     
     public static void checkArgumentCount(ThreadContext context, int length, int min, int max) {
         int expected = 0;
         if (length < min) {
             expected = min;
         } else if (max > -1 && length > max) {
             expected = max;
         } else {
             return;
         }
         throw context.runtime.newArgumentError(length, expected);
     }
 
     public static boolean isModuleAndHasConstant(IRubyObject left, String name) {
         return left instanceof RubyModule && ((RubyModule) left).getConstantFromNoConstMissing(name, false) != null;
     }
 
     public static RubyString getDefinedConstantOrBoundMethod(IRubyObject left, String name) {
         if (isModuleAndHasConstant(left, name)) return left.getRuntime().getDefinedMessage(DefinedMessage.CONSTANT);
         if (left.getMetaClass().isMethodBound(name, true)) left.getRuntime().getDefinedMessage(DefinedMessage.METHOD);
         return null;
     }
 
     public static RubyModule getSuperClassForDefined(Ruby runtime, RubyModule klazz) {
         RubyModule superklazz = klazz.getSuperClass();
 
         if (superklazz == null && klazz.isModule()) superklazz = runtime.getObject();
 
         return superklazz;
     }
 
     public static boolean isGenerationEqual(IRubyObject object, int generation) {
         RubyClass metaClass;
         if (object instanceof RubyBasicObject) {
             metaClass = ((RubyBasicObject)object).getMetaClass();
         } else {
             metaClass = object.getMetaClass();
         }
         return metaClass.getGeneration() == generation;
     }
 
     public static String[] getScopeNames(String scopeNames) {
         StringTokenizer toker = new StringTokenizer(scopeNames, ";");
         ArrayList list = new ArrayList(10);
         while (toker.hasMoreTokens()) {
             list.add(toker.nextToken().intern());
         }
         return (String[])list.toArray(new String[list.size()]);
     }
 
     public static IRubyObject[] arraySlice1N(IRubyObject arrayish) {
         arrayish = aryToAry(arrayish);
         RubyArray arrayish2 = ensureMultipleAssignableRubyArray(arrayish, arrayish.getRuntime(), true);
         return new IRubyObject[] {arrayEntryOrNilZero(arrayish2), subarrayOrEmpty(arrayish2, arrayish2.getRuntime(), 1)};
     }
 
     public static IRubyObject arraySlice1(IRubyObject arrayish) {
         arrayish = aryToAry(arrayish);
         RubyArray arrayish2 = ensureMultipleAssignableRubyArray(arrayish, arrayish.getRuntime(), true);
         return arrayEntryOrNilZero(arrayish2);
     }
 
     public static RubyClass metaclass(IRubyObject object) {
         return object instanceof RubyBasicObject ?
             ((RubyBasicObject)object).getMetaClass() :
             object.getMetaClass();
     }
 
     public static String rawBytesToString(byte[] bytes) {
         // stuff bytes into chars
         char[] chars = new char[bytes.length];
         for (int i = 0; i < bytes.length; i++) chars[i] = (char)bytes[i];
         return new String(chars);
     }
 
     public static byte[] stringToRawBytes(String string) {
         char[] chars = string.toCharArray();
         byte[] bytes = new byte[chars.length];
         for (int i = 0; i < chars.length; i++) bytes[i] = (byte)chars[i];
         return bytes;
     }
 
     public static String encodeCaptureOffsets(int[] scopeOffsets) {
         char[] encoded = new char[scopeOffsets.length * 2];
         for (int i = 0; i < scopeOffsets.length; i++) {
             int offDepth = scopeOffsets[i];
             char off = (char)(offDepth & 0xFFFF);
             char depth = (char)(offDepth >> 16);
             encoded[2 * i] = off;
             encoded[2 * i + 1] = depth;
         }
         return new String(encoded);
     }
 
     public static int[] decodeCaptureOffsets(String encoded) {
         char[] chars = encoded.toCharArray();
         int[] scopeOffsets = new int[chars.length / 2];
         for (int i = 0; i < scopeOffsets.length; i++) {
             char off = chars[2 * i];
             char depth = chars[2 * i + 1];
             scopeOffsets[i] = (((int)depth) << 16) | (int)off;
         }
         return scopeOffsets;
     }
 
     public static IRubyObject match2AndUpdateScope(IRubyObject receiver, ThreadContext context, IRubyObject value, String scopeOffsets) {
         DynamicScope scope = context.getCurrentScope();
         IRubyObject match = ((RubyRegexp)receiver).op_match(context, value);
         updateScopeWithCaptures(context, scope, decodeCaptureOffsets(scopeOffsets), match);
         return match;
     }
 
     public static IRubyObject match2AndUpdateScope19(IRubyObject receiver, ThreadContext context, IRubyObject value, String scopeOffsets) {
         DynamicScope scope = context.getCurrentScope();
         IRubyObject match = ((RubyRegexp)receiver).op_match19(context, value);
         updateScopeWithCaptures(context, scope, decodeCaptureOffsets(scopeOffsets), match);
         return match;
     }
 
     public static void updateScopeWithCaptures(ThreadContext context, DynamicScope scope, int[] scopeOffsets, IRubyObject result) {
         Ruby runtime = context.runtime;
         if (result.isNil()) { // match2 directly calls match so we know we can count on result
             IRubyObject nil = runtime.getNil();
 
             for (int i = 0; i < scopeOffsets.length; i++) {
                 scope.setValue(nil, scopeOffsets[i], 0);
             }
         } else {
             RubyMatchData matchData = (RubyMatchData)scope.getBackRef(runtime);
             // FIXME: Mass assignment is possible since we know they are all locals in the same
             //   scope that are also contiguous
             IRubyObject[] namedValues = matchData.getNamedBackrefValues(runtime);
 
             for (int i = 0; i < scopeOffsets.length; i++) {
                 scope.setValue(namedValues[i], scopeOffsets[i] & 0xffff, scopeOffsets[i] >> 16);
             }
         }
     }
 
     public static RubyArray argsPush(RubyArray first, IRubyObject second) {
         return ((RubyArray)first.dup()).append(second);
     }
 
     public static RubyArray argsCat(IRubyObject first, IRubyObject second) {
         Ruby runtime = first.getRuntime();
         IRubyObject secondArgs;
         if (runtime.is1_9()) {
             secondArgs = RuntimeHelpers.splatValue19(second);
         } else {
             secondArgs = RuntimeHelpers.splatValue(second);
         }
 
         return ((RubyArray)RuntimeHelpers.ensureRubyArray(runtime, first).dup()).concat(secondArgs);
     }
 
     public static String encodeParameterList(ArgsNode argsNode) {
         StringBuilder builder = new StringBuilder();
         
         boolean added = false;
         if (argsNode.getPre() != null) {
             for (Node preNode : argsNode.getPre().childNodes()) {
                 if (added) builder.append(';');
                 added = true;
                 if (preNode instanceof MultipleAsgn19Node) {
                     builder.append("nil");
                 } else {
                     builder.append("q").append(((ArgumentNode)preNode).getName());
                 }
             }
         }
 
         if (argsNode.getOptArgs() != null) {
             for (Node optNode : argsNode.getOptArgs().childNodes()) {
                 if (added) builder.append(';');
                 added = true;
                 builder.append("o");
                 if (optNode instanceof OptArgNode) {
                     builder.append(((OptArgNode)optNode).getName());
                 } else if (optNode instanceof LocalAsgnNode) {
                     builder.append(((LocalAsgnNode)optNode).getName());
                 } else if (optNode instanceof DAsgnNode) {
                     builder.append(((DAsgnNode)optNode).getName());
                 }
             }
         }
 
         if (argsNode.getRestArg() >= 0) {
             if (added) builder.append(';');
             added = true;
             if (argsNode.getRestArgNode() instanceof UnnamedRestArgNode) {
                 if (((UnnamedRestArgNode) argsNode.getRestArgNode()).isStar()) builder.append("R");
             } else {
                 builder.append("r").append(argsNode.getRestArgNode().getName());
             }
         }
 
         if (argsNode.getPost() != null) {
             for (Node postNode : argsNode.getPost().childNodes()) {
                 if (added) builder.append(';');
                 added = true;
                 if (postNode instanceof MultipleAsgn19Node) {
                     builder.append("nil");
                 } else {
                     builder.append("q").append(((ArgumentNode)postNode).getName());
                 }
             }
         }
 
         if (argsNode.getBlock() != null) {
             if (added) builder.append(';');
             added = true;
             builder.append("b").append(argsNode.getBlock().getName());
         }
 
         if (!added) builder.append("NONE");
 
         return builder.toString();
     }
 
     public static RubyArray parameterListToParameters(Ruby runtime, String[] parameterList, boolean isLambda) {
         RubyArray parms = RubyArray.newEmptyArray(runtime);
 
         for (String param : parameterList) {
             if (param.equals("NONE")) break;
 
             RubyArray elem = RubyArray.newEmptyArray(runtime);
             if (param.equals("nil")) {
                 // marker for masgn args (the parens in "a, b, (c, d)"
                 elem.add(RubySymbol.newSymbol(runtime, isLambda ? "req" : "opt"));
                 parms.add(elem);
                 continue;
             }
 
             if (param.charAt(0) == 'q') {
                 // required/normal arg
                 elem.add(RubySymbol.newSymbol(runtime, isLambda ? "req" : "opt"));
             } else if (param.charAt(0) == 'r') {
                 // named rest arg
                 elem.add(RubySymbol.newSymbol(runtime, "rest"));
             } else if (param.charAt(0) == 'R') {
                 // unnamed rest arg (star)
                 elem.add(RubySymbol.newSymbol(runtime, "rest"));
                 parms.add(elem);
                 continue;
             } else if (param.charAt(0) == 'o') {
                 // optional arg
                 elem.add(RubySymbol.newSymbol(runtime, "opt"));
                 if (param.length() == 1) {
                     // no name; continue
                     parms.add(elem);
                     continue;
                 }
             } else if (param.charAt(0) == 'b') {
                 // block arg
                 elem.add(RubySymbol.newSymbol(runtime, "block"));
             }
             elem.add(RubySymbol.newSymbol(runtime, param.substring(1)));
             parms.add(elem);
         }
 
         return parms;
     }
 
     public static RubyString getDefinedCall(ThreadContext context, IRubyObject self, IRubyObject receiver, String name) {
         RubyClass metaClass = receiver.getMetaClass();
         DynamicMethod method = metaClass.searchMethod(name);
         Visibility visibility = method.getVisibility();
 
         if (visibility != Visibility.PRIVATE &&
                 (visibility != Visibility.PROTECTED || metaClass.getRealClass().isInstance(self)) && !method.isUndefined()) {
             return context.runtime.getDefinedMessage(DefinedMessage.METHOD);
         }
 
         if (context.runtime.is1_9() && receiver.callMethod(context, "respond_to_missing?",
             new IRubyObject[]{context.runtime.newSymbol(name), context.runtime.getFalse()}).isTrue()) {
             return context.runtime.getDefinedMessage(DefinedMessage.METHOD);
         }
         return null;
     }
 
     public static RubyString getDefinedNot(Ruby runtime, RubyString definition) {
         if (definition != null && runtime.is1_9()) {
             definition = runtime.getDefinedMessage(DefinedMessage.METHOD);
         }
 
         return definition;
     }
 
     public static IRubyObject invokedynamic(ThreadContext context, IRubyObject self, MethodNames method) {
         RubyClass metaclass = self.getMetaClass();
         String name = method.realName();
         return getMethodCached(context, metaclass, method.ordinal(), name).call(context, self, metaclass, name);
     }
 
     public static IRubyObject invokedynamic(ThreadContext context, IRubyObject self, MethodNames method, IRubyObject arg0) {
         RubyClass metaclass = self.getMetaClass();
         String name = method.realName();
         return getMethodCached(context, metaclass, method.ordinal(), name).call(context, self, metaclass, name, arg0);
     }
     
     private static DynamicMethod getMethodCached(ThreadContext context, RubyClass metaclass, int index, String name) {
         if (metaclass.index >= ClassIndex.MAX_CLASSES) return metaclass.searchMethod(name);
         return context.runtimeCache.getMethod(context, metaclass, metaclass.index * (index + 1), name);
     }
     
     public static IRubyObject lastElement(IRubyObject[] ary) {
         return ary[ary.length - 1];
     }
     
     public static RubyString appendAsString(RubyString target, IRubyObject other) {
         return target.append(other.asString());
     }
     
     public static RubyString appendAsString19(RubyString target, IRubyObject other) {
         return target.append19(other.asString());
     }
 
     /**
      * We need to splat incoming array to a block when |a, *b| (any required +
      * rest) or |a, b| (>1 required).
      */
     public static boolean needsSplat19(int requiredCount, boolean isRest) {
         return (isRest && requiredCount > 0) || (!isRest && requiredCount > 1);
     }
 
     // . Array given to rest should pass itself
     // . Array with rest + other args should extract array
     // . Array with multiple values and NO rest should extract args if there are more than one argument
     // Note: In 1.9 alreadyArray is only relevent from our internal Java code in core libs.  We never use it
     // from interpreter or JIT.  FIXME: Change core lib consumers to stop using alreadyArray param.
     public static IRubyObject[] restructureBlockArgs19(IRubyObject value, boolean needsSplat, boolean alreadyArray) {
         if (value != null && !(value instanceof RubyArray) && needsSplat) value = RuntimeHelpers.aryToAry(value);
 
         IRubyObject[] parameters;
         if (value == null) {
             parameters = IRubyObject.NULL_ARRAY;
         } else if (value instanceof RubyArray && (alreadyArray || needsSplat)) {
             parameters = ((RubyArray) value).toJavaArray();
         } else {
             parameters = new IRubyObject[] { value };
         }
 
         return parameters;
     }
 
     public static boolean BEQ(ThreadContext context, IRubyObject value1, IRubyObject value2) {
         return value1.op_equal(context, value2).isTrue();
     }
 
     public static boolean BNE(ThreadContext context, IRubyObject value1, IRubyObject value2) {
         boolean eql = value2 == context.nil || value2 == UndefinedValue.UNDEFINED ?
                 value1 == value2 : value1.op_equal(context, value2).isTrue();
 
         return !eql;
     }
 
     public static RubyModule checkIsRubyModule(ThreadContext context, Object object) {
         if (!(object instanceof RubyModule)) {
             throw context.runtime.newTypeError("no outer class/module");
         }
 
         return (RubyModule)object;
     }
 
     public static IRubyObject invokeModuleBody(ThreadContext context, CompiledIRMethod method) {
         RubyModule implClass = method.getImplementationClass();
 
         return method.call(context, implClass, implClass, "");
     }
 
     public static RubyClass newClassForIR(ThreadContext context, String name, IRubyObject self, RubyModule classContainer, Object superClass, boolean meta) {
         if (meta) return classContainer.getMetaClass();
 
         RubyClass sc = null;
 
         if (superClass != null) {
             if (!(superClass instanceof RubyClass)) {
                 throw context.runtime.newTypeError("superclass must be Class (" + superClass + " given)");
             }
 
             sc = (RubyClass) superClass;
         }
 
 
         return classContainer.defineOrGetClassUnder(name, sc);
     }
 
     public static RubyString appendByteList(RubyString target, ByteList source) {
         target.getByteList().append(source);
         return target;
     }
 
     public static RubyString appendByteList19(RubyString target, ByteList source, int codeRange) {
         target.cat19(source, codeRange);
         return target;
     }
 
+    public static RubyString shortcutAppend18(RubyString string, IRubyObject object) {
+        if (object instanceof RubyFixnum || object instanceof RubyFloat || object instanceof RubySymbol) {
+            return string.append(object);
+        } else {
+            return string.append(object.asString());
+        }
+    }
+
     public static RubyString shortcutAppend(RubyString string, IRubyObject object) {
         if (object instanceof RubyFixnum || object instanceof RubyFloat || object instanceof RubySymbol) {
             return string.append19(object);
         } else {
             return string.append19(object.asString());
         }
     }
 
     @Deprecated
     public static IRubyObject invokedynamic(ThreadContext context, IRubyObject self, int index) {
         return invokedynamic(context, self, MethodNames.values()[index]);
     }
 
     @Deprecated
     public static IRubyObject invokedynamic(ThreadContext context, IRubyObject self, int index, IRubyObject arg0) {
         return invokedynamic(context, self, MethodNames.values()[index], arg0);
     }
 }
