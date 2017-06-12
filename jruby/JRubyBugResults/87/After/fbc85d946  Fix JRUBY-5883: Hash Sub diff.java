diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index 7f647e800b..991a0b873b 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1,2981 +1,2981 @@
 /*
  **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <Ola.Bini@ki.se>
  * Copyright (C) 2006 Daniel Steer <damian.steer@hp.com>
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
 import java.lang.reflect.Array;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Comparator;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Random;
 import java.util.Set;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.cext.RArray;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.java.addons.ArrayJavaAddons;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.ClassIndex;
+import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.Pack;
 import org.jruby.util.Qsort;
 import org.jruby.util.RecursiveComparator;
 import org.jruby.util.TypeConverter;
 
 import static org.jruby.javasupport.util.RuntimeHelpers.invokedynamic;
 import static org.jruby.runtime.MethodIndex.HASH;
 import static org.jruby.runtime.MethodIndex.OP_CMP;
 
 /**
  * The implementation of the built-in class Array in Ruby.
  *
  * Concurrency: no synchronization is required among readers, but
  * all users must synchronize externally with writers.
  *
  */
 @JRubyClass(name="Array")
 public class RubyArray extends RubyObject implements List {
 
     public static RubyClass createArrayClass(Ruby runtime) {
         RubyClass arrayc = runtime.defineClass("Array", runtime.getObject(), ARRAY_ALLOCATOR);
         runtime.setArray(arrayc);
 
         arrayc.index = ClassIndex.ARRAY;
         arrayc.setReifiedClass(RubyArray.class);
         
         arrayc.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyArray;
             }
         };
 
         arrayc.includeModule(runtime.getEnumerable());
         arrayc.defineAnnotatedMethods(RubyArray.class);
 
         return arrayc;
     }
 
     private static ObjectAllocator ARRAY_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyArray(runtime, klass, IRubyObject.NULL_ARRAY);
         }
     };
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.ARRAY;
     }
 
     private final void concurrentModification() {
         concurrentModification(getRuntime());
     }
 
     private static void concurrentModification(Ruby runtime) {
         throw runtime.newConcurrencyError("Detected invalid array contents due to unsynchronized modifications with concurrent users");
     }
 
     /** rb_ary_s_create
      * 
      */
     @JRubyMethod(name = "[]", rest = true, meta = true)
     public static IRubyObject create(IRubyObject klass, IRubyObject[] args, Block block) {
         RubyArray arr = (RubyArray) ((RubyClass) klass).allocate();
 
         if (args.length > 0) {
             arr.values = new IRubyObject[args.length];
             System.arraycopy(args, 0, arr.values, 0, args.length);
             arr.realLength = args.length;
         }
         return arr;
     }
 
     /** rb_ary_new2
      *
      */
     public static final RubyArray newArray(final Ruby runtime, final long len) {
         checkLength(runtime, len);
         return newArray(runtime, (int)len);
     }
     
     public static final RubyArray newArrayLight(final Ruby runtime, final long len) {
         checkLength(runtime, len);
         return newArrayLight(runtime, (int)len);
     }
     
     public static final RubyArray newArray(final Ruby runtime, final int len) {
         RubyArray array = new RubyArray(runtime, len);
         RuntimeHelpers.fillNil(array.values, 0, array.values.length, runtime);
         return array;
     }
 
     public static final RubyArray newArrayLight(final Ruby runtime, final int len) {
         RubyArray array = new RubyArray(runtime, len, false);
         RuntimeHelpers.fillNil(array.values, 0, array.values.length, runtime);
         return array;
     }
 
     /** rb_ary_new
      *
      */
     public static final RubyArray newArray(final Ruby runtime) {
         return newArray(runtime, ARRAY_DEFAULT_SIZE);
     }
 
     /** rb_ary_new
      *
      */
     public static final RubyArray newArrayLight(final Ruby runtime) {
         /* Ruby arrays default to holding 16 elements, so we create an
          * ArrayList of the same size if we're not told otherwise
          */
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
 
     private RArray rarray;
 
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
 
     private RubyArray(Ruby runtime, RubyClass metaClass, IRubyObject[] vals, int begin, int length) {
         super(runtime, metaClass);
         this.values = vals;
         this.begin = begin;
         this.realLength = length;
         this.isShared = true;
     }
     
     protected RubyArray(Ruby runtime, int length) {
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
         safeArrayCopy(runtime, original.values, original.begin, values, 0, realLength);
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
         if (newLength > valuesLength) {
             RuntimeHelpers.fillNil(reallocated, valuesLength, newLength, getRuntime());
             safeArrayCopy(values, begin, reallocated, 0, valuesLength); // elements and trailing nils
         } else {
             safeArrayCopy(values, begin, reallocated, 0, newLength); // ???
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
 
     public void setRArray(RArray rarray) {
         this.rarray = rarray;
     }
 
     public RArray getRArray() {
         return rarray;
     }
 
     public IRubyObject[] toJavaArray() {
         IRubyObject[] copy = new IRubyObject[realLength];
         safeArrayCopy(values, begin, copy, 0, realLength);
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
             safeArrayCopy(values, begin, vals, 0, realLength);
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
     @JRubyMethod(visibility = PRIVATE)
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
     @JRubyMethod(visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject arg0, Block block) {
         return initializeCommon(context, arg0, null, block);
     }
 
     /** rb_ary_initialize
      * 
      */
     @JRubyMethod(visibility = PRIVATE)
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
     @JRubyMethod(name = {"initialize_copy"}, required = 1, visibility=PRIVATE)
     @Override
     public IRubyObject initialize_copy(IRubyObject orig) {
         return this.replace(orig);
     }
 
     /**
      * Overridden dup for fast-path logic.
      *
      * @return A new RubyArray sharing the original backing store.
      */
     public IRubyObject dup() {
         if (metaClass.index != ClassIndex.ARRAY) return super.dup();
 
         RubyArray dup = new RubyArray(metaClass.getClassRuntime(), values, begin, realLength);
         dup.isShared = isShared = true;
         dup.flags |= flags & TAINTED_F; // from DUP_SETUP
         dup.flags |= flags & UNTRUSTED_F;
 
         return dup;
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
         int end = myBegin + realLength;
         IRubyObject[] values = this.values;
         for (int i = myBegin; i < end; i++) {
             final IRubyObject value = safeArrayRef(values, i);
             if (equalInternal(context, value, item)) return true;
         }
         
         return false;
     }
 
     /** rb_ary_hash
      * 
      */
     @JRubyMethod(name = "hash", compat = RUBY1_8)
     public RubyFixnum hash(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (runtime.isInspecting(this)) return  RubyFixnum.zero(runtime);
 
         try {
             runtime.registerInspecting(this);
             int myBegin = this.begin;
             int h = realLength;
             for (int i = myBegin; i < myBegin + realLength; i++) {
                 h = (h << 1) | (h < 0 ? 1 : 0);
                 final IRubyObject value = safeArrayRef(values, i);
                 h ^= RubyNumeric.num2long(invokedynamic(context, value, HASH));
             }
             return runtime.newFixnum(h);
         } finally {
             runtime.unregisterInspecting(this);
         }
     }
 
     /** rb_ary_hash
      * 
      */
     @JRubyMethod(name = "hash", compat = RUBY1_9)
     public RubyFixnum hash19(final ThreadContext context) {
         return (RubyFixnum)getRuntime().execRecursiveOuter(new Ruby.RecursiveFunction() {
                 public IRubyObject call(IRubyObject obj, boolean recur) {
                     int begin = RubyArray.this.begin;
                     long h = realLength;
                     if(recur) {
                         h ^= RubyNumeric.num2long(invokedynamic(context, context.runtime.getArray(), HASH));
                     } else {
                         for(int i = begin; i < begin + realLength; i++) {
                             h = (h << 1) | (h < 0 ? 1 : 0);
                             final IRubyObject value = safeArrayRef(values, i);
                             h ^= RubyNumeric.num2long(invokedynamic(context, value, HASH));
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
 
         safeArraySet(values, begin + (int) index, value);
         
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
 
     public final IRubyObject eltOk(long offset) {
         return safeArrayRef(values, begin + (int)offset);
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
         
         return safeArrayRef(values, begin + (int) index);
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
        
        return safeArrayRef(values, begin + (int) index);
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
                 safeArrayCopy(values, begin + (int) (beg + len), values, begin + (int) beg + rlen, realLength - (int) (beg + len));
                 realLength = alen;
             }
         }
 
         if (rlen > 0) {
             safeArrayCopy(rplArr.values, rplArr.begin, values, begin + (int) beg, rlen);
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
                 safeArrayCopy(values, begin + (int) beg, values, begin + (int) beg + 1, realLength - (int) beg);
                 realLength = alen;
             }
         }
 
         safeArraySet(values, begin + (int)beg, rpl);
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
         RubyArray dup = new RubyArray(metaClass.getClassRuntime(), metaClass, values, begin, realLength);
         dup.isShared = true;
         isShared = true;
         dup.flags |= flags & (TAINTED_F | UNTRUSTED_F); // from DUP_SETUP
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
         int realLength = this.realLength;
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
         
         safeArraySet(values, begin + realLength++, item);
         
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
 
         if (isShared) {
             return safeArrayRef(values, begin + --realLength);
         } else {
             int index = begin + --realLength;
             return safeArrayRefSet(values, index, context.getRuntime().getNil());
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
 
         final IRubyObject obj = safeArrayRefCondSet(values, begin, !isShared, runtime.getNil());
         begin++;
         realLength--;
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
             final int valuesLength = values.length - begin;
             if (realLength == valuesLength) {
                 int newLength = valuesLength >> 1;
                 if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
     
                 newLength += valuesLength;
                 IRubyObject[]vals = new IRubyObject[newLength];
                 safeArrayCopy(values, begin, vals, 1, valuesLength);
                 RuntimeHelpers.fillNil(vals, valuesLength + 1, newLength, getRuntime());
                 values = vals;
                 begin = 0;
             } else {
                 safeArrayCopy(values, begin, values, begin + 1, realLength);
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
         ByteList buffer = new ByteList();
         buffer.append('[');
         boolean tainted = isTaint();
         boolean untrust = isUntrusted();
 
         for (int i = 0; i < realLength; i++) {
             if (i > 0) buffer.append(',').append(' ');
 
             RubyString str = inspect(context, safeArrayRef(values, begin + i));
             if (str.isTaint()) tainted = true;
             if (str.isUntrusted()) untrust = true;
             buffer.append(str.getByteList());
         }
         buffer.append(']');
 
         RubyString str = getRuntime().newString(buffer);
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
 
     private boolean[] join0(ThreadContext context, ByteList sep, int max, ByteList result) {
         boolean t = false;
         boolean u = false;
         try {
             for(int i = begin; i < max; i++) {
                 IRubyObject val = values[i];
 
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
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
             return new boolean[] {t,u};
         }
         return new boolean[]{t,u};
     }
 
     private void join1(final ThreadContext context, IRubyObject obj, final ByteList sep, int i, final ByteList result) {
         for(; i < begin + realLength; i++) {
             if(i > begin && sep != null) {
                 result.append(sep);
             }
 
             IRubyObject val = safeArrayRef(values, i);
 
             if(val instanceof RubyString) {
                 result.append(((RubyString)val).getByteList());
             } else if(val instanceof RubyArray) {
                 obj = val;
                 recursiveJoin(context, obj, sep, result, val);
             } else {
                 IRubyObject tmp = val.checkStringType19();
                 if(tmp.isNil()){
                     tmp = TypeConverter.convertToTypeWithCheck(val, getRuntime().getString(), "to_s");
                 }
                 if(!tmp.isNil()) {
                     val = tmp;
                     result.append(((RubyString)val).getByteList());
                 } else {
                     tmp = TypeConverter.convertToTypeWithCheck(val, getRuntime().getArray(), "to_a");
                     if(!tmp.isNil()) {
                         obj = val;
                         val = tmp;
                         recursiveJoin(context, obj, sep, result, val);
                     } else {
                         val = RubyString.objAsString(context, val);
                         result.append(((RubyString)val).getByteList());
                     }
                 }
             }
         }
     }
 
     private void recursiveJoin(final ThreadContext context, IRubyObject obj, final ByteList sep,
             final ByteList result, IRubyObject val) {
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
             IRubyObject val = safeArrayRef(values, i);
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
-        return RecursiveComparator.compare(context, "==", this, obj, null);
+        return RecursiveComparator.compare(context, MethodIndex.OP_EQUAL, this, obj);
     }
 
-    public RubyBoolean compare(ThreadContext context, String method,
-            IRubyObject other, Set<RecursiveComparator.Pair> seen) {
+    public RubyBoolean compare(ThreadContext context, int method, IRubyObject other) {
 
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
-            if (!RecursiveComparator.compare(context, method, elt(i), ary.elt(i), seen).isTrue()) {
+            if (!invokedynamic(context, elt(i), method, ary.elt(i)).isTrue()) {
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
-        return RecursiveComparator.compare(context, "eql?", this, obj, null);
+        return RecursiveComparator.compare(context, MethodIndex.EQL, this, obj);
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
diff --git a/src/org/jruby/RubyHash.java b/src/org/jruby/RubyHash.java
index 3a4fa8c461..b0337514e6 100644
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
@@ -1,1986 +1,1987 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Ola Bini <Ola.Bini@ki.se>
  * Copyright (C) 2006 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 MenTaLguY <mental@rydia.net>
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
 import java.util.AbstractCollection;
 import java.util.AbstractSet;
 import java.util.Collection;
 import java.util.Map;
 import java.util.NoSuchElementException;
 import java.util.Iterator;
 import java.util.Set;
 import java.util.concurrent.atomic.AtomicInteger;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
+import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.RecursiveComparator;
 
 import static org.jruby.CompatVersion.*;
 import static org.jruby.javasupport.util.RuntimeHelpers.invokedynamic;
 import static org.jruby.runtime.MethodIndex.HASH;
 
 // Design overview:
 //
 // RubyHash is implemented as hash table with a singly-linked list of
 // RubyHash.RubyHashEntry objects for each bucket.  RubyHashEntry objects
 // are also kept in a doubly-linked list which reflects their insertion
 // order and is used for iteration.  For simplicity, this latter list is
 // circular; a dummy RubyHashEntry, RubyHash.head, is used to mark the
 // ends of the list.
 //
 // When an entry is removed from the table, it is also removed from the
 // doubly-linked list.  However, while the reference to the previous
 // RubyHashEntry is cleared (to mark the entry as dead), the reference
 // to the next RubyHashEntry is preserved so that iterators are not
 // invalidated: any iterator with a reference to a dead entry can climb
 // back up into the list of live entries by chasing next references until
 // it finds a live entry (or head).
 //
 // Ordinarily, this scheme would require O(N) time to clear a hash (since
 // each RubyHashEntry would need to be visited and unlinked from the
 // iteration list), but RubyHash also maintains a generation count.  Every
 // time the hash is cleared, the doubly-linked list is simply discarded and
 // the generation count incremented.  Iterators check to see whether the
 // generation count has changed; if it has, they reset themselves back to
 // the new start of the list.
 //
 // This design means that iterators are never invalidated by changes to the
 // hashtable, and they do not need to modify the structure during their
 // lifecycle.
 //
 
 /** Implementation of the Hash class.
  *
  *  Concurrency: no synchronization is required among readers, but
  *  all users must synchronize externally with writers.
  *
  */
 @JRubyClass(name = "Hash", include="Enumerable")
 public class RubyHash extends RubyObject implements Map {
 
     public static RubyClass createHashClass(Ruby runtime) {
         RubyClass hashc = runtime.defineClass("Hash", runtime.getObject(), HASH_ALLOCATOR);
         runtime.setHash(hashc);
 
         hashc.index = ClassIndex.HASH;
         hashc.setReifiedClass(RubyHash.class);
         
         hashc.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyHash;
             }
         };
 
         hashc.includeModule(runtime.getEnumerable());
 
         hashc.defineAnnotatedMethods(RubyHash.class);
 
         return hashc;
     }
 
     private final static ObjectAllocator HASH_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyHash(runtime, klass);
         }
     };
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.HASH;
     }
 
     /** rb_hash_s_create
      *
      */
     @JRubyMethod(name = "[]", rest = true, meta = true)
     public static IRubyObject create(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass) recv;
         Ruby runtime = context.getRuntime();
         RubyHash hash;
 
         if (args.length == 1) {
             IRubyObject tmp = TypeConverter.convertToTypeWithCheck(
                     args[0], runtime.getHash(), "to_hash");
 
             if (!tmp.isNil()) {
                 RubyHash otherHash = (RubyHash) tmp;
                 return new RubyHash(runtime, klass, otherHash);
             }
 
             tmp = TypeConverter.convertToTypeWithCheck(args[0], runtime.getArray(), "to_ary");
             if (!tmp.isNil()) {
                 hash = (RubyHash)klass.allocate();
                 RubyArray arr = (RubyArray)tmp;
                 for(int i = 0, j = arr.getLength(); i<j; i++) {
                     IRubyObject v = TypeConverter.convertToTypeWithCheck(arr.entry(i), runtime.getArray(), "to_ary");
                     IRubyObject key = runtime.getNil();
                     IRubyObject val = runtime.getNil();
                     if(v.isNil()) {
                         continue;
                     }
                     switch(((RubyArray)v).getLength()) {
                     case 2:
                         val = ((RubyArray)v).entry(1);
                     case 1:
                         key = ((RubyArray)v).entry(0);
                         hash.fastASet(key, val);
                     }
                 }
                 return hash;
             }
         }
 
         if ((args.length & 1) != 0) {
             throw runtime.newArgumentError("odd number of arguments for Hash");
         }
 
         hash = (RubyHash)klass.allocate();
         for (int i=0; i < args.length; i+=2) hash.op_aset(context, args[i], args[i+1]);
 
         return hash;
     }
 
     @JRubyMethod(name = "try_convert", meta = true, compat = RUBY1_9)
     public static IRubyObject try_convert(ThreadContext context, IRubyObject recv, IRubyObject args) {
         return TypeConverter.convertToTypeWithCheck(args, context.getRuntime().getHash(), "to_hash");
     }
 
     /** rb_hash_new
      *
      */
     public static final RubyHash newHash(Ruby runtime) {
         return new RubyHash(runtime);
     }
 
     /** rb_hash_new
      *
      */
     public static final RubyHash newHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
         assert defaultValue != null;
 
         return new RubyHash(runtime, valueMap, defaultValue);
     }
 
     private RubyHashEntry[] table;
     protected int size = 0;
     private int threshold;
 
     private static final int PROCDEFAULT_HASH_F = 1 << 10;
 
     private IRubyObject ifNone;
 
     private RubyHash(Ruby runtime, RubyClass klass, RubyHash other) {
         super(runtime, klass);
         this.ifNone = runtime.getNil();
         threshold = INITIAL_THRESHOLD;
         table = other.internalCopyTable(head);
         size = other.size;
     }
 
     public RubyHash(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
         this.ifNone = runtime.getNil();
         alloc();
     }
 
     public RubyHash(Ruby runtime) {
         this(runtime, runtime.getNil());
     }
 
     public RubyHash(Ruby runtime, IRubyObject defaultValue) {
         super(runtime, runtime.getHash());
         this.ifNone = defaultValue;
         alloc();
     }
 
     /*
      *  Constructor for internal usage (mainly for Array#|, Array#&, Array#- and Array#uniq)
      *  it doesn't initialize ifNone field
      */
     RubyHash(Ruby runtime, boolean objectSpace) {
         super(runtime, runtime.getHash(), objectSpace);
         alloc();
     }
 
     // TODO should this be deprecated ? (to be efficient, internals should deal with RubyHash directly)
     public RubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
         super(runtime, runtime.getHash());
         this.ifNone = defaultValue;
         alloc();
 
         for (Iterator iter = valueMap.entrySet().iterator();iter.hasNext();) {
             Map.Entry e = (Map.Entry)iter.next();
             internalPut((IRubyObject)e.getKey(), (IRubyObject)e.getValue());
         }
     }
 
     private final void alloc() {
         threshold = INITIAL_THRESHOLD;
         generation++;
         head.nextAdded = head.prevAdded = head;
         table = new RubyHashEntry[MRI_HASH_RESIZE ? MRI_INITIAL_CAPACITY : JAVASOFT_INITIAL_CAPACITY];
     }
 
     /* ============================
      * Here are hash internals
      * (This could be extracted to a separate class but it's not too large though)
      * ============================
      */
 
     private static final int MRI_PRIMES[] = {
         8 + 3, 16 + 3, 32 + 5, 64 + 3, 128 + 3, 256 + 27, 512 + 9, 1024 + 9, 2048 + 5, 4096 + 3,
         8192 + 27, 16384 + 43, 32768 + 3, 65536 + 45, 131072 + 29, 262144 + 3, 524288 + 21, 1048576 + 7,
         2097152 + 17, 4194304 + 15, 8388608 + 9, 16777216 + 43, 33554432 + 35, 67108864 + 15,
         134217728 + 29, 268435456 + 3, 536870912 + 11, 1073741824 + 85, 0
     };
 
     private static final int JAVASOFT_INITIAL_CAPACITY = 8; // 16 ?
     private static final int MRI_INITIAL_CAPACITY = MRI_PRIMES[0];
 
     private static final int INITIAL_THRESHOLD = JAVASOFT_INITIAL_CAPACITY - (JAVASOFT_INITIAL_CAPACITY >> 2);
     private static final int MAXIMUM_CAPACITY = 1 << 30;
 
     public static final RubyHashEntry NO_ENTRY = new RubyHashEntry();
     private int generation = 0; // generation count for O(1) clears
     private final RubyHashEntry head = new RubyHashEntry();
 
     { head.prevAdded = head.nextAdded = head; }
 
     public static final class RubyHashEntry implements Map.Entry {
         private IRubyObject key;
         private IRubyObject value;
         private RubyHashEntry next;
         private RubyHashEntry prevAdded;
         private RubyHashEntry nextAdded;
         private int hash;
 
         RubyHashEntry() {
             key = NEVER;
         }
 
         public RubyHashEntry(int h, IRubyObject k, IRubyObject v, RubyHashEntry e, RubyHashEntry head) {
             key = k; value = v; next = e; hash = h;
             if (head != null) {
                 prevAdded = head.prevAdded;
                 nextAdded = head;
                 nextAdded.prevAdded = this;
                 prevAdded.nextAdded = this;
             }
         }
 
         public void detach() {
             if (prevAdded != null) {
                 prevAdded.nextAdded = nextAdded;
                 nextAdded.prevAdded = prevAdded;
                 prevAdded = null;
             }
         }
 
         public boolean isLive() {
             return prevAdded != null;
         }
 
         public Object getKey() {
             return key;
         }
         public Object getJavaifiedKey(){
             return key.toJava(Object.class);
         }
 
         public Object getValue() {
             return value;
         }
         public Object getJavaifiedValue() {
             return value.toJava(Object.class);
         }
 
         public Object setValue(Object value) {
             IRubyObject oldValue = this.value;
             if (value instanceof IRubyObject) {
                 this.value = (IRubyObject)value;
             } else {
                 throw new UnsupportedOperationException("directEntrySet() doesn't support setValue for non IRubyObject instance entries, convert them manually or use entrySet() instead");
             }
             return oldValue;
         }
 
         @Override
         public boolean equals(Object other){
             if(!(other instanceof RubyHashEntry)) return false;
             RubyHashEntry otherEntry = (RubyHashEntry)other;
             
             return (key == otherEntry.key || key.eql(otherEntry.key)) &&
                     (value == otherEntry.value || value.equals(otherEntry.value));
         }
 
         @Override
         public int hashCode(){
             return key.hashCode() ^ value.hashCode();
         }
     }
 
     private static int JavaSoftHashValue(int h) {
         h ^= (h >>> 20) ^ (h >>> 12);
         return h ^ (h >>> 7) ^ (h >>> 4);
     }
 
     private static int JavaSoftBucketIndex(final int h, final int length) {
         return h & (length - 1);
     }
 
     private static int MRIHashValue(int h) {
         return h & HASH_SIGN_BIT_MASK;
     }
 
     private static final int HASH_SIGN_BIT_MASK = ~(1 << 31);
     private static int MRIBucketIndex(final int h, final int length) {
         return ((h & HASH_SIGN_BIT_MASK) % length);
     }
 
     private final void resize(int newCapacity) {
         final RubyHashEntry[] oldTable = table;
         final RubyHashEntry[] newTable = new RubyHashEntry[newCapacity];
         for (int j = 0; j < oldTable.length; j++) {
             RubyHashEntry entry = oldTable[j];
             oldTable[j] = null;
             while (entry != null) {
                 RubyHashEntry next = entry.next;
                 int i = bucketIndex(entry.hash, newCapacity);
                 entry.next = newTable[i];
                 newTable[i] = entry;
                 entry = next;
             }
         }
         table = newTable;
     }
 
     private final void JavaSoftCheckResize() {
         if (overThreshold()) {
             RubyHashEntry[] tbl = table;
             if (tbl.length == MAXIMUM_CAPACITY) {
                 threshold = Integer.MAX_VALUE;
                 return;
             }
             resizeAndAdjustThreshold(table);
         }
     }
     
     private boolean overThreshold() {
         return size > threshold;
     }
     
     private void resizeAndAdjustThreshold(RubyHashEntry[] oldTable) {
         int newCapacity = oldTable.length << 1;
         resize(newCapacity);
         threshold = newCapacity - (newCapacity >> 2);
     }
 
     private static final int MIN_CAPA = 8;
     private static final int ST_DEFAULT_MAX_DENSITY = 5;
     private final void MRICheckResize() {
         if (size / table.length > ST_DEFAULT_MAX_DENSITY) {
             int forSize = table.length + 1; // size + 1;
             for (int i=0, newCapacity = MIN_CAPA; i < MRI_PRIMES.length; i++, newCapacity <<= 1) {
                 if (newCapacity > forSize) {
                     resize(MRI_PRIMES[i]);
                     return;
                 }
             }
             return; // suboptimal for large hashes (> 1073741824 + 85 entries) not very likely to happen
         }
     }
     // ------------------------------
     private static final boolean MRI_HASH = true;
     private static final boolean MRI_HASH_RESIZE = true;
 
     protected static int hashValue(final int h) {
         return MRI_HASH ? MRIHashValue(h) : JavaSoftHashValue(h);
     }
 
     private static int bucketIndex(final int h, final int length) {
         return MRI_HASH ? MRIBucketIndex(h, length) : JavaSoftBucketIndex(h, length);
     }
 
     private void checkResize() {
         if (MRI_HASH_RESIZE) MRICheckResize(); else JavaSoftCheckResize();
     }
 
     private void checkIterating() {
         if (iteratorCount.get() > 0) {
             throw getRuntime().newRuntimeError("can't add a new key into hash during iteration");
         }
     }
     // ------------------------------
     public static long collisions = 0;
 
     // put implementation
 
     private final void internalPut(final IRubyObject key, final IRubyObject value) {
         internalPut(key, value, true);
     }
 
     protected void internalPut(final IRubyObject key, final IRubyObject value, final boolean checkForExisting) {
         checkResize();
         final int hash = hashValue(key.hashCode());
         final int i = bucketIndex(hash, table.length);
 
         // if (table[i] != null) collisions++;
 
         if (checkForExisting) {
             for (RubyHashEntry entry = table[i]; entry != null; entry = entry.next) {
                 if (internalKeyExist(entry, hash, key)) {
                     entry.value = value;
                     return;
                 }
             }
         }
 
         checkIterating();
 
         table[i] = new RubyHashEntry(hash, key, value, table[i], head);
         size++;
     }
 
     // get implementation
 
     protected IRubyObject internalGet(IRubyObject key) { // specialized for value
         return internalGetEntry(key).value;
     }
 
     protected RubyHashEntry internalGetEntry(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         for (RubyHashEntry entry = table[bucketIndex(hash, table.length)]; entry != null; entry = entry.next) {
             if (internalKeyExist(entry, hash, key)) {
                 return entry;
             }
         }
         return NO_ENTRY;
     }
 
     private boolean internalKeyExist(RubyHashEntry entry, int hash, IRubyObject key) {
         return (entry.hash == hash
             && (entry.key == key || (!isComparedByIdentity() && key.eql(entry.key))));
     }
 
     // delete implementation
 
 
     protected RubyHashEntry internalDelete(final IRubyObject key) {
         return internalDelete(hashValue(key.hashCode()), MATCH_KEY, key);
     }
 
     protected RubyHashEntry internalDeleteEntry(final RubyHashEntry entry) {
         // n.b. we need to recompute the hash in case the key object was modified
         return internalDelete(hashValue(entry.key.hashCode()), MATCH_ENTRY, entry);
     }
 
     private final RubyHashEntry internalDelete(final int hash, final EntryMatchType matchType, final Object obj) {
         final int i = bucketIndex(hash, table.length);
 
         RubyHashEntry entry = table[i];
         if (entry != null) {
             RubyHashEntry prior = null;
             for (; entry != null; prior = entry, entry = entry.next) {
                 if (entry.hash == hash && matchType.matches(entry, obj)) {
                     if (prior != null) {
                         prior.next = entry.next;
                     } else {
                         table[i] = entry.next;
                     }
                     entry.detach();
                     size--;
                     return entry;
                 }
             }
         }
 
         return NO_ENTRY;
     }
 
     private static abstract class EntryMatchType {
         public abstract boolean matches(final RubyHashEntry entry, final Object obj);
     }
 
     private static final EntryMatchType MATCH_KEY = new EntryMatchType() {
         public boolean matches(final RubyHashEntry entry, final Object obj) {
             final IRubyObject key = entry.key;
             return obj == key || (((IRubyObject)obj).eql(key));
         }
     };
 
     private static final EntryMatchType MATCH_ENTRY = new EntryMatchType() {
         public boolean matches(final RubyHashEntry entry, final Object obj) {
             return entry.equals(obj);
         }
     };
 
     private final RubyHashEntry[] internalCopyTable(RubyHashEntry destHead) {
          RubyHashEntry[]newTable = new RubyHashEntry[table.length];
 
          for (RubyHashEntry entry = head.nextAdded; entry != head; entry = entry.nextAdded) {
              int i = bucketIndex(entry.hash, table.length);
              newTable[i] = new RubyHashEntry(entry.hash, entry.key, entry.value, newTable[i], destHead);
          }
          return newTable;
     }
 
     public static abstract class Visitor {
         public abstract void visit(IRubyObject key, IRubyObject value);
     }
 
     public void visitAll(Visitor visitor) {
         int startGeneration = generation;
         for (RubyHashEntry entry = head.nextAdded; entry != head; entry = entry.nextAdded) {
             if (startGeneration != generation) {
                 startGeneration = generation;
                 entry = head.nextAdded;
                 if (entry == head) break;
             }
             if (entry.isLive()) visitor.visit(entry.key, entry.value);
         }
     }
 
     /* ============================
      * End of hash internals
      * ============================
      */
 
     /*  ================
      *  Instance Methods
      *  ================
      */
 
     /** rb_hash_initialize
      *
      */
     @JRubyMethod(optional = 1, visibility = PRIVATE)
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
     @JRubyMethod(name = "default")
     public IRubyObject default_value_get(ThreadContext context) {
         if ((flags & PROCDEFAULT_HASH_F) != 0) {
             return getRuntime().getNil();
         }
         return ifNone;
     }
     @JRubyMethod(name = "default")
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
     @JRubyMethod
     public IRubyObject default_proc() {
         return (flags & PROCDEFAULT_HASH_F) != 0 ? ifNone : getRuntime().getNil();
     }
 
     /** default_proc_arity_check
      *
      */
     private void checkDefaultProcArity(IRubyObject proc) {
         int n = ((RubyProc)proc).getBlock().arity().getValue();
 
         if(((RubyProc)proc).getBlock().type == Block.Type.LAMBDA && n != 2 && (n >= 0 || n < -3)) {
             if(n < 0) n = -n-1;
             throw getRuntime().newTypeError("default_proc takes two arguments (2 for " + n + ")");
         }
     }
 
     /** rb_hash_set_default_proc
      *
      */
     @JRubyMethod(name = "default_proc=", compat = RUBY1_9)
     public IRubyObject set_default_proc(IRubyObject proc) {
         modify();
         IRubyObject b = TypeConverter.convertToType(proc, getRuntime().getProc(), "to_proc");
         if(b.isNil() || !(b instanceof RubyProc)) {
             throw getRuntime().newTypeError("wrong default_proc type " + proc.getMetaClass() + " (expected Proc)");
         }
         proc = b;
         checkDefaultProcArity(proc);
         ifNone = proc;
         flags |= PROCDEFAULT_HASH_F;
         return proc;
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
         try {
             final RubyArray result = RubyArray.newArray(runtime, size);
 
             visitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     result.append(RubyArray.newArray(runtime, key, value));
                 }
             });
 
             result.setTaint(isTaint());
             return result;
         } catch (NegativeArraySizeException nase) {
             throw concurrentModification();
         }
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
 
     @JRubyMethod(name = "to_s", compat = RUBY1_9)
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
     
     public final void fastASetCheckString(Ruby runtime, IRubyObject key, IRubyObject value) {
       if (key instanceof RubyString) {
           op_asetForString(runtime, (RubyString) key, value);
       } else {
           internalPut(key, value);
       }
     }
 
     public final void fastASetCheckString19(Ruby runtime, IRubyObject key, IRubyObject value) {
       if (key.getMetaClass().getRealClass() == runtime.getString()) {
           op_asetForString(runtime, (RubyString) key, value);
       } else {
           internalPut(key, value);
       }
     }
 
     @Deprecated
     public IRubyObject op_aset(IRubyObject key, IRubyObject value) {
         return op_aset(getRuntime().getCurrentContext(), key, value);
     }
 
     /** rb_hash_aset
      *
      */
     @JRubyMethod(name = {"[]=", "store"}, required = 2, compat = RUBY1_8)
     public IRubyObject op_aset(ThreadContext context, IRubyObject key, IRubyObject value) {
         modify();
         
         fastASetCheckString(context.getRuntime(), key, value);
         return value;
     }
 
     @JRubyMethod(name = {"[]=", "store"}, required = 2, compat = RUBY1_9)
     public IRubyObject op_aset19(ThreadContext context, IRubyObject key, IRubyObject value) {
         modify();
 
         fastASetCheckString19(context.getRuntime(), key, value);
         return value;
     }
 
     protected void op_asetForString(Ruby runtime, RubyString key, IRubyObject value) {
         final RubyHashEntry entry = internalGetEntry(key);
         if (entry != NO_ENTRY) {
             entry.value = value;
         } else {
             checkIterating();
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
 
-    public RubyBoolean compare(final ThreadContext context, final String method, IRubyObject other, final Set<RecursiveComparator.Pair> seen) {
+    public RubyBoolean compare(final ThreadContext context, final int method, IRubyObject other) {
 
         Ruby runtime = context.getRuntime();
 
         if (!(other instanceof RubyHash)) {
             if (!other.respondsTo("to_hash")) {
                 return runtime.getFalse();
             } else {
                 return RuntimeHelpers.rbEqual(context, other, this);
             }
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
 
-                    if (!RecursiveComparator.compare(context, method, value, value2, seen).isTrue()) {
+                    if (!invokedynamic(context, value, method, value2).isTrue()) {
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
     public IRubyObject op_equal(final ThreadContext context, IRubyObject other) {
-        return RecursiveComparator.compare(context, "==", this, other, null);
+        return RecursiveComparator.compare(context, MethodIndex.OP_EQUAL, this, other);
     }
 
     /** rb_hash_eql
      * 
      */
     @JRubyMethod(name = "eql?")
     public IRubyObject op_eql19(final ThreadContext context, IRubyObject other) {
-        return RecursiveComparator.compare(context, "eql?", this, other, null);
+        return RecursiveComparator.compare(context, MethodIndex.EQL, this, other);
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
     @JRubyMethod(name = "hash", compat = RUBY1_8)
     public RubyFixnum hash() {
         final Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         if (size == 0 || runtime.isInspecting(this)) return RubyFixnum.zero(runtime);
         final long hash[] = new long[]{size};
         
         try {
             runtime.registerInspecting(this);
             visitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     hash[0] ^= invokedynamic(context, key, HASH).convertToInteger().getLongValue();
                     hash[0] ^= invokedynamic(context, value, HASH).convertToInteger().getLongValue();
                 }
             });
         } finally {
             runtime.unregisterInspecting(this);
         }
         return RubyFixnum.newFixnum(runtime, hash[0]);
     }
 
     /** rb_hash_hash
      * 
      */
     @JRubyMethod(name = "hash", compat = RUBY1_9)
     public RubyFixnum hash19() {
         final Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         return (RubyFixnum)getRuntime().execRecursiveOuter(new Ruby.RecursiveFunction() {
                 public IRubyObject call(IRubyObject obj, boolean recur) {
                     if(size == 0) {
                         return RubyFixnum.zero(runtime);
                     }
                     final long[] h = new long[]{size};
                     if(recur) {
                         h[0] ^= RubyNumeric.num2long(invokedynamic(context, runtime.getHash(), HASH));
                     } else {
                         visitAll(new Visitor() {
                                 public void visit(IRubyObject key, IRubyObject value) {
                                     h[0] ^= invokedynamic(context, key, HASH).convertToInteger().getLongValue();
                                     h[0] ^= invokedynamic(context, value, HASH).convertToInteger().getLongValue();
                                 }
                             });
 
                     }
                     return runtime.newFixnum(h[0]);
                 }
             }, this);
     }
 
     /** rb_hash_fetch
      *
      */
     @JRubyMethod(required = 1, optional = 1)
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
     public RubyHash eachCommon(final ThreadContext context, final Block block) {
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
 
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject each(final ThreadContext context, final Block block) {
         return block.isGiven() ? eachCommon(context, block) : enumeratorize(context.getRuntime(), this, "each");
     }
 
     @JRubyMethod(name = "each", compat = RUBY1_9)
     public IRubyObject each19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_pairCommon(context, block, true) : enumeratorize(context.getRuntime(), this, "each");
     }
 
     /** rb_hash_each_pair
      *
      */
     public RubyHash each_pairCommon(final ThreadContext context, final Block block, final boolean oneNine) {
         final Ruby runtime = getRuntime();
 
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 // rb_yield_values(2,...) equivalent
                 if (oneNine) {
                     block.yield(context, RubyArray.newArray(runtime, key, value));
                 } else {
                     block.yieldArray(context, RubyArray.newArray(runtime, key, value), null, null);
                 }
             }
         });
 
         return this;	
     }
 
     @JRubyMethod
     public IRubyObject each_pair(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_pairCommon(context, block, context.getRuntime().is1_9()) : enumeratorize(context.getRuntime(), this, "each_pair");
     }
 
     /** rb_hash_each_value
      *
      */
     public RubyHash each_valueCommon(final ThreadContext context, final Block block) {
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 block.yield(context, value);
             }
         });
 
         return this;
     }
 
     @JRubyMethod
     public IRubyObject each_value(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_valueCommon(context, block) : enumeratorize(context.getRuntime(), this, "each_value");
     }
 
     /** rb_hash_each_key
      *
      */
     public RubyHash each_keyCommon(final ThreadContext context, final Block block) {
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 block.yield(context, key);
             }
         });
 
         return this;
     }
 
     @JRubyMethod
     public IRubyObject each_key(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_keyCommon(context, block) : enumeratorize(context.getRuntime(), this, "each_key");
     }
 
     @JRubyMethod(name = "select!", compat = RUBY1_9)
     public IRubyObject select_bang(final ThreadContext context, final Block block) {
         if (block.isGiven()) {
             if (keep_ifCommon(context, block)) {
                 return this;
             } else {
                 return context.getRuntime().getNil();
             }
         } else {
             return enumeratorize(context.getRuntime(), this, "each_key");
         }
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject keep_if(final ThreadContext context, final Block block) {
         if (block.isGiven()) {
             keep_ifCommon(context, block);
             return this;
         } else {
             return enumeratorize(context.getRuntime(), this, "each_key");
         }
     }
     
     public boolean keep_ifCommon(final ThreadContext context, final Block block) {
         testFrozen("hash");
         final boolean[] modified = {false};
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 if (!block.yieldSpecific(context, key, value).isTrue()) {
                     modified[0] = true;
                     remove(key);
                 }
             }
         });
         return modified[0];
     }
 
     /** rb_hash_sort
      *
      */
     @JRubyMethod
     public IRubyObject sort(ThreadContext context, Block block) {
         return to_a().sort_bang(context, block);
     }
 
     /** rb_hash_index
      *
      */
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject index(ThreadContext context, IRubyObject expected) {
         IRubyObject key = internalIndex(context, expected);
         return key != null ? key : context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "index", compat = RUBY1_9)
     public IRubyObject index19(ThreadContext context, IRubyObject expected) {
         context.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Hash#index is deprecated; use Hash#key");
         return key(context, expected);
     }
 
     @JRubyMethod(compat = RUBY1_9)
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
         try {
             final RubyArray keys = RubyArray.newArray(runtime, size);
 
             visitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     keys.append(key);
                 }
             });
 
             return keys;
         } catch (NegativeArraySizeException nase) {
             throw concurrentModification();
         }
     }
 
     /** rb_hash_values
      *
      */
     @JRubyMethod(name = "values")
     public RubyArray rb_values() {
         try {
             final RubyArray values = RubyArray.newArray(getRuntime(), size);
 
             visitAll(new Visitor() {
                 public void visit(IRubyObject key, IRubyObject value) {
                     values.append(value);
                 }
             });
 
             return values;
         } catch (NegativeArraySizeException nase) {
             throw concurrentModification();
         }
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
     @JRubyMethod
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
     @JRubyMethod
     public IRubyObject select(final ThreadContext context, final Block block) {
         final Ruby runtime = getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "select");
 
         final RubyArray result = runtime.newArray();
 
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 if (block.yieldArray(context, runtime.newArray(key, value), null, null).isTrue()) {
                     result.append(runtime.newArray(key, value));
                 }
             }
         });
 
         return result;
     }
 
     @JRubyMethod(name = "select", compat = RUBY1_9)
     public IRubyObject select19(final ThreadContext context, final Block block) {
         final Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, this, "select");
 
         final RubyHash result = newHash(runtime);
 
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 if (block.yieldArray(context, runtime.newArray(key, value), null, null).isTrue()) {
                     result.fastASet(key, value);
                 }
             }
         });
 
         return result;        
     }
 
     /** rb_hash_delete_if
      *
      */
     public RubyHash delete_ifInternal(final ThreadContext context, final Block block) {
         modify();
 
         final Ruby runtime = getRuntime();
         final RubyHash self = this;
         iteratorVisitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 if (block.yieldArray(context, RubyArray.newArray(runtime, key, value), null, null).isTrue()) {
                     self.delete(context, key, Block.NULL_BLOCK);
                 }
             }
         });
 
         return this;
     }
 
     @JRubyMethod
     public IRubyObject delete_if(final ThreadContext context, final Block block) {
         return block.isGiven() ? delete_ifInternal(context, block) : enumeratorize(context.getRuntime(), this, "delete_if");
     }
 
     /** rb_hash_reject
      *
      */
     public RubyHash rejectInternal(ThreadContext context, Block block) {
         return ((RubyHash)dup()).delete_ifInternal(context, block);
     }
 
     @JRubyMethod
     public IRubyObject reject(final ThreadContext context, final Block block) {
         return block.isGiven() ? rejectInternal(context, block) : enumeratorize(context.getRuntime(), this, "reject");
     }
 
     /** rb_hash_reject_bang
      *
      */
     public IRubyObject reject_bangInternal(ThreadContext context, Block block) {
         int n = size;
         delete_if(context, block);
         if (n == size) return getRuntime().getNil();
         return this;
     }
 
     @JRubyMethod(name = "reject!")
     public IRubyObject reject_bang(final ThreadContext context, final Block block) {
         return block.isGiven() ? reject_bangInternal(context, block) : enumeratorize(context.getRuntime(), this, "reject!");
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
     @JRubyMethod(name = {"merge!", "update"}, required = 1, compat = RUBY1_8)
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
                     if (existing != null) {
                         value = block.yield(context, RubyArray.newArrayNoCopy(runtime, new IRubyObject[]{key, existing, value}));
                     }
                 }
                 self.op_aset(context, key, value);
             }
         });
 
         return this;
     }
 
     /** rb_hash_update
      *
      */
     @JRubyMethod(name = {"merge!", "update"}, required = 1, compat = RUBY1_9)
     public RubyHash merge_bang19(final ThreadContext context, final IRubyObject other, final Block block) {
         final RubyHash otherHash = other.convertToHash();
         modify();
 
         if (otherHash.empty_p().isTrue()) {
             return this;
         }
 
         final Ruby runtime = getRuntime();
         final RubyHash self = this;
         otherHash.visitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 if (block.isGiven()) {
                     IRubyObject existing = self.internalGet(key);
                     if (existing != null) {
                         value = block.yield(context, RubyArray.newArrayNoCopy(runtime, new IRubyObject[]{key, existing, value}));
                     }
                 }
                 self.op_aset(context, key, value);
             }
         });
 
         return this;
     }
 
     /** rb_hash_merge
      *
      */
     @JRubyMethod
     public RubyHash merge(ThreadContext context, IRubyObject other, Block block) {
         return ((RubyHash)dup()).merge_bang(context, other, block);
     }
 
     /** rb_hash_replace
      *
      */
     @JRubyMethod(visibility = PRIVATE, compat = RUBY1_8)
     public RubyHash initialize_copy(ThreadContext context, IRubyObject other) {
         return replace(context, other);
     }
 
     /** rb_hash_replace
      *
      */
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = PRIVATE, compat = RUBY1_9)
     public RubyHash initialize_copy19(ThreadContext context, IRubyObject other) {
         return replace19(context, other);
     }
 
     /** rb_hash_replace
      *
      */
     @JRubyMethod(name = "replace", required = 1, compat = RUBY1_8)
     public RubyHash replace(final ThreadContext context, IRubyObject other) {
         final RubyHash self = this;
         return replaceCommon(context, other, new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 self.op_aset(context, key, value);
             }
         });
     }
 
     @JRubyMethod(name = "replace", required = 1, compat = RUBY1_9)
     public RubyHash replace19(final ThreadContext context, IRubyObject other) {
         final RubyHash self = this;
         return replaceCommon19(context, other, new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
                 self.op_aset19(context, key, value);
             }
         });
     }
 
     private RubyHash replaceCommon(final ThreadContext context, IRubyObject other, Visitor visitor) {
         final RubyHash otherHash = other.convertToHash();
 
         if (this == otherHash) return this;
 
         rb_clear();
 
         if (!isComparedByIdentity() && otherHash.isComparedByIdentity()) {
             setComparedByIdentity(true);
         }
 
         otherHash.visitAll(visitor);
 
         ifNone = otherHash.ifNone;
 
         if ((otherHash.flags & PROCDEFAULT_HASH_F) != 0) {
             flags |= PROCDEFAULT_HASH_F;
         } else {
             flags &= ~PROCDEFAULT_HASH_F;
         }
 
         return this;
     }
 
     private RubyHash replaceCommon19(final ThreadContext context, IRubyObject other, Visitor visitor) {
         final RubyHash otherHash = other.convertToHash();
 
         rb_clear();
 
         if (this == otherHash) return this;
 
         if (!isComparedByIdentity() && otherHash.isComparedByIdentity()) {
             setComparedByIdentity(true);
         }
 
         otherHash.visitAll(visitor);
 
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
 
     @JRubyMethod(name = "assoc", compat = RUBY1_9)
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
 
     @JRubyMethod(name = "rassoc", compat = RUBY1_9)
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
 
     @JRubyMethod(name = "flatten", compat = RUBY1_9)
     public IRubyObject flatten(ThreadContext context) {
         RubyArray ary = to_a(); 
         ary.callMethod(context, "flatten!", RubyFixnum.one(context.getRuntime()));
         return ary;
     }
 
     @JRubyMethod(name = "flatten", compat = RUBY1_9)
     public IRubyObject flatten(ThreadContext context, IRubyObject level) {
         RubyArray ary = to_a();
         ary.callMethod(context, "flatten!", level);
         return ary;
     }
 
     @JRubyMethod(name = "compare_by_identity", compat = RUBY1_9)
     public IRubyObject getCompareByIdentity(ThreadContext context) {
         modify();
         setComparedByIdentity(true);
         return this;
     }
 
     @JRubyMethod(name = "compare_by_identity?", compat = RUBY1_9)
     public IRubyObject getCompareByIdentity_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isComparedByIdentity());
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject dup(ThreadContext context) {
         RubyHash dup = (RubyHash) super.dup();
         dup.setComparedByIdentity(isComparedByIdentity());
         return dup;
     }
 
     @JRubyMethod(name = "clone", compat = RUBY1_9)
     public IRubyObject rbClone(ThreadContext context) {
         RubyHash clone = (RubyHash) super.rbClone();
         clone.setComparedByIdentity(isComparedByIdentity());
         return clone;
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
         for (int i = 0; i < size; i++) {
             result.fastASetCheckString(input.getRuntime(), input.unmarshalObject(), input.unmarshalObject());
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
         return internalGet(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key)) != null;
     }
 
     public boolean containsValue(Object value) {
         return hasValue(getRuntime().getCurrentContext(), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), value));
     }
 
     public Object get(Object key) {
         IRubyObject gotten = internalGet(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key));
         return gotten == null ? null : gotten.toJava(Object.class);
     }
 
     public Object put(Object key, Object value) {
         internalPut(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), value));
         return value;
     }
 
     public Object remove(Object key) {
         IRubyObject rubyKey = JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key);
         return internalDelete(rubyKey).value;
     }
 
     public void putAll(Map map) {
         Ruby runtime = getRuntime();
         for (Iterator<Map.Entry> iter = map.entrySet().iterator(); iter.hasNext();) {
             Map.Entry entry = iter.next();
             internalPut(JavaUtil.convertJavaToUsableRubyObject(runtime, entry.getKey()), JavaUtil.convertJavaToUsableRubyObject(runtime, entry.getValue()));
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
 
     private final RaiseException concurrentModification() {
         return getRuntime().newConcurrencyError(
                 "Detected invalid hash contents due to unsynchronized modifications with concurrent users");
     }
 
     /**
      * Is this object compared by identity or not? Shortcut for doing
      * getFlag(COMPARE_BY_IDENTITY_F).
      *
      * @return true if this object is compared by identity, false otherwise
      */
     protected boolean isComparedByIdentity() {
         return (flags & COMPARE_BY_IDENTITY_F) != 0;
     }
 
     /**
      * Sets whether this object is compared by identity or not. Shortcut for doing
      * setFlag(COMPARE_BY_IDENTITY_F, frozen).
      *
      * @param comparedByIdentity should this object be compared by identity?
      */
     public void setComparedByIdentity(boolean comparedByIdentity) {
         if (comparedByIdentity) {
             flags |= COMPARE_BY_IDENTITY_F;
         } else {
             flags &= ~COMPARE_BY_IDENTITY_F;
         }
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
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 275d1b6af3..b399bbea97 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,1284 +1,1296 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
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
 package org.jruby.runtime;
 
 import org.jruby.runtime.backtrace.BacktraceElement;
 import org.jruby.runtime.backtrace.RubyStackTraceElement;
 import org.jruby.runtime.profile.IProfileData;
 import java.util.ArrayList;
 import org.jruby.runtime.profile.ProfileData;
 import java.util.List;
+import java.util.Set;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyContinuation.Continuation;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.RubyThread;
 import org.jruby.ast.executable.RuntimeCache;
 import org.jruby.exceptions.JumpException.ReturnJump;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.libraries.FiberLibrary.Fiber;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.backtrace.TraceType;
 import org.jruby.runtime.backtrace.TraceType.Gather;
 import org.jruby.runtime.builtin.IRubyObject;
+import org.jruby.util.RecursiveComparator;
 
 public final class ThreadContext {
     public static ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
         return context;
     }
     
     private final static int INITIAL_SIZE = 10;
     private final static int INITIAL_FRAMES_SIZE = 10;
     
     /** The number of calls after which to do a thread event poll */
     private final static int CALL_POLL_COUNT = 0xFFF;
 
     // runtime, nil, and runtimeCache cached here for speed of access from any thread
     public final Ruby runtime;
     public final IRubyObject nil;
     public final RuntimeCache runtimeCache;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     private Fiber fiber;
     
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     private Frame[] frameStack = new Frame[INITIAL_FRAMES_SIZE];
     private int frameIndex = -1;
 
     private BacktraceElement[] backtrace = new BacktraceElement[INITIAL_FRAMES_SIZE];
     private int backtraceIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
 
     private static final Continuation[] EMPTY_CATCHTARGET_STACK = new Continuation[0];
     private Continuation[] catchStack = EMPTY_CATCHTARGET_STACK;
     private int catchIndex = -1;
     
     private boolean isProfiling = false;
     // The flat profile data for this thread
 	private IProfileData profileData;
 	
     // In certain places, like grep, we don't use real frames for the
     // call blocks. This has the effect of not setting the backref in
     // the correct frame - this delta is activated to the place where
     // the grep is running in so that the backref will be set in an
     // appropriate place.
     private int rubyFrameDelta = 0;
     private boolean eventHooksEnabled = true;
 
     CallType lastCallType;
 
     Visibility lastVisibility;
 
     IRubyObject lastExitStatus;
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         this.nil = runtime.getNil();
         if (runtime.getInstanceConfig().isProfilingEntireRun())
             startProfiling();
 
         this.runtimeCache = runtime.getRuntimeCache();
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         StaticScope topStaticScope = new LocalStaticScope(null);
         pushScope(new ManyVarsDynamicScope(topStaticScope, null));
 
         Frame[] stack = frameStack;
         int length = stack.length;
         for (int i = 0; i < length; i++) {
             stack[i] = new Frame();
         }
         BacktraceElement[] stack2 = backtrace;
         int length2 = stack2.length;
         for (int i = 0; i < length2; i++) {
             stack2[i] = new BacktraceElement();
         }
         ThreadContext.pushBacktrace(this, "", "", "", 0);
         ThreadContext.pushBacktrace(this, "", "", "", 0);
         fiber = (Fiber) runtime.getRootFiber();
     }
 
     @Override
     protected void finalize() throws Throwable {
         thread.dispose();
     }
     
     public final Ruby getRuntime() {
         return runtime;
     }
     
     public IRubyObject getErrorInfo() {
         return thread.getErrorInfo();
     }
     
     public IRubyObject setErrorInfo(IRubyObject errorInfo) {
         thread.setErrorInfo(errorInfo);
         return errorInfo;
     }
     
     public ReturnJump returnJump(IRubyObject value) {
         return new ReturnJump(getFrameJumpTarget(), value);
     }
     
     /**
      * Returns the lastCallStatus.
      * @return LastCallStatus
      */
     public void setLastCallStatus(CallType callType) {
         lastCallType = callType;
     }
 
     public CallType getLastCallType() {
         return lastCallType;
     }
 
     public void setLastVisibility(Visibility visibility) {
         lastVisibility = visibility;
     }
 
     public Visibility getLastVisibility() {
         return lastVisibility;
     }
     
     public void setLastCallStatusAndVisibility(CallType callType, Visibility visibility) {
         lastCallType = callType;
         lastVisibility = visibility;
     }
     
     public IRubyObject getLastExitStatus() {
         return lastExitStatus;
     }
     
     public void setLastExitStatus(IRubyObject lastExitStatus) {
         this.lastExitStatus = lastExitStatus;
     }
 
     public void printScope() {
         System.out.println("SCOPE STACK:");
         for (int i = 0; i <= scopeIndex; i++) {
             System.out.println(scopeStack[i]);
         }
     }
 
     public DynamicScope getCurrentScope() {
         return scopeStack[scopeIndex];
     }
     
     public DynamicScope getPreviousScope() {
         return scopeStack[scopeIndex - 1];
     }
     
     private void expandFramesIfNecessary() {
         int newSize = frameStack.length * 2;
         frameStack = fillNewFrameStack(new Frame[newSize], newSize);
     }
 
     private Frame[] fillNewFrameStack(Frame[] newFrameStack, int newSize) {
         System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
 
         for (int i = frameStack.length; i < newSize; i++) {
             newFrameStack[i] = new Frame();
         }
         
         return newFrameStack;
     }
     
     private void expandParentsIfNecessary() {
         int newSize = parentStack.length * 2;
         RubyModule[] newParentStack = new RubyModule[newSize];
 
         System.arraycopy(parentStack, 0, newParentStack, 0, parentStack.length);
 
         parentStack = newParentStack;
     }
     
     public void pushScope(DynamicScope scope) {
         int index = ++scopeIndex;
         DynamicScope[] stack = scopeStack;
         stack[index] = scope;
         if (index + 1 == stack.length) {
             expandScopesIfNecessary();
         }
     }
     
     public void popScope() {
         scopeStack[scopeIndex--] = null;
     }
     
     private void expandScopesIfNecessary() {
         int newSize = scopeStack.length * 2;
         DynamicScope[] newScopeStack = new DynamicScope[newSize];
 
         System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
 
         scopeStack = newScopeStack;
     }
     
     public RubyThread getThread() {
         return thread;
     }
     
     public void setThread(RubyThread thread) {
         this.thread = thread;
 
         // associate the thread with this context, unless we're clearing the reference
         if (thread != null) {
             thread.setContext(this);
         }
     }
     
     public Fiber getFiber() {
         return fiber;
     }
     
     public void setFiber(Fiber fiber) {
         this.fiber = fiber;
     }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         int newSize = catchStack.length * 2;
         if (newSize == 0) newSize = 1;
         Continuation[] newCatchStack = new Continuation[newSize];
 
         System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
         catchStack = newCatchStack;
     }
     
     public void pushCatch(Continuation catchTarget) {
         int index = ++catchIndex;
         if (index == catchStack.length) {
             expandCatchIfNecessary();
         }
         catchStack[index] = catchTarget;
     }
     
     public void popCatch() {
         catchIndex--;
     }
 
     /**
      * Find the active Continuation for the given tag. Must be called with an
      * interned string.
      *
      * @param tag The interned string to search for
      * @return The continuation associated with this tag
      */
     public Continuation getActiveCatch(Object tag) {
         for (int i = catchIndex; i >= 0; i--) {
             Continuation c = catchStack[i];
             if (runtime.is1_9()) {
                 if (c.tag == tag) return c;
             } else {
                 if (c.tag.equals(tag)) return c;
             }
         }
         return null;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         Frame currentFrame = stack[index - 1];
         stack[index].updateFrame(currentFrame);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private Frame pushFrame(Frame frame) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index] = frame;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
         return frame;
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(clazz, self, name, block, callNumber);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushEvalFrame(IRubyObject self) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrameForEval(self, callNumber);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushFrame(String name) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(name);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     public void pushFrame() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     public void popFrame() {
         Frame frame = frameStack[frameIndex--];
         
         frame.clear();
     }
         
     private void popFrameReal(Frame oldFrame) {
         frameStack[frameIndex--] = oldFrame;
     }
     
     public Frame getCurrentFrame() {
         return frameStack[frameIndex];
     }
 
     public int getRubyFrameDelta() {
         return this.rubyFrameDelta;
     }
     
     public void setRubyFrameDelta(int newDelta) {
         this.rubyFrameDelta = newDelta;
     }
 
     public Frame getCurrentRubyFrame() {
         return frameStack[frameIndex-rubyFrameDelta];
     }
     
     public Frame getNextFrame() {
         int index = frameIndex;
         Frame[] stack = frameStack;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
         return stack[index + 1];
     }
     
     public Frame getPreviousFrame() {
         int index = frameIndex;
         return index < 1 ? null : frameStack[index - 1];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
 
     public Frame[] getFrames(int delta) {
         int top = frameIndex + delta;
         Frame[] frames = new Frame[top + 1];
         for (int i = 0; i <= top; i++) {
             frames[i] = frameStack[i].duplicateForBacktrace();
         }
         return frames;
     }
 
     /////////////////// BACKTRACE ////////////////////
 
     private static void expandBacktraceIfNecessary(ThreadContext context) {
         int newSize = context.backtrace.length * 2;
         context.backtrace = fillNewBacktrace(context, new BacktraceElement[newSize], newSize);
     }
 
     private static BacktraceElement[] fillNewBacktrace(ThreadContext context, BacktraceElement[] newBacktrace, int newSize) {
         System.arraycopy(context.backtrace, 0, newBacktrace, 0, context.backtrace.length);
 
         for (int i = context.backtrace.length; i < newSize; i++) {
             newBacktrace[i] = new BacktraceElement();
         }
 
         return newBacktrace;
     }
 
     public static void pushBacktrace(ThreadContext context, String klass, String method, ISourcePosition position) {
         int index = ++context.backtraceIndex;
         BacktraceElement[] stack = context.backtrace;
         BacktraceElement.update(stack[index], klass, method, position);
         if (index + 1 == stack.length) {
             ThreadContext.expandBacktraceIfNecessary(context);
         }
     }
 
     public static void pushBacktrace(ThreadContext context, String klass, String method, String file, int line) {
         int index = ++context.backtraceIndex;
         BacktraceElement[] stack = context.backtrace;
         BacktraceElement.update(stack[index], klass, method, file, line);
         if (index + 1 == stack.length) {
             ThreadContext.expandBacktraceIfNecessary(context);
         }
     }
 
     public static void popBacktrace(ThreadContext context) {
         context.backtraceIndex--;
     }
 
     /**
      * Search the frame stack for the given JumpTarget. Return true if it is
      * found and false otherwise. Skip the given number of frames before
      * beginning the search.
      * 
      * @param target The JumpTarget to search for
      * @param skipFrames The number of frames to skip before searching
      * @return
      */
     public boolean isJumpTargetAlive(int target, int skipFrames) {
         for (int i = frameIndex - skipFrames; i >= 0; i--) {
             if (frameStack[i].getJumpTarget() == target) return true;
         }
         return false;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public int getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlock();
     }
     
     public String getFile() {
         return backtrace[backtraceIndex].filename;
     }
     
     public int getLine() {
         return backtrace[backtraceIndex].line;
     }
     
     public void setFile(String file) {
         backtrace[backtraceIndex].filename = file;
     }
     
     public void setLine(int line) {
         backtrace[backtraceIndex].line = line;
     }
     
     public void setFileAndLine(String file, int line) {
         backtrace[backtraceIndex].filename = file;
         backtrace[backtraceIndex].line = line;
     }
 
     public void setFileAndLine(ISourcePosition position) {
         backtrace[backtraceIndex].filename = position.getFile();
         backtrace[backtraceIndex].line = position.getStartLine();
     }
     
     public Visibility getCurrentVisibility() {
         return getCurrentFrame().getVisibility();
     }
     
     public Visibility getPreviousVisibility() {
         return getPreviousFrame().getVisibility();
     }
     
     public void setCurrentVisibility(Visibility visibility) {
         getCurrentFrame().setVisibility(visibility);
     }
     
     public void pollThreadEvents() {
         thread.pollThreadEvents(this);
     }
     
     public int callNumber = 0;
 
     public int getCurrentTarget() {
         return callNumber;
     }
     
     public void callThreadPoll() {
         if ((callNumber++ & CALL_POLL_COUNT) == 0) pollThreadEvents();
     }
 
     public static void callThreadPoll(ThreadContext context) {
         if ((context.callNumber++ & CALL_POLL_COUNT) == 0) context.pollThreadEvents();
     }
     
     public void trace(RubyEvent event, String name, RubyModule implClass) {
         trace(event, name, implClass, backtrace[backtraceIndex].filename, backtrace[backtraceIndex].line);
     }
 
     public void trace(RubyEvent event, String name, RubyModule implClass, String file, int line) {
         runtime.callEventHooks(this, event, file, line, name, implClass);
     }
     
     public void pushRubyClass(RubyModule currentModule) {
         // FIXME: this seems like a good assertion, but it breaks compiled code and the code seems
         // to run without it...
         //assert currentModule != null : "Can't push null RubyClass";
         int index = ++parentIndex;
         RubyModule[] stack = parentStack;
         stack[index] = currentModule;
         if (index + 1 == stack.length) {
             expandParentsIfNecessary();
         }
     }
     
     public RubyModule popRubyClass() {
         int index = parentIndex;
         RubyModule[] stack = parentStack;
         RubyModule ret = stack[index];
         stack[index] = null;
         parentIndex = index - 1;
         return ret;
     }
     
     public RubyModule getRubyClass() {
         assert parentIndex != -1 : "Trying to get RubyClass from empty stack";
         RubyModule parentModule = parentStack[parentIndex];
         return parentModule.getNonIncludedClass();
     }
 
     public RubyModule getPreviousRubyClass() {
         assert parentIndex != 0 : "Trying to get RubyClass from too-shallow stack";
         RubyModule parentModule = parentStack[parentIndex - 1];
         return parentModule.getNonIncludedClass();
     }
     
     public boolean getConstantDefined(String internedName) {
         IRubyObject value = getConstant(internedName);
 
         return value != null;
     }
     
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     public IRubyObject getConstant(String internedName) {
         return getCurrentScope().getStaticScope().getConstant(runtime, internedName, runtime.getObject());
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a null const decl
      */
     public IRubyObject setConstantInCurrent(String internedName, IRubyObject result) {
         RubyModule module;
 
         if ((module = getCurrentScope().getStaticScope().getModule()) != null) {
             module.fastSetConstant(internedName, result);
             return result;
         }
 
         // TODO: wire into new exception handling mechanism
         throw runtime.newTypeError("no class/module to define constant");
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name.
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInModule(String internedName, IRubyObject target, IRubyObject result) {
         if (!(target instanceof RubyModule)) {
             throw runtime.newTypeError(target.toString() + " is not a class/module");
         }
         RubyModule module = (RubyModule)target;
         module.fastSetConstant(internedName, result);
         
         return result;
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInObject(String internedName, IRubyObject result) {
         runtime.getObject().fastSetConstant(internedName, result);
         
         return result;
     }
     
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, RubyStackTraceElement element) {
         RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'");
         backtrace.append(str);
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public IRubyObject createCallerBacktrace(Ruby runtime, int level) {
         runtime.incrementCallerCount();
         
         RubyStackTraceElement[] trace = gatherCallerBacktrace(level);
         
         RubyArray newTrace = runtime.newArray(trace.length - level);
 
         for (int i = level; i < trace.length; i++) {
             addBackTraceElement(runtime, newTrace, trace[i]);
         }
         
         if (RubyInstanceConfig.LOG_CALLERS) TraceType.dumpCaller(newTrace);
         
         return newTrace;
     }
     
     public RubyStackTraceElement[] gatherCallerBacktrace(int level) {
         Thread nativeThread = thread.getNativeThread();
 
         // Future thread or otherwise unforthgiving thread impl.
         if (nativeThread == null) return new RubyStackTraceElement[] {};
 
         BacktraceElement[] copy = new BacktraceElement[backtraceIndex + 1];
 
         System.arraycopy(backtrace, 0, copy, 0, backtraceIndex + 1);
         RubyStackTraceElement[] trace = Gather.CALLER.getBacktraceData(this, false).getBacktrace(runtime);
 
         return trace;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public Frame[] createBacktrace(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         Frame[] traceFrames;
         
         if (traceSize <= 0) return null;
         
         if (nativeException) {
             // assert level == 0;
             traceFrames = new Frame[traceSize + 1];
             traceFrames[traceSize] = frameStack[frameIndex];
         } else {
             traceFrames = new Frame[traceSize];
         }
         
         System.arraycopy(frameStack, 0, traceFrames, 0, traceSize);
         
         return traceFrames;
     }
 
     public boolean isEventHooksEnabled() {
         return eventHooksEnabled;
     }
 
     public void setEventHooksEnabled(boolean flag) {
         eventHooksEnabled = flag;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public BacktraceElement[] createBacktrace2(int level, boolean nativeException) {
         BacktraceElement[] newTrace = new BacktraceElement[backtraceIndex + 1];
         for (int i = 0; i <= backtraceIndex; i++) {
             newTrace[i] = backtrace[i].clone();
         }
         return newTrace;
     }
     
     private static String createRubyBacktraceString(StackTraceElement element) {
         return element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'";
     }
     
     public static String createRawBacktraceStringFromThrowable(Throwable t) {
         StackTraceElement[] javaStackTrace = t.getStackTrace();
         
         StringBuilder buffer = new StringBuilder();
         if (javaStackTrace != null && javaStackTrace.length > 0) {
             StackTraceElement element = javaStackTrace[0];
 
             buffer
                     .append(createRubyBacktraceString(element))
                     .append(": ")
                     .append(t.toString())
                     .append("\n");
             for (int i = 1; i < javaStackTrace.length; i++) {
                 element = javaStackTrace[i];
                 
                 buffer
                         .append("\tfrom ")
                         .append(createRubyBacktraceString(element));
                 if (i + 1 < javaStackTrace.length) buffer.append("\n");
             }
         }
         
         return buffer.toString();
     }
 
     public static RubyStackTraceElement[] gatherRawBacktrace(Ruby runtime, StackTraceElement[] stackTrace) {
         List trace = new ArrayList(stackTrace.length);
         
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             trace.add(new RubyStackTraceElement(element));
         }
 
         RubyStackTraceElement[] rubyStackTrace = new RubyStackTraceElement[trace.size()];
         return (RubyStackTraceElement[])trace.toArray(rubyStackTrace);
     }
 
     private Frame pushFrameForBlock(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         f.setVisibility(binding.getVisibility());
         
         return lastFrame;
     }
 
     private Frame pushFrameForEval(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         f.setVisibility(binding.getVisibility());
         return lastFrame;
     }
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
 
     public void preExtensionLoad(IRubyObject self) {
         pushFrame();
         pushRubyClass(runtime.getObject());
         getCurrentFrame().setSelf(self);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
     }
 
     public void postExtensionLoad() {
         popFrame();
         popRubyClass();
     }
     
     public void preCompiledClass(RubyModule type, StaticScope staticScope) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         staticScope.setModule(type);
         pushScope(DynamicScope.newDynamicScope(staticScope));
     }
 
     public void preCompiledClassDummyScope(RubyModule type, StaticScope staticScope) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         staticScope.setModule(type);
         pushScope(staticScope.getDummyScope());
     }
 
     public void postCompiledClass() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preScopeNode(StaticScope staticScope) {
         pushScope(DynamicScope.newDynamicScope(staticScope, getCurrentScope()));
     }
 
     public void postScopeNode() {
         popScope();
     }
 
     public void preClassEval(StaticScope staticScope, RubyModule type) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
 
         pushScope(DynamicScope.newDynamicScope(staticScope, null));
     }
     
     public void postClassEval() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preBsfApply(String[] names) {
         // FIXME: I think we need these pushed somewhere?
         LocalStaticScope staticScope = new LocalStaticScope(null);
         staticScope.setVariables(names);
         pushFrame();
     }
     
     public void postBsfApply() {
         popFrame();
     }
     
     public void preMethodFrameAndScope(RubyModule clazz, String name, IRubyObject self, Block block, 
             StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block);
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void preMethodFrameAndDummyScope(RubyModule clazz, String name, IRubyObject self, Block block, 
             StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block);
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
 
     public void preMethodNoFrameAndDummyScope(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodFrameAndScope() {
         popRubyClass();
         popScope();
         popFrame();
     }
     
     public void preMethodFrameOnly(RubyModule clazz, String name, IRubyObject self, Block block) {
         pushRubyClass(clazz);
         pushCallFrame(clazz, name, self, block);
     }
     
     public void postMethodFrameOnly() {
         popFrame();
         popRubyClass();
     }
     
     public void preMethodScopeOnly(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postMethodScopeOnly() {
         popRubyClass();
         popScope();
     }
     
     public void preMethodBacktraceAndScope(String name, RubyModule clazz, StaticScope staticScope) {
         preMethodScopeOnly(clazz, staticScope);
     }
     
     public void postMethodBacktraceAndScope() {
         postMethodScopeOnly();
     }
     
     public void preMethodBacktraceOnly(String name) {
     }
 
     public void preMethodBacktraceDummyScope(RubyModule clazz, String name, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodBacktraceOnly() {
     }
 
     public void postMethodBacktraceDummyScope() {
         popRubyClass();
         popScope();
     }
     
     public void prepareTopLevel(RubyClass objectClass, IRubyObject topSelf) {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
         
         pushRubyClass(objectClass);
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
         
         getCurrentScope().getStaticScope().setModule(objectClass);
     }
     
     public void preNodeEval(RubyModule rubyClass, IRubyObject self, String name) {
         pushRubyClass(rubyClass);
         pushEvalFrame(self);
     }
 
     public void preNodeEval(RubyModule rubyClass, IRubyObject self) {
         pushRubyClass(rubyClass);
         pushEvalFrame(self);
     }
     
     public void postNodeEval() {
         popFrame();
         popRubyClass();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
     public void preExecuteUnder(RubyModule executeUnderClass, Block block) {
         Frame frame = getCurrentFrame();
         
         pushRubyClass(executeUnderClass);
         DynamicScope scope = getCurrentScope();
         StaticScope sScope = new BlockStaticScope(scope.getStaticScope());
         sScope.setModule(executeUnderClass);
         pushScope(DynamicScope.newDynamicScope(sScope, scope));
         pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), block);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popScope();
         popRubyClass();
     }
     
     public void preMproc() {
         pushFrame();
     }
     
     public void postMproc() {
         popFrame();
     }
     
     public void preRunThread(Frame[] currentFrames) {
         for (Frame frame : currentFrames) {
             pushFrame(frame);
         }
     }
     
     public void preTrace() {
         setWithinTrace(true);
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
         setWithinTrace(false);
     }
     
     public Frame preForBlock(Binding binding, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         pushScope(binding.getDynamicScope());
         return lastFrame;
     }
     
     public Frame preYieldSpecificBlock(Binding binding, StaticScope scope, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         // new scope for this invocation of the block, based on parent scope
         pushScope(DynamicScope.newDynamicScope(scope, binding.getDynamicScope()));
         return lastFrame;
     }
     
     public Frame preYieldLightBlock(Binding binding, DynamicScope emptyScope, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         // just push the same empty scope, since we won't use one
         pushScope(emptyScope);
         return lastFrame;
     }
     
     public Frame preYieldNoScope(Binding binding, RubyModule klass) {
         pushRubyClass((klass != null) ? klass : binding.getKlass());
         return pushFrameForBlock(binding);
     }
     
     public void preEvalScriptlet(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postEvalScriptlet() {
         popScope();
     }
     
     public Frame preEvalWithBinding(Binding binding) {
         binding.getFrame().setIsBindingFrame(true);
         Frame lastFrame = pushFrameForEval(binding);
         pushRubyClass(binding.getKlass());
         return lastFrame;
     }
     
     public void postEvalWithBinding(Binding binding, Frame lastFrame) {
         binding.getFrame().setIsBindingFrame(false);
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYield(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYieldLight(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYieldNoScope(Frame lastFrame) {
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void preScopedBody(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postScopedBody() {
         popScope();
     }
     
     /**
      * Is this thread actively tracing at this moment.
      *
      * @return true if so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public boolean isWithinTrace() {
         return isWithinTrace;
     }
     
     /**
      * Set whether we are actively tracing or not on this thread.
      *
      * @param isWithinTrace true is so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public void setWithinTrace(boolean isWithinTrace) {
         this.isWithinTrace = isWithinTrace;
     }
     
     /**
      * Is this thread actively in defined? at the moment.
      *
      * @return true if within defined?
      */
     public boolean isWithinDefined() {
         return isWithinDefined;
     }
     
     /**
      * Set whether we are actively within defined? or not.
      *
      * @param isWithinDefined true if so
      */
     public void setWithinDefined(boolean isWithinDefined) {
         this.isWithinDefined = isWithinDefined;
     }
 
     /**
      * Return a binding representing the current call's state
      * @return the current binding
      */
     public Binding currentBinding() {
         Frame frame = getCurrentFrame();
         return new Binding(frame, getRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding currentBinding(IRubyObject self) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility and self.
      * @param self the self object to use
      * @param visibility the visibility to use
      * @return the current binding using the specified self and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, visibility, getRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified scope and self.
      * @param self the self object to use
      * @param visibility the scope to use
      * @return the current binding using the specified self and scope
      */
     public Binding currentBinding(IRubyObject self, DynamicScope scope) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), scope, backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility, scope, and self. For shared-scope binding
      * consumers like for loops.
      * 
      * @param self the self object to use
      * @param visibility the visibility to use
      * @param scope the scope to use
      * @return the current binding using the specified self, scope, and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility, DynamicScope scope) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, visibility, getRubyClass(), scope, backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the previous call's state
      * @return the current binding
      */
     public Binding previousBinding() {
         Frame frame = getPreviousFrame();
         return new Binding(frame, getPreviousRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the previous call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding previousBinding(IRubyObject self) {
         Frame frame = getPreviousFrame();
         return new Binding(self, frame, frame.getVisibility(), getPreviousRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Get the profile data for this thread (ThreadContext).
      *
      * @return the thread's profile data
      */
     public IProfileData getProfileData() {
         if (profileData == null)
             profileData = new ProfileData(this);
         return profileData;
     }
 
     private int currentMethodSerial = 0;
     
     public int profileEnter(int nextMethod) {
         int previousMethodSerial = currentMethodSerial;
         currentMethodSerial = nextMethod;
         if (isProfiling)
             getProfileData().profileEnter(nextMethod);
         return previousMethodSerial;
     }
 
     public int profileExit(int nextMethod, long startTime) {
         int previousMethodSerial = currentMethodSerial;
         currentMethodSerial = nextMethod;
         if (isProfiling)
             getProfileData().profileExit(nextMethod, startTime);
         return previousMethodSerial;
     }
     
     public void startProfiling() {
         isProfiling = true;
     }
     
     public void stopProfiling() {
         isProfiling = false;
     }
     
     public boolean isProfiling() {
         return isProfiling;
     }
+    
+    public Set<RecursiveComparator.Pair> getRecursiveSet() {
+        return recursiveSet;
+    }
+    
+    public void setRecursiveSet(Set<RecursiveComparator.Pair> recursiveSet) {
+        this.recursiveSet = recursiveSet;
+    }
+    
+    private Set<RecursiveComparator.Pair> recursiveSet;
 }
diff --git a/src/org/jruby/util/RecursiveComparator.java b/src/org/jruby/util/RecursiveComparator.java
index e02a6373e8..c76f557be5 100644
--- a/src/org/jruby/util/RecursiveComparator.java
+++ b/src/org/jruby/util/RecursiveComparator.java
@@ -1,81 +1,91 @@
 package org.jruby.util;
 
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.RubyHash;
 import org.jruby.RubyArray;
 import org.jruby.Ruby;
+import static org.jruby.javasupport.util.RuntimeHelpers.invokedynamic;
 
 import java.util.Set;
 import java.util.HashSet;
 
 public class RecursiveComparator
 {
-    public static IRubyObject compare(ThreadContext context, String method, IRubyObject a, IRubyObject b, Set<Pair> seen) {
+    public static IRubyObject compare(ThreadContext context, final int method, IRubyObject a, IRubyObject b) {
         Ruby runtime = context.getRuntime();
 
         if (a == b) {
             return runtime.getTrue();
         }
+        
+        boolean clear = false; // whether to clear thread-local set (at top comparison)
+
+        try {
+            Set<Pair> seen = null;
+            
+            if (a instanceof RubyHash && b instanceof RubyHash ||
+                a instanceof RubyArray && b instanceof RubyArray) {
+
+                RecursiveComparator.Pair pair = new RecursiveComparator.Pair(a, b);
+
+                if ((seen = context.getRecursiveSet()) == null) {
+                    context.setRecursiveSet(seen = new HashSet<Pair>());
+                    clear = true;
+                }
+                else if (seen.contains(pair)) { // are we recursing?
+                    return runtime.getTrue();
+                }
+
+                seen.add(pair);
+            }
 
-        if (a instanceof RubyHash && b instanceof RubyHash ||
-            a instanceof RubyArray && b instanceof RubyArray) {
-
-            RecursiveComparator.Pair pair = new RecursiveComparator.Pair(a, b);
-
-            if (seen == null) {
-                seen = new HashSet<Pair>();
+            if (a instanceof RubyHash) {
+                RubyHash hash = (RubyHash) a;
+                return hash.compare(context, method, b);
             }
-            else if (seen.contains(pair)) { // are we recursing?
-                return runtime.getTrue();
+            else if (a instanceof RubyArray) {
+                RubyArray array = (RubyArray) a;
+                return array.compare(context, method, b);
             }
-
-            seen.add(pair);
-        }
-
-        if (a instanceof RubyHash) {
-            RubyHash hash = (RubyHash) a;
-            return hash.compare(context, method, b, seen);
-        }
-        else if (a instanceof RubyArray) {
-            RubyArray array = (RubyArray) a;
-            return array.compare(context, method, b, seen);
-        }
-        else {
-            return a.callMethod(context, method, b);
+            else {
+                return invokedynamic(context, a, method, b);
+            }
+        } finally {
+            if (clear) context.setRecursiveSet(null);
         }
     }
 
     public static class Pair
     {
         private int a;
         private int b;
 
         public Pair(IRubyObject a, IRubyObject b) {
             this.a = System.identityHashCode(a);
             this.b = System.identityHashCode(b);
         }
 
         @Override
         public boolean equals(Object other) {
             if (this == other) {
                 return true;
             }
             if (other == null || !(other instanceof Pair)) {
                 return false;
             }
 
             Pair pair = (Pair) other;
 
             return a == pair.a && b == pair.b;
         }
 
         @Override
         public int hashCode() {
             int result = a;
             result = 31 * result + b;
             return result;
         }
     }
 
 }
