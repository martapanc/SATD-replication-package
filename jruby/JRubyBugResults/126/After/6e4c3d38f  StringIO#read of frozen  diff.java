diff --git a/core/src/main/java/org/jruby/ext/stringio/RubyStringIO.java b/core/src/main/java/org/jruby/ext/stringio/RubyStringIO.java
index 97af035626..74ed5d2042 100644
--- a/core/src/main/java/org/jruby/ext/stringio/RubyStringIO.java
+++ b/core/src/main/java/org/jruby/ext/stringio/RubyStringIO.java
@@ -191,1461 +191,1490 @@ public class RubyStringIO extends org.jruby.RubyStringIO implements EncodingCapa
         if (ptr.modes.isTruncate()) {
             ptr.string.modifyCheck();
             ptr.string.empty();
         }
 
         return this;
     }
 
     @JRubyMethod(visibility = PRIVATE)
     @Override
     public IRubyObject initialize_copy(IRubyObject other) {
         RubyStringIO otherIO = (RubyStringIO) TypeConverter.convertToType(other, 
                 getRuntime().getClass("StringIO"), "to_strio");
 
         if (this == otherIO) return this;
 
         ptr = otherIO.ptr;
         if (otherIO.isTaint()) setTaint(true);
 
         return this;
     }
 
     @JRubyMethod(name = "<<", required = 1)
     @Override
     public IRubyObject append(ThreadContext context, IRubyObject arg) {
         // Claims conversion is done via 'to_s' in docs.
         callMethod(context, "write", arg);
         
         return this; 
     }
 
     @JRubyMethod
     @Override
     public IRubyObject binmode() {
         return this;
     }
 
     @JRubyMethod
     @Override
     public IRubyObject close() {
         checkInitialized();
         checkOpen();
 
         ptr.closedRead = true;
         ptr.closedWrite = true;
 
         return getRuntime().getNil();
     }
 
     private void doFinalize() {
         ptr.closedRead = true;
         ptr.closedWrite = true;
         ptr.string = null;
     }
 
     @JRubyMethod(name = "closed?")
     @Override
     public IRubyObject closed_p() {
         checkInitialized();
         return getRuntime().newBoolean(ptr.closedRead && ptr.closedWrite);
     }
 
     @JRubyMethod
     @Override
     public IRubyObject close_read() {
         checkReadable();
         ptr.closedRead = true;
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "closed_read?")
     @Override
     public IRubyObject closed_read_p() {
         checkInitialized();
         return getRuntime().newBoolean(ptr.closedRead);
     }
 
     @JRubyMethod
     @Override
     public IRubyObject close_write() {
         checkWritable();
         ptr.closedWrite = true;
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "closed_write?")
     @Override
     public IRubyObject closed_write_p() {
         checkInitialized();
         return getRuntime().newBoolean(ptr.closedWrite);
     }
 
     @Override
     public IRubyObject eachInternal(ThreadContext context, IRubyObject[] args, Block block) {
         IRubyObject line = getsOnly(context, args);
 
         while (!line.isNil()) {
             block.yield(context, line);
             line = getsOnly(context, args);
         }
 
         return this;
     }
 
     @JRubyMethod(name = "each", optional = 1, writes = FrameField.LASTLINE)
     @Override
     public IRubyObject each(ThreadContext context, IRubyObject[] args, Block block) {
         return block.isGiven() ? eachInternal(context, args, block) : enumeratorize(context.runtime, this, "each", args);
     }
 
     @JRubyMethod(name = "each", optional = 2, writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject each19(ThreadContext context, IRubyObject[] args, Block block) {
         if (!block.isGiven()) return enumeratorize(context.runtime, this, "each", args);
         
         if (args.length > 0 && !args[args.length - 1].isNil() && args[args.length - 1].checkStringType19().isNil() &&
                 RubyNumeric.num2long(args[args.length - 1]) == 0) {
             throw context.runtime.newArgumentError("invalid limit: 0 for each_line");
         }
         
         return eachInternal(context, args, block);
     }
 
     @JRubyMethod(optional = 1, compat = RUBY1_8)
     @Override
     public IRubyObject each_line(ThreadContext context, IRubyObject[] args, Block block) {
         return block.isGiven() ? eachInternal(context, args, block) : enumeratorize(context.runtime, this, "each_line", args);
     }
 
     @JRubyMethod(name = "each_line", optional = 2, compat = RUBY1_9)
     public IRubyObject each_line19(ThreadContext context, IRubyObject[] args, Block block) {
         if (!block.isGiven()) return enumeratorize(context.runtime, this, "each_line", args);
         
         return each19(context, args, block);
     }
 
     @JRubyMethod(optional = 1, compat = RUBY1_8)
     @Override
     public IRubyObject lines(ThreadContext context, IRubyObject[] args, Block block) {
         return block.isGiven() ? each(context, args, block) : enumeratorize(context.runtime, this, "lines", args);
     }
 
     @JRubyMethod(name = "lines", optional = 2, compat = RUBY1_9)
     public IRubyObject lines19(ThreadContext context, IRubyObject[] args, Block block) {
         context.runtime.getWarnings().warn("StringIO#lines is deprecated; use #each_line instead");
         return block.isGiven() ? each19(context, args, block) : enumeratorize(context.runtime, this, "each_line", args);
     }
 
     @Override
     public IRubyObject each_byte(ThreadContext context, Block block) {
         checkReadable();
         Ruby runtime = context.runtime;
         ByteList bytes = ptr.string.getByteList();
 
         // Check the length every iteration, since
         // the block can modify this string.
         while (ptr.pos < bytes.length()) {
             block.yield(context, runtime.newFixnum(bytes.get((int) ptr.pos++) & 0xFF));
         }
         return this;
     }
 
     @JRubyMethod(name = "each_byte")
     @Override
     public IRubyObject each_byte19(ThreadContext context, Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.runtime, this, "each_byte");
     }
 
     @JRubyMethod
     @Override
     public IRubyObject bytes(ThreadContext context, Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.runtime, this, "bytes");
     }
 
     @Override
     public IRubyObject each_charInternal(final ThreadContext context, final Block block) {
         checkReadable();
 
         Ruby runtime = context.runtime;
         ByteList bytes = ptr.string.getByteList();
         int len = bytes.getRealSize();
         int end = bytes.getBegin() + len;
         Encoding enc = runtime.is1_9() ? bytes.getEncoding() : runtime.getKCode().getEncoding();        
         while (ptr.pos < len) {
             int pos = (int) ptr.pos;
             int n = StringSupport.length(enc, bytes.getUnsafeBytes(), pos, end);
 
             if(len < pos + n) n = len - pos;
 
             ptr.pos += n;
 
             block.yield(context, ptr.string.makeShared19(runtime, pos, n));
         }
 
         return this;
     }
 
     @JRubyMethod
     @Override
     public IRubyObject each_char(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal(context, block) : enumeratorize(context.runtime, this, "each_char");
     }
 
     @JRubyMethod
     @Override
     public IRubyObject chars(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_charInternal(context, block) : enumeratorize(context.runtime, this, "chars");
     }
 
     @JRubyMethod(name = {"eof", "eof?"})
     @Override
     public IRubyObject eof() {
         return getRuntime().newBoolean(isEOF());
     }
 
     private boolean isEOF() {
         return isEndOfString() || (getRuntime().is1_8() && ptr.eof);
     }
     
     private boolean isEndOfString() {
         return ptr.pos >= ptr.string.getByteList().length();
     }    
 
     @JRubyMethod(name = "fcntl")
     @Override
     public IRubyObject fcntl() {
         throw getRuntime().newNotImplementedError("fcntl not implemented");
     }
 
     @JRubyMethod(name = "fileno")
     @Override
     public IRubyObject fileno() {
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "flush")
     @Override
     public IRubyObject flush() {
         return this;
     }
 
     @JRubyMethod(name = "fsync")
     @Override
     public IRubyObject fsync() {
         return RubyFixnum.zero(getRuntime());
     }
 
     @JRubyMethod(name = {"getc", "getbyte"})
     @Override
     public IRubyObject getc() {
         checkReadable();
         if (isEndOfString()) return getRuntime().getNil();
 
         return getRuntime().newFixnum(ptr.string.getByteList().get((int)ptr.pos++) & 0xFF);
     }
 
     @JRubyMethod(name = "getc", compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject getc19(ThreadContext context) {
         checkReadable();
         if (isEndOfString()) return context.runtime.getNil();
 
         int start = ptr.pos;
         int total = 1 + StringSupport.bytesToFixBrokenTrailingCharacter(ptr.string.getByteList(), start + 1);
         
         ptr.pos += total;
         
         return context.runtime.newString(ptr.string.getByteList().makeShared(start, total));
     }
     
     private RubyString strioSubstr(Ruby runtime, int pos, int len) {
         RubyString str = ptr.string;
         ByteList strByteList = str.getByteList();
         byte[] strBytes = strByteList.getUnsafeBytes();
         Encoding enc = str.getEncoding();
         int rlen = str.size() - pos;
         
         if (len > rlen) len = rlen;
         if (len < 0) len = 0;
         
         if (len == 0) return RubyString.newEmptyString(runtime);
         return RubyString.newStringShared(runtime, strBytes, strByteList.getBegin() + pos, len, enc);
     }
 
     private IRubyObject internalGets18(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
 
         if (!isEndOfString() && !ptr.eof) {
             boolean isParagraph = false;
             ByteList sep = ((RubyString)runtime.getGlobalVariables().get("$/")).getByteList();
             IRubyObject sepArg;
             int limit = -1;
 
             sepArg = (args.length > 0 ? args[0] : null);
 
             if (sepArg != null) {
                 if (sepArg.isNil()) {
                     int bytesAvailable = ptr.string.getByteList().getRealSize() - (int)ptr.pos;
                     int bytesToUse = (limit < 0 || limit >= bytesAvailable ? bytesAvailable : limit);
                     
                     // add additional bytes to fix trailing broken character
                     bytesToUse += StringSupport.bytesToFixBrokenTrailingCharacter(ptr.string.getByteList(), bytesToUse);
                     
                     ByteList buf = ptr.string.getByteList().makeShared(
                         (int)ptr.pos, bytesToUse);
                     ptr.pos += buf.getRealSize();
                     return makeString(runtime, buf);
                 }
 
                 sep = sepArg.convertToString().getByteList();
                 if (sep.getRealSize() == 0) {
                     isParagraph = true;
                     sep = Stream.PARAGRAPH_SEPARATOR;
                 }
             }
 
             ByteList ss = ptr.string.getByteList();
 
             if (isParagraph) {
                 swallowLF(ss);
                 if (ptr.pos == ss.getRealSize()) {
                     return runtime.getNil();
                 }
             }
 
             int sepIndex = ss.indexOf(sep, (int)ptr.pos);
 
             ByteList add;
             if (-1 == sepIndex) {
                 sepIndex = ptr.string.getByteList().getRealSize();
                 add = ByteList.EMPTY_BYTELIST;
             } else {
                 add = sep;
             }
 
             int bytes = sepIndex - (int)ptr.pos;
             int bytesToUse = (limit < 0 || limit >= bytes ? bytes : limit);
 
             int bytesWithSep = sepIndex - (int)ptr.pos + add.getRealSize();
             int bytesToUseWithSep = (limit < 0 || limit >= bytesWithSep ? bytesWithSep : limit);
 
             ByteList line = new ByteList(bytesToUseWithSep);
             line.append(ptr.string.getByteList(), (int)ptr.pos, bytesToUse);
             ptr.pos += bytesToUse;
 
             int sepBytesToUse = bytesToUseWithSep - bytesToUse;
             line.append(add, 0, sepBytesToUse);
             ptr.pos += sepBytesToUse;
 
             if (sepBytesToUse >= add.getRealSize()) {
                 ptr.lineno++;
             }
 
             return makeString(runtime, line);
         }
         return runtime.getNil();
     }
     
     private IRubyObject internalGets19(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
 
         IRubyObject str = context.nil;;
         int n, limit = -1;
 
         switch (args.length) {
             case 0:
                 str = runtime.getGlobalVariables().get("$/");
                 break;
 
             case 1:
             {
                 str = args[0];
                 if (!str.isNil() && !(str instanceof RubyString)) {
                     IRubyObject tmp = str.checkStringType19();
                     if (tmp.isNil()) {
                         limit = RubyNumeric.num2int(str);
                         if (limit == 0) return runtime.newString();
                         str = runtime.getGlobalVariables().get("$/");
                     } else {
                         str = tmp;
                     }
                 }
                 break;
             }
 
             case 2:
                 if (!args[0].isNil()) str = args[0].convertToString();
                 // 2.0 ignores double nil, 1.9 raises
                 if (runtime.is2_0()) {
                     if (!args[1].isNil()) {
                         limit = RubyNumeric.num2int(args[1]);
                     }
                 } else {
                     limit = RubyNumeric.num2int(args[1]);
                 }
                 break;
         }
         
         if (ptr.pos >= (n = ptr.string.size())) {
             return context.nil;
         }
         
         ByteList dataByteList = ptr.string.getByteList();
         byte[] dataBytes = dataByteList.getUnsafeBytes();
         int begin = dataByteList.getBegin();
         int s = begin + ptr.pos;
         int e = begin + dataByteList.getRealSize();
         int p;
         
         if (limit > 0 && s + limit < e) {
             e = dataByteList.getEncoding().rightAdjustCharHead(dataBytes, s, s + limit, e);
         }
         
         if (str.isNil()) {
             str = strioSubstr(runtime, ptr.pos, e - s);
         } else if ((n = ((RubyString)str).size()) == 0) {
             // this is not an exact port; the original confused me
             p = s;
             // remove leading \n
             while (dataBytes[p] == '\n') {
                 if (++p == e) {
                     return context.nil;
                 }
             }
             s = p;
             // find next \n or end; if followed by \n, include it too
             p = memchr(dataBytes, p, '\n', e - p);
             if (p != -1) {
                 if (++p < e && dataBytes[p] == '\n') {
                     e = p + 1;
                 } else {
                     e = p;
                 }
             }
             str = strioSubstr(runtime, s - begin, e - s);
         } else if (n == 1) {
             RubyString strStr = (RubyString)str;
             ByteList strByteList = strStr.getByteList();
             if ((p = memchr(dataBytes, s, strByteList.get(0), e - s)) != -1) {
                 e = p + 1;
             }
             str = strioSubstr(runtime, ptr.pos, e - s);
         } else {
             if (n < e - s) {
                 RubyString strStr = (RubyString)str;
                 ByteList strByteList = strStr.getByteList();
                 byte[] strBytes = strByteList.getUnsafeBytes();
                 
                 int[] skip = new int[1 << CHAR_BIT];
                 int pos;
                 p = strByteList.getBegin();
                 bm_init_skip(skip, strBytes, p, n);
                 if ((pos = bm_search(strBytes, p, n, dataBytes, s, e - s, skip)) >= 0) {
                     e = s + pos + n;
                 }
             }
             str = strioSubstr(runtime, ptr.pos, e - s);
         }
         ptr.pos = e - begin;
         ptr.lineno++;
         return str;
     }
     
     private static int memchr(byte[] ptr, int start, int find, int len) {
         for (int i = start; i < start + len; i++) {
             if (ptr[i] == find) return i;
         }
         return -1;
     }
     
     private static final int CHAR_BIT = 8;
     
     private static void bm_init_skip(int[] skip, byte[] pat, int patPtr, int m) {
         int c;
         
         for (c = 0; c < (1 << CHAR_BIT); c++) {
             skip[c] = m;
         }
         while ((--m) > 0) {
             skip[pat[patPtr++]] = m;
         }
     }
     
     // Note that this is substantially more complex in 2.0 (Onigmo)
     private static int bm_search(byte[] little, int lstart, int llen, byte[] big, int bstart, int blen, int[] skip) {
         int i, j, k;
         
         i = llen - 1;
         while (i < blen) {
             k = i;
             j = llen - 1;
             while (j >= 0 && big[k + bstart] == little[j + lstart]) {
                 k--;
                 j--;
             }
             if (j < 0) return k + 1;
             i += skip[big[i + bstart]];
         }
         return -1;
     }
 
 //        if (sepArg != null) {
 //            if (sepArg.isNil()) {
 //                int bytesAvailable = data.internal.getByteList().getRealSize() - (int)data.pos;
 //                int bytesToUse = (limit < 0 || limit >= bytesAvailable ? bytesAvailable : limit);
 //
 //                // add additional bytes to fix trailing broken character
 //                bytesToUse += StringSupport.bytesToFixBrokenTrailingCharacter(data.internal.getByteList(), bytesToUse);
 //
 //                ByteList buf = data.internal.getByteList().makeShared(
 //                    (int)data.pos, bytesToUse);
 //                data.pos += buf.getRealSize();
 //                return makeString(runtime, buf);
 //            }
 //
 //            sep = sepArg.convertToString().getByteList();
 //            if (sep.getRealSize() == 0) {
 //                isParagraph = true;
 //                sep = Stream.PARAGRAPH_SEPARATOR;
 //            }
 //        }
 //
 //        if (isEndOfString() || data.eof) return context.nil;
 //
 //        ByteList ss = data.internal.getByteList();
 //
 //        if (isParagraph) {
 //            swallowLF(ss);
 //            if (data.pos == ss.getRealSize()) {
 //                return runtime.getNil();
 //            }
 //        }
 //
 //        int sepIndex = ss.indexOf(sep, (int)data.pos);
 //
 //        ByteList add;
 //        if (-1 == sepIndex) {
 //            sepIndex = data.internal.getByteList().getRealSize();
 //            add = ByteList.EMPTY_BYTELIST;
 //        } else {
 //            add = sep;
 //        }
 //
 //        int bytes = sepIndex - (int)data.pos;
 //        int bytesToUse = (limit < 0 || limit >= bytes ? bytes : limit);
 //
 //        int bytesWithSep = sepIndex - (int)data.pos + add.getRealSize();
 //        int bytesToUseWithSep = (limit < 0 || limit >= bytesWithSep ? bytesWithSep : limit);
 //
 //        ByteList line = new ByteList(bytesToUseWithSep);
 //        if (is19) line.setEncoding(data.internal.getByteList().getEncoding());
 //        line.append(data.internal.getByteList(), (int)data.pos, bytesToUse);
 //        data.pos += bytesToUse;
 //
 //        if (is19) {
 //            // add additional bytes to fix trailing broken character
 //            int extraBytes = StringSupport.bytesToFixBrokenTrailingCharacter(line, line.length());
 //            if (extraBytes != 0) {
 //                line.append(data.internal.getByteList(), (int)data.pos, extraBytes);
 //                data.pos += extraBytes;
 //            }
 //        }
 //
 //        int sepBytesToUse = bytesToUseWithSep - bytesToUse;
 //        line.append(add, 0, sepBytesToUse);
 //        data.pos += sepBytesToUse;
 //
 //        if (sepBytesToUse >= add.getRealSize()) {
 //            data.lineno++;
 //        }
 //
 //        return makeString(runtime, line);
 //    }
 
     private void swallowLF(ByteList list) {
         while (ptr.pos < list.getRealSize()) {
             if (list.get((int)ptr.pos) == '\n') {
                 ptr.pos++;
             } else {
                 break;
             }
         }
     }
 
     @JRubyMethod(name = "gets", optional = 1, writes = FrameField.LASTLINE, compat = CompatVersion.RUBY1_8)
     @Override
     public IRubyObject gets(ThreadContext context, IRubyObject[] args) {
         IRubyObject result = getsOnly(context, args);
         context.setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", optional = 2, writes = FrameField.LASTLINE, compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject gets19(ThreadContext context, IRubyObject[] args) {
         IRubyObject result = getsOnly(context, args);
         context.setLastLine(result);
 
         return result;
     }
 
     @Override
     public IRubyObject getsOnly(ThreadContext context, IRubyObject[] args) {
         checkReadable();
 
         return context.is19 ? internalGets19(context, args) : internalGets18(context, args);
     }
 
     @JRubyMethod(name = {"tty?", "isatty"})
     @Override
     public IRubyObject isatty() {
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(name = {"length", "size"})
     @Override
     public IRubyObject length() {
         checkFinalized();
         return getRuntime().newFixnum(ptr.string.getByteList().length());
     }
 
     @JRubyMethod(name = "lineno")
     @Override
     public IRubyObject lineno() {
         return getRuntime().newFixnum(ptr.lineno);
     }
 
     @JRubyMethod(name = "lineno=", required = 1)
     @Override
     public IRubyObject set_lineno(IRubyObject arg) {
         ptr.lineno = RubyNumeric.fix2int(arg);
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "path", compat = CompatVersion.RUBY1_8)
     @Override
     public IRubyObject path() {
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "pid")
     @Override
     public IRubyObject pid() {
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = {"pos", "tell"})
     @Override
     public IRubyObject pos() {
         return getRuntime().newFixnum(ptr.pos);
     }
 
     @JRubyMethod(name = "pos=", required = 1)
     @Override
     public IRubyObject set_pos(IRubyObject arg) {
         ptr.pos = RubyNumeric.fix2int(arg);
         
         if (ptr.pos < 0) throw getRuntime().newErrnoEINVALError("Invalid argument");
 
         if (getRuntime().is1_8() && !isEndOfString()) ptr.eof = false;
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "print", rest = true)
     @Override
     public IRubyObject print(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         if (args.length != 0) {
             for (int i=0,j=args.length;i<j;i++) {
                 append(context, args[i]);
             }
         } else {
             IRubyObject arg = runtime.getGlobalVariables().get("$_");
             append(context, arg.isNil() ? makeString(runtime, new ByteList(new byte[] {'n', 'i', 'l'})) : arg);
         }
         IRubyObject sep = runtime.getGlobalVariables().get("$\\");
         if (!sep.isNil()) append(context, sep);
 
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "print", rest = true, compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject print19(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         if (args.length != 0) {
             for (int i=0,j=args.length;i<j;i++) {
                 append(context, args[i]);
             }
         } else {
             IRubyObject arg = runtime.getGlobalVariables().get("$_");
             append(context, arg.isNil() ? RubyString.newEmptyString(getRuntime()) : arg);
         }
         IRubyObject sep = runtime.getGlobalVariables().get("$\\");
         if (!sep.isNil()) append(context, sep);
 
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "printf", required = 1, rest = true)
     @Override
     public IRubyObject printf(ThreadContext context, IRubyObject[] args) {
         append(context, RubyKernel.sprintf(context, this, args));
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "putc", required = 1)
     @Override
     public IRubyObject putc(IRubyObject obj) {
         checkWritable();
         byte c = RubyNumeric.num2chr(obj);
         checkFrozen();
 
         ptr.string.modify();
         ByteList bytes = ptr.string.getByteList();
         if (ptr.modes.isAppendable()) {
             ptr.pos = bytes.length();
             bytes.append(c);
         } else {
             if (isEndOfString()) bytes.length((int)ptr.pos + 1);
 
             bytes.set((int) ptr.pos, c);
             ptr.pos++;
         }
 
         return obj;
     }
 
     public static final ByteList NEWLINE = ByteList.create("\n");
 
     @JRubyMethod(name = "puts", rest = true)
     @Override
     public IRubyObject puts(ThreadContext context, IRubyObject[] args) {
         checkWritable();
 
         // FIXME: the code below is a copy of RubyIO.puts,
         // and we should avoid copy-paste.
 
         if (args.length == 0) {
             callMethod(context, "write", RubyString.newStringShared(getRuntime(), NEWLINE));
             return getRuntime().getNil();
         }
 
         for (int i = 0; i < args.length; i++) {
             RubyString line = getRuntime().newString();
 
             if (args[i].isNil()) {
                 if (!getRuntime().is1_9()) {
                     line = getRuntime().newString("nil");
                 }
             } else {
                 IRubyObject tmp = args[i].checkArrayType();
                 if (!tmp.isNil()) {
                     RubyArray arr = (RubyArray) tmp;
                     if (getRuntime().isInspecting(arr)) {
                         line = getRuntime().newString("[...]");
                     } else {
                         inspectPuts(context, arr);
                         continue;
                     }
                 } else {
                     if (args[i] instanceof RubyString) {
                         line = (RubyString)args[i];
                     } else {
                         line = args[i].asString();
                     }
                 }
             }
 
             callMethod(context, "write", line);
 
             if (!line.getByteList().endsWith(NEWLINE)) {
                 callMethod(context, "write", RubyString.newStringShared(getRuntime(), NEWLINE));
             }
         }
         return getRuntime().getNil();
     }
 
     private IRubyObject inspectPuts(ThreadContext context, RubyArray array) {
         try {
             getRuntime().registerInspecting(array);
             return puts(context, array.toJavaArray());
         } finally {
             getRuntime().unregisterInspecting(array);
         }
     }
     
     // Make string based on internal data encoding (which ironically is its
     // external encoding.  This seems messy and we should consider a more
     // uniform method for makeing strings (we have a slightly different variant
     // of this in RubyIO.
     private RubyString makeString(Ruby runtime, ByteList buf, boolean setEncoding) {
         if (runtime.is1_9() && setEncoding) buf.setEncoding(ptr.string.getEncoding());
 
         RubyString str = RubyString.newString(runtime, buf);
         str.setTaint(true);
 
         return str;        
     }
     
     private RubyString makeString(Ruby runtime, ByteList buf) {
         return makeString(runtime, buf, true);
     }
 
     @JRubyMethod(name = "read", optional = 2)
     public IRubyObject read(ThreadContext context, IRubyObject[] args) {
         if (context.is19) {
             return read19(args);
         }
         return read18(args);
     }
     
     private IRubyObject read19(IRubyObject[] args) {
         checkReadable();
 
         Ruby runtime = getRuntime();
         IRubyObject str = runtime.getNil();
         int len;
         boolean binary = false;
 
         switch (args.length) {
         case 2:
             str = args[1];
             if (!str.isNil()) {
                 str = str.convertToString();
                 ((RubyString)str).modify();
             }
         case 1:
             if (!args[0].isNil()) {
                 len = RubyNumeric.fix2int(args[0]);
 
                 if (len < 0) {
                     throw getRuntime().newArgumentError("negative length " + len + " given");
                 }
                 if (len > 0 && isEndOfString()) {
                     if (!str.isNil()) ((RubyString)str).resize(0);
                     return getRuntime().getNil();
                 }
                 binary = true;
                 break;
             }
         case 0:
             len = ptr.string.size();
             if (len <= ptr.pos) {
                 if (str.isNil()) {
                     str = runtime.newString();
                 } else {
                     ((RubyString)str).resize(0);
                 }
 
                 return str;
             } else {
                 len -= ptr.pos;
             }
             break;
         default:
             throw getRuntime().newArgumentError(args.length, 0);
         }
 
         if (str.isNil()) {
             str = strioSubstr(runtime, ptr.pos, len);
             if (binary) ((RubyString)str).setEncoding(ASCIIEncoding.INSTANCE);
         } else {
             int rest = ptr.string.size() - ptr.pos;
             if (len > rest) len = rest;
             ((RubyString)str).resize(len);
             ByteList strByteList = ((RubyString)str).getByteList();
             byte[] strBytes = strByteList.getUnsafeBytes();
             ByteList dataByteList = ptr.string.getByteList();
             byte[] dataBytes = dataByteList.getUnsafeBytes();
             System.arraycopy(dataBytes, dataByteList.getBegin() + ptr.pos, strBytes, strByteList.getBegin(), len);
             if (binary) {
                 ((RubyString)str).setEncoding(ASCIIEncoding.INSTANCE);
             } else {
                 ((RubyString)str).setEncoding(ptr.string.getEncoding());
             }
         }
         ptr.pos += ((RubyString)str).size();
         return str;
     }
     
     private IRubyObject read18(IRubyObject[] args) {
         checkReadable();
 
         ByteList buf = null;
         int length = 0;
         int oldLength = 0;
         RubyString originalString = null;
 
         switch (args.length) {
         case 2:
             originalString = args[1].convertToString();
             // must let original string know we're modifying, so shared buffers aren't damaged
             originalString.modify();
             buf = originalString.getByteList();
         case 1:
             if (!args[0].isNil()) {
                 length = RubyNumeric.fix2int(args[0]);
                 oldLength = length;
 
                 if (length < 0) {
                     throw getRuntime().newArgumentError("negative length " + length + " given");
                 }
                 if (length > 0 && isEndOfString()) {
                     ptr.eof = true;
                     if (buf != null) buf.setRealSize(0);
                     return getRuntime().getNil();
                 } else if (ptr.eof) {
                     if (buf != null) buf.setRealSize(0);
                     return getRuntime().getNil();
                 }
                 break;
             }
         case 0:
             oldLength = -1;
             length = ptr.string.getByteList().length();
 
             if (length <= ptr.pos) {
                 ptr.eof = true;
                 if (buf == null) {
                     buf = new ByteList();
                 } else {
                     buf.setRealSize(0);
                 }
 
                 return makeString(getRuntime(), buf);
             } else {
                 length -= ptr.pos;
             }
             break;
         default:
             getRuntime().newArgumentError(args.length, 0);
         }
 
         if (buf == null) {
             int internalLength = ptr.string.getByteList().length();
 
             if (internalLength > 0) {
                 if (internalLength >= ptr.pos + length) {
                     buf = new ByteList(ptr.string.getByteList(), (int) ptr.pos, length);
                 } else {
                     int rest = (int) (ptr.string.getByteList().length() - ptr.pos);
 
                     if (length > rest) length = rest;
                     buf = new ByteList(ptr.string.getByteList(), (int) ptr.pos, length);
                 }
             }
         } else {
             int rest = (int) (ptr.string.getByteList().length() - ptr.pos);
 
             if (length > rest) length = rest;
 
             // Yow...this is still ugly
             byte[] target = buf.getUnsafeBytes();
             if (target.length > length) {
                 System.arraycopy(ptr.string.getByteList().getUnsafeBytes(), (int) ptr.pos, target, 0, length);
                 buf.setBegin(0);
                 buf.setRealSize(length);
             } else {
                 target = new byte[length];
                 System.arraycopy(ptr.string.getByteList().getUnsafeBytes(), (int) ptr.pos, target, 0, length);
                 buf.setBegin(0);
                 buf.setRealSize(length);
                 buf.setUnsafeBytes(target);
             }
         }
 
         if (buf == null) {
             if (!ptr.eof) buf = new ByteList();
             length = 0;
         } else {
             length = buf.length();
             ptr.pos += length;
         }
 
         if (oldLength < 0 || oldLength > length) ptr.eof = true;
 
         return originalString != null ? originalString : makeString(getRuntime(), buf);
     }
 
     @JRubyMethod(name="read_nonblock", compat = CompatVersion.RUBY1_9, optional = 2)
     @Override
     public IRubyObject read_nonblock(ThreadContext contet, IRubyObject[] args) {
         return sysreadCommon(args);
     }
 
     /**
      * readpartial(length, [buffer])
      *
      */
     @JRubyMethod(name ="readpartial", compat = CompatVersion.RUBY1_9, optional = 2)
     @Override
     public IRubyObject readpartial(ThreadContext context, IRubyObject[] args) {
         return sysreadCommon(args);
     }
 
-    @JRubyMethod(name = {"readchar", "readbyte"})
-    @Override
+    @JRubyMethod(name = {"readchar", "readbyte"}, compat = RUBY1_8)
     public IRubyObject readchar() {
         IRubyObject c = getc();
 
         if (c.isNil()) throw getRuntime().newEOFError();
 
         return c;
     }
 
     @JRubyMethod(name = "readchar", compat = CompatVersion.RUBY1_9)
-    @Override
     public IRubyObject readchar19(ThreadContext context) {
-        IRubyObject c = getc19(context);
+        IRubyObject c = callMethod(context, "getc");
+
+        if (c.isNil()) throw getRuntime().newEOFError();
+
+        return c;
+    }
+
+    @JRubyMethod(name = "readbyte", compat = CompatVersion.RUBY1_9)
+    public IRubyObject readbyte(ThreadContext context) {
+        IRubyObject c = callMethod(context, "getbyte");
 
         if (c.isNil()) throw getRuntime().newEOFError();
 
         return c;
     }
 
-    @JRubyMethod(name = "readline", optional = 1, writes = FrameField.LASTLINE)
+    @JRubyMethod(name = "readline", optional = 1, writes = FrameField.LASTLINE, compat = RUBY1_8)
+    public IRubyObject readline18(ThreadContext context, IRubyObject[] args) {
+        IRubyObject line = gets(context, args);
+
+        if (line.isNil()) throw getRuntime().newEOFError();
+
+        return line;
+    }
+
+    @JRubyMethod(name = "readline", optional = 1, writes = FrameField.LASTLINE, compat = RUBY1_9)
     @Override
     public IRubyObject readline(ThreadContext context, IRubyObject[] args) {
-        IRubyObject line = gets(context, args);
+        IRubyObject line = callMethod(context, "gets", args);
 
         if (line.isNil()) throw getRuntime().newEOFError();
 
         return line;
     }
 
     @JRubyMethod(name = "readlines", optional = 1, compat = RUBY1_8)
     public IRubyObject readlines(ThreadContext context, IRubyObject[] args) {
         return readlines19(context, args);
     }
 
     @JRubyMethod(name = "readlines", optional = 2, compat = RUBY1_9)
     public IRubyObject readlines19(ThreadContext context, IRubyObject[] args) {
         checkReadable();
         
         if (context.is19) {
             if (args.length > 0 && !args[args.length - 1].isNil() && args[args.length - 1].checkStringType19().isNil() &&
                     RubyNumeric.num2long(args[args.length - 1]) == 0) {
                 throw context.runtime.newArgumentError("invalid limit: 0 for each_line");
             }
         }
 
         RubyArray ary = context.runtime.newArray();
         while (!(isEOF())) {
             IRubyObject line = context.is19 ? internalGets19(context, args) : internalGets18(context, args);
             if (line.isNil()) {
                 break;
             }
             ary.append(line);
         }
 
         return ary;
     }
 
     @JRubyMethod(name = "reopen", required = 0, optional = 2)
     @Override
     public IRubyObject reopen(IRubyObject[] args) {
         checkFrozen();
         
         if (args.length == 1 && !(args[0] instanceof RubyString)) {
             return initialize_copy(args[0]);
         }
 
         // reset the state
         doRewind();
         ptr.closedRead = false;
         ptr.closedWrite = false;
         return initialize(args, Block.NULL_BLOCK);
     }
 
     @JRubyMethod(name = "rewind")
     @Override
     public IRubyObject rewind() {
         checkFrozen();
         
         doRewind();
         return RubyFixnum.zero(getRuntime());
     }
 
     private void doRewind() {
         this.ptr.pos = 0;
         // used in 1.8 mode only
         this.ptr.eof = false;
         this.ptr.lineno = 0;
     }
     
     @Override
     @Deprecated
     public IRubyObject seek(IRubyObject[] args) {
         return seek(getRuntime().getCurrentContext(), args);
     }
 
     @JRubyMethod(required = 1, optional = 1)
     public IRubyObject seek(ThreadContext context, IRubyObject[] args) {
         checkFrozen();
         checkFinalized();
         
         int offset = RubyNumeric.num2int(args[0]);
         IRubyObject whence = context.nil;
 
         if (args.length > 1 && !args[0].isNil()) whence = args[1];
         
         checkOpen();
 
         switch (whence.isNil() ? 0 : RubyNumeric.num2int(whence)) {
             case 0:
                 break;
             case 1:
                 offset += ptr.pos;
                 break;
             case 2:
                 offset += ptr.string.size();
                 break;
             default:
                 throw getRuntime().newErrnoEINVALError("invalid whence");
         }
         
         if (offset < 0) throw getRuntime().newErrnoEINVALError("invalid seek value");
 
         ptr.pos = offset;
         // used in 1.8 mode only
         ptr.eof = false;
 
         return RubyFixnum.zero(getRuntime());
     }
 
     @JRubyMethod(name = "string=", required = 1)
     @Override
     public IRubyObject set_string(IRubyObject arg) {
         checkFrozen();
         RubyString str = arg.convertToString();
         ptr.modes = ModeFlags.createModeFlags(str.isFrozen() ? ModeFlags.RDONLY : ModeFlags.RDWR);
         ptr.pos = 0;
         ptr.lineno = 0;
         return ptr.string = str;
     }
 
     @JRubyMethod(name = "sync=", required = 1)
     @Override
     public IRubyObject set_sync(IRubyObject args) {
         checkFrozen();
         
         return args;
     }
 
     @JRubyMethod(name = "string")
     @Override
     public IRubyObject string() {
         if (ptr.string == null) return getRuntime().getNil();
 
         return ptr.string;
     }
 
     @JRubyMethod(name = "sync")
     @Override
     public IRubyObject sync() {
         return getRuntime().getTrue();
     }
 
-    @JRubyMethod(name = "sysread", optional = 2)
-    @Override
-    public IRubyObject sysread(IRubyObject[] args) {
+    @JRubyMethod(name = "sysread", optional = 2, compat = RUBY1_8)
+    public IRubyObject sysread18(IRubyObject[] args) {
         return sysreadCommon(args);
     }
     
+    @JRubyMethod(name = "sysread", optional = 2, compat = RUBY1_9)
+    public IRubyObject sysread(ThreadContext context, IRubyObject[] args) {
+        IRubyObject val = callMethod(context, "read", args);
+        
+        if (val.isNil()) throw getRuntime().newEOFError();
+        
+        return val;
+    }
+    
+    // only here for the fake-out class in org.jruby
+    @Override
+    public IRubyObject sysread(IRubyObject[] args) {
+        return sysread(getRuntime().getCurrentContext(), args);
+    }
 
     private IRubyObject sysreadCommon(IRubyObject[] args) {
         IRubyObject obj = read(args);
 
         if (isEOF() && obj.isNil()) throw getRuntime().newEOFError();
 
         return obj;        
     }
 
     @JRubyMethod(name = "truncate", required = 1)
     @Override
     public IRubyObject truncate(IRubyObject arg) {
         checkWritable();
 
         int len = RubyFixnum.fix2int(arg);
         if (len < 0) {
             throw getRuntime().newErrnoEINVALError("negative legnth");
         }
 
         ptr.string.modify();
         ByteList buf = ptr.string.getByteList();
         if (len < buf.length()) {
             Arrays.fill(buf.getUnsafeBytes(), len, buf.length(), (byte) 0);
         }
         buf.length(len);
         return arg;
     }
 
-    @JRubyMethod(name = "ungetc", required = 1)
+    @JRubyMethod(name = "ungetc", required = 1, compat = RUBY1_8)
     @Override
     public IRubyObject ungetc(IRubyObject arg) {
         checkReadable();
 
         int c = RubyNumeric.num2int(arg);
         if (ptr.pos == 0) return getRuntime().getNil();
         ungetbyteCommon(c);
         return getRuntime().getNil();
     }
 
-    @JRubyMethod(name = "ungetc", compat = CompatVersion.RUBY1_9)
+    @JRubyMethod(name = "ungetc", compat = RUBY1_9)
     @Override
     public IRubyObject ungetc19(ThreadContext context, IRubyObject arg) {
         return ungetbyte(context, arg);
     }
 
     private void ungetbyteCommon(int c) {
         ptr.string.modify();
         ptr.pos--;
         
         ByteList bytes = ptr.string.getByteList();
 
         if (isEndOfString()) bytes.length((int)ptr.pos + 1);
 
         if (ptr.pos == -1) {
             bytes.prepend((byte)c);
             ptr.pos = 0;
         } else {
             bytes.set((int) ptr.pos, c);
         }
     }
 
     private void ungetbyteCommon(RubyString ungetBytes) {
         ByteList ungetByteList = ungetBytes.getByteList();
         int len = ungetByteList.getRealSize();
         int start = ptr.pos;
         
         if (len == 0) return;
         
         ptr.string.modify();
         
         if (len > ptr.pos) {
             start = 0;
         } else {
             start = ptr.pos - len;
         }
         
         ByteList bytes = ptr.string.getByteList();
         
         if (isEndOfString()) bytes.length(Math.max(ptr.pos, len));
 
         bytes.replace(start, ptr.pos - start, ungetBytes.getByteList());
         
         ptr.pos = start;
     }
     
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject ungetbyte(ThreadContext context, IRubyObject arg) {
         checkReadable();
         
         if (arg.isNil()) return arg;
 
         if (arg instanceof RubyFixnum) {
             ungetbyteCommon(RubyNumeric.fix2int(arg));
         } else {
             ungetbyteCommon(arg.convertToString());
         }
 
         return context.nil;
     }
 
     @JRubyMethod(name = {"write", "write_nonblock", "syswrite"}, required = 1)
     @Override
     public IRubyObject write(ThreadContext context, IRubyObject arg) {
         return context.runtime.newFixnum(writeInternal18(context, arg));
     }
     
     // This logic was ported from MRI, but did not appear to match behavior for
     // unknown reasons.
     private int writeInternal19(ThreadContext context, IRubyObject arg) {
         checkWritable();
         checkFrozen();
 
         RubyString str = arg.asString();
         int len;
         int olen;
         
         // This logic exists in MRI, but I can't figure out when and why it
         // actually fires. With it in place, incoming bytes should get
         // transcoded, but they don't appear to do so. Baffling.
         /*
         Encoding enc;
         Encoding enc2;
 
         enc = ptr.string.getEncoding();
         enc2 = str.getEncoding();
         if (enc != enc2 && enc != ASCIIEncoding.INSTANCE) {
             str = context.runtime.newString(CharsetTranscoder.transcode(context, str.getByteList(), enc2, enc, context.nil));
         }
         */
         
         len = str.size();
         if (len == 0) return 0;
         checkWritable();
         olen = ptr.string.size();
         if (ptr.modes.isAppendable()) {
             ptr.pos = olen;
         }
         if (ptr.pos < olen) {
             ptr.string.resize(olen + len);
         }
         ptr.string.getByteList().append(str.getByteList());
         ptr.string.setCodeRange(StringSupport.CR_UNKNOWN);
         
         ptr.pos += len;
         
         return len;
     }
 
     private int writeInternal18(ThreadContext context, IRubyObject arg) {
         checkWritable();
 
         RubyString val = arg.asString();
         ptr.string.modify();
 
         if (ptr.modes.isAppendable()) {
             ptr.string.getByteList().append(val.getByteList());
             ptr.pos = ptr.string.getByteList().length();
         } else {
             int left = ptr.string.getByteList().length()-(int)ptr.pos;
             ptr.string.getByteList().replace((int)ptr.pos,Math.min(val.getByteList().length(),left),val.getByteList());
             ptr.pos += val.getByteList().length();
         }
 
         if (val.isTaint()) {
             ptr.string.setTaint(true);
         }
 
         return val.getByteList().length();
     }
     
     @JRubyMethod(compat = RUBY1_9)
     @Override
     public IRubyObject set_encoding(ThreadContext context, IRubyObject enc) {
         if (enc.isNil()) {
             enc = context.runtime.getEncodingService().getDefaultExternal();
         }
         Encoding encoding = context.runtime.getEncodingService().getEncodingFromObject(enc);
         ptr.string.setEncoding(encoding);
         return this;
     }
     
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject enc, IRubyObject ignored) {
         return set_encoding(context, enc);
     }
     
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject enc, IRubyObject ignored1, IRubyObject ignored2) {
         return set_encoding(context, enc);
     }
     
     @JRubyMethod(compat = RUBY1_9)
     @Override
     public IRubyObject external_encoding(ThreadContext context) {
         return context.runtime.getEncodingService().convertEncodingToRubyEncoding(ptr.string.getEncoding());
     }
     
     @JRubyMethod(compat = RUBY1_9)
     @Override
     public IRubyObject internal_encoding(ThreadContext context) {
         return context.nil;
     }
     
     @JRubyMethod(name = {"each_codepoint", "codepoints"}, compat = RUBY1_9)
     public IRubyObject each_codepoint(ThreadContext context, Block block) {
         return block.isGiven() ? eachCodepointInternal(context, block) : enumeratorize(context.runtime, this, "each_codepoint");
     }
     
     private IRubyObject eachCodepointInternal(ThreadContext context, Block block) {
         Ruby runtime = context.runtime;
         checkReadable();
         
         Encoding enc = ptr.string.getEncoding();
         byte[] unsafeBytes = ptr.string.getByteList().getUnsafeBytes();
         int begin = ptr.string.getByteList().getBegin();
         for (;;) {
             if (ptr.pos >= ptr.string.size()) {
                 return this;
             }
             
             int c = StringSupport.codePoint(runtime, enc, unsafeBytes, begin + ptr.pos, unsafeBytes.length);
             int n = StringSupport.codeLength(runtime, enc, c);
             block.yield(context, runtime.newFixnum(c));
             ptr.pos += n;
         }
     }
 
     /* rb: check_modifiable */
     @Override
     public void checkFrozen() {
         super.checkFrozen();
         checkInitialized();
-        if (ptr.string.isFrozen()) throw getRuntime().newIOError("not modifiable string");
     }
 
     /* rb: readable */
     private void checkReadable() {
         checkFrozen();
         
         checkInitialized();
         if (ptr.closedRead || !ptr.modes.isReadable()) {
             throw getRuntime().newIOError("not opened for reading");
         }
     }
 
     /* rb: writable */
     private void checkWritable() {
         checkFrozen();
         checkInitialized();
+        if (ptr.string.isFrozen()) throw getRuntime().newIOError("not modifiable string");
         if (ptr.closedWrite || !ptr.modes.isWritable()) {
             throw getRuntime().newIOError("not opened for writing");
         }
 
         // Tainting here if we ever want it. (secure 4)
     }
 
     private void checkInitialized() {
         if (ptr.modes == null) {
             throw getRuntime().newIOError("uninitialized stream");
         }
     }
 
     private void checkFinalized() {
         if (ptr.string == null) {
             throw getRuntime().newIOError("not opened");
         }
     }
 
     private void checkOpen() {
         if (ptr.closedRead && ptr.closedWrite) {
             throw getRuntime().newIOError("closed stream");
         }
     }
     
     private void setupModes() {
         ptr.closedWrite = false;
         ptr.closedRead = false;
 
         if (ptr.modes.isReadOnly()) ptr.closedWrite = true;
         if (!ptr.modes.isReadable()) ptr.closedRead = true;
     }
 
     
     @Override
     @Deprecated
     public IRubyObject read(IRubyObject[] args) {
         if (getRuntime().is1_9()) {
             return read19(args);
         }
         return read18(args);
     }
 }
diff --git a/spec/regression/GH-1008_stringio_fails_read_if_frozen_string_spec.rb b/spec/regression/GH-1008_stringio_fails_read_if_frozen_string_spec.rb
new file mode 100644
index 0000000000..63f3d812fb
--- /dev/null
+++ b/spec/regression/GH-1008_stringio_fails_read_if_frozen_string_spec.rb
@@ -0,0 +1,11 @@
+require 'rspec'
+require 'stringio'
+
+describe "StringIO#read" do
+  it "works when the contained string is frozen" do
+    str = "Hello".freeze
+    strio = StringIO.new(str)
+
+    strio.read.should == str
+  end
+end
