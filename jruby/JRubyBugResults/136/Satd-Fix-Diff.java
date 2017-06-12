diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index e17ae1f08f..5787e7426c 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -175,2002 +175,2002 @@ public class RubyArray extends RubyObject implements List {
         return newArrayLight(runtime, ARRAY_DEFAULT_SIZE);
     }
 
     public static RubyArray newArray(Ruby runtime, IRubyObject obj) {
         return new RubyArray(runtime, new IRubyObject[] { obj });
     }
 
     public static RubyArray newArrayLight(Ruby runtime, IRubyObject obj) {
         return new RubyArray(runtime, new IRubyObject[] { obj }, false);
     }
 
     public static RubyArray newArrayLight(Ruby runtime, IRubyObject... objs) {
         return new RubyArray(runtime, objs, false);
     }
 
     /** rb_assoc_new
      *
      */
     public static RubyArray newArray(Ruby runtime, IRubyObject car, IRubyObject cdr) {
         return new RubyArray(runtime, new IRubyObject[] { car, cdr });
     }
     
     public static RubyArray newEmptyArray(Ruby runtime) {
         return new RubyArray(runtime, NULL_ARRAY);
     }
 
     /** rb_ary_new4, rb_ary_new3
      *   
      */
     public static RubyArray newArray(Ruby runtime, IRubyObject[] args) {
         RubyArray arr = new RubyArray(runtime, new IRubyObject[args.length]);
         System.arraycopy(args, 0, arr.values, 0, args.length);
         arr.realLength = args.length;
         return arr;
     }
     
     public static RubyArray newArrayNoCopy(Ruby runtime, IRubyObject[] args) {
         return new RubyArray(runtime, args);
     }
     
     public static RubyArray newArrayNoCopy(Ruby runtime, IRubyObject[] args, int begin) {
         return new RubyArray(runtime, args, begin);
     }
 
     public static RubyArray newArrayNoCopy(Ruby runtime, IRubyObject[] args, int begin, int length) {
         assert begin >= 0 : "begin must be >= 0";
         assert length >= 0 : "length must be >= 0";
         
         return new RubyArray(runtime, args, begin, length);
     }
 
     public static RubyArray newArrayNoCopyLight(Ruby runtime, IRubyObject[] args) {
         RubyArray arr = new RubyArray(runtime, false);
         arr.values = args;
         arr.realLength = args.length;
         return arr;
     }
 
     public static RubyArray newArray(Ruby runtime, Collection<? extends IRubyObject> collection) {
         return new RubyArray(runtime, collection.toArray(new IRubyObject[collection.size()]));
     }
 
     public static final int ARRAY_DEFAULT_SIZE = 16;    
 
     // volatile to ensure that initial nil-fill is visible to other threads
     private volatile IRubyObject[] values;
 
     private static final int TMPLOCK_ARR_F = 1 << 9;
     private static final int TMPLOCK_OR_FROZEN_ARR_F = TMPLOCK_ARR_F | FROZEN_F;
 
     private volatile boolean isShared = false;
     private int begin = 0;
     private int realLength = 0;
 
     /* 
      * plain internal array assignment
      */
     private RubyArray(Ruby runtime, IRubyObject[] vals) {
         super(runtime, runtime.getArray());
         values = vals;
         realLength = vals.length;
     }
 
     /* 
      * plain internal array assignment
      */
     private RubyArray(Ruby runtime, IRubyObject[] vals, boolean objectSpace) {
         super(runtime, runtime.getArray(), objectSpace);
         values = vals;
         realLength = vals.length;
     }
 
     /* 
      * plain internal array assignment
      */
     private RubyArray(Ruby runtime, IRubyObject[] vals, int begin) {
         super(runtime, runtime.getArray());
         this.values = vals;
         this.begin = begin;
         this.realLength = vals.length - begin;
         this.isShared = true;
     }
 
     private RubyArray(Ruby runtime, IRubyObject[] vals, int begin, int length) {
         super(runtime, runtime.getArray());
         this.values = vals;
         this.begin = begin;
         this.realLength = length;
         this.isShared = true;
     }
     
     private RubyArray(Ruby runtime, int length) {
         super(runtime, runtime.getArray());
         values = new IRubyObject[length];
     }
 
     private RubyArray(Ruby runtime, int length, boolean objectspace) {
         super(runtime, runtime.getArray(), objectspace);
         values = new IRubyObject[length];
     }
 
     /* NEWOBJ and OBJSETUP equivalent
      * fastest one, for shared arrays, optional objectspace
      */
     private RubyArray(Ruby runtime, boolean objectSpace) {
         super(runtime, runtime.getArray(), objectSpace);
     }
 
     private RubyArray(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
     }
 
     /* Array constructors taking the MetaClass to fulfil MRI Array subclass behaviour
      * 
      */
     private RubyArray(Ruby runtime, RubyClass klass, int length) {
         super(runtime, klass);
         values = new IRubyObject[length];
     }
 
     private RubyArray(Ruby runtime, RubyClass klass, IRubyObject[]vals, boolean objectspace) {
         super(runtime, klass, objectspace);
         values = vals;
     }    
 
     private RubyArray(Ruby runtime, RubyClass klass, boolean objectSpace) {
         super(runtime, klass, objectSpace);
     }
     
     private RubyArray(Ruby runtime, RubyClass klass, RubyArray original) {
         super(runtime, klass);
         realLength = original.realLength;
         values = new IRubyObject[realLength];
         try {
             System.arraycopy(original.values, original.begin, values, 0, realLength);
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
     }
     
     private RubyArray(Ruby runtime, RubyClass klass, IRubyObject[] vals) {
         super(runtime, klass);
         values = vals;
         realLength = vals.length;
     }
 
     private void alloc(int length) {
         final IRubyObject[] newValues = new IRubyObject[length];
         RuntimeHelpers.fillNil(newValues, getRuntime());
         values = newValues;
         begin = 0;
     }
 
     private void realloc(int newLength, int valuesLength) {
         IRubyObject[] reallocated = new IRubyObject[newLength];
         try {
             if (newLength > valuesLength) {
                 RuntimeHelpers.fillNil(reallocated, valuesLength, newLength, getRuntime());
                 System.arraycopy(values, begin, reallocated, 0, valuesLength); // elements and trailing nils
             } else {
                 System.arraycopy(values, begin, reallocated, 0, newLength); // ???
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         begin = 0;
         values = reallocated;
     }
 
     private static void fill(IRubyObject[]arr, int from, int to, IRubyObject with) {
         for (int i=from; i<to; i++) {
             arr[i] = with;
         }
     }
 
     private static final void checkLength(Ruby runtime, long length) {
         if (length < 0) {
             throw runtime.newArgumentError("negative array size (or size too big)");
         }
 
         if (length >= Integer.MAX_VALUE) {
             throw runtime.newArgumentError("array size too big");
         }
     }
 
     /** Getter for property list.
      * @return Value of property list.
      */
     public List getList() {
         return Arrays.asList(toJavaArray()); 
     }
 
     public int getLength() {
         return realLength;
     }
 
     public IRubyObject[] toJavaArray() {
         IRubyObject[] copy = new IRubyObject[realLength];
         try {
             System.arraycopy(values, begin, copy, 0, realLength);
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         return copy;
     }
     
     public IRubyObject[] toJavaArrayUnsafe() {
         return !isShared ? values : toJavaArray();
     }    
 
     public IRubyObject[] toJavaArrayMaybeUnsafe() {
         return (!isShared && begin == 0 && values.length == realLength) ? values : toJavaArray();
     }    
 
     /** rb_ary_make_shared
     *
     */
     private RubyArray makeShared() {
         return makeShared(begin, realLength, getMetaClass());
     }
 
     private RubyArray makeShared(int beg, int len, RubyClass klass) {
         return makeShared(beg, len, new RubyArray(klass.getRuntime(), klass));
     }
 
     private RubyArray makeShared(int beg, int len, RubyArray sharedArray) {
         isShared = true;
         sharedArray.values = values;
         sharedArray.isShared = true;
         sharedArray.begin = beg;
         sharedArray.realLength = len;
         return sharedArray;
     }
 
     /** ary_shared_first
      * 
      */
     private RubyArray makeSharedFirst(ThreadContext context, IRubyObject num, boolean last, RubyClass klass) {
         int n = RubyNumeric.num2int(num);
         
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw context.getRuntime().newArgumentError("negative array size");
         }
         
         return makeShared(last ? begin + realLength - n : begin, n, klass);
     }
 
     /** rb_ary_modify_check
      *
      */
     private final void modifyCheck() {
         if ((flags & TMPLOCK_OR_FROZEN_ARR_F) != 0) {
             if ((flags & FROZEN_F) != 0) throw getRuntime().newFrozenError("array");           
             if ((flags & TMPLOCK_ARR_F) != 0) throw getRuntime().newTypeError("can't modify array during iteration");
         }
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify array");
         }
     }
 
     /** rb_ary_modify
      *
      */
     private final void modify() {
         modifyCheck();
         if (isShared) {
             IRubyObject[] vals = new IRubyObject[realLength];
             isShared = false;
             try {
                 System.arraycopy(values, begin, vals, 0, realLength);
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
             }
             begin = 0;            
             values = vals;
         }
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * @deprecated Use the versions with zero, one, or two args.
      */
     public IRubyObject initialize(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 0:
             return initialize(context, block);
         case 1:
             return initializeCommon(context, args[0], null, block);
         case 2:
             return initializeCommon(context, args[0], args[1], block);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 0, 2);
             return null; // not reached
         }
     }    
     
     /** rb_ary_initialize
      * 
      */
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, Block block) {
         modifyCheck();
         Ruby runtime = context.getRuntime();
         realLength = 0;
         if (block.isGiven() && runtime.isVerbose()) {
             runtime.getWarnings().warning(ID.BLOCK_UNUSED, "given block not used");
         }
         return this;
     }
 
     /** rb_ary_initialize
      * 
      */
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject arg0, Block block) {
         return initializeCommon(context, arg0, null, block);
     }
 
     /** rb_ary_initialize
      * 
      */
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return initializeCommon(context, arg0, arg1, block);
     }
 
     private IRubyObject initializeCommon(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         Ruby runtime = context.getRuntime();
 
         if (arg1 == null && !(arg0 instanceof RubyFixnum)) {
             IRubyObject val = arg0.checkArrayType();
             if (!val.isNil()) {
                 replace(val);
                 return this;
             }
         }
 
         long len = RubyNumeric.num2long(arg0);
         if (len < 0) throw runtime.newArgumentError("negative array size");
         if (len >= Integer.MAX_VALUE) throw runtime.newArgumentError("array size too big");
         int ilen = (int) len;
 
         modify();
 
         if (ilen > values.length - begin) {
             values = new IRubyObject[ilen];
             begin = 0;
         }
 
         if (block.isGiven()) {
             if (arg1 != null) {
                 runtime.getWarnings().warn(ID.BLOCK_BEATS_DEFAULT_VALUE, "block supersedes default value argument");
             }
 
             if (block.getBody().getArgumentType() == BlockBody.ZERO_ARGS) {
                 IRubyObject nil = runtime.getNil();
                 for (int i = 0; i < ilen; i++) {
                     store(i, block.yield(context, nil));
                     realLength = i + 1;
                 }
             } else {
                 for (int i = 0; i < ilen; i++) {
                     store(i, block.yield(context, RubyFixnum.newFixnum(runtime, i)));
                     realLength = i + 1;
                 }
             }
             
         } else {
             try {
                 if (arg1 == null) {
                     RuntimeHelpers.fillNil(values, begin, begin + ilen, runtime);
                 } else {
                     fill(values, begin, begin + ilen, arg1);
                 }
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
             }
             realLength = ilen;
         }
         return this;
     }
 
     /** rb_ary_initialize_copy
      * 
      */
     @JRubyMethod(name = {"initialize_copy"}, required = 1, visibility=Visibility.PRIVATE)
     @Override
     public IRubyObject initialize_copy(IRubyObject orig) {
         return this.replace(orig);
     }
     
     /** rb_ary_replace
      *
      */
     @JRubyMethod(name = {"replace"}, required = 1)
     public IRubyObject replace(IRubyObject orig) {
         modifyCheck();
 
         RubyArray origArr = orig.convertToArray();
 
         if (this == orig) return this;
 
         origArr.isShared = true;
         isShared = true;
         values = origArr.values;
         realLength = origArr.realLength;
         begin = origArr.begin;
 
 
         return this;
     }
 
     /** rb_ary_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     @Override
     public IRubyObject to_s() {
         if (getRuntime().is1_9()) {
             // 1.9 seems to just do inspect for to_s now
             return inspect();
         }
         
         if (realLength == 0) return RubyString.newEmptyString(getRuntime());
 
         return join(getRuntime().getCurrentContext(), getRuntime().getGlobalVariables().get("$,"));
     }
 
     
     public boolean includes(ThreadContext context, IRubyObject item) {
         int myBegin = this.begin;
         
         for (int i = myBegin; i < myBegin + realLength; i++) {
             final IRubyObject value;
             try {
                 value = values[i];
             } catch (ArrayIndexOutOfBoundsException e) {
                 concurrentModification();
                 continue;
             }
             if (equalInternal(context, value, item)) return true;
     	}
         
         return false;
     }
 
     /** rb_ary_hash
      * 
      */
     @JRubyMethod(name = "hash", compat = CompatVersion.RUBY1_8)
     public RubyFixnum hash(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (runtime.isInspecting(this)) return  RubyFixnum.zero(runtime);
 
         try {
             runtime.registerInspecting(this);
             int myBegin = this.begin;
             int h = realLength;
             for (int i = myBegin; i < myBegin + realLength; i++) {
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
             return runtime.newFixnum(h);
         } finally {
             runtime.unregisterInspecting(this);
         }
     }
 
     /** rb_ary_hash
      * 
      */
     @JRubyMethod(name = "hash", compat = CompatVersion.RUBY1_9)
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
     @JRubyMethod(name = "fetch", frame = true)
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
    @JRubyMethod(name = "fetch", frame = true)
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
     @JRubyMethod(name = "insert", compat = CompatVersion.RUBY1_8)
     public IRubyObject insert(IRubyObject arg) {
         return this;
     }
     
     @JRubyMethod(name = "insert", compat = CompatVersion.RUBY1_9)
     public IRubyObject insert19(IRubyObject arg) {
         modifyCheck();
         
         return insert(arg);
     }
 
     @JRubyMethod(name = "insert", compat = CompatVersion.RUBY1_8)
     public IRubyObject insert(IRubyObject arg1, IRubyObject arg2) {
         long pos = RubyNumeric.num2long(arg1);
 
         if (pos == -1) pos = realLength;
         if (pos < 0) pos++;
         
         spliceOne(pos, arg2); // rb_ary_new4
         
         return this;
     }
 
     @JRubyMethod(name = "insert", compat = CompatVersion.RUBY1_9)
     public IRubyObject insert19(IRubyObject arg1, IRubyObject arg2) {
         modifyCheck();
 
         return insert(arg1, arg2);
     }
 
     @JRubyMethod(name = "insert", required = 1, rest = true, compat = CompatVersion.RUBY1_8)
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
 
     @JRubyMethod(name = "insert", required = 1, rest = true, compat = CompatVersion.RUBY1_9)
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
 
-    /** rb_ary_push_m
-     * FIXME: Whis is this named "push_m"?
+    /** rb_ary_push_m - instance method push
+     *
      */
     @JRubyMethod(name = "push", rest = true, compat = CompatVersion.RUBY1_8)
     public RubyArray push_m(IRubyObject[] items) {
         for (int i = 0; i < items.length; i++) {
             append(items[i]);
         }
         
         return this;
     }
 
     @JRubyMethod(name = "push", rest = true, compat = CompatVersion.RUBY1_9)
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
 
     @JRubyMethod(name = "unshift", compat = CompatVersion.RUBY1_8)
     public IRubyObject unshift() {
         return this;
     }
 
     @JRubyMethod(name = "unshift", compat = CompatVersion.RUBY1_9)
     public IRubyObject unshift19() {
         modifyCheck();
 
         return this;
 
     }
 
     /** rb_ary_unshift
      *
      */
     @JRubyMethod(name = "unshift", compat = CompatVersion.RUBY1_8)
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
 
     @JRubyMethod(name = "unshift", compat = CompatVersion.RUBY1_9)
     public IRubyObject unshift19(IRubyObject item) {
         modifyCheck();
 
         return unshift(item);
     }
 
     @JRubyMethod(name = "unshift", rest = true, compat = CompatVersion.RUBY1_8)
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
 
     @JRubyMethod(name = "unshift", rest = true, compat = CompatVersion.RUBY1_9)
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
     @JRubyMethod(name = {"[]", "slice"}, compat = CompatVersion.RUBY1_8)
     public IRubyObject aref(IRubyObject arg0) {
         assert !arg0.getRuntime().is1_9();
         if (arg0 instanceof RubyFixnum) return entry(((RubyFixnum)arg0).getLongValue());
         if (arg0 instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
         return arefCommon(arg0);
     }
 
     @JRubyMethod(name = {"[]", "slice"}, compat = CompatVersion.RUBY1_9)
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
 
     @JRubyMethod(name = {"[]", "slice"}, compat = CompatVersion.RUBY1_8)
     public IRubyObject aref(IRubyObject arg0, IRubyObject arg1) {
         assert !arg0.getRuntime().is1_9();
         if (arg0 instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
         return arefCommon(arg0, arg1);
     }
 
     @JRubyMethod(name = {"[]", "slice"}, compat = CompatVersion.RUBY1_9)
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
 
     @JRubyMethod(name = "[]=", compat = CompatVersion.RUBY1_8)
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
 
     @JRubyMethod(name = "[]=", compat = CompatVersion.RUBY1_9)
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
     @JRubyMethod(name = "[]=", compat = CompatVersion.RUBY1_8)
     public IRubyObject aset(IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         assert !getRuntime().is1_9();
         if (arg0 instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
         if (arg1 instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as subarray length");
         splice(RubyNumeric.num2long(arg0), RubyNumeric.num2long(arg1), arg2, false);
         return arg2;
     }
 
     @JRubyMethod(name = "[]=", compat = CompatVersion.RUBY1_9)
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
     @JRubyMethod(name = "concat", required = 1, compat = CompatVersion.RUBY1_8)
     public RubyArray concat(IRubyObject obj) {
         RubyArray ary = obj.convertToArray();
         
         if (ary.realLength > 0) splice(realLength, 0, ary, false);
 
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
     @JRubyMethod(name = "join", compat = CompatVersion.RUBY1_8)
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
 
     @JRubyMethod(name = "join", compat = CompatVersion.RUBY1_8)
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
     @JRubyMethod(name = "join", compat = CompatVersion.RUBY1_9)
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
 
     @JRubyMethod(name = "join", compat = CompatVersion.RUBY1_9)
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
