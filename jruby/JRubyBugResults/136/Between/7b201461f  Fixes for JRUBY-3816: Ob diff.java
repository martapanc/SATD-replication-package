diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index eca7893366..e192d0bdc5 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -2411,1194 +2411,1194 @@ public class RubyArray extends RubyObject implements List {
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
 
         arg1 = makeShared((int)pos, (int)len, getMetaClass());
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
 
     /** flatten
      * 
      */
     private final int flatten(ThreadContext context, int index, RubyArray ary2, RubyArray memo) {
         int i = index;
         int n;
         int lim = index + ary2.realLength;
 
         IRubyObject id = ary2.id();
 
         if (memo.includes(context, id)) throw getRuntime().newArgumentError("tried to flatten recursive array");
 
         memo.append(id);
         splice(index, 1, ary2);
         while (i < lim) {
             IRubyObject tmp = elt(i).checkArrayType();
             if (!tmp.isNil()) {
                 n = flatten(context, i, (RubyArray) tmp, memo);
                 i += n;
                 lim += n;
             }
             i++;
         }
         memo.pop(context);
         return lim - index - 1; /* returns number of increased items */
     }
 
     /** rb_ary_flatten_bang
      *
      */
     public IRubyObject flatten_bang(ThreadContext context) {
         int i = 0;
         RubyArray memo = null;
 
         while (i < realLength) {
             IRubyObject ary2 = values[begin + i];
             IRubyObject tmp = ary2.checkArrayType();
             if (!tmp.isNil()) {
                 if (memo == null) {
                     memo = new RubyArray(context.getRuntime(), ARRAY_DEFAULT_SIZE, false); // doesn't escape, don't fill with nils
                 }
 
                 i += flatten(context, i, (RubyArray) tmp, memo);
             }
             i++;
         }
         if (memo == null) return context.getRuntime().getNil();
 
         return this;
     }
 
     /** rb_ary_flatten
     *
     */
     public IRubyObject flatten(ThreadContext context) {
         RubyArray ary = aryDup();
         ary.flatten_bang(context);
         return ary;
     }
 
     private boolean flatten19(ThreadContext context, int level, RubyArray result) {
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
 
     @JRubyMethod(name = "flatten!")
     public IRubyObject flatten_bang19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         if (flatten19(context, -1, result)) {
             modify();
             begin = 0;
             realLength = result.realLength;
             values = result.values;
             return this;
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "flatten!")
     public IRubyObject flatten_bang19(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         int level = RubyNumeric.num2int(arg);
         if (level == 0) return this;
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         if (flatten19(context, level, result)) {
             begin = 0;
             realLength = result.realLength;
             values = result.values;
             return this;
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "flatten")
     public IRubyObject flatten19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten19(context, -1, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "flatten")
     public IRubyObject flatten19(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         int level = RubyNumeric.num2int(arg);
         if (level == 0) return this;
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten19(context, level, result);
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
             flags |= TMPLOCK_ARR_F;
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
 
     /** rb_ary_choice
      * 
      */
     @JRubyMethod(name = "choice")
     public IRubyObject choice(ThreadContext context) {
         Random random = context.getRuntime().getRandom();
         int i = realLength;
         if(i == 0) {
             return context.getRuntime().getNil();
         }
         int r = random.nextInt(i);
         return values[begin + r];
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
             array[i - begin] = JavaUtil.convertRubyToJava(values[i]);
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
             array[i] = JavaUtil.convertRubyToJava(values[i + begin]);
         }
         return array;
     }
 
     public boolean add(Object element) {
-        append(JavaUtil.convertJavaToRuby(getRuntime(), element));
+        append(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element));
         return true;
     }
 
     public boolean remove(Object element) {
-        IRubyObject deleted = delete(getRuntime().getCurrentContext(), JavaUtil.convertJavaToRuby(getRuntime(), element), Block.NULL_BLOCK);
+        IRubyObject deleted = delete(getRuntime().getCurrentContext(), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element), Block.NULL_BLOCK);
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
         return JavaUtil.convertRubyToJava((IRubyObject) elt(index), Object.class);
     }
 
     public Object set(int index, Object element) {
-        return store(index, JavaUtil.convertJavaToRuby(getRuntime(), element));
+        return store(index, JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element));
     }
 
     // TODO: make more efficient by not creating IRubyArray[]
     public void add(int index, Object element) {
-        insert(new IRubyObject[]{RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToRuby(getRuntime(), element)});
+        insert(new IRubyObject[]{RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element)});
     }
 
     public Object remove(int index) {
         return JavaUtil.convertRubyToJava(delete_at(index), Object.class);
     }
 
     public int indexOf(Object element) {
         int begin = this.begin;
 
         if (element != null) {
-            IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
+            IRubyObject convertedElement = JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element);
 
             for (int i = begin; i < begin + realLength; i++) {
                 if (convertedElement.equals(values[i])) {
                     return i;
                 }
             }
         }
         return -1;
     }
 
     public int lastIndexOf(Object element) {
         int begin = this.begin;
 
         if (element != null) {
-            IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
+            IRubyObject convertedElement = JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element);
 
             for (int i = begin + realLength - 1; i >= begin; i--) {
                 if (convertedElement.equals(values[i])) {
                     return i;
                 }
             }
         }
 
         return -1;
     }
 
     public class RubyArrayConversionIterator implements Iterator {
         protected int index = 0;
         protected int last = -1;
 
         public boolean hasNext() {
             return index < realLength;
         }
 
         public Object next() {
             IRubyObject element = elt(index);
             last = index++;
             return JavaUtil.convertRubyToJava(element, Object.class);
         }
 
         public void remove() {
             if (last == -1) throw new IllegalStateException();
 
             delete_at(last);
             if (last < index) index--;
 
             last = -1;
 	
         }
     }
 
     public Iterator iterator() {
         return new RubyArrayConversionIterator();
     }
 
     final class RubyArrayConversionListIterator extends RubyArrayConversionIterator implements ListIterator {
         public RubyArrayConversionListIterator() {
         }
 
         public RubyArrayConversionListIterator(int index) {
             this.index = index;
 		}
 
 		public boolean hasPrevious() {
             return index >= 0;
 		}
 
 		public Object previous() {
             return JavaUtil.convertRubyToJava((IRubyObject) elt(last = --index), Object.class);
 		}
 
 		public int nextIndex() {
             return index;
 		}
 
 		public int previousIndex() {
             return index - 1;
 		}
 
         public void set(Object obj) {
             if (last == -1) throw new IllegalStateException();
 
-            store(last, JavaUtil.convertJavaToRuby(getRuntime(), obj));
+            store(last, JavaUtil.convertJavaToUsableRubyObject(getRuntime(), obj));
         }
 
         public void add(Object obj) {
-            insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index++), JavaUtil.convertJavaToRuby(getRuntime(), obj) });
+            insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index++), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), obj) });
             last = -1;
         }
     }
 
     public ListIterator listIterator() {
         return new RubyArrayConversionListIterator();
     }
 
     public ListIterator listIterator(int index) {
         return new RubyArrayConversionListIterator(index);
 	}
 
     // TODO: list.subList(from, to).clear() is supposed to clear the sublist from the list.
     // How can we support this operation?
     public List subList(int fromIndex, int toIndex) {
         if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
             throw new IndexOutOfBoundsException();
         }
         
         IRubyObject subList = subseq(fromIndex, toIndex - fromIndex + 1);
 
         return subList.isNil() ? null : (List) subList;
     }
 
     public void clear() {
         rb_clear();
     }
 }
diff --git a/src/org/jruby/RubyHash.java b/src/org/jruby/RubyHash.java
index 1ce93fe9f1..c7b4f0654a 100644
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
@@ -582,1307 +582,1307 @@ public class RubyHash extends RubyObject implements Map {
      *  ================
      */
 
     /** rb_hash_initialize
      *
      */
     @JRubyMethod(name = "initialize", optional = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, final Block block) {
         modify();
 
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError("wrong number of arguments");
             ifNone = getRuntime().newProc(Block.Type.PROC, block);
             flags |= PROCDEFAULT_HASH_F;
         } else {
             Arity.checkArgumentCount(getRuntime(), args, 0, 1);
             if (args.length == 1) ifNone = args[0];
         }
         return this;
     }
 
     /** rb_hash_default
      *
      */
     @Deprecated
     public IRubyObject default_value_get(ThreadContext context, IRubyObject[] args) {
         switch (args.length) {
             case 0: return default_value_get(context);
             case 1: return default_value_get(context, args[0]);
             default: throw context.getRuntime().newArgumentError(args.length, 1);
         }
     }
     @JRubyMethod(name = "default", frame = true)
     public IRubyObject default_value_get(ThreadContext context) {
         if ((flags & PROCDEFAULT_HASH_F) != 0) {
             return getRuntime().getNil();
         }
         return ifNone;
     }
     @JRubyMethod(name = "default", frame = true)
     public IRubyObject default_value_get(ThreadContext context, IRubyObject arg) {
         if ((flags & PROCDEFAULT_HASH_F) != 0) {
             return RuntimeHelpers.invoke(context, ifNone, "call", this, arg);
         }
         return ifNone;
     }
 
     /** rb_hash_set_default
      *
      */
     @JRubyMethod(name = "default=", required = 1)
     public IRubyObject default_value_set(final IRubyObject defaultValue) {
         modify();
 
         ifNone = defaultValue;
         flags &= ~PROCDEFAULT_HASH_F;
 
         return ifNone;
     }
 
     /** rb_hash_default_proc
      *
      */
     @JRubyMethod(name = "default_proc", frame = true)
     public IRubyObject default_proc() {
         return (flags & PROCDEFAULT_HASH_F) != 0 ? ifNone : getRuntime().getNil();
     }
 
     /** rb_hash_modify
      *
      */
     public void modify() {
     	testFrozen("hash");
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify hash");
         }
     }
 
     /** inspect_hash
      *
      */
     private IRubyObject inspectHash(final ThreadContext context) {
         final ByteList buffer = new ByteList();
         buffer.append('{');
         final boolean[] firstEntry = new boolean[1];
 
         firstEntry[0] = true;
         visitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 if (!firstEntry[0]) buffer.append(',').append(' ');
 
                 buffer.append(inspect(context, key).getByteList());
                 buffer.append('=').append('>');
                 buffer.append(inspect(context, value).getByteList());
                 firstEntry[0] = false;
             }
         });
         buffer.append('}');
         return getRuntime().newString(buffer);
     }
 
     /** rb_hash_inspect
      *
      */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect(ThreadContext context) {
         if (size == 0) return getRuntime().newString("{}");
         if (getRuntime().isInspecting(this)) return getRuntime().newString("{...}");
 
         try {
             getRuntime().registerInspecting(this);
             return inspectHash(context);
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_hash_size
      *
      */
     @JRubyMethod(name = {"size", "length"})
     public RubyFixnum rb_size() {
         return getRuntime().newFixnum(size);
     }
 
     /** rb_hash_empty_p
      *
      */
     @JRubyMethod(name = "empty?")
     public RubyBoolean empty_p() {
         return size == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_hash_to_a
      *
      */
     @JRubyMethod(name = "to_a")
     @Override
     public RubyArray to_a() {
         final Ruby runtime = getRuntime();
         final RubyArray result = RubyArray.newArray(runtime, size);
 
         visitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 result.append(RubyArray.newArray(runtime, key, value));
             }
         });
 
         result.setTaint(isTaint());
         return result;
     }
 
     /** rb_hash_to_s & to_s_hash
      *
      */
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (runtime.isInspecting(this)) return runtime.newString("{...}");
         try {
             runtime.registerInspecting(this);
             return to_a().to_s();
         } finally {
             runtime.unregisterInspecting(this);
         }
     }
 
     @JRubyMethod(name = "to_s", compat = CompatVersion.RUBY1_9)
     public IRubyObject to_s19(ThreadContext context) {
         return inspect(context);
     }
 
     /** rb_hash_rehash
      *
      */
     @JRubyMethod(name = "rehash")
     public RubyHash rehash() {
         if (iteratorCount.get() > 0) {
             throw getRuntime().newRuntimeError("rehash during iteration");
         }
 
         modify();
         final RubyHashEntry[] oldTable = table;
         final RubyHashEntry[] newTable = new RubyHashEntry[oldTable.length];
         for (int j = 0; j < oldTable.length; j++) {
             RubyHashEntry entry = oldTable[j];
             oldTable[j] = null;
             while (entry != null) {
                 RubyHashEntry next = entry.next;
                 entry.hash = entry.key.hashCode(); // update the hash value
                 int i = bucketIndex(entry.hash, newTable.length);
                 entry.next = newTable[i];
                 newTable[i] = entry;
                 entry = next;
             }
         }
         table = newTable;
         return this;
     }
 
     /** rb_hash_to_hash
      *
      */
     @JRubyMethod(name = "to_hash")
     public RubyHash to_hash() {
         return this;
     }
 
     @Override
     public RubyHash convertToHash() {
         return this;
     }
 
     public final void fastASet(IRubyObject key, IRubyObject value) {
         internalPut(key, value);
     }
 
     public final RubyHash fastASetChained(IRubyObject key, IRubyObject value) {
         internalPut(key, value);
         return this;
     }
 
     @Deprecated
     public IRubyObject op_aset(IRubyObject key, IRubyObject value) {
         return op_aset(getRuntime().getCurrentContext(), key, value);
     }
 
     /** rb_hash_aset
      *
      */
     @JRubyMethod(name = {"[]=", "store"}, required = 2, compat = CompatVersion.RUBY1_8)
     public IRubyObject op_aset(ThreadContext context, IRubyObject key, IRubyObject value) {
         modify();
 
         if (key instanceof RubyString) {
             op_asetForString(context.getRuntime(), (RubyString)key, value);
         } else {
             internalPut(key, value);
         }
 
         return value;
     }
 
     @JRubyMethod(name = {"[]=", "store"}, required = 2, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_aset19(ThreadContext context, IRubyObject key, IRubyObject value) {
         modify();
 
         Ruby runtime = context.getRuntime();
         if (key.getMetaClass().getRealClass() == runtime.getString()) {
             op_asetForString(runtime, (RubyString)key, value);
         } else {
             internalPut(key, value);
         }
 
         return value;
     }
 
     private void op_asetForString(Ruby runtime, RubyString key, IRubyObject value) {
         final RubyHashEntry entry = internalGetEntry(key);
         if (entry != NO_ENTRY) {
             entry.value = value;
         } else {
             if (!key.isFrozen()) {
                 key = key.strDup(runtime, key.getMetaClass().getRealClass());
                 key.setFrozen(true);
             }
             internalPut(key, value, false);
         }
     }
 
     /**
      * Note: this is included as a compatibility measure for AR-JDBC
      * @deprecated use RubyHash.op_aset instead
      */
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         return op_aset(getRuntime().getCurrentContext(), key, value);
     }
 
     /**
      * Note: this is included as a compatibility measure for Mongrel+JRuby
      * @deprecated use RubyHash.op_aref instead
      */
     public IRubyObject aref(IRubyObject key) {
         return op_aref(getRuntime().getCurrentContext(), key);
     }
 
     public final IRubyObject fastARef(IRubyObject key) { // retuns null when not found to avoid unnecessary getRuntime().getNil() call
         return internalGet(key);
     }
 
     public RubyBoolean compare(final ThreadContext context, final String method, IRubyObject other, final Set<RecursiveComparator.Pair> seen) {
 
         Ruby runtime = context.getRuntime();
 
         if (!(other instanceof RubyHash)) {
             return runtime.getFalse();
         }
 
         final RubyHash otherHash = (RubyHash) other;
 
         if (this.size != otherHash.size) {
             return runtime.getFalse();
         }
 
         try {
             visitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     IRubyObject value2 = otherHash.fastARef(key);
 
                     if (value2 == null) {
                         // other hash does not contain key
                         throw new Mismatch();
                     }
 
                     if (!RecursiveComparator.compare(context, method, value, value2, seen).isTrue()) {
                         throw new Mismatch();
                     }
                 }
             });
         } catch (Mismatch e) {
             return runtime.getFalse();
         }
         
         return runtime.getTrue();
     }
 
     /** rb_hash_equal
      * 
      */
     @JRubyMethod(name = "==")
     public IRubyObject op_equal19(final ThreadContext context, IRubyObject other) {
         return RecursiveComparator.compare(context, "==", this, other, null);
     }
 
     /** rb_hash_eql
      * 
      */
     @JRubyMethod(name = "eql?")
     public IRubyObject op_eql19(final ThreadContext context, IRubyObject other) {
         return RecursiveComparator.compare(context, "eql?", this, other, null);
     }
 
     /** rb_hash_aref
      *
      */
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject op_aref(ThreadContext context, IRubyObject key) {
         IRubyObject value;
         return ((value = internalGet(key)) == null) ? callMethod(context, "default", key) : value;
     }
 
     /** rb_hash_hash
      * 
      */
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         final Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         if (size == 0 || runtime.isInspecting(this)) return RubyFixnum.zero(runtime);
         final long hash[] = new long[]{size};
         
         try {
             runtime.registerInspecting(this);
             visitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     hash[0] ^= key.callMethod(context, "hash").convertToInteger().getLongValue();
                     hash[0] ^= value.callMethod(context, "hash").convertToInteger().getLongValue();
                 }
             });
         } finally {
             runtime.unregisterInspecting(this);
         }
         return RubyFixnum.newFixnum(runtime, hash[0]);
     }
 
     /** rb_hash_fetch
      *
      */
     @JRubyMethod(name = "fetch", required = 1, optional = 1, frame = true)
     public IRubyObject fetch(ThreadContext context, IRubyObject[] args, Block block) {
         if (args.length == 2 && block.isGiven()) {
             getRuntime().getWarnings().warn(ID.BLOCK_BEATS_DEFAULT_VALUE, "block supersedes default value argument");
         }
 
         IRubyObject value;
         if ((value = internalGet(args[0])) == null) {
             if (block.isGiven()) return block.yield(context, args[0]);
             if (args.length == 1) throw getRuntime().newIndexError("key not found");
             return args[1];
         }
         return value;
     }
 
     /** rb_hash_has_key
      *
      */
     @JRubyMethod(name = {"has_key?", "key?", "include?", "member?"}, required = 1)
     public RubyBoolean has_key_p(IRubyObject key) {
         return internalGetEntry(key) == NO_ENTRY ? getRuntime().getFalse() : getRuntime().getTrue();
     }
 
     private static class Found extends RuntimeException {
         @Override
         public synchronized Throwable fillInStackTrace() {
             return null;
         }
     }
 
     private static final Found FOUND = new Found();
 
     private static class FoundKey extends Found {
         public final IRubyObject key;
         FoundKey(IRubyObject key) {
             super();
             this.key = key;
         }
     }
 
     private static class FoundPair extends FoundKey {
         public final IRubyObject value;
         FoundPair(IRubyObject key, IRubyObject value) {
             super(key);
             this.value = value;
         }
     }
 
     private boolean hasValue(final ThreadContext context, final IRubyObject expected) {
         try {
             visitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     if (equalInternal(context, value, expected)) {
                         throw FOUND;
                     }
                 }
             });
             return false;
         } catch (Found found) {
             return true;
         }
     }
 
     /** rb_hash_has_value
      *
      */
     @JRubyMethod(name = {"has_value?", "value?"}, required = 1)
     public RubyBoolean has_value_p(ThreadContext context, IRubyObject expected) {
         return getRuntime().newBoolean(hasValue(context, expected));
     }
 
     private AtomicInteger iteratorCount = new AtomicInteger(0);
 
     private void iteratorEntry() {
         iteratorCount.incrementAndGet();
     }
 
     private void iteratorExit() {
         iteratorCount.decrementAndGet();
     }
 
     private void iteratorVisitAll(Visitor visitor) {
         try {
             iteratorEntry();
             visitAll(visitor);
         } finally {
             iteratorExit();
         }
     }
 
     /** rb_hash_each
      *
      */
     public RubyHash each(final ThreadContext context, final Block block) {
         if (block.arity() == Arity.TWO_ARGUMENTS) {
             iteratorVisitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     block.yieldSpecific(context, key, value);
                 }
             });
         } else {
             final Ruby runtime = context.getRuntime();
             
             iteratorVisitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     block.yield(context, RubyArray.newArray(runtime, key, value));
                 }
             });
         }
 
         return this;
     }
 
     @JRubyMethod(name = "each", frame = true)
     public IRubyObject each19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each(context, block) : enumeratorize(context.getRuntime(), this, "each");
     }
 
     /** rb_hash_each_pair
      *
      */
     public RubyHash each_pair(final ThreadContext context, final Block block) {
         final Ruby runtime = getRuntime();
 
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 // rb_yield_values(2,...) equivalent
                 block.yield(context, RubyArray.newArray(runtime, key, value), null, null, true);
             }
         });
 
         return this;	
     }
 
     @JRubyMethod(name = "each_pair", frame = true)
     public IRubyObject each_pair19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_pair(context, block) : enumeratorize(context.getRuntime(), this, "each_pair");
     }
 
     /** rb_hash_each_value
      *
      */
     public RubyHash each_value(final ThreadContext context, final Block block) {
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 block.yield(context, value);
             }
         });
 
         return this;
     }
 
     @JRubyMethod(name = "each_value", frame = true)
     public IRubyObject each_value19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_value(context, block) : enumeratorize(context.getRuntime(), this, "each_value");
     }
 
     /** rb_hash_each_key
      *
      */
     public RubyHash each_key(final ThreadContext context, final Block block) {
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 block.yield(context, key);
             }
         });
 
         return this;
     }
 
     @JRubyMethod(name = "each_key", frame = true)
     public IRubyObject each_key19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_key(context, block) : enumeratorize(context.getRuntime(), this, "each_key");
     }
 
     /** rb_hash_sort
      *
      */
     @JRubyMethod(name = "sort", frame = true)
     public IRubyObject sort(ThreadContext context, Block block) {
         return to_a().sort_bang(context, block);
     }
 
     /** rb_hash_index
      *
      */
     @JRubyMethod(name = "index", compat = CompatVersion.RUBY1_8)
     public IRubyObject index(ThreadContext context, IRubyObject expected) {
         IRubyObject key = internalIndex(context, expected);
         return key != null ? key : context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "index", compat = CompatVersion.RUBY1_9)
     public IRubyObject index19(ThreadContext context, IRubyObject expected) {
         context.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Hash#index is deprecated; use Hash#key");
         return key(context, expected);
     }
 
     @JRubyMethod(name = "key", compat = CompatVersion.RUBY1_9)
     public IRubyObject key(ThreadContext context, IRubyObject expected) {
         IRubyObject key = internalIndex(context, expected);
         return key != null ? key : context.getRuntime().getNil();
     }
 
     private IRubyObject internalIndex(final ThreadContext context, final IRubyObject expected) {
         try {
             visitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     if (equalInternal(context, value, expected)) {
                         throw new FoundKey(key);
                     }
                 }
             });
             return null;
         } catch (FoundKey found) {
             return found.key;
         }
     }
 
     /** rb_hash_indexes
      *
      */
     @JRubyMethod(name = {"indexes", "indices"}, rest = true)
     public RubyArray indices(ThreadContext context, IRubyObject[] indices) {
         return values_at(context, indices);
     }
 
     /** rb_hash_keys
      *
      */
     @JRubyMethod(name = "keys")
     public RubyArray keys() {
         final Ruby runtime = getRuntime();
         final RubyArray keys = RubyArray.newArray(runtime, size);
 
         visitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 keys.append(key);
             }
         });
 
         return keys;
     }
 
     /** rb_hash_values
      *
      */
     @JRubyMethod(name = "values")
     public RubyArray rb_values() {
         final RubyArray values = RubyArray.newArray(getRuntime(), size);
 
         visitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 values.append(value);
             }
         });
 
         return values;
     }
 
     /** rb_hash_equal
      *
      */
 
     private static class Mismatch extends RuntimeException {}
 
     /** rb_hash_shift
      *
      */
     @JRubyMethod(name = "shift")
     public IRubyObject shift(ThreadContext context) {
         modify();
 
         RubyHashEntry entry = head.nextAdded;
         if (entry != head) {
             RubyArray result = RubyArray.newArray(getRuntime(), entry.key, entry.value);
             internalDeleteEntry(entry);
             return result;
         }
 
         if ((flags & PROCDEFAULT_HASH_F) != 0) {
             return RuntimeHelpers.invoke(context, ifNone, "call", this, getRuntime().getNil());
         } else {
             return ifNone;
         }
     }
 
     public final boolean fastDelete(IRubyObject key) {
         return internalDelete(key) != NO_ENTRY;
     }
 
     /** rb_hash_delete
      *
      */
     @JRubyMethod(name = "delete", required = 1, frame = true)
     public IRubyObject delete(ThreadContext context, IRubyObject key, Block block) {
         modify();
 
         final RubyHashEntry entry = internalDelete(key);
         if (entry != NO_ENTRY) return entry.value;
 
         if (block.isGiven()) return block.yield(context, key);
         return getRuntime().getNil();
     }
 
     /** rb_hash_select
      *
      */
     @JRubyMethod(name = "select", frame = true)
     public IRubyObject select(final ThreadContext context, final Block block) {
         final Ruby runtime = getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "select");
 
         final RubyArray result = runtime.newArray();
 
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 if (block.yield(context, runtime.newArray(key, value), null, null, true).isTrue()) {
                     result.append(runtime.newArray(key, value));
                 }
             }
         });
 
         return result;
     }
 
     @JRubyMethod(name = "select", frame = true, compat = CompatVersion.RUBY1_9)
     public IRubyObject select19(final ThreadContext context, final Block block) {
         final Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "select");
 
         final RubyHash result = newHash(runtime);
 
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 if (block.yield(context, runtime.newArray(key, value), null, null, true).isTrue()) {
                     result.fastASet(key, value);
                 }
             }
         });
 
         return result;        
     }
 
     /** rb_hash_delete_if
      *
      */
     public RubyHash delete_if(final ThreadContext context, final Block block) {
         modify();
 
         final Ruby runtime = getRuntime();
         final RubyHash self = this;
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 if (block.yield(context, RubyArray.newArray(runtime, key, value), null, null, true).isTrue()) {
                     self.delete(context, key, Block.NULL_BLOCK);
                 }
             }
         });
 
         return this;
     }
 
     @JRubyMethod(name = "delete_if", frame = true)
     public IRubyObject delete_if19(final ThreadContext context, final Block block) {
         return block.isGiven() ? delete_if(context, block) : enumeratorize(context.getRuntime(), this, "delete_if");
     }
 
     /** rb_hash_reject
      *
      */
     public RubyHash reject(ThreadContext context, Block block) {
         return ((RubyHash)dup()).delete_if(context, block);
     }
 
     @JRubyMethod(name = "reject", frame = true)
     public IRubyObject reject19(final ThreadContext context, final Block block) {
         return block.isGiven() ? reject(context, block) : enumeratorize(context.getRuntime(), this, "reject");
     }
 
     /** rb_hash_reject_bang
      *
      */
     public IRubyObject reject_bang(ThreadContext context, Block block) {
         int n = size;
         delete_if(context, block);
         if (n == size) return getRuntime().getNil();
         return this;
     }
 
     @JRubyMethod(name = "reject!", frame = true)
     public IRubyObject reject_bang19(final ThreadContext context, final Block block) {
         return block.isGiven() ? reject_bang(context, block) : enumeratorize(context.getRuntime(), this, "reject!");
     }
 
     /** rb_hash_clear
      *
      */
     @JRubyMethod(name = "clear")
     public RubyHash rb_clear() {
         modify();
 
         if (size > 0) {
             alloc();
             size = 0;
         }
 
         return this;
     }
 
     /** rb_hash_invert
      *
      */
     @JRubyMethod(name = "invert")
     public RubyHash invert(final ThreadContext context) {
         final RubyHash result = newHash(getRuntime());
 
         visitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 result.op_aset(context, value, key);
             }
         });
 
         return result;
     }
 
     /** rb_hash_update
      *
      */
     @JRubyMethod(name = {"merge!", "update"}, required = 1, frame = true)
     public RubyHash merge_bang(final ThreadContext context, final IRubyObject other, final Block block) {
         final RubyHash otherHash = other.convertToHash();
         if (otherHash.empty_p().isTrue()) {
             return this;
         }
 
         modify();
 
         final Ruby runtime = getRuntime();
         final RubyHash self = this;
         otherHash.visitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 if (block.isGiven()) {
                     IRubyObject existing = self.internalGet(key);
                     if (existing != null)
                         value = block.yield(context, RubyArray.newArrayNoCopy(runtime, new IRubyObject[]{key, existing, value}));
                 }
                 self.op_aset(context, key, value);
             }
         });
 
         return this;
     }
 
     /** rb_hash_merge
      *
      */
     @JRubyMethod(name = "merge", required = 1, frame = true)
     public RubyHash merge(ThreadContext context, IRubyObject other, Block block) {
         return ((RubyHash)dup()).merge_bang(context, other, block);
     }
 
     /** rb_hash_replace
      *
      */
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = Visibility.PRIVATE)
     public RubyHash initialize_copy(ThreadContext context, IRubyObject other) {
         return replace(context, other);
     }
 
     /** rb_hash_replace
      *
      */
     @JRubyMethod(name = "replace", required = 1)
     public RubyHash replace(final ThreadContext context, IRubyObject other) {
         final RubyHash otherHash = other.convertToHash();
 
         if (this == otherHash) return this;
 
         rb_clear();
 
         final RubyHash self = this;
         otherHash.visitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 self.op_aset(context, key, value);
             }
         });
 
         ifNone = otherHash.ifNone;
 
         if ((otherHash.flags & PROCDEFAULT_HASH_F) != 0) {
             flags |= PROCDEFAULT_HASH_F;
         } else {
             flags &= ~PROCDEFAULT_HASH_F;
         }
 
         return this;
     }
 
     /** rb_hash_values_at
      *
      */
     @JRubyMethod(name = "values_at", rest = true)
     public RubyArray values_at(ThreadContext context, IRubyObject[] args) {
         RubyArray result = RubyArray.newArray(getRuntime(), args.length);
         for (int i = 0; i < args.length; i++) {
             result.append(op_aref(context, args[i]));
         }
         return result;
     }
 
     @JRubyMethod(name = "assoc", compat = CompatVersion.RUBY1_9)
     public IRubyObject assoc(final ThreadContext context, final IRubyObject obj) {
         try {
             visitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     if (equalInternal(context, obj, key)) {
                         throw new FoundPair(key, value);
                     }
                 }
             });
             return context.getRuntime().getNil();
         } catch (FoundPair found) {
             return context.getRuntime().newArray(found.key, found.value);
         }
     }
 
     @JRubyMethod(name = "rassoc", compat = CompatVersion.RUBY1_9)
     public IRubyObject rassoc(final ThreadContext context, final IRubyObject obj) {
         try {
             visitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     if (equalInternal(context, obj, value)) {
                         throw new FoundPair(key, value);
                     }
                 }
             });
             return context.getRuntime().getNil();
         } catch (FoundPair found) {
             return context.getRuntime().newArray(found.key, found.value);
         }
     }
 
     @JRubyMethod(name = "flatten", compat = CompatVersion.RUBY1_9)
     public IRubyObject flatten(ThreadContext context) {
         RubyArray ary = to_a(); 
         ary.callMethod(context, "flatten!", RubyFixnum.one(context.getRuntime()));
         return ary;
     }
 
     @JRubyMethod(name = "flatten", compat = CompatVersion.RUBY1_9)
     public IRubyObject flatten(ThreadContext context, IRubyObject level) {
         RubyArray ary = to_a();
         ary.callMethod(context, "flatten!", level);
         return ary;
     }
 
     public boolean hasDefaultProc() {
         return (flags & PROCDEFAULT_HASH_F) != 0;
     }
 
     public IRubyObject getIfNone(){
         return ifNone;
     }
 
     private static class VisitorIOException extends RuntimeException {
         VisitorIOException(Throwable cause) {
             super(cause);
         }
     }
 
     // FIXME:  Total hack to get flash in Rails marshalling/unmarshalling in session ok...We need
     // to totally change marshalling to work with overridden core classes.
     public static void marshalTo(final RubyHash hash, final MarshalStream output) throws IOException {
         output.registerLinkTarget(hash);
         output.writeInt(hash.size);
         try {
             hash.visitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     try {
                         output.dumpObject(key);
                         output.dumpObject(value);
                     } catch (IOException e) {
                         throw new VisitorIOException(e);
                     }
                 }
             });
         } catch (VisitorIOException e) {
             throw (IOException)e.getCause();
         }
 
         if (!hash.ifNone.isNil()) output.dumpObject(hash.ifNone);
     }
 
     public static RubyHash unmarshalFrom(UnmarshalStream input, boolean defaultValue) throws IOException {
         RubyHash result = newHash(input.getRuntime());
         input.registerLinkTarget(result);
         int size = input.unmarshalInt();
         ThreadContext context = input.getRuntime().getCurrentContext();
         for (int i = 0; i < size; i++) {
             result.op_aset(context, input.unmarshalObject(), input.unmarshalObject());
         }
         if (defaultValue) result.default_value_set(input.unmarshalObject());
         return result;
     }
 
     @Override
     public Class getJavaClass() {
         return Map.class;
     }
 
     // Satisfy java.util.Set interface (for Java integration)
 
     public int size() {
         return size;
     }
 
     public boolean isEmpty() {
         return size == 0;
     }
 
     public boolean containsKey(Object key) {
-        return internalGet(JavaUtil.convertJavaToRuby(getRuntime(), key)) != null;
+        return internalGet(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key)) != null;
     }
 
     public boolean containsValue(Object value) {
-        return hasValue(getRuntime().getCurrentContext(), JavaUtil.convertJavaToRuby(getRuntime(), value));
+        return hasValue(getRuntime().getCurrentContext(), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), value));
     }
 
     public Object get(Object key) {
-        return JavaUtil.convertRubyToJava(internalGet(JavaUtil.convertJavaToRuby(getRuntime(), key)));
+        return JavaUtil.convertRubyToJava(internalGet(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key)));
     }
 
     public Object put(Object key, Object value) {
-        internalPut(JavaUtil.convertJavaToRuby(getRuntime(), key), JavaUtil.convertJavaToRuby(getRuntime(), value));
+        internalPut(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), value));
         return value;
     }
 
     public Object remove(Object key) {
-        IRubyObject rubyKey = JavaUtil.convertJavaToRuby(getRuntime(), key);
+        IRubyObject rubyKey = JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key);
         return internalDelete(rubyKey).value;
     }
 
     public void putAll(Map map) {
         Ruby runtime = getRuntime();
         for (Iterator<Map.Entry> iter = map.entrySet().iterator(); iter.hasNext();) {
             Map.Entry entry = iter.next();
-            internalPut(JavaUtil.convertJavaToRuby(runtime, entry.getKey()), JavaUtil.convertJavaToRuby(runtime, entry.getValue()));
+            internalPut(JavaUtil.convertJavaToUsableRubyObject(runtime, entry.getKey()), JavaUtil.convertJavaToUsableRubyObject(runtime, entry.getValue()));
         }
     }
 
     public void clear() {
         rb_clear();
     }
 
     @Override
     public boolean equals(Object other) {
         if (!(other instanceof RubyHash)) return false;
         if (this == other) return true;
         return op_equal(getRuntime().getCurrentContext(), (RubyHash)other).isTrue() ? true : false;
     }
 
     public Set keySet() {
         return new BaseSet(KEY_VIEW);
     }
 
     public Set directKeySet() {
         return new BaseSet(DIRECT_KEY_VIEW);
     }
 
     public Collection values() {
         return new BaseCollection(VALUE_VIEW);
     }
 
     public Collection directValues() {
         return new BaseCollection(DIRECT_VALUE_VIEW);
     }
 
     public Set entrySet() {
         return new BaseSet(ENTRY_VIEW);
     }
 
     public Set directEntrySet() {
         return new BaseSet(DIRECT_ENTRY_VIEW);
     }
 
     private class BaseSet extends AbstractSet {
         final EntryView view;
 
         public BaseSet(EntryView view) {
             this.view = view;
         }
 
         public Iterator iterator() {
             return new BaseIterator(view);
         }
 
         @Override
         public boolean contains(Object o) {
             return view.contains(RubyHash.this, o);
         }
 
         @Override
         public void clear() {
             RubyHash.this.clear();
         }
 
         public int size() {
             return RubyHash.this.size;
         }
 
         @Override
         public boolean remove(Object o) {
             return view.remove(RubyHash.this, o);
         }
     }
 
     private class BaseCollection extends AbstractCollection {
         final EntryView view;
 
         public BaseCollection(EntryView view) {
             this.view = view;
         }
 
         public Iterator iterator() {
             return new BaseIterator(view);
         }
 
         @Override
         public boolean contains(Object o) {
             return view.contains(RubyHash.this, o);
         }
 
         @Override
         public void clear() {
             RubyHash.this.clear();
         }
 
         public int size() {
             return RubyHash.this.size;
         }
 
         @Override
         public boolean remove(Object o) {
             return view.remove(RubyHash.this, o);
         }
     }
 
     private class BaseIterator implements Iterator {
         final private EntryView view;
         private RubyHashEntry entry;
         private boolean peeking;
         private int startGeneration;
 
         public BaseIterator(EntryView view) {
             this.view = view;
             this.entry = head;
             this.startGeneration = generation;
         }
 
         private void advance(boolean consume) {
             if (!peeking) {
                 do {
                     if (startGeneration != generation) {
                         startGeneration = generation;
                         entry = head;
                     }
                     entry = entry.nextAdded;
                 } while (entry != head && !entry.isLive());
             }
             peeking = !consume;
         }
 
         public Object next() {
             advance(true);
             if (entry == head) {
                 peeking = true; // remain where we are
                 throw new NoSuchElementException();
             }
             return view.convertEntry(getRuntime(), entry);
         }
 
         // once hasNext has been called, we commit to next() returning
         // the entry it found, even if it were subsequently deleted
         public boolean hasNext() {
             advance(false);
             return entry != head;
         }
 
         public void remove() {
             if (entry == head) {
                 throw new IllegalStateException("Iterator out of range");
             }
             internalDeleteEntry(entry);
         }
     }
 
     private static abstract class EntryView {
         public abstract Object convertEntry(Ruby runtime, RubyHashEntry value);
         public abstract boolean contains(RubyHash hash, Object o);
         public abstract boolean remove(RubyHash hash, Object o);
     }
 
     private static final EntryView DIRECT_KEY_VIEW = new EntryView() {
         public Object convertEntry(Ruby runtime, RubyHashEntry entry) {
             return entry.key;
         }
         public boolean contains(RubyHash hash, Object o) {
             if (!(o instanceof IRubyObject)) return false;
             return hash.internalGet((IRubyObject)o) != null;
         }
         public boolean remove(RubyHash hash, Object o) {
             if (!(o instanceof IRubyObject)) return false;
             return hash.internalDelete((IRubyObject)o) != NO_ENTRY;
         }
     };
 
     private static final EntryView KEY_VIEW = new EntryView() {
         public Object convertEntry(Ruby runtime, RubyHashEntry entry) {
             return JavaUtil.convertRubyToJava(entry.key, Object.class);
         }
         public boolean contains(RubyHash hash, Object o) {
             return hash.containsKey(o);
         }
         public boolean remove(RubyHash hash, Object o) {
             return hash.remove(o) != null;
         }
     };
 
     private static final EntryView DIRECT_VALUE_VIEW = new EntryView() {
         public Object convertEntry(Ruby runtime, RubyHashEntry entry) {
             return entry.value;
         }
         public boolean contains(RubyHash hash, Object o) {
             if (!(o instanceof IRubyObject)) return false;
             IRubyObject obj = (IRubyObject)o;
             return hash.hasValue(obj.getRuntime().getCurrentContext(), obj);
         }
         public boolean remove(RubyHash hash, Object o) {
             if (!(o instanceof IRubyObject)) return false;
             IRubyObject obj = (IRubyObject) o;
             IRubyObject key = hash.internalIndex(obj.getRuntime().getCurrentContext(), obj);
             if (key == null) return false;
             return hash.internalDelete(key) != NO_ENTRY;
         }
     };
 
     private final EntryView VALUE_VIEW = new EntryView() {
         public Object convertEntry(Ruby runtime, RubyHashEntry entry) {
             return JavaUtil.convertRubyToJava(entry.value, Object.class);
         }
         public boolean contains(RubyHash hash, Object o) {
             return hash.containsValue(o);
         }
         public boolean remove(RubyHash hash, Object o) {
-            IRubyObject value = JavaUtil.convertJavaToRuby(hash.getRuntime(), o);
+            IRubyObject value = JavaUtil.convertJavaToUsableRubyObject(hash.getRuntime(), o);
             IRubyObject key = hash.internalIndex(hash.getRuntime().getCurrentContext(), value);
             if (key == null) return false;
             return hash.internalDelete(key) != NO_ENTRY;
         }
     };
 
     private final EntryView DIRECT_ENTRY_VIEW = new EntryView() {
         public Object convertEntry(Ruby runtime, RubyHashEntry entry) {
             return entry;
         }
         public boolean contains(RubyHash hash, Object o) {
             if (!(o instanceof RubyHashEntry)) return false;
             RubyHashEntry entry = (RubyHashEntry)o;
             RubyHashEntry candidate = internalGetEntry(entry.key);
             return candidate != NO_ENTRY && entry.equals(candidate);
         }
         public boolean remove(RubyHash hash, Object o) {
             if (!(o instanceof RubyHashEntry)) return false;
             return hash.internalDeleteEntry((RubyHashEntry)o) != NO_ENTRY;
         }
     };
 
     private final EntryView ENTRY_VIEW = new EntryView() {
         public Object convertEntry(Ruby runtime, RubyHashEntry entry) {
             return new ConvertingEntry(runtime, entry);
         }
         public boolean contains(RubyHash hash, Object o) {
             if (!(o instanceof ConvertingEntry)) return false;
             ConvertingEntry entry = (ConvertingEntry)o;
             RubyHashEntry candidate = hash.internalGetEntry(entry.entry.key);
             return candidate != NO_ENTRY && entry.entry.equals(candidate);
         }
         public boolean remove(RubyHash hash, Object o) {
             if (!(o instanceof ConvertingEntry)) return false;
             ConvertingEntry entry = (ConvertingEntry)o;
             return hash.internalDeleteEntry(entry.entry) != NO_ENTRY;
         }
     };
 
     private static class ConvertingEntry implements Map.Entry {
         private final RubyHashEntry entry;
         private final Ruby runtime;
 
         public ConvertingEntry(Ruby runtime, RubyHashEntry entry) {
             this.entry = entry;
             this.runtime = runtime;
         }
 
         public Object getKey() {
             return JavaUtil.convertRubyToJava(entry.key, Object.class);
         }
         public Object getValue() {
             return JavaUtil.convertRubyToJava(entry.value, Object.class);
         }
         public Object setValue(Object o) {
-            return entry.setValue(JavaUtil.convertJavaToRuby(runtime, o));
+            return entry.setValue(JavaUtil.convertJavaToUsableRubyObject(runtime, o));
         }
 
         @Override
         public boolean equals(Object o) {
             if (!(o instanceof ConvertingEntry)) {
                 return false;
             }
             ConvertingEntry other = (ConvertingEntry)o;
             return entry.equals(other.entry);
         }
         
         @Override
         public int hashCode() {
             return entry.hashCode();
         }
     }
 }
