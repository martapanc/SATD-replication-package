diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index ae4db21276..9c200b3caa 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1,1056 +1,1057 @@
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
 import org.jruby.common.IRubyWarnings.ID;
+import org.jruby.java.addons.ArrayJavaAddons;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.Pack;
 import org.jruby.util.Qsort;
 import org.jruby.util.RecursiveComparator;
 import org.jruby.util.TypeConverter;
 
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
         throw getRuntime().newConcurrencyError("Detected invalid array contents due to unsynchronized modifications with concurrent users");
     }
 
     /** rb_ary_s_create
      * 
      */
     @JRubyMethod(name = "[]", rest = true, frame = true, meta = true)
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
     private RubyArray makeSharedFirst(ThreadContext context, IRubyObject num, boolean last) {
         int n = RubyNumeric.num2int(num);
         
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw context.getRuntime().newArgumentError("negative array size");
         }
         
         return makeShared(last ? realLength - n : 0, n, getMetaClass());
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
         
         return makeShared(last ? realLength - n : 0, n, klass);
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
         int begin = this.begin;
         
         for (int i = begin; i < begin + realLength; i++) {
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
             int begin = this.begin;
             int h = realLength;
             for (int i = begin; i < begin + realLength; i++) {
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
     private final void splice(long beg, long len, IRubyObject rpl) {
         if (len < 0) throw getRuntime().newIndexError("negative length (" + len + ")");
         if (beg < 0 && (beg += realLength) < 0) throw getRuntime().newIndexError("index " + (beg - realLength) + " out of array");
 
         final RubyArray rplArr;
         final int rlen;
 
         if (rpl == null || rpl.isNil()) {
             rplArr = null;
             rlen = 0;
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
         
         splice(pos, 0, inserted); // rb_ary_new4
         
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
@@ -2856,1197 +2857,1209 @@ public class RubyArray extends RubyObject implements List {
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
 
 
     private static void aryReverse(IRubyObject[] _p1, int p1, IRubyObject[] _p2, int p2) {
         while(p1 < p2) {
             IRubyObject tmp = _p1[p1];
             _p1[p1++] = _p2[p2];
             _p2[p2--] = tmp;
         }
     }
 
     private IRubyObject internalRotateBang(ThreadContext context, int cnt) {
         modify();
         
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
         
         return context.getRuntime().getNil();
     }
 
     private static int rotateCount(int cnt, int len) {
         return (cnt < 0) ? (len - (~cnt % len) - 1) : (cnt % len);
     }
 
     private IRubyObject internalRotate(ThreadContext context, int cnt) {
         int len = realLength;
         RubyArray rotated = aryDup();
         rotated.modify();
 
         if(len > 0) {
             cnt = rotateCount(cnt, len);
             IRubyObject[] ptr = this.values;
             IRubyObject[] ptr2 = rotated.values;
             len -= cnt;
             System.arraycopy(ptr, begin + cnt, ptr2, 0, len);
             System.arraycopy(ptr, begin, ptr2, len, cnt);
         }
         
         return rotated;
     }
 
     @JRubyMethod(name = "rotate!", compat = CompatVersion.RUBY1_9)
     public IRubyObject rotate_bang(ThreadContext context) {
         internalRotateBang(context, 1);
         return this;
     }
 
     @JRubyMethod(name = "rotate!", compat = CompatVersion.RUBY1_9)
     public IRubyObject rotate_bang(ThreadContext context, IRubyObject cnt) {
         internalRotateBang(context, RubyNumeric.fix2int(cnt));
         return this;
     }
 
     @JRubyMethod(name = "rotate", compat = CompatVersion.RUBY1_9)
     public IRubyObject rotate(ThreadContext context) {
         return internalRotate(context, 1);
     }
 
     @JRubyMethod(name = "rotate", compat = CompatVersion.RUBY1_9)
     public IRubyObject rotate(ThreadContext context, IRubyObject cnt) {
         return internalRotate(context, RubyNumeric.fix2int(cnt));
     }
 
 
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
 
+    @Override
+    public Object toJava(Class target) {
+        if (target.isArray()) {
+            Class type = target.getComponentType();
+            Object rawJavaArray = Array.newInstance(type, realLength);
+            ArrayJavaAddons.copyDataToJavaArrayDirect(getRuntime().getCurrentContext(), this, rawJavaArray);
+            return rawJavaArray;
+        } else {
+            return super.toJava(target);
+        }
+    }
+
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
diff --git a/src/org/jruby/java/addons/ArrayJavaAddons.java b/src/org/jruby/java/addons/ArrayJavaAddons.java
index 8c24d4899a..71ce810ced 100644
--- a/src/org/jruby/java/addons/ArrayJavaAddons.java
+++ b/src/org/jruby/java/addons/ArrayJavaAddons.java
@@ -1,141 +1,155 @@
 package org.jruby.java.addons;
 
+import java.lang.reflect.Array;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyFixnum;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.javasupport.JavaArray;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class ArrayJavaAddons {
     @JRubyMethod
     public static IRubyObject copy_data(
             ThreadContext context, IRubyObject rubyArray, IRubyObject javaArray,
             IRubyObject fillValue) {
         JavaArray javaArrayJavaObj = (JavaArray)javaArray.dataGetStruct();
         Object fillJavaObject = null;
         int javaLength = (int)javaArrayJavaObj.length().getLongValue();
         Class targetType = javaArrayJavaObj.getComponentType();
         
         if (!fillValue.isNil()) {
             fillJavaObject = fillValue.toJava(targetType);
         }
         
         RubyArray array = null;
         int rubyLength;
         if (rubyArray instanceof RubyArray) {
             array = (RubyArray)rubyArray;
             rubyLength = ((RubyArray)rubyArray).getLength();
         } else {
             rubyLength = 0;
             fillJavaObject = rubyArray.toJava(targetType);
         }
         
         int i = 0;
         for (; i < rubyLength && i < javaLength; i++) {
             javaArrayJavaObj.setWithExceptionHandling(i, array.entry(i).toJava(targetType));
         }
         
         if (i < javaLength && fillJavaObject != null) {
             javaArrayJavaObj.fillWithExceptionHandling(i, javaLength, fillJavaObject);
         }
         
         return javaArray;
     }
     
     @JRubyMethod
     public static IRubyObject copy_data_simple(
             ThreadContext context, IRubyObject from, IRubyObject to) {
         JavaArray javaArray = (JavaArray)to.dataGetStruct();
         RubyArray rubyArray = (RubyArray)from;
         
         copyDataToJavaArray(context, rubyArray, javaArray);
         
         return to;
     }
     
     public static void copyDataToJavaArray(
             ThreadContext context, RubyArray rubyArray, JavaArray javaArray) {
         int javaLength = (int)javaArray.length().getLongValue();
         Class targetType = javaArray.getComponentType();
         
         int rubyLength = rubyArray.getLength();
         
         int i = 0;
         for (; i < rubyLength && i < javaLength; i++) {
             javaArray.setWithExceptionHandling(i, rubyArray.entry(i).toJava(targetType));
         }
     }
+
+    public static void copyDataToJavaArrayDirect(
+            ThreadContext context, RubyArray rubyArray, Object javaArray) {
+        int javaLength = Array.getLength(javaArray);
+        Class targetType = javaArray.getClass().getComponentType();
+
+        int rubyLength = rubyArray.getLength();
+
+        int i = 0;
+        for (; i < rubyLength && i < javaLength; i++) {
+            Array.set(javaArray, i, rubyArray.entry(i).toJava(targetType));
+        }
+    }
     
     public static void copyDataToJavaArray(
             ThreadContext context, RubyArray rubyArray, int src, JavaArray javaArray, int dest, int length) {
         Class targetType = javaArray.getComponentType();
         
         int destLength = (int)javaArray.length().getLongValue();
         int srcLength = rubyArray.getLength();
         
         for (int i = 0; src + i < srcLength && dest + i < destLength && i < length; i++) {
             javaArray.setWithExceptionHandling(dest + i, rubyArray.entry(src + i).toJava(targetType));
         }
     }
     
     @JRubyMethod
     public static IRubyObject dimensions(ThreadContext context, IRubyObject maybeArray) {
         Ruby runtime = context.getRuntime();
         if (!(maybeArray instanceof RubyArray)) {
             return runtime.newEmptyArray();
         }
         RubyArray rubyArray = (RubyArray)maybeArray;
         RubyArray dims = runtime.newEmptyArray();
         
         return dimsRecurse(context, rubyArray, dims, 0);
     }
     
     @JRubyMethod
     public static IRubyObject dimensions(ThreadContext context, IRubyObject maybeArray, IRubyObject dims) {
         Ruby runtime = context.getRuntime();
         if (!(maybeArray instanceof RubyArray)) {
             return runtime.newEmptyArray();
         }
         assert dims instanceof RubyArray;
         
         RubyArray rubyArray = (RubyArray)maybeArray;
         
         return dimsRecurse(context, rubyArray, (RubyArray)dims, 0);
     }
     
     @JRubyMethod
     public static IRubyObject dimensions(ThreadContext context, IRubyObject maybeArray, IRubyObject dims, IRubyObject index) {
         Ruby runtime = context.getRuntime();
         if (!(maybeArray instanceof RubyArray)) {
             return runtime.newEmptyArray();
         }
         assert dims instanceof RubyArray;
         assert index instanceof RubyFixnum;
         
         RubyArray rubyArray = (RubyArray)maybeArray;
         
         return dimsRecurse(context, rubyArray, (RubyArray)dims, (int)((RubyFixnum)index).getLongValue());
     }
     
     private static RubyArray dimsRecurse(ThreadContext context, RubyArray rubyArray, RubyArray dims, int index) {
         Ruby runtime = context.getRuntime();
 
         while (dims.size() <= index) {
             dims.append(RubyFixnum.zero(runtime));
         }
         
         if (rubyArray.size() > ((RubyFixnum)dims.eltInternal(index)).getLongValue()) {
             dims.eltInternalSet(index, RubyFixnum.newFixnum(runtime, rubyArray.size()));
         }
         
         for (int i = 0; i < rubyArray.size(); i++) {
             if (rubyArray.eltInternal(i) instanceof RubyArray) {
                 dimsRecurse(context, (RubyArray)rubyArray.eltInternal(i), dims, 1);
             }
         }
         
         return dims;
     }
 }
