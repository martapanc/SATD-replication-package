diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index f0305c8e7b..ca60c6845f 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -2453,2040 +2453,2040 @@ public class RubyArray extends RubyObject implements List, RandomAccess {
      *
      */
     public IRubyObject selectCommon(ThreadContext context, Block block) {
         Ruby runtime = context.runtime;
         RubyArray result = new RubyArray(runtime, realLength);
 
         for (int i = 0; i < realLength; i++) {
             // Do not coarsen the "safe" check, since it will misinterpret AIOOBE from the yield
             // See JRUBY-5434
             IRubyObject value = safeArrayRef(values, begin + i);
 
             if (block.yield(context, value).isTrue()) result.append(value);
         }
 
         Helpers.fillNil(result.values, result.realLength, result.values.length, runtime);
         return result;
     }
 
     @JRubyMethod
     public IRubyObject select(ThreadContext context, Block block) {
         return block.isGiven() ? selectCommon(context, block) : enumeratorize(context.runtime, this, "select");
     }
 
     @JRubyMethod(name = "select!", compat = RUBY1_9)
     public IRubyObject select_bang(ThreadContext context, Block block) {
         Ruby runtime = context.runtime;
         if (!block.isGiven()) return enumeratorize(runtime, this, "select!");
         
         modify();
         
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
             return enumeratorize(context.runtime, this, "keep_if");
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
         IRubyObject value = item;
 
         Ruby runtime = context.runtime;
         for (int i1 = 0; i1 < realLength; i1++) {
             // Do not coarsen the "safe" check, since it will misinterpret AIOOBE from equalInternal
             // See JRUBY-5434
             IRubyObject e = safeArrayRef(values, begin + i1);
             if (equalInternal(context, e, item)) {
                 value = e;
                 continue;
             }
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
                 Helpers.fillNil(myValues, myBegin + i2, myBegin + myRealLength, context.runtime);
                 this.realLength = i2;
                 int valuesLength = myValues.length - myBegin;
                 if (i2 << 1 < valuesLength && valuesLength > ARRAY_DEFAULT_SIZE) realloc(i2 << 1, valuesLength);
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
 
         return context.is19 ? value : item;
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
         return block.isGiven() ? rejectCommon(context, block) : enumeratorize(context.runtime, this, "reject");
     }
 
     /** rb_ary_reject_bang
      *
      */
     public IRubyObject rejectBang(ThreadContext context, Block block) {
         if (!block.isGiven()) throw context.runtime.newLocalJumpErrorNoBlock();
 
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
 
         if (realLength == i2) return context.runtime.getNil();
 
         if (i2 < realLength) {
             try {
                 Helpers.fillNil(values, begin + i2, begin + realLength, context.runtime);
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
             }
             realLength = i2;
         }
 
         return this;
     }
 
     @JRubyMethod(name = "reject!")
     public IRubyObject reject_bang(ThreadContext context, Block block) {
         return block.isGiven() ? rejectBang(context, block) : enumeratorize(context.runtime, this, "reject!");
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
         return block.isGiven() ? deleteIf(context, block) : enumeratorize(context.runtime, this, "delete_if");
     }
 
     /** rb_ary_zip
      *
      */
     @JRubyMethod(optional = 1, rest = true)
     public IRubyObject zip(ThreadContext context, IRubyObject[] args, Block block) {
         final Ruby runtime = context.runtime;
         final int aLen = args.length + 1;
         RubyClass array = runtime.getArray();
 
         final IRubyObject[] newArgs = new IRubyObject[args.length];
 
         boolean hasUncoercible = false;
         for (int i = 0; i < args.length; i++) {
             newArgs[i] = TypeConverter.convertToType(args[i], array, "to_ary", false);
             if (newArgs[i].isNil()) {
                 hasUncoercible = true;
             }
         }
 
         // Handle uncoercibles by trying to_enum conversion
         if (hasUncoercible) {
             RubySymbol each = runtime.newSymbol("each");
             for (int i = 0; i < args.length; i++) {
                 newArgs[i] = args[i].callMethod(context, "to_enum", each);
             }
         }
 
         if (hasUncoercible) {
             return zipCommon(context, newArgs, block, new ArgumentVisitor() {
                 public IRubyObject visit(ThreadContext ctx, IRubyObject arg, int i) {
                     return RubyEnumerable.zipEnumNext(ctx, arg);
                 }
             });
         } else {
             return zipCommon(context, newArgs, block, new ArgumentVisitor() {
                 public IRubyObject visit(ThreadContext ctx, IRubyObject arg, int i) {
                     return ((RubyArray) arg).elt(i);
                 }
             });
         }
     }
 
     // This can be shared with RubyEnumerable to clean #zipCommon{Enum,Arg} a little
     public static interface ArgumentVisitor {
         IRubyObject visit(ThreadContext ctx, IRubyObject arg, int i);
     }
 
     private IRubyObject zipCommon(ThreadContext context, IRubyObject[] args, Block block, ArgumentVisitor visitor) {
         Ruby runtime = context.runtime;
 
         if (block.isGiven()) {
             for (int i = 0; i < realLength; i++) {
                 IRubyObject[] tmp = new IRubyObject[args.length + 1];
                 // Do not coarsen the "safe" check, since it will misinterpret AIOOBE from the yield
                 // See JRUBY-5434
                 tmp[0] = safeArrayRef(values, begin + i);
                 for (int j = 0; j < args.length; j++) {
                     tmp[j + 1] = visitor.visit(context, args[j], i);
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
                     tmp[j + 1] = visitor.visit(context, args[j], i);
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
         Ruby runtime = context.runtime;
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
         Ruby runtime = context.runtime;
 
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
         Ruby runtime = context.runtime;
 
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
         Ruby runtime = context.runtime;
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
         Ruby runtime = context.runtime;
 
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
         Ruby runtime = context.runtime;
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
         Ruby runtime = context.runtime;
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, -1, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "flatten", compat = RUBY1_8)
     public IRubyObject flatten(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.runtime;
         int level = RubyNumeric.num2int(arg);
         if (level == 0) return this;
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, level, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "flatten", compat = RUBY1_9)
     public IRubyObject flatten19(ThreadContext context) {
         Ruby runtime = context.runtime;
 
         RubyArray result = new RubyArray(runtime, getMetaClass(), realLength);
         flatten(context, -1, result);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "flatten", compat = RUBY1_9)
     public IRubyObject flatten19(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.runtime;
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
             return RubyFixnum.newFixnum(context.runtime, n);
         } else {
             return RubyFixnum.newFixnum(context.runtime, realLength);
         }
     }
 
     @JRubyMethod(name = "count")
     public IRubyObject count(ThreadContext context, IRubyObject obj, Block block) {
         if (block.isGiven()) context.runtime.getWarnings().warn(ID.BLOCK_UNUSED, "given block not used");
 
         int n = 0;
         for (int i = 0; i < realLength; i++) {
             if (equalInternal(context, elt(i), obj)) n++;
         }
         return RubyFixnum.newFixnum(context.runtime, n);
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
         Ruby runtime = context.runtime;
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
         Ruby runtime = context.runtime;
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
         if (realLength == hash.size()) return context.runtime.getNil();
 
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
         if (realLength == hash.size()) return context.runtime.getNil();
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
 
         RubyArray result = new RubyArray(context.runtime, getMetaClass(), hash.size()); 
 
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
 
         final RubyArray result = new RubyArray(context.runtime, getMetaClass(), hash.size());
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
         Helpers.fillNil(ary3.values, ary3.realLength, ary3.values.length, getRuntime());
 
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
 
         Helpers.fillNil(ary3.values, ary3.realLength, ary3.values.length, getRuntime());
 
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
 
         Helpers.fillNil(ary3.values, ary3.realLength, ary3.values.length, getRuntime());
 
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
         final boolean fixnumBypass = !honorOverride || context.runtime.newFixnum(0).isBuiltin("<=>");
         final boolean stringBypass = !honorOverride || context.runtime.newString("").isBuiltin("<=>");
 
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
         if (!block.isGiven()) return enumeratorize(context.runtime, this, "sort_by!");
 
         modifyCheck();
         RubyArray sorted = Helpers.invoke(context, this, "sort_by", block).convertToArray();
         values = sorted.values;
         isShared = false;
         begin = 0;
         return this;
     }
 
     /** rb_ary_take
      * 
      */
-    @JRubyMethod(name = "take", compat = RUBY1_9)
+    @JRubyMethod(name = "take")
     public IRubyObject take(ThreadContext context, IRubyObject n) {
         Ruby runtime = context.runtime;
         long len = RubyNumeric.num2long(n);
         if (len < 0) throw runtime.newArgumentError("attempt to take negative size");
 
         return subseq(0, len);
     }
 
     /** rb_ary_take_while
      * 
      */
-    @JRubyMethod(name = "take_while", compat = RUBY1_9)
+    @JRubyMethod(name = "take_while")
     public IRubyObject take_while(ThreadContext context, Block block) {
         Ruby runtime = context.runtime;
         if (!block.isGiven()) return enumeratorize(runtime, this, "take_while");
 
         int i = 0;
         for (; i < realLength; i++) {
             if (!block.yield(context, eltOk(i)).isTrue()) break;
         }
         return subseq(0, i);
     }
 
     /** rb_ary_take
      * 
      */
-    @JRubyMethod(name = "drop", compat = RUBY1_9)
+    @JRubyMethod(name = "drop")
     public IRubyObject drop(ThreadContext context, IRubyObject n) {
         Ruby runtime = context.runtime;
         long pos = RubyNumeric.num2long(n);
         if (pos < 0) throw runtime.newArgumentError("attempt to drop negative size");
 
         IRubyObject result = subseq(pos, realLength);
         return result.isNil() ? runtime.newEmptyArray() : result;
     }
 
     /** rb_ary_take_while
      * 
      */
-    @JRubyMethod(name = "drop_while", compat = RUBY1_9)
+    @JRubyMethod(name = "drop_while")
     public IRubyObject drop_while(ThreadContext context, Block block) {
         Ruby runtime = context.runtime;
         if (!block.isGiven()) return enumeratorize(runtime, this, "drop_while");
 
         int i = 0;
         for (; i < realLength; i++) {
             if (!block.yield(context, eltOk(i)).isTrue()) break;
         }
         IRubyObject result = subseq(i, realLength);
         return result.isNil() ? runtime.newEmptyArray() : result;
     }
 
     /** rb_ary_cycle
      * 
      */
     @JRubyMethod(name = "cycle")
     public IRubyObject cycle(ThreadContext context, Block block) {
         if (!block.isGiven()) return enumeratorize(context.runtime, this, "cycle");
         return cycleCommon(context, -1, block);
     }
 
     /** rb_ary_cycle
      * 
      */
     @JRubyMethod(name = "cycle")
     public IRubyObject cycle(ThreadContext context, IRubyObject arg, Block block) {
         if (arg.isNil()) return cycle(context, block);
         if (!block.isGiven()) return enumeratorize(context.runtime, this, "cycle", arg);
 
         long times = RubyNumeric.num2long(arg);
         if (times <= 0) return context.runtime.getNil();
 
         return cycleCommon(context, times, block);
     }
 
     private IRubyObject cycleCommon(ThreadContext context, long n, Block block) {
         while (realLength > 0 && (n < 0 || 0 < n--)) {
             for (int i = 0; i < realLength; i++) {
                 block.yield(context, eltOk(i));
             }
         }
         return context.runtime.getNil();
     }
 
 
     /** rb_ary_product
      *
      */
     @JRubyMethod(name = "product", rest = true, compat = RUBY1_8)
     public IRubyObject product(ThreadContext context, IRubyObject[] args) {
         return product19(context, args, Block.NULL_BLOCK);
     }
     
     @JRubyMethod(name = "product", rest = true, compat = RUBY1_9)
     public IRubyObject product19(ThreadContext context, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
 
         boolean useBlock = block.isGiven();
 
         int n = args.length + 1;
         RubyArray arrays[] = new RubyArray[n];
         int counters[] = new int[n];
 
         arrays[0] = this;
         for (int i = 1; i < n; i++) arrays[i] = args[i - 1].convertToArray();
 
         int resultLen = 1;
         for (int i = 0; i < n; i++) {
             int k = arrays[i].realLength;
             int l = resultLen;
             if (k == 0) return useBlock ? this : newEmptyArray(runtime);
             resultLen *= k;
             if (resultLen < k || resultLen < l || resultLen / k != l) {
                 if (!block.isGiven()) throw runtime.newRangeError("too big to product");
             }
         }
 
         RubyArray result = useBlock ? null : newArray(runtime, resultLen);
 
         for (int i = 0; i < resultLen; i++) {
             RubyArray sub = newArray(runtime, n);
             for (int j = 0; j < n; j++) sub.append(arrays[j].entry(counters[j]));
 
             if (useBlock) {
                 block.yieldSpecific(context, sub);
             } else {
                 result.append(sub);
             }
             int m = n - 1;
             counters[m]++;
 
             while (m > 0 && counters[m] == arrays[m].realLength) {
                 counters[m] = 0;
                 m--;
                 counters[m]++;
             }
         }
         return useBlock ? this : result;
     }
 
     /** rb_ary_combination
      * 
      */
     @JRubyMethod(name = "combination")
     public IRubyObject combination(ThreadContext context, IRubyObject num, Block block) {
         Ruby runtime = context.runtime;
         if (!block.isGiven()) return enumeratorize(runtime, this, "combination", num);
 
         int n = RubyNumeric.num2int(num);
 
         if (n == 0) {
             block.yield(context, newEmptyArray(runtime));
         } else if (n == 1) {
             for (int i = 0; i < realLength; i++) {
                 block.yield(context, newArray(runtime, eltOk(i)));
             }
         } else if (n >= 0 && realLength >= n) {
             int stack[] = new int[n + 1];
             IRubyObject chosen[] = new IRubyObject[n];
             int lev = 0;
 
             stack[0] = -1;
             for (;;) {
                 chosen[lev] = eltOk(stack[lev + 1]);
                 for (lev++; lev < n; lev++) {
                     chosen[lev] = eltOk(stack[lev + 1] = stack[lev] + 1);
                 }
                 block.yield(context, newArray(runtime, chosen));
                 do {
                     if (lev == 0) return this;
                     stack[lev--]++;
                 } while (stack[lev + 1] + n == realLength + lev + 1);
             }
         }
 
         return this;
     }
 
     @JRubyMethod(name = "repeated_combination", compat = RUBY1_9)
     public IRubyObject repeatedCombination(ThreadContext context, IRubyObject num, Block block) {
         Ruby runtime = context.runtime;
         if (!block.isGiven()) return enumeratorize(runtime, this, "repeated_combination", num);
 
         int n = RubyNumeric.num2int(num);
         int myRealLength = realLength;
         IRubyObject[] myValues = new IRubyObject[realLength];
         safeArrayCopy(values, begin, myValues, 0, realLength);
 
         if (n < 0) {
             // yield nothing
         } else if (n == 0) {
             block.yield(context, newEmptyArray(runtime));
         } else if (n == 1) {
             for (int i = 0; i < myRealLength; i++) {
                 block.yield(context, newArray(runtime, myValues[i]));
             }
         } else {
             int[] stack = new int[n];
             repeatCombination(context, runtime, myValues, stack, 0, myRealLength - 1, block);
         }
 
         return this;
     }
 
     private static void repeatCombination(ThreadContext context, Ruby runtime, IRubyObject[] values, int[] stack, int index, int max, Block block) {
         if (index == stack.length) {
             IRubyObject[] obj = new IRubyObject[stack.length];
             
             for (int i = 0; i < obj.length; i++) {
                 int idx = stack[i];
                 obj[i] = values[idx];
             }
 
             block.yield(context, newArray(runtime, obj));
         } else {
             int minValue = 0;
             if (index > 0) {
                 minValue = stack[index - 1];
             }
             for (int i = minValue; i <= max; i++) {
                 stack[index] = i;
                 repeatCombination(context, runtime, values, stack, index + 1, max, block);
             }
         }
     }
 
     private void permute(ThreadContext context, int n, int r, int[]p, int index, boolean[]used, boolean repeat, RubyArray values, Block block) {
         for (int i = 0; i < n; i++) {
             if (repeat || !used[i]) {
                 p[index] = i;
                 if (index < r - 1) {
                     used[i] = true;
                     permute(context, n, r, p, index + 1, used, repeat, values, block);
                     used[i] = false;
                 } else {
                     RubyArray result = newArray(context.runtime, r);
 
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
         return block.isGiven() ? permutationCommon(context, RubyNumeric.num2int(num), false, block) : enumeratorize(context.runtime, this, "permutation", num);
     }
 
     @JRubyMethod(name = "permutation")
     public IRubyObject permutation(ThreadContext context, Block block) {
         return block.isGiven() ? permutationCommon(context, realLength, false, block) : enumeratorize(context.runtime, this, "permutation");
     }
 
     @JRubyMethod(name = "repeated_permutation", compat = RUBY1_9)
     public IRubyObject repeated_permutation(ThreadContext context, IRubyObject num, Block block) {
         return block.isGiven() ? permutationCommon(context, RubyNumeric.num2int(num), true, block) : enumeratorize(context.runtime, this, "repeated_permutation", num);
     }
 
     @JRubyMethod(name = "repeated_permutation", compat = RUBY1_9)
     public IRubyObject repeated_permutation(ThreadContext context, Block block) {
         return block.isGiven() ? permutationCommon(context, realLength, true, block) : enumeratorize(context.runtime, this, "repeated_permutation");
     }
 
     private IRubyObject permutationCommon(ThreadContext context, int r, boolean repeat, Block block) {
         if (r == 0) {
             block.yield(context, newEmptyArray(context.runtime));
         } else if (r == 1) {
             for (int i = 0; i < realLength; i++) {
                 block.yield(context, newArray(context.runtime, eltOk(i)));
             }
         } else if (r >= 0) {
             int n = realLength;
             permute(context, n, r,
                     new int[r], 0,
                     new boolean[n],
                     repeat,
                     makeShared(begin, n, getMetaClass()), block);
         }
         return this;
     }
 
     @JRubyMethod(name = "choice", compat = RUBY1_8)
     public IRubyObject choice(ThreadContext context) {
         if (realLength == 0) {
             return context.nil;
         }
         return eltOk((int) (context.runtime.getDefaultRand().genrandReal() * realLength));
     }
     
     @JRubyMethod(name = "shuffle!", compat = RUBY1_8)
     public IRubyObject shuffle_bang(ThreadContext context) {
         modify();
         int i = realLength;
         
         try {
             while (i > 0) {
                 int r = (int)(context.runtime.getDefaultRand().genrandReal() * i);
                 IRubyObject tmp = eltOk(--i);
                 values[begin + i] = eltOk(r);
                 values[begin + r] = tmp;
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
 
         return this;
     }
 
     @JRubyMethod(name = "shuffle!", optional = 1, compat = RUBY1_9)
     public IRubyObject shuffle_bang(ThreadContext context, IRubyObject[] args) {
         modify();
         IRubyObject randgen = context.runtime.getRandomClass();
         if (args.length > 0) {
             IRubyObject hash = TypeConverter.checkHashType(context.runtime, args[args.length - 1]);
             if (!hash.isNil()) {
                 randgen = ((RubyHash) hash).fastARef(context.runtime.newSymbol("random"));
             }
         }
         int i = realLength;
         try {
             while (i > 0) {
                 int r = (int) (RubyRandom.randomReal(context, randgen) * i);
                 IRubyObject tmp = eltOk(--i);
                 values[begin + i] = eltOk(r);
                 values[begin + r] = tmp;
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
 
         return this;
     }
 
     @JRubyMethod(name = "shuffle", compat = RUBY1_8)
     public IRubyObject shuffle(ThreadContext context) {
         RubyArray ary = aryDup();
         ary.shuffle_bang(context);
         return ary;
     }
 
     @JRubyMethod(name = "shuffle", optional = 1, compat = RUBY1_9)
     public IRubyObject shuffle(ThreadContext context, IRubyObject[] args) {
         RubyArray ary = aryDup19();
         ary.shuffle_bang(context, args);
         return ary;
     }
 
     private static int SORTED_THRESHOLD = 10; 
     @JRubyMethod(name = "sample", optional = 2, compat = RUBY1_9)
     public IRubyObject sample(ThreadContext context, IRubyObject[] args) {
         try {
             IRubyObject randgen = context.runtime.getRandomClass();
             if (args.length == 0) {
                 if (realLength == 0)
                     return context.nil;
                 int i = realLength == 1 ? 0 : randomReal(context, randgen, realLength);
                 return eltOk(i);
             }
             if (args.length > 0) {
                 IRubyObject hash = TypeConverter.checkHashType(context.runtime,
                         args[args.length - 1]);
                 if (!hash.isNil()) {
                     randgen = ((RubyHash) hash).fastARef(context.runtime.newSymbol("random"));
                     IRubyObject[] newargs = new IRubyObject[args.length - 1];
                     System.arraycopy(args, 0, newargs, 0, args.length - 1);
                     args = newargs;
                 }
             }
             if (args.length == 0) {
                 if (realLength == 0) {
                     return context.nil;
                 } else if (realLength == 1) {
                     return eltOk(0);
                 }
                 return eltOk(randomReal(context, randgen, realLength));
             }
             Ruby runtime = context.runtime;
             int n = RubyNumeric.num2int(args[0]);
 
             if (n < 0)
                 throw runtime.newArgumentError("negative sample number");
             if (n > realLength)
                 n = realLength;
             double[] rnds = new double[SORTED_THRESHOLD];
             if (n <= SORTED_THRESHOLD) {
                 for (int idx = 0; idx < n; ++idx) {
                     rnds[idx] = RubyRandom.randomReal(context, randgen);
                 }
             }
 
             int i, j, k;
             switch (n) {
             case 0:
                 return newEmptyArray(runtime);
             case 1:
                 if (realLength <= 0)
                     return newEmptyArray(runtime);
 
                 return newArray(runtime, eltOk((int) (rnds[0] * realLength)));
             case 2:
                 i = (int) (rnds[0] * realLength);
                 j = (int) (rnds[1] * (realLength - 1));
                 if (j >= i)
                     j++;
                 return newArray(runtime, eltOk(i), eltOk(j));
             case 3:
                 i = (int) (rnds[0] * realLength);
                 j = (int) (rnds[1] * (realLength - 1));
                 k = (int) (rnds[2] * (realLength - 2));
                 int l = j,
                 g = i;
                 if (j >= i) {
                     l = i;
                     g = ++j;
                 }
                 if (k >= l && (++k >= g))
                     ++k;
                 return new RubyArray(runtime, new IRubyObject[] { eltOk(i),
                         eltOk(j), eltOk(k) });
             }
 
             int len = realLength;
             if (n > len)
                 n = len;
             if (n < SORTED_THRESHOLD) {
                 int idx[] = new int[SORTED_THRESHOLD];
                 int sorted[] = new int[SORTED_THRESHOLD];
                 sorted[0] = idx[0] = (int) (rnds[0] * len);
                 for (i = 1; i < n; i++) {
                     k = (int) (rnds[i] * --len);
                     for (j = 0; j < i; j++) {
                         if (k < sorted[j])
                             break;
                         k++;
                     }
                     System.arraycopy(sorted, j, sorted, j + 1, i - j);
                     sorted[j] = idx[i] = k;
                 }
                 IRubyObject[] result = new IRubyObject[n];
                 for (i = 0; i < n; i++)
                     result[i] = eltOk(idx[i]);
                 return new RubyArray(runtime, result);
             } else {
                 IRubyObject[] result = new IRubyObject[len];
                 System.arraycopy(values, begin, result, 0, len);
                 for (i = 0; i < n; i++) {
                     j = randomReal(context, randgen, len - i) + i;
                     IRubyObject tmp = result[j];
                     result[j] = result[i];
                     result[i] = tmp;
                 }
                 RubyArray ary = new RubyArray(runtime, result);
                 ary.realLength = n;
                 return ary;
             }
         } catch (ArrayIndexOutOfBoundsException aioob) {
             concurrentModification();
             return this; // not reached
         }
     }
 
     private int randomReal(ThreadContext context, IRubyObject randgen, int len) {
         return (int) (RubyRandom.randomReal(context, randgen) * len);
     }
 
     private static void aryReverse(IRubyObject[] _p1, int p1, IRubyObject[] _p2, int p2) {
         while(p1 < p2) {
             IRubyObject tmp = _p1[p1];
             _p1[p1++] = _p2[p2];
             _p2[p2--] = tmp;
         }
     }
 
     private IRubyObject internalRotateBang(ThreadContext context, int cnt) {
         modify();
 
         try {
             if(cnt != 0) {
                 IRubyObject[] ptr = values;
                 int len = realLength;
 
                 if(len > 0 && (cnt = rotateCount(cnt, len)) > 0) {
                     --len;
                     if(cnt < len) aryReverse(ptr, begin + cnt, ptr, begin + len);
                     if(--cnt > 0) aryReverse(ptr, begin, ptr, begin + cnt);
                     if(len > 0)   aryReverse(ptr, begin, ptr, begin + len);
                     return this;
                 }
             }
         } catch (ArrayIndexOutOfBoundsException aioob) {
             concurrentModification();
         }
         
         return context.runtime.getNil();
     }
 
     private static int rotateCount(int cnt, int len) {
         return (cnt < 0) ? (len - (~cnt % len) - 1) : (cnt % len);
     }
 
     private IRubyObject internalRotate(ThreadContext context, int cnt) {
         int len = realLength;
         RubyArray rotated = aryDup();
         rotated.modify();
 
         try {
             if(len > 0) {
                 cnt = rotateCount(cnt, len);
                 IRubyObject[] ptr = this.values;
                 IRubyObject[] ptr2 = rotated.values;
                 len -= cnt;
                 System.arraycopy(ptr, begin + cnt, ptr2, 0, len);
                 System.arraycopy(ptr, begin, ptr2, len, cnt);
             }
         } catch (ArrayIndexOutOfBoundsException aioob) {
             concurrentModification();
         }
         
         return rotated;
     }
 
     @JRubyMethod(name = "rotate!", compat = RUBY1_9)
     public IRubyObject rotate_bang(ThreadContext context) {
         internalRotateBang(context, 1);
         return this;
     }
 
     @JRubyMethod(name = "rotate!", compat = RUBY1_9)
     public IRubyObject rotate_bang(ThreadContext context, IRubyObject cnt) {
         internalRotateBang(context, RubyNumeric.fix2int(cnt));
         return this;
     }
 
     @JRubyMethod(name = "rotate", compat = RUBY1_9)
     public IRubyObject rotate(ThreadContext context) {
         return internalRotate(context, 1);
     }
 
     @JRubyMethod(name = "rotate", compat = RUBY1_9)
     public IRubyObject rotate(ThreadContext context, IRubyObject cnt) {
         return internalRotate(context, RubyNumeric.fix2int(cnt));
     }
 
 
     // Enumerable direct implementations (non-"each" versions)
     public IRubyObject all_p(ThreadContext context, Block block) {
         if (!isBuiltin("each")) return RubyEnumerable.all_pCommon(context, this, block);
         if (!block.isGiven()) return all_pBlockless(context);
 
         for (int i = 0; i < realLength; i++) {
             if (!block.yield(context, eltOk(i)).isTrue()) return context.runtime.getFalse();
         }
 
         return context.runtime.getTrue();
     }
 
     private IRubyObject all_pBlockless(ThreadContext context) {
         for (int i = 0; i < realLength; i++) {
             if (!eltOk(i).isTrue()) return context.runtime.getFalse();
         }
 
         return context.runtime.getTrue();
     }
 
     public IRubyObject any_p(ThreadContext context, Block block) {
         if (!isBuiltin("each")) return RubyEnumerable.any_pCommon(context, this, block);
         if (!block.isGiven()) return any_pBlockless(context);
 
         for (int i = 0; i < realLength; i++) {
             if (block.yield(context, eltOk(i)).isTrue()) return context.runtime.getTrue();
         }
 
         return context.runtime.getFalse();
     }
 
     private IRubyObject any_pBlockless(ThreadContext context) {
         for (int i = 0; i < realLength; i++) {
             if (eltOk(i).isTrue()) return context.runtime.getTrue();
         }
 
         return context.runtime.getFalse();
     }
 
     public IRubyObject find(ThreadContext context, IRubyObject ifnone, Block block) {
         if (!isBuiltin("each")) return RubyEnumerable.detectCommon(context, this, block);
 
         return detectCommon(context, ifnone, block);
     }
 
     public IRubyObject find_index(ThreadContext context, Block block) {
         if (!isBuiltin("each")) return RubyEnumerable.find_indexCommon(context, this, block);
 
         for (int i = 0; i < realLength; i++) {
             if (block.yield(context, eltOk(i)).isTrue()) return context.runtime.newFixnum(i);
         }
 
         return context.runtime.getNil();
     }
 
     public IRubyObject find_index(ThreadContext context, IRubyObject cond) {
         if (!isBuiltin("each")) return RubyEnumerable.find_indexCommon(context, this, cond);
 
         for (int i = 0; i < realLength; i++) {
             if (eltOk(i).equals(cond)) return context.runtime.newFixnum(i);
         }
 
         return context.runtime.getNil();
     }
 
     public IRubyObject detectCommon(ThreadContext context, IRubyObject ifnone, Block block) {
         for (int i = 0; i < realLength; i++) {
             IRubyObject value = eltOk(i);
 
             if (block.yield(context, value).isTrue()) return value;
         }
 
         return ifnone != null ? ifnone.callMethod(context, "call") : 
             context.runtime.getNil();
     }
 
     public static void marshalTo(RubyArray array, MarshalStream output) throws IOException {
         output.registerLinkTarget(array);
         
         int length = array.realLength;
         int begin = array.begin;
         IRubyObject[] ary = array.values;
         
         output.writeInt(length);
         try {
             for (int i = 0; i < length; i++) {
                 output.dumpObject(ary[i + begin]);
             }
         } catch (ArrayIndexOutOfBoundsException aioob) {
             concurrentModification(array.getRuntime());
         }
     }
 
     public static RubyArray unmarshalFrom(UnmarshalStream input) throws IOException {
         int size = input.unmarshalInt();
         
         // we create this now with an empty, nulled array so it's available for links in the marshal data
         RubyArray result = input.getRuntime().newArray(size);
 
         input.registerLinkTarget(result);
 
         for (int i = 0; i < size; i++) {
             result.append(input.unmarshalObject());
         }
 
         return result;
     }
 
     @JRubyMethod(name = "try_convert", meta = true, compat = RUBY1_9)
     public static IRubyObject try_convert(ThreadContext context, IRubyObject self, IRubyObject arg) {
         return arg.checkArrayType();
     }
 
     /**
      * @see org.jruby.util.Pack#pack
      */
     @JRubyMethod(name = "pack", required = 1, compat = RUBY1_8)
     public RubyString pack(ThreadContext context, IRubyObject obj) {
         RubyString iFmt = obj.convertToString();
         try {
             return Pack.pack(getRuntime(), this, iFmt.getByteList(), iFmt.isTaint());
         } catch (ArrayIndexOutOfBoundsException aioob) {
             concurrentModification();
             return null; // not reached
         }
     }
 
     @JRubyMethod(name = "pack", required = 1, compat = RUBY1_9)
     public RubyString pack19(ThreadContext context, IRubyObject obj) {
         RubyString iFmt = obj.convertToString();
         try {
             return Pack.pack19(context, context.runtime, this, iFmt);
         } catch (ArrayIndexOutOfBoundsException aioob) {
             concurrentModification();
             return null; // not reached
         }
     }
 
     @Override
     public Class getJavaClass() {
         return List.class;
     }
     
     /**
      * Copy the values contained in this array into the target array at the specified offset.
      * It is expected that the target array is large enough to hold all necessary values.
      */
     public void copyInto(IRubyObject[] target, int start) {
         assert target.length - start >= realLength;
         safeArrayCopy(values, begin, target, start, realLength);
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
         // no catch for ArrayIndexOutOfBounds here because this impls a List method
         Object[] array = new Object[realLength];
         for (int i = 0; i < realLength; i++) {
             array[i] = values[i + begin].toJava(Object.class);
         }
         return array;
     }
 
     public Object[] toArray(final Object[] arg) {
         // no catch for ArrayIndexOutOfBounds here because this impls a List method
         Object[] array = arg;
         Class type = array.getClass().getComponentType();
         if (array.length < realLength) {
             array = (Object[]) Array.newInstance(type, realLength);
         }
         int length = realLength - begin;
 
         for (int i = 0; i < length; i++) {
             array[i] = values[i + begin].toJava(type);
         }
         return array;
     }
 
     @Override
     public Object toJava(Class target) {
         if (target.isArray()) {
             Class type = target.getComponentType();
             Object rawJavaArray = Array.newInstance(type, realLength);
             try {
                 ArrayUtils.copyDataToJavaArrayDirect(getRuntime().getCurrentContext(), this, rawJavaArray);
             } catch (ArrayIndexOutOfBoundsException aioob) {
                 concurrentModification();
             }
             return rawJavaArray;
         } else {
             return super.toJava(target);
         }
     }
 
     public boolean add(Object element) {
         append(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element));
         return true;
     }
 
     public boolean remove(Object element) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         IRubyObject item = JavaUtil.convertJavaToUsableRubyObject(runtime, element);
         boolean listchanged = false;
 
         for (int i1 = 0; i1 < realLength; i1++) {
             IRubyObject e = values[begin + i1];
             if (equalInternal(context, e, item)) {
                 delete_at(i1);
                 listchanged = true;
                 break;
             }
         }
 
         return listchanged;
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
             IRubyObject deleted = delete(getRuntime().getCurrentContext(), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), iter.next()), Block.NULL_BLOCK);
             if (!deleted.isNil()) {
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
         Object previous = elt(index).toJava(Object.class);
         store(index, JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element));
         return previous;
     }
 
     // TODO: make more efficient by not creating IRubyArray[]
     public void add(int index, Object element) {
         insert(new IRubyObject[]{RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element)});
     }
 
     public Object remove(int index) {
         return delete_at(index).toJava(Object.class);
     }
 
     public int indexOf(Object element) {
         int myBegin = this.begin;
 
         if (element != null) {
             IRubyObject convertedElement = JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element);
 
             for (int i = myBegin; i < myBegin + realLength; i++) {
                 if (convertedElement.equals(values[i])) {
                     return i;
                 }
             }
         }
         return -1;
     }
 
     public int lastIndexOf(Object element) {
         int myBegin = this.begin;
 
         if (element != null) {
             IRubyObject convertedElement = JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element);
 
             for (int i = myBegin + realLength - 1; i >= myBegin; i--) {
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
 
     private IRubyObject safeArrayRef(IRubyObject[] values, int i) {
         return safeArrayRef(getRuntime(), values, i);
     }
 
     private IRubyObject safeArrayRef(Ruby runtime, IRubyObject[] values, int i) {
         try {
             return values[i];
         } catch (ArrayIndexOutOfBoundsException x) {
             concurrentModification(runtime);
             return null; // not reached
         }
     }
 
     private IRubyObject safeArraySet(IRubyObject[] values, int i, IRubyObject value) {
         return safeArraySet(getRuntime(), values, i, value);
     }
 
     private static IRubyObject safeArraySet(Ruby runtime, IRubyObject[] values, int i, IRubyObject value) {
         try {
             return values[i] = value;
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification(runtime);
             return null; // not reached
         }
     }
 
     private IRubyObject safeArrayRefSet(IRubyObject[] values, int i, IRubyObject value) {
         return safeArrayRefSet(getRuntime(), values, i, value);
     }
 
     private static IRubyObject safeArrayRefSet(Ruby runtime, IRubyObject[] values, int i, IRubyObject value) {
         try {
             IRubyObject tmp = values[i];
             values[i] = value;
             return tmp;
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification(runtime);
             return null; // not reached
         }
     }
 
     private IRubyObject safeArrayRefCondSet(IRubyObject[] values, int i, boolean doSet, IRubyObject value) {
         return safeArrayRefCondSet(getRuntime(), values, i, doSet, value);
     }
 
     private static IRubyObject safeArrayRefCondSet(Ruby runtime, IRubyObject[] values, int i, boolean doSet, IRubyObject value) {
         try {
             IRubyObject tmp = values[i];
             if (doSet) values[i] = value;
             return tmp;
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification(runtime);
             return null; // not reached
         }
     }
 
     private void safeArrayCopy(IRubyObject[] source, int sourceStart, IRubyObject[] target, int targetStart, int length) {
         safeArrayCopy(getRuntime(), source, sourceStart, target, targetStart, length);
     }
 
     private void safeArrayCopy(Ruby runtime, IRubyObject[] source, int sourceStart, IRubyObject[] target, int targetStart, int length) {
         try {
             System.arraycopy(source, sourceStart, target, targetStart, length);
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification(runtime);
         }
         // not reached
     }
 }