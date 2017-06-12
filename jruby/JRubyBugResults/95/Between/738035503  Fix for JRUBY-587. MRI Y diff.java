diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index 58766a4ba5..c2c7a0f60a 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1,1343 +1,1352 @@
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
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.Pack;
 
 /**
  * The implementation of the built-in class Array in Ruby.
  */
 public class RubyArray extends RubyObject implements List {
 
     public static RubyClass createArrayClass(Ruby runtime) {
         RubyClass arrayc = runtime.defineClass("Array", runtime.getObject(), ARRAY_ALLOCATOR);
         arrayc.index = ClassIndex.ARRAY;
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
         arrayc.defineFastMethod("eql?", callbackFactory.getFastMethod("eql", RubyKernel.IRUBY_OBJECT));
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
 
         return arrayc;
     }
 
     private static ObjectAllocator ARRAY_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyArray(runtime, klass);
         }
     };
     
     public static final byte OP_PLUS_SWITCHVALUE = 1;
     public static final byte AREF_SWITCHVALUE = 2;
     public static final byte ASET_SWITCHVALUE = 3;
     public static final byte POP_SWITCHVALUE = 4;
     public static final byte PUSH_SWITCHVALUE = 5;
     public static final byte NIL_P_SWITCHVALUE = 6;
     public static final byte EQUALEQUAL_SWITCHVALUE = 7;
     public static final byte UNSHIFT_SWITCHVALUE = 8;
     public static final byte OP_LSHIFT_SWITCHVALUE = 9;
     public static final byte EMPTY_P_SWITCHVALUE = 10;
 
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue,
             String name, IRubyObject[] args, CallType callType, Block block) {
         switch (switchvalue) {
             case OP_PLUS_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
                 return op_plus(args[0]);
             case AREF_SWITCHVALUE:
                 Arity.optional().checkArity(context.getRuntime(), args);
                 return aref(args);
             case ASET_SWITCHVALUE:
                 Arity.optional().checkArity(context.getRuntime(), args);
                 return aset(args);
             case POP_SWITCHVALUE:
                 Arity.noArguments().checkArity(context.getRuntime(), args);
                 return pop();
             case PUSH_SWITCHVALUE:
                 Arity.optional().checkArity(context.getRuntime(), args);
             return push_m(args);
             case NIL_P_SWITCHVALUE:
                 Arity.noArguments().checkArity(context.getRuntime(), args);
                 return nil_p();
             case EQUALEQUAL_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
             return op_equal(args[0]);
             case UNSHIFT_SWITCHVALUE:
                 Arity.optional().checkArity(context.getRuntime(), args);
             return unshift_m(args);
             case OP_LSHIFT_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
                 return append(args[0]);
             case EMPTY_P_SWITCHVALUE:
                 Arity.noArguments().checkArity(context.getRuntime(), args);
                 return empty_p();
             case 0:
             default:
                 return super.callMethod(context, rubyclass, name, args, callType, block);
         }
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
 
     public static RubyArray newArray(Ruby runtime, List list) {
         RubyArray arr = new RubyArray(runtime, list.size());
         list.toArray(arr.values);
         arr.realLength = arr.values.length;
         return arr;
     }
 
     public static final int ARRAY_DEFAULT_SIZE = 16;    
 
     private IRubyObject[] values;
     private boolean tmpLock = false;
     private boolean shared = false;
 
     private int begin = 0;
     private int realLength = 0;
 
     /* 
      * plain internal array assignment
      */
     public RubyArray(Ruby runtime, IRubyObject[]vals){
         super(runtime, runtime.getArray());
         values = vals;
         realLength = vals.length;
     }
     
     /* rb_ary_new2
      * just allocates the internal array
      */
     private RubyArray(Ruby runtime, long length) {
         super(runtime, runtime.getArray());
         checkLength(length);
         alloc((int) length);
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
 
     /* rb_ary_new3, rb_ary_new4, with begin
      * allocates the internal array of size length and copies the 'length' elements from 'vals' starting from 'beg'
      */
     private RubyArray(Ruby runtime, int beg, long length, IRubyObject[] vals) {
         super(runtime, runtime.getArray());
         checkLength(length);
         int ilength = (int) length;
         alloc(ilength);
         if (ilength > 0 && vals.length > 0) System.arraycopy(vals, beg, values, 0, ilength);
 
         realLength = ilength;
     }
 
+    /*
+     * just allocates the internal array, with optional objectspace
+     */
+    private RubyArray(Ruby runtime, long length, boolean objectSpace) {
+        super(runtime, runtime.getArray(), objectSpace);
+        checkLength(length);
+        alloc((int) length);
+    }
+
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
 
     public int getNativeTypeIndex() {
         return ClassIndex.ARRAY;
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
         return !shared ? values : toJavaArray();
     }    
 
     /** rb_ary_make_shared
      *
      */
     private final RubyArray makeShared(int beg, int len){
         RubyArray sharedArray = new RubyArray(getRuntime(), true);
         shared = true;
         sharedArray.values = values;
         sharedArray.shared = true;
         sharedArray.begin = beg;
         sharedArray.realLength = len;
         return sharedArray;        
     }
 
     /** rb_ary_modify_check
      *
      */
     private final void modifyCheck() {
         testFrozen("array");
 
         if (tmpLock) {
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
         if (shared) {
             IRubyObject[] vals = reserve(realLength);
             shared = false;
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
         int argc = checkArgumentCount(args, 0, 2);
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
                 store(i, context.yield(new RubyFixnum(runtime, i), block));
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
 
         origArr.shared = true;
         values = origArr.values;
         realLength = origArr.realLength;
         begin = origArr.begin;
         shared = true;
 
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
         ThreadContext context = getRuntime().getCurrentContext();
         int begin = this.begin;
         
         for (int i = begin; i < begin + realLength; i++) {
             if (item.callMethod(context, "==", values[i]).isTrue()) return true;
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
             h ^= RubyNumeric.num2long(values[i].callMethod(context, "hash"));
         }
 
         return runtime.newFixnum(h);
     }
 
     /** rb_ary_store
      *
      */
     private final IRubyObject store(long index, IRubyObject value) {
         if (index < 0) {
             index += realLength;
             if (index < 0) {
                 throw getRuntime().newIndexError("index " + (index - realLength) + " out of array");
             }
         }
 
         modify();
 
         if (index >= values.length) {
             long newLength = values.length / 2;
 
             if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
 
             newLength += index;
             if (newLength >= Integer.MAX_VALUE) {
                 throw getRuntime().newArgumentError("index too big");
             }
             realloc((int) newLength);
         }
         if (index > realLength) {
             Arrays.fill(values, realLength, (int) index + 1, getRuntime().getNil());
         }
         
         if (index >= realLength) realLength = (int) index + 1;
 
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
 
     /** rb_ary_fetch
      *
      */
     public IRubyObject fetch(IRubyObject[] args, Block block) {
         checkArgumentCount(args, 1, 2);
         IRubyObject pos = args[0];
 
         if (block.isGiven() && args.length == 2) {
             getRuntime().getWarnings().warn("block supersedes default value argument");
         }
 
         long index = RubyNumeric.num2long(pos);
 
         if (index < 0) index += realLength;
 
         if (index < 0 || index >= realLength) {
             if (block.isGiven()) return getRuntime().getCurrentContext().yield(pos, block);
 
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
         long rlen;
 
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
                 int tryNewLength = values.length + values.length / 2;
                 
                 realloc(len > tryNewLength ? (int)len : tryNewLength);
             }
             
             Arrays.fill(values, realLength, (int) beg, getRuntime().getNil());
             if (rlen > 0) {
                 System.arraycopy(rplArr.values, rplArr.begin, values, (int) beg, (int) rlen);
             }
             realLength = (int) len;
         } else {
             long alen;
 
             if (beg + len > realLength) len = realLength - beg;
 
             alen = realLength + rlen - len;
             if (alen >= values.length) {
                 int tryNewLength = values.length + values.length / 2;
                 realloc(alen > tryNewLength ? (int)alen : tryNewLength);
             }
             
             if (len != rlen) {
                 System.arraycopy(values, (int) (beg + len), values, (int) (beg + rlen), realLength - (int) (beg + len));
                 realLength = (int) alen;
             }
             
             if (rlen > 0) {
                 System.arraycopy(rplArr.values, rplArr.begin, values, (int) beg, (int) rlen);
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
 
         splice(pos, 0, new RubyArray(getRuntime(), 1, args.length - 1, args)); // rb_ary_new4
         
         return this;
     }
 
     public final IRubyObject dup() {
         return aryDup();
     }
 
     /** rb_ary_dup
      * 
      */
     private final RubyArray aryDup() {
         RubyArray dup = new RubyArray(getRuntime(), realLength);
         dup.setTaint(isTaint()); // from DUP_SETUP
         // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
         System.arraycopy(values, begin, dup.values, 0, realLength);
         dup.realLength = realLength;
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
                 result.append(entry(RubyNumeric.fix2long(args[i])));
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
         
         // MRI does klass = rb_obj_class(ary); here, what for ?
         if (len == 0) return new RubyArray(getRuntime(), 0);
 
         return makeShared(begin + (int) beg, (int) len);
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
     public final RubyArray append(IRubyObject item) {
         modify();
         
         if (realLength == Integer.MAX_VALUE) throw getRuntime().newArgumentError("index too big");
 
         if (realLength == values.length){
             long newLength = values.length + values.length / 2;
             if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
 
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
 
         if (!shared) {
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
 
         if (!shared) shared = true;
 
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
             long newLength = values.length / 2;
             if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
 
             newLength += values.length;
             realloc((int) newLength);
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
         return getRuntime().newBoolean(isFrozen() || tmpLock);
     }
 
     /** rb_ary_aref
      */
     public IRubyObject aref(IRubyObject[] args) {
         long beg, len;
         if (args.length == 2) {
             if (args[0] instanceof RubySymbol) {
                 throw getRuntime().newTypeError("Symbol as array index");
             }
             beg = RubyNumeric.num2long(args[0]);
             len = RubyNumeric.num2long(args[1]);
 
             if (beg < 0) beg += realLength;
 
             return subseq(beg, len);
         }
 
         if (args.length != 1) checkArgumentCount(args, 1, 1);
 
         IRubyObject arg = args[0];
 
         if (arg instanceof RubyFixnum) return entry(RubyNumeric.fix2long(arg));
         if (arg instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
 
         long[] beglen;
         if (!(arg instanceof RubyRange)) {
         } else if ((beglen = ((RubyRange) arg).begLen(realLength, 0)) == null) {
             return getRuntime().getNil();
         } else {
             beg = beglen[0];
             len = beglen[1];
             return subseq(beg, len);
         }
 
         return entry(RubyNumeric.num2long(arg));
     }
 
     /** rb_ary_aset
      *
      */
     public IRubyObject aset(IRubyObject[] args) {
         if (args.length == 3) {
             if (args[0] instanceof RubySymbol) {
                 throw getRuntime().newTypeError("Symbol as array index");
             }
             if (args[1] instanceof RubySymbol) {
                 throw getRuntime().newTypeError("Symbol as subarray length");
             }
             splice(RubyNumeric.num2long(args[0]), RubyNumeric.num2long(args[1]), args[2]);
             return args[2];
         }
 
         if (args.length != 2) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + 
                     " for 2)");
         }
         
         if (args[0] instanceof RubyFixnum) {
             store(RubyNumeric.fix2long(args[0]), args[1]);
             return args[1];
         }
         
         if (args[0] instanceof RubySymbol) {
             throw getRuntime().newTypeError("Symbol as array index");
         }
 
         if (args[0] instanceof RubyRange) {
             long[] beglen = ((RubyRange) args[0]).begLen(realLength, 1);
             splice(beglen[0], beglen[1], args[1]);
             return args[1];
         }
         
         store(RubyNumeric.num2long(args[0]), args[1]);
         return args[1];
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
 
     /** rb_ary_inspect
      *
      */
     public IRubyObject inspect() {
         if (realLength == 0) return getRuntime().newString("[]");
 
         if (!getRuntime().registerInspecting(this)) return getRuntime().newString("[...]");
 
         RubyString s;
         try {
             StringBuffer buffer = new StringBuffer("[");
             Ruby runtime = getRuntime();
             ThreadContext context = runtime.getCurrentContext();
             boolean tainted = isTaint();
             for (int i = 0; i < realLength; i++) {
                 s = RubyString.objAsString(values[begin + i].callMethod(context, "inspect"));
                 
                 if (s.isTaint()) tainted = true;
 
                 if (i > 0) buffer.append(", ");
 
                 buffer.append(s.toString());
             }
             buffer.append("]");
             if (tainted) setTaint(true);
 
             return runtime.newString(buffer.toString());
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
             
         checkArgumentCount(args, 0, 1);
         int n = (int)RubyNumeric.num2long(args[0]);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
     	}
     	
         return makeShared(begin, n);
     }
 
     /** rb_ary_last
      *
      */
     public IRubyObject last(IRubyObject[] args) {
         if (args.length == 0) {
             if (realLength == 0) return getRuntime().getNil();
 
             return values[begin + realLength - 1];
         } 
             
         checkArgumentCount(args, 0, 1);
 
         int n = (int)RubyNumeric.num2long(args[0]);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
         }
 
         return makeShared(begin + realLength - n, n);
     }
 
     /** rb_ary_each
      *
      */
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = begin; i < begin + realLength; i++) {
             context.yield(values[i], block);
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
             context.yield(runtime.newFixnum(i), block);
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
             context.yield(values[begin + len], block);
             
             if (realLength < len) len = realLength;
         }
         
         return this;
     }
 
     private final IRubyObject inspectJoin(IRubyObject sep) {
         IRubyObject result = join(sep);
         getRuntime().unregisterInspecting(this);
         return result;
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
 
         if (!sep.isNil()) len += sep.convertToString().getByteList().length() * (realLength - 1);
 
         StringBuffer buf = new StringBuffer((int) len);
         Ruby runtime = getRuntime();
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject tmp = values[i];
             if (tmp instanceof RubyString) {
                 // do nothing
             } else if (tmp instanceof RubyArray) {
                 if (!runtime.registerInspecting(tmp)) {
                     tmp = runtime.newString("[...]");
                 } else {
                     tmp = ((RubyArray) tmp).inspectJoin(sep);
                 }
             } else {
                 tmp = RubyString.objAsString(tmp);
             }
 
             if (i > begin && !sep.isNil()) buf.append(sep.toString());
 
             buf.append(((RubyString) tmp).toString());
             taint |= tmp.isTaint();
         }
 
         RubyString result = RubyString.newString(runtime, buf.toString());
         
         if (taint) result.setTaint(taint);
 
         return result;
     }
 
     /** rb_ary_join_m
      *
      */
     public RubyString join_m(IRubyObject[] args) {
         int argc = checkArgumentCount(args, 0, 1);
         IRubyObject sep = (argc == 1) ? args[0] : getRuntime().getGlobalVariables().get("$,");
         
         return join(sep);
     }
 
     /** rb_ary_to_a
      *
      */
     public RubyArray to_a() {
         return this;
     }
 
     public IRubyObject to_ary() {
     	return this;
     }
 
     public RubyArray convertToArray() {
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
                 return obj.callMethod(getRuntime().getCurrentContext(), "==", this);
             }
         }
 
         RubyArray ary = (RubyArray) obj;
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (long i = 0; i < realLength; i++) {
             if (!elt(i).callMethod(context, "==", ary.elt(i)).isTrue()) return runtime.getFalse();
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_eql
      *
      */
     public RubyBoolean eql(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
         if (!(obj instanceof RubyArray)) return getRuntime().getFalse();
 
         RubyArray ary = (RubyArray) obj;
 
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (long i = 0; i < realLength; i++) {
             if (!elt(i).callMethod(context, "eql?", ary.elt(i)).isTrue()) return runtime.getFalse();
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
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index d29de1c67b..2658a28d1c 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1094 +1,1098 @@
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
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import org.jruby.ast.Node;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicMethod;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.IdUtil;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.collections.SinglyLinkedList;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import org.jruby.runtime.ClassIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyObject implements Cloneable, IRubyObject {
 	
     // The class of this object
     private RubyClass metaClass;
 
     // The instance variables of this object.
     protected Map instanceVariables;
 
     // The two properties frozen and taint
     private boolean frozen;
     private boolean taint;
 
     public RubyObject(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
 
     public RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         this.metaClass = metaClass;
         this.frozen = false;
         this.taint = false;
 
         // Do not store any immediate objects into objectspace.
         if (useObjectSpace && !isImmediate()) {
             runtime.getObjectSpace().add(this);
         }
 
         // FIXME are there objects who shouldn't be tainted?
         // (mri: OBJSETUP)
         taint |= runtime.getSafeLevel() >= 3;
     }
+
+    public void attachToObjectSpace() {
+        getRuntime().getObjectSpace().add(this);
+    }
     
     /**
      * This is overridden in the other concrete Java builtins to provide a fast way
      * to determine what type they are.
      */
     public int getNativeTypeIndex() {
         return ClassIndex.OBJECT;
     }
     
     /*
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /**
      * Create a new meta class.
      *
      * @since Ruby 1.6.7
      */
     public RubyClass makeMetaClass(RubyClass superClass, SinglyLinkedList parentCRef) {
         RubyClass klass = new MetaClass(getRuntime(), superClass, getMetaClass().getAllocator(), parentCRef);
         setMetaClass(klass);
 		
         klass.setInstanceVariable("__attached__", this);
 
         if (this instanceof RubyClass && isSingleton()) { // could be pulled down to RubyClass in future
             klass.setMetaClass(klass);
             klass.setSuperClass(((RubyClass)this).getSuperClass().getRealClass().getMetaClass());
         } else {
             klass.setMetaClass(superClass.getRealClass().getMetaClass());
         }
         
         // use same ClassIndex as metaclass, since we're technically still of that type 
         klass.index = superClass.index;
         return klass;
     }
         
     public boolean isSingleton() {
         return false;
     }
 
     public boolean singletonMethodsAllowed() {
         return true;
     }
 
     public Class getJavaClass() {
         return IRubyObject.class;
     }
     
     public static void puts(Object obj) {
         System.out.println(obj.toString());
     }
 
     /**
      * This method is just a wrapper around the Ruby "==" method,
      * provided so that RubyObjects can be used as keys in the Java
      * HashMap object underlying RubyHash.
      */
     public boolean equals(Object other) {
         return other == this || other instanceof IRubyObject && callMethod(getRuntime().getCurrentContext(), "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return callMethod(getRuntime().getCurrentContext(), "to_s").toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public Ruby getRuntime() {
         return metaClass.getRuntime();
     }
     
     public boolean safeHasInstanceVariables() {
         return instanceVariables != null && instanceVariables.size() > 0;
     }
     
     public Map safeGetInstanceVariables() {
         return instanceVariables == null ? null : getInstanceVariablesSnapshot();
     }
 
     public IRubyObject removeInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().remove(name);
     }
 
     /**
      * Returns an unmodifiable snapshot of the current state of instance variables.
      * This method synchronizes access to avoid deadlocks.
      */
     public Map getInstanceVariablesSnapshot() {
         synchronized(getInstanceVariables()) {
             return Collections.unmodifiableMap(new HashMap(getInstanceVariables()));
         }
     }
 
     public Map getInstanceVariables() {
     	// TODO: double checking may or may not be safe enough here
     	if (instanceVariables == null) {
 	    	synchronized (this) {
 	    		if (instanceVariables == null) {
                             instanceVariables = Collections.synchronizedMap(new HashMap());
 	    		}
 	    	}
     	}
         return instanceVariables;
     }
 
     public void setInstanceVariables(Map instanceVariables) {
         this.instanceVariables = Collections.synchronizedMap(instanceVariables);
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public RubyClass getMetaClass() {
         return metaClass;
     }
 
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * Gets the frozen.
      * @return Returns a boolean
      */
     public boolean isFrozen() {
         return frozen;
     }
 
     /**
      * Sets the frozen.
      * @param frozen The frozen to set
      */
     public void setFrozen(boolean frozen) {
         this.frozen = frozen;
     }
 
     /** rb_frozen_class_p
     *
     */
    protected void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message + getMetaClass().getName());
        }
    }
 
    protected void checkFrozen() {
        testFrozen("can't modify frozen ");
    }
 
     /**
      * Gets the taint.
      * @return Returns a boolean
      */
     public boolean isTaint() {
         return taint;
     }
 
     /**
      * Sets the taint.
      * @param taint The taint to set
      */
     public void setTaint(boolean taint) {
         this.taint = taint;
     }
 
     public boolean isNil() {
         return false;
     }
 
     public boolean isTrue() {
         return !isNil();
     }
 
     public boolean isFalse() {
         return isNil();
     }
 
     public boolean respondsTo(String name) {
         return getMetaClass().isMethodBound(name, false);
     }
 
     // Some helper functions:
 
     public int checkArgumentCount(IRubyObject[] args, int min, int max) {
         if (args.length < min) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for " + min + ")");
         }
         if (max > -1 && args.length > max) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for " + max + ")");
         }
         return args.length;
     }
 
     public boolean isKindOf(RubyModule type) {
         return getMetaClass().hasModuleInHierarchy(type);
     }
 
     /** rb_singleton_class
      *
      */    
     public RubyClass getSingletonClass() {
         RubyClass klass;
         
         if (getMetaClass().isSingleton() && getMetaClass().getInstanceVariable("__attached__") == this) {
             klass = getMetaClass();            
         } else {
             klass = makeMetaClass(getMetaClass(), getMetaClass().getCRef());
         }
         
         klass.setTaint(isTaint());
         klass.setFrozen(isFrozen());
         
         return klass;
     }
     
     /** rb_singleton_class_clone
      *
      */
     public RubyClass getSingletonClassClone() {
        RubyClass klass = getMetaClass();
 
        if (!klass.isSingleton()) {
            return klass;
 		}
        
        MetaClass clone = new MetaClass(getRuntime(), klass.getSuperClass(), getMetaClass().getAllocator(), getMetaClass().getCRef());
        clone.setFrozen(klass.isFrozen());
        clone.setTaint(klass.isTaint());
 
        if (this instanceof RubyClass) {
            clone.setMetaClass(clone);
        } else {
            clone.setMetaClass(klass.getSingletonClassClone());
        }
        
        if (klass.safeHasInstanceVariables()) {
            clone.setInstanceVariables(new HashMap(klass.getInstanceVariables()));
        }
 
        klass.cloneMethods(clone);
 
        clone.getMetaClass().setInstanceVariable("__attached__", clone);
 
        return clone;
     }    
 
     /** rb_define_singleton_method
      *
      */
     public void defineSingletonMethod(String name, Callback method) {
         getSingletonClass().defineMethod(name, method);
     }
 
     /** init_copy
      * 
      */
     public void initCopy(IRubyObject original) {
         assert original != null;
         assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";
 
         setInstanceVariables(new HashMap(original.getInstanceVariables()));
         /* FIXME: finalizer should be dupped here */
         callMethod(getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         setTaint(isTaint() || obj.isTaint());
 
         return this;
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType, Block.NULL_BLOCK);
     }
     
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, getMetaClass(), name, args, callType, block);
     }
 
     /**
      * Used by the compiler to ease calling indexed methods
      */
     public IRubyObject callMethod(ThreadContext context, byte methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, getRuntime().getSelectorTable().table[module.index][methodIndex], name, args, callType, block);
         } 
             
         return callMethod(context, module, name, args, callType, block);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, name, args, callType, Block.NULL_BLOCK);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, rubyclass, name, args, callType, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         method = rubyclass.searchMethod(name);
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(context.getFrameSelf(), callType))) {
 
             if (callType == CallType.SUPER) {
                 throw getRuntime().newNameError("super: no superclass method '" + name + "'", name);
             }
 
             // store call information so method_missing impl can use it
             context.setLastCallStatus(method.getVisibility(), callType);
 
             if (name.equals("method_missing")) {
                 return RubyKernel.method_missing(this, args, block);
             }
 
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(getRuntime(), name);
 
             return callMethod(context, "method_missing", newArgs, block);
         }
 
         RubyModule implementer = null;
         if (method.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             implementer = rubyclass.findImplementer(method.getImplementationClass());
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             implementer = method.getImplementationClass();
         }
 
         String originalName = method.getOriginalName();
         if (originalName != null) {
             name = originalName;
         }
 
         return method.call(context, this, implementer, name, args, false, block);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, Block block) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, block);
     }
 
     /**
      * rb_funcall
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return callMethod(context, name, new IRubyObject[] { arg });
     }
 
     public IRubyObject instance_variable_get(IRubyObject var) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	IRubyObject variable = getInstanceVariable(varName);
 
     	// Pickaxe v2 says no var should show NameError, but ruby only sends back nil..
     	return variable == null ? getRuntime().getNil() : variable;
     }
 
     public IRubyObject getInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().get(name);
     }
 
     public IRubyObject instance_variable_set(IRubyObject var, IRubyObject value) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	return setInstanceVariable(var.asSymbol(), value);
     }
 
     public IRubyObject setInstanceVariable(String name, IRubyObject value,
             String taintError, String freezeError) {
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError(taintError);
         }
         testFrozen(freezeError);
 
         getInstanceVariables().put(name, value);
 
         return value;
     }
 
     /** rb_iv_set / rb_ivar_set
      *
      */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         return setInstanceVariable(name, value,
                 "Insecure: can't modify instance variable", "");
     }
 
     public Iterator instanceVariableNames() {
         return getInstanceVariables().keySet().iterator();
     }
 
     /** rb_eval
      *
      */
     public IRubyObject eval(Node n) {
         //return new EvaluationState(getRuntime(), this).begin(n);
         // need to continue evaluation with a new self, so save the old one (should be a stack?)
         return EvaluationState.eval(getRuntime().getCurrentContext(), n, this, Block.NULL_BLOCK);
     }
 
     public void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
     public void extendObject(RubyModule module) {
         getSingletonClass().includeModule(module);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
     public IRubyObject convertToTypeWithCheck(String targetType, String convertMethod) {
         if (targetType.equals(getMetaClass().getName())) {
             return this;
         }
 
         IRubyObject value = convertToType(targetType, convertMethod, false);
         if (value.isNil()) {
             return value;
         }
 
         if (!targetType.equals(value.getMetaClass().getName())) {
             throw getRuntime().newTypeError(value.getMetaClass().getName() + "#" + convertMethod +
                     "should return " + targetType);
         }
 
         return value;
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(String targetType, String convertMethod, boolean raise) {
         // No need to convert something already of the correct type.
         // XXXEnebo - Could this pass actual class reference instead of String?
         if (targetType.equals(getMetaClass().getName())) {
             return this;
         }
         
         if (!respondsTo(convertMethod)) {
             if (raise) {
                 throw getRuntime().newTypeError(
                     "can't convert " + trueFalseNil(getMetaClass().getName()) + " into " + trueFalseNil(targetType));
             } 
 
             return getRuntime().getNil();
         }
         return callMethod(getRuntime().getCurrentContext(), convertMethod);
     }
 
     public static String trueFalseNil(IRubyObject v) {
         return trueFalseNil(v.getMetaClass().getName());
     }
 
     public static String trueFalseNil(String v) {
         if("TrueClass".equals(v)) {
             return "true";
         } else if("FalseClass".equals(v)) {
             return "false";
         } else if("NilClass".equals(v)) {
             return "nil";
         }
         return v;
     }
 
     public RubyArray convertToArray() {
         return (RubyArray) convertToType("Array", "to_ary", true);
     }
 
     public RubyFloat convertToFloat() {
         return (RubyFloat) convertToType("Float", "to_f", true);
     }
 
     public RubyInteger convertToInteger() {
         return (RubyInteger) convertToType("Integer", "to_int", true);
     }
 
     public RubyString convertToString() {
         return (RubyString) convertToType("String", "to_str", true);
     }
 
     /** rb_obj_as_string
      */
     public RubyString objAsString() {
         IRubyObject str;
         if(this instanceof RubyString) {
             return (RubyString)this;
         }
         str = this.callMethod(getRuntime().getCurrentContext(),"to_s");
         if(!(str instanceof RubyString)) {
             str = anyToString();
         }
         return (RubyString)str;
     }
 
     /** rb_convert_type
      *
      */
     public IRubyObject convertType(Class type, String targetType, String convertMethod) {
         if (type.isAssignableFrom(getClass())) {
             return this;
         }
 
         IRubyObject result = convertToType(targetType, convertMethod, true);
 
         if (!type.isAssignableFrom(result.getClass())) {
             throw getRuntime().newTypeError(
                 getMetaClass().getName() + "#" + convertMethod + " should return " + targetType + ".");
         }
 
         return result;
     }
     
     /** rb_check_string_type
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = convertToTypeWithCheck("String","to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = getRuntime().newString("");
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     */    
     public IRubyObject checkArrayType() {
         return convertToTypeWithCheck("Array","to_ary");
     }
 
     public void checkSafeString() {
         if (getRuntime().getSafeLevel() > 0 && isTaint()) {
             ThreadContext tc = getRuntime().getCurrentContext();
             if (tc.getFrameLastFunc() != null) {
                 throw getRuntime().newSecurityError("Insecure operation - " + tc.getFrameLastFunc());
             }
             throw getRuntime().newSecurityError("Insecure operation: -r");
         }
         getRuntime().secure(4);
         if (!(this instanceof RubyString)) {
             throw getRuntime().newTypeError(
                 "wrong argument type " + getMetaClass().getName() + " (expected String)");
         }
     }
 
     /** specific_eval
      *
      */
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(mod, block);
         }
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
 		    throw getRuntime().newArgumentError("block not supplied");
 		} else if (args.length > 3) {
 		    String lastFuncName = tc.getFrameLastFunc();
 		    throw getRuntime().newArgumentError(
 		        "wrong # of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
 		}
 		/*
 		if (ruby.getSecurityLevel() >= 4) {
 			Check_Type(argv[0], T_STRING);
 		} else {
 			Check_SafeStr(argv[0]);
 		}
 		*/
         
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         args[0].convertToString();
         
 		IRubyObject file = args.length > 1 ? args[1] : getRuntime().newString("(eval)");
 		IRubyObject line = args.length > 2 ? args[2] : RubyFixnum.one(getRuntime());
 
 		Visibility savedVisibility = tc.getCurrentVisibility();
         tc.setCurrentVisibility(Visibility.PUBLIC);
 		try {
 		    return evalUnder(mod, args[0], file, line);
 		} finally {
             tc.setCurrentVisibility(savedVisibility);
 		}
     }
 
     public IRubyObject evalUnder(RubyModule under, IRubyObject src, IRubyObject file, IRubyObject line) {
         /*
         if (ruby_safe_level >= 4) {
         	Check_Type(src, T_STRING);
         } else {
         	Check_SafeStr(src);
         	}
         */
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // FIXME: lineNumber is not supported
                 //IRubyObject lineNumber = args[3];
 
                 return args[0].evalSimple(source.getRuntime().getCurrentContext(),
                                   source, ((RubyString) filename).toString());
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line }, Block.NULL_BLOCK);
     }
 
     private IRubyObject yieldUnder(RubyModule under, Block block) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield = args[0];
                     IRubyObject selfInYield = args[0];
                     return block.yield(context, valueInYield, selfInYield, context.getRubyClass(), false);
                     //TODO: Should next and return also catch here?
                 } catch (JumpException je) {
                 	if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 		return (IRubyObject) je.getValue();
                 	} 
 
                     throw je;
                 } finally {
                     block.setVisibility(savedVisibility);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this }, block);
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalWithBinding(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, String file) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         assert file != null;
 
         ThreadContext threadContext = getRuntime().getCurrentContext();
 
         ISourcePosition savedPosition = threadContext.getPosition();
         IRubyObject result = getRuntime().getNil();
 
         IRubyObject newSelf = null;
 
         if (!(scope instanceof RubyBinding)) {
             if (scope instanceof RubyProc) {
                 scope = ((RubyProc) scope).binding();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
 
         Block blockOfBinding = ((RubyBinding)scope).getBlock();
         try {
             // Binding provided for scope, use it
             threadContext.preEvalWithBinding(blockOfBinding);
             newSelf = threadContext.getFrameSelf();
 
             result = EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope()), newSelf, blockOfBinding);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             threadContext.postEvalWithBinding(blockOfBinding);
 
             // restore position
             threadContext.setPosition(savedPosition);
         }
         return result;
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalSimple(org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalSimple(ThreadContext context, IRubyObject src, String file) {
         // this is ensured by the callers
         assert file != null;
 
         ISourcePosition savedPosition = context.getPosition();
 
         // no binding, just eval in "current" frame (caller's frame)
         try {
             return EvaluationState.eval(context, getRuntime().parse(src.toString(), file, context.getCurrentScope()), this, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             // restore position
             context.setPosition(savedPosition);
         }
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     public IRubyObject obj_equal(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
 //        if (isNil()) {
 //            return getRuntime().newBoolean(obj.isNil());
 //        }
 //        return getRuntime().newBoolean(this == obj);
     }
 
 	public IRubyObject same(IRubyObject other) {
 		return this == other ? getRuntime().getTrue() : getRuntime().getFalse();
 	}
 
     
     /** rb_obj_init_copy
      * 
      */
 	public IRubyObject initialize_copy(IRubyObject original) {
 	    if (this == original) return this;
 	    
 	    checkFrozen();
         
         if (getMetaClass().getRealClass() != original.getMetaClass().getRealClass()) {
 	            throw getRuntime().newTypeError("initialize_copy should take same class object");
 	    }
 
 	    return this;
 	}
 
     /**
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      */
     public RubyBoolean respond_to(IRubyObject[] args) {
         checkArgumentCount(args, 1, 2);
 
         String name = args[0].asSymbol();
         boolean includePrivate = args.length > 1 ? args[1].isTrue() : false;
 
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate));
     }
 
     /** Return the internal id of an object.
      *
      * <i>CRuby function: rb_obj_id</i>
      *
      */
     public synchronized RubyFixnum id() {
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
     
     public RubyFixnum hash() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
 
     public int hashCode() {
     	return (int) RubyNumeric.fix2long(callMethod(getRuntime().getCurrentContext(), "hash"));
     }
 
     /** rb_obj_type
      *
      */
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn("Object#type is deprecated; use Object#class");
         return type();
     }
 
     /** rb_obj_clone
      *  should be overriden only by: Proc, Method, UnboundedMethod, Binding
      */
     public IRubyObject rbClone() {
         if (isImmediate()) { // rb_special_const_p(obj) equivalent
             throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
         }
         
         IRubyObject clone = doClone();
         clone.setMetaClass(getSingletonClassClone());
         clone.setTaint(isTaint());
         clone.initCopy(this);
         clone.setFrozen(isFrozen());
         return clone;
     }
 
     // Hack: allow RubyModule and RubyClass to override the allocation and return the the correct Java instance
     // Cloning a class object doesn't work otherwise and I don't really understand why --sma
     protected IRubyObject doClone() {
         RubyClass realClass = getMetaClass().getRealClass();
     	return realClass.getAllocator().allocate(getRuntime(), realClass);
     }
 
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(getRuntime().getCurrentContext(), "write", this);
 
         return getRuntime().getNil();
     }
 
     /** rb_obj_dup
      *  should be overriden only by: Proc
      */
     public IRubyObject dup() {
         if (isImmediate()) {
             throw getRuntime().newTypeError("can't dup " + getMetaClass().getName());
         }        
         
         IRubyObject dup = doClone();    
 
         dup.setMetaClass(type());
         dup.setFrozen(false);
         dup.setTaint(isTaint());
         
         dup.initCopy(this);
 
         return dup;
     }
 
     /** rb_obj_tainted
      *
      */
     public RubyBoolean tainted() {
         return getRuntime().newBoolean(isTaint());
     }
 
     /** rb_obj_taint
      *
      */
     public IRubyObject taint() {
         getRuntime().secure(4);
         if (!isTaint()) {
         	testFrozen("object");
             setTaint(true);
         }
         return this;
     }
 
     /** rb_obj_untaint
      *
      */
     public IRubyObject untaint() {
         getRuntime().secure(3);
         if (isTaint()) {
         	testFrozen("object");
             setTaint(false);
         }
         return this;
     }
 
     /** Freeze an object.
      *
      * rb_obj_freeze
      *
      */
     public IRubyObject freeze() {
         if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't freeze object");
         }
         setFrozen(true);
         return this;
     }
 
     /** rb_obj_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen());
     }
 
     /** rb_obj_inspect
      *
      */
     public IRubyObject inspect() {
         if ((!isImmediate()) &&
                 // TYPE(obj) == T_OBJECT
                 !(this instanceof RubyClass) &&
                 this != getRuntime().getObject() &&
                 this != getRuntime().getClass("Module") &&
                 !(this instanceof RubyModule) &&
                 safeHasInstanceVariables()) {
 
             StringBuffer part = new StringBuffer();
             String cname = getMetaClass().getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(this)));
             if(!getRuntime().registerInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append(" ...>");
                 return getRuntime().newString(part.toString());
             }
             try {
                 String sep = "";
                 Map iVars = getInstanceVariablesSnapshot();
                 for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
                     String name = (String) iter.next();
                     if(IdUtil.isInstanceVariable(name)) {
                         part.append(" ");
                         part.append(sep);
                         part.append(name);
                         part.append("=");
                         part.append(((IRubyObject)(iVars.get(name))).callMethod(getRuntime().getCurrentContext(), "inspect"));
                         sep = ",";
                     }
                 }
                 part.append(">");
                 return getRuntime().newString(part.toString());
             } finally {
                 getRuntime().unregisterInspecting(this);
             }
diff --git a/src/org/jruby/runtime/builtin/IRubyObject.java b/src/org/jruby/runtime/builtin/IRubyObject.java
index d290720c3a..0f388d5b55 100644
--- a/src/org/jruby/runtime/builtin/IRubyObject.java
+++ b/src/org/jruby/runtime/builtin/IRubyObject.java
@@ -1,397 +1,399 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
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
 package org.jruby.runtime.builtin;
 
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFloat;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyString;
 import org.jruby.ast.Node;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.callback.Callback;
 
 /** Object is the parent class of all classes in Ruby. Its methods are
  * therefore available to all objects unless explicitly overridden.
  *
  * @author  jpetersen
  */
 public interface IRubyObject {
     public static final IRubyObject[] NULL_ARRAY = new IRubyObject[0];
     
     /**
      * Return the ClassIndex value for the native type this object was
      * constructed from. Particularly useful for determining marshalling
      * format. All instances of Hash instances of subclasses of Hash, for example
      * are of Java type RubyHash, and so should utilize RubyHash marshalling
      * logic in addition to user-defined class marshalling logic.
      * 
      * @return the ClassIndex of the native type this object was constructed from
      */
     int getNativeTypeIndex();
     
     /**
      * Gets a copy of the instance variables for this object, if any exist.
      * Returns null if this object has no instance variables.
      * "safe" in that it doesn't cause the instance var map to be created.
      * 
      * @return A snapshot of the instance vars, or null if none.
      */
     Map safeGetInstanceVariables();
     
     /**
      * Returns true if the object has any instance variables, false otherwise.
      * "safe" in that it doesn't cause the instance var map to be created.
      * 
      * @return true if the object has instance variables, false otherwise.
      */
     boolean safeHasInstanceVariables();
     
     /**
      * RubyMethod getInstanceVar.
      * @param string
      * @return RubyObject
      */
     IRubyObject getInstanceVariable(String string);
 
     /**
      * RubyMethod setInstanceVar.
      * @param string
      * @param rubyObject
      * @return RubyObject
      */
     IRubyObject setInstanceVariable(String string, IRubyObject rubyObject);
     
     Map getInstanceVariables();
     Map getInstanceVariablesSnapshot();
 
     IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name, IRubyObject[] args, CallType callType, Block block);
     IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name, IRubyObject[] args, CallType callType);
     
     IRubyObject callMethod(ThreadContext context, byte switchValue, String name, IRubyObject[] args, CallType callType, Block block);
     
     IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType);
     IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType, Block block);
     
     /**
      * RubyMethod funcall.
      * @param context TODO
      * @param string
      * @return RubyObject
      */
     IRubyObject callMethod(ThreadContext context, String string);
     IRubyObject callMethod(ThreadContext context, String string, Block aBlock);
 
     /**
      * RubyMethod funcall.
      * @param context TODO
      * @param string
      * @param arg
      * @return RubyObject
      */
     IRubyObject callMethod(ThreadContext context, String string, IRubyObject arg);
 
     /**
      * RubyMethod callMethod.
      * @param context TODO
      * @param method
      * @param rubyArgs
      * @return IRubyObject
      */
     IRubyObject callMethod(ThreadContext context, String method, IRubyObject[] rubyArgs);
     IRubyObject callMethod(ThreadContext context, String method, IRubyObject[] rubyArgs, Block block);
 
     /**
      * RubyMethod isNil.
      * @return boolean
      */
     boolean isNil();
 
     boolean isTrue();
 
     /**
      * RubyMethod isTaint.
      * @return boolean
      */
     boolean isTaint();
 
     /**
      * RubyMethod isFrozen.
      * @return boolean
      */
     boolean isFrozen();
 
     boolean isImmediate();
 
     /**
      * RubyMethod getRubyClass.
      */
     RubyClass getMetaClass();
 
     void setMetaClass(RubyClass metaClass);
 
     /**
      * RubyMethod getSingletonClass.
      * @return RubyClass
      */
     RubyClass getSingletonClass();
 
     /**
      * RubyMethod getType.
      * @return RubyClass
      */
     RubyClass getType();
 
     /**
      * RubyMethod isKindOf.
      * @param rubyClass
      * @return boolean
      */
     boolean isKindOf(RubyModule rubyClass);
 
     /**
      * RubyMethod respondsTo.
      * @param string
      * @return boolean
      */
     boolean respondsTo(String string);
 
     /**
      * RubyMethod getRuntime.
      */
     Ruby getRuntime();
 
     /**
      * RubyMethod getJavaClass.
      * @return Class
      */
     Class getJavaClass();
 
     /**
      * RubyMethod eval.
      * @param iNode
      * @return IRubyObject
      */
     IRubyObject eval(Node iNode);
 
     /**
      * Evaluate the given string under the specified binding object. If the binding is not a Proc or Binding object
      * (RubyProc or RubyBinding) throw an appropriate type error.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @param file The filename to use when reporting errors during the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalWithBinding(ThreadContext context, IRubyObject evalString, IRubyObject binding, String file);
 
     /**
      * Evaluate the given string.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param file The filename to use when reporting errors during the evaluation
      * @param binding The binding object under which to perform the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalSimple(ThreadContext context, IRubyObject evalString, String file);
 
     /**
      * RubyMethod extendObject.
      * @param rubyModule
      */
     void extendObject(RubyModule rubyModule);
 
     /**
      * Convert the object into a symbol name if possible.
      * 
      * @return String the symbol name
      */
     String asSymbol();
 
     /**
      * Methods which perform to_xxx if the object has such a method
      */
     RubyArray convertToArray();
     RubyFloat convertToFloat();
     RubyInteger convertToInteger();
     RubyString convertToString();
 
     /** rb_obj_as_string
      */
     RubyString objAsString();
 
     /**
      * Converts this object to type 'targetType' using 'convertMethod' method (MRI: convert_type).
      * 
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @param raiseOnError will throw an Error if conversion does not work
      * @return the converted value
      */
     IRubyObject convertToType(String targetType, String convertMethod, boolean raiseOnError);
 
     /**
      * Higher level conversion utility similiar to convertToType but it can throw an
      * additional TypeError during conversion (MRI: rb_check_convert_type).
      * 
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @return the converted value
      */
     IRubyObject convertToTypeWithCheck(String targetType, String convertMethod);
 
 
     /**
      * RubyMethod setTaint.
      * @param b
      */
     void setTaint(boolean b);
 
     /**
      * RubyMethod checkSafeString.
      */
     void checkSafeString();
 
     /**
      * RubyMethod convertType.
      * @param type
      * @param string
      * @param string1
      */
     IRubyObject convertType(Class type, String string, String string1);
 
     /**
      * RubyMethod dup.
      */
     IRubyObject dup();
 
     /**
      * RubyMethod setupClone.
      * @param original
      */
     void initCopy(IRubyObject original);
 
     /**
      * RubyMethod setFrozen.
      * @param b
      */
     void setFrozen(boolean b);
 
     /**
      * RubyMethod inspect.
      * @return String
      */
     IRubyObject inspect();
 
     /**
      * Make sure the arguments fit the range specified by minimum and maximum.  On
      * a failure, The Ruby runtime will generate an ArgumentError.
      * 
      * @param arguments to check
      * @param minimum number of args
      * @param maximum number of args (-1 for any number of args)
      * @return the number of arguments in args
      */
     int checkArgumentCount(IRubyObject[] arguments, int minimum, int maximum);
 
     /**
      * RubyMethod rbClone.
      * @return IRubyObject
      */
     IRubyObject rbClone();
 
 
     public void callInit(IRubyObject[] args, Block block);
 
     /**
      * RubyMethod defineSingletonMethod.
      * @param name
      * @param callback
      */
     void defineSingletonMethod(String name, Callback callback);
 
     boolean singletonMethodsAllowed();
     
     boolean isSingleton();
 
 	Iterator instanceVariableNames();
 
     /**
      * rb_scan_args
      *
      * This method will take the arguments specified, fill in an array and return it filled
      * with nils for every argument not provided. It's guaranteed to always return a new array.
      * 
      * @param args the arguments to check
      * @param required the amount of required arguments
      * @param optional the amount of optional arguments
      * @return a new array containing all arguments provided, and nils in those spots not provided.
      * 
      */
     IRubyObject[] scanArgs(IRubyObject[] args, int required, int optional);
 
     /**
      * Our version of Data_Wrap_Struct.
      *
      * This method will just set a private pointer to the object provided. This pointer is transient
      * and will not be accessible from Ruby.
      *
      * @param obj the object to wrap
      */
     void dataWrapStruct(Object obj);
 
     /**
      * Our version of Data_Get_Struct.
      *
      * Returns a wrapped data value if there is one, otherwise returns null.
      *
      * @return the object wrapped.
      */
     Object dataGetStruct();
 
     RubyFixnum id();
 
     IRubyObject anyToString();
 
     IRubyObject checkStringType();
 
     IRubyObject checkArrayType();    
+
+    void attachToObjectSpace();
 }
diff --git a/src/org/jvyamlb/ResolverScanner.java b/src/org/jvyamlb/ResolverScanner.java
index 21dd050040..ef48a84b0b 100644
--- a/src/org/jvyamlb/ResolverScanner.java
+++ b/src/org/jvyamlb/ResolverScanner.java
@@ -1,359 +1,359 @@
 
 package org.jvyamlb;
 
 import org.jruby.util.ByteList;
 
 public class ResolverScanner {
 
 
 
 static final byte[] _resolver_scanner_actions = {
 	0, 1, 0, 1, 1, 1, 2, 1, 
 	3, 1, 4, 1, 5, 1, 6, 1, 
 	7
 };
 
 static final short[] _resolver_scanner_key_offsets = {
-	0, 20, 20, 24, 29, 31, 33, 35, 
-	36, 37, 38, 43, 47, 51, 53, 56, 
-	63, 67, 74, 76, 77, 78, 79, 81, 
-	84, 86, 92, 96, 99, 100, 102, 104, 
-	105, 107, 109, 114, 116, 118, 120, 124, 
-	126, 127, 129, 135, 141, 146, 151, 152, 
-	154, 155, 156, 157, 158, 159, 160, 163, 
-	164, 165, 169, 170, 171, 173, 174, 175, 
-	177, 178, 179, 180, 182, 184, 185, 186, 
-	186, 191, 193, 193, 202, 209, 212, 215, 
-	222, 227, 231, 233, 242, 249, 256, 264, 
-	270, 273, 274, 274, 281, 285, 290, 295, 
-	300, 306, 306, 306
+	0, 20, 20, 24, 28, 30, 32, 34, 
+	35, 36, 37, 41, 45, 49, 51, 54, 
+	61, 65, 69, 75, 77, 78, 79, 80, 
+	82, 85, 87, 93, 97, 100, 101, 103, 
+	105, 106, 108, 110, 115, 117, 119, 121, 
+	125, 127, 128, 130, 136, 141, 145, 149, 
+	150, 152, 153, 154, 155, 156, 157, 158, 
+	161, 162, 163, 167, 168, 169, 171, 172, 
+	173, 175, 176, 177, 178, 180, 182, 183, 
+	184, 184, 188, 190, 190, 199, 206, 209, 
+	212, 219, 224, 228, 230, 234, 237, 238, 
+	247, 254, 261, 269, 275, 278, 279, 279, 
+	286, 290, 295, 300, 305, 311, 311, 311
 };
 
 static final char[] _resolver_scanner_trans_keys = {
 	32, 43, 45, 46, 48, 60, 61, 70, 
 	78, 79, 84, 89, 102, 110, 111, 116, 
 	121, 126, 49, 57, 46, 48, 49, 57, 
-	73, 95, 105, 48, 57, 43, 45, 48, 
-	57, 78, 110, 70, 102, 110, 46, 58, 
-	95, 48, 57, 48, 53, 54, 57, 46, 
-	58, 48, 57, 46, 58, 95, 48, 49, 
-	95, 48, 57, 65, 70, 97, 102, 48, 
-	53, 54, 57, 73, 78, 95, 105, 110, 
-	48, 57, 65, 97, 78, 97, 110, 48, 
-	57, 45, 48, 57, 48, 57, 9, 32, 
-	84, 116, 48, 57, 9, 32, 48, 57, 
-	58, 48, 57, 58, 48, 57, 48, 57, 
-	58, 48, 57, 48, 57, 9, 32, 43, 
-	45, 90, 48, 57, 48, 57, 48, 57, 
-	9, 32, 84, 116, 48, 57, 45, 48, 
+	73, 105, 48, 57, 43, 45, 48, 57, 
+	78, 110, 70, 102, 110, 46, 58, 48, 
+	57, 48, 53, 54, 57, 46, 58, 48, 
+	57, 46, 58, 95, 48, 49, 95, 48, 
+	57, 65, 70, 97, 102, 48, 53, 54, 
+	57, 48, 53, 54, 57, 73, 78, 105, 
+	110, 48, 57, 65, 97, 78, 97, 110, 
+	48, 57, 45, 48, 57, 48, 57, 9, 
+	32, 84, 116, 48, 57, 9, 32, 48, 
+	57, 58, 48, 57, 58, 48, 57, 48, 
+	57, 58, 48, 57, 48, 57, 9, 32, 
+	43, 45, 90, 48, 57, 48, 57, 48, 
 	57, 9, 32, 84, 116, 48, 57, 45, 
-	46, 58, 95, 48, 57, 46, 58, 95, 
-	48, 57, 46, 58, 95, 48, 57, 60, 
-	65, 97, 76, 83, 69, 108, 115, 101, 
-	79, 111, 117, 108, 108, 70, 78, 102, 
-	110, 70, 102, 82, 114, 85, 117, 69, 
-	101, 83, 115, 97, 111, 117, 102, 110, 
-	114, 101, 69, 95, 101, 48, 57, 48, 
-	57, 46, 58, 95, 98, 120, 48, 55, 
-	56, 57, 46, 58, 95, 48, 55, 56, 
-	57, 95, 48, 57, 95, 48, 49, 95, 
-	48, 57, 65, 70, 97, 102, 46, 58, 
-	95, 48, 57, 46, 58, 48, 57, 46, 
-	58, 46, 58, 95, 98, 120, 48, 55, 
-	56, 57, 46, 58, 95, 48, 55, 56, 
-	57, 46, 58, 95, 48, 55, 56, 57, 
-	45, 46, 58, 95, 48, 55, 56, 57, 
-	9, 32, 43, 45, 46, 90, 58, 48, 
-	57, 58, 9, 32, 43, 45, 90, 48, 
-	57, 9, 32, 84, 116, 46, 58, 95, 
-	48, 57, 46, 58, 95, 48, 57, 46, 
-	58, 95, 48, 57, 45, 46, 58, 95, 
-	48, 57, 0
+	48, 57, 9, 32, 84, 116, 48, 57, 
+	45, 46, 58, 48, 57, 46, 58, 48, 
+	57, 46, 58, 48, 57, 60, 65, 97, 
+	76, 83, 69, 108, 115, 101, 79, 111, 
+	117, 108, 108, 70, 78, 102, 110, 70, 
+	102, 82, 114, 85, 117, 69, 101, 83, 
+	115, 97, 111, 117, 102, 110, 114, 101, 
+	69, 101, 48, 57, 48, 57, 46, 58, 
+	95, 98, 120, 48, 55, 56, 57, 46, 
+	58, 95, 48, 55, 56, 57, 95, 48, 
+	55, 95, 48, 49, 95, 48, 57, 65, 
+	70, 97, 102, 46, 58, 95, 48, 57, 
+	46, 58, 48, 57, 46, 58, 58, 95, 
+	48, 57, 58, 48, 57, 58, 46, 58, 
+	95, 98, 120, 48, 55, 56, 57, 46, 
+	58, 95, 48, 55, 56, 57, 46, 58, 
+	95, 48, 55, 56, 57, 45, 46, 58, 
+	95, 48, 55, 56, 57, 9, 32, 43, 
+	45, 46, 90, 58, 48, 57, 58, 9, 
+	32, 43, 45, 90, 48, 57, 9, 32, 
+	84, 116, 46, 58, 95, 48, 57, 46, 
+	58, 95, 48, 57, 46, 58, 95, 48, 
+	57, 45, 46, 58, 95, 48, 57, 0
 };
 
 static final byte[] _resolver_scanner_single_lengths = {
-	18, 0, 2, 3, 2, 0, 2, 1, 
-	1, 1, 3, 0, 2, 2, 1, 1, 
-	0, 5, 2, 1, 1, 1, 0, 1, 
-	0, 4, 2, 1, 1, 0, 0, 1, 
-	0, 0, 5, 0, 0, 0, 4, 0, 
-	1, 0, 4, 4, 3, 3, 1, 2, 
-	1, 1, 1, 1, 1, 1, 3, 1, 
-	1, 4, 1, 1, 2, 1, 1, 2, 
-	1, 1, 1, 2, 2, 1, 1, 0, 
-	3, 0, 0, 5, 3, 1, 1, 1, 
-	3, 2, 2, 5, 3, 3, 4, 6, 
-	1, 1, 0, 5, 4, 3, 3, 3, 
-	4, 0, 0, 0
+	18, 0, 2, 2, 2, 0, 2, 1, 
+	1, 1, 2, 0, 2, 2, 1, 1, 
+	0, 0, 4, 2, 1, 1, 1, 0, 
+	1, 0, 4, 2, 1, 1, 0, 0, 
+	1, 0, 0, 5, 0, 0, 0, 4, 
+	0, 1, 0, 4, 3, 2, 2, 1, 
+	2, 1, 1, 1, 1, 1, 1, 3, 
+	1, 1, 4, 1, 1, 2, 1, 1, 
+	2, 1, 1, 1, 2, 2, 1, 1, 
+	0, 2, 0, 0, 5, 3, 1, 1, 
+	1, 3, 2, 2, 2, 1, 1, 5, 
+	3, 3, 4, 6, 1, 1, 0, 5, 
+	4, 3, 3, 3, 4, 0, 0, 0
 };
 
 static final byte[] _resolver_scanner_range_lengths = {
 	1, 0, 1, 1, 0, 1, 0, 0, 
 	0, 0, 1, 2, 1, 0, 1, 3, 
-	2, 1, 0, 0, 0, 0, 1, 1, 
-	1, 1, 1, 1, 0, 1, 1, 0, 
-	1, 1, 0, 1, 1, 1, 0, 1, 
-	0, 1, 1, 1, 1, 1, 0, 0, 
+	2, 2, 1, 0, 0, 0, 0, 1, 
+	1, 1, 1, 1, 1, 0, 1, 1, 
+	0, 1, 1, 0, 1, 1, 1, 0, 
+	1, 0, 1, 1, 1, 1, 1, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
-	1, 1, 0, 2, 2, 1, 1, 3, 
-	1, 1, 0, 2, 2, 2, 2, 0, 
-	1, 0, 0, 1, 0, 1, 1, 1, 
-	1, 0, 0, 0
+	0, 1, 1, 0, 2, 2, 1, 1, 
+	3, 1, 1, 0, 1, 1, 0, 2, 
+	2, 2, 2, 0, 1, 0, 0, 1, 
+	0, 1, 1, 1, 1, 0, 0, 0
 };
 
 static final short[] _resolver_scanner_index_offsets = {
-	0, 20, 20, 24, 29, 32, 34, 37, 
-	39, 41, 43, 48, 51, 55, 58, 61, 
-	66, 69, 76, 79, 81, 83, 85, 87, 
-	90, 92, 98, 102, 105, 107, 109, 111, 
-	113, 115, 117, 123, 125, 127, 129, 134, 
-	136, 138, 140, 146, 152, 157, 162, 164, 
-	167, 169, 171, 173, 175, 177, 179, 183, 
-	185, 187, 192, 194, 196, 199, 201, 203, 
-	206, 208, 210, 212, 215, 218, 220, 222, 
-	223, 228, 230, 231, 239, 245, 248, 251, 
-	256, 261, 265, 268, 276, 282, 288, 295, 
-	302, 305, 307, 308, 315, 320, 325, 330, 
-	335, 341, 342, 343
+	0, 20, 20, 24, 28, 31, 33, 36, 
+	38, 40, 42, 46, 49, 53, 56, 59, 
+	64, 67, 70, 76, 79, 81, 83, 85, 
+	87, 90, 92, 98, 102, 105, 107, 109, 
+	111, 113, 115, 117, 123, 125, 127, 129, 
+	134, 136, 138, 140, 146, 151, 155, 159, 
+	161, 164, 166, 168, 170, 172, 174, 176, 
+	180, 182, 184, 189, 191, 193, 196, 198, 
+	200, 203, 205, 207, 209, 212, 215, 217, 
+	219, 220, 224, 226, 227, 235, 241, 244, 
+	247, 252, 257, 261, 264, 268, 271, 273, 
+	281, 287, 293, 300, 307, 310, 312, 313, 
+	320, 325, 330, 335, 340, 346, 347, 348
 };
 
 static final byte[] _resolver_scanner_indicies = {
-	29, 56, 56, 57, 58, 60, 61, 62, 
-	63, 64, 65, 66, 67, 68, 69, 70, 
-	71, 29, 59, 0, 49, 50, 17, 0, 
-	43, 11, 44, 11, 0, 55, 55, 0, 
-	9, 0, 34, 35, 0, 28, 0, 28, 
-	0, 35, 0, 11, 20, 19, 19, 0, 
-	86, 51, 0, 10, 20, 51, 0, 10, 
-	20, 0, 13, 13, 0, 14, 14, 14, 
-	14, 0, 83, 16, 0, 43, 45, 11, 
-	44, 46, 11, 0, 40, 40, 0, 28, 
-	0, 31, 0, 28, 0, 87, 0, 53, 
-	54, 0, 97, 0, 1, 1, 2, 2, 
-	89, 0, 1, 1, 72, 0, 48, 47, 
-	0, 48, 0, 94, 0, 84, 0, 42, 
-	0, 93, 0, 82, 0, 5, 5, 6, 
-	6, 8, 0, 81, 0, 85, 0, 8, 
-	0, 1, 1, 2, 2, 0, 72, 0, 
-	52, 0, 88, 0, 1, 1, 2, 2, 
-	73, 0, 23, 11, 20, 19, 19, 0, 
-	11, 20, 19, 76, 0, 11, 20, 19, 
-	92, 0, 41, 0, 80, 79, 0, 36, 
-	0, 32, 0, 25, 0, 78, 0, 24, 
-	0, 25, 0, 25, 25, 27, 0, 77, 
-	0, 29, 0, 37, 25, 30, 25, 0, 
-	25, 0, 25, 0, 33, 26, 0, 32, 
-	0, 24, 0, 38, 39, 0, 25, 0, 
-	25, 0, 79, 0, 25, 27, 0, 30, 
-	25, 0, 26, 0, 39, 0, 0, 12, 
-	11, 12, 11, 0, 9, 0, 0, 11, 
-	20, 18, 21, 22, 18, 19, 0, 11, 
-	20, 18, 18, 19, 0, 10, 10, 0, 
-	13, 13, 0, 14, 14, 14, 14, 0, 
-	11, 15, 17, 17, 0, 10, 15, 16, 
-	0, 10, 15, 0, 11, 20, 18, 21, 
-	22, 95, 96, 0, 11, 20, 18, 91, 
-	92, 0, 11, 20, 18, 75, 76, 0, 
-	23, 11, 20, 18, 18, 19, 0, 5, 
-	5, 6, 6, 7, 8, 0, 3, 4, 
-	0, 3, 0, 0, 5, 5, 6, 6, 
-	8, 7, 0, 1, 1, 2, 2, 0, 
-	11, 15, 17, 98, 0, 11, 15, 17, 
-	90, 0, 11, 15, 17, 74, 0, 23, 
-	11, 15, 17, 17, 0, 0, 0, 0, 
-	0
+	32, 59, 59, 60, 61, 63, 64, 65, 
+	66, 67, 68, 69, 70, 71, 72, 73, 
+	74, 32, 62, 0, 52, 53, 20, 0, 
+	46, 47, 10, 0, 58, 58, 0, 9, 
+	0, 37, 38, 0, 31, 0, 31, 0, 
+	38, 0, 10, 23, 22, 0, 90, 54, 
+	0, 9, 23, 54, 0, 9, 23, 0, 
+	13, 13, 0, 15, 15, 15, 15, 0, 
+	87, 19, 0, 86, 16, 0, 46, 48, 
+	47, 49, 10, 0, 43, 43, 0, 31, 
+	0, 34, 0, 31, 0, 91, 0, 56, 
+	57, 0, 101, 0, 1, 1, 2, 2, 
+	93, 0, 1, 1, 75, 0, 51, 50, 
+	0, 51, 0, 98, 0, 88, 0, 45, 
+	0, 97, 0, 85, 0, 5, 5, 6, 
+	6, 8, 0, 84, 0, 89, 0, 8, 
+	0, 1, 1, 2, 2, 0, 75, 0, 
+	55, 0, 92, 0, 1, 1, 2, 2, 
+	76, 0, 26, 10, 23, 22, 0, 10, 
+	23, 79, 0, 10, 23, 96, 0, 44, 
+	0, 83, 82, 0, 39, 0, 35, 0, 
+	28, 0, 81, 0, 27, 0, 28, 0, 
+	28, 28, 30, 0, 80, 0, 32, 0, 
+	40, 28, 33, 28, 0, 28, 0, 28, 
+	0, 36, 29, 0, 35, 0, 27, 0, 
+	41, 42, 0, 28, 0, 28, 0, 82, 
+	0, 28, 30, 0, 33, 28, 0, 29, 
+	0, 42, 0, 0, 11, 11, 10, 0, 
+	9, 0, 0, 10, 23, 14, 24, 25, 
+	21, 22, 0, 10, 23, 14, 21, 22, 
+	0, 14, 14, 0, 13, 13, 0, 15, 
+	15, 15, 15, 0, 10, 18, 17, 20, 
+	0, 9, 18, 19, 0, 9, 18, 0, 
+	12, 17, 17, 0, 12, 16, 0, 12, 
+	0, 10, 23, 14, 24, 25, 99, 100, 
+	0, 10, 23, 14, 95, 96, 0, 10, 
+	23, 14, 78, 79, 0, 26, 10, 23, 
+	14, 21, 22, 0, 5, 5, 6, 6, 
+	7, 8, 0, 3, 4, 0, 3, 0, 
+	0, 5, 5, 6, 6, 8, 7, 0, 
+	1, 1, 2, 2, 0, 10, 18, 17, 
+	102, 0, 10, 18, 17, 94, 0, 10, 
+	18, 17, 77, 0, 26, 10, 18, 17, 
+	20, 0, 0, 0, 0, 0
 };
 
 static final byte[] _resolver_scanner_trans_targs_wi = {
-	1, 26, 39, 36, 89, 34, 35, 91, 
-	90, 73, 77, 72, 4, 78, 79, 16, 
-	82, 80, 76, 10, 11, 14, 15, 22, 
-	53, 99, 62, 55, 74, 71, 59, 21, 
-	50, 61, 7, 8, 49, 58, 64, 65, 
-	19, 97, 32, 6, 9, 18, 20, 28, 
-	29, 3, 75, 13, 41, 24, 40, 5, 
-	2, 17, 83, 93, 46, 98, 47, 54, 
-	57, 60, 63, 66, 67, 68, 69, 70, 
-	27, 92, 96, 86, 43, 56, 52, 51, 
-	48, 88, 87, 81, 31, 37, 12, 23, 
-	42, 38, 95, 85, 44, 33, 30, 84, 
-	45, 25, 94
+	1, 27, 40, 37, 93, 35, 36, 95, 
+	94, 74, 73, 4, 17, 79, 78, 80, 
+	86, 84, 16, 83, 81, 77, 10, 11, 
+	14, 15, 23, 54, 103, 63, 56, 75, 
+	72, 60, 22, 51, 62, 7, 8, 50, 
+	59, 65, 66, 20, 101, 33, 6, 9, 
+	19, 21, 29, 30, 3, 76, 13, 42, 
+	25, 41, 5, 2, 18, 87, 97, 47, 
+	102, 48, 55, 58, 61, 64, 67, 68, 
+	69, 70, 71, 28, 96, 100, 90, 44, 
+	57, 53, 52, 49, 92, 91, 85, 82, 
+	32, 38, 12, 24, 43, 39, 99, 89, 
+	45, 34, 31, 88, 46, 26, 98
 };
 
 static final byte[] _resolver_scanner_trans_actions_wi = {
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
-	0, 0, 0
+	0, 0, 0, 0, 0, 0, 0
 };
 
 static final byte[] _resolver_scanner_eof_actions = {
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
 	0, 0, 0, 0, 0, 0, 0, 0, 
-	0, 0, 0, 0, 0, 0, 0, 5, 
-	13, 13, 13, 15, 15, 13, 15, 15, 
-	15, 15, 15, 15, 15, 15, 15, 9, 
-	9, 9, 9, 9, 7, 15, 15, 15, 
-	15, 3, 11, 1
+	0, 0, 0, 0, 0, 0, 0, 0, 
+	5, 13, 13, 13, 15, 15, 15, 15, 
+	15, 15, 15, 15, 15, 15, 15, 15, 
+	15, 15, 15, 9, 9, 9, 9, 9, 
+	7, 15, 15, 15, 15, 3, 11, 1
 };
 
 static final int resolver_scanner_start = 0;
 
 static final int resolver_scanner_error = 1;
 
 
    public String recognize(ByteList list) {
        String tag = null;
        int cs;
        int act;
        int have = 0;
        int nread = 0;
        int p=0;
        int pe = list.realSize;
        int tokstart = -1;
        int tokend = -1;
 
        byte[] data = list.bytes;
        if(pe == 0) {
          data = new byte[]{(byte)'~'};
          pe = 1;
        }
               
 
 	{
 	cs = resolver_scanner_start;
 	}
 
 
 	{
 	int _klen;
 	int _trans;
 	int _keys;
 
 	if ( p != pe ) {
 	_resume: while ( true ) {
 	_again: do {
 	if ( cs == 1 )
 		break _resume;
 	_match: do {
 	_keys = _resolver_scanner_key_offsets[cs];
 	_trans = _resolver_scanner_index_offsets[cs];
 	_klen = _resolver_scanner_single_lengths[cs];
 	if ( _klen > 0 ) {
 		int _lower = _keys;
 		int _mid;
 		int _upper = _keys + _klen - 1;
 		while (true) {
 			if ( _upper < _lower )
 				break;
 
 			_mid = _lower + ((_upper-_lower) >> 1);
 			if ( data[p] < _resolver_scanner_trans_keys[_mid] )
 				_upper = _mid - 1;
 			else if ( data[p] > _resolver_scanner_trans_keys[_mid] )
 				_lower = _mid + 1;
 			else {
 				_trans += (_mid - _keys);
 				break _match;
 			}
 		}
 		_keys += _klen;
 		_trans += _klen;
 	}
 
 	_klen = _resolver_scanner_range_lengths[cs];
 	if ( _klen > 0 ) {
 		int _lower = _keys;
 		int _mid;
 		int _upper = _keys + (_klen<<1) - 2;
 		while (true) {
 			if ( _upper < _lower )
 				break;
 
 			_mid = _lower + (((_upper-_lower) >> 1) & ~1);
 			if ( data[p] < _resolver_scanner_trans_keys[_mid] )
 				_upper = _mid - 2;
 			else if ( data[p] > _resolver_scanner_trans_keys[_mid+1] )
 				_lower = _mid + 2;
 			else {
 				_trans += ((_mid - _keys)>>1);
 				break _match;
 			}
 		}
 		_trans += _klen;
 	}
 	} while (false);
 
 	_trans = _resolver_scanner_indicies[_trans];
 	cs = _resolver_scanner_trans_targs_wi[_trans];
 
 	} while (false);
 	if ( ++p == pe )
 		break _resume;
 	}
 	}
 	}
 
 
 	int _acts = _resolver_scanner_eof_actions[cs];
 	int _nacts = (int) _resolver_scanner_actions[_acts++];
 	while ( _nacts-- > 0 ) {
 		switch ( _resolver_scanner_actions[_acts++] ) {
 	case 0:
 	{ tag = "tag:yaml.org,2002:bool"; }
 	break;
 	case 1:
 	{ tag = "tag:yaml.org,2002:merge"; }
 	break;
 	case 2:
 	{ tag = "tag:yaml.org,2002:null"; }
 	break;
 	case 3:
 	{ tag = "tag:yaml.org,2002:timestamp#ymd"; }
 	break;
 	case 4:
 	{ tag = "tag:yaml.org,2002:timestamp"; }
 	break;
 	case 5:
 	{ tag = "tag:yaml.org,2002:value"; }
 	break;
 	case 6:
 	{ tag = "tag:yaml.org,2002:float"; }
 	break;
 	case 7:
 	{ tag = "tag:yaml.org,2002:int"; }
 	break;
 		}
 	}
 
        return tag;
    }
 
    public static void main(String[] args) {
        ByteList b = new ByteList(78);
        b.append(args[0].getBytes());
 /*
        for(int i=0;i<b.realSize;i++) {
            System.err.println("byte " + i + " is " + b.bytes[i] + " char is: " + args[0].charAt(i));
        }
 */
        System.err.println(new ResolverScanner().recognize(b));
    }
 }
diff --git a/src/org/jvyamlb/resolver_scanner.rl b/src/org/jvyamlb/resolver_scanner.rl
index 813f30ccae..fed694bfdf 100644
--- a/src/org/jvyamlb/resolver_scanner.rl
+++ b/src/org/jvyamlb/resolver_scanner.rl
@@ -1,89 +1,90 @@
 
 package org.jvyamlb;
 
 import org.jruby.util.ByteList;
 
 public class ResolverScanner {
 %%{
         machine resolver_scanner;
 
         action bool_tag { tag = "tag:yaml.org,2002:bool"; }
         action merge_tag { tag = "tag:yaml.org,2002:merge"; }
         action null_tag { tag = "tag:yaml.org,2002:null"; }
         action timestamp_ymd_tag { tag = "tag:yaml.org,2002:timestamp#ymd"; }
         action timestamp_tag { tag = "tag:yaml.org,2002:timestamp"; }
         action value_tag { tag = "tag:yaml.org,2002:value"; }
         action float_tag { tag = "tag:yaml.org,2002:float"; }
         action int_tag { tag = "tag:yaml.org,2002:int"; }
 
         Bool = ("yes" | "Yes" | "YES" | "no" | "No" | "NO" | 
                 "true" | "True" | "TRUE" | "false" | "False" | "FALSE" | 
                 "on" | "On" | "ON" | "off" | "Off" | "OFF") %/bool_tag;
 
         Merge = "<<" %/merge_tag;
         Value = "=" %/value_tag;
         Null  = ("~" | "null" | "Null" | "null" | " ") %/null_tag;
 
         digit2 = digit | "_";
         sign = "-" | "+";
         timestampFract = "." digit*;
         timestampZone = [ \t]* ("Z" | (sign digit{1,2} ( ":" digit{2} )?));
         TimestampYMD = digit{4} ("-" digit{2}){2} %/timestamp_ymd_tag;
         Timestamp = digit{4} ("-" digit{1,2}){2} ([Tt] | [ \t]+) digit{1,2} ":" digit{2} ":" digit{2} timestampFract? timestampZone? %/timestamp_tag;
 
         exp = [eE] sign digit+;
-        Float = ((sign? ((digit digit2*    "." digit2* exp?)
-                     | ((digit digit2*)? "." digit2+ exp?)
-                     | (digit digit2* (":" [0-5]? digit)+ "." digit2*)
+
+        Float = ((sign? ((digit+ "." digit* exp?)
+                     | ((digit+)? "." digit+ exp?)
+                     | (digit+ (":" [0-5]? digit)+ "." digit*)
                      | "." ("inf" | "Inf" | "INF"))) 
                  | ("." ("nan" | "NaN" | "NAN"))) %/float_tag;
 
         binaryInt = "0b" [0-1_]+;
         octalInt = "0" [0-7_]+;
         decimalInt = "0" |
                      [1-9]digit2* (":" [0-5]? digit)*;
         hexaInt = "0x" [0-9a-fA-F_]+;
         Int = sign? (binaryInt | octalInt | decimalInt | hexaInt) %/int_tag;
 
         Scalar = Bool | Null | Int | Float | Merge | Value | Timestamp | TimestampYMD;
         main := Scalar;
 }%%
 
 %% write data nofinal;
 
    public String recognize(ByteList list) {
        String tag = null;
        int cs;
        int act;
        int have = 0;
        int nread = 0;
        int p=0;
        int pe = list.realSize;
        int tokstart = -1;
        int tokend = -1;
 
        byte[] data = list.bytes;
        if(pe == 0) {
          data = new byte[]{(byte)'~'};
          pe = 1;
        }
               
 %% write init;
 
 %% write exec;
 
 %% write eof;
        return tag;
    }
 
    public static void main(String[] args) {
        ByteList b = new ByteList(78);
        b.append(args[0].getBytes());
 /*
        for(int i=0;i<b.realSize;i++) {
            System.err.println("byte " + i + " is " + b.bytes[i] + " char is: " + args[0].charAt(i));
        }
 */
        System.err.println(new ResolverScanner().recognize(b));
    }
 }
