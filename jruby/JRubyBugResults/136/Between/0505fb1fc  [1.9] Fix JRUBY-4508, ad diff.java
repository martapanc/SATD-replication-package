diff --git a/spec/tags/1.9/ruby/core/array/rotate_tags.txt b/spec/tags/1.9/ruby/core/array/rotate_tags.txt
deleted file mode 100644
index a553ff8bda..0000000000
--- a/spec/tags/1.9/ruby/core/array/rotate_tags.txt
+++ /dev/null
@@ -1,9 +0,0 @@
-fails(JRUBY-4508):Array#rotate returns a copy of the array whose first n elements is moved to the last
-fails(JRUBY-4508):Array#rotate returns a copy of the array when the length is one
-fails(JRUBY-4508):Array#rotate returns an empty array when self is empty
-fails(JRUBY-4508):Array#rotate does not return self
-fails(JRUBY-4508):Array#rotate returns subclass instance for Array subclasses
-fails(JRUBY-4508):Array#rotate! moves the first n elements to the last and returns self
-fails(JRUBY-4508):Array#rotate! does nothing and returns self when the length is zero or one
-fails(JRUBY-4508):Array#rotate! returns self
-fails(JRUBY-4508):Array#rotate! raises a RuntimeError on a frozen array
diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index 62484beeeb..ae4db21276 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -2638,1343 +2638,1415 @@ public class RubyArray extends RubyObject implements List {
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
         splice(pos, len, null);
 
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
 
     @JRubyMethod(name = "flatten", compat = CompatVersion.RUBY1_8)
     public IRubyObject flatten(ThreadContext context) {
         Ruby runtime = context.getRuntime();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, -1, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "flatten", compat = CompatVersion.RUBY1_8)
     public IRubyObject flatten(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         int level = RubyNumeric.num2int(arg);
         if (level == 0) return this;
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, level, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "flatten", compat = CompatVersion.RUBY1_9)
     public IRubyObject flatten19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, -1, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "flatten", compat = CompatVersion.RUBY1_9)
     public IRubyObject flatten19(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         int level = RubyNumeric.num2int(arg);
         if (level == 0) return makeShared();
 
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
     @JRubyMethod(name = "*", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject op_times(ThreadContext context, IRubyObject times) {
         IRubyObject tmp = times.checkStringType();
 
         if (!tmp.isNil()) return join(context, tmp);
 
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
 
     /** rb_ary_times
      *
      */
     @JRubyMethod(name = "*", required = 1, compat = CompatVersion.RUBY1_9)
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
     @JRubyMethod(name = "sort", frame = true, compat = CompatVersion.RUBY1_8)
     public RubyArray sort(ThreadContext context, Block block) {
         RubyArray ary = aryDup();
         ary.sort_bang(context, block);
         return ary;
     }
 
     @JRubyMethod(name = "sort", frame = true, compat = CompatVersion.RUBY1_9)
     public RubyArray sort19(ThreadContext context, Block block) {
         RubyArray ary = aryDup();
         ary.sort_bang19(context, block);
         return ary;
     }
 
     /** rb_ary_sort_bang
      *
      */
     @JRubyMethod(name = "sort!", frame = true, compat = CompatVersion.RUBY1_8)
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
 
     @JRubyMethod(name = "sort!", frame = true, compat = CompatVersion.RUBY1_9)
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
                 IRubyObject ret = block.yieldArray(context, getRuntime().newArray(obj1, obj2), null, null);
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
         if (n > realLength) n = realLength;
 
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
 
+
+    private static void aryReverse(IRubyObject[] _p1, int p1, IRubyObject[] _p2, int p2) {
+        while(p1 < p2) {
+            IRubyObject tmp = _p1[p1];
+            _p1[p1++] = _p2[p2];
+            _p2[p2--] = tmp;
+        }
+    }
+
+    private IRubyObject internalRotateBang(ThreadContext context, int cnt) {
+        modify();
+        
+        if(cnt != 0) {
+            IRubyObject[] ptr = values;
+            int len = realLength;
+            
+            if(len > 0 && (cnt = rotateCount(cnt, len)) > 0) {
+                --len;
+                if(cnt < len) aryReverse(ptr, begin + cnt, ptr, begin + len);
+                if(--cnt > 0) aryReverse(ptr, begin, ptr, begin + cnt);
+                if(len > 0)   aryReverse(ptr, begin, ptr, begin + len);
+                return this;
+            }
+        }
+        
+        return context.getRuntime().getNil();
+    }
+
+    private static int rotateCount(int cnt, int len) {
+        return (cnt < 0) ? (len - (~cnt % len) - 1) : (cnt % len);
+    }
+
+    private IRubyObject internalRotate(ThreadContext context, int cnt) {
+        int len = realLength;
+        RubyArray rotated = aryDup();
+        rotated.modify();
+
+        if(len > 0) {
+            cnt = rotateCount(cnt, len);
+            IRubyObject[] ptr = this.values;
+            IRubyObject[] ptr2 = rotated.values;
+            len -= cnt;
+            System.arraycopy(ptr, begin + cnt, ptr2, 0, len);
+            System.arraycopy(ptr, begin, ptr2, len, cnt);
+        }
+        
+        return rotated;
+    }
+
+    @JRubyMethod(name = "rotate!", compat = CompatVersion.RUBY1_9)
+    public IRubyObject rotate_bang(ThreadContext context) {
+        internalRotateBang(context, 1);
+        return this;
+    }
+
+    @JRubyMethod(name = "rotate!", compat = CompatVersion.RUBY1_9)
+    public IRubyObject rotate_bang(ThreadContext context, IRubyObject cnt) {
+        internalRotateBang(context, RubyNumeric.fix2int(cnt));
+        return this;
+    }
+
+    @JRubyMethod(name = "rotate", compat = CompatVersion.RUBY1_9)
+    public IRubyObject rotate(ThreadContext context) {
+        return internalRotate(context, 1);
+    }
+
+    @JRubyMethod(name = "rotate", compat = CompatVersion.RUBY1_9)
+    public IRubyObject rotate(ThreadContext context, IRubyObject cnt) {
+        return internalRotate(context, RubyNumeric.fix2int(cnt));
+    }
+
+
     // Enumerable direct implementations (non-"each" versions)
     public IRubyObject all_p(ThreadContext context, Block block) {
         if (!isBuiltin("each")) return RubyEnumerable.all_pCommon(context, this, block);
         if (!block.isGiven()) return all_pBlockless(context);
 
         for (int i = begin; i < begin + realLength; i++) {
             if (!block.yield(context, values[i]).isTrue()) return context.getRuntime().getFalse();
         }
 
         return context.getRuntime().getTrue();
     }
 
     private IRubyObject all_pBlockless(ThreadContext context) {
         for (int i = begin; i < begin + realLength; i++) {
             if (!values[i].isTrue()) return context.getRuntime().getFalse();
         }
 
         return context.getRuntime().getTrue();
     }
 
     public IRubyObject any_p(ThreadContext context, Block block) {
         if (!isBuiltin("each")) return RubyEnumerable.any_pCommon(context, this, block);
         if (!block.isGiven()) return any_pBlockless(context);
 
         for (int i = begin; i < begin + realLength; i++) {
             if (block.yield(context, values[i]).isTrue()) return context.getRuntime().getTrue();
         }
 
         return context.getRuntime().getFalse();
     }
 
     private IRubyObject any_pBlockless(ThreadContext context) {
         for (int i = begin; i < begin + realLength; i++) {
             if (values[i].isTrue()) return context.getRuntime().getTrue();
         }
 
         return context.getRuntime().getFalse();
     }
 
     public IRubyObject find(ThreadContext context, IRubyObject ifnone, Block block) {
         if (!isBuiltin("each")) return RubyEnumerable.detectCommon(context, this, block);
 
         return detectCommon(context, ifnone, block);
     }
 
     public IRubyObject find_index(ThreadContext context, Block block) {
         if (!isBuiltin("each")) return RubyEnumerable.find_indexCommon(context, this, block);
 
         for (int i = 0; i < realLength; i++) {
             if (block.yield(context, values[begin + i]).isTrue()) return context.getRuntime().newFixnum(i);
         }
 
         return context.getRuntime().getNil();
     }
 
     public IRubyObject find_index(ThreadContext context, IRubyObject cond) {
         if (!isBuiltin("each")) return RubyEnumerable.find_indexCommon(context, this, cond);
 
         for (int i = 0; i < realLength; i++) {
             if (values[begin + i].equals(cond)) return context.getRuntime().newFixnum(i);
         }
 
         return context.getRuntime().getNil();
     }
 
     public IRubyObject detectCommon(ThreadContext context, IRubyObject ifnone, Block block) {
         for (int i = begin; i < begin + realLength; i++) {
             if (block.yield(context, values[i]).isTrue()) return values[i];
         }
 
         return ifnone != null ? ifnone.callMethod(context, "call") : 
             context.getRuntime().getNil();
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
             IRubyObject convertedElement = JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element);
 
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
             return element.toJava(Object.class);
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
             return elt(last = --index).toJava(Object.class);
         }
 
         public int nextIndex() {
             return index;
         }
 
         public int previousIndex() {
             return index - 1;
         }
 
         public void set(Object obj) {
             if (last == -1) {
                 throw new IllegalStateException();
             }
 
             store(last, JavaUtil.convertJavaToUsableRubyObject(getRuntime(), obj));
         }
 
         public void add(Object obj) {
             insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index++), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), obj) });
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
