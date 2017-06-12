diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index 578805b80c..c2705ba18d 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1,1803 +1,1803 @@
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
 
 import java.lang.reflect.Array;
 import java.io.IOException;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Set;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.Pack;
 
 /**
  * The implementation of the built-in class Array in Ruby.
  */
 public class RubyArray extends RubyObject implements List {
 
     public static RubyClass createArrayClass(Ruby runtime) {
         RubyClass arrayc = runtime.defineClass("Array", runtime.getObject(), ARRAY_ALLOCATOR);
         arrayc.index = ClassIndex.ARRAY;
         arrayc.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyArray;
                 }
             };
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyArray.class);
 
         arrayc.includeModule(runtime.getModule("Enumerable"));
         arrayc.getMetaClass().defineMethod("[]", callbackFactory.getOptSingletonMethod("create"));
 
         arrayc.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         arrayc.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s")); 
         arrayc.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         arrayc.defineFastMethod("to_a", callbackFactory.getFastMethod("to_a"));
         arrayc.defineFastMethod("to_ary", callbackFactory.getFastMethod("to_ary"));
         arrayc.defineFastMethod("frozen?", callbackFactory.getFastMethod("frozen"));
 
         arrayc.defineFastMethod("==", callbackFactory.getFastMethod("op_equal", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("eql?", callbackFactory.getFastMethod("eql_p", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
 
         arrayc.defineFastMethod("[]", callbackFactory.getFastOptMethod("aref"));
         arrayc.defineFastMethod("[]=", callbackFactory.getFastOptMethod("aset"));
         arrayc.defineFastMethod("at", callbackFactory.getFastMethod("at", RubyKernel.IRUBY_OBJECT));
         arrayc.defineMethod("fetch", callbackFactory.getOptMethod("fetch"));
         arrayc.defineFastMethod("first", callbackFactory.getFastOptMethod("first"));
         arrayc.defineFastMethod("last", callbackFactory.getFastOptMethod("last"));
         arrayc.defineFastMethod("concat", callbackFactory.getFastMethod("concat", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("<<", callbackFactory.getFastMethod("append", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("push", callbackFactory.getFastOptMethod("push_m"));
         arrayc.defineFastMethod("pop", callbackFactory.getFastMethod("pop"));
         arrayc.defineFastMethod("shift", callbackFactory.getFastMethod("shift"));
         arrayc.defineFastMethod("unshift", callbackFactory.getFastOptMethod("unshift_m"));
         arrayc.defineFastMethod("insert", callbackFactory.getFastOptMethod("insert"));
         arrayc.defineMethod("each", callbackFactory.getMethod("each"));
         arrayc.defineMethod("each_index", callbackFactory.getMethod("each_index"));
         arrayc.defineMethod("reverse_each", callbackFactory.getMethod("reverse_each"));
         arrayc.defineFastMethod("length", callbackFactory.getFastMethod("length"));
         arrayc.defineAlias("size", "length");
         arrayc.defineFastMethod("empty?", callbackFactory.getFastMethod("empty_p"));
         arrayc.defineFastMethod("index", callbackFactory.getFastMethod("index", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("rindex", callbackFactory.getFastMethod("rindex", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("indexes", callbackFactory.getFastOptMethod("indexes"));
         arrayc.defineFastMethod("indices", callbackFactory.getFastOptMethod("indexes"));
         arrayc.defineFastMethod("join", callbackFactory.getFastOptMethod("join_m"));
         arrayc.defineFastMethod("reverse", callbackFactory.getFastMethod("reverse"));
         arrayc.defineFastMethod("reverse!", callbackFactory.getFastMethod("reverse_bang"));
         arrayc.defineMethod("sort", callbackFactory.getMethod("sort"));
         arrayc.defineMethod("sort!", callbackFactory.getMethod("sort_bang"));
         arrayc.defineMethod("collect", callbackFactory.getMethod("collect"));
         arrayc.defineMethod("collect!", callbackFactory.getMethod("collect_bang"));
         arrayc.defineMethod("map", callbackFactory.getMethod("collect"));
         arrayc.defineMethod("map!", callbackFactory.getMethod("collect_bang"));
         arrayc.defineMethod("select", callbackFactory.getMethod("select"));
         arrayc.defineFastMethod("values_at", callbackFactory.getFastOptMethod("values_at"));
         arrayc.defineMethod("delete", callbackFactory.getMethod("delete", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("delete_at", callbackFactory.getFastMethod("delete_at", RubyKernel.IRUBY_OBJECT));
         arrayc.defineMethod("delete_if", callbackFactory.getMethod("delete_if"));
         arrayc.defineMethod("reject", callbackFactory.getMethod("reject"));
         arrayc.defineMethod("reject!", callbackFactory.getMethod("reject_bang"));
         arrayc.defineMethod("zip", callbackFactory.getOptMethod("zip"));
         arrayc.defineFastMethod("transpose", callbackFactory.getFastMethod("transpose"));
         arrayc.defineFastMethod("replace", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("clear", callbackFactory.getFastMethod("rb_clear"));
         arrayc.defineMethod("fill", callbackFactory.getOptMethod("fill"));
         arrayc.defineFastMethod("include?", callbackFactory.getFastMethod("include_p", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("<=>", callbackFactory.getFastMethod("op_cmp", RubyKernel.IRUBY_OBJECT));
 
         arrayc.defineFastMethod("slice", callbackFactory.getFastOptMethod("aref"));
         arrayc.defineFastMethod("slice!", callbackFactory.getFastOptMethod("slice_bang"));
 
         arrayc.defineFastMethod("assoc", callbackFactory.getFastMethod("assoc", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("rassoc", callbackFactory.getFastMethod("rassoc", RubyKernel.IRUBY_OBJECT));
 
         arrayc.defineFastMethod("+", callbackFactory.getFastMethod("op_plus", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("*", callbackFactory.getFastMethod("op_times", RubyKernel.IRUBY_OBJECT));
 
         arrayc.defineFastMethod("-", callbackFactory.getFastMethod("op_diff", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("&", callbackFactory.getFastMethod("op_and", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("|", callbackFactory.getFastMethod("op_or", RubyKernel.IRUBY_OBJECT));
 
         arrayc.defineFastMethod("uniq", callbackFactory.getFastMethod("uniq"));
         arrayc.defineFastMethod("uniq!", callbackFactory.getFastMethod("uniq_bang"));
         arrayc.defineFastMethod("compact", callbackFactory.getFastMethod("compact"));
         arrayc.defineFastMethod("compact!", callbackFactory.getFastMethod("compact_bang"));
 
         arrayc.defineFastMethod("flatten", callbackFactory.getFastMethod("flatten"));
         arrayc.defineFastMethod("flatten!", callbackFactory.getFastMethod("flatten_bang"));
 
         arrayc.defineFastMethod("nitems", callbackFactory.getFastMethod("nitems"));
 
         arrayc.defineFastMethod("pack", callbackFactory.getFastMethod("pack", RubyKernel.IRUBY_OBJECT));
         
         arrayc.dispatcher = callbackFactory.createDispatcher(arrayc);
 
         return arrayc;
     }
 
     private static ObjectAllocator ARRAY_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyArray(runtime, klass);
         }
     };
 
     public int getNativeTypeIndex() {
         return ClassIndex.ARRAY;
     }
 
     /** rb_ary_s_create
      * 
      */
     public static IRubyObject create(IRubyObject klass, IRubyObject[] args, Block block) {
         RubyArray arr = (RubyArray) ((RubyClass) klass).allocate();
         arr.callInit(IRubyObject.NULL_ARRAY, block);
     
         if (args.length > 0) {
             arr.alloc(args.length);
             System.arraycopy(args, 0, arr.values, 0, args.length);
             arr.realLength = args.length;
         }
         return arr;
     }
 
     /** rb_ary_new2
      *
      */
     public static final RubyArray newArray(final Ruby runtime, final long len) {
         return new RubyArray(runtime, len);
     }
     public static final RubyArray newArrayLight(final Ruby runtime, final long len) {
         return new RubyArray(runtime, len, false);
     }
 
     /** rb_ary_new
      *
      */
     public static final RubyArray newArray(final Ruby runtime) {
         return new RubyArray(runtime, ARRAY_DEFAULT_SIZE);
     }
 
     /** rb_ary_new
      *
      */
     public static final RubyArray newArrayLight(final Ruby runtime) {
         /* Ruby arrays default to holding 16 elements, so we create an
          * ArrayList of the same size if we're not told otherwise
          */
         RubyArray arr = new RubyArray(runtime, false);
         arr.alloc(ARRAY_DEFAULT_SIZE);
         return arr;
     }
 
     public static RubyArray newArray(Ruby runtime, IRubyObject obj) {
         return new RubyArray(runtime, new IRubyObject[] { obj });
     }
 
     /** rb_assoc_new
      *
      */
     public static RubyArray newArray(Ruby runtime, IRubyObject car, IRubyObject cdr) {
         return new RubyArray(runtime, new IRubyObject[] { car, cdr });
     }
 
     /** rb_ary_new4, rb_ary_new3
      *   
      */
     public static RubyArray newArray(Ruby runtime, IRubyObject[] args) {
         RubyArray arr = new RubyArray(runtime, args.length);
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
     
     public static RubyArray newArrayNoCopyLight(Ruby runtime, IRubyObject[] args) {
         RubyArray arr = new RubyArray(runtime, false);
         arr.values = args;
         arr.realLength = args.length;
         return arr;
     }
 
     public static RubyArray newArray(Ruby runtime, Collection collection) {
         RubyArray arr = new RubyArray(runtime, collection.size());
         collection.toArray(arr.values);
         arr.realLength = arr.values.length;
         return arr;
     }
 
     public static final int ARRAY_DEFAULT_SIZE = 16;    
 
     private IRubyObject[] values;
 
     private static final int TMPLOCK_ARR_F = 1 << 9;
     private static final int SHARED_ARR_F = 1 << 10;
 
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
     private RubyArray(Ruby runtime, IRubyObject[] vals, int begin) {
         super(runtime, runtime.getArray());
         this.values = vals;
         this.begin = begin;
         this.realLength = vals.length - begin;
         flags |= SHARED_ARR_F;
     }
     
     /* rb_ary_new2
      * just allocates the internal array
      */
     private RubyArray(Ruby runtime, long length) {
         super(runtime, runtime.getArray());
         checkLength(length);
         alloc((int) length);
     }
 
     private RubyArray(Ruby runtime, long length, boolean objectspace) {
         super(runtime, runtime.getArray(), objectspace);
         checkLength(length);
         alloc((int)length);
     }     
 
     /* rb_ary_new3, rb_ary_new4
      * allocates the internal array of size length and copies the 'length' elements
      */
     public RubyArray(Ruby runtime, long length, IRubyObject[] vals) {
         super(runtime, runtime.getArray());
         checkLength(length);
         int ilength = (int) length;
         alloc(ilength);
         if (ilength > 0 && vals.length > 0) System.arraycopy(vals, 0, values, 0, ilength);
 
         realLength = ilength;
     }
 
     /* NEWOBJ and OBJSETUP equivalent
      * fastest one, for shared arrays, optional objectspace
      */
     private RubyArray(Ruby runtime, boolean objectSpace) {
         super(runtime, runtime.getArray(), objectSpace);
     }
 
     private RubyArray(Ruby runtime) {
         super(runtime, runtime.getArray());
         alloc(ARRAY_DEFAULT_SIZE);
     }
 
     public RubyArray(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
         alloc(ARRAY_DEFAULT_SIZE);
     }
     
     /* Array constructors taking the MetaClass to fulfil MRI Array subclass behaviour
      * 
      */
     private RubyArray(Ruby runtime, RubyClass klass, int length) {
         super(runtime, klass);
         alloc(length);
     }
     
     private RubyArray(Ruby runtime, RubyClass klass, long length) {
         super(runtime, klass);
         checkLength(length);
         alloc((int)length);
     }
 
     private RubyArray(Ruby runtime, RubyClass klass, long length, boolean objectspace) {
         super(runtime, klass, objectspace);
         checkLength(length);
         alloc((int)length);
     }    
 
     private RubyArray(Ruby runtime, RubyClass klass, boolean objectSpace) {
         super(runtime, klass, objectSpace);
     }
     
     private RubyArray(Ruby runtime, RubyClass klass, RubyArray original) {
         super(runtime, klass);
         realLength = original.realLength;
         alloc(realLength);
         System.arraycopy(original.values, original.begin, values, 0, realLength);
     }
     
     private final IRubyObject[] reserve(int length) {
         return new IRubyObject[length];
     }
 
     private final void alloc(int length) {
         values = new IRubyObject[length];
     }
 
     private final void realloc(int newLength) {
         IRubyObject[] reallocated = new IRubyObject[newLength];
         System.arraycopy(values, 0, reallocated, 0, newLength > realLength ? realLength : newLength);
         values = reallocated;
     }
 
     private final void checkLength(long length) {
         if (length < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
         }
 
         if (length >= Integer.MAX_VALUE) {
             throw getRuntime().newArgumentError("array size too big");
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
         IRubyObject[] copy = reserve(realLength);
         System.arraycopy(values, begin, copy, 0, realLength);
         return copy;
     }
     
     public IRubyObject[] toJavaArrayUnsafe() {
         return (flags & SHARED_ARR_F) == 0 ? values : toJavaArray();
     }    
 
     public IRubyObject[] toJavaArrayMaybeUnsafe() {
         return ((flags & SHARED_ARR_F) == 0 && begin == 0 && values.length == realLength) ? values : toJavaArray();
     }    
 
     /** rb_ary_make_shared
      *
      */
     private final RubyArray makeShared(int beg, int len, RubyClass klass, boolean objectSpace) {
         RubyArray sharedArray = new RubyArray(getRuntime(), klass, objectSpace);
         flags |= SHARED_ARR_F;
         sharedArray.values = values;
         sharedArray.flags |= SHARED_ARR_F;
         sharedArray.begin = beg;
         sharedArray.realLength = len;
         return sharedArray;
     }
 
     /** rb_ary_modify_check
      *
      */
     private final void modifyCheck() {
         testFrozen("array");
 
         if ((flags & TMPLOCK_ARR_F) != 0) {
             throw getRuntime().newTypeError("can't modify array during iteration");
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
         if ((flags & SHARED_ARR_F) != 0) {
             IRubyObject[] vals = reserve(realLength);
             flags &= ~SHARED_ARR_F;
             System.arraycopy(values, begin, vals, 0, realLength);
             begin = 0;            
             values = vals;
         }
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** rb_ary_initialize
      * 
      */
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 0, 2);
         Ruby runtime = getRuntime();
 
         if (argc == 0) {
             realLength = 0;
             if (block.isGiven()) runtime.getWarnings().warn("given block not used");
 
     	    return this;
     	}
 
         if (argc == 1 && !(args[0] instanceof RubyFixnum)) {
             IRubyObject val = args[0].checkArrayType();
             if (!val.isNil()) {
                 replace(val);
                 return this;
             }
         }
 
         long len = RubyNumeric.num2long(args[0]);
 
         if (len < 0) throw runtime.newArgumentError("negative array size");
 
         if (len >= Integer.MAX_VALUE) throw runtime.newArgumentError("array size too big");
 
         int ilen = (int) len;
 
         modify();
 
         if (ilen > values.length) values = reserve(ilen);
 
         if (block.isGiven()) {
             if (argc == 2) {
                 runtime.getWarnings().warn("block supersedes default value argument");
             }
 
             ThreadContext context = runtime.getCurrentContext();
             for (int i = 0; i < ilen; i++) {
                 store(i, block.yield(context, new RubyFixnum(runtime, i)));
                 realLength = i + 1;
             }
         } else {
             Arrays.fill(values, 0, ilen, (argc == 2) ? args[1] : runtime.getNil());
             realLength = ilen;
         }
     	return this;
     }
 
     /** rb_ary_replace
      *
      */
     public IRubyObject replace(IRubyObject orig) {
         modifyCheck();
 
         RubyArray origArr = orig.convertToArray();
 
         if (this == orig) return this;
 
         origArr.flags |= SHARED_ARR_F;
         flags |= SHARED_ARR_F;        
         values = origArr.values;
         realLength = origArr.realLength;
         begin = origArr.begin;
 
 
         return this;
     }
 
     /** rb_ary_to_s
      *
      */
     public IRubyObject to_s() {
         if (realLength == 0) return getRuntime().newString("");
 
         return join(getRuntime().getGlobalVariables().get("$,"));
     }
 
     public boolean includes(IRubyObject item) {
         final ThreadContext context = getRuntime().getCurrentContext();
         int begin = this.begin;
         
         for (int i = begin; i < begin + realLength; i++) {
             if (values[i].equalInternal(context,item ).isTrue()) return true;
     	}
         
         return false;
     }
 
     /** rb_ary_hash
      * 
      */
     public RubyFixnum hash() {
         int h = realLength;
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             h = (h << 1) | (h < 0 ? 1 : 0);
             h ^= RubyNumeric.num2long(values[i].callMethod(context, MethodIndex.HASH, "hash"));
         }
 
         return runtime.newFixnum(h);
     }
 
     /** rb_ary_store
      *
      */
     public final IRubyObject store(long index, IRubyObject value) {
         if (index < 0) {
             index += realLength;
             if (index < 0) {
                 throw getRuntime().newIndexError("index " + (index - realLength) + " out of array");
             }
         }
 
         modify();
 
         if (index >= realLength) {
         if (index >= values.length) {
                 long newLength = values.length >> 1;
 
             if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
 
             newLength += index;
             if (newLength >= Integer.MAX_VALUE) {
                 throw getRuntime().newArgumentError("index too big");
             }
             realloc((int) newLength);
         }
             if(index != realLength) Arrays.fill(values, realLength, (int) index + 1, getRuntime().getNil());
             
             realLength = (int) index + 1;
         }
 
         values[(int) index] = value;
         return value;
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt(long offset) {
         if (realLength == 0 || offset < 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + (int) offset];
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt(int offset) {
         if (realLength == 0 || offset < 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + offset];
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt_f(long offset) {
         if (realLength == 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + (int) offset];
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt_f(int offset) {
         if (realLength == 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + offset];
     }
 
     /** rb_ary_entry
      *
      */
     public final IRubyObject entry(long offset) {
         return (offset < 0 ) ? elt(offset + realLength) : elt_f(offset);
     }
 
 
     /** rb_ary_entry
      *
      */
     public final IRubyObject entry(int offset) {
         return (offset < 0 ) ? elt(offset + realLength) : elt_f(offset);
     }
 
     public final IRubyObject eltInternal(int offset) {
         return values[begin + offset];
     }
     
     public final IRubyObject eltInternalSet(int offset, IRubyObject item) {
         return values[begin + offset] = item;
     }
     
     /** rb_ary_fetch
      *
      */
     public IRubyObject fetch(IRubyObject[] args, Block block) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2 && block.isGiven()) {
             getRuntime().getWarnings().warn("block supersedes default value argument");
         }
 
         long index = RubyNumeric.num2long(args[0]);
 
         if (index < 0) index += realLength;
 
         if (index < 0 || index >= realLength) {
             if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), args[0]);
 
             if (args.length == 1) {
                 throw getRuntime().newIndexError("index " + index + " out of array");
             }
             
             return args[1];
         }
         
         return values[begin + (int) index];
     }
 
     /** rb_ary_to_ary
      * 
      */
     private static RubyArray aryToAry(IRubyObject obj) {
         if (obj instanceof RubyArray) return (RubyArray) obj;
 
         if (obj.respondsTo("to_ary")) return obj.convertToArray();
 
         RubyArray arr = new RubyArray(obj.getRuntime(), false); // possibly should not in object space
         arr.alloc(1);
         arr.values[0] = obj;
         arr.realLength = 1;
         return arr;
     }
 
     /** rb_ary_splice
      * 
      */
     private final void splice(long beg, long len, IRubyObject rpl) {
         int rlen;
 
         if (len < 0) throw getRuntime().newIndexError("negative length (" + len + ")");
 
         if (beg < 0) {
             beg += realLength;
             if (beg < 0) {
                 beg -= realLength;
                 throw getRuntime().newIndexError("index " + beg + " out of array");
             }
         }
         
         if (beg + len > realLength) len = realLength - beg;
 
         RubyArray rplArr;
         if (rpl == null || rpl.isNil()) {
             rplArr = null;
             rlen = 0;
         } else {
             rplArr = aryToAry(rpl);
             rlen = rplArr.realLength;
         }
 
         modify();
 
         if (beg >= realLength) {
             len = beg + rlen;
 
             if (len >= values.length) {
                 int tryNewLength = values.length + (values.length >> 1);
                 
                 realloc(len > tryNewLength ? (int)len : tryNewLength);
             }
 
             Arrays.fill(values, realLength, (int) beg, getRuntime().getNil());
             if (rlen > 0) {
                 System.arraycopy(rplArr.values, rplArr.begin, values, (int) beg, rlen);
             }
             realLength = (int) len;
         } else {
             long alen;
 
             if (beg + len > realLength) len = realLength - beg;
 
             alen = realLength + rlen - len;
             if (alen >= values.length) {
                 int tryNewLength = values.length + (values.length >> 1);
                 
                 realloc(alen > tryNewLength ? (int)alen : tryNewLength);
             }
 
             if (len != rlen) {
                 System.arraycopy(values, (int) (beg + len), values, (int) beg + rlen, realLength - (int) (beg + len));
                 realLength = (int) alen;
             }
 
             if (rlen > 0) {
                 System.arraycopy(rplArr.values, rplArr.begin, values, (int) beg, rlen);
             }
         }
     }
 
     /** rb_ary_insert
      * 
      */
     public IRubyObject insert(IRubyObject[] args) {
         if (args.length == 1) return this;
 
         if (args.length < 1) {
             throw getRuntime().newArgumentError("wrong number of arguments (at least 1)");
         }
 
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
 
     /** rb_ary_dup
      * 
      */
-    private final RubyArray aryDup() {
+    public final RubyArray aryDup() {
         RubyArray dup = new RubyArray(getRuntime(), getMetaClass(), this);
         dup.flags |= flags & TAINTED_F; // from DUP_SETUP
         // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
         return dup;
     }
 
     /** rb_ary_transpose
      * 
      */
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
 
         return result;
     }
 
     /** rb_values_at
      * 
      */
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
         
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), 0);
 
         return makeShared(begin + (int) beg, (int) len, getMetaClass(), true);
     }
 
     /** rb_ary_subseq
      *
      */
     public IRubyObject subseqLight(long beg, long len) {
         if (beg > realLength || beg < 0 || len < 0) return getRuntime().getNil();
 
         if (beg + len > realLength) {
             len = realLength - beg;
             
             if (len < 0) len = 0;
         }
         
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), 0, false);
 
         return makeShared(begin + (int) beg, (int) len, getMetaClass(), false);
     }
 
     /** rb_ary_length
      *
      */
     public RubyFixnum length() {
         return getRuntime().newFixnum(realLength);
     }
 
     /** rb_ary_push - specialized rb_ary_store 
      *
      */
     public RubyArray append(IRubyObject item) {
         modify();
         
         if (realLength == values.length) {
         if (realLength == Integer.MAX_VALUE) throw getRuntime().newArgumentError("index too big");
             
             long newLength = values.length + (values.length >> 1);
             if ( newLength > Integer.MAX_VALUE ) {
                 newLength = Integer.MAX_VALUE;
             }else if ( newLength < ARRAY_DEFAULT_SIZE ) {
                 newLength = ARRAY_DEFAULT_SIZE;
             }
 
             realloc((int) newLength);
         }
         
         values[realLength++] = item;
         return this;
     }
 
     /** rb_ary_push_m
      *
      */
     public RubyArray push_m(IRubyObject[] items) {
         for (int i = 0; i < items.length; i++) {
             append(items[i]);
         }
         
         return this;
     }
 
     /** rb_ary_pop
      *
      */
     public IRubyObject pop() {
         modifyCheck();
         
         if (realLength == 0) return getRuntime().getNil();
 
         if ((flags & SHARED_ARR_F) == 0) {
             int index = begin + --realLength;
             IRubyObject obj = values[index];
             values[index] = null;
             return obj;
         } 
 
         return values[begin + --realLength];
     }
 
     /** rb_ary_shift
      *
      */
     public IRubyObject shift() {
         modifyCheck();
 
         if (realLength == 0) return getRuntime().getNil();
 
         IRubyObject obj = values[begin];
 
         flags |= SHARED_ARR_F;
 
         begin++;
         realLength--;
 
         return obj;
     }
 
     /** rb_ary_unshift
      *
      */
     public RubyArray unshift(IRubyObject item) {
         modify();
 
         if (realLength == values.length) {
             int newLength = values.length >> 1;
             if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
 
             newLength += values.length;
             realloc(newLength);
         }
         System.arraycopy(values, 0, values, 1, realLength);
 
         realLength++;
         values[0] = item;
 
         return this;
     }
 
     /** rb_ary_unshift_m
      *
      */
     public RubyArray unshift_m(IRubyObject[] items) {
         long len = realLength;
 
         if (items.length == 0) return this;
 
         store(len + items.length - 1, getRuntime().getNil());
 
         // it's safe to use zeroes here since modified by store()
         System.arraycopy(values, 0, values, items.length, (int) len);
         System.arraycopy(items, 0, values, 0, items.length);
         
         return this;
     }
 
     /** rb_ary_includes
      * 
      */
     public RubyBoolean include_p(IRubyObject item) {
         return getRuntime().newBoolean(includes(item));
     }
 
     /** rb_ary_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen() || (flags & TMPLOCK_ARR_F) != 0);
     }
 
     /** rb_ary_aref
      */
     public IRubyObject aref(IRubyObject[] args) {
         long beg, len;
 
         if(args.length == 1) {
             if (args[0] instanceof RubyFixnum) return entry(((RubyFixnum)args[0]).getLongValue());
             if (args[0] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
             
             long[] beglen;
             if (!(args[0] instanceof RubyRange)) {
             } else if ((beglen = ((RubyRange) args[0]).begLen(realLength, 0)) == null) {
                 return getRuntime().getNil();
             } else {
                 beg = beglen[0];
                 len = beglen[1];
                 return subseq(beg, len);
             }
 
             return entry(RubyNumeric.num2long(args[0]));            
         }        
 
         if (args.length == 2) {
             if (args[0] instanceof RubySymbol) {
                 throw getRuntime().newTypeError("Symbol as array index");
             }
             beg = RubyNumeric.num2long(args[0]);
             len = RubyNumeric.num2long(args[1]);
 
             if (beg < 0) beg += realLength;
 
             return subseq(beg, len);
         }
 
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         return null;
         }
 
     /** rb_ary_aset
      *
      */
     public IRubyObject aset(IRubyObject[] args) {
         if (args.length == 2) {
         if (args[0] instanceof RubyFixnum) {
                 store(((RubyFixnum)args[0]).getLongValue(), args[1]);
             return args[1];
         }
         if (args[0] instanceof RubyRange) {
             long[] beglen = ((RubyRange) args[0]).begLen(realLength, 1);
             splice(beglen[0], beglen[1], args[1]);
             return args[1];
         }
             if (args[0] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
 
         store(RubyNumeric.num2long(args[0]), args[1]);
         return args[1];
     }
 
         if (args.length == 3) {
             if (args[0] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
             if (args[1] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as subarray length");
 
             splice(RubyNumeric.num2long(args[0]), RubyNumeric.num2long(args[1]), args[2]);
             return args[2];
         }
 
         throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 2)");
     }
 
     /** rb_ary_at
      *
      */
     public IRubyObject at(IRubyObject pos) {
         return entry(RubyNumeric.num2long(pos));
     }
 
 	/** rb_ary_concat
      *
      */
     public RubyArray concat(IRubyObject obj) {
         RubyArray ary = obj.convertToArray();
         
         if (ary.realLength > 0) splice(realLength, 0, ary);
 
         return this;
     }
 
     /** inspect_ary
      * 
      */
     private IRubyObject inspectAry() {
         StringBuffer buffer = new StringBuffer("[");
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         boolean tainted = isTaint();
 
         for (int i = 0; i < realLength; i++) {
             RubyString s = RubyString.objAsString(values[begin + i].callMethod(context, "inspect"));
 
             if (s.isTaint()) tainted = true;
 
             if (i > 0) buffer.append(", ");
 
             buffer.append(s.toString());
         }
         buffer.append("]");
 
         RubyString str = runtime.newString(buffer.toString());
         if (tainted) str.setTaint(true);
 
         return str;
     }
 
     /** rb_ary_inspect
     *
     */    
     public IRubyObject inspect() {
         if (realLength == 0) return getRuntime().newString("[]");
         if (getRuntime().isInspecting(this)) return  getRuntime().newString("[...]");
 
         try {
             getRuntime().registerInspecting(this);
             return inspectAry();
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_ary_first
      *
      */
     public IRubyObject first(IRubyObject[] args) {
     	if (args.length == 0) {
             if (realLength == 0) return getRuntime().getNil();
 
             return values[begin];
         } 
             
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         long n = RubyNumeric.num2long(args[0]);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
     	}
     	
         return makeShared(begin, (int) n, getRuntime().getArray(), true);
     }
 
     /** rb_ary_last
      *
      */
     public IRubyObject last(IRubyObject[] args) {
         if (args.length == 0) {
             if (realLength == 0) return getRuntime().getNil();
 
             return values[begin + realLength - 1];
         } 
             
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         long n = RubyNumeric.num2long(args[0]);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
         }
 
         return makeShared(begin + realLength - (int) n, (int) n, getRuntime().getArray(), true);
     }
 
     /** rb_ary_each
      *
      */
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         if ((flags & SHARED_ARR_F) != 0) {
             for (int i = begin; i < begin + realLength; i++) {
                 block.yield(context, values[i]);
             }
         } else {
             for (int i = 0; i < realLength; i++) {
                 block.yield(context, values[i]);
             }
         }
         return this;
     }
 
     /** rb_ary_each_index
      *
      */
     public IRubyObject each_index(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < realLength; i++) {
             block.yield(context, runtime.newFixnum(i));
         }
         return this;
     }
 
     /** rb_ary_reverse_each
      *
      */
     public IRubyObject reverse_each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         int len = realLength;
         
         while(len-- > 0) {
             block.yield(context, values[begin + len]);
             
             if (realLength < len) len = realLength;
         }
         
         return this;
     }
 
     private IRubyObject inspectJoin(RubyArray tmp, IRubyObject sep) {
         try {
             getRuntime().registerInspecting(this);
             return tmp.join(sep);
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_ary_join
      *
      */
     public RubyString join(IRubyObject sep) {
         if (realLength == 0) return getRuntime().newString("");
 
         boolean taint = isTaint() || sep.isTaint();
 
         long len = 1;
         for (int i = begin; i < begin + realLength; i++) {            
             IRubyObject tmp = values[i].checkStringType();
             len += tmp.isNil() ? 10 : ((RubyString) tmp).getByteList().length();
         }
 
         RubyString strSep = null;
         if (!sep.isNil()) {
             sep = strSep = sep.convertToString();
             len += strSep.getByteList().length() * (realLength - 1);
         }
 
         ByteList buf = new ByteList((int)len);
         Ruby runtime = getRuntime();
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject tmp = values[i];
             if (tmp instanceof RubyString) {
                 // do nothing
             } else if (tmp instanceof RubyArray) {
                 if (runtime.isInspecting(tmp)) {
                     tmp = runtime.newString("[...]");
                 } else {
                     tmp = inspectJoin((RubyArray)tmp, sep);
                 }
             } else {
                 tmp = RubyString.objAsString(tmp);
             }
 
             if (i > begin && !sep.isNil()) buf.append(strSep.getByteList());
 
             buf.append(tmp.asString().getByteList());
             if (tmp.isTaint()) taint = true;
         }
 
         RubyString result = runtime.newString(buf); 
 
         if (taint) result.setTaint(true);
 
         return result;
     }
 
     /** rb_ary_join_m
      *
      */
     public RubyString join_m(IRubyObject[] args) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         IRubyObject sep = (argc == 1) ? args[0] : getRuntime().getGlobalVariables().get("$,");
         
         return join(sep);
     }
 
     /** rb_ary_to_a
      *
      */
     public RubyArray to_a() {
         if(getMetaClass() != getRuntime().getArray()) {
             RubyArray dup = new RubyArray(getRuntime(), true);
 
             flags |= SHARED_ARR_F;
             dup.flags |= SHARED_ARR_F;
             dup.values = values;
             dup.realLength = realLength; 
             dup.begin = begin;
             
             return dup;
         }        
         return this;
     }
 
     public IRubyObject to_ary() {
     	return this;
     }
 
     public RubyArray convertToArray() {
         return this;
     }
     
     public IRubyObject checkArrayType(){
         return this;
     }
 
     /** rb_ary_equal
      *
      */
     public IRubyObject op_equal(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
 
         if (!(obj instanceof RubyArray)) {
             if (!obj.respondsTo("to_ary")) {
                 return getRuntime().getFalse();
             } else {
                 return obj.equalInternal(getRuntime().getCurrentContext(), this);
             }
         }
 
         RubyArray ary = (RubyArray) obj;
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (long i = 0; i < realLength; i++) {
             if (!elt(i).equalInternal(context, ary.elt(i)).isTrue()) return runtime.getFalse();
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_eql
      *
      */
     public RubyBoolean eql_p(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
         if (!(obj instanceof RubyArray)) return getRuntime().getFalse();
 
         RubyArray ary = (RubyArray) obj;
 
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < realLength; i++) {
             if (!elt(i).eqlInternal(context, ary.elt(i))) return runtime.getFalse();
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_compact_bang
      *
      */
     public IRubyObject compact_bang() {
         modify();
 
         int p = 0;
         int t = 0;
         int end = p + realLength;
 
         while (t < end) {
             if (values[t].isNil()) {
                 t++;
             } else {
                 values[p++] = values[t++];
             }
         }
 
         if (realLength == p) return getRuntime().getNil();
 
         realloc(p);
         realLength = p;
         return this;
     }
 
     /** rb_ary_compact
      *
      */
     public IRubyObject compact() {
         RubyArray ary = aryDup();
         ary.compact_bang();
         return ary;
     }
 
     /** rb_ary_empty_p
      *
      */
     public IRubyObject empty_p() {
         return realLength == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_ary_clear
      *
      */
     public IRubyObject rb_clear() {
         modifyCheck();
 
         if((flags & SHARED_ARR_F) != 0){
             alloc(ARRAY_DEFAULT_SIZE);
             flags |= SHARED_ARR_F;
         } else if (values.length > ARRAY_DEFAULT_SIZE << 1){
             alloc(ARRAY_DEFAULT_SIZE << 1);
         }
 
         begin = 0;
         realLength = 0;
         return this;
     }
 
     /** rb_ary_fill
      *
      */
     public IRubyObject fill(IRubyObject[] args, Block block) {
         IRubyObject item = null;
         IRubyObject begObj = null;
         IRubyObject lenObj = null;
         int argc = args.length;
 
         if (block.isGiven()) {
             Arity.checkArgumentCount(getRuntime(), args, 0, 2);
             item = null;
         	begObj = argc > 0 ? args[0] : null;
         	lenObj = argc > 1 ? args[1] : null;
         	argc++;
         } else {
             Arity.checkArgumentCount(getRuntime(), args, 1, 3);
             item = args[0];
         	begObj = argc > 1 ? args[1] : null;
         	lenObj = argc > 2 ? args[2] : null;
         }
 
         long beg = 0, end = 0, len = 0;
         switch (argc) {
         case 1:
             beg = 0;
             len = realLength;
             break;
         case 2:
             if (begObj instanceof RubyRange) {
                 long[] beglen = ((RubyRange) begObj).begLen(realLength, 1);
                 beg = (int) beglen[0];
                 len = (int) beglen[1];
                 break;
             }
             /* fall through */
         case 3:
             beg = begObj.isNil() ? 0 : RubyNumeric.num2long(begObj);
             if (beg < 0) {
                 beg = realLength + beg;
                 if (beg < 0) beg = 0;
             }
             len = (lenObj == null || lenObj.isNil()) ? realLength - beg : RubyNumeric.num2long(lenObj);
             break;
         }
 
         modify();
 
         end = beg + len;
         if (end > realLength) {
             if (end >= values.length) realloc((int) end);
 
             Arrays.fill(values, realLength, (int) end, getRuntime().getNil());
             realLength = (int) end;
         }
 
         if (block.isGiven()) {
             Ruby runtime = getRuntime();
             ThreadContext context = runtime.getCurrentContext();
             for (int i = (int) beg; i < (int) end; i++) {
                 IRubyObject v = block.yield(context, runtime.newFixnum(i));
                 if (i >= realLength) break;
 
                 values[i] = v;
             }
         } else {
             if(len > 0) Arrays.fill(values, (int) beg, (int) (beg + len), item);
         }
         
         return this;
     }
 
     /** rb_ary_index
      *
      */
     public IRubyObject index(IRubyObject obj) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i = begin; i < begin + realLength; i++) {
             if (values[i].equalInternal(context, obj).isTrue()) return runtime.newFixnum(i - begin);
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_rindex
      *
      */
     public IRubyObject rindex(IRubyObject obj) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         int i = realLength;
 
         while (i-- > 0) {
             if (i > realLength) {
                 i = realLength;
                 continue;
             }
             if (values[begin + i].equalInternal(context, obj).isTrue()) {
                 return getRuntime().newFixnum(i);
             }
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_indexes
      * 
      */
     public IRubyObject indexes(IRubyObject[] args) {
         getRuntime().getWarnings().warn("Array#indexes is deprecated; use Array#values_at");
 
         RubyArray ary = new RubyArray(getRuntime(), args.length);
 
         IRubyObject[] arefArgs = new IRubyObject[1];
         for (int i = 0; i < args.length; i++) {
             arefArgs[0] = args[i];
             ary.append(aref(arefArgs));
         }
 
         return ary;
     }
 
     /** rb_ary_reverse_bang
      *
      */
     public IRubyObject reverse_bang() {
         modify();
 
         IRubyObject tmp;
         if (realLength > 1) {
             int p1 = 0;
             int p2 = p1 + realLength - 1;
 
             while (p1 < p2) {
                 tmp = values[p1];
                 values[p1++] = values[p2];
                 values[p2--] = tmp;
             }
         }
         return this;
     }
 
     /** rb_ary_reverse_m
      *
      */
     public IRubyObject reverse() {
         return aryDup().reverse_bang();
     }
 
     /** rb_ary_collect
      *
      */
     public RubyArray collect(Block block) {
         Ruby runtime = getRuntime();
         
         if (!block.isGiven()) return new RubyArray(getRuntime(), runtime.getArray(), this);
         
         ThreadContext context = runtime.getCurrentContext();
         RubyArray collect = new RubyArray(runtime, realLength);
         
         for (int i = begin; i < begin + realLength; i++) {
             collect.append(block.yield(context, values[i]));
         }
         
         return collect;
     }
 
     /** rb_ary_collect_bang
      *
      */
     public RubyArray collect_bang(Block block) {
         modify();
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0, len = realLength; i < len; i++) {
             store(i, block.yield(context, values[begin + i]));
         }
         return this;
     }
 
     /** rb_ary_select
      *
      */
     public RubyArray select(Block block) {
         Ruby runtime = getRuntime();
         RubyArray result = new RubyArray(runtime, realLength);
 
         ThreadContext context = runtime.getCurrentContext();
         if ((flags & SHARED_ARR_F) != 0) {
             for (int i = begin; i < begin + realLength; i++) {
                 if (block.yield(context, values[i]).isTrue()) result.append(elt(i - begin));
             }
         } else {
             for (int i = 0; i < realLength; i++) {
                 if (block.yield(context, values[i]).isTrue()) result.append(elt(i));
             }
         }
         return result;
     }
 
     /** rb_ary_delete
      *
      */
     public IRubyObject delete(IRubyObject item, Block block) {
         int i2 = 0;
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i1 = 0; i1 < realLength; i1++) {
             IRubyObject e = values[begin + i1];
             if (e.equalInternal(context, item).isTrue()) continue;
             if (i1 != i2) store(i2, e);
             i2++;
         }
         
         if (realLength == i2) {
             if (block.isGiven()) return block.yield(context, item);
 
             return runtime.getNil();
         }
 
         modify();
 
         if (realLength > i2) {
             realLength = i2;
             if (i2 << 1 < values.length && values.length > ARRAY_DEFAULT_SIZE) realloc(i2 << 1);
         }
         return item;
     }
 
     /** rb_ary_delete_at
      *
      */
     private final IRubyObject delete_at(int pos) {
         int len = realLength;
 
         if (pos >= len) return getRuntime().getNil();
 
         if (pos < 0) pos += len;
 
         if (pos < 0) return getRuntime().getNil();
 
         modify();
 
         IRubyObject obj = values[pos];
         System.arraycopy(values, pos + 1, values, pos, len - (pos + 1));
 
         realLength--;
 
         return obj;
     }
 
     /** rb_ary_delete_at_m
      * 
      */
     public IRubyObject delete_at(IRubyObject obj) {
         return delete_at((int) RubyNumeric.num2long(obj));
     }
 
     /** rb_ary_reject_bang
      * 
      */
     public IRubyObject reject(Block block) {
         RubyArray ary = aryDup();
         ary.reject_bang(block);
         return ary;
     }
 
     /** rb_ary_reject_bang
      *
      */
     public IRubyObject reject_bang(Block block) {
         int i2 = 0;
         modify();
 
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i1 = 0; i1 < realLength; i1++) {
             IRubyObject v = values[i1];
             if (block.yield(context, v).isTrue()) continue;
 
             if (i1 != i2) store(i2, v);
             i2++;
         }
         if (realLength == i2) return getRuntime().getNil();
 
         if (i2 < realLength) realLength = i2;
 
         return this;
     }
 
     /** rb_ary_delete_if
      *
      */
     public IRubyObject delete_if(Block block) {
         reject_bang(block);
         return this;
     }
 
     /** rb_ary_zip
      * 
      */
     public IRubyObject zip(IRubyObject[] args, Block block) {
         for (int i = 0; i < args.length; i++) {
             args[i] = args[i].convertToArray();
         }
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
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
     public IRubyObject op_cmp(IRubyObject obj) {
         RubyArray ary2 = obj.convertToArray();
 
         int len = realLength;
 
         if (len > ary2.realLength) len = ary2.realLength;
 
         Ruby runtime = getRuntime();
diff --git a/src/org/jruby/RubyEnumerable.java b/src/org/jruby/RubyEnumerable.java
index 6604bf618a..b6ca66c40a 100644
--- a/src/org/jruby/RubyEnumerable.java
+++ b/src/org/jruby/RubyEnumerable.java
@@ -1,561 +1,555 @@
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
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 
 import java.util.Comparator;
 import java.util.Arrays;
 
 import org.jruby.exceptions.JumpException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallBlock;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * The implementation of Ruby's Enumerable module.
  */
 public class RubyEnumerable {
 
     public static RubyModule createEnumerableModule(Ruby runtime) {
         RubyModule enm = runtime.defineModule("Enumerable");
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyEnumerable.class);
 
         enm.defineFastMethod("to_a", callbackFactory.getFastSingletonMethod("to_a"));
         enm.defineFastMethod("entries", callbackFactory.getFastSingletonMethod("to_a"));
         enm.defineMethod("sort", callbackFactory.getSingletonMethod("sort"));
         enm.defineMethod("sort_by", callbackFactory.getSingletonMethod("sort_by"));
         enm.defineMethod("grep", callbackFactory.getSingletonMethod("grep", IRubyObject.class));
         enm.defineMethod("detect", callbackFactory.getOptSingletonMethod("detect"));
         enm.defineMethod("find", callbackFactory.getOptSingletonMethod("detect"));
         enm.defineMethod("select", callbackFactory.getSingletonMethod("select"));
         enm.defineMethod("find_all", callbackFactory.getSingletonMethod("select"));
         enm.defineMethod("reject", callbackFactory.getSingletonMethod("reject"));
         enm.defineMethod("collect", callbackFactory.getSingletonMethod("collect"));
         enm.defineMethod("map", callbackFactory.getSingletonMethod("collect"));
         enm.defineMethod("inject", callbackFactory.getOptSingletonMethod("inject"));
         enm.defineMethod("partition", callbackFactory.getSingletonMethod("partition"));
         enm.defineMethod("each_with_index", callbackFactory.getSingletonMethod("each_with_index"));
         enm.defineFastMethod("include?", callbackFactory.getFastSingletonMethod("include_p", IRubyObject.class));
         enm.defineFastMethod("member?", callbackFactory.getFastSingletonMethod("include_p", IRubyObject.class));
         enm.defineMethod("max", callbackFactory.getSingletonMethod("max"));
         enm.defineMethod("min", callbackFactory.getSingletonMethod("min"));
         enm.defineMethod("all?", callbackFactory.getSingletonMethod("all_p"));
         enm.defineMethod("any?", callbackFactory.getSingletonMethod("any_p"));
         enm.defineMethod("zip", callbackFactory.getOptSingletonMethod("zip"));
         enm.defineMethod("group_by", callbackFactory.getSingletonMethod("group_by"));
 
         return enm;
     }
-    
-    public static IRubyObject callEachOld(ThreadContext context, IRubyObject self,
-            RubyModule module, BlockCallback bc) {
-        return self.callMethod(context, "each", new CallBlock(self, module, Arity.noArguments(),
-                bc, context));
-    }
 
     public static IRubyObject callEach(Ruby runtime, ThreadContext context, IRubyObject self,
             BlockCallback callback) {
         return self.callMethod(context, "each", new CallBlock(self, runtime.getEnumerable(), 
                 Arity.noArguments(), callback, context));
     }
 
     public static IRubyObject to_a(IRubyObject self) {
         Ruby runtime = self.getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         RubyArray result = runtime.newArray();
 
         callEach(runtime, context, self, new AppendBlockCallback(runtime, result));
 
         return result;
     }
 
     public static IRubyObject sort(IRubyObject self, final Block block) {
         Ruby runtime = self.getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         RubyArray result = runtime.newArray();
 
         callEach(runtime, context, self, new AppendBlockCallback(runtime, result));
         result.sort_bang(block);
         
         return result;
     }
 
     public static IRubyObject sort_by(IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         if (self instanceof RubyArray) {
             RubyArray selfArray = (RubyArray) self;
             final IRubyObject[][] valuesAndCriteria = new IRubyObject[selfArray.size()][2];
 
             callEach(runtime, context, self, new BlockCallback() {
                 int i = 0;
 
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     valuesAndCriteria[i][0] = largs[0];
                     valuesAndCriteria[i++][1] = block.yield(context, largs[0]);
                     return runtime.getNil();
                 }
             });
             
             Arrays.sort(valuesAndCriteria, new Comparator() {
                 public int compare(Object o1, Object o2) {
                     IRubyObject ro1 = ((IRubyObject[]) o1)[1];
                     IRubyObject ro2 = ((IRubyObject[]) o2)[1];
                     return RubyFixnum.fix2int(ro1.callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", ro2));
                 }
             });
             
             IRubyObject dstArray[] = new IRubyObject[selfArray.size()];
             for (int i = 0; i < dstArray.length; i++) {
                 dstArray[i] = valuesAndCriteria[i][0];
             }
 
             return runtime.newArrayNoCopy(dstArray);
         } else {
             final RubyArray result = runtime.newArray();
             callEach(runtime, context, self, new AppendBlockCallback(runtime, result));
             
             final IRubyObject[][] valuesAndCriteria = new IRubyObject[result.size()][2];
             for (int i = 0; i < valuesAndCriteria.length; i++) {
                 IRubyObject val = result.eltInternal(i);
                 valuesAndCriteria[i][0] = val;
                 valuesAndCriteria[i][1] = block.yield(context, val);
             }
             
             Arrays.sort(valuesAndCriteria, new Comparator() {
                 public int compare(Object o1, Object o2) {
                     IRubyObject ro1 = ((IRubyObject[]) o1)[1];
                     IRubyObject ro2 = ((IRubyObject[]) o2)[1];
                     return RubyFixnum.fix2int(ro1.callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", ro2));
                 }
             });
             
             for (int i = 0; i < valuesAndCriteria.length; i++) {
                 result.eltInternalSet(i, valuesAndCriteria[i][0]);
             }
 
             return result;
         }
     }
 
     public static IRubyObject grep(IRubyObject self, final IRubyObject pattern, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         final RubyArray result = runtime.newArray();
 
         if (block.isGiven()) {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (pattern.callMethod(context, MethodIndex.OP_EQQ, "===", largs[0]).isTrue()) {
                         result.append(block.yield(context, largs[0]));
                     }
                     return runtime.getNil();
                 }
             });
         } else {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (pattern.callMethod(context, MethodIndex.OP_EQQ, "===", largs[0]).isTrue()) {
                         result.append(largs[0]);
                     }
                     return runtime.getNil();
                 }
             });
         }
         
         return result;
     }
 
     public static IRubyObject detect(IRubyObject self, IRubyObject[] args, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         final IRubyObject result[] = new IRubyObject[] { null };
         IRubyObject ifnone = null;
 
         if (Arity.checkArgumentCount(runtime, args, 0, 1) == 1) ifnone = args[0];
 
         try {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (block.yield(context, largs[0]).isTrue()) {
                         result[0] = largs[0];
                         throw JumpException.SPECIAL_JUMP;
                     }
                     return runtime.getNil();
                 }
             });
         } catch (JumpException.SpecialJump sj) {
             return result[0];
         }
 
         return ifnone != null ? ifnone.callMethod(context, "call") : runtime.getNil();
     }
 
     public static IRubyObject select(IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         final RubyArray result = runtime.newArray();
 
         callEach(runtime, context, self, new BlockCallback() {
             public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                 if (block.yield(context, largs[0]).isTrue()) result.append(largs[0]);
                 return runtime.getNil();
             }
         });
 
         return result;
     }
 
     public static IRubyObject reject(IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         final RubyArray result = runtime.newArray();
 
         callEach(runtime, context, self, new BlockCallback() {
             public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                 if (!block.yield(context, largs[0]).isTrue()) result.append(largs[0]);
                 return runtime.getNil();
             }
         });
 
         return result;
     }
 
     public static IRubyObject collect(IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         final RubyArray result = runtime.newArray();
 
         if (block.isGiven()) {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     result.append(block.yield(context, largs[0]));
                     return runtime.getNil();
                 }
             });
         } else {
             callEach(runtime, context, self, new AppendBlockCallback(runtime, result));
         }
         return result;
     }
 
     public static IRubyObject inject(IRubyObject self, IRubyObject[] args, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         final IRubyObject result[] = new IRubyObject[] { null };
 
         if (Arity.checkArgumentCount(runtime, args, 0, 1) == 1) result[0] = args[0];
 
         callEach(runtime, context, self, new BlockCallback() {
             public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                 result[0] = result[0] == null ? 
                         largs[0] : block.yield(context, runtime.newArray(result[0], largs[0]));
 
                 return runtime.getNil();
             }
         });
 
         return result[0] == null ? runtime.getNil() : result[0];
     }
 
     public static IRubyObject partition(IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         final RubyArray arr_true = runtime.newArray();
         final RubyArray arr_false = runtime.newArray();
 
         callEach(runtime, context, self, new BlockCallback() {
             public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                 if (block.yield(context, largs[0]).isTrue()) {
                     arr_true.append(largs[0]);
                 } else {
                     arr_false.append(largs[0]);
                 }
 
                 return runtime.getNil();
             }
         });
 
         return runtime.newArray(arr_true, arr_false);
     }
 
     private static class EachWithIndex implements BlockCallback {
         private int index = 0;
         private final Block block;
         private final Ruby runtime;
 
         public EachWithIndex(ThreadContext ctx, Block block) {
             this.block = block;
             this.runtime = ctx.getRuntime();
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject[] iargs, Block block) {
             this.block.yield(context, runtime.newArray(iargs[0], runtime.newFixnum(index++)));
             return runtime.getNil();            
         }
     }
 
     public static IRubyObject each_with_index(IRubyObject self, Block block) {
         ThreadContext context = self.getRuntime().getCurrentContext();
         self.callMethod(context, "each", new CallBlock(self, self.getRuntime().getModule("Enumerable"), 
                 Arity.noArguments(), new EachWithIndex(context, block), context));
         
         return self;
     }
 
     public static IRubyObject include_p(IRubyObject self, final IRubyObject arg) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         try {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (arg.equalInternal(context, largs[0]).isTrue()) {
                         throw JumpException.SPECIAL_JUMP;
                     }
                     return runtime.getNil();
                 }
             });
         } catch (JumpException.SpecialJump sj) {
             return runtime.getTrue();
         }
         
         return runtime.getFalse();
     }
 
     public static IRubyObject max(IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         final IRubyObject result[] = new IRubyObject[] { null };
 
         if (block.isGiven()) {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (result[0] == null || RubyComparable.cmpint(block.yield(context, 
                             runtime.newArray(largs[0], result[0])), largs[0], result[0]) > 0) {
                         result[0] = largs[0];
                     }
                     return runtime.getNil();
                 }
             });
         } else {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (result[0] == null || RubyComparable.cmpint(largs[0].callMethod(context,
                             MethodIndex.OP_SPACESHIP, "<=>", result[0]), largs[0], result[0]) > 0) {
                         result[0] = largs[0];
                     }
                     return runtime.getNil();
                 }
             });
         }
         
         return result[0] == null ? runtime.getNil() : result[0];
     }
 
     public static IRubyObject min(IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         final IRubyObject result[] = new IRubyObject[] { null };
 
         if (block.isGiven()) {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (result[0] == null || RubyComparable.cmpint(block.yield(context, 
                             runtime.newArray(largs[0], result[0])), largs[0], result[0]) < 0) {
                         result[0] = largs[0];
                     }
                     return runtime.getNil();
                 }
             });
         } else {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (result[0] == null || RubyComparable.cmpint(largs[0].callMethod(context,
                             MethodIndex.OP_SPACESHIP, "<=>", result[0]), largs[0], result[0]) < 0) {
                         result[0] = largs[0];
                     }
                     return runtime.getNil();
                 }
             });
         }
         
         return result[0] == null ? runtime.getNil() : result[0];
     }
 
     public static IRubyObject all_p(IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         try {
             if (block.isGiven()) {
                 callEach(runtime, context, self, new BlockCallback() {
                     public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                         if (!block.yield(context, largs[0]).isTrue()) {
                             throw JumpException.SPECIAL_JUMP;
                         }
                         return runtime.getNil();
                     }
                 });
             } else {
                 callEach(runtime, context, self, new BlockCallback() {
                     public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                         if (!largs[0].isTrue()) {
                             throw JumpException.SPECIAL_JUMP;
                         }
                         return runtime.getNil();
                     }
                 });
             }
         } catch (JumpException.SpecialJump sj) {
             return runtime.getFalse();
         }
 
         return runtime.getTrue();
     }
 
     public static IRubyObject any_p(IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         try {
             if (block.isGiven()) {
                 callEach(runtime, context, self, new BlockCallback() {
                     public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                         if (block.yield(context, largs[0]).isTrue()) {
                             throw JumpException.SPECIAL_JUMP;
                         }
                         return runtime.getNil();
                     }
                 });
             } else {
                 callEach(runtime, context, self, new BlockCallback() {
                     public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                         if (largs[0].isTrue()) {
                             throw JumpException.SPECIAL_JUMP;
                         }
                         return runtime.getNil();
                     }
                 });
             }
         } catch (JumpException.SpecialJump sj) {
             return runtime.getTrue();
         }
 
         return runtime.getFalse();
     }
 
     public static IRubyObject zip(IRubyObject self, final IRubyObject[] args, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         for (int i = 0; i < args.length; i++) {
             args[i] = args[i].convertToType(runtime.getArray(), MethodIndex.TO_A, "to_a");
         }
         
         final int aLen = args.length + 1;
 
         if (block.isGiven()) {
             callEach(runtime, context, self, new BlockCallback() {
                 int ix = 0;
 
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     RubyArray array = runtime.newArray(aLen);
                     array.append(largs[0]);
                     for (int i = 0, j = args.length; i < j; i++) {
                         array.append(((RubyArray) args[i]).entry(ix));
                     }
                     block.yield(context, array);
                     ix++;
                     return runtime.getNil();
                 }
             });
             return runtime.getNil();
         } else {
             final RubyArray zip = runtime.newArray();
             callEach(runtime, context, self, new BlockCallback() {
                 int ix = 0;
 
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     RubyArray array = runtime.newArray(aLen);
                     array.append(largs[0]);
                     for (int i = 0, j = args.length; i < j; i++) {
                         array.append(((RubyArray) args[i]).entry(ix));
                     }
                     zip.append(array);
                     ix++;
                     return runtime.getNil();
                 }
             });
             return zip;
         }
     }
 
     public static IRubyObject group_by(IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         final RubyHash result = new RubyHash(runtime);
 
         callEach(runtime, context, self, new BlockCallback() {
             public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                 IRubyObject key = block.yield(context, largs[0]);
                 IRubyObject curr = result.fastARef(key);
 
                 if (curr == null) {
                     curr = runtime.newArray();
                     result.fastASet(key, curr);
                 }
                 curr.callMethod(context, MethodIndex.OP_LSHIFT, "<<", largs[0]);
                 return runtime.getNil();
             }
         });
 
         return result;
     }
     
     public static final class AppendBlockCallback implements BlockCallback {
         private Ruby runtime;
         private RubyArray result;
 
         public AppendBlockCallback(Ruby runtime, RubyArray result) {
             this.runtime = runtime;
             this.result = result;
         }
         
         public IRubyObject call(ThreadContext context, IRubyObject[] largs, Block blk) {
             result.append(largs[0]);
             
             return runtime.getNil();
         }
     }
 }
diff --git a/src/org/jruby/RubyEnumerator.java b/src/org/jruby/RubyEnumerator.java
index b581555762..7935712974 100644
--- a/src/org/jruby/RubyEnumerator.java
+++ b/src/org/jruby/RubyEnumerator.java
@@ -1,263 +1,174 @@
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
  * Copyright (C) 2006 Michael Studman <me@michaelstudman.com>
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
 
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Implementation of Ruby's Enumerator module.
  */
 public class RubyEnumerator extends RubyObject {
     /** target for each operation */
     private IRubyObject object;
     
     /** method to invoke for each operation */
     private IRubyObject method;
     
     /** args to each method */
     private IRubyObject[] methodArgs;
     
     private static ObjectAllocator ENUMERATOR_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyEnumerator(runtime, klass);
         }
     };
 
     public static void defineEnumerator(Ruby runtime) {
-        RubyModule enumerableModule = runtime.getModule("Enumerable");
-        RubyClass object = runtime.getObject();
-        RubyClass enumeratorClass = enumerableModule.defineClassUnder("Enumerator", object, ENUMERATOR_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyEnumerator.class);
 
-        enumeratorClass.includeModule(enumerableModule);
-        enumeratorClass.getMetaClass().defineMethod("new", callbackFactory.getOptSingletonMethod("new_instance"));
-        enumeratorClass.defineMethod("initialize", callbackFactory.getOptSingletonMethod("initialize"));
-        enumeratorClass.defineMethod("each", callbackFactory.getOptSingletonMethod("each"));
+        RubyModule kernel = runtime.getKernel();
+        kernel.defineMethod("to_enum", callbackFactory.getOptSingletonMethod("obj_to_enum"));
+        kernel.defineMethod("enum_for", callbackFactory.getOptSingletonMethod("obj_to_enum"));
 
-        object.defineMethod("to_enum", callbackFactory.getOptSingletonMethod("o_to_enum"));
-        object.defineMethod("enum_for", callbackFactory.getOptSingletonMethod("o_to_enum"));
+        RubyModule enm = runtime.getEnumerable();
+        enm.defineFastMethod("enum_with_index", callbackFactory.getFastSingletonMethod("each_with_index"));
+        enm.defineMethod("each_slice", callbackFactory.getSingletonMethod("each_slice", IRubyObject.class));
+        enm.defineFastMethod("enum_slice", callbackFactory.getFastSingletonMethod("enum_slice", IRubyObject.class));
+        enm.defineMethod("each_cons", callbackFactory.getSingletonMethod("each_cons", IRubyObject.class));
+        enm.defineFastMethod("enum_cons", callbackFactory.getFastSingletonMethod("enum_cons", IRubyObject.class));
 
-        enumerableModule.defineMethod("enum_with_index", callbackFactory.getSingletonMethod("each_with_index"));
-        enumerableModule.defineMethod("each_slice", callbackFactory.getSingletonMethod("each_slice",IRubyObject.class));
-        enumerableModule.defineMethod("enum_slice", callbackFactory.getSingletonMethod("enum_slice",IRubyObject.class));
-        enumerableModule.defineMethod("each_cons", callbackFactory.getSingletonMethod("each_cons",IRubyObject.class));
-        enumerableModule.defineMethod("enum_cons", callbackFactory.getSingletonMethod("enum_cons",IRubyObject.class));
-    }
-
-    private RubyEnumerator(Ruby runtime, RubyClass type) {
-        super(runtime, type);
-    }
-
-    public static IRubyObject new_instance(IRubyObject self, IRubyObject[] args, Block block) {
-        RubyClass klass = (RubyClass)self;
-        RubyEnumerator result = (RubyEnumerator) klass.allocate();
-        result.callInit(args, block);
-        return result;
-    }
+        RubyClass enmr = enm.defineClassUnder("Enumerator", runtime.getObject(), ENUMERATOR_ALLOCATOR);
 
-    public static IRubyObject initialize(IRubyObject self, IRubyObject[] args, Block block) {
-        return ((RubyEnumerator) self).initialize(self.getRuntime().getCurrentContext(), args, block);
-    }
+        enmr.includeModule(enm);
 
-    public static IRubyObject each(IRubyObject self, IRubyObject[] args, Block block) {
-        return ((RubyEnumerator) self).each(self.getRuntime().getCurrentContext(), args, block);
+        enmr.defineFastMethod("initialize", callbackFactory.getFastOptMethod("initialize"));
+        enmr.defineMethod("each", callbackFactory.getMethod("each"));
     }
 
-    public static IRubyObject o_to_enum(IRubyObject self, IRubyObject[] args, Block block) {
+    public static IRubyObject obj_to_enum(IRubyObject self, IRubyObject[] args, Block block) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
-
         newArgs[0] = self;
         System.arraycopy(args, 0, newArgs, 1, args.length);
 
-        return self.getRuntime().getModule("Enumerable").getConstant("Enumerator").callMethod(self.getRuntime().getCurrentContext(), "new", newArgs);
-    }
-
-    public static IRubyObject each_with_index(IRubyObject self, Block block) {
-        return self.getRuntime().getModule("Enumerable").getConstant("Enumerator").callMethod(self.getRuntime().getCurrentContext(), "new", 
-                               new IRubyObject[] { self, self.getRuntime().newSymbol("each_with_index") });
-    }
-
-    public static IRubyObject each_slice(IRubyObject self, IRubyObject arg, Block block) {
-        long sliceSize = arg.convertToInteger().getLongValue();
-
-        if (sliceSize <= 0L) {
-            throw self.getRuntime().newArgumentError("invalid slice size");
-        } 
-
-        SlicedBlockCallback sliceBlock = new SlicedBlockCallback(self.getRuntime(), block, sliceSize);
-
-        RubyEnumerable.callEachOld(self.getRuntime().getCurrentContext(), self, self.getMetaClass(), sliceBlock);
-            
-        if (sliceBlock.hasLeftovers()) {
-            sliceBlock.yieldLeftovers(self.getRuntime().getCurrentContext());
-        }
-
-        return self.getRuntime().getNil();
-    }
-
-    public static IRubyObject each_cons(IRubyObject self, IRubyObject arg, Block block) {
-        long consecutiveSize = arg.convertToInteger().getLongValue();
-
-        if (consecutiveSize <= 0L) {
-            throw self.getRuntime().newArgumentError("invalid size");
-        }
-
-        RubyEnumerable.callEachOld(self.getRuntime().getCurrentContext(), self, self.getMetaClass(), 
-                                new ConsecutiveBlockCallback(self.getRuntime(), block, consecutiveSize));
-
-        return self.getRuntime().getNil();
-    }
-
-    public static IRubyObject enum_slice(IRubyObject self, IRubyObject arg, Block block) {
-        return self.getRuntime().getModule("Enumerable").getConstant("Enumerator").callMethod(self.getRuntime().getCurrentContext(), "new", 
-                                     new IRubyObject[] { self, self.getRuntime().newSymbol("each_slice"), arg });
+        return self.getRuntime().getEnumerable().getConstant("Enumerator").callMethod(self.getRuntime().getCurrentContext(), "new", newArgs);
     }
 
-    public static IRubyObject enum_cons(IRubyObject self, IRubyObject arg, Block block) {
-        return self.getRuntime().getModule("Enumerable").getConstant("Enumerator").callMethod(self.getRuntime().getCurrentContext(), "new", 
-                               new IRubyObject[] { self, self.getRuntime().newSymbol("each_cons"), arg });
+    private RubyEnumerator(Ruby runtime, RubyClass type) {
+        super(runtime, type);
+        object = method = runtime.getNil();
     }
 
-    /** Primes the instance. Little validation is done at this stage */
-    private IRubyObject initialize(ThreadContext tc, IRubyObject[] args, Block block) {
-        Arity.checkArgumentCount(tc.getRuntime(), args, 1, -1);
-           
+    public IRubyObject initialize(IRubyObject[] args) {
+        Arity.checkArgumentCount(getRuntime(), args, 1, -1);
         object = args[0];
-        methodArgs = new IRubyObject[Math.max(0, args.length - 2)];
-
-        if (args.length >= 2) {
-            method = args[1];
-        } else {
-            method = RubySymbol.newSymbol(tc.getRuntime(), "each");
-        }
-
-        if (args.length >= 3) {
+        method = args.length > 1 ? args[1] : getRuntime().newSymbol("each");
+        if (args.length > 2) {
+            methodArgs = new IRubyObject[Math.max(0, args.length - 2)];
             System.arraycopy(args, 2, methodArgs, 0, args.length - 2);
         } else {
             methodArgs = new IRubyObject[0];
         }
-
         return this;
     }
 
     /**
      * Send current block and supplied args to method on target. According to MRI
      * Block may not be given and "each" should just ignore it and call on through to
      * underlying method.
      */
-    private IRubyObject each(ThreadContext tc, IRubyObject[] args, Block block) {
-        Arity.checkArgumentCount(tc.getRuntime(), args, 0, 0);
+    public IRubyObject each(Block block) {
+        return object.callMethod(getRuntime().getCurrentContext(), method.asSymbol(), methodArgs, block);
+    }
 
-        return object.callMethod(tc, method.asSymbol(), methodArgs, block);
+    public static IRubyObject each_with_index(IRubyObject self) {
+        return self.getRuntime().getEnumerable().getConstant("Enumerator").callMethod(self.getRuntime().getCurrentContext(), "new", 
+                               new IRubyObject[] { self, self.getRuntime().newSymbol("each_with_index") });
     }
 
-    /** Block callback for slicing the results of calling the client block */
-    public static class SlicedBlockCallback implements BlockCallback {
-        protected RubyArray slice;
-        protected final long sliceSize;
-        protected final Block clientBlock;
-        protected final Ruby runtime;
+    public static IRubyObject each_slice(IRubyObject self, IRubyObject arg, final Block block) {
+        final int size = (int)RubyNumeric.num2long(arg);
 
-        public SlicedBlockCallback(Ruby runtime, Block clientBlock, long sliceSize) {
-            this.runtime = runtime;
-            this.clientBlock = clientBlock;
-            this.sliceSize = sliceSize;
-            this.slice = RubyArray.newArray(runtime, sliceSize);
-        }
+        if (size <= 0) throw self.getRuntime().newArgumentError("invalid slice size");
 
-        public IRubyObject call(ThreadContext context, IRubyObject[] args, Block block) {
-            if (args.length > 1) {
-                slice.append(RubyArray.newArray(runtime, args));
-            } else {
-                slice.append(args[0]);
-            }
-
-            if (slice.getLength() == sliceSize) {
-                //no need to dup slice as we create a new one momentarily
-                clientBlock.call(context, new IRubyObject[] { slice });
+        final Ruby runtime = self.getRuntime();
+        final ThreadContext context = runtime.getCurrentContext();
+        final RubyArray result[] = new RubyArray[]{runtime.newArray(size)};
 
-                slice = RubyArray.newArray(runtime, sliceSize);
+        RubyEnumerable.callEach(runtime, context, self, new BlockCallback() {
+            public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
+                result[0].append(largs[0]);
+                if (result[0].size() == size) {
+                    block.yield(context, result[0]);
+                    result[0] = runtime.newArray(size);
+                }
+                return runtime.getNil();
             }
+        });
 
-            return runtime.getNil();
-        }
-
-
-        /** Slice may be over but there weren't enough items to make the slice */
-        public boolean hasLeftovers() {
-            return (slice.getLength() > 0) && (slice.getLength() < sliceSize);
-        }
-
-        /** Pass slice dregs on to client blcok */
-        public void yieldLeftovers(ThreadContext context) {
-            clientBlock.call(context, new IRubyObject[] { slice });
-        }
+        if (result[0].size() > 0) block.yield(context, result[0]);
+        return self.getRuntime().getNil();
     }
 
-    /** Block callback for viewing consecutive results from calling the client block */
-    public static class ConsecutiveBlockCallback implements BlockCallback {
-        protected final RubyArray cont;
-        protected final long contSize;
-        protected final Block clientBlock;
-        protected final Ruby runtime;
+    public static IRubyObject each_cons(IRubyObject self, IRubyObject arg, final Block block) {
+        final int size = (int)RubyNumeric.num2long(arg);
 
+        if (size <= 0) throw self.getRuntime().newArgumentError("invalid size");
 
-        public ConsecutiveBlockCallback(Ruby runtime, Block clientBlock, long contSize) {
-            this.runtime = runtime;
-            this.clientBlock = clientBlock;
-            this.contSize = contSize;
-            this.cont = RubyArray.newArray(runtime, contSize);
-        }
+        final Ruby runtime = self.getRuntime();
+        final ThreadContext context = runtime.getCurrentContext();
+        final RubyArray result = runtime.newArray(size);
 
-        public IRubyObject call(ThreadContext context, IRubyObject[] args, Block block) {
-            if (cont.getLength() == contSize) {
-                cont.shift();
+        RubyEnumerable.callEach(runtime, context, self, new BlockCallback() {
+            public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
+                if (result.size() == size) result.shift();
+                result.append(largs[0]);
+                if (result.size() == size) block.yield(context, result.aryDup());
+                return runtime.getNil();
             }
+        });
 
-            if (args.length > 1) {
-                cont.append(RubyArray.newArray(runtime, args));
-            } else {
-                cont.append(args[0]);
-            }
+        return runtime.getNil();        
+    }
 
-            if (cont.getLength() == contSize) {
-                //dup so we are in control of the array
-                clientBlock.call(context, new IRubyObject[] { cont.dup() });
-            }
+    public static IRubyObject enum_slice(IRubyObject self, IRubyObject arg) {
+        return self.getRuntime().getEnumerable().getConstant("Enumerator").callMethod(self.getRuntime().getCurrentContext(), "new", 
+                                     new IRubyObject[] { self, self.getRuntime().newSymbol("each_slice"), arg });
+    }
 
-            return runtime.getNil();
-        }
+    public static IRubyObject enum_cons(IRubyObject self, IRubyObject arg) {
+        return self.getRuntime().getEnumerable().getConstant("Enumerator").callMethod(self.getRuntime().getCurrentContext(), "new", 
+                               new IRubyObject[] { self, self.getRuntime().newSymbol("each_cons"), arg });
     }
 }
