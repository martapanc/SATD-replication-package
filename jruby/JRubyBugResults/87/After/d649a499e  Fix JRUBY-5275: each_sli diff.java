diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index 92082770a4..61f5e325ac 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -695,2001 +695,2001 @@ public class RubyArray extends RubyObject implements List {
     public RubyFixnum hash19(final ThreadContext context) {
         return (RubyFixnum)getRuntime().execRecursiveOuter(new Ruby.RecursiveFunction() {
                 public IRubyObject call(IRubyObject obj, boolean recur) {
                     int begin = RubyArray.this.begin;
                     long h = realLength;
                     if(recur) {
                         h ^= RubyNumeric.num2long(getRuntime().getArray().callMethod(context, "hash"));
                     } else {
                         for(int i = begin; i < begin + realLength; i++) {
                             h = (h << 1) | (h < 0 ? 1 : 0);
                             final IRubyObject value;
                             try {
                                 value = values[i];
                             } catch (ArrayIndexOutOfBoundsException e) {
                                 concurrentModification();
                                 continue;
                             }
                             h ^= RubyNumeric.num2long(value.callMethod(context, "hash"));
                         }
                     }
                     return getRuntime().newFixnum(h);
                 }
             }, this);
     }
 
     /** rb_ary_store
      *
      */
     public final IRubyObject store(long index, IRubyObject value) {
         if (index < 0 && (index += realLength) < 0) throw getRuntime().newIndexError("index " + (index - realLength) + " out of array");
 
         modify();
 
         if (index >= realLength) {
             int valuesLength = values.length - begin;
             if (index >= valuesLength) storeRealloc(index, valuesLength);
             realLength = (int) index + 1;
         }
 
         try {
             values[begin + (int) index] = value;
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         return value;
     }
 
     private void storeRealloc(long index, int valuesLength) {
         long newLength = valuesLength >> 1;
 
         if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
 
         newLength += index;
         if (index >= Integer.MAX_VALUE || newLength >= Integer.MAX_VALUE) {
             throw getRuntime().newArgumentError("index too big");
         }
         realloc((int) newLength, valuesLength);
     }
 
     /** rb_ary_elt
      *
      */
     private final IRubyObject elt(long offset) {
         if (offset < 0 || offset >= realLength) {
             return getRuntime().getNil();
         }
         return eltOk(offset);
     }
 
     private final IRubyObject eltOk(long offset) {
         try {
             return values[begin + (int)offset];
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
             return getRuntime().getNil();
         }
     }
 
     /** rb_ary_entry
      *
      */
     public final IRubyObject entry(long offset) {
         return (offset < 0 ) ? elt(offset + realLength) : elt(offset);
     }
 
     public final IRubyObject entry(int offset) {
         return (offset < 0 ) ? elt(offset + realLength) : elt(offset);
     }
 
     public final IRubyObject eltInternal(int offset) {
         return values[begin + offset];
     }
     
     public final IRubyObject eltInternalSet(int offset, IRubyObject item) {
         return values[begin + offset] = item;
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * @deprecated Use the versions with zero, one, or two args.
      */
     public IRubyObject fetch(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1:
             return fetch(context, args[0], block);
         case 2:
             return fetch(context, args[0], args[1], block);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 1, 2);
             return null; // not reached
         }
     }    
 
     /** rb_ary_fetch
      *
      */
     @JRubyMethod
     public IRubyObject fetch(ThreadContext context, IRubyObject arg0, Block block) {
         long index = RubyNumeric.num2long(arg0);
 
         if (index < 0) index += realLength;
         if (index < 0 || index >= realLength) {
             if (block.isGiven()) return block.yield(context, arg0);
             throw getRuntime().newIndexError("index " + index + " out of array");
         }
         
         try {
             return values[begin + (int) index];
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
             return getRuntime().getNil();
         }
     }
 
     /** rb_ary_fetch
     *
     */
    @JRubyMethod
    public IRubyObject fetch(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
        if (block.isGiven()) getRuntime().getWarnings().warn(ID.BLOCK_BEATS_DEFAULT_VALUE, "block supersedes default value argument");
 
        long index = RubyNumeric.num2long(arg0);
 
        if (index < 0) index += realLength;
        if (index < 0 || index >= realLength) {
            if (block.isGiven()) return block.yield(context, arg0);
            return arg1;
        }
        
        try {
            return values[begin + (int) index];
        } catch (ArrayIndexOutOfBoundsException e) {
            concurrentModification();
            return getRuntime().getNil();
        }
    }    
 
     /** rb_ary_to_ary
      * 
      */
     private static RubyArray aryToAry(IRubyObject obj) {
         if (obj instanceof RubyArray) return (RubyArray) obj;
 
         if (obj.respondsTo("to_ary")) return obj.convertToArray();
 
         RubyArray arr = new RubyArray(obj.getRuntime(), false); // possibly should not in object space
         arr.values = new IRubyObject[]{obj};
         arr.realLength = 1;
         return arr;
     }
 
     /** rb_ary_splice
      * 
      */
     private final void splice(long beg, long len, IRubyObject rpl, boolean oneNine) {
         if (len < 0) throw getRuntime().newIndexError("negative length (" + len + ")");
         if (beg < 0 && (beg += realLength) < 0) throw getRuntime().newIndexError("index " + (beg - realLength) + " out of array");
 
         final RubyArray rplArr;
         final int rlen;
 
         if (rpl == null || (rpl.isNil() && !oneNine)) {
             rplArr = null;
             rlen = 0;
         } else if (rpl.isNil()) {
             // 1.9 replaces with nil
             rplArr = newArray(getRuntime(), rpl);
             rlen = 1;
         } else {
             rplArr = aryToAry(rpl);
             rlen = rplArr.realLength;
         }
 
         modify();
 
         int valuesLength = values.length - begin;
         if (beg >= realLength) {
             len = beg + rlen;
             if (len >= valuesLength) spliceRealloc((int)len, valuesLength);
             try {
                 RuntimeHelpers.fillNil(values, begin + realLength, begin + ((int)beg), getRuntime());
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
             }
             realLength = (int) len;
         } else {
             if (beg + len > realLength) len = realLength - beg;
             int alen = realLength + rlen - (int)len;
             if (alen >= valuesLength) spliceRealloc(alen, valuesLength);
 
             if (len != rlen) {
                 try {
                     System.arraycopy(values, begin + (int) (beg + len), values, begin + (int) beg + rlen, realLength - (int) (beg + len));
                 } catch (ArrayIndexOutOfBoundsException e) {
                     concurrentModification();
                 }
                 realLength = alen;
             }
         }
 
         if (rlen > 0) {
             try {
                 System.arraycopy(rplArr.values, rplArr.begin, values, begin + (int) beg, rlen);
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
             }
         }
     }
 
     /** rb_ary_splice
      * 
      */
     private final void spliceOne(long beg, IRubyObject rpl) {
         if (beg < 0 && (beg += realLength) < 0) throw getRuntime().newIndexError("index " + (beg - realLength) + " out of array");
 
         modify();
 
         int valuesLength = values.length - begin;
         if (beg >= realLength) {
             int len = (int)beg + 1;
             if (len >= valuesLength) spliceRealloc((int)len, valuesLength);
             RuntimeHelpers.fillNil(values, begin + realLength, begin + ((int)beg), getRuntime());
             realLength = (int) len;
         } else {
             int len = beg > realLength ? realLength - (int)beg : 0;
             int alen = realLength + 1 - len;
             if (alen >= valuesLength) spliceRealloc((int)alen, valuesLength);
 
             if (len == 0) {
                 try {
                     System.arraycopy(values, begin + (int) beg, values, begin + (int) beg + 1, realLength - (int) beg);
                 } catch (ArrayIndexOutOfBoundsException e) {
                     concurrentModification();
                 }
                 realLength = alen;
             }
         }
 
         try {
             values[begin + (int)beg] = rpl;
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
     }
 
     private void spliceRealloc(int length, int valuesLength) {
         int tryLength = valuesLength + (valuesLength >> 1);
         int len = length > tryLength ? length : tryLength;
         IRubyObject[] vals = new IRubyObject[len];
         System.arraycopy(values, begin, vals, 0, realLength);
         
         // only fill if there actually will remain trailing storage
         if (len > length) RuntimeHelpers.fillNil(vals, length, len, getRuntime());
         begin = 0;
         values = vals;
     }
 
     @JRubyMethod
     public IRubyObject insert() {
         throw getRuntime().newArgumentError(0, 1);
     }
 
     /** rb_ary_insert
      * 
      */
     @JRubyMethod(name = "insert", compat = RUBY1_8)
     public IRubyObject insert(IRubyObject arg) {
         return this;
     }
     
     @JRubyMethod(name = "insert", compat = RUBY1_9)
     public IRubyObject insert19(IRubyObject arg) {
         modifyCheck();
         
         return insert(arg);
     }
 
     @JRubyMethod(name = "insert", compat = RUBY1_8)
     public IRubyObject insert(IRubyObject arg1, IRubyObject arg2) {
         long pos = RubyNumeric.num2long(arg1);
 
         if (pos == -1) pos = realLength;
         if (pos < 0) pos++;
         
         spliceOne(pos, arg2); // rb_ary_new4
         
         return this;
     }
 
     @JRubyMethod(name = "insert", compat = RUBY1_9)
     public IRubyObject insert19(IRubyObject arg1, IRubyObject arg2) {
         modifyCheck();
 
         return insert(arg1, arg2);
     }
 
     @JRubyMethod(name = "insert", required = 1, rest = true, compat = RUBY1_8)
     public IRubyObject insert(IRubyObject[] args) {
         if (args.length == 1) return this;
 
         long pos = RubyNumeric.num2long(args[0]);
 
         if (pos == -1) pos = realLength;
         if (pos < 0) pos++;
 
         RubyArray inserted = new RubyArray(getRuntime(), false);
         inserted.values = args;
         inserted.begin = 1;
         inserted.realLength = args.length - 1;
         
         splice(pos, 0, inserted, false); // rb_ary_new4
         
         return this;
     }
 
     @JRubyMethod(name = "insert", required = 1, rest = true, compat = RUBY1_9)
     public IRubyObject insert19(IRubyObject[] args) {
         modifyCheck();
 
         return insert(args);
     }
 
     /** rb_ary_dup
      * 
      */
     public final RubyArray aryDup() {
         RubyArray dup = new RubyArray(getRuntime(), getMetaClass(), this);
         dup.flags |= flags & TAINTED_F; // from DUP_SETUP
         dup.flags |= flags & UNTRUSTED_F;
         // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
         return dup;
     }
 
     /** rb_ary_transpose
      * 
      */
     @JRubyMethod(name = "transpose")
     public RubyArray transpose() {
         RubyArray tmp, result = null;
 
         int alen = realLength;
         if (alen == 0) return aryDup();
     
         Ruby runtime = getRuntime();
         int elen = -1;
         int end = begin + alen;
         for (int i = begin; i < end; i++) {
             tmp = elt(i).convertToArray();
             if (elen < 0) {
                 elen = tmp.realLength;
                 result = new RubyArray(runtime, elen);
                 for (int j = 0; j < elen; j++) {
                     result.store(j, new RubyArray(runtime, alen));
                 }
             } else if (elen != tmp.realLength) {
                 throw runtime.newIndexError("element size differs (" + tmp.realLength
                         + " should be " + elen + ")");
             }
             for (int j = 0; j < elen; j++) {
                 ((RubyArray) result.elt(j)).store(i - begin, tmp.elt(j));
             }
         }
         return result;
     }
 
     /** rb_values_at (internal)
      * 
      */
     private final IRubyObject values_at(long olen, IRubyObject[] args) {
         RubyArray result = new RubyArray(getRuntime(), args.length);
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] instanceof RubyFixnum) {
                 result.append(entry(((RubyFixnum)args[i]).getLongValue()));
                 continue;
             }
 
             long beglen[];
             if (!(args[i] instanceof RubyRange)) {
             } else if ((beglen = ((RubyRange) args[i]).begLen(olen, 0)) == null) {
                 continue;
             } else {
                 int beg = (int) beglen[0];
                 int len = (int) beglen[1];
                 int end = begin + len;
                 for (int j = begin; j < end; j++) {
                     result.append(entry(j + beg));
                 }
                 continue;
             }
             result.append(entry(RubyNumeric.num2long(args[i])));
         }
 
         RuntimeHelpers.fillNil(result.values, result.realLength, result.values.length, getRuntime());
         return result;
     }
 
     /** rb_values_at
      * 
      */
     @JRubyMethod(name = "values_at", rest = true)
     public IRubyObject values_at(IRubyObject[] args) {
         return values_at(realLength, args);
     }
 
     /** rb_ary_subseq
      *
      */
     public IRubyObject subseq(long beg, long len) {
         if (beg > realLength || beg < 0 || len < 0) return getRuntime().getNil();
 
         if (beg + len > realLength) {
             len = realLength - beg;
             
             if (len < 0) len = 0;
         }
         
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), IRubyObject.NULL_ARRAY);
 
         return makeShared(begin + (int) beg, (int) len, getMetaClass());
     }
 
     /** rb_ary_subseq
      *
      */
     public IRubyObject subseqLight(long beg, long len) {
         Ruby runtime = getRuntime();
         if (beg > realLength || beg < 0 || len < 0) return runtime.getNil();
 
         if (beg + len > realLength) {
             len = realLength - beg;
             if (len < 0) len = 0;
         }
 
         if (len == 0) return new RubyArray(runtime, getMetaClass(), IRubyObject.NULL_ARRAY, false);
         return makeShared(begin + (int) beg, (int) len, new RubyArray(runtime, getMetaClass(), false));
     }
 
     /** rb_ary_length
      *
      */
     @JRubyMethod(name = "length", alias = "size")
     public RubyFixnum length() {
         return getRuntime().newFixnum(realLength);
     }
 
     /** rb_ary_push - specialized rb_ary_store 
      *
      */
     @JRubyMethod(name = "<<", required = 1)
     public RubyArray append(IRubyObject item) {
         modify();
         int valuesLength = values.length - begin;
         if (realLength == valuesLength) {
             if (realLength == Integer.MAX_VALUE) throw getRuntime().newArgumentError("index too big");
 
             long newLength = valuesLength + (valuesLength >> 1);
             if (newLength > Integer.MAX_VALUE) {
                 newLength = Integer.MAX_VALUE;
             } else if (newLength < ARRAY_DEFAULT_SIZE) {
                 newLength = ARRAY_DEFAULT_SIZE;
             }
 
             realloc((int) newLength, valuesLength);
         }
         
         try {
             values[begin + realLength++] = item;
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         return this;
     }
 
     /** rb_ary_push_m - instance method push
      *
      */
     @JRubyMethod(name = "push", rest = true, compat = RUBY1_8)
     public RubyArray push_m(IRubyObject[] items) {
         for (int i = 0; i < items.length; i++) {
             append(items[i]);
         }
         
         return this;
     }
 
     @JRubyMethod(name = "push", rest = true, compat = RUBY1_9)
     public RubyArray push_m19(IRubyObject[] items) {
         modifyCheck();
 
         return push_m(items);
     }
 
     /** rb_ary_pop
      *
      */
     @JRubyMethod
     public IRubyObject pop(ThreadContext context) {
         modifyCheck();
 
         if (realLength == 0) return context.getRuntime().getNil();
 
         try {
             if (isShared) {
                 return values[begin + --realLength];
             } else {
                 int index = begin + --realLength;
                 final IRubyObject obj = values[index];
                 values[index] = context.getRuntime().getNil();
                 return obj;
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
             return context.getRuntime().getNil();
         }
     }
 
     @JRubyMethod
     public IRubyObject pop(ThreadContext context, IRubyObject num) {
         modifyCheck();
         RubyArray result = makeSharedFirst(context, num, true, context.getRuntime().getArray());
         realLength -= result.realLength;
         return result;
     }
     
     /** rb_ary_shift
      *
      */
     @JRubyMethod(name = "shift")
     public IRubyObject shift(ThreadContext context) {
         modifyCheck();
         Ruby runtime = context.getRuntime();
         if (realLength == 0) return runtime.getNil();
 
         final IRubyObject obj;
         try {
             obj = values[begin];
             if (!isShared) values[begin] = runtime.getNil();
             begin++;
             realLength--;
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
             return runtime.getNil();
         }
         return obj;
         
     }    
 
     @JRubyMethod(name = "shift")
     public IRubyObject shift(ThreadContext context, IRubyObject num) {
         modify();
 
         RubyArray result = makeSharedFirst(context, num, false, context.getRuntime().getArray());
 
         int n = result.realLength;
         begin += n;
         realLength -= n;
         return result;
     }
 
     @JRubyMethod(name = "unshift", compat = RUBY1_8)
     public IRubyObject unshift() {
         return this;
     }
 
     @JRubyMethod(name = "unshift", compat = RUBY1_9)
     public IRubyObject unshift19() {
         modifyCheck();
 
         return this;
 
     }
 
     /** rb_ary_unshift
      *
      */
     @JRubyMethod(name = "unshift", compat = RUBY1_8)
     public IRubyObject unshift(IRubyObject item) {
         if (begin == 0 || isShared) {
             modify();
             int valuesLength = values.length - begin;
             if (realLength == valuesLength) {
                 int newLength = valuesLength >> 1;
                 if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
     
                 newLength += valuesLength;
                 IRubyObject[]vals = new IRubyObject[newLength];
                 try {
                     System.arraycopy(values, begin, vals, 1, realLength);
                     RuntimeHelpers.fillNil(vals, realLength + 1, newLength, getRuntime());
                 } catch (ArrayIndexOutOfBoundsException e) {
                     concurrentModification();
                 }
                 values = vals;
                 begin = 0;
             } else {
                 try {
                     System.arraycopy(values, begin, values, begin + 1, realLength);
                 } catch (ArrayIndexOutOfBoundsException e) {
                     concurrentModification();
                 }
             }
         } else {
             modifyCheck();
             begin--;
         }
         realLength++;
         values[begin] = item;
         return this;
     }
 
     @JRubyMethod(name = "unshift", compat = RUBY1_9)
     public IRubyObject unshift19(IRubyObject item) {
         modifyCheck();
 
         return unshift(item);
     }
 
     @JRubyMethod(name = "unshift", rest = true, compat = RUBY1_8)
     public IRubyObject unshift(IRubyObject[] items) {
         long len = realLength;
         if (items.length == 0) return this;
 
         store(len + items.length - 1, getRuntime().getNil());
 
         try {
             System.arraycopy(values, begin, values, begin + items.length, (int) len);
             System.arraycopy(items, 0, values, begin, items.length);
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         
         return this;
     }
 
     @JRubyMethod(name = "unshift", rest = true, compat = RUBY1_9)
     public IRubyObject unshift19(IRubyObject[] items) {
         modifyCheck();
 
         return unshift(items);
     }
 
     /** rb_ary_includes
      * 
      */
     @JRubyMethod(name = "include?", required = 1)
     public RubyBoolean include_p(ThreadContext context, IRubyObject item) {
         return context.getRuntime().newBoolean(includes(context, item));
     }
 
     /** rb_ary_frozen_p
      *
      */
     @JRubyMethod(name = "frozen?")
     @Override
     public RubyBoolean frozen_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isFrozen() || (flags & TMPLOCK_ARR_F) != 0);
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * @deprecated Use the versions with zero, one, or two args.
      */
     public IRubyObject aref(IRubyObject[] args) {
         switch (args.length) {
         case 1:
             return aref(args[0]);
         case 2:
             return aref(args[0], args[1]);
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
         final boolean specificArity = block.arity().isFixed() && block.arity().required() != 1;
         
         for (; localRealLength >= size; localRealLength -= size) {
             block.yield(context, window);
             if (specificArity) { // array is never exposed to ruby, just use for yielding
                 window.begin += localBegin += size;
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
-        return block.isGiven() ? eachSlice(context, size, block) : enumeratorize(context.getRuntime(), this, "each_slice");
+        return block.isGiven() ? eachSlice(context, size, block) : enumeratorize(context.getRuntime(), this, "each_slice", arg);
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
             block.yield(context, values[begin + len]);
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
             IRubyObject value;
             try {
                 value = values[i];
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
             len += sepBytes.getRealSize() * (realLength - 1);
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
 
     @JRubyMethod(name = "join", compat = RUBY1_8)
     public IRubyObject join(ThreadContext context) {
         return join(context, context.getRuntime().getGlobalVariables().get("$,"));
     }
 
     private boolean[] join0(ThreadContext context, ByteList sep, int max, ByteList result) {
         boolean t = false;
         boolean u = false;
         for(int i = begin; i < max; i++) {
             IRubyObject val;
             try {
                 val = values[i];
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
                 return new boolean[] {t,u};
             }
 
             if(i > begin && sep != null) {
                 result.append(sep);
             }
             
             result.append(((RubyString)val).getByteList());
             if(val.isTaint()) {
                 t = true;
             }
             if(val.isUntrusted()) {
                 u = true;
             }
         }
         return new boolean[]{t,u};
     }
 
     private void join1(final ThreadContext context, IRubyObject obj, final ByteList sep, int i, final ByteList result) {
         for(; i < begin + realLength; i++) {
             if(i > begin && sep != null) {
                 result.append(sep);
             }
 
             IRubyObject val;
             try {
                 val = values[i];
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
                 return;
             }
 
             if(val instanceof RubyString) {
                 result.append(((RubyString)val).getByteList());
             } else if(val instanceof RubyArray) {
                 obj = val;
                 if(val == this) {
                     throw getRuntime().newArgumentError("recursive array join");
                 } else {
                     final RubyArray ary = (RubyArray)val;
                     final IRubyObject outobj = obj;
                     getRuntime().execRecursive(new Ruby.RecursiveFunction() {
                             public IRubyObject call(IRubyObject obj, boolean recur) {
                                 if(recur) {
                                     throw getRuntime().newArgumentError("recursive array join");
                                 } else {
                                     ((RubyArray)ary).join1(context, outobj, sep, 0, result);
                                 }
                                 return getRuntime().getNil();
                             }
                         }, obj);
                 }
             } else {
                 IRubyObject tmp = val.checkStringType19();
                 if(!tmp.isNil()) {
                     val = tmp;
                     result.append(((RubyString)val).getByteList());
                 } else {
                     tmp = TypeConverter.convertToTypeWithCheck(val, getRuntime().getArray(), "to_a");
                     if(!tmp.isNil()) {
                         obj = val;
                         val = tmp;
                         if(val == this) {
                             throw getRuntime().newArgumentError("recursive array join");
                         } else {
                             final RubyArray ary = (RubyArray)val;
                             final IRubyObject outobj = obj;
                             getRuntime().execRecursive(new Ruby.RecursiveFunction() {
                                     public IRubyObject call(IRubyObject obj, boolean recur) {
                                         if(recur) {
                                             throw getRuntime().newArgumentError("recursive array join");
                                         } else {
                                             ((RubyArray)ary).join1(context, outobj, sep, 0, result);
                                         }
                                         return getRuntime().getNil();
                                     }
                                 }, obj);
                         }
                     } else {
                         val = RubyString.objAsString(context, val);
                         result.append(((RubyString)val).getByteList());
                     }
                 }
             }
         }
     }
 
     /** rb_ary_join
      *
      */
     @JRubyMethod(name = "join", compat = RUBY1_9)
     public IRubyObject join19(ThreadContext context, IRubyObject sep) {
         final Ruby runtime = context.getRuntime();
         if (realLength == 0) return RubyString.newEmptyString(runtime);
 
         boolean taint = isTaint() || sep.isTaint();
         boolean untrusted = isUntrusted() || sep.isUntrusted();
 
         int len = 1;
         ByteList sepBytes = null;
         if (!sep.isNil()) {
             sepBytes = sep.convertToString().getByteList();
             len += sepBytes.getRealSize() * (realLength - 1);
         }
 
         for (int i = begin; i < begin + realLength; i++) {            
             IRubyObject val;
             try {
                 val = values[i];
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
                 return RubyString.newEmptyString(runtime);
             }
             IRubyObject tmp = val.checkStringType19();
             if(tmp.isNil() || tmp != val) {
                 ByteList buf = new ByteList(len + ((begin + realLength) - i) * 10);
                 boolean[] tu = join0(context, sepBytes, i, buf);
                 join1(context, this, sepBytes, i, buf);
                 RubyString result = runtime.newString(buf); 
                 if (taint || tu[0]) result.setTaint(true);
                 if (untrusted || tu[1]) result.untrust(context);
                 return result;
             }
 
             len += ((RubyString) tmp).getByteList().length();
         }
 
         ByteList buf = new ByteList(len);
         boolean[] tu = join0(context, sepBytes, begin + realLength, buf);
 
         RubyString result = runtime.newString(buf); 
         if (taint || tu[0]) result.setTaint(true);
         if (untrusted || tu[1]) result.untrust(context);
         
         return result;
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
         return RecursiveComparator.compare(context, "==", this, obj, null);
     }
 
     public RubyBoolean compare(ThreadContext context, String method,
             IRubyObject other, Set<RecursiveComparator.Pair> seen) {
 
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
 
     @JRubyMethod(name = {"index", "find_index"})
     public IRubyObject index(ThreadContext context, IRubyObject obj, Block unused) {
         if (unused.isGiven()) context.getRuntime().getWarnings().warn(ID.BLOCK_UNUSED, "given block not used");
         return index(context, obj); 
     }
 
     @JRubyMethod(name = {"index", "find_index"})
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
             arr[i] = block.yield(context, values[i + begin]);
         }
 
         return new RubyArray(runtime, arr);
     }
 
     @JRubyMethod(name = {"collect", "map"}, compat = RUBY1_9)
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
             IRubyObject value = values[begin + i];
 
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
         if (!block.isGiven()) {
             return enumeratorize(runtime, this, "select!");
         }
 
         int j = 0;
         IRubyObject[] aux = new IRubyObject[values.length];
         for (int i = 0; i < realLength; i++) {
             IRubyObject value = values[begin + i];
             if (!block.yield(context, value).isTrue()) {
                 continue;
             }
             if (i != j) {
                 aux[begin + j] = value;
             }
             j++;
         }
 
         if (realLength == j) {
             return runtime.getNil();
         }
         if (j < realLength) {
             System.arraycopy(aux, begin, values, begin, j);
             realLength = j;
         }
 
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
 
         final int myRealLength = this.realLength;
         final int myBegin = this.begin;
         final IRubyObject[] myValues = this.values;
         if (myRealLength > i2) {
             try {
                 RuntimeHelpers.fillNil(myValues, myBegin + i2, myBegin + myRealLength, context.getRuntime());
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
             }
             this.realLength = i2;
             int valuesLength = myValues.length - myBegin;
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
                 tmp[0] = values[begin + i];
                 for (int j = 0; j < args.length; j++) {
                     tmp[j + 1] = ((RubyArray) args[j]).elt(i);
                 }
                 block.yield(context, newArrayNoCopyLight(runtime, tmp));
             }
             return runtime.getNil();
         }
 
         IRubyObject[] result = new IRubyObject[realLength];
         for (int i = 0; i < realLength; i++) {
             IRubyObject[] tmp = new IRubyObject[args.length + 1];
             tmp[0] = values[begin + i];
             for (int j = 0; j < args.length; j++) {
                 tmp[j + 1] = ((RubyArray) args[j]).elt(i);
             }
             result[i] = newArrayNoCopyLight(runtime, tmp);
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
