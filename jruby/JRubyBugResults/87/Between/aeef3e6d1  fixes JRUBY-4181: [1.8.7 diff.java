diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index a847ad1ce6..0ed2f975d5 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1491,2050 +1491,2056 @@ public class RubyArray extends RubyObject implements List {
     @JRubyMethod(name = "concat", required = 1, compat = CompatVersion.RUBY1_8)
     public RubyArray concat(IRubyObject obj) {
         RubyArray ary = obj.convertToArray();
         
         if (ary.realLength > 0) splice(realLength, 0, ary);
 
         return this;
     }
 
     @JRubyMethod(name = "concat", required = 1, compat = CompatVersion.RUBY1_9)
     public RubyArray concat19(IRubyObject obj) {
         modifyCheck();
 
         return concat(obj);
     }
 
     /** inspect_ary
      * 
      */
     private IRubyObject inspectAry(ThreadContext context) {
         ByteList buffer = new ByteList();
         buffer.append('[');
         boolean tainted = isTaint();
 
         for (int i = 0; i < realLength; i++) {
             if (i > 0) buffer.append(',').append(' ');
 
             RubyString str = inspect(context, values[begin + i]);
             if (str.isTaint()) tainted = true;
             buffer.append(str.getByteList());
         }
         buffer.append(']');
 
         RubyString str = getRuntime().newString(buffer);
         if (tainted) str.setTaint(true);
 
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
             block.yield(context, values[begin + i]);
         }
         return this;
     }
 
     @JRubyMethod(name = "each", frame = true)
     public IRubyObject each(ThreadContext context, Block block) {
         return block.isGiven() ? eachCommon(context, block) : enumeratorize(context.getRuntime(), this, "each");
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
     
     @JRubyMethod(name = "each_index", frame = true)
     public IRubyObject each_index(ThreadContext context, Block block) {
         return block.isGiven() ? eachIndex(context, block) : enumeratorize(context.getRuntime(), this, "each_index");
     }
 
     /** rb_ary_reverse_each
      *
      */
     public IRubyObject reverseEach(ThreadContext context, Block block) {
         int len = realLength;
 
         while(len-- > 0) {
             block.yield(context, values[begin + len]);
             if (realLength < len) len = realLength;
         }
 
         return this;
     }
 
     @JRubyMethod(name = "reverse_each", frame = true)
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
     @JRubyMethod(name = "join")
     public IRubyObject join(ThreadContext context, IRubyObject sep) {
         final Ruby runtime = context.getRuntime();
         if (realLength == 0) return RubyString.newEmptyString(runtime);
 
         boolean taint = isTaint() || sep.isTaint();
         boolean untrusted = isUntrusted() || sep.isUntrusted();
 
         int len = 1;
         for (int i = begin; i < begin + realLength; i++) {            
             IRubyObject value;
             try {
                 value = values[i];
                 
                 if (runtime.is1_9() && equalInternal(context, this, value)) {
                     throw runtime.newArgumentError("recursive array join");
                 }
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
                 return RubyString.newEmptyString(runtime);
             }
             IRubyObject tmp = value.checkStringType();
             len += tmp.isNil() ? 10 : ((RubyString) tmp).getByteList().length();
         }
 
         ByteList sepBytes = null;
         if (!sep.isNil()) {
             sepBytes = sep.convertToString().getByteList();
             len += sepBytes.realSize * (realLength - 1);
         }
 
         ByteList buf = new ByteList(len);
         for (int i = 0; i < realLength; i++) {
             IRubyObject tmp;
             try {
                 tmp = values[begin + i];
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
                 return RubyString.newEmptyString(runtime);
             }
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
 
     @JRubyMethod(name = "join")
     public IRubyObject join(ThreadContext context) {
         return join(context, context.getRuntime().getGlobalVariables().get("$,"));
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
         if (!(obj instanceof RubyArray)) {
             if (!obj.respondsTo("to_ary")) {
                 return context.getRuntime().getFalse();
             }
             return RuntimeHelpers.rbEqual(context, obj, this);
         }
         return RecursiveComparator.compare(context, "==", this, obj, null);
     }
 
     public RubyBoolean compare(ThreadContext context, String method, IRubyObject other, Set<RecursiveComparator.Pair> seen)
     {
         Ruby runtime = context.getRuntime();
 
         if (!(other instanceof RubyArray)) {
             return runtime.getFalse();
         }
 
         RubyArray ary = (RubyArray) other;
 
         if (realLength != ary.realLength) {
             return runtime.getFalse();
         }
 
         for (int i = 0; i < realLength; i++) {
             if (!RecursiveComparator.compare(context, method, elt(i), ary.elt(i), seen).isTrue()) {
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
         return RecursiveComparator.compare(context, "eql?", this, obj, null);
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
 
         while (t < end) {
             if (values[t].isNil()) {
                 t++;
             } else {
                 values[p++] = values[t++];
             }
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
     @JRubyMethod(name = "compact")
     public IRubyObject compact() {
         RubyArray ary = aryDup();
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
 
     @JRubyMethod(name = "fill", frame = true)
     public IRubyObject fill(ThreadContext context, Block block) {
         if (block.isGiven()) return fillCommon(context, 0, realLength, block);
         throw context.getRuntime().newArgumentError(0, 1);
     }
 
     @JRubyMethod(name = "fill", frame = true)
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
 
     @JRubyMethod(name = "fill", frame = true)
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
 
     @JRubyMethod(name = "fill", frame = true)
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
             try {
                 values[begin + i] = v;
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
             }
         }
         return this;
     }
 
 
     /** rb_ary_index
      *
      */
     public IRubyObject index(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         for (int i = begin; i < begin + realLength; i++) {
             if (equalInternal(context, values[i], obj)) return runtime.newFixnum(i - begin);            
         }
 
         return runtime.getNil();
     }
 
     @JRubyMethod(name = {"index", "find_index"}, frame = true)
     public IRubyObject index(ThreadContext context, IRubyObject obj, Block unused) {
         if (unused.isGiven()) context.getRuntime().getWarnings().warn(ID.BLOCK_UNUSED, "given block not used");
         return index(context, obj); 
     }
 
     @JRubyMethod(name = {"index", "find_index"}, frame = true)
     public IRubyObject index(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "index");
 
         for (int i = begin; i < begin + realLength; i++) {
             if (block.yield(context, values[i]).isTrue()) return runtime.newFixnum(i - begin);
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
             if (equalInternal(context, values[begin + i], obj)) return runtime.newFixnum(i);
         }
 
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "rindex", frame = true)
     public IRubyObject rindex(ThreadContext context, IRubyObject obj, Block unused) {
         if (unused.isGiven()) context.getRuntime().getWarnings().warn(ID.BLOCK_UNUSED, "given block not used");
         return rindex(context, obj); 
     }
 
     @JRubyMethod(name = "rindex", frame = true)
     public IRubyObject rindex(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "rindex");
 
         int i = realLength;
 
         while (i-- > 0) {
             if (i > realLength) {
                 i = realLength;
                 continue;
             }
             if (block.yield(context, values[begin + i]).isTrue()) return runtime.newFixnum(i);
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_indexes
      * 
      */
     @JRubyMethod(name = {"indexes", "indices"}, required = 1, rest = true)
     public IRubyObject indexes(IRubyObject[] args) {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Array#indexes is deprecated; use Array#values_at", "Array#indexes", "Array#values_at");
 
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
         modify();
         final RubyArray dup; 
         if (realLength > 1) {
             dup = safeReverse();
         } else {
             dup = new RubyArray(getRuntime(), getMetaClass(), this);
         }
         dup.flags |= flags & TAINTED_F; // from DUP_SETUP
         // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
         return dup;
     }
 
     private RubyArray safeReverse() {
         int length = realLength;
         int begin = this.begin;
         IRubyObject[]values = this.values;
         IRubyObject[]vals = new IRubyObject[length];
 
         try {
             for (int i = 0; i <= length >> 1; i++) {
                 vals[i] = values[begin + length - i - 1];
                 vals[length - i - 1] = values[begin + i];
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         return new RubyArray(getRuntime(), getMetaClass(), vals);
     }
 
     /** rb_ary_collect
      *
      */
     @JRubyMethod(name = "collect", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject collect(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return new RubyArray(runtime, runtime.getArray(), this);
 
         RubyArray collect = new RubyArray(runtime, realLength);
 
         for (int i = begin; i < begin + realLength; i++) {
             collect.append(block.yield(context, values[i]));
         }
         
         return collect;
     }
 
     @JRubyMethod(name = "collect", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject collect19(ThreadContext context, Block block) {
         return block.isGiven() ? collect(context, block) : enumeratorize(context.getRuntime(), this, "collect");
     }
 
     /** rb_ary_collect_bang
      *
      */
     public RubyArray collectBang(ThreadContext context, Block block) {
         if (!block.isGiven()) throw context.getRuntime().newLocalJumpErrorNoBlock();
         modify();
         for (int i = 0, len = realLength; i < len; i++) {
             store(i, block.yield(context, values[begin + i]));
         }
         return this;
     }
 
     /** rb_ary_collect_bang
     *
     */
     @JRubyMethod(name = "collect!", frame = true)
     public IRubyObject collect_bang(ThreadContext context, Block block) {
         return block.isGiven() ? collectBang(context, block) : enumeratorize(context.getRuntime(), this, "collect!");
     }
 
     /** rb_ary_collect_bang
     *
     */
     @JRubyMethod(name = "map!", frame = true)
     public IRubyObject map_bang(ThreadContext context, Block block) {
         return block.isGiven() ? collectBang(context, block) : enumeratorize(context.getRuntime(), this, "map!");
     }
 
     /** rb_ary_select
      *
      */
     @JRubyMethod(name = "select", frame = true, compat = CompatVersion.RUBY1_8)
     public RubyArray select(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         RubyArray result = new RubyArray(runtime, realLength);
 
         for (int i = 0; i < realLength; i++) {
             if (block.yield(context, values[begin + i]).isTrue()) result.append(elt(i));
         }
 
         RuntimeHelpers.fillNil(result.values, result.realLength, result.values.length, runtime);
         return result;
     }
 
     @JRubyMethod(name = "select", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject select19(ThreadContext context, Block block) {
         return block.isGiven() ? select(context, block) : enumeratorize(context.getRuntime(), this, "select");
     }
 
     /** rb_ary_delete
      *
      */
     @JRubyMethod(name = "delete", required = 1, frame = true)
     public IRubyObject delete(ThreadContext context, IRubyObject item, Block block) {
         int i2 = 0;
 
         Ruby runtime = context.getRuntime();
         for (int i1 = 0; i1 < realLength; i1++) {
             IRubyObject e = values[begin + i1];
             if (equalInternal(context, e, item)) continue;
             if (i1 != i2) store(i2, e);
             i2++;
         }
 
         if (realLength == i2) {
             if (block.isGiven()) return block.yield(context, item);
 
             return runtime.getNil();
         }
 
         modify();
 
         final int realLength = this.realLength;
         final int begin = this.begin;
         final IRubyObject[] values = this.values;
         if (realLength > i2) {
             try {
                 RuntimeHelpers.fillNil(values, begin + i2, begin + realLength, context.getRuntime());
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
             }
             this.realLength = i2;
             int valuesLength = values.length - begin;
             if (i2 << 1 < valuesLength && valuesLength > ARRAY_DEFAULT_SIZE) realloc(i2 << 1, valuesLength);
         }
 
         return item;
     }
 
     /** rb_ary_delete_at
      *
      */
     private final IRubyObject delete_at(int pos) {
         int len = realLength;
         if (pos >= len || (pos < 0 && (pos += len) < 0)) return getRuntime().getNil();
 
         modify();
 
         IRubyObject obj = values[begin + pos];
         try {
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
 
     @JRubyMethod(name = "reject", frame = true)
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
             IRubyObject v = values[begin + i1];
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
 
     @JRubyMethod(name = "reject!", frame = true)
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
 
     @JRubyMethod(name = "delete_if", frame = true)
     public IRubyObject delete_if(ThreadContext context, Block block) {
         return block.isGiven() ? deleteIf(context, block) : enumeratorize(context.getRuntime(), this, "delete_if");
     }
 
 
     /** rb_ary_zip
      * 
      */
     @JRubyMethod(name = "zip", optional = 1, rest = true, frame = true)
     public IRubyObject zip(ThreadContext context, IRubyObject[] args, Block block) {
         for (int i = 0; i < args.length; i++) {
             args[i] = args[i].convertToArray();
         }
 
         Ruby runtime = context.getRuntime();
         if (block.isGiven()) {
             for (int i = 0; i < realLength; i++) {
                 RubyArray tmp = new RubyArray(runtime, args.length + 1);
                 tmp.append(elt(i));
                 for (int j = 0; j < args.length; j++) {
                     tmp.append(((RubyArray) args[j]).elt(i));
                 }
                 block.yield(context, tmp);
             }
             return runtime.getNil();
         }
         
         int len = realLength;
         RubyArray result = new RubyArray(runtime, len);
         for (int i = 0; i < len; i++) {
             RubyArray tmp = new RubyArray(runtime, args.length + 1);
             tmp.append(elt(i));
             for (int j = 0; j < args.length; j++) {
                 tmp.append(((RubyArray) args[j]).elt(i));
             }
             result.append(tmp);
         }
         return result;
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
                 IRubyObject v = elt(i).callMethod(context, "<=>", ary2.elt(i));
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
 
-    private IRubyObject slice_internal(long pos, long len, IRubyObject arg0, IRubyObject arg1) {
-        Ruby runtime = getRuntime();
-
+    private IRubyObject slice_internal(long pos, long len, 
+            IRubyObject arg0, IRubyObject arg1, Ruby runtime) {
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
+
         if(orig_len < pos + len) {
             len = orig_len - pos;
         }
         if(len == 0) {
             return runtime.newEmptyArray();
         }
 
         arg1 = makeShared(begin + (int)pos, (int)len, getMetaClass());
         splice(pos, len, null);
 
         return arg1;
     }
 
     /** rb_ary_slice_bang
      *
      */
     @JRubyMethod(name = "slice!")
     public IRubyObject slice_bang(IRubyObject arg0) {
         modifyCheck();
+        Ruby runtime = getRuntime();
         if (arg0 instanceof RubyRange) {
-            long[] beglen = ((RubyRange) arg0).begLen(realLength, 1);
+            RubyRange range = (RubyRange) arg0;
+            if (!range.checkBegin(realLength)) {
+                return runtime.getNil();
+            }
+
+            long[] beglen = range.begLen(realLength, 1);
             long pos = beglen[0];
             long len = beglen[1];
-            return slice_internal(pos, len, arg0, null);
+            return slice_internal(pos, len, arg0, null, runtime);
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
-        return slice_internal(pos, len, arg0, arg1);
+        return slice_internal(pos, len, arg0, arg1, getRuntime());
     }    
 
     /** rb_ary_assoc
      *
      */
     @JRubyMethod(name = "assoc", required = 1)
     public IRubyObject assoc(ThreadContext context, IRubyObject key) {
         Ruby runtime = context.getRuntime();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray) {
                 RubyArray arr = (RubyArray)v;
                 if (arr.realLength > 0 && equalInternal(context, arr.values[arr.begin], key)) return arr;
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
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray) {
                 RubyArray arr = (RubyArray)v;
                 if (arr.realLength > 1 && equalInternal(context, arr.values[arr.begin + 1], value)) return arr;
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
         return modified;
     }
 
     @JRubyMethod(name = "flatten!", compat = CompatVersion.RUBY1_8)
     public IRubyObject flatten_bang(ThreadContext context) {
         Ruby runtime = context.getRuntime();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         if (flatten(context, -1, result)) {
             modify();
             begin = 0;
             realLength = result.realLength;
             values = result.values;
             return this;
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "flatten!", compat = CompatVersion.RUBY1_9)
     public IRubyObject flatten_bang19(ThreadContext context) {
         modifyCheck();
 
         return flatten_bang(context);
     }
 
     @JRubyMethod(name = "flatten!", compat = CompatVersion.RUBY1_8)
     public IRubyObject flatten_bang(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         int level = RubyNumeric.num2int(arg);
         if (level == 0) return runtime.getNil();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         if (flatten(context, level, result)) {
             begin = 0;
             realLength = result.realLength;
             values = result.values;
             return this;
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "flatten!", compat = CompatVersion.RUBY1_9)
     public IRubyObject flatten_bang19(ThreadContext context, IRubyObject arg) {
         modifyCheck();
 
         return flatten_bang(context, arg);
     }
 
     @JRubyMethod(name = "flatten")
     public IRubyObject flatten(ThreadContext context) {
         Ruby runtime = context.getRuntime();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, -1, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "flatten")
     public IRubyObject flatten(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         int level = RubyNumeric.num2int(arg);
         if (level == 0) return this;
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, level, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "count", compat = CompatVersion.RUBY1_9)
     public IRubyObject count(ThreadContext context, Block block) {
         if (block.isGiven()) {
             int n = 0;
             for (int i = 0; i < realLength; i++) {
                 if (block.yield(context, values[begin + i]).isTrue()) n++;
             }
             return RubyFixnum.newFixnum(context.getRuntime(), n);
         } else {
             return RubyFixnum.newFixnum(context.getRuntime(), realLength);
         }
     }
 
     @JRubyMethod(name = "count", compat = CompatVersion.RUBY1_9)
     public IRubyObject count(ThreadContext context, IRubyObject obj, Block block) {
         if (block.isGiven()) context.getRuntime().getWarnings().warn(ID.BLOCK_UNUSED, "given block not used");
 
         int n = 0;
         for (int i = 0; i < realLength; i++) {
             if (equalInternal(context, values[begin + i], obj)) n++;
         }
         return RubyFixnum.newFixnum(context.getRuntime(), n);
     }
 
     /** rb_ary_nitems
      *
      */
     @JRubyMethod(name = "nitems")
     public IRubyObject nitems() {
         int n = 0;
 
         for (int i = begin; i < begin + realLength; i++) {
             if (!values[i].isNil()) n++;
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
     @JRubyMethod(name = "*", required = 1)
     public IRubyObject op_times(ThreadContext context, IRubyObject times) {
         IRubyObject tmp = times.checkStringType();
 
         if (!tmp.isNil()) return join(context, tmp);
 
         long len = RubyNumeric.num2long(times);
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), IRubyObject.NULL_ARRAY).infectBy(this);
         if (len < 0) throw getRuntime().newArgumentError("negative argument");
 
         if (Long.MAX_VALUE / len < realLength) {
             throw getRuntime().newArgumentError("argument too big");
         }
 
         len *= realLength;
 
         RubyArray ary2 = new RubyArray(getRuntime(), getMetaClass(), len);
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
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             hash.fastASet(values[i], NEVER);
         }
         return hash;
     }
 
     private RubyHash makeHash(RubyArray ary2) {
         return ary2.makeHash(makeHash());
     }
 
     /** ary_make_hash_by
      * 
      */
     private RubyHash makeHash(ThreadContext context, RubyArray ary2, Block block) {
         return ary2.makeHash(makeHash(context, block));
     }
 
     private RubyHash makeHash(ThreadContext context, Block block) {
         return makeHash(context, new RubyHash(getRuntime(), false), block);
     }
 
     private RubyHash makeHash(ThreadContext context, RubyHash hash, Block block) {
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = elt(i);
             IRubyObject k = block.yield(context, v);
             if (hash.fastARef(k) == null) hash.fastASet(k, v);
         }
         return hash;
     }
 
     /** rb_ary_uniq_bang 
      *
      */
     @JRubyMethod(name = "uniq!", compat = CompatVersion.RUBY1_8)
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
 
     @JRubyMethod(name = "uniq!", compat = CompatVersion.RUBY1_9)
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
     @JRubyMethod(name = "uniq", compat = CompatVersion.RUBY1_8)
     public IRubyObject uniq(ThreadContext context) {
         RubyHash hash = makeHash();
         if (realLength == hash.size()) return makeShared();
 
         RubyArray result = new RubyArray(context.getRuntime(), getMetaClass(), hash.size()); 
 
         int j = 0;
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (hash.fastDelete(v)) result.values[j++] = v;
         }
         result.realLength = j;
         return result;
     }
 
     @JRubyMethod(name = "uniq", compat = CompatVersion.RUBY1_9)
     public IRubyObject uniq19(ThreadContext context, Block block) {
         if (!block.isGiven()) return uniq(context);
         RubyHash hash = makeHash(context, block);
         if (realLength == hash.size()) return context.getRuntime().getNil();
 
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
 
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             if (hash.fastARef(values[i]) != null) continue;
             ary3.append(elt(i - begin));
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
     @JRubyMethod(name = "sort", frame = true)
     public RubyArray sort(ThreadContext context, Block block) {
         RubyArray ary = aryDup();
         ary.sort_bang(context, block);
         return ary;
     }
 
     /** rb_ary_sort_bang
      *
      */
     @JRubyMethod(name = "sort!", frame = true)
     public IRubyObject sort_bang(ThreadContext context, Block block) {
         modify();
         if (realLength > 1) {
             if (!context.getRuntime().is1_9()) flags |= TMPLOCK_ARR_F;
 
             try {
                 return block.isGiven() ? sortInternal(context, block): sortInternal(context);
             } finally {
                 flags &= ~TMPLOCK_ARR_F;
             }
         }
         return this;
     }
 
     private IRubyObject sortInternal(final ThreadContext context) {
         Qsort.sort(values, begin, begin + realLength, new Comparator() {
             public int compare(Object o1, Object o2) {
                 if (o1 instanceof RubyFixnum && o2 instanceof RubyFixnum) {
                     return compareFixnums((RubyFixnum)o1, (RubyFixnum)o2);
                 }
                 if (o1 instanceof RubyString && o2 instanceof RubyString) {
                     return ((RubyString) o1).op_cmp((RubyString) o2);
                 }
                 return compareOthers(context, (IRubyObject)o1, (IRubyObject)o2);
             }
         });
         return this;
     }
 
     private static int compareFixnums(RubyFixnum o1, RubyFixnum o2) {
         long a = o1.getLongValue();
         long b = o2.getLongValue();
         return a > b ? 1 : a == b ? 0 : -1;
     }
 
     private static int compareOthers(ThreadContext context, IRubyObject o1, IRubyObject o2) {
         IRubyObject ret = o1.callMethod(context, "<=>", o2);
         int n = RubyComparable.cmpint(context, ret, o1, o2);
         //TODO: ary_sort_check should be done here
         return n;
     }
 
     private IRubyObject sortInternal(final ThreadContext context, final Block block) {
         Qsort.sort(values, begin, begin + realLength, new Comparator() {
             public int compare(Object o1, Object o2) {
                 IRubyObject obj1 = (IRubyObject) o1;
                 IRubyObject obj2 = (IRubyObject) o2;
                 IRubyObject ret = block.yield(context, getRuntime().newArray(obj1, obj2), null, null, true);
                 //TODO: ary_sort_check should be done here
                 return RubyComparable.cmpint(context, ret, obj1, obj2);
             }
         });
         return this;
     }
 
     /** rb_ary_sort_by_bang
      * 
      */
     @JRubyMethod(name = "sort_by!", compat = CompatVersion.RUBY1_9, frame = true)
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
     @JRubyMethod(name = "take", compat = CompatVersion.RUBY1_9)
     public IRubyObject take(ThreadContext context, IRubyObject n) {
         Ruby runtime = context.getRuntime();
         long len = RubyNumeric.num2long(n);
         if (len < 0) throw runtime.newArgumentError("attempt to take negative size");
 
         return subseq(0, len);
     }
 
     /** rb_ary_take_while
      * 
      */
     @JRubyMethod(name = "take_while", compat = CompatVersion.RUBY1_9)
     public IRubyObject take_while(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "take_while");
 
         int i = begin;
         for (; i < begin + realLength; i++) {
             if (!block.yield(context, values[i]).isTrue()) break; 
         }
         return subseq(0, i - begin);
     }
 
     /** rb_ary_take
      * 
      */
     @JRubyMethod(name = "drop", compat = CompatVersion.RUBY1_9)
     public IRubyObject drop(ThreadContext context, IRubyObject n) {
         Ruby runtime = context.getRuntime();
         long pos = RubyNumeric.num2long(n);
         if (pos < 0) throw runtime.newArgumentError("attempt to drop negative size");
 
         IRubyObject result = subseq(pos, realLength);
         return result.isNil() ? runtime.newEmptyArray() : result;
     }
 
     /** rb_ary_take_while
      * 
      */
     @JRubyMethod(name = "drop_while", compat = CompatVersion.RUBY1_9)
     public IRubyObject drop_while(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "drop_while");
 
         int i= begin;
         for (; i < begin + realLength; i++) {
             if (!block.yield(context, values[i]).isTrue()) break;
         }
         IRubyObject result = subseq(i - begin, realLength);
         return result.isNil() ? runtime.newEmptyArray() : result;
     }
 
     /** rb_ary_cycle
      * 
      */
     @JRubyMethod(name = "cycle")
     public IRubyObject cycle(ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "cycle");
         return cycleCommon(context, -1, block);
     }
 
     /** rb_ary_cycle
      * 
      */
     @JRubyMethod(name = "cycle")
     public IRubyObject cycle(ThreadContext context, IRubyObject arg, Block block) {
         if (arg.isNil()) return cycle(context, block);
         if (!block.isGiven()) return enumeratorize(context.getRuntime(), this, "cycle", arg);
 
         long times = RubyNumeric.num2long(arg);
         if (times <= 0) return context.getRuntime().getNil();
 
         return cycleCommon(context, times, block);
     }
 
     private IRubyObject cycleCommon(ThreadContext context, long n, Block block) {
         while (realLength > 0 && (n < 0 || 0 < n--)) {
             for (int i=begin; i < begin + realLength; i++) {
                 block.yield(context, values[i]);
             }
         }
         return context.getRuntime().getNil();
     }
 
     /** rb_ary_product
      * 
      */
     @JRubyMethod(name = "product", rest = true)
     public IRubyObject product(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         int n = args.length + 1;
         RubyArray arrays[] = new RubyArray[n];
         int counters[] = new int[n];
 
         arrays[0] = this;
         for (int i = 1; i < n; i++) arrays[i] = args[i - 1].convertToArray();
 
         int resultLen = 1;
         for (int i = 0; i < n; i++) {
             int k = arrays[i].realLength;
             int l = resultLen;
             if (k == 0) return newEmptyArray(runtime);
             resultLen *= k;
             if (resultLen < k || resultLen < l || resultLen / k != l) {
                 throw runtime.newRangeError("too big to product");
             }
         }
 
         RubyArray result = newArray(runtime, resultLen);
 
         for (int i = 0; i < resultLen; i++) {
             RubyArray sub = newArray(runtime, n);
             for (int j = 0; j < n; j++) sub.append(arrays[j].entry(counters[j]));
 
             result.append(sub);
             int m = n - 1;
             counters[m]++;
 
             while (m > 0 && counters[m] == arrays[m].realLength) {
                 counters[m] = 0;
                 m--;
                 counters[m]++;
             }
         }
         return result;
     }
 
     private int combinationLength(ThreadContext context, int n, int k) {
         if (k * 2 > n) k = n - k;
         if (k == 0) return 1;
         if (k < 0) return 0;
         int val = 1;
         for (int i = 1; i <= k; i++, n--) {
             long m = val;
             val *= n;
             if (val < m) throw context.getRuntime().newRangeError("too big for combination");
             val /= i;
         }
         return val;
     }
 
     /** rb_ary_combination
      * 
      */
     @JRubyMethod(name = "combination")
     public IRubyObject combination(ThreadContext context, IRubyObject num, Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "combination", num);
 
         int n = RubyNumeric.num2int(num);
 
         if (n == 0) {
             block.yield(context, newEmptyArray(runtime));
         } else if (n == 1) {
             for (int i = 0; i < realLength; i++) {
                 block.yield(context, newArray(runtime, values[begin + i]));
             }
         } else if (n >= 0 && realLength >= n) {
             int stack[] = new int[n + 1];
             int nlen = combinationLength(context, (int)realLength, n);
             IRubyObject chosen[] = new IRubyObject[n];
             int lev = 0;
 
             stack[0] = -1;
             for (int i = 0; i < nlen; i++) {
                 chosen[lev] = values[begin + stack[lev + 1]];
                 for (lev++; lev < n; lev++) {
                     chosen[lev] = values[begin + (stack[lev + 1] = stack[lev] + 1)];
                 }
                 block.yield(context, newArray(runtime, chosen));
                 do {
                     stack[lev--]++;
                 } while (lev != 0 && stack[lev + 1] + n == realLength + lev + 1);
             }
         }
         return this;
     }
     
     private void permute(ThreadContext context, int n, int r, int[]p, int index, boolean[]used, RubyArray values, Block block) {
         for (int i = 0; i < n; i++) {
             if (!used[i]) {
                 p[index] = i;
                 if (index < r - 1) {
                     used[i] = true;
                     permute(context, n, r, p, index + 1, used, values, block);
                     used[i] = false;
                 } else {
                     RubyArray result = newArray(context.getRuntime(), r);
 
                     for (int j = 0; j < r; j++) {
                         result.values[result.begin + j] = values.values[values.begin + p[j]];
                     }
 
                     result.realLength = r;
                     block.yield(context, result);
                 }
             }
         }
     }
 
     /** rb_ary_permutation
      * 
      */
     @JRubyMethod(name = "permutation")
     public IRubyObject permutation(ThreadContext context, IRubyObject num, Block block) {
         return block.isGiven() ? permutationCommon(context, RubyNumeric.num2int(num), block) : enumeratorize(context.getRuntime(), this, "permutation", num);
     }
 
     @JRubyMethod(name = "permutation")
     public IRubyObject permutation(ThreadContext context, Block block) {
         return block.isGiven() ? permutationCommon(context, realLength, block) : enumeratorize(context.getRuntime(), this, "permutation");
     }
 
     private IRubyObject permutationCommon(ThreadContext context, int r, Block block) {
         if (r == 0) {
             block.yield(context, newEmptyArray(context.getRuntime()));
         } else if (r == 1) {
             for (int i = 0; i < realLength; i++) {
                 block.yield(context, newArray(context.getRuntime(), values[begin + i]));
             }
         } else if (r >= 0 && realLength >= r) {
             int n = realLength;
             permute(context, n, r,
                     new int[n], 0,
                     new boolean[n],
                     makeShared(begin, n, getMetaClass()), block);
         }
         return this;
     }
 
     @JRubyMethod(name = "choice", compat = CompatVersion.RUBY1_8)
     public IRubyObject choice(ThreadContext context) {
         return (realLength == 0) ? context.getRuntime().getNil() : choiceCommon(context);
     }
 
     @JRubyMethod(name = "choice", compat = CompatVersion.RUBY1_9)
     public IRubyObject choice19(ThreadContext context) {
         if (realLength == 0) {
             throw context.getRuntime().newNoMethodError("undefined method 'choice' for []:Array", null, context.getRuntime().getNil());
         }
         
         return choiceCommon(context);
     }
 
     public IRubyObject choiceCommon(ThreadContext context) {
         return values[begin + context.getRuntime().getRandom().nextInt(realLength)];
     }
 
     @JRubyMethod(name = "shuffle!")
     public IRubyObject shuffle_bang(ThreadContext context) {
         modify();
         Random random = context.getRuntime().getRandom();
         
         int i = realLength;
         
         try {
             while (i > 0) {
                 int r = random.nextInt(i);
                 IRubyObject tmp = values[begin + --i];
                 values[begin + i] = values[begin + r];
                 values[begin + r] = tmp;
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
 
         return this;
     }
 
     @JRubyMethod(name = "shuffle")
     public IRubyObject shuffle(ThreadContext context) {
         RubyArray ary = aryDup();
         ary.shuffle_bang(context);
         return ary;
     }
 
     @JRubyMethod(name = "sample", compat = CompatVersion.RUBY1_9)
     public IRubyObject sample(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (realLength == 0) return runtime.getNil();
         int i = realLength == 1 ? 0 : runtime.getRandom().nextInt(realLength);
         try {
             return values[begin + i];
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
             return runtime.getNil();
         }
     }
 
     private static int SORTED_THRESHOLD = 10; 
     @JRubyMethod(name = "sample", compat = CompatVersion.RUBY1_9)
     public IRubyObject sample(ThreadContext context, IRubyObject nv) {
         Ruby runtime = context.getRuntime();
         Random random = runtime.getRandom();
         int n = RubyNumeric.num2int(nv);
 
         if (n < 0) throw runtime.newArgumentError("negative sample number");
 
         int i, j, k;
         switch (n) {
         case 0: 
             return newEmptyArray(runtime);
         case 1:
             if (realLength <= 0) return newEmptyArray(runtime);
             
             return newArray(runtime, values[begin + random.nextInt(realLength)]);
         case 2:
             i = random.nextInt(realLength);
             j = random.nextInt(realLength - 1);
             if (j >= i) j++;
             return newArray(runtime, values[begin + i], values[begin + j]);
         case 3:
             i = random.nextInt(realLength);
             j = random.nextInt(realLength - 1);
             k = random.nextInt(realLength - 2);
             int l = j, g = i;
             if (j >= i) {
                 l = i;
                 g = ++j;
             }
             if (k >= l && (++k >= g)) ++k;
             return new RubyArray(runtime, new IRubyObject[] {values[begin + i], values[begin + j], values[begin + k]});
         }
         
         int len = realLength;
         if (n > len) n = len;
         if (n < SORTED_THRESHOLD) {
             int idx[] = new int[SORTED_THRESHOLD];
             int sorted[] = new int[SORTED_THRESHOLD];
             sorted[0] = idx[0] = random.nextInt(len);
             for (i = 1; i < n; i++) {
                 k = random.nextInt(--len);
                 for (j = 0; j < i; j++) {
                     if (k < sorted[j]) break;
                     k++;
                 }
                 System.arraycopy(sorted, j, sorted, j + 1, i - j);
                 sorted[j] = idx[i] = k;
             }
             IRubyObject[]result = new IRubyObject[n];
             for (i = 0; i < n; i++) result[i] = values[begin + idx[i]];
             return new RubyArray(runtime, result);
         } else {
             IRubyObject[]result = new IRubyObject[len];
             System.arraycopy(values, begin, result, 0, len);
             for (i = 0; i < n; i++) {
                 j = random.nextInt(len - i) + i;
                 IRubyObject tmp = result[j];
                 result[j] = result[i];
                 result[i] = tmp;
             }
             RubyArray ary = new RubyArray(runtime, result);
             ary.realLength = n;
             return ary;
         }
     }
 
     public static void marshalTo(RubyArray array, MarshalStream output) throws IOException {
         output.registerLinkTarget(array);
         output.writeInt(array.getList().size());
         for (Iterator iter = array.getList().iterator(); iter.hasNext();) {
             output.dumpObject((IRubyObject) iter.next());
         }
     }
 
     public static RubyArray unmarshalFrom(UnmarshalStream input) throws IOException {
         RubyArray result = input.getRuntime().newArray();
         input.registerLinkTarget(result);
         int size = input.unmarshalInt();
         for (int i = 0; i < size; i++) {
             result.append(input.unmarshalObject());
         }
         return result;
     }
 
     @JRubyMethod(name = "try_convert", meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject try_convert(ThreadContext context, IRubyObject self, IRubyObject arg) {
         return arg.checkArrayType();
     }
 
     /**
      * @see org.jruby.util.Pack#pack
      */
     @JRubyMethod(name = "pack", required = 1)
     public RubyString pack(ThreadContext context, IRubyObject obj) {
         RubyString iFmt = RubyString.objAsString(context, obj);
         return Pack.pack(getRuntime(), this, iFmt.getByteList());
     }
 
     @Override
     public Class getJavaClass() {
         return List.class;
     }
 
     // Satisfy java.util.List interface (for Java integration)
     public int size() {
         return realLength;
     }
 
     public boolean isEmpty() {
         return realLength == 0;
     }
 
     public boolean contains(Object element) {
         return indexOf(element) != -1;
     }
 
     public Object[] toArray() {
         Object[] array = new Object[realLength];
         for (int i = begin; i < realLength; i++) {
             array[i - begin] = values[i].toJava(Object.class);
         }
         return array;
     }
 
     public Object[] toArray(final Object[] arg) {
         Object[] array = arg;
         if (array.length < realLength) {
             Class type = array.getClass().getComponentType();
             array = (Object[]) Array.newInstance(type, realLength);
         }
         int length = realLength - begin;
 
         for (int i = 0; i < length; i++) {
             array[i] = values[i + begin].toJava(Object.class);
         }
         return array;
     }
 
     public boolean add(Object element) {
         append(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element));
         return true;
     }
 
     public boolean remove(Object element) {
         IRubyObject deleted = delete(getRuntime().getCurrentContext(), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element), Block.NULL_BLOCK);
         return deleted.isNil() ? false : true; // TODO: is this correct ?
     }
 
     public boolean containsAll(Collection c) {
         for (Iterator iter = c.iterator(); iter.hasNext();) {
             if (indexOf(iter.next()) == -1) {
                 return false;
             }
         }
 
         return true;
     }
 
     public boolean addAll(Collection c) {
         for (Iterator iter = c.iterator(); iter.hasNext();) {
             add(iter.next());
         }
         return !c.isEmpty();
     }
 
     public boolean addAll(int index, Collection c) {
         Iterator iter = c.iterator();
         for (int i = index; iter.hasNext(); i++) {
             add(i, iter.next());
         }
         return !c.isEmpty();
     }
 
     public boolean removeAll(Collection c) {
         boolean listChanged = false;
         for (Iterator iter = c.iterator(); iter.hasNext();) {
             if (remove(iter.next())) {
                 listChanged = true;
             }
         }
         return listChanged;
     }
 
     public boolean retainAll(Collection c) {
         boolean listChanged = false;
 
         for (Iterator iter = iterator(); iter.hasNext();) {
             Object element = iter.next();
             if (!c.contains(element)) {
                 remove(element);
                 listChanged = true;
             }
         }
         return listChanged;
     }
 
     public Object get(int index) {
         return elt(index).toJava(Object.class);
     }
 
     public Object set(int index, Object element) {
         return store(index, JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element));
     }
 
     // TODO: make more efficient by not creating IRubyArray[]
     public void add(int index, Object element) {
         insert(new IRubyObject[]{RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element)});
     }
 
     public Object remove(int index) {
         return delete_at(index).toJava(Object.class);
     }
 
     public int indexOf(Object element) {
         int begin = this.begin;
 
         if (element != null) {
             IRubyObject convertedElement = JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element);
 
diff --git a/src/org/jruby/RubyRange.java b/src/org/jruby/RubyRange.java
index ae149a0feb..049df84d69 100644
--- a/src/org/jruby/RubyRange.java
+++ b/src/org/jruby/RubyRange.java
@@ -1,695 +1,708 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001 Ed Sinjiashvili <slorcim@users.sourceforge.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jcodings.Encoding;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.CallBlock;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.TypeConverter;
 
 /**
  * @author jpetersen
  */
 @JRubyClass(name="Range", include="Enumerable")
 public class RubyRange extends RubyObject {
     private IRubyObject begin;
     private IRubyObject end;
     private boolean isExclusive;
 
     public static RubyClass createRangeClass(Ruby runtime) {
         RubyClass result = runtime.defineClass("Range", runtime.getObject(), RANGE_ALLOCATOR);
         runtime.setRange(result);
         result.kindOf = new RubyModule.KindOf() {
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyRange;
             }
         };
         
         result.setMarshal(RANGE_MARSHAL);
         result.includeModule(runtime.getEnumerable());
 
         result.defineAnnotatedMethods(RubyRange.class);
         return result;
     }
 
     private static final ObjectAllocator RANGE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyRange(runtime, klass);
         }
     };    
 
     private RubyRange(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
         begin = end = runtime.getNil();
     }
 
     public static RubyRange newRange(Ruby runtime, ThreadContext context, IRubyObject begin, IRubyObject end, boolean isExclusive) {
         RubyRange range = new RubyRange(runtime, runtime.getRange());
         range.init(context, begin, end, isExclusive);
         return range;
     }
 
     public static RubyRange newExclusiveRange(Ruby runtime, ThreadContext context, IRubyObject begin, IRubyObject end) {
         RubyRange range = new RubyRange(runtime, runtime.getRange());
         range.init(context, begin, end, true);
         return range;
     }
 
     public static RubyRange newInclusiveRange(Ruby runtime, ThreadContext context, IRubyObject begin, IRubyObject end) {
         RubyRange range = new RubyRange(runtime, runtime.getRange());
         range.init(context, begin, end, false);
         return range;
     }
 
     @Override
     public void copySpecialInstanceVariables(IRubyObject clone) {
         RubyRange range = (RubyRange)clone;
         range.begin = begin;
         range.end = end;
         range.isExclusive = isExclusive;
     }
 
+    final boolean checkBegin(long length) {
+        long beg = RubyNumeric.num2long(this.begin);
+        if(beg < 0) {
+            beg += length;
+            if(beg < 0) {
+                return false;
+            }
+        } else if(length < beg) {
+            return false;
+        }
+        return true;
+    }
+
     final long[] begLen(long len, int err){
         long beg = RubyNumeric.num2long(this.begin);
         long end = RubyNumeric.num2long(this.end);
 
         if (beg < 0) {
             beg += len;
             if (beg < 0) {
                 if (err != 0) throw getRuntime().newRangeError(beg + ".." + (isExclusive ? "." : "") + end + " out of range");
                 return null;
             }
         }
 
         if (err == 0 || err == 2) {
             if (beg > len) {
                 if (err != 0) throw getRuntime().newRangeError(beg + ".." + (isExclusive ? "." : "") + end + " out of range");
                 return null;
             }
             if (end > len) end = len;
         }
 
         if (end < 0) end += len;
         if (!isExclusive) end++;
         len = end - beg;
         if (len < 0) len = 0;
 
         return new long[]{beg, len};
     }
 
     final int[] begLenInt(int len, int err){
         int beg = RubyNumeric.num2int(this.begin);
         int end = RubyNumeric.num2int(this.end);
 
         if (beg < 0) {
             beg += len;
             if (beg < 0) {
                 if (err != 0) throw getRuntime().newRangeError(beg + ".." + (isExclusive ? "." : "") + end + " out of range");
                 return null;
             }
         }
 
         if (err == 0 || err == 2) {
             if (beg > len) {
                 if (err != 0) throw getRuntime().newRangeError(beg + ".." + (isExclusive ? "." : "") + end + " out of range");
                 return null;
             }
             if (end > len) end = len;
         }
 
         if (end < 0) end += len;
         if (!isExclusive) end++;
         len = end - beg;
         if (len < 0) len = 0;
 
         return new int[]{beg, len};
     }
 
     private void init(ThreadContext context, IRubyObject begin, IRubyObject end, boolean isExclusive) {
         if (!(begin instanceof RubyFixnum && end instanceof RubyFixnum)) {
             try {
                 IRubyObject result = begin.callMethod(context, "<=>", end);
                 if (result.isNil()) throw getRuntime().newArgumentError("bad value for range");
             } catch (RaiseException re) {
                 throw getRuntime().newArgumentError("bad value for range");
             }
         }
 
         this.begin = begin;
         this.end = end;
         this.isExclusive = isExclusive;
     }
     
     @JRubyMethod(name = "initialize", required = 2, optional = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject[] args, Block unusedBlock) {
         if (!begin.isNil() || !end.isNil()) {
             throw getRuntime().newNameError("`initialize' called twice", "initialize");
         }
         init(context, args[0], args[1], args.length > 2 && args[2].isTrue());
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = {"first", "begin"})
     public IRubyObject first() {
         return begin;
     }
 
     @JRubyMethod(name = {"last", "end"})
     public IRubyObject last() {
         return end;
     }
     
     @JRubyMethod(name = "hash")
     public RubyFixnum hash(ThreadContext context) {
         long hash = isExclusive ? 1 : 0;
         long h = hash;
         
         long v = begin.callMethod(context, "hash").convertToInteger().getLongValue();
         hash ^= v << 1;
         v = end.callMethod(context, "hash").convertToInteger().getLongValue();
         hash ^= v << 9;
         hash ^= h << 24;
         return getRuntime().newFixnum(hash);
     }
     
     private static byte[] DOTDOTDOT = "...".getBytes();
     private static byte[] DOTDOT = "..".getBytes();
 
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect(ThreadContext context) {
         RubyString str = inspect(context, begin).strDup(context.getRuntime());
         RubyString str2 = inspect(context, end);
 
         str.cat(isExclusive ? DOTDOTDOT : DOTDOT);
         str.concat(str2);
         str.infectBy(str2);
         return str;
     }
     
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s(ThreadContext context) {
         RubyString str = RubyString.objAsString(context, begin).strDup(context.getRuntime());
         RubyString str2 = RubyString.objAsString(context, end);
 
         str.cat(isExclusive ? DOTDOTDOT : DOTDOT);
         str.concat(str2);
         str.infectBy(str2);
         return str;
 
     }
 
     @JRubyMethod(name = "exclude_end?")
     public RubyBoolean exclude_end_p() {
         return getRuntime().newBoolean(isExclusive);
     }
 
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyRange)) return getRuntime().getFalse();
         RubyRange otherRange = (RubyRange) other;
 
         if (equalInternal(context, begin, otherRange.begin) &&
             equalInternal(context, end, otherRange.end) &&
             isExclusive == otherRange.isExclusive) return getRuntime().getTrue();
 
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql_p(ThreadContext context, IRubyObject other) {
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyRange)) return getRuntime().getFalse();
         RubyRange otherRange = (RubyRange)other;
 
         if (eqlInternal(context, begin, otherRange.begin) &&
             eqlInternal(context, end, otherRange.end) &&
             isExclusive == otherRange.isExclusive) return getRuntime().getTrue();
 
         return getRuntime().getFalse();
     }
 
     private static abstract class RangeCallBack {
         abstract void call(ThreadContext context, IRubyObject arg);
     }
 
     private static final class StepBlockCallBack extends RangeCallBack implements BlockCallback {
         final Block block;
         IRubyObject iter;
         final IRubyObject step;
 
         StepBlockCallBack(Block block, IRubyObject iter, IRubyObject step) {
             this.block = block;
             this.iter = iter;
             this.step = step;
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject[] args, Block originalBlock) {
             call(context, args[0]);
             return context.getRuntime().getNil();
         }
 
         void call(ThreadContext context, IRubyObject arg) {
             if (iter instanceof RubyFixnum) {
                 iter = RubyFixnum.newFixnum(context.getRuntime(), ((RubyFixnum)iter).getLongValue() - 1);                
             } else {
                 iter = iter.callMethod(context, "-", RubyFixnum.one(context.getRuntime()));
             }
             if (iter == RubyFixnum.zero(context.getRuntime())) {
                 block.yield(context, arg);
                 iter = step;
             }
         }
     }
 
     private IRubyObject rangeLt(ThreadContext context, IRubyObject a, IRubyObject b) {
         IRubyObject result = a.callMethod(context, "<=>", b);
         if (result.isNil()) return null;
         return RubyComparable.cmpint(context, result, a, b) < 0 ? getRuntime().getTrue() : null;
     }
 
     private IRubyObject rangeLe(ThreadContext context, IRubyObject a, IRubyObject b) {
         IRubyObject result = a.callMethod(context, "<=>", b);
         if (result.isNil()) return null;
         int c = RubyComparable.cmpint(context, result, a, b);
         if (c == 0) return RubyFixnum.zero(getRuntime());
         return c < 0 ? getRuntime().getTrue() : null;
     }    
 
     private void rangeEach(ThreadContext context, RangeCallBack callback) {
         IRubyObject v = begin;
         if (isExclusive) {
             while (rangeLt(context, v, end) != null) {
                 callback.call(context, v);
                 v = v.callMethod(context, "succ");
             }
         } else {
             IRubyObject c;
             while ((c = rangeLe(context, v, end)) != null && c.isTrue()) {
                 callback.call(context, v);
                 if (c == RubyFixnum.zero(getRuntime())) break;
                 v = v.callMethod(context, "succ");
             }
         }
     }
 
     @JRubyMethod(name = "to_a", frame = true)
     public IRubyObject to_a(ThreadContext context, final Block block) {
         final Ruby runtime = context.getRuntime();
 
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum) {
             long lim = ((RubyFixnum) end).getLongValue();
             if (!isExclusive) lim++;
 
             long base = ((RubyFixnum) begin).getLongValue();
             long size = lim - base;
             if (size > Integer.MAX_VALUE) {
                 throw runtime.newRangeError("Range size too large for to_a");
             }
             if (size < 0) return RubyArray.newEmptyArray(runtime);
             IRubyObject[] array = new IRubyObject[(int)size];
             for (int i = 0; i < size; i++) {
                 array[i] = RubyFixnum.newFixnum(runtime, base + i);
             }
             return RubyArray.newArrayNoCopy(runtime, array);
         } else {
             return RubyEnumerable.to_a19(context, this);
         }
     }
 
     @JRubyMethod(name = "each", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject each(ThreadContext context, final Block block) {
         final Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "each");
 
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum) {
             fixnumEach(context, runtime, block);
         } else if (begin instanceof RubyString) {
             ((RubyString) begin).uptoCommon18(context, end, isExclusive, block);
         } else {
             if (!begin.respondsTo("succ")) throw getRuntime().newTypeError(
                     "can't iterate from " + begin.getMetaClass().getName());
             rangeEach(context, new RangeCallBack() {
                 @Override
                 void call(ThreadContext context, IRubyObject arg) {
                     block.yield(context, arg);
                 }
             });
         }
         return this;
     }
 
     private void fixnumEach(ThreadContext context, Ruby runtime, Block block) {
         long lim = ((RubyFixnum) end).getLongValue();
         if (!isExclusive) lim++;
 
         if (block.getBody().getArgumentType() == BlockBody.ZERO_ARGS) {
             final IRubyObject nil = runtime.getNil();
             for (long i = ((RubyFixnum) begin).getLongValue(); i < lim; i++) {
                 block.yield(context, nil);
             }
         } else {
             for (long i = ((RubyFixnum) begin).getLongValue(); i < lim; i++) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
         }
     }
 
     @JRubyMethod(name = "each", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject each19(final ThreadContext context, final Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "each");
 
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum) {
             fixnumEach(context, runtime, block);
         } else if (begin instanceof RubyString) {
             ((RubyString) begin).uptoCommon19(context, end, isExclusive, block);
         } else {
             if (!begin.respondsTo("succ")) throw getRuntime().newTypeError(
                     "can't iterate from " + begin.getMetaClass().getName());
             rangeEach(context, new RangeCallBack() {
                 @Override
                 void call(ThreadContext context, IRubyObject arg) {
                     block.yield(context, arg);
                 }
             });
         }
         return this;
     }
 
     @JRubyMethod(name = "step", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject step(ThreadContext context, IRubyObject step, Block block) {
         return block.isGiven() ? stepCommon(context, step, block) : enumeratorize(context.getRuntime(), this, "step", step);
     }
 
     @JRubyMethod(name = "step", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject step(ThreadContext context, Block block) {
         return block.isGiven() ? stepCommon(context, RubyFixnum.one(context.getRuntime()), block)  : enumeratorize(context.getRuntime(), this, "step");
     }
 
     private IRubyObject stepCommon(ThreadContext context, IRubyObject step, Block block) {
         final Ruby runtime = context.getRuntime();
         long unit = RubyNumeric.num2long(step);
         if (unit < 0) throw runtime.newArgumentError("step can't be negative");
 
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum) {
             if (unit == 0) throw runtime.newArgumentError("step can't be 0");
             fixnumStep(context, runtime, unit, block);
         } else {
             IRubyObject tmp = begin.checkStringType();
             if (!tmp.isNil()) {
                 if (unit == 0) throw runtime.newArgumentError("step can't be 0");
                 // rb_iterate((VALUE(*)_((VALUE)))str_step, (VALUE)args, step_i, (VALUE)iter);
                 StepBlockCallBack callback = new StepBlockCallBack(block, RubyFixnum.one(runtime), step);
                 Block blockCallback = CallBlock.newCallClosure(this, runtime.getRange(), Arity.singleArgument(), callback, context);
                 ((RubyString)tmp).uptoCommon18(context, end, isExclusive, blockCallback);
             } else if (begin instanceof RubyNumeric) {
                 if (equalInternal(context, step, RubyFixnum.zero(runtime))) throw runtime.newArgumentError("step can't be 0");
                 numericStep(context, runtime, step, block);
             } else {
                 if (unit == 0) throw runtime.newArgumentError("step can't be 0");
                 if (!begin.respondsTo("succ")) throw runtime.newTypeError("can't iterate from " + begin.getMetaClass().getName());
                 // range_each_func(range, step_i, b, e, args);
                 rangeEach(context, new StepBlockCallBack(block, RubyFixnum.one(runtime), step));
             }
         }
         return this;
     }
 
     private void fixnumStep(ThreadContext context, Ruby runtime, long unit, Block block) {
         long e = ((RubyFixnum)end).getLongValue();
         if (!isExclusive) e++;
         for (long i = ((RubyFixnum)begin).getLongValue(); i < e; i += unit) {
             block.yield(context, RubyFixnum.newFixnum(runtime, i));
         }
     }
 
     private void numericStep(ThreadContext context, Ruby runtime, IRubyObject step, Block block) {
         final String method = isExclusive ? "<" : "<=";
         IRubyObject beg = begin;
         while (beg.callMethod(context, method, end).isTrue()) {
             block.yield(context, beg);
             beg = beg.callMethod(context, "+", step);
         }
     }
 
     @JRubyMethod(name = "step", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject step19(final ThreadContext context, final Block block) {
         return block.isGiven() ? stepCommon19(context, RubyFixnum.zero(context.getRuntime()), block) : enumeratorize(context.getRuntime(), this, "step");
     }
 
     @JRubyMethod(name = "step", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject step19(final ThreadContext context, IRubyObject step, final Block block) {
         Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "step", step);
 
         if (!(step instanceof RubyNumeric)) step = step.convertToInteger("to_int");
         IRubyObject zero = RubyFixnum.zero(runtime);
         if (step.callMethod(context, "<", zero).isTrue()) throw runtime.newArgumentError("step can't be negative");
         if (!step.callMethod(context, ">", zero).isTrue()) throw runtime.newArgumentError("step can't be 0");
         return stepCommon19(context, step, block);
     }
 
     private IRubyObject stepCommon19(ThreadContext context, IRubyObject step, Block block) {
         Ruby runtime = context.getRuntime();
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum && step instanceof RubyFixnum) {
             fixnumStep(context, runtime, ((RubyFixnum)step).getLongValue(), block);
         } else if (begin instanceof RubyFloat || end instanceof RubyFloat || step instanceof RubyFloat) {
             RubyNumeric.floatStep19(context, runtime, begin, end, step, isExclusive, block);
         } else if (begin instanceof RubyNumeric ||
                         !checkIntegerType(runtime, begin, "to_int").isNil() ||
                         !checkIntegerType(runtime, end, "to_int").isNil()) {
             numericStep19(context, runtime, step, block);
         } else {
             IRubyObject tmp = begin.checkStringType();
             if (!tmp.isNil()) {
                 StepBlockCallBack callback = new StepBlockCallBack(block, RubyFixnum.one(runtime), step);
                 Block blockCallback = CallBlock.newCallClosure(this, runtime.getRange(), Arity.singleArgument(), callback, context);
                 ((RubyString)tmp).uptoCommon19(context, end, isExclusive, blockCallback);
             } else {
                 if (!begin.respondsTo("succ")) throw runtime.newTypeError("can't iterate from " + begin.getMetaClass().getName());
                 // range_each_func(range, step_i, b, e, args);
                 rangeEach(context, new StepBlockCallBack(block, RubyFixnum.one(runtime), step));
             }
         }
         return this;
     }
 
     private void numericStep19(ThreadContext context, Ruby runtime, IRubyObject step, Block block) {
         final String method = isExclusive ? "<" : "<=";
         IRubyObject beg = begin;
         long i = 0;
         while (beg.callMethod(context, method, end).isTrue()) {
             block.yield(context, beg);
             i++;
             beg = begin.callMethod(context, "+", RubyFixnum.newFixnum(runtime, i).callMethod(context, "*", step));
         }
     }
 
     @JRubyMethod(name = {"include?", "member?", "==="}, required = 1, compat = CompatVersion.RUBY1_8)
     public RubyBoolean include_p(ThreadContext context, IRubyObject obj) {
         if (rangeLe(context, begin, obj) != null) {
             if (isExclusive) {
                 if (rangeLt(context, obj, end) != null) return context.getRuntime().getTrue();
             } else {
                 if (rangeLe(context, obj, end) != null) return context.getRuntime().getTrue();
             }
         }
         return context.getRuntime().getFalse();
     }
 
     @JRubyMethod(name = {"include?", "member?"}, frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject include_p19(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         if (begin instanceof RubyNumeric || end instanceof RubyNumeric ||
                 !TypeConverter.convertToTypeWithCheck(begin, runtime.getInteger(), "to_int").isNil() ||
                 !TypeConverter.convertToTypeWithCheck(end, runtime.getInteger(), "to_int").isNil()) {
             if (rangeLe(context, begin, obj) != null) {
                 if (isExclusive) {
                     if (rangeLt(context, obj, end) != null) return runtime.getTrue();
                 } else {
                     if (rangeLe(context, obj, end) != null) return runtime.getTrue();
                 }
             }
             return runtime.getFalse();
         } else if (begin instanceof RubyString && end instanceof RubyString &&
                 ((RubyString)begin).getByteList().realSize == 1 &&
                 ((RubyString)end).getByteList().realSize == 1) {
             if (obj.isNil()) return runtime.getFalse();
             if (obj instanceof RubyString) {
                 ByteList Vbytes = ((RubyString)obj).getByteList();
                 if (Vbytes.realSize != 1) return runtime.getFalse();
                 int v = Vbytes.bytes[Vbytes.begin] & 0xff;
                 ByteList Bbytes = ((RubyString)begin).getByteList();
                 int b = Bbytes.bytes[Bbytes.begin] & 0xff;
                 ByteList Ebytes = ((RubyString)end).getByteList();
                 int e = Ebytes.bytes[Ebytes.begin] & 0xff;
                 if (Encoding.isAscii(v) && Encoding.isAscii(b) && Encoding.isAscii(e)) {
                     if ((b <= v && v < e) || (!isExclusive && v == e)) return runtime.getTrue();
                     return runtime.getFalse();
                 }
             }
         }
         return RuntimeHelpers.invokeSuper(context, this, obj, Block.NULL_BLOCK);
     }
 
     @JRubyMethod(name = "===", compat = CompatVersion.RUBY1_9)
     public IRubyObject eqq_p19(ThreadContext context, IRubyObject obj) {
         return callMethod(context, "include?", obj);
     }
 
     @JRubyMethod(name = "cover?", compat = CompatVersion.RUBY1_9)
     public IRubyObject cover_p(ThreadContext context, IRubyObject obj) {
         return include_p(context, obj); // 1.8 "include?"
     }
 
     @JRubyMethod(name = "min", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject min(ThreadContext context, Block block) {
         if (block.isGiven()) {
             return RuntimeHelpers.invokeSuper(context, this, block);
         } else {
             int c = RubyComparable.cmpint(context, begin.callMethod(context, "<=>", end), begin, end);
             if (c > 0 || (c == 0 && isExclusive)) return context.getRuntime().getNil();
             return begin;
         }
     }
 
     @JRubyMethod(name = "max", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject max(ThreadContext context, Block block) {
         if (block.isGiven() || isExclusive && !(end instanceof RubyNumeric)) {
             return RuntimeHelpers.invokeSuper(context, this, block);
         } else {
             int c = RubyComparable.cmpint(context, begin.callMethod(context, "<=>", end), begin, end);
             Ruby runtime = context.getRuntime();
             if (isExclusive) {
                 if (!(end instanceof RubyInteger)) throw runtime.newTypeError("cannot exclude non Integer end value");
                 if (c == 0) return runtime.getNil();
                 if (end instanceof RubyFixnum) return RubyFixnum.newFixnum(runtime, ((RubyFixnum)end).getLongValue() - 1);
                 return end.callMethod(context, "-", RubyFixnum.one(runtime));
             }
             return end;
         }
     }
 
     @JRubyMethod(name = "first", compat = CompatVersion.RUBY1_9)
     public IRubyObject first(ThreadContext context) {
         return begin;
     }
 
     @JRubyMethod(name = "first", compat = CompatVersion.RUBY1_9)
     public IRubyObject first(ThreadContext context, IRubyObject arg) {
         final Ruby runtime = context.getRuntime();
         final int num = RubyNumeric.num2int(arg);
         final RubyArray result = runtime.newArray(num);
         try {
             RubyEnumerable.callEach(runtime, context, this, new BlockCallback() {
                 int n = num;
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (n-- <= 0) throw JumpException.SPECIAL_JUMP;
                     result.append(largs[0]);
                     return runtime.getNil();
                 }
             });
         } catch (JumpException.SpecialJump sj) {}
         return result;
     }
 
     @JRubyMethod(name = "last", compat = CompatVersion.RUBY1_9)
     public IRubyObject last(ThreadContext context) {
         return end;
     }
 
     @JRubyMethod(name = "last", compat = CompatVersion.RUBY1_9)
     public IRubyObject last(ThreadContext context, IRubyObject arg) {
         return ((RubyArray)RubyKernel.new_array(context, this, this)).last(arg);
     }
 
     private static final ObjectMarshal RANGE_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             RubyRange range = (RubyRange)obj;
 
             marshalStream.registerLinkTarget(range);
             List<Variable<Object>> attrs = range.getVariableList();
 
             attrs.add(new VariableEntry<Object>("begin", range.begin));
             attrs.add(new VariableEntry<Object>("end", range.end));
             attrs.add(new VariableEntry<Object>("excl", range.isExclusive ? runtime.getTrue() : runtime.getFalse()));
 
             marshalStream.dumpVariables(attrs);
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             RubyRange range = (RubyRange)type.allocate();
             
             unmarshalStream.registerLinkTarget(range);
 
             unmarshalStream.defaultVariablesUnmarshal(range);
             
             range.begin = (IRubyObject)range.removeInternalVariable("begin");
             range.end = (IRubyObject)range.removeInternalVariable("end");
             range.isExclusive = ((IRubyObject)range.removeInternalVariable("excl")).isTrue();
 
             return range;
         }
     };
 }
diff --git a/test/externals/ruby1.8/ruby/test_array.rb b/test/externals/ruby1.8/ruby/test_array.rb
index 36f2b85a3d..bddd89ead5 100644
--- a/test/externals/ruby1.8/ruby/test_array.rb
+++ b/test/externals/ruby1.8/ruby/test_array.rb
@@ -105,1170 +105,1169 @@ class TestArray < Test::Unit::TestCase
     x.clear
     assert_equal([], x)
 
     x = [1,2,3]
     y = x.dup
     x << 4
     y << 5
     assert_equal([1,2,3,4], x)
     assert_equal([1,2,3,5], y)
   end
 
   def test_beg_end_0
     x = [1, 2, 3, 4, 5]
     
     assert_equal(1, x.first)
     assert_equal([1], x.first(1))
     assert_equal([1, 2, 3], x.first(3))
     
     assert_equal(5, x.last)
     assert_equal([5], x.last(1))
     assert_equal([3, 4, 5], x.last(3))
     
     assert_equal(1, x.shift)
     assert_equal([2, 3, 4], x.shift(3))
     assert_equal([5], x)
     
     assert_equal([2, 3, 4, 5], x.unshift(2, 3, 4))
     assert_equal([1, 2, 3, 4, 5], x.unshift(1))
     assert_equal([1, 2, 3, 4, 5], x)
     
     assert_equal(5, x.pop)
     assert_equal([3, 4], x.pop(2))
     assert_equal([1, 2], x)
     
     assert_equal([1, 2, 3, 4], x.push(3, 4))
     assert_equal([1, 2, 3, 4, 5], x.push(5))
     assert_equal([1, 2, 3, 4, 5], x)
   end
 
   def test_find_all_0
     assert_respond_to([], :find_all)
     assert_respond_to([], :select)       # Alias
     assert_equal([], [].find_all{ |obj| obj == "foo"})
 
     x = ["foo", "bar", "baz", "baz", 1, 2, 3, 3, 4]
     assert_equal(["baz","baz"], x.find_all{ |obj| obj == "baz" })
     assert_equal([3,3], x.find_all{ |obj| obj == 3 })
   end
 
   def test_fill_0
     assert_equal([-1, -1, -1, -1, -1, -1], [0, 1, 2, 3, 4, 5].fill(-1))
     assert_equal([0, 1, 2, -1, -1, -1], [0, 1, 2, 3, 4, 5].fill(-1, 3))
     assert_equal([0, 1, 2, -1, -1, 5], [0, 1, 2, 3, 4, 5].fill(-1, 3, 2))
     assert_equal([0, 1, 2, -1, -1, -1, -1, -1], [0, 1, 2, 3, 4, 5].fill(-1, 3, 5))
     assert_equal([0, 1, -1, -1, 4, 5], [0, 1, 2, 3, 4, 5].fill(-1, 2, 2))
     assert_equal([0, 1, -1, -1, -1, -1, -1], [0, 1, 2, 3, 4, 5].fill(-1, 2, 5))
     assert_equal([0, 1, 2, 3, -1, 5], [0, 1, 2, 3, 4, 5].fill(-1, -2, 1))
     assert_equal([0, 1, 2, 3, -1, -1, -1], [0, 1, 2, 3, 4, 5].fill(-1, -2, 3))
     assert_equal([0, 1, 2, -1, -1, 5], [0, 1, 2, 3, 4, 5].fill(-1, 3..4))
     assert_equal([0, 1, 2, -1, 4, 5], [0, 1, 2, 3, 4, 5].fill(-1, 3...4))
     assert_equal([0, 1, -1, -1, -1, 5], [0, 1, 2, 3, 4, 5].fill(-1, 2..-2))
     assert_equal([0, 1, -1, -1, 4, 5], [0, 1, 2, 3, 4, 5].fill(-1, 2...-2))
     assert_equal([10, 11, 12, 13, 14, 15], [0, 1, 2, 3, 4, 5].fill{|i| i+10})
     assert_equal([0, 1, 2, 13, 14, 15], [0, 1, 2, 3, 4, 5].fill(3){|i| i+10})
     assert_equal([0, 1, 2, 13, 14, 5], [0, 1, 2, 3, 4, 5].fill(3, 2){|i| i+10})
     assert_equal([0, 1, 2, 13, 14, 15, 16, 17], [0, 1, 2, 3, 4, 5].fill(3, 5){|i| i+10})
     assert_equal([0, 1, 2, 13, 14, 5], [0, 1, 2, 3, 4, 5].fill(3..4){|i| i+10})
     assert_equal([0, 1, 2, 13, 4, 5], [0, 1, 2, 3, 4, 5].fill(3...4){|i| i+10})
     assert_equal([0, 1, 12, 13, 14, 5], [0, 1, 2, 3, 4, 5].fill(2..-2){|i| i+10})
     assert_equal([0, 1, 12, 13, 4, 5], [0, 1, 2, 3, 4, 5].fill(2...-2){|i| i+10})
   end
 
   # From rubicon
 
   def setup
     @cls = Array
   end
 
   def test_00_new
     a = @cls.new()
     assert_instance_of(@cls, a)
     assert_equal(0, a.length)
     assert_nil(a[0])
   end
 
   def test_01_square_brackets
     a = @cls[ 5, 4, 3, 2, 1 ]
     assert_instance_of(@cls, a)
     assert_equal(5, a.length)
     5.times { |i| assert_equal(5-i, a[i]) }
     assert_nil(a[6])
   end
 
   def test_AND # '&'
     assert_equal(@cls[1, 3], @cls[ 1, 1, 3, 5 ] & @cls[ 1, 2, 3 ])
     assert_equal(@cls[],     @cls[ 1, 1, 3, 5 ] & @cls[ ])
     assert_equal(@cls[],     @cls[  ]           & @cls[ 1, 2, 3 ])
     assert_equal(@cls[],     @cls[ 1, 2, 3 ]    & @cls[ 4, 5, 6 ])
   end
 
   def test_MUL # '*'
     assert_equal(@cls[], @cls[]*3)
     assert_equal(@cls[1, 1, 1], @cls[1]*3)
     assert_equal(@cls[1, 2, 1, 2, 1, 2], @cls[1, 2]*3)
     assert_equal(@cls[], @cls[1, 2, 3] * 0)
     assert_raise(ArgumentError) { @cls[1, 2]*(-3) }
 
     assert_equal('1-2-3-4-5', @cls[1, 2, 3, 4, 5] * '-')
     assert_equal('12345',     @cls[1, 2, 3, 4, 5] * '')
 
   end
 
   def test_PLUS # '+'
     assert_equal(@cls[],     @cls[]  + @cls[])
     assert_equal(@cls[1],    @cls[1] + @cls[])
     assert_equal(@cls[1],    @cls[]  + @cls[1])
     assert_equal(@cls[1, 1], @cls[1] + @cls[1])
     assert_equal(@cls['cat', 'dog', 1, 2, 3], %w(cat dog) + (1..3).to_a)
   end
 
   def test_MINUS # '-'
     assert_equal(@cls[],  @cls[1] - @cls[1])
     assert_equal(@cls[1], @cls[1, 2, 3, 4, 5] - @cls[2, 3, 4, 5])
     # Ruby 1.8 feature change
     #assert_equal(@cls[1], @cls[1, 2, 1, 3, 1, 4, 1, 5] - @cls[2, 3, 4, 5])
     assert_equal(@cls[1, 1, 1, 1], @cls[1, 2, 1, 3, 1, 4, 1, 5] - @cls[2, 3, 4, 5])
     a = @cls[]
     1000.times { a << 1 }
     assert_equal(1000, a.length)
     #assert_equal(@cls[1], a - @cls[2])
     assert_equal(@cls[1] * 1000, a - @cls[2])
     #assert_equal(@cls[1],  @cls[1, 2, 1] - @cls[2])
     assert_equal(@cls[1, 1],  @cls[1, 2, 1] - @cls[2])
     assert_equal(@cls[1, 2, 3], @cls[1, 2, 3] - @cls[4, 5, 6])
   end
 
   def test_LSHIFT # '<<'
     a = @cls[]
     a << 1
     assert_equal(@cls[1], a)
     a << 2 << 3
     assert_equal(@cls[1, 2, 3], a)
     a << nil << 'cat'
     assert_equal(@cls[1, 2, 3, nil, 'cat'], a)
     a << a
     assert_equal(@cls[1, 2, 3, nil, 'cat', a], a)
   end
 
   def test_CMP # '<=>'
     assert_equal(0,  @cls[] <=> @cls[])
     assert_equal(0,  @cls[1] <=> @cls[1])
     assert_equal(0,  @cls[1, 2, 3, 'cat'] <=> @cls[1, 2, 3, 'cat'])
     assert_equal(-1, @cls[] <=> @cls[1])
     assert_equal(1,  @cls[1] <=> @cls[])
     assert_equal(-1, @cls[1, 2, 3] <=> @cls[1, 2, 3, 'cat'])
     assert_equal(1,  @cls[1, 2, 3, 'cat'] <=> @cls[1, 2, 3])
     assert_equal(-1, @cls[1, 2, 3, 'cat'] <=> @cls[1, 2, 3, 'dog'])
     assert_equal(1,  @cls[1, 2, 3, 'dog'] <=> @cls[1, 2, 3, 'cat'])
   end
 
   def test_EQUAL # '=='
     assert(@cls[] == @cls[])
     assert(@cls[1] == @cls[1])
     assert(@cls[1, 1, 2, 2] == @cls[1, 1, 2, 2])
     assert(@cls[1.0, 1.0, 2.0, 2.0] == @cls[1, 1, 2, 2])
   end
 
   def test_VERY_EQUAL # '==='
     assert(@cls[] === @cls[])
     assert(@cls[1] === @cls[1])
     assert(@cls[1, 1, 2, 2] === @cls[1, 1, 2, 2])
     assert(@cls[1.0, 1.0, 2.0, 2.0] === @cls[1, 1, 2, 2])
   end
 
   def test_AREF # '[]'
     a = @cls[*(1..100).to_a]
 
     assert_equal(1, a[0])
     assert_equal(100, a[99])
     assert_nil(a[100])
     assert_equal(100, a[-1])
     assert_equal(99,  a[-2])
     assert_equal(1,   a[-100])
     assert_nil(a[-101])
     assert_nil(a[-101,0])
     assert_nil(a[-101,1])
     assert_nil(a[-101,-1])
     assert_nil(a[10,-1])
 
     assert_equal(@cls[1],   a[0,1])
     assert_equal(@cls[100], a[99,1])
     assert_equal(@cls[],    a[100,1])
     assert_equal(@cls[100], a[99,100])
     assert_equal(@cls[100], a[-1,1])
     assert_equal(@cls[99],  a[-2,1])
     assert_equal(@cls[],    a[-100,0])
     assert_equal(@cls[1],   a[-100,1])
 
     assert_equal(@cls[10, 11, 12], a[9, 3])
     assert_equal(@cls[10, 11, 12], a[-91, 3])
 
     assert_equal(@cls[1],   a[0..0])
     assert_equal(@cls[100], a[99..99])
     assert_equal(@cls[],    a[100..100])
     assert_equal(@cls[100], a[99..200])
     assert_equal(@cls[100], a[-1..-1])
     assert_equal(@cls[99],  a[-2..-2])
 
     assert_equal(@cls[10, 11, 12], a[9..11])
     assert_equal(@cls[10, 11, 12], a[-91..-89])
     
     assert_nil(a[10, -3])
     # Ruby 1.8 feature change:
     # Array#[size..x] returns [] instead of nil.
     #assert_nil(a[10..7])
     assert_equal [], a[10..7]
 
     assert_raise(TypeError) {a['cat']}
   end
 
   def test_ASET # '[]='
     a = @cls[*(0..99).to_a]
     assert_equal(0, a[0] = 0)
     assert_equal(@cls[0] + @cls[*(1..99).to_a], a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(0, a[10,10] = 0)
     assert_equal(@cls[*(0..9).to_a] + @cls[0] + @cls[*(20..99).to_a], a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(0, a[-1] = 0)
     assert_equal(@cls[*(0..98).to_a] + @cls[0], a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(0, a[-10, 10] = 0)
     assert_equal(@cls[*(0..89).to_a] + @cls[0], a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(0, a[0,1000] = 0)
     assert_equal(@cls[0] , a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(0, a[10..19] = 0)
     assert_equal(@cls[*(0..9).to_a] + @cls[0] + @cls[*(20..99).to_a], a)
 
     b = @cls[*%w( a b c )]
     a = @cls[*(0..99).to_a]
     assert_equal(b, a[0,1] = b)
     assert_equal(b + @cls[*(1..99).to_a], a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(b, a[10,10] = b)
     assert_equal(@cls[*(0..9).to_a] + b + @cls[*(20..99).to_a], a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(b, a[-1, 1] = b)
     assert_equal(@cls[*(0..98).to_a] + b, a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(b, a[-10, 10] = b)
     assert_equal(@cls[*(0..89).to_a] + b, a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(b, a[0,1000] = b)
     assert_equal(b , a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(b, a[10..19] = b)
     assert_equal(@cls[*(0..9).to_a] + b + @cls[*(20..99).to_a], a)
 
     # Ruby 1.8 feature change:
     # assigning nil does not remove elements.
 =begin
     a = @cls[*(0..99).to_a]
     assert_equal(nil, a[0,1] = nil)
     assert_equal(@cls[*(1..99).to_a], a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(nil, a[10,10] = nil)
     assert_equal(@cls[*(0..9).to_a] + @cls[*(20..99).to_a], a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(nil, a[-1, 1] = nil)
     assert_equal(@cls[*(0..98).to_a], a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(nil, a[-10, 10] = nil)
     assert_equal(@cls[*(0..89).to_a], a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(nil, a[0,1000] = nil)
     assert_equal(@cls[] , a)
 
     a = @cls[*(0..99).to_a]
     assert_equal(nil, a[10..19] = nil)
     assert_equal(@cls[*(0..9).to_a] + @cls[*(20..99).to_a], a)
 =end
 
     a = @cls[1, 2, 3]
     a[1, 0] = a
     assert_equal([1, 1, 2, 3, 2, 3], a)
 
     a = @cls[1, 2, 3]
     a[-1, 0] = a
     assert_equal([1, 2, 1, 2, 3, 3], a)
   end
 
   def test_assoc
     a1 = @cls[*%w( cat feline )]
     a2 = @cls[*%w( dog canine )]
     a3 = @cls[*%w( mule asinine )]
 
     a = @cls[ a1, a2, a3 ]
 
     assert_equal(a1, a.assoc('cat'))
     assert_equal(a3, a.assoc('mule'))
     assert_equal(nil, a.assoc('asinine'))
     assert_equal(nil, a.assoc('wombat'))
     assert_equal(nil, a.assoc(1..2))
   end
 
   def test_at
     a = @cls[*(0..99).to_a]
     assert_equal(0,   a.at(0))
     assert_equal(10,  a.at(10))
     assert_equal(99,  a.at(99))
     assert_equal(nil, a.at(100))
     assert_equal(99,  a.at(-1))
     assert_equal(0,  a.at(-100))
     assert_equal(nil, a.at(-101))
     assert_raise(TypeError) { a.at('cat') }
   end
 
   def test_clear
     a = @cls[1, 2, 3]
     b = a.clear
     assert_equal(@cls[], a)
     assert_equal(@cls[], b)
     assert_equal(a.__id__, b.__id__)
   end
 
   def test_clone
     for taint in [ false, true ]
       for frozen in [ false, true ]
         a = @cls[*(0..99).to_a]
         a.taint  if taint
         a.freeze if frozen
         b = a.clone
 
         assert_equal(a, b)
         assert(a.__id__ != b.__id__)
         assert_equal(a.frozen?, b.frozen?)
         assert_equal(a.tainted?, b.tainted?)
       end
     end
   end
 
   def test_collect
     a = @cls[ 1, 'cat', 1..1 ]
     assert_equal([ Fixnum, String, Range], a.collect {|e| e.class} )
     assert_equal([ 99, 99, 99], a.collect { 99 } )
 
     assert_equal([], @cls[].collect { 99 })
 
     assert_equal([1, 2, 3], @cls[1, 2, 3].collect)
   end
 
   # also update map!
   def test_collect!
     a = @cls[ 1, 'cat', 1..1 ]
     assert_equal([ Fixnum, String, Range], a.collect! {|e| e.class} )
     assert_equal([ Fixnum, String, Range], a)
    
     a = @cls[ 1, 'cat', 1..1 ]
     assert_equal([ 99, 99, 99], a.collect! { 99 } )
     assert_equal([ 99, 99, 99], a)
 
     a = @cls[ ]
     assert_equal([], a.collect! { 99 })
     assert_equal([], a)
   end
 
   def test_compact
     a = @cls[ 1, nil, nil, 2, 3, nil, 4 ]
     assert_equal(@cls[1, 2, 3, 4], a.compact)
 
     a = @cls[ nil, 1, nil, 2, 3, nil, 4 ]
     assert_equal(@cls[1, 2, 3, 4], a.compact)
 
     a = @cls[ 1, nil, nil, 2, 3, nil, 4, nil ]
     assert_equal(@cls[1, 2, 3, 4], a.compact)
 
     a = @cls[ 1, 2, 3, 4 ]
     assert_equal(@cls[1, 2, 3, 4], a.compact)
   end
 
   def test_compact!
     a = @cls[ 1, nil, nil, 2, 3, nil, 4 ]
     assert_equal(@cls[1, 2, 3, 4], a.compact!)
     assert_equal(@cls[1, 2, 3, 4], a)
 
     a = @cls[ nil, 1, nil, 2, 3, nil, 4 ]
     assert_equal(@cls[1, 2, 3, 4], a.compact!)
     assert_equal(@cls[1, 2, 3, 4], a)
 
     a = @cls[ 1, nil, nil, 2, 3, nil, 4, nil ]
     assert_equal(@cls[1, 2, 3, 4], a.compact!)
     assert_equal(@cls[1, 2, 3, 4], a)
 
     a = @cls[ 1, 2, 3, 4 ]
     assert_equal(nil, a.compact!)
     assert_equal(@cls[1, 2, 3, 4], a)
   end
 
   def test_concat
     assert_equal(@cls[1, 2, 3, 4],     @cls[1, 2].concat(@cls[3, 4]))
     assert_equal(@cls[1, 2, 3, 4],     @cls[].concat(@cls[1, 2, 3, 4]))
     assert_equal(@cls[1, 2, 3, 4],     @cls[1, 2, 3, 4].concat(@cls[]))
     assert_equal(@cls[],               @cls[].concat(@cls[]))
     assert_equal(@cls[@cls[1, 2], @cls[3, 4]], @cls[@cls[1, 2]].concat(@cls[@cls[3, 4]]))
     
     a = @cls[1, 2, 3]
     a.concat(a)
     assert_equal([1, 2, 3, 1, 2, 3], a)
   end
 
   def test_count
     a = @cls[1, 2, 3, 1, 2]
     assert_equal(5, a.count)
     assert_equal(2, a.count(1))
     assert_equal(3, a.count {|x| x % 2 == 1 })
     assert_equal(2, a.count(1) {|x| x % 2 == 1 })
     assert_raise(ArgumentError) { a.count(0, 1) }
   end
 
   def test_delete
     a = @cls[*('cab'..'cat').to_a]
     assert_equal('cap', a.delete('cap'))
     assert_equal(@cls[*('cab'..'cao').to_a] + @cls[*('caq'..'cat').to_a], a)
 
     a = @cls[*('cab'..'cat').to_a]
     assert_equal('cab', a.delete('cab'))
     assert_equal(@cls[*('cac'..'cat').to_a], a)
 
     a = @cls[*('cab'..'cat').to_a]
     assert_equal('cat', a.delete('cat'))
     assert_equal(@cls[*('cab'..'cas').to_a], a)
 
     a = @cls[*('cab'..'cat').to_a]
     assert_equal(nil, a.delete('cup'))
     assert_equal(@cls[*('cab'..'cat').to_a], a)
 
     a = @cls[*('cab'..'cat').to_a]
     assert_equal(99, a.delete('cup') { 99 } )
     assert_equal(@cls[*('cab'..'cat').to_a], a)
   end
 
   def test_delete_at
     a = @cls[*(1..5).to_a]
     assert_equal(3, a.delete_at(2))
     assert_equal(@cls[1, 2, 4, 5], a)
 
     a = @cls[*(1..5).to_a]
     assert_equal(4, a.delete_at(-2))
     assert_equal(@cls[1, 2, 3, 5], a)
 
     a = @cls[*(1..5).to_a]
     assert_equal(nil, a.delete_at(5))
     assert_equal(@cls[1, 2, 3, 4, 5], a)
 
     a = @cls[*(1..5).to_a]
     assert_equal(nil, a.delete_at(-6))
     assert_equal(@cls[1, 2, 3, 4, 5], a)
   end
 
   # also reject!
   def test_delete_if
     a = @cls[ 1, 2, 3, 4, 5 ]
     assert_equal(a, a.delete_if { false })
     assert_equal(@cls[1, 2, 3, 4, 5], a)
 
     a = @cls[ 1, 2, 3, 4, 5 ]
     assert_equal(a, a.delete_if { true })
     assert_equal(@cls[], a)
 
     a = @cls[ 1, 2, 3, 4, 5 ]
     assert_equal(a, a.delete_if { |i| i > 3 })
     assert_equal(@cls[1, 2, 3], a)
   end
 
   def test_dup
     for taint in [ false, true ]
       for frozen in [ false, true ]
         a = @cls[*(0..99).to_a]
         a.taint  if taint
         a.freeze if frozen
         b = a.dup
 
         assert_equal(a, b)
         assert(a.__id__ != b.__id__)
         assert_equal(false, b.frozen?)
         assert_equal(a.tainted?, b.tainted?)
       end
     end
   end
 
   def test_each
     a = @cls[*%w( ant bat cat dog )]
     i = 0
     a.each { |e|
       assert_equal(a[i], e)
       i += 1
     }
     assert_equal(4, i)
 
     a = @cls[]
     i = 0
     a.each { |e|
       assert_equal(a[i], e)
       i += 1
     }
     assert_equal(0, i)
 
     assert_equal(a, a.each {})
   end
 
   def test_each_index
     a = @cls[*%w( ant bat cat dog )]
     i = 0
     a.each_index { |ind|
       assert_equal(i, ind)
       i += 1
     }
     assert_equal(4, i)
 
     a = @cls[]
     i = 0
     a.each_index { |ind|
       assert_equal(i, ind)
       i += 1
     }
     assert_equal(0, i)
 
     assert_equal(a, a.each_index {})
   end
 
   def test_empty?
     assert(@cls[].empty?)
     assert(!@cls[1].empty?)
   end
 
   def test_eql?
     assert(@cls[].eql?(@cls[]))
     assert(@cls[1].eql?(@cls[1]))
     assert(@cls[1, 1, 2, 2].eql?(@cls[1, 1, 2, 2]))
     assert(!@cls[1.0, 1.0, 2.0, 2.0].eql?(@cls[1, 1, 2, 2]))
   end
 
   def test_fill
     assert_equal(@cls[],   @cls[].fill(99))
     assert_equal(@cls[],   @cls[].fill(99, 0))
     assert_equal(@cls[99], @cls[].fill(99, 0, 1))
     assert_equal(@cls[99], @cls[].fill(99, 0..0))
 
     assert_equal(@cls[99],   @cls[1].fill(99))
     assert_equal(@cls[99],   @cls[1].fill(99, 0))
     assert_equal(@cls[99],   @cls[1].fill(99, 0, 1))
     assert_equal(@cls[99],   @cls[1].fill(99, 0..0))
 
     assert_equal(@cls[99, 99], @cls[1, 2].fill(99))
     assert_equal(@cls[99, 99], @cls[1, 2].fill(99, 0))
     assert_equal(@cls[99, 99], @cls[1, 2].fill(99, nil))
     assert_equal(@cls[1,  99], @cls[1, 2].fill(99, 1, nil))
     assert_equal(@cls[99,  2], @cls[1, 2].fill(99, 0, 1))
     assert_equal(@cls[99,  2], @cls[1, 2].fill(99, 0..0))
   end
 
   def test_first
     assert_equal(3,   @cls[3, 4, 5].first)
     assert_equal(nil, @cls[].first)
   end
 
   def test_flatten
     a1 = @cls[ 1, 2, 3]
     a2 = @cls[ 5, 6 ]
     a3 = @cls[ 4, a2 ]
     a4 = @cls[ a1, a3 ]
     assert_equal(@cls[1, 2, 3, 4, 5, 6], a4.flatten)
     assert_equal(@cls[ a1, a3], a4)
 
     a5 = @cls[ a1, @cls[], a3 ]
     assert_equal(@cls[1, 2, 3, 4, 5, 6], a5.flatten)
     assert_equal(@cls[], @cls[].flatten)
     assert_equal(@cls[], 
                  @cls[@cls[@cls[@cls[],@cls[]],@cls[@cls[]],@cls[]],@cls[@cls[@cls[]]]].flatten)
 
     assert_raise(TypeError, "[ruby-dev:31197]") { [[]].flatten("") }
   end
 
   def test_flatten!
     a1 = @cls[ 1, 2, 3]
     a2 = @cls[ 5, 6 ]
     a3 = @cls[ 4, a2 ]
     a4 = @cls[ a1, a3 ]
     assert_equal(@cls[1, 2, 3, 4, 5, 6], a4.flatten!)
     assert_equal(@cls[1, 2, 3, 4, 5, 6], a4)
 
     a5 = @cls[ a1, @cls[], a3 ]
     assert_equal(@cls[1, 2, 3, 4, 5, 6], a5.flatten!)
     assert_equal(@cls[1, 2, 3, 4, 5, 6], a5)
 
     assert_equal(@cls[], @cls[].flatten)
     assert_equal(@cls[], 
                  @cls[@cls[@cls[@cls[],@cls[]],@cls[@cls[]],@cls[]],@cls[@cls[@cls[]]]].flatten)
   end
 
   # JRuby does not support callcc
   # def test_flatten_with_callcc
   #   respond_to?(:callcc, true) or require 'continuation'
   #   o = Object.new
   #   def o.to_ary() callcc {|k| @cont = k; [1,2,3]} end
   #   begin
   #     assert_equal([10, 20, 1, 2, 3, 30, 1, 2, 3, 40], [10, 20, o, 30, o, 40].flatten)
   #   rescue => e
   #   else
   #     o.instance_eval {@cont}.call
   #   end
   #   assert_instance_of(RuntimeError, e, '[ruby-dev:34798]')
   #   assert_match(/reentered/, e.message, '[ruby-dev:34798]')
   # end
 
   def test_hash
     a1 = @cls[ 'cat', 'dog' ]
     a2 = @cls[ 'cat', 'dog' ]
     a3 = @cls[ 'dog', 'cat' ]
     assert(a1.hash == a2.hash)
     assert(a1.hash != a3.hash)
   end
 
   def test_include?
     a = @cls[ 'cat', 99, /a/, @cls[ 1, 2, 3] ]
     assert(a.include?('cat'))
     assert(a.include?(99))
     assert(a.include?(/a/))
     assert(a.include?([1,2,3]))
     assert(!a.include?('ca'))
     assert(!a.include?([1,2]))
   end
 
   def test_index
     a = @cls[ 'cat', 99, /a/, 99, @cls[ 1, 2, 3] ]
     assert_equal(0, a.index('cat'))
     assert_equal(1, a.index(99))
     assert_equal(4, a.index([1,2,3]))
     assert_nil(a.index('ca'))
     assert_nil(a.index([1,2]))
   end
 
   def test_values_at
     a = @cls[*('a'..'j').to_a]
     assert_equal(@cls['a', 'c', 'e'], a.values_at(0, 2, 4))
     assert_equal(@cls['j', 'h', 'f'], a.values_at(-1, -3, -5))
     assert_equal(@cls['h', nil, 'a'], a.values_at(-3, 99, 0))
   end
 
   def test_join
     $, = ""
     a = @cls[]
     assert_equal("", a.join)
     assert_equal("", a.join(','))
 
     $, = ""
     a = @cls[1, 2]
     assert_equal("12", a.join)
     assert_equal("1,2", a.join(','))
 
     $, = ""
     a = @cls[1, 2, 3]
     assert_equal("123", a.join)
     assert_equal("1,2,3", a.join(','))
 
     $, = ":"
     a = @cls[1, 2, 3]
     assert_equal("1:2:3", a.join)
     assert_equal("1,2,3", a.join(','))
 
     $, = ""
   end
 
   def test_last
     assert_equal(nil, @cls[].last)
     assert_equal(1, @cls[1].last)
     assert_equal(99, @cls[*(3..99).to_a].last)
   end
 
   def test_length
     assert_equal(0, @cls[].length)
     assert_equal(1, @cls[1].length)
     assert_equal(2, @cls[1, nil].length)
     assert_equal(2, @cls[nil, 1].length)
     assert_equal(234, @cls[*(0..233).to_a].length)
   end
 
   # also update collect!
   def test_map!
     a = @cls[ 1, 'cat', 1..1 ]
     assert_equal(@cls[ Fixnum, String, Range], a.map! {|e| e.class} )
     assert_equal(@cls[ Fixnum, String, Range], a)
    
     a = @cls[ 1, 'cat', 1..1 ]
     assert_equal(@cls[ 99, 99, 99], a.map! { 99 } )
     assert_equal(@cls[ 99, 99, 99], a)
 
     a = @cls[ ]
     assert_equal(@cls[], a.map! { 99 })
     assert_equal(@cls[], a)
   end
 
   def test_nitems
     assert_equal(0, @cls[].nitems)
     assert_equal(1, @cls[1].nitems)
     assert_equal(1, @cls[1, nil].nitems)
     assert_equal(1, @cls[nil, 1].nitems)
     assert_equal(3, @cls[1, nil, nil, 2, nil, 3, nil].nitems)
   end
 
   def test_pack
     a = @cls[*%w( cat wombat x yy)]
     assert_equal("catwomx  yy ", a.pack("A3A3A3A3"))
     assert_equal("cat", a.pack("A*"))
     assert_equal("cwx  yy ", a.pack("A3@1A3@2A3A3"))
     assert_equal("catwomx\000\000yy\000", a.pack("a3a3a3a3"))
     assert_equal("cat", a.pack("a*"))
     assert_equal("ca", a.pack("a2"))
     assert_equal("cat\000\000", a.pack("a5"))
 
     assert_equal("\x61",     @cls["01100001"].pack("B8"))
     assert_equal("\x61",     @cls["01100001"].pack("B*"))
     assert_equal("\x61",     @cls["0110000100110111"].pack("B8"))
     assert_equal("\x61\x37", @cls["0110000100110111"].pack("B16"))
     assert_equal("\x61\x37", @cls["01100001", "00110111"].pack("B8B8"))
     assert_equal("\x60",     @cls["01100001"].pack("B4"))
     assert_equal("\x40",     @cls["01100001"].pack("B2"))
 
     assert_equal("\x86",     @cls["01100001"].pack("b8"))
     assert_equal("\x86",     @cls["01100001"].pack("b*"))
     assert_equal("\x86",     @cls["0110000100110111"].pack("b8"))
     assert_equal("\x86\xec", @cls["0110000100110111"].pack("b16"))
     assert_equal("\x86\xec", @cls["01100001", "00110111"].pack("b8b8"))
     assert_equal("\x06",     @cls["01100001"].pack("b4"))
     assert_equal("\x02",     @cls["01100001"].pack("b2"))
 
     assert_equal("ABC",      @cls[ 65, 66, 67 ].pack("C3"))
     assert_equal("\377BC",   @cls[ -1, 66, 67 ].pack("C*"))
     assert_equal("ABC",      @cls[ 65, 66, 67 ].pack("c3"))
     assert_equal("\377BC",   @cls[ -1, 66, 67 ].pack("c*"))
 
     
     assert_equal("AB\n\x10",  @cls["4142", "0a", "12"].pack("H4H2H1"))
     assert_equal("AB\n\x02",  @cls["1424", "a0", "21"].pack("h4h2h1"))
 
     assert_equal("abc=02def=\ncat=\n=01=\n", 
                  @cls["abc\002def", "cat", "\001"].pack("M9M3M4"))
 
     assert_equal("aGVsbG8K\n",  @cls["hello\n"].pack("m"))
     assert_equal(",:&5L;&\\*:&5L;&\\*\n",  @cls["hello\nhello\n"].pack("u"))
 
     assert_equal("\xc2\xa9B\xe2\x89\xa0", @cls[0xa9, 0x42, 0x2260].pack("U*"))
 
 
     format = "c2x5CCxsdils_l_a6";
     # Need the expression in here to force ary[5] to be numeric.  This avoids
     # test2 failing because ary2 goes str->numeric->str and ary does not.
     ary = [1, -100, 127, 128, 32767, 987.654321098/100.0,
       12345, 123456, -32767, -123456, "abcdef"]
     x    = ary.pack(format)
     ary2 = x.unpack(format)
 
     assert_equal(ary.length, ary2.length)
     assert_equal(ary.join(':'), ary2.join(':'))
     assert_not_nil(x =~ /def/)
 
 =begin
     skipping "Not tested:
         D,d & double-precision float, native format\\
         E & double-precision float, little-endian byte order\\
         e & single-precision float, little-endian byte order\\
         F,f & single-precision float, native format\\
         G & double-precision float, network (big-endian) byte order\\
         g & single-precision float, network (big-endian) byte order\\
         I & unsigned integer\\
         i & integer\\
         L & unsigned long\\
         l & long\\
 
         N & long, network (big-endian) byte order\\
         n & short, network (big-endian) byte-order\\
         P & pointer to a structure (fixed-length string)\\
         p & pointer to a null-terminated string\\
         S & unsigned short\\
         s & short\\
         V & long, little-endian byte order\\
         v & short, little-endian byte order\\
         X & back up a byte\\
         x & null byte\\
         Z & ASCII string (null padded, count is width)\\
 "
 =end
   end
 
   def test_pop
     a = @cls[ 'cat', 'dog' ]
     assert_equal('dog', a.pop)
     assert_equal(@cls['cat'], a)
     assert_equal('cat', a.pop)
     assert_equal(@cls[], a)
     assert_nil(a.pop)
     assert_equal(@cls[], a)
   end
 
   def test_push
     a = @cls[1, 2, 3]
     assert_equal(@cls[1, 2, 3, 4, 5], a.push(4, 5))
     assert_equal(@cls[1, 2, 3, 4, 5, nil], a.push(nil))
     # Ruby 1.8 feature:
     # Array#push accepts any number of arguments.
     #assert_raise(ArgumentError, "a.push()") { a.push() }
     a.push
     assert_equal @cls[1, 2, 3, 4, 5, nil], a
     a.push 6, 7
     assert_equal @cls[1, 2, 3, 4, 5, nil, 6, 7], a
   end
 
   def test_rassoc
     a1 = @cls[*%w( cat  feline )]
     a2 = @cls[*%w( dog  canine )]
     a3 = @cls[*%w( mule asinine )]
     a  = @cls[ a1, a2, a3 ]
 
     assert_equal(a1,  a.rassoc('feline'))
     assert_equal(a3,  a.rassoc('asinine'))
     assert_equal(nil, a.rassoc('dog'))
     assert_equal(nil, a.rassoc('mule'))
     assert_equal(nil, a.rassoc(1..2))
   end
 
   # also delete_if
   def test_reject!
     a = @cls[ 1, 2, 3, 4, 5 ]
     assert_equal(nil, a.reject! { false })
     assert_equal(@cls[1, 2, 3, 4, 5], a)
 
     a = @cls[ 1, 2, 3, 4, 5 ]
     assert_equal(a, a.reject! { true })
     assert_equal(@cls[], a)
 
     a = @cls[ 1, 2, 3, 4, 5 ]
     assert_equal(a, a.reject! { |i| i > 3 })
     assert_equal(@cls[1, 2, 3], a)
   end
 
   def test_replace
     a = @cls[ 1, 2, 3]
     a_id = a.__id__
     assert_equal(@cls[4, 5, 6], a.replace(@cls[4, 5, 6]))
     assert_equal(@cls[4, 5, 6], a)
     assert_equal(a_id, a.__id__)
     assert_equal(@cls[], a.replace(@cls[]))
   end
 
   def test_reverse
     a = @cls[*%w( dog cat bee ant )]
     assert_equal(@cls[*%w(ant bee cat dog)], a.reverse)
     assert_equal(@cls[*%w(dog cat bee ant)], a)
     assert_equal(@cls[], @cls[].reverse)
   end
 
   def test_reverse!
     a = @cls[*%w( dog cat bee ant )]
     assert_equal(@cls[*%w(ant bee cat dog)], a.reverse!)
     assert_equal(@cls[*%w(ant bee cat dog)], a)
     # Ruby 1.8 feature change:
     # Array#reverse always returns self.
     #assert_nil(@cls[].reverse!)
     assert_equal @cls[], @cls[].reverse!
   end
 
   def test_reverse_each
     a = @cls[*%w( dog cat bee ant )]
     i = a.length
     a.reverse_each { |e|
       i -= 1
       assert_equal(a[i], e)
     }
     assert_equal(0, i)
 
     a = @cls[]
     i = 0
     a.reverse_each { |e|
       assert(false, "Never get here")
     }
     assert_equal(0, i)
   end
 
   def test_rindex
     a = @cls[ 'cat', 99, /a/, 99, [ 1, 2, 3] ]
     assert_equal(0, a.rindex('cat'))
     assert_equal(3, a.rindex(99))
     assert_equal(4, a.rindex([1,2,3]))
     assert_nil(a.rindex('ca'))
     assert_nil(a.rindex([1,2]))
   end
 
   def test_shift
     a = @cls[ 'cat', 'dog' ]
     assert_equal('cat', a.shift)
     assert_equal(@cls['dog'], a)
     assert_equal('dog', a.shift)
     assert_equal(@cls[], a)
     assert_nil(a.shift)
     assert_equal(@cls[], a)
   end
 
   def test_size
     assert_equal(0,   @cls[].size)
     assert_equal(1,   @cls[1].size)
     assert_equal(100, @cls[*(0..99).to_a].size)
   end
 
   def test_slice
     a = @cls[*(1..100).to_a]
 
     assert_equal(1, a.slice(0))
     assert_equal(100, a.slice(99))
     assert_nil(a.slice(100))
     assert_equal(100, a.slice(-1))
     assert_equal(99,  a.slice(-2))
     assert_equal(1,   a.slice(-100))
     assert_nil(a.slice(-101))
 
     assert_equal(@cls[1],   a.slice(0,1))
     assert_equal(@cls[100], a.slice(99,1))
     assert_equal(@cls[],    a.slice(100,1))
     assert_equal(@cls[100], a.slice(99,100))
     assert_equal(@cls[100], a.slice(-1,1))
     assert_equal(@cls[99],  a.slice(-2,1))
 
     assert_equal(@cls[10, 11, 12], a.slice(9, 3))
     assert_equal(@cls[10, 11, 12], a.slice(-91, 3))
 
     assert_nil(a.slice(-101, 2))
 
     assert_equal(@cls[1],   a.slice(0..0))
     assert_equal(@cls[100], a.slice(99..99))
     assert_equal(@cls[],    a.slice(100..100))
     assert_equal(@cls[100], a.slice(99..200))
     assert_equal(@cls[100], a.slice(-1..-1))
     assert_equal(@cls[99],  a.slice(-2..-2))
 
     assert_equal(@cls[10, 11, 12], a.slice(9..11))
     assert_equal(@cls[10, 11, 12], a.slice(-91..-89))
     
     assert_nil(a.slice(-101..-1))
 
     assert_nil(a.slice(10, -3))
     # Ruby 1.8 feature change:
     # Array#slice[size..x] always returns [].
     #assert_nil(a.slice(10..7))
     assert_equal @cls[], a.slice(10..7)
   end
 
   def test_slice!
     a = @cls[1, 2, 3, 4, 5]
     assert_equal(3, a.slice!(2))
     assert_equal(@cls[1, 2, 4, 5], a)
 
     a = @cls[1, 2, 3, 4, 5]
     assert_equal(4, a.slice!(-2))
     assert_equal(@cls[1, 2, 3, 5], a)
 
     a = @cls[1, 2, 3, 4, 5]
     assert_equal(@cls[3,4], a.slice!(2,2))
     assert_equal(@cls[1, 2, 5], a)
 
     a = @cls[1, 2, 3, 4, 5]
     assert_equal(@cls[4,5], a.slice!(-2,2))
     assert_equal(@cls[1, 2, 3], a)
 
     a = @cls[1, 2, 3, 4, 5]
     assert_equal(@cls[3,4], a.slice!(2..3))
     assert_equal(@cls[1, 2, 5], a)
 
     a = @cls[1, 2, 3, 4, 5]
     assert_equal(nil, a.slice!(20))
     assert_equal(@cls[1, 2, 3, 4, 5], a)
 
     a = @cls[1, 2, 3, 4, 5]
     assert_equal(nil, a.slice!(-6))
     assert_equal(@cls[1, 2, 3, 4, 5], a)
 
-    # JRUBY-4181
-    # a = @cls[1, 2, 3, 4, 5]
-    # assert_equal(nil, a.slice!(-6..4))
-    # assert_equal(@cls[1, 2, 3, 4, 5], a)
+    a = @cls[1, 2, 3, 4, 5]
+    assert_equal(nil, a.slice!(-6..4))
+    assert_equal(@cls[1, 2, 3, 4, 5], a)
 
     a = @cls[1, 2, 3, 4, 5]
     assert_equal(nil, a.slice!(-6,2))
     assert_equal(@cls[1, 2, 3, 4, 5], a)
   end
 
   def test_sort
     a = @cls[ 4, 1, 2, 3 ]
     assert_equal(@cls[1, 2, 3, 4], a.sort)
     assert_equal(@cls[4, 1, 2, 3], a)
 
     assert_equal(@cls[4, 3, 2, 1], a.sort { |x, y| y <=> x} )
     assert_equal(@cls[4, 1, 2, 3], a)
 
     a.fill(1)
     assert_equal(@cls[1, 1, 1, 1], a.sort)
     
     assert_equal(@cls[], @cls[].sort)
   end
 
   def test_sort!
     a = @cls[ 4, 1, 2, 3 ]
     assert_equal(@cls[1, 2, 3, 4], a.sort!)
     assert_equal(@cls[1, 2, 3, 4], a)
 
     assert_equal(@cls[4, 3, 2, 1], a.sort! { |x, y| y <=> x} )
     assert_equal(@cls[4, 3, 2, 1], a)
 
     a.fill(1)
     assert_equal(@cls[1, 1, 1, 1], a.sort!)
 
     assert_equal(@cls[1], @cls[1].sort!)
     assert_equal(@cls[], @cls[].sort!)
   end
 
   def test_to_a
     a = @cls[ 1, 2, 3 ]
     a_id = a.__id__
     assert_equal(a, a.to_a)
     assert_equal(a_id, a.to_a.__id__)
   end
 
   def test_to_ary
     a = [ 1, 2, 3 ]
     b = @cls[*a]
 
     a_id = a.__id__
     assert_equal(a, b.to_ary)
     if (@cls == Array)
       assert_equal(a_id, a.to_ary.__id__)
     end
   end
 
   def test_to_s
     $, = ""
     a = @cls[]
     assert_equal("", a.to_s)
 
     $, = ""
     a = @cls[1, 2]
     assert_equal("12", a.to_s)
 
     $, = ""
     a = @cls[1, 2, 3]
     assert_equal("123", a.to_s)
 
     $, = ":"
     a = @cls[1, 2, 3]
     assert_equal("1:2:3", a.to_s)
 
     $, = ""
   end
 
   def test_uniq
     a = @cls[ 1, 2, 3, 2, 1, 2, 3, 4, nil ]
     b = a.dup
     assert_equal(@cls[1, 2, 3, 4, nil], a.uniq)
     assert_equal(b, a)
 
     assert_equal(@cls[1, 2, 3], @cls[1, 2, 3].uniq)
   end
 
   def test_uniq!
     a = @cls[ 1, 2, 3, 2, 1, 2, 3, 4, nil ]
     assert_equal(@cls[1, 2, 3, 4, nil], a.uniq!)
     assert_equal(@cls[1, 2, 3, 4, nil], a)
 
     assert_nil(@cls[1, 2, 3].uniq!)
   end
 
   def test_unshift
     a = @cls[]
     assert_equal(@cls['cat'], a.unshift('cat'))
     assert_equal(@cls['dog', 'cat'], a.unshift('dog'))
     assert_equal(@cls[nil, 'dog', 'cat'], a.unshift(nil))
     assert_equal(@cls[@cls[1,2], nil, 'dog', 'cat'], a.unshift(@cls[1, 2]))
   end
 
   def test_OR # '|'
     assert_equal(@cls[],  @cls[]  | @cls[])
     assert_equal(@cls[1], @cls[1] | @cls[])
     assert_equal(@cls[1], @cls[]  | @cls[1])
     assert_equal(@cls[1], @cls[1] | @cls[1])
 
     assert_equal(@cls[1,2], @cls[1] | @cls[2])
     assert_equal(@cls[1,2], @cls[1, 1] | @cls[2, 2])
     assert_equal(@cls[1,2], @cls[1, 2] | @cls[1, 2])
   end
 
  def test_combination
     assert_equal(@cls[[]], @cls[1,2,3,4].combination(0).to_a)
     assert_equal(@cls[[1],[2],[3],[4]], @cls[1,2,3,4].combination(1).to_a)
     assert_equal(@cls[[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]], @cls[1,2,3,4].combination(2).to_a)
     assert_equal(@cls[[1,2,3],[1,2,4],[1,3,4],[2,3,4]], @cls[1,2,3,4].combination(3).to_a)
     assert_equal(@cls[[1,2,3,4]], @cls[1,2,3,4].combination(4).to_a)
     assert_equal(@cls[], @cls[1,2,3,4].combination(5).to_a)
   end
 
   def test_product
     assert_equal(@cls[[1,4],[1,5],[2,4],[2,5],[3,4],[3,5]],
                  @cls[1,2,3].product([4,5]))
     assert_equal(@cls[[1,1],[1,2],[2,1],[2,2]], @cls[1,2].product([1,2]))
 
     assert_equal(@cls[[1,3,5],[1,3,6],[1,4,5],[1,4,6],
                    [2,3,5],[2,3,6],[2,4,5],[2,4,6]], 
                  @cls[1,2].product([3,4],[5,6]))
     assert_equal(@cls[[1],[2]], @cls[1,2].product)
     assert_equal(@cls[], @cls[1,2].product([]))
   end
 
   def test_permutation
     a = @cls[1,2,3]
     assert_equal(@cls[[]], a.permutation(0).to_a)
     assert_equal(@cls[[1],[2],[3]], a.permutation(1).to_a.sort)
     assert_equal(@cls[[1,2],[1,3],[2,1],[2,3],[3,1],[3,2]],
                  a.permutation(2).to_a.sort)
     assert_equal(@cls[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]],
                  a.permutation(3).sort.to_a)
     assert_equal(@cls[], a.permutation(4).to_a)
     assert_equal(@cls[], a.permutation(-1).to_a)
     assert_equal("abcde".each_char.to_a.permutation(5).sort,
                  "edcba".each_char.to_a.permutation(5).sort)
     assert_equal(@cls[].permutation(0).to_a, @cls[[]])
 
   end
 
   def test_take
     assert_equal([1,2,3], [1,2,3,4,5,0].take(3))
     assert_raise(ArgumentError, '[ruby-dev:34123]') { [1,2].take(-1) }
     assert_equal([1,2], [1,2].take(1000000000), '[ruby-dev:34123]')
   end
 
   def test_take_while
     assert_equal([1,2], [1,2,3,4,5,0].take_while {|i| i < 3 })
   end
 
   def test_drop
     assert_equal([4,5,0], [1,2,3,4,5,0].drop(3))
     assert_raise(ArgumentError, '[ruby-dev:34123]') { [1,2].drop(-1) }
     assert_equal([], [1,2].drop(1000000000), '[ruby-dev:34123]')
   end
 
   def test_drop_while
     assert_equal([3,4,5,0], [1,2,3,4,5,0].drop_while {|i| i < 3 })
   end
 end
