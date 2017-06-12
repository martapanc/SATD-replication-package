diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index ca60c6845f..783926c3d7 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1,1110 +1,1105 @@
 /*
  **** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
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
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
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
 import java.util.RandomAccess;
 import java.util.concurrent.Callable;
 
 import org.jcodings.Encoding;
 import org.jcodings.specific.USASCIIEncoding;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.java.util.ArrayUtils;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.runtime.invokedynamic.MethodNames;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.Pack;
 import org.jruby.util.Qsort;
 import org.jruby.util.RecursiveComparator;
 import org.jruby.util.TypeConverter;
 
 import static org.jruby.runtime.Helpers.invokedynamic;
 import static org.jruby.runtime.invokedynamic.MethodNames.HASH;
 import static org.jruby.runtime.invokedynamic.MethodNames.OP_CMP;
 
 /**
  * The implementation of the built-in class Array in Ruby.
  *
  * Concurrency: no synchronization is required among readers, but
  * all users must synchronize externally with writers.
  *
  */
 @JRubyClass(name="Array")
 public class RubyArray extends RubyObject implements List, RandomAccess {
     public static final int DEFAULT_INSPECT_STR_SIZE = 10;
 
     public static RubyClass createArrayClass(Ruby runtime) {
         RubyClass arrayc = runtime.defineClass("Array", runtime.getObject(), ARRAY_ALLOCATOR);
         runtime.setArray(arrayc);
 
         arrayc.index = ClassIndex.ARRAY;
         arrayc.setReifiedClass(RubyArray.class);
         
-        arrayc.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyArray;
-            }
-        };
+        arrayc.kindOf = new RubyModule.JavaClassKindOf(RubyArray.class);
 
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
         Helpers.fillNil(array.values, 0, array.values.length, runtime);
         return array;
     }
 
     public static final RubyArray newArrayLight(final Ruby runtime, final int len) {
         RubyArray array = new RubyArray(runtime, len, false);
         Helpers.fillNil(array.values, 0, array.values.length, runtime);
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
     
     private static final ByteList EMPTY_ARRAY_BYTELIST = new ByteList(ByteList.plain("[]"), USASCIIEncoding.INSTANCE);
     private static final ByteList RECURSIVE_ARRAY_BYTELIST = new ByteList(ByteList.plain("[...]"), USASCIIEncoding.INSTANCE);
 
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
         values = length == 0 ? IRubyObject.NULL_ARRAY : new IRubyObject[length];
     }
 
     private RubyArray(Ruby runtime, int length, boolean objectspace) {
         super(runtime, runtime.getArray(), objectspace);
         values = length == 0 ? IRubyObject.NULL_ARRAY : new IRubyObject[length];
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
         values = length == 0 ? IRubyObject.NULL_ARRAY : new IRubyObject[length];
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
         IRubyObject[] newValues = length == 0 ? IRubyObject.NULL_ARRAY : new IRubyObject[length];
         Helpers.fillNil(newValues, getRuntime());
         values = newValues;
         begin = 0;
     }
 
     private void realloc(int newLength, int valuesLength) {
         IRubyObject[] reallocated = new IRubyObject[newLength];
         if (newLength > valuesLength) {
             Helpers.fillNil(reallocated, valuesLength, newLength, getRuntime());
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
             throw context.runtime.newArgumentError("negative array size");
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
     }
 
     /** rb_ary_modify
      *
      */
     private final void modify() {
         modifyCheck();
         if (isShared) {
             IRubyObject[] vals = new IRubyObject[realLength];
             safeArrayCopy(values, begin, vals, 0, realLength);
             begin = 0;            
             values = vals;
             isShared = false;
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
         Ruby runtime = context.runtime;
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
         Ruby runtime = context.runtime;
 
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
                     Helpers.fillNil(values, begin, begin + ilen, runtime);
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
         Ruby runtime = context.runtime;
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
         return (RubyFixnum) getRuntime().execRecursiveOuter(new Ruby.RecursiveFunction() {
             public IRubyObject call(IRubyObject obj, boolean recur) {
                 int begin = RubyArray.this.begin;
                 long h = realLength;
                 if (recur) {
                     h ^= RubyNumeric.num2long(invokedynamic(context, context.runtime.getArray(), HASH));
                 } else {
                     for (int i = begin; i < begin + realLength; i++) {
                         h = (h << 1) | (h < 0 ? 1 : 0);
                         final IRubyObject value = safeArrayRef(values, i);
                         h ^= RubyNumeric.num2long(invokedynamic(context, value, HASH));
                     }
                 }
                 return getRuntime().newFixnum(h);
             }
         }, RubyArray.this);
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
                 Helpers.fillNil(values, begin + realLength, begin + ((int) beg), getRuntime());
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
             Helpers.fillNil(values, begin + realLength, begin + ((int) beg), getRuntime());
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
         if (len > length) Helpers.fillNil(vals, length, len, getRuntime());
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
     
     public final RubyArray aryDup19() {
         // In 1.9, rb_ary_dup logic changed so that on subclasses of Array,
         // dup returns an instance of Array, rather than an instance of the subclass
         // Also, taintedness and trustedness are not inherited to duplicates
         RubyArray dup = new RubyArray(metaClass.getClassRuntime(), values, begin, realLength);
         dup.isShared = true;
         isShared = true;
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
             tmp = elt(i - begin).convertToArray();
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
 
         Helpers.fillNil(result.values, result.realLength, result.values.length, getRuntime());
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
diff --git a/src/org/jruby/RubyClass.java b/src/org/jruby/RubyClass.java
index 1f3879ef21..31ffedab7d 100644
--- a/src/org/jruby/RubyClass.java
+++ b/src/org/jruby/RubyClass.java
@@ -1,1113 +1,1108 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jruby.runtime.ivars.VariableAccessor;
 import static org.jruby.util.CodegenUtils.ci;
 import static org.jruby.util.CodegenUtils.p;
 import static org.jruby.util.CodegenUtils.sig;
 import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
 import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
 import static org.objectweb.asm.Opcodes.ACC_STATIC;
 import static org.objectweb.asm.Opcodes.ACC_SUPER;
 import static org.objectweb.asm.Opcodes.ACC_VARARGS;
 
 import java.io.IOException;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.java.codegen.RealClassGenerator;
 import org.jruby.java.codegen.Reified;
 import org.jruby.javasupport.Java;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.CacheEntry;
 import org.jruby.runtime.ivars.VariableAccessorField;
 import org.jruby.runtime.ivars.VariableTableManager;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.runtime.opto.Invalidator;
 import org.jruby.util.ClassCache.OneShotClassLoader;
 import org.jruby.util.ClassDefiningClassLoader;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.collections.WeakHashSet;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.objectweb.asm.AnnotationVisitor;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.FieldVisitor;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Class", parent="Module")
 public class RubyClass extends RubyModule {
 
     private static final Logger LOG = LoggerFactory.getLogger("RubyClass");
 
     public static void createClassClass(Ruby runtime, RubyClass classClass) {
         classClass.index = ClassIndex.CLASS;
         classClass.setReifiedClass(RubyClass.class);
-        classClass.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyClass;
-            }
-        };
+        classClass.kindOf = new RubyModule.JavaClassKindOf(RubyClass.class);
         
         classClass.undefineMethod("module_function");
         classClass.undefineMethod("append_features");
         classClass.undefineMethod("extend_object");
         
         classClass.defineAnnotatedMethods(RubyClass.class);
     }
     
     public static final ObjectAllocator CLASS_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyClass clazz = new RubyClass(runtime);
             clazz.allocator = ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR; // Class.allocate object is not allocatable before it is initialized
             return clazz;
         }
     };
 
     public ObjectAllocator getAllocator() {
         return allocator;
     }
 
     public void setAllocator(ObjectAllocator allocator) {
         this.allocator = allocator;
     }
 
     /**
      * Set a reflective allocator that calls a no-arg constructor on the given
      * class.
      *
      * @param cls The class on which to call the default constructor to allocate
      */
     public void setClassAllocator(final Class cls) {
         this.allocator = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                 try {
                     RubyBasicObject object = (RubyBasicObject)cls.newInstance();
                     object.setMetaClass(klazz);
                     return object;
                 } catch (InstantiationException ie) {
                     throw runtime.newTypeError("could not allocate " + cls + " with default constructor:\n" + ie);
                 } catch (IllegalAccessException iae) {
                     throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible default constructor:\n" + iae);
                 }
             }
         };
         
         this.reifiedClass = cls;
     }
 
     /**
      * Set a reflective allocator that calls the "standard" Ruby object
      * constructor (Ruby, RubyClass) on the given class.
      *
      * @param cls The class from which to grab a standard Ruby constructor
      */
     public void setRubyClassAllocator(final Class cls) {
         try {
             final Constructor constructor = cls.getConstructor(Ruby.class, RubyClass.class);
             
             this.allocator = new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                     try {
                         return (IRubyObject)constructor.newInstance(runtime, klazz);
                     } catch (InvocationTargetException ite) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ite);
                     } catch (InstantiationException ie) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ie);
                     } catch (IllegalAccessException iae) {
                         throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible (Ruby, RubyClass) constructor:\n" + iae);
                     }
                 }
             };
 
             this.reifiedClass = cls;
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 
     /**
      * Set a reflective allocator that calls the "standard" Ruby object
      * constructor (Ruby, RubyClass) on the given class via a static
      * __allocate__ method intermediate.
      *
      * @param cls The class from which to grab a standard Ruby __allocate__
      *            method.
      */
     public void setRubyStaticAllocator(final Class cls) {
         try {
             final Method method = cls.getDeclaredMethod("__allocate__", Ruby.class, RubyClass.class);
 
             this.allocator = new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                     try {
                         return (IRubyObject)method.invoke(null, runtime, klazz);
                     } catch (InvocationTargetException ite) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ite);
                     } catch (IllegalAccessException iae) {
                         throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible (Ruby, RubyClass) constructor:\n" + iae);
                     }
                 }
             };
 
             this.reifiedClass = cls;
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 
     @JRubyMethod(name = "allocate")
     public IRubyObject allocate() {
         if (superClass == null) {
             if(!(runtime.is1_9() && this == runtime.getBasicObject())) {
                 throw runtime.newTypeError("can't instantiate uninitialized class");
             }
         }
         IRubyObject obj = allocator.allocate(runtime, this);
         if (obj.getMetaClass().getRealClass() != getRealClass()) {
             throw runtime.newTypeError("wrong instance allocation");
         }
         return obj;
     }
 
     public CallSite getBaseCallSite(int idx) {
         return baseCallSites[idx];
     }
 
     public CallSite[] getBaseCallSites() {
         return baseCallSites;
     }
     
     public CallSite[] getExtraCallSites() {
         return extraCallSites;
     }
     
     public VariableTableManager getVariableTableManager() {
         return variableTableManager;
     }
     
     public boolean hasObjectID() {
         return variableTableManager.hasObjectID();
     }
 
     public Map<String, VariableAccessor> getVariableAccessorsForRead() {
         return variableTableManager.getVariableAccessorsForRead();
     }
 
     public VariableAccessor getVariableAccessorForWrite(String name) {
         return variableTableManager.getVariableAccessorForWrite(name);
     }
 
     public VariableAccessor getVariableAccessorForRead(String name) {
         VariableAccessor accessor = getVariableAccessorsForRead().get(name);
         if (accessor == null) accessor = VariableAccessor.DUMMY_ACCESSOR;
         return accessor;
     }
 
     public VariableAccessorField getObjectIdAccessorField() {
         return variableTableManager.getObjectIdAccessorField();
     }
 
     public VariableAccessorField getNativeHandleAccessorField() {
         return variableTableManager.getNativeHandleAccessorField();
     }
 
     public VariableAccessor getNativeHandleAccessorForWrite() {
         return variableTableManager.getNativeHandleAccessorForWrite();
     }
 
     public VariableAccessorField getFFIHandleAccessorField() {
         return variableTableManager.getFFIHandleAccessorField();
     }
 
     public VariableAccessor getFFIHandleAccessorForRead() {
         return variableTableManager.getFFIHandleAccessorForRead();
     }
 
     public VariableAccessor getFFIHandleAccessorForWrite() {
         return variableTableManager.getFFIHandleAccessorForWrite();
     }
 
     public VariableAccessorField getObjectGroupAccessorField() {
         return variableTableManager.getObjectGroupAccessorField();
     }
 
     public VariableAccessor getObjectGroupAccessorForRead() {
         return variableTableManager.getObjectGroupAccessorForRead();
     }
 
     public VariableAccessor getObjectGroupAccessorForWrite() {
         return variableTableManager.getObjectGroupAccessorForWrite();
     }
 
     public int getVariableTableSize() {
         return variableTableManager.getVariableTableSize();
     }
 
     public int getVariableTableSizeWithExtras() {
         return variableTableManager.getVariableTableSizeWithExtras();
     }
 
     /**
      * Get an array of all the known instance variable names. The offset into
      * the array indicates the offset of the variable's value in the per-object
      * variable array.
      *
      * @return a copy of the array of known instance variable names
      */
     public String[] getVariableNames() {
         return variableTableManager.getVariableNames();
     }
 
     public Map<String, VariableAccessor> getVariableTableCopy() {
         return new HashMap<String, VariableAccessor>(getVariableAccessorsForRead());
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.CLASS;
     }
     
     @Override
     public boolean isModule() {
         return false;
     }
 
     @Override
     public boolean isClass() {
         return true;
     }
 
     @Override
     public boolean isSingleton() {
         return false;
     }
 
     /** boot_defclass
      * Create an initial Object meta class before Module and Kernel dependencies have
      * squirreled themselves together.
      * 
      * @param runtime we need it
      * @return a half-baked meta class for object
      */
     public static RubyClass createBootstrapClass(Ruby runtime, String name, RubyClass superClass, ObjectAllocator allocator) {
         RubyClass obj;
 
         if (superClass == null ) {  // boot the Object class 
             obj = new RubyClass(runtime);
             obj.marshal = DEFAULT_OBJECT_MARSHAL;
         } else {                    // boot the Module and Class classes
             obj = new RubyClass(runtime, superClass);
         }
         obj.setAllocator(allocator);
         obj.setBaseName(name);
         return obj;
     }
 
     /** separate path for MetaClass and IncludedModuleWrapper construction
      *  (rb_class_boot version for MetaClasses)
      *  no marshal, allocator initialization and addSubclass(this) here!
      */
     protected RubyClass(Ruby runtime, RubyClass superClass, boolean objectSpace) {
         super(runtime, runtime.getClassClass(), objectSpace);
         this.runtime = runtime;
         
         // Since this path is for included wrappers and singletons, use parent
         // class's realClass and varTableMgr. If the latter is null, create a
         // dummy, since we won't be using it anyway (we're above BasicObject
         // so variable requests won't reach us).
         if (superClass == null) {
             this.realClass = null;
             this.variableTableManager = new VariableTableManager(this);
         } else {
             this.realClass = superClass.realClass;
             if (realClass != null) {
                 this.variableTableManager = realClass.variableTableManager;
             } else {
                 this.variableTableManager = new VariableTableManager(this);
             }
         }
         
         setSuperClass(superClass); // this is the only case it might be null here (in MetaClass construction)
     }
     
     /** used by CLASS_ALLOCATOR (any Class' class will be a Class!)
      *  also used to bootstrap Object class
      */
     protected RubyClass(Ruby runtime) {
         super(runtime, runtime.getClassClass());
         this.runtime = runtime;
         this.realClass = this;
         this.variableTableManager = new VariableTableManager(this);
         index = ClassIndex.CLASS;
     }
     
     /** rb_class_boot (for plain Classes)
      *  also used to bootstrap Module and Class classes 
      */
     protected RubyClass(Ruby runtime, RubyClass superClazz) {
         this(runtime);
         setSuperClass(superClazz);
         marshal = superClazz.marshal; // use parent's marshal
         superClazz.addSubclass(this);
         allocator = superClazz.allocator;
         
         infectBy(superClass);        
     }
     
     /** 
      * A constructor which allows passing in an array of supplementary call sites.
      */
     protected RubyClass(Ruby runtime, RubyClass superClazz, CallSite[] extraCallSites) {
         this(runtime);
         setSuperClass(superClazz);
         this.marshal = superClazz.marshal; // use parent's marshal
         superClazz.addSubclass(this);
         
         this.extraCallSites = extraCallSites;
         
         infectBy(superClass);        
     }
 
     /** 
      * Construct a new class with the given name scoped under Object (global)
      * and with Object as its immediate superclass.
      * Corresponds to rb_class_new in MRI.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass) {
         if (superClass == runtime.getClassClass()) throw runtime.newTypeError("can't make subclass of Class");
         if (superClass.isSingleton()) throw runtime.newTypeError("can't make subclass of virtual class");
         return new RubyClass(runtime, superClass);        
     }
 
     /** 
      * A variation on newClass that allow passing in an array of supplementary
      * call sites to improve dynamic invocation.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, CallSite[] extraCallSites) {
         if (superClass == runtime.getClassClass()) throw runtime.newTypeError("can't make subclass of Class");
         if (superClass.isSingleton()) throw runtime.newTypeError("can't make subclass of virtual class");
         return new RubyClass(runtime, superClass, extraCallSites);        
     }
 
     /** 
      * Construct a new class with the given name, allocator, parent class,
      * and containing class. If setParent is true, the class's parent will be
      * explicitly set to the provided parent (rather than the new class just
      * being assigned to a constant in that parent).
      * Corresponds to rb_class_new/rb_define_class_id/rb_name_class/rb_set_class_path
      * in MRI.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, String name, ObjectAllocator allocator, RubyModule parent, boolean setParent) {
         RubyClass clazz = newClass(runtime, superClass);
         clazz.setBaseName(name);
         clazz.setAllocator(allocator);
         clazz.makeMetaClass(superClass.getMetaClass());
         if (setParent) clazz.setParent(parent);
         parent.setConstant(name, clazz);
         clazz.inherit(superClass);
         return clazz;
     }
 
     /** 
      * A variation on newClass that allows passing in an array of supplementary
      * call sites to improve dynamic invocation performance.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, String name, ObjectAllocator allocator, RubyModule parent, boolean setParent, CallSite[] extraCallSites) {
         RubyClass clazz = newClass(runtime, superClass, extraCallSites);
         clazz.setBaseName(name);
         clazz.setAllocator(allocator);
         clazz.makeMetaClass(superClass.getMetaClass());
         if (setParent) clazz.setParent(parent);
         parent.setConstant(name, clazz);
         clazz.inherit(superClass);
         return clazz;
     }
 
     /** rb_make_metaclass
      *
      */
     @Override
     public RubyClass makeMetaClass(RubyClass superClass) {
         if (isSingleton()) { // could be pulled down to RubyClass in future
             MetaClass klass = new MetaClass(runtime, superClass, this); // rb_class_boot
             setMetaClass(klass);
 
             klass.setMetaClass(klass);
             klass.setSuperClass(getSuperClass().getRealClass().getMetaClass());
             
             return klass;
         } else {
             return super.makeMetaClass(superClass);
         }
     }
     
     @Deprecated
     public IRubyObject invoke(ThreadContext context, IRubyObject self, int methodIndex, String name, IRubyObject[] args, CallType callType, Block block) {
         return invoke(context, self, name, args, callType, block);
     }
     
     public boolean notVisibleAndNotMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return !method.isCallableFrom(caller, callType) && !name.equals("method_missing");
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, block);
         }
         return method.call(context, self, this, name, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, block);
         }
         return method.call(context, self, this, name, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, args, block);
         }
         return method.call(context, self, this, name, args, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, Block block) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, args, block);
         }
         return method.call(context, self, this, name, args, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg, block);
         }
         return method.call(context, self, this, name, arg, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg, block);
         }
         return method.call(context, self, this, name, arg, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, block);
         }
         return method.call(context, self, this, name, arg0, arg1, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, block);
         }
         return method.call(context, self, this, name, arg0, arg1, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, arg2, block);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, arg2, block);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name);
     }
 
     public IRubyObject finvokeChecked(ThreadContext context, IRubyObject self, String name) {
         DynamicMethod method = searchMethod(name);
         if(method.isUndefined()) {
             DynamicMethod methodMissing = searchMethod("method_missing");
             if(methodMissing.isUndefined() || methodMissing.equals(context.runtime.getDefaultMethodMissing())) {
                 return null;
             }
 
             try {
                 return Helpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, Block.NULL_BLOCK);
             } catch(RaiseException e) {
                 if(context.runtime.getNoMethodError().isInstance(e.getException())) {
                     if(self.respondsTo(name)) {
                         throw e;
                     } else {
                         // we swallow, so we also must clear $!
                         context.setErrorInfo(context.nil);
                         return null;
                     }
                 } else {
                     throw e;
                 }
             }
         }
         return method.call(context, self, this, name);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, CallType callType) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, args, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, args);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, args, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, args);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2);
     }
 
     private void dumpReifiedClass(String dumpDir, String javaPath, byte[] classBytes) {
         if (dumpDir != null) {
             if (dumpDir.equals("")) {
                 dumpDir = ".";
             }
             java.io.FileOutputStream classStream = null;
             try {
                 java.io.File classFile = new java.io.File(dumpDir, javaPath + ".class");
                 classFile.getParentFile().mkdirs();
                 classStream = new java.io.FileOutputStream(classFile);
                 classStream.write(classBytes);
             } catch (IOException io) {
                 getRuntime().getWarnings().warn("unable to dump class file: " + io.getMessage());
             } finally {
                 if (classStream != null) {
                     try {
                         classStream.close();
                     } catch (IOException ignored) {
                     }
                 }
             }
         }
     }
 
     private void generateMethodAnnotations(Map<Class, Map<String, Object>> methodAnnos, SkinnyMethodAdapter m, List<Map<Class, Map<String, Object>>> parameterAnnos) {
         if (methodAnnos != null && methodAnnos.size() != 0) {
             for (Map.Entry<Class, Map<String, Object>> entry : methodAnnos.entrySet()) {
                 m.visitAnnotationWithFields(ci(entry.getKey()), true, entry.getValue());
             }
         }
         if (parameterAnnos != null && parameterAnnos.size() != 0) {
             for (int i = 0; i < parameterAnnos.size(); i++) {
                 Map<Class, Map<String, Object>> annos = parameterAnnos.get(i);
                 if (annos != null && annos.size() != 0) {
                     for (Iterator<Map.Entry<Class, Map<String, Object>>> it = annos.entrySet().iterator(); it.hasNext();) {
                         Map.Entry<Class, Map<String, Object>> entry = it.next();
                         m.visitParameterAnnotationWithFields(i, ci(entry.getKey()), true, entry.getValue());
                     }
                 }
             }
         }
     }
     
     private boolean shouldCallMethodMissing(DynamicMethod method) {
         return method.isUndefined();
     }
     private boolean shouldCallMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return method.isUndefined() || notVisibleAndNotMethodMissing(method, name, caller, callType);
     }
     
     public IRubyObject invokeInherited(ThreadContext context, IRubyObject self, IRubyObject subclass) {
         DynamicMethod method = getMetaClass().searchMethod("inherited");
 
         if (method.isUndefined()) {
             return Helpers.callMethodMissing(context, self, method.getVisibility(), "inherited", CallType.FUNCTIONAL, Block.NULL_BLOCK);
         }
 
         return method.call(context, self, getMetaClass(), "inherited", subclass, Block.NULL_BLOCK);
     }
 
     /** rb_class_new_instance
     *
     */
     @JRubyMethod(name = "new", omit = true)
     public IRubyObject newInstance(ThreadContext context, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, obj, obj, block);
         return obj;
     }
 
     @JRubyMethod(name = "new", omit = true)
     public IRubyObject newInstance(ThreadContext context, IRubyObject arg0, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, obj, obj, arg0, block);
         return obj;
     }
 
     @JRubyMethod(name = "new", omit = true)
     public IRubyObject newInstance(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, obj, obj, arg0, arg1, block);
         return obj;
     }
 
     @JRubyMethod(name = "new", omit = true)
     public IRubyObject newInstance(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, obj, obj, arg0, arg1, arg2, block);
         return obj;
     }
 
     @JRubyMethod(name = "new", rest = true, omit = true)
     public IRubyObject newInstance(ThreadContext context, IRubyObject[] args, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, obj, obj, args, block);
         return obj;
     }
 
     /** rb_class_initialize
      * 
      */
     @JRubyMethod(compat = RUBY1_8, visibility = PRIVATE)
     @Override
     public IRubyObject initialize(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(context, runtime.getObject(), block, false);
     }
         
     @JRubyMethod(compat = RUBY1_8, visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon(context, (RubyClass)superObject, block, false);
     }
         
     @JRubyMethod(name = "initialize", compat = RUBY1_9, visibility = PRIVATE)
     public IRubyObject initialize19(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(context, runtime.getObject(), block, true);
     }
         
     @JRubyMethod(name = "initialize", compat = RUBY1_9, visibility = PRIVATE)
     public IRubyObject initialize19(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon(context, (RubyClass)superObject, block, true);
     }
 
     private IRubyObject initializeCommon(ThreadContext context, RubyClass superClazz, Block block, boolean ruby1_9 /*callInheritBeforeSuper*/) {
         setSuperClass(superClazz);
         allocator = superClazz.allocator;
         makeMetaClass(superClazz.getMetaClass());
 
         marshal = superClazz.marshal;
 
         superClazz.addSubclass(this);
 
         if (ruby1_9) {
             inherit(superClazz);
             super.initialize(context, block);
         } else {
             super.initialize(context, block);
             inherit(superClazz);
         }
 
         return this;
     }
 
     /** rb_class_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = PRIVATE)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         checkNotInitialized();
         if (original instanceof MetaClass) throw runtime.newTypeError("can't copy singleton class");        
         
         super.initialize_copy(original);
         allocator = ((RubyClass)original).allocator; 
         return this;        
     }
 
     protected void setModuleSuperClass(RubyClass superClass) {
         // remove us from old superclass's child classes
         if (this.superClass != null) this.superClass.removeSubclass(this);
         // add us to new superclass's child classes
         superClass.addSubclass(this);
         // update superclass reference
         setSuperClass(superClass);
     }
     
     public Collection<RubyClass> subclasses(boolean includeDescendants) {
         Set<RubyClass> mySubclasses = subclasses;
         if (mySubclasses != null) {
             Collection<RubyClass> mine = new ArrayList<RubyClass>(mySubclasses);
             if (includeDescendants) {
                 for (RubyClass i: mySubclasses) {
                     mine.addAll(i.subclasses(includeDescendants));
                 }
             }
 
             return mine;
         } else {
             return Collections.EMPTY_LIST;
         }
     }
 
     /**
      * Add a new subclass to the weak set of subclasses.
      *
      * This version always constructs a new set to avoid having to synchronize
      * against the set when iterating it for invalidation in
      * invalidateCacheDescendants.
      *
      * @param subclass The subclass to add
      */
     public synchronized void addSubclass(RubyClass subclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) subclasses = oldSubclasses = new WeakHashSet<RubyClass>(4);
             oldSubclasses.add(subclass);
         }
     }
     
     /**
      * Remove a subclass from the weak set of subclasses.
      *
      * @param subclass The subclass to remove
      */
     public synchronized void removeSubclass(RubyClass subclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) return;
 
             oldSubclasses.remove(subclass);
         }
     }
 
     /**
      * Replace an existing subclass with a new one.
      *
      * @param subclass The subclass to remove
      * @param newSubclass The subclass to replace it with
      */
     public synchronized void replaceSubclass(RubyClass subclass, RubyClass newSubclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) return;
 
             oldSubclasses.remove(subclass);
             oldSubclasses.add(newSubclass);
         }
     }
 
     public void becomeSynchronized() {
         // make this class and all subclasses sync
         synchronized (getRuntime().getHierarchyLock()) {
             super.becomeSynchronized();
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.becomeSynchronized();
             }
         }
     }
 
     /**
      * Invalidate all subclasses of this class by walking the set of all
      * subclasses and asking them to invalidate themselves.
      *
      * Note that this version works against a reference to the current set of
      * subclasses, which could be replaced by the time this iteration is
      * complete. In theory, there may be a path by which invalidation would
      * miss a class added during the invalidation process, but the exposure is
      * minimal if it exists at all. The only way to prevent it would be to
      * synchronize both invalidation and subclass set modification against a
      * global lock, which we would like to avoid.
      */
     @Override
     public void invalidateCacheDescendants() {
         super.invalidateCacheDescendants();
 
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.invalidateCacheDescendants();
             }
         }
     }
     
     public void addInvalidatorsAndFlush(List<Invalidator> invalidators) {
         // add this class's invalidators to the aggregate
         invalidators.add(methodInvalidator);
         
         // if we're not at boot time, don't bother fully clearing caches
         if (!runtime.isBooting()) cachedMethods.clear();
 
         // no subclasses, don't bother with lock and iteration
         if (subclasses == null || subclasses.isEmpty()) return;
         
         // cascade into subclasses
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.addInvalidatorsAndFlush(invalidators);
             }
         }
     }
     
     public Ruby getClassRuntime() {
         return runtime;
     }
 
     public final RubyClass getRealClass() {
         return realClass;
     }    
 
     @JRubyMethod(name = "inherited", required = 1, visibility = PRIVATE)
     public IRubyObject inherited(ThreadContext context, IRubyObject arg) {
         return runtime.getNil();
     }
 
     /** rb_class_inherited (reversed semantics!)
      * 
      */
     public void inherit(RubyClass superClazz) {
         if (superClazz == null) superClazz = runtime.getObject();
 
         if (getRuntime().getNil() != null) {
             superClazz.invokeInherited(runtime.getCurrentContext(), superClazz, this);
         }
     }
 
     /** Return the real super class of this class.
      * 
      * rb_class_superclass
      *
      */    
     @JRubyMethod(name = "superclass")
     public IRubyObject superclass(ThreadContext context) {
         RubyClass superClazz = superClass;
         
         if (superClazz == null) {
             if (runtime.is1_9() && metaClass == runtime.getBasicObject().getMetaClass()) return runtime.getNil();
             throw runtime.newTypeError("uninitialized class");
         }
 
         while (superClazz != null && superClazz.isIncluded()) superClazz = superClazz.superClass;
 
         return superClazz != null ? superClazz : runtime.getNil();
     }
 
     private void checkNotInitialized() {
         if (superClass != null || (runtime.is1_9() && this == runtime.getBasicObject())) {
             throw runtime.newTypeError("already initialized class");
         }
     }
     /** rb_check_inheritable
      * 
      */
     public static void checkInheritable(IRubyObject superClass) {
         if (!(superClass instanceof RubyClass)) {
             throw superClass.getRuntime().newTypeError("superclass must be a Class (" + superClass.getMetaClass() + " given)"); 
         }
         if (((RubyClass)superClass).isSingleton()) {
             throw superClass.getRuntime().newTypeError("can't make subclass of virtual class");
         }        
     }
 
     public final ObjectMarshal getMarshal() {
         return marshal;
     }
     
     public final void setMarshal(ObjectMarshal marshal) {
         this.marshal = marshal;
     }
     
     public final void marshal(Object obj, MarshalStream marshalStream) throws IOException {
         getMarshal().marshalTo(runtime, obj, this, marshalStream);
     }
     
     public final Object unmarshal(UnmarshalStream unmarshalStream) throws IOException {
         return getMarshal().unmarshalFrom(runtime, this, unmarshalStream);
     }
     
     public static void marshalTo(RubyClass clazz, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(clazz);
         output.writeString(MarshalStream.getPathFromClass(clazz));
     }
 
     public static RubyClass unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = RubyString.byteListToString(input.unmarshalString());
         RubyClass result = UnmarshalStream.getClassFromPath(input.getRuntime(), name);
         input.registerLinkTarget(result);
         return result;
     }
 
     protected static final ObjectMarshal DEFAULT_OBJECT_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
diff --git a/src/org/jruby/RubyComplex.java b/src/org/jruby/RubyComplex.java
index 98292cb7f8..9c896d44d2 100644
--- a/src/org/jruby/RubyComplex.java
+++ b/src/org/jruby/RubyComplex.java
@@ -1,1049 +1,1044 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import static org.jruby.util.Numeric.f_abs;
 import static org.jruby.util.Numeric.f_abs2;
 import static org.jruby.util.Numeric.f_add;
 import static org.jruby.util.Numeric.f_arg;
 import static org.jruby.util.Numeric.f_conjugate;
 import static org.jruby.util.Numeric.f_denominator;
 import static org.jruby.util.Numeric.f_div;
 import static org.jruby.util.Numeric.f_divmod;
 import static org.jruby.util.Numeric.f_equal;
 import static org.jruby.util.Numeric.f_exact_p;
 import static org.jruby.util.Numeric.f_expt;
 import static org.jruby.util.Numeric.f_gt_p;
 import static org.jruby.util.Numeric.f_inspect;
 import static org.jruby.util.Numeric.f_lcm;
 import static org.jruby.util.Numeric.f_mul;
 import static org.jruby.util.Numeric.f_negate;
 import static org.jruby.util.Numeric.f_negative_p;
 import static org.jruby.util.Numeric.f_numerator;
 import static org.jruby.util.Numeric.f_one_p;
 import static org.jruby.util.Numeric.f_polar;
 import static org.jruby.util.Numeric.f_quo;
 import static org.jruby.util.Numeric.f_real_p;
 import static org.jruby.util.Numeric.f_sub;
 import static org.jruby.util.Numeric.f_to_f;
 import static org.jruby.util.Numeric.f_to_i;
 import static org.jruby.util.Numeric.f_to_r;
 import static org.jruby.util.Numeric.f_to_s;
 import static org.jruby.util.Numeric.f_xor;
 import static org.jruby.util.Numeric.f_zero_p;
 import static org.jruby.util.Numeric.k_exact_p;
 import static org.jruby.util.Numeric.k_inexact_p;
 
 import org.jcodings.specific.ASCIIEncoding;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.Numeric;
 
 import static org.jruby.runtime.Helpers.invokedynamic;
 import static org.jruby.runtime.invokedynamic.MethodNames.HASH;
 
 /**
  *  1.9 complex.c as of revision: 20011
  */
 
 @JRubyClass(name = "Complex", parent = "Numeric")
 public class RubyComplex extends RubyNumeric {
 
     public static RubyClass createComplexClass(Ruby runtime) {
         RubyClass complexc = runtime.defineClass("Complex", runtime.getNumeric(), COMPLEX_ALLOCATOR);
         runtime.setComplex(complexc);
 
         complexc.index = ClassIndex.COMPLEX;
         complexc.setReifiedClass(RubyComplex.class);
         
-        complexc.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyComplex;
-            }
-        };
+        complexc.kindOf = new RubyModule.JavaClassKindOf(RubyComplex.class);
 
         complexc.defineAnnotatedMethods(RubyComplex.class);
 
         complexc.getSingletonClass().undefineMethod("allocate");
         complexc.getSingletonClass().undefineMethod("new");
 
         String[]undefined = {"<", "<=", "<=>", ">", ">=", "between?", "divmod",
                              "floor", "ceil", "modulo", "round", "step", "truncate"};
 
         for (String undef : undefined) {
             complexc.undefineMethod(undef);
         }
 
         complexc.defineConstant("I", RubyComplex.newComplexConvert(runtime.getCurrentContext(), RubyFixnum.zero(runtime), RubyFixnum.one(runtime)));
 
         return complexc;
     }
 
     private static ObjectAllocator COMPLEX_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyFixnum zero = RubyFixnum.zero(runtime);
             return new RubyComplex(runtime, klass, zero, zero);
         }
     };
 
     /** internal
      * 
      */
     private RubyComplex(Ruby runtime, IRubyObject clazz, IRubyObject real, IRubyObject image) {
         super(runtime, (RubyClass)clazz);
         this.real = real;
         this.image = image;
     }
 
     /** rb_complex_raw
      * 
      */
     static RubyComplex newComplexRaw(Ruby runtime, IRubyObject x, RubyObject y) {
         return new RubyComplex(runtime, runtime.getComplex(), x, y);
     }
 
     /** rb_complex_raw1
      * 
      */
     static RubyComplex newComplexRaw(Ruby runtime, IRubyObject x) {
         return new RubyComplex(runtime, runtime.getComplex(), x, RubyFixnum.zero(runtime));
     }
 
     /** rb_complex_new1
      * 
      */
     public static IRubyObject newComplexCanonicalize(ThreadContext context, IRubyObject x) {
         return newComplexCanonicalize(context, x, RubyFixnum.zero(context.runtime));
     }
     
     /** rb_complex_new
      * 
      */
     public static IRubyObject newComplexCanonicalize(ThreadContext context, IRubyObject x, IRubyObject y) {
         return canonicalizeInternal(context, context.runtime.getComplex(), x, y);
     }
 
     /** rb_complex_polar
      * 
      */
     static IRubyObject newComplexPolar(ThreadContext context, IRubyObject x, IRubyObject y) {
         return polar(context, context.runtime.getComplex(), x, y);
     }
 
     /** f_complex_new1
      * 
      */
     static IRubyObject newComplex(ThreadContext context, IRubyObject clazz, IRubyObject x) {
         return newComplex(context, clazz, x, RubyFixnum.zero(context.runtime));
     }
 
     /** f_complex_new2
      * 
      */
     static IRubyObject newComplex(ThreadContext context, IRubyObject clazz, IRubyObject x, IRubyObject y) {
         assert !(x instanceof RubyComplex);
         return canonicalizeInternal(context, clazz, x, y);
     }
     
     /** f_complex_new_bang2
      * 
      */
     static RubyComplex newComplexBang(ThreadContext context, IRubyObject clazz, IRubyObject x, IRubyObject y) {
 // FIXME: what should these really be? Numeric?       assert x instanceof RubyComplex && y instanceof RubyComplex;
         return new RubyComplex(context.runtime, clazz, x, y);
     }
 
     /** f_complex_new_bang1
      * 
      */
     public static RubyComplex newComplexBang(ThreadContext context, IRubyObject clazz, IRubyObject x) {
 // FIXME: what should this really be?       assert x instanceof RubyComplex;
         return newComplexBang(context, clazz, x, RubyFixnum.zero(context.runtime));
     }
 
     private IRubyObject real;
     private IRubyObject image;
     
     IRubyObject getImage() {
         return image;
     }
 
     IRubyObject getReal() {
         return real;
     }
 
     /** m_cos
      * 
      */
     private static IRubyObject m_cos(ThreadContext context, IRubyObject x) {
         if (f_real_p(context, x).isTrue()) return RubyMath.cos(x, x);
         RubyComplex complex = (RubyComplex)x;
         return newComplex(context, context.runtime.getComplex(),
                           f_mul(context, RubyMath.cos(x, complex.real), RubyMath.cosh(x, complex.image)),
                           f_mul(context, f_negate(context, RubyMath.sin(x, complex.real)), RubyMath.sinh(x, complex.image)));
     }
 
     /** m_sin
      * 
      */
     private static IRubyObject m_sin(ThreadContext context, IRubyObject x) {
         if (f_real_p(context, x).isTrue()) return RubyMath.sin(x, x);
         RubyComplex complex = (RubyComplex)x;
         return newComplex(context, context.runtime.getComplex(),
                           f_mul(context, RubyMath.sin(x, complex.real), RubyMath.cosh(x, complex.image)),
                           f_mul(context, RubyMath.cos(x, complex.real), RubyMath.sinh(x, complex.image)));
     }    
     
     /** m_sqrt
      * 
      */
     private static IRubyObject m_sqrt(ThreadContext context, IRubyObject x) {
         if (f_real_p(context, x).isTrue()) {
             if (!f_negative_p(context, x)) return RubyMath.sqrt(x, x);
             return newComplex(context, context.runtime.getComplex(),
                               RubyFixnum.zero(context.runtime),
                               RubyMath.sqrt(x, f_negate(context, x)));
         } else {
             RubyComplex complex = (RubyComplex)x;
             if (f_negative_p(context, complex.image)) {
                 return f_conjugate(context, m_sqrt(context, f_conjugate(context, x)));
             } else {
                 IRubyObject a = f_abs(context, x);
                 IRubyObject two = RubyFixnum.two(context.runtime);
                 return newComplex(context, context.runtime.getComplex(),
                                   RubyMath.sqrt(x, f_div(context, f_add(context, a, complex.real), two)),
                                   RubyMath.sqrt(x, f_div(context, f_sub(context, a, complex.real), two)));
             }
         }
     }
 
     /** nucomp_s_new_bang
      *
      */
     @Deprecated
     public static IRubyObject newInstanceBang(ThreadContext context, IRubyObject recv, IRubyObject[]args) {
         switch (args.length) {
         case 1: return newInstanceBang(context, recv, args[0]);
         case 2: return newInstanceBang(context, recv, args[0], args[1]);
         }
         Arity.raiseArgumentError(context.runtime, args.length, 1, 1);
         return null;
     }
 
     @JRubyMethod(name = "new!", meta = true, visibility = Visibility.PRIVATE)
     public static IRubyObject newInstanceBang(ThreadContext context, IRubyObject recv, IRubyObject real) {
         if (!(real instanceof RubyNumeric)) real = f_to_i(context, real);
         return new RubyComplex(context.runtime, recv, real, RubyFixnum.zero(context.runtime));
     }
 
     @JRubyMethod(name = "new!", meta = true, visibility = Visibility.PRIVATE)
     public static IRubyObject newInstanceBang(ThreadContext context, IRubyObject recv, IRubyObject real, IRubyObject image) {
         if (!(real instanceof RubyNumeric)) real = f_to_i(context, real);
         if (!(image instanceof RubyNumeric)) image = f_to_i(context, image);
         return new RubyComplex(context.runtime, recv, real, image);
     }
 
     /** nucomp_canonicalization
      * 
      */
     private static boolean canonicalization = false;
     public static void setCanonicalization(boolean canonical) {
         canonicalization = canonical;
     }
 
     /** nucomp_real_check (might go to bimorphic)
      * 
      */
     private static void realCheck(ThreadContext context, IRubyObject num) {
         switch (num.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
         case ClassIndex.RATIONAL:
             break;
         default:
              if (!(num instanceof RubyNumeric ) || !f_real_p(context, num).isTrue()) {
                  throw context.runtime.newTypeError("not a real");
              }
         }
     }
 
     /** nucomp_s_canonicalize_internal
      * 
      */
     private static final boolean CL_CANON = Numeric.CANON;
     private static IRubyObject canonicalizeInternal(ThreadContext context, IRubyObject clazz, IRubyObject real, IRubyObject image) {
         if (Numeric.CANON) {
             if (f_zero_p(context, image) &&
                     (!CL_CANON || k_exact_p(image)) &&
                     canonicalization)
                     return real;
         }
         if (f_real_p(context, real).isTrue() &&
                    f_real_p(context, image).isTrue()) {
             return new RubyComplex(context.runtime, clazz, real, image);
         } else if (f_real_p(context, real).isTrue()) {
             RubyComplex complex = (RubyComplex)image;
             return new RubyComplex(context.runtime, clazz,
                                    f_sub(context, real, complex.image),
                                    f_add(context, RubyFixnum.zero(context.runtime), complex.real));
         } else if (f_real_p(context, image).isTrue()) {
             RubyComplex complex = (RubyComplex)real;
             return new RubyComplex(context.runtime, clazz,
                                    complex.real,
                                    f_add(context, complex.image, image));
         } else {
             RubyComplex complex1 = (RubyComplex)real;
             RubyComplex complex2 = (RubyComplex)image;
             return new RubyComplex(context.runtime, clazz,
                                    f_sub(context, complex1.real, complex2.image),
                                    f_add(context, complex1.image, complex2.real));
         }
     }
     
     /** nucomp_s_new
      * 
      */
     @Deprecated
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[]args) {
         switch (args.length) {
         case 1: return newInstance(context, recv, args[0]);
         case 2: return newInstance(context, recv, args[0], args[1]);
         }
         Arity.raiseArgumentError(context.runtime, args.length, 1, 1);
         return null;
     }
 
     // @JRubyMethod(name = "new", meta = true, visibility = Visibility.PRIVATE)
     public static IRubyObject newInstanceNew(ThreadContext context, IRubyObject recv, IRubyObject real) {
         return newInstance(context, recv, real);
     }
 
     @JRubyMethod(name = {"rect", "rectangular"}, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject real) {
         realCheck(context, real);
         return canonicalizeInternal(context, recv, real, RubyFixnum.zero(context.runtime));
     }
 
     // @JRubyMethod(name = "new", meta = true, visibility = Visibility.PRIVATE)
     public static IRubyObject newInstanceNew(ThreadContext context, IRubyObject recv, IRubyObject real, IRubyObject image) {
         return newInstance(context, recv, real, image);
     }
 
     @JRubyMethod(name = {"rect", "rectangular"}, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject real, IRubyObject image) {
         realCheck(context, real);
         realCheck(context, image);
         return canonicalizeInternal(context, recv, real, image);
     }
 
     /** f_complex_polar
      * 
      */
     private static IRubyObject f_complex_polar(ThreadContext context, IRubyObject clazz, IRubyObject x, IRubyObject y) {
         assert !(x instanceof RubyComplex) && !(y instanceof RubyComplex);
         return canonicalizeInternal(context, clazz,
                                              f_mul(context, x, m_cos(context, y)),
                                              f_mul(context, x, m_sin(context, y)));
     }
 
     /** nucomp_s_polar
      * 
      */
     @JRubyMethod(name = "polar", meta = true)
     public static IRubyObject polar(ThreadContext context, IRubyObject clazz, IRubyObject abs, IRubyObject arg) {
         return f_complex_polar(context, clazz, abs, arg);
     }
 
     /** nucomp_s_polar
      *
      */
     @JRubyMethod(name = "polar", meta = true, required = 1, optional = 1, compat = CompatVersion.RUBY1_9)
     public static IRubyObject polar19(ThreadContext context, IRubyObject clazz, IRubyObject[] args) {
         IRubyObject abs = args[0];
         IRubyObject arg;
         if (args.length < 2) {
             arg = RubyFixnum.zero(context.runtime);
         } else {
             arg = args[1];
         }
         realCheck(context, abs);
         realCheck(context, arg);
         return f_complex_polar(context, clazz, abs, arg);
     }
 
     /** rb_Complex1
      * 
      */
     public static IRubyObject newComplexConvert(ThreadContext context, IRubyObject x) {
         return newComplexConvert(context, x, RubyFixnum.zero(context.runtime));
     }
 
     /** rb_Complex/rb_Complex2
      * 
      */
     public static IRubyObject newComplexConvert(ThreadContext context, IRubyObject x, IRubyObject y) {
         return convert(context, context.runtime.getComplex(), x, y);
     }
 
     @Deprecated
     public static IRubyObject convert(ThreadContext context, IRubyObject clazz, IRubyObject[]args) {
         switch (args.length) {
         case 1: return convert(context, clazz, args[0]);        
         case 2: return convert(context, clazz, args[0], args[1]);
         }
         Arity.raiseArgumentError(context.runtime, args.length, 1, 1);
         return null;
     }
 
     /** nucomp_s_convert
      * 
      */
     @JRubyMethod(name = "convert", meta = true, visibility = Visibility.PRIVATE)
     public static IRubyObject convert(ThreadContext context, IRubyObject recv, IRubyObject a1) {
         return convertCommon(context, recv, a1, context.runtime.getNil());
     }
 
     /** nucomp_s_convert
      * 
      */
     @JRubyMethod(name = "convert", meta = true, visibility = Visibility.PRIVATE)
     public static IRubyObject convert(ThreadContext context, IRubyObject recv, IRubyObject a1, IRubyObject a2) {
         return convertCommon(context, recv, a1, a2);
     }
     
     private static IRubyObject convertCommon(ThreadContext context, IRubyObject recv, IRubyObject a1, IRubyObject a2) {
         if (a1 instanceof RubyString) a1 = str_to_c_strict(context, a1);
         if (a2 instanceof RubyString) a2 = str_to_c_strict(context, a2);
 
         if (a1 instanceof RubyComplex) {
             RubyComplex a1Complex = (RubyComplex)a1;
             if (k_exact_p(a1Complex.image) && f_zero_p(context, a1Complex.image)) {
                 a1 = a1Complex.real;
             }
         }
 
         if (a2 instanceof RubyComplex) {
             RubyComplex a2Complex = (RubyComplex)a2;
             if (k_exact_p(a2Complex.image) && f_zero_p(context, a2Complex.image)) {
                 a2 = a2Complex.real;
             }
         }
 
         if (a1 instanceof RubyComplex) {
             if (a2.isNil() || (k_exact_p(a2) && f_zero_p(context, a2))) return a1;
         }
 
         if (a2.isNil()) {
             if (a1 instanceof RubyNumeric && !f_real_p(context, a1).isTrue()) return a1;
             return newInstance(context, recv, a1);
         } else {
             if (a1 instanceof RubyNumeric && a2 instanceof RubyNumeric &&
                 (!f_real_p(context, a1).isTrue() || !f_real_p(context, a2).isTrue())) {
                 Ruby runtime = context.runtime;
                 return f_add(context, a1,
                              f_mul(context, a2, newComplexBang(context, runtime.getComplex(),
                                      RubyFixnum.zero(runtime), RubyFixnum.one(runtime))));
             }
             return newInstance(context, recv, a1, a2);
         }
     }
 
     /** nucomp_real
      * 
      */
     @JRubyMethod(name = "real")
     public IRubyObject real() {
         return real;
     }
     
     /** nucomp_image
      * 
      */
     @JRubyMethod(name = {"imaginary", "imag"})
     public IRubyObject image() {
         return image;
     }
 
     /** nucomp_negate
      * 
      */
     @JRubyMethod(name = "-@")
     public IRubyObject negate(ThreadContext context) {
         return newComplex(context, getMetaClass(), f_negate(context, real), f_negate(context, image));
     }
 
     /** nucomp_add
      * 
      */
     @JRubyMethod(name = "+")
     public IRubyObject op_add(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyComplex) {
             RubyComplex otherComplex = (RubyComplex)other;
             return newComplex(context, getMetaClass(), 
                               f_add(context, real, otherComplex.real),
                               f_add(context, image, otherComplex.image));
         } else if (other instanceof RubyNumeric && f_real_p(context, other).isTrue()) {
             return newComplex(context, getMetaClass(), f_add(context, real, other), image);
         }
         return coerceBin(context, "+", other);
     }
 
     /** nucomp_sub
      * 
      */
     @JRubyMethod(name = "-")
     public IRubyObject op_sub(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyComplex) {
             RubyComplex otherComplex = (RubyComplex)other;
             return newComplex(context, getMetaClass(), 
                               f_sub(context, real, otherComplex.real),
                               f_sub(context, image, otherComplex.image));
         } else if (other instanceof RubyNumeric && f_real_p(context, other).isTrue()) {
             return newComplex(context, getMetaClass(), f_sub(context, real, other), image);
         }
         return coerceBin(context, "-", other);
     }
 
     /** nucomp_mul
      * 
      */
     @JRubyMethod(name = "*")
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyComplex) {
             RubyComplex otherComplex = (RubyComplex)other;
             IRubyObject realp = f_sub(context, 
                                 f_mul(context, real, otherComplex.real),
                                 f_mul(context, image, otherComplex.image));
             IRubyObject imagep = f_add(context,
                                 f_mul(context, real, otherComplex.image),
                                 f_mul(context, image, otherComplex.real));
             
             return newComplex(context, getMetaClass(), realp, imagep); 
         } else if (other instanceof RubyNumeric && f_real_p(context, other).isTrue()) {
             return newComplex(context, getMetaClass(),
                     f_mul(context, real, other),
                     f_mul(context, image, other));
         }
         return coerceBin(context, "*", other);
     }
     
     /** nucomp_div / nucomp_quo
      * 
      */
     @JRubyMethod(name = {"/", "quo"})
     public IRubyObject op_div(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyComplex) {
             RubyComplex otherComplex = (RubyComplex)other;
             if (real instanceof RubyFloat || image instanceof RubyFloat ||
                 otherComplex.real instanceof RubyFloat || otherComplex.image instanceof RubyFloat) {
                 IRubyObject magn = RubyMath.hypot(this, otherComplex.real, otherComplex.image);
                 IRubyObject tmp = newComplexBang(context, getMetaClass(),
                                                  f_quo(context, otherComplex.real, magn),
                                                  f_quo(context, otherComplex.image, magn));
                 return f_quo(context, f_mul(context, this, f_conjugate(context, tmp)), magn);
             }
             return f_quo(context, f_mul(context, this, f_conjugate(context, other)), f_abs2(context, other));
         } else if (other instanceof RubyNumeric) {
             if (f_real_p(context, other).isTrue()) {
                 return newComplex(context, getMetaClass(),
                         f_quo(context, real, other),
                         f_quo(context, image, other));
             } else {
                 RubyArray coercedOther = doCoerce(context, other, true);
                 return RubyRational.newInstance(context, context.runtime.getRational(), coercedOther.first(), coercedOther.last());
             }
         }
         return coerceBin(context, "/", other);
     }
 
     /** nucomp_fdiv
      *
      */
     @JRubyMethod(name = "fdiv")
     @Override
     public IRubyObject fdiv(ThreadContext context, IRubyObject other) {
         IRubyObject complex = newComplex(context, getMetaClass(),
                                          f_to_f(context, real),   
                                          f_to_f(context, image));
 
         return f_div(context, complex, other);
     }
 
     /** nucomp_expt
      * 
      */
     @JRubyMethod(name = "**")
     public IRubyObject op_expt(ThreadContext context, IRubyObject other) {
         if (k_exact_p(other) && f_zero_p(context, other)) {
             return newComplexBang(context, getMetaClass(), RubyFixnum.one(context.runtime));
         } else if (other instanceof RubyRational && f_one_p(context, f_denominator(context, other))) {
             other = f_numerator(context, other); 
         } 
 
         if (other instanceof RubyComplex) {
             RubyArray a = f_polar(context, this).convertToArray();
             IRubyObject r = a.eltInternal(0);
             IRubyObject theta = a.eltInternal(1);
             RubyComplex otherComplex = (RubyComplex)other;
             IRubyObject nr = RubyMath.exp(this, f_sub(context, 
                                                       f_mul(context, otherComplex.real, RubyMath.log(this, r)),
                                                       f_mul(context, otherComplex.image, theta)));
             IRubyObject ntheta = f_add(context,
                                         f_mul(context, theta, otherComplex.real),
                                         f_mul(context, otherComplex.image, RubyMath.log(this, r)));
             return polar(context, getMetaClass(), nr, ntheta);
         } else if (other instanceof RubyInteger) {
             IRubyObject one = RubyFixnum.one(context.runtime);
             if (f_gt_p(context, other, RubyFixnum.zero(context.runtime)).isTrue()) {
                 IRubyObject x = this;
                 IRubyObject z = x;
                 IRubyObject n = f_sub(context, other, one);
 
                 IRubyObject two = RubyFixnum.two(context.runtime);
                 
                 while (!f_zero_p(context, n)) {
                     
                     RubyArray a = f_divmod(context, n, two).convertToArray();
 
                     while (f_zero_p(context, a.eltInternal(1))) {
                         RubyComplex xComplex = (RubyComplex)x;
                         x = newComplex(context, getMetaClass(),
                                        f_sub(context, f_mul(context, xComplex.real, xComplex.real),
                                                       f_mul(context, xComplex.image, xComplex.image)),
                                        f_mul(context, f_mul(context, two, xComplex.real), xComplex.image));
                         
                         n = a.eltInternal(0);
                         a = f_divmod(context, n, two).convertToArray();
                     }
                     z = f_mul(context, z, x);
                     n = f_sub(context, n, one);
                 }
                 return z;
             }
             return f_expt(context, f_div(context, f_to_r(context, one), this), f_negate(context, other));
         } else if (other instanceof RubyNumeric && f_real_p(context, other).isTrue()) {
             RubyArray a = f_polar(context, this).convertToArray();
             IRubyObject r = a.eltInternal(0);
             IRubyObject theta = a.eltInternal(1);
             return f_complex_polar(context, getMetaClass(), f_expt(context, r, other), f_mul(context, theta, other));
         }
         return coerceBin(context, "**", other);
     }
 
     /** nucomp_equal_p
      * 
      */
     @JRubyMethod(name = "==")
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyComplex) {
             RubyComplex otherComplex = (RubyComplex) other;
             boolean test = f_equal(context, real, otherComplex.real).isTrue() &&
                     f_equal(context, image, otherComplex.image).isTrue();
 
             return context.runtime.newBoolean(test);
         }
 
         if (other instanceof RubyNumeric && f_real_p(context, other).isTrue()) {
             boolean test = f_equal(context, real, other).isTrue() && f_zero_p(context, image);
 
             return context.runtime.newBoolean(test);
         }
         
         return f_equal(context, other, this);
     }
 
     /** nucomp_coerce 
      * 
      */
     @JRubyMethod(name = "coerce")
     public IRubyObject coerce(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyNumeric && f_real_p(context, other).isTrue()) {
             return context.runtime.newArray(newComplexBang(context, getMetaClass(), other), this);
         }
         if (other instanceof RubyComplex) {
             return context.runtime.newArray(other, this);
         }
         throw context.runtime.newTypeError(other.getMetaClass().getName() + " can't be coerced into " + getMetaClass().getName());
     }
 
     /** nucomp_abs 
      * 
      */
     @JRubyMethod(name = {"abs", "magnitude"})
     @Override
     public IRubyObject abs(ThreadContext context) {
         return RubyMath.hypot(this, real, image);
     }
 
     /** nucomp_abs2 
      * 
      */
     @JRubyMethod(name = "abs2")
     @Override
     public IRubyObject abs2(ThreadContext context) {
         return f_add(context,
                      f_mul(context, real, real),
                      f_mul(context, image, image));
     }
 
     /** nucomp_arg 
      * 
      */
     @JRubyMethod(name = {"arg", "angle", "phase"})
     @Override
     public IRubyObject arg(ThreadContext context) {
         return RubyMath.atan2(this, image, real);
     }
 
     /** nucomp_rect
      * 
      */
     @JRubyMethod(name = {"rectangular", "rect"})
     @Override
     public IRubyObject rect(ThreadContext context) {
         return context.runtime.newArray(real, image);
     }
 
     /** nucomp_polar 
      * 
      */
     @JRubyMethod(name = "polar")
     @Override
     public IRubyObject polar(ThreadContext context) {
         return context.runtime.newArray(f_abs(context, this), f_arg(context, this));
     }
 
     /** nucomp_conjugate
      * 
      */
     @JRubyMethod(name = {"conjugate", "conj", "~"})
     @Override
     public IRubyObject conjugate(ThreadContext context) {
         return newComplex(context, getMetaClass(), real, f_negate(context, image));
     }
 
     /** nucomp_real_p
      * 
      */
     @JRubyMethod(name = "real?")
     public IRubyObject real_p(ThreadContext context) {
         return context.runtime.getFalse();
     }
 
     /** nucomp_complex_p
      * 
      */
     // @JRubyMethod(name = "complex?")
     public IRubyObject complex_p(ThreadContext context) {
         return context.runtime.getTrue();
     }
 
     /** nucomp_exact_p
      * 
      */
     // @JRubyMethod(name = "exact?")
     public IRubyObject exact_p(ThreadContext context) {
         return (f_exact_p(context, real).isTrue() && f_exact_p(context, image).isTrue()) ? context.runtime.getTrue() : context.runtime.getFalse();
     }
 
     /** nucomp_exact_p
      * 
      */
     // @JRubyMethod(name = "inexact?")
     public IRubyObject inexact_p(ThreadContext context) {
         return exact_p(context).isTrue() ? context.runtime.getFalse() : context.runtime.getTrue();
     }
 
     /** nucomp_denominator
      * 
      */
     @JRubyMethod(name = "denominator")
     public IRubyObject demoninator(ThreadContext context) {
         return f_lcm(context, f_denominator(context, real), f_denominator(context, image));
     }
 
     /** nucomp_numerator
      * 
      */
     @JRubyMethod(name = "numerator")
     @Override
     public IRubyObject numerator(ThreadContext context) {
         IRubyObject cd = callMethod(context, "denominator");
         return newComplex(context, getMetaClass(),
                           f_mul(context, 
                                 f_numerator(context, real),
                                 f_div(context, cd, f_denominator(context, real))),
                           f_mul(context,
                                 f_numerator(context, image),
                                 f_div(context, cd, f_denominator(context, image))));
     }
 
     /** nucomp_hash
      * 
      */
     @JRubyMethod(name = "hash")
     public IRubyObject hash(ThreadContext context) {
         return f_xor(context, invokedynamic(context, real, HASH), invokedynamic(context, image, HASH));
     }
 
     /** nucomp_eql_p
      * 
      */
     @JRubyMethod(name = "eql?")
     @Override
     public IRubyObject eql_p(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyComplex) {
             RubyComplex otherComplex = (RubyComplex)other;
             if (real.getMetaClass() == otherComplex.real.getMetaClass() &&
                 image.getMetaClass() == otherComplex.image.getMetaClass() &&
                 f_equal(context, this, otherComplex).isTrue()) {
                 return context.runtime.getTrue();
             }
         }
         return context.runtime.getFalse();
     }
 
     /** f_signbit
      * 
      */
     private static boolean signbit(ThreadContext context, IRubyObject x) {
         if (x instanceof RubyFloat) {
             double value = ((RubyFloat)x).getDoubleValue();
             return !Double.isNaN(value) && Double.doubleToLongBits(value) < 0;
         }
         return f_negative_p(context, x);
     }
 
     /** f_tpositive_p
      * 
      */
     private static boolean tpositive_p(ThreadContext context, IRubyObject x) {
         return !signbit(context, x);
     }
 
     /** nucomp_to_s
      * 
      */
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s(ThreadContext context) {
         boolean impos = tpositive_p(context, image);
 
         RubyString str = f_to_s(context, real).convertToString();
         str.cat(impos ? (byte)'+' : (byte)'-');
         str.cat(f_to_s(context, f_abs(context, image)).convertToString().getByteList());
         if (!lastCharDigit(str)) str.cat((byte)'*');
         str.cat((byte)'i');
         return str;
     }
 
     /** nucomp_inspect
      * 
      */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect(ThreadContext context) {
         boolean impos = tpositive_p(context, image);
         RubyString str = context.runtime.newString();
         str.cat((byte)'(');
         str.cat(f_inspect(context, real).convertToString().getByteList());
         str.cat(impos ? (byte)'+' : (byte)'-');
         str.cat(f_inspect(context, f_abs(context, image)).convertToString().getByteList());
         if (!lastCharDigit(str)) str.cat((byte)'*');
         str.cat((byte)'i');
         str.cat((byte)')');
         return str;
     }
 
     private static boolean lastCharDigit(RubyString str) {
         ByteList bytes = str.getByteList();
         return ASCIIEncoding.INSTANCE.isDigit(bytes.getUnsafeBytes()[bytes.getBegin() + bytes.getRealSize() - 1]);
     }
 
     /** nucomp_marshal_dump
      * 
      */
     @JRubyMethod(name = "marshal_dump")
     public IRubyObject marshal_dump(ThreadContext context) {
         RubyArray dump = context.runtime.newArray(real, image);
         if (hasVariables()) dump.syncVariables(this);
         return dump;
     }
 
     /** nucomp_marshal_load
      * 
      */
     @JRubyMethod(name = "marshal_load")
     public IRubyObject marshal_load(ThreadContext context, IRubyObject arg) {
         RubyArray load = arg.convertToArray();
         real = load.size() > 0 ? load.eltInternal(0) : context.runtime.getNil();
         image = load.size() > 1 ? load.eltInternal(1) : context.runtime.getNil();
 
         if (load.hasVariables()) syncVariables((IRubyObject)load);
         return this;
     }
 
     /** nucomp_to_i
      * 
      */
     @JRubyMethod(name = "to_i")
     public IRubyObject to_i(ThreadContext context) {
         if (k_inexact_p(image) || !f_zero_p(context, image)) {
             throw context.runtime.newRangeError("can't convert " + f_to_s(context, this).convertToString() + " into Integer");
         }
         return f_to_i(context, real);
     }
 
     /** nucomp_to_f
      * 
      */
     @JRubyMethod(name = "to_f")
     public IRubyObject to_f(ThreadContext context) {
         if (k_inexact_p(image) || !f_zero_p(context, image)) {
             throw context.runtime.newRangeError("can't convert " + f_to_s(context, this).convertToString() + " into Float");
         }
         return f_to_f(context, real);
     }
 
     /** nucomp_to_r
      * 
      */
     @JRubyMethod(name = "to_r")
     public IRubyObject to_r(ThreadContext context) {
         if (k_inexact_p(image) || !f_zero_p(context, image)) {
             throw context.runtime.newRangeError("can't convert " + f_to_s(context, this).convertToString() + " into Rational");
         }
         return f_to_r(context, real);
     }
 
     /** nucomp_rationalize
      *
      */
     @JRubyMethod(name = "rationalize", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject rationalize(ThreadContext context, IRubyObject[] args) {
         if (k_inexact_p(image) || !f_zero_p(context, image)) {
             throw context.runtime.newRangeError("can't convert " + f_to_s(context, this).convertToString() + " into Rational");
         }
         return real.callMethod(context, "rationalize", args);
     }
     
     static RubyArray str_to_c_internal(ThreadContext context, IRubyObject recv) {
         RubyString s = recv.convertToString();
         ByteList bytes = s.getByteList();
 
         Ruby runtime = context.runtime;
         if (bytes.getRealSize() == 0) return runtime.newArray(runtime.getNil(), recv);
 
         IRubyObject sr, si, re;
         sr = si = re = runtime.getNil();
         boolean po = false;
         IRubyObject m = RubyRegexp.newDummyRegexp(runtime, Numeric.ComplexPatterns.comp_pat0).match_m19(context, s, false, Block.NULL_BLOCK);
 
         if (!m.isNil()) {
             RubyMatchData match = (RubyMatchData)m;
             sr = match.op_aref19(RubyFixnum.one(runtime));
             si = match.op_aref19(RubyFixnum.two(runtime));
             re = match.post_match(context);
             po = true;
         }
 
         if (m.isNil()) {
             m = RubyRegexp.newDummyRegexp(runtime, Numeric.ComplexPatterns.comp_pat1).match_m19(context, s, false, Block.NULL_BLOCK);
 
             if (!m.isNil()) {
                 RubyMatchData match = (RubyMatchData)m;
                 sr = runtime.getNil();
                 si = match.op_aref19(RubyFixnum.one(runtime));
                 if (si.isNil()) si = runtime.newString();
                 IRubyObject t = match.op_aref19(RubyFixnum.two(runtime));
                 if (t.isNil()) t = runtime.newString(new ByteList(new byte[]{'1'}));
                 si.convertToString().cat(t.convertToString().getByteList());
                 re = match.post_match(context);
                 po = false;
             }
         }
 
         if (m.isNil()) {
             m = RubyRegexp.newDummyRegexp(runtime, Numeric.ComplexPatterns.comp_pat2).match_m19(context, s, false, Block.NULL_BLOCK);
             if (m.isNil()) return runtime.newArray(runtime.getNil(), recv);
             RubyMatchData match = (RubyMatchData)m;
             sr = match.op_aref19(RubyFixnum.one(runtime));
             if (match.op_aref19(RubyFixnum.two(runtime)).isNil()) {
                 si = runtime.getNil();
             } else {
                 si = match.op_aref19(RubyFixnum.three(runtime));
                 IRubyObject t = match.op_aref19(RubyFixnum.four(runtime));
                 if (t.isNil()) t = runtime.newString(RubyFixnum.SINGLE_CHAR_BYTELISTS19['1']);
                 si.convertToString().cat(t.convertToString().getByteList());
             }
             re = match.post_match(context);
             po = false;
         }
 
         IRubyObject r = RubyFixnum.zero(runtime);
         IRubyObject i = r;
 
         if (!sr.isNil()) {
             if (sr.callMethod(context, "include?", runtime.newString(new ByteList(new byte[]{'/'}))).isTrue()) {
                 r = f_to_r(context, sr);
             } else if (f_gt_p(context, sr.callMethod(context, "count", runtime.newString(".eE")), RubyFixnum.zero(runtime)).isTrue()) {
                 r = f_to_f(context, sr); 
             } else {
                 r = f_to_i(context, sr);
             }
         }
 
         if (!si.isNil()) {
             if (si.callMethod(context, "include?", runtime.newString(new ByteList(new byte[]{'/'}))).isTrue()) {
                 i = f_to_r(context, si);
             } else if (f_gt_p(context, si.callMethod(context, "count", runtime.newString(".eE")), RubyFixnum.zero(runtime)).isTrue()) {
                 i = f_to_f(context, si);
             } else {
                 i = f_to_i(context, si);
             }
         }
         return runtime.newArray(po ? newComplexPolar(context, r, i) : newComplexCanonicalize(context, r, i), re);
     }
     
     private static IRubyObject str_to_c_strict(ThreadContext context, IRubyObject recv) {
         RubyArray a = str_to_c_internal(context, recv);
         if (a.eltInternal(0).isNil() || a.eltInternal(1).convertToString().getByteList().length() > 0) {
             IRubyObject s = recv.callMethod(context, "inspect");
             throw context.runtime.newArgumentError("invalid value for convert(): " + s.convertToString());
         }
         return a.eltInternal(0);
     }    
 }
diff --git a/src/org/jruby/RubyConverter.java b/src/org/jruby/RubyConverter.java
index 25aa475f70..e7eb71238b 100644
--- a/src/org/jruby/RubyConverter.java
+++ b/src/org/jruby/RubyConverter.java
@@ -1,209 +1,204 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jcodings.Encoding;
 import org.jcodings.specific.UTF16BEEncoding;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 import java.nio.ByteBuffer;
 import java.nio.CharBuffer;
 import java.nio.charset.CharsetDecoder;
 import java.nio.charset.CharsetEncoder;
 import java.nio.charset.CoderResult;
 
 import static org.jruby.CompatVersion.*;
 import org.jruby.exceptions.RaiseException;
 import static org.jruby.runtime.Visibility.*;
 
 @JRubyClass(name="Converter")
 public class RubyConverter extends RubyObject {
     private RubyEncoding srcEncoding;
     private RubyEncoding destEncoding;
     private CharsetDecoder srcDecoder;
     private CharsetEncoder destEncoder;
 
     public static RubyClass createConverterClass(Ruby runtime) {
         RubyClass converterc = runtime.defineClassUnder("Converter", runtime.getClass("Data"), CONVERTER_ALLOCATOR, runtime.getEncoding());
         runtime.setConverter(converterc);
         converterc.index = ClassIndex.CONVERTER;
         converterc.setReifiedClass(RubyConverter.class);
-        converterc.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyConverter;
-            }
-        };
+        converterc.kindOf = new RubyModule.JavaClassKindOf(RubyConverter.class);
 
         converterc.defineAnnotatedMethods(RubyConverter.class);
         return converterc;
     }
 
     private static ObjectAllocator CONVERTER_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyConverter(runtime, klass);
         }
     };
 
     private static final Encoding UTF16 = UTF16BEEncoding.INSTANCE;
 
     public RubyConverter(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
     }
 
     public RubyConverter(Ruby runtime) {
         super(runtime, runtime.getConverter());
     }
 
     @JRubyMethod(visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject convpath) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject src, IRubyObject dest) {
         
         if (src instanceof RubyEncoding) {
             srcEncoding = (RubyEncoding)src;
         } else {
             srcEncoding = (RubyEncoding)context.runtime.getEncodingService().rubyEncodingFromObject(src);
         }
 
         
         if (dest instanceof RubyEncoding) {
             destEncoding = (RubyEncoding)dest;
         } else {
             destEncoding = (RubyEncoding)context.runtime.getEncodingService().rubyEncodingFromObject(dest);
         }
 
         
         try {
             srcDecoder = context.runtime.getEncodingService().charsetForEncoding(srcEncoding.getEncoding()).newDecoder();
             destEncoder = context.runtime.getEncodingService().charsetForEncoding(destEncoding.getEncoding()).newEncoder();
         } catch (RaiseException e) {
             if (e.getException().getMetaClass().getBaseName().equals("CompatibilityError")) {
                 throw context.runtime.newConverterNotFoundError("code converter not found (" + srcEncoding + " to " + destEncoding + ")");
             } else {
                 throw e;
             }
         }
 
         return context.runtime.getNil();
     }
 
     @JRubyMethod(visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject src, IRubyObject dest, IRubyObject opt) {
         // TODO: opt
         initialize(context, src, dest);
         return context.runtime.getNil();
     }
 
     @JRubyMethod
     public IRubyObject inspect(ThreadContext context) {
         return RubyString.newString(context.runtime, "#<Encoding::Converter: " + srcDecoder.charset().name() + " to " + destEncoder.charset().name());
     }
 
     @JRubyMethod
     public IRubyObject convpath(ThreadContext context) {
         // we always pass through UTF-16
         IRubyObject utf16Encoding = context.runtime.getEncodingService().getEncodingList()[UTF16.getIndex()];
         return RubyArray.newArray(
                 context.runtime,
                 RubyArray.newArray(context.runtime, srcEncoding, utf16Encoding),
                 RubyArray.newArray(context.runtime, utf16Encoding, destEncoding)
         );
     }
 
     @JRubyMethod
     public IRubyObject source_encoding() {
         return srcEncoding;
     }
 
     @JRubyMethod
     public IRubyObject destination_encoding() {
         return destEncoding;
     }
 
     @JRubyMethod
     public IRubyObject primitive_convert(ThreadContext context, IRubyObject src, IRubyObject dest) {
         RubyString result = (RubyString)convert(context, src);
         dest.convertToString().replace19(result);
 
         return context.runtime.newSymbol("finished");
     }
 
     @JRubyMethod
     public IRubyObject convert(ThreadContext context, IRubyObject srcBuffer) {
         if (!(srcBuffer instanceof RubyString)) {
             throw context.runtime.newTypeError(srcBuffer, context.runtime.getString());
         }
 
         RubyString srcString = (RubyString)srcBuffer;
 
         ByteList srcBL = srcString.getByteList();
 
         if (srcBL.getRealSize() == 0) return context.runtime.newSymbol("source_buffer_empty");
 
         ByteBuffer srcBB = ByteBuffer.wrap(srcBL.getUnsafeBytes(), srcBL.begin(), srcBL.getRealSize());
         try {
             CharBuffer srcCB = CharBuffer.allocate((int) (srcDecoder.maxCharsPerByte() * srcBL.getRealSize()) + 1);
             CoderResult decodeResult = srcDecoder.decode(srcBB, srcCB, true);
             srcCB.flip();
 
             ByteBuffer destBB = ByteBuffer.allocate((int) (destEncoder.maxBytesPerChar() * srcCB.limit()) + 1);
             CoderResult encodeResult = destEncoder.encode(srcCB, destBB, true);
             destBB.flip();
 
             byte[] destBytes = new byte[destBB.limit()];
             destBB.get(destBytes);
 
             srcDecoder.reset();
             destEncoder.reset();
             
             return context.runtime.newString(new ByteList(destBytes, destEncoding.getEncoding(), false));
         } catch (Exception e) {
             throw context.runtime.newRuntimeError(e.getLocalizedMessage());
         }
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject replacement(ThreadContext context) {
         return RubyString.newString(context.runtime, srcDecoder.replacement());
     }
 
 
     @JRubyMethod(name = "replacement=", compat = RUBY1_9)
     public IRubyObject replacement_set(ThreadContext context, IRubyObject replacement) {
         srcDecoder.replaceWith(replacement.convertToString().asJavaString());
 
         return replacement;
     }}
diff --git a/src/org/jruby/RubyEncoding.java b/src/org/jruby/RubyEncoding.java
index 7f1744781c..be2861b6d9 100755
--- a/src/org/jruby/RubyEncoding.java
+++ b/src/org/jruby/RubyEncoding.java
@@ -1,499 +1,494 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.lang.ref.SoftReference;
 import java.nio.ByteBuffer;
 import java.nio.CharBuffer;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetDecoder;
 import java.nio.charset.CharsetEncoder;
 import java.nio.charset.CodingErrorAction;
 
 import org.jcodings.Encoding;
 import org.jcodings.EncodingDB.Entry;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jcodings.util.CaseInsensitiveBytesHash;
 import org.jcodings.util.Hash.HashEntryIterator;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.runtime.encoding.EncodingService;
 import org.jruby.util.ByteList;
 import org.jruby.util.StringSupport;
 import static org.jruby.CompatVersion.*;
 
 @JRubyClass(name="Encoding")
 public class RubyEncoding extends RubyObject {
     public static final Charset UTF8 = Charset.forName("UTF-8");
     public static final Charset ISO = Charset.forName("ISO-8859-1");
     public static final ByteList LOCALE = ByteList.create("locale");
     public static final ByteList EXTERNAL = ByteList.create("external");
 
     public static RubyClass createEncodingClass(Ruby runtime) {
         RubyClass encodingc = runtime.defineClass("Encoding", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setEncoding(encodingc);
         encodingc.index = ClassIndex.ENCODING;
         encodingc.setReifiedClass(RubyEncoding.class);
-        encodingc.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyEncoding;
-            }
-        };
+        encodingc.kindOf = new RubyModule.JavaClassKindOf(RubyEncoding.class);
 
         encodingc.getSingletonClass().undefineMethod("allocate");
         encodingc.defineAnnotatedMethods(RubyEncoding.class);
 
         return encodingc;
     }
 
     private Encoding encoding;
     private final ByteList name;
     private final boolean isDummy;
 
     private RubyEncoding(Ruby runtime, byte[] name, int p, int end, boolean isDummy) {
         super(runtime, runtime.getEncoding());
         this.name = new ByteList(name, p, end);
         this.isDummy = isDummy;
     }
     
     private RubyEncoding(Ruby runtime, byte[]name, boolean isDummy) {
         this(runtime, name, 0, name.length, isDummy);
     }
 
     private RubyEncoding(Ruby runtime, Encoding encoding) {
         super(runtime, runtime.getEncoding());
         this.name = new ByteList(encoding.getName());
         this.isDummy = false;
         this.encoding = encoding;
     }
 
     public static RubyEncoding newEncoding(Ruby runtime, byte[] name, int p, int end, boolean isDummy) {
         return new RubyEncoding(runtime, name, p, end, isDummy);
     }
 
     public static RubyEncoding newEncoding(Ruby runtime, byte[] name, boolean isDummy) {
         return new RubyEncoding(runtime, name, isDummy);
     }
 
     public static RubyEncoding newEncoding(Ruby runtime, Encoding encoding) {
         return new RubyEncoding(runtime, encoding);
     }
 
     public final Encoding getEncoding() {
         // TODO: make threadsafe
         if (encoding == null) encoding = getRuntime().getEncodingService().loadEncoding(name);
         return encoding;
     }
 
     public static Encoding areCompatible(IRubyObject obj1, IRubyObject obj2) {
         Encoding enc1 = null;
         Encoding enc2 = null;
 
         if (obj1 instanceof RubyEncoding) {
             enc1 = ((RubyEncoding)obj1).getEncoding();
         } else if (obj1 instanceof RubySymbol) {
             enc1 = ((RubySymbol)obj1).asString().getEncoding();
         } else if (obj1 instanceof EncodingCapable) {
             enc1 = ((EncodingCapable)obj1).getEncoding();
         }
 
         if (obj2 instanceof RubyEncoding) {
             enc2 = ((RubyEncoding)obj2).getEncoding();
         } else if (obj2 instanceof RubySymbol) {
             enc2 = ((RubySymbol)obj2).asString().getEncoding();
         } else if (obj2 instanceof EncodingCapable) {
             enc2 = ((EncodingCapable)obj2).getEncoding();
         }
 
         if (enc1 != null && enc2 != null) {
             if (enc1 == enc2) return enc1;
 
             if (obj2 instanceof RubyString && ((RubyString) obj2).getByteList().getRealSize() == 0) return enc1;
             if (obj1 instanceof RubyString && ((RubyString) obj1).getByteList().getRealSize() == 0) return enc2;
 
             if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
 
             if (!(obj2 instanceof RubyString) && enc2 instanceof USASCIIEncoding) return enc1;
             if (!(obj1 instanceof RubyString) && enc1 instanceof USASCIIEncoding) return enc2;
 
             if (!(obj1 instanceof RubyString)) {
                 IRubyObject objTmp = obj1;
                 obj1 = obj2;
                 obj1 = objTmp;
 
                 Encoding encTmp = enc1;
                 enc1 = enc2;
                 enc2 = encTmp;
             }
 
             if (obj1 instanceof RubyString) {
                 int cr1 = ((RubyString)obj1).scanForCodeRange();
                 if (obj2 instanceof RubyString) {
                     int cr2 = ((RubyString)obj2).scanForCodeRange();
                     return areCompatible(enc1, cr1, enc2, cr2);
                 }
                 if (cr1 == StringSupport.CR_7BIT) return enc2;
             }
         }
         return null;
     }
 
     static Encoding areCompatible(Encoding enc1, int cr1, Encoding enc2, int cr2) {
         if (cr1 != cr2) {
             /* may need to handle ENC_CODERANGE_BROKEN */
             if (cr1 == StringSupport.CR_7BIT) return enc2;
             if (cr2 == StringSupport.CR_7BIT) return enc1;
         }
         if (cr2 == StringSupport.CR_7BIT) return enc1;
         if (cr1 == StringSupport.CR_7BIT) return enc2;
         return null;
     }
 
     public static byte[] encodeUTF8(CharSequence cs) {
         return getUTF8Coder().encode(cs);
     }
 
     public static byte[] encodeUTF8(String str) {
         return getUTF8Coder().encode(str);
     }
 
     public static byte[] encode(CharSequence cs, Charset charset) {
         ByteBuffer buffer = charset.encode(cs.toString());
         byte[] bytes = new byte[buffer.limit()];
         buffer.get(bytes);
         return bytes;
     }
 
     public static byte[] encode(String str, Charset charset) {
         ByteBuffer buffer = charset.encode(str);
         byte[] bytes = new byte[buffer.limit()];
         buffer.get(bytes);
         return bytes;
     }
 
     public static String decodeUTF8(byte[] bytes, int start, int length) {
         return getUTF8Coder().decode(bytes, start, length);
     }
 
     public static String decodeUTF8(byte[] bytes) {
         return getUTF8Coder().decode(bytes);
     }
 
     public static String decode(byte[] bytes, int start, int length, Charset charset) {
         return charset.decode(ByteBuffer.wrap(bytes, start, length)).toString();
     }
 
     public static String decode(byte[] bytes, Charset charset) {
         return charset.decode(ByteBuffer.wrap(bytes)).toString();
     }
     
     private static class UTF8Coder {
         private final CharsetEncoder encoder = UTF8.newEncoder();
         private final CharsetDecoder decoder = UTF8.newDecoder();
         /** The maximum number of characters we can encode/decode in our cached buffers */
         private static final int CHAR_THRESHOLD = 1024;
         /** The resulting encode/decode buffer sized by the max number of
          * characters (using 4 bytes per char possible for utf-8) */
         private static final int BUF_SIZE = CHAR_THRESHOLD * 4;
         private final ByteBuffer byteBuffer = ByteBuffer.allocate(BUF_SIZE);
         private final CharBuffer charBuffer = CharBuffer.allocate(BUF_SIZE);
 
         public UTF8Coder() {
             decoder.onMalformedInput(CodingErrorAction.REPLACE);
             decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
         }
 
         public byte[] encode(CharSequence cs) {
             ByteBuffer buffer;
             if (cs.length() > CHAR_THRESHOLD) {
                 buffer = UTF8.encode(cs.toString());
             } else {
                 buffer = byteBuffer;
                 CharBuffer cbuffer = charBuffer;
                 buffer.clear();
                 cbuffer.clear();
                 cbuffer.put(cs.toString());
                 cbuffer.flip();
                 encoder.encode(cbuffer, buffer, true);
                 buffer.flip();
             }
             
             byte[] bytes = new byte[buffer.limit()];
             buffer.get(bytes);
             return bytes;
         }
         
         public String decode(byte[] bytes, int start, int length) {
             CharBuffer cbuffer;
             if (length > CHAR_THRESHOLD) {
                 cbuffer = UTF8.decode(ByteBuffer.wrap(bytes, start, length));
             } else {
                 cbuffer = charBuffer;
                 ByteBuffer buffer = byteBuffer;
                 cbuffer.clear();
                 buffer.clear();
                 buffer.put(bytes, start, length);
                 buffer.flip();
                 decoder.decode(buffer, cbuffer, true);
                 cbuffer.flip();
             }
             
             return cbuffer.toString();
         }
         
         public String decode(byte[] bytes) {
             return decode(bytes, 0, bytes.length);
         }
     }
 
     /**
      * UTF8Coder wrapped in a SoftReference to avoid possible ClassLoader leak.
      * See JRUBY-6522
      */
     private static final ThreadLocal<SoftReference<UTF8Coder>> UTF8_CODER =
         new ThreadLocal<SoftReference<UTF8Coder>>();
 
     private static UTF8Coder getUTF8Coder() {
         UTF8Coder coder;
         SoftReference<UTF8Coder> ref = UTF8_CODER.get();
         if (ref == null || (coder = ref.get()) == null) {
             coder = new UTF8Coder();
             ref = new SoftReference<UTF8Coder>(coder);
             UTF8_CODER.set(ref);
         }
         
         return coder;
     }
 
     @JRubyMethod(name = "list", meta = true)
     public static IRubyObject list(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.runtime;
         return RubyArray.newArrayNoCopy(runtime, runtime.getEncodingService().getEncodingList(), 0);
     }
 
     @JRubyMethod(name = "locale_charmap", meta = true)
     public static IRubyObject locale_charmap(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.runtime;
         EncodingService service = runtime.getEncodingService();
         ByteList name = new ByteList(service.getLocaleEncoding().getName());
         
         return RubyString.newUsAsciiStringNoCopy(runtime, name);
     }
 
     @SuppressWarnings("unchecked")
     @JRubyMethod(name = "name_list", meta = true)
     public static IRubyObject name_list(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.runtime;
         EncodingService service = runtime.getEncodingService();
         
         RubyArray result = runtime.newArray(service.getEncodings().size() + service.getAliases().size());
         HashEntryIterator i;
         i = service.getEncodings().entryIterator();
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
         }
         i = service.getAliases().entryIterator();        
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
         }
 
         result.append(runtime.newString(EXTERNAL));
         result.append(runtime.newString(LOCALE));
         
         return result;
     }
 
     @SuppressWarnings("unchecked")
     @JRubyMethod(name = "aliases", meta = true)
     public static IRubyObject aliases(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.runtime;
         EncodingService service = runtime.getEncodingService();
 
         IRubyObject list[] = service.getEncodingList();
         HashEntryIterator i = service.getAliases().entryIterator();
         RubyHash result = RubyHash.newHash(runtime);
 
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             IRubyObject alias = RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context);
             IRubyObject name = RubyString.newUsAsciiStringShared(runtime, 
                                 ((RubyEncoding)list[e.value.getIndex()]).name).freeze(context);
             result.fastASet(alias, name);
         }
 
         result.fastASet(runtime.newString(EXTERNAL),
                 runtime.newString(new ByteList(runtime.getDefaultExternalEncoding().getName())));
         result.fastASet(runtime.newString(LOCALE),
                 runtime.newString(new ByteList(service.getLocaleEncoding().getName())));
 
         return result;
     }
 
     @JRubyMethod(name = "find", meta = true)
     public static IRubyObject find(ThreadContext context, IRubyObject recv, IRubyObject str) {
         Ruby runtime = context.runtime;
 
         // Wacky but true...return arg if it is an encoding looking for itself
         if (str instanceof RubyEncoding) return str;
 
         return runtime.getEncodingService().rubyEncodingFromObject(str);
     }
 
     @JRubyMethod(name = "_dump")
     public IRubyObject _dump(ThreadContext context, IRubyObject arg) {
         return to_s(context);
     }
 
     @JRubyMethod(name = "_load", meta = true)
     public static IRubyObject _load(ThreadContext context, IRubyObject recv, IRubyObject str) {
         return find(context, recv, str);
     }
 
     @JRubyMethod(name = "ascii_compatible?")
     public IRubyObject asciiCompatible_p(ThreadContext context) {
         return context.runtime.newBoolean(getEncoding().isAsciiCompatible());
     }
 
     @JRubyMethod(name = {"to_s", "name"})
     public IRubyObject to_s(ThreadContext context) {
         // TODO: rb_usascii_str_new2
         return RubyString.newUsAsciiStringShared(context.runtime, name);
     }
 
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect(ThreadContext context) {
         ByteList bytes = new ByteList();
         bytes.append("#<Encoding:".getBytes());
         bytes.append(name);
         if (isDummy) bytes.append(" (dummy)".getBytes());
         bytes.append('>');
         return RubyString.newUsAsciiStringNoCopy(context.runtime, bytes);
     }
 
     @SuppressWarnings("unchecked")
     @JRubyMethod(name = "names")
     public IRubyObject names(ThreadContext context) {
         Ruby runtime = context.runtime;
         EncodingService service = runtime.getEncodingService();
         Entry entry = service.findEncodingOrAliasEntry(name);
 
         RubyArray result = runtime.newArray();
         HashEntryIterator i;
         i = service.getEncodings().entryIterator();
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             if (e.value == entry) {
                 result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
             }
         }
         i = service.getAliases().entryIterator();        
         while (i.hasNext()) {
             CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry> e = 
                 ((CaseInsensitiveBytesHash.CaseInsensitiveBytesHashEntry<Entry>)i.next());
             if (e.value == entry) {
                 result.append(RubyString.newUsAsciiStringShared(runtime, e.bytes, e.p, e.end - e.p).freeze(context));
             }
         }
         result.append(runtime.newString(EXTERNAL));
         result.append(runtime.newString(LOCALE));
         
         return result;
     }
 
     @JRubyMethod(name = "dummy?")
     public IRubyObject dummy_p(ThreadContext context) {
         return context.runtime.newBoolean(isDummy);
     }
 
     @JRubyMethod(name = "compatible?", meta = true)
     public static IRubyObject compatible_p(ThreadContext context, IRubyObject self, IRubyObject first, IRubyObject second) {
         Ruby runtime = context.runtime;
         Encoding enc = areCompatible(first, second);
 
         return enc == null ? runtime.getNil() : runtime.getEncodingService().getEncoding(enc);
     }
 
     @JRubyMethod(name = "default_external", meta = true, compat = RUBY1_9)
     public static IRubyObject getDefaultExternal(IRubyObject recv) {
         return recv.getRuntime().getEncodingService().getDefaultExternal();
     }
 
     @JRubyMethod(name = "default_external=", meta = true, compat = RUBY1_9)
     public static IRubyObject setDefaultExternal(IRubyObject recv, IRubyObject encoding) {
         Ruby runtime = recv.getRuntime();
         EncodingService service = runtime.getEncodingService();
         if (encoding.isNil()) {
             throw runtime.newArgumentError("default_external can not be nil");
         }
         runtime.setDefaultExternalEncoding(service.getEncodingFromObject(encoding));
         return encoding;
     }
 
     @JRubyMethod(name = "default_internal", meta = true, compat = RUBY1_9)
     public static IRubyObject getDefaultInternal(IRubyObject recv) {
         return recv.getRuntime().getEncodingService().getDefaultInternal();
     }
 
     @JRubyMethod(name = "default_internal=", required = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject setDefaultInternal(IRubyObject recv, IRubyObject encoding) {
         Ruby runtime = recv.getRuntime();
         EncodingService service = runtime.getEncodingService();
         runtime.setDefaultInternalEncoding(service.getEncodingFromObject(encoding));
         return encoding;
     }
 
     @Deprecated
     public static IRubyObject getDefaultExternal(Ruby runtime) {
         return runtime.getEncodingService().getDefaultExternal();
     }
 
     @Deprecated
     public static IRubyObject getDefaultInternal(Ruby runtime) {
         return runtime.getEncodingService().getDefaultInternal();
     }
 
     @Deprecated
     public static IRubyObject convertEncodingToRubyEncoding(Ruby runtime, Encoding defaultEncoding) {
         return runtime.getEncodingService().convertEncodingToRubyEncoding(defaultEncoding);
     }
 
     @Deprecated
     public static Encoding getEncodingFromObject(Ruby runtime, IRubyObject arg) {
         return runtime.getEncodingService().getEncodingFromObject(arg);
     }
 }
diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index d0d2b2cb8c..c6a756d79f 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1110 +1,1105 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2003 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2007 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import jnr.constants.platform.OpenFlags;
 import org.jcodings.Encoding;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.ChannelDescriptor;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.net.URI;
 import java.net.URL;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.FileLock;
 import java.util.Enumeration;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipFile;
 import org.jcodings.specific.ASCIIEncoding;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import jnr.posix.FileStat;
 import jnr.posix.util.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.DirectoryAsFileException;
 import org.jruby.util.io.PermissionDeniedException;
 import org.jruby.util.io.Stream;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.IOOptions;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.encoding.EncodingService;
 import org.jruby.util.CharsetTranscoder;
 import org.jruby.util.io.EncodingUtils;
 
 /**
  * Ruby File class equivalent in java.
  **/
 @JRubyClass(name="File", parent="IO", include="FileTest")
 public class RubyFile extends RubyIO implements EncodingCapable {
     public static RubyClass createFileClass(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
 
         RubyClass fileClass = runtime.defineClass("File", runtime.getIO(), FILE_ALLOCATOR);
 
         runtime.setFile(fileClass);
 
         fileClass.defineAnnotatedMethods(RubyFile.class);
 
         fileClass.index = ClassIndex.FILE;
         fileClass.setReifiedClass(RubyFile.class);
 
-        fileClass.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyFile;
-            }
-        };
+        fileClass.kindOf = new RubyModule.JavaClassKindOf(RubyFile.class);
 
         // file separator constants
         RubyString separator = runtime.newString("/");
         separator.freeze(context);
         fileClass.defineConstant("SEPARATOR", separator);
         fileClass.defineConstant("Separator", separator);
 
         if (File.separatorChar == '\\') {
             RubyString altSeparator = runtime.newString("\\");
             altSeparator.freeze(context);
             fileClass.defineConstant("ALT_SEPARATOR", altSeparator);
         } else {
             fileClass.defineConstant("ALT_SEPARATOR", runtime.getNil());
         }
 
         // path separator
         RubyString pathSeparator = runtime.newString(File.pathSeparator);
         pathSeparator.freeze(context);
         fileClass.defineConstant("PATH_SEPARATOR", pathSeparator);
 
         // For JRUBY-5276, physically define FileTest methods on File's singleton
         fileClass.getSingletonClass().defineAnnotatedMethods(RubyFileTest.FileTestFileMethods.class);
 
         // Create Constants class
         RubyModule constants = fileClass.defineModuleUnder("Constants");
 
         // open flags
         for (OpenFlags f : OpenFlags.values()) {
             // Strip off the O_ prefix, so they become File::RDONLY, and so on
             final String name = f.name();
             if (name.startsWith("O_")) {
                 final String cname = name.substring(2);
                 // Special case for handling ACCMODE, since constantine will generate
                 // an invalid value if it is not defined by the platform.
                 final RubyFixnum cvalue = f == OpenFlags.O_ACCMODE
                         ? runtime.newFixnum(ModeFlags.ACCMODE)
                         : runtime.newFixnum(f.intValue());
                 constants.setConstant(cname, cvalue);
             }
         }
 
         // case handling, escaping, path and dot matching
         constants.setConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         constants.setConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         constants.setConstant("FNM_SYSCASE", runtime.newFixnum(FNM_SYSCASE));
         constants.setConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
         constants.setConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
 
         // flock operations
         constants.setConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         constants.setConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         constants.setConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         constants.setConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         // NULL device
         if (runtime.is1_9() || runtime.is2_0()) {
             constants.setConstant("NULL", runtime.newString(getNullDevice()));
         }
 
         // File::Constants module is included in IO.
         runtime.getIO().includeModule(constants);
 
         return fileClass;
     }
 
     private static ObjectAllocator FILE_ALLOCATOR = new ObjectAllocator() {
         @Override
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyFile instance = new RubyFile(runtime, klass);
 
             instance.setMetaClass(klass);
 
             return instance;
         }
     };
 
     private static String getNullDevice() {
         // FIXME: MRI defines special null device for Amiga and VMS, but currently
         // we lack ability to detect these platforms
         String null_device;
         if (Platform.IS_WINDOWS) {
             null_device = "NUL";
         } else {
             null_device = "/dev/null";
         }
         return null_device;
     }
 
     public RubyFile(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     // XXX This constructor is a hack to implement the __END__ syntax.
     //     Converting a reader back into an InputStream doesn't generally work.
     public RubyFile(Ruby runtime, String path, final Reader reader) {
         this(runtime, path, new InputStream() {
             @Override
             public int read() throws IOException {
                 return reader.read();
             }
         });
     }
 
     public RubyFile(Ruby runtime, String path, InputStream in) {
         super(runtime, runtime.getFile());
         this.path = path;
         try {
             this.openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(in))));
             this.openFile.setMode(openFile.getMainStreamSafe().getModes().getOpenFileFlags());
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         }
     }
     
     @JRubyMethod
     @Override
     public IRubyObject close() {
         // Make sure any existing lock is released before we try and close the file
         if (currentLock != null) {
             try {
                 currentLock.release();
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
         }
         return super.close();
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject flock(ThreadContext context, IRubyObject lockingConstant) {
         Ruby runtime = context.runtime;
         
         // TODO: port exact behavior from MRI, and move most locking logic into ChannelDescriptor
         // TODO: for all LOCK_NB cases, return false if they would block
         ChannelDescriptor descriptor;
         try {
             descriptor = openFile.getMainStreamSafe().getDescriptor();
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
 
         // null channel always succeeds for all locking operations
         if (descriptor.isNull()) return RubyFixnum.zero(runtime);
 
         if (descriptor.getChannel() instanceof FileChannel) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             int lockMode = RubyNumeric.num2int(lockingConstant);
 
             checkSharedExclusive(runtime, openFile, lockMode);
     
             if (!lockStateChanges(currentLock, lockMode)) return RubyFixnum.zero(runtime);
 
             try {
                 synchronized (fileChannel) {
                     // check again, to avoid unnecessary overhead
                     if (!lockStateChanges(currentLock, lockMode)) return RubyFixnum.zero(runtime);
                     
                     switch (lockMode) {
                         case LOCK_UN:
                         case LOCK_UN | LOCK_NB:
                             return unlock(runtime);
                         case LOCK_EX:
                             return lock(runtime, fileChannel, true);
                         case LOCK_EX | LOCK_NB:
                             return tryLock(runtime, fileChannel, true);
                         case LOCK_SH:
                             return lock(runtime, fileChannel, false);
                         case LOCK_SH | LOCK_NB:
                             return tryLock(runtime, fileChannel, false);
                     }
                 }
             } catch (IOException ioe) {
                 if (runtime.getDebug().isTrue()) {
                     ioe.printStackTrace(System.err);
                 }
             } catch (java.nio.channels.OverlappingFileLockException ioe) {
                 if (runtime.getDebug().isTrue()) {
                     ioe.printStackTrace(System.err);
                 }
             }
             return lockFailedReturn(runtime, lockMode);
         } else {
             // We're not actually a real file, so we can't flock
             return runtime.getFalse();
         }
     }
 
     @JRubyMethod(required = 1, optional = 2, visibility = PRIVATE, compat = RUBY1_8)
     @Override
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (openFile == null) {
             throw getRuntime().newRuntimeError("reinitializing File");
         }
         
         if (args.length > 0 && args.length < 3) {
             if (args[0] instanceof RubyInteger) {
                 return super.initialize(args, block);
             }
         }
 
         return openFile(args);
     }
 
     @JRubyMethod(name = "initialize", required = 1, optional = 2, visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject[] args, Block block) {
         if (openFile == null) {
             throw context.runtime.newRuntimeError("reinitializing File");
         }
 
         if (args.length > 0 && args.length <= 3) {
             IRubyObject fd = TypeConverter.convertToTypeWithCheck(args[0], context.runtime.getFixnum(), "to_int");
             if (!fd.isNil()) {
                 if (args.length == 1) {
                     return super.initialize19(context, fd, block);
                 } else if (args.length == 2) {
                     return super.initialize19(context, fd, args[1], block);
                 }
                 return super.initialize19(context, fd, args[1], args[2], block);
             }
         }
 
         return openFile19(context, args);
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject chmod(ThreadContext context, IRubyObject arg) {
         checkClosed(context);
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw context.runtime.newErrnoENOENTError(path);
         }
 
         return context.runtime.newFixnum(context.runtime.getPosix().chmod(path, mode));
     }
 
     @JRubyMethod(required = 2)
     public IRubyObject chown(ThreadContext context, IRubyObject arg1, IRubyObject arg2) {
         checkClosed(context);
         int owner = -1;
         if (!arg1.isNil()) {
             owner = RubyNumeric.num2int(arg1);
         }
 
         int group = -1;
         if (!arg2.isNil()) {
             group = RubyNumeric.num2int(arg2);
         }
 
         if (!new File(path).exists()) {
             throw context.runtime.newErrnoENOENTError(path);
         }
 
         return context.runtime.newFixnum(context.runtime.getPosix().chown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject atime(ThreadContext context) {
         checkClosed(context);
         return context.runtime.newFileStat(path, false).atime();
     }
 
     @JRubyMethod
     public IRubyObject ctime(ThreadContext context) {
         checkClosed(context);
         return context.runtime.newFileStat(path, false).ctime();
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject lchmod(ThreadContext context, IRubyObject arg) {
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw context.runtime.newErrnoENOENTError(path);
         }
 
         return context.runtime.newFixnum(context.runtime.getPosix().lchmod(path, mode));
     }
 
     // TODO: this method is not present in MRI!
     @JRubyMethod(required = 2)
     public IRubyObject lchown(ThreadContext context, IRubyObject arg1, IRubyObject arg2) {
         int owner = -1;
         if (!arg1.isNil()) {
             owner = RubyNumeric.num2int(arg1);
         }
 
         int group = -1;
         if (!arg2.isNil()) {
             group = RubyNumeric.num2int(arg2);
         }
 
         if (!new File(path).exists()) {
             throw context.runtime.newErrnoENOENTError(path);
         }
 
         return context.runtime.newFixnum(context.runtime.getPosix().lchown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject lstat(ThreadContext context) {
         checkClosed(context);
         return context.runtime.newFileStat(path, true);
     }
     
     @JRubyMethod
     public IRubyObject mtime(ThreadContext context) {
         checkClosed(context);
         return context.runtime.newFileStat(path, false).mtime();
     }
 
     @JRubyMethod(meta = true, compat = RUBY1_9)
     public static IRubyObject path(ThreadContext context, IRubyObject self, IRubyObject str) {
         return get_path(context, str);
     }
 
     @JRubyMethod(name = {"path", "to_path"})
     public IRubyObject path(ThreadContext context) {
         IRubyObject newPath = context.runtime.getNil();
         if (path != null) {
             newPath = context.runtime.newString(path);
             newPath.setTaint(true);
         }
         return newPath;
     }
 
     @JRubyMethod
     @Override
     public IRubyObject stat(ThreadContext context) {
         checkClosed(context);
         return context.runtime.newFileStat(path, false);
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject truncate(ThreadContext context, IRubyObject arg) {
         RubyInteger newLength = arg.convertToInteger();
         if (newLength.getLongValue() < 0) {
             throw context.runtime.newErrnoEINVALError(path);
         }
         try {
             openFile.checkWritable(context.runtime);
             openFile.getMainStreamSafe().ftruncate(newLength.getLongValue());
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.runtime.newErrnoESPIPEError();
         } catch (InvalidValueException ex) {
             throw context.runtime.newErrnoEINVALError();
         } catch (IOException e) {
             // Should we do anything?
         }
 
         return RubyFixnum.zero(context.runtime);
     }
 
     @JRubyMethod
     @Override
     public IRubyObject inspect() {
         StringBuilder val = new StringBuilder();
         val.append("#<File:").append(path);
         if(!openFile.isOpen()) {
             val.append(" (closed)");
         }
         val.append(">");
         return getRuntime().newString(val.toString());
     }
     
     /* File class methods */
     
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject basename(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         String name = get_path(context,args[0]).getUnicodeValue();
 
         // MRI-compatible basename handling for windows drive letter paths
         if (Platform.IS_WINDOWS) {
             if (name.length() > 1 && name.charAt(1) == ':' && Character.isLetter(name.charAt(0))) {
                 switch (name.length()) {
                 case 2:
                     return RubyString.newEmptyString(context.runtime).infectBy(args[0]);
                 case 3:
                     return context.runtime.newString(name.substring(2)).infectBy(args[0]);
                 default:
                     switch (name.charAt(2)) {
                     case '/':
                     case '\\':
                         break;
                     default:
                         // strip c: away from relative-pathed name
                         name = name.substring(2);
                         break;
                     }
                     break;
                 }
             }
         }
 
         while (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
             name = name.substring(0, name.length() - 1);
         }
         
         // Paths which end in "/" or "\\" must be stripped off.
         int slashCount = 0;
         int length = name.length();
         for (int i = length - 1; i >= 0; i--) {
             char c = name.charAt(i);
             if (c != '/' && c != '\\') {
                 break;
             }
             slashCount++;
         }
         if (slashCount > 0 && length > 1) {
             name = name.substring(0, name.length() - slashCount);
         }
         
         int index = name.lastIndexOf('/');
         if (index == -1) {
             // XXX actually only on windows...
             index = name.lastIndexOf('\\');
         }
         
         if (!name.equals("/") && index != -1) {
             name = name.substring(index + 1);
         }
         
         if (args.length == 2) {
             String ext = RubyString.stringValue(args[1]).toString();
             if (".*".equals(ext)) {
                 index = name.lastIndexOf('.');
                 if (index > 0) {  // -1 no match; 0 it is dot file not extension
                     name = name.substring(0, index);
                 }
             } else if (name.endsWith(ext)) {
                 name = name.substring(0, name.length() - ext.length());
             }
         }
         return context.runtime.newString(name).infectBy(args[0]);
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject chmod(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             JRubyFile filename = file(args[i]);
             
             if (!filename.exists()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
             
             if (0 != runtime.getPosix().chmod(filename.getAbsolutePath(), (int)mode.getLongValue())) {
                 throw runtime.newErrnoFromLastPOSIXErrno();
             } else {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 3, rest = true, meta = true)
     public static IRubyObject chown(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
 
         int count = 0;
         int owner = -1;
         if (!args[0].isNil()) {
             owner = RubyNumeric.num2int(args[0]);
         }
 
         int group = -1;
         if (!args[1].isNil()) {
             group = RubyNumeric.num2int(args[1]);
         }
         for (int i = 2; i < args.length; i++) {
             JRubyFile filename = file(args[i]);
 
             if (!filename.exists()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
             
             if (0 != runtime.getPosix().chown(filename.getAbsolutePath(), owner, group)) {
                 throw runtime.newErrnoFromLastPOSIXErrno();
             } else {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject dirname(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyString filename = get_path(context, arg);
         
         String jfilename = filename.asJavaString();
         
         String name = jfilename.replace('\\', '/');
         int minPathLength = 1;
         boolean trimmedSlashes = false;
 
         boolean startsWithDriveLetterOnWindows = startsWithDriveLetterOnWindows(name);
 
         if (startsWithDriveLetterOnWindows) {
             minPathLength = 3;
         }
 
         while (name.length() > minPathLength && name.charAt(name.length() - 1) == '/') {
             trimmedSlashes = true;
             name = name.substring(0, name.length() - 1);
         }
 
         String result;
         if (startsWithDriveLetterOnWindows && name.length() == 2) {
             if (trimmedSlashes) {
                 // C:\ is returned unchanged
                 result = jfilename.substring(0, 3);
             } else {
                 result = jfilename.substring(0, 2) + '.';
             }
         } else {
             //TODO deal with UNC names
             int index = name.lastIndexOf('/');
 
             if (index == -1) {
                 if (startsWithDriveLetterOnWindows) {
                     return context.runtime.newString(jfilename.substring(0, 2) + ".");
                 } else {
                     return context.runtime.newString(".");
                 }
             }
             if (index == 0) {
                 return context.runtime.newString("/");
             }
 
             if (startsWithDriveLetterOnWindows && index == 2) {
                 // Include additional path separator
                 // (so that dirname of "C:\file.txt" is  "C:\", not "C:")
                 index++;
             }
 
             if (jfilename.startsWith("\\\\")) {
                 index = jfilename.length();
                 String[] splitted = jfilename.split(Pattern.quote("\\"));
                 int last = splitted.length-1;
                 if (splitted[last].contains(".")) {
                     index = jfilename.lastIndexOf("\\");
                 }
                 
             }
             
             result = jfilename.substring(0, index);
             
         }
         
         char endChar;
         // trim trailing slashes
         while (result.length() > minPathLength) {
             endChar = result.charAt(result.length() - 1);
             if (endChar == '/' || endChar == '\\') {
                 result = result.substring(0, result.length() - 1);
             } else {
                 break;
             }
         }
 
         return context.runtime.newString(result).infectBy(filename);
     }
 
     /**
      * Returns the extension name of the file. An empty string is returned if 
      * the filename (not the entire path) starts or ends with a dot.
      * @param recv
      * @param arg Path to get extension name of
      * @return Extension, including the dot, or an empty string
      */
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject extname(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         IRubyObject baseFilename = basename(context, recv, new IRubyObject[]{arg});
         
         String filename = RubyString.stringValue(baseFilename).getUnicodeValue();
         String result = "";
 
         int dotIndex = filename.lastIndexOf(".");
         if (dotIndex > 0 && dotIndex != (filename.length() - 1)) {
             // Dot is not at beginning and not at end of filename. 
             result = filename.substring(dotIndex);
         }
 
         return context.runtime.newString(result);
     }
 
     /**
      * Converts a pathname to an absolute pathname. Relative paths are 
      * referenced from the current working directory of the process unless 
      * a second argument is given, in which case it will be used as the 
      * starting point. If the second argument is also relative, it will 
      * first be converted to an absolute pathname.
      * @param recv
      * @param args 
      * @return Resulting absolute path as a String
      */
     @JRubyMethod(required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject expand_path(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, true);
     }
 
     @JRubyMethod(name = "expand_path", required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject expand_path19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         RubyString path = (RubyString) expandPathInternal(context, recv, args, true);
         path.force_encoding(context, context.runtime.getEncodingService().getDefaultExternal());
 
         return path;
     }
 
 
     /**
      * ---------------------------------------------------- File::absolute_path
      *      File.absolute_path(file_name [, dir_string] ) -> abs_file_name
      *
      *      From Ruby 1.9.1
      * ------------------------------------------------------------------------
      *      Converts a pathname to an absolute pathname. Relative paths are
      *      referenced from the current working directory of the process unless
      *      _dir_string_ is given, in which case it will be used as the
      *      starting point. If the given pathname starts with a ``+~+'' it is
      *      NOT expanded, it is treated as a normal directory name.
      *
      *         File.absolute_path("~oracle/bin")       #=> "<relative_path>/~oracle/bin"
      *
      * @param context
      * @param recv
      * @param args
      * @return
      */
     @JRubyMethod(required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject absolute_path(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, false);
     }
 
     @JRubyMethod(name = {"realdirpath"}, required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject realdirpath(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return expandPathInternal(context, recv, args, false);
     }
 
     @JRubyMethod(name = {"realpath"}, required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject realpath(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject file = expandPathInternal(context, recv, args, false);
         if (!RubyFileTest.exist_p(recv, file).isTrue()) {
             throw context.runtime.newErrnoENOENTError(file.toString());
         }
         return file;
     }
 
     /**
      * Returns true if path matches against pattern The pattern is not a regular expression;
      * instead it follows rules similar to shell filename globbing. It may contain the following
      * metacharacters:
      *   *:  Glob - match any sequence chars (re: .*).  If like begins with '.' then it doesn't.
      *   ?:  Matches a single char (re: .).
      *   [set]:  Matches a single char in a set (re: [...]).
      *
      */
     @JRubyMethod(name = {"fnmatch", "fnmatch?"}, required = 2, optional = 1, meta = true)
     public static IRubyObject fnmatch(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         int flags = args.length == 3 ? RubyNumeric.num2int(args[2]) : 0;
 
         ByteList pattern = args[0].convertToString().getByteList();
         ByteList path = get_path(context, args[1]).getByteList();
 
         if (org.jruby.util.Dir.fnmatch(pattern.getUnsafeBytes(), pattern.getBegin(), pattern.getBegin()+pattern.getRealSize(), path.getUnsafeBytes(), path.getBegin(), path.getBegin()+path.getRealSize(), flags) == 0) {
             return context.runtime.getTrue();
         }
         return context.runtime.getFalse();
     }
     
     @JRubyMethod(name = "ftype", required = 1, meta = true)
     public static IRubyObject ftype(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         return context.runtime.newFileStat(get_path(context, filename).getUnicodeValue(), true).ftype();
     }
     
     /*
      * Fixme:  This does not have exact same semantics as RubyArray.join, but they
      * probably could be consolidated (perhaps as join(args[], sep, doChomp)).
      */
     @JRubyMethod(rest = true, meta = true)
     public static RubyString join(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return join(context, recv, RubyArray.newArrayNoCopyLight(context.runtime, args));
     }
     
     @JRubyMethod(name = "lstat", required = 1, meta = true)
     public static IRubyObject lstat(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.runtime.newFileStat(f, true);
     }
 
     @JRubyMethod(name = "stat", required = 1, meta = true)
     public static IRubyObject stat(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.runtime.newFileStat(f, false);
     }
 
     @JRubyMethod(name = "atime", required = 1, meta = true)
     public static IRubyObject atime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.runtime.newFileStat(f, false).atime();
     }
 
     @JRubyMethod(name = "ctime", required = 1, meta = true)
     public static IRubyObject ctime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         String f = get_path(context, filename).getUnicodeValue();
         return context.runtime.newFileStat(f, false).ctime();
     }
 
     @JRubyMethod(required = 1, rest = true, meta = true)
     public static IRubyObject lchmod(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             JRubyFile file = file(args[i]);
             if (0 != runtime.getPosix().lchmod(file.toString(), (int)mode.getLongValue())) {
                 throw runtime.newErrnoFromLastPOSIXErrno();
             } else {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject lchown(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         int owner = !args[0].isNil() ? RubyNumeric.num2int(args[0]) : -1;
         int group = !args[1].isNil() ? RubyNumeric.num2int(args[1]) : -1;
         int count = 0;
 
         for (int i = 2; i < args.length; i++) {
             JRubyFile file = file(args[i]);
 
             if (0 != runtime.getPosix().lchown(file.toString(), owner, group)) {
                 throw runtime.newErrnoFromLastPOSIXErrno();
             } else {
                 count++;
             }
         }
 
         return runtime.newFixnum(count);
     }
 
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject link(ThreadContext context, IRubyObject recv, IRubyObject from, IRubyObject to) {
         Ruby runtime = context.runtime;
         String fromStr = file(from).toString();
         String toStr = file(to).toString();
 
         int ret = runtime.getPosix().link(fromStr, toStr);
         if (ret != 0) {
             if (runtime.getPosix().isNative()) {
                 throw runtime.newErrnoFromInt(runtime.getPosix().errno(), String.format("(%s, %s)", fromStr, toStr));
             } else {
                 // In most cases, when there is an error during the call,
                 // the POSIX handler throws an exception, but not in case
                 // with pure Java POSIX layer (when native support is disabled),
                 // so we deal with it like this:
                 throw runtime.newErrnoEEXISTError(fromStr + " or " + toStr);
             }
         }
         return runtime.newFixnum(ret);
     }
 
     @JRubyMethod(name = "mtime", required = 1, meta = true)
     public static IRubyObject mtime(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         return context.runtime.newFileStat(get_path(context, filename).getUnicodeValue(), false).mtime();
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject rename(ThreadContext context, IRubyObject recv, IRubyObject oldName, IRubyObject newName) {
         Ruby runtime = context.runtime;
         RubyString oldNameString = RubyString.stringValue(oldName);
         RubyString newNameString = RubyString.stringValue(newName);
 
         String newNameJavaString = newNameString.getUnicodeValue();
         String oldNameJavaString = oldNameString.getUnicodeValue();
         JRubyFile oldFile = JRubyFile.create(runtime.getCurrentDirectory(), oldNameJavaString);
         JRubyFile newFile = JRubyFile.create(runtime.getCurrentDirectory(), newNameJavaString);
         
         if (!oldFile.exists() || !newFile.getParentFile().exists()) {
             throw runtime.newErrnoENOENTError(oldNameJavaString + " or " + newNameJavaString);
         }
 
         JRubyFile dest = JRubyFile.create(runtime.getCurrentDirectory(), newNameJavaString);
 
         if (oldFile.renameTo(dest)) {  // rename is successful
             return RubyFixnum.zero(runtime);
         }
 
         // rename via Java API call wasn't successful, let's try some tricks, similar to MRI 
 
         if (newFile.exists()) {
             runtime.getPosix().chmod(newNameJavaString, 0666);
             newFile.delete();
         }
 
         if (oldFile.renameTo(dest)) { // try to rename one more time
             return RubyFixnum.zero(runtime);
         }
 
         throw runtime.newErrnoEACCESError(oldNameJavaString + " or " + newNameJavaString);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static RubyArray split(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyString filename = get_path(context, arg);
 
         return context.runtime.newArray(dirname(context, recv, filename),
                 basename(context, recv, new IRubyObject[]{filename}));
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject symlink(ThreadContext context, IRubyObject recv, IRubyObject from, IRubyObject to) {
         Ruby runtime = context.runtime;
         RubyString fromStr = get_path(context, from);
         RubyString toStr = get_path(context, to);
         String tovalue = toStr.getUnicodeValue();
         tovalue = JRubyFile.create(runtime.getCurrentDirectory(), tovalue).getAbsolutePath();
         try {
             if (runtime.getPosix().symlink(fromStr.getUnicodeValue(), tovalue) == -1) {
                 if (runtime.getPosix().isNative()) {
                     throw runtime.newErrnoFromInt(runtime.getPosix().errno(), String.format("(%s, %s)", fromStr, toStr));
                 } else {
                     throw runtime.newErrnoEEXISTError(String.format("(%s, %s)", fromStr, toStr));
                 }
             }
         } catch (java.lang.UnsatisfiedLinkError ule) {
             throw runtime.newNotImplementedError("symlink() function is unimplemented on this machine");
         }
         
         return RubyFixnum.zero(runtime);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject readlink(ThreadContext context, IRubyObject recv, IRubyObject path) {
         Ruby runtime = context.runtime;
         JRubyFile link = file(path);
         
         try {
             String realPath = runtime.getPosix().readlink(link.toString());
         
             if (!RubyFileTest.exist_p(recv, path).isTrue()) {
                 throw runtime.newErrnoENOENTError(path.toString());
             }
         
             if (!RubyFileTest.symlink_p(recv, path).isTrue()) {
                 // Can not check earlier, File.exist? might return false yet the symlink be there
                 if (!RubyFileTest.exist_p(recv, path).isTrue()) {
                     throw runtime.newErrnoENOENTError(path.toString());
                 }
                 throw runtime.newErrnoEINVALError(path.toString());
             }
         
             if (realPath == null) {
                 throw runtime.newErrnoFromLastPOSIXErrno();
             }
 
             return runtime.newString(realPath);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     // Can we produce IOError which bypasses a close?
     @JRubyMethod(required = 2, meta = true, compat = RUBY1_8)
     public static IRubyObject truncate(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {        
         return truncateCommon(context, recv, arg1, arg2);
     }
 
     @JRubyMethod(name = "truncate", required = 2, meta = true, compat = RUBY1_9)
     public static IRubyObject truncate19(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return truncateCommon(context, recv, get_path(context, arg1), arg2);
     }
 
     @JRubyMethod(meta = true, optional = 1)
     public static IRubyObject umask(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         int oldMask = 0;
         if (args.length == 0) {
             oldMask = getUmaskSafe( runtime );
         } else if (args.length == 1) {
             int newMask = (int) args[0].convertToInteger().getLongValue();
             synchronized (_umaskLock) {
                 oldMask = runtime.getPosix().umask(newMask);
                 _cachedUmask = newMask;
             }
         } else {
             runtime.newArgumentError("wrong number of arguments");
         }
         
         return runtime.newFixnum(oldMask);
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject utime(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         long[] atimeval = null;
         long[] mtimeval = null;
 
         if (args[0] != runtime.getNil() || args[1] != runtime.getNil()) {
             atimeval = extractTimeval(runtime, args[0]);
             mtimeval = extractTimeval(runtime, args[1]);
         }
 
         for (int i = 2, j = args.length; i < j; i++) {
             RubyString filename = get_path(context, args[i]);
             
             JRubyFile fileToTouch = JRubyFile.create(runtime.getCurrentDirectory(),filename.getUnicodeValue());
             
             if (!fileToTouch.exists()) {
                 throw runtime.newErrnoENOENTError(filename.toString());
             }
 
             int result = runtime.getPosix().utimes(fileToTouch.getAbsolutePath(), atimeval, mtimeval);
             if (result == -1) {
                 throw runtime.newErrnoFromInt(runtime.getPosix().errno());
             }
         }
         
         return runtime.newFixnum(args.length - 2);
     }
     
     @JRubyMethod(name = {"unlink", "delete"}, rest = true, meta = true)
     public static IRubyObject unlink(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
          
         for (int i = 0; i < args.length; i++) {
             RubyString filename = get_path(context, args[i]);
             JRubyFile lToDelete = JRubyFile.create(runtime.getCurrentDirectory(), filename.getUnicodeValue());
             
             boolean isSymlink = RubyFileTest.symlink_p(recv, filename).isTrue();
             // Broken symlinks considered by exists() as non-existing,
             // so we need to check for symlinks explicitly.
             if (!lToDelete.exists() && !isSymlink) {
                 throw runtime.newErrnoENOENTError(filename.getUnicodeValue());
             }
 
             if (lToDelete.isDirectory() && !isSymlink) {
                 throw runtime.newErrnoEPERMError(filename.getUnicodeValue());
             }
 
             if (!lToDelete.delete()) {
                 throw runtime.newErrnoEACCESError(filename.getUnicodeValue());
             }
         }
         
         return runtime.newFixnum(args.length);
     }
 
     @JRubyMethod(name = "size", compat = RUBY1_9)
     public IRubyObject size(ThreadContext context) {
         Ruby runtime = context.runtime;
         if ((openFile.getMode() & OpenFile.WRITABLE) != 0) {
             flush();
         }
 
         try {
             FileStat stat = runtime.getPosix().fstat(
                     getOpenFileChecked().getMainStreamSafe().getDescriptor().getFileDescriptor());
             if (stat == null) {
                 throw runtime.newErrnoEACCESError(path);
             }
 
             return runtime.newFixnum(stat.st_size());
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         }
     }
 
     public String getPath() {
         return path;
     }
 
     @Override
     public Encoding getEncoding() {
         return null;
     }
 
     @Override
     public void setEncoding(Encoding encoding) {
         // :)
     }
 
     // mri: rb_open_file + rb_scan_open_args
     private IRubyObject openFile19(ThreadContext context, IRubyObject args[]) {
         Ruby runtime = context.runtime;
         RubyString filename = get_path(context, args[0]);
diff --git a/src/org/jruby/RubyFixnum.java b/src/org/jruby/RubyFixnum.java
index afef0b2ea2..766dc21173 100644
--- a/src/org/jruby/RubyFixnum.java
+++ b/src/org/jruby/RubyFixnum.java
@@ -1,1080 +1,1075 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Antti Karanta <antti.karanta@napa.fi>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.math.BigInteger;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.ConvertBytes;
 import org.jruby.util.Numeric;
 import org.jruby.util.TypeCoercer;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.Arity;
 
 /** 
  * Implementation of the Fixnum class.
  */
 @JRubyClass(name="Fixnum", parent="Integer", include="Precision")
 public class RubyFixnum extends RubyInteger {
     
     public static RubyClass createFixnumClass(Ruby runtime) {
         RubyClass fixnum = runtime.defineClass("Fixnum", runtime.getInteger(),
                 ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setFixnum(fixnum);
 
         fixnum.index = ClassIndex.FIXNUM;
         fixnum.setReifiedClass(RubyFixnum.class);
         
-        fixnum.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyFixnum;
-            }
-        };
+        fixnum.kindOf = new RubyModule.JavaClassKindOf(RubyFixnum.class);
 
         if (!runtime.is1_9()) {
             fixnum.includeModule(runtime.getPrecision());
         }
 
         fixnum.defineAnnotatedMethods(RubyFixnum.class);
         
         for (int i = 0; i < runtime.fixnumCache.length; i++) {
             runtime.fixnumCache[i] = new RubyFixnum(fixnum, i - CACHE_OFFSET);
         }
 
         return fixnum;
     }
     
     private final long value;
     private static final int BIT_SIZE = 64;
     public static final long SIGN_BIT = (1L << (BIT_SIZE - 1));
     public static final long MAX = (1L<<(BIT_SIZE - 1)) - 1;
     public static final long MIN = -1 * MAX - 1;
     public static final long MAX_MARSHAL_FIXNUM = (1L << 30) - 1; // 0x3fff_ffff
     public static final long MIN_MARSHAL_FIXNUM = - (1L << 30);   // -0x4000_0000
     public static final int CACHE_OFFSET = 256;
 
     private static IRubyObject fixCoerce(IRubyObject x) {
         do {
             x = x.convertToInteger();
         } while (!(x instanceof RubyFixnum) && !(x instanceof RubyBignum));
         return x;
     }
     
     private static IRubyObject bitCoerce(IRubyObject x) {
         do {
             if (x instanceof RubyFloat)
                 throw x.getRuntime().newTypeError("can't convert Float to Integer");
             x = x.convertToInteger("to_int");
         } while (!(x instanceof RubyFixnum) && !(x instanceof RubyBignum));
         return x;
     }
     
     public RubyFixnum(Ruby runtime) {
         this(runtime, 0);
     }
 
     public RubyFixnum(Ruby runtime, long value) {
         super(runtime.getFixnum());
         this.value = value;
     }
     
     private RubyFixnum(RubyClass klazz, long value) {
         super(klazz);
         this.value = value;
     }
     
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.FIXNUM;
     }
     
     /** 
      * short circuit for Fixnum key comparison
      */
     @Override
     public final boolean eql(IRubyObject other) {
         return other instanceof RubyFixnum && value == ((RubyFixnum)other).value;
     }
 
     @Override
     public IRubyObject equal_p(ThreadContext context, IRubyObject obj) {
         return equal_p19(context, obj);
     }
 
     @Override
     public IRubyObject equal_p19(ThreadContext context, IRubyObject obj) {
         return context.runtime.newBoolean(this == obj || eql(obj));
     }
     
     @Override
     public boolean isImmediate() {
     	return true;
     }
     
     @Override
     public RubyClass getSingletonClass() {
         throw getRuntime().newTypeError("can't define singleton");
     }
 
     @Override
     public Class<?> getJavaClass() {
         return long.class;
     }
 
     @Override
     public double getDoubleValue() {
         return value;
     }
 
     @Override
     public long getLongValue() {
         return value;
     }
 
     @Override
     public BigInteger getBigIntegerValue() {
         return BigInteger.valueOf(value);
     }
 
     public static RubyFixnum newFixnum(Ruby runtime, long value) {
         if (isInCacheRange(value)) {
             return runtime.fixnumCache[(int) value + CACHE_OFFSET];
         }
         return new RubyFixnum(runtime, value);
     }
     
     private static boolean isInCacheRange(long value) {
         return value <= CACHE_OFFSET - 1 && value >= -CACHE_OFFSET;
     }
 
     public RubyFixnum newFixnum(long newValue) {
         return newFixnum(getRuntime(), newValue);
     }
 
     public static RubyFixnum zero(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET];
     }
 
     public static RubyFixnum one(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET + 1];
     }
     
     public static RubyFixnum two(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET + 2];
     }
     
     public static RubyFixnum three(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET + 3];
     }
     
     public static RubyFixnum four(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET + 4];
     }
     
     public static RubyFixnum five(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET + 5];
     }
 
     public static RubyFixnum minus_one(Ruby runtime) {
         return runtime.fixnumCache[CACHE_OFFSET - 1];
     }
 
     @Override
     public RubyFixnum hash() {
         return newFixnum(hashCode());
     }
 
     @Override
     public final int hashCode() {
         return (int)(value ^ value >>> 32);
     }
 
     @Override
     public boolean equals(Object other) {
         if (other == this) {
             return true;
         }
         
         if (other instanceof RubyFixnum) { 
             RubyFixnum num = (RubyFixnum)other;
             
             if (num.value == value) {
                 return true;
             }
         }
         
         return false;
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
     @Override
     @JRubyMethod
     public IRubyObject times(ThreadContext context, Block block) {
         if (block.isGiven()) {
             Ruby runtime = context.runtime;
             long lvalue = this.value;
             boolean checkArity = block.type.checkArity;
             
             if (block.getBody().getArgumentType() == BlockBody.ZERO_ARGS ||
                     block.arity() == Arity.NO_ARGUMENTS) {
                 if (checkArity) {
                     // must pass arg
                     IRubyObject nil = runtime.getNil();
                     for (long i = 0; i < lvalue; i++) {
                         block.yieldSpecific(context, nil);
                     }
                 } else {
                     // no arg needed
                     for (long i = 0; i < lvalue; i++) {
                         block.yieldSpecific(context);
                     }
                 }
             } else {
                 for (long i = 0; i < lvalue; i++) {
                     block.yield(context, RubyFixnum.newFixnum(runtime, i));
                 }
             }
             return this;
         } else {
             return RubyEnumerator.enumeratorize(context.runtime, this, "times");
         }
     }
 
     /** fix_to_s
      * 
      */
     public RubyString to_s(IRubyObject[] args) {
         switch (args.length) {
         case 0: return to_s();
         case 1: return to_s(args[0]);
         default: throw getRuntime().newArgumentError(args.length, 1);
         }
     }
     
     @JRubyMethod
     @Override
     public RubyString to_s() {
         int base = 10;
         ByteList bl = ConvertBytes.longToByteList(value, base);
         if (getRuntime().is1_9()) bl.setEncoding(USASCIIEncoding.INSTANCE);
         return getRuntime().newString(bl);
     }
     
     @JRubyMethod
     public RubyString to_s(IRubyObject arg0) {
         int base = num2int(arg0);
         if (base < 2 || base > 36) {
             throw getRuntime().newArgumentError("illegal radix " + base);
         }
         ByteList bl = ConvertBytes.longToByteList(value, base);
         if (getRuntime().is1_9()) bl.setEncoding(USASCIIEncoding.INSTANCE);
         return getRuntime().newString(bl);
     }
 
     /** fix_id2name
      * 
      */
     @JRubyMethod
     public IRubyObject id2name() {
         RubySymbol symbol = RubySymbol.getSymbolLong(getRuntime(), value);
         
         if (symbol != null) return getRuntime().newString(symbol.asJavaString());
 
         return getRuntime().getNil();
     }
 
     /** fix_to_sym
      * 
      */
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject to_sym() {
         RubySymbol symbol = RubySymbol.getSymbolLong(getRuntime(), value);
         
         return symbol != null ? symbol : getRuntime().getNil(); 
     }
 
     /** fix_uminus
      * 
      */
     @JRubyMethod(name = "-@")
     public IRubyObject op_uminus() {
         if (value == MIN) { // a gotcha
             return RubyBignum.newBignum(getRuntime(), BigInteger.valueOf(value).negate());
         }
         return RubyFixnum.newFixnum(getRuntime(), -value);
     }
 
     /** fix_plus
      * 
      */
     @JRubyMethod(name = "+")
     public IRubyObject op_plus(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return addFixnum(context, (RubyFixnum)other);
         }
         return addOther(context, other);
     }
     
     public IRubyObject op_plus(ThreadContext context, long otherValue) {
         long result = value + otherValue;
         if (additionOverflowed(value, otherValue, result)) {
             return addAsBignum(context, otherValue);
         }
         return newFixnum(context.runtime, result);
     }
     
     public IRubyObject op_plus_one(ThreadContext context) {
         long result = value + 1;
         if (result == Long.MIN_VALUE) {
             return addAsBignum(context, 1);
         }
         return newFixnum(context.runtime, result);
     }
     
     public IRubyObject op_plus_two(ThreadContext context) {
         long result = value + 2;
     //- if (result == Long.MIN_VALUE + 1) {     //-code
         if (result < value) {                   //+code+patch; maybe use  if (result <= value) {
             return addAsBignum(context, 2);
         }
         return newFixnum(context.runtime, result);
     }
     
     private IRubyObject addFixnum(ThreadContext context, RubyFixnum other) {
         long otherValue = other.value;
         long result = value + otherValue;
         if (additionOverflowed(value, otherValue, result)) {
             return addAsBignum(context, other);
         }
         return newFixnum(context.runtime, result);
     }
     
     private static boolean additionOverflowed(long original, long other, long result) {
         return (~(original ^ other) & (original ^ result) & SIGN_BIT) != 0;
     }
     
     private static boolean subtractionOverflowed(long original, long other, long result) {
         return (~(original ^ ~other) & (original ^ result) & SIGN_BIT) != 0;
     }
     
     private IRubyObject addAsBignum(ThreadContext context, RubyFixnum other) {
         return RubyBignum.newBignum(context.runtime, value).op_plus(context, other);
     }
 
     private IRubyObject addAsBignum(ThreadContext context, long other) {
         return RubyBignum.newBignum(context.runtime, value).op_plus(context, other);
     }
     
     private IRubyObject addOther(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).op_plus(context, this);
         }
         if (other instanceof RubyFloat) {
             return context.runtime.newFloat((double) value + ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin(context, "+", other);
     }
 
     /** fix_minus
      * 
      */
     @JRubyMethod(name = "-")
     public IRubyObject op_minus(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return subtractFixnum(context, (RubyFixnum)other);
         }
         return subtractOther(context, other);
     }
 
     public IRubyObject op_minus(ThreadContext context, long otherValue) {
         long result = value - otherValue;
         if (subtractionOverflowed(value, otherValue, result)) {
             return subtractAsBignum(context, otherValue);
         }
         return newFixnum(context.runtime, result);
     }
 
     public IRubyObject op_minus_one(ThreadContext context) {
         long result = value - 1;
         if (result == Long.MAX_VALUE) {
             return subtractAsBignum(context, 1);
         }
         return newFixnum(context.runtime, result);
     }
 
     public IRubyObject op_minus_two(ThreadContext context) {
         long result = value - 2;
     //- if (result == Long.MAX_VALUE - 1) {     //-code
         if (value < result) {                   //+code+patch; maybe use  if (value <= result) {
             return subtractAsBignum(context, 2);
         }
         return newFixnum(context.runtime, result);
     }
 
     private IRubyObject subtractFixnum(ThreadContext context, RubyFixnum other) {
         long otherValue = other.value;
         long result = value - otherValue;
         if (subtractionOverflowed(value, otherValue, result)) {
             return subtractAsBignum(context, other);
         }
         return newFixnum(context.runtime, result);
     }
     
     private IRubyObject subtractAsBignum(ThreadContext context, RubyFixnum other) {
         return RubyBignum.newBignum(context.runtime, value).op_minus(context, other);
     }
 
     private IRubyObject subtractAsBignum(ThreadContext context, long other) {
         return RubyBignum.newBignum(context.runtime, value).op_minus(context, other);
     }
     
     private IRubyObject subtractOther(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyBignum) {
             return RubyBignum.newBignum(context.runtime, value).op_minus(context, other);
         } else if (other instanceof RubyFloat) {
             return context.runtime.newFloat((double) value - ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin(context, "-", other);
     }
 
     /** fix_mul
      * 
      */
     @JRubyMethod(name = "*")
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return multiplyFixnum(context, other);
         } else {
             return multiplyOther(context, other);
         }
     }
 
     private IRubyObject multiplyFixnum(ThreadContext context, IRubyObject other) {
         return op_mul(context, ((RubyFixnum) other).value);
     }
 
     private IRubyObject multiplyOther(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).op_mul(context, this);
         } else if (other instanceof RubyFloat) {
             return runtime.newFloat((double) value * ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin(context, "*", other);
     }
 
     public IRubyObject op_mul(ThreadContext context, long otherValue) {
         // See JRUBY-6612 for reasons for these different cases.
         // The problem is that these Java long calculations overflow:
         //   value == -1; otherValue == Long.MIN_VALUE;
         //   result = value * othervalue;  #=> Long.MIN_VALUE (overflow)
         //   result / value  #=>  Long.MIN_VALUE (overflow) == otherValue
         Ruby runtime = context.runtime;
         if (value == 0) {
             return RubyFixnum.zero(runtime);
         }
         if (value == -1) {
             if (otherValue != Long.MIN_VALUE) {
                 return newFixnum(runtime, -otherValue);
             }
         } else {
             long result = value * otherValue;
             if (result / value == otherValue) {
                 return newFixnum(runtime, result);
             }
         }
         // if here (value * otherValue) overflows long, so must return Bignum
         return RubyBignum.newBignum(runtime, value).op_mul(context, otherValue);
     }
 
     /** fix_div
      * here is terrible MRI gotcha:
      * 1.div 3.0 -> 0
      * 1 / 3.0   -> 0.3333333333333333
      * 
      * MRI is also able to do it in one place by looking at current frame in rb_num_coerce_bin:
      * rb_funcall(x, ruby_frame->orig_func, 1, y);
      * 
      * also note that RubyFloat doesn't override Numeric.div
      */
     @JRubyMethod(name = "div")
     public IRubyObject div_div(ThreadContext context, IRubyObject other) {
         if (context.is19) checkZeroDivisionError(context, other);
         return idiv(context, other, "div");
     }
     	
     @JRubyMethod(name = "/")
     public IRubyObject op_div(ThreadContext context, IRubyObject other) {
         return idiv(context, other, "/");
     }
 
     public IRubyObject op_div(ThreadContext context, long other) {
         return idiv(context, other, "/");
     }
 
     @JRubyMethod(name = {"odd?"}, compat = RUBY1_9)
     public RubyBoolean odd_p(ThreadContext context) {
         if(value%2 != 0) {
             return context.runtime.getTrue();
         }
         return context.runtime.getFalse();
     }
 
     @JRubyMethod(name = {"even?"}, compat = RUBY1_9)
     public RubyBoolean even_p(ThreadContext context) {
         if(value%2 == 0) {
             return context.runtime.getTrue();
         }
         return context.runtime.getFalse();
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject pred(ThreadContext context) {
         return context.runtime.newFixnum(value-1);
     }
 
     public IRubyObject idiv(ThreadContext context, IRubyObject other, String method) {
         if (other instanceof RubyFixnum) {
             return idivLong(context, value, ((RubyFixnum) other).value);
         } 
         return coerceBin(context, method, other);
     }
 
     public IRubyObject idiv(ThreadContext context, long y, String method) {
         long x = value;
 
         return idivLong(context, x, y);
     }
 
     private IRubyObject idivLong(ThreadContext context, long x, long y) {
         Ruby runtime = context.runtime;
         if (y == 0) {
             throw runtime.newZeroDivisionError();
         }
         long result;
         if (y > 0) {
             if (x >= 0) {
                 result = x / y;          // x >= 0, y > 0;
             } else {
                 result = (x + 1) / y - 1;  // x < 0, y > 0;  // OOPS "=" was omitted
             }
         } else if (x > 0) {
             result = (x - 1) / y - 1;    // x > 0, y < 0;
         } else if (y == -1) {
             if (x == MIN) {
                 return RubyBignum.newBignum(runtime, BigInteger.valueOf(x).negate());
             }
             result = -x;
         } else {
             result = x / y;  // x <= 0, y < 0;
         }
         return runtime.newFixnum(result);
     }
         
     /** fix_mod
      * 
      */
     @JRubyMethod(name = {"%", "modulo"})
     public IRubyObject op_mod(ThreadContext context, IRubyObject other) {
         if (context.runtime.is1_9()) checkZeroDivisionError(context, other);
         if (other instanceof RubyFixnum) {
             return moduloFixnum(context, (RubyFixnum)other);
         }
         return coerceBin(context, "%", other);
     }
     
     public IRubyObject op_mod(ThreadContext context, long other) {
         return moduloFixnum(context, other);
     }
 
     private IRubyObject moduloFixnum(ThreadContext context, RubyFixnum other) {
         return moduloFixnum(context, other.value);
     }
 
     private IRubyObject moduloFixnum(ThreadContext context, long other) {
         // Java / and % are not the same as ruby
         long x = value;
         long y = other;
         if (y == 0) {
             throw context.runtime.newZeroDivisionError();
         }
         long mod = x % y;
         if (mod < 0 && y > 0 || mod > 0 && y < 0) {
             mod += y;
         }
         return context.runtime.newFixnum(mod);
     }
                 
     /** fix_divmod
      * 
      */
     @JRubyMethod(name = "divmod")
     @Override
     public IRubyObject divmod(ThreadContext context, IRubyObject other) {
         if (context.runtime.is1_9()) checkZeroDivisionError(context, other);
         if (other instanceof RubyFixnum) {
             return divmodFixnum(context, other);
         }
         return coerceBin(context, "divmod", other);
     }
 
     private IRubyObject divmodFixnum(ThreadContext context, IRubyObject other) {
         long x = value;
         long y = ((RubyFixnum) other).value;
         final Ruby runtime = context.runtime;
         if (y == 0) {
             throw runtime.newZeroDivisionError();
         }
 
         long mod;
         IRubyObject integerDiv;
         if (y == -1) {
             if (x == MIN) {
                 integerDiv = RubyBignum.newBignum(runtime, BigInteger.valueOf(x).negate());
             } else {
                 integerDiv = RubyFixnum.newFixnum(runtime, -x);
             }
             mod = 0;
         } else {
             long div = x / y;
             // Next line avoids using the slow: mod = x % y,
             // and I believe there is no possibility of integer overflow.
             mod = x - y * div;
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 div -= 1; // horrible sudden thought: might this overflow? probably not?
                 mod += y;
             }
             integerDiv = RubyFixnum.newFixnum(runtime, div);
         }
         IRubyObject fixMod = RubyFixnum.newFixnum(runtime, mod);
         return RubyArray.newArray(runtime, integerDiv, fixMod);
     }
     	
     /** fix_quo
      * 
      */
     @JRubyMethod(name = "quo", compat = RUBY1_8)
     @Override
     public IRubyObject quo(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyFloat.newFloat(context.runtime, (double) value / (double) ((RubyFixnum) other).value);
         } else if (other instanceof RubyBignum) {
             return RubyFloat.newFloat(context.runtime, (double) value / (double) ((RubyBignum) other).getDoubleValue());
         }
         return coerceBin(context, "quo", other);
     }
 
     /** fix_pow 
      * 
      */
     @JRubyMethod(name = "**")
     public IRubyObject op_pow(ThreadContext context, IRubyObject other) {
         if (context.is19) return op_pow_19(context, other);
         if(other instanceof RubyFixnum) {
             return powerFixnum(context, ((RubyFixnum)other).value);
         } else {
             return powerOther(context, other);
         }
     }
 
     public IRubyObject op_pow(ThreadContext context, long other) {
         // FIXME this needs to do the right thing for 1.9 mode before we can use it
         if (context.is19) throw context.runtime.newRuntimeError("bug: using direct op_pow(long) in 1.8 mode");
         return powerFixnum(context, other);
     }
 
     private IRubyObject powerFixnum(ThreadContext context, long other) {
         Ruby runtime = context.runtime;
         if (other == 0) {
             return RubyFixnum.one(runtime);
         }
         if (other == 1) {
             return this;
         }
         if (other > 0) {
             return RubyBignum.newBignum(runtime, value).op_pow(context, other);
         }
         return RubyFloat.newFloat(runtime, Math.pow(value, other));
     }
 
     private IRubyObject powerOther(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(runtime, Math.pow(value, ((RubyFloat) other)
                     .getDoubleValue()));
         }
         return coerceBin(context, "**", other);
     }
 
     public IRubyObject op_pow_19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyNumeric) {
             double d_other = ((RubyNumeric) other).getDoubleValue();
             if (value < 0 && (d_other != Math.round(d_other))) {
                 return RubyComplex.newComplexRaw(getRuntime(), this).callMethod(context, "**", other);
             }
             if (other instanceof RubyFixnum) {
                 return powerFixnum19(context, other);
             }
         }
         return powerOther19(context, other);
     }
 
     private IRubyObject powerOther19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         long a = value;
         if (other instanceof RubyBignum) {
             if (other.callMethod(context, "<", RubyFixnum.zero(runtime)).isTrue()) {
                 return RubyRational.newRationalRaw(runtime, this).callMethod(context, "**", other);
             }
             if (a == 0) return RubyFixnum.zero(runtime);
             if (a == 1) return RubyFixnum.one(runtime);
             if (a == -1) {
                 return ((RubyBignum)other).even_p(context).isTrue() ? RubyFixnum.one(runtime) : RubyFixnum.minus_one(runtime);
             }
             RubyBignum.newBignum(runtime, RubyBignum.fix2big(this)).op_pow(context, other);
         } else if (other instanceof RubyFloat) {
             double b = ((RubyFloat)other).getValue();
             if (b == 0.0 || a == 1) return runtime.newFloat(1.0);
             if (a == 0) return runtime.newFloat(b < 0 ? 1.0 / 0.0 : 0.0);
             return RubyFloat.newFloat(runtime, Math.pow(a, b));
         }
         return coerceBin(context, "**", other);
     }
 
     private IRubyObject powerFixnum19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         long a = value;
         long b = ((RubyFixnum) other).value;
         if (b < 0) {
             return RubyRational.newRationalRaw(runtime, this).callMethod(context, "**", other);
         }
         if (b == 0) {
             return RubyFixnum.one(runtime);
         }
         if (b == 1) {
             return this;
         }
         if (a == 0) {
             return b > 0 ? RubyFixnum.zero(runtime) : RubyNumeric.dbl2num(runtime, 1.0 / 0.0);
         }
         if (a == 1) {
             return RubyFixnum.one(runtime);
         }
         if (a == -1) {
             return b % 2 == 0 ? RubyFixnum.one(runtime) : RubyFixnum.minus_one(runtime);
         }
         return Numeric.int_pow(context, a, b);
     }
 
     /** fix_abs
      * 
      */
     @JRubyMethod
     @Override
     public IRubyObject abs(ThreadContext context) {
         if (value < 0) {
             // A gotcha for Long.MIN_VALUE: value = -value
             if (value == Long.MIN_VALUE) {
                 return RubyBignum.newBignum(
                         context.runtime, BigInteger.valueOf(value).negate());
             }
             return RubyFixnum.newFixnum(context.runtime, -value);
         }
         return this;
     }
 
     /** fix_abs/1.9
      * 
      */
     @JRubyMethod(name = "magnitude", compat = RUBY1_9)
     @Override
     public IRubyObject magnitude(ThreadContext context) {
         return abs(context);
     }
 
     /** fix_equal
      * 
      */
     @JRubyMethod(name = "==")
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) return op_equal(context, ((RubyFixnum) other).value);
         return context.is19 ?
                 op_equalOther(context, other) :
                 super.op_num_equal(context, other);
     }
 
     public IRubyObject op_equal(ThreadContext context, long other) {
         return RubyBoolean.newBoolean(context.runtime, value == other);
     }
 
     public boolean op_equal_boolean(ThreadContext context, long other) {
         return value == other;
     }
 
     public boolean fastEqual(RubyFixnum other) {
         return value == other.value;
     }
 
     private IRubyObject op_equalOther(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyBignum) {
             return RubyBoolean.newBoolean(context.runtime,
                     BigInteger.valueOf(value).compareTo(((RubyBignum) other).getValue()) == 0);
         }
         if (other instanceof RubyFloat) {
             return RubyBoolean.newBoolean(context.runtime, (double) value == ((RubyFloat) other).getDoubleValue());
         }
         return super.op_num_equal(context, other);
     }
 
     @Override
     public final int compareTo(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum)other).value;
             return value == otherValue ? 0 : value > otherValue ? 1 : -1; 
         }
         return compareToOther(other);
     }
 
     private int compareToOther(IRubyObject other) {
         if (other instanceof RubyBignum) return BigInteger.valueOf(value).compareTo(((RubyBignum)other).getValue());
         if (other instanceof RubyFloat) return Double.compare((double)value, ((RubyFloat)other).getDoubleValue());
         return (int)coerceCmp(getRuntime().getCurrentContext(), "<=>", other).convertToInteger().getLongValue();
     }
 
     /** fix_cmp
      * 
      */
     @JRubyMethod(name = "<=>")
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) return op_cmp(context, ((RubyFixnum)other).value);
         return context.is19 ?
                 compareOther(context, other) :
                 coerceCmp(context, "<=>", other);
     }
 
     public IRubyObject op_cmp(ThreadContext context, long other) {
         Ruby runtime = context.runtime;
         return value == other ? RubyFixnum.zero(runtime) : value > other ?
                 RubyFixnum.one(runtime) : RubyFixnum.minus_one(runtime);
     }
 
     private IRubyObject compareOther(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyBignum) {
             return newFixnum(context.runtime, BigInteger.valueOf(value).compareTo(((RubyBignum)other).getValue()));
         }
         if (other instanceof RubyFloat) {
             return dbl_cmp(context.runtime, (double)value, ((RubyFloat)other).getDoubleValue());
         }
         return coerceCmp(context, "<=>", other);
     }
 
     /** fix_gt
      * 
      */
     @JRubyMethod(name = ">")
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(context.runtime, value > ((RubyFixnum) other).value);
         }
         return context.is19 ?
                 op_gtOther(context, other) :
                 coerceRelOp(context, ">", other);
     }
 
     public IRubyObject op_gt(ThreadContext context, long other) {
         return RubyBoolean.newBoolean(context.runtime, value > other);
     }
 
     public boolean op_gt_boolean(ThreadContext context, long other) {
         return value > other;
     }
 
     private IRubyObject op_gtOther(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyBignum) {
             return RubyBoolean.newBoolean(context.runtime,
                     BigInteger.valueOf(value).compareTo(((RubyBignum) other).getValue()) > 0);
         }
         if (other instanceof RubyFloat) {
             return RubyBoolean.newBoolean(context.runtime, (double) value > ((RubyFloat) other).getDoubleValue());
         }
         return coerceRelOp(context, ">", other);
     }
 
     /** fix_ge
      * 
      */
     @JRubyMethod(name = ">=")
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(context.runtime, value >= ((RubyFixnum) other).value);
         }
         return context.is19 ?
                 op_geOther(context, other) :
                 coerceRelOp(context, ">=", other);
     }
 
     public IRubyObject op_ge(ThreadContext context, long other) {
         return RubyBoolean.newBoolean(context.runtime, value >= other);
     }
 
     public boolean op_ge_boolean(ThreadContext context, long other) {
         return value >= other;
     }
 
     private IRubyObject op_geOther(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyBignum) {
             return RubyBoolean.newBoolean(context.runtime,
                     BigInteger.valueOf(value).compareTo(((RubyBignum) other).getValue()) >= 0);
         }
         if (other instanceof RubyFloat) {
             return RubyBoolean.newBoolean(context.runtime, (double) value >= ((RubyFloat) other).getDoubleValue());
         }
         return coerceRelOp(context, ">=", other);
     }
 
     /** fix_lt
      * 
      */
     @JRubyMethod(name = "<")
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return op_lt(context, ((RubyFixnum)other).value);
         }
         return op_ltOther(context, other);
     }
 
     public IRubyObject op_lt(ThreadContext context, long other) {
         return RubyBoolean.newBoolean(context.runtime, value < other);
     }
 
     public boolean op_lt_boolean(ThreadContext context, long other) {
         return value < other;
     }
 
     private IRubyObject op_ltOther(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyBignum) {
             return RubyBoolean.newBoolean(context.runtime,
                     BigInteger.valueOf(value).compareTo(((RubyBignum) other).getValue()) < 0);
         }
         if (other instanceof RubyFloat) {
             return RubyBoolean.newBoolean(context.runtime, (double) value < ((RubyFloat) other).getDoubleValue());
         }
         return coerceRelOp(context, "<", other);
     }
 
     /** fix_le
      * 
      */
     @JRubyMethod(name = "<=")
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(context.runtime, value <= ((RubyFixnum) other).value);
         }
         return context.is19 ?
                 op_leOther(context, other) :
                 coerceRelOp(context, "<=", other);
     }
 
     public IRubyObject op_le(ThreadContext context, long other) {
         return RubyBoolean.newBoolean(context.runtime, value <= other);
     }
 
     public boolean op_le_boolean(ThreadContext context, long other) {
         return value <= other;
     }
 
     private IRubyObject op_leOther(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyBignum) {
             return RubyBoolean.newBoolean(context.runtime,
                     BigInteger.valueOf(value).compareTo(((RubyBignum) other).getValue()) <= 0);
         }
         if (other instanceof RubyFloat) {
             return RubyBoolean.newBoolean(context.runtime, (double) value <= ((RubyFloat) other).getDoubleValue());
         }
         return coerceRelOp(context, "<=", other);
     }
 
     /** fix_rev
      * 
      */
     @JRubyMethod(name = "~")
     public IRubyObject op_neg() {
         return newFixnum(~value);
     }
     	
     /** fix_and
      * 
      */
     @JRubyMethod(name = "&")
     public IRubyObject op_and(ThreadContext context, IRubyObject other) {
         if (context.is19) return op_and19(context, other);
         return op_and18(context, other);
     }
 
     private IRubyObject op_and18(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum || (other = fixCoerce(other)) instanceof RubyFixnum) {
             return newFixnum(context.runtime, value & ((RubyFixnum) other).value);
         }
         return ((RubyBignum) other).op_and(context, this);
     }
     
     public IRubyObject op_and(ThreadContext context, long other) {
         return newFixnum(context.runtime, value & other);
     }
 
     private IRubyObject op_and19(ThreadContext context, IRubyObject other) {
         if (!((other = bitCoerce(other)) instanceof RubyFixnum)) {
             return ((RubyBignum) other).op_and(context, this);
         }
diff --git a/src/org/jruby/RubyFloat.java b/src/org/jruby/RubyFloat.java
index a6ccfa4fb1..0f0211eee0 100644
--- a/src/org/jruby/RubyFloat.java
+++ b/src/org/jruby/RubyFloat.java
@@ -1,987 +1,982 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Don Schwartz <schwardo@users.sourceforge.net>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2008 Joseph LaFata <joe@quibb.org>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import static org.jruby.util.Numeric.f_abs;
 import static org.jruby.util.Numeric.f_add;
 import static org.jruby.util.Numeric.f_expt;
 import static org.jruby.util.Numeric.f_lshift;
 import static org.jruby.util.Numeric.f_mul;
 import static org.jruby.util.Numeric.f_negate;
 import static org.jruby.util.Numeric.f_negative_p;
 import static org.jruby.util.Numeric.f_sub;
 import static org.jruby.util.Numeric.f_to_r;
 import static org.jruby.util.Numeric.f_zero_p;
 import static org.jruby.util.Numeric.frexp;
 import static org.jruby.util.Numeric.ldexp;
 import static org.jruby.util.Numeric.nurat_rationalize_internal;
 
 import java.text.DecimalFormat;
 import java.text.DecimalFormatSymbols;
 import java.util.Locale;
 
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.ConvertDouble;
 import org.jruby.util.Sprintf;
 
 import static org.jruby.runtime.Helpers.invokedynamic;
 import static org.jruby.runtime.invokedynamic.MethodNames.OP_EQUAL;
 
 /**
   * A representation of a float object
  */
 @JRubyClass(name="Float", parent="Numeric", include="Precision")
 public class RubyFloat extends RubyNumeric {
     public static final int ROUNDS = 1;
     public static final int RADIX = 2;
     public static final int MANT_DIG = 53;
     public static final int DIG = 15;
     public static final int MIN_EXP = -1021;
     public static final int MAX_EXP = 1024;
     public static final int MAX_10_EXP = 308;
     public static final int MIN_10_EXP = -307;
     public static final double EPSILON = 2.2204460492503131e-16;
     public static final double INFINITY = Double.POSITIVE_INFINITY;
     public static final double NAN = Double.NaN;
 
     public static RubyClass createFloatClass(Ruby runtime) {
         RubyClass floatc = runtime.defineClass("Float", runtime.getNumeric(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setFloat(floatc);
 
         floatc.index = ClassIndex.FLOAT;
         floatc.setReifiedClass(RubyFloat.class);
         
-        floatc.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyFloat;
-            }
-        };        
+        floatc.kindOf = new RubyModule.JavaClassKindOf(RubyFloat.class);
 
         floatc.getSingletonClass().undefineMethod("new");
 
         if (!runtime.is1_9()) {
             floatc.includeModule(runtime.getPrecision());
         }
 
         // Java Doubles are 64 bit long:            
         floatc.defineConstant("ROUNDS", RubyFixnum.newFixnum(runtime, ROUNDS));
         floatc.defineConstant("RADIX", RubyFixnum.newFixnum(runtime, RADIX));
         floatc.defineConstant("MANT_DIG", RubyFixnum.newFixnum(runtime, MANT_DIG));
         floatc.defineConstant("DIG", RubyFixnum.newFixnum(runtime, DIG));
         // Double.MAX_EXPONENT since Java 1.6
         floatc.defineConstant("MIN_EXP", RubyFixnum.newFixnum(runtime, MIN_EXP));
         // Double.MAX_EXPONENT since Java 1.6            
         floatc.defineConstant("MAX_EXP", RubyFixnum.newFixnum(runtime, MAX_EXP));
         floatc.defineConstant("MIN_10_EXP", RubyFixnum.newFixnum(runtime, MIN_10_EXP));
         floatc.defineConstant("MAX_10_EXP", RubyFixnum.newFixnum(runtime, MAX_10_EXP));
         floatc.defineConstant("MIN", RubyFloat.newFloat(runtime, Double.MIN_VALUE));
         floatc.defineConstant("MAX", RubyFloat.newFloat(runtime, Double.MAX_VALUE));
         floatc.defineConstant("EPSILON", RubyFloat.newFloat(runtime, EPSILON));
 
         if (runtime.is1_9()) {
             floatc.defineConstant("INFINITY", RubyFloat.newFloat(runtime, INFINITY));
             floatc.defineConstant("NAN", RubyFloat.newFloat(runtime, NAN));
         }
 
         floatc.defineAnnotatedMethods(RubyFloat.class);
 
         return floatc;
     }
 
     private final double value;
     
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.FLOAT;
     }
 
     public RubyFloat(Ruby runtime) {
         this(runtime, 0.0);
     }
 
     public RubyFloat(Ruby runtime, double value) {
         super(runtime.getFloat());
         this.value = value;
     }
 
     @Override
     public Class<?> getJavaClass() {
         return double.class;
     }
 
     /** Getter for property value.
      * @return Value of property value.
      */
     public double getValue() {
         return this.value;
     }
 
     @Override
     public double getDoubleValue() {
         return value;
     }
 
     @Override
     public long getLongValue() {
         return (long) value;
     }
 
     @Override
     public BigInteger getBigIntegerValue() {
         return BigInteger.valueOf((long)value);
     }
     
     @Override
     public RubyFloat convertToFloat() {
     	return this;
     }
 
     protected int compareValue(RubyNumeric other) {
         double otherVal = other.getDoubleValue();
         return getValue() > otherVal ? 1 : getValue() < otherVal ? -1 : 0;
     }
 
     public static RubyFloat newFloat(Ruby runtime, double value) {
         return new RubyFloat(runtime, value);
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** rb_flo_induced_from
      * 
      */
     @JRubyMethod(name = "induced_from", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject induced_from(ThreadContext context, IRubyObject recv, IRubyObject number) {
         if (number instanceof RubyFixnum || number instanceof RubyBignum || number instanceof RubyRational) {
             return number.callMethod(context, "to_f");
         } else if (number instanceof RubyFloat) {
             return number;
         }
         throw recv.getRuntime().newTypeError(
                 "failed to convert " + number.getMetaClass() + " into Float");
     }
 
     private final static DecimalFormat FORMAT = new DecimalFormat("##############0.0##############",
             new DecimalFormatSymbols(Locale.ENGLISH));
 
     /** flo_to_s
      * 
      */
     @JRubyMethod(name = "to_s")
     @Override
     public IRubyObject to_s() {
         Ruby runtime = getRuntime();
         if (Double.isInfinite(value)) return RubyString.newString(runtime, value < 0 ? "-Infinity" : "Infinity");
         if (Double.isNaN(value)) return RubyString.newString(runtime, "NaN");
 
         ByteList buf = new ByteList();
         // Under 1.9, use full-precision float formatting (JRUBY-4846).
         // Double-precision can represent around 16 decimal digits;
         // we use 20 to ensure full representation.
         if (runtime.is1_9()) {
             Sprintf.sprintf(buf, Locale.US, "%#.20g", this);
         } else {
             Sprintf.sprintf(buf, Locale.US, "%#.15g", this);
         }
         int e = buf.indexOf('e');
         if (e == -1) e = buf.getRealSize();
         ASCIIEncoding ascii = ASCIIEncoding.INSTANCE; 
 
         if (!ascii.isDigit(buf.get(e - 1))) {
             buf.setRealSize(0);
             Sprintf.sprintf(buf, Locale.US, "%#.14e", this);
             e = buf.indexOf('e');
             if (e == -1) e = buf.getRealSize();
         }
 
         int p = e;
         while (buf.get(p - 1) == '0' && ascii.isDigit(buf.get(p - 2))) p--;
         System.arraycopy(buf.getUnsafeBytes(), e, buf.getUnsafeBytes(), p, buf.getRealSize() - e);
         buf.setRealSize(p + buf.getRealSize() - e);
 
         if (getRuntime().is1_9()) buf.setEncoding(USASCIIEncoding.INSTANCE);
 
         return runtime.newString(buf);
     }
 
     /** flo_coerce
      * 
      */
     @JRubyMethod(name = "coerce", required = 1)
     @Override
     public IRubyObject coerce(IRubyObject other) {
         return getRuntime().newArray(RubyKernel.new_float(this, other), this);
     }
 
     /** flo_uminus
      * 
      */
     @JRubyMethod(name = "-@")
     public IRubyObject op_uminus() {
         return RubyFloat.newFloat(getRuntime(), -value);
     }
 
     /** flo_plus
      * 
      */
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyFloat.newFloat(getRuntime(), value + ((RubyNumeric) other).getDoubleValue());
         default:
             return coerceBin(context, "+", other);
         }
     }
 
     public IRubyObject op_plus(ThreadContext context, double other) {
         return RubyFloat.newFloat(getRuntime(), value + other);
     }
 
     /** flo_minus
      * 
      */
     @JRubyMethod(name = "-", required = 1)
     public IRubyObject op_minus(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyFloat.newFloat(getRuntime(), value - ((RubyNumeric) other).getDoubleValue());
         default:
             return coerceBin(context, "-", other);
         }
     }
 
     public IRubyObject op_minus(ThreadContext context, double other) {
         return RubyFloat.newFloat(getRuntime(), value - other);
     }
 
     /** flo_mul
      * 
      */
     @JRubyMethod(name = "*", required = 1)
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyFloat.newFloat(
                     getRuntime(), value * ((RubyNumeric) other).getDoubleValue());
         default:
             return coerceBin(context, "*", other);
         }
     }
 
     public IRubyObject op_mul(ThreadContext context, double other) {
         return RubyFloat.newFloat(
                 getRuntime(), value * other);
     }
     
     /** flo_div
      * 
      */
     @JRubyMethod(name = "/", required = 1)
     public IRubyObject op_fdiv(ThreadContext context, IRubyObject other) { // don't override Numeric#div !
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyFloat.newFloat(getRuntime(), value / ((RubyNumeric) other).getDoubleValue());
         default:
             return coerceBin(context, "/", other);
         }
     }
 
     public IRubyObject op_fdiv(ThreadContext context, double other) { // don't override Numeric#div !
         return RubyFloat.newFloat(getRuntime(), value / other);
     }
 
     /** flo_quo
     *
     */
     @JRubyMethod(name = "quo", compat = CompatVersion.RUBY1_9)
         public IRubyObject magnitude(ThreadContext context, IRubyObject other) {
         return callMethod(context, "/", other);
     }
 
     /** flo_mod
      * 
      */
     @JRubyMethod(name = {"%", "modulo"}, required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject op_mod(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double y = ((RubyNumeric) other).getDoubleValue();
             return op_mod(context, y);
         default:
             return coerceBin(context, "%", other);
         }
     }
 
     public IRubyObject op_mod(ThreadContext context, double other) {
         // Modelled after c ruby implementation (java /,% not same as ruby)
         double x = value;
 
         double mod = Math.IEEEremainder(x, other);
         if (other * mod < 0) {
             mod += other;
         }
 
         return RubyFloat.newFloat(getRuntime(), mod);
     }
 
     /** flo_mod
      * 
      */
     @JRubyMethod(name = {"%", "modulo"}, required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_mod19(ThreadContext context, IRubyObject other) {
         if (!other.isNil() && other instanceof RubyNumeric
             && ((RubyNumeric)other).getDoubleValue() == 0) {
             throw context.runtime.newZeroDivisionError();
         }
         return op_mod(context, other);
     }
 
     /** flo_divmod
      * 
      */
     @JRubyMethod(name = "divmod", required = 1, compat = CompatVersion.RUBY1_8)
     @Override
     public IRubyObject divmod(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double y = ((RubyNumeric) other).getDoubleValue();
             double x = value;
 
             double mod = Math.IEEEremainder(x, y);
             // MRI behavior:
             if (Double.isNaN(mod)) {
                 throw getRuntime().newFloatDomainError("NaN");
             }
             double div = Math.floor(x / y);
 
             if (y * mod < 0) {
                 mod += y;
             }
             final Ruby runtime = getRuntime();
             IRubyObject car = dbl2num(runtime, div);
             RubyFloat cdr = RubyFloat.newFloat(runtime, mod);
             return RubyArray.newArray(runtime, car, cdr);
         default:
             return coerceBin(context, "divmod", other);
         }
     }
     	
     /** flo_divmod
      * 
      */
     @JRubyMethod(name = "divmod", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject divmod19(ThreadContext context, IRubyObject other) {
         if (!other.isNil() && other instanceof RubyNumeric
             && ((RubyNumeric)other).getDoubleValue() == 0) {
             throw context.runtime.newZeroDivisionError();
         }
         return divmod(context, other);
     }
     	
     /** flo_pow
      * 
      */
     @JRubyMethod(name = "**", required = 1)
     public IRubyObject op_pow(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyFloat.newFloat(getRuntime(), Math.pow(value, ((RubyNumeric) other)
                     .getDoubleValue()));
         default:
             return coerceBin(context, "**", other);
         }
     }
 
     public IRubyObject op_pow(ThreadContext context, double other) {
         return RubyFloat.newFloat(getRuntime(), Math.pow(value, other));
     }
     
     @JRubyMethod(name = "**", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_pow19(ThreadContext context, IRubyObject other) {
         double d_other = ((RubyNumeric) other).getDoubleValue();
         if (value < 0 && (d_other != Math.round(d_other))) {
             return RubyComplex.newComplexRaw(getRuntime(), this).callMethod(context, "**", other);
         } else {
             return op_pow(context, other);
         }
     }
 
     /** flo_eq
      * 
      */
     @JRubyMethod(name = "==", required = 1)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         if (Double.isNaN(value)) {
             return getRuntime().getFalse();
         }
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return RubyBoolean.newBoolean(getRuntime(), value == ((RubyNumeric) other)
                     .getDoubleValue());
         default:
             // Numeric.equal            
             return super.op_num_equal(context, other);
         }
     }
 
     public IRubyObject op_equal(ThreadContext context, double other) {
         if (Double.isNaN(value)) {
             return getRuntime().getFalse();
         }
         return RubyBoolean.newBoolean(getRuntime(), value == other);
     }
 
     public boolean fastEqual(RubyFloat other) {
         if (Double.isNaN(value)) {
             return false;
         }
         return value == ((RubyFloat)other).value;
     }
 
     @Override
     public final int compareTo(IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             return Double.compare(value, ((RubyNumeric) other).getDoubleValue());
         default:
             return (int)coerceCmp(getRuntime().getCurrentContext(), "<=>", other).convertToInteger().getLongValue();
         }
     }
 
     /** flo_cmp
      * 
      */
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
             if (Double.isInfinite(value)) {
                 return value > 0.0 ? RubyFixnum.one(getRuntime()) : RubyFixnum.minus_one(getRuntime());
             }
         case ClassIndex.FLOAT:
             double b = ((RubyNumeric) other).getDoubleValue();
             return dbl_cmp(getRuntime(), value, b);
         default:
             if (Double.isInfinite(value) && other.respondsTo("infinite?")) {
                 IRubyObject infinite = other.callMethod(context, "infinite?");
                 if (infinite.isNil()) {
                     return value > 0.0 ? RubyFixnum.one(getRuntime()) : RubyFixnum.minus_one(getRuntime());
                 } else {
                     int sign = RubyFixnum.fix2int(infinite);
 
                     if (sign > 0) {
                         if (value > 0.0) {
                             return RubyFixnum.zero(getRuntime());
                         } else {
                             return RubyFixnum.minus_one(getRuntime());
                         }
                     } else {
                         if (value < 0.0) {
                             return RubyFixnum.zero(getRuntime());
                         } else {
                             return RubyFixnum.one(getRuntime());
                         }
                     }
                 }
             }
             return coerceCmp(context, "<=>", other);
         }
     }
 
     public IRubyObject op_cmp(ThreadContext context, double other) {
         return dbl_cmp(getRuntime(), value, other);
     }
 
     /** flo_gt
      * 
      */
     @JRubyMethod(name = ">", required = 1)
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double b = ((RubyNumeric) other).getDoubleValue();
             return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value > b);
         default:
             return coerceRelOp(context, ">", other);
         }
     }
 
     public IRubyObject op_gt(ThreadContext context, double other) {
         return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(other) && value > other);
     }
 
     /** flo_ge
      * 
      */
     @JRubyMethod(name = ">=", required = 1)
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double b = ((RubyNumeric) other).getDoubleValue();
             return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value >= b);
         default:
             return coerceRelOp(context, ">=", other);
         }
     }
 
     public IRubyObject op_ge(ThreadContext context, double other) {
         return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(other) && value >= other);
     }
 
     /** flo_lt
      * 
      */
     @JRubyMethod(name = "<", required = 1)
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double b = ((RubyNumeric) other).getDoubleValue();
             return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value < b);
         default:
             return coerceRelOp(context, "<", other);
 		}
     }
 
     public IRubyObject op_lt(ThreadContext context, double other) {
         return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(other) && value < other);
     }
 
     /** flo_le
      * 
      */
     @JRubyMethod(name = "<=", required = 1)
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         switch (other.getMetaClass().index) {
         case ClassIndex.FIXNUM:
         case ClassIndex.BIGNUM:
         case ClassIndex.FLOAT:
             double b = ((RubyNumeric) other).getDoubleValue();
             return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value <= b);
         default:
             return coerceRelOp(context, "<=", other);
 		}
 	}
 
     public IRubyObject op_le(ThreadContext context, double other) {
         return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(other) && value <= other);
 	}
 	
     /** flo_eql
      * 
      */
     @JRubyMethod(name = "eql?", required = 1)
     @Override
     public IRubyObject eql_p(IRubyObject other) {
         if (other instanceof RubyFloat) {
             double b = ((RubyFloat) other).value;
             if (Double.isNaN(value) || Double.isNaN(b)) {
                 return getRuntime().getFalse();
             }
             if (value == b) {
                 return getRuntime().getTrue();
             }
         }
         return getRuntime().getFalse();
     }
 
     /** flo_hash
      * 
      */
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
 
     @Override
     public final int hashCode() {
         long l = Double.doubleToLongBits(value);
         return (int)(l ^ l >>> 32);
     }    
 
     /** flo_fo 
      * 
      */
     @JRubyMethod(name = "to_f")
     public IRubyObject to_f() {
         return this;
     }
 
     /** flo_abs
      * 
      */
     @JRubyMethod(name = "abs")
     @Override
     public IRubyObject abs(ThreadContext context) {
         if (Double.doubleToLongBits(value) < 0) {
             return RubyFloat.newFloat(context.runtime, Math.abs(value));
         }
         return this;
     }
 
     /** flo_abs/1.9
      * 
      */
     @JRubyMethod(name = "magnitude", compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject magnitude(ThreadContext context) {
         return abs(context);
     }
 
     /** flo_zero_p
      * 
      */
     @JRubyMethod(name = "zero?")
     public IRubyObject zero_p() {
         return RubyBoolean.newBoolean(getRuntime(), value == 0.0);
     }
 
     /** flo_truncate
      * 
      */
     @JRubyMethod(name = {"truncate", "to_i", "to_int"})
     @Override
     public IRubyObject truncate() {
         double f = value;
         if (f > 0.0) f = Math.floor(f);
         if (f < 0.0) f = Math.ceil(f);
 
         return dbl2num(getRuntime(), f);
     }
 
     /** flo_numerator
      * 
      */
     @JRubyMethod(name = "numerator", compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject numerator(ThreadContext context) {
         if (Double.isInfinite(value) || Double.isNaN(value)) return this;
         return super.numerator(context);
     }
 
     /** flo_denominator
      * 
      */
     @JRubyMethod(name = "denominator", compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject denominator(ThreadContext context) {
         if (Double.isInfinite(value) || Double.isNaN(value)) {
             return RubyFixnum.one(context.runtime);
         }
         return super.denominator(context);
     }
 
     /** float_to_r, float_decode
      * 
      */
     static final int DBL_MANT_DIG = 53;
     static final int FLT_RADIX = 2;
     @JRubyMethod(name = "to_r", compat = CompatVersion.RUBY1_9)
     public IRubyObject to_r(ThreadContext context) {
         long[]exp = new long[1]; 
         double f = frexp(value, exp);
         f = ldexp(f, DBL_MANT_DIG);
         long n = exp[0] - DBL_MANT_DIG;
 
         Ruby runtime = context.runtime;
 
         IRubyObject rf = RubyNumeric.dbl2num(runtime, f);
         IRubyObject rn = RubyFixnum.newFixnum(runtime, n);
         return f_mul(context, rf, f_expt(context, RubyFixnum.newFixnum(runtime, FLT_RADIX), rn));
     }
 
     /** float_rationalize
      *
      */
     @JRubyMethod(name = "rationalize", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject rationalize(ThreadContext context, IRubyObject[] args) {
         if (f_negative_p(context, this))
             return f_negate(context, ((RubyFloat) f_abs(context, this)).rationalize(context, args));
 
         Ruby runtime = context.runtime;
         RubyFixnum one = RubyFixnum.one(runtime);
         RubyFixnum two = RubyFixnum.two(runtime);
 
         IRubyObject eps, a, b;
         if (args.length != 0) {
             eps = f_abs(context, args[0]);
             a = f_sub(context, this, eps);
             b = f_add(context, this, eps);
         } else {
             long[] exp = new long[1];
             double f = frexp(value, exp);
             f = ldexp(f, DBL_MANT_DIG);
             long n = exp[0] - DBL_MANT_DIG;
 
 
             IRubyObject rf = RubyNumeric.dbl2num(runtime, f);
             IRubyObject rn = RubyFixnum.newFixnum(runtime, n);
 
             if (f_zero_p(context, rf) || !(f_negative_p(context, rn) || f_zero_p(context, rn)))
                 return RubyRational.newRationalRaw(runtime, f_lshift(context,rf,rn));
 
             a = RubyRational.newRationalRaw(runtime,
                     f_sub(context,f_mul(context, two, rf),one),
                     f_lshift(context, one, f_sub(context,one,rn)));
             b = RubyRational.newRationalRaw(runtime,
                     f_add(context,f_mul(context, two, rf),one),
                     f_lshift(context, one, f_sub(context,one,rn)));
         }
 
         if (invokedynamic(context, a, OP_EQUAL, b).isTrue()) return f_to_r(context, this);
 
         IRubyObject[] ary = new IRubyObject[2];
         ary[0] = a;
         ary[1] = b;
         IRubyObject[] ans = nurat_rationalize_internal(context, ary);
 
         return RubyRational.newRationalRaw(runtime, ans[0], ans[1]);
 
     }
 
     /** floor
      * 
      */
     @JRubyMethod(name = "floor")
     @Override
     public IRubyObject floor() {
         return dbl2num(getRuntime(), Math.floor(value));
     }
 
     /** flo_ceil
      * 
      */
     @JRubyMethod(name = "ceil")
     @Override
     public IRubyObject ceil() {
         return dbl2num(getRuntime(), Math.ceil(value));
     }
 
     /** flo_round
      * 
      */
     @JRubyMethod(name = "round")
     @Override
     public IRubyObject round() {
         return dbl2num(getRuntime(), val2dbl());
     }
     
     @JRubyMethod(name = "round", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject round(ThreadContext context, IRubyObject[] args) {
         if (args.length == 0) return round();
         // truncate floats.
         double digits = num2long(args[0]);
         
         double magnifier = Math.pow(10.0, Math.abs(digits));
         double number = value;
         
         if (Double.isInfinite(value)) {
             if (digits <= 0) throw getRuntime().newFloatDomainError(value < 0 ? "-Infinity" : "Infinity");
             return this;
         }
         
         if (Double.isNaN(value)) {
             if (digits <= 0) throw getRuntime().newFloatDomainError("NaN");
             return this;
         }
         
         // MRI flo_round logic to deal with huge precision numbers.
         double binexp = Math.ceil(Math.log(value)/Math.log(2));
         if (digits >= (DIG+2) - (binexp > 0 ? binexp / 4 : binexp / 3 - 1)) {
             return RubyFloat.newFloat(context.runtime, number);
         }
         if (digits < -(binexp > 0 ? binexp / 3 + 1 : binexp / 4)) {
             return RubyFixnum.zero(context.runtime);
         }
         
         if (Double.isInfinite(magnifier)) {
             if (digits < 0) number = 0;
         } else {
             if (digits < 0) {
                 number /= magnifier;
             } else {
                 number *= magnifier;
             }
 
             number = Math.round(Math.abs(number))*Math.signum(number);
             if (digits < 0) {
                 number *= magnifier;
             } else {
                 number /= magnifier;
             }
         }
         
         if (digits > 0) {
             return RubyFloat.newFloat(context.runtime, number);
         } else {
             if (number > Long.MAX_VALUE || number < Long.MIN_VALUE) {
                 // The only way to get huge precise values with BigDecimal is 
                 // to convert the double to String first.
                 BigDecimal roundedNumber = new BigDecimal(Double.toString(number));
                 return RubyBignum.newBignum(context.runtime, roundedNumber.toBigInteger());
             }
             return dbl2num(context.runtime, (long)number);
         }
     }
     
     private double val2dbl() {
         double f = value;
         if (f > 0.0) {
             f = Math.floor(f);
             if (value - f >= 0.5) {
                 f += 1.0;
             }
         } else if (f < 0.0) {
             f = Math.ceil(f);
             if (f - value >= 0.5) {
                 f -= 1.0;
             }
         }
         
         return f;
     }
         
     /** flo_is_nan_p
      * 
      */
     @JRubyMethod(name = "nan?")
     public IRubyObject nan_p() {
         return RubyBoolean.newBoolean(getRuntime(), Double.isNaN(value));
     }
 
     /** flo_is_infinite_p
      * 
      */
     @JRubyMethod(name = "infinite?")
     public IRubyObject infinite_p() {
         if (Double.isInfinite(value)) {
             return RubyFixnum.newFixnum(getRuntime(), value < 0 ? -1 : 1);
         }
         return getRuntime().getNil();
     }
             
     /** flo_is_finite_p
      * 
      */
     @JRubyMethod(name = "finite?")
     public IRubyObject finite_p() {
         if (Double.isInfinite(value) || Double.isNaN(value)) {
             return getRuntime().getFalse();
         }
         return getRuntime().getTrue();
     }
 
     private ByteList marshalDump() {
         if (Double.isInfinite(value)) return value < 0 ? NEGATIVE_INFINITY_BYTELIST : INFINITY_BYTELIST;
         if (Double.isNaN(value)) return NAN_BYTELIST;
 
         ByteList byteList = new ByteList();
         // Always use US locale, to ensure "." separator. JRUBY-5918
         Sprintf.sprintf(byteList, Locale.US, "%.17g", RubyArray.newArray(getRuntime(), this));
         return byteList;
     }
 
     public static void marshalTo(RubyFloat aFloat, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(aFloat);
         output.writeString(aFloat.marshalDump());
     }
         
     public static RubyFloat unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         ByteList value = input.unmarshalString();
         RubyFloat result;
         if (value.equals(NAN_BYTELIST)) {
             result = RubyFloat.newFloat(input.getRuntime(), RubyFloat.NAN);
         } else if (value.equals(NEGATIVE_INFINITY_BYTELIST)) {
             result = RubyFloat.newFloat(input.getRuntime(), Double.NEGATIVE_INFINITY);
         } else if (value.equals(INFINITY_BYTELIST)) {
             result = RubyFloat.newFloat(input.getRuntime(), Double.POSITIVE_INFINITY);
         } else {
             result = RubyFloat.newFloat(input.getRuntime(),
                     ConvertDouble.byteListToDouble(value, false));
         }
         input.registerLinkTarget(result);
         return result;
     }
 
     private static final ByteList NAN_BYTELIST = new ByteList("nan".getBytes());
     private static final ByteList NEGATIVE_INFINITY_BYTELIST = new ByteList("-inf".getBytes());
     private static final ByteList INFINITY_BYTELIST = new ByteList("inf".getBytes());
 }
diff --git a/src/org/jruby/RubyHash.java b/src/org/jruby/RubyHash.java
index 18e2de9de2..43cbe86448 100644
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
@@ -1,1128 +1,1123 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
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
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
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
 import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.invokedynamic.MethodNames;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.RecursiveComparator;
 
 import static org.jruby.CompatVersion.*;
 import static org.jruby.runtime.Helpers.invokedynamic;
 import static org.jruby.runtime.invokedynamic.MethodNames.HASH;
 
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
     public static final int DEFAULT_INSPECT_STR_SIZE = 20;
 
     public static RubyClass createHashClass(Ruby runtime) {
         RubyClass hashc = runtime.defineClass("Hash", runtime.getObject(), HASH_ALLOCATOR);
         runtime.setHash(hashc);
 
         hashc.index = ClassIndex.HASH;
         hashc.setReifiedClass(RubyHash.class);
         
-        hashc.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyHash;
-            }
-        };
+        hashc.kindOf = new RubyModule.JavaClassKindOf(RubyHash.class);
 
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
         Ruby runtime = context.runtime;
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
         return TypeConverter.convertToTypeWithCheck(args, context.runtime.getHash(), "to_hash");
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
     public static final RubyHash newSmallHash(Ruby runtime) {
         return new RubyHash(runtime, 1);
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
         allocFirst();
     }
 
     public RubyHash(Ruby runtime, int buckets) {
         this(runtime, runtime.getNil(), buckets);
     }
 
     public RubyHash(Ruby runtime) {
         this(runtime, runtime.getNil());
     }
 
     public RubyHash(Ruby runtime, IRubyObject defaultValue) {
         super(runtime, runtime.getHash());
         this.ifNone = defaultValue;
         allocFirst();
     }
 
     public RubyHash(Ruby runtime, IRubyObject defaultValue, int buckets) {
         super(runtime, runtime.getHash());
         this.ifNone = defaultValue;
         allocFirst(buckets);
     }
 
     /*
      *  Constructor for internal usage (mainly for Array#|, Array#&, Array#- and Array#uniq)
      *  it doesn't initialize ifNone field
      */
     RubyHash(Ruby runtime, boolean objectSpace) {
         super(runtime, runtime.getHash(), objectSpace);
         allocFirst();
     }
 
     // TODO should this be deprecated ? (to be efficient, internals should deal with RubyHash directly)
     public RubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
         super(runtime, runtime.getHash());
         this.ifNone = defaultValue;
         allocFirst();
 
         for (Iterator iter = valueMap.entrySet().iterator();iter.hasNext();) {
             Map.Entry e = (Map.Entry)iter.next();
             internalPut((IRubyObject)e.getKey(), (IRubyObject)e.getValue());
         }
     }
 
     private final void allocFirst() {
         threshold = INITIAL_THRESHOLD;
         table = new RubyHashEntry[MRI_HASH_RESIZE ? MRI_INITIAL_CAPACITY : JAVASOFT_INITIAL_CAPACITY];
     }
 
     private final void allocFirst(int buckets) {
         threshold = INITIAL_THRESHOLD;
         table = new RubyHashEntry[buckets];
     }
 
     private final void alloc() {
         generation++;
         head.prevAdded = head.nextAdded = head;
         allocFirst();
     }
 
     private final void alloc(int buckets) {
         generation++;
         head.prevAdded = head.nextAdded = head;
         allocFirst(buckets);
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
 
     private final synchronized void resize(int newCapacity) {
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
         if (iteratorCount > 0) {
             throw getRuntime().newRuntimeError("can't add a new key into hash during iteration");
         }
     }
     // ------------------------------
     public static long collisions = 0;
 
     // put implementation
 
     private final void internalPut(final IRubyObject key, final IRubyObject value) {
         internalPut(key, value, true);
     }
 
     private final void internalPutSmall(final IRubyObject key, final IRubyObject value) {
         internalPutSmall(key, value, true);
     }
 
     protected void internalPut(final IRubyObject key, final IRubyObject value, final boolean checkForExisting) {
         checkResize();
 
         internalPutSmall(key, value, checkForExisting);
     }
 
     protected void internalPutSmall(final IRubyObject key, final IRubyObject value, final boolean checkForExisting) {
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
             default:
                 throw context.runtime.newArgumentError(args.length, 1);
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
             return Helpers.invoke(context, ifNone, "call", this, arg);
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
     
     @JRubyMethod(name = "default_proc=", compat = RUBY2_0)
     public IRubyObject set_default_proc20(IRubyObject proc) {
         modify();
         
         if (proc.isNil()) {
             ifNone = proc;
             return proc;
         }
         
         return set_default_proc(proc);
     }
 
     /** rb_hash_modify
      *
      */
     public void modify() {
     	testFrozen("Hash");
     }
 
     /** inspect_hash
      *
      */
     private IRubyObject inspectHash(final ThreadContext context) {
         final RubyString str = RubyString.newStringLight(context.runtime, DEFAULT_INSPECT_STR_SIZE);
         str.cat((byte)'{');
         final boolean[] firstEntry = new boolean[1];
 
         firstEntry[0] = true;
         visitAll(new Visitor() {
             @Override
             public void visit(IRubyObject key, IRubyObject value) {
                 if (!firstEntry[0]) str.cat((byte)',').cat((byte)' ');
 
                 str.cat(inspect(context, key)).cat((byte)'=').cat((byte)'>').cat(inspect(context, value));
                 
                 firstEntry[0] = false;
             }
         });
         str.cat((byte)'}');
         return str;
     }
     
     private IRubyObject inspectHash19(final ThreadContext context) {
         final RubyString str = RubyString.newStringLight(context.runtime, DEFAULT_INSPECT_STR_SIZE, ASCIIEncoding.INSTANCE);
         str.cat((byte)'{');
         final boolean[] firstEntry = new boolean[1];
 
         firstEntry[0] = true;
         visitAll(new Visitor() {
             @Override
             public void visit(IRubyObject key, IRubyObject value) {
                 if (!firstEntry[0]) str.cat((byte)',').cat((byte)' ');
 
                 str.cat19(inspect(context, key)).cat((byte)'=').cat((byte)'>').cat19(inspect(context, value));
                 
                 firstEntry[0] = false;
             }
         });
         str.cat((byte)'}');
         return str;
     }    
 
     /** rb_hash_inspect
      *
      */
     @JRubyMethod(name = "inspect", compat = RUBY1_8)
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
 
     @JRubyMethod(name = "inspect", compat = RUBY1_9)
     public IRubyObject inspect19(ThreadContext context) {
         if (size == 0) return RubyString.newUSASCIIString(context.runtime, "{}");
         if (getRuntime().isInspecting(this)) return RubyString.newUSASCIIString(context.runtime, "{...}");
 
         try {
             getRuntime().registerInspecting(this);
             return inspectHash19(context);
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
         Ruby runtime = context.runtime;
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
         return inspect19(context);
     }
 
     /** rb_hash_rehash
      *
      */
     @JRubyMethod(name = "rehash")
     public RubyHash rehash() {
         if (iteratorCount > 0) {
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
                 entry.hash = hashValue(entry.key.hashCode()); // update the hash value
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
     
     @JRubyMethod
     public RubyHash to_h(ThreadContext context) {
         return getType() == getRuntime().getHash() ? this : newHash(getRuntime()).replace(context, this);
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
 
     public final void fastASetSmallCheckString(Ruby runtime, IRubyObject key, IRubyObject value) {
         if (key instanceof RubyString) {
             op_asetSmallForString(runtime, (RubyString) key, value);
         } else {
             internalPutSmall(key, value);
         }
     }
 
     /** rb_hash_aset
      *
      */
     @JRubyMethod(name = {"[]=", "store"})
     public IRubyObject op_aset(ThreadContext context, IRubyObject key, IRubyObject value) {
         modify();
 
         fastASetCheckString(context.runtime, key, value);
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
 
     protected void op_asetSmallForString(Ruby runtime, RubyString key, IRubyObject value) {
         final RubyHashEntry entry = internalGetEntry(key);
         if (entry != NO_ENTRY) {
             entry.value = value;
         } else {
             checkIterating();
             if (!key.isFrozen()) {
                 key = key.strDup(runtime, key.getMetaClass().getRealClass());
                 key.setFrozen(true);
             }
             internalPutSmall(key, value, false);
         }
     }
 
     public final IRubyObject fastARef(IRubyObject key) { // retuns null when not found to avoid unnecessary getRuntime().getNil() call
         return internalGet(key);
     }
 
     public RubyBoolean compare(final ThreadContext context, final MethodNames method, IRubyObject other) {
 
         Ruby runtime = context.runtime;
 
         if (!(other instanceof RubyHash)) {
             if (!other.respondsTo("to_hash")) {
                 return runtime.getFalse();
             } else {
                 return Helpers.rbEqual(context, other, this);
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
                         throw MISMATCH;
                     }
 
                     if (!(method == MethodNames.OP_EQUAL ?
                             Helpers.rbEqual(context, value, value2) :
                             Helpers.rbEql(context, value, value2)).isTrue()) {
                         throw MISMATCH;
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
         return RecursiveComparator.compare(context, MethodNames.OP_EQUAL, this, other);
     }
 
     /** rb_hash_eql
      * 
      */
     @JRubyMethod(name = "eql?")
     public IRubyObject op_eql19(final ThreadContext context, IRubyObject other) {
         return RecursiveComparator.compare(context, MethodNames.EQL, this, other);
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
diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index b4c3a9584a..d72d2c46e3 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,1275 +1,1270 @@
 /*
  **** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Evan Buswell <ebuswell@gmail.com>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jcodings.specific.UTF16BEEncoding;
 import org.jcodings.specific.UTF16LEEncoding;
 import org.jcodings.specific.UTF32BEEncoding;
 import org.jcodings.specific.UTF32LEEncoding;
 import org.jcodings.specific.UTF8Encoding;
 import org.jruby.runtime.Helpers;
 import org.jruby.util.StringSupport;
 import org.jruby.util.io.EncodingUtils;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.io.SelectBlob;
 import jnr.constants.platform.Fcntl;
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.FileNotFoundException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.NonReadableChannelException;
 import java.nio.channels.Pipe;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.WritableByteChannel;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.jcodings.Encoding;
 import org.jruby.anno.FrameField;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.ext.fcntl.FcntlLibrary;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.Stream;
 import org.jruby.util.io.IOOptions;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.DirectoryAsFileException;
 import org.jruby.util.io.STDIO;
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.ChannelDescriptor;
 
 import org.jcodings.specific.ASCIIEncoding;
 import org.jruby.runtime.Arity;
 
 import static org.jruby.CompatVersion.*;
 import static org.jruby.RubyEnumerator.enumeratorize;
 import org.jruby.runtime.encoding.EncodingService;
 import org.jruby.util.CharsetTranscoder;
 import org.jruby.util.ShellLauncher.POpenProcess;
 import org.jruby.util.io.IOEncodable;
 import static org.jruby.util.StringSupport.isIncompleteChar;
 
 /**
  * 
  * @author jpetersen
  */
 @JRubyClass(name="IO", include="Enumerable")
 public class RubyIO extends RubyObject implements IOEncodable {
     // This should only be called by this and RubyFile.
     // It allows this object to be created without a IOHandler.
     public RubyIO(Ruby runtime, RubyClass type) {
         super(runtime, type);
         
         openFile = new OpenFile();
     }
     
     public RubyIO(Ruby runtime, OutputStream outputStream) {
         this(runtime, outputStream, true);
     }
 
     public RubyIO(Ruby runtime, OutputStream outputStream, boolean autoclose) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (outputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(outputStream)), autoclose));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.WRITABLE | OpenFile.APPEND);
     }
     
     public RubyIO(Ruby runtime, InputStream inputStream) {
         super(runtime, runtime.getIO());
         
         if (inputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(inputStream))));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.READABLE);
     }
     
     public RubyIO(Ruby runtime, Channel channel) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (channel == null) {
             throw runtime.newRuntimeError("Opening null channel");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(channel)));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
     }
 
     public RubyIO(Ruby runtime, ShellLauncher.POpenProcess process, IOOptions ioOptions) {
         this(runtime, runtime.getIO(), process, null, ioOptions);
     }
     
     @Deprecated
     public RubyIO(Ruby runtime, RubyClass cls, ShellLauncher.POpenProcess process, RubyHash options, IOOptions ioOptions) {
         super(runtime, cls);
 
         ioOptions = updateIOOptionsFromOptions(runtime.getCurrentContext(), (RubyHash) options, ioOptions);
 
         openFile = new OpenFile();
         
         setupPopen(ioOptions.getModeFlags(), process);
     }
     
     public RubyIO(Ruby runtime, STDIO stdio) {
         super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         ChannelDescriptor descriptor;
         Stream mainStream;
 
         switch (stdio) {
         case IN:
             // special constructor that accepts stream, not channel
             descriptor = new ChannelDescriptor(runtime.getIn(), newModeFlags(runtime, ModeFlags.RDONLY), FileDescriptor.in);
             runtime.putFilenoMap(0, descriptor.getFileno());
             mainStream = ChannelStream.open(runtime, descriptor);
             openFile.setMainStream(mainStream);
             break;
         case OUT:
             descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getOut()), newModeFlags(runtime, ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out);
             runtime.putFilenoMap(1, descriptor.getFileno());
             mainStream = ChannelStream.open(runtime, descriptor);
             openFile.setMainStream(mainStream);
             openFile.getMainStream().setSync(true);
             break;
         case ERR:
             descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getErr()), newModeFlags(runtime, ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.err);
             runtime.putFilenoMap(2, descriptor.getFileno());
             mainStream = ChannelStream.open(runtime, descriptor);
             openFile.setMainStream(mainStream);
             openFile.getMainStream().setSync(true);
             break;
         }
 
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         // never autoclose stdio streams
         openFile.setAutoclose(false);
     }
     
     public static RubyIO newIO(Ruby runtime, Channel channel) {
         return new RubyIO(runtime, channel);
     }
     
     public OpenFile getOpenFile() {
         return openFile;
     }
     
     protected OpenFile getOpenFileChecked() {
         openFile.checkClosed(getRuntime());
         return openFile;
     }
     
     private static ObjectAllocator IO_ALLOCATOR = new ObjectAllocator() {
         @Override
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIO(runtime, klass);
         }
     };
 
     /*
      * We use FILE versus IO to match T_FILE in MRI.
      */
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.FILE;
     }
 
     public static RubyClass createIOClass(Ruby runtime) {
         RubyClass ioClass = runtime.defineClass("IO", runtime.getObject(), IO_ALLOCATOR);
 
         ioClass.index = ClassIndex.IO;
         ioClass.setReifiedClass(RubyIO.class);
 
-        ioClass.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyIO;
-            }
-        };
+        ioClass.kindOf = new RubyModule.JavaClassKindOf(RubyIO.class);
 
         ioClass.includeModule(runtime.getEnumerable());
         
         // TODO: Implement tty? and isatty.  We have no real capability to
         // determine this from java, but if we could set tty status, then
         // we could invoke jruby differently to allow stdin to return true
         // on this.  This would allow things like cgi.rb to work properly.
         
         ioClass.defineAnnotatedMethods(RubyIO.class);
 
         // Constants for seek
         ioClass.setConstant("SEEK_SET", runtime.newFixnum(Stream.SEEK_SET));
         ioClass.setConstant("SEEK_CUR", runtime.newFixnum(Stream.SEEK_CUR));
         ioClass.setConstant("SEEK_END", runtime.newFixnum(Stream.SEEK_END));
 
         if (runtime.is1_9()) {
             ioClass.defineModuleUnder("WaitReadable");
             ioClass.defineModuleUnder("WaitWritable");
         }
 
         return ioClass;
     }
 
     public OutputStream getOutStream() {
         try {
             return getOpenFileChecked().getMainStreamSafe().newOutputStream();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     public InputStream getInStream() {
         try {
             return getOpenFileChecked().getMainStreamSafe().newInputStream();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     public Channel getChannel() {
         try {
             return getOpenFileChecked().getMainStreamSafe().getChannel();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     @Deprecated
     public Stream getHandler() throws BadDescriptorException {
         return getOpenFileChecked().getMainStreamSafe();
     }
 
     protected void reopenPath(Ruby runtime, IRubyObject[] args) {
         IRubyObject pathString;
         
         if (runtime.is1_9()) {
             pathString = RubyFile.get_path(runtime.getCurrentContext(), args[0]);
         } else {
             pathString = args[0].convertToString();
         }
 
         // TODO: check safe, taint on incoming string
 
         try {
             IOOptions modes;
             if (args.length > 1) {
                 IRubyObject modeString = args[1].convertToString();
                 modes = newIOOptions(runtime, modeString.toString());
 
                 openFile.setMode(modes.getModeFlags().getOpenFileFlags());
             } else {
                 modes = newIOOptions(runtime, "r");
             }
 
             String path = pathString.toString();
 
             // Ruby code frequently uses a platform check to choose "NUL:" on windows
             // but since that check doesn't work well on JRuby, we help it out
 
             openFile.setPath(path);
 
             if (openFile.getMainStream() == null) {
                 try {
                     openFile.setMainStream(ChannelStream.fopen(runtime, path, modes.getModeFlags()));
                 } catch (FileExistsException fee) {
                     throw runtime.newErrnoEEXISTError(path);
                 }
 
                 if (openFile.getPipeStream() != null) {
                     openFile.getPipeStream().fclose();
                     openFile.setPipeStream(null);
                 }
             } else {
                 // TODO: This is an freopen in MRI, this is close, but not quite the same
                 openFile.getMainStreamSafe().freopen(runtime, path, newIOOptions(runtime, openFile.getModeAsString(runtime)).getModeFlags());
                 
                 if (openFile.getPipeStream() != null) {
                     // TODO: pipe handler to be reopened with path and "w" mode
                 }
             }
         } catch (PipeException pe) {
             throw runtime.newErrnoEPIPEError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     protected void reopenIO(Ruby runtime, RubyIO ios) {
         try {
             if (ios.openFile == this.openFile) return;
 
             OpenFile origFile = ios.getOpenFileChecked();
             OpenFile selfFile = getOpenFileChecked();
 
             long pos = 0;
             Stream origStream = origFile.getMainStreamSafe();
             ChannelDescriptor origDescriptor = origStream.getDescriptor();
             boolean origIsSeekable = origDescriptor.isSeekable();
 
             if (origFile.isReadable() && origIsSeekable) {
                 pos = origFile.getMainStreamSafe().fgetpos();
             }
 
             if (origFile.getPipeStream() != null) {
                 origFile.getPipeStream().fflush();
             } else if (origFile.isWritable()) {
                 origStream.fflush();
             }
 
             if (selfFile.isWritable()) {
                 selfFile.getWriteStreamSafe().fflush();
             }
 
             selfFile.setMode(origFile.getMode());
             selfFile.setProcess(origFile.getProcess());
             selfFile.setLineNumber(origFile.getLineNumber());
             selfFile.setPath(origFile.getPath());
             selfFile.setFinalizer(origFile.getFinalizer());
 
             Stream selfStream = selfFile.getMainStreamSafe();
             ChannelDescriptor selfDescriptor = selfFile.getMainStreamSafe().getDescriptor();
             boolean selfIsSeekable = selfDescriptor.isSeekable();
 
             // confirm we're not reopening self's channel
             if (selfDescriptor.getChannel() != origDescriptor.getChannel()) {
                 // check if we're a stdio IO, and ensure we're not badly mutilated
                 if (runtime.getFileno(selfDescriptor) >= 0 && runtime.getFileno(selfDescriptor) <= 2) {
                     selfFile.getMainStreamSafe().clearerr();
 
                     // dup2 new fd into self to preserve fileno and references to it
                     origDescriptor.dup2Into(selfDescriptor);
                 } else {
                     Stream pipeFile = selfFile.getPipeStream();
                     int mode = selfFile.getMode();
                     selfFile.getMainStreamSafe().fclose();
                     selfFile.setPipeStream(null);
 
                     // TODO: turn off readable? am I reading this right?
                     // This only seems to be used while duping below, since modes gets
                     // reset to actual modes afterward
                     //fptr->mode &= (m & FMODE_READABLE) ? ~FMODE_READABLE : ~FMODE_WRITABLE;
 
                     if (pipeFile != null) {
                         selfFile.setMainStream(ChannelStream.fdopen(runtime, origDescriptor, origDescriptor.getOriginalModes()));
                         selfFile.setPipeStream(pipeFile);
                     } else {
                         // only use internal fileno here, stdio is handled above
                         selfFile.setMainStream(
                                 ChannelStream.open(
                                 runtime,
                                 origDescriptor.dup2(selfDescriptor.getFileno())));
 
                         // since we're not actually duping the incoming channel into our handler, we need to
                         // copy the original sync behavior from the other handler
                         selfFile.getMainStreamSafe().setSync(selfFile.getMainStreamSafe().isSync());
                     }
                     selfFile.setMode(mode);
                 }
 
                 // TODO: anything threads attached to original fd are notified of the close...
                 // see rb_thread_fd_close
 
                 if (origFile.isReadable() && pos >= 0) {
                     if (selfIsSeekable) {
                         selfFile.seek(pos, Stream.SEEK_SET);
                     }
 
                     if (origIsSeekable) {
                         origFile.seek(pos, Stream.SEEK_SET);
                     }
                 }
             }
 
             // only use internal fileno here, stdio is handled above
             if (selfFile.getPipeStream() != null && selfDescriptor.getFileno() != selfFile.getPipeStream().getDescriptor().getFileno()) {
                 int fd = selfFile.getPipeStream().getDescriptor().getFileno();
 
                 if (origFile.getPipeStream() == null) {
                     selfFile.getPipeStream().fclose();
                     selfFile.setPipeStream(null);
                 } else if (fd != origFile.getPipeStream().getDescriptor().getFileno()) {
                     selfFile.getPipeStream().fclose();
                     ChannelDescriptor newFD2 = origFile.getPipeStream().getDescriptor().dup2(fd);
                     selfFile.setPipeStream(ChannelStream.fdopen(runtime, newFD2, newIOOptions(runtime, "w").getModeFlags()));
                 }
             }
 
             // TODO: restore binary mode
             //            if (fptr->mode & FMODE_BINMODE) {
             //                rb_io_binmode(io);
             //            }
 
             // TODO: set our metaclass to target's class (i.e. scary!)
 
         } catch (IOException ex) { // TODO: better error handling
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (PipeException ex) {
             ex.printStackTrace();
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (InvalidValueException ive) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     @JRubyMethod(name = "reopen", required = 1, optional = 1)
     public IRubyObject reopen(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
     	IRubyObject tmp = TypeConverter.convertToTypeWithCheck(args[0], runtime.getIO(), "to_io");
         
     	if (!tmp.isNil()) {
             reopenIO(runtime, (RubyIO) tmp);
         } else {
             reopenPath(runtime, args);
         }
         
         return this;
     }
 
     @Deprecated
     public static ModeFlags getIOModes(Ruby runtime, String modesString) {
         return newModeFlags(runtime, modesString);
     }
 
     @Deprecated
     public static int getIOModesIntFromString(Ruby runtime, String modesString) {
         try {
             return ModeFlags.getOFlagsFromString(modesString);
         } catch (InvalidValueException ive) {
             throw runtime.newArgumentError("illegal access mode");
         }
     }
 
     /*
      * Ensure that separator is valid otherwise give it the default paragraph separator.
      */
     private ByteList separator(Ruby runtime) {
         return separator(runtime, runtime.getRecordSeparatorVar().get());
     }
 
     private ByteList separator(Ruby runtime, IRubyObject separatorValue) {
         ByteList separator = separatorValue.isNil() ? null :
             separatorValue.convertToString().getByteList();
 
         if (separator != null) {
             if (separator.getRealSize() == 0) return Stream.PARAGRAPH_DELIMETER;
 
             if (runtime.is1_9()) {
                 if (separator.getEncoding() != getReadEncoding(runtime)) {
                     separator = CharsetTranscoder.transcode(runtime.getCurrentContext(), separator,
                             getInputEncoding(runtime), getReadEncoding(runtime), runtime.getNil());
                 }
             }
         }
 
         return separator;
     }
 
     private ByteList getSeparatorFromArgs(Ruby runtime, IRubyObject[] args, int idx) {
 
         if (args.length > idx && args[idx] instanceof RubyFixnum) {
             return separator(runtime, runtime.getRecordSeparatorVar().get());
         }
 
         return separator(runtime, args.length > idx ? args[idx] : runtime.getRecordSeparatorVar().get());
     }
 
     private ByteList getSeparatorForGets(Ruby runtime, IRubyObject[] args) {
         return getSeparatorFromArgs(runtime, args, 0);
     }
 
     private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {
         return getline(runtime, separator, -1, cache);
     }
 
     public IRubyObject getline(Ruby runtime, ByteList separator) {
         return getline(runtime, separator, -1, null);
     }
 
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     public IRubyObject getline(Ruby runtime, ByteList separator, long limit) {
         return getline(runtime, separator, limit, null);
     }
 
     private IRubyObject getline(Ruby runtime, ByteList separator, long limit, ByteListCache cache) {
         return getlineInner(runtime, separator, limit, cache);
     }
     
     private IRubyObject getlineEmptyString(Ruby runtime) {
         if (runtime.is1_9()) return RubyString.newEmptyString(runtime, getReadEncoding(runtime));
 
         return RubyString.newEmptyString(runtime);
     }
     
     private IRubyObject getlineAll(Ruby runtime, OpenFile myOpenFile) throws IOException, BadDescriptorException {
         RubyString str = readAll();
 
         if (str.getByteList().length() == 0) return runtime.getNil();
         incrementLineno(runtime, myOpenFile);
         
         return str;
     }
     
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      * mri: rb_io_getline_1 (mostly)
      */
     private IRubyObject getlineInner(Ruby runtime, ByteList separator, long limit, ByteListCache cache) {
         try {
             boolean is19 = runtime.is1_9();
             
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             boolean isParagraph = separator == Stream.PARAGRAPH_DELIMETER;
             separator = isParagraph ? Stream.PARAGRAPH_SEPARATOR : separator;
             
             if (isParagraph) swallow('\n');
             
             if (separator == null && limit < 0) {
                 return getlineAll(runtime, myOpenFile);
             } else if (limit == 0) {
                 return getlineEmptyString(runtime);
             } else if (separator != null && separator.length() == 1 && limit < 0 && 
                     (!is19 || (!needsReadConversion() && getReadEncoding(runtime).isAsciiCompatible()))) {
                 return getlineFast(runtime, separator.get(0) & 0xFF, cache);
             } else {
                 Stream readStream = myOpenFile.getMainStreamSafe();
                 int c = -1;
                 int n = -1;
                 int newline = (separator != null) ? (separator.get(separator.length() - 1) & 0xFF) : -1;
                 
                 // FIXME: Change how we consume streams to match MRI (see append_line/more_char/fill_cbuf)
                 // Awful hack.  MRI pre-transcodes lines into read-ahead whereas
                 // we read a single line at a time PRE-transcoded.  To keep our
                 // logic we need to do one additional transcode of the sep to
                 // match the pre-transcoded encoding.  This is gross and we should
                 // mimick MRI.
                 if (is19 && separator != null && separator.getEncoding() != getInputEncoding(runtime)) {
                     separator = CharsetTranscoder.transcode(runtime.getCurrentContext(), separator, separator.getEncoding(), getInputEncoding(runtime), null);
                     newline = separator.get(separator.length() - 1) & 0xFF;
                 }
 
                 ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
                 try {
                     ThreadContext context = runtime.getCurrentContext();
                     boolean update = false;
                     boolean limitReached = false;
                     
                     if (is19) makeReadConversion(context);
                     
                     while (true) {
                         do {
                             readCheck(readStream);
                             readStream.clearerr();
 
                             try {
                                 runtime.getCurrentContext().getThread().beforeBlockingCall();
                                 if (limit == -1) {
                                     n = readStream.getline(buf, (byte) newline);
                                 } else {
                                     n = readStream.getline(buf, (byte) newline, limit);
 
                                     if (buf.length() > 0 && isIncompleteChar(buf.get(buf.length() - 1))) {
                                         buf.append((byte)readStream.fgetc());
                                     }
 
                                     limit -= n;
                                     if (limit <= 0) {
                                         update = limitReached = true;
                                         break;
                                     }
                                 }
 
                                 c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                             } catch (EOFException e) {
                                 n = -1;
                             } finally {
                                 runtime.getCurrentContext().getThread().afterBlockingCall();
                             }
                             
                             // CRuby checks ferror(f) and retry getc for
                             // non-blocking IO.
                             if (n == 0) {
                                 waitReadable(readStream);
                                 continue;
                             } else if (n == -1) {
                                 break;
                             }
 
                             update = true;
                         } while (c != newline); // loop until we see the nth separator char
 
 
                         // if we hit EOF or reached limit then we're done
                         if (n == -1 || limitReached) {
                             break;
                         }
 
                         // if we've found the last char of the separator,
                         // and we've found at least as many characters as separator length,
                         // and the last n characters of our buffer match the separator, we're done
                         if (c == newline && separator != null && buf.length() >= separator.length() &&
                                 0 == ByteList.memcmp(buf.getUnsafeBytes(), buf.getBegin() + buf.getRealSize() - separator.length(), separator.getUnsafeBytes(), separator.getBegin(), separator.getRealSize())) {
                             break;
                         }
                     }
                     
                     if (is19) buf = readTranscoder.transcode(context, buf);
                     
                     if (isParagraph && c != -1) swallow('\n');
                     if (!update) return runtime.getNil();
 
                     incrementLineno(runtime, myOpenFile);
 
                     return makeString(runtime, buf, cache != null);
                 }
                 finally {
                     if (cache != null) cache.release(buf);
                 }
             }
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (EOFException e) {
             return runtime.getNil();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     // mri: io_read_encoding
     private Encoding getReadEncoding(Ruby runtime) {
         return readEncoding != null ? readEncoding : runtime.getDefaultExternalEncoding();
     }
     
     // mri: io_input_encoding
     private Encoding getInputEncoding(Ruby runtime) {
         return writeEncoding != null ? writeEncoding : getReadEncoding(runtime);
     }
     
     protected Encoding getInternalEncoding(Ruby runtime) {
         if (writeEncoding == null) return null;
         
         return getReadEncoding(runtime);
     }
 
     private RubyString makeString(Ruby runtime, ByteList buffer, boolean isCached) {
         ByteList newBuf = isCached ? new ByteList(buffer) : buffer;
 
         if (runtime.is1_9()) newBuf.setEncoding(getReadEncoding(runtime));
 
         RubyString str = RubyString.newString(runtime, newBuf);
         str.setTaint(true);
 
         return str;
     }
 
     private void incrementLineno(Ruby runtime, OpenFile myOpenFile) {
         int lineno = myOpenFile.getLineNumber() + 1;
         myOpenFile.setLineNumber(lineno);
         runtime.setCurrentLine(lineno);
         RubyArgsFile.setCurrentLineNumber(runtime.getArgsFile(), lineno);
     }
 
     protected boolean swallow(int term) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStreamSafe();
         int c;
         
         do {
             readCheck(readStream);
             
             try {
                 c = readStream.fgetc();
             } catch (EOFException e) {
                 c = -1;
             }
             
             if (c != term) {
                 readStream.ungetc(c);
                 return true;
             }
         } while (c != -1);
         
         return false;
     }
     
     private static String vendor;
     static { String v = SafePropertyAccessor.getProperty("java.vendor") ; vendor = (v == null) ? "" : v; };
     private static String msgEINTR = "Interrupted system call";
 
     public static boolean restartSystemCall(Exception e) {
         return vendor.startsWith("Apple") && e.getMessage().equals(msgEINTR);
     }
     
     private IRubyObject getlineFast(Ruby runtime, int delim, ByteListCache cache) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStreamSafe();
         int c = -1;
 
         ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
         try {
             boolean update = false;
             do {
                 readCheck(readStream);
                 readStream.clearerr();
                 int n;
                 try {
                     runtime.getCurrentContext().getThread().beforeBlockingCall();
                     n = readStream.getline(buf, (byte) delim);
                     c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                 } catch (EOFException e) {
                     n = -1;
                 } finally {
                     runtime.getCurrentContext().getThread().afterBlockingCall();
                 }
 
                 // CRuby checks ferror(f) and retry getc for non-blocking IO.
                 if (n == 0) {
                     waitReadable(readStream);
                     continue;
                 } else if (n == -1) {
                     break;
                 }
                 
                 update = true;
             } while (c != delim);
 
             if (!update) return runtime.getNil();
                 
             incrementLineno(runtime, openFile);
 
             return makeString(runtime, buf, cache != null);
         } finally {
             if (cache != null) cache.release(buf);
         }
     }
     // IO class methods.
 
     @JRubyMethod(name = {"new", "for_fd"}, rest = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass)recv;
         
         if (block.isGiven()) {
             String className = klass.getName();
             context.runtime.getWarnings().warn(
                     ID.BLOCK_NOT_ACCEPTED,
                     className + "::new() does not take block; use " + className + "::open() instead");
         }
         
         return klass.newInstance(context, args, block);
     }
 
     private IRubyObject initializeCommon19(ThreadContext context, int fileno, IRubyObject vmodeArg, IRubyObject options) {
         Ruby runtime = context.runtime;
 
         if(options != null && !options.isNil() && !(options instanceof RubyHash) && !(options.respondsTo("to_hash"))) {
             throw runtime.newArgumentError("last argument must be a hash!");
         }
 
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(runtime.getFilenoExtMap(fileno));
 
             if (descriptor == null) throw runtime.newErrnoEBADFError();
 
             descriptor.checkOpen();
 
             IRubyObject[] pm = new IRubyObject[] { runtime.newFixnum(0), vmodeArg };
             int oflags = EncodingUtils.extractModeEncoding(context, this, pm, options, false);
             
             if (pm[EncodingUtils.VMODE] == null || pm[EncodingUtils.VMODE].isNil()) {
                 oflags = descriptor.getOriginalModes().getFlags();
             }
 
             // JRUBY-4650: Make sure we clean up the old data, if it's present.
             if (openFile.isOpen()) openFile.cleanup(runtime, false);
             
             ModeFlags modes = ModeFlags.createModeFlags(oflags);
             
             openFile.setMode(modes.getOpenFileFlags());
             openFile.setMainStream(fdopen(descriptor, modes));
         } catch (BadDescriptorException ex) {
             throw context.runtime.newErrnoEBADFError();
         }
 
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, Block unused) {
         return initializeCommon19(context, RubyNumeric.fix2int(fileNumber), null, null);
     }
     
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject second, Block unused) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         IRubyObject vmode = null;
         RubyHash options = null;
         IRubyObject hashTest = TypeConverter.checkHashType(context.runtime, second);
         if (hashTest instanceof RubyHash) {
             options = (RubyHash)hashTest;
         } else {
             vmode = second;
         }
 
         return initializeCommon19(context, fileno, vmode, options);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject modeValue, IRubyObject options, Block unused) {
         int fileno = RubyNumeric.fix2int(fileNumber);
 
         return initializeCommon19(context, fileno, modeValue, options);
     }
 
     // No encoding processing
     protected IOOptions parseIOOptions(IRubyObject arg) {
         Ruby runtime = getRuntime();
 
         if (arg instanceof RubyFixnum) return newIOOptions(runtime, (int) RubyFixnum.fix2long(arg));
 
         return newIOOptions(runtime, newModeFlags(runtime, arg.convertToString().toString()));
     }
 
     // Encoding processing
     protected IOOptions parseIOOptions19(IRubyObject arg) {
         Ruby runtime = getRuntime();
 
         if (arg instanceof RubyFixnum) return newIOOptions(runtime, (int) RubyFixnum.fix2long(arg));
 
         String modeString = arg.convertToString().toString();
         try {
             return new IOOptions(runtime, modeString);
         } catch (InvalidValueException ive) {
             throw runtime.newArgumentError("invalid access mode " + modeString);
         }
     }
 
     @JRubyMethod(required = 1, optional = 1, visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         Ruby runtime = getRuntime();
         int argCount = args.length;
         IOOptions ioOptions;
         
         int fileno = RubyNumeric.fix2int(args[0]);
         
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(runtime.getFilenoExtMap(fileno));
             
             if (descriptor == null) {
                 throw runtime.newErrnoEBADFError();
             }
             
             descriptor.checkOpen();
             
             if (argCount == 2) {
                 if (args[1] instanceof RubyFixnum) {
                     ioOptions = newIOOptions(runtime, RubyFixnum.fix2long(args[1]));
                 } else {
                     ioOptions = newIOOptions(runtime, args[1].convertToString().toString());
                 }
             } else {
                 // use original modes
                 ioOptions = newIOOptions(runtime, descriptor.getOriginalModes());
             }
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(runtime, false);
             }
 
             openFile.setMode(ioOptions.getModeFlags().getOpenFileFlags());
         
             openFile.setMainStream(fdopen(descriptor, ioOptions.getModeFlags()));
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         }
         
         return this;
     }
     
     protected Stream fdopen(ChannelDescriptor existingDescriptor, ModeFlags modes) {
         Ruby runtime = getRuntime();
 
         // See if we already have this descriptor open.
         // If so then we can mostly share the handler (keep open
         // file, but possibly change the mode).
         
         if (existingDescriptor == null) {
             // redundant, done above as well
             
             // this seems unlikely to happen unless it's a totally bogus fileno
             // ...so do we even need to bother trying to create one?
             
             // IN FACT, we should probably raise an error, yes?
             throw runtime.newErrnoEBADFError();
             
 //            if (mode == null) {
 //                mode = "r";
 //            }
 //            
 //            try {
 //                openFile.setMainStream(streamForFileno(getRuntime(), fileno));
 //            } catch (BadDescriptorException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            } catch (IOException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            }
 //            //modes = new IOModes(getRuntime(), mode);
 //            
 //            registerStream(openFile.getMainStream());
         } else {
             // We are creating a new IO object that shares the same
             // IOHandler (and fileno).
             try {
                 return ChannelStream.fdopen(runtime, existingDescriptor, modes);
             } catch (InvalidValueException ive) {
                 throw runtime.newErrnoEINVALError();
             }
         }
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject external_encoding(ThreadContext context) {
         EncodingService encodingService = context.runtime.getEncodingService();
         
         if (writeEncoding != null) return encodingService.getEncoding(writeEncoding);
         
         if (openFile.isWritable()) {
             return readEncoding == null ? context.runtime.getNil() : encodingService.getEncoding(readEncoding);
         }
         
         return encodingService.getEncoding(getReadEncoding(context.runtime));
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject internal_encoding(ThreadContext context) {
         if (writeEncoding == null) return context.runtime.getNil();
         
         return context.runtime.getEncodingService().getEncoding(getReadEncoding(context.runtime));
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingObj) {
         setEncoding(context, encodingObj, context.runtime.getNil(), null);
 
         return context.runtime.getNil();
     }
     
     // mri: io_encoding_set
     protected void setEncoding(ThreadContext context, IRubyObject external, IRubyObject internal, IRubyObject options) {        
         if (!internal.isNil()) {
             Encoding enc;
             Encoding enc2 = EncodingUtils.toEncoding(context, external);
             
             if (internal instanceof RubyString) {
                 RubyString internalAsString = (RubyString) internal;
                 
                 // No encoding '-'
                 if (internalAsString.size() == 1 && internalAsString.asJavaString().equals("-")) {
                     enc = enc2;
                     enc2 = null;
                 } else {
                     enc = EncodingUtils.toEncoding(context, internalAsString);
                 }
                 
                 if (enc == enc2) {
                     context.runtime.getWarnings().warn("Ignoring internal encoding " + 
                             enc + ": it is identical to external encoding " + enc2);
                     enc2 = null;
                 }
             } else {
                 enc = EncodingUtils.toEncoding(context, internal);
 
                 // FIXME: missing rb_econv_prepare_options
                 if (enc2 == enc) {
                     context.runtime.getWarnings().warn("Ignoring internal encoding " + 
                             enc2 + ": it is identical to external encoding " + enc);
                     enc2 = null;
                 }
             }
             transcodingActions = CharsetTranscoder.getCodingErrorActions(context, options);
             setReadEncoding(enc);
             setWriteEncoding(enc2);
         } else {
             if (external.isNil()) {
                 EncodingUtils.setupReadWriteEncodings(context, this, null, null);
             } else {
                 if (external instanceof RubyString) {
                     RubyString externalAsString = (RubyString) external;
                     
                     EncodingUtils.parseModeEncoding(context, this, externalAsString.asJavaString());
                     // FIXME: missing rb_econv_prepare_options
                 } else {
                     Encoding enc = EncodingUtils.toEncoding(context, external);
                     
                     EncodingUtils.setupReadWriteEncodings(context, this, null, enc);
                 }
                 transcodingActions = CharsetTranscoder.getCodingErrorActions(context, options);
             }
         }
 
         validateEncodingBinmode();
         clearCodeConversion();
     }
     
     private void validateEncodingBinmode() {
         if (openFile.isReadable() && writeEncoding == null && 
                 !openFile.isBinmode() && readEncoding != null && !readEncoding.isAsciiCompatible()) {
             throw getRuntime().newArgumentError("ASCII incompatible encoding needs binmode");
         }
         
         // FIXME: Replace false with ecflags equiv when impl'd
         if (openFile.isBinmode() && false) { // DEFAULT_TEXTMODE & ECONV_DEC_MASK w/ ecflags
             openFile.setTextMode();
         }
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding) {
         if (internalEncoding instanceof RubyHash) {
             setEncoding(context, encodingString, context.runtime.getNil(), internalEncoding);            
         } else {
             setEncoding(context, encodingString, internalEncoding, null);
         }
 
         return context.runtime.getNil();
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding, IRubyObject options) {
         setEncoding(context, encodingString, internalEncoding, options);
 
         return context.runtime.getNil();
     }
 
     @JRubyMethod(required = 1, rest = true, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.runtime;
         RubyClass klass = (RubyClass)recv;
         
         RubyIO io = (RubyIO)klass.newInstance(context, args, block);
 
         if (block.isGiven()) {
             try {
                 return block.yield(context, io);
             } finally {
                 try {
                     io.getMetaClass().finvoke(context, io, "close", IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
                 } catch (RaiseException re) {
                     RubyException rubyEx = re.getException();
                     if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                         // MRI behavior: swallow StandardErorrs
                         runtime.getGlobalVariables().clear("$!");
                     } else {
                         throw re;
                     }
                 }
             }
         }
 
         return io;
     }
 
     @JRubyMethod(required = 1, optional = 2, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject sysopen(IRubyObject recv, IRubyObject[] args, Block block) {
         StringSupport.checkStringSafety(recv.getRuntime(), args[0]);
         IRubyObject pathString = args[0].convertToString();
         return sysopenCommon(recv, args, block, pathString);
     }
     
     @JRubyMethod(name = "sysopen", required = 1, optional = 2, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject sysopen19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         StringSupport.checkStringSafety(context.runtime, args[0]);
 
         return sysopenCommon(recv, args, block, RubyFile.get_path(context, args[0]));
     }
 
     private static IRubyObject sysopenCommon(IRubyObject recv, IRubyObject[] args, Block block, IRubyObject pathString) {
         Ruby runtime = recv.getRuntime();
         String path = pathString.toString();
 
         IOOptions modes;
         int perms = -1; // -1 == don't set permissions
 
         if (args.length > 1 && !args[1].isNil()) {
             IRubyObject modeString = args[1].convertToString();
             modes = newIOOptions(runtime, modeString.toString());
         } else {
             modes = newIOOptions(runtime, "r");
         }
 
         if (args.length > 2 && !args[2].isNil()) {
             RubyInteger permsInt =
                 args.length >= 3 ? args[2].convertToInteger() : null;
             perms = RubyNumeric.fix2int(permsInt);
         }
 
         int fileno = -1;
         try {
             ChannelDescriptor descriptor =
                 ChannelDescriptor.open(runtime.getCurrentDirectory(),
                                        path, modes.getModeFlags(), perms, runtime.getPosix(),
                                        runtime.getJRubyClassLoader());
             // always a new fileno, so ok to use internal only
             fileno = descriptor.getFileno();
         }
         catch (FileNotFoundException fnfe) {
             throw runtime.newErrnoENOENTError(path);
         } catch (DirectoryAsFileException dafe) {
             throw runtime.newErrnoEISDirError(path);
         } catch (FileExistsException fee) {
             throw runtime.newErrnoEEXISTError(path);
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
         }
         return runtime.newFixnum(fileno);
     }
 
     public boolean isAutoclose() {
         return openFile.isAutoclose();
     }
 
     public void setAutoclose(boolean autoclose) {
         openFile.setAutoclose(autoclose);
     }
 
     @JRubyMethod(name = "autoclose?", compat = RUBY1_9)
     public IRubyObject autoclose(ThreadContext context) {
         return context.runtime.newBoolean(isAutoclose());
     }
 
     @JRubyMethod(name = "autoclose=", compat = RUBY1_9)
     public IRubyObject autoclose_set(ThreadContext context, IRubyObject autoclose) {
         setAutoclose(autoclose.isTrue());
         return context.nil;
     }
 
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
         if (isClosed()) throw getRuntime().newIOError("closed stream");
 
         setAscii8bitBinmode();
 
         // missing logic:
         // write_io = GetWriteIO(io);
         // if (write_io != io)
         //     rb_io_ascii8bit_binmode(write_io);
 
         return this;
     }
 
     @JRubyMethod(name = "binmode?", compat = RUBY1_9)
     public IRubyObject op_binmode(ThreadContext context) {
         return RubyBoolean.newBoolean(context.runtime, openFile.isBinmode());
     }
 
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(ThreadContext context, IRubyObject obj) {
        Ruby runtime = context.runtime;
         
         try {
             RubyString string = obj.asString();
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
             
             Stream writeStream = myOpenFile.getWriteStream();
             
diff --git a/src/org/jruby/RubyInteger.java b/src/org/jruby/RubyInteger.java
index c190e10e54..6ccf90d229 100644
--- a/src/org/jruby/RubyInteger.java
+++ b/src/org/jruby/RubyInteger.java
@@ -1,515 +1,509 @@
 /*
  **** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.util.Numeric.checkInteger;
 import static org.jruby.util.Numeric.f_gcd;
 import static org.jruby.util.Numeric.f_lcm;
 
 import org.jcodings.Encoding;
 import org.jcodings.exception.EncodingException;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.Numeric;
 
 /** Implementation of the Integer class.
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Integer", parent="Numeric", include="Precision")
 public abstract class RubyInteger extends RubyNumeric { 
 
     public static RubyClass createIntegerClass(Ruby runtime) {
         RubyClass integer = runtime.defineClass("Integer", runtime.getNumeric(),
                 ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setInteger(integer);
 
         integer.index = ClassIndex.INTEGER;
         integer.setReifiedClass(RubyInteger.class);
         
-        integer.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyInteger;
-            }
-
-        };
+        integer.kindOf = new RubyModule.JavaClassKindOf(RubyInteger.class);
 
         integer.getSingletonClass().undefineMethod("new");
 
         if (!runtime.is1_9()) {
             integer.includeModule(runtime.getPrecision());
         }
 
         integer.defineAnnotatedMethods(RubyInteger.class);
         
         return integer;
     }
 
     public RubyInteger(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
 
     public RubyInteger(RubyClass rubyClass) {
         super(rubyClass);
     }
     
     public RubyInteger(Ruby runtime, RubyClass rubyClass, boolean useObjectSpace) {
         super(runtime, rubyClass, useObjectSpace);
     }     
 
     @Deprecated
     public RubyInteger(Ruby runtime, RubyClass rubyClass, boolean useObjectSpace, boolean canBeTainted) {
         super(runtime, rubyClass, useObjectSpace, canBeTainted);
     }     
 
     @Override
     public RubyInteger convertToInteger() {
     	return this;
     }
 
     // conversion
     protected RubyFloat toFloat() {
         return RubyFloat.newFloat(getRuntime(), getDoubleValue());
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** int_int_p
      * 
      */
     @Override
     @JRubyMethod(name = "integer?")
     public IRubyObject integer_p() {
         return getRuntime().getTrue();
     }
 
     /** int_upto
      * 
      */
     @JRubyMethod
     public IRubyObject upto(ThreadContext context, IRubyObject to, Block block) {
         if (block.isGiven()) {
             if (this instanceof RubyFixnum && to instanceof RubyFixnum) {
                 fixnumUpto(context, ((RubyFixnum)this).getLongValue(), ((RubyFixnum)to).getLongValue(), block);
             } else {
                 duckUpto(context, this, to, block);
             }
             return this;
         } else {
             return enumeratorize(context.runtime, this, "upto", to);
         }
     }
 
     private static void fixnumUpto(ThreadContext context, long from, long to, Block block) {
         // We must avoid "i++" integer overflow when (to == Long.MAX_VALUE).
         Ruby runtime = context.runtime;
         if (block.getBody().getArgumentType() == BlockBody.ZERO_ARGS) {
             IRubyObject nil = runtime.getNil();
             long i;
             for (i = from; i < to; i++) {
                 block.yield(context, nil);
             }
             if (i <= to) {
                 block.yield(context, nil);
             }
         } else {
             long i;
             for (i = from; i < to; i++) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
             if (i <= to) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
         }
     }
 
     private static void duckUpto(ThreadContext context, IRubyObject from, IRubyObject to, Block block) {
         Ruby runtime = context.runtime;
         IRubyObject i = from;
         RubyFixnum one = RubyFixnum.one(runtime);
         while (true) {
             if (i.callMethod(context, ">", to).isTrue()) {
                 break;
             }
             block.yield(context, i);
             i = i.callMethod(context, "+", one);
         }
     }
 
     /** int_downto
      * 
      */
     // TODO: Make callCoerced work in block context...then fix downto, step, and upto.
     @JRubyMethod
     public IRubyObject downto(ThreadContext context, IRubyObject to, Block block) {
         if (block.isGiven()) {
             if (this instanceof RubyFixnum && to instanceof RubyFixnum) {
                 fixnumDownto(context, ((RubyFixnum)this).getLongValue(), ((RubyFixnum)to).getLongValue(), block);
             } else {
                 duckDownto(context, this, to, block);
             }
             return this;
         } else {
             return enumeratorize(context.runtime, this, "downto", to);
         }
     }
 
     private static void fixnumDownto(ThreadContext context, long from, long to, Block block) {
         // We must avoid "i--" integer overflow when (to == Long.MIN_VALUE).
         Ruby runtime = context.runtime;
         if (block.getBody().getArgumentType() == BlockBody.ZERO_ARGS) {
             IRubyObject nil = runtime.getNil();
             long i;
             for (i = from; i > to; i--) {
                 block.yield(context, nil);
             }
             if (i >= to) {
                 block.yield(context, nil);
             }
         } else {
             long i;
             for (i = from; i > to; i--) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
             if (i >= to) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
         }
     }
 
     private static void duckDownto(ThreadContext context, IRubyObject from, IRubyObject to, Block block) {
         Ruby runtime = context.runtime;
         IRubyObject i = from;
         RubyFixnum one = RubyFixnum.one(runtime);
         while (true) {
             if (i.callMethod(context, "<", to).isTrue()) {
                 break;
             }
             block.yield(context, i);
             i = i.callMethod(context, "-", one);
         }
     }
 
     @JRubyMethod
     public IRubyObject times(ThreadContext context, Block block) {
         if (block.isGiven()) {
             Ruby runtime = context.runtime;
             IRubyObject i = RubyFixnum.zero(runtime);
             RubyFixnum one = RubyFixnum.one(runtime);
             while (true) {
                 if (!i.callMethod(context, "<", this).isTrue()) {
                     break;
                 }
                 block.yield(context, i);
                 i = i.callMethod(context, "+", one);
             }
             return this;
         } else {
             return enumeratorize(context.runtime, this, "times");
         }
     }
 
     /** int_succ
      * 
      */
     @JRubyMethod(name = {"succ", "next"})
     public IRubyObject succ(ThreadContext context) {
         if (this instanceof RubyFixnum) {
             return ((RubyFixnum) this).op_plus_one(context);
         } else {
             return callMethod(context, "+", RubyFixnum.one(context.runtime));
         }
     }
 
     static final ByteList[] SINGLE_CHAR_BYTELISTS;
     static final ByteList[] SINGLE_CHAR_BYTELISTS19;
     static {
         SINGLE_CHAR_BYTELISTS = new ByteList[256];
         SINGLE_CHAR_BYTELISTS19 = new ByteList[256];
         for (int i = 0; i < 256; i++) {
             ByteList usascii = new ByteList(new byte[]{(byte)i}, false);
             SINGLE_CHAR_BYTELISTS[i] = usascii;
             SINGLE_CHAR_BYTELISTS19[i] = i < 0x80 ?
                 new ByteList(new byte[]{(byte)i}, USASCIIEncoding.INSTANCE)
                 :
                 new ByteList(
                     new byte[]{(byte)i},
                     ASCIIEncoding.INSTANCE);
         }
     }
 
     /** int_chr
      * 
      */
     @JRubyMethod(name = "chr", compat = CompatVersion.RUBY1_8)
     public RubyString chr(ThreadContext context) {
         Ruby runtime = context.runtime;
         long value = getLongValue();
         if (value < 0 || value > 0xff) throw runtime.newRangeError(this.toString() + " out of char range");
         return RubyString.newStringShared(runtime, SINGLE_CHAR_BYTELISTS[(int)value]);
     }
 
     @JRubyMethod(name = "chr", compat = CompatVersion.RUBY1_9)
     public RubyString chr19(ThreadContext context) {
         Ruby runtime = context.runtime;
         int value = (int)getLongValue();
         if (value >= 0 && value <= 0xFF) {
             ByteList bytes = SINGLE_CHAR_BYTELISTS19[value];
             return RubyString.newStringShared(runtime, bytes, bytes.getEncoding());
         } else {
             Encoding enc = runtime.getDefaultInternalEncoding();
             if (value > 0xFF && (enc == null || enc == ASCIIEncoding.INSTANCE)) {
                 throw runtime.newRangeError(this.toString() + " out of char range");
             } else {
                 if (enc == null) enc = USASCIIEncoding.INSTANCE;
                 return RubyString.newStringNoCopy(runtime, fromEncodedBytes(runtime, enc, (int)value), enc, 0);
             }
         }
     }
 
     @JRubyMethod(name = "chr", compat = CompatVersion.RUBY1_9)
     public RubyString chr19(ThreadContext context, IRubyObject arg) {
         Ruby runtime = context.runtime;
         long value = getLongValue();
         Encoding enc;
         if (arg instanceof RubyEncoding) {
             enc = ((RubyEncoding)arg).getEncoding();
         } else {
             enc =  arg.convertToString().toEncoding(runtime);
         }
         if (enc == ASCIIEncoding.INSTANCE && value >= 0x80) {
             return chr19(context);
         }
         return RubyString.newStringNoCopy(runtime, fromEncodedBytes(runtime, enc, (int)value), enc, 0);
     }
 
     private ByteList fromEncodedBytes(Ruby runtime, Encoding enc, int value) {
         int n;
         try {
             n = value < 0 ? 0 : enc.codeToMbcLength(value);
         } catch (EncodingException ee) {
             n = 0;
         }
 
         if (n <= 0) throw runtime.newRangeError(this.toString() + " out of char range");
         
         ByteList bytes = new ByteList(n);
         
         try {
             enc.codeToMbc(value, bytes.getUnsafeBytes(), 0);
         } catch (EncodingException e) {
             throw runtime.newRangeError("invalid codepoint " + String.format("0x%x in ", value) + enc.getCharsetName());
         }
         bytes.setRealSize(n);
         return bytes;
     }
 
     /** int_ord
      * 
      */
     @JRubyMethod(name = "ord")
     public IRubyObject ord(ThreadContext context) {
         return this;
     }
 
     /** int_to_i
      * 
      */
     @JRubyMethod(name = {"to_i", "to_int", "floor", "ceil", "truncate"})
     public IRubyObject to_i() {
         return this;
     }
 
     @Override
     @JRubyMethod(name = "round", compat = CompatVersion.RUBY1_8)
     public IRubyObject round() {
         return this;
     }
 
     @JRubyMethod(name = "round", compat = CompatVersion.RUBY1_9)
     public IRubyObject round19() {
         return this;
     }
 
     @JRubyMethod(name = "round", compat = CompatVersion.RUBY1_9)
     public IRubyObject round19(ThreadContext context, IRubyObject arg) {
         int ndigits = RubyNumeric.num2int(arg);
         if (ndigits > 0) return RubyKernel.new_float(this, this);
         if (ndigits == 0) return this;
         Ruby runtime = context.runtime;
         
         long bytes = (this instanceof RubyFixnum) ? 8 : RubyFixnum.fix2long(callMethod("size"));
         /* If 10**N/2 > this, return 0 */
         /* We have log_256(10) > 0.415241 and log_256(1/2)=-0.125 */
         if (-0.415241 * ndigits - 0.125 > bytes) {
             return RubyFixnum.zero(runtime);
         }
         
         IRubyObject f = Numeric.int_pow(context, 10, -ndigits);
 
         if (this instanceof RubyFixnum && f instanceof RubyFixnum) {
             long x = ((RubyFixnum)this).getLongValue();
             long y = ((RubyFixnum)f).getLongValue();
             boolean neg = x < 0;
             if (neg) x = -x;
             x = (x + y / 2) / y * y;
             if (neg) x = -x;
             return RubyFixnum.newFixnum(runtime, x);
         } else if (f instanceof RubyFloat) {
             return RubyFixnum.zero(runtime);
         } else {
             IRubyObject h = f.callMethod(context, "/", RubyFixnum.two(runtime));
             IRubyObject r = callMethod(context, "%", f);
             IRubyObject n = callMethod(context, "-", r);
             String op = callMethod(context, "<", RubyFixnum.zero(runtime)).isTrue() ? "<=" : "<";
             if (!r.callMethod(context, op, h).isTrue()) n = n.callMethod(context, "+", f);
             return n;
         }
     }
 
     /** integer_to_r
      * 
      */
     @JRubyMethod(name = "to_r", compat = CompatVersion.RUBY1_9)
     public IRubyObject to_r(ThreadContext context) {
         return RubyRational.newRationalCanonicalize(context, this);
     }
 
     /** integer_rationalize
      *
      */
     @JRubyMethod(name = "rationalize", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject rationalize(ThreadContext context, IRubyObject[] args) {
         return to_r(context);
     }
     
 
     @JRubyMethod(name = "odd?")
     public RubyBoolean odd_p(ThreadContext context) {
         Ruby runtime = context.runtime;
         if (callMethod(context, "%", RubyFixnum.two(runtime)) != RubyFixnum.zero(runtime)) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = "even?")
     public RubyBoolean even_p(ThreadContext context) {
         Ruby runtime = context.runtime;
         if (callMethod(context, "%", RubyFixnum.two(runtime)) == RubyFixnum.zero(runtime)) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = "pred")
     public IRubyObject pred(ThreadContext context) {
         return callMethod(context, "-", RubyFixnum.one(context.runtime));
     }
 
     /** rb_gcd
      * 
      */
     @JRubyMethod(name = "gcd", compat = CompatVersion.RUBY1_9)
     public IRubyObject gcd(ThreadContext context, IRubyObject other) {
         checkInteger(context, other);
         return f_gcd(context, this, RubyRational.intValue(context, other));
     }    
 
     /** rb_lcm
      * 
      */
     @JRubyMethod(name = "lcm", compat = CompatVersion.RUBY1_9)
     public IRubyObject lcm(ThreadContext context, IRubyObject other) {
         checkInteger(context, other);
         return f_lcm(context, this, RubyRational.intValue(context, other));
     }    
 
     /** rb_gcdlcm
      * 
      */
     @JRubyMethod(name = "gcdlcm", compat = CompatVersion.RUBY1_9)
     public IRubyObject gcdlcm(ThreadContext context, IRubyObject other) {
         checkInteger(context, other);
         other = RubyRational.intValue(context, other);
         return context.runtime.newArray(f_gcd(context, this, other), f_lcm(context, this, other));
     }
 
     @Override
     @JRubyMethod(name = "numerator", compat = CompatVersion.RUBY1_9)
     public IRubyObject numerator(ThreadContext context) {
         return this;
     }
 
     @Override
     @JRubyMethod(name = "denominator", compat = CompatVersion.RUBY1_9)
     public IRubyObject denominator(ThreadContext context) {
         return RubyFixnum.one(context.runtime);
     }
 
     /*  ================
      *  Singleton Methods
      *  ================ 
      */
 
     /** rb_int_induced_from
      * 
      */
     @JRubyMethod(name = "induced_from", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject induced_from(ThreadContext context, IRubyObject recv, IRubyObject other) {
         if (other instanceof RubyFixnum || other instanceof RubyBignum) {
             return other;
         } else if (other instanceof RubyFloat || other instanceof RubyRational) {
             return other.callMethod(context, "to_i");
         } else {
             throw recv.getRuntime().newTypeError(
                     "failed to convert " + other.getMetaClass().getName() + " into Integer");
         }
     }
 }
diff --git a/src/org/jruby/RubyMatchData.java b/src/org/jruby/RubyMatchData.java
index a4e16d3a90..1ada3520aa 100644
--- a/src/org/jruby/RubyMatchData.java
+++ b/src/org/jruby/RubyMatchData.java
@@ -1,691 +1,686 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.util.Arrays;
 import java.util.Iterator;
 
 import org.jcodings.Encoding;
 import org.joni.NameEntry;
 import org.joni.Regex;
 import org.joni.Region;
 import org.joni.exception.JOniException;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.StringSupport;
 import static org.jruby.CompatVersion.*;
 
 /**
  * @author olabini
  */
 @JRubyClass(name="MatchData")
 public class RubyMatchData extends RubyObject {
     Region regs;        // captures
     int begin, end;     // begin and end are used when not groups defined
     RubyString str;     // source string
     Regex pattern;
     RubyRegexp regexp;
     boolean charOffsetUpdated;
     Region charOffsets;
 
     public static RubyClass createMatchDataClass(Ruby runtime) {
         RubyClass matchDataClass = runtime.defineClass("MatchData", runtime.getObject(), MATCH_DATA_ALLOCATOR);
         runtime.setMatchData(matchDataClass);
 
         matchDataClass.index = ClassIndex.MATCHDATA;
         matchDataClass.setReifiedClass(RubyMatchData.class);
         
         runtime.defineGlobalConstant("MatchingData", matchDataClass);
-        matchDataClass.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyMatchData;
-            }
-        };
+        matchDataClass.kindOf = new RubyModule.JavaClassKindOf(RubyMatchData.class);
 
         matchDataClass.getMetaClass().undefineMethod("new");
         matchDataClass.defineAnnotatedMethods(RubyMatchData.class);
         return matchDataClass;
     }
 
     private static ObjectAllocator MATCH_DATA_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyMatchData(runtime, klass);
         }
     };
 
     public RubyMatchData(Ruby runtime) {
         super(runtime, runtime.getMatchData());
     }
 
     public RubyMatchData(Ruby runtime, RubyClass metaClass) {
         super(runtime, metaClass);
     }
 
     @Override
     public void copySpecialInstanceVariables(IRubyObject clone) {
         RubyMatchData match = (RubyMatchData)clone;
         match.regs = regs;
         match.begin = begin;
         match.end = end;
         match.pattern = pattern;
         match.regexp = regexp;
         match.charOffsetUpdated = charOffsetUpdated;
         match.charOffsets = charOffsets;
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.MATCHDATA;
     }
 
     private static final class Pair implements Comparable<Pair> {
         int bytePos, charPos;
         public int compareTo(Pair pair) {
             return bytePos - pair.bytePos;
         }
     }
 
     private void updatePairs(ByteList value, Encoding encoding, Pair[] pairs) {
         Arrays.sort(pairs);
 
         int length = pairs.length;
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int s = p;
         int c = 0;
         
         for (int i = 0; i < length; i++) {
             int q = s + pairs[i].bytePos;
             c += StringSupport.strLength(encoding, bytes, p, q);
             pairs[i].charPos = c;
             p = q;
         }
     }
 
     private void updateCharOffsetOnlyOneReg(ByteList value, Encoding encoding) {
         if (charOffsets == null || charOffsets.numRegs < 1) charOffsets = new Region(1);
         
         if (encoding.maxLength() == 1) {
             charOffsets.beg[0] = begin;
             charOffsets.end[0] = end;
             charOffsetUpdated = true;
             return;
         }
 
         Pair[] pairs = new Pair[2];
         pairs[0] = new Pair();
         pairs[0].bytePos = begin;
         pairs[1] = new Pair();
         pairs[1].bytePos = end;
 
         updatePairs(value, encoding, pairs);
 
         Pair key = new Pair();
         key.bytePos = begin;
         charOffsets.beg[0] = pairs[Arrays.binarySearch(pairs, key)].charPos;
         key.bytePos = end;
         charOffsets.end[0] = pairs[Arrays.binarySearch(pairs, key)].charPos;        
     }
 
     private void updateCharOffsetManyRegs(ByteList value, Encoding encoding) {
         int numRegs = regs.numRegs;
 
         if (charOffsets == null || charOffsets.numRegs < numRegs) charOffsets = new Region(numRegs);
         
         if (encoding.maxLength() == 1) {
             for (int i = 0; i < numRegs; i++) {
                 charOffsets.beg[i] = regs.beg[i];
                 charOffsets.end[i] = regs.end[i];
             }
             return;
         }
 
         Pair[] pairs = new Pair[numRegs * 2];
         for (int i = 0; i < pairs.length; i++) pairs[i] = new Pair();
 
         int numPos = 0;
         for (int i = 0; i < numRegs; i++) {
             if (regs.beg[i] < 0) continue;
             pairs[numPos++].bytePos = regs.beg[i];
             pairs[numPos++].bytePos = regs.end[i];
         }
 
         updatePairs(value, encoding, pairs);
 
         Pair key = new Pair();
         for (int i = 0; i < regs.numRegs; i++) {
             if (regs.beg[i] < 0) {
                 charOffsets.beg[i] = charOffsets.end[i] = -1;
                 continue;
             }
             key.bytePos = regs.beg[i];
             charOffsets.beg[i] = pairs[Arrays.binarySearch(pairs, key)].charPos;
             key.bytePos = regs.end[i];
             charOffsets.end[i] = pairs[Arrays.binarySearch(pairs, key)].charPos;
         }        
     }
 
     private void updateCharOffset() {
         if (charOffsetUpdated) return;
 
         ByteList value = str.getByteList();
         Encoding enc = value.getEncoding();
 
         if (regs == null) {
             updateCharOffsetOnlyOneReg(value, enc);
         } else {
             updateCharOffsetManyRegs(value, enc);
         }
 
         charOffsetUpdated = true;
     }
 
     private static final int MATCH_BUSY = USER2_F;
 
     // rb_match_busy
     public final void use() {
         flags |= MATCH_BUSY; 
     }
 
     public final boolean used() {
         return (flags & MATCH_BUSY) != 0;
     }
 
     void check() {
         if (str == null) throw getRuntime().newTypeError("uninitialized Match");
     }
 
     private void checkLazyRegexp() {
         if (regexp == null) regexp = RubyRegexp.newRegexp(getRuntime(), (ByteList)pattern.getUserObject(), pattern);
     }
     
     // FIXME: We should have a better way of using the proper method based
     // on version as a general solution...
     private RubyString makeShared(Ruby runtime, RubyString str, int begin, int length) {
         if (runtime.is1_9()) {
             return str.makeShared19(runtime, begin, length);
         } else {
             return str.makeShared(runtime, begin, length);
         }
     }
 
     private RubyArray match_array(Ruby runtime, int start) {
         check();
         if (regs == null) {
             if (start != 0) return runtime.newEmptyArray();
             if (begin == -1) {
                 return runtime.newArray(runtime.getNil());
             } else {
                 RubyString ss = makeShared(runtime, str, begin, end - begin);
                 if (isTaint()) ss.setTaint(true);
                 return runtime.newArray(ss);
             }
         } else {
             RubyArray arr = runtime.newArray(regs.numRegs - start);
             for (int i=start; i<regs.numRegs; i++) {
                 if (regs.beg[i] == -1) {
                     arr.append(runtime.getNil());
                 } else {
                     RubyString ss = makeShared(runtime, str, regs.beg[i], regs.end[i] - regs.beg[i]);                   
                     if (isTaint()) ss.setTaint(true); 
                     arr.append(ss);
                 }
             }
             return arr;
         }
         
     }
 
     public IRubyObject group(long n) {
         return RubyRegexp.nth_match((int)n, this);
     }
 
     public IRubyObject group(int n) {
         return RubyRegexp.nth_match(n, this);
     }
 
     // This returns a list of values in the order the names are defined (named capture local var
     // feature uses this).
     public IRubyObject[] getNamedBackrefValues(Ruby runtime) {
         if (pattern.numberOfNames() == 0) return NULL_ARRAY;
 
         IRubyObject[] values = new IRubyObject[pattern.numberOfNames()];
 
         int j = 0;
         for (Iterator<NameEntry> i = pattern.namedBackrefIterator(); i.hasNext();) {
             NameEntry e = i.next();
             int[] refs = e.getBackRefs();
             int length = refs.length;
 
             values[j++] = length == 0 ? runtime.getNil() : RubyRegexp.nth_match(refs[length - 1], this);
         }
 
         return values;
     }
 
     @JRubyMethod(name = "inspect")
     @Override
     public IRubyObject inspect() {
         if (str == null) return anyToString();
 
         Ruby runtime = getRuntime();
         RubyString result = runtime.newString();
         result.cat((byte)'#').cat((byte)'<');
         result.append(getMetaClass().getRealClass().to_s());
 
         NameEntry[]names = new NameEntry[regs == null ? 1 : regs.numRegs];
 
         if (pattern.numberOfNames() > 0) {
             for (Iterator<NameEntry> i = pattern.namedBackrefIterator(); i.hasNext();) {
                 NameEntry e = i.next();
                 for (int num : e.getBackRefs()) names[num] = e;
             }
         }
 
         for (int i=0; i<names.length; i++) {
             result.cat((byte)' ');
             if (i > 0) {
                 NameEntry e = names[i];
                 if (e != null) {
                     result.cat(e.name, e.nameP, e.nameEnd - e.nameP);
                 } else {
                     result.cat((byte)('0' + i));
                 }
                 result.cat((byte)':');
             }
             IRubyObject v = RubyRegexp.nth_match(i, this);
             if (v.isNil()) {
                 result.cat("nil".getBytes());
             } else {
                 result.append(runtime.is1_9() ? ((RubyString)v).inspect19() : ((RubyString)v).inspect());
             }
         }
 
         return result.cat((byte)'>');
     }
 
     @JRubyMethod(name = "regexp", compat = CompatVersion.RUBY1_9)
     public IRubyObject regexp(ThreadContext context, Block block) {
         check();
         checkLazyRegexp();
         return regexp;
     }
 
     @JRubyMethod(name = "names", compat = CompatVersion.RUBY1_9)
     public IRubyObject names(ThreadContext context, Block block) {
         check();
         checkLazyRegexp();
         return regexp.names(context);
     }
 
     /** match_to_a
      *
      */
     @JRubyMethod(name = "to_a")
     @Override
     public RubyArray to_a() {
         return match_array(getRuntime(), 0);
     }
 
     @JRubyMethod(name = "values_at", rest = true)
     public IRubyObject values_at(IRubyObject[] args) {
         return to_a().values_at(args);
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_8)
     public IRubyObject select(ThreadContext context, Block block) {
         Ruby runtime = context.runtime;
         final RubyArray result;
         if (regs == null) {
             if (begin < 0) return runtime.newEmptyArray();
             IRubyObject s = str.substr(runtime, begin, end - begin);
             s.setTaint(isTaint());
             result = block.yield(context, s).isTrue() ? runtime.newArray(s) : runtime.newEmptyArray();
         } else {
             result = runtime.newArray();
             boolean taint = isTaint();
             for (int i = 0; i < regs.numRegs; i++) {
                 IRubyObject s = str.substr(runtime, regs.beg[i], regs.end[i] - regs.beg[i]);
                 if (taint) s.setTaint(true);
                 if (block.yield(context, s).isTrue()) result.append(s);
             }
         }
         return result;
     }
 
     /** match_captures
      *
      */
     @JRubyMethod(name = "captures")
     public IRubyObject captures(ThreadContext context) {
         return match_array(context.runtime, 1);
     }
 
     private int nameToBackrefNumber(RubyString str) {
         ByteList value = str.getByteList();
         try {
             return pattern.nameToBackrefNumber(value.getUnsafeBytes(), value.getBegin(), value.getBegin() + value.getRealSize(), regs);
         } catch (JOniException je) {
             throw getRuntime().newIndexError(je.getMessage());
         }
     }
 
     public final int backrefNumber(IRubyObject obj) {
         check();
         if (obj instanceof RubySymbol) {
             return nameToBackrefNumber((RubyString)((RubySymbol)obj).id2name());
         } else if (obj instanceof RubyString) {
             return nameToBackrefNumber((RubyString)obj);
         } else {
             return RubyNumeric.num2int(obj);
         }
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * @deprecated Use the versions with zero, one, or two args.
      */
     public IRubyObject op_aref(IRubyObject[] args) {
         switch (args.length) {
         case 1:
             return op_aref(args[0]);
         case 2:
             return op_aref(args[0], args[1]);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 1, 2);
             return null; // not reached
         }
     }
 
     /** match_aref
     *
     */
     @JRubyMethod(name = "[]")
     public IRubyObject op_aref(IRubyObject idx) {
         check();
         if (!(idx instanceof RubyFixnum) || ((RubyFixnum)idx).getLongValue() < 0) {
             return ((RubyArray)to_a()).aref(idx);
         }
         return RubyRegexp.nth_match(RubyNumeric.fix2int(idx), this);
     }
 
     /** match_aref
      *
      */
     @JRubyMethod(name = "[]")
     public IRubyObject op_aref(IRubyObject idx, IRubyObject rest) {
         if (!rest.isNil() || !(idx instanceof RubyFixnum) || ((RubyFixnum)idx).getLongValue() < 0) {
             return ((RubyArray)to_a()).aref(idx, rest);
         }
         return RubyRegexp.nth_match(RubyNumeric.fix2int(idx), this);
     }
 
     /** match_aref
      *
      */
     @JRubyMethod(name = "[]", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_aref19(IRubyObject idx) {
         check();
         IRubyObject result = op_arefCommon(idx);
         return result == null ? ((RubyArray)to_a()).aref19(idx) : result;
     }
 
     /** match_aref
     *
     */
     @JRubyMethod(name = "[]", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_aref19(IRubyObject idx, IRubyObject rest) {
         IRubyObject result;
         return !rest.isNil() || (result = op_arefCommon(idx)) == null ? ((RubyArray)to_a()).aref19(idx, rest) : result;
     }
 
     private IRubyObject op_arefCommon(IRubyObject idx) {
         if (idx instanceof RubyFixnum) {
             int num = RubyNumeric.fix2int(idx);
             if (num >= 0) return RubyRegexp.nth_match(num, this);
         } else {
             if (idx instanceof RubySymbol) {
                 return RubyRegexp.nth_match(nameToBackrefNumber((RubyString)((RubySymbol)idx).id2name()), this);
             } else if (idx instanceof RubyString) {
                 return RubyRegexp.nth_match(nameToBackrefNumber((RubyString)idx), this);
             }
         }
         return null;
     }
 
     /** match_size
      *
      */
     @JRubyMethod(name = {"size", "length"})
     public IRubyObject size(ThreadContext context) {
         check();
         Ruby runtime = context.runtime;
         return regs == null ? RubyFixnum.one(runtime) : RubyFixnum.newFixnum(runtime, regs.numRegs);
     }
 
     /** match_begin
      *
      */
     @JRubyMethod(name = "begin", compat = CompatVersion.RUBY1_8)
     public IRubyObject begin(ThreadContext context, IRubyObject index) {
         int i = RubyNumeric.num2int(index);
         Ruby runtime = context.runtime;
         int b = beginCommon(runtime, i);
         return b < 0 ? runtime.getNil() : RubyFixnum.newFixnum(runtime, b);
     }
 
     @JRubyMethod(name = "begin", compat = CompatVersion.RUBY1_9)
     public IRubyObject begin19(ThreadContext context, IRubyObject index) {
         int i = backrefNumber(index);
         Ruby runtime = context.runtime;
         int b = beginCommon(runtime, i);
         if (b < 0) return runtime.getNil();
         if (!str.singleByteOptimizable()) {
             updateCharOffset();
             b = charOffsets.beg[i];
         }
         return RubyFixnum.newFixnum(runtime, b);
     }
 
     private int beginCommon(Ruby runtime, int i) {
         check();
         if (i < 0 || (regs == null ? 1 : regs.numRegs) <= i) throw runtime.newIndexError("index " + i + " out of matches");
         return regs == null ? begin : regs.beg[i];
     }
 
     /** match_end
      *
      */
     @JRubyMethod(name = "end", compat = CompatVersion.RUBY1_8)
     public IRubyObject end(ThreadContext context, IRubyObject index) {
         int i = RubyNumeric.num2int(index);
         Ruby runtime = context.runtime;
         int e = endCommon(runtime, i);
         return e < 0 ? runtime.getNil() : RubyFixnum.newFixnum(runtime, e);
     }
 
     @JRubyMethod(name = "end", compat = CompatVersion.RUBY1_9)
     public IRubyObject end19(ThreadContext context, IRubyObject index) {
         int i = backrefNumber(index);
         Ruby runtime = context.runtime;
         int e = endCommon(runtime, i);
         if (e < 0) return runtime.getNil();
         if (!str.singleByteOptimizable()) {
             updateCharOffset();
             e = charOffsets.end[i];
         }
         return RubyFixnum.newFixnum(runtime, e);
     }
 
     private int endCommon(Ruby runtime, int i) {
         check();
         if (i < 0 || (regs == null ? 1 : regs.numRegs) <= i) throw runtime.newIndexError("index " + i + " out of matches");
         return regs == null ? end : regs.end[i];
     }
 
     /** match_offset
      *
      */
     @JRubyMethod(name = "offset", compat = CompatVersion.RUBY1_8)
     public IRubyObject offset(ThreadContext context, IRubyObject index) {
         return offsetCommon(context, RubyNumeric.num2int(index), false);
 
     }
 
     @JRubyMethod(name = "offset", compat = CompatVersion.RUBY1_9)
     public IRubyObject offset19(ThreadContext context, IRubyObject index) {
         return offsetCommon(context, backrefNumber(index), true);
     }
 
     private IRubyObject offsetCommon(ThreadContext context, int i, boolean is_19) {
         check();
         Ruby runtime = context.runtime;
         if (i < 0 || (regs == null ? 1 : regs.numRegs) <= i) throw runtime.newIndexError("index " + i + " out of matches");
         int b, e;
         if (regs == null) {
             b = begin;
             e = end;
         } else {
             b = regs.beg[i];
             e = regs.end[i];
         }
         if (b < 0) return runtime.newArray(runtime.getNil(), runtime.getNil());
         if (is_19 && !str.singleByteOptimizable()) {
             updateCharOffset();
             b = charOffsets.beg[i];
             e = charOffsets.end[i];
         }
         return runtime.newArray(RubyFixnum.newFixnum(runtime, b), RubyFixnum.newFixnum(runtime, e));
     }
 
     /** match_pre_match
      *
      */
     @JRubyMethod(name = "pre_match")
     public IRubyObject pre_match(ThreadContext context) {
         check();
         if (begin == -1) {
             return context.runtime.getNil();
         }
         return makeShared(context.runtime, str, 0, begin).infectBy(this);
     }
 
     /** match_post_match
      *
      */
     @JRubyMethod(name = "post_match")
     public IRubyObject post_match(ThreadContext context) {
         check();
         if (begin == -1) {
             return context.runtime.getNil();
         }
         return makeShared(context.runtime, str, end, str.getByteList().length() - end).infectBy(this);
     }
 
     /** match_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     @Override
     public IRubyObject to_s() {
         check();
         IRubyObject ss = RubyRegexp.last_match(this);
         if (ss.isNil()) ss = RubyString.newEmptyString(getRuntime());
         if (isTaint()) ss.setTaint(true);
         return ss;
     }
 
     /** match_string
      *
      */
     @JRubyMethod(name = "string")
     public IRubyObject string() {
         check();
         return str; //str is frozen
     }
 
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original) {
         if (this == original) return this;
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         if ((original instanceof RubyBasicObject) && !((RubyBasicObject)original).instance_of_p(context, getMetaClass()).isTrue()) {
             throw runtime.newTypeError("wrong argument class");
         }
 
         RubyMatchData origMatchData = (RubyMatchData)original;
         str = origMatchData.str;
         regs = origMatchData.regs;
 
         return this;
     }
 
     public boolean equals(Object other) {
         if (this == other) return true;
         if (!(other instanceof RubyMatchData)) return false;
 
         RubyMatchData match = (RubyMatchData)other;
 
         return (this.str == match.str || (this.str != null && this.str.equals(match.str))) &&
                 (this.regexp == match.regexp || (this.regexp != null && this.regexp.equals(match.regexp))) &&
                 (this.charOffsets == match.charOffsets || (this.charOffsets != null && this.charOffsets.equals(match.charOffsets))) &&
                 this.begin == match.begin &&
                 this.end == match.end &&
                 this.charOffsetUpdated == match.charOffsetUpdated;
     }
 
     @JRubyMethod(name = {"eql?", "=="}, required = 1, compat = RUBY1_9)
     @Override
     public IRubyObject eql_p(IRubyObject obj) {
         return getRuntime().newBoolean(equals(obj));
     }
 
     @JRubyMethod(name = "hash", compat = RUBY1_9)
     @Override
     public RubyFixnum hash() {
         check();
         return getRuntime().newFixnum(regexp.hashCode() ^ str.hashCode());
     }
 
 }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index b103145eb9..c93c17cab7 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1152 +1,1147 @@
 /*
  **** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006-2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jruby.internal.runtime.methods.AttrWriterMethod;
 import org.jruby.internal.runtime.methods.AttrReaderMethod;
 import static org.jruby.anno.FrameField.VISIBILITY;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.security.AccessControlException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
 
 import org.jruby.anno.AnnotationBinder;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyConstant;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JavaMethodDescriptor;
 import org.jruby.anno.TypePopulator;
 import org.jruby.ast.Node;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.AliasMethod;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.CacheableMethod;
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.InterpretedMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.ProfilingDynamicMethod;
 import org.jruby.internal.runtime.methods.SynchronizedDynamicMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.runtime.Helpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.callsite.CacheEntry;
 import org.jruby.runtime.callsite.FunctionalCachingCallSite;
 import org.jruby.runtime.ivars.MethodData;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.runtime.opto.Invalidator;
 import org.jruby.runtime.opto.OptoFactory;
 import org.jruby.util.ClassProvider;
 import org.jruby.util.IdUtil;
 import org.jruby.util.cli.Options;
 import org.jruby.util.collections.WeakHashSet;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Module")
 public class RubyModule extends RubyObject {
 
     private static final Logger LOG = LoggerFactory.getLogger("RubyModule");
 
     private static final boolean DEBUG = false;
     protected static final String ERR_INSECURE_SET_CONSTANT  = "Insecure: can't modify constant";
     protected static final String ERR_FROZEN_CONST_TYPE = "class/module ";
     public static final Set<String> SCOPE_CAPTURING_METHODS = new HashSet<String>(Arrays.asList(
             "eval",
             "module_eval",
             "class_eval",
             "instance_eval",
             "module_exec",
             "class_exec",
             "instance_exec",
             "binding",
             "local_variables"
             ));
 
     public static final ObjectAllocator MODULE_ALLOCATOR = new ObjectAllocator() {
         @Override
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyModule(runtime, klass);
         }
     };
     
     public static RubyClass createModuleClass(Ruby runtime, RubyClass moduleClass) {
         moduleClass.index = ClassIndex.MODULE;
         moduleClass.setReifiedClass(RubyModule.class);
-        moduleClass.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyModule;
-            }
-        };
+        moduleClass.kindOf = new RubyModule.JavaClassKindOf(RubyModule.class);
         
         moduleClass.defineAnnotatedMethods(RubyModule.class);
         moduleClass.defineAnnotatedMethods(ModuleKernelMethods.class);
 
         return moduleClass;
     }
 
     public void checkValidBindTargetFrom(ThreadContext context, RubyModule originModule) throws RaiseException {
         if (!this.hasModuleInHierarchy(originModule)) {
             if (originModule instanceof MetaClass) {
                 throw context.runtime.newTypeError("can't bind singleton method to a different class");
             } else {
                 throw context.runtime.newTypeError("bind argument must be an instance of " + originModule.getName());
             }
         }
     }
     
     public static class ModuleKernelMethods {
         @JRubyMethod
         public static IRubyObject autoload(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return RubyKernel.autoload(recv, arg0, arg1);
         }
         
         @JRubyMethod(name = "autoload?")
         public static IRubyObject autoload_p(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
             return RubyKernel.autoload_p(context, recv, arg0);
         }
     }
     
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.MODULE;
     }
 
     @Override
     public boolean isModule() {
         return true;
     }
 
     @Override
     public boolean isClass() {
         return false;
     }
 
     public boolean isSingleton() {
         return false;
     }
 
     public static class KindOf {
         public static final KindOf DEFAULT_KIND_OF = new KindOf();
         public boolean isKindOf(IRubyObject obj, RubyModule type) {
             return obj.getMetaClass().hasModuleInHierarchy(type);
         }
     }
 
 		public static final class JavaClassKindOf extends RubyModule.KindOf {
 			private final Class klass;
 			public JavaClassKindOf(Class klass) { 
 				this.klass = klass;
 			}
 
 			@Override
 			public boolean isKindOf(IRubyObject obj, RubyModule type) {
 				return klass.isInstance(obj);
 			}
 		}
     
     public boolean isInstance(IRubyObject object) {
         return kindOf.isKindOf(object, this);
     }
 
     public Map<String, ConstantEntry> getConstantMap() {
         return constants;
     }
 
     public synchronized Map<String, ConstantEntry> getConstantMapForWrite() {
         return constants == Collections.EMPTY_MAP ? constants = new ConcurrentHashMap<String, ConstantEntry>(4, 0.9f, 1) : constants;
     }
     
     /**
      * AutoloadMap must be accessed after checking ConstantMap. Checking UNDEF value in constantMap works as a guard.
      * For looking up constant, check constantMap first then try to get an Autoload object from autoloadMap.
      * For setting constant, update constantMap first and remove an Autoload object from autoloadMap.
      */
     private Map<String, Autoload> getAutoloadMap() {
         return autoloads;
     }
     
     private synchronized Map<String, Autoload> getAutoloadMapForWrite() {
         return autoloads == Collections.EMPTY_MAP ? autoloads = new ConcurrentHashMap<String, Autoload>(4, 0.9f, 1) : autoloads;
     }
     
     public void addIncludingHierarchy(IncludedModuleWrapper hierarchy) {
         synchronized (getRuntime().getHierarchyLock()) {
             Set<RubyClass> oldIncludingHierarchies = includingHierarchies;
             if (oldIncludingHierarchies == Collections.EMPTY_SET) includingHierarchies = oldIncludingHierarchies = new WeakHashSet(4);
             oldIncludingHierarchies.add(hierarchy);
         }
     }
 
     /** separate path for MetaClass construction
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass, boolean objectSpace) {
         super(runtime, metaClass, objectSpace);
         id = runtime.allocModuleId();
         runtime.addModule(this);
         // if (parent == null) parent = runtime.getObject();
         setFlag(USER7_F, !isClass());
         generationObject = generation = runtime.getNextModuleGeneration();
         if (runtime.getInstanceConfig().isProfiling()) {
             cacheEntryFactory = new ProfilingCacheEntryFactory(NormalCacheEntryFactory);
         } else {
             cacheEntryFactory = NormalCacheEntryFactory;
         }
         
         // set up an invalidator for use in new optimization strategies
         methodInvalidator = OptoFactory.newMethodInvalidator(this);
     }
     
     /** used by MODULE_ALLOCATOR and RubyClass constructors
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
     
     /** standard path for Module construction
      * 
      */
     protected RubyModule(Ruby runtime) {
         this(runtime, runtime.getModule());
     }
 
     public boolean needsImplementer() {
         return getFlag(USER7_F);
     }
     
     /** rb_module_new
      * 
      */
     public static RubyModule newModule(Ruby runtime) {
         return new RubyModule(runtime);
     }
     
     /** rb_module_new/rb_define_module_id/rb_name_class/rb_set_class_path
      * 
      */
     public static RubyModule newModule(Ruby runtime, String name, RubyModule parent, boolean setParent) {
         RubyModule module = newModule(runtime);
         module.setBaseName(name);
         if (setParent) module.setParent(parent);
         parent.setConstant(name, module);
         return module;
     }
     
     // synchronized method per JRUBY-1173 (unsafe Double-Checked Locking)
     // FIXME: synchronization is still wrong in CP code
     public synchronized void addClassProvider(ClassProvider provider) {
         if (!classProviders.contains(provider)) {
             Set<ClassProvider> cp = new HashSet<ClassProvider>(classProviders);
             cp.add(provider);
             classProviders = cp;
         }
     }
 
     public synchronized void removeClassProvider(ClassProvider provider) {
         Set<ClassProvider> cp = new HashSet<ClassProvider>(classProviders);
         cp.remove(provider);
         classProviders = cp;
     }
 
     private void checkForCyclicInclude(RubyModule m) throws RaiseException {
         if (getNonIncludedClass() == m.getNonIncludedClass()) {
             throw getRuntime().newArgumentError("cyclic include detected");
         }
     }
 
     private RubyClass searchProvidersForClass(String name, RubyClass superClazz) {
         RubyClass clazz;
         for (ClassProvider classProvider: classProviders) {
             if ((clazz = classProvider.defineClassUnder(this, name, superClazz)) != null) {
                 return clazz;
             }
         }
         return null;
     }
 
     private RubyModule searchProvidersForModule(String name) {
         RubyModule module;
         for (ClassProvider classProvider: classProviders) {
             if ((module = classProvider.defineModuleUnder(this, name)) != null) {
                 return module;
             }
         }
         return null;
     }
 
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     public void setSuperClass(RubyClass superClass) {
         // update superclass reference
         this.superClass = superClass;
         if (superClass != null && superClass.isSynchronized()) becomeSynchronized();
     }
 
     public RubyModule getParent() {
         return parent;
     }
 
     public void setParent(RubyModule parent) {
         this.parent = parent;
     }
     
     public Map<String, DynamicMethod> getMethods() {
         return this.methods;
     }
 
     public synchronized Map<String, DynamicMethod> getMethodsForWrite() {
         Map<String, DynamicMethod> myMethods = this.methods;
         return myMethods == Collections.EMPTY_MAP ?
             this.methods = new ConcurrentHashMap<String, DynamicMethod>(0, 0.9f, 1) :
             myMethods;
     }
     
     // note that addMethod now does its own put, so any change made to
     // functionality here should be made there as well 
     private void putMethod(String name, DynamicMethod method) {
         getMethodsForWrite().put(name, method);
 
         getRuntime().addProfiledMethod(name, method);
     }
 
     /**
      * Is this module one that in an included one (e.g. an IncludedModuleWrapper). 
      */
     public boolean isIncluded() {
         return false;
     }
 
     public RubyModule getNonIncludedClass() {
         return this;
     }
 
     /**
      * Get the base name of this class, or null if it is an anonymous class.
      * 
      * @return base name of the class
      */
     public String getBaseName() {
         return baseName;
     }
 
     /**
      * Set the base name of the class. If null, the class effectively becomes
      * anonymous (though constants elsewhere may reference it).
      * @param name the new base name of the class
      */
     public void setBaseName(String name) {
         baseName = name;
     }
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (cachedName != null) return cachedName;
         return calculateName();
     }
     
     /**
      * Get the "simple" name for the class, which is either the "base" name or
      * the "anonymous" class name.
      * 
      * @return the "simple" name of the class
      */
     public String getSimpleName() {
         if (baseName != null) return baseName;
         return calculateAnonymousName();
     }
 
     /**
      * Recalculate the fully-qualified name of this class/module.
      */
     private String calculateName() {
         boolean cache = true;
 
         if (getBaseName() == null) {
             // we are anonymous, use anonymous name
             return calculateAnonymousName();
         }
         
         Ruby runtime = getRuntime();
         
         String name = getBaseName();
         RubyClass objectClass = runtime.getObject();
         
         // First, we count the parents
         int parentCount = 0;
         for (RubyModule p = getParent() ; p != null && p != objectClass ; p = p.getParent()) {
             parentCount++;
         }
         
         // Allocate a String array for all of their names and populate it
         String[] parentNames = new String[parentCount];
         int i = parentCount - 1;
         int totalLength = name.length() + parentCount * 2; // name length + enough :: for all parents
         for (RubyModule p = getParent() ; p != null && p != objectClass ; p = p.getParent(), i--) {
             String pName = p.getBaseName();
             
             // This is needed when the enclosing class or module is a singleton.
             // In that case, we generated a name such as null::Foo, which broke 
             // Marshalling, among others. The correct thing to do in this situation 
             // is to insert the generate the name of form #<Class:01xasdfasd> if 
             // it's a singleton module/class, which this code accomplishes.
             if(pName == null) {
                 cache = false;
                 pName = p.getName();
              }
             
             parentNames[i] = pName;
             totalLength += pName.length();
         }
         
         // Then build from the front using a StringBuilder
         StringBuilder builder = new StringBuilder(totalLength);
         for (String parentName : parentNames) {
             builder.append(parentName).append("::");
         }
         builder.append(name);
         
         String fullName = builder.toString();
 
         if (cache) cachedName = fullName;
 
         return fullName;
     }
 
     private String calculateAnonymousName() {
         if (anonymousName == null) {
             // anonymous classes get the #<Class:0xdeadbeef> format
             StringBuilder anonBase = new StringBuilder("#<" + metaClass.getRealClass().getName() + ":0x");
             anonBase.append(Integer.toHexString(System.identityHashCode(this))).append('>');
             anonymousName = anonBase.toString();
         }
         return anonymousName;
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     @Deprecated
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
 
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
 
         return includedModule;
     }
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module;
         if ((module = getConstantAt(name)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     @Deprecated
     public RubyClass fastGetClass(String internedName) {
         return getClass(internedName);
     }
 
     /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
 
         testFrozen("module");
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
 
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         checkForCyclicInclude(module);
 
         infectBy(module);
 
         doIncludeModule(module);
         invalidateCoreClasses();
         invalidateCacheDescendants();
         invalidateConstantCacheForModuleInclusion(module);
     }
     
     public void defineAnnotatedMethod(Class clazz, String name) {
         // FIXME: This is probably not very efficient, since it loads all methods for each call
         boolean foundMethod = false;
         for (Method method : clazz.getDeclaredMethods()) {
             if (method.getName().equals(name) && defineAnnotatedMethod(method, MethodFactory.createFactory(getRuntime().getJRubyClassLoader()))) {
                 foundMethod = true;
             }
         }
 
         if (!foundMethod) {
             throw new RuntimeException("No JRubyMethod present for method " + name + "on class " + clazz.getName());
         }
     }
     
     public void defineAnnotatedConstants(Class clazz) {
         Field[] declaredFields = clazz.getDeclaredFields();
         for (Field field : declaredFields) {
             if (Modifier.isStatic(field.getModifiers())) {
                 defineAnnotatedConstant(field);
             }
         }
     }
 
     public boolean defineAnnotatedConstant(Field field) {
         JRubyConstant jrubyConstant = field.getAnnotation(JRubyConstant.class);
 
         if (jrubyConstant == null) return false;
 
         String[] names = jrubyConstant.value();
         if(names.length == 0) {
             names = new String[]{field.getName()};
         }
 
         Class tp = field.getType();
         IRubyObject realVal;
 
         try {
             if(tp == Integer.class || tp == Integer.TYPE || tp == Short.class || tp == Short.TYPE || tp == Byte.class || tp == Byte.TYPE) {
                 realVal = RubyNumeric.int2fix(getRuntime(), field.getInt(null));
             } else if(tp == Boolean.class || tp == Boolean.TYPE) {
                 realVal = field.getBoolean(null) ? getRuntime().getTrue() : getRuntime().getFalse();
             } else {
                 realVal = getRuntime().getNil();
             }
         } catch(Exception e) {
             realVal = getRuntime().getNil();
         }
 
         
         for(String name : names) {
             this.setConstant(name, realVal);
         }
 
         return true;
     }
 
     public void defineAnnotatedMethods(Class clazz) {
         defineAnnotatedMethodsIndividually(clazz);
     }
     
     public static class MethodClumper {
         Map<String, List<JavaMethodDescriptor>> annotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_8 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_8 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_9 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_9 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods2_0 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods2_0 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> allAnnotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         
         public void clump(Class cls) {
             Method[] declaredMethods = cls.getDeclaredMethods();
             for (Method method: declaredMethods) {
                 JRubyMethod anno = method.getAnnotation(JRubyMethod.class);
                 if (anno == null) continue;
                 
                 JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
                 
                 String name = anno.name().length == 0 ? method.getName() : anno.name()[0];
                 
                 List<JavaMethodDescriptor> methodDescs;
                 Map<String, List<JavaMethodDescriptor>> methodsHash = null;
                 if (desc.isStatic) {
                     if (anno.compat() == RUBY1_8) {
                         methodsHash = staticAnnotatedMethods1_8;
                     } else if (anno.compat() == RUBY1_9) {
                         methodsHash = staticAnnotatedMethods1_9;
                     } else if (anno.compat() == RUBY2_0) {
                         methodsHash = staticAnnotatedMethods2_0;
                     } else {
                         methodsHash = staticAnnotatedMethods;
                     }
                 } else {
                     if (anno.compat() == RUBY1_8) {
                         methodsHash = annotatedMethods1_8;
                     } else if (anno.compat() == RUBY1_9) {
                         methodsHash = annotatedMethods1_9;
                     } else if (anno.compat() == RUBY2_0) {
                         methodsHash = annotatedMethods2_0;
                     } else {
                         methodsHash = annotatedMethods;
                     }
                 }
 
                 // add to specific
                 methodDescs = methodsHash.get(name);
                 if (methodDescs == null) {
                     methodDescs = new ArrayList<JavaMethodDescriptor>();
                     methodsHash.put(name, methodDescs);
                 }
                 
                 methodDescs.add(desc);
 
                 // add to general
                 methodDescs = allAnnotatedMethods.get(name);
                 if (methodDescs == null) {
                     methodDescs = new ArrayList<JavaMethodDescriptor>();
                     allAnnotatedMethods.put(name, methodDescs);
                 }
 
                 methodDescs.add(desc);
             }
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAllAnnotatedMethods() {
             return allAnnotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods() {
             return annotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_8() {
             return annotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_9() {
             return annotatedMethods1_9;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods2_0() {
             return annotatedMethods2_0;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods() {
             return staticAnnotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_8() {
             return staticAnnotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_9() {
             return staticAnnotatedMethods1_9;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods2_0() {
             return staticAnnotatedMethods2_0;
         }
     }
     
     public void defineAnnotatedMethodsIndividually(Class clazz) {
         TypePopulator populator;
         
         if (RubyInstanceConfig.FULL_TRACE_ENABLED || RubyInstanceConfig.REFLECTED_HANDLES) {
             // we want reflected invokers or need full traces, use default (slow) populator
             if (DEBUG) LOG.info("trace mode, using default populator");
             populator = TypePopulator.DEFAULT;
         } else {
             try {
                 String qualifiedName = "org.jruby.gen." + clazz.getCanonicalName().replace('.', '$');
 
                 if (DEBUG) LOG.info("looking for " + qualifiedName + AnnotationBinder.POPULATOR_SUFFIX);
 
                 Class populatorClass = Class.forName(qualifiedName + AnnotationBinder.POPULATOR_SUFFIX);
                 populator = (TypePopulator)populatorClass.newInstance();
             } catch (Throwable t) {
                 if (DEBUG) LOG.info("Could not find it, using default populator");
                 populator = TypePopulator.DEFAULT;
             }
         }
         
         populator.populate(this, clazz);
     }
     
     public boolean defineAnnotatedMethod(String name, List<JavaMethodDescriptor> methods, MethodFactory methodFactory) {
         JavaMethodDescriptor desc = methods.get(0);
         if (methods.size() == 1) {
             return defineAnnotatedMethod(desc, methodFactory);
         } else {
             CompatVersion compatVersion = getRuntime().getInstanceConfig().getCompatVersion();
             if (shouldBindMethod(compatVersion, desc.anno.compat())) {
                 DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, methods);
                 define(this, desc, dynamicMethod);
             
                 return true;
             }
             
             return false;
         }
     }
     
     public boolean defineAnnotatedMethod(Method method, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
         if (jrubyMethod == null) return false;
 
         CompatVersion compatVersion = getRuntime().getInstanceConfig().getCompatVersion();
         if (shouldBindMethod(compatVersion, jrubyMethod.compat())) {
             JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
     
     public boolean defineAnnotatedMethod(JavaMethodDescriptor desc, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = desc.anno;
 
         if (jrubyMethod == null) return false;
 
         CompatVersion compatVersion = getRuntime().getInstanceConfig().getCompatVersion();
         if (shouldBindMethod(compatVersion, jrubyMethod.compat())) {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(ThreadContext context, String name) {
         Ruby runtime = context.runtime;
 
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.UNDEFINING_BAD, "undefining `"+ name +"' may cause serious problem");
         }
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             String s0 = " class";
             RubyModule c = this;
 
             if (c.isSingleton()) {
                 IRubyObject obj = ((MetaClass)c).getAttached();
 
                 if (obj != null && obj instanceof RubyModule) {
                     c = (RubyModule) obj;
                     s0 = "";
                 }
             } else if (c.isModule()) {
                 s0 = " module";
             }
 
             throw runtime.newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_undefined", runtime.newSymbol(name));
         } else {
             callMethod(context, "method_undefined", runtime.newSymbol(name));
         }
     }
 
     @JRubyMethod(name = "include?", required = 1)
     public IRubyObject include_p(ThreadContext context, IRubyObject arg) {
         if (!arg.isModule()) {
             throw context.runtime.newTypeError(arg, context.runtime.getModule());
         }
         RubyModule moduleToCompare = (RubyModule) arg;
 
         // See if module is in chain...Cannot match against itself so start at superClass.
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isSame(moduleToCompare)) {
                 return context.runtime.getTrue();
             }
         }
 
         return context.runtime.getFalse();
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         testFrozen("class/module");
 
         addMethodInternal(name, method);
     }
 
     public void addMethodInternal(String name, DynamicMethod method) {
         synchronized(getMethodsForWrite()) {
             addMethodAtBootTimeOnly(name, method);
             invalidateCoreClasses();
             invalidateCacheDescendants();
         }
     }
 
     /**
      * This method is not intended for use by normal users; it is a fast-path
      * method that skips synchronization and hierarchy invalidation to speed
      * boot-time method definition.
      *
      * @param name The name to which to bind the method
      * @param method The method to bind
      */
     public void addMethodAtBootTimeOnly(String name, DynamicMethod method) {
         getMethodsForWrite().put(name, method);
 
         getRuntime().addProfiledMethod(name, method);
     }
 
     public void removeMethod(ThreadContext context, String name) {
         Ruby runtime = context.runtime;
 
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethodsForWrite()) {
             DynamicMethod method = (DynamicMethod) getMethodsForWrite().remove(name);
             if (method == null) {
                 throw runtime.newNameError("method '" + name + "' not defined in " + getName(), name);
             }
 
             invalidateCoreClasses();
             invalidateCacheDescendants();
         }
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_removed", runtime.newSymbol(name));
         } else {
             callMethod(context, "method_removed", runtime.newSymbol(name));
         }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */    
     public DynamicMethod searchMethod(String name) {
         return searchWithCache(name).method;
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */    
     public CacheEntry searchWithCache(String name) {
         CacheEntry entry = cacheHit(name);
 
         if (entry != null) return entry;
 
         // we grab serial number first; the worst that will happen is we cache a later
         // update with an earlier serial number, which would just flush anyway
         int token = getGeneration();
         DynamicMethod method = searchMethodInner(name);
 
         if (method instanceof CacheableMethod) {
             method = ((CacheableMethod) method).getMethodForCaching();
         }
 
         return method != null ? addToCache(name, method, token) : addToCache(name, UndefinedMethod.getInstance(), token);
     }
     
     @Deprecated
     public final int getCacheToken() {
         return generation;
     }
     
     public final int getGeneration() {
         return generation;
     }
 
     public final Integer getGenerationObject() {
         return generationObject;
     }
 
     private final Map<String, CacheEntry> getCachedMethods() {
         return this.cachedMethods;
     }
 
     private final Map<String, CacheEntry> getCachedMethodsForWrite() {
         Map<String, CacheEntry> myCachedMethods = this.cachedMethods;
         return myCachedMethods == Collections.EMPTY_MAP ?
             this.cachedMethods = new ConcurrentHashMap<String, CacheEntry>(0, 0.75f, 1) :
             myCachedMethods;
     }
     
     private CacheEntry cacheHit(String name) {
         CacheEntry cacheEntry = getCachedMethods().get(name);
 
         if (cacheEntry != null) {
             if (cacheEntry.token == getGeneration()) {
                 return cacheEntry;
             }
         }
         
         return null;
     }
 
     private void invalidateConstantCacheForModuleInclusion(RubyModule module)
     {
         for (RubyModule mod : gatherModules(module)) {
             if (!mod.getConstantMap().isEmpty()) {
                 invalidateConstantCache();
                 break;
             }
         }
     }
     
     protected static abstract class CacheEntryFactory {
         public abstract CacheEntry newCacheEntry(DynamicMethod method, int token);
 
         /**
          * Test all WrapperCacheEntryFactory instances in the chain for assignability
          * from the given class.
          *
          * @param cacheEntryFactoryClass the class from which to test assignability
          * @return whether the given class is assignable from any factory in the chain
          */
         public boolean hasCacheEntryFactory(Class cacheEntryFactoryClass) {
             CacheEntryFactory current = this;
             while (current instanceof WrapperCacheEntryFactory) {
                 if (cacheEntryFactoryClass.isAssignableFrom(current.getClass())) {
                     return true;
                 }
                 current = ((WrapperCacheEntryFactory)current).getPrevious();
             }
 
             return cacheEntryFactoryClass.isAssignableFrom(current.getClass());
         }
     }
 
     /**
      * A wrapper CacheEntryFactory, for delegating cache entry creation along a chain.
      */
     protected static abstract class WrapperCacheEntryFactory extends CacheEntryFactory {
         /** The CacheEntryFactory being wrapped. */
         protected final CacheEntryFactory previous;
 
         /**
          * Construct a new WrapperCacheEntryFactory using the given CacheEntryFactory as
          * the "previous" wrapped factory.
          *
          * @param previous the wrapped factory
          */
         public WrapperCacheEntryFactory(CacheEntryFactory previous) {
             this.previous = previous;
         }
 
         public CacheEntryFactory getPrevious() {
             return previous;
         }
     }
 
     protected static final CacheEntryFactory NormalCacheEntryFactory = new CacheEntryFactory() {
         @Override
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             return new CacheEntry(method, token);
         }
     };
 
     protected static class SynchronizedCacheEntryFactory extends WrapperCacheEntryFactory {
         public SynchronizedCacheEntryFactory(CacheEntryFactory previous) {
             super(previous);
         }
         @Override
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             if (method.isUndefined()) {
                 return new CacheEntry(method, token);
             }
             // delegate up the chain
             CacheEntry delegated = previous.newCacheEntry(method, token);
             return new CacheEntry(new SynchronizedDynamicMethod(delegated.method), delegated.token);
         }
     }
 
     protected static class ProfilingCacheEntryFactory extends WrapperCacheEntryFactory {
         public ProfilingCacheEntryFactory(CacheEntryFactory previous) {
             super(previous);
         }
         @Override
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             if (method.isUndefined()) {
                 return new CacheEntry(method, token);
             }
             CacheEntry delegated = previous.newCacheEntry(method, token);
             return new CacheEntry(new ProfilingDynamicMethod(delegated.method), delegated.token);
         }
     }
 
     private volatile CacheEntryFactory cacheEntryFactory;
 
     // modifies this class only; used to make the Synchronized module synchronized
     public void becomeSynchronized() {
         cacheEntryFactory = new SynchronizedCacheEntryFactory(cacheEntryFactory);
     }
 
     public boolean isSynchronized() {
         return cacheEntryFactory.hasCacheEntryFactory(SynchronizedCacheEntryFactory.class);
     }
 
     private CacheEntry addToCache(String name, DynamicMethod method, int token) {
         CacheEntry entry = cacheEntryFactory.newCacheEntry(method, token);
         getCachedMethodsForWrite().put(name, entry);
 
         return entry;
     }
     
     public DynamicMethod searchMethodInner(String name) {
         DynamicMethod method = getMethods().get(name);
         
         if (method != null) return method;
         
         return superClass == null ? null : superClass.searchMethodInner(name);
     }
 
     public void invalidateCacheDescendants() {
         if (DEBUG) LOG.debug("invalidating descendants: {}", baseName);
 
         if (includingHierarchies.isEmpty()) {
             // it's only us; just invalidate directly
             methodInvalidator.invalidate();
             return;
         }
 
         List<Invalidator> invalidators = new ArrayList();
         invalidators.add(methodInvalidator);
         
         synchronized (getRuntime().getHierarchyLock()) {
             for (RubyClass includingHierarchy : includingHierarchies) {
                 includingHierarchy.addInvalidatorsAndFlush(invalidators);
             }
         }
         
         methodInvalidator.invalidateAll(invalidators);
     }
     
     protected void invalidateCoreClasses() {
         if (!getRuntime().isBooting()) {
             if (this == getRuntime().getFixnum()) {
                 getRuntime().reopenFixnum();
             } else if (this == getRuntime().getFloat()) {
                 getRuntime().reopenFloat();
             }
         }
     }
     
     public Invalidator getInvalidator() {
         return methodInvalidator;
     }
     
     public void updateGeneration() {
         generationObject = generation = getRuntime().getNextModuleGeneration();
     }
 
     @Deprecated
     protected void invalidateCacheDescendantsInner() {
         methodInvalidator.invalidate();
     }
     
     protected void invalidateConstantCache() {
diff --git a/src/org/jruby/RubyNumeric.java b/src/org/jruby/RubyNumeric.java
index 6688f7fcfa..69c4906dc1 100644
--- a/src/org/jruby/RubyNumeric.java
+++ b/src/org/jruby/RubyNumeric.java
@@ -1,1019 +1,1014 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Antti Karanta <Antti.Karanta@napa.fi>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.util.Numeric.f_abs;
 import static org.jruby.util.Numeric.f_arg;
 import static org.jruby.util.Numeric.f_mul;
 import static org.jruby.util.Numeric.f_negative_p;
 
 import java.math.BigInteger;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.ConvertDouble;
 import org.jruby.util.ConvertBytes;
 import static org.jruby.CompatVersion.*;
 
 import static org.jruby.runtime.Helpers.invokedynamic;
 
 import org.jruby.runtime.invokedynamic.MethodNames;
 
 /**
  * Base class for all numerical types in ruby.
  */
 // TODO: Numeric.new works in Ruby and it does here too.  However trying to use
 //   that instance in a numeric operation should generate an ArgumentError. Doing
 //   this seems so pathological I do not see the need to fix this now.
 @JRubyClass(name="Numeric", include="Comparable")
 public class RubyNumeric extends RubyObject {
     
     public static RubyClass createNumericClass(Ruby runtime) {
         RubyClass numeric = runtime.defineClass("Numeric", runtime.getObject(), NUMERIC_ALLOCATOR);
         runtime.setNumeric(numeric);
 
         numeric.index = ClassIndex.NUMERIC;
         numeric.setReifiedClass(RubyNumeric.class);
 
-        numeric.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyNumeric;
-            }
-        };
+        numeric.kindOf = new RubyModule.JavaClassKindOf(RubyNumeric.class);
 
         numeric.includeModule(runtime.getComparable());
         numeric.defineAnnotatedMethods(RubyNumeric.class);
 
         return numeric;
     }
 
     protected static final ObjectAllocator NUMERIC_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyNumeric(runtime, klass);
         }
     };
 
     public static final double DBL_EPSILON=2.2204460492503131e-16;
 
     private static IRubyObject convertToNum(double val, Ruby runtime) {
 
         if (val >= (double) RubyFixnum.MAX || val < (double) RubyFixnum.MIN) {
             return RubyBignum.newBignum(runtime, val);
         }
         return RubyFixnum.newFixnum(runtime, (long) val);
     }
     
     public RubyNumeric(Ruby runtime, RubyClass metaClass) {
         super(runtime, metaClass);
     }
 
     public RubyNumeric(RubyClass metaClass) {
         super(metaClass);
     }
 
     public RubyNumeric(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         super(runtime, metaClass, useObjectSpace);
     }    
 
     @Deprecated
     public RubyNumeric(Ruby runtime, RubyClass metaClass, boolean useObjectSpace, boolean canBeTainted) {
         super(runtime, metaClass, useObjectSpace, canBeTainted);
     }    
     
     // The implementations of these are all bonus (see TODO above)  I was going
     // to throw an error from these, but it appears to be the wrong place to
     // do it.
     public double getDoubleValue() {
         return 0;
     }
 
     public long getLongValue() {
         return 0;
     }
 
     public BigInteger getBigIntegerValue() {
         return BigInteger.ZERO;
     }
     
     public static RubyNumeric newNumeric(Ruby runtime) {
     	return new RubyNumeric(runtime, runtime.getNumeric());
     }
 
     /*  ================
      *  Utility Methods
      *  ================ 
      */
 
     /** rb_num2int, NUM2INT
      * 
      */
     public static int num2int(IRubyObject arg) {
         long num = num2long(arg);
 
         checkInt(arg, num);
         return (int)num;
     }
     
     /** check_int
      * 
      */
     public static void checkInt(IRubyObject arg, long num){
         if (num < Integer.MIN_VALUE) {
             tooSmall(arg, num);
         } else if (num > Integer.MAX_VALUE) {
             tooBig(arg, num);
         } else {
             return;
         }
     }
     
     private static void tooSmall(IRubyObject arg, long num) {
         throw arg.getRuntime().newRangeError("integer " + num + " too small to convert to `int'");
     }
     
     private static void tooBig(IRubyObject arg, long num) {
         throw arg.getRuntime().newRangeError("integer " + num + " too big to convert to `int'");
     }
 
     /**
      * NUM2CHR
      */
     public static byte num2chr(IRubyObject arg) {
         if (arg instanceof RubyString) {
             String value = ((RubyString) arg).toString();
 
             if (value != null && value.length() > 0) return (byte) value.charAt(0);
         } 
 
         return (byte) num2int(arg);
     }
 
     /** rb_num2long and FIX2LONG (numeric.c)
      * 
      */
     public static long num2long(IRubyObject arg) {
         if (arg instanceof RubyFixnum) {
             return ((RubyFixnum) arg).getLongValue();
         } else {
             return other2long(arg);
         }
     }
 
     private static long other2long(IRubyObject arg) throws RaiseException {
         if (arg.isNil()) {
             throw arg.getRuntime().newTypeError("no implicit conversion from nil to integer");
         } else if (arg instanceof RubyFloat) {
             return float2long((RubyFloat)arg);
         } else if (arg instanceof RubyBignum) {
             return RubyBignum.big2long((RubyBignum) arg);
         }
         return arg.convertToInteger().getLongValue();
     }
     
     private static long float2long(RubyFloat flt) {
         double aFloat = flt.getDoubleValue();
         if (aFloat <= (double) Long.MAX_VALUE && aFloat >= (double) Long.MIN_VALUE) {
             return (long) aFloat;
         } else {
             // TODO: number formatting here, MRI uses "%-.10g", 1.4 API is a must?
             throw flt.getRuntime().newRangeError("float " + aFloat + " out of range of integer");
         }
     }
 
     /** rb_dbl2big + LONG2FIX at once (numeric.c)
      * 
      */
     public static IRubyObject dbl2num(Ruby runtime, double val) {
         if (Double.isInfinite(val)) {
             throw runtime.newFloatDomainError(val < 0 ? "-Infinity" : "Infinity");
         }
         if (Double.isNaN(val)) {
             throw runtime.newFloatDomainError("NaN");
         }
         return convertToNum(val, runtime);
     }
 
     /** rb_num2dbl and NUM2DBL
      * 
      */
     public static double num2dbl(IRubyObject arg) {
         if (arg instanceof RubyFloat) {
             return ((RubyFloat) arg).getDoubleValue();
         } else if (arg instanceof RubyString) {
             throw arg.getRuntime().newTypeError("no implicit conversion to float from string");
         } else if (arg == arg.getRuntime().getNil()) {
             throw arg.getRuntime().newTypeError("no implicit conversion to float from nil");
         }
         return RubyKernel.new_float(arg, arg).getDoubleValue();
     }
 
     /** rb_dbl_cmp (numeric.c)
      * 
      */
     public static IRubyObject dbl_cmp(Ruby runtime, double a, double b) {
         if (Double.isNaN(a) || Double.isNaN(b)) return runtime.getNil();
         return a == b ? RubyFixnum.zero(runtime) : a > b ?
                 RubyFixnum.one(runtime) : RubyFixnum.minus_one(runtime);
     }
 
     public static long fix2long(IRubyObject arg) {
         return ((RubyFixnum) arg).getLongValue();
     }
 
     public static int fix2int(IRubyObject arg) {
         long num = arg instanceof RubyFixnum ? fix2long(arg) : num2long(arg);
         checkInt(arg, num);
         return (int) num;
     }
 
     public static int fix2int(RubyFixnum arg) {
         long num = arg.getLongValue();
         checkInt(arg, num);
         return (int) num;
     }
 
     public static RubyInteger str2inum(Ruby runtime, RubyString str, int base) {
         return str2inum(runtime,str,base,false);
     }
 
     public static RubyNumeric int2fix(Ruby runtime, long val) {
         return RubyFixnum.newFixnum(runtime,val);
     }
 
     /** rb_num2fix
      * 
      */
     public static IRubyObject num2fix(IRubyObject val) {
         if (val instanceof RubyFixnum) {
             return val;
         }
         if (val instanceof RubyBignum) {
             // any BigInteger is bigger than Fixnum and we don't have FIXABLE
             throw val.getRuntime().newRangeError("integer " + val + " out of range of fixnum");
         }
         return RubyFixnum.newFixnum(val.getRuntime(), num2long(val));
     }
 
     /**
      * Converts a string representation of an integer to the integer value. 
      * Parsing starts at the beginning of the string (after leading and 
      * trailing whitespace have been removed), and stops at the end or at the 
      * first character that can't be part of an integer.  Leading signs are
      * allowed. If <code>base</code> is zero, strings that begin with '0[xX]',
      * '0[bB]', or '0' (optionally preceded by a sign) will be treated as hex, 
      * binary, or octal numbers, respectively.  If a non-zero base is given, 
      * only the prefix (if any) that is appropriate to that base will be 
      * parsed correctly.  For example, if the base is zero or 16, the string
      * "0xff" will be converted to 256, but if the base is 10, it will come out 
      * as zero, since 'x' is not a valid decimal digit.  If the string fails 
      * to parse as a number, zero is returned.
      * 
      * @param runtime  the ruby runtime
      * @param str   the string to be converted
      * @param base  the expected base of the number (for example, 2, 8, 10, 16),
      *              or 0 if the method should determine the base automatically 
      *              (defaults to 10). Values 0 and 2-36 are permitted. Any other
      *              value will result in an ArgumentError.
      * @param strict if true, enforce the strict criteria for String encoding of
      *               numeric values, as required by Integer('n'), and raise an
      *               exception when those criteria are not met. Otherwise, allow
      *               lax expression of values, as permitted by String#to_i, and
      *               return a value in almost all cases (excepting illegal radix).
      *               TODO: describe the rules/criteria
      * @return  a RubyFixnum or (if necessary) a RubyBignum representing 
      *          the result of the conversion, which will be zero if the 
      *          conversion failed.
      */
     public static RubyInteger str2inum(Ruby runtime, RubyString str, int base, boolean strict) {
         ByteList s = str.getByteList();
         return ConvertBytes.byteListToInum(runtime, s, base, strict);
     }
 
     public static RubyFloat str2fnum(Ruby runtime, RubyString arg) {
         return str2fnum(runtime,arg,false);
     }
 
     /**
      * Converts a string representation of a floating-point number to the 
      * numeric value.  Parsing starts at the beginning of the string (after 
      * leading and trailing whitespace have been removed), and stops at the 
      * end or at the first character that can't be part of a number.  If 
      * the string fails to parse as a number, 0.0 is returned.
      * 
      * @param runtime  the ruby runtime
      * @param arg   the string to be converted
      * @param strict if true, enforce the strict criteria for String encoding of
      *               numeric values, as required by Float('n'), and raise an
      *               exception when those criteria are not met. Otherwise, allow
      *               lax expression of values, as permitted by String#to_f, and
      *               return a value in all cases.
      *               TODO: describe the rules/criteria
      * @return  a RubyFloat representing the result of the conversion, which
      *          will be 0.0 if the conversion failed.
      */
     public static RubyFloat str2fnum(Ruby runtime, RubyString arg, boolean strict) {
         return str2fnumCommon(runtime, arg, strict, biteListCaller18);
     }
     
     public static RubyFloat str2fnum19(Ruby runtime, RubyString arg, boolean strict) {
         return str2fnumCommon(runtime, arg, strict, biteListCaller19);
     }
 
     private static RubyFloat str2fnumCommon(Ruby runtime, RubyString arg, boolean strict, ByteListCaller caller) {
         final double ZERO = 0.0;
         try {
             return new RubyFloat(runtime, caller.yield(arg, strict));
         } catch (NumberFormatException e) {
             if (strict) {
                 throw runtime.newArgumentError("invalid value for Float(): "
                         + arg.callMethod(runtime.getCurrentContext(), "inspect").toString());
             }
             return new RubyFloat(runtime,ZERO);
         }
     }
 
     private static interface ByteListCaller {
         public double yield(RubyString arg, boolean strict);
     }
 
     private static class ByteListCaller18 implements ByteListCaller {
         public double yield(RubyString arg, boolean strict) {
             return ConvertDouble.byteListToDouble(arg.getByteList(),strict);
         }
     }
     private static final ByteListCaller18 biteListCaller18 = new ByteListCaller18();
 
     private static class ByteListCaller19 implements ByteListCaller {
         public double yield(RubyString arg, boolean strict) {
             return ConvertDouble.byteListToDouble19(arg.getByteList(),strict);
         }
     }
     private static final ByteListCaller19 biteListCaller19 = new ByteListCaller19();
 
     
     /** Numeric methods. (num_*)
      *
      */
     
     protected IRubyObject[] getCoerced(ThreadContext context, IRubyObject other, boolean error) {
         IRubyObject result;
         
         try {
             result = other.callMethod(context, "coerce", this);
         } catch (RaiseException e) {
             if (error) {
                 throw getRuntime().newTypeError(
                         other.getMetaClass().getName() + " can't be coerced into " + getMetaClass().getName());
             }
              
             return null;
         }
         
         if (!(result instanceof RubyArray) || ((RubyArray)result).getLength() != 2) {
             throw getRuntime().newTypeError("coerce must return [x, y]");
         }
         
         return ((RubyArray)result).toJavaArray();
     }
 
     protected IRubyObject callCoerced(ThreadContext context, String method, IRubyObject other, boolean err) {
         IRubyObject[] args = getCoerced(context, other, err);
         if(args == null) {
             return getRuntime().getNil();
         }
         return args[0].callMethod(context, method, args[1]);
     }
 
     public IRubyObject callCoerced(ThreadContext context, String method, IRubyObject other) {
         IRubyObject[] args = getCoerced(context, other, false);
         if(args == null) {
             return getRuntime().getNil();
         }
         return args[0].callMethod(context, method, args[1]);
     }
     
     // beneath are rewritten coercions that reflect MRI logic, the aboves are used only by RubyBigDecimal
 
     /** coerce_body
      *
      */
     protected final IRubyObject coerceBody(ThreadContext context, IRubyObject other) {
         return other.callMethod(context, "coerce", this);
     }
 
     /** do_coerce
      * 
      */
     protected final RubyArray doCoerce(ThreadContext context, IRubyObject other, boolean err) {
         IRubyObject result;
         try {
             result = coerceBody(context, other);
         } catch (RaiseException e) {
             if (err) {
                 throw getRuntime().newTypeError(
                         other.getMetaClass().getName() + " can't be coerced into " + getMetaClass().getName());
             }
             return null;
         }
     
         if (!(result instanceof RubyArray) || ((RubyArray) result).getLength() != 2) {
             if (err) {
                 throw getRuntime().newTypeError("coerce must return [x, y]");
             }
             return null;
         }
         return (RubyArray) result;
     }
 
     /** rb_num_coerce_bin
      *  coercion taking two arguments
      */
     protected final IRubyObject coerceBin(ThreadContext context, String method, IRubyObject other) {
         RubyArray ary = doCoerce(context, other, true);
         return (ary.eltInternal(0)).callMethod(context, method, ary.eltInternal(1));
     }
     
     /** rb_num_coerce_cmp
      *  coercion used for comparisons
      */
     protected final IRubyObject coerceCmp(ThreadContext context, String method, IRubyObject other) {
         RubyArray ary = doCoerce(context, other, false);
         if (ary == null) {
             return getRuntime().getNil(); // MRI does it!
         } 
         return (ary.eltInternal(0)).callMethod(context, method, ary.eltInternal(1));
     }
         
     /** rb_num_coerce_relop
      *  coercion used for relative operators
      */
     protected final IRubyObject coerceRelOp(ThreadContext context, String method, IRubyObject other) {
         RubyArray ary = doCoerce(context, other, false);
         if (ary == null) {
             return RubyComparable.cmperr(this, other);
         }
 
         return unwrapCoerced(context, method, other, ary);
     }
     
     private final IRubyObject unwrapCoerced(ThreadContext context, String method, IRubyObject other, RubyArray ary) {
         IRubyObject result = (ary.eltInternal(0)).callMethod(context, method, ary.eltInternal(1));
         if (result.isNil()) {
             return RubyComparable.cmperr(this, other);
         }
         return result;
     }
         
     public RubyNumeric asNumeric() {
         return this;
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** num_sadded
      *
      */
     @JRubyMethod(name = "singleton_method_added")
     public IRubyObject sadded(IRubyObject name) {
         throw getRuntime().newTypeError("can't define singleton method " + name + " for " + getType().getName());
     } 
         
     /** num_init_copy
      *
      */
     @Override
     @JRubyMethod(name = "initialize_copy", visibility = Visibility.PRIVATE)
     public IRubyObject initialize_copy(IRubyObject arg) {
         throw getRuntime().newTypeError("can't copy " + getType().getName());
     }
     
     /** num_coerce
      *
      */
     @JRubyMethod(name = "coerce")
     public IRubyObject coerce(IRubyObject other) {
         if (getMetaClass() == other.getMetaClass()) return getRuntime().newArray(other, this);
 
         IRubyObject cdr = RubyKernel.new_float(this, this);
         IRubyObject car = RubyKernel.new_float(this, other);
 
         return getRuntime().newArray(car, cdr);
     }
 
     /** num_uplus
      *
      */
     @JRubyMethod(name = "+@")
     public IRubyObject op_uplus() {
         return this;
     }
 
     /** num_imaginary
      *
      */
     @JRubyMethod(name = "i", compat = CompatVersion.RUBY1_9)
     public IRubyObject num_imaginary(ThreadContext context) {
         return RubyComplex.newComplexRaw(context.runtime, RubyFixnum.zero(context.runtime), this);
     }
 
     /** num_uminus
      *
      */
     @JRubyMethod(name = "-@")
     public IRubyObject op_uminus(ThreadContext context) {
         RubyArray ary = RubyFixnum.zero(context.runtime).doCoerce(context, this, true);
         return ary.eltInternal(0).callMethod(context, "-", ary.eltInternal(1));
     }
     
     /** num_cmp
      *
      */
     @JRubyMethod(name = "<=>")
     public IRubyObject op_cmp(IRubyObject other) {
         if (this == other) { // won't hurt fixnums
             return RubyFixnum.zero(getRuntime());
         }
         return getRuntime().getNil();
     }
 
     /** num_eql
      *
      */
     @JRubyMethod(name = "eql?")
     public IRubyObject eql_p(ThreadContext context, IRubyObject other) {
         if (getClass() != other.getClass()) return getRuntime().getFalse();
         return equalInternal(context, this, other) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** num_quo (1.8)
      * quo and fdiv in 1.8 just invokes "/"
      */
     @JRubyMethod(name = {"quo", "fdiv"}, compat = CompatVersion.RUBY1_8)
     public IRubyObject quo(ThreadContext context, IRubyObject other) {
         return callMethod(context, "/", other);
     }
     
     /** num_quo (1.9)
     *
     */
     @JRubyMethod(name = "quo", compat = CompatVersion.RUBY1_9)
     public IRubyObject quo_19(ThreadContext context, IRubyObject other) {
         return RubyRational.newRationalRaw(context.runtime, this).callMethod(context, "/", other);
     }
 
     /** num_div
      * 
      */
     @JRubyMethod(name = "div", compat = RUBY1_8)
     public IRubyObject div(ThreadContext context, IRubyObject other) {
         return callMethod(context, "/", other).convertToFloat().floor();
     }
 
     /** num_div
      *
      */
     @JRubyMethod(name = "div", compat = RUBY1_9)
     public IRubyObject div19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyNumeric) {
             RubyNumeric numeric = (RubyNumeric) other;
             if (numeric.zero_p(context).isTrue()) {
                 throw context.runtime.newZeroDivisionError();
             }
         }
         return callMethod(context, "/", other).callMethod(context, "floor");
     }
 
     /** num_divmod
      * 
      */
     @JRubyMethod(name = "divmod", compat = RUBY1_8)
     public IRubyObject divmod(ThreadContext context, IRubyObject other) {
         return RubyArray.newArray(getRuntime(), div(context, other), modulo(context, other));
     }
 
     /** num_divmod
      *
      */
     @JRubyMethod(name = "divmod", compat = RUBY1_9)
     public IRubyObject divmod19(ThreadContext context, IRubyObject other) {
         return RubyArray.newArray(getRuntime(), div(context, other), modulo19(context, other));
     }
     
     /** num_fdiv (1.9) */
     @JRubyMethod(name = "fdiv", compat = RUBY1_9)
     public IRubyObject fdiv(ThreadContext context, IRubyObject other) {
         return Helpers.invoke(context, this.convertToFloat(), "/", other);
     }
 
     /** num_modulo
      *
      */
     @JRubyMethod(name = "modulo", compat = RUBY1_8)
     public IRubyObject modulo(ThreadContext context, IRubyObject other) {
         return callMethod(context, "%", other);
     }
 
     /** num_modulo
      *
      */
     @JRubyMethod(name = "modulo", compat = RUBY1_9)
     public IRubyObject modulo19(ThreadContext context, IRubyObject other) {
         return callMethod(context, "-", other.callMethod(context, "*", callMethod(context, "div", other)));
     }
 
     /** num_remainder
      *
      */
     @JRubyMethod(name = "remainder")
     public IRubyObject remainder(ThreadContext context, IRubyObject dividend) {
         IRubyObject z = callMethod(context, "%", dividend);
         IRubyObject x = this;
         RubyFixnum zero = RubyFixnum.zero(getRuntime());
 
         if (!equalInternal(context, z, zero) &&
                 ((x.callMethod(context, "<", zero).isTrue() &&
                 dividend.callMethod(context, ">", zero).isTrue()) ||
                 (x.callMethod(context, ">", zero).isTrue() &&
                 dividend.callMethod(context, "<", zero).isTrue()))) {
             return z.callMethod(context, "-", dividend);
         } else {
             return z;
         }
     }
 
     /** num_abs
      *
      */
     @JRubyMethod(name = "abs")
     public IRubyObject abs(ThreadContext context) {
         if (callMethod(context, "<", RubyFixnum.zero(getRuntime())).isTrue()) {
             return callMethod(context, "-@");
         }
         return this;
     }
 
     /** num_abs/1.9
      * 
      */
     @JRubyMethod(name = "magnitude", compat = CompatVersion.RUBY1_9)
     public IRubyObject magnitude(ThreadContext context) {
         return abs(context);
     }
 
     /** num_to_int
      * 
      */
     @JRubyMethod(name = "to_int")
     public IRubyObject to_int(ThreadContext context) {
         return Helpers.invoke(context, this, "to_i");
     }
 
     /** num_real_p
     *
     */
     @JRubyMethod(name = "real?", compat = CompatVersion.RUBY1_9)
     public IRubyObject scalar_p() {
         return getRuntime().getTrue();
     }
 
     /** num_int_p
      *
      */
     @JRubyMethod(name = "integer?")
     public IRubyObject integer_p() {
         return getRuntime().getFalse();
     }
     
     /** num_zero_p
      *
      */
     @JRubyMethod(name = "zero?")
     public IRubyObject zero_p(ThreadContext context) {
         return equalInternal(context, this, RubyFixnum.zero(getRuntime())) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
     
     /** num_nonzero_p
      *
      */
     @JRubyMethod(name = "nonzero?")
     public IRubyObject nonzero_p(ThreadContext context) {
         if (callMethod(context, "zero?").isTrue()) {
             return getRuntime().getNil();
         }
         return this;
     }
 
     /** num_floor
      *
      */
     @JRubyMethod(name = "floor")
     public IRubyObject floor() {
         return convertToFloat().floor();
     }
         
     /** num_ceil
      *
      */
     @JRubyMethod(name = "ceil")
     public IRubyObject ceil() {
         return convertToFloat().ceil();
     }
 
     /** num_round
      *
      */
     @JRubyMethod(name = "round")
     public IRubyObject round() {
         return convertToFloat().round();
     }
 
     /** num_truncate
      *
      */
     @JRubyMethod(name = "truncate")
     public IRubyObject truncate() {
         return convertToFloat().truncate();
     }
 
     @JRubyMethod
     public IRubyObject step(ThreadContext context, IRubyObject arg0, Block block) {
         return block.isGiven() ? stepCommon(context, arg0, RubyFixnum.one(context.runtime), block) : enumeratorize(context.runtime, this, "step", arg0);
     }
 
     @JRubyMethod
     public IRubyObject step(ThreadContext context, IRubyObject to, IRubyObject step, Block block) {
         return block.isGiven() ? stepCommon(context, to, step, block) : enumeratorize(context.runtime, this, "step", new IRubyObject[] {to, step});
     }
 
     private IRubyObject stepCommon(ThreadContext context, IRubyObject to, IRubyObject step, Block block) {
         Ruby runtime = context.runtime;
         if (this instanceof RubyFixnum && to instanceof RubyFixnum && step instanceof RubyFixnum) {
             fixnumStep(context, runtime, ((RubyFixnum)this).getLongValue(),
                                          ((RubyFixnum)to).getLongValue(),
                                          ((RubyFixnum)step).getLongValue(),
                                           block);
         } else if (this instanceof RubyFloat || to instanceof RubyFloat || step instanceof RubyFloat) {
             floatStep19(context, runtime, this, to, step, false, block);
         } else {
             duckStep(context, runtime, this, to, step, block);
         }
         return this;
     }
 
     private static void fixnumStep(ThreadContext context, Ruby runtime, long from, long to, long step, Block block) {
         // We must avoid integer overflows in "i += step".
         if (step == 0) throw runtime.newArgumentError("step cannot be 0");
         if (step > 0) {
             long tov = Long.MAX_VALUE - step;
             if (to < tov) tov = to;
             long i;
             for (i = from; i <= tov; i += step) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
             if (i <= to) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
         } else {
             long tov = Long.MIN_VALUE - step;
             if (to > tov) tov = to;
             long i;
             for (i = from; i >= tov; i += step) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
             if (i >= to) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
         }
     }
 
     protected static void floatStep(ThreadContext context, Ruby runtime, IRubyObject from, IRubyObject to, IRubyObject step, Block block) { 
         double beg = num2dbl(from);
         double end = num2dbl(to);
         double unit = num2dbl(step);
 
         if (unit == 0) throw runtime.newArgumentError("step cannot be 0");
 
         double n = (end - beg)/unit;
         double err = (Math.abs(beg) + Math.abs(end) + Math.abs(end - beg)) / Math.abs(unit) * DBL_EPSILON;
 
         if (err > 0.5) err = 0.5;            
         n = Math.floor(n + err) + 1;
 
         for (long i = 0; i < n; i++) {
             block.yield(context, RubyFloat.newFloat(runtime, i * unit + beg));
         }
     }
 
     static void floatStep19(ThreadContext context, Ruby runtime, IRubyObject from, IRubyObject to, IRubyObject step, boolean excl, Block block) { 
         double beg = num2dbl(from);
         double end = num2dbl(to);
         double unit = num2dbl(step);
 
         // TODO: remove
         if (unit == 0) throw runtime.newArgumentError("step cannot be 0");
 
         double n = (end - beg)/unit;
         double err = (Math.abs(beg) + Math.abs(end) + Math.abs(end - beg)) / Math.abs(unit) * DBL_EPSILON;
 
         if (Double.isInfinite(unit)) {
             if (unit > 0) block.yield(context, RubyFloat.newFloat(runtime, beg));
         } else {
             if (err > 0.5) err = 0.5;            
             n = Math.floor(n + err);
             if (!excl) n++;
             for (long i = 0; i < n; i++){
                 block.yield(context, RubyFloat.newFloat(runtime, i * unit + beg));
             }
         }
     }
 
     private static void duckStep(ThreadContext context, Ruby runtime, IRubyObject from, IRubyObject to, IRubyObject step, Block block) {
         IRubyObject i = from;
         String cmpString = step.callMethod(context, ">", RubyFixnum.zero(runtime)).isTrue() ? ">" : "<";
 
         while (true) {
             if (i.callMethod(context, cmpString, to).isTrue()) break;
             block.yield(context, i);
             i = i.callMethod(context, "+", step);
         }
     }
 
     /** num_equal, doesn't override RubyObject.op_equal
      *
      */
     protected final IRubyObject op_num_equal(ThreadContext context, IRubyObject other) {
         // it won't hurt fixnums
         if (this == other)  return getRuntime().getTrue();
 
         return invokedynamic(context, other, MethodNames.OP_EQUAL, this);
     }
 
     /** num_numerator
      * 
      */
     @JRubyMethod(name = "numerator", compat = CompatVersion.RUBY1_9)
     public IRubyObject numerator(ThreadContext context) {
         return RubyRational.newRationalConvert(context, this).callMethod(context, "numerator");
     }
     
     /** num_denominator
      * 
      */
     @JRubyMethod(name = "denominator", compat = CompatVersion.RUBY1_9)
     public IRubyObject denominator(ThreadContext context) {
         return RubyRational.newRationalConvert(context, this).callMethod(context, "denominator");
     }
 
     /** numeric_to_c
      * 
      */
     @JRubyMethod(name = "to_c", compat = CompatVersion.RUBY1_9)
     public IRubyObject to_c(ThreadContext context) {
         return RubyComplex.newComplexCanonicalize(context, this);
     }
 
     /** numeric_real
      * 
      */
     @JRubyMethod(name = "real", compat = CompatVersion.RUBY1_9)
     public IRubyObject real(ThreadContext context) {
         return this;
     }
 
     /** numeric_image
      * 
      */
     @JRubyMethod(name = {"imaginary", "imag"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject image(ThreadContext context) {
         return RubyFixnum.zero(context.runtime);
     }
 
     /** numeric_abs2
      * 
      */
     @JRubyMethod(name = "abs2", compat = CompatVersion.RUBY1_9)
     public IRubyObject abs2(ThreadContext context) {
         return f_mul(context, this, this);
     }
 
     /** numeric_arg
      * 
      */
     @JRubyMethod(name = {"arg", "angle", "phase"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject arg(ThreadContext context) {
         double value = this.getDoubleValue();
         if (Double.isNaN(value)) {
             return this;
         }
         if (f_negative_p(context, this) || (value == 0.0 && 1/value == Double.NEGATIVE_INFINITY)) {
             // negative or -0.0
             return context.runtime.getMath().getConstant("PI");
         }
         return RubyFixnum.zero(context.runtime);
     }    
 
     /** numeric_rect
      * 
      */
     @JRubyMethod(name = {"rectangular", "rect"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject rect(ThreadContext context) {
         return context.runtime.newArray(this, RubyFixnum.zero(context.runtime));
     }    
 
     /** numeric_polar
      * 
      */
     @JRubyMethod(name = "polar", compat = CompatVersion.RUBY1_9)
     public IRubyObject polar(ThreadContext context) {
         return context.runtime.newArray(f_abs(context, this), f_arg(context, this));
     }    
 
     /** numeric_real
      * 
      */
     @JRubyMethod(name = {"conjugate", "conj"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject conjugate(ThreadContext context) {
         return this;
     }
 
     @Override
     public Object toJava(Class target) {
         return JavaUtil.getNumericConverter(target).coerce(this, target);
     }
 
     public static class InvalidIntegerException extends NumberFormatException {
         private static final long serialVersionUID = 55019452543252148L;
         
         public InvalidIntegerException() {
             super();
         }
         public InvalidIntegerException(String message) {
             super(message);
         }
         @Override
         public Throwable fillInStackTrace() {
             return this;
         }
     }
     
     public static class NumberTooLargeException extends NumberFormatException {
         private static final long serialVersionUID = -1835120694982699449L;
         public NumberTooLargeException() {
             super();
         }
         public NumberTooLargeException(String message) {
             super(message);
         }
         @Override
         public Throwable fillInStackTrace() {
             return this;
         }
     }
 }
diff --git a/src/org/jruby/RubyRange.java b/src/org/jruby/RubyRange.java
index ced97779bd..bc2bd6a30e 100644
--- a/src/org/jruby/RubyRange.java
+++ b/src/org/jruby/RubyRange.java
@@ -1,759 +1,755 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
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
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
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
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.CallBlock;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.TypeConverter;
 
 import static org.jruby.runtime.Helpers.invokedynamic;
 
 import org.jruby.runtime.invokedynamic.MethodNames;
 
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
 
         result.index = ClassIndex.RANGE;
         result.setReifiedClass(RubyRange.class);
 
-        result.kindOf = new RubyModule.KindOf() {
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyRange;
-            }
-        };
+        result.kindOf = new RubyModule.JavaClassKindOf(RubyRange.class);
         
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
 
     final boolean checkBegin(long length) {
         long beg = RubyNumeric.num2long(this.begin);
         if(beg < 0) {
             beg += length;
             if(beg < 0) {
                 return false;
             }
         } else if(length < beg) {
             return false;
         }
         return true;
     }
 
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
                 IRubyObject result = invokedynamic(context, begin, MethodNames.OP_CMP, end);
                 if (result.isNil()) throw getRuntime().newArgumentError("bad value for range");
             } catch (RaiseException re) {
                 throw getRuntime().newArgumentError("bad value for range");
             }
         }
 
         this.begin = begin;
         this.end = end;
         this.isExclusive = isExclusive;
     }
     
     @JRubyMethod(required = 2, optional = 1, visibility = PRIVATE)
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
         
         long v = invokedynamic(context, begin, MethodNames.HASH).convertToInteger().getLongValue();
         hash ^= v << 1;
         v = invokedynamic(context, end, MethodNames.HASH).convertToInteger().getLongValue();
         hash ^= v << 9;
         hash ^= h << 24;
         return getRuntime().newFixnum(hash);
     }
     
     private static byte[] DOTDOTDOT = "...".getBytes();
     private static byte[] DOTDOT = "..".getBytes();
 
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect(ThreadContext context) {
         RubyString str = inspect(context, begin).strDup(context.runtime);
         RubyString str2 = inspect(context, end);
 
         str.cat(isExclusive ? DOTDOTDOT : DOTDOT);
         str.concat(str2);
         str.infectBy(str2);
         return str;
     }
     
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s(ThreadContext context) {
         RubyString str = RubyString.objAsString(context, begin).strDup(context.runtime);
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
             return context.runtime.getNil();
         }
 
         void call(ThreadContext context, IRubyObject arg) {
             if (iter instanceof RubyFixnum) {
                 iter = RubyFixnum.newFixnum(context.runtime, ((RubyFixnum)iter).getLongValue() - 1);
             } else {
                 iter = iter.callMethod(context, "-", RubyFixnum.one(context.runtime));
             }
             if (iter == RubyFixnum.zero(context.runtime)) {
                 block.yield(context, arg);
                 iter = step;
             }
         }
     }
 
     private IRubyObject rangeLt(ThreadContext context, IRubyObject a, IRubyObject b) {
         IRubyObject result = invokedynamic(context, a, MethodNames.OP_CMP, b);
         if (result.isNil()) return null;
         return RubyComparable.cmpint(context, result, a, b) < 0 ? getRuntime().getTrue() : null;
     }
 
     private IRubyObject rangeLe(ThreadContext context, IRubyObject a, IRubyObject b) {
         IRubyObject result = invokedynamic(context, a, MethodNames.OP_CMP, b);
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
 
     @JRubyMethod
     public IRubyObject to_a(ThreadContext context, final Block block) {
         final Ruby runtime = context.runtime;
 
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
             return RubyEnumerable.to_a(context, this);
         }
     }
 
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject each(ThreadContext context, final Block block) {
         final Ruby runtime = context.runtime;
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
         // We must avoid integer overflows.
         long to = ((RubyFixnum) end).getLongValue();
         if (isExclusive) {
             if (to == Long.MIN_VALUE) return;
             to--;
         }
         long from = ((RubyFixnum) begin).getLongValue();
         if (block.getBody().getArgumentType() == BlockBody.ZERO_ARGS) {
             IRubyObject nil = runtime.getNil();
             long i;
             for (i = from; i < to; i++) {
                 block.yield(context, nil);
             }
             if (i <= to) {
                 block.yield(context, nil);
             }
         } else {
             long i;
             for (i = from; i < to; i++) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
             if (i <= to) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
         }
     }
 
     @JRubyMethod(name = "each", compat = RUBY1_9)
     public IRubyObject each19(final ThreadContext context, final Block block) {
         Ruby runtime = context.runtime;
         if (!block.isGiven()) return enumeratorize(runtime, this, "each");
 
         if (begin instanceof RubyTime) {
             throw runtime.newTypeError("can't iterate from Time");
         } else if (begin instanceof RubyFixnum && end instanceof RubyFixnum) {
             fixnumEach(context, runtime, block);
         } else if (begin instanceof RubyString) {
             ((RubyString) begin).uptoCommon19(context, end, isExclusive, block);
         } else if (begin instanceof RubySymbol) {
             begin.asString().uptoCommon19(context, end.asString(), isExclusive, block, true);
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
 
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject step(ThreadContext context, IRubyObject step, Block block) {
         return block.isGiven() ? stepCommon(context, step, block) : enumeratorize(context.runtime, this, "step", step);
     }
 
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject step(ThreadContext context, Block block) {
         return block.isGiven() ? stepCommon(context, RubyFixnum.one(context.runtime), block)  : enumeratorize(context.runtime, this, "step");
     }
 
     private IRubyObject stepCommon(ThreadContext context, IRubyObject step, Block block) {
         final Ruby runtime = context.runtime;
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
 
     private void fixnumStep(ThreadContext context, Ruby runtime, long step, Block block) {
         // We must avoid integer overflows.
         // Any method calling this method must ensure that "step" is greater than 0.
         long to = ((RubyFixnum) end).getLongValue();
         if (isExclusive) {
             if (to == Long.MIN_VALUE) return;
             to--;
         }
         long tov = Long.MAX_VALUE - step;
         if (to < tov) tov = to;
         long i;
         for (i = ((RubyFixnum)begin).getLongValue(); i <= tov; i += step) {
             block.yield(context, RubyFixnum.newFixnum(runtime, i));
         }
         if (i <= to) {
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
 
     @JRubyMethod(name = "step", compat = RUBY1_9)
     public IRubyObject step19(final ThreadContext context, final Block block) {
         return block.isGiven() ? stepCommon19(context, RubyFixnum.one(context.runtime), block) : enumeratorize(context.runtime, this, "step");
     }
 
     @JRubyMethod(name = "step", compat = RUBY1_9)
     public IRubyObject step19(final ThreadContext context, IRubyObject step, final Block block) {
         Ruby runtime = context.runtime;
         if (!block.isGiven()) return enumeratorize(runtime, this, "step", step);
 
         if (!(step instanceof RubyNumeric)) step = step.convertToInteger("to_int");
         IRubyObject zero = RubyFixnum.zero(runtime);
         if (step.callMethod(context, "<", zero).isTrue()) throw runtime.newArgumentError("step can't be negative");
         if (!step.callMethod(context, ">", zero).isTrue()) throw runtime.newArgumentError("step can't be 0");
         return stepCommon19(context, step, block);
     }
 
     private IRubyObject stepCommon19(ThreadContext context, IRubyObject step, Block block) {
         Ruby runtime = context.runtime;
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum && step instanceof RubyFixnum) {
             fixnumStep(context, runtime, ((RubyFixnum)step).getLongValue(), block);
         } else if (begin instanceof RubyFloat || end instanceof RubyFloat || step instanceof RubyFloat) {
             RubyNumeric.floatStep19(context, runtime, begin, end, step, isExclusive, block);
         } else if (begin instanceof RubyNumeric ||
                         !TypeConverter.checkIntegerType(runtime, begin, "to_int").isNil() ||
                         !TypeConverter.checkIntegerType(runtime, end, "to_int").isNil()) {
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
 
     @JRubyMethod(name = {"include?", "member?", "==="}, required = 1, compat = RUBY1_8)
     public RubyBoolean include_p(ThreadContext context, IRubyObject obj) {
         if (rangeLe(context, begin, obj) != null) {
             if (isExclusive) {
                 if (rangeLt(context, obj, end) != null) {
                     return context.runtime.getTrue();
                 }
             } else {
                 if (rangeLe(context, obj, end) != null) {
                     return context.runtime.getTrue();
                 }
             }
         }
         return context.runtime.getFalse();
     }
 
     // framed for invokeSuper
     @JRubyMethod(name = {"include?", "member?"}, frame = true, compat = RUBY1_9)
     public IRubyObject include_p19(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.runtime;
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
                 ((RubyString) begin).getByteList().getRealSize() == 1 &&
                 ((RubyString) end).getByteList().getRealSize() == 1) {
             if (obj.isNil()) return runtime.getFalse();
             if (obj instanceof RubyString) {
                 ByteList Vbytes = ((RubyString)obj).getByteList();
                 if (Vbytes.getRealSize() != 1) return runtime.getFalse();
                 int v = Vbytes.getUnsafeBytes()[Vbytes.getBegin()] & 0xff;
                 ByteList Bbytes = ((RubyString)begin).getByteList();
                 int b = Bbytes.getUnsafeBytes()[Bbytes.getBegin()] & 0xff;
                 ByteList Ebytes = ((RubyString)end).getByteList();
                 int e = Ebytes.getUnsafeBytes()[Ebytes.getBegin()] & 0xff;
                 if (Encoding.isAscii(v) && Encoding.isAscii(b) && Encoding.isAscii(e)) {
                     if ((b <= v && v < e) || (!isExclusive && v == e)) return runtime.getTrue();
                     return runtime.getFalse();
                 }
             }
         }
         return Helpers.invokeSuper(context, this, obj, Block.NULL_BLOCK);
     }
 
     @JRubyMethod(name = "===", compat = RUBY1_9)
     public IRubyObject eqq_p19(ThreadContext context, IRubyObject obj) {
         return callMethod(context, "include?", obj);
     }
 
     @JRubyMethod(name = "cover?", compat = RUBY1_9)
     public IRubyObject cover_p(ThreadContext context, IRubyObject obj) {
         return include_p(context, obj); // 1.8 "include?"
     }
 
     @JRubyMethod(compat = RUBY1_9, frame = true)
     public IRubyObject min(ThreadContext context, Block block) {
         if (block.isGiven()) {
             return Helpers.invokeSuper(context, this, block);
         } else {
             int c = RubyComparable.cmpint(context, invokedynamic(context, begin, MethodNames.OP_CMP, end), begin, end);
             if (c > 0 || (c == 0 && isExclusive)) {
                 return context.runtime.getNil();
             }
             return begin;
         }
     }
 
     @JRubyMethod(compat = RUBY1_9, frame = true)
     public IRubyObject max(ThreadContext context, Block block) {
         if (begin.callMethod(context, ">", end).isTrue()) {
             return context.runtime.getNil();
         }
         if (block.isGiven() || isExclusive && !(end instanceof RubyNumeric)) {
             return Helpers.invokeSuper(context, this, block);
         } else {
             int c = RubyComparable.cmpint(context, invokedynamic(context, begin, MethodNames.OP_CMP, end), begin, end);
             Ruby runtime = context.runtime;
             if (isExclusive) {
                 if (!(end instanceof RubyInteger)) throw runtime.newTypeError("cannot exclude non Integer end value");
                 if (c == 0) return runtime.getNil();
                 if (end instanceof RubyFixnum) return RubyFixnum.newFixnum(runtime, ((RubyFixnum)end).getLongValue() - 1);
                 return end.callMethod(context, "-", RubyFixnum.one(runtime));
             }
             return end;
         }
     }
 
     @JRubyMethod(name = "first", compat = RUBY1_9)
     public IRubyObject first(ThreadContext context) {
         return begin;
     }
 
     @JRubyMethod(name = "first", compat = RUBY1_9)
     public IRubyObject first(ThreadContext context, IRubyObject arg) {
         final Ruby runtime = context.runtime;
         final int num = RubyNumeric.num2int(arg);
         if (num < 0) {
             throw context.runtime.newArgumentError("negative array size (or size too big)");
         }
         final RubyArray result = runtime.newArray(num);
         try {
             RubyEnumerable.callEach(runtime, context, this, Arity.ONE_ARGUMENT, new BlockCallback() {
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
 
     @JRubyMethod(name = "last", compat = RUBY1_9)
     public IRubyObject last(ThreadContext context) {
         return end;
     }
 
     @JRubyMethod(name = "last", compat = RUBY1_9)
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
 
             // FIXME: Maybe we can just gank these off the line directly?
             unmarshalStream.defaultVariablesUnmarshal(range);
             
             range.begin = (IRubyObject)range.removeInternalVariable("begin");
             range.end = (IRubyObject)range.removeInternalVariable("end");
             range.isExclusive = ((IRubyObject)range.removeInternalVariable("excl")).isTrue();
 
             return range;
         }
     };
 }
diff --git a/src/org/jruby/RubyRational.java b/src/org/jruby/RubyRational.java
index 1403b26724..70893b95d9 100644
--- a/src/org/jruby/RubyRational.java
+++ b/src/org/jruby/RubyRational.java
@@ -1,1088 +1,1083 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import static org.jruby.util.Numeric.checkInteger;
 import static org.jruby.util.Numeric.f_abs;
 import static org.jruby.util.Numeric.f_add;
 import static org.jruby.util.Numeric.f_cmp;
 import static org.jruby.util.Numeric.f_div;
 import static org.jruby.util.Numeric.f_equal;
 import static org.jruby.util.Numeric.f_expt;
 import static org.jruby.util.Numeric.f_floor;
 import static org.jruby.util.Numeric.f_gcd;
 import static org.jruby.util.Numeric.f_idiv;
 import static org.jruby.util.Numeric.f_inspect;
 import static org.jruby.util.Numeric.f_integer_p;
 import static org.jruby.util.Numeric.f_lt_p;
 import static org.jruby.util.Numeric.f_mul;
 import static org.jruby.util.Numeric.f_negate;
 import static org.jruby.util.Numeric.f_negative_p;
 import static org.jruby.util.Numeric.f_one_p;
 import static org.jruby.util.Numeric.f_rshift;
 import static org.jruby.util.Numeric.f_sub;
 import static org.jruby.util.Numeric.f_to_f;
 import static org.jruby.util.Numeric.f_to_i;
 import static org.jruby.util.Numeric.f_to_r;
 import static org.jruby.util.Numeric.f_to_s;
 import static org.jruby.util.Numeric.f_truncate;
 import static org.jruby.util.Numeric.f_xor;
 import static org.jruby.util.Numeric.f_zero_p;
 import static org.jruby.util.Numeric.i_gcd;
 import static org.jruby.util.Numeric.i_ilog2;
 import static org.jruby.util.Numeric.k_exact_p;
 import static org.jruby.util.Numeric.ldexp;
 import static org.jruby.util.Numeric.nurat_rationalize_internal;
 
 import org.jcodings.specific.ASCIIEncoding;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.Numeric;
 
 import static org.jruby.runtime.Helpers.invokedynamic;
 import static org.jruby.runtime.invokedynamic.MethodNames.HASH;
 
 /**
  *  1.9 rational.c as of revision: 20011
  */
 
 @JRubyClass(name = "Rational", parent = "Numeric", include = "Precision")
 public class RubyRational extends RubyNumeric {
     
     public static RubyClass createRationalClass(Ruby runtime) {
         RubyClass rationalc = runtime.defineClass("Rational", runtime.getNumeric(), RATIONAL_ALLOCATOR);
         runtime.setRational(rationalc);
 
         rationalc.index = ClassIndex.RATIONAL;
         rationalc.setReifiedClass(RubyRational.class);
         
-        rationalc.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyRational;
-            }
-        };
+        rationalc.kindOf = new RubyModule.JavaClassKindOf(RubyRational.class);
 
         rationalc.defineAnnotatedMethods(RubyRational.class);
 
         rationalc.getSingletonClass().undefineMethod("allocate");
         rationalc.getSingletonClass().undefineMethod("new");
 
         return rationalc;
     }
 
     private static ObjectAllocator RATIONAL_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyFixnum zero = RubyFixnum.zero(runtime);
             return new RubyRational(runtime, klass, zero, zero);
         }
     };
 
     /** internal
      * 
      */
     private RubyRational(Ruby runtime, IRubyObject clazz, IRubyObject num, IRubyObject den) {
         super(runtime, (RubyClass)clazz);
         this.num = num;
         this.den = den;
     }
 
     /** rb_rational_raw
      * 
      */
     static RubyRational newRationalRaw(Ruby runtime, IRubyObject x, IRubyObject y) {
         return new RubyRational(runtime, runtime.getRational(), x, y);
     }
 
     /** rb_rational_raw1
      * 
      */
     static RubyRational newRationalRaw(Ruby runtime, IRubyObject x) {
         return new RubyRational(runtime, runtime.getRational(), x, RubyFixnum.one(runtime));
     }
 
     /** rb_rational_new1
      * 
      */
     static IRubyObject newRationalCanonicalize(ThreadContext context, IRubyObject x) {
         return newRationalCanonicalize(context, x, RubyFixnum.one(context.runtime));
     }
 
     /** rb_rational_new
      * 
      */
     private static IRubyObject newRationalCanonicalize(ThreadContext context, IRubyObject x, IRubyObject y) {
         return canonicalizeInternal(context, context.runtime.getRational(), x, y);
     }
     
     /** f_rational_new2
      * 
      */
     private static IRubyObject newRational(ThreadContext context, IRubyObject clazz, IRubyObject x, IRubyObject y) {
         assert !(x instanceof RubyRational) && !(y instanceof RubyRational);
         return canonicalizeInternal(context, clazz, x, y);
     }
 
     /** f_rational_new_no_reduce2
      * 
      */
     private static IRubyObject newRationalNoReduce(ThreadContext context, IRubyObject clazz, IRubyObject x, IRubyObject y) {
         assert !(x instanceof RubyRational) && !(y instanceof RubyRational);
         return canonicalizeInternalNoReduce(context, clazz, x, y);
     }
 
     /** f_rational_new_bang2
      * 
      */
     private static RubyRational newRationalBang(ThreadContext context, IRubyObject clazz, IRubyObject x, IRubyObject y) { 
         assert !f_negative_p(context, y) && !(f_zero_p(context, y));
         return new RubyRational(context.runtime, clazz, x, y);
     }
 
     /** f_rational_new_bang1
      * 
      */
     private static RubyRational newRationalBang(ThreadContext context, IRubyObject clazz, IRubyObject x) {
         return newRationalBang(context, clazz, x, RubyFixnum.one(context.runtime));
     }
     
     private IRubyObject num;
     private IRubyObject den;
 
     /** nurat_s_new_bang
      * 
      */
     @Deprecated
     public static IRubyObject newInstanceBang(ThreadContext context, IRubyObject recv, IRubyObject[]args) {
         switch (args.length) {
         case 1: return newInstanceBang(context, recv, args[0]);
         case 2: return newInstanceBang(context, recv, args[0], args[1]);
         }
         Arity.raiseArgumentError(context.runtime, args.length, 1, 1);
         return null;
     }
 
     @JRubyMethod(name = "new!", meta = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject newInstanceBang(ThreadContext context, IRubyObject recv, IRubyObject num) {
         return newInstanceBang(context, recv, num, RubyFixnum.one(context.runtime));
     }
 
     @JRubyMethod(name = "new!", meta = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject newInstanceBang(ThreadContext context, IRubyObject recv, IRubyObject num, IRubyObject den) {
         if (!(num instanceof RubyInteger)) num = f_to_i(context, num);
         if (!(den instanceof RubyInteger)) den = f_to_i(context, den);
 
         Ruby runtime = context.runtime;
         IRubyObject res = f_cmp(context, den, RubyFixnum.zero(runtime));
         if (res == RubyFixnum.minus_one(runtime)) {
             num = f_negate(context, num);
             den = f_negate(context, den);
         } else if (res == RubyFixnum.zero(runtime)) {
             throw runtime.newZeroDivisionError();
         }
 
         return new RubyRational(runtime, recv, num, den);
     }
 
     /** nurat_canonicalization
      *
      */
     private static boolean canonicalization = false;
     public static void setCanonicalization(boolean canonical) {
         canonicalization = canonical;
     }
 
     /** nurat_int_check
      * 
      */
     static void intCheck(ThreadContext context, IRubyObject num) {
         if (num instanceof RubyFixnum || num instanceof RubyBignum) return;
         if (!(num instanceof RubyNumeric) || !num.callMethod(context, "integer?").isTrue()) {
             Ruby runtime = num.getRuntime();
             if (runtime.is1_9()) {
                 throw runtime.newTypeError("can't convert "
                         + num.getMetaClass().getName() + " into Rational");
             } else {
                 throw runtime.newArgumentError("not an integer");
             }
         }
     }
 
     /** nurat_int_value
      * 
      */
     static IRubyObject intValue(ThreadContext context, IRubyObject num) {
         intCheck(context, num);
         if (!(num instanceof RubyInteger)) num = num.callMethod(context, "to_f");
         return num;
     }
     
     /** nurat_s_canonicalize_internal
      * 
      */
     private static IRubyObject canonicalizeInternal(ThreadContext context, IRubyObject clazz, IRubyObject num, IRubyObject den) {
         Ruby runtime = context.runtime;
         IRubyObject res = f_cmp(context, den, RubyFixnum.zero(runtime));
         if (res == RubyFixnum.minus_one(runtime)) {
             num = f_negate(context, num);
             den = f_negate(context, den);
         } else if (res == RubyFixnum.zero(runtime)) {
             throw runtime.newZeroDivisionError();            
         }
 
         IRubyObject gcd = f_gcd(context, num, den);
         num = f_idiv(context, num, gcd);
         den = f_idiv(context, den, gcd);
 
         if (Numeric.CANON) {
             if (f_one_p(context, den) && canonicalization) return num;
         }
 
         return new RubyRational(context.runtime, clazz, num, den);
     }
     
     /** nurat_s_canonicalize_internal_no_reduce
      * 
      */
     private static IRubyObject canonicalizeInternalNoReduce(ThreadContext context, IRubyObject clazz, IRubyObject num, IRubyObject den) {
         Ruby runtime = context.runtime;
         IRubyObject res = f_cmp(context, den, RubyFixnum.zero(runtime));
         if (res == RubyFixnum.minus_one(runtime)) {
             num = f_negate(context, num);
             den = f_negate(context, den);
         } else if (res == RubyFixnum.zero(runtime)) {
             throw runtime.newZeroDivisionError();            
         }        
 
         if (Numeric.CANON) {
             if (f_one_p(context, den) && canonicalization) return num;
         }
 
         return new RubyRational(context.runtime, clazz, num, den);
     }
     
     /** nurat_s_new
      * 
      */
     @Deprecated
     public static IRubyObject newInstance(ThreadContext context, IRubyObject clazz, IRubyObject[]args) {
         switch (args.length) {
         case 1: return newInstance(context, clazz, args[0]);
         case 2: return newInstance(context, clazz, args[0], args[1]);
         }
         Arity.raiseArgumentError(context.runtime, args.length, 1, 1);
         return null;
     }
 
     // @JRubyMethod(name = "new", meta = true, visibility = Visibility.PRIVATE)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject clazz, IRubyObject num) {
         num = intValue(context, num);
         return canonicalizeInternal(context, clazz, num, RubyFixnum.one(context.runtime));
     }
 
     // @JRubyMethod(name = "new", meta = true, visibility = Visibility.PRIVATE)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject clazz, IRubyObject num, IRubyObject den) {
         num = intValue(context, num);
         den = intValue(context, den);
         return canonicalizeInternal(context, clazz, num, den);
     }
     
     /** rb_Rational1
      * 
      */
     public static IRubyObject newRationalConvert(ThreadContext context, IRubyObject x) {
         return newRationalConvert(context, x, RubyFixnum.one(context.runtime));
     }
 
     /** rb_Rational/rb_Rational2
      * 
      */
     public static IRubyObject newRationalConvert(ThreadContext context, IRubyObject x, IRubyObject y) {
         return convert(context, context.runtime.getRational(), x, y);
     }
     
     public static RubyRational newRational(Ruby runtime, long x, long y) {
         return new RubyRational(runtime, runtime.getRational(), runtime.newFixnum(x), runtime.newFixnum(y));
     }
     
     @Deprecated
     public static IRubyObject convert(ThreadContext context, IRubyObject clazz, IRubyObject[]args) {
         switch (args.length) {
         case 1: return convert(context, clazz, args[0]);        
         case 2: return convert(context, clazz, args[0], args[1]);
         }
         Arity.raiseArgumentError(context.runtime, args.length, 1, 1);
         return null;
     }
 
     /** nurat_s_convert
      * 
      */
     @JRubyMethod(name = "convert", meta = true, visibility = Visibility.PRIVATE)
     public static IRubyObject convert(ThreadContext context, IRubyObject recv, IRubyObject a1) {
         if (a1.isNil()) {
             throw context.runtime.newTypeError("can't convert nil into Rational");
         }
 
         return convertCommon(context, recv, a1, context.runtime.getNil());
     }
 
     /** nurat_s_convert
      * 
      */
     @JRubyMethod(name = "convert", meta = true, visibility = Visibility.PRIVATE)
     public static IRubyObject convert(ThreadContext context, IRubyObject recv, IRubyObject a1, IRubyObject a2) {
         if (a1.isNil() || a2.isNil()) {
             throw context.runtime.newTypeError("can't convert nil into Rational");
         }
         
         return convertCommon(context, recv, a1, a2);
     }
     
     private static IRubyObject convertCommon(ThreadContext context, IRubyObject recv, IRubyObject a1, IRubyObject a2) {
         if (a1 instanceof RubyComplex) {
             RubyComplex a1Complex = (RubyComplex)a1;
             if (k_exact_p(a1Complex.getImage()) && f_zero_p(context, a1Complex.getImage())) a1 = a1Complex.getReal();            
         }
 
         if (a2 instanceof RubyComplex) {
             RubyComplex a2Complex = (RubyComplex)a2;
             if (k_exact_p(a2Complex.getImage()) && f_zero_p(context, a2Complex.getImage())) a2 = a2Complex.getReal();
         }
         
         if (a1 instanceof RubyFloat) {
             a1 = f_to_r(context, a1);
         } else if (a1 instanceof RubyString) {
             a1 = str_to_r_strict(context, a1);
         } else {
             if (a1.respondsTo("to_r")) {
                 a1 = f_to_r(context, a1);
             }
         }
         
         if (a2 instanceof RubyFloat) {
             a2 = f_to_r(context, a2);
         } else if (a2 instanceof RubyString) {
             a2 = str_to_r_strict(context, a2);
         }
 
         if (a1 instanceof RubyRational) {
             if (a2.isNil() || (k_exact_p(a2) && f_one_p(context, a2))) return a1;
         }
 
         if (a2.isNil()) {
             if (a1 instanceof RubyNumeric && !f_integer_p(context, a1).isTrue()) return a1;
             return newInstance(context, recv, a1);
         } else {
             if (a1 instanceof RubyNumeric && a2 instanceof RubyNumeric &&
                 (!f_integer_p(context, a1).isTrue() || !f_integer_p(context, a2).isTrue())) {
                 return f_div(context, a1, a2);
             }
             return newInstance(context, recv, a1, a2);
         }
     }
 
     /** nurat_numerator
      * 
      */
     @JRubyMethod(name = "numerator")
     @Override
     public IRubyObject numerator(ThreadContext context) {
         return num;
     }
 
     /** nurat_denominator
      * 
      */
     @JRubyMethod(name = "denominator")
     @Override
     public IRubyObject denominator(ThreadContext context) {
         return den;
     }
 
     /** f_imul
      * 
      */
     private static IRubyObject f_imul(ThreadContext context, long a, long b) {
         Ruby runtime = context.runtime;
         if (a == 0 || b == 0) {
             return RubyFixnum.zero(runtime);
         } else if (a == 1) {
             return RubyFixnum.newFixnum(runtime, b);
         } else if (b == 1) {
             return RubyFixnum.newFixnum(runtime, a);
         }
 
         long c = a * b;
         if(c / a != b) {
             return RubyBignum.newBignum(runtime, a).op_mul(context, RubyBignum.newBignum(runtime, b));
         }
         return RubyFixnum.newFixnum(runtime, c);
     }
     
     /** f_addsub
      * 
      */
     private IRubyObject f_addsub(ThreadContext context, IRubyObject anum, IRubyObject aden, IRubyObject bnum, IRubyObject bden, boolean plus) {
         Ruby runtime = context.runtime;
         IRubyObject newNum, newDen, g, a, b;
         if (anum instanceof RubyFixnum && aden instanceof RubyFixnum &&
             bnum instanceof RubyFixnum && bden instanceof RubyFixnum) {
             long an = ((RubyFixnum)anum).getLongValue();
             long ad = ((RubyFixnum)aden).getLongValue();
             long bn = ((RubyFixnum)bnum).getLongValue();
             long bd = ((RubyFixnum)bden).getLongValue();
             long ig = i_gcd(ad, bd);
 
             g = RubyFixnum.newFixnum(runtime, ig);
             a = f_imul(context, an, bd / ig);
             b = f_imul(context, bn, ad / ig);
         } else {
             g = f_gcd(context, aden, bden);
             a = f_mul(context, anum, f_idiv(context, bden, g));
             b = f_mul(context, bnum, f_idiv(context, aden, g));
         }
 
         IRubyObject c = plus ? f_add(context, a, b) : f_sub(context, a, b);
 
         b = f_idiv(context, aden, g);
         g = f_gcd(context, c, g);
         newNum = f_idiv(context, c, g);
         a = f_idiv(context, bden, g);
         newDen = f_mul(context, a, b);
         
         return RubyRational.newRationalNoReduce(context, getMetaClass(), newNum, newDen);
     }
     
     /** nurat_add
      * 
      */
     @JRubyMethod(name = "+")
     public IRubyObject op_add(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum || other instanceof RubyBignum) {
             return f_addsub(context, num, den, other, RubyFixnum.one(context.runtime), true);
         } else if (other instanceof RubyFloat) {
             return f_add(context, f_to_f(context, this), other);
         } else if (other instanceof RubyRational) {
             RubyRational otherRational = (RubyRational)other;
             return f_addsub(context, num, den, otherRational.num, otherRational.den, true);
         }            
         return coerceBin(context, "+", other);
     }
     
     /** nurat_sub
      * 
      */
     @JRubyMethod(name = "-")
     public IRubyObject op_sub(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum || other instanceof RubyBignum) {
             return f_addsub(context, num, den, other, RubyFixnum.one(context.runtime), false);
         } else if (other instanceof RubyFloat) {
             return f_sub(context, f_to_f(context, this), other);
         } else if (other instanceof RubyRational) {
             RubyRational otherRational = (RubyRational)other;
             return f_addsub(context, num, den, otherRational.num, otherRational.den, false);
         }
         return coerceBin(context, "-", other);
     }
     
     /** f_muldiv
      * 
      */
     private IRubyObject f_muldiv(ThreadContext context, IRubyObject anum, IRubyObject aden, IRubyObject bnum, IRubyObject bden, boolean mult) {
         if (!mult) {
             if (f_negative_p(context, bnum)) {
                 anum = f_negate(context, anum);
                 bnum = f_negate(context, bnum);
             }
             IRubyObject tmp =  bnum;
             bnum = bden;
             bden = tmp;
         }
         
         final IRubyObject newNum, newDen;
         if (anum instanceof RubyFixnum && aden instanceof RubyFixnum &&
             bnum instanceof RubyFixnum && bden instanceof RubyFixnum) {
             long an = ((RubyFixnum)anum).getLongValue();
             long ad = ((RubyFixnum)aden).getLongValue();
             long bn = ((RubyFixnum)bnum).getLongValue();
             long bd = ((RubyFixnum)bden).getLongValue();
             long g1 = i_gcd(an, bd);
             long g2 = i_gcd(ad, bn);
             
             newNum = f_imul(context, an / g1, bn / g2);
             newDen = f_imul(context, ad / g2, bd / g1);
         } else {
             IRubyObject g1 = f_gcd(context, anum, bden); 
             IRubyObject g2 = f_gcd(context, aden, bnum);
             
             newNum = f_mul(context, f_idiv(context, anum, g1), f_idiv(context, bnum, g2));
             newDen = f_mul(context, f_idiv(context, aden, g2), f_idiv(context, bden, g1));
         }
 
         return RubyRational.newRationalNoReduce(context, getMetaClass(), newNum, newDen);
     }
 
     /** nurat_mul
      * 
      */
     @JRubyMethod(name = "*")
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum || other instanceof RubyBignum) {
             return f_muldiv(context, num, den, other, RubyFixnum.one(context.runtime), true);
         } else if (other instanceof RubyFloat) {
             return f_mul(context, f_to_f(context, this), other);
         } else if (other instanceof RubyRational) {
             RubyRational otherRational = (RubyRational)other;
             return f_muldiv(context, num, den, otherRational.num, otherRational.den, true);
         }
         return coerceBin(context, "*", other);
     }
     
     /** nurat_div
      * 
      */
     @JRubyMethod(name = {"/", "quo"})
     public IRubyObject op_div(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum || other instanceof RubyBignum) {
             if (f_zero_p(context, other)) {
                 throw context.runtime.newZeroDivisionError();
             }
             return f_muldiv(context, num, den, other, RubyFixnum.one(context.runtime), false);
         } else if (other instanceof RubyFloat) {
             return f_to_f(context, this).callMethod(context, "/", other);
         } else if (other instanceof RubyRational) {
             if (f_zero_p(context, other)) {
                 throw context.runtime.newZeroDivisionError();
             }
             RubyRational otherRational = (RubyRational)other;
             return f_muldiv(context, num, den, otherRational.num, otherRational.den, false);
         }
         return coerceBin(context, "/", other);
     }
 
     /** nurat_fdiv
      * 
      */
     @JRubyMethod(name = "fdiv")
     public IRubyObject op_fdiv(ThreadContext context, IRubyObject other) {
         return f_div(context, f_to_f(context, this), other);
     }
 
     /** nurat_expt
      * 
      */
     @JRubyMethod(name = "**")
     public IRubyObject op_expt(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
 
         if (k_exact_p(other) && f_zero_p(context, other)) {
             return RubyRational.newRationalBang(context, getMetaClass(), RubyFixnum.one(runtime));
         }
 
         if (other instanceof RubyRational) {
             RubyRational otherRational = (RubyRational)other;
             if (f_one_p(context, otherRational.den)) other = otherRational.num;
         }
 
         if (other instanceof RubyFixnum || other instanceof RubyBignum) {        
             final IRubyObject tnum, tden;
             IRubyObject res = f_cmp(context, other, RubyFixnum.zero(runtime));
             if (res == RubyFixnum.one(runtime)) {
                 tnum = f_expt(context, num, other);
                 tden = f_expt(context, den, other);
             } else if (res == RubyFixnum.minus_one(runtime)){
                 tnum = f_expt(context, den, f_negate(context, other));
                 tden = f_expt(context, num, f_negate(context, other));
             } else {
                 tnum = tden = RubyFixnum.one(runtime);
             }
             return RubyRational.newRational(context, getMetaClass(), tnum, tden);
         } else if (other instanceof RubyFloat || other instanceof RubyRational) {
             return f_expt(context, f_to_f(context, this), other);
         }
         return coerceBin(context, "**", other);
     }
 
     
     /** nurat_cmp
      * 
      */
     @JRubyMethod(name = "<=>")
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyFixnum || other instanceof RubyBignum) {
             if (den instanceof RubyFixnum && ((RubyFixnum)den).getLongValue() == 1) return f_cmp(context, num, other);
             return f_cmp(context, this, RubyRational.newRationalBang(context, getMetaClass(), other));
         } else if (other instanceof RubyFloat) {
             return f_cmp(context, f_to_f(context, this), other);
         } else if (other instanceof RubyRational) {
             RubyRational otherRational = (RubyRational)other;
             final IRubyObject num1, num2;
             if (num instanceof RubyFixnum && den instanceof RubyFixnum &&
                 otherRational.num instanceof RubyFixnum && otherRational.den instanceof RubyFixnum) {
                 num1 = f_imul(context, ((RubyFixnum)num).getLongValue(), ((RubyFixnum)otherRational.den).getLongValue());
                 num2 = f_imul(context, ((RubyFixnum)otherRational.num).getLongValue(), ((RubyFixnum)den).getLongValue());
             } else {
                 num1 = f_mul(context, num, otherRational.den);
                 num2 = f_mul(context, otherRational.num, den);
             }
             return f_cmp(context, f_sub(context, num1, num2), RubyFixnum.zero(context.runtime));
         }
         return coerceBin(context, "<=>", other);             
     }
 
     /** nurat_equal_p
      * 
      */
     @JRubyMethod(name = "==")
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         if (other instanceof RubyFixnum || other instanceof RubyBignum) {
             if (f_zero_p(context, num) && f_zero_p(context, other)) return runtime.getTrue();
             if (!(den instanceof RubyFixnum) || ((RubyFixnum)den).getLongValue() != 1) return runtime.getFalse();
             return f_equal(context, num, other);
         } else if (other instanceof RubyFloat) {
             return f_equal(context, f_to_f(context, this), other);
         } else if (other instanceof RubyRational) {
             RubyRational otherRational = (RubyRational)other;
             if (f_zero_p(context, num) && f_zero_p(context, otherRational.num)) return runtime.getTrue();
             return runtime.newBoolean(f_equal(context, num, otherRational.num).isTrue() &&
                     f_equal(context, den, otherRational.den).isTrue());
 
         }
         return f_equal(context, other, this);
     }
 
     /** nurat_coerce
      * 
      */
     @JRubyMethod(name = "coerce")
     public IRubyObject op_coerce(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         if (other instanceof RubyFixnum || other instanceof RubyBignum) {
             return runtime.newArray(RubyRational.newRationalBang(context, getMetaClass(), other), this);
         } else if (other instanceof RubyFloat) {
             return runtime.newArray(other, f_to_f(context, this));
         } else if (other instanceof RubyRational) {
             return runtime.newArray(other, this);
         }
         throw runtime.newTypeError(other.getMetaClass() + " can't be coerced into " + getMetaClass());
     }
 
     /** nurat_idiv
      * 
      */
     @JRubyMethod(name = "div", compat = CompatVersion.RUBY1_8)
     public IRubyObject op_idiv(ThreadContext context, IRubyObject other) {
         return f_floor(context, f_div(context, this, other));
     }
 
     @JRubyMethod(name = "div", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_idiv19(ThreadContext context, IRubyObject other) {
         if (num2dbl(other) == 0.0) {
             throw context.runtime.newZeroDivisionError();
         }
         return op_idiv(context, other);
     }
 
     /** nurat_mod
      * 
      */
     @JRubyMethod(name = {"modulo", "%"}, compat = CompatVersion.RUBY1_8)
     public IRubyObject op_mod(ThreadContext context, IRubyObject other) {
         IRubyObject val = f_floor(context, f_div(context, this, other));
         return f_sub(context, this, f_mul(context, other, val));
     }
 
     @JRubyMethod(name = {"modulo", "%"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_mod19(ThreadContext context, IRubyObject other) {
         if (num2dbl(other) == 0.0) {
             throw context.runtime.newZeroDivisionError();
         }
         return op_mod(context, other);
     }
 
     /** nurat_divmod
      * 
      */
     @JRubyMethod(name = "divmod", compat = CompatVersion.RUBY1_8)
     public IRubyObject op_divmod(ThreadContext context, IRubyObject other) {
         IRubyObject val = f_floor(context, f_div(context, this, other));
         return context.runtime.newArray(val, f_sub(context, this, f_mul(context, other, val)));
     }
 
     @JRubyMethod(name = "divmod", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_divmod19(ThreadContext context, IRubyObject other) {
         if (num2dbl(other) == 0.0) {
             throw context.runtime.newZeroDivisionError();
         }
         return op_divmod(context, other);
     }
 
     /** nurat_rem
      * 
      */
     @JRubyMethod(name = "remainder")
     public IRubyObject op_rem(ThreadContext context, IRubyObject other) {
         IRubyObject val = f_truncate(context, f_div(context, this, other));
         return f_sub(context, this, f_mul(context, other, val));
     }
 
     /** nurat_abs
      * 
      */
     @JRubyMethod(name = "abs")
     public IRubyObject op_abs(ThreadContext context) {
         if (!f_negative_p(context, this)) return this;
         return f_negate(context, this);
     }
 
     private IRubyObject op_roundCommonPre(ThreadContext context, IRubyObject n) {
         checkInteger(context, n);
         Ruby runtime = context.runtime;
         return f_expt(context, RubyFixnum.newFixnum(runtime, 10), n);
     }
 
     private IRubyObject op_roundCommonPost(ThreadContext context, IRubyObject s, IRubyObject n, IRubyObject b) {
         s = f_div(context, newRationalBang(context, getMetaClass(), s), b);
         if (f_lt_p(context, n, RubyFixnum.one(context.runtime)).isTrue()) s = f_to_i(context, s);
         return s;
     }
 
     /** nurat_floor
      * 
      */
     @JRubyMethod(name = "floor")
     public IRubyObject op_floor(ThreadContext context) {
         return f_idiv(context, num, den);
     }
 
     @JRubyMethod(name = "floor")
     public IRubyObject op_floor(ThreadContext context, IRubyObject n) {
         IRubyObject b = op_roundCommonPre(context, n);
         return op_roundCommonPost(context, ((RubyRational)f_mul(context, this, b)).op_floor(context), n, b);
     }
 
     /** nurat_ceil
      * 
      */
     @JRubyMethod(name = "ceil")
     public IRubyObject op_ceil(ThreadContext context) {
         return f_negate(context, f_idiv(context, f_negate(context, num), den));
     }
 
     @JRubyMethod(name = "ceil")
     public IRubyObject op_ceil(ThreadContext context, IRubyObject n) {
         IRubyObject b = op_roundCommonPre(context, n);
         return op_roundCommonPost(context, ((RubyRational)f_mul(context, this, b)).op_ceil(context), n, b);
     }
     
     @JRubyMethod(name = "to_i")
     public IRubyObject to_i(ThreadContext context) {
         return op_truncate(context);
     }
 
     /** nurat_truncate
      * 
      */
     @JRubyMethod(name = "truncate")
     public IRubyObject op_truncate(ThreadContext context) {
         if (f_negative_p(context, num)) {
             return f_negate(context, f_idiv(context, f_negate(context, num), den));
         }
         return f_idiv(context, num, den);
     }
 
     @JRubyMethod(name = "truncate")
     public IRubyObject op_truncate(ThreadContext context, IRubyObject n) {
         IRubyObject b = op_roundCommonPre(context, n);
         return op_roundCommonPost(context, ((RubyRational)f_mul(context, this, b)).op_truncate(context), n, b);
     }
 
     /** nurat_round
      * 
      */
     @JRubyMethod(name = "round")
     public IRubyObject op_round(ThreadContext context) {
         IRubyObject myNum = this.num;
         boolean neg = f_negative_p(context, myNum);
         if (neg) myNum = f_negate(context, myNum);
 
         IRubyObject myDen = this.den;
         IRubyObject two = RubyFixnum.two(context.runtime);
         myNum = f_add(context, f_mul(context, myNum, two), myDen);
         myDen = f_mul(context, myDen, two);
         myNum = f_idiv(context, myNum, myDen);
 
         if (neg) myNum = f_negate(context, myNum);
         return myNum;
     }
 
     @JRubyMethod(name = "round")
     public IRubyObject op_round(ThreadContext context, IRubyObject n) {
         IRubyObject b = op_roundCommonPre(context, n);
         return op_roundCommonPost(context, ((RubyRational)f_mul(context, this, b)).op_round(context), n, b);
     }
 
     /** nurat_to_f
      * 
      */
     private static long ML = (long)(Math.log(Double.MAX_VALUE) / Math.log(2.0) - 1);
     @JRubyMethod(name = "to_f")
     public IRubyObject to_f(ThreadContext context) {
         return context.runtime.newFloat(getDoubleValue(context));
     }
     
     @Override
     public double getDoubleValue() {
         return getDoubleValue(getRuntime().getCurrentContext());
     }
     
     public double getDoubleValue(ThreadContext context) {
         Ruby runtime = context.runtime;
         if (f_zero_p(context, num)) return 0;
 
         IRubyObject myNum = this.num;
         IRubyObject myDen = this.den;
 
         boolean minus = false;
         if (f_negative_p(context, myNum)) {
             myNum = f_negate(context, myNum);
             minus = true;
         }
 
         long nl = i_ilog2(context, myNum);
         long dl = i_ilog2(context, myDen);
 
         long ne = 0;
         if (nl > ML) {
             ne = nl - ML;
             myNum = f_rshift(context, myNum, RubyFixnum.newFixnum(runtime, ne));
         }
 
         long de = 0;
         if (dl > ML) {
             de = dl - ML;
             myDen = f_rshift(context, myDen, RubyFixnum.newFixnum(runtime, de));
         }
 
         long e = ne - de;
 
         if (e > 1023 || e < -1022) {
             runtime.getWarnings().warn(IRubyWarnings.ID.FLOAT_OUT_OF_RANGE, "out of Float range");
             return e > 0 ? Double.MAX_VALUE : 0;
         }
 
         double f = RubyNumeric.num2dbl(myNum) / RubyNumeric.num2dbl(myDen);
 
         if (minus) f = -f;
 
         f = ldexp(f, e);
 
         if (Double.isInfinite(f) || Double.isNaN(f)) {
             runtime.getWarnings().warn(IRubyWarnings.ID.FLOAT_OUT_OF_RANGE, "out of Float range");
         }
 
         return f;
     }
 
     /** nurat_to_r
      * 
      */
     @JRubyMethod(name = "to_r")
     public IRubyObject to_r(ThreadContext context) {
         return this;
     }
 
     /** nurat_rationalize
      *
      */
     @JRubyMethod(name = "rationalize", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject rationalize(ThreadContext context, IRubyObject[] args) {
 
         IRubyObject a, b;
 
         if (args.length == 0) return to_r(context);
 
         if (f_negative_p(context, this)) {
             return f_negate(context,
                     ((RubyRational) f_abs(context, this)).rationalize(context, args));
         }
 
         IRubyObject eps = f_abs(context, args[0]);
         a = f_sub(context, this, eps);
         b = f_add(context, this, eps);
 
         if (f_equal(context, a, b).isTrue()) return this;
         IRubyObject[] ary = new IRubyObject[2];
         ary[0] = a;
         ary[1] = b;
         IRubyObject[] ans = nurat_rationalize_internal(context, ary);
 
         return newRational(context, this.metaClass, ans[0], ans[1]);
     }
 
     /** nurat_hash
      * 
      */
     @JRubyMethod(name = "hash")
     public IRubyObject hash(ThreadContext context) {
         return f_xor(context, invokedynamic(context, num, HASH), invokedynamic(context, den, HASH));
     }
 
     /** nurat_to_s
      * 
      */
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s(ThreadContext context) {
         RubyString str = RubyString.newEmptyString(context.getRuntime());
         str.append(f_to_s(context, num));
         str.cat((byte)'/');
         str.append(f_to_s(context, den));
         return str;
     }
 
     /** nurat_inspect
      * 
      */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect(ThreadContext context) {
         RubyString str = RubyString.newEmptyString(context.getRuntime());
         str.cat((byte)'(');
         str.append(f_inspect(context, num));
         str.cat((byte)'/');
         str.append(f_inspect(context, den));
         str.cat((byte)')');
         return str;
     }
 
     /** nurat_marshal_dump
      * 
      */
     @JRubyMethod(name = "marshal_dump")
     public IRubyObject marshal_dump(ThreadContext context) {
         RubyArray dump = context.runtime.newArray(num, den);
         if (hasVariables()) dump.syncVariables(this);
         return dump;
     }
 
     /** nurat_marshal_load
      * 
      */
     @JRubyMethod(name = "marshal_load")
     public IRubyObject marshal_load(ThreadContext context, IRubyObject arg) {
         RubyArray load = arg.convertToArray();
         num = load.size() > 0 ? load.eltInternal(0) : context.runtime.getNil();
         den = load.size() > 1 ? load.eltInternal(1) : context.runtime.getNil();
 
         if (f_zero_p(context, den)) {
             throw context.runtime.newZeroDivisionError();
         }
         if (load.hasVariables()) syncVariables((IRubyObject)load);
         return this;
     }
 
     static RubyArray str_to_r_internal(ThreadContext context, IRubyObject recv) {
         RubyString s = recv.convertToString();
         ByteList bytes = s.getByteList();
 
         Ruby runtime = context.runtime;
         if (bytes.getRealSize() == 0) return runtime.newArray(runtime.getNil(), recv);
 
         IRubyObject m = RubyRegexp.newDummyRegexp(runtime, Numeric.RationalPatterns.rat_pat).match_m19(context, s, false, Block.NULL_BLOCK);
         
         if (!m.isNil()) {
             RubyMatchData match = (RubyMatchData)m;
             IRubyObject si = match.op_aref19(RubyFixnum.one(runtime));
             RubyString nu = (RubyString)match.op_aref19(RubyFixnum.two(runtime));
             IRubyObject de = match.op_aref19(RubyFixnum.three(runtime));
             IRubyObject re = match.post_match(context);
             
             RubyArray a = nu.split19(context, RubyRegexp.newDummyRegexp(runtime, Numeric.RationalPatterns.an_e_pat), false).convertToArray();
             RubyString ifp = (RubyString)a.eltInternal(0);
             IRubyObject exp = a.size() != 2 ? runtime.getNil() : a.eltInternal(1);
             
             a = ifp.split19(context, RubyRegexp.newDummyRegexp(runtime, Numeric.RationalPatterns.a_dot_pat), false).convertToArray();
             IRubyObject ip = a.eltInternal(0);
             IRubyObject fp = a.size() != 2 ? runtime.getNil() : a.eltInternal(1);
             
             IRubyObject v = RubyRational.newRationalCanonicalize(context, f_to_i(context, ip));
             
             if (!fp.isNil()) {
                 bytes = fp.convertToString().getByteList();
                 int count = 0;
                 byte[]buf = bytes.getUnsafeBytes();
                 int i = bytes.getBegin();
                 int end = i + bytes.getRealSize();
 
                 while (i < end) {
                     if (ASCIIEncoding.INSTANCE.isDigit(buf[i])) count++;
                     i++;
                 }
 
                 IRubyObject l = f_expt(context, RubyFixnum.newFixnum(runtime, 10), RubyFixnum.newFixnum(runtime, count));
                 v = f_mul(context, v, l);
                 v = f_add(context, v, f_to_i(context, fp));
                 v = f_div(context, v, l);
             }
 
             if (!si.isNil()) {
                 ByteList siBytes = si.convertToString().getByteList();
                 if (siBytes.length() > 0 && siBytes.get(0) == '-') v = f_negate(context, v); 
             }
 
             if (!exp.isNil()) {
                 v = f_mul(context, v, f_expt(context, RubyFixnum.newFixnum(runtime, 10), f_to_i(context, exp)));
             }
 
             if (!de.isNil()) {
                 v = f_div(context, v, f_to_i(context, de));
             }
             return runtime.newArray(v, re);
         }
         return runtime.newArray(runtime.getNil(), recv);
     }
     
     private static IRubyObject str_to_r_strict(ThreadContext context, IRubyObject recv) {
         RubyArray a = str_to_r_internal(context, recv);
         if (a.eltInternal(0).isNil() || a.eltInternal(1).convertToString().getByteList().length() > 0) {
             IRubyObject s = recv.callMethod(context, "inspect");
             throw context.runtime.newArgumentError("invalid value for convert(): " + s.convertToString());
         }
         return a.eltInternal(0);
     }    
 }
diff --git a/src/org/jruby/RubyRegexp.java b/src/org/jruby/RubyRegexp.java
index bab7521a8d..126eee4237 100644
--- a/src/org/jruby/RubyRegexp.java
+++ b/src/org/jruby/RubyRegexp.java
@@ -1,1229 +1,1224 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Nick Sieger <nicksieger@gmail.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import static org.jruby.anno.FrameField.BACKREF;
 import static org.jruby.anno.FrameField.LASTLINE;
 
 import java.lang.ref.SoftReference;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 
 import org.jcodings.Encoding;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jcodings.specific.UTF8Encoding;
 import org.joni.Matcher;
 import org.joni.NameEntry;
 import org.joni.Option;
 import org.joni.Regex;
 import org.joni.Region;
 import org.joni.Syntax;
 import org.joni.exception.JOniException;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.parser.ReOptions;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.runtime.encoding.MarshalEncoding;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.Pack;
 import org.jruby.util.RegexpOptions;
 import org.jruby.util.Sprintf;
 import org.jruby.util.StringSupport;
 import org.jruby.util.TypeConverter;
 
 @JRubyClass(name="Regexp")
 public class RubyRegexp extends RubyObject implements ReOptions, EncodingCapable, MarshalEncoding {
     private Regex pattern;
     private ByteList str = ByteList.EMPTY_BYTELIST;
     private RegexpOptions options;
 
     public static final int ARG_ENCODING_FIXED     =   16;
     public static final int ARG_ENCODING_NONE      =   32;
 
     public void setLiteral() {
         options.setLiteral(true);
     }
 
     public void clearLiteral() {
         options.setLiteral(false);
     }
 
     public boolean isLiteral() {
         return options.isLiteral();
     }
 
     public boolean isKCodeDefault() {
         return options.isKcodeDefault();
     }
 
     public void setEncodingNone() {
         options.setEncodingNone(true);
     }
     
     public void clearEncodingNone() {
         options.setEncodingNone(false);
     }
 
     public boolean isEncodingNone() {
         return options.isEncodingNone();
     }
 
     public KCode getKCode() {
         return options.getKCode();
     }
 
     @Override
     public Encoding getEncoding() {
         return pattern.getEncoding();
     }
 
     @Override
     public void setEncoding(Encoding encoding) {
         // FIXME: Which encoding should be changed here?  
         // FIXME: transcode?
     }
 
     @Override
     public boolean shouldMarshalEncoding() {
         return getEncoding() != ASCIIEncoding.INSTANCE;
     }
 
     @Override
     public Encoding getMarshalEncoding() {
         return getEncoding();
     }
 
     private static final class RegexpCache {
         private volatile SoftReference<Map<ByteList, Regex>> cache = new SoftReference<Map<ByteList, Regex>>(null);
         private Map<ByteList, Regex> get() {
             Map<ByteList, Regex> patternCache = cache.get();
             if (patternCache == null) {
                 patternCache = new ConcurrentHashMap<ByteList, Regex>(5);
                 cache = new SoftReference<Map<ByteList, Regex>>(patternCache);
             }
             return patternCache;
         }
     }
 
     private static final RegexpCache patternCache = new RegexpCache();
     private static final RegexpCache quotedPatternCache = new RegexpCache();
     private static final RegexpCache preprocessedPatternCache = new RegexpCache();
 
     private static Regex makeRegexp(Ruby runtime, ByteList bytes, RegexpOptions options, Encoding enc) {
         try {
             int p = bytes.getBegin();
             return new Regex(bytes.getUnsafeBytes(), p, p + bytes.getRealSize(), options.toJoniOptions(), enc, Syntax.DEFAULT, runtime.getWarnings());
         } catch (Exception e) {
             if (runtime.is1_9()) {
                 raiseRegexpError19(runtime, bytes, enc, options, e.getMessage());
             } else {
                 raiseRegexpError(runtime, bytes, enc, options, e.getMessage());
             }
             return null; // not reached
         }
     }
 
     static Regex getRegexpFromCache(Ruby runtime, ByteList bytes, Encoding enc, RegexpOptions options) {
         Map<ByteList, Regex> cache = patternCache.get();
         Regex regex = cache.get(bytes);
         if (regex != null && regex.getEncoding() == enc && regex.getOptions() == options.toJoniOptions()) return regex;
         regex = makeRegexp(runtime, bytes, options, enc);
         cache.put(bytes, regex);
         return regex;
     }
 
     static Regex getQuotedRegexpFromCache(Ruby runtime, ByteList bytes, Encoding enc, RegexpOptions options) {
         Map<ByteList, Regex> cache = quotedPatternCache.get();
         Regex regex = cache.get(bytes);
         if (regex != null && regex.getEncoding() == enc && regex.getOptions() == options.toJoniOptions()) return regex;
         regex = makeRegexp(runtime, quote(bytes, enc), options, enc);
         cache.put(bytes, regex);
         return regex;
     }
 
     static Regex getQuotedRegexpFromCache19(Ruby runtime, ByteList bytes, RegexpOptions options, boolean asciiOnly) {
         Map<ByteList, Regex> cache = quotedPatternCache.get();
         Regex regex = cache.get(bytes);
         Encoding enc = asciiOnly ? USASCIIEncoding.INSTANCE : bytes.getEncoding();
         if (regex != null && regex.getEncoding() == enc && regex.getOptions() == options.toJoniOptions()) return regex;
         ByteList quoted = quote19(bytes, asciiOnly);
         regex = makeRegexp(runtime, quoted, options, quoted.getEncoding());
         regex.setUserObject(quoted);
         cache.put(bytes, regex);
         return regex;
     }
 
     private static Regex getPreprocessedRegexpFromCache(Ruby runtime, ByteList bytes, Encoding enc, RegexpOptions options, ErrorMode mode) {
         Map<ByteList, Regex> cache = preprocessedPatternCache.get();
         Regex regex = cache.get(bytes);
         if (regex != null && regex.getEncoding() == enc && regex.getOptions() == options.toJoniOptions()) return regex;
         ByteList preprocessed = preprocess(runtime, bytes, enc, new Encoding[]{null}, ErrorMode.RAISE);
         regex = makeRegexp(runtime, preprocessed, options, enc);
         regex.setUserObject(preprocessed);
         cache.put(bytes, regex);
         return regex;
     }
 
     public static RubyClass createRegexpClass(Ruby runtime) {
         RubyClass regexpClass = runtime.defineClass("Regexp", runtime.getObject(), REGEXP_ALLOCATOR);
         runtime.setRegexp(regexpClass);
 
         regexpClass.index = ClassIndex.REGEXP;
         regexpClass.setReifiedClass(RubyRegexp.class);
         
-        regexpClass.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyRegexp;
-            }
-        };
+        regexpClass.kindOf = new RubyModule.JavaClassKindOf(RubyRegexp.class);
 
         regexpClass.defineConstant("IGNORECASE", runtime.newFixnum(RE_OPTION_IGNORECASE));
         regexpClass.defineConstant("EXTENDED", runtime.newFixnum(RE_OPTION_EXTENDED));
         regexpClass.defineConstant("MULTILINE", runtime.newFixnum(RE_OPTION_MULTILINE));
 
         if (runtime.is1_9()) {
             regexpClass.defineConstant("FIXEDENCODING", runtime.newFixnum(ARG_ENCODING_FIXED));
             regexpClass.defineConstant("NOENCODING", runtime.newFixnum(ARG_ENCODING_NONE));
         }
 
         regexpClass.defineAnnotatedMethods(RubyRegexp.class);
 
         return regexpClass;
     }
 
     private static ObjectAllocator REGEXP_ALLOCATOR = new ObjectAllocator() {
         @Override
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyRegexp(runtime, klass);
         }
     };
     
     public static int matcherSearch(Ruby runtime, Matcher matcher, int start, int range, int option) {
         try {
             RubyThread thread = runtime.getCurrentContext().getThread();
             SearchMatchTask task = new SearchMatchTask(thread, matcher, start, range, option, false);
             thread.executeBlockingTask(task);
             return task.retval;
         } catch (InterruptedException e) {
             throw runtime.newInterruptedRegexpError("Regexp Interrrupted");
         }
     }
     
     public static int matcherMatch(Ruby runtime, Matcher matcher, int start, int range, int option) {
         try {
             RubyThread thread = runtime.getCurrentContext().getThread();
             SearchMatchTask task = new SearchMatchTask(thread, matcher, start, range, option, true);
             thread.executeBlockingTask(task);
             return task.retval;
         } catch (InterruptedException e) {
             throw runtime.newInterruptedRegexpError("Regexp Interrrupted");
         }
     }
     
     private static class SearchMatchTask implements RubyThread.BlockingTask {
         int retval;
         final RubyThread thread;
         final Matcher matcher;
         final int start;
         final int range;
         final int option;
         final boolean match;
         
         SearchMatchTask(RubyThread thread, Matcher matcher, int start, int range, int option, boolean match) {
             this.thread = thread;
             this.matcher = matcher;
             this.start = start;
             this.range = range;
             this.option = option;
             this.match = match;
         }
         
         @Override
         public void run() throws InterruptedException {
             retval = match ?
                     matcher.matchInterruptible(start, range, option) :
                     matcher.searchInterruptible(start, range, option);
         }
 
         @Override
         public void wakeup() {
             thread.getNativeThread().interrupt();
         }
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.REGEXP;
     }
 
     /** used by allocator
      */
     private RubyRegexp(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
         this.options = new RegexpOptions();
     }
 
     /** default constructor
      */
     private RubyRegexp(Ruby runtime) {
         super(runtime, runtime.getRegexp());
         this.options = new RegexpOptions();
     }
 
     private RubyRegexp(Ruby runtime, ByteList str) {
         this(runtime);
         this.str = str;
         this.pattern = getRegexpFromCache(runtime, str, getEncoding(runtime, str), RegexpOptions.NULL_OPTIONS);
     }
 
     private RubyRegexp(Ruby runtime, ByteList str, RegexpOptions options) {
         this(runtime);
 
         if (runtime.is1_9()) {
             initializeCommon19(str, str.getEncoding(), options);
         } else {
             this.options = options;            
             this.str = str;
             this.pattern = getRegexpFromCache(runtime, str, getEncoding(runtime, str), options);
         }
     }
 
     private Encoding getEncoding(Ruby runtime, ByteList str) {
         if (runtime.is1_9()) return str.getEncoding();
 
         // Whatever $KCODE is we should use
         if (options.isKcodeDefault()) return runtime.getKCode().getEncoding();
         
         return options.getKCode().getEncoding();
     }
 
     // used only by the compiler/interpreter (will set the literal flag)
     public static RubyRegexp newRegexp(Ruby runtime, String pattern, RegexpOptions options) {
         return newRegexp(runtime, ByteList.create(pattern), options);
     }
 
     // used only by the compiler/interpreter (will set the literal flag)
     public static RubyRegexp newRegexp(Ruby runtime, ByteList pattern, RegexpOptions options) {
         try {
             return new RubyRegexp(runtime, pattern, options);
         } catch (RaiseException re) {
             throw runtime.newSyntaxError(re.getMessage());
         }
     }
 
     // used only by the compiler/interpreter (will set the literal flag)
     public static RubyRegexp newDRegexp(Ruby runtime, RubyString pattern, RegexpOptions options) {
         try {
             return new RubyRegexp(runtime, pattern.getByteList(), options);
         } catch (RaiseException re) {
             throw runtime.newRegexpError(re.getMessage());
         }
     }
 
     // used only by the compiler/interpreter (will set the literal flag)
     public static RubyRegexp newDRegexp(Ruby runtime, RubyString pattern, int joniOptions) {
         try {
             RegexpOptions options = RegexpOptions.fromJoniOptions(joniOptions);
             return new RubyRegexp(runtime, pattern.getByteList(), options);
         } catch (RaiseException re) {
             throw runtime.newRegexpError(re.getMessage());
         }
     }
 
     // used only by the compiler/interpreter (will set the literal flag)
     public static RubyRegexp newDRegexpEmbedded(Ruby runtime, RubyString pattern, int embeddedOptions) {
         try {
             RegexpOptions options = RegexpOptions.fromEmbeddedOptions(embeddedOptions);
             // FIXME: Massive hack (fix in DRegexpNode too for interpreter)
             if (pattern.getEncoding() == USASCIIEncoding.INSTANCE) {
                 pattern.setEncoding(ASCIIEncoding.INSTANCE);
             }
             return new RubyRegexp(runtime, pattern.getByteList(), options);
         } catch (RaiseException re) {
             throw runtime.newRegexpError(re.getMessage());
         }
     }
     
     public static RubyRegexp newDRegexpEmbedded19(Ruby runtime, IRubyObject[] strings, int embeddedOptions) {
         try {
             RegexpOptions options = RegexpOptions.fromEmbeddedOptions(embeddedOptions);
             RubyString pattern = preprocessDRegexp(runtime, strings, options);
             
             return new RubyRegexp(runtime, pattern.getByteList(), options);
         } catch (RaiseException re) {
             throw runtime.newRegexpError(re.getMessage());
         }
         
     }
     
     public static RubyRegexp newRegexp(Ruby runtime, ByteList pattern) {
         return new RubyRegexp(runtime, pattern);
     }
 
     static RubyRegexp newRegexp(Ruby runtime, ByteList str, Regex pattern) {
         RubyRegexp regexp = new RubyRegexp(runtime);
         regexp.str = str;
         regexp.options = RegexpOptions.fromJoniOptions(pattern.getOptions());
         regexp.pattern = pattern;
         return regexp;
     }
     
     // internal usage (Complex/Rational)
     static RubyRegexp newDummyRegexp(Ruby runtime, Regex regex) {
         RubyRegexp regexp = new RubyRegexp(runtime);
         regexp.pattern = regex;
         regexp.str = ByteList.EMPTY_BYTELIST;
         regexp.options.setFixed(true);
         return regexp;
     }
 
     /** rb_reg_options
      */
     public RegexpOptions getOptions() {
         check();
         return options;
     }
 
     public final Regex getPattern() {
         check();
         return pattern;
     }
 
     private static void encodingMatchError(Ruby runtime, Regex pattern, Encoding strEnc) {
         throw runtime.newEncodingCompatibilityError("incompatible encoding regexp match (" +
                 pattern.getEncoding() + " regexp with " + strEnc + " string)");
     }
 
     private Encoding checkEncoding(RubyString str, boolean warn) {
         if (str.scanForCodeRange() == StringSupport.CR_BROKEN) {
             throw getRuntime().newArgumentError("invalid byte sequence in " + str.getEncoding());
         }
         check();
         Encoding enc = str.getEncoding();
         if (!enc.isAsciiCompatible()) {
             if (enc != pattern.getEncoding()) encodingMatchError(getRuntime(), pattern, enc);
         } else if (options.isFixed()) {
             if (enc != pattern.getEncoding() && 
                (!pattern.getEncoding().isAsciiCompatible() ||
                str.scanForCodeRange() != StringSupport.CR_7BIT)) encodingMatchError(getRuntime(), pattern, enc);
             enc = pattern.getEncoding();
         }
         if (warn && isEncodingNone() && enc != ASCIIEncoding.INSTANCE && str.scanForCodeRange() != StringSupport.CR_7BIT) {
             getRuntime().getWarnings().warn(ID.REGEXP_MATCH_AGAINST_STRING, "regexp match /.../n against to " + enc + " string");
         }
         return enc;
     }
 
     public final Regex preparePattern(RubyString str) {
         check();
         Encoding enc = checkEncoding(str, true);
         if (enc == pattern.getEncoding()) return pattern;
         return getPreprocessedRegexpFromCache(getRuntime(), this.str, enc, options, ErrorMode.PREPROCESS);
     }
 
     static Regex preparePattern(Ruby runtime, Regex pattern, RubyString str) {
         if (str.scanForCodeRange() == StringSupport.CR_BROKEN) {
             throw runtime.newArgumentError("invalid byte sequence in " + str.getEncoding());
         }
         Encoding enc = str.getEncoding();
         if (!enc.isAsciiCompatible()) {
             if (enc != pattern.getEncoding()) encodingMatchError(runtime, pattern, enc);
         }
         // TODO: check for isKCodeDefault() somehow
 //        if (warn && isEncodingNone() && enc != ASCIIEncoding.INSTANCE && str.scanForCodeRange() != StringSupport.CR_7BIT) {
 //            getRuntime().getWarnings().warn(ID.REGEXP_MATCH_AGAINST_STRING, "regexp match /.../n against to " + enc + " string");
 //        }
         if (enc == pattern.getEncoding()) return pattern;
         return getPreprocessedRegexpFromCache(runtime, (ByteList)pattern.getUserObject(), enc, RegexpOptions.fromJoniOptions(pattern.getOptions()), ErrorMode.PREPROCESS);
     }
 
     private static enum ErrorMode {RAISE, PREPROCESS, DESC} 
 
     private static int raisePreprocessError(Ruby runtime, ByteList str, String err, ErrorMode mode) {
         switch (mode) {
         case RAISE:
             raiseRegexpError19(runtime, str, str.getEncoding(), RegexpOptions.NULL_OPTIONS, err);
         case PREPROCESS:
             throw runtime.newArgumentError("regexp preprocess failed: " + err);
         case DESC:
             // silent ?
         }
         return 0;
     }
 
     private static int readEscapedByte(Ruby runtime, byte[]to, int toP, byte[]bytes, int p, int end, ByteList str, ErrorMode mode) {
         if (p == end || bytes[p++] != (byte)'\\') raisePreprocessError(runtime, str, "too short escaped multibyte character", mode);
 
         boolean metaPrefix = false, ctrlPrefix = false;
         int code = 0;
         while (true) {
             if (p == end) raisePreprocessError(runtime, str, "too short escape sequence", mode);
 
             switch (bytes[p++]) {
             case '\\': code = '\\'; break;
             case 'n': code = '\n'; break;
             case 't': code = '\t'; break;
             case 'r': code = '\r'; break;
             case 'f': code = '\f'; break;
             case 'v': code = '\013'; break;
             case 'a': code = '\007'; break;
             case 'e': code = '\033'; break;
 
             /* \OOO */
             case '0': case '1': case '2': case '3':
             case '4': case '5': case '6': case '7':
                 p--;
                 int olen = end < p + 3 ? end - p : 3;
                 code = StringSupport.scanOct(bytes, p, olen);
                 p += StringSupport.octLength(bytes, p, olen);
                 break;
 
             case 'x': /* \xHH */
                 int hlen = end < p + 2 ? end - p : 2;
                 code = StringSupport.scanHex(bytes, p, hlen);
                 int len = StringSupport.hexLength(bytes, p, hlen);
                 if (len < 1) raisePreprocessError(runtime, str, "invalid hex escape", mode);
                 p += len;
                 break;
 
             case 'M': /* \M-X, \M-\C-X, \M-\cX */
                 if (metaPrefix) raisePreprocessError(runtime, str, "duplicate meta escape", mode);
                 metaPrefix = true;
                 if (p + 1 < end && bytes[p++] == (byte)'-' && (bytes[p] & 0x80) == 0) {
                     if (bytes[p] == (byte)'\\') {
                         p++;
                         continue;
                     } else {
                         code = bytes[p++] & 0xff;
                         break;
                     }
                 }
                 raisePreprocessError(runtime, str, "too short meta escape", mode);
 
             case 'C': /* \C-X, \C-\M-X */
                 if (p == end || bytes[p++] != (byte)'-') raisePreprocessError(runtime, str, "too short control escape", mode);
 
             case 'c': /* \cX, \c\M-X */
                 if (ctrlPrefix) raisePreprocessError(runtime, str, "duplicate control escape", mode);
                 ctrlPrefix = true;
                 if (p < end && (bytes[p] & 0x80) == 0) {
                     if (bytes[p] == (byte)'\\') {
                         p++;
                         continue;
                     } else {
                         code = bytes[p++] & 0xff;
                         break;
                     }
                 }
                 raisePreprocessError(runtime, str, "too short control escape", mode);
             default:
                 raisePreprocessError(runtime, str, "unexpected escape sequence", mode);
             } // switch
 
             if (code < 0 || code > 0xff) raisePreprocessError(runtime, str, "invalid escape code", mode);
 
             if (ctrlPrefix) code &= 0x1f;
             if (metaPrefix) code |= 0x80;
 
             to[toP] = (byte)code;
             return p;
         } // while
     }
 
     // MRI: unescape_escapted_nonascii
     private static int unescapeEscapedNonAscii(Ruby runtime, ByteList to, byte[]bytes, int p, int end, Encoding enc, Encoding[]encp, ByteList str, ErrorMode mode) {
         byte[]chBuf = new byte[enc.maxLength()];
         int chLen = 0;
 
         p = readEscapedByte(runtime, chBuf, chLen++, bytes, p, end, str, mode);
         while (chLen < enc.maxLength() && StringSupport.preciseLength(enc, chBuf, 0, chLen) < -1) { // MBCLEN_NEEDMORE_P
             p = readEscapedByte(runtime, chBuf, chLen++, bytes, p, end, str, mode);
         }
 
         int cl = StringSupport.preciseLength(enc, chBuf, 0, chLen);
         if (cl == -1) {
             raisePreprocessError(runtime, str, "invalid multibyte escape", mode); // MBCLEN_INVALID_P
         }
 
         if (chLen > 1 || (chBuf[0] & 0x80) != 0) {
             to.append(chBuf, 0, chLen);
 
             if (encp[0] == null) {
                 encp[0] = enc;
             } else if (encp[0] != enc) {
                 raisePreprocessError(runtime, str, "escaped non ASCII character in UTF-8 regexp", mode);
             }
         } else {
             Sprintf.sprintf(runtime, to, "\\x%02X", chBuf[0] & 0xff);
         }
         return p;
     }
 
     private static void checkUnicodeRange(Ruby runtime, int code, ByteList str, ErrorMode mode) {
         // Unicode is can be only 21 bits long, int is enough
         if ((0xd800 <= code && code <= 0xdfff) /* Surrogates */ || 0x10ffff < code) {
             raisePreprocessError(runtime, str, "invalid Unicode range", mode);
         }
     }
 
     private static void appendUtf8(Ruby runtime, ByteList to, int code, Encoding[]enc, ByteList str, ErrorMode mode) {
         checkUnicodeRange(runtime, code, str, mode);
 
         if (code < 0x80) {
             Sprintf.sprintf(runtime, to, "\\x%02X", code);
         } else {
             to.ensure(to.getRealSize() + 6);
             to.setRealSize(to.getRealSize() + Pack.utf8Decode(runtime, to.getUnsafeBytes(), to.getBegin() + to.getRealSize(), code));
             if (enc[0] == null) {
                 enc[0] = UTF8Encoding.INSTANCE;
             } else if (!(enc[0] instanceof UTF8Encoding)) { // do not load the class if not used
                 raisePreprocessError(runtime, str, "UTF-8 character in non UTF-8 regexp", mode);
             }
         }
     }
     
     private static int unescapeUnicodeList(Ruby runtime, ByteList to, byte[]bytes, int p, int end, Encoding[]encp, ByteList str, ErrorMode mode) {
         while (p < end && ASCIIEncoding.INSTANCE.isSpace(bytes[p] & 0xff)) p++;
 
         boolean hasUnicode = false; 
         while (true) {
             int code = StringSupport.scanHex(bytes, p, end - p);
             int len = StringSupport.hexLength(bytes, p, end - p);
             if (len == 0) break;
             if (len > 6) raisePreprocessError(runtime, str, "invalid Unicode range", mode);
             p += len;
             appendUtf8(runtime, to, code, encp, str, mode);
             hasUnicode = true;
             while (p < end && ASCIIEncoding.INSTANCE.isSpace(bytes[p] & 0xff)) p++;
         }
 
         if (!hasUnicode) raisePreprocessError(runtime, str, "invalid Unicode list", mode); 
         return p;
     }
 
     private static int unescapeUnicodeBmp(Ruby runtime, ByteList to, byte[]bytes, int p, int end, Encoding[]encp, ByteList str, ErrorMode mode) {
         if (p + 4 > end) raisePreprocessError(runtime, str, "invalid Unicode escape", mode);
         int code = StringSupport.scanHex(bytes, p, 4);
         int len = StringSupport.hexLength(bytes, p, 4);
         if (len != 4) raisePreprocessError(runtime, str, "invalid Unicode escape", mode);
         appendUtf8(runtime, to, code, encp, str, mode);
         return p + 4;
     }
 
     private static boolean unescapeNonAscii(Ruby runtime, ByteList to, byte[]bytes, int p, int end, Encoding enc, Encoding[]encp, ByteList str, ErrorMode mode) {
         boolean hasProperty = false;
 
         while (p < end) {
             int cl = StringSupport.preciseLength(enc, bytes, p, end);
             if (cl <= 0) raisePreprocessError(runtime, str, "invalid multibyte character", mode);
             if (cl > 1 || (bytes[p] & 0x80) != 0) {
                 to.append(bytes, p, cl);
                 p += cl;
                 if (encp[0] == null) {
                     encp[0] = enc;
                 } else if (encp[0] != enc) {
                     raisePreprocessError(runtime, str, "non ASCII character in UTF-8 regexp", mode);
                 }
                 continue;
             }
             int c;
             switch (c = bytes[p++] & 0xff) {
             case '\\':
                 if (p == end) raisePreprocessError(runtime, str, "too short escape sequence", mode);
 
                 switch (c = bytes[p++] & 0xff) {
                 case '1': case '2': case '3':
                 case '4': case '5': case '6': case '7': /* \O, \OO, \OOO or backref */
                     if (StringSupport.scanOct(bytes, p - 1, end - (p - 1)) <= 0177) {
                         to.append('\\').append(c);
                         break;
                     }
 
                 case '0': /* \0, \0O, \0OO */
                 case 'x': /* \xHH */
                 case 'c': /* \cX, \c\M-X */
                 case 'C': /* \C-X, \C-\M-X */
                 case 'M': /* \M-X, \M-\C-X, \M-\cX */
                     p = unescapeEscapedNonAscii(runtime, to, bytes, p - 2, end, enc, encp, str, mode);
                     break;
 
                 case 'u':
                     if (p == end) raisePreprocessError(runtime, str, "too short escape sequence", mode);
                     if (bytes[p] == (byte)'{') { /* \\u{H HH HHH HHHH HHHHH HHHHHH ...} */
                         p++;
                         p = unescapeUnicodeList(runtime, to, bytes, p, end, encp, str, mode);
                         if (p == end || bytes[p++] != (byte)'}') raisePreprocessError(runtime, str, "invalid Unicode list", mode);
                     } else { /* \\uHHHH */
                         p = unescapeUnicodeBmp(runtime, to, bytes, p, end, encp, str, mode);
                     }
                     break;
                 case 'p': /* \p{Hiragana} */
                     if (encp[0] == null) hasProperty = true;
                     to.append('\\').append(c);
                     break;
 
                 default:
                     to.append('\\').append(c);
                     break;
                 } // inner switch
                 break;
 
             default:
                 to.append(c);
             } // switch
         } // while
         return hasProperty;
     }
 
     private static ByteList preprocess(Ruby runtime, ByteList str, Encoding enc, Encoding[]fixedEnc, ErrorMode mode) {
         ByteList to = new ByteList(str.getRealSize());
 
         if (enc.isAsciiCompatible()) {
             fixedEnc[0] = null;
         } else {
             fixedEnc[0] = enc;
             to.setEncoding(enc);
         }
 
         boolean hasProperty = unescapeNonAscii(runtime, to, str.getUnsafeBytes(), str.getBegin(), str.getBegin() + str.getRealSize(), enc, fixedEnc, str, mode);
         if (hasProperty && fixedEnc[0] == null) fixedEnc[0] = enc;
         if (fixedEnc[0] != null) to.setEncoding(fixedEnc[0]);
         return to;
     }
 
     public static void preprocessCheck(Ruby runtime, ByteList bytes) {
         preprocess(runtime, bytes, bytes.getEncoding(), new Encoding[]{null}, ErrorMode.RAISE);
     }
     
     // rb_reg_preprocess_dregexp
     public static RubyString preprocessDRegexp(Ruby runtime, IRubyObject[] strings, RegexpOptions options) {
         RubyString string = null;
         Encoding regexpEnc = null;
         
         for (int i = 0; i < strings.length; i++) {
             RubyString str = strings[i].convertToString();
             Encoding strEnc = str.getEncoding();
             
             if (options.isEncodingNone() && strEnc != ASCIIEncoding.INSTANCE) {
                 if (str.scanForCodeRange() != StringSupport.CR_7BIT) {
                     throw runtime.newRegexpError("/.../n has a non escaped non ASCII character in non ASCII-8BIT script");
                 }
                 strEnc = ASCIIEncoding.INSTANCE;
             }
             
             Encoding[] fixedEnc = new Encoding[1];
             ByteList buf = RubyRegexp.preprocess(runtime, str.getByteList(), strEnc, fixedEnc, RubyRegexp.ErrorMode.PREPROCESS);
             
             if (fixedEnc[0] != null) {
                 if (regexpEnc != null && regexpEnc != fixedEnc[0]) {
                     throw runtime.newRegexpError("encoding mismatch in dynamic regexp: " + new String(regexpEnc.getName()) + " and " + new String(fixedEnc[0].getName()));
                 }
                 regexpEnc = fixedEnc[0];
             }
             
             if (string == null) {
                 string = (RubyString)str.dup();
             } else {
                 string.append19(str);
             }
         }
         
         if (regexpEnc != null) {
             string.setEncoding(regexpEnc);
         }
 
         return string;
     }
 
     private void check() {
         if (pattern == null) throw getRuntime().newTypeError("uninitialized Regexp");
     }
 
     @JRubyMethod(name = {"new", "compile"}, rest = true, meta = true)
     public static RubyRegexp newInstance(IRubyObject recv, IRubyObject[] args) {
         RubyClass klass = (RubyClass)recv;
         RubyRegexp re = (RubyRegexp) klass.allocate();
 
         re.callInit(args, Block.NULL_BLOCK);
         return re;
     }
 
     @JRubyMethod(name = "try_convert", meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject try_convert(ThreadContext context, IRubyObject recv, IRubyObject args) {
         return TypeConverter.convertToTypeWithCheck(args, context.runtime.getRegexp(), "to_regexp");
     }
 
     /** rb_reg_s_quote
      * 
      */
     @JRubyMethod(name = {"quote", "escape"}, required = 1, optional = 1, meta = true, compat = CompatVersion.RUBY1_8)
     public static RubyString quote(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         final KCode code;
         if (args.length == 1 || args[1].isNil()) {
             code = runtime.getKCode();
         } else {
             code = KCode.create(runtime, args[1].toString());
         }
 
         RubyString src = args[0].convertToString();
         RubyString dst = RubyString.newStringShared(runtime, quote(src.getByteList(), code.getEncoding()));
         dst.infectBy(src);
         return dst;
     }
 
     @JRubyMethod(name = {"quote", "escape"}, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject quote19(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.runtime;
         RubyString str = operandCheck(runtime, arg);
         return RubyString.newStringShared(runtime, quote19(str.getByteList(), str.isAsciiOnly()));
     }
 
     /** rb_reg_quote
      *
      */
     private static ByteList quote(ByteList bs, Encoding enc) {
         int p = bs.getBegin();
         int end = p + bs.getRealSize();
         byte[]bytes = bs.getUnsafeBytes();
 
         metaFound: do {
             for(; p < end; p++) {
                 int c = bytes[p] & 0xff;
                 int cl = enc.length(bytes, p, end);
                 if (cl != 1) {
                     while (cl-- > 0 && p < end) p++;
                     p--;
                     continue;
                 }
                 switch (c) {
                 case '[': case ']': case '{': case '}':
                 case '(': case ')': case '|': case '-':
                 case '*': case '.': case '\\':
                 case '?': case '+': case '^': case '$':
                 case ' ': case '#':
                 case '\t': case '\f': case '\n': case '\r':
                     break metaFound;
                 }
             }
             return bs;
         } while (false);
 
         ByteList result = new ByteList(end * 2);
         byte[]obytes = result.getUnsafeBytes();
         int op = p - bs.getBegin();
         System.arraycopy(bytes, bs.getBegin(), obytes, 0, op);
 
         for(; p < end; p++) {
             int c = bytes[p] & 0xff;
             int cl = enc.length(bytes, p, end);
             if (cl != 1) {
                 while (cl-- > 0 && p < end) obytes[op++] = bytes[p++];
                 p--;
                 continue;
             }
 
             switch (c) {
             case '[': case ']': case '{': case '}':
             case '(': case ')': case '|': case '-':
             case '*': case '.': case '\\':
             case '?': case '+': case '^': case '$':
             case '#': obytes[op++] = '\\'; break;
             case ' ': obytes[op++] = '\\'; obytes[op++] = ' '; continue;
             case '\t':obytes[op++] = '\\'; obytes[op++] = 't'; continue;
             case '\n':obytes[op++] = '\\'; obytes[op++] = 'n'; continue;
             case '\r':obytes[op++] = '\\'; obytes[op++] = 'r'; continue;
             case '\f':obytes[op++] = '\\'; obytes[op++] = 'f'; continue;
             }
             obytes[op++] = (byte)c;
         }
 
         result.setRealSize(op);
         return result;
     }
 
     private static final int QUOTED_V = 11;
     static ByteList quote19(ByteList bs, boolean asciiOnly) {
         int p = bs.getBegin();
         int end = p + bs.getRealSize();
         byte[]bytes = bs.getUnsafeBytes();
         Encoding enc = bs.getEncoding();
 
         metaFound: do {
             while (p < end) {
                 final int c;
                 final int cl;
                 if (enc.isAsciiCompatible()) {
                     cl = 1;
                     c = bytes[p] & 0xff;
                 } else {
                     cl = StringSupport.preciseLength(enc, bytes, p, end);
                     c = enc.mbcToCode(bytes, p, end);
                 }
 
                 if (!Encoding.isAscii(c)) {
                     p += StringSupport.length(enc, bytes, p, end);
                     continue;
                 }
                 
                 switch (c) {
                 case '[': case ']': case '{': case '}':
                 case '(': case ')': case '|': case '-':
                 case '*': case '.': case '\\':
                 case '?': case '+': case '^': case '$':
                 case ' ': case '#':
                 case '\t': case '\f': case QUOTED_V: case '\n': case '\r':
                     break metaFound;
                 }
                 p += cl;
             }
             if (asciiOnly) {
                 ByteList tmp = bs.shallowDup();
                 tmp.setEncoding(USASCIIEncoding.INSTANCE);
                 return tmp;
             }
             return bs;
         } while (false);
 
         ByteList result = new ByteList(end * 2);
         result.setEncoding(asciiOnly ? USASCIIEncoding.INSTANCE : bs.getEncoding());
         byte[]obytes = result.getUnsafeBytes();
         int op = p - bs.getBegin();
         System.arraycopy(bytes, bs.getBegin(), obytes, 0, op);
 
         while (p < end) {
             final int c;
             final int cl;
             if (enc.isAsciiCompatible()) {
                 cl = 1;
                 c = bytes[p] & 0xff;
             } else {
                 cl = StringSupport.preciseLength(enc, bytes, p, end);
                 c = enc.mbcToCode(bytes, p, end);
             }
 
             if (!Encoding.isAscii(c)) {
                 int n = StringSupport.length(enc, bytes, p, end);
                 while (n-- > 0) obytes[op++] = bytes[p++];
                 continue;
             }
             p += cl;
             switch (c) {
             case '[': case ']': case '{': case '}':
             case '(': case ')': case '|': case '-':
             case '*': case '.': case '\\':
             case '?': case '+': case '^': case '$':
             case '#': 
                 op += enc.codeToMbc('\\', obytes, op);
                 break;
             case ' ':
                 op += enc.codeToMbc('\\', obytes, op);
                 op += enc.codeToMbc(' ', obytes, op);
                 continue;
             case '\t':
                 op += enc.codeToMbc('\\', obytes, op);
                 op += enc.codeToMbc('t', obytes, op);
                 continue;
             case '\n':
                 op += enc.codeToMbc('\\', obytes, op);
                 op += enc.codeToMbc('n', obytes, op);
                 continue;
             case '\r':
                 op += enc.codeToMbc('\\', obytes, op);
                 op += enc.codeToMbc('r', obytes, op);
                 continue;
             case '\f':
                 op += enc.codeToMbc('\\', obytes, op);
                 op += enc.codeToMbc('f', obytes, op);
                 continue;
             case QUOTED_V:
                 op += enc.codeToMbc('\\', obytes, op);
                 op += enc.codeToMbc('v', obytes, op);
                 continue;
             }
             op += enc.codeToMbc(c, obytes, op);
         }
 
         result.setRealSize(op);
         return result;
     }
     
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * @deprecated Use the versions with zero, one, or two args.
      */
     public static IRubyObject last_match_s(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         switch (args.length) {
         case 0:
             return last_match_s(context, recv);
         case 1:
             return last_match_s(context, recv, args[0]);
         default:
             Arity.raiseArgumentError(context.runtime, args.length, 0, 1);
             return null; // not reached
         }
     }
 
     /** rb_reg_s_last_match / match_getter
     *
     */
     @JRubyMethod(name = "last_match", meta = true, reads = BACKREF)
     public static IRubyObject last_match_s(ThreadContext context, IRubyObject recv) {
         IRubyObject match = context.getCurrentScope().getBackRef(context.runtime);
         if (match instanceof RubyMatchData) ((RubyMatchData)match).use();
         return match;
     }
 
     /** rb_reg_s_last_match
     *
     */
     @JRubyMethod(name = "last_match", meta = true, reads = BACKREF)
     public static IRubyObject last_match_s(ThreadContext context, IRubyObject recv, IRubyObject nth) {
         IRubyObject match = context.getCurrentScope().getBackRef(context.runtime);
         if (match.isNil()) return match;
         return nth_match(((RubyMatchData)match).backrefNumber(nth), match);
     }
 
     /** rb_reg_s_union
     *
     */
     @JRubyMethod(name = "union", rest = true, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject union(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         if (args.length == 0) return newRegexp(runtime, ByteList.create("(?!)"));
 
         IRubyObject[] realArgs = args;        
         if (args.length == 1) {
             // The power of the union of one!
             IRubyObject v = TypeConverter.convertToTypeWithCheck(args[0], runtime.getRegexp(), "to_regexp");
             if (!v.isNil()) return v;
             
             IRubyObject a = TypeConverter.convertToTypeWithCheck(args[0], runtime.getArray(), "to_ary");
             if (a.isNil()) return newRegexp(runtime, quote(context, recv, args).getByteList());
 
             RubyArray aa = (RubyArray)a;
             int len = aa.getLength();
             realArgs = new IRubyObject[len];
             for(int i = 0; i<len; i++) {
                 realArgs[i] = aa.entry(i);
             }
         }
 
         KCode kcode = null;
         IRubyObject kcode_re = runtime.getNil();
         RubyString source = runtime.newString();
 
         for (int i = 0; i < realArgs.length; i++) {
             if (0 < i) source.cat((byte)'|');
             IRubyObject v = TypeConverter.convertToTypeWithCheck(realArgs[i], runtime.getRegexp(), "to_regexp");
             if (!v.isNil()) {
                 if (!((RubyRegexp)v).isKCodeDefault()) {
                     if (kcode == null) { // First regexp of union sets kcode.
                         kcode_re = v;
                         kcode = ((RubyRegexp)v).options.getKCode();
                     } else if (((RubyRegexp)v).options.getKCode() != kcode) { // n kcode doesn't match first one
                         IRubyObject str1 = kcode_re.inspect();
                         IRubyObject str2 = v.inspect();
                         throw runtime.newArgumentError("mixed kcode " + str1 + " and " + str2);
                     }
                 }
                 v = ((RubyRegexp)v).to_s();
             } else {
                 v = quote(context, recv, new IRubyObject[]{realArgs[i]});
             }
             source.append(v);
         }
 
         IRubyObject[] _args = new IRubyObject[3];        
         _args[0] = source;
         _args[1] = runtime.getNil();
         if (kcode == null) { // No elements in the array.
             _args[2] = runtime.getNil();
         } else if (kcode == KCode.NONE) {
             _args[2] = runtime.newString("n");
         } else if (kcode == KCode.EUC) {
             _args[2] = runtime.newString("e");
         } else if (kcode == KCode.SJIS) {
             _args[2] = runtime.newString("s");
         } else if (kcode == KCode.UTF8) {
             _args[2] = runtime.newString("u");
         }
         return recv.callMethod(context, "new", _args);
     }
 
     @JRubyMethod(name = "union", rest = true, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject union19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject obj;
         if (args.length == 1 && !(obj = args[0].checkArrayType()).isNil()) {
             RubyArray ary = (RubyArray)obj;
             IRubyObject[]tmp = new IRubyObject[ary.size()];
             ary.copyInto(tmp, 0);
             args = tmp;
         }
 
         Ruby runtime = context.runtime;
         if (args.length == 0) {
             return runtime.getRegexp().newInstance(context, runtime.newString("(?!)"), Block.NULL_BLOCK);
         } else if (args.length == 1) {
             IRubyObject re = TypeConverter.convertToTypeWithCheck(args[0], runtime.getRegexp(), "to_regexp");
             return !re.isNil() ? re : newRegexp(runtime, ((RubyString)quote19(context, recv, args[0])).getByteList());
         } else {
             boolean hasAsciiOnly = false;
             RubyString source = runtime.newString();
             Encoding hasAsciiCompatFixed = null;
             Encoding hasAsciiIncompat = null;
 
             for(int i = 0; i < args.length; i++) {
                 IRubyObject e = args[i];
                 if (i > 0) source.cat((byte)'|');
                 IRubyObject v = TypeConverter.convertToTypeWithCheck(args[i], runtime.getRegexp(), "to_regexp");
                 Encoding enc;
                 if (!v.isNil()) {
                     RubyRegexp regex = (RubyRegexp) v;
                     enc = regex.getEncoding();
                     if (!enc.isAsciiCompatible()) {
                         if (hasAsciiIncompat == null) { // First regexp of union sets kcode.
                             hasAsciiIncompat = enc;
                         } else if (hasAsciiIncompat != enc) { // n kcode doesn't match first one
                             throw runtime.newArgumentError("incompatible encodings: " + hasAsciiIncompat + " and " + enc);
                         }
                     } else if (regex.getOptions().isFixed()) {
                         if (hasAsciiCompatFixed == null) { // First regexp of union sets kcode.
                             hasAsciiCompatFixed = enc;
                         } else if (hasAsciiCompatFixed != enc) { // n kcode doesn't match first one
                             throw runtime.newArgumentError("incompatible encodings: " + hasAsciiCompatFixed + " and " + enc);
                         }
                     } else {
                         hasAsciiOnly = true;
                     }
                     v = regex.to_s();
                 } else {
                     RubyString str = args[i].convertToString();
                     enc = str.getEncoding();
 
                     if (!enc.isAsciiCompatible()) {
                         if (hasAsciiIncompat == null) { // First regexp of union sets kcode.
                             hasAsciiIncompat = enc;
                         } else if (hasAsciiIncompat != enc) { // n kcode doesn't match first one
                             throw runtime.newArgumentError("incompatible encodings: " + hasAsciiIncompat + " and " + enc);
                         }
                     } else if (str.isAsciiOnly()) {
                         hasAsciiOnly = true;
                     } else {
                         if (hasAsciiCompatFixed == null) { // First regexp of union sets kcode.
                             hasAsciiCompatFixed = enc;
                         } else if (hasAsciiCompatFixed != enc) { // n kcode doesn't match first one
                             throw runtime.newArgumentError("incompatible encodings: " + hasAsciiCompatFixed + " and " + enc);
                         }
                     }
                     v = quote19(context, recv, str);
                 }
 
                 if (hasAsciiIncompat != null) {
                     if (hasAsciiOnly) {
                         throw runtime.newArgumentError("ASCII incompatible encoding: " + hasAsciiIncompat);
                     }
                     if (hasAsciiCompatFixed != null) {
                         throw runtime.newArgumentError("incompatible encodings: " + hasAsciiIncompat + " and " + hasAsciiCompatFixed);
                     }
                 }
 
                 // Enebo: not sure why this is needed.
                 if (i == 0) source.setEncoding(enc);
                 source.append(v);
             }
             if (hasAsciiIncompat != null) {
                 source.setEncoding(hasAsciiIncompat);
             } else if (hasAsciiCompatFixed != null) {
                 source.setEncoding(hasAsciiCompatFixed);
             } else {
                 source.setEncoding(ASCIIEncoding.INSTANCE);
             }
             return runtime.getRegexp().newInstance(context, source, Block.NULL_BLOCK);
         }
     }
 
     // rb_reg_raise
     private static void raiseRegexpError(Ruby runtime, ByteList bytes, Encoding enc, RegexpOptions options, String err) {
         throw runtime.newRegexpError(err + ": " + regexpDescription(runtime, bytes, enc, options));
     }
 
     // rb_reg_desc
     private static ByteList regexpDescription(Ruby runtime, ByteList bytes, Encoding enc, RegexpOptions options) {
         return regexpDescription(runtime, bytes.getUnsafeBytes(), bytes.getBegin(), bytes.getRealSize(), enc, options);
     }
     private static ByteList regexpDescription(Ruby runtime, byte[] bytes, int start, int len, Encoding enc, RegexpOptions options) {
         ByteList description = new ByteList();
         description.append((byte)'/');
         appendRegexpString(runtime, description, bytes, start, len, enc);
         description.append((byte)'/');
         appendOptions(description, options);
         return description;
     }
 
     // rb_enc_reg_raise
     private static void raiseRegexpError19(Ruby runtime, ByteList bytes, Encoding enc, RegexpOptions options, String err) {
         // TODO: we loose encoding information here, fix it
         throw runtime.newRegexpError(err + ": " + regexpDescription19(runtime, bytes, options, enc));
     }
 
     // rb_enc_reg_error_desc
     static ByteList regexpDescription19(Ruby runtime, ByteList bytes, RegexpOptions options, Encoding enc) {
         return regexpDescription19(runtime, bytes.getUnsafeBytes(), bytes.getBegin(), bytes.getRealSize(), options, enc);
     }
     private static ByteList regexpDescription19(Ruby runtime, byte[] s, int start, int len, RegexpOptions options, Encoding enc) {
         ByteList description = new ByteList();
         description.setEncoding(enc);
         description.append((byte)'/');
         Encoding resultEnc = runtime.getDefaultInternalEncoding();
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index 67c3e8fadc..501e947b62 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -1,1163 +1,1158 @@
 /*
  **** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jcodings.Encoding;
 import org.jcodings.EncodingDB;
 import org.jcodings.ascii.AsciiTables;
 import org.jcodings.constants.CharacterType;
 import org.jcodings.exception.EncodingException;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jcodings.specific.UTF8Encoding;
 import org.jcodings.util.CaseInsensitiveBytesHash;
 import org.jcodings.util.IntHash;
 import org.joni.Matcher;
 import org.joni.Option;
 import org.joni.Regex;
 import org.joni.Region;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.runtime.encoding.MarshalEncoding;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.CharsetTranscoder;
 import org.jruby.util.ConvertBytes;
 import org.jruby.util.Numeric;
 import org.jruby.util.Pack;
 import org.jruby.util.PerlHash;
 import org.jruby.util.RegexpOptions;
 import org.jruby.util.SipHashInline;
 import org.jruby.util.Sprintf;
 import org.jruby.util.StringSupport;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.XMLConverter;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.jruby.util.string.JavaCrypt;
 
 import java.io.UnsupportedEncodingException;
 import java.nio.charset.Charset;
 import java.util.Locale;
 
 import static org.jruby.CompatVersion.RUBY1_8;
 import static org.jruby.CompatVersion.RUBY1_9;
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.anno.FrameField.BACKREF;
 import static org.jruby.runtime.Helpers.invokedynamic;
 import static org.jruby.runtime.Visibility.PRIVATE;
 import static org.jruby.runtime.invokedynamic.MethodNames.OP_CMP;
 import static org.jruby.runtime.invokedynamic.MethodNames.OP_EQUAL;
 import static org.jruby.util.StringSupport.CR_7BIT;
 import static org.jruby.util.StringSupport.CR_BROKEN;
 import static org.jruby.util.StringSupport.CR_MASK;
 import static org.jruby.util.StringSupport.CR_UNKNOWN;
 import static org.jruby.util.StringSupport.CR_VALID;
 import static org.jruby.util.StringSupport.codeLength;
 import static org.jruby.util.StringSupport.codePoint;
 import static org.jruby.util.StringSupport.codeRangeScan;
 import static org.jruby.util.StringSupport.searchNonAscii;
 import static org.jruby.util.StringSupport.strLengthWithCodeRange;
 import static org.jruby.util.StringSupport.toLower;
 import static org.jruby.util.StringSupport.toUpper;
 import static org.jruby.util.StringSupport.unpackArg;
 import static org.jruby.util.StringSupport.unpackResult;
 
 /**
  * Implementation of Ruby String class
  * 
  * Concurrency: no synchronization is required among readers, but
  * all users must synchronize externally with writers.
  *
  */
 @JRubyClass(name="String", include={"Enumerable", "Comparable"})
 public class RubyString extends RubyObject implements EncodingCapable, MarshalEncoding {
 
     private static final Logger LOG = LoggerFactory.getLogger("RubyString");
 
     private static final ASCIIEncoding ASCII = ASCIIEncoding.INSTANCE;
     private static final UTF8Encoding UTF8 = UTF8Encoding.INSTANCE;
     private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
 
     // string doesn't share any resources
     private static final int SHARE_LEVEL_NONE = 0;
     // string has it's own ByteList, but it's pointing to a shared buffer (byte[])
     private static final int SHARE_LEVEL_BUFFER = 1;
     // string doesn't have it's own ByteList (values)
     private static final int SHARE_LEVEL_BYTELIST = 2;
 
     private volatile int shareLevel = SHARE_LEVEL_NONE;
 
     private ByteList value;
     
     private static final String[][] opTable19 = {
         { "+", "+(binary)" }, 
         { "-", "-(binary)" }
     };
     
     private static final String[][] opTable18 = {
         { "!", "!@" },
         { "~", "~@" },
         { "+", "+(binary)" }, 
         { "-", "-(binary)" },
         { "+@", "+(unary)" }, 
         { "-@", "-(unary)" },
         { "!", "!(unary)" }, 
         { "~", "~(unary)" }
     };
 
     public static RubyClass createStringClass(Ruby runtime) {
         RubyClass stringClass = runtime.defineClass("String", runtime.getObject(), STRING_ALLOCATOR);
         runtime.setString(stringClass);
         stringClass.index = ClassIndex.STRING;
         stringClass.setReifiedClass(RubyString.class);
-        stringClass.kindOf = new RubyModule.KindOf() {
-            @Override
-                public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                    return obj instanceof RubyString;
-                }
-            };
+        stringClass.kindOf = new RubyModule.JavaClassKindOf(RubyString.class);
 
         stringClass.includeModule(runtime.getComparable());
         if (!runtime.is1_9()) stringClass.includeModule(runtime.getEnumerable());
         stringClass.defineAnnotatedMethods(RubyString.class);
 
         return stringClass;
     }
 
     private static ObjectAllocator STRING_ALLOCATOR = new ObjectAllocator() {
         @Override
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return RubyString.newAllocatedString(runtime, klass);
         }
     };
 
     @Override
     public Encoding getEncoding() {
         return value.getEncoding();
     }
 
     @Override
     public void setEncoding(Encoding encoding) {
         value.setEncoding(encoding);
     }
 
     @Override
     public boolean shouldMarshalEncoding() {
         return getEncoding() != ASCIIEncoding.INSTANCE;
     }
 
     @Override
     public Encoding getMarshalEncoding() {
         return getEncoding();
     }
 
     public void associateEncoding(Encoding enc) {
         if (value.getEncoding() != enc) {
             if (!isCodeRangeAsciiOnly() || !enc.isAsciiCompatible()) clearCodeRange();
             value.setEncoding(enc);
         }
     }
 
     public final void setEncodingAndCodeRange(Encoding enc, int cr) {
         value.setEncoding(enc);
         setCodeRange(cr);
     }
 
     public final Encoding toEncoding(Ruby runtime) {
         return runtime.getEncodingService().findEncoding(this);
     }
 
     public final int getCodeRange() {
         return flags & CR_MASK;
     }
 
     public final void setCodeRange(int codeRange) {
         clearCodeRange();
         flags |= codeRange & CR_MASK;
     }
 
     public final void clearCodeRange() {
         flags &= ~CR_MASK;
     }
 
     private void keepCodeRange() {
         if (getCodeRange() == CR_BROKEN) clearCodeRange();
     }
 
     // ENC_CODERANGE_ASCIIONLY
     public final boolean isCodeRangeAsciiOnly() {
         return getCodeRange() == CR_7BIT;
     }
 
     // rb_enc_str_asciionly_p
     public final boolean isAsciiOnly() {
         return value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT;
     }
 
     public final boolean isCodeRangeValid() {
         return (flags & CR_VALID) != 0;
     }
 
     public final boolean isCodeRangeBroken() {
         return (flags & CR_BROKEN) != 0;
     }
 
     static int codeRangeAnd(int cr1, int cr2) {
         if (cr1 == CR_7BIT) return cr2;
         if (cr1 == CR_VALID) return cr2 == CR_7BIT ? CR_VALID : cr2;
         return CR_UNKNOWN;
     }
 
     private void copyCodeRangeForSubstr(RubyString from, Encoding enc) {
         int fromCr = from.getCodeRange();
         if (fromCr == CR_7BIT) {
             setCodeRange(fromCr);
         } else if (fromCr == CR_VALID) {
             if (!enc.isAsciiCompatible() || searchNonAscii(value) != -1) {
                 setCodeRange(CR_VALID);
             } else {
                 setCodeRange(CR_7BIT);
             }
         } else{ 
             if (value.getRealSize() == 0) {
                 setCodeRange(!enc.isAsciiCompatible() ? CR_VALID : CR_7BIT);
             }
         }
     }
 
     private void copyCodeRange(RubyString from) {
         value.setEncoding(from.value.getEncoding());
         setCodeRange(from.getCodeRange());
     }
 
     // rb_enc_str_coderange
     final int scanForCodeRange() {
         int cr = getCodeRange();
         if (cr == CR_UNKNOWN) {
             cr = codeRangeScan(value.getEncoding(), value);
             setCodeRange(cr);
         }
         return cr;
     }
 
     final boolean singleByteOptimizable() {
         return getCodeRange() == CR_7BIT || value.getEncoding().isSingleByte();
     }
 
     final boolean singleByteOptimizable(Encoding enc) {
         return getCodeRange() == CR_7BIT || enc.isSingleByte();
     }
 
     private Encoding isCompatibleWith(RubyString other) { 
         Encoding enc1 = value.getEncoding();
         Encoding enc2 = other.value.getEncoding();
 
         if (enc1 == enc2) return enc1;
 
         if (other.value.getRealSize() == 0) return enc1;
         if (value.getRealSize() == 0) return enc2;
 
         if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
 
         return RubyEncoding.areCompatible(enc1, scanForCodeRange(), enc2, other.scanForCodeRange());
     }
 
     final Encoding isCompatibleWith(EncodingCapable other) {
         if (other instanceof RubyString) return checkEncoding((RubyString)other);
         Encoding enc1 = value.getEncoding();
         Encoding enc2 = other.getEncoding();
 
         if (enc1 == enc2) return enc1;
         if (value.getRealSize() == 0) return enc2;
         if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
         if (enc2 instanceof USASCIIEncoding) return enc1;
         if (scanForCodeRange() == CR_7BIT) return enc2;
         return null;
     }
 
     public final Encoding checkEncoding(RubyString other) {
         Encoding enc = isCompatibleWith(other);
         if (enc == null) throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + 
                                 value.getEncoding() + " and " + other.value.getEncoding());
         return enc;
     }
 
     final Encoding checkEncoding(EncodingCapable other) {
         Encoding enc = isCompatibleWith(other);
         if (enc == null) throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + 
                                 value.getEncoding() + " and " + other.getEncoding());
         return enc;
     }
 
     private Encoding checkDummyEncoding() {
         Encoding enc = value.getEncoding();
         if (enc.isDummy()) throw getRuntime().newEncodingCompatibilityError(
                 "incompatible encoding with this operation: " + enc);
         return enc;
     }
 
     private boolean isComparableWith(RubyString other) {
         ByteList otherValue = other.value;
         if (value.getEncoding() == otherValue.getEncoding() ||
             value.getRealSize() == 0 || otherValue.getRealSize() == 0) return true;
         return isComparableViaCodeRangeWith(other);
     }
 
     private boolean isComparableViaCodeRangeWith(RubyString other) {
         int cr1 = scanForCodeRange();
         int cr2 = other.scanForCodeRange();
 
         if (cr1 == CR_7BIT && (cr2 == CR_7BIT || other.value.getEncoding().isAsciiCompatible())) return true;
         if (cr2 == CR_7BIT && value.getEncoding().isAsciiCompatible()) return true;
         return false;
     }
 
     private int strLength(Encoding enc) {
         if (singleByteOptimizable(enc)) return value.getRealSize();
         return strLength(value, enc);
     }
 
     public final int strLength() {
         if (singleByteOptimizable()) return value.getRealSize();
         return strLength(value);
     }
 
     private int strLength(ByteList bytes) {
         return strLength(bytes, bytes.getEncoding());
     }
 
     private int strLength(ByteList bytes, Encoding enc) {
         if (isCodeRangeValid() && enc instanceof UTF8Encoding) return StringSupport.utf8Length(value);
 
         long lencr = strLengthWithCodeRange(bytes, enc);
         int cr = unpackArg(lencr);
         if (cr != 0) setCodeRange(cr);
         return unpackResult(lencr);
     }
 
     final int subLength(int pos) {
         if (singleByteOptimizable() || pos < 0) return pos;
         return StringSupport.strLength(value.getEncoding(), value.getUnsafeBytes(), value.getBegin(), value.getBegin() + pos);
     }
 
     /** short circuit for String key comparison
      * 
      */
     @Override
     public final boolean eql(IRubyObject other) {
         RubyClass metaclass = getMetaClass();
         Ruby runtime = metaclass.getClassRuntime();
         if (metaclass != runtime.getString() || metaclass != other.getMetaClass()) return super.eql(other);
         return runtime.is1_9() ? eql19(runtime, other) : eql18(runtime, other);
     }
 
     private boolean eql18(Ruby runtime, IRubyObject other) {
         return value.equal(((RubyString)other).value);
     }
 
     // rb_str_hash_cmp
     private boolean eql19(Ruby runtime, IRubyObject other) {
         RubyString otherString = (RubyString)other;
         return isComparableWith(otherString) && value.equal(((RubyString)other).value);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass) {
         this(runtime, rubyClass, EMPTY_BYTE_ARRAY);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, CharSequence value) {
         super(runtime, rubyClass);
         assert value != null;
 
         Encoding defaultEncoding = runtime.getEncodingService().getLocaleEncoding();
         if (defaultEncoding == null) defaultEncoding = UTF8;
 
         this.value = encodeBytelist(value, defaultEncoding);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, CharSequence value, Encoding defaultEncoding) {
         super(runtime, rubyClass);
         assert value != null;
 
         this.value = encodeBytelist(value, defaultEncoding);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, byte[] value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = new ByteList(value);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = value;
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, boolean objectSpace) {
         super(runtime, rubyClass, objectSpace);
         assert value != null;
         this.value = value;
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding encoding, boolean objectSpace) {
         this(runtime, rubyClass, value, objectSpace);
         value.setEncoding(encoding);
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding enc, int cr) {
         this(runtime, rubyClass, value);
         value.setEncoding(enc);
         flags |= cr;
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding enc) {
         this(runtime, rubyClass, value);
         value.setEncoding(enc);
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, int cr) {
         this(runtime, rubyClass, value);
         flags |= cr;
     }
 
     // Deprecated String construction routines
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *  @deprecated  
      */
     @Deprecated
     public RubyString newString(CharSequence s) {
         return new RubyString(getRuntime(), getType(), s);
     }
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *  @deprecated
      */
     @Deprecated
     public RubyString newString(ByteList s) {
         return new RubyString(getRuntime(), getMetaClass(), s);
     }
 
     @Deprecated
     public static RubyString newString(Ruby runtime, RubyClass clazz, CharSequence str) {
         return new RubyString(runtime, clazz, str);
     }
 
     public static RubyString newStringLight(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes, false);
     }
 
     public static RubyString newStringLight(Ruby runtime, int size) {
         return new RubyString(runtime, runtime.getString(), new ByteList(size), false);
     }
 
     public static RubyString newStringLight(Ruby runtime, int size, Encoding encoding) {
         return new RubyString(runtime, runtime.getString(), new ByteList(size), encoding, false);
     }
   
     public static RubyString newString(Ruby runtime, CharSequence str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
 
     public static RubyString newString(Ruby runtime, String str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
 
     public static RubyString newUSASCIIString(Ruby runtime, String str) {
         return new RubyString(runtime, runtime.getString(), str, USASCIIEncoding.INSTANCE);
     }
     
     public static RubyString newString(Ruby runtime, byte[] bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newString(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] copy = new byte[length];
         System.arraycopy(bytes, start, copy, 0, length);
         return new RubyString(runtime, runtime.getString(), new ByteList(copy, false));
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes, Encoding encoding) {
         return new RubyString(runtime, runtime.getString(), bytes, encoding);
     }
     
     public static RubyString newUnicodeString(Ruby runtime, String str) {
         ByteList byteList = new ByteList(RubyEncoding.encodeUTF8(str), UTF8Encoding.INSTANCE, false);
         return new RubyString(runtime, runtime.getString(), byteList);
     }
     
     public static RubyString newUnicodeString(Ruby runtime, CharSequence str) {
         ByteList byteList = new ByteList(RubyEncoding.encodeUTF8(str), UTF8Encoding.INSTANCE, false);
         return new RubyString(runtime, runtime.getString(), byteList);
     }
 
     /**
      * Return a new Ruby String encoded as the default internal encoding given a Java String that
      * has come from an external source. If there is no default internal encoding set, the Ruby
      * String will be encoded using Java's default external encoding. If an internal encoding is
      * set, that encoding will be used for the Ruby String.
      *
      * @param runtime
      * @param str
      * @return
      */
     public static RubyString newInternalFromJavaExternal(Ruby runtime, String str) {
         // Ruby internal
         Encoding internal = runtime.getDefaultInternalEncoding();
         Charset rubyInt = null;
         if (internal != null && internal.getCharset() != null) rubyInt = internal.getCharset();
 
         // Java external, used if no internal
         Charset javaExt = Charset.defaultCharset();
         Encoding javaExtEncoding = runtime.getEncodingService().getJavaDefault();
 
         if (rubyInt == null) {
             return RubyString.newString(
                     runtime,
                     new ByteList(str.getBytes(), javaExtEncoding));
         } else {
             return RubyString.newString(
                     runtime,
                     new ByteList(RubyEncoding.encode(str, rubyInt), internal));
         }
     }
 
     // String construction routines by NOT byte[] buffer and making the target String shared 
     public static RubyString newStringShared(Ruby runtime, RubyString orig) {
         orig.shareLevel = SHARE_LEVEL_BYTELIST;
         RubyString str = new RubyString(runtime, runtime.getString(), orig.value);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }       
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes) {
         return newStringShared(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes, Encoding encoding) {
         return newStringShared(runtime, runtime.getString(), bytes, encoding);
     }
 
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes, int codeRange) {
         RubyString str = new RubyString(runtime, runtime.getString(), bytes, codeRange);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes) {
         RubyString str = new RubyString(runtime, clazz, bytes);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes, Encoding encoding) {
         RubyString str = new RubyString(runtime, clazz, bytes, encoding);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, byte[] bytes) {
         return newStringShared(runtime, new ByteList(bytes, false));
     }
 
     public static RubyString newStringShared(Ruby runtime, byte[] bytes, int start, int length) {
         return newStringShared(runtime, new ByteList(bytes, start, length, false));
     }
 
     public static RubyString newEmptyString(Ruby runtime) {
         return newEmptyString(runtime, runtime.getString());
     }
 
     private static final ByteList EMPTY_ASCII8BIT_BYTELIST = new ByteList(new byte[0], ASCIIEncoding.INSTANCE);
     private static final ByteList EMPTY_USASCII_BYTELIST = new ByteList(new byte[0], USASCIIEncoding.INSTANCE);
 
     public static RubyString newAllocatedString(Ruby runtime, RubyClass metaClass) {
         RubyString empty = new RubyString(runtime, metaClass, EMPTY_ASCII8BIT_BYTELIST);
         empty.shareLevel = SHARE_LEVEL_BYTELIST;
         return empty;
     }
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass) {
         RubyString empty = new RubyString(runtime, metaClass, runtime.is1_9() ? EMPTY_USASCII_BYTELIST : EMPTY_ASCII8BIT_BYTELIST);
         empty.shareLevel = SHARE_LEVEL_BYTELIST;
         return empty;
     }
 
     // String construction routines by NOT byte[] buffer and NOT making the target String shared 
     public static RubyString newStringNoCopy(Ruby runtime, ByteList bytes) {
         return newStringNoCopy(runtime, runtime.getString(), bytes);
     }    
 
     public static RubyString newStringNoCopy(Ruby runtime, RubyClass clazz, ByteList bytes) {
         return new RubyString(runtime, clazz, bytes);
     }    
 
     public static RubyString newStringNoCopy(Ruby runtime, byte[] bytes, int start, int length) {
         return newStringNoCopy(runtime, new ByteList(bytes, start, length, false));
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, byte[] bytes) {
         return newStringNoCopy(runtime, new ByteList(bytes, false));
     }
 
     /** Encoding aware String construction routines for 1.9
      * 
      */
     private static final class EmptyByteListHolder {
         final ByteList bytes;
         final int cr;
         EmptyByteListHolder(Encoding enc) {
             this.bytes = new ByteList(ByteList.NULL_ARRAY, enc);
             this.cr = bytes.getEncoding().isAsciiCompatible() ? CR_7BIT : CR_VALID;
         }
     }
 
     private static EmptyByteListHolder EMPTY_BYTELISTS[] = new EmptyByteListHolder[4];
 
     static EmptyByteListHolder getEmptyByteList(Encoding enc) {
         if (enc == null) enc = ASCIIEncoding.INSTANCE;
         int index = enc.getIndex();
         EmptyByteListHolder bytes;
         if (index < EMPTY_BYTELISTS.length && (bytes = EMPTY_BYTELISTS[index]) != null) {
             return bytes;
         }
         return prepareEmptyByteList(enc);
     }
 
     private static EmptyByteListHolder prepareEmptyByteList(Encoding enc) {
         if (enc == null) enc = ASCIIEncoding.INSTANCE;
         int index = enc.getIndex();
         if (index >= EMPTY_BYTELISTS.length) {
             EmptyByteListHolder tmp[] = new EmptyByteListHolder[index + 4];
             System.arraycopy(EMPTY_BYTELISTS,0, tmp, 0, EMPTY_BYTELISTS.length);
             EMPTY_BYTELISTS = tmp;
         }
         return EMPTY_BYTELISTS[index] = new EmptyByteListHolder(enc);
     }
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass, Encoding enc) {
         EmptyByteListHolder holder = getEmptyByteList(enc);
         RubyString empty = new RubyString(runtime, metaClass, holder.bytes, holder.cr);
         empty.shareLevel = SHARE_LEVEL_BYTELIST;
         return empty;
     }
 
     public static RubyString newEmptyString(Ruby runtime, Encoding enc) {
         return newEmptyString(runtime, runtime.getString(), enc);
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, RubyClass clazz, ByteList bytes, Encoding enc, int cr) {
         return new RubyString(runtime, clazz, bytes, enc, cr);
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, ByteList bytes, Encoding enc, int cr) {
         return newStringNoCopy(runtime, runtime.getString(), bytes, enc, cr);
     }
 
     public static RubyString newUsAsciiStringNoCopy(Ruby runtime, ByteList bytes) {
         return newStringNoCopy(runtime, bytes, USASCIIEncoding.INSTANCE, CR_7BIT);
     }
 
     public static RubyString newUsAsciiStringShared(Ruby runtime, ByteList bytes) {
         RubyString str = newStringNoCopy(runtime, bytes, USASCIIEncoding.INSTANCE, CR_7BIT);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
     
     public static RubyString newUsAsciiStringShared(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] copy = new byte[length];
         System.arraycopy(bytes, start, copy, 0, length);
         return newUsAsciiStringShared(runtime, new ByteList(copy, false));
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.STRING;
     }
 
     @Override
     public Class getJavaClass() {
         return String.class;
     }
 
     @Override
     public RubyString convertToString() {
         return this;
     }
 
     @Override
     public String toString() {
         return decodeString();
     }
 
     /**
      * Convert this Ruby string to a Java String. This version is encoding-aware.
      *
      * @return A decoded Java String, based on this Ruby string's encoding.
      */
     public String decodeString() {
         Ruby runtime = getRuntime();
         // Note: we always choose UTF-8 for outbound strings in 1.8 mode.  This is clearly undesirable
         // but we do not mark any incoming Strings from JI with their real encoding so we just pick utf-8.
         
         if (runtime.is1_9()) {
             Encoding encoding = getEncoding();
             
             if (encoding == UTF8) {
                 // faster UTF8 decoding
                 return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.begin(), value.length());
             }
             
             Charset charset = runtime.getEncodingService().charsetForEncoding(encoding);
 
             // charset is not defined for this encoding in jcodings db.  Try letting Java resolve this.
             if (charset == null) {
                 try {
                     return new String(value.getUnsafeBytes(), value.begin(), value.length(), encoding.toString());
                 } catch (UnsupportedEncodingException uee) {
                     return value.toString();
                 }
             }
             
             return RubyEncoding.decode(value.getUnsafeBytes(), value.begin(), value.length(), charset);
         } else {
             // fast UTF8 decoding
             return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.begin(), value.length());
         }
     }
 
     /**
      * Overridden dup for fast-path logic.
      *
      * @return A new RubyString sharing the original backing store.
      */
     @Override
     public IRubyObject dup() {
         RubyClass mc = metaClass.getRealClass();
         if (mc.index != ClassIndex.STRING) return super.dup();
 
         return strDup(mc.getClassRuntime(), mc.getRealClass());
     }
 
     /** rb_str_dup
      * 
      */
     @Deprecated
     public final RubyString strDup() {
         return strDup(getRuntime(), getMetaClass());
     }
     
     public final RubyString strDup(Ruby runtime) {
         return strDup(runtime, getMetaClass());
     }
     
     @Deprecated
     final RubyString strDup(RubyClass clazz) {
         return strDup(getRuntime(), getMetaClass());
     }
 
     final RubyString strDup(Ruby runtime, RubyClass clazz) {
         shareLevel = SHARE_LEVEL_BYTELIST;
         RubyString dup = new RubyString(runtime, clazz, value);
         dup.shareLevel = SHARE_LEVEL_BYTELIST;
         dup.flags |= flags & (CR_MASK | TAINTED_F | UNTRUSTED_F);
         
         return dup;
     }
     
     /* rb_str_subseq */
     public final RubyString makeSharedString(Ruby runtime, int index, int len) {
         return makeShared(runtime, runtime.getString(), index, len);
     }
     
     public RubyString makeSharedString19(Ruby runtime, int index, int len) {
         return makeShared19(runtime, runtime.getString(), value, index, len);
     }
 
     public final RubyString makeShared(Ruby runtime, int index, int len) {
         return makeShared(runtime, getType(), index, len);
     }
 
     public final RubyString makeShared(Ruby runtime, RubyClass meta, int index, int len) {
         final RubyString shared;
         if (len == 0) {
             shared = newEmptyString(runtime, meta);
         } else if (len == 1) {
             shared = newStringShared(runtime, meta, 
                     RubyInteger.SINGLE_CHAR_BYTELISTS[value.getUnsafeBytes()[value.getBegin() + index] & 0xff]);
         } else {
             if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
             shared = new RubyString(runtime, meta, value.makeShared(index, len));
             shared.shareLevel = SHARE_LEVEL_BUFFER;
         }
 
         shared.infectBy(this);
         return shared;
     }
 
     public final RubyString makeShared19(Ruby runtime, int index, int len) {
         return makeShared19(runtime, value, index, len);
     }
 
     private RubyString makeShared19(Ruby runtime, ByteList value, int index, int len) {
         return makeShared19(runtime, getType(), value, index, len);
     }
     
     private RubyString makeShared19(Ruby runtime, RubyClass meta, ByteList value, int index, int len) {
         final RubyString shared;
         Encoding enc = value.getEncoding();
 
         if (len == 0) {
             shared = newEmptyString(runtime, meta, enc);
         } else if (len == 1) {
             // as with the 1.8 makeShared, don't bother sharing for substrings that are a single byte
             // to get a good speed boost in a number of common scenarios (note though that unlike 1.8,
             // we can't take advantage of SINGLE_CHAR_BYTELISTS since our encoding may not be ascii, but the
             // single byte copy is pretty much negligible)
             shared = newStringShared(runtime,
                                      meta,
                                      new ByteList(new byte[] { (byte) value.get(index) }, enc),
                                      enc);
         } else {
             if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
             shared = new RubyString(runtime, meta, value.makeShared(index, len));
             shared.shareLevel = SHARE_LEVEL_BUFFER;
             shared.copyCodeRangeForSubstr(this, enc); // no need to assign encoding, same bytelist shared
         }
         shared.infectBy(this);
         return shared;
     }
 
     public final void setByteListShared() {
         if (shareLevel != SHARE_LEVEL_BYTELIST) shareLevel = SHARE_LEVEL_BYTELIST;
     }
 
     /**
      * Check that the string can be modified, raising error otherwise.
      *
      * If you plan to modify a string with shared backing store, this
      * method is not sufficient; you will need to call modify() instead.
      */
     public final void modifyCheck() {
         frozenCheck();
     }
 
     private void modifyCheck(byte[] b, int len) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len) throw getRuntime().newRuntimeError("string modified");
     }
 
     private void modifyCheck(byte[] b, int len, Encoding enc) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len || value.getEncoding() != enc) throw getRuntime().newRuntimeError("string modified");
     }
 
     private void frozenCheck() {
         frozenCheck(false);
     }
 
     private void frozenCheck(boolean runtimeError) {
         if (isFrozen()) throw getRuntime().newFrozenError("string", runtimeError);
     }
 
     /** rb_str_modify
      *
      */
     public final void modify() {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 value = value.dup();
             } else {
                 value.unshare();
             }
             shareLevel = SHARE_LEVEL_NONE;
         }
 
         value.invalidate();
     }
 
     public final void modify19() {
         modify();
         clearCodeRange();
     }
 
     private void modifyAndKeepCodeRange() {
         modify();
         keepCodeRange();
     }
 
     /** rb_str_modify (with length bytes ensured)
      *
      */
     public final void modify(int length) {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 value = value.dup(length);
             } else {
                 value.unshare(length);
             }
             shareLevel = SHARE_LEVEL_NONE;
         } else {
             value.ensure(length);
         }
 
         value.invalidate();
     }
 
     public final void modify19(int length) {
         modify(length);
         clearCodeRange();
     }
 
     /** rb_str_resize
      */
     public final void resize(int length) {
         modify();
         if (value.getRealSize() > length) {
             value.setRealSize(length);
         } else if (value.length() < length) {
             value.length(length);
         }
     }
 
     public final void view(ByteList bytes) {
         modifyCheck();
 
         value = bytes;
         shareLevel = SHARE_LEVEL_NONE;
     }
 
     private void view(byte[]bytes) {
         modifyCheck();
 
         value = new ByteList(bytes);
         shareLevel = SHARE_LEVEL_NONE;
 
         value.invalidate();
     }
 
     private void view(int index, int len) {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 // if len == 0 then shared empty
                 value = value.makeShared(index, len);
                 shareLevel = SHARE_LEVEL_BUFFER;
             } else {
                 value.view(index, len);
             }
         } else {
             value.view(index, len);
             // FIXME this below is temporary, but its much safer for COW (it prevents not shared Strings with begin != 0)
             // this allows now e.g.: ByteList#set not to be begin aware
             shareLevel = SHARE_LEVEL_BUFFER;
         }
 
         value.invalidate();
     }
 
     public static String bytesToString(byte[] bytes, int beg, int len) {
         return new String(ByteList.plain(bytes, beg, len));
     }
 
     public static String byteListToString(ByteList bytes) {
         return bytesToString(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
     }
 
     public static String bytesToString(byte[] bytes) {
         return bytesToString(bytes, 0, bytes.length);
     }
 
     public static byte[] stringToBytes(String string) {
         return ByteList.plain(string);
     }
 
     @Override
     public RubyString asString() {
         return this;
     }
 
     @Override
     public IRubyObject checkStringType() {
         return this;
     }
 
     @Override
     public IRubyObject checkStringType19() {
         return this;
     }
 
     @JRubyMethod(name = "try_convert", meta = true, compat = RUBY1_9)
     public static IRubyObject try_convert(ThreadContext context, IRubyObject recv, IRubyObject str) {
         return str.checkStringType();
     }
 
     @JRubyMethod(name = {"to_s", "to_str"})
     @Override
     public IRubyObject to_s() {
         Ruby runtime = getRuntime();
         if (getMetaClass().getRealClass() != runtime.getString()) {
             return strDup(runtime, runtime.getString());
         }
         return this;
     }
 
     @Override
     public final int compareTo(IRubyObject other) {
         Ruby runtime = getRuntime();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             return runtime.is1_9() ? op_cmp19(otherString) : op_cmp(otherString);
         }
         return (int)op_cmpCommon(runtime.getCurrentContext(), other).convertToInteger().getLongValue();
     }
 
     /* rb_str_cmp_m */
     @JRubyMethod(name = "<=>", compat = RUBY1_8)
     @Override
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.runtime.newFixnum(op_cmp((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     @JRubyMethod(name = "<=>", compat = RUBY1_9)
     public IRubyObject op_cmp19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.runtime.newFixnum(op_cmp19((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     private IRubyObject op_cmpCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         // deal with case when "other" is not a string
         if (other.respondsTo("to_str") && other.respondsTo("<=>")) {
             IRubyObject result = invokedynamic(context, other, OP_CMP, this);
             if (result.isNil()) return result;
             if (result instanceof RubyFixnum) {
                 return RubyFixnum.newFixnum(runtime, -((RubyFixnum)result).getLongValue());
             } else {
                 return RubyFixnum.zero(runtime).callMethod(context, "-", result);
             }
         }
         return runtime.getNil();
     }
 
     /** rb_str_equal
      *
      */
     @JRubyMethod(name = "==", compat = RUBY1_8)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             return value.equal(((RubyString)other).value) ? runtime.getTrue() : runtime.getFalse();
         }
         return op_equalCommon(context, other);
     }
 
     @JRubyMethod(name = {"==", "==="}, compat = RUBY1_9)
     public IRubyObject op_equal19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             return isComparableWith(otherString) && value.equal(otherString.value) ? runtime.getTrue() : runtime.getFalse();
         }
         return op_equalCommon(context, other);
     }
 
     private IRubyObject op_equalCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         if (!other.respondsTo("to_str")) return runtime.getFalse();
         return invokedynamic(context, other, OP_EQUAL, this).isTrue() ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_8)
     public IRubyObject op_plus(ThreadContext context, IRubyObject _str) {
         RubyString str = _str.convertToString();
         RubyString resultStr = newString(context.runtime, addByteLists(value, str.value));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_9)
     public IRubyObject op_plus19(ThreadContext context, IRubyObject _str) {
         RubyString str = _str.convertToString();
         Encoding enc = checkEncoding(str);
         RubyString resultStr = newStringNoCopy(context.runtime, addByteLists(value, str.value),
                                     enc, codeRangeAnd(getCodeRange(), str.getCodeRange()));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
 
     private ByteList addByteLists(ByteList value1, ByteList value2) {
         ByteList result = new ByteList(value1.getRealSize() + value2.getRealSize());
         result.setRealSize(value1.getRealSize() + value2.getRealSize());
         System.arraycopy(value1.getUnsafeBytes(), value1.getBegin(), result.getUnsafeBytes(), 0, value1.getRealSize());
         System.arraycopy(value2.getUnsafeBytes(), value2.getBegin(), result.getUnsafeBytes(), value1.getRealSize(), value2.getRealSize());
         return result;
     }
 
diff --git a/src/org/jruby/RubySymbol.java b/src/org/jruby/RubySymbol.java
index b37a31ddec..40c22333d3 100644
--- a/src/org/jruby/RubySymbol.java
+++ b/src/org/jruby/RubySymbol.java
@@ -1,921 +1,916 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Derek Berner <derek.berner@state.nm.us>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block.Type;
 import static org.jruby.util.StringSupport.codeLength;
 import static org.jruby.util.StringSupport.codePoint;
 
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.locks.ReentrantLock;
 
 import org.jcodings.Encoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ContextAwareBlockBody;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.FunctionalCachingCallSite;
 import org.jruby.runtime.encoding.MarshalEncoding;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.PerlHash;
 import org.jruby.util.SipHashInline;
 
 /**
  * Represents a Ruby symbol (e.g. :bar)
  */
 @JRubyClass(name="Symbol")
 public class RubySymbol extends RubyObject implements MarshalEncoding {
     public static final long symbolHashSeedK0 = 5238926673095087190l;
 
     private final String symbol;
     private final int id;
     private final ByteList symbolBytes;
     private final int hashCode;
     
     /**
      * 
      * @param runtime
      * @param internedSymbol the String value of the new Symbol. This <em>must</em>
      *                       have been previously interned
      */
     private RubySymbol(Ruby runtime, String internedSymbol, ByteList symbolBytes) {
         super(runtime, runtime.getSymbol(), false);
         // symbol string *must* be interned
 
         //        assert internedSymbol == internedSymbol.intern() : internedSymbol + " is not interned";
 
         if (!runtime.is1_9()) {
             int length = symbolBytes.getBegin() + symbolBytes.getRealSize();
             for (int i = symbolBytes.getBegin(); i < length; i++) {
                 if (symbolBytes.getUnsafeBytes()[i] == 0) {
                     throw runtime.newSyntaxError("symbol cannot contain '\\0'");
                 }
             }
         }
 
         this.symbol = internedSymbol;
         this.symbolBytes = symbolBytes;
         this.id = runtime.allocSymbolId();
 
         long hash = runtime.isSiphashEnabled() ? SipHashInline.hash24(
                 symbolHashSeedK0, 0, symbolBytes.getUnsafeBytes(),
                 symbolBytes.getBegin(), symbolBytes.getRealSize()) :
                 PerlHash.hash(symbolHashSeedK0, symbolBytes.getUnsafeBytes(),
                 symbolBytes.getBegin(), symbolBytes.getRealSize());
         this.hashCode = (int) hash;
     }
 
     private RubySymbol(Ruby runtime, String internedSymbol) {
         this(runtime, internedSymbol, symbolBytesFromString(runtime, internedSymbol));
     }
 
     public static RubyClass createSymbolClass(Ruby runtime) {
         RubyClass symbolClass = runtime.defineClass("Symbol", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setSymbol(symbolClass);
         RubyClass symbolMetaClass = symbolClass.getMetaClass();
         symbolClass.index = ClassIndex.SYMBOL;
         symbolClass.setReifiedClass(RubySymbol.class);
-        symbolClass.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubySymbol;
-            }
-        };
+        symbolClass.kindOf = new RubyModule.JavaClassKindOf(RubySymbol.class);
 
         symbolClass.defineAnnotatedMethods(RubySymbol.class);
         symbolMetaClass.undefineMethod("new");
         
         return symbolClass;
     }
     
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.SYMBOL;
     }
 
     /** rb_to_id
      * 
      * @return a String representation of the symbol 
      */
     @Override
     public String asJavaString() {
         return symbol;
     }
 
     @Override
     public String toString() {
         return symbol;
     }
 
     final ByteList getBytes() {
         return symbolBytes;
     }
 
     /** short circuit for Symbol key comparison
      * 
      */
     @Override
     public final boolean eql(IRubyObject other) {
         return other == this;
     }
 
     @Override
     public boolean isImmediate() {
     	return true;
     }
 
     @Override
     public RubyClass getSingletonClass() {
         throw getRuntime().newTypeError("can't define singleton");
     }
 
     public static RubySymbol getSymbolLong(Ruby runtime, long id) {
         return runtime.getSymbolTable().lookup(id);
     }
 
     /* Symbol class methods.
      * 
      */
 
     public static RubySymbol newSymbol(Ruby runtime, String name) {
         return runtime.getSymbolTable().getSymbol(name);
     }
 
     @Deprecated
     public RubyFixnum to_i() {
         return to_i(getRuntime());
     }
 
     @JRubyMethod(name = "to_i", compat = CompatVersion.RUBY1_8)
     public RubyFixnum to_i(ThreadContext context) {
         return to_i(context.runtime);
     }
 
     private final RubyFixnum to_i(Ruby runtime) {
         return runtime.newFixnum(id);
     }
 
     @Deprecated
     public RubyFixnum to_int() {
         return to_int(getRuntime());
     }
 
     @JRubyMethod(name = "to_int", compat = CompatVersion.RUBY1_8)
     public RubyFixnum to_int(ThreadContext context) {
         return to_int(context.runtime);
     }
 
     private final RubyFixnum to_int(Ruby runtime) {
         if (runtime.isVerbose()) runtime.getWarnings().warn(ID.SYMBOL_AS_INTEGER, "treating Symbol as an integer");
 
         return to_i(runtime);
     }
 
     @Deprecated
     @Override
     public IRubyObject inspect() {
         return inspect(getRuntime());
     }
     @JRubyMethod(name = "inspect", compat = CompatVersion.RUBY1_8)
     public IRubyObject inspect(ThreadContext context) {
         return inspect(context.runtime);
     }
     private final IRubyObject inspect(Ruby runtime) {
         final ByteList bytes = isSymbolName(symbol) ? symbolBytes : 
                 ((RubyString)RubyString.newString(runtime, symbolBytes).dump()).getByteList();
 
         ByteList result = new ByteList(bytes.getRealSize() + 1);
         result.append((byte)':').append(bytes);
 
         return RubyString.newString(runtime, result);
     }
 
     @Deprecated
     public IRubyObject inspect19() {
         return inspect19(getRuntime());
     }
 
     @JRubyMethod(name = "inspect", compat = CompatVersion.RUBY1_9)
     public IRubyObject inspect19(ThreadContext context) {
         return inspect19(context.runtime);
     }
 
     private final IRubyObject inspect19(Ruby runtime) {
         ByteList result = new ByteList(symbolBytes.getRealSize() + 1);
         result.setEncoding(symbolBytes.getEncoding());
         result.append((byte)':');
         result.append(symbolBytes);
 
         RubyString str = RubyString.newString(runtime, result); 
         // TODO: 1.9 rb_enc_symname_p
         if (isPrintable() && isSymbolName19(symbol)) return str;
             
         str = (RubyString)str.inspect19();
         ByteList bytes = str.getByteList();
         bytes.set(0, ':');
         bytes.set(1, '"');
         
         return str;
     }
 
     @Override
     public IRubyObject to_s() {
         return to_s(getRuntime());
     }
     
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s(ThreadContext context) {
         return to_s(context.runtime);
     }
     
     private final IRubyObject to_s(Ruby runtime) {
         return RubyString.newStringShared(runtime, symbolBytes);
     }
 
     public IRubyObject id2name() {
         return to_s(getRuntime());
     }
     
     @JRubyMethod(name = "id2name")
     public IRubyObject id2name(ThreadContext context) {
         return to_s(context);
     }
 
     @JRubyMethod(name = "===", required = 1)
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return super.op_equal(context, other);
     }
 
     @Deprecated
     @Override
     public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
 
     @JRubyMethod(name = "hash")
     public RubyFixnum hash(ThreadContext context) {
         return context.runtime.newFixnum(hashCode());
     }
     
     @Override
     public int hashCode() {
         return hashCode;
     }
 
     public int getId() {
         return id;
     }
     
     @Override
     public boolean equals(Object other) {
         return other == this;
     }
     
     @JRubyMethod(name = "to_sym")
     public IRubyObject to_sym() {
         return this;
     }
 
     @JRubyMethod(name = "intern", compat = CompatVersion.RUBY1_9)
     public IRubyObject to_sym19() {
         return this;
     }
 
     @Override
     public IRubyObject taint(ThreadContext context) {
         return this;
     }
 
     private RubyString newShared(Ruby runtime) {
         return RubyString.newStringShared(runtime, symbolBytes);
     }
 
     private RubyString rubyStringFromString(Ruby runtime) {
         return RubyString.newString(runtime, symbol);
     }
 
     @JRubyMethod(name = {"succ", "next"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject succ(ThreadContext context) {
         Ruby runtime = context.runtime;
         return newSymbol(runtime, newShared(runtime).succ19(context).toString());
     }
 
     @JRubyMethod(name = "<=>", compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         
         return !(other instanceof RubySymbol) ? runtime.getNil() :
                 newShared(runtime).op_cmp19(context, ((RubySymbol)other).newShared(runtime));
     }
 
     @JRubyMethod(name = "casecmp", compat = CompatVersion.RUBY1_9)
     public IRubyObject casecmp(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         
         return !(other instanceof RubySymbol) ? runtime.getNil() :
                 newShared(runtime).casecmp19(context, ((RubySymbol) other).newShared(runtime));
     }
 
     @JRubyMethod(name = {"=~", "match"}, compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject op_match19(ThreadContext context, IRubyObject other) {
         return newShared(context.runtime).op_match19(context, other);
     }
 
     @JRubyMethod(name = {"[]", "slice"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_aref(ThreadContext context, IRubyObject arg) {
         return newShared(context.runtime).op_aref19(context, arg);
     }
 
     @JRubyMethod(name = {"[]", "slice"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_aref(ThreadContext context, IRubyObject arg1, IRubyObject arg2) {
         return newShared(context.runtime).op_aref19(context, arg1, arg2);
     }
 
     @JRubyMethod(name = {"length", "size"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject length() {
         return newShared(getRuntime()).length19();
     }
 
     @JRubyMethod(name = "empty?", compat = CompatVersion.RUBY1_9)
     public IRubyObject empty_p(ThreadContext context) {
         return newShared(context.runtime).empty_p(context);
     }
 
     @JRubyMethod(name = "upcase", compat = CompatVersion.RUBY1_9)
     public IRubyObject upcase(ThreadContext context) {
         Ruby runtime = context.runtime;
         
         return newSymbol(runtime, rubyStringFromString(runtime).upcase19(context).toString());
     }
 
     @JRubyMethod(name = "downcase", compat = CompatVersion.RUBY1_9)
     public IRubyObject downcase(ThreadContext context) {
         Ruby runtime = context.runtime;
         
         return newSymbol(runtime, rubyStringFromString(runtime).downcase19(context).toString());
     }
 
     @JRubyMethod(name = "capitalize", compat = CompatVersion.RUBY1_9)
     public IRubyObject capitalize(ThreadContext context) {
         Ruby runtime = context.runtime;
         
         return newSymbol(runtime, rubyStringFromString(runtime).capitalize19(context).toString());
     }
 
     @JRubyMethod(name = "swapcase", compat = CompatVersion.RUBY1_9)
     public IRubyObject swapcase(ThreadContext context) {
         Ruby runtime = context.runtime;
         
         return newSymbol(runtime, rubyStringFromString(runtime).swapcase19(context).toString());
     }
 
     @JRubyMethod(name = "encoding", compat = CompatVersion.RUBY1_9)
     public IRubyObject encoding(ThreadContext context) {
         return context.runtime.getEncodingService().getEncoding(symbolBytes.getEncoding());
     }
     
     @JRubyMethod
     public IRubyObject to_proc(ThreadContext context) {
         StaticScope scope = context.runtime.getStaticScopeFactory().getDummyScope();
         final CallSite site = new FunctionalCachingCallSite(symbol);
         BlockBody body = new ContextAwareBlockBody(scope, Arity.OPTIONAL, BlockBody.SINGLE_RESTARG) {
             private IRubyObject yieldInner(ThreadContext context, RubyArray array, Block block) {
                 if (array.isEmpty()) {
                     throw context.runtime.newArgumentError("no receiver given");
                 }
 
                 IRubyObject self = array.shift(context);
 
                 return site.call(context, self, self, array.toJavaArray(), block);
             }
 
             @Override
             public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self,
                     RubyModule klass, boolean aValue, Binding binding, Block.Type type, Block block) {
                 RubyArray array = aValue && value instanceof RubyArray ?
                         (RubyArray) value : ArgsUtil.convertToRubyArray(context.runtime, value, false);
 
                 return yieldInner(context, array, block);
             }
 
             @Override
             public IRubyObject yield(ThreadContext context, IRubyObject value,
                     Binding binding, Block.Type type, Block block) {
                 return yieldInner(context, ArgsUtil.convertToRubyArray(context.runtime, value, false), block);
             }
             
             @Override
             public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Type type) {
                 return yieldInner(context, ArgsUtil.convertToRubyArray(context.runtime, value, false), Block.NULL_BLOCK);
             }
 
             @Override
             public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, RubyModule klass, boolean aValue, Binding binding, Type type) {
                 RubyArray array = aValue && value instanceof RubyArray ?
                         (RubyArray) value : ArgsUtil.convertToRubyArray(context.runtime, value, false);
 
                 return yieldInner(context, array, Block.NULL_BLOCK);
             }
 
             @Override
             public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
                 return site.call(context, arg0, arg0);
             }
 
             @Override
             public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
                 return site.call(context, arg0, arg0, arg1);
             }
 
             @Override
             public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
                 return site.call(context, arg0, arg0, arg1, arg2);
             }
 
             @Override
             public Block cloneBlock(Binding binding) {
                 return new Block(this, binding);
             }
 
             public String getFile() {
                 return symbol;
             }
 
             public int getLine() {
                 return -1;
             }
         };
 
         return RubyProc.newProc(context.runtime,
                                 new Block(body, context.currentBinding()),
                                 Block.Type.PROC);
     }
     
     private static boolean isIdentStart(char c) {
         return ((c >= 'a' && c <= 'z')|| (c >= 'A' && c <= 'Z') || c == '_');
     }
     
     private static boolean isIdentChar(char c) {
         return ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_');
     }
     
     private static boolean isIdentifier(String s) {
         if (s == null || s.length() <= 0 || !isIdentStart(s.charAt(0))) return false;
 
         for (int i = 1; i < s.length(); i++) {
             if (!isIdentChar(s.charAt(i))) return false;
         }
         
         return true;
     }
     
     /**
      * is_special_global_name from parse.c.  
      * @param s
      * @return
      */
     private static boolean isSpecialGlobalName(String s) {
         if (s == null || s.length() <= 0) return false;
 
         int length = s.length();
            
         switch (s.charAt(0)) {        
         case '~': case '*': case '$': case '?': case '!': case '@': case '/': case '\\':        
         case ';': case ',': case '.': case '=': case ':': case '<': case '>': case '\"':        
         case '&': case '`': case '\'': case '+': case '0':
             return length == 1;            
         case '-':
             return (length == 1 || (length == 2 && isIdentChar(s.charAt(1))));
             
         default:
             for (int i = 0; i < length; i++) {
                 if (!Character.isDigit(s.charAt(i))) return false;
             }
         }
         
         return true;
     }
 
     private boolean isPrintable() {
         Ruby runtime = getRuntime();
         int p = symbolBytes.getBegin();
         int end = p + symbolBytes.getRealSize();
         byte[]bytes = symbolBytes.getUnsafeBytes();
         Encoding enc = symbolBytes.getEncoding();
 
         while (p < end) {
             int c = codePoint(runtime, enc, bytes, p, end);
             
             if (!enc.isPrint(c)) return false;
             
             p += codeLength(runtime, enc, c);
         }
         
         return true;
     }
 
     private static boolean isSymbolName19(String s) {
         if (s == null || s.length() < 1) return false;
 
         int length = s.length();
         char c = s.charAt(0);
         
         return isSymbolNameCommon(s, c, length) || 
                 (c == '!' && (length == 1 ||
                              (length == 2 && (s.charAt(1) == '~' || s.charAt(1) == '=')))) ||
                 isSymbolLocal(s, c, length);
     }
 
     private static boolean isSymbolName(String s) {
         if (s == null || s.length() < 1) return false;
 
         int length = s.length();
         char c = s.charAt(0);
         
         return isSymbolNameCommon(s, c, length) || isSymbolLocal(s, c, length);
     }
 
     private static boolean isSymbolNameCommon(String s, char c, int length) {        
         switch (c) {
         case '$':
             if (length > 1 && isSpecialGlobalName(s.substring(1))) return true;
 
             return isIdentifier(s.substring(1));
         case '@':
             int offset = 1;
             if (length >= 2 && s.charAt(1) == '@') offset++;
 
             return isIdentifier(s.substring(offset));
         case '<':
             return (length == 1 || (length == 2 && (s.equals("<<") || s.equals("<="))) ||
                     (length == 3 && s.equals("<=>")));
         case '>':
             return (length == 1) || (length == 2 && (s.equals(">>") || s.equals(">=")));
         case '=':
             return ((length == 2 && (s.equals("==") || s.equals("=~"))) ||
                     (length == 3 && s.equals("===")));
         case '*':
             return (length == 1 || (length == 2 && s.equals("**")));
         case '+':
             return (length == 1 || (length == 2 && s.equals("+@")));
         case '-':
             return (length == 1 || (length == 2 && s.equals("-@")));
         case '|': case '^': case '&': case '/': case '%': case '~': case '`':
             return length == 1;
         case '[':
             return s.equals("[]") || s.equals("[]=");
         }
         return false;
     }
 
     private static boolean isSymbolLocal(String s, char c, int length) {
         if (!isIdentStart(c)) return false;
 
         boolean localID = (c >= 'a' && c <= 'z');
         int last = 1;
 
         for (; last < length; last++) {
             char d = s.charAt(last);
 
             if (!isIdentChar(d)) break;
         }
 
         if (last == length) return true;
         if (localID && last == length - 1) {
             char d = s.charAt(last);
 
             return d == '!' || d == '?' || d == '=';
         }
 
         return false;
     }
     
     @JRubyMethod(name = "all_symbols", meta = true)
     public static IRubyObject all_symbols(ThreadContext context, IRubyObject recv) {
         return context.runtime.getSymbolTable().all_symbols();
     }
     @Deprecated
     public static IRubyObject all_symbols(IRubyObject recv) {
         return recv.getRuntime().getSymbolTable().all_symbols();
     }
 
     public static RubySymbol unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubySymbol result = newSymbol(input.getRuntime(), RubyString.byteListToString(input.unmarshalString()));
         
         input.registerLinkTarget(result);
         
         return result;
     }
 
     @Override
     public Object toJava(Class target) {
         if (target == String.class || target == CharSequence.class) return symbol;
 
         return super.toJava(target);
     }
 
     private static ByteList symbolBytesFromString(Ruby runtime, String internedSymbol) {
         if (runtime.is1_9()) {
             return new ByteList(ByteList.plain(internedSymbol), USASCIIEncoding.INSTANCE, false);
         } else {
             return ByteList.create(internedSymbol);
         }
     }
 
     public static final class SymbolTable {
         static final int DEFAULT_INITIAL_CAPACITY = 2048; // *must* be power of 2!
         static final int MAXIMUM_CAPACITY = 1 << 30;
         static final float DEFAULT_LOAD_FACTOR = 0.75f;
         
         private final ReentrantLock tableLock = new ReentrantLock();
         private volatile SymbolEntry[] symbolTable;
         private final ConcurrentHashMap<ByteList, RubySymbol> bytelistTable = new ConcurrentHashMap<ByteList, RubySymbol>(100, 0.75f, Runtime.getRuntime().availableProcessors());
         private int size;
         private int threshold;
         private final float loadFactor;
         private final Ruby runtime;
         
         public SymbolTable(Ruby runtime) {
             this.runtime = runtime;
             this.loadFactor = DEFAULT_LOAD_FACTOR;
             this.threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
             this.symbolTable = new SymbolEntry[DEFAULT_INITIAL_CAPACITY];
         }
         
         // note all fields are final -- rehash creates new entries when necessary.
         // as documented in java.util.concurrent.ConcurrentHashMap.java, that will
         // statistically affect only a small percentage (< 20%) of entries for a given rehash.
         static class SymbolEntry {
             final int hash;
             final String name;
             final RubySymbol symbol;
             final SymbolEntry next;
             
             SymbolEntry(int hash, String name, RubySymbol symbol, SymbolEntry next) {
                 this.hash = hash;
                 this.name = name;
                 this.symbol = symbol;
                 this.next = next;
             }
         }
 
         public RubySymbol getSymbol(String name) {
             int hash = name.hashCode();
             SymbolEntry[] table = symbolTable;
             
             for (SymbolEntry e = getEntryFromTable(table, hash); e != null; e = e.next) {
                 if (isSymbolMatch(name, hash, e)) return e.symbol;
             }
             
             return createSymbol(name, symbolBytesFromString(runtime, name), hash, table);
         }
 
         public RubySymbol getSymbol(ByteList bytes) {
             RubySymbol symbol = bytelistTable.get(bytes);
             if (symbol != null) return symbol;
 
             String name = bytes.toString();
             int hash = name.hashCode();
             SymbolEntry[] table = symbolTable;
             
             for (SymbolEntry e = getEntryFromTable(table, hash); e != null; e = e.next) {
                 if (isSymbolMatch(name, hash, e)) {
                     symbol = e.symbol;
                     break;
                 }
             }
 
             if (symbol == null) {
                 symbol = createSymbol(name, bytes, hash, table);
             }
             
             bytelistTable.put(bytes, symbol);
 
             return symbol;
         }
 
         public RubySymbol fastGetSymbol(String internedName) {
             SymbolEntry[] table = symbolTable;
             
             for (SymbolEntry e = getEntryFromTable(symbolTable, internedName.hashCode()); e != null; e = e.next) {
                 if (isSymbolMatch(internedName, e)) return e.symbol;
             }
             
             return fastCreateSymbol(internedName, table);
         }
 
         private static SymbolEntry getEntryFromTable(SymbolEntry[] table, int hash) {
             return table[hash & (table.length - 1)];
         }
 
         private static boolean isSymbolMatch(String name, int hash, SymbolEntry entry) {
             return hash == entry.hash && name.equals(entry.name);
         }
 
         private static boolean isSymbolMatch(String internedName, SymbolEntry entry) {
             return internedName == entry.name;
         }
 
         private RubySymbol createSymbol(String name, ByteList value, int hash, SymbolEntry[] table) {
             ReentrantLock lock;
             (lock = tableLock).lock();
             try {
                 int index;                
                 int potentialNewSize = size + 1;
                 
                 table = potentialNewSize > threshold ? rehash() : symbolTable;
 
                 // try lookup again under lock
                 for (SymbolEntry e = table[index = hash & (table.length - 1)]; e != null; e = e.next) {
                     if (hash == e.hash && name.equals(e.name)) return e.symbol;
                 }
                 String internedName = name.intern();
                 RubySymbol symbol = new RubySymbol(runtime, internedName, value);
                 table[index] = new SymbolEntry(hash, internedName, symbol, table[index]);
                 size = potentialNewSize;
                 // write-volatile
                 symbolTable = table;
                 return symbol;
             } finally {
                 lock.unlock();
             }
         }
 
         private RubySymbol fastCreateSymbol(String internedName, SymbolEntry[] table) {
             ReentrantLock lock;
             (lock = tableLock).lock();
             try {
                 int index;
                 int hash;
                 int potentialNewSize = size + 1;
                 
                 table = potentialNewSize > threshold ? rehash() : symbolTable;
 
                 // try lookup again under lock
                 for (SymbolEntry e = table[index = (hash = internedName.hashCode()) & (table.length - 1)]; e != null; e = e.next) {
                     if (internedName == e.name) return e.symbol;
                 }
                 RubySymbol symbol = new RubySymbol(runtime, internedName);
                 table[index] = new SymbolEntry(hash, internedName, symbol, table[index]);
                 size = potentialNewSize;
                 // write-volatile
                 symbolTable = table;
                 return symbol;
             } finally {
                 lock.unlock();
             }
         }
         
         // backwards-compatibility, but threadsafe now
         public RubySymbol lookup(String name) {
             int hash = name.hashCode();
             SymbolEntry[] table;
             
             for (SymbolEntry e = (table = symbolTable)[hash & (table.length - 1)]; e != null; e = e.next) {
                 if (hash == e.hash && name.equals(e.name)) return e.symbol;
             }
 
             return null;
         }
         
         public RubySymbol lookup(long id) {
             SymbolEntry[] table = symbolTable;
             
             for (int i = table.length; --i >= 0; ) {
                 for (SymbolEntry e = table[i]; e != null; e = e.next) {
                     if (id == e.symbol.id) return e.symbol;
                 }
             }
 
             return null;
         }
         
         public RubyArray all_symbols() {
             SymbolEntry[] table = this.symbolTable;
             RubyArray array = runtime.newArray(this.size);
             
             for (int i = table.length; --i >= 0; ) {
                 for (SymbolEntry e = table[i]; e != null; e = e.next) {
                     array.append(e.symbol);
                 }
             }
             return array;
         }
         
         // not so backwards-compatible here, but no one should have been
         // calling this anyway.
         @Deprecated
         public void store(RubySymbol symbol) {
             throw new UnsupportedOperationException();
         }
         
         private SymbolEntry[] rehash() {
             SymbolEntry[] oldTable = symbolTable;
             int oldCapacity = oldTable.length;
             
             if (oldCapacity >= MAXIMUM_CAPACITY) return oldTable;
             
             int newCapacity = oldCapacity << 1;
             SymbolEntry[] newTable = new SymbolEntry[newCapacity];
             threshold = (int)(newCapacity * loadFactor);
             int sizeMask = newCapacity - 1;
             SymbolEntry e;
             for (int i = oldCapacity; --i >= 0; ) {
                 // We need to guarantee that any existing reads of old Map can
                 //  proceed. So we cannot yet null out each bin.
                 e = oldTable[i];
 
                 if (e != null) {
                     SymbolEntry next = e.next;
                     int idx = e.hash & sizeMask;
 
                     //  Single node on list
                     if (next == null) {
                         newTable[idx] = e;
                     } else {
                         // Reuse trailing consecutive sequence at same slot
                         SymbolEntry lastRun = e;
                         int lastIdx = idx;
                         for (SymbolEntry last = next;
                              last != null;
                              last = last.next) {
                             int k = last.hash & sizeMask;
                             if (k != lastIdx) {
                                 lastIdx = k;
                                 lastRun = last;
                             }
                         }
                         newTable[lastIdx] = lastRun;
 
                         // Clone all remaining nodes
                         for (SymbolEntry p = e; p != lastRun; p = p.next) {
                             int k = p.hash & sizeMask;
                             SymbolEntry n = newTable[k];
                             newTable[k] = new SymbolEntry(p.hash, p.name, p.symbol, n);
                         }
                     }
                 }
             }
             symbolTable = newTable;
             return newTable;
         }
     }
 
     public boolean shouldMarshalEncoding() {
         return getMarshalEncoding() != USASCIIEncoding.INSTANCE;
     }
 
     public Encoding getMarshalEncoding() {
         return symbolBytes.getEncoding();
     }
 }
diff --git a/src/org/jruby/RubyYielder.java b/src/org/jruby/RubyYielder.java
index c8b715ef7d..ac86a2a130 100644
--- a/src/org/jruby/RubyYielder.java
+++ b/src/org/jruby/RubyYielder.java
@@ -1,97 +1,92 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 
 @JRubyClass(name = "Enumerator::Yielder")
 public class RubyYielder extends RubyObject {
     private RubyProc proc; 
 
     public static RubyClass createYielderClass(Ruby runtime) {
         RubyClass yielderc = runtime.defineClassUnder("Yielder", runtime.getObject(), YIELDER_ALLOCATOR, runtime.getEnumerator());
         runtime.setYielder(yielderc);
         yielderc.index = ClassIndex.YIELDER;
-        yielderc.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyYielder;
-            }
-        };
+        yielderc.kindOf = new RubyModule.JavaClassKindOf(RubyYielder.class);
 
         yielderc.defineAnnotatedMethods(RubyYielder.class);
         return yielderc;
     }
 
     private static ObjectAllocator YIELDER_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyYielder(runtime, klass);
         }
     };
 
     public RubyYielder(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
     }
 
     public RubyYielder(Ruby runtime) {
         super(runtime, runtime.getYielder());
     }
 
     private void checkInit() {
         if (proc == null) throw getRuntime().newArgumentError("uninitializer yielder");
     }
 
     @JRubyMethod(visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, Block block) {
         Ruby runtime = context.runtime;
         if (!block.isGiven()) throw runtime.newLocalJumpErrorNoBlock();
         proc = runtime.newProc(Block.Type.PROC, block);
         return this;
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject yield(ThreadContext context, IRubyObject[]args) {
         checkInit();
         if (context.runtime.is1_9()) {
             return proc.call19(context, args, Block.NULL_BLOCK);
         } else {
             return proc.call(context, args);
         }
     }
 
     @JRubyMethod(name = "<<", rest = true)
     public IRubyObject op_lshift(ThreadContext context, IRubyObject[]args) {
         yield(context, args);
         return this;
     }
 }
