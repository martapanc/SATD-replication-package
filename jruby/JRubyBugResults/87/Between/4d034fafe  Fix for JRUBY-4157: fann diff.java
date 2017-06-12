diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index 993e76e33e..f2996fcced 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1498,2001 +1498,2001 @@ public class RubyArray extends RubyObject implements List {
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
         RubyArray ary2 = obj.convertToArray();
 
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
 
     private IRubyObject slice_internal(long pos, long len, IRubyObject arg0, IRubyObject arg1) {
         Ruby runtime = getRuntime();
 
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
 
-        arg1 = makeShared((int)pos, (int)len, getMetaClass());
+        arg1 = makeShared(begin + (int)pos, (int)len, getMetaClass());
         splice(pos, len, null);
 
         return arg1;
     }
 
     /** rb_ary_slice_bang
      *
      */
     @JRubyMethod(name = "slice!")
     public IRubyObject slice_bang(IRubyObject arg0) {
         modifyCheck();
         if (arg0 instanceof RubyRange) {
             long[] beglen = ((RubyRange) arg0).begLen(realLength, 1);
             long pos = beglen[0];
             long len = beglen[1];
             return slice_internal(pos, len, arg0, null);
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
         return slice_internal(pos, len, arg0, arg1);
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
         if (level == 0) return this;
 
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
     @JRubyMethod(name = "product", rest = true, compat = CompatVersion.RUBY1_9)
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
                 block.yield(context, values[begin + i]);
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
diff --git a/test/test_array.rb b/test/test_array.rb
index 019b2172eb..9765608a0f 100644
--- a/test/test_array.rb
+++ b/test/test_array.rb
@@ -1,21 +1,26 @@
 require 'test/unit'
 
 ## NOTE: Most of the tests that were here have been moved to the RubySpec.
 
 class TestArray < Test::Unit::TestCase
   ##### splat test #####
   class ATest
     def to_a; 1; end
   end
 
   def test_splatting
     proc { |a| assert_equal(1, a) }.call(*1)
     assert_raises(TypeError) { proc { |a| }.call(*ATest.new) }
   end
 
   def test_initialize_on_frozen_array
     assert_raises(TypeError) {  
       [1, 2, 3].freeze.instance_eval { initialize }
     }
   end
+
+  # JRUBY-4157
+  def test_shared_ary_slice
+    assert_equal [4,5,6], [1,2,3,4,5,6].slice(1,5).slice!(2,3)
+  end
 end
