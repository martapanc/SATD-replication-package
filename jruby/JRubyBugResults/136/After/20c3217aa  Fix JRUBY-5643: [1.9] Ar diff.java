diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index c7410a9fe5..acdddb3db1 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1328,2005 +1328,2010 @@ public class RubyArray extends RubyObject implements List, RandomAccess {
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 1, 2);
             return null; // not reached
         }
     }
 
     /** rb_ary_aref
      */
     @JRubyMethod(name = {"[]", "slice"}, compat = RUBY1_8)
     public IRubyObject aref(IRubyObject arg0) {
         assert !arg0.getRuntime().is1_9();
         if (arg0 instanceof RubyFixnum) return entry(((RubyFixnum)arg0).getLongValue());
         if (arg0 instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
         return arefCommon(arg0);
     }
 
     @JRubyMethod(name = {"[]", "slice"}, compat = RUBY1_9)
     public IRubyObject aref19(IRubyObject arg0) {
         return arg0 instanceof RubyFixnum ? entry(((RubyFixnum)arg0).getLongValue()) : arefCommon(arg0); 
     }
 
     private IRubyObject arefCommon(IRubyObject arg0) {
         if (arg0 instanceof RubyRange) {
             long[] beglen = ((RubyRange) arg0).begLen(realLength, 0);
             return beglen == null ? getRuntime().getNil() : subseq(beglen[0], beglen[1]);
         }
         return entry(RubyNumeric.num2long(arg0));
     }
 
     @JRubyMethod(name = {"[]", "slice"}, compat = RUBY1_8)
     public IRubyObject aref(IRubyObject arg0, IRubyObject arg1) {
         assert !arg0.getRuntime().is1_9();
         if (arg0 instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
         return arefCommon(arg0, arg1);
     }
 
     @JRubyMethod(name = {"[]", "slice"}, compat = RUBY1_9)
     public IRubyObject aref19(IRubyObject arg0, IRubyObject arg1) {
         return arefCommon(arg0, arg1);
     }
 
     private IRubyObject arefCommon(IRubyObject arg0, IRubyObject arg1) {
         long beg = RubyNumeric.num2long(arg0);
         if (beg < 0) beg += realLength;
         return subseq(beg, RubyNumeric.num2long(arg1));
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * @deprecated Use the versions with zero, one, or two args.
      */
     public IRubyObject aset(IRubyObject[] args) {
         switch (args.length) {
         case 2:
             return aset(args[0], args[1]);
         case 3:
             return aset(args[0], args[1], args[2]);
         default:
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 2)");
         }
     }
 
     @JRubyMethod(name = "[]=", compat = RUBY1_8)
     public IRubyObject aset(IRubyObject arg0, IRubyObject arg1) {
         assert !getRuntime().is1_9();
         if (arg0 instanceof RubyFixnum) {
             store(((RubyFixnum)arg0).getLongValue(), arg1);
         } else if (arg0 instanceof RubyRange) {
             long[] beglen = ((RubyRange) arg0).begLen(realLength, 1);
             splice(beglen[0], beglen[1], arg1, false);
         } else if(arg0 instanceof RubySymbol) {
             throw getRuntime().newTypeError("Symbol as array index");
         } else {
             store(RubyNumeric.num2long(arg0), arg1);
         }
         return arg1;
     }
 
     @JRubyMethod(name = "[]=", compat = RUBY1_9)
     public IRubyObject aset19(IRubyObject arg0, IRubyObject arg1) {
         modifyCheck();
         if (arg0 instanceof RubyFixnum) {
             store(((RubyFixnum)arg0).getLongValue(), arg1);
         } else if (arg0 instanceof RubyRange) {
             long[] beglen = ((RubyRange) arg0).begLen(realLength, 1);
             splice(beglen[0], beglen[1], arg1, true);
         } else {
             store(RubyNumeric.num2long(arg0), arg1);
         }
         return arg1;
     }
 
     /** rb_ary_aset
     *
     */
     @JRubyMethod(name = "[]=", compat = RUBY1_8)
     public IRubyObject aset(IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         assert !getRuntime().is1_9();
         if (arg0 instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
         if (arg1 instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as subarray length");
         splice(RubyNumeric.num2long(arg0), RubyNumeric.num2long(arg1), arg2, false);
         return arg2;
     }
 
     @JRubyMethod(name = "[]=", compat = RUBY1_9)
     public IRubyObject aset19(IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         modifyCheck();
         splice(RubyNumeric.num2long(arg0), RubyNumeric.num2long(arg1), arg2, true);
         return arg2;
     }
 
     /** rb_ary_at
      *
      */
     @JRubyMethod(name = "at", required = 1)
     public IRubyObject at(IRubyObject pos) {
         return entry(RubyNumeric.num2long(pos));
     }
 
 	/** rb_ary_concat
      *
      */
     @JRubyMethod(name = "concat", required = 1, compat = RUBY1_8)
     public RubyArray concat(IRubyObject obj) {
         RubyArray ary = obj.convertToArray();
         
         if (ary.realLength > 0) splice(realLength, 0, ary, false);
 
         return this;
     }
 
     @JRubyMethod(name = "concat", required = 1, compat = RUBY1_9)
     public RubyArray concat19(IRubyObject obj) {
         modifyCheck();
 
         return concat(obj);
     }
 
     /** inspect_ary
      * 
      */
     private IRubyObject inspectAry(ThreadContext context) {
         Encoding encoding = context.runtime.getDefaultInternalEncoding();
         if (encoding == null) encoding = USASCIIEncoding.INSTANCE;
         RubyString str = RubyString.newStringLight(context.runtime, DEFAULT_INSPECT_STR_SIZE, encoding);
         str.cat((byte)'[');
         boolean tainted = isTaint();
         boolean untrust = isUntrusted();
 
         for (int i = 0; i < realLength; i++) {
             if (i > 0) str.cat((byte)',').cat((byte)' ');
 
             RubyString str2 = inspect(context, safeArrayRef(values, begin + i));
             if (str2.isTaint()) tainted = true;
             if (str2.isUntrusted()) untrust = true;
             
             // safe for both 1.9 and 1.8
             str.cat19(str2);
         }
         str.cat((byte)']');
 
         if (tainted) str.setTaint(true);
         if (untrust) str.setUntrusted(true);
 
         return str;
     }
 
     /** rb_ary_inspect
     *
     */
     @JRubyMethod(name = "inspect")
     @Override
     public IRubyObject inspect() {
         if (realLength == 0) return getRuntime().newString("[]");
         if (getRuntime().isInspecting(this)) return  getRuntime().newString("[...]");
 
         try {
             getRuntime().registerInspecting(this);
             return inspectAry(getRuntime().getCurrentContext());
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * @deprecated Use the versions with zero, one, or two args.
      */
     public IRubyObject first(IRubyObject[] args) {
         switch (args.length) {
         case 0:
             return first();
         case 1:
             return first(args[0]);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 0, 1);
             return null; // not reached
         }
     }
 
     /** rb_ary_first
      *
      */
     @JRubyMethod(name = "first")
     public IRubyObject first() {
         if (realLength == 0) return getRuntime().getNil();
         return values[begin];
     }
 
     /** rb_ary_first
     *
     */
     @JRubyMethod(name = "first")
     public IRubyObject first(IRubyObject arg0) {
         long n = RubyNumeric.num2long(arg0);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
         }
 
         return makeShared(begin, (int) n, getRuntime().getArray());
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * @deprecated Use the versions with zero, one, or two args.
      */
     public IRubyObject last(IRubyObject[] args) {
         switch (args.length) {
         case 0:
             return last();
         case 1:
             return last(args[0]);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 0, 1);
             return null; // not reached
         }
     }
 
     /** rb_ary_last
      *
      */
     @JRubyMethod(name = "last")
     public IRubyObject last() {
         if (realLength == 0) return getRuntime().getNil();
         return values[begin + realLength - 1];
     }
 
     /** rb_ary_last
     *
     */
     @JRubyMethod(name = "last")
     public IRubyObject last(IRubyObject arg0) {
         long n = RubyNumeric.num2long(arg0);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
         }
 
         return makeShared(begin + realLength - (int) n, (int) n, getRuntime().getArray());
     }
 
     /** rb_ary_each
      *
      */
     public IRubyObject eachCommon(ThreadContext context, Block block) {
         if (!block.isGiven()) {
             throw context.getRuntime().newLocalJumpErrorNoBlock();
         }
         for (int i = 0; i < realLength; i++) {
             // do not coarsen the "safe" catch, since it will misinterpret AIOOBE from the yielded code.
             // See JRUBY-5434
             block.yield(context, safeArrayRef(values, begin + i));
         }
         return this;
     }
 
     @JRubyMethod
     public IRubyObject each(ThreadContext context, Block block) {
         return block.isGiven() ? eachCommon(context, block) : enumeratorize(context.getRuntime(), this, "each");
     }
 
     public IRubyObject eachSlice(ThreadContext context, int size, Block block) {
         Ruby runtime = context.getRuntime();
 
         // local copies of everything
         int localRealLength = realLength;
         IRubyObject[] localValues = values;
         int localBegin = begin;
 
         // sliding window
         RubyArray window = newArrayNoCopy(runtime, localValues, localBegin, size);
         makeShared();
 
         // don't expose shared array to ruby
         final boolean specificArity = (block.arity().isFixed()) && (block.arity().required() != 1);
         
         for (; localRealLength >= size; localRealLength -= size) {
             block.yield(context, window);
             if (specificArity) { // array is never exposed to ruby, just use for yielding
                 window.begin = localBegin += size;
             } else { // array may be exposed to ruby, create new
                 window = newArrayNoCopy(runtime, localValues, localBegin += size, size);
             }
         }
 
         // remainder
         if (localRealLength > 0) {
             window.realLength = localRealLength;
             block.yield(context, window);
         }
         return runtime.getNil();
     }
 
     @JRubyMethod
     public IRubyObject each_slice(ThreadContext context, IRubyObject arg, Block block) {
         final int size = RubyNumeric.num2int(arg);
         final Ruby runtime = context.getRuntime();
         if (size <= 0) throw runtime.newArgumentError("invalid slice size");
         return block.isGiven() ? eachSlice(context, size, block) : enumeratorize(context.getRuntime(), this, "each_slice", arg);
     }
 
     /** rb_ary_each_index
      *
      */
     public IRubyObject eachIndex(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) {
             throw runtime.newLocalJumpErrorNoBlock();
         }
         for (int i = 0; i < realLength; i++) {
             block.yield(context, runtime.newFixnum(i));
         }
         return this;
     }
     
     @JRubyMethod
     public IRubyObject each_index(ThreadContext context, Block block) {
         return block.isGiven() ? eachIndex(context, block) : enumeratorize(context.getRuntime(), this, "each_index");
     }
 
     /** rb_ary_reverse_each
      *
      */
     public IRubyObject reverseEach(ThreadContext context, Block block) {
         int len = realLength;
 
         while(len-- > 0) {
             // do not coarsen the "safe" catch, since it will misinterpret AIOOBE from the yielded code.
             // See JRUBY-5434
             block.yield(context, safeArrayRef(values, begin + len));
             if (realLength < len) len = realLength;
         }
 
         return this;
     }
 
     @JRubyMethod
     public IRubyObject reverse_each(ThreadContext context, Block block) {
         return block.isGiven() ? reverseEach(context, block) : enumeratorize(context.getRuntime(), this, "reverse_each");
     }
 
     private IRubyObject inspectJoin(ThreadContext context, RubyArray tmp, IRubyObject sep) {
         Ruby runtime = context.getRuntime();
 
         // If already inspecting, there is no need to register/unregister again.
         if (runtime.isInspecting(this)) {
             return tmp.join(context, sep);
         }
 
         try {
             runtime.registerInspecting(this);
             return tmp.join(context, sep);
         } finally {
             runtime.unregisterInspecting(this);
         }
     }
 
     /** rb_ary_join
      *
      */
     @JRubyMethod(name = "join", compat = RUBY1_8)
     public IRubyObject join(ThreadContext context, IRubyObject sep) {
         final Ruby runtime = context.getRuntime();
         if (realLength == 0) return RubyString.newEmptyString(runtime);
 
         boolean taint = isTaint() || sep.isTaint();
         boolean untrusted = isUntrusted() || sep.isUntrusted();
 
         int len = 1;
         for (int i = begin; i < begin + realLength; i++) {
             // do not coarsen the "safe" catch, since it will misinterpret AIOOBE from to_str.
             // See JRUBY-5434
             IRubyObject value = safeArrayRef(values, i);
             IRubyObject tmp = value.checkStringType();
             len += tmp.isNil() ? 10 : ((RubyString) tmp).getByteList().length();
         }
 
         ByteList sepBytes = null;
         if (!sep.isNil()) {
             sepBytes = sep.convertToString().getByteList();
             len += sepBytes.getRealSize() * (realLength - 1);
         }
 
         ByteList buf = new ByteList(len);
         for (int i = 0; i < realLength; i++) {
             // do not coarsen the "safe" catch, since it will misinterpret AIOOBE from inspect.
             // See JRUBY-5434
             IRubyObject tmp = safeArrayRef(values, begin + i);
             if (!(tmp instanceof RubyString)) {
                 if (tmp instanceof RubyArray) {
                     if (tmp == this || runtime.isInspecting(tmp)) {
                         tmp = runtime.newString("[...]");
                     } else {
                         tmp = inspectJoin(context, (RubyArray)tmp, sep);
                     }
                 } else {
                     tmp = RubyString.objAsString(context, tmp);
                 }
             }
 
             if (i > 0 && sepBytes != null) buf.append(sepBytes);
 
             buf.append(tmp.asString().getByteList());
             if (tmp.isTaint()) taint = true;
             if (tmp.isUntrusted()) untrusted = true;
         }
 
         RubyString result = runtime.newString(buf); 
         if (taint) result.setTaint(true);
         if (untrusted) result.untrust(context);
         
         return result;
     }
 
     @JRubyMethod(name = "join", compat = RUBY1_8)
     public IRubyObject join(ThreadContext context) {
         return join(context, context.getRuntime().getGlobalVariables().get("$,"));
     }
 
     // 1.9 MRI: join0
     private RubyString joinStrings(RubyString sep, int max, RubyString result) {
         try {
             for(int i = begin; i < max; i++) {
                 if (i > begin && sep != null) result.append19(sep);
                 result.append19(values[i]);
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         
         return result;
     }
 
     // 1.9 MRI: join1
     private RubyString joinAny(ThreadContext context, IRubyObject obj, RubyString sep, 
             int i, RubyString result) {
         RubyClass stringClass = context.getRuntime().getString();
         RubyClass arrayClass = context.getRuntime().getArray();
 
         for (; i < begin + realLength; i++) {
             if (i > begin && sep != null) result.append19(sep);
 
             IRubyObject val = safeArrayRef(values, i);
 
             if (val instanceof RubyString) {
                 result.append19(val);
             } else if (val instanceof RubyArray) {
                 obj = val;
                 recursiveJoin(context, obj, sep, result, val);
             } else {
                 IRubyObject tmp = val.checkStringType19();
                 if (tmp.isNil()) tmp = TypeConverter.convertToTypeWithCheck(val, stringClass, "to_s");
 
                 if (!tmp.isNil()) {
                     result.append19(tmp);
                 } else {
                     tmp = TypeConverter.convertToTypeWithCheck(val, arrayClass, "to_a");
                     if(!tmp.isNil()) {
                         obj = val;
                         recursiveJoin(context, obj, sep, result, tmp);
                     } else {
                         result.append19(RubyString.objAsString(context, val));
                     }
                 }
             }
         }
         
         return result;
     }
 
     private void recursiveJoin(final ThreadContext context, final IRubyObject outValue,
             final RubyString sep, final RubyString result, final IRubyObject ary) {
         final Ruby runtime = context.getRuntime();
         
         if (ary == this) throw runtime.newArgumentError("recursive array join");
             
         runtime.execRecursive(new Ruby.RecursiveFunction() {
             public IRubyObject call(IRubyObject obj, boolean recur) {
                 if (recur) throw runtime.newArgumentError("recursive array join");
                             
                 ((RubyArray) ary).joinAny(context, outValue, sep, 0, result);
                 
                 return runtime.getNil();
             }}, outValue);
     }
 
     /** rb_ary_join
      *
      */
     @JRubyMethod(name = "join", compat = RUBY1_9)
     public IRubyObject join19(ThreadContext context, IRubyObject sep) {
         final Ruby runtime = context.getRuntime();
         if (realLength == 0) return RubyString.newEmptyString(runtime);
 
         int len = 1;
         RubyString sepString = null;
         if (!sep.isNil()) {
             sepString = sep.convertToString();
             len += sepString.size() * (realLength - 1);
         }
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject val = safeArrayRef(values, i);
             IRubyObject tmp = val.checkStringType19();
             if (tmp.isNil() || tmp != val) {
                 len += ((begin + realLength) - i) * 10;
                 RubyString result = (RubyString) RubyString.newStringLight(runtime, len).infectBy(this);
 
                 return joinAny(context, this, sepString, i, joinStrings(sepString, i, result));
             }
 
             len += ((RubyString) tmp).getByteList().length();
         }
 
         return joinStrings(sepString, begin + realLength, 
                 (RubyString) RubyString.newStringLight(runtime, len).infectBy(this));
     }
 
     @JRubyMethod(name = "join", compat = RUBY1_9)
     public IRubyObject join19(ThreadContext context) {
         return join19(context, context.getRuntime().getGlobalVariables().get("$,"));
     }
 
 
     /** rb_ary_to_a
      *
      */
     @JRubyMethod(name = "to_a")
     @Override
     public RubyArray to_a() {
         if(getMetaClass() != getRuntime().getArray()) {
             RubyArray dup = new RubyArray(getRuntime(), getRuntime().isObjectSpaceEnabled());
 
             isShared = true;
             dup.isShared = true;
             dup.values = values;
             dup.realLength = realLength; 
             dup.begin = begin;
             
             return dup;
         }        
         return this;
     }
 
     @JRubyMethod(name = "to_ary")
     public IRubyObject to_ary() {
     	return this;
     }
 
     @Override
     public RubyArray convertToArray() {
         return this;
     }
     
     @Override
     public IRubyObject checkArrayType(){
         return this;
     }
 
     /** rb_ary_equal
      *
      */
     @JRubyMethod(name = "==", required = 1)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject obj) {
         if (this == obj) {
             return context.getRuntime().getTrue();
         }
         if (!(obj instanceof RubyArray)) {
             if (!obj.respondsTo("to_ary")) {
                 return context.getRuntime().getFalse();
             }
             return RuntimeHelpers.rbEqual(context, obj, this);
         }
         return RecursiveComparator.compare(context, MethodIndex.OP_EQUAL, this, obj);
     }
 
     public RubyBoolean compare(ThreadContext context, int method, IRubyObject other) {
 
         Ruby runtime = context.getRuntime();
 
         if (!(other instanceof RubyArray)) {
             if (!other.respondsTo("to_ary")) {
                 return runtime.getFalse();
             } else {
                 return RuntimeHelpers.rbEqual(context, other, this);
             }
         }
 
         RubyArray ary = (RubyArray) other;
 
         if (realLength != ary.realLength) {
             return runtime.getFalse();
         }
 
         for (int i = 0; i < realLength; i++) {
             if (!invokedynamic(context, elt(i), method, ary.elt(i)).isTrue()) {
                 return runtime.getFalse();
             }
         }
 
         return runtime.getTrue();
     }
 
     /** rb_ary_eql
      *
      */
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql(ThreadContext context, IRubyObject obj) {
         return RecursiveComparator.compare(context, MethodIndex.EQL, this, obj);
     }
 
     /** rb_ary_compact_bang
      *
      */
     @JRubyMethod(name = "compact!")
     public IRubyObject compact_bang() {
         modify();
 
         int p = begin;
         int t = p;
         int end = p + realLength;
 
         try {
             while (t < end) {
                 if (values[t].isNil()) {
                     t++;
                 } else {
                     values[p++] = values[t++];
                 }
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
 
         p -= begin;
         if (realLength == p) return getRuntime().getNil();
 
         realloc(p, values.length - begin);
         realLength = p;
         return this;
     }
 
     /** rb_ary_compact
      *
      */
     @JRubyMethod(name = "compact", compat = RUBY1_8)
     public IRubyObject compact() {
         RubyArray ary = aryDup();
         ary.compact_bang();
         return ary;
     }
     
     @JRubyMethod(name = "compact", compat = RUBY1_9)
     public IRubyObject compatc19() {
         RubyArray ary = aryDup19();
         ary.compact_bang();
         return ary;
     }
 
     /** rb_ary_empty_p
      *
      */
     @JRubyMethod(name = "empty?")
     public IRubyObject empty_p() {
         return realLength == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_ary_clear
      *
      */
     @JRubyMethod(name = "clear")
     public IRubyObject rb_clear() {
         modifyCheck();
 
         if (isShared) {
             alloc(ARRAY_DEFAULT_SIZE);
             isShared = false;
         } else if (values.length > ARRAY_DEFAULT_SIZE << 1) {
             alloc(ARRAY_DEFAULT_SIZE << 1);
         } else {
             try {
                 begin = 0;
                 RuntimeHelpers.fillNil(values, 0, realLength, getRuntime());
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
             }
         }
 
         realLength = 0;
         return this;
     }
 
     @JRubyMethod
     public IRubyObject fill(ThreadContext context, Block block) {
         if (block.isGiven()) return fillCommon(context, 0, realLength, block);
         throw context.getRuntime().newArgumentError(0, 1);
     }
 
     @JRubyMethod
     public IRubyObject fill(ThreadContext context, IRubyObject arg, Block block) {
         if (block.isGiven()) {
             if (arg instanceof RubyRange) {
                 int[] beglen = ((RubyRange) arg).begLenInt(realLength, 1);
                 return fillCommon(context, beglen[0], beglen[1], block);
             }
             int beg;
             return fillCommon(context, beg = fillBegin(arg), fillLen(beg, null),  block);
         } else {
             return fillCommon(context, 0, realLength, arg);
         }
     }
 
     @JRubyMethod
     public IRubyObject fill(ThreadContext context, IRubyObject arg1, IRubyObject arg2, Block block) {
         if (block.isGiven()) {
             int beg;
             return fillCommon(context, beg = fillBegin(arg1), fillLen(beg, arg2), block);
         } else {
             if (arg2 instanceof RubyRange) {
                 int[] beglen = ((RubyRange) arg2).begLenInt(realLength, 1);
                 return fillCommon(context, beglen[0], beglen[1], arg1);
             }
             int beg;
             return fillCommon(context, beg = fillBegin(arg2), fillLen(beg, null), arg1);
         }
     }
 
     @JRubyMethod
     public IRubyObject fill(ThreadContext context, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
         if (block.isGiven()) {
             throw context.getRuntime().newArgumentError(3, 2);
         } else {
             int beg;
             return fillCommon(context, beg = fillBegin(arg2), fillLen(beg, arg3), arg1);
         }
     }
 
     private int fillBegin(IRubyObject arg) {
         int beg = arg.isNil() ? 0 : RubyNumeric.num2int(arg);
         if (beg < 0) {
             beg = realLength + beg;
             if (beg < 0) beg = 0;
         }
         return beg;
     }
 
     private long fillLen(long beg, IRubyObject arg) {
         if (arg == null || arg.isNil()) {
             return realLength - beg;
         } else {
             return RubyNumeric.num2long(arg);
         }
         // TODO: In MRI 1.9, an explicit check for negative length is
         // added here. IndexError is raised when length is negative.
         // See [ruby-core:12953] for more details.
         //
         // New note: This is actually under re-evaluation,
         // see [ruby-core:17483].
     }
 
     private IRubyObject fillCommon(ThreadContext context, int beg, long len, IRubyObject item) {
         modify();
 
         // See [ruby-core:17483]
         if (len < 0) return this;
 
         if (len > Integer.MAX_VALUE - beg) throw context.getRuntime().newArgumentError("argument too big");
 
         int end = (int)(beg + len);
         if (end > realLength) {
             int valuesLength = values.length - begin;
             if (end >= valuesLength) realloc(end, valuesLength);
             realLength = end;
         }
 
         if (len > 0) {
             try {
                 fill(values, begin + beg, begin + end, item);
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
             }
         }
 
         return this;
     }
 
     private IRubyObject fillCommon(ThreadContext context, int beg, long len, Block block) {
         modify();
 
         // See [ruby-core:17483]
         if (len < 0) return this;
 
         if (len > Integer.MAX_VALUE - beg) throw getRuntime().newArgumentError("argument too big");
 
         int end = (int)(beg + len);
         if (end > realLength) {
             int valuesLength = values.length - begin;
             if (end >= valuesLength) realloc(end, valuesLength);
             realLength = end;
         }
 
         Ruby runtime = context.getRuntime();
         for (int i = beg; i < end; i++) {
             IRubyObject v = block.yield(context, runtime.newFixnum(i));
             if (i >= realLength) break;
             safeArraySet(values, begin + i, v);
         }
         return this;
     }
 
 
     /** rb_ary_index
      *
      */
     public IRubyObject index(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
 
         for (int i = 0; i < realLength; i++) {
             if (equalInternal(context, eltOk(i), obj)) return runtime.newFixnum(i);
         }
 
         return runtime.getNil();
     }
 
     @JRubyMethod(name = {"index", "find_index"})
     public IRubyObject index(ThreadContext context, IRubyObject obj, Block unused) {
         if (unused.isGiven()) context.getRuntime().getWarnings().warn(ID.BLOCK_UNUSED, "given block not used");
         return index(context, obj); 
     }
 
     @JRubyMethod(name = {"index", "find_index"})
     public IRubyObject index(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "index");
 
         for (int i = 0; i < realLength; i++) {
             if (block.yield(context, eltOk(i)).isTrue()) return runtime.newFixnum(i);
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_rindex
      *
      */
     public IRubyObject rindex(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         int i = realLength;
 
         while (i-- > 0) {
             if (i > realLength) {
                 i = realLength;
                 continue;
             }
             if (equalInternal(context, eltOk(i), obj)) return runtime.newFixnum(i);
         }
 
         return runtime.getNil();
     }
 
     @JRubyMethod
     public IRubyObject rindex(ThreadContext context, IRubyObject obj, Block unused) {
         if (unused.isGiven()) context.getRuntime().getWarnings().warn(ID.BLOCK_UNUSED, "given block not used");
         return rindex(context, obj); 
     }
 
     @JRubyMethod
     public IRubyObject rindex(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "rindex");
 
         int i = realLength;
 
         while (i-- > 0) {
             if (i >= realLength) {
                 i = realLength;
                 continue;
             }
             if (block.yield(context, eltOk(i)).isTrue()) return runtime.newFixnum(i);
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_indexes
      * 
      */
     @JRubyMethod(name = {"indexes", "indices"}, required = 1, rest = true)
     public IRubyObject indexes(IRubyObject[] args) {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Array#indexes is deprecated; use Array#values_at");
 
         RubyArray ary = new RubyArray(getRuntime(), args.length);
 
         for (int i = 0; i < args.length; i++) {
             ary.append(aref(args[i]));
         }
 
         return ary;
     }
 
     /** rb_ary_reverse_bang
      *
      */
     @JRubyMethod(name = "reverse!")
     public IRubyObject reverse_bang() {
         modify();
 
         try {
             if (realLength > 1) {
                 IRubyObject[] vals = values;
                 int p = begin;
                 int len = realLength;
                 for (int i = 0; i < len >> 1; i++) {
                     IRubyObject tmp = vals[p + i];
                     vals[p + i] = vals[p + len - i - 1];
                     vals[p + len - i - 1] = tmp;
                 }
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         return this;
     }
 
     /** rb_ary_reverse_m
      *
      */
     @JRubyMethod(name = "reverse")
     public IRubyObject reverse() {
         if (realLength > 1) {
             RubyArray dup = safeReverse();
             dup.flags |= flags & TAINTED_F; // from DUP_SETUP
             dup.flags |= flags & UNTRUSTED_F; // from DUP_SETUP
             // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
             return dup;
         } else {
             return dup();
         }
     }
 
     private RubyArray safeReverse() {
         int length = realLength;
         int myBegin = this.begin;
         IRubyObject[] myValues = this.values;
         IRubyObject[] vals = new IRubyObject[length];
 
         try {
             for (int i = 0; i <= length >> 1; i++) {
                 vals[i] = myValues[myBegin + length - i - 1];
                 vals[length - i - 1] = myValues[myBegin + i];
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         return new RubyArray(getRuntime(), getMetaClass(), vals);
     }
 
     /** rb_ary_collect
      *
      */
     @JRubyMethod(name = {"collect", "map"}, compat = RUBY1_8)
     public IRubyObject collect(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return new RubyArray(runtime, runtime.getArray(), this);
 
         IRubyObject[] arr = new IRubyObject[realLength];
 
         for (int i = 0; i < realLength; i++) {
             // Do not coarsen the "safe" check, since it will misinterpret AIOOBE from the yield
             // See JRUBY-5434
             arr[i] = block.yield(context, safeArrayRef(values, i + begin));
         }
 
         return new RubyArray(runtime, arr);
     }
 
-    @JRubyMethod(name = {"collect", "map"}, compat = RUBY1_9)
+    @JRubyMethod(name = {"collect"}, compat = RUBY1_9)
     public IRubyObject collect19(ThreadContext context, Block block) {
         return block.isGiven() ? collect(context, block) : enumeratorize(context.getRuntime(), this, "collect");
     }
 
+    @JRubyMethod(name = {"map"}, compat = RUBY1_9)
+    public IRubyObject map19(ThreadContext context, Block block) {
+        return block.isGiven() ? collect(context, block) : enumeratorize(context.getRuntime(), this, "map");
+    }
+
     /** rb_ary_collect_bang
      *
      */
     public RubyArray collectBang(ThreadContext context, Block block) {
         if (!block.isGiven()) throw context.getRuntime().newLocalJumpErrorNoBlock();
         modify();
 
         for (int i = 0, len = realLength; i < len; i++) {
             // Do not coarsen the "safe" check, since it will misinterpret AIOOBE from the yield
             // See JRUBY-5434
             store(i, block.yield(context, safeArrayRef(values, begin + i)));
         }
         
         return this;
     }
 
     /** rb_ary_collect_bang
     *
     */
     @JRubyMethod(name = "collect!")
     public IRubyObject collect_bang(ThreadContext context, Block block) {
         return block.isGiven() ? collectBang(context, block) : enumeratorize(context.getRuntime(), this, "collect!");
     }
 
     /** rb_ary_collect_bang
     *
     */
     @JRubyMethod(name = "map!")
     public IRubyObject map_bang(ThreadContext context, Block block) {
         return block.isGiven() ? collectBang(context, block) : enumeratorize(context.getRuntime(), this, "map!");
     }
 
     /** rb_ary_select
      *
      */
     public IRubyObject selectCommon(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         RubyArray result = new RubyArray(runtime, realLength);
 
         for (int i = 0; i < realLength; i++) {
             // Do not coarsen the "safe" check, since it will misinterpret AIOOBE from the yield
             // See JRUBY-5434
             IRubyObject value = safeArrayRef(values, begin + i);
 
             if (block.yield(context, value).isTrue()) result.append(value);
         }
 
         RuntimeHelpers.fillNil(result.values, result.realLength, result.values.length, runtime);
         return result;
     }
 
     @JRubyMethod
     public IRubyObject select(ThreadContext context, Block block) {
         return block.isGiven() ? selectCommon(context, block) : enumeratorize(context.getRuntime(), this, "select");
     }
 
     @JRubyMethod(name = "select!", compat = RUBY1_9)
     public IRubyObject select_bang(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "select!");
 
         int newLength = 0;
         IRubyObject[] aux = new IRubyObject[values.length];
 
         for (int oldIndex = 0; oldIndex < realLength; oldIndex++) {
             // Do not coarsen the "safe" check, since it will misinterpret 
             // AIOOBE from the yield (see JRUBY-5434)
             IRubyObject value = safeArrayRef(values, begin + oldIndex);
             
             if (!block.yield(context, value).isTrue()) continue;
 
             aux[begin + newLength++] = value;
         }
 
         if (realLength == newLength) return runtime.getNil(); // No change
 
         safeArrayCopy(aux, begin, values, begin, newLength);
         realLength = newLength;
 
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject keep_if(ThreadContext context, Block block) {
         if (!block.isGiven()) {
             return enumeratorize(context.getRuntime(), this, "keep_if");
         }
         select_bang(context, block);
         return this;
     }
 
     /** rb_ary_delete
      *
      */
     @JRubyMethod(required = 1)
     public IRubyObject delete(ThreadContext context, IRubyObject item, Block block) {
         int i2 = 0;
 
         Ruby runtime = context.getRuntime();
         for (int i1 = 0; i1 < realLength; i1++) {
             // Do not coarsen the "safe" check, since it will misinterpret AIOOBE from equalInternal
             // See JRUBY-5434
             IRubyObject e = safeArrayRef(values, begin + i1);
             if (equalInternal(context, e, item)) continue;
             if (i1 != i2) store(i2, e);
             i2++;
         }
 
         if (realLength == i2) {
             if (block.isGiven()) return block.yield(context, item);
 
             return runtime.getNil();
         }
 
         modify();
 
         final int myRealLength = this.realLength;
         final int myBegin = this.begin;
         final IRubyObject[] myValues = this.values;
         try {
             if (myRealLength > i2) {
                 RuntimeHelpers.fillNil(myValues, myBegin + i2, myBegin + myRealLength, context.getRuntime());
                 this.realLength = i2;
                 int valuesLength = myValues.length - myBegin;
                 if (i2 << 1 < valuesLength && valuesLength > ARRAY_DEFAULT_SIZE) realloc(i2 << 1, valuesLength);
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
 
         return item;
     }
 
     /** rb_ary_delete_at
      *
      */
     public final IRubyObject delete_at(int pos) {
         int len = realLength;
         if (pos >= len || (pos < 0 && (pos += len) < 0)) return getRuntime().getNil();
 
         modify();
 
         IRubyObject nil = getRuntime().getNil();
         IRubyObject obj = null; // should never return null below
 
         try {
             obj = values[begin + pos];
             // fast paths for head and tail
             if (pos == 0) {
                 values[begin] = nil;
                 begin++;
                 realLength--;
                 return obj;
             } else if (pos == realLength - 1) {
                 values[begin + realLength - 1] = nil;
                 realLength--;
                 return obj;
             }
 
             System.arraycopy(values, begin + pos + 1, values, begin + pos, len - (pos + 1));
             values[begin + len - 1] = getRuntime().getNil();
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         realLength--;
 
         return obj;
     }
 
     /** rb_ary_delete_at_m
      * 
      */
     @JRubyMethod(name = "delete_at", required = 1)
     public IRubyObject delete_at(IRubyObject obj) {
         return delete_at((int) RubyNumeric.num2long(obj));
     }
 
     /** rb_ary_reject_bang
      * 
      */
     public IRubyObject rejectCommon(ThreadContext context, Block block) {
         RubyArray ary = aryDup();
         ary.reject_bang(context, block);
         return ary;
     }
 
     @JRubyMethod
     public IRubyObject reject(ThreadContext context, Block block) {
         return block.isGiven() ? rejectCommon(context, block) : enumeratorize(context.getRuntime(), this, "reject");
     }
 
     /** rb_ary_reject_bang
      *
      */
     public IRubyObject rejectBang(ThreadContext context, Block block) {
         if (!block.isGiven()) throw context.getRuntime().newLocalJumpErrorNoBlock();
 
         int i2 = 0;
         modify();
         
         for (int i1 = 0; i1 < realLength; i1++) {
             // Do not coarsen the "safe" check, since it will misinterpret AIOOBE from the yield
             // See JRUBY-5434
             IRubyObject v = safeArrayRef(values, begin + i1);
             if (block.yield(context, v).isTrue()) continue;
             if (i1 != i2) store(i2, v);
             i2++;
         }
 
         if (realLength == i2) return context.getRuntime().getNil();
 
         if (i2 < realLength) {
             try {
                 RuntimeHelpers.fillNil(values, begin + i2, begin + realLength, context.getRuntime());
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
             }
             realLength = i2;
         }
 
         return this;
     }
 
     @JRubyMethod(name = "reject!")
     public IRubyObject reject_bang(ThreadContext context, Block block) {
         return block.isGiven() ? rejectBang(context, block) : enumeratorize(context.getRuntime(), this, "reject!");
     }
 
     /** rb_ary_delete_if
      *
      */
     public IRubyObject deleteIf(ThreadContext context, Block block) {
         reject_bang(context, block);
         return this;
     }
 
     @JRubyMethod
     public IRubyObject delete_if(ThreadContext context, Block block) {
         return block.isGiven() ? deleteIf(context, block) : enumeratorize(context.getRuntime(), this, "delete_if");
     }
 
 
     /** rb_ary_zip
      *
      */
     @JRubyMethod(optional = 1, rest = true)
     public IRubyObject zip(ThreadContext context, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         // Array#zip whether 1.8 or 1.9 uses to_ary unlike Enumerable version
         args = RubyEnumerable.zipCommonConvert(runtime, args, "to_ary");
 
         if (block.isGiven()) {
             for (int i = 0; i < realLength; i++) {
                 IRubyObject[] tmp = new IRubyObject[args.length + 1];
                 // Do not coarsen the "safe" check, since it will misinterpret AIOOBE from the yield
                 // See JRUBY-5434
                 tmp[0] = safeArrayRef(values, begin + i);
                 for (int j = 0; j < args.length; j++) {
                     tmp[j + 1] = ((RubyArray) args[j]).elt(i);
                 }
                 block.yield(context, newArrayNoCopyLight(runtime, tmp));
             }
             return runtime.getNil();
         }
 
         IRubyObject[] result = new IRubyObject[realLength];
         try {
             for (int i = 0; i < realLength; i++) {
                 IRubyObject[] tmp = new IRubyObject[args.length + 1];
                 tmp[0] = values[begin + i];
                 for (int j = 0; j < args.length; j++) {
                     tmp[j + 1] = ((RubyArray) args[j]).elt(i);
                 }
                 result[i] = newArrayNoCopyLight(runtime, tmp);
             }
         } catch (ArrayIndexOutOfBoundsException aioob) {
             concurrentModification();
         }
         return newArrayNoCopy(runtime, result);
     }
     
     /** rb_ary_cmp
      *
      */
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         IRubyObject ary2 = runtime.getNil();
         boolean isAnArray = (obj instanceof RubyArray) || obj.getMetaClass().getSuperClass() == runtime.getArray();
 
         if (!isAnArray && !obj.respondsTo("to_ary")) {
             return ary2;
         } else if (!isAnArray) {
             ary2 = obj.callMethod(context, "to_ary");
         } else {
             ary2 = obj.convertToArray();
         }
         
         return cmpCommon(context, runtime, (RubyArray) ary2);
     }
 
     private IRubyObject cmpCommon(ThreadContext context, Ruby runtime, RubyArray ary2) {
         if (this == ary2 || runtime.isInspecting(this)) return RubyFixnum.zero(runtime);
 
         try {
             runtime.registerInspecting(this);
 
             int len = realLength;
             if (len > ary2.realLength) len = ary2.realLength;
 
             for (int i = 0; i < len; i++) {
                 IRubyObject v = invokedynamic(context, elt(i), OP_CMP, ary2.elt(i));
                 if (!(v instanceof RubyFixnum) || ((RubyFixnum) v).getLongValue() != 0) return v;
             }
         } finally {
             runtime.unregisterInspecting(this);
         }
 
         int len = realLength - ary2.realLength;
 
         if (len == 0) return RubyFixnum.zero(runtime);
         if (len > 0) return RubyFixnum.one(runtime);
 
         return RubyFixnum.minus_one(runtime);
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * @deprecated Use the versions with zero, one, or two args.
      */
     public IRubyObject slice_bang(IRubyObject[] args) {
         switch (args.length) {
         case 1:
             return slice_bang(args[0]);
         case 2:
             return slice_bang(args[0], args[1]);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 1, 2);
             return null; // not reached
         }
     }
 
     private IRubyObject slice_internal(long pos, long len, 
             IRubyObject arg0, IRubyObject arg1, Ruby runtime) {
         if(len < 0) return runtime.getNil();
         int orig_len = realLength;
         if(pos < 0) {
             pos += orig_len;
             if(pos < 0) {
                 return runtime.getNil();
             }
         } else if(orig_len < pos) {
             return runtime.getNil();
         }
 
         if(orig_len < pos + len) {
             len = orig_len - pos;
         }
         if(len == 0) {
             return runtime.newEmptyArray();
         }
 
         arg1 = makeShared(begin + (int)pos, (int)len, getMetaClass());
         splice(pos, len, null, false);
 
         return arg1;
     }
 
     /** rb_ary_slice_bang
      *
      */
     @JRubyMethod(name = "slice!")
     public IRubyObject slice_bang(IRubyObject arg0) {
         modifyCheck();
         Ruby runtime = getRuntime();
         if (arg0 instanceof RubyRange) {
             RubyRange range = (RubyRange) arg0;
             if (!range.checkBegin(realLength)) {
                 return runtime.getNil();
             }
 
             long[] beglen = range.begLen(realLength, 1);
             long pos = beglen[0];
             long len = beglen[1];
             return slice_internal(pos, len, arg0, null, runtime);
         }
         return delete_at((int) RubyNumeric.num2long(arg0));
     }
 
     /** rb_ary_slice_bang
     *
     */
     @JRubyMethod(name = "slice!")
     public IRubyObject slice_bang(IRubyObject arg0, IRubyObject arg1) {
         modifyCheck();
         long pos = RubyNumeric.num2long(arg0);
         long len = RubyNumeric.num2long(arg1);
         return slice_internal(pos, len, arg0, arg1, getRuntime());
     }    
 
     /** rb_ary_assoc
      *
      */
     @JRubyMethod(name = "assoc", required = 1)
     public IRubyObject assoc(ThreadContext context, IRubyObject key) {
         Ruby runtime = context.getRuntime();
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = eltOk(i);
             if (v instanceof RubyArray) {
                 RubyArray arr = (RubyArray)v;
                 if (arr.realLength > 0 && equalInternal(context, arr.elt(0), key)) return arr;
             }
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_rassoc
      *
      */
     @JRubyMethod(name = "rassoc", required = 1)
     public IRubyObject rassoc(ThreadContext context, IRubyObject value) {
         Ruby runtime = context.getRuntime();
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = eltOk(i);
             if (v instanceof RubyArray) {
                 RubyArray arr = (RubyArray)v;
                 if (arr.realLength > 1 && equalInternal(context, arr.eltOk(1), value)) return arr;
             }
         }
 
         return runtime.getNil();
     }
 
     private boolean flatten(ThreadContext context, int level, RubyArray result) {
         Ruby runtime = context.getRuntime();
         RubyArray stack = new RubyArray(runtime, ARRAY_DEFAULT_SIZE, false);
         IdentityHashMap<Object, Object> memo = new IdentityHashMap<Object, Object>();
         RubyArray ary = this;
         memo.put(ary, NEVER);
         boolean modified = false;
 
         int i = 0;
 
         try {
             while (true) {
                 IRubyObject tmp;
                 while (i < ary.realLength) {
                     IRubyObject elt = ary.values[ary.begin + i++];
                     tmp = elt.checkArrayType();
                     if (tmp.isNil() || (level >= 0 && stack.realLength / 2 >= level)) {
                         result.append(elt);
                     } else {
                         modified = true;
                         if (memo.get(tmp) != null) throw runtime.newArgumentError("tried to flatten recursive array");
                         memo.put(tmp, NEVER);
                         stack.append(ary);
                         stack.append(RubyFixnum.newFixnum(runtime, i));
                         ary = (RubyArray)tmp;
                         i = 0;
                     }
                 }
                 if (stack.realLength == 0) break;
                 memo.remove(ary);
                 tmp = stack.pop(context);
                 i = (int)((RubyFixnum)tmp).getLongValue();
                 ary = (RubyArray)stack.pop(context);
             }
         } catch (ArrayIndexOutOfBoundsException aioob) {
             concurrentModification();
         }
         return modified;
     }
 
     @JRubyMethod(name = "flatten!", compat = RUBY1_8)
     public IRubyObject flatten_bang(ThreadContext context) {
         Ruby runtime = context.getRuntime();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         if (flatten(context, -1, result)) {
             modifyCheck();
             isShared = false;
             begin = 0;
             realLength = result.realLength;
             values = result.values;
             return this;
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "flatten!", compat = RUBY1_9)
     public IRubyObject flatten_bang19(ThreadContext context) {
         modifyCheck();
 
         return flatten_bang(context);
     }
 
     @JRubyMethod(name = "flatten!", compat = RUBY1_8)
     public IRubyObject flatten_bang(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         int level = RubyNumeric.num2int(arg);
         if (level == 0) return runtime.getNil();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         if (flatten(context, level, result)) {
             isShared = false;
             begin = 0;
             realLength = result.realLength;
             values = result.values;
             return this;
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "flatten!", compat = RUBY1_9)
     public IRubyObject flatten_bang19(ThreadContext context, IRubyObject arg) {
         modifyCheck();
 
         return flatten_bang(context, arg);
     }
 
     @JRubyMethod(name = "flatten", compat = RUBY1_8)
     public IRubyObject flatten(ThreadContext context) {
         Ruby runtime = context.getRuntime();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, -1, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "flatten", compat = RUBY1_8)
     public IRubyObject flatten(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         int level = RubyNumeric.num2int(arg);
         if (level == 0) return this;
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, level, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "flatten", compat = RUBY1_9)
     public IRubyObject flatten19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, -1, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "flatten", compat = RUBY1_9)
     public IRubyObject flatten19(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         int level = RubyNumeric.num2int(arg);
         if (level == 0) return makeShared();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, level, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "count")
     public IRubyObject count(ThreadContext context, Block block) {
         if (block.isGiven()) {
             int n = 0;
             for (int i = 0; i < realLength; i++) {
                 if (block.yield(context, elt(i)).isTrue()) n++;
             }
             return RubyFixnum.newFixnum(context.getRuntime(), n);
         } else {
             return RubyFixnum.newFixnum(context.getRuntime(), realLength);
         }
     }
 
     @JRubyMethod(name = "count")
     public IRubyObject count(ThreadContext context, IRubyObject obj, Block block) {
         if (block.isGiven()) context.getRuntime().getWarnings().warn(ID.BLOCK_UNUSED, "given block not used");
 
         int n = 0;
         for (int i = 0; i < realLength; i++) {
             if (equalInternal(context, elt(i), obj)) n++;
         }
         return RubyFixnum.newFixnum(context.getRuntime(), n);
     }
 
     /** rb_ary_nitems
      *
      */
     @JRubyMethod(name = "nitems")
     public IRubyObject nitems() {
         int n = 0;
 
         for (int i = 0; i < realLength; i++) {
             if (!eltOk(i).isNil()) n++;
         }
         
         return getRuntime().newFixnum(n);
     }
 
     /** rb_ary_plus
      *
      */
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(IRubyObject obj) {
         RubyArray y = obj.convertToArray();
         int len = realLength + y.realLength;
         RubyArray z = new RubyArray(getRuntime(), len);
         try {
             System.arraycopy(values, begin, z.values, 0, realLength);
             System.arraycopy(y.values, y.begin, z.values, realLength, y.realLength);
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         z.realLength = len;
         return z;
     }
 
     /** rb_ary_times
      *
      */
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_8)
     public IRubyObject op_times(ThreadContext context, IRubyObject times) {
         IRubyObject tmp = times.checkStringType();
 
         if (!tmp.isNil()) return join(context, tmp);
 
         long len = RubyNumeric.num2long(times);
         Ruby runtime = context.getRuntime();
         if (len == 0) return new RubyArray(runtime, getMetaClass(), IRubyObject.NULL_ARRAY);
         if (len < 0) throw runtime.newArgumentError("negative argument");
 
         if (Long.MAX_VALUE / len < realLength) {
             throw runtime.newArgumentError("argument too big");
         }
 
         len *= realLength;
 
         checkLength(runtime, len);
         RubyArray ary2 = new RubyArray(runtime, getMetaClass(), (int)len);
         ary2.realLength = ary2.values.length;
 
         try {
             for (int i = 0; i < len; i += realLength) {
                 System.arraycopy(values, begin, ary2.values, i, realLength);
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
 
         ary2.infectBy(this);
 
         return ary2;
     }
 
     /** rb_ary_times
      *
      */
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_9)
     public IRubyObject op_times19(ThreadContext context, IRubyObject times) {
         IRubyObject tmp = times.checkStringType();
 
         if (!tmp.isNil()) return join19(context, tmp);
 
         long len = RubyNumeric.num2long(times);
         Ruby runtime = context.getRuntime();
         if (len == 0) return new RubyArray(runtime, getMetaClass(), IRubyObject.NULL_ARRAY).infectBy(this);
         if (len < 0) throw runtime.newArgumentError("negative argument");
 
         if (Long.MAX_VALUE / len < realLength) {
             throw runtime.newArgumentError("argument too big");
         }
 
         len *= realLength;
 
         checkLength(runtime, len);
         RubyArray ary2 = new RubyArray(runtime, getMetaClass(), (int)len);
         ary2.realLength = ary2.values.length;
 
         try {
             for (int i = 0; i < len; i += realLength) {
                 System.arraycopy(values, begin, ary2.values, i, realLength);
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
 
         ary2.infectBy(this);
 
         return ary2;
     }
 
     /** ary_make_hash
      * 
      */
     private RubyHash makeHash() {
         return makeHash(new RubyHash(getRuntime(), false));
     }
 
     private RubyHash makeHash(RubyHash hash) {
         int myBegin = this.begin;
         for (int i = myBegin; i < myBegin + realLength; i++) {
             hash.fastASet(values[i], NEVER);
         }
         return hash;
     }
 
     private RubyHash makeHash(RubyArray ary2) {
         return ary2.makeHash(makeHash());
     }
 
     private RubyHash makeHash(ThreadContext context, Block block) {
         return makeHash(context, new RubyHash(getRuntime(), false), block);
     }
 
     private RubyHash makeHash(ThreadContext context, RubyHash hash, Block block) {
         int myBegin = this.begin;
         for (int i = myBegin; i < myBegin + realLength; i++) {
             IRubyObject v = elt(i);
             IRubyObject k = block.yield(context, v);
             if (hash.fastARef(k) == null) hash.fastASet(k, v);
         }
         return hash;
     }
 
     /** rb_ary_uniq_bang 
      *
      */
     @JRubyMethod(name = "uniq!", compat = RUBY1_8)
     public IRubyObject uniq_bang(ThreadContext context) {
         RubyHash hash = makeHash();
         if (realLength == hash.size()) return context.getRuntime().getNil();
 
         int j = 0;
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (hash.fastDelete(v)) store(j++, v);
         }
         realLength = j;
         return this;
     }
 
     @JRubyMethod(name = "uniq!", compat = RUBY1_9)
     public IRubyObject uniq_bang19(ThreadContext context, Block block) {
         modifyCheck();
         
         if (!block.isGiven()) return uniq_bang(context);
         RubyHash hash = makeHash(context, block);
         if (realLength == hash.size()) return context.getRuntime().getNil();
         realLength = 0;
 
         hash.visitAll(new RubyHash.Visitor() {
             @Override
             public void visit(IRubyObject key, IRubyObject value) {
                 append(value);
             }
         });
         return this;
     }
 
     /** rb_ary_uniq 
      *
      */
     @JRubyMethod(name = "uniq", compat = RUBY1_8)
     public IRubyObject uniq(ThreadContext context) {
         RubyHash hash = makeHash();
         if (realLength == hash.size()) return makeShared();
 
         RubyArray result = new RubyArray(context.getRuntime(), getMetaClass(), hash.size()); 
 
         int j = 0;
         try {
             for (int i = 0; i < realLength; i++) {
                 IRubyObject v = elt(i);
                 if (hash.fastDelete(v)) result.values[j++] = v;
             }
         } catch (ArrayIndexOutOfBoundsException aioob) {
             concurrentModification();
         }
         result.realLength = j;
         return result;
     }
 
     @JRubyMethod(name = "uniq", compat = RUBY1_9)
     public IRubyObject uniq19(ThreadContext context, Block block) {
         if (!block.isGiven()) return uniq(context);
         RubyHash hash = makeHash(context, block);
 
         final RubyArray result = new RubyArray(context.getRuntime(), getMetaClass(), hash.size());
         hash.visitAll(new RubyHash.Visitor() {
             @Override
             public void visit(IRubyObject key, IRubyObject value) {
                 result.append(value);
             }
         });
         return result;
     }
 
     /** rb_ary_diff
      *
      */
     @JRubyMethod(name = "-", required = 1)
     public IRubyObject op_diff(IRubyObject other) {
         RubyHash hash = other.convertToArray().makeHash();
         RubyArray ary3 = new RubyArray(getRuntime(), ARRAY_DEFAULT_SIZE);
 
         int myBegin = this.begin;
         try {
             for (int i = myBegin; i < myBegin + realLength; i++) {
                 if (hash.fastARef(values[i]) != null) continue;
                 ary3.append(elt(i - myBegin));
             }
         } catch (ArrayIndexOutOfBoundsException aioob) {
             concurrentModification();
         }
         RuntimeHelpers.fillNil(ary3.values, ary3.realLength, ary3.values.length, getRuntime());
 
         return ary3;
     }
 
     /** rb_ary_and
      *
      */
     @JRubyMethod(name = "&", required = 1)
     public IRubyObject op_and(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
         RubyHash hash = ary2.makeHash();
         RubyArray ary3 = new RubyArray(getRuntime(), realLength < ary2.realLength ? realLength : ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (hash.fastDelete(v)) ary3.append(v);
         }
 
         RuntimeHelpers.fillNil(ary3.values, ary3.realLength, ary3.values.length, getRuntime());
 
         return ary3;
     }
 
     /** rb_ary_or
      *
      */
     @JRubyMethod(name = "|", required = 1)
     public IRubyObject op_or(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
         RubyHash set = makeHash(ary2);
 
         RubyArray ary3 = new RubyArray(getRuntime(), realLength + ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (set.fastDelete(v)) ary3.append(v);
         }
         for (int i = 0; i < ary2.realLength; i++) {
             IRubyObject v = ary2.elt(i);
             if (set.fastDelete(v)) ary3.append(v);
         }
 
         RuntimeHelpers.fillNil(ary3.values, ary3.realLength, ary3.values.length, getRuntime());
 
         return ary3;
     }
 
     /** rb_ary_sort
      *
      */
     @JRubyMethod(compat = RUBY1_8)
     public RubyArray sort(ThreadContext context, Block block) {
         RubyArray ary = aryDup();
         ary.sort_bang(context, block);
         return ary;
     }
 
     @JRubyMethod(name = "sort", compat = RUBY1_9)
     public RubyArray sort19(ThreadContext context, Block block) {
         RubyArray ary = aryDup19();
         ary.sort_bang19(context, block);
         return ary;
     }
 
     /** rb_ary_sort_bang
      *
      */
     @JRubyMethod(name = "sort!", compat = RUBY1_8)
     public IRubyObject sort_bang(ThreadContext context, Block block) {
         modify();
         if (realLength > 1) {
             flags |= TMPLOCK_ARR_F;
 
             try {
                 return block.isGiven() ? sortInternal(context, block): sortInternal(context, false);
             } finally {
                 flags &= ~TMPLOCK_ARR_F;
             }
         }
         return this;
     }
 
     @JRubyMethod(name = "sort!", compat = RUBY1_9)
     public IRubyObject sort_bang19(ThreadContext context, Block block) {
         modify();
         if (realLength > 1) {
             return block.isGiven() ? sortInternal(context, block) : sortInternal(context, true);
         }
         return this;
     }
 
     private IRubyObject sortInternal(final ThreadContext context, boolean honorOverride) {
         // One check per specialized fast-path to make the check invariant.
         final boolean fixnumBypass = !honorOverride || context.getRuntime().newFixnum(0).isBuiltin("<=>");
         final boolean stringBypass = !honorOverride || context.getRuntime().newString("").isBuiltin("<=>");
 
         try {
             Qsort.sort(values, begin, begin + realLength, new Comparator() {
                 public int compare(Object o1, Object o2) {
                     if (fixnumBypass && o1 instanceof RubyFixnum && o2 instanceof RubyFixnum) {
                         return compareFixnums((RubyFixnum) o1, (RubyFixnum) o2);
                     }
                     if (stringBypass && o1 instanceof RubyString && o2 instanceof RubyString) {
                         return ((RubyString) o1).op_cmp((RubyString) o2);
                     }
                     return compareOthers(context, (IRubyObject)o1, (IRubyObject)o2);
                 }
             });
         } catch (ArrayIndexOutOfBoundsException aioob) {
             concurrentModification();
         }
         return this;
     }
 
     private static int compareFixnums(RubyFixnum o1, RubyFixnum o2) {
         long a = o1.getLongValue();
         long b = o2.getLongValue();
         return a > b ? 1 : a == b ? 0 : -1;
     }
 
     private static int compareOthers(ThreadContext context, IRubyObject o1, IRubyObject o2) {
         IRubyObject ret = invokedynamic(context, o1, OP_CMP, o2);
         int n = RubyComparable.cmpint(context, ret, o1, o2);
         //TODO: ary_sort_check should be done here
         return n;
     }
 
     private IRubyObject sortInternal(final ThreadContext context, final Block block) {
         IRubyObject[] newValues = new IRubyObject[realLength];
         int length = realLength;
 
         safeArrayCopy(values, begin, newValues, 0, length);
         Qsort.sort(newValues, 0, length, new Comparator() {
             public int compare(Object o1, Object o2) {
                 IRubyObject obj1 = (IRubyObject) o1;
                 IRubyObject obj2 = (IRubyObject) o2;
                 IRubyObject ret = block.yieldArray(context, getRuntime().newArray(obj1, obj2), null, null);
                 //TODO: ary_sort_check should be done here
                 return RubyComparable.cmpint(context, ret, obj1, obj2);
             }
         });
         
         values = newValues;
         begin = 0;
         realLength = length;
         return this;
     }
 
     /** rb_ary_sort_by_bang
      * 
      */
     @JRubyMethod(name = "sort_by!", compat = RUBY1_9)
     public IRubyObject sort_by_bang(ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "sort_by!");
 
         modifyCheck();
         RubyArray sorted = RuntimeHelpers.invoke(context, this, "sort_by", block).convertToArray();
         values = sorted.values;
         isShared = false;
         begin = 0;
         return this;
     }
 
     /** rb_ary_take
      * 
      */
     @JRubyMethod(name = "take", compat = RUBY1_9)
     public IRubyObject take(ThreadContext context, IRubyObject n) {
         Ruby runtime = context.getRuntime();
         long len = RubyNumeric.num2long(n);
         if (len < 0) throw runtime.newArgumentError("attempt to take negative size");
 
         return subseq(0, len);
     }
 
     /** rb_ary_take_while
      * 
      */
